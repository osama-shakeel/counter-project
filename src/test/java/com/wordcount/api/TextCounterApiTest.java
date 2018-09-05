package com.wordcount.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordcount.api.TextCounterApi;
import com.wordcount.exception.AppApiError;
import com.wordcount.exception.AppApiExceptionHandler;
import com.wordcount.exception.ApplicationException;
import com.wordcount.service.TextCountService;
import com.wordcount.vo.WordCountRequestVO;
import com.wordcount.vo.WordCountResultVO;

/**
 * Test class for the TextCounterApi.
 *
 */
@RunWith(SpringRunner.class)
public class TextCounterApiTest {
	private MockMvc mockMvc;

	@InjectMocks
	private TextCounterApi textCounterApi;

	@Mock
	private TextCountService counterSearchService;

	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * Sets up the API along with its Exception Handler advice.
	 */
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(textCounterApi).setControllerAdvice(new AppApiExceptionHandler())
				.build();
	}

	/**
	 * Tests a text count search of 3 valid words. The test will pass with their
	 * expected counts returned by the API.
	 */
	@Test
	public void testValidSearchWordCounts() throws Exception {
		when(this.counterSearchService.findTextCount("Sed")).thenReturn(16L);
		when(this.counterSearchService.findTextCount("Donec")).thenReturn(6L);
		when(this.counterSearchService.findTextCount("Augue")).thenReturn(7L);
		
		this.mockMvc
				.perform(post("/counter-api/search/")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.content(mapper.writeValueAsString(prepareWordCountRequestTestData())))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().json(mapper.writeValueAsString(prepareWordCountResponseTestData())));
	}

	/**
	 * Tests a text count search of invalid API request - Invalid request JSON.
	 * The test will pass with their Bad Request error expected.
	 */
	@Test
	public void testSearchWordCountsInvalidData() throws Exception {
		this.mockMvc
				.perform(post("/counter-api/search/").contentType(MediaType.APPLICATION_JSON_UTF8).content("{Search"))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(content()
						.json(mapper.writeValueAsString(new AppApiError("Invalid Request Body / Method argument(s)",
								HttpStatus.BAD_REQUEST.value(), "Bad Request"))));
	}

	/**
	 * Tests a text count search of invalid API request - Empty search word in JSON input.
	 * The test will pass with their Internal server error expected.
	 */
	@Test
	public void testInternalServerError() throws Exception {
		WordCountRequestVO vo = new WordCountRequestVO();
		vo.setSearchText(Arrays.asList(""));

		ApplicationException ex = new ApplicationException("Invalid Input");
		when(this.counterSearchService.findTextCount("")).thenThrow(ex);

		this.mockMvc
				.perform(post("/counter-api/search/").contentType(MediaType.APPLICATION_JSON_UTF8)
						.content(mapper.writeValueAsString(vo)))
				.andDo(print()).andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().json(mapper.writeValueAsString(new AppApiError(ex.getMessage(),
						HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Application Error"))));
	}

	/**
	 * Tests a top 1 word count search. The test will pass with the expected top
	 * 1 word-count returned counts returned by the API in CSV format.
	 */
	@Test
	public void testSearchTopNValidWords() throws Exception {
		when(this.counterSearchService.findTopNWordCounts(1)).thenReturn(prepareMockTopNWordsData());
		this.mockMvc.perform(get("/counter-api/top/1/").accept("text/csv")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType("text/csv")).andExpect(content().string("vel|17\n"));
	}

	/**
	 * Tests a topN word count search with invalid input. The test will pass
	 * with their Bad Request error expected.
	 */
	@Test
	public void testSearchTopNWordsInvalidData() throws Exception {
		this.mockMvc.perform(get("/counter-api/top/ABC")).andDo(print()).andExpect(status().isBadRequest());
	}

	private WordCountRequestVO prepareWordCountRequestTestData() {
		WordCountRequestVO vo = new WordCountRequestVO();
		vo.setSearchText(Arrays.asList("Sed", "Donec", "Augue"));
		return vo;
	}

	private WordCountResultVO prepareWordCountResponseTestData() {
		WordCountResultVO vo = new WordCountResultVO();
		vo.addWordCount("Sed", 16L);
		vo.addWordCount("Donec", 6L);
		vo.addWordCount("Augue", 7L);
		return vo;
	}

	@SuppressWarnings("serial")
	private List<Entry<String, Long>> prepareMockTopNWordsData() {
		Map<String, Long> testDataMap = Collections.unmodifiableMap(new LinkedHashMap<String, Long>() {
			{
				put("vel", 17L);
			}
		});
		return new ArrayList<>(testDataMap.entrySet());

	}
}

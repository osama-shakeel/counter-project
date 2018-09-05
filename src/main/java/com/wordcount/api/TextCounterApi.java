package com.wordcount.api;

import static org.springframework.http.HttpStatus.OK;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.wordcount.api.utilities.CSVWriterUtils;
import com.wordcount.exception.ApplicationException;
import com.wordcount.service.TextCountService;
import com.wordcount.vo.WordCountRequestVO;
import com.wordcount.vo.WordCountResultVO;

/**
 * Rest API class for '/counter-api' that provides various text count URLs: 1.
 * /counter-api/search/ 2. /counter-api/top/{topN}
 *
 */
@RestController
@RequestMapping("/counter-api/")
public class TextCounterApi {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TextCountService textCountService;

	/**
	 * Post method: /search that is used for finding counts of the provided list
	 * of words.
	 * 
	 * @param requestVO
	 *            Represents the request body json that contains the list of
	 *            words to be searched.
	 * @return WordCountSearchResultVO that contains the list of words searched
	 *         and their counts. The result returned is in JSON format.
	 */
	@PostMapping(value = "/search/", consumes = "application/json", produces = "application/json")
	@ResponseStatus(OK)
	public @ResponseBody WordCountResultVO searchWordCounts(@RequestBody WordCountRequestVO requestVO) {
		if (logger.isDebugEnabled()) {
			logger.debug("Searching text counts");
		}
		WordCountResultVO resultVO = new WordCountResultVO();

		// Find the count of each requested word and populate the result.
		if (requestVO != null && requestVO.getSearchText() != null) {
			requestVO.getSearchText()
					.forEach(word -> resultVO.addWordCount(word, this.textCountService.findTextCount(word)));
		}
		return resultVO;
	}

	/**
	 * Get Method: /top/{topN} that is used for getting the topN list of
	 * frequently occuring words and their counts. Returns the topN list of
	 * word-counts in CSV format.
	 * 
	 * In case of any CSV formatting error, Internal server error is raised.
	 * 
	 * @param topN
	 *            The top N number of words to search.
	 * @param response
	 *            HttpServlet Response
	 * @throws IOException
	 */
	@GetMapping(value = "/top/{topN}", produces = "text/csv")
	public void searchTopNWords(@PathVariable String topN, HttpServletResponse response) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("In API method for Searching top {} text counts", topN);
		}
		response.setContentType("text/csv");
		try {
			Integer topNum = Integer.parseInt(topN);
			List<Entry<String, Long>> list = this.textCountService.findTopNWordCounts(topNum);
			CSVWriterUtils.writeList(list, Entry.class, new String[] { "key", "value" }, response.getWriter());
			response.setStatus(HttpStatus.OK.value());

			logger.info("In API Method: Top {} text counts found", topN);
		} catch (NumberFormatException ex) {
			logger.error("Requested TopN number: {} is invalid", topN);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			CSVWriterUtils.writeOject(new ApplicationException("Requested TopN number is invalid"),
					new String[] { "message" }, response.getWriter());
		} catch (Exception ex) {
			logger.error("Error occured in finding TopN words for topN", ex);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			CSVWriterUtils.writeOject(ex, new String[] { "message" }, response.getWriter());
		}
	}

}

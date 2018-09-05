package com.wordcount.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordcount.WordCountProjectApplication;
import com.wordcount.vo.WordCountRequestVO;

/**
 * Integration Test class for testing the API Security Configuration
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
  webEnvironment = WebEnvironment.RANDOM_PORT,
  classes = WordCountProjectApplication.class)
@AutoConfigureMockMvc
public class SecurityConfigurationTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private MockMvc mvc;
	
	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * Tests API Security with VALID credentials. 
	 * The test will pass with the API called and OK status returned.
	 */
	@Test
	public void testAPISecurityWithValidCredentials() throws Exception {
		this.mvc
		.perform(post("/counter-api/search/")
				.with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "password"))
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(mapper.writeValueAsString(prepareWordCountRequestTestData())))
		.andDo(print()).andExpect(status().isOk());
	}

	/**
	 * Tests API Security with INVALID credentials. 
	 * The test will pass with the 401 - Unauthorized status returned.
	 */
	@Test
	public void testAPISecurityWithInValidCredentials() throws Exception {
		this.mvc
		.perform(post("/counter-api/search/")
				.with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "password123"))
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(mapper.writeValueAsString(prepareWordCountRequestTestData())))
		.andDo(print())
		.andExpect(status().isUnauthorized());
	}

	private WordCountRequestVO prepareWordCountRequestTestData() {
		WordCountRequestVO vo = new WordCountRequestVO();
		vo.setSearchText(Arrays.asList("Sed", "Donec", "Augue"));
		return vo;
	}
}

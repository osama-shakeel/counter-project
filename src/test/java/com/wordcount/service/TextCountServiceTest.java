package com.wordcount.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.util.ArrayList;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import com.wordcount.exception.ApplicationException;
import com.wordcount.service.TextCountServiceWordMapImpl;

/**
 * Test class for the TextCountServiceWordMapImpl service.
 *
 */
@RunWith(SpringRunner.class)
public class TextCountServiceTest {

	@InjectMocks
	private TextCountServiceWordMapImpl service;

	@Mock
	private Resource srcFile;

	/**
	 * Initializes the service required to start testing.
	 */
	@Before
	public void setup() throws Exception {
		// Mock srcFile
		when(srcFile.getURI()).thenReturn(new ClassPathResource("/test_paragraph.txt").getURI());

		// Test the method
		service.init();
	}

	/**
	 * Tests service initialiation with valid conditions. The service is
	 * expected to be correctly initialized: with an initialized text count map.
	 */
	@Test
	public void testValidServiceInitialization() throws Exception {
		// Test assertions
		Map<String, Long> textMap = service.getTextCountMap();
		assertNotNull(textMap);
		assertTrue(!textMap.isEmpty());
	}

	/**
	 * Tests service initialization with invalid conditions such as Source Text
	 * file not found. The service is not expected to be initialized: text count
	 * map uninitialized.
	 */
	@Test(expected = ApplicationException.class)
	public void testInValidServiceInitialization() throws Exception {
		// Mock srcFile
		when(srcFile.getURI()).thenThrow(new FileNotFoundException("File Not Found"));

		try {
			// Test the method
			service.init();
		} catch (ApplicationException ex) {
			assertTrue(ex.getCause().getClass() == FileNotFoundException.class);
			throw ex;
		} catch (Exception ex) {
			fail("Not expecting " + ex);
		}
		fail("Application Exception expected");
	}

	/**
	 * Tests searching count of a valid word that exists in the source text
	 * file. The service is expected to return the correct word count.
	 */
	@Test
	public void testSearchValidWordCount() {
		Long count = service.findTextCount("Sed");
		assertNotNull(count);
		assertTrue(count.equals(16L));
	}

	/**
	 * Tests searching count of a null word. The service is expected to throw
	 * ApplicationException.
	 */
	@Test(expected = ApplicationException.class)
	public void testSearchNullWordCount() {
		service.findTextCount(null);
		fail("Application Exception expected");
	}

	/**
	 * Tests searching count of an empty/trailing spaces word. The service is
	 * expected to throw ApplicationException.
	 */
	@Test(expected = ApplicationException.class)
	public void testSearchEmptyWordCount() {
		service.findTextCount("   ");
		fail("Application Exception expected");
	}

	/**
	 * Tests searching of top 5 word counts from the source text file. The
	 * service is expected to return the list of 5 word-count entries.
	 */
	@Test
	@SuppressWarnings("serial")
	public void testFindValidTopNCounts() {
		Map<String, Long> testDataMap = Collections.unmodifiableMap(new LinkedHashMap<String, Long>() {
			{
				put("et", 14L);
				put("vel", 17L);
				put("eget", 17L);
				put("sed", 16L);
				put("in", 15L);
			}
		});
		List<Entry<String, Long>> expectedCountsList = new ArrayList<>(testDataMap.entrySet());
		List<Entry<String, Long>> actualCountsList = service.findTopNWordCounts(5);
		assertNotNull(actualCountsList != null);
		assertTrue(actualCountsList.size() == 5);
		for (int i = 0; i < expectedCountsList.size(); i++) {
			actualCountsList.get(i).equals(expectedCountsList.get(i));
		}
	}

	/**
	 * Tests searching of top null word counts from the source text file. The
	 * service is expected to throw ApplicationException.
	 */
	@Test(expected = ApplicationException.class)
	public void testFindNullTopNCount() {
		service.findTopNWordCounts(null);
		fail("Application Exception expected");
	}

	/**
	 * Tests searching of top -1 word counts from the source text file. The
	 * service is expected to throw ApplicationException.
	 */
	@Test(expected = ApplicationException.class)
	public void testFindNegTopNCount() {
		service.findTopNWordCounts(-1);
		fail("Application Exception expected");
	}
}

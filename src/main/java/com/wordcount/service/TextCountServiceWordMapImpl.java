package com.wordcount.service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.cache.annotation.CacheResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.wordcount.exception.ApplicationException;

/**
 * Implementation class for the Text Count Service based on internal word map it
 * maintains of the text file source.
 */
@Service
public class TextCountServiceWordMapImpl implements TextCountService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Map of text count sorted by count, initialized after reading the srcFile.
	 */
	private Map<String, Long> textCountMap = new LinkedHashMap<>();

	/**
	 * Source Text file resource available on the classpath.
	 */
	@Value("classpath:/paragraph.txt")
	private Resource srcFile;

	/**
	 * Initializes the text count map after reading from the srcFile:
	 * paragraph.txt on classpath.
	 */
	@PostConstruct
	public void init() {
		if (logger.isDebugEnabled()) {
			logger.info("Loading Paragraph text");
		}
		// Read the src text file
		String fileText = this.readFile();
		
		// Initialize Text Count Map.
		initializedTextCountMap(fileText);
		logger.info("{} bean Initialized", this.getClass().getName());
	}

	@Override
	@CacheResult(cacheName = "textCountCache")
	public Long findTextCount(String text) {
		if (logger.isDebugEnabled()) {
			logger.debug("Search text count for: {}", text);
		}

		// If text is null or empty then throw ApplicationException
		if (text == null) {
			throw new ApplicationException("Text to be searched cannot be null");
		}
		String searchText = text.trim();
		if (searchText.isEmpty()) {
			throw new ApplicationException(
					new StringBuilder("Text to be searched: ").append(text).append(" is Invalid").toString());
		}

		// Get the word count if the word is available otherwise return count 0.
		Long count = this.textCountMap.get(searchText.toLowerCase());
		count = count != null ? count : 0L;
		logger.info("Searched count for text: {} : {}", text, count);
		return count;
	}

	@Override
	@CacheResult(cacheName = "topNCountCache")
	public List<Entry<String, Long>> findTopNWordCounts(Integer topN) {
		if (logger.isDebugEnabled()) {
			logger.debug("Searching top {} text counts", topN);
		}

		// If topN is invalid then throw Application Exception
		if (topN == null || topN <= 0) {
			throw new ApplicationException(
					new StringBuilder("Requested TopN number: ").append(topN).append(" is invalid").toString());
		}
		if (topN > this.textCountMap.size()) {
			logger.warn("Requested top {} text exceeded the Total available text count of {}", topN,
					this.textCountMap.size());
		}

		// Get the topN word counts from the textMap and return a list of word
		// count entries.
		logger.info("Got Top {} text-count list", topN);
		return this.textCountMap.entrySet().stream().limit(topN).collect(Collectors.toList());
	}

	/**
	 * Returns an unmodifiable text count map initialized from the source text
	 * file.
	 * 
	 * @return An unmodifiable text count map. If the bean has not been
	 *         initialized with the init() method then null will be returned.
	 */
	public Map<String, Long> getTextCountMap() {
		return this.textCountMap != null ? Collections.unmodifiableMap(this.textCountMap) : null;
	}

	/**
	 * Internal method for reading the source text file based on the resource
	 * srcFile.
	 * 
	 * @return File text.
	 */
	private String readFile() {
		StringBuilder fileStrBuilder = new StringBuilder();

		try (Stream<String> stream = Files.lines(Paths.get(srcFile.getURI()), StandardCharsets.UTF_8)) {

			stream.forEach(str -> fileStrBuilder.append(str));

		} catch (Exception ex) {
			logger.error("Error occurred in loading Paragraph file. Application not initialized", ex);
			throw new ApplicationException("Error occurred in loading Paragraph file. Application not initialized", ex);
		}
		return fileStrBuilder.toString();
	}
	
	/**
	 * Parses the provided text to initialize the Text Count Map.
	 * @param textStr Text string to parse for map initialization.
	 */
	private void initializedTextCountMap(String textStr) {
		// Create word count map from the source file's text
		Map<String, Long> notSortedMap = Arrays
				.stream(textStr.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").split(" "))
				.filter(text -> !text.trim().isEmpty()).map(text -> new SimpleEntry<>(text, 1L))
				.collect(Collectors.groupingBy(SimpleEntry::getKey, Collectors.counting()));

		// Sort the word count map by value and assign it to the textCountMap
		notSortedMap.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed())
				.forEachOrdered(entry -> this.textCountMap.put(entry.getKey(), entry.getValue()));
		logger.info("Loaded Paragraph text. Total no. of words: {}", this.textCountMap.size());
	}
}

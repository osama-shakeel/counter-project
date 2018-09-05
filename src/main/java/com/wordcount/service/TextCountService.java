package com.wordcount.service;

import java.util.List;
import java.util.Map.Entry;

import com.wordcount.exception.ApplicationException;

/**
 * Interface for the Text Count Service. It includes different methods for
 * counting occurences of text.
 *
 */
public interface TextCountService {

	/**
	 * Finds the count of a text.
	 * 
	 * @param text
	 *            Text whose count is to be found.
	 * @return Count of text.
	 * @throws ApplicationException
	 *             If provided text is null or empty.
	 */
	Long findTextCount(String text);

	/**
	 * Returns the top N Words and their counts.
	 * 
	 * @param topN
	 *            Represents the top N word counts to search.
	 * @return List of Map entries, where each entry represents the word with
	 *         count as its value.
	 * @throws ApplicationException
	 *             If provided topN search is null or less than 1.
	 */
	List<Entry<String, Long>> findTopNWordCounts(Integer topN);
}

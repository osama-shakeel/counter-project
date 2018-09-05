package com.wordcount.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * VO class that represents the response for the Word Count search API:
 * /counter-api/search/
 *
 */
public class WordCountResultVO {
	private List<Entry<String, Long>> counts;

	public WordCountResultVO() {
		this.counts = new ArrayList<>();
	}

	public List<Entry<String, Long>> getCounts() {
		return counts;
	}

	public void setCounts(List<Entry<String, Long>> results) {
		this.counts = results;
	}

	public void addWordCount(final String word, final Long count) {
		this.counts.add(new Entry<String, Long>() {

			@Override
			public String getKey() {
				return word;
			}

			@Override
			public Long getValue() {
				return count;
			}

			@Override
			public Long setValue(Long value) {
				return value;
			}

		});
	}
}

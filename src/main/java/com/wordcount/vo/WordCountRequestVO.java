package com.wordcount.vo;

import java.util.List;

/**
 * VO class that represents the request for the Word Count search API:
 * /counter-api/search/
 *
 */
public class WordCountRequestVO {

	private List<String> searchText;

	public List<String> getSearchText() {
		return searchText;
	}

	public void setSearchText(List<String> searchText) {
		this.searchText = searchText;
	}
}

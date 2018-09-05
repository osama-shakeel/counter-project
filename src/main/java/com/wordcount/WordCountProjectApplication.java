package com.wordcount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Word Count main application class, with Caching enabled.
 *
 */
@SpringBootApplication
@EnableCaching
public class WordCountProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(WordCountProjectApplication.class, args);
	}
}

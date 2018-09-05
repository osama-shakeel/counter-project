package com.wordcount.api.utilities;

import java.io.Writer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.wordcount.exception.ApplicationException;

/**
 * Utility class for writing Java objects such as list/exception to a
 * PrintWriter in CSV format.
 *
 */
public class CSVWriterUtils {
	private static Logger logger = LoggerFactory.getLogger(CSVWriterUtils.class);

	/**
	 * Writes the given list of objects on the given Writer in CSV Format.
	 * @param list The list of objects to write as CSV.
	 * @param typeClass
	 * @param headers Array representing CSV headers
	 * @param writer Writer instance.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> void writeList(List<T> list, Class typeClass, String[] headers, Writer writer) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Converting list data: into CSV format");
			}
			ColumnPositionMappingStrategy<T> mapStrategy = new ColumnPositionMappingStrategy<>();

			mapStrategy.setType(typeClass);
			mapStrategy.generateHeader();

			String[] columns = headers;
			mapStrategy.setColumnMapping(columns);
			StatefulBeanToCsv<T> btcsv = new StatefulBeanToCsvBuilder<>(writer)
					.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).withMappingStrategy(mapStrategy).withSeparator('|')
					.build();
			btcsv.write(list);
			logger.debug("Converted list data to CSV");
		} catch (Exception ex) {
			logger.error("Error occured in converting list data to CSV format");
			throw new ApplicationException("Error occured in converting list data to CSV format", ex);
		}
	}

	/**
	 * Writes the given object on the given Writer in CSV Format.
	 * @param object The object to write as CSV.
	 * @param typeClass
	 * @param headers Array representing CSV headers
	 * @param writer Writer instance.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void writeOject(Object object, String[] headers, Writer writer) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Converting object: {} into CSV format", object);
			}
			ColumnPositionMappingStrategy mapStrategy = new ColumnPositionMappingStrategy();

			mapStrategy.setType(object.getClass());
			mapStrategy.generateHeader();

			String[] columns = headers;
			mapStrategy.setColumnMapping(columns);
			StatefulBeanToCsv btcsv = new StatefulBeanToCsvBuilder(writer).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
					.withMappingStrategy(mapStrategy).withSeparator('|').build();

			btcsv.write(object);
			logger.info("Converted object: {} into CSV format", object);
		} catch (Exception ex) {
			logger.error("Error occured in converting exception: {} into CSV format", object);
			throw new ApplicationException("Error occured in converted list data to CSV format", ex);
		}
	}
}

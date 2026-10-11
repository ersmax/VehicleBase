package process;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import exception.CustomException;

public class StringParsing {

	/**
	 * Separate a string of elements, using "," as separator 
	 * @param row	: a single row of elements
	 * @return		: an array of String elements 
	 */
	public static String[] rowParser(String row) {				
		return Arrays.stream(row.split(","))
					 .map(String::trim)
					 .toArray(String[]::new);
	}
	
	/**
	 * Turns the array items (e.g. color=yellow) into a pair Key-Value
	 * @param items	: the array of items to be transformed into Key-Value pairs
	 * @return		: the map of key-value attributes for each vehicle 
	 */
	public static Map<String, String> mapKeyValue(String[] items) {
		Map<String, String> map = new HashMap<>();
		
		for (String item : items) {
			String[] pair = item.split("=", 2);
			if (pair.length < 2)
				throw new CustomException("Invalid parameter (expected Key=value). "
										+ "Found instead: '" + item + "'");
			
			String key = pair[0].trim().toLowerCase();
			String value = pair[1].trim();
			
			if (key.isEmpty())
				throw new CustomException("Key (" + key + ") should not be empty");
			if (map.containsKey(key))
				throw new CustomException("Duplicate parameter (" + key + ")");
			
			map.put(key, value);
		}
		return map;
	}
	
}

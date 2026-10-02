package process;

import java.util.HashMap;
import java.util.Map;

import exception.CustomException;

public class StringParsing {

	/**
	 * Parse the string elements separated by comma into array elements
	 * @param parameters : the whole string 
	 * @return			 : an array with elements and no "," between them
	 */
	public static String[] parsing(String parameters) {			
		String[] result = parameters.split(",");
		
		int idx = 0;
		for (String item : result)
			result[idx++] = item.trim();
	
		return result;
	}
	
	/**
	 * Turns the array items (e.g. color=yellow) into a pair Key-Value
	 * @param items	: the array of items to be transformed into Key-Value pairs
	 * @param start	: the starting position should be after operation and vehicle type
	 * @return		: the map of key-value attributes for each vehicle 
	 */
	public static Map<String, String> mapKeyValue(String[] items, int start) {
		Map<String, String> map = new HashMap<>();
		
		for (int idx = start; idx < items.length; idx++) {
			String[] pair = items[idx].split("=", 2);
			if (pair.length < 2)
				throw new CustomException("Invalid parameter (expected Key=value). "
										+ "Found instead: '" + items[idx] + "'");
			
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

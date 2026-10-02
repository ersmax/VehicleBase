package utils;

import java.time.Year;
import java.util.Arrays;
import java.util.Map;

import exception.CustomException;

public class Validation {
	
	public static final String[] FUEL_TYPES  = {"benzina", "diesel", "gpl", "metano", "elettrica", "ibrida", "manuale"};
	public static final String[] CATEGORIES  = {"strada", "citycar", "utilitaria", "berlina", "suv", "sportiva", "fuoristrada", "corsa"};
	public static final String[] COLORS      = {"bianco", "nero", "grigio", "argento", "rosso", "blu", "verde", "giallo", "arancione", "marrone"};
	public static final String[] SUSPENSIONS = {"con", "senza"};

	// formats: car AA123AA, motorbike AA12345
	public static final String CAR_PLATE  = "[A-Z]{2}[0-9]{3}[A-Z]{2}";
	public static final String BIKE_PLATE = "[A-Z]{2}[0-9]{5}";
	
	
	/**
	 * @param map	: the map of attribute and value
	 * @param key	: the key we are searching
	 * @return		: Returns a value, or throw an Exception if it is missing or it's empty 
	 */
	public static String getString(Map<String, String> map, String key) {
		String value = map.get(key);
		if (value == null || value.isEmpty())
			throw new CustomException("Missing parameter: " + key);
		
		return value;
	}
	
	/** 
	 * @param map	: the map of attribute and value
	 * @param key	: the key we are searching
	 * @return		: Returns the numeric value, or throw error if NaN.
	 */
	public static Integer getInt(Map<String, String> map, String key) {
		String value = getString(map, key);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new CustomException("Parameter " + key + " must be a number. "
									+ "Found instead a value (" + value + ")");
		}
	}
	
	public static boolean getBool(Map<String, String> map, String key) {
		String value = getString(map, key).toLowerCase();
		switch (value) {
			case "si":
			case "sì":
			case "yes":
			case "true":
				return true;
			case "no":
			case "false":
				return false;
			default:
				throw new CustomException("Parameter " + key + " must be si/no. "
										+ "Found instead a value (" + value + ")");
		}
	}
	
	/**
	 * Returns the value if it is one of the allowed ones, otherwise throws
	 */
	public static String getOneOf(Map<String, String> map, String key, String[] allowed) {
		String value = getString(map, key).toLowerCase();
		for (String a : allowed)
			if (a.equals(value))
				return value;
		throw new CustomException("Invalid " + key + " (" + value + "). "
								+ "Allowed values: " + Arrays.toString(allowed));
	}

	/**
	 * Returns the number if it is between min and max (included), otherwise throws
	 */
	public static Integer getIntInRange(Map<String, String> map, String key, int min, int max) {
		Integer value = getInt(map, key);
		if (value < min || value > max)
			throw new CustomException("Parameter " + key + " must be between " + min + " and " + max
									+ ". Found instead a value (" + value + ")");
		return value;
	}

	/**
	 * A year cannot be in the future (1886 first car)
	 */
	public static Integer getYear(Map<String, String> map, String key) {
		return getIntInRange(map, key, 1886, Year.now().getValue());
	}

	/**
	 * Checks the plate format with a regular expression
	 */
	public static String getPlate(Map<String, String> map, String key, String pattern) {
		String value = getString(map, key).toUpperCase();
		if (!value.matches(pattern))
			throw new CustomException("Invalid plate format (" + value + ")");
		return value;
	}
	
}

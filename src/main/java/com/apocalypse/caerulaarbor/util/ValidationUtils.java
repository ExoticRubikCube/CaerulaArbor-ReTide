package com.apocalypse.caerulaarbor.util;

public class ValidationUtils {

	private ValidationUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	//可疑，需要解释
	public static boolean isValidString(String item, String name) {
		if (item == null || name == null)
			return false;
		if (item.equals("*")) {
			return true;
		}
		if (name.equals(item)) {
			return true;
		}
		if (item.contains("*")) {
			int index = item.indexOf("*");
			if (index < 1) {
				return false;
			}
			String clip = item.substring(0, index);
            return name.startsWith(clip);
		}
		return false;
	}
}
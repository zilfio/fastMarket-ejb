package it.univaq.mwt.fastmarket.common.utility;

public class ConversionUtility {

	public static String addPercentSuffix(String s) {
		if (s == null || "".equals(s)) {
			return "%";
		}
		return s + "%";
	}
	
}

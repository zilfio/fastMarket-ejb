package it.univaq.mwt.fastmarket.common.utility;

import java.util.Set;

public class FilterUtility {

	public static <T> Set<T> difference(Set<T> setA, Set<T> setB) {

		setA.removeAll(setB);
		
		return setA;
				
	}

}

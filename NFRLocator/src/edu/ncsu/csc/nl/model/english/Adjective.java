package edu.ncsu.csc.nl.model.english;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Represents various information we need to track about adjectives
 * 
 * @author John Slankas
 */
public class Adjective {

	public static final String[] SELECTORS = {"additional", "all", "appropriate", "associated", "both", "certain", 
										      "consecutive","corresponding", 
		                                      "current", "default", "different",
		                                      "few", "invalid", "individual", "last", "many", "multiple", "new","old","other", "own", "other","particular", "past","possible",
		                                      "previous","primary","recent","same","secondary","separate",
		                                      "several", "single","specific", "sufficient","upcoming", "various",
		                                      "first","second","third","fourth","fifth","sixth","seventh","eighth","ninth","tenth"
		                                     };
	private static HashSet<String> _selectorSet = new HashSet<String>(Arrays.asList(SELECTORS));
	

	public static boolean isSelector(String word) {
		return _selectorSet.contains(word.toLowerCase());
	}
	
}

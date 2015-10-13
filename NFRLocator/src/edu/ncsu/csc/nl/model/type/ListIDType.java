package edu.ncsu.csc.nl.model.type;

/**
 * Represents the different types of sentences that we could find
 * during the parsing.
 * 
 * @author John
 *
 */
public enum ListIDType {
	
	UNKNOWN("unknown","U"),
	UPPER_ALPHA("upper_alpha", "A"),
	LOWER_ALPHA("lower_alpha", "a"),
	NUMBER("number", "1"),
	UPPER_ROMAN("upper_roman", "I"),
	LOWER_ROMAN("lower roman", "i"),
	BULLET_DASH("bullet:-", "-"),
	BULLET_ASTERICK("bullet:*", "*"),
	BULLET_FILLED_CIRCLE("bullet:•", "•"),
	BULLET_EMPTY_CIRCLE("bullet:○", "○"),
	BULLET_LETTER_O("bullet:o", "o");
	
	private String _label;
	private String _abbreviation;
	
	private ListIDType(String label, String abbr) {
		_label = label;
	}
	
	public String toString() {
		return _label;
	}
	
	public String getAbbreviation() { return _abbreviation; }

	public static ListIDType getListIDType(char c) {
		if (c == 'i' || c == 'v'  || c== 'x') { return LOWER_ROMAN; }
		if (c == 'I' || c == 'V'  || c== 'X') { return UPPER_ROMAN; }
		if (c == 'o') { return BULLET_LETTER_O; }

		if (c >= '0' && c <= '9') { return NUMBER; }
		if (c >= 'A' && c <= 'Z') { return UPPER_ALPHA; }
		if (c >= 'a' && c <= 'z') { return LOWER_ALPHA; }
		
		if (c == '-') { return BULLET_DASH; }
		if (c == '*') { return BULLET_ASTERICK; }
		if (c == '•') { return BULLET_FILLED_CIRCLE; }
		if (c == '○') { return BULLET_EMPTY_CIRCLE; }
		
		
		return UNKNOWN;
	}
}

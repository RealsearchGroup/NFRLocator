package edu.ncsu.csc.nl.model.type;


/**
 * The access control source enumeration represents where an access control object
 * or an access control pattern originated. a set value originate?
 * 
 * 
 * @author John
 *
 */
public enum RelationSource {
	
	PATTERN("user-based pattern"),
	PATTERN_DERIVED_ACTIVE("active voice derived pattern"),
	PATTERN_DERIVED_PASSIVE("passive voice derived pattern"),
	SEED("seed"),
	UNKNOWN("unknown"),
	USER("user");
	
	private String _label;
	
	private RelationSource(String label) {
		_label = label;
	}
	
	public String toString() {
		return _label;
	}
	

}

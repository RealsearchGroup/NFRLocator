package edu.ncsu.csc.nl.model.type;

/**
 * A basic extension to boolean, but to allow for the fact that the
 * value may be "unknown" in addition to true/false
 * 
 * 
 * @author John
 *
 */
public enum BooleanType {

	UNKNOWN("unknown"),
	FALSE("false"),
	TRUE("true");
	
	private String _label;
	
	private BooleanType(String label){
		_label = label;
	}
	public String toString() {
		return _label;
	}
	
	public boolean getBooleanValue() throws java.lang.RuntimeException {
		if (this._label.equals("true")) {return true;}
		else if (this._label.equals("false")) {return false;}
		else {
			throw new RuntimeException("BooleanType was of unknown value;");
		}
	}
	
	public static BooleanType getBooleanType(boolean b) {
		if (b) { return TRUE; }
		else   { return FALSE; }
	}
	public static BooleanType getBooleanType(String s) {
		if (s.equalsIgnoreCase("true")) { return TRUE; }
		else { return FALSE; }
	}
}

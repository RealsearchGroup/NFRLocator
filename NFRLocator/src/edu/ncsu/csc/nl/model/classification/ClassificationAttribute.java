package edu.ncsu.csc.nl.model.classification;

/**
 * ClassificationAttribute represents a classification that can be used on a particular item.
 * 
 * Purpose is to allow the user interface to be more data driven and configurable.
 * 
 * @author John Slankas
 */
public class ClassificationAttribute  {

	/** name of the attribute */
	private String _name;
	
	/** What is the classification type?  Right now its only Boolean */
	private String _type;
	
	/** What is the description for the attribute */
	private String _description;
	
	/** What is the abbrevation for the attribute */
	private String _abbreviation;
	
	/** Comma delimited list of values to use when selecting a value */
	private String _values = "";
	
	/** Should we currently evaluate with this attribute or not? */
	private boolean _includeInEvaluation = true;
	
	
	public String getName() { return _name; }
	public String getType() { return _type; }
	public String getDescription() { return _description; }
	public String getAbbreviation() { return _abbreviation; }
	public String getValues() { return _values; }
	public boolean getIncludeInEvaluation() { return _includeInEvaluation; }
	
	public void setName(String newValue) { _name = newValue; }
	public void setType(String newType)  { _type = newType; }
	public void setDescription(String newDescription) { _description = newDescription; }
	public void setAbbreviation(String newValue) { _abbreviation = newValue; }
	public void setValues(String newValue) { _values = newValue; }
	public void setIncludeInEvaluation(boolean newValue) { _includeInEvaluation = newValue; }
	
	public String toString() {
		return _name;
	}
 }

package edu.ncsu.csc.nl.model.classification;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.ncsu.csc.nl.model.type.BooleanType;
import edu.ncsu.csc.nl.model.type.Source;

public class StringClassification extends ClassificationType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String _value;
	
	public StringClassification() {
		super();
		this.setValue(BooleanType.UNKNOWN);
	}	
		
	public StringClassification(String value) {
		super();
		this.setValue(value);
	}	
	
	public StringClassification(String value, Source source) {
		super(source);
		this.setValue(value);
	}
	
	
	public String getValue() {
		return _value;
	}
	
	public void setValue(Object newValue) { _value = (String) newValue; }
	
	
	public void setValue(String newValue) { _value= newValue; }
	
	@JsonIgnore
	public boolean isComplete() {
		throw new Error("not implemented");
	    //return false;
	}
	
	public String toString() { return _value.toString()+"("+this.getSource().toString()+")"; }
	
}

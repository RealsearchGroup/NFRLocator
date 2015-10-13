package edu.ncsu.csc.nl.model.classification;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.ncsu.csc.nl.model.type.BooleanType;
import edu.ncsu.csc.nl.model.type.Source;

public class BooleanClassification extends ClassificationType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private BooleanType _value;
	
	public BooleanClassification() {
		super();
		this.setValue(BooleanType.UNKNOWN);
	}	
		
	public BooleanClassification(BooleanType value) {
		super();
		this.setValue(value);
	}	
	
	public BooleanClassification(BooleanType value, Source source) {
		super(source);
		this.setValue(value);
	}
	
	
	public BooleanType getValue() {
		return _value;
	}
	
	public void setValue(Object newValue) { _value = (BooleanType) newValue; }
	
	
	public void setValue(BooleanType newValue) { _value= newValue; }
	
	@JsonIgnore
	public boolean isComplete() {
		throw new Error("not implemented");
	    //return false;
	}
	
	public String toString() { return _value.toString()+"("+this.getSource().toString()+")"; }
	
}

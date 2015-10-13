package edu.ncsu.csc.nl.model.classification;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.ncsu.csc.nl.model.type.Source;

/**
 * This class represents the super-class class for all classification types
 * 
 * @author John Slankas
 */
public class ClassificationType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Source _source = Source.NOT_SET;
	
	private Object _objectValue = "";
	
	protected ClassificationType() {
		_source = Source.NOT_SET;
	}
	
	protected ClassificationType(Source src) {
		this.setSource(src);
	}
	
	public Source getSource() { return _source; }
	public void setSource(Source newSourceValue) { _source = newSourceValue; }
	
	@JsonIgnore
	public  boolean isComplete() {
		return false;
	}
	
	public  Object getValue() {
		return _objectValue;
	}
	
	public void setValue(Object newValue) {
		_objectValue = newValue;
	}
}

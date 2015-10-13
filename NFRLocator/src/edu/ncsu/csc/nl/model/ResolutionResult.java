package edu.ncsu.csc.nl.model;

public class ResolutionResult {

	private String _value;
	private double _sentencePosition;
	
	public ResolutionResult(String value, double sentencePosition) {
		_value = value;
		_sentencePosition = sentencePosition;
	}
	
	
	public String getValue() {
		return _value;
	}
	
	public double getSentencePosition() {
		return _sentencePosition;
	}
}

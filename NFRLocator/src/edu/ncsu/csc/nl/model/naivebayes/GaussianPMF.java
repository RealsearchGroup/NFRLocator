package edu.ncsu.csc.nl.model.naivebayes;

import java.io.Serializable;

public class GaussianPMF extends PMF implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private double _mean;

	private double doubleVariance;
	private double initialFactor;
	
	public GaussianPMF(double mean, double standardDeviation) {
		_mean = mean;

		doubleVariance       = 2.0 * standardDeviation * standardDeviation;
		initialFactor = 1.0 / Math.sqrt( 2.0 * Math.PI * standardDeviation * standardDeviation  );
	}

	@Override
	public double getProbability(String sValue) {
		// TODO Auto-generated method stub
		double value = Double.parseDouble(sValue);
		
		double result = initialFactor * Math.exp( (-1.0 * (value - _mean) * (value - _mean) ) /doubleVariance)   ;
		
		return result;
	}
	

}

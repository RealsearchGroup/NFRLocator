package edu.ncsu.csc.nl.model.naivebayes;

import java.io.Serializable;

public abstract class PMF  implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract double getProbability(String value);
}

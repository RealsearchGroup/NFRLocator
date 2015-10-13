package edu.ncsu.csc.nl.model.ml;

import java.util.HashMap;

import edu.ncsu.csc.nl.model.Sentence;
import edu.ncsu.csc.nl.model.classification.BooleanClassification;

public class Document {
	public String documentID;          // used for performing by document validations
	public Sentence sentence;          // the actual sentence this refers to
	
	public boolean inTest;            // sentence has been marked for testing and should not be used to compute the model
	public boolean unlabeledTraining; // sentence has been marked as unlabelled in the training set.  Used for Expectation-Maximization processing
	
	public HashMap<String, BooleanClassification> emBooleanClassifications;
}

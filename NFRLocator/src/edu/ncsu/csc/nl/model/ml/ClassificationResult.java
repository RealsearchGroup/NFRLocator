package edu.ncsu.csc.nl.model.ml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import edu.ncsu.csc.nl.model.classification.ClassificationType;
import edu.ncsu.csc.nl.model.ml.InstanceLearner.IntermediateResults;

public class ClassificationResult {
	public static final String INTERNAL_KNN         = "internal: k-nn";
	public static final String INTERNAL_NAIVE_BAYES = "internal: naive bayes";
	public static final String WEKA_NAIVE_BAYES = "weka: naive bayes";
	public static final String WEKA_SMO         = "weka: SVM(SMO)";
	
	public String classificationMethod;
		
	public HashMap<String, ClassificationType> classifications = new HashMap<String, ClassificationType>();
	
	public HashMap<String, double[]>distributions = new HashMap<String, double[]>();
	
	public IntermediateResults[] distances = new IntermediateResults[0];
	
	
	public int k;
	public double averageDistance;
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		ArrayList<String> al = new ArrayList<String>(classifications.keySet());
		Collections.sort(al);
		
		for (String key:al) {
			
			sb.append(key);
			sb.append("(");
			if (classifications.get(key) == null) {
				sb.append("null");
			} else {
				sb.append(classifications.get(key));
			}
			sb.append(")   ");
		}
		return sb.toString();
	}
	
}

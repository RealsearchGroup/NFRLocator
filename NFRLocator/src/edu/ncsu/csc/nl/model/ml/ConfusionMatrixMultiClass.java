package edu.ncsu.csc.nl.model.ml;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Implements a binary confusion chart.
 * 
 * Related Wikipedia Pages:  (fyi, wiki's confusion matrices are transposed 
 * from how most books present the matrix with actuals as rows and predictions as columns)
 *   http://en.wikipedia.org/wiki/Sensitivity_and_specificity  
 * 	 http://en.wikipedia.org/wiki/Precision_and_recall
 *   http://en.wikipedia.org/wiki/Confusion_matrix
 *   http://en.wikipedia.org/wiki/Binary_classification
 *   
 * Standard mechanism to evaluate machine learning, classification, information retrieval.
 * 
 * 
 * @author Adminuser
 *
 */
public class ConfusionMatrixMultiClass {
	private List<String> _classList;
	
	private HashMap<String, HashMap<String, Integer>> matrix;   //index by row and then col.  So.  Actual followed by predictions(columns)
	
	public ConfusionMatrixMultiClass(List<String> classList) {
		List<String> _classList = classList;
		
		matrix = new HashMap<String, HashMap<String, Integer>>();
		for (String item: classList) {
			HashMap<String, Integer> hash = new HashMap<String, Integer>();
			hash.put(item,0);
		}
	}
	
	public ConfusionMatrixMultiClass(String[] classList) {
		this(Arrays.asList(classList));
	}
	
	public void add(ConfusionMatrixMultiClass cm) {
		
		//		HashMap<String, HashMap<String, Integer>> matrix;  
		
	}
		
	/**
	 * Sets the appropriate value (TP,TN, FP, FN) 
	 * 
	 * @param actualClass
	 * @param predicatedClass
	 */
	public void setCell(String actualClass, String predictedClass) {
		 matrix.get(actualClass).put(predictedClass, matrix.get(actualClass).get(predictedClass) +1);
	}

	//public double getPrecision() {  // TP / (TP+FP)
		//return ((double) _truePositiveCount) / ( _truePositiveCount + _falsePositiveCount );
	//}
	
	/**
	 * Out of the positive items in the test set, how well did we identify those. 
	 * Recall = TP / (TP + FN)
	 * 
	 * @return
	 */
	//public double getRecall() {    // TP / (TP+FN)
		//return  ((double)_truePositiveCount) / ( _truePositiveCount + _falseNegativeCount );
	//}

	//public double getTrueNegativeRate() {   // TN / TN+FP
		//return ((double) _trueNegativeCount) / ( _trueNegativeCount + _falsePositiveCount);
	//}
	
	//public double getAccuracy() {  // (tp+tn)/(tp+tn+fp+fn) {
		//return ((double) _truePositiveCount +(double) _trueNegativeCount) / (_truePositiveCount+ _trueNegativeCount+_falsePositiveCount+_falseNegativeCount);
	//}
	
	/**
	 * Relates to the ability to identify positive results.  Same as recall.
	 * @return
	 */
	//public double getSensitivity() {
		//return this.getRecall();
	//}
	
	/**
	 * Sepcificity relates to identify negative results.  Equals true negative rate
	 * 
	 * @return
	 */
	//public double getSpecificity() {
		//return this.getTrueNegativeRate();
	//}
	
	/**
	 * Harmonic mean of the precision and recall.  Values favors the lower value.
	 * 
	 * @return
	 */
	/*
	public double getF1Measure() {
		double precision = this.getPrecision();
		double recall    = this.getRecall();
		
		return 2 * (precision*recall)/(precision+recall);
	}
	*/
	/*
	public String toString() {
		//return "TP: "+_truePositiveCount+"  TN: "+_trueNegativeCount+"  FP: "+_falsePositiveCount+"  FN: "+_falseNegativeCount;
	}
	*/
}


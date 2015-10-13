package edu.ncsu.csc.nl.model.ml;

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
public class ConfusionMatrix {

	private int _truePositiveCount = 0;
	private int _trueNegativeCount = 0;
	private int _falsePositiveCount = 0;
	private int _falseNegativeCount = 0;
	
	public ConfusionMatrix() {
	}
	
	public ConfusionMatrix(ConfusionMatrix cm) {
		this._truePositiveCount  = cm.getTruePositive();
		this._trueNegativeCount  = cm.getTrueNegative();
		this._falsePositiveCount = cm.getFalsePositive();
		this._falseNegativeCount = cm.getFalseNegative();
	}
	
	public ConfusionMatrix(double[][] matrix) {
		this._truePositiveCount  = (int) matrix[0][0];
		this._trueNegativeCount  = (int) matrix[1][1];
		this._falsePositiveCount = (int) matrix[1][0];
		this._falseNegativeCount = (int) matrix[0][1];
	}	
	
	public void add(ConfusionMatrix cm) {
		this._truePositiveCount  += cm.getTruePositive();
		this._trueNegativeCount  += cm.getTrueNegative();
		this._falsePositiveCount += cm.getFalsePositive();
		this._falseNegativeCount += cm.getFalseNegative();
	}
	
	public int getTruePositive()   { return _truePositiveCount; }
	public int getTrueNegative()   { return _trueNegativeCount; }
	public int getFalsePositive()  { return _falsePositiveCount; }
	public int getFalseNegative()  { return _falseNegativeCount; }
	
	public int getTotal() {
		return (_truePositiveCount + _trueNegativeCount + _falsePositiveCount + _falseNegativeCount );
	}
	
	/**
	 * Sets the appropriate value (TP,TN, FP, FN) 
	 * 
	 * @param actualClass
	 * @param predicatedClass
	 */
	public void setCell(boolean actualClass, boolean predictedClass) {
		if (actualClass) {
			if (predictedClass) { _truePositiveCount++; }
			else { _falseNegativeCount++; }
		}
		else {  //actual value is false / "no"
			if (predictedClass) { _falsePositiveCount++; }
			else { _trueNegativeCount++; }
		}
	}

	public double getPrecision() {  // TP / (TP+FP)
		return ((double) _truePositiveCount) / ( _truePositiveCount + _falsePositiveCount );
	}
	
	/**
	 * Out of the positive items in the test set, how well did we identify those. 
	 * Recall = TP / (TP + FN)
	 * 
	 * @return
	 */
	public double getRecall() {    // TP / (TP+FN)
		return  ((double)_truePositiveCount) / ( _truePositiveCount + _falseNegativeCount );
	}

	public double getTrueNegativeRate() {   // TN / TN+FP
		return ((double) _trueNegativeCount) / ( _trueNegativeCount + _falsePositiveCount);
	}
	
	public double getAccuracy() {  // (tp+tn)/(tp+tn+fp+fn) {
		return ((double) _truePositiveCount +(double) _trueNegativeCount) / (_truePositiveCount+ _trueNegativeCount+_falsePositiveCount+_falseNegativeCount);
	}
	
	/**
	 * Relates to the ability to identify positive results.  Same as recall.
	 * @return
	 */
	public double getSensitivity() {
		return this.getRecall();
	}
	
	/**
	 * Sepcificity relates to identify negative results.  Equals true negative rate
	 * 
	 * @return
	 */
	public double getSpecificity() {
		return this.getTrueNegativeRate();
	}
	
	/**
	 * Harmonic mean of the precision and recall.  Values favors the lower value.
	 * 
	 * @return
	 */
	public double getF1Measure() {
		double precision = this.getPrecision();
		double recall    = this.getRecall();
		
		return 2 * (precision*recall)/(precision+recall);
	}
	
	public String toString() {
		return "TP: "+_truePositiveCount+"  TN: "+_trueNegativeCount+"  FP: "+_falsePositiveCount+"  FN: "+_falseNegativeCount;
	}
	
	public double getKappaScore() {
		double total = (double) this.getTotal();  // this gives up the conversion we need to cast everything to doubles ...
		
		double TP = (double) this.getTruePositive();
		double TN = (double) this.getTrueNegative();
		double FP = (double) this.getFalsePositive();
		double FN = (double) this.getFalseNegative();
		
		double probObserved = (TP + TN) / total;
		double probExpected = ( ((TP+FP)/total) * ((TP+FN)/total)) + (((TN+FN)/total) * ((TN+FP)/total));
		
		double kappa = (probObserved - probExpected) / (1 - probExpected);
		
		return kappa;
	}
	
}


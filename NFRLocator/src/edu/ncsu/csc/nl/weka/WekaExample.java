package edu.ncsu.csc.nl.weka;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToNominal;

public class WekaExample {

	public static void runNaiveBayes(weka.core.Instances dataSet) {
		try {
			//Need to convert string values to nominal so we can apply most classifiers
			 String[] options = new String[2];
			 options[0] = "-R";                                // "range"
			 options[1] = "first-last";                        // first attribute
			 StringToNominal filterSN = new StringToNominal(); // new instance of filter
			 filterSN.setOptions(options);                     // set options
			 filterSN.setInputFormat(dataSet);                 // inform filter about dataset **AFTER** setting options
			 dataSet = Filter.useFilter(dataSet, filterSN);    // apply filter			
			
			 Classifier cModel = (Classifier) new NaiveBayes();
			 cModel.buildClassifier(dataSet);
	
			 // Test the model
			 Evaluation eTest = new Evaluation(dataSet);
			 eTest.evaluateModel(cModel, dataSet);
	
			 // Print the result à la Weka explorer:
			 String strSummary = eTest.toSummaryString();
			 ///System.out.println(eTest.toString());
			 System.out.println(strSummary);
			 System.out.println(eTest.toClassDetailsString());
			 System.out.println(eTest.toMatrixString());
			 
	        /*
			 // Get the confusion matrix
			 double[][] cmMatrix = eTest.confusionMatrix();
			 for(int row_i=0; row_i<cmMatrix.length; row_i++){
				 for(int col_i=0; col_i<cmMatrix.length; col_i++){
					 System.out.print(cmMatrix[row_i][col_i]);
					 System.out.print("|");
				 }
				 System.out.println();
			 }
			 */
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
		
}

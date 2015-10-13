package edu.ncsu.csc.nl.model.ml;

import javax.swing.JOptionPane;

import edu.ncsu.csc.nl.model.classification.ClassificationAttribute;
import edu.ncsu.csc.nl.model.classification.ClassificationAttributeTableModel;
import edu.ncsu.csc.nl.model.distance.LevenshteinSentenceAsWordsDistance;
import edu.ncsu.csc.nl.model.distance.SentenceDistance;
import edu.ncsu.csc.nl.model.english.StopWord;

public class ExpirementOptions {
	public static final int CANCELLED_OPERATION = -1;
	
	/** folds will be randomly created based upon the number specified by the numberOfFolds option */
	public static final int FOLD_RANDOM = 1;
	
	/** folds will be created by the different document IDs in the learner */
	public static final int FOLD_BY_DOCUMENT = 2;
	
	/** the learner will be tested against a specified set of test sentences from the user */
	public static final int FOLD_TEST = 3;
	
	private static Object[] distanceMethods = {"CosineTermFreqDistance","CosineTermFreqIDFDistance","DiceWordSetDistance","GraphWalkDistance","JacardWordSetDistance","LevenshteinSentenceAsStringDistance","LevenshteinSentenceAsWordsDistance","LinearWalkDistance","TreeRelationshipAsLevenshteinDistance"};
	private static String defaultDistanceMethod = "LevenshteinSentenceAsWordsDistance";

	/** methods will set this message to identify issues if an error occurred */
	private String _lastOperationMessage = null;
	
	private ClassificationAttribute _primaryClassification;

	private int _foldType = FOLD_RANDOM;
	
	private int _k;
	private int _numberOfFolds;
	private double _threshold;
	private boolean _showRandomResults;
	private SentenceDistance _sentenceDistance;
	private String[] _stopWords;
	
	public ExpirementOptions() {
		_primaryClassification = null; 
		_foldType = FOLD_RANDOM;
		_k = 3;
		_numberOfFolds = 10;
		_threshold = .85;
		_showRandomResults = false;
		_sentenceDistance = new LevenshteinSentenceAsWordsDistance();
		_stopWords = StopWord.getListByName(StopWord.EMPTY);
	}
	
	public ExpirementOptions(ClassificationAttribute primaryClassification, int foldType, int k, int numberOfFolds, double threshold, boolean showRandomResults,
			                 SentenceDistance sentenceDistance, String[] stopWords) {
		_primaryClassification = primaryClassification;
		_foldType = foldType;
		_k = k;
		_numberOfFolds = numberOfFolds;
		_threshold = threshold;
		_showRandomResults = showRandomResults;
		_sentenceDistance =  sentenceDistance;
		_stopWords = stopWords;		
	}
	
	public ExpirementOptions(ClassificationAttribute primaryClassification, int k, int numberOfFolds, double threshold, boolean showRandomResults) {
		this(primaryClassification, FOLD_RANDOM,k, numberOfFolds, threshold, false, new LevenshteinSentenceAsWordsDistance(),StopWord.getListByName(StopWord.EMPTY));
	}	
	
	public ExpirementOptions(ClassificationAttribute primaryClassification, int k, double threshold, boolean showRandomResults) {
		this(primaryClassification, FOLD_BY_DOCUMENT,k, 0, threshold, false, new LevenshteinSentenceAsWordsDistance(),StopWord.getListByName(StopWord.EMPTY));
	}		
	
	public SentenceDistance getSentenceDistanceMethodFromUser() {
		SentenceDistance sentenceDistance = null;
		
		Object result = JOptionPane.showInputDialog(null, "Select sentence distance algorithm", "Perform Self Evaluation", JOptionPane.PLAIN_MESSAGE,null, distanceMethods,defaultDistanceMethod);
		try {
			sentenceDistance = (SentenceDistance) Class.forName("edu.ncsu.csc.nl.model.distance."+result.toString()).newInstance();
		}
		catch (Throwable t){
			_lastOperationMessage = t.toString();
			return null;
		}	
	
		return sentenceDistance;
	}
	
	/**
	 * Gets the value to be used from k from the user.  If a bad value is entered, -1 is returned;
	 * 
	 * @param maxSize what is the maximum value that can be set for k?
	 * @return the value the user entered.  If user cancel's the operation, CANCELLED_OPERATION is returned.
	 */
	public int getKvalueFromUser(int maxSize) {
		String value = JOptionPane.showInputDialog(null,"<html>Enter value for <i>k</i>:</html>","Perform Self-Evaluation", JOptionPane.QUESTION_MESSAGE);
		int k = CANCELLED_OPERATION;
		
		try {
			k = Integer.parseInt(value);
			if (k<1 || k > maxSize) {
				_lastOperationMessage = "User entered a bad integer value for k: "+value;
				return CANCELLED_OPERATION;
			}
		}
		catch (Throwable t) {
			_lastOperationMessage = "User entered a bad integer value for k: "+value;
			return CANCELLED_OPERATION;			
		}
		return k;
	}
	
	

	public int getNumberOfFoldsFromUser(int maxSize) {
		//TODO: probably should have an object that sets the various options
		String value = JOptionPane.showInputDialog(null,"<html>Number of folds to use in the evaluation(2-"+maxSize+"): </html>","Perform Self-Evaluation", JOptionPane.QUESTION_MESSAGE);
		int numberOfFolds = CANCELLED_OPERATION;

		try {
			numberOfFolds = Integer.parseInt(value);
			if (numberOfFolds<3 || numberOfFolds >  maxSize) {
				_lastOperationMessage = "User entered a bad integer value for numberOfFolds: "+value;
				return CANCELLED_OPERATION;
			}
		}
		catch (Throwable t) {
			_lastOperationMessage = "User entered a bad integer value for numberOfFolds: "+value;
			return CANCELLED_OPERATION;			
		}
		
		return numberOfFolds;
	}

	public double getThresholdFromUser() {
		String value = JOptionPane.showInputDialog(null,"<html>Threshold: </html>","Perform Self-Evaluation", JOptionPane.QUESTION_MESSAGE);
		double threshold = CANCELLED_OPERATION;
		try {
			threshold = Double.parseDouble(value);
		}
		catch (Throwable t) {
			_lastOperationMessage = "Bad value for threshold: "+ value;
			return CANCELLED_OPERATION;			
		}
		return threshold;
	}
	
	public boolean includeRandomResultsFromUser() {
		int answer = JOptionPane.showConfirmDialog(null, "Compute random results?", "Perform Self Evaluation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		return (answer == JOptionPane.YES_OPTION); 
	}
	
	
	public String getLastOperationMessage()  { return _lastOperationMessage; }
	public ClassificationAttribute getPrimaryClassification() { return _primaryClassification; }
	public int getFoldType()                 { return _foldType; }
	public int getK()                        { return _k; }
	public int getNumberOfFolds()            { return _numberOfFolds; }
	public double getThreshold()             { return _threshold; }
	public boolean showRandomResults()       { return _showRandomResults; }
	public SentenceDistance getSentenceDistance() { return _sentenceDistance; }
	public String[] getStopWords()           { return _stopWords; }
	
	
	/**
	 * Creates a user expirement options from the items selected by the user
	 * 
	 * @param numberOfSentences
	 * @param classifications
	 * @return
	 */
	public static ExpirementOptions establishOptionsFromUserFeedback(int numberOfSentences, ClassificationAttributeTableModel classifications) {
		ExpirementOptions eo = new ExpirementOptions();
		
		eo._k = eo.getKvalueFromUser(numberOfSentences);
		if (eo._k == CANCELLED_OPERATION) { return eo; }
		
		eo._threshold = eo.getThresholdFromUser();
		if (eo._threshold == CANCELLED_OPERATION) { return eo; }
		
		eo._numberOfFolds = eo.getNumberOfFoldsFromUser(numberOfSentences);
		if (eo._numberOfFolds == CANCELLED_OPERATION) { return eo; }
		
		eo._sentenceDistance = eo.getSentenceDistanceMethodFromUser();
		if (eo._sentenceDistance == null) { return eo; }
		
		eo._showRandomResults = eo.includeRandomResultsFromUser();
		
		return eo;
	}
	
	
}
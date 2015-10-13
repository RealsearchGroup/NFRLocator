package edu.ncsu.csc.nl.model.ml;

import java.util.HashSet;

import edu.ncsu.csc.nl.model.PorterStemmer;
import edu.ncsu.csc.nl.model.classification.ClassificationAttribute;
import edu.ncsu.csc.nl.model.distance.LevenshteinSentenceAsWordsDistance;
import edu.ncsu.csc.nl.model.distance.SentenceDistance;
import edu.ncsu.csc.nl.model.english.StopWord;
import edu.ncsu.csc.nl.model.type.WordType;

public class ClassifierOptions {
	public static final int CANCELLED_OPERATION = -1;
	
	/** folds will be randomly created based upon the number specified by the numberOfFolds option */
	public static final int FOLD_RANDOM = 1;
	
	/** folds will be created by the different document IDs in the learner */
	public static final int FOLD_BY_DOCUMENT = 2;
	
	/** the learner will be tested against a specified set of test sentences from the user */
	public static final int FOLD_TEST = 3;
	
	/** methods will set this message to identify issues if an error occurred */
	private String _lastOperationMessage = null;
	
	private ClassificationAttribute _primaryClassification; 
	
	private SentenceDistance _sentenceDistance;
	
	private int _foldType = FOLD_RANDOM;
	
	private int _numberOfFolds;
	private boolean _showRandomResults;
	private String[] _stopWords;
	private HashSet<String> _stopWordsSet = null;

	private WordType _wordType = WordType.LEMMA;
	
	private boolean _useTDFIDF = false;
	private int _k = 1;
	private double _threshold = 0.85;
	
	public ClassifierOptions() {
		_primaryClassification = null; 
		_foldType = FOLD_RANDOM;
		_numberOfFolds = 10;
		_showRandomResults = false;
		_stopWords = StopWord.getListByName(StopWord.EMPTY);
		_useTDFIDF = false;
		_sentenceDistance = new LevenshteinSentenceAsWordsDistance();
	}
	
	public ClassifierOptions(ClassificationAttribute primaryClassification, int foldType, WordType wordType, int numberOfFolds, boolean showRandomResults,
			                 String[] stopWords, boolean useTDFIDF) {
		_primaryClassification = primaryClassification;
		_foldType = foldType;
		_wordType = wordType;
		_numberOfFolds = numberOfFolds;
		_showRandomResults = showRandomResults;
		_stopWords = stopWords;		
		_useTDFIDF = useTDFIDF;
		_sentenceDistance = new LevenshteinSentenceAsWordsDistance();
	}

	

	public SentenceDistance getSentenceDistance() { return _sentenceDistance; }
	public String getLastOperationMessage()  { return _lastOperationMessage; }
	public ClassificationAttribute getPrimaryClassification() { return _primaryClassification; }
	public int getK()                        { return _k; } 
	public int getFoldType()                 { return _foldType; }
	public WordType getWordType()            { return _wordType;  }
	public int getNumberOfFolds()            { return _numberOfFolds; }
	public boolean showRandomResults()       { return _showRandomResults; }
	public boolean useTDFIDF()               { return _useTDFIDF; }
	public double getThreshold()             { return _threshold; }
	public HashSet<String> getStopWords()    {
		if (_stopWordsSet == null) {
			HashSet<String> temp = new HashSet<String>(_stopWords.length);
			for (String w: _stopWords) {
				if (this.getWordType().equals(WordType.STEM)) {
					temp.add(PorterStemmer.getStem(w));
				}
				else {
					temp.add(w);
				}
			}
			_stopWordsSet = temp;
		}
 		 return _stopWordsSet; 
	}
	
	public HashSet<String> getStopWordsAsArray()    {
		if (_stopWordsSet == null) {
			HashSet<String> temp = new HashSet<String>(_stopWords.length);
			for (String w: _stopWords) {
				if (this.getWordType().equals(WordType.STEM)) {
					temp.add(PorterStemmer.getStem(w));
				}
				else {
					temp.add(w);
				}
			}
			_stopWordsSet = temp;
		}
 		 return _stopWordsSet; 
	}	
	
	public String toString() { return _primaryClassification.getName(); }
	
}
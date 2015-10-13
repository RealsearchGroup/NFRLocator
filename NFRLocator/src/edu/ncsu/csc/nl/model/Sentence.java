package edu.ncsu.csc.nl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.ncsu.csc.nl.GCController;
import edu.ncsu.csc.nl.event.NLPEventManager;
import edu.ncsu.csc.nl.event.NLPEventSentenceDataEvent;
import edu.ncsu.csc.nl.event.NLPEventType;
import edu.ncsu.csc.nl.model.classification.BooleanClassification;
import edu.ncsu.csc.nl.model.classification.ClassifiableItem;
import edu.ncsu.csc.nl.model.classification.ClassificationAttribute;
import edu.ncsu.csc.nl.model.classification.ClassificationAttributeTableModel;
import edu.ncsu.csc.nl.model.classification.ClassificationType;
import edu.ncsu.csc.nl.model.classification.StringClassification;
import edu.ncsu.csc.nl.model.distance.LevenshteinSentenceAsWordsDistance;
import edu.ncsu.csc.nl.model.english.Abbreviation;
import edu.ncsu.csc.nl.model.english.MiscCheck;
import edu.ncsu.csc.nl.model.english.Voice;
import edu.ncsu.csc.nl.model.ml.ClassificationResult;

import edu.ncsu.csc.nl.model.type.BooleanType;
import edu.ncsu.csc.nl.model.type.PartOfSpeech;
import edu.ncsu.csc.nl.model.type.SentenceType;
import edu.ncsu.csc.nl.model.type.Source;
import edu.ncsu.csc.nl.model.type.WordType;

import edu.stanford.nlp.semgraph.SemanticGraph;



/**
 * Sentence represents a sentence in the document.
 * There are two versions of the sentence that are maintained:
 *    1) the original sentence as is in the document (_originalSentence, getSentence, setSentence)
 *    2) the sentence passed to parser (_parserSentence, getParserSentence, setParserSentence)
 *       This varies from the orginalSentence in two cases -
 *       a. if it starts with an ID, the ID is removed
 *       b. if it is a list element, it is combined with the list start.  (a is also applied)
 * 
 * JSON Representation Notes:
 * - when reading back from a JSON, representation, it is necessary to call
 *   fixClassificationsFromLoad() and fixAccessControlRelationsFromLoad()
 *   
 *   
 *  
 * @author John
 *
 */
public class Sentence  implements Serializable, ClassifiableItem, Comparable<Sentence> {
	
	public static final double UNASSIGNED_SENTENCE_POSITION = -1.0;
	
	public static final long serialVersionUID = 1;
	
	private WordVertex _root;
	
	/* list of the word nodes for the sentence.  use this for the instance based learning 
	 * algorithm as its easier to traverse this versus the graph.  
	 */
	private WordVertex[] _wordList;  

	private WordVertex[] _sortedByPositionWordList;
	
	//private edu.stanford.nlp.util.CoreMap _stanfordSentence;
	
	private SemanticGraph _stanfordGraph;
	
	private String _orginalSentence;
	
	private String _parserSentence;

	private int _numberOfNodes;			
	
	private double _originalSentencePosition = UNASSIGNED_SENTENCE_POSITION;
	
	private SentenceType _sentenceType = SentenceType.UNKNOWN;
	
	private boolean _relatedToPrevious = false;
	
	private String _comments = "";
	
	/** What was the orgin of this sentence?  useful for a document gleamed from many places ... */
	private String _source = "";
	
	/**
	 * Does this sentence represent a legal-based requirement?
	 */
	private boolean _legal = false;
	
	/***
	 * Refers to sentence?  if this sentence refers to another sentence, this will contain that referred to sentence number
	 */
	private double _referredToSentence = UNASSIGNED_SENTENCE_POSITION;
	
	/** Have this sentence been processed or not? This only will get set if we are learning mode... */
	private boolean _processed = false;
	
	/** have we completely trained this sentence or not? */ 
	private boolean _trained; 
	
	
	/** name that this document belongs to.   Used to perform document-based evaluation within the learner */
	private String _documentID = "unknown";
	
	/** if there was an identifier for the sentence, it is set here.*/
	private String _sentenceID = "";
	
	private HashMap<String, ClassificationType> _classifications = new HashMap<String, ClassificationType>();
	
	private transient int _clusterID = -1;
	
	private String _requirements = "";
	public String getRequirements() { return _requirements; }
	public void setRequirements(String newText) {_requirements = newText; }
	
	/** Was the parser correct for this sentence?  */
	private boolean _parsedCorrectly = true;
	
	
	private String _userAssignedRole = "";
	public String getUserAssignedRole() { return _userAssignedRole; }
	public void setUserAssignedRole(String newText) { _userAssignedRole = newText; }
	
	
	public Sentence(WordVertex root, SemanticGraph graph, String sentence) {
		_root = root;
		_stanfordGraph = graph;
		_orginalSentence = sentence;
		
		_numberOfNodes = root.getGraphSize();
		
		_wordList = new WordVertex[_numberOfNodes];
		_sortedByPositionWordList = new WordVertex[_numberOfNodes];
		_root.populateWordVertexArray(_wordList);
		System.arraycopy(_wordList, 0, _sortedByPositionWordList, 0, _numberOfNodes);
		
		
		java.util.Arrays.sort(_sortedByPositionWordList, new java.util.Comparator<WordVertex>() {
		    public int compare(WordVertex o1, WordVertex o2) {
		    	return Integer.compare(o1.getStartIndexOfWordInSentence(), o2.getStartIndexOfWordInSentence());
		    }
		  });
		
	}
	
	
	public Sentence() {
	}
	
	public void setSentence( String sentence) {
		_orginalSentence = sentence;
	}
	
	public void setParserSentence( String sentence) {
		_parserSentence = sentence;
	}	
	
	@JsonIgnore
	public WordVertex getRoot() {
		return _root;
	}

	public String getDocumentID() {
		return _documentID;
	}
	
	public void setDocumentID(String value) {
		_documentID = value;
	}
	
	public String getSentenceID() {
		return _sentenceID;
	}
	
	public void setSentenceID(String newValue) {
		_sentenceID = newValue;
	}
	
	public double getOriginalSentencePosition() {
		return _originalSentencePosition;
	}
	
	public void setOriginalSentencePosition(double newValue) {
		_originalSentencePosition = newValue;
	}

	public boolean isRelatedToPrevious() {
		return _relatedToPrevious;
	}
	
	public void setRelatedToPrevious(boolean newValue) {
		_relatedToPrevious = newValue;
	}

	public SentenceType getSentenceType() {
		return _sentenceType;
	}
	
	public void setSentenceType(SentenceType newType) {
		_sentenceType = newType;
	}
	
	public boolean isLegal() {
		return _legal;
	}
	
	public void setLegal(boolean newValue) {
		_legal = newValue;
	}
	
	public double getReferredToSentence() {
		return _referredToSentence;
	}
	
	public void setReferredToSentence(double newValue) {
		_referredToSentence = newValue;
	}
	
	@JsonIgnore	
	public SemanticGraph getSemanticGraph() {
		return _stanfordGraph;
	}
	
	public String getSentence(){
		return _orginalSentence;
	}
	
	
	@JsonIgnore
	public String getSentence(WordType wt, Collection<String> stopWords) {
		String result = "";
		
		for (WordVertex wv: this.getSentenceAsList()) {
			if (stopWords.contains(wv.getWord(wt))) { continue; }
			result = result + wv.getWord(wt) +" ";
		}
		
		return result.trim();
	}
	
	public String getParserSentence() {
		if (_parserSentence == null) {
			return _orginalSentence;
		}
		return _parserSentence;
	}
	
	public boolean isParsedCorrectly() {  return _parsedCorrectly;	}
	public void setParsedCorrectly(boolean newValue) { _parsedCorrectly = newValue; }
	
	@JsonIgnore
	public int getNumberOfNodes() {
		return _numberOfNodes;
	}
	
	@JsonIgnore
	public int getClusterID() {
		return _clusterID;
	}

	@JsonIgnore
	public void setClusterID(int newValue) {
		_clusterID = newValue;
	}
	

	
	
	public HashMap<String, ClassificationType> getClassifications() {
		return _classifications;
	}
	
	public void setClassifications(HashMap<String, ClassificationType> classifications) {
		_classifications = classifications;
	}
	
	public void setBooleanClassifications(HashMap<String, ClassificationType> classifications) {
		_classifications = classifications;
	}	
	
	/**
	 * This function is called when a JSON file is read, to convert the ClassificationType to the proper subclass.
	 */
	public void fixClassificationsFromLoad() {
		ClassificationAttributeTableModel catm = GCController.getTheGCController().getClassificationAttributes();
		
		for (ClassificationAttribute ca: catm.getAttributeList()) {
			String name = ca.getName();
			String type = ca.getType();
			
			if (_classifications.containsKey(name)== false) {
				if (type.equalsIgnoreCase("boolean")) {
					_classifications.put(name, new BooleanClassification(BooleanType.FALSE, Source.DEFAULT));
				}
				else {
					_classifications.put(name, new StringClassification(""));
				}
			}
			else {
				String currentValue = _classifications.get(name).getValue().toString();
				if (type.equalsIgnoreCase("boolean")) {
					_classifications.put(name, new BooleanClassification(BooleanType.getBooleanType(currentValue), Source.DEFAULT));
				}
				else {
					_classifications.put(name, new StringClassification(currentValue));
				}				
			}
			
		}
		
	}
	
	
	/**
	 * Returns true if any of the boolean classifications has been set to true. Otherwise false
	 */
	@JsonIgnore
	public boolean hasBooleanClassifications() {
		boolean foundClassification = false;
		
		for (String key: _classifications.keySet()) {
			ClassificationType ct = _classifications.get(key);
			if (ct instanceof BooleanClassification && ((BooleanClassification)ct).getValue().getBooleanValue()) {
				foundClassification = true;
				break;
			}
		}
		return foundClassification;
	}

	/**
	 * Returns true if the sentence has the given classfication;
	 */
	@JsonIgnore
	public boolean hasBooleanClassification(String clasificationToCheck) {
		ClassificationType ct = _classifications.get(clasificationToCheck);
		return (ct != null && ct instanceof BooleanClassification && ((BooleanClassification)ct).getValue().getBooleanValue());
	}
	
	
	/**
	 * Returns a string array of the boolean classifications set to true.
	 */
	@JsonIgnore
	public String[] getBooleanClassificationsAsStringArray() {
		ArrayList<String> tempResults = new ArrayList<String>();
		
		for (String key: _classifications.keySet()) {
			ClassificationType ct = _classifications.get(key);
			if (ct instanceof  BooleanClassification) {
				BooleanClassification bc = (BooleanClassification) ct;
				if (bc.getValue().getBooleanValue()) {
					tempResults.add(key);
				}
			}
		}
		return tempResults.toArray(new String[tempResults.size()]);
	}
	
	
	
	
	/**
	 * Returns a string array of the boolean classifications set to true.
	 */
	@JsonIgnore
	public String getAllBooleanClassificationsAsString() {
		String result = "";
		
		for (ClassificationAttribute ca: GCController.getTheGCController().getClassificationAttributes().getAttributeList()) {
			ClassificationType ct = _classifications.get(ca.getName());
			if (ct instanceof  BooleanClassification) {
				BooleanClassification bc = (BooleanClassification) ct;
				try {
					if (bc.getValue().getBooleanValue()) {
						if (result.length()>0) { result += " "; }
						result += ca.getName();
					}
				}
				catch (Exception e) {
					System.out.println(this);
					System.out.println(bc);
					System.out.println("**************************************************");
				}
			}
		}
		return result;
	}
	
	/**
	 * Returns a string array of the boolean classifications set to true.
	 */
	@JsonIgnore
	public String getAllBooleanClassificationsAsAbbreviationString() {
		String result = "";
		
		for (ClassificationAttribute ca: GCController.getTheGCController().getClassificationAttributes().getAttributeList()) {
			ClassificationType ct = _classifications.get(ca.getName());
			if (ct != null && ct instanceof  BooleanClassification) {
				BooleanClassification bc = (BooleanClassification) ct;
				if (bc.getValue().getBooleanValue()) {
					if (result.length()>0) { result += ","; }
					result += ca.getAbbreviation();
				}
			}
		}
		
		if (this.isTrained()) {
			result += "+";
		}
		
		return result;
	}
	

	public boolean isTrained() {
		return _trained;
	}
	
	public void setTrained(boolean newValue) {
		_trained = newValue;
		NLPEventManager.getTheEventManager().sendEvent(NLPEventType.SENTENCE_DATA_CHANGE, new NLPEventSentenceDataEvent(this, "trained"));
	}
		
	public void moveSentenceToInstanceLearner() {
		GCController.getTheGCController().getInstanceLearner().addTrainedSentence(this);
	}

	@JsonIgnore
	public ArrayList<WordVertex> getSentenceAsList() {
		ArrayList<WordVertex> result = new ArrayList<WordVertex>();
		for (WordVertex word: _wordList) {
			result.add(word);
		}
		return result;
	}
	
	@JsonIgnore
	public WordVertex getWordVertexAt(int i) {
		return _wordList[i];
	}
	
	@JsonIgnore
	public WordVertex getWordVertexBySortedPosition(int i) {
		return _sortedByPositionWordList[i];
	}	
	
	public boolean hasLemma(String lemma) {
		for (WordVertex wc: _wordList) {
			if (wc.getLemma().equals(lemma)) { return true;}
		}
		return false;
	}
	
	public String getComments() {
		if (_comments == null) { _comments = "";}
		return _comments;
	}
	public void setComments(String newValue) {
		_comments = newValue;
	}	
	
	public String getSource() { return _source; }
	public void setSource(String newValue) { _source= newValue; } 
	

	
	public String toString() {
		return _orginalSentence;
	}
	
	public boolean containsReadVerb() {
		for (WordVertex wv: _wordList) {
			if (MiscCheck.isReadVerb(wv.getLemma())) { return true; }
		}
		return false;
	}
	
	public boolean containsWriteVerb() {
		for (WordVertex wv: _wordList) {
			if (MiscCheck.isWriteVerb(wv.getLemma())) { return true; }
		}
		return false;
	}	
	
	public void checkForNewAbbreviations(NLDocument doc) {
		java.util.Set<WordVertex> abbreviationNodes = _root.getAllAbbreviationNodes();
		
		if (abbreviationNodes.size() == 0) {
			System.out.println("   none found");
		}
		else {
			String sentenceToCheck = _orginalSentence.toLowerCase();  //declared up here because this is an invariant;
			for (WordVertex wv: abbreviationNodes) {
				System.out.print("   Proccesing "+wv.getLemma());
				String possibleAbbr = wv.getLemma().toUpperCase();
				String matchedWord = Abbreviation.checkAbbreviation(sentenceToCheck,wv);
				
				if (matchedWord != null) { // found a good abbreviation
					System.out.println(" - matched:"+matchedWord);
					
					if (doc.isAbbreviationDefined(possibleAbbr)) {
						String currentDefinition = doc.getTextForAbbrevation(possibleAbbr);
						if (currentDefinition.equals(matchedWord) == false ) {
							System.out.println(" - Error: mulitple definitions found for "+possibleAbbr+": already defined - \""+currentDefinition+"\", new - \""+matchedWord+"\"");
						}
					}
					else {
						doc.addAbbreviation(possibleAbbr, matchedWord);
						System.out.println("           added abbreviation:"+matchedWord);
					
					}
				}	
				else {
					System.out.println(" - not an abbreviation: "+possibleAbbr);
				}
			}
			
		}		
		
	}
	
	
	/**
	 * This method walks the sentence looking for only/just.  As the positioning of only/just
	 * affects which phrase/noun/verb has the focus of this limiter, the following rules are applied
	 * Active: must come before both the noun and verb (collapsed in the sentence)
	 * Passive: must come after the start of the verb.  (verb phrase doesn't have to be complete
	 * 
	 */
	public void checkForOnlyRoles() {
		/*
		if (this.getAccessControlDecision().getOnlyFlag() != BooleanType.UNKNOWN) { //flag has already been set by either the program or a user, don't re-processs
			return;
		}
		*/
		boolean foundNoun = false;
		boolean foundVerb = false;

		if (Voice.inPassiveVoice(this)) {
			for (WordVertex w: _sortedByPositionWordList) {
				if (w.getPartOfSpeech().getCollapsedPOS() == PartOfSpeech.VB) { foundVerb = true;}
				else {
					if (foundVerb && (w.getLemma().equals("only") || w.getLemma().equals("just")) ) {
						//this.getAccessControlDecision().setOnlyFlag(BooleanType.TRUE);
						//this.getAccessControlDecision().setOnlyThisRoleSource(Source.PARSER);
						return;
					}
				}
			}
		}
		else {
			for (WordVertex w: _sortedByPositionWordList) {
				if (w.getPartOfSpeech().getCollapsedPOS() == PartOfSpeech.NN) { foundNoun = true;}
				else if (w.getPartOfSpeech().getCollapsedPOS() == PartOfSpeech.VB) { foundVerb = true;}
				else {
					if (w.getLemma().equals("only") || w.getLemma().equals("just")) {
						if (foundNoun == false && foundVerb == false) {
							//this.getAccessControlDecision().setOnlyFlag(BooleanType.TRUE);
							//this.getAccessControlDecision().setOnlyThisRoleSource(Source.PARSER);
							return;
						}
					}
				}
			}
		}
	}
	
	public void checkSentenceType() {
		if (this.getSentenceType() != SentenceType.UNKNOWN) { return; }
		if (MiscCheck.isTitle(this.getSentence())) {
			this.setSentenceType(SentenceType.TITLE);
			System.out.println("\tmarked sentence as title");
		}	
	}
	
	
	public void processSentence(NLDocument doc) {
		System.out.println(this.getRoot().getStringRepresentationUltraCollapsed());
		
		System.out.println("Evaluating: "+_orginalSentence);
		System.out.println("\tText sent to parser: "+_parserSentence);
		System.out.println("\tSentence Type: "+_sentenceType);
		System.out.println("\t1. Top of process sentence");
		System.out.println("\t2. Checking for new abbrevations");
		checkForNewAbbreviations(doc);
		System.out.println("3. Check if an unknown sentence type is a title");
		checkSentenceType();
		
		System.out.println("bottom of process sentence - go use the classifier to get answers");

		
		if (!_processed && (!this.isTrained() || GCController.getTheGCController().isSupervisedLearning())) { 
			ClassificationResult r = GCController.getTheGCController().getInstanceLearner().getClassification(this,GCController.getTheGCController().getKForInstanceLearner(), new LevenshteinSentenceAsWordsDistance(),true);
			
				
			//System.out.println(r.k+": "+r);
			
			//Let's add a threshold on this
			if (r.averageDistance > (this.getNumberOfNodes()*.85)) {
				System.out.println("Not using results of IBL - avg distance > .85 * number of nodes");
			}
			else {
				if (!this.isTrained()) {
					this.setClassifications(r.classifications);
				}

				_processed = false;
			
			}
		}
		
	}	
	
	public void processTrainedSentence() {
		if (!this.isTrained()) {
			this.moveSentenceToInstanceLearner();
			this.setTrained(true);	
		}
	}
	
	public void unTrainSentence() {
		//_accessControlDefined = false;
		this.setTrained(false);
	}
	
	/**
	 * Have gone back to straight equality
	 * Sentences are considered equal when they have the same # of nodes, and each node in position is equal.
	 */
	public boolean equals(Object o) {
		return super.equals(o);
		/*
		if (o instanceof Sentence == false) {
			return false;
		}
		Sentence s = (Sentence) o;
		
		if (s._wordList.length != this._wordList.length) {
			return false;
		}
		
		for (int i=0; i< this._wordList.length;i++) {
			if (this._wordList[i].equals(s._wordList[i]) == false) {
				return false;
			}
		}
		
		return true;
		*/
	}
	
	/**
	 * Converts a string of comma separated numbers that represent wordvertex IDs into an array of WordVertices
	 * 
	 * @param string which is comma separated list of word vertex IDs
	 * @return array of wordVertices
	 * @throws exception if the word vertex is not a number or not a valid word vertex ID, or if ID is specified multiple times
	 */
	public ArrayList<WordVertex> generateWordVertexListFromString(String s) throws IndexOutOfBoundsException, NumberFormatException, IllegalArgumentException {
		ArrayList<WordVertex> result = new ArrayList<WordVertex>();	
	
		String ids[] = s.split(",");
		for (String id: ids) {
			try {
				if (id.length() == 0) { continue; } // If there was no vertex defined, it's blank and we just need to return it.
				int i= Integer.parseInt(id);
				if (i<1 || i> _wordList.length) {
					throw new IndexOutOfBoundsException("Invalid vertex ID, out of bounds: "+id);
				}
				if (result.contains(_wordList[i-1])) {
					throw new IllegalArgumentException("Same vertex ID specified multiple times: "+id);
				}
				result.add(_wordList[i-1]);
			}
			catch (NumberFormatException nfe) {
				throw new NumberFormatException("Invalid vertex ID: "+id);
			}
		}
		
		return result;
	}
		
	
	/**
	 * Converts a word list (like subject/action/resource into the actual list of words affected.
	 * 
	 * @param list
	 * @return
	 */
	public ArrayList<String> generateWordList(String list) {
		ArrayList<String> result = new ArrayList<String>();
		if (list.trim().equals("") == false) {
			String[] items = list.split(":");
			for (String s: items) {
				String currentWord;
				
				String[] nodes = s.split(",");
				if (nodes.length == 1) {
					int id = Integer.parseInt(nodes[0]);
					currentWord = _wordList[id-1].getLemma();
				}
				else {
					currentWord="";
					for (int i=0; i<nodes.length; i++) {
						int id = Integer.parseInt(nodes[i]);
						if (i>0) {
							currentWord += " ";
						}
						currentWord += _wordList[id-1].getLemma();
					}
				}
				result.add(currentWord);
			}
		}
		
		return result;
	}
	
	private HashMap<String,Integer> _frequencyMap = null;
	private HashMap<String,Double>  _frequencyMapNormalized = null; // divided by the length so that its entries sum to one
	private double                  _frequencyLength = Double.MAX_VALUE;  //used as an indicator that it isn't defined.
	private double                  _frequencyLengthIDF = Double.MAX_VALUE;  //used as an indicator that it isn't defined. 
	private HashMap<String,Double>  _lastInverseDocumentFrequencySeen = null;
	
	/**
	 * 
	 * @return
	 */
	@JsonIgnore
	public HashMap<String,Integer> getFrequencyMap() {
		if (_frequencyMap == null) { 
			HashMap<String, Integer> result = new HashMap<String, Integer>();
			
			int numWords = this.getNumberOfNodes();
			for (int i=0;i<numWords;i++) {
				WordVertex wv = this.getWordVertexAt(i);
				//String key = wv.getOriginalWord().toLowerCase();
				String key = wv.getLemma().toLowerCase();
					
				int count = result.containsKey(key) ? result.get(key) : 0;
				result.put(key, count + 1);
			}		
			_frequencyMap = result;
		}
		return _frequencyMap;
	}
	
	/**
	 * 
	 * @return
	 */
	@JsonIgnore
	public HashMap<String,Double> getFrequencyMapNormalized() {
		if (_frequencyMapNormalized == null) { 
			HashMap<String, Double> result = new HashMap<String, Double>();
			
			int numWords = this.getNumberOfNodes();
			double normalizedAddOne = 1.0 / numWords;
			for (int i=0;i<numWords;i++) {
				WordVertex wv = this.getWordVertexAt(i);
				//String key = wv.getOriginalWord().toLowerCase();
				String key = wv.getLemma().toLowerCase();
					
				double count = result.containsKey(key) ? result.get(key) : 0.0;
				result.put(key, count + normalizedAddOne);
			}		
			_frequencyMapNormalized = result;
		}
		return _frequencyMapNormalized;
	}	
	
	/**
	 * This computes the vector length that would be used in cosine distance calculation.
	 * See bottom of p769 in Speech and Natural Language Processing (or any other multitude of sources)
	 * 
	 * @return
	 */
	@JsonIgnore
	public double computeFrequencyVectorLength() {
		if (_frequencyLength == Double.MAX_VALUE) {
			int sum = 0;
			
			for (int value: this.getFrequencyMap().values()) {
				sum += (value*value);
			}
			_frequencyLength = Math.sqrt(sum);
		}
		return _frequencyLength;
	}

	
	/**
	 * This computes the vector length that would be used in cosine distance calculation,
	 * but weighting in regards to the inverseDocumentFrequency IDF
	 * See bottom of p771 in Speech and Natural Language Processing (or any other multitude of sources)
	 * 
	 * @return
	 */
	@JsonIgnore
	public double computeFrequencyVectorLength(HashMap<String, Double> inverseDocumentFrequency, double defaultIDFValue) {
		if (_frequencyLengthIDF == Double.MAX_VALUE || _lastInverseDocumentFrequencySeen != inverseDocumentFrequency) {
			double sum = 0.0;
				
			HashMap<String,Double> freqMap = this.getFrequencyMapNormalized();
			
			//StopWord sw = new StopWord();
			
			
			for (String key: freqMap.keySet()) {
				//if (Utility.contains(sw.frakes, key)) { continue; }
				
				double tf = freqMap.get(key);
				double idf = inverseDocumentFrequency.containsKey(key) ? inverseDocumentFrequency.get(key) : defaultIDFValue;
				sum += (tf *idf * tf *idf);  //same as (tf*idf)^2
			}
			
			_frequencyLengthIDF =  Math.sqrt(sum);
			_lastInverseDocumentFrequencySeen = inverseDocumentFrequency;
		}
		return _frequencyLengthIDF;
	}


	@Override
	public int compareTo(Sentence o) {
		return this.getSentence().compareTo(o.getSentence());
	}	
		
	
	/**
	 * returns a list of all of the verbs present in the sentence
	 * @return
	 */
	public List<String> retrieveAllVerbs() {
		ArrayList<String> tempResults = new ArrayList<String>();
		
		for (WordVertex wv: _wordList) {
			if (wv.getPartOfSpeech().getCollapsedPOS() == PartOfSpeech.VB) {
				tempResults.add(wv.getLemma());
			}
		}
		
		return tempResults;
	}

	/**
	 * returns a list of all of the nouns in the sentences.
	 * 
	 * Tried to make this be just resources, but it's really not feasible.  imperative type sentences were parsed with the direct object as the nominative subject
	 * 
	 */

	public List<String> retrieveAllNouns() {
		ArrayList<String> tempResults = new ArrayList<String>();
		
		//boolean passive = Voice.inPassiveVoice(this);
		
		for (WordVertex wv: _wordList) {
			if (wv.getPartOfSpeech().getCollapsedPOS() == PartOfSpeech.NN) {
				tempResults.add(wv.getLemma());
			}
		}
		
		return tempResults;
	}
	
	@JsonIgnore
	public String getAmbiguityFlag() {
		
		return "-";
	}
	
}


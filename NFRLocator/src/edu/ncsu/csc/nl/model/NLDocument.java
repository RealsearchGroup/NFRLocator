package edu.ncsu.csc.nl.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ncsu.csc.nl.GCConstants;
import edu.ncsu.csc.nl.GCController;
import edu.ncsu.csc.nl.model.classification.BooleanClassification;
import edu.ncsu.csc.nl.model.classification.ClassificationAttribute;
import edu.ncsu.csc.nl.model.classification.ClassificationType;
import edu.ncsu.csc.nl.model.english.MiscCheck;
import edu.ncsu.csc.nl.model.english.Modal;
import edu.ncsu.csc.nl.model.ml.ConfusionMatrix;
import edu.ncsu.csc.nl.model.ml.SentenceCluster;

import edu.ncsu.csc.nl.model.type.ListIDType;
import edu.ncsu.csc.nl.model.type.PartOfSpeech;

import edu.ncsu.csc.nl.model.type.SentenceType;
import edu.ncsu.csc.nl.util.Utility;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;




/**
 * Represents a container around a natural language document.
 * 
 * @author John
 *
 */
public class NLDocument implements Serializable, javax.swing.table.TableModel {

	public static final long serialVersionUID = 1;
	
	public static final int UNITIALIZED = -1;
	
	public static final	TextProcessor TEXT_PROCESSOR = new TextProcessor();
	
	private String _fileLocation;
	private String _fileType; //Not yet used.  Placeholder in case we want to detect use cases/design doc/test script/requirements/etc
	private ArrayList<Sentence> _sentences =  new ArrayList<Sentence>();
	
	private HashMap<String, String> _abbreviations = new HashMap<String,String>();
	
	private static final int PARSE_STATE_NORMAL = 1;
	private static final int PARSE_STATE_LIST   = 2;
	
	// if these are changed, will also need to update corresponding code in readFromSerialFile, true for any transient objects
	private transient Stack<ListIDType> _parselistIDStack = new Stack<ListIDType>();
	private transient Stack<String> _parseListParentSentenceStack = new Stack<String>();
	private transient int _parseState = PARSE_STATE_NORMAL;
	private transient boolean _parseFoundStartOfList = false;
	
	private transient TableModelListener _listener;
	
	public NLDocument() {
		
	}
	
	public void setFileLocation(String newLocation) {
		_fileLocation = newLocation;
	}
	
	public String getFileLocation() {
		return _fileLocation;
	}
	
	public void setFileType(String type) {
		_fileType = type;
	}
	
	public String getFileType() {
		return _fileType;
	}
	
	
	public void setContent(Sentence[] sentences) {
		for (Sentence s: sentences) {
			_sentences.add(s);
		}
	}

	public Sentence[] getContent() {
		return _sentences.toArray(new Sentence[0]);
	}
	
	public HashMap<String, String> getAllAbbreviations() {
	    return _abbreviations;
	}

	public void setAllAbbreviations(HashMap<String, String> newAbbreviations) {
	    _abbreviations =newAbbreviations;
	}

	
	
	@JsonIgnore
	public int getNumberOfSentences() {
		if (_sentences == null) {
			return UNITIALIZED;
		}
		else {
			return _sentences.size();
		}
	}
	
	@JsonIgnore
	public int getSentencePosition(Sentence s) {
		return _sentences.indexOf(s);
	}
	
	@JsonIgnore
	public int getNumberOfAbbrevations() {
		return _abbreviations.size();
	}
	
	@JsonIgnore
	public Set<String> getAbbreviationSet() {
		return _abbreviations.keySet();
	}
	
	public boolean isAbbreviationDefined(String abbreviation) {
		return _abbreviations.containsKey(abbreviation);
	}
	
	public String getTextForAbbrevation(String abbreviation) {
		return _abbreviations.get(abbreviation);
	}

	public void addAbbreviation(String abbreviation, String text) {
		_abbreviations.put(abbreviation, text);
	}
	
	/** Renumbers sentences based upon their current position */
	public void normalizeOriginalSentencePostionFromCurrentOrder() {
		double position=0.0;
		
		for (Sentence s: _sentences) {
			s.setOriginalSentencePosition(position);
			position++;
		}
	}

	public void addSentence(Sentence s) {
		_sentences.add(s);

		sendTableChangedEvent(new TableModelEvent(this,  Math.max(0, _sentences.size()-2), _sentences.size()-1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT)); 		
	}	
	
	public void addSentences(List<Sentence> sentences) {
		_sentences.addAll(sentences);
		
		sendTableChangedEvent(new TableModelEvent(this,  Math.max(0, _sentences.size()-1-sentences.size()), _sentences.size()-1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT)); 		
	}
	
	@JsonIgnore
	public void setClusterIDs(ArrayList<SentenceCluster> clusters) {
		int id = 0;
		HashSet<Sentence> set = new HashSet<Sentence>();
		for (SentenceCluster sc: clusters) {
			id++;
			for (Sentence s:  sc.getMembers()) {   s.setClusterID(id); 	set.add(s);		}
		}
		sendTableChangedEvent(new TableModelEvent(this));
	}
	
	/**
	 * Opens the set file, reads all of the lines at once.
	 * Then for each lines, runs the annotation on it and creates the applicable sentence objects
	 * 
	 * @param pipeline
	 */
	public void loadAndParse(edu.stanford.nlp.pipeline.StanfordCoreNLP pipeline) {
		_sentences = new ArrayList<Sentence>();
		_lastIDParsed = ' ';
		
		List<String> fileContents;
		try {
			fileContents = Utility.readLines(this.getFileLocation());
		}
		catch (Exception e) {
			System.out.println("Unable to parse file: "+e);
			e.printStackTrace();
			return;
		}
		
		double lineCount = 0.0;
		boolean inMultiLine = false;
		for (String originalLine: fileContents) {
			System.out.println("Processing: "+originalLine);
			originalLine = originalLine.trim();
			lineCount++;
			
			java.util.List<Sentence> createdSentences = this.parseSentence(pipeline, originalLine);
		    
		    boolean needToStartMultiLine = false;
		    if (!inMultiLine && originalLine.startsWith("\"")) {
		    	System.out.println("\tStarting a group");
		    	inMultiLine = true;
		    	needToStartMultiLine = true;  // don't set the first element/sentence to being related to the previous sentence
		    }
		    

		    for (Sentence s: createdSentences) {
		    	s.setOriginalSentencePosition(lineCount);
		    	if (needToStartMultiLine == true) {
		    		needToStartMultiLine = false;
		    	}
		    	else {
		    		if (inMultiLine) {
		    			s.setRelatedToPrevious(true);
		    	        System.out.println("\tSMarking sentence related to the previous");
		    	    }
		    	}
		    	    
		    	_sentences.add(s);
		    }
		    if (inMultiLine && originalLine.endsWith("\"")) {
		    	inMultiLine = false;
		    	System.out.println("\tEnding a group");
		    }
		    
		}
	}
	
	//private static Annotation mostRecentAnnotatedDocument = null; //WARNING! This is not thead-safe now
	
	//Using the output of the Stanford coreference system (resolution/related words),
	//add the sets of related words to a sentence
	/*
    Map<Integer, CorefChain> graph =  mostRecentAnnotatedDocument.get(CorefChainAnnotation.class);
    ArrayList<Integer> keylist = new ArrayList<Integer>(graph.keySet());     
    java.util.Collections.sort(keylist);
    for (Integer key:keylist) {
    	CorefChain c = graph.get(key);
    	List<CorefMention> mentions = c.getCorefMentions();
    	if (mentions.size() > 1) {
    		Sentence.RelatedWordGroup rwg = new Sentence.RelatedWordGroup();	    		
    		for (CorefMention cm: mentions) {
    			int sentenceNumber = cm.sentNum-1;
    			while (sentenceNumber >= createdSentences.size()) { sentenceNumber--;}  //this is needed as the coreref processor can return bad sentence number results.
    			Sentence relatedSentence = createdSentences.get(sentenceNumber);
    			for (int i=cm.startIndex;i<cm.endIndex;i++) {
    				WordVertex v = relatedSentence.getRoot().getVertexByWordPosition(i);
    				if (v != null) {
    					rwg._relatedWords.add(new Sentence.RelatedWordItem(relatedSentence,v));
    				}
    			}		
    		}	
    		
    		int sentenceNumber=c.getRepresentativeMention().sentNum-1;
    		while (sentenceNumber >= createdSentences.size()) { sentenceNumber--;}  //this is needed as the coreref processor can return bad sentence number results.
    		createdSentences.get(sentenceNumber).addResolutionGroup(rwg);
    	}
    }
	*/
	
	private char _lastIDParsed = ' ';
	public java.util.List<Sentence> parseSentence(edu.stanford.nlp.pipeline.StanfordCoreNLP pipeline, String line) {
		java.util.ArrayList<Sentence> createdSentences = new java.util.ArrayList<Sentence>();
		line = line.trim();
		line = TEXT_PROCESSOR.preProcessLine(line);
		Annotation initialDocument = new Annotation(line);
		
		// run all Annotators on this text
		pipeline.annotate(initialDocument);
		
		//mostRecentAnnotatedDocument = document;
		    
		// these are all the sentences in this document
		java.util.List<CoreMap> initialSentences = initialDocument.get(SentencesAnnotation.class); 		    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		
		for(CoreMap initialSentence: initialSentences) {    	
		   	SemanticGraph initialDependencies = initialSentence.get(CollapsedCCProcessedDependenciesAnnotation.class);   // this is the Stanford dependency graph of the current sentence
		   	if (initialDependencies.getRoots().size() == 0) {  //if there were not any roots defined, just skip this sentence
		   		continue;
		   	}
		   	try {
		    	String initialText = initialSentence.get(TextAnnotation.class); // this is the pre-processed text for this sentence.
		    	String parserText  = initialText;
		    	SemanticGraph initialSentenceDependencies = initialSentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
		    	if (initialSentenceDependencies.getRoots().size() != 0) {  //if there were not any roots defined, skip.  should never happen.
		    		// Create an initial sentence object such that we can check for modal verbs and place any modifiers that occur between a 
		    		// modal verb and a verb.  If so, swap the modify and the verb
			   		
			   	
		    		WordVertex v = WordVertex.create(initialSentenceDependencies, false);
		    		v.collapseNounPhrases(parserText);
			    
			   		Sentence s = new Sentence(v, initialSentenceDependencies, initialText);
			   		//parserText = Modal.checkModalVerbForModifier(s);
		   		}
		    	
		    	
		    	String identifier = "";
		    	SentenceType sentenceType = SentenceType.NORMAL;
		    	char idStart = ' ';
		    	
		    	if (_parseState == NLDocument.PARSE_STATE_LIST) {
			    	if (MiscCheck.hasLineIdentifier(parserText)) {
			    		sentenceType = SentenceType.LIST_MEMBER;
			    		identifier = MiscCheck.extractLineID(parserText);
			    					    		
			    		ListIDType lit;
			    		if (identifier.charAt(0)=='(') {
			    			lit = ListIDType.getListIDType(identifier.charAt(1));
			    			idStart = identifier.charAt(1);
			    		}
			    		else {
			    			lit = ListIDType.getListIDType(identifier.charAt(0));
			    			idStart = identifier.charAt(0);
			    		}
			    		
			    		if (_parseFoundStartOfList) {
			    			_parselistIDStack.add(lit);
			    			_parseFoundStartOfList = false;
			    		}
			    		else {
			    			if (!_parselistIDStack.empty()) {
			    				ListIDType topType = _parselistIDStack.peek();
			    				if (!(lit == ListIDType.LOWER_ROMAN && idStart == 'i' && _lastIDParsed == 'h') && (topType != lit) ){
			    					_parselistIDStack.pop();
			    					_parseListParentSentenceStack.pop();
			    				}	
			    			}
			    		}
			    		parserText = MiscCheck.extractLineWithoutID(parserText);
			    		
			    		if (MiscCheck.startsList(parserText)) {
			    			_parseListParentSentenceStack.add(parserText);
			    			_parseFoundStartOfList = true;
			    			// Since this guy is an element of a list, give precent to list element of list start
			    		}

			    		if (!_parseListParentSentenceStack.isEmpty()) {
			    			String prefixString = "";
			    			for (String s: _parseListParentSentenceStack) {
			    				if (prefixString.length()>0) { prefixString += " "; }
			    				prefixString += s;
			    			}
			    						    			
			    			parserText = prefixString + " " + parserText;
			    		}
			    		
			    	}
			    	else { // no line identifier found.
			    		if (MiscCheck.startsList(parserText)) {
			    			_parseState = NLDocument.PARSE_STATE_LIST;
			    			_parseListParentSentenceStack.add(parserText);
			    			_parseFoundStartOfList = true;
			    			sentenceType = SentenceType.LIST_START;
			    		}
			    		else {  // Move to the normal state. clear all list elements
			    			_parseState = NLDocument.PARSE_STATE_NORMAL;
			    			_parselistIDStack.removeAllElements();
			    			_parseListParentSentenceStack.removeAllElements();
			    			_parseFoundStartOfList = false;
			    			
			    			if (MiscCheck.isTitle(parserText)) {
			    				sentenceType = SentenceType.TITLE;
			    			}
			    			if (MiscCheck.isCFRTitle(parserText)) {
			    				sentenceType = SentenceType.TITLE;
			    				parserText = MiscCheck.extractCFRTitle(parserText);
			    			}
			    		}			    		
			    	}
		    	}
		    	else {  // we are in the "normal" state.
		    		if (MiscCheck.hasLineIdentifier(parserText)) {
		    			identifier = MiscCheck.extractLineID(parserText);
		    			parserText = MiscCheck.extractLineWithoutID(parserText);
		    		}
		    		
		    		if (MiscCheck.startsList(parserText)) {
		    			_parseState = NLDocument.PARSE_STATE_LIST;
		    			_parseListParentSentenceStack.add(parserText);
		    			_parseFoundStartOfList = true;
		    			sentenceType = SentenceType.LIST_START;
		    		}
		    		else {  // staying in the normal state.  As a precaution make sure the list stuff is cleared out.
		    			_parselistIDStack.removeAllElements();
		    			_parseListParentSentenceStack.removeAllElements();
		    			_parseFoundStartOfList = false;
		    			
		    			if (MiscCheck.isTitle(parserText)) {
		    				sentenceType = SentenceType.TITLE;
		    			}
		    			if (MiscCheck.isCFRTitle(parserText)) {
		    				sentenceType = SentenceType.TITLE;
		    				identifier = MiscCheck.extractCFRid(parserText);
		    				parserText = MiscCheck.extractCFRTitle(parserText);
		    			}
		    		}
		    	}
		    	_lastIDParsed = idStart;
		    	
		    	
		    	    	
		    	// As the stanford parse can break a line into multiple sentences, we'll run through the parser again.
		    	Annotation document = new Annotation(parserText);
				pipeline.annotate(document);
				java.util.List<CoreMap> sentences = document.get(SentencesAnnotation.class); 
				for(CoreMap sentence: sentences) {    	
				   	SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);   // this is the Stanford dependency graph of the current sentence
				   	if (dependencies.getRoots().size() == 0) {  //if there were not any roots defined, just skip this sentence
				   		continue;
				   	}
				   	//String text = sentence.get(TextAnnotation.class); // this is the pre-processed text for this sentence.
				   	initialText = TEXT_PROCESSOR.postProcessLine(initialText); 
				   	parserText  = TEXT_PROCESSOR.postProcessLine(parserText); 
				   	
				   	WordVertex v = WordVertex.create(dependencies);
				    v.collapseNounPhrases(parserText);
				    
				   	Sentence s = new Sentence(v, dependencies, initialText);
				   	s.setParserSentence(parserText);
				   	s.setSentenceType(sentenceType);
				   	s.setDocumentID(GCController.getTheGCController().getCurrentDocumentID());
				   	s.setSentenceID(identifier);
				   	
				   
				   	if (s.getRoot().getNumberOfParents() > 0) {
				   		s.getRoot().removeAllParents();
				   	}
				   	
				   	createdSentences.add(s);
				}
		   	}
		   	catch (IllegalArgumentException e) {
		   		System.out.println(e);
	    	}
		}		
		
		return createdSentences;
	}

	public void addSentenceAndParse(edu.stanford.nlp.pipeline.StanfordCoreNLP pipeline, String line) {
		_lastIDParsed = ' ';
		java.util.List<Sentence> createdSentences = this.parseSentence(pipeline,line);

		for (Sentence s: createdSentences) {
			s.setOriginalSentencePosition(_sentences.size()+1);
			_sentences.add(s);
		}

		if (_listener != null) { 
			sendTableChangedEvent(new TableModelEvent(this)); //,  Math.max(0, _sentences.size()-1-createdSentences.size()), _sentences.size()-1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT)); 
		}
	}	
	
	public void removeElementAt(int index) {
		_sentences.remove(index);
		sendTableChangedEvent(new TableModelEvent(this, index, index, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE)); 
	}
	
	@JsonIgnore
	public ArrayList<Sentence> getSentences() {  // probably not the best of design to expose the list
		return _sentences;
	}
	
	/**
	 * Returns the sentence who has the value of the original position ID.  If not found, returns null
	 * 
	 * @param id what is the position of the sentence to be found?
	 * @return
	 */
	@JsonIgnore
	public Sentence getSentenceByOriginalPosition(double position) {
		for (Sentence s: _sentences) {
			if (s.getOriginalSentencePosition() == position) {
				return s;
			}
		}
		return null;
	}
		
	
	public void writeToJSONFile(java.io.File file) throws IOException, FileNotFoundException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.writerWithDefaultPrettyPrinter().writeValue(file, this);  // this uses UTF-8
	}
	
	public static NLDocument readFromJSONFile(java.io.File file) throws IOException, FileNotFoundException, ClassNotFoundException {
		//String fileName = file.getName();
		
		StanfordCoreNLP pipeline = GCController.getTheGCController().getPipeline();
		
		String input = new String(Files.readAllBytes(file.toPath()),Charset.forName("UTF-8"));   // if not specified, uses windows-1552
		ObjectMapper mapper = new ObjectMapper();
			
		NLDocument result = (NLDocument) mapper.readValue(input, NLDocument.class);
			
		Sentence[] sentencesToLoad = result.getContent();
		result._sentences =  new ArrayList<Sentence>();
	
		for (int i=0; i< sentencesToLoad.length;i++) {
			String message = "Processing "+(i+1)+ " of " +sentencesToLoad.length +" sentences";
			 //GCController.getTheRBACController().setStatusMessage(message);
			
			 Sentence s = sentencesToLoad[i];
			 s.fixClassificationsFromLoad();
			 
			 System.out.println(message+": "+s.getSentence());
			 
			 double currentSentencePosition = s.getOriginalSentencePosition();
			
			 java.util.List<Sentence> sentences = result.parseSentence(pipeline, s.getSentence());
			 
			 for (Sentence createdSentence: sentences) {
				 //createdSentence.setAccessControlDecision(s.getAccessControlDecision());
				 //createdSentence.setAuditDecision(s.getAuditDecision());
				 createdSentence.setClassifications(s.getClassifications());
				 createdSentence.setTrained(s.isTrained());
				 createdSentence.setComments(s.getComments());
				 createdSentence.setUserAssignedRole(s.getUserAssignedRole());
				 createdSentence.setDocumentID(s.getDocumentID());
				 createdSentence.setOriginalSentencePosition(currentSentencePosition);
				
				 result._sentences.add(createdSentence);
			 }
		 }
    	
    	return result;
    }
	
	
	public static NLDocument readFromJSONFileWithoutParsing(java.io.File file) throws IOException, FileNotFoundException, ClassNotFoundException {		
		String input = new String(Files.readAllBytes(file.toPath()),Charset.forName("UTF-8"));    // if not specified, uses windows-1552
		ObjectMapper mapper = new ObjectMapper();
			
		NLDocument result = (NLDocument) mapper.readValue(input, NLDocument.class);
		
		
		Sentence[] sentencesToLoad = result.getContent();
		//result._sentences =  new ArrayList<Sentence>();

		for (int i=0; i< sentencesToLoad.length;i++) {
			//String message = "Processing "+(i+1)+ " of " +sentencesToLoad.length +" sentences";
			//GCController.getTheRBACController().setStatusMessage(message);

			Sentence s = sentencesToLoad[i];
			s.fixClassificationsFromLoad();

			//System.out.println(message+": "+s.getSentence());
		}

		return result;
    }	
	
	public void writeToSerialFile(java.io.File file) throws IOException, FileNotFoundException {
    	ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file) );
    	output.writeObject(this);    	
    	output.close();
	}
	
	public static NLDocument readFromSerialFile(java.io.File file) throws IOException, FileNotFoundException, ClassNotFoundException {
    	ObjectInputStream input = new ObjectInputStream(new FileInputStream( file ) );
    	 
    	NLDocument result = ( NLDocument ) input.readObject();
    	   	
    	input.close();
    	
    	// Fix Transient objects
    	result._parselistIDStack = new Stack<ListIDType>();
    	result._parseListParentSentenceStack = new Stack<String>();
    	result._parseState = PARSE_STATE_NORMAL;
    	result._parseFoundStartOfList = false;   	
    	
    	return result;
    }	
	
	
	
	 public void addListDataListener(ListDataListener l) {
		 
	 }
	 
	 @JsonIgnore
	 public Sentence getElementAt(int index) {
		 return _sentences.get(index);
	 }
	 
	 /**
	  * Swaps the sentences in positions a and b
	  * @param a
	  * @param b
	  */
	 public void swapSentences(int a, int b) {
		 Sentence temp = _sentences.get(a);
		 _sentences.set(a, _sentences.get(b));
		 _sentences.set(b, temp);
	 }
	
	public void dumpSentences(java.io.PrintStream ps) {
		for (Sentence s:this.getSentences()) {
			ps.println("*******************************************************");
			ps.println(s.getSentence());
			ps.println("---------");
			s.getSemanticGraph().prettyPrint();
			ps.println("---------");
			ps.println(s.getRoot().getStringRepresentation());
			ps.println("---------");
			ps.println(s.getRoot().getStringRepresentationCollapsed());
			ps.println("---------");
			ps.println(s.getRoot().getInstanceRepresentation());			
		}		
	}
	
	/**
	 * Returns a list of all of the subjects(ie, roles) defined within the document.  
	 * Only looks at trained sentences.
	 * 
	 * @return
	 */
	@JsonIgnore
	public ArrayList<String> getAllSubjects() {
		HashSet<String> tempResult= new HashSet<String>();
		
		for (Sentence sentence: _sentences) {
			if (!sentence.isTrained()) { continue; } // only consider sentences that have been trained. 

			//ArrayList<String> subjects = sentence.generateWordList(sentence.getAccessControlDecision().getSubjectNodeList());
			//tempResult.addAll(subjects);
		}
		
		ArrayList<String> result = new ArrayList<String>(tempResult);
		Collections.sort(result);
		return result;
	}
	
	/**
	 * Returns a list of all of the resources(ie, tables / columns) defined within the document.  
	 * Only looks at trained sentences.
	 * 
	 * @return
	 */
	@JsonIgnore
	public ArrayList<String> getAllResources() {
		HashSet<String> tempResult= new HashSet<String>();
		
		for (Sentence sentence: _sentences) {
			if (!sentence.isTrained()) { continue; } // only consider sentences that have been trained. 

			//ArrayList<String> resources = sentence.generateWordList(sentence.getAccessControlDecision().getResourceNodeList());
			//tempResult.addAll(resources);
		}
		
		ArrayList<String> result = new ArrayList<String>(tempResult);
		Collections.sort(result);
		return result;
	}
	
	/**
	 * 
	 */
	public void addAllTrainedSentencesToInstanceLearner() {
		for (Sentence s: _sentences) {
			if (s.isTrained()) {  
				s.moveSentenceToInstanceLearner();
				System.out.println("moving: "+s);
			}
			else {
				System.out.println("Not TRAINED: "+s);
			}
		}
	}
	
	@JsonIgnore
	public int getNumberOfTrainedSentences() {
		int result =0;
		
		for (Sentence s: _sentences) {
			if (s.isTrained()) { result++; }
		}
		return result;
	}
	
	
	public void produceReport() {
		HashMap<String,Integer> classifiedCount = new HashMap<String,Integer>();
		int numberOfTrainedSentences       = 0;
		int numberOfNotApplicableSentences = 0;
		
		double numberOfNodesRemoved = 0;
		
		HashSet<String> nerStrings = new HashSet<String>();
		for (Sentence s: _sentences) {
			for (WordVertex wv: s.getSentenceAsList()) {
				nerStrings.add(wv.getNamedEntityRecognizedLabel());
			}
		}
		System.out.println("===============");
		for (String s: nerStrings) {
			System.out.println(s);
		}
		System.out.println("===============");
	
		for (Sentence s: _sentences) {
			if (s.isTrained()) {
				numberOfTrainedSentences++;
				
				String[] classifications = s.getBooleanClassificationsAsStringArray();
				
				for (String key: classifications) {
					int count = classifiedCount.containsKey(key) ? classifiedCount.get(key) : 0;
					classifiedCount.put(key, count + 1);
				}
				if (classifications.length == 0) {
					numberOfNotApplicableSentences++;
				}
			}
			numberOfNodesRemoved +=   Math.abs(s.getSemanticGraph().size() -  s.getNumberOfNodes());
		}
		
		HashMap<String,Integer> wordFrequences = this.produceWordCount();
		
		System.out.println("Number of sentences:"+ _sentences.size());
		System.out.println("Number of words: "+Utility.sum(wordFrequences));
		System.out.println("Number of distinct words: "+wordFrequences.size());
		System.out.println("Average number of nodes removed: "+(numberOfNodesRemoved/ _sentences.size()));
		System.out.println("Trained count: "+numberOfTrainedSentences);
		System.out.println("Not Applicable count: "+numberOfNotApplicableSentences);
		
		ArrayList<String> al = new ArrayList<String>(classifiedCount.keySet());
		Collections.sort(al);
		
		for (String key:al) {
			System.out.println(key+": "+classifiedCount.get(key));
		}
		
		
		this.produceSentenceLengthReport();
		
		for (Sentence s: _sentences) {
			if (s.isTrained()) {
				System.out.println(s.getAllBooleanClassificationsAsString()+"\t"+s);			
			}
			else {
				System.out.println("UNTRAINED\t"+s);
			}
		}
					
	}
	
	public void printUniqueNouns() {
		HashSet<String> uniqueNouns = new HashSet<String>();
		for (Sentence s: _sentences) {
			int numVertices = s.getNumberOfNodes();
			for (int i=0;i<numVertices;i++) {
				WordVertex wv = s.getWordVertexAt(i);
				if (wv.getPartOfSpeech().equalsCollapsed(PartOfSpeech.NN) && wv.getLemma().indexOf(" ")>-1) {
					uniqueNouns.add(wv.getLemma());
				}
			}
		}
		ArrayList<String> sortedUniqueNouns = new ArrayList<String>(uniqueNouns);
		Collections.sort(sortedUniqueNouns);
		for (String member: sortedUniqueNouns) {
			System.out.println (member);
		}
	}
	

	
	public void sendTableChangedEvent(TableModelEvent tme) {
		if (_listener != null) {
			_listener.tableChanged(tme);
		}
	}
	
	private static class SentenceLengthItem {
		int numberOfSentences = 0;
		int numberOfTrained = 0;
		int numberOfUnTrained = 0;
	}
	
	public void produceSentenceLengthReport() {	
		int minSentenceLength = Integer.MAX_VALUE;
		int maxSentenceLength = Integer.MIN_VALUE;
		
		for (Sentence s: _sentences) {
			if (!s.isTrained()) {continue;}
			int numNodes = s.getNumberOfNodes();
		
			if (numNodes < minSentenceLength) {minSentenceLength = numNodes;}
			if (numNodes > maxSentenceLength) {maxSentenceLength = numNodes; }
		}
		
		SentenceLengthItem[] results = new SentenceLengthItem[maxSentenceLength+1];
		for (Sentence s: _sentences) {
			if (!s.isTrained()) {continue;}
			
			int numNodes = s.getNumberOfNodes();
			SentenceLengthItem currentResult = results[numNodes];
			if (currentResult ==null) {
				currentResult = new SentenceLengthItem();
				results[numNodes] = currentResult;
			}
			currentResult.numberOfSentences++;
			if (s.isTrained()) {currentResult.numberOfTrained++;} else {currentResult.numberOfUnTrained++; }
		}
		
		System.out.println("Sentence Length\tNumber\tNum Trained\tNum UnTrained");
		for (int i=1;i<=maxSentenceLength;i++) {
			if (results[i]==null) {results[i]= new SentenceLengthItem();}
			System.out.print(i+"\t"+results[i].numberOfSentences+"\t");
			System.out.print(results[i].numberOfTrained+"\t");
			System.out.println(results[i].numberOfUnTrained);
		}
	}
	
	
	/**
	 * Generates a hashmap containing all of the words in the in document with how many times each word occurs
	 * 
	 * @return
	 */
	public void countWords(HashMap<String, Integer> words, double startPosition, double endPosition) {

		for (Sentence s: _sentences) {
			if (s.getOriginalSentencePosition() >= startPosition && s.getOriginalSentencePosition() <= endPosition) {
			
				int numWords = s.getNumberOfNodes();
				for (int i=0;i<numWords;i++) {
					WordVertex wv = s.getWordVertexAt(i);
					//String key = wv.getOriginalWord().toLowerCase();
					String key = wv.getLemma().toLowerCase();
					
					if (words.containsKey(key)) {
						int count = words.get(key);
						words.put(key, count + 1);
					}
				}
			}
		}

	}	
	
	/**
	 * Generates a hashmap containing all of the words in the in document with how many times each word occurs
	 * 
	 * @return
	 */
	public HashMap<String, Integer> produceWordCount() {
		HashMap<String, Integer> results = new HashMap<String, Integer>();
		
		for (Sentence s: _sentences) {
			int numWords = s.getNumberOfNodes();
			for (int i=0;i<numWords;i++) {
				WordVertex wv = s.getWordVertexAt(i);
				//String key = wv.getOriginalWord().toLowerCase();
				String key = wv.getLemma().toLowerCase();
				
				int count = results.containsKey(key) ? results.get(key) : 0;
				results.put(key, count + 1);
			}
		}
		
		return results;
	}
	
	public HashMap<String, Integer> produceWordCountForClassification(ClassificationAttribute ca) {
		if (ca != null) {
			HashMap<String, Integer> results = new HashMap<String, Integer>();
			
			for (Sentence s: _sentences) {
				if (!s.hasBooleanClassification(ca.getName())) { continue; }
				
				int numWords = s.getNumberOfNodes();
				for (int i=0;i<numWords;i++) {
					WordVertex wv = s.getWordVertexAt(i);
					//String key = wv.getOriginalWord().toLowerCase();
					String key = wv.getLemma().toLowerCase();
					
					int count = results.containsKey(key) ? results.get(key) : 0;
					results.put(key, count + 1);
				}
			}
			
			return results;
		}
		else {  // if null was passed, assume we need to count NA
			HashMap<String, Integer> results = new HashMap<String, Integer>();
			
			for (Sentence s: _sentences) {
				if (s.hasBooleanClassifications()) { continue;}
				
				int numWords = s.getNumberOfNodes();
				for (int i=0;i<numWords;i++) {
					WordVertex wv = s.getWordVertexAt(i);
					//String key = wv.getOriginalWord().toLowerCase();
					String key = wv.getLemma().toLowerCase();
					
					int count = results.containsKey(key) ? results.get(key) : 0;
					results.put(key, count + 1);
				}
			}
			
			return results;			
		}
	}

		
	
	// The following are the methods necessary to implement the table model listener for the viewer
	@JsonIgnore
	@Override
	public int getRowCount() {
		 return _sentences.size();
	}

	@JsonIgnore
	@Override
	public int getColumnCount() {
		return 7;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
			case 0: return "#";
			case 1: return "T";
			case 2: return "Classifications"; 
			case 3: return "C";   //Cluster #
			case 4: return "Sentence";
			case 5: return "Requirements";
			case 6: return "Ambiguity";
			default: return "UNKNOWN COLUMN #"+columnIndex;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex ==0) { return Double.class; }
		else if (columnIndex == 3) { return Integer.class; }
		else { return String.class; }
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Sentence s = this.getElementAt(rowIndex);
		switch (columnIndex) {
			case 0: return s.getOriginalSentencePosition();
			case 1: return s.getSentenceType();
			case 2: return s.getAllBooleanClassificationsAsAbbreviationString(); 
			case 3: return s.getClusterID();
			case 4: return s.getSentence();
			case 5: return s.getRequirements();
			case 6: return s.getAmbiguityFlag();
			default: return "UNKNOWN COLUMN #"+columnIndex;
		}
	}


	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		return; // no action taken
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		_listener = l;
		
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		_listener = null;
	}	
	
	
	
}

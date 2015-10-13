package edu.ncsu.csc.nl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.ncsu.csc.nl.GCConstants;
import edu.ncsu.csc.nl.model.english.Adjective;
import edu.ncsu.csc.nl.model.english.IgnoreWord;
import edu.ncsu.csc.nl.model.english.MiscCheck;
import edu.ncsu.csc.nl.model.type.PartOfSpeech;
import edu.ncsu.csc.nl.model.type.Relationship;
import edu.ncsu.csc.nl.model.type.WordType;
import edu.ncsu.csc.nl.util.Utility;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetEndAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.IndexAnnotation;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;


/**
 * Represents a vertex/node that was derived from the Stanford dependency graph.
 * 
 * The root node has no parent.  to be reachable, a other nodes must have a parent
 * 
 * JSON Representation:
 * When an object of this class is converted to a JSON File, only the ID is exported.
 * Therefore, it
 * 
 * @author John
 *
 */
public class WordVertex implements Serializable {
	
	/* This class is used to create a reference to a variable that we can increment each time a new node is created when creating a graph */
	// want the count to start over at 1 for each new graph (ie, sentence)
	private static class IDCounter {
		int id =0;
	}
	
	public static final long serialVersionUID = 2;
	
	
	private int _id = -1;
	private String _originalWord;
	private String _lemma;
	
	private String _ner; // named entity class from the stanford parser. will be blank if nothing set.
	
	private PartOfSpeech _pos;
	
	private int _startIndexOfWordInSentence = GCConstants.UNDEFINED;
	private int _endIndexOfWordInSentence = GCConstants.UNDEFINED;
	private int _wordIndex =  GCConstants.UNDEFINED;
	
	private ArrayList<WordEdge> _parentRelationship = new ArrayList<WordEdge>(); //Since it is a graph, a word can have two parents
	
	private ArrayList<WordEdge> _childRelationship = new ArrayList<WordEdge>();
	
	@JsonIgnore
	public String getOriginalWord() {
		return _originalWord;
	}
	
	@JsonIgnore
	public String getLemma() {
		return _lemma;
	}
	
	@JsonIgnore
	public void setLemma(String newValue) {
		_lemma = newValue;
	}
	
	public String getWord(WordType wt) {
		if (wt.equals(WordType.LEMMA)) {
			return _lemma;
		}
		else if (wt.equals(WordType.STEM)) {
			return PorterStemmer.getStem(_originalWord);
		}
		else {
			return _originalWord;
		}
	}
	
	@JsonIgnore
	public String getNamedEntityRecognizedLabel() {
		return _ner;
	}
		
	public int getID() {
		return _id;
	}
	
	public void setID(int id) {
		_id = id;
	}
	
	@JsonIgnore
	public int getStartIndexOfWordInSentence() {
		return _startIndexOfWordInSentence;
	}
	
	@JsonIgnore
	public int getEndIndexOfWordInSentence() {
		return _endIndexOfWordInSentence;
	}
	
	@JsonIgnore
	public int getNumberOfParents() {
		return _parentRelationship.size();
	}
	
	@JsonIgnore
	public int getNumberOfChildren() {
		return _childRelationship.size();
	}
	public WordEdge getParentAt(int i) {
		return _parentRelationship.get(i);
	}
	public WordEdge getChildAt(int i) {
		return _childRelationship.get(i);
	}
	
	@JsonIgnore
	public Iterator<WordEdge> getChildren() {
		return _childRelationship.iterator();
	}
	
	public void addParent(WordEdge parent) {
		_parentRelationship.add(parent);
	}
	public void addChild(WordEdge child) {
		for (int i = 0; i < _childRelationship.size(); i++) {
			if (_childRelationship.get(i).getChildNode().getID() > child.getChildNode().getID() ) {
				_childRelationship.add(i,child);
				return;
			}
			
		}
		_childRelationship.add(child);
	}
	
	@JsonIgnore
	public PartOfSpeech getPartOfSpeech() {
		return _pos;
	}
	
	/**
	 * Returns the word's position in the parsed sentence as determined by the tokens generator
	 * Can be used to compare the position of words relative to each other.
	 * 
	 * @return
	 */
	@JsonIgnore
	public int getWordIndex() {
		return _wordIndex;
	}
	
	public WordVertex() {
	}
	
	/** 
	 * Creates a copy of the current vertex, 
	 * NOTE: Only the current vertex is copied
	 *       there is lationship information
	 *       No link to the semantic graph 
	 * 
	 */
	public WordVertex(WordVertex original) {
		_id           = original._id;
		_originalWord = original._originalWord;
		_lemma        = original._lemma;
		_pos          = original._pos;
		_ner          = original._ner;
		_startIndexOfWordInSentence = original._startIndexOfWordInSentence;
        _endIndexOfWordInSentence   = original._endIndexOfWordInSentence;
        _wordIndex                  = original._wordIndex;
	}	
	
	
	public WordVertex(int id, String orginalWord, String lemma, PartOfSpeech pos, String ner, 
			          int startIndex, int endIndex, int wordIndex) {
		
		 orginalWord =  orginalWord.replaceAll("ZDOTZ", ".");   //TODO - clean this up with the textprocessor object
		 orginalWord =  orginalWord.replaceAll("zodtz", ".");
		 
		_id = id;
		_originalWord = orginalWord.toLowerCase();
		_lemma = lemma.toLowerCase();
		_pos = pos;
		if (ner.equals("O")) { _ner = ""; } else { _ner = ner; }
		
		_startIndexOfWordInSentence = startIndex;
		_endIndexOfWordInSentence = endIndex;
		_wordIndex = wordIndex;
				
		if (_ner.equals("")) {
			if (MiscCheck.hasFileType(orginalWord)) {
				_ner = "FILE";
			}
		}
	}
	
	
	
	
	public WordVertex(int id, IndexedWord node, int startIndex, int endIndex, int wordIndex) {
		this(id, node.word(),node.lemma(), PartOfSpeech.retrieve(node.tag()), node.ner(), startIndex, endIndex, wordIndex);
	}
	
	public WordVertex copyGraph() {
		return (WordVertex) Utility.copy(this);
	}
	

	public static WordVertex create(SemanticGraph sg) {
		return WordVertex.create(sg, true);
	}
	
	/**
	 * 
	 * @param sg
	 * @param allowWordRemoval  should stoplist / determiniers / modals verbs be skipped/removed?
	 * @return
	 */
	public static WordVertex create(SemanticGraph sg, boolean allowWordRemoval) {
		
		if (sg.getRoots().size() != 1) {
			System.out.println("WordVertex.create(): received semantic graph with "+sg.getRoots().size()+ " roots");
			sg.prettyPrint();
			throw new UnsupportedOperationException("Invalid number of root nodes in semantic graph");
		}
		
		HashSet<IndexedWord> visitedWords = new HashSet<IndexedWord>();
		
		WordVertex result = createFromSemanticGraph(new IDCounter(), sg, sg.getFirstRoot(), null, null, visitedWords, allowWordRemoval);
		
		
		return result;
	}	
	
	
	private static WordVertex createFromSemanticGraph(IDCounter id, SemanticGraph sg, IndexedWord node, WordVertex parent, WordVertex root, HashSet<IndexedWord> visitedWords, boolean allowWordRemoval) {
		if (visitedWords.contains(node)) {  // prevent looping in the graph
			return parent;
		}
		visitedWords.add(node); // mark that we've been to this node
		
		if (parent == null) { //we were passed in the root node
			
			parent = new WordVertex(++id.id, node, node.get(CharacterOffsetBeginAnnotation.class),node.get(CharacterOffsetEndAnnotation.class),node.get(IndexAnnotation.class));
			root = parent;
		}
		
		//Create and visit children
		for (SemanticGraphEdge depcy: sg.getOutEdgesSorted(node)) {
			Relationship relation = Relationship.retrieve(depcy.getRelation().toString());
			
			if (allowWordRemoval && IgnoreWord.shouldIgnore(relation, PartOfSpeech.retrieve(depcy.getTarget().tag()),depcy.getTarget().lemma()) ){
				continue;
			}
			
			IndexedWord childNode = depcy.getTarget();
			
			//TODO: if we want to get rid of duplicate indices, see if the node already exists.  This will substantially affect the walk graph algorithms. 
			//      will need to include both the vertex and the relationship into the node into it.
			WordVertex childVertex = root.getVertexByWordPosition(childNode.get(IndexAnnotation.class));
			
			if (childVertex == null) {
				childVertex = new WordVertex(++id.id, childNode, childNode.get(CharacterOffsetBeginAnnotation.class),childNode.get(CharacterOffsetEndAnnotation.class),childNode.get(IndexAnnotation.class) );
			}
			
			
			WordEdge we = new WordEdge(relation, parent, childVertex);
			parent.addChild(we);
			childVertex.addParent(we);
			WordVertex.overridePartsOfSpeech(childVertex);
			
			createFromSemanticGraph(id,sg, childNode, childVertex, root, visitedWords,allowWordRemoval); //ignore return value as this is the same as the childvertex value
				
		}
			
		return parent;
	}
	
	/**
	 * Correct possible issues w/ parts of speech from our overrides with "view"
	 * @param wv
	 */
	private static void overridePartsOfSpeech(WordVertex wv) {
		if (wv.getLemma().equals("view") && wv.getParentAt(0).getRelationship() == Relationship.DEP) {
			if (wv.getOriginalWord().endsWith("s")) {wv.setPartOfSpeech(PartOfSpeech.NNS); }
			else { wv.setPartOfSpeech(PartOfSpeech.NN); }
		}
	}
	
	@JsonIgnore
	public String getStringRepresentation() {
		return getStringRepresentation(this, Relationship.ROOT, new HashSet<Integer>(), false, false,false, false,true, true,false);
	}
	
	@JsonIgnore
	public String getStringRepresentationCollapsed() {
		return getStringRepresentation(this, Relationship.ROOT, new HashSet<Integer>(), true,true, false, false,true, true,false);
	}
	
	@JsonIgnore
	public String getStringRepresentationUltraCollapsed() {
		return  getStringRepresentation(this, Relationship.ROOT, new HashSet<Integer>(), true,true, false, true,true, true,true).replaceAll("\\s","");  // ideally should by st.replaceAll("\\s+","") if long sequences of whitespace existed
	}	

	@JsonIgnore
	public String getStringRepresentationUltraCollapsedWithID() {
		return  getStringRepresentation(this, Relationship.ROOT, new HashSet<Integer>(), false,true, false, true,true, true,true).replaceAll("\\s","");  // ideally should by st.replaceAll("\\s+","") if long sequences of whitespace existed
	}	
	
	
	@JsonIgnore
	public String getStringRepresentationPOSCollapsed() {
		return getStringRepresentation(this, Relationship.ROOT, new HashSet<Integer>(), false,false, false, true,true, true,false);
	}
	
	@JsonIgnore
	public String getStringRepresentationPOSOnly() {
		return getStringRepresentation(this, Relationship.ROOT, new HashSet<Integer>(), true,true, false, true,false, true,false);
	}

	@JsonIgnore
	public String getStringRepresentationRelationshipOnly() {
		return getStringRepresentation(this, Relationship.ROOT, new HashSet<Integer>(), true,true, false, true,true,false,false);
	}
		
	/**
	 * This method will generate a line of input that is appropriate to send to a machine learning algorithm
	 * 
	 * @return
	 */
	@JsonIgnore
	public String getInstanceRepresentation() {
		return getInstanceRepresentation(this, Relationship.ROOT, new HashSet<Integer>());
	}
	
	/**
	 * This method will generate a line of input that is appropriate to send to a machine learning algorithm
	 * 
	 * @return
	 */
	@JsonIgnore
	public String getWekaInstanceRepresentation() {
		return getWekaInstanceRepresentation(this, Relationship.ROOT, new HashSet<Integer>());
	}	
	
	@JsonIgnore
	public String getDisplayRepresentation() {
		return getDisplayRepresentation(this, new HashSet<Integer>(),"");
	}	
	
	//intentional made this static to be able to not use "this"
	@JsonIgnore
	private static String getStringRepresentation(WordVertex v, Relationship r, HashSet<Integer> visitedWords, boolean hideID, boolean hideLemma, boolean isRelationshipWildcard, boolean collapsePOS, 
			                                      boolean showRelationship, boolean showPartOfSpeech, boolean useSingleCharLabel) {
		if (visitedWords.contains(v.getID())) {
			return "";
		}
		else {
			visitedWords.add(v.getID());
		}
		
		StringBuilder result = new StringBuilder();
		
		result.append("(");
		if (!hideID) {
			result.append(v.getID());
			result.append(" ");
		}
		if (!hideLemma) {
			result.append(v.getLemma());
			result.append(" ");
		}
		if (showPartOfSpeech) {
			if (hideLemma || collapsePOS) {
				if (useSingleCharLabel) { result.append(v.getPartOfSpeech().getSingleCharacterLabel()); }
				else {	result.append(v.getPartOfSpeech().getCollapsedLabel()); }
			}
			else {
				if (useSingleCharLabel) { result.append(v.getPartOfSpeech().getSingleCharacterLabel()); }
				else { result.append(v.getPartOfSpeech().getActualLabel()); }
			}
			result.append(" ");
		}
		
		if (showRelationship) {
			if (useSingleCharLabel) {
				result.append(r.getSingleCharLabel());
			}
			else {
				if (isRelationshipWildcard && r.isPreposition()) {
					result.append("prep_%");
				}
				else {
					result.append(r);
				}
			}
			result.append(" ");
		}
		
		for (WordEdge e: v._childRelationship) {
			/*
			 * THIS CODE SHOULDN'T BE NEEDED as the nodes aren't created
			 * 
			// check to see if the child is an article determiner, if so skip,
			if (collapse && e.getRelationship() == Relationship.DET && RBACController.ignoreDetermine(e.getChildNode().getLemma())) {
				continue;
			}
			*/
			result.append(getStringRepresentation(e.getChildNode(),e.getRelationship(),visitedWords, hideID, hideLemma,e.isWildcardRelationship(), collapsePOS, showRelationship, showPartOfSpeech,useSingleCharLabel));
		}
		result.append(")");
		
		return result.toString();
	}
	
	private static String getInstanceRepresentation(WordVertex v, Relationship r, HashSet<Integer> visitedWords) {
		if (visitedWords.contains(v.getID())) {
			return "";
		}
		else {
			visitedWords.add(v.getID());
		}
		
		StringBuffer result = new StringBuffer();
		
		result.append(v.getID());
		result.append(",");
		result.append(v.getLemma());
		result.append(",");
		result.append(v.getPartOfSpeech().getCollapsedLabel());
		result.append(",");
		result.append(r);
		result.append(",");
		
		//need to add the parent here.
		if (r.equals(Relationship.ROOT)) {
			result.append("0");
		}
		else {
			result.append(v.getParentAt(0).getParentNode().getID());
		}
		
		for (WordEdge e: v._childRelationship) {
			/* SHOULDN'T NEED THIS AS WE DON'T CREATE the NODES
			// check to see if the child is an article determiner, if so skip,
			if (e.getRelationship() == Relationship.DET && RBACController.ignoreDetermine(e.getChildNode().getLemma())) {
				continue;
			}
			*/
			
			String temp = getInstanceRepresentation(e.getChildNode(),e.getRelationship(),visitedWords);
			if (temp.equals("") == false) {
				result.append(",");
				result.append(temp);
			}
		}

		
		
		return result.toString();
		
		
	}

	
	private static String getDisplayRepresentation(WordVertex v, HashSet<Integer> visitedWords, String indent) {
		if (visitedWords.contains(v.getID())) {
			return "";
		}
		else {
			visitedWords.add(v.getID());
		}
		
		StringBuffer result = new StringBuffer();
		
		result.append(indent);
		result.append("(");
		result.append(v.getID());
		result.append(" ");
		result.append(v.getLemma());
		result.append(" ");
		result.append("["+v._wordIndex+":"+v._startIndexOfWordInSentence+","+v._endIndexOfWordInSentence+"] ");	
		result.append(v.getPartOfSpeech().getCollapsedLabel());
		result.append(" ");
		
		boolean fenceFlag=false;
		for (WordEdge e: v._parentRelationship) {
			if (fenceFlag) { result.append(" "); }
			result.append(e.getRelationship());
			result.append(" ");
			
			if (e.getRelationship().equals(Relationship.ROOT)) {
				result.append("0");
			}
			else {
				result.append(e.getParentNode().getID());
			}
			fenceFlag=true;			
		}
		
		for (WordEdge e: v._childRelationship) {			
			String temp = getDisplayRepresentation(e.getChildNode(),visitedWords,indent+"  ");
			if (temp.equals("") == false) {
				result.append("\n");
				result.append(temp);
			}
		}
		//result.append("\n");
		//result.append(indent);
		result.append(")");
		
		
		return result.toString();
	}
	
	
	private static String getWekaInstanceRepresentation(WordVertex v, Relationship r, HashSet<Integer> visitedWords) {
		if (visitedWords.contains(v.getID())) {
			return "";
		}
		else {
			visitedWords.add(v.getID());
		}
		
		StringBuffer result = new StringBuffer();
		
		result.append(v.getID());
		result.append(",");
		result.append(v.getLemma());
		result.append(",");
		result.append(v.getPartOfSpeech().getCollapsedLabel());
		result.append(",");
		result.append(r);
		result.append(",");
		
		//need to add the parent here.
		if (r.equals(Relationship.ROOT)) {
			result.append("0");
		}
		else {
			result.append(v.getParentAt(0).getParentNode().getID());
		}
		
		for (WordEdge e: v._childRelationship) {
			/* SHOULDN'T NEED THIS AS WE DON'T CREATE the NODES
			// check to see if the child is an article determiner, if so skip,
			if (e.getRelationship() == Relationship.DET && RBACController.ignoreDetermine(e.getChildNode().getLemma())) {
				continue;
			}
			*/
			
			String temp = getWekaInstanceRepresentation(e.getChildNode(),e.getRelationship(),visitedWords);
			if (temp.equals("") == false) {
				result.append(",");
				result.append(temp);
			}
		}

		
		
		return result.toString();
		
		
	}
	
	@JsonIgnore
	public WordVertex getVertexByID(int id) {
		return this.getVertexByID(id, new HashSet<Integer>());
	}
	
	@JsonIgnore
	private  WordVertex getVertexByID(int id, HashSet<Integer> visitedWords) {
		if (this.getID() == id) {
			return this;
		}
		if (visitedWords.contains(this.getID())) { 
			return null;
		}
		else {
			visitedWords.add(this.getID());
		}
				
		for (WordEdge e: this._childRelationship) {
			WordVertex possResult = e.getChildNode().getVertexByID(id, visitedWords);
			if (possResult != null) {
				return possResult;
			}
		}
		return null;
	}
	
	/**
	 * Searches the graph from the current node and returns all vertices
	 * containing the specified word.
	 * 
	 * @param wordType
	 * @param word
	 * @return
	 */
	@JsonIgnore
	public List<WordVertex> getVertexByWord(WordType wordType, String word) {
		List<WordVertex> results = new ArrayList<WordVertex>();
		this.getVertexByWord(wordType, word, results, new HashSet<Integer>());
		return results;
	}
	
	@JsonIgnore
	private  void getVertexByWord(WordType wordType, String word, List<WordVertex> results, HashSet<Integer> visitedWords) {
		if (visitedWords.contains(this.getID())) { 
			return;
		}
		else {
			visitedWords.add(this.getID());
		}
		if (this.getWord(wordType).equals(word)) {
			results.add(this);
		}

				
		for (WordEdge e: this._childRelationship) {
			e.getChildNode().getVertexByWord(wordType, word, results, visitedWords);
		}
	}	
	
	/**
	 * Searches the graph from the current node and returns all vertices
	 * containing words of the specified type.
	 * 
	 * @param wordType
	 * @param word
	 * @return
	 */
	@JsonIgnore
	public List<WordVertex> getVertexByPartOfSpeech(PartOfSpeech pos, boolean useCollapsedComparison, boolean treatModalsSeparate) {
		List<WordVertex> results = new ArrayList<WordVertex>();
		this.getVertexByPartOfSpeech(pos, useCollapsedComparison, treatModalsSeparate, results, new HashSet<Integer>());
		return results;
	}
	
	@JsonIgnore
	private  void getVertexByPartOfSpeech(PartOfSpeech pos, boolean useCollapsedComparison, boolean treatModalsSeparate, List<WordVertex> results, HashSet<Integer> visitedWords) {
		if (useCollapsedComparison) {
			if (treatModalsSeparate && this.getPartOfSpeech() == PartOfSpeech.MD) {
				if (this.getPartOfSpeech().equals(pos)) {	results.add(this);	}
			}
			else {
				if (this.getPartOfSpeech().equalsCollapsed(pos)) {	results.add(this);	}
			}
		}
		else {
			if (this.getPartOfSpeech().equals(pos)) {	results.add(this);	}
		}
		if (visitedWords.contains(this.getID())) { 
			return;
		}
		else {
			visitedWords.add(this.getID());
		}
				
		for (WordEdge e: this._childRelationship) {
			e.getChildNode().getVertexByPartOfSpeech(pos, useCollapsedComparison, treatModalsSeparate, results, visitedWords);
		}
	}	

	
	
	
	
	@JsonIgnore
	public WordVertex getVertexByWordPosition(int wordIndex) {
		
		return this.getVertexByWordPosition(wordIndex, new HashSet<Integer>());
		
		
	}
	
	@JsonIgnore
	private  WordVertex getVertexByWordPosition(int wordIndex, HashSet<Integer> visitedWords) {
		if (this._wordIndex == wordIndex) {
			return this;
		}
		if (visitedWords.contains(this.getID())) { 
			return null;
		}
		else {
			visitedWords.add(this.getID());
		}
				
		for (WordEdge e: this._childRelationship) {
			WordVertex possResult = e.getChildNode().getVertexByWordPosition(wordIndex, visitedWords);
			if (possResult != null) {
				return possResult;
			}
		}
		return null;
	}	
	
	@JsonIgnore
	public java.util.Set<WordVertex> getAllAbbreviationNodes() {
		return this.getAllAbbreviationNodes(new HashSet<Integer>(),new HashSet<WordVertex>(),Relationship.ROOT);
	}
	
	@JsonIgnore
	private  java.util.Set<WordVertex> getAllAbbreviationNodes(HashSet<Integer> visitedWords,java.util.Set<WordVertex> foundAbbreviations, Relationship r) {
		if (visitedWords.contains(this.getID())) { 
			return foundAbbreviations;
		}

		if (r == Relationship.ABBREV) {
			foundAbbreviations.add(this);
		}
		visitedWords.add(this.getID());
				
		for (WordEdge e: this._childRelationship) {
			foundAbbreviations = e.getChildNode().getAllAbbreviationNodes(visitedWords,foundAbbreviations,e.getRelationship());
		}
		return foundAbbreviations;
	}	
	
	
	/** returns the smallest index value (ie, what would be the first word) for a given node.  needed because adjectives are children of their nouns */
	@JsonIgnore
	public int getSmallestIndex() {
		return this.getSmallestIndex(new HashSet<Integer>());
	}

	@JsonIgnore
	private int getSmallestIndex(HashSet<Integer> visitedWords) {
		int smallestFound = Integer.MAX_VALUE;
		
		if (visitedWords.contains(this.getID()) == false) { 
			visitedWords.add(this.getID());
			
			smallestFound = Math.min(this._startIndexOfWordInSentence,smallestFound);
			
			for (WordEdge e: this._childRelationship) {
				smallestFound = Math.min(smallestFound,e.getChildNode().getSmallestIndex(visitedWords));
			}			
		
		}
		
		return smallestFound;
	}
	
	
	/** returns the greatest index value (ie, what would be the first word) for a given node.  needed because adjectives are children of their nouns */
	@JsonIgnore
	public int getGreatestIndex() {
		return this.getGreatestIndex(new HashSet<Integer>());
	}

	@JsonIgnore
	private int getGreatestIndex(HashSet<Integer> visitedWords) {
		int greatestFound = Integer.MIN_VALUE;
		
		if (visitedWords.contains(this.getID()) == false) { 
			visitedWords.add(this.getID());
			
			greatestFound = Math.max(this._startIndexOfWordInSentence,greatestFound);
			
			for (WordEdge e: this._childRelationship) {
				greatestFound = Math.max(greatestFound,e.getChildNode().getGreatestIndex(visitedWords));
			}			
		
		}
		
		return greatestFound;
	}	
	
	/** 
	 *	returns the smallest WordPosition value (ie, what would be the first word) for a given node.
     *  this is based upon the original word position in the string
     */
	@JsonIgnore
	public int getSmallestWordPosition() {
		return this.getSmallestWordPosition(new HashSet<Integer>());
	}

	@JsonIgnore
	private int getSmallestWordPosition(HashSet<Integer> visitedWords) {
		int smallestFound = Integer.MAX_VALUE;
		
		if (visitedWords.contains(this.getID()) == false) { 
			visitedWords.add(this.getID());
			
			smallestFound = Math.min(this.getWordIndex(),smallestFound);
			
			for (WordEdge e: this._childRelationship) {
				smallestFound = Math.min(smallestFound,e.getChildNode().getSmallestWordPosition(visitedWords));
			}			
		
		}
		
		return smallestFound;
	}
	
	
	/** 
	 * returns the greatest WordPosition value (ie, what would be the first word) for a given node.  
	 */
	@JsonIgnore
	public int getGreatestWordPosition() {
		return this.getGreatestWordPosition(new HashSet<Integer>());
	}

	@JsonIgnore
	private int getGreatestWordPosition(HashSet<Integer> visitedWords) {
		int greatestFound = Integer.MIN_VALUE;
		
		if (visitedWords.contains(this.getID()) == false) { 
			visitedWords.add(this.getID());
			
			greatestFound = Math.max(this.getWordIndex(),greatestFound);
			
			for (WordEdge e: this._childRelationship) {
				greatestFound = Math.max(greatestFound,e.getChildNode().getGreatestWordPosition(visitedWords));
			}			
		
		}
		
		return greatestFound;
	}	
	
	
	/** counts how large the graph is, starting at this node */
	@JsonIgnore
	public int getGraphSize() {
		return this.getGraphSize(new HashSet<Integer>());
	}

	@JsonIgnore
	private int getGraphSize(HashSet<Integer> visitedWords) {
		if (visitedWords.contains(this.getID())) { 
			return 0;
		}
		visitedWords.add(this.getID());
		
		int result = 1;  // count this node	
		
		for (WordEdge e: this._childRelationship) {   // sum the sizes of the children
			result += e.getChildNode().getGraphSize(visitedWords);
		}
		
		return result;
	}	
	
	
	
	@JsonIgnore
	public void populateWordVertexArray(WordVertex[] list) {
		this.populateWordVertexArray(list, new HashSet<Integer>());
	}
	
	@JsonIgnore
	private void populateWordVertexArray(WordVertex[] list, HashSet<Integer> visitedWords) {

		if (visitedWords.contains(this.getID())) { 
			return;
		}
		
		visitedWords.add(this.getID());
		list[this.getID()-1] = this;
				
		for (WordEdge e: this._childRelationship) {
			e.getChildNode().populateWordVertexArray(list,visitedWords);
		}
	}	
	
	@JsonIgnore
	public String toString() {
		StringBuffer result = new StringBuffer();
		
		Relationship r = Relationship.ROOT;
		if (this.getNumberOfParents() > 0) { 
			r= this.getParentAt(0).getRelationship();
		}
		
		result.append(this.getID());
		result.append(" ");
		result.append(this.getLemma());
		result.append(" ");
		result.append(this.getPartOfSpeech().getCollapsedLabel());
		result.append(" ");
		result.append(r);
		result.append(" ");
		
		//need to add the parent here.
		if (r.equals(Relationship.ROOT)) {
			result.append("0");
		}
		else {
			result.append(this.getParentAt(0).getParentNode().getID());
		}
		return result.toString();
	}
	
	@JsonIgnore
	public boolean equals(Object o) {
		if (o == null || o instanceof WordVertex == false) {
			return false;
		}
		
		WordVertex v = (WordVertex) o;
		if ( this._id != v._id || 
			!this._originalWord.equals(v._originalWord) ||
			!this._lemma.equals(v._lemma) ||
			this._pos != v._pos ||
			this._startIndexOfWordInSentence != v._startIndexOfWordInSentence ||
			this._endIndexOfWordInSentence != v._endIndexOfWordInSentence ||
			this._parentRelationship.size() != v._parentRelationship.size() ||
			this._childRelationship.size()  != v._childRelationship.size()   		) { 
			return false; 
		}
		
		//TODO: Is this logic correct?  Do I care about the ordering of these relationships?
		
		for (int i=0;i<_parentRelationship.size();i++) {
			if (this._parentRelationship.get(i).equals(v._parentRelationship.get(i)) == false) {
				return false;
			}
		}
		
		for (int i=0;i<_childRelationship.size();i++) {
			if (this._childRelationship.get(i).equals(v._childRelationship.get(i)) == false) {
				return false;
			}
		}
		

		return true;

	}
	
	@JsonIgnore
	public List<Object> getPathToRoot() {
		List<Object> result = new ArrayList<Object>();
		
		WordVertex currentNode = this;
		while (currentNode != null) {
			if (result.contains(currentNode)) {break;}  //TODO: Fix possible hack if root has a parent.
			
			result.add(currentNode);
			if (currentNode._parentRelationship.size() == 0) {
				currentNode = null;
			}
			else if (currentNode._parentRelationship.size() == 1)  {
				result.add(currentNode._parentRelationship.get(0));
				currentNode = currentNode._parentRelationship.get(0).getParentNode();				
			} else { // there are two or more parents for a particular node   TODO:  Fix this to handle cases where there isn't a conjunction (doctors and nurses write prescriptions)
				if (currentNode._parentRelationship.get(0).getRelationship().isConjunction()) {
					result.add(currentNode._parentRelationship.get(1));
					currentNode = currentNode._parentRelationship.get(1).getParentNode();	
				}
				else {
					result.add(currentNode._parentRelationship.get(0));
					currentNode = currentNode._parentRelationship.get(0).getParentNode();						
				}
			}	
		}
		
		return result;
	}
	
	public static WordVertex getLowestCommonAncestor(HashMap<WordVertex, List<Object>> paths) {
		Set<WordVertex> keys = paths.keySet();
		
		if (keys.iterator().hasNext() == false) {
			System.err.println("error detected.  bug not fixed yet");
		}
		WordVertex anyKey = keys.iterator().next(); // don't care which path it is, the last element of all of them is the root;
		List<Object> anyPath = paths.get(anyKey);
		
		WordVertex result = (WordVertex) anyPath.get(anyPath.size()-1);
		
		int offset = 3;
		boolean foundAncestor = false;
		
		while (!foundAncestor) {
			WordVertex possibleResult = null;
			for (WordVertex key: keys) {
				List<Object> path = paths.get(key);
				if (path.size() < offset) {        // Can't go any deeper on this path, found the ancestor 
					possibleResult = null;
					foundAncestor = true;
					break;
				}  
				if (possibleResult == null) {          // we haven't explored any of the paths yet,  just get a node so we can start comparing...
					possibleResult = (WordVertex) path.get(path.size()-offset);
				}
				else {
					if (!path.get(path.size()-offset).equals(possibleResult)) {
						possibleResult = null;
						foundAncestor = true;
						break;
					}  
				}
			}
			if (possibleResult != null) {
				result = possibleResult;
			}
			offset += 2;
		}
		
		return result;	
	}
	
	@JsonIgnore
	public	static int getPositionOfVertexInPath(WordVertex v, List<Object> path) {
		for (int i=0;i<path.size();i=i+2) {   //advance by two to skip relationship entries
			if (path.get(i) == v) { // equality should be ok as we pretty have access to the references
				return i;
			}
		}
		return -1;
	}
	
	@JsonIgnore
	private boolean hasChildRelationship(Relationship r) {
		for (WordEdge e: _childRelationship) {
			if (e.getRelationship() == r) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the first child relationship found.
	 * 
	 * @param r
	 * @return
	 */
	@JsonIgnore
	public WordEdge getChildRelationship(Relationship r) {
		for (WordEdge e: _childRelationship) {
			if (e.getRelationship() == r) {
				return e;
			}
		}
		return null;
	}
	
	/**
	 * returns all instances of the child relationship
	 * 
	 * @param r
	 * @param followWildcards
	 * @return
	 */
	@JsonIgnore
	public List<WordEdge> getChildRelationshipAll(WordEdge otherEdge, boolean followWildcards) {
		
		//asdfas
		//In following the wildards, I need to be careful about which one is selected from the calling program here.
		
		ArrayList<WordEdge> result = new ArrayList<WordEdge>();
		for (WordEdge e: _childRelationship) {
			if (e.getRelationship() == otherEdge.getRelationship()) { result.add(e);	}
			
			// we also need to test for wildcard -> assume that the wildcards
			if (followWildcards) {
				if (e.isWildcardRelationship() && otherEdge.getRelationship().isPreposition()) {
					result.add(e);
				}
				else if (otherEdge.isWildcardRelationship() && e.getRelationship().isPreposition()) {
					result.add(e);
				}
			}
		}
		return result;
	}	
	
	
	/**
	 * Returns the child wordEdge by position for a given relationship, but skips those list in the passed in array
	 * 
	 * @param r
	 * @param indicesToAvoid
	 * @returns the first discovered edge (by child #) with the pattern, -1 if it is not found
	 */
	@JsonIgnore
	public int getChildRelationship(Relationship r, ArrayList<Integer> indicesToAvoid) {
		for (int i=0; i < this.getNumberOfChildren(); i++) {
			if (indicesToAvoid.contains(i)) { continue; }
			WordEdge e = this.getChildAt(i);
			if (e.getRelationship().equals(r)) {
				return i;
			}
		}
		return -1;
	}	
	
	
	private boolean isParentRelationshipSignifyCollapsible() {
		if (this.getNumberOfParents() == 0) { return true; }   // all the root node to be collapsed if possible.
		
		
		for (WordEdge e: _parentRelationship) {
			if (e.getRelationship().areChildrenCollapsible()) {return true; }
		}
		
		return false;
	}
	
	/**
	 * Checks if the children can be collapsed, for that to occur, they can not have any children themselves.
	 * @return
	 */
	private boolean areChildrenCollapsible() {
	
		for (WordEdge e: _childRelationship) {
			if (e.getChildNode().getNumberOfChildren() >0) {return false;}
		}
		
		return true;
		
	}
	
	public void collapseNounPhrases(String originalSentenceText) {
		this.collapseNounPhrases(originalSentenceText, new HashSet<Integer>());
		IDCounter ic = new IDCounter();
		this.renumberIDs(ic,new HashSet<Integer>());
	}
	
	/**
	 * Checks if the current node can by collapsed as part of a noun phrase. 
	 * Cannot be a determiner or a number
	 * 
	 */
	private boolean isCollapsibleAsPartOfNounPhrase() {
		return  ( (this.getPartOfSpeech().equalsCollapsed(PartOfSpeech.NN) ||
				   this.getPartOfSpeech().equals(PartOfSpeech.JJ)  )&&        // Needs to be just the adjectives, don't want comparatives or superlatives
				   !(Adjective.isSelector(this.getLemma())) && !this.getLemma().equals("only") &&
				   !this.getLemma().endsWith("able") && !this.getLemma().equals("review"));
		
/*		this.getPartOfSpeech() != PartOfSpeech.CD && this.getPartOfSpeech() != PartOfSpeech.DT  && 
				 this.getPartOfSpeech() != PartOfSpeech.PRP$ &&
				 this.getPartOfSpeech() != PartOfSpeech.RB &&
				 this.getPartOfSpeech() != PartOfSpeech.LS &&
				 this.getPartOfSpeech() != PartOfSpeech.MD &&*/		
	}
	
	/**
	 * Tests if the child (both by the relationship to the child and by the node itself
	 */
	private static boolean isChildCollapsible(WordEdge edgeToChild) {
		return (!edgeToChild.getRelationship().isPreposition() && 
				!edgeToChild.getRelationship().isConjunction() && 
				edgeToChild.getRelationship() != Relationship.POSS  && 
				edgeToChild.getRelationship() != Relationship.POSSESSIVE  && 
				edgeToChild.getRelationship() != Relationship.ABBREV && 
				edgeToChild.getRelationship() != Relationship.DEP && 
				edgeToChild.getChildNode().getNumberOfChildren() == 0 &&
				edgeToChild.getChildNode().getNumberOfParents()  == 1);
	}
	
	
	/**
	 * This method checks to see whether or not there is any of the following
	 * characters: , ( )
	 * in the substring of the sentence defined by the pos1 and pos2 variables
	 * @param sentence
	 * @param pos1
	 * @param pos2
	 * @return
	 */
	private static boolean canCollapseString(String sentence, int pos1, int pos2) {
		String sub = "";
		/*
		int max = sentence.length()-1;
		if (pos1 < 0 || pos2 < 0) { return false;}
		if (pos1 < max || pos2 > max) {return false; }
		
		if (pos1<pos2) {
			sub = sentence.substring(Math.max(0, pos1),Math.min(pos2,max));
		}
		else {
			sub = sentence.substring(Math.max(0, pos2),Math.min(pos1,max));
		}
		*/
		try {
		if (pos1<pos2) {
			sub = sentence.substring(pos1,pos2);
		}
		else {
			sub = sentence.substring(pos2,pos1);
		}
		} 
		catch (java.lang.StringIndexOutOfBoundsException e) {
			//occurs when we have two sentences in the same input.  the indices for the latter sentence don't exist ..
			return false;
		}
		
		return (!(sub.contains(",") || sub.contains(":") || sub.contains(";") || sub.contains("(") || sub.contains(")")));
		
	}
	
	
	/**
	 * Function will recursively walk a tree and collapse any noun phrases possible.  need to track IDs that I've visited ...
	 */
	private void collapseNounPhrases(String originalSentenceText, HashSet<Integer> visitedWords) {
		if (visitedWords.contains(this.getID())) { return; }
		visitedWords.add(this.getID());
			
		for (WordEdge e: _childRelationship) {
			e.getChildNode().collapseNounPhrases(originalSentenceText, visitedWords);
		}
		
		if (this.getNumberOfChildren() > 0 && this.isParentRelationshipSignifyCollapsible() /* && this.areChildrenCollapsible() */&& this.getPartOfSpeech().equalsCollapsed(PartOfSpeech.NN)) {
			// need to construct orginal string and lemma phrases
			StringBuilder sbLemma = new StringBuilder();
			StringBuilder sbOriginalWord = new StringBuilder();
			
			// other words to ignore: own, 
			for (WordEdge e:_childRelationship) {
				if (!WordVertex.isChildCollapsible(e)) { continue; }
				WordVertex child = e.getChildNode();
				
				if (!child.isCollapsibleAsPartOfNounPhrase()) { continue; }  // Don't collapse numbers or determiners
				if (!canCollapseString(originalSentenceText, this.getStartIndexOfWordInSentence(), child.getStartIndexOfWordInSentence())) { continue; } // don't collapse if theres a ',' or paranthesis
				
				sbLemma.append(child.getLemma()); sbLemma.append(" ");
				sbOriginalWord.append(child.getOriginalWord()); sbOriginalWord.append(" ");
			}
			
			_originalWord = sbOriginalWord + _originalWord;
			_lemma = sbLemma + _lemma;
			
			for (int i = _childRelationship.size() -1; i >=0; i--) {
				if (_childRelationship.get(i).getChildNode().isCollapsibleAsPartOfNounPhrase()) {
					if (WordVertex.isChildCollapsible(_childRelationship.get(i)) && canCollapseString(originalSentenceText, this.getStartIndexOfWordInSentence(), _childRelationship.get(i).getChildNode().getStartIndexOfWordInSentence())   ) {	_childRelationship.remove(i); }
				}
			}
		}

	}
	
	private void renumberIDs(IDCounter id, HashSet<Integer> visitedWords) {
		if (visitedWords.contains(this.getID())) { return; }
		
		this.setID(++id.id);
		visitedWords.add(id.id);
		
		for (WordEdge e: _childRelationship) {
			e.getChildNode().renumberIDs(id, visitedWords);
		}		
	}
	
	
	public boolean hasAllChildRelationships(Relationship[] r) {
		for (int i=0;i<r.length;i++) {
			WordEdge e = this.getChildRelationship(r[i]);
			if (e == null) { return false; }
		}
		return true;
	}
	
	public void removeChildRelationship(Relationship r) {
		WordEdge we = this.getChildRelationship(r);
		if (we != null) {
			_childRelationship.remove(we);
		}
	}
	
	public void removeAllParents() {
		this._parentRelationship.clear();
	}	
	
	/**
	 * Returns a subgraph from the root object containing all of the vertices
	 * Assumptions: - All vertices are in the same graph
	 *              - Root node is reachable from the each vertex
	 * @param vertices
	 * @return
	 */
	@JsonIgnore
	public static WordVertex extractPattern(Set<WordVertex> vertices) {
		
		// 1) For each node, get the primary path to the root node  TODO: THIS MAY CAUSE A HIGHER ANCESTOR TO BE FOUND
		HashMap<WordVertex, List<Object>> paths = new HashMap<WordVertex, List<Object>>();
		for (WordVertex wv: vertices) {
			paths.put(wv, wv.getPathToRoot());
		}
		WordVertex commonRoot = WordVertex.getLowestCommonAncestor(paths);  // 2. find the lowest root
	
		// 3. Generate a new graph consisting of just the vertices passed in and the common root (which may be one of those vertices)
		//    This is going to be done by creating a copy of the root, then copy over the path from each vertex to each root
		WordVertex resultRoot = new WordVertex(commonRoot);
		
		for (WordVertex wv: vertices) {
			List<Object> path = paths.get(wv);
			
			int rootIndex = WordVertex.getPositionOfVertexInPath(commonRoot, path);
			if (rootIndex == -1) { throw new IllegalStateException("ExtractPattern: didn't find common ancestor"); }
			
			WordVertex currentNode = resultRoot;
			for (int i=rootIndex-1;i>0;i=i-2) {  //we'll process both a relation and the child node in the same loop.
				WordEdge edge = (WordEdge) path.get(i);
				WordEdge existingEdge = currentNode.getChildRelationship(edge.getRelationship());
				if (existingEdge == null || !existingEdge.getChildNode().getOriginalWord().equals( ((WordVertex)path.get(i-1)).getOriginalWord() )) {
					WordVertex childNode = (WordVertex) resultRoot.getVertexByID( ((WordVertex) path.get(i-1)).getID() ); 
					if (childNode == null) {
						childNode = new WordVertex((WordVertex) path.get(i-1));
					}
					WordEdge we = new WordEdge(edge.getRelationship(), currentNode, childNode);
					currentNode.addChild(we);
					childNode.addParent(we);
					currentNode = childNode;
				}
				else {
					currentNode = (WordVertex) existingEdge.getChildNode();
				}
			}
		}

		return resultRoot;
		
	}		
	
	
	
	
	
	
	/**
	 * concatenates all of the words from the passed in list into a single string
	 * 
	 * @param list
	 * @return
	 */
	public static String getListAsSingleWord(List<WordVertex> list, WordType wt) {
		if (list.size() == 0) { return ""; }
		StringBuilder result = new StringBuilder(ifNullUseBlank(list.get(0).getWord(wt)));
		for (int i=1;i<list.size();i++) {
			result.append(" ");
			result.append(ifNullUseBlank(list.get(i).getWord(wt)));
		}
		return result.toString();
	}
	
	private static String ifNullUseBlank(String s) {
		if (s == null) {
			return "";
		}
		return s;
	}
	
	/**
	 * concatenates all of the words from the passed in list into a single string
	 * of vertex IDs, separate by commas
	 * 
	 * @param list
	 * @return
	 */
	public static String getListAsVertexIDs(List<WordVertex> list) {
		if (list.size() == 0) { return ""; }
		StringBuilder result = new StringBuilder(Integer.toString(list.get(0).getID()));
		for (int i=1;i<list.size();i++) {
			result.append(",");
			result.append(Integer.toString(list.get(i).getID()));
		}
		return result.toString();
	}		
	
	@JsonIgnore
	private void setPartOfSpeech(PartOfSpeech newSpeechTag) {
		_pos = newSpeechTag;
	}
	
	public void setActionNode(boolean value) {};
	public void setObjectNode(boolean value) {};
	public void setSubjectNode(boolean value) {};
	
	
	/**
	 * returns a list containing strings representing the different node patterns within the string
	 * 
	 * @param minNodes
	 * @param maxNodes
	 * @return
	 */
	@JsonIgnore
	public ArrayList<String> extractAllSubgraphPatterns(int minNodes, int maxNodes, int depthLimit) {
		ArrayList<WordVertex> intermediateResults = this.extractSubgraphs(new HashSet<Integer>(), 1, depthLimit);
		HashSet<String> tempResults = new HashSet<String>();
		
		for (WordVertex wv: intermediateResults) {
			String pattern = wv.getStringRepresentationUltraCollapsedWithID();
			int countNodes = Utility.countOccurrences(pattern, '(');
			if (countNodes >= minNodes && countNodes <= maxNodes) {
				tempResults.add(pattern);
			}
		}
		ArrayList<String> results = new ArrayList<String>();
		for (String result: tempResults) {
			results.add(result.replaceAll("[0-9]", ""));
		}
		
		return results;
	}
	
	@JsonIgnore
	public static ArrayList<WordVertex> getCombinations(ArrayList<WordVertex> items, int size) {
		ArrayList<WordVertex> lst = new ArrayList<WordVertex>();
	    for (int i=0; i< items.size(); i++) { 
	        if (size == 1) {
	        	lst.add(items.get(i).copyGraph());
	        }
	        ArrayList<WordVertex> tempItems = new ArrayList<WordVertex>(items);
	        for (int k=0;k<i+1;k++) {
	        	tempItems.remove(0);
	        }
	        for (WordVertex item: getCombinations(tempItems,size-1)) {
	        	WordVertex result = items.get(i).copyGraph();
	        	for (WordEdge we: item._childRelationship) {
	        		WordEdge ne = new WordEdge(we.getRelationship(),result, we.getChildNode().copyGraph());
	        		ne.getParentNode().addChild(ne);
	        		ne.getChildNode().removeAllParents();
	        		ne.getChildNode().addParent(ne);
	        		
	        	}
	        	lst.add(result);
	        	
	        	//lst.add(items.get(i) + item);
	        }
	        
	    }
	    return lst;
	}	
	
	@JsonIgnore
	private ArrayList<WordVertex> extractSubgraphs(HashSet<Integer> visitedNodes, int currentDepth, int maxDepth) {
		ArrayList<WordVertex> results = new  ArrayList<WordVertex>();
		if (visitedNodes.contains(this.getID())) { return results; }
		else { visitedNodes.add(this.getID()); }
		
		WordVertex currentNode = new WordVertex(this);
		results.add(currentNode);
		
		if (currentDepth >= maxDepth) { return results;}
		
		ArrayList<WordVertex> directChildren = new ArrayList<WordVertex>();
		for (WordEdge e: this._childRelationship) {
			ArrayList<WordVertex> tempResults = e.getChildNode().extractSubgraphs(visitedNodes, currentDepth++, maxDepth);
			
			for (WordVertex wv: tempResults) {
				results.add(wv);
				
				if (wv.getID() == e.getChildNode().getID()) {
					WordEdge we = new WordEdge(e.getRelationship(),new WordVertex(this), wv.copyGraph());
					we.getParentNode().addChild(we);
					we.getChildNode().addParent(we);
					results.add(we.getParentNode());
					directChildren.add(we.getParentNode().copyGraph());
				}	
			}
			
			// Now, I need to generate all of the combinations from directChildren
			for (int i=2;i<=directChildren.size();i++) {
				results.addAll(getCombinations(directChildren,i));
			}
			
		}
		
		return results;
	}
	
	/*
	@JsonIgnore
	public int hashCode() {
		return this.getNumberOfChildren() *10000000 + this.getNumberOfParents() * 1000000 + this.getPartOfSpeech().ordinal() *1000 + this.getLemma().hashCode();
	}
	*/
		
}

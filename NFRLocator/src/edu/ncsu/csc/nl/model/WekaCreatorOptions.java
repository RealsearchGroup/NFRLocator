package edu.ncsu.csc.nl.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.ncsu.csc.nl.model.english.StopWord;
import edu.ncsu.csc.nl.model.type.WordType;

/**
 * Simple data class to hold all of the options when creating Weka-based data
 * 
 * @author Adminuser
 */
public class WekaCreatorOptions {
	private boolean _nodeNumber = false;
	private boolean _lemma = true;
	private boolean _originalWord = false;
	private boolean _partOfSpeech = false;
	private boolean _relationshipToParent = false;
	private boolean _parentNodeNumber = false;
	private boolean _namedEntity = false;
	private boolean _exportSentenceAsString = false;  // if true exports entire sentence as string.  Otherwise, the nodes are exported
	private boolean _useOriginalSentence    = false;  // if we are exporting as a string, should we just use the original sentence?
	private WordType _wordType = WordType.LEMMA;      // when exporting sentence as astring, what type of word should be used?
	private List<String> _stopWords = new ArrayList<String>(); // what stop words should be used when exporting as a string.  Defaults to none
	private boolean _useNERSentenceIndicators = false; 
	private boolean _includeUltraCollapsedTreeRepresentation = true;
	private boolean _useCasamayorSentenceRepresentation = false; // if exporting as a string, if this is set, export using the format in his paper.  Overrides useOrginalSentence, wordType.  Sets stopWords to glasgow if set to true;
	/**
	 * Creates the option with only the lemma selected for node options.
	 */
	public WekaCreatorOptions() {
	}
	
	/**
	 * Creates the option with only the lemma selected, and exporting a sentence rather than the nodes.
	 */
	public static WekaCreatorOptions createExportSentence() {
		WekaCreatorOptions wco = new WekaCreatorOptions();
		wco.setExportSentenceAsString(true);
		
		return wco;
	}
	
	/**
	 * Creates the option with only the lemma selected, and exporting a sentence rather than the nodes.
	 */
	public static WekaCreatorOptions createCasamayorOption() {
		WekaCreatorOptions wco = new WekaCreatorOptions();
		wco.setUseCasamayorSentenceRepresentation(true);
		
		return wco;
	}
	
	
	public WekaCreatorOptions(boolean nodeNumber, boolean lemma,
			boolean originalWord, boolean partOfSpeech,
			boolean relationshipToParent, boolean parentNodeNumber,
			boolean namedEntity, boolean sentenceString, boolean useNERSentenceIndicators) {
		_nodeNumber = nodeNumber;
		_lemma = lemma;
		_originalWord = originalWord;
		_partOfSpeech = partOfSpeech;
		_relationshipToParent = relationshipToParent;
		_parentNodeNumber = parentNodeNumber;
		_namedEntity = namedEntity;
		_exportSentenceAsString = sentenceString;
		_useNERSentenceIndicators = useNERSentenceIndicators;
	}

	public boolean useNodeNumber() {
		return _nodeNumber;
	}

	public void setNodeNumber(boolean nodeNumber) {
		_nodeNumber = nodeNumber;
	}

	public boolean useLemma() {
		return _lemma;
	}

	public void setLemma(boolean lemma) {
		_lemma = lemma;
	}

	public boolean useOriginalWord() {
		return _originalWord;
	}

	public void setOriginalWord(boolean originalWord) {
		_originalWord = originalWord;
	}

	public boolean usePartOfSpeech() {
		return _partOfSpeech;
	}

	public void setPartOfSpeech(boolean partOfSpeech) {
		_partOfSpeech = partOfSpeech;
	}

	public boolean useRelationshipToParent() {
		return _relationshipToParent;
	}

	public void setRelationshipToParent(boolean relationshipToParent) {
		_relationshipToParent = relationshipToParent;
	}

	public boolean useParentNodeNumber() {
		return _parentNodeNumber;
	}

	public void setParentNodeNumber(boolean parentNodeNumber) {
		_parentNodeNumber = parentNodeNumber;
	}

	public boolean useNamedEntity() {
		return _namedEntity;
	}

	public void setNamedEntity(boolean namedEntity) {
		_namedEntity = namedEntity;
	}

	public boolean exportSentenceAsString() {
		return _exportSentenceAsString;
	}

	public void setExportSentenceAsString(boolean sentenceString) {
		_exportSentenceAsString = sentenceString;
	}

	public boolean useNERSentenceIndicators() {
		return _useNERSentenceIndicators;
	}

	public void setNERSentenceIndicators(boolean useNERSentenceIndicators) {
		_useNERSentenceIndicators = useNERSentenceIndicators;
	}
	
	public int getNumberOfNodeOptionsSelected() {
		int count = 0;
		if (_nodeNumber) count++;
		if (_lemma) count++;
		if (_originalWord) count++;
		if (_partOfSpeech) count++;
		if (_relationshipToParent) count++;
		if (_parentNodeNumber) count++;
		if (_namedEntity) count++;
		return count;
	}

	public boolean useOriginalSentence() {
		return _useOriginalSentence;
	}

	//setting to true, will make casamayor false
	public void setUseOriginalSentence(boolean useOriginalSentence) {
		_useOriginalSentence = useOriginalSentence;
		if (_useOriginalSentence) { _useCasamayorSentenceRepresentation = false; }
	}

	public WordType getWordType() {
		return _wordType;
	}

	public void setWordType(WordType wordType) {
		_wordType = wordType;
	}

	public List<String> getStopWords() {
		return _stopWords;
	}

	public void setStopWords(String[] stopWords) {
		_stopWords = Arrays.asList(stopWords);		
	}
	public void setStopWords(Collection<String> stopWords) {
		_stopWords = new java.util.ArrayList<String>(stopWords);		
	}

	public boolean useCasamayorSentenceRepresentation() {
		return _useCasamayorSentenceRepresentation;
	}

	public void setUseCasamayorSentenceRepresentation(boolean useStyle) {
		_useCasamayorSentenceRepresentation = useStyle;
		if (useStyle) {
			_useOriginalSentence = false;
			_exportSentenceAsString = true;
			this.setStopWords(StopWord.getListByName(StopWord.GLASGOW));
		}
	}

	public boolean useUltraCollapsedStringRepresentation() {
		return _includeUltraCollapsedTreeRepresentation;
	}
	
	public void setIncludeUltraCollapsedTreeRepresentation(boolean value) {
		_includeUltraCollapsedTreeRepresentation = value;
	}
}
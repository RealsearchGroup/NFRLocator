package edu.ncsu.csc.nl.model.type;

/**
 * PennBank Part of speech tags
 * 
 * @author John
 *
 */
public enum PartOfSpeech {

	CC("CC",'C',"conjunction, coordinating","and, or, but",null),
	CD("CD",'D',"cardinal number","five, three, 13%",null),
	DT("DT",'T',"determiner","the, a, these ",null),
	EX("EX",'E',"existential there","there were six boys ",null),
	FW("FW",'F',"foreign word","mais ",null),
	IN("IN",'I',"conjunction, subordinating or preposition","of, on, before, unless ",null),
	JJ("JJ",'J',"adjective","nice, easy",edu.mit.jwi.item.POS.ADJECTIVE),
	JJR("JJR",'z',"adjective, comparative","nicer, easier",JJ,edu.mit.jwi.item.POS.ADJECTIVE),
	JJS("JJS",'y',"adjective, superlative","nicest, easiest ",JJ,edu.mit.jwi.item.POS.ADJECTIVE),
	LS("LS",'L',"list item marker","",null),
	MD("MD",'M',"verb, modal auxillary","may, should ",edu.mit.jwi.item.POS.VERB),
	NN("NN",'N',"noun, singular or mass","tiger, chair, laughter ",edu.mit.jwi.item.POS.NOUN),
	NNS("NNS",'x',"noun, plural","tigers, chairs, insects ",NN, edu.mit.jwi.item.POS.NOUN),
	NNP("NNP",'w',"noun, proper singular","Germany, God, Alice ",NN,edu.mit.jwi.item.POS.NOUN),
	NNPS("NNPS",'v',"noun, proper plural","we met two Christmases ago ",NN,edu.mit.jwi.item.POS.NOUN),
	PDT("PDT",'R',"predeterminer","both his children ",null),
	PRP("PRP",'P',"pronoun, personal","me, you, it ",edu.mit.jwi.item.POS.NOUN),
	PRP$("PRP$",'O',"pronoun, possessive","my, your, our ",null),
	RB("RB",'A',"adverb","extremely, loudly, hard  ",edu.mit.jwi.item.POS.ADVERB),
	RBR("RBR",'u',"adverb, comparative","better ",RB,edu.mit.jwi.item.POS.ADVERB),
	RBS("RBS",'t',"adverb, superlative","best ",RB,edu.mit.jwi.item.POS.ADVERB),
	RP("RP",'s',"adverb, particle","about, off, up ",RB,edu.mit.jwi.item.POS.ADVERB),
	SYM("SYM",'S',"symbol","% ",null),
	TO("TO",'G',"infinitival to","what to do? ",null),
	UH("UH",'U',"interjection","oh, oops, gosh ",null),
	VB("VB",'V',"verb, base form","think ",edu.mit.jwi.item.POS.VERB),
	VBZ("VBZ",'r',"verb, 3rd person singular present","she thinks ",VB,edu.mit.jwi.item.POS.VERB),
	VBP("VBP",'q',"verb, non-3rd person singular present","I think ", VB,edu.mit.jwi.item.POS.VERB),
	VBD("VBD",'p',"verb, past tense","they thought ",VB,edu.mit.jwi.item.POS.VERB),
	VBN("VBN",'o',"verb, past participle","a sunken ship ",VB,edu.mit.jwi.item.POS.VERB),
	VBG("VBG",'n',"verb, gerund or present participle","thinking is fun ",VB,edu.mit.jwi.item.POS.VERB),
	WDT("WDT",'B',"wh-determiner","which, whatever, whichever ",edu.mit.jwi.item.POS.NOUN),
	WP("WP",'W',"wh-pronoun, personal","what, who, whom ",edu.mit.jwi.item.POS.NOUN),
	WP$("WP$",'H',"wh-pronoun, possessive","whose, whosever ",null),
	WRB("WRB",'K',"wh-adverb","where, when ",edu.mit.jwi.item.POS.ADVERB),
	POS("POS",'_',"","",null), // TODO: Explore when this arises.
	$("$",'$',"","",null),
	COMMA(",",',',"","",null),
	COLON(":",':',"","",null),
	RRB("RRB",']',"","",null),
	LRB("LRB",'[',"","",null),
	LCB("LCB",'{',"","",null),
	RCB("RCB",'}',"","",null),
	LSB("LSB",'>',"","",null),
	RSB("RSB",'<',"","",null),
	NONE("-NONE",'-',"","",null),
	HASH("HASH(#)",'#',"","",null),
	DQ("DQ",'*',"","",null);

	char _singleCharacterLabel;
	String _label;
	String _name;
	String _example;
	edu.mit.jwi.item.POS _wordNetPartOfSpeech;
	
	/** This allows us to treat singular and plural nouns the same.  Similarly all verbs should be just a verb */
	PartOfSpeech _collapsedPOS;
	PartOfSpeech(String label, char singleCharacterLabel, String name, String example, edu.mit.jwi.item.POS wordNetPOS ) {
		_label = label;
		_singleCharacterLabel = singleCharacterLabel;
		_name  = name;
		_example = example;
		_collapsedPOS = this;
		_wordNetPartOfSpeech = wordNetPOS;
		
	}
	PartOfSpeech(String label, char singleCharacterLabel, String name, String example, PartOfSpeech collapsed, edu.mit.jwi.item.POS wordNetPOS) {
		this(label,singleCharacterLabel,name,example, wordNetPOS);
		_collapsedPOS = collapsed;
	}
	
	
	public String toString() {
		return _label;
	}
	
	public String getActualLabel() {
		return _label;
	}
	
	public String getName() {
		return _name;
	}
	
	public String getExample() {
		return _example;
	}
	
	public PartOfSpeech getCollapsedPOS() {
		return _collapsedPOS;
	}
	
	public String getCollapsedLabel() {
		return _collapsedPOS.getActualLabel();
	}
	
	public static PartOfSpeech retrieve(String tag) {
		switch (tag) {
		  case "-RRB-":  return RRB;
		  case "-LRB-":  return LRB;
		  case "-LCB-":	 return LCB;
		  case "-RCB-":  return RCB;
		  case "-LSB-":  return LSB;
		  case "-RSB-":  return RSB;
		  case "-NONE-": return NONE;
		  case ",":		 return COMMA;
		  case ":":		 return COLON;
		  case "#":    	 return HASH;
		  case "``":     return DQ;
		  default:       return PartOfSpeech.valueOf(PartOfSpeech.class,tag);
		}
	}
	
	public edu.mit.jwi.item.POS getWordNetPOS() {
		return _wordNetPartOfSpeech;
	}
	
	public boolean equalsCollapsed(PartOfSpeech pos) {
		//System.out.println(pos.getCollapsedLabel() +":" + this.getCollapsedLabel());
		return this.getCollapsedPOS() == pos.getCollapsedPOS();
	}
	
	public char getSingleCharacterLabel() {
		return _collapsedPOS._singleCharacterLabel;
	}
	
	public boolean isPlural() {
		return (this == NNS || this == NNPS);
	}
	
	public boolean isPronomial() {
		return (this == PRP || this == PRP$  || this == WP || this == WP$);
	}
}

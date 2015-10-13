package edu.ncsu.csc.nl.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.util.CoreMap;

/**
 * This class is used to override parts of speech tags that are inherently
 * incorrect in the input.
 * 
 * % is used as a wild card for both the words and the POS to match (1st and 2nd columns)
 * 
 * the format of an input line needs to be
 *  words<tab>part of speech pattern<tab>new part of speech pattern 
 *  system displays<tab>NN NNS<tab>NN VB
 * 
 * If a wild card exists in the third column, then the original POS for that tag will not be corrected.
 * 
 * @author John
 *
 */
public class CorrectPOSTags implements Annotator {
	String[] _initialLines = {"% displays\t% NNS\t% VB",
			                  "professional\tJJ\tNN",                 //Professional is treated an adjective pretty much always by stanford.  Our sitaution it will be a noun usually.
			                  "to access\t% %\tTO VB",
			                  "representative\tJJ\tNN",
			                  "subject\tJJ\tNN",
			                  //"% %\tMD %\tMD VB",                      // any modal verb should always be followed by another verb ...
			                  "% stores\tNN NNS\tNN VB",              // words more frequently used as nouns tend to be misclassified as such.  Make sure they are verbs
			                  "% views\tNN NNS\tNN VB",
			                  "% schedules\tNN NNS\tNN VB",
			                  "% requests\tNN NNS\tNN VB",
			                  "% types\tNN NNS\tNN VB",
			                  "% inputs\tNN NNS\tNN VB",
			                  "% reports\tNN NNS\tNN VB",
			                  "% changes\tNN NNS\tNN VB",
			                  "% records\tNN NNS\tNN VB",
			                  "review %\tNN NNS\tVB NNS",
			                  "review %\tNN NN\tVB NN",
			                  "% % records\tNN VB MD\tNN NNS MD",
			                  "% % records\tVB NN VB\tVB NN NNS",
			                  "can graph\tMD NN\tMD VB",
			                  "% upload\tMD NN\tMD VB",
			                  "system %\tNN NNS\tNN VB",
			                  "Registrar %\tNNP NNS\tNNP VB",
			                  "% typing\tNN NN\tNN VBG",
			                  "% typing\tNNP NN\tNNP VBG",
			                  "deletes\tNNS\tVB",
			                  "the %\tDT VBP\tDT NN",                    //a verb can never follow a determiner
			                  "record %\tJJ NNS\tVB NNS",
			                  "records %\tNNS DT\tVB DT",
			                  "view\t%\tVBZ",
			                  "views\t%\tVBZ",
			                  "not\tVB\tRB",
			                  "but\t%\tCC",
			                  "and\t%\tCC",
			                  "or\t%\tCC",
			                  "nor\t%\tCC",
			                  "% %\tDT VB\tDT NN",
			                  "% shows\tNN NNS\tNN VB",
			                  "% shows\tNNP NNS\tNNP VB",
			                  "% checks\tNN NNS\tNN VB",
			                  "% checks\tNNP NNS\tNNP VB",
			                  "% index\tMD NN\tMD VB",
			                  "Export\tNN\tVB",
			                  "to type\t% %\tTO VB"};                   
	
	java.util.List<String> _lines = Arrays.asList(_initialLines);
	
	class OverrideTag {
		String text;  // this is the text to match
		Pattern pattern;  //this is the regular expression for the text to find in a sentence
		StringBuilder regularExpression;  //regular exprssion used in the matcher.  convenience for debugging.
		String[] words;   //this is the words in the text, split apart by whitespace
		String[] pos;     //this is the part of speech we are looking for
		String[] correctedPOS;  //what to replace each of the POS with ...
	}
	
	ArrayList<OverrideTag> _overrides;
	
	public static CorrectPOSTags _theCorrectPOSTagsAnnonotator = null;
	
	public static CorrectPOSTags getThePOSTagsAnnotator() {
		return _theCorrectPOSTagsAnnonotator;
	}
	
	public CorrectPOSTags(String s, Properties p) {
		//System.out.println(s);  // This is the name of the annotator in the pipeline
		//p.list(System.out);     // this is the properties used to construct the pipeline
		//ideally, there should be a custom property for this annotator that points to a file to utilize to
		//overwrite tags.
		// another property for verboisty.  as well as one for dumping the list to stderr at startup
		_theCorrectPOSTagsAnnonotator = this;
		
		initialize();
	}
	
	public void setOverrides(java.util.List<String> newOverrideLines) {
		_lines = newOverrideLines;
		this.initialize();
	}
	
	public java.util.List<String> getOverrides() {
		return new java.util.ArrayList<>(_lines);
	}
	
	public void initialize() {
		_overrides = new ArrayList<OverrideTag>();
		for (String s: _lines) {
			String[] lineTokens = s.split("\t"); 
			if (lineTokens.length !=3 ) {
				System.err.println("Warning incorrect token count - ignorning: "+s);
				continue;
			}
			
			OverrideTag ot = new OverrideTag();
			ot.text = lineTokens[0];
			ot.words = lineTokens[0].split("\\s");  //split the text to match by words
			
			ot.regularExpression = new StringBuilder("");
			for (int i=0;i<ot.words.length;i++) {
				if (i > 0) {
					ot.regularExpression.append("(\\s)+"); //adds the space between words
				}
				if (ot.words[i].equals("%")) {
					ot.regularExpression.append("(\\S)+"); //match any word.  Word is defined as non whitespace
				}
				else {
					ot.regularExpression.append(ot.words[i]);
				}
			}
			//System.out.println(ot.regularExpression);
			ot.pattern = Pattern.compile(ot.regularExpression.toString());
			ot.pos = lineTokens[1].split(" ");
			ot.correctedPOS = lineTokens[2].split(" ");
			_overrides.add(ot);
		}		
	}
	
	private boolean isMatchWithWordsAndTokens(int startIndex, ArrayList<CoreLabel> tokens, String words[] , String[] pos ) {
		boolean result = true;
		int endIndex = startIndex+pos.length;
		
		for (int i=startIndex;i<endIndex;i++) {
			int indexCorrect = i-startIndex;
			if (words[indexCorrect].equals("%") == false) { // need to test the word to match
				String word = tokens.get(i).get(TextAnnotation.class);
				
				if (word.equalsIgnoreCase(words[indexCorrect]) == false) {
					return false;
				}
			}
			if (pos[indexCorrect].equals("%") == false) { // need to test the pos to match
				if (tokens.get(i).get(PartOfSpeechAnnotation.class).equalsIgnoreCase(pos[indexCorrect]) == false) {
					return false;
				}
			}			
		}
		return result;
	}
	
	public void annotate(Annotation a) {
		this.overrideAnnotations(a, 0);
	}
	
	private void overrideAnnotations(Annotation a, int depth) {
		if (depth > 5) { return; }
		
  	    java.util.List<CoreMap> sentences = a.get(SentencesAnnotation.class);
  	    
  	    boolean change = false;
  	    
  	    for (OverrideTag ot: _overrides) {
  	    	Matcher matcher = ot.pattern.matcher("");
  	    	
  	    	for(CoreMap sentence: sentences) {
  	    		String text = sentence.get(TextAnnotation.class);	
  	    		matcher = matcher.reset(text); //technically it returns itself
  	    		if (!matcher.find()) { continue; } // didn't find the pattern in this sentence, skip
  	    			
  	    		
  	    		ArrayList<CoreLabel> tokens = new ArrayList<CoreLabel>(sentence.get(TokensAnnotation.class));
  	    		int numberOfTokens = tokens.size();
  	    		int numberOfCorrectionTokens = ot.words.length;
  	    		for (int i=0; i<numberOfTokens-numberOfCorrectionTokens;i++) {
  	    			
  	    			//test if there is a match here. compare words and the tokens
  	    			if (isMatchWithWordsAndTokens(i, tokens, ot.words, ot.pos)) {
  	    				
  	    				for (int j=0;j<numberOfCorrectionTokens;j++) {      //perform replacement
  	    					if (ot.correctedPOS[j].equals("%") == false) {  //we won't replace wildcards
  	    						tokens.get(i+j).setTag(ot.correctedPOS[j]);
  	    						change = true;
  	    					}
  	    				}
  	    				//i += numberOfCorrectionTokens; //we can skip until after this replacement
  	    			}
  	    		}
  	    	}
		}
  	   if (change) { this.overrideAnnotations(a,depth+1); } // if changes are made, allow multiple ones to occur.  	
	}

	@Override
	public Set<Requirement> requirementsSatisfied() {
		// TODO Auto-generated method stub
		return Annotator.TOKENIZE_SSPLIT_POS;
	}

	@Override
	public Set<Requirement> requires() {
		return Annotator.TOKENIZE_SSPLIT_POS;
	}
}

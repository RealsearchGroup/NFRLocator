package edu.ncsu.csc.nl.model;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ncsu.csc.nl.model.english.MiscCheck;



/**
 * This class is used to pre-process text prior to the text being sent to
 * the Stanford NLP
 * 
 *  Input lines should be regular expressions in the format of
 *  	textToMatch\tTextToReplace
 * 
 * @author John
 *
 */
public class TextProcessor  {
	String[] _lines = {"24/7\t24 by 7",
					   "incl\\.\tincluding",
			           "24[ ]*[xX][ ]*7\t24 by 7",  //converts 24x7 to 24 by 7 with optional spaces.
			           "X\\.X\tXdotX",
			           "w/out\twithout", //replaces "w/out" with "without".  not a recognized word otherwise
			           "w/in\twithin",   //replaces "w/in" with "within".
			           "w/ \twith ",       //replaces "w/" with "with".  note: could have probably just gotten away with this one
			           "and/or\tpossibly and possibly",
			           "n/a\tnot applicable",
			           "N/a\tNot applicable",
			           "N/A\tnot applicable",
			           "c/o\tcare of",
			           "vs\\.\tversus",
			           "\"\"\t'", // replaces two double quotes next to each other with just a single quote
			           "\\[.*?\\]\t",
			           "/\t or "
			           }; 
	
	
	String[] _stopWords = {"http","mm/dd/yyyy"};
	
	class SearchAndReplaceTag {
		String findText;  // this is the text to match
		Pattern findPattern;  //this is the regular expression for the text to find in a sentence
		
		String replaceText;
	}
	
	ArrayList<SearchAndReplaceTag> _changes;
	
	
	public TextProcessor() {
		initialize();
	}
	
	public void initialize() {
		_changes = new ArrayList<SearchAndReplaceTag>();
		for (String s: _lines) {
			String[] lineTokens = s.split("\t");  // should have a check that this is 3. if not, dump the error message
			
			SearchAndReplaceTag c = new SearchAndReplaceTag();
			c.findText = lineTokens[0];
			if (lineTokens.length == 1) {
				c.replaceText="";
			}
			else {
				c.replaceText = lineTokens[1];
			}
			c.findPattern = Pattern.compile(c.findText);

			_changes.add(c);
		}		
	}
	
	private boolean isStopWord(String word) {
		word = word.toLowerCase();
		for (String s: _stopWords) {
			if (word.contains(s)) {
				return true;
			}
		}
		return false;
	}
	
	public String preProcessLine(String s) {
		s = MiscCheck.replaceFileTypesWithZDOTZ(s);  // replaces any files ending in a common prefix with "ZDOTZprefix"
		
		// replace any sentence that begins with a number, then a period, and then a space to be "x) "
		String pattern = "^(\\d+)(\\.) ";
		s = s.replaceAll(pattern, "$1) "); 
		
		s = MiscCheck.replaceEmbeddedPeriodsWithZDOTZ(s);
		
		// check other matches
		for (SearchAndReplaceTag srt: _changes) {
			Matcher m = srt.findPattern.matcher(s);
			
			if (m.find()) {
				if (this.isStopWord(s.substring(m.regionStart(), m.regionEnd()))) {
					continue;
				}
			}
			
			s = m.replaceAll(srt.replaceText);				
		}
		return s;
		
	}
	public String postProcessLine(String s) {
		s =  s.replaceAll("ZDOTZ", ".");
		return s;
	}
}

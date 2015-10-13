package edu.ncsu.csc.nl.model.english;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiscCheck {
	private static String  _cfrTitlePattern = "^(§)(\\s)*([0-9])+\\.([0-9])+";
	private static Pattern _cfrPattern = Pattern.compile(_cfrTitlePattern);
	private static String  _lineIdentiferPattern = "^((((-?|\\*|•|○|o)\\s+)|(\\(?)([0-9]+|[A-Z]+|[a-z]+|[ivx]+|[IVX]+)(\\.([0-9]+|[A-Z]+|[a-z]+|[ivx]+|[IVX]+))*(\\.|\\))\\s+)|(([0-9]+)(\\.[0-9])*\\s+))";
	private static Pattern _lineIDPattern = Pattern.compile(_lineIdentiferPattern);
	
	private static String[] _minorWords = {"a","an","and","the","amid","as","at","atop","by","but","due","from","for","if","in","into","less","like","near","of","off","on","onto","or","over","out","past","per","re","than","that","thru","till","to","up","upon","via","with","within","without"};

	private static String[] _commonFileSuffixes = { "con","conf","csv","doc","docx","ini","java","jsp","pdf","ppt","pptx","rtf","txt", "xls","xlsx"};  
	
	private static Pattern _fileTypePattern;
	private static Pattern _fileTypeWithDotPattern;
	static {
		String combined = "";
		for (String s: _commonFileSuffixes) {
			if (combined.length() > 0) {
				combined += "|";
			}
			combined += s;
		}
		for (String s: _commonFileSuffixes) {
			combined +=  ("|"+s.toUpperCase());
		}
		
		_fileTypePattern = Pattern.compile("(\\.|ZDOTZ|)("+combined+")(\\b|$)");
		_fileTypeWithDotPattern  = Pattern.compile("(\\.)("+combined+")\\s");
		
	}
	
	private static HashSet<String> _minorWordSet = new HashSet<>(Arrays.asList(_minorWords));

	private static String extractFoundPortion(String s, Pattern p) {
		s = s.trim();
		Matcher matcher = p.matcher(s);
		if (matcher.find()) {
			return s.substring(matcher.start(),matcher.end());
		}
		else {
			return null;
		}
	}
	private static String extractPortionAfterFound(String s, Pattern p) {
		s = s.trim();
		Matcher matcher = p.matcher(s);
		if (matcher.find()) {
			return s.substring(matcher.end());
		}
		else {
			return null;
		}
	}	
	public static boolean isMinorWord(String word) {
		word = word.toLowerCase();
		return _minorWordSet.contains(word);
	}
	
	public static boolean hasLineIdentifier(String s) {
		 s = s.trim();
		 Matcher matcher = _lineIDPattern.matcher(s);
		 return matcher.find();		
	}
	
	public static String extractLineWithoutID(String s) {
		return MiscCheck.extractPortionAfterFound(s, _lineIDPattern);
	}
	
	public static String extractLineID(String s) {
		return MiscCheck.extractFoundPortion(s, _lineIDPattern);
	}	
	
	public static String extractCFRid(String s) {
		return MiscCheck.extractFoundPortion(s, _cfrPattern);
	}
	
	public static String extractCFRTitle(String s) {
		return MiscCheck.extractPortionAfterFound(s, _cfrPattern);
	
	}
	
	public static boolean isCFRTitle(String s) {
		 s = s.trim();
		 Matcher matcher = _cfrPattern.matcher(s);
		 return matcher.find();
	}

	public static boolean isTitle(String s) {
		s = s.trim();
		boolean foundUpperCase = false;	
		
		String[] words = s.split("\\s");
		for (int i=0;i<words.length;i++) {
			String w = words[i].trim();
			if (w.length() == 0) { continue; }
			if (Character.isLowerCase(w.charAt(0))) {
				if (!MiscCheck.isMinorWord(w)) {
					return false;
				}
			}
			if (Character.isUpperCase(w.charAt(0))) {
				foundUpperCase = true;
			}
		}
		
		return foundUpperCase;
	}	
	
	public static boolean startsList(String s) {
		s =s.trim();
		return (s.endsWith(":")||s.endsWith("-"));
	}
	
	public static boolean hasFileType(String s) {
		s = s.trim();
		Matcher matcher = _fileTypePattern.matcher(s);
		return matcher.find();
	}
	public static String replaceFileTypesWithZDOTZ(String s) {
		String result = "";
		int lastIndex = 0;
		
		Matcher matcher = _fileTypePattern.matcher(s);
		while (matcher.find()) {
			 result += s.substring(lastIndex,matcher.start());
			 if (s.charAt(matcher.start()) == '.') {
				 result += "ZDOTZ";
				 result += s.substring(matcher.start()+1,matcher.end());
			 }

			 lastIndex = matcher.end();
		}
		result += s.substring(lastIndex,s.length());
		return result;
	}	
	
	public static String replaceEmbeddedPeriodsWithZDOTZ(String str) {
		String regex = "[a-zA-Z]{2,}(\\.[a-zA-Z_0-9]{2,})+";
		
		Pattern pattern = Pattern.compile(regex);

		String result = "";
		int lastIndex = 0;
		
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			 result += str.substring(lastIndex,matcher.start());
			 result += str.substring(matcher.start(),matcher.end()).replaceAll("\\.", "ZDOTZ");
			 lastIndex = matcher.end();
		}
		result += str.substring(lastIndex,str.length());	
		
		return result;
	}
	
	private static String[] _readVerbArray = {"read","view","display","show","share","transfer","access","scan","show","examine","see","query","print","search","filter","sort"};
	private static HashSet<String> _readVerbs = new HashSet<String>();
	static {
		for (String s: _readVerbArray) { _readVerbs.add(s); }
	};
	
	private static String[] _writeVerbArray = {"create","update","delete","approve","append","write","capture",
		                                       "store","finalize","maintain","remove"};
	private static HashSet<String> _writeVerbs = new HashSet<String>();
	static {
		for (String s: _writeVerbArray) { _writeVerbs.add(s); }
	};	
	
	public static boolean isReadVerb(String s) {
		return _readVerbs.contains(s);
	}
	
	public static boolean isWriteVerb(String s) {
		return _writeVerbs.contains(s);
	}	
	

	
}

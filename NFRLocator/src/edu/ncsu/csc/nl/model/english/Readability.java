package edu.ncsu.csc.nl.model.english;

import java.util.List;

import edu.ncsu.csc.nl.model.Sentence;

public class Readability {

	//check if a char is a vowel (count y)
	public static boolean isVowel(char c) {
	    if      ((c == 'a') || (c == 'A')) { return true;  }
	    else if ((c == 'e') || (c == 'E')) { return true;  }
	    else if ((c == 'i') || (c == 'I')) { return true;  }
	    else if ((c == 'o') || (c == 'O')) { return true;  }
	    else if ((c == 'u') || (c == 'U')) { return true;  }
	    else if ((c == 'y') || (c == 'Y')) { return true;  }
	    else                               { return false; }
	  }
	
	public static int countSyllables(String word) {
	    int      syl    = 0;
	    boolean  vowel  = false;
	    int      length = word.length();

	    //check each word for vowels (don't count more than one vowel in a row)
	    for(int i=0; i<length; i++) {
	      if        (isVowel(word.charAt(i)) && (vowel==false)) {
	        vowel = true;
	        syl++;
	      } else if (isVowel(word.charAt(i)) && (vowel==true)) {
	        vowel = true;
	      } else {
	        vowel = false;
	      }
	    }

	    char tempChar = word.charAt(word.length()-1);
	    //check for 'e' at the end, as long as not a word w/ one syllable
	    if (((tempChar == 'e') || (tempChar == 'E')) && (syl != 1)) {
	      syl--;
	    }
	    return syl;
	}
	
	public static double getFleschReadingEaseTestScore(List<Sentence> sentences, String classification) {
		double numSentences = 0.0;
		double totalNumberOfWords = 0.0;
		double totalNumberOfSyllables = 0.0;
		
		for (Sentence s: sentences) {
			if (s.hasBooleanClassification(classification)) {
				numSentences += 1;
				totalNumberOfWords += s.getNumberOfNodes();
				
				for (int i = 0; i < s.getNumberOfNodes(); i++) {
					totalNumberOfSyllables += countSyllables(s.getWordVertexAt(i).getOriginalWord());
				}
				
			}
		}		
		return 206.835 - (1.015 * (totalNumberOfWords/numSentences)) - (84.6 * totalNumberOfSyllables/totalNumberOfWords);
	}
	
	public static double getFleschReadingEaseTestScoreBySentence(Sentence sentence) {
		int numWords = sentence.getNumberOfNodes();
		double totalSyllables = 0;
		for (int i=0;i<numWords;i++) {
			totalSyllables += countSyllables(sentence.getWordVertexBySortedPosition(i).getOriginalWord());
		}
		return 206.835 - (1.015 * numWords) - (84.6 * totalSyllables/((double) numWords) );
	}
	
	public static double getFleshKincaidGradeLevel(List<Sentence> sentences, String classification) {
		double numSentences = 0.0;
		double totalNumberOfWords = 0.0;
		double totalNumberOfSyllables = 0.0;
		
		for (Sentence s: sentences) {
			if (s.hasBooleanClassification(classification)) {
				numSentences += 1;
				totalNumberOfWords += s.getNumberOfNodes();
				
				for (int i = 0; i < s.getNumberOfNodes(); i++) {
					totalNumberOfSyllables += countSyllables(s.getWordVertexAt(i).getOriginalWord());
				}
				
			}
		}		
		return  (0.39 * (totalNumberOfWords/numSentences)) + (11.8 * totalNumberOfSyllables/totalNumberOfWords) - 15.59;
	}
	
	public static double getFleshKincaidGradeLevelBySentence(Sentence sentence) {
		int numWords = sentence.getNumberOfNodes();
		double totalSyllables = 0;
		for (int i=0;i<numWords;i++) {
			totalSyllables += countSyllables(sentence.getWordVertexBySortedPosition(i).getOriginalWord());
		}
		
		return .39  + (11.8 * totalSyllables/numWords) - 15.59;
	}	
}

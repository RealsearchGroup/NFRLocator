package edu.ncsu.csc.nl.model.english;

import edu.ncsu.csc.nl.model.WordVertex;

public class Abbreviation {

	
	public static String checkAbbreviation(String sentenceToCheck, WordVertex abbreviationNode) {
		WordVertex parent = abbreviationNode.getParentAt(0).getParentNode(); //TODO: Fix this logic.  Need to check all parents and only use the one that is an abbreviation.

		//wv.getLemma is the abbrevation.   go through each letter and check that the letter is in the orginal sentence starting at the minimum index)
		String abbreviationToCheck = abbreviationNode.getLemma().toLowerCase();
		
		int startIndex = parent.getSmallestIndex();
		int saveStartIndex = startIndex;
		int lastPossibleIndex = abbreviationNode.getStartIndexOfWordInSentence();
		boolean isGood = true;
		for (int i=0;i<abbreviationToCheck.length();i++) {
			int checkIndex = sentenceToCheck.indexOf(abbreviationToCheck.charAt(i),startIndex);
			if (checkIndex < 0 || checkIndex>= lastPossibleIndex) {
				isGood=false;
				break;
			}
			startIndex = checkIndex;
		}
		
		if (isGood) {
			if (lastPossibleIndex > sentenceToCheck.length()) { lastPossibleIndex = sentenceToCheck.length(); } //TODO: may not be correct!  need to review code
			String word = sentenceToCheck.substring(saveStartIndex, lastPossibleIndex-1 );
			if (word.startsWith(abbreviationToCheck.substring(0,1)) == false){
				word = word.substring(word.indexOf(abbreviationToCheck.charAt(0)));  //Todo: make this find the first index
			}
			return word; // this was the matched word
		}			
		else {
			return null; // no valid index found for the entry
		}
		
	}
}

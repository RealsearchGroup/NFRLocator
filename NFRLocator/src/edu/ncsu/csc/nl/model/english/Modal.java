package edu.ncsu.csc.nl.model.english;

import edu.ncsu.csc.nl.model.Sentence;
import edu.ncsu.csc.nl.model.WordVertex;
import edu.ncsu.csc.nl.model.type.PartOfSpeech;

public class Modal {
	
	
	/**
	 * Checks to see if the sentence contains a modal verb, followed by another word, and then a verb.
	 * If so, it moves that other word to be in front of the modal verb.  This corrects parse tree errors
	 * when the modal verb appears to relate to the middle word.  (The parser tends to make it the root).
	 * 
	 * Test notes: had difficulting parsing sentences with multiple spaces
	 *             what happens if there are multiple modifiers between the modal verb and a verb?
	 * 
	 * @param s
	 * @return
	 */
	/*
	public static String checkModalVerbForModifier(Sentence s) {
		String result = s.getSentence();
		int maxNodeCheck = s.getNumberOfNodes() -2;
		WordVertex[] words = new WordVertex[s.getNumberOfNodes()];
		for (int i=0; i<  s.getNumberOfNodes(); i++) {
			words[i] = s.getWordVertexBySortedPosition(i);
		}
		int numchanges = 0;
		for (int i=0; i< maxNodeCheck; i++) {		
			if (s.getWordVertexBySortedPosition(i).getPartOfSpeech().equals(PartOfSpeech.MD) &&s.getWordVertexBySortedPosition(i+2).getPartOfSpeech().equals(PartOfSpeech.VB)) {
				String previousSentence = result;
				result = previousSentence.substring(0,s.getWordVertexBySortedPosition(i).getStartIndexOfWordInSentence() + numchanges);
				result +=s.getWordVertexBySortedPosition(i+1).getOriginalWord()+ " " + s.getWordVertexBySortedPosition(i).getOriginalWord();
				result += previousSentence.substring(s.getWordVertexBySortedPosition(i+1).getEndIndexOfWordInSentence()+ numchanges);
				numchanges++;
			}
		}
		
		return result;
	}
	*/
}

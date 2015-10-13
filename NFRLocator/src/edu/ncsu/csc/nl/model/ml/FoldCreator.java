package edu.ncsu.csc.nl.model.ml;

import java.security.SecureRandom;
import java.util.ArrayList;

import edu.ncsu.csc.nl.model.Sentence;

public class FoldCreator {


	private static int countNumberOfSentencesByClassification(java.util.ArrayList<Sentence> sentences, String clasification) {  //TODO: Should this include the test set?
		int result =0;
		
		for (Sentence s: sentences) {
			if (s.hasBooleanClassification(clasification)) {
				result++;
			}
		}
		return result;
	}
	
	private static java.util.Set<String> computeUniqueDocumentIDs(java.util.ArrayList<Sentence> sentences) {
		java.util.HashSet<String> result = new java.util.HashSet<String>();
		
		for (Sentence s: sentences) {
			result.add(s.getDocumentID());
		}
		
		return result;
	}	
	
	public static java.util.ArrayList<java.util.ArrayList<Sentence>> createFolds(java.util.ArrayList<Sentence> sentences, String primaryClassification, int numberOfFolds) {
		int numberOfTrainedSentences   = sentences.size();
		int numberOfPrimarySentences    = countNumberOfSentencesByClassification(sentences, primaryClassification);
		int numberOfNonPrimarySentences = numberOfTrainedSentences-numberOfPrimarySentences;
		double primaryClassificationRatio = numberOfPrimarySentences/(((double) numberOfNonPrimarySentences) +numberOfPrimarySentences);
		
		SecureRandom random = new SecureRandom();
		//Now, lets set up the folds.  These should be stratified, and randomized
		//duplicate copy of our Array.
		java.util.ArrayList<Sentence> unPlacedSentences = new java.util.ArrayList<Sentence>(sentences);
		
		int foldSize = numberOfTrainedSentences / numberOfFolds;
		int foldsWithExtraMemberBelowIndex = numberOfTrainedSentences % numberOfFolds;
		
		java.util.ArrayList<java.util.ArrayList<Sentence>> folds = new java.util.ArrayList<java.util.ArrayList<Sentence>>();
		for (int i=0;i<numberOfFolds;i++) {
			java.util.ArrayList<Sentence> fold = new java.util.ArrayList<Sentence>();
			folds.add(fold);
			
			int numberOfElementsRemainingToPutinFold = foldSize;
			if (i < foldsWithExtraMemberBelowIndex) { numberOfElementsRemainingToPutinFold++;}
			
			int expectNumberOfPrimaryLeft    = (int) ( foldSize * primaryClassificationRatio +1);
			int expectNumberOfNonPrimaryLeft = (int) (foldSize * (1.0-primaryClassificationRatio) +1);

			//System.out.println(i+": "+numberOfElementsRemainingToPutinFold+"\t"+expectNumberOfAccessLeft+"\t"+expectNumberOfNonAccessLeft); //sanity check on numbers
			//now randomly place trained sentences into the folds
			int numberOfTries =0;
			while (numberOfElementsRemainingToPutinFold > 0) {
				//get a random element form the unplaced sentences and see if we can use it.
				int indexToCheck = random.nextInt(unPlacedSentences.size());
				
				Sentence s = unPlacedSentences.get(indexToCheck);
				if (numberOfTries > 1000) {
					fold.add(s);
					numberOfElementsRemainingToPutinFold--;
					unPlacedSentences.remove(indexToCheck);
					continue;					
				}
				
				if (s.hasBooleanClassification(primaryClassification) && expectNumberOfPrimaryLeft >0) {
					fold.add(s);
					expectNumberOfPrimaryLeft--;
					numberOfElementsRemainingToPutinFold--;
					unPlacedSentences.remove(indexToCheck);
					continue;
				}
				else if (!s.hasBooleanClassification(primaryClassification) && expectNumberOfNonPrimaryLeft >0) {
					fold.add(s);
					expectNumberOfNonPrimaryLeft--;
					numberOfElementsRemainingToPutinFold--;
					unPlacedSentences.remove(indexToCheck);
					continue;
				}
				else {
					numberOfTries++;
				}
				
			}
		}		
		
		return folds;
	}
	
	public static java.util.ArrayList<java.util.ArrayList<Sentence>> createFoldsByDocumentID(java.util.ArrayList<Sentence> sentences) {
		java.util.List<String> uniqueDocumentIDs = new ArrayList<String>(computeUniqueDocumentIDs(sentences));
		int numberOfFolds = uniqueDocumentIDs.size();;

		java.util.ArrayList<java.util.ArrayList<Sentence>> folds = new java.util.ArrayList<java.util.ArrayList<Sentence>>();
		for (int i=0;i<numberOfFolds;i++) {
			java.util.ArrayList<Sentence> fold = new java.util.ArrayList<Sentence>();
			folds.add(fold);
			
			String currentDocumentID = uniqueDocumentIDs.get(i);
			for (Sentence s: sentences) {
				if (s.getDocumentID().equals(currentDocumentID)) {
					fold.add(s);
				}
			}
		}
		return folds;
	}
}

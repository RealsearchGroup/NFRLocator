package edu.ncsu.csc.nl.model.ml;

import java.security.SecureRandom;
import java.util.ArrayList;

public class DocumentFoldCreator {


	
	public static int countNumberOfSentencesByClassification(java.util.ArrayList<Document> documents, String clasification) {  //TODO: Should this include the test set?
		int result =0;
		
		for (Document d: documents) {
			if (d.sentence.hasBooleanClassification(clasification)) {
				result++;
			}
		}
		return result;
	}
	
	public static java.util.Set<String> computeUniqueDocumentIDs(java.util.ArrayList<Document> documents) {
		java.util.HashSet<String> result = new java.util.HashSet<String>();
		
		for (Document d: documents) {
			result.add(d.sentence.getDocumentID());
		}
		
		return result;
	}	
	
	public static java.util.ArrayList<java.util.ArrayList<Document>> createFolds(java.util.ArrayList<Document> documents, String primaryClassification, int numberOfFolds) {
		int numberOfTrainedSentences   = documents.size();
		int numberOfPrimarySentences    = countNumberOfSentencesByClassification(documents, primaryClassification);
		int numberOfNonPrimarySentences = numberOfTrainedSentences-numberOfPrimarySentences;
		double primaryClassificationRatio = numberOfPrimarySentences/((double) numberOfNonPrimarySentences);
		
		SecureRandom random = new SecureRandom();
		//Now, lets set up the folds.  These should be stratified, and randomized
		//duplicate copy of our Array.
		java.util.ArrayList<Document> unPlacedSentences = new java.util.ArrayList<Document>(documents);
		
		int foldSize = numberOfTrainedSentences / numberOfFolds;
		int foldsWithExtraMemberBelowIndex = numberOfTrainedSentences % numberOfFolds;
		
		java.util.ArrayList<java.util.ArrayList<Document>> folds = new java.util.ArrayList<java.util.ArrayList<Document>>();
		for (int i=0;i<numberOfFolds;i++) {
			java.util.ArrayList<Document> fold = new java.util.ArrayList<Document>();
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
				
				Document d = unPlacedSentences.get(indexToCheck);
				if (numberOfTries > 1000) {
					fold.add(d);
					numberOfElementsRemainingToPutinFold--;
					unPlacedSentences.remove(indexToCheck);
					continue;					
				}
				
				if (d.sentence.hasBooleanClassification(primaryClassification) && expectNumberOfPrimaryLeft >0) {
					fold.add(d);
					expectNumberOfPrimaryLeft--;
					numberOfElementsRemainingToPutinFold--;
					unPlacedSentences.remove(indexToCheck);
					continue;
				}
				else if (!d.sentence.hasBooleanClassification(primaryClassification) && expectNumberOfNonPrimaryLeft >0) {
					fold.add(d);
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
	
	public static java.util.ArrayList<java.util.ArrayList<Document>> createFoldsByDocumentID(java.util.ArrayList<Document> documents) {
		java.util.List<String> uniqueDocumentIDs = new ArrayList<String>(computeUniqueDocumentIDs(documents));
		int numberOfFolds = uniqueDocumentIDs.size();;

		java.util.ArrayList<java.util.ArrayList<Document>> folds = new java.util.ArrayList<java.util.ArrayList<Document>>();
		for (int i=0;i<numberOfFolds;i++) {
			java.util.ArrayList<Document> fold = new java.util.ArrayList<Document>();
			folds.add(fold);
			
			String currentDocumentID = uniqueDocumentIDs.get(i);
			for (Document d: documents) {
				if (d.sentence.getDocumentID().equals(currentDocumentID)) {
					fold.add(d);
				}
			}
		}
		return folds;
	}
	
	/**
	 * Create a single fold to evaluate based upon the first x% of a document.
	 * The contents of a fold are what gets created.
	 * 
	 * So 10% has 10% for training and 90% for test
	 * 
	 * @param documents
	 * @param percent
	 * @return
	 */
	public static java.util.ArrayList<java.util.ArrayList<Document>> createFoldsByPercentage(java.util.ArrayList<Document> documents, double percentInTraining) {
		int numberOfFolds = 1;

		java.util.ArrayList<java.util.ArrayList<Document>> folds = new java.util.ArrayList<java.util.ArrayList<Document>>();
		for (int i=0;i<numberOfFolds;i++) {
			java.util.ArrayList<Document> fold = new java.util.ArrayList<Document>();
			folds.add(fold);
			
			int maxSentenceNumber = (int) ( documents.size() * percentInTraining);
			maxSentenceNumber = Math.min(maxSentenceNumber, documents.size()); // make sure we stay in bounds!
			
			for (int docNum=maxSentenceNumber; docNum < documents.size(); docNum++) {
				fold.add(documents.get(docNum));
			}
		}
		return folds;
	}
	
}

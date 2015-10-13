package edu.ncsu.csc.nl.model.ml;


//import java.nio.charset.Charset;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ncsu.csc.nl.GCConstants;
import edu.ncsu.csc.nl.GCController;
import edu.ncsu.csc.nl.event.NLPEventClassification;
import edu.ncsu.csc.nl.event.NLPEventManager;
import edu.ncsu.csc.nl.event.NLPEventType;
import edu.ncsu.csc.nl.model.NLDocument;
import edu.ncsu.csc.nl.model.Sentence;
import edu.ncsu.csc.nl.model.classification.BooleanClassification;
import edu.ncsu.csc.nl.model.classification.ClassificationAttribute;
import edu.ncsu.csc.nl.model.classification.ClassificationType;
import edu.ncsu.csc.nl.model.distance.CosineTermFreqIDFDistance;
import edu.ncsu.csc.nl.model.distance.LevenshteinSentenceAsWordsDistance;
import edu.ncsu.csc.nl.model.distance.SentenceDistance;
import edu.ncsu.csc.nl.model.naivebayes.NaiveBayesClassifier;
import edu.ncsu.csc.nl.model.type.BooleanType;
import edu.ncsu.csc.nl.model.type.Source;
import edu.ncsu.csc.nl.util.Logger;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class InstanceLearner {
	
	private ArrayList<Document> _trainedSentences = new ArrayList<Document>();
	private HashMap<String, Double> _inverseDocumentFrequency = null;  // lazily-initialized hashMap of inverse document frequencies
	
	public int getNumberOfTrainedSentences() {
		return _trainedSentences.size();
	}
	
	public Document getTrainedDocumentAt(int pos) {
		return _trainedSentences.get(pos);
	}

	public Sentence getTrainedSentenceAt(int pos) {
		return _trainedSentences.get(pos).sentence;
	}
	
	
	public void addTrainedSentence(Sentence s) {
		// Don't add sentences that are already here.
		for (Document d: _trainedSentences) {
			if (d.sentence.equals(s)) {
				return;
			}
		}		
		
		Document d = new Document();
		d.documentID = s.getDocumentID();
		d.sentence   = s;
		d.inTest     = false;
		d.unlabeledTraining = false;
		_trainedSentences.add(d);
		_inverseDocumentFrequency = null;
	}
	
	public void addTrainedDocument(Document d) {
		_trainedSentences.add(d);
		_inverseDocumentFrequency = null;
	}
	
	public void removeTrainedSentence(Sentence s) {
		for (Document d: _trainedSentences) {
			if (d.sentence.equals(s)) {
				_trainedSentences.remove(d);
			}
		}
		_inverseDocumentFrequency = null;
	}	
	
	public void clearTrainedSentence() {
		_trainedSentences.clear();
		_inverseDocumentFrequency = null;
	}	
	
	public ArrayList<Document> getTrainedSentences() {
		return _trainedSentences;
	}
	
	/**
	 * returns an array of the actual sentences in the learner, rather than the surrounded by the Document object
	 * @return
	 */
	public ArrayList<Sentence> getTrainedSentencesActual() {
		ArrayList<Sentence> sentences = new ArrayList<Sentence>();

		for (Document d: this.getTrainedSentences()) {
			sentences.add(d.sentence);
		}
		return sentences;
	}

	public static class IntermediateResults implements Comparable<IntermediateResults> {
		public int pos;
		public double distance;
		
		public IntermediateResults(int p, double d) {
			pos = p;
			distance = d;
		}
		
		public int compareTo(IntermediateResults o) {
			return Double.compare(this.distance, o.distance);
		}
		
	}
	
	
	protected IntermediateResults[] computeNeighborDistances(Sentence testSentence, SentenceDistance distanceAlgorithm, boolean skipTestSentences, boolean verbose, int k) {
		IntermediateResults[] distances = new IntermediateResults[_trainedSentences.size()];
		for (int i=0;i<_trainedSentences.size();i++) {
			if (skipTestSentences && _trainedSentences.get(i).inTest) {
				distances[i] = new IntermediateResults(i,Double.MAX_VALUE);
			}
			else {
				double distance = distanceAlgorithm.computeDistance(_trainedSentences.get(i).sentence, testSentence);
				distances[i] = new IntermediateResults(i,distance);
			}
		}
		java.util.Arrays.sort(distances);
		if (verbose) {
			System.out.println("--------------------------");
			System.out.println("k="+k +" distance from ("+distanceAlgorithm.getMethodName()+"): "+testSentence.getSentence());
			for (int i=distances.length-1; i>=0; i--) {
				System.out.println(i+") "+distances[i].distance+": ("+distances[i].pos+") "  +_trainedSentences.get(distances[i].pos).sentence);
			}
		}
		
		return distances;
	}
	
	
	protected int computeLastIndex(IntermediateResults[] distances, int k) {
		int resultIndex = Math.max(Math.min(k, distances.length) -1,0); 
		
		double lastDistanceSeen = distances[resultIndex].distance;
		while (resultIndex < (distances.length-1)) {
			if (lastDistanceSeen ==  distances[resultIndex+1].distance) {
				resultIndex++;
			}
			else {
				break;
			}
		}
		return resultIndex;
	}
	
	protected void tallyClassificationVotes(Sentence s, HashMap<String, Integer> votes) {
		HashMap<String, ClassificationType> sentenceClassifications = s.getClassifications();
		for (String key: sentenceClassifications.keySet()) {
			Object value = sentenceClassifications.get(key).getValue();
			
			incrementHashMapValueStringKey(votes,getClassificationKey(key, value));
		}
	}
	
	protected void findClassificationMajority(HashMap<String, Integer> votes, ClassificationResult result) {
		for (ClassificationAttribute ca: GCController.getTheGCController().getClassificationAttributes().getAttributeList()) {  //use the first result to get the list of classification
			//TODO: This probably doesn't work for types other boolean .... need to fix
			result.classifications.put(ca.getName(), getBooleanClassificationOfMaximumValue(votes,ca.getName()));
		}		
	}
	

	/**
	 * Computes the votes of both the classification items currently setup for the system as
	 * well as the SecurityAnnotations that are in place.
	 * 
	 * The HashMap values are the approriate votes.  The keys follow one of the following patterns:
     * 	   classification_(yes|no)
	 *     SecuritObjectiveAbbr_(yes|no)
     *     SOAbbr_impact_(high|moderation|low|not applied)
     *     SOAbbr_implied_(true|false)
     *     SOAbbr_mitigation_(PREVENTION|DETECTION|REACTION|ADAPTATION)
	 * 
	 * Additionally, the result object is updated with the final value of k used, along with the average distance
	 * 
	 * @param distances
	 * @param k
	 * @param result
	 * @return
	 */
	protected HashMap<String, Integer> computeClassificationVotes(IntermediateResults[] distances, int k, ClassificationResult result) {

		HashMap<String, Integer> votes = new HashMap<String, Integer>();
		int finalSortedIndexMax = this.computeLastIndex(distances, k);

		double distanceSum = 0;
		for (int i=0; i<=finalSortedIndexMax; i++) {
			Sentence s = _trainedSentences.get(distances[i].pos).sentence;
			tallyClassificationVotes(s,votes);
			distanceSum += distances[i].distance;
		}		
		result.averageDistance  =   ((int) (distanceSum * 100.0 /(finalSortedIndexMax+1)))/100.0;
		result.k = finalSortedIndexMax+1;
		
		return votes;
	}
	
	
	protected void configureCosineTermFreqIDFDistance(CosineTermFreqIDFDistance distanceAlgorithm,  boolean skipTestSentences) {
		((CosineTermFreqIDFDistance) distanceAlgorithm).setInverseDocumentFrequncy(this.generateInverseDocumentFrequency(this.generateTermSentenceFrequency(skipTestSentences),skipTestSentences));

		int numberOfDocuments = _trainedSentences.size();
		if (skipTestSentences) {
			numberOfDocuments -= this.countNumberOfSentencesInTesting();
		}		
		((CosineTermFreqIDFDistance) distanceAlgorithm).setNumberOfSentences(numberOfDocuments);
	}
	
	/**
	 * For the given testSentence, compute its classification using a kNN classifier with the 
	 * specified distance algorithm.
	 * 
	 * @param testSentence
	 * @param k
	 * @param verbose
	 * @param skipTestSentences  during self-evaluation (ie, folds being used), this should be set to be true to 
	 *                           not use those sentences that are under evaluation
	 * @param sendEvent          if set to true, will send out a notice that a sentence was classified.  Should be 
	 *                           false when doing any type of evaluation
	 * @param distanceAlgorithm
	 * 
	 * If the there are no trained sentences, then Double.MAX_VALUE is returned in the result.averageDistance field.
	 * 
	 * @return
	 */
	public ClassificationResult getClassification(Sentence testSentence, int k, boolean verbose, boolean skipTestSentences, boolean sendEvent,SentenceDistance distanceAlgorithm) {		
		ClassificationResult result = new ClassificationResult();
		
		if (this.getTrainedSentences().size() == 0) {
			result.averageDistance = Double.MAX_VALUE;
			return result;
		}
		
		
		if (distanceAlgorithm instanceof CosineTermFreqIDFDistance) { 
			configureCosineTermFreqIDFDistance((CosineTermFreqIDFDistance) distanceAlgorithm, skipTestSentences);
		}
		
		IntermediateResults[] distances = this.computeNeighborDistances(testSentence, distanceAlgorithm, skipTestSentences, verbose, k);
		HashMap<String, Integer> votes = this.computeClassificationVotes(distances,k, result);
		this.findClassificationMajority(votes, result);

		result.distances = distances;
		
		if (sendEvent) {
			NLPEventClassification nec = new NLPEventClassification(testSentence, result, distances);
			NLPEventManager.getTheEventManager().sendEvent(NLPEventType.CLASSIFIED_SENTENCE, nec);
		}
		
		return result;
	}
	
	/**
	 * defaults skipSentences to false, and verbose to true when calling getClassification
	 * 
	 * @param testSentence
	 * @param k
	 * @param distanceAlgorithm
	 * @param sendEvent
	 * @return
	 */
	public  ClassificationResult getClassification(Sentence testSentence, int k, SentenceDistance distanceAlgorithm, boolean sendEvent) {
		return getClassification(testSentence,k,true, false,sendEvent, distanceAlgorithm);
	}
	
	
	
	/**
	 * Checks the passed in hashmap of votes to see if the key has been defined.
	 * If so, it returns that value.  Otherwise, 0 is returned.
	 * We need to use this method when checking the majority votes as it is possible that a vote was not made.
	 * 
	 * @param votes
	 * @param key
	 * @return
	 */
	protected int getDefaultVoteValue(HashMap<String,Integer> votes, String key) {
		return votes.containsKey(key) ? votes.get(key):  0;
	}
		
	private String getClassificationKey(String classification,Object value) {
		return classification+"_"+value.toString();
	}
	
	private void incrementHashMapValueStringKey(HashMap<String,Integer> a, String key) {
		int i=1;
		if (a.containsKey(key)) {
			i =  a.get(key) + 1;
		}
		a.put(key,i);
	}	
	
	private BooleanClassification getBooleanClassificationOfMaximumValue(HashMap<String, Integer> votes, String key) {
		BooleanType result = null;
		
		int maxValueFound = Integer.MIN_VALUE;
		for (BooleanType bt: BooleanType.values()) {
			String btkey = getClassificationKey(key, bt);
			if (votes.containsKey(btkey) && votes.get(btkey)>maxValueFound) {
				result = bt;
				maxValueFound = votes.get(btkey);
			}
		}
				
		return new BooleanClassification(result, Source.ML_INSTANCE);
	}
		
	public void printTrainedSentences(java.io.PrintStream ps) {
		for (Document d: _trainedSentences) {
			ps.println(d.sentence.getDocumentID()+"\t"+d.sentence);
		}
	}
	
	
	public void saveToFile(java.io.File f) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		
		mapper.writeValue(f, _trainedSentences.toArray(new Sentence[0]));		// this uses UTF-8
	}
	
	/**
	 * 
	 * @param f
	 * @throws Exception
	 */
	public void loadFromFile(java.io.File file) throws Exception {
		StanfordCoreNLP pipeline = GCController.getTheGCController().getPipeline();
		String input =  new String(Files.readAllBytes(file.toPath()), Charset.forName("UTF-8"));   // if not specified, uses windows-1552
		ObjectMapper mapper = new ObjectMapper();
		NLDocument documentHolder = new NLDocument();		
		
		Sentence[] sentencesToLoad = new Sentence[0];
		sentencesToLoad = (Sentence[]) mapper.readValue(input, sentencesToLoad.getClass());
	
		for (int i=0; i< sentencesToLoad.length;i++) {
			 GCController.getTheGCController().setStatusMessage("Processing "+(i+1)+ " of " +sentencesToLoad +" sentences");
			 
			 Sentence s = sentencesToLoad[i];
			 s.fixClassificationsFromLoad(); //required to fix an issue with classifications being the super-type.
			 
			 java.util.List<Sentence> sentences = documentHolder.parseSentence(pipeline, s.getSentence());
			 
			 for (Sentence createdSentence: sentences) {
				// createdSentence.setAccessControlDecision(s.getAccessControlDecision());
				// createdSentence.setAuditDecision(s.getAuditDecision());
				 createdSentence.setTrained(s.isTrained());
				 createdSentence.setComments(s.getComments());
				 createdSentence.setClassifications(s.getClassifications());
				 this.addTrainedSentence(createdSentence);
			 }
		 }
		_inverseDocumentFrequency = null;
	}

	/**
	 * Note that this adds to the current listener
	 * 
	 * @param f
	 * @throws Exception
	 */	
	public void loadFromSerializedFile(java.io.File f) throws Exception {
		java.io.ObjectInputStream input = new java.io.ObjectInputStream(new java.io.FileInputStream( f ) );
		int numberOfTrainSentences = input.readInt();

		for (int i=0;i<numberOfTrainSentences;i++) {
			this.addTrainedSentence( (Sentence) input.readObject());
		}
		input.close();				

		_inverseDocumentFrequency = null;
	}

	/**
	 * Note that this adds to the current listener
	 * 
	 * @param f
	 * @throws Exception
	 */
	public void saveToSerializedObjectFile(java.io.File f) throws Exception {
		java.io.ObjectOutputStream output = new java.io.ObjectOutputStream(new java.io.FileOutputStream(f) );
    	output.writeInt(_trainedSentences.size());
    	for (Document d:_trainedSentences) {
    		output.writeObject(d.sentence);
    	}
    	output.close();				
	}	
	
	private void clearAllTestingFlags() {
		for (Document d: _trainedSentences) {
			d.inTest = false;
		}
		_inverseDocumentFrequency = null;
	}
	
	private int countNumberOfSentencesInTesting() {
		int result =0;
		
		for (Document d: _trainedSentences) {
			if (d.inTest) {result ++;}
		}
		return result;
	}	
	
	private void setTestingFlagForFold(java.util.ArrayList<Document> fold) {
		for (Document d: fold) {
			d.inTest = true;
		}
		_inverseDocumentFrequency = null;
	}

	/**
	 * 
	 */
	public ConfusionMatrix performEvaluationForClassifications(ExpirementOptions options, java.util.ArrayList<java.util.ArrayList<Document>> folds ) {
		boolean verbose = false;
		ClassificationAttribute primaryClassification = options.getPrimaryClassification();
		String primaryClassificationName = primaryClassification.getName();
		
		int numberOfTrainedSentences   = _trainedSentences.size();
		
		if (options.getLastOperationMessage() != null && !options.getLastOperationMessage().equals("")) {
			GCController.getTheGCController().setStatusMessage(options.getLastOperationMessage());
			return null;
		}
		
		int k = options.getK();
		int numberOfFolds = folds.size();
		double threshold = options.getThreshold();
		double totalDistance = 0.0;    //Used to measure the compute the total overall average.  Each average distance is added to this, and then divded by the # of Sentences		
		SentenceDistance sentenceDistance = options.getSentenceDistance();
			

		//Folds have been generated, now we need to test each fold and put results into a temporary location
		System.out.println("Folds produced, starting test(k="+k+", n="+numberOfFolds+", threshold="+threshold+")");
		
		ConfusionMatrix totalMatrix = new ConfusionMatrix();
		ConfusionMatrix totalUnknownMatrix = new ConfusionMatrix();
				
		double distances[] = new double[_trainedSentences.size()];
		int numRecordsSeen = -1;
		
		for (int i=0;i<numberOfFolds;i++) {
			java.util.ArrayList<Document> fold = folds.get(i);
			clearAllTestingFlags();
			setTestingFlagForFold(fold);
			
			ConfusionMatrix foldMatrix = new ConfusionMatrix();
			ConfusionMatrix foldUnknownMatrix = new ConfusionMatrix();
			
			for (Document d:fold) {
				ClassificationResult r = getClassification(d.sentence, k, false, true,false, sentenceDistance);
				numRecordsSeen++;
				distances[numRecordsSeen] = r.averageDistance;
				BooleanClassification bc = (BooleanClassification) r.classifications.get(primaryClassificationName);
				totalDistance += (r.averageDistance/d.sentence.getNumberOfNodes());
				
				if (r.averageDistance > d.sentence.getNumberOfNodes() * threshold) {
					try { 
					  foldUnknownMatrix.setCell(d.sentence.hasBooleanClassification(primaryClassificationName), bc.getValue().getBooleanValue());
					}
					catch (Exception e) {
						System.err.println("Classification not defined (assuming null): "+primaryClassificationName+", "+d.sentence);
						foldUnknownMatrix.setCell(false, false);
					}					
				}
				else {
					try{
						foldMatrix.setCell(d.sentence.hasBooleanClassification(primaryClassificationName), bc.getValue().getBooleanValue());
						
						if (verbose &&  d.sentence.hasBooleanClassification(primaryClassificationName) != bc.getValue().getBooleanValue()) {
							System.out.println ("Mistake("+primaryClassification+",actual="+d.sentence.hasBooleanClassification(primaryClassificationName)+"): "+d.sentence);
							int kMax = Math.min(k,r.distances.length);
							for (int kNum=0; kNum <kMax; kNum++) {
								Sentence sNeighbor = _trainedSentences.get(r.distances[kNum].pos).sentence;
								System.out.println("\t"+kNum+":(distance="+r.distances[kNum].distance+", "+ sNeighbor.hasBooleanClassification(primaryClassificationName)+": "+sNeighbor);
								
								if ( kNum+1 == kMax && kMax < r.distances.length && r.distances[kNum].distance == r.distances[kNum+1].distance) {
									kMax++; // this allows us to show ties.
								}
							}
						}
						/*
						if ( verbose && d.sentence.hasBooleanClassification(primaryClassificationName) != bc.getValue().getBooleanValue()) {
							System.out.println ("Mistake("+primaryClassificationName+","+d.sentence.hasBooleanClassification(primaryClassificationName)+"): "+d.sentence);
						}
						*/
					}
					catch (Exception e) {
						System.err.println("Classification not defined (assuming null): "+primaryClassificationName);
						System.err.println(d.sentence);
						foldMatrix.setCell(false, false);
						
					}
				}
			}
			if (verbose) {
				System.out.println("Fold #"+i+"  "+ foldMatrix+"   UNKNOWNS Count:"+foldUnknownMatrix.getTotal() +"  "+foldUnknownMatrix);
			}
			
			totalMatrix.add(foldMatrix);
			totalUnknownMatrix.add(foldUnknownMatrix);
		}
		
		//average the results, and report
		System.out.println("Test Paramaters: k="+k+", n="+numberOfFolds+", threshold="+threshold+", attribute="+primaryClassification);
		System.out.println("Sentence Distance Method: "+sentenceDistance.getMethodName());
		System.out.println("TOTAL:  "+ totalMatrix+"   UNKNOWNS Count:"+totalUnknownMatrix.getTotal() +"  "+totalUnknownMatrix);
		System.out.println("Average distance ratio: "+ (totalDistance/numberOfTrainedSentences));
		System.out.println("Precision: "+ totalMatrix.getPrecision());
		System.out.println("Recall: "+ totalMatrix.getRecall());
		System.out.println("True negative rate: "+totalMatrix.getTrueNegativeRate());
		System.out.println("Accuracy: "+totalMatrix.getAccuracy());
		System.out.println("F-Measure:" +totalMatrix.getF1Measure());
		outputDistanceDescriptiveStatistics(distances);
		
		//cleanup in testing flags
		clearAllTestingFlags();
		
		return totalMatrix;
	}	
	
	
	
	/**
	 * 
	 */
	public ConfusionMatrix performEvaluationForClassificationsByFolds(ExpirementOptions options ) {
		boolean verbose = false;
		ClassificationAttribute primaryClassification = options.getPrimaryClassification();
		String primaryClassificationName = primaryClassification.getName();
			
		if (options.getLastOperationMessage() != null && !options.getLastOperationMessage().equals("")) {
			GCController.getTheGCController().setStatusMessage(options.getLastOperationMessage());
			return null;
		}
		
		java.util.ArrayList<java.util.ArrayList<Document>> folds = DocumentFoldCreator.createFolds(_trainedSentences, primaryClassificationName, options.getNumberOfFolds());

		ConfusionMatrix totalMatrix = this.performEvaluationForClassifications(options, folds);
		
		if (verbose) {   
			outputClassificationRatioAndMetrics(primaryClassificationName); 
		}

		
		System.out.println("==============================================");
		
		return totalMatrix;
	}
	
	
	/**
	 * 
	 */
	public ConfusionMatrix performRandomEvaluation(ExpirementOptions options, boolean computeWeight) {
		ClassificationAttribute primaryClassification = options.getPrimaryClassification();
		String primaryClassificationName = primaryClassification.getName();
		
		int numberOfTrainedSentences   = _trainedSentences.size();
		int numberOfPrimarySentences    = DocumentFoldCreator.countNumberOfSentencesByClassification(_trainedSentences,primaryClassificationName);
		int numberOfNonPrimarySentences = numberOfTrainedSentences-numberOfPrimarySentences;
				
		double primaryClassificationRatio = 0.5;
		if (computeWeight) {
			primaryClassificationRatio = numberOfPrimarySentences/ (((double) numberOfNonPrimarySentences)+numberOfPrimarySentences);
		}
		
		int numberOfFolds = options.getNumberOfFolds();
			
		java.util.ArrayList<java.util.ArrayList<Document>> folds = DocumentFoldCreator.createFolds(_trainedSentences, primaryClassificationName, numberOfFolds);

		//Folds have been generated, now we need to test each fold and put results into a temporary location
		System.out.println("Folds produced, starting random test of "+primaryClassificationName+", ratio="+primaryClassificationRatio);
		
				
		SecureRandom random = new SecureRandom();
		
		//Now we need to test each fold and put results into a temporary location
		//System.out.println("Folds produced, starting test(k="+k+", n="+numberOfFolds+", threshold="+threshold+")");
		
		ConfusionMatrix totalMatrix = new ConfusionMatrix();
		
		for (int i=0;i<numberOfFolds;i++) {
			java.util.ArrayList<Document> fold = folds.get(i);
			
			ConfusionMatrix foldMatrix = new ConfusionMatrix();
			
			for (Document d:fold) {
				boolean prediction = (random.nextDouble()<= primaryClassificationRatio );

				foldMatrix.setCell(d.sentence.hasBooleanClassification(primaryClassificationName), prediction);
			}
			//System.out.print("Fold #"+i+"  "+ foldMatrix);

			totalMatrix.add(foldMatrix);
		}
		
		return totalMatrix;
	}	
		
	/**
	 * 
	 */
	public ConfusionMatrix performEvaluationForClassificationsByDocument(ExpirementOptions options) {
		java.util.ArrayList<java.util.ArrayList<Document>> folds = DocumentFoldCreator.createFoldsByDocumentID(_trainedSentences);	
		ConfusionMatrix totalMatrix = performEvaluationForClassifications(options, folds);
		
		//cleanup in testing flags
		clearAllTestingFlags();
		
		return totalMatrix;
	}
		
	private void outputDistanceDescriptiveStatistics(double[] distances) {
		//Compute descriptive statistics
		java.util.Arrays.sort(distances);
		int midPoint = distances.length/2;
		double distanceMedian = distances[midPoint];   //this isn't complete precise, but its close enough
		double total = 0.0;
		double distanceMinimum = Double.MAX_VALUE;
		double distanceMaximum = Double.MIN_VALUE;
		for (double d: distances) {
			total += d;
			if (d < distanceMinimum) { distanceMinimum = d; }
			if (d > distanceMaximum) { distanceMaximum = d; }
		}
		double distanceMean = total/ distances.length;

		double sumOfDistancesSquared = 0.0;
		for (double d: distances) {
			sumOfDistancesSquared += (Math.abs(d-distanceMean)*Math.abs(d-distanceMean));
		}

		double distanceVariance = sumOfDistancesSquared / distances.length;
		double distanceStandardDeviation = Math.sqrt(distanceVariance);
		
		System.out.println("Descriptive statistics for the distances: ");
		System.out.println("    Minimum: "+distanceMinimum);
		System.out.println("    Maximum: "+distanceMaximum);
		System.out.println("    Median: "+distanceMedian);
		System.out.println("    Mean: "+distanceMean);
		System.out.println("    Variance: "+distanceVariance);
		System.out.println("    Standard Deviation: "+distanceStandardDeviation);		
	}
	
	/**
	 * This creates a hashmap for all words in the Learner.
	 * For each word, it returns the number of sentences in which it appears
	 * 
	 * @return
	 */
	public HashMap<String, Integer> generateTermSentenceFrequency(boolean skipTestSentences) {
		HashMap<String, Integer> results = new HashMap<String, Integer>();
		
		for (Document d: _trainedSentences) {
			if (skipTestSentences && d.inTest) { continue; }   // don't include sentences that we are currently evaluating
			HashMap<String, Integer> sentenceFreqs = d.sentence.getFrequencyMap();
			for (String key: sentenceFreqs.keySet()) {
				int count = results.containsKey(key) ? results.get(key) : 0;
				results.put(key, count + 1);
			}
		}
		
		return results;
	}	
	
	/**
	 * This creates a hashmap for all words in the Learner.
	 * For each word, it returns the number of sentences in which it appears
	 * 
	 * @return
	 */
	public HashMap<String, Double> generateInverseDocumentFrequency(HashMap<String, Integer> termSentenceFrequency, boolean skipTestSentences) {
		if (_inverseDocumentFrequency == null) {
			HashMap<String, Double> results = new HashMap<String, Double>();
			
			double numberOfDocuments = _trainedSentences.size();
			
			if (skipTestSentences) {
				numberOfDocuments -= this.countNumberOfSentencesInTesting();
			}
			
			for (String word: termSentenceFrequency.keySet()) {
				double idf = Math.log(numberOfDocuments / (termSentenceFrequency.get(word)+1));
				
				results.put(word, idf);
			}
			_inverseDocumentFrequency = results;
		}
		
		return _inverseDocumentFrequency;
	}		
	
	
	public NaiveBayesClassifier createBayesClassifier(ExpirementOptions options, java.util.ArrayList<java.util.ArrayList<Document>> folds, java.util.ArrayList<Document> testFold) {
		ClassificationAttribute primaryClassification = options.getPrimaryClassification();
		String primaryClassificationName = primaryClassification.getName();
		
		NaiveBayesClassifier bayes = new NaiveBayesClassifier();
		bayes.addClass("false");
		bayes.addClass("true");
		bayes.addLikelyhood("text", "words", new String[0]);
		
		for (java.util.ArrayList<Document> fold: folds) {
			if (fold == testFold) { continue; }  // we can't add this part to the classifier, it's the part we are testing
			
			for (Document d:fold) {
				if (d.sentence.hasBooleanClassification(primaryClassificationName)) {
					bayes.incrementClass("true");
					bayes.incrementClassLikelyhood("true", "text", d.sentence.getRoot().getStringRepresentationUltraCollapsed());
					/*
					for (String pattern: d.sentence.getRoot().extractAllSubgraphPatterns(Integer.MIN_VALUE, Integer.MAX_VALUE,4)) {
						bayes.incrementClassLikelyhood("true", "text", pattern);
					}
					*/
				}
				else {
					bayes.incrementClass("false");
					bayes.incrementClassLikelyhood("false", "text", d.sentence.getRoot().getStringRepresentationUltraCollapsed());
					/*
					for (String pattern: d.sentence.getRoot().extractAllSubgraphPatterns(Integer.MIN_VALUE, Integer.MAX_VALUE,4)) {
						bayes.incrementClassLikelyhood("false", "text", pattern);
					}
					*/					
				}
			}
		}
		
		return bayes;
		
	}
	

	public ConfusionMatrix evaluateWithInternalNaiveBayes(ExpirementOptions options, java.util.ArrayList<java.util.ArrayList<Document>> folds ) {
		boolean verbose = false;
		ClassificationAttribute primaryClassification = options.getPrimaryClassification();
		String primaryClassificationName = primaryClassification.getName();
		
		Logger.switchToLevel(Logger.LEVEL_DEBUG);
		
		
		int numberOfFolds = folds.size();
			

		//Folds have been generated, now we need to test each fold and put results into a temporary location
		Logger.log(Logger.LEVEL_DEBUG, "Folds produced, starting test(n="+numberOfFolds+")");

		
		ConfusionMatrix totalMatrix = new ConfusionMatrix();
						
		for (int i=0;i<numberOfFolds;i++) {
			java.util.ArrayList<Document> fold = folds.get(i);
			clearAllTestingFlags();
			setTestingFlagForFold(fold);
			
			ConfusionMatrix foldMatrix = new ConfusionMatrix();
			
			NaiveBayesClassifier bayes = createBayesClassifier(options,folds,  fold);
			Logger.log(Logger.LEVEL_DEBUG, "Naive Bayes classifer created for fold #"+i);
			
			for (Document d:fold) {
				//classify with internal NB
				Object[] arguments = new Object[1];
				String[] pattern = new String[1];
				pattern[0] = d.sentence.getRoot().getStringRepresentationUltraCollapsed();
				arguments[0] =pattern; // d.sentence.getRoot().extractAllSubgraphPatterns(Integer.MIN_VALUE, Integer.MAX_VALUE,6);
				Map<String, Double> result = bayes.computeClassProbabilitiesByLogs(arguments);		//computeClassProbabilitiesByLogs //computeClassProbabilities
			
				boolean bayesClassification = (result.get("true") > result.get("false"));


				try{
					foldMatrix.setCell(d.sentence.hasBooleanClassification(primaryClassificationName), bayesClassification);
					
					if ( d.sentence.hasBooleanClassification(primaryClassificationName) != bayesClassification) {
						System.out.println ("Mistake("+primaryClassification+",actual="+d.sentence.hasBooleanClassification(primaryClassificationName)+"): "+d.sentence);
					}
					/*
					if ( verbose && d.sentence.hasBooleanClassification(primaryClassificationName) != bc.getValue().getBooleanValue()) {
						System.out.println ("Mistake("+primaryClassificationName+","+d.sentence.hasBooleanClassification(primaryClassificationName)+"): "+d.sentence);
					}
					*/
				}
				catch (Exception e) {
					System.err.println("Classification not defined (assuming null): "+primaryClassificationName);
					System.err.println(d.sentence);
					foldMatrix.setCell(false, false);
				}
			}
			if (verbose) {
				System.out.println("Fold #"+i+"  "+ foldMatrix);
			}
			
			totalMatrix.add(foldMatrix);
		}
		
		//average the results, and report
		System.out.println("Test Paramaters: n="+numberOfFolds+", attribute="+primaryClassification);
		System.out.println("TOTAL:  "+ totalMatrix);
		System.out.println("Precision: "+ totalMatrix.getPrecision());
		System.out.println("Recall: "+ totalMatrix.getRecall());
		System.out.println("True negative rate: "+totalMatrix.getTrueNegativeRate());
		System.out.println("Accuracy: "+totalMatrix.getAccuracy());
		System.out.println("F-Measure:" +totalMatrix.getF1Measure());
		
		//cleanup in testing flags
		clearAllTestingFlags();
		
		Logger.restoreLoggingLevel();
		
		return totalMatrix;
	}
	
	public ConfusionMatrix evaluateWithInternalNaiveBayesByFolds(ExpirementOptions options ) {
		boolean verbose = false;
		ClassificationAttribute primaryClassification = options.getPrimaryClassification();
		String primaryClassificationName = primaryClassification.getName();
			
		if (options.getLastOperationMessage() != null && !options.getLastOperationMessage().equals("")) {
			GCController.getTheGCController().setStatusMessage(options.getLastOperationMessage());
			return null;
		}
		
		java.util.ArrayList<java.util.ArrayList<Document>> folds = DocumentFoldCreator.createFolds(_trainedSentences, primaryClassificationName, options.getNumberOfFolds());
		//java.util.ArrayList<java.util.ArrayList<Document>> folds = DocumentFoldCreator.createFoldsByDocumentID(_trainedSentences);

		
		ConfusionMatrix totalMatrix = this.evaluateWithInternalNaiveBayes(options, folds);
		
		if (verbose) {   outputClassificationRatioAndMetrics(primaryClassificationName); }
		
		System.out.println("==============================================");
		
		return totalMatrix;
	}
	
	
	public void outputClassificationRatioAndMetrics(String primaryClassificationName) {
		int numberOfTrainedSentences   = _trainedSentences.size();
		int numberOfPrimarySentences    = DocumentFoldCreator.countNumberOfSentencesByClassification(_trainedSentences,primaryClassificationName);
		int numberOfNonPrimarySentences = numberOfTrainedSentences-numberOfPrimarySentences;		
		double primaryClassificationRatio = numberOfPrimarySentences/((double) numberOfNonPrimarySentences);
		
		System.out.println("Classification positive ratio: "+primaryClassificationRatio);
		System.out.println("Number of access statements: "+ numberOfPrimarySentences);
		System.out.println("Number of non-access statements: " +numberOfNonPrimarySentences);
		System.out.println("Number of trained statements: " +numberOfTrainedSentences);				
	}
}

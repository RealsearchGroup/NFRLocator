package edu.ncsu.csc.nl.model.ml;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.HashMap;


import edu.ncsu.csc.nl.model.classification.BooleanClassification;
import edu.ncsu.csc.nl.model.classification.ClassificationAttribute;
import edu.ncsu.csc.nl.model.type.BooleanType;

import edu.ncsu.csc.nl.model.type.Source;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;


public class WekaInterfacer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String WORD_VECTOR_PREFIX =  "nlp_"; //this is necessary to avoid conflicts with existing attributes when converting to a word vector;
	
	private HashMap<String, Classifier> _naiveBayesClassifierForClassifications   = new HashMap<String, Classifier>();
	private HashMap<String, Classifier> _smoClassifierForClassifications = new HashMap<String, Classifier>();

	
	private HashMap<String, StringToWordVector> _trainerConvertToWordVector = new HashMap<String, StringToWordVector>();

	/** this is used while evaluating specific test / training sets.  it is set from the training set and applied to the test set */
	private Filter _lastStringToWordVectorFilter = null;
	
	/** this is used while evaluating specific test / training sets.  it is set from the training set and applied to the test set */
	private weka.filters.unsupervised.attribute.StringToNominal _stringToNominalFilter = null;
	
	
	private Classifier _acpClassifier = null;

	private Classifier _dbClassifier = null;

	
	/**
	 * Trains SMO classifier
	 * for each of the attributes that are marked as being in scope.
	 * 
	 * @param dataSet
	 * @param attributeList
	 */
	public void trainAccessControlPatternClassifier(Instances dataSet) {
	
		//_acpSMOClassifier = new weka.classifiers.functions.SMO();
		//_acpSMOClassifier = new weka.classifiers.rules.Ridor();
		_acpClassifier = new weka.classifiers.trees.J48();
		//_acpClassifier = new weka.classifiers.bayes.BayesNet();
		
		try {
			_stringToNominalFilter = new weka.filters.unsupervised.attribute.StringToNominal();
			
			String range = "1-"+ dataSet.instance(0).numAttributes();
			
			String[] options = {"-R", range};

			_stringToNominalFilter.setOptions(options);
			_stringToNominalFilter.setInputFormat(dataSet);
			
			dataSet = Filter.useFilter(dataSet, _stringToNominalFilter);			
			
			
			_acpClassifier.buildClassifier(dataSet);
		}
		catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
		
	public ClassificationResult classifyAccessControlPattern(Instances originalDataSet) {
		ClassificationResult result = new ClassificationResult(); 
		result.classificationMethod = ClassificationResult.WEKA_SMO;
		
		Instances dataSet = originalDataSet;
		Classifier classifier =_acpClassifier;
			
		try {
			dataSet.setClassIndex(0);
			dataSet = Filter.useFilter(dataSet, _stringToNominalFilter);
			
			double classValue = classifier.classifyInstance(dataSet.firstInstance());
			result.averageDistance = classValue;
				
			result.distributions.put("acp", classifier.distributionForInstance(dataSet.firstInstance()));
		}
		catch (Exception e) {
			System.out.println("WekaInterfacer.classify: "+e);
			return null;

		}
		return result;

	}		
	
	/**
	 * Trains SMO classifier
	 * for each of the attributes that are marked as being in scope.
	 * 
	 * @param dataSet
	 * @param attributeList
	 */
	public void trainDatabasePatternClassifier(Instances dataSet) {
	
		//_acpSMOClassifier = new weka.classifiers.functions.SMO();
		//_acpSMOClassifier = new weka.classifiers.rules.Ridor();
		_dbClassifier = new weka.classifiers.trees.J48();
		//_acpClassifier = new weka.classifiers.bayes.BayesNet();
		
		try {
			_stringToNominalFilter = new weka.filters.unsupervised.attribute.StringToNominal();
			
			String range = "1-"+ dataSet.instance(0).numAttributes();
			
			String[] options = {"-R", range};

			_stringToNominalFilter.setOptions(options);
			_stringToNominalFilter.setInputFormat(dataSet);
			
			dataSet = Filter.useFilter(dataSet, _stringToNominalFilter);			
			
			
			_dbClassifier.buildClassifier(dataSet);
		}
		catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}	
	
	
	public ClassificationResult classifyDatabasePattern(Instances originalDataSet) {
		ClassificationResult result = new ClassificationResult(); 
		result.classificationMethod = ClassificationResult.WEKA_SMO;
		
		Instances dataSet = originalDataSet;
		Classifier classifier =_dbClassifier;
			
		try {
			dataSet.setClassIndex(0);
			dataSet = Filter.useFilter(dataSet, _stringToNominalFilter);
			
			double classValue = classifier.classifyInstance(dataSet.firstInstance());
			result.averageDistance = classValue;
				
			result.distributions.put("db", classifier.distributionForInstance(dataSet.firstInstance()));
		}
		catch (Exception e) {
			System.out.println("WekaInterfacer.classify: "+e);
			return null;

		}
		return result;

	}			
	
	
	
	/**
	 * Trains two setes of classifiers (naiveBayesMultinomial and SupportVectorMachine(SMO) 
	 * for each of the attributes that are marked as being in scope.
	 * 
	 * @param dataSet
	 * @param attributeList
	 */
	public void trainClassifiers(Instances dataSet, java.util.List<ClassificationAttribute> attributeList) {
	
		// Train classifiers for the classification attributes
		for (ClassificationAttribute ca: attributeList) {
			if (ca.getIncludeInEvaluation() == false) { continue; }
			String attribute = ca.getName();
			Instances currentDataSet = removeOtherClassifications(dataSet, attribute);
			currentDataSet = this.convertSentenceToWordVector(currentDataSet, true, false, attribute);
			currentDataSet.setClassIndex(this.getClassPosition(currentDataSet, attribute));
			
			try {				
				NaiveBayesMultinomial nbm = new NaiveBayesMultinomial();
				nbm.buildClassifier(currentDataSet);
				_naiveBayesClassifierForClassifications.put(attribute, nbm);

				weka.classifiers.functions.SMO smo = new weka.classifiers.functions.SMO(); 
				smo.buildClassifier(currentDataSet);
				_smoClassifierForClassifications.put(attribute,smo);
			}
			catch (Exception e) {
				System.out.println("WekaInterfacer.trainClassifiers: "+e);
			}		
		}
	}
	

	private Instances removeOtherClassifications(Instances dataSet, String attributeToTest) {
		int numberOfAttributes = dataSet.numAttributes();
		for (int i=numberOfAttributes-1;i>=0;i--) {
			//System.out.println(dataSet.attribute(i));
			if (dataSet.attribute(i).name().equals("sentence") || dataSet.attribute(i).name().equals(attributeToTest)) {continue; }
			
			Remove remove = new weka.filters.unsupervised.attribute.Remove();
			String[] options = {"-R", Integer.toString(i+1)};
			try {
				remove.setOptions(options);
				remove.setInputFormat(dataSet);
			
				dataSet = Filter.useFilter(dataSet, remove);
			}
			catch (Exception e) {
				System.out.println("WekaInterfacer.removeOtherclassifications: "+e);
			}
			i++;
		}		
		return dataSet;
	}
	
	
	private ConfusionMatrix evaluateNFoldWekaClassifier(Instances completeDataSet, java.util.List<ClassificationAttribute> attributeList, weka.classifiers.Classifier classifier, int numberOfFolds)  {		
		ConfusionMatrix totalMatrix = new ConfusionMatrix();
		double numberOfAttributesEvaluted = 0.0;
		double precision = 0.0;
		double recall    = 0.0;
		double f1        = 0.0;
		
		
		for (ClassificationAttribute ca: attributeList) {
			if (ca.getIncludeInEvaluation() == false) { continue; }
			numberOfAttributesEvaluted++;
			
			Instances currentDataSet = this.removeOtherClassifications(completeDataSet, ca.getName());
			currentDataSet = this.convertSentenceToWordVector(currentDataSet, false, false, null);
			
			try {
				currentDataSet.setClassIndex(this.getClassPosition(currentDataSet, ca.getName()));			
				
				Evaluation eval = new Evaluation(currentDataSet);
				eval.crossValidateModel(classifier, currentDataSet, numberOfFolds, new SecureRandom());
				ConfusionMatrix attributeMatrix = new ConfusionMatrix(eval.confusionMatrix());
				
				totalMatrix.add(attributeMatrix);
				precision += attributeMatrix.getPrecision();
				recall    += attributeMatrix.getRecall();
				f1        += attributeMatrix.getF1Measure();
				
				System.out.println(ca.getName() +":" + attributeMatrix);
				
			}
			catch (Exception e) {
				System.out.println("WekaInterfacer.classifyEvaluation: "+e);
				return null;
			}
			
		}
		
		System.out.println("************************************************************");
		System.out.println("TOTAL:  "+ totalMatrix);

		System.out.println("Precision: "+ totalMatrix.getPrecision()+"\t"+ precision/numberOfAttributesEvaluted);
		System.out.println("Recall: "+ totalMatrix.getRecall() +"\t"+ recall/numberOfAttributesEvaluted);
		//System.out.println("True negative rate: "+totalMatrix.getTrueNegativeRate());
		//System.out.println("Accuracy: "+totalMatrix.getAccuracy());
		System.out.println("F-Measure:" +totalMatrix.getF1Measure() +"\t"+ f1/numberOfAttributesEvaluted);			

		return totalMatrix;

	}	
	
	public ConfusionMatrix evaluateNFoldWithNaiveBayesMultinomial(Instances dataSet, java.util.List<ClassificationAttribute> attributeList, int numberOfFolds) throws Exception {
		System.out.println("Weka - NaiveBayes:");
		
		return evaluateNFoldWekaClassifier(dataSet,attributeList, new NaiveBayesMultinomial(), numberOfFolds);

	}
	
	
	public ConfusionMatrix evaluateNFoldWithSupportVectorMachine(Instances dataSet, java.util.List<ClassificationAttribute> attributeList, int numberOfFolds) throws Exception {
		System.out.println("Weka - Support Vector Machine (SMO):");
		
		return evaluateNFoldWekaClassifier(dataSet,attributeList, new weka.classifiers.functions.SMO(),numberOfFolds);
	}	
	
	/**
	 * Evaluates the currently defined(trained) classifier against the passed-in data set. 
	 * 
	 * @param testDataSet
	 * @param attributeList
	 * @param classifier
	 * @return
	 */
	private ConfusionMatrix evaluateWithTestSetWekaClassifier(Instances trainDataSet, Instances testDataSet, java.util.List<ClassificationAttribute> attributeList, weka.classifiers.Classifier classifier)  {
		ConfusionMatrix totalMatrix = new ConfusionMatrix();
		double numberOfAttributesEvaluted = 0.0;
		double precision = 0.0;
		double recall    = 0.0;
		double f1        = 0.0;
		
		
		for (ClassificationAttribute ca: attributeList) {
			if (ca.getIncludeInEvaluation() == false) { continue; }
			String attributeName = ca.getName();
			numberOfAttributesEvaluted++;
			
			Instances currentTrainDataSet = this.removeOtherClassifications(trainDataSet, attributeName);
			Instances currentTestDataSet  = this.removeOtherClassifications(testDataSet,attributeName);

			
			try {
				currentTrainDataSet = this.convertSentenceToWordVector(currentTrainDataSet, false, true, null);  // there is a side effect to properly set the filter used in the following statement
				currentTestDataSet = Filter.useFilter(currentTestDataSet, _lastStringToWordVectorFilter);
				
				currentTrainDataSet.setClassIndex(this.getClassPosition(currentTrainDataSet, attributeName));			
				currentTestDataSet.setClassIndex(this.getClassPosition(currentTestDataSet, attributeName));			

				classifier.buildClassifier(currentTrainDataSet);
				
				Evaluation eval = new Evaluation(currentTrainDataSet);
				eval.evaluateModel(classifier,currentTestDataSet);
				ConfusionMatrix attributeMatrix = new ConfusionMatrix(eval.confusionMatrix());
				
				totalMatrix.add(attributeMatrix);
				precision += attributeMatrix.getPrecision();
				recall    += attributeMatrix.getRecall();
				f1        += attributeMatrix.getF1Measure();
				
				System.out.println(attributeName+":" + attributeMatrix);
			}
			catch (Exception e) {
				System.out.println("WekaInterfacer.classifyEvaluation: "+e);
				return null;
			}
			
		}
		
		System.out.println("************************************************************");
		System.out.println("TOTAL:  "+ totalMatrix);

		System.out.println("Precision: "+ totalMatrix.getPrecision()+"\t"+ precision/numberOfAttributesEvaluted);
		System.out.println("Recall: "+ totalMatrix.getRecall() +"\t"+ recall/numberOfAttributesEvaluted);
		//System.out.println("True negative rate: "+totalMatrix.getTrueNegativeRate());
		//System.out.println("Accuracy: "+totalMatrix.getAccuracy());
		System.out.println("F-Measure:" +totalMatrix.getF1Measure() +"\t"+ f1/numberOfAttributesEvaluted);			

		return totalMatrix;

	}		
	
	public ConfusionMatrix evaluateTestSetWithNaiveBayesMultinomial(Instances trainDataSet, Instances testDataSet, java.util.List<ClassificationAttribute> attributeList) throws Exception {
		System.out.println("Weka - NaiveBayes:");
		
		return evaluateWithTestSetWekaClassifier(trainDataSet, testDataSet,attributeList, new NaiveBayesMultinomial());
	}
	
	public ConfusionMatrix evaluateTestSetWithSupportVectorMachine(Instances trainDataSet, Instances testDataSet, java.util.List<ClassificationAttribute> attributeList) throws Exception {
		System.out.println("Weka - Support Vector Machine (SMO):");
		
		return evaluateWithTestSetWekaClassifier(trainDataSet, testDataSet, attributeList, new weka.classifiers.functions.SMO());
	}		
	
	/**
	 * 
	 * @param dataSet
	 * @param storeFilter   if training the classifier for future use, does it need to be stored?
	 * @param storeTemporarily if this is set, then the stringToWordVector is stored in the instance variabe "_lastStringToWordVectorFilter"
	 * @param attributeName if the filter is to be stored, what attribute name (key) should be used in the hash map.
	 * @return
	 */
	private Instances convertSentenceToWordVector(Instances dataSet, boolean storeFilter,  boolean storeTemporarily, String attributeName){ 
		int sentencePosition = -1;
		
		for (int i=0;i<dataSet.numAttributes();i++) {
			if (dataSet.attribute(i).name().equals("sentence")) { sentencePosition = i; }
		}
		
		StringToWordVector stwv = new weka.filters.unsupervised.attribute.StringToWordVector();
		String[] options = {"-R", Integer.toString(sentencePosition+1), "-P", WORD_VECTOR_PREFIX, "-W", "100000", 
				            "-prune-rate", "-1.0", "-N", "0", "-stemmer", "weka.core.stemmers.NullStemmer", "-M", "1",
				            "-tokenizer", "weka.core.tokenizers.WordTokenizer"};//, "-delimiters", "\" \\r\\n\\t.,;:\\\'\\\"()?!\""};
		try {
			stwv.setOptions(options);
			stwv.setInputFormat(dataSet);

			//stwv.setUseStoplist(true);
			
			if (storeFilter)      {	_trainerConvertToWordVector.put(attributeName, stwv); }
			if (storeTemporarily) {	_lastStringToWordVectorFilter = stwv;		          }
			
			dataSet = Filter.useFilter(dataSet, stwv);
		}
		catch(Exception e) {
			System.out.println("WekaInterfacer.convertSentenceToWordVector: "+e);
			return null;
		}
		return dataSet;
	}
	
	private int getClassPosition(Instances dataSet, String attributeToTest) {
		int classPosition = -1;
		
		for (int i=0;i<dataSet.numAttributes();i++) {
			if (dataSet.attribute(i).name().equals( attributeToTest)) { classPosition = i; }
		}	
		
		return classPosition;
	}
		
	private ClassificationResult classify(HashMap<String, Classifier> classifierMap, Instances originalDataSet) {
		ClassificationResult result = new ClassificationResult();
		result.classificationMethod = ClassificationResult.WEKA_NAIVE_BAYES;
		
		for (String attribute: classifierMap.keySet()) {
			Instances dataSet = originalDataSet;
			Classifier classifier = classifierMap.get(attribute);
			
			try {
				dataSet = this.removeOtherClassifications(dataSet, attribute);
				dataSet = Filter.useFilter(dataSet, _trainerConvertToWordVector.get(attribute));
				dataSet.setClassIndex(this.getClassPosition(dataSet, attribute));
				
				result.classifications.put(attribute, new BooleanClassification(BooleanType.getBooleanType(classifier.classifyInstance(dataSet.firstInstance()) == 0),  Source.ML_NAIVE_BAYES));
				result.distributions.put(attribute, classifier.distributionForInstance(dataSet.firstInstance()));
			}
			catch (Exception e) {
				System.out.println("WekaInterfacer.classify: "+e);
				return null;
			}
		}
		return result;
	}
	
	public ClassificationResult classifyWithNaiveBayesMultinomial(Instances originalDataSet) {
		return classify(_naiveBayesClassifierForClassifications,originalDataSet);
	}

	public ClassificationResult classifyWithSMO(Instances originalDataSet) {
		return classify(_smoClassifierForClassifications,originalDataSet);
	}


	
	public void generateInformationGainMatrixClassifications(Instances completeDataSet, java.util.List<ClassificationAttribute> attributeList, int numberOfTerms) {
		
		java.util.HashMap<ClassificationAttribute, java.util.ArrayList<String>> topWords = new java.util.HashMap<ClassificationAttribute, java.util.ArrayList<String>>();
		
		for (ClassificationAttribute ca: attributeList) {
			
			Instances currentDataSet = this.removeOtherClassifications(completeDataSet, ca.getName());
			currentDataSet = this.convertSentenceToWordVector(currentDataSet, false, false, null);
			
			try {
				currentDataSet.setClassIndex(this.getClassPosition(currentDataSet, ca.getName()));	
				
				weka.attributeSelection.InfoGainAttributeEval eval = new weka.attributeSelection. InfoGainAttributeEval();
				weka.attributeSelection.Ranker ranker = new weka.attributeSelection.Ranker();					
				weka.attributeSelection.AttributeSelection attsel = new weka.attributeSelection.AttributeSelection();
				attsel.setEvaluator(eval);
				attsel.setSearch(ranker); 
				attsel.SelectAttributes(currentDataSet);

				int[] indices = attsel.selectedAttributes();
				
				java.util.ArrayList<String> topWordsForObjective = new java.util.ArrayList<String>();
				int startingWordPosition = WORD_VECTOR_PREFIX.length(); //don't get the prefix in the wor.
				for (int i=0;i< Math.min(numberOfTerms, indices.length); i++) {
					topWordsForObjective.add(currentDataSet.attribute(indices[i]).name().substring(startingWordPosition));
				}
				topWords.put(ca,topWordsForObjective);
			}
			catch (Exception e) {
				System.out.println(e);
			}
		}
		
		// Now, output the data
		
		//header
		boolean initialComplete = false;
		for (ClassificationAttribute ca: attributeList) {
			if (initialComplete) { System.out.print("\t"); }
			else {initialComplete = true;}
			System.out.print(ca.getName());
		}
		System.out.println();
		
		//now the data
		for (int i=0; i < numberOfTerms; i++) {
			initialComplete = false;
			for (ClassificationAttribute ca: attributeList) {
				if (initialComplete) { System.out.print("\t"); }
				else {initialComplete = true;}
				
				java.util.ArrayList<String> topWordsForObjective = topWords.get(ca);
				if (topWordsForObjective.size() <= i) {   // feasible that an objective may not have that many keywords... (unlikely, though...)
					System.out.print(" ");
				}
				else {
					System.out.print(topWordsForObjective.get(i));
				}
			}
			System.out.println();
		}
		System.out.println();		
	}
	
}

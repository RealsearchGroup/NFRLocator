package edu.ncsu.csc.nl.model.ml;

import java.util.ArrayList;

import weka.core.Instances;
import edu.ncsu.csc.nl.GCController;
import edu.ncsu.csc.nl.model.Sentence;
import edu.ncsu.csc.nl.model.WekaCreator;
import edu.ncsu.csc.nl.model.WekaCreatorOptions;
import edu.ncsu.csc.nl.model.classification.BooleanClassification;
import edu.ncsu.csc.nl.model.classification.ClassificationAttribute;
import edu.ncsu.csc.nl.model.distance.SentenceDistance;
import edu.ncsu.csc.nl.model.english.StopWord;
import edu.ncsu.csc.nl.model.type.WordType;

public class MasterClassifier {
	private java.util.List<ClassificationAttribute> _classificationAttributes;  // what are the different attributes that can be classifed
	
	private int _currentKValueForInstanceLearner = 3;
	
	private ClassifierOptions _options = new ClassifierOptions();
	
	private InstanceLearner _instanceLearner = null;
	//private NaiveBayes _naiveBayesClassifier = null;
	//private TermIDFCategoryClassifier _termIDFCategoryClassifer = null;
	private WekaInterfacer _wekaInterfacer   = null;
	
	
	public MasterClassifier( java.util.List<ClassificationAttribute> classificationList) {
		_classificationAttributes = classificationList;
	}

	public void createInstanceLearner() {
		_instanceLearner = new InstanceLearner();
		int numTrainedSentences = GCController.getTheGCController().getInstanceLearner().getNumberOfTrainedSentences();
		for (int i=0;i<numTrainedSentences; i++) {
			_instanceLearner.addTrainedSentence(GCController.getTheGCController().getInstanceLearner().getTrainedSentenceAt(i));
		}
	}
	
	public void createWekaInterfacer(String documentID) {
		ArrayList<Sentence> sentences =GCController.getTheGCController().getInstanceLearner().getTrainedSentencesActual();

		WekaCreatorOptions wo = new WekaCreatorOptions();
		wo.setExportSentenceAsString(true);
		wo.setNERSentenceIndicators(false);
		wo.setWordType( this.getClassifierOptions().getWordType());
		wo.setStopWords(this.getClassifierOptions().getStopWords());
		wo.setUseOriginalSentence(false);		
				
		Instances dataSetClassifications = (new WekaCreator()).createWekaInstancesForClassifications(documentID,sentences,_classificationAttributes,wo);
		_wekaInterfacer = new WekaInterfacer();
		_wekaInterfacer.trainClassifiers(dataSetClassifications, _classificationAttributes);
	}

	public void createInstanceLearner(ArrayList<Document> documents) {
		_instanceLearner = new InstanceLearner();
		int numTrainedSentences = documents.size();
		for (int i=0;i<numTrainedSentences; i++) {
			_instanceLearner.addTrainedDocument(documents.get(i));
		}
	}	
	
	public void createWekaInterfacer(ArrayList<Document> documents, String documentID) {
		ArrayList<Sentence> sentences = new ArrayList<Sentence>();
		for (Document d: documents) {
			if (d.inTest == false) {
				sentences.add(d.sentence);
			}
		}
		WekaCreatorOptions wo = new WekaCreatorOptions();
		wo.setExportSentenceAsString(true);
		wo.setNERSentenceIndicators(false);
		wo.setWordType( this.getClassifierOptions().getWordType());
		wo.setStopWords(this.getClassifierOptions().getStopWords());
		wo.setUseOriginalSentence(false);		
				
		Instances dataSetClassifications = (new WekaCreator()).createWekaInstancesForClassifications(documentID,sentences,_classificationAttributes,wo);

		_wekaInterfacer = new WekaInterfacer();
		_wekaInterfacer.trainClassifiers(dataSetClassifications, _classificationAttributes);		
		
		
	}
	
	
	
	public void setCurrentKValueForInstanceLearner(int newValue) {
		_currentKValueForInstanceLearner = newValue;
	}
	
	public int getCurrentKValueForInstanceLearner() {
		return _currentKValueForInstanceLearner;
	}
	
	public ClassifierOptions getClassifierOptions() {
		return _options;
	}
	public void setClassifierOptions(ClassifierOptions newValue) {
		_options = newValue;
		
		//_wekaInterfacer.setOptions(newValue);
	}
	
	public InstanceLearner getInstanceLearner() {
		if (_instanceLearner == null) {
			this.createInstanceLearner();
		}
		return _instanceLearner;
	}	
		
	public WekaInterfacer getWekaInterfacer() {
		return _wekaInterfacer;
	}
	

	private void clearAllTestingFlags(ArrayList<Document> documents) {
		for (Document d: documents) {
			d.inTest = false;
		}
	}
	
	private static void setTestingFlagForFold(java.util.ArrayList<Document> fold) {
		for (Document d: fold) {
			d.inTest = true;
		}
	}	
	
	
	public void performOverallSelfEvaluate() {
		System.out.println("Ensuring classifiers are created");
		this.createWekaInterfacer("genericID");
		
		ConfusionMatrix totalMatrix = new ConfusionMatrix();
		
		double precision = 0.0;
		double recall    = 0.0;
		double f1        = 0.0;
		double numberOfAttributesEvaluted = 0.0;
		boolean verbose = false;
		
		ArrayList<Document> documents = new ArrayList<Document>();
		for (Document d: GCController.getTheGCController().getInstanceLearner().getTrainedSentences()) {
			Document newDoc = new Document();
			newDoc.sentence = d.sentence;
			newDoc.inTest   = false;
			newDoc.unlabeledTraining = false;
			documents.add(newDoc);
		}
		
		for (ClassificationAttribute ca: _classificationAttributes) {
			if (ca.getIncludeInEvaluation() == false) { continue; }
			String primaryClassification = ca.getName();
			numberOfAttributesEvaluted++;
			
			ClassifierOptions options  = new ClassifierOptions( ca,ClassifierOptions.FOLD_RANDOM, WordType.LEMMA, 10, false, StopWord.getListByName(StopWord.DETERMINER), false);
			this.setClassifierOptions(options);
			ConfusionMatrix attributeMatrix = new ConfusionMatrix();
			
			java.util.ArrayList<java.util.ArrayList<Document>> folds = DocumentFoldCreator.createFolds(documents,primaryClassification, options.getNumberOfFolds());
			//java.util.ArrayList<java.util.ArrayList<Document>> folds = DocumentFoldCreator.createFoldsByDocumentID(documents);

			int numberOfFolds = folds.size();
			SentenceDistance sentenceDistance = options.getSentenceDistance();
			int k = options.getK();
			double threshold = options.getThreshold();
			
			for (int i=0;i<numberOfFolds;i++) {
				java.util.ArrayList<Document> fold = folds.get(i);
				clearAllTestingFlags(documents);
				setTestingFlagForFold(fold);
				
				//need to reset term and internal naive
				//need to re-create weka
				this.createWekaInterfacer(documents,"testingFold");
				this.createInstanceLearner(documents);
				
				
				ConfusionMatrix foldMatrix = new ConfusionMatrix();
				
				for (Document d:fold) {
					ArrayList<Sentence> sentences = new ArrayList<Sentence>();
					sentences.add(d.sentence);
					//Instances testDataSetSentence= (new WekaCreator()).createWekaInstancesAsString("testFold",sentences,_classificationAttributes,false,false);
					WekaCreatorOptions wo = new WekaCreatorOptions();
					wo.setExportSentenceAsString(true);
					wo.setNERSentenceIndicators(false);
					wo.setWordType( this.getClassifierOptions().getWordType());
					wo.setStopWords(this.getClassifierOptions().getStopWords());
					wo.setUseOriginalSentence(false);		
							
					Instances testDataSetSentence = (new WekaCreator()).createWekaInstancesForClassifications("testFold",sentences,_classificationAttributes,wo);

					ClassificationResult kNNResult = this.getInstanceLearner().getClassification(d.sentence, k, false, true,false, sentenceDistance);
					ClassificationResult wekaNB     = this.getWekaInterfacer().classifyWithNaiveBayesMultinomial(testDataSetSentence);
					ClassificationResult wekaSMO    = this.getWekaInterfacer().classifyWithSMO(testDataSetSentence);

					
					boolean instanceResult = ((BooleanClassification) kNNResult.classifications.get(primaryClassification)).getValue().getBooleanValue();
					
					if (kNNResult.averageDistance/d.sentence.getNumberOfNodes() > .6) {
						int foundTrue = 0;
						if (instanceResult) { foundTrue++; }
						if (((BooleanClassification) wekaNB.classifications.get(primaryClassification)).getValue().getBooleanValue()) {  foundTrue++; }
						if (((BooleanClassification) wekaSMO.classifications.get(primaryClassification)).getValue().getBooleanValue()) {  foundTrue++; }
						if (foundTrue > 2) {
							foldMatrix.setCell(d.sentence.hasBooleanClassification(primaryClassification), true);
						}
						else if (foundTrue <2) {
							foldMatrix.setCell(d.sentence.hasBooleanClassification(primaryClassification), false);
						}
						else {
							foldMatrix.setCell(d.sentence.hasBooleanClassification(primaryClassification), ((BooleanClassification) wekaSMO.classifications.get(primaryClassification)).getValue().getBooleanValue());
						}

					}
					else {
						foldMatrix.setCell(d.sentence.hasBooleanClassification(primaryClassification), instanceResult);
					}
					/*
					if ( d.sentence.hasBooleanClassification(primaryClassification) != bc.getValue().getBooleanValue()) {
						//System.out.println ("Mistake("+primaryClassification+",actual="+s.hasBooleanClassification(primaryClassification)+"): "+s);
					}
					
					if ( verbose && d.sentence.hasBooleanClassification(primaryClassification) != bc.getValue().getBooleanValue()) {
						System.out.println ("Mistake("+primaryClassification+","+d.sentence.hasBooleanClassification(primaryClassification)+"): "+d.sentence);
					}					
					*/
					
					if (verbose) {
						System.out.print(ca.getAbbreviation());
						System.out.print("\t");
						System.out.print(d.sentence);
						System.out.print("\t");					
						System.out.print(d.sentence.hasBooleanClassification(primaryClassification));
						System.out.print("\t");
						System.out.print(kNNResult.classifications.get(primaryClassification).getValue());
						System.out.print("\t");
						System.out.print(kNNResult.averageDistance);
						System.out.print("\t");
						System.out.print(wekaNB.classifications.get(primaryClassification).getValue());
						System.out.print("\t");			
						System.out.print(this.getRatio( wekaNB.distributions.get(primaryClassification)));
	
						
						System.out.print("\t");
						System.out.print(wekaSMO.classifications.get(primaryClassification).getValue());
						System.out.print("\t");			
						System.out.print(this.getRatio( wekaSMO.distributions.get(primaryClassification)));
						
						System.out.println();
					}
					
				}
				
				if (verbose) {
					System.out.println("Fold #"+i+"  "+ foldMatrix);
				}
				
				attributeMatrix.add(foldMatrix);
			}
			
			//average the results, and report
			System.out.println("Test Paramaters: k="+k+", n="+numberOfFolds+", threshold="+threshold+", attribute="+primaryClassification);
			System.out.println("Sentence Distance Method: "+sentenceDistance.getMethodName());
			System.out.println("TOTAL:  "+ attributeMatrix);

			System.out.println("Precision: "+ attributeMatrix.getPrecision());
			System.out.println("Recall: "+ attributeMatrix.getRecall());
			System.out.println("True negative rate: "+attributeMatrix.getTrueNegativeRate());
			System.out.println("Accuracy: "+attributeMatrix.getAccuracy());
			System.out.println("F-Measure:" +attributeMatrix.getF1Measure());			
			
			//_mainFrame.setStatusMessage("Self-evaluation generated for " + ca.getName());
			totalMatrix.add(attributeMatrix);
			precision += attributeMatrix.getPrecision();
			recall    += attributeMatrix.getRecall();
			f1        += attributeMatrix.getF1Measure();
		}
		System.out.println("************************************************************");
		System.out.println("TOTAL:  "+ totalMatrix);

		System.out.println("Precision: "+ totalMatrix.getPrecision()+"\t"+ precision/numberOfAttributesEvaluted);
		System.out.println("Recall: "+ totalMatrix.getRecall() +"\t"+ recall/numberOfAttributesEvaluted);
		//System.out.println("True negative rate: "+totalMatrix.getTrueNegativeRate());
		//System.out.println("Accuracy: "+totalMatrix.getAccuracy());
		System.out.println("F-Measure:" +totalMatrix.getF1Measure() +"\t"+ f1/numberOfAttributesEvaluted);			
	
	
		
	}

	public void performOverallDocumentEvaluate() {
		System.out.println("Ensuring classifiers are created");
		this.createWekaInterfacer("genericID");
		
		ConfusionMatrix totalMatrix = new ConfusionMatrix();
		
		double precision = 0.0;
		double recall    = 0.0;
		double f1        = 0.0;
		double numberOfAttributesEvaluted = 0.0;
		boolean verbose = false;
		
		ArrayList<Document> documents = new ArrayList<Document>();
		for (Document d: GCController.getTheGCController().getInstanceLearner().getTrainedSentences()) {
			Document newDoc = new Document();
			newDoc.sentence = d.sentence;
			newDoc.inTest   = false;
			newDoc.unlabeledTraining = false;
			documents.add(newDoc);
		}
		
		for (ClassificationAttribute ca: _classificationAttributes) {
			//if (ca.getIncludeInEvaluation() == false) { continue; }
			String primaryClassification = ca.getName();
			numberOfAttributesEvaluted++;
			
			ClassifierOptions options  = new ClassifierOptions( ca, ClassifierOptions.FOLD_RANDOM, WordType.LEMMA, 10, false, StopWord.getListByName(StopWord.DETERMINER), false);
			this.setClassifierOptions(options);
			ConfusionMatrix attributeMatrix = new ConfusionMatrix();
			
			java.util.ArrayList<java.util.ArrayList<Document>> folds = DocumentFoldCreator.createFoldsByDocumentID(documents);
			int numberOfFolds = folds.size();
			SentenceDistance sentenceDistance = options.getSentenceDistance();
			int k = options.getK();
			double threshold = options.getThreshold();
			
			for (int i=0;i<numberOfFolds;i++) {
				java.util.ArrayList<Document> fold = folds.get(i);
				clearAllTestingFlags(documents);
				setTestingFlagForFold(fold);
				
				//need to reset term and internal naive
				//need to re-create weka
				this.createWekaInterfacer(documents,"testingFold");
				this.createInstanceLearner(documents);
				
				
				ConfusionMatrix foldMatrix = new ConfusionMatrix();
				
				for (Document d:fold) {
					ArrayList<Sentence> sentences = new ArrayList<Sentence>();
					sentences.add(d.sentence);
					//Instances testDataSetSentence= (new WekaCreator()).createWekaInstancesAsString("testFold",sentences,_classificationAttributes,false,false);
					WekaCreatorOptions wo = new WekaCreatorOptions();
					wo.setExportSentenceAsString(true);
					wo.setNERSentenceIndicators(false);
					wo.setWordType( this.getClassifierOptions().getWordType());
					wo.setStopWords(this.getClassifierOptions().getStopWords());
					wo.setUseOriginalSentence(false);		
							
					Instances testDataSetSentence = (new WekaCreator()).createWekaInstancesForClassifications("testFold",sentences,_classificationAttributes,wo);

					ClassificationResult kNNResult = this.getInstanceLearner().getClassification(d.sentence, k, false, true,false, sentenceDistance);
					ClassificationResult wekaNB     = this.getWekaInterfacer().classifyWithNaiveBayesMultinomial(testDataSetSentence);
					ClassificationResult wekaSMO    = this.getWekaInterfacer().classifyWithSMO(testDataSetSentence);

					
					boolean instanceResult = ((BooleanClassification) kNNResult.classifications.get(primaryClassification)).getValue().getBooleanValue();
					
					if (kNNResult.averageDistance/d.sentence.getNumberOfNodes() > .5) {
						int foundTrue = 0;
						if (instanceResult) { foundTrue++; }
						try {
						  if (((BooleanClassification) wekaNB.classifications.get(primaryClassification)).getValue().getBooleanValue()) {  foundTrue++; }
						  if (((BooleanClassification) wekaSMO.classifications.get(primaryClassification)).getValue().getBooleanValue()) {  foundTrue++; }
						}
						catch (Exception e) {
							
						}
						if (foundTrue > 2) {
							foldMatrix.setCell(d.sentence.hasBooleanClassification(primaryClassification), true);
						}
						else if (foundTrue <2) {
							foldMatrix.setCell(d.sentence.hasBooleanClassification(primaryClassification), false);
						}
						else {
							foldMatrix.setCell(d.sentence.hasBooleanClassification(primaryClassification), ((BooleanClassification) wekaSMO.classifications.get(primaryClassification)).getValue().getBooleanValue());

						}

					}
					else {
						foldMatrix.setCell(d.sentence.hasBooleanClassification(primaryClassification), instanceResult);
					}
					/*
					if ( d.sentence.hasBooleanClassification(primaryClassification) != bc.getValue().getBooleanValue()) {
						//System.out.println ("Mistake("+primaryClassification+",actual="+s.hasBooleanClassification(primaryClassification)+"): "+s);
					}
					
					if ( verbose && d.sentence.hasBooleanClassification(primaryClassification) != bc.getValue().getBooleanValue()) {
						System.out.println ("Mistake("+primaryClassification+","+d.sentence.hasBooleanClassification(primaryClassification)+"): "+d.sentence);
					}					
					*/
					
					
					System.out.print(ca.getAbbreviation());
					System.out.print("\t");
					System.out.print(d.sentence);
					System.out.print("\t");					
					System.out.print(d.sentence.hasBooleanClassification(primaryClassification));
					System.out.print("\t");
					System.out.print(kNNResult.classifications.get(primaryClassification).getValue());
					System.out.print("\t");
					System.out.print(kNNResult.averageDistance);


					System.out.print("\t");
					System.out.print(wekaNB.classifications.get(primaryClassification).getValue());
					System.out.print("\t");			
					System.out.print(this.getRatio( wekaNB.distributions.get(primaryClassification)));

					
					System.out.print("\t");
					System.out.print(wekaSMO.classifications.get(primaryClassification).getValue());
					System.out.print("\t");			
					System.out.print(this.getRatio( wekaSMO.distributions.get(primaryClassification)));
					
					System.out.println();
					
					
				}
				
				if (verbose) {
					System.out.println("Fold #"+i+"  "+ foldMatrix);
				}
				
				attributeMatrix.add(foldMatrix);
			}
			
			//average the results, and report
			System.out.println("Test Paramaters: k="+k+", n="+numberOfFolds+", threshold="+threshold+", attribute="+primaryClassification);
			System.out.println("Sentence Distance Method: "+sentenceDistance.getMethodName());
			System.out.println("TOTAL:  "+ attributeMatrix);

			System.out.println("Precision: "+ attributeMatrix.getPrecision());
			System.out.println("Recall: "+ attributeMatrix.getRecall());
			System.out.println("True negative rate: "+attributeMatrix.getTrueNegativeRate());
			System.out.println("Accuracy: "+attributeMatrix.getAccuracy());
			System.out.println("F-Measure:" +attributeMatrix.getF1Measure());			
			
			//_mainFrame.setStatusMessage("Self-evaluation generated for " + ca.getName());
			totalMatrix.add(attributeMatrix);
			precision += attributeMatrix.getPrecision();
			recall    += attributeMatrix.getRecall();
			f1        += attributeMatrix.getF1Measure();
		}
		System.out.println("************************************************************");
		System.out.println("TOTAL:  "+ totalMatrix);

		System.out.println("Precision: "+ totalMatrix.getPrecision()+"\t"+ precision/numberOfAttributesEvaluted);
		System.out.println("Recall: "+ totalMatrix.getRecall() +"\t"+ recall/numberOfAttributesEvaluted);
		//System.out.println("True negative rate: "+totalMatrix.getTrueNegativeRate());
		//System.out.println("Accuracy: "+totalMatrix.getAccuracy());
		System.out.println("F-Measure:" +totalMatrix.getF1Measure() +"\t"+ f1/numberOfAttributesEvaluted);			
	
	
		
	}	
	
	private double getRatio(double[] distribution) {
		double max = Math.max(distribution[0],distribution[1]);
		double min = Math.min(distribution[0],distribution[1]);
		return max/min;
	}
	
	
	public void performOverallCurrentDocumentEvaluate(ArrayList<Sentence> testSentences) {
		System.out.println("Ensuring classifiers are created");
		this.createWekaInterfacer("genericID");
		
		ConfusionMatrix totalMatrix = new ConfusionMatrix();
		
		double precision = 0.0;
		double recall    = 0.0;
		double f1        = 0.0;
		double numberOfAttributesEvaluted = 0.0;
		boolean verbose = false;
		
		ArrayList<Document> trainDocuments = new ArrayList<Document>();
		for (Document d: GCController.getTheGCController().getInstanceLearner().getTrainedSentences()) {
			Document newDoc = new Document();
			newDoc.sentence = d.sentence;
			newDoc.inTest   = false;
			newDoc.unlabeledTraining = false;
			trainDocuments.add(newDoc);
		}
		
		ArrayList<Document> testDocuments = new ArrayList<Document>();
		for (Sentence s: testSentences) {
			Document newDoc = new Document();
			newDoc.sentence = s;
			newDoc.inTest   = true;
			newDoc.unlabeledTraining = false;
			testDocuments.add(newDoc);
		}
		
		
		for (ClassificationAttribute ca: _classificationAttributes) {
			if (ca.getIncludeInEvaluation() == false) { continue; }
			String primaryClassification = ca.getName();
			numberOfAttributesEvaluted++;
			
			ClassifierOptions options  = new ClassifierOptions( ca, ClassifierOptions.FOLD_RANDOM, WordType.LEMMA, 10, false, StopWord.getListByName(StopWord.DETERMINER), false);
			this.setClassifierOptions(options);
			ConfusionMatrix attributeMatrix = new ConfusionMatrix();
			
			SentenceDistance sentenceDistance = options.getSentenceDistance();
			int k = options.getK();
			double threshold = options.getThreshold();
			
				
			//need to reset term and internal naive
			//need to re-create weka
			this.createWekaInterfacer(trainDocuments,"trainingFold");
			this.createInstanceLearner(trainDocuments);
				
			for (Document d: testDocuments) {
				ArrayList<Sentence> sentences = new ArrayList<Sentence>();
				sentences.add(d.sentence);
				//Instances testDataSetSentence= (new WekaCreator()).createWekaInstancesAsString("testFold",sentences,_classificationAttributes,false,false);
				WekaCreatorOptions wo = new WekaCreatorOptions();
				wo.setExportSentenceAsString(true);
				wo.setNERSentenceIndicators(false);
				wo.setWordType( this.getClassifierOptions().getWordType());
				wo.setStopWords(this.getClassifierOptions().getStopWords());
				wo.setUseOriginalSentence(false);		
						
				Instances testDataSetSentence = (new WekaCreator()).createWekaInstancesForClassifications("testFold",sentences,_classificationAttributes,wo);

				ClassificationResult kNNResult = this.getInstanceLearner().getClassification(d.sentence, k, false, true,false, sentenceDistance);
				ClassificationResult wekaNB     = this.getWekaInterfacer().classifyWithNaiveBayesMultinomial(testDataSetSentence);
				ClassificationResult wekaSMO    = this.getWekaInterfacer().classifyWithSMO(testDataSetSentence);

					
				boolean instanceResult = ((BooleanClassification) kNNResult.classifications.get(primaryClassification)).getValue().getBooleanValue();
					
				if (kNNResult.averageDistance/d.sentence.getNumberOfNodes() > .6) {
					int foundTrue = 0;
					if (instanceResult) { foundTrue++; }
					if (((BooleanClassification) wekaNB.classifications.get(primaryClassification)).getValue().getBooleanValue()) {  foundTrue++; }
					if (((BooleanClassification) wekaSMO.classifications.get(primaryClassification)).getValue().getBooleanValue()) {  foundTrue++; }
					if (foundTrue > 2) {
						attributeMatrix.setCell(d.sentence.hasBooleanClassification(primaryClassification), true);
					}
					else if (foundTrue <2) {
						attributeMatrix.setCell(d.sentence.hasBooleanClassification(primaryClassification), false);
					}
					else {
						attributeMatrix.setCell(d.sentence.hasBooleanClassification(primaryClassification),((BooleanClassification)  wekaSMO.classifications.get(primaryClassification)).getValue().getBooleanValue());
					}

				}
				else {
					attributeMatrix.setCell(d.sentence.hasBooleanClassification(primaryClassification), instanceResult);
				}

					
				if (verbose) {
					System.out.print(ca.getAbbreviation());
					System.out.print("\t");
					System.out.print(d.sentence);
					System.out.print("\t");					
					System.out.print(d.sentence.hasBooleanClassification(primaryClassification));
					System.out.print("\t");
					System.out.print(kNNResult.classifications.get(primaryClassification).getValue());
					System.out.print("\t");
					System.out.print(kNNResult.averageDistance);
					System.out.print("\t");
					System.out.print(wekaNB.classifications.get(primaryClassification).getValue());
					System.out.print("\t");			
					System.out.print(this.getRatio( wekaNB.distributions.get(primaryClassification)));

					
					System.out.print("\t");
					System.out.print(wekaSMO.classifications.get(primaryClassification).getValue());
					System.out.print("\t");			
					System.out.print(this.getRatio( wekaSMO.distributions.get(primaryClassification)));
					
					System.out.println();
				}
			}			
			
			//average the results, and report
			System.out.println("Test Paramaters: k="+k+",  threshold="+threshold+", attribute="+primaryClassification);
			System.out.println("Sentence Distance Method: "+sentenceDistance.getMethodName());
			System.out.println("TOTAL:  "+ attributeMatrix);

			System.out.println("Precision: "+ attributeMatrix.getPrecision());
			System.out.println("Recall: "+ attributeMatrix.getRecall());
			System.out.println("True negative rate: "+attributeMatrix.getTrueNegativeRate());
			System.out.println("Accuracy: "+attributeMatrix.getAccuracy());
			System.out.println("F-Measure:" +attributeMatrix.getF1Measure());			
			
			//_mainFrame.setStatusMessage("Self-evaluation generated for " + ca.getName());
			totalMatrix.add(attributeMatrix);
			precision += attributeMatrix.getPrecision();
			recall    += attributeMatrix.getRecall();
			f1        += attributeMatrix.getF1Measure();
		}
		System.out.println("************************************************************");
		System.out.println("TOTAL:  "+ totalMatrix);

		System.out.println("Precision: "+ totalMatrix.getPrecision()+"\t"+ precision/numberOfAttributesEvaluted);
		System.out.println("Recall: "+ totalMatrix.getRecall() +"\t"+ recall/numberOfAttributesEvaluted);
		//System.out.println("True negative rate: "+totalMatrix.getTrueNegativeRate());
		//System.out.println("Accuracy: "+totalMatrix.getAccuracy());
		System.out.println("F-Measure:" +totalMatrix.getF1Measure() +"\t"+ f1/numberOfAttributesEvaluted);			
	
	}
	
	

	
	/**
	 * This method evaluates the sentences in the current learner.  It doesn't create folds,
	 * but rather assumes that a certain percentage of the document has already been evalaute.
	 * It will produce classifications result from the step% to 100% of the document in the learner
	 * 
	 */
	public void performSelfEvaluateByWorkComplete(ClassificationAttribute ca) {
		System.out.println("Ensuring classifiers are created");
		this.createWekaInterfacer("genericID");
		
		double stepPercent = 0.02;
		
		boolean verbose = false;
		
		ArrayList<Document> documents = new ArrayList<Document>();
		for (Document d: GCController.getTheGCController().getInstanceLearner().getTrainedSentences()) {
			Document newDoc = new Document();
			newDoc.sentence = d.sentence;
			newDoc.inTest   = false;
			newDoc.unlabeledTraining = false;
			documents.add(newDoc);
		}
		
		String primaryClassification = ca.getName();

			
		ClassifierOptions options  = new ClassifierOptions( ca,ClassifierOptions.FOLD_RANDOM, WordType.LEMMA, 10, false, StopWord.getListByName(StopWord.DETERMINER), false);
		this.setClassifierOptions(options);
		
		for (double testPercent=stepPercent; testPercent<= 1.00; testPercent += stepPercent) {
			java.util.ArrayList<java.util.ArrayList<Document>> folds = DocumentFoldCreator.createFoldsByPercentage(documents, testPercent);

			SentenceDistance sentenceDistance = options.getSentenceDistance();
			int k = options.getK();
			k=1;
			double threshold = options.getThreshold();
			
			java.util.ArrayList<Document> fold = folds.get(0);  // we only ever have one fold.  x% for the train, the rest is test
			clearAllTestingFlags(documents);
			setTestingFlagForFold(fold);
				
			//need to reset term and internal naive
			//need to re-create weka
			this.createWekaInterfacer(documents,"testingFold");
			this.createInstanceLearner(documents);
				
				
			ConfusionMatrix foldMatrix = new ConfusionMatrix();
				
			for (Document d:fold) {
				ArrayList<Sentence> sentences = new ArrayList<Sentence>();
				sentences.add(d.sentence);
				//Instances testDataSetSentence= (new WekaCreator()).createWekaInstancesAsString("testFold",sentences,_classificationAttributes,false,false);
				WekaCreatorOptions wo = new WekaCreatorOptions();
				wo.setExportSentenceAsString(true);
				wo.setNERSentenceIndicators(false);
				wo.setWordType( this.getClassifierOptions().getWordType());
				wo.setStopWords(this.getClassifierOptions().getStopWords());
				wo.setUseOriginalSentence(false);		
							
				Instances testDataSetSentence = (new WekaCreator()).createWekaInstancesForClassifications("testFold",sentences,_classificationAttributes,wo);

				ClassificationResult kNNResult = this.getInstanceLearner().getClassification(d.sentence, k, false, true,false, sentenceDistance);
				ClassificationResult wekaNB     = this.getWekaInterfacer().classifyWithNaiveBayesMultinomial(testDataSetSentence);
				ClassificationResult wekaSMO    = this.getWekaInterfacer().classifyWithSMO(testDataSetSentence);

					
				boolean instanceResult = ((BooleanClassification) kNNResult.classifications.get(primaryClassification)).getValue().getBooleanValue();
					
				if (kNNResult.averageDistance/d.sentence.getNumberOfNodes() > .4) {// || kNNResult.k > (k+1)) {   //what if we used to many neighbors?  I've moved this down from .6 to .4
					int foundTrue = 0;
					if (instanceResult) { foundTrue++; }
					if (((BooleanClassification) wekaNB.classifications.get(primaryClassification)).getValue().getBooleanValue()) {  foundTrue++; }
					if (((BooleanClassification) wekaSMO.classifications.get(primaryClassification)).getValue().getBooleanValue()) {  foundTrue++; }
					if (foundTrue > 2) {
						foldMatrix.setCell(d.sentence.hasBooleanClassification(primaryClassification), true);
					}
					else if (foundTrue <2) {
						foldMatrix.setCell(d.sentence.hasBooleanClassification(primaryClassification), false);
					}
					else {
						foldMatrix.setCell(d.sentence.hasBooleanClassification(primaryClassification), ((BooleanClassification) wekaSMO.classifications.get(primaryClassification)).getValue().getBooleanValue());
					}
				}
				else {
					foldMatrix.setCell(d.sentence.hasBooleanClassification(primaryClassification), instanceResult);
				}
				/*
				if ( d.sentence.hasBooleanClassification(primaryClassification) != bc.getValue().getBooleanValue()) {
					//System.out.println ("Mistake("+primaryClassification+",actual="+s.hasBooleanClassification(primaryClassification)+"): "+s);
				}
				
				if ( verbose && d.sentence.hasBooleanClassification(primaryClassification) != bc.getValue().getBooleanValue()) {
					System.out.println ("Mistake("+primaryClassification+","+d.sentence.hasBooleanClassification(primaryClassification)+"): "+d.sentence);
				}					
				*/
					
				if (verbose) {
					System.out.print(ca.getAbbreviation());
					System.out.print("\t");
					System.out.print(d.sentence);
					System.out.print("\t");					
					System.out.print(d.sentence.hasBooleanClassification(primaryClassification));
					System.out.print("\t");
					System.out.print(kNNResult.classifications.get(primaryClassification).getValue());
					System.out.print("\t");
					System.out.print(kNNResult.averageDistance);
					System.out.print("\t");
					System.out.print(wekaNB.classifications.get(primaryClassification).getValue());
					System.out.print("\t");			
					System.out.print(this.getRatio( wekaNB.distributions.get(primaryClassification)));
	
						
					System.out.print("\t");
					System.out.print(wekaSMO.classifications.get(primaryClassification).getValue());
					System.out.print("\t");			
					System.out.print(this.getRatio( wekaSMO.distributions.get(primaryClassification)));
						
					System.out.println();
				}			
			}  // end of testing the fold
				
			
			// Report on the fold

			
			if (verbose) {
				System.out.println("Test Paramaters: k="+k+", % in training ="+testPercent+", threshold="+threshold+", attribute="+primaryClassification);
				System.out.println("Sentence Distance Method: "+sentenceDistance.getMethodName());
				System.out.println("TOTAL:  "+ foldMatrix);

				System.out.println("Precision: "+ foldMatrix.getPrecision());
				System.out.println("Recall: "+ foldMatrix.getRecall());
				System.out.println("True negative rate: "+foldMatrix.getTrueNegativeRate());
				System.out.println("Accuracy: "+foldMatrix.getAccuracy());
				System.out.println("F-Measure:" +foldMatrix.getF1Measure());				
			}
			else {
				System.out.printf("%1.3g\t%1.3g\n",testPercent,foldMatrix.getF1Measure());
			}
			
			
		}

	}
}

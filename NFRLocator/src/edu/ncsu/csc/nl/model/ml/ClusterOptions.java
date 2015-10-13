package edu.ncsu.csc.nl.model.ml;

import javax.swing.JOptionPane;

import edu.ncsu.csc.nl.model.distance.LevenshteinSentenceAsWordsDistance;
import edu.ncsu.csc.nl.model.distance.SentenceDistance;

public class ClusterOptions {
	public static final int CANCELLED_OPERATION = -1;
	
	public static final String CLUSTER_KMEDOIDS = "kMedoids";
	public static final String CLUSTER_KMEDOIDS_THRESHOLD = "kMedoids-threshold";
	public static final String CLUSTER_KMEDOIDS_EXPAND_K  = "kMedoids - expand k";
	
	private static Object[] clusterMethods = {CLUSTER_KMEDOIDS,CLUSTER_KMEDOIDS_THRESHOLD,CLUSTER_KMEDOIDS_EXPAND_K};
	private static String defaultClusterMethod = CLUSTER_KMEDOIDS_THRESHOLD;

	private static Object[] distanceMethods = {"CosineTermFreqDistance","CosineTermFreqIDFDistance","DiceWordSetDistance","GraphWalkDistance","JacardWordSetDistance","LevenshteinSentenceAsStringDistance","LevenshteinSentenceAsWordsDistance","LinearWalkDistance","TreeRelationshipAsLevenshteinDistance"};
	private static String defaultDistanceMethod = "LevenshteinSentenceAsWordsDistance";


	
	
	
	/** methods will set this message to identify issues if an error occurred */
	private String _lastOperationMessage = null;

	private int _k = 30;
	private double _threshold = 1.15;
	private String _clusterMethod = defaultClusterMethod;
	private SentenceDistance _sentenceDistance = new LevenshteinSentenceAsWordsDistance();
	
	public ClusterOptions() {
	}
	
	public ClusterOptions(SentenceDistance sd, String clusterMethod, int k, double threshold) {
		_k = k;
		_threshold = threshold;
		_clusterMethod = clusterMethod;
		_sentenceDistance = sd;
	}


	public SentenceDistance getSentenceDistanceMethodFromUser() {
		SentenceDistance sentenceDistance = null;
		
		Object result = JOptionPane.showInputDialog(null, "Select sentence distance algorithm", "Cluster", JOptionPane.PLAIN_MESSAGE,null, distanceMethods,defaultDistanceMethod);
		try {
			sentenceDistance = (SentenceDistance) Class.forName("edu.ncsu.csc.nl.model.distance."+result.toString()).newInstance();
		}
		catch (Throwable t){
			_lastOperationMessage = t.toString();
			return null;
		}	
	
		return sentenceDistance;
	}	
	
	public String getClusterMethodFromUser() {
		String result = null;
		
		Object o = JOptionPane.showInputDialog(null, "Select cluster method", "Cluster", JOptionPane.PLAIN_MESSAGE,null, clusterMethods,defaultClusterMethod);
		if (o != null) { 
			result = o.toString(); 
			_clusterMethod = result;
		}
		return result;
	}
	
	/**
	 * Gets the value to be used from k from the user.  If a bad value is entered, -1 is returned;
	 * 
	 * @param maxSize what is the maximum value that can be set for k?
	 * @return the value the user entered.  If user cancel's the operation, CANCELLED_OPERATION is returned.
	 */
	public int getKvalueFromUser() {
		String value = JOptionPane.showInputDialog(null,"<html>Enter value for <i>k</i>:</html>","Cluster", JOptionPane.QUESTION_MESSAGE);
		int k = CANCELLED_OPERATION;
		
		try {
			k = Integer.parseInt(value);
			if (k<1) {
				_lastOperationMessage = "User entered a bad integer value for k: "+value;
				return CANCELLED_OPERATION;
			}
			_k = k;
		}
		catch (Throwable t) {
			_lastOperationMessage = "User entered a bad integer value for k: "+value;
			return CANCELLED_OPERATION;			
		}
		return k;
	}
	

	public double getThresholdFromUser() {
		String value = JOptionPane.showInputDialog(null,"<html>Threshold: </html>","Cluster", JOptionPane.QUESTION_MESSAGE);
		double threshold = CANCELLED_OPERATION;
		try {
			threshold = Double.parseDouble(value);
		}
		catch (Throwable t) {
			_lastOperationMessage = "Bad value for threshold: "+ value;
			return CANCELLED_OPERATION;			
		}
		return threshold;
	}

	
	public String getClusterMethod()  { return _clusterMethod; }
	public int getK()                        { return _k; }
	public double getThreshold()             { return _threshold; }
	public String getLastOperationMessage() { return _lastOperationMessage; }
	public SentenceDistance getSentenceDistance() { return _sentenceDistance; }
	
	public boolean wasCancelled() {
		return _lastOperationMessage != null;
	}
	
	/**
	 * Creates a user expirement options from the items selected by the user
	 * 
	 * @param numberOfSentences
	 * @param classifications
	 * @return ClusterOption object.  If the user cancels any of the actions, a mesage is set
	 */
	public static ClusterOptions establishOptionsFromUserFeedback() {
		ClusterOptions eo = new ClusterOptions();
		
		eo._k = eo.getKvalueFromUser();
		if (eo._k == CANCELLED_OPERATION) { return eo; }
		
		eo._threshold = eo.getThresholdFromUser();
		if (eo._threshold == CANCELLED_OPERATION) { return eo; }
		
		
		eo._clusterMethod = eo.getClusterMethodFromUser();
		if (eo._clusterMethod == null) { return eo; }
		
		eo._sentenceDistance = eo.getSentenceDistanceMethodFromUser();
		if (eo._sentenceDistance == null) { return eo; }		
		
		return eo;
	}
	
	
}
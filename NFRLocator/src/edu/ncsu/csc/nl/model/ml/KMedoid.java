package edu.ncsu.csc.nl.model.ml;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;

import edu.ncsu.csc.nl.model.Sentence;

/**
 * implements a k-medoid cluster algorithm
 * 
 * Algorithm:
 * - Initiate k clusters by randomly selecting k sentences to use as the initial medoids
 * - For each of the remaining sentences
 *   -- find which medoid the sentences is closest to and place in that cluster
 *   
 * - repeat until no changes in medoids
 *   -- foreach cluster, find which sentence has the lowest distance to all of the others
 *   -- use those sentences as the new medoids
 *   -- place the remaining sentences into their closest cluster
 * 
 * @author John Slankas
 */
public class KMedoid {

	private ClusterOptions _clusterOptions;
	private SecureRandom _random = new SecureRandom();
	
	/**
	 * Establishes the initial cluster object.
	 * 
	 * @param k
	 * @param sd
	 */
	public KMedoid(ClusterOptions clusterOptions) {
		_clusterOptions = clusterOptions;
	}

	public ArrayList<SentenceCluster> cluster(ArrayList<Sentence> sentences) {
		if (_clusterOptions.getClusterMethod().equals(ClusterOptions.CLUSTER_KMEDOIDS)) {
			return clusterFixed(sentences);
		}
		if (_clusterOptions.getClusterMethod().equals(ClusterOptions.CLUSTER_KMEDOIDS_EXPAND_K)) {
			return expandableClustersWithValidationOfInitialMedoids(sentences);
		}
		if (_clusterOptions.getClusterMethod().equals(ClusterOptions.CLUSTER_KMEDOIDS_THRESHOLD)) {
			return clusterWithValidationOfInitialMedoids(sentences);
		}
		System.err.println("No valid cluster method: "+_clusterOptions.getClusterMethod());
		return null;
	}
	
	
	
	private ArrayList<SentenceCluster> clusterFixed(ArrayList<Sentence> sentences) {
		ArrayList<Sentence> workingCopy = new ArrayList<Sentence>(sentences);
		
		ArrayList<SentenceCluster> clusters = new ArrayList<SentenceCluster>();
		for (int i=0;i < _clusterOptions.getK(); i++) {
			int randSentencePosition = _random.nextInt(workingCopy.size());
			SentenceCluster sc = new SentenceCluster(workingCopy.remove(randSentencePosition));
			clusters.add(sc);
		}
		this.placeSentencesIntoClusters(clusters,workingCopy);
		
		while (true) {
System.out.println(System.currentTimeMillis());
			HashSet<Sentence> currentMedoids = this.getMedoids(clusters);
			HashSet<Sentence> newMedoids = this.getNewMedoids(clusters);
			
			if (currentMedoids.equals(newMedoids)) { break; } // algorithm has converged
			
			clusters = new ArrayList<SentenceCluster>();
			workingCopy = new ArrayList<Sentence>(sentences);
			for (Sentence medoid: newMedoids) {
				clusters.add(new SentenceCluster(medoid));
				workingCopy.remove(medoid);
			}
			this.placeSentencesIntoClusters(clusters,workingCopy);
		}
		return clusters;
	}

	private ArrayList<SentenceCluster> clusterWithValidationOfInitialMedoids(ArrayList<Sentence> sentences) {
		ArrayList<Sentence> workingCopy = new ArrayList<Sentence>(sentences);
		
		ArrayList<SentenceCluster> clusters = new ArrayList<SentenceCluster>();

		for (int i=0;i < _clusterOptions.getK(); i++) {
			int tries = 0;
			while (true) {
				tries++;
			
				int randSentencePosition = _random.nextInt(workingCopy.size());
				Sentence s= workingCopy.get(randSentencePosition);
				double d = findMinimumDistanceFromCenters(clusters,s);
				if (tries > 1000 || (d/s.getNumberOfNodes() > 0.85)) {
					workingCopy.remove(randSentencePosition);
					SentenceCluster sc = new SentenceCluster(s);
					clusters.add(sc);
					break;
				}
			}
		}
//System.out.println("count: "+count);
//System.out.println("k:"+_k);
//System.out.println("Working Copy size:"+workingCopy.size());
//System.out.println("Cluster count: "+ clusters.size());
		this.placeSentencesIntoClusters(clusters,workingCopy);
		
		while (true) {
//System.out.println(System.currentTimeMillis());
			HashSet<Sentence> currentMedoids = this.getMedoids(clusters);
			HashSet<Sentence> newMedoids = this.getNewMedoids(clusters);
			
			if (currentMedoids.equals(newMedoids)) { break; } // algorithm has converged
			
			clusters = new ArrayList<SentenceCluster>();
			workingCopy = new ArrayList<Sentence>(sentences);
//System.out.println("Working Copy size:"+workingCopy.size());
			
			for (Sentence medoid: newMedoids) {
				clusters.add(new SentenceCluster(medoid));
				workingCopy.remove(medoid);
			}
//System.out.println("Working Copy size:"+workingCopy.size());
			
			this.placeSentencesIntoClusters(clusters,workingCopy);
		}
		return clusters;
	}
	
	private ArrayList<SentenceCluster> expandableClustersWithValidationOfInitialMedoids(ArrayList<Sentence> sentences) {
		ArrayList<Sentence> workingCopy = new ArrayList<Sentence>(sentences);
		
		ArrayList<SentenceCluster> clusters = new ArrayList<SentenceCluster>();
		for (int i=0; i< workingCopy.size(); i++) {	
			Sentence s = workingCopy.get(i);
			double d = findMinimumDistanceFromCenters(clusters,s);
			if (d > _clusterOptions.getThreshold()) {
				workingCopy.remove(i);
				SentenceCluster sc = new SentenceCluster(s);
				clusters.add(sc);
				i--; // need to re-evaluate the same index as we have just shifted things down into it.
			}
		}

		this.placeSentencesIntoClusters(clusters,workingCopy);
		
		while (true) {
//System.out.println(System.currentTimeMillis());
			HashSet<Sentence> currentMedoids = this.getMedoids(clusters);
			HashSet<Sentence> newMedoids = this.getNewMedoids(clusters);
			
			if (currentMedoids.equals(newMedoids)) { break; } // algorithm has converged
			
			clusters = new ArrayList<SentenceCluster>();
			workingCopy = new ArrayList<Sentence>(sentences);
			for (Sentence medoid: newMedoids) {
				clusters.add(new SentenceCluster(medoid));
				workingCopy.remove(medoid);
			}
			this.placeSentencesIntoClusters(clusters,workingCopy);
		}
		return clusters;
	}	
	
	
	private double findMinimumDistanceFromCenters(ArrayList<SentenceCluster> clusters, Sentence  s) {
		double result = Double.MAX_VALUE;
		
		for (SentenceCluster sc: clusters) {
			double distance = _clusterOptions.getSentenceDistance().computeDistance(s, sc.getCenter());
			result = Math.min(distance,  result);
		}
		return result;
	}
	
	private void placeSentencesIntoClusters(ArrayList<SentenceCluster> clusters, ArrayList<Sentence> sentences) {
		for (Sentence s: sentences) {
			double smallestDistance = Double.MAX_VALUE;
			SentenceCluster smallestCluster = null;
			
			for (SentenceCluster sc: clusters) {
				double d = sc.getDistanceFromCenter(_clusterOptions.getSentenceDistance(), s);
				if (d < smallestDistance) {
					smallestCluster = sc;
					smallestDistance= d;
				}
			}
			smallestCluster.add(s);
		}
	}
	
	private HashSet<Sentence> getMedoids(ArrayList<SentenceCluster> clusters) {
		HashSet<Sentence> result = new HashSet<Sentence>();
		
		for (SentenceCluster sc: clusters) {
			result.add(sc.getCenter());
		}
		return result;
	}
	
	private HashSet<Sentence> getNewMedoids(ArrayList<SentenceCluster> clusters) {
		HashSet<Sentence> result = new HashSet<Sentence>();
		
		for (SentenceCluster sc: clusters) {
			result.add(sc.computeNewMediod(_clusterOptions.getSentenceDistance()));
		}
		return result;
	}
	
	
}

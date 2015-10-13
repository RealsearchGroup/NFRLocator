package edu.ncsu.csc.nl.model.ml;

import java.util.ArrayList;

import edu.ncsu.csc.nl.model.distance.SentenceDistance;
import edu.ncsu.csc.nl.model.Sentence;

/**
 * Represents a cluster of sentences with a given medoid.  
 * 
 * @author John Slankas
 *
 */
public class SentenceCluster {
	
	private Sentence _medoid;  // TODO: make this a classifiableItem
	
	private ArrayList<Sentence> _members = new ArrayList<Sentence>();
	
	public SentenceCluster(Sentence medoid) {
		_medoid = medoid;
		_members.add(medoid);
	}

	public Sentence getCenter() {
		return _medoid;
	}
	
	public void add(Sentence s) {
		_members.add(s);
	}
	
	public int getSize() {
		return _members.size();
	}
	
	public ArrayList<Sentence> getMembers() {
		return _members;
	}
	
	public Sentence computeNewMediod(SentenceDistance sd) {
		double lowestDistance = Double.MAX_VALUE;
		Sentence lowestMedoid = null;
		
		for (Sentence s: _members) {
			double distance = this.getTotalDistanceFromSentence(sd, s);
			if (distance < lowestDistance) {
				lowestDistance = distance;
				lowestMedoid = s;
			}
		}
	
		return lowestMedoid;
	}
	
	public double getDistanceFromCenter(SentenceDistance sd, Sentence s) {
		return sd.computeDistance(s, this.getCenter());
	}
	
	public double getTotalDistanceFromSentence(SentenceDistance sd, Sentence s){
		double total = 0.0;
		
		for (Sentence t: _members) {
			total += sd.computeDistance(s, t);
		}
		return total;
	}
	
	public double getTotalDistance(SentenceDistance sd){
		double total = 0.0;
		
		for (Sentence t: _members) {
			total += this.getTotalDistanceFromSentence(sd, t);
		}
		return total;
	}
	
}

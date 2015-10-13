package edu.ncsu.csc.nl.model;

import java.util.List;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;


public class WordNetSynonymSet {
	
	private int _distance;
	private String _id;
	private String _relationship;  // how was this SynSet derived? ie, hypernym, hyponym
	private String _definition;
	private List<IWord> _iwords;
	private ISynsetID _synSetID;
	
	public static WordNetSynonymSet createFromSynSet(ISynset synset, int distance, String ID, String relationship, int parentNumber, int myNumber) {
		//Dictionary dict = RBACController.getTheRBACController().getWordNetDictionary();
	
		WordNetSynonymSet result = new WordNetSynonymSet();
		
		result._distance = distance;
		if (ID.equals("")) {
			result._id  = parentNumber + "."+myNumber+"("+relationship+")";
		}
		else {
			result._id = ID + "." +myNumber+"("+relationship+")";
		}
		result._relationship = relationship;
		result._definition = synset.getGloss();
		result._iwords = synset.getWords();
		result._synSetID = synset.getID();
		
		return result;
	}
	
	public int getDistance() {
		return _distance;
	}
	
	public String getID() {
		return _id;
	}
	
	public String getRelationship() {
		return _relationship;
	}
	
	public String getDefinition() {
		return _definition;
	}
	
	public boolean containsWord(String word) {
		for (int j=0;j<_iwords.size();j++) {
			String l = _iwords.get(j).getLemma().replace('_', ' ');
			if (word.equals(l)) {
				return true;
			}
		}
		
		return false;
	}
	
	public String getWordList() {	
		StringBuffer temp = new StringBuffer();
		for (int j=0;j<_iwords.size();j++) {
			if (j>0) { temp.append(", "); }
			temp.append(_iwords.get(j).getLemma());
		}
		return temp.toString();
	}
	
	public int hashCode() {
		return _synSetID.toString().hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof WordNetSynonymSet == false) {
			return false;
		}
		WordNetSynonymSet d = (WordNetSynonymSet) o;
		return d.getSynSetID().equals(this.getSynSetID());
	}
	
	public String getSynSetID() {
		return  _synSetID.toString();
	}
}

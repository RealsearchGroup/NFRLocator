package edu.ncsu.csc.nl.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IPointer;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.item.WordID;
import edu.ncsu.csc.nl.GCController;
import edu.ncsu.csc.nl.model.type.PartOfSpeech;

/**
 * 
 * There are two types of synonyms:
 * - those generated automatically by the meanings we include from wordnet as the word is defined
 * - entries that the user manually adds
 * 
 * @author John
 *
 */
public class WordNetEntry implements Serializable, TableModel {
	
	public static final long serialVersionUID = 1;

	/** this is base word */
	private String _lemma;
	private PartOfSpeech _partOfSpeech;
	
	/** list of word IDs associated with this dictionary entry */
	private transient IWordID[] _wordnetIWordID;
	
	/** these correspond to wordnetIWord ID's above.  Needed as we can't serialize iWordIDs, so we use
	 *  these to re-establish the dictionary when it is loaded.
	 */
	private String[] _iWordIDAsString;

	/** This is a list of additional works that can be mapped back this particular entry. comma separated. */
	private String _userDefinedSynonyms = "";
	
	private transient String[][] _tableData = null; // on any type of change, set this to null;
	
	private transient HashSet<String> _synonyms = null;  // This is a list of the all of the initial synonyms
	
	public WordNetEntry(String lemma, PartOfSpeech partOfSpeech) {
		_lemma = lemma;
		_partOfSpeech = partOfSpeech;
		_wordnetIWordID = new IWordID[0];
		_iWordIDAsString = new String[0];
	}

	public String toString() {
		return _lemma;
	}
	
	/**
	 * Tests whether or not two objects are the same.
	 * Equality is based upon the lemma, partofspeech, and domainflag being the same
	 */
	public boolean equals(Object o) {
		if (o instanceof WordNetEntry == false ) {
			return false;
		}
		WordNetEntry entry = (WordNetEntry) o;
 			
		return this._lemma.equals(entry._lemma) && this._partOfSpeech == entry._partOfSpeech;
	}
	
	public static WordNetEntry createFromWordNet(String lemma, POS pos) {
		if (pos == null) {return null;}
		
		Dictionary dict = GCController.getTheGCController().getWordNetDictionary();
		
		IIndexWord idxWord = dict.getIndexWord(lemma,pos);
		if (idxWord == null) {
			return null;
		}
		PartOfSpeech newPos;
		if      (pos.equals(POS.ADJECTIVE)) { newPos = PartOfSpeech.JJ; }
		else if (pos.equals(POS.ADVERB))    { newPos = PartOfSpeech.RB; }
		else if (pos.equals(POS.NOUN))      { newPos = PartOfSpeech.NN; }
		else                                { newPos = PartOfSpeech.VB; }
		
		WordNetEntry result = new WordNetEntry(lemma, newPos);
		
		List<IWordID> ids = idxWord.getWordIDs(); //get all of the different entries
		result._wordnetIWordID = new IWordID[ids.size()];
		for (int i=0;i< ids.size();i++) {
			result._wordnetIWordID[i] = ids.get(i);
		}
		result.setStringArrayFromIDs();
		
		return result;
	}
	
	public void setWordIDs(IWordID[] values) {
		_wordnetIWordID = values;
		this.setStringArrayFromIDs();
	}
	
	private void setStringArrayFromIDs() {
		_iWordIDAsString = new String[_wordnetIWordID.length];
		for (int i=0;i<_wordnetIWordID.length;i++) {
			_iWordIDAsString[i] = _wordnetIWordID[i].toString();
		}
		
	}

	public void setWordIDFromStringArray() {
		_wordnetIWordID = new IWordID[_iWordIDAsString.length];
		for (int i=0;i<_wordnetIWordID.length;i++) {
			_wordnetIWordID[i] = WordID.parseWordID(_iWordIDAsString[i]);
		}
	}
	
	
	
	public String getLemma() {
		return _lemma;
	}
	
	
	public PartOfSpeech getPartOfSpeech() {
		return _partOfSpeech;
	}
	
	/** 
	 * returns the number of entries in the princeton wordnet dictionary, which corresponds to the meanings involved
	 * 
	 * @return
	 */
	public int getNumberOfMeanings() {
		return _wordnetIWordID.length;
	}

	public IWordID getWordNetID(int index) {
		return _wordnetIWordID[index];
	}
	
	
	public HashSet<String> getAllSynonyms() {
		if (_synonyms == null) {
			_synonyms = new HashSet<String>();

			int numberOfMeanings = this.getNumberOfMeanings();
		
			Dictionary dict = GCController.getTheGCController().getWordNetDictionary();
		 
			for (int i=0;i<numberOfMeanings;i++) {
				IWord iword = dict.getWord( _wordnetIWordID[i] ); // look at one specific entry

				List<IWord> words =  iword.getSynset().getWords();
				for (int j=0;j<words.size();j++) {
					_synonyms.add(words.get(j).getLemma().replace('_', ' '));
				}		
			}
		}
		return _synonyms;
	}
	
	
	public void removeRow(int index) {
		IWordID[] destination = new IWordID[_wordnetIWordID.length-1];
		if (index>0) { // if we delete the first element, there are no items to copy before that
			System.arraycopy(_wordnetIWordID, 0, destination, 0, index);  // length is still the same 
		}
		if (index < (_wordnetIWordID.length-1)){ // copy from the index to the point afterwords
			System.arraycopy(_wordnetIWordID, index+1, destination, index, _wordnetIWordID.length - index -1 );
		}
		_wordnetIWordID = destination;
		this.setStringArrayFromIDs();
		
		_tableData = null;
	}
	
	//following methods are for  TableModel
	public void addTableModelListener(TableModelListener l) {
		return;
	}

	public Class<?> getColumnClass(int columnIndex) {
		return String.class; // all of our columns are strings
	}

	public int getColumnCount() {
		return 3;
	}

	public String getColumnName(int columnIndex) {
		if (columnIndex == 0) {
			return "ID";
		}
		else if (columnIndex == 1) {
			return "Definition";
		}
		else if (columnIndex == 2) {
			return "Synonyms";
		}
		else {
			return "COLUMN NAME NOT DEFINED";
		}
	}

	public int getRowCount() {
		return this.getNumberOfMeanings();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (_tableData == null) {
			this.setUpTableData();
		}
		return _tableData[rowIndex][columnIndex];
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		
		return true;
	}

	public void removeTableModelListener(TableModelListener l) {

		return;

	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		return; // data is not editable through this interface
	}


	
	private void setUpTableData() {
		int numberOfRows = this.getNumberOfMeanings();
		
		_tableData = new String[numberOfRows][3];
		
		Dictionary dict = GCController.getTheGCController().getWordNetDictionary();
		 
		for (int i=0;i<numberOfRows;i++) {
			IWord iword = dict.getWord( _wordnetIWordID[i] ); // look at one specific entry
			ISynset synset = iword.getSynset();
		
			StringBuffer temp = new StringBuffer();
			List<IWord> words =  synset.getWords();
			for (int j=0;j<words.size();j++) {
				if (j>0) { temp.append(", "); }
				temp.append(words.get(j).getLemma());
			}
			_tableData[i][0] = Integer.toString(i+1);
			_tableData[i][1] = synset.getGloss();
			_tableData[i][2] = temp.toString(); 
			
		}
	}
	
	
	private static class QueueEntry {
		ISynset synset;
		int depth;
		int number; 
		String ID;
		
		public QueueEntry(ISynset s, int d, int n, String id) {
			synset = s; depth =d; number = n; ID = id;
		}
	}
	
	public java.util.ArrayList<WordNetSynonymSet> getRelatedSets(int maxDepth, boolean limitToRelatedSets) {
		java.util.ArrayList<WordNetSynonymSet> result = new java.util.ArrayList<WordNetSynonymSet>();
		Dictionary dict = GCController.getTheGCController().getWordNetDictionary();
	
		
		java.util.Queue<QueueEntry> queue = new java.util.LinkedList<QueueEntry>();
		java.util.HashSet<ISynsetID> visited = new java.util.HashSet<ISynsetID>();
		
		int numberOfRows = this.getNumberOfMeanings();		 
		for (int i=0;i<numberOfRows;i++) {
			IWord iword = dict.getWord( _wordnetIWordID[i] ); 
			queue.add(new QueueEntry(iword.getSynset(),1,i+1,""));
		}
		
		while (queue.isEmpty() == false) {
			QueueEntry qe = queue.remove();
			if (qe.depth > maxDepth) { break; }  //stop processing, we'll have gone down too far
			if (visited.contains(qe.synset.getID())) { continue; } // we've already processed this node
			visited.add(qe.synset.getID());
			
			String relationshipOfParent ="";
			if (qe.ID.indexOf("(") >0) {
				relationshipOfParent = qe.ID.substring(qe.ID.indexOf("(")+1,qe.ID.indexOf(")"));
			}
			
			
			int i = 0;
			Map<IPointer, List<ISynsetID>> map = qe.synset.getRelatedMap();     //each semantic type can link to multiple semantic groups - in the list
			for (IPointer ip: map.keySet()){
				if (relationshipOfParent.equals("") && 
				    (ip.equals(Pointer.TOPIC) || ip.equals(Pointer.TOPIC_MEMBER) || 
				     ip.equals(Pointer.HYPERNYM_INSTANCE) || ip.equals(Pointer.HYPONYM_INSTANCE))) { continue; } // we don't follow these pointers
				
				String relationship = ip.getName();
				//System.out.println(relationship+":"+relationshipOfParent);
				if (limitToRelatedSets && relationshipOfParent.equals("") == false &&relationship.equals(relationshipOfParent) == false ) { continue;	} // we need to follow the same type of relationship
				
				List<ISynsetID> relatedIDs =  qe.synset.getRelatedSynsets(ip);
			
				for(ISynsetID isid: relatedIDs) {
					i++;
					ISynset is = dict.getSynset(isid);
					
					WordNetSynonymSet dders = WordNetSynonymSet.createFromSynSet(is, qe.depth, qe.ID, relationship, qe.number, i);
					if (result.contains(dders) == false ) { result.add(dders); 	}

										
					queue.add(new QueueEntry(is,qe.depth+1,i, dders.getID()));
				}
			}
		}
						
		return result;
	}

}

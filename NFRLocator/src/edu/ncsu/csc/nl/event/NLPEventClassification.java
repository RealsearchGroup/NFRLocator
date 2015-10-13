package edu.ncsu.csc.nl.event;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import edu.ncsu.csc.nl.GCController;
import edu.ncsu.csc.nl.model.Sentence;
import edu.ncsu.csc.nl.model.ml.ClassificationResult;

/**
 * Represents that a classification event just occurred for a particular sentence.
 * 
 * @author John Slankas
 */
public class NLPEventClassification extends NLPEvent implements TableModel {

	private Sentence _classifiedSentence;
	private ClassificationResult _result;
	private edu.ncsu.csc.nl.model.ml.InstanceLearner.IntermediateResults[] _orderedSentences;
	
	public NLPEventClassification(Sentence s,  ClassificationResult r, edu.ncsu.csc.nl.model.ml.InstanceLearner.IntermediateResults[] os) {
		_classifiedSentence = s;
		_result = r;
		_orderedSentences = os;
	}
	
	public Sentence getClassifiedSentence() {
		return _classifiedSentence;
	}
	
	public ClassificationResult getResult() {
		return _result;
	}

	@Override
	public int getRowCount() {
		return _orderedSentences.length;
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch(columnIndex) {
			case 0: return "Distance";
			case 1: return "Sentence #";
			case 2: return "Sentence";
			default: return "undefined";
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex) {
			case 0: return Double.class;
			case 1: return Integer.class;
			case 2: return String.class;
			default: return String.class;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(columnIndex) {
			case 0: return _orderedSentences[rowIndex].distance;
			case 1: return _orderedSentences[rowIndex].pos;
			case 2: return GCController.getTheGCController().getInstanceLearner().getTrainedSentenceAt(_orderedSentences[rowIndex].pos);
			default: return "Undefined";
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		return;
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {		
	}
	
	
}

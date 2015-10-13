package edu.ncsu.csc.nl.model;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;



public class EntryRelatedSynSetTableModel implements TableModel {

	private java.util.ArrayList<WordNetSynonymSet> _data;
	
	public EntryRelatedSynSetTableModel(java.util.ArrayList<WordNetSynonymSet> data) {
		_data = data;
	}
	
	public void clearAllData() {
		_data.clear();
	}
	
	@Override
	public void addTableModelListener(TableModelListener l) {
		return; // do nothing
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class; // all of our columns are strings
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
			case 0: return "#";
			case 1: return "k";
			case 2: return "ID";
			case 3: return "Definition";
			case 4: return "Synonym";
			default: return "NOT DEFINED";
		}
	}

	@Override
	public int getRowCount() {
		return _data.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		WordNetSynonymSet s = _data.get(rowIndex);
		
		switch (columnIndex) {
			case 0: return rowIndex +1;
			case 1: return s.getDistance();
			case 2: return s.getID();
			case 3: return s.getDefinition();
			case 4: return s.getWordList();
			default: return "NOT DEFINED";
		}
			
		
		
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true; // this allows the user to resize and do other things
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		return; // do nothing
		
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		return; // do nothing
	}
}

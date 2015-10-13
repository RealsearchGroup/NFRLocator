package edu.ncsu.csc.nl.model.classification;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;


import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ncsu.csc.nl.event.NLPEventManager;
import edu.ncsu.csc.nl.event.NLPEventSentenceDataEvent;
import edu.ncsu.csc.nl.event.NLPEventType;
import edu.ncsu.csc.nl.model.type.BooleanType;
import edu.ncsu.csc.nl.model.type.Source;


public class ClassificationAttributeTableModel implements TableModel, ClassifiableItem  {

	private java.util.ArrayList<ClassificationAttribute> _attributeList;
	
	//TODO..  add in the ability to get /set the attributes for a sentence here.. or the classification set we are looking at.
	private HashMap<String, ClassificationType> _classifications = new HashMap<String, ClassificationType>();
	
	public ClassificationAttributeTableModel() {
		_attributeList = new java.util.ArrayList<ClassificationAttribute>();
	}
	
	public ClassificationAttributeTableModel(java.util.ArrayList<ClassificationAttribute> attributeList) {
		_attributeList = attributeList;
	}

	
	public ClassificationAttribute getClassificationAttributeAt(int index) {
		return _attributeList.get(index);
	}
	
	public static ClassificationAttributeTableModel readFromClasspath(String resourceName) throws IOException, FileNotFoundException, ClassNotFoundException {
		//String fileName = file.getName();
	    try (ByteArrayOutputStream os = new ByteArrayOutputStream();)
	    {
	    	InputStream is = ClassificationAttribute.class.getResourceAsStream(resourceName);
	        byte[] buffer = new byte[0xFFFF];

	        for (int len; (len = is.read(buffer)) != -1;)
	            os.write(buffer, 0, len);

	        os.flush();

	        String input = new String(os.toByteArray(),Charset.forName("UTF-8"));
			ObjectMapper mapper = new ObjectMapper();
			
			ClassificationAttributeTableModel result = (ClassificationAttributeTableModel) mapper.readValue(input, ClassificationAttributeTableModel.class);
			return result;
	    }
	    // if exception is thrown, will be handled by caller.
    }
	
	public void setAttributeList(ClassificationAttribute[] attributes) {
		for (ClassificationAttribute ca: attributes) {
			_attributeList.add(ca);
		}
	}

	public java.util.List<ClassificationAttribute> getAttributeList() {
		return _attributeList;
	}	
	
	
	public void clearAllData() {
		_attributeList.clear();
	}
	
	public void dumpAttributeList(java.io.PrintStream ps) {
		for (ClassificationAttribute ca: _attributeList) {
			ps.print(ca.getName());
			ps.print(": ");
			ps.println(ca.getType());
		}
	}
	
	@Override
	public void addTableModelListener(TableModelListener l) {
		return; // do nothing
	}

	@Override
	@JsonIgnore
	public Class<?> getColumnClass(int columnIndex) {
		
		if (_attributeList.get(columnIndex).getType().equalsIgnoreCase("boolean")) {
			return Boolean.class;
		}
		else {
			return String.class; // otherwise, assume a string type.
		}
	}

	@Override
	@JsonIgnore
	public int getColumnCount() {
		return _attributeList.size();
	}

	@Override
	@JsonIgnore
	public String getColumnName(int columnIndex) {
		return _attributeList.get(columnIndex).getAbbreviation();
		/*
		switch (columnIndex) {
			case 0: return "Name";
			case 1: return "Value";
			default: return "NOT DEFINED";
		}
		*/
	}

	@Override
	@JsonIgnore
	public int getRowCount() {
		return 1;//_attributeList.size();
	}

	@Override
	@JsonIgnore
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex != 0) {
			return "";
		}
		String key= this.getKeyByColumnIndex(columnIndex);
		if (key != null) {
			ClassificationType c = 	_classifications.get(key);
			if (c == null) {
				return false;
			}
			else {
				if (c instanceof BooleanClassification) {
					return ((BooleanClassification) c).getValue().getBooleanValue();
				}
				else {
					return c.getValue();
				}
			}
		}
		else {
			return null;
		}
		/*
		if (columnIndex == 0) {
			return _attributeList.get(rowIndex).getName();
		}
		else {
			String key= this.getKeyByRowIndex(rowIndex);
			if (key != null) {
				BooleanClassification bc = 	_classifications.get(key);
				if (bc == null) {
					return false;
				}
				else {
					return bc.getValue().getBooleanValue();
				}
			}
			else {
				return null;
			}
		}
		*/
	}

	@Override
	@JsonIgnore
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (rowIndex == 0) {
			return true;
		}
		else {
			return false; 
		}
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		return; // do nothing
		
	}

	@Override
	@JsonIgnore
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (rowIndex != 0) {
			return;
		}
		
		ClassificationAttribute ca = _attributeList.get(columnIndex);
		ClassificationType ct;
		if (ca.getType().equalsIgnoreCase("boolean")) {
			Boolean bValue = (Boolean) aValue;
			ct = new BooleanClassification(BooleanType.getBooleanType(bValue), Source.USER);	
		}
		else {
			ct = new StringClassification(aValue.toString());
		}
		_classifications.put(this.getKeyByColumnIndex(columnIndex), ct);
			
		NLPEventManager.getTheEventManager().sendEvent(NLPEventType.SENTENCE_DATA_CHANGE, new NLPEventSentenceDataEvent(null, "classification"));
			
		return;
	}
	
	public HashMap<String, ClassificationType> getClassifications() {
		return _classifications;
	}
	
	public void setClassifications(HashMap<String, ClassificationType> classifications) {
		_classifications = classifications;
		this.setDefaults();
	}
	
	private String getKeyByColumnIndex(int column) {
		return _attributeList.get(column).getName();
	}
	
	private void setDefaults() {
		for (ClassificationAttribute ca: _attributeList) {
			if (!_classifications.containsKey(ca.getName())) {
				if (ca.getType().equalsIgnoreCase("boolean")) {
					_classifications.put(ca.getName(), new BooleanClassification(BooleanType.getBooleanType(false), Source.DEFAULT));
				}
				else {
					_classifications.put(ca.getName(), new StringClassification("", Source.DEFAULT));
				}
			}
		}
	}
	
}

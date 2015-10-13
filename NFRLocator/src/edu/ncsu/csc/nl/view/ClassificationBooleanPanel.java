package edu.ncsu.csc.nl.view;



import java.awt.event.MouseEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import edu.ncsu.csc.nl.model.classification.ClassificationAttribute;
import edu.ncsu.csc.nl.model.classification.ClassificationAttributeTableModel;
import edu.ncsu.csc.nl.model.classification.ClassifiableItem;

public class ClassificationBooleanPanel extends ClassificationPanel  {

	/**	 */
	private static final long serialVersionUID = 1L;
	
	private JTable _jtBoolean;
	
	private ClassificationAttributeTableModel _classificationAttributeTableModel;
	
	protected void actionMarkAll() { 
		TableModel tm = _jtBoolean.getModel();
		for (int i=0;i<tm.getRowCount();i++) {
			tm.setValueAt(true, i, 1);
		}
		_jtBoolean.tableChanged(null);
	}

	protected void actionClearAll() { 
		TableModel tm = _jtBoolean.getModel();
		for (int i=0;i<tm.getRowCount();i++) {
			tm.setValueAt(false, i, 1);
		}
		_jtBoolean.tableChanged(null);
	}
	
	protected  JComponent getClassifyAreaPanel() { 
		
		JScrollPane scrollPane = new JScrollPane(_jtBoolean);
		_jtBoolean.setFillsViewportHeight(true);	
		
		_jtBoolean.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		
		/* original values with vertical items
		_jtBoolean.getColumnModel().getColumn(0).setPreferredWidth(220);
		//_jtBoolean.getColumnModel().getColumn(0).setMinWidth(200);
		//_jtBoolean.getColumnModel().getColumn(0).setMaxWidth(200);
		
		_jtBoolean.getColumnModel().getColumn(1).setPreferredWidth(50);
		_jtBoolean.getColumnModel().getColumn(1).setMinWidth(50);
		_jtBoolean.getColumnModel().getColumn(1).setMaxWidth(50);
		*/
		for (int i=0;i < _classificationAttributeTableModel.getColumnCount(); i++) {
			_jtBoolean.getColumnModel().getColumn(i).setPreferredWidth(30);
			_jtBoolean.getColumnModel().getColumn(i).setMinWidth(20);
			//_jtBoolean.getColumnModel().getColumn(i).setMaxWidth(50);			
		}
		
		
		return scrollPane;
	}
		
	protected  void classificationItemChanged(ClassifiableItem itemToClassify) { 
		if (itemToClassify == null) {return;}
		_classificationAttributeTableModel.setClassifications(itemToClassify.getClassifications());
		_jtBoolean.tableChanged(null);
		this.setupComboBoxEditors();
		/*
		TableModel tm = _jtBoolean.getModel();
		for (int i=0;i<tm.getRowCount();i++) {
			System.out.println(tm.getValueAt(i,0)+": "+tm.getValueAt(i,1));
		}
		*/

	}
	
	public ClassificationBooleanPanel(ClassificationAttributeTableModel catm) {
		_classificationAttributeTableModel = catm;
		
		_jtBoolean = new JTable(catm) {
			public static final long serialVersionUID = 0L;
			
		    //Implement table header tool tips.
		    protected JTableHeader createDefaultTableHeader() {
		        return new JTableHeader(columnModel) {
		        	public static final long serialVersionUID = 0L;
		        	
		        	
		            public String getToolTipText(MouseEvent e) {
		                java.awt.Point p = e.getPoint();
		                int index = columnModel.getColumnIndexAtX(p.x);
		                int realIndex = columnModel.getColumn(index).getModelIndex();
		                
		                ClassificationAttribute ca = _classificationAttributeTableModel.getClassificationAttributeAt(realIndex);
		                return ca.getName()+": "+ca.getDescription();
		            }
		        };
		    }
		};

		new EditableCellFocusAction(_jtBoolean, KeyStroke.getKeyStroke("TAB"));
		new EditableCellFocusAction(_jtBoolean, KeyStroke.getKeyStroke("shift TAB"));
		new EditableCellFocusAction(_jtBoolean, KeyStroke.getKeyStroke("RIGHT"));
		new EditableCellFocusAction(_jtBoolean, KeyStroke.getKeyStroke("LEFT"));
		new EditableCellFocusAction(_jtBoolean, KeyStroke.getKeyStroke("DOWN"));		
		new EditableCellFocusAction(_jtBoolean, KeyStroke.getKeyStroke("UP"));	
		
		this.setupComboBoxEditors();
		
		this.createBasePanel();
		
	}
	
	private void setupComboBoxEditors() {
		for (int i=0;i < _classificationAttributeTableModel.getColumnCount(); i++) {
			ClassificationAttribute ca = _classificationAttributeTableModel.getClassificationAttributeAt(i);
			if (ca.getType().equalsIgnoreCase("list")) {
				String[] valueList = ca.getValues().split(",");
				
				TableColumn column = _jtBoolean.getColumnModel().getColumn(i);
				JComboBox<String> comboBox = new JComboBox<String>();
				for (String s: valueList) {comboBox.addItem(s.trim()); }
				column.setCellEditor(new DefaultCellEditor(comboBox));
				
			}
		}
		
		
	}
	
	
}

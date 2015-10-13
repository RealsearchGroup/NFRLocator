package edu.ncsu.csc.nl.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JList;

import edu.ncsu.csc.nl.model.CorrectPOSTags;

public class POSOverrideEditor extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTextField jtfWords  =  new JTextField(15);
	private JTextField jtfOldPOS =  new JTextField(15);
	private JTextField jtfNewPOS =  new JTextField(15);
	
	private DefaultListModel<String> listModel = new DefaultListModel<String>();;
	private JList<String> list = new JList<String>(listModel);
	
	/**
	 * Create the dialog.
	 */
	public POSOverrideEditor() {
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		
		setBounds(100, 100, 800, 300);
		JScrollPane listScrollPane = new JScrollPane(list);

		getContentPane().add(listScrollPane, BorderLayout.CENTER);

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			
			buttonPane.add(jtfWords);
			buttonPane.add(jtfOldPOS);
			buttonPane.add(jtfNewPOS);
			
			{
				JButton addButton = new JButton("Add");
				addButton.setActionCommand("Add");
				buttonPane.add(addButton);
				addButton.addActionListener(this);
			}
			{
				JButton removeButton = new JButton("Remove");
				removeButton.setActionCommand("Remove");
				buttonPane.add(removeButton);
				removeButton.addActionListener(this);
			}			
			{
				JButton cancelButton = new JButton("Close");
				cancelButton.setActionCommand("Close");
				buttonPane.add(cancelButton);
				getRootPane().setDefaultButton(cancelButton);
				cancelButton.addActionListener(this);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "Close": this.setVisible(false); return;
		case "Add": addItemAction(); return;
		case "Remove": removeItemAction(); return;
		}
		
	}

	private void addItemAction() {
		String line = jtfWords.getText() +"\t" + jtfOldPOS.getText() + "\t" + jtfNewPOS.getText();
		
		java.util.ArrayList<String> newList = new java.util.ArrayList<String>();
		int size = listModel.getSize();
		for (int i=0;i<size;i++) {
			newList.add(listModel.get(i));
		}
		newList.add(line);
		CorrectPOSTags._theCorrectPOSTagsAnnonotator.setOverrides(newList);
		
		listModel.addElement(line);
		
		jtfWords.setText("");
		jtfOldPOS.setText("");
		jtfNewPOS.setText("");
	}
	
	private void removeItemAction() {
		int selectedIndex = list.getSelectedIndex();
		if (selectedIndex != -1) {
			listModel.remove(selectedIndex);
			
			java.util.ArrayList<String> newList = new java.util.ArrayList<String>();
			int size = listModel.getSize();
			for (int i=0;i<size;i++) {
				newList.add(listModel.get(i));
			}
			CorrectPOSTags._theCorrectPOSTagsAnnonotator.setOverrides(newList);
		}
	}
	
	public void open() {
		java.util.List<String> l = CorrectPOSTags._theCorrectPOSTagsAnnonotator.getOverrides();
		listModel.removeAllElements();
		for (String s: l) {
			listModel.addElement(s);
		}
		this.setVisible(true);
	}
}

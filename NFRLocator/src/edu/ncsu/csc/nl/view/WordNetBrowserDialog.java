package edu.ncsu.csc.nl.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javax.swing.SpinnerListModel;
import javax.swing.border.EmptyBorder;

import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.JTable;
import edu.mit.jwi.item.POS;
import edu.ncsu.csc.nl.model.WordNetSynonymSet;
import edu.ncsu.csc.nl.model.EntryRelatedSynSetTableModel;
import edu.ncsu.csc.nl.model.WordNetEntry;
import edu.ncsu.csc.nl.model.type.PartOfSpeech;


import javax.swing.JScrollPane;
import javax.swing.JSpinner;


import javax.swing.JSplitPane;


public class WordNetBrowserDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String DICTIONARY_LOOKUP = "DictionaryLookup";
	private static final String VIEW_RELATED_SETS = "ViewRelatedSets";
	
	public static final String ACTION_DOMAIN_DICTIONARY_ADD    = "DDictionaryAdd";
	public static final String ACTION_CLOSE = "actClose";
	

	private static final Object[] ALLOWABLE_PARTS_OF_SPEECH =  {POS.ADJECTIVE, POS.ADVERB, POS.NOUN,POS.VERB };

	
	private final JPanel contentPanel = new JPanel();
	private final JPanel relatedPanel = new JPanel();
	private JTextField _jtfLemma;
	private JSpinner _jsPartOfSpeech;
	
	private JTextField _jtfDistance;
	private JCheckBox  _jcbLimitRelationship = new JCheckBox("");
	
		
	private JTable _jtSymanticSets;
	private JTable _jtRelated;
	
	private WordNetEntry _currentWord;

	//private JTextField textField;
	
	
	/**
	 * Create the dialog.
	 */
	public WordNetBrowserDialog() {
		setBounds(100, 100, 800, 600);
		getContentPane().setLayout(new BorderLayout());
		
		JSplitPane splitPanelTopBottom = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		getContentPane().add(splitPanelTopBottom, BorderLayout.CENTER);
		
		
		splitPanelTopBottom.setTopComponent(contentPanel);
		splitPanelTopBottom.setBottomComponent(relatedPanel);
		
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		relatedPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		{
			GridBagLayout gbl_contentPanel = new GridBagLayout();
			gbl_contentPanel.columnWidths = new int[]{0};
			gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
			gbl_contentPanel.columnWeights = new double[]{1.0};
			gbl_contentPanel.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
			contentPanel.setLayout(gbl_contentPanel);
		}
		{
			GridBagLayout gbl_relatedPanel = new GridBagLayout();
			gbl_relatedPanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
			gbl_relatedPanel.rowHeights = new int[]{0, 0, 0};
			gbl_relatedPanel.columnWeights = new double[]{1.0, 0.25, 1.0, 1.0, 0.25, Double.MIN_VALUE};
			gbl_relatedPanel.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
			relatedPanel.setLayout(gbl_relatedPanel);
		}
		
		
		
		{
			JPanel lookupBarPanel = new JPanel();
			GridBagConstraints gbc_lookupBarPanel = new GridBagConstraints();
			gbc_lookupBarPanel.gridwidth = 1;
			gbc_lookupBarPanel.insets = new Insets(0, 0, 5, 0);
			gbc_lookupBarPanel.fill = GridBagConstraints.HORIZONTAL;
			gbc_lookupBarPanel.gridx = 0;
			gbc_lookupBarPanel.gridy = 0;
			contentPanel.add(lookupBarPanel, gbc_lookupBarPanel);
			lookupBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
			{
				JLabel lblLemma = new JLabel("Lemma:");
				lookupBarPanel.add(lblLemma);
			}
			{
				_jtfLemma = new JTextField();
				lookupBarPanel.add(_jtfLemma);
				_jtfLemma.setColumns(15);
			}
			{
				JLabel lblPartOfSpeech = new JLabel("Part Of Speech:");
				lookupBarPanel.add(lblPartOfSpeech);
			}
			{
				_jsPartOfSpeech = new JSpinner(new SpinnerListModel(ALLOWABLE_PARTS_OF_SPEECH));
				lookupBarPanel.add(_jsPartOfSpeech);
			}
			{
				JButton jbtLookup = new JButton("Lookup");
				jbtLookup.setActionCommand(DICTIONARY_LOOKUP);
				jbtLookup.addActionListener(this);
				lookupBarPanel.add(jbtLookup);
			}
		}
		{
			_jtSymanticSets = new JTable();
			JScrollPane scrollPane = new JScrollPane(_jtSymanticSets);
			_jtSymanticSets.setFillsViewportHeight(true);			
			
			//_jtSymanticSets.
			GridBagConstraints gbc_scrollPane = new GridBagConstraints();
			gbc_scrollPane.gridwidth = 1;
			gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
			gbc_scrollPane.fill = GridBagConstraints.BOTH;
			gbc_scrollPane.gridx = 0;
			gbc_scrollPane.gridy = 1;
			contentPanel.add(scrollPane, gbc_scrollPane);
		}
		

		{
			JLabel lblViewRelatedSets = new JLabel("View Related Sets:");
			GridBagConstraints gbc_lblViewRelatedSets = new GridBagConstraints();
			gbc_lblViewRelatedSets.anchor = GridBagConstraints.WEST;
			gbc_lblViewRelatedSets.gridwidth = 2;
			gbc_lblViewRelatedSets.insets = new Insets(0, 0, 5, 5);
			gbc_lblViewRelatedSets.gridx = 0;
			gbc_lblViewRelatedSets.gridy = 0;
			relatedPanel.add(lblViewRelatedSets, gbc_lblViewRelatedSets);
		}
		{
			JPanel panel = new JPanel();
			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.gridwidth = 2;
			gbc_panel.insets = new Insets(0, 0, 5, 0);
			gbc_panel.fill = GridBagConstraints.BOTH;
			gbc_panel.anchor = GridBagConstraints.EAST;
			gbc_panel.gridx = 3;
			gbc_panel.gridy = 0;
			relatedPanel.add(panel, gbc_panel);
			panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
			
			JLabel lblLimitRelationship = new JLabel("Limit Relationship:");
			panel.add(lblLimitRelationship);
			
			
			panel.add(_jcbLimitRelationship);
			{
				JLabel lblDistance = new JLabel("Distance: ");
				panel.add(lblDistance);
			}
			{
				_jtfDistance = new JTextField();
				_jtfDistance.setText("1");
				panel.add(_jtfDistance);
				_jtfDistance.setColumns(3);
			}
			{
				JButton btnNewButton = new JButton("View");
				panel.add(btnNewButton);
				btnNewButton.setActionCommand(VIEW_RELATED_SETS);
				btnNewButton.addActionListener(this);
			}
		}
		{
			_jtRelated = new JTable();
			JScrollPane scrollPane = new JScrollPane(_jtRelated);
			_jtRelated.setFillsViewportHeight(true);		
			GridBagConstraints gbc__jtRelated = new GridBagConstraints();
			gbc__jtRelated.gridwidth = 5;
			gbc__jtRelated.fill = GridBagConstraints.BOTH;
			gbc__jtRelated.gridx = 0;
			gbc__jtRelated.gridy = 1;
			relatedPanel.add(scrollPane, gbc__jtRelated);
		}

		
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			/*
			{
				JButton addButton = new JButton("Add");
				addButton.setActionCommand("AddWordToDictionary");
				buttonPane.add(addButton);
				getRootPane().setDefaultButton(addButton);
			}
			*/

			JButton closeButton = new JButton("Close");
			closeButton.setActionCommand(ACTION_CLOSE);
			closeButton.addActionListener(this);
			
			GridBagConstraints gbc_closeButton = new GridBagConstraints();
			gbc_closeButton.anchor = GridBagConstraints.NORTHEAST;
			gbc_closeButton.gridx = 4;
			gbc_closeButton.gridy = 0;
			buttonPane.add(closeButton, gbc_closeButton);
		}
		
		
		splitPanelTopBottom.setDividerLocation(400);
	}
	
	public void setData(WordNetEntry  word) {
		_jtfLemma.setText(word.getLemma());
		
		_jsPartOfSpeech.setValue(word.getPartOfSpeech().getWordNetPOS());
		
		_jtSymanticSets.setModel(word);
		resizeSymanticSetColums();
		
		if ( !(_jtRelated.getModel() instanceof javax.swing.table.DefaultTableModel)) { 
			EntryRelatedSynSetTableModel e = (EntryRelatedSynSetTableModel) _jtRelated.getModel();
			e.clearAllData();
			_jtRelated.tableChanged(null);
			resizeRelatedColumns();
		}
		_currentWord = word;
	}
		
	public void actionPerformed(ActionEvent ae) {
		
		switch (ae.getActionCommand()) {
		  case DICTIONARY_LOOKUP:	    lookEntryInDictionary();       return;
		  case VIEW_RELATED_SETS:       retrieveRelatedSets(); 	       return;
		  case ACTION_CLOSE:            this.setVisible(false);	       return;
		}
	}
	
	/*
	public boolean setWordToLookup(String word, PartOfSpeech pos) {
		_jtfLemma.setText(word);
		_jsPartOfSpeech.setValue(pos.getWordNetPOS());
		
		this.setData(word, pos);
			
		return true;
	}
	*/	
	
	public void lookEntryInDictionary() {
		String word = _jtfLemma.getText();
		POS pos = (POS) _jsPartOfSpeech.getValue();
		
		WordNetEntry wne = WordNetEntry.createFromWordNet(word, pos);
		if (wne != null) {
			this.setData(wne);
			
		}
		else {
			JOptionPane.showMessageDialog(this, "Unable to locate \""+word+"\"in the dictionary.", "Word Not Found", JOptionPane.ERROR_MESSAGE);
			
			if (pos.equals(POS.NOUN)) {
				wne = new WordNetEntry(word,PartOfSpeech.NN);
			}
			else if (pos.equals(POS.VERB)){
				wne = new WordNetEntry(word,PartOfSpeech.VB);
			}
			else {
				return; // take no action on adjectives or adverbs.
			}
			
			this.setData(wne);
		}
	}
	
	private void retrieveRelatedSets() {
		java.util.ArrayList<WordNetSynonymSet> words = _currentWord.getRelatedSets(Integer.parseInt(_jtfDistance.getText()), _jcbLimitRelationship.isSelected()); 
		EntryRelatedSynSetTableModel model = new EntryRelatedSynSetTableModel(words);
		
		_jtRelated.setModel(model);
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(_jtRelated.getModel());
		_jtRelated.setRowSorter(sorter);
		
		resizeRelatedColumns();
	}
	
	private void resizeSymanticSetColums() {
		_jtSymanticSets.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		_jtSymanticSets.getColumnModel().getColumn(0).setPreferredWidth(20);
		_jtSymanticSets.getColumnModel().getColumn(0).setMinWidth(20);
		_jtSymanticSets.getColumnModel().getColumn(0).setMaxWidth(20);
	}
	private void resizeRelatedColumns() {
		_jtRelated.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		_jtRelated.getColumnModel().getColumn(0).setPreferredWidth(20);
		_jtRelated.getColumnModel().getColumn(0).setMinWidth(20);
		_jtRelated.getColumnModel().getColumn(0).setMaxWidth(20);		
		_jtRelated.getColumnModel().getColumn(0).setWidth(20);
		_jtRelated.getColumnModel().getColumn(1).setPreferredWidth(20);
		_jtRelated.getColumnModel().getColumn(1).setMinWidth(20);
		_jtRelated.getColumnModel().getColumn(1).setMaxWidth(20);	
		_jtRelated.getColumnModel().getColumn(1).setWidth(20);
		_jtRelated.getColumnModel().getColumn(2).setPreferredWidth(100);
		_jtRelated.getColumnModel().getColumn(2).setMinWidth(100);
		_jtRelated.getColumnModel().getColumn(2).setWidth(100);
		_jtRelated.getColumnModel().getColumn(2).setMaxWidth(100);	
		_jtRelated.getColumnModel().getColumn(0).setMaxWidth(1000);	
		_jtRelated.getColumnModel().getColumn(1).setMaxWidth(1000);
		_jtRelated.getColumnModel().getColumn(2).setMaxWidth(1000);	
	}

}

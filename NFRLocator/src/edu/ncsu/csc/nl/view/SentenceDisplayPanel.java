package edu.ncsu.csc.nl.view;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextField;
import javax.swing.JTextArea;

import edu.ncsu.csc.nl.GCConstants;
import edu.ncsu.csc.nl.GCController;
import edu.ncsu.csc.nl.event.NLPEvent;
import edu.ncsu.csc.nl.event.NLPEventListener;
import edu.ncsu.csc.nl.event.NLPEventManager;
import edu.ncsu.csc.nl.event.NLPEventType;
import edu.ncsu.csc.nl.model.NLDocument;
import edu.ncsu.csc.nl.model.Sentence;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;

import javax.swing.JPopupMenu;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.event.ListSelectionListener;

import java.awt.event.FocusAdapter;

public class SentenceDisplayPanel extends JPanel implements ActionListener, NLPEventListener {

	public static final long serialVersionUID = 1;
	
	private JTextArea  _jtaStanford;
	private JTextArea  _jtaCollapsed;
	
	
	JTable _jtSentence = new JTable();
	JScrollPane _jspSentence = new JScrollPane(_jtSentence);
	

	private final JPanel addSentencePanel = new JPanel();
	private final JLabel lblNewLabel_3 = new JLabel("Add:");
	private final JTextField _jtfSentence = new JTextField();
	private final JButton _jbtParse = new JButton("Parse");
	
	private Sentence _currentSentence;

	private final JPopupMenu popupMenu = new JPopupMenu();
	private final JMenuItem _jmiRemove = new JMenuItem("Remove");
	private final JMenuItem _jmiReparse = new JMenuItem("Reparse");
	private final JMenuItem _jmiReplace = new JMenuItem("Replace");
	
	private final JTextArea _jtaComments = new JTextArea();
	private final JPanel panelComments = new JPanel();	

	private final JPanel sourceSentencePanel = new JPanel();
	
	private final JLabel _jlbParsedCorrect = new JLabel("Parsed Correctly:");
	private final JCheckBox _jcbParsedCorrectly = new JCheckBox();	
	
	private final JLabel _jlbConditions = new JLabel("Has AC Conditions:");
	private final JCheckBox _jcbConditions = new JCheckBox();
	
	private final JLabel _jlbLegal = new JLabel("Legal Source:");
	private final JCheckBox _jcbLegal = new JCheckBox();
	
	private final JLabel _jlbRefersTo = new JLabel("Refers To #:");
	private final JTextField _jtfRefersTO = new JTextField(10);
	
	private final JLabel     _jlbSource = new JLabel("Source: ");
	private final JTextField _jtfSource = new JTextField(20);
	
	private final JLabel _jlbAssignedRole = new JLabel("Assigned Role: ");
	private final JTextField _jtfAssignedRole= new JTextField(20);
	
	/**
	 * Create the panel.
	 */
	public SentenceDisplayPanel()  {
		GCController gcc = GCController.getTheGCController();
		
		
		JSplitPane overallPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		this.setLayout(new BorderLayout());
		this.add(overallPanel,BorderLayout.CENTER);

		JPanel topPanel = new JPanel();
		JPanel bottomPanel = new JPanel();
		overallPanel.setTopComponent(topPanel);
		overallPanel.setBottomComponent(bottomPanel);
		overallPanel.setDividerLocation(65);

		{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{1.0};
		topPanel.setLayout(gridBagLayout);
		}
		
		_jtSentence.setAutoCreateRowSorter(true);
		_jtSentence.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
		_jtSentence.setCellSelectionEnabled(false);
		_jtSentence.setRowSelectionAllowed(true);
		_jtSentence.setColumnSelectionAllowed(false);
		//_jtSentence.setVisibleRowCount(5);
		_jtSentence.getSelectionModel().addListSelectionListener(gcc);
		/*
		_jtSentence.setCellRenderer( new javax.swing.DefaultListCellRenderer() {
			public static final long serialVersionUID = 1;
			
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index,  isSelected, cellHasFocus);
				
				Sentence s = (Sentence) value;
				
				Font f = this.getFont();
				if (s.isTrained()) { 
					f = f.deriveFont(Font.ITALIC); 
					
					/*
					if (s.isAccessControl() && s.getAccessControlDecision().requiresResolution()) {
						this.setForeground(Color.RED);
						if (isSelected) {
							this.setBackground(Color.BLACK);
						}
					}
					else if (s.isAccessControl() == false) {
						this.setForeground(Color.GRAY);
						if (isSelected) {
							this.setBackground(Color.BLACK);
						}						
					}
					*/
					/*
					
					this.setFont(f);
				}
				
				return this;
			}
			
		});
		*/
		_jtSentence.setFillsViewportHeight(true);
		
		addPopup(_jtSentence, popupMenu);
		
		popupMenu.add(_jmiRemove);
		popupMenu.addSeparator();
		popupMenu.add(_jmiReparse);
		popupMenu.addSeparator();
		popupMenu.add(_jmiReplace);
		_jspSentence.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.anchor = GridBagConstraints.NORTH;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		topPanel.add(_jspSentence, gbc_scrollPane);
		
		
		{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0};
		gridBagLayout.rowWeights = new double[]{0.0,1.0, 0.0,0.0, Double.MIN_VALUE};
		bottomPanel.setLayout(gridBagLayout);
		}
			
		
		_jtfSentence.setText("");
		_jtfSentence.setColumns(10);
		_jtfSentence.setActionCommand(GCConstants.ACTION_PARSE_NEW_SENTENCE);
		_jtfSentence.addActionListener(this);
		
		GridBagConstraints gbc_addSentencePanel = new GridBagConstraints();
		gbc_addSentencePanel.insets = new Insets(0, 0, 5, 0);
		gbc_addSentencePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_addSentencePanel.gridx = 0;
		gbc_addSentencePanel.gridy = 0;
		gbc_addSentencePanel.gridwidth = 2;
		bottomPanel.add(addSentencePanel, gbc_addSentencePanel);
		
		GridBagLayout gbl_addSentencePanel = new GridBagLayout();
		gbl_addSentencePanel.columnWeights = new double[]{0.0,1.0, 0.0};
		gbl_addSentencePanel.rowWeights = new double[]{ 0.0};
		addSentencePanel.setLayout(gbl_addSentencePanel);
		
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_3.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 0;
		addSentencePanel.add(lblNewLabel_3, gbc_lblNewLabel_3);
		
		GridBagConstraints gbc__jtfSentence = new GridBagConstraints();
		gbc__jtfSentence.insets = new Insets(0, 0, 5, 0);
		gbc__jtfSentence.fill = GridBagConstraints.HORIZONTAL;
		gbc__jtfSentence.gridx = 1;
		gbc__jtfSentence.gridy = 0;
		addSentencePanel.add(_jtfSentence, gbc__jtfSentence);
		
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.fill = GridBagConstraints.NONE;
		gbc_btnNewButton.gridx = 2;
		gbc_btnNewButton.gridy = 0;
		addSentencePanel.add(_jbtParse, gbc_btnNewButton);
		

		_jtaStanford = new JTextArea();
		_jtaStanford.setFont(new Font("Courier New", Font.PLAIN, 12));
		_jtaStanford.setRows(15);
		_jtaStanford.setColumns(40);
		_jtaStanford.setEditable(true);
		_jtaStanford.setBorder(BorderFactory.createTitledBorder("Stanford Parse Tree"));   

		_jtaCollapsed = new JTextArea();
		_jtaCollapsed.setFont(new Font("Courier New", Font.PLAIN, 12));
		_jtaCollapsed.setColumns(40);
		_jtaCollapsed.setRows(15);
		_jtaCollapsed.setBorder(BorderFactory.createTitledBorder("My Parse Tree"));		
		JScrollPane scrollPaneStanford = new JScrollPane(_jtaCollapsed);   
		JScrollPane scrollPaneCollapsed = new JScrollPane(_jtaStanford); 
		{
			GridBagConstraints gbc_panelStanford = new GridBagConstraints();
			gbc_panelStanford.fill = GridBagConstraints.BOTH;
			gbc_panelStanford.insets = new Insets(0, 0, 5, 5);
			gbc_panelStanford.gridx = 1;
			gbc_panelStanford.gridy = 1;
			gbc_panelStanford.weightx = 1.0;
			gbc_panelStanford.weighty = 1.0;
			bottomPanel.add(scrollPaneStanford,gbc_panelStanford);
		}
		_jtaCollapsed.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent fe) {			
				if (_currentSentence != null) {
					//String text=  ((JTextArea)fe.getSource()).getText().trim();
					//_currentSentence.setRequirements(text);
				}
			}
		});		

		
		{
			GridBagConstraints gbc_panelCollapsed = new GridBagConstraints();
			gbc_panelCollapsed.fill = GridBagConstraints.BOTH;
			gbc_panelCollapsed.insets = new Insets(0, 0, 5, 0);
			gbc_panelCollapsed.gridx = 0;
			gbc_panelCollapsed.gridy = 1;
			gbc_panelCollapsed.weightx = 1.0;
			gbc_panelCollapsed.weighty = 1.0;
			bottomPanel.add(scrollPaneCollapsed,gbc_panelCollapsed);
		}		
		
		GridBagConstraints gbc_panelComments = new GridBagConstraints();
		gbc_panelComments.fill = GridBagConstraints.BOTH;
		gbc_panelComments.gridwidth = 2;
		gbc_panelComments.insets = new Insets(0, 0, 5, 5);
		gbc_panelComments.gridx = 0;
		gbc_panelComments.gridy = 2;
		panelComments.setBorder(null);
		bottomPanel.add(panelComments, gbc_panelComments);
		GridBagLayout gbl_panelComments = new GridBagLayout();
		gbl_panelComments.columnWidths = new int[]{0, 0, 0};
		gbl_panelComments.rowHeights = new int[]{0, 0};
		gbl_panelComments.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_panelComments.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panelComments.setLayout(gbl_panelComments);
		GridBagConstraints gbc__jtaComments = new GridBagConstraints();
		gbc__jtaComments.fill = GridBagConstraints.HORIZONTAL;
		gbc__jtaComments.gridwidth = 2;
		gbc__jtaComments.insets = new Insets(0, 0, 0, 5);
		gbc__jtaComments.gridx = 0;
		gbc__jtaComments.gridy = 0;
		_jtaComments.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent fe) {			
				if (_currentSentence != null) {
					String text=  ((JTextArea)fe.getSource()).getText().trim();
					_currentSentence.setComments(text);
				}
			}
		});
		_jtaComments.setBorder(BorderFactory.createTitledBorder("Comments"));
		panelComments.add(_jtaComments, gbc__jtaComments);
		_jtaComments.setToolTipText("Enter parsing comments or notes to keep about this sentence.");
		_jtaComments.setRows(2);
				
		
		/*
		GridBagLayout gbl_sourceSentencePanel = new GridBagLayout();
		gbl_sourceSentencePanel.columnWeights = new double[]{0.0,1.0, 0.0};
		gbl_sourceSentencePanel.rowWeights = new double[]{ 0.0};
		sourceSentencePanel.setLayout(gbl_addSentencePanel);
		
		GridBagConstraints gbc_labelSource = new GridBagConstraints();
		gbc_labelSource.insets = new Insets(0, 0, 5, 0);
		gbc_labelSource.anchor = GridBagConstraints.WEST;
		gbc_labelSource.gridx = 0;
		gbc_labelSource.gridy = 0;
		sourceSentencePanel.add(_jlbSource, gbc_labelSource);
		
		GridBagConstraints gbc_jtfSource = new GridBagConstraints();
		gbc_jtfSource.insets = new Insets(0, 0, 5, 0);
		gbc_jtfSource.fill = GridBagConstraints.HORIZONTAL;
		gbc_jtfSource.gridx = 1;
		gbc_jtfSource.gridy = 0;
		sourceSentencePanel.add(_jtfSource, gbc_jtfSource);	
		*/
		sourceSentencePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		sourceSentencePanel.add(_jlbParsedCorrect);
		sourceSentencePanel.add(_jcbParsedCorrectly);
		sourceSentencePanel.add(_jlbConditions);
		sourceSentencePanel.add(_jcbConditions);
		
		sourceSentencePanel.add(_jlbLegal);
		sourceSentencePanel.add(_jcbLegal);
		sourceSentencePanel.add(_jlbRefersTo);
		sourceSentencePanel.add(_jtfRefersTO);
		sourceSentencePanel.add(_jlbSource);
		sourceSentencePanel.add(_jtfSource);
		sourceSentencePanel.add(_jlbAssignedRole);
		sourceSentencePanel.add(_jtfAssignedRole);
		
		GridBagConstraints gbc_sourcePanel = new GridBagConstraints();
		gbc_sourcePanel.anchor = GridBagConstraints.WEST;
		gbc_sourcePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_sourcePanel.gridx = 0;
		gbc_sourcePanel.gridy = 4;
		gbc_sourcePanel.gridwidth = 2;
		bottomPanel.add(sourceSentencePanel, gbc_sourcePanel);
		
		
		_jtfRefersTO.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent fe) {			
				if (_currentSentence != null) {
					String text=  ((JTextField)fe.getSource()).getText().trim();
					
					try {
						if (text.equals("") == false) {
							double newReferredToValue = Double.parseDouble(text);
							_currentSentence.setReferredToSentence(newReferredToValue);
						}
					}
					catch (NumberFormatException nfe) {
						GCController.getTheGCController().setStatusMessage("\"Referred to\" must be a number: "+text);
					}
				}
			}
		});		
		
		_jtfSource.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent fe) {			
				if (_currentSentence != null) {
					String text=  ((JTextField)fe.getSource()).getText().trim();
					_currentSentence.setSource(text);
				}
			}
		});
		
		
		_jtfAssignedRole.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent fe) {			
				if (_currentSentence != null) {
					String text=  ((JTextField)fe.getSource()).getText().trim();
					_currentSentence.setUserAssignedRole(text);
				}
			}
		});
		
		_jcbLegal.addActionListener(this);
		_jcbParsedCorrectly.addActionListener(this);
		_jcbConditions.addActionListener(this);

		
		
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 4, 0, 0));
		JButton btnFirst = new JButton("First");		
		JButton btnPrevious = new JButton("Previous");
		JButton btnNext = new JButton("Next");
		JButton btnLast = new JButton("Last");

		buttonPanel.add(btnFirst);
		buttonPanel.add(btnPrevious);
		buttonPanel.add(btnNext);		
		buttonPanel.add(btnLast);
		
	
		GridBagConstraints gbc_tempPanel = new GridBagConstraints();
		gbc_tempPanel.anchor = GridBagConstraints.CENTER;
		gbc_tempPanel.fill = GridBagConstraints.NONE;
		gbc_tempPanel.gridx = 0;
		gbc_tempPanel.gridy = 5;
		gbc_tempPanel.gridwidth = 2;
		bottomPanel.add(buttonPanel, gbc_tempPanel);
		
		btnFirst.addActionListener(gcc);
		btnPrevious.addActionListener(gcc);
		btnNext.addActionListener(gcc);
		btnLast.addActionListener(gcc);
		
		_jbtParse.setActionCommand(GCConstants.ACTION_PARSE_NEW_SENTENCE);
		_jbtParse.addActionListener(this);
		
		
		_jmiRemove.addActionListener(this);
		_jmiRemove.setActionCommand(GCConstants.ACTION_POPUP_REMOVE_SENTENCE);
		
		_jmiReplace.addActionListener(this);
		_jmiReplace.setActionCommand(GCConstants.ACTION_POPUP_REPLACE_SENTENCE);
		
		_jmiReparse.addActionListener(this);
		_jmiReparse.setActionCommand(GCConstants.ACTION_POPUP_REPARSE_SENTENCE);
		
		NLPEventManager.getTheEventManager().registerForEvent(NLPEventType.SENTENCE_DATA_CHANGE, this);
		NLPEventManager.getTheEventManager().registerForEvent(NLPEventType.VIEW_CHANGED, this);
	}
	
	public void setCurrentDocument(edu.ncsu.csc.nl.model.NLDocument document) {
		_jtSentence.setModel(document);
		
		_jtSentence.getColumnModel().getColumn(0).setPreferredWidth(30);
		_jtSentence.getColumnModel().getColumn(0).setMinWidth(30);
		_jtSentence.getColumnModel().getColumn(0).setMaxWidth(200);
		
		_jtSentence.getColumnModel().getColumn(1).setPreferredWidth(20);
		_jtSentence.getColumnModel().getColumn(1).setMinWidth(20);
		_jtSentence.getColumnModel().getColumn(1).setMaxWidth(100);		

		_jtSentence.getColumnModel().getColumn(2).setPreferredWidth(80);
		_jtSentence.getColumnModel().getColumn(2).setMinWidth(40);
		_jtSentence.getColumnModel().getColumn(2).setMaxWidth(200);	
		
		_jtSentence.getColumnModel().getColumn(3).setPreferredWidth(30);
		_jtSentence.getColumnModel().getColumn(3).setMinWidth(30);
		_jtSentence.getColumnModel().getColumn(3).setMaxWidth(100);	
		
		//_jtSentence.getColumnModel().getColumn(2).setPreferredWidth(50);
		//_jtSentence.getColumnModel().getColumn(2).setMinWidth(50);
		//_jtSentence.getColumnModel().getColumn(2).setMaxWidth(50);		
	}
	

	public void clearAllFeilds() {
		_currentSentence = null;
		_jtaStanford.setText("");
		_jtaCollapsed.setText("");

	}
	
	/**
	 * a convenience method that calls setCurrentState(index,true);
	 * @param index
	 */
	public void setCurrentSentence(int modelIndex) {
		setCurrentSentence(modelIndex,true);
	}
	
	/**
	 * 
	 * 
	 * @param index
	 * @param setObject  if this is setTo true, then the relevant/appropriate sentence is selected in the 
	 *                  jList. if not, we assume that the user had selected it.
	 */
	public void setCurrentSentence(int modelIndex, boolean setObject) {
		if (modelIndex == -1) {
			this.clearAllFeilds();
			return;
		}
		int viewIndex = _jtSentence.convertRowIndexToView(modelIndex);
		
		NLDocument document = (NLDocument) _jtSentence.getModel();
		
		Sentence s = (Sentence) document.getElementAt(modelIndex);
		
		
		if (setObject) {
			_jtSentence.getSelectionModel().clearSelection();

			ListSelectionListener[] listeners = ((DefaultListSelectionModel) _jtSentence.getSelectionModel()).getListSelectionListeners();
			for (int i=0;i<listeners.length;i++) {
				_jtSentence.getSelectionModel().removeListSelectionListener(listeners[i]);
			}
			_jtSentence.setRowSelectionInterval(viewIndex, viewIndex);
			
			for (int i=0;i<listeners.length;i++) {
				_jtSentence.getSelectionModel().addListSelectionListener(listeners[i]);
			}			
		}
		if ((_jtSentence.getParent() instanceof JViewport)) {
			JViewport viewport = (JViewport) _jtSentence.getParent();
			Rectangle rect = _jtSentence.getCellRect(viewIndex, 0, true);
			Point pt = viewport.getViewPosition();
			rect.setLocation(rect.x - pt.x, rect.y - pt.y);
			if (!(new Rectangle(viewport.getExtentSize()).contains(rect))) {
				_jtSentence.scrollRectToVisible(new Rectangle(_jtSentence.getCellRect(viewIndex, 0, true)));
			}
		}
		
		
		_currentSentence = s;
		//_jtfOrginalSentence.setText(s.getSentence());
		_jtaStanford.setText(s.getSemanticGraph().toString());
		_jtaCollapsed.setText(s.getRoot().getDisplayRepresentation());
		
		//_jtaCollapsed.setText(s.getComments());
		
		_jtaComments.setText(s.getComments());	
		_jtfSource.setText(s.getSource());
		_jcbLegal.setSelected(s.isLegal());
		_jtfAssignedRole.setText(s.getUserAssignedRole());
		
		_jcbParsedCorrectly.setSelected(s.isParsedCorrectly());
		
		
		if (s.getReferredToSentence() == Sentence.UNASSIGNED_SENTENCE_POSITION) {
			_jtfRefersTO.setText("");
		}
		else {
			_jtfRefersTO.setText(Double.toString(s.getReferredToSentence()));
		}

		_jtSentence.repaint();
	}
	
	
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == _jbtParse || ae.getSource() == _jtfSentence) {
			GCController.getTheGCController().parseLine(_jtfSentence.getText());
			return;
		}
		
		if (_currentSentence == null) { 
			return;
		}
		
		if (ae.getSource() == _jmiRemove) {
			int viewIndex         = _jtSentence.getSelectedRow();
			int modelIndex = _jtSentence.convertRowIndexToModel(viewIndex);
			if (modelIndex > -1) {
				NLDocument document = (NLDocument) _jtSentence.getModel();
				document.removeElementAt(modelIndex);
				if (document.getNumberOfSentences() <= viewIndex) {
					viewIndex--;
				}
				int newModelIndex = _jtSentence.convertRowIndexToModel(viewIndex);
				this.setCurrentSentence(newModelIndex);
				GCController.getTheGCController().setCurrentSentenceNoAction(newModelIndex,true);
			}
		}
		else if (ae.getSource() == _jmiReplace) {
			int viewIndex         = _jtSentence.getSelectedRow();
			int modelIndex = _jtSentence.convertRowIndexToModel(viewIndex);
					
			if (viewIndex <0 || _jtfSentence.getText().equals("")) {
				return;
			}
			NLDocument document = (NLDocument) _jtSentence.getModel();

			
			GCController.getTheGCController().parseLine(_jtfSentence.getText());
			
			int lastModelIndex = document.getSentences().size() -1;
			
			//TODO: fix this so the logic is in the right place.  THIS REALLY BELONGS elsewhere
			Sentence newSentence = document.getSentences().get(lastModelIndex);
			Sentence currentSentence = document.getSentences().get(modelIndex);
			newSentence.setOriginalSentencePosition(currentSentence.getOriginalSentencePosition());
			newSentence.setSentenceType(currentSentence.getSentenceType());
			newSentence.setRelatedToPrevious(currentSentence.isRelatedToPrevious());
	
			document.swapSentences(modelIndex,lastModelIndex);
			document.removeElementAt(lastModelIndex);
			this.setCurrentSentence(modelIndex);
			GCController.getTheGCController().setCurrentSentenceNoAction(modelIndex,true);  // this is really bad design!
		}	
		else if (ae.getSource() == _jmiReparse) {
			int viewIndex         = _jtSentence.getSelectedRow();
			int modelIndex = _jtSentence.convertRowIndexToModel(viewIndex);
			
			String text = _currentSentence.getSentence();
			
			if (viewIndex <0 || text.equals("")) {
				return;
			}
			NLDocument document = (NLDocument) _jtSentence.getModel();
			
			GCController.getTheGCController().parseLine(text);
			
			int lastModelIndex = document.getSentences().size() -1;
			
			//TODO: fix this so the logic is in the right place.  THIS REALLY BELONGS elsewhere
			Sentence newSentence = document.getSentences().get(lastModelIndex);
			Sentence currentSentence = document.getSentences().get(modelIndex);
			newSentence.setOriginalSentencePosition(currentSentence.getOriginalSentencePosition());
			newSentence.setSentenceType(currentSentence.getSentenceType());
			newSentence.setRelatedToPrevious(currentSentence.isRelatedToPrevious());
	
			document.swapSentences(modelIndex,lastModelIndex);
			document.removeElementAt(lastModelIndex);
			this.setCurrentSentence(modelIndex);
			GCController.getTheGCController().setCurrentSentenceNoAction(modelIndex,true);  // this is really bad design!			
		}
		else if (ae.getSource() == _jcbLegal) {
			_currentSentence.setLegal(_jcbLegal.isSelected());
		}
		else if (ae.getSource() == _jcbParsedCorrectly) {
			_currentSentence.setParsedCorrectly(_jcbParsedCorrectly.isSelected());
		}
		
	}
	
	/** This only exists so that the controller can verify the list event change. */
	public JTable getSentenceTable() {
		return _jtSentence;
	}
	
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	@Override
	public void eventOccured(NLPEventType eventType, NLPEvent event) {   
		if (eventType == NLPEventType.SENTENCE_DATA_CHANGE){
			//called when a classification changes so we can update the table.  This should have been 
			//event driven from the model
			_jtSentence.repaint();
		}
	}
	
	/*
	private void setStatusMessage(String message) {
		MainFrame mf = (MainFrame) javax.swing.SwingUtilities.getAncestorOfClass(MainFrame.class, this);
		mf.setStatusMessage(message);
	}	
	*/
}

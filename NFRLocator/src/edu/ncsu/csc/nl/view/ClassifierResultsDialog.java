package edu.ncsu.csc.nl.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import edu.ncsu.csc.nl.GCConstants;
import edu.ncsu.csc.nl.event.NLPEvent;
import edu.ncsu.csc.nl.event.NLPEventClassification;
import edu.ncsu.csc.nl.event.NLPEventListener;
import edu.ncsu.csc.nl.event.NLPEventManager;
import edu.ncsu.csc.nl.event.NLPEventType;
import edu.ncsu.csc.nl.model.Sentence;
import edu.ncsu.csc.nl.model.classification.ClassificationType;

public class ClassifierResultsDialog extends JDialog implements NLPEventListener, ActionListener, ListSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();
	private JTextField jtfSentence;
	private JTextField jtfResults;
	private JTable jtNeighbors;

	/**
	 * Create the dialog.
	 */
	public ClassifierResultsDialog(MainFrame mf) {
		super(mf,false);
		
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

		setBounds(100, 100, 300, 360);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane);
			{
				jtNeighbors = new JTable();
				jtNeighbors.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
				jtNeighbors.getSelectionModel().addListSelectionListener(this);
				jtNeighbors.setAutoCreateRowSorter(true);
				scrollPane.setViewportView(jtNeighbors);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton closeButton = new JButton("Close");
				closeButton.setActionCommand(GCConstants.ACTION_OTHER_VIEW_CLASS_RESULTS);
				closeButton.addActionListener(this);
				buttonPane.add(closeButton);
			}
		}
		{
			JPanel headerPanel = new JPanel();
			getContentPane().add(headerPanel, BorderLayout.NORTH);
			headerPanel.setLayout(new GridLayout(3, 1, 0, 0));
			{
				JPanel jpTitle = new JPanel();
				FlowLayout fl_jpTitle = (FlowLayout) jpTitle.getLayout();
				fl_jpTitle.setVgap(2);
				fl_jpTitle.setHgap(2);
				fl_jpTitle.setAlignment(FlowLayout.LEFT);
				headerPanel.add(jpTitle);
				{
					JLabel lblClassifierResults = new JLabel("Classifier Results");
					lblClassifierResults.setFont(new Font("Tahoma", Font.BOLD, 11));
					jpTitle.add(lblClassifierResults);
				}
			}
			{
				JPanel jpSentence = new JPanel();
				headerPanel.add(jpSentence);
				jpSentence.setLayout(new BorderLayout(0, 0));
				{
					jtfSentence = new JTextField();
					jtfSentence.setEditable(false);
					jpSentence.add(jtfSentence, BorderLayout.NORTH);
					jtfSentence.setColumns(10);
				}
			}
			{
				JPanel jpResults = new JPanel();
				headerPanel.add(jpResults);
				jpResults.setLayout(new BorderLayout(0, 0));
				{
					JLabel jlbResults = new JLabel("Results:");
					jpResults.add(jlbResults, BorderLayout.WEST);
				}
				{
					jtfResults = new JTextField();
					jtfResults.setEditable(false);
					jpResults.add(jtfResults, BorderLayout.CENTER);
					jtfResults.setColumns(10);
				}
			}
		}
	}

	public void register() {
		NLPEventManager.getTheEventManager().registerForEvent(NLPEventType.CLASSIFIED_SENTENCE, this);
	}
	
	@Override
	public void eventOccured(NLPEventType eventType, NLPEvent event) {
		if (eventType ==NLPEventType.CLASSIFIED_SENTENCE) {
			NLPEventClassification nec = (NLPEventClassification) event;
			
			jtfSentence.setText(nec.getClassifiedSentence().getSentence());
			// TODO: Fix this issue - doesn't work with security annotations
			//jtfResults.setText(nec.getResult().averageDistance+": "+nec.getResult().toString());
			jtfResults.setCaretPosition(0);
			jtNeighbors.setModel(nec);
			jtNeighbors.tableChanged(null);
			
			jtNeighbors.getColumnModel().getColumn(0).setPreferredWidth(50);
			jtNeighbors.getColumnModel().getColumn(0).setMinWidth(50);
			jtNeighbors.getColumnModel().getColumn(0).setMaxWidth(50);
			
			jtNeighbors.getColumnModel().getColumn(1).setPreferredWidth(50);
			jtNeighbors.getColumnModel().getColumn(1).setMinWidth(50);
			jtNeighbors.getColumnModel().getColumn(1).setMaxWidth(50);	
					
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(GCConstants.ACTION_OTHER_VIEW_CLASS_RESULTS)) {
			this.setVisible(false);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			int index = jtNeighbors.convertRowIndexToModel( jtNeighbors.getSelectedRow());
			// message = "<html>";
			
			Sentence s = (Sentence) jtNeighbors.getModel().getValueAt(index, 2);
			
			HashMap<String, ClassificationType> classifications = s.getClassifications();
		
			ArrayList<String> keys = new ArrayList<String>(classifications.keySet());
			Collections.sort(keys);
			
			System.out.println(s.getSentence());
			
			for (String key: keys) {
				System.out.println("    "+key+":\t"+classifications.get(key).getValue());
				//message += key+": " + classifications.get(key).getValue()+"<br>";
			}
			
			//message += "</html>";
			//JOptionPane.showMessageDialog(this, message);
					//showInputDialog(null,"<html>Current <i>k</i> for the instance learner: "+_currentKValueForInstanceLearner+"<p>Enter  new <i>k</i>:","Change K", JOptionPane.QUESTION_MESSAGE);

            //int firstIndex = e.get
            
            //System.out.println(index);		
		}
	}

}

package edu.ncsu.csc.nl.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.ncsu.csc.nl.GCController;
import edu.ncsu.csc.nl.event.NLPEventManager;
import edu.ncsu.csc.nl.event.NLPEventMarkClassifiedAndMove;
import edu.ncsu.csc.nl.event.NLPEventType;
import edu.ncsu.csc.nl.model.Sentence;
import edu.ncsu.csc.nl.model.classification.ClassifiableItem;

public abstract class ClassificationPanel extends JPanel implements ActionListener {

	/**	 */
	private static final long serialVersionUID = 1L;
	
	private ClassifiableItem _itemUnderClassification;
	

	JButton _btnMarkAll   = new JButton("Mark All");		
	JButton _btnClearAll  = new JButton("Clear All");
	JButton _btnClassify  = new JButton("Classify and next");
	JButton _btnMarkTrain = new JButton("Mark Trained");
	JButton _btnMarkUntrained = new JButton("Unmark Trained");
	
	JRadioButton _jrbLastItemMoveToTop    = new JRadioButton("top");
	JRadioButton _jrbLastItemMoveToBottom = new JRadioButton("bottom");
	JRadioButton _jrbLastItemMoveToNext   = new JRadioButton("next");
	ButtonGroup _bgAction = new ButtonGroup();
	
	protected void createBasePanel() {
		this.setLayout(new BorderLayout());
		
		// sets up the side buttons 
		JPanel sideButtonPanel = new JPanel();
		sideButtonPanel.setLayout(new GridLayout(5, 1, 0, 0));
		sideButtonPanel.add(_btnMarkAll);
		sideButtonPanel.add(_btnClearAll);
		sideButtonPanel.add(_btnClassify);		
		sideButtonPanel.add(_btnMarkTrain);
		sideButtonPanel.add(_btnMarkUntrained);
		
		JPanel jp = new JPanel(new GridBagLayout());
		jp.add(sideButtonPanel,new GridBagConstraints());
		
		this.add(jp,BorderLayout.EAST);
		
		JPanel bottomPanel = new JPanel(new FlowLayout());
		bottomPanel.add(new JLabel("On last item:"));
		bottomPanel.add(_jrbLastItemMoveToTop);
		bottomPanel.add(_jrbLastItemMoveToBottom);
		bottomPanel.add(_jrbLastItemMoveToNext);
		_bgAction.add(_jrbLastItemMoveToTop);
		_bgAction.add(_jrbLastItemMoveToBottom);
		_bgAction.add(_jrbLastItemMoveToNext);
		_jrbLastItemMoveToNext.doClick();
		this.add(bottomPanel,BorderLayout.SOUTH);
		
		this.add(this.getClassifyAreaPanel(), BorderLayout.CENTER);
		
		// sets up the action handlers
		_btnMarkAll.addActionListener(this);	
		_btnClearAll.addActionListener(this);
		_btnClassify.addActionListener(this);
		_btnMarkTrain.addActionListener(this);
		_btnMarkUntrained.addActionListener(this);
	}
	
	protected abstract void actionMarkAll();
	protected abstract void actionClearAll();

	protected void actionClassify() {
		NLPEventManager.getTheEventManager().sendEvent(NLPEventType.MARK_CLASSIFIED_AND_MOVE, new NLPEventMarkClassifiedAndMove());
	}
	
	protected abstract JComponent getClassifyAreaPanel();
	
	public void actionPerformed(ActionEvent ae) {
		if      (ae.getSource() == _btnMarkAll)   { actionMarkAll();     }
		else if (ae.getSource() == _btnClearAll)  { actionClearAll();    }
		else if (ae.getSource() == _btnClassify)  { actionClassify();    }
		else if (ae.getSource() == _btnMarkTrain) { markCurrentSentenceTrained(); }
		else if (ae.getSource() == _btnMarkUntrained) { markCurrentSentenceUnTrained(); }
	}
	
	public void setCurrentItem(Sentence s, ClassifiableItem itemToClassify) {
		if (s!=null && s.isTrained()) {
			_btnMarkTrain.setEnabled(false);
			_btnMarkUntrained.setEnabled(true);
		}
		else {
			_btnMarkTrain.setEnabled(true);
			_btnMarkUntrained.setEnabled(false);	
		}
		
		_itemUnderClassification = itemToClassify;
		classificationItemChanged(itemToClassify);
	}
	
	public ClassifiableItem getCurrentItem() {
		return _itemUnderClassification;
	}
	
	private void markCurrentSentenceTrained() {
		_btnMarkTrain.setEnabled(false);
		_btnMarkUntrained.setEnabled(true);
		GCController.getTheGCController().trainCurrentSentence();
	}
	
	private void markCurrentSentenceUnTrained() {
		_btnMarkTrain.setEnabled(true);
		_btnMarkUntrained.setEnabled(false);
		GCController.getTheGCController().unTrainCurrentSentence();
	}		
	
	
	protected abstract void classificationItemChanged(ClassifiableItem itemToClassify);
	
	
}

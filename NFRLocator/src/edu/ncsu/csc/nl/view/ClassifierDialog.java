package edu.ncsu.csc.nl.view;

import javax.swing.JDialog;

import edu.ncsu.csc.nl.model.Sentence;
import edu.ncsu.csc.nl.model.classification.ClassifiableItem;

public class ClassifierDialog extends JDialog {

	/** */
	private static final long serialVersionUID = 1L;

	private ClassificationPanel _classificationPanel;
	
	public ClassifierDialog(MainFrame mf, ClassificationPanel cp) {
		super(mf,false);
		this.setContentPane(cp);
		this.setSize(280, 280);
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		
		_classificationPanel = cp;
	}
	
	public void setCurrentClassificationItem(Sentence s, ClassifiableItem ci) {
		_classificationPanel.setCurrentItem(s, ci);
	}
}

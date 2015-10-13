package edu.ncsu.csc.nl.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.ncsu.csc.nl.GCConstants;
import edu.ncsu.csc.nl.GCController;
import edu.ncsu.csc.nl.ReportController;

import javax.swing.KeyStroke;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

import javax.swing.JCheckBoxMenuItem;

public class MainFrame extends JFrame {

	public static final long serialVersionUID = 1;
	
	private static class MyMenu extends JMenuItem {
		public static final long serialVersionUID = 1;
		
		public MyMenu(String text, String actionCommand) {
			this(text,actionCommand, GCController.getTheGCController());
		}
		
		public MyMenu(String text, String actionCommand,ActionListener al) {
			super(text);
			
			this.setActionCommand(actionCommand);
			this.addActionListener(al);
		}		
		
	}
	
	

	MyMenu _miNew               = new MyMenu("New",GCConstants.ACTION_DOCUMENT_NEW);
	MyMenu _miLoadJSON          = new MyMenu("Load JSON File",GCConstants.ACTION_DOCUMENT_LOAD_JSON);
	MyMenu _miAppendJSON        = new MyMenu("Append JSON File",GCConstants.ACTION_DOCUMENT_APPEND_JSON);
	MyMenu _miLoadSerial        = new MyMenu("Load Serial File",GCConstants.ACTION_DOCUMENT_LOAD_SERIAL);
	MyMenu _miLoadDocument      = new MyMenu("Load Text Document",GCConstants.ACTION_DOCUMENT_LOAD_TEXT_DOCUMENT);
	MyMenu _miSaveJSON          = new MyMenu("Save JSON File",GCConstants.ACTION_DOCUMENT_SAVE_JSON);
	MyMenu _miSaveSerial        = new MyMenu("Save Serial File",GCConstants.ACTION_DOCUMENT_SAVE_SERIAL);
	MyMenu _miCompareJSON       = new MyMenu("Compare JSON File", GCConstants.ACTION_DOCUMENT_COMPARE_JSON);
	
	MyMenu _miExit               = new MyMenu("Exit",GCConstants.ACTION_DOCUMENT_EXIT);	
	MyMenu _miExportAsARFF       = new MyMenu("Export as ARFF ...",GCConstants.ACTION_DOCUMENT_EXPORT_ARFF);
	MyMenu _miLineGoto           = new MyMenu("Goto Line ...",GCConstants.ACTION_DOCUMENT_GOTO_LINE);
	MyMenu _miGotoNextUnclass    = new MyMenu("Goto First Unclassifed",GCConstants.ACTION_DOCUMENT_GOTO_NEXT_UNCLASS);

	MyMenu _miSetCurrentDocID   = new MyMenu("Set Document ID",GCConstants.ACTION_DOCUMENT_SET_ID);
	MyMenu _miRenumber          = new MyMenu("Renumber",GCConstants.ACTION_DOCUMENT_RENUMBER);
	
	
    MyMenu _miReportValidation         = new MyMenu("Validation Report", GCConstants.ACTION_REPORT_VALIDATION, ReportController.getTheReportController());
	MyMenu _miReportDocumentStatistics = new MyMenu("View Document Stats",GCConstants.ACTION_REPORT_DOCUMENT_STATISTICS, ReportController.getTheReportController());
	MyMenu _miReportFreqReport         = new MyMenu("Frequency Report - overall",GCConstants.ACTION_REPORT_FREQUENCY, ReportController.getTheReportController());
	MyMenu _miReportPrintFreqClassRpt  = new MyMenu("Frequency Report - by class",GCConstants.ACTION_REPORT_FREQUENCY_BY_CLASSIFICATION, ReportController.getTheReportController());
	MyMenu _miReportPrintFreqSS        = new MyMenu("Frequency Report - spreadsheet",GCConstants.ACTION_REPORT_FREQUENCY_SPREADSHEET, ReportController.getTheReportController());
	MyMenu _miReportClassSentences     = new MyMenu("Sentences by Classification",GCConstants.ACTION_REPORT_CLASSIFICATION_SENTENCES, ReportController.getTheReportController());
	MyMenu _miReportCustom             = new MyMenu("Custom", GCConstants.ACTION_REPORT_CUSTOM, ReportController.getTheReportController());
		
	MyMenu _miClassMark_DB_FT    = new MyMenu("Mark DB, Fnct",GCConstants.ACTION_CLASS_MARK_DB_FT);
	MyMenu _miClassMark_AC_DB_FT = new MyMenu("Mark Access, DB, Fnct",GCConstants.ACTION_CLASS_MARK_ACF_DB_FT);
	MyMenu _miClassMark          = new MyMenu("Mark Complete", GCConstants.ACTION_CLASS_MARK_COMPLETE);
		
	JPanel _jpCurrentPanel = new JPanel();
	JTextField _jtfStatus  = new JTextField();
	SentenceDisplayPanel _sentenceClassifier;
	
	BorderLayout _layout = new BorderLayout();
	
	 MyMenu _miWordNetBrowser = new MyMenu("WordNet Browser",GCConstants.ACTION_OTHER_WORDNET_BROWSER);
	
	 MyMenu _miLoadPipeLine = new MyMenu("Load Pipeline",GCConstants.ACTION_LOAD_PIPELINE);

	 MyMenu _miAbout = new MyMenu("About","");

	
	 MyMenu _miClearLearner = new MyMenu("Clear Learner",GCConstants.ACTION_LEARNER_CLEAR);
	 MyMenu _miDumpLearner  = new MyMenu("Dump Learner",GCConstants.ACTION_LEARNER_DUMP);
	 MyMenu _miLoadLearner  = new MyMenu("Load Learner",GCConstants.ACTION_LEARNER_LOAD);
	 MyMenu _miSaveLearner  = new MyMenu("Save Learner",GCConstants.ACTION_LEARNER_SAVE);
	 MyMenu _miLoadLearnerSer  = new MyMenu("Load Learner - serial",GCConstants.ACTION_LEARNER_LOAD_SERIAL);
	 MyMenu _miSaveLearnerSer  = new MyMenu("Save Learner - serial",GCConstants.ACTION_LEARNER_SAVE_SERIAL);	
	 MyMenu _miSetK              = new MyMenu("Set K",GCConstants.ACTION_LEARNER_SETK);
	 MyMenu _miSelfEvaluate      = new MyMenu("Evaluate by n-Folds",GCConstants.ACTION_LEARNER_SELFEVAL);
	 MyMenu _miDocumentEvaluate  = new MyMenu("Evaluate by Documents",GCConstants.ACTION_LEARNER_DOCUMENT_EVAL);	
	 MyMenu _miEvaluateWithIntNB = new MyMenu("Evaluate with internal NB",GCConstants.ACTION_LEARNER_INTERNAL_NB);
	 
	 MyMenu _miLoadAllTrained = new MyMenu("Load all Trained Sentences",GCConstants.ACTION_LEARNER_ADDTRAIN);
	 MyMenu _miMoveLearnerToDoc = new MyMenu("Mover Learner to Current Doc",GCConstants.ACTION_LEARNER_MOVE_TO_CURRENT_DOC);
	 
	 JCheckBoxMenuItem _jcbmiSupervisedLearning = new JCheckBoxMenuItem("Supervised Learning");
	 JCheckBoxMenuItem _jcbmAutoCompleteMatch   = new JCheckBoxMenuItem("Auto Complete Exact Match",true);

	 MyMenu _miPOSOverrides = new MyMenu("POS Overrides",GCConstants.ACTION_OTHER_POS_OVERRIDES);
	 MyMenu _miViewClassResults = new MyMenu("Classifications ...",GCConstants.ACTION_OTHER_VIEW_CLASS_RESULTS);
	 MyMenu _miFind             = new MyMenu("Find",GCConstants.ACTION_OTHER_FIND);
	 MyMenu _miFindNext         = new MyMenu("Find Next",GCConstants.ACTION_OTHER_FIND_NEXT);
	 
	 MyMenu _miCluster          = new MyMenu("Cluster",GCConstants.ACTION_OTHER_CLUSTER);
	 MyMenu _miOtherAllSelfEval = new MyMenu("All Self-Evaluate",GCConstants.ACTION_OTHER_ALL_SELF_EVALUATE);
	 MyMenu _miOtherAllDocEval  = new MyMenu("All Document-Evaluate",GCConstants.ACTION_OTHER_ALL_DOC_EVALUATE);
	 MyMenu _miOtherCurrentDoc  = new MyMenu("Current Document-Evaluate",GCConstants.ACTION_OTHER_CURRENT_DOC_EVALUATE);
	 MyMenu _miBootstrap        = new MyMenu("Verb Frequencies",GCConstants.ACTION_OTHER_VERB_FREQUENCIES);
	 MyMenu _miRestoreACRelation= new MyMenu("Restore AC Relations", GCConstants.ACTION_OTHER_RESTORE_AC_RELATIONS);
	
	 MyMenu _miWekaComputeIGClassifications = new MyMenu("Information Gain - Classifications (LNR)",GCConstants.ACTION_WEKA_COMPUTE_INFO_GAIN_CLASSIFICATIONS);
	 
	 MyMenu _miWekaNaiveBayes = new MyMenu("Evaluate with Naive Bayes",GCConstants.ACTION_WEKA_EVAL_NAIVE_BAYES);
	 MyMenu _miWekaSMO        = new MyMenu("Evaluate with SVM(SMO)",GCConstants.ACTION_WEKA_EVAL_SMO);
	 MyMenu _miWekaCreate     = new MyMenu("Create from Learner",GCConstants.ACTION_WEKA_CREATE_CLASSIFIERS);
	 MyMenu _miWekaClassify   = new MyMenu("Classify Current Sentence",GCConstants.ACTION_WEKA_CLASSIFY_CURRENT);
	 MyMenu _miWekaCurrentDocNB  = new MyMenu("Current Doc - Naive Bayes",GCConstants.ACTION_WEKA_CURRENT_DOC_NAIVE_BAYES);
	 MyMenu _miWekaCurrentDocSMO = new MyMenu("Current Doc - SVM(SMO)",GCConstants.ACTION_WEKA_CURRENT_DOC_SMO);

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		super();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setSize(1250,650);
		
		JMenuBar mb = new JMenuBar();
		this.setJMenuBar(mb);
		
		JMenu projectMenu    = new JMenu("Project");
		JMenu reportMenu     = new JMenu("Reports");
		
		//JMenu dictionaryMenu = new JMenu("Dictionary");

		JMenu learnerMenu    = new JMenu("Learner");
		JMenu wekaMenu       = new JMenu("Weka");
		JMenu otherMenu      = new JMenu("Other");
		JMenu classMenu      = new JMenu("Classfications");
		JMenu helpMenu       = new JMenu("Help");
		
		mb.add(projectMenu);
		mb.add(reportMenu);

		mb.add(learnerMenu);
		learnerMenu.add(_miClearLearner);
		learnerMenu.add(_miDumpLearner);
		learnerMenu.add(_miLoadLearner);
		learnerMenu.add(_miSaveLearner);
		learnerMenu.addSeparator();
		learnerMenu.add(_miLoadLearnerSer);
		learnerMenu.add(_miSaveLearnerSer);
		learnerMenu.addSeparator();
		learnerMenu.add(_jcbmiSupervisedLearning);
		learnerMenu.add(_jcbmAutoCompleteMatch);
		learnerMenu.addSeparator();
		learnerMenu.add(_miLoadAllTrained);
		learnerMenu.add(_miSetK);
		learnerMenu.addSeparator();		
		learnerMenu.add(_miSelfEvaluate);	
		learnerMenu.add(_miDocumentEvaluate);
		learnerMenu.add(_miEvaluateWithIntNB);
		learnerMenu.addSeparator();
		learnerMenu.add(_miMoveLearnerToDoc);
		
		//learnerMenu.addSeparator();
		//learnerMenu.add(_miLearnerExportSOA);
		//learnerMenu.add(_miLearnerSOAMatrix);
		//learnerMenu.add(_miLearnerSOAReport);
		
		
		_miSetK.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_MASK));
				
		
		mb.add(wekaMenu);
		wekaMenu.add(_miWekaComputeIGClassifications);
		wekaMenu.addSeparator();
		wekaMenu.add(_miWekaCreate);
		wekaMenu.addSeparator();	
		wekaMenu.add(_miWekaClassify);
		wekaMenu.addSeparator();	
		wekaMenu.add(_miWekaNaiveBayes);
		wekaMenu.add(_miWekaSMO);
		wekaMenu.add(_miWekaCurrentDocNB);
		wekaMenu.add(_miWekaCurrentDocSMO);
		
		//wekaMenu.addSeparator();
		//wekaMenu.add(_miWekaSOANaiveBayes);
		//wekaMenu.add(_miWekaSOASMO);
		//wekaMenu.add(_miWekaSOACurrentDocNB);
		//wekaMenu.add(_miWekaSOACurrentDocSMO);
		
		//mb.add(new JMenu("Requirement Extraction"));
		
		mb.add(otherMenu);
		otherMenu.add(_miWordNetBrowser);
		otherMenu.add(_miPOSOverrides);
		otherMenu.add(_miViewClassResults);
		otherMenu.addSeparator();
		otherMenu.add(_miFind);
		otherMenu.add(_miFindNext);
		otherMenu.addSeparator();

		otherMenu.add(_miCluster);
		otherMenu.addSeparator();
		otherMenu.add(_miOtherAllSelfEval);
		otherMenu.add(_miOtherAllDocEval);
		otherMenu.add(_miOtherCurrentDoc);
		otherMenu.addSeparator();
		otherMenu.add(_miBootstrap);
		otherMenu.add(_miRestoreACRelation);

		_miFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
		_miFindNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3,0));
		

		

		mb.add(classMenu);
		classMenu.add(_miClassMark_DB_FT);
		classMenu.add(_miClassMark_AC_DB_FT);
		classMenu.add(_miClassMark);
		
		_miClassMark_DB_FT.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9,0));
		_miClassMark_AC_DB_FT.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11,0));
		_miClassMark.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12,0));
			
		mb.add(helpMenu);
		_miLoadPipeLine.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		
		helpMenu.add(_miLoadPipeLine);
		helpMenu.add(_miAbout);
		_miNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));

		_miLoadJSON.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		_miSaveJSON.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		_miLineGoto.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));
		_miExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
		
		_miGotoNextUnclass.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5,0));

		projectMenu.add(_miNew);
		projectMenu.add(_miLoadJSON);
		projectMenu.add(_miLoadSerial);
		projectMenu.add(_miLoadDocument);
		projectMenu.add(_miAppendJSON);
		projectMenu.add(_miSaveJSON);
		projectMenu.add(_miSaveSerial);
		projectMenu.addSeparator();
		projectMenu.add(_miExportAsARFF);
		projectMenu.addSeparator();
		projectMenu.add(_miCompareJSON);
		projectMenu.addSeparator();
		projectMenu.add(_miLineGoto);
		projectMenu.add(_miGotoNextUnclass);
		projectMenu.add(_miSetCurrentDocID);
		projectMenu.add(_miRenumber);
		projectMenu.addSeparator();

		projectMenu.add(_miExit);
		
		reportMenu.add(_miReportValidation);
		reportMenu.addSeparator();
		reportMenu.add(_miReportDocumentStatistics);
		reportMenu.add(_miReportFreqReport);
		reportMenu.add(_miReportPrintFreqClassRpt);
		reportMenu.add(_miReportPrintFreqSS);
		reportMenu.add(_miReportClassSentences);
		reportMenu.addSeparator();
		reportMenu.add(_miReportCustom);
		
		
		_miAbout.addActionListener(  new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, GCConstants.VERSION_INFORMATION, "Access Control Relation Extractor", JOptionPane.PLAIN_MESSAGE); 

            }
        });
				
		_jtfStatus.setEditable(false);
		_jtfStatus.setText("");
		
		this.getContentPane().setLayout(_layout);
		
		_sentenceClassifier = new SentenceDisplayPanel();
		_jpCurrentPanel = _sentenceClassifier;
		
		this.getContentPane().add(_jpCurrentPanel,BorderLayout.CENTER);
		this.getContentPane().add(_jtfStatus, BorderLayout.SOUTH);
	}
	
	public SentenceDisplayPanel getSentenceClassifer() {
		return _sentenceClassifier;
	}
	
	public void setStatusMessage(String s) {
		_jtfStatus.setText(s);
	}
	
	public boolean isSupervisedLearningChecked() {
		return _jcbmiSupervisedLearning.isSelected();
	}

	public boolean isAutocompleteChecked() {
		return _jcbmiSupervisedLearning.isSelected();
	}	
}

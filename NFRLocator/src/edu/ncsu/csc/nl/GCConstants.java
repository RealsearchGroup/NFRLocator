package edu.ncsu.csc.nl;


public class GCConstants {

	public static final String VERSION_INFORMATION = "NFR Locator, verion 1.01";
	
	public static final int VIEW_CLASSIFICATIONS = 1;
	
	public static final String ACTION_PARSE_NEW_SENTENCE = "ParseNewSentence";
	public static final String ACTION_DICTIONARY_VIEW = "ViewDict";
	public static final String ACTION_DICTIONARY_LOAD = "LoadDict";
	public static final String ACTION_DICTIONARY_SAVE = "SaveDict";
	public static final String ACTION_DICTIONARY_REPORT = "ReportDict";
	public static final String ACTION_LOAD_PIPELINE   = "LoadPipe";
	public static final String ACTION_LEARNER_CLEAR   = "ClearLearner";
	public static final String ACTION_LEARNER_DUMP    = "DumpLearner";
	public static final String ACTION_LEARNER_LOAD    = "LoadLearner";
	public static final String ACTION_LEARNER_SAVE    = "SaveLearner";
	public static final String ACTION_LEARNER_LOAD_SERIAL = "LoadLearnerSerial";
	public static final String ACTION_LEARNER_SAVE_SERIAL = "SaveLearnerSerial";
	public static final String ACTION_LEARNER_SETK    = "setklearner";
	public static final String ACTION_LEARNER_SELFEVAL = "selfevallearner";
	public static final String ACTION_LEARNER_DOCUMENT_EVAL = "documentEvalLearner";
	public static final String ACTION_LEARNER_INTERNAL_NB   = "internalNBEvaluation";
	
	
	public static final String ACTION_LEARNER_MOVE_TO_CURRENT_DOC = "learnerMoveToCurrentDoc";

	public static final String ACTION_LEARNER_ADDTRAIN = "AddTrainSent";
	public static final String ACTION_LEARNER_SUPERON  = "LearnSupperOn";
	public static final String ACTION_LEARNER_SUPEROFF = "LearnSupperOff";
	
	
	public static final String ACTION_WEKA_COMPUTE_INFO_GAIN_CLASSIFICATIONS = "wekaInformationGainClassifictions";
	public static final String ACTION_WEKA_EVAL_NAIVE_BAYES   = "WekaNaiveBayes";
	public static final String ACTION_WEKA_EVAL_SMO           = "WekaSMO";
	public static final String ACTION_WEKA_CURRENT_DOC_NAIVE_BAYES   = "WekaCurrDocNaiveBayes";
	public static final String ACTION_WEKA_CURRENT_DOC_SMO           = "WekaCurrDocSMO";
	
	public static final String ACTION_WEKA_CREATE_CLASSIFIERS = "WekacreateClass";
	public static final String ACTION_WEKA_CLASSIFY_CURRENT   = "WekaClassifycurrentSent";
	
	public static final String ACTION_DOCUMENT_NEW                = "actDocNew";
	public static final String ACTION_DOCUMENT_LOAD_JSON          = "actDocLoadJson";
	public static final String ACTION_DOCUMENT_LOAD_SERIAL        = "actDocLoadSer";
	public static final String ACTION_DOCUMENT_LOAD_TEXT_DOCUMENT = "actDocLoadText";
	public static final String ACTION_DOCUMENT_APPEND_JSON        = "actDocAppendJSON";
	public static final String ACTION_DOCUMENT_SAVE_JSON          = "actDocSaveJSON";
	public static final String ACTION_DOCUMENT_SAVE_SERIAL        = "actDocSaveSerial";
	public static final String ACTION_DOCUMENT_EXPORT_ARFF        = "actDocExportARFF";
	public static final String ACTION_DOCUMENT_EXIT               = "actDocExit";

	public static final String ACTION_DOCUMENT_COMPARE_JSON       = "actDocCompJSON";
	
	public static final String ACTION_DOCUMENT_GOTO_LINE          = "actiongotoLine";
	public static final String ACTION_DOCUMENT_GOTO_NEXT_UNCLASS  = "actiongotonextunclass";


	public static final String ACTION_DOCUMENT_SET_ID             = "docsetID";
	public static final String ACTION_DOCUMENT_RENUMBER           = "docRenumberMe";

	public static final String ACTION_REPORT_VALIDATION                  = "reportValidation";
	public static final String ACTION_REPORT_DOCUMENT_STATISTICS         = "DOCUMENTCurrREport";
	public static final String ACTION_REPORT_FREQUENCY                   = "docactionfreqreport";
	public static final String ACTION_REPORT_FREQUENCY_BY_CLASSIFICATION = "docactionfreqreportclass";
	public static final String ACTION_REPORT_FREQUENCY_SPREADSHEET       = "docactionfreqspreadsheet";	
	public static final String ACTION_REPORT_CLASSIFICATION_SENTENCES    = "reprotClassificationSentences";

	public static final String ACTION_REPORT_CUSTOM                      = "reportCustomRpt";
	
	public static final String ACTION_POPUP_REMOVE_SENTENCE  = "PopupRemoveSentence";
	public static final String ACTION_POPUP_REPLACE_SENTENCE = "ActReplaceSentence";
	public static final String ACTION_POPUP_REPARSE_SENTENCE = "popActReparseSentence";
	
	public static final String ACTION_OTHER_POS_OVERRIDES      = "OTHPOSoverRides";
	public static final String ACTION_OTHER_WORDNET_BROWSER    = "OTHWordNetBrowser";
	public static final String ACTION_OTHER_VIEW_CLASS_RESULTS = "OTHViewClassResults";
	public static final String ACTION_OTHER_FIND               = "OTHFind";
	public static final String ACTION_OTHER_FIND_NEXT          = "OTHFindNext";;
	public static final String ACTION_OTHER_CLUSTER            = "OTHcluster";
	public static final String ACTION_OTHER_ALL_SELF_EVALUATE  = "OTHallSelfEval";
	public static final String ACTION_OTHER_ALL_DOC_EVALUATE  = "OTHallDocEval";
	public static final String ACTION_OTHER_CURRENT_DOC_EVALUATE  = "OTHallCURRENTDocEval";
	
	public static final String ACTION_OTHER_VERB_FREQUENCIES             = "OTHbootitbaby";
	public static final String ACTION_OTHER_RESTORE_AC_RELATIONS  = "OTHrestoreACrelations";
		
	public static final String ACTION_CLASS_MARK_COMPLETE  = "CLASSaction_mark_complete";
	public static final String ACTION_CLASS_MARK_DB_FT     = "CLASSaction_mark_DB_FT_complete";
	public static final String ACTION_CLASS_MARK_ACF_DB_FT = "CLASSaction_mark_ACF_DB_FT_complete";
	
	
	public static final int UNDEFINED = -2;
	
	public static final String CLASSIFICATION_ACCESS_CONTROL_FUNCTIONAL = "access control:functional";
}

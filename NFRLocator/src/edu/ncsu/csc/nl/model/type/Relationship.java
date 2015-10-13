package edu.ncsu.csc.nl.model.type;

/**
 * Represents a relationship between two words in Stanford's Collapsed Typed dependency graph.
 * 
 * @author John
 *
 */
public enum Relationship {
	ABBREV("abbrev",'a',"","",false),
	ACOMP("acomp",'b',"","",false),
	ADVCL("advcl",'c',"","",true),
	ADVMOD("advmod",'d',"","",false),
	AGENT("agent",'A',"agent","An agent is the complement of a passive verb which is introduced by the preposition \"by\" and does the action.",false),
	AMOD("amod",'e',"adjectival modifer","An adjectival modifer of an NP is any ajectival phrase that serves to define or modify that NP.",false),
	APPOS("appos",'f',"appositional modifier","An appositional modifer of an NP is an NP immediately to the right of the first NP that serves to define or modify that NP. It includes parenthesized examples.",false),
	ATTR("attr",'g',"attributive","An attributive is a WHNP complement of a copular verb such as \"to be\", \"to seem\", \"to appear\".",false),
	AUX("aux",'h',"","",false),
	AUXPASS("auxpass",'i',"","",false),
	CC("cc",'j',"","",false),
	CCOMP("ccomp",'k',"","",true),
	COMPLM("complm",'l',"","",false), 
	CONJ("conj",'C',"","",false),
	CONJ_AND("conj_and",'C',"","",false),
	CONJ_AND_OR("conj_and_or",'C',"","",false),
	CONJ_AS("conj_as",'C',"","",false),
	CONJ_NEGCC("conj_negcc",'m',"","",false),
	CONJ_OR("conj_or",'C',"","",false),
	CONJ_BUT("conj_but",'n',"","",false),
	CONJ_NOR("conj_nor",'o',"","",false),
	CONJ_IN("conj_in",'p',"","",false),
	CONJ_ONLY("conj_plus",'y',"","",false),
	CONJ_PLUS("conj_+",'q',"","",false),
	COP("cop",'r',"","",false),
	CSUBJ("csubj",'s',"","",true),
	CSUBJPASS("csubjpass",'t',"","",true),
	DEP("dep",'D',"","",false),
	DET("det",'u',"","",false),
	DET_NEG("det_neg",'v',"negative determiner","This represents a negative determiner prior to a noun.  Not in the stanford ",false),
	DISCOURSE("discourse",'w',"","",false),
	DOBJ("dobj",'O',"","",false),
	EXPL("expl",'y',"","",false),
	INFMOD("infmod",'z',"","",false),
	IOBJ("iobj",'1',"","",false),
	MARK("mark",'2',"","",true),
	MWE("mwe",'3',"","",false),
	NEG("neg",'4',"","",false),
	NN("nn",'N',"","",false),
	NPADVMOD("npadvmod",'5',"","",false),
	NSUBJ("nsubj",'S',"","",false),
	NSUBJPASS("nsubjpass",'6',"","",false),
	NUM("num",'7',"","",false),
	NUMBER("number",'8',"","",false),
	PARATAXIS("parataxis",'9',"","",false),
	PARTMOD("partmod",'!',"","",false),
	PCOMP("pcomp",'@',"","",false),
	POBJ("pobj",'#',"","",false),
	POSS("poss",'$',"","",false),
	POSSESSIVE("possessive",'%',"","",false),
	PRECONJ("preconj",'^',"","",false),
	PREDET("predet",'&',"","",false),
	PREP("prep",'P', "","",false),
	PREPC("prepc",'P',"","",true),
	PREPC_ABOARD("prepc_aboard",'P',"","",true),
	PREPC_ABOUT("prepc_about",'P',"","",true),
	PREPC_ABOVE("prepc_above",'P',"","",true),
	PREPC_ACCORDING("prepc_according",'P',"","",true),
	PREPC_ACROSS("prepc_across",'P',"","",true),
	PREPC_AFTER("prepc_after",'P',"","",true),
	PREPC_AGAINST("prepc_against",'P',"","",true),
	PREPC_ALONG("prepc_along",'P',"","",true),
	PREPC_ALONGSIDE("prepc_alongside",'P',"","",true),
	PREPC_AMID("prepc_amid",'P',"","",true),
	PREPC_AMIDST("prepc_amidst",'P',"","",true),
	PREPC_AMONG("prepc_among",'P',"","",true),
	PREPC_AMONGST("prepc_amongst",'P',"","",true),
	PREPC_ANTI("prepc_anti",'P',"","",true),
	PREPC_AROUND("prepc_around",'P',"","",true),
	PREPC_AS("prepc_as",'P',"","",true),
	PREPC_ASTRIDE("prepc_astride",'P',"","",true),
	PREPC_AT("prepc_at",'P',"","",true),
	PREPC_ATOP("prepc_atop",'P',"","",true),
	PREPC_ACCORDING_TO("prepc_according_to",'P',"","",true),
	PREPC_ACROSS_FROM("prepc_across_from",'P',"","",true),
	PREPC_AHEAD_OF("prepc_ahead_of",'P',"","",true),
	PREPC_ALONG_WITH("prepc_along_with",'P',"","",true),
	PREPC_ALONGSIDE_OF("prepc_alongside_of",'P',"","",true),
	PREPC_APART_FROM("prepc_apart_from",'P',"","",true),
	PREPC_AS_FOR("prepc_as_for",'P',"","",true),
	PREPC_ASIDE_FROM("prepc_aside_from",'P',"","",true),
	PREPC_AS_OF("prepc_as_of",'P',"","",true),
	PREPC_AS_PER("prepc_as_per",'P',"","",true),
	PREPC_AS_TO("prepc_as_to",'P',"","",true),
	PREPC_AS_WELL_AS("prepc_as_well_as",'P',"","",true),
	PREPC_AWAY_FROM("prepc_away_from",'P',"","",true),
	PREPC_À_LA("prepc_à_la",'P',"","",true), 
	PREPC_BAR("prepc_bar",'P',"","",true),
	PREPC_BARRING("prepc_barring",'P',"","",true),
	PREPC_BECAUSE("prepc_because",'P',"","",true),
	PREPC_BEFORE("prepc_before",'P',"","",true),
	PREPC_BEHIND("prepc_behind",'P',"","",true),
	PREPC_BELOW("prepc_below",'P',"","",true),
	PREPC_BENEATH("prepc_beneath",'P',"","",true),
	PREPC_BESIDE("prepc_beside",'P',"","",true),
	PREPC_BESIDES("prepc_besides",'P',"","",true),
	PREPC_BETWEEN("prepc_between",'P',"","",true),
	PREPC_BEYOND("prepc_beyond",'P',"","",true),
	PREPC_BY("prepc_by",'P',"","",true),
	PREPC_BUT("prepc_but",'P',"","",true),
	PREPC_BECAUSE_OF("prepc_because_of",'P',"","",true),
	PREPC_BUT_FOR("prepc_but_for",'P',"","",true),
	PREPC_BY_MEANS_OF("prepc_by_means_of",'P',"","",true),
	PREPC_BASED_ON("prepc_based_on",'P',"","",true),
	PREPC_CIRCA("prepc_circa",'P',"","",true),
	PREPC_CONCERNING("prepc_concerning",'P',"","",true),
	PREPC_CONSIDERING("prepc_considering",'P',"","",true),
	PREPC_COUNTING("prepc_counting",'P',"","",true),
	PREPC_CUM("prepc_cum",'P',"","",true),
	PREPC_CLOSE_BY("prepc_close_by",'P',"","",true),
	PREPC_CLOSE_TO("prepc_close_to",'P',"","",true),
	PREPC_CONTRARY_TO("prepc_contrary_to",'P',"","",true),
	PREPC_COMPARED_TO("prepc_compared_to",'P',"","",true),
	PREPC_COMPARED_WITH("prepc_compared_with",'P',"","",true),
	PREPC_DESPITE("prepc_despite",'P',"","",true),
	PREPC_DOWN("prepc_down",'P',"","",true),
	PREPC_DUE("prepc_due",'P',"","",true),
	PREPC_DURING("prepc_during",'P',"","",true),
	PREPC_DEPENDING_ON("prepc_depending_on",'P',"","",true),
	PREPC_DUE_TO("prepc_due_to",'P',"","",true),
	PREPC_EXCEPT("prepc_except",'P',"","",true),
	PREPC_EXCEPTING("prepc_excepting",'P',"","",true),
	PREPC_EXCLUDING("prepc_excluding",'P',"","",true),
	PREPC_EXCEPT_FOR("prepc_except_for",'P',"","",true),
	PREPC_EXCLUSIVE_OF("prepc_exclusive_of",'P',"","",true),
	PREPC_FOLLOWING("prepc_following",'P',"","",true),
	PREPC_FROM("prepc_from",'P',"","",true),
	PREPC_FOR("prepc_for",'P',"","",true),
	PREPC_FORWARD_OF("prepc_forward_of",'P',"","",true),
	PREPC_FURTHER_TO("prepc_further_to",'P',"","",true),
	PREPC_FOLLOWED_BY("prepc_followed_by",'P',"","",true),
	PREPC_GIVEN("prepc_given",'P',"","",true),
	PREPC_GONE("prepc_gone",'P',"","",true),
	PREPC_IF("prepc_if",'P',"","",true),
	PREPC_IN("prepc_in",'P',"","",true),
	PREPC_INCLUDING("prepc_including",'P',"","",true),
	PREPC_INSIDE("prepc_inside",'P',"","",true),
	PREPC_INTO("prepc_into",'P',"","",true),
	PREPC_IN_ADDITION_TO("prepc_in_addition_to",'P',"","",true),
	PREPC_IN_BETWEEN("prepc_in_between",'P',"","",true),
	PREPC_IN_CASE_OF("prepc_in_case_of",'P',"","",true),
	PREPC_IN_FACE_OF("prepc_in_face_of",'P',"","",true),
	PREPC_IN_FAVOR_OF("prepc_in_favor_of",'P',"","",true),
	PREPC_IN_FRONT_OF("prepc_in_front_of",'P',"","",true),
	PREPC_IN_LIEU_OF("prepc_in_lieu_of",'P',"","",true),
	PREPC_INSTEAD_OF("prepc_instead_of",'P',"","",true),
	PREPC_IN_SPITE_OF("prepc_in_spit_of",'P',"","",true),
	PREPC_IN_VIEW_OF("prepc_in_view_of",'P',"","",true),
	PREPC_IN_ACCORDANCE_WITH("prepc_in_accordance_with",'P',"","",true),
	PREPC_IN_PLACE_OF("prepc_in_place_of",'P',"","",true),
	PREPC_INSIDE_OF("prepc_inside_of",'P',"","",true),
	PREPC_IRRESPECTIVE_OF("prepc_irrespective_of",'P',"","",true),
	PREPC_LESS("prepc_less",'P',"","",true),
	PREPC_LIKE("prepc_like",'P',"","",true),
	PREPC_MINUS("prepc_minus",'P',"","",true),
	PREPC_NEAR("prepc_near",'P',"","",true),
	PREPC_NOTWITHSTANDING("prepc_notwithstanding",'P',"","",true),
	PREPC_NEXT_TO("prepc_next_to",'P',"","",true),
	PREPC_NEAR_TO("prepc_near_to",'P',"","",true),
	PREPC_OF("prepc_of",'P',"","",true),
	PREPC_OFF("prepc_off",'P',"","",true),
	PREPC_ON("prepc_on",'P',"","",true),
	PREPC_ONTO("prepc_onto",'P',"","",true),
	PREPC_OPPOSITE("prepc_opposite",'P',"","",true),
	PREPC_OUTSIDE("prepc_outside",'P',"","",true),
	PREPC_OVER("prepc_over",'P',"","",true),
	PREPC_ON_ACCOUNT_OF("prepc_on_account_of",'P',"","",true),
	PREPC_ON_BEHALF_OF("prepc_on_behalf_of",'P',"","",true),
	PREPC_ON_BOARD("prepc_on_board",'P',"","",true),
	PREPC_ON_TO("prepc_on_to",'P',"","",true),
	PREPC_ON_TOP_OF("prepc_on_top_of",'P',"","",true),
	PREPC_OPPOSITE_TO("prepc_opposite_to",'P',"","",true),
	PREPC_OTHER_THAN("prepc_other_than",'P',"","",true),
	PREPC_OUT("prepc_out",'P',"","",true),
	PREPC_OUT_OF("prepc_out_of",'P',"","",true),
	PREPC_OUTSIDE_OF("prepc_outside_of",'P',"","",true),
	PREPC_OWING_TO("prepc_owing_to",'P',"","",true),
	PREPC_OFF_OF("prepc_off_of",'P',"","",true),
	PREPC_PAST("prepc_past",'P',"","",true),
	PREPC_PENDING("prepc_pending",'P',"","",true),
	PREPC_PER("prepc_per",'P',"","",true),
	PREPC_PLUS("prepc_plus",'P',"","",true),
	PREPC_POST("prepc_post",'P',"","",true),
	PREPC_PRO("prepc_pro",'P',"","",true),
	PREPC_PREPCARATORY_TO("prepc_prepcaratory_to",'P',"","",true),
	PREPC_PRIOR_TO("prepc_prior_to",'P',"","",true),
	PREPC_PREVIOUS_TO("prepc_previous_to",'P',"","",true),
	PREPC_PROVIDING("prepc_providing",'P',"","",true),
	PREPC_PURSUANT_TO("prepc_pursuant_to",'P',"","",true),
	PREPC_RE("prepc_re",'P',"","",true),
	PREPC_REGARDING("prepc_regarding",'P',"","",true),
	PREPC_RESPECTING("prepc_respecting",'P',"","",true),
	PREPC_ROUND("prepc_round",'P',"","",true),
	PREPC_REGARDLESS_OF("prepc_regardless_of",'P',"","",true),
	PREPC_SAVE("prepc_save",'P',"","",true),
	PREPC_SAVING("prepc_saving",'P',"","",true),
	PREPC_SINCE("prepc_since",'P',"","",true),
	PREPC_SAVE_FOR("prepc_save_for",'P',"","",true),
	PREPC_SUBSEQUENT_TO("prepc_subsequent_to",'P',"","",true),
	PREPC_SUCH_AS("prepc_such_as",'P',"","",true),
	PREPC_TERMINATING("prepc_terminating",'P',"","",true),
	PREPC_THAN("prepc_than",'P',"","",true),
	PREPC_THAT("prepc_that",'P',"","",true),
	PREPC_THROUGH("prepc_through",'P',"","",true),
	PREPC_THRU("prepc_thru",'P',"","",true),
	PREPC_THROUGHOUT("prepc_throughout",'P',"","",true),
	PREPC_TILL("prepc_till",'P',"","",true),
	PREPC_TO("prepc_to",'P',"","",true),
	PREPC_TOUCHING("prepc_touching",'P',"","",true),
	PREPC_TOWARDS("prepc_towards",'P',"","",true),
	PREPC_TOWARD("prepc_toward",'P',"","",true),
	PREPC_THANKS_TO("prepc_thanks_to",'P',"","",true),
	PREPC_TOGETHER_WITH("prepc_together_with",'P',"","",true),
	PREPC_UNDER("prepc_under",'P',"","",true),
	PREPC_UNDERNEATH("prepc_underneath",'P',"","",true),
	PREPC_UNLESS("prepc_unless",'P',"","",true),
	PREPC_UNLIKE("prepc_unlike",'P',"","",true),
	PREPC_UNTILUP("prepc_untilup",'P',"","",true),
	PREPC_UPON("prepc_upon",'P',"","",true),
	PREPC_UP_AGAINST("prepc_up_against",'P',"","",true),
	PREPC_UP_TO("prepc_up_to",'P',"","",true),
	PREPC_UP_UNTIL("prepc_up_until",'P',"","",true),
	PREPC_USING("prepc_using",'P',"","",true),
	PREPC_VERSUS("prepc_versus",'P',"","",true),
	PREPC_VIA("prepc_via",'P',"","",true),
	PREPC_VS("prepc_vs",'P',"","",true),
	PREPC_WHEREBY("prepc_whereby",'P',"","",true),
	PREPC_WHILE("prepc_while",'P',"","",true),
	PREPC_WITH("prepc_with",'P',"","",true),
	PREPC_WITHIN("prepc_within",'P',"","",true),
	PREPC_WITHOUT("prepc_without",'P',"","",true),
	PREPC_WORTH("prepc_worth",'P',"","",true),
	PREPC_WITH_REFERENCE_TO("prepc_with_reference_to",'P',"","",true),
	PREPC_WITH_REGARD_TO("prepc_with_regard_to",'P',"","",true),
	PREPC_WITH_RESPECT_TO("prepc_with_respect_to",'P',"","",true),
	PREP_ABOARD("prep_aboard",'P',"","",false),
	PREP_ABOUT("prep_about",'P',"","",false),
	PREP_ABOVE("prep_above",'P',"","",false),
	PREP_ACCORDING("prep_according",'P',"","",false),
	PREP_ACROSS("prep_across",'P',"","",false),
	PREP_AFTER("prep_after",'P',"","",false),
	PREP_AGAINST("prep_against",'P',"","",false),
	PREP_ALONG("prep_along",'P',"","",false),
	PREP_ALONGSIDE("prep_alongside",'P',"","",false),
	PREP_AMID("prep_amid",'P',"","",false),
	PREP_AMIDST("prep_amidst",'P',"","",false),
	PREP_AMONG("prep_among",'P',"","",false),
	PREP_AMONGST("prep_amongst",'P',"","",false),
	PREP_ANTI("prep_anti",'P',"","",false),
	PREP_AROUND("prep_around",'P',"","",false),
	PREP_AS("prep_as",'P',"","",false),
	PREP_ASTRIDE("prep_astride",'P',"","",false),
	PREP_AT("prep_at",'P',"","",false),
	PREP_ATOP("prep_atop",'P',"","",false),
	PREP_ACCORDING_TO("prep_according_to",'P',"","",false),
	PREP_ACROSS_FROM("prep_across_from",'P',"","",false),
	PREP_AHEAD_OF("prep_ahead_of",'P',"","",false),
	PREP_ALONG_WITH("prep_along_with",'P',"","",false),
	PREP_ALONGSIDE_OF("prep_alongside_of",'P',"","",false),
	PREP_APART_FROM("prep_apart_from",'P',"","",false),
	PREP_AS_FOR("prep_as_for",'P',"","",false),
	PREP_ASIDE_FROM("prep_aside_from",'P',"","",false),
	PREP_AS_OF("prep_as_of",'P',"","",false),
	PREP_AS_PER("prep_as_per",'P',"","",false),
	PREP_AS_TO("prep_as_to",'P',"","",false),
	PREP_AS_WELL_AS("prep_as_well_as",'P',"","",false),
	PREP_AWAY_FROM("prep_away_from",'P',"","",false),
	PREP_À_LA("prep_à_la",'P',"","",false), 
	PREP_BAR("prep_bar",'P',"","",false),
	PREP_BARRING("prep_barring",'P',"","",false),
	PREP_BECAUSE("prep_because",'P',"","",false),
	PREP_BEFORE("prep_before",'P',"","",false),
	PREP_BEHIND("prep_behind",'P',"","",false),
	PREP_BELOW("prep_below",'P',"","",false),
	PREP_BENEATH("prep_beneath",'P',"","",false),
	PREP_BESIDE("prep_beside",'P',"","",false),
	PREP_BESIDES("prep_besides",'P',"","",false),
	PREP_BETWEEN("prep_between",'P',"","",false),
	PREP_BEYOND("prep_beyond",'P',"","",false),
	PREP_BY("prep_by",'P',"","",false),
	PREP_BUT("prep_but",'P',"","",false),
	PREP_BECAUSE_OF("prep_because_of",'P',"","",false),
	PREP_BEGINNING("prep_beginning",'P',"","",false),
	PREP_BUT_FOR("prep_but_for",'P',"","",false),
	PREP_BY_MEANS_OF("prep_by_means_of",'P',"","",false),
	PREP_BASED_ON("prep_based_on",'P',"","",false),
	PREP_CIRCA("prep_circa",'P',"","",false),
	PREP_CONCERNING("prep_concerning",'P',"","",false),
	PREP_CONSIDERING("prep_considering",'P',"","",false),
	PREP_COUNTING("prep_counting",'P',"","",false),
	PREP_CUM("prep_cum",'P',"","",false),
	PREP_CLOSE_BY("prep_close_by",'P',"","",false),
	PREP_CLOSE_TO("prep_close_to",'P',"","",false),
	PREP_CONTRARY_TO("prep_contrary_to",'P',"","",false),
	PREP_COMPARED_TO("prep_compared_to",'P',"","",false),
	PREP_COMPARED_WITH("prep_compared_with",'P',"","",false),
	PREP_DESPITE("prep_despite",'P',"","",false),
	PREP_DOWN("prep_down",'P',"","",false),
	PREP_DURING("prep_during",'P',"","",false),
	PREP_DEPENDING_ON("prep_depending_on",'P',"","",false),
	PREP_DUE("prep_due",'P',"","",false),
	PREP_DUE_TO("prep_due_to",'P',"","",false),
	PREP_EXCEPT("prep_except",'P',"","",false),
	PREP_EXCEPTING("prep_excepting",'P',"","",false),
	PREP_EXCLUDING("prep_excluding",'P',"","",false),
	PREP_EXCEPT_FOR("prep_except_for",'P',"","",false),
	PREP_EXCLUSIVE_OF("prep_exclusive_of",'P',"","",false),
	PREP_FOLLOWING("prep_following",'P',"","",false),
	PREP_FROM("prep_from",'P',"","",false),
	PREP_FOR("prep_for",'P',"","",false),
	PREP_FORWARD_OF("prep_forward_of",'P',"","",false),
	PREP_FURTHER_TO("prep_further_to",'P',"","",false),
	PREP_FOLLOWED_BY("prep_followed_by",'P',"","",false),
	PREP_GIVEN("prep_given",'P',"","",false),
	PREP_GONE("prep_gone",'P',"","",false),
	PREP_IDENTIFYING("prep_identifying",'P',"","",false),
	PREP_IF("prep_if",'P',"","",false),
	PREP_IN("prep_in",'P',"","",false),
	PREP_INCLUDING("prep_including",'P',"","",false),
	PREP_INSIDE("prep_inside",'P',"","",false),
	PREP_INTO("prep_into",'P',"","",false),
	PREP_IN_ADDITION_TO("prep_in_addition_to",'P',"","",false),
	PREP_IN_ACCORDANCE_WITH("prep_in_accordance_with",'P',"","",false),
	PREP_IN_PLACE_OF("prep_in_place_of",'P',"","",false),
	PREP_IN_BETWEEN("prep_in_between",'P',"","",false),
	PREP_IN_CASE_OF("prep_in_case_of",'P',"","",false),
	PREP_IN_FACE_OF("prep_in_face_of",'P',"","",false),
	PREP_IN_FAVOR_OF("prep_in_favor_of",'P',"","",false),
	PREP_IN_FRONT_OF("prep_in_front_of",'P',"","",false),
	PREP_IN_LIEU_OF("prep_in_lieu_of",'P',"","",false),
	PREP_INSTEAD_OF("prep_instead_of",'P',"","",false),
	PREP_IN_VIEW_OF("prep_in_view_of",'P',"","",false),
	PREP_IN_SPITE_OF("prep_in_spit_of",'P',"","",false),
	PREP_INSIDE_OF("prep_inside_of",'P',"","",false),
	PREP_INVOLVING("prep_involving",'P',"","",false),
	PREP_IRRESPECTIVE_OF("prep_irrespective_of",'P',"","",false),
	PREP_LESS("prep_less",'P',"","",false),
	PREP_LIKE("prep_like",'P',"","",false),
	PREP_MINUS("prep_minus",'P',"","",false),
	PREP_NEAR("prep_near",'P',"","",false),
	PREP_NOTWITHSTANDING("prep_notwithstanding",'P',"","",false),
	PREP_NEXT_TO("prep_next_to",'P',"","",false),
	PREP_NEAR_TO("prep_near_to",'P',"","",false),
	PREP_OF("prep_of",'P',"","",false),
	PREP_OFF("prep_off",'P',"","",false),
	PREP_OFFERING("prep_offering",'P',"","",false),
	PREP_ON("prep_on",'P',"","",false),
	PREP_ONTO("prep_onto",'P',"","",false),
	PREP_OPPOSITE("prep_opposite",'P',"","",false),
	PREP_OUTSIDE("prep_outside",'P',"","",false),
	PREP_OVER("prep_over",'P',"","",false),
	PREP_ON_ACCOUNT_OF("prep_on_account_of",'P',"","",false),
	PREP_ON_BEHALF_OF("prep_on_behalf_of",'P',"","",false),
	PREP_ON_BOARD("prep_on_board",'P',"","",false),
	PREP_ON_TO("prep_on_to",'P',"","",false),
	PREP_ON_TOP_OF("prep_on_top_of",'P',"","",false),
	PREP_OPPOSITE_TO("prep_opposite_to",'P',"","",false),
	PREP_OTHER_THAN("prep_other_than",'P',"","",false),
	PREP_OUT("prep_out",'P',"","",false),
	PREP_OUT_OF("prep_out_of",'P',"","",false),
	PREP_OUTSIDE_OF("prep_outside_of",'P',"","",false),
	PREP_OWING_TO("prep_owing_to",'P',"","",false),
	PREP_OFF_OF("prep_off_of",'P',"","",false),
	PREP_PAST("prep_past",'P',"","",false),
	PREP_PENDING("prep_pending",'P',"","",false),
	PREP_PER("prep_per",'P',"","",false),
	PREP_PLUS("prep_plus",'P',"","",false),
	PREP_POST("prep_post",'P',"","",false),
	PREP_PRO("prep_pro",'P',"","",false),
	PREP_PREPARATORY_TO("prep_preparatory_to",'P',"","",false),
	PREP_PRIOR_TO("prep_prior_to",'P',"","",false),
	PREP_PREVIOUS_TO("prep_previous_to",'P',"","",false),
	PREP_PROVIDING("prep_providing",'P',"","",false),
	PREP_PURSUANT_TO("prep_pursuant_to",'P',"","",false),
	PREP_QUALIFYING("prep_qualifying",'P',"","",false),
	PREP_RE("prep_re",'P',"","",false),
	PREP_REGARDING("prep_regarding",'P',"","",false),
	PREP_REPRESENTING("prep_representing",'P',"","",false),
	PREP_REQUIRING("prep_requiting",'P',"","",false),
	PREP_RESPECTING("prep_respecting",'P',"","",false),
	PREP_ROUND("prep_round",'P',"","",false),
	PREP_REGARDLESS_OF("prep_regardless_of",'P',"","",false),
	PREP_SAVE("prep_save",'P',"","",false),
	PREP_SAVING("prep_saving",'P',"","",false),
	PREP_SINCE("prep_since",'P',"","",false),
	PREP_SAVE_FOR("prep_save_for",'P',"","",false),
	PREP_SUBSEQUENT_TO("prep_subsequent_to",'P',"","",false),
	PREP_SUCH_AS("prep_such_as",'P',"","",false),
	PREP_TERMINATING("prep_terminating",'P',"","",false),
	PREP_THAN("prep_than",'P',"","",false),
	PREP_THAT("prep_that",'P',"","",false),
	PREP_THROUGH("prep_through",'P',"","",false),
	PREP_THRU("prep_thru",'P',"","",false),
	PREP_THROUGHOUT("prep_throughout",'P',"","",false),
	PREP_TILL("prep_till",'P',"","",false),
	PREP_TO("prep_to",'P',"","",false),
	PREP_TOUCHING("prep_touching",'P',"","",false),
	PREP_TOWARDS("prep_towards",'P',"","",false),
	PREP_TOWARD("prep_toward",'P',"","",false),
	PREP_THANKS_TO("prep_thanks_to",'P',"","",false),
	PREP_TOGETHER_WITH("prep_together_with",'P',"","",false),
	PREP_UNDER("prep_under",'P',"","",false),
	PREP_UNDERNEATH("prep_underneath",'P',"","",false),
	PREP_UNLESS("prep_unless",'P',"","",false),
	PREP_UNLIKE("prep_unlike",'P',"","",false),
	PREP_UNTIL("prep_until",'P',"","",false),
	PREP_UNTILUP("prep_untilup",'P',"","",false),
	PREP_UP("prep_up",'P',"","",false),
	PREP_UPON("prep_upon",'P',"","",false),
	PREP_UP_AGAINST("prep_up_against",'P',"","",false),
	PREP_UP_TO("prep_up_to",'P',"","",false),
	PREP_UP_UNTIL("prep_up_until",'P',"","",false),
	PREP_USING("prep_using",'P',"","",true),
	PREP_VERSUS("prep_versus",'P',"","",false),
	PREP_VIA("prep_via",'P',"","",false),
	PREP_VS("prepc_vs",'P',"","",true),
	PREP_WHEREBY("prep_whereby",'P',"","",false),
	PREP_WHETHER("prep_whether",'P',"","",false),
	PREP_WHILE("prep_while",'P',"","",false),
	PREP_WITH("prep_with",'P',"","",false),
	PREP_WITHIN("prep_within",'P',"","",false),
	PREP_WITHOUT("prep_without",'P',"","",false),
	PREP_WORTH("prep_worth",'P',"","",false),
	PREP_WITH_REFERENCE_TO("prep_with_reference_to",'P',"","",false),
	PREP_WITH_REGARD_TO("prep_with_regard_to",'P',"","",false),
	PREP_WITH_RESPECT_TO("prep_with_respect_to",'P',"","",false),
	PRT("prt",'*',"","",false),
	PUNCT("punct",'.',"","",false),
	PURPCL("purpcl",',',"","",true),
	QUANTMOD("quantmod",':',"","",false),
	RCMOD("rcmod",'-',"","",true),
	REF("ref",'~',"","",false),
	REL("rel",'=',"","",false),
	ROOT("root",'R',"","",false),
	TMOD("tmod",'+',"","",false),
	VMOD("vmod",';',"","",false),
	XCOMP("xcomp",'X',"","",true),
	XSUBJ("xsubj",'x',"","",false);
	
	private static Relationship _collapsibleRelationship[] = { NSUBJ,NSUBJPASS,DOBJ,IOBJ,AGENT,XSUBJ,DEP};
	
	
	String _label;
	char _singleCharLabel;
	String _name;
	String _definition;
	
	boolean _prepostion; // is this relationship a preposition
	boolean _conjunction; // is this relationship a conjunction
	boolean _clausal;     // does this relationship indicate the start of a subclause?
	
	private static java.util.ArrayList<Relationship> _staticSubjectRelationships  = new java.util.ArrayList<Relationship>();
	private static java.util.ArrayList<Relationship> _staticResourceRelationships = new java.util.ArrayList<Relationship>();
	private static java.util.ArrayList<Relationship> _staticEmptyRelationships    = new java.util.ArrayList<Relationship>();
	static {
		_staticSubjectRelationships.add(NSUBJ);
		_staticSubjectRelationships.add(AGENT);
		
		_staticResourceRelationships.add(DOBJ);
		_staticResourceRelationships.add(NSUBJPASS);
		_staticResourceRelationships.add(PREP_TO);
		_staticResourceRelationships.add(PREP_OF);
		_staticResourceRelationships.add(PREP_FROM);
		_staticResourceRelationships.add(PREP_FOR);
		_staticResourceRelationships.add(PREP_BY);
		_staticResourceRelationships.add(PREP_ON);
		_staticResourceRelationships.add(PREP_IN);
		_staticResourceRelationships.add(PREP_ABOUT);		
	}
	
	Relationship(String label) {
		_label=label;
	}
	
	Relationship(String label, char singleCharLabel, String name, String definition, boolean clausal) {
		_label = label;
		_singleCharLabel = singleCharLabel;
		_name  = name;
		_definition = definition;
		_clausal    = clausal;
		
		_prepostion = _label.startsWith("prep");
		_conjunction = _label.startsWith("conj");
	}	
	
	public String toString() {
		return _label;
	}
	
	public String getLabel() {
		return _label;
	}
	
	public char getSingleCharLabel() {
		return _singleCharLabel;
	}
	
	public String getName() {
		return _name;
	}
	public String getDefinition() {
		return _definition;
	}

	public static Relationship retrieve(String tag) {
		tag = tag.toUpperCase();
		if (tag.equals("CONJ_AND\\/OR")) {
			return Relationship.CONJ_AND_OR;
		}
		else if (tag.equals("CONJ_+")){
			return Relationship.CONJ_PLUS;
		}
		else  {
			return Relationship.valueOf(Relationship.class, tag);		
		}
	}
	
	public boolean isClausal() {
		return _clausal;
	}

	public boolean isPreposition() {
		return _prepostion;
	}
	
	public boolean isConjunction() {
		return _conjunction;
	}

	public boolean equalsCollapsed(Relationship r) {
		if (r.isConjunction() && this.isConjunction()) { return true; }
		if (r.isPreposition() && this.isPreposition()) { return true; }
		return this.equals(r);
	}
	
	/** returns true if this relationship indicates a noun phrase that can be collapsed together. */
	public boolean areChildrenCollapsible() {
		if (this.isPreposition()) { return true; }
		
		for (int i=0; i < Relationship._collapsibleRelationship.length; i++) {
			if (_collapsibleRelationship[i].equals(this)) { return true; }
		}
		
		return false;
	}
	
	public static java.util.List<Relationship> getSubjectRelationships() {
		return _staticSubjectRelationships;
	}
	
	public static java.util.List<Relationship>  getResourceRelationships() {
		return _staticResourceRelationships;
	}
	
	public static java.util.List<Relationship>  getEmptyRelationships() {
		return _staticEmptyRelationships;
	}
}

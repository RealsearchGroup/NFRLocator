package edu.ncsu.csc.nl.model.english;


/**
 * Represents a stop word / word to ignore in the list.
 * 
 * From Manning's Introduction to retreival book, direction leans towards less use of stop words
 * rather than more.  The extra storage isn't great deal, plus significant semantic information is 
 * often lost "to be or not to be"
 * 
 * Lists should probably be static, but were made instance level so that they can be altered in a debugger
 * 
 * Glasgow: http://ir.dcs.gla.ac.uk/resources/linguistic_utils/stop_words
 * retreive 1/20/2013
 * 
 * Frakes: Frakes, William B. and Baeza-Yates, Ricardo (Eds.), Information Retrieval: Data Structures and Algorithms, Englewood Cliffs, NJ: Prentice-Hall, 1992
 * 
 * Manning: Manning, C., Raghavan, P., & Schütze, H. (2008). Introduction to Information Retrieval. Cambridge University Press. Retrieved from http://nlp.stanford.edu/IR-book/
 * 
 * USPTO: http://www.uspto.gov/patft/help/stopword.htm
 * retreived 1/31/2013 
 * 
 * @author John Slankas
 */
public class StopWord {
	
	public static final String EMPTY   = "empty";
	public static final String FRAKES  = "frakes";
	public static final String MANNING = "mannin";
	public static final String PROJECT = "project";
	public static final String GLASGOW = "glasgow";
	public static final String USPTO   = "uspto";
	public static final String DETERMINER = "determiner";
	
	public static final String[] AVAILABLE_LISTS = {EMPTY,DETERMINER,FRAKES,GLASGOW,MANNING,PROJECT ,USPTO};
	
	public static final String[] getListByName(String name) {
		StopWord sw = new StopWord();
		
		switch (name) {
			case EMPTY:   return sw.empty;
			case FRAKES:  return sw.frakes;
			case GLASGOW: return sw.glasgow;
			case MANNING: return sw.manningNonSelective;
			case PROJECT: return sw.projectBased;
			case USPTO:   return sw.uspto;
			case DETERMINER:   return sw.determiner;
			default: throw new java.util.NoSuchElementException("No such stopword list: "+name);
		}
	}
	
	public String[] empty = { };
	
	public String[] projectBased = { "a","an","the", "be", "to", "have"};

	public String[] determiner = { "a", "an", "the"};
	
	
	public String[] manningNonSelective = { "a","an","and","are","as","at","be","by","for","from","has","he","in","is","it","its","of","on","that","the","to","was","were","will","with"};
	
	/** Frakes, William B. and Baeza-Yates, Ricardo (Eds.), Information Retrieval: Data Structures and Algorithms, Englewood Cliffs, NJ: Prentice-Hall, 1992 */
	public String[] frakes = {"about","above","across","after","again","against","all","almost","alone","along","already",
			"also","although","always","among","an","and","another","any","anybody","anyone","anything",
			"anywhere","are","area","areas","around","as","ask","asked","asking","asks","at","away",
			"back","backed","backing","backs","be","because","become","becomes","became","been",
			"before","began","behind","being","beings","best","better","between","big","both","but","by",
			"came","can","cannot","case","cases","certain","certainly","clear","clearly","come","could",
			"did","differ","different","differently","do","does","done","down","downed","downing","downs","during",
			"each","early","either","end","ended","ending","ends","enough","even","evenly","ever","every",
			"everybody","everyone","everything","everywhere","face","faces","fact","facts","far","felt","few","find","finds","first","for","four","from","full","fully","further","furthered","furthering","furthers",
			"gave","general","generally","get","gets","give","given","gives","go","going","good",
			"goods","got","great","greater","greatest","group","grouped","grouping","groups",
			"had","has","have","having","he","her","herself","here","high","higher","highest","him",
			"himself","his","how","however",
			"if","important","in","interest","interested","interesting","interests","into","is","it","its","itself",
			"just","keep","keeps","kind","knew","know","known","knows",
			"large","largely","last","later","latest","least","less","let","lets","like","likely","long","longer","longest",
			"made","make","making","man","many","may","me","member","members","men","might","more","most",
			"mostly","mr","mrs","much","must","my","myself",
			"necessary","need","needed","needing","needs","never","new","newer","newest","next","no","non",
			"not","nobody","noone","nothing","now","nowhere","number","numbered","numbering","numbers",
			"of","off","often","old","older","oldest","on","once","one","only","open","opened","opening",
			"opens","or","order","ordered","ordering","orders","other","others","our","out","over",
			"part","parted","parting","parts","per","perhaps","place","places","point","pointed","pointing",
			"points","possible","present","presented","presenting","presents","problem","problems","put","puts",
			"quite","r","rather","really","right","room","rooms","said","same","saw","say","says",
			"second","seconds","see","sees","seem","seemed","seeming","seems","several","shall","she",
			"should","show","showed","showing","shows","side","sides","since","small","smaller","smallest",
			"so","some","somebody","someone","something","somewhere","state","states","still","such","sure",
			"take","taken","than","that","the","their","them","then","there","therefore","these","they",
			"thing","things","think","thinks","this","those","though","thought","thoughts","three",
			"through","thus","to","today","together","too","took","toward","turn","turned","turning","turns","two",
			"under","until","up","upon","us","use","uses","used",
			"very","want","wanted","wanting","wants","was","way","ways","we","well","wells","went",
			"were","what","when","where","whether","which","while","who","whole","whose","why","will",
			"with","within","without","work","worked","working","works","would",
			"year","years","yet","you","young","younger","youngest","your","yours"};
	
	public String[] glasgow = {"a","about","above","across","after","afterwards","again","against","all","almost",
			"alone","along","already","also","although","always","am","among","amongst","amoungst",
			"amount","an","and","another","any","anyhow","anyone","anything","anyway","anywhere",
			"are","around","as","at","back","be","became","because","become","becomes","becoming",
			"been","before","beforehand","behind","being","below","beside","besides","between",
			"beyond","bill","both","bottom","but","by","call","can","cannot","cant","co",
			"computer","con","could","couldnt","cry","de","describe","detail","do","done",
			"down","due","during","each","eg","eight","either","eleven","else","elsewhere",
			"empty","enough","etc","even","ever","every","everyone","everything","everywhere",
			"except","few","fifteen","fify","fill","find","fire","first","five","for","former",
			"formerly","forty","found","four","from","front","full","further","get","give","go"
			,"had","has","hasnt","have","he","hence","her","here","hereafter","hereby","herein",
			"hereupon","hers","herself","him","himself","his","how","however","hundred","i","ie",
			"if","in","inc","indeed","interest","into","is","it","its","itself","keep","last","latter",
			"latterly","least","less","ltd","made","many","may","me","meanwhile","might","mill","mine",
			"more","moreover","most","mostly","move","much","must","my","myself","name","namely","neither",
			"never","nevertheless","next","nine","no","nobody","none","noone","nor","not","nothing","now",
			"nowhere","of","off","often","on","once","one","only","onto","or","other","others","otherwise",
			"our","ours","ourselves","out","over","own","part","per","perhaps","please","put","rather","re",
			"same","see","seem","seemed","seeming","seems","serious","several","she","should","show","side",
			"since","sincere","six","sixty","so","some","somehow","someone","something","sometime","sometimes"
			,"somewhere","still","such","system","take","ten","than","that","the","their","them","themselves",
			"then","thence","there","thereafter","thereby","therefore","therein","thereupon","these","they",
			"thick","thin","third","this","those","though","three","through","throughout","thru","thus","to",
			"together","too","top","toward","towards","twelve","twenty","two","un","under","until","up","upon",
			"us","very","via","was","we","well","were","what","whatever","when","whence","whenever","where",
			"whereafter","whereas","whereby","wherein","whereupon","wherever","whether","which","while",
			"whither","who","whoever","whole","whom","whose","why","will","with","within","without",
			"would","yet","you","your","yours","yourself","yourselves"};
	
	public String[] uspto = { "a","accordance","according","all","also","an","and","another","are","as",
		"at","be","because","been","being","by","claim","comprises","corresponding",
		"could","described","desired","do","does","each","embodiment","fig","figs",
		"for","from","further","generally","had","has","have","having","herein",
		"however","if","in","into","invention","is","it","its","means","not","now",
		"of","on","onto","or","other","particularly","preferably","preferred","present",
		"provide","provided","provides","relatively","respectively","said","should",
		"since","some","such","suitable","than","that","the","their","then","there",
		"thereby","therefore","thereof","thereto","these","they","this","those",
		"thus","to","use","various","was","were","what","when","where","whereby",
		"wherein","which","while","who","will","with","would" };
}

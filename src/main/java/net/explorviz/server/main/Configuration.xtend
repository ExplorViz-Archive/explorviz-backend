package net.explorviz.server.main;

import java.util.ArrayList
import java.util.Arrays

class Configuration {
	public static var selectedLanguage = "english"
	public static var languages = new ArrayList<String>(Arrays.asList("english", "german"));
	public static var secondLandscape = false
	public static var long tutorialStart = System.currentTimeMillis();
	public static var long secondLandscapeTime = System.currentTimeMillis();
	
	
//	public static var tutorialSteps = new ArrayList<Step>(
//		Arrays.asList(new Step(""), //0 text
//					  new Step("OCN Editor", true, false, false, false), //1 close system
//					  new Step("OCN Editor", true, false, false, false), //2 open system
//					  new Step(""), //3 text
//					  new Step("10.0.0.1 - 10.0.0.2", true, false, false, false), //4 open nodegroup
//					  new Step("10.0.0.1 - 10.0.0.2", true, false, false, false), //5 close nodegroup
//					  new Step("Neo4j", true, false, false, false), //6 enter application
//					  new Step(""), //7 text
//					  new Step("kernel", true, false, false, false), // 8 open package
//					  new Step("main", false,false,false,true), //9 hover over package
//					  new Step("main",true,false,false,false), //10 enter package
//					  new Step("Main", false, false,true, false), //11 click class
//					  new Step(""), // 12 text: arrowcolours			  
//					  new Step("Main","configuration",true,false), //13 hover over communication
//					  new Step("configuration",true,false,false,false), //	14 open config				  
//					  new Step("ConfigReader", false, false, true, false), // 15 click class 
//					  new Step("FileUtils", false, false,true, false), //16 click class
//					  new Step("TransactionImpl", false, false,true, false), //17 click class
//					  new Step(""), //18 text
//					  new Step("landscape"), //19 go back to landscape
//					  new Step("") //20 end text
//		)
//	);
//	
	
// Experiment-Tutorial	
//	public static var tutorialSteps = new ArrayList<Step>(
//		Arrays.asList(new Step(""), //0 text
//					  new Step("Neo4j", true, false, false, false), //1 enter application
//					  new Step(""), //2 text
//					  new Step("kernel", true, false, false, false), // 3 open package
//					  new Step("main", false,false,false,true), //4 hover over package
//					  new Step("main",true,false,false,false), //5 enter package
//					  new Step("Main", false, false,true, false), //6 click class
//					  new Step(""), // 7 text: arrowcolours			  
//					  new Step("Main","configuration",true,false), //8 hover over communication
//					  new Step("configuration",true,false,false,false), //					  
//					  new Step("ConfigReader", false, false, true, false), // 10 click component 
//					  new Step("FileUtils", false, false,true, false), //11 click class
//					  new Step("TransactionImpl", false, false,true, false), //12 click class
//					  new Step(""), //13 text
//					  new Step("kernel", true, false, false, false), //14 close package
//					  new Step("") //15 text
//		)
//	);
	
//mit codeviewer und timeshift
//	public static var tutorialSteps = new ArrayList<Step>(
//		Arrays.asList(new Step(""), //0 text
//					  new Step("OCN Editor", true, false, false, false), //1 close system
//					  new Step("OCN Editor", true, false, false, false), //2 open system
//					  new Step(""), //3 text
//					  new Step("10.0.0.1 - 10.0.0.2", true, false, false, false), //4 open nodegroup
//					  new Step("10.0.0.1 - 10.0.0.2", true, false, false, false), //5 close nodegroup
//					  new Step("Neo4j", true, false, false, false), //6 enter application
//					  new Step(""), //7 text
//					  new Step("kernel", true, false, false, false), // 8 open package
//					  new Step("TransactionImpl", false,false,false,true), //9 hover over class
//					  new Step("SystemUtils", false, true, false, false),  //10 context men�
//					  new Step("codeview"), //11 codeview systemUtils
//					  new Step(""), //12 close codeviewer
//					  new Step("lifecycle", false, false, true, false), // 14 click component
//					  new Step("FileUtils", false, false,true, false), //13 click class
//					  new Step("FileUtils","TransactionImpl", false, true), //15 click communication
//					  new Step("choosetrace"), //16 choose trace dialog description
//					  new Step("startanalysis"), // 17 replayer description
//					  new Step("pauseanalysis"), //18
//					  new Step("nextanalysis"), //19
//					  new Step("leaveanalysis"), //20
//					  new Step("kernel", true, false, false, false), //21 close package
//					  new Step("landscape"), //22 go back to landscape
//					  new Step("timeshift"), //23 use timeshift
//					  new Step("") //24 text
//		)
//	);
	
	public static var experiment = false
	public static var boolean skipQuestion = false
	
	public static var rsfExportEnabled = false
	public static var outputIntervalSeconds = 10
	public val static databaseNames = new ArrayList<String>()
	
	public val static int TIMESHIFT_INTERVAL_IN_MINUTES = 10
	public val static MODEL_EXTENSION = ".expl"
	public val static DUMMYMODE = true
}
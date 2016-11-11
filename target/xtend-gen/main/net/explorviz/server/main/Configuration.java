package net.explorviz.server.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("all")
public class Configuration {
  public static String selectedLanguage = "english";
  
  public static ArrayList<String> languages = new ArrayList<String>(Arrays.<String>asList("english", "german"));
  
  public static boolean secondLandscape = false;
  
  public static long tutorialStart = System.currentTimeMillis();
  
  public static long secondLandscapeTime = System.currentTimeMillis();
  
  public static boolean experiment = false;
  
  public static boolean skipQuestion = false;
  
  public static boolean rsfExportEnabled = false;
  
  public static int outputIntervalSeconds = 10;
  
  public final static List<String> databaseNames = new ArrayList<String>();
  
  public static int TIMESHIFT_INTERVAL_IN_MINUTES = 10;
  
  public final static String MODEL_EXTENSION = ".expl";
}

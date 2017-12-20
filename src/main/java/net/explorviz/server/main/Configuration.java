package net.explorviz.server.main;

import java.util.ArrayList;
import java.util.Arrays;

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
	public static final ArrayList<String> DATABASE_NAMES = new ArrayList<String>();
	public static final int TIMESHIFT_INTERVAL_IN_MINUTES = 10;
	public static final int HISTORY_INTERVAL_IN_MINUTES = 24 * 60; // one day
	public static final String MODEL_EXTENSION = ".expl";
	public static final boolean DUMMYMODE = false;

	public static final boolean ENABLE_KIEKER_ADAPTER = true;
}

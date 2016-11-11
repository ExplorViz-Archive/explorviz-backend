package net.explorviz.server.main;

import java.io.File;

public class FileSystemHelper {
	public static String getExplorVizDirectory() {

		final String homefolder = System.getProperty("user.home");
		final String filePath = homefolder + "/.explorviz";

		new File(filePath).mkdir();

		return filePath;
	}
}

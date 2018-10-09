package net.explorviz.landscape.server.helper;

import java.io.File;

/**
 * Helper class for generic server purposes.
 */
public final class FileSystemHelper {

  private FileSystemHelper() {
    // Utility Class
  }

  /**
   * Retrieves the current ExplorViz directory for the running system user.
   *
   * @return ExplorVizDirectoryPath
   */
  public static String getExplorVizDirectory() {
    final String homefolder = System.getProperty("user.home");
    final String filePath = homefolder + "/.explorviz";

    // create folder, if it not already exists
    if (!new File(filePath).exists()) {
      new File(filePath).mkdir();
    }
    return filePath;
  }
}

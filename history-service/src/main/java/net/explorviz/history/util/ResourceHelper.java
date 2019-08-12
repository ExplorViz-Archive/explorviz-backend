package net.explorviz.history.util;

public class ResourceHelper {

  /**
   * Remove the extension of a passed filename
   * 
   * @param fileName the fileName
   * @return the trimmed fileName
   */
  public static String removeFileNameExtension(final String fileName) {
    int extPos = fileName.lastIndexOf(".");
    if (extPos == -1) {
      return fileName;
    } else {
      return fileName.substring(0, extPos);
    }
  }

}

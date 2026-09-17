package com.takypok.mediaservice.util;

public class FileUtil {
  public static String getFileExtension(String fileName) {
    if (fileName == null || fileName.isEmpty()) {
      return ""; // Handle null or empty file names
    }

    int dotIndex = fileName.lastIndexOf('.');

    // Check if a dot exists and is not the last character
    if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
      return ""; // No extension or dot is the last character
    }

    return fileName.substring(dotIndex);
  }
}

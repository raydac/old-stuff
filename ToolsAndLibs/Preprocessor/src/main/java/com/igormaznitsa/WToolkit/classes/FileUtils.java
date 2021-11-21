package com.igormaznitsa.WToolkit.classes;

import java.io.*;

public class FileUtils {

  public static boolean deleteDir(File dir) {
    try {
      if (deleteDirRecursive(dir)) {
        if (dir.isDirectory()) return dir.delete();
        return true;
      } else {
        return false;
      }
    } catch (SecurityException securityexception) {
      securityexception.printStackTrace();
      return false;
    }
  }

  public static boolean renameFolder(File folder, String newName) {
    if (folder.isDirectory()) {
      return folder.renameTo(new File(folder.getParentFile(), newName));
    } else {
      return false;
    }
  }

  private static boolean deleteDirRecursive(File file) {
    if (!file.exists()) return true;

    if (file.isDirectory()) {
      File[] files = file.listFiles();
      for (int i = 0; i < files.length; i++) {
        File target = files[i];
        if (target.isDirectory()) {
          if (!deleteDirRecursive(target)) {
            return false;
          }
        }
        if (!target.delete()) return false;
      }
      return true;
    } else {
      return !file.isDirectory() || file.delete();
    }
  }

  public static boolean clearDirectory(File file) {
    if (file.isDirectory()) {
      String as[] = file.list();
      for (int i = 0; i < as.length; i++) {
        File file1 = new File(file, as[i]);
        if (file1.isDirectory()) {
          if (!clearDirectory(file1)) return false;
          if (!file1.delete()) return false;
        } else {
          if (!file1.delete()) return false;
        }
      }
      return true;
    }
    return false;
  }

  public static void copyDir(File src, File target) throws IOException {
    if (!src.isDirectory()) throw new IOException("Can't find " + src);
    if (!target.isDirectory() && !target.mkdirs()) {
      throw new IOException("Can't find target dir " + target);
    }
    final File[] files = src.listFiles();
    for (int i = 0; i < files.length; i++) {
      final File srcFile = files[i];
      if (srcFile.isDirectory()) {
        copyDir(srcFile, new File(target, srcFile.getName()));
      } else {
        copyFile(srcFile, new File(target, srcFile.getName()));
      }
    }
  }

  public static void copyFile(File _src, File _dest) throws IOException {
    FileInputStream fileinputstream = null;
    FileOutputStream fileoutputstream = null;
    try {
      byte abyte0[] = new byte[1024];
      fileinputstream = new FileInputStream(_src);
      fileoutputstream = new FileOutputStream(_dest);
      int i;
      while ((i = fileinputstream.read(abyte0)) > -1)
        fileoutputstream.write(abyte0, 0, i);
    } catch (FileNotFoundException filenotfoundexception) {
      throw new IOException("File Not Found: " + filenotfoundexception);
    } finally {
      if (fileinputstream != null) fileinputstream.close();
      if (fileoutputstream != null) fileoutputstream.close();
    }
  }
}

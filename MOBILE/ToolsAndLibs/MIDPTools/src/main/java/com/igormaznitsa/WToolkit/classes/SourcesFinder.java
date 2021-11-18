package com.igormaznitsa.WToolkit.classes;

import java.io.FilenameFilter;
import java.io.File;
import java.util.ArrayList;

public class SourcesFinder
{
    private static boolean endsWithIgnoreCase(String s, String s1)
    {
        int i = s1.length();
        return s.regionMatches(true, s.length() - i, s1, 0, i);
    }

    private static void fillListWithFilenames(File _file,ArrayList _filelist,FilenameFilter _filter)
    {
        if (!_file.exists()) return;
        if (!_file.isDirectory()) return;
        File [] p_fileList = _file.listFiles();
        for(int li=0;li<p_fileList.length;li++)
        {
            File p_file = p_fileList[li];
            if (p_file.isDirectory())
            {
                fillListWithFilenames(p_file,_filelist,_filter);
            }
            else
            {
                if(_filter.accept(p_file, p_file.getName())) _filelist.add(p_file);
            }
        }
    }

    private static String[] toStringArray(File afile[])
    {
        if(afile == null) return null;
        String as[] = new String[afile.length];
        for(int i = 0; i < afile.length; i++)
            as[i] = afile[i].getAbsolutePath();
         return as;
    }

    public static String[] sourceList(File _srcdir)
    {
        ArrayList p_list = new ArrayList();
        fillListWithFilenames(_srcdir,p_list,sourceFilter);
        return toStringArray((File[])p_list.toArray(new File[0]));
    }

    public static File[] classFileList(File _srcdir)
    {
        ArrayList p_list = new ArrayList();
        fillListWithFilenames(_srcdir,p_list,classFilter);
        return (File[])p_list.toArray(new File[0]);
    }

    public static File[] sourceFileList(File _srcdir)
    {
        ArrayList p_list = new ArrayList();
        fillListWithFilenames(_srcdir,p_list,sourceFilter);
        return (File[])p_list.toArray(new File[0]);
    }

    public static String[] libList(File _srcdir)
    {
        ArrayList p_list = new ArrayList();
        fillListWithFilenames(_srcdir,p_list,libFilter);
        return toStringArray((File[])p_list.toArray(new File[0]));
    }


    private static FilenameFilter sourceFilter = new FilenameFilter() {

        public boolean accept(File file, String s)
        {
            boolean flag = SourcesFinder.fileFilter.accept(file, s) && SourcesFinder.endsWithIgnoreCase(s, ".java");
            return flag;
        }

    };

    private static FilenameFilter classFilter = new FilenameFilter() {

        public boolean accept(File file, String s)
        {
            boolean flag = SourcesFinder.fileFilter.accept(file, s) && SourcesFinder.endsWithIgnoreCase(s, ".class");
            return flag;
        }

    };

    private static FilenameFilter sccsFilter = new FilenameFilter() {

        public boolean accept(File file, String s)
        {
            return SourcesFinder.fileFilter.accept(file, s) && file.getName().equals("SCCS");
        }

    };
    private static FilenameFilter rcsFilter = new FilenameFilter() {

        public boolean accept(File file, String s)
        {
            return SourcesFinder.fileFilter.accept(file, s) && file.getName().equals("RCS");
        }

    };
    private static FilenameFilter cvsFilter = new FilenameFilter() {

        public boolean accept(File file, String s)
        {
            return SourcesFinder.fileFilter.accept(file, s) && file.getName().equals("CVS");
        }

    };
    private static FilenameFilter fileFilter = new FilenameFilter() {

        public boolean accept(File file, String s)
        {
            return file.isFile();
        }

    };
    private static FilenameFilter dirFilter = new FilenameFilter() {

        public boolean accept(File file, String s)
        {
            return file.isDirectory();
        }

    };
    private static FilenameFilter libFilter = new FilenameFilter() {

        public boolean accept(File file, String s)
        {
            return SourcesFinder.fileFilter.accept(file, s) && !SourcesFinder.sccsFilter.accept(file, s) && (SourcesFinder.endsWithIgnoreCase(s, ".jar") || SourcesFinder.endsWithIgnoreCase(s, ".zip"));
        }

    };




}

package com.igormaznitsa.Preprocessor;

import java.io.File;

public class FileReference
{
        public File p_srcFile;
        public boolean lg_excluded;

        public boolean lg_copyOnly;

        public String s_destDir;
        public String s_destName;

        public FileReference(File _file, String _destFileName, boolean _copyOnly)
        {
            lg_copyOnly = _copyOnly;
            lg_excluded = false;
            p_srcFile = _file;

            int i_index = _destFileName.lastIndexOf('/');
            if (i_index<0) i_index = _destFileName.lastIndexOf('\\');

            if (i_index<0)
            {
                s_destDir = "./";
                s_destName = _destFileName;
            }
            else
            {
                s_destDir = _destFileName.substring(0,i_index);
                s_destName = _destFileName.substring(i_index);
            }
        }

    public String getDestFileName()
    {
        String s_string = s_destDir +File.separatorChar+s_destName;
        return s_string;
    }


    public String toString()
    {
        return p_srcFile.getAbsolutePath();
    }
}

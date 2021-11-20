package com.igormaznitsa.Preprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.util.Vector;

public class IncludeReference
{
        Vector p_stringsArray;
        int i_stringCounter;
        String s_FileName;
        File p_File;

        public IncludeReference(File _curFile,Vector _stringsArray, int _stringCounter,String _fileName)
        {
            p_File = _curFile;
            s_FileName = _fileName;
            p_stringsArray = _stringsArray;
            i_stringCounter = _stringCounter;
        }
}

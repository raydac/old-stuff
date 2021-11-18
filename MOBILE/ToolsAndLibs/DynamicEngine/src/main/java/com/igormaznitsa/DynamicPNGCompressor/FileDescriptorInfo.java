package com.igormaznitsa.DynamicPNGCompressor;

import java.io.File;

class FileDescriptorInfo
{
    String s_filename;
    int i_fileindex;
    int i_compresslevel;
    File p_file;
    int i_linkindex;
    boolean lg_commentedFile;

    public FileDescriptorInfo(String _name, int _index, int _level,int _link,boolean _commented)
    {
        s_filename = _name;
        i_fileindex = _index;
        i_compresslevel = _level;
        i_linkindex = _link;
        p_file = null;
        lg_commentedFile = _commented;
    }

    public int hashCode()
    {
        return s_filename.hashCode();
    }
}

package com.igormaznitsa.midp;

import com.igormaznitsa.GameAPI.LoadListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

/**
 * Class implements the text block which possible to load from a file
 */
public class TextBlock
{
    private String [] as_str_array;

    /**
     * Constructor
     * @param _resourcename Name of file contains text resources
     * @param _loadlistener Pointer to a load listener
     * @throws IOException
     */
    public TextBlock(String _resourcename,LoadListener _loadlistener) throws IOException
    {
        changeResource(_resourcename,_loadlistener);
    }

    /**
     * This function loads texts from a text resource file
     * @param _resourcename Name of file contains text resources
     * @param _loadlistener Pointer to a load listener
     * @throws IOException
     */
    public void changeResource(String _resourcename,LoadListener _loadlistener) throws IOException
    {
        Runtime.getRuntime().gc();

        InputStream is = getClass().getResourceAsStream(_resourcename);
        DataInputStream ds = new DataInputStream(is);
        int i_num = ds.readUnsignedByte();
        String [] as_new_array = new String[i_num];

        for(int li=0;li<i_num;li++)
        {
            as_new_array[li] = ds.readUTF();
            if (as_new_array[li].length()==0) as_new_array[li] = null;
            else
            as_new_array[li] = as_new_array[li].replace('~','\n');
            Runtime.getRuntime().gc();
	    if(_loadlistener!=null)_loadlistener.nextItemLoaded(1);
        }
        as_str_array = null;
        as_str_array = as_new_array;
        ds.close();
        ds = null;
        is = null;
        Runtime.getRuntime().gc();
    }

    /**
     * This function returns the array contains text strings
     * @return string array
     */
    public String [] getStringArray()
    {
        return as_str_array;
    }
}

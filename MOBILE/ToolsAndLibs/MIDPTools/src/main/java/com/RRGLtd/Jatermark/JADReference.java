package com.RRGLtd.Jatermark;

import java.util.*;
import java.io.*;

public class JADReference
{
    private Properties p_Properties;
    private Vector p_KeySet;

    public JADReference(byte[] _jadFile) throws IOException
    {
        p_Properties = new Properties();
        p_KeySet = new Vector();

        // Reading JAD file
        BufferedReader p_reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(_jadFile)));

        p_Properties = new Properties();
        while (true)
        {
            String s_str = p_reader.readLine();
            if (s_str == null) break;
            StringTokenizer p_st = new StringTokenizer(s_str);
            try
            {
                String s_Key = p_st.nextToken(":").trim();
                String s_Value = p_st.nextToken().trim();
                p_Properties.setProperty(s_Key,s_Value);
                p_KeySet.add(s_Key);
            }
            catch (NoSuchElementException e)
            {
                throw new IOException("Error parameter in the JAD file");
            }
        }
        p_reader.close();
    }

    public void changeJARName(String _newName)
    {
        p_Properties.setProperty("MIDlet-Jar-URL",_newName);
    }

    public void AddValue(String _key,String _value)
    {
        p_Properties.setProperty(_key,_value);
        if (!p_KeySet.contains(_key))
        {
            p_KeySet.add(_key);
        }
    }

    public void setNewSizeValue(int _newSize)
    {
        p_Properties.setProperty("MIDlet-Jar-Size", Integer.toString(_newSize));
    }

    public byte[] getJADFile() throws IOException
    {
        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(1024);
        Iterator p_keys = p_KeySet.iterator();
        boolean lg_second = false;
        while (p_keys.hasNext())
        {
            if (lg_second)
            {
                p_baos.write('\r');
                p_baos.write('\n');
            }
            else
            {
                lg_second = true;
            }

            String s_key = (String) p_keys.next();
            String s_value = p_Properties.getProperty(s_key);
            p_baos.write(s_key.getBytes());
            p_baos.write(':');
            p_baos.write(' ');
            p_baos.write(s_value.getBytes());
        }

        return p_baos.toByteArray();
    }
}

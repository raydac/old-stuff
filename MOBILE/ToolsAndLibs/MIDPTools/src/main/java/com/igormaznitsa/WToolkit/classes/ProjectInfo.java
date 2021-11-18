package com.igormaznitsa.WToolkit.classes;

import com.igormaznitsa.WToolkit.classes.Panel;

import java.io.*;
import java.util.*;

public class ProjectInfo
{
        File p_Directory;
        private Properties p_Properties;
        String s_ViewName;
        File p_JADFile,p_ManifFile;
        boolean lg_notSaved;
        HashSet p_mainClasses;

        private String getStartClassName(String _string)
        {
            int i_indx = _string.lastIndexOf(',');
            String s_str = _string.substring(i_indx+1).trim();
            s_str = s_str.replace('.','/');
            return s_str;
        }

        public void saveParameters() throws IOException
        {
            HashSet p_hashset = new HashSet();

            if (p_ManifFile.exists())
            {
                if (!p_ManifFile.delete()) throw new IOException("I can't delete Manifest file");
            }

            if (p_JADFile.exists())
            {
                if (!p_JADFile.delete()) throw new IOException("I can't delete JAD file");
            }

            // Writing Manifest
            DataOutputStream p_dos = new DataOutputStream(new FileOutputStream(p_ManifFile));

            for(int li=0;li<Panel.as_ManifValues.length;li++)
            {
                String s_str = Panel.as_ManifValues[li];
                String s_value = p_Properties.getProperty(s_str);
                if (s_value!=null)
                {
                    p_dos.writeBytes(s_str+": "+s_value+"\r\n");
                }

                p_hashset.add(s_str);
            }

            Enumeration p_enum = p_Properties.keys();
            while(p_enum.hasMoreElements())
            {
                String s_key = (String)p_enum.nextElement();
                if (s_key.startsWith("MIDlet-"))
                {
                    String s_num = s_key.substring(7);
                    try
                    {
                        int i_num = Integer.parseInt(s_num);
                    }
                    catch (NumberFormatException e)
                    {
                        continue;
                    }
                    String s_value = p_Properties.getProperty(s_key);
                    if (s_value!=null)
                    {
                        p_dos.writeBytes(s_key+": "+s_value+"\r\n");
                    }
                    p_hashset.add(s_key);
                }
            }
            p_dos.flush();
            p_dos.close();

            // Writing JAD
            p_dos = new DataOutputStream(new FileOutputStream(p_JADFile));

            for(int li=0;li<Panel.as_JADvalues.length;li++)
            {
                String s_str = Panel.as_JADvalues[li];
                String s_value = p_Properties.getProperty(s_str);
                if (s_value!=null)
                {
                    p_dos.writeBytes(s_str+": "+s_value+"\r\n");
                }
                p_hashset.add(s_str);
            }

            p_enum = p_Properties.keys();
            while(p_enum.hasMoreElements())
            {
                String s_key = (String)p_enum.nextElement();
                if (s_key.startsWith("MIDlet-"))
                {
                    String s_num = s_key.substring(7);
                    try
                    {
                        int i_num = Integer.parseInt(s_num);
                    }
                    catch (NumberFormatException e)
                    {
                        continue;
                    }
                    String s_value = p_Properties.getProperty(s_key);
                    if (s_value!=null)
                    {
                        p_dos.writeBytes(s_key+": "+s_value+"\r\n");
                    }
                }
            }

            p_enum = p_Properties.keys();
            while(p_enum.hasMoreElements())
            {
                String s_key = (String)p_enum.nextElement();
                if (p_hashset.contains(s_key)) continue;
                String s_value = p_Properties.getProperty(s_key);
                if (s_value!=null) p_dos.writeBytes(s_key+": "+s_value+"\r\n");
            }
            p_dos.flush();
            p_dos.close();
            lg_notSaved = false;
        }

        public boolean isModified()
        {
            return lg_notSaved;
        }

        public ProjectInfo(File _directory,String _newname,String _mainclass)
        {
            lg_notSaved = true;
            p_JADFile = new File(_directory, "/bin/" + _directory.getName() + ".jad");
            p_ManifFile = new File(_directory, "/bin/Manifest.mf");

            p_Directory = _directory;
            p_Properties = new Properties();
            p_mainClasses = new HashSet();

            p_Properties.setProperty("MIDlet-Name",_newname);
            p_Properties.setProperty("MIDlet-Jar-URL",_newname+".jar");
            p_Properties.setProperty("MIDlet-Jar-Size","0");
            p_Properties.setProperty("MIDlet-Version","1.0");
            p_Properties.setProperty("MIDlet-1",_newname+", ,"+_mainclass);
            p_Properties.setProperty("MIDlet-Vendor","Igor A. Maznitsa");
            p_Properties.setProperty("MicroEdition-Configuration","CLDC-1.0");
            p_Properties.setProperty("MicroEdition-Profile","MIDP-1.0");

            s_ViewName = p_Properties.getProperty("MIDlet-Name") + " [" + _directory.getName() + "]";

            updateNamesOfMidlets();
        }

        public ProjectInfo(File _directory) throws IOException
        {
            p_mainClasses = new HashSet();
            lg_notSaved = false;
            p_JADFile = new File(_directory, "/bin/" + _directory.getName() + ".jad");
            p_ManifFile = new File(_directory, "/bin/Manifest.mf");
            p_Directory= _directory;
            if (!p_JADFile.exists() || !p_ManifFile.exists()) throw new IOException(_directory.getName()+" is not project");
            if (!p_JADFile.isFile() || !p_ManifFile.isFile()) throw new IOException(_directory.getName()+" is not project");

            // Reading JAD file
            BufferedReader p_reader = new BufferedReader(new InputStreamReader(new FileInputStream(p_JADFile)));

            p_Properties = new Properties();
            while (true)
            {
                String s_str = p_reader.readLine();
                if (s_str == null) break;
                StringTokenizer p_st = new StringTokenizer(s_str);
                try
                {
                    p_Properties.setProperty(p_st.nextToken(":").trim(), p_st.nextToken().trim());
                }
                catch (NoSuchElementException e)
                {
                    throw new IOException("Error parameter in the JAD file ["+p_ManifFile.getAbsolutePath()+"]");
                }
            }
            p_reader.close();

            // Reading Manifest file
            p_reader = new BufferedReader(new InputStreamReader(new FileInputStream(p_ManifFile)));

            while (true)
            {
                String s_str = p_reader.readLine();
                if (s_str == null) break;
                StringTokenizer p_st = new StringTokenizer(s_str);
                try
                {
                    p_Properties.setProperty(p_st.nextToken(":").trim(), p_st.nextToken().trim());
                }
                catch (NoSuchElementException e)
                {
                    throw new IOException("Error parameter in the Manifest file ["+p_ManifFile.getAbsolutePath()+"]");
                }
            }
            p_reader.close();

            s_ViewName = p_Properties.getProperty("MIDlet-Name") + " [" + _directory.getName() + "]";

            updateNamesOfMidlets();
        }

        public void setNewProperties(Properties _prop)
        {
            p_Properties = _prop;
            lg_notSaved = true;
        }

        public Properties getProperties()
        {
            return p_Properties;
        }

        public String toString()
        {
            return s_ViewName;
        }

        public void updateNamesOfMidlets()
        {
            Enumeration p_enum = p_Properties.keys();
            p_mainClasses.clear();
            while(p_enum.hasMoreElements())
            {
                String s_key = (String)p_enum.nextElement();
                if (s_key.startsWith("MIDlet-"))
                {
                    String s_num = s_key.substring(7);
                    try
                    {
                        int i_num = Integer.parseInt(s_num);
                    }
                    catch (NumberFormatException e)
                    {
                        continue;
                    }
                    String s_value = p_Properties.getProperty(s_key);
                    p_mainClasses.add(getStartClassName(s_value));
                }
            }
        }
}

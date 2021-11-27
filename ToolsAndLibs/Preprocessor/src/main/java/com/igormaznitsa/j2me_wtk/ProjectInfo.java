package com.igormaznitsa.j2me_wtk;

import java.io.*;
import java.util.*;

public class ProjectInfo
{
    public static final String CB_TAG = "Created-By";
    public  static final String MIDLET_VERSION = "MIDlet-Version";
    public  static final String MIDLET_NAME = "MIDlet-Name";
    public  static final String MIDLET_MIDP_PROFILE = "MicroEdition-Profile";
    public  static final String MIDLET_CLDC_PROFILE = "MicroEdition-Configuration";
    public  static final String MIDLET_JAR_URL = "MIDlet-Jar-URL";
    public  static final String MIDLET_JAR_SIZE = "MIDlet-Jar-Size";
    public  static final String MIDLET_DESCRIPTION = "MIDlet-Description";

    private static final String SECTION_PARAMETERS = "[SECTION_PARAMETERS]";
    private static final String SECTION_ORDER_JAD = "[SECTION_ORDER_JAD]";
    private static final String SECTION_ORDER_MF = "[SECTION_ORDER_MF]";
    private static final String SECTION_END = "[END]";

    private static final String [] EXCLUDE_FROM_MF = new String[]{MIDLET_JAR_URL,MIDLET_JAR_SIZE};

    private File p_Directory;
    private String s_ViewName;
    private String s_JDDName;
    public File p_JDDFile;
    private HashSet p_mainClasses;
    private Properties p_Properties;
    private boolean lg_saved;
    private Properties p_customVariabes;
    protected Vector p_propertiesNames;
    protected Vector p_cvarNames;

    protected Vector p_NamesOrderJAD;
    protected Vector p_NamesOrderMF;
    protected Vector p_ValuesForReplacing;

    public static final int MIDP_UNKNOWN = 0;
    public static final int CLDC_UNKNOWN = 0;
    public static final int MIDP_10 = 1;
    public static final int MIDP_20 = 2;
    public static final int CLDC10 = 1;
    public static final int CLDC11 = 2;

    private int i_MIDP_version;
    private int i_CLDC_version;

    public int getMIDPVersion()
    {
        return i_MIDP_version;
    }

    public File getJDDFile()
    {
        return p_JDDFile;
    }

    public int getCLDCVersion()
    {
        return i_CLDC_version;
    }

    private String getStartClassName(String _string)
    {
        int i_indx = _string.lastIndexOf(',');
        String s_str = _string.substring(i_indx + 1).trim();
        s_str = s_str.replace('.', '/');
        return s_str;
    }

    public boolean isSaved()
    {
        return lg_saved;
    }

    public String getJDDName()
    {
        return s_JDDName;
    }

    public File getDirectory()
    {
        return p_Directory;
    }

    private static final boolean excludeForMF(String _parameter)
    {
        for(int li=0;li<EXCLUDE_FROM_MF.length;li++)
        {
            String s_str = EXCLUDE_FROM_MF[li];
            if (_parameter.equals(s_str)) return true;
        }
        return false;
    }

    public void generateManifestFile(File _mfFile) throws IOException
    {
        FileOutputStream p_fos = new FileOutputStream(_mfFile);
        PrintStream p_stream = new PrintStream(p_fos);

        // Добалвяем метку приложения которым создано
        String s_value = p_Properties.getProperty(CB_TAG);
        if (s_value == null)
        {
            p_propertiesNames.addElement(CB_TAG);
            p_Properties.setProperty(CB_TAG, Main.APP_ID);
        }

        // Добалвяем версию мидлета
        s_value = p_Properties.getProperty(MIDLET_VERSION);
        if (s_value == null)
        {
            p_propertiesNames.addElement(MIDLET_VERSION);
        }
        p_Properties.setProperty(MIDLET_VERSION, "1.0");


        Properties p_workProperties = new Properties();
        Enumeration p_propNames = p_Properties.keys();
        while(p_propNames.hasMoreElements())
        {
            String s_key = (String)p_propNames.nextElement();
            String s_val = p_Properties.getProperty(s_key);
            p_workProperties.setProperty(s_key,s_val);
        }

        Vector p_nameOrder = new Vector();
        Vector p_endOfFile = new Vector();
        for(int li=0;li<p_NamesOrderMF.size();li++)
        {
            String s_name = (String) p_NamesOrderMF.elementAt(li);
            if (p_workProperties.getProperty(s_name)!=null)
            {
                p_nameOrder.addElement(s_name);
            }
        }

        for(int li=0;li<p_propertiesNames.size();li++)
        {
            String s_name = (String) p_propertiesNames.elementAt(li);
            if (s_name.startsWith("$"))
            {
                String s_val = p_Properties.getProperty(s_name);
                s_name = s_name.substring(1);
                p_workProperties.setProperty(s_name,s_val);
                continue;
            }

            if (p_NamesOrderMF.contains(s_name)) continue;
            p_endOfFile.addElement(s_name);
        }

        for (int li = 0; li < p_nameOrder.size(); li++)
        {
            String s_name = (String) p_nameOrder.elementAt(li);
            s_name = s_name.trim();
            if (excludeForMF(s_name)) continue;

            s_value = p_workProperties.getProperty(s_name).trim();
            p_stream.println(s_name + ": " + s_value);
        }

        for (int li = 0; li < p_endOfFile.size(); li++)
        {
            String s_name = (String) p_endOfFile.elementAt(li);
            s_name = s_name.trim();
            if (excludeForMF(s_name)) continue;
            s_value = p_workProperties.getProperty(s_name).trim();
            p_stream.println(s_name + ": " + s_value);
        }
    }

    public void saveJDDFile(File _jddFile) throws IOException
    {
        FileOutputStream p_fos = new FileOutputStream(_jddFile);
        PrintStream p_stream = new PrintStream(p_fos);

        // Добалвяем метку приложения которым создано
        String s_value = p_Properties.getProperty(CB_TAG);
        if (s_value == null)
        {
            p_propertiesNames.addElement(CB_TAG);
            p_Properties.setProperty(CB_TAG, Main.APP_ID);
        }

        // Добалвяем версию мидлета
        s_value = p_Properties.getProperty(MIDLET_VERSION);
        if (s_value == null)
        {
            p_propertiesNames.addElement(MIDLET_VERSION);
            p_Properties.setProperty(MIDLET_VERSION, "1.0");
        }


        //Секция параметров
        p_stream.println(SECTION_PARAMETERS);
        for (int li = 0; li < p_propertiesNames.size(); li++)
        {
            String s_name = (String) p_propertiesNames.elementAt(li);
            s_name = s_name.trim();
            s_value = p_Properties.getProperty(s_name).trim();
            p_stream.println(s_name + "=" + s_value);
        }
        p_stream.println(SECTION_END);

        //Секция порядка в JAD
        p_stream.println(SECTION_ORDER_JAD);
        for (int li = 0; li < p_NamesOrderJAD.size(); li++)
        {
            String s_name = (String) p_NamesOrderJAD.elementAt(li);
            p_stream.println(s_name);
        }
        p_stream.println(SECTION_END);

        //Секция порядка в MF
        p_stream.println(SECTION_ORDER_MF);
        for (int li = 0; li < p_NamesOrderMF.size(); li++)
        {
            String s_name = (String) p_NamesOrderMF.elementAt(li);
            p_stream.println(s_name);
        }
        p_stream.println(SECTION_END);
    }

    public void generateJadFile(File _jadFile) throws IOException
    {
        FileOutputStream p_fos = new FileOutputStream(_jadFile);
        PrintStream p_stream = new PrintStream(p_fos);

        // Добалвяем метку приложения которым создано
        String s_value = p_Properties.getProperty(CB_TAG);
        if (s_value == null)
        {
            p_propertiesNames.addElement(CB_TAG);
            p_Properties.setProperty(CB_TAG, Main.APP_ID);
        }

        // Добалвяем версию мидлета
        s_value = p_Properties.getProperty(MIDLET_VERSION);
        if (s_value == null)
        {
            p_propertiesNames.addElement(MIDLET_VERSION);
            p_Properties.setProperty(MIDLET_VERSION, "1.0");
        }


        Properties p_workProperties = new Properties();
        Enumeration p_propNames = p_Properties.keys();
        while(p_propNames.hasMoreElements())
        {
            String s_key = (String)p_propNames.nextElement();
            String s_val = p_Properties.getProperty(s_key);
            p_workProperties.setProperty(s_key,s_val);
        }

        Vector p_nameOrder = new Vector();
        Vector p_endOfFile = new Vector();
        for(int li=0;li<p_NamesOrderJAD.size();li++)
        {
            String s_name = (String) p_NamesOrderJAD.elementAt(li);
            if (p_workProperties.getProperty(s_name)!=null)
            {
                p_nameOrder.addElement(s_name);
            }
        }

        for(int li=0;li<p_propertiesNames.size();li++)
        {
            String s_name = (String) p_propertiesNames.elementAt(li);
            if (s_name.startsWith("$"))
            {
                String s_val = p_Properties.getProperty(s_name);
                s_name = s_name.substring(1);
                p_workProperties.setProperty(s_name,s_val);
                continue;
            }

            if (p_NamesOrderJAD.contains(s_name)) continue;
            p_endOfFile.addElement(s_name);
        }

        for (int li = 0; li < p_nameOrder.size(); li++)
        {
            String s_name = (String) p_nameOrder.elementAt(li);
            s_name = s_name.trim();

            s_value = p_workProperties.getProperty(s_name).trim();
            p_stream.println(s_name + ": " + s_value);
        }

        for (int li = 0; li < p_endOfFile.size(); li++)
        {
            String s_name = (String) p_endOfFile.elementAt(li);
            s_name = s_name.trim();
            s_value = p_workProperties.getProperty(s_name).trim();
            p_stream.println(s_name + ": " + s_value);
        }
    }

    public void saveVarFile() throws IOException
    {
        final File p_BinFolder = new File(p_Directory, BuilderModule.PATH_BIN);
        if (!p_BinFolder.isDirectory()) throw new IOException("Can't find BIN folder: "+p_BinFolder);

        File p_file = new File(p_BinFolder, BuilderModule.PATH_VARLIST);
        FileOutputStream p_fos = new FileOutputStream(p_file);
        try
        {
            PrintStream p_out = new PrintStream(p_fos);

            for (int li = 0; li < p_cvarNames.size(); li++)
            {
                String s_name = (String) p_cvarNames.elementAt(li);
                String s_value = p_customVariabes.getProperty(s_name);
                p_out.println(s_name + "=" + s_value);
            }
        }
        finally
        {
            try
            {
                p_fos.close();
            }
            catch (IOException e)
            {
            }
        }
    }

    public ProjectInfo(File _directory) throws IOException
    {
        p_propertiesNames = new Vector();
        p_mainClasses = new HashSet();
        lg_saved = true;

        final File p_BinFolder = new File(_directory, BuilderModule.PATH_BIN);

        p_JDDFile = p_BinFolder;

        if (p_JDDFile.exists() && p_JDDFile.isDirectory())
        {
            File [] ap_lst = p_JDDFile.listFiles();
            for(int li=0;li<ap_lst.length;li++)
            {
                File p_f = ap_lst[li];
                if (!p_f.isDirectory())
                {
                    if (p_f.getName().endsWith(".jdd"))
                    {
                        p_JDDFile = p_f;
                        break;
                    }
                }
            }
        }
        p_Directory = _directory;
        if (p_JDDFile==null || !p_JDDFile.exists() || !p_JDDFile.isFile()) throw new IOException(_directory.getName() + " is not project");

        s_JDDName = p_JDDFile.getName();
        int i_index = s_JDDName.lastIndexOf('.');
        if (i_index>=0)
        {
            s_JDDName = s_JDDName.substring(0,i_index);
        }

        // Reading JDD file
        BufferedReader p_reader = new BufferedReader(new InputStreamReader(new FileInputStream(p_JDDFile)));

        p_NamesOrderJAD = new Vector();
        p_NamesOrderMF = new Vector();
        p_ValuesForReplacing = new Vector();
        p_Properties = new Properties();

        while (true)
        {
            String s_str = p_reader.readLine();
            if (s_str == null) break;
            s_str = s_str.trim().toUpperCase();
            if (s_str.length()==0 || s_str.startsWith("#")) continue;

            if (s_str.equals(SECTION_PARAMETERS))
            {
                // Читаем параметры
                while (true)
                {
                    s_str = p_reader.readLine();
                    if (s_str == null) break;
                    if (s_str.length()==0 || s_str.startsWith("#")) continue;
                    if (s_str.trim().equalsIgnoreCase(SECTION_END)) break;
                    StringTokenizer p_st = new StringTokenizer(s_str);
                    try
                    {
                        String s_Name = p_st.nextToken("=").trim();
                        String s_Value = p_st.nextToken().trim();
                        p_Properties.setProperty(s_Name, s_Value);
                        p_propertiesNames.addElement(s_Name);
                    }
                    catch (NoSuchElementException e)
                    {
                        throw new IOException("Error parameter in the JDD file [" + p_JDDFile.getAbsolutePath() + "]");
                    }
                }
            }
            else if (s_str.equals(SECTION_ORDER_JAD))
            {
                // Читаем секцию порядка в джаде
                while (true)
                {
                    s_str = p_reader.readLine();
                    if (s_str == null) break;
                    if (s_str.length()==0 || s_str.startsWith("#")) continue;
                    if (s_str.trim().equalsIgnoreCase(SECTION_END)) break;
                    s_str = s_str.trim();

                    p_NamesOrderJAD.addElement(s_str);
                }

            }
            else if (s_str.equals(SECTION_ORDER_MF))
            {
                // Читаем секцию порядка в джаде
                while (true)
                {
                    s_str = p_reader.readLine();
                    if (s_str == null) break;
                    if (s_str.length()==0 || s_str.startsWith("#")) continue;
                    if (s_str.trim().equalsIgnoreCase(SECTION_END)) break;
                    s_str = s_str.trim();

                    p_NamesOrderMF.addElement(s_str);
                }
            }
        }
        p_reader.close();


        s_ViewName = p_Properties.getProperty(MIDLET_NAME) + " [" + _directory.getName() + "]";

        p_cvarNames = new Vector();

        File p_varFile = new File(p_BinFolder, BuilderModule.PATH_VARLIST);
        p_customVariabes = new Properties();
        p_cvarNames = new Vector();
        if (p_varFile.exists() && !p_varFile.isDirectory())
        {
            p_customVariabes = new Properties();
            try
            {
                p_cvarNames = loadVariablesFromFile(p_customVariabes, p_varFile);
            }
            catch (IOException e)
            {
                throw new IOException("Error of loading variable file");
            }
        }

        String s_midletVersion = p_Properties.getProperty(MIDLET_VERSION);
        if (s_midletVersion == null)
        {
            p_propertiesNames.addElement(MIDLET_VERSION);
            p_Properties.setProperty(MIDLET_VERSION, "1.0");
        }
        if (!updateMIDPCLDCVersions()) throw new IOException("Can't load project, can't find " + MIDLET_CLDC_PROFILE +" or "+ MIDLET_MIDP_PROFILE);
        updateNamesOfMidlets();
    }

    public boolean updateMIDPCLDCVersions()
    {
        // Выявляем версии
        String s_midp = p_Properties.getProperty(MIDLET_MIDP_PROFILE);
        String s_cldc = p_Properties.getProperty(MIDLET_CLDC_PROFILE);

        i_MIDP_version = MIDP_UNKNOWN;
        i_CLDC_version = CLDC_UNKNOWN;

        if (s_midp == null) {
            return false;
        } else {
            if (s_midp.equals("MIDP-1.0")) i_MIDP_version = MIDP_10;
            else if (s_midp.equals("MIDP-2.0")) i_MIDP_version = MIDP_20;
        }

        if (s_cldc == null) {
            return false;
        } else {
            if (s_cldc.equals("CLDC-1.0")) i_CLDC_version = CLDC10;
            else if (s_cldc.equals("CLDC-1.1")) i_CLDC_version = CLDC11;
        }

        String s_midletVersion = p_Properties.getProperty(MIDLET_VERSION);
        if (s_midletVersion == null)
        {
            p_Properties.setProperty(MIDLET_VERSION, "1.0");
        }
        return true;
    }

    public Vector loadVariablesFromFile(Properties _properties, File _file) throws IOException
    {
        BufferedReader p_bufreader = new BufferedReader(new FileReader(_file));
        Vector p_keyNames = new Vector();

        int i_stringCounter = 0;

        while (true)
        {
            String s_str = p_bufreader.readLine();
            if (s_str == null) break;
            i_stringCounter++;

            s_str = s_str.trim();

            if (s_str.length() == 0) continue;
            if (s_str.startsWith("#")) continue;

            String s_name = null;
            String s_value = null;

            int i_equindx = s_str.indexOf('=');
            if (i_equindx < 0)
            {
                throw new IOException("Error parameter format in " + _file.getPath() + " line:" + i_stringCounter);
            }

            s_name = s_str.substring(0, i_equindx).trim();
            s_value = s_str.substring(i_equindx + 1).trim();

            p_keyNames.addElement(s_name);

            _properties.put(s_name, s_value);
        }
        return p_keyNames;
    }

    public void setNewProperties(Properties _prop)
    {
        p_Properties = _prop;
        lg_saved = false;
    }

    public Properties getProperties()
    {
        return p_Properties;
    }

    public String toString()
    {
        return s_ViewName;
    }

    public boolean addCustomVariable(String _name, String _value) throws IOException
    {
        if (p_customVariabes.getProperty(_name) != null) return false;
        p_cvarNames.addElement(_name);
        p_customVariabes.setProperty(_name, _value);
        saveVarFile();
        return true;
    }

    public boolean addJDDVariable(String _name, String _value, boolean forceSave) throws IOException
    {
        if (p_Properties.getProperty(_name) != null)
        {
            p_Properties.setProperty(_name,_value);
            return false;
        }
        p_propertiesNames.addElement(_name);
        p_Properties.setProperty(_name, _value);

        if (forceSave) {
            saveJDDFile(p_JDDFile);
        }
        return true;
    }

    public boolean removeJDDVariable(String _name) throws IOException
    {
        if (p_Properties.getProperty(_name) == null) return false;
        for (int li = 0; li < p_propertiesNames.size(); li++)
        {
            if (p_propertiesNames.elementAt(li).equals(_name))
            {
                p_propertiesNames.removeElementAt(li);
                break;
            }
        }
        p_Properties.remove(_name);
        saveVarFile();
        return true;
    }

    public boolean removeCustomVariable(String _name) throws IOException
    {
        if (p_customVariabes.getProperty(_name) == null) return false;
        for (int li = 0; li < p_cvarNames.size(); li++)
        {
            if (p_cvarNames.elementAt(li).equals(_name))
            {
                p_cvarNames.removeElementAt(li);
                break;
            }
        }
        p_customVariabes.remove(_name);
        saveVarFile();
        return true;
    }

    public int compare(ProjectInfo other) {
        return other == null ? 1 : this.s_ViewName.compareTo(other.s_ViewName);
    }

    public Properties getCustomVariables()
    {
        return p_customVariabes;
    }

    public void updateNamesOfMidlets()
    {
        Enumeration p_enum = p_Properties.keys();
        p_mainClasses.clear();
        while (p_enum.hasMoreElements())
        {
            String s_key = (String) p_enum.nextElement();
            if (s_key.startsWith("MIDlet-"))
            {
                String s_num = s_key.substring(7).trim();
                try
                {
                    Integer.parseInt(s_num);
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

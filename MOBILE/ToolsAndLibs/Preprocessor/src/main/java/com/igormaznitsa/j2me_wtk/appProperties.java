package com.igormaznitsa.j2me_wtk;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class appProperties
{
    public static boolean Debug;
    public static boolean Optimizing;
    public static boolean Obfuscating;
    public static boolean RemoveComments;
    public static boolean JarOptimizing;
    public static File MainDirectory;

    public static String DestinationDir;
    public static String JarPath;
    public static String JavacPath;
    public static String MIDP10Path;
    public static String MIDP20Path;
    public static String CLDC10Path;
    public static String CLDC11Path;
    public static String AddLibPath;
    public static String PreverifierPath;
    public static String ObfuscatorPath;

    public static File p_storageFile;

    private static final String VAR_DEBUG = "DEBUG";
    private static final String VAR_OPTIMIZING = "OPTIMIZIG";
    private static final String VAR_OBFUSCATING = "OBFUSCATING";
    private static final String VAR_REMOVECOMMENTS = "REMOVECOMMENTS";
    private static final String VAR_MAINDIR = "MAINDIR";
    private static final String VAR_DESTDIR = "DESTDIR";
    private static final String VAR_JARPATH = "JAR";
    private static final String VAR_JAVACPATH = "JAVAC";
    private static final String VAR_MIDP10 = "MIDP10";
    private static final String VAR_MIDP20 = "MIDP20";
    private static final String VAR_CLDC10 = "CLDC10";
    private static final String VAR_CLDC11 = "CLDC11";
    private static final String VAR_ADDLIB = "ADDLIB";
    private static final String VAR_PREVERIFIER = "PREVERIF";
    private static final String VAR_OBFUSCATOR = "OBFUSC";
    private static final String VAR_JAROPT = "JAROPT";

    public static final void initDefault()
    {
        MainDirectory = null;
        Debug = false;
        Optimizing = true;
        Obfuscating = true;
        RemoveComments = true;
        JarOptimizing = true;

        DestinationDir = "/bin";
        JarPath = "jar.exe";
        JavacPath = "javac.exe";
        MIDP10Path = "./midpapi10.jar";
        MIDP20Path = "./midpapi20.jar";
        CLDC10Path = "./cldcapi10.jar";
        CLDC11Path = "./cldcapi11.jar";
        AddLibPath = "r:/currentproject/preprocessor/AddLib";
        PreverifierPath = "preverify.exe";
        ObfuscatorPath = "proguard.jar";
    }

    public static final void save() throws IOException
    {
        FileOutputStream p_fos = new FileOutputStream(p_storageFile);
        Properties p_props = new Properties();
        p_props.setProperty(VAR_JAROPT,JarOptimizing ? "TRUE" : "FALSE");
        p_props.setProperty(VAR_DEBUG,Debug ? "TRUE" : "FALSE");
        p_props.setProperty(VAR_OBFUSCATING,Obfuscating ? "TRUE" : "FALSE");
        p_props.setProperty(VAR_REMOVECOMMENTS,RemoveComments ? "TRUE" : "FALSE");
        p_props.setProperty(VAR_OPTIMIZING,Optimizing ? "TRUE" : "FALSE");
        p_props.setProperty(VAR_MAINDIR,MainDirectory==null ? "" : MainDirectory.getCanonicalPath());

        p_props.setProperty(VAR_DESTDIR,DestinationDir);
        p_props.setProperty(VAR_JARPATH,JarPath);
        p_props.setProperty(VAR_JAVACPATH,JavacPath);
        p_props.setProperty(VAR_MIDP10,MIDP10Path);
        p_props.setProperty(VAR_MIDP20,MIDP20Path);
        p_props.setProperty(VAR_CLDC10,CLDC10Path);
        p_props.setProperty(VAR_CLDC11,CLDC11Path);
        p_props.setProperty(VAR_PREVERIFIER,PreverifierPath);
        p_props.setProperty(VAR_ADDLIB,AddLibPath);
        p_props.setProperty(VAR_OBFUSCATOR,ObfuscatorPath);

        p_props.store(p_fos,"RRGWTK");
        p_fos.close();
        p_props = null;
    }

    public static final boolean loadFromFile(File _file) throws IOException
    {
        initDefault();

        p_storageFile = _file;

        if (_file.exists() && !_file.isDirectory())
        {
            FileInputStream p_fis = new FileInputStream(_file);
            try
            {
                Properties p_prop = new Properties();
                p_prop.load(p_fis);

                String s_debug = p_prop.getProperty(VAR_DEBUG);
                String s_maindir = p_prop.getProperty(VAR_MAINDIR);
                String s_optimizing = p_prop.getProperty(VAR_OPTIMIZING);
                String s_rmovecomm = p_prop.getProperty(VAR_REMOVECOMMENTS);
                String s_obfuscate = p_prop.getProperty(VAR_OBFUSCATING);
                String s_jaropt = p_prop.getProperty(VAR_JAROPT);
                String s_destdir = p_prop.getProperty(VAR_DESTDIR);
                String s_jarpath = p_prop.getProperty(VAR_JARPATH);
                String s_javacpath = p_prop.getProperty(VAR_JAVACPATH);
                String s_midp10 = p_prop.getProperty(VAR_MIDP10);
                String s_midp11 = p_prop.getProperty(VAR_MIDP20);
                String s_cldc10 = p_prop.getProperty(VAR_CLDC10);
                String s_cldc11 = p_prop.getProperty(VAR_CLDC11);
                String s_addlib = p_prop.getProperty(VAR_ADDLIB);
                String s_preverif = p_prop.getProperty(VAR_PREVERIFIER);
                String s_obfuscator = p_prop.getProperty(VAR_OBFUSCATOR);

                if (s_debug!=null)
                {
                    if (s_debug.equals("TRUE")) Debug = true; else Debug = false;
                }

                if (s_obfuscate!=null)
                {
                    if (s_obfuscate.equals("TRUE")) Obfuscating = true; else Obfuscating = false;
                }

                if (s_jaropt!=null)
                {
                    if (s_jaropt.equals("TRUE")) JarOptimizing = true; else JarOptimizing = false;
                }

                if (s_rmovecomm!=null)
                {
                    if (s_rmovecomm.equals("TRUE")) RemoveComments = true; else RemoveComments = false;
                }

                if (s_optimizing!=null)
                {
                    if (s_optimizing.equals("TRUE")) Optimizing = true; else Optimizing = false;
                }

                if (s_destdir != null)
                {
                    DestinationDir = s_destdir;
                }

                if (s_obfuscator != null)
                {
                    ObfuscatorPath = s_obfuscator;
                }

                if (s_jarpath != null)
                {
                    JarPath = s_jarpath;
                }

                if (s_javacpath != null)
                {
                    JavacPath = s_javacpath;
                }

                if (s_preverif != null)
                {
                    PreverifierPath = s_preverif;
                }

                if (s_midp10 != null)
                {
                    MIDP10Path = s_midp10;
                }

                if (s_midp11 != null)
                {
                    MIDP20Path = s_midp11;
                }

                if (s_cldc10 != null)
                {
                    CLDC10Path= s_cldc10;
                }

                if (s_cldc11 != null)
                {
                    CLDC11Path= s_cldc11;
                }

                if (s_addlib != null)
                {
                    AddLibPath= s_addlib;
                }

                if (s_maindir!=null)
                {
                    if (s_maindir.length()==0) MainDirectory = null;
                    else
                    {
                        MainDirectory = new File(s_maindir);
                        if (!MainDirectory.exists() || !MainDirectory.isDirectory())
                        {
                            MainDirectory = null;
                        }
                    }
                }
            }
            finally
            {
                try
                {
                    p_fis.close();
                }
                catch (IOException e)
                {
                }
                p_fis = null;
            }
            return true;
        }
        else
            return false;
    }



}

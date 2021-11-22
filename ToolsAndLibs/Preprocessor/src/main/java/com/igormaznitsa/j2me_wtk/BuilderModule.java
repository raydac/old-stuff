package com.igormaznitsa.j2me_wtk;

import com.igormaznitsa.WToolkit.classes.*;
import com.igormaznitsa.Preprocessor.Preprocessor;

import java.io.*;
import java.util.Locale;

public class BuilderModule
{
    public static interface ProjectProcessingListener
    {
        public void nextProjectBuildStep();
    }

    public static ProjectProcessingListener p_ProcessListener;

    public static final String PATH_VARLIST = "varlist.jcp";
    public static final String PATH_BEFORELOCAL = "/before.bat";
    public static final String PATH_AFTERLOCAL = "/after.bat";
    public static final String PATH_AFTER = "/after.bat";
    public static final String PATH_BEFORE = "/before.bat";
    public static final String PATH_SOURCES = "src";
    public static final String PATH_RESOURCES = "res";
    public static final String PATH_PREPROCESSED = "preprocessed";
    public static final String PATH_GENERICLIB = "lib";
    public static final String PATH_TMPCLASSES = "tmpclasses";
    public static final String PATH_CLASSES = "classes";
    public static final String PATH_BIN = "bin";
    public static final String PATH_MAINFEST = "Manifest.mf";

    public static final String SYSPROPERTY_PROJNAME = "RRGWTK_PROJNAME";
    public static final String SYSPROPERTY_PROJDIR = "RRGWTK_PROJDIR";
    public static final String SYSPROPERTY_PROJDEST = "RRGWTK_DESTDIR";
    public static final String SYSPROPERTY_MAINDIR = "RRGWTK_MAINDIR";

    public static String s_ProtocolString = "";
    public static final int BUILD_STEPS = 8;

    public static int deleteTempFolders(ProjectInfo project) throws IOException {
        int counter = 0;
        final File baseFolder = project.getDirectory();
        final File preprocessed = new File(baseFolder, PATH_PREPROCESSED);
        final File tempClasses = new File(baseFolder, PATH_TMPCLASSES);
        final File classes = new File(baseFolder, PATH_CLASSES);
        if (preprocessed.isDirectory()) {
            if (!FileUtils.deleteDir(preprocessed)) throw new IOException("Can't delete folder "+preprocessed);
            counter ++;
        }
        if (tempClasses.isDirectory()) {
            if (!FileUtils.deleteDir(tempClasses)) throw new IOException("Can't delete folder "+tempClasses);
            counter ++;
        }
        if (classes.isDirectory()) {
            if (!FileUtils.deleteDir(classes)) throw new IOException("Can't delete folder "+classes);
            counter ++;
        }
        return counter;
    }

    public static final String buildProject(ProjectInfo _project, File _mainDir, File _destDir, boolean _obfuscated, boolean _debug, boolean _removecomments, boolean _optimization, String _bootclassPath, boolean _jarOptimization) throws IOException
    {
        final String[] as_envVars = new String[]
                {
                            SYSPROPERTY_PROJNAME + '=' + _project.getJDDName().trim().replaceAll("/", File.separator),
                            SYSPROPERTY_PROJDIR + '=' + _project.getDirectory().getCanonicalPath().trim().replaceAll("/", File.separator),
                            SYSPROPERTY_PROJDEST + '=' + _destDir.getCanonicalPath().replaceAll("/", File.separator),
                            SYSPROPERTY_MAINDIR, _mainDir.getCanonicalPath().replaceAll("/", File.separator)
                    };

        // Отключаем обфускацию принудительно если требуется отладочная версия
        if (_debug)
        {
            _obfuscated = false;
        }

        StringArray p_strArray = new StringArray();
        StringBuffer p_strBuff = new StringBuffer();

        deleteTempFolders(_project);

        final File binFolder = new File(_project.getDirectory(), PATH_BIN);

        // Отработка сценария BEFORE, если есть
        //-------------------------------------------------------
        p_strArray.clear();
        File p_beforeBat = new File(_project.getDirectory(), PATH_BEFORELOCAL);
        if (!p_beforeBat.exists() || p_beforeBat.isDirectory())
        {
            if (_mainDir != null)
            {
                p_beforeBat = new File(_mainDir, PATH_BEFORE);
            }
        }

        if (p_beforeBat.exists() && !p_beforeBat.isDirectory())
        {
            p_strArray.add(p_beforeBat.getCanonicalPath());
            p_strBuff.append(p_strArray.toString());

            ExtAppStructure p_struct = ExtAppStarter.startApp(p_strArray, as_envVars);
            String s_strOut = new String(p_struct.outStreamArray);
            String s_strErr = new String(p_struct.errStreamArray);
            p_strBuff.append(s_strOut);
            p_strBuff.append(s_strErr);

            if (p_struct.Status != 0)
            {
                s_ProtocolString = p_strBuff.toString();
                throw new IOException("Error status of BEFORE.BAT [" + p_struct.Status + "]");
            }
        }

        p_ProcessListener.nextProjectBuildStep();

        // Создаем директории
        //-------------------------------------------------------
        if (!_destDir.exists())
        {
            if (!_destDir.mkdirs())
            {
                s_ProtocolString = p_strBuff.toString();
                throw new IOException("I can't make the destination directory [" + _destDir.getAbsolutePath() + "]");
            }
        }

        File p_classesDir = new File(_project.getDirectory(), PATH_CLASSES);
        if (p_classesDir.exists())
        {
            if (!FileUtils.clearDirectory(p_classesDir))
            {
                s_ProtocolString = p_strBuff.toString();
                throw new IOException("I can't clear the classes directory [" + p_classesDir.getAbsolutePath() + "]");
            }
        } else if (!p_classesDir.mkdirs())
        {
            s_ProtocolString = p_strBuff.toString();
            throw new IOException("I can't make the classes directory [" + p_classesDir.getAbsolutePath() + "]");
        }

        File p_tmpClassesDir = new File(_project.getDirectory(), PATH_TMPCLASSES);
        if (p_tmpClassesDir.exists())
        {
            if (!FileUtils.clearDirectory(p_tmpClassesDir))
            {
                s_ProtocolString = p_strBuff.toString();
                throw new IOException("I can't clear the temp classes directory [" + p_tmpClassesDir.getAbsolutePath() + "]");
            }
        } else if (!p_tmpClassesDir.mkdirs())
        {
            s_ProtocolString = p_strBuff.toString();
            throw new IOException("I can't make the temp classes directory [" + p_tmpClassesDir.getAbsolutePath() + "]");
        }

        p_ProcessListener.nextProjectBuildStep();


        // Препроцессинг исходных текстов
        //-------------------------------------------------------
        p_strArray = new StringArray();

        // Подключаем файл с переменными если есть
        File p_varList = new File(binFolder, PATH_VARLIST);
        if (p_varList.exists())
        {
            p_strArray.add("@" + p_varList.getCanonicalPath());
        }

        // Добавляем флаг отладки
        if (_debug)
        {
            p_strArray.add("/P:DEBUG=true");
        } else
        {
            p_strArray.add("/P:DEBUG=false");
        }

        // Добавляем значения MIDP и CLDC
        String s_MIDP = "UNKNOWN";
        String s_CLDC = "UNKNOWN";
        switch (_project.getCLDCVersion())
        {
            case ProjectInfo.CLDC10:
                s_CLDC = "1.0";
                break;
            case ProjectInfo.CLDC11:
                s_CLDC = "1.1";
                break;
        }

        switch (_project.getMIDPVersion())
        {
            case ProjectInfo.MIDP_10:
                s_MIDP = "1.0";
                break;
            case ProjectInfo.MIDP_20:
                s_MIDP = "2.0";
                break;
        }

        p_strArray.add("/P:MIDP=$" + s_MIDP + "$");
        p_strArray.add("/P:CLDC=$" + s_CLDC + "$");

        // Добавляем флаг удаления комментариев
        if (_removecomments)
        {
            p_strArray.add("/R");
        }

        File p_srcFile = new File(_project.getDirectory(), PATH_SOURCES);
        if (!p_srcFile.exists() || !p_srcFile.isDirectory()) throw new IOException("I can't find the sources directory");
        p_strArray.add("/I:" + p_srcFile.getCanonicalPath());
        File p_preprocessedFile = new File(_project.getDirectory(), PATH_PREPROCESSED);
        if (p_preprocessedFile.isDirectory())
        {
            if (!FileUtils.deleteDir(p_preprocessedFile))
            {
                s_ProtocolString = p_strBuff.toString();
                throw new IOException("I can't delete the preprocessed directory");
            }
        }
        if (!p_preprocessedFile.mkdirs())
        {
            s_ProtocolString = p_strBuff.toString();
            throw new IOException("I can't make the directory for preprocessed files");
        }
        p_strArray.add("/O:" + p_preprocessedFile.getCanonicalPath());

        ByteArrayOutputStream p_outStreamArray = new ByteArrayOutputStream(16000);
        ByteArrayOutputStream p_errStreamArray = new ByteArrayOutputStream(8000);
        PrintStream p_outPrStr = new PrintStream(p_outStreamArray);
        PrintStream p_errPrStr = new PrintStream(p_errStreamArray);

        int i_state = Preprocessor.process(p_strArray.toStringArray(), p_outPrStr, p_errPrStr, false, null);

        p_outPrStr.close();
        p_errPrStr.close();
        String s_outStr = new String(p_outStreamArray.toByteArray());
        String s_errStr = new String(p_errStreamArray.toByteArray());
        p_strBuff.append(s_outStr);
        p_strBuff.append(s_errStr);

        if (i_state != 0)
        {
            s_ProtocolString = p_strBuff.toString();
            throw new IOException("Error status of preprocessing [" + i_state + "]");
        }

        p_ProcessListener.nextProjectBuildStep();

        // Компиляция исходных текстов
        //-------------------------------------------------------
        String[] as_JavaSources = SourcesFinder.sourceList(p_preprocessedFile);
        if (as_JavaSources == null || as_JavaSources.length == 0)
        {
            s_ProtocolString = p_strBuff.toString();
            throw new IOException("There are not resources for compiling");
        }

        String s_path = getClassPath(_project.getDirectory());

        p_strArray = new StringArray();
        p_strArray.add(appProperties.JavacPath);


        p_strArray.add("-target");
        p_strArray.add("1.1");
        p_strArray.add("-source");
        p_strArray.add("1.3");

        if (_debug)
        {
            p_strArray.add("-g:lines,vars,source");
        } else
        {
            p_strArray.add("-g:none");
            p_strArray.add("-O");
        }
        p_strArray.add("-bootclasspath");
        p_strArray.add(_bootclassPath);
        p_strArray.add("-d");
        p_strArray.add(p_tmpClassesDir.getCanonicalPath());

        if (s_path != null && s_path.length() > 0)
        {
            p_strArray.add("-classpath");
            p_strArray.add(s_path);
        }
        p_strArray.append(as_JavaSources);

        p_strBuff.append("\r\n" + p_strArray.toString() + "\r\n");

        ExtAppStructure p_result = ExtAppStarter.startApp(p_strArray, null);

        String s_strOut = new String(p_result.outStreamArray);
        String s_strErr = new String(p_result.errStreamArray);
        p_strBuff.append(s_strOut);
        p_strBuff.append(s_strErr);

        if (p_result.Status != 0)
        {
            s_ProtocolString = p_strBuff.toString();
            throw new IOException("Error status of compilation [" + p_result.Status + "]");
        }

        p_ProcessListener.nextProjectBuildStep();

        if (_obfuscated) {
            if (appProperties.ObfuscatorPath.trim().length() == 0) {
                throw new IOException("Obfuscation is requested but obfuscator path is empty");
            } else {
                // Обфускация JAR архива (ProGuard)
                //-------------------------------------------------------
                p_strArray.clear();
                if (appProperties.ObfuscatorPath.trim().toLowerCase(Locale.ENGLISH).endsWith(".jar")) {
                    p_strArray.add("java");
                    p_strArray.add("-jar");
                    p_strArray.add(appProperties.ObfuscatorPath);
                } else {
                    p_strArray.add(appProperties.ObfuscatorPath);
                }

                File p_proguardOpt = new File(_project.getDirectory().getAbsolutePath(), "proguard.pro");
                final boolean proguardOptionFileFound = p_proguardOpt.isFile();
                if (proguardOptionFileFound) {
                    p_strBuff.append("\r\nfound custom proguard.pro file\r\n");
                    p_strArray.add("@" + p_proguardOpt.getAbsolutePath());
                } else {
                    p_strArray.add("-overloadaggressively");
                    p_strArray.add("-defaultpackage");
                    p_strArray.add("''");
                    p_strArray.add("-dontusemixedcaseclassnames");
                    p_strArray.add("-allowaccessmodification");
                    p_strArray.add("-keep public class * extends javax.microedition.midlet.MIDlet");
                }
                if (!_optimization) p_strArray.add("-dontoptimize");
                p_strArray.add("-libraryjars");

                if (s_path != null && s_path.length() > 0) {
                    p_strArray.add(_bootclassPath + File.pathSeparator + s_path);
                } else
                    p_strArray.add(_bootclassPath);

                p_strArray.add("-injars");
                p_strArray.add(p_tmpClassesDir.getCanonicalPath());
                p_strArray.add("-outjar");
                p_strArray.add(p_classesDir.getCanonicalPath());

                p_strBuff.append("\r\n" + p_strArray.toString() + "\r\n");

                p_result = ExtAppStarter.startApp(p_strArray, null);
                s_strOut = new String(p_result.outStreamArray);
                s_strErr = new String(p_result.errStreamArray);
                p_strBuff.append(s_strOut);
                p_strBuff.append(s_strErr);

                if (p_result.Status != 0) {
                    s_ProtocolString = p_strBuff.toString();
                    throw new IOException("Error status of obfuscator [" + p_result.Status + "]");
                }

                File p_fileA = p_classesDir;
                p_classesDir = p_tmpClassesDir;
                p_tmpClassesDir = p_fileA;
                p_fileA = null;
            }
        }

        p_ProcessListener.nextProjectBuildStep();

        // Preverifier call
        //-------------------------------------------------------
        if (appProperties.PreverifierPath.trim().length() == 0) {
            p_strBuff.append("\r\n!!! Preverifier call is ignored because path is empty !!!\r\n");
            if (_obfuscated) {
                // just copy classes folder into tmpclasses
                if (!FileUtils.deleteDir(p_classesDir)) {
                    throw new IOException("Can't delete folder: "+ p_classesDir);
                }
                FileUtils.copyDir(p_tmpClassesDir, p_classesDir);
            }
        } else {
            String s_cldc = "CLDC1.0";
            if (_project.getCLDCVersion() == ProjectInfo.CLDC11) {
                s_cldc = "CLDC1.1";
            }

            if (p_classesDir.exists()) {
                if (!FileUtils.clearDirectory(p_classesDir)) {
                    s_ProtocolString = p_strBuff.toString();
                    throw new IOException("I can't clear a directory");
                }
            } else if (!p_classesDir.mkdirs()) {
                s_ProtocolString = p_strBuff.toString();
                throw new IOException("I can't make a directory");
            }

            p_strArray.clear();
            p_strArray.add(appProperties.PreverifierPath);
            if (s_cldc.equals("CLDC1.0")) {
                p_strArray.add("-nofp");
            }
            p_strArray.add("-nofinalize");
            p_strArray.add("-nonative");
            p_strArray.add("-target");
            p_strArray.add(s_cldc);
            p_strArray.add("-classpath");

            if (s_path != null && s_path.length() > 0) {
                p_strArray.add(_bootclassPath + File.pathSeparator + s_path);
            } else
                p_strArray.add(_bootclassPath);

            p_strArray.add("-d");
            p_strArray.add(p_classesDir.getCanonicalPath());
            p_strArray.add(p_tmpClassesDir.getCanonicalPath());

            p_strBuff.append("\r\n" + p_strArray.toString() + "\r\n");

            p_result = ExtAppStarter.startApp(p_strArray, null);

            s_strOut = new String(p_result.outStreamArray);
            s_strErr = new String(p_result.errStreamArray);
            p_strBuff.append(s_strOut);
            p_strBuff.append(s_strErr);

            if (p_result.Status != 0) {
                s_ProtocolString = p_strBuff.toString();
                throw new IOException("Error status of preverifier [" + p_result.Status + "]");
            }
        }
        //----------------------------------------------------------------


        // Увеличиваем номер билда
        _project.increaseBuildNumber();

        // Записываем манифестный файл
        //-------------------------------------------------------
        p_strBuff.append("Saving the manifest file\r\n");
        File p_manifestFile = new File(binFolder, PATH_MAINFEST);
        _project.generateManifestFile(p_manifestFile);

        // Формируем целевой JAR файл из проверенных файлов
        //---------------------------------------------------
        File p_JarFile = new File(_destDir, _project.getJDDName() + ".jar");
        String[] as_jarClasses = new String[]
                {
                            "cmf", p_manifestFile.getCanonicalPath(), p_JarFile.getCanonicalPath(), "-C", p_classesDir.getCanonicalPath(), "."
                    };

        p_strArray.clear();
        p_strArray.add(appProperties.JarPath);
        p_strArray.append(as_jarClasses);

        p_strBuff.append("\r\n" + p_strArray.toString() + "\r\n");

        p_result = ExtAppStarter.startApp(p_strArray, null);

        s_strOut = new String(p_result.outStreamArray);
        s_strErr = new String(p_result.errStreamArray);
        p_strBuff.append(s_strOut);
        p_strBuff.append(s_strErr);

        if (p_result.Status != 0)
        {
            s_ProtocolString = p_strBuff.toString();
            throw new IOException("Error status of JAR packing of destination JAR[" + p_result.Status + "]");
        }

        p_ProcessListener.nextProjectBuildStep();

        // Добавляем ресурсы в JAR
        //-------------------------------------------------------
        String as_jarRes[] = null;
        File p_resDir = new File(_project.getDirectory(), PATH_RESOURCES);
        if (p_resDir.exists())
        {
            as_jarRes = new String[]
                    {
                                "uf", p_JarFile.getCanonicalPath(), "-C", p_resDir.getCanonicalPath(), "."
                        };
        }

        if (as_jarRes != null)
        {
            p_strArray.clear();
            p_strArray.add(appProperties.JarPath);
            p_strArray.append(as_jarRes);

            p_strBuff.append("\r\n" + p_strArray.toString() + "\r\n");

            p_result = ExtAppStarter.startApp(p_strArray, null);

            s_strOut = new String(p_result.outStreamArray);
            s_strErr = new String(p_result.errStreamArray);
            p_strBuff.append(s_strOut);
            p_strBuff.append(s_strErr);

            if (p_result.Status != 0)
            {
                s_ProtocolString = p_strBuff.toString();
                throw new IOException("Error status of destination JAR packing of resources [" + p_result.Status + "]");
            }
        }

        p_ProcessListener.nextProjectBuildStep();

        // Оптимизируем JAR
        //-------------------------------------------------------
        if (_jarOptimization)
        {
            try
            {
                byte[] ab_newJar = com.igormaznitsa.jaroptimizer.Main.optimizeJar(p_JarFile, 9);
                FileOutputStream p_fos = new FileOutputStream(p_JarFile);
                p_fos.write(ab_newJar);
                p_fos.flush();
                try
                {
                    p_fos.close();
                }
                catch (Exception e)
                {
                }
                p_fos = null;
            } catch (IOException e)
            {
                throw new IOException("Error during JAR optimization [" + e.getMessage() + "]");
            }
        }
        p_ProcessListener.nextProjectBuildStep();

        // Записываем новый размер JAR в JAD и сохраняем его
        //-------------------------------------------------------
        int i_size = (int) p_JarFile.length();
        _project.addJDDVariable("MIDlet-Jar-Size", Integer.toString(i_size));
        String s_name = _project.getJDDName();
        s_name += ".jad";

        File p_jadFile = new File(_destDir, s_name);

        // Записываем параметр MIDlet-Jar-URL если его нет
        //-------------------------------------------------
        _project.addJDDVariable("MIDlet-Jar-URL", p_JarFile.getName());

        _project.generateJadFile(p_jadFile);

        p_ProcessListener.nextProjectBuildStep();

        // Отработка сценария AFTER, если есть
        //-------------------------------------------------------
        p_strArray.clear();

        File p_afterBat = new File(_project.getDirectory(), PATH_AFTERLOCAL);
        if (!p_afterBat.exists() || p_afterBat.isDirectory())
        {
            if (_mainDir != null)
            {
                p_afterBat = new File(_mainDir, PATH_AFTER);
            }
        }

        if (p_afterBat.exists() && !p_afterBat.isDirectory())
        {
            p_strArray.add(p_afterBat.getCanonicalPath());

            p_strBuff.append(p_strArray.toString());

            ExtAppStructure p_struct = ExtAppStarter.startApp(p_strArray, as_envVars);

            s_strOut = new String(p_struct.outStreamArray);
            s_strErr = new String(p_struct.errStreamArray);
            p_strBuff.append(s_strOut);
            p_strBuff.append(s_strErr);

            if (p_struct.Status != 0)
            {
                s_ProtocolString = p_strBuff.toString();
                throw new IOException("Error status of AFTER.BAT [" + p_struct.Status + "]");
            }
        }

        p_ProcessListener.nextProjectBuildStep();

        p_tmpClassesDir = null;
        p_preprocessedFile = null;
        p_srcFile = null;
        p_varList = null;
        p_afterBat = null;
        p_beforeBat = null;
        p_classesDir = null;
        p_jadFile = null;
        p_JarFile = null;
        p_manifestFile = null;
        p_resDir = null;

        System.gc();

        p_strBuff.append("--------OK-----------");

        _project.saveJDDFile(_project.getJDDFile());

        return p_strBuff.toString();
    }

    private static final String getClassPath(File _projDir)
    {
        File p_file = new File(_projDir, PATH_GENERICLIB);
        StringBuffer p_strBuff = new StringBuffer();
        String[] as_string = null;
        if (p_file.exists() && p_file.isDirectory())
        {
            as_string = SourcesFinder.libList(p_file);
            if (as_string != null)
            {
                for (int j = 0; j < as_string.length; j++)
                {
                    if (j != 0) p_strBuff.append(File.pathSeparator);
                    p_strBuff.append(as_string[j]);
                }
            }
        }

        File p_addlib = new File(appProperties.AddLibPath);
        if (p_addlib.exists() && p_addlib.isDirectory())
        {
            as_string = SourcesFinder.libList(p_addlib);
            if (as_string.length > 0)
            {
                if (p_strBuff.length() > 0) p_strBuff.append(File.pathSeparator);
                for (int li = 0; li < as_string.length; li++)
                {
                    if (li != 0)
                    {
                        p_strBuff.append(File.pathSeparator);
                    }
                    p_strBuff.append(as_string[li]);
                }
            }
        }

        p_file = null;

        return p_strBuff.toString();
    }

}

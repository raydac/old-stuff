package com.igormaznitsa.WToolkit.classes;

import java.io.*;

public class BuildProject
{
    File p_projectDir;
    File sourceDir;
    File resDir;
    File appDir;
    File libDir;

    String s_ProjectName;

    File tmplibDir;
    File genericLibDir;
    File genericTmplibDir;

    File p_jadFile;
    File p_manifestFile;

    File tmpClassDir;
    File classDir;

    String s_classPath;
    String s_jarPath;
    String s_preverifierPath;
    String s_javac;

    boolean lg_includeDebug;

    public ByteArrayOutputStream p_terminalArray;

    public BuildProject(ProjectInfo _info, String _classPath, String _preverifierPath, String _jarpath,String _javac,boolean _IncludeDebug) throws IOException
    {
        lg_includeDebug = _IncludeDebug;
        s_javac = _javac;
        s_jarPath = _jarpath;
        s_preverifierPath = _preverifierPath;
        p_terminalArray = new ByteArrayOutputStream(256);

        s_ProjectName = _info.p_Directory.getName();
        p_projectDir = _info.p_Directory;
        s_classPath = _classPath;

        sourceDir = new File(p_projectDir, "src");
        resDir = new File(p_projectDir, "res");
        appDir = new File(p_projectDir, "bin");
        libDir = new File(p_projectDir, "lib");

        tmplibDir = new File(p_projectDir, "tmplib");
        genericLibDir = new File(p_projectDir, "lib");
        genericTmplibDir = new File(p_projectDir, "tmplib");

        appDir.mkdirs();
        p_jadFile = _info.p_JADFile;
        p_manifestFile = _info.p_ManifFile;

        tmpClassDir = new File(p_projectDir, "tmpclasses");
        classDir = new File(p_projectDir, "classes");
        tmpClassDir.mkdirs();
        classDir.mkdirs();
    }

    void preprocess(boolean _debug) throws IOException
    {
        File p_preprocessDir = new File(p_projectDir,"preprocessed");
        p_preprocessDir.mkdirs();

        String as[] = SourcesFinder.sourceList(sourceDir);
        if (as == null || as.length == 0) throw new IOException("There are not resources for compiling");
        File afile[] = {tmplibDir, genericTmplibDir};

        StringArray stringarray = new StringArray();
        stringarray.add("java");
        stringarray.add("com.igormaznitsa.Preprocessor.Preprocessor");
        stringarray.add("/I:"+sourceDir.getCanonicalPath());
        stringarray.add("/O:"+p_preprocessDir.getCanonicalPath());
        if (_debug)
            stringarray.add("/P:DEBUG=true");
        else
            stringarray.add("/P:DEBUG=false");

        Process process = Runtime.getRuntime().exec(stringarray.toStringArray());
        ByteArrayOutputStream p_baos_out = new ByteArrayOutputStream();
        ByteArrayOutputStream p_baos_err = new ByteArrayOutputStream();

        StreamCopier p_outStreamCopier = new StreamCopier(process.getInputStream(), p_baos_out);
        StreamCopier p_errStreamCopier = new StreamCopier(process.getErrorStream(), p_baos_err);
        (new Thread(p_outStreamCopier)).start();
        (new Thread(p_errStreamCopier)).start();
        try
        {
            int i = process.waitFor();
            p_terminalArray.write(p_baos_out.toByteArray());
            p_terminalArray.write('\r');
            p_terminalArray.write('\n');
            p_terminalArray.write(p_baos_err.toByteArray());
            p_terminalArray.write('\r');
            p_terminalArray.write('\n');
                if (i != 0) throw new IOException("Preprocessing failed rc=" + i);
        }
        catch (InterruptedException interruptedexception)
        {
            return;
        }

        sourceDir = p_preprocessDir;
    }

    void compile() throws IOException
    {
        String as[] = SourcesFinder.sourceList(sourceDir);
        if (as == null || as.length == 0) throw new IOException("There are not resources for compiling");
        File afile[] = {tmplibDir, genericTmplibDir};

        String as1[] = {
            "-bootclasspath", s_classPath, "-d", tmpClassDir.toString(), "-classpath", getClassPath(afile)
        };
        StringArray stringarray = new StringArray();
        stringarray.add(s_javac);
        stringarray.add("-target");
        stringarray.add("1.1");
        if (lg_includeDebug)
        {
            p_terminalArray.write("The debug info should be included\r\n".getBytes());
            stringarray.add("-g:lines,vars,source");
        }
        else
            stringarray.add("-g:none");
        stringarray.add("-O");
        //stringarray.add("-g:lines,vars,source");

        stringarray.append(as1);
        stringarray.append(as);

        FileUtils.deleteDir(tmpClassDir);
        FileUtils.deleteDir(classDir);
        tmpClassDir.mkdir();
        classDir.mkdir();

        //PrintWriter p_terminalWriter = new PrintWriter(p_terminalArray);

        Process process = Runtime.getRuntime().exec(stringarray.toStringArray());
        ByteArrayOutputStream p_baos_out = new ByteArrayOutputStream();
        ByteArrayOutputStream p_baos_err = new ByteArrayOutputStream();

        StreamCopier p_outStreamCopier = new StreamCopier(process.getInputStream(), p_baos_out);
        StreamCopier p_errStreamCopier = new StreamCopier(process.getErrorStream(), p_baos_err);
        (new Thread(p_outStreamCopier)).start();
        (new Thread(p_errStreamCopier)).start();
        try
        {
            int i = process.waitFor();
            p_terminalArray.write(p_baos_out.toByteArray());
            p_terminalArray.write('\r');
            p_terminalArray.write('\n');
            p_terminalArray.write(p_baos_err.toByteArray());
            p_terminalArray.write('\r');
            p_terminalArray.write('\n');
                if (i != 0) throw new IOException("Compilation failed rc=" + i);
                //throw new IOException("Error CLASSES packaging [" + i + "]");
        }
        catch (InterruptedException interruptedexception)
        {
            return;
        }

        //int i_result = Main.compile(stringarray.toStringArray(), p_terminalWriter);
        //if (i_result != 0) throw new IOException("Compilation failed rc=" + i_result);
    }

    private String getClassPath(File afile[])
    {
        StringBuffer stringbuffer = new StringBuffer(s_classPath);
        for (int i = 0; i < afile.length; i++)
        {
            String as[] = SourcesFinder.libList(afile[i]);
            if (as != null)
            {
                for (int j = 0; j < as.length; j++)
                    stringbuffer.append(File.pathSeparator).append(as[j]);
            }
        }

        return stringbuffer.toString();
    }

    public void preverifyProject() throws IOException
    {
        File afile[] = {tmplibDir, genericTmplibDir};

        String as_prev[] =
                {
                    s_preverifierPath, "-cldc", "-classpath", getClassPath(afile), "-d", classDir.toString(), tmpClassDir.toString()
                };

            Process process = Runtime.getRuntime().exec(as_prev);

                ByteArrayOutputStream p_baos_out = new ByteArrayOutputStream();
                ByteArrayOutputStream p_baos_err = new ByteArrayOutputStream();

                StreamCopier p_outStreamCopier = new StreamCopier(process.getInputStream(), p_baos_out);
                StreamCopier p_errStreamCopier = new StreamCopier(process.getErrorStream(), p_baos_err);
                (new Thread(p_outStreamCopier)).start();
                (new Thread(p_errStreamCopier)).start();


                try
                {
                    int i = process.waitFor();
                    p_terminalArray.write(p_baos_out.toByteArray());
                    p_terminalArray.write('\r');
                    p_terminalArray.write('\n');
                    p_terminalArray.write(p_baos_err.toByteArray());
                    p_terminalArray.write('\r');
                    p_terminalArray.write('\n');
                    if (i != 0)
                        throw new IOException("Preverifier failed " + i);
                }
                catch (InterruptedException interruptedexception)
                {
                    return;
                }
    }

    // The function returns the size of the created JAR file
    public int jar() throws IOException
    {
        String as_jarClasses[] =
                {
                    s_jarPath, "cmf", p_manifestFile.getAbsolutePath(), appDir.getAbsolutePath() + "/" + s_ProjectName + ".jar", "-C", classDir.toString(), "."
                };

        String as_jarRes[] =
                {
                    s_jarPath, "umf", p_manifestFile.getAbsolutePath(), appDir.getAbsolutePath() + "/" + s_ProjectName + ".jar", "-C", resDir.toString(), "."
                };

        try
        {
            Process process = Runtime.getRuntime().exec(as_jarClasses);
            ByteArrayOutputStream p_baos_out = new ByteArrayOutputStream();
            ByteArrayOutputStream p_baos_err = new ByteArrayOutputStream();

            StreamCopier p_outStreamCopier = new StreamCopier(process.getInputStream(), p_baos_out);
            StreamCopier p_errStreamCopier = new StreamCopier(process.getErrorStream(), p_baos_err);
            (new Thread(p_outStreamCopier)).start();
            (new Thread(p_errStreamCopier)).start();
            try
            {
                int i = process.waitFor();
                p_terminalArray.write(p_baos_out.toByteArray());
                p_terminalArray.write('\r');
                p_terminalArray.write('\n');
                p_terminalArray.write(p_baos_err.toByteArray());
                p_terminalArray.write('\r');
                p_terminalArray.write('\n');
                if (i != 0)
                    throw new IOException("Error CLASSES packaging [" + i + "]");
            }
            catch (InterruptedException interruptedexception)
            {
                return -1;
            }

            process = Runtime.getRuntime().exec(as_jarRes);
            p_baos_out = new ByteArrayOutputStream();
            p_baos_err = new ByteArrayOutputStream();

            p_outStreamCopier = new StreamCopier(process.getInputStream(), p_baos_out);
            p_errStreamCopier = new StreamCopier(process.getErrorStream(), p_baos_err);
            (new Thread(p_outStreamCopier)).start();
            (new Thread(p_errStreamCopier)).start();
            try
            {
                int i = process.waitFor();
                p_terminalArray.write(p_baos_out.toByteArray());
                p_terminalArray.write('\r');
                p_terminalArray.write('\n');
                p_terminalArray.write(p_baos_err.toByteArray());
                p_terminalArray.write('\r');
                p_terminalArray.write('\n');
                if (i != 0)
                    throw new IOException("Error RESOURCE packaging [" + i + "]");
            }
            catch (InterruptedException interruptedexception)
            {
                return -1;
            }

        }
        catch (Exception exception)
        {
            throw new IOException(exception.getMessage());
        }

        // Getting of the size of the created JAR file
        File p_file = new File(appDir.getAbsolutePath() + "/" + s_ProjectName + ".jar");
        if (p_file.exists())
        {
            return (int) p_file.length();
        }
        else
            return -1;
    }


}

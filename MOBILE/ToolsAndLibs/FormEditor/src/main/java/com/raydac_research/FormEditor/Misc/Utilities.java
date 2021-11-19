package com.raydac_research.FormEditor.Misc;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;

public class Utilities
{
    public static boolean askDialog(Component _parent, String _title, String _question)
    {
        if (JOptionPane.showConfirmDialog(_parent, _question, _title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) return true;
        return false;
    }

    public static final String getFileNameWithoutExt(String _fileName)
    {
        int i_point = _fileName.indexOf('.');
        if (i_point<0) return _fileName;
        else
        return _fileName.substring(0,i_point);
    }

    public static final void showErrorDialog(Component _component, String _title, String _message)
    {
        JOptionPane.showMessageDialog(_component, _message, _title,JOptionPane.ERROR_MESSAGE);
    }

    public static final void showInfoDialog(Component _component, String _title, String _message)
    {
        JOptionPane.showMessageDialog(_component, _message, _title, JOptionPane.INFORMATION_MESSAGE);
     }

    public static void toScreenCenter(Component _component)
    {
        int i_WindowWidth = _component.getWidth();
        int i_WindowHeight = _component.getHeight();

        int i_ScreenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int i_ScreenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

        int i_ScreenX = (i_ScreenWidth - i_WindowWidth) >> 1;
        int i_ScreenY = (i_ScreenHeight - i_WindowHeight) >> 1;

        _component.setLocation(i_ScreenX, i_ScreenY);
    }

    public static void toFullScreen(Component _component)
    {
        int i_ScreenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int i_ScreenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

        _component.setBounds(0, 0, i_ScreenWidth, i_ScreenHeight);
    }

    public static File selectFileForSave(Component _parent, FileFilter _fileFilter, String _title, File _initFile)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        chooser.setDragEnabled(false);
        chooser.setControlButtonsAreShown(true);
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(_fileFilter);
        chooser.setDialogTitle(_title);
        chooser.setAcceptAllFileFilterUsed(false);

        if (_initFile != null)
        {
            chooser.setCurrentDirectory(_initFile);
            chooser.setName(_initFile.getName());
        }

        int returnVal = chooser.showDialog(_parent, "Save");

        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File p_file = chooser.getSelectedFile();
            return p_file;
        }
        else
            return null;
    }

    public static boolean isAbsolutePath(String _path)
    {
        if (_path == null) return false;
        if (_path.length()<2) return false;
        if (_path.charAt(1) == ':') return true;
        return false;
    }

    public static File getFilePath(File _file)
    {
        if (_file.isDirectory()) return _file;
        String s_path = _file.getParent();
        if (s_path == null) return null;
        return new File(s_path);
    }

    public static Color selectColor(Component _parent,Color _initialColor,String _title)
    {
        Color returnVal = JColorChooser.showDialog(_parent, _title, _initialColor);
        return returnVal;
    }

    public static File selectFileForOpen(Component _parent, FileFilter _fileFilter, String _title, File _initFile)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        chooser.setDragEnabled(false);
        chooser.setControlButtonsAreShown(true);
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(_fileFilter);
        chooser.setDialogTitle(_title);
        chooser.setAcceptAllFileFilterUsed(false);

        if (_initFile != null)
        {
            chooser.setCurrentDirectory(_initFile);
            chooser.setName(_initFile.getName());
        }

        int returnVal = chooser.showDialog(_parent, "Open");

        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File p_file = chooser.getSelectedFile();
            return p_file;
        }
        else
            return null;
    }

    public static String calcRelativePath(File _src, File _dst)
    {
        String s_str1 = _src.getAbsolutePath();
        String s_str2 = _dst.getAbsolutePath();
        if (s_str1.charAt(0) != s_str2.charAt(0)) return _dst.getAbsolutePath();

        String s_waySrc = _src.getAbsolutePath();
        String s_wayDst = _dst.getAbsolutePath();

        s_waySrc = s_waySrc.substring(0,s_waySrc.length()-_src.getName().length());
        s_wayDst = s_wayDst.substring(0,s_wayDst.length()-_dst.getName().length());

        Vector p_stackSrc = new Vector();
        Vector p_stackDst = new Vector();

        StringTokenizer p_tokenizerSrc = new StringTokenizer(s_waySrc,File.separator);
        StringTokenizer p_tokenizerDst = new StringTokenizer(s_wayDst,File.separator);

        while(p_tokenizerSrc.hasMoreTokens())
        {
            String s_str = p_tokenizerSrc.nextToken();
            p_stackSrc.add(s_str);
        }

        while(p_tokenizerDst.hasMoreTokens())
        {
            String s_str = p_tokenizerDst.nextToken();
            p_stackDst.add(s_str);
        }

        int i_minSize = Math.min(p_stackSrc.size(),p_stackDst.size());
        int i_startDiffIndex = 0;

        for (int li=0;li<i_minSize;li++)
        {
            if (p_stackSrc.elementAt(li).equals(p_stackDst.elementAt(li)))
            {
                i_startDiffIndex++;
            }
            else
              break;
        }

        StringBuffer p_newWay = new StringBuffer(256);

        if (i_startDiffIndex<p_stackSrc.size())
        {
            for(int li=i_startDiffIndex;li<p_stackSrc.size();li++)
            {
                p_newWay.append("..");
                p_newWay.append(File.separatorChar);
            }
        }

        for (int li=i_startDiffIndex;li<p_stackDst.size();li++)
        {
            p_newWay.append((String)p_stackDst.elementAt(li));
            p_newWay.append((File.separatorChar));
        }

        p_newWay.append(_dst.getName());

        return p_newWay.toString();
    }



    public static ImageIcon loadIconFromResource(String _path)
    {
        return new ImageIcon(ClassLoader.getSystemResource("com/raydac_research/FormEditor/images/"+_path));
    }

}

package ru.coldcore.PixelFontMaker;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;

public class Utils
{
    public static final void toScreenCenter(Component _component)
    {
        int i_WindowWidth = _component.getWidth();
        int i_WindowHeight = _component.getHeight();

        int i_ScreenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int i_ScreenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

        int i_ScreenX = (i_ScreenWidth - i_WindowWidth) >> 1;
        int i_ScreenY = (i_ScreenHeight - i_WindowHeight) >> 1;

        _component.setLocation(i_ScreenX, i_ScreenY);
    }

    public static ImageIcon loadIconFromResource(String _path)
    {
        return new ImageIcon(ClassLoader.getSystemResource("ru/coldcore/PixelFontMaker/gui/images/" + _path));
    }

    public static void readArrayFromStream(byte [] _array, InputStream _inStream) throws IOException
    {
        int i_pos = 0;
        int i_len = _array.length;

        while(i_len>0)
        {
            int i_read = _inStream.read(_array,i_pos,i_len);
            i_pos += i_read;
            i_len -= i_read;
        }
    }

    public static byte [] loadFile(File _file) throws IOException
    {
        byte [] ab_arr = new byte[(int)_file.length()];
        FileInputStream p_fis = null;

        try
        {
            p_fis = new FileInputStream(_file);

            int i_pos = 0;
            int i_len = ab_arr.length;

            while(i_len>0)
            {
                int i_read = p_fis.read(ab_arr,i_pos,i_len);
                i_pos += i_read;
                i_len -= i_read;
            }

            return ab_arr;
        }
        finally
        {
            if (p_fis!=null)
            {
                try
                {
                    p_fis.close();
                }
                catch(Throwable _thr)
                {}
                p_fis = null;
            }
        }
    }

    public static final int mixColors(int _colorARGB, int _backgroundRGB)
    {
        int i_a = (_colorARGB >>> 24) & 0xFF;
        int i_r = (_colorARGB >>> 16) & 0xFF;
        int i_g = (_colorARGB >>> 8) & 0xFF;
        int i_b = _colorARGB & 0xFF;

        int i_bckgR = (_backgroundRGB >>> 16) & 0xFF;
        int i_bckgG = (_backgroundRGB >>> 8) & 0xFF;
        int i_bckgB = _backgroundRGB & 0xFF;

        switch (i_a)
        {
            case 255:
                return _colorARGB;
            case 0:
                return _backgroundRGB | 0xFF000000;
            default:
            {
                i_r = Math.min(255,(i_r*i_a+i_bckgR*(255-i_a))/255);
                i_g = Math.min(255,(i_g*i_a+i_bckgG*(255-i_a))/255);
                i_b = Math.min(255,(i_b*i_a+i_bckgB*(255-i_a))/255);

                return 0xFF000000 | (i_r<<16) | (i_g<<8) | i_b;
            }
        }
    }

    public static boolean askDialog(Component _parent, String _title, String _question)
    {
        if (JOptionPane.showConfirmDialog(_parent, _question, _title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) return true;
        return false;
    }
    
    public static final void showErrorDialog(Component _component, String _title, String _message)
    {
        JOptionPane.showMessageDialog(_component, _message, _title,JOptionPane.ERROR_MESSAGE);
    }

    public static final void showInfoDialog(Component _component, String _title, String _message)
    {
        JOptionPane.showMessageDialog(_component, _message, _title, JOptionPane.INFORMATION_MESSAGE);
     }

}

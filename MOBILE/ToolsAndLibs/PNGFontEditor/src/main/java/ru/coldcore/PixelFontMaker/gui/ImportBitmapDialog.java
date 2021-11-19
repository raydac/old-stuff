package ru.coldcore.PixelFontMaker.gui;

import ru.coldcore.PixelFontMaker.FileSelector;
import ru.coldcore.PixelFontMaker.Utils;
import ru.coldcore.PixelFontMaker.FontContainer;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class ImportBitmapDialog extends JDialog implements ActionListener
{
    private JTextField p_CharSetFiled;
    private JButton p_CancelButton;
    private JLabel p_PreviewImageLabel;
    private JSpinner p_CharWidth;
    private JSpinner p_CharHeight;
    private FileSelector p_SelectFileButton;
    private JPanel p_MainPanel;
    private JButton p_OkButton;
    private JScrollPane p_PreviewPanel;
    private JLabel p_LabelImageWidth;
    private JLabel p_LabelImageHeight;

    protected FontContainer p_result;

    public FontContainer getResult()
    {
        return p_result;
    }

    private static class ImageFileFilter extends FileFilter
    {

        public boolean accept(File f)
        {
            if (f == null) return false;
            if (f.isDirectory()) return true;
            String s_name = f.getName().toLowerCase();

            return s_name.endsWith(".gif")||s_name.endsWith(".jpg")||s_name.endsWith(".png");
        }

        public String getDescription()
        {
            return "PNG, GIF or JPG images";
        }
    }

    private static final FileFilter FILTER = new ImageFileFilter();

    public void setVisible(boolean _flag)
    {
        pack();
        if (_flag) Utils.toScreenCenter(this);
        super.setVisible(_flag);
    }

    public ImportBitmapDialog(Frame _owner)
    {
        super(_owner,true);

        setTitle("Import bitmap font");

        p_CharWidth.setModel(new SpinnerNumberModel(1, 1, 128, 1));
        p_CharHeight.setModel(new SpinnerNumberModel(1, 1, 128, 1));

        setContentPane(p_MainPanel);
        p_SelectFileButton.setFileFilter(FILTER);
        p_SelectFileButton.setSelectedListener(this);

        p_OkButton.addActionListener(this);
        p_CancelButton.addActionListener(this);
    }


    public void actionPerformed(ActionEvent e)
    {
        if (e == null) return;
        Object p_src = e.getSource();

        if (p_src.equals(p_SelectFileButton))
        {
            // выбран файл
            try
            {
                BufferedImage p_icoImg = ImageIO.read(p_SelectFileButton.getFile());
                p_PreviewImageLabel.setIcon(new ImageIcon(p_icoImg));
                 p_PreviewImageLabel.invalidate();
                 p_PreviewPanel.repaint();

                p_LabelImageWidth.setText(p_icoImg.getWidth()+"");
                p_LabelImageHeight.setText(p_icoImg.getHeight()+"");

            }
            catch(Throwable _thr)
            {
                _thr.printStackTrace();
                Utils.showErrorDialog(this,"Can't load an image","I can't load image file \'"+p_SelectFileButton.getFile().getAbsolutePath()+"\'");
                p_SelectFileButton.setFile(null);
                return;
            }
         }
        else
        if (p_src.equals(p_CancelButton))
        {
            p_result = null;
            setVisible(false);
        }
        else
        if (p_src.equals(p_OkButton))
        {
            // проверяем данные
            BufferedImage p_bi = (BufferedImage)((ImageIcon) p_PreviewImageLabel.getIcon()).getImage();
            int i_charW = ((Integer)p_CharWidth.getValue()).intValue();
            int i_charH = ((Integer)p_CharHeight.getValue()).intValue();
            String s_charSet = p_CharSetFiled.getText();

            if (s_charSet.length()==0)
            {
                Utils.showErrorDialog(this,"Error parameter","You must not have empty char set string!");
                return;
            }

            if ((p_bi.getWidth() % i_charW)!=0)
            {
                Utils.showErrorDialog(this,"Error parameter","You have wrong char width!");
                return;
            }

            int i_charsPerLine = p_bi.getWidth() / i_charW;
            int i_lines = s_charSet.length() / i_charsPerLine;
            int i_calculatedHeight = i_lines * i_charH;

            if (i_calculatedHeight>p_bi.getHeight())
            {
                Utils.showErrorDialog(this,"Error parameter","You have wrong char height!");
                return;
            }

            setVisible(false);

            FontContainer p_fontContainer = new FontContainer(p_bi,p_SelectFileButton.getFile(),i_charW,i_charH,s_charSet);
            p_result = p_fontContainer;
        }
    }
}

package ru.coldcore.PixelFontMaker.gui;

import ru.coldcore.PixelFontMaker.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class MainGUIForm extends JFrame implements ActionListener
{
    private JPanel MainPanel;
    private JTextField p_CharSetField;
    private JButton p_LoadFontButton;
    private JButton p_SaveFontButton;
    private JButton p_MakeFontButton;
    private JButton p_NewFontButton;
    private JButton p_AboutFontButton;
    private JCheckBox p_TransparentBackgroundFlag;
    private JButton p_SetTheCharsetButton;
    private JButton p_SetThePropertiesButton;
    private JSpinner p_HorzImageWidth;
    private JSpinner p_VertImageHeight;
    private JCheckBox p_EnableHorzAlignment;
    private JCheckBox p_EnableVertAlignment;
    private JSpinner p_VertSpace;
    private JSpinner p_HorzSpace;
    private JCheckBox p_SmoothFlag;
    private JCheckBox p_FlagBorder;
    private JSpinner p_FontSizeSpinner;
    private JCheckBox p_FontBoldFlag;
    private JCheckBox p_FontItalicFlag;
    private JCheckBox p_TransparentForegroundFlag;
    private JComboBox p_ZoomComboBox;
    private JLabel p_LabelMaxHeight;
    private JLabel p_LabelMaxWidth;
    private JButton p_ImportBitmapImage;
    private PreviewPanel p_PrevPanel;
    private FileSelector p_FontFileSelector;
    private ColorSelector p_BackgroundColorSelector;
    private ColorSelector p_ForegroundColorSelector;
    private ColorSelector p_BorderColorSelector;
    

    public FontContainer p_curFontContainer;

    public static final String DEFAULT_CHARSET = " 0123456789:.,!?+-/\'\"()ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзийклмнопрстуфхцчшщъыьэюя";
    public static final Color DEFAULT_BACKGROUND = Color.WHITE;
    public static final Color DEFAULT_FOREGROUND = Color.BLACK;
    public static final Color DEFAULT_BORDER = Color.RED;

    private File p_LastProcessedFile = null;

    private static class TTFFileFilter extends FileFilter
    {

        public boolean accept(File f)
        {
            if (f == null) return false;
            if (f.isDirectory()) return true;
            return f.getName().toLowerCase().endsWith(".ttf");
        }

        public String getDescription()
        {
            return "TTF font files";
        }
    }

    private static class BinFontFileFilter extends FileFilter
    {
        public File processFileName(File _file)
        {
            if (accept(_file)) return _file;
            _file = new File(_file.getAbsolutePath() + ".bfn");
             return _file;
        }

        public boolean accept(File f)
        {
            if (f == null) return false;
            if (f.isDirectory()) return true;
            return f.getName().toLowerCase().endsWith(".bfn");
        }

        public String getDescription()
        {
            return "BFN font files";
        }
    }

    private class JFontFileFilter extends FileFilter
    {
        public File processFileName(File _file)
        {
            if (accept(_file)) return _file;
            _file = new File(_file.getAbsolutePath() + ".jfont");
            return _file;
        }


        public boolean accept(File f)
        {
            if (f == null) return false;
            if (f.isDirectory()) return true;
            return f.getName().toLowerCase().endsWith(".jfont");
        }

        public String getDescription()
        {
            return "JFONT files";
        }
    }

    public static FileFilter p_TTFFileFilter = new TTFFileFilter();
    private JFontFileFilter p_JFontFileFilter = new JFontFileFilter();
    private BinFontFileFilter p_BinFontFileFilter = new BinFontFileFilter();

    public MainGUIForm()
    {
        super();

        setContentPane(MainPanel);
        setTitle(Main.NAME + Main.VERSION);

        p_curFontContainer = new FontContainer(DEFAULT_CHARSET);
        p_curFontContainer.setBackground(DEFAULT_BACKGROUND);
        p_curFontContainer.setForeground(DEFAULT_FOREGROUND);
        p_curFontContainer.setBorder(DEFAULT_BORDER);
        p_curFontContainer.setImageWidth(128);
        p_curFontContainer.setImageHeight(128);
        p_curFontContainer.setQuality(false);
        p_curFontContainer.updateChars();

        p_PrevPanel.setFontContainer(p_curFontContainer);

        p_SetTheCharsetButton.addActionListener(this);
        p_SetThePropertiesButton.addActionListener(this);

        p_AboutFontButton.addActionListener(this);
        p_LoadFontButton.addActionListener(this);
        p_SaveFontButton.addActionListener(this);
        p_MakeFontButton.addActionListener(this);
        p_NewFontButton.addActionListener(this);
        p_ImportBitmapImage.addActionListener(this);

        p_FontSizeSpinner.setModel(new SpinnerNumberModel(8, 1, 80, 1));
        p_VertImageHeight.setModel(new SpinnerNumberModel(128, 1, 320, 1));
        p_HorzImageWidth.setModel(new SpinnerNumberModel(128, 1, 320, 1));
        p_HorzSpace.setModel(new SpinnerNumberModel(0, 0, 32, 1));
        p_VertSpace.setModel(new SpinnerNumberModel(0, -32, 32, 1));

        p_FontFileSelector.setFileFilter(p_TTFFileFilter);

        p_ZoomComboBox.removeAllItems();
        for(int li=1;li<7;li++)
        {
           p_ZoomComboBox.addItem("  x"+li);
        }

        fillFromFontContainer(p_curFontContainer);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        p_ZoomComboBox.setSelectedIndex(0);

        p_ZoomComboBox.addActionListener(this);

        setIconImage(Utils.loadIconFromResource("icon.gif").getImage());
    }

    public void fillFromFontContainer(FontContainer _container)
    {
        if (_container.isBitmapFont())
        {
           p_CharSetField.setEnabled(false);
           p_FontSizeSpinner.setEnabled(false);
           p_SmoothFlag.setEnabled(false);
            p_SetTheCharsetButton.setEnabled(false);

            p_FontBoldFlag.setEnabled(false);
            p_FontItalicFlag.setEnabled(false);
            p_FontFileSelector.setEnabled(false);
            p_FontSizeSpinner.setEnabled(false);
            p_ForegroundColorSelector.setEnabled(false);
        }
        else
        {
            p_CharSetField.setEnabled(true);
            p_FontSizeSpinner.setEnabled(true);
            p_SmoothFlag.setEnabled(true);
            p_SetTheCharsetButton.setEnabled(true);

            p_FontBoldFlag.setEnabled(true);
               p_FontItalicFlag.setEnabled(true);
               p_FontFileSelector.setEnabled(true);
               p_FontSizeSpinner.setEnabled(true);
               p_ForegroundColorSelector.setEnabled(true);
           }

        p_BackgroundColorSelector.setColor(_container.getBackgroundColor());
        p_BorderColorSelector.setColor(_container.getBorderColor());
        p_ForegroundColorSelector.setColor(_container.getForegroundColor());
        p_CharSetField.setText(_container.getCharSet());
        p_FontSizeSpinner.setValue(new Integer(_container.getSize()));
        p_HorzImageWidth.setValue(new Integer(_container.getImageWidth()));
        p_VertImageHeight.setValue(new Integer(_container.getImageHeight()));
        p_FlagBorder.setSelected(_container.isBorderEnable());
        p_TransparentBackgroundFlag.setSelected(_container.isTransparentBackground());
        p_TransparentForegroundFlag.setSelected(_container.isTransparentForeground());
        p_SmoothFlag.setSelected(_container.isQuality());
        p_VertSpace.setValue(new Integer(_container.getVertInterval()));
        p_HorzSpace.setValue(new Integer(_container.getHorzInterval()));
        p_EnableVertAlignment.setSelected(_container.isCropVertical());
        p_EnableHorzAlignment.setSelected(_container.isCropHorizontal());

        p_LabelMaxWidth.setText(""+_container.getMaxCharWidth()+" px");
        p_LabelMaxHeight.setText(""+_container.getMaxCharHeight()+" px");
    }

    private void createUIComponents()
    {
    }

    public void actionPerformed(ActionEvent _e)
    {
        Object p_src = _e.getSource();

        if (p_src == null) return;
        if (p_src.equals(p_SetTheCharsetButton))
        {
            // Присвоить новый набор символов
            setCharset();
        }
        else
            if (p_src.equals(p_ImportBitmapImage))
            {
                ImportBitmapDialog p_Dialog = new ImportBitmapDialog(this);
                p_Dialog.setVisible(true);
                if (p_Dialog.getResult()!=null)
                {
                    p_curFontContainer = p_Dialog.getResult();
                    p_PrevPanel.setFontContainer(p_curFontContainer);
                    fillFromFontContainer(p_curFontContainer);
                    p_PrevPanel.repaint();
                }
            }
            else
            if (p_src.equals(p_SetThePropertiesButton))
            {
                // Присвоить новые свойства символов
                setProperties();
            }
            else
                if (p_src.equals(p_MakeFontButton))
                {
                    // выбираем файл для записи
                    JFileChooser chooser = new JFileChooser();
                    chooser.setMultiSelectionEnabled(false);
                    chooser.setDragEnabled(false);
                    chooser.setControlButtonsAreShown(true);
                    chooser.setDialogType(JFileChooser.SAVE_DIALOG);
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    chooser.setFileFilter(p_BinFontFileFilter);
                    chooser.setDialogTitle("Save as..");
                    chooser.setAcceptAllFileFilterUsed(false);

                    chooser.setCurrentDirectory(ExportBINFont.p_LastDir);

                    int returnVal = chooser.showDialog(this, "Save");

                    if (returnVal == JFileChooser.APPROVE_OPTION)
                    {
                        File p_file = chooser.getSelectedFile();
                        p_file = p_BinFontFileFilter.processFileName(p_file);

                        try
                        {
                            ExportBINFont.export(p_curFontContainer,p_file,this);
                        }
                        catch (Throwable _thr)
                        {
                            Utils.showErrorDialog(this, "Can't save font file", _thr.getMessage());
                        }
                    }
                }
                else
                if (p_src.equals(p_AboutFontButton))
                {
                    new aboutform().setVisible(true);
                }
                else
                    if (p_src.equals(p_SaveFontButton))
                    {
                        // записываем файл
                        JFileChooser chooser = new JFileChooser();
                        chooser.setMultiSelectionEnabled(false);
                        chooser.setDragEnabled(false);
                        chooser.setControlButtonsAreShown(true);
                        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
                        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        chooser.setFileFilter(p_JFontFileFilter);
                        chooser.setDialogTitle("Save as..");
                        chooser.setAcceptAllFileFilterUsed(false);

                        chooser.setCurrentDirectory(p_LastProcessedFile);

                        int returnVal = chooser.showDialog(this, "Save");

                        if (returnVal == JFileChooser.APPROVE_OPTION)
                        {
                            File p_file = chooser.getSelectedFile();
                            p_file = p_JFontFileFilter.processFileName(p_file);

                            try
                            {
                                FileOutputStream p_fos = null;

                                try
                                {
                                    p_fos = new FileOutputStream(p_file);
                                    p_curFontContainer.saveToStream(p_fos);
                                    p_fos.flush();
                                    p_fos.close();
                                    setTitle(p_file.getAbsolutePath());
                                }
                                finally
                                {
                                    if (p_fos != null)
                                    {
                                        try
                                        {
                                            p_fos.close();
                                        }
                                        catch (Throwable _thr)
                                        {
                                        }
                                        p_fos = null;
                                    }
                                }

                            }
                            catch (Throwable _thr)
                            {
                                Utils.showErrorDialog(this, "Can't save file", _thr.getMessage());
                            }
                        }
                    }
                    else
                        if (p_src.equals(p_NewFontButton))
                        {
                            if (Utils.askDialog(this, "Are you sure?", "Do you really want to make new font?"))
                            {
                                p_curFontContainer = new FontContainer(DEFAULT_CHARSET);
                                fillFromFontContainer(p_curFontContainer);
                                p_PrevPanel.setFontContainer(p_curFontContainer);
                                p_PrevPanel.repaint();
                                setTitle("<Untitled>");
                            }
                        }
                        else
                            if (p_src.equals(p_ZoomComboBox))
                            {
                                p_PrevPanel.setScale(p_ZoomComboBox.getSelectedIndex()+1);
                                p_PrevPanel.repaint();
                            }
                            else
                            if (p_src.equals(p_LoadFontButton))
                            {
                                // загружаем фонт
                                JFileChooser chooser = new JFileChooser();
                                chooser.setMultiSelectionEnabled(false);
                                chooser.setDragEnabled(false);
                                chooser.setControlButtonsAreShown(true);
                                chooser.setDialogType(JFileChooser.OPEN_DIALOG);
                                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                                chooser.setFileFilter(p_JFontFileFilter);
                                chooser.setDialogTitle("Load font..");
                                chooser.setAcceptAllFileFilterUsed(false);

                                chooser.setCurrentDirectory(p_LastProcessedFile);

                                int returnVal = chooser.showDialog(this, "Open");

                                if (returnVal == JFileChooser.APPROVE_OPTION)
                                {
                                    File p_file = chooser.getSelectedFile();

                                    try
                                    {
                                        FileInputStream p_fis = null;

                                        try
                                        {
                                            p_fis = new FileInputStream(p_file);
                                            FontContainer p_newCont = new FontContainer("000");
                                            p_newCont.loadFromStream(p_fis);
                                            p_curFontContainer = p_newCont;
                                            setTitle(p_file.getAbsolutePath());
                                            p_PrevPanel.setFontContainer(p_curFontContainer);
                                            fillFromFontContainer(p_curFontContainer);
                                            p_PrevPanel.repaint();

                                        }
                                        finally
                                        {
                                            if (p_fis != null)
                                            {
                                                try
                                                {
                                                    p_fis.close();
                                                }
                                                catch (Throwable _thr)
                                                {
                                                }
                                                p_fis = null;
                                            }
                                        }

                                    }
                                    catch (Throwable _thr)
                                    {
                                        Utils.showErrorDialog(this, "Can't open file", _thr.getMessage());
                                    }
                                }

                            }
    }

    public void setProperties()
    {
        // проверяем правильность данных
        int i_fontSize = ((Number) p_FontSizeSpinner.getValue()).intValue();
        int i_imgWidth = ((Number) p_HorzImageWidth.getValue()).intValue();
        int i_imgHeight = ((Number) p_VertImageHeight.getValue()).intValue();
        int i_horzInterval = ((Number) p_HorzSpace.getValue()).intValue();
        int i_vertInterval = ((Number) p_VertSpace.getValue()).intValue();

        // загружаем фонт
        File p_font = p_FontFileSelector.getFile();
        if (p_font != null)
        {
            FileInputStream p_inStream = null;
            Font p_fontTTF = null;
            try
            {
                p_inStream = new FileInputStream(p_font);
                p_fontTTF = Font.createFont(Font.TRUETYPE_FONT, p_inStream);
            }
            catch (Throwable _thr)
            {
                Utils.showErrorDialog(this, "Error", _thr.getMessage());
                _thr.printStackTrace();
                return;
            }
            finally
            {
                if (p_inStream != null)
                {
                    try
                    {
                        p_inStream.close();
                    }
                    catch (Throwable _t)
                    {
                    }
                    p_inStream = null;
                }
            }

            p_curFontContainer.setFont(p_fontTTF, p_font);

            p_curFontContainer.setStyle((p_FontBoldFlag.isSelected() ? Font.BOLD : Font.PLAIN) | (p_FontItalicFlag.isSelected() ? Font.ITALIC : Font.PLAIN));
            p_curFontContainer.setSize(i_fontSize);
        }

        p_curFontContainer.setBorder(p_BorderColorSelector.getColor());
        p_curFontContainer.setBorderEnable(p_FlagBorder.isSelected());

        p_curFontContainer.setQuality(p_SmoothFlag.isSelected());

        p_curFontContainer.setHorzInterval(i_horzInterval);
        p_curFontContainer.setVertInterval(i_vertInterval);

        p_curFontContainer.setImageWidth(i_imgWidth);
        p_curFontContainer.setImageHeight(i_imgHeight);

        p_curFontContainer.setCropVertical(p_EnableVertAlignment.isSelected());
        p_curFontContainer.setCropHorizontal(p_EnableHorzAlignment.isSelected());

        p_curFontContainer.setTransparentBackground(p_TransparentBackgroundFlag.isSelected());
        p_curFontContainer.setTransparentForeground(p_TransparentForegroundFlag.isSelected());

        p_curFontContainer.setBackground(p_BackgroundColorSelector.getColor());
        p_curFontContainer.setForeground(p_ForegroundColorSelector.getColor());

        p_curFontContainer.updateChars();
        p_PrevPanel.repaint();

        p_LabelMaxWidth.setText(""+p_curFontContainer.getMaxCharWidth()+" px.");
        p_LabelMaxHeight.setText(""+p_curFontContainer.getMaxCharHeight()+" px.");

    }

    public void setCharset()
    {
        String s_str = p_CharSetField.getText();
        p_curFontContainer.setCharacters(s_str);
        p_curFontContainer.updateChars();
        p_PrevPanel.repaint();
    }
}

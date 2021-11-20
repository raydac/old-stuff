package ru.coldcore.PixelFontMaker;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class FileSelector extends JButton implements ActionListener
{
    protected File p_SelectedFile;
    protected FileFilter p_fileFilter;
    protected ActionListener p_actListener;

    public void setSelectedListener(ActionListener _listener)
    {
       p_actListener = _listener;
    }

    public File getFile()
    {
        return p_SelectedFile;
    }

    public void setFileFilter(FileFilter _filter)
    {
        p_fileFilter = _filter;
    }

    public void setFile(File _file)
    {
        p_SelectedFile = _file;
        if (_file == null)
        {
            setText("...");
            setToolTipText("Not selected");
        }
        else
        {
            setText(_file.getName());
            setToolTipText(_file.getAbsolutePath());
        }
    }

    public FileSelector()
    {
        super();
        setFile(null);
        addActionListener(this);
    }

    public void actionPerformed(ActionEvent e)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        chooser.setDragEnabled(false);
        chooser.setControlButtonsAreShown(true);
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(p_fileFilter);
        chooser.setDialogTitle("Select a font file");
        chooser.setAcceptAllFileFilterUsed(false);

        if (p_SelectedFile != null)
        {
            chooser.setCurrentDirectory(p_SelectedFile);
        }

        int returnVal = chooser.showDialog(this, "Open");

        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File p_file = chooser.getSelectedFile();
            setFile(p_file);
            if (p_actListener!=null)
            {
                p_actListener.actionPerformed(e);
            }
        }
    }
}

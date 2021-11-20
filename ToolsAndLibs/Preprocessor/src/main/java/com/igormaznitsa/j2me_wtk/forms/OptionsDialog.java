package com.igormaznitsa.j2me_wtk.forms;

import com.igormaznitsa.j2me_wtk.appProperties;
import com.raydac_research.FormEditor.Misc.Utilities;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;

public class OptionsDialog extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField p_Text_Jar;
    private JTextField p_Text_Javac;
    private JTextField p_Text_MIDP10;
    private JTextField p_Text_MIDP20;
    private JTextField p_Text_CLDC10;
    private JTextField p_Text_CLDC11;
    private JTextField p_Text_AddLibs;
    private JTextField p_Text_Preverif;
    private JTextField p_Text_DestDir;
    private JTextField p_Text_Obfuscator;

    public OptionsDialog(JFrame _parent)
    {
        super(_parent);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        setTitle("Options and paths");

        buttonOK.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
}

    public void showDialog()
    {
        pack();

        p_Text_AddLibs.setText(appProperties.AddLibPath);
        p_Text_CLDC10.setText(appProperties.CLDC10Path);
        p_Text_CLDC11.setText(appProperties.CLDC11Path);
        p_Text_MIDP10.setText(appProperties.MIDP10Path);
        p_Text_MIDP20.setText(appProperties.MIDP20Path);
        p_Text_DestDir.setText(appProperties.DestinationDir);
        p_Text_Jar.setText(appProperties.JarPath);
        p_Text_Javac.setText(appProperties.JavacPath);
        p_Text_Obfuscator.setText(appProperties.ObfuscatorPath);
        p_Text_Preverif.setText(appProperties.PreverifierPath);

        Utilities.toScreenCenter(this);

        setVisible(true);
    }

    private void onOK()
    {
        appProperties.AddLibPath = p_Text_AddLibs.getText().trim();
        appProperties.CLDC10Path = p_Text_CLDC10.getText().trim();
        appProperties.CLDC11Path = p_Text_CLDC11.getText().trim();
        appProperties.MIDP10Path = p_Text_MIDP10.getText().trim();
        appProperties.MIDP20Path = p_Text_MIDP20.getText().trim();
        appProperties.DestinationDir = p_Text_DestDir.getText().trim();
        appProperties.JarPath = p_Text_Jar.getText().trim();
        appProperties.JavacPath = p_Text_Javac.getText().trim();
        appProperties.ObfuscatorPath = p_Text_Obfuscator.getText().trim();
        appProperties.PreverifierPath = p_Text_Preverif.getText().trim();

        try
        {
            appProperties.save();
        }
        catch (IOException e)
        {
            Utilities.showErrorDialog(null,"Saving error","I can't save INI file.");
            return;
        }

        dispose();
    }

    private void onCancel()
    {
        dispose();
    }
}

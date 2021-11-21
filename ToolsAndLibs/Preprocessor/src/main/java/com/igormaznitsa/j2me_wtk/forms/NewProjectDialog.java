package com.igormaznitsa.j2me_wtk.forms;

import com.igormaznitsa.j2me_wtk.appProperties;
import com.igormaznitsa.j2me_wtk.ProjectsTreeContainer;
import com.raydac_research.FormEditor.Misc.Utilities;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class NewProjectDialog extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField p_ProjectNameField;
    private JTextField p_ClassNameField;
    private JComboBox p_ListMidpVersions;
    private JComboBox p_ListCLDCVersions;

    private ProjectsTreeContainer p_ProjectsContainer;

    public NewProjectDialog(JFrame _parent)
    {
        super(_parent);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        p_ListMidpVersions.addItem("MIDP-1.0");
        p_ListMidpVersions.addItem("MIDP-2.0");

        p_ListCLDCVersions.addItem("CLDC-1.0");
        p_ListCLDCVersions.addItem("CLDC-1.1");

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

    public void showDialog(ProjectsTreeContainer _container)
    {
        pack();

        p_ProjectsContainer = _container;

//        Utilities.toScreenCenter(this);

        p_ProjectNameField.setText("");
        p_ClassNameField.setText("");

        p_ListCLDCVersions.setSelectedIndex(0);
        p_ListMidpVersions.setSelectedIndex(0);

        setTitle("Make new project");

        setVisible(true);
    }


    private void onOK()
    {
        String s_projectName = p_ProjectNameField.getText().trim();
        String s_projectiClassName = p_ClassNameField.getText().trim();

        String s_midp = (String)p_ListMidpVersions.getSelectedItem();
        String s_cldc = (String)p_ListCLDCVersions.getSelectedItem();

        if (s_projectName.length()==0)
        {
            Utilities.showErrorDialog(this,"Project name not found","You must have a project name!");
            return;
        }

        if (s_projectiClassName.length()==0)
        {
            Utilities.showErrorDialog(this,"Class name not found","You must have a class name!");
            return;
        }

        // проверяем есть ли уже проект с таким именем
        File p_dirFile = new File(p_ProjectsContainer.p_MainDirectory,s_projectName);

        if (p_dirFile.exists())
        {
            Utilities.showInfoDialog(this,"Directory exists","Sorry but a directory with the name exists already");
            return;
        }

        // создаем
        if (!p_dirFile.mkdirs())
        {
            Utilities.showErrorDialog(this,"Can't create","I can't create the project directory ["+p_dirFile.getAbsolutePath()+"]");
            return;
        }

        // создаем подкаталоги
        String [] as_subdirs = new String[]{"res","src","bin"};
        File p_dir = null;
        for(int li=0;li<as_subdirs.length;li++)
        {
            String s_name = as_subdirs[li];
            p_dir = new File(p_dirFile,s_name);
            if (!p_dir.mkdirs())
            {
                Utilities.showErrorDialog(this,"Can't create","I can't create a directory ["+p_dir.getAbsolutePath()+"]");
                return;
            }
        }

        // создаем файл проекта
        p_dir = new File(p_dir,s_projectName+".jdd");
        FileOutputStream p_fos = null;
        try
        {
            p_fos = new FileOutputStream(p_dir);
            PrintStream p_pr = new PrintStream(p_fos);

            // выводим данные

            p_pr.println("[SECTION_PARAMETERS]");
            p_pr.println("MIDlet-1=%PROJNAME%,/ico.png,%CLASSNAME%".replaceAll("%PROJNAME%",s_projectName).replaceAll("%CLASSNAME%",s_projectiClassName));
            p_pr.println("MIDlet-Jar-Size=0");
            p_pr.println("MIDlet-Jar-URL=%PROJNAME%.jar".replaceAll("%PROJNAME%",s_projectName));
            p_pr.println("MIDlet-Name=%PROJNAME%".replaceAll("%PROJNAME%",s_projectName));
            p_pr.println("MIDlet-Vendor=UNKNOWN");
            p_pr.println("MIDlet-Version=1.0.1");
            p_pr.println("MicroEdition-Configuration=%CLDC%".replaceAll("%CLDC%",s_cldc));
            p_pr.println("MicroEdition-Profile=%MIDP%".replaceAll("%MIDP%",s_midp));
            p_pr.println("Created-By=RRGWTK");
            p_pr.println("MIDlet-Icon=/ico.png");
            p_pr.println("MIDlet-Description=None");
            p_pr.println("[END]");
            p_pr.println("[SECTION_ORDER_JAD]");
            p_pr.println("MIDlet-Name");
            p_pr.println("MIDlet-Vendor");
            p_pr.println("MIDlet-1");
            p_pr.println("MIDlet-Jar-URL");
            p_pr.println("MIDlet-Jar-Size");
            p_pr.println("MIDlet-Version");
            p_pr.println("MicroEdition-Configuration");
            p_pr.println("MicroEdition-Profile");
            p_pr.println("[END]");
            p_pr.println("[SECTION_ORDER_MF]");
            p_pr.println("[END]");
            p_pr.flush();
            p_pr.close();
        }
        catch (Throwable e)
        {
            Utilities.showErrorDialog(this,"Can't create","I can't create a directory ["+p_dir.getAbsolutePath()+"]");
            return;
        }
        finally
        {
            if (p_fos!=null)
            {
                try
                {
                    p_fos.close();
                }
                catch (Throwable e)
                {
                }
                p_fos = null;
            }
        }

        p_ProjectsContainer.setMainDirectory(p_ProjectsContainer.p_MainDirectory);

        dispose();
    }

    private void onCancel()
    {
        // add your code here if necessary
        dispose();
    }
}

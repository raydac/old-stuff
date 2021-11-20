package com.igormaznitsa.j2me_wtk;

import com.raydac_research.FormEditor.Misc.Utilities;
import com.raydac_research.FormEditor.Misc.AboutForm;
import com.igormaznitsa.j2me_wtk.forms.NewVarDialog;
import com.igormaznitsa.j2me_wtk.forms.OptionsDialog;
import com.igormaznitsa.j2me_wtk.forms.NewProjectDialog;

import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class mainForm extends JFrame implements ActionListener,TreeSelectionListener,FocusListener,BuilderModule.ProjectProcessingListener
{
    private JTextPane p_TerminalWindow;
    private JTree p_ProjectsTree;
    private JButton p_Button_SelectDirectory;
    private JButton p_Button_Build;
    private JButton p_Button_NewProject;
    private JTable p_MidletPropertiesTable;
    private JButton p_Button_About;
    private JCheckBox p_Check_Debug;
    private JCheckBox p_Check_Obfuscating;
    private JCheckBox p_Check_RemoveComments;
    private JPanel p_MainPanel;

    private PropertiesTableModel p_propTableModel;
    private VariablesTableModel p_variableTableModel;
    private JButton p_Button_Options;

    private ProjectsTreeContainer p_projectsContainer;

    private boolean lg_busy;

    private NewVarDialog p_newVarDialog;
    private OptionsDialog p_optDialog;
    private NewProjectDialog p_NewProjectDialog;

    private JCheckBox p_Check_Optimization;
    private JButton p_Button_AddVar;
    private JButton p_Button_RemoveVar;
    private JTable p_VarTable;

    private static final String STORAGE_FILE = "wtkrrg.ini";
    private JButton p_Button_AddMIDLETProperty;
    private JButton p_Button_RemoveMIDLETProperty;

    private JPanel p_ProgressPanel;

    private JProgressBar p_ProgressBar;
    private JCheckBox p_Check_JarOptimization;

    public mainForm()
    {
        BuilderModule.p_ProcessListener = this;

        p_ProgressBar = new JProgressBar(JProgressBar.HORIZONTAL,0,100);
        p_ProgressPanel.setLayout(new BorderLayout(0,0));
        p_ProgressPanel.add(p_ProgressBar,BorderLayout.CENTER);
        p_ProgressBar.setValue(0);
        p_ProgressBar.setToolTipText("Progress of project compilation");

        p_newVarDialog = new NewVarDialog(this);
        p_optDialog = new OptionsDialog(this);
        p_NewProjectDialog = new NewProjectDialog(this);

        File p_stFile = new File(STORAGE_FILE);
        try
        {
            if (!appProperties.loadFromFile(p_stFile))
            {
                appProperties.initDefault();
            }
        }
        catch (IOException e)
        {
            Utilities.showErrorDialog(this,"Init error","Load error of init file");
            appProperties.initDefault();
        }

        p_Check_Debug.setSelected(appProperties.Debug);
        p_Check_Obfuscating.setSelected(appProperties.Obfuscating);
        p_Check_Optimization.setSelected(appProperties.Optimizing);
        p_Check_RemoveComments.setSelected(appProperties.RemoveComments);
        p_Check_JarOptimization.setSelected(appProperties.JarOptimizing);

        p_projectsContainer = new ProjectsTreeContainer();

        p_Button_SelectDirectory.addActionListener(this);
        p_Button_Build.addActionListener(this);
        p_Button_NewProject.addActionListener(this);
        p_Button_About.addActionListener(this);
        p_Check_Debug.addActionListener(this);
        p_Check_Obfuscating.addActionListener(this);
        p_Check_JarOptimization.addActionListener(this);
        p_Check_Optimization.addActionListener(this);
        p_Button_Options.addActionListener(this);
        p_Check_RemoveComments.addActionListener(this);
        p_Button_AddVar.addActionListener(this);
        p_Button_RemoveVar.addActionListener(this);
        p_Button_RemoveMIDLETProperty.addActionListener(this);
        p_Button_AddMIDLETProperty.addActionListener(this);

        p_propTableModel = new PropertiesTableModel();
        p_variableTableModel = new VariablesTableModel();
        p_MidletPropertiesTable.setModel(p_propTableModel);
        p_VarTable.setModel(p_variableTableModel);

        p_ProjectsTree.setModel(p_projectsContainer);
        p_ProjectsTree.addTreeSelectionListener(this);

        p_VarTable.addFocusListener(this);
        p_MidletPropertiesTable.addFocusListener(this);

        setContentPane(p_MainPanel);
        pack();

        Utilities.toFullScreen(this);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        p_projectsContainer.setMainDirectory(appProperties.MainDirectory);

        if (p_projectsContainer.p_MainDirectory==null)
        {
            p_Button_NewProject.setEnabled(false);
        }

        updateButtonState();

        setIconImage(Utilities.loadIconFromResource("ico.gif").getImage());

        setTitle();
        setVisible(true);
    }

    public synchronized void actionPerformed(ActionEvent e)
    {
        if (lg_busy) return;
        lg_busy = true;


        Object p_src = e.getSource();

        if (p_Button_About.equals(p_src))
        {
           new AboutForm(this);
           lg_busy = false;
        }
        else
        if (p_Button_Build.equals(p_src))
        {
            new Thread(new Runnable()
            {
                public void run()
                {
                    buildProjects();
                }
            }
            ).start();
        }
        else
        if (p_Button_NewProject.equals(p_src))
        {
            p_NewProjectDialog.showDialog(p_projectsContainer);
            lg_busy = false;
        }
        else
        if (p_Button_Options.equals(p_src))
        {
            p_optDialog.showDialog();
            lg_busy = false;
        }
        else
        if (p_Button_SelectDirectory.equals(p_src))
        {
            selectMainDirectory();

            lg_busy = false;
        }
        else
        if (p_Button_AddVar.equals(p_src))
        {
            addVariable();
            lg_busy = false;
        }
        else
        if (p_Button_RemoveVar.equals(p_src))
        {
            removeVariable();
            lg_busy = false;
        }
        else
        if (p_Button_AddMIDLETProperty.equals(p_src))
        {
            addMidletProperty();
            lg_busy = false;
        }
        else
        if (p_Button_RemoveMIDLETProperty.equals(p_src))
        {
            removeMidletProperty();
            lg_busy = false;
        }
        else
        {
            if (p_src instanceof JCheckBox)
            {
                appProperties.Debug = p_Check_Debug.isSelected();
                appProperties.Obfuscating = p_Check_Obfuscating.isSelected();
                appProperties.JarOptimizing = p_Check_JarOptimization.isSelected();
                appProperties.Optimizing = p_Check_Optimization.isSelected();
                appProperties.RemoveComments = p_Check_RemoveComments.isSelected();
                saveINI();
            }
            lg_busy = false;
        }
    }

    private void addMidletProperty()
    {
        TreePath p_path = p_ProjectsTree.getSelectionPath();
        ProjectInfo p_proj = (ProjectInfo) p_path.getLastPathComponent();

        if (p_newVarDialog.showDialog("New JAD property",true,"","",p_proj.getCustomVariables()))
        {
            String s_name = p_newVarDialog.getName();
            String s_value = p_newVarDialog.getValue();

            try
            {
                p_proj.addJDDVariable(s_name,s_value);
            }
            catch (IOException e)
            {
                Utilities.showErrorDialog(this,"Save error","I can't save JDD.");
            }

            p_propTableModel.updated();
        }
     }

    private void removeMidletProperty()
    {
        TreePath p_path = p_ProjectsTree.getSelectionPath();
        ProjectInfo p_proj = (ProjectInfo) p_path.getLastPathComponent();

        if (Utilities.askDialog(this,"Confirmation","Are you sure?"))
        {
            String s_name = (String)p_proj.p_propertiesNames.elementAt(p_MidletPropertiesTable.getSelectedRow());
            try
            {
                p_proj.removeJDDVariable(s_name);
            }
            catch (IOException e)
            {
                Utilities.showErrorDialog(this,"Save error","I can't save JDD.");
            }

            p_propTableModel.updated();
        }
    }

    private void addVariable()
    {
        TreePath p_path = p_ProjectsTree.getSelectionPath();
        ProjectInfo p_proj = (ProjectInfo) p_path.getLastPathComponent();

        if (p_newVarDialog.showDialog("New variable",true,"","",p_proj.getCustomVariables()))
        {
            String s_name = p_newVarDialog.getName();
            String s_value = p_newVarDialog.getValue();

            try
            {
                p_proj.addCustomVariable(s_name,s_value);
            }
            catch (IOException e)
            {
                Utilities.showErrorDialog(this,"Save error","I can't save custom variable file.");
            }

            p_variableTableModel.updated();
        }
    }

    private void removeVariable()
    {
        TreePath p_path = p_ProjectsTree.getSelectionPath();
        ProjectInfo p_proj = (ProjectInfo) p_path.getLastPathComponent();

        if (Utilities.askDialog(this,"Confirmation","Are you sure?"))
        {
            String s_name = (String)p_proj.p_cvarNames.elementAt(p_VarTable.getSelectedRow());
            try
            {
                p_proj.removeCustomVariable(s_name);
            }
            catch (IOException e)
            {
                Utilities.showErrorDialog(this,"Save error","I can't save custom variable file.");
            }

            p_variableTableModel.updated();
        }
    }

    private void saveINI()
    {
        try
        {
            appProperties.save();
        }
        catch (IOException e1)
        {
            Utilities.showErrorDialog(this,"Save error","I can't save INI file");
        }
    }

    public final void buildProjects()
    {
        TreePath [] ap_paths = p_ProjectsTree.getSelectionPaths();
        if (ap_paths == null)
        {
            lg_busy = false;
            return;
        }
        StringBuffer p_strBuffer = new StringBuffer(16000);

        p_TerminalWindow.setText("Building....");

        int i_summaryProjects = 0;
        int i_builtProjects = 0;
        int i_errorProjects  = 0;


        int i_numberProjects = 0;
        for(int li=0;li<ap_paths.length;li++)
        {
            TreePath p_path = ap_paths[li];
            if (p_path.getLastPathComponent() instanceof ProjectInfo)
            {
                i_numberProjects ++;
            }
        }


        i_numberProjects *= BuilderModule.BUILD_STEPS;
        p_ProgressBar.setMinimum(0);
        p_ProgressBar.setMaximum(i_numberProjects);
        p_ProgressBar.setValue(0);

        p_ProgressBar.setForeground(Color.green);

        for(int li=0;li<ap_paths.length;li++)
        {
            TreePath p_path = ap_paths[li];
            if (p_path.getLastPathComponent() instanceof ProjectInfo)
            {
                i_summaryProjects ++;
                ProjectInfo p_projInfo = (ProjectInfo) p_path.getLastPathComponent();
                p_strBuffer.append("\r\n--------------------\r\nBuild project "+p_projInfo.toString()+"\r\n");
                try
                {
                    File p_dest = new File(p_projInfo.getDirectory(),appProperties.DestinationDir);
                    String s_cldc = "";
                    String s_midp = "";
                    switch(p_projInfo.getCLDCVersion())
                    {
                        case ProjectInfo.CLDC10:{s_cldc = appProperties.CLDC10Path;};break;
                        case ProjectInfo.CLDC11:{s_cldc = appProperties.CLDC11Path;};break;
                    }

                    switch(p_projInfo.getMIDPVersion())
                    {
                        case ProjectInfo.MIDP_10:{s_midp = appProperties.MIDP10Path;};break;
                        case ProjectInfo.MIDP_20:{s_midp = appProperties.MIDP20Path;};break;
                    }

                    String s_bootPath = s_cldc;
                    if (s_bootPath.length()!=0 && s_midp.length()!=0) s_bootPath +=";";
                    if (s_midp.length()!=0)
                    {
                        s_bootPath += s_midp;
                    }

                    String s_report = BuilderModule.buildProject(p_projInfo,appProperties.MainDirectory,p_dest,p_Check_Obfuscating.isSelected(),p_Check_Debug.isSelected(),p_Check_RemoveComments.isSelected(),p_Check_Optimization.isSelected(),s_bootPath,p_Check_JarOptimization.isSelected());
                    p_strBuffer.append(s_report);
                    p_dest = null;
                    i_builtProjects++;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    i_errorProjects ++;
                    p_strBuffer.append("!!!ERROR ["+e.getMessage()+"]");
                    p_strBuffer.append(BuilderModule.s_ProtocolString);
                    p_ProgressBar.setForeground(Color.red);
                    //e.printStackTrace();
                }
            }
        }
        p_TerminalWindow.setText(p_strBuffer.toString());

        String s_str = "Summary: "+i_summaryProjects+"\r\nBuilt projects: "+i_builtProjects+"\r\nError projects: "+i_errorProjects;

        Utilities.showInfoDialog(this,"Projects statistics",s_str);

        p_MidletPropertiesTable.repaint();

        p_ProgressBar.setValue(0);
        lg_busy = false;
    }

    public final void selectMainDirectory()
    {
        p_TerminalWindow.setText("");
        File p_file = Utilities.selectDirectoryForOpen(this,"Select projects directory",p_projectsContainer.p_MainDirectory);
        if (p_file!=null)
        {
            appProperties.MainDirectory = p_file;
            if (p_file!=null) p_Button_NewProject.setEnabled(true);
            p_TerminalWindow.setText(p_projectsContainer.setMainDirectory(appProperties.MainDirectory));
            saveINI();
            setTitle();
        }
        else
        {
            p_Button_NewProject.setEnabled(false);
        }
    }


    private void updateButtonState()
    {
        TreePath [] ap_paths =  p_ProjectsTree.getSelectionPaths();
        if (ap_paths == null || (ap_paths.length == 1 && ap_paths[0].getLastPathComponent().equals(p_projectsContainer)))
        {
            // Нет выбранного проекта
            p_Button_AddVar.setEnabled(false);
            p_Button_RemoveVar.setEnabled(false);
            p_Button_Build.setEnabled(false);
            p_Button_AddMIDLETProperty.setEnabled(false);
            p_Button_RemoveMIDLETProperty.setEnabled(false);
        }
        else
        {
            // Есть выбранный проект
            p_Button_AddVar.setEnabled(true);
            p_Button_AddMIDLETProperty.setEnabled(true);
            if (p_VarTable.getRowCount()>0 && p_VarTable.getSelectedRow()>=0)
            {
                p_Button_RemoveVar.setEnabled(true);
            }
            else
            {
                p_Button_RemoveVar.setEnabled(false);
            }

            if (p_MidletPropertiesTable.getRowCount()>0 && p_MidletPropertiesTable.getSelectedRow()>=0)
            {
                p_Button_RemoveMIDLETProperty.setEnabled(true);
            }
            else
            {
                p_Button_RemoveMIDLETProperty.setEnabled(false);
            }
            p_Button_Build.setEnabled(true);
        }

        if  (appProperties.MainDirectory==null)
            p_Button_NewProject.setEnabled(false);
        else
            p_Button_NewProject.setEnabled(true);

    }

    private void setTitle()
    {
        if (appProperties.MainDirectory==null)
            setTitle("RRG WTK");
        else
            setTitle(appProperties.MainDirectory.getAbsolutePath());
    }

    public void valueChanged(TreeSelectionEvent e)
    {
        if (p_VarTable.isEditing())
        {
            p_VarTable.removeEditor();
        }
        if (p_MidletPropertiesTable.isEditing())
        {
            p_MidletPropertiesTable.removeEditor();
        }

        TreePath [] ap_paths =  p_ProjectsTree.getSelectionPaths();
        if (ap_paths == null || (ap_paths.length == 1 && ap_paths[0].getLastPathComponent().equals(p_projectsContainer)) || ap_paths.length>1)
        {
            p_propTableModel.setProjectInfo(null);
            p_variableTableModel.setProjectInfo(null);
        }
        else
        {
            ProjectInfo p_info = (ProjectInfo) ap_paths[0].getLastPathComponent();
            p_propTableModel.setProjectInfo(p_info);
            p_variableTableModel.setProjectInfo(p_info);
        }
        updateButtonState();
    }

    public void focusGained(FocusEvent e)
    {
        updateButtonState();
    }

    public void focusLost(FocusEvent e)
    {
        updateButtonState();
    }


    public void nextProjectBuildStep()
    {
        p_ProgressBar.setValue(p_ProgressBar.getValue()+1);
        p_ProgressBar.repaint();
    }
}

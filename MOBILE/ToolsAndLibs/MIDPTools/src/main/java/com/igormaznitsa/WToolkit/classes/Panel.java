package com.igormaznitsa.WToolkit.classes;

import com.igormaznitsa.WToolkit.classes.BuildProject;
import com.igormaznitsa.WToolkit.classes.EditPropertiesFrame;
import com.igormaznitsa.WToolkit.classes.FileUtils;
import com.igormaznitsa.WToolkit.classes.NewProjectDialog;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;

public class Panel extends JFrame implements TreeSelectionListener, ActionListener
{
    JPanel p_ButtonPanel;
    JButton p_SelectDirectoryButton,p_EditProjectButton,p_BuildProjectButton,p_CreateNewProject,p_SaveChanging,p_ClearProjectButton;
    JTree p_ProjectsTree;
    JSplitPane p_SplitPane;
    JTextArea p_MessageArea,p_InfoArea;
    DefaultMutableTreeNode p_indexTreeNode;
    File p_selectedDirectory;
    JSplitPane p_InfoPane;

    DefaultMutableTreeNode p_selectedNode;
    TreePath p_selectedPath;

    String s_ClassPath,s_Preverifier,s_JarPath,s_Javac;

    public static final String[] as_JADvalues = new String[]
    {
        "MIDlet-Name",
        "MIDlet-Version",
        "MIDlet-Jar-URL",
        "MIDlet-Jar-Size",
        "MIDlet-Vendor",
        "MIDlet-Icon",
        "MIDlet-Description",
        "MIDlet-Info-URL",
        "MIDlet-Data-Size",
        "MIDlet-Delete-Confirm",
        "MIDlet-Install-Notify"
    };

    public static final String[] as_ManifValues = new String[]
    {
        "MIDlet-Name",
        "MIDlet-Version",
        "MIDlet-Vendor",
        "MicroEdition-Profile",
        "MicroEdition-Configuration",
        "MIDlet-Icon",
        "MIDlet-Description",
        "MIDlet-Info-URL",
        "MIDlet-Data-Size",
        "MIDlet-Delete-Confirm",
        "MIDlet-Install-Notify"
    };

    public Panel(String _classPath, String _preverifier, String _jarPath, String _JavaC) throws IOException
    {
        super("IAMWToolkit v1.0");
        setSize(600, 500);

        s_Javac = _JavaC;
        s_ClassPath = _classPath;
        s_Preverifier = _preverifier;
        s_JarPath = _jarPath;

        getContentPane().setLayout(new BorderLayout(2, 2));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add buttons and panels
        p_ButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        p_SelectDirectoryButton = new JButton("Directory");
        p_SelectDirectoryButton.setToolTipText("Select directory");
        p_SelectDirectoryButton.addActionListener(this);

        p_EditProjectButton = new JButton("Settings");
        p_EditProjectButton.setToolTipText("Settings of the project");
        p_EditProjectButton.addActionListener(this);

        p_BuildProjectButton = new JButton("Build");
        p_BuildProjectButton.setToolTipText("Build the project");
        p_BuildProjectButton.addActionListener(this);

        p_ClearProjectButton = new JButton("Clear");
        p_ClearProjectButton.setToolTipText("Clear the project");
        p_ClearProjectButton.addActionListener(this);

        p_CreateNewProject = new JButton("Create new");
        p_CreateNewProject.setToolTipText("Create new project");
        p_CreateNewProject.addActionListener(this);

        p_SaveChanging = new JButton("Save");
        p_SaveChanging.setToolTipText("Save changing in all projects");
        p_SaveChanging.addActionListener(this);

        p_ButtonPanel.add(p_SelectDirectoryButton);
        p_ButtonPanel.add(p_EditProjectButton);
        p_ButtonPanel.add(p_BuildProjectButton);
        p_ButtonPanel.add(p_CreateNewProject);
        p_ButtonPanel.add(p_SaveChanging);
        p_ButtonPanel.add(p_ClearProjectButton);

        // Add projects tree
        p_SplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        p_indexTreeNode = new DefaultMutableTreeNode("None");

        p_ProjectsTree = new JTree(p_indexTreeNode);
        p_ProjectsTree.setShowsRootHandles(true);
        p_ProjectsTree.addTreeSelectionListener(this);

        p_MessageArea = new JTextArea();
        p_MessageArea.setEditable(false);
        p_InfoArea = new JTextArea();
        p_InfoArea.setEditable(false);

        p_InfoPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        p_InfoPane.add(new JScrollPane(p_InfoArea), JSplitPane.TOP);
        p_InfoPane.add(new JScrollPane(p_MessageArea), JSplitPane.BOTTOM);

        p_SplitPane.add(new JScrollPane(p_ProjectsTree), JSplitPane.LEFT);
        p_SplitPane.add(p_InfoPane, JSplitPane.RIGHT);

        getContentPane().add(p_ButtonPanel, BorderLayout.NORTH);
        getContentPane().add(p_SplitPane, BorderLayout.CENTER);

        updateTree(null);

        p_InfoArea.setLineWrap(true);
        p_InfoArea.setWrapStyleWord(true);
        p_InfoArea.setText("");

        p_selectedNode = null;
        p_selectedPath = null;

        pack();

        Dimension p_screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) p_screenSize.getWidth() - 500) >> 1, ((int) p_screenSize.getHeight() - 500) >> 1);

        this.show();
    }

    public void updateTree(File _directory) throws IOException
    {
        if (_directory == null || !_directory.exists())
        {
            p_indexTreeNode.removeAllChildren();
            p_indexTreeNode.setUserObject("None");
            p_BuildProjectButton.setEnabled(false);
            p_ClearProjectButton.setEnabled(false);
            p_EditProjectButton.setEnabled(false);
            p_CreateNewProject.setEnabled(false);
            DefaultTreeModel p_dtm = (DefaultTreeModel) p_ProjectsTree.getModel();
            p_dtm.reload();
            return;
        }

        DefaultTreeModel p_dtm = (DefaultTreeModel) p_ProjectsTree.getModel();

        //p_dtm.removeNodeFromParent((MutableTreeNode)p_dtm.getRoot());

        p_ProjectsTree.setSelectionPaths(null);

        p_indexTreeNode.setUserObject(_directory.getAbsolutePath());

        p_indexTreeNode.removeAllChildren();

        p_MessageArea.setText("");

        File[] ap_file = _directory.listFiles();
        for (int li = 0; li < ap_file.length; li++)
        {
            ProjectInfo p_pinfo = null;
            try
            {
                p_pinfo = new ProjectInfo(ap_file[li]);
            }
            catch (IOException e)
            {
                p_MessageArea.append("Error reading project " + ap_file[li].getName() + " [" + e.getMessage() + "]\r\n");
                continue;
            }
            p_indexTreeNode.add(new DefaultMutableTreeNode(p_pinfo));
        }
        p_dtm.reload();

        p_ClearProjectButton.setEnabled(false);
        p_BuildProjectButton.setEnabled(false);
        p_EditProjectButton.setEnabled(false);
        p_SaveChanging.setEnabled(false);
        p_CreateNewProject.setEnabled(true);

        p_selectedDirectory = _directory;
    }

    public void valueChanged(TreeSelectionEvent e)
    {
        TreePath[] ap_paths = p_ProjectsTree.getSelectionPaths();

        if (ap_paths == null)
        {
            p_InfoArea.setText("");
            return;
        }

        //DefaultTreeModel p_dtm = (DefaultTreeModel) p_ProjectsTree.getModel();

        int i_len = ap_paths.length;

        for (int li = 0; li < ap_paths.length; li++)
        {
            if (ap_paths[li].getLastPathComponent().equals(p_indexTreeNode))
            {
                i_len--;
                break;
            }
        }

        if (i_len == 0)
        {
            p_ClearProjectButton.setEnabled(false);
            p_BuildProjectButton.setEnabled(false);
            p_EditProjectButton.setEnabled(false);
        }
        else if (i_len == 1)
        {
            p_EditProjectButton.setEnabled(true);
            p_BuildProjectButton.setEnabled(true);
            p_ClearProjectButton.setEnabled(true);
        }
        else
        {
            p_EditProjectButton.setEnabled(false);
            p_BuildProjectButton.setEnabled(true);
            p_ClearProjectButton.setEnabled(true);
        }

        // Filling the info panel with the Info about last selected item
        p_InfoArea.setText("");
        p_selectedNode = null;
        p_selectedPath = null;
        if (i_len > 0)
        {
            DefaultMutableTreeNode p_selNode = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
            if (!p_selNode.equals(p_indexTreeNode))
            {
                ProjectInfo p_prop = (ProjectInfo) p_selNode.getUserObject();
                fillInfoWithParameters(p_prop.getProperties());
                if (i_len == 1)
                {
                    p_selectedNode = p_selNode;
                    p_selectedPath = e.getPath();
                }
            }
        }
    }

    public File selectProjectsDirectory()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        chooser.setDragEnabled(false);
        chooser.setControlButtonsAreShown(true);
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select a directory contains projects");

        if (p_selectedDirectory != null) chooser.setCurrentDirectory(p_selectedDirectory);

        int returnVal = chooser.showDialog(this, "Select");

        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                updateTree(chooser.getSelectedFile());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }

    private void fillInfoWithParameters(Properties _prop)
    {
        p_InfoArea.setText("");
        Enumeration p_keys = _prop.keys();
        while (p_keys.hasMoreElements())
        {
            String s_key = (String) p_keys.nextElement();
            String s_value = _prop.getProperty(s_key);
            p_InfoArea.append(s_key + ": " + s_value + "\r\n");
        }
    }

    public void buildProjects(String _classPath, String _preverifier, String _jar, String _javac)
    {
        boolean lg_debugInclude = JOptionPane.showConfirmDialog(this,"To include the debug info?","DEBUG INFO",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;

        TreePath[] p_selectedProjects = p_ProjectsTree.getSelectionPaths();

        Vector p_vector = new Vector();

        File p_copyHolder = new File(p_selectedDirectory, "lastBuild");
        FileUtils.deleteDir(p_copyHolder);

        if (!p_copyHolder.exists())
            if (!p_copyHolder.mkdirs()) p_copyHolder = null;

        // Creating list of projects to build
        for (int li = 0; li < p_selectedProjects.length; li++)
        {
            DefaultMutableTreeNode p_treeNode = (DefaultMutableTreeNode) p_selectedProjects[li].getLastPathComponent();
            if (p_treeNode.equals(p_indexTreeNode)) continue;
            p_vector.add(p_treeNode.getUserObject());
        }

        // Building the projects from the list
        Enumeration p_enum = p_vector.elements();
        p_MessageArea.setText("------Build projects " + new Date(System.currentTimeMillis()) + "-----\r\n");
        int i_summaryProjects = 0;
        int i_builtProjects = 0;
        while (p_enum.hasMoreElements())
        {
            ProjectInfo p_projInfo = (ProjectInfo) p_enum.nextElement();

            p_MessageArea.append("--------->Processing " + p_projInfo.s_ViewName + " project\r\n");

            BuildProject p_bp = null;
            try
            {
                p_bp = new BuildProject(p_projInfo, _classPath, _preverifier, _jar, _javac,lg_debugInclude);
            }
            catch (IOException e)
            {
                p_MessageArea.append(e.getMessage() + "\r\n");
                continue;
            }

            i_summaryProjects++;

            try
            {
                p_MessageArea.append("Preprocessing...\r\n");
                p_bp.preprocess(lg_debugInclude);
                p_MessageArea.append("Compiling...\r\n");
                p_bp.compile();
//                p_MessageArea.append("Obfuscating...\r\n");
//                Iterator p_iter = p_projInfo.p_mainClasses.iterator();
//                while(p_iter.hasNext())
//                {
//                    String s_mclass = (String) p_iter.next();
//                    p_bp.optimize(s_mclass);
//                }

                p_MessageArea.append("Preverifyng...\r\n");
                p_bp.preverifyProject();
                p_MessageArea.append("Jaring...\r\n");
                int i_size = p_bp.jar();
                if (i_size < 0) throw new IOException("I couldn't create JAR file");
                p_projInfo.getProperties().setProperty("MIDlet-Jar-Size", "" + i_size);
                p_projInfo.saveParameters();

                if (p_copyHolder != null)
                {
                    // Copy JAR
                    File p_srcJar = new File(p_projInfo.p_Directory, "/bin/" + p_projInfo.p_Directory.getName() + ".jar");
                    File p_dstJar = new File(p_copyHolder, p_projInfo.p_Directory.getName() + ".jar");
                    FileUtils.copyFile(p_srcJar, p_dstJar);

                    // Copy JAD
                    p_srcJar = new File(p_projInfo.p_Directory, "/bin/" + p_projInfo.p_Directory.getName() + ".jad");
                    p_dstJar = new File(p_copyHolder, p_projInfo.p_Directory.getName() + ".jad");
                    FileUtils.copyFile(p_srcJar, p_dstJar);
                }
            }
            catch (IOException e)
            {
                p_MessageArea.append("(!) BUILDING ERROR [" + e.getMessage() + "]\r\n");
                String p_str = null;
                p_str = new String(p_bp.p_terminalArray.toByteArray()).trim();
                p_MessageArea.append(p_str + "\r\n");
                p_MessageArea.append("--------->Processing " + p_projInfo.s_ViewName + " has finished\r\n");
                continue;
            }
            String p_str = new String(p_bp.p_terminalArray.toByteArray()).trim();
            p_MessageArea.append(p_str);
            p_MessageArea.append("\r\nCompilation has finished successfully\r\n");


            p_MessageArea.append("--------->Processing " + p_projInfo.s_ViewName + " has finished\r\n");
            i_builtProjects++;

            p_MessageArea.repaint();
        }
        p_MessageArea.append("Summary projects : " + i_summaryProjects + "\r\n");
        p_MessageArea.append("Built projects   : " + i_builtProjects + "\r\n");
    }

    public void clearProjects()
    {
        TreePath[] p_selectedProjects = p_ProjectsTree.getSelectionPaths();

        Vector p_vector = new Vector();

        // Creating list of projects to build
        for (int li = 0; li < p_selectedProjects.length; li++)
        {
            DefaultMutableTreeNode p_treeNode = (DefaultMutableTreeNode) p_selectedProjects[li].getLastPathComponent();
            if (p_treeNode.equals(p_indexTreeNode)) continue;
            p_vector.add(p_treeNode.getUserObject());
        }

        // Building the projects from the list
        Enumeration p_enum = p_vector.elements();
        p_MessageArea.setText("------Clearing projects " + new Date(System.currentTimeMillis()) + "-----\r\n");
        while (p_enum.hasMoreElements())
        {
            ProjectInfo p_projInfo = (ProjectInfo) p_enum.nextElement();

            File p_workDir = p_projInfo.p_Directory;

            // Clearing temp directories
            FileUtils.deleteDir(new File(p_workDir, "classes"));
            FileUtils.deleteDir(new File(p_workDir, "tmpclasses"));
            FileUtils.deleteDir(new File(p_workDir, "tmplib"));

            // Removing Jar file if it is exist
            File p_jar = new File(p_workDir, "bin/" + p_workDir.getName() + ".jar");
            if (p_jar.exists())
            {
                p_jar.delete();
            }

            p_MessageArea.append("Project " + p_projInfo.s_ViewName + " was cleared...\r\n");
        }
    }

    private void createNewProject()
    {
        if (p_selectedDirectory == null) return;

        NewProjectDialog p_newDialog = new NewProjectDialog(this);

        if (!p_newDialog.showDialog()) return;

        String s_projectName = p_newDialog.s_projectName;
        String s_mainClassName = p_newDialog.s_mainClassName;

        File p_dir = new File(p_selectedDirectory, s_projectName);
        if (p_dir.exists())
        {
            JOptionPane.showMessageDialog(this, "I can't create new project with \'" + s_projectName + "\' name because it is exist already!", "New project error!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        p_dir.mkdir();
        File p_Src = new File(p_dir, "src");
        File p_Lib = new File(p_dir, "lib");
        File p_Res = new File(p_dir, "res");
        File p_Bin = new File(p_dir, "bin");

        if (!p_Src.exists())
            p_Src.mkdirs();
        if (!p_Lib.exists())
            p_Lib.mkdirs();
        if (!p_Res.exists())
            p_Res.mkdirs();
        if (!p_Bin.exists())
            p_Bin.mkdirs();

        ProjectInfo p_newProject = new ProjectInfo(p_dir, s_projectName, s_mainClassName);

        try
        {
            p_newProject.saveParameters();
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, "I can't save parameters of the \'" + s_projectName + "\' project!", "New project error!", JOptionPane.ERROR_MESSAGE);
            FileUtils.deleteDir(p_dir);
            return;
        }

        p_indexTreeNode.add(new DefaultMutableTreeNode(p_newProject));

        DefaultTreeModel p_dtm = (DefaultTreeModel) p_ProjectsTree.getModel();
        p_dtm.reload();
    }

    public synchronized void actionPerformed(ActionEvent e)
    {
        if (e.getSource().equals(p_SelectDirectoryButton))
        {
            selectProjectsDirectory();
        }
        else if (e.getSource().equals(p_EditProjectButton))
        {
            if (p_selectedNode != null)
            {
                ProjectInfo p_info = (ProjectInfo) p_selectedNode.getUserObject();
                EditPropertiesFrame p_editDialog = new EditPropertiesFrame(this, p_info.s_ViewName, p_info.getProperties());
                Properties p_prop = p_editDialog.showDialog();
                if (p_prop != null)
                {
                    p_info.setNewProperties(p_prop);
                    p_ProjectsTree.setSelectionPath(p_selectedPath);
                    p_SaveChanging.setEnabled(true);
                    fillInfoWithParameters(p_prop);
                }
            }
        }
        else if (e.getSource().equals(p_BuildProjectButton))
        {
            buildProjects(s_ClassPath, s_Preverifier, s_JarPath, s_Javac);
        }
        else if (e.getSource().equals(p_ClearProjectButton))
        {
            clearProjects();
        }
        else if (e.getSource().equals(p_CreateNewProject))
        {
            createNewProject();
        }
        else if (e.getSource().equals(p_SaveChanging))
        {
            Enumeration p_projects = p_indexTreeNode.children();
            p_MessageArea.setText("");
            while (p_projects.hasMoreElements())
            {
                ProjectInfo p_info = null;
                try
                {
                    p_info = (ProjectInfo) (((DefaultMutableTreeNode) p_projects.nextElement()).getUserObject());
                    if (p_info.isModified())
                        p_info.saveParameters();
                    else
                        continue;
                }
                catch (IOException e1)
                {
                    p_MessageArea.append("I can't save parameters for " + p_info.s_ViewName + " [" + e1.getMessage() + "]\r\n");
                    continue;
                }
                p_MessageArea.append("Parameters for " + p_info.s_ViewName + " were saved successfully\r\n");
            }
            p_SaveChanging.setEnabled(false);
        }
    }

}

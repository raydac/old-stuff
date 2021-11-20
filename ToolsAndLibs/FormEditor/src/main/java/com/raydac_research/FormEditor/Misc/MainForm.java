package com.raydac_research.FormEditor.Misc;

import com.raydac_research.FormEditor.RrgFormResources.*;
import com.raydac_research.FormEditor.RrgFormComponents.*;
import com.raydac_research.FormEditor.ExportModules.FormExport;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Vector;
import java.io.PrintStream;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MainForm extends FileFilter implements WindowListener, ActionListener, TreeSelectionListener, MouseListener, MouseMotionListener, FocusListener
{
    private static final boolean DEMO = false;

    private JPanel p_PropertiesPanel;
    private JTextField p_ResourceIDEditLabel;

    private Object p_CurrentPropertiesPage;

    private AbstractRrgResource p_CurrentSelectedResource;
    private AbstractFormComponent p_CurrentSelectedComponent;
    private JButton p_AppendChangesButton;
    private JCheckBox p_HiddenFlagButton;
    private JCheckBox p_PinnedFlagButton;
    private JPanel p_UpPropPanel;
    private JPanel p_DownPropPanel;

    private File p_CurrentFile;

    private JFrame p_MainFrame;
    private JSplitPane p_SplitPanel;
    protected ResourceContainer p_ResourceContainer;

    protected boolean lg_currentFileChanged = false;

    private int i_difX;
    private int i_difY;

    private static final String ACTION_COMPONENT_HIDE = "COMPONENT_HIDE";
    private static final String ACTION_COMPONENT_PINED = "COMPONENT_PINED";
    private static final String ACTION_COMPONENT_LOCKED = "COMPONENT_LOCKED";

    private static final String ACTION_FILE_NEW = "FILE_NEW";
    private static final String ACTION_FILE_OPEN = "FILE_OPEN";
    private static final String ACTION_FILE_SAVE = "FILE_SAVE";
    private static final String ACTION_FILE_SAVEAS = "FILE_SAVEAS";
    private static final String ACTION_FILE_EXIT = "FILE_EXIT";

    private static final String ACTION_EDIT_UNDO = "EDIT_UNDO";
    private static final String ACTION_EDIT_REDO = "EDIT_REDO";
    private static final String ACTION_EDIT_COPY = "EDIT_COPY";
    private static final String ACTION_EDIT_PASTE = "EDIT_PASTE";

    private static final String ACTION_SELECT_FORM = "SELECT_FORM";
    private static final String ACTION_RESOURCE_ADD = "RESOURCE_ADD";
    private static final String ACTION_RESOURCE_REMOVE = "RESOURCE_REMOVE";
    private static final String ACTION_RESOURCE_REFRESH_ALL = "RESOURCE_REFRESH_ALL";

    private static final String ACTION_FORM_NEW_FORM = "NEW_FORM";
    private static final String ACTION_FORM_REMOVE_FORM = "REMOVE_FORM";
    private static final String ACTION_FORM_COPY_FORM = "COPY_FORM";

    private static final String ACTION_FORM_ADD = "FORM_ADD";
    private static final String ACTION_FORM_REMOVE = "FORM_REMOVE";
    private static final String ACTION_FORM_CLEAR = "FORM_CLEAR";
    private static final String ACTION_FORM_EXPORT = "FORM_EXPORT";
    private static final String ACTION_FORM_UP = "FORM_UP";
    private static final String ACTION_FORM_DOWN = "FORM_DOWN";
    private static final String ACTION_HELP_ABOUT = "HELP_ABOUT";

    private static final String ACTION_FILTER_NONE = "FILTER_NONE";
    private static final String ACTION_FILTER_NTSC = "FILTER_NTSC";
    private static final String ACTION_FILTER_LCD444 = "FILTER_LCD444";
    private static final String ACTION_FILTER_LCD332 = "FILTER_LCD332";

    private static final JMenu p_Menu_File = new JMenu("File");

    private static final JMenuItem p_Item_FileNew = new JMenuItem("New");
    private static final JMenuItem p_Item_FileOpen = new JMenuItem("Open");
    private static final JMenuItem p_Item_FileSave = new JMenuItem("Save");
    private static final JMenuItem p_Item_FileSaveAs = new JMenuItem("Save as");

    private static final JMenuItem p_Item_ResourceAdd = new JMenuItem("Add resource");
    private static final JMenuItem p_Item_ResourceRemove = new JMenuItem("Remove resource");
    private static final JMenuItem p_Item_ResourceRefreshAll = new JMenuItem("Refresh all resources");

    private static final JMenuItem p_Item_Exit = new JMenuItem("Exit");

    private static final JMenu p_Menu_Edit = new JMenu("Edit");
    private static final JMenuItem p_Item_EditCopy = new JMenuItem("Copy");
    private static final JMenuItem p_Item_EditPaste = new JMenuItem("Paste");
    private static final JMenuItem p_Item_EditUndo = new JMenuItem("Undo");
    private static final JMenuItem p_Item_EditRedo = new JMenuItem("Redo");

    private static final JMenu p_Menu_Form = new JMenu("Form");
    private static final JMenuItem p_Item_FormClear = new JMenuItem("Clear form");

    private static final JMenuItem p_Item_RemoveForm = new JMenuItem("Remove form");
    private static final JMenuItem p_Item_NewForm = new JMenuItem("New form");
    private static final JMenuItem p_Item_CopyForm = new JMenuItem("Copy form");

    private static final JMenuItem p_Item_FormAdd = new JMenuItem("Add component");
    private static final JMenuItem p_Item_FormRemove = new JMenuItem("Remove component");
    private static final JMenuItem p_Item_FormExport = new JMenuItem("Export");

    private static final JMenu p_Menu_View = new JMenu("View");

    private static final ButtonGroup p_ViewButtonGroup = new ButtonGroup();
    private static final JRadioButtonMenuItem p_View_Normal = new JRadioButtonMenuItem("Normal", true);
    private static final JRadioButtonMenuItem p_View_Inactive = new JRadioButtonMenuItem("Disabled");
    private static final JRadioButtonMenuItem p_View_Active = new JRadioButtonMenuItem("Selected");
    private static final JRadioButtonMenuItem p_View_Pressed = new JRadioButtonMenuItem("Pressed");

    private static final ButtonGroup p_ViewScaleButtonGroup = new ButtonGroup();
    private static final JCheckBoxMenuItem p_View_Scale1 = new JCheckBoxMenuItem("x1", true);
    private static final JCheckBoxMenuItem p_View_Scale2 = new JCheckBoxMenuItem("x2");
    private static final JCheckBoxMenuItem p_View_Scale4 = new JCheckBoxMenuItem("x4");
    private static final JCheckBoxMenuItem p_View_Scale8 = new JCheckBoxMenuItem("x8");
    private static final JCheckBoxMenuItem p_View_Scale16 = new JCheckBoxMenuItem("x16");

    private static final JCheckBoxMenuItem p_View_TVGrid = new JCheckBoxMenuItem("TV Grid");
    private static final JCheckBoxMenuItem p_View_Grid = new JCheckBoxMenuItem("Grid");
    private static final JCheckBoxMenuItem p_View_Rulers = new JCheckBoxMenuItem("Rulers");

    private static final ButtonGroup p_ViewFilterGroup = new ButtonGroup();
    private static final JCheckBoxMenuItem p_View_Filter_None = new JCheckBoxMenuItem("Filter NONE");
    private static final JCheckBoxMenuItem p_View_Filter_NTSC = new JCheckBoxMenuItem("Filter NTSC");
    private static final JCheckBoxMenuItem p_View_Filter_LCD444 = new JCheckBoxMenuItem("Filter LCD444");
    private static final JCheckBoxMenuItem p_View_Filter_LCD332 = new JCheckBoxMenuItem("Filter LCD332");

    private static final JMenu p_Help = new JMenu("Help");

    private static final JMenuItem p_Help_About = new JMenuItem("About");

    private JTree ResourcesTree;
    private JScrollPane ScrollPanel;
    private JLabel LabelX;
    private JLabel LabelY;
    private JLabel LabelWidth;
    private JLabel LabelHeight;

    private FormCollection p_FormCollection;
    //private FormContainer p_FormContainer;
    private JRrgFormContainer p_FormComponent;

    private JLabel ViewStatusLabel;
    private JLabel ScaleLabel;
    private JButton Button_AddResource;
    private JButton Button_RemoveResource;
    private JButton Button_FormUp;
    private JButton Button_FormDown;
    private JButton Button_FormResourceRemove;
    private JButton Button_FormResourceAdd;

    private NewResourceDialog p_ResourceDialog;
    private NewComponent_Form p_NewComponent_Form;
    private JTree FormTree;
    private JLabel LabelCX;
    private JLabel LabelCY;
    private JTextField p_ComponentChannelID;
    private JCheckBox p_LockedFlagButton;
    private JComboBox p_FormsList;
    private JTextField p_ComponentXcoord;
    private JTextField p_ComponentYcoord;
    private JTabbedPane p_TabbedPane;
//    private JPanel p_mpp;

    private AbstractFormComponent p_Clipboard;
    private FormRuler p_focusedFormRuler;

    public JMenuBar makeMainMenuBar()
    {
        JMenuBar p_menuBar = new JMenuBar();

        p_Item_Exit.setActionCommand(ACTION_FILE_EXIT);
        p_Item_FileNew.setActionCommand(ACTION_FILE_NEW);
        p_Item_FileOpen.setActionCommand(ACTION_FILE_OPEN);
        p_Item_FileSave.setActionCommand(ACTION_FILE_SAVE);
        p_Item_FileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));

        p_Item_FileSaveAs.setActionCommand(ACTION_FILE_SAVEAS);

        p_Item_EditCopy.setActionCommand(ACTION_EDIT_COPY);
        p_Item_EditPaste.setActionCommand(ACTION_EDIT_PASTE);
        p_Item_EditUndo.setActionCommand(ACTION_EDIT_UNDO);
        p_Item_EditRedo.setActionCommand(ACTION_EDIT_REDO);

        p_Item_ResourceAdd.setActionCommand(ACTION_RESOURCE_ADD);
        p_Item_ResourceRemove.setActionCommand(ACTION_RESOURCE_REMOVE);
        p_Item_ResourceRefreshAll.setActionCommand(ACTION_RESOURCE_REFRESH_ALL);

        p_FormsList.setActionCommand(ACTION_SELECT_FORM);
        p_FormsList.addActionListener(this);

        Button_AddResource.setActionCommand(ACTION_RESOURCE_ADD);
        Button_AddResource.addActionListener(this);
        Button_RemoveResource.setActionCommand(ACTION_RESOURCE_REMOVE);
        Button_RemoveResource.addActionListener(this);
        Button_FormDown.setActionCommand(ACTION_FORM_DOWN);
        Button_FormDown.addActionListener(this);
        Button_FormResourceAdd.setActionCommand(ACTION_FORM_ADD);
        Button_FormResourceAdd.addActionListener(this);
        Button_FormResourceRemove.setActionCommand(ACTION_FORM_REMOVE);
        Button_FormResourceRemove.addActionListener(this);
        Button_FormUp.setActionCommand(ACTION_FORM_UP);
        Button_FormUp.addActionListener(this);

        // File
        p_Menu_File.add(p_Item_FileNew);
        p_Item_FileNew.addActionListener(this);
        p_Menu_File.add(p_Item_FileOpen);
        p_Item_FileOpen.addActionListener(this);
        p_Menu_File.add(p_Item_FileSave);
        p_Item_FileSave.addActionListener(this);
        p_Menu_File.add(p_Item_FileSaveAs);
        p_Item_FileSaveAs.addActionListener(this);

        // Edit
        p_Menu_Edit.add(p_Item_EditCopy);
        p_Item_EditCopy.addActionListener(this);
        p_Item_EditCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        p_Menu_Edit.add(p_Item_EditPaste);
        p_Item_EditPaste.addActionListener(this);
        p_Item_EditPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        p_Menu_Edit.addSeparator();
        p_Menu_Edit.add(p_Item_EditUndo);
        p_Item_EditUndo.addActionListener(this);
        p_Item_EditUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        p_Menu_Edit.add(p_Item_EditRedo);
        p_Item_EditRedo.addActionListener(this);
        p_Item_EditRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));

        // Resource
        p_Menu_File.addSeparator();
        p_Menu_File.add(p_Item_ResourceAdd);
        p_Item_ResourceAdd.addActionListener(this);
        p_Menu_File.add(p_Item_ResourceRemove);
        p_Item_ResourceRemove.addActionListener(this);
        p_Menu_File.add(p_Item_ResourceRefreshAll);
        p_Item_ResourceRefreshAll.addActionListener(this);

        p_Menu_File.addSeparator();
        p_Menu_File.add(p_Item_Exit);
        p_Item_Exit.addActionListener(this);

        // View
        p_ViewButtonGroup.add(p_View_Normal);
        p_ViewButtonGroup.add(p_View_Active);
        p_ViewButtonGroup.add(p_View_Inactive);
        p_ViewButtonGroup.add(p_View_Pressed);

        p_Menu_View.add(p_View_Normal);
        p_View_Normal.addActionListener(this);
        p_Menu_View.add(p_View_Active);
        p_View_Active.addActionListener(this);
        p_Menu_View.add(p_View_Inactive);
        p_View_Inactive.addActionListener(this);
        p_Menu_View.add(p_View_Pressed);
        p_View_Pressed.addActionListener(this);

        p_Menu_View.addSeparator();

        p_ViewScaleButtonGroup.add(p_View_Scale1);
        p_ViewScaleButtonGroup.add(p_View_Scale2);
        p_ViewScaleButtonGroup.add(p_View_Scale4);
        p_ViewScaleButtonGroup.add(p_View_Scale8);
        p_ViewScaleButtonGroup.add(p_View_Scale16);

        p_Menu_View.add(p_View_Scale1);
        p_View_Scale1.addActionListener(this);
        p_Menu_View.add(p_View_Scale2);
        p_View_Scale2.addActionListener(this);
        p_Menu_View.add(p_View_Scale4);
        p_View_Scale4.addActionListener(this);
        p_Menu_View.add(p_View_Scale8);
        p_View_Scale8.addActionListener(this);
        p_Menu_View.add(p_View_Scale16);
        p_View_Scale16.addActionListener(this);

        p_Menu_View.addSeparator();
        p_Menu_View.add(p_View_Rulers);
        p_View_Rulers.addActionListener(this);
        p_Menu_View.add(p_View_Grid);
        p_View_Grid.addActionListener(this);
        p_Menu_View.add(p_View_TVGrid);
        p_View_TVGrid.addActionListener(this);

        p_ViewFilterGroup.add(p_View_Filter_None);
        p_ViewFilterGroup.add(p_View_Filter_NTSC);
        p_ViewFilterGroup.add(p_View_Filter_LCD444);
        p_ViewFilterGroup.add(p_View_Filter_LCD332);

        p_Menu_View.addSeparator();
        p_View_Filter_None.setActionCommand(ACTION_FILTER_NONE);
        p_Menu_View.add(p_View_Filter_None);
        p_View_Filter_None.addActionListener(this);

        p_View_Filter_NTSC.setActionCommand(ACTION_FILTER_NTSC);
        p_Menu_View.add(p_View_Filter_NTSC);
        p_View_Filter_NTSC.addActionListener(this);

        p_View_Filter_LCD444.setActionCommand(ACTION_FILTER_LCD444);
        p_Menu_View.add(p_View_Filter_LCD444);
        p_View_Filter_LCD444.addActionListener(this);

        p_View_Filter_LCD332.setActionCommand(ACTION_FILTER_LCD332);
        p_Menu_View.add(p_View_Filter_LCD332);
        p_View_Filter_LCD332.addActionListener(this);

        // RrgForm
        p_Menu_Form.add(p_Item_NewForm);
        p_Item_NewForm.setActionCommand(ACTION_FORM_NEW_FORM);
        p_Item_NewForm.addActionListener(this);

        p_Menu_Form.add(p_Item_CopyForm);
        p_Item_CopyForm.setActionCommand(ACTION_FORM_COPY_FORM);
        p_Item_CopyForm.addActionListener(this);

        p_Menu_Form.add(p_Item_RemoveForm);
        p_Item_RemoveForm.setActionCommand(ACTION_FORM_REMOVE_FORM);
        p_Item_RemoveForm.addActionListener(this);

        p_Menu_Form.add(p_Item_FormClear);
        p_Item_FormClear.setActionCommand(ACTION_FORM_CLEAR);
        p_Item_FormClear.addActionListener(this);

        p_Menu_Form.addSeparator();

        p_Menu_Form.add(p_Item_FormAdd);
        p_Item_FormAdd.setActionCommand(ACTION_FORM_ADD);
        p_Item_FormAdd.addActionListener(this);
        p_Menu_Form.add(p_Item_FormRemove);
        p_Item_FormRemove.setActionCommand(ACTION_FORM_REMOVE);
        p_Item_FormRemove.addActionListener(this);
        p_Menu_Form.add(p_Item_FormExport);
        p_Item_FormExport.setActionCommand(ACTION_FORM_EXPORT);
        p_Item_FormExport.addActionListener(this);

        // Help
        p_Help.add(p_Help_About);
        p_Help_About.setActionCommand(ACTION_HELP_ABOUT);
        p_Help_About.addActionListener(this);

        p_menuBar.add(p_Menu_File);
        p_menuBar.add(p_Menu_Edit);
        p_menuBar.add(p_Menu_Form);
        p_menuBar.add(p_Menu_View);
        p_menuBar.add(p_Help);

        p_HiddenFlagButton.setActionCommand(ACTION_COMPONENT_HIDE);
        p_HiddenFlagButton.addActionListener(this);
        p_PinnedFlagButton.setActionCommand(ACTION_COMPONENT_PINED);
        p_PinnedFlagButton.addActionListener(this);
        p_LockedFlagButton.setActionCommand(ACTION_COMPONENT_LOCKED);
        p_LockedFlagButton.addActionListener(this);

        return p_menuBar;
    }

    private FormExport p_FormExport;
    private TreeDataRenderer p_TreeDataRenderer;
    private PopupMenu p_componentPopupMenu;

    private Object p_undoComponent = null;
    private Object p_redoComponent = null;

    public MainForm(String _file)
    {
        p_FormCollection = new FormCollection();
        p_TreeDataRenderer = new TreeDataRenderer();
        p_ResourceContainer = new ResourceContainer();
        p_componentPopupMenu = new PopupMenu();
        p_componentPopupMenu.addActionListener(this);

        p_MainFrame = new JFrame("RRG Form Editor");
        p_MainFrame.setIconImage(Utilities.loadIconFromResource("ico.gif").getImage());
        p_CurrentFile = null;

        p_FormExport = new FormExport(p_MainFrame);

        p_AppendChangesButton.addActionListener(this);

        p_PropertiesPanel.setLayout(new BorderLayout(0, 0));

        p_MainFrame.setContentPane(p_SplitPanel);

        p_MainFrame.addWindowListener(this);

        p_MainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        p_MainFrame.setJMenuBar(makeMainMenuBar());

        ResourcesTree.addTreeSelectionListener(this);
        FormTree.addTreeSelectionListener(this);

        p_FormComponent = new JRrgFormContainer();

        p_FormComponent.add(p_componentPopupMenu);

        p_FormComponent.addMouseListener(this);
        p_FormComponent.addMouseMotionListener(this);

        ScrollPanel.setViewportView(p_FormComponent);
        ScrollPanel.doLayout();

        p_focusedFormRuler = null;

        p_MainFrame.doLayout();
        p_MainFrame.pack();
        updateTreeButtons();

        ResourcesTree.setBackground(Color.white);
        FormTree.setBackground(Color.white);

        ResourcesTree.setModel(p_ResourceContainer);
        ResourcesTree.setCellRenderer(p_TreeDataRenderer);
        FormTree.setCellRenderer(p_TreeDataRenderer);

        ResourcesTree.setFocusable(true);
        FormTree.setFocusable(true);

        ResourcesTree.addFocusListener(this);
        FormTree.addFocusListener(this);

        initTrees();

        p_NewComponent_Form = new NewComponent_Form(p_MainFrame);
        p_ResourceDialog = new NewResourceDialog(p_MainFrame, p_ResourceContainer);

        initResourcesAndForm();

        //Utilities.toFullScreen(p_MainFrame);

        updatePropertiesPanel(false);
        updateMenuState();

        FormTree.setModel(p_FormCollection);

        p_TabbedPane.setSelectedIndex(1);

        if (_file!=null)
        {
            // Загружаем форму
            File p_fileForOpen = new File (_file);
            if (p_fileForOpen.exists() && !p_fileForOpen.isDirectory())
            {
                ResourceContainer p_resCont = new ResourceContainer();
                FormCollection p_newFormCollection = new FormCollection();
                try
                {
                    LoadFromFile(p_fileForOpen, p_resCont, p_newFormCollection);
                }
                catch (IOException e1)
                {
                    Utilities.showErrorDialog(p_MainFrame, "Error of loading", e1.getMessage());
                    return;
                }

                p_Clipboard = null;

                p_ResourceContainer.copy(p_resCont);
                p_FormCollection.copy(p_newFormCollection);
                p_CurrentFile = p_fileForOpen;
                p_MainFrame.setTitle(p_CurrentFile.getAbsolutePath());

                selectForm(p_FormCollection.getFirstForm());

                fillFormsList();

                lg_currentFileChanged = false;
            }
        }

        p_MainFrame.show();
    }

    public JFrame getMainFrame()
    {
        return p_MainFrame;
    }

    protected void refreshAllResources()
    {
        for(int li=0;li<p_ResourceContainer.getSize();li++)
        {
            boolean lg_result = false;

            AbstractRrgResource p_resource = p_ResourceContainer.getResourceForIndex(li);

            try
            {
                lg_result = p_resource.refreshResource();
            }
            catch (IOException e)
            {
                Utilities.showInfoDialog(p_MainFrame,"Error of resource refreshing","I can't refresh resource \""+p_resource.getResourceID()+"\"");
                continue;
            }

            if (lg_result)
            {
                Vector p_vector =  p_FormCollection.getListOfComponentsUseResource(p_resource);
                for(int lt=0;lt<p_vector.size();lt++)
                {
                    AbstractFormComponent p_component = (AbstractFormComponent) p_vector.elementAt(lt);
                    p_component.resourceUpdated();
                }
            }
        }
    }

    protected void addNewResourceInResources()
    {
        if (p_CurrentSelectedResource != null)
        {
            ResourcesTree.setSelectionPath(null);
            updatePropertiesPanel(false);
        }
        AbstractRrgResource p_resource = p_ResourceDialog.showDialog();
        if (p_resource != null)
        {
            p_ResourceContainer.addResource(p_resource);
            lg_currentFileChanged = true;
        }
    }

    private boolean lg_actionPerforming = false;

    public void actionPerformed(ActionEvent e)
    {
        if (lg_actionPerforming) return;
        lg_actionPerforming = true;

        try
        {

            int i_scrollX = ScrollPanel.getHorizontalScrollBar().getValue();
            int i_scrollY = ScrollPanel.getVerticalScrollBar().getValue();
            boolean lg_repeatScrollPosition = true;

            if (e.getSource().equals(p_componentPopupMenu))
            {
                if (p_currentPopupComponent.processPopupAction(e.getActionCommand()))
                {
                    updatePropertiesPanel(false);
                    p_FormComponent.fillBufferImage();
                    p_FormComponent.validate();
                    p_FormComponent.repaint();//updateUI();
                    lg_currentFileChanged = true;
                }
            }
            else if (e.getSource().equals(p_AppendChangesButton))
            {
                String s_Str = p_ResourceIDEditLabel.getText().trim();
                if (s_Str.length() == 0)
                {
                    Utilities.showInfoDialog(p_MainFrame, "Info", "You should not have an empty id field");
                    return;
                }

                if (p_CurrentSelectedResource != null)
                {
                    // производим апгрейд ресурса

                    if (!p_CurrentSelectedResource.getResourceID().equals(s_Str))
                    {
                        if (p_ResourceContainer.containsID(s_Str))
                        {
                            Utilities.showErrorDialog(p_MainFrame, "Duplicated name", "You are having the \'" + s_Str + "\' resource already in the list");
                            return;
                        }
                        else
                        {
                            p_CurrentSelectedResource.setResourceID(s_Str);
                            TreePath p_treePath = p_ResourceContainer.getTreePath(p_CurrentSelectedResource);
                            ResourcesTree.setSelectionPath(p_treePath);
                            p_ResourceContainer.isUpdated();
                        }
                    }

                    AbstractPropertiesPage p_propertiesPage = (AbstractPropertiesPage) p_CurrentPropertiesPage;
                    String s_errorMessage = p_propertiesPage.isDataOk();
                    if (s_errorMessage == null)
                    {
                        try
                        {
                            p_propertiesPage.fillResourceFromProperties(p_CurrentSelectedResource);
                        }
                        catch (IOException e1)
                        {
                            Utilities.showErrorDialog(p_MainFrame, "Error", s_errorMessage);
                            return;
                        }
                    }
                    else
                    {
                        Utilities.showErrorDialog(p_MainFrame, "Error", s_errorMessage);
                        return;
                    }

                    // Udating of components
                    Vector p_vector = p_FormCollection.getListOfComponentsUseResource(p_CurrentSelectedResource);
                    for (int li = 0; li < p_vector.size(); li++) ((AbstractFormComponent) p_vector.elementAt(li)).resourceUpdated();
                    p_FormComponent.fillBufferImage();

                    p_FormsList.repaint();

                    lg_currentFileChanged = true;
                }
                else
                {
                    // Апгрейд компонента
                    String s_StrChannel = p_ComponentChannelID.getText().trim();
                    String s_StrX = p_ComponentXcoord.getText().trim();
                    String s_StrY = p_ComponentYcoord.getText().trim();

                    int i_channel = 0;
                    int i_newX = 0;
                    int i_newY = 0;

                    try
                    {
                        i_channel = Integer.parseInt(s_StrChannel);
                        if (i_channel < 0 || i_channel > 255) throw new NumberFormatException();
                    }
                    catch (NumberFormatException e1)
                    {
                        Utilities.showErrorDialog(p_MainFrame, "Bad channel", "You are having a bad channel value, you must use value from 0..255");
                        return;
                    }

                    try
                    {
                        i_newX = Integer.parseInt(s_StrX);
                    }
                    catch (NumberFormatException e1)
                    {
                        Utilities.showErrorDialog(p_MainFrame, "Bad X coord", "You are having a bad value of the X coordinate");
                        return;
                    }

                    try
                    {
                        i_newY = Integer.parseInt(s_StrY);
                    }
                    catch (NumberFormatException e1)
                    {
                        Utilities.showErrorDialog(p_MainFrame, "Bad Y coord", "You are having a bad value of the Y coordinate");
                        return;
                    }

                    if (!p_CurrentSelectedComponent.getID().equals(s_Str) && p_FormCollection.getSelectedForm().containsID(s_Str))
                    {
                        Utilities.showErrorDialog(p_MainFrame, "Duplicated name", "You are having the \'" + s_Str + "\' component already in the list");
                        return;
                    }

                        p_CurrentSelectedComponent.setID(s_Str);
                        p_CurrentSelectedComponent.setX(i_newX);
                        p_CurrentSelectedComponent.setY(i_newY);
                        p_CurrentSelectedComponent.setChannel(i_channel);



                    NewComponent_Form.AbstractFormComponentPanel p_propertiesPage = (NewComponent_Form.AbstractFormComponentPanel) p_CurrentPropertiesPage;
                    String s_errorMessage = p_propertiesPage.isDataOk();
                    if (s_errorMessage == null)
                    {
                        synchronized (p_CurrentSelectedComponent)
                        {
                            p_propertiesPage.fillComponentFromProperties(p_CurrentSelectedComponent, p_ResourceContainer);
                            p_propertiesPage.fillPropertiesFromComponent(p_CurrentSelectedComponent, p_ResourceContainer);
                        }

                        if (p_CurrentSelectedComponent.equals(p_FormCollection.getSelectedForm()))
                        {
                            p_FormComponent.setFormSize(p_FormCollection.getSelectedForm().getWidth(), p_FormCollection.getSelectedForm().getHeight());
                            p_FormComponent.setBackground(p_FormCollection.getSelectedForm().getBackgroundColor());
                            p_FormCollection.getSelectedForm().fillComponentPropertiesFromForm();
                        }
                        lg_currentFileChanged = true;

                        p_FormComponent.fillBufferImage();
                    }
                    else
                    {
                        Utilities.showErrorDialog(p_MainFrame, "Error", s_errorMessage);
                        return;
                    }

                    TreePath p_treePath = p_FormCollection.getSelectedForm().getTreePath(p_CurrentSelectedComponent);
                    FormTree.setSelectionPath(p_treePath);
                    p_FormCollection.getSelectedForm().isUpdated();
                }
            }
            else if (e.getActionCommand() == ACTION_FILE_NEW)
            {
                if (Utilities.askDialog(p_MainFrame, "New?", "Do you really want to make new document?"))
                {
                    initResourcesAndForm();
                }
            }
            else if (e.getActionCommand() == ACTION_FORM_EXPORT)
            {
                p_FormExport.showDialog(p_ResourceContainer, p_FormCollection);
            }
            else if (e.getActionCommand() == ACTION_COMPONENT_HIDE)
            {
                AbstractFormComponent p_component = (AbstractFormComponent) FormTree.getSelectionPath().getLastPathComponent();
                if (p_HiddenFlagButton.isSelected()) p_component.setHidden(true); else p_component.setHidden(false);
                FormTree.validate();
                FormTree.repaint();
                p_FormComponent.repaint();

                lg_currentFileChanged = true;
            }
            else if (e.getActionCommand() == ACTION_COMPONENT_PINED)
            {
                AbstractFormComponent p_component = (AbstractFormComponent) FormTree.getSelectionPath().getLastPathComponent();
                if (p_PinnedFlagButton.isSelected()) p_component.setPinned(true); else p_component.setPinned(false);
                FormTree.validate();
                FormTree.repaint();

                lg_currentFileChanged = true;
            }
            else if (e.getActionCommand() == ACTION_COMPONENT_LOCKED)
            {
                AbstractFormComponent p_component = (AbstractFormComponent) FormTree.getSelectionPath().getLastPathComponent();
                if (p_LockedFlagButton.isSelected()) p_component.setLocked(true); else p_component.setLocked(false);
                FormTree.validate();
                FormTree.repaint();

                lg_currentFileChanged = true;
            }
            else if (e.getActionCommand() == ACTION_FILE_EXIT)
            {
                closeApplication();
            }
            else if (e.getActionCommand() == ACTION_HELP_ABOUT)
                new AboutForm(p_MainFrame);
            else if (e.getActionCommand() == ACTION_RESOURCE_ADD)
            {
                addNewResourceInResources();
            }
            else if (e.getActionCommand() == ACTION_RESOURCE_REFRESH_ALL)
            {
                refreshAllResources();
            }
            else if (e.getActionCommand() == ACTION_FORM_CLEAR)
            {
                if (p_FormCollection.getSelectedForm().getSize() > 0)
                {
                    if (Utilities.askDialog(p_MainFrame, "Clear form", "Do you really want to clear the form?"))
                    {
                        p_FormCollection.getSelectedForm().removeAll();
                        p_FormComponent.fillBufferImage();
                    }
                    lg_currentFileChanged = true;
                }
            }
            else if (e.getActionCommand() == ACTION_FORM_UP)
            {
                TreePath p_path = FormTree.getSelectionPath();
                if (p_path != null)
                {
                    AbstractFormComponent p_component = (AbstractFormComponent) p_path.getLastPathComponent();
                    int i_index = p_FormCollection.getSelectedForm().getIndexForComponent(p_component);
                    if (i_index > 0)
                    {
                        p_FormCollection.getSelectedForm().moveComponentUp(i_index);
                        p_FormComponent.fillBufferImage();
                        FormTree.setSelectionPath(p_FormCollection.getSelectedForm().getTreePath(p_component));
                        p_FormComponent.repaint();
                        lg_currentFileChanged = true;
                    }
                }
            }
            else if (e.getActionCommand() == ACTION_FORM_DOWN)
            {
                TreePath p_path = FormTree.getSelectionPath();
                if (p_path != null)
                {
                    AbstractFormComponent p_component = (AbstractFormComponent) p_path.getLastPathComponent();
                    int i_index = p_FormCollection.getSelectedForm().getIndexForComponent(p_component);
                    if (i_index < p_FormCollection.getSelectedForm().getSize() - 1)
                    {
                        p_FormCollection.getSelectedForm().moveComponentDown(i_index);
                        p_FormComponent.fillBufferImage();
                        FormTree.setSelectionPath(p_FormCollection.getSelectedForm().getTreePath(p_component));
                        p_FormComponent.repaint();
                        lg_currentFileChanged = true;
                    }
                }
            }
            else if (e.getActionCommand() == ACTION_FORM_REMOVE)
            {
                TreePath p_path = FormTree.getSelectionPath();
                if (p_path != null)
                {
                    AbstractFormComponent p_component = (AbstractFormComponent) p_path.getLastPathComponent();
                    int i_index = p_FormCollection.getSelectedForm().getIndexForComponent(p_component);
                    if (Utilities.askDialog(p_MainFrame, "Remove a component", "Do you really want to remove '" + p_component.getID() + "'?"))
                    {
                        p_FormCollection.getSelectedForm().removeComponentForIndex(i_index);
                        p_FormComponent.fillBufferImage();
                        p_FormComponent.repaint();
                        lg_currentFileChanged = true;
                    }
                }
            }
            else if (e.getActionCommand() == ACTION_FORM_ADD)
            {
                addNewComponentInForm();
            }
            else if (e.getActionCommand() == ACTION_FORM_NEW_FORM)
            {
                addNewForm();
            }
            else if (e.getActionCommand() == ACTION_FORM_COPY_FORM)
            {
                copyForm();
            }
            else if (e.getActionCommand() == ACTION_FORM_REMOVE_FORM)
            {
                removeCurrentForm();
            }
            else if (e.getActionCommand() == ACTION_RESOURCE_REMOVE)
            {
                AbstractRrgResource p_resource = (AbstractRrgResource) ResourcesTree.getSelectionPath().getLastPathComponent();
                if (Utilities.askDialog(p_MainFrame, "Remove resource", "Do you really want to remove '" + p_resource.toString() + "'?"))
                {
                    Vector p_vector = p_FormCollection.getListOfComponentsUseResource(p_resource);
                    if (p_vector.size() > 0)
                    {
                        String s_str = "The resource is used by:\r\n";
                        for (int li = 0; li < p_vector.size(); li++)
                        {
                            AbstractFormComponent p_compo = (AbstractFormComponent) p_vector.elementAt(li);
                            s_str += p_compo.getParent().getID() + "." + p_compo.getID() + "\r\n";
                        }
                        s_str += "The components will be removed. Do you agree?";

                        if (Utilities.askDialog(p_MainFrame, "Problem", s_str))
                        {
                            for (int li = 0; li < p_vector.size(); li++)
                            {
                                AbstractFormComponent p_compo = (AbstractFormComponent) p_vector.elementAt(li);
                                FormContainer p_fc = p_compo.getParent();
                                p_fc.removeComponentForIndex(p_fc.getIndexForComponent((AbstractFormComponent) p_vector.elementAt(li)));
                            }

                            if (p_Clipboard!=null && p_Clipboard.doesUseResource(p_resource))
                            {
                                p_Clipboard = null;
                            }

                            p_ResourceContainer.removeResourceForID(p_resource.getResourceID());
                            lg_currentFileChanged = true;
                        }
                    }
                    else
                    {
                        p_ResourceContainer.removeResourceForID(p_resource.getResourceID());
                        lg_currentFileChanged = true;
                    }
                    updateTreeButtons();
                }
            }
            else if (e.getActionCommand().equals(ACTION_FILE_OPEN))
            {
                boolean lg_w = true;

                if (lg_currentFileChanged)
                {
                    if (Utilities.askDialog(p_MainFrame, "You have changed the current form", "You have not saved the current form. Do you want to open new file?"))
                        lg_w = true;
                    else
                        lg_w = false;
                }

                if (lg_w)
                {
                    File p_fileForOpen = Utilities.selectFileForOpen(p_MainFrame, this, "Open file..", p_CurrentFile);
                    if (p_fileForOpen != null)
                    {
                        ResourceContainer p_resCont = new ResourceContainer();
                        FormCollection p_newFormCollection = new FormCollection();
                        try
                        {
                            LoadFromFile(p_fileForOpen, p_resCont, p_newFormCollection);
                        }
                        catch (IOException e1)
                        {
                            Utilities.showErrorDialog(p_MainFrame, "Error of loading", e1.getMessage());
                            return;
                        }

                        p_Clipboard = null;

                        p_ResourceContainer.copy(p_resCont);
                        p_FormCollection.copy(p_newFormCollection);
                        p_CurrentFile = p_fileForOpen;
                        p_MainFrame.setTitle(p_CurrentFile.getAbsolutePath());

                        selectForm(p_FormCollection.getFirstForm());

                        fillFormsList();

                        lg_currentFileChanged = false;
                    }
                }
                else
                    return;
            }
            else if (e.getActionCommand().equals(ACTION_SELECT_FORM))
            {
                FormContainer p_formContainer = (FormContainer) p_FormsList.getSelectedItem();
                if (p_formContainer != null)
                    selectForm(p_formContainer);
            }
            else if (e.getActionCommand().equals(ACTION_FILE_SAVE))
            {
                File p_newFile = null;
                boolean lg_selected = false;
                if (p_CurrentFile == null)
                {
                    p_newFile = Utilities.selectFileForSave(p_MainFrame, this, "Select file for save..", null);
                    lg_selected = true;
                    if (p_newFile == null) return;
                }
                else
                {
                    p_newFile = p_CurrentFile;
                }
                try
                {
                    if (!p_newFile.getName().toUpperCase().endsWith(MainForm.FILE_EXTENSION.toUpperCase()))
                    {
                        p_newFile = new File(p_newFile.getAbsolutePath() + MainForm.FILE_EXTENSION);
                    }

                    if (p_newFile.exists() && lg_selected)
                    {
                        if (Utilities.askDialog(p_MainFrame, "Replace file?", "Do you really want to replace \'" + p_newFile.getAbsolutePath() + "\"?"))
                        {
                            saveToFile(p_newFile, p_ResourceContainer, p_FormCollection);
                        }
                    }
                    else
                    {
                        saveToFile(p_newFile, p_ResourceContainer, p_FormCollection);
                    }
                }
                catch (IOException e1)
                {
                    Utilities.showErrorDialog(p_MainFrame, "Error during saving", e1.getMessage());
                    return;
                }
                p_CurrentFile = p_newFile;
                p_MainFrame.setTitle(p_CurrentFile.getAbsolutePath());

            }
            else if (e.getActionCommand().equals(ACTION_FILE_SAVEAS))
            {
                File p_newFile = Utilities.selectFileForSave(p_MainFrame, this, "Select file for save..", p_CurrentFile);
                if (p_newFile == null) return;
                try
                {
                    if (!p_newFile.getName().toUpperCase().endsWith(MainForm.FILE_EXTENSION.toUpperCase()))
                    {
                        p_newFile = new File(p_newFile.getAbsolutePath() + MainForm.FILE_EXTENSION);
                    }

                    if (p_newFile.exists())
                    {
                        if (Utilities.askDialog(p_MainFrame, "Replace file?", "Do you really want to replace \'" + p_newFile.getAbsolutePath() + "\"?"))
                        {
                            saveToFile(p_newFile, p_ResourceContainer, p_FormCollection);
                        }
                    }
                    else
                    {
                        saveToFile(p_newFile, p_ResourceContainer, p_FormCollection);
                    }
                }
                catch (IOException e1)
                {
                    Utilities.showErrorDialog(p_MainFrame, "Error during saving", e1.getMessage());
                    return;
                }
                p_CurrentFile = p_newFile;
                p_MainFrame.setTitle(p_CurrentFile.getAbsolutePath());
            }
            else if (e.getActionCommand().equals(ACTION_EDIT_UNDO))
            {
                undo();
            }
            else if (e.getActionCommand().equals(ACTION_EDIT_REDO))
            {
                redo();
            }
            else if (e.getActionCommand().equals(ACTION_EDIT_COPY))
            {
                copyToClipboard();
            }
            else if (e.getActionCommand().equals(ACTION_EDIT_PASTE))
            {
                pasteFromClipboard();
            }
            else if (e.getActionCommand().equals(ACTION_FILTER_NONE))
            {
                p_FormComponent.setFilter(JRrgFormContainer.FILTER_NONE);
            }
            else if (e.getActionCommand().equals(ACTION_FILTER_NTSC))
            {
                p_FormComponent.setFilter(JRrgFormContainer.FILTER_NTSC);
            }
            else if (e.getActionCommand().equals(ACTION_FILTER_LCD444))
            {
                p_FormComponent.setFilter(JRrgFormContainer.FILTER_LCD444);
            }
            else if (e.getActionCommand().equals(ACTION_FILTER_LCD332))
            {
                p_FormComponent.setFilter(JRrgFormContainer.FILTER_LCD332);
            }
            else
            {
                //System.out.println(e.getActionCommand());
            }

            int i_scale = p_FormComponent.getScale();

            updateMenuState();

            if (i_scale != p_FormComponent.getScale()) lg_repeatScrollPosition = false;


            if (lg_repeatScrollPosition)
            {
                ScrollPanel.getHorizontalScrollBar().setValue(i_scrollX);
                ScrollPanel.getVerticalScrollBar().setValue(i_scrollY);
            }
        }
        finally
        {
            lg_actionPerforming = false;
        }
    }

    // Инициализация коллекции, удаляем все ресурсы и создаем одну пустую форму
    protected void initResourcesAndForm()
    {
        p_Clipboard = null;
        synchronized (p_FormCollection)
        {
            p_ResourceContainer.removeAll();

            FormContainer p_FormContainer = new FormContainer("Untitled_form", 320, 240);
            p_FormContainer.setBackgroundColor(Color.black);
            p_FormCollection.addForm(p_FormContainer);
            p_CurrentFile = null;
            p_MainFrame.setTitle("Untitled");
            p_FormComponent.setFormContainer(p_FormContainer);
            lg_currentFileChanged = false;
            p_FormCollection.removeAllForms(p_FormContainer);
            selectForm(p_FormContainer);

            fillFormsList();
        }
        lg_currentFileChanged = false;
    }

    protected void saveToFile(File _newFile, ResourceContainer _resources, FormCollection _collection) throws IOException
    {
        if (!DEMO)
        {
            if (Utilities.askDialog(p_MainFrame, "Relative paths", "Do you want to use relative paths to resources?"))
                SaveToFile(_newFile, _resources, _collection, true);
            else
                SaveToFile(_newFile, _resources, _collection, false);
            lg_currentFileChanged = false;
        }
    }

    protected void removeCurrentForm()
    {
        if (p_FormCollection.getSelectedForm() != null)
        {
            if (p_FormCollection.getFormsNumber() > 1)
            {
                Utilities.askDialog(p_MainFrame, "To remove a form?", "Do you really want to remove \"" + p_FormCollection.getSelectedForm().getID() + "\" form ?");
                p_FormCollection.removeForm(p_FormCollection.getSelectedForm());
                fillFormsList();
                selectForm(p_FormCollection.getFirstForm());
            }
            else
                Utilities.showErrorDialog(p_MainFrame, "You can't remove the form", "You can't remove the last form!");
        }
    }

    protected void addNewForm()
    {
        FormTree.setSelectionPath(null);
        updatePropertiesPanel(false);

        FormContainer p_newComponent = p_NewComponent_Form.createFormContainer(p_ResourceContainer);
        if (p_newComponent != null)
        {
            p_FormCollection.addForm(p_newComponent);
            fillFormsList();
            selectForm(p_newComponent);

            p_FormsList.setSelectedItem(p_newComponent);
        }
    }

    protected void copyForm()
    {
        if (p_FormCollection.getSelectedForm() == null) return;

        FormTree.setSelectionPath(null);
        updatePropertiesPanel(false);

        FormContainer p_newComponent = p_NewComponent_Form.createFormContainer(p_ResourceContainer, p_FormCollection.getSelectedForm());
        if (p_newComponent != null)
        {
            p_FormCollection.addForm(p_newComponent);
            fillFormsList();
            selectForm(p_newComponent);

            p_FormsList.setSelectedItem(p_newComponent);
        }
    }

    protected void addNewComponentInForm()
    {
        FormTree.setSelectionPath(null);
        updatePropertiesPanel(false);

        AbstractFormComponent p_newComponent = p_NewComponent_Form.showDialog(p_ResourceContainer);
        if (p_newComponent != null)
        {
            p_FormCollection.getSelectedForm().addComponent(p_newComponent);
/*
            p_newComponent.setNormalTextColor(p_FormContainer.getNormalTextColor());
            p_newComponent.setSelectedTextColor(p_FormContainer.getSelectedTextColor());
            p_newComponent.setPressedTextColor(p_FormContainer.getPressedTextColor());
            p_newComponent.setDisabledTextColor(p_FormContainer.getDisabledTextColor());
*/
            p_newComponent.resourceUpdated();
            setPlacedComponent(p_newComponent);
            FormTree.setSelectionPath(p_FormCollection.getSelectedForm().getTreePath(p_newComponent));
            p_FormComponent.fillBufferImage();
            p_FormComponent.validate();
            p_FormComponent.repaint();//updateUI();
            FormTree.repaint();

            lg_currentFileChanged = true;
        }
    }

    protected void setPlacedComponent(AbstractFormComponent _component)
    {
        p_FormComponent.setPlacedComponent(_component);
        if (_component == null)
        {
            LabelWidth.setText("");
            LabelHeight.setText("");
            LabelCX.setText("");
            LabelCY.setText("");
        }
        else
        {
            String s_width = Integer.toString(_component.getWidth());
            String s_height = Integer.toString(_component.getHeight());
            String s_x = Integer.toString(_component.getX());
            String s_y = Integer.toString(_component.getY());
            LabelWidth.setText(s_width);
            LabelHeight.setText(s_height);
            LabelCX.setText(s_x);
            LabelCY.setText(s_y);
        }
    }

    protected void updateTreeButtons()
    {
        TreePath[] ap_paths = ResourcesTree.getSelectionPaths();

        if (ap_paths == null)
        {
            Button_RemoveResource.setEnabled(false);
            Button_FormResourceAdd.setEnabled(false);
            p_Item_ResourceRemove.setEnabled(false);
        }
        else
        {
            Button_RemoveResource.setEnabled(true);
            Button_FormResourceAdd.setEnabled(true);
            p_Item_ResourceRemove.setEnabled(true);
        }

        // -------------------

        ap_paths = FormTree.getSelectionPaths();
        Button_FormResourceAdd.setEnabled(true);
        p_Item_FormAdd.setEnabled(true);

        if (ap_paths == null)
        {
            p_PinnedFlagButton.setSelected(false);
            p_HiddenFlagButton.setSelected(false);
            p_LockedFlagButton.setSelected(false);
            p_PinnedFlagButton.setEnabled(false);
            p_HiddenFlagButton.setEnabled(false);
            p_LockedFlagButton.setEnabled(false);

            p_Item_EditCopy.setEnabled(false);

            Button_FormDown.setEnabled(false);
            Button_FormUp.setEnabled(false);
            Button_FormResourceRemove.setEnabled(false);
            p_Item_FormRemove.setEnabled(false);
            setPlacedComponent(null);
        }
        else
        {
            Button_FormDown.setEnabled(true);
            Button_FormUp.setEnabled(true);
            Button_FormResourceRemove.setEnabled(true);
            p_Item_FormRemove.setEnabled(true);

            AbstractFormComponent p_component = (AbstractFormComponent) FormTree.getSelectionPath().getLastPathComponent();
            if (p_component.getType() == AbstractFormComponent.COMPONENT_FORM)
            {
                setPlacedComponent(null);
                p_HiddenFlagButton.setEnabled(false);
                p_LockedFlagButton.setEnabled(false);
                p_PinnedFlagButton.setEnabled(false);
                p_HiddenFlagButton.setSelected(false);
                p_PinnedFlagButton.setSelected(false);
                p_LockedFlagButton.setSelected(false);

                p_Item_EditCopy.setEnabled(false);
            }
            else
            {
                setPlacedComponent(p_component);
                p_HiddenFlagButton.setEnabled(true);
                p_PinnedFlagButton.setEnabled(true);
                p_LockedFlagButton.setEnabled(true);
                p_HiddenFlagButton.setSelected(p_component.isHidden());
                p_PinnedFlagButton.setSelected(p_component.isPinned());
                p_LockedFlagButton.setSelected(p_component.isLocked());

                p_Item_EditCopy.setEnabled(true);
            }
        }
    }

    protected synchronized void updatePropertiesPanel(boolean _resourceSelected)
    {
        p_PropertiesPanel.removeAll();
        p_ResourceIDEditLabel.setText("");

        p_ComponentChannelID.setEnabled(false);
        p_ComponentXcoord.setEnabled(false);
        p_ComponentYcoord.setEnabled(false);
        p_ComponentChannelID.setText("");
        p_ComponentXcoord.setText("");
        p_ComponentYcoord.setText("");

        if (_resourceSelected)
        {
            if (ResourcesTree.getSelectionPath() == null || ResourcesTree.getSelectionPath().getPathCount() == 1)
            {
                p_UpPropPanel.setVisible(false);
                p_DownPropPanel.setVisible(false);
                p_CurrentSelectedResource = null;
            }
            else
            {
                p_CurrentSelectedResource = (AbstractRrgResource) ResourcesTree.getSelectionPath().getLastPathComponent();
                p_ResourceIDEditLabel.setText(p_CurrentSelectedResource.getResourceID());
                AbstractPropertiesPage p_propPanel = null;
                switch (p_CurrentSelectedResource.getType())
                {
                    case AbstractRrgResource.TYPE_FONT:
                        {
                            p_propPanel = p_ResourceDialog.getFontPropertiesPanel();
                        }
                        ;
                        break;
                    case AbstractRrgResource.TYPE_IMAGE:
                        {
                            p_propPanel = p_ResourceDialog.getImagePropertiesPanel();
                        }
                        ;
                        break;
                    case AbstractRrgResource.TYPE_SOUND:
                        {
                            p_propPanel = p_ResourceDialog.getSoundPropertiesPanel();
                        }
                        ;
                        break;
                    case AbstractRrgResource.TYPE_TEXT:
                        {
                            p_propPanel = p_ResourceDialog.getTextPropertiesPanel();
                        }
                        ;
                        break;
                }

                p_propPanel.fillPropertiesFromResource(p_CurrentSelectedResource);
                p_CurrentSelectedComponent = null;
                p_CurrentPropertiesPage = p_propPanel;
                p_PropertiesPanel.add(p_propPanel.getPanel(p_MainFrame), BorderLayout.CENTER);

                p_UpPropPanel.setVisible(true);
                p_DownPropPanel.setVisible(true);
            }
        }
        else
        {
            if (FormTree.getSelectionPath() == null)
            {
                p_PropertiesPanel.removeAll();
                p_UpPropPanel.setVisible(false);
                p_DownPropPanel.setVisible(false);
                p_CurrentSelectedComponent = null;
                p_FormComponent.setPlacedComponent(null);
                p_FormComponent.fillBufferImage();
            }
            else
            {
                p_CurrentSelectedComponent = (AbstractFormComponent) FormTree.getSelectionPath().getLastPathComponent();
                p_ResourceIDEditLabel.setText(p_CurrentSelectedComponent.getID());

                p_ComponentChannelID.setText(Integer.toString(p_CurrentSelectedComponent.getChannel()));
                p_ComponentChannelID.setEnabled(true);

                p_ComponentXcoord.setText(Integer.toString(p_CurrentSelectedComponent.getX()));
                p_ComponentXcoord.setEnabled(true);

                p_ComponentYcoord.setText(Integer.toString(p_CurrentSelectedComponent.getY()));
                p_ComponentYcoord.setEnabled(true);

                NewComponent_Form.AbstractFormComponentPanel p_propPanel = null;
                switch (p_CurrentSelectedComponent.getType())
                {
                    case AbstractFormComponent.COMPONENT_IMAGE:
                        {
                            p_propPanel = p_NewComponent_Form.p_ComponentImageProperties;
                        }
                        ;
                        break;
                    case AbstractFormComponent.COMPONENT_FORM:
                        {
                            p_propPanel = p_NewComponent_Form.p_ComponentFormProperties;
                            p_HiddenFlagButton.setEnabled(false);
                            p_PinnedFlagButton.setEnabled(false);
                            p_LockedFlagButton.setEnabled(false);
                        }
                        ;
                        break;
                    case AbstractFormComponent.COMPONENT_BUTTON:
                        {
                            p_propPanel = p_NewComponent_Form.p_ComponentButtonProperties;
                        }
                        ;
                        break;
                    case AbstractFormComponent.COMPONENT_LABEL:
                        {
                            p_propPanel = p_NewComponent_Form.p_ComponentLabelProperties;
                        }
                        ;
                        break;
                    case AbstractFormComponent.COMPONENT_PATH:
                        {
                            p_propPanel = p_NewComponent_Form.p_ComponentPathProperties;
                        }
                        ;
                        break;
                    case AbstractFormComponent.COMPONENT_CUSTOMAREA:
                        {
                            p_propPanel = p_NewComponent_Form.p_ComponentCustomAreaProperties;
                        }
                        ;
                        break;
                }

                p_ResourceIDEditLabel.setEnabled(true);
                p_CurrentSelectedResource = null;
                p_propPanel.fillPropertiesFromComponent(p_CurrentSelectedComponent, p_ResourceContainer);
                p_CurrentPropertiesPage = p_propPanel;
                p_PropertiesPanel.add(p_propPanel.getPanel(p_ResourceContainer), BorderLayout.CENTER);

                p_UpPropPanel.setVisible(true);
                p_DownPropPanel.setVisible(true);

                p_FormComponent.fillBufferImage();
            }
        }
        p_PropertiesPanel.doLayout();
        p_PropertiesPanel.validate();
        p_PropertiesPanel.repaint();//updateUI();
    }

    public void valueChanged(TreeSelectionEvent e)
    {
        updateTreeButtons();
        if (e.getSource().equals(ResourcesTree))
        {
            if (ResourcesTree.getSelectionPaths() != null && ResourcesTree.getSelectionPaths().length > 1)
            {
                ResourcesTree.setSelectionPath(null);
            }

            updatePropertiesPanel(true);
        }
        else
        {
            if (FormTree.getSelectionPaths() != null)
            {
                updatePropertiesPanel(false);
            }
        }

    }

    private AbstractFormComponent p_currentPopupComponent = null;

    public void mouseClicked(MouseEvent e)
    {
        int i_overRulers = p_FormComponent.isOverRulers(e.getX(), e.getY());

        if (i_overRulers != JRrgFormContainer.OVER_RULER_NONE)
        {
            if (e.getClickCount() == 2)
            {
                p_FormComponent.doubleClickOnRulers(e.getX(), e.getY(), e.getButton() == MouseEvent.BUTTON3);
            }
        }
        else
        {
            int i_x = e.getX() / p_FormComponent.getScale() - JRrgFormContainer.BORDER_WIDTH_HEIGHT;
            int i_y = e.getY() / p_FormComponent.getScale() - JRrgFormContainer.BORDER_WIDTH_HEIGHT;
            AbstractFormComponent p_component = p_FormCollection.getSelectedForm().getComponentAt(i_x, i_y);
            if (p_component != null)
            {
                if (p_component.isLocked()) return;
                switch (e.getClickCount())
                {
                    case 1:
                        {
                            if (e.getButton() == MouseEvent.BUTTON3)
                            {
                                Vector p_MenuItems = p_component.getPopupMenuItems(i_x, i_y);
                                if (p_MenuItems == null) return;

                                p_componentPopupMenu.setLabel("Component: " + p_component.getID());

                                p_componentPopupMenu.removeAll();

                                for (int li = 0; li < p_MenuItems.size(); li++)
                                {
                                    Object p_obj = p_MenuItems.elementAt(li);
                                    if (p_obj == null)
                                    {
                                        p_componentPopupMenu.addSeparator();
                                    }
                                    else
                                        p_componentPopupMenu.add((MenuItem) p_obj);
                                }

                                p_currentPopupComponent = p_component;

                                p_componentPopupMenu.show(p_FormComponent, e.getX(), e.getY());
                            }
                            else
                            {
                                p_component.processMouseClick(e.getButton());
                                break;
                            }
                        }
                        ;
                        break;
                    case 2:
                        {
                            if (p_component.processDoubleMouseClick(i_x, i_y, e.getButton()))
                            {
                                p_FormComponent.fillBufferImage();
                                p_FormComponent.validate();
                                p_FormComponent.repaint();//updateUI();
                                updatePropertiesPanel(false);
                            }
                        }
                        ;
                        break;
                }
            }
        }
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
        LabelX.setText("");
        LabelY.setText("");
    }

    private boolean lg_SubComponentIsDragged = false;

    public void mousePressed(MouseEvent e)
    {
        int i_x = e.getX();
        int i_y = e.getY();

        int i_scrollbarX = ScrollPanel.getHorizontalScrollBar().getValue();
        int i_scrollbarY = ScrollPanel.getVerticalScrollBar().getValue();

        int i_overRuler = p_FormComponent.isOverRulers(i_x, i_y);

        if (i_overRuler != JRrgFormContainer.OVER_RULER_NONE)
        {
            // Обработка нажатия на линейки
            p_focusedFormRuler = p_FormComponent.getFocusedRuler(i_x, i_y, i_overRuler);
            if (p_focusedFormRuler != null)
            {
                switch (p_focusedFormRuler.getType())
                {
                    case FormRuler.TYPE_HORIZ:
                        {
                            LabelCY.setText(Integer.toString(p_focusedFormRuler.getCoord()));
                            LabelCX.setText("");
                        }
                        ;
                        break;
                    case FormRuler.TYPE_VERT:
                        {
                            LabelCX.setText(Integer.toString(p_focusedFormRuler.getCoord()));
                            LabelCY.setText("");
                        }
                        ;
                        break;
                }
            }
        }
        else
        {
            // Обработка нажатия на компоненты
            i_x = i_x / p_FormComponent.getScale() - JRrgFormContainer.BORDER_WIDTH_HEIGHT;
            i_y = i_y / p_FormComponent.getScale() - JRrgFormContainer.BORDER_WIDTH_HEIGHT;
            AbstractFormComponent p_component = p_FormCollection.getSelectedForm().getComponentAt(i_x, i_y);

            boolean lg_resizing = p_FormComponent.takeToResizingSelectedComponentAt(e.getX(), e.getY());

            if (lg_resizing)
            {
                p_component = p_FormComponent.getPlacedComponent();
                p_undoComponent = p_component;
                p_redoComponent = null;
                p_component.startTransaction();

                updateMenuState();
            }
            else if (p_component != null)
            {
                p_undoComponent = p_component;
                p_redoComponent = null;
                p_component.startTransaction();

                updateMenuState();

                if (p_component.equals(p_FormComponent.getPlacedComponent()))
                {
                    if (p_component.isFocusedSubComponent(i_x, i_y) && !p_component.isLocked())
                    {
                        p_component.takeSubcomponentForDrag(i_x, i_y);
                        p_component.dragSubcomponent(i_x, i_y);
                        p_FormComponent.fillBufferImage();
                        p_FormComponent.validate();
                        p_FormComponent.repaint();//updateUI();
                        lg_SubComponentIsDragged = true;
                    }
                    else
                    {
                        i_difX = i_x - p_component.getX();
                        i_difY = i_y - p_component.getY();
                        setPlacedComponent(p_component);
                        FormTree.repaint();
                    }
                }
                else
                {
                    FormTree.setSelectionPath(p_FormCollection.getSelectedForm().getTreePath(p_component));
                    i_difX = i_x - p_component.getX();
                    i_difY = i_y - p_component.getY();
                    setPlacedComponent(p_component);
                    updatePropertiesPanel(true);
                    FormTree.repaint();
                }
            }
            else
            {
                FormTree.setSelectionPath(null);
                updatePropertiesPanel(false);
                p_FormComponent.setPlacedComponent(null);
                p_FormComponent.fillBufferImage();
            }
        }

        ScrollPanel.getHorizontalScrollBar().setValue(i_scrollbarX);
        ScrollPanel.getVerticalScrollBar().setValue(i_scrollbarY);
    }

    public void mouseReleased(MouseEvent e)
    {
        LabelX.setText("");
        LabelY.setText("");

        int i_x = e.getX() / p_FormComponent.getScale() - JRrgFormContainer.BORDER_WIDTH_HEIGHT;
        int i_y = e.getY() / p_FormComponent.getScale() - JRrgFormContainer.BORDER_WIDTH_HEIGHT;

        if (p_focusedFormRuler != null)
        {
            // Отрабатываем смену позиции у перетаскиваемой линейки
            switch (p_focusedFormRuler.getType())
            {
                case FormRuler.TYPE_VERT:
                    {
                        if (i_x <= (0 - JRrgFormContainer.BORDER_WIDTH_HEIGHT) || i_x > p_FormComponent.getWidth())
                        {
                            p_FormCollection.getSelectedForm().removeRuler(p_focusedFormRuler);
                        }
                    }
                    ;
                    break;
                case FormRuler.TYPE_HORIZ:
                    {
                        if (i_y <= (0 - JRrgFormContainer.BORDER_WIDTH_HEIGHT) || i_y > p_FormComponent.getHeight())
                        {
                            p_FormCollection.getSelectedForm().removeRuler(p_focusedFormRuler);
                        }
                    }
                    ;
                    break;
            }

            LabelCX.setText("");
            LabelCY.setText("");

            p_focusedFormRuler = null;
        }
        else if (p_FormComponent.isResizing())
        {
            p_FormComponent.setResizingToComponent();
        }
        else if (lg_SubComponentIsDragged)
        {
            AbstractFormComponent p_placedComponent = p_FormComponent.getPlacedComponent();
            if (p_placedComponent != null)
            {
                p_placedComponent.holdSubcomponent(i_x, i_y);
            }
            lg_SubComponentIsDragged = false;
        }
        p_FormComponent.fillBufferImage();
        p_FormComponent.repaint();
        updatePropertiesPanel(false);
    }

    public void mouseDragged(MouseEvent e)
    {
        AbstractFormComponent p_placedComponent = p_FormComponent.getPlacedComponent();

        if (p_focusedFormRuler != null)
        {
            Rectangle p_viewRect = ScrollPanel.getViewport().getViewRect();

            int i_vx = p_viewRect.x + JRrgFormContainer.RULES_WIDTH;
            int i_vy = p_viewRect.y + JRrgFormContainer.RULES_WIDTH;

            int i_x = e.getX() / p_FormComponent.getScale() - JRrgFormContainer.BORDER_WIDTH_HEIGHT;
            int i_y = e.getY() / p_FormComponent.getScale() - JRrgFormContainer.BORDER_WIDTH_HEIGHT;

            // Отрабатываем смену позиции у перетаскиваемой линейки
            switch (p_focusedFormRuler.getType())
            {
                case FormRuler.TYPE_VERT:
                    {
                        int i_newViewX = (i_x + JRrgFormContainer.BORDER_WIDTH_HEIGHT) * p_FormComponent.getScale();
                        if (i_newViewX <= i_vx) return;

                        p_focusedFormRuler.setCoord(i_x);
                        LabelCX.setText(Integer.toString(i_x));
                    }
                    ;
                    break;
                case FormRuler.TYPE_HORIZ:
                    {
                        int i_newViewY = (i_y + JRrgFormContainer.BORDER_WIDTH_HEIGHT) * p_FormComponent.getScale();
                        if (i_newViewY <= i_vy) return;

                        p_focusedFormRuler.setCoord(i_y);
                        LabelCY.setText(Integer.toString(i_y));
                    }
                    ;
                    break;
            }

            p_FormComponent.repaint();
        }
        else if (p_placedComponent != null)
        {
            if (p_FormComponent.isResizing())
            {
                p_FormComponent.changeResizeCoords(e.getX(), e.getY());

                int i_w = p_FormComponent.getResizingWidth();
                int i_h = p_FormComponent.getResizingHeight();

                LabelWidth.setText(Integer.toString(i_w));
                LabelHeight.setText(Integer.toString(i_h));

            }
            else if (lg_SubComponentIsDragged)
            {
                int i_x = e.getX() / p_FormComponent.getScale() - JRrgFormContainer.BORDER_WIDTH_HEIGHT;
                int i_y = e.getY() / p_FormComponent.getScale() - JRrgFormContainer.BORDER_WIDTH_HEIGHT;

                p_placedComponent.dragSubcomponent(i_x, i_y);

                LabelX.setText(Integer.toString(e.getX() / p_FormComponent.getScale()));
                LabelY.setText(Integer.toString(e.getY() / p_FormComponent.getScale()));

                String s_cx = Integer.toString(i_x);
                String s_cy = Integer.toString(i_y);

                LabelCX.setText(s_cx);
                LabelCY.setText(s_cy);

                p_FormComponent.fillBufferImage();
            }
            else if (!p_placedComponent.isPinned())
            {
                //if (p_FormComponent.contains(e.getX(), e.getY()))
                {
                    int i_x = e.getX() / p_FormComponent.getScale() - i_difX - JRrgFormContainer.BORDER_WIDTH_HEIGHT;
                    int i_y = e.getY() / p_FormComponent.getScale() - i_difY - JRrgFormContainer.BORDER_WIDTH_HEIGHT;

                    p_placedComponent.setX(i_x);
                    p_placedComponent.setY(i_y);
                    p_FormComponent.fillBufferImage();

                    LabelX.setText(Integer.toString(e.getX() / p_FormComponent.getScale() - JRrgFormContainer.BORDER_WIDTH_HEIGHT));
                    LabelY.setText(Integer.toString(e.getY() / p_FormComponent.getScale() - JRrgFormContainer.BORDER_WIDTH_HEIGHT));

                    String s_cx = Integer.toString(i_x);
                    String s_cy = Integer.toString(i_y);
                    LabelCX.setText(s_cx);
                    LabelCY.setText(s_cy);
                }
            }
        }
    }

    public void mouseMoved(MouseEvent e)
    {
        int i_overRulerState = p_FormComponent.isOverRulers(e.getX(), e.getY());
        if (i_overRulerState != JRrgFormContainer.OVER_RULER_NONE)
        {
            p_FormComponent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            LabelX.setText("");
            LabelY.setText("");
        }
        else
        {
            if (e.getComponent().equals(p_FormComponent))
            {
                LabelX.setText(Integer.toString(e.getX() / p_FormComponent.getScale() - JRrgFormContainer.BORDER_WIDTH_HEIGHT));
                LabelY.setText(Integer.toString(e.getY() / p_FormComponent.getScale() - JRrgFormContainer.BORDER_WIDTH_HEIGHT));

                if (p_FormComponent.overResizable(e.getX(), e.getY()) != JRrgFormContainer.RESIZABLE_NONE)
                {
                    p_FormComponent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                else
                {
                    p_FormComponent.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                }
            }
            else
            {
                LabelX.setText("");
                LabelY.setText("");
            }
        }
    }

    private void changeScale(int _scale)
    {
        p_FormComponent.setScale(_scale);

        JViewport p_vp = ScrollPanel.getViewport();

        p_vp.updateUI();

        //ScrollPanel.doLayout();
        ScrollPanel.repaint();
    }

    private void updateMenuState()
    {
        if (p_CurrentFile == null)
        {
            p_Item_FileSave.setEnabled(false);
        }
        else
        {
            p_Item_FileSave.setEnabled(true);
        }

        // Scale
        if (p_View_Scale1.isSelected())
        {
            ScaleLabel.setText("x1");
            changeScale(1);
        }
        else if (p_View_Scale2.isSelected())
        {
            ScaleLabel.setText("x2");
            changeScale(2);
        }
        else if (p_View_Scale4.isSelected())
        {
            ScaleLabel.setText("x4");
            changeScale(4);
        }
        else if (p_View_Scale8.isSelected())
        {
            ScaleLabel.setText("x8");
            changeScale(8);
        }
        else if (p_View_Scale16.isSelected())
        {
            ScaleLabel.setText("x16");
            changeScale(16);
        }

        // Filter
        switch (p_FormComponent.getFilter())
        {
            case JRrgFormContainer.FILTER_NONE:
                p_View_Filter_None.setSelected(true);
                break;
            case JRrgFormContainer.FILTER_NTSC:
                p_View_Filter_NTSC.setSelected(true);
                break;
            case JRrgFormContainer.FILTER_LCD444:
                p_View_Filter_LCD444.setSelected(true);
                break;
            case JRrgFormContainer.FILTER_LCD332:
                p_View_Filter_LCD332.setSelected(true);
                break;
        }

        p_FormComponent.setTVGrid(p_View_TVGrid.isSelected());
        p_FormComponent.setGrid(p_View_Grid.isSelected());
        p_FormComponent.setRulers(p_View_Rulers.isSelected());

        int i_State = AbstractFormComponent.STATE_NORMAL;

        // View mode
        if (p_View_Active.isSelected())
        {
            ViewStatusLabel.setText("SELECTED");
            i_State = AbstractFormComponent.STATE_SELECTED;
        }
        else if (p_View_Inactive.isSelected())
        {
            ViewStatusLabel.setText("DISABLED");
            i_State = AbstractFormComponent.STATE_DISABLED;
        }
        else if (p_View_Normal.isSelected())
        {
            ViewStatusLabel.setText("NORMAL");
            i_State = AbstractFormComponent.STATE_NORMAL;
        }
        else if (p_View_Pressed.isSelected())
        {
            ViewStatusLabel.setText("PRESSED");
            i_State = AbstractFormComponent.STATE_PRESSED;
        }

        if (p_undoComponent == null)
        {
            p_Item_EditUndo.setEnabled(false);
        }
        else
        {
            p_Item_EditUndo.setEnabled(true);
        }

        if (p_redoComponent == null)
        {
            p_Item_EditRedo.setEnabled(false);
        }
        else
        {
            p_Item_EditRedo.setEnabled(true);
        }

        p_MainFrame.getJMenuBar().validate();

        // Кнопки отвечающие за добавление, удаление форм
        if (p_FormCollection.getFormsNumber() == 1)
        {
            p_Item_RemoveForm.setEnabled(false);
        }
        else
        {
            p_Item_RemoveForm.setEnabled(true);
        }

        if (p_Clipboard == null)
        {
            p_Item_EditPaste.setEnabled(false);
        }
        else
        {
            p_Item_EditPaste.setEnabled(true);
        }

        p_FormCollection.changeStateForComponents(i_State);
        p_FormComponent.fillBufferImage();
        p_FormComponent.validate();
        p_FormComponent.repaint();//updateUI();
    }

    private void initTrees()
    {
        ResourcesTree.setRootVisible(false);
        FormTree.setRootVisible(true);
        ResourcesTree.addTreeSelectionListener(this);
        FormTree.addTreeSelectionListener(this);
    }

    protected static final String XML_RESOURCES = "Resources";
    protected static final String XML_FORMS = "Forms";
    protected static final String XML_RRGFRM = "RRGFRM";

    public static final String FILE_EXTENSION = ".rfrm";

    public static final void SaveToFile(File _name, ResourceContainer _resources, FormCollection _collection, boolean _relative) throws IOException
    {
        if (!_name.getName().toUpperCase().endsWith(FILE_EXTENSION.toUpperCase()))
        {
            _name = new File(_name.getAbsolutePath() + FILE_EXTENSION);
        }
        FileOutputStream p_fos = new FileOutputStream(_name);
        PrintStream p_PrintStream = new PrintStream(p_fos);
        p_PrintStream.println("<?xml version=\"1.0\" encoding=\"windows-1251\" ?>");
        p_PrintStream.println("<" + XML_RRGFRM + ">");
        if (_resources != null)
        {
            SaveResourcesIntoStream(_name, p_PrintStream, _resources, _relative);
        }

        if (_collection != null)
        {
            SaveFormIntoStream(p_PrintStream, _collection);
        }
        p_PrintStream.println("</" + XML_RRGFRM + ">");
        p_fos.close();
    }

    public static final void SaveResourcesIntoStream(File _file, PrintStream _printStream, ResourceContainer _resourceContainer, boolean _relative)
    {
        if (_resourceContainer == null) return;
        _printStream.println("<" + XML_RESOURCES + ">");
        for (int li = 0; li < _resourceContainer.getSize(); li++)
        {
            AbstractRrgResource p_resource = _resourceContainer.getComponentArray()[li];
            p_resource.saveAsXML(_printStream, _file, _relative);
        }
        _printStream.println("</" + XML_RESOURCES + ">");
    }

    public static final void SaveFormIntoStream(PrintStream _printStream, FormCollection _formCollection)
    {
        if (_formCollection == null) return;
        _formCollection.saveAsXML(_printStream);
    }

    public boolean accept(File f)
    {
        if (f == null) return false;
        if (f.isDirectory()) return true;
        String s_filename = f.getName().toUpperCase();
        if (s_filename.endsWith(FILE_EXTENSION.toUpperCase())) return true;
        return false;
    }

    public String getDescription()
    {
        return "RRG forms";
    }

    protected void LoadFromFile(File _file, ResourceContainer _resources, FormCollection _collection) throws IOException
    {
        DocumentBuilderFactory p_dbf = DocumentBuilderFactory.newInstance();
        p_dbf.setIgnoringComments(true);
        DocumentBuilder p_db = null;

        try
        {
            p_db = p_dbf.newDocumentBuilder();
            Document p_doc = null;
            p_doc = p_db.parse(_file);

            Element p_mainelement = p_doc.getDocumentElement();
            if (!p_mainelement.getNodeName().equals(XML_RRGFRM)) throw new IOException("This is not a RRG FORM file");

            NodeList p_list = p_mainelement.getElementsByTagName(XML_RESOURCES);
            if (p_list.getLength() == 0) throw new IOException("There is not a " + XML_RESOURCES + " tag");
            Element p_res = (Element) p_list.item(0);
            p_list = p_res.getElementsByTagName(AbstractRrgResource.XML_RESOURCE);
            _resources.removeAll();

            File p_pathToFile = Utilities.getFilePath(_file);

            for (int li = 0; li < p_list.getLength(); li++)
            {
                Element p_current = (Element) p_list.item(li);
                String s_ID = p_current.getAttribute(AbstractRrgResource.XML_ID);
                String s_type = p_current.getAttribute(AbstractRrgResource.XML_TYPE);
                if (s_ID.length() == 0 || s_type.length() == 0) throw new IOException("Error file format");

                int i_type = Integer.parseInt(s_type);

                AbstractRrgResource p_newResource = null;
                switch (i_type)
                {
                    case AbstractRrgResource.TYPE_FONT:
                        p_newResource = new RrgResource_Font(s_ID, null, false, false, false, 8);
                        break;
                    case AbstractRrgResource.TYPE_IMAGE:
                        p_newResource = new RrgResource_Image(s_ID, null);
                        break;
                    case AbstractRrgResource.TYPE_SOUND:
                        p_newResource = new RrgResource_Sound(s_ID, null);
                        break;
                    case AbstractRrgResource.TYPE_TEXT:
                        p_newResource = new RrgResource_Text(s_ID, null);
                        break;
                    default:
                        throw new IOException("Unsupported resource type");
                }

                p_newResource.loadFromXML(p_pathToFile, p_current);
                _resources.addResource(p_newResource);
            }

            //---------------Form loading
            _collection.loadFromXML(p_mainelement, _resources);

            MainForm.p_View_Scale1.doClick();
            MainForm.p_View_Normal.doClick();

        }
        catch (Exception ex)
        {
            throw new IOException(ex.getMessage());
        }
    }

    private class TreeDataRenderer implements TreeCellRenderer
    {
        private ImageIcon p_Tree_ResourceImageIcon;
        private ImageIcon p_Tree_ResourceSoundIcon;
        private ImageIcon p_Tree_ResourceFontIcon;
        private ImageIcon p_Tree_ResourceTextIcon;

        private ImageIcon p_Tree_FormButtonIcon;
        private ImageIcon p_Tree_FormFormIcon;
        private ImageIcon p_Tree_FormImageIcon;
        private ImageIcon p_Tree_FormLabelIcon;
        private ImageIcon p_Tree_FormPathIcon;
        private ImageIcon p_Tree_FormCustomAreaIcon;

        private ImageIcon p_Tree_FormHidden;
        private ImageIcon p_Tree_FormPinned;

        public TreeDataRenderer()
        {
            super();

            //Loading of icons
            p_Tree_ResourceImageIcon = Utilities.loadIconFromResource("res_img_ico.gif");
            p_Tree_ResourceSoundIcon = Utilities.loadIconFromResource("res_snd_ico.gif");
            p_Tree_ResourceFontIcon = Utilities.loadIconFromResource("res_fnt_ico.gif");
            p_Tree_ResourceTextIcon = Utilities.loadIconFromResource("res_txt_ico.gif");

            p_Tree_FormButtonIcon = Utilities.loadIconFromResource("cmp_btn_ico.gif");
            p_Tree_FormFormIcon = Utilities.loadIconFromResource("cmp_frm_ico.gif");
            p_Tree_FormImageIcon = Utilities.loadIconFromResource("cmp_img_ico.gif");
            p_Tree_FormLabelIcon = Utilities.loadIconFromResource("cmp_lbl_ico.gif");
            p_Tree_FormPathIcon = Utilities.loadIconFromResource("cmp_pth_ico.gif");
            p_Tree_FormCustomAreaIcon = Utilities.loadIconFromResource("cmp_car_ico.gif");

            p_Tree_FormHidden = Utilities.loadIconFromResource("cmp_hdn_ico.gif");
            p_Tree_FormPinned = Utilities.loadIconFromResource("cmp_pnd_ico.gif");

        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
        {
            if (tree.equals(ResourcesTree))
            {
                if (value instanceof AbstractRrgResource)
                {
                    ImageIcon p_icon = null;
                    switch (((AbstractRrgResource) value).getType())
                    {
                        case AbstractRrgResource.TYPE_FONT:
                            p_icon = p_Tree_ResourceFontIcon;
                            break;
                        case AbstractRrgResource.TYPE_SOUND:
                            p_icon = p_Tree_ResourceSoundIcon;
                            break;
                        case AbstractRrgResource.TYPE_IMAGE:
                            p_icon = p_Tree_ResourceImageIcon;
                            break;
                        case AbstractRrgResource.TYPE_TEXT:
                            p_icon = p_Tree_ResourceTextIcon;
                            break;
                    }

                    JLabel p_result = new JLabel(value.toString(),p_icon,JLabel.LEFT);

                    p_result.setToolTipText(value.toString());


                    if (!selected)
                    {
                        p_result.setBackground(Color.white);
                        p_result.setForeground(Color.black);
                    }
                    else
                    {
                        p_result.setBackground(Color.blue);
                        p_result.setForeground(Color.white);
                    }
                    p_result.setOpaque(true);
                    return p_result;
                }
                else
                {
                    return new JLabel("<>");
                }
            }
            else if (tree.equals(FormTree))
            {
                if (value instanceof AbstractFormComponent)
                {
                    ImageIcon p_icon = null;
                    AbstractFormComponent p_value = (AbstractFormComponent) value;
                    switch (p_value.getType())
                    {
                        case AbstractFormComponent.COMPONENT_BUTTON:
                            p_icon = p_Tree_FormButtonIcon;
                            break;
                        case AbstractFormComponent.COMPONENT_IMAGE:
                            p_icon = p_Tree_FormImageIcon;
                            break;
                        case AbstractFormComponent.COMPONENT_FORM:
                            p_icon = p_Tree_FormFormIcon;
                            break;
                        case AbstractFormComponent.COMPONENT_LABEL:
                            p_icon = p_Tree_FormLabelIcon;
                            break;
                        case AbstractFormComponent.COMPONENT_PATH:
                            p_icon = p_Tree_FormPathIcon;
                            break;
                        case AbstractFormComponent.COMPONENT_CUSTOMAREA:
                            p_icon = p_Tree_FormCustomAreaIcon;
                            break;
                    }

                    ImageIcon p_ic = p_icon;

                    int i_iconWidth = p_icon.getIconWidth();

                    if (p_value.isHidden()) i_iconWidth += p_Tree_FormHidden.getIconWidth();
                    if (p_value.isPinned()) i_iconWidth += p_Tree_FormPinned.getIconWidth();

                    if (i_iconWidth == p_icon.getIconWidth())
                    {
                        p_ic = p_icon;
                    }
                    else
                    {
                        BufferedImage p_newIcon = new BufferedImage(i_iconWidth, 16, BufferedImage.TYPE_INT_RGB);

                        p_newIcon.getGraphics().drawImage(p_icon.getImage(), 0, 0, null);
                        int i_iw = p_icon.getIconWidth();
                        if (p_value.isHidden())
                        {
                            p_newIcon.getGraphics().drawImage(p_Tree_FormHidden.getImage(), i_iw, 0, null);
                            i_iw += p_Tree_FormHidden.getIconWidth();
                        }
                        if (p_value.isPinned())
                        {
                            p_newIcon.getGraphics().drawImage(p_Tree_FormPinned.getImage(), i_iw, 0, null);
                            i_iw += p_Tree_FormPinned.getIconWidth();
                        }

                        p_ic = new ImageIcon(p_newIcon);
                    }

                    String s_string = value.toString();

                    JLabel p_result = new JLabel(s_string,p_ic,JLabel.LEFT);
                    p_result.setToolTipText(s_string);

                    if (!selected)
                    {
                        p_result.setBackground(Color.white);
                        p_result.setForeground(Color.black);
                    }
                    else
                    {
                        p_result.setBackground(Color.red);
                        p_result.setForeground(Color.white);
                    }
                    p_result.setOpaque(true);
                    return p_result;
                }
                else
                {
                    return new JLabel("<>");
                }
            }
            return null;
        }
    }

    public void focusGained(FocusEvent e)
    {
        if (e.getComponent().equals(ResourcesTree))
        {
            FormTree.setSelectionPath(null);
            updatePropertiesPanel(true);
        }
        else if (e.getComponent().equals(FormTree))
        {
            ResourcesTree.setSelectionPath(null);
            updatePropertiesPanel(false);
        }
    }

    public void focusLost(FocusEvent e)
    {
    }

    protected void redo()
    {
        if (p_redoComponent != null)
        {
            if (p_redoComponent instanceof AbstractFormComponent)
            {
                ((AbstractFormComponent) p_redoComponent).rollbackTransaction(true);
                updatePropertiesPanel(false);
            }
            else if (p_redoComponent instanceof AbstractRrgResource)
            {
                ((AbstractRrgResource) p_undoComponent).rollbackTransaction();
            }

            p_undoComponent = p_redoComponent;
            p_redoComponent = null;

            p_FormComponent.fillBufferImage();
            p_FormComponent.validate();
            p_FormComponent.repaint();//updateUI();
            lg_currentFileChanged = true;
        }
    }

    protected void copyToClipboard()
    {
        synchronized (p_FormCollection)
        {
            TreePath p_path = FormTree.getSelectionPath();
            if (p_path != null)
            {
                AbstractFormComponent p_component = (AbstractFormComponent) p_path.getLastPathComponent();
                if (p_component != null)
                {
                    p_Clipboard = AbstractFormComponent.cloneComponent(p_component);
                }
            }
        }
    }

    protected void pasteFromClipboard()
    {
        synchronized (p_FormCollection)
        {

            if (p_Clipboard == null) return;

            String s_ID = p_Clipboard.getID();

            FormContainer p_currentForm = p_FormCollection.getSelectedForm();
            if (p_currentForm == null) return;

            for (int li = 0; li < 99999; li++)
            {
                String s_newID = s_ID + "_" + li;
                if (p_currentForm.containsID(s_newID)) continue;

                AbstractFormComponent p_newComponent = AbstractFormComponent.cloneComponent(p_Clipboard);
                p_newComponent.setID(s_newID);
                p_currentForm.addComponent(p_newComponent);
                break;
            }
        }
    }

    protected void undo()
    {
        if (p_undoComponent != null)
        {
            if (p_undoComponent instanceof AbstractFormComponent)
            {
                ((AbstractFormComponent) p_undoComponent).rollbackTransaction(true);
                updatePropertiesPanel(false);
            }
            else if (p_undoComponent instanceof AbstractRrgResource)
            {
                ((AbstractRrgResource) p_undoComponent).rollbackTransaction();
            }

            p_redoComponent = p_undoComponent;
            p_undoComponent = null;

            p_FormComponent.fillBufferImage();
            p_FormComponent.validate();
            p_FormComponent.repaint();//updateUI();
            lg_currentFileChanged = true;
        }
    }

    protected void closeApplication()
    {
        if (lg_currentFileChanged)
        {
            if (!Utilities.askDialog(p_MainFrame, "Do you really want to exit?", "You changed the form and have not saved it, do you really want to close the application?")) return;
        }
        p_MainFrame.hide();
        System.exit(1);
    }

    protected FormContainer createNewForm()
    {
        FormTree.setSelectionPath(null);
        updatePropertiesPanel(false);

        FormContainer p_FormContainer = new FormContainer("Untitled_form", 320, 240);

        p_FormCollection.addForm(p_FormContainer);

        selectForm(p_FormContainer);

        return p_FormContainer;
    }

    protected void selectForm(FormContainer _form)
    {
        p_FormCollection.selectForm(_form);

        p_FormComponent.setFormSize(p_FormCollection.getSelectedForm().getWidth(), p_FormCollection.getSelectedForm().getHeight());
        p_FormComponent.setBackground(p_FormCollection.getSelectedForm().getBackgroundColor());
        p_FormComponent.setFormContainer(p_FormCollection.getSelectedForm());

        p_NewComponent_Form.setFormContainer(p_FormCollection.getSelectedForm());

        FormTree.setSelectionPath(null);

        FormTree.validate();

        updatePropertiesPanel(false);
    }

    protected void fillFormsList()
    {
        p_FormsList.removeAllItems();
        for (int li = 0; li < p_FormCollection.getFormsNumber(); li++)
        {
            p_FormsList.addItem(p_FormCollection.getFormAt(li));
        }
    }

    public void windowActivated(WindowEvent e)
    {
    }

    public void windowClosed(WindowEvent e)
    {

    }

    public void windowClosing(WindowEvent e)
    {
        closeApplication();
    }

    public void windowDeactivated(WindowEvent e)
    {
    }

    public void windowDeiconified(WindowEvent e)
    {
    }

    public void windowIconified(WindowEvent e)
    {
    }

    public void windowOpened(WindowEvent e)
    {
    }
}


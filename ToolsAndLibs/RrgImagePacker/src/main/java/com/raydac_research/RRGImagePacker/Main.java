package com.raydac_research.RRGImagePacker;

import com.raydac_research.RRGImagePacker.Container.*;
import com.raydac_research.RRGImagePacker.Container.Container;
import com.raydac_research.RRGImagePacker.Utils.Utilities;
import com.raydac_research.RRGImagePacker.Utils.SelectNewResourceDialog;
import com.raydac_research.RRGImagePacker.tga.TGAImageWriter;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.*;
import java.io.*;

public class Main extends JFrame implements TreeSelectionListener, WindowListener, ActionListener
{
    private static final String FILE_EXTENSION = ".rimg";
    private fFilter p_MainFilter = new fFilter();
    private JButton p_AppendButton;
    private JPanel p_ButtonPanel;
    private JPanel p_RightPanel;

    private SelectNewResourceDialog p_addNewResourceDialog;

    protected class fFilter extends FileFilter
    {
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
            return "RRG image blocks";
        }
    }

    private JSplitPane p_MainPanel;
    private JTree p_ProjectTree;
    private JButton p_ButtonRemoveImage;
    private JButton p_ButtonAddImage;
    private JCheckBox p_CheckBox_Exclude;

    private Container p_treeContainer;
    private JButton p_ButtonUpResource;
    private JButton p_ButtonDownResource;
    private JRootPane p_MPanel;

    private ImagePropertiesPanel p_imgPanel;
    private PalettePropertiesPanel p_palPanel;

    private JMenu p_Menu_File = new JMenu("File");
    private JMenuItem p_Item_New = new JMenuItem("New");
    private JMenuItem p_Item_Open = new JMenuItem("Open");
    private JMenuItem p_Item_Save = new JMenuItem("Save");
    private JMenuItem p_Item_SaveAs = new JMenuItem("Save as");
    private JMenuItem p_Item_Extract = new JMenuItem("Extract");
    private JMenuItem p_Item_ExtractAll = new JMenuItem("Extract all");
    private JMenuItem p_Item_Compile = new JMenuItem("Compile");
    private JMenuItem p_Item_Exit = new JMenuItem("Exit");

    private JMenu p_Menu_Edit = new JMenu("Edit");
    private JMenuItem p_Item_Add = new JMenuItem("Add resource");
    private JMenuItem p_Item_Remove = new JMenuItem("Remove resource");

    private JMenu p_Menu_Help = new JMenu("Help");
    private JMenuItem p_Item_About = new JMenuItem("About");

    private File p_currentFile = null;

    public JMenuBar makeMainMenuBar()
    {
        JMenuBar p_menuBar = new JMenuBar();

        p_Menu_File.add(p_Item_New);
        p_Item_New.addActionListener(this);

        p_Menu_File.add(p_Item_Open);
        p_Item_Open.addActionListener(this);

        p_Menu_File.add(p_Item_Save);
        p_Item_Save.addActionListener(this);

        p_Menu_File.add(p_Item_SaveAs);
        p_Item_SaveAs.addActionListener(this);

        p_Menu_File.addSeparator();
        p_Menu_File.add(p_Item_Extract);
        p_Item_Extract.addActionListener(this);

        p_Menu_File.add(p_Item_ExtractAll);
        p_Item_ExtractAll.addActionListener(this);

        p_Menu_File.addSeparator();
        p_Menu_File.add(p_Item_Compile);
        p_Item_Compile.addActionListener(this);

        p_Menu_File.addSeparator();
        p_Menu_File.add(p_Item_Exit);
        p_Item_Exit.addActionListener(this);

        p_menuBar.add(p_Menu_File);

        p_Menu_Edit.add(p_Item_Add);
        p_Item_Add.addActionListener(this);

        p_Menu_Edit.add(p_Item_Remove);
        p_Item_Remove.addActionListener(this);

        p_menuBar.add(p_Menu_Edit);

        p_Menu_Help.add(p_Item_About);
        p_Item_About.addActionListener(this);

        p_menuBar.add(p_Menu_Help);

        return p_menuBar;
    }

    public void actionPerformed(ActionEvent e)
    {
        Object p_src = e.getSource();
        Object p_focusedObject = null;
        TreePath p_path = p_ProjectTree.getSelectionPath();
        if (p_path != null)
        {
            Object[] ap_obj = p_path.getPath();
            switch (ap_obj.length)
            {
                case 0:
                    ;
                case 1:
                    ;
                    break;
                case 2:
                    {
                        p_focusedObject = ap_obj[1];
                    }
                    ;
                    break;
                case 3:
                    {
                        p_focusedObject = ap_obj[2];
                    }
                    ;
                    break;
            }
        }

        if (p_src.equals(p_Item_Exit))
        {
            exitApplication();
        }
        else if (p_src.equals(p_Item_Add) || p_src.equals(p_ButtonAddImage))
        {
            addResource();
        }
        else if (p_src.equals(p_AppendButton))
        {
            TreePath p_tp = p_ProjectTree.getSelectionPath();
            if (p_tp == null) return;

            fillFromPanel(p_tp);
            fillFromPath(p_tp);
        }
        else if (p_src.equals(p_Item_Remove) || p_src.equals(p_ButtonRemoveImage))
        {

        }
        else if (p_src.equals(p_Item_Save))
        {
            try
            {
                saveFile(p_currentFile);
            }
            catch (IOException e1)
            {
                Utilities.showErrorDialog(this, "Error during saving", e1.getMessage());
            }
        }
        else if (p_src.equals(p_ButtonDownResource))
        {
            if (p_focusedObject instanceof ImageCnt)
            {
                p_treeContainer.getImageContainer().downImage((ImageCnt) p_focusedObject);
                p_ProjectTree.setModel(p_treeContainer);
                expandTree(p_focusedObject);
            }
        }
        else if (p_src.equals(p_ButtonUpResource))
        {
            if (p_focusedObject instanceof ImageCnt)
            {
                p_treeContainer.getImageContainer().upImage((ImageCnt) p_focusedObject);
                p_ProjectTree.setModel(p_treeContainer);
                expandTree(p_focusedObject);
            }
        }
        else if (p_src.equals(p_Item_SaveAs))
        {
            try
            {
                p_currentFile = saveFile(null);
            }
            catch (IOException e1)
            {
                Utilities.showErrorDialog(this, "Error during saving", e1.getMessage());
            }
        }
        else if (p_src.equals(p_Item_Extract))
        {

        }
        else if (p_src.equals(p_Item_New))
        {

        }
        else if (p_src.equals(p_Item_Open))
        {
            try
            {
                File p_f = openFile();

                p_currentFile = p_f;
            }
            catch (IOException e1)
            {
                Utilities.showErrorDialog(this, "Error of opening", e1.getMessage());
                p_currentFile = null;
            }
        }
        else if (p_src.equals(p_Item_ExtractAll))
        {
            extractAllResources();
        }
        else if (p_src.equals(p_Item_Compile))
        {

        }
        else if (p_src.equals(p_Item_About))
        {

        }
    }

    protected void extractAllResources()
    {
        // Выбираем директорию
        File p_dir = Utilities.selectDirectoryForSave(this, null, "Select directory for extraction", null);
        if (p_dir == null) return;

        // Картинки
        ImageContainer p_imgCont = p_treeContainer.getImageContainer();
        for (int li = 0; li < p_imgCont.getSize(); li++)
        {
            ImageCnt p_imgCnt = p_imgCont.getImageAt(li);

            if (p_imgCnt.isExternalImageLink() || p_imgCnt.isLink()) continue;
            String s_name = p_imgCnt.getName() + ".tga";

            File p_file = new File(p_dir, s_name);

            try
            {
                saveTGAImage(p_file, p_imgCnt.getImage());
            }
            catch (IOException e)
            {
                Utilities.showInfoDialog(this, "Exctraction", "I can't extract \"" + p_imgCnt.getName() + "\" image");
            }
        }

        // Палитры
        PaletteContainer p_palCont = p_treeContainer.getPaletteContainer();
        for (int li = 0; li < p_palCont.getSize(); li++)
        {
            PaletteCnt p_palCnt = p_palCont.getPaletteAt(li);

            String s_name = p_palCnt.getName() + ".pal";

            File p_file = new File(p_dir, s_name);

            try
            {
                p_palCnt.savePALPalette(p_file);
            }
            catch (IOException e)
            {
                Utilities.showInfoDialog(this, "Exctraction", "I can't extract \"" + p_palCnt.getName() + "\" palette");
            }
        }
    }

    protected File openFile() throws IOException
    {
        File p_file = Utilities.selectFileForOpen(this, p_MainFilter, "Select a block file", null);
        if (p_file == null) return null;

        p_treeContainer.readFromFile(p_file);

        return p_file;
    }

    protected void addResource()
    {
        Object p_newObject = p_addNewResourceDialog.showDialog(p_treeContainer);
        p_ProjectTree.setModel(p_treeContainer);
        expandTree(p_newObject);
    }

    protected void expandTree(Object _focusedObj)
    {
        TreePath p_path = p_treeContainer.getTreePath(p_treeContainer.getImageContainer());
        p_ProjectTree.expandPath(p_path);
        p_path = p_treeContainer.getTreePath(p_treeContainer.getPaletteContainer());
        p_ProjectTree.expandPath(p_path);
       // p_ProjectTree.setSelectionPath(p_treeContainer.getTreePath(_focusedObj));
    }

    protected void loadResource(File _resourceFile) throws IOException
    {
        String s_fileName = _resourceFile.getName().toLowerCase();
        String s_name = Utilities.getFileNameWithoutExt(s_fileName);

        if (s_fileName.endsWith(".act"))
        {
            // Грузим палитру
            int[] ai_palette = PaletteCnt.loadACTPAlette(_resourceFile);

            String s_fName = s_name;

            for (int li = 0; li < 1000; li++)
            {
                if (p_treeContainer.getPaletteContainer().getPaletteForName(s_fName) != null)
                {
                    s_fName = s_name + "_" + li;
                }
                else
                    break;
            }

            PaletteCnt p_palCnt = p_treeContainer.addPalette();

            p_palCnt.setName(s_fName);
            p_palCnt.setFileName(_resourceFile.getCanonicalPath());

            p_palCnt.loadFromArray(ai_palette);
        }
        else if (s_fileName.endsWith(".pal"))
        {
            // Грузим палитру
            int[] ai_palette = PaletteCnt.loadPALPalette(_resourceFile);

            String s_fName = s_name;

            for (int li = 0; li < 1000; li++)
            {
                if (p_treeContainer.getPaletteContainer().getPaletteForName(s_fName) != null)
                {
                    s_fName = s_name + "_" + li;
                }
                else
                    break;
            }

            PaletteCnt p_palCnt = p_treeContainer.addPalette();

            p_palCnt.setName(s_fName);
            p_palCnt.setFileName(_resourceFile.getCanonicalPath());
            p_palCnt.loadFromArray(ai_palette);
        }
        else
        {
            // Грузим картинку
            try
            {
                BufferedImage p_image = null;//convertFileToImage(_resourceFile);

                String s_fName = s_name;

                for (int li = 0; li < 1000; li++)
                {
                    if (p_treeContainer.getImageContainer().getImageForName(s_fName) != null)
                    {
                        s_fName = s_name + "_" + li;
                    }
                    else
                        break;
                }

                ImageCnt p_imgCnt = p_treeContainer.addImage();

                p_imgCnt.setName(s_fName);
                p_imgCnt.setSource(_resourceFile.getCanonicalPath());
                p_imgCnt.setImage(p_image);
            }
            catch (IOException e)
            {
                throw new IOException("I can't load " + s_fileName);
            }
        }
    }


    protected void loadFile() throws IOException
    {
        File p_file = Utilities.selectFileForOpen(this, p_MainFilter, "Open an image block file", null);
        if (p_file == null) return;
        p_treeContainer.readFromFile(p_file);
    }

    protected File saveFile(File _file) throws IOException
    {
        if (_file == null)
        {
            _file = Utilities.selectFileForSave(this, p_MainFilter, "Save the image block file", null);
            if (_file == null) return _file;
        }

        String s_path = _file.getCanonicalPath();

        if (!s_path.toLowerCase().endsWith(FILE_EXTENSION))
        {
            s_path += FILE_EXTENSION;
            _file = new File(s_path);
        }

        p_treeContainer.saveToFile(_file);
        return _file;
    }

    public Main()
    {
        super();

        p_addNewResourceDialog = new SelectNewResourceDialog(this);

        setContentPane(p_MainPanel);

        setBounds(0, 0, 300, 300);
        p_treeContainer = new Container("Untitled");

        p_ProjectTree.setModel(p_treeContainer);
        p_ProjectTree.addTreeSelectionListener(this);

        p_imgPanel = new ImagePropertiesPanel();
        p_palPanel = new PalettePropertiesPanel();

        p_ButtonAddImage.addActionListener(this);
        p_ButtonDownResource.addActionListener(this);
        p_ButtonRemoveImage.addActionListener(this);
        p_ButtonUpResource.addActionListener(this);

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setJMenuBar(makeMainMenuBar());

        p_RightPanel.removeAll();
        p_RightPanel.setLayout(new BorderLayout(0, 0));

        p_MPanel = new JRootPane();

        p_RightPanel.add(p_MPanel, BorderLayout.CENTER);
        p_RightPanel.add(p_ButtonPanel, BorderLayout.SOUTH);

        p_MPanel.setVisible(false);
        p_ButtonPanel.setVisible(false);

        p_AppendButton.addActionListener(this);

        updateMenu();
        addWindowListener(this);
        show();
    }

    public static final void main(String[] _args)
    {
        new Main();
    }

    protected void fillFromPath(TreePath _path)
    {
        p_MPanel.setVisible(false);
        p_ButtonPanel.setVisible(false);

        if (_path == null) return;
        Object[] p_way = _path.getPath();

        p_CheckBox_Exclude.setEnabled(false);

        switch (p_way.length)
        {
            case 0:
            case 2:
                ;
                break;

            case 1:
                {
                    if (p_way[0].equals(p_treeContainer))
                    {
                        p_MPanel.setContentPane(p_treeContainer.getPanel());
                        p_MPanel.setVisible(true);
                        p_ButtonPanel.setVisible(true);
                    }
                }
                ;
                break;
            case 3:
                {
                    if (p_way[1].equals(p_treeContainer.getImageContainer()))
                    {
                        ImageCnt p_imgCnt = (ImageCnt) p_way[2];
                        p_MPanel.setContentPane(p_imgPanel.getPanel(p_imgCnt, p_treeContainer));
                        p_CheckBox_Exclude.setEnabled(true);
                        p_CheckBox_Exclude.setSelected(p_imgCnt.isExcluded());
                        p_MPanel.setVisible(true);
                        p_ButtonPanel.setVisible(true);
                    }
                    else if (p_way[1].equals(p_treeContainer.getPaletteContainer()))
                    {
                        PaletteCnt p_imgCnt = (PaletteCnt) p_way[2];
                        p_MPanel.setContentPane(p_palPanel.getPanel(p_imgCnt, p_treeContainer));
                        p_MPanel.setVisible(true);
                        p_ButtonPanel.setVisible(true);
                    }
                }
                ;
                break;
        }

    }

    protected void fillFromPanel(TreePath _path)
    {
        if (!p_MPanel.isVisible()) return;
        if (_path == null) return;

        Object[] p_way = _path.getPath();
        switch (p_way.length)
        {
            case 0:
            case 2:
                ;
                break;
            case 1:
                {
                    if (p_way[0].equals(p_treeContainer))
                    {
                        String s_str = p_treeContainer.isDataOK();
                        if (s_str != null)
                        {
                            Utilities.showErrorDialog(p_MPanel, "Error", s_str);
                            return;
                        }

                        p_treeContainer.fillDataFromPanel();
                    }
                }
                ;
                break;
            case 3:
                {
                    if (p_way[1].equals(p_treeContainer.getImageContainer()))
                    {
                        ImageCnt p_imgCnt = (ImageCnt) p_way[2];

                        String s_str = p_imgPanel.isDataOk();
                        if (s_str != null)
                        {
                            Utilities.showErrorDialog(p_MPanel, "Error", s_str);
                            return;
                        }

                        p_imgCnt.setExcludedFlag(p_CheckBox_Exclude.isSelected());

                        p_imgPanel.fillObjectFromPanel(p_imgCnt);
                    }
                    else if (p_way[1].equals(p_treeContainer.getPaletteContainer()))
                    {
                        PaletteCnt p_palCnt = (PaletteCnt) p_way[2];

                        String s_str = p_palPanel.isDataOk();
                        if (s_str != null)
                        {
                            Utilities.showErrorDialog(p_MPanel, "Error", s_str);
                            return;
                        }

                        p_palPanel.fillObjectFromPanel(p_palCnt);
                    }
                }
                ;
                break;
        }

    }

    protected void updateMenu()
    {
        TreePath p_path = p_ProjectTree.getSelectionPath();

        //p_ButtonAddImage.setEnabled(false);
        p_ButtonRemoveImage.setEnabled(false);
        p_ButtonDownResource.setEnabled(false);
        p_ButtonUpResource.setEnabled(false);

        //p_Item_Add.setEnabled(false);
        p_Item_Remove.setEnabled(false);
        p_Item_Extract.setEnabled(false);

        if (p_path == null) return;

        Object[] ap_obj = p_path.getPath();

        switch (ap_obj.length)
        {
            case 0:
            case 1:
                return;
            case 2:
                {
                    p_ButtonAddImage.setEnabled(true);
                    p_Item_Add.setEnabled(true);
                }
                ;
                break;
            case 3:
                {
                    //p_ButtonAddImage.setEnabled(true);
                    p_ButtonRemoveImage.setEnabled(true);
                    //p_Item_Add.setEnabled(true);
                    p_Item_Remove.setEnabled(true);
                    p_ButtonDownResource.setEnabled(true);
                    p_ButtonUpResource.setEnabled(true);
                }
                ;
                break;
        }
    }

    protected void exitApplication()
    {
        hide();
        System.exit(1);
    }

    public void valueChanged(TreeSelectionEvent e)
    {
//        fillFromPanel(e.getOldLeadSelectionPath());
        p_CheckBox_Exclude.setEnabled(false);

        fillFromPath(e.getNewLeadSelectionPath());
        updateMenu();
    }

    public void windowActivated(WindowEvent e)
    {

    }

    public void windowClosed(WindowEvent e)
    {

    }

    public void windowClosing(WindowEvent e)
    {
        exitApplication();
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

    public static void saveTGAImage(File _file, BufferedImage _image) throws IOException
    {
        FileOutputStream p_fos = new FileOutputStream(_file);
        TGAImageWriter.WriteImageAsARGB(_image, p_fos);
        p_fos.close();
    }

}

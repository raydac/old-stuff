package com.raydac_research.FormEditor.ExportModules.RrgScene;

import com.raydac_research.FormEditor.RrgFormComponents.*;
import com.raydac_research.FormEditor.RrgFormResources.ResourceContainer;
import com.raydac_research.FormEditor.ExportModules.AbstractFormExportModule;
import com.raydac_research.FormEditor.ExportModules.FormsList;
import com.raydac_research.FormEditor.ExportModules.RrgScene.Languages.Generator_Java;
import com.raydac_research.FormEditor.Misc.Utilities;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class export_RRG_SCENE extends FileFilter implements AbstractFormExportModule, ActionListener
{
    private JPanel p_MainPanel;
    private JComboBox p_SupportedImageFormatsList;
    private JCheckBox p_CheckBoxSaveTextAsImages;
    private JCheckBox p_CheckBox_SaveChannelData;
    private JCheckBox p_CheckBoxSaveModifiedImagesAsImages;
    private JCheckBox p_CheckBoxSaveFontsData;
    private JCheckBox p_CheckBoxSaveOffsetsToResources;
    private JCheckBox p_CheckBox_Support_Buttons;
    private JCheckBox p_CheckBox_Support_Labels;
    private JCheckBox p_CheckBoxSaveSounds;
    private JCheckBox p_CheckBox_Support_Images;
    private JCheckBox p_CheckBoxSaveImageTypes;
    private JCheckBox p_CheckBoxSaveSoundTypes;
    private JCheckBox p_CheckBoxSaveFontTypes;
    private JCheckBox p_CheckBoxSaveButtonTextAsImages;
    private JCheckBox p_CheckBoxSaveImages;
    private JCheckBox p_CheckBoxSaveTexts;
    private JComboBox p_ProgrammingLanguageList;
    private JCheckBox p_CheckBox_Support_CustomAreas;

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource().equals(p_CheckBoxSaveTextAsImages))
        {
            if (p_CheckBoxSaveTextAsImages.isSelected())
            {
                p_CheckBoxSaveFontsData.setEnabled(false);
            }
            else
            {
                p_CheckBoxSaveFontsData.setEnabled(true);
            }
        }
    }

    private static final int IMAGETYPE_PNG = 0;

    private static final int LANGTYPE_NONE = 0;
    private static final int LANGTYPE_JAVA = 1;

    private static final String[] as_imageFormats = new String[]{"PNG"};
    private static final String[] as_programmingLanguages = new String[]{"<NO GENERATE>","Java"};

    private int _getSelectedImageType()
    {
        switch (p_SupportedImageFormatsList.getSelectedIndex())
        {
            case 0:
                return IMAGETYPE_PNG;
        }
        return -1;
    }

    private int _getSelectedLanguage()
    {
        switch (p_ProgrammingLanguageList.getSelectedIndex())
        {
            case 0:
                return LANGTYPE_NONE;
            case 1:
                return LANGTYPE_JAVA;
        }
        return -1;
    }

    private AbstractLanguageHeaderGenerator getLanguageGenerator(int _langId)
    {
        switch (_langId)
        {
            case LANGTYPE_JAVA: return new Generator_Java();
        }
        return null;
    }

    public void init(FormCollection _collection)
    {
    }

    public export_RRG_SCENE()
    {
        super();

        //-------------------
        p_SupportedImageFormatsList.removeAllItems();
        for (int li = 0; li < as_imageFormats.length; li++)
        {
            p_SupportedImageFormatsList.addItem(as_imageFormats[li]);
        }
        p_SupportedImageFormatsList.setSelectedIndex(0);

        //-------------------
        p_ProgrammingLanguageList.removeAllItems();
        for (int li = 0; li < as_programmingLanguages.length; li++)
        {
            p_ProgrammingLanguageList.addItem(as_programmingLanguages[li]);
        }
        p_ProgrammingLanguageList.setSelectedIndex(0);

        //-------------------
        p_CheckBoxSaveTextAsImages.addActionListener(this);
    }

    public JPanel getPanel()
    {
        return p_MainPanel;
    }

    public String isDataOk()
    {
        return null;
    }

    public FileFilter getFileFilter()
    {
        return this;
    }

    public void exportData(File _file, ResourceContainer _resources, FormsList _list) throws IOException
    {
        if (_list.getSize()==0) return;

        int     i_ImageFormat = _getSelectedImageType();
        int     i_languageFormat = _getSelectedLanguage();

        boolean lg_SaveTextAsImage = p_CheckBoxSaveTextAsImages.isSelected();
        boolean lg_SaveChannelData = p_CheckBox_SaveChannelData.isSelected();
        boolean lg_SaveFontsData = p_CheckBoxSaveFontsData.isSelected();
        boolean lg_SaveOffsetsToResources = p_CheckBoxSaveOffsetsToResources.isSelected();
        boolean lg_SaveModifiedImagesAsImages = p_CheckBoxSaveModifiedImagesAsImages.isSelected();
        boolean lg_SaveSounds = p_CheckBoxSaveSounds.isSelected();
        boolean lg_SupportButtons = p_CheckBox_Support_Buttons.isSelected();
        boolean lg_SupportImages = p_CheckBox_Support_Images.isSelected();
        boolean lg_SupportLabel = p_CheckBox_Support_Labels.isSelected();
        boolean lg_SupportCustomArea = p_CheckBox_Support_CustomAreas.isSelected();
        boolean lg_SaveImageTypes = p_CheckBoxSaveImageTypes.isSelected();
        boolean lg_SaveFontTypes = p_CheckBoxSaveFontTypes.isSelected();
        boolean lg_SaveSoundTypes = p_CheckBoxSaveSoundTypes.isSelected();
        boolean lg_SaveButtonTextsAsImages = p_CheckBoxSaveButtonTextAsImages.isSelected();
        boolean lg_SaveImages = p_CheckBoxSaveImages.isSelected();
        boolean lg_SaveTexts = p_CheckBoxSaveTexts.isSelected();

        RrgScene p_scene = new RrgScene(_list,i_ImageFormat,lg_SaveTextAsImage,lg_SaveChannelData,lg_SaveFontsData,lg_SaveOffsetsToResources,lg_SaveModifiedImagesAsImages,lg_SaveSounds,lg_SupportButtons,lg_SupportCustomArea,lg_SupportImages,lg_SupportLabel,lg_SaveImageTypes,lg_SaveSoundTypes,lg_SaveFontTypes,lg_SaveButtonTextsAsImages,lg_SaveImages,lg_SaveTexts);

        byte [] ab_array = p_scene.toByteArray();

        FileOutputStream p_outStream = new FileOutputStream(_file);
        p_outStream.write(ab_array);
        p_outStream.flush();
        p_outStream.close();

        // Генерируем исходники если требуется
        AbstractLanguageHeaderGenerator p_generator = getLanguageGenerator(i_languageFormat);
        if (p_generator!=null)
        {
            String s_Name = Utilities.getFileNameWithoutExt(_file.getName());
            String s_fileName = s_Name+p_generator.getExtension();
            File p_file = new File(_file.getParent(),s_fileName);

            PrintStream p_stream = new PrintStream(new FileOutputStream(p_file));
            p_generator.generateHeader(s_Name,p_stream,p_scene);
            p_stream.flush();
            p_stream.close();
        }
    }

    public String getName()
    {
        return "Export the form as an RRG Scene";
    }

    public File processFileNameBeforeSaving(File _file)
    {
        if (_file == null) return null;
        if (_file.getAbsolutePath().toUpperCase().endsWith(".SCN")) return _file;
        _file = new File(_file.getAbsolutePath() + ".scn");
        return _file;
    }

    public boolean accept(File f)
    {
        if (f == null) return false;
        if (f.isDirectory()) return true;
        String s_fileName = f.getName().toUpperCase();
        if (s_fileName.endsWith(".SCN")) return true;
        return false;
    }

    public String getDescription()
    {
        return "RRG Scene binary file";
    }

    public boolean supportsMultiform()
    {
        return true;
    }

}


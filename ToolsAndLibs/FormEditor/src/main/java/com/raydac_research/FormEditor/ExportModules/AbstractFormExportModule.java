package com.raydac_research.FormEditor.ExportModules;

import com.raydac_research.FormEditor.RrgFormResources.ResourceContainer;
import com.raydac_research.FormEditor.RrgFormComponents.FormCollection;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;

public interface AbstractFormExportModule
{
    public void init(FormCollection _container);
    public JPanel getPanel();
    public String isDataOk();
    public FileFilter getFileFilter();
    public void exportData(File _file, ResourceContainer _resources, FormsList _form) throws IOException;
    public String getName();
    public File processFileNameBeforeSaving(File _file);
    public boolean supportsMultiform();
}


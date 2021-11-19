package com.raydac_research.FormEditor.ExportModules;

import com.raydac_research.FormEditor.RrgFormResources.ResourceContainer;
import com.raydac_research.FormEditor.RrgFormResources.AbstractRrgResource;
import com.raydac_research.FormEditor.RrgFormResources.RrgResource_Image;
import com.raydac_research.FormEditor.RrgFormResources.RrgResource_Font;
import com.raydac_research.FormEditor.RrgFormComponents.*;
import com.raydac_research.FormEditor.Misc.Utilities;

import javax.swing.filechooser.FileFilter;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Vector;
import java.awt.*;

class export_DOBGUI extends FileFilter implements AbstractFormExportModule
{
    private JTextField p_FormName;
    private JPanel p_MainPanel;

    public boolean accept(File f)
    {
        if (f == null) return false;
        if (f.isDirectory()) return true;
        String s_fileName = f.getName().toUpperCase();
        if (s_fileName.endsWith(".TXT")) return true;
        return false;
    }

    public File processFileNameBeforeSaving(File _file)
    {
        if (_file == null) return null;
        if (_file.getAbsolutePath().toUpperCase().endsWith(".TXT")) return _file;
        _file = new File(_file.getAbsolutePath() + ".txt");
        return _file;
    }

    public String getDescription()
    {
        return "DOB GUI text forms";
    }

    public void init(FormCollection _container)
    {
        p_FormName.setText(_container.getSelectedForm().getID());
    }

    public JPanel getPanel()
    {
        return p_MainPanel;
    }

    public String isDataOk()
    {
        String s_panelName = p_FormName.getText().trim();
        if (s_panelName.length() == 0) return "You have not entered the form name";
        return null;
    }

    public FileFilter getFileFilter()
    {
        return this;
    }

    public void exportData(File _file, ResourceContainer _resources, FormsList _list) throws IOException
    {
        if (_list.getSize() == 0) return;

        FormContainer p_exportedForm = _list.getFirstForm();

        FileOutputStream p_fos = new FileOutputStream(_file);
        PrintStream p_printStream = new PrintStream(p_fos);

        Hashtable p_spriteDecodeTable = new Hashtable();
        Vector p_spriteVector = new Vector();

        // Writing the image list
        p_printStream.println("IMGLIST = {");
        p_printStream.println("name = AutoLoad");
        int i_index = 1;
        for (int li = 0; li < _resources.getSize(); li++)
        {
            AbstractRrgResource p_resource = _resources.getResourceForIndex(li);
            if (p_resource.getType() == AbstractRrgResource.TYPE_IMAGE)
            {
                if (p_exportedForm.getListOfComponentsUseResource(p_resource).size() > 0)
                {
                    RrgResource_Image p_resImage = (RrgResource_Image) p_resource;
                    String s_sprite = "I" + conertIndex(i_index) + " = ";
                    p_spriteDecodeTable.put(p_resource.getResourceID(), new Integer(i_index));
                    p_spriteVector.add(p_resource);
                    i_index++;

                    s_sprite += Utilities.calcRelativePath(_file, p_resImage.getImageFile()) + "," + p_resImage.getResourceID() + ",i";
                    p_printStream.println(s_sprite);
                }
            }
        }
        p_printStream.println("}");

        // Writing the font list
        p_printStream.println("BFTFONTS = {");
        for (int li = 0; li < _resources.getSize(); li++)
        {
            AbstractRrgResource p_resource = _resources.getResourceForIndex(li);
            if (p_resource.getType() == AbstractRrgResource.TYPE_FONT)
            {
                if (p_exportedForm.getListOfComponentsUseResource(p_resource).size() > 0)
                {
                    RrgResource_Font p_resFont = (RrgResource_Font) p_resource;
                    String s_font = p_resFont.getResourceID() + " = " + Utilities.calcRelativePath(_file, p_resFont.getFontFile());
                    p_printStream.println(s_font);
                }
            }
        }
        p_printStream.println("}");

        // Writing the sprites list
        p_printStream.println("SPRITES = {");
        for (int li = 0; li < p_spriteVector.size(); li++)
        {
            RrgResource_Image p_imageRes = (RrgResource_Image) p_spriteVector.elementAt(li);
            p_printStream.println("S" + conertIndex(li + 1) + " = {");
            p_printStream.println("dx = " + p_imageRes.getWidth() + " dy = " + p_imageRes.getHeight());
            p_printStream.println("P1 = R,0,0," + p_imageRes.getResourceID() + ",,,,,,,,,");
            p_printStream.println("}");
        }

        p_printStream.println("}");

        p_printStream.println("GUIFORM = {");
        p_printStream.println("name = \"" + p_FormName.getText().trim() + "\"");
        p_printStream.println("dx = " + p_exportedForm.getWidth());
        p_printStream.println("dy = " + p_exportedForm.getHeight());

        final  int BEGIN_ID_INDEX = 1000;

        // Writing of components
        for (int li = 0; li < p_exportedForm.getSize(); li++)
        {
            AbstractFormComponent p_component = p_exportedForm.getComponentAt(li);
            switch (p_component.getType())
            {
                case AbstractFormComponent.COMPONENT_IMAGE:
                    {
                        RrgFormComponent_Image p_imageComponent = (RrgFormComponent_Image) p_component;
                        p_printStream.println("IMAGE = {");
                        p_printStream.println("object_id = " + (li + BEGIN_ID_INDEX));
                        p_printStream.println("dx = " + p_imageComponent.getWidth() + " dy = " + p_imageComponent.getHeight());
                        p_printStream.println("x = " + p_imageComponent.getX() + " y = " + p_imageComponent.getY());
                        p_printStream.println("sprite=" + ((Integer) p_spriteDecodeTable.get(p_imageComponent.getImageResource().getResourceID())).intValue());
                        p_printStream.println("}");
                    }
                    ;
                    break;
                case AbstractFormComponent.COMPONENT_LABEL:
                    {
                        RrgFormComponent_Label p_labelComponent = (RrgFormComponent_Label) p_component;
                        p_printStream.println("TEXT = {");
                        p_printStream.println("object_id = " + (li + BEGIN_ID_INDEX));
                        p_printStream.println("dx = " + p_labelComponent.getWidth() + " dy = " + p_labelComponent.getHeight());
                        p_printStream.println("x = " + p_labelComponent.getX() + " y = " + p_labelComponent.getY());
                        p_printStream.println("color = "+convertColor(p_labelComponent.getLabelColor()));
                        p_printStream.println("text = \""+p_labelComponent.getLabelText().getText()+"\"");
                        p_printStream.println("align = LEFT");
                        p_printStream.println("alignv = TOP");
                        p_printStream.println("font = "+p_labelComponent.getLabelFont().getResourceID());
                        p_printStream.println("lines = 1");
                        p_printStream.println("}");
                    }
                    ;
                    break;
                case AbstractFormComponent.COMPONENT_BUTTON:
                    {
                        RrgFormComponent_Button p_buttonComponent = (RrgFormComponent_Button) p_component;
                        p_printStream.println("BUTTON = {");
                        p_printStream.println("object_id = " + (li + BEGIN_ID_INDEX));
                        p_printStream.println("dx = " + p_buttonComponent.getWidth() + " dy = " + p_buttonComponent.getHeight());
                        p_printStream.println("x = " + p_buttonComponent.getX() + " y = " + p_buttonComponent.getY());
                        if (p_buttonComponent.getFont() != null)
                        {
                            p_printStream.println("font = \"" + p_buttonComponent.getFont().getResourceID() + "\"");
                        }

                        if (p_buttonComponent.getText() != null)
                        {
                            p_printStream.println("text = \"" + p_buttonComponent.getText().getText() + "\"");
                            p_printStream.println("align = CENTER");
                        }

                        p_printStream.println("sprite_n = " + ((Integer) p_spriteDecodeTable.get(p_buttonComponent.getNormalImage().getResourceID())).intValue());

                        if (p_buttonComponent.getSelectedImage() != null)
                        {
                            p_printStream.println("sprite_l = " + ((Integer) p_spriteDecodeTable.get(p_buttonComponent.getSelectedImage().getResourceID())).intValue());
                        }

                        if (p_buttonComponent.getPressedImage() != null)
                        {
                            p_printStream.println("sprite_p = " + ((Integer) p_spriteDecodeTable.get(p_buttonComponent.getPressedImage().getResourceID())).intValue());
                        }

                        if (p_buttonComponent.getDisabledImage() != null)
                        {
                            p_printStream.println("sprite_d = " + ((Integer) p_spriteDecodeTable.get(p_buttonComponent.getDisabledImage().getResourceID())).intValue());
                        }

                        p_printStream.println("color_n = "+convertColor(p_buttonComponent.getParent().getNormalTextColor()));
                        p_printStream.println("color_l = "+convertColor(p_buttonComponent.getParent().getSelectedTextColor()));
                        p_printStream.println("color_p = "+convertColor(p_buttonComponent.getParent().getPressedTextColor()));
                        p_printStream.println("color_d = "+convertColor(p_buttonComponent.getParent().getDisabledTextColor()));


                        int i_previousFocusable = p_exportedForm.getPreviousFocusableComponent(li);
                        if (i_previousFocusable<0)
                        {
                            i_previousFocusable = p_exportedForm.getLastFocusableComponent();
                            if (i_previousFocusable<0)
                                i_previousFocusable = 0;
                            else
                                i_previousFocusable += BEGIN_ID_INDEX;
                        }
                        else
                            i_previousFocusable += BEGIN_ID_INDEX;

                        int i_nextFocusable = p_exportedForm.getNextFocusableComponent(li);
                        if (i_nextFocusable<0)
                        {
                            i_nextFocusable = p_exportedForm.getFirstFocusableComponent();
                            if (i_nextFocusable<0)
                                i_nextFocusable = 0;
                            else
                                i_nextFocusable += BEGIN_ID_INDEX;
                        }
                        else
                            i_nextFocusable += BEGIN_ID_INDEX;

                        p_printStream.println("up = "+i_previousFocusable);
                        p_printStream.println("down = "+i_nextFocusable);

                        p_printStream.println("}");
                    }
                    ;
                    break;
            }
        }
        p_printStream.println("}");
        p_printStream.close();
    }

    protected String conertIndex(int _index)
    {
        String s_str = Integer.toString(_index);
        if (_index<10) s_str = "0"+s_str;
        return s_str;
    }

    protected String convertColor(Color _color)
    {
        if (_color == null) return "";

        String s_colorR = Integer.toHexString(_color.getRed()).toUpperCase();
        if (s_colorR.length() == 1) s_colorR = "0"+s_colorR;
        String s_colorG = Integer.toHexString(_color.getGreen()).toUpperCase();
        if (s_colorG.length() == 1) s_colorG = "0"+s_colorG;
        String s_colorB = Integer.toHexString(_color.getBlue()).toUpperCase();
        if (s_colorB.length() == 1) s_colorB = "0"+s_colorB;

        return "0x"+s_colorB+s_colorG+s_colorR;
   }

    public String getName()
    {
        return "Export to a DOB GUI form";
    }

    public boolean supportsMultiform()
    {
        return false;
    }
}

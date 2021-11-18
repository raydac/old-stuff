package com.raydac_research.FormEditor.ExportModules;

import com.raydac_research.FormEditor.RrgFormComponents.*;
import com.raydac_research.FormEditor.RrgFormResources.ResourceContainer;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Vector;
import java.awt.*;

public class export_RRG_PATHS extends FileFilter implements AbstractFormExportModule
{
    private JCheckBox p_Flag_Offsets;
    private JCheckBox p_Flag_GenerateGabarite;

    private class PathDataStore
    {
        String s_ID;
        int i_Offset;
        int i_mainX;
        int i_mainY;
    }


    private JCheckBox p_Flag_MainPointCoords;
    private JPanel p_MainPanel;

    public boolean accept(File f)
    {
        if (f == null) return false;
        if (f.isDirectory()) return true;
        String s_fileName = f.getName().toUpperCase();
        if (s_fileName.endsWith(".JAVA")) return true;
        return false;
    }

    public File processFileNameBeforeSaving(File _file)
    {
        if (_file == null) return null;
        if (_file.getAbsolutePath().toUpperCase().endsWith(".JAVA")) return _file;
        _file = new File(_file.getAbsolutePath() + ".java");
        return _file;
    }

    public String getDescription()
    {
        return "RRG PATHS java source";
    }

    public void init(FormCollection _collection)
    {
        p_Flag_MainPointCoords.setSelected(true);
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
        FormContainer p_exportedForm = _list.getFirstForm();

        FileOutputStream p_fos = new FileOutputStream(_file);
        PrintStream p_printStream = new PrintStream(p_fos);
        String s_spaces = "     ";
        p_printStream.println(s_spaces+"// The array contains values for path controllers");
        p_printStream.println(s_spaces+"public static final short [] ash_Paths= new short[] {");

        Vector p_pathData = new Vector();

        int i_offset = 0;

        for (int li = 0; li < p_exportedForm.getSize(); li++)
        {
            AbstractFormComponent p_component = p_exportedForm.getComponentAt(li);
            switch (p_component.getType())
            {
                case AbstractFormComponent.COMPONENT_PATH:
                    {
                        String s_pathID = p_component.getID().trim();
                        String s_id = "PATH_" + s_pathID;

                        p_printStream.print(s_spaces+s_spaces);
                        p_printStream.println("// " + s_id);

                        RrgFormComponent_Path p_pathComponent = (RrgFormComponent_Path) p_component;

                        PathDataStore p_PathDataStore = new PathDataStore();
                        p_PathDataStore.i_mainX = p_pathComponent.p_MainPoint.i_X+p_pathComponent.getX();
                        p_PathDataStore.i_mainY = p_pathComponent.p_MainPoint.i_Y+p_pathComponent.getY();
                        p_PathDataStore.s_ID = s_id;

                        StringBuffer p_StrBuffer = new StringBuffer(256);

                        if (p_Flag_GenerateGabarite.isSelected())
                        {
                            Dimension p_dim = p_pathComponent.getPathDimension();
                            p_StrBuffer.append("(short)");
                            p_StrBuffer.append(p_dim.width);
                            p_StrBuffer.append(',');
                            p_StrBuffer.append("(short)");
                            p_StrBuffer.append(p_dim.height);
                            p_StrBuffer.append(',');
                            i_offset += 2;
                        }

                        p_PathDataStore.i_Offset = i_offset;

                        // Количество точек-1
                        int i_points = p_pathComponent.p_PointVector.size();
                        if (i_points == 0) throw new IOException("Path " + s_pathID + " doesn't have any point");
                        p_StrBuffer.append("(short)");
                        p_StrBuffer.append(p_pathComponent.p_PointVector.size() - 1);
                        i_offset++;
                        p_StrBuffer.append(',');

                        // Тип пути
                        switch (p_pathComponent.i_PathType)
                        {
                            case RrgFormComponent_Path.PATH_NORMAL:
                                p_StrBuffer.append("(short)");
                                p_StrBuffer.append('0');
                                break;
                            case RrgFormComponent_Path.PATH_PENDULUM:
                                p_StrBuffer.append("(short)");
                                p_StrBuffer.append('1');
                                break;
                            case RrgFormComponent_Path.PATH_CYCLIC:
                                p_StrBuffer.append("(short)");
                                p_StrBuffer.append('2');
                                break;
                        }
                        p_StrBuffer.append(',');
                        i_offset++;

                        for (int lp = 0; lp < p_pathComponent.p_PointVector.size(); lp++)
                        {
                            PathPoint p_PathPoint = (PathPoint) p_pathComponent.p_PointVector.elementAt(lp);

                            p_StrBuffer.append("(short)");
                            p_StrBuffer.append(p_PathPoint.i_X);
                            p_StrBuffer.append(',');
                            p_StrBuffer.append("(short)");
                            p_StrBuffer.append(p_PathPoint.i_Y);
                            p_StrBuffer.append(',');
                            p_StrBuffer.append("(short)");
                            p_StrBuffer.append(p_PathPoint.i_Steps);
                            p_StrBuffer.append(',');
                            i_offset += 3;
                        }

                        p_pathData.add(p_PathDataStore);
                        p_printStream.print(s_spaces+s_spaces);
                        p_printStream.println(p_StrBuffer.toString());
                    }
                    ;
                    break;
            }
        }
        p_printStream.println(s_spaces+"};");
        p_printStream.println();

        if (p_Flag_Offsets.isSelected())
        {
            p_printStream.println(s_spaces+"// PATH offsets");

            for (int li = 0; li < p_pathData.size(); li++)
            {
                PathDataStore p_store = (PathDataStore) p_pathData.elementAt(li);

                p_printStream.print(s_spaces);
                p_printStream.print("private static final int ");
                p_printStream.println(p_store.s_ID + " = " + p_store.i_Offset + ";");
            }
            p_printStream.println();
        }
        p_printStream.println();

        if (p_Flag_MainPointCoords.isSelected())
        {
            p_printStream.println(s_spaces+"// Main point coords");

            for (int li = 0; li < p_pathData.size(); li++)
            {
                PathDataStore p_store = (PathDataStore) p_pathData.elementAt(li);

                p_printStream.print(s_spaces);
                p_printStream.print("private static final int ");
                p_printStream.println(p_store.s_ID + "_MP_X = " + p_store.i_mainX + ";");
                p_printStream.print(s_spaces);
                p_printStream.print("private static final int ");
                p_printStream.println(p_store.s_ID + "_MP_Y = " + p_store.i_mainY + ";");
            }

            p_printStream.println();

        }

        p_printStream.println();

        p_printStream.flush();
        p_printStream.close();
    }

    public String getName()
    {
        return "Export paths as a java source file";
    }

    public boolean supportsMultiform()
    {
        return false;
    }
}

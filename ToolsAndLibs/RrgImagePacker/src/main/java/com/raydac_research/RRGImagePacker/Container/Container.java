package com.raydac_research.RRGImagePacker.Container;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.*;
import java.io.*;
import java.awt.*;

public class Container implements TreeModel
{
    protected ImageContainer p_imageContainer;
    protected PaletteContainer p_paletteContainer;
    protected TreeModelListener p_treeModelListener;

    protected String s_name;
    protected String s_comments;
    protected Dimension p_base;
    private JPanel p_PropPanel;
    private JTextArea p_ComentsArea;
    private JTextField p_TextBaseHeight;
    private JTextField p_TextBaseWidth;
    private JTextField p_TextProjectName;

    public Dimension getBase()
    {
        return p_base;
    }

    public void fillPanelData()
    {
        p_TextProjectName.setText(s_name);
        p_TextBaseWidth.setText(""+p_base.width);
        p_TextBaseHeight.setText(""+p_base.height);
        p_ComentsArea.setText(s_comments);
    }

    public String isDataOK()
    {
        String s_bw = p_TextBaseWidth.getText().trim();
        String s_bh = p_TextBaseHeight.getText().trim();

        String s_projName = p_TextProjectName.getText().trim();
        if (s_projName.length()==0) return "You must have non empty the project name field";

        int i_bw = 0;
        int i_bh = 0;

        try
        {
            i_bw = Integer.parseInt(s_bw);
            i_bh = Integer.parseInt(s_bh);
        }
        catch (NumberFormatException e)
        {
            return  "You have wrong value in a base size field";
        }

        if (i_bw<=0) return "You must have the base width more than zero";
        if (i_bh<=0) return "You must have the base height more than zero";

        return null;
    }

    public void fillDataFromPanel()
    {
        if (isDataOK()!=null) return;

        String s_bw = p_TextBaseWidth.getText().trim();
        String s_bh = p_TextBaseHeight.getText().trim();

        int i_bw = 0;
        int i_bh = 0;

        i_bw = Integer.parseInt(s_bw);
        i_bh = Integer.parseInt(s_bh);

        p_base.setSize(i_bw,i_bh);
        s_comments = p_ComentsArea.getText().trim();
        s_name = p_TextProjectName.getText().trim();
    }

    public JPanel getPanel()
    {
        fillPanelData();
        return p_PropPanel;
    }

    public void setBase(int _width,int _height)
    {
        p_base.width = _width;
        p_base.height = _height;
    }

    public String getComments()
    {
        return s_comments;
    }

    public void setComments(String _comments)
    {
        s_comments = _comments;
    }

    public String toString()
    {
        return s_name;
    }

    public void saveToFile(File _file) throws IOException
    {
        FileOutputStream p_fos = new FileOutputStream(_file);
        DataOutputStream p_dos = new DataOutputStream(p_fos);

        p_dos.writeInt(0xDDFF0CAA);

        // Базовое разрешение
        p_dos.writeInt(p_base.width);
        p_dos.writeInt(p_base.height);

        // Комментарии
        p_dos.writeUTF(s_comments);

        // Пул палитр
        p_paletteContainer.writeDataToStream(p_dos);
        // Картинки
        p_imageContainer.writeDataToStream(p_dos);

        p_dos.flush();
        p_dos.close();
    }

    public void readFromFile(File _file) throws IOException
    {
        FileInputStream p_fis = new FileInputStream(_file);
        DataInputStream p_dis = new DataInputStream(p_fis);

        if (p_dis.readInt()!=0xDDFF0CAA) throw new IOException("Bad file signature");

        // Базовое разрешение
        p_base.width = p_dis.readInt();
        p_base.height = p_dis.readInt();

        // Комментарии
        s_comments = p_dis.readUTF();

        // Пул палитр
        p_paletteContainer.loadDataFromStream(p_dis);
        // Картинки
        p_imageContainer.loadDataFromStream(p_paletteContainer,p_dis);

        if (p_treeModelListener!=null)
        {
            p_treeModelListener.treeStructureChanged(new TreeModelEvent(this,new Object[]{this}));
        }

        p_dis.close();
    }

    public TreePath getTreePath(Object _obj)
    {
        if (_obj == null) return new TreePath(new Object[]{this});
        if (_obj instanceof ImageCnt)
        {
            return new TreePath(new Object[]{this,p_imageContainer,_obj});
        }
        else
        if (_obj instanceof PaletteCnt)
        {
            return new TreePath(new Object[]{this,p_paletteContainer,_obj});
        }
        else
        if (_obj instanceof ImageContainer)
        {
            return new TreePath(new Object[]{this,_obj});
        }
        else
        if (_obj instanceof PaletteContainer)
        {
            return new TreePath(new Object[]{this,_obj});
        }

        return new TreePath(new Object[]{this});
    }

    public ImageContainer getImageContainer()
    {
        return p_imageContainer;
    }

    public PaletteContainer getPaletteContainer()
    {
        return p_paletteContainer;
    }

    public ImageCnt addImage()
    {
        return p_imageContainer.addImage();
    }

    public PaletteCnt addPalette()
    {
        return p_paletteContainer.addPalette();
    }

    public Container(String _name)
    {
        s_name = _name;
        p_imageContainer = new ImageContainer("Images");
        p_paletteContainer = new PaletteContainer("Palettes");

        p_base = new Dimension(320,320);

        s_comments = "";

        p_treeModelListener = null;
    }

    public Object getRoot()
    {
        return this;
    }

    public int getChildCount(Object parent)
    {
        if (this.equals(parent))
        {
            return 2;
        }
        else
        if (p_imageContainer.equals(parent))
        {
            return p_imageContainer.getSize();
        }
        else
        if (p_paletteContainer.equals(parent))
        {
            return p_paletteContainer.getSize();
        }
        return 0;
    }

    public boolean isLeaf(Object node)
    {
        if (this.equals(node))
        {
            return false;
        }
        else
        if (p_imageContainer.equals(node))
        {
            return false;
        }
        else
        if (p_paletteContainer.equals(node))
        {
            return false;
        }
        return true;
    }

    public void addTreeModelListener(TreeModelListener l)
    {
        p_treeModelListener = l;
    }

    public void removeTreeModelListener(TreeModelListener l)
    {
        p_treeModelListener = null;
    }

    public Object getChild(Object parent, int index)
    {
        if (this.equals(parent))
        {
            switch(index)
            {
                case 0: return p_imageContainer;
                case 1: return p_paletteContainer;
            }
            return null;
        }
        else
        if (p_imageContainer.equals(parent))
        {
            return p_imageContainer.getImageAt(index);
        }
        else
        if (p_paletteContainer.equals(parent))
        {
            return p_paletteContainer.getPaletteAt(index);
        }
        return null;
    }

    public int getIndexOfChild(Object parent, Object child)
    {
        if (this.equals(parent))
        {
            if (p_imageContainer.equals(child))
            {
                return 0;
            }
            else
            if (p_paletteContainer.equals(child))
            {
                return 1;
            }
        }
        else
        if (p_imageContainer.equals(parent))
        {
            int i_indx = p_imageContainer.getIndex(child);
            return i_indx;
        }
        else
        if (p_paletteContainer.equals(parent))
        {
            return p_paletteContainer.getIndex(child);
        }
        return -1;
    }

    public void valueForPathChanged(TreePath path, Object newValue)
    {
    }
}

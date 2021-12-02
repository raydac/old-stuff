package com.raydac_research.RRGImagePacker.Utils;

import com.raydac_research.RRGImagePacker.Container.Container;
import com.raydac_research.RRGImagePacker.Container.ImageCnt;
import com.raydac_research.RRGImagePacker.Container.PaletteCnt;
import com.raydac_research.RRGImagePacker.tga.TGAImageReader;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;

public class SelectNewResourceDialog extends JDialog implements ActionListener
{
    private JPanel p_SelectResourcePanel;
    private JButton p_ButtonOk;
    private JButton p_ButtonCancel;
    private JRadioButton p_Radio_LoadAllResourcesFromDirectory;
    private JRadioButton p_Radio_Radio_PaletteFromFile;
    private JRadioButton p_Radio_EmptyImage;
    private JRadioButton p_Radio_ImageFromFile;

    private ButtonGroup p_ButtonGroup;

    private static final int STATE_IMAGEFROMFILE = 0;
    private static final int STATE_EMPTYIMAGE = 1;
    private static final int STATE_PALETTEFROMFILE = 2;
    private static final int STATE_LOADALLRESOURCES = 3;

    private int i_selectedState;

    private File p_lastFile;

    protected static class imageFilter extends FileFilter
    {
        private static final String[] EXTENSIONS = new String[]{".jpg", ".gif", ".tga"};

        public boolean accept(File f)
        {
            if (f == null) return false;
            if (f.isDirectory()) return true;
            String s_filename = f.getName().toLowerCase();

            int i_len = EXTENSIONS.length;

            for (int li = 0; li < i_len; li++)
            {
                if (s_filename.endsWith(EXTENSIONS[li])) return true;
            }
            return false;
        }

        public String getDescription()
        {
            return "Supported images formats (JPG,GIF,TGA)";
        }
    }

    protected static class paletteFilter extends FileFilter
    {
        private static final String[] EXTENSIONS = new String[]{".pal", ".act"};

        public boolean accept(File f)
        {
            if (f == null) return false;
            if (f.isDirectory()) return true;
            String s_filename = f.getName().toLowerCase();

            int i_len = EXTENSIONS.length;

            for (int li = 0; li < i_len; li++)
            {
                if (s_filename.endsWith(EXTENSIONS[li])) return true;
            }
            return false;
        }

        public String getDescription()
        {
            return "Supported palette formats (PAL,ACT)";
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource().equals(p_ButtonOk))
        {
            i_selectedState = getSelectedState();
            hide();
        }
        else if (e.getSource().equals(p_ButtonCancel))
        {
            i_selectedState = -1;
            hide();
        }
    }

    private paletteFilter p_paletteFilter;
    private imageFilter p_imageFilter;

    public SelectNewResourceDialog(JFrame _parent)
    {
        super(_parent,"Select resource type",true);
        p_ButtonGroup = new ButtonGroup();
        p_ButtonGroup.add(p_Radio_ImageFromFile);
        p_ButtonGroup.add(p_Radio_EmptyImage);
        p_ButtonGroup.add(p_Radio_Radio_PaletteFromFile);
        p_ButtonGroup.add(p_Radio_LoadAllResourcesFromDirectory);

        p_Radio_ImageFromFile.setSelected(true);

        p_ButtonOk.addActionListener(this);
        p_ButtonCancel.addActionListener(this);

        p_paletteFilter = new paletteFilter();
        p_imageFilter = new imageFilter();

        setContentPane(p_SelectResourcePanel);
        pack();
        setResizable(false);
        Utilities.toScreenCenter(this);
    }

    protected int getSelectedState()
    {
        if (p_Radio_EmptyImage.isSelected()) return STATE_EMPTYIMAGE;
        if (p_Radio_ImageFromFile.isSelected()) return STATE_IMAGEFROMFILE;
        if (p_Radio_LoadAllResourcesFromDirectory.isSelected()) return STATE_LOADALLRESOURCES;
        if (p_Radio_Radio_PaletteFromFile.isSelected()) return STATE_PALETTEFROMFILE;
        return -1;
    }

    private String processName(Container _container,String _name,boolean _images)
    {
        _name = _name.trim();

        if (_images)
        {
            if (_container.getImageContainer().getImageForName(_name)==null) return _name;

            String s_fName = _name;

            for (int li = 0; li < 1000; li++)
            {
                if (_container.getImageContainer().getImageForName(s_fName)==null) return s_fName;
                s_fName = _name + "_" + li;
            }
            return null;
        }
        else
        {
            if (_container.getPaletteContainer().getPaletteForName(_name)==null) return _name;

            String s_fName = _name;

            for (int li = 0; li < 1000; li++)
            {
                if (_container.getPaletteContainer().getPaletteForName(_name)==null) return s_fName;
                s_fName = _name + "_" + li;
            }
            return null;
        }
    }

    private ImageCnt loadImageResourceFromFile(Container _container,File _file)
    {
        BufferedImage p_newImage = null;
        try
        {
            p_newImage = convertFileToImage(_file);
        }
        catch (IOException e)
        {
            boolean lg_wasisible = false;

            if (Utilities.WAIT_DIALOG.isVisible())
            {
                Utilities.WAIT_DIALOG.hide();
                lg_wasisible = true;
            }

            Utilities.showErrorDialog(this,"Error of opening",e.getMessage());

            if (lg_wasisible)
            {
                Utilities.WAIT_DIALOG.show();
            }

            return null;
        }

        ImageCnt p_newImgCnt = _container.addImage();

        String s_name = _file.getName();
        s_name = Utilities.getFileNameWithoutExt(s_name).trim();

        s_name = processName(_container,s_name,true);
        if (s_name == null) s_name = "<<<NAME>>>";

        p_newImgCnt.setName(s_name);
        p_newImgCnt.setImage(p_newImage);
        p_newImgCnt.setSource(_file.getAbsolutePath());

        return p_newImgCnt;
    }

    private PaletteCnt loadPaletteResourceFromFile(Container _container,File _file)
    {
        int [] ai_newPalette = null;
        try
        {
            String s_name = _file.getName().trim().toLowerCase();
            if (s_name.endsWith(".pal"))
            {
                ai_newPalette = PaletteCnt.loadPALPalette(_file);
            }
            else
            if (s_name.endsWith(".act"))
            {
                ai_newPalette = PaletteCnt.loadACTPAlette(_file);
            }
        }
        catch (IOException e)
        {

            boolean lg_wasisible = false;

            if (Utilities.WAIT_DIALOG.isVisible())
            {
                Utilities.WAIT_DIALOG.hide();
                lg_wasisible = true;
            }

            Utilities.showErrorDialog(this,"Error of opening",e.getMessage());

            if (lg_wasisible)
            {
                Utilities.WAIT_DIALOG.show();
            }

            return null;
        }

        PaletteCnt p_newPalCnt = _container.addPalette();

        String s_name = _file.getName();
        s_name = Utilities.getFileNameWithoutExt(s_name).trim();

        s_name = processName(_container,s_name,false);
        if (s_name == null) s_name = "<<<NAME>>>";

        p_newPalCnt.setName(s_name);
        p_newPalCnt.loadFromArray(ai_newPalette);
        p_newPalCnt.setFileName(_file.getAbsolutePath());

        return p_newPalCnt;
    }

    public Object showDialog(Container _container)
    {
        i_selectedState = -1;
        show();
        if (i_selectedState < 0) return null;

        switch (i_selectedState)
        {
            case STATE_EMPTYIMAGE:
                {
                    String s_name = "New_empty_image";
                    s_name = processName(_container,s_name,true);
                    if (s_name == null) return null;
                    ImageCnt p_newImage = _container.addImage();
                    p_newImage.setName(s_name);
                    return p_newImage;
                }
            case STATE_IMAGEFROMFILE:
                {
                    File p_file = Utilities.selectFileForOpen(this,p_imageFilter,"Select an image",p_lastFile);
                    if (p_file == null) return null;
                    p_lastFile = p_file;

                    loadImageResourceFromFile(_container,p_file);
                }
                ;
                break;
            case STATE_LOADALLRESOURCES:
                {
                    File p_file = Utilities.selectDirectoryForOpen(this,null,"Select a directory",p_lastFile);
                    if (p_file == null) return null;
                    p_lastFile = p_file;

                    Utilities.WAIT_DIALOG.show();

                    File [] ap_files = p_file.listFiles();

                    for(int li=0;li<ap_files.length;li++)
                    {
                        if (ap_files[li].isDirectory()) continue;

                        if (p_imageFilter.accept(ap_files[li]))
                        {
                            loadImageResourceFromFile(_container,ap_files[li]);
                        }
                        else
                            if (p_paletteFilter.accept(ap_files[li]))
                            {
                                loadPaletteResourceFromFile(_container,ap_files[li]);
                            }
                    }

                    Utilities.WAIT_DIALOG.hide();
                }
                ;
                break;
            case STATE_PALETTEFROMFILE:
                {
                    File p_file = Utilities.selectFileForOpen(this,p_imageFilter,"Select a palette",p_lastFile);
                    if (p_file == null) return null;
                    p_lastFile = p_file;

                    return loadPaletteResourceFromFile(_container,p_file);
                }
        }

        return null;
    }

    public static BufferedImage convertFileToImage(File _imageFile) throws IOException
    {
        byte[] ab_byte = new byte[(int) _imageFile.length()];
        FileInputStream p_fis = new FileInputStream(_imageFile);
        p_fis.read(ab_byte);
        p_fis.close();
        return convertFileToImage(ab_byte);
    }

    private static MediaTracker pMediaTracker = new MediaTracker(new Button());
    private static TGAImageReader pTgaReader = new TGAImageReader();

    public static BufferedImage convertFileToImage(byte[] _imageFile) throws IOException
    {
        // May be  it is GIF or JPEG?
        BufferedImage pResultImage = null;
        Image pImage = Toolkit.getDefaultToolkit().createImage(_imageFile);

        pMediaTracker.addImage(pImage, 1);

        try
        {
            pMediaTracker.waitForAll();
        }
        catch (InterruptedException e)
        {
            return null;
        }

        if (pMediaTracker.isErrorAny())
        {
            if ((pMediaTracker.statusID(1, true) & MediaTracker.ERRORED) != 0)
            {
                pMediaTracker.removeImage(pImage);
                pImage = null;
            }
        }
        else
        {
            pMediaTracker.removeImage(pImage);
        }


        if (pImage != null)
        {
            pResultImage = new BufferedImage(pImage.getWidth(null), pImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            pResultImage.getGraphics().drawImage(pImage, 0, 0, null);
            pImage = null;
        }
        else
        {
            // May be that file is a TGA file?
            try
            {
                ByteArrayInputStream pFis = new ByteArrayInputStream(_imageFile);
                pResultImage = pTgaReader.read(new MemoryCacheImageInputStream(pFis));
            }
            catch (OutOfMemoryError ex)
            {
                pResultImage = null;
            }
            catch (Exception ex)
            {
                pResultImage = null;
            }
        }

        if (pResultImage == null)
        {
            throw new IOException("Unsupported image format, you must use GIF,JPEG or TGA format");
        }

        return pResultImage;
    }

}

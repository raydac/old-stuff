package ru.coldcore.png;

import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;



public class LogoConverter extends JTextArea implements DropTargetListener,MouseListener
{
    public void dragEnter(DropTargetDragEvent arg0)
    {
        dragOver(arg0);
    }

    public void dragExit(DropTargetEvent arg0)
    {
    }

    public void dragOver(DropTargetDragEvent _evt)
    {
        if(!_evt.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) _evt.rejectDrag();
    }

    public void mouseClicked(MouseEvent e)
    {
        aboutform p_about = new aboutform(p_Parent);
        p_about.setVisible(true);
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    public void mousePressed(MouseEvent e)
    {
    }

    public void mouseReleased(MouseEvent e)
    {
    }

    public static final int convertFile(File _file,Component _component) throws IOException
    {
        File p_file = _file;
        int i_newsize = -1;
        BufferedImage p_image = null;
        
        try
        {
            p_image = Utilities.convertFileToImage(p_file);
        }
        catch(Throwable _thr)
        {
            if (_component!=null)
            {
                Utilities.showErrorDialog(_component,"Can't load image",_thr.getMessage());
            }
            else
            {
                throw new IOException("Can't load image "+p_file.getAbsolutePath());
            }
        }
        
        String s_fileParent = p_file.getParent();
        if (s_fileParent==null) s_fileParent = ".";
        String s_fileName = p_file.getName();
        s_fileName = Utilities.getFileNameWithoutExt(s_fileName);
        
        s_fileName += ".png";
        
        String s_newPath = s_fileParent+File.separator+s_fileName;
        
        // обрабатываем картинку
        colorCorrectionForSamsungLGphones(p_image);
        
        // делаем PNG версию картинки
        ByteArrayOutputStream p_outStream = new ByteArrayOutputStream(32000);
        PNGEncoder p_pngEncoder = new PNGEncoder(p_image,p_outStream,null,false);
        p_pngEncoder.setAlpha(true);
        p_pngEncoder.setCompressionLevel(9);
        
        p_pngEncoder.encodeImage();
        
        byte [] ab_imageData = p_outStream.toByteArray();
        p_pngEncoder = null;
        p_outStream = null;
        p_image = null;
        
        i_newsize = ab_imageData.length;
        
        FileOutputStream p_outImageStream = new FileOutputStream(s_newPath);
        
        try
        {
            p_outImageStream.write(ab_imageData);
            p_outImageStream.flush();
        }
        catch(IOException _ex)
        {
            if (_component!=null) 
                Utilities.showErrorDialog(_component,"Can't save image","Can't save new image "+s_newPath);
            else
                throw new IOException("Can't save new image "+s_newPath);
        }
        finally
        {
            if (p_outImageStream!=null)
            {
                try
                {
                    p_outImageStream.close();
                }
                catch(Throwable _ther)
                {
                }
                p_outImageStream = null;
            }
        }
        
        p_image = null;
        return i_newsize;
    }
    
    public void drop(DropTargetDropEvent _arg)
    {
        _arg.acceptDrop(DnDConstants.ACTION_LINK);
        Transferable p_trans = _arg.getTransferable();
        
        StringBuffer p_terminal = new StringBuffer();
        
        try
        {
            if (!p_trans.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
            {
            }
            else
            {
                List p_list = (List) p_trans.getTransferData(DataFlavor.javaFileListFlavor);
                for(int li=0;li<p_list.size();li++)
                {
                    File p_file = (File)p_list.get(li);
                    try
                    {
                        int i_oldLen = (int)p_file.length();
                        if (i_oldLen==0) throw new IOException("File zero length");
                        int i_newLen = convertFile(p_file,this);
                        
                        p_terminal.append(p_file.getName()+((i_oldLen>i_newLen)?" saved "+(i_oldLen-i_newLen)+" bytes":"")+" new size "+((i_newLen*100)/i_oldLen)+'%');
                        p_terminal.append('\n');
                    }
                    catch(Throwable _thr)
                    {
                        p_terminal.append("Can't pack "+p_file.getName()+(_thr.getMessage()!=null ? "["+_thr.getMessage()+"]\n":""));
                    }
                }
            }
        }
        catch(Throwable _thr)
        {
            System.out.println("!!!Error DnD operation!!!");
            p_terminal.append("----System DnD error----");
            _thr.printStackTrace();
        }
        _arg.dropComplete(true);
        setText("\n\n"+p_terminal.toString());
    }

    public void dropActionChanged(DropTargetDragEvent arg0)
    {
    }

    public static final void colorCorrectionForSamsungLGphones(BufferedImage _img)
    {
        // Обрабатываем изображение что бы не было проблем на Samsung и LG где белый цвет в диапазоне от 251,251,251-255,255,255 прозрачен
        int[] ai_ImageBuffer = ((DataBufferInt) _img.getRaster().getDataBuffer()).getData();

        int i_len = ai_ImageBuffer.length - 1;
        while (i_len >= 0)
        {
            int i_argb = ai_ImageBuffer[i_len];

            int i_a = i_argb >>> 24;
            int i_r = (i_argb >>> 16) & 0xFF;
            int i_g = (i_argb >>> 8) & 0xFF;
            int i_b = i_argb & 0xFF;

            if (i_a < 0x80)
            {
                i_argb = 0xFFFFFF;
            }
            else
            {
                if (i_r > 250) i_r = 250;
                if (i_g > 250) i_g = 250;
                if (i_b > 250) i_b = 250;

                i_argb = 0xFF000000 | (i_r << 16) | (i_g << 8) | i_b;
            }

            ai_ImageBuffer[i_len] = i_argb;

            i_len--;
        }
    }

    public main p_Parent;
    
    public LogoConverter(main _owner)
    {
        super();
        p_Parent = _owner;
        DropTarget p_dropTarget = new DropTarget(this,this);
        setEditable(false);
        setForeground(Color.magenta.darker());
        addMouseListener(this);
    }
    
}

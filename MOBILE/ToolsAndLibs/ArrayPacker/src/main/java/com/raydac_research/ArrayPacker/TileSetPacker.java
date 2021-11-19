package com.raydac_research.ArrayPacker;

import com.raydac_research.Png.PNGEncoder;
import com.raydac_research.TGA.TGAImageReader;

import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.*;

public class TileSetPacker
{
    protected static MediaTracker pMediaTracker = new MediaTracker(new Button());
    protected static TGAImageReader pTgaReader = new TGAImageReader();

    public static final byte [] packTileSet(BufferedImage _image, int _cellWidth, int _cellHeight) throws Exception
    {
        int i_imgW = _image.getWidth();
        int i_imgH = _image.getHeight();

        if (_cellWidth<=0 && _cellHeight<=0) throw new IOException("There is not any info about cell width or height");
        _cellWidth = _cellWidth<0 ? _cellHeight : _cellWidth;
        _cellHeight = _cellHeight<0 ? _cellWidth : _cellHeight;

        if (i_imgW % _cellWidth != 0) throw new Exception("ill-defined cell width value");
        if (i_imgH % _cellHeight != 0) throw new Exception("ill-defined cell height value");

        int i_tileWidthNumber = i_imgW / _cellWidth;
        int i_tileHeightNumber = i_imgH / _cellHeight;

        int i_tilesNum = i_tileHeightNumber*i_tileWidthNumber;
        if (i_tilesNum>256) throw new Exception("You have too many tiles (>256)");

        BufferedImage [] ap_tileSet = new BufferedImage[i_tilesNum];

        int i_imageIndex = 0;
        for(int ly=0;ly<i_imgH;ly+=_cellHeight)
        {
            for(int lx=0;lx<i_imgW;lx+=_cellWidth)
            {
                BufferedImage p_buffImage = new BufferedImage(_cellWidth,_cellHeight,BufferedImage.TYPE_INT_ARGB);
                Graphics p_graph = p_buffImage.getGraphics();
                p_graph.drawImage(_image,0-lx,0-ly,null);
                ap_tileSet[i_imageIndex++] = p_buffImage;
                p_graph = null;
                p_buffImage = null;
            }
        }

        // Пакуем тайлы
        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(16000);

        int i_maxFileSize = 0;

        for(int li=0;li<ap_tileSet.length;li++)
        {
            BufferedImage p_img = ap_tileSet[li];
            ByteArrayOutputStream p_outStream = new ByteArrayOutputStream(10000);
            PNGEncoder p_enc = new PNGEncoder(p_img, p_outStream, null, false);
            p_enc.setCompressionLevel(9);
            p_enc.setAlpha(true);
            p_enc.encodeImage();
            byte[] ab_arr = p_outStream.toByteArray();

            int i_len = ab_arr.length;
            int i_arg1 = i_len >>> 8;
            int i_arg2 = i_len & 0xFF;
            p_baos.write(i_arg1);
            p_baos.write(i_arg2);
            p_baos.write(ab_arr);

            if (i_len>i_maxFileSize) i_maxFileSize = i_len;

            //---
            //FileOutputStream p_outStre = new FileOutputStream("pngTile"+li+".png");
            //p_outStre.write(ab_arr);
            //p_outStre.flush();
            //p_outStre.close();
        }

        p_baos.flush();
        p_baos.close();
        byte [] ab_tileSetArray = p_baos.toByteArray();

        p_baos = new ByteArrayOutputStream(ab_tileSetArray.length+10);
        p_baos.write(i_tilesNum-1);
        p_baos.write(i_maxFileSize>>>8);
        p_baos.write(i_maxFileSize&0xFF);
        p_baos.write(ab_tileSetArray);

        p_baos.flush();
        p_baos.close();
        ab_tileSetArray = p_baos.toByteArray();

        return ab_tileSetArray;
    }

    public static BufferedImage convertFileToImage(File _imageFile) throws IOException
    {
        byte[] ab_byte = new byte[(int) _imageFile.length()];
        FileInputStream p_fis = new FileInputStream(_imageFile);
        p_fis.read(ab_byte);
        p_fis.close();
        return convertFileToImage(ab_byte);
    }

    public static void saveBinFile(File _file,byte [] _array) throws IOException
    {
        FileOutputStream p_fos = new FileOutputStream(_file);
        p_fos.write(_array);
        p_fos.flush();
        p_fos.close();
        p_fos = null;
    }

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
            catch (IOException ex)
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

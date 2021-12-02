package com.raydac_research.RRGImagePacker.Container;

import com.raydac_research.RRGImagePacker.Utils.Utilities;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.util.HashSet;

public class ImageCnt
{
    protected String s_name;
    protected String s_source;

    protected int i_colors;

    protected int i_groupID;

    protected Dimension p_Dimension;
    protected Rectangle p_cropRegion;

    protected ImageCnt p_linkedImage;

    protected String s_extImage;
    protected BufferedImage p_image;
    protected PaletteCnt p_usedPalette;

    protected boolean lg_isCropped;
    protected boolean lg_isExcluded;

    protected int i_compress;
    protected int i_optimizingState;
    protected int i_formatType;

    protected boolean lg_Tiling;
    protected Dimension p_tileDim;

    protected int i_showMagnify;

    protected String s_comments;

    public static final int FORMAT_PNG = 0;
    public static final int FORMAT_JPG = 1;
    public static final int FORMAT_TGA = 2;

    public static final int USAGE_RARELY = 0;
    public static final int USAGE_NOTRARELY = 1;
    public static final int USAGE_OFTEN = 2;

    public static final int OPTIMIZATION_SPEED = 0;
    public static final int OPTIMIZATION_SIZE = 1;

    public static final int ROTATE_0 = 0;
    public static final int ROTATE_90 = 1;
    public static final int ROTATE_180 = 2;
    public static final int ROTATE_270 = 3;

    protected boolean lg_flipHorz;
    protected boolean lg_flipVert;
    protected int i_Rotation;
    protected int i_Usage;

    public int getUsage()
    {
        return i_Usage;
    }

    public void setUsage(int _state)
    {
        i_Usage = _state;
    }

    public boolean isFlippedHorizondal()
    {
        return lg_flipHorz;
    }

    public boolean isFlippedVertical()
    {
        return lg_flipVert;
    }

    public void setFlipVertState(boolean _state)
    {
        lg_flipVert = _state;
    }

    public void setFlipHorzState(boolean _state)
    {
        lg_flipHorz = _state;
    }

    public void setRotationState(int _state)
    {
        i_Rotation = _state;
    }

    public int getRotationState()
    {
        return i_Rotation;
    }

    public String getComments()
    {
        return s_comments;
    }

    public void setComments(String _comments)
    {
        s_comments = _comments;
    }

    public boolean isLink()
    {
        return p_linkedImage != null;
    }

    public String getSource()
    {
        if (isLink())
            return p_linkedImage.getSource();
        else
            return s_source;
    }

    public void loadFromStream(ImageContainer _images, PaletteContainer _palettes, DataInputStream _inStream) throws IOException
    {
        // Имя
        s_name = _inStream.readUTF();
        // Исходник
        s_source = _inStream.readUTF();
        if (s_source.length() == 0) s_source = "";

        // Комментарии
        s_comments = _inStream.readUTF();
        // Частота использования
        i_Usage = _inStream.readUnsignedByte();
        // Количество цветов
        i_colors = _inStream.readInt();
        // Размеры
        int i_width = _inStream.readInt();
        int i_height = _inStream.readInt();
        p_Dimension.setSize(i_width, i_height);
        p_image = new BufferedImage(i_width, i_height, BufferedImage.TYPE_INT_ARGB);

        // Кропрегион
        p_cropRegion.x = _inStream.readInt();
        p_cropRegion.y = _inStream.readInt();
        p_cropRegion.width = _inStream.readInt();
        p_cropRegion.height = _inStream.readInt();

        // Tile данные
        lg_Tiling = _inStream.readBoolean();
        p_tileDim.width = _inStream.readInt();
        p_tileDim.height = _inStream.readInt();

        // Ротация
        i_Rotation = _inStream.readShort();
        // Флипы
        lg_flipHorz = _inStream.readBoolean();
        lg_flipVert = _inStream.readBoolean();

        // Линкованная картинка
        String s_linkedImage = _inStream.readUTF();
        if (s_linkedImage.length() == 0)
        {
            p_linkedImage = null;
        }
        else
        {
            p_linkedImage = _images.getImageForName(s_linkedImage);
            if (p_linkedImage == null) throw new IOException("I can't find a linked image " + s_linkedImage);
        }

        // Внешняя картинка
        s_extImage = _inStream.readUTF();

        if (s_extImage.length() == 0) s_extImage = null;

        // Используемая палитра
        String s_usedPalette = _inStream.readUTF();
        if (s_usedPalette.length() == 0)
        {
            p_usedPalette = null;
        }
        else
        {
            p_usedPalette = _palettes.getPaletteForName(s_usedPalette);
            if (p_usedPalette == null) throw new IOException("I can't find an used image " + s_usedPalette);
        }

        // Флаг кропа
        lg_isCropped = _inStream.readBoolean();

        // Исключено
        lg_isExcluded = _inStream.readBoolean();

        // Компрессия
        i_compress = _inStream.readInt();

        // ТИп оптимизации
        i_optimizingState = _inStream.readInt();

        // Тип формата
        i_formatType = _inStream.readInt();

        // Увеличение при отображении
        i_showMagnify = _inStream.readInt();

        // Группа
        i_groupID = _inStream.readInt();

        // Данные изображения
        int[] ai_ImageBuffer = ((DataBufferInt) p_image.getRaster().getDataBuffer()).getData();

        //Упакованный размер
        int i_length = _inStream.readInt();

        byte[] ab_packedData = new byte[i_length];
        _inStream.read(ab_packedData);

        byte[] ab_decompressedData = new byte[0];
        try
        {
            ab_decompressedData = Utilities.decompressArray(ab_packedData, i_width * i_height * 4);
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage());
        }

        for (int li = 0; li < ai_ImageBuffer.length; li++)
        {
            int i_indx = li * 4;
            int i_b3 = ab_decompressedData[i_indx++] & 0xFF;
            int i_b2 = ab_decompressedData[i_indx++] & 0xFF;
            int i_b1 = ab_decompressedData[i_indx++] & 0xFF;
            int i_b0 = ab_decompressedData[i_indx] & 0xFF;

            int i_val = (i_b3 << 24) | (i_b2 << 16) | (i_b1 << 8) | i_b0;

            ai_ImageBuffer[li] = i_val;
        }
        ab_decompressedData = null;
    }

    public void saveToStream(DataOutputStream _outStream) throws IOException
    {
        // Имя
        _outStream.writeUTF(s_name);
        // Исходник
        if (s_source == null)
        _outStream.writeUTF("");
        else
        _outStream.writeUTF(s_source);

        // Комментарии
        _outStream.writeUTF(s_comments);
        // Частота использования
        _outStream.writeByte(i_Usage);
        // Количество цветов
        _outStream.writeInt(i_colors);
        // Размеры
        _outStream.writeInt(p_Dimension.width);
        _outStream.writeInt(p_Dimension.height);
        // Кропрегион
        _outStream.writeInt(p_cropRegion.x);
        _outStream.writeInt(p_cropRegion.y);
        _outStream.writeInt(p_cropRegion.width);
        _outStream.writeInt(p_cropRegion.height);
        // Tile данные
        _outStream.writeBoolean(lg_Tiling);
        _outStream.writeInt(p_tileDim.width);
        _outStream.writeInt(p_tileDim.height);

        // Ротация
        _outStream.writeShort(i_Rotation);
        // Флипы
        _outStream.writeBoolean(lg_flipHorz);
        _outStream.writeBoolean(lg_flipVert);

        // Линкованная картинка
        if (p_linkedImage != null)
        {
            _outStream.writeUTF(p_linkedImage.getName());
        }
        else
        {
            _outStream.writeUTF("");
        }

        // Внешняя картинка
        if (s_extImage == null)
            _outStream.writeUTF("");
        else
            _outStream.writeUTF(s_extImage);

        // Используемая палитра
        if (p_usedPalette != null)
        {
            _outStream.writeUTF(p_usedPalette.getName());
        }
        else
        {
            _outStream.writeUTF("");
        }

        // Флаг кропа
        _outStream.writeBoolean(lg_isCropped);

        // Исключено
        _outStream.writeBoolean(lg_isExcluded);

        // Компрессия
        _outStream.writeInt(i_compress);

        // ТИп оптимизации
        _outStream.writeInt(i_optimizingState);

        // Тип формата
        _outStream.writeInt(i_formatType);

        // Увеличение при отображении
        _outStream.writeInt(i_showMagnify);

        // Группа
        _outStream.writeInt(i_groupID);

        // Данные изображения
        if (p_image == null) throw new IOException("You have not defined an image for " + getName());

        int[] ai_ImageBuffer = ((DataBufferInt) p_image.getRaster().getDataBuffer()).getData();
        ByteArrayOutputStream p_outStr = new ByteArrayOutputStream(ai_ImageBuffer.length * 4);
        for (int li = 0; li < ai_ImageBuffer.length; li++)
        {
            int i_val = ai_ImageBuffer[li];

            p_outStr.write(i_val >>> 24);
            p_outStr.write(i_val >>> 16);
            p_outStr.write(i_val >>> 8);
            p_outStr.write(i_val);
        }
        p_outStr.close();

        byte[] ab_new = Utilities.compressArray(p_outStr.toByteArray());

        // Упакованный размер
        _outStream.writeInt(ab_new.length);

        _outStream.write(ab_new);
    }

    public int getColors()
    {
        if (isLink())
        {
            return p_linkedImage.getColors();
        }
        else
            return i_colors;
    }

    public void setSource(String _source)
    {
        s_source = _source;
    }

    public String getExternalImageLink()
    {
        if (s_extImage == null) return "";
        return s_extImage;
    }

    public void setExternalImageLink(String _link)
    {
        if (_link != null)
        {
            _link = _link.trim();
            if (_link.length() == 0) _link = null;
        }
        s_extImage = _link;
    }

    public boolean isExternalImageLink()
    {
        return (s_extImage != null) && (s_extImage.length() != 0);
    }

    public BufferedImage getImage()
    {
        return makeImageForModifiers();
    }

    public int getOptimization()
    {
        return i_optimizingState;
    }

    public void setOptimization(int _state)
    {
        i_optimizingState = _state;
    }

    public ImageCnt getLinkedImage()
    {
        return p_linkedImage;
    }

    public void setImage(BufferedImage _image)
    {
        p_image = _image;
        i_colors = 0;
        if (p_image != null)
        {
            p_Dimension.setSize(p_image.getWidth(), p_image.getHeight());
            lg_isCropped = false;
            p_cropRegion.setBounds(0, 0, 0, 0);
            s_extImage = null;
            p_linkedImage = null;

            HashSet p_colorTable = new HashSet(256);

            int[] ai_ImageBuffer = ((DataBufferInt) _image.getRaster().getDataBuffer()).getData();

            int i_len = ai_ImageBuffer.length;
            for(int li=0;li<i_len;li++)
            {
                p_colorTable.add(new Integer(ai_ImageBuffer[li]));
                if (p_colorTable.size()>256) break;
            }

            i_colors = p_colorTable.size();
            p_colorTable.clear();
            p_colorTable = null;
        }
    }

    public void setLinkedImage(ImageCnt _image)
    {
        p_linkedImage = _image;
    }

    public Dimension getDimension()
    {
        if (isLink())
        {
            return getLinkedImage().getDimension();
        }
        else
        {
            return p_Dimension;
        }
    }

    public Dimension getTileDim()
    {
        return p_tileDim;
    }

    public Rectangle getCropRegion()
    {
        return p_cropRegion;
    }

    public int getCompress()
    {
        return i_compress;
    }

    public void setCompress(int _compress)
    {
        i_compress = _compress;
    }

    public int getFormat()
    {
        return i_formatType;
    }

    public void setFormat(int _format)
    {
        i_formatType = _format;
    }

    public PaletteCnt getUsedPalette()
    {
        return p_usedPalette;
    }

    public void setUsedPalette(PaletteCnt _palette)
    {
        p_usedPalette = _palette;
    }

    public String getName()
    {
        return s_name;
    }

    public void setName(String _name)
    {
        s_name = _name;
    }

    public void setCropFlag(boolean _state)
    {
        lg_isCropped = _state;
    }

    public void setTileFlag(boolean _state)
    {
        lg_Tiling = _state;
    }

    public void setExcludedFlag(boolean _state)
    {
        lg_isExcluded = _state;
    }

    public void setTileDim(Dimension _dim)
    {
        p_tileDim.setSize(_dim.width, _dim.height);
    }

    public void setCropRegion(Rectangle _rect)
    {
        p_cropRegion.setBounds(_rect.x, _rect.y, _rect.width, _rect.height);
    }

    public boolean isCropped()
    {
        return lg_isCropped;
    }

    public boolean isTile()
    {
        return lg_Tiling;
    }

    public boolean isExcluded()
    {
        return lg_isExcluded;
    }

    public String toString()
    {
        Dimension p_dim = getDimension();
        int i_colors = getColors();
        return s_name + "(" + p_dim.width + "x" + p_dim.height + "x"+(i_colors>256 ? "TruColor":""+i_colors)+")";
    }

    public int getMagnify()
    {
        return i_showMagnify;
    }

    public int getGroupID()
    {
        return i_groupID;
    }

    public void setGroupID(int _id)
    {
        i_groupID = _id;
    }

    public void setMagnify(int _value)
    {
        i_showMagnify = _value;
    }

    public ImageCnt()
    {
        i_Rotation = ROTATE_0;
        lg_flipHorz = false;
        lg_flipVert = false;

        i_Usage = USAGE_OFTEN;

        p_image = null;
        p_linkedImage = null;
        p_Dimension = new Dimension(0, 0);
        p_tileDim = new Dimension(1, 1);
        lg_Tiling = false;
        s_name = null;
        lg_isCropped = false;
        lg_isExcluded = false;
        p_cropRegion = new Rectangle(0, 0, 0, 0);

        i_optimizingState = OPTIMIZATION_SIZE;
        i_formatType = FORMAT_PNG;
        i_compress = 9;
        i_colors = 0;
        i_groupID = -1;
        i_showMagnify = 1;
        p_usedPalette = null;

        s_comments = "";
    }

    private BufferedImage makeImageForModifiers()
    {
        BufferedImage p_srcImage = null;

        if (isLink())
        {
            p_srcImage = p_linkedImage.getImage();
        }
        else
        {
            if (p_image == null) return null;

            p_srcImage = p_image;
        }

        BufferedImage p_modifiedImage = new BufferedImage(p_srcImage.getWidth(), p_srcImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        int[] ai_ImageBuffer = ((DataBufferInt) p_modifiedImage.getRaster().getDataBuffer()).getData();
        for (int li = 0; li < ai_ImageBuffer.length; li++) ai_ImageBuffer[li] = 0;
        ai_ImageBuffer = null;

        Graphics p_g = p_modifiedImage.getGraphics();
        p_g.drawImage(p_srcImage, 0, 0, null);

        ai_ImageBuffer = ((DataBufferInt) p_modifiedImage.getRaster().getDataBuffer()).getData();

        int i_width = p_modifiedImage.getWidth();
        int i_height = p_modifiedImage.getHeight();

        if (lg_flipVert)
        {
            // Вертикальный флип
            if (i_height >= 2)
            {
                int i_topLineOffset = 0;
                int i_downLineOffset = ai_ImageBuffer.length - i_width;

                int[] ai_lineBuffer = new int[i_width];

                while (true)
                {
                    // Copy of the top line to the buffer
                    System.arraycopy(ai_ImageBuffer, i_topLineOffset, ai_lineBuffer, 0, i_width);
                    // Copy of the down line to the top line
                    System.arraycopy(ai_ImageBuffer, i_downLineOffset, ai_ImageBuffer, i_topLineOffset, i_width);
                    // Copy the buffer to the down line
                    System.arraycopy(ai_lineBuffer, 0, ai_ImageBuffer, i_downLineOffset, i_width);

                    i_topLineOffset += i_width;
                    i_downLineOffset -= i_width;

                    if (i_topLineOffset > i_downLineOffset) break;
                }
            }
        }

        // Горизонтальный флип
        if (lg_flipHorz)
        {
            if (i_width >= 2)
            {
                int i_leftLineOffset = 0;
                int i_rightLineOffset = i_leftLineOffset + i_width - 1;
                int i_maxDown = i_rightLineOffset + ((i_height - 1) * i_width);

                while (i_leftLineOffset < i_rightLineOffset)
                {
                    int i_vertLOffst = i_leftLineOffset;
                    int i_vertROffst = i_rightLineOffset;
                    while (i_vertLOffst < i_maxDown)
                    {
                        int i_buff = ai_ImageBuffer[i_vertLOffst];
                        ai_ImageBuffer[i_vertLOffst] = ai_ImageBuffer[i_vertROffst];
                        ai_ImageBuffer[i_vertROffst] = i_buff;
                        i_vertLOffst += i_width;
                        i_vertROffst += i_width;
                    }

                    i_leftLineOffset++;
                    i_rightLineOffset--;
                }
            }
        }

        // Поворот
        switch (i_Rotation)
        {
            case ROTATE_0:
                ;
                break;
            case ROTATE_90:
                {
                    BufferedImage p_newImage = new BufferedImage(i_height,i_width,BufferedImage.TYPE_INT_ARGB);

                    Graphics2D p_gr = (Graphics2D) p_newImage.getGraphics();

                    AffineTransform p_old = p_gr.getTransform();

                    AffineTransform p_at = new AffineTransform();
                    p_at.setToRotation(Math.PI/2d);

                    p_gr.translate(i_height,0);
                    p_gr.drawImage(p_modifiedImage,p_at,null);
                    p_gr.translate(-i_height,0);
                    p_gr.setTransform(p_old);

                    p_gr = null;

                    p_modifiedImage = p_newImage;
                }
                ;
                break;
            case ROTATE_180:
                {
                    BufferedImage p_newImage = new BufferedImage(i_width,i_height,BufferedImage.TYPE_INT_ARGB);
                    Graphics2D p_gr = (Graphics2D) p_newImage.getGraphics();

                    AffineTransform p_old = p_gr.getTransform();

                    AffineTransform p_at = new AffineTransform();
                    p_at.setToRotation(Math.PI);

                    p_gr.translate(i_width,i_height);
                    p_gr.drawImage(p_modifiedImage,p_at,null);
                    p_gr.translate(-i_width,-i_height);

                    p_gr.setTransform(p_old);

                    p_gr = null;

                    p_modifiedImage = p_newImage;
                }
                ;
                break;
            case ROTATE_270:
                {
                    BufferedImage p_newImage = new BufferedImage(i_height,i_width,BufferedImage.TYPE_INT_ARGB);
                    Graphics2D p_gr = (Graphics2D) p_newImage.getGraphics();

                    AffineTransform p_old = p_gr.getTransform();

                    AffineTransform p_at = new AffineTransform();
                    p_at.setToRotation(Math.PI/2d+Math.PI);

                    p_gr.translate(0,i_width);
                    p_gr.drawImage(p_modifiedImage,p_at,null);
                    p_gr.translate(0,-i_width);
                    p_gr.setTransform(p_old);

                    p_gr = null;

                    p_modifiedImage = p_newImage;
                }
                ;
                break;
        }

        return p_modifiedImage;
    }

    public Dimension getTileCellDim()
    {
        int i_rotation = getRotationState();
        int i_width = 0;
        int i_height = 0;
        if (i_rotation==ImageCnt.ROTATE_90 || i_rotation==ImageCnt.ROTATE_270)
        {
            int i_w = i_height;
            i_height = i_width;
            i_width = i_w;
        }

        int i_w = i_rotation==ImageCnt.ROTATE_90 || i_rotation==ImageCnt.ROTATE_270 ?  getDimension().height / getTileDim().width: getDimension().width / getTileDim().width;
        int i_h = i_rotation==ImageCnt.ROTATE_90 || i_rotation==ImageCnt.ROTATE_270 ?  getDimension().width / getTileDim().height: getDimension().height / getTileDim().height;

        return new Dimension(i_w,i_h);
    }

}

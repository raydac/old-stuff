package ru.coldcore.PixelFontMaker;

import com.raydac_research.PNGWriter.PNGEncoder;

import java.io.*;
import java.awt.*;
import java.awt.image.DataBufferInt;
import java.awt.image.BufferedImage;

public class ExportBINFont
{
    public static final int FORMAT_VERSION = 0x1;

    public static final int FLAG_NONE = 0;
    public static final int FLAG_CROPVERTICAL = 1;
    public static final int FLAG_CROPHORIZONTAL = 2;
    public static final int FLAG_TRANSP_BCKG = 4;
    public static final int FLAG_TRANSP_FRGR = 8;
    public static final int FLAG_EMBD_IMG = 16;
    public static final int FLAG_EMBD_SEPARATED_IMG = 32;

    public static File p_LastDir;

    public static final int colorCorrectionForSamsungLGphones(Color _color)
    {
        // Обрабатываем изображение что бы не было проблем на Samsung и LG где белый цвет в диапазоне от 251,251,251-255,255,255 прозрачен
            int i_argb = _color.getRGB();

            int i_a = i_argb >>> 24;
            int i_r = (i_argb >>> 16) & 0xFF;
            int i_g = (i_argb >>> 8) & 0xFF;
            int i_b = i_argb & 0xFF;

                if (i_r > 250) i_r = 250;
                if (i_g > 250) i_g = 250;
                if (i_b > 250) i_b = 250;

                i_argb = 0xFF000000 | (i_r << 16) | (i_g << 8) | i_b;

            return i_argb;
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

    private static final byte [] convertImageToPNG(BufferedImage _img,boolean _makeColorCorrection) throws IOException
    {
        ByteArrayOutputStream p_outStream = new ByteArrayOutputStream(9000);
        BufferedImage p_buffImage = _img;
        if (_makeColorCorrection) colorCorrectionForSamsungLGphones(p_buffImage);
        PNGEncoder p_pngEncoder = new PNGEncoder(p_buffImage, p_outStream, null, false);
        p_pngEncoder.setCompressionLevel(9);
        p_pngEncoder.encodeImage();

        p_outStream.close();
        byte[] ab_imageArr = p_outStream.toByteArray();
        return ab_imageArr;
    }

    public static final void export(FontContainer _contaner, File _file, Component _owner) throws Throwable
    {
        p_LastDir = _file;
        boolean lg_embeddedFontImage = Utils.askDialog(_owner, "Font format", "Do you want to embed the font image into the binary file?");
        boolean lg_separatedChars = false;
        boolean lg_colorCorrection = false;

        if (lg_embeddedFontImage)
        {
            lg_separatedChars = Utils.askDialog(_owner, "Separated chars", "Use separated char images?");
        }

        lg_colorCorrection = Utils.askDialog(_owner,"Color correction","Make SAMSUNG and LG color correction?");

        FileOutputStream p_fos = null;

        try
        {
            p_fos = new FileOutputStream(_file);
            DataOutputStream p_dos = new DataOutputStream(p_fos);

            // записываекм версию
            p_dos.writeByte(FORMAT_VERSION);

            // записываем набор символов, поддерживаемый фонтом
            p_dos.writeUTF(_contaner.getCharSet());

            // расстояние между символами
            p_dos.writeByte(_contaner.i_HorzInterval);

            // расстояние между строками
            p_dos.writeByte(_contaner.i_VertInterval);

            // максимальная ширина символа
            p_dos.writeShort(_contaner.i_MaxCharWidth);

            // максимальная высота символа
            p_dos.writeShort(_contaner.i_MaxCharHeight);

            // цвет фона
            p_dos.writeInt(colorCorrectionForSamsungLGphones(_contaner.p_Background));

            // флаги
            int i_flags = (_contaner.lg_CropHorizontal ? FLAG_CROPHORIZONTAL : FLAG_NONE) | (_contaner.lg_CropVertical ? FLAG_CROPVERTICAL : FLAG_NONE) | (_contaner.lg_TransparentBackground ? FLAG_TRANSP_BCKG : FLAG_NONE)|(_contaner.lg_TransparentForeground? FLAG_TRANSP_FRGR : FLAG_NONE)|(lg_embeddedFontImage ? FLAG_EMBD_IMG : FLAG_NONE)|(lg_separatedChars ? FLAG_EMBD_SEPARATED_IMG : FLAG_NONE) ;
            p_dos.writeByte(i_flags);

            // таблица ширин символов
            Object [] ap_obj = _contaner.getCharacters();

            for(int li=0;li<ap_obj.length;li++)
            {
                FontContainer.FontCharacter p_char = (FontContainer.FontCharacter) ap_obj[li];
                p_dos.writeByte(p_char.i_Width);
            }

            if ((_contaner.lg_CropHorizontal || _contaner.lg_CropVertical) || !lg_separatedChars)
            {
                // таблица нарезок символов
                for(int li=0;li<ap_obj.length;li++)
                {
                    FontContainer.FontCharacter p_char = (FontContainer.FontCharacter) ap_obj[li];

                    Rectangle p_rect = null;
                    if (lg_separatedChars)
                        p_rect = p_char.p_CropRectangle;
                    else
                        p_rect = p_char.p_CoordOnImage;

                    if (p_char.lg_EmptySymbol)
                    {
                        // X
                        p_dos.writeByte(0xFF);
                        // Y
                        p_dos.writeByte(0xFF);
                        // WIDTH
                        p_dos.writeByte(0xFF);
                        // HEIGHT
                        p_dos.writeByte(0xFF);
                    }
                    else
                    {
                        p_dos.writeByte(p_rect.x);
                        p_dos.writeByte(p_rect.y);
                        p_dos.writeByte(p_rect.width);
                        p_dos.writeByte(p_rect.height);
                    }
                }

                if ((_contaner.lg_CropHorizontal || _contaner.lg_CropVertical) && !lg_separatedChars)
                {
                    // записываем смещения XY до начала вывода символа
                    for(int li=0;li<ap_obj.length;li++)
                    {
                        FontContainer.FontCharacter p_char = (FontContainer.FontCharacter) ap_obj[li];

                        Rectangle p_rect = null;
                        p_rect = p_char.p_CropRectangle;

                        if (p_char.lg_EmptySymbol)
                        {
                            // X
                            p_dos.writeByte(0xFF);
                            // Y
                            p_dos.writeByte(0xFF);
                        }
                        else
                        {
                            p_dos.writeByte(p_rect.x);
                            p_dos.writeByte(p_rect.y);
                        }
                    }
                }
            }


            if (lg_embeddedFontImage)
            {
                if (lg_separatedChars)
                {
                    // записываем символы

                     for(int li=0;li<ap_obj.length;li++)
                     {
                         FontContainer.FontCharacter p_char = (FontContainer.FontCharacter) ap_obj[li];

                         Rectangle p_rect = p_char.p_CropRectangle;

                         if (_contaner.lg_CropHorizontal || _contaner.lg_CropVertical)
                         {
                             if (p_char.lg_EmptySymbol)
                             {
                                 p_dos.writeShort(0);
                             }
                             else
                             {
                                 // делаем картинку
                                 BufferedImage p_img = new BufferedImage(p_rect.width,p_rect.height,BufferedImage.TYPE_INT_ARGB);
                                 Graphics p_g = p_img.getGraphics();
                                 p_g.drawImage(p_char.p_CharImage,-p_rect.x, -p_rect.y, null);

                                 byte[] ab_imageArr = convertImageToPNG(_contaner.getFontImage(),lg_colorCorrection);

                                 p_dos.writeShort(ab_imageArr.length);
                                 p_dos.write(ab_imageArr);
                             }
                         }
                         else
                         {

                             BufferedImage p_img = new BufferedImage(p_char.i_Width,p_char.i_Height,BufferedImage.TYPE_INT_ARGB);
                             Graphics p_g = p_img.getGraphics();
                             p_g.drawImage(p_char.p_CharImage,0, 0, null);

                             byte[] ab_imageArr = convertImageToPNG(p_img,lg_colorCorrection);
                             p_dos.writeShort(ab_imageArr.length);
                             p_dos.write(ab_imageArr);

                         }
                     }
                }
                else
                {

                    // формируем картинку фонта
                    byte[] ab_imageArr = convertImageToPNG(_contaner.getFontImage(),lg_colorCorrection);
                    // записываем размер
                    p_dos.writeInt(ab_imageArr.length);

                    // записываем данные картинки в поток
                    p_dos.write(ab_imageArr);
                }
            }
            else
            {
                // формируем картинку фонта
                byte[] ab_imageArr = convertImageToPNG(_contaner.getFontImage(),lg_colorCorrection);

                // записываем размер
                p_dos.writeInt(ab_imageArr.length);

                // записываем на диск
                String s_path = _file.getAbsolutePath()+".png";
                FileOutputStream p_outPNG = null;
                try
                {
                    p_outPNG = new FileOutputStream(s_path);
                    p_outPNG.write(ab_imageArr);
                    p_outPNG.flush();
                }
                finally
                {
                    if (p_outPNG!=null)
                    {
                        try
                        {
                            p_outPNG.close();
                        }
                        catch (Throwable e)
                        {
                        }

                        p_outPNG = null;
                    }
                }
            }

            p_dos.flush();
        }
        finally
        {
            if (p_fos != null)
            {
                try
                {
                    p_fos.close();
                }
                catch (Throwable _o)
                {
                }
                p_fos = null;
            }
        }
    }


}

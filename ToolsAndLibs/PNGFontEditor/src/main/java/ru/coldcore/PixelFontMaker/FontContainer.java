package ru.coldcore.PixelFontMaker;

import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.RenderedImage;
import java.io.*;

public class FontContainer
{


    public class FontCharacter
    {
        protected BufferedImage p_CharImage;
        protected char ch_char;

        public int i_Height;
        public int i_Width;

        public boolean lg_EmptySymbol;

        public Rectangle p_CropRectangle;
        public Rectangle p_CoordOnImage;

        private void processImage(Image _image)
        {
            int i_width = _image.getWidth(null);
            int i_height = _image.getHeight(null);

            p_CharImage = new BufferedImage(i_width, i_height, BufferedImage.TYPE_INT_ARGB);
            p_CharImage.getGraphics().drawImage(_image, 0, 0, null);

            int[] ai_ImageBuffer = ((DataBufferInt) p_CharImage.getRaster().getDataBuffer()).getData();

            int[] ai_copyBuffer = new int[ai_ImageBuffer.length];
            final int i_len = ai_ImageBuffer.length;
            System.arraycopy(ai_ImageBuffer, 0, ai_copyBuffer, 0, i_len);

            if (lg_enableBorder)
            {
                int i_color = p_Border.getRGB() | 0xFF000000;

                //int[] ai_ImageBuffer = ((DataBufferInt) p_CharImage.getRaster().getDataBuffer()).getData();

                for (int li = 0; li < i_len; li++)
                {
                    int i_val = ai_copyBuffer[li];
                    if ((i_val & 0xFF000000) != 0)
                    {
                        // рисуем границу по кресту

                        // верх
                        int i_v = li - i_width;
                        if (i_v >= 0)
                        {
                            if ((ai_copyBuffer[i_v] & 0xFF000000) == 0) ai_ImageBuffer[i_v] = i_color;
                        }

                        // низ
                        i_v = li + i_width;
                        if (i_v < i_len)
                        {
                            if ((ai_copyBuffer[i_v] & 0xFF000000) == 0) ai_ImageBuffer[i_v] = i_color;
                        }

                        // лево
                        i_v = li - 1;
                        if (i_v >= 0)
                        {
                            if ((ai_copyBuffer[i_v] & 0xFF000000) == 0) ai_ImageBuffer[i_v] = i_color;
                        }

                        // право
                        i_v = li + 1;
                        if (i_v < i_len)
                        {
                            if (i_v / i_width == li / i_width)
                            {
                                if ((ai_copyBuffer[i_v] & 0xFF000000) == 0) ai_ImageBuffer[i_v] = i_color;
                            }
                        }

                        // лево
                        i_v = li - 1;
                        if (i_v < 0)
                        {
                            if (i_v / i_width == li / i_width)
                            {
                                if ((ai_copyBuffer[i_v] & 0xFF000000) == 0) ai_ImageBuffer[i_v] = i_color;
                            }
                        }
                    }
                }

            }

            // очищаем символы если должны быть прозрачны
            if (lg_TransparentForeground)
            {
                for (int li = 0; li < i_len; li++)
                {
                    int i_argbBorder = ai_copyBuffer[li];
                    if ((i_argbBorder & 0xFF000000) != 00)
                    {
                        ai_ImageBuffer[li] = 0;
                    }
                }
            }

            // получаем кроп координаты
            int i_tX = i_width;
            int i_dX = 0;
            int i_tY = i_height;
            int i_dY = 0;

            p_CropRectangle = new Rectangle();

            for (int li = 0; li < i_len; li++)
            {
                int i_argb = ai_ImageBuffer[li];

                if ((i_argb & 0xFF000000) != 0)
                {
                    int i_cx = li % i_width;
                    int i_cy = li / i_width;

                    if (i_cx < i_tX) i_tX = i_cx;
                    if (i_cy < i_tY) i_tY = i_cy;

                    if (i_cx > i_dX) i_dX = i_cx;
                    if (i_cy > i_dY) i_dY = i_cy;
                }
            }

            p_CropRectangle.x = i_tX;
            p_CropRectangle.y = i_tY;
            p_CropRectangle.width = i_dX - i_tX + 1;
            p_CropRectangle.height = i_dY - i_tY + 1;

            i_Width = i_width;
            i_Height = i_height;

            if (p_CropRectangle.width < 0 && p_CropRectangle.height < 0)
            {
                lg_EmptySymbol = true;
                return;
            }

            lg_EmptySymbol = false;
        }

        public FontCharacter(char _char)
        {
            BufferedImage p_buffImage = null;

            if (lg_BitmapFont)
            {
                int i_index = s_CharSet.indexOf(_char);
                if (i_index < 0) i_index = 0;

                int i_charsInLine = p_fullImage.getWidth(null) / i_bitmapCharWidth;

                int i_y = (i_index / i_charsInLine) * i_bitmapCharHeight;
                int i_x = (i_index % i_charsInLine) * i_bitmapCharWidth;

                int i_imgw = i_bitmapCharWidth + (lg_enableBorder ? 2 : 0) + (lg_CropHorizontal ? 0 : i_HorzInterval);
                int i_imgh = i_bitmapCharHeight + (lg_enableBorder ? 2 : 0) + (lg_CropVertical ? 0 : i_VertInterval);

                p_buffImage = new BufferedImage(i_imgw, i_imgh, BufferedImage.TYPE_INT_ARGB);

                Graphics p_gr = p_buffImage.getGraphics();

                if (lg_enableBorder)
                {
                    p_gr.setClip(1,1,i_bitmapCharWidth,i_bitmapCharHeight);
                }

                p_gr.drawImage(p_fullImage, (lg_enableBorder ? 1 : 0) -i_x,(lg_enableBorder ? 1 : 0) -i_y, null);

                p_gr.setClip(0,0,i_imgw,i_imgh);
            }
            else
            {

                int i_width = p_FontMetrics.charWidth(_char) + (lg_enableBorder ? 2 : 0) + (lg_CropHorizontal ? 0 : i_HorzInterval);
                int i_height = p_FontMetrics.getAscent() + p_FontMetrics.getDescent() - p_FontMetrics.getLeading() + (lg_enableBorder ? 2 : 0) + (lg_CropVertical ? 0 : i_VertInterval);

                p_buffImage = new BufferedImage(i_width, i_height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D p_g = (Graphics2D) p_buffImage.getGraphics();

                p_g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, lg_Antialias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
                p_g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, lg_Antialias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
                p_g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                p_g.setFont(p_Font);
                p_g.setColor(p_Foreground);
                p_g.drawChars(new char[]{_char}, 0, 1, lg_enableBorder ? 1 : 0, p_FontMetrics.getAscent() + (lg_enableBorder ? 1 : 0));
            }
            processImage(p_buffImage);
        }
    }

    protected ArrayList<FontCharacter> p_FontChars;
    protected Font p_Font;
    protected Image p_fullImage;
    protected File p_FontFile;
    protected FontMetrics p_FontMetrics;
    protected String s_CharSet;
    protected int i_Size;
    protected int i_Style;
    protected Color p_Background;
    protected Color p_Foreground;
    protected Color p_Border;

    protected boolean lg_TransparentBackground;
    protected boolean lg_TransparentForeground;

    protected boolean lg_enableBorder;
    protected int i_HorzInterval;
    protected int i_VertInterval;

    protected int i_bitmapCharWidth;
    protected int i_bitmapCharHeight;

    protected boolean lg_CropVertical;
    protected boolean lg_CropHorizontal;

    protected int i_ImageWidth;
    protected int i_ImageHeight;

    protected int i_MaxCharWidth;
    protected int i_MaxCharHeight;

    protected boolean lg_Antialias;

    protected boolean lg_BitmapFont;

    protected BufferedImage p_ResultImage;

    public boolean isBitmapFont()
    {
        return lg_BitmapFont;
    }

    public int getMaxCharWidth()
    {
        return i_MaxCharWidth;
    }

    public Image getCharsetImage()
    {
        return p_fullImage;
    }

    public int getMaxCharHeight()
    {
        return i_MaxCharHeight;
    }

    public File getFontFile()
    {
        return p_FontFile;
    }

    public BufferedImage getResultImage()
    {
        return p_ResultImage;
    }

    public void setBorderEnable(boolean _flag)
    {
        lg_enableBorder = _flag;
    }

    public boolean isBorderEnable()
    {
        return lg_enableBorder;
    }

    public void setCropHorizontal(boolean _value)
    {
        lg_CropHorizontal = _value;
    }

    public void setCropVertical(boolean _value)
    {
        lg_CropVertical = _value;
    }

    public boolean isCropVertical()
    {
        return lg_CropVertical;
    }

    public boolean isCropHorizontal()
    {
        return lg_CropHorizontal;
    }

    public void setHorzInterval(int _value)
    {
        i_HorzInterval = _value;
    }

    public void setVertInterval(int _value)
    {
        i_VertInterval = _value;
    }

    public void setTransparentBackground(boolean _flag)
    {
        lg_TransparentBackground = _flag;
    }

    public void setTransparentForeground(boolean _flag)
    {
        lg_TransparentForeground = _flag;
    }

    public boolean isTransparentBackground()
    {
        return lg_TransparentBackground;
    }

    public boolean isTransparentForeground()
    {
        return lg_TransparentForeground;
    }

    public int getHorzInterval()
    {
        return i_HorzInterval;
    }

    public int getVertInterval()
    {
        return i_VertInterval;
    }

    public Color getBackgroundColor()
    {
        return p_Background;
    }

    public Color getBorderColor()
    {
        return p_Border;
    }

    public Color getForegroundColor()
    {
        return p_Foreground;
    }

    public void setImageWidth(int _value)
    {
        i_ImageWidth = _value;
    }

    public void setImageHeight(int _value)
    {
        i_ImageHeight = _value;
    }

    public int getImageWidth()
    {
        return i_ImageWidth;
    }

    public int getImageHeight()
    {
        return i_ImageHeight;
    }

    public boolean isQuality()
    {
        return lg_Antialias;
    }

    public void setQuality(boolean _flag)
    {
        lg_Antialias = _flag;
        updateChars();
    }

    public String getCharSet()
    {
        return s_CharSet;
    }

    public Object[] getCharacters()
    {
        return p_FontChars.toArray();
    }

    public void setCharacters(String _chars)
    {
        s_CharSet = _chars;
    }

    public void setBackground(Color _color)
    {
        p_Background = _color;
    }

    public void setForeground(Color _color)
    {
        p_Foreground = _color;
    }

    public void setBorder(Color _color)
    {
        p_Border = _color;
    }

    public int getSize()
    {
        return i_Size;
    }

    public void setSize(int _size)
    {
        i_Size = _size;
        p_Font = p_Font.deriveFont(p_Font.getStyle(), i_Size);
        p_FontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(p_Font);
    }

    public void setStyle(int _style)
    {
        i_Style = _style;
        p_Font = p_Font.deriveFont(_style, p_Font.getSize());
        p_FontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(p_Font);
    }

    public int getStyle()
    {
        return i_Style;
    }

    public void updateChars()
    {
        p_FontChars.clear();
        char[] ach_chars = s_CharSet.toCharArray();

        int i_maxW = 0;
        int i_maxH = 0;

        for (int li = 0; li < ach_chars.length; li++)
        {
            char ch_char = ach_chars[li];
            FontCharacter p_char = new FontCharacter(ch_char);
            if (i_maxW < p_char.i_Width) i_maxW = p_char.i_Width;
            if (i_maxH < p_char.i_Height) i_maxH = p_char.i_Height;

            p_FontChars.add(p_char);
        }

        i_MaxCharWidth = i_maxW;
        i_MaxCharHeight = i_maxH;

        p_ResultImage = getFontImage();
    }

    public void setFont(Font _font, File _fontFile)
    {
        if (!lg_BitmapFont)
        {
            p_Font = _font;
            p_FontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(_font);
            p_FontFile = _fontFile;
        }
    }

    public BufferedImage getFontImage()
    {
        final BufferedImage p_buffImage = new BufferedImage(i_ImageWidth, i_ImageHeight, BufferedImage.TYPE_INT_ARGB);
        final Graphics p_buffGraphics = p_buffImage.getGraphics();

        // отрисовываем ограничивающий прямоугольник
        if (!lg_TransparentBackground)
        {
            p_buffGraphics.setColor(p_Background);
            p_buffGraphics.fillRect(0, 0, i_ImageWidth, i_ImageHeight);
        }

        // отрисовываем фонт
        int i_size = p_FontChars.size();
        Object[] ap_chars = getCharacters();

        int i_x = 0;
        int i_y = 0;

        FontContainer.FontCharacter p_character = null;

        boolean lg_firstSymbol = true;

        boolean lg_cropVert = isCropVertical();
        boolean lg_cropHorz = isCropHorizontal();
        boolean lg_transparent = isTransparentBackground();

        int i_borderW = i_ImageWidth;
        int i_borderH = i_ImageHeight;

        int i_maxHeight = 0;

        for (int li = 0; li < i_size; li++)
        {
            p_character = (FontContainer.FontCharacter) ap_chars[li];

            int i_cx = 0;
            int i_cy = 0;
            int i_cw = 0;
            int i_ch = 0;

            if ((lg_cropVert || lg_cropHorz) && p_character.lg_EmptySymbol) continue;

            if (lg_cropVert)
            {
                i_cy = p_character.p_CropRectangle.y;
                i_ch = p_character.p_CropRectangle.height;
            }
            else
            {
                i_cy = 0;
                i_ch = p_character.i_Height;
            }

            if (lg_cropHorz)
            {
                i_cx = p_character.p_CropRectangle.x;
                i_cw = p_character.p_CropRectangle.width;
            }
            else
            {
                i_cx = 0;
                i_cw = p_character.i_Width;
            }

            if (i_ch > i_maxHeight) i_maxHeight = i_ch;

            if (i_cw + i_x > i_borderW)
            {
                if (lg_firstSymbol)
                {
                    p_buffGraphics.setClip(i_x, i_y, i_cw, i_ch);
                    p_buffGraphics.drawImage(p_character.p_CharImage, i_x - i_cx, i_y - i_cy, null);
                    p_character.p_CoordOnImage = new Rectangle(i_x, i_y, i_cw, i_ch);
                    i_x = 0;
                    i_y += i_maxHeight;
                    i_maxHeight = 0;
                    lg_firstSymbol = true;
                }
                else
                {
                    i_x = 0;
                    i_y += i_maxHeight;
                    i_maxHeight = 0;
                    p_buffGraphics.setClip(i_x, i_y, i_cw, i_ch);
                    p_buffGraphics.drawImage(p_character.p_CharImage, i_x - i_cx, i_y - i_cy, null);
                    p_character.p_CoordOnImage = new Rectangle(i_x, i_y, i_cw, i_ch);
                    i_x += i_cw;
                }
            }
            else
            {
                p_buffGraphics.setClip(i_x, i_y, i_cw, i_ch);
                p_buffGraphics.drawImage(p_character.p_CharImage, i_x - i_cx, i_y - i_cy, null);
                p_character.p_CoordOnImage = new Rectangle(i_x, i_y, i_cw, i_ch);
                i_x += i_cw;
                lg_firstSymbol = false;
            }
        }

        // обрабатываем альфа канал если имеется и прозрачный фон
        final int[] ai_ImageBuffer = ((DataBufferInt) p_buffImage.getRaster().getDataBuffer()).getData();

        int i_len = ai_ImageBuffer.length;
        int i_rgb = p_Background.getRGB();

        for (int li = 0; li < i_len; li++)
        {
            int i_argb = ai_ImageBuffer[li];
            if ((i_argb & 0xFF000000) != 0)
            {
                ai_ImageBuffer[li] = Utils.mixColors(i_argb, i_rgb);
            }
        }

        return p_buffImage;
    }

    public void saveToStream(OutputStream _outStream) throws IOException
    {
        DataOutputStream p_dos = new DataOutputStream(_outStream);

        p_dos.writeBoolean(lg_BitmapFont);

        if (lg_BitmapFont)
        {
            if (p_FontFile != null)
            {
                // записываем фонт как бинарный блок
                ByteArrayOutputStream p_baos = new ByteArrayOutputStream();
                ImageIO.write((RenderedImage) p_fullImage, "png", p_baos);

                p_dos.writeUTF(p_FontFile != null ? p_FontFile.getAbsolutePath() : "");

                byte[] ab_img = p_baos.toByteArray();
                p_dos.writeInt(ab_img.length);
                p_dos.write(ab_img);
            }
        }
        else
        {
            if (p_FontFile != null)
            {
                // записываем фонт как бинарный блок
                p_dos.writeBoolean(true);

                byte[] ab_font = Utils.loadFile(p_FontFile);

                p_dos.writeUTF(p_FontFile.getAbsolutePath());
                p_dos.writeInt(ab_font.length);
                p_dos.write(ab_font);
            }
            else
            {
                throw new IOException("Can't find font file");
            }
        }

        p_dos.writeInt(i_bitmapCharWidth);
        p_dos.writeInt(i_bitmapCharHeight);

        p_dos.writeUTF(s_CharSet);

        p_dos.writeInt(p_Foreground.getRGB());
        p_dos.writeInt(p_Background.getRGB());
        p_dos.writeInt(p_Border.getRGB());

        p_dos.writeBoolean(lg_Antialias);
        p_dos.writeBoolean(lg_CropHorizontal);
        p_dos.writeBoolean(lg_CropVertical);
        p_dos.writeBoolean(lg_enableBorder);
        p_dos.writeBoolean(lg_TransparentBackground);
        p_dos.writeBoolean(lg_TransparentForeground);

        p_dos.writeInt(i_HorzInterval);
        p_dos.writeInt(i_VertInterval);
        p_dos.writeInt(i_ImageWidth);
        p_dos.writeInt(i_ImageHeight);
        p_dos.writeInt(i_Size);
        p_dos.writeInt(i_Style);
    }

    public void loadFromStream(InputStream _inStream) throws IOException
    {
        DataInputStream p_dis = new DataInputStream(_inStream);

        lg_BitmapFont = p_dis.readBoolean();

        if (!lg_BitmapFont)
        {
            // TTF фонт
            String s_fontPath = p_dis.readUTF();

            File p_file = null;

            if (s_fontPath.length()==0)
            {
                p_file = null;
            }
            else
            {
                p_file = new File(s_fontPath);
                if (!p_file.exists()) p_file = null;
            }

            int i_size = p_dis.readInt();
            byte[] ab_font = new byte[i_size];

            Utils.readArrayFromStream(ab_font, _inStream);

            ByteArrayInputStream p_bais = new ByteArrayInputStream(ab_font);
            try
            {
                Font p_font = Font.createFont(Font.TRUETYPE_FONT, p_bais);
                setFont(p_font, p_file);
            }
            catch (FontFormatException e)
            {
                throw new IOException("Font format exception");
            }
            p_bais.close();
            p_bais = null;
            ab_font = null;
        }
        else
        {
            // Bitmap фонт
            // внутренний

            String s_fontPath = p_dis.readUTF();

            File p_file = null;

            if (s_fontPath.length()==0)
            {
                p_file = null;
            }
            else
            {
            p_file = new File(s_fontPath);
            if (!p_file.exists()) p_file = null;
            }

            int i_size = p_dis.readInt();
            byte[] ab_font = new byte[i_size];

            Utils.readArrayFromStream(ab_font, _inStream);

            try
            {
                p_fullImage = ImageIO.read(new ByteArrayInputStream(ab_font));
            }
            catch (Throwable e)
            {
                throw new IOException("Cant load font image");
            }
            ab_font = null;
        }

        i_bitmapCharWidth = p_dis.readInt();
        i_bitmapCharHeight = p_dis.readInt();

        setCharacters(p_dis.readUTF());

        setForeground(new Color(p_dis.readInt()));
        setBackground(new Color(p_dis.readInt()));
        setBorder(new Color(p_dis.readInt()));

        setQuality(p_dis.readBoolean());
        setCropHorizontal(p_dis.readBoolean());
        setCropVertical(p_dis.readBoolean());
        setBorderEnable(p_dis.readBoolean());
        setTransparentBackground(p_dis.readBoolean());
        setTransparentForeground(p_dis.readBoolean());

        setHorzInterval(p_dis.readInt());
        setVertInterval(p_dis.readInt());
        setImageWidth(p_dis.readInt());
        setImageHeight(p_dis.readInt());
        setSize(p_dis.readInt());
        setStyle(p_dis.readInt());

        updateChars();
    }

    public FontContainer(String _chars)
    {
        this(new Font("Dialog", Font.BOLD, 10), Color.black, Color.white, Font.PLAIN, 10, _chars);
    }

    public FontContainer(Image _charsImage, File _fontFile, int _charWidth, int _charHeight, String _characters)
    {
        lg_BitmapFont = true;
        p_fullImage = _charsImage;

        i_bitmapCharWidth = _charWidth;
        i_bitmapCharHeight = _charHeight;

        p_FontFile = _fontFile;

        lg_Antialias = true;
        lg_enableBorder = false;
        lg_CropHorizontal = false;
        lg_CropVertical = false;
        lg_TransparentBackground = true;
        lg_TransparentForeground = false;

        i_ImageWidth = _charsImage.getWidth(null);
        i_ImageHeight = _charsImage.getHeight(null);

        p_FontChars = new ArrayList<FontCharacter>();
        p_Border = Color.ORANGE;

        setBackground(Color.BLACK);
        setForeground(Color.WHITE);

        setCharacters(_characters);

        updateChars();
    }

    public FontContainer(Font _font, Color _foreground, Color _background, int _style, int _size, String _characters)
    {
        lg_Antialias = true;
        lg_enableBorder = false;
        lg_CropHorizontal = false;
        lg_CropVertical = false;
        lg_TransparentBackground = true;
        lg_TransparentForeground = false;

        i_ImageWidth = 128;
        i_ImageHeight = 128;

        p_FontChars = new ArrayList<FontCharacter>();
        p_Border = Color.ORANGE;
        setBackground(_background);
        setForeground(_foreground);
        setFont(_font, null);
        setStyle(_style);
        setSize(_size);
        setCharacters(_characters);

        updateChars();
    }

}

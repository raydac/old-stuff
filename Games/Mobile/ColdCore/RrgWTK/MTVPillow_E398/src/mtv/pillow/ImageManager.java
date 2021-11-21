package mtv.pillow;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;
import java.io.InputStream;
import java.io.DataInputStream;

public class ImageManager
{
    private static Image[] ap_Images;
    private static byte[] ab_dynamicarray;
    private static short[] ash_imageInfo;

    private static final String FULLIMAGE = "/fimg";
    private static final String IMAGEMAPINFO = "/map.bin";
    private static final String DYNIMAGES = "/dyn.bin";

    private static int i_imagesNum = 0;
    private static int i_groups = 0;


    public static final int IMAGEINFO_LENGTH = 9;

    /**
     * Пакуется в общее изображение
     */
    public static final int FLAG_NORMAL = 0;

    /**
     * Хранится во внешнем файле
     */
    public static final int FLAG_EXTERNAL = 1;

    /**
     * Динамически распаковывается
     */
    public static final int FLAG_DYNAMIC = 2;

    /**
     * Ссылка
     */
    public static final int FLAG_LINK = 3;

    public static final void release()
    {
        ash_imageInfo = null;
        ab_dynamicarray = null;
        ap_Images = null;
        Runtime.getRuntime().gc();
    }

    protected static final void drawImage(int _imageOffset, Graphics _g, int _x, int _y)
    {
        short[] ash_map = ash_imageInfo;
        int i_type = ash_map[_imageOffset++];

        Image p_fullImage = ap_Images[i_type >>> 4];
        i_type &= 0xF;

        if (i_type == FLAG_LINK)
        {
            _imageOffset = ash_map[_imageOffset];
            i_type = ash_map[_imageOffset++];
        }

        int i_xMap = ash_map[_imageOffset++];
        int i_yMap = ash_map[_imageOffset++];
        _x += ash_map[_imageOffset++];
        _y += ash_map[_imageOffset++];
        int i_cW = ash_map[_imageOffset++];
        int i_cH = ash_map[_imageOffset];

        switch (i_type)
        {
            case FLAG_EXTERNAL:
                {
                    int i_index = i_xMap;
                    String s_name = Integer.toString(i_index);
                    Image p_img = null;
                    try
                    {
                        p_img = Image.createImage(s_name);
                        _g.drawImage(p_img, _x, _y, 0);
                    }
                    catch (Exception e)
                    {
                    }
                    p_img = null;
                }
        ;
                break;
            case FLAG_DYNAMIC:
                {
                    int i_offset = i_xMap & 0xFFFF;
                    int i_len = i_yMap & 0xFFFF;

                    Image p_img = Image.createImage(ab_dynamicarray, i_offset, i_len);
                    _g.drawImage(p_img, _x, _y, 0);
                    p_img = null;
                }
        ;
                break;
            case FLAG_NORMAL:
                {
                    _g.setClip(_x, _y, i_cW, i_cH);
                    _g.drawImage(p_fullImage, _x - i_xMap, _y - i_yMap, 0);
                }
        ;
                break;
        }
    }

    protected static final void drawImage(int _imageOffset, Graphics _g, int _x, int _y, int _transform)
    {
        short[] ash_map = ash_imageInfo;

        int i_type = ash_map[_imageOffset++] & 0xFFFF;

        int i_img = i_type >>> 4;
        Image p_fullImage = ap_Images[i_img];
        i_type &= 0xF;

        if (i_type == FLAG_LINK)
        {
            _imageOffset = ash_map[_imageOffset];
            i_type = ash_map[_imageOffset++];
        }

        int i_xMap = ash_map[_imageOffset++];
        int i_yMap = ash_map[_imageOffset++];
        int i_xOff = ash_map[_imageOffset++];

        int i_yOff = ash_map[_imageOffset++];
        int i_cW = ash_map[_imageOffset++];
        int i_cH = ash_map[_imageOffset++];

        int i_fW = ash_map[_imageOffset++];
        //int i_fH = ash_map[_imageOffset];

        if (_transform == javax.microedition.lcdui.game.Sprite.TRANS_MIRROR)
        {
            int i_delta = i_fW - (i_xOff + i_cW);
            _x += i_delta;
            _y += i_yOff;
        }
        else
        {
            _x += i_xOff;
            _y += i_yOff;
        }

        switch (i_type)
        {
            case FLAG_EXTERNAL:
                {
                    int i_index = i_xMap;
                    String s_name = Integer.toString(i_index);
                    Image p_img = null;
                    try
                    {
                        p_img = Image.createImage(s_name);
                        _g.drawRegion(p_img, 0, 0, p_img.getWidth(), p_img.getHeight(), _transform, _x, _y, 0);
                    }
                    catch (Exception e)
                    {
                    }
                    p_img = null;
                }
        ;
                break;
            case FLAG_DYNAMIC:
                {
                    int i_offset = i_xMap & 0xFFFF;
                    int i_len = i_yMap & 0xFFFF;
                    Image p_img = Image.createImage(ab_dynamicarray, i_offset, i_len);
                    _g.drawRegion(p_img, 0, 0, p_img.getWidth(), p_img.getHeight(), _transform, _x, _y, 0);
                    p_img = null;
                }
        ;
                break;
            case FLAG_NORMAL:
                {
                    _g.setClip(_x, _y, i_cW, i_cH);
                    _g.drawRegion(p_fullImage, i_xMap, i_yMap, i_cW, i_cH, _transform, _x, _y, 0);
                }
        ;
                break;
        }
    }

    private static final void loadMapResource(Class _this) throws Exception
    {
        InputStream p_in = _this.getResourceAsStream(IMAGEMAPINFO);
        DataInputStream p_dis = new DataInputStream(p_in);
        if (p_in != null)
        {
            //#if DEBUG
            System.out.println("Image map array loading...");
            //#endif

            i_imagesNum = p_dis.readUnsignedShort();
            i_groups = p_dis.readUnsignedShort();

            int i_len = i_imagesNum * IMAGEINFO_LENGTH;
            short[] ash_array = new short[i_len];
            Runtime.getRuntime().gc();

            //#if DEBUG
            System.out.println(i_imagesNum + " images");
            System.out.println("Array length "+i_len);
            //#endif

            int i_offset = 0;
            for (int li = 0; li < i_imagesNum;li++)
            {
                // Тип хранения
                ash_array[i_offset++] = (short) p_dis.readUnsignedByte();
                // Координата X на картинке
                ash_array[i_offset++] = p_dis.readShort();
                // Координата Y на картинке
                ash_array[i_offset++] = p_dis.readShort();
                // Смещение X
                ash_array[i_offset++] = p_dis.readShort();
                // Смещение Y
                ash_array[i_offset++] = p_dis.readShort();
                // Ширина области
                ash_array[i_offset++] = p_dis.readShort();
                // Высота области
                ash_array[i_offset++] = p_dis.readShort();

                // Полная ширина области
                ash_array[i_offset++] = p_dis.readShort();
                // Полная высота области
                ash_array[i_offset++] = p_dis.readShort();
            }
            p_dis.close();
            ash_imageInfo = ash_array;

            //#if DEBUG
            System.out.println("Image map array loading...ok");
            //#endif
        }
        //#if DEBUG
        else
        {
            System.out.println("I can't find image map resource");
        }
        //#endif
        p_in = null;
        p_dis = null;
        Runtime.getRuntime().gc();
    }

    public static final void init(Class _this, Image[] _images) throws Exception
    {
        // Грузим карту изображений
        loadMapResource(_this);

        // Грузим большую картинку
        ap_Images = _images;
        Runtime.getRuntime().gc();
        // Грузим динамический массив
        InputStream p_in = _this.getResourceAsStream(DYNIMAGES);
        if (p_in != null)
        {
            //#if DEBUG
            System.out.println("Dynamic images loading...");
            //#endif

            int i_len = p_in.read();
            i_len = i_len | (p_in.read() << 8);
            Runtime.getRuntime().gc();
            ab_dynamicarray = new byte[i_len];
            p_in.read(ab_dynamicarray);
            p_in.close();

            //#if DEBUG
            System.out.println("Dynamic images loading...ok [" + i_len + "]");
            //#endif
        }
        p_in = null;
        Runtime.getRuntime().gc();
    }

    public static final void init(Class _this) throws Exception
    {
        // Грузим карту изображений
        loadMapResource(_this);

        // Грузим картинки
        try
        {
            int i_flag = 1;
            for (int li = 0; li < 15; li++)
            {
                Runtime.getRuntime().gc();
                if ((i_groups & i_flag) != 0)
                {
                    ap_Images[li] = Image.createImage(FULLIMAGE + li + ".png");
                    //#if DEBUG
                    System.out.println("Big image " + li + " loading...ok");
                    //#endif
                }
                else
                {
                    ap_Images[li] = null;
                }
            }
        }
        catch (Exception e)
        {
            //#if DEBUG
            System.out.println("Big image loading...ERROR [" + e.getMessage() + "]");
            //#endif
        }
        Runtime.getRuntime().gc();
        // Грузим динамический массив
        InputStream p_in = _this.getResourceAsStream(DYNIMAGES);
        if (p_in != null)
        {
            //#if DEBUG
            System.out.println("Dynamic images loading...");
            //#endif

            int i_len = p_in.read();
            i_len = i_len | (p_in.read() << 8);
            Runtime.getRuntime().gc();
            ab_dynamicarray = new byte[i_len];
            p_in.read(ab_dynamicarray);
            p_in.close();

            //#if DEBUG
            System.out.println("Dynamic images loading...ok [" + i_len + "]");
            //#endif
        }
        p_in = null;
        Runtime.getRuntime().gc();

        Runtime.getRuntime().gc();
    }
}

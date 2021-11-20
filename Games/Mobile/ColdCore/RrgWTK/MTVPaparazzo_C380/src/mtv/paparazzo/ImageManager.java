package mtv.paparazzo;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;
import java.io.IOException;
import java.io.InputStream;

public class ImageManager
{
    private static Image p_fullImage;
    private static byte [] ab_dynamicarray;
    private static short[] ash_imageInfo;

    private static final String FULLIMAGE = "/fimg.png";
    private static final String IMAGEMAPINFO = "/map.bin";
    private static final String DYNIMAGES = "/dyn.bin";

    public static final int IMAGEINFO_LENGTH = 7;

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
        p_fullImage = null;
        Runtime.getRuntime().gc();
    }

    protected static final void drawImage(int _imageOffset, Graphics _g, int _x, int _y)
    {
        short [] ash_map = ash_imageInfo;

        int i_type = ash_map[_imageOffset++];
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

        switch(i_type)
        {
            case FLAG_EXTERNAL : {
                int i_index = i_xMap;
                String s_name = Integer.toString(i_index);
                Image p_img = null;
                try
                {
                    p_img = Image.createImage(s_name);
                    _g.drawImage(p_img, _x, _y, 0);
                }
                catch (IOException e)
                {
                }
                p_img = null;
            };break;
            case FLAG_DYNAMIC : {
                int i_offset = i_xMap & 0xFFFF;
                int i_len = i_yMap & 0xFFFF;

                Image p_img = Image.createImage(ab_dynamicarray,i_offset,i_len);
                _g.drawImage(p_img, _x, _y, 0);
                p_img = null;
            };break;
            case FLAG_NORMAL : {
                _g.setClip(_x, _y, i_cW, i_cH);
                _g.drawImage(p_fullImage, _x - i_xMap, _y - i_yMap, 0);
            };break;
        }
    }

    public static final void init(Class _this) throws IOException
    {
        // Грузим большую картинку
        try
        {
            p_fullImage = Image.createImage(FULLIMAGE);
            //#if DEBUG
            System.out.println("Big image loading...ok");
            //#endif
        }
        catch (IOException e)
        {
            p_fullImage = null;
            //#if DEBUG
            System.out.println("Big image loading...ERROR ["+e.getMessage()+"]");
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
            i_len = i_len | (p_in.read()<<8);
            ab_dynamicarray = new byte[i_len];
            p_in.read(ab_dynamicarray);
            p_in.close();

            //#if DEBUG
            System.out.println("Dynamic images loading...ok ["+i_len+"]");
            //#endif
        }
        p_in = null;
        Runtime.getRuntime().gc();

        // Грузим карту изображений
        p_in = _this.getResourceAsStream(IMAGEMAPINFO);
        if (p_in != null)
        {
            //#if DEBUG
            System.out.println("Image map array loading...");
            //#endif

            int i_imagesNum = (short)((p_in.read()<<8)|(p_in.read()));

            int i_len = i_imagesNum * IMAGEINFO_LENGTH;
            short[] ash_array = new short[i_len];

            //#if DEBUG
            System.out.println(i_imagesNum+" images");
            //#endif

             for (int li = 0; li < i_len;)
             {
                 // Тип хранения
                 ash_array[li++] = (short)(p_in.read());
                 // Координата X на картинке
                 ash_array[li++] = (short)((p_in.read()<<8)|(p_in.read()));
                 // Координата Y на картинке
                 ash_array[li++] = (short)((p_in.read()<<8)|(p_in.read()));
                 // Смещение X
                 ash_array[li++] = (short)((p_in.read()<<8)|(p_in.read()));
                 // Смещение Y
                 ash_array[li++] = (short)((p_in.read()<<8)|(p_in.read()));
                 // Ширина области
                 ash_array[li++] = (short)((p_in.read()<<8)|(p_in.read()));
                 // Высота области
                 ash_array[li++] = (short)((p_in.read()<<8)|(p_in.read()));
             }
            p_in.close();
            ash_imageInfo = ash_array;

            //#if DEBUG
            System.out.println("Image map array loading...ok");
            //#endif
        }
        p_in = null;
        Runtime.getRuntime().gc();
    }
}

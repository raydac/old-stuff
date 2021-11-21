import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;
import java.io.InputStream;
import java.io.DataInputStream;

/**
 * Класс управляет выводом, загрузкой и хранением изображений
 * @author Igor Maznitsa
 * @version 2.99 (30 jan 2005)
 */
public class ImageManager
{
    //#local WILLBEMODIFIED = false || IMAGES_FLIPPEDLINK

    // Клипкомпенсация на телефонах, неправильно выводящих изображение при выходе зоны за пределы экрана

    //#if VENDOR=="MOTOROLA" ||  VENDOR=="LG" || VENDOR=="SAMSUNG" || VENDOR=="NOKIA"
        //#local CLIPCOMPENSATION=false
    //#endif

    //#if VENDOR=="SIEMENS"
        //#if MODEL=="M50" || MODEL=="M55" || MODEL=="S55"
            //#local CLIPCOMPENSATION=true
        //#else
            //#local CLIPCOMPENSATION=false
        //#endif
    //#endif

    //#local IMAGE_TYPES = 0
    //#if IMAGES_NORMAL
    //#local IMAGE_TYPES = IMAGE_TYPES + 1
    //#endif
    //#if IMAGES_DYNAMIC
    //#local IMAGE_TYPES = IMAGE_TYPES + 1
    //#endif
    //#if IMAGES_EXTERNAL
    //#local IMAGE_TYPES = IMAGE_TYPES + 1
    //#endif

    //#_if (VENDOR=="SAMSUNG" && MODEL=="X100") || (VENDOR=="SE" && MODEL=="T610") || (VENDOR=="LG" && MODEL=="G1600")
    //#global CHECK_MEMORY = false
    //#_else
    //#global CHECK_MEMORY = true
    //#_endif

    //#if IMAGES_DYNAMIC || IMAGES_EXTERNAL

    private static Image[] ap_dynamicImageCache = null;

    //#if CHECK_MEMORY
    private static int[] ai_dynamicImageCache = null;
    //#endif

    //#if CHECK_MEMORY
    private static final int OBJECT_SIZE = 800;
    private static final int TTL_IMAGEINCACHE = 100; // Длительность хранения картинки в кэше в миллимсекундах
    //#endif

    //#if VENDOR=="SIEMENS"
    //#if MODEL=="C55" || MODEL=="M55" || MODEL=="S55" || MODEL=="C60"
    //$private static final int BPP = 0; // Байт на пиксель (1 - 2 байта, 0 - 1 байт , 2 - 4 байта) в картинке
    //#else
    //$private static final int BPP = 1; // Байт на пиксель (1 - 2 байта, 0 - 1 байт , 2 - 4 байта) в картинке
    //#endif
    //#else
    private static final int BPP = 1; // Байт на пиксель (1 - 2 байта, 0 - 1 байт , 2 - 4 байта) в картинке
    //#endif
    //#endif

    //#if IMAGES_NORMAL
    private static Image[] ap_Images;
    //#endif

    //#if IMAGES_DYNAMIC
    private static byte[] ab_dynamicarray;
    //#endif

    private static short[] ash_imageInfo;

    private static final String IMAGEMAPINFO = "/map.bin";

    //#if IMAGES_NORMAL
    private static final String FULLIMAGE = "/fimg";
    //#endif

    //#if IMAGES_DYNAMIC
    private static final String DYNIMAGES = "/dyn.bin";
    //#endif

    private static int i_imagesNum = 0;
    private static int i_groups = 0;
    private static int i_serviceFlags = 0;

    //#if IMAGES_EXT2ONE
    private static boolean lg_externalInOneFile = false;
    private static final String EXTERNAL_BIN_FILE = "/ext.bin";
    //#endif


    //#-
    public static final int IMAGEINFO_LENGTH = 7;
    //#+
    //$public static final int IMAGEINFO_LENGTH = /*$IMAGES_INFO_LENGTH$*/;

    private static Class p_This;

    /**
     * Флаг, показывающий что линк должен быть флипнут по вертикали
     */
    public static final int FLIP_VERT = 1;

    /**
     * Флаг, показывающий что линк должен быть флипнут по горизонтали
     */
    public static final int FLIP_HORZ = 2;

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

    /**
     * Макрокартинка
     */
    public static final int FLAG_MACRO = 4;

    /**
     * Все внешние файлы в одном бинарном
     */
    private static final int SERVICEFLAG_EXT2ONE = 2;

    /**
     * Файл содержит полную информацию об изображении, т.е. хранит инфо об исходном размере картинок
     */
    private static final int SERVICEFLAG_FULLIMAGEINFO = 2;

    /**
     * Указатель на графический контент, сделано для ускорения групповых операций
     */
    public static Graphics p_DestinationGraphics;

    public static final void release()
    {
        //#if IMAGES_DYNAMIC || IMAGES_EXTERNAL
        clearDynamicCahce();

        ap_dynamicImageCache = null;

        //#if CHECK_MEMORY
        ai_dynamicImageCache = null;
        //#endif

        //#endif

        ash_imageInfo = null;

        //#if IMAGES_DYNAMIC
        ab_dynamicarray = null;
        //#endif

        //#if IMAGES_NORMAL
        ap_Images = null;
        //#endif

        Runtime.getRuntime().gc();
    }

    //#if IMAGES_DYNAMIC || IMAGES_EXTERNAL
    private static final void clearTTLImageCache()
    {
        //#if CHECK_MEMORY
        final int i_num = i_imagesNum;
        final Image[] ap_imgs = ap_dynamicImageCache;
        final int[] ai_images = ai_dynamicImageCache;

        final int i_time = (int) System.currentTimeMillis() & 0x7FFFFFFF;
        for (int li = 0; li < i_num; li++)
        {
            int i_val = ai_images[li];
            if (i_val == 0)
                ap_imgs [li] = null;
            else
            if (i_val != 0)
            {
                // Проверка просроченности
                final int i_imageTime = i_val & 0x7FFFFFFF;
                if (i_time - i_imageTime > TTL_IMAGEINCACHE)
                {
                    ai_images[li] = 0;
                    ap_imgs[li] = null;
                }
            }
        }
        Runtime.getRuntime().gc();
        //#endif
    }
    //#endif

    protected static final void drawImage(int _imageOffset, int _x, int _y)
    {
        short[] ash_map = ash_imageInfo;

        int i_type = ash_map[_imageOffset++] & 0xFFFF;

        final Graphics _g = p_DestinationGraphics;

        //#if IMAGES_NORMAL
        Image p_fullImage = null;
        int i_imageIndex = i_type >>> 4;
        //#endif

        i_type &= 0xF;

        //#if IMAGES_MACRO
        if (i_type==FLAG_MACRO)
        {
            int i_index = IMAGEINFO_LENGTH;
            while(i_index!=0)
            {
                int i_imageOffset = ash_map[_imageOffset++] & 0xFFFF;

                if (i_imageOffset == 0xFFFF) return;
                drawImage(i_imageOffset,_x,_y);
                i_index--;
            }

            return;
        }
        //#endif

        //#if IMAGES_LINK
        int i_linkXOffset=0;
        int i_linkYOffset=0;
        if (i_type == FLAG_LINK)
        {
            int i_imageOffset = ash_map[_imageOffset++] & 0xFFFF;
            i_linkXOffset = ash_map[_imageOffset++];
            i_linkYOffset = ash_map[_imageOffset++];

            //#if IMAGES_FLIPPEDLINK
            int i_flippedFlags = ash_map[_imageOffset];
            if (i_flippedFlags!=0)
            {
                boolean lg_flipVert = (i_flippedFlags & FLIP_VERT)!=0;
                boolean lg_flipHorz = (i_flippedFlags & FLIP_HORZ)!=0;
                drawImage(i_imageOffset,_x+i_linkXOffset,_y+i_linkYOffset,lg_flipVert,lg_flipHorz);
                return;
            }
            //#endif

            _imageOffset = i_imageOffset;
            i_type = ash_map[_imageOffset++] & 0xFFFF;

            //#if IMAGES_NORMAL
            i_imageIndex = i_type >>> 4;
            //#endif

            i_type &= 0xF;

            //#if IMAGES_LINK2LINK
            if (i_type == FLAG_LINK)
            {
                drawImage(i_imageOffset,_x+i_linkXOffset,_y+i_linkYOffset);
                return;
            }
            //#endif
        }
        //#endif
        //#if IMAGES_NORMAL
        p_fullImage = ap_Images[i_imageIndex];
        //#endif

        int i_xMap = ash_map[_imageOffset++];
        int i_yMap = ash_map[_imageOffset++];
        //#if IMAGES_LINK
        _x += ash_map[_imageOffset++] + i_linkXOffset;
        _y += ash_map[_imageOffset++] + i_linkYOffset;
        //#else
        //$_x += ash_map[_imageOffset++];
        //$_y += ash_map[_imageOffset++];
        //#endif
        int i_cW = ash_map[_imageOffset++];
        int i_cH = ash_map[_imageOffset];

        //#if IMAGE_TYPES>1
        switch (i_type)
        {
        //#endif
                //#if IMAGES_DYNAMIC || IMAGES_EXTERNAL
                //#if IMAGES_DYNAMIC

                //#if IMAGE_TYPES>1
                case FLAG_DYNAMIC:
                //#endif

                //#endif
                //#if IMAGES_EXTERNAL
                //#if IMAGE_TYPES>1
                case FLAG_EXTERNAL:
                //#endif
                //#endif
                {

                    final int i_indexImage = _imageOffset / IMAGEINFO_LENGTH;
                    Image p_img = null;

                    //#if CHECK_MEMORY
                    final int i_time = (int) System.currentTimeMillis() & 0x7FFFFFFF;
                    //#endif

                    //#if CHECK_MEMORY
                    if (ai_dynamicImageCache[i_indexImage] == 0)
                    //#else
                    //$if (ap_dynamicImageCache[i_indexImage] == null)
                    //#endif
                    {
                        try
                        {
                            //#if CHECK_MEMORY
                            // Проверка наличия памяти
                            int i_neededMemory = ((i_cW * i_cH) << BPP) + OBJECT_SIZE;
                            if (Runtime.getRuntime().freeMemory() <= i_neededMemory)
                            {
                                // Пробуем гарбадж коллектор
                                Runtime.getRuntime().gc();
                                if (Runtime.getRuntime().freeMemory() <= i_neededMemory)
                                {
                                    // Пытаемся освободить место в памяти, освобождая кэш
                                    clearTTLImageCache();
                                    if (Runtime.getRuntime().freeMemory() <= i_neededMemory)
                                    {
                                        // Если нет памяти то выходим
                                        return;
                                    }
                                }
                            }
                            //#endif

                                //#if IMAGES_EXTERNAL
                                //#if IMAGE_TYPES>1
                                if (i_type == FLAG_EXTERNAL)
                                {
                                //#endif
                                    //#if SHOWSYS
                                    System.out.println("Loading external image "+_imageOffset);
                                    //#endif

                                    //#if IMAGES_EXT2ONE
                                    //#-
                                    if (lg_externalInOneFile)
                                    {
                                    //#+
                                        int i_offset = (((i_xMap & 0xFFFF)<<16)|(i_yMap & 0xFFFF))+2;
                                        InputStream p_inStr = p_This.getResourceAsStream(EXTERNAL_BIN_FILE);
                                        p_inStr.skip(i_offset);
                                        int i_size = p_inStr.read()&0xFF;
                                        i_size = (i_size<<8) | (p_inStr.read()&0xFF);
                                        byte [] ab_byte = new byte[i_size];
                                        p_inStr.read(ab_byte);
                                        p_inStr.close();
                                        p_inStr = null;
                                        p_img = Image.createImage(ab_byte,0,i_size);
                                    //#-
                                    }
                                    else
                                    //#+
                                    //#else
                                    {
                                        final int i_index = i_xMap;
                                        StringBuffer p_name = new StringBuffer("/").append(i_index);
                                        p_img = Image.createImage(p_name.toString());
                                    }
                                    //#endif
                                    //#if IMAGE_TYPES>1
                                }
                                else
                                    //#endif
                                //#endif
                                {
                                    //#if IMAGES_DYNAMIC
                                    int i_offset = i_xMap & 0xFFFF;
                                    int i_len = i_yMap & 0xFFFF;
                                    p_img = Image.createImage(ab_dynamicarray, i_offset, i_len);
                                    //#endif
                                }

                                //#if CHECK_MEMORY
                                ai_dynamicImageCache[i_indexImage] = 0x80000000 | i_time;
                                //#endif

                            ap_dynamicImageCache[i_indexImage] = p_img;
                        }
                        catch (Exception e)
                        {
                            //#if SHOWSYS
                            String s_msg = e.getMessage();
                            if (s_msg == null) s_msg = e.getClass().getName();
                            //#endif

                            //#if CHECK_MEMORY
                            ai_dynamicImageCache[i_indexImage] = 0;
                            //#endif
                            if (p_img == null) return;
                        }
                    }
                    else
                    {
                        p_img = ap_dynamicImageCache[i_indexImage];
                        //#if CHECK_MEMORY
                        ai_dynamicImageCache[i_indexImage] = 0x80000000 | i_time;
                        //#endif
                    }

                    //#if IMAGES_NORMAL
                    //#if CLIPCOMPENSATION
                    //$int i_w = i_cW;
                    //$int i_h = i_cH;
                    //$int i_x = _x;
                    //$int i_y = _y;
                    //$if (i_x<0)
                    //${
                    //$i_x = 0;
                    //$i_w = _x + i_cW;
                    //$if (i_w<=0) return;
                    //$}
                    //$if (i_y<0)
                    //${
                    //$i_y = 0;
                    //$i_h = _y + i_cH;
                    //$if (i_h<=0) return;
                    //$}
                    //$_g.setClip(i_x, i_y, i_w, i_h);
                    //#else
                    _g.setClip(_x, _y, i_cW, i_cH);
                    //#endif
                    //#endif

                    _g.drawImage(p_img, _x, _y, 0);
                }

                //#if IMAGE_TYPES>1
        ;
                break;
                //#endif

                //#endif

                //#if IMAGES_NORMAL
                //#if IMAGE_TYPES>1
                case FLAG_NORMAL:
                //#endif
                {
                    //#if CLIPCOMPENSATION
                    //$int i_w = i_cW;
                    //$int i_h = i_cH;
                    //$int i_x = _x;
                    //$int i_y = _y;
                    //$if (i_x<0)
                    //${
                    //$i_x = 0;
                    //$i_w = _x + i_cW;
                    //$if (i_w<=0) return;
                    //$}
                    //$if (i_y<0)
                    //${
                    //$i_y = 0;
                    //$i_h = _y + i_cH;
                    //$if (i_h<=0) return;
                    //$}
                    //$_g.setClip(i_x, i_y, i_w, i_h);
                    //#else
                    _g.setClip(_x, _y, i_cW, i_cH);
                    //#endif

                    _g.drawImage(p_fullImage, _x - i_xMap, _y - i_yMap, 0);

                }
                //#if IMAGE_TYPES>1
        ;
                break;
                //#endif
                //#endif

        //#if IMAGE_TYPES>1
        }
        //#endif
    }

    //#if (MIDP=="2.0" || VENDOR=="NOKIA") && WILLBEMODIFIED
    protected static final void drawImage(int _imageOffset, int _x, int _y, boolean _flipVert, boolean _flipHorz)
    {
        final int i_origImageOffset = _imageOffset;

        final Graphics _g = p_DestinationGraphics;
        final short[] ash_map = ash_imageInfo;
        int i_type = ash_map[_imageOffset++] & 0xFFFF;

        //#if IMAGES_MACRO
        if (i_type==FLAG_MACRO)
        {
            int i_index = IMAGEINFO_LENGTH;
            while(i_index!=0)
            {
                int i_imageOffset = ash_map[_imageOffset++] & 0xFFFF;
                if (i_imageOffset == 0xFFFF) return;
                drawImage(i_imageOffset,_x,_y,_flipVert,_flipHorz);
                i_index--;
            }

            return;
        }
        //#endif


        //#if IMAGES_NORMAL
        Image p_fullImage = null;
        int i_imageIndex = i_type >>> 4;
        //#endif

        i_type &= 0xF;

        //#if IMAGES_LINK
        int i_linkXOffset = 0;
        int i_linkYOffset = 0;
        if (i_type == FLAG_LINK)
        {
            int i_imageOffset = ash_map[_imageOffset++] & 0xFFFF;
            i_linkXOffset = ash_map[_imageOffset++];
            i_linkYOffset = ash_map[_imageOffset++];

            //#if IMAGES_FLIPPEDLINK
            int i_flippedFlags = ash_map[_imageOffset];
            if (i_flippedFlags!=0)
            {
                boolean lg_flipVert = (i_flippedFlags & FLIP_VERT)!=0;
                boolean lg_flipHorz = (i_flippedFlags & FLIP_HORZ)!=0;
                drawImage(i_imageOffset,_x+i_linkXOffset,_y+i_linkYOffset,lg_flipVert,lg_flipHorz);
                return;
            }
            //#endif

            _imageOffset = i_imageOffset;
            i_type = ash_map[_imageOffset++] & 0xFFFF;

            //#if IMAGES_NORMAL
            i_imageIndex = i_type >>> 4;
            //#endif

            i_type &= 0xF;

            //#if IMAGES_LINK2LINK
            if (i_type == FLAG_LINK)
            {
                drawImage(i_imageOffset,_x+i_linkXOffset,_y+i_linkYOffset,_flipVert,_flipHorz);
                return;
            }
            //#endif
        }
        //#endif
        //#if IMAGES_NORMAL
        p_fullImage = ap_Images[i_imageIndex];
        //#endif

        int i_xMap = ash_map[_imageOffset++];
        int i_yMap = ash_map[_imageOffset++];

        int i_xOff = ash_map[_imageOffset++];
        int i_yOff = ash_map[_imageOffset++];

        int i_cW = ash_map[_imageOffset++];
        int i_cH = ash_map[_imageOffset++];

        int i_fW = ash_map[_imageOffset++];
        int i_fH = ash_map[_imageOffset];

        //#if (VENDOR=="NOKIA" && MIDP=="1.0")
        //$int i_flag = 0;
        //#else
        //#if MIDP=="2.0"
        int i_flag = javax.microedition.lcdui.game.Sprite.TRANS_NONE;
        //#endif
        //#endif

        if (_flipHorz)
        {
            //#if (VENDOR=="NOKIA" && MIDP=="1.0")
            //$i_flag = com.nokia.mid.ui.DirectGraphics.FLIP_HORIZONTAL;
            //#else
            //#if MIDP=="2.0"
            i_flag = javax.microedition.lcdui.game.Sprite.TRANS_MIRROR;
            //#endif
            //#endif

            int i_delta = i_fW - (i_xOff + i_cW);
            _x += i_delta;
            _y += i_yOff;

            //#if MIDP=="1.0" && VENDOR=="NOKIA"
                //#if IMAGES_NORMAL
                    //#if IMAGES_NORMAL && IMAGE_TYPES>1
                      //$if (i_type == FLAG_NORMAL)
                    //#endif
                        //$ i_xMap = p_fullImage.getWidth() - (i_xMap+i_cW);
                //#endif
            //#endif
        }

        if (_flipVert)
        {
            //#if (VENDOR=="NOKIA" && MIDP=="1.0")
            //$i_flag |= com.nokia.mid.ui.DirectGraphics.FLIP_VERTICAL;
            //#else
            //#if MIDP=="2.0"
            i_flag |= javax.microedition.lcdui.game.Sprite.TRANS_MIRROR_ROT180;
            //#endif
            //#endif

            int i_delta = i_fH - (i_yOff + i_cH);
            _x += i_xOff;
            _y += i_delta;

            //#if (VENDOR=="NOKIA" && MIDP=="1.0")
                //#if IMAGES_NORMAL
                    //#if IMAGES_NORMAL && IMAGE_TYPES>1
                       //$ if (i_type == FLAG_NORMAL)
                    //#endif
                        //$ i_yMap = p_fullImage.getHeight() - (i_yMap+i_cH);
                //#endif
            //#endif
        }

        if (!(_flipVert || _flipHorz))
        {
            _x += i_xOff;
            _y += i_yOff;
        }

        //#if IMAGES_LINK
        _x += i_linkXOffset;
        _y += i_linkYOffset;
        //#endif

        //#if IMAGE_TYPES>1
        switch (i_type)
        {
        //#endif
                //#if IMAGES_DYNAMIC || IMAGES_EXTERNAL
                //#if IMAGES_DYNAMIC
                //#if IMAGE_TYPES>1
                case FLAG_DYNAMIC:
                //#endif
                //#endif
                //#if IMAGES_EXTERNAL
                //#if IMAGE_TYPES>1
                case FLAG_EXTERNAL:
                //#endif
                //#endif
                {
                    final int i_indexImage = _imageOffset / IMAGEINFO_LENGTH;
                    Image p_img = null;
                    //#if CHECK_MEMORY
                    final int i_time = (int) System.currentTimeMillis() & 0x7FFFFFFF;
                    //#endif

                    //#if CHECK_MEMORY
                    if (ai_dynamicImageCache[i_indexImage] == 0)
                    //#else
                    //$if (ap_dynamicImageCache[i_indexImage] == null)
                    //#endif
                    {
                        try
                        {
                            //#if CHECK_MEMORY
                            // Проверка наличия памяти
                            int i_neededMemory = i_cW * i_cH * BPP + OBJECT_SIZE;
                            if (Runtime.getRuntime().freeMemory() <= i_neededMemory)
                            {
                                // Пробуем гарбадж коллектор
                                Runtime.getRuntime().gc();
                                if (Runtime.getRuntime().freeMemory() <= i_neededMemory)
                                {
                                    // Пытаемся освободить место в памяти, освобождая кэш
                                    clearTTLImageCache();
                                    if (Runtime.getRuntime().freeMemory() <= i_neededMemory)
                                    {
                                        // Если нет памяти то выходим
                                        return;
                                    }
                                }
                            }
                            //#endif

                                //#if IMAGES_EXTERNAL
                                //#if IMAGE_TYPES>1
                                if (i_type == FLAG_EXTERNAL)
                                //#endif
                                {
                                    //#if SHOWSYS
                                    System.out.println("Load ext. image "+_imageOffset);
                                    //#endif

                                    //#if IMAGES_EXT2ONE
                                    //#-
                                    if (lg_externalInOneFile)
                                    {
                                    //#+
                                        int i_offset = (((i_xMap & 0xFFFF)<<16)|i_yMap)+2;
                                        InputStream p_inStr = p_This.getResourceAsStream(EXTERNAL_BIN_FILE);
                                        p_inStr.skip(i_offset);
                                        int i_size = p_inStr.read()&0xFF;
                                        i_size = (i_size<<8) | (p_inStr.read()&0xFF);
                                        byte [] ab_byte = new byte[i_size];
                                        p_inStr.read(ab_byte);
                                        p_inStr.close();
                                        p_inStr = null;
                                        p_img = Image.createImage(ab_byte,0,i_size);
                                    //#-
                                    }
                                    else
                                    //#+
                                    //#else
                                    {
                                        final int i_index = i_xMap;
                                        StringBuffer p_name = new StringBuffer("/").append(i_index);
                                        p_img = Image.createImage(p_name.toString());
                                    }
                                    //#endif
                                //#if IMAGE_TYPES>1
                                }
                                else
                                //#endif
                                //#endif
                                {
                                    //#if IMAGES_DYNAMIC
                                    int i_offset = i_xMap & 0xFFFF;
                                    int i_len = i_yMap & 0xFFFF;
                                    p_img = Image.createImage(ab_dynamicarray, i_offset, i_len);
                                    //#endif
                                }

                                //#if CHECK_MEMORY
                                ai_dynamicImageCache[i_indexImage] = 0x80000000 | i_time;
                                //#endif

                            ap_dynamicImageCache[i_indexImage] = p_img;
                        }
                        catch (Exception e)
                        {
                            //#if SHOWSYS
                            String s_msg = e.getMessage();
                            if (s_msg == null) s_msg = e.getClass().getName();
                            //#endif

                            //#if CHECK_MEMORY
                            ai_dynamicImageCache[i_indexImage] = 0;
                            //#endif
                            if (p_img == null) return;
                        }
                    }
                    else
                    {
                        p_img = ap_dynamicImageCache[i_indexImage];
                        //#if CHECK_MEMORY
                        ai_dynamicImageCache[i_indexImage] = 0x80000000 | i_time;
                        //#endif
                    }

                    //#if IMAGES_NORMAL
                    //#if CLIPCOMPENSATION
                    //$int i_w = i_cW;
                    //$int i_h = i_cH;
                    //$int i_x = _x;
                    //$int i_y = _y;
                    //$if (i_x<0)
                    //${
                    //$i_x = 0;
                    //$i_w = _x + i_cW;
                    //$if (i_w<=0) return;
                    //$}
                    //$if (i_y<0)
                    //${
                    //$i_y = 0;
                    //$i_h = _y + i_cH;
                    //$if (i_h<=0) return;
                    //$}
                    //$_g.setClip(i_x, i_y, i_w, i_h);
                    //#else
                    _g.setClip(_x, _y, i_cW, i_cH);
                    //#endif
                    //#endif

                    //#if (VENDOR=="NOKIA" && MIDP=="1.0")
                    //$com.nokia.mid.ui.DirectUtils.getDirectGraphics(_g).drawImage(p_img, _x,_y, 0, i_flag);
                    //#else
                    _g.drawRegion(p_img, 0, 0, i_cW, i_cH, i_flag, _x, _y, 0);
                    //#endif
                }
                //#if IMAGE_TYPES>1
        ;
                break;
                //#endif
                //#endif
                //#if IMAGES_NORMAL
                //#if IMAGE_TYPES>1
                case FLAG_NORMAL:
                //#endif
                {
                    //#if CLIPCOMPENSATION
                    //$int i_w = i_cW;
                    //$int i_h = i_cH;
                    //$int i_x = _x;
                    //$int i_y = _y;
                    //$if (i_x<0)
                    //${
                    //$i_x = 0;
                    //$i_w = _x + i_cW;
                    //$if (i_w<=0) return;
                    //$}
                    //$if (i_y<0)
                    //${
                    //$i_y = 0;
                    //$i_h = _y + i_cH;
                    //$if (i_h<=0) return;
                    //$}
                    //$_g.setClip(i_x, i_y, i_w, i_h);
                    //#else
                    _g.setClip(_x, _y, i_cW, i_cH);

                    //#if VENDOR=="NOKIA" && MODEL=="7650" && MIDP=="1.0"
                    //$_g.clipRect(_x, _y, i_cW, i_cH);
                    //#endif

                    //#endif

                    //#if (VENDOR=="NOKIA" && MIDP=="1.0")
                    //$com.nokia.mid.ui.DirectUtils.getDirectGraphics(_g).drawImage(p_fullImage, _x-i_xMap, _y-i_yMap,0,  i_flag);
                    //#else
                    _g.drawRegion(p_fullImage, i_xMap, i_yMap, i_cW, i_cH, i_flag, _x, _y, 0);
                    //#endif
                }
                //#if IMAGE_TYPES>1
        ;
                break;
                //#endif
                //#endif
        //#if IMAGE_TYPES>1
        }
        //#endif
    }
    //#endif

/*
    private static void traceImageForOffset(int _offset)
    {
        System.out.println("Trace image map array for "+_offset+"\n\r-------------");

        System.out.println("Type "+(ash_imageInfo[_offset++] & 0xFFFF));
        System.out.println("X on image "+(ash_imageInfo[_offset++] & 0xFFFF));
        System.out.println("Y on image "+(ash_imageInfo[_offset++] & 0xFFFF));
        System.out.println("OFFSETX on image "+(ash_imageInfo[_offset++] & 0xFFFF));
        System.out.println("OFFSETY on image "+(ash_imageInfo[_offset++] & 0xFFFF));
        System.out.println("WIDTH on image "+(ash_imageInfo[_offset++] & 0xFFFF));
        System.out.println("HEIGHT on image "+(ash_imageInfo[_offset++] & 0xFFFF));

        if (IMAGEINFO_LENGTH == 9)
        {
            System.out.println("ORIGWIDTH on image "+(ash_imageInfo[_offset++] & 0xFFFF));
            System.out.println("ORIGFHEIGHT on image "+(ash_imageInfo[_offset++] & 0xFFFF));
        }
    }
*/


    private static final void loadMapResource(Class _this) throws Exception
    {
        InputStream p_in = _this.getResourceAsStream(IMAGEMAPINFO);
        DataInputStream p_dis = new DataInputStream(p_in);
        if (p_in != null)
        {
            //#if DEBUG
            System.out.println("Image map array loading...");
            //#endif

            i_serviceFlags = p_dis.readUnsignedByte();
            //#if IMAGES_EXT2ONE
            lg_externalInOneFile = (i_serviceFlags & SERVICEFLAG_EXT2ONE) != 0;
            //#endif

            i_imagesNum = p_dis.readUnsignedShort();

            //#if DEBUG
            System.out.println("Summary images..." + i_imagesNum);
            //#endif
            i_groups = p_dis.readUnsignedShort();
            //#if DEBUG
            System.out.println("Groups..." + i_groups);
            //#endif

            int i_len = i_imagesNum * IMAGEINFO_LENGTH;
            short[] ash_array = new short[i_len];
            Runtime.getRuntime().gc();

            //#if DEBUG
            System.out.println("Array length " + i_len);
            //#endif

            int i_offset = 0;
            for (int li = 0; li < i_imagesNum; li++)
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

                if (IMAGEINFO_LENGTH == 9)
                {
                    // Полная ширина области
                    ash_array[i_offset++] = p_dis.readShort();
                    // Полная высота области
                    ash_array[i_offset++] = p_dis.readShort();
                }
            }
            p_dis.close();

            ash_imageInfo = ash_array;

            //#if IMAGES_DYNAMIC || IMAGES_EXTERNAL
            ap_dynamicImageCache = new Image[i_imagesNum];

            //#if CHECK_MEMORY
            ai_dynamicImageCache = new int[i_imagesNum];
            //#endif
            //#endif

            //#if DEBUG
            System.out.println("Image map array loading...ok");
            //#endif
        }
        //#if DEBUG
        else
        {
            //#if DEBUG
            System.out.println("I can't find image map resource");
            //#endif
        }
        //#endif
        p_in = null;
        p_dis = null;
        Runtime.getRuntime().gc();
    }

    public static final void clearDynamicCahce()
    {
        //#if IMAGES_DYNAMIC || IMAGES_EXTERNAL

        //#if SHOWSYS
        System.out.println("Clear dynamic image cache");
        //#endif

        for (int li = 0; li < i_imagesNum; li++)
        {
            //#if CHECK_MEMORY
            ai_dynamicImageCache[li] = 0;
            //#endif
            ap_dynamicImageCache[li] = null;
        }
        Runtime.getRuntime().gc();
        //#endif
    }

    public static final void init(Class _this, Image[] _images, boolean _loadExternal) throws Exception
    {
        p_This = _this;
        // Грузим карту изображений
        loadMapResource(_this);

        //#if IMAGES_NORMAL
        // Грузим большую картинку
        ap_Images = _images;
        Runtime.getRuntime().gc();
        //#endif

        //#if IMAGES_DYNAMIC
        // Грузим динамический массив
        InputStream p_in = _this.getResourceAsStream(DYNIMAGES);
        if (p_in != null)
        {
            //#if DEBUG
            System.out.print("Dyn.images loading...");
            //#endif

            int i_len = p_in.read();
            i_len = i_len | (p_in.read() << 8);
            Runtime.getRuntime().gc();
            ab_dynamicarray = new byte[i_len];
            p_in.read(ab_dynamicarray);
            p_in.close();

            //#if DEBUG
            System.out.println("ok [" + i_len + "]");
            //#endif
        }

        //#if IMAGES_EXTERNAL
        if (_loadExternal) loadAllExternalImages(_this);
        //#endif

        p_in = null;
        Runtime.getRuntime().gc();
        //#endif
    }

    //#if IMAGES_EXTERNAL
    private static final void loadAllExternalImages(Class _class) throws Exception
    {
        //#if SHOWSYS
        System.out.println("Load all ext.images");
        //#endif

        int i_indexImage = 0;
        //#if CHECK_MEMORY
        final int i_time = (int) System.currentTimeMillis() & 0x7FFFFFFF;
        //#endif

        //#if IMAGES_EXT2ONE
        byte [] ab_externalLoadImageArray = null;
        //#-
        InputStream p_extBlockStream = null;
        if (lg_externalInOneFile)
        {
            p_extBlockStream = _class.getResourceAsStream(EXTERNAL_BIN_FILE);
        //#+
            //$InputStream p_extBlockStream = _class.getResourceAsStream(EXTERNAL_BIN_FILE);
            int i_extFileNumber = p_extBlockStream.read()<<8;
            i_extFileNumber |= p_extBlockStream.read();
            int i_maxExternalImageBlockSize = p_extBlockStream.read()<<8;
            i_maxExternalImageBlockSize |= p_extBlockStream.read();
            ab_externalLoadImageArray = new byte[i_maxExternalImageBlockSize];
            //$int i_extOffset = 0;
        //#-
        }
        //#+
        //#endif

        int i_imageNum = i_imagesNum*IMAGEINFO_LENGTH;
        for (int li = 0; li < i_imageNum; li += IMAGEINFO_LENGTH)
        {
            int i_type = ash_imageInfo[li] & 0xF;

            if (i_type == FLAG_EXTERNAL)
            {
                final int i_index = (ash_imageInfo[li + 1] & 0xFFFF);

                Image p_img = null;

                //#if IMAGES_EXT2ONE
                if (lg_externalInOneFile)
                {
                    int i_b0 = p_extBlockStream.read()<<8;
                    i_b0 |= p_extBlockStream.read();
                    i_indexImage = (i_b0);

                    i_b0 = p_extBlockStream.read()<<8;
                    i_b0 |= p_extBlockStream.read();

                    int i_len = i_b0;
                    p_extBlockStream.read(ab_externalLoadImageArray,0,i_len);
                    p_img = Image.createImage(ab_externalLoadImageArray,0,i_len);
                }
                //#else
                else
                {
                    StringBuffer p_name = new StringBuffer("/").append(i_index);
                    p_img = Image.createImage(p_name.toString());
                }
                //#endif

                //#if CHECK_MEMORY
                ai_dynamicImageCache[i_indexImage] = 0x80000000 | i_time;
                //#endif
                ap_dynamicImageCache[i_indexImage] = p_img;
            }
            i_indexImage++;
        }

        //#if IMAGES_EXT2ONE
        ab_externalLoadImageArray = null;
        //#endif

        if (p_extBlockStream!=null) p_extBlockStream.close();
    }
    //#endif

    public static final void init(Class _this, boolean _loadExternal) throws Exception
    {
        p_This = _this;

        // Грузим карту изображений
        loadMapResource(_this);

        // Грузим картинки
        //#if IMAGES_NORMAL
        try
        {
            int i_flag = 1;
            ap_Images = new Image[16];
            for (int li = 0; li < 15; li++)
            {
                Runtime.getRuntime().gc();
                if ((i_groups & i_flag) != 0)
                {
                    //#if SHOWSYS
                    System.out.println("Big image " + li + " loading...");
                    //#endif
                    ap_Images[li] = Image.createImage(FULLIMAGE + li + ".png");
                    //#if SHOWSYS
                    if (ap_Images[li] != null) System.out.println("Big image " + li + "...loaded (" + ap_Images[li].getWidth() + 'x' + ap_Images[li].getHeight()+')');
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
        //#endif

        //#if IMAGES_DYNAMIC
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
        //#endif

        //#if IMAGES_EXTERNAL
        if (_loadExternal) loadAllExternalImages(_this);
        //#endif

        Runtime.getRuntime().gc();
    }
}

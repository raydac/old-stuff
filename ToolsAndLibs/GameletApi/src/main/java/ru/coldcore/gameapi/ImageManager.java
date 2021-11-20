package ru.coldcore.gameapi;

import java.awt.Graphics;
import java.awt.Image;
import java.io.DataInputStream;
import java.io.InputStream;

/**
 * Менеджер картинок
 * @author Igor Maznitsa
 * @version 2.00
 */
public class ImageManager
{
    /**
     * Флаг использования проверки объема доступной памяти
     */
    private static final boolean CHECK_MEMORY = false;

    /**
     * Размер хранения в памяти одного пикселя
     * 0 - 1 байт, 1 - 2 байта, 2 - 4 байта
     */
    private static final int BPP = 2;
    
    /**
     * Длительность валидности картинки в кэше в миллисекундах
     */
    private static final int TTL_IMAGEINCACHE = 1000; 

    /**
     * Приблизительный размер данных в байтах одного объекта 
     */
    private static final int OBJECT_SIZE = 800;
    
    /**
     * Кэш динамических изображений
     */
    private static Image[] ap_dynamicImageCache = null;

    /**
     * Флаги кэша динамических изображений
     */
    private static int[] ai_dynamicImageCache = null;

    /**
     * Массив хранения данных динамически распакуемых картинок
     */
    private static byte[] ab_dynamicarray;
    
    /**
     * Массив хранения данных смещения и обрезки изображений
     */
    private static int[] ai_imageInfo;

    /**
     * Константа, показывающая имя файла, содержащего данные картинок
     */
    private static final String IMAGEMAPINFO = "map.bin";
    
    /**
     * Константа, показывающая имя файла, содержащего динамические изображения
     */
    private static final String DYNIMAGES = "dyn.bin";

    /**
     * Константа, показывающая имя файла, содержащего пакет внешне-загружаемых изображений
     */
    private static final String EXTERNAL_BIN_FILE = "ext.bin";

    /**
     * Переменная показывающая количество изображений доступных менеджеру
     */
    private static int i_imagesNum = 0;
    
    /**
     * Переменная содержащая сервисные флаги (служебные)
     */
    private static int i_serviceFlags = 0;

    /**
     * Переменная содержащая флаг, показывающий, что все внешние изображения упакованы в один файл
     */
    private static boolean lg_allExtInOneFile;

    /**
     * Константа, показывающая сколько ячеек в массиве данных, отводится на каждое изображение
     */
    public static final int IMAGEINFO_LENGTH = 7;

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
     * Указатель на графический контент, сделано для ускорения групповых операций
     */
    public static Graphics p_DestinationGraphics;

    /**
     * Деинициализация менеджера изображений 
     */
    public static final void release()
    {
        clearDynamicCahce();

        ap_dynamicImageCache = null;
        ai_dynamicImageCache = null;
        ai_imageInfo = null;
        ab_dynamicarray = null;

        Runtime.getRuntime().gc();
    }

    /**
     * Очистка кэша. Удаляются изображения, которые "устарели"
     */
    private static final void clearTTLImageCache()
    {
        final int i_num = i_imagesNum;
        final Image[] ap_imgs = ap_dynamicImageCache;
        final int[] ai_images = ai_dynamicImageCache;

        final int i_time = (int) System.currentTimeMillis() & 0x7FFFFFFF;
        for (int li = 0; li < i_num; li++)
        {
            int i_val = ai_images[li];
            if (i_val == 0)
                ap_imgs[li] = null;
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
    }

    /**
     * Отрисовка изображения в заданных координатах
     * @param _imageOffset смещение данных изображения в массиве
     * @param _x координата X верхнего левого края
     * @param _y координата Y верхнего левого края
     * @throws Throwable порождается в случае ошибки работы процедуры
     */
    public static final void drawImage(int _imageOffset, int _x, int _y) throws Throwable
    {
        int[] ai_map = ai_imageInfo;
        int i_type = ai_map[_imageOffset++];

        final Graphics _g = p_DestinationGraphics;

        i_type &= 0xF;

        if (i_type == FLAG_MACRO)
        {
            int i_index = IMAGEINFO_LENGTH;
            while (i_index != 0)
            {
                int i_imageOffset = ai_map[_imageOffset++];

                if (i_imageOffset == 0xFFFF) return;
                drawImage(i_imageOffset, _x, _y);
                i_index--;
            }

            return;
        }

        int i_linkXOffset = 0;
        int i_linkYOffset = 0;
        if (i_type == FLAG_LINK)
        {
            int i_imageOffset = ai_map[_imageOffset++];
            i_linkXOffset = ai_map[_imageOffset++];
            i_linkYOffset = ai_map[_imageOffset++];

            _imageOffset = i_imageOffset;
            i_type = ai_map[_imageOffset++];

            i_type &= 0xF;

            if (i_type == FLAG_LINK)
            {
                drawImage(i_imageOffset, _x + i_linkXOffset, _y + i_linkYOffset);
                return;
            }
        }

        int i_xMap = ai_map[_imageOffset++];
        int i_yMap = ai_map[_imageOffset++];

        _x += ai_map[_imageOffset++] + i_linkXOffset;
        _y += ai_map[_imageOffset++] + i_linkYOffset;

        int i_cW = ai_map[_imageOffset++];
        int i_cH = ai_map[_imageOffset];

        final int i_indexImage = _imageOffset / IMAGEINFO_LENGTH;
        Image p_img = null;

        int i_time = 0;
        
        if (CHECK_MEMORY)
        {
            i_time = (int) System.currentTimeMillis() & 0x7FFFFFFF;
        }

        if (ai_dynamicImageCache[i_indexImage] == 0)
        {
            try
            {
                if (CHECK_MEMORY)
                {
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
                }

                if (i_type == FLAG_EXTERNAL)
                {
                    if (lg_allExtInOneFile)
                    {
                        int i_offset = (((i_xMap & 0xFFFF) << 16) | (i_yMap & 0xFFFF)) + 2;
                        InputStream p_inStr = Utils.getResourceAsStream(EXTERNAL_BIN_FILE);
                        p_inStr.skip(i_offset);
                        int i_size = p_inStr.read() & 0xFF;
                        i_size = (i_size << 8) | (p_inStr.read() & 0xFF);
                        byte[] ab_byte = new byte[i_size];
                        p_inStr.read(ab_byte);
                        p_inStr.close();
                        p_inStr = null;
                        p_img = Utils.createImageFromArray(ab_byte, 0, i_size);
                    }
                    else
                    {
                        final int i_index = i_xMap;
                        StringBuffer p_name = new StringBuffer("/").append(i_index);
                        p_img = Utils.loadImageFromResource(p_name.toString());
                    }
                }
                else
                {
                    int i_offset = i_xMap & 0xFFFF;
                    int i_len = i_yMap & 0xFFFF;
                    p_img = Utils.createImageFromArray(ab_dynamicarray, i_offset, i_len);
                }

                if (CHECK_MEMORY)
                    ai_dynamicImageCache[i_indexImage] = 0x80000000 | i_time;
                else
                    ai_dynamicImageCache[i_indexImage] = 0x80000000;

                ap_dynamicImageCache[i_indexImage] = p_img;
            }
            catch (Exception e)
            {
                String s_msg = e.getMessage();
                if (s_msg == null) s_msg = e.getClass().getName();

                ai_dynamicImageCache[i_indexImage] = 0;
                if (p_img == null) return;
            }
        }
        else
        {
            p_img = ap_dynamicImageCache[i_indexImage];
            
            if (CHECK_MEMORY)
                ai_dynamicImageCache[i_indexImage] = 0x80000000 | i_time;
            else
                ai_dynamicImageCache[i_indexImage] = 0x80000000;
        }
        _g.drawImage(p_img, _x, _y, null);
    }

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

    /**
     * Загрузка карты изображений
     */
    private static final void loadMapResource() throws Throwable
    {
        DataInputStream p_dis = new DataInputStream(Utils.getResourceAsStream(IMAGEMAPINFO));

        i_serviceFlags = p_dis.readUnsignedByte();

        lg_allExtInOneFile = (i_serviceFlags & SERVICEFLAG_EXT2ONE) != 0;

        i_imagesNum = p_dis.readUnsignedShort();

        p_dis.skip(2);

        int i_len = i_imagesNum * IMAGEINFO_LENGTH;
        int[] ai_array = new int[i_len];
        Runtime.getRuntime().gc();

        int i_offset = 0;
        for (int li = 0; li < i_imagesNum; li++)
        {
            // Тип хранения
            ai_array[i_offset++] = (short) p_dis.readUnsignedByte();
            // Координата X на картинке
            ai_array[i_offset++] = p_dis.readShort();
            // Координата Y на картинке
            ai_array[i_offset++] = p_dis.readShort();
            // Смещение X
            ai_array[i_offset++] = p_dis.readShort();
            // Смещение Y
            ai_array[i_offset++] = p_dis.readShort();
            // Ширина области
            ai_array[i_offset++] = p_dis.readUnsignedShort();
            // Высота области
            ai_array[i_offset++] = p_dis.readUnsignedShort();
        }
        Utils.closeStream(p_dis);
        p_dis = null;

        ai_imageInfo = ai_array;

        ap_dynamicImageCache = new Image[i_imagesNum];
        ai_dynamicImageCache = new int[i_imagesNum];

        Runtime.getRuntime().gc();
    }

    public static final void clearDynamicCahce()
    {
        int i_index = i_imagesNum - 1;
        final int[] ai_dynImg = ai_dynamicImageCache;
        final Image[] ap_dynImg = ap_dynamicImageCache;
        while (i_index >= 0)
        {
            ai_dynImg[i_index] = 0;
            ap_dynImg[i_index] = null;
            i_index--;
        }
        Runtime.getRuntime().gc();
    }

    /**
     * Инициализация менеджера на базе массива загруженных изображений
     * @param _images массив редзагруженных изображений
     * @param _loadExternal флаг, показывающий что требуется загрузить все изображения
     * @throws Throwable порождаетсяв случае проблем работы процедуры
     */
    public static final void init(Image[] _images, boolean _loadExternal) throws Throwable
    {
        // Грузим карту изображений
        loadMapResource();

        // Грузим динамический массив
        InputStream p_in = null;
        try
        {
            p_in = Utils.getResourceAsStream(DYNIMAGES);
            int i_len = p_in.read();
            i_len = i_len | (p_in.read() << 8);
            Runtime.getRuntime().gc();
            ab_dynamicarray = new byte[i_len];
            p_in.read(ab_dynamicarray);
        }
        catch(Throwable _thr)
        {
            ab_dynamicarray = null;
        }
        finally
        {
            Utils.closeStream(p_in);
            p_in = null;
        }

        if (_loadExternal) loadAllExternalImages();
        Runtime.getRuntime().gc();
    }

    /**
     * Процедура загрузки всех внешних изображений
     * @throws Throwable порождается если была проблема загрузки
     */
    private static final void loadAllExternalImages() throws Throwable
    {
        int i_indexImage = 0;
        final int i_time = (int) System.currentTimeMillis() & 0x7FFFFFFF;

        byte[] ab_externalLoadImageArray = null;
        InputStream p_extBlockStream = null;
        if (lg_allExtInOneFile)
        {
            p_extBlockStream = Utils.getResourceAsStream(EXTERNAL_BIN_FILE);
            int i_extFileNumber = p_extBlockStream.read() << 8;
            i_extFileNumber |= p_extBlockStream.read();
            int i_maxExternalImageBlockSize = p_extBlockStream.read() << 8;
            i_maxExternalImageBlockSize |= p_extBlockStream.read();
            ab_externalLoadImageArray = new byte[i_maxExternalImageBlockSize];
        }

        int i_imageNum = i_imagesNum * IMAGEINFO_LENGTH;
        for (int li = 0; li < i_imageNum; li += IMAGEINFO_LENGTH)
        {
            int i_type = ai_imageInfo[li] & 0xF;

            if (i_type == FLAG_EXTERNAL)
            {
                final int i_index = (ai_imageInfo[li + 1] & 0xFFFF);

                Image p_img = null;

                if (lg_allExtInOneFile)
                {
                    int i_b0 = p_extBlockStream.read() << 8;
                    i_b0 |= p_extBlockStream.read();
                    i_indexImage = (i_b0);

                    i_b0 = p_extBlockStream.read() << 8;
                    i_b0 |= p_extBlockStream.read();

                    int i_len = i_b0;
                    p_extBlockStream.read(ab_externalLoadImageArray, 0, i_len);
                    p_img = Utils.createImageFromArray(ab_externalLoadImageArray, 0, i_len);
                }
                else
                {
                    p_img = Utils.loadImageFromResource(Integer.toString(i_index));
                }

                ai_dynamicImageCache[i_indexImage] = 0x80000000 | i_time;
                ap_dynamicImageCache[i_indexImage] = p_img;
            }
            i_indexImage++;
        }

        ab_externalLoadImageArray = null;

        Utils.closeStream(p_extBlockStream);
    }

    /**
     * Процедура инициализации менеджера и загрузки ресурсов
     * @param _loadExternal флаг, показывающий что надо загрузить все внешние изображения (если true)
     * @throws Throwable порождается если происходит ошибка в процессе работы
     */
    public static final void init(boolean _loadExternal) throws Throwable
    {
        // Грузим карту изображений
        loadMapResource();

        // Грузим картинки
        InputStream p_in = null;
        try
        {
            p_in = Utils.getResourceAsStream(DYNIMAGES);
        }
        catch(Throwable _thr)
        {
            p_in = null;
        }
        if (p_in != null)
        {
            int i_len = p_in.read();
            i_len = i_len | (p_in.read() << 8);
            Runtime.getRuntime().gc();
            ab_dynamicarray = new byte[i_len];
            p_in.read(ab_dynamicarray);
        }
        Utils.closeStream(p_in);
        p_in = null;

        if (_loadExternal) loadAllExternalImages();

        Runtime.getRuntime().gc();
    }

}

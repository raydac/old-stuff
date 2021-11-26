import java.awt.Graphics;
import java.awt.Image;
import java.io.DataInputStream;
import java.io.InputStream;

/**
 * Менеджер картинок
 * 
 * @author Igor Maznitsa
 * @version 2.04
 */
public class ImageManager
{
    /**
     * Кэш динамических изображений
     */
    private static Image[] ap_dynamicImageCache = null;

    /**
     * Массив хранения данных смещения и обрезки изображений
     */
    private static int[] ai_imageInfo;

    /**
     * Константа, показывающая имя файла, содержащего данные картинок
     */
    private static final String IMAGEMAPINFO = "gfx/map.bin";

    /**
     * Переменная показывающая количество изображений доступных менеджеру
     */
    private static int i_imagesNum = 0;

    /**
     * Переменная содержащая сервисные флаги (служебные)
     */
    private static int i_serviceFlags = 0;

    /**
     * Переменная содержащая флаг, показывающий, что все внешние изображения
     * упакованы в один файл
     */
    private static boolean lg_allExtInOneFile;

    /**
     * Константа, показывающая сколько ячеек в массиве данных, отводится на
     * каждое изображение
     */
    public static final int IMAGEINFO_LENGTH = 7;

    /**
     * Хранится во внешнем файле
     */
    public static final int FLAG_EXTERNAL = 1;

    /**
     * Все внешние файлы в одном бинарном
     */
    private static final int SERVICEFLAG_EXT2ONE = 2;

    /**
     * Указатель на графический контент, сделано для ускорения групповых
     * операций
     */
    public static Graphics p_DestinationGraphics;

    /**
     * Деинициализация менеджера изображений
     */
    public static final void release()
    {
        clearDynamicCahce();

        ap_dynamicImageCache = null;
        ai_imageInfo = null;

        System.gc();
    }

    /**
     * Отрисовка изображения в заданных координатах
     * 
     * @param _imageOffset смещение данных изображения в массиве
     * @param _x координата X верхнего левого края
     * @param _y координата Y верхнего левого края
     * @throws Throwable порождается в случае ошибки работы процедуры
     */
    public static final void drawImage(int _imageOffset, int _x, int _y) throws Throwable
    {
            final int[] ai_map = ai_imageInfo;

            int i_type = ai_map[_imageOffset++];

            final Graphics _g = p_DestinationGraphics;

            i_type &= 0xF;

            int i_linkXOffset = 0;
            int i_linkYOffset = 0;

            _imageOffset+=2;
            
            _x += ai_map[_imageOffset++] + i_linkXOffset;
            _y += ai_map[_imageOffset++] + i_linkYOffset;

            final int i_indexImage = _imageOffset / IMAGEINFO_LENGTH;
            Image p_img = null;

            p_img = ap_dynamicImageCache[i_indexImage];

            if (_g == null || p_img == null)
                System.out.println("I have found null");
            _g.drawImage(p_img, _x, _y, null);
    }

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

        System.out.println("Image map table length = " + ai_imageInfo.length);

        Runtime.getRuntime().gc();
    }

    public static final void clearDynamicCahce()
    {
        int i_index = i_imagesNum - 1;
        final Image[] ap_dynImg = ap_dynamicImageCache;
        while (i_index >= 0)
        {
            ap_dynImg[i_index] = null;
            i_index--;
        }
        Runtime.getRuntime().gc();
        Thread.yield();
    }

    /**
     * Инициализация менеджера на базе массива загруженных изображений
     * 
     * @param _images
     *            массив редзагруженных изображений
     * @param _loadExternal
     *            флаг, показывающий что требуется загрузить все изображения
     * @throws Throwable
     *             порождаетсяв случае проблем работы процедуры
     */
    public static final void init(Image[] _images, boolean _loadExternal) throws Throwable
    {
        // Грузим карту изображений
        loadMapResource();

        if (_loadExternal)
            loadAllExternalImages();
        System.gc();
    }

    /**
     * Процедура загрузки всех внешних изображений
     * 
     * @throws Throwable
     *             порождается если была проблема загрузки
     */
    private static final void loadAllExternalImages() throws Throwable
    {
        int i_indexImage = 0;

        InputStream p_extBlockStream = null;

        int i_imageNum = i_imagesNum * IMAGEINFO_LENGTH;
        for (int li = 0; li < i_imageNum; li += IMAGEINFO_LENGTH)
        {
            int i_type = ai_imageInfo[li] & 0xF;

            if (i_type == FLAG_EXTERNAL)
            {
                final int i_index = (ai_imageInfo[li + 1] & 0xFFFF);

                Image p_img = null;

                p_img = Utils.loadImageFromResource("gfx/"+Integer.toString(i_index));

                ap_dynamicImageCache[i_indexImage] = p_img;
            }
            i_indexImage++;
        }

        Utils.closeStream(p_extBlockStream);
    }

    /**
     * Процедура инициализации менеджера и загрузки ресурсов
     * 
     * @param _loadExternal
     *            флаг, показывающий что надо загрузить все внешние изображения
     *            (если true)
     * @throws Throwable
     *             порождается если происходит ошибка в процессе работы
     */
    public static final void init(boolean _loadExternal) throws Throwable
    {
        // Грузим карту изображений
        loadMapResource();

        if (_loadExternal)
            loadAllExternalImages();

        Runtime.getRuntime().gc();
    }

}

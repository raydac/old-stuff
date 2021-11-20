//#local TRANS=true

/**
 * Класс описывает систему динамического формирования PNG изображений 8 бит на пиксель с прозрачностью или без (через чанк tRNS)
 *
 * @author  Igor Maznitsa
 * @version 2.0
 * @since 09 may 2005
 */
public class DEngine
{
    // Заголовок PNG файла
    private static final int[] PNG_HEADER = new int[]{0x89504E47, 0x0D0A1A0A, 0x0000000D, 0x49484452};
    // Тэг окончания PNG файла
    private static final int[] PNG_END = new int[]{0x00000000, 0x49454E44, 0xAE426082};

    //#if TRANS
    private static final int[] PNG_tRNS = new int[]{0x00000000,0x74524E53};
    // Флаг показывающий что прозрачности были изменены
    private static boolean lg_tRNSchanged;
    // Смещение до значения CRC32 кода прозрачностей
    private static int i_tRNScrc32coffset;
    // Смещение до данных tRNS
    private static int i_tRNSdataoffset;
    //#endif

    protected static byte[] ab_ImageData;

    /**
     * The width of the image
     */
    protected static int i_ImageWidth;
    /**
     * The height of the image
     */
    protected static int i_ImageHeight;
    /**
     * The number of colors in the image palette
     */
    protected static int i_PaletteWidth;


    // Флаг показывающий что картинка была изменена
    private static boolean lg_imagechanged;
    // Флаг показывающий что палитра была изменена
    private static boolean lg_palettechanged;
    // Смещение до значения CRC32 кода палитры
    private static int i_palettecrcoffset;
    // Смещение до данных палитры
    private static int i_palettedataoffset;
    // Смещение до RAW блока изображения
    private static int i_imagelen;
    // Смещение до adler32 значения изображения
    private static int i_adler32offset;

    // Позиция CRC32 для IDAT чанка
    private static int i_imageDataCRC32Offset;
    // Смещение до IDAT чанка
    private static int i_startOffsetForIDATChunk;
    // Размер IDAT чанка
    private static int i_lengthIDATChunk;
    // Позиция данных изображения
    private static int i_startPosForImage;

    // Линейный буффер для использования drawSpriteFromLibrary функцией
    private static final byte[] ab_linebuffer = new byte[257];

    // Внутрянняя палитра которая содержит байтовое представление палитры
    private static byte[] ab_palette_array = null;


    //==================== The CRC32 & Adler32 section ============================
    private static final int[] CRC_TABLE = new int[256];

    static
    {
        final int[] ai_crctable = CRC_TABLE;
        for (int li = 0; li < 256; li++)
        {
            int i_c = li;
            for (int lk = 0; lk < 8; lk++)
            {
                if ((i_c & 1) != 0)
                    i_c = 0xedb88320 ^ (i_c >>> 1);
                else
                    i_c = i_c >>> 1;
            }
            ai_crctable[li] = i_c;
        }
    }

    private static final int calculateCRC32(final int _startpos,final int _len)
    {
        int c = -1;
        final int [] crc_table = CRC_TABLE;
        final byte [] ab_imgArr = ab_ImageData;

        int i_len = _len;
        int i_index = _startpos;

        while(i_len!=0)
        {
            c = crc_table[(c ^ ab_imgArr[i_index++]) & 0xff] ^ (c >>> 8);
            i_len--;
        }
        return c ^ (0xFFFFFFFF);
    }

    /**
     * Расчет кода Adler32 для участка данных картинки
     *
     * @param _start   стартовая позиция
     * @param _length  размер рассчитываемого блока
     * @return Adler32 код как int значение
     */
    private static final int calculateAdler32(final int _start,final int _length)
    {
        final int ADLER32_BASE = 0xFFF1;
        final byte [] ab_imgArray = ab_ImageData;

        //int i_adler = 1;
        int i_s1 = 1; //i_adler & 0xffff;
        int i_s2 = 0;// (i_adler >>> 16);

        int i_len = _length;
        int i_index = _start;

        while(i_len!=0)
        {
            i_s1 = (i_s1 + (ab_imgArray[i_index++] & 0xff)) % ADLER32_BASE;
            i_s2 = (i_s2 + i_s1) % ADLER32_BASE;
            i_len--;
        }
        return (i_s2 << 16) | i_s1;
    }

    //----------------------------------------------------------------------------

    /**
     * The function writes an int value to the image array
     *
     * @param _offset the offset for start of writing
     * @param _value  the written value
     * @return
     */
    private final static int writeIntToArray(int _offset,final int _value)
    {
        final byte[] ab_arr = ab_ImageData;
        ab_arr[_offset++] = (byte) (_value >>> 24);
        ab_arr[_offset++] = (byte) (_value >>> 16);
        ab_arr[_offset++] = (byte) (_value >>> 8);
        ab_arr[_offset++] = (byte) _value;
        return _offset;
    }

    // Write an int array into the image array
    private final static int writeIntArray(int _offset, int[] _array)
    {
        final byte[] ab_arr = ab_ImageData;
        final int i_len = _array.length;
        for (int li = 0; li < i_len; li++)
        {
            int i_elem = _array[li];
            ab_arr[_offset++] = (byte) (i_elem >>> 24);
            ab_arr[_offset++] = (byte) (i_elem >>> 16);
            ab_arr[_offset++] = (byte) (i_elem >>> 8);
            ab_arr[_offset++] = (byte) i_elem;
        }
        return _offset;
    }

    protected final static void initEngine(final int _width, final int _height, final int _palettewidth)
    {
        final int i_imgelen = (_width + 1) * _height;
        i_imagelen = i_imgelen;
        int i_back = i_imgelen ^ 0xFFFF;
        //#if DEBUG
        if (i_imgelen > 0x8FFF) new Throwable("DE:Too big image[" + i_imgelen + ']');
        //#endif

        i_ImageWidth = _width;
        i_ImageHeight = _height;
        i_PaletteWidth = _palettewidth;
        final int i_palW = _palettewidth;
        final int i_palettebytes = i_palW * 3;

        int i_fullArrayLength = ((PNG_HEADER.length << 2) + 17) + (i_palettebytes + 12) + (i_imgelen + 11 + 12) + (PNG_END.length << 2);

        //#if TRANS
        i_fullArrayLength += ((PNG_tRNS.length<<2)+i_palW+4);
        //#endif

        final byte[] ab_ImgData = new byte[i_fullArrayLength];
        ab_ImageData = ab_ImgData;

        // Writing the PNG header
        int i_offset = 0;
        i_offset = writeIntArray(i_offset, PNG_HEADER);
        i_offset = writeIntToArray(i_offset, i_ImageWidth);
        i_offset = writeIntToArray(i_offset, i_ImageHeight);
        ab_ImgData[i_offset++] = 8;
        ab_ImgData[i_offset++] = 3;
        ab_ImgData[i_offset++] = 0;
        ab_ImgData[i_offset++] = 0;
        ab_ImgData[i_offset++] = 0;

        int i_crc32 = calculateCRC32(12, 17);// calculateCrc32(_de_ab_imagedata,12,17);
        i_offset = writeIntToArray(i_offset, i_crc32);

        //Writing the palette data
        i_offset = writeIntToArray(i_offset, i_palettebytes);
        i_offset = writeIntToArray(i_offset, 0x504c5445);
        i_palettedataoffset = i_offset;
        i_offset += i_palettebytes;
        i_palettecrcoffset = i_offset;
        i_offset += 4;

        //#if TRANS
        i_offset = writeIntToArray(i_offset,i_palW);
        i_offset = writeIntArray(i_offset,PNG_tRNS);

        i_tRNSdataoffset = i_offset;
        //Записываем прозрачности
        int i_palW2 = i_palW;
        while(i_palW2!=0)
        {
            ab_ImgData[i_offset++] = (byte)0xFF;
            i_palW2 --;
        }
        i_tRNScrc32coffset = i_offset;
        i_offset = writeIntToArray(i_offset,0);
        lg_tRNSchanged = true;
        //#endif

        // Writing the IDAT chunk
        i_offset = writeIntToArray(i_offset, i_imgelen + 11);
        i_startOffsetForIDATChunk = i_offset;
        i_lengthIDATChunk = i_imgelen + 15;
        i_offset = writeIntToArray(i_offset, 0x49444154);

        ab_ImgData[i_offset++] = 0x78;
        ab_ImgData[i_offset++] = (byte) 0xda;
        ab_ImgData[i_offset++] = 0x01;
        ab_ImgData[i_offset++] = (byte) i_imgelen;
        ab_ImgData[i_offset++] = (byte) (i_imgelen >>> 8);
        ab_ImgData[i_offset++] = (byte) i_back;
        ab_ImgData[i_offset++] = (byte) (i_back >>> 8);

        i_startPosForImage = i_offset;
        i_offset += i_imgelen;
        i_adler32offset = i_offset;
        i_offset += 4;
        i_imageDataCRC32Offset = i_offset;
        i_offset += 4;

        // Writing the end of the PNG image
        writeIntArray(i_offset, PNG_END);

        ab_palette_array = new byte[_palettewidth*3];

        lg_imagechanged = true;
        lg_palettechanged = true;
    }

    protected static final byte[] encodeToPNGpackedImageArray()
    {
        final int i_palW = i_PaletteWidth;
        if (lg_palettechanged)
        {
            int i_crc32 = calculateCRC32(i_palettedataoffset - 4, i_palW * 3 + 4);
            writeIntToArray(i_palettecrcoffset, i_crc32);
            lg_palettechanged = false;
        }

        //#if TRANS
        if (lg_tRNSchanged)
        {
            int i_crc32 = calculateCRC32(i_tRNSdataoffset - 4, i_palW + 4);
            writeIntToArray(i_tRNScrc32coffset, i_crc32);
            lg_tRNSchanged = false;
        }
        //#endif

        if (lg_imagechanged)
        {
            int i_adler32code = calculateAdler32(i_startPosForImage, i_imagelen);
            writeIntToArray(i_adler32offset, i_adler32code);
            int i_crc32 = calculateCRC32(i_startOffsetForIDATChunk, i_lengthIDATChunk);
            writeIntToArray(i_imageDataCRC32Offset, i_crc32);
            lg_imagechanged = false;
        }
        return ab_ImageData;
    }

    //#if TRANS
    //====================TRANSPARANCY OPERATIONS===================
    protected static final int getTransparancy(final int _index)
    {
        final int i_index = i_tRNSdataoffset + _index;
        return ab_ImageData[i_index]&0xFF;
    }

    protected static final void setTransparancy(final int _index, int _value)
    {
        final int i_index = i_tRNSdataoffset + _index;
        ab_ImageData[i_index] = (byte) _value;
        lg_tRNSchanged = true;
    }
    //#endif

    //=====================PALETTE OPERATIONS=======================
    /**
     * To download new palette from a byte array
     *
     * @param _newpalette a byte array contains new palette
     */
    protected static final void loadPaletteFromArray(byte[] _newpalette)
    {
        int i_len = i_PaletteWidth * 3;
        //#if DEBUG
        if (_newpalette.length != i_len) new Throwable("DE:Incompatible palette");
        //#endif
        int i_indx = i_palettedataoffset;
        for (int li = 0; li < i_len; li++) ab_ImageData[i_indx++] = _newpalette[li];
        lg_palettechanged = true;
    }

    /**
     * Функция возвращает RGB значение палитры в заданном индексе
     *
     * @param _index индекс
     * @return the color цвет в формате RGB
     */
    protected static final int getPaletteForIndex(int _index)
    {
        final byte[] ab_img = ab_ImageData;
        int i_offst = i_palettedataoffset + _index * 3;
        int i_r = ab_img[i_offst++] & 0xFF;
        int i_g = ab_img[i_offst++] & 0xFF;
        int i_b = ab_img[i_offst] & 0xFF;

        return (i_r << 16) | (i_g << 8) | i_b;
    }

    /**
     * Функция  позволяет загрузить значения палитры из палитрового массива и умножает значения на коэффициент
     *
     * @param _i8coeff              коэффициент с фиксированной точкой 8 бит, используемый при копировании значений
     * @param _brightnessIncreasing значение увеличения яркости, добавляется ко всем составляющим цвета
     * @param _limitSaturation      если true то производится проверка на насыщенность полученного цветового значения и выравнивание его по минимальному или максимальному значению
     */
    protected static final void loadPaletteFromPaletteArray(int _i8coeff, int _brightnessIncreasing, boolean _limitSaturation)
    {
        final byte[] ab_palArray = ab_palette_array;
        final byte[] ab_imageData = ab_ImageData;

        int i_offset = i_palettedataoffset;
        int i_arroffset = 1;

        for (int li = 0; li < i_PaletteWidth; li++)
        {
            int i_r = (((ab_palArray[i_arroffset++] & 0xFF) + _brightnessIncreasing) * _i8coeff + 0x7F) >> 8;
            int i_g = (((ab_palArray[i_arroffset++] & 0xFF) + _brightnessIncreasing) * _i8coeff + 0x7F) >> 8;
            int i_b = (((ab_palArray[i_arroffset++] & 0xFF) + _brightnessIncreasing) * _i8coeff + 0x7F) >> 8;

            if (_limitSaturation)
            {
                if (i_r < 0)
                    i_r = 0;
                else if (i_r > 0xFF) i_r = 0xFF;

                if (i_g < 0)
                    i_g = 0;
                else if (i_g > 0xFF) i_g = 0xFF;

                if (i_b < 0)
                    i_b = 0;
                else if (i_b > 0xFF) i_b = 0xFF;
            }

            ab_imageData[i_offset++] = (byte) i_r;
            ab_imageData[i_offset++] = (byte) i_g;
            ab_imageData[i_offset++] = (byte) i_b;
        }
        lg_palettechanged = true;
    }

    /**
     * Задает новое значение для индекса палитры
     *
     * @param _index    Индекс в палитре
     * @param _rgbvalue упакованное RGB значение
     */
    protected static final void setPaletteForIndex(int _index, int _rgbvalue)
    {
        final byte[] ab_imgData = ab_ImageData;
        final byte[] ab_pal = ab_palette_array;

        int i_offset = i_palettedataoffset + _index * 3;
        byte b_r = (byte) (_rgbvalue >>> 16);
        byte b_g = (byte) (_rgbvalue >>> 8);
        byte b_b = (byte) _rgbvalue;

        ab_imgData[i_offset++] = b_r;
        ab_imgData[i_offset++] = b_g;
        ab_imgData[i_offset] = b_b;

        int i_off = _index * 3;
        i_off++;
        ab_pal[i_off++] = b_r;
        ab_pal[i_off++] = b_g;
        ab_pal[i_off] = b_b;

        lg_palettechanged = true;
    }

    /**
     * Функция скроллирует палитру в изображении
     *
     * @param _startIndex стартовый индекс скроллируемой области
     * @param _length     размер скроллируемой области
     * @param _leftward   если флаг true то скроллируем влево иначе вправо
     * @param _cyclic     флаг, показывающий что скроллирование циклическое
     */
    protected static final void scrollPalette(int _startIndex, int _length, boolean _leftward, boolean _cyclic)
    {
        final byte[] ab_img = ab_ImageData;

        int i_indxOffset = i_palettedataoffset + _startIndex * 3;
        _length--;
        int i_length3 = _length * 3;

        if (_leftward)
        {
            // Scroll leftward
            int i_doffset = i_indxOffset;
            byte b_r = ab_img[i_doffset++];
            byte b_g = ab_img[i_doffset++];
            byte b_b = ab_img[i_doffset];

            System.arraycopy(ab_img, i_indxOffset + 3, ab_img, i_indxOffset, i_length3);

            if (_cyclic)
            {
                i_doffset = i_indxOffset + i_length3;
                ab_img[i_doffset++] = b_r;
                ab_img[i_doffset++] = b_g;
                ab_img[i_doffset] = b_b;
            }
        } else
        {
            // Scroll rightward
            int i_doffset = i_indxOffset + i_length3;
            byte b_r = ab_img[i_doffset++];
            byte b_g = ab_img[i_doffset++];
            byte b_b = ab_img[i_doffset];

            int i_arrIndex = i_indxOffset + i_length3 - 3;

            int li = _length - 1;
            while (li >= 0)
            {
                //Processing of the image array
                int i_indx = i_arrIndex;
                byte b_fr = ab_img[i_indx++];
                byte b_fg = ab_img[i_indx++];
                byte b_fb = ab_img[i_indx++];

                ab_img[i_indx++] = b_fr;
                ab_img[i_indx++] = b_fg;
                ab_img[i_indx++] = b_fb;
                i_arrIndex -= 3;

                li--;
            }

            if (_cyclic)
            {
                i_doffset = i_indxOffset;
                ab_img[i_doffset++] = b_r;
                ab_img[i_doffset++] = b_g;
                ab_img[i_doffset] = b_b;
            }
        }
        lg_palettechanged = true;
    }

    //=====================IMAGE OPERATIONS=======================
    /**
     * Осуществляет вертикальный флип (верхняя строка становится нижней)
     */
    protected static final void flipVertical()
    {
        final int i_wdthg = i_ImageWidth;
        final byte[] ab_imgData = ab_ImageData;
        final byte[] ab_buff = ab_linebuffer;

        int i_topLineOffset = i_startPosForImage + 1;
        int i_offset = i_wdthg + 1;
        int i_downLineOffset = i_topLineOffset + ((i_ImageHeight - 1) * i_offset);

        while (true)
        {
            // Copy of the top line to the buffer
            System.arraycopy(ab_imgData, i_topLineOffset, ab_buff, 1, i_wdthg);
            // Copy of the down line to the top line
            System.arraycopy(ab_imgData, i_downLineOffset, ab_imgData, i_topLineOffset, i_wdthg);
            // Copy the buffer to the down line
            System.arraycopy(ab_buff, 1, ab_imgData, i_downLineOffset, i_wdthg);

            i_topLineOffset += i_offset;
            i_downLineOffset -= i_offset;

            if (i_topLineOffset > i_downLineOffset) break;
        }
        lg_imagechanged = true;
    }

    /**
     * Осуществляет горизонтальный флип
     */
    protected static final void flipHorizontal()
    {
        final byte[] ab_imgData = ab_ImageData;

        int i_leftLineOffset = i_startPosForImage + 1;
        int i_offset = i_ImageWidth + 1;
        int i_rightLineOffset = i_leftLineOffset + i_ImageWidth - 1;
        int i_maxDown = i_rightLineOffset + ((i_ImageHeight - 1) * i_offset);

        while (i_leftLineOffset < i_rightLineOffset)
        {
            int i_vertLOffst = i_leftLineOffset;
            int i_vertROffst = i_rightLineOffset;
            while (i_vertLOffst < i_maxDown)
            {
                final byte b_buff = ab_imgData[i_vertLOffst];
                ab_imgData[i_vertLOffst] = ab_imgData[i_vertROffst];
                ab_imgData[i_vertROffst] = b_buff;
                i_vertLOffst += i_offset;
                i_vertROffst += i_offset;
            }

            i_leftLineOffset++;
            i_rightLineOffset--;
        }

        lg_imagechanged = true;
    }

    /**
     * Ротация картинки на 90 градусов по часовой стрелке, использовать только для квадратных изображений!
     */
    protected static final void rotateRight()
    {
        final byte[] ab_imgData = ab_ImageData;
        final int i_ImgW = i_ImageWidth;

        int i_lineOffset = i_ImgW + 1;
        int i_tlCornerI = i_startPosForImage + 1;
        int i_trCornerI = i_tlCornerI + i_ImgW - 1;
        int i_drCornerI = i_trCornerI + i_lineOffset * (i_ImageHeight - 1);
        int i_dlCornerI = i_tlCornerI + i_lineOffset * (i_ImageHeight - 1);

        int i_lineWidth = i_ImgW - 1;

        while (i_lineWidth > 0)
        {
            int i_tlCorner = i_tlCornerI;
            int i_trCorner = i_trCornerI;
            int i_drCorner = i_drCornerI;
            int i_dlCorner = i_dlCornerI;

            int i_loffst = 0;

            while (i_loffst < i_lineWidth)
            {
                final byte b_tl = ab_imgData[i_tlCorner];
                final byte b_tr = ab_imgData[i_trCorner];
                final byte b_dr = ab_imgData[i_drCorner];
                final byte b_dl = ab_imgData[i_dlCorner];

                ab_imgData[i_tlCorner] = b_dl;
                ab_imgData[i_trCorner] = b_tl;
                ab_imgData[i_drCorner] = b_tr;
                ab_imgData[i_dlCorner] = b_dr;

                i_tlCorner++;
                i_trCorner += i_lineOffset;
                i_drCorner--;
                i_dlCorner -= i_lineOffset;

                i_loffst++;
            }

            i_tlCornerI = i_tlCornerI + i_lineOffset + 1;
            i_trCornerI = i_trCornerI + i_lineOffset - 1;
            i_drCornerI = i_drCornerI - i_lineOffset - 1;
            i_dlCornerI = i_dlCornerI - i_lineOffset + 1;

            i_lineWidth -= 2;
        }
        lg_imagechanged = true;
    }

    /**
     * Ротация картинки на 90 градусов поротив часовой стрелки, использовать только для квадратных изображений!
     */
    protected static final void rotateLeft()
    {
        final int i_imgW = i_ImageWidth;
        final int i_imgH = i_ImageHeight;

        final byte[] ab_imgData = ab_ImageData;

        int i_lineOffset = i_imgW + 1;
        int i_tlCornerI = i_startPosForImage + 1;
        int i_trCornerI = i_tlCornerI + i_imgW - 1;
        int i_drCornerI = i_trCornerI + i_lineOffset * (i_imgH - 1);
        int i_dlCornerI = i_tlCornerI + i_lineOffset * (i_imgH - 1);

        int i_lineWidth = i_imgW - 1;

        while (i_lineWidth > 0)
        {
            int i_tlCorner = i_tlCornerI;
            int i_trCorner = i_trCornerI;
            int i_drCorner = i_drCornerI;
            int i_dlCorner = i_dlCornerI;

            int i_loffst = 0;

            while (i_loffst < i_lineWidth)
            {
                final byte b_tl = ab_imgData[i_tlCorner];
                final byte b_tr = ab_imgData[i_trCorner];
                final byte b_dr = ab_imgData[i_drCorner];
                final byte b_dl = ab_imgData[i_dlCorner];

                ab_imgData[i_tlCorner] = b_tr;
                ab_imgData[i_trCorner] = b_dr;
                ab_imgData[i_drCorner] = b_dl;
                ab_imgData[i_dlCorner] = b_tl;

                i_tlCorner += i_lineOffset;
                i_trCorner--;
                i_drCorner -= i_lineOffset;
                i_dlCorner++;

                i_loffst++;
            }

            i_tlCornerI = i_tlCornerI + i_lineOffset + 1;
            i_trCornerI = i_trCornerI + i_lineOffset - 1;
            i_drCornerI = i_drCornerI - i_lineOffset - 1;
            i_dlCornerI = i_dlCornerI - i_lineOffset + 1;

            i_lineWidth -= 2;
        }
        lg_imagechanged = true;
    }

    /**
     * Задать индекс цвета для пикселя (без осуществления проверки выхода за границы изображения)
     *
     * @param _x     координата X
     * @param _y     координата Y
     * @param _colorIndex цветовой индекс из палитры
     */
    protected static final void setPixelWithoutChecking(int _x, int _y, int _colorIndex)
    {
        ab_ImageData[i_startPosForImage + (1 + i_ImageWidth) * _y + _x + 1] = (byte) _colorIndex;
        lg_imagechanged = true;
    }

    /**
     * Возвращает цветовой индекс пикселя (без проверки выхода за границы)
     *
     * @param _x координата X
     * @param _y координата Y
     * @return цветовой индекс как int значение
     */
    protected static final int getPixelWithoutChecking(int _x, int _y)
    {
        return (ab_ImageData[i_startPosForImage + (1 + i_ImageWidth) * _y + _x + 1] & 0xFF);
    }

    /**
     * Заполнение изображения заданным цветом
     *
     * @param _backgroundColorIndex индекс выбранного цвета
     */
    protected static final void clearImage(final int _backgroundColorIndex)
    {
        int i_indx = i_ImageWidth;
        final byte[] ab_lb = ab_linebuffer;
        final byte[] ab_imgData = ab_ImageData;
        final byte b_indexColor = (byte) _backgroundColorIndex;
        while (i_indx != 1)
        {
            ab_lb[i_indx--] = b_indexColor;
        }

        int i_offset = i_startPosForImage;
        int i_wdth = i_ImageWidth + 1;
        i_indx = i_ImageHeight - 1;
        while (i_indx >= 0)
        {
            System.arraycopy(ab_lb, 0, ab_imgData, i_offset, i_wdth);
            i_offset += i_wdth;
            i_indx--;
        }
        lg_imagechanged = true;
    }

    /**
     * Скроллирует изображение вверх на заданное количество точек
     *
     * @param _step количество пикселей для скроллинга
     */
    protected static final void scrollUp(final int _step)
    {
        final int i_imgW = i_ImageWidth;
        final byte[] ab_ImgData = ab_ImageData;
        final int i_startPos = i_startPosForImage;
        int i_offset = i_imgW + 1;
        int i_topOffset = i_startPos + 1;
        int i_stOffset = i_topOffset + i_offset * _step;
        int i_maxData = i_startPos + (i_ImageHeight - 1) * i_offset;

        while (i_stOffset < i_maxData)
        {
            System.arraycopy(ab_ImgData, i_stOffset, ab_ImgData, i_topOffset, i_imgW);
            i_topOffset += i_offset;
            i_stOffset += i_offset;
        }
        lg_imagechanged = true;
    }

    /**
     * Скроллинг изображения вниз
     *
     * @param _step шаг скроллирования в пикселях
     */
    protected static final void scrollDown(final int _step)
    {
        final int i_imgW = i_ImageWidth;
        final byte[] ab_ImgData = ab_ImageData;
        final int i_startPos = i_startPosForImage;
        int i_offset = i_imgW + 1;
        int i_downOffset = i_startPos + 1 + (i_ImageHeight - 1) * i_offset;
        int i_stOffset = i_downOffset - (i_offset * _step);

        while (i_stOffset > i_startPosForImage)
        {
            System.arraycopy(ab_ImgData, i_stOffset, ab_ImgData, i_downOffset, i_imgW);
            i_downOffset -= i_offset;
            i_stOffset -= i_offset;
        }
        lg_imagechanged = true;
    }

    /**
     * Скроллирование изображения вправо
     *
     * @param _step количество пикселей к скроллированию
     */
    protected static final void scrollRight(final int _step)
    {
        final int i_imgW = i_ImageWidth;
        final byte[] ab_ImgData = ab_ImageData;
        int i_imageoffset = i_startPosForImage + 1;
        int i_imageoffset2 = _step + i_startPosForImage + 1;

        int i_width = i_imgW - _step;

        int li = i_ImageHeight;

        while (li != 0)
        {
            System.arraycopy(ab_ImgData, i_imageoffset, ab_ImgData, i_imageoffset2, i_width);
            i_imageoffset += i_imgW + 1;
            i_imageoffset2 += i_imgW + 1;
            li--;
        }
        lg_imagechanged = true;
    }

    /**
     * Скроллирование изображения влево на заданный шаг
     *
     * @param _step количество пикселей на которое следует сместить изображение
     */
    protected static final void scrollLeft(final int _step)
    {
        final int i_imgW = i_ImageWidth;
        final byte[] ab_ImgData = ab_ImageData;
        int i_imageoffset = _step + i_startPosForImage + 1;
        int i_imageoffset2 = i_startPosForImage + 1;

        int i_width = i_imgW - _step;

        int li = i_ImageHeight;

        while (li != 0)
        {
            System.arraycopy(ab_ImgData, i_imageoffset, ab_ImgData, i_imageoffset2, i_width);
            i_imageoffset += i_imgW + 1;
            i_imageoffset2 += i_imgW + 1;
            li--;
        }
        lg_imagechanged = true;
    }

}

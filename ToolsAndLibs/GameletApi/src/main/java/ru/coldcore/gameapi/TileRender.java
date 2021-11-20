package ru.coldcore.gameapi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

/**
 * Тайловый рендер
 * @author Igor Maznitsa (igor.maznitsa@raydac-research.com)
 * @version 2.06
 */
public class TileRender
{
    /**
     * Флаг, показывающий что скроллирование будет осуществляться построчно вниз
     * (видимое изображение едет снизу вверх)
     */
    private static final boolean SCROLLDOWN = true;

    /**
     * Флаг, показывающий что скроллирование будет осуществляться построчно
     * вверх (видимое изображение едет сверху вниз)
     */
    private static final boolean SCROLLUP = true;

    /**
     * Флаг, показывающий что скроллирование будет осуществляться построчно
     * влево (видимое изображение едет слева направо)
     */
    private static final boolean SCROLLLEFT = true;

    /**
     * Флаг, показывающий что скроллирование будет осуществляться построчно
     * вправо (видимое изображение едет справа налево)
     */
    private static final boolean SCROLLRIGHT = true;

    /**
     * Индекс тайловой картинки или смещения по умолчанию (обычно 0)
     */
    private static final int DEFAULTTILE = 0;

    /**
     * Количество тайловых картинок в одном ряду тайлового изображения
     */
    private static final int TILESPERLINE = 16;

    /**
     * Флаг показывает что требуется поизводить компенсацию setClip, так как
     * устройство неправильно отрабатывает выход области за пределы экрана
     */
    private static final boolean CLIPCOMPENSATION = true;

    /**
     * Флаг показывает что следует отображать зацикленный массив
     */
    private static final boolean CYCLED = true;

    /**
     * Флаг показывает что следует использовать двойную буферизацию
     */
    private static final boolean DOUBLEBUFFER = true;

    /**
     * Флаг показывает что можно использовать буфер для копирования частей
     * теневого буфера
     */
    private static final boolean COPYPARTENABLE = true;

    /**
     * Флаг показывает что используется массив с short данными тайлового поля
     * иначе int
     */
    private static final boolean SHORTDATAARRAY = true;

    /**
     * Флаг показывает что используются раздельные картинки для каждого тайла
     */
    private static final boolean SEPARATEDIMAGES = false;

    /**
     * Картинка содержит тайлы для отображения
     */
    public Image p_TileSetImage;

    /**
     * Массив содержит картинки тайлов
     */
    public Image[] ap_TileSetImages;

    /**
     * Массив содержит данные тайлов в short представлении
     */
    public short[] ash_TileArray;

    /**
     * Массив содержит данные тайлов в int представлении
     */
    public int[] ai_TileArray;

    /**
     * Ширина области вывода тайлового изображения
     */
    public int i_OutAreaWidth;

    /**
     * Высота области вывода тайлового изображения
     */
    public int i_OutAreaHeight;

    /**
     * Ширина ячейки в пикселях
     */
    private int i_CellWidth;

    /**
     * Высота ячейки в пикселях
     */
    private int i_CellHeight;

    /**
     * Теневой буфер
     */
    public Image p_HiddenBuffer;

    /**
     * Объект Graphics теневого буфера
     */
    public Graphics p_HiddenBufferGraphics;

    /**
     * Ширина теневого буффера в ячейках
     */
    public int i_HiddenBufferCellWidth;

    /**
     * Высота теневого буффера в ячейках
     */
    public int i_HiddenBufferCellHeight;

    /**
     * Буфер для копирования участков теневого буфера
     */
    private Image p_CopyBuffer;

    /**
     * Объект Graphics буфера для копирования участков теневого буфера
     */
    private Graphics p_CopyBufferGraphics;

    /**
     * Ширина буфера для копирования
     */
    private int i_CopyBufferPartWidth;

    /**
     * Высота буфера для копирования
     */
    private int i_CopyBufferPartHeight;

    /**
     * Количество участков копирования теневого буфера по ширине теневого буфера
     */
    private static final int COPY_PARTS_NUMBER_HORZ = 2;

    /**
     * Количество участков копирования теневого буфера по высоте теневого буфера
     */
    private static final int COPY_PARTS_NUMBER_VERT = 2;

    /**
     * Количество ячеек, выводимых на экран по ширине
     */
    private int i_CellsNumberOnScreenX;

    /**
     * Количество ячеек, выводимых на экран по высоте
     */
    private int i_CellsNumberOnScreenY;

    /**
     * Ширина массива, содержащего информацию о тайлах
     */
    public int i_TileArrayWidth;

    /**
     * Высота массива, содержащего информацию о тайлах
     */
    public int i_TileArrayHeight;

    /**
     * Координата X верхней левой точки отображаемой области тайлового поля
     */
    public int i_ViewAreaX;

    /**
     * Координата Y верхней левой точки отображаемой области тайлового поля
     */
    public int i_ViewAreaY;

    /**
     * Верхняя левая координата X отображаемой области
     */
    public int i_ViewAreaTopCellX;

    /**
     * Верхняя левая координата Y отображаемой области
     */
    public int i_ViewAreaTopCellY;

    /**
     * Верхняя левая координата X области буффера
     */
    public int i_BufferAreaTopCellX;

    /**
     * Верхняя левая координата Y области буффера
     */
    public int i_BufferAreaTopCellY;

    /**
     * Ширина экрана
     */
    public static int SCREEN_WIDTH;

    /**
     * Высота экрана
     */
    public static int SCREEN_HEIGHT;

    /**
     * Инициализация менеджера
     * 
     * @param _outAreaWidth ширина области отобюражения тайловой картинки
     * @param _outAreaHeight высота области отображения тайловой картинки
     * @param _cellWidth ширина тайловой ячейки
     * @param _cellHeight высота тайловой ячейки
     */
    public final void init(int _outAreaWidth, int _outAreaHeight, int _cellWidth, int _cellHeight)
    {
        i_OutAreaWidth = _outAreaWidth;
        i_OutAreaHeight = _outAreaHeight;
        i_CellWidth = _cellWidth;
        i_CellHeight = _cellHeight;

        i_CellsNumberOnScreenX = _outAreaWidth / i_CellWidth + (_outAreaWidth % i_CellWidth != 0 ? 1 : 0);
        i_CellsNumberOnScreenY = _outAreaHeight / i_CellHeight + (_outAreaHeight % i_CellHeight != 0 ? 1 : 0);

        if (DOUBLEBUFFER)
        {
            // Создаем даблбуфер

            // Размеры буфера должны быть выровнены по ячейкам для правильного
            // скроллирования
            // размер буфера делаем на одну ячейки в каждое направления
            // скроллирования больше

            int i_addedBufferHorzCells = 0, i_addedBufferVertCells = 0;

            if (SCROLLUP) i_addedBufferVertCells++;
            if (SCROLLDOWN) i_addedBufferVertCells++;
            if (SCROLLLEFT) i_addedBufferHorzCells++;
            if (SCROLLRIGHT) i_addedBufferHorzCells++;

            i_HiddenBufferCellWidth = i_CellsNumberOnScreenX + i_addedBufferHorzCells;
            i_HiddenBufferCellHeight = i_CellsNumberOnScreenY + i_addedBufferVertCells;

            final int i_bufferWidth = i_HiddenBufferCellWidth * i_CellWidth;
            final int i_bufferHeight = i_HiddenBufferCellHeight * i_CellHeight;

            p_HiddenBuffer = Utils.createTransparentImage(i_bufferWidth, i_bufferHeight);
            p_HiddenBufferGraphics = p_HiddenBuffer.getGraphics();

            if (COPYPARTENABLE)
            {
                // вычисляем размер буфера копирования
                if (COPY_PARTS_NUMBER_HORZ == 1 && COPY_PARTS_NUMBER_VERT == 1)
                {
                    i_CopyBufferPartWidth = i_bufferWidth;
                    i_CopyBufferPartHeight = i_bufferHeight;
                }
                else
                {
                    i_CopyBufferPartWidth = (i_bufferWidth / COPY_PARTS_NUMBER_HORZ) + (i_bufferWidth & 1);
                    i_CopyBufferPartHeight = (i_bufferHeight / COPY_PARTS_NUMBER_VERT) + (i_bufferHeight & 1);
                }

                p_CopyBuffer = Utils.createTransparentImage(i_CopyBufferPartWidth, i_CopyBufferPartHeight);
                p_CopyBufferGraphics = p_CopyBuffer.getGraphics();
            }
        }

        i_ViewAreaX = 0x7FFFFFFF;
        i_ViewAreaY = 0x7FFFFFFF;
    }

    /**
     * Изменить значение ячейки в тайловом массиве
     * @param _value новое значение
     * @param _position позиция в массиве (массив одномерный)
     */
    public final void setTileValue(int _value, int _position)
    {
        final int[] ai_tilearr = ai_TileArray;
        final short[] ash_tilearr = ash_TileArray;

        int i_cellX = _position % i_TileArrayWidth;
        int i_cellY = _position / i_TileArrayWidth;

        int i_cx = 0, i_cy = 0;

        if (SEPARATEDIMAGES)
        {
            if (SHORTDATAARRAY)
            {
                ash_tilearr[_position] = (short) _value;
            }
            else
            {
                ai_tilearr[_position] = _value;
            }
        }
        else
        {
            // конвертируем value
            i_cx = (_value % TILESPERLINE) * i_CellWidth;
            i_cy *= (_value / TILESPERLINE) * i_CellHeight;

            if (SHORTDATAARRAY)
                ash_tilearr[_position] = (short) ((i_cx << 8) | i_cy);
            else
                ai_tilearr[_position] = (i_cx << 16) | i_cy;
        }

        if (DOUBLEBUFFER)
        {
            // если присутствует теневой буффер, то производим проверку на отображение измененной ячейки и перерисовываем её
            int i_topcellX = i_BufferAreaTopCellX;
            int i_topcellY = i_BufferAreaTopCellY;

            if (i_cellX > i_topcellX && i_cellY > i_topcellY)
            {
                int i_leftcellX = i_topcellX + i_HiddenBufferCellWidth;
                int i_leftcellY = i_topcellY + i_HiddenBufferCellHeight;
                if (i_cellX < i_leftcellX && i_cellY < i_leftcellY)
                {
                    // ячейка в зоне видимости

                    // вычисляем её координаты на поле буффера
                    i_cellX = (i_cellX - i_topcellX) * i_CellWidth;
                    i_cellY = (i_cellY - i_topcellY) * i_CellHeight;

                    // производим отрисовку
                    if (SEPARATEDIMAGES)
                    {
                        Image p_image = ap_TileSetImages[_value];
                        p_HiddenBufferGraphics.drawImage(p_image, i_cellX, i_cellY, null);
                    }
                    else
                    {
                        p_HiddenBufferGraphics.setClip(i_cellX, i_cellY, i_CellWidth, i_CellHeight);
                        p_HiddenBufferGraphics.drawImage(p_TileSetImage, i_cellX - i_cx, i_cellY - i_cy, null);
                    }
                }
            }
        }
    }

    /**
     * Задает текстовую строку как массив тайлов
     * @param _string
     */
    public final void setSringAsTileArray(String _string)
    {
        final char[] ach_chars = _string.toCharArray();
        int i_stringLen = _string.length();

        int i_arrayHeight = i_stringLen / i_CellsNumberOnScreenX;
        if (i_stringLen % i_CellsNumberOnScreenX != 0) i_arrayHeight++;

        byte[] ab_tileArray = new byte[i_CellsNumberOnScreenX * i_arrayHeight];

        int i_pos = 0;

        while (i_stringLen != 0)
        {
            char ch_curChar = ach_chars[i_pos];
            int i_value = 0;

            if (ch_curChar >= '!' && ch_curChar < '\'')
            {
                i_value = ch_curChar - '!' + 1;
            }
            else
                if (ch_curChar >= 'a' && ch_curChar <= 'z')
                {
                    i_value = (ch_curChar - 'a') + 'A';
                }
                else
                    if (ch_curChar >= 'А' && ch_curChar <= 'Я')
                    {
                        i_value = (ch_curChar - 'А') + 68;
                    }
                    else
                        if (ch_curChar >= 'а' && ch_curChar <= 'ё')
                        {
                            i_value = (ch_curChar - 'а') + 68;
                        }
                        else
                            if (ch_curChar == 'Ё')
                            {
                                i_value = 100;
                            }

            // корректируем
            int i_x = i_value & 0xF;
            int i_y = i_value >>> 4;
            i_x <<= 4;

            ab_tileArray[i_pos++] = (byte) (i_x | i_y);

            i_stringLen--;
        }

        setTileArray(ab_tileArray, i_CellsNumberOnScreenX);
    }

    /**
     * Задаем массив содержащий информацию о тайлах
     * 
     * @param _tileArray одномерный массив с информацией о тайлах
     * @param _lineWidth ширина линии массива в ячейках
     */
    public final void setTileArray(byte[] _tileArray, int _lineWidth)
    {
        final int i_length = _tileArray.length;
        i_TileArrayWidth = _lineWidth;
        i_TileArrayHeight = i_length / _lineWidth;

        if (SHORTDATAARRAY)
        {
            ash_TileArray = new short[i_length];
        }
        else
        {
            ai_TileArray = new int[i_length];
        }

        if (SEPARATEDIMAGES)
        {
            if (SHORTDATAARRAY)
            {
                for (int li = 0; li < i_length; li++)
                    ash_TileArray[li] = (short) (_tileArray[li] & 0xFF);
            }
            else
            {
                for (int li = 0; li < i_length; li++)
                    ai_TileArray[li] = _tileArray[li] & 0xFF;
            }
        }
        else
        {
            for (int li = 0; li < i_length; li++)
            {
                int i_data = _tileArray[li] & 0xFF;
                int i_cx = 0, i_cy = 0;

                switch (TILESPERLINE)
                {
                    case 1:
                    {
                        i_cx = 0;
                        i_cy = i_data;
                    }
                        ;
                        break;
                    case 2:
                    {
                        i_cx = i_data >>> 7;
                        i_cy = i_data & 0x7F;
                    }
                        ;
                        break;
                    case 4:
                    {
                        i_cx = i_data >>> 6;
                        i_cy = i_data & 0x3F;
                    }
                        ;
                        break;
                    case 8:
                    {
                        i_cx = i_data >>> 5;
                        i_cy = i_data & 0x1F;
                    }
                        ;
                        break;
                    case 16:
                    {
                        i_cx = i_data >>> 4;
                        i_cy = i_data & 0xF;
                    }
                        ;
                        break;
                }

                i_cx *= i_CellWidth;
                i_cy *= i_CellHeight;

                if (SHORTDATAARRAY)
                    ash_TileArray[li] = (short) ((i_cx << 8) | i_cy);
                else
                    ai_TileArray[li] = (i_cx << 16) | i_cy;
            }
        }
    }

    /**
     * Установка изображения, содержащего тайлы
     * @param _image одно общее изображение с тайлами
     * @param _imageArray массив изображений, каждое из которых являтся отдельным тайлом
     */
    public final void setTileSetImage(Image _image, Image[] _imageArray)
    {
        if (SEPARATEDIMAGES)
            ap_TileSetImages = _imageArray;
        else
            p_TileSetImage = _image;

        if (DOUBLEBUFFER)
        {
            int i_cellTX = 0;
            int i_cellTY = 0;
            int i_cellW = i_HiddenBufferCellWidth;
            int i_cellH = i_HiddenBufferCellHeight;

            _fillHiddenBufferPart(i_cellTX, i_cellTY, i_cellW, i_cellH);
        }
    }

    /**
     * Отрисовка тайлового поля на графическое устройство в заданную область
     * @param _g контекст графического устройства
     * @param _x координата X верхней левой точки области
     * @param _y координата Y верхней левой точки области
     * @param _w ширина области
     * @param _h высота области
     */
    public final void directPaint(final Graphics _g, int _x, int _y, int _w, int _h)
    {
        final Image[] ap_separatedImages = ap_TileSetImages;
        final Image p_tileImage = p_TileSetImage;
        final short[] ash_tilearr = ash_TileArray;
        final int[] ai_tilearr = ai_TileArray;

        int LEFTAREAX = _x + _w;
        int LEFTAREAY = _y + _h;

        final int ARRWIDTH = i_TileArrayWidth;
        final int ARRHEIGHT = i_TileArrayHeight;

        final int SCR_WIDTH = SCREEN_WIDTH;
        final int SCR_HEIGHT = SCREEN_HEIGHT;

        if (LEFTAREAX >= SCR_WIDTH) LEFTAREAX = SCR_WIDTH - 1;
        if (LEFTAREAY >= SCR_HEIGHT) LEFTAREAY = SCR_HEIGHT - 1;

        final int CELLWIDTH = i_CellWidth;
        final int CELLHEIGHT = i_CellHeight;

        final int CELLSHORZ = _w / CELLWIDTH + (_w % CELLWIDTH == 0 ? 0 : 1);
        final int CELLSVERT = _h / CELLHEIGHT + (_h % CELLHEIGHT == 0 ? 0 : 1);

        int i_viewX = i_ViewAreaX;
        int i_viewY = i_ViewAreaY;

        int i_startCellX = i_ViewAreaTopCellX;
        int i_startCellY = i_ViewAreaTopCellY;

        int i_deltaX = i_viewX % CELLWIDTH;
        int i_deltaY = i_viewY % CELLHEIGHT;

        int i_limitCellX = i_startCellX + CELLSHORZ + (i_deltaX % CELLWIDTH == 0 ? 0 : 1);
        int i_limitCellY = i_startCellY + CELLSVERT + (i_deltaY % CELLHEIGHT == 0 ? 0 : 1);

        boolean lg_coordYoutOfBounds, lg_coordXoutOfBounds;
        int i_lineCellStart;

        if (i_viewX < 0 && i_deltaX != 0)
        {
            i_deltaX = _x - i_deltaX;
            i_startCellX--;
            i_limitCellX--;
            i_deltaX -= CELLWIDTH;
        }
        else
        {
            i_deltaX = _x - i_deltaX;
        }

        if (i_viewY < 0 && i_deltaY != 0)
        {
            i_deltaY = _y - i_deltaY;
            i_startCellY--;
            i_limitCellY--;
            i_deltaY -= CELLHEIGHT;
        }
        else
        {
            i_deltaY = _y - i_deltaY;
        }

        int i_lineCS = 0;
        if (!CYCLED)
        {
            i_lineCS = i_startCellY * ARRWIDTH;
        }

        if (CLIPCOMPENSATION)
        {
            if (_y < 0) _y = 0;
            if (_x < 0) _x = 0;
        }

        for (int ly = i_startCellY; ly < i_limitCellY; ly++)
        {
            if (CYCLED)
            {
                i_lineCellStart = ly % ARRHEIGHT;
                if (i_lineCellStart < 0) i_lineCellStart += ARRHEIGHT;
                i_lineCellStart *= ARRWIDTH;
            }
            else
            {
                lg_coordYoutOfBounds = ly < 0 || ly >= ARRHEIGHT;
                i_lineCellStart = i_lineCS + i_startCellX;
            }

            int i_pixelX = i_deltaX;

            for (int lx = i_startCellX; lx < i_limitCellX; lx++)
            {
                int i_blockNum;

                if (CYCLED)
                {
                    int i_xIndex = lx % ARRWIDTH;
                    if (i_xIndex < 0) i_xIndex += ARRWIDTH;

                    if (SHORTDATAARRAY)
                        i_blockNum = ash_tilearr[i_lineCellStart + i_xIndex] & 0xFFFF;
                    else
                        i_blockNum = ai_tilearr[i_lineCellStart + i_xIndex];
                }
                else
                {
                    lg_coordXoutOfBounds = lx < 0 || lx >= ARRWIDTH;

                    if (lg_coordXoutOfBounds || lg_coordYoutOfBounds)
                    {
                        i_blockNum = DEFAULTTILE;
                    }
                    else
                        if (SHORTDATAARRAY)
                            i_blockNum = ash_tilearr[i_lineCellStart] & 0xFFFF;
                        else
                            i_blockNum = ai_tilearr[i_lineCellStart];
                }

                int i_clipX, i_clipY, i_clipWidth, i_clipHeight;
                if (SEPARATEDIMAGES)
                {
                    if (CLIPCOMPENSATION)
                    {
                        i_clipX = i_pixelX;
                        i_clipY = i_deltaY;
                        i_clipWidth = CELLWIDTH;
                        i_clipHeight = CELLHEIGHT;

                        if (i_clipX < _x)
                        {
                            i_clipWidth -= (_x - i_clipX);
                            i_clipX = _x;
                        }
                        else
                            if (i_clipX < 0)
                            {
                                i_clipWidth += i_clipX;
                                i_clipX = 0;
                            }

                        int i_outLeftX = i_clipX + i_clipWidth;
                        if (i_outLeftX > LEFTAREAX)
                        {
                            i_clipWidth = CELLWIDTH - (i_outLeftX - LEFTAREAX);
                        }

                        if (i_clipY < _y)
                        {
                            i_clipHeight -= (_y - i_clipY);
                            i_clipY = _y;
                        }
                        else
                            if (i_clipY < 0)
                            {
                                i_clipHeight += i_clipY;
                                i_clipY = _y;
                            }

                        int i_outLeftY = i_clipY + i_clipHeight;
                        if (i_outLeftY > LEFTAREAY)
                        {
                            i_clipHeight = CELLHEIGHT - (i_outLeftY - LEFTAREAY);
                        }

                        _g.setClip(i_clipX, i_clipY, i_clipWidth, i_clipHeight);
                    }

                    _g.drawImage(ap_separatedImages[i_blockNum], i_pixelX, i_deltaY, null);
                }
                else
                {
                    int i_imgCol, i_imgRow;
                    if (SHORTDATAARRAY)
                    {
                        i_imgCol = i_blockNum >>> 8;
                        i_imgRow = i_blockNum & 0xFF;
                    }
                    else
                    {
                        i_imgCol = i_blockNum >>> 16;
                        i_imgRow = i_blockNum & 0xFFFF;
                    }

                    if (CLIPCOMPENSATION)
                    {
                        i_clipX = i_pixelX;
                        i_clipY = i_deltaY;
                        i_clipWidth = CELLWIDTH;
                        i_clipHeight = CELLHEIGHT;

                        if (i_clipX < _x)
                        {
                            i_clipWidth -= (_x - i_clipX);
                            i_clipX = _x;
                        }
                        else
                            if (i_clipX < 0)
                            {
                                i_clipWidth += i_clipX;
                                i_clipX = 0;
                            }

                        int i_outLeftX = i_clipX + i_clipWidth;
                        if (i_outLeftX > LEFTAREAX)
                        {
                            i_clipWidth = CELLWIDTH - (i_outLeftX - LEFTAREAX);
                        }

                        if (i_clipY < _y)
                        {
                            i_clipHeight -= (_y - i_clipY);
                            i_clipY = _y;
                        }
                        else
                            if (i_clipY < 0)
                            {
                                i_clipHeight += i_clipY;
                                i_clipY = 0;
                            }

                        int i_outLeftY = i_clipY + i_clipHeight;
                        if (i_outLeftY > LEFTAREAY)
                        {
                            i_clipHeight = CELLHEIGHT - (i_outLeftY - LEFTAREAY);
                        }
                    }
                    else
                    {
                        i_clipX = i_pixelX;
                        i_clipY = i_deltaY;
                        i_clipWidth = CELLWIDTH;
                        i_clipHeight = CELLHEIGHT;
                    }

                    _g.setClip(i_clipX, i_clipY, i_clipWidth, i_clipHeight);
                    _g.drawImage(p_tileImage, i_pixelX - i_imgCol, i_deltaY - i_imgRow, null);
                }
                i_pixelX += CELLWIDTH;
                if (i_pixelX >= SCREEN_WIDTH) break;
                if (!CYCLED) i_lineCellStart++;
            }

            if (!CYCLED) i_lineCS += ARRWIDTH;
            i_deltaY += CELLHEIGHT;
            if (i_deltaY >= SCREEN_HEIGHT) break;
        }
    }

    /**
     * Заполнить вес теневой буффер изображением для текущей позиции
     */
    public final void fillAllHiddenBuffer()
    {
        _fillHiddenBufferPart(0, 0, i_CellsNumberOnScreenX, i_CellsNumberOnScreenY);
    }

    /**
     * Заполнить участок теневого буфера
     * 
     * @param _localCellX смещение ячейки на теневом буфере по X
     * @param _localCellY смещение ячейки на теневом буфере по Y
     * @param _widthCells ширина зоны заполнения в ячейках
     * @param _heightCells высота зоны заполнения в ячейках
     */
    private final void _fillHiddenBufferPart(int _localCellX, int _localCellY, int _widthCells, int _heightCells)
    {

        final int[] ai_tilearr = ai_TileArray;
        final short[] ash_tilearr = ash_TileArray;

        if (SHORTDATAARRAY)
        {
            if (ash_tilearr == null) return;
        }
        else
        {
            if (ai_tilearr == null) return;
        }

        final int CELLWIDTH = i_CellWidth;
        final int CELLHEIGHT = i_CellHeight;

        // стартовая координата X верхней левой ячейки
        int i_topTileX = i_BufferAreaTopCellX + _localCellX;
        int i_topTileY = i_BufferAreaTopCellY + _localCellY;

        int i_BufferCellsWidth = _widthCells;
        int i_BufferCellsHeight = _heightCells;

        final int ARRHEIGHT = i_TileArrayHeight;
        final int ARRWIDTH = i_TileArrayWidth;
        final Image p_tileimg = p_TileSetImage;
        final Image[] ap_tileimages = ap_TileSetImages;
        final Graphics p_hiddenBufferGraphics = p_HiddenBufferGraphics;

        int i_arraylineOffset = ARRWIDTH * i_topTileY;

        // Заполняем буфер графикой
        int i_cellCoordY = _localCellY * CELLHEIGHT;

        while (i_BufferCellsHeight != 0)
        {
            int i_buffw = i_BufferCellsWidth;

            boolean lg_youtofbound = false;
            int i_curLine;

            if (CYCLED)
            {
                i_arraylineOffset = i_topTileY % ARRHEIGHT;
                if (i_arraylineOffset < 0) i_arraylineOffset += ARRHEIGHT;
                i_arraylineOffset *= ARRWIDTH;
            }
            else
            {
                if (i_topTileY < 0 || i_topTileY >= ARRHEIGHT) lg_youtofbound = true;
                i_curLine = i_arraylineOffset + i_topTileX;
            }

            int i_cellIndexX = i_topTileX;
            int i_cellCoordX = _localCellX * CELLWIDTH;

            while (i_buffw != 0)
            {
                boolean lg_xoutofbound = false;

                int i_blockNum;

                if (CYCLED)
                {
                    i_curLine = i_cellIndexX % ARRWIDTH;
                    if (i_curLine < 0) i_curLine += ARRWIDTH;
                    i_curLine += i_arraylineOffset;

                    if (SHORTDATAARRAY)
                        i_blockNum = ash_tilearr[i_curLine] & 0xFFFF;
                    else
                        i_blockNum = ai_tilearr[i_curLine];
                }
                else
                {
                    lg_xoutofbound = i_cellIndexX < 0 || i_cellIndexX >= ARRWIDTH;
                    if (lg_youtofbound || lg_xoutofbound)
                        i_blockNum = DEFAULTTILE;
                    else
                    {
                        if (SHORTDATAARRAY)
                            i_blockNum = ash_tilearr[i_curLine] & 0xFFFF;
                        else
                            i_blockNum = ai_tilearr[i_curLine];
                    }
                }

                if (SEPARATEDIMAGES)
                {
                    p_hiddenBufferGraphics.drawImage(ap_tileimages[i_blockNum], i_cellCoordX, i_cellCoordY, null);
                }
                else
                {
                    int i_imgCol, i_imgRow;
                    if (SHORTDATAARRAY)
                    {
                        i_imgCol = i_blockNum >>> 8;
                        i_imgRow = i_blockNum & 0xFF;
                    }
                    else
                    {
                        i_imgCol = i_blockNum >>> 16;
                        i_imgRow = i_blockNum & 0xFFFF;
                    }
                    p_hiddenBufferGraphics.setClip(i_cellCoordX, i_cellCoordY, CELLWIDTH, CELLHEIGHT);
                    p_hiddenBufferGraphics.drawImage(p_tileimg, i_cellCoordX - i_imgCol, i_cellCoordY - i_imgRow, null);
                }

                if (!CYCLED) i_curLine++;
                i_cellIndexX++;
                i_cellCoordX += CELLWIDTH;
                i_buffw--;
            }

            if (!CYCLED) i_arraylineOffset += ARRWIDTH;

            i_topTileY++;
            i_BufferCellsHeight--;
            i_cellCoordY += CELLHEIGHT;
        }
    }

    /**
     * Отриосвка содержимого теневого буффера
     * @param _g контекст устройства
     * @param _x координата X верхнего левого края вывода
     * @param _y координата Y верхнего левого края вывода
     */
    public final void drawHiddenBuffer(Graphics _g, int _x, int _y)
    {
        if (_x >= SCREEN_WIDTH || _y >= SCREEN_HEIGHT) return;

        int i_clipX = _x;
        int i_clipY = _y;
        int i_clipW = i_OutAreaWidth;
        int i_clipH = i_OutAreaHeight;

        if (CLIPCOMPENSATION)
        {
            if (i_clipX < 0)
            {
                i_clipW += i_clipX;
                i_clipX = 0;
            }

            if (i_clipY < 0)
            {
                i_clipH += i_clipY;
                i_clipY = 0;
            }

            int i_leftX = i_clipX + i_clipW;
            int i_leftY = i_clipY + i_clipH;
            if (i_leftX >= SCREEN_WIDTH)
            {
                i_clipW += (SCREEN_WIDTH - (i_leftX + 1));
            }

            if (i_leftY >= SCREEN_HEIGHT)
            {
                i_clipH += (SCREEN_HEIGHT - (i_leftY + 1));
            }
        }

        _g.setClip(i_clipX, i_clipY, i_clipW, i_clipH);

        // Количество буфферных ячеек у теневой картинки
        int i_bufferOffsetX = 0, i_bufferOffsetY = 0;

        if (SCROLLUP)
        {
            i_bufferOffsetY += i_CellHeight;
        }

        if (SCROLLLEFT)
        {
            i_bufferOffsetX += i_CellWidth;
        }

        _g.drawImage(p_HiddenBuffer, _x - (i_ViewAreaX % i_CellWidth + i_bufferOffsetX), _y - (i_ViewAreaY % i_CellHeight + i_bufferOffsetY), null);
    }

    /**
     * Смещение буффера
     * 
     * @param _deltaXcells количество ячеек по X
     * @param _deltaYcells количество ячеек по Y
     */
    private final void _scrollHiddenBuffer(final int _deltaXcells, final int _deltaYcells)
    {
        final Graphics p_cGraphics = p_CopyBufferGraphics;
        final Graphics p_hBufferGraphics = p_HiddenBufferGraphics;
        final Image p_iBuffer = p_HiddenBuffer;
        final Image p_cBuffer = p_CopyBuffer;

        final int i_hiddenBufferWidth = i_HiddenBufferCellWidth * i_CellWidth;
        final int i_hiddenBufferHeight = i_HiddenBufferCellHeight * i_CellHeight;

        p_hBufferGraphics.setClip(0, 0, i_hiddenBufferWidth, i_hiddenBufferHeight);

        final int i_offsetZoneX = _deltaXcells * i_CellWidth;
        final int i_offsetZoneY = _deltaYcells * i_CellHeight;

        if (COPYPARTENABLE)
        {
            if (COPY_PARTS_NUMBER_HORZ == 1 && COPY_PARTS_NUMBER_VERT == 1)
            {
                // копруем через один блок один блок
                p_cGraphics.drawImage(p_iBuffer, 0, 0, null);
                // восстанавливаем
                p_hBufferGraphics.drawImage(p_cBuffer, i_offsetZoneX, i_offsetZoneY, null);
            }
            else
            {
                // выявляем направление скроллирования и переносим по частям
                // изображение буффера
                final int i_copyPartWidth = i_CopyBufferPartWidth;
                final int i_copyPartHeight = i_CopyBufferPartHeight;

                int i_startXcoord;
                int i_startYcoord;
                int i_xStep;
                int i_yStep;

                if (SCROLLUP || SCROLLDOWN)
                {
                    if (_deltaYcells > 0)
                    {
                        // скролл вниз
                        i_startYcoord = i_hiddenBufferHeight - i_copyPartHeight;
                        i_yStep = 0 - i_copyPartHeight;
                    }
                    else
                        if (_deltaYcells < 0)
                        {
                            // скролл вверх
                            i_startYcoord = 0;
                            i_yStep = i_copyPartHeight;
                        }
                        else
                        {
                            // нет вертикального скролла
                            i_startYcoord = 0;
                            i_yStep = i_copyPartHeight;
                        }
                }

                if (SCROLLLEFT || SCROLLRIGHT)
                {
                    if (_deltaXcells > 0)
                    {
                        // скролл вправо
                        i_startXcoord = i_hiddenBufferWidth - i_copyPartWidth;
                        i_xStep = 0 - i_copyPartWidth;
                    }
                    else
                        if (_deltaXcells < 0)
                        {
                            // скролл вверх
                            i_startXcoord = 0;
                            i_xStep = i_copyPartWidth;
                        }
                        else
                        {
                            // нет вертикального скролла
                            i_startXcoord = 0;
                            i_xStep = i_copyPartWidth;
                        }
                }

                int i_yNum = COPY_PARTS_NUMBER_VERT;
                while (i_yNum != 0)
                {
                    final int i_destY = i_startYcoord + i_offsetZoneY;

                    // проверяем видимость участка, который будет
                    // скопирован, что бы не отрисовывать его за
                    // пределами буффера
                    if (i_destY < i_hiddenBufferHeight && i_destY + i_copyPartHeight >= 0)
                    {
                        int i_xNum = COPY_PARTS_NUMBER_HORZ;
                        int i_sX = i_startXcoord;

                        final int i_copyY = 0 - i_startYcoord;

                        while (i_xNum != 0)
                        {
                            final int i_destX = i_sX + i_offsetZoneX;

                            // проверяем видимость участка, который будет
                            // скопирован, что бы не отрисовывать его за
                            // пределами буффера
                            if (i_destX < i_hiddenBufferWidth && i_destX + i_copyPartWidth >= 0)
                            {
                                // осуществляем копирование блока с буффера в
                                // буффер копирования
                                p_cGraphics.drawImage(p_iBuffer, 0 - i_sX, i_copyY, null);

                                // отрисовываем обратно со сдвигом
                                p_hBufferGraphics.drawImage(p_cBuffer, i_destX, i_destY, null);
                            }

                            i_sX += i_xStep;
                            i_xNum--;
                        }
                    }
                    i_startYcoord += i_yStep;
                    i_yNum--;
                }
            }
        }
        else
        {
            p_hBufferGraphics.copyArea(0, 0, i_hiddenBufferWidth, i_hiddenBufferHeight, i_offsetZoneX, i_offsetZoneY);
        }

        // отрисовываем полоску
        if (SCROLLLEFT || SCROLLRIGHT)
        {
            if (_deltaXcells > 0)
            {
                // Левая вертикальная полоска
                _fillHiddenBufferPart(0, 0, _deltaXcells, i_HiddenBufferCellHeight);
            }
            else
                if (_deltaXcells < 0)
                {
                    // восстанавливаем правую полоску
                    _fillHiddenBufferPart(i_HiddenBufferCellWidth + _deltaXcells, 0, 0 - _deltaXcells, i_HiddenBufferCellHeight);
                }
        }

        if (SCROLLDOWN || SCROLLUP)
        {
            if (_deltaYcells > 0)
            {
                // Верхняя горизонтальная полоса
                _fillHiddenBufferPart(0, 0, i_HiddenBufferCellWidth, _deltaYcells);
            }
            else
                if (_deltaYcells < 0)
                {
                    // Нижняя горизонтальная полоса
                    _fillHiddenBufferPart(0, i_HiddenBufferCellHeight + _deltaYcells, i_HiddenBufferCellWidth, 0 - _deltaYcells);
                }
        }
    }

    /**
     * Производится заливка областей, взятых по координатам в оптимизаторе
     * @param _g графический контент
     * @param _x координата X верхней левой точки зоны отображения 
     * @param _y координата Y верхней левой точки зоны отображения 
     * @param _optimizer оптимизатор
     */
    public final void directFillRegions(Graphics _g,int _x,int _y, DrawOptimizer _optimizer)
    {
        if (_optimizer.lg_PaintFullArea)
        {
            directPaint(_g, 0, 0, i_OutAreaWidth, i_OutAreaHeight);
            return;
        }

        int i_areasNumber = _optimizer.i_StackPointer;
        if (i_areasNumber==0) return;
        int i_areaPointer = 0;

        final int ARRWIDTH = i_TileArrayWidth;
        final int ARRHEIGHT = i_TileArrayHeight;

        final int[] ai_areasArray = _optimizer.AREA_ARRAY;

        final Image[] ap_separatedImages = ap_TileSetImages;
        final Image p_tileImage = p_TileSetImage;
        final short[] ash_tilearr = ash_TileArray;
        final int[] ai_tilearr = ai_TileArray;

        final int i_arrlen = ARRWIDTH*ARRHEIGHT;
        
        final int i_cellw = i_CellWidth;
        final int i_cellh = i_CellHeight;
        
        final int i_tlcx = i_ViewAreaTopCellX;
        final int i_tlcy = i_ViewAreaTopCellY;
        final int i_deltax = i_ViewAreaX % i_cellw;
        final int i_deltay = i_ViewAreaY % i_cellh;
        
        while (i_areasNumber != 0)
        {
            int i_areaCoordX = ai_areasArray[i_areaPointer++]+i_deltax;
            int i_areaCellX = i_areaCoordX / i_cellw;
            int i_areaOffsetX = i_areaCellX*i_cellw +_x-i_deltax;
            
            int i_areaCoordY = ai_areasArray[i_areaPointer++]+i_deltay;
            int i_areaCellY = i_areaCoordY / i_cellh;
            int i_areaOffsetY = i_areaCellY*i_cellh +_y-i_deltay;
            
            int i_areaCellsWidth = (i_areaCoordX+ai_areasArray[i_areaPointer++])/i_cellw;
            int i_areaCellsHeight = (i_areaCoordY+ai_areasArray[i_areaPointer++])/i_cellh;
            
            i_areaPointer += 4;

            i_areaCellsWidth -= i_areaCellX;
            i_areaCellsHeight -= i_areaCellY;
            
            i_areaCellsWidth++;
            i_areaCellsHeight++;
            
            i_areaCellX += i_tlcx;
            i_areaCellY += i_tlcy;
            
            int i_linecs = i_areaCellY*ARRWIDTH;

            if (CYCLED)
            {
                i_linecs %=  i_arrlen;
                if (i_linecs<0) i_linecs+=i_arrlen;
            }
            
            // Заполняем область
            while(i_areaCellsHeight!=0)
            {
                boolean lg_youtofbound = false;
                
                if (i_areaCellY<0 || i_areaCellY>=ARRHEIGHT) lg_youtofbound = true;
                
                int i_tmpAreaWdth = i_areaCellsWidth;
                int i_tmpX = i_areaOffsetX;
                int i_cellxindex = i_areaCellX;
                while(i_tmpAreaWdth!=0)
                {
                    int i_tileValue;
                    boolean lg_xoutofbound = false;
                    
                    lg_xoutofbound = i_cellxindex<0 || i_cellxindex>=ARRWIDTH;

                    if (CYCLED)
                    {
                        int i_xindex = i_cellxindex;
                        if (lg_xoutofbound)
                        {
                            i_xindex %= ARRWIDTH;
                            if (i_xindex<0) i_xindex+=ARRWIDTH;
                        }
                        
                        if (SHORTDATAARRAY)
                        {
                            i_tileValue = ash_tilearr[i_linecs+i_xindex] & 0xFFFF;
                        }
                        else
                        {
                            i_tileValue = ai_tilearr[i_linecs+i_xindex];
                        }
                    }
                    else
                    {
                        if (lg_youtofbound || lg_xoutofbound) 
                            i_tileValue = DEFAULTTILE;
                        else
                        {
                            if (SHORTDATAARRAY)
                                i_tileValue = ash_tilearr[i_linecs+i_cellxindex] & 0xFFFF;
                            else
                                i_tileValue = ai_tilearr[i_linecs+i_cellxindex];
                        }
                    }
                    
                    if (SEPARATEDIMAGES)
                    {
                        _g.drawImage(ap_separatedImages[i_tileValue],i_tmpX,i_areaOffsetY,null);
                    }
                    else
                    {
                        int i_imgCol, i_imgRow;
                        if (SHORTDATAARRAY)
                        {
                            i_imgCol = i_tileValue >>> 8;
                            i_imgRow = i_tileValue & 0xFF;
                        }
                        else
                        {
                            i_imgCol = i_tileValue >>> 16;
                            i_imgRow = i_tileValue & 0xFFFF;
                        }
                        _g.setClip(i_tmpX, i_areaOffsetY, i_cellw, i_cellh);
                        _g.setColor(Color.yellow);
                        _g.fillRect(i_tmpX,i_areaOffsetY, i_cellw, i_cellh);
                        _g.setColor(Color.black);
                        _g.drawRect(i_tmpX,i_areaOffsetY, i_cellw-1, i_cellh-1);
                        _g.drawImage(p_tileImage, i_tmpX - i_imgCol, i_areaOffsetY - i_imgRow, null);
                    }
                    
                    i_cellxindex++;
                    i_tmpX += i_cellw;
                    i_tmpAreaWdth--;
                }

                i_linecs += ARRWIDTH;
                
                if (CYCLED)
                {
                    i_linecs %=  i_arrlen;
                    if (i_linecs<0) i_linecs+=i_arrlen;
                }
                
                i_areaOffsetY += i_cellh;
                i_areaCellsHeight--;
            }
            
            i_areasNumber--;
        }
    }

    /**
     * Выставляет координаты XY отображаемой на экране области тайлового поля
     * @param _x координата X верхней левой точки
     * @param _y координата Y верхней левой точки
     */
    public final void setPositionXY(int _x, int _y)
    {
        int i_oldViewAreaX = i_ViewAreaX;
        int i_oldViewAreaY = i_ViewAreaY;

        i_ViewAreaX = _x;
        i_ViewAreaY = _y;

        final int CELLWIDTH = i_CellWidth;
        final int CELLHEIGHT = i_CellHeight;

        i_ViewAreaTopCellX = i_ViewAreaX / CELLWIDTH;
        i_ViewAreaTopCellY = i_ViewAreaY / CELLHEIGHT;

        if (DOUBLEBUFFER)
        {
            if (SCROLLUP)
                i_BufferAreaTopCellY = i_ViewAreaTopCellY - 1;
            else
                i_BufferAreaTopCellY = i_ViewAreaTopCellY;

            if (SCROLLLEFT)
                i_BufferAreaTopCellX = i_ViewAreaTopCellX - 1;
            else
                i_BufferAreaTopCellX = i_ViewAreaTopCellX;

            // проверка на переход границ ячейки

            // количество ячеек к смещению в буфере по вертикали
            int i_stepCellsVert = 0;

            // количество ячеек к смещению в буфере по горизонтали
            int i_stepCellsHorz = 0;

            if (SCROLLDOWN || SCROLLUP) i_stepCellsVert = i_oldViewAreaY / CELLHEIGHT - i_ViewAreaTopCellY;

            if (SCROLLLEFT || SCROLLRIGHT) i_stepCellsHorz = i_oldViewAreaX / CELLWIDTH - i_ViewAreaTopCellX;

            if (Math.abs(i_stepCellsHorz) >= i_HiddenBufferCellWidth || Math.abs(i_stepCellsVert) >= i_HiddenBufferCellHeight)
                _fillHiddenBufferPart(0, 0, i_HiddenBufferCellWidth, i_HiddenBufferCellHeight);
            else
            {
                if ((i_stepCellsHorz | i_stepCellsVert) != 0)
                {
                    _scrollHiddenBuffer(i_stepCellsHorz, i_stepCellsVert);
                }
            }
        }
    }
}

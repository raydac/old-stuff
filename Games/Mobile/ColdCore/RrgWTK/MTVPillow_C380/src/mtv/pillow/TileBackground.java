package mtv.pillow;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

/**
 * Класс реализует тайловый рендер
 *
 * @author Igor Maznitsa
 * @version 1.00
 */
public class TileBackground
{
    /**
     * Изображение, содержащее тайлы
     */
    private Image p_BlockImage;

    /**
     * Теневой буфер
     */
    private Image p_HiddenBuffer;

    /**
     * Теневой буфер, содержащий одну горизонтальную линию
     */
    private Image p_HorzLineBuffer;

    /**
     * Теневой буфер, содержащий одну вертикальную линию
     */
    private Image p_VertLineBuffer;

    /**
     * Графический контент теневого буфера
     */
    private Graphics p_HiddenBufferGraphics;

    /**
     * Графический контент буфера горизонтальной линии
     */
    private Graphics p_HorzLineBufferGaphics;

    /**
     * Графический контент буфера вертикальной линии
     */
    private Graphics p_VertLineBufferGraphics;

    /**
     * Указатель на массив, содержащий индексы тайлов
     */
    private byte[] ab_roomArray;

    /**
     * Ширина одного блока в пикселях
     */
    private int i_blockWidth;

    /**
     * Высота одного блока в пикселях
     */
    private int i_blockHeight;

    /**
     * Количество ячеек, умещающихся по ширине
     */
    private int i_cellWidthNumber;

    /**
     * Количество ячеек, умещающихся по высоте
     */
    private int i_cellHeightNumber;

    /**
     * Ширина одной строки в массиве индексов
     */
    private int i_roomArrayWidth;

    /**
     * Длина массива индексов
     */
    private int i_roomArrayLength;

    /**
     * Ширина отображаемой области
     */
    private int i_outAreaWdth;

    /**
     * Высота отображаемой области
     */
    private int i_outAreaHght;

    /**
     * Ширина теневого буфера
     */
    private int i_hiddenBufferWidth;

    /**
     * Высота теневого буфера
     */
    private int i_hiddenBufferHeight;

    /**
     * индекс X верхней левой отображаемой ячейки
     */
    private int i_cellTopX;

    /**
     * индекс Y верхней левой отображаемой ячейки
     */
    private int i_cellTopY;

    /**
     * Координата X верхней левой точки, с которой начинается отображение
     */
    private int i8_viewCoordX;

    /**
     * Координата Y верхней левой точки, с которой начинается отображение
     */
    private int i8_viewCoordY;

    /**
     * Выровненная координата X верхней левой ячейки на фоновом изображении
     */
    private int i_hiddenBufferCoordX;

    /**
     * Выровненная координата Y верхней левой ячейки на фоновом изображении
     */
    private int i_hiddenBufferCoordY;

    private final int[] ai_rowBlockImageOffsets = new int [16];
    private final int[] ai_colBlockImageOffsets = new int [16];


    /**
     * Освобождает ресурсы, взятые блоком
     */
    public final void release()
    {
        ab_roomArray = null;
        p_BlockImage = null;
        p_HiddenBufferGraphics = null;
        p_HorzLineBuffer = null;
        p_HorzLineBufferGaphics = null;
        p_VertLineBuffer = null;
        p_VertLineBufferGraphics = null;
        p_HiddenBuffer = null;
        Runtime.getRuntime().gc();
    }

    /**
     * Инициализация блока
     *
     * @param _width            ширина видимой части
     * @param _height           высота видимой части
     * @param _cellWidth        ширина ячейки
     * @param _cellHeight       высота ячейки
     * @param _makeDoubleBuffer флаг спользования теневой буферизации
     */
    public final void initTileBackground(int _width, int _height, int _cellWidth, int _cellHeight, boolean _makeDoubleBuffer)
    {
        i_outAreaHght = _height;
        i_outAreaWdth = _width;
        i_blockWidth = _cellWidth;
        i_blockHeight = _cellHeight;
        i_cellWidthNumber = (_width % _cellWidth == 0 ? _width / _cellWidth : ((_width + (_cellWidth - 1))) / _cellWidth);
        i_cellHeightNumber = (_height % _cellHeight == 0 ? _height / _cellHeight : ((_height + (_cellHeight - 1))) / _cellHeight);

        int i_x = 0;
        int i_y = 0;
        for (int li = 0; li < 16; li++)
        {
            ai_colBlockImageOffsets[li] = i_x;
            ai_rowBlockImageOffsets[li] = i_y;
            i_x += _cellWidth;
            i_y += _cellHeight;
        }

        if (_makeDoubleBuffer)
        {
            p_HiddenBuffer = Image.createImage(i_cellWidthNumber * _cellWidth, i_cellHeightNumber * _cellHeight);
            i_hiddenBufferHeight = p_HiddenBuffer.getHeight();
            i_hiddenBufferWidth = p_HiddenBuffer.getWidth();
            p_HiddenBufferGraphics = p_HiddenBuffer.getGraphics();

            p_HorzLineBuffer = Image.createImage(i_hiddenBufferWidth, _cellHeight);
            p_HorzLineBufferGaphics = p_HorzLineBuffer.getGraphics();

            p_VertLineBuffer = Image.createImage(_cellWidth, i_hiddenBufferHeight);
            p_VertLineBufferGraphics = p_VertLineBuffer.getGraphics();
        } else
        {
            i_hiddenBufferHeight = i_cellHeightNumber * _cellHeight;
            i_hiddenBufferWidth = i_cellWidthNumber * _cellWidth;
        }
    }

    /**
     * Отрисовать фон
     *
     * @param _g Graphics объект
     * @param _x Координата X верхнего левого угла
     * @param _y Координата Y верхнего левого угла
     */
    public final void drawBufferToGraphics(Graphics _g, int _x, int _y)
    {
        _g.setClip(_x, _y, i_outAreaWdth, i_outAreaHght);

        final int i_x = 0 - ((i8_viewCoordX >> 8) - i_hiddenBufferCoordX) + _x;
        final int i_y = 0 - ((i8_viewCoordY >> 8) - i_hiddenBufferCoordY) + _y;
        _g.drawImage(p_HiddenBuffer, i_x, i_y, 0);
    }

    /**
     * Задает массив, содержащий указатили на тайлы
     *
     * @param _roomWidth ширина массива
     * @param _roomArray массив
     */
    public final void setGameRoomArray(int _roomWidth, byte[] _roomArray)
    {
        i_roomArrayWidth = _roomWidth;
        i_roomArrayLength = _roomArray.length;
        ab_roomArray = _roomArray;
    }

    /**
     * Задает блок изображений, содержащих тайлы
     *
     * @param _image указатель на изображение
     */
    public final void setBlockImage(Image _image)
    {
        p_BlockImage = _image;
    }

    /**
     * Задает координаты смещения буффера
     *
     * @param _x8 координата X
     * @param _y8 координата Y
     */
    public final void setXY(int _x8, int _y8)
    {
        final int i_oldX = i8_viewCoordX >> 8;
        final int i_oldY = i8_viewCoordY >> 8;
        i8_viewCoordX = _x8;
        i8_viewCoordY = _y8;

        int i_x = _x8 >> 8;
        int i_y = _y8 >> 8;

        if ((i_oldX == i_x) && (i_oldY == i_y)) return;

        i_x -= i_blockWidth;
        i_y -= i_blockHeight;

        i_cellTopX = i_x / i_blockWidth;
        i_cellTopY = i_y / i_blockHeight;

        i_hiddenBufferCoordX = i_cellTopX * i_blockWidth;
        i_hiddenBufferCoordY = i_cellTopY * i_blockHeight;

        if (p_HiddenBuffer == null) return;
        fillAllHiddenBufferFromPoint();
    }

    private final void fillAllHiddenBufferFromPoint()
    {
        final int i_startXCellBlock = i_cellTopX;
        final int i_startYCellBlock = i_cellTopY;
        final int i_limitY = i_startYCellBlock + i_cellHeightNumber;
        final int i_limitX = i_startXCellBlock + i_cellWidthNumber;
        int i_pixelY = 0;
        int i_lineCS = i_startYCellBlock * i_roomArrayWidth;
        final int i_arrayLength = i_roomArrayLength;
        final byte[] ab_array = ab_roomArray;
        final int i_blockH = i_blockHeight;
        final int i_blockW = i_blockWidth;
        final int i_roomArrayW = i_roomArrayWidth;
        final Graphics p_hbg = p_HiddenBufferGraphics;
        final Image p_bimg = p_BlockImage;

        for (int ly = i_startYCellBlock; ly < i_limitY; ly++)
        {
            boolean lg_YoutOfBounds = false;

            if (i_lineCS >= i_arrayLength || i_lineCS < 0) lg_YoutOfBounds = true;
            int i_lineCellStart = i_lineCS;

            int i_pixelX = 0;
            i_lineCellStart += i_startXCellBlock;
            for (int lx = i_startXCellBlock; lx < i_limitX; lx++)
            {
                boolean lg_XoutOfBounds = false;
                if (lx < 0 || lx >= i_arrayLength) lg_XoutOfBounds = true;
                int i_blockNum;
                if (lg_XoutOfBounds || lg_YoutOfBounds)
                    i_blockNum = 0;
                else
                    i_blockNum = ab_array[i_lineCellStart] & 0xFF;

                int i_imgCol = i_blockNum >> 4;
                int i_imgRow = i_blockNum & 0xF;

                p_hbg.setClip(i_pixelX, i_pixelY, i_blockW, i_blockH);
                p_hbg.drawImage(p_bimg, i_pixelX - i_imgCol * i_blockW, i_pixelY - i_imgRow * i_blockH, 0);
                i_lineCellStart++;
                i_pixelX += i_blockW;
            }
            i_pixelY += i_blockH;
            i_lineCS += i_roomArrayW;
        }
    }

    /**
     * Отрисовка на канвас текущего положения картинки
     *
     * @param _g канвас
     * @param _x X координата начала вывода
     * @param _y Y координата начала вывода
     */
    public final void directPaint(Graphics _g, int _x, int _y)
    {
        final int i_startX = i8_viewCoordX >> 8;
        final int i_startY = i8_viewCoordY >> 8;

        final int i_startXCellBlock = i_cellTopX;
        final int i_startYCellBlock = i_cellTopY;
        final int i_limitY = i_startYCellBlock + i_cellHeightNumber+1;
        final int i_limitX = i_startXCellBlock + i_cellWidthNumber+1;

        final int i_arrayLength = i_roomArrayLength;
        final Image p_bimg = p_BlockImage;
        final byte[] ab_array = ab_roomArray;

        final int i_blockH = i_blockHeight;
        final int i_blockW = i_blockWidth;

        final int i_roomArrayW = i_roomArrayWidth;

        int i_lineCS = i_startYCellBlock * i_roomArrayW;

        final int i_idealX = i_startXCellBlock * i_blockW;
        final int i_idealY = i_startYCellBlock * i_blockH;

        final int i_yoffset = 0 - (i_startY - i_idealY);
        final int i_xoffset = 0 - (i_startX - i_idealX);

        int i_pixelY = i_yoffset + _y;

        final int i_pxX = i_xoffset + _x;

        final int[] ai_xoffsets = ai_colBlockImageOffsets;
        final int[] ai_yoffsets = ai_rowBlockImageOffsets;

        for (int ly = i_cellTopY; ly <= i_limitY; ly++)
        {
            boolean lg_YoutOfBounds = false;

            if (i_lineCS >= i_arrayLength || i_lineCS < 0) lg_YoutOfBounds = true;
            int i_lineCellStart = i_lineCS;

            int i_pixelX = i_pxX;
            i_lineCellStart += i_startXCellBlock;
            for (int lx = i_startXCellBlock; lx <= i_limitX; lx++)
            {
                boolean lg_XoutOfBounds = false;
                if (lx < 0 || lx >= i_roomArrayW) lg_XoutOfBounds = true;
                int i_blockNum;
                if (lg_XoutOfBounds || lg_YoutOfBounds)
                    i_blockNum = 0;
                else
                {
                    i_blockNum = ab_array[i_lineCellStart] & 0xFF;
                }

                int i_blockC = i_blockNum >>> 4;
                int i_blockR = i_blockNum & 0xF;

                final int i_imgCol = ai_xoffsets[i_blockC];
                final int i_imgRow = ai_yoffsets[i_blockR];

                _g.setClip(i_pixelX, i_pixelY, i_blockW, i_blockH);
                _g.drawImage(p_bimg, i_pixelX - i_imgCol, i_pixelY - i_imgRow, 0);
                i_lineCellStart++;
                i_pixelX += i_blockW;
            }
            i_pixelY += i_blockH;
            i_lineCS += i_roomArrayW;
        }
    }

    private final void drawHorizontalLineOnHiddenBuffer(int _y, int _cellOffsetY)
    {
        final int i_startXCellBlock = i_cellTopX;
        final int i_limitX = i_startXCellBlock + i_cellWidthNumber;

        boolean lg_YoutOfBounds = false;
        int i_lineCellStart = _cellOffsetY * i_roomArrayWidth;
        if (i_lineCellStart >= i_roomArrayLength || i_lineCellStart < 0) lg_YoutOfBounds = true;
        int i_pixelX = 0;
        i_lineCellStart += i_startXCellBlock;
        final byte[] ab_array = ab_roomArray;
        final Graphics p_hg = p_HiddenBufferGraphics;
        final Image p_bimg = p_BlockImage;
        final int i_bw = i_blockWidth;
        final int i_bh = i_blockHeight;
        final int i_arrayW = i_roomArrayWidth;

        final int[] ai_xoffsets = ai_colBlockImageOffsets;
        final int[] ai_yoffsets = ai_rowBlockImageOffsets;

        for (int lx = i_startXCellBlock; lx < i_limitX; lx++)
        {
            boolean lg_XoutOfBounds = false;
            if (lx < 0 || lx >= i_arrayW) lg_XoutOfBounds = true;
            int i_blockNum;
            if (lg_XoutOfBounds || lg_YoutOfBounds)
                i_blockNum = 0;
            else
                i_blockNum = ab_array[i_lineCellStart] & 0xFF;

            final int i_imgCol = ai_xoffsets[i_blockNum >> 4];
            final int i_imgRow = ai_yoffsets[i_blockNum & 0xF];

            p_hg.setClip(i_pixelX, _y, i_bw, i_bh);
            p_hg.drawImage(p_bimg, i_pixelX - i_imgCol, _y - i_imgRow, 0);
            i_lineCellStart++;
            i_pixelX += i_bw;
        }
    }

    /**
         * Скроллирует теневой буфер вертикально на одну ячейку
         *
         * @param _up true если вверх иначе false
         */
    private final void scrollVerticalHiddenBuffer(boolean _up)
    {
        p_HiddenBufferGraphics.setClip(0, 0, i_hiddenBufferWidth, i_hiddenBufferHeight);

        final Graphics p_gr = p_HorzLineBufferGaphics;
        final Graphics p_grh = p_HiddenBufferGraphics;
        final int i_cellHN = i_cellHeightNumber;
        final Image p_hb = p_HiddenBuffer;
        final Image p_hlb = p_HorzLineBuffer;
        final int i_bh = i_blockHeight;
        final int i_hbh = i_hiddenBufferHeight;

        if (_up)
        {
            i_cellTopY++;
            int i_yofst = 0 - i_bh;
            int i_hyoffst = 0;
            for (int ly = 1; ly < i_cellHN; ly++)
            {
                p_gr.drawImage(p_hb, 0, i_yofst, 0);
                p_grh.drawImage(p_hlb, 0, i_hyoffst, 0);
                i_yofst -= i_bh;
                i_hyoffst += i_bh;
            }
            drawHorizontalLineOnHiddenBuffer(i_hyoffst, i_cellTopY + i_cellHN - 1);
        } else
        {
            i_cellTopY--;
            int i_yofst = 0 - (i_hbh - (i_bh << 1));
            int i_hyoffst = i_hbh - i_bh;
            int ly = i_cellHN - 1;
            while (ly != 0)
            {
                p_gr.drawImage(p_hb, 0, i_yofst, 0);
                p_grh.drawImage(p_hlb, 0, i_hyoffst, 0);
                i_yofst += i_bh;
                i_hyoffst -= i_bh;

                ly--;
            }
            drawHorizontalLineOnHiddenBuffer(0, i_cellTopY);
        }
    }

    /**
     * Скроллирует теневой буфер горизонтально на одну ячейку
     *
     * @param _left true если влево иначе false
     */
    private final void scrollHorizontalHiddenBuffer(boolean _left)
    {
        p_HiddenBufferGraphics.setClip(0, 0, i_hiddenBufferWidth, i_hiddenBufferHeight);

        final int i_cellWN = i_cellWidthNumber;
        final Graphics p_vlbg = p_VertLineBufferGraphics;
        final Graphics p_hbg = p_HiddenBufferGraphics;
        final int i_bw = i_blockWidth;
        final Image p_hb = p_HiddenBuffer;
        final Image p_vlb = p_VertLineBuffer;
        final int i_hbw = i_hiddenBufferWidth;

        if (_left)
        {
            i_cellTopX++;
            int i_xofst = 0 - i_bw;
            int i_hxoffst = 0;
            for (int lx = 1; lx < i_cellWN; lx++)
            {
                p_vlbg.drawImage(p_hb, i_xofst, 0, 0);
                p_hbg.drawImage(p_vlb, i_hxoffst, 0, 0);
                i_xofst -= i_bw;
                i_hxoffst += i_bw;
            }
            drawVerticalLineOnHiddenBuffer(i_hxoffst, i_cellTopX + i_cellWidthNumber - 1);
        } else
        {
            i_cellTopX--;
            int i_xofst = 0 - (i_hbw - (i_bw << 1));
            int i_hxoffst = i_hbw - i_bw;

            int lx = i_cellWN - 1;

            while (lx != 0)
            {
                p_vlbg.drawImage(p_hb, i_xofst, 0, 0);
                p_hbg.drawImage(p_vlb, i_hxoffst, 0, 0);
                i_xofst += i_bw;
                i_hxoffst -= i_bw;

                lx--;
            }
            drawVerticalLineOnHiddenBuffer(0, i_cellTopX);
        }
    }

    /**
     * Получить смещение в массиве до ячейки отображаемой в координатах
     *
     * @param _x координата X точки
     * @param _y координата Y точки
     * @return сещение в массиве до элемента отображаемого в координатах
     */
    public final int getCellIndexForXY(int _x, int _y)
    {
        final int i_hbcx = i_hiddenBufferCoordX;
        final int i_hbcy = i_hiddenBufferCoordY;
        final int i_vpx = i8_viewCoordX >> 8;
        final int i_vpy = i8_viewCoordY >> 8;

        final int i_cx = ((i_vpx - i_hbcx) + _x) / i_blockWidth;
        final int i_cy = ((i_vpy - i_hbcy) + _y) / i_blockHeight;

        final int i_raw = i_roomArrayWidth;

        if (i_cx < 0 || i_cx >= i_raw) return -1;
        final int i_offst = i_cy * i_raw + i_cx;
        if (i_offst < 0 || i_offst >= i_roomArrayLength) return -1;
        return i_offst;
    }

    /**
     * Проверка видимости ячейки на экране
     *
     * @param _cellX8 координата X ячейки
     * @param _cellY8 координата Y ячейки
     * @return true если ячейка видна, иначе false
     */
    public final boolean isCellVisible(int _cellX8, int _cellY8)
    {
        final int i8_xcoords = _cellX8 * i_blockWidth;
        final int i8_ycoords = _cellY8 * i_blockHeight;

        final int i8_vcx = i8_viewCoordX;
        final int i8_vcy = i8_viewCoordY;

        final int i8_bw = i_blockWidth << 8;
        final int i8_bh = i_blockHeight << 8;

        if ((i8_vcx - i8_xcoords) >= i8_bw || (i8_xcoords >= i8_viewCoordX + (i_outAreaWdth << 8))) return false;
        if ((i8_vcy - i8_ycoords) >= i8_bh || (i8_ycoords >= i8_viewCoordY + (i_outAreaHght << 8))) return false;

        return true;
    }

    private final void drawVerticalLineOnHiddenBuffer(int _x, int _XOffset)
    {
        final int i_startYCellBlock = i_cellTopY;
        final int i_limitY = i_startYCellBlock + i_cellHeightNumber;

        boolean lg_XoutOfBounds = false;
        int i_lineCellStart = i_startYCellBlock * i_roomArrayWidth + _XOffset;
        if (_XOffset < 0 || _XOffset >= i_roomArrayWidth) lg_XoutOfBounds = true;

        int i_pixelY = 0;

        final int i_bw = i_blockWidth;
        final int i_bh = i_blockHeight;
        final Image p_imgb = p_BlockImage;
        final int i_raw = i_roomArrayWidth;
        final byte[] ab_array = ab_roomArray;
        final int i_ral = i_roomArrayLength;
        final Graphics p_hbg = p_HiddenBufferGraphics;

        final int[] ai_xoffsets = ai_colBlockImageOffsets;
        final int[] ai_yoffsets = ai_rowBlockImageOffsets;


        for (int ly = i_startYCellBlock; ly < i_limitY; ly++)
        {
            boolean lg_YoutOfBounds = false;
            if (i_lineCellStart >= i_ral || i_lineCellStart < 0) lg_YoutOfBounds = true;
            int i_blockNum;
            if (lg_XoutOfBounds || lg_YoutOfBounds)
                i_blockNum = 0;
            else
                i_blockNum = ab_array[i_lineCellStart] & 0xFF;

            int i_imgCol = ai_xoffsets[i_blockNum >> 4];
            int i_imgRow = ai_yoffsets[i_blockNum & 0xF];

            p_hbg.setClip(_x, i_pixelY, i_bw, i_bh);
            p_hbg.drawImage(p_imgb, _x - i_imgCol, i_pixelY - i_imgRow, 0);
            i_lineCellStart += i_raw;
            i_pixelY += i_bh;
        }
    }

    /**
     * Скроллировать горизонтально отображаемую область на заданное количество шагов в формате фиксированная точка 8 разрядов
     *
     * @param _step8 шаг в формате с фиксированной точкой в 8 разрядов
     */
    public final void scrollHorizontal(int _step8)
    {
        final int i_oldX = i8_viewCoordX >> 8;
        i8_viewCoordX += _step8;

        final int i_x = i8_viewCoordX >> 8;

        if (i_x == i_oldX) return;

        final int i_hbcx = i_hiddenBufferCoordX;

        if (_step8 < 0)
        {
            if (i_x < i_hbcx)
            {
                i_hiddenBufferCoordX -= i_blockWidth;
                if (p_HiddenBuffer != null)
                    scrollHorizontalHiddenBuffer(false);
                else
                    i_cellTopX--;
            }
        } else
        {
            if (i_x + i_outAreaWdth - i_hbcx > i_hiddenBufferWidth)
            {
                i_hiddenBufferCoordX += i_blockWidth;
                if (p_HiddenBuffer != null)
                    scrollHorizontalHiddenBuffer(true);
                else
                    i_cellTopX++;
            }
        }
    }

    /**
     * Скроллировать вертикально отображаемую область на заданное количество шагов в формате фиксированная точка 8 разрядов
     *
     * @param _step8 шаг в формате с фиксированной точкой в 8 разрядов
     */
    public final void scrollVertical(int _step8)
    {
        final int i_oldY = i8_viewCoordY >> 8;
        i8_viewCoordY += _step8;

        final int i_y = i8_viewCoordY >> 8;

        if (i_oldY == i_y) return;

        final int i_hbcy = i_hiddenBufferCoordY;
        final int i_bh = i_blockHeight;

        if (_step8 < 0)
        {
            if (i_y < i_hbcy)
            {
                i_hiddenBufferCoordY -= i_bh;
                if (p_HiddenBuffer != null)
                    scrollVerticalHiddenBuffer(false);
                else
                    i_cellTopY--;
            }
        } else
        {
            if (i_y + i_outAreaHght - i_hbcy > i_hiddenBufferHeight)
            {
                i_hiddenBufferCoordY += i_bh;
                if (p_HiddenBuffer != null)
                    scrollVerticalHiddenBuffer(true);
                else
                    i_cellTopY++;
            }
        }
    }
}

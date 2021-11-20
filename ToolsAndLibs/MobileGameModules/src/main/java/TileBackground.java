
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

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
    private int i8_outAreaWdth;

    /**
     * Высота отображаемой области
     */
    private int i8_outAreaHght;

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

    /**
     * Объект, позволяющий блокировать операции в момент вывода на экран контента
     */
    private Object p_syncObject = new Object();

    /**
     * Количество изображений в тайловом изображении по ширине
     */
    private int i_BlockImageHorzTilesNum;

    /**
     * Освобождает ресурсы, взятые блоком
     */
    public void release()
    {
        synchronized (p_HiddenBuffer)
        {
            ab_roomArray = null;
            p_BlockImage = null;
            p_HiddenBufferGraphics = null;
            p_HorzLineBuffer = null;
            p_HorzLineBufferGaphics = null;
            p_VertLineBuffer = null;
            p_VertLineBufferGraphics = null;
        }
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
    public void initTileBackground(int _width, int _height, int _cellWidth, int _cellHeight, boolean _makeDoubleBuffer)
    {
        i8_outAreaHght = _height << 8;
        i8_outAreaWdth = _width << 8;
        i_blockWidth = _cellWidth;
        i_blockHeight = _cellHeight;
        i_cellWidthNumber = (_width % _cellWidth == 0 ? _width / _cellWidth : (_width + (_cellWidth - 1)) / _cellWidth) + 1;
        i_cellHeightNumber = (_height % _cellHeight == 0 ? _height / _cellHeight : (_height + (_cellHeight - 1)) / _cellHeight) + 1;

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
        }
}

    /**
     * Отрисовать фон
     *
     * @param _g Graphics объект
     * @param _x Координата X верхнего левого угла
     * @param _y Координата Y верхнего левого угла
     */
    public void drawBufferToGraphics(Graphics _g, int _x, int _y)
    {
        synchronized (p_syncObject)
        {
            _g.setClip(_x, _y, i8_outAreaWdth >> 8, i8_outAreaHght >> 8);

            int i_x = 0 - ((i8_viewCoordX >> 8) - i_hiddenBufferCoordX) + _x;
            int i_y = 0 - ((i8_viewCoordY >> 8) - i_hiddenBufferCoordY) + _y;
            _g.drawImage(p_HiddenBuffer, i_x, i_y, 0);
        }
}

    /**
     * Задает массив, содержащий указатили на тайлы
     *
     * @param _roomWidth ширина массива
     * @param _roomArray массив
     */
    public void setGameRoomArray(int _roomWidth, byte[] _roomArray)
    {
        synchronized (p_syncObject)
        {
            i_roomArrayWidth = _roomWidth;
            i_roomArrayLength = _roomArray.length;
            ab_roomArray = _roomArray;
        }
}

    /**
     * Задает блок изображений, содержащих тайлы
     *
     * @param _image указатель на изображение
     */
    public void setBlockImage(Image _image, int _cols)
    {
        synchronized (p_syncObject)
        {
            p_BlockImage = _image;
            i_BlockImageHorzTilesNum = _cols;
        }
}

    /**
     * Задает координаты смещения буффера
     *
     * @param _x8 координата X
     * @param _y8 координата Y
     */
    public void setXY(int _x8, int _y8)
    {
        synchronized (p_syncObject)
        {
            int i_oldX = i8_viewCoordX >> 8;
            int i_oldY = i8_viewCoordY >> 8;
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
}

    private void fillAllHiddenBufferFromPoint()
    {
        int i_startXCellBlock = i_cellTopX;
        int i_startYCellBlock = i_cellTopY;
        int i_limitY = i_startYCellBlock + i_cellHeightNumber;
        int i_limitX = i_startXCellBlock + i_cellWidthNumber;
        int i_pixelY = 0;
        int i_lineCS = i_startYCellBlock * i_roomArrayWidth;
        int i_arrayLength = i_roomArrayLength;
        byte[] ab_array = ab_roomArray;
        int i_blockH = i_blockHeight;
        int i_blockW = i_blockWidth;
        int i_roomArrayW = i_roomArrayWidth;
        Graphics p_hbg = p_HiddenBufferGraphics;
        Image p_bimg = p_BlockImage;

        int i_bimgW = i_BlockImageHorzTilesNum;

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

                int i_imgCol = i_blockNum % i_bimgW;
                int i_imgRow = i_blockNum / i_bimgW;

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
     * @param _g канвас
     * @param _x X координата начала вывода
     * @param _y Y координата начала вывода
     */
    public void drawDirectOnGraphics(Graphics _g, int _x, int _y)
    {
        synchronized (p_syncObject)
        {
            int i_startX = i8_viewCoordX >> 8;
            int i_startY = i8_viewCoordY >> 8;
            _g.setClip(_x, _y, i8_outAreaWdth >> 8, i8_outAreaHght >> 8);
            int i_pixelY = 0 - i_startY + _y;

            int i_startXCellBlock = i_cellTopX;
            int i_startYCellBlock = i_cellTopY;
            int i_limitY = i_startYCellBlock + i_cellHeightNumber;
            int i_limitX = i_startXCellBlock + i_cellWidthNumber;

            int i_lineCS = i_startYCellBlock * i_roomArrayWidth;
            int i_arrayLength = i_roomArrayLength;
            Image p_bimg = p_BlockImage;
            byte[] ab_array = ab_roomArray;
            int i_bimgW = i_BlockImageHorzTilesNum;

            int i_blockH = i_blockHeight;
            int i_blockW = i_blockWidth;

            int i_roomArrayW = i_roomArrayWidth;

            for (int ly = i_cellTopY; ly < i_limitY; ly++)
            {
                boolean lg_YoutOfBounds = false;

                if (i_lineCS >= i_arrayLength || i_lineCS < 0) lg_YoutOfBounds = true;
                int i_lineCellStart = i_lineCS;

                int i_pixelX = 0 - i_startX + _x;
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

                    int i_imgCol = i_blockNum % i_bimgW;
                    int i_imgRow = i_blockNum / i_bimgW;

                    _g.setClip(i_pixelX, i_pixelY, i_blockW, i_blockH);
                    _g.drawImage(p_bimg, i_pixelX - i_imgCol * i_blockW, i_pixelY - i_imgRow * i_blockH, 0);
                    i_lineCellStart++;
                    i_pixelX += i_blockW;
                }
                i_pixelY += i_blockH;
                i_lineCS += i_roomArrayW;
            }
        }
}

    private void drawHorizontalLineOnHiddenBuffer(int _y, int _cellOffsetY)
    {
        int i_startXCellBlock = i_cellTopX;
        int i_limitX = i_startXCellBlock + i_cellWidthNumber;

        boolean lg_YoutOfBounds = false;
        int i_lineCellStart = _cellOffsetY * i_roomArrayWidth;
        if (i_lineCellStart >= i_roomArrayLength || i_lineCellStart < 0) lg_YoutOfBounds = true;
        int i_pixelX = 0;
        i_lineCellStart += i_startXCellBlock;
        byte[] ab_array = ab_roomArray;
        Graphics p_hg = p_HiddenBufferGraphics;
        Image p_bimg = p_BlockImage;
        int i_bw = i_blockWidth;
        int i_bh = i_blockHeight;
        int i_arrayW = i_roomArrayWidth;
        int i_bimgW = i_BlockImageHorzTilesNum;

        for (int lx = i_startXCellBlock; lx < i_limitX; lx++)
        {
            boolean lg_XoutOfBounds = false;
            if (lx < 0 || lx >= i_arrayW) lg_XoutOfBounds = true;
            int i_blockNum;
            if (lg_XoutOfBounds || lg_YoutOfBounds)
                i_blockNum = 0;
            else
                i_blockNum = ab_array[i_lineCellStart] & 0xFF;

            int i_imgCol = i_blockNum % i_bimgW;
            int i_imgRow = i_blockNum / i_bimgW;

            p_hg.setClip(i_pixelX, _y, i_bw, i_bh);
            p_hg.drawImage(p_bimg, i_pixelX - i_imgCol * i_bw, _y - i_imgRow * i_bh, 0);
            i_lineCellStart++;
            i_pixelX += i_bw;
        }
}

    /**
     * Скроллирует теневой буфер вертикально на одну ячейку
     *
     * @param _up true если вверх иначе false
     */
    private void scrollVerticalHiddenBuffer(boolean _up)
    {
        p_HiddenBufferGraphics.setClip(0, 0, i_hiddenBufferWidth, i_hiddenBufferHeight);

        Graphics p_gr = p_HorzLineBufferGaphics;
        Graphics p_grh = p_HiddenBufferGraphics;
        int i_cellHN = i_cellHeightNumber;
        Image p_hb = p_HiddenBuffer;
        Image p_hlb = p_HorzLineBuffer;
        int i_bh = i_blockHeight;
        int i_hbh = i_hiddenBufferHeight;

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
            drawHorizontalLineOnHiddenBuffer(i_hyoffst, i_cellTopY + i_cellHeightNumber - 1);
        }
        else
        {
            i_cellTopY--;
            int i_yofst = 0 - (i_hbh - (i_bh << 1));
            int i_hyoffst = i_hbh - i_bh;
            for (int ly = i_cellHN - 1; ly > 0; ly--)
            {
                p_gr.drawImage(p_hb, 0, i_yofst, 0);
                p_grh.drawImage(p_hlb, 0, i_hyoffst, 0);
                i_yofst += i_bh;
                i_hyoffst -= i_bh;
            }
            drawHorizontalLineOnHiddenBuffer(0, i_cellTopY);
        }
}

    /**
     * Скроллирует теневой буфер горизонтально на одну ячейку
     *
     * @param _left true если влево иначе false
     */
    private void scrollHorizontalHiddenBuffer(boolean _left)
    {
        p_HiddenBufferGraphics.setClip(0, 0, i_hiddenBufferWidth, i_hiddenBufferHeight);

        int i_cellWN = i_cellWidthNumber;
        Graphics p_vlbg = p_VertLineBufferGraphics;
        Graphics p_hbg = p_HiddenBufferGraphics;
        int i_bw = i_blockWidth;
        Image p_hb = p_HiddenBuffer;
        Image p_vlb = p_VertLineBuffer;
        int i_hbw = i_hiddenBufferWidth;

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
        }
        else
        {
            i_cellTopX--;
            int i_xofst = 0 - (i_hbw - (i_bw << 1));
            int i_hxoffst = i_hbw - i_bw;
            for (int lx = i_cellWN - 1; lx > 0; lx--)
            {
                p_vlbg.drawImage(p_hb, i_xofst, 0, 0);
                p_hbg.drawImage(p_vlb, i_hxoffst, 0, 0);
                i_xofst += i_bw;
                i_hxoffst -= i_bw;
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
    public int getCellIndexForXY(int _x, int _y)
    {
        synchronized (p_syncObject)
        {
            int i_hbcx = i_hiddenBufferCoordX;
            int i_hbcy = i_hiddenBufferCoordY;
            int i_vpx = i8_viewCoordX >> 8;
            int i_vpy = i8_viewCoordY >> 8;

            int i_cx = ((i_vpx - i_hbcx) + _x) / i_blockWidth;
            int i_cy = ((i_vpy - i_hbcy) + _y) / i_blockHeight;

            int i_raw = i_roomArrayWidth;

            if (i_cx < 0 || i_cx >= i_raw) return -1;
            int i_offst = i_cy * i_raw + i_cx;
            if (i_offst < 0 || i_offst >= i_roomArrayLength) return -1;
            return i_offst;
        }
}

    /**
     * Проверка видимости ячейки на экране
     *
     * @param _cellX8 координата X ячейки
     * @param _cellY8 координата Y ячейки
     * @return true если ячейка видна, иначе false
     */
    public boolean isCellVisible(int _cellX8, int _cellY8)
    {
        int i8_xcoords = _cellX8 * i_blockWidth;
        int i8_ycoords = _cellY8 * i_blockHeight;

        int i8_vcx = i8_viewCoordX;
        int i8_vcy = i8_viewCoordY;

        int i8_bw = i_blockWidth << 8;
        int i8_bh = i_blockHeight << 8;

        if ((i8_vcx - i8_xcoords) >= i8_bw || (i8_xcoords >= i8_viewCoordX + i8_outAreaWdth)) return false;
        if ((i8_vcy - i8_ycoords) >= i8_bh || (i8_ycoords >= i8_viewCoordY + i8_outAreaHght)) return false;

        return true;
}

    private void drawVerticalLineOnHiddenBuffer(int _x, int _XOffset)
    {
        int i_startYCellBlock = i_cellTopY;
        int i_limitY = i_startYCellBlock + i_cellHeightNumber;

        boolean lg_XoutOfBounds = false;
        int i_lineCellStart = i_startYCellBlock * i_roomArrayWidth + _XOffset;
        if (_XOffset < 0 || _XOffset >= i_roomArrayWidth) lg_XoutOfBounds = true;

        int i_pixelY = 0;

        int i_bimgW = i_BlockImageHorzTilesNum;
        int i_bw = i_blockWidth;
        int i_bh = i_blockHeight;
        Image p_imgb = p_BlockImage;
        int i_raw = i_roomArrayWidth;
        byte[] ab_array = ab_roomArray;
        int i_ral = i_roomArrayLength;
        Graphics p_hbg = p_HiddenBufferGraphics;

        for (int ly = i_startYCellBlock; ly < i_limitY; ly++)
        {
            boolean lg_YoutOfBounds = false;
            if (i_lineCellStart >= i_ral || i_lineCellStart < 0) lg_YoutOfBounds = true;
            int i_blockNum;
            if (lg_XoutOfBounds || lg_YoutOfBounds)
                i_blockNum = 0;
            else
                i_blockNum = ab_array[i_lineCellStart] & 0xFF;

            int i_imgCol = i_blockNum % i_bimgW;
            int i_imgRow = i_blockNum / i_bimgW;

            p_hbg.setClip(_x, i_pixelY, i_bw, i_bh);
            p_hbg.drawImage(p_imgb, _x - i_imgCol * i_bw, i_pixelY - i_imgRow * i_bh, 0);
            i_lineCellStart += i_raw;
            i_pixelY += i_bh;
        }
}

    /**
     * Скроллировать горизонтально отображаемую область на заданное количество шагов в формате фиксированная точка 8 разрядов
     *
     * @param _step8 шаг в формате с фиксированной точкой в 8 разрядов
     */
    public void scrollHorizontal(int _step8)
    {
        synchronized (p_syncObject)
        {
            int i_oldX = i8_viewCoordX >> 8;
            i8_viewCoordX += _step8;
            int i_x = i8_viewCoordX >> 8;

            if (i_x == i_oldX) return;

            int i_hbcx = i_hiddenBufferCoordX;

            if (_step8 < 0)
            {
                if (i_x < i_hbcx)
                {
                    i_hiddenBufferCoordX -= i_blockWidth;
                    scrollHorizontalHiddenBuffer(false);
                }
            }
            else
            {
                if ((i8_viewCoordX + i8_outAreaWdth) - i_hbcx > i_hiddenBufferWidth)
                {
                    i_hiddenBufferCoordX += i_blockWidth;
                    scrollHorizontalHiddenBuffer(true);
                }
            }
        }
}

    /**
     * Скроллировать вертикально отображаемую область на заданное количество шагов в формате фиксированная точка 8 разрядов
     *
     * @param _step8 шаг в формате с фиксированной точкой в 8 разрядов
     */
    public void scrollVertical(int _step8)
    {
        synchronized (p_syncObject)
        {
            int i_oldY = i8_viewCoordY >> 8;
            i8_viewCoordY += _step8;
            int i_y = i8_viewCoordY >> 8;

            if (i_oldY == i_y) return;

            int i_hbcy = i_hiddenBufferCoordY;
            int i_bh = i_blockHeight;

            if (_step8 < 0)
            {
                if (i_y < i_hbcy)
                {
                    i_hiddenBufferCoordY -= i_bh;
                    scrollVerticalHiddenBuffer(false);
                }
            }
            else
            {
                if ((i8_viewCoordY + i8_outAreaHght) - i_hbcy > i_hiddenBufferHeight)
                {
                    i_hiddenBufferCoordY += i_bh;
                    scrollVerticalHiddenBuffer(true);
                }
            }
        }
}
}

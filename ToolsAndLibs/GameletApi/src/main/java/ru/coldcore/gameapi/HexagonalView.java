package ru.coldcore.gameapi;

import java.awt.Graphics;

/**
 * Класс, позволяющий отобразить массив в виде гексагонального поля
 */
public class HexagonalView
{
    protected int i_mapblockgapx;
    protected int i_mapblockgapy;
    protected int i_mapblockstaggerx;
    protected int i_mapblockstaggery;
    protected int i_mapblockwidth;
    protected int i_mapblockheight;
    protected boolean lg_horizontal;

    /**
     * Конструктор
     * @param _width ширина ячейки
     * @param _height высота ячейки
     * @param _horizontal флаг, показывает, что ячейки отрисовываются горизонтально, иначе вертикально
     */
    public HexagonalView(int _width,int _height,boolean _horizontal)
    {
        lg_horizontal = _horizontal;

        i_mapblockwidth = _width;
        if (lg_horizontal)
        {
            i_mapblockgapx = _width + (_width>>1);
            i_mapblockstaggerx = _width - (_width>>2);
        }
        else
        {
            i_mapblockgapx = _width;
            i_mapblockstaggerx = _width>>1;
        }

        i_mapblockheight = _height;

        if (lg_horizontal)
        {
            i_mapblockgapy = _height;
            i_mapblockstaggery = _height>>1;
        }
        else
        {
            i_mapblockgapy = _height + (_height>>1);
            i_mapblockstaggery = _height - (_height>>2);
        }
    }

    /**
     * Конвертация координат в 2D массиве в экранные координаты
     * @param _x координата X в массиве
     * @param _y координата Y в массиве
     * @return упакованное значение (short)X<<16 | (short)Y
     */
    public final int convertBlockToPixelCoords(int _x, int _y)
    {
            int i_px = _x * i_mapblockgapx;
            int i_py = (_y >> 1) * i_mapblockgapy;

            if ((_y & 1)!=0)
            {
                i_px -= i_mapblockstaggerx;
                i_py -= i_mapblockstaggery;
            }

            i_px += (i_mapblockwidth >> 1);
            i_py += (i_mapblockheight >> 1);

            return (i_px<<16 | (i_py & 0xFFFF));
    }

    /**
     * Отобразить массив на канвас в заданных координатах
     * @param _g канвас
     * @param _x X координата вывода поля
     * @param _y Y координата вывода поля
     * @param _array одномерный массив, содержащй значения
     * @param _arrayWidth ширина строки в массиве
     */
    public final void drawArray(Graphics _g, int _x, int _y, byte [] _array, int _arrayWidth)
    {
        int i_height = _array.length / _arrayWidth;

        final int i_mbwhalf = i_mapblockwidth>>1;
        final int i_mbhhalf = i_mapblockheight>>1;

        final int i_mbgapx = i_mapblockgapx;
        final int i_mbgapy = i_mapblockgapy;

        final int i_maptaggerx = i_mapblockstaggerx;
        final int i_maptaggery = i_mapblockstaggery;
        
        int i_index = 0;
        for(int ly=0;ly<i_height;ly++)
        {
            boolean lg_yoffst = (ly & 1)!=0;

            int i_py = (ly >> 1) * i_mbgapy;
            if (lg_yoffst) i_py -= i_maptaggery;
            i_py += i_mbhhalf;
            int i_y = i_py+_y;
            
            for(int lx=0;lx<_arrayWidth;lx++)
            {
                int i_px = lx * i_mbgapx;

                if (lg_yoffst)
                {
                    i_px -= i_maptaggerx;
                }

                i_px += i_mbwhalf;

                int i_x = i_px+_x;

                Tester.renderCell(_g,i_x,i_y,_array[i_index] & 0xFF);

                i_index++;
                
                drawCell(_g,i_x,i_y);
            }
        }
    }

    /**
     * Отрисовка границ гексагона
     * @param _g канвас
     * @param _x X координата
     * @param _y Y координата
     */
    public final void drawCell(Graphics _g,int _x,int _y)
    {
        final int i_mbw = i_mapblockwidth;
        final int i_mbh = i_mapblockheight;
            
        if (lg_horizontal)
        {
            int i_xoff = i_mbw>>2;
            int i_yoff = i_mbh >> 1;
            int i_xoff2 = i_mbw - i_xoff;

            _g.drawLine(_x,_y+i_yoff,_x+i_xoff,_y);
            _g.drawLine(_x+i_xoff,_y,_x+i_xoff2,_y);
            _g.drawLine(_x+i_xoff2,_y,_x+i_mbw,_y+i_yoff);
            _g.drawLine(_x+i_mbw,_y+i_yoff,_x+i_xoff2,_y+i_mbh);
            _g.drawLine(_x+i_xoff2,_y+i_mbh,_x+i_xoff,_y+i_mbh);
            _g.drawLine(_x+i_xoff,_y+i_mbh,_x,_y+i_yoff);
        }
        else
        {
            int i_yoff = i_mbh >> 2;
            int i_xoff = i_mbw >> 1;
            int i_yoff2 = i_mbh - i_yoff;

            _g.drawLine(_x,_y+i_yoff,_x+i_xoff,_y);
            _g.drawLine(_x+i_xoff,_y,_x+i_mbw,_y+i_yoff);
            _g.drawLine(_x+i_mbw,_y+i_yoff,_x+i_mbw,_y+i_yoff2);
            _g.drawLine(_x+i_mbw,_y+i_yoff2,_x+i_xoff,_y+i_mbh);
            _g.drawLine(_x+i_xoff,_y+i_mbh,_x,_y+i_yoff2);
            _g.drawLine(_x,_y+i_yoff2,_x,_y+i_yoff);
        }
    }
}

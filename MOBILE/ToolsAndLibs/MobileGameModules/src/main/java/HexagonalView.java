import javax.microedition.lcdui.Graphics;

/**
 * Класс, позволяющий отобразить массив в виде гексагонального поля
 */
public class HexagonalView
{
    /**
     * Интерфейс, описывающий класс, осуществляющий отрисовку заданной ячейки в указанных координатах
     */
    public interface HexagonalViewRenderer
    {
        /**
         * Отрисовка заданной ячейки
         * @param _g Канвас для отрисовки
         * @param _x координата X ячейки на канвасе
         * @param _y координата Y ячейки на канвасе
         * @param _value значение ячейки в массиве данных
         */
        public void renderCell(Graphics _g, int _x, int _y,int _value);
    }

    /**
     * Класс, описывающий 2D точку
     */
    public class Point
    {
        public int x;
        public int y;

        /**
         * Конструктор
         * @param _x координата X
         * @param _y координата Y
         */
        public Point(int _x,int _y)
        {
            x = _x;
            y = _y;
        }
    }

    protected int i_mapblockgapx;
    protected int i_mapblockgapy;
    protected int i_mapblockstaggerx;
    protected int i_mapblockstaggery;
    protected int i_mapblockwidth;
    protected int i_mapblockheight;
    protected boolean lg_horizontal;

    /**
     * Конструктор
     * @param _mapblockwidth ширина ячейки
     * @param _mapblockheight высота ячейки
     * @param _horizontal флаг, показывает, что ячейки отрисовываются горизонтально, иначе вертикально
     */
    public HexagonalView(int _mapblockwidth,int _mapblockheight,boolean _horizontal)
    {
        lg_horizontal = _horizontal;
        setCellWidth(_mapblockwidth);
        setCellHeight(_mapblockheight);
    }

    /**
     * Конвертация координат в 2D массиве в экранные координаты
     * @param _x координата X в массиве
     * @param _y координата Y в массиве
     * @param _point структура, в которую заносятся полученные координаты
     */
    public final void convertBlockToPixelCoords(int _x, int _y,Point _point)
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

            _point.x = i_px;
            _point.y = i_py;
    }

    /**
     * Отобразить массив на канвас в заданных координатах
     * @param _g канвас
     * @param _color цвет границ ячеек
     * @param _x X координата вывода поля
     * @param _y Y координата вывода поля
     * @param _array одномерный массив, содержащй значения
     * @param _arrayWidth ширина строки в массиве
     * @param _renderer ссылка на отрисовщик ячеек
     */
    public final void drawArray(Graphics _g,int _color, int _x, int _y, byte [] _array, int _arrayWidth, HexagonalViewRenderer _renderer)
    {
        int i_height = _array.length / _arrayWidth;
        Point p_point = new Point(0,0);
        _g.setColor(_color);

        int i_index = 0;
        for(int ly=0;ly<i_height;ly++)
        {
            for(int lx=0;lx<_arrayWidth;lx++)
            {
                convertBlockToPixelCoords(lx,ly,p_point);

                int i_x = p_point.x+_x;
                int i_y = p_point.y+_y;

                if (_renderer != null) _renderer.renderCell(_g,i_x,i_y,_array[i_index++] & 0xFF);

                drawCell(_g,i_x,i_y);
            }
        }
    }

    /**
     * Отрисовка гексагона
     * @param _g канвас
     * @param _x X координата
     * @param _y Y координата
     */
    public final void drawCell(Graphics _g,int _x,int _y)
    {
        if (lg_horizontal)
        {
            int i_xoff = i_mapblockwidth>>2;
            int i_yoff = i_mapblockheight >> 1;
            int i_xoff2 = i_mapblockwidth - i_xoff;

            _g.drawLine(_x,_y+i_yoff,_x+i_xoff,_y);
            _g.drawLine(_x+i_xoff,_y,_x+i_xoff2,_y);
            _g.drawLine(_x+i_xoff2,_y,_x+i_mapblockwidth,_y+i_yoff);
            _g.drawLine(_x+i_mapblockwidth,_y+i_yoff,_x+i_xoff2,_y+i_mapblockheight);
            _g.drawLine(_x+i_xoff2,_y+i_mapblockheight,_x+i_xoff,_y+i_mapblockheight);
            _g.drawLine(_x+i_xoff,_y+i_mapblockheight,_x,_y+i_yoff);
        }
        else
        {
            int i_yoff = i_mapblockheight >> 2;
            int i_xoff = i_mapblockwidth >> 1;
            int i_yoff2 = i_mapblockheight - i_yoff;

            _g.drawLine(_x,_y+i_yoff,_x+i_xoff,_y);
            _g.drawLine(_x+i_xoff,_y,_x+i_mapblockwidth,_y+i_yoff);
            _g.drawLine(_x+i_mapblockwidth,_y+i_yoff,_x+i_mapblockwidth,_y+i_yoff2);
            _g.drawLine(_x+i_mapblockwidth,_y+i_yoff2,_x+i_xoff,_y+i_mapblockheight);
            _g.drawLine(_x+i_xoff,_y+i_mapblockheight,_x,_y+i_yoff2);
            _g.drawLine(_x,_y+i_yoff2,_x,_y+i_yoff);
        }
    }

    /**
     * Задать ширину гексагональной ячейки
     * @param _width
     */
    public final void setCellWidth(int _width)
    {
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
    }

    /**
     * Задать высоту гексагональной ячейки
     * @param _height
     */
    public final void setCellHeight(int _height)
    {
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
}

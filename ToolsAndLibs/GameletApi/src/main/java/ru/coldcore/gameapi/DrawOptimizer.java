package ru.coldcore.gameapi;

/**
 * Класс осуществляет оптимизацию областей отрисовки, для уменьшения зон копирования бэкбуффера на экран.
 * @author Igoe Maznitsa
 * @version 1.00 (02/02/2006)
 */
public class DrawOptimizer
{
    /**
     * Соотношение при котором выгоднее слить прямоугольные области
     * (SUMMARYAREA<<8 / FULLAREA) (0x33 - 20%)
     */
    private static final int AREACOEFF = 0x33;
    
    /**
     * Флаг показывает что надо проверять пересечение с областью отображения
     */
    private static final boolean CHECKGRAPHICSAREA = true;

    /**
     * Ширина области отображения
     */
    public static int GRAPHICSAREA_WIDTH;

    /**
     * Высота области отображения
     */
    public static int GRAPHICSAREA_HEIGHT;

    /**
     * Максимальная глубина стека 
     */
    private int STACKDEPTH;

    /**
     * Временный массив прямоугольных областей вывода, секциями по 8 элементов
     * X,Y,W,H,RX,RY,AREA 
     */
    private int[] TEMPAREA_ARRAY;

    /**
     * Чистовой массив выводимых прямоугольных областей
     * X,Y,W,H,RX,RY,AREA 
     */
    public int[] AREA_ARRAY;

    /**
     * левая верхняя минимальная координата X добавленной зоны 
     */
    private static int i_minAreaX;

    /**
     * Ссылка на массив рабочей коллекции спрайтов, если была задана
     */
    private int [] ai_destSpriteCollectionArray;
    
    /**
     * Ссылка на массив статических данных спрайтов рабочей коллекции спрайтов, если была задана
     */
    private short [] ash_destSpriteCollectionStaticArray;
    
    /**
     * левая верхняя минимальная координата Y добавленной зоны 
     */
    private int i_minAreaY;

    /**
     * правая нижняя максимальная координата X добавленной зоны 
     */
    private int i_maxAreaX;

    /**
     * правая нижняя максимальная координата Y добавленной зоны 
     */
    private int i_maxAreaY;

    /**
     * смещение зоны с максимальной зоной 
     */
    private int i_maxAreaOffset;
    
    /**
     * Указатель текущей свободной позиции в стеке
     */
    public int i_StackPointer;

    /**
     * Флаг, показывающий что должна отрисоваться полная область 
     */
    public boolean lg_PaintFullArea;

    public DrawOptimizer(int _stackDepth)
    {
        STACKDEPTH = _stackDepth;
        TEMPAREA_ARRAY = new int[_stackDepth<<3];
        AREA_ARRAY = new int[_stackDepth<<3];
    }
    
    /**
     * Сброс оптимизатора 
     */
    public final void reset()
    {
        lg_PaintFullArea = false;
        i_StackPointer = 0;
        i_maxAreaX = 0;
        i_maxAreaY = 0;
        i_minAreaX = GRAPHICSAREA_WIDTH;
        i_minAreaY = GRAPHICSAREA_HEIGHT;
    }
    
    /**
     * Выставляем коллекцию спрайтов для последующей работы
     * @param _collection коллекция
     */
    public final void setCurrentSpriteCOllection(SpriteCollection _collection )
    {
        if (_collection == null) 
        {
            ai_destSpriteCollectionArray = null;
            ash_destSpriteCollectionStaticArray = null;
        }
        else
        {
            ai_destSpriteCollectionArray = _collection.ai_spriteDataArray;
            ash_destSpriteCollectionStaticArray = _collection.ash_spriteAnimationDataArray;
        }
    }
    
    /**
     * Добавляем отрисовочную зону спрайта из текущей коллекции
     * @param _offset смещение спрайта в массиве данных коллекции
     * @param _optimize флаг оптимизации взаимодействия добавляемых зон
     */
    public final void addSpriteFromCurrentCollection(int _offset,boolean _optimize)
    {
        final int [] ai_sprArray = ai_destSpriteCollectionArray;
        final short [] ash_sprArray = ash_destSpriteCollectionStaticArray;
        
        int i_spriteOffset = _offset+SpriteCollection.OFFSET_SCREENX;
        
        int i_x = (ai_sprArray[i_spriteOffset++]+0x7F)>> 8;
        int i_y = (ai_sprArray[i_spriteOffset]+0x7F)>> 8;

        i_spriteOffset = ai_sprArray[_offset + SpriteCollection.OFFSET_STATICDATAOFFSET] + SpriteCollection.ANIMATIONTABLEEOFFSET_WIDTH;

        int i_w = ash_sprArray[i_spriteOffset++];
        int i_h = ash_sprArray[i_spriteOffset];

        addRectangleArea(i_x,i_y,i_w,i_h,_optimize);
    }

    /**
     * Добавляем в стек область отрисовки
     * @param _x координата X верхнего левого края области
     * @param _y координата Y верхнего левого края области
     * @param _width ширина области
     * @param _height высота области
     * @param _optimize флаг показывает, что можно проверить область на слияние с другой областью
     */
    public final void addRectangleArea(int _x, int _y, int _width, int _height, boolean _optimize)
    {
        if (lg_PaintFullArea) return;

        int i_pos = i_StackPointer;
        if (i_pos == STACKDEPTH)
        {
            lg_PaintFullArea = true;
            return;
        }

        // выравниваем по области видимости
        if (CHECKGRAPHICSAREA)
        {
            if (_x >= GRAPHICSAREA_WIDTH || _y >= GRAPHICSAREA_HEIGHT) return;
            
            if (_x < 0)
            {
                _width += _x;
                _x = 0;
            }

            if (_y < 0)
            {
                _height += _y;
                _y = 0;
            }
            
            final int i_lx = _x + _width;
            final int i_ly = _y + _height;
            
            if (i_lx<0 || i_ly<0) return; 
            
            if (i_lx>=GRAPHICSAREA_WIDTH)
            {
                _width -= (i_lx-(GRAPHICSAREA_WIDTH+1));
            }

            if (i_ly>=GRAPHICSAREA_HEIGHT)
            {
                _height -= (i_ly-(GRAPHICSAREA_HEIGHT+1));
            }
            
            if ((_width | _height)==0) return;
        }

        final int [] ai_array = TEMPAREA_ARRAY; 
        
        int i_rx = _x+_width;
        int i_ry = _y+_height;
        int i_area = _width*_height;
        
        // проверяем на включение
        final int i_maxareax = i_maxAreaX; 
        final int i_maxareay = i_maxAreaY; 
        final int i_minareax = i_minAreaX; 
        final int i_minareay = i_minAreaY; 
        
        // проверяем на попадание в уже заданную зону
        if (i_minareax<=_x && i_minareay<=_y && i_maxareax>=i_rx && i_maxareay>=i_ry) return;
        
        // оптимизируем если требуется
        if (_optimize)
        {
            // проверяем пересечение с существующими прямоугольниками
            int i_cnt = 0;
            int i_num = i_StackPointer;
            
            while(i_num!=0)
            {
                int i_curx = ai_array[i_cnt++];
                int i_cury = ai_array[i_cnt++];
                i_cnt++;
                i_cnt++;
                int i_currx = ai_array[i_cnt++];
                int i_curry = ai_array[i_cnt++];
                int i_curarea = ai_array[i_cnt++];
                i_cnt++;
                
                int i_fullx = i_curx<_x ? i_curx : _x;
                int i_fully = i_cury<_y ? i_cury : _y;
                
                int i_fullrx = i_currx>i_rx ? i_currx : i_rx;
                int i_fullry = i_curry>i_ry ? i_curry : i_ry;

                int i_fullw = i_fullrx-i_fullx+1;
                int i_fullh = i_fullry-i_fully+1;
                
                int i_fullarea = i_fullw * i_fullh;
                int i_partarea = (i_fullarea*AREACOEFF)>>8;
                
                i_curarea += i_area;

                // проверяем разницу в площадях между полученным общим прямоугольником и маленькими
                // если суммарная площадь маленьких больше чем заданный процент от площади нового прямоугольника, то сливаем 
                if(i_partarea<i_curarea)
                {
                    // операция слияния оправдана
                    int i_offset = i_cnt-8;

                    if (i_fullx<=i_minareax && i_fully<=i_minareay && i_fullrx>=i_maxareax && i_fullry>i_maxareay)
                    {
                        i_maxAreaOffset = i_offset;
                        
                        i_minAreaX = i_fullx;
                        i_minAreaY = i_fully;
                        i_maxAreaX = i_fullrx;
                        i_maxAreaY = i_fullry;
                    }

                    ai_array[i_offset++] = i_fullx;
                    ai_array[i_offset++] = i_fully;
                    ai_array[i_offset++] = i_fullw;
                    ai_array[i_offset++] = i_fullh;
                    ai_array[i_offset++] = i_fullrx;
                    ai_array[i_offset++] = i_fullry;
                    ai_array[i_offset] = i_fullarea;
                    

                    return;
                }
                    
                
                i_num--;
            }
        }

        int i_offset = i_StackPointer<<3; 

        if (_x<=i_minareax && _y<=i_minareay && i_rx>=i_maxareax && i_ry>i_maxareay)
        {
            i_maxAreaOffset = i_offset;
            i_minAreaX = _x;
            i_minAreaY = _y;
            i_maxAreaX = i_rx;
            i_maxAreaY = i_ry;
        }
        
        ai_array[i_offset++] = _x;
        ai_array[i_offset++] = _y;
        ai_array[i_offset++] = _width;
        ai_array[i_offset++] = _height;
        ai_array[i_offset++] = i_rx;
        ai_array[i_offset++] = i_ry;
        ai_array[i_offset] = i_area;

        
        i_StackPointer++;
    }

    /**
     * Оптимизация, подготавливающая массив областей к выводу
     * обязательна к выполнению, так как наполняет массив вывода
     */
    public final void pack()
    {
        final int i_minx = i_minAreaX;
        final int i_miny = i_minAreaY;
        final int i_maxx = i_maxAreaX;
        final int i_maxy = i_maxAreaY;
        final int i_maxoffset = i_maxAreaOffset;

        final int [] ai_srcarray = TEMPAREA_ARRAY;
        final int [] ai_dstarray = AREA_ARRAY;
        
        int i_srcPointer = 0;
        int i_dstPointer = 0;
        
        int i_stackNum = i_StackPointer;

        int i_areas = 0;
        
        while(i_stackNum!=0)
        {
            int i_startpos = i_srcPointer;
            
            int i_x = ai_srcarray[i_srcPointer++];
            int i_y = ai_srcarray[i_srcPointer++];
            i_srcPointer+=2;
            int i_lx = ai_srcarray[i_srcPointer++];
            int i_ly = ai_srcarray[i_srcPointer++];
            i_srcPointer+=2;
            
            if ((i_maxoffset != i_startpos) && i_x>=i_minx && i_y>=i_miny && i_lx<=i_maxx && i_ly<=i_maxy)
            {
                // область входит в наибольшую
                // не копируем её
            }
            else
            {
                // область не сливается, копируем
                System.arraycopy(ai_srcarray,i_startpos,ai_dstarray,i_dstPointer,8);
                i_dstPointer+=8;
                i_areas ++;
            }
            
            i_stackNum--;
        }
    
        i_StackPointer = i_areas;
    }
}

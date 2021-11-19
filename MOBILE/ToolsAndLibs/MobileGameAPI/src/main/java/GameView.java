import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Класс реализует отображение игрового процесса
 * @author Igor Maznitsa
 * @version 1.05
 */
public class GameView
{
    /**
     * Флаг, показывающий что требуется перерисовать весь экран
     */
    private static boolean lg_PaintAll;

    /**
     * Счетчик количества областей для перерисовки 
     */
    public static int i_AreasForDrawingNumber;

    /**
     * Координаты областей для перерисовки
     */
    private static final long [] al_areasForDrawing = new long[32];

    /**
     * Инициализация визуализатора
     * @param _width ширина игровой зоны в пикселях
     * @param _height высота игровой зоны в пикселях
     * @throws Throwable порождается в случае проблем при инициализации
     */
    public static final void initResources(int _width,int _height) throws Throwable
    {
        ImageManager.init(startup.p_This.getClass(),true);

        lg_PaintAll = true;
    }

    /**
     * Освобождение ресурсов визуализатора
     */
    public static final void releaseResources()
    {
            ImageManager.release();
    }

    /**
     * Функция вызывается для уведомления о первом отображении игрового поля
     */
    public static final void showNotify()
    {
        lg_PaintAll = true;
    }

    /**
     * Обработка игровых событий
     * @param _action идентификатор акции
     * @param _optionaldata опциональные данные
     */
    public static final void gameAction(int _action,int _optionaldata)
    {
    }

    /**
     * Функция вызывающаяся перед обработкой игрового шага
     * @throws Throwable
     */
    public static final void beforeGameIteration() throws Throwable
    {
    }

    /**
     * Функция вызывающаяся после обработки игрового шага
     * @throws Throwable
     */
    public static final void afterGameIteration() throws Throwable
    {
    }

    /**
     * Функция вызывающаяся после инициализации игрового уровня
     * @throws Throwable
     */
    public static final void afterInitGameStage() throws Throwable
    {
    }

    /**
     * Функция вызывающаяся после afterGameIteration() для подготовки списка областей к отрисовке
     * @throws Throwable
     */
    public static final void prepareDrawingList()
    {
        if (lg_PaintAll)
        {
            addAreaForDrawing(0,0,640,480);
            lg_PaintAll = false;
        }
        else
        {
        }
    }

    /**
     * Функция добавляет область в список областей отрисовки
     * @param _x X верхней левой точки области
     * @param _y Y верхней левой точки области
     * @param _w ширина области
     * @param _h высота области
     */
    private static final void addAreaForDrawing(int _x,int _y,int _w,int _h)
    {
        long l_area = (((long)_x)<<48) | (((long)_y)<<32) | (((long)_w)<<16) | (long)_h;
        al_areasForDrawing[i_AreasForDrawingNumber++] = l_area;
    }

    /**
     * Функция возвращает первую в стеке область к отрисовке
     * @return упакованное значение координат области
     */
    public static final long getRepaintArea()
    {
        while(i_AreasForDrawingNumber>0)
        {
            return al_areasForDrawing[--i_AreasForDrawingNumber];
        }
        return -1L;
    }

    /**
     * Отрисовка области выявляемой через параметры clip региона
     * @param _g графический контекст
     * @param _outputField
     * @throws Throwable
     */
    public static final void paint(Graphics _g,boolean _outputField) throws Throwable
    {
    }

}

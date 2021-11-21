import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;
import java.io.InputStream;

// Количество тайлов по горизонтали в общей картинке, может быть 16,8,4,2,1
    //#local MAXCELLWIDTHNUMBER = 16

// Клипкомпенсация на телефонах, неправильно выводящих изображение при выходе зоны за пределы экрана
//#if VENDOR=="MOTOROLA" || VENDOR=="LG" || VENDOR=="SAMSUNG" || VENDOR=="SE" || VENDOR=="NOKIA" || (VENDOR=="SIEMENS" && MODEL!="M55")
    //#local CLIPCOMPENSATION=false
//#else
    //#local CLIPCOMPENSATION=true
//#endif

// Флаг, показывающий что следует подключить механизм отображения тайлов не из одной картинки а из массива картинок, при этом меняется механизм присвоения массива
//#if VENDOR=="SAMSUNG" || VENDOR=="LG" || VENDOR=="SE"
    //#local SEPARATEDIMAGES = true
//#else
    //#local SEPARATEDIMAGES = false
//#endif

// Использовать массив short для хранения координат смещения изображений тайлов, используется если размер картинки меньше 256 на 256  не столь важна скорость
//#local IMAGECOORDSSHORT = true

// Компенсировать ошибку с выводом непонятной линии при отрисовке картинки за границей экрана на одну точку
//#local wronglinecompensation = (VENDOR=="SIEMENS")

// Сделать все члены класса статическими (для повышения скорости, но нельзя делать экземпляров)
//#local STATIC=true

// Класс будет осуществлять горизонтальное скроллирование (вставляет соответствующий код)
//#local SCROLLX=false

// Класс будет осуществлять вертикальное скроллирование (вставляет соответствующий код)
//#local SCROLLY=true

// Класс будет осуществлять зацикленное скроллирование (оптимизирует код)
//#local CYCLED=true

//#local STILEM50 = (VENDOR=="SIEMENS" && (MODEL=="M50" || MODEL=="C55" || MODEL=="SL45"))
//#local STILEM55 = (VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55"))
//#local HARDWARE= (MIDP=="2.0" || STILEM50 || STILEM55) && !(CYCLED || SEPARATEDIMAGES)

//#local DBLBUFFER=true

/**
 * Класс реализует тайловый рендер
 *
 * @author Igor Maznitsa
 * @version 4.95a
 * @since 29 dec 2005
 */
public class TileManager
{
    //#if SEPARATEDIMAGES
    //#if STATIC
    private static Image[] ap_separatedImages;
    //#else
    //$private Image [] ai_separatedImages;
    //#endif
    //#endif

    //#if HARDWARE
    //#if STILEM50
    //#if STATIC
    //$private static com.siemens.mp.game.TiledBackground p_HardTile;
    //$private static com.siemens.mp.game.GraphicObjectManager p_HardTileManager;
    //#else
    //$private com.siemens.mp.game.TiledBackground p_HardTile;
    //$private com.siemens.mp.game.GraphicObjectManager p_HardTileManager;
    //#endif
    //#else
    //#if STILEM55
    //#if STATIC
    //$private static com.siemens.mp.color_game.TiledLayer p_HardTile;
    //#else
    //$private com.siemens.mp.color_game.TiledLayer p_HardTile;
    //#endif
    //#else
    //#if STATIC
    //$private static javax.microedition.lcdui.game.TiledLayer p_HardTile;
    //#else
    //$private javax.microedition.lcdui.game.TiledLayer p_HardTile;
    //#endif
    //#endif
    //#endif
    //#endif


    //#if STATIC
    protected static Image p_BlockImage;
    //#else
    //$protected Image p_BlockImage;
    //#endif

    //#if STILEM50 && HARDWARE
    //#if STATIC
    //$private static byte [] ab_roomArray;
    //#else
    //$private byte [] ab_roomArray;
    //#endif
    //#else
    //#if IMAGECOORDSSHORT
    //#if STATIC
    private static short[] ash_roomArray;
    //#else
    //$private short[] ash_roomArray;
    //#endif
    //#else
    //#if STATIC
    //$private static int [] ai_roomArray;
    //#else
    //$private int[] ai_roomArray;
    //#endif
    //#endif
    //#endif

    //#if STATIC
    private static int i_cellTopX;
    //#else
    //$private int i_cellTopX;
    //#endif

    //#if STATIC
    private static int i_cellTopY;
    //#else
    //$private int i_cellTopY;
    //#endif

    //#if STATIC
    private static int i_viewCoordX;
    //#else
    //$private int i_viewCoordX;
    //#endif

    //#if STATIC
    private static int i_viewCoordY;
    //#else
    //$private int i_viewCoordY;
    //#endif

    //#if STATIC
    private static int i_blockWidth;
    //#else
    //$private int i_blockWidth;
    //#endif

    //#if STATIC
    private static int i_blockHeight;
    //#else
    //$private int i_blockHeight;
    //#endif

    //#if STATIC
    private static int i_cellWidthNumber;
    //#else
    //$private int i_cellWidthNumber;
    //#endif

    //#if STATIC
    private static int i_cellHeightNumber;
    //#else
    //$private int i_cellHeightNumber;
    //#endif

    //#if STATIC
    private static int i_roomArrayWidth;
    //#else
    //$private int i_roomArrayWidth;
    //#endif

    //#if STATIC
    private static int i_roomArrayHeight;
    //#else
    //$private int i_roomArrayHeight;
    //#endif

    //#if STATIC
    private static int i_roomArrayLength;
    //#else
    //$private int i_roomArrayLength;
    //#endif

    //#if STATIC
    private static int i_outAreaWidth;
    //#else
    //$private int i_outAreaWidth;
    //#endif

    //#if STATIC
    private static int i_outAreaHeight;
    //#else
    //$private int i_outAreaHeight;
    //#endif

    //#if STATIC
    private static int i_screenWidth;
    //#else
    //$private int i_screenWidth;
    //#endif

    //#if STATIC
    private static int i_screenHeight;
    //#else
    //$private int i_screenHeight;
    //#endif

    //#if STATIC
    private static int i_HBoffsetY;
    //#else
    //$private int i_HBoffsetY;
    //#endif

    //#if STATIC
    private static int i_HBoffsetX;
    //#else
    //$private int i_HBoffsetX;
    //#endif

    //#if DBLBUFFER

    //#if STILEM50
    //#if STATIC
    //$private static com.siemens.mp.game.ExtendedImage p_HiddenBuffer;
    //#else
    //$private static com.siemens.mp.game.ExtendedImage p_HiddenBuffer;
    //#endif
    //#else
    //#if STATIC
    private static Image p_HiddenBuffer;
    //#else
    //$private Image p_HiddenBuffer;
    //#endif
    //#endif

    //#if STATIC
    private static Graphics p_HiddenBufferGraphics;
    //#else
    //$private Graphics p_HiddenBufferGraphics;
    //#endif

    //#if !HARDWARE
    //#if STATIC
    private static Image p_HiddenPartBuffer;
    //#else
    //$private Image p_HiddenPartBuffer;
    //#endif

    //#if STATIC
    private static Graphics p_HiddenPartBufferGraphics;
    //#else
    //$private Graphics p_HiddenPartBufferGraphics;
    //#endif

    //#if STATIC
    private static int i_hiddenBufferWidth;
    //#else
    //$private int i_hiddenBufferWidth;
    //#endif

    //#if STATIC
    private static int i_hiddenBufferHeight;
    //#else
    //$private int i_hiddenBufferHeight;
    //#endif

    //#if STATIC
        private static int i_hiddenPartBufferWidth;
    //#else
        //$private int i_hiddenPartBufferWidth;
    //#endif

    //#if STATIC
        private static int i_hiddenPartBufferHeight;
    //#else
        //$private int i_hiddenPartBufferHeight;
    //#endif

    //#if STATIC
        private static int i_hiddenVertPartsNumber;
    //#else
        //$private int i_hiddenVertPartsNumber;
    //#endif

    //#if STATIC
        private static int i_hiddenHorzPartsNumber;
    //#else
        //$private int i_hiddenHorzPartsNumber;
    //#endif

    //#endif
    //#endif

    /**
     * Освобождает ресурсы, взятые блоком
     */
    //#if STATIC
    public static final void release()
    //#else
    //$public final void release()
    //#endif
    {
        //#if HARDWARE
        //#if STILEM50
        //$p_HardTileManager.deleteObject(p_HardTile);
        //$p_HardTileManager = null;
        //#endif

        //$p_HardTile = null;
        //#endif

        //#if DBLBUFFER
        p_HiddenBufferGraphics = null;
        p_HiddenBuffer = null;

        //#if !HARDWARE
        p_HiddenPartBuffer = null;
        p_HiddenPartBufferGraphics = null;
        //#endif
        //#endif

        //#if IMAGECOORDSSHORT
        ash_roomArray = null;
        //#else
        //$ai_roomArray = null;
        //#endif
        p_BlockImage = null;
        Runtime.getRuntime().gc();
    }

    /**
         * Инициализация блока
         *
         * @param _width      ширина видимой части
         * @param _height     высота видимой части
         * @param _cellWidth  ширина ячейки
         * @param _cellHeight высота ячейки
         */
    //#if STATIC
    public static final void initTileBackground(int _width, int _height, int _cellWidth, int _cellHeight, int _screenWidth, int _screenHeight)
    //#else
    //$public final void initTileBackground(int _width, int _height, int _cellWidth, int _cellHeight,int _screenWidth,int _screenHeight)
    //#endif
    {
        i_screenWidth = _screenWidth;
        i_screenHeight = _screenHeight;

        i_outAreaWidth = _width;
        i_outAreaHeight = _height;

        i_blockWidth = _cellWidth;
        i_blockHeight = _cellHeight;
        i_cellWidthNumber = _width % _cellWidth == 0 ? _width / _cellWidth : (_width / _cellWidth + 1);
        i_cellHeightNumber = _height % _cellHeight == 0 ? _height / _cellHeight : (_height / _cellHeight + 1);

        i_viewCoordX = 0x7FFFFFFF;
        i_viewCoordY = i_viewCoordX;

        //#if DBLBUFFER
        //#if HARDWARE
            //#if STILEM50
                //$Image p_dbImg = Image.createImage((i_outAreaWidth & 0x7)!=0? ((i_outAreaWidth >> 3)+1)<<3 : i_outAreaWidth, i_outAreaHeight);
                //$p_HiddenBuffer = new com.siemens.mp.game.ExtendedImage(p_dbImg);
                //$p_HiddenBufferGraphics = p_dbImg.getGraphics();
            //#else
                //$p_HiddenBuffer = Image.createImage(i_outAreaWidth, i_outAreaHeight);
                //$p_HiddenBufferGraphics = p_HiddenBuffer.getGraphics();
            //#endif
        //#else
        i_hiddenBufferWidth = i_cellWidthNumber * i_blockWidth;
        i_hiddenBufferHeight = i_cellHeightNumber * i_blockHeight;

        p_HiddenBuffer = Image.createImage(i_hiddenBufferWidth, i_hiddenBufferHeight);
        p_HiddenBufferGraphics = p_HiddenBuffer.getGraphics();

            i_hiddenPartBufferWidth = (i_hiddenBufferWidth >> 1) + (i_hiddenBufferWidth & 1);
            i_hiddenPartBufferHeight = (i_hiddenBufferHeight >> 1) + (i_hiddenBufferHeight & 1);
            i_hiddenHorzPartsNumber = 2;
            i_hiddenVertPartsNumber = 2;

        p_HiddenPartBuffer = Image.createImage(i_hiddenPartBufferWidth, i_hiddenPartBufferHeight);
        p_HiddenPartBufferGraphics = p_HiddenPartBuffer.getGraphics();
        //#endif
        //#endif
    }

    //#if DBLBUFFER
    /**
     * Отрисовать фон
     *
     * @param _g Graphics объект
     * @param _x Координата X верхнего левого угла
     * @param _y Координата Y верхнего левого угла
     */
    //#if STATIC
    public static final void drawBufferToGraphics(Graphics _g, int _x, int _y)
    //#else
    //$public final void drawBufferToGraphics(Graphics _g, int _x, int _y)
    //#endif
    {
        //#if CLIPCOMPENSATION
            //$int i_clX = _x;
            //$int i_clY = _y;
            //$int i_bW = i_outAreaWidth;
            //$int i_bH = i_outAreaHeight;
            //$if (_x<0)
            //${
            //$    i_clX = 0;
            //$    i_bW += _x;
            //$}
            //$if (_y<0)
            //${
            //$    i_clY = 0;
            //$    i_bH += _y;
            //$}
            //$_g.setClip(i_clX,i_clY,i_bW,i_bH);
         //#else
            _g.setClip(_x, _y, i_outAreaWidth, i_outAreaHeight);
         //#endif
        //#if STILEM50
        //$_g.drawImage(p_HiddenBuffer.getImage(), _x, _y, 0);
        //#else
        _g.drawImage(p_HiddenBuffer, _x, _y, 0);
        //#endif
    }
    //#endif

    /**
     * Задает массив, содержащий указатили на тайлы
     *
     * @param _roomWidth ширина массива
     * @param _roomArray массив
     */
    //#if STATIC
    public static final void setGameRoomArray(final int _roomWidth, final byte[] _roomArray)
    //#else
    //$public final void setGameRoomArray(final int _roomWidth, final byte[] _roomArray)
    //#endif
    {
        i_roomArrayWidth = _roomWidth;
        i_roomArrayLength = _roomArray.length;
        i_roomArrayHeight = i_roomArrayLength / _roomWidth;

        //#if SEPARATEDIMAGES
            //$int i_len = _roomArray.length;

            //#if IMAGECOORDSSHORT
                //$    ash_roomArray = new short[i_len];
            //#else
                //$ai_roomArray = new int[i_len];
            //#endif
                //$    for(int li=0;li<i_len;li++)
            //#if IMAGECOORDSSHORT
                //$      ash_roomArray[li] = (short) (_roomArray[li] & 0xFF);
            //#else
                //$ai_roomArray[li] = (_roomArray[li] & 0xFF);
            //#endif
        //#else

        //#if STILEM50 && HARDWARE
            //$int i_len = _roomArray.length;
            //$for(int li=0;li<i_len;li++)
            //${
            //$ int i_data = (_roomArray[li] & 0xFF);

            //#if MAXCELLWIDTHNUMBER == 16
                //$ int i_cx = i_data >>> 4;
                //$ int i_cy = i_data & 0xF;
            //#else
                //#if MAXCELLWIDTHNUMBER == 8
                    //$ int i_cx = i_data >>> 5;
                    //$ int i_cy = i_data & 0x1F;
                //#else
                    //#if MAXCELLWIDTHNUMBER == 4
                        //$ int i_cx = i_data >>> 6;
                        //$ int i_cy = i_data & 0x3F;
                    //#else
                        //#if MAXCELLWIDTHNUMBER == 2
                            //$ int i_cx = i_data >>> 7;
                            //$ int i_cy = i_data & 0x7F;
                        //#else
                            //#if MAXCELLWIDTHNUMBER == 1
                                //$ int i_cx = 0;
                                //$ int i_cy = i_data;
                            //#else
                                //#assert !!!!!UNSUPPORTED TILE WIDTH VALUE!!!!!!!
                            //#endif
                        //#endif
                    //#endif
                //#endif
            //#endif

            //$ _roomArray[li] = (byte)(i_cx+(i_cy<<4)+3);
            //$}
            //$ab_roomArray = _roomArray;
        //#else

        int i_len = _roomArray.length;

        //#if IMAGECOORDSSHORT
        ash_roomArray = new short[i_len];
        //#else
        //$ai_roomArray = new int[i_len];
        //#endif

        for (int li = 0; li < i_len; li++)
        {
            int i_data = (_roomArray[li] & 0xFF);

            //#if MAXCELLWIDTHNUMBER == 16
            int i_cx = i_data >>> 4;
            int i_cy = i_data & 0xF;
            //#else
            //#if MAXCELLWIDTHNUMBER == 8
            //$int i_cx = i_data >>> 5;
            //$int i_cy = i_data & 0x1F;
            //#else
            //#if MAXCELLWIDTHNUMBER == 4
            //$ int i_cx = i_data >>> 6;
            //$ int i_cy = i_data & 0x3F;
            //#else
            //#if MAXCELLWIDTHNUMBER == 2
            //$ int i_cx = i_data >>> 7;
            //$ int i_cy = i_data & 0x7F;
            //#else
            //#if MAXCELLWIDTHNUMBER == 1
            //$ int i_cx = 0;
            //$ int i_cy = i_data;
            //#else
            //#assert !!!!!UNSUPPORTED TILE WIDTH VALUE!!!!!!!
            //#endif
            //#endif
            //#endif
            //#endif
            //#endif

            int i_cellx = i_cx * i_blockWidth;
            int i_celly = i_cy * i_blockHeight;

            //#if IMAGECOORDSSHORT
            ash_roomArray[li] = (short) ((i_cellx << 8) | (i_celly));
            //#else
            //$ai_roomArray [li] = (i_cellx<<16) | (i_celly);
            //#endif
        }
        //#endif
        //#endif
    }

    /**
     * Задает блок изображений, содержащих тайлы. При аппаратной поддержке MIDP 2.0, функция должна вызываться последней перед выводом.
     *
     * @param _image указатель на изображение
     */
    //#if STATIC
    public static final void setBlockImage(final Image _image, final Image[] _separatedImages)
    //#else
    //$public final void setBlockImage(final Image _image)
    //#endif
    {
        p_BlockImage = _image;

        //#if SEPARATEDIMAGES
        ap_separatedImages = _separatedImages;
        //#endif

        //#if HARDWARE
        //$int i_cols = i_roomArrayWidth;
        //$int i_rows = i_roomArrayLength/i_roomArrayWidth;
        //$int i_index = 0;
        //$byte [] ab_arr = ab_roomArray;


        //#if STILEM50
        //$p_HardTileManager = new com.siemens.mp.game.GraphicObjectManager();
        //$Image p_maskImage = null;
        //$try
        //${
        //$ p_maskImage = Image.createImage("/tilemask.png");
        //$}
        //$catch (Exception e)
        //${
        //$}
        //$p_HardTile = new com.siemens.mp.game.TiledBackground(p_BlockImage,p_maskImage,ab_arr,i_cols,i_rows);
        //$p_HardTile.setVisible(true);
        //$p_HardTileManager.addObject(p_HardTile);
        //#else
        //#if STILEM55
        //$final com.siemens.mp.color_game.TiledLayer p_HTL = new com.siemens.mp.color_game.TiledLayer(i_cols,i_rows,p_BlockImage,i_blockWidth,i_blockHeight);
        //#else
        //$final javax.microedition.lcdui.game.TiledLayer p_HTL = new javax.microedition.lcdui.game.TiledLayer(i_cols,i_rows,p_BlockImage,i_blockWidth,i_blockHeight);
        //#endif


        //$for(int ly=0;ly<i_rows;ly++)
        //${
        //$    for(int lx=0;lx<i_cols;lx++)
        //$    {
        //$        int i_data = (ab_arr[i_index++] & 0xFF);

        //#if MAXCELLWIDTHNUMBER == 16
        //$ int i_cx = i_data >>> 4;
        //$ int i_cy = i_data & 0xF;
        //#else
        //#if MAXCELLWIDTHNUMBER == 8
        //$ int i_cx = i_data >>> 5;
        //$ int i_cy = i_data & 0x1F;
        //#else
        //#if MAXCELLWIDTHNUMBER == 4
        //$ int i_cx = i_data >>> 6;
        //$ int i_cy = i_data & 0x3F;
        //#else
        //#if MAXCELLWIDTHNUMBER == 2
        //$ int i_cx = i_data >>> 7;
        //$ int i_cy = i_data & 0x7F;
        //#else
        //#if MAXCELLWIDTHNUMBER == 1
        //$ int i_cx = 0;
        //$ int i_cy = i_data;
        //#else
        //#assert !!!!!UNSUPPORTED TILE WIDTH VALUE!!!!!!!
        //#endif
        //#endif
        //#endif
        //#endif
        //#endif
        //$        i_data = i_cx+(i_cy<<4)+1;
        //$        p_HTL.setCell(lx,ly,i_data);
        //$    }
        //$}
        //$p_HTL.setVisible(true);
        //$p_HardTile = p_HTL;

        //#endif

        //#endif
    }

    /**
     * Задает координаты смещения буффера
     *
     * @param _x координата X
     * @param _y координата Y
     */
    //#if STATIC
    public static final void setXY(int _x, int _y)
    //#else
    //$public final void setXY(int _x, int _y)
    //#endif
    {
        final int i_bW = i_blockWidth;
        final int i_bH = i_blockHeight;

        final int i_oldX = i_viewCoordX;
        final int i_oldY = i_viewCoordY;
        i_viewCoordX = _x;
        i_viewCoordY = _y;

        if ((i_oldX == _x) && (i_oldY == _y)) return;

        //#if !HARDWARE
        final int i_cellTopXold = i_cellTopX;
        final int i_cellTopYold = i_cellTopY;
        //#endif

        i_HBoffsetX = 0 - _x % i_bW;
        i_HBoffsetY = 0 - _y % i_bH;

        i_cellTopX = _x / i_bW;
        i_cellTopY = _y / i_bH;

        if (_x < 0)
        {
            i_cellTopX--;
            i_HBoffsetX -= i_bW;
        }

        if (_y < 0)
        {
            i_cellTopY--;
            i_HBoffsetY -= i_bH;
        }

        final int i_deltaX = _x - i_oldX;
        final int i_deltaY = _y - i_oldY;

        //#if STILEM50
        //$p_HardTile.setPositionInMap(i_viewCoordX,i_viewCoordY);
        //#if DBLBUFFER
        //$p_HardTileManager.paint(p_HiddenBuffer,0,0);
        //#endif
        //#else
        if (Math.abs(i_deltaX) > i_bW || Math.abs(i_deltaY) > i_bH)
        {
            //#if HARDWARE
            //$p_HardTile.setPosition(-i_viewCoordX,-i_viewCoordY);
            //#else
            //#if DBLBUFFER
            fillAllHiddenBufferFromPoint();
            //#endif
            //#endif
        }
        else
        {
            //#if HARDWARE
            //$p_HardTile.move(0-i_deltaX,0-i_deltaY);
            //#else
            //#if DBLBUFFER
            // Необходимо не забывать про проблему связанную с изображением при переходе границы тайла за шаг
            boolean lg_overCellX = i_cellTopXold != i_cellTopX;
            boolean lg_overCellY = i_cellTopYold != i_cellTopY;
            scrollHiddenBuffer(i_deltaX, i_deltaY, lg_overCellX, lg_overCellY);
            //#endif
            //#endif
        }
        //#if DBLBUFFER && HARDWARE
        //$p_HardTile.paint(p_HiddenBufferGraphics);
        //#endif
        //#endif
    }

    //#if DBLBUFFER

    //#if !HARDWARE

    //#if STATIC
    private static final void scrollHiddenBuffer(final int _stepX, final int _stepY, final boolean _overCellX, final boolean _overCellY)
    //#else
    //$private final void scrollHiddenBuffer(final int _stepX, final int _stepY, final boolean _overCellX, final boolean _overCellY)
    //#endif
    {
        //#if SEPARATEDIMAGES
        final Image[] ap_sepImages = ap_separatedImages;
        //#endif

        final Image p_HBuffer = p_HiddenBuffer;
        final Graphics p_HBufferGraphics = p_HiddenBufferGraphics;
        final Image p_HPart = p_HiddenPartBuffer;
        final Graphics p_HPartGraphics = p_HiddenPartBufferGraphics;

        final int i_partW = i_hiddenPartBufferWidth;
        final int i_partH = i_hiddenPartBufferHeight;

        p_HBufferGraphics.setClip(0, 0, i_hiddenBufferWidth, i_hiddenBufferHeight);

        // Осуществляем перемещение контента буффера на выбранное количество шагов
        int i_initX;
        int i_stepX;
        int i_initY;
        int i_stepY;

        if (_stepX >= 0)
        {
            i_initX = 0;
            i_stepX = i_partW;
        }
        else
        {
            i_initX = i_partW;
            i_stepX = -i_partW;
        }

        if (_stepY >= 0)
        {
            i_initY = 0;
            i_stepY = i_partH;
        }
        else
        {
            i_initY = i_partH;
            i_stepY = -i_partH;
        }

        int i_yp = i_initY;
        int i_partYnum = i_hiddenVertPartsNumber;

        while(i_partYnum!=0)
        {
            int i_xp = i_initX;
            final int i_yp2 = i_yp - _stepY;
            final int i_ypNeg = 0 - i_yp;

            int i_partXnum = i_hiddenHorzPartsNumber;

            while(i_partXnum!=0)
            {
                // Копируем контент буффера в теневой буффер
                p_HPartGraphics.drawImage(p_HBuffer, 0 - i_xp, i_ypNeg, 0);
                // Копируем обратно
                p_HBufferGraphics.drawImage(p_HPart, i_xp - _stepX, i_yp2, 0);
                i_xp += i_stepX;

                i_partXnum--;
            }
            i_yp += i_stepY;

            i_partYnum--;
        }

        //-------------

        final int i_startXCellBlock = i_cellTopX;
        final int i_startYCellBlock = i_cellTopY;

        final int i_limitX = i_startXCellBlock + i_cellWidthNumber;
        final int i_limitY = i_startYCellBlock + i_cellHeightNumber;

        final int i_arrayLength = i_roomArrayLength;

        //#if IMAGECOORDSSHORT
        final short[] ash_array = ash_roomArray;
        //#else
        //$final int[] ai_array = ai_roomArray;
        //#endif

        final int i_blockH = i_blockHeight;
        final int i_blockW = i_blockWidth;
        final int i_roomArrayW = i_roomArrayWidth;
        final int i_roomArrayH = i_roomArrayHeight;
        final Graphics p_hbg = p_HiddenBufferGraphics;
        final Image p_bimg = p_BlockImage;

        final int i_hbx = i_HBoffsetX;
        final int i_hby = i_HBoffsetY;

        //#if SCROLLX

        //#if wronglinecompensation
        //$final int i_finalX = i_hiddenBufferWidth-1;
        //#endif

        if (_stepX != 0)
        {
            int i_cellNumber = 1;
            int i_pixelX;
            int i_cellX;

            if (_stepX < 0)
            {
                i_pixelX = i_hbx;
                i_cellX = i_startXCellBlock;

                if (_overCellX)
                {
                    i_cellNumber = 2;
                }
            }
            else
            {
                i_pixelX = i_hiddenBufferWidth + i_hbx;
                i_cellX = i_limitX;

                if (_overCellX)
                {
                    i_cellNumber = 2;
                    i_cellX = i_limitX - 1;
                    i_pixelX -= i_blockW;
                }
            }

            while (i_cellNumber != 0)
            {
                int i_pixelY = i_hby;

                //#if !CYCLED
                //$int i_lineCS = i_startYCellBlock * i_roomArrayW + i_cellX;
                //$boolean lg_xoutofbound = false;
                //$if (i_cellX < 0 || i_cellX >= i_roomArrayW) lg_xoutofbound = true;
                //#else
                int i_xindex = i_cellX % i_roomArrayW;
                if (i_xindex < 0) i_xindex += i_roomArrayW;
                //#endif

                //#if wronglinecompensation
                //$if (i_pixelX<=i_finalX)
                //${
                //#endif

                // Восстанавливаем столбец
                for (int ly = i_startYCellBlock; ly <= i_limitY; ly++)
                {
                    int i_blockNum;

                    //#if !CYCLED
                    //$boolean lg_YoutOfBounds = false;
                    //$if (i_lineCS >= i_arrayLength || i_lineCS < 0) lg_YoutOfBounds = true;
                    //$if (lg_xoutofbound || lg_YoutOfBounds) i_blockNum = 0;
                    //$else
                    //#if IMAGECOORDSSHORT
                    //$i_blockNum = ash_array[i_lineCS]&0xFFFF;
                    //#else
                    //$i_blockNum = ai_array[i_lineCS];
                    //#endif
                    //#else
                    int i_lineY = ly % i_roomArrayH;
                    if (i_lineY < 0) i_lineY += i_roomArrayH;
                    i_lineY *= i_roomArrayW;

                    //#if IMAGECOORDSSHORT
                    i_blockNum = ash_array[i_lineY + i_xindex] & 0xFFFF;
                    //#else
                    //$i_blockNum = ai_array[i_lineY+i_xindex];
                    //#endif
                    //#endif

                    //#if SEPARATEDIMAGES
                    //$p_hbg.drawImage(ap_sepImages[i_blockNum], i_pixelX, i_pixelY, 0);
                    //#else
                    //#if IMAGECOORDSSHORT
                    int i_imgCol = i_blockNum >>> 8;
                    int i_imgRow = i_blockNum & 0xFF;
                    //#else
                    //$int i_imgCol = i_blockNum >>> 16 ;
                    //$int i_imgRow = i_blockNum & 0xFFFF;
                    //#endif

                    //#if CLIPCOMPENSATION
                    //$int i_clX = i_pixelX;
                    //$int i_clY = i_pixelY;
                    //$int i_bW = i_blockW;
                    //$int i_bH = i_blockH;
                    //$if (i_pixelX<0)
                    //${
                    //$    i_clX = 0;
                    //$    i_bW += i_pixelX;
                    //$}
                    //$if (i_pixelY<0)
                    //${
                    //$    i_clY = 0;
                    //$    i_bH += i_pixelY;
                    //$}
                    //$p_hbg.setClip(i_clX,i_clY,i_bW,i_bH);
                    //#else
                        p_hbg.setClip(i_pixelX, i_pixelY, i_blockW, i_blockH);
                    //#endif

                    p_hbg.drawImage(p_bimg, i_pixelX - i_imgCol, i_pixelY - i_imgRow, 0);
                    //#endif

                    i_pixelY += i_blockH;

                    //#if !CYCLED
                    //$i_lineCS += i_roomArrayW;
                    //#endif
                }
                //#if wronglinecompensation
                //$}
                //#endif

                i_pixelX += i_blockW;
                i_cellX++;
                i_cellNumber--;
            }
        }
        //#endif

        //#if SCROLLY

        //#if wronglinecompensation
        //$final int i_finalY = i_hiddenBufferHeight-1;
        //#endif

        if (_stepY != 0)
        {
            int i_cellNumber = 1;
            int i_pixelY;
            int i_cellY;
            if (_stepY < 0)
            {
                i_pixelY = i_hby;
                i_cellY = i_startYCellBlock;

                if (_overCellY)
                {
                    i_cellNumber = 2;
                }
            }
            else
            {
                i_pixelY = i_hiddenBufferHeight + i_hby;
                i_cellY = i_limitY;

                if (_overCellY)
                {
                    i_cellNumber = 2;
                    i_cellY = i_limitY - 1;
                    i_pixelY -= i_blockH;
                }
                else
                    i_cellY = i_limitY;
            }


            //#if !CYCLED
            //$int i_lineCS = i_cellY * i_roomArrayW;
            //#endif

            while (i_cellNumber != 0)
            {
                //#if wronglinecompensation
                //$if (i_pixelY<=i_finalY)
                //${
                //#endif


                //#if !CYCLED
                //$boolean lg_YoutOfBounds = false;
                //$if (i_lineCS >= i_arrayLength || i_lineCS < 0) lg_YoutOfBounds = true;
                //$int i_lineCellStart = i_lineCS + i_startXCellBlock;
                //#else
                int i_curCellY = i_cellY % i_roomArrayH;
                if (i_curCellY < 0) i_curCellY += i_roomArrayH;
                i_curCellY *= i_roomArrayW;
                //#endif

                int i_pixelX = i_hbx;

                for (int lx = i_startXCellBlock; lx <= i_limitX; lx++)
                {
                    int i_blockNum;

                    //#if !CYCLED
                       //$if (lx < 0 || lx >= i_roomArrayW || lg_YoutOfBounds) i_blockNum = 0;
                    //$else
                        //#if IMAGECOORDSSHORT
                            //$i_blockNum = ash_array[i_lineCellStart]&0xFFFF;
                        //#else
                            //$i_blockNum = ai_array[i_lineCellStart];
                        //#endif
                    //#else
                        int i_xindex = lx % i_roomArrayW;
                        if (i_xindex < 0) i_xindex += i_roomArrayW;
                        //#if IMAGECOORDSSHORT
                            i_blockNum = ash_array[i_curCellY + i_xindex] & 0xFFFF;
                        //#else
                            //$i_blockNum = ai_array[i_curCellY+i_xindex];
                        //#endif
                    //#endif

                    //#if SEPARATEDIMAGES
                        //$p_hbg.drawImage(ap_sepImages[i_blockNum], i_pixelX, i_pixelY, 0);
                    //#else

                    //#if IMAGECOORDSSHORT
                       int i_imgCol = i_blockNum >>> 8;
                        int i_imgRow = i_blockNum & 0xFF;
                    //#else
                        //$int i_imgCol = i_blockNum >>> 16;
                        //$int i_imgRow = i_blockNum & 0xFFFF;
                    //#endif

                    //#if CLIPCOMPENSATION
                        //$int i_clX = i_pixelX;
                        //$int i_clY = i_pixelY;
                        //$int i_bW = i_blockW;
                        //$int i_bH = i_blockH;
                        //$if (i_pixelX<0)
                        //${
                        //$    i_clX = 0;
                        //$    i_bW += i_pixelX;
                        //$}
                        //$if (i_pixelY<0)
                        //${
                        //$    i_clY = 0;
                        //$    i_bH += i_pixelY;
                        //$}
                        //$p_hbg.setClip(i_clX,i_clY,i_bW,i_bH);
                    //#else
                        p_hbg.setClip(i_pixelX, i_pixelY, i_blockW, i_blockH);
                    //#endif

                        p_hbg.drawImage(p_bimg, i_pixelX - i_imgCol, i_pixelY - i_imgRow, 0);
                    //#endif

                    //#if !CYCLED
                    //$i_lineCellStart++;
                    //#endif
                    i_pixelX += i_blockW;
                }
                //#if wronglinecompensation
                //$}
                //#endif

                i_pixelY += i_blockH;

                //#if !CYCLED
                //$i_lineCS += i_roomArrayW;
                //#else
                i_cellY++;
                //#endif

                i_cellNumber--;
            }
        }
        //#endif
    }
    //#endif //!HARDWARE


    //#if STATIC
    private static final void fillAllHiddenBufferFromPoint()
    //#else
    //$private final void fillAllHiddenBufferFromPoint()
    //#endif
    {
        //#if SEPARATEDIMAGES
        final Image[] ap_sepImages = ap_separatedImages;
        //#endif

        //#if HARDWARE
        //#if STILEM50
        //$p_HardTileManager.paint(p_HiddenBuffer,0,0);
        //#else
        //$p_HardTile.paint(p_HiddenBufferGraphics);
        //#endif
        //#else

        final int i_startXCellBlock = i_cellTopX;
        final int i_startYCellBlock = i_cellTopY;

        final int i_limitY = i_startYCellBlock + i_cellHeightNumber;
        final int i_limitX = i_startXCellBlock + i_cellWidthNumber;

        int i_pixelY = i_HBoffsetY;

        final int i_hbx = i_HBoffsetX;

        //#if !CYCLED
        //$int i_lineCS = i_startYCellBlock * i_roomArrayWidth + i_startXCellBlock;
        //#endif

        final int i_arrayLength = i_roomArrayLength;

        //#if IMAGECOORDSSHORT
        final short[] ash_array = ash_roomArray;
        //#else
        //$final int[] ai_array = ai_roomArray;
        //#endif

        final int i_blockH = i_blockHeight;
        final int i_blockW = i_blockWidth;
        final int i_roomArrayW = i_roomArrayWidth;
        final int i_roomArrayH = i_roomArrayHeight;
        final Graphics p_hbg = p_HiddenBufferGraphics;
        final Image p_bimg = p_BlockImage;

        //#if wronglinecompensation
        //$final int i_finalX = i_hiddenBufferWidth-1;
        //$final int i_finalY = i_hiddenBufferHeight-1;
        //#endif

        for (int ly = i_startYCellBlock; ly <= i_limitY; ly++)
        {
            //#if wronglinecompensation
            //$if (i_pixelY>i_finalY) break;
            //#endif

            //#if !CYCLED
            //$boolean lg_YoutOfBounds = false;
            //$if (i_lineCS >= i_arrayLength || i_lineCS < 0) lg_YoutOfBounds = true;
            //$int i_lineCellStart = i_lineCS;
            //#else
            int i_strIndex = ly % i_roomArrayH;
            if (i_strIndex < 0) i_strIndex += i_roomArrayH;
            i_strIndex *= i_roomArrayW;
            //#endif

            int i_pixelX = i_hbx;

            for (int lx = i_startXCellBlock; lx <= i_limitX; lx++)
            {
                //#if wronglinecompensation
                //$if (i_pixelX>i_finalX) break;
                //#endif

                int i_blockNum;

                //#if !CYCLED
                //$if (lx < 0 || lx >= i_roomArrayW || lg_YoutOfBounds) i_blockNum = 0;
                //$else

                //#if IMAGECOORDSSHORT
                //$i_blockNum = ash_array[i_lineCellStart]&0xFFFF;
                //#else
                //$i_blockNum = ai_array[i_lineCellStart];
                //#endif
                //#else
                int i_indexX = lx % i_roomArrayW;
                if (i_indexX < 0) i_indexX += i_roomArrayW;
                //#if IMAGECOORDSSHORT
                i_blockNum = ash_array[i_strIndex + i_indexX] & 0xFFFF;
                //#else
                //$i_blockNum = ai_array[ i_strIndex + i_indexX];
                //#endif
                //#endif

                //#if SEPARATEDIMAGES
                //$p_hbg.drawImage(ap_sepImages[i_blockNum], i_pixelX, i_pixelY, 0);
                //#else

                //#if IMAGECOORDSSHORT
                int i_imgCol = i_blockNum >>> 8;
                int i_imgRow = i_blockNum & 0xFF;
                //#else
                //$int i_imgCol = i_blockNum >>> 16;
                //$int i_imgRow = i_blockNum & 0xFFFF;
                //#endif

                //#if CLIPCOMPENSATION
                //$int i_clX = i_pixelX;
                //$int i_clY = i_pixelY;
                //$int i_bW = i_blockW;
                //$int i_bH = i_blockH;
                //$if (i_pixelX<0)
                //${
                //$    i_clX = 0;
                //$    i_bW += i_pixelX;
                //$}
                //$if (i_pixelY<0)
                //${
                //$    i_clY = 0;
                //$    i_bH += i_pixelY;
                //$}
                //$p_hbg.setClip(i_clX,i_clY,i_bW,i_bH);
                //#else
                p_hbg.setClip(i_pixelX, i_pixelY, i_blockW, i_blockH);
                //#endif

                p_hbg.drawImage(p_bimg, i_pixelX - i_imgCol, i_pixelY - i_imgRow, 0);
                //#endif

                //#if !CYCLED
                //$i_lineCellStart++;
                //#endif
                i_pixelX += i_blockW;
            }
            i_pixelY += i_blockH;
            //#if !CYCLED
            //$i_lineCS += i_roomArrayW;
            //#endif
        }
        //#endif
    }
    //#endif //DBLBUFFER

    //#if !STILEM50

    /**
     * Отрисовка на канвас текущего положения картинки
     *
     * @param _g канвас
     * @param _x X координата начала вывода
     * @param _y Y координата начала вывода
     */
    //#if STATIC
    public static final void directPaint(final Graphics _g, final int _x, final int _y)
    //#else
    //$public final void directPaint(final Graphics _g,final int _x,final int _y)
    //#endif
    {
        //#if SEPARATEDIMAGES
        final Image[] ap_sepImages = ap_separatedImages;
        //#endif

        //#if HARDWARE
        //#if VENDOR=="SIEMENS" && MODEL=="CX65"
        //$_g.setClip(_x,_y,i_outAreaWidth,i_outAreaHeight);
        //$p_HardTile.move(_x,_y);
        //$p_HardTile.paint(_g);
        //$p_HardTile.move(0-_x,0-_y);
        //#else
        //$_g.translate(_x,_y);
        //$_g.setClip(0,0,i_outAreaWidth,i_outAreaHeight);
        //$p_HardTile.paint(_g);
        //$_g.translate(-_x,-_y);
        //#endif
        //#else
        final int i_startX = i_viewCoordX;
        final int i_startY = i_viewCoordY;

        final int i_startXCellBlock = i_cellTopX;
        final int i_startYCellBlock = i_cellTopY;

        final int i_limitY = i_startYCellBlock + i_cellHeightNumber - (i_HBoffsetY == 0 ? 1 : 0);
        final int i_limitX = i_startXCellBlock + i_cellWidthNumber - (i_HBoffsetX == 0 ? 1 : 0);

        final int i_arrayLength = i_roomArrayLength;
        final Image p_bimg = p_BlockImage;

        //#if IMAGECOORDSSHORT
        final short[] ash_array = ash_roomArray;
        //#else
        //$final int [] ai_array = ai_roomArray;
        //#endif

        final int i_blockH = i_blockHeight;
        final int i_blockW = i_blockWidth;

        final int i_roomArrayW = i_roomArrayWidth;
        final int i_roomArrayH = i_roomArrayHeight;

        //#if !CYCLED
        //$int i_lineCS = i_startYCellBlock * i_roomArrayW;
        //#endif

        final int i_idealX = i_startXCellBlock * i_blockW;
        final int i_idealY = i_startYCellBlock * i_blockH;

        final int i_deltaX = i_startX - i_idealX;
        final int i_deltaY = i_startY - i_idealY;

        final int i_yoffset = 0 - i_deltaY;
        final int i_xoffset = 0 - i_deltaX;

        int i_pixelY = i_yoffset + _y;

        final int i_pxX = i_xoffset + _x;

        //#if wronglinecompensation
        //$final int i_finalX = i_screenWidth-1;
        //$final int i_finalY = i_screenHeight-1;
        //#endif

        for (int ly = i_cellTopY; ly <= i_limitY; ly++)
        {
            //#if wronglinecompensation
            //$if (i_pixelY>i_finalY) break;
            //#endif

            int i_pixelX = i_pxX;

            //#if !CYCLED
            //$boolean lg_YoutOfBounds = false;
            //$if (i_lineCS >= i_arrayLength || i_lineCS < 0) lg_YoutOfBounds = true;
            //$int i_lineCellStart = i_lineCS;
            //$int i_pixelX = i_pxX;
            //$i_lineCellStart += i_startXCellBlock;
            //#else
            int i_lineCellStart = ly % i_roomArrayH;
            if (i_lineCellStart < 0) i_lineCellStart += i_roomArrayH;
            i_lineCellStart *= i_roomArrayW;
            //#endif

            for (int lx = i_startXCellBlock; lx <= i_limitX; lx++)
            {
                //#if wronglinecompensation
                    //$if (i_pixelX>i_finalX) break;
                //#endif

                int i_blockNum;

                //#if !CYCLED
                    //$if (lx < 0 || lx >= i_roomArrayW || lg_YoutOfBounds) i_blockNum = 0;
                //$else
                    //#if IMAGECOORDSSHORT
                        //$i_blockNum = ash_array[i_lineCellStart]&0xFFFF;
                    //#else
                        //$i_blockNum = ai_array[i_lineCellStart];
                    //#endif
                //#else
                int i_xIndex = lx;
                i_xIndex %= i_roomArrayW;
                if (i_xIndex < 0) i_xIndex += i_roomArrayW;
                //#if IMAGECOORDSSHORT
                i_blockNum = ash_array[i_lineCellStart + i_xIndex] & 0xFFFF;
                //#else
                //$i_blockNum = ai_array[i_lineCellStart+i_xIndex];
                //#endif
                //#endif

                //#if SEPARATEDIMAGES
                //$_g.drawImage(ap_sepImages[i_blockNum], i_pixelX, i_pixelY, 0);
                //#else

                //#if IMAGECOORDSSHORT
                final int i_imgCol = i_blockNum >>> 8;
                final int i_imgRow = i_blockNum & 0xFF;
                //#else
                //$final int i_imgCol = i_blockNum >>> 16;
                //$final int i_imgRow = i_blockNum & 0xFFFF;
                //#endif

                    //#if CLIPCOMPENSATION
                //$int i_clX = i_pixelX;
                //$int i_clY = i_pixelY;
                //$int i_bW = i_blockW;
                //$int i_bH = i_blockH;
                //$if (i_pixelX<0)
                //${
                //$    i_clX = 0;
                //$    i_bW += i_pixelX;
                //$}
                //$if (i_pixelY<0)
                //${
                //$    i_clY = 0;
                //$    i_bH += i_pixelY;
                //$}
                //$_g.setClip(i_clX,i_clY,i_bW,i_bH);
                    //#else
                        _g.setClip(i_pixelX, i_pixelY, i_blockW, i_blockH);
                    //#endif
                        _g.drawImage(p_bimg, i_pixelX - i_imgCol, i_pixelY - i_imgRow, 0);
                //#endif

                //#if !CYCLED
                //$i_lineCellStart++;
                //#endif
                i_pixelX += i_blockW;
            }
            i_pixelY += i_blockH;

            //#if !CYCLED
            //$i_lineCS += i_roomArrayW;
            //#endif
        }
        //#endif
    }
    //#endif //!STILEM50




//#if SEPARATEDIMAGES

    /**
     * Функция загружает массив картинок из файла, содержащего их последовательность
     * @param _class класс, используемый для открытия ресурса
     * @param _resource имя ресурса
     * @return массив Image с картинками
     * @throws Exception порождается если произошли проблемы при чтении
     */
    public static final Image [] loadSeparatedImagesContainer(Class _class,String _resource) throws Exception
    {
        InputStream p_inStream = _class.getResourceAsStream(_resource);
        int i_imagesNumber = p_inStream.read()+1;

        int i_maxFileSize = p_inStream.read()<<8;
        i_maxFileSize |= p_inStream.read();

        byte [] ab_pngArray = new byte[i_maxFileSize];

        final Image [] ap_resultArray = new Image[i_imagesNumber];
        int i_indx = 0;
        while(i_imagesNumber!=0)
        {
            int i_size = p_inStream.read()<<8;
            i_size |= p_inStream.read();

            p_inStream.read(ab_pngArray,0,i_size);
            Image p_tileImage = Image.createImage(ab_pngArray,0,i_size);
            ap_resultArray[i_indx++] = p_tileImage;
            i_imagesNumber--;
        }
        p_inStream.close();
        p_inStream = null;
        ab_pngArray = null;
        return ap_resultArray;
    }
//#endif
}

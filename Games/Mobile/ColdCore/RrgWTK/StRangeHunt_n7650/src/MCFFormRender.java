import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.io.DataInputStream;
import java.io.InputStream;

//#local STRINGS = false
//#local FONTS = false
//#local PATHSDATA = false
//#local PATHSDATA_SAVESTEPS = false
//#local PATHSDATA_SAVEBOUNDIARY = false
//#local PATHSDATA_SAVEMAINPOINT = false
//#local CHANNEL = true
//#local IMAGESONLY = false
//#local NATIVEFONTS = false
//#local HASANCHORINFO = true
//#local OPTIONALELEMENTS = false
//#local MODIFIED = false

//#local COMPONENT_CLIPAREA = true
//#local COMPONENT_IMAGE = true
//#local COMPONENT_BUTTON = false
//#local COMPONENT_TEXTLABEL = false


/**
 * Класс реализует рендер MCF файлов (по спецификации 2.3)
 * @author Игорь Мазница
 * @version 2.05 (13 dec 2005)
 */
public class MCFFormRender
{
//#local STATIC = false
//#local CACHESTRINGS = false

    private static final int FORMAT_VERSION = 0x10;

    //#if STATIC
    public static byte [] ab_formsData;
    private static int [] ai_formsOffset;
    private static int i_formsNumber;


    //#if PATHSDATA
    public static short [] ash_PathArray;
    //#endif

    //#-
    private static boolean lg_hasModified;
    private static boolean lg_hasAnchor;
    private static boolean lg_hasPath;
    //#+

    private static int i_selectedFormOffset;
    public static int i_selectedFormIndex;

    private static int i_selectedFormWidth;
    private static int i_selectedFormHeight;
    private static int i_backroundRGB;

    //#if STRINGS
    public static byte [] ab_Strings;
    public static short [] ash_StringTable;

        //#if CACHESTRINGS
            public static String [] as_CacheStrings;
        //#endif
    //#endif

    //#if FONTS
    public static byte [] ab_Fonts;
    //#endif

    public static Image [] ap_Images;
    //#else

    //#if PATHSDATA
    //$private short [] ash_PathArray;
    //#endif

    //$public  byte [] ab_formsData;
    //$private int [] ai_formsOffset;
    //$private int i_formsNumber;

    //#-
    //$private boolean lg_hasModified;
    //$private boolean lg_hasAnchor;
    //$private boolean lg_hasPath;
    //#+

    //$private int i_selectedFormOffset;
    //$public int i_selectedFormIndex;

    //$private int i_selectedFormWidth;
    //$private int i_selectedFormHeight;
    //$private int i_backroundRGB;

    //$public Image [] ap_Images;

    //#if STRINGS
    //$public byte [] ab_Strings;
    //$public short [] ash_StringTable;

        //#if CACHESTRINGS
            //$public String [] as_CacheStrings;
        //#endif
    //#endif

    //#if FONTS
    //$public byte [] ab_Fonts;
    //#endif

    //#endif

    //#-
    //#if STATIC
        private static boolean lg_hasChannelData;
    //#else
        //$private boolean lg_hasChannelData;
    //#endif
    //#+

    //#-
    //#if STATIC
        private static boolean lg_imagesOnly;
    //#else
        //$private boolean lg_imagesOnly;
    //#endif
    //#+

    private static final int FONTDATASIZEINBYTES = 5;

    private static final int FLAG_HASMODIFIEDIMAGES = 1;// флаг наличия модифицируемых изображений
    private static final int FLAG_IMAGES_ONLY = 2; // флаг, что хранятся только изображения
    private static final int FLAG_CHANNELDATA = 4; // флаг, что присутствует информация о канале
    private static final int FLAG_STRINGS = 8; // флаг, что присутствует информация о строках
    private static final int FLAG_FONTS = 16; // флаг, что присутствует информация о фонтах
    private static final int FLAG_HASANCHOR = 32; // флаг, что присутствует информация о якорях
    private static final int FLAG_PATHS = 64; // флаг, что присутствует информация о путях

    private static final int COMPONENTFLAG_OPTIONAL = 0x80; // флаг, запросить подтверждение на вывод данного компонента

    //#if !IMAGESONLY
    private static final int COMPONENT_IMAGE = 0;
    private static final int COMPONENT_CLIPAREA = 1;
    private static final int COMPONENT_BUTTON = 2;
    private static final int COMPONENT_TEXTLABEL = 3;

    //
    // Интерфейс описывает класс, способный взаимодействовать с рендером форм через определенные функции
    //
    //public static interface MCFListener
    //{
        //Отработать функцию до вывода кнопки
        public static final int BUTTON_DRAWBUTTONBEFORE = 0xF0;

        //Отработать функцию после вывода кнопки
        public static final int BUTTON_DRAWBUTTONAFTER = 0xF00;

        //Кнопка в состоянии NORMAL
        public static final int BUTTON_NORMAL = 0;

        //Кнопка в состоянии PRESSED
        public static final int BUTTON_PRESSED = 1;

        //Кнопка в состоянии FOCUSED
        public static final int BUTTON_FOCUSED = 2;

        //Кнопка в состоянии DISABLED
        public static final int BUTTON_DISABLED = 3;

        /*
         * Функция отрисовывает прямоугольную зону в заданных координатах
         * @param _gr канвас
         * @param _componentIndex индекс компонента
         * @param _x X координата верхнего левого края
         * @param _y Y координата верхнего левого края
         * @param _width ширина зоны
         * @param _height высота зоны
         * @param _channel канал компонента
         */
        //public void areaComponentPaint(Graphics _gr,int _componentIndex,int _x,int _y,int _width,int _height,int _channel);

        /*
         * Функция возвращает состояние кнопки на форме
         * @param _componentIndex индекс компонента
         * @param _channel канал компонента
         * @return целочисленное значение состояния кнопки
         */
        //public int getButtonState(int _componentIndex, int _channel);
    //}
    //#endif

    //#if STATIC
    public static void init(Class _parent,String _resource) throws Exception
    //#else
    //$public void init(Class _parent,String _resource) throws Exception
    //#endif
    {
        i_selectedFormOffset = -1;
        InputStream p_inStr = _parent.getResourceAsStream(_resource);
        DataInputStream p_inStream = new DataInputStream(p_inStr);
        p_inStr = null;

        if (p_inStream.readUnsignedByte()!=FORMAT_VERSION)
            throw new Exception("Error format");

        i_formsNumber = p_inStream.readUnsignedByte()+1;
        ai_formsOffset = new int[i_formsNumber];

        int i_flags = p_inStream.readUnsignedByte();

        //#-
        lg_hasModified = (i_flags & FLAG_HASMODIFIEDIMAGES) !=0;
        lg_imagesOnly = (i_flags & FLAG_IMAGES_ONLY) !=0;
        lg_hasChannelData = (i_flags & FLAG_CHANNELDATA) !=0;
        lg_hasAnchor = (i_flags & FLAG_HASANCHOR) !=0;
        lg_hasPath = (i_flags & FLAG_PATHS) !=0;
        //#+

        int i_formsData = p_inStream.readUnsignedShort();
        ab_formsData = new byte[i_formsData];
        p_inStream.read(ab_formsData);

        // Вычисляем смещения форм
        int i_pos = 0;
        final int i_len = i_formsData;

        int i_form = 0;

        while(i_pos<i_len)
        {
            ai_formsOffset[i_form++] = i_pos;

            i_pos+=5;

            int i_components = ab_formsData[i_pos++] & 0xFF;
            for(int li=0;li<i_components;li++)
            {
                //#if IMAGESONLY
                //#-
                if (lg_imagesOnly)
                {
                //#+
                    // X, Y
                    i_pos += 2;

                    //#if CHANNEL
                    // Channel
                    //#-
                    if (lg_hasChannelData)
                    //#+
                        i_pos ++;
                    //#endif

					//#if HASANCHORINFO
                    // anchor
                    i_pos ++;
					//#endif

                    // модификатор
                    //#if MODIFIED
                    //#-
                    if (lg_hasModified)
                    //#+
                        i_pos ++;
                    //#endif

                    // индекс
                    i_pos++;
                //#-
                }
                //#+
                //#else
                //#-
                else
                {
                //#+
                    int i_componenttype = ab_formsData[i_pos++];
                    // X, Y
                    i_pos += 2;

                    //#if CHANNEL
                    // Channel
                    //#-
                    if (lg_hasChannelData)
                    //#+
                        i_pos ++;
                    //#endif

                    // anchor
                    //#if HASANCHORINFO
                    //#-
                    if (lg_hasAnchor)
                    //#+
                    i_pos ++;
                    //#endif

                    switch(i_componenttype)
                    {
                        //#if COMPONENT_IMAGE
                        case COMPONENT_IMAGE :
                            {
                                //#if MODIFIED
                                //#-
                                if (lg_hasModified)
                                //#+
                                    i_pos ++;
                                //#endif

                                i_pos++;
                            };break;
                        //#endif
                        //#if COMPONENT_CLIPAREA
                        case COMPONENT_CLIPAREA :
                            {
                                i_pos+=2;
                            };break;
                        //#endif
                        //#if COMPONENT_BUTTON
                        case COMPONENT_BUTTON :
                            {
                                i_pos += 4;
                            };break;
                        //#endif
                        //#if COMPONENT_TEXTLABEL
                        case COMPONENT_TEXTLABEL :
                            {
                                i_pos += 5;
                            };break;
                        //#endif
                    }
                //#-
                }
                //#+
                //#endif
            }
        }

        //#if STRINGS
        if ((i_flags & FLAG_STRINGS)!=0)
        {
            int i_stringsNumber = p_inStream.readUnsignedByte();
            ash_StringTable = new short[i_stringsNumber];
            //#if CACHESTRINGS
                as_CacheStrings = new String[i_stringsNumber];
            //#endif

            // Читаем таблицу
            for(int li=0;li<i_stringsNumber;li++) ash_StringTable[li] = p_inStream.readShort();
            // Читаем массив строк
            int i_arraySize = p_inStream.readUnsignedShort();
            ab_Strings = new byte[i_arraySize];
            p_inStream.read(ab_Strings);
        }
        //#endif

        //#if FONTS
        if ((i_flags & FLAG_FONTS)!=0)
        {
            int i_fontsNumber = p_inStream.readUnsignedByte();
            int i_arrayLen = FONTDATASIZEINBYTES * i_fontsNumber;
            ab_Fonts = new byte[i_arrayLen];
            p_inStream.read(ab_Fonts);
        }
        //#endif

        //#if PATHSDATA
        //#-
        if (lg_hasPath)
        //#+
        {
            final int PATHFLAG_SHORT = 1;
            final int PATHFLAG_HASBOUNDARYINFO = 2;
            final int PATHFLAG_HASMAINPOINTINFO = 4;
            final int PATHFLAG_HASSTEPSINFO = 8;

            int i_pathsNumber = p_inStream.readUnsignedByte()+1;
            int i_pathCellsNumber = p_inStream.readUnsignedShort();
            ash_PathArray = new short[i_pathCellsNumber];
            short [] ash_paths = ash_PathArray;

            int i_index = 0;
            while(i_pathsNumber!=0)
            {
                // Считываем путь

                // Флаги хранения пути
                int i_savedFlags = p_inStream.readUnsignedByte();
                final boolean lg_asShort = (i_savedFlags & PATHFLAG_SHORT)!=0;
                final boolean lg_hasBoundary = (i_savedFlags & PATHFLAG_HASBOUNDARYINFO)!=0;
                final boolean lg_hasMainPointInfo = (i_savedFlags & PATHFLAG_HASMAINPOINTINFO)!=0;
                final boolean lg_hasSteps = (i_savedFlags & PATHFLAG_HASSTEPSINFO)!=0;

                // Тип пути и события
                ash_paths[i_index++] = (short)p_inStream.readUnsignedByte();

                //#if PATHSDATA_SAVEBOUNDIARY
                // Границы пути
                if (lg_hasBoundary)
                {
                    if (lg_asShort)
                    {
                        // w
                        ash_paths[i_index++] = p_inStream.readShort();
                        // h
                        ash_paths[i_index++] = p_inStream.readShort();
                    }
                    else
                    {
                        // w
                        ash_paths[i_index++] = (short)p_inStream.readUnsignedByte();
                        // h
                        ash_paths[i_index++] = (short)p_inStream.readUnsignedByte();
                    }
                }
                //#endif

                //#if PATHSDATA_SAVEMAINPOINT
                // Главная точка
                if (lg_hasMainPointInfo)
                {
                    if (lg_asShort)
                    {
                        // x
                        ash_paths[i_index++] = p_inStream.readShort();
                        // y
                        ash_paths[i_index++] = p_inStream.readShort();
                    }
                    else
                    {
                        // x
                        ash_paths[i_index++] = (short)p_inStream.readUnsignedByte();
                        // y
                        ash_paths[i_index++] = (short)p_inStream.readUnsignedByte();
                    }
                }
                //#endif

                // Точки пути
                int i_pointsNumber = p_inStream.readUnsignedByte()+1;
                while(i_pointsNumber>0)
                {
                    if (lg_asShort)
                    {
                        // x
                        ash_paths[i_index++] = p_inStream.readShort();
                        // y
                        ash_paths[i_index++] = p_inStream.readShort();

                        //#if PATHSDATA_SAVESTEPS
                        if (lg_hasSteps)
                            ash_paths[i_index++] = p_inStream.readShort();
                        //#endif
                    }
                    else
                    {
                        // x
                        ash_paths[i_index++] = (short)p_inStream.readUnsignedByte();
                        // y
                        ash_paths[i_index++] = (short)p_inStream.readUnsignedByte();

                        //#if PATHSDATA_SAVESTEPS
                        if (lg_hasSteps)
                            ash_paths[i_index++] = (short)p_inStream.readUnsignedByte();
                        //#endif
                    }
                    i_pointsNumber--;
                }

                i_pathsNumber--;
            }
        }
        //#endif

        int i_imagesNumber =  p_inStream.readUnsignedShort();
        ap_Images = new Image[i_imagesNumber];
        int i_maxImageDataBlock = p_inStream.readUnsignedShort();
        p_inStream.skip(2); // пропускаем размер блока данных изображений

        byte [] ab_imageArray = new byte[i_maxImageDataBlock];

        while(i_imagesNumber!=0)
        {
            int i_ArrayIndex = p_inStream.readUnsignedShort();
            int i_dataBlockLen = p_inStream.readUnsignedShort();

            p_inStream.read(ab_imageArray,0,i_dataBlockLen);
            Image p_img = Image.createImage(ab_imageArray,0,i_dataBlockLen);

            ap_Images[i_ArrayIndex] = p_img;
            i_imagesNumber--;
        }
        ab_imageArray = null;
        p_inStream.close();
        p_inStream = null;
        Runtime.getRuntime().gc();
    }



    //#if STATIC
    public static final void selectForm(int _index)
    //#else
    //$public final void selectForm(int _index)
    //#endif
    {
        i_selectedFormIndex = _index;
        int i_indexPos = ai_formsOffset[_index];

        final int i_r = ab_formsData[i_indexPos++]&0xFF;
        final int i_g = ab_formsData[i_indexPos++]&0xFF;
        final int i_b = ab_formsData[i_indexPos++]&0xFF;

        i_backroundRGB = (i_r<<16) | (i_g<<8) | i_b;
        i_selectedFormWidth  = (ab_formsData[i_indexPos++]&0xFF)+1;
        i_selectedFormHeight = (ab_formsData[i_indexPos++]&0xFF)+1;

        i_selectedFormOffset = i_indexPos;
    }

    //#if STATIC
    public static final int getFormWidth(int _index)
    //#else
    //$public final int getFormWidth(int _index)
    //#endif
    {
        int i_indexPos = ai_formsOffset[_index];
        i_indexPos += 3;

        return (ab_formsData[i_indexPos]&0xFF)+1;
    }

    //#if STATIC
    public static final int getFormHeight(int _index)
    //#else
    //$public final int getFormHeight(int _index)
    //#endif
    {
        int i_indexPos = ai_formsOffset[_index];
        i_indexPos += 4;

        return (ab_formsData[i_indexPos]&0xFF)+1;
    }

    //#if STATIC
    public static final void paint(Graphics _g,int _x,int _y,boolean _opaque)
    //#else
    //$public final void paint(Graphics _g,int _x,int _y,boolean _opaque)
    //#endif
    {
        _g.translate(_x,_y);

        //#if SHOWSYS
        try{
        //#endif

        int i_pos = i_selectedFormOffset;

        final byte [] ab_formData = ab_formsData;
        final Image [] ap_formImages = ap_Images;

        final int i_selFW = i_selectedFormWidth;
        final int i_selFH = i_selectedFormHeight;

        _g.setClip(0,0,i_selFW,i_selFH);
        //#if MIDP=="1.0" && VENDOR=="NOKIA"
        //$com.nokia.mid.ui.DirectGraphics p_dirGraphics = com.nokia.mid.ui.DirectUtils.getDirectGraphics(_g);
        //#endif

        //#if !IMAGESONLY
        //final MCFListener p_listener = p_MCFListener;
        //#endif

        if (_opaque)
        {
            _g.setColor(i_backroundRGB);
            _g.fillRect(0,0,i_selFW,i_selFH);
        }

        final int i_components = ab_formData[i_pos++]&0xFF;
        final int i_formIndex = i_selectedFormIndex;

        for(int i_componentIndex =0;i_componentIndex < i_components;i_componentIndex ++)
        {
                // тип компонента
                //#if !IMAGESONLY
                int i_compoType = COMPONENT_IMAGE;

                //#-
                if (!lg_imagesOnly)
                {
                //#+
                    i_compoType = ab_formData[i_pos++];

                    //#if OPTIONALELEMENTS
                    if ((i_compoType&COMPONENTFLAG_OPTIONAL)!=0)
                    {
                        //todo запрос на обработку
                    }
                    //#endif
                    i_compoType &= 0x7F;
                //#-
                }
                //#+
                //#endif

                int i_x = ab_formData[i_pos++] & 0xFF;
                int i_y = ab_formData[i_pos++] & 0xFF;

                int i_channel = 0;

                //#if CHANNEL
                //#-
                if (lg_hasChannelData)
                //#+
                    i_channel = ab_formData[i_pos++] & 0xFF;
                //#endif

                //#if HASANCHORINFO
                int i_anchor = 0x14;
                //#-
                if (lg_hasAnchor)
                //#+
                    i_anchor = ab_formData[i_pos++] & 0xFF;
                //#endif

                //#if !IMAGESONLY
                switch(i_compoType)
                {
                        //#if COMPONENT_IMAGE
                        case COMPONENT_IMAGE :
                        {
                        //#endif
                //#endif
                        //#if COMPONENT_IMAGE

                            int i_modifier = 0;

                            //#if MODIFIED
                            //#-
                            if (lg_hasModified)
                            //#+
                                i_modifier = ab_formData[i_pos++];
                            //#endif

                            final int i_index = ab_formData[i_pos++] & 0xFF;
                            final Image p_img = ap_formImages[i_index];

                            _g.setClip(0,0,i_selFW,i_selFH);

                            //#if MIDP=="2.0"
                                if (i_modifier==0)
                                {
                                    //#if HASANCHORINFO
                                    _g.drawImage(p_img,i_x,i_y,i_anchor);
                                    //#else
                                    //$_g.drawImage(p_img,i_x,i_y,0x14);
                                    //#endif
                                }
                                else
                                {
                                    int i_m = javax.microedition.lcdui.game.Sprite.TRANS_NONE;
                                    if ((i_modifier & 1) != 0) i_m = javax.microedition.lcdui.game.Sprite.TRANS_MIRROR;
                                    if ((i_modifier & 2) != 0) i_m |= javax.microedition.lcdui.game.Sprite.TRANS_MIRROR_ROT180;

                                    //#if HASANCHORINFO
                                    _g.drawRegion(p_img,0,0,p_img.getWidth(),p_img.getHeight(),i_m,i_x,i_y,i_anchor);
                                    //#else
                                    //$_g.drawRegion(p_img,0,0,p_img.getWidth(),p_img.getHeight(),i_m,i_x,i_y,0x14);
                                    //#endif
                                }
                            //#else
                                //#if MIDP=="1.0" && VENDOR=="NOKIA"
                                //$    int i_m = 0;
                                //$    if ((i_modifier & 1) != 0) i_m = com.nokia.mid.ui.DirectGraphics.FLIP_HORIZONTAL;
                                //$    if ((i_modifier & 2) != 0) i_m |= com.nokia.mid.ui.DirectGraphics.FLIP_VERTICAL;
                                    //#if HASANCHORINFO
                                        //$    p_dirGraphics.drawImage(p_img,i_x,i_y,i_anchor,i_m);
                                    //#else
                                        //$    p_dirGraphics.drawImage(p_img,i_x,i_y,0x14,i_m);
                                    //#endif
                                //#else
                                    //#if HASANCHORINFO
                                    //$_g.drawImage(p_img,i_x,i_y,i_anchor);
                                    //#else
                                    //$_g.drawImage(p_img,i_x,i_y,0x14);
                                    //#endif
                                //#endif
                            //#endif
                      //#endif


                 //#if !IMAGESONLY
                        //#if COMPONENT_IMAGE
                        };break;
                        //#endif

                        //#if COMPONENT_CLIPAREA
                        case COMPONENT_CLIPAREA :
                        {
                            int i_width = ab_formData[i_pos++] & 0xFF;
                            int i_height = ab_formData[i_pos++] & 0xFF;

                            //#if HASANCHORINFO
                            if (i_anchor!=0x14)
                            {
                                if ((i_anchor & 8)!=0)
                                {
                                    i_x -= i_width;
                                }
                                if ((i_anchor & 32)!=0)
                                {
                                    i_y -= i_height;
                                }
                            }
                            //#endif

                            startup.onAreaComponentPaint(_g, i_formIndex,i_x,i_y,i_width,i_height,i_channel);
                        };break;
                        //#endif

                        //#if COMPONENT_BUTTON
                        case COMPONENT_BUTTON :
                        {
                            int i_normalImage = ab_formsData[i_pos++] & 0xFF;
                            int i_focusedImage = ab_formsData[i_pos++] & 0xFF;
                            int i_pressedImage = ab_formsData[i_pos++] & 0xFF;
                            int i_disabledImage = ab_formsData[i_pos++] & 0xFF;

                            int i_imageIndex = i_normalImage;
                            int i_buttonState = startup.onGetButtonState(i_componentIndex,i_channel);
                                switch(i_buttonState&0xF)
                                {
                                   case  BUTTON_NORMAL : i_imageIndex = i_normalImage;break;
                                   case  BUTTON_FOCUSED : i_imageIndex = i_focusedImage;break;
                                   case  BUTTON_PRESSED : i_imageIndex = i_pressedImage;break;
                                   case  BUTTON_DISABLED : i_imageIndex = i_disabledImage;break;
                                }

                            final Image p_img = ap_formImages[i_imageIndex];

                            //#if HASANCHORINFO
                            if ((i_buttonState & BUTTON_DRAWBUTTONBEFORE)!=0) startup.onButtonBeforeDrawn(_g,i_componentIndex,i_channel,i_x,i_y,p_img.getWidth(),p_img.getHeight(),i_anchor);
                            _g.drawImage(p_img,i_x,i_y,i_anchor);
                            if ((i_buttonState & BUTTON_DRAWBUTTONAFTER)!=0) startup.onButtonDrawn(_g,i_componentIndex,i_channel,i_x,i_y,i_anchor);
                            //#else
                            //$if ((i_buttonState & BUTTON_DRAWBUTTONBEFORE)!=0) startup.onButtonBeforeDrawn(_g,i_componentIndex,i_channel,i_x,i_y,0x14);
                            //$_g.drawImage(p_img,i_x,i_y,0x14);
                            //$if ((i_buttonState & BUTTON_DRAWBUTTONAFTER)!=0) startup.onButtonDrawn(_g,i_componentIndex,i_channel,i_x,i_y,0x14);
                            //#endif
                        };break;
                        //#endif

                        //#if COMPONENT_TEXTLABEL
                        case COMPONENT_TEXTLABEL :
                        {
                            int i_stringIndex = ab_formData[i_pos++] & 0xFF;
                            int i_fontIndex = (ab_formData[i_pos++] & 0xFF)-1;
                            int i_rgb = 0;

                            //#if NATIVEFONTS
                            if (i_fontIndex<0)
                            {
                                i_rgb = ab_formData[i_pos++]&0xFF;
                                i_rgb <<= 8;
                                i_rgb |= ab_formData[i_pos++]&0xFF;
                                i_rgb <<= 8;
                                i_rgb |= ab_formData[i_pos++]&0xFF;
                                _g.setColor(i_rgb);

                                //#if CACHESTRINGS
                                    String s_string = as_CacheStrings[i_stringIndex];
                                    if (s_string == null)
                                    {
                                        s_string = decodeString(i_stringIndex);
                                        as_CacheStrings[i_stringIndex] = s_string;
                                    }
                                //#else
                                    //$String s_string = decodeString(i_stringIndex);
                                //#endif
                                //#if HASHANCHORINFO
                                _g.drawString(s_string,i_x,i_y,i_anchor);
                                //#else
                                //$_g.drawString(s_string,i_x,i_y,0x14);
                                //#endif
                            }
                            else
                            //#endif
                            {
                                i_pos += 3;
                                //#if FONTS
                                drawStringWithFont(_g,_x,_y,i_fontIndex,i_stringIndex);
                                _g.setClip(0,0,i_selFW,i_selFH);
                                //#endif
                            }
                        };break;
                        //#endif
            }
            //#endif
        }

        //#if SHOWSYS
        }catch(Exception _ex)
        {
            _ex.printStackTrace();
        }
        //#endif

        _g.translate(-_x,-_y);
    }

    //#if NATIVEFONTS
    public static final String decodeString(int _index)
    {
        int i_stringOffset = ash_StringTable[_index]&0xFFFF;
        final String CHARSET = "~ 0123456789:.,!?+-/\'\"()ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзийклмнопрстуфхцчшщъыьэюя";
        final byte [] ab_stringsArray = ab_Strings;
        int i_len = (ab_stringsArray[i_stringOffset++]&0xFF)+1;
        StringBuffer p_strBuff = new StringBuffer(i_len);
        while(i_len>0)
        {
            int i_val = ab_stringsArray[i_stringOffset++] & 0xFF;
            p_strBuff.append(CHARSET.charAt(i_val));
            i_len--;
        }
        return p_strBuff.toString();
    }
    //#endif


    //#if STRINGS && FONTS
    public static final void drawStringWithFont(Graphics _graphics,int _x,int _y,int _fontIndex,int _stringIndex)
    {
        int i_stringOffset = ash_StringTable[_stringIndex]&0xFFFF;
        int i_fontOffset = _fontIndex * FONTDATASIZEINBYTES;
        final int i_imageFontIndex = ab_Fonts[i_fontOffset++]&0xFF;
        final int i_charWidth = ab_Fonts[i_fontOffset++]&0xFF;
        final int i_charHeight = ab_Fonts[i_fontOffset++]&0xFF;
        final int i_charHorzInterval = ab_Fonts[i_fontOffset++]&0xFF;
        final int i_charVertInterval = ab_Fonts[i_fontOffset]&0xFF;

        final Image p_fontImage = ap_Images[i_imageFontIndex];
        int i_stringLen = ab_Strings[i_stringOffset++]&0xFF;
        int i_x = _x;
        while(i_stringLen>0)
        {
            int i_char = ab_Strings[i_stringOffset++]&0xFF;

            switch(i_char)
            {
                 case 0x00 : {
                        // Перевод каретки
                        _y += i_charHeight + i_charVertInterval;
                        i_x = _x;
                    };break;
                 case 0x01 :
                    {
                        // Пробел
                        i_x += i_charWidth + i_charHorzInterval;
                    };break;
                 default:
                    {
                        int i_imgy = (i_char>>>4)*i_charHeight;
                        int i_imgx = (i_char & 0xF)*i_charWidth;
                        _graphics.setClip(i_x,_y,i_charWidth,i_charHeight);
                        _graphics.drawImage(p_fontImage,i_x-i_imgx,_y-i_imgy,0x14);
                    }
            }

            i_stringLen--;
        }
    }
    //#endif

    //#if STATIC
    public static final void realize()
    //#else
    //$public final void realize()
    //#endif
    {
        ai_formsOffset = null;
        ap_Images = null;
        ab_formsData = null;

        //#if STRINGS
        ab_Strings = null;
            //#if CACHESTRINGS
            as_CacheStrings = null;
            //#endif
        //#endif

        //#if FONTS
        ab_Fonts =null;
        //#endif

        Runtime.getRuntime().gc();
    }


//------------MCI FORMS-----------------
    protected static final int MCF_FORM_GAMEAREA = 0;
    protected static final int MCF_CMP_GAMEAREA_area_water = 0;
    protected static final int MCF_CMP_GAMEAREA_area_water_OFFSET = 6;
    protected static final int MCF_CMP_GAMEAREA_area_sky = 1;
    protected static final int MCF_CMP_GAMEAREA_area_sky_OFFSET = 13;
    protected static final int MCF_CMP_GAMEAREA_cloud1 = 2;
    protected static final int MCF_CMP_GAMEAREA_cloud1_OFFSET = 20;
    protected static final int MCF_CMP_GAMEAREA_cloud2 = 3;
    protected static final int MCF_CMP_GAMEAREA_cloud2_OFFSET = 26;
    protected static final int MCF_CMP_GAMEAREA_cloud3 = 4;
    protected static final int MCF_CMP_GAMEAREA_cloud3_OFFSET = 32;
    protected static final int MCF_CMP_GAMEAREA_gamearea = 5;
    protected static final int MCF_CMP_GAMEAREA_gamearea_OFFSET = 38;
    protected static final int MCF_CMP_GAMEAREA_ATT_AREA = 6;
    protected static final int MCF_CMP_GAMEAREA_ATT_AREA_OFFSET = 45;
    protected static final int MCF_CMP_GAMEAREA_SCORE_AREA = 7;
    protected static final int MCF_CMP_GAMEAREA_SCORE_AREA_OFFSET = 52;
    protected static final int MCF_CMP_GAMEAREA_MENU = 8;
    protected static final int MCF_CMP_GAMEAREA_MENU_OFFSET = 59;
    protected static final int MCF_FORM_SPRITES = 1;
//------------MCF IMAGES-----------------
   protected static final int MCF_IMG_trostnik = 1;
   protected static final int MCF_IMG_cloud = 0;
   protected static final int MCF_IMG_MENUPNTR = 2;
   protected static final int MCF_IMG_medkit = 13;
   protected static final int MCF_IMG_dig9 = 12;
   protected static final int MCF_IMG_dig8 = 11;
   protected static final int MCF_IMG_dig7 = 10;
   protected static final int MCF_IMG_dig6 = 9;
   protected static final int MCF_IMG_dig5 = 8;
   protected static final int MCF_IMG_dig4 = 7;
   protected static final int MCF_IMG_dig3 = 6;
   protected static final int MCF_IMG_dig2 = 5;
   protected static final int MCF_IMG_dig1 = 4;
   protected static final int MCF_IMG_dig0 = 3;
}

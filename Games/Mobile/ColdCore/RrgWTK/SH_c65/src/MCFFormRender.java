import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.io.DataInputStream;
import java.io.InputStream;

//#local IMAGESONLY = true
//#local CHANNEL = false

//#local STATIC = true

/**
 * Класс реализует рендер MCF файлов (по спецификации 2.0)
 * @author Игорь Мазница
 * @version 1.01
 */
public class MCFFormRender
{
    //#if STATIC
    private static byte [] ab_formsData;
    private static int [] ai_formsOffset;
    private static int i_formsNumber;
    private static boolean lg_hasModified;

    private static int i_selectedFormOffset;
    public static int i_selectedFormIndex;

    private static int i_selectedFormWidth;
    private static int i_selectedFormHeight;
    private static int i_backroundRGB;

    public static Image [] ap_Images;
    //#else
    //$private byte [] ab_formsData;
    //$private int [] ai_formsOffset;
    //$private int i_formsNumber;
    //$private boolean lg_hasModified;

    //$private int i_selectedFormOffset;
    //$public int i_selectedFormIndex;

    //$private int i_selectedFormWidth;
    //$private int i_selectedFormHeight;
    //$private int i_backroundRGB;

    //$public Image [] ap_Images;
    //#endif

    //#if CHANNEL
        //#if STATIC
            private static boolean lg_hasChannelData;
        //#else
            //$private boolean lg_hasChannelData;
        //#endif
    //#endif

    //#if !IMAGESONLY
        //#if STATIC
            private static boolean lg_imagesOnly;
        //#else
            //$private boolean lg_imagesOnly;
        //#endif
    //#endif

    private static final int FLAG_HASMODIFIEDIMAGES = 1;// флаг наличия модифицируемых изображений
    private static final int FLAG_IMAGES_ONLY = 2; // флаг, что хранятся только изображения
    private static final int FLAG_CHANNELDATA = 4; // флаг, что присутствует информация о канале

    //#if !IMAGESONLY
    private static final int COMPONENT_IMAGE = 0;
    private static final int COMPONENT_CLIPAREA = 1;
    private static final int COMPONENT_BUTTON = 2;

    //
    // Интерфейс описывает класс, способный взаимодействовать с рендером форм через определенные функции
    //
    //public static interface MCFListener
    //{
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
    public static final void init(Class _parent,String _resource) throws Exception
    //#else
    //$public void init(Class _parent,String _resource) throws Exception
    //#endif
    {
        i_selectedFormOffset = -1;
        InputStream p_inStr = _parent.getResourceAsStream(_resource);
        DataInputStream p_inStream = new DataInputStream(p_inStr);
        p_inStr = null;
        i_formsNumber = p_inStream.readUnsignedByte()+1;
        ai_formsOffset = new int[i_formsNumber];

        int i_flags = p_inStream.readUnsignedByte();

        lg_hasModified = (i_flags & FLAG_HASMODIFIEDIMAGES) !=0;

        //#if !IMAGESONLY
        lg_imagesOnly = (i_flags & FLAG_IMAGES_ONLY) !=0;
        //#endif

        //#if CHANNEL
        lg_hasChannelData = (i_flags & FLAG_CHANNELDATA) !=0;
        //#endif

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
                //#if !IMAGESONLY
                if (lg_imagesOnly)
                {
                //#endif
                    // X, Y
                    i_pos += 2;

                    //#if CHANNEL
                    // Channel
                    if (lg_hasChannelData) i_pos ++;
                    //#endif

                    // anchor
                    i_pos ++;
                    // модификатор
                    if (lg_hasModified) i_pos ++;
                    // индекс
                    i_pos++;
                //#if !IMAGESONLY
                }
                else
                {
                    int i_componenttype = ab_formsData[i_pos++];

                    // X, Y
                    i_pos += 2;

                    //#if CHANNEL
                    // Channel
                    if (lg_hasChannelData) i_pos ++;
                    //#endif

                    switch(i_componenttype)
                    {
                        case COMPONENT_IMAGE :
                            {
                                i_pos ++;
                                if (lg_hasModified) i_pos ++;
                                i_pos++;
                            };break;
                        case COMPONENT_CLIPAREA :
                            {
                                i_pos+=2;
                            };break;
                        case COMPONENT_BUTTON :
                            {
                                i_pos += 4;
                            };break;
                    }
                }
                //#endif
            }
        }

        int i_imagesNumber =  p_inStream.readUnsignedByte();

        ap_Images = new Image[i_imagesNumber];

        p_inStream.skip(2);

        i_pos = 0;

        byte [] ab_imageArray = null;

        while(i_imagesNumber!=0)
        {
            int i_dataBlockLen = p_inStream.readUnsignedShort();

            ab_imageArray = new byte[i_dataBlockLen];
            p_inStream.read(ab_imageArray);
            Image p_img = Image.createImage(ab_imageArray,0,i_dataBlockLen);
            ab_imageArray = null;

            ap_Images[i_pos++] = p_img;
            i_imagesNumber--;
        }

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
    public static final void paint(Graphics _g,int _x,int _y,boolean _opaque)
    //#else
    //$public final void paint(Graphics _g,int _x,int _y,boolean _opaque)
    //#endif
    {
        _g.translate(_x,_y);

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
            _g.fillRect(_x,_y,i_selFW,i_selFH);
        }

        final int i_components = ab_formData[i_pos++]&0xFF;

        for(int i_componentIndex =0;i_componentIndex < i_components;i_componentIndex ++)
        {
                // тип компонента
                //#if !IMAGESONLY
                int i_compoType = COMPONENT_IMAGE;
                if (!lg_imagesOnly) i_compoType = ab_formData[i_pos++];
                //#endif

                int i_x = ab_formData[i_pos++] & 0xFF;
                int i_y = ab_formData[i_pos++] & 0xFF;

                int i_channel = 0;

                //#if CHANNEL
                if (lg_hasChannelData)
                {
                    i_channel = ab_formData[i_pos++] & 0xFF;
                }
                //#endif

                //#if !IMAGESONLY
                switch(i_compoType)
                {
                        case COMPONENT_IMAGE :
                        {
                //#endif
                            int i_ancor = ab_formData[i_pos++] & 0xFF;

                            int i_modifier = 0;

                            if (lg_hasModified) i_modifier = ab_formData[i_pos++];

                            final int i_index = ab_formData[i_pos++] & 0xFF;
                            final Image p_img = ap_formImages[i_index];

                            //#if MIDP=="2.0"
                                if (i_modifier==0) _g.drawImage(p_img,i_x,i_y,i_ancor);
                                else
                                {
                                    int i_m = javax.microedition.lcdui.game.Sprite.TRANS_NONE;
                                    if ((i_modifier & 1) != 0) i_m = javax.microedition.lcdui.game.Sprite.TRANS_MIRROR;
                                    if ((i_modifier & 2) != 0) i_m |= javax.microedition.lcdui.game.Sprite.TRANS_MIRROR_ROT180;

                                    _g.drawRegion(p_img,0,0,p_img.getWidth(),p_img.getHeight(),i_m,i_x,i_y,i_ancor);
                                }
                            //#else
                                //#if MIDP=="1.0" && VENDOR=="NOKIA"
                                //$    int i_m = 0;
                                //$    if ((i_modifier & 1) != 0) i_m = com.nokia.mid.ui.DirectGraphics.FLIP_HORIZONTAL;
                                //$    if ((i_modifier & 2) != 0) i_m |= com.nokia.mid.ui.DirectGraphics.FLIP_VERTICAL;
                                //$    p_dirGraphics.drawImage(p_img,i_x,i_y,i_ancor,i_m);
                                //#else
                                    //$_g.drawImage(p_img,i_x,i_y,i_ancor);
                                //#endif
                            //#endif

                 //#if !IMAGESONLY
                        };break;
                        case COMPONENT_CLIPAREA :
                        {
                            int i_width = ab_formData[i_pos++] & 0xFF;
                            int i_height = ab_formData[i_pos++] & 0xFF;

                            startup.areaComponentPaint(_g, i_componentIndex,i_x,i_y,i_width,i_height,i_channel);
                        };break;
                        case COMPONENT_BUTTON :
                        {
                            int i_normalImage = ab_formsData[i_pos++] & 0xFF;
                            int i_focusedImage = ab_formsData[i_pos++] & 0xFF;
                            int i_pressedImage = ab_formsData[i_pos++] & 0xFF;
                            int i_disabledImage = ab_formsData[i_pos++] & 0xFF;

                            int i_imageIndex = i_normalImage;
                                switch(startup.getButtonState(i_componentIndex,i_channel))
                                {
                                   case  BUTTON_NORMAL : i_imageIndex = i_normalImage;break;
                                   case  BUTTON_FOCUSED : i_imageIndex = i_focusedImage;break;
                                   case  BUTTON_PRESSED : i_imageIndex = i_pressedImage;break;
                                   case  BUTTON_DISABLED : i_imageIndex = i_disabledImage;break;
                                }
                            final Image p_img = ap_formImages[i_imageIndex];

                            _g.drawImage(p_img,i_x,i_y,0x14);
                        };break;
            }
            //#endif
        }

        _g.translate(-_x,-_y);
    }

    //#if STATIC
    public static final void realize()
    //#else
    //$public final void realize()
    //#endif
    {
        ai_formsOffset = null;
        ap_Images = null;
        ab_formsData = null;
        Runtime.getRuntime().gc();
    }
}

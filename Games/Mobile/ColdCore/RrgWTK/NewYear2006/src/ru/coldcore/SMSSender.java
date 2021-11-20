package ru.coldcore;

import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.Canvas;

public abstract class SMSSender
{
    private static final int TYPE_SIEMENS=0;
    private static final int TYPE_SAMSUNG=1;
    private static final int TYPE_JSR120=2;
    private static final int TYPE_NONE=3;

    public static final int LAYOUT_LEFT = 1; //Item.LAYOUT_LEFT;
    public static final int LAYOUT_RIGHT = 2; //Item.LAYOUT_RIGHT;
    public static final int LAYOUT_CENTER = 3; //Item.LAYOUT_CENTER;
    public static final int LAYOUT_NEWLINE_AFTER = 0x200; //Item.LAYOUT_NEWLINE_AFTER;
    public static final int LAYOUT_NEWLINE_BEFORE = 0x100; //Item.LAYOUT_NEWLINE_BEFORE;

    //#if SMSENGINE
    public boolean sendTextSMS(String _smsBody,String _phoneNumber)
    {
        return false;
    }

    public static boolean sendTxtSMS(String _smsBody,String _phoneNumber)
    {
        String s_class = null;
        switch(checkType())
        {
            case TYPE_SIEMENS : s_class = "ru.coldcore.Smn";break;
            case TYPE_SAMSUNG : s_class = "ru.coldcore.Smsn";break;
            case TYPE_JSR120 : s_class = "ru.coldcore.Wma";break;
            default: return false;
        }

        try
        {
            Class p_class = Class.forName(s_class);
            SMSSender p_sender = (SMSSender) p_class.newInstance();
            if (!p_sender.supportsSMS()) return false;

            return p_sender.sendTextSMS(_smsBody,_phoneNumber);
        }
        catch (Throwable e)
        {
            return false;
        }
    }

    private static int checkType()
    {
        Class p_class = null;
        try
        {
            Class p_class1 = Class.forName("javax.wireless.messaging.MessageConnection");
            Class p_class2 = Class.forName("javax.wireless.messaging.TextMessage");
            if (p_class1!=null && p_class2!=null) return TYPE_JSR120;
        }
        catch(Throwable _e){}
        try
        {
            p_class = Class.forName("com.siemens.mp.gsm.SMS");
            return TYPE_SIEMENS;
        }
        catch(Throwable _e){}
        try
        {
            p_class = Class.forName("com.samsung.util.SMS");
            //#if SAMSUNGSMS
            //$if (com.samsung.util.SMS.isSupported())return TYPE_SAMSUNG;
            //#endif
            return TYPE_NONE;
        }
        catch(Throwable _e){}
        return TYPE_NONE;
    }

    public static final boolean doesSupportSMS()
    {
        return checkType()!=TYPE_NONE;
    }

    public boolean supportsSMS()
    {
        return false;
    }
    //#endif

    //#if UPDATEURL
    public boolean plRequest(MIDlet _midlet,String _url)
    {
        return false;
    }

    public static boolean plReq(MIDlet _midlet,String _url)
    {
        String s_class = "ru.coldcore.Mpq";
        try
        {
            Class p_class = Class.forName(s_class);
            SMSSender p_sender = (SMSSender) p_class.newInstance();
            return p_sender.plRequest(_midlet,_url);
        }
        catch (Throwable e)
        {
            return false;
        }
    }
    //#endif

    public void align(Item _item,int _flags)
    {
    }

    //#if FCMIDP20
    public void fullCanvas(Canvas _canvas,boolean _flag)
    {
    }
    //#endif

    public static void algnItem(Item _item,int _flags)
    {
        if (isMIDP20())
        {
            String s_class = "ru.coldcore.Mpq";
            try
            {
                Class p_class = Class.forName(s_class);
                SMSSender p_sender = (SMSSender) p_class.newInstance();
                p_sender.align(_item,_flags);
            }
            catch (Throwable e)
            {
            }
        }
    }

    //#if FCMIDP20
    public static void setFullCanvas(Canvas _canvas,boolean _flag)
    {
        if (isMIDP20())
        {
            String s_class = "ru.coldcore.Mpq";
            try
            {
                Class p_class = Class.forName(s_class);
                SMSSender p_sender = (SMSSender) p_class.newInstance();
                p_sender.fullCanvas(_canvas,_flag);
            }
            catch (Throwable e)
            {
            }
        }
    }
    //#endif

    public static boolean isMIDP20()
    {
        try
        {
            Class p_class = Class.forName("javax.microedition.lcdui.Spacer");
            boolean lg_flag = p_class.isInterface();
            return lg_flag | true;
        }
        catch(Throwable _thr)
        {
            return false;
        }
    }
}

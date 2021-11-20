package ru.coldcore;

import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.Canvas;

public class Mpq extends SMSSender
{
    public void align(Item _item, int _flags)
    {
        _item.setLayout(_flags);
    }

    //#if FCMIDP20
    public void fullCanvas(Canvas _canvas,boolean _flag)
    {
        //#-
        System.out.println("Set FullCanvas = "+_flag);
        //#+
        _canvas.setFullScreenMode(_flag);
    }
    //#endif

    //#if UPDATEURL
    public boolean plRequest(MIDlet _midlet,String _url)
    {
        try
        {
            _midlet.platformRequest(_url);
            _midlet.notifyDestroyed();
            return true;
        }
        catch(Throwable _thr)
        {
            return false;
        }
    }
    //#endif
}

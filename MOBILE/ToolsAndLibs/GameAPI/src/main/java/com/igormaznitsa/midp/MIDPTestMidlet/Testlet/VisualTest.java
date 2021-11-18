package com.igormaznitsa.midp.MIDPTestMidlet.Testlet;

import com.igormaznitsa.midp.LanguageBlock;

import javax.microedition.lcdui.*;

public abstract class VisualTest extends Testlet implements CommandListener
{
    public Canvas p_canvas;
    public String s_waitPlease;
    public int i_ScreenWidth,i_ScreenHeight;

    public abstract void init (TestletListener _listener, LanguageBlock _languageBlock, int _backStringIndex, int _waitPleaseItem, int _cancelStringIndex);

    public VisualTest()
    {
        super();
    }

    public void setScreenSize(int _width,int _height)
    {
        i_ScreenWidth = _width;
        i_ScreenHeight = _height;
    }

    public int getScreenWidth()
    {
        return p_canvas.getWidth();
    }

    public int getScreenHeight()
    {
        return p_canvas.getHeight();
    }

    protected void init(TestletListener _listener, LanguageBlock _languageBlock, int _backStringIndex, int _waitPleaseItem, int _cancelStringIndex, boolean _interruptable)
    {
        super.init(_listener, _languageBlock, _interruptable);

        s_waitPlease = p_LanguageBlock.getStringArray()[_waitPleaseItem];

        int i_indx = 0;

        if (lg_interruptable)
        {
            Command p_cancelCommand = new Command(p_LanguageBlock.getStringArray()[_cancelStringIndex], Command.CANCEL, i_indx++);
            p_canvas.addCommand(p_cancelCommand);
        }

        Command p_backCommand = new Command(p_LanguageBlock.getStringArray()[_backStringIndex], Command.BACK, i_indx++);
        p_canvas.addCommand(p_backCommand);
        p_canvas.setCommandListener(this);
    }

    public final Displayable getInerface()
    {
        return p_canvas;
    }

    public abstract void Paint(Graphics _graphics);

    public void commandAction(Command command, Displayable displayable)
    {
        switch (command.getCommandType())
        {
            case Command.BACK:
                {
                    if (i_state != STATE_STARTED)
                        if (p_listener != null) p_listener.endTest(this);
                }
                ;
                break;
            case Command.CANCEL:
                {
                    i_state = STATE_CANCELED;
                    if (p_listener != null) p_listener.testCanceled(this);
                }
                ;
                break;
        }
    }
}

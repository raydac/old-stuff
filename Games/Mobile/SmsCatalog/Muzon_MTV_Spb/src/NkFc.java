//#excludeif !FCNOKIA || MIDP=="2.0"
import javax.microedition.lcdui.*;
import java.util.Vector;

public class NkFc extends com.nokia.mid.ui.FullCanvas implements CommandListener
{
    private InsideCanvas p_canvas;
    private Vector p_Commands;
    private boolean lg_ComandsShown;
    private int  i_focusedCommand;
    private CommandListener p_CmndListener;

    public NkFc()
    {
        p_Commands = new Vector();
        lg_ComandsShown = false;
    }

    public void setClockImage(Image _image)
    {
        p_canvas.p_clockImage = _image;
    }

    public boolean equals(Object _obj)
    {
        if (p_canvas==null)
        {
            if (_obj instanceof int[])
            {
                int [] ai_args = (int[])_obj;
                int i_width = getWidth();
                int i_height = getHeight();
                p_canvas = new InsideCanvas(ai_args[0],ai_args[1],null,i_width,i_height);
                return false;
            }
        }
        else
        if (_obj instanceof Image)
        {
            if (p_canvas.p_clockImage==null) p_canvas.p_clockImage = (Image) _obj;
        }
        return super.equals(_obj);
    }

    public void commandAction(Command command, Displayable displayable)
    {
        if (Main.i_ProcessCommand != Main.COMMAND_NONE) return;
        if (Main.p_Command_Exit.equals(command))
        {
            // Выход из игры
            Main.i_ArgScreenID = 0;
            Main.i_ArgCommandID = 1;
            Main.i_ArgSelectedID = 0;
            Main.i_ProcessCommand = Main.COMMAND_COMMAND;
        }
        else if (Main.p_Command_Restart.equals(command))
        {
            // Выход из игры
            Main.i_ArgScreenID = 0;
            Main.i_ArgCommandID = 0;
            Main.i_ArgSelectedID = 0;
            Main.i_ProcessCommand = Main.COMMAND_COMMAND;
        }
    }

    public void addCommand(Command _command)
    {
        p_Commands.addElement(_command);
    }

    public void setCommandListener(CommandListener commandListener)
    {
        p_CmndListener = commandListener;
    }

    public void removeCommand(Command _command)
    {
        p_Commands.removeElement(_command);
    }

    protected void showNotify()
    {
        if (p_canvas!=null) p_canvas.showNotify();
    }

    protected void keyPressed(int i)
    {
        if (lg_ComandsShown)
        {
            if (i==-7)
            {
                lg_ComandsShown = false;
            }
            else
            switch(this.getGameAction(i))
            {
                case Canvas.UP :
                {
                    if (i_focusedCommand==0) i_focusedCommand=p_Commands.size()-1;
                    else
                        i_focusedCommand--;
                };break;
                case Canvas.DOWN :
                {
                    if (i_focusedCommand==p_Commands.size()-1) i_focusedCommand=0;
                    else
                        i_focusedCommand++;
                };break;
                case Canvas.FIRE :
                {
                    if (p_CmndListener!=null)
                    {
                        p_CmndListener.commandAction((Command)p_Commands.elementAt(i_focusedCommand),this);
                        lg_ComandsShown = false;
                    }
                };break;
            }
            repaint();
        }
        else
        {
            if (i==-7)
            {
                if (p_Commands.size()>0)
                {
                    lg_ComandsShown = true;
                    repaint();
                }
            }
            else
                if (p_canvas!=null) p_canvas.keyPressed(i);
        }

    }

    protected void keyReleased(int i)
    {
        if (p_canvas!=null) p_canvas.keyReleased(i);
    }

    private void drawCommands(Graphics _graphic)
    {
        if (p_Commands.size()==0) return;

        Font p_font = Font.getDefaultFont();
        int i_FontHeight = p_font.getHeight();

        int i_size = p_Commands.size();

        int i_width = getWidth();

        final int COLOR_BCKG = 0x0000FF;
        final int COLOR_FRG = 0xFFFFFF;

        // Окно
        _graphic.setColor(COLOR_BCKG);
        _graphic.fillRect(0,0,i_width,i_size*i_FontHeight+2);

        int i_y = 0;
        _graphic.setColor(COLOR_FRG);
        for(int li=0;li<i_size;li++)
        {
            Command p_command = (Command) p_Commands.elementAt(li);
            String s_name = p_command.getLabel();
            s_name = (li+1)+"."+s_name;
            if (li==i_focusedCommand)
            {
                _graphic.setColor(COLOR_FRG);
                _graphic.fillRect(0,i_y,i_width,i_FontHeight);
                _graphic.setColor(COLOR_BCKG);
            }
            else
            {
                _graphic.setColor(COLOR_FRG);
            }
            _graphic.drawString(s_name,0,i_y,Graphics.TOP|Graphics.LEFT);
            i_y += i_FontHeight;
        }
    }

    protected void paint(Graphics _graphics)
    {
        int i_w = getWidth();
        int i_h = getHeight();
        if (p_canvas!=null) p_canvas.paint(_graphics);
        _graphics.setClip(0,0,i_w,i_h);
        // Рисуем треугольник вывод списка команд
        if (p_Commands.size()>0)
        {
            // рисуем над правой софт кнопкой
            Font p_font = Font.getDefaultFont();

            final int COLOR_FILL = 0x00FFFF;
            final int COLOR_OUTLINE = 0x000033;
            final String STR = "<m>";
            int i_x = i_w-p_font.stringWidth(STR)-2;
            int i_y = i_h-p_font.getHeight()-2;
            final int i_fl = Graphics.TOP|Graphics.LEFT;

            _graphics.setColor(COLOR_OUTLINE);
            _graphics.drawString(STR,i_x+1,i_y+1,i_fl);
            _graphics.setColor(COLOR_FILL);
            _graphics.drawString(STR,i_x,i_y,i_fl);
        }
        if (lg_ComandsShown)
        {
            drawCommands(_graphics);
        }
    }
}

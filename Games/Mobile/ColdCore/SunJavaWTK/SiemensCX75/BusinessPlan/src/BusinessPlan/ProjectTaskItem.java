package BusinessPlan;

import BusinessPlan.MenuBlock;

import javax.microedition.lcdui.*;

public class ProjectTaskItem extends CustomItem implements MenuBlock.Focusable
{
    private boolean lg_hasFocus;

    private int i_TextColor;

    private Image p_postImage;
    private String s_text;

    private static final int COLOR_PROGRESS_BACKGROUND = 0xFFFFFF;
    private static final int COLOR_PROGRESS_BORDER = 0x000000;
    private static final int COLOR_PROGRESS_BAR_NORMAL = 0x0000FF;
    private static final int COLOR_PROGRESS_BAR_OVERTIME = 0xFF0000;
    private static final int BAR_WIDTH = 40;
    private static final int ICON_WIDTH = 16;

    private static final int BAR_HEIGHT = ICON_WIDTH-1;
    private static final int BAR_PERC_PRGR = (BAR_WIDTH << 8) / 100;

    public Image generateProgressImage(int _progress, boolean _overtime)
    {
        int i_height = BAR_HEIGHT;
        Image p_newImage = Image.createImage(BAR_WIDTH, i_height);
        Graphics p_gr = p_newImage.getGraphics();
        p_gr.setColor(COLOR_PROGRESS_BACKGROUND);
        p_gr.fillRect(0, 0, BAR_WIDTH, i_height);
        if (_overtime)
        {
            p_gr.setColor(COLOR_PROGRESS_BAR_OVERTIME);
        }
        else
        {
            p_gr.setColor(COLOR_PROGRESS_BAR_NORMAL);
        }
        p_gr.fillRect(0, 0, (BAR_PERC_PRGR * _progress) >> 8, i_height);
        p_gr.setColor(COLOR_PROGRESS_BORDER);
        p_gr.drawRect(0, 0, BAR_WIDTH - 1, i_height - 1);
        p_gr = null;

        return p_newImage;
    }

    public boolean hasFocus()
    {
        return lg_hasFocus;
    }

    public void setFocusState(boolean _state)
    {
        lg_hasFocus = _state;
    }

    private static final int ICON_SIGNAL_RED = 0;
    private static final int ICON_SIGNAL_YELLOW = 1;
    private static final int ICON_SIGNAL_GREEN = 2;
    private static final int ICON_COMPLETED = 3;
    private static final int ICON_PERSON = 4;
    private static final int ICON_TODAY = 5;
    private static final int ICON_WORKTASK = 6;
    private static final int ICON_WARNING = 7;
    private static final int ICON_LINKED = 8;

    private static Image[] ap_icons;

    private int i_firstIconIndex;
    private int i_secondIconIndex;


    public ProjectTaskItem(ProjectRecord _project, int _textColor, Image[] _icons)
    {
        super(null);

        i_firstIconIndex = -1;
        i_secondIconIndex = -1;

        if (ap_icons == null)
        {
            ap_icons = _icons;
        }

        i_TextColor = _textColor;

        s_text = _project.s_ProjectName;

        if (_project.l_person >= 0)
        {
            i_firstIconIndex = ICON_PERSON;
        }
        else
        {
            i_firstIconIndex = -1;
        }

        switch (_project.i_projectPriority)
        {
            case Project.PRIORITY_LOW:
                i_secondIconIndex = ICON_SIGNAL_GREEN;
                break;
            case Project.PRIORITY_NORMAL:
                i_secondIconIndex = ICON_SIGNAL_YELLOW;
                break;
            case Project.PRIORITY_HIGH:
                i_secondIconIndex = ICON_SIGNAL_RED;
                break;
        }

        if (_project.i_rateOfDelivery < 100)
        {
            if (_project.l_endDate > 0)
            {
                boolean lg_today = Utils.isToday(_project.l_endDate);
                if (lg_today)
                {
                    i_secondIconIndex = ICON_TODAY;
                }
                else if (_project.l_endDate < System.currentTimeMillis())
                {
                    i_secondIconIndex = ICON_WARNING;
                }
            }
        }

        if (_project.l_person >= 0)
        {
            i_firstIconIndex = ICON_PERSON;
        }
        else
        {
            i_firstIconIndex = -1;
        }

        if (_project.i_rateOfDelivery < 100)
        {
            if (_project.l_endDate >= 0 && _project.l_endDate < System.currentTimeMillis())
            {
                p_postImage = generateProgressImage(_project.i_rateOfDelivery, true);
            }
            else
            {
                p_postImage = generateProgressImage(_project.i_rateOfDelivery, false);
            }
        }
        else
        {
            p_postImage = ap_icons[ICON_COMPLETED];
        }
    }

    public ProjectTaskItem(Task _task, int _textColor,Image[] _icons)
    {
        super(null);

        if (ap_icons == null)
        {
            ap_icons = _icons;
        }

        i_TextColor = _textColor;

        s_text = _task.getTitle();

        i_firstIconIndex = -1;
        i_secondIconIndex = -1;

        if (_task.getResource()>= 0)
        {
            i_firstIconIndex = ICON_PERSON;
        }
        else
        {
            i_firstIconIndex = -1;
        }

        i_secondIconIndex = ICON_WORKTASK;

        if (_task.getParentTask()>=0)
        {
            i_secondIconIndex = ICON_LINKED;
        }

        if (_task.getProgress()< 100)
        {
            if (_task.getDeadline()> 0)
            {
                boolean lg_today = Utils.isToday(_task.getDeadline());
                if (lg_today)
                {
                    i_secondIconIndex = ICON_TODAY;
                }
                else if (_task.getDeadline()< System.currentTimeMillis())
                {
                    i_secondIconIndex = ICON_WARNING;
                }
            }
        }

        if (_task.getProgress() < 100)
        {
            if (_task.getDeadline() >= 0 && _task.getDeadline() < System.currentTimeMillis())
            {
                p_postImage = generateProgressImage(_task.getProgress(), true);
            }
            else
            {
                p_postImage = generateProgressImage(_task.getProgress(), false);
            }
        }
        else
        {
            p_postImage = null;
        }
    }

    protected int getMinContentWidth()
    {
        return 129;
    }

    protected int getMinContentHeight()
    {
        return ICON_WIDTH;
    }

    protected int getPrefContentWidth(int i)
    {
        return 129;
    }

    protected int getPrefContentHeight(int i)
    {
        return ICON_WIDTH;
    }

    protected void traverseOut()
    {
        lg_hasFocus = false;
        repaint();
    }

    protected boolean traverse(int dir, int i1, int i2, int[] ints)
    {
        if (lg_hasFocus)
        {
            switch (dir)
            {
                case Canvas.UP:
                    {
                        lg_hasFocus = false;
                        return false;
                    }
                case Canvas.DOWN:
                    {
                         lg_hasFocus = false;
                         return false;
                    }
            }
            return true;
        }
        else
        {
            lg_hasFocus = true;
            repaint();
            return true;
        }
    }

    protected void paint(Graphics _g, int w, int h)
    {
        int i_curColor = _g.getColor();
        int i_x = 0;

        if (i_firstIconIndex>=0)
        {
            _g.drawImage(ap_icons[i_firstIconIndex], i_x, 0, 0);
        }
        i_x += ICON_WIDTH;

        if (i_secondIconIndex>=0)
        {
            _g.drawImage(ap_icons[i_secondIconIndex], i_x, 0, 0);
        }
        i_x += ICON_WIDTH;

        i_x += 2;

        _g.setColor(i_TextColor);
        _g.drawString(s_text, i_x, 0, Graphics.TOP | Graphics.LEFT);

        if (p_postImage != null)
        {
            i_x = w - p_postImage.getWidth();
            _g.drawImage(p_postImage, i_x, 0, 0);
        }
        else
        {
            i_x = w - ICON_WIDTH;
            _g.drawImage(ap_icons[ICON_COMPLETED],i_x,0,0);
        }

        _g.setColor(i_curColor);
    }
}

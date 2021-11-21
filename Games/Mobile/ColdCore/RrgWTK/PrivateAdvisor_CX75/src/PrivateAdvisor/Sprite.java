package PrivateAdvisor;

public class Sprite
{
    public static final int OBJECT_GUNSIGHT = 0;
    public static final int OBJECT_GUNSIGHTFIRE = 0;

    public static final int ANIMATION_CYCLIC = 0;

    protected int i_width, i_height;
    protected int i_mainX, i_mainY;
    protected int i_maxFrames;
    protected int i_maxTimeDelay;
    protected int i_offsetX, i_offsetY;

    public boolean lg_SpriteActive;
    public int i_ScreenX, i_ScreenY;
    public int i_Frame;
    public int i_Delay;

    public int i_ObjectType;
    public int i_ObjectState;
    public boolean lg_SpriteInvisible;

    protected int i_spriteID;

    protected int i_linkedSprite;
    protected boolean lg_linkedSprite;

    protected int i_deltaX;
    protected int i_deltaY;

    public Sprite(int _id)
    {
        lg_SpriteActive = false;
        i_spriteID = _id;
        i_linkedSprite = -1;
        lg_linkedSprite = false;
    }

    public void setAnimation(int _frameWidth, int _frameHeight, int _maxFrames, int _initFrame, int _timeDelay)
    {
        i_width = _frameWidth;
        i_height = _frameHeight;
        i_maxFrames = _maxFrames;
        i_maxTimeDelay = _timeDelay;

        i_Delay = 0;
        i_Frame = _initFrame;

        i_offsetX = 0 - (i_width >> 1);

        i_offsetY = 0 - (i_height >> 1);
        setMainPointXY(i_mainX, i_mainY);
    }

    public boolean processAnimation()
    {
        i_Delay++;
        if (i_Delay < i_maxTimeDelay) return false;
        i_Delay = 0;

        i_Frame++;
        if (i_Frame >= i_maxFrames)
        {
            i_Frame = 0;
            return true;
        }
        else
        {
            return false;
        }
    }

    public void setMainPointXY(int _newX, int _newY)
    {
        i_mainX = _newX;
        i_mainY = _newY;
        i_ScreenX = i_mainX + i_offsetX;
        i_ScreenY = i_mainY + i_offsetY;
    }
}

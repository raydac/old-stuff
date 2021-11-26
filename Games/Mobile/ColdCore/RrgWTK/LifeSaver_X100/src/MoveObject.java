
public class MoveObject
{
    public static final int BUTTON_NONE = 0;
    public static final int BUTTON_LEFT = 1;
    public static final int BUTTON_RIGHT = 2;
    public static final int BUTTON_UP = 4;
    public static final int BUTTON_DOWN = 8;
    public static final int BUTTON_FIRE = 16;

    public int i_buttonState;

    public MoveObject()
    {
        i_buttonState = BUTTON_NONE;
    }
}

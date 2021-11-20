
public class ControlObject
{   
    public static final int BUTTON_NONE = 0; 
    public static final int BUTTON_LEFT = 1; 
    public static final int BUTTON_RIGHT = 2; 
    public static final int BUTTON_UP = 4; 
    public static final int BUTTON_DOWN = 8; 
    public static final int BUTTON_FIRE = 16; 
    public static final int BUTTON_SERVICE1 = 32; 
    public static final int BUTTON_SERVICE2 = 64;
    
    public int BUTTON_FLAGS;
    
    public void reset()
    {
        BUTTON_FLAGS = BUTTON_NONE;
    }
}

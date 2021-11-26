
public class Player extends CAbstractPlayer
{
    public int i_buttonState;

    public Player(int _id)
    {
        super(_id);
    }

    public void nextPlayerMove(Object _mobileGamelet, Object _moveObject)
    {
        ((MoveObject)_moveObject).i_buttonState = i_buttonState;
    }

    public int getPlayerFlags()
    {
        return PLAYERFLAG_OBLIGATORY | PLAYERFLAG_SAVEABLE;
    }
}

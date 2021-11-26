
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public abstract class CAbstractPlayer
{
    public int m_iPlayerState;

    public int m_iPlayerGameScores;

    public int m_iPlayerID;

    public static final int PLAYERID_NULL = -1;

    public static final int PLAYERSTATE_UNKNOWN = 0;

    public static final int PLAYERSTATE_INITIALISED = 1;

    public static final int PLAYERSTATE_PLAYING = 2;

    public static final int PLAYERSTATE_INNACTIVE = 3;

    public static final int PLAYERSTATE_USERDEFINED = 9;

    public static final int PLAYERFLAG_NONE = 0;

    public static final int PLAYERFLAG_SAVEABLE = 1;

    public static final int PLAYERFLAG_OBLIGATORY = 2;

    public abstract void nextPlayerMove(Object _mobileGamelet,Object _moveObject);

    public abstract int getPlayerFlags();

    public CAbstractPlayer(int _id)
    {
        m_iPlayerID = _id;
        changePlayerState(PLAYERSTATE_UNKNOWN);
        m_iPlayerGameScores = 0;
    }

    public void changePlayerState(int _playerState)
    {
        m_iPlayerState = _playerState;
    }

    public void releasePlayer()
    {
        changePlayerState(PLAYERSTATE_UNKNOWN);
    }

    protected boolean initPlayerForGameSession(Object _gameWorld)
    {
        changePlayerState(PLAYERSTATE_INITIALISED);
        m_iPlayerGameScores = 0;
        return true;
    }

    protected void localInitPlayer()
    {
    }

    public void setPlayerMoveGameScores(int scores, boolean setScores)
    {
        if (setScores)
        {
            m_iPlayerGameScores = scores;
        }
        else
        {
            m_iPlayerGameScores += scores;
        }
    }

    public void savePlayerData(DataOutputStream _outStream) throws IOException
    {
        if (_outStream == null) throw new IOException();
        _outStream.writeByte(m_iPlayerID);
        _outStream.writeByte(m_iPlayerState);
        _outStream.writeInt(m_iPlayerGameScores);
    }

    public void loadPlayerData(DataInputStream _inputStream) throws IOException
    {
        int iPlayerID = _inputStream.readByte();
        if (iPlayerID == m_iPlayerID)
        {
            int iGameScores;
            int iPlayerState;

            iPlayerState = _inputStream.readByte();
            iGameScores = _inputStream.readInt();

            m_iPlayerGameScores = iGameScores;
            changePlayerState(iPlayerState);
        }
        else
            throw new IOException();
    }

    public int getMaximumSizeOfPlayerDataBlock()
    {
        int iSize = 0;
        iSize += 4; 
        iSize += 1; 
        iSize += 1; 

        return iSize;
    }

}

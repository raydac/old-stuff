
import java.io.*;
import java.util.Random;


public abstract class MobileGamelet extends Random
{

    public static int sqr(int _x)
    {
        int bx = _x;
        int ax = 1,di = 2,cx = 0,dx = 0;
        while (cx <= bx)
        {
            cx += ax;
            ax += di;
            dx++;
        }
        return dx - 1;
    }

    public final static int[] ai_sineTable = new int[]{0, 25, 50, 74, 98, 121, 142, 162, 181, 198, 213, 226, 237, 245, 251, 255, 256, 255, 251, 245, 237, 226, 213, 198, 181, 162, 142, 121, 98, 74, 50, 25, 0, -25, -50, -74, -98, -121, -142, -162, -181, -198, -213, -226, -237, -245, -251, -255, -256, -255, -251, -245, -237, -226, -213, -198, -181, -162, -142, -121, -98, -74, -50, -25};

    public int convertRadiansTo64BasedAngle(int _radians)
    {
        return (_radians / 6536);
    }

    public static int xSine(int x, int index)
    {
        return (x * ai_sineTable[index]) >> 8;
    }

    public static int xCoSine(int _x, int _index)
    {
        _index += 16;
        return (_x * ai_sineTable[_index & 63]) >> 8;
    }

    public static int xSineFloat(int _x, int _index)
    {
        return (_x * ai_sineTable[_index]);
    }

    public static int xCoSineFloat(int _x, int _index)
    {
        _index += 16;
        return (_x * ai_sineTable[_index & 63]);
    }


    public int getRandomInt(int _limit)
    {
        _limit++;
        _limit = (int) (((long) Math.abs(nextInt()) * (long) _limit) >>> 31);
        return _limit;
    }

    public int m_iGameAreaWidth;

    public int m_iGameAreaHeight;

    public int m_iPreviousGameState;

    public int m_iGameStage;

    public int m_iGameDifficultLevel;

    public int m_iGameState;


public startup m_pAbstractGameActionListener;

    protected CAbstractPlayer[] m_pWinningList;

    protected CAbstractPlayer[] m_pPlayerList;

    protected void setGameState(int iNewState)
    {
        m_iPreviousGameState = m_iGameState;
        m_iGameState = iNewState;
    }

    public static final int GAMEWORLDSTATE_UNKNOWN = 0;

    public static final int GAMEWORLDSTATE_INITED = 1;

    public static final int GAMEWORLDSTATE_PLAYED = 2;

    public static final int GAMEWORLDSTATE_PLAYERLOST = 3;

    public static final int GAMEWORLDSTATE_GAMEOVER = 4;

    public static final int GAMEWORLDSTATE_PAUSED = 5;

    public static final int GAMEWORLDSTATE_USERDEFINED = 10;

    public static final int GAMELEVEL_EASY = 0;
    public static final int GAMELEVEL_NORMAL = 1;
    public static final int GAMELEVEL_HARD = 2;
    public static final int GAMELEVEL_USERDEFINED = 10;

    public boolean globalInitWorld()
    {
        setGameState(GAMEWORLDSTATE_INITED);
        return true;
    }

    public void globalReleaseWorld()
    {
        setGameState(GAMEWORLDSTATE_UNKNOWN);
    }


public boolean newGameSession(int _gameAreaWidth, int _gameAreaHeight, int _gameLevel, CAbstractPlayer [] _players, startup _listener,String _staticArrayResourceName)
    {
        if (m_iGameState != GAMEWORLDSTATE_INITED)
        {
            return false;
        }

        if (_players == null) return false;

        m_iGameAreaWidth = _gameAreaWidth;
        m_iGameAreaHeight = _gameAreaHeight;

        m_iGameDifficultLevel = _gameLevel;
        m_pAbstractGameActionListener = _listener;
        m_pPlayerList = _players;
        m_pWinningList = null;

        if (!initPlayersForGameSession()) return false;

        if(!initState()) return false;

        setGameState(GAMEWORLDSTATE_PLAYED);


        return true;
    }


    public void endGameSession()
    {
        setGameState(GAMEWORLDSTATE_INITED);

        deinitState();

        if (m_pPlayerList != null) releasePlayers();
        m_pWinningList = null;
    }

    public void pauseGame()
    {
        if (m_iGameState == GAMEWORLDSTATE_PLAYED)
        {
            setGameState(GAMEWORLDSTATE_PAUSED);
        }
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        if (m_iGameState == GAMEWORLDSTATE_PAUSED)
        {
            setGameState(GAMEWORLDSTATE_PLAYED);
        }
        else if (m_iGameState == GAMEWORLDSTATE_PLAYERLOST)
        {
            setGameState(GAMEWORLDSTATE_PLAYED);
            localInitPlayers();
        }
    }

    public int getGameWorldState()
    {
        return m_iGameState;
    }

    public int getPreviousGameWorldState()
    {
        return m_iPreviousGameState;
    }

    public boolean newGameStage(int _stage)
    {
        if (m_iGameState != GAMEWORLDSTATE_GAMEOVER && m_iGameState != GAMEWORLDSTATE_PLAYED) return false;

        m_pWinningList = null;

        setGameState(GAMEWORLDSTATE_PLAYED);
        m_iGameStage = _stage;

        if (m_pPlayerList == null) return false;

        if (m_iGameState == GAMEWORLDSTATE_GAMEOVER) initPlayersForGameSession();

        return true;
    }

    public int getGameStage()
    {
        return m_iGameStage;
    }

    public int getGameDifficultLevel()
    {
        return m_iGameDifficultLevel;
    }

    public void saveGameData(DataOutputStream _outputStream) throws IOException
    {
        if (m_iGameState != GAMEWORLDSTATE_PLAYED) throw new IOException();

        _outputStream.writeInt(m_iGameAreaWidth);
        _outputStream.writeInt(m_iGameAreaHeight);
        _outputStream.writeByte(m_iGameDifficultLevel);
        _outputStream.writeByte(m_iPreviousGameState);
        _outputStream.writeByte(m_iGameStage);

        for (int li = 0; li < m_pPlayerList.length; li++)
        {
            CAbstractPlayer pCurrentPlayer = m_pPlayerList[li];
            if ((pCurrentPlayer.getPlayerFlags() & CAbstractPlayer.PLAYERFLAG_SAVEABLE) != 0)
            {
                _outputStream.writeByte(pCurrentPlayer.m_iPlayerID);
                _outputStream.writeByte(pCurrentPlayer.getPlayerFlags());
                _outputStream.writeShort(pCurrentPlayer.getMaximumSizeOfPlayerDataBlock());

                pCurrentPlayer.savePlayerData(_outputStream);
            }
        }

        _outputStream.writeByte(CAbstractPlayer.PLAYERID_NULL);

        _saveGameData(_outputStream);
    }

public void loadGameData(DataInputStream _inputStream, CAbstractPlayer [] _players, startup _listener) throws IOException
    {
        if (_inputStream == null) throw new IOException();

        if (m_iGameState == GAMEWORLDSTATE_INITED)
        {
            int iGameAreaWidth;
            int iGameAreaHeight;
            int iGameDifficultLevel;
            int iPreviousGameState;
            int iGameStage;

            iGameAreaWidth = _inputStream.readInt();
            iGameAreaHeight = _inputStream.readInt();
            iGameDifficultLevel = _inputStream.readByte();
            iPreviousGameState = _inputStream.readByte();
            iGameStage = _inputStream.readByte();

            if (!newGameSession(iGameAreaWidth, iGameAreaHeight, iGameDifficultLevel, _players, _listener,null)) throw new IOException("I can't initialize new game session");

            if (!newGameStage(iGameStage))
            {
                endGameSession();
                throw new IOException();
            }

            m_iPreviousGameState = iPreviousGameState;

            while (true)
            {
                int iCurrentPlayerID;
                iCurrentPlayerID = _inputStream.readByte();

                if (iCurrentPlayerID == CAbstractPlayer.PLAYERID_NULL) break;

                int iPlayerFlags;
                int iDataLength;
                iPlayerFlags = _inputStream.readByte();
                iDataLength = _inputStream.readShort();

                CAbstractPlayer pCurrentPlayer = getPlayerForPlayerID(iCurrentPlayerID);

                if (pCurrentPlayer == null)
                {
                    if ((iPlayerFlags & CAbstractPlayer.PLAYERFLAG_OBLIGATORY) == 0)
                    {
                        _inputStream.skip(iDataLength);
                        continue;
                    }
                    else
                    {
                        throw new IOException();
                    }
                }

                pCurrentPlayer.loadPlayerData(_inputStream);
            }

            try
            {
                _loadGameData(_inputStream);
            }
            catch (IOException e)
            {
                endGameSession();

                e.printStackTrace();

                throw new IOException();
            }
        }
        else
        {
            throw new IOException();
        }
    }

    public int getMaximumDataSize()
    {
        int iSummaryLength = 9;
        iSummaryLength += 1;
        iSummaryLength += 1;

        if (m_pPlayerList != null)
        {
            for (int i = 0; i < m_pPlayerList.length; i++)
            {
                if ((m_pPlayerList[i].getPlayerFlags() & CAbstractPlayer.PLAYERFLAG_SAVEABLE) != 0)
                {
                    iSummaryLength += 4; 
                    iSummaryLength += m_pPlayerList[i].getMaximumSizeOfPlayerDataBlock();
                }
            }
            iSummaryLength += 1; 
        }
          else
            iSummaryLength += 100;  

        iSummaryLength += _getMaximumDataSize();

        return iSummaryLength;
    }

    public CAbstractPlayer[] getWinningList()
    {
        return m_pWinningList;
    }

    protected CAbstractPlayer getPlayerForPlayerID(int _playerID)
    {
        for(int li=0;li<m_pPlayerList.length;li++) if (m_pPlayerList[li].m_iPlayerID == _playerID) return m_pPlayerList[li];
        return null;
    }

    protected boolean initPlayersForGameSession()
    {
        for(int li=0;li<m_pPlayerList.length;li++)
        {
            if (!m_pPlayerList[li].initPlayerForGameSession(this)) return false;
        }
        return true;
    }

    protected void localInitPlayers()
    {
        for(int li=0;li<m_pPlayerList.length;li++) m_pPlayerList[li].localInitPlayer();
    }

    protected void releasePlayers()
    {
        for(int li=0;li<m_pPlayerList.length;li++) m_pPlayerList[li].releasePlayer();
    }

    public abstract String getGameTextID();

    public abstract int _getMaximumDataSize();

    public abstract boolean initState();

    public abstract void deinitState();

    public abstract void _saveGameData(DataOutputStream _outputStream) throws IOException;

    public abstract void _loadGameData(DataInputStream _inputStream) throws IOException;

    public abstract int nextGameStep();

    public abstract int getGameStepDelay();
}

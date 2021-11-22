package com.igormaznitsa.GameKit_FE652.Seabattle;

import com.igormaznitsa.GameAPI.StrategicBlock;
import com.igormaznitsa.GameAPI.GameStateRecord;
import com.igormaznitsa.GameAPI.PlayerBlock;

import java.io.*;

public class Seabattle_SB implements StrategicBlock
{
    public static final int GAMEFIELD_WIDTH = 10;
    public static final int GAMEFIELD_HEIGHT = 10;

    protected Seabattle_GSR _game_state = null;
    protected PlayerBlock _player_block = null;

    public void newGame(int level)
    {
        _game_state = new Seabattle_GSR();
        _game_state.getAILogic().autoPlacingOurShips();
        if(_player_block!=null) _player_block.initPlayer();
    }

    public void saveGameState(OutputStream outputStream) throws IOException
    {
        System.gc();
        DataOutputStream dos = new DataOutputStream(outputStream);

        // Saving player's data
        SBLogic _logic = _game_state.getPlayerLogic();

        for(int li=0;li<_logic._ourGameField.length;li++)
        {
            dos.writeByte(_logic._ourGameField[li]);
        }

        for(int li=0;li<_logic._opponentGameField.length;li++)
        {
            dos.writeByte(_logic._opponentGameField[li]);
        }

        dos.writeShort(_logic._current_hits);
        dos.writeByte(_logic._1_ship);
        dos.writeByte(_logic._2_ship);
        dos.writeByte(_logic._3_ship);
        dos.writeByte(_logic._4_ship);

        // Saving opponent's data
        _logic = _game_state.getAILogic();

        for(int li=0;li<_logic._ourGameField.length;li++)
        {
            dos.writeByte(_logic._ourGameField[li]);
        }

        for(int li=0;li<_logic._opponentGameField.length;li++)
        {
            dos.writeByte(_logic._opponentGameField[li]);
        }

        dos.writeShort(_logic._current_hits);
        dos.writeByte(_logic._1_ship);
        dos.writeByte(_logic._2_ship);
        dos.writeByte(_logic._3_ship);
        dos.writeByte(_logic._4_ship);


        dos.writeBoolean(_game_state._ai_is_moving);

        dos.writeByte(_game_state._last_opponent_x);
        dos.writeByte(_game_state._last_opponent_y);


        dos = null;
        System.gc();
    }

    public void loadGameState(InputStream inputStream) throws IOException
    {
        System.gc();
        DataInputStream dos = new DataInputStream(inputStream);

        _game_state = null;
        _game_state = new Seabattle_GSR();

        System.gc();

        // Loading player's data
        SBLogic _logic = _game_state.getPlayerLogic();

        _logic.clearOpponentField();
        _logic.clearOurField();

        for(int li=0;li<_logic._ourGameField.length;li++)
        {
            _logic._ourGameField[li] = dos.readByte();
        }

        for(int li=0;li<_logic._opponentGameField.length;li++)
        {
            _logic._opponentGameField[li] = dos.readByte();
        }

        _logic._current_hits =  dos.readShort();
        _logic._1_ship = dos.readByte();
        _logic._2_ship = dos.readByte();
        _logic._3_ship = dos.readByte();
        _logic._4_ship = dos.readByte();

        // Loading opponent's data
        _logic = _game_state.getAILogic();

        _logic.clearOpponentField();
        _logic.clearOurField();

        for(int li=0;li<_logic._ourGameField.length;li++)
        {
            _logic._ourGameField[li] = dos.readByte();
        }

        for(int li=0;li<_logic._opponentGameField.length;li++)
        {
            _logic._opponentGameField[li] = dos.readByte();
        }

        _logic._current_hits =  dos.readShort();
        _logic._1_ship = dos.readByte();
        _logic._2_ship = dos.readByte();
        _logic._3_ship = dos.readByte();
        _logic._4_ship = dos.readByte();


        _game_state._ai_is_moving = dos.readBoolean();

        _game_state._last_opponent_x = dos.readByte();
        _game_state._last_opponent_y = dos.readByte();

        dos = null;
        System.gc();

        if (_player_block!=null) _player_block.initPlayer();
    }

    public void nextGameStep()
    {
        if (_player_block==null) return;

        SBLogic _ai = _game_state.getAILogic();
        SBLogic _player = _game_state.getPlayerLogic();
        byte _result = 0;

        if (_game_state.isPlayerMoving())
        {
            // Player's move
            Seabattle_PMR _move = (Seabattle_PMR) _player_block.getPlayerMoveRecord(_game_state);
            _result = _ai.getShotResult(_move._x,_move._y);
            _player.setShotResult(_move._x,_move._y,_result);

            if (_result==SBLogic.BS_MOVE_MISS) _game_state._ai_is_moving = true;

        }
        else
        {
            // Ai's move
            Seabattle_PMR _move = _game_state.getAILogic().getStrikeCoord();
            _game_state._last_opponent_x = _move.getX();
            _game_state._last_opponent_y = _move.getY();
            _result = _player.getShotResult(_move._x,_move._y);
            _ai.setShotResult(_move._x,_move._y, _result);
            if (_result==SBLogic.BS_MOVE_MISS) _game_state._ai_is_moving = false;
        }

        if (_player.getHitNumber() == SBLogic.SUMMARY_SHIPS_FIELDS)
        {
            _game_state._player_state = Seabattle_GSR.PLAYERSTATE_LOST;
            _game_state._ai_state = Seabattle_GSR.PLAYERSTATE_WON;
            _game_state._game_state = Seabattle_GSR.GAMESTATE_OVER;
        }
        else
        if (_ai.getHitNumber() == SBLogic.SUMMARY_SHIPS_FIELDS)
        {
            _game_state._player_state = Seabattle_GSR.PLAYERSTATE_WON;
            _game_state._ai_state = Seabattle_GSR.PLAYERSTATE_LOST;
            _game_state._game_state = Seabattle_GSR.GAMESTATE_OVER;
        }


    }

    public GameStateRecord getGameStateRecord()
    {
        return _game_state;
    }

    public void setPlayerBlock(PlayerBlock playerBlock)
    {
        _player_block = playerBlock;
    }

    public void setAIBlock(PlayerBlock aiBlock)
    {
    }

    public boolean isLoadSaveSupporting()
    {
        return true;
    }
}

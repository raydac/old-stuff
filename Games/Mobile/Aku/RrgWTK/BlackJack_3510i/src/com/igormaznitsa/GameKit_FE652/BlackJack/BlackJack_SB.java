package com.igormaznitsa.GameKit_FE652.BlackJack;

import com.igormaznitsa.GameAPI.StrategicBlock;
import com.igormaznitsa.GameAPI.GameStateRecord;
import com.igormaznitsa.GameAPI.PlayerBlock;
import com.igormaznitsa.GameAPI.Utils.RndGenerator;

import java.io.*;

public class BlackJack_SB implements StrategicBlock
{
    public static final int START_PLAYER_MONEYS = 100;

    public static final RndGenerator _rnd = new RndGenerator(System.currentTimeMillis());

    protected BlackJack_GSR _game_state = null;
    protected BlackJackListener _listener = null;

    public void initNewGame(int bet)
    {
        _game_state.initNewGame(bet);
    }

    public void exceptGame()
    {
        if (_game_state._player_state != BlackJack_GSR.PLAYER_NORMAL) return ;
        _game_state._player_money += _game_state._player_bet;
        _game_state._player_money += _game_state._insurance;
    }

    public boolean canBeSurrended()
    {
        if (_game_state._player_state != BlackJack_GSR.PLAYER_NORMAL) return false;
        if (_game_state._player_cards_counter > 2) return false; else return true;
    }

    public void setBJListener(BlackJackListener lstnr)
    {
        _listener = lstnr;
    }

    public void surrender()
    {
        if (_listener.requestSurrender() == BlackJackListener.YES)
        {
            _game_state._player_money += _game_state._player_bet >> 1;
            _game_state._game_state = BlackJack_GSR.GAMESTATE_OVER;
        }
    }

    public BlackJack_SB()
    {
    }

    public void newGame(int level)
    {
        _game_state = new BlackJack_GSR();
//        _game_state.initNewGame(20);
    }

    public void saveGameState(OutputStream outputStream) throws IOException
    {
        System.gc();

        DataOutputStream dos = new DataOutputStream(outputStream);
        dos.writeInt(_game_state._player_money);

        dos = null;
        System.gc();
    }

    public void loadGameState(InputStream inputStream) throws IOException
    {
        System.gc();
        DataInputStream dos = new DataInputStream(inputStream);

        _game_state = null;
        System.gc();

        newGame(0);

        _game_state = new BlackJack_GSR();
        _game_state._player_money = dos.readInt();

        dos = null;
        System.gc();
    }

    public void nextGameStep()
    {
        if (_game_state._dispensation_of_cards)
        {
            if (_game_state._player_cards_counter < 2)
            {
                _game_state.getNextCardForPlayer(true);
            }
            else
            {
                if (_game_state._dealer_cards_counter < 2)
                {
                    if (_game_state._dealer_cards_counter == 1)
                    {
                        _game_state.getNextCardForDealer(false);
                    }
                    else
                    {
                        _game_state.getNextCardForDealer(true);
                    }
                }
                else
                {
                    _game_state._dispensation_of_cards = false;
                }
            }
        }
        else if (_game_state._player_move)
        {
            if (_game_state._double_request_was)
            {
                _game_state._player_move = false;
                return;
            }

            // Processing of the player move
            if (_game_state._player_scores == 21)
            {
                if ((_game_state._dealer_cards[0] & 0x0F) == Deck.CARD_A)
                {
                    if (_listener.requestPayment11() == BlackJackListener.YES)
                    {
                        _game_state._player_money += (_game_state._player_bet << 1);
                        _game_state._player_money += _game_state._insurance;
                    }
                    else
                    {
                        _game_state._player_move = false;
                        return;
                    }
                }
            }

            if (_game_state._player_scores > 21)
            {
                _game_state._player_state = BlackJack_GSR.PLAYER_LOST_BUST;
                _game_state._game_state = BlackJack_GSR.GAMESTATE_OVER;
                return;
            }

            if ((_game_state._player_cards_counter == 2))
            {
                if ((_game_state._dealer_cards[0] & 0x0F) == Deck.CARD_A)
                {
                    if (!_game_state._insurance_was && (_game_state._player_money >= (_game_state._player_bet>>1)))
                    {
                        if (_listener.requestInsurance() == BlackJackListener.YES)
                        {
                            _game_state._insurance = _game_state.getMoney(_game_state._player_bet >> 1);
                            _game_state._insurance_was = true;
                        }
                    }
                    else
                    {
                        _game_state._insurance_was = true;
                    }
                }
                else
                {
                    if (!_game_state._double_request_was && (_game_state._player_money >= _game_state._player_bet) && _game_state._player_scores>=9 && _game_state._player_scores<=11)
                    {
                        if (_listener.requestDouble() == BlackJackListener.YES)
                        {
                            _game_state._player_bet += _game_state.getMoney(_game_state._player_bet);
                            _game_state.getNextCardForPlayer(true);
                        }
                        _game_state._double_request_was = true;
                        return;
                    }
                    else
                    if (_game_state._double_request_was)
                    {
                        _game_state._player_move = false;
                        return;
                    }
                }
            }
            else
            {
                if (_game_state._player_scores > 21)
                {
                    _game_state._dealer_state = BlackJack_GSR.PLAYER_LOST_BUST;
                    _game_state._player_state = BlackJack_GSR.PLAYER_WON;
                    _game_state._game_state = BlackJack_GSR.GAMESTATE_OVER;
                    return;
                }
            }

            if (_listener.doYouMove())
            {
                _game_state.getNextCardForPlayer(true);
            }
            else
            {
                _game_state._player_move = false;
            }
        }
        else
        {
            // Check visibility of second card
            if ((_game_state._dealer_cards[1] & Deck.VISIBLE_FLAG) == 0)
            {
                _game_state._dealer_cards[1] |= Deck.VISIBLE_FLAG;
            }
            else
            // Processing of the dealer move
                if (_game_state._dealer_scores == 21)
                {
                    if (_game_state._player_scores == 21)
                    {
                        _game_state._player_money += (_game_state._player_bet + _game_state._insurance);
                        _game_state._dealer_state = BlackJack_GSR.PLAYER_WON;
                        _game_state._player_state = BlackJack_GSR.PLAYER_WON;
                    }
                    else
                    {
                        if (_game_state._insurance != 0)
                        {
                            _game_state._player_money += (_game_state._insurance << 1);
                        }
                        _game_state._dealer_state = BlackJack_GSR.PLAYER_WON;
                        _game_state._player_state = BlackJack_GSR.PLAYER_LOST_LESS;
                    }

                    _game_state._game_state = BlackJack_GSR.GAMESTATE_OVER;
                }
                else
                {
                    if (_game_state._dealer_scores > 21)
                    {
                        _game_state._dealer_state = BlackJack_GSR.PLAYER_LOST_BUST;
                        _game_state._player_state = BlackJack_GSR.PLAYER_WON;
                        _game_state._game_state = BlackJack_GSR.GAMESTATE_OVER;
                        if (_game_state._player_scores == 21)
                        {
                            _game_state._player_money += (_game_state._player_bet << 1) + (_game_state._player_bet >> 1);
                        }
                        else
                        {
                            _game_state._player_money += (_game_state._player_bet << 1);
                        }
                        _game_state._player_money += _game_state._insurance;
                    }
                    else if (_game_state._dealer_scores < 17)
                    {
                        _game_state.getNextCardForDealer(true);
                    }
                    else
                    {
                        if (_game_state._player_scores > _game_state._dealer_scores)
                        {
                            if (_game_state._player_scores == 21)
                            {
                                _game_state._player_money += ((_game_state._player_bet << 1) + (_game_state._player_bet >> 1));
                            }
                            else
                            {
                                _game_state._player_money += (_game_state._player_bet << 1);
                            }
                            _game_state._player_money += _game_state._insurance;
                            _game_state._player_state = BlackJack_GSR.PLAYER_WON;
                            _game_state._dealer_state = BlackJack_GSR.PLAYER_LOST_LESS;
                        }
                        else if (_game_state._player_scores == _game_state._dealer_scores)
                        {
                            _game_state._player_money += _game_state._player_bet + _game_state._insurance;
                            _game_state._player_state = BlackJack_GSR.PLAYER_WON;
                            _game_state._dealer_state = BlackJack_GSR.PLAYER_WON;
                        }
                        else
                        {
                            _game_state._player_state = BlackJack_GSR.PLAYER_LOST_LESS;
                            _game_state._dealer_state = BlackJack_GSR.PLAYER_WON;
                        }
                        _game_state._game_state = BlackJack_GSR.GAMESTATE_OVER;
                    }
                }
        }
    }

    public GameStateRecord getGameStateRecord()
    {
        return _game_state;
    }

    public void setPlayerBlock(PlayerBlock playerBlock)
    {
    }

    public void setAIBlock(PlayerBlock aiBlock)
    {
    }

    public boolean isLoadSaveSupporting()
    {
        return true;
    }
}

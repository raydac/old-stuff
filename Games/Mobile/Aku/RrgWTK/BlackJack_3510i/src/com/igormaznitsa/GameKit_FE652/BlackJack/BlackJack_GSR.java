package com.igormaznitsa.GameKit_FE652.BlackJack;

import com.igormaznitsa.GameAPI.GameStateRecord;

public class BlackJack_GSR implements GameStateRecord
{
    public static final int GAMESTATE_PLAYED = 0;
    public static final int GAMESTATE_OVER = 1;

    public static final int PLAYER_NORMAL = 0;
    public static final int PLAYER_LOST_BUST = 1;
    public static final int PLAYER_LOST_LESS = 2;
    public static final int PLAYER_WON = 3;

    public static final int MAX_HANDLED_CARDS = 8;

    protected int _game_state = 0;
    protected int _player_state = 0;
    protected int _player_money = 0;
    protected int _player_scores = 0;
    protected int _dealer_state = 0;
    protected int _dealer_scores = 0;
    protected int _insurance = 0;

    protected int[] _player_cards = null;
    protected int[] _dealer_cards = null;

    protected int _player_cards_counter = 0;
    protected int _dealer_cards_counter = 0;
    protected int _player_bet = 0;

    protected int _player_noprocess_aces_counter = 0;
    protected int _dealer_noprocess_aces_counter = 0;

    protected boolean _double_request_was = false;
    protected boolean _insurance_was = false;
    protected Deck _deck = null;
    protected boolean _player_move = true;
    protected int _cur_card = 0;
    protected boolean _dispensation_of_cards = true;

    public int getNoprocessAces()
    {
        return _player_noprocess_aces_counter;
    }

    protected int getCardScore(int card)
    {
        switch (card & 0x0f)
        {
            case Deck.CARD_2:
                return 2;
            case Deck.CARD_3:
                return 3;
            case Deck.CARD_4:
                return 4;
            case Deck.CARD_5:
                return 5;
            case Deck.CARD_6:
                return 6;
            case Deck.CARD_7:
                return 7;
            case Deck.CARD_8:
                return 8;
            case Deck.CARD_9:
                return 9;
            case Deck.CARD_10:
            case Deck.CARD_J:
            case Deck.CARD_Q:
            case Deck.CARD_K:
                return 10;
            case Deck.CARD_A:
                return 11;
            default : return -1;
        }
    }

    protected void getNextCardForPlayer(boolean visible)
    {
        int nextcard = _deck.getCard(_cur_card++);

        if (visible)
        {
            nextcard |= Deck.VISIBLE_FLAG;
        }

        if ((nextcard & 0x0F) == Deck.CARD_A)
        {
            _player_noprocess_aces_counter++;
        }

        int cardscore = getCardScore(nextcard);
        int cursc = _player_scores + cardscore;

        _player_cards [_player_cards_counter++] = nextcard;

        if (cursc > 21)
        {
            while (_player_noprocess_aces_counter != 0)
            {
                cursc -= 10;
                _player_noprocess_aces_counter--;
                if (cursc <= 21) break;
            }
        }

        _player_scores = cursc;
    }

    protected void getNextCardForDealer(boolean visible)
    {
        int nextcard = _deck.getCard(_cur_card++);
        if (visible)
        {
            nextcard |= Deck.VISIBLE_FLAG;
        }

        if ((nextcard & 0x0F) == Deck.CARD_A)
        {
            _dealer_noprocess_aces_counter++;
        }

        int cardscore = getCardScore(nextcard);
        int cursc = _dealer_scores + cardscore;

        _dealer_cards [_dealer_cards_counter++] = nextcard;

        if (cursc > 21)
        {
            while (_dealer_noprocess_aces_counter != 0)
            {
                cursc -= 10;
                _dealer_noprocess_aces_counter--;
                if (cursc <= 21) break;
            }
        }

        _dealer_scores = cursc;
    }

    public void initNewGame(int bet)
    {
        _game_state = GAMESTATE_PLAYED;
        _player_state = PLAYER_NORMAL;
        _dealer_state = PLAYER_NORMAL;
        _deck.initDeck();
        _cur_card = 0;
        _player_cards_counter = 0;
        _dealer_cards_counter = 0;
        _insurance = 0;
        _player_bet = getMoney(bet);
        _player_noprocess_aces_counter = 0;
        _dealer_noprocess_aces_counter = 0;
        _player_scores = 0;
        _dealer_scores = 0;
        _player_move = true;
        _double_request_was = false;
        _insurance_was = false;
        _dispensation_of_cards = true;
    }

    public boolean isPlayerMove()
    {
        return _player_move;
    }

    public int getBet()
    {
        return _player_bet;
    }

    public int [] getPlayerCardArray()
    {
        return _player_cards;
    }

    public int getPlayerCardCounter()
    {
        return _player_cards_counter;
    }

    public int getDealerCardCounter()
    {
        return _dealer_cards_counter;
    }

    public int [] getDealerCardArray()
    {
        return _dealer_cards;
    }

    protected int getMoney(int val)
    {
        if (val > _player_money) val = _player_money;
        _player_money -= val;
        return val;
    }

    public BlackJack_GSR()
    {
        _deck = new Deck();
        _player_cards = new int[MAX_HANDLED_CARDS];
        _dealer_cards = new int[MAX_HANDLED_CARDS];
        _player_money = BlackJack_SB.START_PLAYER_MONEYS;
    }

    public int getInsurance()
    {
        return _insurance;
    }

    public int getPlayerMoney()
    {
        return _player_money;
    }

    public int getGameState()
    {
        return _game_state;
    }

    public int getPlayerScores()
    {
        return _player_scores;
    }

    public int getAIScores()
    {
        return _dealer_scores;
    }

    public int getPlayerState()
    {
        return _player_state;
    }

    public int getAIState()
    {
        return _dealer_state;
    }

    public int getLevel()
    {
        return 0;
    }

    public int getStage()
    {
        return 0;
    }
}

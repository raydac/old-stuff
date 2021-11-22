package com.igormaznitsa.GameKit_FE652.BlackJack;

public class Deck
{
    public static final int CARD_2 = 0x00;
    public static final int CARD_3 = 0x01;
    public static final int CARD_4 = 0x02;
    public static final int CARD_5 = 0x03;
    public static final int CARD_6 = 0x04;
    public static final int CARD_7 = 0x05;
    public static final int CARD_8 = 0x06;
    public static final int CARD_9 = 0x07;
    public static final int CARD_10 =0x08;
    public static final int CARD_J = 0x09;
    public static final int CARD_Q = 0x0A;
    public static final int CARD_K = 0x0B;
    public static final int CARD_A = 0x0C;

    public static final int SUIT_SPADES = 0x00;
    public static final int SUIT_CLUBS = 0x10;
    public static final int SUIT_DIAMONDS = 0x20;
    public static final int SUIT_HEARTS = 0x30;

    public static final int VISIBLE_FLAG = 0x100;

    public static final int TOTAL_CARD_NUMBER = 52;

    protected int [] cardarray = null;

    public void setCard(int num,int card)
    {
        cardarray [num] = card;
    }

    public int getCard(int num)
    {
        return cardarray[num];
    }

    public void initDeck()
    {
        for(int li=0;li<TOTAL_CARD_NUMBER;li++)
        {
            if (li==0)
            {
                int suit = BlackJack_SB._rnd.getInt(3)<<4;
                int card = BlackJack_SB._rnd.getInt(CARD_A);
                cardarray[0] = suit | card;
            }
            else
            {
                int prevcard = cardarray[li-1];

                int suit  = 0,card = 0;
                while (true)
                {
                    suit = BlackJack_SB._rnd.getInt(3)<<4;
                    card = BlackJack_SB._rnd.getInt(CARD_A);

                    if ((suit == (prevcard>>4))||(card == (prevcard & 0x0F))) continue; else break;
                }
                cardarray[li] = suit | card;
            }
        }
    }

    public Deck()
    {
        cardarray = new int [TOTAL_CARD_NUMBER];
    }

}

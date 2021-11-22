package com.igormaznitsa.GameKit_FE652.BlackJack;

public interface BlackJackListener
{
    public static final int YES = 0;
    public static final int NO = 1;

    // Запрос на страхование суммы от блэк джека диллера, если первая карта диллера туз
    public int requestInsurance();
    // Запрос на удвоение ставки и получение дополнительной карты, только если на руках две карты
    public int requestDouble();
    // Запрос на выплату 1:1 если у игрока 21 а у диллера первая карта туз
    public int requestPayment11();
    // Запрос на капитуляцию игрока, возвращается половина суммы
    public int requestSurrender();

    // Запрос на ожидание хода игрока, true если ходит и false если отказывается
    public boolean doYouMove();


}

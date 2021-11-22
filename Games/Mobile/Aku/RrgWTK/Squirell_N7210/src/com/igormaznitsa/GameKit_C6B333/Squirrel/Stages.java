package com.igormaznitsa.GameKit_C6B333.Squirrel;

import com.igormaznitsa.GameKit_C6B333.Squirrel.GameletImpl;

public class Stages {
    // Пусто
    public static final int OBJECT_NONE = 0;
    // Ветка
    public static final int OBJECT_BRANCH = 1;
    // Ствол
    public static final int OBJECT_TRUNK = 2;
    // Сова
    public static final int OBJECT_OWL = 3;
    // Гусеница
    public static final int OBJECT_CATERPILLAR = 4;
    // Жук
    public static final int OBJECT_BEETLE = 5;
    // Дровосек
    public static final int OBJECT_LOGGER = 6;
    // Пчела
    public static final int OBJECT_BEE = 7;
    // Орех
    public static final int OBJECT_NUT = 8;
    // Игрок
    public static final int OBJECT_PLAYER = 9;
    // Охотник
    public static final int OBJECT_HUNTER = 0xB;
    // Почва
    public static final int OBJECT_EARTH = 0xA;


    // Количество уровней
    public static final int TOTAL_STAGES = 16;


    public static final int EMPTY = 0;

    public static final byte TREE_BRANCH             = (byte) 0x01;
    public static final byte TREE_BRANCH1            = (byte) 0x11;
    public static final byte TREE_BRANCH2            = (byte) 0x21;
    public static final byte TREE_BRANCH_END_LEFT    = (byte) 0x31;
    public static final byte TREE_BRANCH_END_RIGHT   = (byte) 0x41;
    public static final byte TREE_BRANCH_TURN_DOWN   = (byte) 0x51;
    public static final byte TREE_BRANCH_TURN_UP     = (byte) 0x61;


    public static final int SMALL_TREE_TOP             = (byte) 0x12;
    public static final byte SMALL_TREE_LOG            = (byte) 0x02;
    public static final byte SMALL_TREE_LOG_LEFT       = (byte) 0x22;
    public static final byte SMALL_TREE_LOG_BOTH       = (byte) 0x32;
    public static final byte SMALL_TREE_LOG_RIGHT      = (byte) 0x42;
    public static final byte SMALL_TREE_ROOT           = (byte) 0x52;

    public static final byte BIG_TREE_TOP_LEFT         = (byte) 0x62;
    public static final byte BIG_TREE_TOP_RIGHT        = (byte) 0x72;
    public static final byte BIG_TREE_LOG_LEFT         = (byte) 0x82;
    public static final byte BIG_TREE_LOG_BRANCH_LEFT  = (byte) 0x92;
    public static final byte BIG_TREE_LOG_BRANCH_RIGHT = (byte) 0xA2;
    public static final byte BIG_TREE_LOG_RIGHT        = (byte) 0xB2;
    public static final byte BIG_TREE_ROOT_LEFT        = (byte) 0xC2;
    public static final byte BIG_TREE_ROOT_RIGHT       = (byte) 0xD2;



}

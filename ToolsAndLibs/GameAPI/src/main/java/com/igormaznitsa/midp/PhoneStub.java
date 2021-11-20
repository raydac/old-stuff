package com.igormaznitsa.midp;

import com.igormaznitsa.midp.Melody.Melody;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.io.DataInputStream;
import java.io.IOException;

/*
 * Copyright (C) 2002 Igor A. Maznitsa (igor.maznitsa@igormaznitsa.com).  All rights reserved.
 * Software is confidential and copyrighted. Title to Software and all associated intellectual property rights
 * is retained by Igor A. Maznitsa. You may not make copies of Software, other than a single copy of Software for
 * archival purposes.  Unless enforcement is prohibited by applicable law, you may not modify, decompile, or reverse
 * engineer Software.  You acknowledge that Software is not designed, licensed or intended for use in the design,
 * construction, operation or maintenance of any nuclear facility. Igor A. Maznitsa disclaims any express or implied
 * warranty of fitness for such uses.
*/
public interface PhoneStub
{
    /**
     * Check enabling of screen output
     */
    public boolean isShowEnabled();

    /**
     * Enable coping of the double buffer to the screen
     */
    public void showNotify();

    /**
     * Disable coping of the double buffer to the screen
     */
    public void hideNotify();

    /**
     * Loading of an image from an input stream
     */
    public Image getImageFromInputStream(DataInputStream _inputstream) throws IOException;

    /**
     * Initing of the double buffer for the phone
     * @param _width
     * @param _height
     */
    public void initDoubleBuffer(int _width,int _height) throws IllegalArgumentException;

    /**
     * Get graphics object for the double buffer
     */
    public Graphics getGraphicsForDoubleBuffer();

    /**
     * Paint the double buffer on the screen
     * @param _x coordinate X of left top corner
     * @param _y coordinate Y of left top corner
     */
    public void paintDoubleBuffer(int _x,int _y);

    /**
     *  Activates and deactivates the lights on the phone
     * @param state
     */
    public void setScreenLight(boolean state);

    /**
     * Activates vibration for a given length of time and frequency
     * @param duration
     */
    public void activateVibrator(long duration);

    /**
     * Play a tone
     * @param freq the frequency of this tone
     * @param tone_time the duration of this tone
     * @param blocking blocking of the tread unntil playing is not stopped
     */
    public void playTone(int freq,int tone_time,boolean blocking);

    /**
     * Converting of content of the melody to native phone format
     * @param _melody Pointer to a melody
     * @return true if operation is ok else false
     */
    public boolean convertMelody(Melody _melody);

    /**
     * Play melody on the phone
     * @param _melody Pointer to a melody object which have to play
     * @param _blocking Flag of blocking of the playing
     */
    public void playMelody(Melody _melody,boolean _blocking);

    /**
     * Stop playing of a melody on the phone
     * @param _melody Pointer to a melody object which have to play
     */
    public void stopMelody(Melody _melody);

    /**
     * Stop playing of all melodies on the phone
     */
    public void stopAllMelodies();

    /**
     * Release all resources links with loading of images and sounds
     */
    public void releaseAuxiliaryModules();

    /**
     * Code of menu key
     */
    public static final int KEY_MENU = 0;

    /**
     * Code of right key
     */
    public static final int KEY_RIGHT = 0;

    /**
     * Code of left key
     */
    public static final int KEY_LEFT = 0;

    /**
     * Code of up key
     */
    public static final int KEY_UP = 0;

    /**
     * Code of down key
     */
    public static final int KEY_DOWN = 0;

    /**
     * Code of fire key
     */
    public static final int KEY_FIRE = 0;

    /**
     * Code of cancel key
     */
    public static final int KEY_CANCEL = 0;

    /**
     * Code of accept key
     */
    public static final int KEY_ACCEPT = 0;

    /**
     * Code of back key
     */
    public static final int KEY_BACK = 0;
}

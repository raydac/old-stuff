package com.igormaznitsa.midp;

import com.igormaznitsa.midp.Melody.Melody;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.io.DataInputStream;
import java.io.IOException;

public interface PhoneStub
{
    /**
     * Flag of light supporting
     */
    public static final int SUPPORT_LIGHT = 1;

    /**
     * Flag of sound supporting
     */
    public static final int SUPPORT_SOUND = 2;

    /**
     * Flag of vibrate supporting
     */
    public static final int SUPPORT_VIBRATION = 4;

    /**
     * Return the set of flags of posibilities which are supported by the phone model
     * @return set of flags as int value
     */
    public int getPhoneSupportFlags();

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
     * Changing background image
     * @param _background
     * @throws IllegalArgumentException
     */
    public void setBackgroundImage(Image _background) throws IllegalArgumentException;

    /**
     * Initing of the double buffer for the phone
     * @param _width
     * @param _height
     * @param _background
     */
    public void initDoubleBuffer(int _width,int _height,Image _background) throws IllegalArgumentException;

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
     * Sleep function
     */
    public void sleep(int duration) throws InterruptedException;

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

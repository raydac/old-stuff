package com.igormaznitsa.midp.MIDPTestMidlet.Testlet;

import com.igormaznitsa.midp.LanguageBlock;

import javax.microedition.lcdui.Displayable;

public abstract class Testlet
{
    public static final int STATE_INITED = 0;
    public static final int STATE_STARTED  = 1;
    public static final int STATE_CANCELED = 2;
    public static final int STATE_COMPLETED = 3;

    public int i_state;
    public TestletListener p_listener;
    public boolean lg_interruptable;
    public LanguageBlock p_LanguageBlock;

    /**
     * Return the interface block for the interface
     * @return a Displayable object
     */
    public abstract Displayable getInerface();

    public Testlet(){}

   /**
    * Initing the class
    * @param _listener the listener what listens to testlet events
    * @param _languageBlock the language block for the testlet
    */
    protected void init(TestletListener _listener,LanguageBlock _languageBlock,boolean _interruptable)
    {
        lg_interruptable = _interruptable;
        p_listener = _listener;
        p_LanguageBlock = _languageBlock;
        i_state = STATE_INITED;
    }

    public boolean isInterruptable()
    {
        return lg_interruptable;
    }

    /**
     * Return the state of the testlet
     * @return the state as an int value
     */
    public int getState()
    {
        return i_state;
    }

    /**
     * Start the test
     * @return true if the test is completed else false
     */
    public abstract boolean startTest();

    /**
     * Return the test results as an object
     * @return the test results as an object
     */
    public abstract Object getResult();

    /**
     * Return the string reference of the test
     */
    public abstract String getTestName();

    /**
     * The command is called when the test are completed
     */
    public void Completed()
    {
        i_state = STATE_COMPLETED;
        if (p_listener!=null)
            p_listener.testCompleted(this);
    }
}

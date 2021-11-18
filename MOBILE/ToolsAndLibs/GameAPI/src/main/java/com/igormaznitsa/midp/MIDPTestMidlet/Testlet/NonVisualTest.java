package com.igormaznitsa.midp.MIDPTestMidlet.Testlet;

import com.igormaznitsa.midp.LanguageBlock;

import javax.microedition.lcdui.*;

public abstract class NonVisualTest extends Testlet implements CommandListener
{
    private Form p_Form;
    private int i_userStartIndex;
    private String s_waitPlease;
    private StringItem p_StringItem;

    public abstract void init (TestletListener _listener, LanguageBlock _languageBlock, int _backStringIndex, int _waitPleaseItem, int _cancelStringIndex);

    /**
     * The initing the kind of testlets
     * @param _listener the listener whay listens to testlet events
     * @param _languageBlock the language block
     * @param _backStringIndex the index of the string "BACK" in the language block
     * @param _waitPleaseItem the index of the string "WAIT PLEASE" in the language block
     * @param _cancelStringIndex the index of the string "CANCEL" in the language block
     */
    protected void init(TestletListener _listener, LanguageBlock _languageBlock, int _backStringIndex, int _waitPleaseItem, int _cancelStringIndex, boolean _interruptable)
    {
        super.init(_listener, _languageBlock, _interruptable);
        p_Form = new Form(getTestName());

        s_waitPlease = p_LanguageBlock.getStringArray()[_waitPleaseItem];
        p_StringItem = new StringItem(null, "");
        p_Form.append(p_StringItem);

        i_userStartIndex = 0;
        if (lg_interruptable)
        {
            Command p_cancelCommand = new Command(p_LanguageBlock.getStringArray()[_cancelStringIndex], Command.CANCEL, i_userStartIndex++);
            p_Form.addCommand(p_cancelCommand);
        }

        Command p_backCommand = new Command(p_LanguageBlock.getStringArray()[_backStringIndex], Command.BACK, i_userStartIndex++);
        p_Form.addCommand(p_backCommand);

        int i_indx = i_userStartIndex;
        while (true)
        {
            String s_customCommand = getCustomCommandItem(i_indx);
            if (s_customCommand == null) break;
            Command p_customCommand = new Command(s_customCommand, Command.ITEM, i_indx++);
            p_Form.addCommand(p_customCommand);
        }

        p_Form.setCommandListener(this);
    }

    public final Displayable getInerface()
    {
        return p_Form;
    }

    public boolean startTest()
    {
        i_state = STATE_STARTED;

        p_StringItem.setText(s_waitPlease);

        return false;
    }

    public void Completed()
    {
        String s_Result = (String) getResult();
        p_StringItem.setText(s_Result);
        super.Completed();
    }

    public final void commandAction(Command command, Displayable displayable)
    {
        switch (command.getCommandType())
        {
            case Command.BACK:
                {
                    if (i_state != STATE_STARTED)
                        if (p_listener != null) p_listener.endTest(this);
                }
                ;
                break;
            case Command.CANCEL:
                {
                    i_state = STATE_CANCELED;
                    if (p_listener != null) p_listener.testCanceled(this);
                }
                ;
                break;
            case Command.ITEM:
                {
                    processUserCommand(command.getPriority() - i_userStartIndex);
                }
                ;
                break;
        }
    }

    /**
     * Get the string representation of a command for the command ID
     * @param _commandId the command ID
     * @return the string command representation as a string object
     */
    public abstract String getCustomCommandItem(int _commandId);

    /**
     * Process a custom command
     * @param _commandId the command id of the processed command
     */
    public void processUserCommand(int _commandId)
    {
    }
}

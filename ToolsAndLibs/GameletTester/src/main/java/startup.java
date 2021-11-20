import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class startup extends JFrame implements ActionListener, Runnable
{
    public static int SCREEN_WIDTH;

    public static int SCREEN_HEIGHT;

    public static int I8_COEFF_WIDTH;

    public static int I8_COEFF_HEIGHT;

    public static class VideoMode
    {
        public String s_ID;

        public int i_Width;

        public int i_Height;

        public int i_CoeffW;

        public int i_CoeffH;

        public VideoMode(String _modeName, int _width, int _height, int _coeffW, int _coeffH)
        {
            s_ID = _modeName;
            i_Width = _width;
            i_Height = _height;
            i_CoeffW = _coeffW;
            i_CoeffH = _coeffH;
        }

        public final String toString()
        {
            return s_ID;
        }
    }

    public static final VideoMode[] ap_VideoModes = new VideoMode[] 
                                                                  { 
        new VideoMode("Main game resolution " + Gamelet.MAINRESOLUTION_WIDTH + 'x' + Gamelet.MAINRESOLUTION_HEIGHT, Gamelet.MAINRESOLUTION_WIDTH, Gamelet.MAINRESOLUTION_HEIGHT, 0x100, 0x100), 
        new VideoMode("176x188", 176, 188, 0x100, 0x100), 
        new VideoMode("128x128", 128, 128, 0x100, 0x100),
        new VideoMode("101x80", 101, 80, 0x100, 0x100),
        new VideoMode("OCAP 640x480", 640, 480, 0x100, 0x100)
        };

    private TesterPanel p_TesterPanel;

    private JButton p_ButtonRestartGame;

    private JButton p_ButtonRestartStage;

    private JButton p_ButtonSave;

    private JButton p_ButtonLoad;

    private JComboBox p_ModeList;

    private JTextField p_GameIdField;

    private JTextField p_GameStageField;

    private JLabel p_MessageLabel;

    private static final ControlObject p_ControlObject = new ControlObject();

    private byte[] ab_SaveGameDataArray;

    private static final int GAMECOMMAND_NONE = 0;

    private static final int GAMECOMMAND_EXIT = 1;

    private static final int GAMECOMMAND_RESTARTSTAGE = 2;

    private static final int GAMECOMMAND_SAVEGAME = 3;

    private static final int GAMECOMMAND_LOADGAME = 4;

    private static final int GAMECOMMAND_REINITGAME = 5;

    private int i_GameCommand = GAMECOMMAND_NONE;

    public void actionPerformed(ActionEvent e)
    {
        Object p_src = e.getSource();

        if (p_src.equals(p_ButtonLoad))
        {
            i_GameCommand = GAMECOMMAND_LOADGAME;
        }
        else if (p_src.equals(p_ButtonSave))
        {
            i_GameCommand = GAMECOMMAND_SAVEGAME;
        }
        else if (p_src.equals(p_ButtonRestartGame))
        {
            i_GameCommand = GAMECOMMAND_REINITGAME;
        }
        else if (p_src.equals(p_ButtonRestartStage))
        {
            i_GameCommand = GAMECOMMAND_RESTARTSTAGE;
        }
        else if (p_src.equals(p_ModeList))
        {
            VideoMode p_mode = (VideoMode) p_ModeList.getSelectedItem();
            p_TesterPanel.changeVideoMode(p_mode);

            // выставляем коэффициенты
            SCREEN_WIDTH = p_mode.i_Width;
            SCREEN_HEIGHT = p_mode.i_Height;
            I8_COEFF_WIDTH = (SCREEN_WIDTH <<8)/Gamelet.MAINRESOLUTION_WIDTH;
            I8_COEFF_HEIGHT = (SCREEN_HEIGHT <<8)/Gamelet.MAINRESOLUTION_HEIGHT;
            
            pack();
            i_GameCommand = GAMECOMMAND_REINITGAME;
        }
    }

    public static final void keyPressed(KeyEvent _key)
    {
        //System.out.println("kode "+_key);
        switch(_key.getKeyCode())
        {
            case KeyEvent.VK_LEFT:
            {
                p_ControlObject.BUTTON_FLAGS |= ControlObject.BUTTON_LEFT;
            };break;
            case KeyEvent.VK_RIGHT:
            {
                p_ControlObject.BUTTON_FLAGS |= ControlObject.BUTTON_RIGHT;
            };break;
            case KeyEvent.VK_UP:
            {
                p_ControlObject.BUTTON_FLAGS |= ControlObject.BUTTON_UP;
            };break;
            case KeyEvent.VK_DOWN:
            {
                p_ControlObject.BUTTON_FLAGS |= ControlObject.BUTTON_DOWN;
            };break;
            case KeyEvent.VK_SPACE:
            {
                p_ControlObject.BUTTON_FLAGS |= ControlObject.BUTTON_FIRE;
            };break;
            case KeyEvent.VK_1:
            {
                p_ControlObject.BUTTON_FLAGS |= ControlObject.BUTTON_SERVICE1;
            };break;
            case KeyEvent.VK_2:
            {
                p_ControlObject.BUTTON_FLAGS |= ControlObject.BUTTON_SERVICE2;
            };break;
        }
    }

    public static final void keyReleased(KeyEvent _key)
    {
        switch(_key.getKeyCode())
        {
            case KeyEvent.VK_LEFT:
            {
                p_ControlObject.BUTTON_FLAGS &= ~ControlObject.BUTTON_LEFT;
            };break;
            case KeyEvent.VK_RIGHT:
            {
                p_ControlObject.BUTTON_FLAGS &= ~ControlObject.BUTTON_RIGHT;
            };break;
            case KeyEvent.VK_UP:
            {
                p_ControlObject.BUTTON_FLAGS &= ~ControlObject.BUTTON_UP;
            };break;
            case KeyEvent.VK_DOWN:
            {
                p_ControlObject.BUTTON_FLAGS &= ~ControlObject.BUTTON_DOWN;
            };break;
            case KeyEvent.VK_SPACE:
            {
                p_ControlObject.BUTTON_FLAGS &= ~ControlObject.BUTTON_FIRE;
            };break;
            case KeyEvent.VK_1:
            {
                p_ControlObject.BUTTON_FLAGS &= ~ControlObject.BUTTON_SERVICE1;
            };break;
            case KeyEvent.VK_2:
            {
                p_ControlObject.BUTTON_FLAGS &= ~ControlObject.BUTTON_SERVICE2;
            };break;
        }
    }

    public static final void keyTyped(KeyEvent arg0)
    {
        
    }
    
    public void println(String _str)
    {
        p_MessageLabel.setText(_str);
    }

    public int getGameID() throws Throwable
    {
        String s_id = p_GameIdField.getText().trim();
        if (s_id.length() == 0) throw new Throwable("You must enter game id");
        int i_gameid = 0;

        try
        {
            i_gameid = Integer.parseInt(s_id);
        }
        catch (Throwable _thr)
        {
            throw new Throwable("You must use a number as the game id");
        }

        return i_gameid;
    }

    public int getGameStageID() throws Throwable
    {
        String s_id = p_GameStageField.getText().trim();
        if (s_id.length() == 0) throw new Throwable("You must enter stage id");
        int i_gameid = 0;

        try
        {
            i_gameid = Integer.parseInt(s_id);
        }
        catch (Throwable _thr)
        {
            throw new Throwable("You must use a number as the stage id");
        }

        return i_gameid;
    }

    public void run()
    {
        try
        {
            boolean lg_reinitGame = true;
            while (lg_reinitGame)
            {
                lg_reinitGame = false;

                println("Init gamelet");
                Gamelet.init(this.getClass());

                Thread.sleep(100);

                int i_id = getGameID();

                println("Init session [" + i_id + "]");
                Gamelet.initSession(this.getClass(), i_id);
                Thread.sleep(100);

                int i_gameStageID = getGameStageID();

                while (true)
                {
                    println("Init stage [" + i_gameStageID + "]");
                    Gamelet.initStage(this.getClass(), i_gameStageID);
                    Thread.sleep(100);
                    
                    p_ControlObject.reset();
                    
                    int i_gameState;

                    println("Process game iterations");

                    i_GameCommand = GAMECOMMAND_NONE;
                    while (true)
                    {
                        i_gameState = Gamelet.processIteration(p_ControlObject);
                        if (i_gameState != Gamelet.GAMESTATE_PLAYED) break;

                        p_TesterPanel.updateGameScreen();
                        getContentPane().repaint(0L);
                        java.awt.Toolkit.getDefaultToolkit().sync();
                        
                        if (i_GameCommand == GAMECOMMAND_NONE)
                        {
                            Thread.sleep(100);
                        }
                        else
                        {
                            if (i_GameCommand == GAMECOMMAND_LOADGAME)
                            {
                                println("Load game state");
                                ByteArrayInputStream p_inStream = new ByteArrayInputStream(ab_SaveGameDataArray);
                                DataInputStream p_dsStream = new DataInputStream(p_inStream); 
                                Gamelet.loadState(this.getClass(),p_dsStream);
                                println("Game state loaded successfully");
                            }
                            else
                            if (i_GameCommand == GAMECOMMAND_SAVEGAME)
                            {
                                ByteArrayOutputStream p_baos = new ByteArrayOutputStream(Gamelet.getDataBlockSzie());
                                DataOutputStream p_dos = new DataOutputStream(p_baos);
                                Gamelet.saveState(p_dos);
                                p_dos.flush();
                                p_dos.close();
                                ab_SaveGameDataArray = p_baos.toByteArray();
                                p_baos = null;
                                p_dos = null;
                                if (ab_SaveGameDataArray.length != Gamelet.getDataBlockSzie())
                                {
                                    Utilities.showErrorDialog(this, "SAVE GAME ERROR", "You have mismatch size of saved block and calculated size (" + ab_SaveGameDataArray.length + "<>" + Gamelet.getDataBlockSzie());
                                }
                                else
                                {
                                    Utilities.showInfoDialog(this, "GAME SAVED", "You have saved block length:" + ab_SaveGameDataArray.length);
                                    p_ButtonLoad.setEnabled(true);
                                }
                            }
                            else if (i_GameCommand == GAMECOMMAND_EXIT || i_GameCommand == GAMECOMMAND_REINITGAME)
                            {
                                break;
                            }
                            else if (i_GameCommand == GAMECOMMAND_RESTARTSTAGE)
                            {
                                println("Restart current stage");
                                Gamelet.restartCurrentStage();
                                println("Current stage has been restarted");
                            }

                            i_GameCommand = GAMECOMMAND_NONE;
                        }
                    }

                    if (i_GameCommand == GAMECOMMAND_EXIT)
                    {
                        println("Exit game");
                        return;
                    }

                    if (i_GameCommand == GAMECOMMAND_REINITGAME)
                    {
                        println("Reinit game");
                        lg_reinitGame = true;
                        break;
                    }
                    
                    println("Dispose stage");
                    Gamelet.disposeStage();

                    if (i_gameState == Gamelet.GAMESTATE_PLAYERLOST)
                    {
                        if ((Gamelet.GAME_FLAGS & Gamelet.FLAG_STAGESCONTINUEAFTERLOST) == 0) break;
                    }

                    if ((Gamelet.GAME_FLAGS & Gamelet.FLAG_MANYSTAGES) == 0 || i_gameStageID == Gamelet.LASTSTAGE_ID) break;

                    i_gameStageID++;
                    if (!Utilities.askDialog(this, "Play next stage?", "To play the game stage ID: " + i_gameStageID + " ?")) break;
                }

                println("Dispose session");
                Gamelet.disposeSession();
                Thread.sleep(1000);

                println("Release gamelet");
                Gamelet.release();
                Thread.sleep(1000);
            }
        }
        catch (Throwable _thr)
        {
            String s_str = _thr.getMessage();
            if (s_str == null) s_str = _thr.getClass().toString();
            println(p_MessageLabel.getText() + "[" + s_str + "]");
            _thr.printStackTrace();
        }
    }

    public startup()
    {
        super(Gamelet.GAME_ID);
        BorderLayout p_layout = new BorderLayout(10, 10);
        getContentPane().setLayout(p_layout);
        getContentPane().setBackground(Color.BLACK);

        VideoMode p_mode = ap_VideoModes[0];
        
        p_TesterPanel = new TesterPanel(p_mode);

        SCREEN_WIDTH = p_mode.i_Width;
        SCREEN_HEIGHT = p_mode.i_Height;
        I8_COEFF_WIDTH = (SCREEN_WIDTH <<8)/Gamelet.MAINRESOLUTION_WIDTH;
        I8_COEFF_HEIGHT = (SCREEN_HEIGHT <<8)/Gamelet.MAINRESOLUTION_HEIGHT;

        add(p_TesterPanel, BorderLayout.CENTER);

        JPanel p_toppanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel p_downpanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        p_GameIdField = new JTextField("0", 4);
        p_GameStageField = new JTextField("0", 4);

        p_toppanel.add(new JLabel("GameID"));
        p_toppanel.add(p_GameIdField);

        p_toppanel.add(new JLabel("StageID"));
        p_toppanel.add(p_GameStageField);

        p_ButtonRestartGame = new JButton("Restart game");
        p_ButtonRestartGame.setActionCommand("RESTARTGAME");
        p_ButtonRestartGame.addActionListener(this);
        p_toppanel.add(p_ButtonRestartGame);

        p_ButtonRestartStage = new JButton("Restart stage");
        p_ButtonRestartStage.setActionCommand("RESTARTSTAGE");
        p_ButtonRestartStage.addActionListener(this);

        if ((Gamelet.FLAG_STAGECANBERESTARTED & Gamelet.GAME_FLAGS) == 0) p_ButtonRestartStage.setEnabled(false);

        p_toppanel.add(p_ButtonRestartStage);

        p_ButtonSave = new JButton("Save");
        p_ButtonSave.setActionCommand("SAVE");
        p_ButtonSave.addActionListener(this);

        if ((Gamelet.GAME_FLAGS & Gamelet.FLAG_CANBESAVED) == 0) p_ButtonSave.setEnabled(false);

        p_toppanel.add(p_ButtonSave);

        p_ButtonLoad = new JButton("Load");
        p_ButtonLoad.setActionCommand("LOAD");
        p_ButtonLoad.addActionListener(this);
        p_ButtonLoad.setEnabled(false);
        p_toppanel.add(p_ButtonLoad);

        p_ModeList = new JComboBox(ap_VideoModes);
        p_ModeList.addActionListener(this);
        p_toppanel.add(p_ModeList);

        p_toppanel.setBackground(Color.gray.brighter());
        p_downpanel.setBackground(Color.GRAY.brighter());
        add(p_toppanel, BorderLayout.NORTH);

        p_MessageLabel = new JLabel("    ");
        p_downpanel.add(p_MessageLabel);
        add(p_downpanel, BorderLayout.SOUTH);

        p_ModeList.setSelectedIndex(0);

        pack();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setResizable(false);

        Utilities.toScreenCenter(this);

        p_TesterPanel.requestFocus();
        
        setVisible(true);

    }

    public static final void main(String[] _args)
    {
        new Thread(new startup()).start();
    }
}

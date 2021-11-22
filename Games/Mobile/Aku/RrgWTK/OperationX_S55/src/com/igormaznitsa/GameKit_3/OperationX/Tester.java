/*
package com.igormaznitsa.GameKit_3.OperationX;

import com.igormaznitsa.gameapi.Gamelet;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Tester extends JFrame implements KeyListener,MouseListener,Runnable
{
    static final int MODEL_M50 = 0;
    static final int MODEL_3410 = 1;
    static final int MODEL_7210 = 2;
    static final int MODEL_7650 = 3;

//=====================================
    GameletImpl p_gamelet = null;
    PlayerMoveObject p_pmr = null;
//=====================================

    private class displayClass extends Canvas
    {
        Image p_db = null;
        int i_width,i_height;

        public displayClass(int _width,int _height)
        {
            super();
            p_db = new BufferedImage(_width,_height,BufferedImage.TYPE_4BYTE_ABGR);
            setSize(_width,_height);
            i_width = _width;
            i_height = _height;
        }

//        public Dimension getMinimumSize()
//        {
//            return new Dimension(p_db.getWidth(null),p_db.getHeight(null));
//        }

        public void paint(Graphics _gr)
        {
            if (p_db==null)
            {
                super.paint(_gr);
            }
            else
            {
                _gr.drawImage(p_db,0,0,null);
            }
        }
    }

    private displayClass p_display;

    public void initPlayer()
    {
        p_pmr.i_Button = PlayerMoveObject.BUTTON_NONE;
    }

    public Tester(int _type)
    {
        super();

        int i_model = _type;
        int i_width,i_height;
        switch(i_model)
        {
            case MODEL_3410 :{i_width = 128;i_height=128;};break;
            case MODEL_7210 :{i_width = 128;i_height=128;};break;
            case MODEL_7650 :{i_width = 128;i_height=128;};break;
            default :{i_width = 128;i_height=128;};break;
        }

        p_display = new displayClass(i_width,i_height);

        getContentPane().setLayout(new BorderLayout(0,0));
        getContentPane().add(p_display,BorderLayout.CENTER);

        this.pack();


        p_display.addKeyListener(this);
        p_display.addMouseListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        //==============
        p_gamelet = new GameletImpl(i_width, i_height, null);
        p_pmr = new PlayerMoveObject();
        new Thread(this).start();
        setTitle(p_gamelet.getGameID());
        setIgnoreRepaint(true);
    }

    public static final void main(String [] _args)
    {
        Tester p_tstr = new Tester(MODEL_7650);
        p_tstr.show();
    }

//=================================================
    public void keyTyped(KeyEvent e)
    {
    }

    public void keyPressed(KeyEvent e)
    {
        int k = e.getKeyCode();
        switch (k)
        {
            case KeyEvent.VK_1:
                {
                    int j = 0;
                    for(int li=0;li<p_gamelet.ap_AnimationObjects.length;li++) if (p_gamelet.ap_AnimationObjects[li].lg_Active) j++;

                    //System.out.print("Active objects = "+j);
                }
                ;
                break;
            case KeyEvent.VK_ENTER:
                {
                    p_pmr.i_Button = PlayerMoveObject.BUTTON_RECHARGE;
                }
                ;
                break;
            case KeyEvent.VK_SPACE:
                {
                    p_pmr.i_Button = PlayerMoveObject.BUTTON_FIRE;
                }
                ;
                break;
            case KeyEvent.VK_LEFT:
                {
                    p_pmr.i_Button = PlayerMoveObject.BUTTON_LEFT;
                }
                ;
                break;
            case KeyEvent.VK_RIGHT:
                {
                    p_pmr.i_Button = PlayerMoveObject.BUTTON_RIGHT;
                }
                ;
                break;
            case KeyEvent.VK_DOWN:
                {
                    p_pmr.i_Button = PlayerMoveObject.BUTTON_DOWN;
                }
                ;
                break;
            case KeyEvent.VK_UP:
                {
                    p_pmr.i_Button = PlayerMoveObject.BUTTON_UP;
                }
                ;
                break;
        }
    }

    public void keyReleased(KeyEvent e)
    {
       p_pmr.i_Button = PlayerMoveObject.BUTTON_NONE;
    }

    public void mouseClicked(MouseEvent e)
    {
    }

    public void mousePressed(MouseEvent e)
    {
    }

    public void mouseReleased(MouseEvent e)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }
//=================================================

    public void run()
    {
        p_gamelet.newGameSession(0);
        p_gamelet.initStage(0);

        while (p_gamelet.i_GameState != Gamelet.GAMESTATE_OVER)
        {
            p_gamelet.nextGameStep(p_pmr);
            updateDBuffer();
            try
            {
                Thread.sleep(p_gamelet.i_TimeDelay);
            }
            catch (InterruptedException e)
            {
                return;
            }

            if (p_gamelet.i_PlayerState == Gamelet.PLAYERSTATE_LOST && p_gamelet.i_GameState != Gamelet.GAMESTATE_OVER)
            {
                try
                {
                    Thread.sleep(2000);
                }
                catch (InterruptedException e)
                {
                    return;
                }

                p_gamelet.resumeGameAfterPlayerLost();
            }
        }

        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(1024);
        try
        {
            p_gamelet.writeToStream(new DataOutputStream(p_baos));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("Saved data length = " + p_baos.toByteArray().length);
        System.out.println("GAME OVER Scores :" + p_gamelet.getPlayerScore());
    }

    public void updateDBuffer()
     {
         Graphics g = p_display.p_db.getGraphics();

         g.setColor(Color.white);
         g.fillRect(0, 0, p_gamelet.i_ScreenWidth, p_gamelet.i_ScreenHeight);

         g.setColor(Color.green);
         g.fillRect(0, GameletImpl.I8_GROUND_Y_OFFSET >> 8, p_display.i_width, p_display.i_height);

         // Drawing enemy objects
         for (int li = 0; li < p_gamelet.ap_AnimationObjects.length; li++)
         {
             AnimeObject p_obj = p_gamelet.ap_AnimationObjects[li];
             if (p_obj.lg_Active)
             {
                 if (p_obj.lg_LinkedObject) continue;

                 while (true)
                 {
                     switch (p_obj.i_Type)
                     {
                         case AnimeObject.OBJECT_TROOPER0:
                         case AnimeObject.OBJECT_TROOPER1:
                         case AnimeObject.OBJECT_TROOPER2:
                             {
                                 g.setColor(Color.magenta);
                             }
                             ;
                             break;
                         case AnimeObject.OBJECT_HELYCOPTERHORZ:
                             {
                                 g.setColor(Color.darkGray);
                             }
                             ;
                             break;
                         case AnimeObject.OBJECT_PILOTGUNNER:
                             {
                                 g.setColor(Color.yellow);
                             }
                             ;
                             break;
                         case AnimeObject.OBJECT_TANK:
                             {
                                 g.setColor(Color.black);
                             }
                             ;
                             break;
                         case AnimeObject.OBJECT_HELYCOPTERVERT:
                             {
                                 g.setColor(Color.blue);
                             }
                             ;
                             break;
                         case AnimeObject.OBJECT_EXPLOSION:
                             {
                                 g.setColor(Color.pink);
                             }
                             ;
                             break;
                     }

                     int i_x = (p_obj.i8_ScreenX - p_gamelet.i8_startViewX) >> 8;
                     int i_y = p_obj.i8_ScreenY >> 8;

                     g.fillRect(i_x, i_y, p_obj.i_Width, p_obj.i_Height);

                     g.setColor(Color.PINK);

                     for (int lf = 0; lf < 2; lf++)
                     {
                         AnimeObject p_flush = p_obj.ap_shotFireArray[lf];

                         if (p_flush != null && p_flush.lg_Active)
                         {

                             i_x = (p_flush.i8_ScreenX - p_gamelet.i8_startViewX) >> 8;
                             i_y = p_flush.i8_ScreenY >> 8;

                             g.fillRect(i_x, i_y, p_flush.i_Width, p_flush.i_Height);
                         }
                     }

                     if (p_obj.p_linkObject != null)
                     {
                         p_obj = p_obj.p_linkObject;
                     }
                     else
                         break;
                 }
             }
         }

         // Drawing player's flashes
         g.setColor(Color.red);
         for (int li = 0; li < p_gamelet.ap_PlayersFlashes.length; li++)
         {
             AnimeObject p_obj = p_gamelet.ap_PlayersFlashes[li];
             if (!p_obj.lg_Active) continue;
             int i_x = (p_obj.i8_ScreenX - p_gamelet.i8_startViewX) >> 8;
             int i_y = p_obj.i8_ScreenY >> 8;
             g.fillRect(i_x, i_y, p_obj.i_Width, p_obj.i_Height);
         }

         // Drawing the sight

         if (p_gamelet.p_PlayerGun.i_state != PlayerGun.STATE_CHARGING)
         {
             g.setColor(Color.red);
             g.drawRect(p_gamelet.p_PlayerGun.getSightX(), p_gamelet.p_PlayerGun.getSightY(), PlayerGun.SIGHT_FRAME_WIDTH, PlayerGun.SIGHT_FRAME_HEIGHT);
         }

         // Drawing the gun
         g.setColor(Color.yellow);
         g.fillRect(p_gamelet.p_PlayerGun.getGunX(), p_gamelet.p_PlayerGun.getGunY(), 20, 20);

         // Drawing bullets
         g.setColor(Color.black);
         g.drawString("" + p_gamelet.p_PlayerGun.i_bulletsingun+","+p_gamelet.i_currentPlayerHealth, 10, 10);

         // Drawing Hit
         if (p_gamelet.i_lastHit_x>=0)
         {
             g.setColor(Color.red);
             g.drawRect(p_gamelet.i_lastHit_x-5,p_gamelet.i_lastHit_y-5,10,10);
         }

         p_display.repaint();
     }

}
*/
/*
import com.itx.mbgame.GameObject;
import com.igormaznitsa.gameapi.Gamelet;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.io.*;

public class Bulgar_test extends Applet implements Runnable, KeyListener
{
    Image offImg = null;
    Graphics offGr = null;

    Bulgar bul = new Bulgar(101, 64, null);

    public void init()
    {
        //RandomAccessFile fl = new RandomAccessFile();
        offImg = createImage(bul.W, bul.H);
        offGr = offImg.getGraphics();
        setSize(bul.W, bul.H);
        addKeyListener(this);
        new Thread(this).start();
    }

    public void run()
    {
        while (true)
        {
            bul.nextGameStep(new game_PMR());
            repaint();
            try
            {
                Thread.sleep(30);
            }
            catch (Exception ex)
            {
            }
        }
    }

    public void keyTyped(KeyEvent e)
    {
    }

    public void keyPressed(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_S:
                {
                    try
                    {
                        File fl = new File("./thief.sv");
                        fl.createNewFile();
                        FileOutputStream out = new FileOutputStream(fl);
                        bul.writeToStream(new DataOutputStream(out));
                        out.close();
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
                break;
            case KeyEvent.VK_L:
                {
                    try
                    {
                        File fl = new File("./thief.sv");
                        FileInputStream in = new FileInputStream(fl);
                        bul.readFromStream(new DataInputStream(in));
                        in.close();
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
                break;
        }
        if(e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            if(bul.i_PlayerState == Gamelet.PLAYERSTATE_LOST)
            {
                if(bul.i_GameState == Gamelet.GAMESTATE_OVER)
                    bul.newGameSession(0);
                else
                    bul.resumeGameAfterPlayerLost();
            }
            if(bul.i_PlayerState == Gamelet.PLAYERSTATE_WON)
            {
                if(bul.i_GameState == Gamelet.GAMESTATE_OVER)
                    bul.newGameSession(0);
                else
                {
                    bul.i_GameStage++;
                    bul.initStage(bul.i_GameStage);
                }
            }
        }
        if (bul.bulgar.getDirection() == GameObject.DIR_STOP && bul.i_PlayerState == Gamelet.PLAYERSTATE_NORMAL)
        {
            switch (e.getKeyCode())
            {
                case KeyEvent.VK_LEFT:
                    {
                        bul.bulgar.setDirection(GameObject.DIR_LEFT);
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    {
                        bul.bulgar.setDirection(GameObject.DIR_RIGHT);
                    }
                    break;
                case KeyEvent.VK_UP:
                    {
                        bul.bulgar.setDirection(GameObject.DIR_UP);
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    {
                        bul.bulgar.setDirection(GameObject.DIR_DOWN);
                    }
                    break;
            }
        }
    }

    public void keyReleased(KeyEvent e)
    {

    }

    public void paint(Graphics g)
    {
        if (offGr == null) return;
        offGr.setColor(new Color(200, 200, 255));
        offGr.fillRect(0, 0, getSize().width, getSize().height);

        int bulX = bul.W / 2 - bul.bulgar.WIDTH() / 2;
        int bulY = bul.H / 2 - bul.bulgar.HEIGHT() / 2;

        // Draw dungeon
        int q = bul.quadWH;
        int bx = bul.bulgar.X();
        int by = bul.bulgar.Y();

        if (bul.level != null)
            for (int y = -4; y < 5; y++)
                for (int x = -6; x < 7; x++)
                {
                    if (bx + x >= 0 && by + y >= 0 && bx + x < bul.level[0].length && by + y < bul.level.length)
                    {
                        int qd = bul.level[by + y][bx + x];
                        // Walls
                        if (qd >= 1 && qd <= 9)
                        {
                            offGr.setColor(new Color(100, 100, 255));
                            offGr.drawRect(bulX + x * q - bul.tmpX - 1, bulY + y * q - bul.tmpY - 1, q, q);
                        }
                        else
                        {
                            // Floor
                            offGr.setColor(new Color(100, 100, 255));
                            offGr.fillRect(bulX + x * q - bul.tmpX - 1, bulY + y * q - bul.tmpY - 1, q, q);
                            // Floor buttons
                            if (qd >= 70 && qd <= 89)
                            {
                                offGr.setColor(Color.blue);
                                offGr.drawRect(bulX + x * q - bul.tmpX + 2, bulY + y * q - bul.tmpY + 2, 6, 6);
                            }
                            // Pushed floor button
                            if (qd == 90)
                            {
                                offGr.setColor(new Color(100, 100, 200));
                                offGr.drawRect(bulX + x * q - bul.tmpX + 2, bulY + y * q - bul.tmpY + 2, 6, 6);
                            }
                            // Doors
                            if (qd >= 10 && qd <= 29)
                            {
                                offGr.setColor(Color.black);
                                if (qd < 20) // Vertical
                                    offGr.fillRect(bulX + x * q - bul.tmpX + 2, bulY + y * q - bul.tmpY - 1, 6, q);
                                else // Horizontal
                                    offGr.fillRect(bulX + x * q - bul.tmpX - 1, bulY + y * q - bul.tmpY + 2, q, 6);
                            }
                            // Opened doors
                            if (qd == 91 || qd == 92)
                            {
                                offGr.setColor(Color.black);
                                if (qd == 91) // Vertical
                                    offGr.drawRect(bulX + x * q - bul.tmpX + 2, bulY + y * q - bul.tmpY - 1, 6, q - 1);
                                else // Horizontal
                                    offGr.drawRect(bulX + x * q - bul.tmpX - 1, bulY + y * q - bul.tmpY + 2, q - 1, 6);
                            }
                            // Electroshok
                            if(qd == 150 || qd == 151)
                            {
                                offGr.setColor(new Color(180,0,0));
                                if(bul.globalcounter %2 ==0 && qd == 150)
                                    offGr.setColor(new Color(100,0,0));
                                if(qd == 150)
                                    offGr.fillRect(bulX + x * q - bul.tmpX , bulY + y * q - bul.tmpY + 2, q - 1, 6);
                                else
                                    offGr.drawRect(bulX + x * q - bul.tmpX , bulY + y * q - bul.tmpY + 2, q - 1, 6);
                            }
                            // Keys
                            if (qd >= 50 && qd <= 69)
                            {
                                offGr.setColor(Color.gray);
                                offGr.fillRect(bulX + x * q - bul.tmpX + 4, bulY + y * q - bul.tmpY + 4, 2, 2);
                            }
                            // Treasure
                            if (qd >= 110 && qd <= 127)
                            {
                                offGr.setColor(Color.yellow);
                                offGr.fillRect(bulX + x * q - bul.tmpX + 3, bulY + y * q - bul.tmpY + 3, 3, 3);
                            }
                            // Exit
                            if (qd == 193)
                            {
                                offGr.setColor(Color.green);
                                offGr.fillRect(bulX + x * q - bul.tmpX + 2, bulY + y * q - bul.tmpY + 2, 5, 5);
                            }
                        }
                    }
                }
        // Guardians
        for (int i = 0; i < bul.guards.length; i++)
        {
            int gx = bul.guards[i].X() - bx;
            int gy = bul.guards[i].Y() - by;
            int visx = bulX + gx * q - bul.tmpX + 1;
            int visy = bulY + gy * q - bul.tmpY + 1;
            // Guards move
            visx += bul.guards[i].aX;
            visy += bul.guards[i].aY;
            int dir = bul.guards[i].vDir; // head direction
            if (visx >= 0 && visx <= bul.W && visy >= 0 && visy <= bul.H)
            {
                offGr.setColor(Color.red);
                offGr.fillRect(visx, visy, q - 3, q - 3);
            }
        }

        // Key rect
        for (int y = 0; y < bul.keys.length; y++)
        {
            offGr.setColor(Color.yellow);
            offGr.fillRect(2, y * 12 + 2, 10, 10);
            if (bul.keys[y] != 0)
            {
                offGr.setColor(Color.pink);
                offGr.fillRect(2 + 3, y * 12 + 2 + 3, 4, 4);
            }
        }

        // Gold
        offGr.setColor(Color.red);
        offGr.drawString("G:" + bul.gold, bul.W - 30, 10);

        // Lives
        offGr.setColor(Color.black);
        offGr.drawString("L:" + bul.lives, bul.W - 30, 20);


        // Draw bulgar
        offGr.setColor(Color.gray);
        int cadr = 0; // 4 animation cadres
        int dir =  0; // Head direction
        if (bul.tmpX != 0) cadr = bul.tmpX % 4;
        if (bul.tmpY != 0) cadr = bul.tmpY % 4;
        dir = bul.bulgar.vDir;
        offGr.fillRect(bulX, bulY, bul.bulgar.WIDTH(), bul.bulgar.HEIGHT());

        if(bul.i_PlayerState == Gamelet.PLAYERSTATE_LOST)
        {
            offGr.setColor(Color.red);
            if(bul.i_GameState != Gamelet.GAMESTATE_OVER)
                offGr.drawString("You Are Dead",10,bul.H/2);
            else
                offGr.drawString("Game Over",10,bul.H/2);
        }

        g.drawImage(offImg, 0, 0, this);
    }

    public void update(Graphics g)
    {
        paint(g);
    }

}










*/
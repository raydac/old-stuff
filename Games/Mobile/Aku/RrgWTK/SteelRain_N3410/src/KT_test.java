
/*
import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import com.itx.mbgame.*;
import com.igormaznitsa.gameapi.Gamelet;


public class KT_test extends Applet implements Runnable, KeyListener
{
    Font font = new Font("Dialog", Font.BOLD, 10);
    Font big = new Font("Dialog", Font.BOLD, 16);

    Image offImg = null;
    Graphics offGr = null;

    KillerTank tank = new KillerTank(101, 64, null);

    public void init()
    {
        offImg = createImage(tank.W, tank.H);
        offGr = offImg.getGraphics();
        setSize(tank.W, tank.H);
        addKeyListener(this);
        new Thread(this).start();
    }

    public void run()
    {
        while (true)
        {
            repaint();
            try
            {
                tank.gameAction();
                Thread.sleep(100);
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
            case KeyEvent.VK_LEFT:
                {
                    if (tank.hobotAngle < 30)
                    {
                        tank.hobotAngle++;
                    }
                }
                break;
            case KeyEvent.VK_RIGHT:
                {
                    if (tank.hobotAngle > -2)
                    {
                        tank.hobotAngle--;
                    }
                }
                break;
            case KeyEvent.VK_UP:
                {
                    if (tank.targetRadius < 90)
                    {
                        tank.targetRadius += 2;
                    }
                }
                break;
            case KeyEvent.VK_DOWN:
                {
                    if (tank.targetRadius > 20)
                    {
                        tank.targetRadius -= 2;
                    }
                }
                break;
            case KeyEvent.VK_ENTER:
                {
                    for (int i = 0; i < tank.fp.length; i++)
                    {
                        if (!tank.fp[i].fire)
                        {
                            tank.fp[i].fire = true;
                            tank.fp[i].fireAngle = tank.hobotAngle;
                            tank.fp[i].fireRadius = tank.targetRadius;
                            tank.fp[i].fireRadiusCounter = tank.hobotRadius;
                            break;
                        }
                    }
                }
                break;
        }
    }

    public void keyReleased(KeyEvent e)
    {
    }

    public void paint(Graphics g)
    {
        if (offGr == null) return;
        offGr.setColor(new Color(0, 150, 0));
        offGr.fillRect(0, 0, getSize().width, getSize().height);

        // test line of fire
        offGr.setColor(Color.black);
        offGr.drawLine(0,tank.LINEOFFIRE,tank.W,tank.LINEOFFIRE);

        // My Killer Tank
        offGr.setColor(Color.blue);
        int w = tank.tank.WIDTH();
        int h = tank.tank.HEIGHT();
        offGr.fillRect(tank.W / 2 - w / 2, tank.H - h, w, h);
        if(tank.shield)
        {
            if(tank.shieldCounter % 2 == 0)
                offGr.setColor(Color.white);
            else
                offGr.setColor(Color.lightGray);
            offGr.drawRect(tank.W / 2 - w / 2, tank.H - h, w, h);
        }

        // Hobot
        int angle = (tank.hobotAngle + 2) & 63;
        int x2 = tank.hobotX + Gamelet.xCoSine(tank.hobotRadius, angle);
        int y2 = tank.hobotY - Gamelet.xSine(tank.hobotRadius, angle);
        offGr.setColor(Color.black);
        offGr.drawLine(tank.hobotX, tank.hobotY, x2, y2);

        // target
        offGr.setColor(Color.white);
        x2 = tank.hobotX + Gamelet.xCoSine(tank.targetRadius, angle);
        y2 = tank.hobotY - Gamelet.xSine(tank.targetRadius, angle);
        offGr.drawLine(x2, y2, x2 + 2, y2);
        offGr.drawLine(x2 + 1, y2 - 1, x2 + 1, y2 + 1);

        // fire
        for (int i = 0; i < tank.fp.length; i++)
        {
            if (tank.fp[i].fire)
            {
                offGr.setColor(Color.red);
                angle = (tank.fp[i].fireAngle + 2) & 63;
                x2 = tank.hobotX + Gamelet.xCoSine(tank.fp[i].fireRadiusCounter, angle);
                y2 = tank.hobotY - Gamelet.xSine(tank.fp[i].fireRadiusCounter, angle);
                if (!tank.fp[i].explosion.isActive())
                {
                    offGr.fillRect(x2, y2, 2, 2);
                }
                else
                {
                    if (tank.fp[i].explosionCounter % 2 == 0)
                        offGr.setColor(Color.yellow);
                    else
                        offGr.setColor(Color.red);

                    // !!! Required for collision detection !!!
                    // Sandy blya - nu nahren mne ne nado v gameStep schitat' eto!!!
                    tank.fp[i].explosion.setCoord((byte) (x2 - tank.fp[i].explosion.WIDTH() / 2), (byte) (y2 - tank.fp[i].explosion.HEIGHT() / 2));
                    // >8E

                    offGr.fillRect(tank.fp[i].explosion.X(), tank.fp[i].explosion.Y(), tank.fp[i].explosion.WIDTH(), tank.fp[i].explosion.HEIGHT());
                }

            }
        }

        // Heal present
        if (tank.healPresent.isActive())
        {
            offGr.setColor(Color.blue);
            offGr.fillRect(tank.healPresent.X(), tank.healPresent.Y(), tank.healPresent.WIDTH(), tank.healPresent.HEIGHT());
        }

        // Shield present
        if (tank.shieldPresent.isActive())
        {
            offGr.setColor(Color.yellow);
            offGr.fillRect(tank.shieldPresent.X(), tank.shieldPresent.Y(), tank.shieldPresent.WIDTH(), tank.shieldPresent.HEIGHT());
        }

        // Enemies
        for (int i = 0; i < tank.enemy.length; i++)
        {
            if(tank.enemy[i].isActive())
            {
                switch(tank.enemy[i].type)
                {
                    case 0:
                        offGr.setColor(new Color(0,100,0));
                        break;
                    case 1:
                        offGr.setColor(new Color(0,180,0));
                        break;
                    case 2:
                        offGr.setColor(new Color(0,240,0));
                        break;
                    default:
                }
                offGr.fillRect(tank.enemy[i].X(), tank.enemy[i].Y(), tank.enemy[i].WIDTH(), tank.enemy[i].HEIGHT());
            }
            // Enemy fire;
            if(tank.enemy[i].fire)
            {
                offGr.setColor(Color.red);
                offGr.fillRect(tank.enemy[i].aX, tank.enemy[i].aY, 2, 2);
            }
        }

        // Aircraft
        if(tank.aircraftObj.isActive())
        {
            offGr.setColor(Color.blue);
            offGr.fillRect(tank.aircraftObj.X(), tank.aircraftObj.Y(), tank.aircraftObj.WIDTH(), tank.aircraftObj.HEIGHT());
        }

        offGr.setColor(Color.blue);
        offGr.draw3DRect(2, 2, tank.MAXLIVES, 5, true);
        offGr.fillRect(2, 2, tank.lives, 5);
        offGr.setColor(Color.white);
        offGr.setFont(font);
        offGr.drawString("score: " + tank.i_PlayerScore, 2, 20);

        if(tank.i_GameState == tank.GAMESTATE_OVER)
        {
            offGr.setColor(Color.red);
            offGr.setFont(big);
            offGr.drawString("Game Over", 2, tank.H/2);
        }

        g.drawImage(offImg, 0, 0, this);

    }

    public void update(Graphics g)
    {
        paint(g);
    }

}












*/
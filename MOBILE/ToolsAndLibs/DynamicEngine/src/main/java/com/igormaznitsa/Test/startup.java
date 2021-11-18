package com.igormaznitsa.Test;

import com.igormaznitsa.DynamicEngine.DynamicImage_8bpp;
import com.igormaznitsa.DynamicEngine.ImageScaler;
import com.nokia.mid.ui.FullCanvas;
import com.nokia.mid.ui.DirectGraphics;
import com.nokia.mid.ui.DirectUtils;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import java.io.IOException;
import java.util.Random;

public class startup extends MIDlet implements Runnable
{
    private Random p_randomGen = new Random();
    private Image p_bckgImage;

    private class CanvasImplement extends FullCanvas
    {
        public Object p_obj = new Object();

        protected void keyPressed(int i)
        {
            if (getGameAction(i) == FullCanvas.FIRE) notifyDestroyed();
        }

        protected void paint(Graphics g)
        {
            synchronized (p_obj)
            {
                if (p_bckgImage == null) return;
                    if (p_bckgImage != null)
                    {
                        g.drawImage(p_bckgImage, 0, 0, 0);
                    }
            }
        }
    }

    byte[] ab_imageArray;
    DynamicImage_8bpp p_dynamicEngine;
    CanvasImplement p_canvas;

    protected void startApp() throws MIDletStateChangeException
    {
        ab_imageArray = new byte[5312];
        byte[] ab_palette = new byte[175];

        try
        {
            this.getClass().getResourceAsStream("/palette.bin").read(ab_palette);
            this.getClass().getResourceAsStream("/set_4.bin").read(ab_imageArray);
            p_dynamicEngine = new DynamicImage_8bpp(128, 128, ab_palette);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }

        p_canvas = new CanvasImplement();

        Display.getDisplay(this).setCurrent(p_canvas);

        new Thread(this).run();
    }

    protected void pauseApp()
    {

    }

    protected void destroyApp(boolean b) throws MIDletStateChangeException
    {

    }

    public void run()
    {
        try
        {
            int i8_coeff = 0;

            p_dynamicEngine.drawSpriteFromLibrary(ab_imageArray,0,0,0);

            for (int li = 0; li < 32; li++)
            {
                p_dynamicEngine.loadPaletteFromPaletteArray(i8_coeff, 0, true);
                i8_coeff += 8;
                p_bckgImage = p_dynamicEngine.encodeToImage();
                p_canvas.repaint();
                Thread.sleep(100);
                synchronized (p_canvas.p_obj)
                {
                }
            }

            p_dynamicEngine.loadPaletteFromPaletteArray(0x100, 0, true);


            for(int li=0;li<8;li++)
            {
                p_dynamicEngine.scrollLeft(1);
                p_bckgImage = p_dynamicEngine.encodeToImage();
                p_canvas.repaint();
                Thread.sleep(100);
                synchronized (p_canvas.p_obj)
                {
                }
            }

            for(int li=0;li<16;li++)
            {
                p_dynamicEngine.scrollRight(1);
                p_bckgImage = p_dynamicEngine.encodeToImage();
                p_canvas.repaint();
                Thread.sleep(100);
                synchronized (p_canvas.p_obj)
                {
                }
            }

            for(int li=0;li<8;li++)
            {
                p_dynamicEngine.scrollLeft(1);
                p_bckgImage = p_dynamicEngine.encodeToImage();
                p_canvas.repaint();
                Thread.sleep(100);
                synchronized (p_canvas.p_obj)
                {
                }
            }

            for(int li=0;li<2;li++)
            {
                p_dynamicEngine.scrollDown(1);
                p_bckgImage = p_dynamicEngine.encodeToImage();
                p_canvas.repaint();
                Thread.sleep(100);
                synchronized (p_canvas.p_obj)
                {
                }
            }

            for(int li=0;li<7;li++)
            {
                p_dynamicEngine.scrollUp(1);
                p_bckgImage = p_dynamicEngine.encodeToImage();
                p_canvas.repaint();
                Thread.sleep(100);
                synchronized (p_canvas.p_obj)
                {
                }
            }

            for(int li=0;li<5;li++)
            {
                p_dynamicEngine.scrollDown(1);
                p_bckgImage = p_dynamicEngine.encodeToImage();
                p_canvas.repaint();
                Thread.sleep(100);
                synchronized (p_canvas.p_obj)
                {
                }
            }

            for(int li=0;li<4;li++)
            {
                p_dynamicEngine.rotateLeft();
                p_bckgImage = p_dynamicEngine.encodeToImage();
                p_canvas.repaint();
                Thread.sleep(400);
                synchronized (p_canvas.p_obj)
                {
                }
            }

            for(int li=0;li<4;li++)
            {
                p_dynamicEngine.rotateRight();
                p_bckgImage = p_dynamicEngine.encodeToImage();
                p_canvas.repaint();
                Thread.sleep(400);
                synchronized (p_canvas.p_obj)
                {
                }
            }

            for(int li=0;li<4;li++)
            {
                p_dynamicEngine.flipHorizontal();
                p_bckgImage = p_dynamicEngine.encodeToImage();
                p_canvas.repaint();
                Thread.sleep(400);
                synchronized (p_canvas.p_obj)
                {
                }
            }

            for(int li=0;li<4;li++)
            {
                p_dynamicEngine.flipVertical();
                p_bckgImage = p_dynamicEngine.encodeToImage();
                p_canvas.repaint();
                Thread.sleep(400);
                synchronized (p_canvas.p_obj)
                {
                }
            }

            for(int li=0;li<p_dynamicEngine.getPaletteWidth();li++)
            {
                p_dynamicEngine.scrollPalette(0,p_dynamicEngine.getPaletteWidth(),true,true);
                p_bckgImage = p_dynamicEngine.encodeToImage();
                p_canvas.repaint();
                Thread.sleep(100);
                synchronized (p_canvas.p_obj)
                {
                }
            }

            for(int ly=0;ly<p_dynamicEngine.getHeight();ly++)
            {
                for(int lx=0;lx<p_dynamicEngine.getWidth();lx++)
                {
                    int i_color = p_dynamicEngine.getPixel(lx,ly);
                    p_dynamicEngine.setPixel(lx,ly,p_dynamicEngine.getPaletteWidth()-i_color-1);
                }
                p_bckgImage = p_dynamicEngine.encodeToImage();
                synchronized (p_canvas.p_obj)
                {
                }
                p_canvas.repaint();
            }

            for(int ly=0;ly<p_dynamicEngine.getHeight();ly++)
            {
                for(int lx=0;lx<p_dynamicEngine.getWidth();lx++)
                {
                    int i_color = p_dynamicEngine.getPixel(lx,ly);
                    p_dynamicEngine.setPixel(lx,ly,p_dynamicEngine.getPaletteWidth()-i_color-1);
                }
                p_bckgImage = p_dynamicEngine.encodeToImage();
                synchronized (p_canvas.p_obj)
                {
                }
                p_canvas.repaint();
            }

            while (true)
            {
            }
        }
        catch (Exception e)
        {
        }
    }
}

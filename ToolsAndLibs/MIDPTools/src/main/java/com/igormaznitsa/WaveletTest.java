package com.igormaznitsa;

import com.igormaznitsa.Utils.WaveletCodec;

import java.applet.Applet;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class WaveletTest extends Applet
{
    byte [] ab_inImage;
    WaveletCodec p_wc;

    Image p_Image47;
    Image p_Image48;
    Image p_Image49;
    Image p_Image50;
    BufferedImage p_bimg;

    public void start()
    {
       try
       {
        WaveletCodec p_wc = new WaveletCodec();

        String s_fileName = "d:/battleship.jpg";

        File inFile = new File(s_fileName);
        if (!inFile.exists())
        {
            System.out.println("Sorry but the file ["+inFile.getCanonicalPath()+"] does not exist");
            System.exit(1);
        }

            ab_inImage = new byte[(int)inFile.length()];

            new FileInputStream(inFile).read(ab_inImage);

            byte [] ab_nonpacked = p_wc.encodeImage(ab_inImage,46,false);

            byte [] ab_image47 = p_wc.encodeImage(ab_inImage,46);
            new FileOutputStream("d:/purewavelet.dat").write(p_wc.ab_nonpackedArray);

            byte [] ab_image48 = p_wc.encodeImage(ab_inImage,47);
            byte [] ab_image49 = p_wc.encodeImage(ab_inImage,48);
            byte [] ab_image50 = p_wc.encodeImage(ab_inImage,49);

            System.out.println("IMG47 = "+ab_image47.length);
            System.out.println("IMG48 = "+ab_image48.length);
            System.out.println("IMG49 = "+ab_image49.length);
            System.out.println("IMG50 = "+ab_image50.length);

            p_Image47 = p_wc.decodeArray(ab_image47);
            p_Image48 = p_wc.decodeArray(ab_image48);
            p_Image49 = p_wc.decodeArray(ab_image49);
            p_Image50 = p_wc.decodeArray(ab_image50);

           p_bimg = new BufferedImage(p_Image47.getWidth(null)<<1,p_Image47.getHeight(null),BufferedImage.TYPE_INT_ARGB);

           Graphics p_g = p_bimg.getGraphics();

           int i_offst = 0;

           int [] ai_stat = new int [256];

           for (int ly=0;ly<p_bimg.getHeight();ly++)
           {
               for (int lx=0;lx<p_bimg.getWidth();lx++)
               {
                   int i_value = ab_nonpacked[i_offst]&0xFF;
                   ai_stat[i_value]++;
                   p_g.setColor(new Color(i_value,i_value,i_value));
                   p_g.drawLine(lx,ly,lx,ly);
                   i_offst++;
               }
           }

           FileOutputStream p_fos = new FileOutputStream("d:/outarray.dat");
           p_fos.write(ab_nonpacked);
           p_fos.close();

           System.out.println("NONPACKED SIZE = "+ab_nonpacked.length);
           System.out.println("STAT:");
           for(int li=0;li<256;li++)
           {
               if (ai_stat[li] == 0) continue;
               System.out.println("#"+Integer.toHexString(li)+" : "+ai_stat[li]);
           }
        }
        catch (Exception e)
        {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            System.exit(1);
        }
        repaint();
    }

    public void paint(Graphics _g)
    {
        int i_width = p_Image47.getWidth(null);

        int i_x = 10;
        _g.drawImage(p_Image47,i_x,5,null);
        i_x+=i_width+5;
        _g.drawImage(p_Image48,i_x,5,null);
        i_x+=i_width+5;
        _g.drawImage(p_Image49,i_x,5,null);
        i_x+=i_width+5;
        _g.drawImage(p_Image50,i_x,5,null);
        i_x+=i_width+5;

        _g.drawImage(p_bimg,i_x,5,null);

    }
}

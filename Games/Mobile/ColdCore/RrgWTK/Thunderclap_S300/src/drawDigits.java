
import javax.microedition.lcdui.*;

public class drawDigits
{
    public int fontHeight,fontWidth;
    private Image numbers;
    public boolean horizontal=true;  

    public drawDigits(Image img, int Radix) throws NullPointerException
    {
        fontHeight = img.getHeight();
        fontWidth = img.getWidth() / Radix;
        numbers = img;
    }

    public drawDigits(Image img, int Radix, boolean orientation) throws NullPointerException
    {
        horizontal = orientation;
        numbers = img;
        fontHeight = img.getHeight();
        fontWidth = img.getWidth();

        if(orientation)
        {
           fontWidth /= Radix;
        }
         else
             {
                fontHeight /= Radix;
             }

    }


    public void drawDigits(Graphics gBuffer, int x, int y, int vacant, int num, int Radix)
    {
        int i,xx,yy,hh,ww;
        x = x + fontWidth * (--vacant);

        hh = gBuffer.getClipHeight();
        ww = gBuffer.getClipWidth();
        xx = gBuffer.getClipX();
        yy = gBuffer.getClipY();

        if (num >= 0)
            if(horizontal)
                 for (; vacant >= 0; vacant--)
                 {
                     i = (num % Radix) * fontWidth;

                     gBuffer.setClip(x,y,fontWidth, fontHeight);
                     gBuffer.drawImage(numbers, x-i, y, 0);
                     num = num / Radix;
                     x -= fontWidth;
                 }
              else
                 for (; vacant >= 0; vacant--)
                 {
                     i = (num % Radix) * fontHeight;

                     gBuffer.setClip(x,y,fontWidth, fontHeight);
                     gBuffer.drawImage(numbers, x, y-i, 0);
                     num = num / Radix;
                     x -= fontWidth;
                 }
        gBuffer.setClip(xx,yy,ww,hh);
    }
}

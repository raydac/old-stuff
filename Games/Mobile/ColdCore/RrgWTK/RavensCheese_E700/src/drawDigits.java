
import javax.microedition.lcdui.*;

public class drawDigits
{
    public int fontHeight,fontWidth;
    private Image numbers;
    private Image place;

    public drawDigits(Image img, int Radix) throws NullPointerException
    {
        fontHeight = img.getHeight();
        fontWidth = img.getWidth() / Radix;
        numbers = img;
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
            for (; vacant >= 0; vacant--)
            {
         i = (num % Radix) * fontWidth;

  gBuffer.setClip(x,y,fontWidth, fontHeight);
                gBuffer.drawImage(numbers, x-i, y, 0);
                num = num / Radix;
                x -= fontWidth;
            }
        gBuffer.setClip(xx,yy,ww,hh);
    }
}

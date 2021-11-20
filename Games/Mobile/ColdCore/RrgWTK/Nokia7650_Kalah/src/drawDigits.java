
import javax.microedition.lcdui.*;

/**
 * Title: Customized output
 * Description: Draws digits form the font image
 */
public class drawDigits
{
    public int fontHeight,fontWidth;
    private Image numbers;

    /**
     * Construct numbers array from given font image
     * @param img font image (sequence of painted digits [1234567890])
     * @throws NullPointerException
     */
    public drawDigits(Image img, int Radix) throws NullPointerException
    {
        fontHeight = img.getHeight();
        fontWidth = img.getWidth() / Radix;
        numbers = img;
    }

    /**
     * Paints sequence of digits in decimal representation
     * @param gBuffer target canvas
     * @param x x-coordinate of left-top corner
     * @param y y-coordinate of lett-top corner
     * @param vacant amount of digits to output
     * @param num value of digits
     */
    public void drawDigits(Graphics gBuffer, int x, int y, int vacant, int num, int Radix)
    {
        int i,xx,yy,hh,ww,dx;
        dx = fontWidth * (--vacant);
        x = x + dx;

 hh = gBuffer.getClipHeight();
 ww = gBuffer.getClipWidth();
 xx = gBuffer.getClipX();
 yy = gBuffer.getClipY();

        if (num >= 0)
            for (; vacant >= 0; vacant--)
            {
         i = (num % Radix) * fontWidth;

  gBuffer.setClip(x,y,fontWidth, fontHeight);
                gBuffer.drawImage(numbers, x-i, y, 0/* Graphics.LEFT|Graphics.TOP*/);
                num = num / Radix;
                x -= fontWidth;
            }
        gBuffer.setClip(xx,yy,ww,hh);
    }
}

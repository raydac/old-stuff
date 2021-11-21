// #excludeif true

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
    public drawDigits(Image img) throws NullPointerException
    {
        fontHeight = img.getHeight();
        fontWidth = img.getWidth() / 10;
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
    public void drawDigits(Graphics gBuffer, int x, int y, int vacant, int num)
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
         i = (num % 10) * fontWidth;

  gBuffer.setClip(x,y,fontWidth, fontHeight);
                gBuffer.drawImage(numbers, x-i, y, 0/* Graphics.LEFT|Graphics.TOP*/);
                num = num / 10;
                x -= fontWidth;
            }
        gBuffer.setClip(xx,yy,ww,hh);
    }
}

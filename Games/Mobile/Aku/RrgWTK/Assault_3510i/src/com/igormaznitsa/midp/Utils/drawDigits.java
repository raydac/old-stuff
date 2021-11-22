package com.igormaznitsa.midp.Utils;

import javax.microedition.lcdui.*;

/**
 * Title: Customized output
 * Description: Draws digits form the font image
 */
public class drawDigits
{
    public int fontHeight = 0,fontWidth = 0;
    private Image[] numbers = new Image[10];

    /**
     * Construct numbers array from given font image
     * @param img font image (sequence of painted digits [1234567890])
     * @throws NullPointerException
     */
    public drawDigits(Image img) throws NullPointerException
    {
        fontHeight = img.getHeight();
        fontWidth = img.getWidth() / 10;
        for (int i = 0; i < 10; i++)
        {
            numbers[i] = Image.createImage(fontWidth, fontHeight);
            numbers[i].getGraphics().drawImage(img, -i * fontWidth, 0, Graphics.TOP | Graphics.LEFT);
        }
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
        x = x + fontWidth * (--vacant);
        if (num >= 0)
            for (; vacant >= 0; vacant--)
            {
                gBuffer.drawImage(numbers[num % 10], x, y, 0/* Graphics.LEFT|Graphics.TOP*/);
                num = num / 10;
                x -= fontWidth;
            }
    }
}
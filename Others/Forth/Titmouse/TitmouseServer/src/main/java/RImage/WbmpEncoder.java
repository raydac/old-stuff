package RImage;

//import java.awt.Color;
import java.io.*;

public class WbmpEncoder
{
	private RImage main_image;
	
	
	public WbmpEncoder(RImage img,OutputStream outputstream)
    {
		os_ = null;
        os_ = outputstream;
		main_image=img;
	}

    public void encode() throws IOException
    {
        if(os_ != null) write(os_);
    }

    public OutputStream getOutputStream()
    {
        return os_;
    }

	private void encode(int i) throws IOException
	{
		int j = i;
		int k;
		for(k = 1; (j >>= 7) > 1; k++);
		byte abyte0[] = new byte[k];
		for(int l = k - 1; l >= 0; l--)
		{
			abyte0[l] = (byte)(i & 0x7f);
			i >>= 7;
		}
		for(int i1 = 0; i1 < k; i1++) this.os_.write(i1 == k - 1 ? ((int) (abyte0[i1])) : abyte0[i1] | 0x80);
	}
	
    private void write(OutputStream outputstream)
        throws IOException
    {
        int l = main_image.ImageWidth;
        int i1 = main_image.ImageHeight;
        outputstream.write(0);
        outputstream.write(0);
		encode(l);
        encode(i1);
        int k = l % 8;
        int i = l / 8 + (k <= 0 ? 0 : 1);
        for(int j1 = 0; j1 < i1; j1++)
        {
            for(int k1 = 0; k1 < i; k1++)
            {
                byte byte0 = 0;
                int l1 = k <= 0 || k1 != i - 1 ? 8 : k;
                for(int i2 = 0; i2 < l1; i2++)
                {
					int j;
					if (main_image.GetPoint(k1 * 8+i2,j1)>main_image.CurrentColor) j=0; else j=1;
                    byte0 |= j << 7 - i2;
                }
                outputstream.write(byte0);
            }
        }
    }

    private OutputStream os_;
}

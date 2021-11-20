package com.igormaznitsa.midp;

import com.igormaznitsa.GameAPI.LoadListener;

import javax.microedition.lcdui.Image;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

public class ImageBlock
{
    public Image[] _image_array;

    /**
     * Return the image for the id. If image with the id is not exists then return null
     * @param id
     * @return image as javax.microedition.lcdui.Image
     */
/*
    public void CopyArray(Image[] i, int Offset)
    {
       for(int j=0;j<i.length;j++)i[j]=_image_array[Offset+j];
    }


    public Image getImageForID(int id)
    {
        return _image_array[id];
    }
*/
    /**
     * Constructor
     * @param resource_name File name of resource contains PNG packed images
     * @param loadListener link to load listener object
     * @throws IOException exception which throws when any problem in load or create time of an image
     */
    public ImageBlock(/*String resource_name,*/ LoadListener loadListener) throws IOException
    {
        Runtime.getRuntime().gc();
        InputStream dis = getClass().getResourceAsStream(/*resource_name*/"/res/images.bin");

        DataInputStream ds = new DataInputStream(dis);
        // Reading of image number
        int num = ds.readUnsignedByte();
        _image_array = new Image[num+1];
        _image_array[num] = getMenuIcon();

        int n;
        try
        {
            for (int li = 0; li < num; li++)
            {
	       n = ds.readUnsignedByte();
	       if( n == 255 /* HYPERLINK */){
		  _image_array[ds.readUnsignedByte()] = _image_array[ds.readUnsignedByte()];
	       }
	         else
                   if(
		       (_image_array[ n ] = getImageFromInputStream(ds))
                            == null )
                                     throw new IOException("I can't create image, indx[" + li + "]");

                if (loadListener != null) loadListener.nextItemLoaded(1);
                Runtime.getRuntime().gc();
            }
        }
        finally
        {
            if (dis != null)
            {
                try
                {
                    dis.close();
                }
                catch (IOException e)
                {
                }
                dis = null;
            }
            ds = null;
            Runtime.getRuntime().gc();
        }
    }
        public Image getImageFromInputStream(DataInputStream _inputstream) throws IOException
        {
            boolean lg_ispackedimage = false;
            boolean lg_ismask = false;

            int i_cmndbyte = _inputstream.readUnsignedByte();

            if ((i_cmndbyte & 0x80) != 0) lg_ispackedimage = true;
            if ((i_cmndbyte & 0x20) != 0) lg_ismask = true;

            int i_width = _inputstream.readUnsignedByte();
            int i_height = _inputstream.readUnsignedByte();

            int i_calculatedlength = ((i_width + 7) >> 3) * i_height;

            if (lg_ismask) i_calculatedlength = ((i_width + 3) >> 2) * i_height;

            int i_len = -1;

            byte[] ab_imagearray = null;

            if (lg_ispackedimage)
            {
                i_len = _inputstream.readUnsignedByte();
                if ((i_len & 0x80) != 0)
                {
                    i_len = (((i_len & 0x7F) << 8) | _inputstream.readUnsignedByte());
                }
                ab_imagearray = new byte[i_len];
                _inputstream.readFully(ab_imagearray);
                ab_imagearray = com.igormaznitsa.GameAPI.Utils.Compress.RLEdecompress(ab_imagearray, i_calculatedlength);
            }
            else
            {
                ab_imagearray = new byte[i_calculatedlength];
                _inputstream.readFully(ab_imagearray);
            }

            Image p_img = null;
            try
            {
                if (lg_ismask)
                {
                    p_img = com.siemens.mp.ui.Image.createTransparentImageFromBitmap(ab_imagearray, i_width, i_height);
                }
                else
                {
                    p_img = com.siemens.mp.ui.Image.createImageFromBitmap(ab_imagearray, i_width, i_height);
                }
            }
            catch (Exception ex)
            {
                throw new IOException(ex.getMessage());
            }

            return p_img;
        }
        private long [] storage = {
727905341920923785l, 5927942488114331648l, 360287970307080192l, 1050556976355869704l, 6433515251655770309l, 792633611911168115l, 277621817606418l, 8017379684120791296l, -8574853190445077139l, 5273715163651280170l, -9112403153787797180l, -1603179204034166775l, 4396817202161377437l, 330255705690873998l, -7497651356403323238l, 2019729493505000636l, 6434547960588605l, -5889496353209319424l, -8232894l,
};

	 private Image getMenuIcon()
	 {
	   try {
	    int id = 147;
 	    byte[] img = new byte[id&0xffff];
	    id>>>=16;
	    int pos = 0;
	    long n=0;
	    while (pos < img.length) {
	      if ((pos&7)==0)
	      /* if (pos == 0) n = storage[0];
	         else */ n=storage[id+(pos>>3)];
	      img[pos++]=(byte)n;
	      n>>>=8;
	    }
               storage = null; 
               Image ret = Image.createImage(img,0,img.length);
               img = null;
	       Runtime.getRuntime().gc();
	       return ret;
	   } catch (Exception e) {return null;}
	 }
}

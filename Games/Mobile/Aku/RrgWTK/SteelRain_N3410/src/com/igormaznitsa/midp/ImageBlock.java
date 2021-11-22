package com.igormaznitsa.midp;

import com.igormaznitsa.gameapi.LoadListener;
import javax.microedition.lcdui.Image;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.ByteArrayOutputStream;
import com.nokia.mid.sound.Sound;

public class ImageBlock
{
    public byte[][] _byte_array;
    public Image[] _image_array;

    /**
     * Constructor
     * @param resource_name File name of resource contains PNG packed images
     * @param loadListener link to load listener object
     * @throws IOException exception which throws when any problem in load or create time of an image
     */
    public ImageBlock(/*String resource_name,*/LoadListener loadListener) throws IOException
    {
        Runtime.getRuntime().gc();
        InputStream dis = getClass().getResourceAsStream("/res/images.bin"/*resource_name*/);

        DataInputStream ds = new DataInputStream(dis);
        // Reading of image number
        int num = ds.readUnsignedByte();
        _image_array = new Image[num+1];
        _byte_array = new byte[num/*16*/][];
        _image_array[num] = getMenuIcon();
	storage = null;

        try
        {
            for (int li = 0; li < num; li++)
            {
	      int n = ds.readUnsignedByte();//System.out.println(n+" ("+li+")");
	    try{
	      byte[]b=null;
	      Runtime.getRuntime().gc();
	       if( n == 255 /* HYPERLINK */){
		  int trg = ds.readUnsignedByte();
		  n = ds.readUnsignedByte();
		  if(_byte_array[n]!=null)_byte_array[trg] = _byte_array[n];
		    else _image_array[trg] = _image_array[n];
	       } else {
                 if(
		   (b = getImageFromInputStream(ds,false))
                       == null )
//		              System.out.println("null ptr");
                              throw new IOException("I can't create image, indx[" + li + "]");
		 if(n<3)_byte_array[n] = b;
		    else
		      if(b!=null)
		       try{
			   Runtime.getRuntime().gc();
			   _image_array[n] = Image.createImage(b,0,b.length);
		       }catch (Exception ex)
		       {
//			   System.out.println("can't convert image ptr");
//			   ex.printStackTrace();
			   throw new IOException("I can't create image, indx[" + li + "]");
		       }
	       }
	    } catch (OutOfMemoryError exx){exx.printStackTrace();}

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

    private int crc32(int crc, byte b)
    {
        final int CRC_POLY = 0xEDB88320;
        int i_crc = (crc ^ b) & 0xFF;

        for (int lj = 0; lj < 8; lj++)
        {
            if ((i_crc & 1) == 0)
            {
                i_crc >>>= 1;
            }
            else
            {
                i_crc = (i_crc >>> 1) ^ CRC_POLY;
            }

        }
        crc = i_crc ^ (crc >>> 8);

        return crc;
    }

    private int crc32(int crc, int b)
    {
        crc = crc32(crc, (byte) (b >>> 24));
        crc = crc32(crc, (byte) (b >>> 16));
        crc = crc32(crc, (byte) (b >>> 8));
        crc = crc32(crc, (byte) b);
        return crc;
    }

      public byte[] getImageFromInputStream(DataInputStream _inputstream, boolean isRaw) throws IOException
        {
            final int HEADER_IHDR = 0;
            final int HEADER_PLTE = 1;
            final int HEADER_IDAT = 2;
            final int HEADER_tRNS = 3;

            // Reading length of image
            int len = _inputstream.readUnsignedShort();
//             System.out.println(len);
	    if(isRaw){
	      byte []b = new byte[len];
	      _inputstream.readFully(b);
	      return b;
	    }

            ByteArrayOutputStream p_baos = new ByteArrayOutputStream(len + PNG_COMMON_PALETTE.length);
            DataOutputStream p_daos = new DataOutputStream(p_baos);

            p_daos.writeLong(0x89504E470D0A1A0Al);

            while (p_baos.size() < (len - 12 + PNG_COMMON_PALETTE.length))
            {
                int i_crc = 0;
                // reading chunk
                int i_chunk = _inputstream.readUnsignedByte();
                switch (i_chunk)
                {
                    case HEADER_IHDR:
                        {
                            int i_width = _inputstream.readUnsignedByte();
                            int i_height = _inputstream.readUnsignedByte();
                            int i_flag = _inputstream.readUnsignedByte();

                            int i_bpp = i_flag >> 4;
                            int i_color = i_flag & 0x07;

                            i_crc = 0x575e51f5;

                            i_crc = crc32(i_crc, i_width);
                            i_crc = crc32(i_crc, i_height);
                            i_crc = crc32(i_crc, (byte) i_bpp);
                            i_crc = crc32(i_crc, (byte) i_color);

                            p_daos.writeLong(0x0000000D49484452l);
                            p_daos.writeInt(i_width);
                            p_daos.writeInt(i_height);

                            p_daos.writeByte(i_bpp);
                            p_daos.writeByte(i_color);

                            p_daos.writeShort(0x0);
                            i_crc = crc32(i_crc, (byte) 0);
                            i_crc = crc32(i_crc, (byte) 0);

                            if ((i_flag & 0x08) == 0)
                            {
                                p_daos.writeByte(0);
                                i_crc = crc32(i_crc, (byte) 0);
                            }
                            else
                            {
                                p_daos.writeByte(1);
                                i_crc = crc32(i_crc, (byte) 1);
                            }
                        }
                        ;
                        break;
                    case HEADER_IDAT:
                        {
                            int i_len = _inputstream.readUnsignedByte();
                            if (i_len >= 0x80)
                            {
                                i_len = ((i_len & 0x7F) << 8) | _inputstream.readUnsignedByte();
                            }
                            p_daos.writeInt(i_len);
                            p_daos.writeInt(0x49444154);

                            i_crc = 0xca50f9e1;


                            for (int li = 0; li < i_len; li++)
                            {
                                byte b_byte = (byte) _inputstream.read();
                                p_daos.writeByte(b_byte);
                                i_crc = crc32(i_crc, b_byte);
                            }
                        }
                        ;
                        break;
                    case HEADER_PLTE:
                        {
                            int i_len = _inputstream.readUnsignedByte();
                            if (i_len >= 0x80)
                            {
                                i_len = ((i_len & 0x7F) << 8) | _inputstream.readUnsignedByte();
                            }
                            p_daos.writeInt(i_len);
                            p_daos.writeInt(0x504C5445);

                            i_crc = 0xb45776aa;

                            for (int li = 0; li < i_len; li++)
                            {
                                byte b_byte = (byte) _inputstream.read();
                                p_daos.writeByte(b_byte);
                                i_crc = crc32(i_crc, b_byte);
                            }
                        }
                        ;
                        break;
                    case HEADER_tRNS:
                        {
                            int i_len = _inputstream.readUnsignedByte();
                            if (i_len >= 0x80)
                            {
                                i_len = ((i_len & 0x7F) << 8) | _inputstream.readUnsignedByte();
                            }
                            p_daos.writeInt(i_len);
                            p_daos.writeInt(0x74524e53);

                            i_crc = 0xc9468f33;

                            for (int li = 0; li < i_len; li++)
                            {
                                byte b_byte = (byte) _inputstream.read();
                                p_daos.writeByte(b_byte);
                                i_crc = crc32(i_crc, b_byte);
                            }
                        }
                        ;
                        break;
                }
                p_daos.writeInt(i_crc ^ -1);
		if(i_chunk == HEADER_IHDR) p_daos.write(PNG_COMMON_PALETTE);

            }

            p_daos.writeLong(0x0000000049454E44);
            p_daos.writeInt(0xAE426082);
            p_daos.flush();
            p_daos = null;
/*
            byte[] ab_bufferpngarray = p_baos.toByteArray();
            p_baos = null;

            Image p_newimage = Image.createImage(ab_bufferpngarray, 0, len);
            ab_bufferpngarray = null;
            return p_newimage;
*/	    return p_baos.toByteArray();
        }

private static final byte [] PNG_COMMON_PALETTE = new byte []
{(byte)0x0,(byte)0x0,(byte)0x0,(byte)0xc0,(byte)0x50,(byte)0x4c,(byte)0x54,(byte)0x45,(byte)0x0,(byte)0xff,(byte)0x0,(byte)0xed,(byte)0x1c,(byte)0x24,(byte)0xec,(byte)0x0,(byte)0x8c,(byte)0x40,(byte)0x0,(byte)0x40,(byte)0x80,(byte)0x0,(byte)0x80,(byte)0xff,(byte)0x0,(byte)0xff,(byte)0xd,(byte)0x0,(byte)0x4c,(byte)0x0,(byte)0x0,(byte)0xa0,(byte)0x80,(byte)0x0,(byte)0xff,(byte)0x0,(byte)0x0,(byte)0xa0,(byte)0x80,(byte)0x80,(byte)0xff,(byte)0x0,(byte)0x0,(byte)0xff,(byte)0x80,(byte)0xff,(byte)0xff,(byte)0x0,(byte)0xff,(byte)0xff,(byte)0x1c,(byte)0xbb,(byte)0xb4,(byte)0x6c,(byte)0xd9,(byte)0xd9,(byte)0x0,(byte)0x74,(byte)0x6b,(byte)0x0,(byte)0x59,(byte)0x52,(byte)0x0,(byte)0x80,(byte)0x40,(byte)0x0,(byte)0x58,(byte)0x26,(byte)0x0,(byte)0x5e,(byte)0x20,(byte)0x80,(byte)0xff,(byte)0x80,(byte)0x0,(byte)0xff,(byte)0x40,(byte)0x59,(byte)0x85,(byte)0x27,(byte)0xff,(byte)0xff,(byte)0x80,(byte)0xff,(byte)0xff,(byte)0x0,(byte)0x82,(byte)0x7b,(byte)0x0,(byte)0x0,(byte)0x80,(byte)0xff,(byte)0xfc,(byte)0xa4,(byte)0x43,(byte)0xa3,(byte)0x62,(byte)0x9,(byte)0xf7,(byte)0x94,(byte)0x1d,(byte)0xc7,(byte)0xb2,(byte)0x99,(byte)0xfd,(byte)0xc6,(byte)0x89,(byte)0xc6,(byte)0x9c,(byte)0x6d,(byte)0x80,(byte)0x40,(byte)0x0,(byte)0x75,(byte)0x4c,(byte)0x24,(byte)0x8c,(byte)0x62,(byte)0x39,(byte)0xa6,(byte)0x7c,(byte)0x52,(byte)0x99,(byte)0x86,(byte)0x75,(byte)0x73,(byte)0x63,(byte)0x57,(byte)0xa0,(byte)0x41,(byte)0xd,(byte)0xf9,(byte)0xad,(byte)0x81,(byte)0xf6,(byte)0x8e,(byte)0x56,(byte)0x53,(byte)0x47,(byte)0x41,(byte)0xff,(byte)0x80,(byte)0x0,(byte)0xf6,(byte)0x96,(byte)0x79,(byte)0x36,(byte)0x2f,(byte)0x2d,(byte)0xff,(byte)0x80,(byte)0x40,(byte)0xff,(byte)0x0,(byte)0x0,(byte)0x9e,(byte)0xb,(byte)0xe,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xda,(byte)0xda,(byte)0xda,(byte)0xa7,(byte)0xa7,(byte)0xa7,(byte)0x81,(byte)0x81,(byte)0x81,(byte)0x73,(byte)0x73,(byte)0x73,(byte)0x66,(byte)0x66,(byte)0x66,(byte)0x3d,(byte)0x3d,(byte)0x3d,(byte)0x13,(byte)0x13,(byte)0x13,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0xcc,(byte)0x3a,(byte)0x5d,(byte)0x92};


//  private static final int IMG_MENUICO = 147; //menuico.png
  private /*static final*/ long [] storage = {
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
               Image ret = Image.createImage(img,0,img.length);
	       img = null;
	       Runtime.getRuntime().gc();
	       return ret;
	   } catch (Exception e) {return null;}
	 }


    public Sound[] LoadSound()
    {
        Runtime.getRuntime().gc();
	Sound []ret = null;
        try
        {
           DataInputStream ds = new DataInputStream(getClass().getResourceAsStream("/res/sound.bin"));
           // Reading of image number
	   ret = new Sound[ds.readUnsignedByte()];
	   for(int i =0;i<ret.length;i++)
	   {
		int i_type = ds.readUnsignedByte();
                    int i_size = ds.readUnsignedByte();
                    if ((i_size & 0x80)!=0) i_size = ((i_size & 0x7F)<< 8) | ds.readUnsignedByte();
                    byte []ab_arr = new byte[i_size];
                    ds.readFully(ab_arr);
	        if (i_type==0/*OTT part type of package*/){
		  ret[i] = new Sound(ab_arr,Sound.FORMAT_TONE);
	        }
	   }
        } catch(Exception e) {ret = null;}
	if(ret!=null && ret.length==0)ret = null;
	return ret;
    }


}

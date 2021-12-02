package com.raydac_research.RRGImagePacker.Packers.Png;

import java.util.zip.CRC32;
import java.util.*;
import java.util.List;
import java.io.*;
import java.awt.*;

public class Palette
{
    // class AlphaComparator
    //
    // Purpose: Compares color objects by their alpha components so that
    //		colors with smaller alpha come first.
    //
    private static class AlphaComparator implements Comparator
    {
        //
        // compare()
        //
        public int compare(Object object1, Object object2)
        {
            int alpha1 = ((Integer) object1).intValue() >>> 24;
            int alpha2 = ((Integer) object2).intValue() >>> 24;

            if (alpha1 < alpha2)
            {
                return -1;
            }
            else if (alpha1 > alpha2)
            {
                return 1;
            }
            else
            {
                return 0;
            }  // end if/else
        }  // end compare()

    }  // end class AlphaComparator

    // The array of colors, sorted so that colors with transparency come
    // first.
    protected Integer[] colors;

    // A color -> palette index mapping
    // (java.awt.Color -> java.lang.Integer)
    private Map indexes;

    // The array of alpha components for the non-fully-opaque colors.  This
    // array is only as big as the number of such colors (which is why the
    // colors array is sorted with transparency first).
    protected  byte[] alphas;

    // Flag of useness of this palette
    // it is need to reduce amounts of palettes in set
    protected boolean wasUsed = false;

    private void setColors(Set _colors, boolean _fromfile)
    {
        this.colors = new Integer[_colors.size()];

        Iterator p_iter = _colors.iterator();
        int i_indx = 0;
        while (p_iter.hasNext())
        {
            Integer p_rgb = (Integer) p_iter.next();
            this.colors[i_indx++] = p_rgb;
        }
        setColors(_fromfile);
    }

    private void setColors(Vector _colors, boolean _fromfile)
    {
        this.colors = new Integer[_colors.size()];

        Iterator p_iter = _colors.iterator();
        int i_indx = 0;
        while (p_iter.hasNext())
        {
            Integer p_rgb = (Integer) p_iter.next();
            this.colors[i_indx++] = p_rgb;
        }
        setColors(_fromfile);
    }

    protected void resetTransparents()
    {
        for (int li = 0; li < alphas.length; li++)
        {
            alphas[li] = (byte) 0xFF;
        }
    }

    private void setColors(boolean _fromfile)
    {
        // Sort the colors array so that transparent colors come first
        this.indexes = new HashMap();

        if (!_fromfile)
        {
            Arrays.sort(this.colors, new AlphaComparator());
            List alphas = new Vector(this.colors.length);

            // This loop is doing two things at once.  The first thing it's
            // doing is putting the colors and their indexes into the
            // color->index mapping.  The second thing it's doing is storing
            // the colors' alpha components, if they have transparency, into the
            // alphas list.

            for (int i = 0; i < this.colors.length; ++i)
            {
                indexes.put(this.colors[i], new Integer(i));

                if ((this.colors[i].intValue() >>> 24) < 255)
                {
                    alphas.add(new Byte((byte) (this.colors[i].intValue() >>> 24)));
                }  // end if
            }  // end for

            // Convert the alphas list to an array of primitive bytes.
            this.alphas = new byte[alphas.size()];
            for (int i = 0; i < this.alphas.length; ++i)
            {
                this.alphas[i] = ((Byte) alphas.get(i)).byteValue();
            }  // end for
        }
        else
        {
            for (int i = 0; i < this.colors.length; i++)
            {
                indexes.put(this.colors[i], new Integer(i));
            }  // end for

            alphas = new byte[colors.length];
            resetTransparents();
        }
    }

    private int processColor(int _value)
    {
//        System.out.println(Integer.toHexString(_value));
        int i_l = _value & 0xFF;
        //int i_h = (_value>>>8) & 0xFF;
        return i_l;
    }

    public void savePaletteToFile(String _file,int _mask) throws IOException
    {
        FileOutputStream p_fos = new FileOutputStream(_file);
        DataOutputStream p_dos = new DataOutputStream(p_fos);

        // Marker
        p_dos.writeShort(1);
        // the number of colours
        p_dos.writeShort(this.getSize()-1);

        // Writing color values
        for(int li=0;li<this.getSize();li++)
        {
            // Writing the colorspace RGB
            p_dos.writeShort(0);

            // Writing RGB values
            int i_rgb = this.getColorAt(li).intValue() & 0xFFFFFF;
            int i_r = i_rgb >>> 16;
            int i_g = (i_rgb >>> 8) & 0xFF;
            int i_b = i_rgb & 0xFF;

            p_dos.writeByte(i_r & _mask);
            p_dos.writeByte(i_r & _mask);

            p_dos.writeByte(i_g & _mask);
            p_dos.writeByte(i_g & _mask);

            p_dos.writeByte(i_b & _mask);
            p_dos.writeByte(i_b & _mask);

            p_dos.writeShort(0);
        }

        p_dos.flush();
        p_dos.close();
    }

    /**
     * This constructor creates a palette from Adobe Photoshop ACO file
     * @param _acofilename the name of an ACO palette file
     *  @throws IOException the expetion is throwed if we have any problem in the load time
     */
    public Palette(String _acofilename, boolean lg_verbose) throws IOException
    {
        DataInputStream p_dis = null;
        Vector p_set = null;
        try
        {

            p_dis = new DataInputStream(new FileInputStream(_acofilename));
            int i_colornumber;
            int i_transparent;
            p_set = new Vector();

            // trying to recognize ACT palette file, we need an
            if (_acofilename.toLowerCase().endsWith(".act") && (new File(_acofilename)).length()==0x304){
                p_dis.skip(0x300);
                i_colornumber = p_dis.readUnsignedShort();
                i_transparent = p_dis.readUnsignedShort();
                p_dis.close();
                p_dis = null;
                if(i_colornumber>0x100 || i_colornumber==0) throw new IOException("Incorrect value of palete size");

                p_dis = new DataInputStream(new FileInputStream(_acofilename));

                for(int i=0;i<i_colornumber;i++)
                {
                   int i_r = p_dis.readUnsignedByte();
                   int i_g = p_dis.readUnsignedByte();
                   int i_b = p_dis.readUnsignedByte();
                   int i_rgb = 0xFF000000 | (i_r << 16) | (i_g << 8) | i_b;

                   if(lg_verbose) System.out.println(i + " : " + Integer.toHexString(i_rgb)
                                                     +(i == i_transparent?" [transparent]":""));

                   if(i == i_transparent)
                      p_set.insertElementAt(new Integer(i_rgb/*&0xffffff*/),0);
                     else
                        p_set.add(new Integer(i_rgb));
                }
            }
              else
              {
                if (p_dis.readUnsignedShort() != 1) throw new IOException("Unknown format of given palette. Palette should be ACO or ACT file.");

                i_colornumber = p_dis.readUnsignedShort();

                for (int li = 0; li < i_colornumber; li++)
                {
                     int i_readcolorspace = p_dis.readUnsignedShort();
                     if (i_readcolorspace != 0) throw new IOException("The palette file contains a color with an unsupported color scheme, only the RGB scheme is supported by the encoder version [" + i_readcolorspace + "]");

                     int i_r = processColor(p_dis.readUnsignedShort());
                     int i_g = processColor(p_dis.readUnsignedShort());
                     int i_b = processColor(p_dis.readUnsignedShort());

//                   long l_rgb = ((long)i_r<<32) | ((long)i_g<<16) | (long)i_b;

                     p_dis.readUnsignedShort();

                     int i_rgb = 0xFF000000 | (i_r << 16) | (i_g << 8) | i_b;

                     if(lg_verbose) System.out.println(li + " : " + Integer.toHexString(i_rgb));

                     p_set.add(new Integer(i_rgb));
                }
              }
        }
        catch (Exception p_ex)
        {
            throw new IOException(p_ex.getMessage());
        }
        finally
        {
            try
            {
                if (p_dis!=null) p_dis.close();
            }
            catch (IOException e)
            {
            }
        }
        if(lg_verbose) System.out.println("The palette has " + p_set.size() + " colours...");
        setColors(p_set, true);
    }

    public Palette(Image _image)
    {
        

    }

    public Palette(Set _colors)
    {
        setColors(_colors, false);
    }

    //
    // getSize()
    //
    public int getSize()
    {
        return colors.length;
    }  // end getSize()

    //
    // getColorAt()
    //
    public Integer getColorAt(int index)
    {
        return colors[index];
    }  // end getColorAt()

    //
    // getColorIndex()
    //
    public int getColorIndex(int color)
    {
        Integer p_color = new Integer(color);
        Integer p_index = (Integer) indexes.get(p_color);

        if (p_index == null)
        {
            p_color = getEquColor(color);
            p_index = (Integer) indexes.get(p_color);
        }

        return p_index.intValue();
    }  // end getColorIndex()

    public Integer getEquColor(int color)
    {
        //fi = 30*(Ri-R0)^2+59*(Gi-G0)^2+11*(Bi-B0)^2;

        int i_r = (color >>> 16) & 0xFF;
        int i_g = (color >>> 8) & 0xFF;
        int i_b = color & 0xFF;

        int i_indx = -1;
        int i_diff = 0x7FFFFFFF;

        for (int li = 0; li < colors.length; li++)
        {
            int i_rgb = colors[li].intValue();
            int i_dr = (i_rgb >>> 16) & 0xFF;
            int i_dg = (i_rgb >>> 8) & 0xFF;
            int i_db = i_rgb & 0xFF;

            int i_v = Math.abs(30*((i_dr-i_r)*(i_dr-i_r))+59*((i_dg-i_g)*(i_dg-i_g))+11*((i_db-i_b)*(i_db-i_b)));

            if (i_v < i_diff)
            {
                i_diff = i_v;
                i_indx = li;
            }
        }

        if (i_indx < 0)
            return null;
        else
            return colors[i_indx];
    }

    public boolean containsColor(int color)
    {
        Integer p_color = new Integer(color);
        if (indexes.containsKey(p_color)) return true;
        p_color = new Integer(color);
        if (indexes.containsKey(p_color)) return true;

        if (getEquColor(color) != null) return true;
        return false;
    }

    //
    // getAlphas()
    //
    public byte[] getAlphas()
    {
        return alphas;
    }  // end getAlphas()

    public byte[] convertPalette2PNGchunk()
    {
      // Writing the palette in the PNG format
      byte[] ab_palettearray = new byte[colors.length * 3 + 12];
      int i_len = colors.length * 3;
      ab_palettearray[0] = (byte) ((i_len >>> 24) & 0xFF);
      ab_palettearray[1] = (byte) ((i_len >>> 16) & 0xFF);
      ab_palettearray[2] = (byte) ((i_len >>> 8) & 0xFF);
      ab_palettearray[3] = (byte) (i_len & 0xFF);

      ab_palettearray[4] = (byte) 'P';
      ab_palettearray[5] = (byte) 'L';
      ab_palettearray[6] = (byte) 'T';
      ab_palettearray[7] = (byte) 'E';

      int i_palindx = 8;
      for (int li = 0; li < colors.length; li++)
      {
          int i_rgb = colors[li].intValue();
          byte i_r = (byte) ((i_rgb >>> 16) & 0xFF);
          byte i_g = (byte) ((i_rgb >>> 8) & 0xFF);
          byte i_b = (byte) (i_rgb & 0xFF);

          ab_palettearray[i_palindx++] = i_r;
          ab_palettearray[i_palindx++] = i_g;
          ab_palettearray[i_palindx++] = i_b;
      }

      CRC32 p_crc32gen = new CRC32();
      p_crc32gen.reset();
      p_crc32gen.update(ab_palettearray, 4, ab_palettearray.length - 8);

      i_len = (int) p_crc32gen.getValue();
      ab_palettearray[i_palindx++] = (byte) ((i_len >>> 24) & 0xFF);
      ab_palettearray[i_palindx++] = (byte) ((i_len >>> 16) & 0xFF);
      ab_palettearray[i_palindx++] = (byte) ((i_len >>> 8) & 0xFF);
      ab_palettearray[i_palindx++] = (byte) (i_len & 0xFF);
      return ab_palettearray;
    }

    public String toString()
    {
                Integer[] ap_colors = colors;
                StringBuffer p_buf = new StringBuffer();
                for (int li = 0; li < ap_colors.length; li++)
                {
                    int i_rgb = ap_colors[li].intValue();
                    int i_r = (i_rgb >>> 16) & 0xFF;
                    int i_g = (i_rgb >>> 8) & 0xFF;
                    int i_b = i_rgb & 0xFF;

                    if (p_buf.length() != 0) p_buf.append(",\r\n");
                    p_buf.append("  (byte)0x");
                    p_buf.append(Integer.toHexString(i_r));
                    p_buf.append(",(byte)0x");
                    p_buf.append(Integer.toHexString(i_g));
                    p_buf.append(",(byte)0x");
                    p_buf.append(Integer.toHexString(i_b));
                }
                return p_buf.toString();
    }

}

package com.igormaznitsa.DynamicPNGCompressor;

import java.util.*;
import java.io.*;

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
    public Palette(String _acofilename) throws IOException
    {
        DataInputStream p_dis = null;
        Vector p_set = null;
        try
        {
            p_dis = new DataInputStream(new FileInputStream(_acofilename));
            if (p_dis.readUnsignedShort() != 1) throw new IOException("It isn't an ACO file or an unsupported format");

            int i_colornumber = p_dis.readUnsignedShort();

            p_set = new Vector();
            for (int li = 0; li < i_colornumber; li++)
            {
                int i_readcolorspace = p_dis.readUnsignedShort();
                if (i_readcolorspace != 0) throw new IOException("The palette file contains a color with an unsupported color scheme, only the RGB scheme is supported by the encoder version [" + i_readcolorspace + "]");

                int i_r = processColor(p_dis.readUnsignedShort());
                int i_g = processColor(p_dis.readUnsignedShort());
                int i_b = processColor(p_dis.readUnsignedShort());

//                long l_rgb = ((long)i_r<<32) | ((long)i_g<<16) | (long)i_b;

                p_dis.readUnsignedShort();

                int i_rgb = 0xFF000000 | (i_r << 16) | (i_g << 8) | i_b;

                System.out.println(li + " : " + Integer.toHexString(i_rgb));

                p_set.add(new Integer(i_rgb));
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
        System.out.println("The palette has got " + p_set.size() + " colours...");
        setColors(p_set, true);
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

        int i_r = color >>> 16;
        int i_g = (color >>> 8) & 0xFF;
        int i_b = color & 0xFF;

        int i_indx = -1;
        int i_diff = 0x7FFFFFFF;

        for (int li = 0; li < colors.length; li++)
        {
            int i_rgb = colors[li].intValue();
            int i_dr = i_rgb >>> 16;
            int i_dg = (i_rgb >>> 8) & 0xFF;
            int i_db = i_rgb & 0xFF;

            int i_v = Math.abs(30*(i_dr-i_r)^2+59*(i_dg-i_g)^2+11*(i_db-i_b)^2);

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

}

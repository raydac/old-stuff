package com.igormaznitsa.DynamicPNGCompressor;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.PixelGrabber;

public class ImagePacker
{
    private static final int TYPE_D8BPP = 4;

    protected static MediaTracker p_mtracker = new MediaTracker(new Button());
    private static String s_descriptorname = "index.lst";

    private static void outFirstStroke()
    {
        System.out.println("The DynamycImagePacker utility\r\nv2.01 (09-May-2003)\r\n(C) 2003 All Copyright by United Computer Arts Ltd.");
    }

    private static void outHelp()
    {
        outFirstStroke();
        System.out.println("\r\nCommand string:");
        System.out.println("com.igormaznitsa.MIDPTools.ImagePacker.ImagePacker [/M:model] [/C:compressionlevel] [/A] [/D:directory_name] [/I] [/L:image_list] [package_name]");
        System.out.println("/I - create descriptor for the directory with name \"index.lst\"");
        System.out.println("/L:image_list - to define an image list file (default \"index.lst\")");
        System.out.println("/H - help");
        System.out.println("/C:0..3 - default compression level for picture packing");
        System.out.println("/P:palette_file_name - the name of a palette for compressed images,");
        System.out.println("/N:name - to define a descriptor file");
    }

    // Comp level can be 0,1,2,3
    // 0 - not compressed
    // 1 - RLE compressed
    // 2 - full screen without compression (with filter values)
    // 3 - RLE compressed full screen (with filter values)
    private static void packingForD8BPP(byte[] _inarray, DataOutputStream _outputstream, Palette _palette, int _complevel) throws IOException
    {
        if (_palette == null) throw new IOException("The palette is not defined");

        Image p_img = Toolkit.getDefaultToolkit().createImage(_inarray);
        p_mtracker.addImage(p_img, 0);

        try
        {
            p_mtracker.waitForAll();
        }
        catch (InterruptedException e)
        {
            return;
        }

        p_mtracker.removeImage(p_img);

        int i_width = p_img.getWidth(null);
        int i_height = p_img.getHeight(null);

        int[] ai_grabarray = new int[i_width * i_height];
        PixelGrabber p_pixgrab = new PixelGrabber(p_img, 0, 0, i_width, i_height, ai_grabarray, 0, i_width);

        try
        {
            p_pixgrab.grabPixels();
        }
        catch (InterruptedException e)
        {
            return;
        }

        ByteArrayOutputStream p_rast = new ByteArrayOutputStream(i_width * i_height + 2);
        Integer p_alphacolor = null;

        for (int ly = 0; ly < i_height; ly++)
        {
            for (int lx = 0; lx < i_width; lx++)
            {
                int i_argb = ai_grabarray[lx + ly * i_width];
                int i_a = (i_argb >>> 24) & 0xFF;

                int i_rgb = i_argb & 0xFFFFFF;// (i_r << 16)|(i_g<<8)|i_b;

                if (!_palette.containsColor(i_rgb | 0xFF000000))
                {
                    Integer p_equcolor = _palette.getEquColor(i_rgb);
                    if (p_equcolor == null) throw new IOException("Undefined color in the image [" + lx + "," + ly + ",0x" + Integer.toHexString(i_rgb) + "] and I can't find an equivalent color");
//                    System.out.println("WARNING: Color 0x"+Integer.toHexString(i_rgb)+" will be changed to 0x"+Integer.toHexString(p_equcolor.intValue()));
                    int i_newvalue = (i_argb & 0xFF000000) | p_equcolor.intValue();
                    ai_grabarray[lx + ly * i_width] = i_newvalue;
                    i_rgb = i_newvalue & 0xFFFFFF;
                }

                if (i_a != 0xFF)
                {
                    if (p_alphacolor == null)
                    {
                        p_alphacolor = new Integer(i_rgb);
                    }
                    else
                    {
                        if (p_alphacolor.intValue() != i_rgb)
                            throw new IOException("There are too many transparent colors in the image");
                    }

                    int i_indx = _palette.getColorIndex(i_rgb);
                    p_rast.write(i_indx);
                }
                else
                {
                    int i_indx = _palette.getColorIndex(i_rgb | 0xFF000000);
                    p_rast.write(i_indx);
                }
            }
        }

        if (p_alphacolor != null)
        {
            int i_indx = _palette.getColorIndex(p_alphacolor.intValue());
            _outputstream.writeByte(1 | (_complevel << 4));
            _outputstream.writeByte(i_indx);
        }
        else
        {
            _outputstream.writeByte((_complevel << 4));
        }
        _outputstream.writeByte(i_width);
        _outputstream.writeByte(i_height);

        byte[] ab_allimage = p_rast.toByteArray();
        switch (_complevel)
        {
            case 0:
                {
                // Without any compression
                _outputstream.write(ab_allimage);
                _outputstream.flush();
                };
                break;
            case 1:
                {
                    // RLE compression
                    byte[] ab_line = new byte[i_width];
                    int i_indx = 0;
                    while (i_indx < ab_allimage.length)
                    {
                        System.arraycopy(ab_allimage, i_indx, ab_line, 0, i_width);
                        byte[] ab_rlearray = RLEcompress(ab_line);
                        _outputstream.write(ab_rlearray);
                        i_indx += i_width;
                    }
                }
                ;
                break;
            case 2:
                {
                    // Full screen with filters without any compression
                    byte[] ab_line = new byte[i_width + 1];
                    int i_indx = 0;
                    while (i_indx < ab_allimage.length)
                    {
                        System.arraycopy(ab_allimage, i_indx, ab_line, 1, i_width);
                        ab_line[0] = 0;
                        _outputstream.write(ab_line);
                        i_indx += i_width;
                    }
                }
                ;
                break;
            case 3:
                {
                    // RLE compressed full screen
                    int i_indx = 0;
                    ByteArrayOutputStream p_baos = new ByteArrayOutputStream((i_width + 1) * i_height);
                    byte[] ab_line = new byte[i_width + 1];

                    while (i_indx < ab_allimage.length)
                    {
                        System.arraycopy(ab_allimage, i_indx, ab_line, 1, i_width);
                        ab_line[0] = 0;
                        p_baos.write(ab_line);
                        i_indx += i_width;
                    }

                    ab_line = RLEcompress(p_baos.toByteArray());
                    _outputstream.write(ab_line);
                }
                ;
                break;
            default :
                {
                    throw new IOException("Error compression level");
                }
        }
        _outputstream.flush();
    }

    public static final byte[] RLEcompress(byte[] inarray)
    {
        System.gc();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(inarray.length);

        int inindx = 0;
        while (inindx < inarray.length)
        {
            int value = inarray[inindx++] & 0xFF;
            int count = 1;
            while (inindx < inarray.length)
            {
                if ((inarray[inindx] & 0xFF) == value)
                {
                    count++;
                    inindx++;
                    if (count == 63) break;
                }
                else
                    break;
            }
            if (count > 1)
            {
                baos.write(count | 0xC0);
                baos.write(value);
            }
            else
            {
                if (value > 63)
                {
                    baos.write(0xC1);
                    baos.write(value);
                }
                else
                    baos.write(value);
            }
        }

        byte[] outarray = baos.toByteArray();
        baos = null;
        System.gc();
        return outarray;
    }

    public static void main(String[] args)
    {
        String s_directory = ".\\";
        String s_javaname = "images.java";

        String[] s_extension = new String[]{".gif"};

        String s_palettename = null;
        Palette p_palette = null;

        int i_compressionlevel = -1;

        int i_type = TYPE_D8BPP;
        boolean lg_help = false;

        String s_packagename = null;
        String s_newpackagename = null;

        boolean lg_tocreatedescriptor = false;

        for (int li = 0; li < args.length; li++)
        {
            String s_buffer = args[li].toLowerCase();
            if (s_buffer.equals("/?"))
            {
                outHelp();
                System.exit(0);
            }
            else if (s_buffer.equals("/i"))
            {
                lg_tocreatedescriptor = true;
            }
            else if (s_buffer.equals("/l:"))
            {
                s_descriptorname = s_buffer.substring(3);
            }
            else if (s_buffer.startsWith("/c:"))
            {
                if (s_buffer.endsWith("0"))
                {
                    i_compressionlevel = 0;
                }
                else if (s_buffer.endsWith("1"))
                {
                    i_compressionlevel = 1;
                }
                else if (s_buffer.endsWith("2"))
                {
                    i_compressionlevel = 2;
                }
                else if (s_buffer.endsWith("3"))
                {
                    i_compressionlevel = 3;
                }
                else
                {
                    outHelp();
                    System.exit(1);
                }
            }
            else if (s_buffer.startsWith("/n:"))
            {
                ImagePacker.s_descriptorname = args[li].substring(3);
            }
            else if (s_buffer.startsWith("/p:"))
            {
                s_palettename = args[li].substring(3);
            }
            else if (s_buffer.startsWith("/d:"))
            {
                s_directory = args[li].substring(3);
            }
            else if (s_buffer.startsWith("/h"))
            {
                lg_help = true;
            }
            else
            {
                s_newpackagename = args[li];
            }
        }

        if (lg_help) outHelp();

        outFirstStroke();
        System.out.print("Mode : ");
        switch (i_type)
        {
            case TYPE_D8BPP:
                System.out.println("DYNAMIC 8BPP");
                break;
        }

        if (s_newpackagename == null)
            s_packagename = "set_" + i_type + ".bin";
        else
            s_packagename = s_newpackagename;

        System.out.println("----------------");

        try
        {
            File p_file = new File(s_directory);
            File[] ap_files = p_file.listFiles();

            int i_counter = 0;
            TreeSet p_fileset = new TreeSet(new Comparator()
            {
                public int compare(Object o1, Object o2)
                {
                    File p_file1 = ((FileDescriptorInfo) o1).p_file;
                    File p_file2 = ((FileDescriptorInfo) o2).p_file;

                    if (((FileDescriptorInfo) o1).i_linkindex >= 0 || ((FileDescriptorInfo) o2).i_linkindex < 0) return 1;
                    if (((FileDescriptorInfo) o1).i_linkindex < 0 || ((FileDescriptorInfo) o2).i_linkindex >= 0) return -1;
                    if (p_file1.length() <= p_file2.length()) return 1; else return -1;
                }
            });

            for (int li = 0; li < ap_files.length; li++)
            {
                p_file = ap_files[li];
                if (!p_file.isFile()) continue;
                String s_name = p_file.getName().toLowerCase();
                for (int lex = 0; lex < s_extension.length; lex++)
                    if (s_name.endsWith(s_extension[lex]))
                    {
                        i_counter++;
                        break;
                    }
            }

            FileOutputStream p_fos = null;
            DataOutputStream p_dos = null;

            if (!lg_tocreatedescriptor)
            {
                p_fos = new FileOutputStream(s_packagename);
                p_dos = new DataOutputStream(p_fos);
            }

            String[] s_extension_up = new String[s_extension.length];
            for (int li = 0; li < s_extension.length; li++) s_extension_up[li] = s_extension[li].toUpperCase();


            p_fileset.clear();
            BufferedWriter p_descrwriter = null;

            // Loading a palette file if it is necessary
            if (s_palettename != null && !lg_tocreatedescriptor)
            {
                System.out.println("Loading the palette from " + s_palettename + "...");
                try
                {
                    p_palette = new Palette(s_palettename);
                }
                catch (IOException e)
                {
                    System.err.println("ERROR: I can't download the palette [" + e.getMessage() + "]");
                    System.exit(1);
                }
            }
            else
            if (s_palettename == null && !lg_tocreatedescriptor)
            {
                System.err.println("ERROR: You must define a palette");
                System.exit(1);
            }

            //int i_summaryfiles = i_counter;

            // Processing of a descriptor file
            if (!lg_tocreatedescriptor)
            {
                BufferedReader p_dis = null;
                try
                {
                    p_dis = new BufferedReader(new InputStreamReader(new FileInputStream(s_directory + "/" + s_descriptorname)));

                    System.out.println("Descriptior file is found...");

                    String s_bstr = null;
                    i_counter = 0;
                    while (true)
                    {
                        s_bstr = p_dis.readLine();
                        if (s_bstr == null) break;

                        s_bstr = s_bstr.trim();
                        if (s_bstr.length() == 0) continue;

                        if (s_bstr.startsWith("#")) continue;

                        StringTokenizer p_tokenizer = new StringTokenizer(s_bstr, ",");

                        String s_imgname = null;
                        String s_number = null;
                        boolean lg_commented = false;

                        try
                        {
                            s_imgname = p_tokenizer.nextToken().trim().toLowerCase();
                            s_number = p_tokenizer.nextToken().trim();
                            if (s_imgname.startsWith("$"))
                            {
                                lg_commented = true;
                                s_imgname = s_imgname.substring(1);
                            }
                        }
                        catch (NoSuchElementException e)
                        {
                            throw new IOException("Error string in descriptor [" + s_bstr + "]");
                        }

                        String s_packlevel = null;
                        if (p_tokenizer.hasMoreTokens())
                        {
                            s_packlevel = p_tokenizer.nextToken();
                            if (s_packlevel.trim().length() == 0)
                            {
                                if (i_compressionlevel>=0)
                                s_packlevel = ""+i_compressionlevel;
                                else
                                s_packlevel = "-1";
                            }
                        }

                        String s_linknumber = null;

                        if (p_tokenizer.hasMoreTokens())
                        {
                            s_linknumber = p_tokenizer.nextToken();
                        }

                        String s_outname = s_imgname.toUpperCase() + s_extension_up;

                        Iterator p_iter = p_fileset.iterator();
                        while (p_iter.hasNext())
                        {
                            FileDescriptorInfo p_cinfo = (FileDescriptorInfo) p_iter.next();
                            if (p_cinfo.s_filename.equals(s_imgname)) throw new IOException("Duplicated image name [" + s_outname + "]");
                        }

                        // Getting the File object for the name
                        p_file = null;

                        if (s_linknumber == null)
                        {
                            for (int li = 0; li < ap_files.length; li++)
                            {
                                for (int lex = 0; lex < s_extension.length; lex++)
                                {
                                    if (ap_files[li].getName().equals(s_imgname + s_extension[lex]))
                                    {
                                        p_file = ap_files[li];
                                        break;
                                    }
                                }
                                if (p_file != null) break;
                            }

                            if (p_file == null) throw new IOException("Unknown image name in the descriptor [" + s_imgname + "]");
                        }
                        try
                        {
                            int i_num = Integer.parseInt(s_number);

                            int i_compresslevel = -1;
                            int i_linknumber = -1;

                            if (s_packlevel != null) i_compresslevel = Integer.parseInt(s_packlevel);
                            if (s_linknumber != null) i_linknumber = Integer.parseInt(s_linknumber);


                            if (i_compresslevel < (-1) || i_compresslevel > 3) throw new IOException("Bad compression level for the image in the descriptor file [" + s_bstr + "]");
                            if (i_linknumber < -1 || i_linknumber > 255) throw new NumberFormatException();
                            if (i_num < 0 || i_num > 255) throw new NumberFormatException();

                            Iterator p_diter = p_fileset.iterator();
                            while (p_diter.hasNext())
                            {
                                FileDescriptorInfo p_info = (FileDescriptorInfo) p_diter.next();
                                if (p_info.i_fileindex == i_num && !(p_info.lg_commentedFile || lg_commented))
                                {
                                    if (p_info.p_file != null) throw new IOException("Duplicated index in descriptor [" + i_num + "]");
                                }
                            }

                            FileDescriptorInfo p_dinfo = new FileDescriptorInfo(s_imgname, i_num, i_compresslevel, i_linknumber, lg_commented);
                            if (i_linknumber < 0) p_dinfo.p_file = p_file;
                            p_fileset.add(p_dinfo);
                            if (!lg_commented) i_counter++;
                        }
                        catch (NumberFormatException e)
                        {
                            throw new IOException("Error number format in descriptor [" + s_bstr + "]");
                        }
                    }
                    p_dis.close();
                }
                catch (FileNotFoundException e)
                {
                    System.out.println("(!)Descriptor file is not found");

                    p_fileset.clear();
                    int i_index = 0;
                    for (int li = 0; li < ap_files.length; li++)
                    {
                        p_file = ap_files[li];
                        String s_filenameext = p_file.getName().toLowerCase();
                        boolean lg_present = true;
                        for (int lex = 0; lex < s_extension.length; lex++)
                        {
                            if (s_filenameext.endsWith(s_extension[lex]))
                            {
                                lg_present = false;
                                s_filenameext = s_extension[lex];
                                break;
                            }
                        }
                        if (lg_present) continue;

                        String s_imgname = p_file.getName().toLowerCase();
                        s_imgname = s_imgname.substring(0, s_imgname.length() - s_filenameext.length());
                        int i_packlevel = -1;
                        FileDescriptorInfo p_fdis = new FileDescriptorInfo(s_imgname, i_index++, i_packlevel, -1, false);
                        p_fdis.p_file = p_file;
                        p_fileset.add(p_fdis);
                    }
                }
            }
            else
            {
                // Creating a descriptor
                p_descrwriter = new BufferedWriter(new FileWriter(s_directory + '/' + s_descriptorname, false));

                int i_indx = 0;

                for (int li = 0; li < ap_files.length; li++)
                {
                    p_file = ap_files[li];
                    String s_filenameext = p_file.getName().trim().toLowerCase();
                    String s_ext = null;
                    boolean lg_present = true;
                    for (int lex = 0; lex < s_extension.length; lex++)
                    {
                        if (s_filenameext.endsWith(s_extension[lex]))
                        {
                            s_ext = s_extension[lex];
                            lg_present = false;
                        }
                    }
                    if (lg_present) continue;

                    String s_imgname = p_file.getName().trim().toUpperCase();
                    s_imgname = s_imgname.substring(0, s_imgname.length() - s_ext.length());

                    p_descrwriter.write(s_imgname + "," + i_indx + "\r\n");
                    i_indx++;
                }

                p_descrwriter.close();
                return;
            }

            // Writing of the number of images in the image block
            if (!lg_tocreatedescriptor && i_type != TYPE_D8BPP)
            {
                p_dos.writeByte(i_counter);
            }

            Iterator p_iterator = p_fileset.iterator();
            int i_indx = 0;

            int[] ai_offsets = new int[p_fileset.size()];

            while (p_iterator.hasNext())
            {
                FileDescriptorInfo p_dinfo = (FileDescriptorInfo) p_iterator.next();

                String s_name = null;
                String s_extensionx = null;
                if (p_dinfo.i_linkindex < 0)
                {
                    p_file = p_dinfo.p_file;
                    s_name = p_file.getName().toUpperCase();

                    s_extensionx = null;
                    for (int lex = 0; lex < s_extension_up.length; lex++)
                    {
                        if (s_name.endsWith(s_extension_up[lex]))
                        {
                            s_extensionx = s_extension_up[lex];
                            break;
                        }
                    }
                }
                else
                {
                    s_name = p_dinfo.s_filename.toUpperCase();
                    s_extensionx = "";
                }

                System.out.println("------------------------------------------");
                int i_complevel = i_compressionlevel;
                if (p_dinfo.i_compresslevel >= 0) i_complevel = p_dinfo.i_compresslevel;
                if (p_dinfo.i_linkindex >= 0 && p_dinfo.lg_commentedFile) throw new IOException("You can not have any commented image as a link image");
                if (p_dinfo.lg_commentedFile)
                {
                    System.out.println("$$$ ");
                }
                if (p_dinfo.i_linkindex >= 0)
                    System.out.println("Picture " + s_name + " : " + i_indx + " a link to image with the number " + p_dinfo.i_linkindex);
                else
                    System.out.println("Picture " + i_indx + " the file name is " + p_file.getName() + " compression level=" + i_complevel);

                byte[] ab_arr = null;
                FileInputStream p_fis = null;

                if (!p_dinfo.lg_commentedFile)
                {
                    if (p_dinfo.i_linkindex < 0)
                    {
                        s_name = s_name.substring(0, s_name.length() - s_extensionx.length());
                        s_name.replace(' ', '_');

                        ab_arr = new byte[(int) p_file.length()];
                        p_fis = new FileInputStream(p_file);
                        p_fis.read(ab_arr);
                    }
                    int i_windx = p_dinfo.i_fileindex;
                    int i_offst = p_dos.size();

                    if (i_type == TYPE_D8BPP)
                    {
                        if (p_dinfo.i_linkindex >= 0)
                        {
                            ai_offsets[i_windx] = ai_offsets[p_dinfo.i_linkindex];
                        }
                        else
                            ai_offsets[i_windx] = i_offst;
                    }
                }
                ByteArrayOutputStream p_tmparray = new ByteArrayOutputStream(1024);
                DataOutputStream p_tmpStream = new DataOutputStream(p_tmparray);

                if (p_dinfo.i_linkindex < 0)
                {
                    packingForD8BPP(ab_arr, p_tmpStream, p_palette, i_complevel);
                    p_tmpStream.flush();
                    p_tmpStream.close();
                    if (!p_dinfo.lg_commentedFile)
                    {
                        p_dos.write(p_tmparray.toByteArray());
                        p_dos.flush();
                        p_fis.close();
                    }
                }

                if (!p_dinfo.lg_commentedFile) i_indx++;
            }
            p_dos.flush();
            int i_filesize = p_dos.size();

            p_fos.flush();
            p_fos.close();
            p_dos = null;

            p_fos = new FileOutputStream(s_javaname);
            p_dos = new DataOutputStream(p_fos);

            Iterator p_enum = p_fileset.iterator();
            while (p_enum.hasNext())
            {
                FileDescriptorInfo p_di = (FileDescriptorInfo) p_enum.next();

                String s_name = p_di.s_filename;

                s_name = "IMG_" + s_name.toUpperCase();
                p_dos.writeBytes("private static final int " + s_name + " = " + p_di.i_fileindex + ";\r\n");
            }

            p_dos.writeBytes("private static final int TOTAL_IMAGES_NUMBER = " + p_fileset.size() + ";\r\n");

            if (i_type == TYPE_D8BPP)
            {
                // Writing the encoding array
                p_dos.writeBytes("\r\nprivate static final int [] DECODING_ARRAY =\r\n{\r\n");

                StringBuffer p_buf = new StringBuffer();

                for (int li = 0; li < ai_offsets.length; li++)
                {
                    if (p_buf.length() != 0) p_buf.append(",\r\n");
                    p_buf.append("  0x" + Integer.toHexString(ai_offsets[li]));
                }
                p_dos.writeBytes(p_buf.toString());
                p_dos.writeBytes("\r\n};\r\n");

                p_dos.writeBytes("private static final int IMAGE_FILE_SIZE = " + i_filesize + ";\r\n");
            }

            // Writing the palette if it is defined
            if (p_palette != null)
            {
                FileOutputStream p_fos_ = new FileOutputStream("palette.bin");
                p_fos_.write(p_palette.colors.length&0xFF);

                p_dos.writeBytes("\r\nprivate static final int PALETTE_SIZE = " + p_palette.colors.length + ";\r\n");
                p_dos.writeBytes("private static final byte [] COMMON_PALETTE = new byte[]\r\n{\r\n");

                Integer[] ap_colors = p_palette.colors;
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

                    p_fos_.write(i_r);
                    p_fos_.write(i_g);
                    p_fos_.write(i_b);
                }

                p_fos_.close();

                p_dos.writeBytes(p_buf.toString());
                p_dos.writeBytes("\r\n};\r\n");
            }

            p_dos.flush();
            p_fos.flush();
            p_dos.close();
            p_fos = null;
            p_dos = null;
        }
        catch (IOException ex)
        {
            System.err.println("IOException [" + ex + "]");
            System.exit(1);
        }
        System.exit(0);
    }
}

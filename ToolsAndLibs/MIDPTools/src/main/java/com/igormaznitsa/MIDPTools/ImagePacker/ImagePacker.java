package com.igormaznitsa.MIDPTools.ImagePacker;

//TODO Сделать значение компрессии по умолчанию

import com.igormaznitsa.Utils.WaveletCodec;

import java.io.*;
import java.util.*;
import java.util.zip.CRC32;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.PixelGrabber;

/**
 * Copyright © 2003 Igor A. Maznitsa (igor.maznitsa@igormaznitsa.com).  All rights reserved.
 * Software is confidential and copyrighted. Title to Software and all associated intellectual property rights
 * is retained by Igor A. Maznitsa. You may not make copies of Software, other than a single copy of Software for
 * archival purposes.  Unless enforcement is prohibited by applicable law, you may not modify, decompile, or reverse
 * engineer Software.  You acknowledge that Software is not designed, licensed or intended for use in the design,
 * construction, operation or maintenance of any nuclear facility. Igor A. Maznitsa disclaims any express or implied
 * warranty of fitness for such uses.
 *
 * This is an utility for packing PNG or GIF files into a bin block.
 */
public class ImagePacker extends PackerMethodsCollection
{
 class CustomFilenameFilter implements FilenameFilter
 {
       Item item;
       public CustomFilenameFilter(Item item)
       {
            this.item = item;
       }
       public boolean accept(File dir, String name)
       {
            for(int i = 0; i<s_extension.length;i++)
               if((item.filename+s_extension[i]).toLowerCase().equals(name.toLowerCase()))
                     return true;
            return false;
       }
 }
    private static final int TYPE_NONE = -1;
    private static final int TYPE_NOKIA = 0;
    private static final int TYPE_SIEMENS = 1;
    private static final int TYPE_PNG = 2;
    private static final int TYPE_PNGLITE = 3;
    private static final int TYPE_D8BPP = 4;

    private static final String[] TYPE_STRINGS = {"NOKIA","SIEMENS","PNG","PNGLITE","D8BPP"};

    public static MediaTracker p_mtracker = new MediaTracker(new Button());

    private static final int TAG_NORMAL = 0;
    private static final int TAG_LINK = 1;
    private static final int TAG_NONUNPACK = 2;
    private static final int TAG_PALETTE = 3;
    private static final int TAG_IMAGEOFFSET = 4;
    private static final int TAG_PALETTESET = 0x13;
    private static final int TAG_CHANGEPALETTE = 0x10;

    private static final String copyright   = "ImagePacker utility,  v3.00 (08-AUG-2003)\r\n(C) 2002,2003 All Copyright by Raydac Research Group,\nhttp://www.raydac-research.com";

    // script section
    private static final int SCR_DIRECTORY = 0, SCR_PALETTE = 1, SCR_SAVESTATE = 2, SCR_LOADSTATE = 3, SCR_CROP = 4;
    private static final String[] SCRIPT_COMMAND = {"set_directory_to","palette","save_state","load_state","crop"};
    private static final String[] PALETTE_COMMAND = {"load","reset","set_default"};
    private static final String[] CROP_COMMAND = {"left","right","top","bottom","store_offset","full","hcenter","vcenter","reset"};
    private final static int C_LEFT=1,C_RIGHT=2,C_TOP=4,C_DOWN=8,C_STORE=16,C_FULL=32,C_HCENTER=64,C_VCENTER=128;
    private int crop_flagset = 0;
    private int i_paletteIndex = 0;
    private Stack script_state = new Stack();
    Vector p_paletteSet = new Vector();

    private final static String optionshelp = "\r\nCommand string:\n"
             +"   com.igormaznitsa.MIDPTools.ImagePacker.ImagePacker [/M:model] [/C:compressionlevel] [/A] [/D:directory_name] [/I] [package_name]\n\n"
             +"/M:SIEMENS packing GIF images for monochrome Siemens (phones C55,M50/MT50,SL45i,wired ME45)\n"
             +"/M:NOKIA   packing GIF images for monochrome Nokia (phones 3410,6310i)\n"
             +"/M:PNG     packing GIF and JPG images into the binary block as encoded and cutted png images\n"
             +"/M:PNGLITE packing PNG images into the binary block as cutted png images\n"
             +"/M:D8BPP   packing GIF images into the binary block as dynamic 8 bits per pixel\n"
             +"/I - create descriptor for the directory with name \"index.lst\"\n"
             +"/C:0..9 - a compression level for packing of PNG pictures (only for /M:PNG mode),0 - minimal compression, 9 - maximal compression\n"
             +"/A - to create the alpha channel in encoded PNG images if it's possible (only for /M:PNG mode)\n"
             +"/P:palette_file_name - the name of a palette for compressed images,\n"
             +"/K - adapt the palette to a phone with mask in /C parameter(0-0xFF,1-0xF0)\n"
             +"/R - remove the palette data from packed images (default the palette is included)\n"
             +"/D - don't store palette in binary storage (for compatibility with early versions)\n"
             +"/N:name - to define a descriptor file\n"
             +"/O - to write the repacked PNG files into the directory\n"
             +"/E - extended index resolution (switch to 16 bit index instead 8 at normal)\n"
             +"/V - verbose mode\n"
             +"/H - this help screen\n";

    String s_directory = ".\\";
    String s_baseDirectory  = ".\\";
    String s_javaname = "images.java";
    String s_spaceline = "                                                                              ";
    String s_clearline = '\r'+s_spaceline+'\r';
    String[] s_extension = new String[]{".png"};

    int []ai_offsets = null;
    int i_outputStorageSize = 0;


    int i_names_counter = -1;
    int i_compressionlevel = -1;
    boolean lg_alphachannel = false;

    int i_type = TYPE_NONE;

    boolean lg_paletterremove = false;
    boolean lg_placeFormedPNGFiles = false;
    boolean lg_dontstorepalette = false;
    boolean lg_extendedindex = false;
    boolean lg_containsOffset = false;

    String s_packagename = null;
    boolean lg_tocreatedescriptor = false;

    void processCommandLine(String []args) throws Exception
    {
        String s_newpackagename = null;
        String s_palettename = null;
        boolean lg_convertPalette = false;

        for (int li = 0; li < args.length; li++)
        {
            String s_buffer = args[li].toLowerCase();
            if(s_buffer.length()>=2 && (s_buffer.charAt(0)=='/' || s_buffer.charAt(0)=='-'))
            {
               s_buffer = s_buffer.substring(1);
               boolean lg_param = (s_buffer.length()>=3 && s_buffer.charAt(1)==':');

               switch (s_buffer.charAt(0)) {
                 case 'h' :
                 case '?' : System.out.println(optionshelp); System.exit(0);
                 case 'i' : lg_tocreatedescriptor = true;  break;
                 case 'o' : lg_placeFormedPNGFiles = true; break;
                 case 'a' : lg_alphachannel = true;        break;
                 case 'r' : lg_paletterremove = true;      break;
                 case 'd' : {
                               if(lg_param)
                               {
                                  s_directory = args[li].substring(3).trim();
                                  if(!s_directory.endsWith(File.separator))
                                       s_directory += File.separator;
                               }
                                else
                                   lg_dontstorepalette = true;
                            } break;
                 case 'k' : lg_convertPalette = true;      break;
                 case 'c' : {
                             if (!lg_param ||
                                 (i_compressionlevel=s_buffer.charAt(2)-'0')<0 ||
                                 i_compressionlevel >9)
                                 {
                                   throw new Exception("Error: Wrong compression ratio\n\n"+ optionshelp);
                                 }
                            } break;
                 case 'n' : ImagePacker.s_descriptorname = args[li].substring(3); break;
                 case 'm' : {
                              if (s_buffer.endsWith("nokia"))
                              {
                                i_type = TYPE_NOKIA;
                                s_extension = new String[]{".gif"};
                              }
                              else if (s_buffer.endsWith("siemens"))
                              {
                                i_type = TYPE_SIEMENS;
                                s_extension = new String[]{".gif"};
                              }
                              else if (s_buffer.endsWith("pnglite"))
                              {
                                i_type = TYPE_PNGLITE;
                                s_extension = new String[]{".png"};
                              }
                              else if (s_buffer.endsWith("png"))
                              {
                                i_type = TYPE_PNG;
                                s_extension = new String[]{".gif", ".jpg"};
                              }
                              else if (s_buffer.endsWith("d8bpp"))
                              {
                                i_type = TYPE_D8BPP;
                                s_extension = new String[]{".gif", ".jpg"};
                              }
                              else
                              {
                                   throw new Exception("\nError: Unknown mode\n\n"+optionshelp);
                              }
                            } break;
                 case 'p' : if (lg_param) s_palettename = args[li].substring(3); break;
                 case 'e' : lg_extendedindex = true; break;
                 case 'v' : lg_verbose = true; break;
                   default: s_newpackagename = args[li];
               }
            }
        }

        s_baseDirectory = new String(s_directory);

        if (lg_convertPalette)
        {
            System.out.println("Converting of the palette " + s_palettename + " to " + s_palettename + ".new");
            try
            {
                Palette p_pal = new Palette(s_palettename,lg_verbose);
                int i_mask = 0;
                switch (i_compressionlevel)
                {
                    case 0:    i_mask = 0xFF;    break;
                    case 1:    i_mask = 0xF0;    break;
                    default:
                            throw new Exception("ERROR: Unsupported mask parameter for palette conversion [" + i_compressionlevel + "]");
                }
                p_pal.savePaletteToFile(s_palettename + ".new", i_mask);
            }
            catch (Exception e)
            {
                throw new Exception("Exception : " + e.getMessage());
            }
            System.exit(0);
        }

        if (s_newpackagename == null) s_packagename = "set_" + i_type + ".bin";
            else s_packagename = s_newpackagename;

        if (i_type == TYPE_NONE) System.exit(0);

        if(s_palettename!=null) loadPalette(s_palettename);

        if(lg_verbose)
        {
           System.out.println("Mode : "+TYPE_STRINGS[i_type]);
           System.out.println("----------------");
        }
    }

    public void loadPalette(String palettename) throws Exception
    {
       if (palettename != null)
       {
         if(lg_verbose)  System.out.println("Loading the palette from " + palettename + "...");
         try
         {
               Palette p = new Palette(palettename, lg_verbose);
               for(int i=p_paletteSet.size()-1;i>=0;i--)
               {
                 if(((Palette)p_paletteSet.elementAt(i)).colors.equals(p.colors))
                 {
                   if(lg_verbose)  System.out.println("palette already loaded" + palettename + "...");
                   i_paletteIndex = i;
                   return;
                 }
               }
               p_paletteSet.add(p);
               i_paletteIndex = p_paletteSet.size()-1;
         }
         catch (Exception e)
         {
               throw new Exception("ERROR: I can't download the palette [" + e.getMessage() + "]");
         }
       }
    }

    public Item[] loadIndexList(String filename) throws Exception
    {
      Item[] ret = null;
      Item item;
      FileInputStream fin;
      try
      {
        fin = new FileInputStream(filename);
        StringBuffer sb = new StringBuffer();
        Vector store = new Vector();                 // result
        Hashtable ht = new Hashtable();              // numbers table
        Hashtable nt = new Hashtable();              // names table

        int i, line = 0;
        i_names_counter=0;
        while(true){
           i=fin.read();
           if (i==10) continue;
           if(i==13 || i==-1) {
             item = Item.processString(sb.toString());
             item.source_line = line++;
             if (item.isItem){
                item.order = i_names_counter++;
                 if (nt.containsKey(item.name))
                     throw new Exception("(!) Dublicate Name Found at line:"+item.source_line+", name:"+item.name);
                nt.put(item.name,item);
                if(item.orig_number!=null)
                     ht.put(item.orig_number,item);
                store.add(item);
             }
             else
               if(item.script!=null)
                  store.add(item);
	     sb = new StringBuffer();
           } else sb.append((char)i);
           if(i==-1) break;
        }
        fin.close();

        if(nt.size()<=0) throw new Exception("Wrong amount of files:"+nt.size());
        if(nt.size()>65535) throw new Exception("Extremely large amount of files:"+nt.size());
        if(nt.size()>255) lg_extendedindex = true;

        ret = new Item[store.size()];

        // check links
        for (i = 0; i < store.size(); i++){
          item = (Item)store.get(i);
          ret[i] = item;
          if(item.link!=null){
             if(ht.containsKey(item.link)) item.link_number = ((Item)ht.get(item.link)).order;
                else
                  if(nt.containsKey(item.link)) item.link_number = ((Item)nt.get(item.link)).order;
             if(item.link_number<0)
               throw new Exception("(!) Target Not Found, see the line:"+item.source_line+", target name:"+item.link);
          }
        }
      } catch (Exception e)
      {
        throw new Exception("Exception: "+e.getClass().getName()+"\nReason: "+e.getMessage());
      }

      return ret;
    }


    public byte[] processImage(Item item) throws Exception
    {
           byte[] ab_arr = null;
           int left=0,right=0,top=0,bottom=0;

           ByteArrayOutputStream p_tmparray = new ByteArrayOutputStream(1024);
           DataOutputStream dos = new DataOutputStream(p_tmparray);

           Palette p_palette = i_paletteIndex>=0 ? (Palette)p_paletteSet.get(i_paletteIndex): null;

           int i_tag = 0;
           ByteArrayOutputStream tagbaos = new ByteArrayOutputStream();
           DataOutputStream dostag = new DataOutputStream(tagbaos);
/*
           if (i_type == TYPE_D8BPP)
           {
                 if (item.i_linkindex >= 0)
                 {
                            ai_offsets[i_windx] = ai_offsets[item.i_linkindex];
                 }
                  else
                 ai_offsets[i_windx] = i_offst;
           }
*/
           if(!item.keepPalette && p_palette!=null && p_paletteSet.size()>1 && i_compressionlevel < 10)
           {
                   i_tag |=TAG_CHANGEPALETTE;
                   dostag.writeByte(i_paletteIndex);
           }

           if(i_type != TYPE_D8BPP)
           {
             if (lg_extendedindex)
                  dostag.writeShort(item.index);
               else
                  dostag.writeByte(item.index);
           }


           if (item.link_number >= 0)
           {
                //writing the link flag
                dos.writeByte(TAG_LINK);
                if (lg_extendedindex)
                {
                   dos.writeShort(item.index);
                   dos.writeShort(item.link_number);
                }
                 else
                 {
                     dos.writeByte(item.index);
                     dos.writeByte(item.link_number);
                 }
           }
           else
           {
               String path = (item.path == null ? s_directory : item.path);
               String fullname = path+item.filename;
               File f[] = new File(path).listFiles(new CustomFilenameFilter(item));
               if(f==null || f.length<1) throw new Exception("File '"+path+item.filename+" not found");
               if(!f[0].isFile()) throw new Exception("Object '"+path+item.filename+" is not a file");
               ab_arr = new byte[(int) f[0].length()];
               FileInputStream fis = new FileInputStream(f[0]);
               fis.read(ab_arr);
               fis.close();

               // mark tag
               if (item.isRaw) i_tag |= TAG_NONUNPACK;

               switch (i_type)
               {
                     case TYPE_NOKIA:
                         {
                                packingForNokia(ab_arr, dos);
                         }
                         ;
                         break;
                     case TYPE_SIEMENS:
                         {
                                packingForSiemens(ab_arr, dos);
                         }
                         ;
                         break;
                     case TYPE_PNG:
                         {
                               if (i_compressionlevel == 51)
                                {
                                    ab_arr = repackGifToPNG(ab_arr, 9, false, null, true);
                                    if (lg_placeFormedPNGFiles)
                                    {
                                        FileOutputStream p_pngFileOutputStream = new FileOutputStream(fullname + ".png");
                                        p_pngFileOutputStream.write(ab_arr);
                                        p_pngFileOutputStream.flush();
                                        p_pngFileOutputStream.close();
                                    }
                                    packingPNGtoLitePNG(ab_arr, dos);
                                }
                                else if (i_compressionlevel >= 40 && i_compressionlevel <= 50)
                                {
                                    // Wavelet
                                    packWaveletImage(ab_arr, i_compressionlevel, dos);
                                    if (lg_placeFormedPNGFiles)
                                    {
                                        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(1024);
                                        packWaveletImage(ab_arr, i_compressionlevel, new DataOutputStream(p_baos));
                                        p_baos.close();
                                        FileOutputStream p_pngFileOutputStream = new FileOutputStream(fullname + ".wvl");
                                        p_pngFileOutputStream.write(p_baos.toByteArray());
                                        p_pngFileOutputStream.flush();
                                        p_pngFileOutputStream.close();
                                        p_baos = null;
                                    }
                                }
                                else
                                {

                                    Image i_img = Toolkit.getDefaultToolkit().createImage(ab_arr);
                                    p_mtracker.addImage(i_img, 0);
                                    try
                                    {
                                         p_mtracker.waitForID(0);
                                    }
                                    catch (InterruptedException e)
                                    {
                                         return null;
                                    }
                                    p_mtracker.removeImage(i_img);

                                    ByteArrayOutputStream p_is = new ByteArrayOutputStream(ab_arr.length);
                                    PNGEncoder p_encoder = new PNGEncoder(i_img, p_is, p_palette, !item.keepPalette);
                                    p_encoder.setCompressionLevel(i_compressionlevel);
                                    p_encoder.setAlpha(lg_alphachannel);

                                    //// cropping;

                                    PNGEncoder.ImageData iData = p_encoder.getImageDataObject();

                                    if((crop_flagset & ~C_STORE)!=0)
                                    {
                                      if((crop_flagset & C_LEFT) !=0)  left   = iData.getFreeSpaceLeft();
                                      if((crop_flagset & C_RIGHT) !=0) right  = iData.getFreeSpaceRight();
                                      if((crop_flagset & C_TOP) !=0)   top    = iData.getFreeSpaceTop();
                                      if((crop_flagset & C_DOWN) !=0)  bottom = iData.getFreeSpaceBottom();
                                      if((crop_flagset & C_HCENTER) !=0)
                                      {
                                        left   = iData.getFreeSpaceLeft();
                                        right   = iData.getFreeSpaceRight();
                                        left = right = Math.min(left,right);

                                      }
                                      if((crop_flagset & C_VCENTER) !=0)
                                      {
                                         top  = iData.getFreeSpaceTop();
                                         bottom  = iData.getFreeSpaceBottom();
                                         top = bottom = Math.min(top,bottom);
                                      }
                                      if((crop_flagset & C_STORE) !=0 && (left!=0 || top!=0))
                                      {
                                          i_tag |= TAG_IMAGEOFFSET;
                                          dostag.writeByte(left);
                                          dostag.writeByte(top);
                                          lg_containsOffset = true;
                                      }
                                      iData.cropImage(left,right,top,bottom);
                                    }

                                    try
                                    {
                                      p_encoder.writePNG(iData);
                                    }
                                    catch (IOException e)
                                    {
                                           System.out.println(e.getMessage());
                                           System.exit(1);
                                    }

                                    ab_arr = p_is.toByteArray();

                                    if (lg_placeFormedPNGFiles)
                                    {
                                        FileOutputStream p_pngFileOutputStream = new FileOutputStream(fullname + ".png");
                                        p_pngFileOutputStream.write(ab_arr);
                                        p_pngFileOutputStream.flush();
                                        p_pngFileOutputStream.close();
                                    }
                                    packingPNGtoLitePNG(ab_arr, dos);
                                }
                         }
                         ;
                         break;
                     case TYPE_D8BPP:
                         {
                                packingForD8BPP(ab_arr, dos, p_palette, i_compressionlevel);
                         }
                         ;
                         break;
                     case TYPE_PNGLITE:
                         {
                                //ab_arr = repackGifToPNG(ab_arr,i_compressionlevel);
                                //new FileOutputStream(s_name+".png").write(ab_arr);
                                packingPNGtoLitePNG(ab_arr, dos);
                         }
                         ;
                         break;
               }
           // Writing the image number
           dos.flush();

         if (i_type != TYPE_D8BPP)
         {
            p_tmparray.writeTo(dostag);
            dostag.flush();
            p_tmparray.reset();
            dostag.close();
            dos.writeByte(i_tag);
            tagbaos.writeTo(dos);
            dos.flush();
         }

           }
         dos.close();
         return p_tmparray.toByteArray();

    }

    public int getCommandIndex(String cmd, String[] cmdlist)
    {
          int ret = -1;
          if(cmd!=null && (cmd=cmd.toLowerCase().trim()).length()>0)
          {
            for(int i=0;i<cmdlist.length;i++)
            {
              if(cmdlist[i].equals(cmd))
              {
                ret = i;
                break;
              }
            }
          }
          return ret;
    }

    public String[] getStringPair(String cmd, char separator)
    {
          if(cmd==null || cmd.length() == 0) return null;
          int idx = cmd.indexOf(separator);
          if(idx>0)
           return new String[]{cmd.substring(0,idx),cmd.substring(idx+1)};
          else
           return new String[]{cmd};
    }

    public void processScript(String str) throws Exception
    {
        String[] sections = str.split(";");
        for(int i = 0; i<sections.length;i++)
        {
          String[] pair  = getStringPair(sections[i], ':');
          String [] pList = null;
          if(pair.length>1 && pair[1]!=null)
               pList = pair[1].split(",");
              switch(getCommandIndex(pair[0],SCRIPT_COMMAND))
              {
                case SCR_DIRECTORY:
                                   {
                                     if(pair.length>0)
                                     {
                                        s_directory = s_baseDirectory+pair[1];
                                     }
                                     else
                                       s_directory = s_baseDirectory;

                                     if(!s_directory.endsWith(File.separator))
                                       s_directory += File.separator;

                                     if(lg_verbose)
                                       System.out.println("Change directory to: "+s_directory);
                                   }
                                    break;
                case SCR_PALETTE:
                                   {
                                     if(pair.length>1)
                                     {
                                        String[] palette_pair = getStringPair(pair[1], ':');
                                        switch(getCommandIndex(palette_pair[0],PALETTE_COMMAND))
                                        {
                                          case 0: // load
                                                  if(palette_pair.length>1)
                                                     loadPalette(s_baseDirectory+palette_pair[1].trim());
                                                  break;
                                          default:
                                                  if(i_paletteIndex>0) i_paletteIndex = 0;
                                        }
                                     }
                                   }
                                    break;

                case SCR_SAVESTATE:
                                    script_state.push(new Integer(crop_flagset));
                                     if(lg_verbose)
                                       System.out.println("State saved");
                                    break;
                case SCR_LOADSTATE:
                                    if(!script_state.empty())
                                    {
                                      crop_flagset = ((Integer)script_state.pop()).intValue();
                                      if(lg_verbose)
                                       System.out.println("State loaded");
                                    }
                                    break;

                case SCR_CROP:      {
                                      String report = "";
                                      if(pList!=null)
                                       for (int j = 0; j<pList.length; j++)
                                       {
                                        int pos = getCommandIndex(pList[j],CROP_COMMAND);
                                        switch(pos)
                                        {
                                          case 0: // left
                                          case 1: // right
                                          case 2: // top
                                          case 3: // down
                                          case 4: // store_offset
                                          case 6: // hcenter
                                          case 7: // vcenter
                                                  crop_flagset |= 1<<pos;
                                                  break;
                                          case 5: crop_flagset |= C_LEFT | C_RIGHT | C_TOP | C_DOWN | C_STORE;
                                                  break;
                                          case 8: crop_flagset = 0;
                                                  break;
                                        }
                                        if (pos>=0) report += (report.length()==0?"":", ")+CROP_COMMAND[pos];
                                      }
                                        if(lg_verbose)
                                           System.out.println("Crop:"+report);
                                    }
                                    break;
              }
        }
    }

    public void writeRelatedText(Item [] list) throws Exception
    {
            FileOutputStream p_fos = new FileOutputStream(s_javaname);
            DataOutputStream p_dos = new DataOutputStream(p_fos);

            p_dos.writeBytes("//$//#global INDEXRESOLUTION=\""+(lg_extendedindex?"EXTENDED":"NORMAL")+"\"\r\n");
            p_dos.writeBytes("//$//#global CONTAINS_OFFSET="+lg_containsOffset+"\r\n");
            if(p_paletteSet.size()>1) p_dos.writeBytes("//$//#global MULTIPALETTE_SET=true\r\n");
            p_dos.writeBytes("\r\n");

            for(int i=0;i<list.length;i++)
            {
                 if(list[i].name!=null)
                 {
                    p_dos.writeBytes("private static final int IMG_"
                             + list[i].name.toUpperCase().replace('-','_').replace(' ', '_')
                             + " = " + list[i].index + ";\r\n");
                 }
            }

            p_dos.writeBytes("private static final int TOTAL_IMAGES_NUMBER = " + i_names_counter + ";\r\n");

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

                p_dos.writeBytes("private static final int IMAGE_FILE_SIZE = " + i_outputStorageSize + ";\r\n");
            }

            // Writing the palette if it is defined
            if (p_paletteSet.size()>0 && lg_dontstorepalette)
            {
               Palette p_palette;
               byte [] ab_palettearray;
               if (p_paletteSet.size() ==  1)
               {
                 p_palette = (Palette)p_paletteSet.get(0);
                 p_dos.writeBytes("\r\nprivate static final int PALETTE_SIZE = " + p_palette.colors.length + ";\r\n");
                 p_dos.writeBytes("private static final byte [] COMMON_PALETTE = new byte[]\r\n{\r\n");
                 p_dos.writeBytes(p_palette.toString());
                 p_dos.writeBytes("\r\n};\r\n");

                 p_dos.writeBytes("\r\nprivate static final byte [] PNG_COMMON_PALETTE = new byte []\r\n{");
                 StringBuffer p_buf = new StringBuffer();
                 ab_palettearray = p_palette.convertPalette2PNGchunk();

                 for (int li = 0; li < ab_palettearray.length; li++)
                 {
                    if (p_buf.length() != 0) p_buf.append(',');
                    p_buf.append("(byte)0x" + Integer.toHexString(ab_palettearray[li] & 0xFF));
                 }
                 p_dos.writeBytes(p_buf.toString());
                 p_dos.writeBytes("};\r\n");
               }
                else
                {
                   StringBuffer ps = new StringBuffer("\r\nprivate static final int [] PALETTE_SIZE = new int[] {");
                   StringBuffer cp = new StringBuffer("private static final byte [][] COMMON_PALETTE = new byte[][] {");
                   StringBuffer png = new StringBuffer("\r\nprivate static final byte [][] PNG_COMMON_PALETTE = new byte [][]\r\n{");
                   int amount = p_paletteSet.size();
                   for(int i = 0; i<amount; i++)
                   {
                      if(i>0) {
                        ps.append(", ");
                        cp.append(", ");
                        png.append(", ");
                      }
                      p_palette = (Palette)p_paletteSet.get(i);
                      ps.append(p_palette.colors.length);

                      cp.append("\r\n {");
                      cp.append(p_palette.toString());
                      cp.append("\r\n }");

                      png.append("\r\n {");
                      ab_palettearray = p_palette.convertPalette2PNGchunk();
                      for (int li = 0; li < ab_palettearray.length; li++)
                      {
                        if (li>0) png.append(',');
                        png.append("(byte)0x" + Integer.toHexString(ab_palettearray[li] & 0xFF));
                      }
                      png.append("\r\n }");
                   }
                 ps.append("};\r\n");
                 cp.append("\r\n};\r\n");
                 png.append("\r\n};\r\n");

                 p_dos.writeBytes(ps.toString());
                 p_dos.writeBytes(cp.toString());
                 p_dos.writeBytes(png.toString());
                }
            }

            p_dos.flush();
            p_fos.flush();
            p_dos.close();
            p_fos = null;
            p_dos = null;
            if (!lg_verbose)System.out.print(s_clearline);
            System.out.println(i_names_counter+" images processed.");
    }


    public void percentLine(int current, int total,String report)
    {
        int i_barlen = 15;
        int i_textlen = 75 - i_barlen-2-1-3;

//                    s_report = (s_report+s_spaceline).substring(0,i_textlen);
//                    s_report = (i_indx+" : "+ s_name + s_spaceline).substring(0,i_textlen);
        System.out.print("\r"+report.substring(0,i_textlen));

        // percentage
        if(total>0)
        {
           int items = (current*i_barlen+total-1)/total;
           int perc = (current*100+total-1)/total;
           StringBuffer sb = new StringBuffer("  [");
           for (int i=0;i<i_barlen;i++)
               sb.append(i<=items?'o':'.');
           sb.append(']');
           sb.append(' ');
           sb.append(Integer.toString(perc));
           sb.append('%');
           System.out.print(sb.toString());
        }
    }

    public static void main(String[] args)
    {
        System.out.println(copyright);

        ImagePacker ip = new ImagePacker();

        try{
             int i,j,k;
             // parse command line
             ip.processCommandLine(args);
             // load list of files to process
             Item[] list = ip.loadIndexList(ip.s_directory + ip.s_descriptorname);

             FileOutputStream fos = new FileOutputStream(ip.s_packagename);
             DataOutputStream dos = new DataOutputStream(fos);

             // store amount of files
             if(ip.lg_extendedindex)
                dos.writeShort(ip.i_names_counter);
               else
                 dos.writeByte(ip.i_names_counter);

             byte[][] files = new byte[ip.i_names_counter][];
             int processed = 0;

             // process images
             for(i=0;i<list.length;i++)
             {
                  if(!lg_verbose)
                    ip.percentLine(i,list.length,
                                    (i+" : "
                                     +(list[i].name!=null?list[i].name:"[ script, line# "+list[i].source_line+"]")
                                     +ip.s_spaceline
                                    )
                                   );
                   else
                    System.out.println(list[i].name!=null?list[i].name:"[ script, line# "+list[i].source_line+"]");

                 if(list[i].name!=null)
                 {
                     files[processed++] = ip.processImage(list[i]);
                 }
                 else
                  if(list[i].script!=null)
                    ip.processScript(list[i].script);
             }

             // store palettes set
             if(!ip.lg_dontstorepalette)
             {
               if(ip.p_paletteSet.size()>1)
               {
                  int n = ip.p_paletteSet.size();
                  dos.writeByte(TAG_PALETTESET);
                  dos.writeByte(n);
                  for(i = 0; i<n; i++)
                  {
                    byte[] tmp = ((Palette)ip.p_paletteSet.get(i)).convertPalette2PNGchunk();
                    dos.writeShort(tmp.length);
                    dos.write(tmp);
                  }
               }
               else
               if(ip.p_paletteSet.size()>0)
               {
                  dos.writeByte(TAG_PALETTE);
                  byte[] tmp = ((Palette)ip.p_paletteSet.get(0)).convertPalette2PNGchunk();
                  dos.writeShort(tmp.length);
                  dos.write(tmp);
               }
             }

             // store files
             for(i=0;i<files.length;i++)
             dos.write(files[i]);

             dos.close();
             fos.close();

             // write text
             ip.writeRelatedText(list);


        } catch(Exception e)
        {
          System.out.println(e.getMessage());
        }
    }
}
/*
            // Writing of the number of images in the image block
            if (!lg_tocreatedescriptor && i_type != TYPE_D8BPP)
            {
                if (i_counter>255)
                  if(i_counter>65535)
                  {
                    System.out.println("(!) The Image Packer engine can't handle over 2^16 images due a right sence limitaion");
                  } else
                      lg_extendedindex = true;


                if (lg_extendedindex) {
                    p_dos.writeShort(i_counter);
                    System.out.println("Processing with extended index resolution");
                } else
                     p_dos.writeByte(i_counter);
            }
            if(ab_palettearray != null && !lg_dontstorepalette)
            {
               System.out.println("Palette was added to binary package");
               p_dos.writeByte(TAG_PALETTE);
               p_dos.writeShort(ab_palettearray.length);
               p_dos.write(ab_palettearray);
            }
            Iterator p_iterator = p_fileset.iterator();
            int i_indx = 0;

            int[] ai_offsets = new int[p_fileset.size()];

            while (p_iterator.hasNext())
            {
                FileDescriptorInfo p_dinfo = (FileDescriptorInfo) p_iterator.next();

                String s_name = null;
                String s_extensionx = null;
                String s_report=null;
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

                if (lg_verbose) System.out.println("------------------------------------------");

                int i_complevel = i_compressionlevel;
                if (p_dinfo.i_compresslevel >= 0) i_complevel = p_dinfo.i_compresslevel;
                if (p_dinfo.i_linkindex >= 0 && p_dinfo.lg_commentedFile) throw new IOException("You can not have any commented image as a link image");
                if (p_dinfo.lg_commentedFile)
                {
                    if (lg_verbose) System.out.println("$$$ ");
                    continue;
                }
                if (p_dinfo.i_linkindex >= 0)
                    s_report="Picture " + s_name + " : " + i_indx + " a link to image with the number " + p_dinfo.i_linkindex;
                else
                    s_report="Picture " + i_indx + " the file name is " + p_file.getName() + " compression level=" + i_complevel
                                       +(p_dinfo.lg_attachedPalette?", with own palette":", w/o palette");

                if (lg_verbose) System.out.println(s_report);
                  else {
                    System.out.print(s_clearline);

                    int i_barlen = 15;
                    int i_textlen = 75 - i_barlen-2-1-3;

//                    s_report = (s_report+s_spaceline).substring(0,i_textlen);
                    s_report = (i_indx+" : "+ s_name + s_spaceline).substring(0,i_textlen);
                    System.out.print(s_report);

                    // percentage
                    if(i_counter>0)
                    {

                      int items = (i_indx*i_barlen+i_counter-1)/i_counter;
                      int perc = (i_indx*100+i_counter-1)/i_counter;
                      StringBuffer sb = new StringBuffer("  [");
                      for (int i=0;i<i_barlen;i++)
                        sb.append(i<=items?'o':'.');
                      sb.append(']');
                      sb.append(' ');
                      sb.append(Integer.toString(perc));
                      sb.append('%');
                      System.out.print(sb.toString());
                    }
                  }

                byte[] ab_arr = null;
                FileInputStream p_fis = null;

                if (!p_dinfo.lg_commentedFile)
                {
                    if (p_dinfo.i_linkindex < 0)
                    {
                        s_name = s_name.substring(0, s_name.length() - s_extensionx.length());
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
                    else
                    {
                        // Writing the image flag
                        if (p_dinfo.i_linkindex >= 0)
                        {
                            //writing the link flag
                            p_dos.writeByte(0x1);
                        }
                        else
                        {
                            if (p_dinfo.lg_nonUnpackFlag)
                            {
                                // Writing non unpacking flag
                                p_dos.writeByte(0x2);
                            }
                              else
                                p_dos.writeByte(0x0);
                        }

                        // Writing the image number
                        if (lg_extendedindex)
                            p_dos.writeShort(i_windx);
                          else
                              p_dos.writeByte(i_windx);

                        if (p_dinfo.i_linkindex >= 0)
                        {
                            if (lg_extendedindex)
                               p_dos.writeShort(p_dinfo.i_linkindex);
                             else
                                p_dos.writeByte(p_dinfo.i_linkindex);
                        }
                    }
                }
                ByteArrayOutputStream p_tmparray = new ByteArrayOutputStream(1024);
                DataOutputStream p_tmpStream = new DataOutputStream(p_tmparray);

                if (p_dinfo.i_linkindex < 0)
                {
                    switch (i_type)
                    {
                        case TYPE_NOKIA:
                            {
                                packingForNokia(ab_arr, p_tmpStream);
                            }
                            ;
                            break;
                        case TYPE_SIEMENS:
                            {
                                packingForSiemens(ab_arr, p_tmpStream);
                            }
                            ;
                            break;
                        case TYPE_PNG:
                            {
                                if (i_complevel == 51)
                                {
                                    ab_arr = repackGifToPNG(ab_arr, 9, false, null, true);
                                    if (lg_placeFormedPNGFiles)
                                    {
                                        FileOutputStream p_pngFileOutputStream = new FileOutputStream(s_name + ".png");
                                        p_pngFileOutputStream.write(ab_arr);
                                        p_pngFileOutputStream.flush();
                                        p_pngFileOutputStream.close();
                                    }
                                    packingPNGtoLitePNG(ab_arr, p_tmpStream);
                                }
                                else if (i_complevel >= 40 && i_complevel <= 50)
                                {
                                    // Wavelet
                                    packWaveletImage(ab_arr, i_complevel, p_tmpStream);
                                    if (lg_placeFormedPNGFiles)
                                    {
                                        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(1024);
                                        packWaveletImage(ab_arr, i_complevel, new DataOutputStream(p_baos));
                                        p_baos.close();
                                        FileOutputStream p_pngFileOutputStream = new FileOutputStream(s_name + ".wvl");
                                        p_pngFileOutputStream.write(p_baos.toByteArray());
                                        p_pngFileOutputStream.flush();
                                        p_pngFileOutputStream.close();
                                        p_baos = null;
                                    }
                                }
                                else
                                {
                                    ab_arr = repackGifToPNG(ab_arr, i_complevel, lg_alphachannel, p_palette, !p_dinfo.lg_attachedPalette);
                                    if (lg_placeFormedPNGFiles)
                                    {
                                        FileOutputStream p_pngFileOutputStream = new FileOutputStream(s_name + ".png");
                                        p_pngFileOutputStream.write(ab_arr);
                                        p_pngFileOutputStream.flush();
                                        p_pngFileOutputStream.close();
                                    }
                                    packingPNGtoLitePNG(ab_arr, p_tmpStream);
                                }
                            }
                            ;
                            break;
                        case TYPE_D8BPP:
                            {
                                packingForD8BPP(ab_arr, p_tmpStream, p_palette, i_complevel);
                            }
                            ;
                            break;
                        case TYPE_PNGLITE:
                            {
                                //ab_arr = repackGifToPNG(ab_arr,i_compressionlevel);
                                //new FileOutputStream(s_name+".png").write(ab_arr);
                                packingPNGtoLitePNG(ab_arr, p_tmpStream);
                            }
                            ;
                            break;
                    }
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

            p_dos.writeBytes("//$//#global INDEXRESOLUTION=\""+(lg_extendedindex?"EXTENDED":"NORMAL")+"\"\r\n\r\n");

            Iterator p_enum = p_fileset.iterator();
            while (p_enum.hasNext())
            {
                FileDescriptorInfo p_di = (FileDescriptorInfo) p_enum.next();

                String s_name = p_di.s_filename;

                s_name = "IMG_" + s_name.toUpperCase().replace('-','_').replace(' ', '_');
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
            if (p_palette != null && lg_dontstorepalette)
            {
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
                }
                p_dos.writeBytes(p_buf.toString());
                p_dos.writeBytes("\r\n};\r\n");


                // Writing the palette in the PNG format
                // byte[] ab_palettearray = convertPalette2PNGchunk(ap_colors);

                p_dos.writeBytes("\r\nprivate static final byte [] PNG_COMMON_PALETTE = new byte []\r\n{");
                p_buf = new StringBuffer();

                for (int li = 0; li < ab_palettearray.length; li++)
                {
                    if (p_buf.length() != 0) p_buf.append(',');
                    p_buf.append("(byte)0x" + Integer.toHexString(ab_palettearray[li] & 0xFF));
                }
                p_dos.writeBytes(p_buf.toString());
                p_dos.writeBytes("};\r\n");
            }

            p_dos.flush();
            p_fos.flush();
            p_dos.close();
            p_fos = null;
            p_dos = null;
            if (!lg_verbose)System.out.print(s_clearline);
            System.out.println(i_counter+" images processed.");
        }
        catch (IOException ex)
        {
            if (!lg_verbose)System.out.println(s_clearline);
            System.err.println("IOException [" + ex + "]");
            System.exit(1);
        }
        System.exit(0);
    }

*/
package com.igormaznitsa.MIDPTools.ImagePacker;
import java.io.*;
public class Item {

    boolean isItem     = false;
    boolean isRaw      = false;
    boolean keepPalette= false;
    String name        = null;
    String compression = null;
    String link        = null;
    String script      = null;
    String raw_source  = null;
    String unrecoginzed= null;
    String orig_number = null;

    String path        = null;
    String filename    = null;

    int order          = -1;
    int source_line    = 0;
    int link_number    = -1;
    int index          = -1;


   public String toString(){return raw_source;}

   public static Item processString(String s) throws Exception {
    Item ret = new Item();
    ret.raw_source = s;

    if(s!=null && s.length()>0){
      String t = s.trim();
      if (t.length()!=0 && !t.startsWith("#"))
      {
        if(t.startsWith("@"))
        {
           t = t.substring(1).trim();
           if(t.length()>0) ret.script = t;
        }
        else
        {
          int action = 0;
          while (t.length()>0){
            int idx = t.indexOf(",");
            if(idx<0) idx = t.length();
            String token = t.substring(0,idx);
            t = t.substring(Math.min(idx+1,t.length()));
            switch(action++){
              case 0:// name
                     {
                       while(true)
                        if (token.startsWith("!"))
                        {
                           ret.keepPalette = true;
                           token = token.substring(1).trim();
                        }
                        else
                        if (token.startsWith("*"))
                        {
                           ret.isRaw = true;
                           token = token.substring(1).trim();
                        }
                        else
                        break;
                       if(token.length()>0)
                       {
                         ret.name = token;
                         File f = new File(token);
                         ret.path = f.getParent();
                         ret.filename = f.getName();
                         ret.isItem = true;
                       } else {
                         action = 9999;
                         //System.out.println("! Empty name");
                       }
                     }
                     break;
              case 1:// original index
                     {
                       if((token = token.trim()).length()>0)
                       {
                         ret.orig_number = token.trim();
                         try
                         {
                           ret.index = Integer.parseInt(ret.orig_number);
                         } catch(NumberFormatException e)
                         {
                           throw new Exception("Invalid index");
                         }
                       }
                     }
                     break;
              case 2:// compression rate
                     {
                       if((token = token.trim()).length()>0)
                       {
                         ret.compression = token.trim();
                       }
                     }
                     break;
              case 3:// link
                     {
                       if((token = token.trim()).length()>0)
                       {
                         ret.link = token.trim();
                       }
                     }
                     break;
              default: // unrecognized data
                ret.unrecoginzed = (ret.unrecoginzed==null?token:ret.unrecoginzed+","+token);
            }
          }
        }
      }
    }
    return ret;
  }
}
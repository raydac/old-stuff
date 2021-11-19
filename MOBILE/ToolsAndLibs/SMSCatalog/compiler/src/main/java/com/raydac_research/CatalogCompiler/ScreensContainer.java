package com.raydac_research.CatalogCompiler;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Hashtable;

public class ScreensContainer
{
      public Screen [] ap_Screens;

      public boolean containsSMSServicesWithMacroses()
      {
          for(int li=0;li<ap_Screens.length;li++)
          {
              Screen p_scr = ap_Screens[li];
              for(int lx=0;lx<p_scr.ap_Components.length;lx++)
              {
                  ScreenComponent p_compo = p_scr.ap_Components[lx];
                  if (p_compo.i_ContentType==Screen.CONTENTTYPE_SMSSERVICE)
                  {
                      String s_resID = p_compo.s_ResourceID;
                      if (s_resID!=null && s_resID.indexOf('%')>=0) return true;
                  }
              }
          }
          return false;
      }

      public ScreensContainer(Element _catalog,StringsPool _pool,CompatibleRecordsContainer _compatible,boolean _fuzzyItemInfo) throws IOException
      {
          Hashtable p_scrTable = new Hashtable();
          NodeList p_screens = _catalog.getElementsByTagName(CatalogContainer.XML_SCREEN);
          ap_Screens = new Screen[p_screens.getLength()];
          for(int li = 0;li<p_screens.getLength();li++)
          {
              Element p_screen = (Element) p_screens.item(li);
              ap_Screens[li] = new Screen(p_screen,_pool,_compatible,_fuzzyItemInfo);
              String s_scrID = ap_Screens[li].s_ScreenID;
              if (p_scrTable.contains(s_scrID)) throw new IOException("You have duplicated screen with id="+s_scrID);
              p_scrTable.put(s_scrID,ap_Screens[li]);
          }

          // Обработка связей
          for(int li=0;li<ap_Screens.length;li++)
          {
              Screen p_scr = ap_Screens[li];
              for(int lx=0;lx<p_scr.ap_Components.length;lx++)
              {
                  ScreenComponent p_compo = p_scr.ap_Components[lx];
                  if (p_compo.i_Type == ScreenComponent.COMPONENT_LINK)
                  {
                      String s_destScreenName = p_compo.s_ScreenID;
                      Screen p_sc = (Screen)p_scrTable.get(s_destScreenName);
                      if (p_sc==null) throw new IOException("Can't find screen for ID="+s_destScreenName);
                      p_compo.p_LinkedScreen = p_sc;
                  }
              }
          }
      }

    public byte [] toByteArray(StringsPool _pool) throws IOException
    {
        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(8000);
        DataOutputStream p_dos = new DataOutputStream(p_baos);

        for(int li=0;li<ap_Screens.length;li++)
        {
            int i_offset = p_dos.size();
            Screen p_scr = ap_Screens[li];
            p_scr.i_ScreenOffset = i_offset;
            p_scr.saveToStream(p_dos,_pool);
        }
        p_dos.flush();

        p_baos.reset();

        for(int li=0;li<ap_Screens.length;li++)
        {
            Screen p_scr = ap_Screens[li];
            p_scr.saveToStream(p_dos,_pool);
        }

        return p_baos.toByteArray();
    }
}

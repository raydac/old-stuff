package com.raydac_research.CatalogCompiler;

import com.raydac_research.Png.PNGEncoder;

import java.io.*;
import java.util.Vector;

import java.util.Hashtable;
import java.awt.image.BufferedImage;

public class ResourcesContainer
{
    public byte [][] abab_Resources;

    /**
     * Конструктор, данный объект формируется последним, на базе уже сформированных объектов
     * @param _scrContainer
     * @param _pool
     * @throws IOException
     */
    public ResourcesContainer(CatalogContainer _catalog,ScreensContainer _scrContainer,StringsPool _pool) throws IOException
    {
        _catalog.lg_HasJARResources = false;
        _catalog.lg_HasHTTPResources = false;
        
        Vector p_resourcesList = new Vector();
        Hashtable p_resTable = new Hashtable();

        Screen [] ap_screens = _scrContainer.ap_Screens;
        for(int li=0;li<ap_screens.length;li++)
        {
            Screen p_scr = ap_screens[li];
            ScreenComponent [] ap_components = p_scr.ap_Components;
            for(int lc=0;lc<ap_components.length;lc++)
            {
                ScreenComponent p_compo = ap_components[lc];
                if (p_compo.i_Type==ScreenComponent.COMPONENT_ITEM)
                {
                    String s_prev = p_compo.s_Preview;
                    if (s_prev!=null)
                    {
                        int i_index = 0xFFFF;
                        if (p_resTable.contains(s_prev))
                        {
                            i_index = ((Integer)p_resTable.get(s_prev)).intValue();
                        }
                        else
                        if (s_prev.startsWith("file://"))
                        {
                            s_prev = s_prev.substring(7).trim();
                            File p_file = new File(s_prev);
                            if (!p_file.exists() || p_file.isDirectory()) throw new IOException("Can't find resource file "+s_prev);

                            byte [] ab_resArray = null;

                            // Если GIF картинка то пережимаем в PNG
                            if (s_prev.endsWith(".gif"))
                            {
                                try
                                {
                                    BufferedImage p_img = PNGEncoder.convertFileToImage(p_file);
                                    ByteArrayOutputStream p_outStream = new ByteArrayOutputStream(10000);
                                    PNGEncoder p_enc = new PNGEncoder(p_img, p_outStream, null, false);
                                    p_enc.setCompressionLevel(9);
                                    p_enc.setAlpha(false);
                                    p_enc.encodeImage();
                                    ab_resArray = p_outStream.toByteArray();
                                }
                                catch (IOException e)
                                {
                                    System.out.println("Can't load resource image "+s_prev);
                                    throw new IOException("Error of resource file loading ["+s_prev+"]");
                                }
                            }
                            else
                            {
                                FileInputStream p_inStr = new FileInputStream(p_file);
                                ab_resArray = new byte[(int)p_file.length()];
                                p_inStr.read(ab_resArray);
                                p_inStr.close();
                            }

                            i_index = p_resourcesList.size() | 0x8000;
                            p_resTable.put(p_compo.s_Preview.trim(),new Integer(i_index));
                            p_resourcesList.add(ab_resArray);
                        }
                        else
                        {
                            if (s_prev.startsWith("http://")) _catalog.lg_HasHTTPResources = true;
                            if (s_prev.startsWith("jar://")) _catalog.lg_HasJARResources = true;
                            i_index = _pool.addString(s_prev);
                        }

                        p_compo.i_PreviewURL = i_index;
                    }
                    else
                    {
                        p_compo.i_PreviewURL = 0xFFFF;
                    }
                }
            }
        }

        abab_Resources = new byte[p_resourcesList.size()][];
        for(int li=0;li<abab_Resources.length;li++)
        {
            byte [] ab_arr = (byte[])p_resourcesList.elementAt(li);
            abab_Resources[li] = ab_arr;
        }
        p_resourcesList.clear();
        p_resTable.clear();
        p_resourcesList = null;
        p_resTable = null;
    }

    /**
     * записать данные в поток
     * @param _outStream поток для записи
     * @param _offset смещение в потоке,которое будет прибавлено к значениям в таблице
     * @throws IOException  происходит если произошло исключение в процесе операции
     */
    public void saveToStream(DataOutputStream _outStream,int _offset) throws IOException
    {
        // Высчитываем таблицу смещений
        int i_tableLength = abab_Resources.length;
        _offset += 2+(i_tableLength*4); // размер таблицы в ячейках по 4 байта
        _outStream.writeShort(i_tableLength);

        for(int li=0;li<abab_Resources.length;li++)
        {
            byte [] ab_byte = abab_Resources[li];
            _outStream.writeInt(_offset);
            _offset += 2;
            _offset += ab_byte.length;
        }

        // Записываем данные
        for(int li=0;li<abab_Resources.length;li++)
        {
            byte [] abab_resource =  abab_Resources[li];
            _outStream.writeShort(abab_resource.length);
            _outStream.write(abab_resource);
        }
        _outStream.flush();
    }
}

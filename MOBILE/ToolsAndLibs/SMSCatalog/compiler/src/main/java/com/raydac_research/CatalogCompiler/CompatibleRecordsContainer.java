package com.raydac_research.CatalogCompiler;

import org.w3c.dom.Element;

import java.util.Vector;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;

public class CompatibleRecordsContainer
{
    public Vector p_CompatibleRecords;

    public CompatibleRecordsContainer()
    {
        p_CompatibleRecords = new Vector();
    }

    public CompatibleRecord addCompatibleRecord(Element _element,StringsPool _pool) throws IOException
    {
        CompatibleRecord p_rec = new CompatibleRecord(_element,_pool);
        for(int li=0;li<p_CompatibleRecords.size();li++)
        {
             if (p_CompatibleRecords.elementAt(li).equals(p_rec))
             {
                 return (CompatibleRecord)p_CompatibleRecords.elementAt(li);
             }
        }

        p_CompatibleRecords.add(p_rec);
        return p_rec;
    }

    public byte [] toByteArray(StringsPool _pool) throws IOException
    {
        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(3000);
        DataOutputStream p_dos = new DataOutputStream(p_baos);

        System.out.println("Compatible size");

        for(int li=0;li<p_CompatibleRecords.size();li++)
        {
            CompatibleRecord p_rec = (CompatibleRecord) p_CompatibleRecords.elementAt(li);
            p_rec.i_Offset = p_dos.size();

            System.out.println("Offset = "+p_rec.i_Offset);

            // Vendor
            p_dos.writeShort(p_rec.i_Vendor);

            System.out.println("Vendor index "+p_rec.i_Vendor);

            // Количество элементов
            p_dos.writeShort(p_rec.ai_Models.length);

            // Элементы
            for(int lс=0;lс<p_rec.ai_Models.length;lс++)
            {
                System.out.println("Model index "+p_rec.ai_Models[lс]);
                p_dos.writeShort(p_rec.ai_Models[lс]);
            }
        }

        System.out.println("Compatible table size="+p_baos.size());

        p_dos.flush();

        return p_baos.toByteArray();
    }
}

package com.raydac_research.FormEditor.ExportModules.RrgScene.Resources;

import com.raydac_research.FormEditor.ExportModules.RrgScene.RrgScene;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Enumeration;

public class RrgResourceSection_Fonts extends RrgResourceSection
{
    public RrgResourceSection_Fonts()
    {
        super();
    }

    public byte[] saveResourceSection(boolean _writeType) throws IOException
    {
        Enumeration p_elements = p_resources.elements();

        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(10000);
        DataOutputStream p_daos = new DataOutputStream(p_baos);

        int i_poolID = 1;

        int i_number = getUsedResourcesCount();

        p_daos.write(i_number);

        while(p_elements.hasMoreElements())
        {
            RRGSceneResourceFont p_font = (RRGSceneResourceFont) p_elements.nextElement();

            if (p_font.isUsed())
            {
                p_font.setPoolID(i_poolID++);

                p_font.setOffset(p_daos.size());

                if (_writeType)
                {
                    p_daos.writeByte(p_font.getFormatType());
                }

                int i_length = p_font.getFontData().length;

                RrgScene.writeVLQ(p_daos,i_length);
                p_daos.write(p_font.getFontData());
            }
        }

        p_daos.flush();
        p_daos.close();

        if (p_baos.size()>0xFFFFFF) throw new IOException("Fonts section is too big");

        return p_baos.toByteArray();
    }
}

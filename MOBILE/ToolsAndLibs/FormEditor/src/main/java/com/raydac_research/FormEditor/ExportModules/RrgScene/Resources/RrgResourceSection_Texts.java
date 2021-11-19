package com.raydac_research.FormEditor.ExportModules.RrgScene.Resources;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.Enumeration;

public class RrgResourceSection_Texts extends RrgResourceSection
{
    public RrgResourceSection_Texts()
    {
        super();
    }

    public byte [] saveResourceSection(boolean _writeType) throws IOException
    {
        Enumeration p_elements = p_resources.elements();

        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(10000);
        DataOutputStream p_daos = new DataOutputStream(p_baos);

        int i_poolID = 1;
        int i_number = getUsedResourcesCount();
        p_daos.write(i_number);

        while(p_elements.hasMoreElements())
        {
            RRGSceneResourceText p_text = (RRGSceneResourceText) p_elements.nextElement();

            if (p_text.isUsed())
            {
                p_text.setPoolID(i_poolID++);
                p_text.setOffset(p_daos.size());

                p_daos.writeByte(p_text.getPoolID());
                p_daos.writeUTF(p_text.getText());
            }
        }

        p_daos.flush();
        p_daos.close();

        if (p_baos.size()>0xFFFFFF) throw new IOException("Texts section is too big");

        return p_baos.toByteArray();
    }
}

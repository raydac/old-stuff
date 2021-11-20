package com.raydac_research.FormEditor.ExportModules.RrgScene.Resources;

import com.raydac_research.FormEditor.ExportModules.RrgScene.RrgScene;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Enumeration;

public class RrgResourceSection_Sounds extends RrgResourceSection
{
    public RrgResourceSection_Sounds()
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
            RRGSceneResourceSound p_sound = (RRGSceneResourceSound) p_elements.nextElement();

            if (p_sound.isUsed())
            {
                p_sound.setPoolID(i_poolID++);
                p_sound.setOffset(p_daos.size());

                p_daos.writeByte(p_sound.getPoolID());

                if (_writeType)
                {
                    p_daos.writeByte(p_sound.getFormatType());
                }

                RrgScene.writeVLQ(p_daos,p_sound.getTimeLength());

                int i_length = p_sound.getSoundData().length;

                RrgScene.writeVLQ(p_daos,i_length);

                p_daos.write(p_sound.getSoundData());
            }
        }

        p_daos.flush();
        p_daos.close();

        if (p_baos.size()>0xFFFFFF) throw new IOException("Sounds section is too big");

        return p_baos.toByteArray();
    }
}

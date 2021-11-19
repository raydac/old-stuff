package com.raydac_research.FormEditor.ExportModules.RrgScene.Resources;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;
import java.io.IOException;
import java.io.DataOutputStream;

public abstract class RrgResourceSection
{
    protected Hashtable p_resources;

    public boolean containsResourceForID(String _id)
    {
        return p_resources.containsKey(_id);
    }

    public int size()
    {
        return p_resources.size();
    }

    public Iterator getResources()
    {
        return p_resources.values().iterator();
    }

    public RrgResourceSection()
    {
        p_resources = new Hashtable();
    }

    public int getUsedResourcesCount()
    {
        Enumeration p_components =  p_resources.elements();

        int i_counter = 0;

        while(p_components.hasMoreElements())
        {
            RRGSceneResource p_resource = (RRGSceneResource) p_components.nextElement();
            if (p_resource.isUsed()) i_counter++;
        }

        return i_counter;
    }

    public static void _write3bytes(int _value,DataOutputStream _stream) throws IOException
    {
        int i_b0 = _value & 0xFF;
        int i_b1 = (_value >> 8) & 0xFF;
        int i_b2 = (_value >> 16) & 0xFF;

        _stream.writeByte(i_b0);
        _stream.writeByte(i_b1);
        _stream.writeByte(i_b2);
    }

    public void addResource(RRGSceneResource _resource)
    {
        _resource.setUsedFlag(false);
        p_resources.put(_resource.s_ID,_resource);
    }

    public void indexingResources()
    {
        Enumeration p_components =  p_resources.elements();

        int i_id = 1;

        while(p_components.hasMoreElements())
        {
            RRGSceneResource p_resource = (RRGSceneResource) p_components.nextElement();
            if (p_resource.isUsed()) p_resource.setPoolID(i_id++);
        }
    }

    public RRGSceneResource getResource(String _id)
    {
        RRGSceneResource p_resource  = (RRGSceneResource) p_resources.get(_id);
        if (p_resource != null)
        {
            p_resource.setUsedFlag(true);
        }

        return p_resource;
    }

    public abstract byte [] saveResourceSection(boolean _writeType) throws IOException;
}

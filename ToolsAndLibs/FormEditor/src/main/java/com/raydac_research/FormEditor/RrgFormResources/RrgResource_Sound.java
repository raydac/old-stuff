package com.raydac_research.FormEditor.RrgFormResources;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.applet.AudioClip;
import java.applet.Applet;

import com.raydac_research.FormEditor.Misc.Utilities;
import com.raydac_research.FormEditor.Misc.Utilities;

public class RrgResource_Sound extends AbstractRrgResource
{
    protected AudioClip p_audioClip;
    protected File p_SoundFile;
    protected AudioClip p_audioClip_trans;
    protected File p_SoundFile_trans;
    protected static final String XML_SOUND = "sound";
    protected static final String XML_FILE = "file";

    public boolean refreshResource() throws IOException
    {
        setAudioFile(p_SoundFile);
        return true;
    }

    public void startTransaction()
    {
        super.startTransaction();
        p_SoundFile_trans = p_SoundFile;
        p_audioClip_trans = p_audioClip;
    }

    public void rollbackTransaction()
    {
        super.rollbackTransaction();
    }

    public RrgResource_Sound(String _id,File _file) throws IOException
    {
        super(_id,TYPE_SOUND);
        setAudioFile(_file);
    }

    public AudioClip getAudioClip()
    {
        return p_audioClip;
    }

    public File getSoundFile()
    {
        return p_SoundFile;
    }

    public void setAudioFile(File _soundFile) throws IOException
    {
        if (_soundFile == null)
        {
            p_audioClip = null;
            p_SoundFile = null;
            return;
        }
        try
        {
             p_audioClip = Applet.newAudioClip(_soundFile.toURL());
        }
        catch (Exception _ex)
        {
            throw new IOException("Unsupported audio format");
        }

        p_SoundFile = _soundFile;
    }

    public void _saveAsXML(PrintStream _stream,File _file,boolean _relative)
    {
        if (_stream == null) return;
        String s_str = "<"+XML_RESOURCE_INFO+">";
        _stream.println(s_str);
        String s_file = p_SoundFile == null ? "" : _relative ? Utilities.calcRelativePath(_file,p_SoundFile) : p_SoundFile.getAbsolutePath();
        s_str="<"+XML_SOUND+" "+XML_FILE+"=\""+s_file+"\"/>";
        _stream.println(s_str);
        _stream.println("</"+XML_RESOURCE_INFO+">");
    }

    public void _loadFromXML(File _file,Element _element) throws IOException
    {
        NodeList p_images =  _element.getElementsByTagName(XML_SOUND);
        if (p_images.getLength() == 0) throw new IOException("<"+XML_SOUND+"> format error");
        Element p_image = (Element)p_images.item(0);
        String s_fileName = p_image.getAttribute(XML_FILE);

        File p_audioFile = Utilities.isAbsolutePath(s_fileName) ? new File(s_fileName) : new File(_file,s_fileName);
        setAudioFile(p_audioFile);
    }
}

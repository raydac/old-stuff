package com.raydac_research.FormEditor.RrgFormResources;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import com.raydac_research.FormEditor.Misc.Utilities;
import com.raydac_research.Font.AbstractFont;
import com.raydac_research.Font.BFTFont;
import com.raydac_research.Font.TTFFont;

public class RrgResource_Font extends AbstractRrgResource
{
    protected File p_FontFile;
    protected AbstractFont p_Font;
    protected File p_FontFile_trans;
    protected AbstractFont p_Font_trans;

    protected static final String XML_FONT = "font";
    protected static final String XML_FILE = "file";
    protected static final String XML_BOLD = "bold";
    protected static final String XML_ITALIC = "italic";
    protected static final String XML_UNDERLINE = "underline";
    protected static final String XML_SIZE = "size";

    public boolean refreshResource() throws IOException
    {
        setFontFile(p_FontFile,p_Font.isBold(),p_Font.isItalic(),p_Font.isUnderline(),p_Font.getSize());
        return true;
    }

    public void startTransaction()
    {
        super.startTransaction();
        p_Font_trans = (AbstractFont) p_Font.clone();
        p_FontFile_trans = p_FontFile;
    }

    public void rollbackTransaction()
    {
        super.rollbackTransaction();

        p_Font = p_Font_trans;
        p_FontFile = p_FontFile_trans;
    }

    public RrgResource_Font(String _id,File _font,boolean _bold,boolean _italic,boolean _underline,int _size) throws IOException
    {
        super(_id,TYPE_FONT);
        setFontFile(_font,_bold,_italic,_underline,_size);
    }

    public AbstractFont getFont()
    {
        return p_Font;
    }

    public File getFontFile()
    {
        return p_FontFile;
    }

    public void setFontFile(File _file,boolean _bold,boolean _italic,boolean _underline,int _size) throws IOException
    {
        if (_file == null)
        {
            p_Font = null;
        }
        else
        {
            BFTFont p_bftFont = new BFTFont();
            TTFFont p_ttfFont = new TTFFont();

            if (p_bftFont.loadForntFromFile(_file))
            {
                p_Font = p_bftFont;
                p_FontFile = _file;
            }
            else
            if (p_ttfFont.loadForntFromFile(_file))
            {
                p_Font = p_ttfFont;
                p_FontFile = _file;
            }
            else
            {
                throw new IOException("Unsupported font format");
            }

            p_Font.setBold(_bold);
            p_Font.setItalic(_italic);
            p_Font.setSize(_size);
            p_Font.setUnderline(_underline);
        }
    }

    public void _saveAsXML(PrintStream _stream,File _file,boolean _relative)
    {
        if (_stream == null) return;
        String s_str = "<"+XML_RESOURCE_INFO+">";
        _stream.println(s_str);
        String s_file = p_FontFile == null ? "" : _relative ? Utilities.calcRelativePath(_file,p_FontFile) : p_FontFile.getAbsolutePath();
        s_str="<"+XML_FONT+" "+XML_FILE+"=\""+s_file+"\" "+ (p_Font.isBold() ? (XML_BOLD+"=\"1\""):"")+" "+(p_Font.isItalic() ? (XML_ITALIC+"=\"1\""):"")+" "+(p_Font.isUnderline() ? (XML_UNDERLINE+"=\"1\""):"")+" "+XML_SIZE+"=\""+p_Font.getSize()+"\""+"/>";
        _stream.println(s_str);
        _stream.println("</"+XML_RESOURCE_INFO+">");
    }

    public void _loadFromXML(File _file,Element _element) throws IOException
    {
        NodeList p_images =  _element.getElementsByTagName(XML_FONT);
        if (p_images.getLength() == 0) throw new IOException("<"+XML_FONT+"> format error");
        Element p_image = (Element)p_images.item(0);
        String s_fileName = p_image.getAttribute(XML_FILE);

        boolean lg_bold = p_image.getAttribute(XML_BOLD).length()!=0 ? true : false;
        boolean lg_italic = p_image.getAttribute(XML_ITALIC).length()!=0 ? true : false;
        boolean lg_underline = p_image.getAttribute(XML_UNDERLINE).length()!=0 ? true : false;

        String s_size = p_image.getAttribute(XML_SIZE);
        int i_size = 8;
        try
        {
            i_size = Integer.parseInt(s_size.trim());
            if (i_size<=0) i_size = 8;
            if (i_size>128) i_size = 72;
        }
        catch(NumberFormatException ex)
        {
        }

        File p_fontFile = Utilities.isAbsolutePath(s_fileName) ? new File(s_fileName) : new File(_file,s_fileName);
        setFontFile(p_fontFile,lg_bold,lg_italic,lg_underline,i_size);
    }
}

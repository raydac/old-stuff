package com.raydac_research.CatalogCompiler;

import org.w3c.dom.Element;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

public class CompatibleRecord
{
    public int i_Vendor;
    public int [] ai_Models;
    public int i_Offset;

    public CompatibleRecord(Element _compatible,StringsPool _pool) throws IOException
    {
        String s_str = _compatible.getAttribute(CatalogContainer.XML_ATTR_VENDOR).trim();
        String s_models = _compatible.getAttribute(CatalogContainer.XML_ATTR_MODELS);
        if (s_str.length()==0 || s_models.length()==0) throw new IOException("You have bad defined a \"compatible\" tag");

        i_Vendor = _pool.addString(s_str);


        // разбираем модели
        StringTokenizer p_tokenizer = new StringTokenizer(s_models,",");
        Vector p_mdl = new Vector();
        while(p_tokenizer.hasMoreTokens())
        {
            String s_token = p_tokenizer.nextToken();
            s_token = s_token.trim();
            p_mdl.add(s_token);
        }
        ai_Models = new int[p_mdl.size()];
        for(int li=0;li<p_mdl.size();li++)
        {
            ai_Models[li] = _pool.addString((String)p_mdl.elementAt(li));
        }
        p_mdl.clear();
    }

    public boolean equals(Object _obj)
    {
        if (!(_obj instanceof CompatibleRecord)) return false;

        CompatibleRecord p_rec = (CompatibleRecord) _obj;

        if (i_Vendor!=p_rec.i_Vendor) return false;

        if (ai_Models.length!=p_rec.ai_Models.length) return false;

        for(int li=0;li<ai_Models.length;li++)
        {
            if (ai_Models[li]!=p_rec.ai_Models[li])return false;
        }
        return true;
    }

 }


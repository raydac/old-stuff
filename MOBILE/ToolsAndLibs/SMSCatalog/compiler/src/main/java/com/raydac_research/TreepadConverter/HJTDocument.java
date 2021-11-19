package com.raydac_research.TreepadConverter;

import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;

public class HJTDocument
{
    private static final String HEADER1 = "<Treepad version 4.0>";
    private static final String HEADER2 = "<hj-Treepad version 2.7>";
    private static final String ID = "id=";

    private Vector p_NodeList;

    public int getNodesNumber()
    {
        return p_NodeList.size();
    }

    public HJTNode getNodeForIndex(int _index)
    {
        if (_index<0 || _index>=p_NodeList.size()) return null;
        return (HJTNode)p_NodeList.elementAt(_index);
    }

    public HJTDocument(InputStream _stream) throws IOException
    {
        p_NodeList = new Vector(64);

        BufferedReader p_reader = new BufferedReader(new InputStreamReader(_stream));

        // Ищем HEADER
        while(true)
        {
            String s_str = p_reader.readLine();
            if (s_str == null) throw new IOException("It's not a Treepad document");
            s_str = s_str.trim();
            if (s_str.length()!=0)
            {
                if (s_str.equals(HEADER1) || s_str.equals(HEADER2)) break;
            }
        }

        // Читаем ноды
        while(true)
        {
            String s_str = p_reader.readLine();
            if (s_str==null) break;
            if (s_str.startsWith(ID))
            {
                int i_id = Integer.parseInt(s_str.substring(ID.length()).trim());
                HJTNode p_newNode = new HJTNode(p_reader,i_id);
                p_NodeList.add(p_newNode);
                System.out.println("Node title = "+p_newNode.getNodeTitle());
            }
        }
    }
}

package com.raydac_research.TreepadConverter;

import java.io.BufferedReader;
import java.io.IOException;

public class HJTNode
{
    private static final String NODE_START = "<node>";
    private static final String NODE_END = "<end node> 5P9i0s8y19Z";
    private static final String DT = "dt=";

    protected int i_NodeID;
    protected String s_DataTypeIndicator;
    protected String s_NodeTitle;
    protected int i_LevelIndicator;
    protected String s_NodeText;

    public String toString()
    {
        String s_str = "NodeID="+i_NodeID+",DataType="+s_DataTypeIndicator+",Title="+s_NodeTitle+",Level="+i_LevelIndicator+",Text="+s_NodeText;
        return s_str;
    }


    public int getNodeID()
    {
        return i_NodeID;
    }

    public int getNodeLevel()
    {
        return i_LevelIndicator;
    }

    public String getDataTypeIndicator()
    {
        return s_DataTypeIndicator;
    }

    public String getNodeTitle()
    {
        return s_NodeTitle;
    }

    public String getNodeText()
    {
        return s_NodeText;
    }

    public HJTNode(BufferedReader _reader,int _id) throws IOException
    {
        i_NodeID = _id;
        s_DataTypeIndicator = "plain text";

        // Считываем тип
        StringBuffer p_strBuff = new StringBuffer(256);

        final int NODE_WAIT = 0;
        final int NODE_TITLE = 1;
        final int NODE_LEVEL = 2;
        final int NODE_TEXT = 3;

        int i_nodeMode = NODE_WAIT;

        boolean lg_work = true;

        while(lg_work)
        {
            String s_str = _reader.readLine();
            if (s_str==null) throw new IOException("Wrong node data");

            switch(i_nodeMode)
            {
                case NODE_WAIT :
                {
                    if (s_str.startsWith(DT))
                    {
                        s_DataTypeIndicator = s_str.substring(DT.length()).trim();
                    }
                    else
                    if (s_str.startsWith(NODE_START))
                    {

                        i_nodeMode = NODE_TITLE;
                    }

                };break;
                case NODE_TITLE :
                {
                    s_NodeTitle = s_str;
                    i_nodeMode = NODE_LEVEL;
                };break;
                case NODE_LEVEL :
                {
                    i_LevelIndicator = Integer.parseInt(s_str.trim());
                    i_nodeMode = NODE_TEXT;
                };break;
                case NODE_TEXT :
                {
                    if (s_str.startsWith(NODE_END))
                    {
                        lg_work = false;
                    }
                    else
                    {
                        if (p_strBuff.length()>0) p_strBuff.append('\n');
                        p_strBuff.append(s_str);
                    }
                };break;
            }
        }
        s_NodeText = p_strBuff.toString();
        if (s_NodeText.length()==0) s_NodeText = null;
    }
}

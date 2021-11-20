package com.raydac_research.TreepadConverter;

import java.io.PrintStream;
import java.io.IOException;
import java.util.Vector;

public class HJT2SMSCatalog
{
    private static class CatalogNode
    {
        public HJTNode p_Node;
        public Vector p_Leafs;
        public CatalogNode p_Parent;
        public int i_Level;

        public boolean lg_Printed;

        public CatalogNode(HJTNode _node)
        {
            i_Level = _node.getNodeLevel();
            p_Node = _node;
            p_Leafs = new Vector();
            p_Parent = null;
            lg_Printed = false;

        }

        public CatalogNode getParent()
        {
            return p_Parent;
        }

        public void addLeaf(CatalogNode _node)
        {
            _node.p_Parent = this;
            p_Leafs.add(_node);
        }

        public HJTNode getNode()
        {
            return p_Node;
        }

        public int getChildrenNumber()
        {
            return p_Leafs.size();
        }

        public CatalogNode getChildAt(int _index)
        {
            return (CatalogNode)p_Leafs.elementAt(_index);
        }

        public boolean hasMixedNodes()
        {
            System.out.println("+"+p_Node.getNodeTitle());
            boolean lg_content = false;
            boolean lg_screen = false;
            for(int li=0;li<p_Leafs.size();li++)
            {
                CatalogNode p_node = (CatalogNode)p_Leafs.elementAt(li);
                if (p_node.p_Node.getNodeText()==null)
                {
                    lg_screen = true;
                    System.out.println("---"+p_node.p_Node.getNodeTitle()+"=SCREEN");
                }
                else
                {
                    lg_content = true;
                    System.out.println("---"+p_node.p_Node.getNodeTitle()+"=CONTENT");
                }
            }
            if (lg_content && lg_screen) return true;
            return false;
        }
    }

    private static String convertForXML(String _str)
    {
        if (_str==null) return null;
        _str = _str.replace('“', '"');
        _str = _str.replace('”', '"');
        _str = _str.replace('«', '"');
        _str = _str.replace('»', '"');
        _str = _str.replace("&","&amp;");
        _str = _str.replace("\"","&quot;");
        _str = _str.replace("<","&lt;");
        _str = _str.replace(">","&rt;");
        _str = _str.replace("…","...");
        _str = _str.replace('–','-');
        _str = _str.replace('—','-');
        _str = _str.replace('ё','е');
        _str = _str.replace('Ё','Е');
        _str = _str.replace((char)0x2019,'\'');
        return _str;
    }

    public static void convert(PrintStream _outStream,HJTDocument _document) throws IOException
    {
        // Заполняем дерево
        int i_size = _document.getNodesNumber();
        if (i_size==0) return;

        // Добавляем корневой
        CatalogNode p_currentNode = new CatalogNode(_document.getNodeForIndex(0));

        Vector p_vec = new Vector();
        p_vec.add(p_currentNode);

        for(int li=1;li<i_size;li++)
        {
            HJTNode p_node = _document.getNodeForIndex(li);
            CatalogNode p_catnode = new CatalogNode(p_node);
            p_vec.add(p_catnode);
            if (p_currentNode.i_Level>=p_catnode.i_Level)
            {
                while(true)
                {
                    p_currentNode = p_currentNode.p_Parent;
                    if (p_currentNode == null) throw new IOException("Error tree graph, I have found null node");
                    if (p_currentNode.i_Level<p_catnode.i_Level)
                    {
                        p_currentNode.addLeaf(p_catnode);
                        p_currentNode = p_catnode;
                        break;
                    }
                }
            }
            else
            {
                p_currentNode.addLeaf(p_catnode);
                p_currentNode = p_catnode;
            }
        }

        // Проверяем пригодность для SMS каталога
        for(int li=0;li<p_vec.size();li++)
        {
            CatalogNode p_node = (CatalogNode) p_vec.elementAt(li);
            if (p_node.hasMixedNodes()) throw new IOException("You have mixed content for \'"+p_node.getNode().getNodeTitle()+"\'");
        }

        // Формируем выходной XML
        _outStream.println("<?xml version=\"1.0\" encoding=\"windows-1251\"?>");
        _outStream.println("<Catalog client=\"coldcore.ru\">");
        _outStream.println("<fuzzyiteminfo/>");
        _outStream.println("<savestringsasbytes/>");

        // Записываем экраны

        for(int li=0;li<p_vec.size();li++)
        {
            CatalogNode p_catNode = (CatalogNode) p_vec.elementAt(li);
            if (p_catNode.lg_Printed) continue;
            _outStream.print("\t<scr id=\""+p_catNode.getNode().i_NodeID+"\" ");
            _outStream.println("name=\""+p_catNode.getNode().getNodeTitle()+"\">");

            for(int lx=0;lx<p_catNode.getChildrenNumber();lx++)
            {
                CatalogNode p_nde=p_catNode.getChildAt(lx);
                String s_text = convertForXML(p_nde.getNode().getNodeText());
                String s_title = convertForXML(p_nde.getNode().getNodeTitle());

                if (s_text==null)
                {
                    // Линк
                    _outStream.println("\t\t<link name=\""+s_title+"\" scr=\""+p_nde.getNode().i_NodeID+"\"/>");
                }
                else
                {
                    // Объект
                    p_nde.lg_Printed = true;
                    s_text = s_text.replace("\r\n","~");
                    s_text = s_text.replace("\n\r","~");
                    s_text = s_text.replace('\n','~');
                    _outStream.println("\t\t<item type=\"INFO\" name=\""+s_title+"\" ref=\""+s_text+"\"/>");
                }
            }

            _outStream.println("\t</scr>");
        }


        _outStream.println("</Catalog>");
    }
}

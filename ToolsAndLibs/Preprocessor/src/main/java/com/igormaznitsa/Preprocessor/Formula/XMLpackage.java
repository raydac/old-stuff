package com.igormaznitsa.Preprocessor.Formula;

import com.igormaznitsa.Preprocessor.Formula.Value;
import com.igormaznitsa.Preprocessor.Formula.FormulaStack;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Vector;
import java.io.IOException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class XMLpackage
{
    //xml_open+
    //xml_getDocumentElement+
    //xml_getElemenName+
    //xml_getElementsForName+
    //xml_elementsNumber+
    //xml_elementAt+
    //xml_getAttribute+

    private static Vector p_openedXMLdocuments;
    private static Vector p_elementsVector;
    private static Vector p_nodeListVector;

    static
    {
        init();
    }

    private static final int getElementIndex(Element _element) throws IOException
    {
        if (_element==null) throw new IOException("Null element has been founded");
        int i_index =  p_elementsVector.indexOf(_element);
        if (i_index<0)
        {
            i_index = p_elementsVector.size();
            p_elementsVector.addElement(_element);
        }
        return i_index;
    }

    private static final int getNodeListIndex(NodeList _nodeList)
    {
        int i_index =  p_nodeListVector.indexOf(_nodeList);
        if (i_index<0)
        {
            i_index = p_nodeListVector.size();
            p_nodeListVector.addElement(_nodeList);
        }
        return i_index;
    }

    public static final void init()
    {
        p_openedXMLdocuments = new Vector(8);
        p_elementsVector = new Vector(8);
        p_nodeListVector = new Vector(8);
    }

    public static final void release()
    {
        p_openedXMLdocuments = null;
        p_elementsVector = null;
        p_nodeListVector = null;
    }

    // Обработка функции xml_open
    public static final void processXML_OPEN(FormulaStack _stack,int _index) throws IOException
    {
        if (!_stack.isOnePreviousItemValue(_index)) throw new IOException("Operation XML_OPEN needs an operand");

        Value _val0 = (Value)_stack.elementAt(_index-1);
        _index--;
        _stack.removeElementAt(_index);

        switch (_val0.getType())
        {
            case Value.TYPE_STRING:
                {
                    String s_result = (String) _val0.getValue();

                    DocumentBuilderFactory p_dbf = DocumentBuilderFactory.newInstance();
                    p_dbf.setIgnoringComments(true);

                    Document p_doc = null;
                    try
                    {
                        DocumentBuilder p_db = p_dbf.newDocumentBuilder();
                        p_doc = p_db.parse(s_result);
                    }
                    catch (Exception e)
                    {
                        throw new IOException("Inside function error ["+e.getMessage()+"]");
                    }

                    int i_index = p_openedXMLdocuments.size();
                    p_openedXMLdocuments.addElement(p_doc);

                    _stack.setElementAt(new Value(new Long(i_index)),_index);
                };break;
            default :
                throw new IOException("Function XML_OPEN processes only the STRING types");
        }

    }

    // Обработка функции xml_getDocumentElement
    public static final void processXML_GETDOCUMENTELEMENT(FormulaStack _stack,int _index) throws IOException
    {
        if (!_stack.isOnePreviousItemValue(_index)) throw new IOException("Operation XML_GETDOCUMENTELEMENT needs an operand");

        Value _val0 = (Value)_stack.elementAt(_index-1);
        _index--;
        _stack.removeElementAt(_index);

        switch (_val0.getType())
        {
            case Value.TYPE_INT:
                {
                    long l_documentIndex = ((Long) _val0.getValue()).longValue();
                    if (l_documentIndex<0 || l_documentIndex>=p_openedXMLdocuments.size()) throw new IOException("Wrong index of XML document");

                    Document p_document = (Document) p_openedXMLdocuments.elementAt((int)l_documentIndex);
                    Element p_element = p_document.getDocumentElement();
                    int i_index  = getElementIndex(p_element);

                    _stack.setElementAt(new Value(new Long(i_index)),_index);
                };break;
            default :
                throw new IOException("Function XML_GETDOCUMENTELEMENT processes only the INTEGER types");
        }

    }

    // Обработка функции xml_getElementName
    public static final void processXML_GETELEMENTNAME(FormulaStack _stack,int _index) throws IOException
    {
        if (!_stack.isOnePreviousItemValue(_index)) throw new IOException("Operation XML_GETELEMENTNAME needs an operand");

        Value _val0 = (Value)_stack.elementAt(_index-1);
        _index--;
        _stack.removeElementAt(_index);

        switch (_val0.getType())
        {
            case Value.TYPE_INT:
                {
                    long l_elementIndex = ((Long) _val0.getValue()).longValue();
                    if (l_elementIndex<0 || l_elementIndex>=p_elementsVector.size()) throw new IOException("Wrong index of XML element.");

                    Element p_element = (Element)p_elementsVector.elementAt((int)l_elementIndex);
                    String s_name  = p_element.getNodeName();

                    if (s_name == null) s_name="";

                    _stack.setElementAt(new Value((Object)s_name),_index);
                };break;
            default :
                throw new IOException("Function XML_GETELEMENTNAME processes only the INTEGER types");
        }
    }

    // Обработка функции xml_getElementText
    public static final void processXML_GETELEMENTTEXT(FormulaStack _stack,int _index) throws IOException
    {
        if (!_stack.isOnePreviousItemValue(_index)) throw new IOException("Operation XML_GETELEMENTTEXT needs an operand");

        Value _val0 = (Value)_stack.elementAt(_index-1);
        _index--;
        _stack.removeElementAt(_index);

        switch (_val0.getType())
        {
            case Value.TYPE_INT:
                {
                    long l_elementIndex = ((Long) _val0.getValue()).longValue();
                    if (l_elementIndex<0 || l_elementIndex>=p_elementsVector.size()) throw new IOException("Wrong index of XML element.");

                    Element p_element = (Element)p_elementsVector.elementAt((int)l_elementIndex);
                    NodeList p_childNodes = p_element.getChildNodes();

                    StringBuffer p_strBuffer = new StringBuffer();
                    for(int li=0;li<p_childNodes.getLength();li++)
                    {
                        Node p_node = p_childNodes.item(li);
                        if (p_node instanceof Text) p_strBuffer.append(((Text)p_node).getData());
                    }

                    _stack.setElementAt(new Value((Object)p_strBuffer.toString()),_index);
                };break;
            default :
                throw new IOException("Function XML_GETELEMENTTEXT processes only the INTEGER types");
        }
    }

    // Обработка функции xml_elementsNumber
    public static final void processXML_ELEMENTSNUMBER(FormulaStack _stack,int _index) throws IOException
    {
        if (!_stack.isOnePreviousItemValue(_index)) throw new IOException("Operation XML_ELEMENTSNUMBER needs an operand");

        Value _val0 = (Value)_stack.elementAt(_index-1);
        _index--;
        _stack.removeElementAt(_index);

        switch (_val0.getType())
        {
            case Value.TYPE_INT:
                {
                    long l_listIndex = ((Long) _val0.getValue()).longValue();
                    if (l_listIndex<0 || l_listIndex>=p_nodeListVector.size()) throw new IOException("Wrong index of XML node list.");

                    NodeList p_nodeList = (NodeList)p_nodeListVector.elementAt((int)l_listIndex);

                    _stack.setElementAt(new Value(new Long(p_nodeList.getLength())),_index);
                };break;
            default :
                throw new IOException("Function XML_ELEMENTSNUMBER processes only the INTEGER types");
        }
    }

    // Обработка функции xml_getElementsForName
    public static final void processXML_GETELEMENTSFORNAME(FormulaStack _stack, int _index) throws IOException
    {
        if (!_stack.isTwoPreviousItemsValues(_index)) throw new IOException("Operation XML_GETELEMENTSFORNAME needs two operands");

        Value _val1 = (Value)_stack.elementAt(_index-1);
        _index--;
        _stack.removeElementAt(_index);

        Value _val0 = (Value)_stack.elementAt(_index-1);
        _index--;
        _stack.removeElementAt(_index);

        String s_tagName = "";
        Element p_element = null;

        switch (_val0.getType())
        {
            case Value.TYPE_INT:
                {
                    long l_index  = ((Long) _val0.getValue()).longValue();

                    if (l_index<0 || l_index>=p_elementsVector.size()) throw new IOException("Wrong index of XML element.["+l_index+":"+p_elementsVector.size()+"]");

                    p_element = (Element)p_elementsVector.elementAt((int)l_index);

                };break;
            default :
                throw new IOException("Function XML_GETELEMENTSFORNAME needs INTEGER type as the first operand");
        }

        switch (_val1.getType())
        {
            case Value.TYPE_STRING:
                {
                    s_tagName  = ((String) _val1.getValue());

                };break;
            default :
                throw new IOException("Function XML_GETELEMENTSFORNAME needs STRING type as the second operand");
        }

        try
        {
            NodeList p_nodeList = p_element.getElementsByTagName(s_tagName);
            int i_listIndex = getNodeListIndex(p_nodeList);

            _stack.setElementAt(new Value(new Long(i_listIndex)),_index);
        } catch (NullPointerException e)
        {
            throw new IOException("Strange error [s_tagName="+s_tagName+", p_element="+p_element+"]");
        }
    }


    // Обработка функции xml_elementAt
    public static final void processXML_ELEMENTAT(FormulaStack _stack, int _index) throws IOException
    {
        if (!_stack.isTwoPreviousItemsValues(_index)) throw new IOException("Operation XML_ELEMENTAT needs two operands");

        Value _val1 = (Value)_stack.elementAt(_index-1);
        _index--;
        _stack.removeElementAt(_index);

        Value _val0 = (Value)_stack.elementAt(_index-1);
        _index--;
        _stack.removeElementAt(_index);

        NodeList p_nodeList = null;
        long l_indexElement = 0;

        switch (_val0.getType())
        {
            case Value.TYPE_INT:
                {
                    long l_index  = ((Long) _val0.getValue()).longValue();

                    if (l_index<0 || l_index>=p_nodeListVector.size()) throw new IOException("Wrong index of XML node list.");

                    p_nodeList = (NodeList)p_nodeListVector.elementAt((int)l_index);

                };break;
            default :
                throw new IOException("Function XML_ELEMENTAT needs INTEGER type as the first operand");
        }

        switch (_val1.getType())
        {
            case Value.TYPE_INT:
                {
                    l_indexElement  = ((Long) _val1.getValue()).longValue();

                };break;
            default :
                throw new IOException("Function XML_ELEMENTAT needs INTEGER type as the second operand");
        }

        Element p_Element = (Element) p_nodeList.item((int)l_indexElement);
        long l_index = getElementIndex(p_Element);

        _stack.setElementAt(new Value(new Long(l_index)),_index);
    }

    // Обработка функции xml_getAttribute
    public static final void processXML_GETATTRIBUTE(FormulaStack _stack, int _index) throws IOException
    {
        if (!_stack.isTwoPreviousItemsValues(_index)) throw new IOException("Operation XML_GETATTRIBUTE needs two operands");

        Value _val1 = (Value)_stack.elementAt(_index-1);
        _index--;
        _stack.removeElementAt(_index);

        Value _val0 = (Value)_stack.elementAt(_index-1);
        _index--;
        _stack.removeElementAt(_index);

        Element p_element = null;
        String s_attribute = "";

        switch (_val0.getType())
        {
            case Value.TYPE_INT:
                {
                    long l_index  = ((Long) _val0.getValue()).longValue();

                    if (l_index<0 || l_index>=p_elementsVector.size()) throw new IOException("Wrong index of XML element.");

                    p_element = (Element)p_elementsVector.elementAt((int)l_index);

                };break;
            default :
                throw new IOException("Function XML_GETATTRIBUTE needs INTEGER type as the first operand");
        }

        switch (_val1.getType())
        {
            case Value.TYPE_STRING:
                {
                    s_attribute  = ((String) _val1.getValue());

                };break;
            default :
                throw new IOException("Function XML_GETATTRIBUTE needs STRING type as the second operand");
        }

        String s_value = p_element.getAttribute(s_attribute);

        _stack.setElementAt(new Value((Object)s_value),_index);
    }
}

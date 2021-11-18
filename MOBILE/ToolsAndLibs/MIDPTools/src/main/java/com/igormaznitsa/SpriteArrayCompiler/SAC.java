package com.igormaznitsa.SpriteArrayCompiler;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.*;
import java.util.Vector;
import java.util.HashSet;

public class SAC
{
    private static final int SPRITERECORDCELLLENGTH = 11;

    protected static final String XML_MAIN_TAG_NAME = "sprites";
    protected static final String XML_SPRITE_TAG_NAME = "sprite";
    protected static final String XML_STATE_TAG_NAME = "state";
    protected static final String XML_WIDTH_TAG_NAME = "width";
    protected static final String XML_HEIGHT_TAG_NAME = "height";
    protected static final String XML_HOTZONEOFFSETX_TAG_NAME = "hotzonex";
    protected static final String XML_HOTZONEOFFSETY_TAG_NAME = "hotzoney";
    protected static final String XML_HOTZONEWIDTH_TAG_NAME = "hotzonew";
    protected static final String XML_HOTZONEHEIGHT_TAG_NAME = "hotzoneh";
    protected static final String XML_FRAMES_TAG_NAME = "frames";
    protected static final String XML_ANIMATIONDELAY_TAG_NAME = "animationdelay";
    protected static final String XML_ANIMATIONTYPE_TAG_NAME = "animationtype";
    protected static final String XML_MAINPOINTX_TAG_NAME = "mainpointx";
    protected static final String XML_MAINPOINTY_TAG_NAME = "mainpointy";

    protected static final String ATTR_ANIMATIONTYPE_FROZEN = "FROZEN";
    protected static final String ATTR_ANIMATIONTYPE_CYCLIC = "CYCLIC";
    protected static final String ATTR_ANIMATIONTYPE_PENDULUM = "PENDULUM";
    protected static final String ATTR_ID = "id";
    protected static final String ATTR_VAL = "value";

    protected static final String ATTR_ANCHOR_TOP = "TOP";
    protected static final String ATTR_ANCHOR_LEFT = "LEFT";
    protected static final String ATTR_ANCHOR_BOTTOM = "BOTTOM";
    protected static final String ATTR_ANCHOR_RIGHT = "RIGHT";
    protected static final String ATTR_ANCHOR_CENTER = "CENTER";

    public static final int ANIMATIONTYPE_CYCLIC = 0;
    public static final int ANIMATIONTYPE_FROZEN = 1;
    public static final int ANIMATIONTYPE_PENDULUM = 2;

    private class State
    {
        public static final int SPRITEDATACELLS = 11;

        public String s_Name;
        public int i_Width;
        public int i_Height;
        public int i_HotzoneoffsetX;
        public int i_HotzoneoffsetY;
        public int i_HotzoneWidth;
        public int i_HotzoneHeight;
        public int i_Frames;
        public int i_AnimationDelay;
        public int i_AnimationType;
        public int i_MainPointX;
        public int i_MainPointY;

        public State p_LinkedState;

        public int i_ArrayOffset;

        public State(Element _elementState) throws Exception
        {
            s_Name = _elementState.getAttribute(ATTR_ID);
            String s_Width = getValueFromTag(_elementState,XML_WIDTH_TAG_NAME);
            String s_Height = getValueFromTag(_elementState,XML_HEIGHT_TAG_NAME);
            String s_HotzoneOffX = getValueFromTag(_elementState,XML_HOTZONEOFFSETX_TAG_NAME);
            String s_HotzoneOffY = getValueFromTag(_elementState,XML_HOTZONEOFFSETY_TAG_NAME);
            String s_HotzoneW = getValueFromTag(_elementState,XML_HOTZONEWIDTH_TAG_NAME);
            String s_HotzoneH = getValueFromTag(_elementState,XML_HOTZONEHEIGHT_TAG_NAME);
            String s_Frames = getValueFromTag(_elementState,XML_FRAMES_TAG_NAME);
            String s_AnimationDelay = getValueFromTag(_elementState,XML_ANIMATIONDELAY_TAG_NAME);
            String s_AnimationType = getValueFromTag(_elementState,XML_ANIMATIONTYPE_TAG_NAME);
            String s_MainPointX = getValueFromTag(_elementState,XML_MAINPOINTX_TAG_NAME);
            String s_MainPointY = getValueFromTag(_elementState,XML_MAINPOINTY_TAG_NAME);

            if (s_Width==null) throw new IOException("There is not any width info");
            if (s_Height==null) throw new IOException("There is not any height info");
            i_Width = Integer.parseInt(s_Width);
            i_Height = Integer.parseInt(s_Height);

            if (s_HotzoneOffX==null)
                i_HotzoneoffsetX = 0;
            else
                i_HotzoneoffsetX = Integer.parseInt(s_HotzoneOffX);

            if (s_HotzoneOffY==null)
                i_HotzoneoffsetY = 0;
            else
                i_HotzoneoffsetY = Integer.parseInt(s_HotzoneOffY);

            if (s_HotzoneW==null)
                i_HotzoneWidth = i_Width;
            else
                i_HotzoneWidth = Integer.parseInt(s_HotzoneW);

            if (s_HotzoneH==null)
                i_HotzoneHeight = i_Height;
            else
                i_HotzoneHeight = Integer.parseInt(s_HotzoneH);

            if (s_Frames==null)
                i_Frames = 1;
            else
                i_Frames = Integer.parseInt(s_Frames);

            if (s_AnimationDelay==null)
                i_AnimationDelay = 1;
            else
                i_AnimationDelay = Integer.parseInt(s_AnimationDelay);

            if (s_AnimationType == null)
            {
                i_AnimationType = ANIMATIONTYPE_CYCLIC;
            }
            else
            {
                if (s_AnimationType.equals(ATTR_ANIMATIONTYPE_FROZEN)) i_AnimationType = ANIMATIONTYPE_FROZEN;
                else
                if (s_AnimationType.equals(ATTR_ANIMATIONTYPE_PENDULUM)) i_AnimationType = ANIMATIONTYPE_PENDULUM;
                else
                if (s_AnimationType.equals(ATTR_ANIMATIONTYPE_CYCLIC)) i_AnimationType = ANIMATIONTYPE_CYCLIC;
                else throw new IOException("Unsupported animation type ["+s_AnimationType+']');
            }

            if (s_MainPointX==null)
            {
                i_MainPointX = 0;
            }
            else
            {
                if (s_MainPointX.equals(ATTR_ANCHOR_LEFT)) i_MainPointX = 0;
                else
                if (s_MainPointX.equals(ATTR_ANCHOR_RIGHT)) i_MainPointX = i_Width;
                else
                if (s_MainPointX.equals(ATTR_ANCHOR_CENTER)) i_MainPointX = i_Width/2;
                else
                    i_MainPointX = Integer.parseInt(s_MainPointX);
            }

            if (s_MainPointY==null)
            {
                i_MainPointY = 0;
            }
            else
            {
                if (s_MainPointY.equals(ATTR_ANCHOR_TOP)) i_MainPointY = 0;
                else
                if (s_MainPointY.equals(ATTR_ANCHOR_BOTTOM)) i_MainPointY = i_Height;
                else
                if (s_MainPointY.equals(ATTR_ANCHOR_CENTER)) i_MainPointY = i_Height/2;
                else
                    i_MainPointY = Integer.parseInt(s_MainPointY);
            }
        }

        public boolean equalsParameters(State _state)
        {
            if (_state==null) return false;

            if (i_AnimationDelay!=_state.i_AnimationDelay) return false;
            if (i_AnimationType!=_state.i_AnimationType) return false;
            if (i_Frames!=_state.i_Frames) return false;
            if (i_Height!=_state.i_Height) return false;
            if (i_Width!=_state.i_Width) return false;
            if (i_HotzoneHeight!=_state.i_HotzoneHeight) return false;
            if (i_HotzoneWidth!=_state.i_HotzoneWidth) return false;
            if (i_HotzoneoffsetX!=_state.i_HotzoneoffsetX) return false;
            if (i_HotzoneoffsetY!=_state.i_HotzoneoffsetY) return false;
            if (i_MainPointX!=_state.i_MainPointX) return false;
            if (i_MainPointY!=_state.i_MainPointY) return false;

            return true;
        }

        public boolean canBeSavedAsByte()
        {
            if (i_Width>255) return false;
            if (i_Height>255) return false;
            if (i_Frames>255) return false;
            if (i_AnimationDelay>255) return false;
            if (i_HotzoneHeight>255) return false;
            if (i_HotzoneWidth>255) return false;
            if (i_HotzoneoffsetX>255) return false;
            if (i_HotzoneoffsetY>255) return false;
            if (i_MainPointX>255 || i_MainPointX<0) return false;
            if (i_MainPointY>255 || i_MainPointY<0) return false;
            return true;
        }

        public String toString()
        {
            StringBuffer p_strBuff = new StringBuffer();
            p_strBuff.append("(short)");
            p_strBuff.append(i_Width);
            p_strBuff.append(',');
            p_strBuff.append("(short)");
            p_strBuff.append(i_Height);
            p_strBuff.append(',');
            p_strBuff.append("(short)");
            p_strBuff.append(i_HotzoneoffsetX);
            p_strBuff.append(',');
            p_strBuff.append("(short)");
            p_strBuff.append(i_HotzoneoffsetY);
            p_strBuff.append(',');
            p_strBuff.append("(short)");
            p_strBuff.append(i_HotzoneWidth);
            p_strBuff.append(',');
            p_strBuff.append("(short)");
            p_strBuff.append(i_HotzoneHeight);
            p_strBuff.append(',');
            p_strBuff.append("(short)");
            p_strBuff.append(i_Frames);
            p_strBuff.append(',');
            p_strBuff.append("(short)");
            p_strBuff.append(i_AnimationDelay);
            p_strBuff.append(',');
            p_strBuff.append("(short)");
            p_strBuff.append(i_AnimationType);
            p_strBuff.append(',');
            p_strBuff.append("(short)");
            p_strBuff.append(i_MainPointX);
            p_strBuff.append(',');
            p_strBuff.append("(short)");
            p_strBuff.append(i_MainPointY);

            return p_strBuff.toString();
        }

        public void saveToStream(DataOutputStream _stream,boolean _asByte) throws IOException
        {
            // width, height, hotzoneoffsetx, hotzoneoffsety, hotzonewidth, hotzoneheight, frames, animation_delay, animation_type, main_offsetx, main_offsety

            if (_asByte)
            {
                // Сохраняем как байты
                _stream.writeByte(i_Width);
                _stream.writeByte(i_Height);
                _stream.writeByte(i_HotzoneoffsetX);
                _stream.writeByte(i_HotzoneoffsetY);
                _stream.writeByte(i_HotzoneWidth);
                _stream.writeByte(i_HotzoneHeight);
                _stream.writeByte(i_Frames);
                _stream.writeByte(i_AnimationDelay);
                _stream.writeByte(i_AnimationType);
                _stream.writeByte(i_MainPointX);
                _stream.writeByte(i_MainPointY);
            }
            else
            {
                // Сохраняем как short
                _stream.writeShort(i_Width);
                _stream.writeShort(i_Height);
                _stream.writeShort(i_HotzoneoffsetX);
                _stream.writeShort(i_HotzoneoffsetY);
                _stream.writeShort(i_HotzoneWidth);
                _stream.writeShort(i_HotzoneHeight);
                _stream.writeShort(i_Frames);
                _stream.writeShort(i_AnimationDelay);
                _stream.writeShort(i_AnimationType);
                _stream.writeShort(i_MainPointX);
                _stream.writeShort(i_MainPointY);
            }
        }
    }

    private class Sprite
    {
        int i_IndexInTable;
        int i_OffsetStates;
        public State [] ap_States;
        public String s_ID;

        public Sprite(Element _elementSprite) throws Exception
        {
            s_ID  = _elementSprite.getAttribute(ATTR_ID);
            if (s_ID==null || s_ID.length()==0) throw new IOException("Can't find sprite id");
            NodeList p_states = _elementSprite.getElementsByTagName(XML_STATE_TAG_NAME);
            ap_States = new State[p_states.getLength()];
            HashSet p_names = new HashSet();
            for(int li=0;li<ap_States.length;li++)
            {
                Element p_elem = (Element) p_states.item(li);
                State p_st = new State(p_elem);
                if (p_names.contains(p_st.s_Name)) throw new IOException("You have duplicated state \'"+p_st.s_Name+"\' for sprite \'"+s_ID+"\'");
                p_names.add(p_st.s_Name);
                ap_States[li] = p_st;
            }
        }

        public boolean canBeSavedAsByte()
        {
            for(int li=0;li<ap_States.length;li++)
            {
                if (!ap_States[li].canBeSavedAsByte()) return false;
            }
            return true;
        }
    }

    private Sprite [] ap_Sprites;

    public SAC(InputStream _xmlInputStream) throws Exception
    {
            DocumentBuilderFactory p_dbf = DocumentBuilderFactory.newInstance();
            p_dbf.setIgnoringComments(true);
            DocumentBuilder p_db = p_dbf.newDocumentBuilder();

            Document p_doc = null;
            p_doc = p_db.parse(_xmlInputStream);
            Element p_mainelement = p_doc.getDocumentElement();

            if (!p_mainelement.getNodeName().equals(XML_MAIN_TAG_NAME)) throw new IOException("It's not a spites XML document");

            HashSet p_spriteNames = new HashSet();
            NodeList p_sprites = p_mainelement.getElementsByTagName(XML_SPRITE_TAG_NAME);
            ap_Sprites = new Sprite[p_sprites.getLength()];
            for(int li=0;li<ap_Sprites.length;li++)
            {
                Element p_sprElem = (Element)p_sprites.item(li);
                Sprite p_spr = new Sprite(p_sprElem);
                if (p_spriteNames.contains(p_spr.s_ID)) throw new IOException("You have duplicated sprite \'"+p_spr.s_ID+"\'");
                p_spriteNames.add(p_spr.s_ID);
                ap_Sprites[li] = p_spr;
            }
    }

    public void saveIntoStream(OutputStream _stream,PrintStream _javaStream,boolean _asJava) throws IOException
    {
        PrintStream p_asJavaStream = null;
        DataOutputStream p_asBinStream = null;
        if (_asJava)
            p_asJavaStream = new PrintStream(_stream);
        else
            p_asBinStream = new DataOutputStream(_stream);

        // Расставляем ссылки на повторяющиеся
        Vector p_vec = new Vector(120);

        int i_offstDataSpr = ap_Sprites.length;

        int i_objTablesOffset = ap_Sprites.length;

        for(int li=0;li<ap_Sprites.length;li++)
        {
            Sprite p_spr = ap_Sprites[li];
            p_spr.i_IndexInTable = li;
            State [] ap_states = p_spr.ap_States;

            p_spr.i_OffsetStates = i_objTablesOffset;
            i_objTablesOffset += ap_states.length;

            i_offstDataSpr += ap_states.length;
            for(int ll=0;ll<ap_states.length;ll++)
            {
                State p_state = ap_states[ll];
                p_state.p_LinkedState = null;

                for(int lk=0;lk<p_vec.size();lk++)
                {
                    State p_ste = (State)p_vec.elementAt(lk);
                    if (p_state.equalsParameters(p_ste))
                    {
                        p_state.p_LinkedState = p_ste;
                        break;
                    }
                }

                if (p_state.p_LinkedState==null)
                {
                    p_vec.add(p_state);
                }
            }
        }

        // Смещения у данных
        for(int li=0;li<p_vec.size();li++)
        {
            State p_ste = (State) p_vec.elementAt(li);
            p_ste.i_ArrayOffset = i_offstDataSpr;
            i_offstDataSpr += SPRITERECORDCELLLENGTH;
        }

        if (p_asBinStream!=null)
        {
            // Записываем бинарный блок

            // Размер массива
            // смещения состояний
            int i_size = ap_Sprites.length;
            // таблица состояний
            for(int li=0;li<ap_Sprites.length;li++)
            {
                i_size += ap_Sprites[li].ap_States.length;
            }
            int i_offsetsSize = i_size;

            // Данные спрайтов
            i_size += p_vec.size()*State.SPRITEDATACELLS;

            // Количество ячеек
            p_asBinStream.writeShort(i_size);

            // Количество смещений
            p_asBinStream.writeShort(i_offsetsSize);

            boolean lg_saveAsBytes = true;

            // Смещения
            for(int li=0;li<ap_Sprites.length;li++)
            {
                p_asBinStream.writeShort(ap_Sprites[li].i_OffsetStates);
                if (!ap_Sprites[li].canBeSavedAsByte()) lg_saveAsBytes = false;
            }

            if (lg_saveAsBytes)
            {
                System.out.println("Saved as bytes");
            }
            else
            {
                System.out.println("Saved as shorts");
            }

            // Состояния
            for(int li=0;li<ap_Sprites.length;li++)
            {
                for(int ll=0;ll<ap_Sprites[li].ap_States.length;ll++)
                {
                    State p_st = ap_Sprites[li].ap_States[ll];
                    if (p_st.p_LinkedState!=null) p_st = p_st.p_LinkedState;
                    p_asBinStream.writeShort(p_st.i_ArrayOffset);
                }
            }

            // Данные спрайтов
            p_asBinStream.writeBoolean(lg_saveAsBytes);
            for(int li=0;li<p_vec.size();li++)
            {
                State p_state = (State) p_vec.elementAt(li);

                p_state.saveToStream(p_asBinStream,lg_saveAsBytes);
            }
        }
        else
        if (p_asJavaStream!=null)
        {
            // Записываем текстовый блок

            p_asJavaStream.println("protected static final short [] ash_SpritesTable = new short[] {");

            // Таблица смещений состояний
            for(int li=0;li<ap_Sprites.length;li++)
            {
                String s_SpriteID = ap_Sprites[li].s_ID.trim().toUpperCase();
                p_asJavaStream.println("             // Object "+s_SpriteID);
                p_asJavaStream.println("             (short)"+ap_Sprites[li].i_OffsetStates+',');
            }
            // Табилца состояний
            for(int li=0;li<ap_Sprites.length;li++)
            {
                String s_SpriteID = ap_Sprites[li].s_ID.trim().toUpperCase();

                for(int ll=0;ll<ap_Sprites[li].ap_States.length;ll++)
                {
                    State p_st = ap_Sprites[li].ap_States[ll];
                    String s_stateID = p_st.s_Name.trim().toUpperCase();
                    p_asJavaStream.println("             // Object "+s_SpriteID+" state "+s_stateID);
                    if (p_st.p_LinkedState!=null) p_st = p_st.p_LinkedState;
                    p_asJavaStream.println("             (short)"+p_st.i_ArrayOffset+',');
                }
            }
            // Данные спрайтов
            for(int li=0;li<p_vec.size();li++)
            {
                State p_state = (State) p_vec.elementAt(li);
                p_asJavaStream.println("             "+p_state.toString()+",");
            }
            p_asJavaStream.println("                    };");
        }

        // Выводим константы
         final String PREFOBJ = "public static final int SPRITE_OBJ_";
         final String PREFSTATE = "public static final int SPRITE_STATE_";

        _javaStream.println("//------------------Sprite constants-----------------");
        for(int li=0;li<ap_Sprites.length;li++)
        {
            String s_spriteID = ap_Sprites[li].s_ID.trim().toUpperCase();
            _javaStream.print(PREFOBJ);
            _javaStream.print(s_spriteID);
            _javaStream.println(" = "+ap_Sprites[li].i_IndexInTable+";");

            // Состояния
            for(int ll=0;ll<ap_Sprites[li].ap_States.length;ll++)
            {
                State p_ste = ap_Sprites[li].ap_States[ll];
                String s_state = p_ste.s_Name.trim().toUpperCase();
                _javaStream.print(PREFSTATE);
                _javaStream.print(s_spriteID+"_"+s_state);
                _javaStream.println(" = "+ll+";");
            }
        }
    }


    public static String getValueFromTag(Element _element,String _tagName)
    {
        try
        {
            NodeList p_list = _element.getElementsByTagName(_tagName);
            Element p_elem = (Element)p_list.item(0);
            String s_attr = p_elem.getAttribute(ATTR_VAL);
            if (s_attr==null || s_attr.length()==0) return null;
            return s_attr;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static final void main(String [] _args)
    {
        String s_file = null;

        boolean lg_saveAsBin = false;
        String s_out = null;
        if (_args.length>1)
        {
            if (_args[0].equalsIgnoreCase("/b")) lg_saveAsBin = true;
            s_file = _args[1];
            s_out = s_file+".bin";
        }
        else
        {
            s_file = _args[0];
            s_out = s_file+".java";
        }

        try
        {
            FileInputStream p_inStr = new FileInputStream(s_file);
            SAC p_sac = new SAC(p_inStr);
            p_inStr.close();
            FileOutputStream p_fos = new FileOutputStream(s_out);
            FileOutputStream p_jos = null;

            if (lg_saveAsBin)
            {
                p_jos = new FileOutputStream(s_file+".java");
            }

            p_sac.saveIntoStream(p_fos,new PrintStream(p_jos == null ? p_fos : p_jos),!lg_saveAsBin);

            if (p_jos!=null)
            {
                p_jos.flush();
                p_jos.close();
            }

            p_fos.flush();
            p_fos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

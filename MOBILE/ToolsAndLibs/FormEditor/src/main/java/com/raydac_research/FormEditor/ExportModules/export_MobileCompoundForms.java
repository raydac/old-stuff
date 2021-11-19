package com.raydac_research.FormEditor.ExportModules;

import com.raydac_research.FormEditor.RrgFormComponents.*;
import com.raydac_research.FormEditor.RrgFormResources.ResourceContainer;
import com.raydac_research.FormEditor.RrgFormResources.RrgResource_Font;
import com.raydac_research.FormEditor.RrgFormResources.RrgResource_Text;
import com.raydac_research.PNGWriter.PNGEncoder;
import com.raydac_research.Font.GIFFont;
import com.raydac_research.Font.AbstractFont;

import javax.swing.filechooser.FileFilter;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.*;

public class export_MobileCompoundForms extends FileFilter implements AbstractFormExportModule
{
    /**
     * Версия формата
     */
    private static final int FORMAT_VERSION = 0x10;

    private JCheckBox p_CheckBox_ImagesOnly;

    private static final int FLAG_HASMODIFIEDIMAGES = 1;// флаг наличия модифицируемых изображений
    private static final int FLAG_IMAGES_ONLY = 2;// флаг, что хранятся только изображения
    private static final int FLAG_CHANNELDATA = 4;// флаг, что присутствует информация о канале
    private static final int FLAG_STRINGS = 8;// флаг, что присутствует информация о строках
    private static final int FLAG_FONTS = 16;// флаг, что присутствует информация о фонтах
    private static final int FLAG_HASANCHOR = 32;// флаг, что присутствует информация о графических якорях
    private static final int FLAG_PATHS = 64;// флаг, что присутствует информация о путях

    private static final int COMPONENTFLAG_OPTIONAL = 0x80;// флаг, компонент опциональный и требуется запрос на его вывод

    private JCheckBox p_CheckBox_SaveChannel;
    private JCheckBox p_CheckBox_SaveTextAsImages;
    private JCheckBox p_CheckBox_copyImagesOnDisk;
    private JCheckBox p_CheckBox_SaveAnchorInfo;
    private JCheckBox p_CheckBox_SavePaths;
    private JCheckBox checkBox1;


    private class MCFPath
    {
        public String s_ID;
        public MCFForm p_FormID;

        public int i_dataOffset;
        public RrgFormComponent_Path p_Path;

        public int i_Offset;

        public MCFPath(MCFForm _form,RrgFormComponent_Path _path) throws IOException
        {
            p_Path = _path;
            s_ID = _path.getID();
            p_FormID = _form;
        }

        public boolean canBeSavedAsByte(boolean _checkSteps)
        {
            if (p_Path.p_PointVector.size()>256) return false;
            if (p_Path.p_MainPoint.i_X<0 || p_Path.p_MainPoint.i_X>255) return false;

            Vector p_Points =  p_Path.p_PointVector;
            for(int li=0;li<p_Points.size();li++)
            {
                PathPoint p_pathPoint = (PathPoint) p_Points.elementAt(li);
                int i_x = p_pathPoint.i_X;
                int i_y = p_pathPoint.i_Y;
                int i_step = p_pathPoint.i_Steps;

                if (i_x<0 || i_x>255) return false;
                if (i_y<0 || i_y>255) return false;
                if (_checkSteps)
                    if (i_step>255) return false;
            }

            return true;
        }

        public int saveToStream(DataOutputStream _stream,int _offset) throws IOException
        {
            i_Offset = _offset;
            final int PATHFLAG_SHORT = 1;
            final int PATHFLAG_HASBOUNDARYINFO = 2;
            final int PATHFLAG_HASMAINPOINTINFO = 4;
            final int PATHFLAG_HASSTEPSINFO = 8;

            final int EVENT_EVERYPOINT = 0x10;
            final int EVENT_ENDPOINT = 0x20;

            final int PATHTYPE_NORMAL = 0;
            final int PATHTYPE_PENDULUM = 1;
            final int PATHTYPE_CYCLED = 2;
            final int PATHTYPE_CYCLED_SMOOTH = 3;

            boolean lg_SaveAsBytes = canBeSavedAsByte(p_Path.lg_SaveSteps);

            int i_flag = !lg_SaveAsBytes ? PATHFLAG_SHORT : 0;
            i_flag |= p_Path.lg_SaveBoundiaryInfo ? PATHFLAG_HASBOUNDARYINFO : 0;
            i_flag |= p_Path.lg_SaveMainPointInfo ? PATHFLAG_HASMAINPOINTINFO : 0;
            i_flag |= p_Path.lg_SaveSteps ? PATHFLAG_HASSTEPSINFO : 0;

            _stream.writeByte(i_flag);

            int i_typePath = 0;
            switch(p_Path.i_PathType)
            {
                case RrgFormComponent_Path.PATH_NORMAL : i_typePath = PATHTYPE_NORMAL;break;
                case RrgFormComponent_Path.PATH_PENDULUM : i_typePath = PATHTYPE_PENDULUM;break;
                case RrgFormComponent_Path.PATH_CYCLIC : i_typePath = PATHTYPE_CYCLED;break;
                case RrgFormComponent_Path.PATH_CYCLIC_SMOOTH : i_typePath = PATHTYPE_CYCLED_SMOOTH;break;
            }

            i_typePath |= p_Path.lg_NotifyOnEndPoint ? EVENT_ENDPOINT:0;
            i_typePath |= p_Path.lg_NotifyOnEveryPoint ? EVENT_EVERYPOINT:0;

            _stream.writeByte(i_typePath);

            _offset++;

            // Информация о границах
            if (p_Path.lg_SaveBoundiaryInfo)
            {
                Dimension p_dim = p_Path.getPathDimension();
                if (lg_SaveAsBytes)
                {
                    _stream.writeByte(p_dim.width);
                    _stream.writeByte(p_dim.height);
                }
                else
                {
                    _stream.writeShort(p_dim.width);
                    _stream.writeShort(p_dim.height);
                }
                _offset+=2;
            }

            // Информация о main point
            if (p_Path.lg_SaveMainPointInfo)
            {
                PathPoint p_point = p_Path.p_MainPoint;
                if (lg_SaveAsBytes)
                {
                    _stream.writeByte(p_point.i_X);
                    _stream.writeByte(p_point.i_Y);
                }
                else
                {
                    _stream.writeShort(p_point.i_X);
                    _stream.writeShort(p_point.i_Y);
                }
                _offset+=2;
            }

            // Количество точек пути
            _stream.writeByte(p_Path.p_PointVector.size()-1);
            _offset++;
            // Точки пути
            for(int li=0;li<p_Path.p_PointVector.size();li++)
            {
                PathPoint p_point = (PathPoint) p_Path.p_PointVector.elementAt(li);
                if (lg_SaveAsBytes)
                {
                    _stream.writeByte(p_point.i_X);
                    _stream.writeByte(p_point.i_Y);
                }
                else
                {
                    _stream.writeShort(p_point.i_X);
                    _stream.writeShort(p_point.i_Y);
                }
                _offset+=2;

                // Записываем количество шагов
                if (p_Path.lg_SaveSteps)
                {
                    if (lg_SaveAsBytes)
                    {
                        _stream.writeByte(p_point.i_Steps);
                    }
                    else
                    {
                        _stream.writeShort(p_point.i_Steps);
                    }
                    _offset++;
                }
            }
            return _offset;
        }
    }


    private abstract class MCFComponent
    {
        public String s_componentID;
        public MCFForm p_parent;

        public int i_type;
        public int i_X;
        public int i_Y;
        public int i_Anchor;
        public int i_Channel;
        public int i_componentIndex;

        public boolean lg_Optional;

        public static final int MCFCOMPONENT_IMAGE = 0;
        public static final int MCFCOMPONENT_AREA = 1;
        public static final int MCFCOMPONENT_BUTTON = 2;
        public static final int MCFCOMPONENT_TEXTLABEL = 3;

        public MCFComponent(MCFForm _parentForm, AbstractFormComponent _component, int _index) throws IOException
        {
            p_parent = _parentForm;

            if (_component instanceof RrgFormComponent_Button) i_type = MCFCOMPONENT_BUTTON;
            else if (_component instanceof RrgFormComponent_CustomArea) i_type = MCFCOMPONENT_AREA;
            else if (_component instanceof RrgFormComponent_Image) i_type = MCFCOMPONENT_IMAGE;
            else if (_component instanceof RrgFormComponent_Label)
            {
                i_type = MCFCOMPONENT_TEXTLABEL;
            }
            else
                throw new IOException("Unsupported type of component");

            s_componentID = _component.getID();
            s_componentID = s_componentID.replace(' ', '_');
            i_componentIndex = _index;
            i_X = _component.getX();
            i_Y = _component.getY();

            i_Channel = _component.getChannel();
            lg_Optional = false;

            i_Anchor = checkAnchorForComponent(_component);

            if (i_Channel >= COMPONENTFLAG_OPTIONAL)
            {
                lg_Optional = true;
                i_Channel = 0x7F;
            }
        }

        protected int checkAnchorForComponent(AbstractFormComponent _component)
        {
            final int ANCHOR_LEFT = 4;
            final int ANCHOR_RIGHT = 8;
            final int ANCHOR_TOP = 16;
            final int ANCHOR_BOTTOM = 32;

            int i_result = 0;

            if (i_X < 0)
            {
                i_result = ANCHOR_RIGHT;
                i_X += _component.getWidth();
            }
            else
            {
                i_result = ANCHOR_LEFT;
            }

            if (i_Y < 0)
            {
                i_result |= ANCHOR_BOTTOM;
                i_Y += _component.getHeight();
            }
            else
            {
                i_result |= ANCHOR_TOP;
            }

            return i_result;
        }

        private final int ANCHOR_LEFT = 4;
        private final int ANCHOR_RIGHT = 8;
        private final int ANCHOR_TOP = 16;
        private final int ANCHOR_BOTTOM = 32;

        protected int checkAnchorForImage(BufferedImage _image)
        {
            int i_result = 0;

            if (i_X < 0)
            {
                i_result = ANCHOR_RIGHT;
                i_X += _image.getWidth();
            }
            else
            {
                i_result = ANCHOR_LEFT;
            }

            if (i_Y < 0)
            {
                i_result |= ANCHOR_BOTTOM;
                i_Y += _image.getHeight();
            }
            else
            {
                i_result |= ANCHOR_TOP;
            }

            return i_result;
        }

        public void saveToStream(DataOutputStream _outStream, Hashtable _imageSet, boolean _saveModifiedImages, boolean _saveImagesOnly, boolean _saveChannelData, PrintStream _javaStream,boolean _saveAnchorInfo) throws IOException
        {
            int i_componentDataOffset = _outStream.size();
            // Записываем тип объекта
            if (!_saveImagesOnly)
                _outStream.writeByte(lg_Optional ? i_type | COMPONENTFLAG_OPTIONAL : i_type);

            // Записываем X
            _outStream.writeByte(i_X);
            // Записываем Y
            _outStream.writeByte(i_Y);

            // Записываем Канал
            if (_saveChannelData)
            {
                _outStream.writeByte(i_Channel);
            }

            // Записываем Якорь
            if (_saveAnchorInfo)
            {
                _outStream.writeByte(i_Anchor);
            }
            else
                if (i_Anchor!=(ANCHOR_TOP | ANCHOR_LEFT)) throw new IOException("You have to save anchor info");

            _javaStream.println("   protected static final int MCF_CMP_" + p_parent.s_formID + "_" + s_componentID + " = " + i_componentIndex + ';');
            _javaStream.println("   protected static final int MCF_CMP_" + p_parent.s_formID + "_" + s_componentID + "_OFFSET = " + i_componentDataOffset + ';');
        }
    }

    private class MCFFont
    {
        public int i_Index;
        public String s_ID;
        public AbstractFont p_resourceFont;
        public MCFImage p_fontImage;
        public int i_horzInterval;
        public int i_vertInterval;
        public int i_CharWidth;
        public int i_CharHeight;

        private final BufferedImage makeFontImageFromImage(GIFFont _srcImage)
        {
            int i_origCharW = _srcImage.i_charWidth;
            int i_origCharH = _srcImage.i_charHeight;

            // Анализируем максимальный требуемый размер для букв
            int i_charsNumber = _srcImage.i_CharsNumber;

            int i_charW = i_origCharW-_srcImage.i_Interval;
            int i_charH = _srcImage.getBaseline();

            Image p_srcImage = _srcImage.p_FontImage;

            i_horzInterval = _srcImage.i_Interval;
            i_vertInterval = i_origCharH - i_charH;

            i_CharWidth = i_charW;
            i_CharHeight = i_charH;

            int i_dstImageWidth = 16 * i_charW;
            int i_dstImageHeight = ((i_charsNumber + 15) / 16) * i_charH;

            BufferedImage p_dstImage = new BufferedImage(i_dstImageWidth, i_dstImageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics p_dstGraphics = p_dstImage.getGraphics();

            for (int li = 0; li < i_charsNumber; li++)
            {
                int i_charX = (li % 16) * i_charW;
                int i_charY = (li / 16) * i_charH;

                p_dstGraphics.setClip(i_charX, i_charY, i_charW, i_charH);
                p_dstGraphics.drawImage(p_srcImage, i_charX - (i_origCharW * li)-i_horzInterval, i_charY, null);
            }

            p_dstGraphics = null;

            return p_dstImage;
        }

        public MCFFont(RrgResource_Font _font, Hashtable _imageSet)
        {
            final String SUFFIX = "_FNTIMG";

            s_ID = _font.getResourceID();
            i_horzInterval = 0;
            i_vertInterval = 0;
            i_Index = -1;

            p_resourceFont = _font.getFont();

            if (isGIFFont())
            {
                String s_strID = "$" + s_ID + SUFFIX;
                if (!_imageSet.contains(s_strID))
                {
                    BufferedImage p_processedImage = makeFontImageFromImage((GIFFont) p_resourceFont);

                    MCFImage p_newmciimage = new MCFImage(s_strID, p_processedImage, 0);

                    _imageSet.put(s_strID,p_newmciimage);
                    p_fontImage = p_newmciimage;
                }
                else
                {
                        MCFImage p_img = (MCFImage) _imageSet.get(s_strID);
                        p_fontImage = p_img;
                }
            }
            else
                p_fontImage = null;
        }

        public boolean isGIFFont()
        {
            return (p_resourceFont instanceof GIFFont);
        }

        public int hashCode()
        {
            return s_ID.hashCode();
        }

        public boolean equals(Object _obj)
        {
            if (_obj == null) return false;
            if (_obj instanceof String)
            {
                if (((String) _obj).equals(s_ID)) return true;
                else return false;
            }

            if (_obj instanceof MCFFont)
            {
                if (((MCFFont) _obj).s_ID.equals(s_ID)) return true;
            }
            return false;
        }
    }

    private class MCFString
    {
        public int i_Index;
        public String s_ID;

        public byte[] ab_EncodedString;
        public String s_String;

        private boolean goodString(String _string)
        {
            final String s_charset = GIFFont.CHARSET;
            int i_len = _string.length();
            for (int li = 0; li < i_len; li++)
            {
                char ch_char = _string.charAt(li);
                if (s_charset.indexOf(ch_char) < 0) return false;
            }
            return true;
        }

        public MCFString(String _id, String _resource) throws IOException
        {
            s_ID = _id;
            i_Index = -1;
            s_String = _resource;

            if (_resource == null) throw new IOException("NULL resource string");
            if (!goodString(_resource)) throw new IOException("String has wrong chars");

            ab_EncodedString = encodeString(_resource);
        }

        public int hashCode()
        {
            return s_ID.hashCode();
        }

        public boolean equals(Object _obj)
        {
            if (_obj == null) return false;
            if (_obj instanceof String)
            {
                if (((String) _obj).equals(s_ID)) return true;
                else return false;
            }

            if (_obj instanceof MCFString)
            {
                if (((MCFString) _obj).s_ID.equals(s_ID)) return true;
            }
            return false;
        }
    }


    private static class MCFImage
    {
        public int i_Index;
        public BufferedImage p_resourceImage;

        public static int i_InsideIndexCounter;

        public int i_InsideIndex;

        public int i_Modifiers;

        public String s_ID;

        public MCFImage(String _id, BufferedImage _resource, int _modifiers)
        {
            s_ID = _id;
            i_Index = -1;
            p_resourceImage = _resource;
            i_Modifiers = _modifiers;
            i_InsideIndex = i_InsideIndexCounter;
            i_InsideIndexCounter++;
        }

        public int hashCode()
        {
            return s_ID.hashCode();
        }

        public boolean equals(Object _obj)
        {
            if (_obj == null) return false;
            if (_obj instanceof String)
            {
                if (((String) _obj).equals(s_ID)) return true;
                else return false;
            }

            if (_obj instanceof MCFImage)
            {
                if (((MCFImage) _obj).s_ID.equals(s_ID)) return true;
            }
            return false;
        }
    }

    private class MCFImageComponent extends MCFComponent
    {
        public String s_ImageResourceID;
        public int i_modifiers;

        public MCFImageComponent(MCFForm _parent, RrgFormComponent_Image _component, int _index, Hashtable _imageResourcesSet, boolean _convertModifiedToStatic) throws IOException
        {
            super(_parent, _component, _index);

            i_modifiers = _component.getModifiers();
            if (_convertModifiedToStatic)
            {
                s_ImageResourceID = _component.getImageResource().getResourceID() + "$" + i_modifiers;
            }
            else
            {
                s_ImageResourceID = _component.getImageResource().getResourceID() + "$" + RrgFormComponent_Image.MODIFIER_NONE;
            }

            MCFImage p_img = null;
                if (!_convertModifiedToStatic)
                    p_img = new MCFImage(s_ImageResourceID, _component.getImageResource().getImage(), i_modifiers);
                else
                    p_img = new MCFImage(s_ImageResourceID, _component.getModifiedImage(), i_modifiers);

            if (!_imageResourcesSet.contains(s_ImageResourceID)) _imageResourcesSet.put(s_ImageResourceID,p_img);
        }

        public MCFImageComponent(MCFForm _parent, RrgFormComponent_Label _component, int _index, Hashtable _imageResourcesSet) throws IOException
        {
            super(_parent, _component, _index);

            i_modifiers = 0;
            s_ImageResourceID = _component.getLabelText().getResourceID() + "$" + _component.getLabelFont().getResourceID() + "$";

            MCFImage p_img = null;
            if (!_imageResourcesSet.contains(s_ImageResourceID))
            {
                BufferedImage p_madeImage = _component.getLabelFont().getFont().makeTransparentStringImage(_component.getLabelText().getText(), _component.getLabelColor());
                p_img = new MCFImage(s_ImageResourceID, p_madeImage, i_modifiers);
                _imageResourcesSet.put(s_ImageResourceID,p_img);
            }
            else
            {
                    MCFImage p_i = (MCFImage) _imageResourcesSet.get(s_ImageResourceID);
                    p_img = p_i;
            }

            checkAnchorForImage(p_img.p_resourceImage);
        }

        public void saveToStream(DataOutputStream _outStream, Hashtable _imageSet, boolean _saveModifiedImages, boolean _saveImagesOnly, boolean _saveChannel, PrintStream _javaStream,boolean _saveAnchorInfo) throws IOException
        {
            super.saveToStream(_outStream, _imageSet, _saveModifiedImages, _saveImagesOnly, _saveChannel, _javaStream,_saveAnchorInfo);
            if (_saveModifiedImages)
            {
                _outStream.writeByte(i_modifiers);
            }
            MCFImage p_image = (MCFImage) _imageSet.get(s_ImageResourceID);
            _outStream.writeByte(p_image.i_Index);
        }
    }

    private class MCFTextLabelComponent extends MCFComponent
    {
        private MCFFont p_Font;
        private MCFString p_String;
        private int i_Color;

        public MCFTextLabelComponent(MCFForm _parentForm, RrgFormComponent_Label _component, Hashtable _images, int _index, Vector _fonts, Vector _strings) throws IOException
        {
            super(_parentForm, _component, _index);
            if (i_X < 0 || i_Y < 0) throw new IOException("Can't save text label \"" + _component.getID() + "\" because it has negative main point");
            i_Color = _component.getLabelColor().getRGB();

            RrgResource_Font p_fnt = _component.getLabelFont();
            RrgResource_Text p_text = _component.getLabelText();

            if (!_fonts.contains(p_fnt.getResourceID()))
            {
                MCFFont p_mcifont = new MCFFont(p_fnt, _images);
                p_Font = p_mcifont;
                _fonts.add(p_mcifont);
            }
            else
            {
                String s_rsrcId = p_fnt.getResourceID();
                for (int li = 0; li < _fonts.size(); li++)
                {
                    MCFFont p_curfont = (MCFFont) _fonts.elementAt(li);
                    if (p_curfont.s_ID.equals(s_rsrcId))
                    {
                        p_Font = p_curfont;
                        break;
                    }
                }
                if (p_Font == null) throw new IOException("Can't find a font");
            }

            if (!_strings.contains(p_text.getResourceID()))
            {
                MCFString p_mcistring = new MCFString(p_text.getResourceID(), p_text.getText());
                p_String = p_mcistring;
                _strings.add(p_mcistring);
            }
            else
            {
                String s_rsrcId = p_text.getResourceID();
                for (int li = 0; li < _strings.size(); li++)
                {
                    MCFString p_curstr = (MCFString) _strings.elementAt(li);
                    if (p_curstr.s_ID.equals(s_rsrcId))
                    {
                        p_String = p_curstr;
                        break;
                    }
                }
                if (p_String == null) throw new IOException("Can't find a string");
            }
        }

        public void saveToStream(DataOutputStream _outStream, Hashtable _imageSet, boolean _saveModifiedImages, boolean _saveImagesOnly, boolean _saveChannelData, PrintStream _javaStream,boolean _saveAnchorInfo) throws IOException
        {
            super.saveToStream(_outStream, _imageSet, _saveModifiedImages, _saveImagesOnly, _saveChannelData, _javaStream,_saveAnchorInfo);
            // индекс фонта
            if (!p_Font.isGIFFont())
            {
                _outStream.writeByte(0);
            }
            else
            {
                _outStream.writeByte(p_Font.i_Index);
            }

            // индекс строки
            _outStream.writeByte(p_String.i_Index);

            // цвет R
            _outStream.writeByte(i_Color >>> 16);
            // цвет G
            _outStream.writeByte(i_Color >>> 8);
            // цвет B
            _outStream.writeByte(i_Color);
        }
    }


    private class MCFButtonComponent extends MCFComponent
    {
        public String s_NormalImageID;
        public String s_DisabledImageID;
        public String s_FocusedImageID;
        public String s_PressedImageID;

        public MCFButtonComponent(MCFForm _parent, RrgFormComponent_Button _component, int _index, Hashtable _imageResourcesSet, boolean _convertModifiedToStatic) throws IOException
        {
            super(_parent, _component, _index);

            if (i_X < 0 || i_Y < 0) throw new IOException("Can't save button \"" + _component.getID() + "\" because it has negative main point");

            String s_ImageResourceID = null;
            // Normal
            s_ImageResourceID = _component.getNormalImage().getResourceID();
            s_ImageResourceID += "$" +  RrgFormComponent_Image.MODIFIER_NONE;

            if (!_imageResourcesSet.contains(s_ImageResourceID))
            {
                _imageResourcesSet.put(s_ImageResourceID,new MCFImage(s_ImageResourceID, _component.getNormalImage().getImage(), RrgFormComponent_Image.MODIFIER_NONE));
            }

            s_NormalImageID = s_ImageResourceID;

            // Disabled
            if (_component.getDisabledImage() != null)
            {
                s_ImageResourceID = _component.getDisabledImage().getResourceID();
                s_ImageResourceID += "$" +  RrgFormComponent_Image.MODIFIER_NONE;

                if (!_imageResourcesSet.contains(s_ImageResourceID))
                {
                    _imageResourcesSet.put(s_ImageResourceID,new MCFImage(s_ImageResourceID, _component.getDisabledImage().getImage(), RrgFormComponent_Image.MODIFIER_NONE));
                }

                s_DisabledImageID = s_ImageResourceID;
            }
            else
            {
                s_DisabledImageID = s_NormalImageID;
            }

            // Pressed
            if (_component.getPressedImage() != null)
            {
                s_ImageResourceID = _component.getPressedImage().getResourceID();
                s_ImageResourceID += "$" + RrgFormComponent_Image.MODIFIER_NONE;

                if (!_imageResourcesSet.contains(s_ImageResourceID))
                {
                    _imageResourcesSet.put(s_ImageResourceID,new MCFImage(s_ImageResourceID, _component.getPressedImage().getImage(), RrgFormComponent_Image.MODIFIER_NONE));
                }

                s_PressedImageID = s_ImageResourceID;
            }
            else
            {
                s_PressedImageID = s_NormalImageID;
            }

            // Focused
            if (_component.getSelectedImage() != null)
            {
                s_ImageResourceID = _component.getSelectedImage().getResourceID();
                s_ImageResourceID += "$" + RrgFormComponent_Image.MODIFIER_NONE;

                if (!_imageResourcesSet.contains(s_ImageResourceID))
                {
                    _imageResourcesSet.put(s_ImageResourceID,new MCFImage(s_ImageResourceID, _component.getSelectedImage().getImage(), RrgFormComponent_Image.MODIFIER_NONE));
                }

                s_FocusedImageID = s_ImageResourceID;
            }
            else
            {
                s_FocusedImageID = s_NormalImageID;
            }
        }

        public void saveToStream(DataOutputStream _outStream, Hashtable _imageSet, boolean _saveModifiedImages, boolean _saveImagesOnly, boolean _saveChannel, PrintStream _printStream,boolean _saveAnchorInfo) throws IOException
        {
            super.saveToStream(_outStream, _imageSet, _saveModifiedImages, _saveImagesOnly, _saveChannel, _printStream,_saveAnchorInfo);
            MCFImage p_image = (MCFImage) _imageSet.get(s_NormalImageID);
            _outStream.writeByte(p_image.i_Index);
            p_image = (MCFImage) _imageSet.get(s_FocusedImageID);
            _outStream.writeByte(p_image.i_Index);
            p_image = (MCFImage) _imageSet.get(s_PressedImageID);
            _outStream.writeByte(p_image.i_Index);
            p_image = (MCFImage) _imageSet.get(s_DisabledImageID);
            _outStream.writeByte(p_image.i_Index);
        }
    }

    private class MCFClipAreaComponent extends MCFComponent
    {
        public int i_Width;
        public int i_Height;
        public int i_Value;

        public MCFClipAreaComponent(MCFForm _parent, RrgFormComponent_CustomArea _component, int _index) throws IOException
        {
            super(_parent, _component, _index);
            i_Width = _component.getWidth();
            i_Height = _component.getHeight();
            i_Value = _component.getAreaValue();
            if (i_X < 0 || i_Y < 0 || i_Width > 255 || i_Height > 255) throw new IOException("Can't save clip area \"" + _component.getID() + "\"");
        }

        public void saveToStream(DataOutputStream _outStream, Hashtable _imageSet, boolean _saveModifiedImages, boolean _saveImagesOnly, boolean _saveChannel, PrintStream _javaStream,boolean _saveAnchorInfo) throws IOException
        {
            super.saveToStream(_outStream, _imageSet, _saveModifiedImages, _saveImagesOnly, _saveChannel, _javaStream,_saveAnchorInfo);
            _outStream.writeByte(i_Width);
            _outStream.writeByte(i_Height);
        }
    }

    private class MCFForm
    {
        public Vector p_Components;
        public int i_BackgroundColor;
        public int i_Width;
        public int i_Height;

        public int i_formIndex;
        public String s_formID;

        public boolean lg_hasModifiedImages;

        public MCFForm(Vector _paths,FormContainer _form, int _index, Vector _fonts, Vector _strings, Hashtable _imageResourcesSet, boolean _convertModifiedToStatic, boolean _imagesOnly, boolean _saveTextsAsImages) throws IOException
        {
            String s_name = _form.getID();
            s_formID = s_name.replace(' ', '_');
            lg_hasModifiedImages = false;
            i_formIndex = _index;
            p_Components = new Vector();
            i_BackgroundColor = _form.getBackgroundColor().getRGB();
            i_Width = _form.getWidth();
            i_Height = _form.getHeight();

            if (i_Width > 255 || i_Height > 255 || _form.getSize() > 255) throw new IOException("Form \'" + _form.getID() + "\' can't be saved to MCF");

            int i_componentIndex = 0;
            for (int li = 0; li < _form.getSize(); li++)
            {
                AbstractFormComponent p_component = _form.getComponentAt(li);
                boolean lg_resourceOnly = false;
                if (p_component.isHidden()) lg_resourceOnly = true;

                int i_x = p_component.getX();
                int i_y = p_component.getY();
                int i_w = p_component.getWidth();
                int i_h = p_component.getHeight();

                if (i_x >= i_Width || i_y >= i_Height) continue;
                if (i_x + i_w <= 0 || i_y + i_h <= 0) continue;

                if (p_component.getType() == AbstractFormComponent.COMPONENT_IMAGE)
                {
                    RrgFormComponent_Image p_imgCompo = (RrgFormComponent_Image) p_component;
                    if (p_imgCompo.getModifiers() != RrgFormComponent_Image.MODIFIER_NONE) lg_hasModifiedImages = true;
                    MCFImageComponent p_compo = new MCFImageComponent(this, p_imgCompo, i_componentIndex, _imageResourcesSet, _convertModifiedToStatic);
                    if (lg_resourceOnly) continue;
                    p_Components.add(p_compo);
                }
                else if (!_imagesOnly)
                {
                    if (lg_resourceOnly) continue;
                    if (p_component.getType() == AbstractFormComponent.COMPONENT_CUSTOMAREA)
                    {
                        RrgFormComponent_CustomArea p_imgCompo = (RrgFormComponent_CustomArea) p_component;

                        MCFClipAreaComponent p_compo = new MCFClipAreaComponent(this, p_imgCompo, i_componentIndex);
                        p_Components.add(p_compo);
                    }
                    else
                    if (p_component.getType() == AbstractFormComponent.COMPONENT_PATH)
                    {
                        RrgFormComponent_Path p_pathCompo = (RrgFormComponent_Path) p_component;

                        MCFPath p_compo = new MCFPath(this,p_pathCompo);
                        _paths.add(p_compo);
                    }
                    else
                    if (p_component.getType() == AbstractFormComponent.COMPONENT_BUTTON)
                    {
                        RrgFormComponent_Button p_imgCompo = (RrgFormComponent_Button) p_component;

                        MCFButtonComponent p_compo = new MCFButtonComponent(this, p_imgCompo, i_componentIndex, _imageResourcesSet, _convertModifiedToStatic);
                        p_Components.add(p_compo);
                    }
                    else if (p_component.getType() == AbstractFormComponent.COMPONENT_LABEL)
                    {
                        RrgFormComponent_Label p_imgCompo = (RrgFormComponent_Label) p_component;

                        if (_saveTextsAsImages)
                        {
                            MCFImageComponent p_compo = new MCFImageComponent(this, p_imgCompo, i_componentIndex, _imageResourcesSet);
                            p_Components.add(p_compo);
                        }
                        else
                        {
                            MCFTextLabelComponent p_compo = new MCFTextLabelComponent(this, p_imgCompo, _imageResourcesSet, i_componentIndex, _fonts, _strings);
                            p_Components.add(p_compo);
                        }
                    }
                }

                i_componentIndex++;
            }
        }

        public void saveToStream(DataOutputStream _outStream, Hashtable _images, boolean _saveModifiedImages, boolean _saveImagesOnly, boolean _saveChannel, PrintStream _javaStream,boolean _saveAnchorInfo) throws IOException
        {
            // Цвет фона RGB
            _outStream.writeByte(i_BackgroundColor >>> 16);
            _outStream.writeByte(i_BackgroundColor >>> 8);
            _outStream.writeByte(i_BackgroundColor);

            // Размеры
            _outStream.writeByte(i_Width - 1);
            _outStream.writeByte(i_Height - 1);

            _outStream.writeByte(p_Components.size());

            _javaStream.println("   protected static final int MCF_FORM_" + s_formID + " = " + i_formIndex + ";");

            for (int li = 0; li < p_Components.size(); li++)
            {
                ((MCFComponent) p_Components.elementAt(li)).saveToStream(_outStream, _images, _saveModifiedImages, _saveImagesOnly, _saveChannel, _javaStream,_saveAnchorInfo);
            }
            _javaStream.flush();
        }
    }


    private JPanel p_MainPanel;
    private JCheckBox p_CheckBox_ConvertModifiedToStaticImages;

    public boolean accept(File f)
    {
        if (f == null) return false;
        if (f.isDirectory()) return true;
        String s_fileName = f.getName().toUpperCase();
        if (s_fileName.endsWith(".MCF")) return true;
        return false;
    }

    public File processFileNameBeforeSaving(File _file)
    {
        if (_file == null) return null;
        if (_file.getAbsolutePath().toUpperCase().endsWith(".MCF")) return _file;
        _file = new File(_file.getAbsolutePath() + ".mcf");
        return _file;
    }

    public String getDescription()
    {
        return "Mobile Form";
    }

    public void init(FormCollection _container)
    {
    }

    public JPanel getPanel()
    {
        return p_MainPanel;
    }

    public String isDataOk()
    {
        return null;
    }

    public FileFilter getFileFilter()
    {
        return this;
    }

    private byte[] encodeString(String _string)
    {
        if (_string == null) return null;
        byte[] ab_result = new byte[_string.length()];
        for (int li = 0; li < ab_result.length; li++)
        {
            char ch_char = _string.charAt(li);
            int i_index = GIFFont.CHARSET.indexOf(ch_char);
            ab_result[li] = (byte) i_index;
        }
        return ab_result;
    }


    public void exportData(File _file, ResourceContainer _resources, FormsList _list) throws IOException
    {
        if (_list.getSize() == 0) return;

        MCFImage.i_InsideIndexCounter = 0;

        ByteArrayOutputStream p_javaOutStream = new ByteArrayOutputStream(20000);
        PrintStream p_outPrintStream = new PrintStream(p_javaOutStream);

        ByteArrayOutputStream p_preprocessorOutStream = new ByteArrayOutputStream(20000);
        PrintStream p_prepPrintStream = new PrintStream(p_preprocessorOutStream);

        boolean lg_convertModifiedImagesToStatic = p_CheckBox_ConvertModifiedToStaticImages.isSelected();
        boolean lg_imagesOnly = p_CheckBox_ImagesOnly.isSelected();
        boolean lg_saveChannelData = p_CheckBox_SaveChannel.isSelected();
        boolean lg_saveTextAsImages = p_CheckBox_SaveTextAsImages.isSelected();
        boolean lg_copyImagesOnDisk = p_CheckBox_copyImagesOnDisk.isSelected();
        boolean lg_saveAnchorInfo = p_CheckBox_SaveAnchorInfo.isSelected();
        boolean lg_savePaths = p_CheckBox_SavePaths.isSelected();

        boolean lg_hasNativeFonts = false;
        boolean lg_hasOptionalElements = false;

        Vector p_MCIforms = new Vector();

        Vector p_Fonts = new Vector();
        Vector p_Strings = new Vector();

        Vector p_Paths = new Vector();

        Hashtable p_ImageTable = new Hashtable();

        boolean lg_hasModifiedImages = false;
        // Формируем список компонент
        for (int li = 0; li < _list.getSize(); li++)
        {
            MCFForm p_form = new MCFForm(p_Paths,_list.getFormAt(li), li, p_Fonts, p_Strings, p_ImageTable, lg_convertModifiedImagesToStatic, lg_imagesOnly, lg_saveTextAsImages);
            lg_hasModifiedImages |= p_form.lg_hasModifiedImages;
            p_MCIforms.add(p_form);
        }

        boolean lg_buttonUsed = false;
        boolean lg_imageUsed = false;
        boolean lg_textlabelUsed = false;
        boolean lg_clipareaUsed = false;

        // Проверяем, есть ли опциональные компоненты и выставляем флаги использования
        for(int li=0;li<p_MCIforms.size();li++)
        {
            MCFForm p_frm = (MCFForm) p_MCIforms.elementAt(li);
            Vector p_cmpn = p_frm.p_Components;
            for (int lc=0;lc<p_cmpn.size();lc++)
            {
                MCFComponent p_compo = (MCFComponent) p_cmpn.elementAt(lc);
                if (p_compo.lg_Optional)
                {
                    lg_hasOptionalElements = true;
                }
                switch(p_compo.i_type)
                {
                   case MCFComponent.MCFCOMPONENT_AREA : lg_clipareaUsed = true;break;
                   case MCFComponent.MCFCOMPONENT_IMAGE : lg_imageUsed = true;break;
                   case MCFComponent.MCFCOMPONENT_BUTTON : lg_buttonUsed = true;break;
                   case MCFComponent.MCFCOMPONENT_TEXTLABEL : lg_textlabelUsed = true;break;
                }
            }
        }


        // Индексируем и формируем блоки строк и фонтов
        byte[] ab_StringsBlock = null;
        byte[] ab_FontsBlock = null;

        if (!lg_saveTextAsImages)
        {
            // индексация строк

            for (int li = 0; li < p_Fonts.size(); li++)
            {
                MCFFont p_fnt = (MCFFont) p_Fonts.elementAt(li);
                p_fnt.i_Index = li + 1;
                if (!p_fnt.isGIFFont()) lg_hasNativeFonts = true;
            }

            // индексация фонтов
            int i_indx = 1;
            for (int li = 0; li < p_Fonts.size(); li++)
            {
                MCFFont p_fnt = (MCFFont) p_Fonts.elementAt(li);
                if (p_fnt.isGIFFont())
                {
                    p_fnt.i_Index = i_indx;
                    i_indx++;
                }
                else
                    p_fnt.i_Index = 0;
            }

            //--------------Модуль строк-----------------
            ByteArrayOutputStream p_stringArray = new ByteArrayOutputStream(1024);
            DataOutputStream p_stringsOutputStream = new DataOutputStream(p_stringArray);

            // Количество строк
            p_stringsOutputStream.writeByte(p_Strings.size());

            // Таблица смещений строк
            int i_pos = 0;
            for (int li = 0; li < p_Strings.size(); li++)
            {
                p_stringsOutputStream.writeShort(i_pos);
                MCFString p_string = (MCFString) p_Strings.elementAt(li);
                byte[] ab_str = p_string.ab_EncodedString;
                i_pos += 1 + ab_str.length;
            }

            // Размер блока строк
            p_stringsOutputStream.writeShort(i_pos);

            // Запись строк
            for (int li = 0; li < p_Strings.size(); li++)
            {
                MCFString p_string = (MCFString) p_Strings.elementAt(li);
                byte[] ab_string = p_string.ab_EncodedString;
                if (ab_string.length == 0 || ab_string.length > 256) throw new IOException("Too long or too short string \"" + p_string.s_ID + '\"');

                p_stringsOutputStream.writeByte(ab_string.length - 1);
                p_stringsOutputStream.write(ab_string);
            }

            p_stringsOutputStream.close();
            ab_StringsBlock = p_stringArray.toByteArray();
            if (p_Strings.size() == 0 ) ab_StringsBlock = null;
        }

        lg_hasModifiedImages &= !lg_convertModifiedImagesToStatic;

        // Выставляем индексы у картинок
        MCFImage [] ap_imagesAr1 = new MCFImage[MCFImage.i_InsideIndexCounter];
        Iterator p_set = p_ImageTable.values().iterator();
        while (p_set.hasNext())
        {
            MCFImage p_image = (MCFImage) p_set.next();
            ap_imagesAr1[p_image.i_InsideIndex] = p_image;
        }
        int i_index = 0;

        // Сортируем изображения по размеру, большие на первое место, маленькие в конец
        TreeSet p_ImagesSet = new TreeSet(new Comparator(){
            public int compare(Object o1, Object o2)
            {
                MCFImage p_img1 = (MCFImage) o1;
                MCFImage p_img2 = (MCFImage) o2;

                int i_o1 = p_img1.p_resourceImage.getWidth()*p_img1.p_resourceImage.getHeight();
                int i_o2 = p_img2.p_resourceImage.getWidth()*p_img2.p_resourceImage.getHeight();

                if (i_o1<i_o2) return 1; return -1;
            }
        });

        for(int li=0;li<ap_imagesAr1.length;li++)
        {
            MCFImage p_curImg = ap_imagesAr1[li];
            if (p_curImg==null) continue;
            p_curImg.i_Index = i_index;
            p_ImagesSet.add(p_curImg);
            i_index++;
        }
        ap_imagesAr1 = null;



        if (!lg_saveTextAsImages)
        {
            //--------------Модуль фонтов-----------------
            ByteArrayOutputStream p_fontsArray = new ByteArrayOutputStream(1024);
            DataOutputStream p_fontsOutputStream = new DataOutputStream(p_fontsArray);

            for (int li = 0; li < p_Fonts.size(); li++)
            {
                MCFFont p_font = (MCFFont) p_Fonts.elementAt(li);
                if (p_font.isGIFFont())
                {
                    MCFImage p_mciimage = p_font.p_fontImage;
                    p_fontsOutputStream.writeByte(p_mciimage.i_Index);
                    p_fontsOutputStream.writeByte(p_font.i_CharWidth);
                    p_fontsOutputStream.writeByte(p_font.i_CharHeight);
                    p_fontsOutputStream.writeByte(p_font.i_horzInterval);
                    p_fontsOutputStream.writeByte(p_font.i_vertInterval);
                }
            }
            p_fontsOutputStream.close();
            ab_FontsBlock = p_fontsArray.toByteArray();
            if (ab_FontsBlock.length == 0) ab_FontsBlock = null;
        }


        FileOutputStream p_fos = new FileOutputStream(_file);
        DataOutputStream p_dos = new DataOutputStream(p_fos);

        // Записываем версию
        p_dos.writeByte(FORMAT_VERSION);

        // Записываем данные о формах

        // Количество форм
        p_dos.writeByte(_list.getSize() - 1);

        // Флаг записи модификаторов
        int i_flag = 0;

        if (lg_hasModifiedImages) i_flag |= FLAG_HASMODIFIEDIMAGES;
        if (lg_saveChannelData) i_flag |= FLAG_CHANNELDATA;
        if (lg_imagesOnly) i_flag |= FLAG_IMAGES_ONLY;
        if (p_Strings.size() != 0) i_flag |= FLAG_STRINGS;
        if (ab_FontsBlock != null) i_flag |= FLAG_FONTS;
        if (lg_saveAnchorInfo) i_flag |= FLAG_HASANCHOR;

        if (lg_savePaths && p_Paths.size()>0) i_flag |= FLAG_PATHS;

        p_dos.writeByte(i_flag);

        int[] ai_formsOffset = new int[_list.getSize()];
        ByteArrayOutputStream p_outFormOutStream = new ByteArrayOutputStream(16384);
        DataOutputStream p_fdos = new DataOutputStream(p_outFormOutStream);

        // Записываем формы
        p_outPrintStream.println("//------------MCI FORMS-----------------");
        for (int li = 0; li < p_MCIforms.size(); li++)
        {
            ai_formsOffset[li] = p_fdos.size();
            MCFForm p_form = (MCFForm) p_MCIforms.elementAt(li);
            p_form.saveToStream(p_fdos, p_ImageTable, (lg_hasModifiedImages && (!lg_convertModifiedImagesToStatic)), lg_imagesOnly, lg_saveChannelData, p_outPrintStream,lg_saveAnchorInfo);
        }
        p_fdos.flush();
        p_fdos.close();
        byte[] ab_formsData = p_outFormOutStream.toByteArray();

        // Размер блока форм
        p_dos.writeShort(ab_formsData.length);

        // Блок форм
        p_dos.write(ab_formsData);

        // Блок строк
        if (ab_StringsBlock != null)
        {
            p_dos.write(ab_StringsBlock);
            p_prepPrintStream.println("//#local STRINGS = true");
        }
        else
        {
            p_prepPrintStream.println("//#local STRINGS = false");
        }

        // Блок фонтов
        if (ab_FontsBlock != null)
        {
            p_dos.write(ab_FontsBlock);
            p_prepPrintStream.println("//#local FONTS = true");
        }
        else
        {
            p_prepPrintStream.println("//#local FONTS = false");
        }


        // Блок путей
        if (lg_savePaths && p_Paths.size()>0)
        {
            // Записываем данные путей
            p_prepPrintStream.println("//#local PATHSDATA = true");

            if (p_Paths.size()>256) throw new IOException("Too many paths");
            p_dos.writeByte(p_Paths.size()-1);

            boolean lg_saveMainPoints = false;
            boolean lg_saveBoundiary = false;
            boolean lg_saveSteps = false;

            int i_offset = 0;
            ByteArrayOutputStream p_pathArray = new ByteArrayOutputStream(16000);
            DataOutputStream p_dataPathStream = new DataOutputStream(p_pathArray);

            for(int li=0;li<p_Paths.size();li++)
            {
                MCFPath p_path = (MCFPath)p_Paths.elementAt(li);
                i_offset = p_path.saveToStream(p_dataPathStream,i_offset);

                i_offset--; //  компенсируем байт флагов данных пути

                RrgFormComponent_Path p_rrgpath = p_path.p_Path;

                if (p_rrgpath.lg_SaveSteps) lg_saveSteps = true;
                if (p_rrgpath.lg_SaveBoundiaryInfo) lg_saveBoundiary = true;
                if (p_rrgpath.lg_SaveMainPointInfo) lg_saveMainPoints = true;
            }
            p_dataPathStream.flush();
            p_dataPathStream.close();

            p_dos.writeShort(i_offset);
            p_dos.write(p_pathArray.toByteArray());

            p_pathArray = null;
            p_dataPathStream = null;            

            if (lg_saveSteps)
                 p_prepPrintStream.println("//#local PATHSDATA_SAVESTEPS = true");
            else
                 p_prepPrintStream.println("//#local PATHSDATA_SAVESTEPS = false");

            if (lg_saveBoundiary)
                p_prepPrintStream.println("//#local PATHSDATA_SAVEBOUNDIARY = true");
            else
                p_prepPrintStream.println("//#local PATHSDATA_SAVEBOUNDIARY = false");

            if (lg_saveMainPoints)
                p_prepPrintStream.println("//#local PATHSDATA_SAVEMAINPOINT = true");
            else
                p_prepPrintStream.println("//#local PATHSDATA_SAVEMAINPOINT = false");
        }
        else
        {
            // Нет путей
            p_prepPrintStream.println("//#local PATHSDATA = false");
            p_prepPrintStream.println("//#local PATHSDATA_SAVESTEPS = false");
            p_prepPrintStream.println("//#local PATHSDATA_SAVEBOUNDIARY = false");
            p_prepPrintStream.println("//#local PATHSDATA_SAVEMAINPOINT = false");
        }

        if (lg_saveChannelData)
        {
            p_prepPrintStream.println("//#local CHANNEL = true");
        }
        else
        {
            p_prepPrintStream.println("//#local CHANNEL = false");
        }

        if (lg_imagesOnly)
        {
            p_prepPrintStream.println("//#local IMAGESONLY = true");
        }
        else
        {
            p_prepPrintStream.println("//#local IMAGESONLY = false");
        }

        if (lg_hasNativeFonts)
        {
            p_prepPrintStream.println("//#local NATIVEFONTS = true");
        }
        else
        {
            p_prepPrintStream.println("//#local NATIVEFONTS = false");
        }

        if (lg_saveAnchorInfo)
        {
            p_prepPrintStream.println("//#local HASANCHORINFO = true");
        }
        else
        {
            p_prepPrintStream.println("//#local HASANCHORINFO = false");
        }

        if (lg_hasOptionalElements)
        {
            p_prepPrintStream.println("//#local OPTIONALELEMENTS = true");
        }
        else
        {
            p_prepPrintStream.println("//#local OPTIONALELEMENTS = false");
        }

        if (lg_hasModifiedImages)
        {
            p_prepPrintStream.println("//#local MODIFIED = true");
        }
        else
        {
            p_prepPrintStream.println("//#local MODIFIED = false");
        }

        p_prepPrintStream.println();

        if (lg_clipareaUsed)
        {
            p_prepPrintStream.println("//#local COMPONENT_CLIPAREA = true");
        }
        else
        {
            p_prepPrintStream.println("//#local COMPONENT_CLIPAREA= false");
        }

        if (lg_imageUsed)
        {
            p_prepPrintStream.println("//#local COMPONENT_IMAGE = true");
        }
        else
        {
            p_prepPrintStream.println("//#local COMPONENT_IMAGE= false");
        }

        if (lg_buttonUsed)
        {
            p_prepPrintStream.println("//#local COMPONENT_BUTTON = true");
        }
        else
        {
            p_prepPrintStream.println("//#local COMPONENT_BUTTON = false");
        }

        if (lg_textlabelUsed)
        {
            p_prepPrintStream.println("//#local COMPONENT_TEXTLABEL = true");
        }
        else
        {
            p_prepPrintStream.println("//#local COMPONENT_TEXTLABEL = false");
        }

        // Блок картинок
        p_outFormOutStream = new ByteArrayOutputStream(16384);
        p_fdos = new DataOutputStream(p_outFormOutStream);


        if (lg_savePaths && p_Paths.size()>0)
        {
            p_outPrintStream.println("//------------MCF PATHS-----------------");
            for(int li=0;li<p_Paths.size();li++)
            {
                MCFPath p_path = (MCFPath)p_Paths.elementAt(li);
                p_outPrintStream.println("  protected static final int MCF_PATH_" + p_path.p_FormID.s_formID+"_"+p_path.s_ID + " = " + p_path.i_Offset + ';');
            }
        }

        p_outPrintStream.println("//------------MCF IMAGES-----------------");

        String s_parentDir = _file.getParent();
        if (s_parentDir == null) s_parentDir = "";

        Iterator p_iter = p_ImagesSet.iterator();
        int i_maxPackedImageSize = 0;
        while(p_iter.hasNext())
        {
            MCFImage p_image = (MCFImage)p_iter.next();

            String s_name = p_image.s_ID;
            if (s_name.endsWith("$0"))
            {
                s_name = s_name.substring(0,s_name.length()-2);
            }
            else
            {
                s_name = s_name.replace('$','_');
            }

            s_name.replace(' ', '_');
            p_outPrintStream.println("  protected static final int MCF_IMG_" + s_name + " = " + p_image.i_Index + ';');

            BufferedImage p_buffImage = p_image.p_resourceImage;

            // Обрабатываем для совместимости с LG и SAMSUNG
            colorCorrectionForSamsungLGphones(p_buffImage);

            ByteArrayOutputStream p_outStream = new ByteArrayOutputStream(9000);
            PNGEncoder p_pngEncoder = new PNGEncoder(p_buffImage, p_outStream, null, false);
            p_pngEncoder.encodeImage();

            p_outStream.close();
            byte[] ab_imageArr = p_outStream.toByteArray();

            if (i_maxPackedImageSize<ab_imageArr.length) i_maxPackedImageSize = ab_imageArr.length;

            // Записываем индекс изображения в массиве
            p_fdos.writeShort(p_image.i_Index);
            // Записываем размер блока данных изображения
            p_fdos.writeShort(ab_imageArr.length);
            // Данные изображения
            p_fdos.write(ab_imageArr);

            // Копируем созданное изображение на диск если есть потребность
            if (lg_copyImagesOnDisk)
            {
                File p_pngOutFile = new File(s_parentDir, s_name + ".png");
                FileOutputStream p_fffos = new FileOutputStream(p_pngOutFile);
                p_fffos.write(ab_imageArr);
                p_fffos.flush();
                p_fffos.close();
            }
        }

        p_fdos.flush();
        p_fdos.close();

        ab_formsData = p_outFormOutStream.toByteArray();

        //--------------Модуль изображений-----------------
        // Записываем количество изображений
        p_dos.writeShort(p_ImagesSet.size());
        // Записываем размер наибольшего упакованного изображения
        p_dos.writeShort(i_maxPackedImageSize);
        // Записываем размер блока изображений
        p_dos.writeShort(ab_formsData.length);
        // Записываем блок изображений
        p_dos.write(ab_formsData);

        p_dos.flush();
        p_fos.close();

        // Записываем JAVA header для формы
        p_fos = new FileOutputStream(_file.getAbsolutePath() + ".java");
        p_outPrintStream.close();

        // Записываем блок констант для препроцессора
        p_fos.write("\r\n\r\n\r\n".getBytes());
        p_prepPrintStream.close();
        p_fos.write(p_preprocessorOutStream.toByteArray());
        p_fos.write("\r\n\r\n\r\n".getBytes());

        p_fos.write(p_javaOutStream.toByteArray());

        p_fos.flush();
        p_fos.close();
    }

    public static final void colorCorrectionForSamsungLGphones(BufferedImage _img)
    {
        // Обрабатываем изображение что бы не было проблем на Samsung и LG где белый цвет в диапазоне от 251,251,251-255,255,255 прозрачен
        int[] ai_ImageBuffer = ((DataBufferInt) _img.getRaster().getDataBuffer()).getData();

        int i_len = ai_ImageBuffer.length - 1;
        while (i_len >= 0)
        {
            int i_argb = ai_ImageBuffer[i_len];

            int i_a = i_argb >>> 24;
            int i_r = (i_argb >>> 16) & 0xFF;
            int i_g = (i_argb >>> 8) & 0xFF;
            int i_b = i_argb & 0xFF;

            if (i_a < 0x80)
            {
                i_argb = 0xFFFFFF;
            }
            else
            {
                if (i_r > 250) i_r = 250;
                if (i_g > 250) i_g = 250;
                if (i_b > 250) i_b = 250;

                i_argb = 0xFF000000 | (i_r << 16) | (i_g << 8) | i_b;
            }

            ai_ImageBuffer[i_len] = i_argb;

            i_len--;
        }
    }

    public String getName()
    {
        return "Export to Mobile Compound Forms";
    }

    public boolean supportsMultiform()
    {
        return true;
    }


}

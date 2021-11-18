package com.raydac_research.FormEditor.ExportModules.RrgScene;

import com.raydac_research.FormEditor.RrgFormComponents.*;
import com.raydac_research.FormEditor.ExportModules.RrgScene.Components.*;
import com.raydac_research.FormEditor.ExportModules.RrgScene.Resources.*;
import com.raydac_research.FormEditor.ExportModules.FormsList;
import com.raydac_research.FormEditor.RrgFormResources.RrgResource_Image;
import com.raydac_research.PNGWriter.PNGEncoder;
import com.raydac_research.Font.TTFFont;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.*;

public class RrgScene
{
    // Типы компонент
    public static final int TYPE_COMPONENT_IMAGE = 0;
    public static final int TYPE_COMPONENT_BUTTON = 1;
    public static final int TYPE_COMPONENT_LABEL = 2;
    public static final int TYPE_COMPONENT_CUSTOMAREA = 3;

    // Выравнивания текста
    public static final int TEXT_ALIGN_HORZ_LEFT = 0x0000;
    public static final int TEXT_ALIGN_HORZ_CENTER = 0x0100;
    public static final int TEXT_ALIGN_HORZ_RIGHT = 0x0200;

    public static final int TEXT_ALIGN_VERT_TOP = 0;
    public static final int TEXT_ALIGN_VERT_CENTER = 1;
    public static final int TEXT_ALIGN_VERT_DOWN = 2;

    // Типы изображений
    public static final int TYPE_IMAGE_PNG = 0;

    // Фонты
    public static final int TYPE_FONT_TTF = 0;
    public static final int TYPE_FONT_BFT = 1;

    // Типы звуков
    public static final int TYPE_SOUND_WAV = 0;

    // Служебные флаги
    public static final int SERVICE_FLAG_CHANNELS_EXIST = 1;
    public static final int SERVICE_FLAG_OFFSETS_EXIST = 2;
    public static final int SERVICE_FLAG_IMAGE_TYPES = 4;
    public static final int SERVICE_FLAG_SOUNDS_TYPES = 8;
    public static final int SERVICE_FLAG_FONTS_TYPES = 16;

    // Флаги секций
    private static final int SECTION_FLAG_FORM = 1;
    private static final int SECTION_FLAG_IMAGES = 2;
    private static final int SECTION_FLAG_SOUNDS = 4;
    private static final int SECTION_FLAG_TEXTS = 8;
    private static final int SECTION_FLAG_FONTS = 16;

    public static final int NULL_OFFSET = 0xFFFF;

    protected RrgResourceSection_Images p_Section_Images;
    protected RrgResourceSection_Sounds p_Section_Sounds;
    protected RrgResourceSection_Fonts p_Section_Fonts;
    protected RrgResourceSection_Texts p_Section_Texts;

    protected RRGScene_FormsSection p_formsSection;

    private static final String SCALE_POSTFIX = "_Scaled";
    private static final String SAVETEXTASIMAGES_POSTFIX = "_AsImages";

    private boolean lg_saveChannelData;
    private boolean lg_saveFontsData;
    private boolean lg_saveOffsetsToResources;
    private boolean lg_saveSounds;
    private boolean lg_saveImageTypes;
    private boolean lg_saveSoundTypes;
    private boolean lg_saveFontTypes;
    private boolean lg_NoTexts;
    private boolean lg_SaveImages;
    private boolean lg_SaveTexts;

    public RrgScene(FormsList _list,
                    int _ImageFormat,
                    boolean _saveTextAsImages,
                    boolean _saveChannelData,
                    boolean _saveFontsData,
                    boolean _saveOffsetsToResources,
                    boolean _saveModifiedImagesAsImages,
                    boolean _saveSounds,
                    boolean _saveButtonsComp,
                    boolean _saveCustomAreaComp,
                    boolean _saveImagesComp,
                    boolean _saveLabelsComp,
                    boolean _saveImageTypes,
                    boolean _saveSoundTypes,
                    boolean _saveFontTypes,
                    boolean _saveButtonTextsAsImages,
                    boolean _saveImages,
                    boolean _saveTexts) throws IOException
    {

        lg_saveChannelData = _saveChannelData;
        lg_saveFontsData = _saveFontsData;
        lg_saveOffsetsToResources = _saveOffsetsToResources;
        lg_saveSounds = _saveSounds;
        lg_saveImageTypes = _saveImageTypes;
        lg_saveSoundTypes = _saveSoundTypes;
        lg_saveFontTypes = _saveFontTypes;
        lg_NoTexts = _saveTextAsImages;
        lg_SaveImages = _saveImages;
        lg_SaveTexts = _saveTexts;

        p_formsSection = new RRGScene_FormsSection();

        fillResourceContainersFromForms(_list, _saveModifiedImagesAsImages, _ImageFormat, _saveTextAsImages, _saveButtonTextsAsImages);

        for (int lt = 0; lt < _list.getSize(); lt++)
        {
            FormContainer p_form = _list.getFormAt(lt);

            // Формируем секцию формы
            RRGScene_Form p_rrg_form = new RRGScene_Form(p_form.getID(),lt, p_form.getWidth(), p_form.getHeight(), p_form.getChannel(), p_form.getBackgroundColor(), p_form.getNormalTextColor(), p_form.getSelectedTextColor(), p_form.getPressedTextColor(), p_form.getDisabledTextColor());

            int i_componentIndex = 0;

            String s_formIndex = "_"+lt+"_";

            for (int li = 0; li < p_form.getSize(); li++)
            {
                AbstractFormComponent p_formComponent = (AbstractFormComponent) p_form.getComponentAt(li);
                int i_x = p_formComponent.getX();
                int i_y = p_formComponent.getY();
                int i_w = p_formComponent.getWidth();
                int i_h = p_formComponent.getHeight();

                switch (p_formComponent.getType())
                {
                    case AbstractFormComponent.COMPONENT_CUSTOMAREA:
                        {
                            if (_saveCustomAreaComp)
                            {
                                RrgFormComponent_CustomArea p_areaComponent = (RrgFormComponent_CustomArea) p_formComponent;

                                RRGSceneComponent_CustomArea p_newComponent = null;
                                p_newComponent = new RRGSceneComponent_CustomArea(p_areaComponent.getID(),i_componentIndex++,i_x, i_y, i_w, i_h, p_areaComponent.getAreaValue());
                                p_rrg_form.getComponentsSection().addComponent(p_newComponent);
                            }
                        }
                        ;
                        break;
                    case AbstractFormComponent.COMPONENT_IMAGE:
                        {
                            if (_saveImagesComp)
                            {
                                RrgFormComponent_Image p_imageComponent = (RrgFormComponent_Image) p_formComponent;

                                String s_id = p_imageComponent.getImageResource().getResourceID();
                                RRGSceneComponent_Image p_newComponent = null;

                                if ((p_imageComponent.getScale()!=1 || p_imageComponent.getModifiers()!=RrgFormComponent_Image.MODIFIER_NONE) && _saveModifiedImagesAsImages)
                                {
                                    s_id += SCALE_POSTFIX + p_imageComponent.getScale()+"m"+p_imageComponent.getModifiers();
                                    RRGSceneResourceImage p_image = (RRGSceneResourceImage) p_Section_Images.getResource(s_id);

                                    if (p_image == null) throw new IOException("Inside error ["+s_id+"]");

                                    p_newComponent = new RRGSceneComponent_Image(p_image.getID(),i_componentIndex++, p_image, i_x, i_y, i_w, i_h, 1, RRGSceneComponent_Image.MODIFIER_NONE);
                                }
                                else
                                {
                                    RRGSceneResourceImage p_image = (RRGSceneResourceImage) p_Section_Images.getResource(s_id);

                                    int i_imgMod = p_imageComponent.getModifiers();
                                    int i_modifiers = ((i_imgMod & RrgFormComponent_Image.MODIFIER_FLIP_HORZ) != 0 ? RRGSceneComponent_Image.MODIFIER_FLIPH : RRGSceneComponent_Image.MODIFIER_NONE) | ((i_imgMod & RrgFormComponent_Image.MODIFIER_FLIP_VERT) != 0 ? RRGSceneComponent_Image.MODIFIER_FLIPV : RRGSceneComponent_Image.MODIFIER_NONE);

                                    p_newComponent = new RRGSceneComponent_Image(p_image.getID(),i_componentIndex++, p_image, i_x, i_y, i_w, i_h, p_imageComponent.getScale(), i_modifiers);
                                }
                                p_rrg_form.getComponentsSection().addComponent(p_newComponent);
                            }
                        }
                        ;
                        break;
                    case AbstractFormComponent.COMPONENT_LABEL:
                        {
                            RrgFormComponent_Label p_labelComponent = (RrgFormComponent_Label) p_formComponent;
                            if (_saveLabelsComp)
                            {
                                if (_saveTextAsImages)
                                {
                                    String s_id = p_labelComponent.getID()+s_formIndex+SAVETEXTASIMAGES_POSTFIX;
                                    RRGSceneResourceImage p_image = (RRGSceneResourceImage) p_Section_Images.getResource(s_id);
                                    RRGSceneComponent_Image p_newComponent = new RRGSceneComponent_Image(p_image.getID(),i_componentIndex++, p_image, i_x, i_y, i_w, i_h, 1, RRGSceneComponent_Image.MODIFIER_NONE);
                                    p_rrg_form.getComponentsSection().addComponent(p_newComponent);
                                }
                                else
                                {
                                    String s_id = p_labelComponent.getLabelText().getResourceID();
                                    RRGSceneResourceText p_text = (RRGSceneResourceText) p_Section_Texts.getResource(s_id);
                                    s_id = p_labelComponent.getLabelFont().getResourceID();
                                    RRGSceneResourceFont p_font = (RRGSceneResourceFont) p_Section_Fonts.getResource(s_id);
                                    RRGSceneComponent_Label p_newComponent = new RRGSceneComponent_Label(s_id,i_componentIndex++, i_x, i_y, i_w, i_h, p_text, p_font, p_labelComponent.getLabelColor());
                                    p_rrg_form.getComponentsSection().addComponent(p_newComponent);
                                }
                            }
                        }
                        ;
                        break;
                    case AbstractFormComponent.COMPONENT_BUTTON:
                        {
                            RrgFormComponent_Button p_buttonComponent = (RrgFormComponent_Button) p_formComponent;

                            if (_saveButtonsComp)
                            {
                                RRGSceneResourceImage p_NormalImage = null;
                                RRGSceneResourceImage p_PressedImage = null;
                                RRGSceneResourceImage p_DisabledImage = null;
                                RRGSceneResourceImage p_SelectedImage = null;
                                RRGSceneResourceText p_ButtonText = null;
                                RRGSceneResourceFont p_ButtonFont = null;
                                RRGSceneResourceSound p_ButtonClickSound = null;

                                int i_textAlign = 0;

                                if (p_buttonComponent.getText() != null && _saveButtonTextsAsImages)
                                {
                                    // Normal
                                    if (p_buttonComponent.getNormalImage() != null)
                                    {
                                        String s_id = p_buttonComponent.getID() + s_formIndex+SAVETEXTASIMAGES_POSTFIX + "NORMAL";
                                        p_NormalImage = (RRGSceneResourceImage) p_Section_Images.getResource(s_id);
                                    }

                                    // Pressed
                                    if (p_buttonComponent.getPressedImage() != null)
                                    {
                                        String s_id = p_buttonComponent.getID() + s_formIndex+SAVETEXTASIMAGES_POSTFIX + "PRESSED";
                                        p_PressedImage = (RRGSceneResourceImage) p_Section_Images.getResource(s_id);
                                    }

                                    // Selected
                                    if (p_buttonComponent.getSelectedImage() != null)
                                    {
                                        String s_id = p_buttonComponent.getID() + s_formIndex+SAVETEXTASIMAGES_POSTFIX + "SELECTED";
                                        p_SelectedImage = (RRGSceneResourceImage) p_Section_Images.getResource(s_id);
                                    }

                                    // Disabled
                                    if (p_buttonComponent.getDisabledImage() != null)
                                    {
                                        String s_id = p_buttonComponent.getID() + s_formIndex+SAVETEXTASIMAGES_POSTFIX + "DISABLED";
                                        p_DisabledImage = (RRGSceneResourceImage) p_Section_Images.getResource(s_id);
                                    }
                                }
                                else
                                {
                                    // Normal
                                    if (p_buttonComponent.getNormalImage() != null)
                                    {
                                        String s_id = p_buttonComponent.getNormalImage().getResourceID();
                                        p_NormalImage = (RRGSceneResourceImage) p_Section_Images.getResource(s_id);
                                    }

                                    // Disabled
                                    if (p_buttonComponent.getDisabledImage() != null)
                                    {
                                        String s_id = p_buttonComponent.getDisabledImage().getResourceID();
                                        p_DisabledImage = (RRGSceneResourceImage) p_Section_Images.getResource(s_id);
                                    }

                                    // Selected
                                    if (p_buttonComponent.getSelectedImage() != null)
                                    {
                                        String s_id = p_buttonComponent.getSelectedImage().getResourceID();
                                        p_SelectedImage = (RRGSceneResourceImage) p_Section_Images.getResource(s_id);
                                    }

                                    // Pressed
                                    if (p_buttonComponent.getPressedImage() != null)
                                    {
                                        String s_id = p_buttonComponent.getPressedImage().getResourceID();
                                        p_PressedImage = (RRGSceneResourceImage) p_Section_Images.getResource(s_id);
                                    }

                                    if (p_buttonComponent.getText() != null)
                                    {
                                        String s_id = p_buttonComponent.getText().getResourceID();
                                        p_ButtonText = (RRGSceneResourceText) p_Section_Texts.getResource(s_id);

                                        s_id = p_buttonComponent.getFont().getResourceID();
                                        p_ButtonFont = (RRGSceneResourceFont) p_Section_Fonts.getResource(s_id);

                                        switch (p_buttonComponent.getTextHorzAlign())
                                        {
                                            case RrgFormComponent_Button.TEXT_ALIGN_HORZ_LEFT:
                                                {
                                                    i_textAlign = TEXT_ALIGN_HORZ_LEFT;
                                                }
                                                ;
                                                break;
                                            case RrgFormComponent_Button.TEXT_ALIGN_HORZ_CENTER:
                                                {
                                                    i_textAlign = TEXT_ALIGN_HORZ_CENTER;
                                                }
                                                ;
                                                break;
                                            case RrgFormComponent_Button.TEXT_ALIGN_HORZ_RIGHT:
                                                {
                                                    i_textAlign = TEXT_ALIGN_HORZ_RIGHT;
                                                }
                                                ;
                                                break;
                                        }

                                        switch (p_buttonComponent.getTextHorzAlign())
                                        {
                                            case RrgFormComponent_Button.TEXT_ALIGN_VERT_TOP:
                                                {
                                                    i_textAlign |= TEXT_ALIGN_VERT_TOP;
                                                }
                                                ;
                                                break;
                                            case RrgFormComponent_Button.TEXT_ALIGN_VERT_CENTER:
                                                {
                                                    i_textAlign |= TEXT_ALIGN_VERT_CENTER;
                                                }
                                                ;
                                                break;
                                            case RrgFormComponent_Button.TEXT_ALIGN_VERT_BOTTOM:
                                                {
                                                    i_textAlign |= TEXT_ALIGN_VERT_DOWN;
                                                }
                                                ;
                                                break;
                                        }
                                    }
                                }

                                if (p_buttonComponent.getClickSound() != null)
                                {
                                    String s_id = p_buttonComponent.getClickSound().getResourceID();
                                    p_ButtonClickSound = (RRGSceneResourceSound) p_Section_Sounds.getResource(s_id);
                                }


                                RRGSceneComponent_Button p_bComponent = new RRGSceneComponent_Button(p_buttonComponent.getID(),i_componentIndex++, i_x, i_y, i_w, i_h, i_textAlign, p_NormalImage, p_SelectedImage, p_DisabledImage, p_PressedImage, p_ButtonFont, p_ButtonText, p_ButtonClickSound);
                                p_rrg_form.getComponentsSection().addComponent(p_bComponent);
                            }
                        }
                        ;
                        break;
                }
            }
            p_formsSection.addForm(p_rrg_form);
        }
    }

    public byte[] toByteArray() throws IOException
    {
        //p_Section_Fonts.indexingResources();
        //p_Section_Images.indexingResources();
        //p_Section_Sounds.indexingResources();
        //p_Section_Texts.indexingResources();

        byte[] ab_FontsSection = p_Section_Fonts.saveResourceSection(lg_saveFontTypes);
        byte[] ab_ImageSection = p_Section_Images.saveResourceSection(lg_saveImageTypes);
        byte[] ab_SoundsSection = p_Section_Sounds.saveResourceSection(lg_saveSoundTypes);
        byte[] ab_TextsSection = p_Section_Texts.saveResourceSection(false);

        boolean lg_isImageSection = ab_ImageSection.length > 1;
        boolean lg_isFontsSection = ab_FontsSection.length > 1;
        boolean lg_isTextsSection = ab_TextsSection.length > 1;
        boolean lg_isSoundsSection = ab_SoundsSection.length > 1;

        int i_fontSectionLength = lg_isFontsSection && lg_saveFontsData ? ab_FontsSection.length : 0;
        int i_fontSectionVLQlen = lg_isFontsSection && lg_saveFontsData ? sizeOfVLQ(ab_FontsSection.length) : 0;

        int i_imageSectionLength = lg_isImageSection && lg_SaveImages ? ab_ImageSection.length : 0;
        int i_imagesSectionVLQlen = lg_isImageSection && lg_SaveImages ? sizeOfVLQ(ab_ImageSection.length) : 0;

        int i_soundSectionLength = lg_isSoundsSection && lg_saveSounds? ab_SoundsSection.length : 0;
        int i_soundsSectionVLQlen = lg_isSoundsSection && lg_saveSounds? sizeOfVLQ(ab_SoundsSection.length) : 0;

        int i_textSectionLength = lg_isTextsSection && lg_SaveTexts? ab_TextsSection.length : 0;
        int i_textSectionVLQlen = lg_isTextsSection && lg_SaveTexts? sizeOfVLQ(ab_TextsSection.length) : 0;

        byte[] ab_FormsSection = p_formsSection.toByteArray(lg_saveChannelData, lg_saveOffsetsToResources, lg_isImageSection, lg_isFontsSection, lg_isTextsSection, lg_isSoundsSection, lg_SaveImages, lg_saveFontsData, lg_saveSounds, lg_SaveTexts);
        int i_formsSectionLength = ab_FormsSection.length;
        int i_formsSectionVLQlen = sizeOfVLQ(i_formsSectionLength);

        // Размер заголовка 24 байта
        final int HEADER_LENGTH = 24;
        int i_dataSize = HEADER_LENGTH;
        i_dataSize += i_fontSectionLength + i_fontSectionVLQlen;
        i_dataSize += i_imageSectionLength + i_imagesSectionVLQlen;
        i_dataSize += i_soundSectionLength + i_soundsSectionVLQlen;
        i_dataSize += i_textSectionLength + i_textSectionVLQlen;
        i_dataSize += i_formsSectionLength + i_formsSectionVLQlen;

        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(i_dataSize);
        DataOutputStream p_daos = new DataOutputStream(p_baos);

        // Идентификатор
        p_daos.writeShort(0xFFDD);
        // Версия
        p_daos.writeShort(0x0100);
        // Размер файла
        RrgResourceSection._write3bytes(i_dataSize, p_daos);
        // Служебные флаги
        int i_flags = 0;

        i_flags |= lg_saveChannelData ? SERVICE_FLAG_CHANNELS_EXIST : 0;
        i_flags |= lg_saveOffsetsToResources ? SERVICE_FLAG_OFFSETS_EXIST : 0;
        i_flags |= lg_saveImageTypes ? SERVICE_FLAG_IMAGE_TYPES : 0;
        i_flags |= lg_saveSoundTypes ? SERVICE_FLAG_SOUNDS_TYPES : 0;
        i_flags |= lg_saveFontTypes ? SERVICE_FLAG_SOUNDS_TYPES : 0;

        p_daos.writeByte(i_flags);

        //Флаги наличия секций
        i_flags = 0;

        i_flags |= i_formsSectionLength > 1 ? SECTION_FLAG_FORM : 0;
        i_flags |= lg_isImageSection ? SECTION_FLAG_IMAGES : 0;
        i_flags |= lg_isSoundsSection ? SECTION_FLAG_SOUNDS : 0;
        i_flags |= lg_isTextsSection ? SECTION_FLAG_TEXTS : 0;
        i_flags |= lg_isFontsSection ? SECTION_FLAG_FONTS : 0;

        p_daos.writeByte(i_flags);

        // Смещения
        int i_offset = HEADER_LENGTH;

        // До секции IMAGES
        if (lg_SaveImages)
        {
            RrgResourceSection._write3bytes(i_offset, p_daos);
        }
        else
        {
            RrgResourceSection._write3bytes(0, p_daos);
        }
        i_offset += i_imageSectionLength + i_imagesSectionVLQlen;

        // До секции SOUNDS
        if (lg_saveSounds)
        {
            RrgResourceSection._write3bytes(i_offset, p_daos);
        }
        else
        {
            RrgResourceSection._write3bytes(0, p_daos);
        }
        i_offset += i_soundSectionLength + i_soundsSectionVLQlen;

        // До секции TEXTS
        if (lg_SaveTexts)
        {
            RrgResourceSection._write3bytes(i_offset, p_daos);
        }
        else
        {
            RrgResourceSection._write3bytes(0, p_daos);
        }
        i_offset += i_textSectionLength + i_textSectionVLQlen;

        // До секции FONTS
        if (lg_saveFontsData)
        {
            RrgResourceSection._write3bytes(i_offset, p_daos);
        }
        else
        {
            RrgResourceSection._write3bytes(0, p_daos);
        }
        i_offset += i_fontSectionLength + i_fontSectionVLQlen;

        // До секции FORMS
        RrgResourceSection._write3bytes(i_offset, p_daos);
        i_offset += i_formsSectionLength + i_formsSectionVLQlen;

        // Записываем секцию IMAGES
        if (lg_isImageSection && lg_SaveImages)
        {
            writeVLQ(p_daos, ab_ImageSection.length);
            p_daos.write(ab_ImageSection);
        }
        // Записываем секцию SOUNDS
        if (lg_isSoundsSection && lg_saveSounds)
        {
            writeVLQ(p_daos, ab_SoundsSection.length);
            p_daos.write(ab_SoundsSection);
        }
        // Записываем секцию TEXTS
        if (lg_isTextsSection && lg_SaveTexts)
        {
            writeVLQ(p_daos, ab_TextsSection.length);
            p_daos.write(ab_TextsSection);
        }
        // Записываем секцию FONTS
        if (lg_isFontsSection && lg_saveFontsData)
        {
            writeVLQ(p_daos, ab_FontsSection.length);
            p_daos.write(ab_FontsSection);
        }
        // Записываем секцию FORMS
        if (ab_FormsSection.length > 1)
        {
            writeVLQ(p_daos, ab_FormsSection.length);
            p_daos.write(ab_FormsSection);
        }
        p_daos.flush();
        p_daos.close();

        byte[] ab_destArray = p_baos.toByteArray();

        if (i_dataSize != ab_destArray.length) throw new IOException("Bad file length");

        return ab_destArray;
    }

    protected BufferedImage makeImageFromImageResource(RrgResource_Image _resource, int _scale, int _modifiers)
    {
        int i_width = 1;
        int i_height = 1;

        Image p_imageResource = _resource.getImage();

        if (p_imageResource != null)
        {
            i_width = _resource.getWidth() * _scale;
            i_height = _resource.getHeight() * _scale;
        }

        BufferedImage p_modifiedImage = new BufferedImage(i_width, i_height, BufferedImage.TYPE_INT_ARGB);
        synchronized (p_modifiedImage)
        {
            if (p_imageResource != null)
            {

                int[] ai_ImageBuffer = ((DataBufferInt) p_modifiedImage.getRaster().getDataBuffer()).getData();
                for (int li = 0; li < ai_ImageBuffer.length; li++) ai_ImageBuffer[li] = 0;
                ai_ImageBuffer = null;

                Graphics p_g = p_modifiedImage.getGraphics();
                p_g.drawImage(_resource.getImage(), 0, 0, i_width, i_height, null);

                ai_ImageBuffer = ((DataBufferInt) p_modifiedImage.getRaster().getDataBuffer()).getData();
                if ((_modifiers & RrgFormComponent_Image.MODIFIER_FLIP_VERT) != 0)
                {

                    // Вертикальный флип
                    if (i_height >= 2)
                    {
                        int i_topLineOffset = 0;
                        int i_downLineOffset = ai_ImageBuffer.length - i_width;

                        int[] ai_lineBuffer = new int[i_width];

                        while (true)
                        {
                            // Copy of the top line to the buffer
                            System.arraycopy(ai_ImageBuffer, i_topLineOffset, ai_lineBuffer, 0, i_width);
                            // Copy of the down line to the top line
                            System.arraycopy(ai_ImageBuffer, i_downLineOffset, ai_ImageBuffer, i_topLineOffset, i_width);
                            // Copy the buffer to the down line
                            System.arraycopy(ai_lineBuffer, 0, ai_ImageBuffer, i_downLineOffset, i_width);

                            i_topLineOffset += i_width;
                            i_downLineOffset -= i_width;

                            if (i_topLineOffset > i_downLineOffset) break;
                        }
                    }
                }

                // Горизонтальный флип
                if ((_modifiers & RrgFormComponent_Image.MODIFIER_FLIP_HORZ) != 0)
                {
                    if (i_width >= 2)
                    {
                        int i_leftLineOffset = 0;
                        int i_rightLineOffset = i_leftLineOffset + i_width - 1;
                        int i_maxDown = i_rightLineOffset + ((i_height - 1) * i_width);

                        while (i_leftLineOffset < i_rightLineOffset)
                        {
                            int i_vertLOffst = i_leftLineOffset;
                            int i_vertROffst = i_rightLineOffset;
                            while (i_vertLOffst < i_maxDown)
                            {
                                int i_buff = ai_ImageBuffer[i_vertLOffst];
                                ai_ImageBuffer[i_vertLOffst] = ai_ImageBuffer[i_vertROffst];
                                ai_ImageBuffer[i_vertROffst] = i_buff;
                                i_vertLOffst += i_width;
                                i_vertROffst += i_width;
                            }

                            i_leftLineOffset++;
                            i_rightLineOffset--;
                        }
                    }
                }
            }
        }
        return p_modifiedImage;
    }

    public RrgResourceSection_Images getImagesSection()
    {
        return p_Section_Images;
    }

    public RrgResourceSection_Sounds getSoundsSection()
    {
        return p_Section_Sounds;
    }

    public RrgResourceSection_Texts getTextsSection()
    {
        return p_Section_Texts;
    }

    public RrgResourceSection_Fonts getFontsSection()
    {
        return p_Section_Fonts;
    }

    public boolean isFontEnabled()
    {
        return lg_saveFontsData;
    }

    public boolean isSoundEnabled()
    {
        return lg_saveSounds;
    }

    public boolean isTextEnabled()
    {
        return lg_SaveTexts;
    }

    public boolean isImageEnabled()
    {
        return lg_SaveImages;
    }

    public RRGScene_FormsSection getFormsSection()
    {
        return p_formsSection;
    }

    public boolean hasChannelData()
    {
        return lg_saveChannelData;
    }

    public boolean hasOffsetsToResources()
    {
        return lg_saveOffsetsToResources;
    }

    protected void fillResourceContainersFromForms(FormsList _list, boolean _saveModifiedImagesAsImages, int _ImageFormat, boolean _saveTextAsImages, boolean _saveButtonTextsAsImages) throws IOException
    {
        p_Section_Images = new RrgResourceSection_Images();
        p_Section_Fonts = new RrgResourceSection_Fonts();
        p_Section_Sounds = new RrgResourceSection_Sounds();
        p_Section_Texts = new RrgResourceSection_Texts();

        for (int lf = 0; lf < _list.getSize(); lf++)
        {
            FormContainer p_form = _list.getFormAt(lf);

            String s_formIndex = "_"+lf+"_";

            for (int li = 0; li < p_form.getSize(); li++)
            {
                AbstractFormComponent p_formComponent = (AbstractFormComponent) p_form.getComponentAt(li);

                switch (p_formComponent.getType())
                {
                    case AbstractFormComponent.COMPONENT_IMAGE:
                        {
                            RrgFormComponent_Image p_imageComponent = (RrgFormComponent_Image) p_formComponent;

                            if ((p_imageComponent.getScale()!=1 || p_imageComponent.getModifiers()!=RrgFormComponent_Image.MODIFIER_NONE) && _saveModifiedImagesAsImages)
                            {
                                String s_id = p_imageComponent.getImageResource().getResourceID() + s_formIndex + SCALE_POSTFIX + p_imageComponent.getScale()+"m"+p_imageComponent.getModifiers();
                                if (!p_Section_Images.containsResourceForID(s_id))
                                {
                                    BufferedImage p_image = makeImageFromImageResource(p_imageComponent.getImageResource(), p_imageComponent.getScale(), p_imageComponent.getModifiers());
                                    RRGSceneResourceImage p_newResourceImage = new RRGSceneResourceImage(s_id, _ImageFormat, p_image);
                                    p_Section_Images.addResource(p_newResourceImage);
                                }
                            }
                            else
                            {
                                String s_id = p_imageComponent.getImageResource().getResourceID();
                                if (!p_Section_Images.containsResourceForID(p_imageComponent.getImageResource().getResourceID()))
                                {
                                    BufferedImage p_image = makeImageFromImageResource(p_imageComponent.getImageResource(), 1, RrgFormComponent_Image.MODIFIER_NONE);
                                    RRGSceneResourceImage p_newResourceImage = new RRGSceneResourceImage(s_id, _ImageFormat, p_image);

                                    p_Section_Images.addResource(p_newResourceImage);
                                }
                            }
                        }
                        ;
                        break;
                    case AbstractFormComponent.COMPONENT_LABEL:
                        {
                            RrgFormComponent_Label p_labelComponent = (RrgFormComponent_Label) p_formComponent;

                            if (_saveTextAsImages)
                            {
                                BufferedImage p_image = getImageFromComponent(p_labelComponent);
                                String s_id = p_labelComponent.getID()+s_formIndex + SAVETEXTASIMAGES_POSTFIX;

                                RRGSceneResourceImage p_newResourceImage = new RRGSceneResourceImage(s_id, _ImageFormat, p_image);

                                p_Section_Images.addResource(p_newResourceImage);
                            }
                            else
                            {
                                String s_id = p_labelComponent.getLabelText().getResourceID();
                                if (!p_Section_Texts.containsResourceForID(s_id))
                                {
                                    RRGSceneResourceText p_newResourceText = new RRGSceneResourceText(s_id, p_labelComponent.getLabelText().getText());
                                    p_Section_Texts.addResource(p_newResourceText);
                                }

                                s_id = p_labelComponent.getLabelFont().getResourceID();

                                if (!p_Section_Fonts.containsResourceForID(p_labelComponent.getLabelFont().getResourceID()))
                                {
                                    File p_fontFile = p_labelComponent.getLabelFont().getFontFile();
                                    byte[] ab_fontArray = new byte[(int) p_fontFile.length()];

                                    FileInputStream p_fis = new FileInputStream(p_fontFile);
                                    p_fis.read(ab_fontArray);
                                    p_fis.close();

                                    RRGSceneResourceFont p_newResourceFont = new RRGSceneResourceFont(s_id, (p_labelComponent.getLabelFont().getFont() instanceof TTFFont ? TYPE_FONT_TTF : TYPE_FONT_BFT), ab_fontArray);
                                    p_Section_Fonts.addResource(p_newResourceFont);
                                }
                            }
                        }
                        ;
                        break;
                    case AbstractFormComponent.COMPONENT_BUTTON:
                        {
                            RrgFormComponent_Button p_buttonComponent = (RrgFormComponent_Button) p_formComponent;

                            if (p_buttonComponent.getText() != null && _saveButtonTextsAsImages)
                            {
                                int i_oldState = p_buttonComponent.getState();

                                // Записываем состояния кнопки как картинки с выведенным текстом

                                // Normal
                                if (p_buttonComponent.getNormalImage() != null)
                                {
                                    p_buttonComponent.setState(AbstractFormComponent.STATE_NORMAL);

                                    String s_id = p_buttonComponent.getID() + s_formIndex +SAVETEXTASIMAGES_POSTFIX + "NORMAL";
                                    BufferedImage p_image = getImageFromComponent(p_buttonComponent);
                                    RRGSceneResourceImage p_newResourceImage = new RRGSceneResourceImage(s_id, _ImageFormat, p_image);

                                    p_Section_Images.addResource(p_newResourceImage);
                                }

                                // Selected
                                if (p_buttonComponent.getSelectedImage() != null)
                                {
                                    p_buttonComponent.setState(AbstractFormComponent.STATE_SELECTED);

                                    String s_id = p_buttonComponent.getID() + s_formIndex +SAVETEXTASIMAGES_POSTFIX + "SELECTED";
                                    BufferedImage p_image = getImageFromComponent(p_buttonComponent);
                                    RRGSceneResourceImage p_newResourceImage = new RRGSceneResourceImage(s_id, _ImageFormat, p_image);

                                    p_Section_Images.addResource(p_newResourceImage);
                                }

                                // Pressed
                                if (p_buttonComponent.getPressedImage() != null)
                                {
                                    p_buttonComponent.setState(AbstractFormComponent.STATE_PRESSED);

                                    String s_id = p_buttonComponent.getID() + s_formIndex +SAVETEXTASIMAGES_POSTFIX + "PRESSED";
                                    BufferedImage p_image = getImageFromComponent(p_buttonComponent);
                                    RRGSceneResourceImage p_newResourceImage = new RRGSceneResourceImage(s_id, _ImageFormat, p_image);

                                    p_Section_Images.addResource(p_newResourceImage);
                                }

                                // Disabled
                                if (p_buttonComponent.getDisabledImage() != null)
                                {
                                    p_buttonComponent.setState(AbstractFormComponent.STATE_DISABLED);

                                    String s_id = p_buttonComponent.getID() + s_formIndex +SAVETEXTASIMAGES_POSTFIX + "DISABLED";
                                    BufferedImage p_image = getImageFromComponent(p_buttonComponent);
                                    RRGSceneResourceImage p_newResourceImage = new RRGSceneResourceImage(s_id, _ImageFormat, p_image);

                                    p_Section_Images.addResource(p_newResourceImage);
                                }

                                p_buttonComponent.setState(i_oldState);
                            }
                            else
                            {
                                if (p_buttonComponent.getText() != null)
                                {
                                    String s_id = p_buttonComponent.getText().getResourceID();
                                    if (!p_Section_Texts.containsResourceForID(s_id))
                                    {
                                        RRGSceneResourceText p_newResourceText = new RRGSceneResourceText(s_id, p_buttonComponent.getText().getText());
                                        p_Section_Texts.addResource(p_newResourceText);
                                    }
                                }

                                if (p_buttonComponent.getFont() != null)
                                {
                                    String s_id = p_buttonComponent.getFont().getResourceID();

                                    if (!p_Section_Fonts.containsResourceForID(p_buttonComponent.getFont().getResourceID()))
                                    {
                                        File p_fontFile = p_buttonComponent.getFont().getFontFile();
                                        byte[] ab_fontArray = new byte[(int) p_fontFile.length()];

                                        FileInputStream p_fis = new FileInputStream(p_fontFile);
                                        p_fis.read(ab_fontArray);
                                        p_fis.close();

                                        RRGSceneResourceFont p_newResourceFont = new RRGSceneResourceFont(s_id, (p_buttonComponent.getFont().getFont() instanceof TTFFont ? TYPE_FONT_TTF : TYPE_FONT_BFT), ab_fontArray);
                                        p_Section_Fonts.addResource(p_newResourceFont);
                                    }
                                }

                                // Normal
                                if (p_buttonComponent.getNormalImage() != null)
                                {
                                    String s_id = p_buttonComponent.getNormalImage().getResourceID();
                                    if (!p_Section_Images.containsResourceForID(s_id))
                                    {
                                        BufferedImage p_image = makeImageFromImageResource(p_buttonComponent.getNormalImage(), 1, RrgFormComponent_Image.MODIFIER_NONE);
                                        RRGSceneResourceImage p_newResourceImage = new RRGSceneResourceImage(s_id, _ImageFormat, p_image);
                                        p_Section_Images.addResource(p_newResourceImage);
                                    }
                                }

                                // Pressed
                                if (p_buttonComponent.getPressedImage() != null)
                                {
                                    String s_id = p_buttonComponent.getPressedImage().getResourceID();
                                    if (!p_Section_Images.containsResourceForID(s_id))
                                    {
                                        BufferedImage p_image = makeImageFromImageResource(p_buttonComponent.getPressedImage(), 1, RrgFormComponent_Image.MODIFIER_NONE);
                                        RRGSceneResourceImage p_newResourceImage = new RRGSceneResourceImage(s_id, _ImageFormat, p_image);
                                        p_Section_Images.addResource(p_newResourceImage);
                                    }
                                }

                                // Selected
                                if (p_buttonComponent.getSelectedImage() != null)
                                {
                                    String s_id = p_buttonComponent.getSelectedImage().getResourceID();
                                    if (!p_Section_Images.containsResourceForID(s_id))
                                    {
                                        BufferedImage p_image = makeImageFromImageResource(p_buttonComponent.getSelectedImage(), 1, RrgFormComponent_Image.MODIFIER_NONE);
                                        RRGSceneResourceImage p_newResourceImage = new RRGSceneResourceImage(s_id, _ImageFormat, p_image);
                                        p_Section_Images.addResource(p_newResourceImage);
                                    }
                                }

                                // Disabled
                                if (p_buttonComponent.getDisabledImage() != null)
                                {
                                    String s_id = p_buttonComponent.getDisabledImage().getResourceID();
                                    if (!p_Section_Images.containsResourceForID(s_id))
                                    {
                                        BufferedImage p_image = makeImageFromImageResource(p_buttonComponent.getDisabledImage(), 1, RrgFormComponent_Image.MODIFIER_NONE);
                                        RRGSceneResourceImage p_newResourceImage = new RRGSceneResourceImage(s_id, _ImageFormat, p_image);
                                        p_Section_Images.addResource(p_newResourceImage);
                                    }
                                }
                            }

                            if (p_buttonComponent.getClickSound() != null)
                            {
                                String s_id = p_buttonComponent.getClickSound().getResourceID();
                                if (!p_Section_Sounds.containsResourceForID(s_id))
                                {
                                    File p_soundFile = p_buttonComponent.getClickSound().getSoundFile();
                                    byte[] ab_soundArray = new byte[(int) p_soundFile.length()];

                                    FileInputStream p_fis = new FileInputStream(p_soundFile);
                                    p_fis.read(ab_soundArray);
                                    p_fis.close();

                                    //TODO Сделать вычисление длительности звукового файла
                                    RRGSceneResourceSound p_newResourceSound = new RRGSceneResourceSound(s_id, TYPE_SOUND_WAV, ab_soundArray, 1000);
                                    p_Section_Sounds.addResource(p_newResourceSound);
                                }
                            }
                        }
                        ;
                        break;
                }
            }
        }
    }

    protected BufferedImage getImageFromComponent(AbstractFormComponent _component)
    {
        int i_width = _component.getWidth();
        int i_heigth = _component.getHeight();

        BufferedImage p_bufImage = new BufferedImage(i_width, i_heigth, BufferedImage.TYPE_INT_ARGB);

        int[] ai_intData = ((DataBufferInt) p_bufImage.getRaster().getDataBuffer()).getData();

        for (int li = 0; li < ai_intData.length; li++)
        {
            ai_intData[li] = 0x00000000;
        }

        ai_intData = null;

        Graphics p_graphics = p_bufImage.getGraphics();
        p_graphics.setClip(0, 0, i_width, i_heigth);

        _component.drawComponentContent(p_graphics);

        return p_bufImage;
    }

    public static final int convertValueToVLQ(int _value)
    {
        int i_buffer = _value & 0x7F;
        while ((_value >>>= 7) > 0)
        {
            i_buffer <<= 8;
            i_buffer |= 0x80;
            i_buffer += (_value & 0x7f);
        }

        return i_buffer;
    }

    public static final void writeVLQ(OutputStream _inStream, int _value) throws IOException
    {
        int i_buffer = convertValueToVLQ(_value);
        while (true)
        {
            _inStream.write(i_buffer);
            if ((i_buffer & 0x80) != 0)
                i_buffer >>>= 8;
            else
                break;
        }
    }

    public static final int sizeOfVLQ(int _value)
    {
        int i_sz = 1;
        while ((_value >>>= 7) > 0)
        {
            i_sz++;
        }
        return i_sz;
    }
}

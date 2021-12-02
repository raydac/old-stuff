package com.raydac_research.RRGImagePacker.Container;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ImagePropertiesPanel implements IPropertiesPanel, ActionListener
{
    private JPanel p_imagePropPanel;
    private JTextField p_TextImageID;
    private JLabel p_LabelFileName;
    private JTextField p_TextExternalImage;
    private JComboBox p_ComboLinkedImages;
    private JCheckBox p_FlagCropped;
    private JButton p_ButtonAutoCrop;
    private JComboBox p_ComboUsedPalettes;
    private JButton p_ButtonTestPreview;
    private JComboBox p_ComboOptimizationLevel;
    private JComboBox p_ComboFormat;
    private JSlider p_SliderCompression;
    private JLabel p_LabelWidth;
    private JLabel p_LabelHeight;
    private JTextField p_TextCropX;
    private JTextField p_TextCropY;
    private JTextField p_TextCropW;
    private JTextField tp_TextCropH;

    private static final String STRING_NULL = "";
    private static final String STRING_PNG = "PNG";
    private static final String STRING_JPEG = "JPEG";
    private static final String STRING_TGA = "TGA";

    private static final String STRING_SIZE = "SIZE";
    private static final String STRING_SPEED = "SPEED";

    private static final String STRING_ROTATE0 = "0";
    private static final String STRING_ROTATE90 = "90";
    private static final String STRING_ROTATE180 = "180";
    private static final String STRING_ROTATE270 = "270";

    private static final String STRING_RARELY = "Rarely";
    private static final String STRING_NOTRARELY = "Not rarely";
    private static final String STRING_OFTEN = "Often";

    private JLabel p_Label_Colors;
    private JComboBox p_ComboMagnify;

    private ImageViewComponent p_viewComponent;
    private JScrollPane p_previewImagePanel;
    private JPanel p_VPanel;
    private JTextField p_TextGroupID;
    private JTextArea p_CommentsArea;
    private JCheckBox p_CheckboxTiling;
    private JTextField p_TextHorizontalTiling;
    private JTextField p_TextVerticalTiling;

    private int i_imageWidth;
    private int i_imageHeight;
    private JButton p_RefreshImageButton;
    private JCheckBox p_FlagVertFlip;
    private JCheckBox p_FlagHorzFlip;
    private JComboBox p_ComboBox_Rotation;
    private JComboBox p_ComboUsageStatus;

    protected void setUsage(int _state)
    {
        switch (_state)
        {
            case ImageCnt.USAGE_RARELY:
                {
                    p_ComboUsageStatus.setSelectedIndex(0);
                }
                ;
                break;
            case ImageCnt.USAGE_NOTRARELY:
                {
                    p_ComboUsageStatus.setSelectedIndex(1);
                }
                ;
                break;
            case ImageCnt.USAGE_OFTEN:
                {
                    p_ComboUsageStatus.setSelectedIndex(2);
                }
                ;
                break;
        }
    }

    protected int getUsage()
    {
        switch (p_ComboUsageStatus.getSelectedIndex())
        {
            case 0:
                {
                    return ImageCnt.USAGE_RARELY;
                }
            case 1:
                {
                    return ImageCnt.USAGE_NOTRARELY;
                }
            case 2:
                {
                    return ImageCnt.USAGE_OFTEN;
                }
        }

        return -1;
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource().equals(p_ComboMagnify))
        {
            if (p_viewComponent != null)
            {
                p_viewComponent.setScale(getMagnify());
                p_previewImagePanel.updateUI();
            }
        }
    }

    protected void setGroupID(int _id)
    {
        if (_id >= 0)
        {
            p_TextGroupID.setText("" + _id);
        }
        else
        {
            p_TextGroupID.setText("");
        }
    }

    public int getGroupID()
    {
        String s_id = p_TextGroupID.getText().trim();
        if (s_id.length() == 0) return -1;
        int i_id = -1;

        try
        {
            i_id = Integer.parseInt(s_id);

            if (i_id < 0) i_id = -1;
        }
        catch (NumberFormatException e)
        {
        }

        return i_id;
    }

    private static final int[][] COMPRESS_RESTRICTIONS = new int[][]
    {
        // для PNG
        new int[]{0, 9}
    };

    protected ImageCnt getLinkedImage()
    {
        if (p_ComboLinkedImages.getSelectedItem().equals(STRING_NULL)) return null;
        return (ImageCnt) p_ComboLinkedImages.getSelectedItem();
    }

    protected void setLinkedImage(ImageCnt _image)
    {
        if (_image == null)
        {
            p_ComboLinkedImages.setSelectedItem(STRING_NULL);
        }
        else
        {
            p_ComboLinkedImages.setSelectedItem(_image);
        }
    }

    protected PaletteCnt getUsedPalette()
    {
        if (p_ComboUsedPalettes.getSelectedItem().equals(STRING_NULL)) return null;
        return (PaletteCnt) p_ComboUsedPalettes.getSelectedItem();
    }

    protected void setUsedPalette(PaletteCnt _palette)
    {
        if (_palette == null)
        {
            p_ComboUsedPalettes.setSelectedItem(STRING_NULL);
        }
        else
        {
            p_ComboUsedPalettes.setSelectedItem(_palette);
        }
    }

    protected void setCropped(boolean _cropped)
    {
        p_FlagCropped.setSelected(_cropped);
    }

    protected boolean isCropped()
    {
        return p_FlagCropped.isSelected();
    }

    protected Rectangle getCropRectangle()
    {
        String s_strX = p_TextCropX.getText();
        String s_strY = p_TextCropY.getText();
        String s_strW = p_TextCropW.getText();
        String s_strH = tp_TextCropH.getText();

        int i_x, i_y, i_w, i_h;

        try
        {
            i_x = Integer.parseInt(s_strX);
            i_y = Integer.parseInt(s_strY);
            i_w = Integer.parseInt(s_strW);
            i_h = Integer.parseInt(s_strH);
        }
        catch (NumberFormatException ex)
        {
            return null;
        }

        return new Rectangle(i_x, i_y, i_w, i_h);
    }

    protected void settCropRectangle(Rectangle _rect)
    {
        p_TextCropX.setText("" + _rect.x);
        p_TextCropY.setText("" + _rect.y);
        p_TextCropW.setText("" + _rect.width);
        tp_TextCropH.setText("" + _rect.height);
    }

    protected String getExternalLinkedImage()
    {
        return p_TextExternalImage.getText().trim();
    }

    protected void setID(String _id)
    {
        p_TextImageID.setText(_id);
    }

    protected void setSource(String _source)
    {
        p_LabelFileName.setText(_source);
    }

    protected String getID()
    {
        return p_TextImageID.getText().trim();
    }

    protected void setExternalLinkedImage(String _name)
    {
        p_TextExternalImage.setText(_name);
    }

    protected void setWidthHeight(Dimension _dim)
    {
        p_LabelWidth.setText("" + _dim.width);
        p_LabelHeight.setText("" + _dim.height);
    }

    protected void setOptimization(int _value)
    {
        switch (_value)
        {
            case ImageCnt.OPTIMIZATION_SIZE:
                {
                    p_ComboOptimizationLevel.setSelectedItem(STRING_SIZE);
                }
                ;
                break;
            case ImageCnt.OPTIMIZATION_SPEED:
                {
                    p_ComboOptimizationLevel.setSelectedItem(STRING_SPEED);
                }
                ;
                break;
        }
    }

    protected int getOptimization()
    {
        if (p_ComboOptimizationLevel.getSelectedItem().equals(STRING_SIZE))
        {
            return ImageCnt.OPTIMIZATION_SIZE;
        }
        else if (p_ComboOptimizationLevel.getSelectedItem().equals(STRING_SPEED))
        {
            return ImageCnt.OPTIMIZATION_SPEED;
        }
        return -1;
    }

    protected int getCompressValue()
    {
        return p_SliderCompression.getValue();
    }

    protected int getFormat()
    {
        if (p_ComboFormat.getSelectedItem().equals(STRING_PNG))
        {
            return ImageCnt.FORMAT_PNG;
        }
        else if (p_ComboFormat.getSelectedItem().equals(STRING_JPEG))
        {
            return ImageCnt.FORMAT_JPG;
        }
        else if (p_ComboFormat.getSelectedItem().equals(STRING_TGA))
        {
            return ImageCnt.FORMAT_TGA;
        }
        return -1;
    }

    protected boolean getFlipVert()
    {
        return p_FlagVertFlip.isSelected();
    }

    protected boolean getFlipHorz()
    {
        return p_FlagHorzFlip.isSelected();
    }

    protected void setFlipVert(boolean _state)
    {
        p_FlagVertFlip.setSelected(_state);
    }

    protected void setFlipHorz(boolean _state)
    {
        p_FlagHorzFlip.setSelected(_state);
    }

    protected int getRotation()
    {
        switch (p_ComboBox_Rotation.getSelectedIndex())
        {
            case 0:
                return ImageCnt.ROTATE_0;
            case 1:
                return ImageCnt.ROTATE_90;
            case 2:
                return ImageCnt.ROTATE_180;
            case 3:
                return ImageCnt.ROTATE_270;
        }
        return -1;
    }

    protected void setRotation(int _value)
    {
        switch (_value)
        {
            case ImageCnt.ROTATE_0:
                p_ComboBox_Rotation.setSelectedIndex(0);
                break;
            case ImageCnt.ROTATE_90:
                p_ComboBox_Rotation.setSelectedIndex(1);
                break;
            case ImageCnt.ROTATE_180:
                p_ComboBox_Rotation.setSelectedIndex(2);
                break;
            case ImageCnt.ROTATE_270:
                p_ComboBox_Rotation.setSelectedIndex(3);
                break;
        }
    }

    protected void setFormatAndCompression(int _format, int _value)
    {
        int[] ai_values = null;
        boolean lg_showNumbers = false;
        boolean lg_show = true;

        switch (_format)
        {
            case ImageCnt.FORMAT_PNG:
                {
                    p_ComboFormat.setSelectedItem(STRING_PNG);
                    ai_values = COMPRESS_RESTRICTIONS[0];
                    lg_showNumbers = true;
                }
                ;
                break;
        }

        p_SliderCompression.setMinimum(ai_values[0]);
        p_SliderCompression.setMaximum(ai_values[1]);
        p_SliderCompression.setPaintTicks(lg_showNumbers);
        p_SliderCompression.setValue(_value);
        p_SliderCompression.setVisible(lg_show);
    }

    public ImagePropertiesPanel()
    {
        p_ComboOptimizationLevel.addItem(STRING_SIZE);
        p_ComboOptimizationLevel.addItem(STRING_SPEED);

        p_ComboUsageStatus.addItem(STRING_RARELY);
        p_ComboUsageStatus.addItem(STRING_NOTRARELY);
        p_ComboUsageStatus.addItem(STRING_OFTEN);

        p_ComboBox_Rotation.addItem(STRING_ROTATE0);
        p_ComboBox_Rotation.addItem(STRING_ROTATE90);
        p_ComboBox_Rotation.addItem(STRING_ROTATE180);
        p_ComboBox_Rotation.addItem(STRING_ROTATE270);

        p_ComboFormat.addItem(STRING_PNG);

        // Magnify
        for (int li = 0; li < 8; li++)
        {
            p_ComboMagnify.addItem("x" + (li + 1));
        }

        p_viewComponent = new ImageViewComponent();
        p_ComboMagnify.addActionListener(this);

        p_VPanel.setLayout(new BorderLayout());
        p_VPanel.add(p_viewComponent, BorderLayout.CENTER);
    }

    public String isDataOk()
    {
        Rectangle p_rect = getCropRectangle();
        if (p_rect == null) return "You have bad crop parameters";
        String s_str = getID();
        if (s_str.length() == 0) return "You must define the ID for the image";

        String s_id = p_TextGroupID.getText().trim();
        int i_id = -1;

        if (s_id.length() > 0)
        {
            try
            {
                i_id = Integer.parseInt(s_id);
                if (i_id < 0) i_id = -1;
            }
            catch (NumberFormatException e)
            {
                return "You have a bad value as the Group ID";
            }
        }

        String s_tileHorz = p_TextHorizontalTiling.getText().trim();
        String s_tileVert = p_TextVerticalTiling.getText().trim();

        int i_ht = 0;
        int i_vt = 0;

        try
        {
            i_ht = Integer.parseInt(s_tileHorz);
            i_vt = Integer.parseInt(s_tileVert);
        }
        catch (NumberFormatException e)
        {
            return "You have a bad value as a tile parameter";
        }

        if (i_ht <= 0) return "You have a bad value for horizontal tile parameter";
        if (i_vt <= 0) return "You have a bad value for vertical tile parameter";

        if (i_imageWidth != 0 && i_imageHeight != 0)
        {
            if (i_imageWidth / i_ht <= 1) return "Too many horizontal tiles ";
            if (i_imageHeight / i_vt <= 1) return "Too many vertical tiles ";
        }

        return null;
    }

    protected void setColors(int _colors)
    {
        if (_colors > 256)
        {
            p_Label_Colors.setText(">256");
        }
        else
        {
            p_Label_Colors.setText("" + _colors);
        }
    }

    protected Dimension getTileValues()
    {
        String s_tileHorz = p_TextHorizontalTiling.getText().trim();
        String s_tileVert = p_TextVerticalTiling.getText().trim();

        int i_ht = 0;
        int i_vt = 0;

        i_ht = Integer.parseInt(s_tileHorz);
        i_vt = Integer.parseInt(s_tileVert);

        return new Dimension(i_ht, i_vt);
    }

    protected void setTileFlag(boolean _state)
    {
        p_CheckboxTiling.setSelected(_state);
    }

    protected boolean getTileFlag()
    {
        return p_CheckboxTiling.isSelected();
    }

    protected void setTileValues(Dimension _values)
    {
        p_TextHorizontalTiling.setText("" + _values.width);
        p_TextVerticalTiling.setText("" + _values.height);
    }

    protected void setComments(String _comments)
    {
        p_CommentsArea.setText(_comments);
    }

    protected String getComments()
    {
        return p_CommentsArea.getText();
    }

    protected void fillPanelFromObject(Object _object)
    {
        ImageCnt p_img = (ImageCnt) _object;

        p_RefreshImageButton.setEnabled(true);
        p_ButtonAutoCrop.setEnabled(true);
        p_ButtonTestPreview.setEnabled(true);

        if (p_img.isExternalImageLink())
        {
            p_RefreshImageButton.setEnabled(false);
            p_ButtonAutoCrop.setEnabled(false);
            p_ButtonTestPreview.setEnabled(false);
        }

        if (p_img.isLink())
        {
            p_RefreshImageButton.setEnabled(false);
        }

        i_imageWidth = p_img.getDimension().width;
        i_imageHeight = p_img.getDimension().height;

        setRotation(p_img.getRotationState());
        setFlipHorz(p_img.isFlippedHorizondal());
        setFlipVert(p_img.isFlippedVertical());

        setCropped(p_img.isCropped());
        setFormatAndCompression(p_img.getFormat(), p_img.getCompress());
        setLinkedImage(p_img.getLinkedImage());
        setOptimization(p_img.getOptimization());
        settCropRectangle(p_img.getCropRegion());
        setUsedPalette(p_img.getUsedPalette());
        setWidthHeight(p_img.getDimension());

        setUsage(p_img.getUsage());

        setMagnify(p_img.getMagnify());

        setExternalLinkedImage(p_img.getExternalImageLink());
        setID(p_img.getName());

        setSource(p_img.getSource());
        setColors(p_img.getColors());

        setGroupID(p_img.getGroupID());

        setComments(p_img.getComments());

        setTileValues(p_img.getTileDim());
        setTileFlag(p_img.isTile());

        p_viewComponent.setImageContainer(p_img);
        p_viewComponent.setScale(p_img.getMagnify());
    }

    protected void setMagnify(int _magnify)
    {
        p_ComboMagnify.setSelectedIndex(_magnify - 1);
    }

    protected int getMagnify()
    {
        return p_ComboMagnify.getSelectedIndex() + 1;
    }

    public JPanel getPanel(Object _image, Container p_container)
    {
        if (_image == null) return null;

        p_ComboLinkedImages.removeAllItems();
        int i_size = p_container.getImageContainer().getSize();
        p_ComboLinkedImages.addItem(STRING_NULL);
        for (int li = 0; li < i_size; li++)
        {
            ImageCnt p_img = p_container.getImageContainer().getImageAt(li);
            if (_image.equals(p_img)) continue;
            if (p_img.isLink()) continue;
            p_ComboLinkedImages.addItem(p_img);
        }

        p_ComboUsedPalettes.removeAllItems();
        i_size = p_container.getPaletteContainer().getSize();
        p_ComboUsedPalettes.addItem(STRING_NULL);
        for (int li = 0; li < i_size; li++)
        {
            PaletteCnt p_pal = p_container.getPaletteContainer().getPaletteAt(li);
            p_ComboUsedPalettes.addItem(p_pal);
        }

        fillPanelFromObject(_image);

        return p_imagePropPanel;
    }

    public void fillObjectFromPanel(Object _object)
    {
        ImageCnt p_img = (ImageCnt) _object;

        p_img.setRotationState(getRotation());
        p_img.setFlipHorzState(getFlipHorz());
        p_img.setFlipVertState(getFlipVert());

        p_img.setUsage(getUsage());

        p_img.setCropRegion(getCropRectangle());
        p_img.setCropFlag(isCropped());
        p_img.setFormat(getFormat());
        p_img.setCompress(getCompressValue());

        p_img.setLinkedImage(getLinkedImage());
        p_img.setOptimization(getOptimization());
        p_img.setUsedPalette(getUsedPalette());

        p_img.setMagnify(getMagnify());

        p_img.setGroupID(getGroupID());

        p_img.setExternalImageLink(getExternalLinkedImage());
        p_img.setName(getID());

        p_img.setComments(getComments());

        p_img.setTileDim(getTileValues());
        p_img.setTileFlag(getTileFlag());
    }
}

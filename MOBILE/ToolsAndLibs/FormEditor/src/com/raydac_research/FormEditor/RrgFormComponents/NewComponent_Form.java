package com.raydac_research.FormEditor.RrgFormComponents;

import com.raydac_research.FormEditor.RrgFormResources.*;
import com.raydac_research.FormEditor.Misc.Utilities;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class NewComponent_Form extends JDialog implements ActionListener
{
    private JPanel p_mainPanel;
    private JTextField p_ComponentIDText;

    private static final int STATE_SELECT_TYPE = 0;
    private static final int STATE_FILL_PROPERTIES = 1;
    private static final int STATE_FORM_PROPERTIES = 2;

    private int i_State;
    private JPanel p_ContentPanel;

    private FormContainer p_formContainer;

    private AbstractFormComponent p_result;
    private AbstractFormComponentPanel p_componentPanel;

    public ComponentImageProperties p_ComponentImageProperties;
    public ComponentButtonProperties p_ComponentButtonProperties;
    public ComponentFormProperties p_ComponentFormProperties;
    public ComponentLabelProperties p_ComponentLabelProperties;
    public ComponentPathProperties p_ComponentPathProperties;
    public ComponentCustomAreaProperties p_ComponentCustomAreaProperties;

    private static final String s_NullItem = "    ";

    private PathPointDialog p_PathPointDialog;
    private JTextField p_YField;
    private JTextField p_XField;

    public interface AbstractFormComponentPanel
    {
        public String isDataOk();
        public AbstractFormComponent makeNewComponent(FormContainer _parent,String _id);
        public void fillPropertiesFromComponent(AbstractFormComponent _component, ResourceContainer _container);
        public void fillComponentFromProperties(AbstractFormComponent _component, ResourceContainer _container);
        public JPanel getPanel(ResourceContainer _container);
        public void updateResourceContainer(ResourceContainer _resources);
        public void initPanelDataForNewComponent();
    }

    public class ComponentFormProperties implements AbstractFormComponentPanel, ActionListener
    {
        private JButton p_ChangeBackgroundColorButton;
        private JLabel p_colorLabel;
        private JTextField p_WidthField;
        private JTextField p_HeightField;
        private JPanel p_MainPanel;

        private Color p_backgroundColor;
        private Color p_NormalTextColor;
        private Color p_SelectedTextColor;
        private Color p_PressedTextColor;
        private Color p_DisabledTextColor;

        private JLabel p_NormalTextColorLabel;
        private JLabel p_SelectedTextColorLabel;
        private JLabel p_PressedTextColorLabel;
        private JLabel p_DisabledTextColorLabel;
        private JButton p_ChangeNormalTextColorButton;
        private JButton p_ChangeSelectedTextColorButton;
        private JButton p_ChangePressedTextColorButton;
        private JButton p_ChangeDisabledTextColorButton;

        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource().equals(p_ChangeBackgroundColorButton))
            {
                Color p_newColor = Utilities.selectColor(p_MainPanel, p_backgroundColor, "Select background color..");
                if (p_newColor != null) setBackgroundColor(p_newColor);
            }
            else if (e.getSource().equals(p_ChangeNormalTextColorButton))
            {
                Color p_newColor = Utilities.selectColor(p_MainPanel, p_NormalTextColor, "Select normal text color..");
                if (p_newColor != null) setNormatTextColor(p_newColor);
            }
            else if (e.getSource().equals(p_ChangeSelectedTextColorButton))
            {
                Color p_newColor = Utilities.selectColor(p_MainPanel, p_SelectedTextColor, "Select selected text color..");
                if (p_newColor != null) setSelectedTextColor(p_newColor);
            }
            else if (e.getSource().equals(p_ChangePressedTextColorButton))
            {
                Color p_newColor = Utilities.selectColor(p_MainPanel, p_PressedTextColor, "Select pressed text color..");
                if (p_newColor != null) setPressedTextColor(p_newColor);
            }
            else if (e.getSource().equals(p_ChangeDisabledTextColorButton))
            {
                Color p_newColor = Utilities.selectColor(p_MainPanel, p_DisabledTextColor, "Select disabled text color..");
                if (p_newColor != null) setDisabledTextColor(p_newColor);
            }
        }

        public ComponentFormProperties()
        {
            p_ChangeBackgroundColorButton.addActionListener(this);
            p_ChangeDisabledTextColorButton.addActionListener(this);
            p_ChangeNormalTextColorButton.addActionListener(this);
            p_ChangePressedTextColorButton.addActionListener(this);
            p_ChangeSelectedTextColorButton.addActionListener(this);

            setBackgroundColor(Color.black);
            setNormatTextColor(Color.blue);
            setSelectedTextColor(Color.yellow);
            setPressedTextColor(Color.red);
            setDisabledTextColor(Color.gray);
        }

        public String isDataOk()
        {
            int i_newWidth, i_newHeight;

            try
            {
                i_newWidth = Integer.parseInt(p_WidthField.getText());
                i_newHeight = Integer.parseInt(p_HeightField.getText());
            }
            catch (NumberFormatException e)
            {
                return "You have a wrong value for Width or Height";
            }

            if (i_newWidth < 5 || i_newHeight < 5) return "You must use WIDTH>=5 and HEIGHT>=5";

            return null;
        }

        public AbstractFormComponent makeNewComponent(FormContainer _parent, String _id)
        {
            int i_newWidth = 0;
            int i_newHeight = 0;
            try
            {
                i_newWidth = Integer.parseInt(p_WidthField.getText());
                i_newHeight = Integer.parseInt(p_HeightField.getText());
            }
            catch (NumberFormatException e)
            {
                return null;
            }

            FormContainer p_form = new FormContainer(_id,i_newWidth,i_newHeight);

            p_form.setBackgroundColor(p_backgroundColor);
            p_form.setSelectedTextColor(p_SelectedTextColor);
            p_form.setNormalTextColor(p_NormalTextColor);
            p_form.setPressedTextColor(p_PressedTextColor);
            p_form.setDisabledTextColor(p_DisabledTextColor);

            return p_form;
        }

        public void fillPropertiesFromComponent(AbstractFormComponent _component, ResourceContainer _container)
        {
            FormContainer p_form = (FormContainer) _component;

            p_HeightField.setText(Integer.toString(p_form.getHeight()));
            p_WidthField.setText(Integer.toString(p_form.getWidth()));

            setBackgroundColor(p_form.getBackgroundColor());
            setNormatTextColor(p_form.getNormalTextColor());
            setSelectedTextColor(p_form.getSelectedTextColor());
            setDisabledTextColor(p_form.getDisabledTextColor());
            setPressedTextColor(p_form.getPressedTextColor());
        }

        private void setNormatTextColor(Color _color)
        {
            p_NormalTextColor = _color;
            BufferedImage p_bi = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
            Graphics p_grph = p_bi.getGraphics();
            p_grph.setColor(p_NormalTextColor);
            p_grph.fill3DRect(0, 0, p_bi.getWidth(), p_bi.getHeight(), true);
            p_NormalTextColorLabel.setIcon(new ImageIcon(p_bi));
            p_NormalTextColorLabel.repaint();
        }

        private void setDisabledTextColor(Color _color)
        {
            p_DisabledTextColor = _color;
            BufferedImage p_bi = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
            Graphics p_grph = p_bi.getGraphics();
            p_grph.setColor(p_DisabledTextColor);
            p_grph.fill3DRect(0, 0, p_bi.getWidth(), p_bi.getHeight(), true);
            p_DisabledTextColorLabel.setIcon(new ImageIcon(p_bi));
            p_DisabledTextColorLabel.repaint();
        }

        private void setPressedTextColor(Color _color)
        {
            p_PressedTextColor = _color;
            BufferedImage p_bi = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
            Graphics p_grph = p_bi.getGraphics();
            p_grph.setColor(p_PressedTextColor);
            p_grph.fill3DRect(0, 0, p_bi.getWidth(), p_bi.getHeight(), true);
            p_PressedTextColorLabel.setIcon(new ImageIcon(p_bi));
            p_PressedTextColorLabel.repaint();
        }

        private void setSelectedTextColor(Color _color)
        {
            p_SelectedTextColor = _color;
            BufferedImage p_bi = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
            Graphics p_grph = p_bi.getGraphics();
            p_grph.setColor(p_SelectedTextColor);
            p_grph.fill3DRect(0, 0, p_bi.getWidth(), p_bi.getHeight(), true);
            p_SelectedTextColorLabel.setIcon(new ImageIcon(p_bi));
            p_SelectedTextColorLabel.repaint();
        }

        private void setBackgroundColor(Color _color)
        {
            p_backgroundColor = _color;
            BufferedImage p_bi = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
            Graphics p_grph = p_bi.getGraphics();
            p_grph.setColor(p_backgroundColor);
            p_grph.fill3DRect(0, 0, p_bi.getWidth(), p_bi.getHeight(), true);
            p_colorLabel.setIcon(new ImageIcon(p_bi));
            p_colorLabel.repaint();
        }

        public void fillComponentFromProperties(AbstractFormComponent _component, ResourceContainer _container)
        {
            if (isDataOk() == null)
            {
                FormContainer p_form = (FormContainer) _component;

                int i_newWidth = 0;
                int i_newHeight = 0;
                try
                {
                    i_newWidth = Integer.parseInt(p_WidthField.getText());
                    i_newHeight = Integer.parseInt(p_HeightField.getText());
                }
                catch (NumberFormatException e)
                {
                    return;
                }

                p_form.setWidthHeight(i_newWidth, i_newHeight);
                p_form.setBackgroundColor(p_backgroundColor);
                p_form.setSelectedTextColor(p_SelectedTextColor);
                p_form.setNormalTextColor(p_NormalTextColor);
                p_form.setPressedTextColor(p_PressedTextColor);
                p_form.setDisabledTextColor(p_DisabledTextColor);
            }
        }

        public JPanel getPanel(ResourceContainer _container)
        {
            return p_MainPanel;
        }

        public void updateResourceContainer(ResourceContainer _resources)
        {

        }

        public void initPanelDataForNewComponent()
        {
            setBackgroundColor(Color.black);
            setNormatTextColor(Color.blue);
            setSelectedTextColor(Color.yellow);
            setPressedTextColor(Color.red);
            setDisabledTextColor(Color.gray);
        }
    }

    public class ComponentButtonProperties implements AbstractFormComponentPanel
    {
        private JComboBox p_List_NormalImage;
        private JComboBox p_List_SelectedImage;
        private JComboBox p_List_DisabledImage;
        private JComboBox p_List_PressedImage;
        private JComboBox p_List_ClickSound;
        private JPanel p_Panel;
        private JComboBox p_List_Font;
        private JComboBox p_List_Text;
        private JComboBox p_VerticalAlign_List;
        private JComboBox p_HorizontalAlign_List;

        public ComponentButtonProperties()
        {
            final String[] as_Horizontal = new String[]{"LEFT", "CENTER", "RIGHT"};
            final String[] as_Vertical = new String[]{"TOP", "CENTER", "BOTTOM"};

            p_VerticalAlign_List.removeAllItems();
            p_VerticalAlign_List.addItem(as_Vertical[RrgFormComponent_Button.TEXT_ALIGN_VERT_TOP]);
            p_VerticalAlign_List.addItem(as_Vertical[RrgFormComponent_Button.TEXT_ALIGN_VERT_CENTER]);
            p_VerticalAlign_List.addItem(as_Vertical[RrgFormComponent_Button.TEXT_ALIGN_VERT_BOTTOM]);

            p_HorizontalAlign_List.removeAllItems();
            p_HorizontalAlign_List.addItem(as_Horizontal[RrgFormComponent_Button.TEXT_ALIGN_HORZ_LEFT]);
            p_HorizontalAlign_List.addItem(as_Horizontal[RrgFormComponent_Button.TEXT_ALIGN_HORZ_CENTER]);
            p_HorizontalAlign_List.addItem(as_Horizontal[RrgFormComponent_Button.TEXT_ALIGN_HORZ_RIGHT]);

            p_HorizontalAlign_List.setSelectedIndex(RrgFormComponent_Button.TEXT_ALIGN_HORZ_CENTER);
            p_VerticalAlign_List.setSelectedIndex(RrgFormComponent_Button.TEXT_ALIGN_VERT_CENTER);
        }

        public void fillComponentFromProperties(AbstractFormComponent _component, ResourceContainer _resources)
        {
            if (isDataOk() != null) return;
            RrgFormComponent_Button p_comp = (RrgFormComponent_Button) _component;
            p_comp.setNormalImage(p_List_NormalImage.getSelectedItem().equals(s_NullItem) ? null : (RrgResource_Image) p_List_NormalImage.getSelectedItem());
            p_comp.setDisabledImage(p_List_DisabledImage.getSelectedItem().equals(s_NullItem) ? null : (RrgResource_Image) p_List_DisabledImage.getSelectedItem());
            p_comp.setPressedImage(p_List_PressedImage.getSelectedItem().equals(s_NullItem) ? null : (RrgResource_Image) p_List_PressedImage.getSelectedItem());
            p_comp.setSelectedImage(p_List_SelectedImage.getSelectedItem().equals(s_NullItem) ? null : (RrgResource_Image) p_List_SelectedImage.getSelectedItem());
            p_comp.setClickSound(p_List_ClickSound.getSelectedItem().equals(s_NullItem) ? null : (RrgResource_Sound) p_List_ClickSound.getSelectedItem());
            p_comp.setFont(p_List_Font.getSelectedItem().equals(s_NullItem) ? null : (RrgResource_Font) p_List_Font.getSelectedItem());
            p_comp.setText(p_List_Text.getSelectedItem().equals(s_NullItem) ? null : (RrgResource_Text) p_List_Text.getSelectedItem());
            p_comp.setTextVertAlign(p_VerticalAlign_List.getSelectedIndex());
            p_comp.setTextHorzAlign(p_HorizontalAlign_List.getSelectedIndex());
        }

        public void fillPropertiesFromComponent(AbstractFormComponent _component, ResourceContainer _resources)
        {
            updateResourceContainer(_resources);
            RrgFormComponent_Button p_comp = (RrgFormComponent_Button) _component;

            p_List_NormalImage.setSelectedItem(p_comp.getNormalImage() == null ? (Object) s_NullItem : p_comp.getNormalImage());
            p_List_SelectedImage.setSelectedItem(p_comp.getSelectedImage() == null ? (Object) s_NullItem : p_comp.getSelectedImage());
            p_List_PressedImage.setSelectedItem(p_comp.getPressedImage() == null ? (Object) s_NullItem : p_comp.getPressedImage());
            p_List_DisabledImage.setSelectedItem(p_comp.getDisabledImage() == null ? (Object) s_NullItem : p_comp.getDisabledImage());
            p_List_ClickSound.setSelectedItem(p_comp.getClickSound() == null ? (Object) s_NullItem : p_comp.getClickSound());
            p_List_Font.setSelectedItem(p_comp.getFont() == null ? (Object) s_NullItem : p_comp.getFont());
            p_List_Text.setSelectedItem(p_comp.getText() == null ? (Object) s_NullItem : p_comp.getText());
            p_HorizontalAlign_List.setSelectedIndex(p_comp.getTextHorzAlign());
            p_VerticalAlign_List.setSelectedIndex(p_comp.getTextVertAlign());
        }

        public JPanel getPanel(ResourceContainer _container)
        {
            updateResourceContainer(_container);
            return p_Panel;
        }

        public String isDataOk()
        {
            if (p_List_NormalImage.getSelectedItem().equals(s_NullItem)) return "You have not selected a NORMAL image resource";
            return null;
        }

        public AbstractFormComponent makeNewComponent(FormContainer _parent,String _id)
        {
            if (isDataOk() != null) return null;
            RrgFormComponent_Button p_new = new RrgFormComponent_Button(_parent,_id, (RrgResource_Image) p_List_NormalImage.getSelectedItem());
            p_new.setDisabledImage(p_List_DisabledImage.getSelectedItem().equals(s_NullItem) ? null : (RrgResource_Image) p_List_DisabledImage.getSelectedItem());
            p_new.setPressedImage(p_List_PressedImage.getSelectedItem().equals(s_NullItem) ? null : (RrgResource_Image) p_List_PressedImage.getSelectedItem());
            p_new.setSelectedImage(p_List_SelectedImage.getSelectedItem().equals(s_NullItem) ? null : (RrgResource_Image) p_List_SelectedImage.getSelectedItem());
            p_new.setClickSound(p_List_ClickSound.getSelectedItem().equals(s_NullItem) ? null : (RrgResource_Sound) p_List_ClickSound.getSelectedItem());
            p_new.setFont(p_List_Font.getSelectedItem().equals(s_NullItem) ? null : (RrgResource_Font) p_List_Font.getSelectedItem());
            p_new.setText(p_List_Text.getSelectedItem().equals(s_NullItem) ? null : (RrgResource_Text) p_List_Text.getSelectedItem());
            p_new.setTextHorzAlign(p_HorizontalAlign_List.getSelectedIndex());
            p_new.setTextVertAlign(p_VerticalAlign_List.getSelectedIndex());

            componentToCenterOfForm(p_new);

            return p_new;
        }

        public void updateResourceContainer(ResourceContainer _resources)
        {
            Object p_normalImageSelected = p_List_NormalImage.getSelectedItem() == null ? s_NullItem : p_List_NormalImage.getSelectedItem();
            Object p_disabledImageSelected = p_List_DisabledImage.getSelectedItem() == null ? s_NullItem : p_List_DisabledImage.getSelectedItem();
            Object p_selectedImageSelected = p_List_SelectedImage.getSelectedItem() == null ? s_NullItem : p_List_SelectedImage.getSelectedItem();
            Object p_pressedImageSelected = p_List_PressedImage.getSelectedItem() == null ? s_NullItem : p_List_PressedImage.getSelectedItem();
            Object p_clickSoundSelected = p_List_ClickSound.getSelectedItem() == null ? s_NullItem : p_List_ClickSound.getSelectedItem();
            Object p_fontSelected = p_List_Font.getSelectedItem() == null ? s_NullItem : p_List_Font.getSelectedItem();
            Object p_textSelected = p_List_Text.getSelectedItem() == null ? s_NullItem : p_List_Text.getSelectedItem();

            p_List_NormalImage.removeAllItems();
            p_List_PressedImage.removeAllItems();
            p_List_SelectedImage.removeAllItems();
            p_List_DisabledImage.removeAllItems();

            p_List_Font.removeAllItems();
            p_List_ClickSound.removeAllItems();
            p_List_Text.removeAllItems();

            p_List_NormalImage.addItem(s_NullItem);
            p_List_SelectedImage.addItem(s_NullItem);
            p_List_PressedImage.addItem(s_NullItem);
            p_List_DisabledImage.addItem(s_NullItem);

            p_List_Text.addItem(s_NullItem);
            p_List_Font.addItem(s_NullItem);
            p_List_ClickSound.addItem(s_NullItem);

            Vector p_Vector = _resources.getVectorForType(AbstractRrgResource.TYPE_IMAGE);
            for (int li = 0; li < p_Vector.size(); li++)
            {
                p_List_NormalImage.addItem(p_Vector.elementAt(li));
                p_List_SelectedImage.addItem(p_Vector.elementAt(li));
                p_List_PressedImage.addItem(p_Vector.elementAt(li));
                p_List_DisabledImage.addItem(p_Vector.elementAt(li));
            }

            p_Vector = _resources.getVectorForType(AbstractRrgResource.TYPE_SOUND);
            for (int li = 0; li < p_Vector.size(); li++)
            {
                p_List_ClickSound.addItem(p_Vector.elementAt(li));
            }

            p_Vector = _resources.getVectorForType(AbstractRrgResource.TYPE_FONT);
            for (int li = 0; li < p_Vector.size(); li++)
            {
                p_List_Font.addItem(p_Vector.elementAt(li));
            }

            p_Vector = _resources.getVectorForType(AbstractRrgResource.TYPE_TEXT);
            for (int li = 0; li < p_Vector.size(); li++)
            {
                p_List_Text.addItem(p_Vector.elementAt(li));
            }

            p_List_NormalImage.setSelectedItem(p_normalImageSelected);
            p_List_SelectedImage.setSelectedItem(p_selectedImageSelected);
            p_List_DisabledImage.setSelectedItem(p_disabledImageSelected);
            p_List_PressedImage.setSelectedItem(p_pressedImageSelected);

            p_List_ClickSound.setSelectedItem(p_clickSoundSelected);
            p_List_Font.setSelectedItem(p_fontSelected);
            p_List_Text.setSelectedItem(p_textSelected);
        }

        public void initPanelDataForNewComponent()
        {
            p_List_NormalImage.setSelectedItem(s_NullItem);
            p_List_SelectedImage.setSelectedItem(s_NullItem);
            p_List_PressedImage.setSelectedItem(s_NullItem);
            p_List_DisabledImage.setSelectedItem(s_NullItem);

            p_List_Text.setSelectedItem(s_NullItem);
            p_List_Font.setSelectedItem(s_NullItem);
            p_List_ClickSound.setSelectedItem(s_NullItem);
        }
    }

    public class ComponentLabelProperties implements AbstractFormComponentPanel, ActionListener
    {
        private JPanel p_propPanel;
        private JComboBox p_TextList;
        private JComboBox p_FontList;
        private JButton p_ButtonSelectColor;
        private JLabel p_colorLabel;

        private Color p_color;

        public ComponentLabelProperties()
        {
            p_ButtonSelectColor.addActionListener(this);
            setColor(Color.blue);
        }

        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource().equals(p_ButtonSelectColor))
            {
                Color p_newColor = Utilities.selectColor(p_propPanel, p_color, "Select label color");
                if (p_newColor != null) setColor(p_newColor);
            }
        }

        private void setColor(Color _color)
        {
            p_color = _color;
            BufferedImage p_bi = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
            Graphics p_grph = p_bi.getGraphics();
            p_grph.setColor(p_color);
            p_grph.fill3DRect(0, 0, p_bi.getWidth(), p_bi.getHeight(), true);
            p_colorLabel.setIcon(new ImageIcon(p_bi));
            p_colorLabel.repaint();
        }

        public String isDataOk()
        {
            if (p_TextList.getSelectedItem().equals(s_NullItem)) return "You must select a text resource";
            if (p_FontList.getSelectedItem().equals(s_NullItem)) return "You must select a font resource";
            return null;
        }

        public AbstractFormComponent makeNewComponent(FormContainer _parent,String _id)
        {
            if (isDataOk() == null)
            {
                RrgFormComponent_Label p_newCompo = new RrgFormComponent_Label(_parent, _id);
                p_newCompo.setLabelColor(p_color);
                p_newCompo.setLabelText(p_TextList.getSelectedItem().equals(s_NullItem) ? null : (RrgResource_Text) p_TextList.getSelectedItem());
                p_newCompo.setLabelFont(p_FontList.getSelectedItem().equals(s_NullItem) ? null : (RrgResource_Font) p_FontList.getSelectedItem());
                componentToCenterOfForm(p_newCompo);
                return p_newCompo;
            }
            return null;
        }

        public void fillPropertiesFromComponent(AbstractFormComponent _component, ResourceContainer _container)
        {
            RrgFormComponent_Label p_l = (RrgFormComponent_Label) _component;
            setColor(p_l.getLabelColor());
            this.updateResourceContainer(_container);
            p_TextList.setSelectedItem(p_l.getLabelText() == null ? (Object) s_NullItem : p_l.getLabelText());
            p_FontList.setSelectedItem(p_l.getLabelFont() == null ? (Object) s_NullItem : p_l.getLabelFont());
        }

        public void fillComponentFromProperties(AbstractFormComponent _component, ResourceContainer _container)
        {
            if (isDataOk() == null)
            {
                RrgFormComponent_Label p_l = (RrgFormComponent_Label) _component;
                p_l.setLabelColor(p_color);
                p_l.setLabelFont(p_FontList.getSelectedItem().equals(s_NullItem) ? null : (RrgResource_Font) p_FontList.getSelectedItem());
                p_l.setLabelText(p_TextList.getSelectedItem().equals(s_NullItem) ? null : (RrgResource_Text) p_TextList.getSelectedItem());
            }
        }

        public JPanel getPanel(ResourceContainer _container)
        {
            this.updateResourceContainer(_container);
            return p_propPanel;
        }

        public void updateResourceContainer(ResourceContainer _resources)
        {
            Object p_text = p_TextList.getSelectedItem() == null ? s_NullItem : p_TextList.getSelectedItem();
            Object p_font = p_FontList.getSelectedItem() == null ? s_NullItem : p_FontList.getSelectedItem();

            p_TextList.removeAllItems();
            p_TextList.addItem(s_NullItem);

            Vector p_Vector = _resources.getVectorForType(AbstractRrgResource.TYPE_TEXT);
            for (int li = 0; li < p_Vector.size(); li++)
            {
                p_TextList.addItem(p_Vector.elementAt(li));
            }
            p_TextList.setSelectedItem(p_text == null ? (Object) s_NullItem : p_text);

            p_FontList.removeAllItems();
            p_FontList.addItem(s_NullItem);

            p_Vector = _resources.getVectorForType(AbstractRrgResource.TYPE_FONT);
            for (int li = 0; li < p_Vector.size(); li++)
            {
                p_FontList.addItem(p_Vector.elementAt(li));
            }
            p_FontList.setSelectedItem(p_font == null ? (Object) s_NullItem : p_font);
        }

        public void initPanelDataForNewComponent()
        {
            p_TextList.setSelectedItem(s_NullItem);
            p_FontList.setSelectedItem(s_NullItem);
        }
    }

    public class ComponentImageProperties implements AbstractFormComponentPanel
    {
        private JComboBox p_ImageResource;
        private JPanel p_Panel;
        private JComboBox p_ScaleList;
        private JCheckBox p_CheckBoxModifierFlipV;
        private JCheckBox p_CheckBoxModifierFlipH;

        public JPanel getPanel(ResourceContainer _resources)
        {
            updateResourceContainer(_resources);
            return p_Panel;
        }

        public ComponentImageProperties()
        {
            p_ScaleList.removeAllItems();
            for (int li = 1; li < 6; li++)
            {
                p_ScaleList.addItem("x" + li);
            }
        }

        protected int getSelectedScale()
        {
            return p_ScaleList.getSelectedIndex() + 1;
        }

        protected void setSelectedScale(int _scale)
        {
            p_ScaleList.setSelectedIndex(_scale - 1);
        }

        public void updateResourceContainer(ResourceContainer _resources)
        {
            Object p_selectedImage = p_ImageResource.getSelectedItem() == null ? s_NullItem : p_ImageResource.getSelectedItem();

            p_ImageResource.removeAllItems();
            p_ImageResource.addItem(s_NullItem);

            Vector p_Vector = _resources.getVectorForType(AbstractRrgResource.TYPE_IMAGE);
            for (int li = 0; li < p_Vector.size(); li++)
            {
                p_ImageResource.addItem(p_Vector.elementAt(li));
            }
            p_ImageResource.setSelectedItem(p_selectedImage);
        }

        public void initPanelDataForNewComponent()
        {
            p_ImageResource.setSelectedItem(s_NullItem);
            setSelectedScale(0);
            p_CheckBoxModifierFlipH.setSelected(false);
            p_CheckBoxModifierFlipV.setSelected(false);
        }

        public void fillComponentFromProperties(AbstractFormComponent _component, ResourceContainer _resources)
        {
            if (isDataOk() != null) return;
            RrgFormComponent_Image p_component = (RrgFormComponent_Image) _component;
            p_component.setImageResource(p_ImageResource.getSelectedItem().equals(s_NullItem) ? null : (RrgResource_Image) p_ImageResource.getSelectedItem());
            p_component.setScale(getSelectedScale());

            int i_modifiers = (p_CheckBoxModifierFlipH.isSelected() ? RrgFormComponent_Image.MODIFIER_FLIP_HORZ : RrgFormComponent_Image.MODIFIER_NONE)|(p_CheckBoxModifierFlipV.isSelected() ? RrgFormComponent_Image.MODIFIER_FLIP_VERT : RrgFormComponent_Image.MODIFIER_NONE);

            p_component.setModifier(i_modifiers);
        }

        public void fillPropertiesFromComponent(AbstractFormComponent _component, ResourceContainer _resources)
        {
            this.updateResourceContainer(_resources);
            RrgFormComponent_Image p_component = (RrgFormComponent_Image) _component;
            p_ImageResource.setSelectedItem((p_component.getImageResource() == null) ? (Object) s_NullItem : (Object) p_component.getImageResource());
            setSelectedScale(p_component.getScale());

            int i_modifiers = p_component.getModifiers();

            p_CheckBoxModifierFlipH.setSelected((i_modifiers & RrgFormComponent_Image.MODIFIER_FLIP_HORZ)!=0);
            p_CheckBoxModifierFlipV.setSelected((i_modifiers & RrgFormComponent_Image.MODIFIER_FLIP_VERT)!=0);
        }

        public String isDataOk()
        {
            if (!p_ImageResource.getSelectedItem().equals(s_NullItem)) return null;
            return "You have not selected an image resource";
        }

        public AbstractFormComponent makeNewComponent(FormContainer _parent, String _id)
        {
            if (isDataOk() != null) return null;
            RrgFormComponent_Image p_newComponent = new RrgFormComponent_Image(_parent, _id, p_ImageResource.getSelectedItem().equals(s_NullItem) ? null : (RrgResource_Image) p_ImageResource.getSelectedItem());
            p_newComponent.setScale(getSelectedScale());
            int i_modifiers = (p_CheckBoxModifierFlipH.isSelected() ? RrgFormComponent_Image.MODIFIER_FLIP_HORZ : RrgFormComponent_Image.MODIFIER_NONE)|(p_CheckBoxModifierFlipV.isSelected() ? RrgFormComponent_Image.MODIFIER_FLIP_VERT : RrgFormComponent_Image.MODIFIER_NONE);
            p_newComponent.setModifier(i_modifiers);

            componentToCenterOfForm(p_newComponent);
            return p_newComponent;
        }
    }

    public class ComponentCustomAreaProperties implements AbstractFormComponentPanel, ActionListener
    {
        private JTextField p_ValueField;
        private JTextField p_WidthField;
        private JTextField p_HeightField;
        private JButton p_SelectColorButton;
        private JPanel p_PropPanel;
        private Color p_color;
        private JLabel p_ColorLabel;

        public String isDataOk()
        {
            try
            {
                Integer.parseInt(p_ValueField.getText().trim());
            }
            catch (NumberFormatException e)
            {
                return "Bad value in the value field";
            }

            try
            {
                int i_val = Integer.parseInt(p_WidthField.getText().trim());
                if (i_val <=0) throw new NumberFormatException();
            }
            catch (NumberFormatException e)
            {
                return "Bad value in the width field (must be more than 0)";
            }

            try
            {
                int i_val = Integer.parseInt(p_HeightField.getText().trim());
                if (i_val <=0) throw new NumberFormatException();
            }
            catch (NumberFormatException e)
            {
                return "Bad value in the height field (must be more than 0)";
            }

            return null;
        }

        public ComponentCustomAreaProperties()
        {
            super();
            p_SelectColorButton.addActionListener(this);
        }

        public AbstractFormComponent makeNewComponent(FormContainer _parent, String _id)
        {
            if (isDataOk() != null) return null;

            int i_value = 0;
            int i_wdth = 0;
            int i_hght = 0;

            i_value = Integer.parseInt(p_ValueField.getText().trim());
            i_wdth = Integer.parseInt(p_WidthField.getText().trim());
            i_hght = Integer.parseInt(p_HeightField.getText().trim());

            RrgFormComponent_CustomArea p_newComponent = new RrgFormComponent_CustomArea(_parent, _id,p_color,i_value);
            p_newComponent.setWidthHeight(i_wdth,i_hght);

            componentToCenterOfForm(p_newComponent);
            return p_newComponent;
        }

        public void fillPropertiesFromComponent(AbstractFormComponent _component, ResourceContainer _container)
        {
            if (_component!=null)
            {
                RrgFormComponent_CustomArea p_component = (RrgFormComponent_CustomArea) _component;
                p_ValueField.setText(Integer.toString(p_component.getAreaValue()));
                p_WidthField.setText(Integer.toString(p_component.getWidth()));
                p_HeightField.setText(Integer.toString(p_component.getHeight()));
                setColor(p_component.getAreaColor());
            }
        }

        private void setColor(Color _color)
        {
            p_color = _color;
            BufferedImage p_bi = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
            Graphics p_grph = p_bi.getGraphics();
            p_grph.setColor(p_color);
            p_grph.fill3DRect(0, 0, p_bi.getWidth(), p_bi.getHeight(), true);
            p_ColorLabel.setIcon(new ImageIcon(p_bi));
            p_ColorLabel.repaint();
        }


        public void fillComponentFromProperties(AbstractFormComponent _component, ResourceContainer _container)
        {
            if (isDataOk() != null) return;
            RrgFormComponent_CustomArea p_component = (RrgFormComponent_CustomArea) _component;

            int i_value = 0;
            int i_wdth = 0;
            int i_hght = 0;

            i_value = Integer.parseInt(p_ValueField.getText().trim());
            i_wdth = Integer.parseInt(p_WidthField.getText().trim());
            i_hght = Integer.parseInt(p_HeightField.getText().trim());

            p_component.setAreaValue(i_value);
            p_component.setAreaColor(p_color);
            p_component.setWidthHeight(i_wdth,i_hght);
        }

        public JPanel getPanel(ResourceContainer _container)
        {
            return p_PropPanel;
        }

        public void updateResourceContainer(ResourceContainer _resources)
        {
        }

        public void initPanelDataForNewComponent()
        {
            p_WidthField.setText("20");
            p_HeightField.setText("20");
            p_ValueField.setText("0");
            setColor(Color.orange);
        }

        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource().equals(p_SelectColorButton))
            {
                Color p_newColor = Utilities.selectColor(p_PropPanel, p_color, "Select area color");
                if (p_newColor != null) setColor(p_newColor);
            }
        }
    }

    public class ComponentPathProperties implements AbstractFormComponentPanel, ActionListener
    {
        private JList p_PointsList;
        private JButton p_AddButton;
        private JButton p_RemoveButton;
        private JButton p_EditButton;
        private JComboBox p_TypeList;
        private JButton p_SelectColorButton;
        private JLabel p_ColorLabel;
        private JPanel p_PropPanel;

        private Color p_color;

        private Vector p_pointsVector;

        private Object p_Type_Normal = new String("Normal");
        private Object p_Type_Cycled = new String("Cycled");
        private Object p_Type_Pendulum = new String("Pendulum");
        private Object p_Type_CycledSmooth = new String("Cycled_smooth");

        private JTextField p_Text_MainX;
        private JTextField p_Text_MainY;
        private JButton p_UP_Button;
        private JButton p_DOWN_Button;
        private JCheckBox p_showStepsFlag;
        private JCheckBox p_CheckBox_onNotifyPathEnd;
        private JCheckBox p_CheckBox_onNotifyEveryPathPoint;
        private JCheckBox p_CheckBox_saveStepsInfo;
        private JCheckBox p_CheckBox_SaveBoundiaryInfo;
        private JCheckBox checkBox1;
        private JCheckBox p_CheckBox_SaveMainPointInfo;

        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource().equals(p_SelectColorButton))
            {
                Color p_newColor = Utilities.selectColor(p_mainPanel, p_color, "Select path color");
                if (p_newColor != null) setColor(p_newColor);
            }
            else if (e.getSource().equals(p_AddButton))
            {
                PathPoint p_new_point = new PathPoint(0, 0, 0);
                if (p_PathPointDialog.editPathPoint("New path point", p_new_point))
                {
                    p_new_point.i_Index = p_pointsVector.size();
                    p_pointsVector.add(p_new_point);
                    p_PointsList.setListData(p_pointsVector);
                    p_PointsList.setSelectedIndex(p_pointsVector.size() - 1);
                }
            }
            else if (e.getSource().equals(p_RemoveButton))
            {
                if (p_PointsList.getSelectedIndex() >= 0)
                {
                    if (Utilities.askDialog(p_mainPanel, "Remove a path point", "Do you really want to remove the point?"))
                    {
                        p_pointsVector.removeElementAt(p_PointsList.getSelectedIndex());
                        reindexesPoint();
                        p_PointsList.setListData(p_pointsVector);
                    }
                }
            }
            else if (e.getSource().equals(p_EditButton))
            {
                if (p_PointsList.getSelectedIndex() >= 0)
                {
                    int i_index = p_PointsList.getSelectedIndex();

                    PathPoint p_point = (PathPoint)p_pointsVector.elementAt(i_index);

                    p_PathPointDialog.editPathPoint("Edit a path point",p_point);

                    p_PointsList.setListData(p_pointsVector);
                    p_PointsList.setSelectedIndex(i_index);
                }
            }
            else if (e.getSource().equals(p_UP_Button))
            {
                int i_index = p_PointsList.getSelectedIndex();
                if (i_index>0)
                {
                    Object p_obj1 = p_pointsVector.elementAt(i_index);
                    Object p_obj2 = p_pointsVector.elementAt(i_index-1);

                    p_pointsVector.setElementAt(p_obj1,i_index-1);
                    p_pointsVector.setElementAt(p_obj2,i_index);

                    reindexesPoint();

                    p_PointsList.setListData(p_pointsVector);
                    p_PointsList.setSelectedIndex(i_index-1);
                }
            }
            else if (e.getSource().equals(p_DOWN_Button))
            {
                int i_index = p_PointsList.getSelectedIndex();
                if (i_index<p_pointsVector.size()-1)
                {
                    Object p_obj1 = p_pointsVector.elementAt(i_index);
                    Object p_obj2 = p_pointsVector.elementAt(i_index+1);

                    p_pointsVector.setElementAt(p_obj1,i_index+1);
                    p_pointsVector.setElementAt(p_obj2,i_index);

                    reindexesPoint();

                    p_PointsList.setListData(p_pointsVector);
                    p_PointsList.setSelectedIndex(i_index+1);
                }
            }
        }

        private void reindexesPoint()
        {
            if (p_pointsVector == null) return;

            for(int li=0;li<p_pointsVector.size();li++)
            {
                ((PathPoint) p_pointsVector.elementAt(li)).i_Index = li;
            }
        }

        private void setColor(Color _color)
        {
            p_color = _color;
            BufferedImage p_bi = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
            Graphics p_grph = p_bi.getGraphics();
            p_grph.setColor(p_color);
            p_grph.fill3DRect(0, 0, p_bi.getWidth(), p_bi.getHeight(), true);
            p_ColorLabel.setIcon(new ImageIcon(p_bi));
            p_ColorLabel.repaint();
        }

        public JPanel getPanel(ResourceContainer _resources)
        {
            return p_PropPanel;
        }

        public ComponentPathProperties()
        {
            setColor(Color.green);
            p_TypeList.addItem(p_Type_Normal);
            p_TypeList.addItem(p_Type_Cycled);
            p_TypeList.addItem(p_Type_CycledSmooth);
            p_TypeList.addItem(p_Type_Pendulum);
            p_TypeList.setSelectedItem(p_Type_Normal);
            p_pointsVector = new Vector();

            p_RemoveButton.addActionListener(this);
            p_AddButton.addActionListener(this);
            p_EditButton.addActionListener(this);
            p_SelectColorButton.addActionListener(this);
            p_UP_Button.addActionListener(this);
            p_DOWN_Button.addActionListener(this);
        }

        public void updateResourceContainer(ResourceContainer _resources)
        {
        }

        public void initPanelDataForNewComponent()
        {
            p_Text_MainX.setText("0");
            p_Text_MainY.setText("0");
            p_TypeList.setSelectedItem(p_Type_Normal);
            p_pointsVector = new Vector();
            p_PointsList.setListData(p_pointsVector);
        }

        public void fillComponentFromProperties(AbstractFormComponent _component, ResourceContainer _resources)
        {
            if (isDataOk() != null) return;

            RrgFormComponent_Path p_path = (RrgFormComponent_Path) _component;
            p_path.p_pathViewColor = p_color;

            p_path.p_PointVector = p_pointsVector;

            int i_mainX = Integer.parseInt(p_Text_MainX.getText());
            int i_mainY = Integer.parseInt(p_Text_MainY.getText());

            int i_dx = i_mainX - p_path.p_MainPoint.i_X;
            int i_dy = i_mainY - p_path.p_MainPoint.i_Y;
            p_path.moveMainPointTo(i_dx,i_dy);

            //p_path.p_MainPoint.i_X = i_mainX;
            //p_path.p_MainPoint.i_Y = i_mainY;

            p_path.i_PathType = convertObjectToPathType(p_TypeList.getSelectedItem());

            p_path.lg_NotifyOnEndPoint = p_CheckBox_onNotifyPathEnd.isSelected();
            p_path.lg_NotifyOnEveryPoint = p_CheckBox_onNotifyEveryPathPoint.isSelected();

            p_path.lg_SaveSteps = p_CheckBox_saveStepsInfo.isSelected();
            p_path.lg_SaveMainPointInfo = p_CheckBox_SaveMainPointInfo.isSelected();
            p_path.lg_SaveBoundiaryInfo = p_CheckBox_SaveBoundiaryInfo.isSelected();

            p_pointsVector = new Vector();

            p_path.lg_showSteps = p_showStepsFlag.isSelected();

            p_path.updateCoordsForPoints();
        }

        private Object convertPathTypeToObject(int _type)
        {
            switch(_type)
            {
                case RrgFormComponent_Path.PATH_NORMAL : return p_Type_Normal;
                case RrgFormComponent_Path.PATH_CYCLIC : return p_Type_Cycled;
                case RrgFormComponent_Path.PATH_PENDULUM : return p_Type_Pendulum;
                case RrgFormComponent_Path.PATH_CYCLIC_SMOOTH : return p_Type_CycledSmooth;
            }
            return null;
        }

        private int convertObjectToPathType(Object _object)
        {
            if (p_Type_Normal.equals(_object)) return RrgFormComponent_Path.PATH_NORMAL;
            if (p_Type_Cycled.equals(_object)) return RrgFormComponent_Path.PATH_CYCLIC;
            if (p_Type_Pendulum.equals(_object)) return RrgFormComponent_Path.PATH_PENDULUM;
            if (p_Type_CycledSmooth.equals(_object)) return RrgFormComponent_Path.PATH_CYCLIC_SMOOTH;
            return -1;
        }

        public void fillPropertiesFromComponent(AbstractFormComponent _component, ResourceContainer _resources)
        {
            RrgFormComponent_Path p_path = (RrgFormComponent_Path) _component;
            setColor(p_path.p_pathViewColor);

            p_pointsVector.clear();

            for (int li = 0; li < p_path.p_PointVector.size(); li++)
            {
                p_pointsVector.add(((PathPoint) p_path.p_PointVector.elementAt(li)).clone());
            }
            p_PointsList.setListData(p_pointsVector);
            if (p_pointsVector.size() > 0) p_PointsList.setSelectedIndex(0);

            p_Text_MainX.setText(Integer.toString(p_path.p_MainPoint.i_X));
            p_Text_MainY.setText(Integer.toString(p_path.p_MainPoint.i_Y));

            p_showStepsFlag.setSelected(p_path.lg_showSteps);

            p_TypeList.setSelectedItem(convertPathTypeToObject(p_path.i_PathType));

            p_CheckBox_onNotifyEveryPathPoint.setSelected( p_path.lg_NotifyOnEveryPoint);
            p_CheckBox_onNotifyPathEnd.setSelected( p_path.lg_NotifyOnEndPoint);

            p_CheckBox_SaveBoundiaryInfo.setSelected(p_path.lg_SaveBoundiaryInfo);
            p_CheckBox_SaveMainPointInfo.setSelected(p_path.lg_SaveMainPointInfo);
            p_CheckBox_saveStepsInfo.setSelected(p_path.lg_SaveSteps);
        }

        public String isDataOk()
        {
            String s_strX = p_Text_MainX.getText();
            String s_strY = p_Text_MainY.getText();

            try
            {
                Integer.parseInt(s_strX);
            }
            catch (NumberFormatException e)
            {
                return "Bad parameter for main X";
            }

            try
            {
                Integer.parseInt(s_strY);
            }
            catch (NumberFormatException e)
            {
                return "Bad parameter for main Y";
            }

            return null;
        }

        public AbstractFormComponent makeNewComponent(FormContainer _parent,String _id)
        {
            if (isDataOk() != null) return null;

            RrgFormComponent_Path p_path = new RrgFormComponent_Path(_parent,_id);
            p_path.p_pathViewColor = p_color;
            p_path.p_PointVector = p_pointsVector;

            p_pointsVector = new Vector();

            p_path.i_PathType = convertObjectToPathType(p_TypeList.getSelectedItem());

            p_path.lg_NotifyOnEndPoint = p_CheckBox_onNotifyPathEnd.isSelected();
            p_path.lg_NotifyOnEveryPoint = p_CheckBox_onNotifyEveryPathPoint.isSelected();

            p_path.lg_SaveBoundiaryInfo = p_CheckBox_SaveBoundiaryInfo.isSelected();
            p_path.lg_SaveMainPointInfo = p_CheckBox_SaveMainPointInfo.isSelected();
            p_path.lg_SaveSteps = p_CheckBox_saveStepsInfo.isSelected();


            p_path.updateCoordsForPoints();
            componentToCenterOfForm(p_path);
            return p_path;
        }
    }

    private void componentToCenterOfForm(AbstractFormComponent _component)
    {
        int i_formWidth = p_formContainer.i_Width;
        int i_formHeight = p_formContainer.i_Height;

        int i_newX = (i_formWidth - _component.getWidth())>>1;
        int i_newY = (i_formHeight - _component.getHeight())>>1;

        _component.setX(i_newX);
        _component.setY(i_newY);
    }

    class SelectComponentTypePanel
    {
        private JRadioButton p_RadioButton_Image;
        private JRadioButton p_RadioButton_Button;
        private JRadioButton p_RadioButton_Label;
        private JRadioButton p_RadioButton_CustomArea;

        private ButtonGroup p_buttonGroup;
        private JPanel p_SelectPanel;
        private JRadioButton p_RadioButton_Path;

        public SelectComponentTypePanel()
        {
            p_buttonGroup = new ButtonGroup();
            p_buttonGroup.add(p_RadioButton_Image);
            p_buttonGroup.add(p_RadioButton_Button);
            p_buttonGroup.add(p_RadioButton_Label);
            p_buttonGroup.add(p_RadioButton_Path);
            p_buttonGroup.add(p_RadioButton_CustomArea);
            p_RadioButton_Image.setSelected(true);
        }

        public JPanel getPanel()
        {
            return p_SelectPanel;
        }

        public int getSelectedType()
        {
            if (p_RadioButton_Image.isSelected()) return AbstractFormComponent.COMPONENT_IMAGE;
            if (p_RadioButton_Button.isSelected()) return AbstractFormComponent.COMPONENT_BUTTON;
            if (p_RadioButton_Label.isSelected()) return AbstractFormComponent.COMPONENT_LABEL;
            if (p_RadioButton_Path.isSelected()) return AbstractFormComponent.COMPONENT_PATH;
            if (p_RadioButton_CustomArea.isSelected()) return AbstractFormComponent.COMPONENT_CUSTOMAREA;
            return -1;
        }
    }

    private JButton p_ButtonCancel;
    private JButton p_ButtonOk;

    private SelectComponentTypePanel p_SelectComponentTypePanel;

    public void setFormContainer(FormContainer _formContainer)
    {
        p_formContainer = _formContainer;
    }

    public NewComponent_Form(JFrame _owner)
    {
        super(_owner, true);

        p_PathPointDialog = new PathPointDialog(_owner);

        p_ComponentButtonProperties = new ComponentButtonProperties();
        p_ComponentImageProperties = new ComponentImageProperties();
        p_ComponentFormProperties = new ComponentFormProperties();
        p_ComponentLabelProperties = new ComponentLabelProperties();
        p_ComponentPathProperties = new ComponentPathProperties();
        p_ComponentCustomAreaProperties = new ComponentCustomAreaProperties();

        p_ButtonCancel.addActionListener(this);
        p_ButtonOk.addActionListener(this);

        p_mainPanel.setLayout(new BorderLayout(0, 0));

        p_SelectComponentTypePanel = new SelectComponentTypePanel();

        setContentPane(p_ContentPanel);
    }

    private ResourceContainer p_rCont;

    public AbstractFormComponent showDialog(ResourceContainer _resources)
    {
        p_XField.setText("0");
        p_YField.setText("0");
        p_XField.setEnabled(true);
        p_YField.setEnabled(true);

        p_rCont = _resources;
        p_result = null;
        i_State = STATE_SELECT_TYPE;
        fillForState(_resources);
        show();
        return p_result;
    }

    public FormContainer createFormContainer(ResourceContainer _resources)
    {
        p_XField.setText("");
        p_YField.setText("");
        p_XField.setEnabled(false);
        p_YField.setEnabled(false);

        p_rCont = _resources;
        p_result = null;
        i_State = STATE_FORM_PROPERTIES;
        fillForState(_resources);
        if (p_componentPanel != null)
            p_componentPanel.initPanelDataForNewComponent();
        show();
        return (FormContainer) p_result;
    }

    public FormContainer createFormContainer(ResourceContainer _resources,FormContainer _source)
    {
        FormContainer p_newForm = new FormContainer("0",3,3);
        p_newForm.copyFrom(_source);

        p_rCont = _resources;
        p_result = null;
        i_State = STATE_FORM_PROPERTIES;
        fillForState(_resources);
        setTitle("Copy of current form");

        if (p_componentPanel != null)
        {
            p_componentPanel.fillPropertiesFromComponent(p_newForm,_resources);
        }
        show();

        if (p_result!=null)
        {
            p_newForm.setID(((FormContainer)p_result).getID());
            p_componentPanel.fillComponentFromProperties(p_newForm,_resources);
            return (FormContainer) p_newForm;
        }
        else
        {
            return null;
        }
    }

    private void fillForState(ResourceContainer _resources)
    {
        p_mainPanel.removeAll();
        switch (i_State)
        {
            case STATE_SELECT_TYPE:
                {
                    setTitle("Select a component type");
                    p_mainPanel.add(p_SelectComponentTypePanel.getPanel(), BorderLayout.CENTER);
                }
                ;
                break;
            case STATE_FORM_PROPERTIES:
                {
                    setTitle("New form");
                    p_mainPanel.add(p_ComponentFormProperties.getPanel(_resources), BorderLayout.CENTER);
                    p_componentPanel = p_ComponentFormProperties;
                };break;
            case STATE_FILL_PROPERTIES:
                {
                    setTitle("Fill properties of new component");

                    AbstractFormComponentPanel p_cp = null;

                    switch (p_SelectComponentTypePanel.getSelectedType())
                    {
                        case AbstractFormComponent.COMPONENT_IMAGE:
                            {
                                p_cp = p_ComponentImageProperties;
                            }
                            ;
                            break;
                        case AbstractFormComponent.COMPONENT_BUTTON:
                            {
                                p_cp = p_ComponentButtonProperties;
                            }
                            ;
                            break;
                        case AbstractFormComponent.COMPONENT_LABEL:
                            {
                                p_cp = p_ComponentLabelProperties;
                            }
                            ;
                            break;
                        case AbstractFormComponent.COMPONENT_PATH:
                            {
                                p_cp = p_ComponentPathProperties;
                            }
                            ;
                            break;
                        case AbstractFormComponent.COMPONENT_CUSTOMAREA:
                            {
                                p_cp = p_ComponentCustomAreaProperties;
                            }
                            ;
                            break;
                    }

                    p_mainPanel.add(p_cp.getPanel(_resources), BorderLayout.CENTER);
                    p_componentPanel = p_cp;

                    p_cp.initPanelDataForNewComponent();
                }
                ;
                break;
        }
        pack();
        Utilities.toScreenCenter(this);
    }


    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource().equals(p_ButtonCancel))
        {
            hide();
        }

        switch (i_State)
        {
            case STATE_SELECT_TYPE:
                {
                    if (e.getSource().equals(p_ButtonOk))
                    {
                        i_State = STATE_FILL_PROPERTIES;
                        fillForState(p_rCont);
                        if (p_componentPanel != null) p_componentPanel.initPanelDataForNewComponent();
                    }
                }
                ;
                break;
            case STATE_FORM_PROPERTIES:
                {
                    if (e.getSource().equals(p_ButtonOk))
                    {
                        String s_error = p_componentPanel.isDataOk();
                        if (s_error != null)
                        {
                            Utilities.showErrorDialog(this, "ERROR", s_error);
                            return;
                        }

                        String s_componentID = p_ComponentIDText.getText().trim();
                        if (s_componentID.length() == 0)
                        {
                            Utilities.showInfoDialog(this, "Component ID is not found", "You have to enter the component ID");
                            return;
                        }

                        AbstractFormComponent p_component = null;

                        p_component = p_componentPanel.makeNewComponent(p_formContainer,s_componentID);

                        p_result = p_component;

                        hide();
                    }
                };break;
            case STATE_FILL_PROPERTIES:
                {
                    if (e.getSource().equals(p_ButtonOk))
                    {
                        String s_error = p_componentPanel.isDataOk();
                        if (s_error != null)
                        {
                            Utilities.showErrorDialog(this, "ERROR", s_error);
                            return;
                        }

                        String s_componentX = p_XField.getText().trim();
                        String s_componentY = p_YField.getText().trim();

                        int i_compx = 0;
                        int i_compy = 0;

                        try
                        {
                            i_compx = Integer.parseInt(s_componentX);
                        }
                        catch (NumberFormatException e1)
                        {
                            Utilities.showInfoDialog(this, "Wrong value", "You have wrong X coordinate");
                            return;
                        }

                        try
                        {
                            i_compy = Integer.parseInt(s_componentY);
                        }
                        catch (NumberFormatException e1)
                        {
                            Utilities.showInfoDialog(this, "Wrong value", "You have wrong Y coordinate");
                            return;
                        }

                        String s_componentID = p_ComponentIDText.getText().trim();
                        if (s_componentID.length() == 0)
                        {
                            Utilities.showInfoDialog(this, "Component ID is not found", "You have to enter the component ID");
                            return;
                        }
                        else if (p_formContainer.containsID(s_componentID))
                        {
                            Utilities.showErrorDialog(this, "It's an error component name", "You have duplicated component name");
                            return;
                        }

                        AbstractFormComponent p_component = null;

                        p_component = p_componentPanel.makeNewComponent(p_formContainer,s_componentID);

                        p_component.setX(i_compx);
                        p_component.setY(i_compy);

                        p_result = p_component;

                        hide();
                    }
                }
                ;
                break;
        }
    }

}

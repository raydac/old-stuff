package com.raydac_research.FormEditor.RrgFormResources;

import com.raydac_research.FormEditor.Misc.Utilities;
import com.raydac_research.Font.AbstractFont;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.applet.AudioClip;
import java.applet.Applet;

public class NewResourceDialog extends JDialog implements ActionListener
{
    private JPanel p_MainDialogPanel;
    private JButton p_ButtonOk;
    private JButton p_ButtonCancel;
    private JTextField p_TextResourceID;
    private AbstractRrgResource p_result;

    private int i_State;

    private static final int STATE_SELECTTYPE = 0;
    private static final int STATE_PROPERTIES = 1;
    private JPanel p_ContentPanel;
    private ResourceContainer p_ResourceContainer;

    public class ResourceType
    {
        private JRadioButton p_RadioButton_Image;
        private JRadioButton p_RadioButton_Sound;
        private JRadioButton p_RadioButton_Text;
        private JRadioButton p_RadioButton_Font;
        private JPanel p_TypesPanel;

        private ButtonGroup p_buttonGroup;

        public ResourceType()
        {
            p_buttonGroup = new ButtonGroup();
            p_buttonGroup.add(p_RadioButton_Image);
            p_buttonGroup.add(p_RadioButton_Sound);
            p_buttonGroup.add(p_RadioButton_Text);
            p_buttonGroup.add(p_RadioButton_Font);
        }

        public JPanel getPanel()
        {
            p_RadioButton_Image.setSelected(true);
            return p_TypesPanel;
        }

        public int getSelectedType()
        {
            if (p_RadioButton_Image.isSelected()) return AbstractRrgResource.TYPE_IMAGE;
            if (p_RadioButton_Sound.isSelected()) return AbstractRrgResource.TYPE_SOUND;
            if (p_RadioButton_Text.isSelected()) return AbstractRrgResource.TYPE_TEXT;
            if (p_RadioButton_Font.isSelected()) return AbstractRrgResource.TYPE_FONT;

            return -1;
        }
    }

    public class FontProperties extends FileFilter implements ActionListener, AbstractPropertiesPage
    {
        private JButton p_SelectFileButton;
        private JLabel p_FontPreviewLabel;
        private JPanel p_Panel;
        private Component p_parent;
        private File p_FontFile;
        private AbstractFont p_abstractFont;

        private File p_LastFile;
        private JComboBox p_FontSize;
        private JPanel p_FontPropertiesPanel;
        private JCheckBox p_CheckBoxBold;
        private JCheckBox p_ChecjBoxItalic;
        private JCheckBox p_CheckBoxUnderline;

        public FontProperties()
        {
            p_SelectFileButton.addActionListener(this);
            p_ChecjBoxItalic.addActionListener(this);
            p_CheckBoxBold.addActionListener(this);
            p_CheckBoxUnderline.addActionListener(this);
            p_FontSize.addActionListener(this);
        }

        public boolean accept(File f)
        {
            if (f.isDirectory()) return true;
            if (f.getName().toUpperCase().endsWith(".BFT") || f.getName().toUpperCase().endsWith(".TTF")|| f.getName().toUpperCase().endsWith(".GIF")) return true;
            return false;
        }

        public String getDescription()
        {
            return "BFT,GIF or TTF fonts";
        }

        protected void drawPrewiew()
        {
            if (p_abstractFont == null)
            {
                p_FontPreviewLabel.setIcon(null);
            }
            else
            {
                String s_previewStr = "0123456789 Preview";
                int i_width = p_abstractFont.getStringWidth(s_previewStr);
                int i_height = p_abstractFont.getHeight();
                if (i_width == 0 || i_height == 0)
                {
                    p_FontPreviewLabel.setIcon(null);
                }
                else
                {
                    BufferedImage p_bi = new BufferedImage(i_width, i_height, BufferedImage.TYPE_INT_RGB);
                    Graphics p_gr = p_bi.getGraphics();
                    p_gr.setColor(Color.white);
                    p_gr.fillRect(0, 0, i_width, i_height);
                    p_gr.setColor(Color.red);
                    p_abstractFont.drawString(p_gr, 0, 0, s_previewStr);
                    p_FontPreviewLabel.setIcon(new ImageIcon(p_bi));
                }

                p_FontPreviewLabel.updateUI();
            }
        }

        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource().equals(p_ChecjBoxItalic))
            {
                p_abstractFont.setItalic(p_ChecjBoxItalic.isSelected());
                drawPrewiew();
            }
            else
            if (e.getSource().equals(p_CheckBoxBold))
            {
                p_abstractFont.setBold(p_CheckBoxBold.isSelected());
                drawPrewiew();
            }
            else
            if (e.getSource().equals(p_CheckBoxUnderline))
            {
                p_abstractFont.setUnderline(p_CheckBoxUnderline.isSelected());
                drawPrewiew();
            }
            else
            if (e.getSource().equals(p_FontSize))
            {
                if (p_FontSize.getSelectedItem()!=null)
                {
                    p_abstractFont.setSize(((Integer)p_FontSize.getSelectedItem()).intValue());
                    drawPrewiew();
                }
            }
            else
            if (e.getSource().equals(p_SelectFileButton))
            {
                File p_SelectedFile = Utilities.selectFileForOpen(p_parent, this, "Select font", p_LastFile);

                if (p_SelectedFile != null)
                {
                    try
                    {
                        p_abstractFont = AbstractFont.getAbstractFont(p_SelectedFile);
                        p_FontFile = p_SelectedFile;
                        p_LastFile = p_SelectedFile;
                    }
                    catch (Exception p_ex)
                    {
                        Utilities.showErrorDialog(p_parent, "Error", p_ex.getMessage()+"["+p_SelectedFile.getAbsolutePath()+"]");
                        //p_ex.printStackTrace();
                    }
                }
                else
                {
                    p_FontFile = null;
                }
                fillData();
            }
        }

        public JPanel getPanel(Component _parent)
        {
            p_parent = _parent;

            //initPropertiesPanelFromFont(null);

            return p_Panel;
        }

        public void fillPropertiesFromResource(AbstractRrgResource _resource)
        {
            if (_resource == null)
            {
                p_FontFile = null;
                p_abstractFont = null;
            }
            else
            {
                RrgResource_Font p_component = (RrgResource_Font) _resource;
                p_FontFile = p_component.getFontFile();
                p_abstractFont = (AbstractFont)p_component.getFont().clone();
            }

            fillData();
        }

        protected void initPropertiesPanelFromFont(AbstractFont _font)
        {
            if (_font!=null && _font.isCustomized())
            {
                if (_font.supportsItalic())
                {
                    p_ChecjBoxItalic.setEnabled(true);
                    p_ChecjBoxItalic.setSelected(_font.isItalic());
                }
                else
                {
                    p_ChecjBoxItalic.setEnabled(false);
                    p_ChecjBoxItalic.setSelected(false);
                }

                if (_font.supportsBold())
                {
                    p_CheckBoxBold.setEnabled(true);
                    p_CheckBoxBold.setSelected(_font.isBold());
                }
                else
                {
                    p_CheckBoxBold.setEnabled(false);
                    p_CheckBoxBold.setSelected(false);
                }

                if (_font.supportsUnderline())
                {
                    p_CheckBoxUnderline.setEnabled(true);
                    p_CheckBoxUnderline.setSelected(_font.isUnderline());
                }
                else
                {
                    p_CheckBoxUnderline.setEnabled(false);
                    p_CheckBoxUnderline.setSelected(false);
                }

                int [] ai_sizes = _font.getSupportedSizes();

                p_FontSize.setSelectedItem(null);
                p_FontSize.removeAllItems();

                if (ai_sizes != null)
                {
                    p_FontSize.removeActionListener(this);

                    for(int li=0;li<ai_sizes.length;li++)
                    {
                        p_FontSize.addItem(new Integer(ai_sizes[li]));
                    }

                    p_FontSize.addActionListener(this);

                    p_FontSize.setSelectedItem(new Integer(_font.getSize()));

                    p_FontSize.setEnabled(true);
                }
                else
                {
                    p_FontSize.setEnabled(false);
                }
            }
            else
            {
                p_ChecjBoxItalic.setSelected(false);
                p_ChecjBoxItalic.setEnabled(false);

                p_CheckBoxBold.setSelected(false);
                p_CheckBoxBold.setEnabled(false);

                p_CheckBoxUnderline.setSelected(false);
                p_CheckBoxUnderline.setEnabled(false);

                p_FontSize.setSelectedItem(null);
                p_FontSize.removeAllItems();
                p_FontSize.setEnabled(false);
            }
        }

        protected void fillData()
        {
            if (p_FontFile == null)
            {
                p_SelectFileButton.setText("...");
                initPropertiesPanelFromFont(null);
            }
            else
            {
                p_SelectFileButton.setText(p_FontFile.getAbsolutePath());
                initPropertiesPanelFromFont(p_abstractFont);
            }

            drawPrewiew();
        }

        public void fillResourceFromProperties(AbstractRrgResource _resource) throws IOException
        {
            if (_resource == null) return;
            RrgResource_Font p_component = (RrgResource_Font) _resource;

            boolean lg_bold = p_CheckBoxBold.isSelected();
            boolean lg_italic = p_ChecjBoxItalic.isSelected();
            boolean lg_underline = p_CheckBoxUnderline.isSelected();
            int  i_size = 8;
            if (p_FontSize.getSelectedItem()!=null)
            {
                i_size = ((Integer)p_FontSize.getSelectedItem()).intValue();
            }

            p_component.setFontFile(p_FontFile,lg_bold,lg_italic,lg_underline,i_size);
        }

        public String isDataOk()
        {
            if (p_FontFile != null) return null;
            return "You must select a font file";
        }

        public AbstractRrgResource makeNewResource(String _id)
        {
            if (isDataOk() != null) return null;

            RrgResource_Font p_newFont = null;
            try
            {
                boolean lg_bold = p_CheckBoxBold.isSelected();
                boolean lg_italic = p_ChecjBoxItalic.isSelected();
                boolean lg_underline = p_CheckBoxUnderline.isSelected();
                int  i_size = 8;
                if (p_FontSize.getSelectedItem()!=null)
                {
                    i_size = ((Integer)p_FontSize.getSelectedItem()).intValue();
                }

                p_newFont = new RrgResource_Font(_id, p_FontFile,lg_bold,lg_italic,lg_underline,i_size);
            }
            catch (IOException e)
            {
                return null;
            }

            return p_newFont;
        }
    }

    public class ImageProperties extends FileFilter implements ActionListener, AbstractPropertiesPage
    {
        private JButton p_ImageFileButton;
        private JLabel p_ImagePreviewLabel;
        private JPanel p_Panel;
        private Component p_parent;
        private File p_imageFile;
        private Image p_Image;
        private File p_LastSelected;


        public ImageProperties()
        {
            p_ImageFileButton.addActionListener(this);
        }

        public void fillPropertiesFromResource(AbstractRrgResource _resource)
        {
            if (_resource == null)
            {
                p_Image = null;
                p_imageFile = null;
            }
            else
            {
                RrgResource_Image p_rrgResource = (RrgResource_Image) _resource;
                p_imageFile = p_rrgResource.getImageFile();
                p_Image = p_rrgResource.getImage();
            }
            fillData();
        }

        public void fillResourceFromProperties(AbstractRrgResource _resource) throws IOException
        {
            if (_resource == null)
            {
                return;
            }
            else
            {
                RrgResource_Image p_rrgResource = (RrgResource_Image) _resource;
                p_rrgResource.setImageFile(p_imageFile);
            }
        }

        public JPanel getPanel(Component _parent)
        {
            p_parent = _parent;
            return p_Panel;
        }

        public String isDataOk()
        {
            if (p_imageFile != null) return null;
            return "You must select an image file";
        }

        public AbstractRrgResource makeNewResource(String _id) throws IOException
        {
            if (isDataOk() != null) return null;
            RrgResource_Image p_newResource = new RrgResource_Image(_id, p_imageFile);
            return p_newResource;
        }

        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource().equals(p_ImageFileButton))
            {
                File p_file = selectFile(p_parent, "Select an image file", this, p_LastSelected);
                if (p_file != null)
                {
                    try
                    {
                        p_Image = RrgResource_Image.convertFileToImage(p_file);
                        p_imageFile = p_file;

                        p_LastSelected = p_file;
                    }
                    catch (IOException e1)
                    {
                        p_imageFile = null;
                        p_Image = null;
                    }

                }
                else
                {
                    p_imageFile = null;
                    p_ImagePreviewLabel.setIcon(null);
                }
                fillData();
            }
        }

        protected void fillData()
        {
            if (p_imageFile == null)
            {
                p_ImageFileButton.setText("...");
                p_ImagePreviewLabel.setIcon(null);
            }
            else
            {
                p_ImageFileButton.setText(p_imageFile.getAbsolutePath());
                p_ImagePreviewLabel.setIcon(new ImageIcon(p_Image));
            }
        }

        public boolean accept(File f)
        {
            if (f.isDirectory()) return true;
            String s_name = f.getName().toUpperCase();
            if (s_name.endsWith(".GIF") || s_name.endsWith(".JPG") || s_name.endsWith(".TGA")) return true;
            return false;
        }

        public String getDescription()
        {
            return "GIF,JPEG and TGA files";
        }
    }

    public class SoundProperties extends FileFilter implements AbstractPropertiesPage, ActionListener
    {
        private JButton p_SelectFileButton;
        private JButton p_PlaySoundButton;
        private JPanel p_Panel;
        private Component p_parent;
        private File p_soundFile;
        private AudioClip p_audioClip;
        private File p_LastSelected;

        public void fillPropertiesFromResource(AbstractRrgResource _resource)
        {
            if (_resource == null)
            {
                p_soundFile = null;
                p_audioClip = null;
            }
            else
            {
                RrgResource_Sound p_resource = (RrgResource_Sound) _resource;
                p_soundFile = p_resource.getSoundFile();
                p_audioClip = p_resource.getAudioClip();
            }
            fillData();
        }

        public SoundProperties()
        {
            super();
            p_SelectFileButton.addActionListener(this);
            p_PlaySoundButton.addActionListener(this);
            p_soundFile = null;
            p_audioClip = null;
            fillData();
        }

        public boolean accept(File f)
        {
            if (f.isDirectory()) return true;
            String s_name = f.getName().toUpperCase();
            if (s_name.endsWith(".WAV") || s_name.endsWith(".AU")) return true;
            return false;
        }

        public String getDescription()
        {
            return "WAV or AU files";
        }

        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource().equals(p_SelectFileButton))
            {
                File p_newFile = selectFile(p_parent, "Select sound", this, p_LastSelected);
                if (p_newFile == null)
                {
                    p_audioClip = null;
                    p_newFile = null;
                }
                else
                {
                    try
                    {
                        p_audioClip = Applet.newAudioClip(p_newFile.toURL());
                        p_soundFile = p_newFile;
                        p_LastSelected = p_newFile;
                    }
                    catch (Exception ex)
                    {
                        Utilities.showErrorDialog(p_Panel, "Error", ex.getMessage());
                        p_audioClip = null;
                        p_soundFile = null;
                    }
                }

                fillData();
            }
            else if (e.getSource().equals(p_PlaySoundButton))
            {
                playSound();
            }
        }

        private void playSound()
        {
            if (p_audioClip != null)
            {
                p_audioClip.play();
            }
        }

        public void fillResourceFromProperties(AbstractRrgResource _resource) throws IOException
        {
            if (_resource == null) return;
            RrgResource_Sound p_resource = (RrgResource_Sound) _resource;
            p_resource.setAudioFile(p_soundFile);
        }

        protected void fillData()
        {
            if (p_soundFile == null)
            {
                p_SelectFileButton.setText("...");
                p_PlaySoundButton.setEnabled(false);
            }
            else
            {
                p_SelectFileButton.setText(p_soundFile.getAbsolutePath());
                p_PlaySoundButton.setEnabled(true);
            }
        }

        public JPanel getPanel(Component _parent)
        {
            p_parent = _parent;
            return p_Panel;
        }

        public String isDataOk()
        {
            if (p_soundFile != null) return null;
            return "You have not selected any sound file";
        }

        public AbstractRrgResource makeNewResource(String _id) throws IOException
        {
            if (isDataOk() != null) return null;
            if (p_soundFile == null) return null;
            RrgResource_Sound p_newResource = new RrgResource_Sound(_id, p_soundFile);
            return p_newResource;
        }
    }

    public class TextProperties implements ActionListener, AbstractPropertiesPage
    {
        private JTextArea p_TextArea;
        private JPanel p_Panel;
        private Component p_parent;

        public TextProperties()
        {
            p_TextArea.setText("");
        }

        public void actionPerformed(ActionEvent e)
        {

        }

        public void fillPropertiesFromResource(AbstractRrgResource _resource)
        {
            if (_resource == null)
            {
                p_TextArea.setText("");
            }
            else
            {
                RrgResource_Text p_resource = (RrgResource_Text) _resource;
                p_TextArea.setText(p_resource.getText());
            }
        }

        public void fillResourceFromProperties(AbstractRrgResource _resource) throws IOException
        {
            if (_resource == null) return;
            RrgResource_Text p_resource = (RrgResource_Text) _resource;
            p_resource.setText(p_TextArea.getText());

        }

        public JPanel getPanel(Component _parent)
        {
            p_parent = _parent;
            return p_Panel;
        }

        public String isDataOk()
        {
            return null;
        }

        public AbstractRrgResource makeNewResource(String _id) throws IOException
        {
            if (isDataOk() != null) return null;
            RrgResource_Text p_text = new RrgResource_Text(_id, p_TextArea.getText());
            return p_text;
        }
    }

    public static final File selectFile(Component _parent, String _title, FileFilter _filter, File _directory)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        chooser.setDragEnabled(false);
        chooser.setControlButtonsAreShown(true);
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(_filter);
        chooser.setDialogTitle(_title);
        chooser.setAcceptAllFileFilterUsed(false);

        if (_directory != null) chooser.setCurrentDirectory(_directory);

        int returnVal = chooser.showDialog(_parent, "Select");

        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            return chooser.getSelectedFile();
        }
        else
            return null;
    }

    public FontProperties getFontPropertiesPanel()
    {
        return p_FontProperties;
    }

    public ImageProperties getImagePropertiesPanel()
    {
        return p_ImageProperties;
    }

    public SoundProperties getSoundPropertiesPanel()
    {
        return p_SoundProperties;
    }

    public TextProperties getTextPropertiesPanel()
    {
        return p_TextProperties;
    }

    public NewResourceDialog(JFrame _frame, ResourceContainer _resourceContainer)
    {
        super(_frame, true);
        p_ButtonCancel.addActionListener(this);
        p_ButtonOk.addActionListener(this);
        p_ResourceContainer = _resourceContainer;
        p_FontProperties = new FontProperties();
        p_ImageProperties = new ImageProperties();
        p_SoundProperties = new SoundProperties();
        p_TextProperties = new TextProperties();
        p_ResourceType = new ResourceType();
        setContentPane(p_MainDialogPanel);
        p_ContentPanel.setLayout(new BorderLayout(0, 0));
    }

    private void toCenter()
    {
        pack();

        int i_WindowWidth = getWidth();
        int i_WindowHeight = getHeight();

        int i_ScreenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int i_ScreenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

        int i_ScreenX = (i_ScreenWidth - i_WindowWidth) >> 1;
        int i_ScreenY = (i_ScreenHeight - i_WindowHeight) >> 1;

        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setLocation(i_ScreenX, i_ScreenY);
    }

    private FontProperties p_FontProperties;
    private ImageProperties p_ImageProperties;
    private SoundProperties p_SoundProperties;
    private TextProperties p_TextProperties;
    private ResourceType p_ResourceType;

    private void fillState()
    {
        p_ContentPanel.removeAll();

        switch (i_State)
        {
            case STATE_SELECTTYPE:
                {
                    p_ContentPanel.add(p_ResourceType.getPanel(), BorderLayout.CENTER);
                    setTitle("Select resource type");
                }
                ;
                break;
            case STATE_PROPERTIES:
                {
                    AbstractPropertiesPage p_Page = null;
                    switch (p_ResourceType.getSelectedType())
                    {
                        case AbstractRrgResource.TYPE_FONT:
                            p_Page = p_FontProperties;
                            break;
                        case AbstractRrgResource.TYPE_IMAGE:
                            p_Page = p_ImageProperties;
                            break;
                        case AbstractRrgResource.TYPE_SOUND:
                            p_Page = p_SoundProperties;
                            break;
                        case AbstractRrgResource.TYPE_TEXT:
                            p_Page = p_TextProperties;
                            break;
                    }

                    p_Page.fillPropertiesFromResource(null);
                    p_ContentPanel.add(p_Page.getPanel(this), BorderLayout.CENTER);
                    setTitle("Fill properties");
                }
                ;
                break;
        }

        pack();
        toCenter();
    }

    public AbstractRrgResource showDialog()
    {
        p_TextResourceID.setText("");
        p_result = null;
        i_State = STATE_SELECTTYPE;
        fillState();
        show();
        return p_result;
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource().equals(p_ButtonOk))
        {
            switch (i_State)
            {
                case STATE_SELECTTYPE:
                    {
                        i_State = STATE_PROPERTIES;
                    }
                    ;
                    break;
                case STATE_PROPERTIES:
                    {
                        String s_resourceName = p_TextResourceID.getText().trim();
                        if (s_resourceName.length() == 0)
                        {
                            Utilities.showInfoDialog(this, "Resource ID not found", "You have to enter the resource ID");
                            return;
                        }
                        else if (p_ResourceContainer.containsID(s_resourceName))
                        {
                            Utilities.showErrorDialog(this, "Wrong resource name", "You have duplicated resource name");
                            return;
                        }

                        AbstractRrgResource p_newResource = null;
                        try
                        {
                            switch (p_ResourceType.getSelectedType())
                            {
                                case AbstractRrgResource.TYPE_FONT:
                                    {
                                        p_newResource = p_FontProperties.makeNewResource(s_resourceName);
                                    }
                                    ;
                                    break;
                                case AbstractRrgResource.TYPE_IMAGE:
                                    {
                                        p_newResource = p_ImageProperties.makeNewResource(s_resourceName);
                                    }
                                    ;
                                    break;
                                case AbstractRrgResource.TYPE_SOUND:
                                    {
                                        p_newResource = p_SoundProperties.makeNewResource(s_resourceName);
                                    }
                                    ;
                                    break;
                                case AbstractRrgResource.TYPE_TEXT:
                                    {
                                        p_newResource = p_TextProperties.makeNewResource(s_resourceName);
                                    }
                                    ;
                                    break;
                            }

                            p_result = p_newResource;
                            hide();
                        }
                        catch (IOException e1)
                        {
                            Utilities.showErrorDialog(this, "Error", e1.getMessage());
                        }
                    }
                    ;
                    break;
            }
            fillState();
        }
        else if (e.getSource().equals(p_ButtonCancel))
        {
            hide();
        }
    }
}

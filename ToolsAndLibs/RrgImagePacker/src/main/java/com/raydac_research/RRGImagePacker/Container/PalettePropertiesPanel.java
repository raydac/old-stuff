package com.raydac_research.RRGImagePacker.Container;

import com.raydac_research.RRGImagePacker.Utils.Utilities;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class PalettePropertiesPanel implements IPropertiesPanel,ActionListener
{
    private JPanel p_MainPanel;
    private JPanel p_PalettePreviewScrollPanel;
    private PaletteViewComponent p_viewComponent;
    private JLabel p_Label_Colors;
    private JTextField p_TextName;
    private JTextArea p_CommentsArea;
    private JButton p_FileRefreshButton;
    private JLabel p_LabelFileLink;

    private PaletteCnt p_currentContainer;

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource().equals(p_FileRefreshButton))
        {
            if (p_currentContainer!=null)
            {
                String s_fileName = p_currentContainer.getFileName();

                int [] ai_palette = null;

                File p_file = new File(s_fileName);
                if (!p_file.exists())
                {
                    Utilities.showErrorDialog(null,"I can't refresh the palette","I can't find \""+s_fileName+"\" file");
                    return;
                }

                try
                {
                    if (s_fileName.toLowerCase().endsWith(".pal"))
                    {
                        ai_palette = PaletteCnt.loadPALPalette(p_file);
                    }
                    else
                    if (s_fileName.toLowerCase().endsWith(".act"))
                    {
                        ai_palette = PaletteCnt.loadACTPAlette(p_file);
                    }
                }
                catch (IOException e1)
                {
                    Utilities.showErrorDialog(null,"I can't refresh the palette","I can't load \""+s_fileName+"\" file");
                    return;
                }

                p_currentContainer.loadFromArray(ai_palette);
                getPanel(p_currentContainer,null);
            }
        }
    }

    public String isDataOk()
    {
        String s_name = p_TextName.getText().trim();
        if (s_name.length() == 0) return "You must have non emty name field";
        return null;
    }

    public void fillObjectFromPanel(Object _object)
    {
        PaletteCnt p_palette = (PaletteCnt) _object;
        String s_name = p_TextName.getText().trim();
        p_palette.setName(s_name);
        p_palette.setComments(getComments());
    }

    public PalettePropertiesPanel()
    {
        p_viewComponent = new PaletteViewComponent(this);
        p_PalettePreviewScrollPanel.add(p_viewComponent);

        p_FileRefreshButton.addActionListener(this);
    }

    public void setComments(String _str)
    {
        p_CommentsArea.setText(_str);
    }

    public String getComments()
    {
        return p_CommentsArea.getText();
    }

    public JPanel getPanel(Object _obj, Container _container)
    {
        PaletteCnt p_palette = (PaletteCnt) _obj;

        p_currentContainer = p_palette;

        p_viewComponent.setPaletteContainer(p_palette);
        p_Label_Colors.setText(""+p_palette.getLength());

        p_TextName.setText(p_palette.getName());
        p_LabelFileLink.setText(p_palette.getFileName());

        setComments(p_palette.getComments());

        return p_MainPanel;
    }
}

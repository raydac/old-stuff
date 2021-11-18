package com.raydac_research.FormEditor.ExportModules;

import com.raydac_research.FormEditor.RrgFormComponents.FormCollection;
import com.raydac_research.FormEditor.RrgFormComponents.FormContainer;
import com.raydac_research.FormEditor.RrgFormResources.ResourceContainer;
import com.raydac_research.FormEditor.Misc.Utilities;
import com.raydac_research.FormEditor.ExportModules.RrgScene.export_RRG_SCENE;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class FormExport extends JDialog implements ActionListener
{
    private JPanel p_MainPanel;
    private JButton p_ButtonOk;
    private JButton p_ButtonCancel;
    private JPanel p_OptionPanel;

    private int i_State;

    private static final int STATE_SELECTTYPE = 0;
    private static final int STATE_SELECTFORM = 1;
    private static final int STATE_FILLPROPERTIES = 2;

    private class SelectExportedForms implements ActionListener
    {
        private JPanel p_SelectFormPanel;
        private JPanel p_FormsList;
        private JButton p_SelectAllFormsButton;
        private JButton p_SelectCurrentFormButton;

        private Vector p_buttons;
        private int i_indexOfCurrent;

        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource().equals(p_SelectAllFormsButton))
            {
               for(int li=0;li<p_buttons.size();li++)
               {
                   ((ButtonContainer)p_buttons.elementAt(li)).setSelected(true);
               }
            }
            else
            if(e.getSource().equals(p_SelectCurrentFormButton))
            {
                for(int li=0;li<p_buttons.size();li++)
                {
                    if (li==i_indexOfCurrent)
                    {
                        ((ButtonContainer)p_buttons.elementAt(li)).setSelected(true);
                    }
                    else
                        ((ButtonContainer)p_buttons.elementAt(li)).setSelected(false);
                }
            }
        }

        public SelectExportedForms()
        {
            p_buttons = new Vector();
            p_SelectAllFormsButton.addActionListener(this);
            p_SelectCurrentFormButton.addActionListener(this);
        }

        public boolean isSelectedAnyButton()
        {
            for(int li =0;li<p_buttons.size();li++)
            {
                if (((ButtonContainer)p_buttons.elementAt(li)).isSelected()) return true;
            }
            return false;
        }

        private class ButtonContainer extends JRadioButton
        {
            protected FormContainer p_fc;

            public FormContainer getForm()
            {
                return p_fc;
            }

            public ButtonContainer(String _id,FormContainer _form)
            {
                super(_id);
                p_fc = _form;
            }
        }

        public void deinit()
        {
            p_buttons.clear();
            p_FormsList.removeAll();
        }

        public FormsList getExportingForms()
        {
            FormsList p_list = new FormsList();

            for(int li=0;li<p_buttons.size();li++)
            {
                ButtonContainer p_bc = (ButtonContainer) p_buttons.elementAt(li);
                if (p_bc.isSelected())
                {
                    p_list.add(p_bc.getForm());
                }
            }
            return p_list;
        }

        public JPanel getPanel(AbstractFormExportModule _module, FormCollection _collection)
        {
            synchronized (_collection)
            {
                p_buttons.removeAllElements();
                p_FormsList.removeAll();

                ButtonGroup p_buttonGroup = _module.supportsMultiform() ? null : new ButtonGroup();

                p_FormsList.setLayout(new GridLayout(_collection.getFormsNumber(),1));

                i_indexOfCurrent = -1;

                for (int li = 0; li < _collection.getFormsNumber(); li++)
                {
                    FormContainer p_form = _collection.getFormAt(li);
                    ButtonContainer p_button = new ButtonContainer(p_form.getID(),p_form);

                    if (p_buttonGroup != null)
                    {
                        p_buttonGroup.add(p_button);
                    }

                    if (p_form.equals(_collection.getSelectedForm()))
                    {
                        p_button.setSelected(true);
                        i_indexOfCurrent = li;
                    }
                    else
                    {
                        p_button.setSelected(false);
                    }

                    p_FormsList.add(p_button);
                    p_buttons.add(p_button);
                }

                if(_module.supportsMultiform())
                {
                    p_SelectAllFormsButton.setEnabled(true);
                }
                else
                {
                    p_SelectAllFormsButton.setEnabled(false);
                }

                return p_SelectFormPanel;
            }
        }
    }

    private class SelectExportTypePanel
    {
        private JPanel p_MainPanel;
        private JRadioButton p_DOBGUI_button;
        private ButtonGroup p_buttonGroup;

        public static final int EXPORT_DOBGUI = 0;
        public static final int EXPORT_RRGPATHS = 1;
        public static final int EXPORT_RRGSCENE = 2;
        private JRadioButton p_RRGPATHS_button;
        private JRadioButton p_RRGSCENE_button;

        public JPanel getPanel()
        {
            p_DOBGUI_button.setSelected(true);
            return p_MainPanel;
        }

        public int getSelectedType()
        {
            if (p_DOBGUI_button.isSelected()) return EXPORT_DOBGUI;
            if (p_RRGPATHS_button.isSelected()) return EXPORT_RRGPATHS;
            if (p_RRGSCENE_button.isSelected()) return EXPORT_RRGSCENE;
            return -1;
        }

        public SelectExportTypePanel()
        {
            p_buttonGroup = new ButtonGroup();
            p_buttonGroup.add(p_DOBGUI_button);
            p_buttonGroup.add(p_RRGPATHS_button);
            p_buttonGroup.add(p_RRGSCENE_button);
        }
    }

    private SelectExportTypePanel p_SelectExportTypePanel;
    private SelectExportedForms p_SelectExportedForms;
    private export_DOBGUI p_export_dobgui;
    private export_RRG_PATHS p_export_rrgpaths;
    private export_RRG_SCENE p_export_rrgscene;
    private FormCollection p_formCollection;
    private FormsList p_formList;
    private ResourceContainer p_resourceContainer;

    public FormExport(JFrame _owner)
    {
        super(_owner, true);
        p_export_dobgui = new export_DOBGUI();
        p_export_rrgpaths = new export_RRG_PATHS();
        p_export_rrgscene = new export_RRG_SCENE();
        p_SelectExportTypePanel = new SelectExportTypePanel();
        p_SelectExportedForms = new SelectExportedForms();
        p_OptionPanel.setLayout(new BorderLayout(0, 0));
        p_ButtonOk.addActionListener(this);
        p_ButtonCancel.addActionListener(this);
        setContentPane(p_MainPanel);
    }

    public void showDialog(ResourceContainer _resources, FormCollection _form)
    {
        p_resourceContainer = _resources;
        p_formCollection = _form;
        i_State = STATE_SELECTTYPE;
        fillForState();
        show();
    }

    private AbstractFormExportModule p_currentPanel;

    private void fillForState()
    {
        p_OptionPanel.removeAll();

        switch (i_State)
        {
            case STATE_SELECTTYPE:
                {
                    p_OptionPanel.add(p_SelectExportTypePanel.getPanel(), BorderLayout.CENTER);
                    setTitle("Select an export type");
                }
                ;
                break;
            case STATE_SELECTFORM:
                {
                    int i_type = p_SelectExportTypePanel.getSelectedType();
                    AbstractFormExportModule p_panel = getPanelForType(i_type);
                    p_OptionPanel.add(p_SelectExportedForms.getPanel(p_panel,p_formCollection), BorderLayout.CENTER);
                    setTitle("Select forms for export");
                }
                ;
                break;
            case STATE_FILLPROPERTIES:
                {
                    int i_type = p_SelectExportTypePanel.getSelectedType();
                    AbstractFormExportModule p_panel = getPanelForType(i_type);
                    p_currentPanel = p_panel;
                    p_panel.init(p_formCollection);
                    p_OptionPanel.add(p_panel.getPanel(), BorderLayout.CENTER);
                    setTitle(p_panel.getName());
                }
                ;
                break;
        }

        pack();
        Utilities.toScreenCenter(this);
    }

    private AbstractFormExportModule getPanelForType(int _type)
    {
        switch (_type)
        {
            case SelectExportTypePanel.EXPORT_DOBGUI:
                return p_export_dobgui;
            case SelectExportTypePanel.EXPORT_RRGPATHS:
                return p_export_rrgpaths;
            case SelectExportTypePanel.EXPORT_RRGSCENE:
                return p_export_rrgscene;
        }
        return null;
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource().equals(p_ButtonOk))
        {
            switch (i_State)
            {
                case STATE_SELECTTYPE:
                    {
                        i_State = STATE_SELECTFORM;
                        fillForState();
                        return;
                    }
                case STATE_SELECTFORM:
                    {
                        if (!p_SelectExportedForms.isSelectedAnyButton())
                        {
                            Utilities.showErrorDialog(this, "Select form", "You have to select a form for export");
                            return;
                        }

                        i_State = STATE_FILLPROPERTIES;
                        p_formList = p_SelectExportedForms.getExportingForms();
                        p_SelectExportedForms.deinit();
                        fillForState();
                        return;
                    }
                case STATE_FILLPROPERTIES:
                    {
                        File p_file = Utilities.selectFileForSave(this, p_currentPanel.getFileFilter(), p_currentPanel.getName(), null);
                        if (p_file != null)
                        {
                            p_file = p_currentPanel.processFileNameBeforeSaving(p_file);

                            try
                            {
                                p_currentPanel.exportData(p_file, p_resourceContainer, p_formList);
                            }
                            catch (IOException e1)
                            {
                                Utilities.showErrorDialog(this, "Error during exporting process", e1.getMessage());
                                return;
                            }
                            finally
                            {
                                p_formCollection = null;
                                p_formList.clear();
                            }
                            hide();
                        }
                        else
                            return;
                    }
                    ;
                    break;
            }
        }
        else if (e.getSource().equals(p_ButtonCancel))
        {
            hide();
        }
    }
}

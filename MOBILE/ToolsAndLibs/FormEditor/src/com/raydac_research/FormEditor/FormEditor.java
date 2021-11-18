package com.raydac_research.FormEditor;

import com.raydac_research.FormEditor.Misc.SplashForm;
import com.raydac_research.FormEditor.Misc.MainForm;

import javax.swing.*;

public class FormEditor
{
    public static final void main(String [] _args)
    {
        MainForm p_MainForm = new MainForm();
        SplashForm p_SplashForm = new SplashForm(p_MainForm.getMainFrame());
    }
}

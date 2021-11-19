package com.raydac_research.FormEditor;

import com.raydac_research.FormEditor.Misc.SplashForm;
import com.raydac_research.FormEditor.Misc.MainForm;

import javax.swing.*;

public class FormEditor
{
    public static final void main(String [] _args)
    {
        String s_file = null;
        if (_args.length>0) s_file = _args[0];

        MainForm p_MainForm = new MainForm(s_file);

        SplashForm p_SplashForm = null;
        if (s_file==null)
            p_SplashForm = new SplashForm(p_MainForm.getMainFrame());
    }
}

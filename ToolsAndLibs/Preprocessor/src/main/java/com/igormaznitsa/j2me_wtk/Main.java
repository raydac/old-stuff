package com.igormaznitsa.j2me_wtk;

import com.raydac_research.FormEditor.Misc.SplashForm;

public class Main
{
    public static final String APP_ID = "RRGWTK10";

    public static final void main(String [] _args)
    {
        mainForm p_mainForm = new mainForm();
        SplashForm p_SplashForm = new SplashForm(p_mainForm);
    }
}

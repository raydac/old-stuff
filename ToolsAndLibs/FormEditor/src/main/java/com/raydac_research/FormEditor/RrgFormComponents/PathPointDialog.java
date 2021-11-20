package com.raydac_research.FormEditor.RrgFormComponents;

import com.raydac_research.FormEditor.Misc.Utilities;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PathPointDialog extends JDialog implements ActionListener
{
    private JPanel p_MainPanel;
    private JButton p_Button_Ok;
    private JButton p_Button_Cancel;
    private JTextField p_Text_Steps;
    private JTextField p_Text_Y;
    private JTextField p_Text_X;

    private boolean lg_result;

    private PathPoint p_PathPoint;

    public PathPointDialog(JFrame _frame)
    {
        super(_frame,true);

        setContentPane(p_MainPanel);

        p_Button_Ok.addActionListener(this);
        p_Button_Cancel.addActionListener(this);
    }

    public boolean editPathPoint(String _title,PathPoint _point)
    {
        setTitle(_title);

        p_PathPoint = _point;

        p_Text_X.setText(Integer.toString(_point.i_X));
        p_Text_Y.setText(Integer.toString(_point.i_Y));
        p_Text_Steps.setText(Integer.toString(_point.i_Steps));

        Utilities.toScreenCenter(this);

        pack();

        lg_result = true;
        show();
        return lg_result;
    }

    private String validateData()
    {
        try
        {
            Integer.parseInt(p_Text_X.getText());
        }
        catch (NumberFormatException e)
        {
            return "Error X value";
        }

        try
        {
            Integer.parseInt(p_Text_Y.getText());
        }
        catch (NumberFormatException e)
        {
            return "Error Y value";
        }

        int i_steps = 0;

        try
        {
            i_steps =Integer.parseInt(p_Text_Steps.getText());
        }
        catch (NumberFormatException e)
        {
            return "Error Steps value";
        }

        if (i_steps<=0) return "You must not have the zero or a negative value as steps";

        return null;
    }

    private void fillData()
    {
        int i_X = 0,i_Y = 0,i_Step= 0;
        try
        {
            i_X = Integer.parseInt(p_Text_X.getText());
            i_Y = Integer.parseInt(p_Text_Y.getText());
            i_Step = Integer.parseInt(p_Text_Steps.getText());
        }
        catch (NumberFormatException e)
        {
        }

        p_PathPoint.i_X = i_X;
        p_PathPoint.i_Y = i_Y;
        p_PathPoint.i_Steps = i_Step;
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource().equals(p_Button_Ok))
        {
            String s_str = validateData();
            if (s_str!=null)
            {
                Utilities.showErrorDialog(this,"ERROR",s_str);
                return;
            }
            else
            {
                fillData();
                lg_result = true;
            }
        }
        else
        if (e.getSource().equals(p_Button_Cancel))
        {
            lg_result = false;
        }
        hide();
    }
}

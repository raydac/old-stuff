package FinancialCalc.EvalModules;

import FinancialCalc.EvalModule;
import FinancialCalc.MenuActionListener;

public class CurrentRatio extends EvalModule
{
    private double d_WorkingAssets;
    private double d_CurrentLiabilities;

    private double d_Result;

    private static final int FIELD_WA = 0;
    private static final int FIELD_CL = 1;

    public String getModuleName(MenuActionListener _listener)
    {
        return "Current ratio";
    }

    public void beginTransaction()
    {
        d_WorkingAssets = 0d;
        d_CurrentLiabilities = 0d;
        d_Result = 0d;
    }

    public void calculate()
    {
        d_Result = d_WorkingAssets / d_CurrentLiabilities;
    }

    public String checkFields()
    {
        if (d_CurrentLiabilities == 0) return "Текущие обязательства не могут быть 0";
        return null;
    }

    public int getFieldsNumber()
    {
        return 2;
    }

    public String getFieldValue(int _field)
    {
        switch (_field)
        {
            case FIELD_CL:
                return convertDoubleToString(d_CurrentLiabilities);
            case FIELD_WA:
                return convertDoubleToString(d_WorkingAssets);
        }
        return null;
    }

    public String getNameForField(int _field, MenuActionListener _listener)
    {
        switch (_field)
        {
            case FIELD_CL:
                return "Текущ.обязательства";
            case FIELD_WA:
                return "Текущ.активы";
        }
        return null;
    }

    public String getNameForResult(int _result, MenuActionListener _listener)
    {
        return "Текущая ликвидность";
    }

    public String getResult(int _result)
    {
        switch (_result)
        {
            case 0:
                return convertDoubleToString(d_Result);
        }
        return null;
    }

    public int getResultsNumber()
    {
        return 1;
    }

    public String setFieldValue(int _field, String _value)
    {
        double p_dbl = 0;

        try
        {
            p_dbl = Double.parseDouble(_value);
        }
        catch (NumberFormatException e)
        {
            return "Ошибочное значение";
        }

        switch (_field)
        {
            case FIELD_CL:
                {
                    d_CurrentLiabilities = p_dbl;
                    if (d_CurrentLiabilities==0d)
                    {
                        return "Текущие обязательства не могут быть 0";
                    }
                }
                ;
                break;
            case FIELD_WA:
                {
                    d_WorkingAssets = p_dbl;
                }
                ;
                break;
        }
        return null;
    }
}

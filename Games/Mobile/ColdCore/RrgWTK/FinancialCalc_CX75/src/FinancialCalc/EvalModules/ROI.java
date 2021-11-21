package FinancialCalc.EvalModules;

import FinancialCalc.EvalModule;
import FinancialCalc.MenuActionListener;

public class ROI extends EvalModule
{
    private double d_OpIncome;
    private double d_Assets;
    private double d_Result;

    private static final int FIELD_OPINC = 0;
    private static final int FIELD_ASSTS = 1;

    public String getModuleName(MenuActionListener _listener)
    {
        return "Return on investment";
    }

    public void beginTransaction()
    {
        d_OpIncome = 0d;
        d_Assets = 0d;
        d_Result = 0d;
    }

    public void calculate()
    {
        d_Result = d_OpIncome / d_Assets;
    }

    public String checkFields()
    {
        if (d_Assets == 0) return "Активы не могут быть 0";
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
            case FIELD_OPINC:
                return convertDoubleToString(d_OpIncome);
            case FIELD_ASSTS:
                return convertDoubleToString(d_Assets);
        }
        return null;
    }

    public String getNameForField(int _field, MenuActionListener _listener)
    {
        switch (_field)
        {
            case FIELD_OPINC:
                return "Операц.доход";
            case FIELD_ASSTS:
                return "Активы";
        }
        return null;
    }

    public String getNameForResult(int _result, MenuActionListener _listener)
    {
        return "Доходн.на инвестиции(ROI)";
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
            case FIELD_ASSTS:
                {
                    d_Assets = p_dbl;
                    if (d_Assets==0d)
                    {
                        return "Активы не могут быть 0";
                    }
                }
                ;
                break;
            case FIELD_OPINC:
                {
                    d_OpIncome = p_dbl;
                }
                ;
                break;
        }
        return null;
    }
}

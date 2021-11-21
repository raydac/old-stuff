package FinancialCalc.EvalModules;

import FinancialCalc.EvalModule;
import FinancialCalc.MenuActionListener;

public class DependenceRatio extends EvalModule
{
    private double d_E;
    private double d_TA;

    private double d_Result;

    private static final int FIELD_E = 0;
    private static final int FIELD_TA = 1;

    public String getModuleName(MenuActionListener _listener)
    {
        return "Dependence Ratio";
    }

    public void beginTransaction()
    {
        d_E = 0d;
        d_TA = 0d;
        d_Result = 0d;
    }

    public void calculate()
    {
        d_Result = 1d- d_E / d_TA;
    }

    public String checkFields()
    {
        if (d_TA == 0) return "Общие активы не могут быть 0";
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
            case FIELD_E:
                return convertDoubleToString(d_E);
            case FIELD_TA:
                return convertDoubleToString(d_TA);
        }
        return null;
    }

    public String getNameForField(int _field, MenuActionListener _listener)
    {
        switch (_field)
        {
            case FIELD_E:
                return "Собств.капитал";
            case FIELD_TA:
                return "Общ.активы";
        }
        return null;
    }

    public String getNameForResult(int _result, MenuActionListener _listener)
    {
        return "Коэфф.фин.зависимости";
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
            case FIELD_TA:
                {
                    d_TA = p_dbl;
                    if (d_TA==0d)
                    {
                        return "Общие активы не могут быть 0";
                    }
                }
                ;
                break;
            case FIELD_E:
                {
                    d_E = p_dbl;
                }
                ;
                break;
        }
        return null;
    }
}

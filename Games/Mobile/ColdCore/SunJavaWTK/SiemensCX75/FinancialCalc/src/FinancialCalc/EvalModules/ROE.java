package FinancialCalc.EvalModules;

import FinancialCalc.EvalModule;
import FinancialCalc.MenuActionListener;

public class ROE extends EvalModule
{
    private double d_NetProfit;
    private double d_ShareCapital;
    private double d_Result;

    private static final int FIELD_NETPROF = 0;
    private static final int FIELD_SHARCAP = 1;

    public String getModuleName(MenuActionListener _listener)
    {
        return "Return on equity";
    }

    public void beginTransaction()
    {
        d_NetProfit = 0d;
        d_ShareCapital = 0d;
        d_Result = 0d;
    }

    public void calculate()
    {
        d_Result = d_NetProfit / d_ShareCapital;
    }

    public String checkFields()
    {
        if (d_ShareCapital == 0) return "Акц.капитал не может быть 0";
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
            case FIELD_NETPROF:
                return convertDoubleToString(d_NetProfit);
            case FIELD_SHARCAP:
                return convertDoubleToString(d_ShareCapital);
        }
        return null;
    }

    public String getNameForField(int _field, MenuActionListener _listener)
    {
        switch (_field)
        {
            case FIELD_NETPROF:
                return "Чистый доход";
            case FIELD_SHARCAP:
                return "Акцион.капитал";
        }
        return null;
    }

    public String getNameForResult(int _result, MenuActionListener _listener)
    {
        return "Поступл.на акц.капитал(ROE)";
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
            return "Ошибочное значение.";
        }

        switch (_field)
        {
            case FIELD_SHARCAP:
                {
                    d_ShareCapital = p_dbl;
                    if (d_ShareCapital==0d)
                    {
                        return "Акц.капита не может быть 0";
                    }
                }
                ;
                break;
            case FIELD_NETPROF:
                {
                    d_NetProfit = p_dbl;
                }
                ;
                break;
        }
        return null;
    }
}

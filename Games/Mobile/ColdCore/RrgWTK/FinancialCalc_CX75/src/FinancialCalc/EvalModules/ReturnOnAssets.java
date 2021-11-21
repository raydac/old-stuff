package FinancialCalc.EvalModules;

import FinancialCalc.EvalModule;
import FinancialCalc.MenuActionListener;

public class ReturnOnAssets extends EvalModule
{
    private double d_ClearProfit;
    private double d_Asset;

    private double d_Result;

    private static final int FIELD_CPROFIT = 0;
    private static final int FIELD_ASSET = 1;

    public String getModuleName(MenuActionListener _listener)
    {
        return "Коэфф. рентабельности продаж";
    }

    public void beginTransaction()
    {
        d_ClearProfit = 0d;
        d_Asset = 0d;

        d_Result = 0d;
    }

    public void calculate()
    {
        d_Result = d_ClearProfit / d_Asset;
    }

    public String checkFields()
    {
        if (d_Asset == 0) return "Текущие обязательства не могут быть 0";
        return null;
    }

    public int getFieldsNumber()
    {
        return 2;
    }

    public String getFieldValue(int _field)
    {
        switch(_field)
        {
            case FIELD_CPROFIT : return convertDoubleToString(d_ClearProfit);
            case FIELD_ASSET : return convertDoubleToString(d_Asset);
        }

        return null;
    }

    public String getNameForField(int _field, MenuActionListener _listener)
    {
        switch(_field)
        {
            case FIELD_CPROFIT : return "Чист.прибыль";
            case FIELD_ASSET : return "Активы";
        }
        return null;
    }

    public String getNameForResult(int _result, MenuActionListener _listener)
    {
        return "Доходн.на активы(ROA)";
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
            case FIELD_ASSET:
                {
                    d_Asset = p_dbl;
                    if (d_Asset==0d)
                    {
                        return "Активы не могут быть 0";
                    }
                }
                ;
                break;
            case FIELD_CPROFIT:
                {
                    d_ClearProfit = p_dbl;
                }
                ;
                break;
        }
        return null;
    }
}

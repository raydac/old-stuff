package FinancialCalc.EvalModules;

import FinancialCalc.MenuActionListener;
import FinancialCalc.EvalModule;

public class PEratio  extends EvalModule
{
    private double d_MarketCost;
    private double d_ClearProfit;

    private double d_Result;

    private static final int FIELD_MCOST = 0;
    private static final int FIELD_CPROFIT = 1;

    public String getModuleName(MenuActionListener _listener)
    {
        return "P/E Ration";
    }

    public void beginTransaction()
    {
        d_ClearProfit = 0d;
        d_MarketCost = 0d;
        d_Result = 0d;
    }

    public void calculate()
    {
        d_Result = d_MarketCost/d_ClearProfit;
    }

    public String checkFields()
    {
        if (d_ClearProfit == 0)
        {
            return "Чистая прибыль на акцию не может быть 0";
        }
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
            case FIELD_CPROFIT:
                return convertDoubleToString(d_ClearProfit);
            case FIELD_MCOST:
                return convertDoubleToString(d_MarketCost);
        }
        return null;
    }

    public String getNameForField(int _field, MenuActionListener _listener)
    {
        switch (_field)
        {
            case FIELD_MCOST:
                return "Рыночн.цена акции";
            case FIELD_CPROFIT:
                return "Чист.прибыль";
        }
        return null;
    }

    public String getNameForResult(int _result, MenuActionListener _listener)
    {
        return "P/E";
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
            case FIELD_CPROFIT:
                {
                    d_ClearProfit = p_dbl;
                    if (d_ClearProfit == 0)
                    {
                        return "Чист.прибыль не может быть 0";
                    }
                }
                ;
                break;
            case FIELD_MCOST:
                {
                    d_MarketCost = p_dbl;
                }
                ;
                break;
        }
        return null;
    }

}

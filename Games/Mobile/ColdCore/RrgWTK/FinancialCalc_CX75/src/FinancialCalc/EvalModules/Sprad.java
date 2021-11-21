package FinancialCalc.EvalModules;

import FinancialCalc.MenuActionListener;
import FinancialCalc.EvalModule;

public class Sprad  extends EvalModule
{
    private double d_MaxCost;
    private double d_MinCost;

    private double d_Result;

    private static final int FIELD_MAXCOST = 0;
    private static final int FIELD_MINCOST = 1;

    public String getModuleName(MenuActionListener _listener)
    {
        return "Спрэд";
    }

    public void beginTransaction()
    {
        d_MaxCost = 0d;
        d_MinCost = 0d;
        d_Result = 0d;
    }

    public void calculate()
    {
        d_Result = ((d_MaxCost - d_MinCost) /d_MinCost)*100d;
    }

    public String checkFields()
    {
        if (d_MinCost == 0)
        {
            return "Минимальная цена не может быть ноль";
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
            case FIELD_MAXCOST:
                return convertDoubleToString(d_MaxCost);
            case FIELD_MINCOST:
                return convertDoubleToString(d_MinCost);
        }
        return null;
    }

    public String getNameForField(int _field, MenuActionListener _listener)
    {
        switch (_field)
        {
            case FIELD_MAXCOST:
                return "Макс.цена";
            case FIELD_MINCOST:
                return "Мин.цена";
        }
        return null;
    }

    public String getNameForResult(int _result, MenuActionListener _listener)
    {
        return "Спрэд";
    }

    public String getResult(int _result)
    {
        switch (_result)
        {
            case 0:
                return convertDoubleToString(d_Result)+"%";
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
            case FIELD_MAXCOST:
                {
                    d_MaxCost = p_dbl;
                }
                ;
                break;
            case FIELD_MINCOST:
                {
                    d_MinCost = p_dbl;
                    if (d_MinCost == 0)
                    {
                        return "Мин.стоимость не может быть ноль";
                    }
                }
                ;
                break;
        }
        return null;
    }

}

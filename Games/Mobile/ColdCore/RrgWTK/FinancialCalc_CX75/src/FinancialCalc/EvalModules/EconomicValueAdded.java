package FinancialCalc.EvalModules;

import FinancialCalc.EvalModule;
import FinancialCalc.MenuActionListener;

public class EconomicValueAdded extends EvalModule
{
    private double d_NOPAT;
    private double d_BookValue;
    private double d_CapitalValue;

    private double d_Result;

    private static final int FIELD_NOPAT = 0;
    private static final int FIELD_BOOKVALUE = 1;
    private static final int FIELD_CAPVALUE = 2;

    public String getModuleName(MenuActionListener _listener)
    {
        return "Economic value added";
    }

    public void beginTransaction()
    {
        d_BookValue = 0d;
        d_CapitalValue = 0d;
        d_NOPAT = 0d;
        d_Result = 0d;
    }

    public void calculate()
    {
        d_Result = d_NOPAT - d_BookValue * d_CapitalValue;
    }

    public String checkFields()
    {
        return null;
    }

    public int getFieldsNumber()
    {
        return 3;
    }

    public String getFieldValue(int _field)
    {
        switch (_field)
        {
            case FIELD_NOPAT:
                return convertDoubleToString(d_NOPAT);
            case FIELD_BOOKVALUE:
                return convertDoubleToString(d_BookValue);
            case FIELD_CAPVALUE:
                return convertDoubleToString(d_CapitalValue);
        }
        return null;
    }

    public String getNameForField(int _field, MenuActionListener _listener)
    {
        switch (_field)
        {
            case FIELD_NOPAT:
                return "Чистая оп.прибыль";
            case FIELD_CAPVALUE:
                return "Стоим.капитала";
            case FIELD_BOOKVALUE:
                return "Баланс.стоимость";
        }
        return null;
    }

    public String getNameForResult(int _result, MenuActionListener _listener)
    {
        return "Доб.экономич.стоимость";
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
            case FIELD_BOOKVALUE:
                {
                    d_BookValue = p_dbl;
                }
                ;
                break;
            case FIELD_CAPVALUE:
                {
                    d_CapitalValue = p_dbl;
                }
                ;
                break;
            case FIELD_NOPAT:
                {
                    d_NOPAT = p_dbl;
                }
                ;
                break;
        }
        return null;
    }
}

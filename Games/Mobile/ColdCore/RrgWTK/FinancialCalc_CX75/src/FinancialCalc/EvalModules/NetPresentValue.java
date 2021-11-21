package FinancialCalc.EvalModules;

import FinancialCalc.EvalModule;
import FinancialCalc.MenuActionListener;

public class NetPresentValue extends EvalModule
{
    private double d_NCF;
    private double d_INV;
    private double d_Result;

    private static final int FIELD_NCF = 0;
    private static final int FIELD_INV = 1;

    public String getModuleName(MenuActionListener _listener)
    {
        return "Net Present Value";
    }

    public void beginTransaction()
    {
        d_NCF = 0d;
        d_INV = 0d;
        d_Result = 0d;
    }

    public void calculate()
    {
        d_Result = d_NCF - d_INV;
    }

    public String checkFields()
    {
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
            case FIELD_INV:
                return convertDoubleToString(d_INV);
            case FIELD_NCF:
                return convertDoubleToString(d_NCF);
        }
        return null;
    }

    public String getNameForField(int _field, MenuActionListener _listener)
    {
        switch (_field)
        {
            case FIELD_INV:
                return "Инвестиции";
            case FIELD_NCF:
                return "Доходы";
        }
        return null;
    }

    public String getNameForResult(int _result, MenuActionListener _listener)
    {
        return "Чистый дисконт.поток";
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
            case FIELD_INV:
                {
                    d_INV = p_dbl;
                }
                ;
                break;
            case FIELD_NCF:
                {
                    d_NCF = p_dbl;
                }
                ;
                break;
        }
        return null;
    }
}

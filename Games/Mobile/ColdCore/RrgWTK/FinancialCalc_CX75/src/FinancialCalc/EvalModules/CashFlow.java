package FinancialCalc.EvalModules;

import FinancialCalc.EvalModule;
import FinancialCalc.MenuActionListener;

public class CashFlow extends EvalModule
{
    private double d_IncomeFromOperations;
    private double d_NoncashExpenses;
    private double d_NoncashSales;

    private double d_Result;

    private static final int FIELD_INCFROMOP = 0;
    private static final int FIELD_NONCASHEXP = 1;
    private static final int FIELD_NONCASHSALES = 2;

    public String getModuleName(MenuActionListener _listener)
    {
        return "Cash Flow from Operations";
    }

    public void beginTransaction()
    {
        d_IncomeFromOperations = 0d;
        d_NoncashExpenses = 0d;
        d_NoncashSales = 0d;
        d_Result = 0d;
    }

    public void calculate()
    {
        d_Result = (d_IncomeFromOperations + d_NoncashExpenses - d_NoncashSales)/d_IncomeFromOperations;
    }

    public String checkFields()
    {
        if (d_IncomeFromOperations == 0)
        {
            return "Доходы не могут быть 0";
        }
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
            case FIELD_INCFROMOP:
                return convertDoubleToString(d_IncomeFromOperations);
            case FIELD_NONCASHEXP:
                return convertDoubleToString(d_NoncashExpenses);
            case FIELD_NONCASHSALES:
                return convertDoubleToString(d_NoncashSales);
        }
        return null;
    }

    public String getNameForField(int _field, MenuActionListener _listener)
    {
        switch (_field)
        {
            case FIELD_INCFROMOP:
                return "Доходы";
            case FIELD_NONCASHEXP:
                return "Безнал.расходы";
            case FIELD_NONCASHSALES :
                return "Безнал.продажи";
        }
        return null;
    }

    public String getNameForResult(int _result, MenuActionListener _listener)
    {
        return "Поток наличн.";
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
            case FIELD_INCFROMOP:
                {
                    d_IncomeFromOperations = p_dbl;
                    if (d_IncomeFromOperations == 0)
                    {
                        return "Доходы не могут быть 0";
                    }
                }
                ;
                break;
            case FIELD_NONCASHEXP:
                {
                    d_NoncashExpenses = p_dbl;
                }
                ;
                break;
            case FIELD_NONCASHSALES:
                {
                    d_NoncashSales = p_dbl;
                }
                ;
                break;
        }
        return null;
    }
}

package FinancialCalc.EvalModules;

import FinancialCalc.EvalModule;
import FinancialCalc.MenuActionListener;

public class EBITDA extends EvalModule
{
    private double d_NetSales;
    private double d_OperatingExpenses;
    private double d_DepreciationExpenses;
    private double d_AmortizationExpenses;

    private double d_Result;

    private static final int FIELD_NETSALES = 0;
    private static final int FIELD_OPEXPENSES = 1;
    private static final int FIELD_DEPREXPENSES = 2;
    private static final int FIELD_AMORTEXPENSES = 3;

    public String getModuleName(MenuActionListener _listener)
    {
        return "EBITDA";
    }

    public void beginTransaction()
    {
        d_NetSales = 0d;
        d_OperatingExpenses = 0d;
        d_DepreciationExpenses = 0d;
        d_AmortizationExpenses = 0d;
        d_Result = 0d;
    }

    public void calculate()
    {
        d_Result = d_NetSales - d_OperatingExpenses + d_DepreciationExpenses + d_AmortizationExpenses;
    }

    public String checkFields()
    {
        return null;
    }

    public int getFieldsNumber()
    {
        return 4;
    }

    public String getFieldValue(int _field)
    {
        switch (_field)
        {
            case FIELD_NETSALES:
                return convertDoubleToString(d_NetSales);
            case FIELD_AMORTEXPENSES:
                return convertDoubleToString(d_AmortizationExpenses);
            case FIELD_DEPREXPENSES:
                return convertDoubleToString(d_DepreciationExpenses);
            case FIELD_OPEXPENSES:
                return convertDoubleToString(d_OperatingExpenses);
        }
        return null;
    }

    public String getNameForField(int _field, MenuActionListener _listener)
    {
        switch (_field)
        {
            case FIELD_NETSALES:
                return "Чист.продажи";
            case FIELD_AMORTEXPENSES:
                return "Невыплачен.задолженности";
            case FIELD_DEPREXPENSES :
                return "Аморт.издержки";
            case FIELD_OPEXPENSES :
                return "Текущ.расходы";
        }
        return null;
    }

    public String getNameForResult(int _result, MenuActionListener _listener)
    {
        return "EBITDA";
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
            case FIELD_AMORTEXPENSES:
                {
                    d_AmortizationExpenses = p_dbl;
                }
                ;
                break;
            case FIELD_DEPREXPENSES:
                {
                    d_DepreciationExpenses = p_dbl;
                }
                ;
                break;
            case FIELD_NETSALES:
                {
                    d_NetSales = p_dbl;
                }
                ;
                break;
            case FIELD_OPEXPENSES:
                {
                    d_OperatingExpenses = p_dbl;
                }
                ;
                break;
        }
        return null;
    }
}

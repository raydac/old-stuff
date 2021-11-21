package FinancialCalc.EvalModules;

import FinancialCalc.EvalModule;
import FinancialCalc.MenuActionListener;

public class QuickRatio extends EvalModule
{
    private double d_Funds;
    private double d_ShortTermInvestment;
    private double d_BillReceivable;
    private double d_CurrentLiability;

    private double d_Result;

    private static final int FIELD_FUNDS = 0;
    private static final int FIELD_SHORTTERMINV = 1;
    private static final int FIELD_BILLRCEIVABLE = 2;
    private static final int FIELD_CURLIABILITY = 3;

    public String getModuleName(MenuActionListener _listener)
    {
        return "Quick ratio";
    }

    public void beginTransaction()
    {
        d_Funds = 0d;
        d_ShortTermInvestment = 0d;
        d_BillReceivable = 0d;
        d_CurrentLiability = 0d;

        d_Result = 0d;
    }

    public void calculate()
    {
        d_Result = (d_Funds+d_ShortTermInvestment+d_BillReceivable) / d_CurrentLiability;
    }

    public String checkFields()
    {
        if (d_CurrentLiability == 0) return "Текущие обязательства не иогут быть 0";
        return null;
    }

    public int getFieldsNumber()
    {
        return 4;
    }

    public String getFieldValue(int _field)
    {
        switch(_field)
        {
            case FIELD_FUNDS : return convertDoubleToString(d_Funds);
            case FIELD_SHORTTERMINV : return convertDoubleToString(d_ShortTermInvestment);
            case FIELD_BILLRCEIVABLE : return convertDoubleToString(d_BillReceivable);
            case FIELD_CURLIABILITY : return convertDoubleToString(d_CurrentLiability);
        }

        return null;
    }

    public String getNameForField(int _field, MenuActionListener _listener)
    {
        switch(_field)
        {
            case FIELD_FUNDS : return "Ден.средства";
            case FIELD_SHORTTERMINV : return "Краткосрочные инвестиции";
            case FIELD_BILLRCEIVABLE : return "Дебит.задолженность";
            case FIELD_CURLIABILITY : return "Текущ.обязательства";
        }
        return null;
    }

    public String getNameForResult(int _result, MenuActionListener _listener)
    {
        return "Срочн. ликвидность";
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
            case FIELD_CURLIABILITY:
                {
                    d_CurrentLiability = p_dbl;
                    if (d_CurrentLiability==0d)
                    {
                        return "Текущие обязательства не могут быть 0";
                    }
                }
                ;
                break;
            case FIELD_FUNDS:
                {
                    d_Funds = p_dbl;
                }
                ;
                break;
            case FIELD_SHORTTERMINV:
                {
                    d_ShortTermInvestment = p_dbl;
                }
                ;
                break;
            case FIELD_BILLRCEIVABLE:
                {
                    d_BillReceivable = p_dbl;
                }
                ;
                break;
        }
        return null;
    }
}

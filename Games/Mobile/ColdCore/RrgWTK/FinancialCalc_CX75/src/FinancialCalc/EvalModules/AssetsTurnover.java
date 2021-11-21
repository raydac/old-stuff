package FinancialCalc.EvalModules;

import FinancialCalc.EvalModule;
import FinancialCalc.MenuActionListener;

public class AssetsTurnover extends EvalModule
{
    private double d_SalesVolume;
    private double d_SummAssets;

    private double d_Result;

    private static final int FIELD_SVOLUME = 0;
    private static final int FIELD_SUMMASSETS = 1;

    public String getModuleName(MenuActionListener _listener)
    {
        return "Assets turnover";
    }

    public void beginTransaction()
    {
        d_SalesVolume = 0d;
        d_SummAssets = 0d;

        d_Result = 0d;
    }

    public void calculate()
    {
        d_Result = d_SalesVolume / d_SummAssets;
    }

    public String checkFields()
    {
        if (d_SummAssets == 0) return "Суммарн. активы не должны быть 0";
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
            case FIELD_SUMMASSETS : return convertDoubleToString(d_SummAssets);
            case FIELD_SVOLUME : return convertDoubleToString(d_SalesVolume);
        }

        return null;
    }

    public String getNameForField(int _field, MenuActionListener _listener)
    {
        switch(_field)
        {
            case FIELD_SUMMASSETS : return "Суммарн.активы";
            case FIELD_SVOLUME: return "Объем продаж";
        }
        return null;
    }

    public String getNameForResult(int _result, MenuActionListener _listener)
    {
        return "Оборач.активов";
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
            case FIELD_SUMMASSETS:
                {
                    d_SummAssets = p_dbl;
                    if (d_SummAssets ==0d)
                    {
                        return "Суммарные активы не могут быть 0";
                    }
                }
                ;
                break;
            case FIELD_SVOLUME:
                {
                    d_SalesVolume = p_dbl;
                }
                ;
                break;
        }
        return null;
    }
}

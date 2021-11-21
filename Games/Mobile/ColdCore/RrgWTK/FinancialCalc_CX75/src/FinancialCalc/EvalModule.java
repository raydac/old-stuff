package FinancialCalc;

public abstract class EvalModule
{
    public abstract int getFieldsNumber();
    public abstract String getNameForField(int _field,MenuActionListener _listener);
    public abstract void beginTransaction();
    public abstract String setFieldValue(int _field,String _value);
    public abstract String getFieldValue(int _field);
    public abstract void calculate();
    public abstract String checkFields();
    public abstract int getResultsNumber();
    public abstract String getNameForResult(int _result,MenuActionListener _listener);
    public abstract String getResult(int _result);
    public abstract String getModuleName(MenuActionListener _listener);

    public String convertDoubleToString(double _value)
    {
        String s_str = Double.toString(_value);
        if (s_str.endsWith(".0"))
        {
            return s_str.substring(0,s_str.length()-2);
        }
        return s_str;
    }
}

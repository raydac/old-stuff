package com.igormaznitsa.Preprocessor.Formula;

import java.util.Vector;

public class FormulaStack extends Vector
{
    public void swap(int _indx0,int _indx1)
    {
        Object p_ak = elementAt(_indx0);
        setElementAt(elementAt(_indx1),_indx0);
        setElementAt(p_ak,_indx1);
    }

    /**
     * Проверяет есть ли два предыдущих числовых значения на стеке
     * @param _index
     * @return
     */
    public boolean isTwoPreviousItemsValues(int _index)
    {
        if (_index>=2)
        {
            Object p_obj1 = elementAt(_index-1);
            Object p_obj2 = elementAt(_index-2);
            if (p_obj1 instanceof Value && p_obj2 instanceof Value) return true;
            else
                return false;
        }
        else
            return false;
    }

    /**
     * Возвращает true если перед индексом в стеке имеется какое то значение
     * @param _index индекс
     * @return true если есть значение и false если нет
     */
    public boolean isOnePreviousItemValue(int _index)
    {
        if (_index>=1)
        {
            Object p_obj = elementAt(_index-1);
            if (p_obj instanceof Value) return true;
            return false;
        }
        else
            return false;
    }

    /**
     * Возвращает true если перед индексом в стеке имеется два каких то значения
     * @param _index индекс
     * @return true если есть значение и false если нет
     */
    public boolean isTwoPreviousItemValue(int _index)
    {
        if (_index>=2)
        {
            Object p_obj = elementAt(_index-1);
            Object p_obj2 = elementAt(_index-2);
            if (p_obj instanceof Value && p_obj2 instanceof Value) return true;
            return false;
        }
        else
            return false;
    }

    public void printFormulaStack()
    {
        System.out.println("Stack depth = "+size());    

        for(int li=0;li<size();li++)
        {
            Object p_obj = elementAt(li);
            System.out.println(p_obj.toString());
        }
    }
}

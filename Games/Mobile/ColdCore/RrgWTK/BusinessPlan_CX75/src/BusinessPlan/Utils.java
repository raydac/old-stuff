package BusinessPlan;

import java.util.Calendar;
import java.util.Date;

public class Utils
{
    private static Calendar p_calendar = Calendar.getInstance();

    public static boolean isToday(long _milliseconds)
    {
        if (_milliseconds<=0) return false;
        p_calendar.setTime(new Date());
        int i_year = p_calendar.get(Calendar.YEAR);
        int i_month = p_calendar.get(Calendar.MONTH);
        int i_day = p_calendar.get(Calendar.DAY_OF_MONTH);

        p_calendar.setTime(new Date(_milliseconds));
        if (p_calendar.get(Calendar.DAY_OF_MONTH)!=i_day) return false;
        if (p_calendar.get(Calendar.MONTH)!=i_month) return false;
        if (p_calendar.get(Calendar.YEAR)!=i_year) return false;
        return true;
    }

}

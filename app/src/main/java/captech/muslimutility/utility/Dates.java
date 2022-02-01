package captech.muslimutility.utility;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

import captech.muslimutility.R;

public class Dates {

    public static String islamicMonthName(Context context, int month) {
        switch (month) {
            case 0:
                return context.getString(R.string.month1);
            case 1:
                return context.getString(R.string.month2);
            case 2:
                return context.getString(R.string.month3);
            case 3:
                return context.getString(R.string.month4);
            case 4:
                return context.getString(R.string.month5);
            case 5:
                return context.getString(R.string.month6);
            case 6:
                return context.getString(R.string.month7);
            case 7:
                return context.getString(R.string.month8);
            case 8:
                return context.getString(R.string.month9);
            case 9:
                return context.getString(R.string.month10);
            case 10:
                return context.getString(R.string.month11);
            default:
                return context.getString(R.string.month12);
        }
    }

    public static String gregorianMonthName(Context context, int month) {
        switch (month) {
            case 0:
                return context.getString(R.string.month1g);
            case 1:
                return context.getString(R.string.month2g);
            case 2:
                return context.getString(R.string.month3g);
            case 3:
                return context.getString(R.string.month4g);
            case 4:
                return context.getString(R.string.month5g);
            case 5:
                return context.getString(R.string.month6g);
            case 6:
                return context.getString(R.string.month7g);
            case 7:
                return context.getString(R.string.month8g);
            case 8:
                return context.getString(R.string.month9g);
            case 9:
                return context.getString(R.string.month10g);
            case 10:
                return context.getString(R.string.month11g);
            default:
                return context.getString(R.string.month12g);
        }
    }

    public static String getHalfMonth(Context context, int month) {
        switch (month) {
            case 0:
                return "Jan";
            case 1:
                return "Feb";
            case 2:
                return "Mar";
            case 3:
                return "Apr";
            case 4:
                return "May";
            case 5:
                return "Jun";
            case 6:
                return "Jul";
            case 7:
                return "Aug";
            case 8:
                return "Sep";
            case 9:
                return "Oct";
            case 10:
                return "Nov";
            default:
                return "Dec";
        }
    }

    public static String weekDayName(Context context, int day) {
        switch (day) {
            case 0:
                return context.getString(R.string.day1);
            case 1:
                return context.getString(R.string.day2);
            case 2:
                return context.getString(R.string.day3);
            case 3:
                return context.getString(R.string.day4);
            case 4:
                return context.getString(R.string.day5);
            case 5:
                return context.getString(R.string.day6);
            default:
                return context.getString(R.string.day7);


        }
    }

    public static String getCurrentWeekDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM , dd");
        String[] myformat = sdf.format(new Date().getTime()).split(",");
        String week = myformat[0];
        return week;
    }

}

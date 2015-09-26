package de.mytfg.app.android.utils;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by t on 26.09.15.
 */
public class TimeUtils {

    public static String getDateStringShort(long timestamp) {
        String datetime;
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp * 1000);
        if (calendar.get(Calendar.DATE) == Calendar.getInstance().get(Calendar.DATE)) {
            datetime = DateFormat.format("HH:mm", calendar).toString();
        } else if (calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
            datetime = DateFormat.format("dd.MM.", calendar).toString();
        } else {
            datetime = DateFormat.format("dd.MM.yyyy", calendar).toString();
        }
        return datetime;
    }


    public static String getDateStringComplete(long timestamp) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp * 1000);
        return DateFormat.format("dd.MM.yyyy, HH:mm", calendar).toString();
    }
}

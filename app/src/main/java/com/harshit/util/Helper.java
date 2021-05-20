package com.harshit.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Helper {

    public static String getTime(String timeStamp) {
        Timestamp ts = new Timestamp(Long.parseLong(timeStamp));
        Date date = new Date(ts.getTime());
        // S is the millisecond
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM' 'HH:mm", Locale.ENGLISH);
        return  simpleDateFormat.format(date);

    }

}

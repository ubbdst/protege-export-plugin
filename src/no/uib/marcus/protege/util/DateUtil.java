package no.uib.marcus.protege.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Hemed on 16.03.2017.
 */
public class DateUtil {

    /**
     * Generate today date
     */
    public static String getCurrentDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String today = formatter.format(new Date());
        return today;
    }

    /**
     *  Generate today date based on the format provided
     *  @param format  a date format
     */
    public static String getCurrentDate(String format){
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String today = formatter.format(new Date());
        return today;
    }

}

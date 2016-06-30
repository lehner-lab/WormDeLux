package org.kuleuven.utilities;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateUtilities {
  public static long second = 1000;
  public static long minute = 60 * second;
  public static long hour = 60 * minute;
  public static long day = 24 * hour;
  public static long week = 7 * day;
  public static long year = 365 * day;
  
  public static long goBack(long time, int amount, int unit){
    if (unit == Calendar.DATE)
      return time-day;
    
    calendar.setTimeInMillis(time);
    int date = calendar.get(Calendar.DATE);
    int month = calendar.get(Calendar.MONTH);
    int year =  calendar.get(Calendar.YEAR);
    if (unit == Calendar.MONTH){      
      month--;
      if (month<0){
        month = 11;
        year--;
      }      
    } else if (unit == Calendar.YEAR)      
      year--;     
    
    calendar.set(year, month, date);
    return calendar.getTimeInMillis();
  }
  
  public static long goForward(long time, int amount, int unit){
    if (unit == Calendar.DATE)
      return time+day;
    
    calendar.setTimeInMillis(time);
    int date = calendar.get(Calendar.DATE);
    int month = calendar.get(Calendar.MONTH);
    int year =  calendar.get(Calendar.YEAR);
    if (unit == Calendar.MONTH){      
      month++;
      if (month>11){
        month = 0;
        year++;
      }      
    } else if (unit == Calendar.YEAR)      
      year++;     
    
    calendar.set(year, month, date);
    return calendar.getTimeInMillis();
  }
  
  private static Calendar calendar = new GregorianCalendar();
}

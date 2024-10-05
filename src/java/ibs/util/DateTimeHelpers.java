/*
 * Class: DateTimeHelpers.java
 */

// package:
package ibs.util;

// imports:
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;



/******************************************************************************
 * This class contains some helper methods for date + time manipulation. <BR/>
 *
 * @version     $Id: DateTimeHelpers.java,v 1.6 2010/11/10 12:42:08 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 20070724
 ******************************************************************************
 */
public abstract class DateTimeHelpers extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DateTimeHelpers.java,v 1.6 2010/11/10 12:42:08 btatzmann Exp $";


    /**
     * Number of milliseconds per hour. <BR/>
     * This value is used when converting date types, especially for getting
     * date values out of the database
     */
    private static final long MILLIS_PER_HOUR = 3600000;

    /**
     * Number of milliseconds per day. <BR/>
     * This value is used when converting date types, especially for getting
     * date values out of the database
     */
    private static final long MILLIS_PER_DAY = DateTimeHelpers.MILLIS_PER_HOUR * 24;

    /**
     * Offset between 1.1.1900 and 1.1.1970 in days. <BR/>
     * This value is used when converting date types, especially for getting
     * date values out of the database
     */
    private static final long CALENDAR_OFFSET = 25569;

    /**
     * Offset between 1.1.1900 and 1.1.1970 in milli seconds. <BR/>
     * This value is used when converting date types,
     */
    private static final long MILLIS_OFFSET = Long.parseLong ("2209161599875");

    /**
     * DateTimeFormat used to convert date/time to String and vice versa. <BR/>
     * Format: dd.mm.yy hh:mm
     */
    public static final SimpleDateFormat FORMAT_DATETIME =
        new SimpleDateFormat ("dd.MM.yyyy HH:mm");

    /**
     * DateFormat used to convert date to String and vice versa. <BR/>
     * Format: dd.mm.yy
     */
    public static final SimpleDateFormat FORMAT_DATE =
        new SimpleDateFormat ("dd.MM.yyyy");

    /**
     * TimeFormat used to convert time to String and vice versa. <BR/>
     * Format: hh:mm
     */
    public static final SimpleDateFormat FORMAT_TIME =
        new SimpleDateFormat ("HH:mm");


    /**
     * DateTimeFormat used to convert date/time to String and vice versa. <BR/>
     * Format: dd.mm.yyyy hh:mm
     */
    public static final SimpleDateFormat FORMATDEF_DATETIME =
        DateTimeHelpers.FORMAT_DATETIME;


    /**
     * DateTimeFormat used to convert date/time to String and vice versa. <BR/>
     * Format: dd.mm.yyyy
     */
    public static final SimpleDateFormat FORMATDEF_DATE =
        DateTimeHelpers.FORMAT_DATE;


    /**
     * Computation mode for date additions: calendar days. <BR/>
     */
    public static final int COMP_CALDAYS = 1;


    /**
     * Computation mode for date additions: first day after week end. <BR/>
     * If the day is on a week end get the next week day (Monday).
     */
    public static final int COMP_FIRSTDAYAFTERWEEKEND = 2;


    /**
     * Computation mode for date additions: week days. <BR/>
     * Week days are all days of a week except Saturday and Sunday.
     */
    public static final int COMP_WEEKDAYS = 3;


    /**
     * Computation mode for date additions: working days. <BR/>
     * Working days are all days except Saturday, Sunday and holidays.
     */
    public static final int COMP_WORKINGDAYS = 4;


    /**************************************************************************
     * Convert a Date object into a String value. <BR/>
     * If the format is <CODE>null</CODE> or <CODE>""</CODE> the method
     * {@link #dateTimeToString (Date) dateTimeToString} is called.
     *
     * @param   date    Date to be converted to a string representation.
     * @param   format  The format for the string.
     *
     * @return  String representation of the date/time value or
     *          <CODE>null</CODE> if the date is not valid.
     *
     * @see java.text.SimpleDateFormat
     */
    public static String dateTimeToString (Date date, String format)
    {
        SimpleDateFormat dateTimeFormat = null;

        // check if a date has been passed:
        if (date == null)               //  no valid date?
        {
            return null;
        } // if no valid date

        // check if the format is valid:
        if (format == null || format.length () == 0)
        {
            // use standard formatter
            return DateTimeHelpers.dateTimeToString (date);
        } // if

        dateTimeFormat = new SimpleDateFormat (format);

        // set timezone of date format - this is necessary due
        // to a bug in some JDK versions
        dateTimeFormat.setTimeZone (TimeZone.getDefault ());

        // convert the date to the required string:
        return dateTimeFormat.format (date);
    } // dateTimeToString


    /**************************************************************************
     * Convert a Date object into a String value. <BR/>
     *
     * @param   date    Date to be converted to a string representation.
     *
     * @return  String representation of the date/time value or
     *          <CODE>null</CODE> if the date is not valid.
     */
    public static String dateTimeToString (Date date)
    {
        // check if a date has been passed:
        if (date == null)               //  no valid date?
        {
            return null;
        } // if no valid date

        // set timezone of date format - this is necessary due
        // to a bug in some JDK versions
        DateTimeHelpers.FORMAT_DATETIME.setTimeZone (TimeZone.getDefault ());

        // format mm.dd.yyyy hh:mm:ss
        return DateTimeHelpers.FORMAT_DATETIME.format (date);
    } // dateTimeToString


    /**************************************************************************
     * Convert the date part of a Date object into a String value. <BR/>
     *
     * @param   date    Date to be converted to a string representation.
     *
     * @return  String representation of the date value or
     *          <CODE>null</CODE> if the date is not valid.
     */
    public static String dateToString (Date date)
    {
        // check if a date has been passed:
        if (date == null)               //  no valid date?
        {
            return null;
        } // if no valid date

        // set timezone of date format - this is necessary due
        // to a bug in some JDK versions
        DateTimeHelpers.FORMATDEF_DATE.setTimeZone (TimeZone.getDefault ());

        // format: dd.mm.yyyy
        return DateTimeHelpers.FORMATDEF_DATE.format (date);
    } // dateToString


    /**************************************************************************
     * Convert the time part of a Date object into a String value. <BR/>
     *
     * @param   date    Date to be converted to a string representation.
     *
     * @return  String representation of the time value or
     *          <CODE>null</CODE> if the time is not valid.
     */
    public static String timeToString (Date date)
    {
        // check if a date has been passed:
        if (date == null)               //  no valid date?
        {
            return null;
        } // if no valid date

        // set timezone of date format - this is necessary due
        // to a bug in some JDK versions
        DateTimeHelpers.FORMAT_TIME.setTimeZone (TimeZone.getDefault ());

        return DateTimeHelpers.FORMAT_TIME.format (date);
    } // timeToString


    /**************************************************************************
     * Get the several parts of a date out of a String representation. <BR/>
     * Not all values have to be set, e.g. '5.3.' is 5th day of 3rd month and
     * year is set automatically.
     * Supported formats: dd.mm.yy and dd.mm.yyyy
     *
     * @param   dateStr Date to be parsed.
     *
     * @return  Array containing the date values:
     *          {year, month day}.
     */
    public static int[] parseDateString (String dateStr)
    {
        int year;                       // date - year
        int month;                      // date - month
        int day;                        // date - day
        String dateStrLocal = dateStr;  // variable for local assignments

        // create GregorianCalendars with timezone and locale:
        GregorianCalendar cal1 = new GregorianCalendar ();

        // check if a value has been passed:
        if (dateStrLocal == null || dateStrLocal.length () == 0)
        {
            return null;
        } // if

        // drop leading and ending spaces:
        dateStrLocal = dateStrLocal.trim ();

/* KR a new calendar is automcaticaly initialized to the actual system time
        // create cal with todays date
        cal1.setTime (new Date ());
*/

        // set date values
        year = cal1.get (Calendar.YEAR);
        month = cal1.get (Calendar.MONTH);
        day = cal1.get (Calendar.DAY_OF_MONTH);

        // split string in date & time part:
        StringTokenizer t = new StringTokenizer (dateStrLocal, " ");

        // get tokens
        if (t.hasMoreTokens ())
        {
            dateStrLocal = t.nextToken ();
        } // if

        // define tokenizer:
        t = new StringTokenizer (dateStrLocal, ".");

        try
        {
            // split string and set date units
            if (t.hasMoreTokens ())
            {
                day = (new Integer (t.nextToken ())).intValue ();
            } // if
            if (t.hasMoreTokens ())
            {
                month = (new Integer (t.nextToken ())).intValue ();
            } // if
            if (t.hasMoreTokens ())
            {
                year = (new Integer (t.nextToken ())).intValue ();
            } // if
        } // try
        catch (NumberFormatException e)
        {
            // should throw exception
            // throw (new ParseException ());
            return null;
        } // catch

        // return the result:
        return new int[] {year, month, day};
    } // parseDateString


    /**************************************************************************
     * Get the several parts of a time value out of a String representation.
     * <BR/>
     * Supported format: hh:mm
     *
     * Remarks: Parsing of time had to be implemented, because JDK 1.1.3 time
     * parsing mechanism throws some unexpected exceptions and is not capable of
     * parsing a time string where the minutes or hours are zero (e.g. '9:00',
     * or '0:30').
     *
     * @param   timeStr Time string to be parsed.
     *
     * @return  Array containing the time values:
     *          {hours, minutes, seconds, milliseconds}.
     */
    public static int[] parseTimeString (String timeStr)
    {
        String timeStrLocal = timeStr;  // variable for local assignments
        // create GregorianCalendars with timezone and locale
        GregorianCalendar cal1 = new GregorianCalendar ();

        // check if a value has been passed:
        if (timeStrLocal == null || timeStrLocal.length () == 0)
        {
            return null;
        } // if

        // drop leading and ending spaces:
        timeStrLocal = timeStrLocal.trim ();

/* KR a new calendar is automcaticaly initialized to the actual system time
        // set current date in calendar
        cal1.setTime (new Date ());
*/

        // set time units
        int hour = cal1.get (Calendar.HOUR_OF_DAY);
        int min = cal1.get (Calendar.MINUTE);
        int sec = cal1.get (Calendar.SECOND);
        int msec = cal1.get (Calendar.MILLISECOND);

        // split string in date & time part:
        StringTokenizer t = new StringTokenizer (timeStrLocal, " ");

        // get tokens
        if (t.hasMoreTokens ())
        {
            timeStrLocal = t.nextToken ();
        } // if
        if (t.hasMoreTokens ())
        {
            timeStrLocal = t.nextToken ();
        } // if

        // define tokenizer
        t = new StringTokenizer (timeStrLocal, ":,");

        try
        {
            // split string and set time units
            if (t.hasMoreTokens ())
            {
                hour = (new Integer (t.nextToken ())).intValue ();
                min = 0;
                sec = 0;
                msec = 0;
            } // if
            if (t.hasMoreTokens ())
            {
                min = (new Integer (t.nextToken ())).intValue ();
            } // if
            if (t.hasMoreTokens ())
            {
                sec = (new Integer (t.nextToken ())).intValue ();
            } // if
            if (t.hasMoreTokens ())
            {
                msec = (new Integer (t.nextToken ())).intValue ();
            } // if

            // check if time units are valid
            if (hour < 0 || hour > 23)
            {
                throw new NumberFormatException ();
            } // if
            if (min < 0 || min > 59)
            {
                throw new NumberFormatException ();
            } // if
            if (sec < 0 || sec > 59)
            {
                throw new NumberFormatException ();
            } // if
            if (msec < 0 || msec > 999)
            {
                throw new NumberFormatException ();
            } // if
        } // try
        catch (NumberFormatException e)
        {
            // should throw exception
            // throw (new ParseException ());
            return null;
        } // catch

        // return the result:
        return new int[] {hour, min, sec, msec};
    } // parseTimeString


    /**************************************************************************
     * Convert a String value into a Date object. <BR/>
     * Splits date-time in a date part and a time part and uses the according
     * methods to create two objects of type date where the according values
     * are set - after that one Date object is created out of the calculated
     * date and time part Date objects.
     *
     * @param   dateStr Date/Time to be converted to a Date representation.
     *
     * @return  Date representation of the date/time value or null if the value
     *          is not the correct representation of a date/time value.
     */
    public static Date stringToDateTime (String dateStr)
    {
        String dateStrLocal = dateStr;  // variable for local assignments
        Date date1;
        Date date2;

        // define date string
        String datePart = new String ("");
        String timePart = new String ("");

        // check if a value has been passed:
        if (dateStrLocal == null || dateStrLocal.length () == 0)
        {
            return null;
        } // if

        // drop leading and ending spaces:
        dateStrLocal = dateStrLocal.trim ();

        // split string in date & time part:
        StringTokenizer t = new StringTokenizer (dateStrLocal, " ");

        // get tokens
        if (t.hasMoreTokens ())
        {
            datePart = t.nextToken ();
        } // if
        if (t.hasMoreTokens ())
        {
            timePart = t.nextToken ();
        } // if

        // now check date part -> get parsed date in date1
        if ((date1 = DateTimeHelpers.stringToDate (datePart)) == null)
        {
            // should throw exception
            // throw (new ParseException ());
            return null;                // error in parsing
        } // if

        // now check time part -> get parsed date in date2
        if ((date2 = DateTimeHelpers.stringToTime (timePart)) == null)
        {
            // should throw exception
            // throw (new ParseException ());
            return null;        // error in parsing
        } // if

        // now combine date and time part in one date object:
        // create GregorianCalendars with timezone and locale
        GregorianCalendar cal1 = new GregorianCalendar ();
        GregorianCalendar cal2 = new GregorianCalendar ();
        GregorianCalendar cal3 = new GregorianCalendar ();

        // set date/time parts in calendars
        cal1.setTime (date1);
        cal2.setTime (date2);

        // now set date of cal1 and time of cal2 in cal3
        cal3.set (Calendar.YEAR, cal1.get (Calendar.YEAR));
        cal3.set (Calendar.MONTH, cal1.get (Calendar.MONTH));
        cal3.set (Calendar.DAY_OF_MONTH, cal1.get (Calendar.DAY_OF_MONTH));
        cal3.set (Calendar.HOUR_OF_DAY, cal2.get (Calendar.HOUR_OF_DAY));
        cal3.set (Calendar.MINUTE, cal2.get (Calendar.MINUTE));
        cal3.set (Calendar.SECOND, cal2.get (Calendar.SECOND));

        // return combined date
        return cal3.getTime ();
    } // stringToDateTime


    /**************************************************************************
     * Convert a String value into the date part of a Date object. <BR/>
     * Not all values must be set, e.g. '5.3.' is 5th day of 3rd month and
     * year is set automatically.
     * Supported formats: dd.mm.yy and dd.mm.yyyy
     *
     * @param   dateStr Date to be converted to a Date representation.
     *
     * @return  Date representation of the date value or null if the value
     *          is not the correct representation of a date value.
     */
    public static Date stringToDate (String dateStr)
    {
        Date date = new Date ();        // return value
        int[] dateParts;                // the date parts
        String dateStrLocal = dateStr;  // variable for local assignments

        // parse the date:
        dateParts = DateTimeHelpers.parseDateString (dateStrLocal);

        // check if parsing was successful:
        if (dateParts == null)
        {
            return null;
        } // if

        // redefine date string
        dateStrLocal = dateParts[2] + "." + dateParts[1] + "." + dateParts[0];

        // try to parse string for format dd.mm.yyyy
        try
        {
            // set timezone of date format - this is necessary due
            // to a bug in some JDK versions
            DateTimeHelpers.FORMATDEF_DATE.setTimeZone (TimeZone.getDefault ());

            // now parse
            date = DateTimeHelpers.FORMATDEF_DATE.parse (dateStrLocal);
        } // try
        catch (StringIndexOutOfBoundsException e1)
        {
            // occurs sometimes
            // try to parse string for shorter format dd.mm.yy
            try
            {
                // set timezone of date format - this is necessary due
                // to a bug in some JDK versions
                DateTimeHelpers.FORMAT_DATE.setTimeZone (TimeZone.getDefault ());

                // now parse
                date = DateTimeHelpers.FORMAT_DATE.parse (dateStrLocal);
            } // try
            catch (ParseException e2)
            {
                // should throw exception
                // throw (new ParseException ());
                date = null;
            } // catch
        } // catch StringIndexOutOfBoundsException
        catch (ParseException e3)
        {
            // should throw exception
            // throw (new ParseException ());
            date = null;
        } // catch ParseException

        // exit
        return date;
    } // stringToDate


    /**************************************************************************
     * Convert a String value into the time part of a Date object. <BR/>
     * Supported format: hh:mm
     *
     * Remarks: Parsing of time had to be implemented, because JDK 1.1.3 time
     * parsing mechanism throws some unexpected exceptions and is not capable of
     * parsing a time string where the minutes or hours are zero (e.g. '9:00',
     * or '0:30').
     *
     * @param   timeStr Time string to be converted to a Date representation.
     *
     * @return  Date representation of the time value or null if the value
     *          is not the correct representation of a time value.
     */
    public static Date stringToTime (String timeStr)
    {
        int[] timeParts;                // the time parts
        GregorianCalendar cal2 = null;  // calendar for getting the time

        // parse the time:
        timeParts = DateTimeHelpers.parseTimeString (timeStr);

        // check if parsing was successful:
        if (timeParts != null)
        {
            // create GregorianCalendar with timezone and locale
            cal2 = new GregorianCalendar ();
            // now set time values in calendar
            cal2.set (Calendar.HOUR_OF_DAY, timeParts[0]);
            cal2.set (Calendar.MINUTE, timeParts[1]);
            cal2.set (Calendar.SECOND, timeParts[2]);
            cal2.set (Calendar.MILLISECOND, timeParts[3]);
        } // if
        else
        {
            return null;
        } // else

        // return date with changed time values
        return cal2.getTime ();
    } // stringToTime


    /**************************************************************************
     * Transforms given a double-date value (xxxxx.xxxxx), for example used in
     * Variant, to a Java Date value. <BR/>
     *
     * @param doubleVal  the given date in form of a double
     *
     * @return  the converted Date value
     */
    public static Date doubleToDate (double doubleVal)
    {
        // convert given variant date (double) to java date (Date):

        // get default time zone of this computer
        // get default timezone, calendar
        TimeZone timeZone = TimeZone.getDefault ();

        // get milliseconds since beginning of computer time:
        long millis = (new Double ((doubleVal - DateTimeHelpers.CALENDAR_OFFSET) *
            DateTimeHelpers.MILLIS_PER_DAY)).longValue ();

        // convert local time into UTC:
        // precompute date
        Date helpDate = new Date (millis);

        // daylight saving time?
        if (timeZone.useDaylightTime () && timeZone.inDaylightTime (helpDate))
        {
            // subtract one hour
            millis -= DateTimeHelpers.MILLIS_PER_HOUR;
        } // if

        // subtract offset to GMT
        millis -= timeZone.getRawOffset ();
        // set return value
        helpDate = new Date (millis);

        // return date
        return helpDate;
    } // doubleToDate


    /**************************************************************************
     * Transforms given date to a double value (xxxxx.xxxxx). <BR/>
     * The Variant class needs to get date in this form.
     *
     * @param date  the given date
     *
     * @return  the converted double value
     */
    public static double dateToDouble (Date date)
    {
        // calculate given date to double value needed for variant date:

        // define locales
        // get default timezone, calendar
        TimeZone tz = TimeZone.getDefault ();
        GregorianCalendar cal = new GregorianCalendar ();

        // convert date to millis since 1970
        long millis = date.getTime ();

        // set in calendar
        cal.setTime (date);

        // calculate offset from GMT - incl. daylight saving time
        int offsetGMT = tz.getOffset (cal.get (Calendar.ERA), cal.get (Calendar.YEAR),
                                      cal.get (Calendar.MONTH), cal.get (Calendar.DAY_OF_MONTH),
                                      cal.get (Calendar.DAY_OF_WEEK), cal.get (Calendar.MILLISECOND));

        // add offset, because the java base date (Date) is
        // 1.1.1970 and the variants (double) base date is
        // 1.1.1900 - offset is difference of these two
        // dates in millis
        double d = ((double) millis + (double) DateTimeHelpers.MILLIS_OFFSET) + offsetGMT;

        // convert millis to double value
        d /= DateTimeHelpers.MILLIS_PER_DAY;
        // return converted value
        return d;
    } // dateToDouble


    /**************************************************************************
     * Compare a date with the current date. <BR/>
     *
     * @param   date    The date to be compared with the current date.
     *
     * @return  if date > currentDate returnvalue > 0
     *          if date = currentDate returnvalue = 0
     *          if date < currentDate returnvalue < 0
     *
     * @see #stringToDate (String)
     * @see #compareDates (Date, Date)
     */
    public static long compareToCurrentDate (Date date)
    {
        // create the current date, compare the dates and return the result:
        return DateTimeHelpers.compareDates (date, new Date ());
    } // compareToCurrentDate


    /**************************************************************************
     * Compare a date with the current date. <BR/>
     * First the string is converted into a Date and then it is compared to
     * the current date.
     *
     * @param   dateStr The date to be compared with the current date.
     *
     * @return  if date > currentDate returnvalue > 0
     *          if date = currentDate returnvalue = 0
     *          if date < currentDate returnvalue < 0
     *
     * @see #stringToDate (String)
     * @see #compareDates (Date, Date)
     */
    public static long compareToCurrentDate (String dateStr)
    {
        // convert the date string to a Date object, create the current date,
        // compare the dates and return the result:
        return DateTimeHelpers.compareDates (DateTimeHelpers.stringToDate (dateStr), new Date ());
    } // compareToCurrentDate


    /**************************************************************************
     * Compare two Dates (year, month, day). <BR/>
     *
     * @param   date1   Date to compare with an other Date
     * @param   date2   Date to compare with an other Date
     *
     * @return  if Date1 > Date2 returnvalue > 0
     *          if Date1 = Date2 returnvalue = 0
     *          if Date1 < Date2 returnvalue < 0
     */
    public static long compareDates (Date date1, Date date2)
    {
        if (date1 == null)
        {
            return Integer.MIN_VALUE;
        } // if
        if (date2 == null)
        {
            return Integer.MAX_VALUE;
        } // if

        // create calendars - use default timezone, locale
        GregorianCalendar cal1 = new GregorianCalendar ();
        GregorianCalendar cal2 = new GregorianCalendar ();

        // set dates in calendars:
        cal1.setTime (date1);
        cal2.setTime (date2);
        // ensure that the parts of a day are equal:
        cal1.set (Calendar.HOUR_OF_DAY, 0);
        cal1.set (Calendar.MINUTE, 0);
        cal1.set (Calendar.SECOND, 0);
        cal1.set (Calendar.MILLISECOND, 0);
        cal2.set (Calendar.HOUR_OF_DAY, 0);
        cal2.set (Calendar.MINUTE, 0);
        cal2.set (Calendar.SECOND, 0);
        cal2.set (Calendar.MILLISECOND, 0);

        // return difference in milliseconds:
        return (cal1.getTime ()).getTime () - (cal2.getTime ()).getTime ();
    } // compareDates


    /**************************************************************************
     * Compare the time parts of two Dates (hours, minutes and seconds). <BR/>
     *
     * @param   date1   Date to compare with an other Date
     * @param   date2   Date to compare with an other Date
     *
     * @return  if Date1 > Date2 returnvalue > 0
     *          if Date1 = Date2 returnvalue = 0
     *          if Date1 < Date2 returnvalue < 0
     */
    public static long compareTimes (Date date1, Date date2)
    {
        if (date1 == null)
        {
            return Integer.MIN_VALUE;
        } // if
        if (date2 == null)
        {
            return Integer.MAX_VALUE;
        } // if

        // create calendars - use default timezone, locale
        GregorianCalendar cal1 = new GregorianCalendar ();
        GregorianCalendar cal2 = new GregorianCalendar ();

        // set dates in calendars
        cal1.setTime (date1);
        cal2.setTime (date2);
        // ensure that the dates and milliseconds are equal:
        cal1.set (2000, 1, 1);
        cal1.set (Calendar.MILLISECOND, 0);
        cal2.set (2000, 1, 1);
        cal2.set (Calendar.MILLISECOND, 0);

        // return difference in milliseconds
        return (cal1.getTime ()).getTime () - (cal2.getTime ()).getTime ();
    } // compareTimes


    /**************************************************************************
     * Compare two Dates including hours, minutes and seconds. <BR/>
     * Milliseconds are not taken into regards for this comparison.
     * To compare dates down to milliseconds the following can be used:
     * <CODE>date1.getTime () - date2.getTime ()</CODE>.
     *
     * @param   date1   Date to compare with an other Date
     * @param   date2   Date to compare with an other Date
     *
     * @return  if Date1 > Date2 returnvalue > 0
     *          if Date1 = Date2 returnvalue = 0
     *          if Date1 < Date2 returnvalue < 0
     */
    public static long compareDateTimes (Date date1, Date date2)
    {
        if (date1 == null)
        {
            return Integer.MIN_VALUE;
        } // if
        if (date2 == null)
        {
            return Integer.MAX_VALUE;
        } // if

        // create calendars - use default timezone, locale
        GregorianCalendar cal1 = new GregorianCalendar ();
        GregorianCalendar cal2 = new GregorianCalendar ();
        // set dates in calendars
        cal1.setTime (date1);
        cal2.setTime (date2);

        // calculate difference (including milliseconds)

        // now compare to dates - do not compare millisecs, stop with seconds
        // compare sec -> min -> hour -> day -> month -> year
        if (cal1.get (Calendar.SECOND) == cal2.get (Calendar.SECOND) &&
            cal1.get (Calendar.MINUTE) == cal2.get (Calendar.MINUTE) &&
            cal1.get (Calendar.HOUR_OF_DAY) == cal2.get (Calendar.HOUR_OF_DAY) &&
            cal1.get (Calendar.DATE) == cal2.get (Calendar.DATE) &&
            cal1.get (Calendar.MONTH) == cal2.get (Calendar.MONTH) &&
            cal1.get (Calendar.YEAR) == cal2.get (Calendar.YEAR))
        {
            // dates equal (down to seconds)
            return 0;
        } // if

        // otherwise - return difference in milliseconds
        return (cal1.getTime ()).getTime () - (cal2.getTime ()).getTime ();
    } // compareDateTimes


    /**************************************************************************
     * Creates current date as absolute date value (GMT+0). Should be used
     * instead of "new Date ()". System works only with absolute dates, e.g.
     * database date-values are absolute, queries on database date values use
     * absolute values (means that no timezone-conversion happens). <BR/>
     *
     * @return  the current absolute date
     */
    public static Date getCurAbsDate ()
    {
        return new Date ();

/*
        // workaround - for details see timezone-paper

        // compute absolute right time: all time-relevant aspects in this
        // system are absolute (GMT+0)
        // date values in database have to be valid for current timezone

        // create default calendar, timezone
        GregorianCalendar cal = new GregorianCalendar ();
        TimeZone tz = TimeZone.getDefault ();

        // get current time
        Date d = cal.getTime ();

        // add timezone offset
        long l = (cal.getTime ()).getTime ()
        - cal.get (GregorianCalendar.ZONE_OFFSET);

        // if in daylight-saving-time of current timezone: add dst-offset
        if (tz.useDaylightTime () && tz.inDaylightTime (d))
        l += cal.get (GregorianCalendar.DST_OFFSET);

        // return converted date
        Date retVal = new Date ();
        retVal.setTime (l);

        return retVal;
*/
    } // getCurAbsDate


    /**************************************************************************
     * Create a timestamp string with the actual date and time using the
     * format "yyyyMMddHHmmssSSS". <BR/>
     *
     * @return  The timestamp
     */
    public static String getTimestamp ()
    {
        return DateTimeHelpers.dateTimeToString (new Date (), "yyyyMMddHHmmssSSS");
    } // getTimestamp

    /**************************************************************************
     * Get the additional days. <BR/>
     *
     * @param   startDate   Date to be checked.
     * @param   daysToAdd   Number of days to be added.
     *
     * @return  The number of calendar day(s) from the given date to the next
     *          working day.
     */
    public static final int getDaysToNextWorkingday (Date startDate,
                                                     int daysToAdd)
    {
        // compute number of week end days in the complete weeks:
        int additionalDays = 0;

        // create a new calendar object:
        GregorianCalendar cal = new GregorianCalendar ();

        // set calendar to current date:
        cal.setTime (startDate);

        // get weekday of new date:
        int weekday = cal.get (Calendar.DAY_OF_WEEK);

        // check the weekday of the current date
        if (weekday == Calendar.SATURDAY)
        {
            // the next working day is in two days:
            additionalDays += 2;
        } // if
        else if (weekday == Calendar.SUNDAY)
        {
            // the next working day is the next day:
            additionalDays++;
        } // if

        // return the result:
        return additionalDays;
    } // getDaysToNextWorkingday


    /**************************************************************************
     * Get the additional days to the next working day date. <BR/>
     *
     * @param   startDate   Date to be checked.
     * @param   daysToAdd   Number of days to be added.
     *
     * @return  The number of additional calendar day(s) from the given date to
     *          the next working day.
     */
    public static final int getAdditionalDaysToAdd (Date startDate,
                                                    int daysToAdd)
    {
        // compute number of week end days in the complete weeks:
        int additionalDays = 2 * (daysToAdd / 5);
        // additional days for Saturday:
        int saturdayAddOn = 0;
        // additional days for Sunday:
        int sundayAddOn = 0;
        // compute rest days:
        int restDaysToAdd = daysToAdd % 5;

        // create a new calendar object:
        GregorianCalendar cal = new GregorianCalendar ();

        // set calendar to current date:
        cal.setTime (startDate);

        // get weekday of new date:
        int weekday = cal.get (Calendar.DAY_OF_WEEK);

        // check the weekday of the current date:
        if (restDaysToAdd >= 0)
        {
            saturdayAddOn = -1;
            sundayAddOn = -2;
        } // if
        else
        {
            saturdayAddOn = +2;
            sundayAddOn = +1;
        } // if

        // compute equivalent working day:
        if (weekday == Calendar.SATURDAY)
        {
            additionalDays += saturdayAddOn;
            weekday += saturdayAddOn;
        } // if
        else if (weekday == Calendar.SUNDAY)
        {
            additionalDays += sundayAddOn;
            weekday += sundayAddOn;
        } // if

        // ensure that there is a valid value for the week day and add the
        // rest days to be added:
        weekday = (weekday + 7) % 7 + restDaysToAdd;

        // check if we have again a weekend day or a weekend between start and
        // new day:
        if (restDaysToAdd >= 0 && weekday >= 7)
        {
            // the next working day is in two days:
            additionalDays += 2;
            weekday += 2;
        } // if
        else if (restDaysToAdd <= 0 && weekday <= 1)
        {
            // the next working day is two days before:
            additionalDays -= 2;
            weekday -= 2;
        } // if

        // return the result:
        return additionalDays;
    } // getAdditionalDaysToAdd


    /**************************************************************************
     * Adds some days to a date. <BR/>
     * The daysToAdd must be in days.
     *
     * @param   baseDate        The baseDate to add the days.
     * @param   daysToAdd          The days to be added.
     * @param    computationMode    Mode for computations.
     *                             Must be one of {@link #COMP_CALDAYS},
     *                             {@link #COMP_FIRSTDAYAFTERWEEKEND},
     *                             {@link #COMP_WEEKDAYS},
     *                             {@link #COMP_WORKINGDAYS}.
     *
     * @return  The calculated date or
     *          <CODE>null</CODE> if the base date was <CODE>null</CODE> or
     *          the addition could not have been completed.
     */
    public static final Date addDaysToDate (Date baseDate, int daysToAdd,
            int computationMode)
    {
        // check if the date is valid:
        if (baseDate != null)
        {
            int additionalDays = daysToAdd;

            // check mode for computation:
            // (for COMP_CALDAYS nothing additional has to be done)
            switch (computationMode)
            {
                case DateTimeHelpers.COMP_WEEKDAYS:
                case DateTimeHelpers.COMP_WORKINGDAYS:
                    additionalDays += DateTimeHelpers.getAdditionalDaysToAdd (
                        baseDate, additionalDays);
                    break;
                default:
                    // nothing to do
            } // switch computationMode

            // convert the daysToAdd in milliseconds and add the result to the date:
            return new Date (baseDate.getTime () +
                additionalDays * DateTimeHelpers.MILLIS_PER_DAY);
        } // if

        // return error value:
        return null;
    } // addDateDays
    

    /**************************************************************************
     * Compute the number of days from now to the given date. <BR/>
     *
     * @param   dateStr   Date to be checked.
     *
     * @return  The number of calendar day(s) from now to the given date.
     * @throws ParseException 
     */
    public static final String getDaysToDate (String dateStr)
    {
        String retValue = "";
        
        try
        {
            Date date = DateTimeHelpers.FORMATDEF_DATE.parse (dateStr);
            Calendar now = Calendar.getInstance ();
            now.set (Calendar.HOUR_OF_DAY, 0);
            now.set (Calendar.MINUTE, 0);
            now.set (Calendar.SECOND, 0);
            
            double deltaMilli = date.getTime () - now.getTimeInMillis ();
            double days = Math.ceil ((deltaMilli / (60*60*24)) / 1000);

            // return the result:
            retValue = new Integer ((int) days).toString ();
        }
        catch (ParseException e)
        {
            // nothing to do
        }
        
        return retValue;
    } // getDaysToDate
} // class DateTimeHelpers

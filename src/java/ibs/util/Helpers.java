/*
 * Class: Helpers.java
 */

// package:
package ibs.util;

// imports:
import ibs.bo.BOMessages;
import ibs.bo.OID;
import ibs.di.DIConstants;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.ml.MultilingualTextProvider;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;



/******************************************************************************
 * This class contains some helper methods. <BR/>
 *
 * @version     $Id: Helpers.java,v 1.61 2013/01/17 15:22:38 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 980329
 ******************************************************************************
 */
public abstract class Helpers extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Helpers.java,v 1.61 2013/01/17 15:22:38 btatzmann Exp $";


    /**************************************************************************
     * Returns the hexadecimal string representation of an integer. <BR/>
     *
     * @param   val         Integer value to be converted to a string
     *                      representation.
     *
     * @return  String containing the hexadecimal representation of the byte
     *          value ("0xhhhhhhhh").
     */
    public static String intToString (int val)
    {
        return Helpers.intToString (val, true);
    } // intToString


    /**************************************************************************
     * Returns the hexadecimal string representation of an integer. <BR/>
     *
     * @param   val         Integer value to be converted to a string
     *                      representation.
     * @param   addPrefix   Add the hexadecimal prefix?
     *
     * @return  String containing the hexadecimal representation of the byte
     *          value ("0xhhhhhhhh").
     */
    public static String intToString (int val, boolean addPrefix)
    {
        return (addPrefix ? UtilConstants.NUM_START_HEX : "") +
            Helpers.byteToString ((byte) (val / 0x01000000)) +
            Helpers.byteToString ((byte) (val / 0x00010000 % 0x0100)) +
            Helpers.byteToString ((byte) (val / 0x00000100 % 0x0100)) +
            Helpers.byteToString ((byte) (val % 0x0100));
    } // intToString


    /***************************************************************************
     * Returns the hexadecimal string representation of a byte. <BR/>
     *
     * @param val Byte value to be converted to a string representation.
     *
     * @return String containing the hexadecimal representation of the byte
     *         value.
     */
    public static String byteToString (byte val)
    {
        int intVal = (256 + val) % 256; // convert to positive integer
        int hiVal = intVal / 16;        // higher 4 bits of value
        int loVal = intVal - hiVal * 16; // lower 4 bits of value

        return "" + (char) (hiVal + 48 + (hiVal / 10) * 7) +
                (char) (loVal + 48 + (loVal / 10) * 7);
    } // byteToString


    /**************************************************************************
     * Returns the char representation of a byte. <BR/>
     *
     * @param   val         Byte value to be converted to a string
     *                      representation.
     *
     * @return  char containing the hexadecimal representation of the lower byte
     *          value.
     */
    public static char byteToHexDigit (byte val)
    {
        int loVal = val % 16;           // lower 4 bits of value

        return (char) (loVal + 48 + (loVal / 10) * 7);
    } // byteToHexDigit



    /**************************************************************************
     * Converts a hexadecimal string to a byte value. <BR/>
     *
     * @param   valStr      String containing the hexadecimal representation of
     *                      a byte.
     *
     * @return  Byte value converted from the string.
     */
    public static byte stringToByte (String valStr)
    {
        byte hiVal = (byte) (valStr.charAt (0) - 48 - (valStr.charAt (0) / 64) * 7 -
                             (valStr.charAt (0) / 96) * 32);
        // higher 4 bits of value
        byte loVal = (byte) (valStr.charAt (1) - 48 - (valStr.charAt (1) / 64) * 7 -
                             (valStr.charAt (1) / 96) * 32);
        // lower 4 bits of value
        return (byte) (hiVal * 16 + loVal);
    } // stringToByte


    /**************************************************************************
     * Replace all occurrences of one String within another one. <BR/>
     * The original string is not changed.
     *
     * @param   majorStr    Major string containing the tags to be replaced.
     * @param   oldStr      String to be replaced.
     * @param   newStr      String which shall replace the oldStr.
     *
     * @return  String in which all occurrences of oldStr are replaced by
     *          newStr. In case any of the parameters is <CODE>null</CODE>
     *          the majorStr will be returned.
     *
     * @deprecated This method was replaced by {@link StringHelpers#replace(String, String, String)}
     */
    public static String replace (String majorStr, String oldStr, String newStr)
    {
        // call common method:
        return StringHelpers.replace (majorStr, oldStr, newStr);
    } // replace


    /**************************************************************************
     * Replace all occurrences of string surrounded by a String pair within
     * another string. <BR/>
     * All parts of the original string starting with <CODE>oldBeginStr</CODE>
     * and ending with <CODE>oldEndStr</CODE> are replaced with the replacement
     * string. <CODE>oldBeginStr</CODE> and <CODE>oldEndStr</CODE> are part of
     * the replaced string. <BR/>
     * A new String object is created. The original string is not changed.
     *
     * @param   majorStr    Major string containing the tags to be replaced.
     * @param   oldBeginStr Begin of string to be replaced.
     * @param   oldEndStr   End of string to be replaced.
     * @param   newStr      String which shall replace the oldStr.
     *
     * @return  String in which all occurrences of oldStr are replaced by
     *          newStr. In case any of the parameters is <CODE>null</CODE>
     *          the majorStr will be returned.
     *
     * @deprecated This method was replaced by {@link StringHelpers#replace(String, String, String, String)}
     */
    public static String replace (String majorStr, String oldBeginStr,
                                  String oldEndStr, String newStr)
    {
        // call common method:
        return StringHelpers.replace (majorStr, oldBeginStr, oldEndStr, newStr);
    } // replace


    /**************************************************************************
     * Replace all occurrences of one String pair within another string. <BR/>
     * All occurrencies of the string pair <CODE>oldBeginStr</CODE> and
     * <CODE>oldEndStr</CODE> are replaced with the replacement strings. The
     * String part between is left unchanged. <BR/>
     * A new String object is created. The original string is not changed.
     *
     * @param   majorStr    Major string containing the tags to be replaced.
     * @param   oldBeginStr Begin of string to be replaced.
     * @param   oldEndStr   End of string to be replaced.
     * @param   newBeginStr String which shall replace the oldBeginStr.
     * @param   newEndStr   String which shall replace the oldEndStr.
     *
     * @return  String in which all occurrences of oldStr are replaced by
     *          newStr. In case any of the parameters is <CODE>null</CODE>
     *          the majorStr will be returned.
     *
     * @deprecated This method was replaced by {@link StringHelpers#replace(String, String, String, String, String)}
     */
    public static String replace (String majorStr,
                                  String oldBeginStr,
                                  String oldEndStr,
                                  String newBeginStr,
                                  String newEndStr)
    {
        // call common method:
        return StringHelpers.replace (majorStr, oldBeginStr, oldEndStr, newBeginStr, newEndStr);
    } // replace


    /**************************************************************************
     * Replace all occurrences of one String within another one. <BR/>
     * The original string is not changed.
     *
     * @param   majorStr    Major string containing the tags to be replaced.
     * @param   oldStr      String to be replaced.
     * @param   newStr      String which shall replace the oldStr.
     *
     * @return  String in which all occurrences of oldStr are replaced by
     *          newStr.
     *
     * @deprecated This method was replaced by {@link StringHelpers#replace(StringBuffer, String, String)}
     */
    public static StringBuffer replace (StringBuffer majorStr, String oldStr,
                                        String newStr)
    {
        // call common method:
        return StringHelpers.replace (majorStr, oldStr, newStr);
    } // replace


    /**************************************************************************
     * Substitute characters within in a string. <BR/>
     * This method replaces all occurrences of characters of the first array
     * within the string with the corresponding characters of the second array.
     * <BR/>
     * So both arrays must have the same size.
     *
     * @param   str         The string to replace the characters in.
     * @param   oldChars    The characters to be replaced.
     * @param   newChars    The replacement characters.
     *
     * @return  The string with substituted characters.
     *          <CODE>null</CODE> if there occurred an error.
     *
     * @deprecated This method was replaced by {@link StringHelpers#replaceChars(String, char[], char[])}
     */
    public static final String replaceChars (String str,
                                             char[] oldChars,
                                             char[] newChars)
    {
        // call common method:
        return StringHelpers.replaceChars (str, oldChars, newChars);
    } // replaceChars


    /**************************************************************************
     * Replace all occurrences of strings within in a string. <BR/>
     * This method replaces all occurrences of strings of the first array
     * within the string with the corresponding strings of the second array.
     * <BR/>
     * So both arrays must have the same size. <BR/>
     * The original string is not changed.
     *
     * @param   majorStr    Major string containing the strings to be replaced.
     * @param   oldStr      Strings to be replaced.
     * @param   newStr      Strings which shall replace the oldStr.
     *
     * @return  String in which all occurrences of oldStr are replaced by
     *          newStr. In case any of the parameters is <CODE>null</CODE>
     *          the majorStr will be returned.
     *          <CODE>null</CODE> if there occurred an error.
     *
     * @deprecated This method was replaced by {@link StringHelpers#replaceStrings(String, String[], String[])}
     */
    public static String replaceStrings (String majorStr,
                                         String[] oldStr,
                                         String[] newStr)
    {
        // call common method:
        return StringHelpers.replaceStrings (majorStr, oldStr, newStr);
    } // replace


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
     *
     * @deprecated This method was replaced by {@link DateTimeHelpers#dateTimeToString(Date, String)}
     */
    public static String dateTimeToString (Date date, String format)
    {
        // call common method:
        return DateTimeHelpers.dateTimeToString (date, format);
    } // dateTimeToString


    /**************************************************************************
     * Convert a Date object into a String value. <BR/>
     *
     * @param   date    Date to be converted to a string representation.
     *
     * @return  String representation of the date/time value or
     *          <CODE>null</CODE> if the date is not valid.
     *
     * @deprecated This method was replaced by {@link DateTimeHelpers#dateTimeToString(Date)}
     */
    public static String dateTimeToString (Date date)
    {
        // call common method:
        return DateTimeHelpers.dateTimeToString (date);
    } // dateTimeToString


    /**************************************************************************
     * Convert the date part of a Date object into a String value. <BR/>
     *
     * @param   date    Date to be converted to a string representation.
     *
     * @return  String representation of the date value or
     *          <CODE>null</CODE> if the date is not valid.
     *
     * @deprecated This method was replaced by {@link DateTimeHelpers#dateToString(Date)}
     */
    public static String dateToString (Date date)
    {
        // call common method:
        return DateTimeHelpers.dateToString (date);
    } // dateToString


    /**************************************************************************
     * Convert the time part of a Date object into a String value. <BR/>
     *
     * @param   date    Date to be converted to a string representation.
     *
     * @return  String representation of the time value or
     *          <CODE>null</CODE> if the time is not valid.
     *
     * @deprecated This method was replaced by {@link DateTimeHelpers#timeToString(Date)}
     */
    public static String timeToString (Date date)
    {
        // call common method:
        return DateTimeHelpers.timeToString (date);
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
     *
     * @deprecated This method was replaced by {@link DateTimeHelpers#parseDateString(String)}
     */
    public static int[] parseDateString (String dateStr)
    {
        // call common method:
        return DateTimeHelpers.parseDateString (dateStr);
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
     *
     * @deprecated This method was replaced by {@link DateTimeHelpers#parseTimeString(String)}
     */
    public static int[] parseTimeString (String timeStr)
    {
        // call common method:
        return DateTimeHelpers.parseTimeString (timeStr);
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
     *
     * @deprecated This method was replaced by {@link DateTimeHelpers#stringToDateTime(String)}
     */
    public static Date stringToDateTime (String dateStr)
    {
        // call common method:
        return DateTimeHelpers.stringToDateTime (dateStr);
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
     *
     * @deprecated This method was replaced by {@link DateTimeHelpers#stringToDate(String)}
     */
    public static Date stringToDate (String dateStr)
    {
        // call common method:
        return DateTimeHelpers.stringToDate (dateStr);
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
     *
     * @deprecated This method was replaced by {@link DateTimeHelpers#stringToTime(String)}
     */
    public static Date stringToTime (String timeStr)
    {
        // call common method:
        return DateTimeHelpers.stringToTime (timeStr);
    } // stringToTime


    /**************************************************************************
     * Convert a given long (SQL-Money) value to a string. <BR/>
     *
     * @param   money   money value
     *
     * @return  String represantion
     */
    public static String moneyToString (long money)
    {
        String str = null;

        str = NumberFormat.getInstance (Locale.GERMAN).format (((double) money) / 10000);

        if (str.lastIndexOf (',') != -1 && (str.length () - 1 - (str.lastIndexOf (',')) == 1))
        {
            // if there is a comma and only one number after the comma
            str += "0";
        } // if

        if ((str.indexOf (',')) == -1)
        {
            str = str + ",-";
        } // if

        return str;
        // oder:        return (NumberFormat.getNumberInstance ().format (((double) money) / 10000));
    } // moneyToString


    /**************************************************************************
     * Convert a given money-string to a long value. <BR/>
     * This convertion includes post-comma values (4 digits).
     *
     * @param   moneyString Money value.
     *
     * @return  String representation.
     */
    public static long stringToMoney (String moneyString)
    {
        // define needed variables
        double d = 0.0;

        // define german locale
        Locale l
            = new Locale (UtilConstants.LOCALE_LANGUAGE,
                          UtilConstants.LOCALE_COUNTRY);

        // get money formatter for given Locale
        NumberFormat moneyFormat = NumberFormat.getNumberInstance (l);

        try
        {
            // parse string and get double value
            d = (moneyFormat.parse (moneyString)).doubleValue ();
        } // try
        catch (Exception e)
        {
            // TODO currently no handling
        } // catch

        // calculate long value - exit
        return Math.round (10000 * d);
    } // stringToMoney


    /**************************************************************************
     * Converts an given amount in ATS or DM to EURO.
     * If given currency is EURO - no conversion is done.
     *
     * @param   currency    Currency of amount to convert.
     *                      Use the following constants:
     *                          {@link ibs.util.UtilConstants#TOK_CURRENCY_ATS UtilConstants.TOK_CURRENCY_ATS},
     *                          {@link ibs.util.UtilConstants#TOK_CURRENCY_EUR UtilConstants.TOK_CURRENCY_EUR},
     *                          {@link ibs.util.UtilConstants#TOK_CURRENCY_DM UtilConstants.TOK_CURRENCY_DM}.
     * @param   amount      Amount of currency multiplied with 10000.
     *
     * @return  Value in Cent*100  (= Euro * 10000)
     *          -1 if invalid currency is used
     */
    public static long getEuroAmount (String currency, long amount)
    {
        long   retValue;
        double euroAmount = -1;
        double atsToEuro = 13.7603;  // multiply with 100 to cut on
        double dmToEuro = 1.95583;


        // if currency is ATS
        if (UtilConstants.TOK_CURRENCY_ATS.equals (currency))
        {
            // convert ATS to EURO
            euroAmount = amount / atsToEuro;
        } // if
        // if currency is DM
        else if (UtilConstants.TOK_CURRENCY_DM.equals (currency))
        {
            // convert DM to EURO
            euroAmount = amount / dmToEuro;
        } // else if
        // if currency is EURO
        else if (UtilConstants.TOK_CURRENCY_EUR.equals (currency))
        {
            // do nothing
            euroAmount = amount;
        } // else if
        // else not a valid currency
        else
        {
            // throw exception
        } // else

        // cut last 2 floatingpoint places
        retValue = Math.round (euroAmount / 100) * 100;

        // round double to long
        return retValue;
    } // getEuroAmount


    /**************************************************************************
     * Transforms given a double-date value (xxxxx.xxxxx), for example used in
     * Variant, to a Java Date value. <BR/>
     *
     * @param doubleVal  the given date in form of a double
     *
     * @return  the converted Date value
     *
     * @deprecated This method was replaced by {@link DateTimeHelpers#doubleToDate(double)}
     */
    public static Date doubleToDate (double doubleVal)
    {
        // call common method:
        return DateTimeHelpers.doubleToDate (doubleVal);
    } // doubleToDate


    /**************************************************************************
     * Transforms given date to a double value (xxxxx.xxxxx). <BR/>
     * The Variant class needs to get date in this form.
     *
     * @param date  the given date
     *
     * @return  the converted double value
     *
     * @deprecated This method was replaced by {@link DateTimeHelpers#dateToDouble(Date)}
     */
    public static double dateToDouble (Date date)
    {
        // call common method:
        return DateTimeHelpers.dateToDouble (date);
    } // dateToDouble


    /**************************************************************************
     * Changes the delimiter in a property string. <BR/>
     *
     * @param   input       The input property string.
     * @param   oldDelim    The old delimiter.
     * @param   newDelim    The new delimiter.
     *
     * @return  the converted property
     *
     * @deprecated This method was replaced by {@link StringHelpers#changeDelimiter(String, String, String)}
     */
    public static String changeDelimiter (String input, String oldDelim,
                                          String newDelim)
    {
        // call common method:
        return StringHelpers.changeDelimiter (input, oldDelim, newDelim);
    } // changeDelimiter


    /**************************************************************************
     * Returns a string containing the size of file in KB or bytes and a
     * unknown value. <BR/>
     *
     * @param   sizeOfFile    size of the file
     * @param   env           The current environment
     *
     * @return  A string containing the size of file in KB or bytes.
     */
    public static String convertFileSize (double sizeOfFile, Environment env)
    {
        String str = null;
        double sizeInBytes = 0.0;
        if (sizeOfFile == 0.0)          // if the size is not known, return message
        {
            str = MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE, BOMessages.ML_MSG_UNKNOWN, env);
        } // if
        else if (sizeOfFile > 10240)    // if the size is bigger than 10240 bytes, create output in KB
        {
            sizeInBytes = Math.round (sizeOfFile / 1024);
            str = Double.toString (sizeInBytes);
            str = str + " KB";
        } // else if
        else if (sizeOfFile > 1024000)    // if the size is bigger than 1024000 bytes, create output in KB
        {
            sizeInBytes = Math.round (sizeOfFile / 1048576);
            str = Double.toString (sizeInBytes);
            str = str + " MB";
        } // else if
        else                            // display the filesize in bytes
        {
            sizeInBytes = Math.round (sizeOfFile);
            int value = (new Double (sizeInBytes)).intValue ();
            str = Integer.toString (value);
            str = str + " Bytes";
        } // else if
        return str;
    } // convertFileSize


    /**************************************************************************
     * Returns a string buffer consisting of the concatenated Strings of an
     * iteration, separated by a specific separator. <BR/>
     *
     * @param   iter        Iterator which contains the strings to be copied
     *                      into one string.
     * @param   separator   Separator to be set between these strings.
     *
     * @return  A string containing different strings from the iterator,
     *          separated by the separator.
     *          <CODE>null</CODE> if the iterator was <CODE>null</CODE>.
     *
     * @deprecated This method was replaced by {@link StringHelpers#stringIteratorToStringBuffer(Iterator, StringBuffer)}
     */
    public static StringBuffer stringIteratorToStringBuffer (
                                                             Iterator<String> iter,
                                                             StringBuffer separator)
    {
        // call common method:
        return StringHelpers.stringIteratorToStringBuffer (iter, separator);
    } // stringIteratorToStringBuffer


    /**************************************************************************
     * Returns a string buffer consisting of the concatenated Strings of an
     * iteration, separated by a specific separator. <BR/>
     *
     * @param   iter        Iterator which contains the strings to be copied
     *                      into one string.
     * @param   separator   Separator to be set between these strings.
     *
     * @return  A string containing different strings from the iterator,
     *          separated by the separator.
     *          <CODE>null</CODE> if the iterator was <CODE>null</CODE>.
     *
     * @deprecated This method was replaced by {@link StringHelpers#stringIteratorToStringBuffer(Iterator, String)}
     */
    public static StringBuffer stringIteratorToStringBuffer (
                                                             Iterator<String> iter,
                                                             String separator)
    {
        // call common method:
        return StringHelpers.stringIteratorToStringBuffer (iter, separator);
    } // stringIteratorToStringBuffer


    /**************************************************************************
     * Returns a string consisting of the Strings of a string array, separated
     * by a specific separator. <BR/>
     *
     * @param   strArray    String array which contains the strings to be copied
     *                      into one string.
     * @param   separator   Separator to be set between these strings.
     *
     * @return  A string containing different strings from the string array,
     *          separated by the separator.
     *          <CODE>null</CODE> if the array was <CODE>null</CODE>.
     *
     * @deprecated This method was replaced by {@link StringHelpers#stringArrayToString(String[], StringBuffer)}
     */
    public static String stringArrayToString (String[] strArray,
                                              StringBuffer separator)
    {
        // call common method:
        return StringHelpers.stringArrayToString (strArray, separator);
    } // stringArrayToString


    /**************************************************************************
     * Returns a string consisting of the Strings of a string array, separated
     * by a specific separator. <BR/>
     *
     * @param   strArray    String array which contains the strings to be copied
     *                      into one string.
     * @param   separator   Separator to be set between these strings.
     *
     * @return  A string containing different strings from the string array,
     *          separated by the separator.
     *          <CODE>null</CODE> if the array was <CODE>null</CODE>.
     *
     * @deprecated This method was replaced by {@link StringHelpers#stringArrayToString(String[], String)}
     */
    public static String stringArrayToString (String[] strArray,
                                              String separator)
    {
        // call common method:
        return StringHelpers.stringArrayToString (strArray, separator);
    } // stringArrayToString


    /**************************************************************************
     * Returns a string consisting of the Strings of a string array, separated
     * by a specific separator. <BR/>
     *
     * @param   strArray    String array which contains the strings to be copied
     *                      into one string.
     * @param   separator   Separator to be set between these strings.
     *
     * @return  A string containing different strings from the string array,
     *          separated by the separator.
     *          <CODE>null</CODE> if the array was <CODE>null</CODE>.
     *
     * @deprecated This method was replaced by {@link StringHelpers#stringArrayToString(String[], char)}
     */
    public static String stringArrayToString (String[] strArray, char separator)
    {
        // call common method:
        return StringHelpers.stringArrayToString (strArray, separator);
    } // stringArrayToString


    /**************************************************************************
     * Returns a string consisting of the Strings of a string array. <BR/>
     *
     * @param   strArray    String array which contains the strings to be copied
     *                      into one string.
     *
     * @return  A string containing different strings from the string array
     *
     * @deprecated This method was replaced by {@link StringHelpers#stringArrayToString(String[])}
     */
    public static String stringArrayToString (String[] strArray)
    {
        // call common method:
        return StringHelpers.stringArrayToString (strArray);
    } // stringArrayToString


    /**************************************************************************
     * Returns a string consisting of the Strings of a string array. <BR/>
     * Uses standard separator: comma (<CODE>", "</CODE>).
     *
     * @param   strArray    String array which contains the strings to be copied
     *                      into one string.
     *
     * @return  A string containing different strings from the string array
     *
     * @deprecated This method was replaced by {@link StringHelpers#stringArrayToStringC(String[])}
     */
    public static String stringArrayToStringC (String[] strArray)
    {
        // call common method:
        return StringHelpers.stringArrayToString (strArray);
    } // stringArrayToStringSC


    /**************************************************************************
     * Returns a string consisting of the Strings of a string array. <BR/>
     * Uses standard separator: semicolon (<CODE>"; "</CODE>).
     *
     * @param   strArray    String array which contains the strings to be copied
     *                      into one string.
     *
     * @return  A string containing different strings from the string array
     *
     * @deprecated This method was replaced by {@link StringHelpers#stringArrayToStringSC(String[])}
     */
    public static String stringArrayToStringSC (String[] strArray)
    {
        // call common method:
        return StringHelpers.stringArrayToString (strArray);
    } // stringArrayToStringSC


    /**************************************************************************
     * A wrapper method for the stringToStringArray method with the
     * includeEmpty lines deactivated. <BR/>
     *
     * @param str           String which contains different strings separated
     *                      by separator character.
     * @param separator     Separator between the different parts of the string.
     *
     * @return  A string array containing the elements from the string.
     *
     * @deprecated This method was replaced by {@link StringHelpers#stringToStringArray(String, char)}
     */
    public static String[] stringToStringArray (String str, char separator)
    {
        // call common method:
        return StringHelpers.stringToStringArray (str, separator);
    } // stringToStringArray


    /**************************************************************************
     * Returns a string containing the Strings of a string array, separated by
     * a separator character. <BR/>
     * Note that a separator at the beginning or at the end will be ignored. <BR/>
     * Trailing white spaces will be trimmed. <BR/>
     * Multiple separators will also be ignored. <BR/>
     *
     * @param   str         String which contains different strings separated
     *                      by separator character.
     * @param   separator   Separator between the different parts of the string.
     * @param   includeEmpty    ??? => BB
     *
     * @return  A string array containing the elements from the string.
     *
     * @deprecated This method was replaced by {@link StringHelpers#stringToStringArray(String, char, boolean)}
     */
    public static String[] stringToStringArray (String str, char separator,
                                                boolean includeEmpty)
    {
        // call common method:
        return StringHelpers.stringToStringArray (str, separator, includeEmpty);
    } // stringToStringArray


    /**************************************************************************
     * Compares two string arrays and creates two vectors. In one vector are the
     * strings wich are only in the first string array, and in the other vector
     * are the strings which are only in the second string array. <BR/>
     *
     * @param   strArray1   First string array to compare.
     * @param   strArray2   Second string array to compare.
     * @param   notInArray1 Vector which contains the strings wich are not in
     *                      strArray1 but in strArray2.
     * @param   notInArray2 Vector which contains the strings wich are not in
     *                      strArray2 but in strArray1.
     *
     * @deprecated This method was replaced by {@link StringHelpers#compareStringArrays(String[], String[], Vector, Vector)}
     */
    public static void compareStringArrays (String[] strArray1,
                                            String[] strArray2,
                                            Vector<String> notInArray1,
                                            Vector<String> notInArray2)
    {
        // call common method:
        StringHelpers.compareStringArrays (strArray1, strArray2, notInArray1, notInArray2);
    } // compareStringArrays


    /**************************************************************************
     * Search for one string in a string array.
     *
     * @param   searchArray stringArray
     * @param   findString  String to search for.
     *
     * @return  If the findString was found, the function returns the index
     *          where searchArray contains the same string
     *          if not found =>   -1
     *
     * @deprecated This method was replaced by {@link StringHelpers#findString(String[], String)}
     */
    public static int findString (String[] searchArray, String findString)
    {
        // call common method:
        return StringHelpers.findString (searchArray, findString);
    } // findString


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
     *
     * @deprecated This method was replaced by {@link DateTimeHelpers#compareToCurrentDate(Date)}
     */
    public static long compareToCurrentDate (Date date)
    {
        // call common method:
        return DateTimeHelpers.compareToCurrentDate (date);
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
     *
     * @deprecated This method was replaced by {@link DateTimeHelpers#compareToCurrentDate(String)}
     */
    public static long compareToCurrentDate (String dateStr)
    {
        // call common method:
        return DateTimeHelpers.compareToCurrentDate (dateStr);
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
     *
     * @deprecated This method was replaced by {@link DateTimeHelpers#compareDates(Date, Date)}
     */
    public static long compareDates (Date date1, Date date2)
    {
        // call common method:
        return DateTimeHelpers.compareDates (date1, date2);
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
     *
     * @deprecated This method was replaced by {@link DateTimeHelpers#compareTimes(Date, Date)}
     */
    public static long compareTimes (Date date1, Date date2)
    {
        // call common method:
        return DateTimeHelpers.compareTimes (date1, date2);
    } // compareTimes


    /**************************************************************************
     * Compare two Dates including hours, minutes and seconds. <BR/>
     *
     * @param   date1   Date to compare with an other Date
     * @param   date2   Date to compare with an other Date
     *
     * @return  if Date1 > Date2 returnvalue > 0
     *          if Date1 = Date2 returnvalue = 0
     *          if Date1 < Date2 returnvalue < 0
     *
     * @deprecated This method was replaced by {@link DateTimeHelpers#compareDateTimes(Date, Date)}
     */
    public static long compareDateTimes (Date date1, Date date2)
    {
        // call common method:
        return DateTimeHelpers.compareDateTimes (date1, date2);
    } // compareDateTimes


    /**************************************************************************
     * converts in Integer Array into a Hex String
     * each entry in the resulting array represents the sum of four elements
     *
     * @param   values  All integer values of the array.
     * @param   max     Constant, which defines the highest value in the
     *                  application.
     *
     * @return  String, which contains the hex representation of the array
     */
    //    public static final String IntegerArrayToHex (int[] values, int max)
    public static final String integerArrayToHex (int[] values, int max)
    {
        int maxLocal = max;             // value for local assignments
        // get the length of the incoming array:
        int maxvalue = (maxLocal / 4) + 1;   // length of return value
        int j;                          // counter
        int encoded = 0;
        StringBuilder retVal = new StringBuilder ("");
        byte[] code = null;

        // get the maximum value of the list:
        // loop through all elements of the list to find the maximum value:
        for (j = 0; j < values.length; j++) // there exists one further element
        {
            // check if the value is bigger than the current maximum value:
            if (values[j] > maxLocal)        // the value is bigger?
            {
                // assign the new maximum value:
                maxLocal = values[j];
            } // if the value is bigger
        } // for

        // set the length of the return value and create the return array:
        maxvalue = (maxLocal / 4) + 1;
        code = new byte [maxvalue];

        // initial values for the array:
        for (j = 0; j < values.length; j++)
        {
            // encode the actual value:
            // set the corresponding bit at this value's position within the
            // array
            encoded = values[j];
            if (encoded != -1)          // do not consider the value
            {
                // determine position within the array, compute the bit which
                // shall be set and set the bit:
                code[encoded / 4] |= 1 << (encoded % 4);
            } // if do not consider the value
        } // for

        // build the result string:
        for (j = maxvalue - 1; j >= 0; j--)
        {
            retVal.append (Helpers.byteToHexDigit (code[j]));
        } // for
        return retVal.toString ();
    } // IntegerArrayToHex


    /**************************************************************************
     * Convert a vector containing values of class <CODE>Integer</CODE> to an
     * int array. <BR/>
     *
     * @param   values  The vector with the Integer values.
     *
     * @return  Array containing the int values. If <CODE>values</CODE> is
     *          empty the resulting array is an array of size <CODE>0</CODE>.
     */
    public static final int[] integerVectorToArray (Vector<Integer> values)
    {
        // create the array:
        int[] retVal = new int[values.size ()];
        int i = 0;                      // current index

        // loop through all elements of the vector and add them to the array:
        for (Iterator<Integer> iter = values.iterator (); iter.hasNext ();)
        {
            Integer elem = iter.next ();
            retVal[i++] = elem.intValue ();
        } // for iter

        // return the result:
        return retVal;
    } // integerVectorToArray


    /**************************************************************************
     * Returns a stringArray containing the different Objects in the enumeration
     * as Strings
     * <BR/>
     *
     * @param   enumValue   Enumeration to be converted in a String array
     * @param   size        Number of elements in the Enumeration
     *
     * @return  a string containing different strings from the string array, separated
     *          by the separator.
     *
     * @deprecated This method was replaced by {@link StringHelpers#enumerationToStringArray(Enumeration, int)}
     */
    public static String [] enumerationToStringArray (Enumeration<String> enumValue,
                                                      int size)
    {
        // call common method:
        return StringHelpers.enumerationToStringArray (enumValue, size);
    } // enumerationToStringArray


    /**************************************************************************
     * Create a correct URL. <BR/>
     *
     * @param   inputUrl        A string, that could be an URL
     *
     * @return  A String, that is an URL
     */
    public static String createUrlString (String inputUrl)
    {
        String fineUrl = "";

        if (inputUrl.indexOf (":") == -1)
        {
            fineUrl = IOConstants.URL_HTTP + inputUrl;
        } // if
        else
        {
            fineUrl = inputUrl;
        } // else
        return fineUrl;                 // return a correct URL
    } // createUrlString


    /**************************************************************************
     * Creates current date as absolute date value (GMT+0). Should be used
     * instead of "new Date ()". System works only with absolute dates, e.g.
     * database date-values are absolute, queries on database date values use
     * absolute values (means that no timezone-conversion happens). <BR/>
     *
     * @return  the current absolute date
     *
     * @deprecated This method was replaced by {@link DateTimeHelpers#getCurAbsDate()}
     */
    public static Date getCurAbsDate ()
    {
        // call common method:
        return DateTimeHelpers.getCurAbsDate ();
    } // getCurAbsDate


    /**************************************************************************
     * Convert byte array to string. <BR/>
     *
     * @param   byteArr     The byte array to be converted to string
     *                      representation.
     *
     * @return  The string representation of the byte array.
     */
    public static final String byteToString (byte[] byteArr)
    {
        // create a string with the configured encoding
        String encodedString = new String (byteArr, Charset.forName (DIConstants.CHARACTER_ENCODING));
        
        return encodedString;
    } // byteToString


    /**************************************************************************
     * Convert the provided part of the byte array to string. <BR/>
     *
     * @param   byteArr     The byte array to be converted to string
     *                      representation.
     * @param   offset      The index of the first byte to decode
     * @param   length      The number of bytes to decode     
     *
     * @return  The string representation of the byte array.
     */
    public static final String byteToString (byte[] byteArr, int offset, int length)
    {   
        // create a string from the provided range of the provided byte array with the configured encoding
        String encodedString = new String (byteArr, offset, length, Charset.forName (DIConstants.CHARACTER_ENCODING));
        
        return encodedString;
    } // byteToString


    /**************************************************************************
     * Substitute special characters in a string. <BR/>
     * These are: ' ', Ä, Ü, Ö, ö, ä, ü, ß. <BR/>
     * Note that this method is used for file names. <BR/>
     *
     * @param   str     The string to replace the characters in.
     *
     * @return  The string with substituted special characters.
     *
     * @deprecated This method was replaced by {@link StringHelpers#substituteSpecialCharacters(String)}
     */
    public static final String substituteSpecialCharacters (String str)
    {
        // call common method:
        return StringHelpers.substituteSpecialCharacters (str);
    } // substituteSpecialCharacters


    /**************************************************************************
     * Compute a cyphered hash of a given input. <BR/>
     *
     * @param   str     The string to cypher.
     *
     * @return  The resulting hash.
     *
     * @deprecated This method was replaced by {@link StringHelpers#computeHash(String)}
     */
    public static final String computeHash (String str)
    {
        // call common method:
        return StringHelpers.computeHash (str);
    } // computeHash


    /**************************************************************************
     * Computes a random number and returns the string-representation. Used
     * to add to the URLs to prevent caching. Cannot be used as a random number
     * used for security purposes. <BR/>
     *
     * @return  The string representing the random number.
     *
     * @deprecated This method was replaced by {@link StringHelpers#computeRandomString()}
     */
    public static String computeRandomString ()
    {
        // call common method:
        return StringHelpers.computeRandomString ();
    } // computeRandomString


    /**************************************************************************
     * This method returns the stacktrace of the exception given as a parameter. <BR/>
     *
     * @param   t       The Throwable object.
     *
     * @return  The stacktrace as a string.
     */
    public static final String getStackTraceFromThrowable (Throwable t)
    {
        String tmp = "";

        if (t != null)
        {
            // initialize the writers needed
            StringWriter sw = new StringWriter ();
            PrintWriter pw = new PrintWriter (sw);

            // prints the stacktrace into the variable
            t.printStackTrace (pw);
            tmp = sw.getBuffer ().toString ();

            // close the writers:
            try
            {
                sw.close ();
            } // try
            catch (IOException e)
            {
                // TODO currently no handling
            } // catch

            pw.close ();
        } // if the throwable object was not null

        // return the stacktrace or the empty string if the throwable object
        // was null
        return tmp;
    } // getStackTraceFromThrowable


    /**************************************************************************
     * Escapes all occurences in the given string. <BR/>
     * All characters which are escaped are declared in the private property
     * p_charsToBeEscaped.
     *
     * @param   toEscape        String which should be escaped if necessary.
     *
     * @return  The escaped string.
     *
     * @deprecated This method was replaced by {@link StringHelpers#escape(String)}
     */
    public static String escape (String toEscape)
    {
        // call common method:
        return StringHelpers.escape (toEscape);
    } // escape


    /**************************************************************************
     * Create a timestamp string with the actual date and time using the
     * format "yyyyMMddHHmmssSSS". <BR/>
     *
     * @return  The timestamp
     *
     * @deprecated This method was replaced by {@link DateTimeHelpers#getTimestamp()}
     */
    public static String getTimestamp ()
    {
        // call common method:
        return DateTimeHelpers.getTimestamp ();
    } // getTimestamp


    /**************************************************************************
     * Create a weblink string with a given oid string and a label for the
     * link. <BR/>
     *
     * @param oidStr    the oid string to link to
     * @param label     the label for the link
     * @param env       the environment to read the base url from
     *
     * @return  the weblink string
     */
    public static String createWeblink (String oidStr, String label,
                                        Environment env)
    {
        return "<A HREF=\"" +
            IOConstants.URL_HTTP + env.getServerName () + env.getBaseURL () +
            "?fct=666&amp;frame=true&amp;dom=1&amp;oid=" +
            oidStr + "\">" + label + "</A>";
    } // createWeblink


    /**************************************************************************
     * Create a weblink string with a given oid and a label for the
     * link. <BR/>
     *
     * @param oid       the oid to link to
     * @param label     the label for the link
     * @param env       the environment to read the base url from
     *
     * @return  the weblink string
     */
    public static String createWeblink (OID oid, String label, Environment env)
    {
        return Helpers.createWeblink (oid.toString (), label, env);
    } // createWeblink


    /**************************************************************************
     * Check if an url exists. <BR/>
     * An empty url will return <code>false</code>!. <BR/>
     *
     * @param urlStr    the url string
     *
     * @return  <code>true</code> if the URL exists or
     *          <code>false</code> otherwise
     */
    public static boolean existsURL (String urlStr)
    {
        // CONSTRAINT: the url string must not be null!
        if (urlStr == null || urlStr.length () == 0)
        {
            return false;
        } // if

        try
        {
            HttpURLConnection.setFollowRedirects (false);
            // note : you may also need
            // HttpURLConnection.setInstanceFollowRedirects (false)
            HttpURLConnection con = (HttpURLConnection) new URL (urlStr)
                .openConnection ();
            con.setRequestMethod ("HEAD");
            // valid response codes are
            // 200:OK
            // 302:FOUND (= HttpURLConnection.HTTP_MOVED_TEMP)
            return con.getResponseCode () == HttpURLConnection.HTTP_OK || con
                .getResponseCode () == HttpURLConnection.HTTP_MOVED_TEMP;
        } // try
        catch (Exception e)
        {
            return false;
        } // catch e
    } // existsURL


    /**************************************************************************
     * Returns the summarized value of 2 hash codes. <BR/>
     * The result can be used for the method {@link Object#hashCode()}. <BR/>
     * <B>ATTENTION: The sequence of the parameters is relevant for the result,
     * i.e. <CODE>sumHashCodes (a, b) != sumHashCodes (b, a)</CODE>.</B>
     *
     * @param   hashCode1   First hash code.
     * @param   hashCode2   Second hash code.
     *
     * @return  The summarized hash code.
     */
    public static int sumHashCodes (int hashCode1, int hashCode2)
    {
        return (hashCode1 & 0x7FFFFFF) + ((hashCode2 & 0x3FFFFFF) >> 1);
    } // sumHashCodes

} // class Helpers

/*
 * Class: SQLHelpers.java
 */

// package:
package ibs.tech.sql;

// imports:
//KR TODO: unsauber
import ibs.app.AppConstants;
import ibs.bo.BOConstants;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.io.IOHelpers;
import ibs.obj.query.QueryConstants;
import ibs.util.DateTimeHelpers;
import ibs.util.Helpers;
import ibs.util.UtilConstants;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;


/******************************************************************************
 * This class defines the constants used in the PreparedStatement class. <BR/>
 *
 * @version     $Id: SQLHelpers.java,v 1.55 2013/01/17 15:22:03 btatzmann Exp $
 *
 * @author      Mark Wassermann (MW)
 ******************************************************************************
 */
public abstract class SQLHelpers extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SQLHelpers.java,v 1.55 2013/01/17 15:22:03 btatzmann Exp $";


    /**
     * Format for datetime data. <BR/>
     * This String contains the format description for datetime formats.
     */
    public static final StringBuffer DATETIMEFORMAT =
        new StringBuffer ("DD.MM.YYYY HH24:MI:SS");


    /**
     * Date value for start date. <BR/>
     * This is the date value which is used to create a fully qualified
     * datetime value out of a time value.
     */
    public static final StringBuffer DATESTART =
        new StringBuffer ("01.01.1970");


    /**
     * Time value for day start. <BR/>
     * This is the time value which is used to create a fully qualified
     * datetime value out of a date value.
     */
    public static final StringBuffer TIMEDAYSTART =
        new StringBuffer ("00:00:00");


    /**
     * Time value for day end. <BR/>
     * This is the time value which is used to create a fully qualified
     * datetime value out of a date value, when the end of the day is necessary.
     */
    public static final StringBuffer TIMEDAYEND =
        new StringBuffer ("23:59:59");


    /**
     * Comparison: equality. <BR/>
     */
    private static final String COMP_EQU = " = ";


    /**************************************************************************
     * Create the content of one text area for storing it on the db. <BR/>
     * the "'" is replaced with one unicode - otherwise you would have
     * problems in queries. <BR/>
     * If the value is <CODE>null</CODE> the result is <CODE>null</CODE>, too.
     *
     * @param   text        The text to be stored on the db.
     *
     * @return  The constructed database string.
     */
    public static String asciiToDb (final String text)
    {
        StringBuilder sqlText = new StringBuilder ("");            // the resulting text
        final String unicode = " " + AppConstants.UC_QUOTE + " ";
                                        // the replacement string
        final char quote = '\'';        // the quote character

        if (text != null)               // no null value?
        {
            // loop through all characters of the text and check each if it is
            // a quote. Each quote is replaced by a special character
            // (AppConstants.UC_QUOTE) surrounded by spaces.
            for (int i = 0; i < text.length (); i++)
            {
                if (text.charAt (i) == quote) // the character is a quote
                {
                    // replace the character by the replacement string:
                    sqlText.append (unicode);
                } // if
                else                    // each other character
                {
                    // add the character to the result:
                    sqlText.append (text.charAt (i));
                } // else
            } // for
        } // if no null value
        else                            // the value is null
        {
            sqlText = null;             // the result is also null
        } // else the value is null

        return sqlText != null ? sqlText.toString () : null;                 // return the result
    } // asciiToDb


    /**************************************************************************
     * Convert the content of one TEXT - Field in DB - back to ascii-text
     * the "'" was replaced with one unicode ('$#39') - replace it back. <BR/>
     * If the value is <CODE>null</CODE> the result is <CODE>null</CODE>, too.
     *
     * @param   sqlText     The text which was gotten out of the database.
     *
     * @return  The constructed element.
     */
    public static String dbToAscii (String sqlText)
    {
        String sqlTextLocal = sqlText; // variable for local assignments
        StringBuilder text = new StringBuilder ("");               // the resulting text
        final String unicode = " " + AppConstants.UC_QUOTE + " ";
                                        // the replacement string
        final int unicodeLength = unicode.length (); // the length of the repl. string
        final char quote = '\'';              // the quote character
        int pos;                        // the actual position

        if (sqlTextLocal != null)            // no null value?
        {
            // replace each occurrence of the replacement string with the
            // quote character:
            while ((pos = sqlTextLocal.indexOf (unicode)) > -1)
            {
                text.append (sqlTextLocal.substring (0, pos) + quote);
                sqlTextLocal = sqlTextLocal.substring (pos + unicodeLength);
            } // while

            // concatenate last part of SQLString
            text.append (sqlTextLocal);
        } // if no null value
        else                            // the value is null
        {
            text = null;                // the result is also null
        } // else the value is null

        return text != null ? text.toString () : null;                    // return the result
    } // dbToAscii


    /**************************************************************************
     * Convert a number stored in a Java String to the corresponding binary
     * representation for the database. <BR/>
     * For Oracle and DB2 the result is <CODE>hextoraw (&lt;value&gt;)</CODE>,
     * for MSSQL it is <CODE>0x&lt;value&gt;</CODE>,<BR/>
     * For DB2 it is <CODE>X'&lt;value&gt;'</CODE>.<BR/>
     * If the value is <CODE>null</CODE> the result is <CODE>null</CODE>, too.
     *
     * @param   value   The value to be converted to database representation.
     *
     * @return  The constructed string with the database value.
     */
    public static String stringToDbBinary (final String value)
    {
        return SQLHelpers.stringToDbBinary (value, false);
    } // stringToDbBinary


    /**************************************************************************
     * Convert a number stored in a Java String to the corresponding binary
     * representation for the database. The conversion can be forced using
     * the forceConversion parameter.<BR/>
     * For Oracle and DB2 the result is <CODE>hextoraw (&lt;value&gt;)</CODE>,
     * for MSSQL it is <CODE>0x&lt;value&gt;</CODE> or when forceConversion is
     * set <CODE>dbo.f_stringToByte ('&lt;value&gt;')</CODE>,
     * For DB2 it is <CODE>X'&lt;value&gt;'</CODE>.<BR/>
     * If the value is <CODE>null</CODE> the result is <CODE>null</CODE>, too.
     *
     * @param value   The value to be converted to database representation.
     * @param forceConversion    force a conversion of the string to an OID
     *
     * @return  The constructed string with the database value.
     */
    public static String stringToDbBinary (final String value,
                                           boolean forceConversion)
    {
        String retValue = null;         // the return value

        // check if the value is valid:
        if (value != null && value.length () > 0) // valid value?
        {
            // handle the database type:
            switch (SQLConstants.DB_TYPE)
            {
                case SQLConstants.DB_ORACLE:
                    // compute the string representation of the value:
                    retValue = "hextoraw (\'" + value + "\')";
                    break;

                case SQLConstants.DB_DB2:
                    // compute the string representation of the value:
                    // (ex.: X'00000000')
                    retValue = "X\'" + value + "\'";
                    break;

                case SQLConstants.DB_MSSQL:
                    // compute the string representation of the value:
                    if (forceConversion)
                    {
                        retValue = " dbo.f_stringToByte ('" +
                            UtilConstants.NUM_START_HEX + value + "') ";
                    } // if (forceConversion)
                    else    // no conversion
                    {
                        retValue = UtilConstants.NUM_START_HEX + value;
                    } // else no conversion
                    break;

                default:
                    retValue = null;
            } // switch
        } // if valid value

        return retValue;                // return the result
    } // stringToDbBinaryConverted


    /**************************************************************************
     * Convert a list of OID objects to a string containing a database specific
     * comma-separated list of oids.
     *
     * @param   oidList List of oids.
     *
     * @return  The constructed string with the database-specific value.
     */
    public static StringBuilder oidListToQueryString (final List<OID> oidList)
    {
        StringBuilder oidStr = new StringBuilder ();
        StringBuilder comma = new StringBuilder ();
        final StringBuilder SEP_COMMA = new StringBuilder (",");

        // loop through all oids:
        // convert each oid in a db specific string value and concatenate them
        // to a comma-separated list:
        for (Iterator<OID> iter = oidList.iterator (); iter.hasNext ();)
        {
            oidStr.append (comma).append (iter.next ().toStringQu ());
            comma = SEP_COMMA;
        } // for iter

        // return the result:
        return oidStr;
    } // oidListToQueryString


    /***************************************************************************
     * Returns a substring expression for the given parameters and the defined
     * database.<BR/>
     *
     * @param   value   The string where the substring should be retrieved from.
     * @param   start   The starting index (starts with '1').
     * @param   length  The number of characters to retrieve starting form the
     *                  starting index.
     *
     * @return  The substring expression.
     *          <CODE>null</CODE> if the database type is not valid.
     */
    public static String getSubstringExpression (String value,
        String start, String length)
    {
        String retValue = null;         // the return value

        // handle the database type:
        switch (SQLConstants.DB_TYPE)
        {
            case SQLConstants.DB_ORACLE:
                retValue = "substr (" + value + ", " + start + ", " + length + ")";
                break;

            case SQLConstants.DB_DB2:
                retValue = "substr (" + value + ", " + start + ", " + length + ")";
                break;

            case SQLConstants.DB_MSSQL:
                retValue = "substring (" + value + ", " + start + ", " + length + ")";
                break;

            default:
                retValue = null;
        } // switch

        // return the result:
        return retValue;
    } // getSubstringExpression


    /***************************************************************************
     * Returns a length expression for the given parameters and the defined
     * database. <BR/>
     *
     * @param   value   The string where the length should be retrieved from.
     *
     * @return  The length expression.
     *          <CODE>null</CODE> if the database type is not valid.
     */
    public static String getLengthExpression (String value)
    {
        String retValue = null;         // the return value

        // handle the database type:
        switch (SQLConstants.DB_TYPE)
        {
            case SQLConstants.DB_ORACLE:
                retValue = "length (" + value + ")";
                break;

            case SQLConstants.DB_DB2:
                retValue = "length (" + value + ")";
                break;

            case SQLConstants.DB_MSSQL:
                retValue = "len (" + value + ")";
                break;

            default:
                retValue = null;
        } // switch

        // return the result:
        return retValue;
    } // getLengthExpression


    /**************************************************************************
     * Convert a calendar value to the corresponding string which can be used
     * in database statements. <BR/>
     * The String format is: <CODE>dd.mm.yyyy hh24:mi:ss</CODE>. <BR/>
     * The time is always truncated, that means that 23:12:57.823 is converted
     * to 23:12:57.
     *
     * @param   cal     The calendar.
     *
     * @return  The date string.
     *
     * @see #DATETIMEFORMAT
     */
    public static StringBuffer dateStringB (final GregorianCalendar cal)
    {
        final StringBuffer retVal;      // the return value

        retVal = new StringBuffer ()
            .append (cal.get (Calendar.DAY_OF_MONTH)).append (".")
            .append (cal.get (Calendar.MONTH) + 1).append (".")
            .append (cal.get (Calendar.YEAR)).append (" ")
            .append (cal.get (Calendar.HOUR_OF_DAY)).append (":")
            .append (cal.get (Calendar.MINUTE)).append (":")
            .append (cal.get (Calendar.SECOND));

        return retVal;                  // return the result
    } // dateStringB


    /**************************************************************************
     * Creates a condition String for the WHERE clause in a query, for datatype
     * STRING/VARCHAR2. <BR/>
     *
     * @param   attribute   Name of attribute in query to be contrained with
     *                      searchvalue
     * @param   matchType   match type for conditionstring between attribute
     *                      and searchvalue
     * @param   searchValue searchvalue to constrain the resultset via
     *                      matching with attribute
     * @param   caseSensitive   Shall the check be done in a case-sensitive
     *                      manner or not?
     *
     * @return  The condition string for the <CODE>WHERE</CODE> clause without
     *          <CODE>AND</CODE> at the beginning;
     *          <CODE>null</CODE>, if any parameter is <CODE>null</CODE>.
     */
    public static StringBuffer getQueryConditionString (
                                                        final String attribute,
                                                        final String matchType,
                                                        final String searchValue,
                                                        final boolean caseSensitive)
    {
        // call the common method and return the result:
        return SQLHelpers.getQueryConditionString (
            new StringBuffer ().append (attribute), matchType,
            new StringBuffer ().append (searchValue), caseSensitive);
    } // getQueryConditionString


    /**************************************************************************
     * Creates a condition String for the WHERE clause in a query, for datatype
     * STRING/VARCHAR2. <BR/>
     *
     * @param   attribute   Name of attribute in query to be contrained with
     *                      searchvalue
     * @param   matchType   match type for conditionstring between attribute
     *                      and searchvalue
     * @param   searchValue searchvalue to constrain the resultset via
     *                      matching with attribute
     * @param   caseSensitive   Shall the check be done in a case-sensitive
     *                      manner or not?
     *
     * @return  The condition string for the <CODE>WHERE</CODE> clause without
     *          <CODE>AND</CODE> at the beginning;
     *          <CODE>null</CODE>, if any parameter is <CODE>null</CODE>.
     */
    public static StringBuffer getQueryConditionString (
                                                        final StringBuffer attribute,
                                                        final String matchType,
                                                        final StringBuffer searchValue,
                                                        final boolean caseSensitive)
    {
        StringBuffer sqlConditionString = null;

        // check valid call of method
        if (attribute == null || matchType == null || searchValue == null)
                                        // values not valid?
        {
            return null;
        } // if values not valid

        // check matchtype
        if (matchType.equals (SQLConstants.MATCH_EXACT)) // exact match?
        {
            if (caseSensitive)
            {
                sqlConditionString = new StringBuffer (" ")
                    .append (attribute).append (" = ")
                    .append (getUnicodeString (searchValue.toString ())).append (" ");
            } // if
            else
            {
                sqlConditionString = new StringBuffer ()
                    .append (" LOWER (").append (attribute).append (") =")
                    .append (" LOWER (")
                    .append (getUnicodeString (searchValue.toString ()))
                    .append (") ");
            } // else
        } // if exact match
        else if (matchType.equals (SQLConstants.MATCH_SUBSTRING))
                                        // substring match?
        {
            if (caseSensitive)
            {
                sqlConditionString = new StringBuffer (" ")
                    .append (attribute)
                    .append (" LIKE ")
                    .append (getUnicodeString ("%" + searchValue + "%"))
                    .append (" ");
            } // if
            else
            {
                sqlConditionString = new StringBuffer ()
                    .append (" LOWER (").append (attribute).append (") LIKE")
                    .append (" LOWER (")
                    .append (getUnicodeString ("%" + searchValue + "%"))
                    .append (") ");
            } // else
        } // else if substring match
        else if (matchType.equals (SQLConstants.MATCH_IN)) // IN match?
        {
            sqlConditionString = new StringBuffer ()
                .append (attribute)
                .append (" IN (")
                .append (searchValue != null ? getUnicodeStringList (searchValue.toString ()) : searchValue)
                .append (") ");
        } // else if IN match
        else if (matchType.equals (SQLConstants.MATCH_STARTSWITH))
                                        // startswith match?
        {
            if (caseSensitive)
            {
                sqlConditionString = new StringBuffer (" ")
                    .append (attribute)
                    .append (" LIKE ")
                    .append (getUnicodeString (searchValue + "%"))
                    .append (" ");
            } // if
            else
            {
                sqlConditionString = new StringBuffer ()
                    .append (" LOWER (").append (attribute).append (") LIKE")
                    .append (" LOWER (")
                    .append (getUnicodeString (searchValue + "%"))
                    .append (") ");
            } // else
        } // else if startswith match
        else if (matchType.equals (SQLConstants.MATCH_ENDSWITH))
                                        // endswith match?
        {
            if (caseSensitive)
            {
                sqlConditionString = new StringBuffer (" ")
                    .append (attribute)
                    .append (" LIKE ")
                    .append (getUnicodeString ("%" + searchValue))
                    .append (" ");
            } // if
            else
            {
                sqlConditionString = new StringBuffer ()
                    .append (" LOWER (").append (attribute).append (") LIKE")
                    .append (" LOWER (")
                    .append (getUnicodeString ("%" + searchValue))
                    .append (") ");
            } // else
        } // else if endswith match
        else if (matchType.equals (SQLConstants.MATCH_SOUNDEX)) // SOUNDEX match?
        {
            sqlConditionString = new StringBuffer ()
                .append (" SOUNDEX (").append (attribute)
                .append (") = SOUNDEX (")
                .append (getUnicodeString (searchValue.toString ()))
                .append (") ");
        } // else if SOUNDEX match

        return sqlConditionString;
    } // getQueryConditionString


    /***************************************************************************
     * creates an conditionString for the WHERE-clause in a query, for datatype
     * BOOLEAN. <BR/>
     *
     * @param   attribute   Name of attribute in query to be contrained with
     *                      searchvalue
     * @param   searchValue searchvalue to constrain the resultset via
     *                      matching with attribute (passed as string)
     *
     * @return  String      returns conditionstring for WHERE - Klause without AND
     *                      in the beginning;
     *                      null, if any paramter is null
     */
    public static String getQueryConditionBoolean (String attribute, String searchValue)
    {
        String sqlConditionString = null;

        // check valid call of method
        if (attribute == null || searchValue == null)
                                        // values not valid?
        {
            return null;
        } // if values not valid

        if (searchValue.equals (SQLConstants.BOOLSTR_TRUE))
        {
            sqlConditionString = attribute + SQLHelpers.COMP_EQU + SQLConstants.BOOL_TRUE;
        } // if
        else if (searchValue.equals (SQLConstants.BOOLSTR_FALSE))
        {
            sqlConditionString = attribute + SQLHelpers.COMP_EQU + SQLConstants.BOOL_FALSE;
        } // if
        else                            // invalid bool param
        {
            return null;
        } // else invalid bool param

        return sqlConditionString;
    } // getQueryConditionBoolean



    /***************************************************************************
     * creates an conditionString for the WHERE-clause in a query, for datatype
     * BOOLEAN. <BR/>
     *
     * @param   attribute   Name of attribute in query to be contrained with
     *                      searchvalue
     * @param   searchValue searchvalue to constrain the resultset via
     *                      matching with attribute (passed as integer)
     *
     * @return  String      returns conditionstring for WHERE - Klause without AND
     *                      in the beginning;
     *                      null, if any paramter is null
     *
     * @deprecated  This method is never used.
     */
    public static String getQueryConditionBoolean (String attribute, int searchValue)
    {
        String sqlConditionString = null;

        // check valid call of method
//      if (attribute == null || searchValue == SQLConstants.BOOL_NOTEXISTS)
        if (attribute == null)          // values not valid?
        {
            return null;
        } // if values not valid

        if (searchValue == SQLConstants.BOOL_TRUE)
        {
            sqlConditionString = attribute + SQLHelpers.COMP_EQU + SQLConstants.BOOL_TRUE;
        } // if
        else if (searchValue == SQLConstants.BOOL_FALSE)
        {
            sqlConditionString = attribute + SQLHelpers.COMP_EQU + SQLConstants.BOOL_FALSE;
        } // else if
        else    // invalid bool param
        {
            return null;
        } // else

        return sqlConditionString;
    } // getQueryConditionBoolean


    /***************************************************************************
     * creates an conditionString for the WHERE-clause in a query, for datatype
     * BOOLEAN. <BR/>
     *
     * @param   attribute   Name of attribute in query to be contrained with
     *                      searchvalue
     * @param   searchValue searchvalue to constrain the resultset via
     *                      matching with attribute (passed as boolean)
     *
     * @return  String      returns conditionstring for WHERE - Klause without AND
     *                      in the beginning;
     *                      null, if any paramter is null
     */
    public static String getQueryConditionBoolean (String attribute, boolean searchValue)
    {
        String sqlConditionString = null;

        // check valid call of method
        if (attribute == null)          // values not valid?
        {
            return null;
        } // if values not valid

        if (searchValue)
        {
            sqlConditionString = attribute + SQLHelpers.COMP_EQU + SQLConstants.BOOL_TRUE;
        } // if
        else
        {
            sqlConditionString = attribute + SQLHelpers.COMP_EQU + SQLConstants.BOOL_FALSE;
        } // else

        return sqlConditionString;
    } // getQueryConditionBoolean


    /**************************************************************************
     * Creates a condition String for the WHERE clause in a query, for datatype
     * MSSQL-TEXT and ORACLE-CLOB. <BR/>
     *
     * @param   attribute   Name of attribute in query to be contrained with
     *                      searchvalue
     * @param   matchType   match type for conditionstring between attribute
     *                      and searchvalue
     * @param   searchValue searchvalue to constrain the resultset via
     *                      matching with attribute
     *
     * @return  The condition string for the <CODE>WHERE</CODE> clause without
     *          <CODE>AND</CODE> at the beginning;
     *          <CODE>null</CODE>, if any parameter is <CODE>null</CODE>.
     */
    public static String getQueryConditionLongText (final String attribute,
        final String matchType, final String searchValue)
    {
        String sqlConditionString = null;

        // check valid call of method
        if (attribute == null || matchType == null || searchValue == null)
                                        // values not valid?
        {
            return null;
        } // if values not valid

        // check db type:
        switch (SQLConstants.DB_TYPE)
        {
            case SQLConstants.DB_MSSQL:
                // use normal sql - string condition:
                if (matchType.equals (SQLConstants.MATCH_EXACT)) // exact match
                {
                    sqlConditionString = " " + attribute + " = '" + searchValue + "' ";
                } // if
                else if (matchType.equals (SQLConstants.MATCH_SUBSTRING)) // substring match
                {
                    sqlConditionString = " " + attribute + " LIKE '%" + searchValue + "%' ";
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_STARTSWITH)) // startswith match
                {
                    sqlConditionString = " " + attribute + " LIKE '" + searchValue + "%' ";
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_ENDSWITH)) // endswith match
                {
                    sqlConditionString = " " + attribute + " LIKE '%" + searchValue + "' ";
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_SOUNDEX)) // SOUNDEX match
                {
                    sqlConditionString = " " + attribute + " LIKE '%" + searchValue + "%' ";
                } // else if
                break;

            case SQLConstants.DB_ORACLE:
                // use for CLOBS in ORACLE an special querystring
                if (matchType.equals (SQLConstants.MATCH_EXACT)) // exact match
                {
                    sqlConditionString = " dbms_lob.INSTR (" + attribute + ",'" +
                        searchValue + "', 1, 1) = 1";
                } // if
                else if (matchType.equals (SQLConstants.MATCH_SUBSTRING)) // substring match
                {
                    sqlConditionString = " dbms_lob.INSTR (" + attribute + ",'" +
                        searchValue + "', 1, 1) > 0";
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_STARTSWITH)) // startswith match
                {
                    sqlConditionString = " dbms_lob.INSTR (" + attribute + ",'" +
                        searchValue + "', 1, 1) = 1";
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_ENDSWITH)) // endswith match
                {
                    sqlConditionString = " dbms_lob.INSTR (" + attribute + ",'" +
                        searchValue + "', 1, 1) > 0"; // = LENGTH (attribute) - LENGTH (searchValue)
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_SOUNDEX)) // SOUNDEX match
                {
                    sqlConditionString = " dbms_lob.INSTR (" + attribute + ",'" +
                        searchValue + "', 1, 1) > 0";
                } // else if
                break;

            case SQLConstants.DB_DB2:
                // use normal sql - string condition:
                if (matchType.equals (SQLConstants.MATCH_EXACT)) // exact match
                {
                    sqlConditionString = " " + attribute + " = CLOB ('" + searchValue + "')) ";
                } // if
                else if (matchType.equals (SQLConstants.MATCH_SUBSTRING)) // substring match
                {
                    sqlConditionString = " " + attribute + " LIKE CLOB ('%" + searchValue + "%') ";
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_STARTSWITH)) // startswith match
                {
                    sqlConditionString = " " + attribute + " LIKE CLOB ('%" + searchValue + "') ";
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_ENDSWITH)) // endswith match
                {
                    sqlConditionString = " " + attribute + " LIKE CLOB ('" + searchValue + "%') ";
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_SOUNDEX)) // SOUNDEX match
                {
                    sqlConditionString = " " + attribute + " LIKE CLOB ('%" + searchValue + "%') ";
                } // else if
                break;

            default: // nothing to do
        } // switch

        return sqlConditionString;
    } // getQueryConditionLongText


    /**************************************************************************
     * Creates a condition String for the WHERE clause in a query, for datatype
     * NUMBER/INTEGER. <BR/>
     *
     * @param   attribute   Name of attribute in query to be contrained with
     *                      searchvalue
     * @param   matchType   match type for conditionstring between attribute
     *                      and searchvalue
     * @param   searchValue searchvalue to constrain the resultset via
     *                      matching with attribute
     *
     * @return  The condition string for the <CODE>WHERE</CODE> clause without
     *          <CODE>AND</CODE> at the beginning;
     *          <CODE>null</CODE>, if any parameter is <CODE>null</CODE>.
     *
     * @deprecated  This method is replaced by
     *              {@link #getQueryConditionNumber (StringBuffer, String, StringBuffer)}
     *              (2006-01-20).
     */
    public static String getQueryConditionNumber (final String attribute,
        final String matchType, final String searchValue)
    {
        String sqlConditionString = null;
        String compOp = SQLHelpers.COMP_EQU;          // comparison operator

        // check valid call of method
        if (attribute == null || matchType == null || searchValue == null)
        {
            return null;
        } // if

        if (searchValue.equals (SQLConstants.DB_NULL)) // null value?
        {
            compOp = " IS ";
        } // if null value

        // extend queryString with one condition for type number
        if (matchType.equals (SQLConstants.MATCH_EXACT))     // exact match
        {
            sqlConditionString = " " + attribute + compOp + searchValue + " ";
        } // if
        else if (matchType.equals (SQLConstants.MATCH_GREATER))    // greater
        {
            sqlConditionString = " " + attribute + " > " + searchValue + " ";
        } // else if
        else if (matchType.equals (SQLConstants.MATCH_LESS))  // less
        {
            sqlConditionString = " " + attribute + " < " + searchValue + " ";
        } // else if
        else if (matchType.equals (SQLConstants.MATCH_GREATEREQUAL))  // greater equal
        {
            sqlConditionString = " " + attribute + " >= " + searchValue + " ";
        } // else if
        else if (matchType.equals (SQLConstants.MATCH_LESSEQUAL))  // less equal
        {
            sqlConditionString = " " + attribute + " <= " + searchValue + " ";
        } // else if

        return sqlConditionString;
    } // getQueryConditionNumber


    /**************************************************************************
     * Creates a condition String for the WHERE clause in a query, for datatype
     * NUMBER/INTEGER. <BR/>
     *
     * @param   attribute   Name of attribute in query to be contrained with
     *                      searchvalue
     * @param   matchType   match type for conditionstring between attribute
     *                      and searchvalue
     * @param   searchValue searchvalue to constrain the resultset via
     *                      matching with attribute
     *
     * @return  The condition string for the <CODE>WHERE</CODE> clause without
     *          <CODE>AND</CODE> at the beginning;
     *          <CODE>null</CODE>, if any parameter is <CODE>null</CODE>.
     */
    public static StringBuffer getQueryConditionNumber (final StringBuffer attribute,
        final String matchType, final StringBuffer searchValue)
    {
        StringBuffer sqlConditionString = null;
        StringBuffer compOp = null;     // comparison operator

        // check valid call of method
        if (attribute == null || matchType == null || searchValue == null)
        {
            return null;
        } // if

        if (matchType.equals (SQLConstants.MATCH_IN)) // IN match?
        {
            sqlConditionString = new StringBuffer ()
                .append (attribute)
                .append (" IN (").append (searchValue).append (") ");
        } // else if IN match
        else // no IN match
        {
            // extend queryString with one condition for type number
            if (matchType.equals (SQLConstants.MATCH_EXACT))     // exact match
            {
                if (searchValue.equals (SQLConstants.DB_NULL)) // null value?
                {
                    compOp = new StringBuffer (" IS ");
                } // if null value
                else                        // any other value
                {
                    compOp = new StringBuffer (SQLHelpers.COMP_EQU);
                } // else any other value
            } // if
            else if (matchType.equals (SQLConstants.MATCH_GREATER))    // greater
            {
                compOp = new StringBuffer (" > ");
            } // else if
            else if (matchType.equals (SQLConstants.MATCH_LESS))  // less
            {
                compOp = new StringBuffer (" < ");
            } // else if
            else if (matchType.equals (SQLConstants.MATCH_GREATEREQUAL))  // greater equal
            {
                compOp = new StringBuffer (" >= ");
            } // else if
            else if (matchType.equals (SQLConstants.MATCH_LESSEQUAL))  // less equal
            {
                compOp = new StringBuffer (" <= ");
            } // else if


            // check if we found a relevant comparison operator:
            if (compOp != null)
            {
                sqlConditionString = new StringBuffer (" ")
                    .append (attribute).append (compOp)
                    .append (searchValue).append (" ");
            } // if
        } // else // no IN match

        // return the result:
        return sqlConditionString;
    } // getQueryConditionNumber


    /**************************************************************************
     * Creates a condition String for the WHERE clause in a query, for datatype
     * MONEY. <BR/>
     *
     * @param   attribute   Name of attribute in query to be contrained with
     *                      searchvalue
     * @param   matchType   match type for conditionstring between attribute
     *                      and searchvalue
     * @param   searchValue searchvalue to constrain the resultset via
     *                      matching with attribute
     *
     * @return  The condition string for the <CODE>WHERE</CODE> clause without
     *          <CODE>AND</CODE> at the beginning;
     *          <CODE>null</CODE>, if any parameter is <CODE>null</CODE>.
     *
     * @deprecated  This method is replaced by
     *              {@link #getQueryConditionMoney (StringBuffer, String, StringBuffer)}
     *              (2006-01-20).
     */
    public static String getQueryConditionMoney (final String attribute,
        final String matchType, final String searchValue)
    {
        String sqlConditionString = null;

        // check valid call of method
        if (attribute == null || matchType == null || searchValue == null)
                                        // values not valid?
        {
            return null;
        } // if values not valid

        float floatSearchVal = Helpers.stringToMoney (searchValue);


// HACK AJ sorry no time left : (
// stringToMoney returns the value * 10000 and MSSQL converts this automatically
// to the moneyformat of MSSQL; ORACLE and DB2 leave the value
        if (SQLConstants.DB_TYPE == SQLConstants.DB_MSSQL)
        {
            floatSearchVal = floatSearchVal / 10000;
        } // if DB_MSSQL
// HACK END

        // extend queryString with one condition for type number
        if (matchType.equals (SQLConstants.MATCH_EXACT))     // exact match
        {
            sqlConditionString = " " + attribute + SQLHelpers.COMP_EQU + floatSearchVal + " ";
        } // if
        else if (matchType.equals (SQLConstants.MATCH_GREATER))    // greater
        {
            sqlConditionString = " " + attribute + " > " + floatSearchVal + " ";
        } // else if
        else if (matchType.equals (SQLConstants.MATCH_LESS))  // less
        {
            sqlConditionString = " " + attribute + " < " + floatSearchVal + " ";
        } // else if
        else if (matchType.equals (SQLConstants.MATCH_GREATEREQUAL))  // greater equal
        {
            sqlConditionString = " " + attribute + " >= " + floatSearchVal + " ";
        } // else if
        else if (matchType.equals (SQLConstants.MATCH_LESSEQUAL))  // less equal
        {
            sqlConditionString = " " + attribute + " <= " + floatSearchVal + " ";
        } // else if

        return sqlConditionString;
    } // getQueryConditionMoney


    /**************************************************************************
     * Creates a condition String for the WHERE clause in a query, for datatype
     * MONEY. <BR/>
     *
     * @param   attribute   Name of attribute in query to be contrained with
     *                      searchvalue
     * @param   matchType   match type for conditionstring between attribute
     *                      and searchvalue
     * @param   searchValue searchvalue to constrain the resultset via
     *                      matching with attribute
     *
     * @return  The condition string for the <CODE>WHERE</CODE> clause without
     *          <CODE>AND</CODE> at the beginning;
     *          <CODE>null</CODE>, if any parameter is <CODE>null</CODE>.
     */
    public static StringBuffer getQueryConditionMoney (
                                               final StringBuffer attribute,
                                               final String matchType,
                                               final StringBuffer searchValue)
    {
        StringBuffer sqlConditionString = null;
        StringBuffer compOp = null;     // comparison operator
        float floatSearchVal = 0;

        // check valid call of method
        if (attribute == null || matchType == null || searchValue == null)
                                        // values not valid?
        {
            return null;
        } // if values not valid

        // check if we don't search for null:
        if (!searchValue.equals (SQLConstants.DB_NULL))
        {
            floatSearchVal = Helpers.stringToMoney (searchValue.toString ());

// HACK AJ sorry no time left : (
// stringToMoney returns the value * 10000 and MSSQL converts this automatically
// to the moneyformat of MSSQL; ORACLE and DB2 leave the value
            if (SQLConstants.DB_TYPE == SQLConstants.DB_MSSQL)
            {
                floatSearchVal = floatSearchVal / 10000;
            } // if DB_MSSQL
// HACK END
        } // if

        // extend queryString with one condition for type number
        if (matchType.equals (SQLConstants.MATCH_EXACT))     // exact match
        {
            if (searchValue.equals (SQLConstants.DB_NULL)) // null value?
            {
                compOp = new StringBuffer (" IS ");
                sqlConditionString = new StringBuffer (" ")
                    .append (attribute).append (compOp)
                    .append (searchValue).append (" ");
            } // if null value
            else                        // any other value
            {
                compOp = new StringBuffer (SQLHelpers.COMP_EQU);
            } // else any other value
        } // if
        else if (matchType.equals (SQLConstants.MATCH_GREATER))    // greater
        {
            compOp = new StringBuffer (" > ");
        } // else if
        else if (matchType.equals (SQLConstants.MATCH_LESS))  // less
        {
            compOp = new StringBuffer (" < ");
        } // else if
        else if (matchType.equals (SQLConstants.MATCH_GREATEREQUAL))  // greater equal
        {
            compOp = new StringBuffer (" >= ");
        } // else if
        else if (matchType.equals (SQLConstants.MATCH_LESSEQUAL))  // less equal
        {
            compOp = new StringBuffer (" <= ");
        } // else if

        // check if we found a relevant comparison operator:
        if (compOp != null && sqlConditionString == null)
        {
            sqlConditionString = new StringBuffer (" ")
                .append (attribute).append (compOp)
                .append (floatSearchVal).append (" ");
        } // if

        // return the result:
        return sqlConditionString;
    } // getQueryConditionMoney


    /**************************************************************************
     * Creates a condition String for the WHERE-clause in a query, for datatype
     * DATE. <BR/>
     *
     * @param   attribute   Name of attribute in query to be contrained with
     *                      searchvalue
     * @param   matchType   match type for conditionstring between attribute
     *                      and searchvalue
     * @param   searchValue searchvalue to constrain the resultset via
     *                      matching with attribute in format 'DD.MM.YYYY'
     *
     * @return  The condition string for the <CODE>WHERE</CODE> clause without
     *          <CODE>AND</CODE> at the beginning;
     *          <CODE>null</CODE>, if any parameter is <CODE>null</CODE>.
     */
    public static String getQueryConditionDate (final String attribute,
        final String matchType, final String searchValue)
    {
        String sqlConditionString = null;
        StringBuffer searchValueString;

        // check valid call of method
        if (attribute == null || matchType == null || searchValue == null)
                                          // values not valid?
        {
            return null;
        } // if values not valid

        searchValueString = SQLHelpers.getDateString (
            new StringBuffer (searchValue), SQLHelpers.TIMEDAYSTART)
            .append (" ");

        // check db type:
        switch (SQLConstants.DB_TYPE)
        {
            case SQLConstants.DB_MSSQL:
            case SQLConstants.DB_DB2:
                // check MATCHTYPES
                if (matchType.equals (SQLConstants.MATCH_EXACT))     // exact match
                {
                    sqlConditionString = " " + attribute +
                        SQLHelpers.COMP_EQU + searchValueString;
                } // if
                else if (matchType.equals (SQLConstants.MATCH_GREATER))    // greater
                {
                    sqlConditionString = " " + attribute +
                        " > " + searchValueString;
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_LESS))  // less
                {
                    sqlConditionString = " " + attribute +
                        " < " + searchValueString;
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_GREATEREQUAL))  // greater equal
                {
                    sqlConditionString = " " + attribute +
                        " >= " + searchValueString;
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_LESSEQUAL))  // less equal
                {
                    sqlConditionString = " " + attribute +
                        " <= " + searchValueString;
                } // else if
                break;

            case SQLConstants.DB_ORACLE:
                if (matchType.equals (SQLConstants.MATCH_EXACT))     // exact match
                {
                    sqlConditionString = " " + attribute + " - " +
                        searchValueString + " = 0 ";
                } // if
                else if (matchType.equals (SQLConstants.MATCH_GREATER))    // greater
                {
                    sqlConditionString = " " + attribute + " - " +
                        searchValueString + " > 0 ";
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_LESS))  // less
                {
                    sqlConditionString = " " + attribute + " - " +
                        searchValueString + " < 0 ";
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_GREATEREQUAL))  // greater equal
                {
                    sqlConditionString = " " + attribute + " - " +
                        searchValueString + " >= 0 ";
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_LESSEQUAL))  // less equal
                {
                    sqlConditionString = " " + attribute + " - " +
                        searchValueString + " <= 0 ";
                } // else if
                break;

            default: // nothing to do
        } // switch

        return sqlConditionString;
    } // getQueryConditionDate


    /***************************************************************************
     * Creates a condition String for the WHERE-clause in a query, for datatype
     * TIME. <BR/>
     *
     * @param   attribute   Name of attribute in query to be contrained with
     *                      searchvalue
     * @param   matchType   match type for conditionstring between attribute
     *                      and searchvalue
     * @param   searchValue searchvalue to constrain the resultset via
     *                      matching with attribute in format 'HH24:MI'
     *
     * @return  The condition string for the <CODE>WHERE</CODE> clause without
     *          <CODE>AND</CODE> at the beginning;
     *          <CODE>null</CODE>, if any parameter is <CODE>null</CODE>.
     *
     * @deprecated  This method is replaced by
     *              {@link #getQueryConditionTime (StringBuffer, String, StringBuffer)}
     *              (2006-01-20).
     */
    public static String getQueryConditionTime (final String attribute,
        final String matchType, final String searchValue)
    {
        String sqlConditionString = null;
        StringBuffer searchValueString;
        StringBuffer attributeString;

        // check valid call of method
        if (attribute == null || matchType == null || searchValue == null)
                                          // values not valid?
        {
            return null;
        } // if values not valid

        searchValueString = SQLHelpers.getDateString (SQLHelpers.DATESTART,
            new StringBuffer (searchValue)).append (" ");

        // check db type:
        switch (SQLConstants.DB_TYPE)
        {
            case SQLConstants.DB_MSSQL:
            case SQLConstants.DB_DB2:
                attributeString =
                    new StringBuffer (" CONVERT (datetime, '01.01.1970 ' + ")
                        .append ("STR (DATEPART (hour, " + attribute + ")) + ':' + ")
                        .append ("STR (DATEPART (minute, " + attribute + ")))");

                // check MATCHTYPES
                if (matchType.equals (SQLConstants.MATCH_EXACT))     // exact match
                {
                    sqlConditionString = " " + attributeString +
                        SQLHelpers.COMP_EQU + searchValueString;
                } // if
                else if (matchType.equals (SQLConstants.MATCH_GREATER))    // greater
                {
                    sqlConditionString = " " + attributeString +
                        " > " + searchValueString;
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_LESS))  // less
                {
                    sqlConditionString = " " + attributeString +
                        " < " + searchValueString;
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_GREATEREQUAL))  // greater equal
                {
                    sqlConditionString = " " + attributeString +
                        " >= " + searchValueString;
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_LESSEQUAL))  // less equal
                {
                    sqlConditionString = " " + attributeString +
                        " <= " + searchValueString;
                } // else if
                break;

            case SQLConstants.DB_ORACLE:
////////////////////////////////////////////////////////////////////////////////
// BB 25.07.2003: missing feature! could not have been developed due to missing
//                oracle database environment
////////////////////////////////////////////////////////////////////////////////
                attributeString = new StringBuffer ("");

                if (matchType.equals (SQLConstants.MATCH_EXACT))     // exact match
                {
                    sqlConditionString = " " + attribute + " - " +
                        searchValueString + " = 0 ";
                } // if
                else if (matchType.equals (SQLConstants.MATCH_GREATER))    // greater
                {
                    sqlConditionString = " " + attribute + " - " +
                        searchValueString + " > 0 ";
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_LESS))  // less
                {
                    sqlConditionString = " " + attribute + " - " +
                        searchValueString + " < 0 ";
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_GREATEREQUAL))  // greater equal
                {
                    sqlConditionString = " " + attribute + " - " +
                        searchValueString + " >= 0 ";
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_LESSEQUAL))  // less equal
                {
                    sqlConditionString = " " + attribute + " - " +
                        searchValueString + " <= 0 ";
                } // else if
                break;

            default: // nothing to do
        } // switch

        return sqlConditionString;
    } // getQueryConditionTime


    /***************************************************************************
     * Creates a condition String for the WHERE-clause in a query, for datatype
     * TIME. <BR/>
     *
     * @param   attribute   Name of attribute in query to be contrained with
     *                      searchvalue
     * @param   matchType   match type for conditionstring between attribute
     *                      and searchvalue
     * @param   searchValue searchvalue to constrain the resultset via
     *                      matching with attribute in format 'HH24:MI'
     *
     * @return  The condition string for the <CODE>WHERE</CODE> clause without
     *          <CODE>AND</CODE> at the beginning;
     *          <CODE>null</CODE>, if any parameter is <CODE>null</CODE>.
     */
    public static StringBuffer getQueryConditionTime (
                                              final StringBuffer attribute,
                                              final String matchType,
                                              final StringBuffer searchValue)
    {
        StringBuffer sqlConditionString = null;
        StringBuffer searchValueString;
        StringBuffer attributeString;
        StringBuffer compOp = null;     // comparison operator

        // check valid call of method
        if (attribute == null || matchType == null || searchValue == null)
                                          // values not valid?
        {
            return null;
        } // if values not valid

        searchValueString = SQLHelpers.getDateString (SQLHelpers.DATESTART, searchValue)
            .append (" ");

        // check db type:
        switch (SQLConstants.DB_TYPE)
        {
            case SQLConstants.DB_MSSQL:
            case SQLConstants.DB_DB2:
                attributeString =
                    new StringBuffer (" CONVERT (datetime, '01.01.1970 ' + ")
                        .append ("STR (DATEPART (hour, " + attribute + ")) + ':' + ")
                        .append ("STR (DATEPART (minute, " + attribute + ")))");

                // check MATCHTYPES
                if (matchType.equals (SQLConstants.MATCH_EXACT))     // exact match
                {
                    compOp = new StringBuffer (SQLHelpers.COMP_EQU);
                } // if
                else if (matchType.equals (SQLConstants.MATCH_GREATER))    // greater
                {
                    compOp = new StringBuffer (" > ");
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_LESS))  // less
                {
                    compOp = new StringBuffer (" < ");
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_GREATEREQUAL))  // greater equal
                {
                    compOp = new StringBuffer (" >= ");
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_LESSEQUAL))  // less equal
                {
                    compOp = new StringBuffer (" <= ");
                } // else if

                // check if we found a relevant comparison operator:
                if (compOp != null)
                {
                    sqlConditionString = new StringBuffer (" ")
                        .append (attributeString).append (compOp)
                        .append (searchValueString);
                } // if
                break;

            case SQLConstants.DB_ORACLE:
////////////////////////////////////////////////////////////////////////////////
// BB 25.07.2003: missing feature! could not have been developed due to missing
//                oracle database environment
////////////////////////////////////////////////////////////////////////////////
                attributeString = new StringBuffer ("");

                if (matchType.equals (SQLConstants.MATCH_EXACT))     // exact match
                {
                    compOp = new StringBuffer (" = 0 ");
                } // if
                else if (matchType.equals (SQLConstants.MATCH_GREATER))    // greater
                {
                    compOp = new StringBuffer (" > 0 ");
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_LESS))  // less
                {
                    compOp = new StringBuffer (" < 0 ");
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_GREATEREQUAL))  // greater equal
                {
                    compOp = new StringBuffer (" >= 0 ");
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_LESSEQUAL))  // less equal
                {
                    compOp = new StringBuffer (" < 0 ");
                } // else if

                // check if we found a relevant comparison operator:
                if (compOp != null)
                {
                    sqlConditionString = new StringBuffer (" ")
                        .append (attributeString).append (" - ")
                        .append (searchValueString).append (compOp);
                } // if
                break;

            default: // nothing to do
        } // switch

        return sqlConditionString;
    } // getQueryConditionTime


    /**************************************************************************
     * Creates a condition String for the WHERE clause in a query, for datatype
     * DATETIME. <BR/>
     *
     * @param   attribute   Name of attribute in query to be contrained with
     *                      searchvalue
     * @param   matchType   match type for conditionstring between attribute
     *                      and searchvalue
     * @param   searchValue searchvalue to constrain the resultset via
     *                      matching with attribute in format:
     *                      'DD.MM.YYYY HH24:MI:SS'
     *
     * @return  The condition string for the <CODE>WHERE</CODE> clause without
     *          <CODE>AND</CODE> at the beginning;
     *          <CODE>null</CODE>, if any parameter is <CODE>null</CODE>.
     *
     * @deprecated  This method is replaced by
     *              {@link #getQueryConditionDateTime (StringBuffer, String, StringBuffer)}.
     */
    public static String getQueryConditionDateTime (final String attribute,
        final String matchType, final String searchValue)
    {
        String sqlConditionString = null;
        StringBuffer searchValueString;

        // check valid call of method
        if (attribute == null || matchType == null || searchValue == null)
                                        // values not valid?
        {
            return null;
        } // if values not valid

        searchValueString = SQLHelpers.getDateString (
            new StringBuffer (searchValue)).append (" ");

        // check db type:
        switch (SQLConstants.DB_TYPE)
        {
            case SQLConstants.DB_MSSQL:
            case SQLConstants.DB_DB2:
                // check MATCHTYPES
                if (matchType.equals (SQLConstants.MATCH_EXACT)) // exact match
                {
                    sqlConditionString = " " + attribute +
                        SQLHelpers.COMP_EQU + searchValueString;
                } // if
                else if (matchType.equals (SQLConstants.MATCH_GREATER)) // greater
                {
                    sqlConditionString = " " + attribute +
                        " > " + searchValueString;
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_LESS))  // less
                {
                    sqlConditionString = " " + attribute +
                        " < " + searchValueString;
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_GREATEREQUAL))  // greater equal
                {
                    sqlConditionString = " " + attribute +
                        " >= " + searchValueString;
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_LESSEQUAL))  // less equal
                {
                    sqlConditionString = " " + attribute +
                        " <= " + searchValueString;
                } // else if
                break;

            case SQLConstants.DB_ORACLE:
                if (matchType.equals (SQLConstants.MATCH_EXACT)) // exact match
                {
                    sqlConditionString = " " + attribute + " - " +
                        searchValueString + " = 0 ";
                } // if
                else if (matchType.equals (SQLConstants.MATCH_GREATER))    // greater
                {
                    sqlConditionString = " " + attribute + " - " +
                        searchValueString + " > 0 ";
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_LESS))  // less
                {
                    sqlConditionString = " " + attribute + " - " +
                        searchValueString + " < 0 ";
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_GREATEREQUAL))  // greater equal
                {
                    sqlConditionString = " " + attribute + " - " +
                        searchValueString + " >= 0 ";
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_LESSEQUAL))  // less equal
                {
                    sqlConditionString = " " + attribute + " - " +
                        searchValueString + " <= 0 ";
                } // else if
                break;

            default: // nothing to do
        } // switch

        return sqlConditionString;
    } // getQueryConditionDateTime


    /**************************************************************************
     * Creates a condition String for the WHERE clause in a query, for datatype
     * DATETIME. <BR/>
     *
     * @param   attribute   Name of attribute in query to be contrained with
     *                      searchvalue
     * @param   matchType   match type for conditionstring between attribute
     *                      and searchvalue
     * @param   searchValue searchvalue to constrain the resultset via
     *                      matching with attribute in format:
     *                      'DD.MM.YYYY HH24:MI:SS'
     *
     * @return  The condition string for the <CODE>WHERE</CODE> clause without
     *          <CODE>AND</CODE> at the beginning;
     *          <CODE>null</CODE>, if any parameter is <CODE>null</CODE>.
     *
     * @see #DATETIMEFORMAT
     */
    public static StringBuffer getQueryConditionDateTime (
                                                          final StringBuffer attribute,
                                                          final String matchType,
                                                          final StringBuffer searchValue)
    {
        StringBuffer sqlConditionString = null;
        StringBuffer searchValueString;

        // check valid call of method
        if (attribute == null || matchType == null || searchValue == null)
                                        // values not valid?
        {
            return null;
        } // if values not valid

        searchValueString = SQLHelpers.getDateString (searchValue).append (" ");

        switch (SQLConstants.DB_TYPE)
        {
            case SQLConstants.DB_MSSQL:
            case SQLConstants.DB_DB2:
                // check MATCHTYPES
                if (matchType.equals (SQLConstants.MATCH_EXACT)) // exact match
                {
                    sqlConditionString = new StringBuffer (" ")
                        .append (attribute).append (SQLHelpers.COMP_EQU)
                        .append (searchValueString);
                } // if
                else if (matchType.equals (SQLConstants.MATCH_GREATER)) // greater
                {
                    sqlConditionString = new StringBuffer (" ")
                        .append (attribute).append (" > ")
                        .append (searchValueString);
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_LESS))  // less
                {
                    sqlConditionString = new StringBuffer (" ")
                        .append (attribute).append (" < ")
                        .append (searchValueString);
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_GREATEREQUAL))  // greater equal
                {
                    sqlConditionString = new StringBuffer (" ")
                        .append (attribute).append (" >= ")
                        .append (searchValueString);
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_LESSEQUAL))  // less equal
                {
                    sqlConditionString = new StringBuffer (" ")
                        .append (attribute).append (" <= ")
                        .append (searchValueString);
                } // else if
                break;

            case SQLConstants.DB_ORACLE:
                sqlConditionString = new StringBuffer (" ")
                    .append (attribute).append (" - ").append (searchValueString);

                if (matchType.equals (SQLConstants.MATCH_EXACT)) // exact match
                {
                    sqlConditionString.append (" = 0 ");
                } // if
                else if (matchType.equals (SQLConstants.MATCH_GREATER))    // greater
                {
                    sqlConditionString.append (" > 0 ");
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_LESS))  // less
                {
                    sqlConditionString.append (" < 0 ");
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_GREATEREQUAL))  // greater equal
                {
                    sqlConditionString.append (" >= 0 ");
                } // else if
                else if (matchType.equals (SQLConstants.MATCH_LESSEQUAL))  // less equal
                {
                    sqlConditionString.append (" <= 0 ");
                } // else if
                break;

            default: // nothing to do
        } // switch

        return sqlConditionString;
    } // getQueryConditionDateTime


    /**************************************************************************
     * Creates a condition String for the WHERE clause in a query, for comparing
     * an attribute with datatype STRING/VARCHAR2. <BR/>
     *
     * @param   attribute   Name of attribute in query to be contrained with
     *                      searchvalue
     * @param   matchType   match type for conditionstring between attribute
     *                      and searchvalue
     * @param   searchAttribute search attribute to constrain the resultset via
     *                      matching with attribute
     * @param   caseSensitive   Shall the check be done in a case-sensitive
     *                      manner or not?
     *
     * @return  The condition string for the <CODE>WHERE</CODE> clause without
     *          <CODE>AND</CODE> at the beginning;
     *          <CODE>null</CODE>, if any parameter is <CODE>null</CODE>.
     */
    public static StringBuffer getQueryConditionAttribute (
                                                           final String attribute,
                                                           final String matchType,
                                                           final String searchAttribute,
                                                           final boolean caseSensitive)
    {
        // call the common method and return the result:
        return SQLHelpers.getQueryConditionAttribute (
            new StringBuffer ().append (attribute), matchType,
            new StringBuffer ().append (searchAttribute), caseSensitive);
    } // getQueryConditionAttribute


    /**************************************************************************
     * Creates a condition String for the WHERE clause in a query, for comparing
     * an attribute with datatype STRING/VARCHAR2. <BR/>
     *
     * @param   attribute   Name of attribute in query to be contrained with
     *                      searchvalue
     * @param   matchType   match type for conditionstring between attribute
     *                      and searchvalue
     * @param   searchAttribute search attribute to constrain the resultset via
     *                      matching with attribute
     * @param   caseSensitive   Shall the check be done in a case-sensitive
     *                      manner or not?
     *
     * @return  The condition string for the <CODE>WHERE</CODE> clause without
     *          <CODE>AND</CODE> at the beginning;
     *          <CODE>null</CODE>, if any parameter is <CODE>null</CODE>.
     */
    public static StringBuffer getQueryConditionAttribute (
                                                           final StringBuffer attribute,
                                                           final String matchType,
                                                           final StringBuffer searchAttribute,
                                                           final boolean caseSensitive)
    {
        StringBuffer sqlConditionString = null;

        // check valid call of method
        if (attribute == null || matchType == null || searchAttribute == null)
                                        // values not valid?
        {
            return null;
        } // if values not valid

        // check match type:
        if (matchType.equals (SQLConstants.MATCH_EXACT)) // exact match?
        {
            if (caseSensitive)
            {
                sqlConditionString = new StringBuffer (" ")
                    .append (attribute).append (SQLHelpers.COMP_EQU)
                    .append (searchAttribute).append (" ");
            } // if
            else
            {
                sqlConditionString = new StringBuffer ()
                    .append (" LOWER (").append (attribute).append (") =")
                    .append (" LOWER (").append (searchAttribute).append (") ");
            } // else
        } // if exact match
        else if (matchType.equals (SQLConstants.MATCH_SUBSTRING))
                                        // substring match?
        {
            if (caseSensitive)
            {
                sqlConditionString = new StringBuffer (" ")
                    .append (attribute)
                    .append (" LIKE ")
                    .append (
                        SQLHelpers.getStrCat (
                            SQLHelpers.getStrCat (new StringBuffer ("'%'"), searchAttribute),
                            "'%' "));
            } // if
            else
            {
                sqlConditionString = new StringBuffer ()
                    .append (" LOWER (").append (attribute).append (") LIKE")
                    .append (" LOWER (")
                    .append (
                        SQLHelpers.getStrCat (
                            SQLHelpers.getStrCat (new StringBuffer ("'%'"), searchAttribute),
                            "'%'"))
                    .append (") ");
            } // else
        } // else if substring match
        else if (matchType.equals (SQLConstants.MATCH_STARTSWITH))
                                        // startswith match?
        {
            if (caseSensitive)
            {
                sqlConditionString = new StringBuffer (" ")
                    .append (attribute)
                    .append (" LIKE ")
                    .append (SQLHelpers.getStrCat (searchAttribute, "'%' "));
            } // if
            else
            {
                sqlConditionString = new StringBuffer ()
                    .append (" LOWER (").append (attribute).append (") LIKE")
                    .append (" LOWER (")
                    .append (SQLHelpers.getStrCat (searchAttribute, "'%'"))
                    .append (") ");
            } // else
        } // else if startswith match
        else if (matchType.equals (SQLConstants.MATCH_ENDSWITH))
                                        // endswith match?
        {
            if (caseSensitive)
            {
                sqlConditionString = new StringBuffer (" ")
                    .append (attribute)
                    .append (" LIKE ")
                    .append (
                        SQLHelpers.getStrCat (new StringBuffer ("'%'"), searchAttribute));
            } // if
            else
            {
                sqlConditionString = new StringBuffer ()
                    .append (" LOWER (").append (attribute).append (") LIKE")
                    .append (" LOWER (")
                    .append (
                        SQLHelpers.getStrCat (new StringBuffer ("'%'"), searchAttribute))
                    .append (") ");
            } // else
        } // else if endswith match
        else if (matchType.equals (SQLConstants.MATCH_SOUNDEX)) // SOUNDEX match?
        {
            sqlConditionString = new StringBuffer ()
                .append (" SOUNDEX (").append (attribute)
                .append (") = SOUNDEX (").append (searchAttribute).append (") ");
        } // else if SOUNDEX match

        return sqlConditionString;
    } // getQueryConditionAttribute


    /**************************************************************************
     * Map a BO match type to the corresponding SQL match type. <BR/>
     * The BO match types may be found in <CODE>ibs.bo.BOConstants</CODE> and
     * start with MATCH_... <BR/>
     * e.g. {@link ibs.bo.BOConstants#MATCH_EXACT MATCH_EXACT}. <BR/>
     * The SQL match types may be found in <CODE>ibs.tech.sql.SQLConstants</CODE> and
     * start with MATCH_... <BR/>
     * e.g. {@link ibs.tech.sql.SQLConstants#MATCH_EXACT MATCH_EXACT}.
     *
     * @param   boMatchType The bo match type.
     *
     * @return  The sql match type or {@link SQLConstants#MATCH_NONE MATCH_NONE}
     *          if there was not corresponding match type found.
     */
    public static String mapBO2SQLMatchType (final String boMatchType)
    {
        String sqlMatchType = SQLConstants.MATCH_NONE; // the match type

        if (boMatchType.equals (BOConstants.MATCH_EXACT))
                                        // attribute value matches searchstring
                                        // exactly?
        {
            sqlMatchType = SQLConstants.MATCH_EXACT;
        } // if attribute value matches searchstring exactly
        else if (boMatchType.equals (BOConstants.MATCH_SUBSTRING))
                                        // searchstring is somewhere in
                                        // attribute value?
        {
            sqlMatchType = SQLConstants.MATCH_SUBSTRING;
        } // else if searchstring is somewhere in attribute value
        else if (boMatchType.equals (BOConstants.MATCH_STARTSWITH))
                                        // searchstring is at the start of
                                        // attribute value?
        {
            sqlMatchType = SQLConstants.MATCH_STARTSWITH;
        } // else if searchstring is at the start of attribute value
        else if (boMatchType.equals (BOConstants.MATCH_ENDSWITH))
                                        // searchstring is at the end of
                                        // attribute value?
        {
            sqlMatchType = SQLConstants.MATCH_ENDSWITH;
        } // else if searchstring is at the end of attribute value
        else if (boMatchType.equals (BOConstants.MATCH_SOUNDEX))
                                        // attribute value sounds like
                                        // searchstring?
        {
            sqlMatchType = SQLConstants.MATCH_SOUNDEX;
        } // else if attribute value sounds like searchstring?
        else if (boMatchType.equals (BOConstants.MATCH_GREATER))
                                        // attribute value is bigger than
                                        // searchstring?
        {
            sqlMatchType = SQLConstants.MATCH_GREATER;
        } // else if attribute value is bigger than searchstring
        else if (boMatchType.equals (BOConstants.MATCH_GREATEREQUAL))
                                        // attribute value is bigger or equal
                                        // than searchstring?
        {
            sqlMatchType = SQLConstants.MATCH_GREATEREQUAL;
        } // else if attribute value is bigger or equal than searchstring
        else if (boMatchType.equals (BOConstants.MATCH_LESS))
                                        // attribute value is less
                                        // than searchstring?
        {
            sqlMatchType = SQLConstants.MATCH_LESS;
        } // else if attribute value is less than searchstring
        else if (boMatchType.equals (BOConstants.MATCH_LESSEQUAL))
                                        // attribute value is less or equal
                                        // than searchstring?
        {
            sqlMatchType = SQLConstants.MATCH_LESSEQUAL;
        } // else if attribute value is less or equal than searchstring

        // return the match type:
        return sqlMatchType;
    } // mapBO2SQLMatchType


    /**************************************************************************
     * Map a SQL match type to the corresponding BO match type. <BR/>
     * The BO match types may be found in <CODE>ibs.bo.BOConstants</CODE> and
     * start with MATCH_... <BR/>
     * e.g. {@link ibs.bo.BOConstants#MATCH_EXACT MATCH_EXACT}. <BR/>
     * The SQL match types may be found in <CODE>ibs.tech.sql.SQLConstants</CODE> and
     * start with MATCH_... <BR/>
     * e.g. {@link ibs.tech.sql.SQLConstants#MATCH_EXACT MATCH_EXACT}.
     *
     * @param   sqlMatchType    The SQL match type.
     *
     * @return  The SQL match type or {@link BOConstants#MATCH_NONE MATCH_NONE}
     *          if there was not corresponding match type found.
     */
    public static String mapSQL2BOMatchType (final String sqlMatchType)
    {
        String boMatchType = SQLConstants.MATCH_NONE; // the match type

        if (sqlMatchType.equals (SQLConstants.MATCH_EXACT))
                                        // attribute value matches searchstring
                                        // exactly?
        {
            boMatchType = BOConstants.MATCH_EXACT;
        } // if attribute value matches searchstring exactly
        else if (sqlMatchType.equals (SQLConstants.MATCH_SUBSTRING))
                                        // searchstring is somewhere in
                                        // attribute value?
        {
            boMatchType = BOConstants.MATCH_SUBSTRING;
        } // else if searchstring is somewhere in attribute value
        else if (sqlMatchType.equals (SQLConstants.MATCH_STARTSWITH))
                                        // searchstring is at the start of
                                        // attribute value?
        {
            boMatchType = BOConstants.MATCH_STARTSWITH;
        } // else if searchstring is at the start of attribute value
        else if (sqlMatchType.equals (SQLConstants.MATCH_ENDSWITH))
                                        // searchstring is at the end of
                                        // attribute value?
        {
            boMatchType = BOConstants.MATCH_ENDSWITH;
        } // else if searchstring is at the end of attribute value
        else if (sqlMatchType.equals (SQLConstants.MATCH_SOUNDEX))
                                        // attribute value sounds like
                                        // searchstring?
        {
            boMatchType = BOConstants.MATCH_SOUNDEX;
        } // else if attribute value sounds like searchstring?
        else if (sqlMatchType.equals (SQLConstants.MATCH_GREATER))
                                        // attribute value is bigger than
                                        // searchstring?
        {
            boMatchType = BOConstants.MATCH_GREATER;
        } // else if attribute value is bigger than searchstring
        else if (sqlMatchType.equals (SQLConstants.MATCH_GREATEREQUAL))
                                        // attribute value is bigger or equal
                                        // than searchstring?
        {
            boMatchType = BOConstants.MATCH_GREATEREQUAL;
        } // else if attribute value is bigger or equal than searchstring
        else if (sqlMatchType.equals (SQLConstants.MATCH_LESS))
                                        // attribute value is less
                                        // than searchstring?
        {
            boMatchType = BOConstants.MATCH_LESS;
        } // else if attribute value is less than searchstring
        else if (sqlMatchType.equals (SQLConstants.MATCH_LESSEQUAL))
                                        // attribute value is less or equal
                                        // than searchstring?
        {
            boMatchType = BOConstants.MATCH_LESSEQUAL;
        } // else if attribute value is less or equal than searchstring

        // return the match type:
        return boMatchType;
    } // mapSQL2BOMatchType


    /**************************************************************************
     * Get the dummy table for the current database and concatenates a string
     * if there exists a dummy table. <BR/>
     * For oracle this means <CODE>" &lt;concatStr&gt;SYS.DUAL "</CODE>.
     * For SQL Server the result is <CODE>" "</CODE>. <BR/>
     * The concatenation string is not checked in any way. If it is incorrect
     * the database has to handle this exception.
     *
     * @param   concatStr   Concatenation string.
     *
     * @return  The name of the table concatenated with the concatenation string;
     *          <CODE>" "</CODE>, if there exists no such table for the current
     *          database;
     *          <CODE>""</CODE>, if there occurred an error.
     */
    public static StringBuffer getDummyTable (final String concatStr)
    {
        final StringBuffer retValue = new StringBuffer (); // the return value

        // check db type:
        switch (SQLConstants.DB_TYPE)
        {
            case SQLConstants.DB_MSSQL:
                retValue.append (" ");
                break;

            case SQLConstants.DB_ORACLE:
                retValue.append (" ").append (concatStr).append (" SYS.DUAL ");
                break;

            case SQLConstants.DB_DB2:
                retValue.append (" ").append (concatStr).append (" SYSIBM.SYSDUMMY1 ");
                break;

            default: // nothing to do
        } // switch

        // return the result:
        return retValue;
    } // getDummyTable


    /**************************************************************************
     * Get database dependent concatenation of two strings. <BR/>
     * For usage in <CODE>SELECT</CODE>, <CODE>WHERE</CODE>, <CODE>SET</CODE>
     * parts of SELECT, UPDATE, etc. clauses. <BR/>
     * If one of the strings is <CODE>null</CODE> the result is the other
     * string.
     *
     * @param   str1        The first string.
     * @param   str2        The second string to be appended to the first
     *                      string.
     *
     * @return  The computation string for concatenation for the
     *          <CODE>SELECT</CODE> or <CODE>WHERE</CODE> clause;
     *          {@link ibs.tech.sql.SQLConstants#DB_NULL DB_NULL}, if both strings
     *          are <CODE>null</CODE>.
     */
    public static StringBuffer getStrCat (final String str1, final String str2)
    {
        // call common method and return the result:
        return SQLHelpers.getStrCat (
            new StringBuffer ().append (str1), new StringBuffer ()
                .append (str2));
    } // getStrCat


    /**************************************************************************
     * Get database dependent concatenation of two strings. <BR/>
     * For usage in <CODE>SELECT</CODE>, <CODE>WHERE</CODE>, <CODE>SET</CODE>
     * parts of SELECT, UPDATE, etc. clauses. <BR/>
     * If one of the strings is <CODE>null</CODE> the result is the other
     * string. <BR/>
     *
     * @param   str1        The first string.
     * @param   str2        The second string to be appended to the first
     *                      string.
     *
     * @return  The computation string for concatenation for the
     *          <CODE>SELECT</CODE> or <CODE>WHERE</CODE> clause;
     *          {@link ibs.tech.sql.SQLConstants#DB_NULL DB_NULL}, if both strings
     *          are <CODE>null</CODE>.
     */
    public static StringBuffer getStrCat (final StringBuffer str1,
                                          final String str2)
    {
        // call common method and return the result:
        return SQLHelpers.getStrCat (str1, new StringBuffer ().append (str2));
    } // getStrCat


    /**************************************************************************
     * Get database dependent concatenation of two strings. <BR/>
     * For usage in <CODE>SELECT</CODE>, <CODE>WHERE</CODE>, <CODE>SET</CODE>
     * parts of SELECT, UPDATE, etc. clauses. <BR/>
     * If one of the strings is <CODE>null</CODE> the result is the other
     * string. <BR/>
     *
     * @param   str1        The first string.
     * @param   str2        The second string to be appended to the first
     *                      string.
     *
     * @return  The computation string for concatenation for the
     *          <CODE>SELECT</CODE> or <CODE>WHERE</CODE> clause;
     *          {@link ibs.tech.sql.SQLConstants#DB_NULL DB_NULL}, if both strings
     *          are <CODE>null</CODE>.
     */
    public static StringBuffer getStrCat (final StringBuffer str1,
                                          final StringBuffer str2)
    {
        final StringBuffer retValue;          // the return value

        // check valid call of method:
        if (str1 != null || str2 != null) // parameters valid?
        {
            // check if one of the strings is null:
            if (str1 == null)           // no first string?
            {
                retValue = new StringBuffer ().append (str2);
            } // if no first string
            else if (str2 == null)      // no second string?
            {

                retValue = new StringBuffer ().append (str1);
            } // if no second string
            else                        // both strings not null
            {
                retValue = new StringBuffer (" ").append (str1);

                // check db type:
                switch (SQLConstants.DB_TYPE)
                {
                    case SQLConstants.DB_MSSQL:
                        retValue.append (" + ");
                        break;

                    case SQLConstants.DB_ORACLE:
                    case SQLConstants.DB_DB2:
                        retValue.append (" || ");
                        break;

                    default: // nothing to do
                } // switch

                retValue.append (str2).append (" ");
            } // else both strings not null
        } // if parameters valid
        else                            // any parameter invalid
        {
            // set default return value:
            retValue = new StringBuffer (SQLConstants.DB_NULL);
        } // else any parameter invalid

        // return the result:
        return retValue;
    } // getStrCat


    /**************************************************************************
     * Get the database string which is evaluating the condition
     * <CODE>" IF &lt;value&gt; = &lt;compValue&gt; THEN &lt;truePart&gt;
     * ELSE &lt;falsePart&gt; "</CODE>.
     * For usage in the SELECT part of a SELECT clause. <BR/>
     * If both truePart and falsePart are <CODE>null</CODE> the value itself is
     * used as truePart. <BR/>
     * A value of <CODE>null</CODE> or {@link ibs.tech.sql.SQLConstants#DB_NULL DB_NULL}
     * means a comparison with database value <CODE>NULL</CODE>. <BR/>
     * If both compValue and falsePart are <CODE>null</CODE> (or
     * {@link ibs.tech.sql.SQLConstants#DB_NULL DB_NULL}) the function
     * <CODE>COALESCE</CODE> is used for the MSSQL implementation.
     *
     * @param   value       The value to be compared in the condition.
     * @param   compValue   The comparison value.
     * @param   truePart    The full code to be executed if the condition
     *                      evaluates to <CODE>true</CODE>. <CODE>null</CODE>
     *                      means that no true part is necessary.
     * @param   falsePart   The full code to be executed if the condition
     *                      evaluates to <CODE>false</CODE>. <CODE>null</CODE>
     *                      means that no false part is necessary.
     *
     * @return  The condition computation string for the <CODE>SELECT</CODE>
     *          clause; {@link ibs.tech.sql.SQLConstants#DB_NULL DB_NULL}, if
     *          <CODE>value</CODE> is <CODE>null</CODE> or empty.
     */
    public static StringBuffer getSelectCondition (final String value,
                                                   String compValue,
                                                   String truePart,
                                                   final String falsePart)
    {
        String compValueLocal = compValue; // variable for local assignments
        String truePartLocal = truePart; // variable for local assignments
        final StringBuffer retValue = new StringBuffer (); // the return value
        String compOp = SQLHelpers.COMP_EQU;          // comparison operator

        // check valid call of method:
        if (value != null && value.length () > 0 &&
            !value.equals (SQLConstants.DB_NULL))
                                        // parameters valid?
        {
            // check if at least one of true part and false part is not null:
            if ((truePartLocal == null || truePartLocal.equals (SQLConstants.DB_NULL)) &&
                (falsePart == null || falsePart.equals (SQLConstants.DB_NULL)))
                                        // both parts are null?
            {
                // use the value itself as truePart:
                truePartLocal = value;
            } // if both parts are null

            // ensure that the content of compValue is valid:
            if (compValueLocal == null)  // comparison value null?
            {
                // set NULL value for database:
                compValueLocal = SQLConstants.DB_NULL;
            } // if comparison value null

            // check db type:
            switch (SQLConstants.DB_TYPE)
            {
                case SQLConstants.DB_MSSQL:
                    if (compValueLocal.equals (SQLConstants.DB_NULL) &&
                        (falsePart == null || falsePart.equals (SQLConstants.DB_NULL)))
                                        // use COALESCE?
                    {
                        retValue.append (" COALESCE (").append (value)
                            .append (", ").append (truePartLocal).append (") ");
                    } // if use COALESCE
                    else                // no COALESCE
                    {
                        retValue.append (" CASE WHEN (").append (value)
                            .append (compOp).append (compValueLocal).append (")")
                            .append (" THEN ")
                            .append (truePartLocal != null ? truePartLocal : SQLConstants.DB_NULL);

                        // check if there is an else part:
                        if (falsePart != null && !falsePart.equals (SQLConstants.DB_NULL))
                        // there is an else part?
                        {
                            // add the else part:
                            retValue.append (" ELSE ").append (falsePart);
                        } // if there is an else part

                        // finish the condition:
                        retValue.append (" END ");
                    } // else no COALESCE
                    break;

                case SQLConstants.DB_ORACLE:
                    retValue.append (" DECODE (").append (value).append (", ")
                        .append (compValueLocal).append (", ")
                        .append (truePartLocal != null ? truePartLocal : SQLConstants.DB_NULL)
                        .append (", ")
                        .append (falsePart != null ? falsePart : SQLConstants.DB_NULL)
                        .append (") ");
                    break;

                case SQLConstants.DB_DB2:
                    if (compValueLocal.equals (SQLConstants.DB_NULL) &&
                        (falsePart == null || falsePart.equals (SQLConstants.DB_NULL)))
                    // use COALESCE?
                    {
                        retValue.append (" COALESCE (").append (value)
                            .append (", ").append (truePartLocal).append (") ");
                    } // if use COALESCE
                    else                    // no COALESCE
                    {
                        if (compValueLocal.equals (SQLConstants.DB_NULL))
                                        // null value?
                        {
                            compOp = " IS ";
                        } // if null value

                        retValue.append (" CASE WHEN (").append (value)
                            .append (compOp).append (compValueLocal).append (")")
                            .append (" THEN ")
                            .append (truePartLocal != null ? truePartLocal : SQLConstants.DB_NULL);

                        // check if there is an else part:
                        if (falsePart != null && !falsePart.equals (SQLConstants.DB_NULL))
                        // there is an else part?
                        {
                            // add the else part:
                            retValue.append (" ELSE ").append (falsePart);
                        } // if there is an else part

                        // finish the condition:
                        retValue.append (" END ");
                    } // else no COALESCE
                    break;

                default: // nothing to do
            } // switch
        } // if parameters valid
        else                            // any parameter invalid
        {
            // set default return value:
            retValue.append (SQLConstants.DB_NULL);
        } // else any parameter invalid

        // return the result:
        return retValue;
    } // getSelectCondition


    /**************************************************************************
     * Get the actual date and time in a single value. <BR/>
     * For usage in <CODE>SELECT</CODE>, <CODE>WHERE</CODE>, <CODE>SET</CODE>
     * parts of SELECT, UPDATE, etc. clauses. <BR/>
     *
     * @return  The computation string for the <CODE>SELECT</CODE> or
     *          <CODE>WHERE</CODE> clause;
     *          <CODE>""</CODE>, if there occurred any error.
     */
    public static StringBuffer getActDateTime ()
    {
        final StringBuffer retValue = new StringBuffer (); // the return value

        // check db type:
        switch (SQLConstants.DB_TYPE)
        {
            case SQLConstants.DB_MSSQL:
                retValue.append (" getDate () ");
                break;

            case SQLConstants.DB_ORACLE:
                retValue.append (" SYSDATE ");
                break;

            case SQLConstants.DB_DB2:
                retValue.append (" NOW () ");
                break;

            default: // nothing to do
        } // switch

        // return the result:
        return retValue;
    } // getActDateTime


    /**************************************************************************
     * Get the difference of two dates. <BR/>
     * For usage in <CODE>SELECT</CODE>, <CODE>WHERE</CODE>, <CODE>SET</CODE>
     * parts of SELECT, UPDATE, etc. clauses. <BR/>
     * The parameter unit defines the base unit of the difference: day, month,
     * ... <BR/>
     * The values are not checked in any way. If one of the values is not of
     * correct type the database has to handle this exception.
     *
     * @param   value1      The first value.
     * @param   value2      The second value.
     * @param   unit        The date unit (see
     *                      {@link ibs.tech.sql.SQLConstants SQLConstants}<CODE>.UNIT_*</CODE>).
     *
     * @return  The computation string for the <CODE>SELECT</CODE> or
     *          <CODE>WHERE</CODE> clause;
     *          {@link ibs.tech.sql.SQLConstants#DB_NULL DB_NULL}, if any parameter
     *          is <CODE>null</CODE>.
     *
     * @see     ibs.tech.sql.SQLConstants#UNIT_DAY
     * @see     ibs.tech.sql.SQLConstants#UNIT_MONTH
     */
    public static StringBuffer getDateDiff (final StringBuffer value1,
                                            final StringBuffer value2,
                                            final String unit)
    {
        final StringBuffer retValue = new StringBuffer (); // the return value

        // check valid call of method:
        if (value1 != null && value2 != null &&
            value1.length () > 0 && value2.length () > 0)
                                        // parameters valid?
        {
            // check db type:
            switch (SQLConstants.DB_TYPE)
            {
                case SQLConstants.DB_MSSQL:
                    // units: UNIT_DAY, UNIT_MONTH
                    retValue.append (" DATEDIFF (").append (unit).append (", ")
                        .append (value1).append (", ").append (value2).append (") ");
                    break;

                case SQLConstants.DB_ORACLE:
                    if (unit.equals (SQLConstants.UNIT_HOUR)) // hour?
                    {
                        retValue.append (" FLOOR ((").append (value1).append (" - ")
                            .append (value2).append (") * 24) ");
                    } // if hour
                    else if (unit.equals (SQLConstants.UNIT_DAY)) // day?
                    {
                        retValue.append (" (").append (value1).append (" - ")
                            .append (value2).append (") ");
                    } // else if day
                    else if (unit.equals (SQLConstants.UNIT_MONTH)) // month?
                    {
                        retValue.append (" FLOOR ((").append (value1).append (" - ")
                            .append (value2).append (") * 12 / 365.2425) ");
                    } // if month
                    break;

                case SQLConstants.DB_DB2:
                    String diffType = "16"; // default: days

                    if (unit.equals (SQLConstants.UNIT_HOUR)) // hour?
                    {
                        diffType = "8";
                    } // if hour
                    else if (unit.equals (SQLConstants.UNIT_DAY)) // day?
                    {
                        diffType = "16";
                    } // else if day
                    else if (unit.equals (SQLConstants.UNIT_MONTH)) // month?
                    {
                        diffType = "64";
                    } // if month
                    retValue
                        .append (" TIMESTAMPDIFF (").append (diffType)
                        .append (", CAST (").append (value1).append (" - ")
                        .append (value2).append (" AS CHAR (22))) ");
                    break;

                default: // nothing to do
            } // switch
        } // if parameters valid
        else                            // any parameter invalid
        {
            // set default return value:
            retValue.append (SQLConstants.DB_NULL);
        } // else any parameter invalid

        // return the result:
        return retValue;
    } // getDateDiff


    /**************************************************************************
     * Converts a standard date String of format 'dd.mm.yyyy hh24:mi:ss' into
     * the date/time String which is accurate for the actual database. <BR/>
     * Calls the common method {@link #getDateString (java.lang.StringBuffer)
     * getDateString (StringBuffer)}.
     *
     * @param   datetime    The datetime string.
     *
     * @return  String, which contains string for the Queries in Java where a
     *          Date is used.
     *
     * @see #DATETIMEFORMAT
     */
    public static StringBuffer getDateString (final String datetime)
    {
        // call common method and retur the result:
        return SQLHelpers.getDateString (new StringBuffer ().append (datetime));
    } // getDateString


    /**************************************************************************
     * Converts a standard date String of format 'dd.mm.yyyy hh24:mi:ss' into
     * the date/time String which is accurate for the actual database.
     *
     * @param   datetime    The datetime string.
     *
     * @return  String, which contains string for the Queries in Java where a
     *          Date is used.
     *
     * @see #DATETIMEFORMAT
     */
    public static StringBuffer getDateString (final StringBuffer datetime)
    {
        final StringBuffer retVal = new StringBuffer ();
        StringBuffer date;              // date part of search value
        StringBuffer time;              // time part of search value
        int pos;                        // current search position

        // check db type:
        switch (SQLConstants.DB_TYPE)
        {
            case SQLConstants.DB_MSSQL:
                retVal.append ("CONVERT (DATETIME, '").append (datetime).append ("', " +
                        SQLConstants.MSSQL_DATEFORMAT_GERMAN + ")");
                break;

            case SQLConstants.DB_ORACLE:
                retVal.append (" TO_DATE ('").append (datetime).append ("', '")
                    .append (SQLHelpers.DATETIMEFORMAT).append ("') ");
                break;

            case SQLConstants.DB_DB2:
// KR HACK: This part is implemented because of backwards compatibility to
// Java 1.3 which does not support the StringBuffer.indexOf () method.
                // split the date string into the date and the time part:

                // search for the date/time separator:
                for (pos = 0;
                     pos < datetime.length () && datetime.charAt (pos) != ' ';
                     pos++)
                {
                    // nothing to do
                } // for pos

                // set the date string:
                date = new StringBuffer (datetime.substring (0, pos));

                // set the time string:
                if (pos >= datetime.length ()) // no date/time sep.?
                {
                    // the search value ist just the date:
                    time = SQLHelpers.TIMEDAYSTART;
                } // no date/time sep.
                else                            // date/time sep. exists
                {
                    // the search consists of both parts:
                    time = new StringBuffer (datetime.substring (pos + 1));
                } // else date/time sep. exists

/*
// KR HACK: This part is not used because of backwards compatibility to
// Java 1.3 which does not support the StringBuffer.indexOf () method.
                if ((pos = datetime.indexOf (" ")) < 0) // no date/time sep.?
                {
                    // the search value ist just the date:
                    date = datetime;
                    time = TIMEDAYSTART;
                } // no date/time sep.
                else                            // date/time sep. exists
                {
                    // the search consists of both parts:
                    date = new StringBuffer (datetime.substring (0, pos));
                    time = new StringBuffer (datetime.substring (pos + 1));
                } // else date/time sep. exists
*/
                retVal.append (" TIMESTAMP ('")
                    .append (date).append ("', '")
                    .append (time).append ("')");
                break;

            default: // nothing to do
        } // switch

        // return the computed date string:
        return retVal;
    } // getDateString


    /**************************************************************************
     * Converts a standard date String of format 'dd.mm.yyyy hh24:mi:ss' into
     * the date/time String which is accurate for the actual database.
     *
     * @param   date    The date string.
     * @param   time    The time string.
     *
     * @return  String, which contains string for the Queries in Java where a
     *          Date is used.
     *
     * @see #DATETIMEFORMAT
     */
    public static StringBuffer getDateString (final StringBuffer date,
                                              final StringBuffer time)
    {
        final StringBuffer retVal = new StringBuffer ();

        // check db type:
        switch (SQLConstants.DB_TYPE)
        {
            case SQLConstants.DB_MSSQL:
                retVal.append ("CONVERT (DATETIME, '")
                    .append (date).append (" ").append (time).append ("')");
                break;

            case SQLConstants.DB_ORACLE:
                retVal.append (" TO_DATE ('")
                    .append (date).append (" ").append (time).append ("', '")
                    .append (SQLHelpers.DATETIMEFORMAT).append ("') ");
                break;

            case SQLConstants.DB_DB2:
                retVal.append (" TIMESTAMP ('")
                    .append (date).append ("', '").append (time).append ("')");
                break;

            default: // nothing to do
        } // switch

        // return the computed date string:
        return retVal;
    } // getDateString


    /**************************************************************************
     * Converts a standard Date object into the date String which is
     * accurate for the actual database.
     * We use the default timezone an locale for conversion.
     *
     * @param   date    The date object.
     *
     * @return  String, which contains string for the Queries in Java where a
     *          Date is used.
     *
     * @see #DATETIMEFORMAT
     */
    public static StringBuffer getDateString (final Date date)
    {
        StringBuffer retVal = null;     // the resulting date string
        GregorianCalendar cal;          // the calendar for the actual day

        // check if we have a valid date:
        if (date != null)
        {
            // create a GregorianCalendar with default timezone and locale:
            cal = new GregorianCalendar ();

            // calendar fills missing data
            // set view date in calendar:
            cal.setTime (date);
            cal.set (Calendar.HOUR_OF_DAY, 0);
            cal.set (Calendar.MINUTE, 0);
            cal.set (Calendar.SECOND, 0);
            cal.set (Calendar.MILLISECOND, 0);

            retVal = SQLHelpers.dateStringB (cal);
        } // if

        // return the computed result:
        return retVal;
    } // getDateString


    /**************************************************************************
     * Converts a standard Date object into the time String which is
     * accurate for the actual database.
     * We use the default timezone an locale for conversion.
     *
     * @param   date    The date object.
     *
     * @return  String, which contains string for the Queries in Java where a
     *          Date is used.
     *
     * @see #DATETIMEFORMAT
     */
    public static StringBuffer getTimeString (final Date date)
    {
        StringBuffer retVal = null;     // the resulting date string
        GregorianCalendar cal;          // the calendar for the actual day

        // check if we have a valid date:
        if (date != null)
        {
            // create a GregorianCalendar with default timezone and locale:
            cal = new GregorianCalendar ();

            // calendar fills missing data
            // set view date in calendar:
            cal.setTime (date);
            cal.set (Calendar.YEAR, 0);
            cal.set (Calendar.MONTH, 0);
            cal.set (Calendar.DATE, 0);

            retVal = SQLHelpers.dateStringB (cal);
        } // if

        // return the computed result:
        return retVal;
    } // getTimeString


    /**************************************************************************
     * Converts a standard Date object into the date/time String which is
     * accurate for the actual database.
     * We use the default timezone an locale for conversion.
     *
     * @param   date    The date object.
     *
     * @return  String, which contains string for the Queries in Java where a
     *          Date is used.
     *
     * @see #DATETIMEFORMAT
     */
    public static StringBuffer getDateTimeString (final Date date)
    {
        StringBuffer retVal = null;     // the resulting date string
        GregorianCalendar cal;          // the calendar for the actual day

        // check if we have a valid date:
        if (date != null)
        {
            // create a GregorianCalendar with default timezone and locale:
            cal = new GregorianCalendar ();

            // calendar fills missing data
            // set view date in calendar:
            cal.setTime (date);

            retVal = SQLHelpers.dateStringB (cal);
        } // if

        // return the computed result:
        return retVal;
    } // getDateTimeString


    /**************************************************************************
     * Get the bitwise AND operation of two values. <BR/>
     * For usage in <CODE>SELECT</CODE>, <CODE>WHERE</CODE>, <CODE>SET</CODE>
     * parts of SELECT, UPDATE, etc. clauses. <BR/>
     * The values are not checked in any way. If one of the values is not of
     * correct type the database has to handle this exception.
     *
     * @param   value1      The first value.
     * @param   value2      The second value.
     *
     * @return  The computation string for the <CODE>SELECT</CODE> or
     *          <CODE>WHERE</CODE> clause;
     *          {@link ibs.tech.sql.SQLConstants#DB_NULL DB_NULL}, if any parameter
     *          is <CODE>null</CODE>.
     */
    public static StringBuffer getBitAnd (final String value1,
                                          final String value2)
    {
        final StringBuffer retValue = new StringBuffer (); // the return value

        // check valid call of method:
        if (value1 != null && value2 != null &&
            value1.length () > 0 && value2.length () > 0)
                                        // parameters valid?
        {
            // check db type:
            switch (SQLConstants.DB_TYPE)
            {
                case SQLConstants.DB_MSSQL:
                    retValue.append (" (").append (value1).append (" & ")
                        .append (value2).append (") ");
                    break;

                case SQLConstants.DB_ORACLE:
                    retValue.append (" B_AND (").append (value1).append (", ")
                        .append (value2).append (") ");
                    break;

                case SQLConstants.DB_DB2:
                    retValue.append (" ")
                        .append (DBConnector.p_defaultConf.getDbSid ())
                        .append (".B_AND (").append (value1).append (", ")
                        .append (value2).append (") ");
                    break;

                default: // nothing to do
            } // switch
        } // if parameters valid
        else                            // any parameter invalid
        {
            // set default return value:
            retValue.append (SQLConstants.DB_NULL);
        } // else any parameter invalid

        // return the result:
        return retValue;
    } // getBitAnd


    /**************************************************************************
     * Get the bitwise OR operation of two values. <BR/>
     * For usage in <CODE>SELECT</CODE>, <CODE>WHERE</CODE>, <CODE>SET</CODE>
     * parts of SELECT, UPDATE, etc. clauses. <BR/>
     * The values are not checked in any way. If one of the values is not of
     * correct type the database has to handle this exception.
     *
     * @param   value1      The first value.
     * @param   value2      The second value.
     *
     * @return  The computation string for the <CODE>SELECT</CODE> or
     *          <CODE>WHERE</CODE> clause;
     *          {@link ibs.tech.sql.SQLConstants#DB_NULL DB_NULL}, if any parameter
     *          is <CODE>null</CODE>.
     */
    public static StringBuffer getBitOr (final String value1,
                                         final String value2)
    {
        final StringBuffer retValue = new StringBuffer (); // the return value

        // check valid call of method:
        if (value1 != null && value2 != null &&
            value1.length () > 0 && value2.length () > 0)
                                        // parameters valid?
        {
            // check db type:
            switch (SQLConstants.DB_TYPE)
            {
                case SQLConstants.DB_MSSQL:
                    retValue.append (" (").append (value1).append (" | ")
                        .append (value2).append (") ");
                    break;

                case SQLConstants.DB_ORACLE:
                    retValue.append (" B_OR (").append (value1).append (", ")
                        .append (value2).append (") ");
                    break;

                case SQLConstants.DB_DB2:
                    retValue.append (" ")
                        .append (DBConnector.p_defaultConf.getDbSid ())
                        .append (".B_OR (").append (value1).append (", ")
                        .append (value2).append (") ");
                    break;

                default: // nothing to do
            } // switch
        } // if parameters valid
        else                            // any parameter invalid
        {
            // set default return value:
            retValue.append (SQLConstants.DB_NULL);
        } // else any parameter invalid

        // return the result:
        return retValue;
    } // getBitOr


    /**************************************************************************
     * Get the statement which evaluates to a value which is between the two
     * one. <BR/>
     * For usage in <CODE>SELECT</CODE>, <CODE>WHERE</CODE>
     * parts of SELECT, UPDATE, etc. clauses. <BR/>
     * The values are not checked in any way. If value1 is bigger than value2
     * or one of the values is not of correct type the database has to handle
     * this exception.
     *
     * @param   value1      The lower value.
     * @param   value2      The upper value.
     *
     * @return  The computation string for the <CODE>SELECT</CODE> or
     *          <CODE>WHERE</CODE> clause;
     *          {@link ibs.tech.sql.SQLConstants#DB_NULL DB_NULL}, if any parameter
     *          is <CODE>null</CODE>.
     */
    public static StringBuffer getBetween (final String value1, final String value2)
    {
        // call common method and return the result:
        return SQLHelpers.getBetween (
            new StringBuffer ().append (value1),
            new StringBuffer ().append (value2));
    } // getBetween


    /**************************************************************************
     * Get the statement which evaluates to a value which is between the two
     * one. <BR/>
     * For usage in <CODE>SELECT</CODE>, <CODE>WHERE</CODE>
     * parts of SELECT, UPDATE, etc. clauses. <BR/>
     * The values are not checked in any way. If value1 is bigger than value2
     * or one of the values is not of correct type the database has to handle
     * this exception.
     *
     * @param   value1      The lower value.
     * @param   value2      The upper value.
     *
     * @return  The computation string for the <CODE>SELECT</CODE> or
     *          <CODE>WHERE</CODE> clause;
     *          {@link ibs.tech.sql.SQLConstants#DB_NULL DB_NULL}, if any parameter
     *          is <CODE>null</CODE>.
     */
    public static StringBuffer getBetween (final StringBuffer value1,
                                           final StringBuffer value2)
    {
        final StringBuffer retValue = new StringBuffer (); // the return value

        // check valid call of method:
        if (value1 != null && value2 != null &&
            value1.length () > 0 && value2.length () > 0)
                                        // parameters valid?
        {
            // check db type:
            switch (SQLConstants.DB_TYPE)
            {
                case SQLConstants.DB_MSSQL:
                    retValue.append (" BETWEEN ").append (value1).append (SQLConstants.SQL_AND)
                        .append (value2).append (" ");
                    break;

                case SQLConstants.DB_ORACLE:
                    retValue.append (" BETWEEN ").append (value1).append (SQLConstants.SQL_AND)
                        .append (value2).append (" ");
                    break;

                case SQLConstants.DB_DB2:
                    retValue.append (" BETWEEN ").append (value1).append (SQLConstants.SQL_AND)
                        .append (value2).append (" ");
                    break;

                default: // nothing to do
            } // switch
        } // if parameters valid
        else                            // any parameter invalid
        {
            // set default return value:
            retValue.append (SQLConstants.DB_NULL);
        } // else any parameter invalid

        // return the result:
        return retValue;
    } // getBetween


    /**************************************************************************
     * Create the statement which is used for LEFT OUTER JOINs. <BR/>
     * This method extends the <CODE>FROM</CODE> and possibly the
     * <CODE>WHERE</CODE> part of a SELECT query. <BR/>
     * Currently there is only the equi join ("=") possible. That means that the
     * comparison within the attributes is done by equi join. <BR/>
     * The values are not checked in any way. If the table or one attribute
     * does not exist the database has to handle this exception. <BR/>
     * Both <CODE>fromClause</CODE> and <CODE>whereClause</CODE> are extended
     * with the new content, that means if there is already something in one of
     * these variables the string stays the same and the new content is
     * appended. Both parameters are either unchanged or finished with a space
     * character (<CODE>' '</CODE>). <BR/>
     * To ensure correct condition handling the attributes of the regarded which
     * shall be left joined have to be on the right hand side of the condition.
     * <BR/>
     * None of the parameters is allowed to be <CODE>null</CODE>. If one is
     * <CODE>null</CODE> a {@link NullPointerException NullPointerException} is
     * raised.
     *
     * @param   tableName   The table to be left joined.
     * @param   tableAlias  Alias name for the table.
     * @param   condition   The join condition.
     * @param   stmtConcat  Statement concatenation which shall be added before
     *                      the where clause. E.g. <CODE>"AND"</CODE>,
     *                      <CODE>"OR"</CODE>, ...
     * @param   fromClause  FROM clause to which to concatenate the table and
     *                      the left join.
     * @param   whereClause WHERE clause to concatenate the attribute
     *                      comparison.
     *
     * @return  The resulting FROM clause. This is the same as the content of
     *          <CODE>fromClause</CODE>.
     */
    public static StringBuffer getLeftOuterJoin (final StringBuffer tableName,
                                                 final StringBuffer tableAlias,
                                                 final StringBuffer condition,
                                                 final StringBuffer stmtConcat,
                                                 final StringBuffer fromClause,
                                                 final StringBuffer whereClause)
    {
        // create StringBuilder instances for later use:
        StringBuilder fromClauseB = new StringBuilder (fromClause);
        StringBuilder whereClauseB = new StringBuilder (whereClause);

        // call common method:
        SQLHelpers.getLeftOuterJoin (
            new StringBuilder (tableName), new StringBuilder (tableAlias),
            new StringBuilder (condition), new StringBuilder (stmtConcat),
            fromClauseB, whereClauseB);

        // To ensure that the returned object will be the same as the original
        // one its content must be replaced.
        // first of all we have to delete the original content:
        // then set the new content:
        fromClause.setLength (0);
        fromClause.append (fromClauseB);
        whereClause.setLength (0);
        whereClause.append (whereClauseB);

        // return the result:
        return fromClause;
    } // getLeftOuterJoin


    /**************************************************************************
     * Create the statement which is used for LEFT OUTER JOINs. <BR/>
     * This method extends the <CODE>FROM</CODE> and possibly the
     * <CODE>WHERE</CODE> part of a SELECT query. <BR/>
     * Currently there is only the equi join ("=") possible. That means that the
     * comparison within the attributes is done by equi join. <BR/>
     * The values are not checked in any way. If the table or one attribute
     * does not exist the database has to handle this exception. <BR/>
     * Both <CODE>fromClause</CODE> and <CODE>whereClause</CODE> are extended
     * with the new content, that means if there is already something in one of
     * these variables the string stays the same and the new content is
     * appended. Both parameters are either unchanged or finished with a space
     * character (<CODE>' '</CODE>). <BR/>
     * To ensure correct condition handling the attributes of the regarded which
     * shall be left joined have to be on the right hand side of the condition.
     *
     * @param   tableName   The table to be left joined.
     * @param   tableAlias  Alias name for the table.
     * @param   condition   The join condition.
     * @param   stmtConcat  Statement concatenation which shall be added before
     *                      the where clause. E.g. <CODE>"AND"</CODE>,
     *                      <CODE>"OR"</CODE>, ...
     * @param   fromClause  FROM clause to which to concatenate the table and
     *                      the left join.
     * @param   whereClause WHERE clause to concatenate the attribute
     *                      comparison.
     *
     * @return  The resulting FROM clause. This is the same as the content of
     *          <CODE>fromClause</CODE>.
     */
    public static StringBuilder getLeftOuterJoin (
                                                  final StringBuilder tableName,
                                                  final StringBuilder tableAlias,
                                                  final StringBuilder condition,
                                                  final StringBuilder stmtConcat,
                                                  final StringBuilder fromClause,
                                                  final StringBuilder whereClause)
    {
        // check valid call of method:
        if (tableName != null && tableName.length () > 0)
                                        // parameters valid?
        {
            // check db type:
            switch (SQLConstants.DB_TYPE)
            {
                case SQLConstants.DB_MSSQL:
                case SQLConstants.DB_DB2:
                    // set the from clause:
                    fromClause
                        .append (" LEFT OUTER JOIN ")
                        .append (tableName).append (" ").append (tableAlias)
                        .append (" ON ").append (condition).append (" ");
                    // no where clause needed.
/* KR alternative for MSSQL:
                    // set the from clause:
                    fromClause
                        .append (", ").append (tableName).append (" ")
                        .append (tableAlias).append (" ");
                    // set the specific attribute comparison:
                    whereClause
                        .append (stmtConcat).append (" ")
                        .append (Helpers.replace (condition, "=", "*="))
                        .append (" ");
*/
                    break;

                case SQLConstants.DB_ORACLE:
                {
                    StringTokenizer tokenizer; // the string tokenizer
                    String token;           // the actual token
                    final int stStart = 0;
                    final int stEqual = 1;
                    final int stDot = 2;
/*
                    final int st_SPACE = 3;
*/
                    final int stAlias = 4;
                    int state = stStart;

                    // set the from clause:
                    fromClause
                        .append (", ").append (tableName).append (" ")
                        .append (tableAlias).append (" ");

                    whereClause.append (stmtConcat).append (" ");

                    // set the specific attribute comparison:
                    // concatenate (+) to all attributes of the required table at
                    // the right hand side
                    tokenizer = new StringTokenizer (condition.toString (),
                        "=. ", true);

                    while (tokenizer.hasMoreTokens ())
                    {
                        // get the actual token:
                        token = tokenizer.nextToken ();

                        // append the token to the result string:
                        whereClause.append (token);

                        switch (state)
                        {
                            case stStart:
                                if (token.equals ("=")) // equality condition?
                                {
                                    state = stEqual;
                                } // if equality condition
                                else        // not recognized token
                                {
                                    // nothing to do
                                } // else not recognized token
                                break;

                            case stEqual:
                                if (token.equals (" ")) // empty space?
                                {
                                    // nothing to do
                                } // else if empty space
                                else if (token.equals (tableAlias)) // table alias?
                                {
                                    state = stAlias;
                                } // else if table alias
                                else        // not recognized token
                                {
                                    // table alias not found => restart
                                    state = stStart;
                                } // else not recognized token
                                break;

                            case stAlias:
                                if (token.equals (".")) // separator alias and attr.?
                                {
                                    state = stDot;
                                } // else if separator alias and attr.
                                else        // not recognized token
                                {
                                    // dot not found => restart
                                    state = stStart;
                                } // else not recognized token
                                break;

                            case stDot:
                                // append the OUTER JOIN operator:
                                whereClause.append (" (+)");
                                // restart:
                                state = stStart;
                                break;

                            default: // nothing to do
                        } // switch
                    } // while

                    whereClause
//                        .append (stmtConcat).append (" ");
//                        .append (Helpers.replace (condition, "=", " (+) ="))
                        .append (" ");
                } // case
                    break;

                default: // nothing to do
            } // switch
        } // if parameters valid
        else                            // any parameter invalid
        {
            // nothing to do
        } // else any parameter invalid

        // return the computed string:
        return fromClause;
    } // getLeftOuterJoin


    /**************************************************************************
     * Creates a condition String for the WHERE clause in a query, for datatype
     * OBJECTID. Matchtype EXACT (" = ") is used as OPERATOR. <BR/>
     *
     * @param   attribute   Name of attribute in query to be contrained with
     *                      searchvalue
     * @param   searchValue searchvalue to constrain the resultset via
     *                      matching with attribute
     *
     * @return  The condition string for the <CODE>WHERE</CODE> clause without
     *          <CODE>AND</CODE> at the beginning;
     *          <CODE>null</CODE>, if any parameter is <CODE>null</CODE>.
     *
     * @exception   IncorrectOidException
     *              Wrong oid.
     */
    public static String getQueryConditionOid (final String attribute,
                                               final String searchValue)
        throws IncorrectOidException
    {
        // check valid call of method
        if (attribute == null || searchValue == null)
        {
            return null;
        } // if

        // matchtype is always EXACT
        // convert the searchValue to an oid and compute the result:
        return SQLHelpers.getQueryConditionOid (attribute, new OID (searchValue));
    } // getQueryConditionOid


    /**************************************************************************
     * Creates a condition String for the WHERE clause in a query, for datatype
     * OBJECTID. Matchtype EXACT (" = ") is used as OPERATOR. <BR/>
     *
     * @param   attribute   Name of attribute in query to be contrained with
     *                      searchvalue
     * @param   searchValue Oid to constrain the resultset via
     *                      matching with attribute
     *
     * @return  The condition string for the <CODE>WHERE</CODE> clause without
     *          <CODE>AND</CODE> at the beginning;
     *          <CODE>null</CODE>, if any parameter is <CODE>null</CODE>.
     */
    public static String getQueryConditionOid (final String attribute,
                                               final OID searchValue)
    {
        // sql string that will be returned
        String sqlConditionString = null;
        String oidString;               // string holding the oid

        // check valid call of method
        if (attribute == null || searchValue == null)
        {
            return null;
        } // if

        // matchtype is always EXACT
        // convert the searchValue to an oid
        oidString = searchValue.toStringQu ();

        if (oidString != null)          // if there is a valid oidString
        {
            sqlConditionString = attribute + SQLHelpers.COMP_EQU + oidString;
        } // if

        return sqlConditionString;
    } // getQueryConditionOid


    /**************************************************************************
     * Transforms a given byte array to a string of the form 0x...., e.g.
     * 0xa334f8. <BR/>
     *
     * @param   byteArray   The given byte array.
     *
     * @return  The converted String.
     */
    public static String byteArrayToString (final byte[] byteArray)
    {
        int pos;                        // counter
        StringBuilder s = new StringBuilder (UtilConstants.NUM_START_HEX); // the result string

        // loop array - convert values
        for (pos = 0; pos < byteArray.length; pos++)
        {
            s.append (((byteArray [pos] & 0xf0) == 0 ? "0" : "") +
                      Integer.toHexString (byteArray [pos] & 0xff));
        } // for

        return s.toString ();
    } // byteArrayToString


    /**************************************************************************
     * Get an OID parameter from a stored procedure. <BR/>
     *
     * @param   param       Parameter containing the oid.
     *
     * @return  The oid gotten from the parameter. <BR/>
     *          If the parameter does not contain an oid or contains an invalid
     *          oid the oid is constructed of the default value
     *          {@link ibs.bo.OID#getEmptyOid () getEmptyOid ()}.
     *          If the parameter is <CODE>null</CODE> the oid is set to
     *          <CODE>null</CODE>, too.
     */
    public static OID getSpOidParam (final Parameter param)
    {
        OID oid = null;                 // the oid itself
        String value;                   // the string representation of the oid

        // check if the parameter was set and exists:
        if (param != null && (value = param.getValueString ()) != null)
                                        // value exists?
        {
            try
            {
                // try to create an oid from the string:
                oid = new OID (value);
            } // try
            catch (IncorrectOidException e)
            {
                // set the standard oid:
                oid = OID.getEmptyOid ();
            } // catch
        } // if value exists

        return oid;                     // return the oid
    } // getSpOidParam


    /**************************************************************************
     * Get an OID value from a query. <BR/>
     *
     * @param   action      The action object associated with the connection.
     * @param   name        Name of the attribute containing the oid.
     *
     * @return  The oid gotten from the attribute. <BR/>
     *          If the parameter does not contain an oid or contains an invalid
     *          oid the oid is constructed of the default value
     *          {@link ibs.bo.OID#getEmptyOid () getEmptyOid ()}.
     *          If the name is <CODE>null</CODE> the oid is set to
     *          <CODE>null</CODE>, too.
     *
     * @throws  DBError
     *          An exception occurred when getting the value out of the action.
     */
    public static OID getQuOidValue (final SQLAction action, final String name)
        throws DBError
    {
        OID oid = null;                 // the oid itself
        String value;                   // the string representation of the oid
        byte[] valueB;                  // the byte array repres. of the oid

        // check if the parameter was set and exists:
        if (name != null && name.length () > 0) // value exists?
        {
            // check db type:
            switch (SQLConstants.DB_TYPE)
            {
// TODO: change also oracle code the new method getBinary after checking if this
// is working
                case SQLConstants.DB_ORACLE:
                    // get the oid value:
                    if ((value = action.getString (name)) != null)
                    {
                        try
                        {
                            // try to create an oid from the string:
                            oid = new OID (value);
                        } // try
                        catch (IncorrectOidException e)
                        {
                            // set the standard oid:
                            oid = OID.getEmptyOid ();
                        } // catch
                    } // if
                    break;

                case SQLConstants.DB_MSSQL:
                case SQLConstants.DB_DB2:
                    // get the oid value:
                    if ((valueB = action.getBinary (name)) != null)
                    {
                        try
                        {
                            // try to create an oid from the string:
                            oid = new OID (valueB);
                        } // try
                        catch (IncorrectOidException e)
                        {
                            // set the standard oid:
                            oid = OID.getEmptyOid ();
                        } // catch
                    } // if
                    break;

                default: // nothing to do
            } // switch
        } // if value exists

        return oid;                     // return the oid
    } // getQuOidValue


    /**************************************************************************
     * Prepares the given unicode string value for the values section of an
     * insert or update statement. 
     *
     * @param   value   The unicode value string.
     *
     * @return  String, prepared for the values section of an
     * insert or update statement.
     */
    public static StringBuilder getUnicodeString (String value)
    {
        StringBuilder retValue = new StringBuilder ();
        
        // Set the DB system dependent UNICODE prefix
        switch (SQLConstants.DB_TYPE)
        {
            case SQLConstants.DB_DB2:
                break;
            default:
                retValue.append ("N");
                break;
        } // switch
        
        retValue.append ("'").append (value).append ("'");
        
        return retValue;
    } // getUnicodeString
    
    
    /**************************************************************************
     * Prepares the given unicode string list for an IN clause of a select
     * statement. 
     *
     * @param   valueList   The unicode value string list.
     *
     * @return  String, prepared for the IN clause of a select statement.
     */
    public static StringBuilder getUnicodeStringList (String valueList)
    {
        StringBuilder retValue = new StringBuilder ();
        
        // Set the DB system dependent UNICODE prefix
        switch (SQLConstants.DB_TYPE)
        {
            case SQLConstants.DB_DB2:
                break;
            default:
                // Tokenize the value list
                StringTokenizer tok = new StringTokenizer (valueList, QueryConstants.CONST_DELIMITER);
                
                while (tok.hasMoreTokens ())
                {
                    retValue.
                        // add the unicode prefix
                        append ("N").
                        // add the value
                        append (tok.nextToken ());
                    
                        // add the delimiter
                        if (tok.hasMoreTokens ())
                        {
                            retValue.append (QueryConstants.CONST_DELIMITER);
                        } // if
                } // while
                
                break;
        } // switch
        
        return retValue;
    } // getUnicodeStringList


    /**************************************************************************
     * Get an the rights checking string for the actual database. <BR/>
     * The column <CODE>rights</CODE> must exist within the query scope.
     *
     * @param   rights      The rights to be checked.
     *
     * @return  The string which performs the rights check.
     */
    public static StringBuffer getStringCheckRights (final int rights)
    {
        // call common function:
        return SQLHelpers.getStringCheckRights (rights, "rights");
    } // String getStringCheckRights


    /**************************************************************************
     * Get an the rights checking string for the actual database. <BR/>
     * The attribute <CODE>rights</CODE> must exist within the query scope.
     *
     * @param   rights      The rights to be checked.
     * @param   column      The name of the query column which contains the
     *                      rights.
     *
     * @return  The string which performs the rights check.
     */
    public static StringBuffer getStringCheckRights (final int rights,
                                                     final String column)
    {
        return new StringBuffer (SQLConstants.SQL_AND)
            .append (SQLHelpers.getBitAnd (column, "" + rights))
            .append (SQLHelpers.COMP_EQU).append (rights).append (" ");
    } // String getStringCheckRights


    /**************************************************************************
     * Get the call to a stored procedure. <BR/>
     * The parameters <CODE>comment</CODE>, <CODE>declarations</CODE>,
     * <CODE>call</CODE>, <CODE>eval</CODE> must be initialized string buffers.
     * The statements are concatenated to these.
     *
     * @param   sp          The stored procedure with its parameters.
     *
     * @param   comment     The comment.
     * @param   declarations Filled with the declarations.
     * @param   call        The call itself.
     * @param   eval        The evaluation of the call.
     *
     * @return  The complete procedure call.
     */
    public static StringBuffer getProcCall (final StoredProcedure sp,
                                            final StringBuffer comment,
                                            final StringBuffer declarations,
                                            final StringBuffer call,
                                            final StringBuffer eval)
    {
        int i;                          // number of actual parameter
        short type;                     // type of parameter
        short direction;                // direction of parameter (IN/OUT)
        final int length = sp.countParameters (); // number of parameters
        byte[] byteArr;                 // byte array
        char[] charArr;                 // character array
        Parameter param;                // actual parameter
        final String varPrefix;         // variable prefix
        String declarePrefix;           // declaration prefix (per variable)
        final String declarePostfix;    // declaration postfix (per variable)
        String typeName;                // name of variable type (for
                                        // declaration)
        final String [] typeNames;      // the type names

        // set the comment:
        comment
            .append ("-- ").append (sp.getName ()).append (": ")
            .append (length).append (" parameters.");

        switch (SQLConstants.DB_TYPE)
        {
            case SQLConstants.DB_ORACLE:
                // the typeNames are oracle specific:
                typeNames = SQLConstants.TYPENAMES_ORACLE;
                // no variable prefix necessary:
                varPrefix = "";
                // no declare prefix necessary:
                declarePrefix = "";
                // after a declaration in oracle there has to be placed an ';':
                declarePostfix = ";";
                // initialize the begin of the oracle-specific call
                // along with the return value:
                call
                    .append ("BEGIN\n ").append (varPrefix).append ("l_retVal := ")
                    .append (sp.getName ()).append (" (");
                declarations
                    .append ("DECLARE\n ").append (declarePrefix).append (varPrefix)
                    .append ("l_retVal INTEGER").append (declarePostfix).append ("\n");
                // initializes the evaluation of the return value
                eval
                    .append ("debug ('retVal -> ' || ").append (varPrefix)
                    .append ("l_retVal);\n");
                break;

            case SQLConstants.DB_DB2:
                // the typeNames are ms-sqlspecific
                typeNames = SQLConstants.TYPENAMES_DB2;
                // the prefix @ for each variable is necessary
                varPrefix = "";
                // in db2 there must always be the declare keyword:
                declarePrefix = "DECLARE ";
                // after a declaration in db2 there has to be placed an ';':
                declarePostfix = ";";

                // initialize the begin of the db2-specific call
                // along with the return value:
                call
                    .append ("CALL ").append (varPrefix)
                    .append ("l_retVal = ")
                    .append (DBConnector.p_defaultConf.getDbSid ()).append (".")
                    .append (sp.getName ()).append (" (");
                declarations
                    .append ("DECLARE ").append (declarePrefix)
                    .append (varPrefix).append ("l_retVal INTEGER")
                    .append (declarePostfix).append ("\n");
                // initializes the evaluation of the return value:
                eval
                    .append ("SELECT ").append (varPrefix).append ("l_retVal AS retVal;\n");
                break;

            case SQLConstants.DB_MSSQL:
            default:
                // the typeNames are ms-sqlspecific
                typeNames = SQLConstants.TYPENAMES_SQLS;
                // the prefix @ for each variable is necessary
                varPrefix = "@";
                // no declare prefix necessary:
                declarePrefix = "";
                // nothing special is necessary after the first declaration
                declarePostfix = "";
                // initialize the begin of the ms-sql-specific call
                // aong with the return value
                call
                    .append ("EXEC ").append (varPrefix)
                    .append ("l_retVal = ").append (sp.getName ()).append (" ");
                declarations
                    .append ("DECLARE\n ").append (declarePrefix)
                    .append (varPrefix).append ("l_retVal INT")
                    .append (declarePostfix).append ("\n");
                // initializes the evaluation of the return value
                eval
                    .append ("SELECT ").append (varPrefix).append ("l_retVal AS retVal ");
                // after the first declaration (namely the one for the return value)
                // after each declaration a ',' is needed
                declarePrefix = ",";
                break;
        } // switch

        // loop through all parameters:
        for (i = 0; i < length; i++)
        {
            param = sp.getParameter (i); // get actual parameter
            type = param.getDataType (); // get type of parameter
            direction = param.getDirection (); // get direction of parameter

            switch (direction)          // handle direction of parameter
            {
                case ParameterConstants.DIRECTION_IN: // in parameter
                    switch (type)       // handle type of parameter
                    {
                        case ParameterConstants.TYPE_BOOLEAN: // boolean parameter
                            call.append (param.getValueBoolean () ? 1 : 0);
                            break;

                        case ParameterConstants.TYPE_BYTE: // byte parameter
                            call.append (param.getValueByte ());
                            break;

                        case ParameterConstants.TYPE_CURRENCY: // currency parameter
                            call.append (param.getValueCurrency ());
                            break;

                        case ParameterConstants.TYPE_DATE: // date parameter
                            if (param.getValueDate () != null)
                            {
                                call.append ("'" + DateTimeHelpers.dateTimeToString (param.getValueDate ()) + "'");
                            } // if
                            else
                            {
                                call.append (SQLConstants.DB_NULL);
                            } // else
                            break;

                        case ParameterConstants.TYPE_DOUBLE: // double parameter
                            call.append (param.getValueDouble ());
                            break;

                        case ParameterConstants.TYPE_FLOAT: // float parameter
                            call.append (param.getValueFloat ());
                            break;

                        case ParameterConstants.TYPE_INTEGER: // integer parameter
                            call.append (param.getValueInteger ());
                            break;

                        case ParameterConstants.TYPE_OBJECT: // object parameter
                            if (param.getValueObject () != null)
                            {
                                call.append ("'" + param.getValueObject () + "'");
                            } // if
                            else
                            {
                                call.append (SQLConstants.DB_NULL);
                            } // else
                            break;

                        case ParameterConstants.TYPE_SHORT: // short parameter
                            call.append (param.getValueShort ());
                            break;

                        case ParameterConstants.TYPE_STRING: // string parameter
                            if (param.getValueString () != null)
                            {
                                if (param.getValueString ().length () == 0 &&
                                    SQLConstants.DB_TYPE == SQLConstants.DB_ORACLE)
                                {
                                    // substitute " " for "" because SQL Server
                                    // do it, but ORACLE do not. (workaround)
                                    call.append ("' '");
                                } // if
                                else
                                {
                                    call.append ("'" + param.getValueString () + "'");
                                } // else
                            } // if
                            else
                            {
                                call.append (SQLConstants.DB_NULL);
                            } // else
                            break;

                        case ParameterConstants.TYPE_VARBYTE: // varbyte parameter
                            byteArr = param.getValueVarByte ();
                            if (byteArr != null)
                            {
                                call.append (UtilConstants.NUM_START_HEX);
                                for (int j = 0; j < byteArr.length; j++)
                                {
                                    call.append (Helpers.byteToString (byteArr [j]));
                                } // for j
                            } // if
                            else
                            {
                                call.append (SQLConstants.DB_NULL);
                            } // else
                            break;

                        case ParameterConstants.TYPE_VARCHAR: // varchar parameter
                            charArr = param.getValueVarChar ();
                            if (charArr != null)
                            {
                                call.append ("'");
                                for (int j = 0; j < charArr.length; j++)
                                {
                                    call.append (charArr[j]);
                                } // for j
                                call.append ("'");
                            } // if
                            else
                            {
                                call.append (SQLConstants.DB_NULL);
                            } // else
                            break;

                        case ParameterConstants.TYPE_UNDEFINED: // undefined parameter type
                        default:                // unknown parameter type
                            call.append ("'").append (param.getValueString ()).append ("'");
                            break;
                    } // switch type
                    break;

                case ParameterConstants.DIRECTION_INOUT: // in/out parameter
                    switch (SQLConstants.DB_TYPE)
                    {
                        case SQLConstants.DB_ORACLE:
                        case SQLConstants.DB_DB2:
                            call.append (varPrefix + "l_var" + i); // write keyword
                            break;

                        case SQLConstants.DB_MSSQL:
                            call.append (varPrefix + "l_var" + i + " INOUT"); // write keyword

                        default: // nothing to do
                    } // switch
                    break;

                case ParameterConstants.DIRECTION_OUT: // out parameter
                    switch (SQLConstants.DB_TYPE)
                    {
                        case SQLConstants.DB_ORACLE:
                            call.append (varPrefix + "l_var" + i); // write keyword
                            eval.append ("debug ('par" + i + "-->' || " + varPrefix + "l_var" + i + ");\n");
                            break;

                        case SQLConstants.DB_DB2:
                            call.append (varPrefix + "l_var" + i); // write keyword
                            eval.append (", " + varPrefix + "l_var" + i + " AS par" + i); // evaluation
                            break;

                        case SQLConstants.DB_MSSQL:
                            call.append (varPrefix + "l_var" + i + " OUTPUT"); // write keyword
                            eval.append (", " + varPrefix + "l_var" + i + " AS par" + i); // evaluation

                        default: // nothing to do
                    } // switch

                    // ensure that the parameter has a defined type:
                    if (type == ParameterConstants.TYPE_UNDEFINED)
                                        // undefined parameter type?
                    {
                        type = ParameterConstants.TYPE_STRING;
                    } // if
                    typeName = typeNames[type];
                    declarations.append (declarePrefix + varPrefix +
                                         "l_var" + i + " " + typeName +
                                         declarePostfix + "\n");
                    break;

                default:                // unknown direction
                    break;
            } // switch direction

            // write delimiter to next parameter:
            if (i < length - 1)         // not last parameter?
            {
                call.append (", ");     // write the comma
            } // if
        } // for

        switch (SQLConstants.DB_TYPE)
        {
            case SQLConstants.DB_ORACLE:
                call.append (");");
                eval.append ("COMMIT WORK;\nEND;\n/");
                break;

            case SQLConstants.DB_DB2:
                call.append (");");
                eval.append ("COMMIT;");
                break;

            case SQLConstants.DB_MSSQL:
                eval.append ("\nGO");

            default: // nothing to do
        } // switch

        return new StringBuffer ()
            .append (comment)
            .append (declarations).append ("\n")
            .append (call).append ("\n")
            .append (eval).append ("\n");
    } // getProcCall



    /**************************************************************************
     * Get the prepared statement for a query. <BR/>
     *
     * @param   stmtStr     The statement.
     *
     * @return  The prepared statement.
     *
     * @deprecated  KR 20090904 Use {@link #getPreparedStatement(StringBuilder)}
     *              instead.
     */
    @Deprecated
    public static PreparedStatement getPreparedStatement (StringBuffer stmtStr)
    {
        // call common method:
        return SQLHelpers.getPreparedStatement (new StringBuilder ().append (stmtStr));
    } // getPreparedStatement


    /**************************************************************************
     * Get the prepared statement for a query. <BR/>
     *
     * @param   stmtStr     The statement.
     *
     * @return  The prepared statement.
     */
    public static PreparedStatement getPreparedStatement (StringBuilder stmtStr)
    {
        SQLAction action = null;    // action object for database access
        PreparedStatement stmt = null; // the statement

        try
        {
            // open db connection - only workaround - db connection must
            // be handled somewhere else
            action = DBConnector.getDBConnection ();

            // prepare the statement:
            stmt = action.getPreparedStatement (stmtStr);

            // finish the action:
            action.end ();
        } // try
        catch (SQLException e)
        {
            // display the error:
            IOHelpers.printError (
                "Could not prepare statement: " + stmtStr, e, true);
        } // catch
        catch (DBError e)
        {
            // close the action:
            if (action != null)
            {
                try
                {
                    // finish the action:
                    action.end ();
                } // try
                catch (DBError e1)
                {
                    // display the error:
                    IOHelpers.printError (
                        "Could not close database connection.", e1, true);
                } // catch
            } // if
/*
                throw new DBQueryException (
                    "Error when opening database connection in SQLManipulationStatement",
                    e);
*/
        } // catch

        // return the statement:
        return stmt;
    } // getPreparedStatement
       
    
    /**************************************************************************
     * Create a filter condition for a query. <BR/>
     * The filter can be either a single value or a comma-separated list
     * surrounded by "(...)" for the IN clause. If the list is not surrounded
     * by brackets they are automatically added. <BR/>
     * The filter may also contain "NULL" or "IS NULL" or "IS NOT NULL" to
     * indicate a check for NULL.
     *
     * @param   attrName    The attribute to be filtered.
     *                      May be something like "o.name", "table.oid", etc.
     * @param   filter      The filter string.
     *
     * @return  The filter condition. <BR/>
     *          <CODE>null</CODE> if the attribute name or the filter where
     *          <CODE>null</CODE>.
     */
    public static final StringBuilder createQueryFilter (String attrName,
                                                          StringBuilder filter)
    {    	
    	// BB20091001: note that a filter can contain whitespace without any data
    	// this will lead to a SQL exception.
    	// Such a contraint must be checked in the appropriate business logic!
        if (attrName != null && filter != null)
        {
        	filter.trimToSize();
            // check if the filter contains the value "NULL":
            if (filter.equals ("NULL") || filter.equals ("IS NULL"))
            {
                return new StringBuilder (attrName).append (" IS NULL");
            } // if
            if (filter.equals ("IS NOT NULL"))
            {
                return new StringBuilder (attrName).append (" IS NOT NULL");
            } // if
            // check if the filter starts and "(" and ends with ")"
            // which indicates a list of values:
            else if (filter.charAt(0) == '(' && 
            		 filter.charAt(filter.length() - 1) == ')')
            {
                return new StringBuilder (attrName)
                    .append (" IN ").append (filter);
            } // if
            // check if the filter contains a "," which also indicates a list of
            // values (but missing "(...)"):
            // BB 20091001: be carefill with value that contain , in their data!
            else if (filter.indexOf (",") > -1)
            {
                return new StringBuilder (attrName)
                    .append (" IN (").append (filter).append (")");
            } // else if
            else
            {
                return new StringBuilder (attrName).append (" = ").append (filter);
            } // else
        } // if

        // no valid filter defined
        return null;
    } // createQueryFilter
            
} // class SQLHelpers

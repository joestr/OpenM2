/******************************************************************************
 * All JavaScript code which is necessary for conversions between different
 * data types. <BR/>
 *
 * @version     $Id: scriptConversion.js,v 1.3 2005/07/28 04:50:11 klreimue Exp $
 *
 * @author      Klaus Reimüller (KR) 20050501
 ******************************************************************************
 */

//============= necessary classes and variables ===============================


//============= declarations ==================================================

var SD = '.';
var ST = ':';
var SDT = ' ';
var MONEY_SUFFIX = ",-";


//============= common functions ==============================================

/**
 * String trimString (String s)
 * Trim leading and ending spaces from a string.
 */
function trimString (s)
{
    s = s.replace (/^\s*([^\s]*(\s+[^\s]+)*)\s*$/, "$1");
    return s;
} // trimString


/**
 * String trimStringZero (String s)
 * Trim leading 0's from content of form field.
 * Also trim leading and ending spaces.
 */
function trimStringZero (s)
{
    s = s.replace (/^\s*0*(\d[\d\.,]*)\s*$/, "$1");
    return s;
} // trimStringZero


/**
 * String trimStringFloat (String s)
 * Trim trailing 0's of float field.
 * All 0's after a '.' at the end are deleted. If there is a '.' at the end it
 * is deleted, too. If the number starts with a '.' a '0' is prepended.
 */
function trimStringFloat (s)
{
    // replace multiple commas and all characters between by one comma:
    s = s.replace (/([\.,]).*[\.,]/, "$1");
    // drop leading and trailing spaces and "0"s:
    s = s.replace (/^\s*(-?)0*([1-9]?\d*\d)([\.,]0*)?\s*$/, "$1$2");
    s = s.replace (/^\s*(-?)0*([1-9]?\d*\d[\.,]\d*[1-9])0*\s*$/, "$1$2");
    // ensure at least "0" before comma:
    s = s.replace (/^(-?)([\.,].*)$/, "$10$2");
    // convert comma to point:
    s = s.replace (/(.*)[\.,](.*)/, "$1.$2");
    // replace "-0" with "0" because "-0" is not a number:
    s = s.replace (/^-0$/, "0");
    return s;
} // trimStringFloat


/**
 * void trimField (FormField f)
 * Trim leading and ending spaces from content of form field.
 */
function trimField (f)
{
    f.value = trimString (f.value);
} // trimField


/**
 * void trimFieldZero (FormField f)
 * Trim leading 0's from content of form field.
 * Also trim leading and ending spaces.
 */
function trimFieldZero (f)
{
    f.value = trimStringZero (f.value);
} // trimFieldZero


/**
 * void trimFieldFloat (FormField f)
 * Trim trailing 0's of float field.
 * All 0's after a '.' at the end are deleted. If there is a '.' at the end it
 * is deleted, too. If the number starts with a '.' a '0' is prepended.
 */
function trimFieldFloat (f)
{
    f.value = trimStringFloat (f.value);
} // trimFieldFloat


//============= number functions ==============================================

/**
 * int stringToInt (String s)
 * Convert a string to an integer value.
 */
function stringToInt (s)
{
    // convert the string to an integer:
    // the string is interpreted as number with radix 10
    return (parseInt (trimStringZero (s), 10));
} // stringToInt


//============= money functions ===============================================

/**
 * int stringToMoney (String s)
 * Convert a string to an integer value.
 */
function stringToMoney (s)
{
    // replace ",-" with ",0":
    s = s.replace (/^(.*[\.,])\-+(.*)$/, "$10$2");
    s = trimStringFloat (s);
    s = s.replace (/^(.*)[\.,](.*)$/, "$1.$2");
    var v = parseFloat (s);
    if (isNaN (v))
    {
        // return the result:
        return v;
    } // if
    else if ("" + v != s)
    {
        // return NaN:
        return parseFloat ("tziu");
    } // else if

    // return the result:
    return v;
} // stringToMoney


/**
 * String moneyToString (float m)
 * Convert a string to an integer value.
 */
function moneyToString (m)
{
    var s = "";

    // ensure not more than 2 digits:
    m = Math.round (m * 100) / 100;
    s = "" + m;

    // check if the number has digits:
    if (Math.floor (m) == m)
    {
        // add default suffix:
        s += MONEY_SUFFIX;
    } // if
    else
    {
        // ensure that there are two digits after comma:
        s = s.replace (/([\.,]\d)$/, "$10");
        // replace '.' with ',':
        s = s.replace (/^(.*)[\.,](.*)$/, "$1,$2");
    } // else

    // return the result:
    return s;
} // moneyToString


//============= date + time functions =========================================

/**
 * Date createDate ()
 * Create a new Date object which is initialized to 0.
 */
function createDate ()
{
    return new Date (0, 0, 1);
} // createDate


/**
 * boolean stringToDate (String dS, Date d)
 * Fill a date object out of a date String.
 * The object is filled with a completely new date value. This means that the
 * time values (hours, minutes, seconds, milliseconds) are set to 0.
 * interpreted formats:
 * - "[d]d[.[m]m[.[[yy]y]y]]"
 * Returns true if the string is a valid date value, false otherwise.
 */
function stringToDate (dS, date)
{
    var i = 0;
    var ok = true;
    var y = 0;
    var m = 0;
    var d = 0;

/*
    dS = dS.replace(/^\s*(\d*)\.?(\d*)\.?(\d*)\s*$/,"00$1.00$2.0000$3");
    dS = dS.replace (/^\d*?(\d\d)\.\d*?(\d\d)\.\d*?(\d\d\d\d)$/, "$1.$2.$3");

    d = parseInt (dS.substring (0, 2), 10);
    m = parseInt (dS.substring (3, 5), 10);
    y = parseInt (dS.substring (6), 10);

    if (y >= 70 && y < 100)
        y += 1900;
    else if (y >= 0 && y < 100)
        y += 2000;

    if (isNaN (y) || y < 1900 || y > 2100 ||
        isNaN (m) || m < 1 || m > 12 ||
        isNaN (d) || d < 1 || d > 31)
    {
        ok = false;
    } // if
*/

    if ((i = dS.indexOf (SD)) > 0)
    {
        d = parseInt (dS.substring (0, i), 10);
        if (!isNaN (d) && d > 0 && d <= 31)
        {
            dS = dS.substring (i + 1);
            if ((i = dS.indexOf (SD)) > 0)
            {
                m = parseInt (dS.substring (0, i), 10);
                if (isNaN (m) || m <= 0 || m > 12)
                    ok = false;
                else if (dS.length > (i + 1))
                {
                    y = parseInt (dS.substring (i + 1), 10);
                    if (isNaN (y) ||
                        (y == 0 && ("" + y) != dS.substring (i + 1) &&
                         dS.substring (i + 1) != "00") || y < 0)
                        ok = false;
                    if (y >= 70 && y < 100)
                        y += 1900;
                    else if (y < 1000)
                        y += 2000;
                    if (y < 1900)
                        ok = false;
                }
                else
                {
                    y = new Date ().getFullYear ();
                }
            }
            else if (dS.length > (i + 1))
            {
                m = parseInt (dS, 10);
                if (isNaN (m) || m <= 0 || m > 12)
                    ok = false;
                else
                    y = new Date ().getFullYear ();
            }
            else
            {
                m = new Date ().getMonth () + 1;
                y = new Date ().getFullYear ();
            } // else
        }
        else
            ok = false;
    }
    else
    {
        d = parseInt (dS, 10);
        if (isNaN (d) || d <= 0 || d > 31)
            ok = false;
        else
        {
            m = new Date ().getMonth () + 1;
            y = new Date ().getFullYear ();
        } // else
    } // else

    if (ok)
    {
        date.setTime (new Date (y, m - 1, d).getTime ());
    } // if

    return ok;
} // stringToDate


/**
 * boolean stringToTime (String tS, Date d)
 * Fill a date object out of a time String.
 * Only the values for hours, minutes and seconds are set, the others stay
 * unchanged.
 * interpreted formats:
 * - "[[h]h][:[m]m][:[[s]s]]]"
 * - "[h]h[mm[ss]]"
 * Returns true if the string is a valid time value, false otherwise.
 */
function stringToTime (tS, date)
{
    var ok = true;
    var i = 0;
    var hrs = 0;
    var mis = 0;
    var scs = 0;

    // insert separators if they are not already there:
    // >= 5 digits:
    tS = tS.replace (/^(\d+)(\d\d)(\d\d)$/, "$1" + ST + "$2" + ST + "$3");
    // 3-4 digits:
    tS = tS.replace (/^(\d+)(\d\d)$/,       "$1" + ST + "$2" + ST + "00");
    // 1-2 digits:
    tS = tS.replace (/^(\d+)$/,             "$1" + ST + "00" + ST + "00");

    // search for first separator:
    if ((i = tS.indexOf (ST)) >= 0)
    {
        hrs = parseInt ("0" + tS.substring (0, i), 10);
        // check if the hours value is valid:
        if (!isNaN (hrs) && (hrs %= 24) >= 0 && hrs <= 23)
        {
            // search for second separator:
            tS = tS.substring (i + 1);
            if ((i = tS.indexOf (ST)) >= 0)
            {
                mis = parseInt ("0" + tS.substring (0, i), 10);
                scs = parseInt ("0" + tS.substring (i + 1), 10);
            } // if
            else if (tS != "")
            {
                mis = parseInt (tS, 10);
            } // else if

            // check if the values for minutes and seconds are valid:
            if (isNaN (mis) || mis < 0 || mis > 59 ||
                isNaN (scs) || scs < 0 || scs > 59)
            {
                ok = false;
            } // if
            else
            {
                date.setHours (hrs);
                date.setMinutes (mis);
                date.setSeconds (scs);
            } // else
        } // if
        else
        {
            ok = false;
        } // else
    } // if
    else                                // the string could not be interpreted
    {
        ok = false;
    } // else the string could not be interpreted

    return ok;
} // stringToTime


/**
 * boolean stringToDateTime (String dS, Date d)
 * Fill a date object out of a date+time String.
 * The object is filled with a completely new date+time value. This means that
 * all values are set except the milliseconds which are set to 0.
 * Returns true if the string is a valid date+time value, false otherwise.
 */
function stringToDateTime (dtS, d)
{
    var ok = true;

    if (dtS == null || dtS == "")
    {
        return false;
    } // if

    var newDate = createDate ();
    var dS = "";
    var tS = "";

    if ((i = dtS.indexOf (SDT)) >= 0)
    {
        dS = dtS.substring (0, i);
        dT = dtS.substring (i + 1);
    } // if
    else
    {
        ds = dtS;
    } // else

    if ((ok = stringToDate (dS, newDate)) &&
        (dT == "" || (ok = stringToTime (dT, newDate))))
    {
        d.setTime (newDate.getTime ());
    } // if
} // stringToDateTime


/**
 * String dateToString (Date d)
 * Create a date string out of a date object.
 */
function dateToString (date)
{
    return ("" +
        (date.getDate () < 10 ? "0" : "") + date.getDate () + SD +
        ((date.getMonth () + 1) < 10 ? "0" : "") + (date.getMonth () + 1) + SD +
        date.getFullYear ()
    );
} // dateToString


/**
 * String timeToString (Date d)
 * Create a time string out of a date object.
 */
function timeToString (date)
{
    return ("" +
        (date.getHours () < 10 ? "0" : "") + date.getHours () + ST +
        (date.getMinutes () < 10 ? "0" : "") + date.getMinutes () +
        // display seconds only if they are > 0:
        (date.getSeconds () > 0 ? ST +
            (date.getSeconds () < 10 ? "0" : "") + date.getSeconds () : "")
    );
} // timeToString


/**
 * String dateTimeToString (Date d)
 * Create a date+time string out of a date object.
 */
function dateTimeToString (date)
{
    if (date == null)
    {
        return null;
    } // if

    return (dateToString (d) + SDT + timeToString (d));
} // dateTimeToString

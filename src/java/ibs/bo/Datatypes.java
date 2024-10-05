/*
 * Class: Datatypes.java
 */

// package:
package ibs.bo;

// imports:
import ibs.bo.BOTokens;


/******************************************************************************
 * Constants containing definitions of datatypes of object properties. <BR/>
 *
 * @version     $Id: Datatypes.java,v 1.18 2010/04/07 13:37:09 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 980427
 ******************************************************************************
 */
public abstract class Datatypes extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Datatypes.java,v 1.18 2010/04/07 13:37:09 rburgermann Exp $";


    // data types used for properties:
    /**
     * Unknown property type. <BR/>
     */
    public static final int DT_UNKNOWN = 0;

    /**
     * Boolean property type. <BR/>
     */
    public static final int DT_BOOL = 1;

    /**
     * Boolean property type with empty option. <BR/>
     */
    public static final int DT_EMPTYBOOL = 5;

    /**
     * Integer property type. <BR/>
     */
    public static final int DT_INTEGER = 11;

    /**
     * integer range property. <BR/>
     */
    public static final int DT_INTEGERRANGE = 12;

    /**
     * Text property type. <BR/>
     */
    public static final int DT_TEXT = 21;

    /**
     * TextArea property. <BR/>
     */
    public static final int DT_TEXTAREA = 31;

    /**
     * Date property type. <BR/>
     */
    public static final int DT_DATE = 51;

    /**
     * Time property type. <BR/>
     */
    public static final int DT_TIME = 52;
    /**
     * Date + Time property type. <BR/>
     */
    public static final int DT_DATETIME = 53;

    /**
     * Date range property. <BR/>
     */
    public static final int DT_DATERANGE = 54;
    /**
     * time range property. <BR/>
     */
    public static final int DT_TIMERANGE = 55;

    /**
     * datetime range property. <BR/>
     */
    public static final int DT_DATETIMERANGE = 56;

    /**
     * REMINDER value. <BR/>
     */
    public static final int DT_REMINDER = 57;

    /**
     * Image. <BR/>
     */
    public static final int DT_IMAGE = 61;

    /**
     * Picture - store. <BR/>
     */
    public static final int DT_PICTURE = 62;

    /**
     * Thumbnail - store. <BR/>
     */
    public static final int DT_THUMBNAIL = 63;

    /**
     * Radio button. <BR/>
     */
    public static final int DT_RADIO = 71;

    /**
     * Selection field. <BR/>
     */
    public static final int DT_SELECT = 81;

    /**
     * Selection field. <BR/>
     */
    public static final int DT_SELECTMULTIPLE = 83;

    /**
     * Selection field - empty allowed. <BR/>
     */
    public static final int DT_SELECTEMPTY = 85;

    /**
     * File property. <BR/>
     */
    public static final int DT_FILE = 91;

    /**
     * Importfile property. <BR/>
     */
    public static final int DT_IMPORTFILE = 92;

    /**
     * Separator between properties. <BR/>
     */
    public static final int DT_HIERARCHY = 93;

    /**
     * WEBDAV File property. <BR/>
     */
    public static final int DT_WEBDAVFOLDER = 94;

    /**
     * Separator between properties. <BR/>
     */
    public static final int DT_SEPARATOR = 99;


    // semantic datatypes:
    /**
     * Object type property. <BR/>
     */
    public static final int DT_TYPE = 201;

    /**
     * Object type property with additional value for all. <BR/>
     */
    public static final int DT_TYPEWITHALL = 202;

    /**
     * User property. <BR/>
     */
    public static final int DT_USER = 211;

    /**
     * Password property. <BR/>
     */
    public static final int DT_PASSWORD = 213;

    /**
     * Combined user/date property. <BR/>
     */
    public static final int DT_USERDATE = 215;

    /**
     * Object name property. <BR/>
     */
    public static final int DT_NAME = 221;

    /**
     * Link to object property. <BR/>
     */
    public static final int DT_LINK = 231;

    /**
     * OBJECTREF value. <BR/>
     */
    public static final int DT_OBJECTREF = 232;

    /**
     * FIELDREF value. <BR/>
     */
    public static final int DT_FIELDREF = 233;

    /**
     * VALUEDOMAIN value. <BR/>
     */
    public static final int DT_VALUEDOMAIN = 234;

    /**
     * Description property. <BR/>
     */
    public static final int DT_DESCRIPTION = 251;

    /**
     * Text to be searched. <BR/>
     */
    public static final int DT_SEARCHTEXT = 261;

    /**
     * Text to be searched with function to immediate perform the search. <BR/>
     */
    public static final int DT_SEARCHTEXTFUNCTION = 262;

    /**
     * CLOB to be searched. <BR/>
     */
    public static final int DT_SEARCHCLOB = 263;

    /**
     * A query field. <BR/>
     */
    public static final int DT_QUERY = 264;

    /**
     * Hidden property. <BR/>
     */
    public static final int DT_HIDDEN = 301;

    /**
     * Email property. <BR/>
     */
    public static final int DT_EMAIL = 401;

    /**
     * URL property. <BR/>
     */
    public static final int DT_URL = 411;

    /**
     * HTML Text. <BR/>
     */
    public static final int DT_HTMLTEXT = 421;

    /**
     * Datatype Number. <BR/>
     */
    public static final int DT_NUMBER = 431;

    /**
     * Number range value. <BR/>
     */
    public static final int DT_NUMBERRANGE = 432;

    /**
     * A multiple selection field. <BR/>
     */
    public static final int DT_MULTISELECT = 441;

    /**
     * Money value. <BR/>
     */
    public static final int DT_MONEY = 451;

    /**
     * Money range value. <BR/>
     */
    public static final int DT_MONEYRANGE = 452;

    /**
     * Search extended value. <BR/>
     */
    public static final int DT_SEARCHTEXT_EXT = 461;

    /**
     * Search number value. <BR/>
     */
    public static final int DT_SEARCHNUMBER = 471;

    /**
     * Search integer value. <BR/>
     */
    public static final int DT_SEARCHINTEGER = 472;

    /**
     * Search date value. <BR/>
     */
    public static final int DT_SEARCHDATE = 481;

    /**
     * Search date range value. <BR/>
     *
     * @deprecated  This property seems to be never used (2006-01-20).
     */
    public static final int DT_SEARCHDATERANGE = 485;

    /**
     * search money value. <BR/>
     */
    public static final int DT_SEARCHMONEY = 491;

    /**
     * search datetime value. <BR/>
     */
    public static final int DT_SEARCHDATETIME = 501;

    /**
     * a selectionbox  filled with query data. <BR/>
     */
    public static final int DT_QUERYSELECTIONBOX = 511;

    /**
     * a selectionbox filled with the options values. <BR/>
     */
    public static final int DT_SELECTIONBOX = 521;

    /**
     * search time value. <BR/>
     */
    public static final int DT_SEARCHTIME = 531;


    /**
     * IDs of the datatypes as a String. <BR/>
     */
    public static final String[] DT_DTIDS =
    {
        Integer.toString (Datatypes.DT_UNKNOWN),
        Integer.toString (Datatypes.DT_BOOL),
        Integer.toString (Datatypes.DT_INTEGER),
        Integer.toString (Datatypes.DT_INTEGERRANGE),
        Integer.toString (Datatypes.DT_TEXT),
        Integer.toString (Datatypes.DT_TEXTAREA),
        Integer.toString (Datatypes.DT_DATE),
        Integer.toString (Datatypes.DT_TIME),
        Integer.toString (Datatypes.DT_DATETIME),
        Integer.toString (Datatypes.DT_DATERANGE),
        Integer.toString (Datatypes.DT_RADIO),
        Integer.toString (Datatypes.DT_SELECT),
        Integer.toString (Datatypes.DT_IMPORTFILE),
        Integer.toString (Datatypes.DT_HIERARCHY),
        Integer.toString (Datatypes.DT_SEPARATOR),
        Integer.toString (Datatypes.DT_TYPE),
        Integer.toString (Datatypes.DT_TYPEWITHALL),
        Integer.toString (Datatypes.DT_USER),
        Integer.toString (Datatypes.DT_PASSWORD),
        Integer.toString (Datatypes.DT_USERDATE),
        Integer.toString (Datatypes.DT_NAME),
        Integer.toString (Datatypes.DT_LINK),
        Integer.toString (Datatypes.DT_DESCRIPTION),
        Integer.toString (Datatypes.DT_SEARCHTEXT),
        Integer.toString (Datatypes.DT_HIDDEN),
        Integer.toString (Datatypes.DT_EMAIL),
        Integer.toString (Datatypes.DT_URL),
        Integer.toString (Datatypes.DT_QUERYSELECTIONBOX),
        Integer.toString (Datatypes.DT_SELECTIONBOX),
    }; // DT_DTIDS

    /**
     * Names of the data types. <BR/>
     */
    public static final String[] DT_DTNAMES =
    {
        BOTokens.ML_DTUNKNOWN,
        BOTokens.ML_DTBOOL,
        BOTokens.ML_DTINTEGER,
        BOTokens.ML_DTINTEGERRANGE,
        BOTokens.ML_DTTEXT,
        BOTokens.ML_DTTEXTAREA,
        BOTokens.ML_DTDATE,
        BOTokens.ML_DTTIME,
        BOTokens.ML_DTDATETIME,
        BOTokens.ML_DTDATERANGE,
        BOTokens.ML_DTRADIO,
        BOTokens.ML_DTSELECT,
        BOTokens.ML_DTFILE,
        BOTokens.ML_DTIMPORTFILE,
        BOTokens.ML_DTHIERARCHY,
        BOTokens.ML_DTSEPARATOR,
        BOTokens.ML_DTTYPE,
        BOTokens.ML_DTTYPEWITHALL,
        BOTokens.ML_DTUSER,
        BOTokens.ML_DTPASSWORD,
        BOTokens.ML_DTUSERDATE,
        BOTokens.ML_DTNAME,
        BOTokens.ML_DTLINK,
        BOTokens.ML_DTDESCRIPTION,
        BOTokens.ML_DTSEARCHTEXT,
        BOTokens.ML_DTHIDDEN,
        BOTokens.ML_DTEMAIL,
        BOTokens.ML_DTURL,
        BOTokens.ML_DTQUERYSELECTIONBOX,
        BOTokens.ML_DTSELECTIONBOX,
    }; // DT_DTNAMES

    /**
     * Data type ids which may be chosen for the data type of a property. <BR/>
     */
    public static final String[] DT_IDSELECT =
    {
        Integer.toString (Datatypes.DT_UNKNOWN),
        Integer.toString (Datatypes.DT_BOOL),
        Integer.toString (Datatypes.DT_INTEGER),
        Integer.toString (Datatypes.DT_TEXT),
        Integer.toString (Datatypes.DT_DATE),
        Integer.toString (Datatypes.DT_TIME),
        Integer.toString (Datatypes.DT_DATETIME),
        Integer.toString (Datatypes.DT_FILE),
        Integer.toString (Datatypes.DT_IMPORTFILE),
        Integer.toString (Datatypes.DT_HIERARCHY),
        Integer.toString (Datatypes.DT_USER),
        Integer.toString (Datatypes.DT_PASSWORD),
        Integer.toString (Datatypes.DT_NAME),
        Integer.toString (Datatypes.DT_LINK),
        Integer.toString (Datatypes.DT_DESCRIPTION),
        Integer.toString (Datatypes.DT_EMAIL),
        Integer.toString (Datatypes.DT_URL),
        Integer.toString (Datatypes.DT_QUERYSELECTIONBOX),
        Integer.toString (Datatypes.DT_SELECTIONBOX),
    }; // DT_IDSELECT

    /**
     * Data types which may be chosen for the data type of a property. <BR/>
     */
    public static final String[] DT_NAMESELECT =
    {
        BOTokens.ML_DTUNKNOWN,
        BOTokens.ML_DTBOOL,
        BOTokens.ML_DTINTEGER,
        BOTokens.ML_DTTEXT,
        BOTokens.ML_DTDATE,
        BOTokens.ML_DTTIME,
        BOTokens.ML_DTDATETIME,
        BOTokens.ML_DTFILE,
        BOTokens.ML_DTIMPORTFILE,
        BOTokens.ML_DTHIERARCHY,
        BOTokens.ML_DTUSER,
        BOTokens.ML_DTPASSWORD,
        BOTokens.ML_DTNAME,
        BOTokens.ML_DTLINK,
        BOTokens.ML_DTDESCRIPTION,
        BOTokens.ML_DTEMAIL,
        BOTokens.ML_DTURL,
        BOTokens.ML_DTQUERYSELECTIONBOX,
        BOTokens.ML_DTSELECTIONBOX,
    }; // DT_NAMESELECT


    // string representations of boolean values:
    /**
     * Boolean value for true as String. <BR/>
     */
    public static final String BOOL_TRUE = "true";

    /**
     * Boolean value for false as String. <BR/>
     */
    public static final String BOOL_FALSE = "false";

    /**
     * Maximum length of content field. <BR/>
     */
    public static final int LEN_CONTENT = 65025;

} // class Datatypes

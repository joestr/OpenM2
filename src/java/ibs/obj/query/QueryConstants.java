/*
 * Class: QueryConstants.java
 */

// package:
package ibs.obj.query;

// imports:


/******************************************************************************
 * Constants for QueryObjects. <BR/>
 * This abstract class contains all constants which are necessary to deal with
 * the classes delivered within this package. <P>
 *
 * @version     $Id: QueryConstants.java,v 1.30 2010/04/21 17:46:07 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ), 000927
 ******************************************************************************
 */
public abstract class QueryConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: QueryConstants.java,v 1.30 2010/04/21 17:46:07 rburgermann Exp $";


    /**
     * possible delimiters for searchfields, columnheaders ...
     * in querydefinition. <BR/>
     * BB20080708: was set to ";,". The ";" is not supported anymore because
     * it hinders a more detailed search parameter configuration like
     * TYPE(PARAM1;PARAM2)
     * Check for sideeffects!
     */
    public static final String CONST_DELIMITER  = ",";

    /**
     * token for selection of querytype
     */
    public static String[] CONST_QUERYTYPE_SELECTION =
    {
        "SEARCH",
        "REPORT",
        "SYSTEM",
    };

    /**
     * No query type. <BR/>
     */
    public static final int QT_NONE = 0;

    /**
     * Query type SEARCH. <BR/>
     */
    public static final int QT_SEARCH = 1;

    /**
     * Query type REPORT. <BR/>
     */
    public static final int QT_REPORT = 2;

    /**
     * Query type SYSTEM. <BR/>
     */
    public static final int QT_SYSTEM = 4;


    /**
     * undefined searchvalue or matchtype for searchfield.
     */
    public static final String CONST_UNDEF  = "<undef>";
    /**
     *
     */
    public static final String CONST_UNDEF2  = "<u>";


    // special SYSTEMVARIABLES:
    /**
     *
     */
    public static final String SYSVAR_ELEMOID  = "#SYSVAR.ELEMOID#";
    /**
     *
     */
    public static final String SYSVAR_CURRENTOBJECTOID = "#SYSVAR.CURRENTOBJECTOID#";
    /**
     *
     */
    public static final String SYSVAR_CURRENTCONTAINERID = "#SYSVAR.CURRENTCONTAINERID#";

    /**
     * only for downcompatibility - befor version R2.2AddOn1
     * the new userid looks like: #SYSVAR.USERID#
     */
    public static final String SYSVAR_USERID  = "#USERID";


    /**
     * fieldvalidation for inputfield. <BR/>
     */
    public static final String SYSVAR_FIELDVALIDATION
        = "#SYSVAR.FIELDVALIDATION#";

    /**
     * character which marks a refence column to get an alternative text 
     * for headerlisting or queryattributes
     */
    public static final String MARK_REFCOL  = "/";


    // FUNCTIONAL COLUMNS fo describe columnattributes in query - set
    // in queryCreator (columnnames)


    /**
     * character which marks M2-FUNCTIONAL COLUMNS used in headerlisting
     * for queryattributes
     */
    public static final char MARK_FUNCCOL  = '#';

    /**
     * means that the queryattribute mapped to this m2-systemvariable
     * is the oid of the current element in the resultset
     */
    public static final String FUNCCOL_OBJECTID =
        QueryConstants.MARK_FUNCCOL + "OBJECTID";

    /**
     * means that the queryattribute mapped to this m2-systemvariable
     * are the flag of the current line in resultset for the current user.
     * could only be used in combination with SYSVAR_USERID
     */
    public static final String FUNCCOL_ISNEW =
        QueryConstants.MARK_FUNCCOL + "ISNEW";

    /**
     * means that the queryattribute mapped to this m2-systemvariable
     * marks that the current object in resultset is link to an otherobject
     */
    public static final String FUNCCOL_ISLINK =
        QueryConstants.MARK_FUNCCOL + "ISLINK";

    /**
     * means that the queryattribute mapped to this m2-systemvariable
     * is the name of the image mapped to the object in the resultset.
     * this image has to exist in the objecticons - directory of the layouts
     */
    public static final String FUNCCOL_TYPEIMAGE =
        QueryConstants.MARK_FUNCCOL + "TYPEIMAGE";

    /**
     * means that the queryattribute mapped to this m2-systemvariable
     * is the name http-path to an image. this image is shown in the
     * resultlist of QueryExecutive.
     */
    public static final String FUNCCOL_IMAGE =
        QueryConstants.MARK_FUNCCOL + "IMAGE";


    /**
     * XML tag used for defining a query. <BR/>
     */
    public static final String XML_QUERY = "QUERY";

    /**
     * XML attribute: oid of container. <BR/>
     */
    public static final String XMLA_CONTAINEROID = "CONTAINEROID";


    // FIELDTYPES to describe type of searchfield

    /**
     * Indicates that the sql-type of an attribute is an object id
     * (BINARY (8), RAW (8), VARCHAR (8) FOR BIT DATA, ...). <BR/>
     */
    public static final String FIELDTYPE_OBJECTID = "OBJECTID";
    /**
     * Indicates that the queryattribute mapped to this m2-systemvariable
     * is the path of object in the systemtree (posNoPath in ibs_Object).
     */
    public static final String FIELDTYPE_OBJECTPATH = "OBJECTPATH";

    /**
     * Indicates that the sql-type of an attribute is any type of string
     * (CHAR, VARCHAR, VARCHAR2, ...). <BR/>
     */
    public static final String FIELDTYPE_STRING  = "STRING";
    /**
     * Indicates that the sql-type of an attribute is any type of long string
     * (TEXT, CLOB, ...). <BR/>
     */
    public static final String FIELDTYPE_LONGTEXT  = "LONGTEXT";

    /**
     * Indicates that the sql-type of an attribute is boolean
     * (BOOL, BIT, NUMBER (1), SMALLINT, ...). <BR/>
     */
    public static final String FIELDTYPE_BOOLEAN  = "BOOLEAN";

    /**
     * Indicates that the sql-type of an attribute is an integer-like type
     * (INTEGER, INT, BIGINT, SMALLINT, TINYINT, BIT, LONG, ...). <BR/>
     */
    public static final String FIELDTYPE_INTEGER = "INTEGER";
    /**
     * Indicates that the sql-type of an attribute is an integer and
     * values in resultset due to this searchfield are less than the value of
     * the searchfield. <BR/>
     */
    public static final String FIELDTYPE_LESS_INTEGER = "LESSINTEGER";
    /**
     * Indicates that the sql-type of an attribute is an integer and
     * values in resultset due to this searchfield are less than or equal to
     * the value of the searchfield. <BR/>
     */
    public static final String FIELDTYPE_LESS_EQUAL_INTEGER =
        "LESSEQUALINTEGER";
    /**
     * Indicates that the sql-type of an attribute is an integer and
     * values in resultset due to this searchfield are bigger than the value of
     * the searchfield. <BR/>
     */
    public static final String FIELDTYPE_GREATER_INTEGER = "GREATERINTEGER";
    /**
     * Indicates that the sql-type of an attribute is an integer and
     * values in resultset due to this searchfield are bigger than or equal to
     * the value of the searchfield. <BR/>
     */
    public static final String FIELDTYPE_GREATER_EQUAL_INTEGER =
        "GREATEREQUALINTEGER";
    /**
     * Indicates that the sql-type of an attribute is an integer and
     * values in resultset due to this searchfield are in the range between
     * a lower and an upper limit. <BR/>
     */
    public static final String FIELDTYPE_INTEGERRANGE = "INTEGERRANGE";

    /**
     * Indicates that the sql-type of an attribute is any type of number
     * (INTEGER, INT, BIGINT, SMALLINT, TINYINT, LONG, FLOAT, DOUBLE, NUMBER,
     * DECIMAL, NUMERIC, REAL, ...). <BR/>
     */
    public static final String FIELDTYPE_NUMBER = "NUMBER";
    /**
     * Indicates that the sql-type of an attribute is any type of number and
     * values in resultset due to this searchfield are less than the value of
     * the searchfield. <BR/>
     */
    public static final String FIELDTYPE_LESS_NUMBER = "LESSNUMBER";
    /**
     * Indicates that the sql-type of an attribute is any type of number and
     * values in resultset due to this searchfield are less than or equal to
     * the value of the searchfield. <BR/>
     */
    public static final String FIELDTYPE_LESS_EQUAL_NUMBER = "LESSEQUALNUMBER";
    /**
     * Indicates that the sql-type of an attribute is any type of number and
     * values in resultset due to this searchfield are bigger than the value of
     * the searchfield. <BR/>
     */
    public static final String FIELDTYPE_GREATER_NUMBER = "GREATERNUMBER";
    /**
     * Indicates that the sql-type of an attribute is any type of number and
     * values in resultset due to this searchfield are bigger than or equal to
     * the value of the searchfield. <BR/>
     */
    public static final String FIELDTYPE_GREATER_EQUAL_NUMBER  =
        "GREATEREQUALNUMBER";
    /**
     * Indicates that the sql-type of an attribute is any type of number and
     * values in resultset due to this searchfield are in the range between
     * a lower and an upper limit. <BR/>
     */
    public static final String FIELDTYPE_NUMBERRANGE = "NUMBERRANGE";

    /**
     * Indicates that the sql-type of an attribute is any type of money
     * (MONEY, SMALLMONEY, NUMBER (19,4), DECIMAL (19,4), ...). <BR/>
     */
    public static final String FIELDTYPE_MONEY = "MONEY";
    /**
     * Indicates that the sql-type of an attribute is money and
     * values in resultset due to this searchfield are less than the value of
     * the searchfield. <BR/>
     */
    public static final String FIELDTYPE_LESS_MONEY = "LESSMONEY";
    /**
     * Indicates that the sql-type of an attribute is money and
     * values in resultset due to this searchfield are less than or equal to
     * the value of the searchfield. <BR/>
     */
    public static final String FIELDTYPE_LESS_EQUAL_MONEY = "LESSEQUALMONEY";
    /**
     * Indicates that the sql-type of an attribute is money and
     * values in resultset due to this searchfield are bigger than the value of
     * the searchfield. <BR/>
     */
    public static final String FIELDTYPE_GREATER_MONEY = "GREATERMONEY";
    /**
     * Indicates that the sql-type of an attribute is money and
     * values in resultset due to this searchfield are bigger than or equal to
     * the value of the searchfield. <BR/>
     */
    public static final String FIELDTYPE_GREATER_EQUAL_MONEY =
        "GREATEREQUALMONEY";
    /**
     * Indicates that the sql-type of an attribute is money and
     * values in resultset due to this searchfield are in the range between
     * a lower and an upper limit. <BR/>
     */
    public static final String FIELDTYPE_MONEYRANGE = "MONEYRANGE";

    /**
     * Indicates that the sql-type of an attribute is DATE
     * (DATETIME, SMALLDATETIME, DATE, TIMESTAMP, ...). <BR/>
     * Format: 'DD.MM.YYYY'
     */
    public static final String FIELDTYPE_DATE = "DATE";
    /**
     * Indicates that the sql-type of an attribute is DATE-TIME
     * (DATETIME, SMALLDATETIME, DATE, TIMESTAMP, ...). <BR/>
     * Format: 'DD.MM.YYYY HH:MI'
     */
    public static final String FIELDTYPE_DATETIME = "DATETIME";
    /**
     * Indicates that the sql-type of an attribute is date and
     * values in resultset due to this searchfield are less than the value of
     * the searchfield. <BR/>
     */
    public static final String FIELDTYPE_LESS_DATE = "LESSDATE";
    /**
     * Indicates that the sql-type of an attribute is date and
     * values in resultset due to this searchfield are less than or equal to
     * the value of the searchfield. <BR/>
     */
    public static final String FIELDTYPE_LESS_EQUAL_DATE  = "LESSEQUALDATE";
    /**
     * Indicates that the sql-type of an attribute is date and
     * values in resultset due to this searchfield are bigger than the value of
     * the searchfield. <BR/>
     */
    public static final String FIELDTYPE_GREATER_DATE  = "GREATERDATE";
    /**
     * Indicates that the sql-type of an attribute is date and
     * values in resultset due to this searchfield are bigger than or equal to
     * the value of the searchfield. <BR/>
     */
    public static final String FIELDTYPE_GREATER_EQUAL_DATE  = "GREATEREQUALDATE";
    /**
     * Indicates that the sql-type of an attribute is date and
     * values in resultset due to this searchfield are in the range between
     * a lower and an upper limit. <BR/>
     */
    public static final String FIELDTYPE_DATERANGE = "DATERANGE";
    /**
     * Indicates that the sql-type of an attribute is date-time and
     * values in resultset due to this searchfield are in the range between
     * a lower and an upper limit. <BR/>
     */
    public static final String FIELDTYPE_DATETIMERANGE = "DATETIMERANGE";

    /**
     * Indicates that the sql-type of an attribute is TIME
     * (DATETIME, SMALLDATETIME, DATE, TIMESTAMP, ...). <BR/>
     * Format: 'HH:MI'
     */
    public static final String FIELDTYPE_TIME  = "TIME";
    /**
     * Indicates that the sql-type of an attribute is time and
     * values in resultset due to this searchfield are less than the value of
     * the searchfield. <BR/>
     */
    public static final String FIELDTYPE_LESS_TIME  = "LESSTIME";
    /**
     * Indicates that the sql-type of an attribute is time and
     * values in resultset due to this searchfield are less than or equal to
     * the value of the searchfield. <BR/>
     */
    public static final String FIELDTYPE_LESS_EQUAL_TIME  = "LESSEQUALTIME";
    /**
     * Indicates that the sql-type of an attribute is time and
     * values in resultset due to this searchfield are bigger than the value of
     * the searchfield. <BR/>
     */
    public static final String FIELDTYPE_GREATER_TIME  = "GREATERTIME";
    /**
     * Indicates that the sql-type of an attribute is time and
     * values in resultset due to this searchfield are bigger than or equal to
     * the value of the searchfield. <BR/>
     */
    public static final String FIELDTYPE_GREATER_EQUAL_TIME  = "GREATEREQUALTIME";
    /**
     * Indicates that the sql-type of an attribute is time and
     * values in resultset due to this searchfield are in the range between
     * a lower and an upper limit. <BR/>
     */
    public static final String FIELDTYPE_TIMERANGE = "TIMERANGE";

    /**
     * Indicates that the sql-type of an attribute is VARCHAR
     * preselected item of the selectionbox
     */
    public static final String FIELDTYPE_QUERYSELECTION  = "QUERYSELECTION";
    /**
     * Indicates that the sql-type of an attribute is NUMBER
     * preselected item of the selectionbox
     */
    public static final String FIELDTYPE_QUERYSELECTIONNUM
        = "QUERYSELECTIONNUM";
    /**
     * Indicates that the sql-type of an attribute is OBJECTID
     * preselected item of the selectionbox
     */
    public static final String FIELDTYPE_QUERYSELECTIONOID
        = "QUERYSELECTIONOID";

    /**
     * Indicates that the sql-type of an attribute is VARCHAR
     * preselected item of the selectionbox for VALUEDOMAINS
     */
    public static final String FIELDTYPE_VALUEDOMAIN
        = "VALUEDOMAIN";
    /**
     * Indicates that the sql-type of an attribute is NUMBER
     * preselected item of the selectionbox for VALUEDOMAINS
     */
    public static final String FIELDTYPE_VALUEDOMAINNUM
        = "VALUEDOMAINNUM";
    /**
     * Indicates that the sql-type of an attribute is OBJECTID
     * preselected item of the selectionbox for VALUEDOMAINS
     */
    public static final String FIELDTYPE_VALUEDOMAINOID
        = "VALUEDOMAINOID";

// FIELDTYPE modifier

    /**
     * possible delimiters for field type modifier parameters
     * in querydefinition.
     */
    public static final String FIELDTYPE_MODIFIER_PARAM_DELIMITER  = ":";

    /**
     * modifier multiple in field type expression
     * in querydefinition.
     */
    public static final String FIELDTYPE_MODIFIER_MULTIPLE  = "MULTIPLE";

   // COLUMNTYPES to describe type of columndata

    /**
     * columncontent is just a string
     */
    public static final String COLUMNTYPE_STRING = "STRING";

    /**
     * columncontent is a boolean
     */
    public static final String COLUMNTYPE_BOOLEAN = "BOOLEAN";

    /**
     * columncontent is a number (floatingpoint)
     */
    public static final String COLUMNTYPE_NUMBER = "NUMBER";


    /**
     * columncontent is a integer
     */
    public static final String COLUMNTYPE_INTEGER = "INTEGER";


    /**
     * columncontent is a long, which represents a money-value
     */
    public static final String COLUMNTYPE_MONEY = "MONEY";


    /**
     * columncontent is just a date
     */
    public static final String COLUMNTYPE_DATE = "DATE";

    /**
     * columncontent is a time
     */
    public static final String COLUMNTYPE_TIME = "TIME";

    /**
     * columncontent is a date with time
     */
    public static final String COLUMNTYPE_DATETIME = "DATETIME";


    /**
     * columncontent is content for a systemvariable
     */
    public static final String COLUMNTYPE_FUNCCOL = "SYSVAR";

    /**
     * columncontent is url to an image  (DRAFT - use as FUNCCOL)
     */
    public static final String COLUMNTYPE_IMAGE = "IMAGE";

    /**
     * columncontent oid
     */
    public static final String COLUMNTYPE_OBJECTID = "OBJECTID";


    /**
     * columncontent is a button
     */
    public static final String COLUMNTYPE_BUTTON = "BUTTON";

    /**
     * columncontent is a button_text
     */
    public static final String COLUMNTYPE_BUTTON_TEXT = "BUTTON_TEXT";

    /**
     * columncontent is a button_image
     */
    public static final String COLUMNTYPE_BUTTON_IMAGE = "BUTTON_IMAGE";

    /**
     * Column content is an input_string. <BR/>
     */
    public static final String COLUMNTYPE_INPUT_STRING = "INPUT_STRING";

    /**
     * Column content is an input_integer. <BR/>
     */
    public static final String COLUMNTYPE_INPUT_INTEGER = "INPUT_INTEGER";

    /**
     * Column content is an input_number. <BR/>
     */
    public static final String COLUMNTYPE_INPUT_NUMBER = "INPUT_NUMBER";

    /**
     * Column content is an input_money. <BR/>
     */
    public static final String COLUMNTYPE_INPUT_MONEY = "INPUT_MONEY";

    /**
     * Column content is an input_date. <BR/>
     */
    public static final String COLUMNTYPE_INPUT_DATE = "INPUT_DATE";

    /**
     * Column content is a value domain. <BR/>
     */
    public static final String COLUMNTYPE_VALUEDOMAIN = "VALUEDOMAIN";

// COLUMNTYPE modifier

    /**
     * opening bracket for column type modifier
     * in querydefinition.
     */
    public static final String COLUMNTYPE_MODIFIER_PARAM_OPENING_BRACKET  = "(";

    /**
     * possible delimiters for column type modifier parameters
     * in querydefinition.
     */
    public static final String COLUMNTYPE_MODIFIER_PARAM_DELIMITER  = ":";

    /**
     * closing bracket for column type modifier
     * in querydefinition.
     */
    public static final String COLUMNTYPE_MODIFIER_PARAM_CLOSING_BRACKET  = ")";

    /**
     * modifier multiple in type modifier expression
     * in querydefinition.
     */
    public static final String COLUMNTYPE_MODIFIER_MULTIPLE  = "MULTIPLE";

// MATCHTYPES

    /**
     * code for no restrictions. <BR/>
     */
    public static final String MATCH_NONE       = "0";

    /**
     * code for substring string match. <BR/>
     */
    public static final String MATCH_SUBSTRING  = "1";

    /**
     * code for exact string match . <BR/>
     */
    public static final String MATCH_EXACT      = "2";

    /**
     * code for soundex match . <BR/>
     */
    public static final String MATCH_SOUNDEX    = "3";

    /**
     * code for greater number match. <BR/>
     */
    public static final String MATCH_GREATER    = "4";

    /**
     * code for less number match. <BR/>
     */
    public static final String MATCH_LESS       = "5";

    /**
     * code for greater-equal number match. <BR/>
     */
    public static final String MATCH_GREATEREQUAL = "6";

    /**
     * code for less-equal number match. <BR/>
     */
    public static final String MATCH_LESSEQUAL = "7";



// EXCEPTIONS

    /**
     * if not supported fieldtype is used
     */
    public static String EXC_WRONGFIELDTYPE =
        "ERROR: the searchfieldtype for this field is not valid.";

    /**
     * error at columtype-syntax, no " (" or ")"
     */
    public static String EXC_WRONGCOLUMNTYPE =
        "ERROR: at columtype-syntax.";

     /**
     * an IncorrectOidException is caught
     */
    public static String EXC_INCORRECTOIDEXCEPTION =
        "ERROR: Invalid oid in searchfield ";

    /**
     * Incorrect inputfieldtype for inputfield in queryresult
     */
    public static String EXC_COLUMNTYPENOTEXIST =
        "ERROR: unknown columntype ";

    /**
     * no oid set for button
     */
    public static String EXC_NOOID =
        "Error: no oid set for button";

    /**
     * no javascript set for button
     */
    public static String EXC_NOJAVASCRIPT =
        "Error: no javascript set for button";

    /**
     * The #IGNORE# tag can be used within the searchfieldattributes
     * of a query to disable adding the searchfieldattributes to the where
     * clause of the query.
     * The searchfield can still be used in the query by adding
     * any #SYSVAR.QUERY_&lt;searchfield&gt;# to the query definition
     */
    public static final String CONST_IGNORE =
        QueryConstants.MARK_FUNCCOL + "IGNORE";

    /**
     * Queryselectionbox paramter for multiple selection. <BR/>
     */
    public static String QUERYSELECTIONBOX_PARAM_MULTIPLE = "MULTIPLE";


    /**
     * undefined searchvalue or matchtype for searchfield are set to 'null'.
     */
    public static final String CONST_NULL  = "null";
} // class QueryConstants

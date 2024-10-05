/*
 * Class: QueryTokens.java
 */

// package:
package ibs.obj.query;

import ibs.bo.BOTokens;

// imports:


/******************************************************************************
 * Tokens for GUI of QueryObjects. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the classes within this package. <P>
 *
 * @version     $Id: QueryTokens.java,v 1.11 2010/04/07 13:37:09 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ), 000927
 ******************************************************************************
 */
public abstract class QueryTokens extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: QueryTokens.java,v 1.11 2010/04/07 13:37:09 rburgermann Exp $";

    /**
     * Name of bundle where the tokens included. <BR/>
     */
    public static String TOK_BUNDLE = BOTokens.TOK_BUNDLE;

    // tokens for queryexecutive

    /**
     * token for selection of query creator
     */
    public static String ML_QUERYCREATOR  = "ML_QUERYCREATOR";



    /**
     * token for global search in whole m2
     */
    public static String ML_SEARCHGLOBAL  = "ML_SEARCHGLOBAL";


    /**
     * token for search in current container
     */
    public static String ML_SEARCHLOCAL  = "ML_SEARCHLOCAL";


    /**
     * token for search values of QueryExecutive
     */
    public static String ML_SEARCHVALUES = "ML_SEARCHVALUES";


    /**
     * token for selection of rootobject
     * it is not used in java, but needed in multilingualtable in database
     * for the creation of the standard querycreators
     */
    public static String ML_ROOTOBJECTSELECTION = "ML_ROOTOBJECTSELECTION";

    /**
     * Token for displaying the search form. <BR/>
     */
    public static String ML_SHOWSEARCHFORM = "ML_SHOWSEARCHFORM";

// tokens for querycreator

// not needed now
/*
    public static final String ML_SELECT = "SELECT";
    public static final String ML_FROM = "FROM";
    public static final String ML_WHERE = "WHERE";
    public static final String ML_ORDERBY = "ORDER BY";
    public static final String ML_COLUMNHEADERS = "Spaltennamen";
    public static final String ML_COLUMNQUERYATTRIBUTES =
        "Queryattriubte (relativ zu den Spaltennamen)";
    public static final String ML_COLUMNTYPES = "Spaltentypen";
    public static final String ML_SEARCHFIELDS = "Suchfelder";
    public static final String ML_SEARCHFIELDQUERYATTRIBUTES =
        "Suchfelderattribute (relativ zu den Suchfeldern)";
    public static final String ML_SEARCHFIELDTYPES = "Suchfeldertypen";
*/

    /**
     * Token for the search button. <BR/>
     */
    public static String ML_SEARCH = "ML_SEARCH";

    /**
     * Token for the new search button. <BR/>
     */
    public static String ML_NEW_SEARCH = "ML_NEW_SEARCH";

    /**
     * Token for the save button. <BR/>
     */
    public static String ML_SAVE = "ML_SAVE";

    /**
     * Token for the save button title. <BR/>
     */
    public static String ML_SAVE_TITLE = "ML_SAVE_TITLE";

    /**
     * Token for the open report button. <BR/>
     */
    public static String ML_OPEN_REPORT = "ML_OPEN_REPORT";

    /**
     * Token for the open report button title. <BR/>
     */
    public static String ML_OPEN_REPORT_TITLE = "ML_OPEN_REPORT_TITLE";

    /*
     * TODO: NEW TOKENS
     */
    /**
     * Token for query . <BR/>
     */
    public static String ML_QUERY = "ML_QUERY";

    /**
     * Token for query use for. <BR/>
     */
    public static String ML_QUERYTYPE = "ML_QUERYTYPE";

    /**
     * Token for column headers. <BR/>
     */
    public static String ML_COLUMNHEADERS = "ML_COLUMNHEADERS";

    /**
     * Token for column attributes. <BR/>
     */
    public static String ML_COLUMNATTRIBUTES = "ML_COLUMNATTRIBUTES";

    /**
     * Token for column types. <BR/>
     */
    public static String ML_COLUMNTYPES = "ML_COLUMNTYPES";

    /**
     * Token for resultfield names. <BR/>
     */
    public static String ML_FIELDNAMES = "ML_FIELDNAMES";

    /**
     * Token for resultfield attributes. <BR/>
     */
    public static String ML_FIELDATTRIBUTES = "ML_FIELDATTRIBUTES";

    /**
     * Token for resultfield types. <BR/>
     */
    public static String ML_FIELDTYPES = "ML_FIELDTYPES";

    /**
     * Token for maximal query results. <BR/>
     */
    public static String ML_MAX_RESULTS = "ML_MAX_RESULTS";

    /**
     * Token for show query. <BR/>
     */
    public static String ML_SHOW_QUERY = "ML_SHOW_QUERY";

    /**
     * Token for query category. <BR/>
     */
    public static String ML_CATEGORY = "ML_CATEGORY";

    /**
     * Token: open query definition. <BR/>
     */
    public static String ML_OPEN_QUERY_DEFINITION = "ML_OPEN_QUERY_DEFINITION";

    /**
     * Token: open query. <BR/>
     */
    public static String ML_OPEN_QUERY = "ML_OPEN_QUERY";

    /**
     * Token: export query. <BR/>
     */
    public static String ML_EXPORT_QUERY = "ML_EXPORT_QUERY";


} // class QueryTokens

/*
 * Class: QueryArguments.java
 */

// package:
package ibs.obj.query;

// imports:

/*******************************************************************************
 * Arguments for GUI of QueryObjects. <BR/>
 * This abstract class contains all constants which are necessary to deal with
 * the classes delivered within this package.
 * <P>
 *
 * @version $Id: QueryArguments.java,v 1.14 2009/07/23 23:46:33 kreimueller Exp $
 *
 * @author Andreas Jansa (AJ), 001019
 *         *****************************************************************************
 */
public abstract class QueryArguments extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag to
     * ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: QueryArguments.java,v 1.14 2009/07/23 23:46:33 kreimueller Exp $";

    /**
     * argument for selection of querycreator (returns the oid)
     */
    public static final String ARG_QUERYCREATOR = "qcroid";

    /**
     * argument for name of selected querycreator
     */
    public static final String ARG_QUERYCREATORNAME = "qcrname";

    /**
     * argument for searchfiled in queryexecutive
     */
    public static final String ARG_SEARCHFIELD = "fie";

    /**
     * argument for selection of rootobject of search
     */
    public static final String ARG_ROOTOBJECTSELECTION = "rootobj";

    /**
     * oid of current object, when search was started
     */
    public static final String ARG_CURRENTOBJECTOID = "curoid";

    /**
     * argument for message for invalid fieldtype
     */
    public static final String ARG_FIELDTYPEERROR = "fterror";

    /**
     * argument for searchvalues in queryexecutive
     */
    public static final String ARG_SEARCHVALUES = "srchvls";

    /**
     * Argument which indicates if the search form for a query executive shall
     * be displayed. <BR/>
     */
    public static final String ARG_SHOWSEARCHFORM = "shwsrchfrm";

    // QueryCreatorArguments

    /**
     *
     */
    public static final String ARG_QUERY = "query";

    /**
     *
     */
    public static final String ARG_QUERYTYPE = "quty";

    /**
     *
     */
    public static final String ARG_SELECT = "sel";

    /**
     *
     */
    public static final String ARG_FROM = "fro";

    /**
     *
     */
    public static final String ARG_WHERE = "whe";

    /**
     *
     */
    public static final String ARG_GROUPBY = "grby";

    /**
     *
     */
    public static final String ARG_ORDERBY = "orby";

    /**
     *
     */
    public static final String ARG_COLUMNHEADERS = "colhe";

    /**
     *
     */
    public static final String ARG_COLUMNATTRIBUTES = "colatr";

    /**
     *
     */
    public static final String ARG_COLUMNTYPES = "colty";

    /**
     *
     */
    public static final String ARG_FIELDNAMES = "fiena";

    /**
     *
     */
    public static final String ARG_FIELDATTRIBUTES = "fieatr";

    /**
     *
     */
    public static final String ARG_FIELDTYPES = "fiety";

    /**
     * argument for the resultCounter field of a querycreator
     */
    public static final String ARG_RESULTCOUNTER = "recou";

    /**
     * argument for the checkbox to enable show debugging
     */
    public static final String ARG_DEBUGGING = "ende";

    /**
     * Argument for the oid of the connector. <BR/>
     */
    public static final String ARG_CONNECTOROID = "conoid";

    /**
     * Argument for the category. <BR/>
     */
    public static final String ARG_CATEGORY = "cat";

    /**
     * Argument for the category selection box. <BR/>
     */
    public static final String ARG_CATEGORY_SELECT = "catselect";

    /**
     * argument for maximal results. <BR/>
     */
    public static final String ARG_MAX_RESULTS = "maxres";

    /**
     * argument to show the query. <BR/>
     */
    public static final String ARG_SHOW_QUERY = "showq";

} // class QueryArguments

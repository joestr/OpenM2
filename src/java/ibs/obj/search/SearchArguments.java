/*
 * Class: SearchArguments.java
 */

// package:
package ibs.obj.search;

// imports:


/******************************************************************************
 * Arguments which are used in SearchGUI. <BR/>
 * This abstract class contains all Arguments which are necessary to deal with
 * the classes delivered within the searchinterface. <P>
 *
 * @version     $Id: SearchArguments.java,v 1.7 2009/07/24 08:26:43 kreimueller Exp $
 *
 * @author      Andreas Jansa (AJ), 001017
 ******************************************************************************
 */
public abstract class SearchArguments extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SearchArguments.java,v 1.7 2009/07/24 08:26:43 kreimueller Exp $";


    /**
     * oid of current object, when search was started
     */
    public static final String ARG_CURRENTOBJECTOID = "curoid";

    /**
     * oid query creator which contents the searchquery
     */
    public static final String ARG_QUERYCREATOROID = "qucroid";

    /**
     * name of query creator which contents the searchquery
     */
    public static final String ARG_QUERYCREATORNAME = "qucrname";

    /**
     * a boolean which is true if the searchForm should contain a selection list
     * for the types and queryObjects
     */
    public static final String ARG_SELECTIONLIST = "slct";

    /**
     * oid of object where search should be performed
     */
    public static final String ARG_ROOTOBJECTOID = "rootobjoid";

} // SearchArguments

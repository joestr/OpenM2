/*
 * Class: QueryExceptions.java
 */

// package:
package ibs.obj.query;

import ibs.util.UtilExceptions;

// imports:


/******************************************************************************
 * Exceptions for QueryObjects and Queryinterface. <BR/>
 * This abstract class contains all constants which are necessary to deal with
 * the exceptions delivered within this package. <P>
 *
 * @version     $Id: QueryExceptions.java,v 1.6 2010/04/07 13:37:09 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ), 010220
 ******************************************************************************
 */
public abstract class QueryExceptions extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final  String VERSIONINFO =
        "$Id: QueryExceptions.java,v 1.6 2010/04/07 13:37:09 rburgermann Exp $";

    /**
     * Name of bundle where the exceptions are included. <BR/>
     */
    public static String EXC_BUNDLE = UtilExceptions.EXC_BUNDLE;

    /**
     * query which was called via oid from java-queryinterface
     * does not exist in queryPool
     */
    public static final String ML_EXC_QUERYDOESNOTEXIST_OID  = "ML_EXC_QUERYDOESNOTEXIST_OID";

    /**
     * query which was called via name from java-queryinterface
     * does not exist in queryPool
     */
    public static final String ML_EXC_QUERYDOESNOTEXIST_NAME  = "ML_EXC_QUERYDOESNOTEXIST_NAME";

    /**
     * inputparameter which is tried to set in query does not exist in query
     * (names are different)
     */
    public static final String ML_EXC_QUERYPARAMDOESNOTEXIST  = "ML_EXC_QUERYPARAMDOESNOTEXIST";

} // QueryExceptions

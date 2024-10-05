/*
 * Class: StoredProcedureConstants.java
 */

// package:
package ibs.tech.sql;

// imports:


/******************************************************************************
 * This class defines the constants used in the StoredProcedure class. <BR/>
 *
 * @version     $Id: StoredProcedureConstants.java,v 1.4 2007/07/31 19:14:00 kreimueller Exp $
 *
 * @author      Mark Wassermann (MW)
 ******************************************************************************
 */
public class StoredProcedureConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: StoredProcedureConstants.java,v 1.4 2007/07/31 19:14:00 kreimueller Exp $";


    /**
     * StoredProcedure return type: undefined. <BR/>
     */
    public static final short RETURN_UNDEFINED = -1;

    /**
     * StoredProcedure return type: returns nothing. <BR/>
     */
    public static final short RETURN_NOTHING = 0;

    /**
     * StoredProcedure return type: returns value. <BR/>
     */
    public static final short RETURN_VALUE = 1;

    /**
     * StoredProcedure return type: returns resultset. <BR/>
     */
    public static final short RETURN_SET = 2;

} // class StoredProcedureConstants

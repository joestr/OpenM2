/*
 * Class: ParameterConstants.java
 */

// package:
package ibs.tech.sql;

// imports:


/******************************************************************************
 * This class defines the constants used in the PreparedStatement class. <BR/>
 *
 * @version     $Id: ParameterConstants.java,v 1.5 2007/07/31 19:13:59 kreimueller Exp $
 *
 * @author      Mark Wassermann (MW)
 ******************************************************************************
 */
public class ParameterConstants
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ParameterConstants.java,v 1.5 2007/07/31 19:13:59 kreimueller Exp $";


    /**
     * Parameter data type: undefined. <BR/>
     */
    public static final short TYPE_UNDEFINED = -1;

    /**
     * Parameter data type: boolean. <BR/>
     */
    public static final short TYPE_BOOLEAN = 0;

    /**
     * Parameter data type: byte. <BR/>
     */
    public static final short TYPE_BYTE = 1;

    /**
     * Parameter data type: short. <BR/>
     */
    public static final short TYPE_SHORT = 2;

    /**
     * Parameter data type: integer. <BR/>
     */
    public static final short TYPE_INTEGER = 3;

    /**
     * Parameter data type: float. <BR/>
     */
    public static final short TYPE_FLOAT = 4;

    /**
     * Parameter data type: double. <BR/>
     */
    public static final short TYPE_DOUBLE = 5;

    /**
     * Parameter data type: date. <BR/>
     */
    public static final short TYPE_DATE = 6;

    /**
     * Parameter data type: currency. <BR/>
     */
    public static final short TYPE_CURRENCY = 7;

    /**
     * Parameter data type: string. <BR/>
     */
    public static final short TYPE_STRING = 8;

    /**
     * Parameter data type: varchar. <BR/>
     */
    public static final short TYPE_VARCHAR = 9;

    /**
     * Parameter data type: object. <BR/>
     */
    public static final short TYPE_OBJECT = 10;

    /**
     * Parameter data type: varchar. <BR/>
     */
    public static final short TYPE_VARBYTE = 11;

    /**
     * Parameter data type: TEXT. <BR/>
     */
    public static final short TYPE_TEXT = 12;

    /**
     * Parameter direction type: undefined. <BR/>
     */
    public static final short DIRECTION_UNDEFINED = -1;

    /**
     * Parameter direction type: in. <BR/>
     * Stored procedure INPUT parameter.
     */
    public static final short DIRECTION_IN = 0;

    /**
     * Parameter direction type: out. <BR/>
     * Stored procedure OUTPUT parameter.
     */
    public static final short DIRECTION_OUT = 1;

    /**
     * Parameter direction type: inout. <BR/>
     * Stored procedure INPUT & OUTPUT parameter.
     * Not used in this version of ibs.tech.sql, because of
     * RDO and SQL Server restrictions
     */
    public static final short DIRECTION_INOUT = 2;

} // class ParameterConstants

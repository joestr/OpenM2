/*
 * Class: DocFunctions.java
 */

// package:
package m2.doc;

// imports:


/******************************************************************************
 * Functions for m2. <BR/>
 * This abstract class contains all Functions which are necessary to deal with
 * the classes delivered within this package.<P>
 * Contains application defined object types and derived types with IDs from
 * 0x0101 to 0xFFFF. <BR/>
 *
 * The functions classes for the different components have to be at the
 * following numbers: <BR/>
 * <UL>
 * <LI>documents:           2001 .. 2999    FCT_D...
 * <LI>diary:               3001 .. 3999    FCT_T...
 * <LI>master data:         4001 .. 4999    FCT_S...
 * <LI>catalog of products: 5001 .. 5999    FCT_W...
 * <LI>discussions:         6001 .. 6999    FCT_N...
 * <LI>phone book:          7001 .. 7999    FCT_PB...
 * </UL>
 *
 * @version     $Id: DocFunctions.java,v 1.11 2007/07/31 19:14:02 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 98049
 ******************************************************************************
 */
public abstract class DocFunctions extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DocFunctions.java,v 1.11 2007/07/31 19:14:02 kreimueller Exp $";


} // class DocFunctions

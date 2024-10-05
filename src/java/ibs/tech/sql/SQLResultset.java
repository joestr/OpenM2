/*
 * Class: SQLResultset.java
 */

// package:
package ibs.tech.sql;

// imports:
import ibs.BaseObject;
import ibs.tech.sql.DBActionException;


/******************************************************************************
 * The SQLResultset class encapsulates some of the rudimentary SQLResultset
 * initialization that is always performed. <BR/>
 * This is an abstract class. It must be overridden. <BR/>
 * The following methods have to be implemented in RDO/JDBC or whatever kind
 * of implementation: <BR/>
 * <UL>
 *      <LI><A HREF="#open">open (String sqlstring, boolean action)</A>
 *      <LI><A HREF="#isOpen">isOpen ()</A>
 *      <LI><A HREF="#end">end ()</A>
 *      <LI><A HREF="#getVariant">getVariant (String column)</A>
 *      <LI><A HREF="#close">close ()</A>
 *      <LI><A HREF="#next">next ()</A>
 * </UL>
 *
 * @version     $Id: SQLResultset.java,v 1.7 2007/07/31 19:14:00 kreimueller Exp $
 *
 * @author      Manfred Rieder 970817
 ******************************************************************************
 */
public abstract class SQLResultset extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SQLResultset.java,v 1.7 2007/07/31 19:14:00 kreimueller Exp $";


    /**
     * Object for COM thread safety. ??
     */
    protected Object vName;

    /**
     * Variant for COM thread safety.
     */
    protected Object vValue;


    /**************************************************************************
     * Creates a SQLResultset object. <BR/>
     * This method initializes the properties vName and vValue to newly created
     * Variants.
     */
    public SQLResultset ()
    {
        this.vName = new Object ();
        this.vValue = new Object ();
    } // SQLResultset


    /**************************************************************************
     * Opens a resultset and retrieves it covered as a simple object. <BR/>
     * THIS METHOD HAS TO BE OVERRIDDEN BY THE REAL IMPLEMENTATION
     * (e.g. RDOResultset). <BR/>
     *
     * @param   sqlString   This string represents the SQLQuery.
     * @param   action      This flag tells if an action query will be
     *                      performed.
     *
     * @return  The result at the current position.
     *
     * @throws  DBActionException
     *          An exception occurred during openng the connection.
     */
    public Object open (String sqlString, boolean action)
        throws DBActionException
    {
        return null;                    // return the resultset object
    } // open


    /**************************************************************************
     * Closes a given resultset (covered in an object). <BR/>
     * THIS METHOD HAS TO BE OVERRIDDEN BY THE REAL IMPLEMENTATION
     * (e.g. RDOResultset). <BR/>
     *
     * @param   myResultObj The resultset to be closed.
     *
     * @throws  DBActionException
     *          Error during closing the result set.
     */
    public void close (Object myResultObj) throws DBActionException
    {
        // this method shall be overwritten in sub classes.
    } // close


    /**************************************************************************
     * Gets a column value as variant out of the resultset. <BR/>
     * THIS METHOD HAS TO BE OVERRIDDEN BY THE REAL IMPLEMENTATION
     * (e.g. RDOResultset). <BR/>
     *
     * @param   myResultObj The active Resultset (covered in an object).
     * @param   column      The active column where values should be taken from.
     *
     * @return  The Variant containing the recordset entry.
     *
     * @throws  DBActionException
     *          An exception occurred when trying to get the object.
     */
    public Object getObject (Object myResultObj, String column)
        throws DBActionException
    {
        return null;                    // return the resulting value
    } // getObject

} // class SQLResultset

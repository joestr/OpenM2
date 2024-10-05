/**
 * Class: Transaction.java
 */

// package:
package ibs.io.session;

// imports:
import ibs.BaseObject;

import java.util.Date;


/******************************************************************************
 * This is the Transaction Object, which holds information about one transaction
 *
 * @version     $Id: Transaction.java,v 1.6 2007/07/24 21:29:09 kreimueller Exp $
 *
 * @author        Christine Keim  (CK)    980304
 ******************************************************************************
 */
public class Transaction extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Transaction.java,v 1.6 2007/07/24 21:29:09 kreimueller Exp $";


    /**
     * name of the transaction. <BR/>
     */
    public String name;
    /**
     * id of the transaction. <BR/>
     */
    public int id;
    /**
     * status of the transaction. <BR/>
     */
    public String status;
    /**
     * values. Array of Strings of the transaction. <BR/>
     */
    public String[] values;
    /**
     * opened. time when the transaction was opened. <BR/>
     */
    public Date opened;
    /**
     * closed time when the transaction was closed. <BR/>
     */
    public Date closed;
    /**
     * deleted. time when the transaction was deleted. <BR/>
     */
    public Date deleted;

    /**
     * Transaction status: opened. <BR/>
     */
    private static final String TRANS_STATUS_OPENED = "opened";


    /**************************************************************************
     * Create a new instance representing a Transaction. <BR/>
     * The properties name and id are instantiated with the given parameters.
     * The <A HREF="#status">status</A> property is set to "opened" and the
     * <A HREF="#opened">opened</A> property is set to the current date.
     * All the other properties are set to null.
     *
     * @param   varName name of the transaction
     * @param   varId   id of the transaction
     */
    public Transaction (String varName, int varId)
    {
        this.name = varName;
        this.id = varId;
        this.status = Transaction.TRANS_STATUS_OPENED;
        this.values = null;
        this.opened = new Date ();
        this.closed = null;
        this.deleted = null;
    } // constructor Transaction


    /**************************************************************************
     * Closes the transaction. <BR/>
     * The <A HREF="#status">status</A> property is set to "closed"
     * and the <A HREF="#closed">closed</A> property is set to the current date.
     */
    public void close ()
    {
        this.closed = new Date ();
        this.status = "closed";
    } // close


    /**************************************************************************
     * Deletes the transaction. <BR/>
     * The <A HREF="#status">status</A> property is set to "deleted"
     * and the <A HREF="#deleted">deleted</A> property is set to the current date.
     */
    public void delete ()
    {
        this.deleted = new Date ();
        this.status = "deleted";
    } // delete


    /**************************************************************************
     * Opens the transaction. <BR/>
     * The <A HREF="#status">status</A> property is set to "opened"
     * and the <A HREF="#opened">opened</A> property is set to the current date.
     */
    public void open ()
    {
        this.opened = new Date ();
        this.status = Transaction.TRANS_STATUS_OPENED;
    } // open

} // class Transaction

/*
 * Class: PaymentTypeContainer_01.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.Container;
import ibs.bo.OID;
import ibs.service.user.User;


/******************************************************************************
 * This class represents one container object of type PaymentTypeContainer. <BR/>
 *
 * @version     $Id: PaymentTypeContainer_01.java,v 1.6 2009/07/25 00:43:11 kreimueller Exp $
 *
 * @author      Daniel Janesch (DJ), 001121
 ******************************************************************************
 */
public class PaymentTypeContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: PaymentTypeContainer_01.java,v 1.6 2009/07/25 00:43:11 kreimueller Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // properties
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor creates a new instance of the class
     * PaymentTypeContainer. <BR/>
     */
    public PaymentTypeContainer_01 ()
    {
        // call constructor of super class Container:
        super ();
    } // PaymentTypeContainer_01


    /**************************************************************************
     * This constructor creates a new instance of the class
     * PaymentTypeContainer. <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public PaymentTypeContainer_01 (OID oid, User user)
    {
        // call constructor of super class Container:
        super (oid, user);
    } // PaymentTypeContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // nothing to do
    } // initClassSpecifics


    ///////////////////////////////////////////////////////////////////////////
    // viewing functions
    ///////////////////////////////////////////////////////////////////////////

} // class PaymentTypeContainer_01

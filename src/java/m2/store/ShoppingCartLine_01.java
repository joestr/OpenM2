/*
 * Class: ShoppingCartLine_01.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.service.user.User;


/******************************************************************************
 * This class represents one BusinessObject of type ShoppingCartLine with version 01.
 * It's only possible to delete this kind of object, therefore this class seems
 * to be quite empty. An entry in the shopping cart is made in Product_02
 * and it doesn't have any visual representation. <BR/>
 *
 * @version     $Id: ShoppingCartLine_01.java,v 1.7 2009/07/25 00:43:15 kreimueller Exp $
 *
 * @author      Bernhard Walter (BW), 980924
 ******************************************************************************
 */
public class ShoppingCartLine_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ShoppingCartLine_01.java,v 1.7 2009/07/25 00:43:15 kreimueller Exp $";


    /**************************************************************************
     * ???
     */
    public ShoppingCartLine_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // ShoppingCartLine_01


    /**************************************************************************
     * ???
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ShoppingCartLine_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // ShoppingCartLine_01

    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // initialize the instance's private properties:
        this.procDelete = "p_VoucherLine_02$delete";
    } // initClassSpecifics

} // class ShoppingCartLine_01

/*
 * Class: PropertyCategory_01.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.service.user.User;


/******************************************************************************
 * This class represents one BusinessObject of type Properties with version 01.
 * <BR/>
 *
 * @version     $Id: PropertyCategory_01.java,v 1.10 2009/07/25 00:43:15 kreimueller Exp $
 *
 * @author      Bernhard Walter (BW), 980527
 ******************************************************************************
 */
public class PropertyCategory_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: PropertyCategory_01.java,v 1.10 2009/07/25 00:43:15 kreimueller Exp $";


    /**************************************************************************
     * Creates a PropertyCategory_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     */
    public PropertyCategory_01 ()
    {
        // call constructor of super class:
        super ();
    } // PropertyCategory_01


    /**************************************************************************
     * This constructor creates a new instance of the class PropertyCategory_01.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public PropertyCategory_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);

    } // PropertyCategory_01


    /**************************************************************************
     * Set the icon of the actual business object. <BR/>
     * If the icon is already set this method leaves it as is.
     * If there is no icon defined yet, the icon name is derived from the name
     * of the type of this object. <BR/>
     */
    protected void setIcon ()
    {
        this.icon = "PropertyCategory.gif";
    } // setIcon

} // class PropertyCategory_01

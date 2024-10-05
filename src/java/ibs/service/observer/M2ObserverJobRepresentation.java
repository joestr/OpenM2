/*
 * Class: M2ObserverJobRepresentation.java
 */

// package:
package ibs.service.observer;

// imports:
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.service.observer.M2ObserverJob;
import ibs.service.user.User;


/******************************************************************************
 * This class.... <BR/>
 *
 * @version     $Id: M2ObserverJobRepresentation.java,v 1.1 2007/07/24 21:28:12 kreimueller Exp $
 *
 * @author      hpichler
 ******************************************************************************
 */
public class M2ObserverJobRepresentation extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: M2ObserverJobRepresentation.java,v 1.1 2007/07/24 21:28:12 kreimueller Exp $";


    /**
     * Object to represent. <BR/>
     */
    protected M2ObserverJob job = null;


    /**************************************************************************
     * This constructor creates a new instance of the class
     * m2ObserverJobRepresentation.
     */
    public M2ObserverJobRepresentation ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // m2ObserverJobRepresentation


    /**************************************************************************
     * Creates a m2ObserverJobRepresentation object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     */
    public M2ObserverJobRepresentation (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
        // initialize properties common to all subclasses:
    } // m2ObserverJobRepresentation


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // this objecttype is never persistent
        this.isPhysical = false;
        this.displayButtons = false;
        this.setIcon ();
    } // initClassSpecifics

} // M2ObserverJobRepresentation

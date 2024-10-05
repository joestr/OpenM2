/*
 * Class: TerminplanContainer_01.java
 */

// package:
package m2.diary;

// imports:
import ibs.bo.Container;
import ibs.bo.OID;
import ibs.service.user.User;


/******************************************************************************
 * This class represents one object of type TerminplanContainer with version 01.
 * <BR/>
 *
 * @version     $Id: TerminplanContainer_01.java,v 1.7 2009/07/25 00:35:41 kreimueller Exp $
 *
 * @author      Horst Pichler   (HP), 980428
 ******************************************************************************
 */
public class TerminplanContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TerminplanContainer_01.java,v 1.7 2009/07/25 00:35:41 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class
     * TerminplanContainer_01. <BR/>
     */
    public TerminplanContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // TerminplanContainer_01


    /**************************************************************************
     * Creates a TerminplanContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public TerminplanContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // TerminplanContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the instance's attributes:
    } // initClassSpecifics

} // class TerminplanContainer_01

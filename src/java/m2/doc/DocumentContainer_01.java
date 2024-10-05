/*
 * Class: DocumentContainer_01.java
 */

// package:
package m2.doc;

// imports:
import ibs.bo.Container;
import ibs.bo.OID;
import ibs.service.user.User;


/******************************************************************************
 * This class represents one object of type DocumentContainer with version 01.
 * <BR/>
 *
 * @version     $Id: DocumentContainer_01.java,v 1.8 2009/07/25 00:36:13 kreimueller Exp $
 *
 * @author      Bernd Buchegger (BB), 980720
 ******************************************************************************
 */
public class DocumentContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DocumentContainer_01.java,v 1.8 2009/07/25 00:36:13 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class
     * DocumentContainer_01. <BR/>
     */
    public DocumentContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // DocumentContainer_01


    /**************************************************************************
     * Creates a DocumentContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public DocumentContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // DocumentContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the instance's attributes:
    } // initClassSpecifics


    /**************************************************************************
     * Is the object type allowed in workflows? <BR/>
     * This method shall be overwritten in subclasses.
     *
     * @return  <CODE>true</CODE> if the object type is allowed in workflows,
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean isWfAllowed ()
    {
        return true;
    } // isWfAllowed

} // class DocumentContainer_01

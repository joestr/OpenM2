/*
 * Class: WorkspaceTemplate_01.java
 */

// package:
package ibs.obj.user;

// imports:
import ibs.bo.OID;
import ibs.obj.doc.File_01;
import ibs.service.user.User;


/******************************************************************************
 * Thix class ic contanier for an uploadet File which is an m2 - Import -
 * XML-Structur. <BR/>
 * This Structure is imported in the Workspace of each user when the user
 * is created. <BR/>
 *
 * @version     $Id: WorkspaceTemplate_01.java,v 1.6 2009/07/24 13:19:33 kreimueller Exp $
 *
 * @author      Andreas Jansa (AJ) 020612
 ******************************************************************************
 */
public class WorkspaceTemplate_01 extends File_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WorkspaceTemplate_01.java,v 1.6 2009/07/24 13:19:33 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class WorkspaceTemplate_01.
     * <BR/>
     */
    public WorkspaceTemplate_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // WorkspaceTemplate_01


    /**************************************************************************
     * Creates a WorkspaceTemplate_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public WorkspaceTemplate_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // WorkspaceTemplate_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        super.initClassSpecifics ();    // has same specifics as super class
    } // initClassSpecifics

} // class WorkspaceTemplate_01

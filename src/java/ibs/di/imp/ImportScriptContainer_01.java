/*
 * Class: ImportScriptContainer_01.java
 */

// package:
package ibs.di.imp;

// imports:
//KR TODO: unsauber
import ibs.bo.Container;
//KR TODO: unsauber
import ibs.bo.OID;
//KR TODO: unsauber
import ibs.service.user.User;


/******************************************************************************
 * This class represents one object of type ImportScriptContainer with
 * version 01. <BR/>
 *
 * @version     $Id: ImportScriptContainer_01.java,v 1.8 2009/07/25 09:11:21 kreimueller Exp $
 *
 * @author      Harald Buzzi (HB), 991202
 ******************************************************************************
 */
public class ImportScriptContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ImportScriptContainer_01.java,v 1.8 2009/07/25 09:11:21 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class
     * ImportScriptContainer_01.
     * <BR/>
     */
    public ImportScriptContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // ImportScriptContainer_01


    /**************************************************************************
     * Creates a ImportScriptContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ImportScriptContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // ImportScriptContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set majorContainer true:
        this.isMajorContainer = true;
    } // initClassSpecifics

} // ImportScriptContainer_01

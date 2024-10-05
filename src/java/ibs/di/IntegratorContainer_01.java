/*
 * Class: IntegratorContainer_01.java
 */

// package:
package ibs.di;

// imports:
//KR TODO: unsauber
import ibs.bo.Container;
//KR TODO: unsauber
import ibs.bo.OID;
//KR TODO: unsauber
import ibs.service.user.User;


/******************************************************************************
 * This class represents one object of type IntegratorContainer with version 01.
 * <BR/>
 *
 * @version     $Id: IntegratorContainer_01.java,v 1.12 2009/07/24 23:49:01 kreimueller Exp $
 *
 * @author      Bernd Buchegger (BB), 990104
 ******************************************************************************
 */
public class IntegratorContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IntegratorContainer_01.java,v 1.12 2009/07/24 23:49:01 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class IntegratorContainer.
     * <BR/>
     */
    public IntegratorContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // IntegratorContainer_01


    /**************************************************************************
     * This constructor creates a new instance of the class IntegratorContainer.
     * <BR/>
     * The compound object id is used as base for getting the
     * <A HREF="#server">server</A>, <A HREF="#type">type</A>, and
     * <A HREF="#id">id</A> of the business object. These values are stored in the
     * special public attributes of this type. <BR/>
     * The <A HREF="#user">user object</A> is also stored in a specific attribute
     * of this object to make sure that the user's context can be used for getting
     * his/her rights.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public IntegratorContainer_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // IntegratorContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the instance's attributes:
    } // initClassSpecifics

} // class IntegratorContainer_01

/*
 * PublicationContainer_01.java
 */

// package:
package m2.version.publish;

// imports:
import ibs.bo.Buttons;
import ibs.bo.OID;
import ibs.service.user.User;

import m2.version.VersionContainer_01;
import m2.version.Version_01;
import m2.version.publish.Publication_01;


/******************************************************************************
 * This class realizes the implementation of a version container which is
 * allowed to contain only version objects.
 *
 * @version     $Id: PublicationContainer_01.java,v 1.7 2009/07/25 09:32:10 kreimueller Exp $
 *
 * @author      Bernd Martin (BM), 011115
 ******************************************************************************
 */
public class PublicationContainer_01 extends VersionContainer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: PublicationContainer_01.java,v 1.7 2009/07/25 09:32:10 kreimueller Exp $";


    /**
     * The classname of the elements which are stored in the container. <BR/>
     */
    private static final String ELEMENTCLASSNAME =
        "m2.version.publish.PublicationContainerElement_01";


    /**************************************************************************
     * This constructor creates a new instance of the class XMLContainer. <BR/>
     */
    public PublicationContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // PVersionContainer_01


    /**************************************************************************
     * This constructor creates a new instance of the class VersionContainer. <BR/>
     * The compound object id is used as base for getting the
     * <A HREF="#server">server</A>, <A HREF="#type">type</A>, and
     * <A HREF="#id">id</A> of the business object. These values are stored in the
     * special public attributes of this type. <BR/>
     * The <A HREF="#user">user object</A> is also stored in a specific attribute
     * of this object to make sure that the user's context can be used for getting
     * his/her rights.
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public PublicationContainer_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // PVersionContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        super.initClassSpecifics ();

        // set name of specific container element
        this.elementClassName = this.getElementClassname ();
    } // initClassSpecifics


    /**************************************************************************
     * This method creates a new pversion object and initializes it via the
     * method from the basis. <BR/>
     *
     * @param   masterOid   The oid of the master object.
     *
     * @return  The new version object.
     */
    protected Version_01 getNewAndInitVersion (OID masterOid)
    {
        Publication_01 v = new Publication_01 ();
        v.initObject (masterOid, this.user, this.env, this.sess, this.app);
        return v;
    } // getAndInitNewVersion


    /**************************************************************************
     * Return the classname for the objects in the container. <BR/>
     *
     * @return  The classname of the objects in the container.
     */
    public String getElementClassname ()
    {
        return PublicationContainer_01.ELEMENTCLASSNAME;
    } // getElementClassname


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * container's content view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setContentButtons ()
    {
        // init buttons
        int[] buttons =
        {
            Buttons.BTN_LISTDELETE,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons

} // class PublicationContainer_01

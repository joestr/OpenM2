/*
 * Class: UserAdminContainer_01.java
 */

// package:
package ibs.obj.user;

// imports:
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.OID;
import ibs.service.user.User;


/******************************************************************************
 * This class represents one object of type UserAdminContainer with
 * version 01. <BR/>
 *
 * @version     $Id: UserAdminContainer_01.java,v 1.9 2009/07/24 13:19:32 kreimueller Exp $
 *
 * @author      Centner Martin (CM), 980701
 ******************************************************************************
 */
public class UserAdminContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: UserAdminContainer_01.java,v 1.9 2009/07/24 13:19:32 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class. <BR/>
     */
    public UserAdminContainer_01 ()
    {
        // call constructor of super class:
        super ();
    } // UserAdminContainer_01


    /**************************************************************************
     * This constructor creates a new instance of the class
     * UserAdminContainer_01. <BR/>
     * The compound object id is used as base for getting the
     * <A HREF="#server">server</A>, <A HREF="#type">type</A>, and
     * <A HREF="#id">id</A> of the business object. These values are stored in
     * the special public attributes of this type. <BR/>
     * The <A HREF="#user">user object</A> is also stored in a specific
     * attribute of this object to make sure that the user's context can be
     * used for getting his/her rights.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public UserAdminContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // UserAdminContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the class-procedureNames
        this.procCreate = "p_UserAdminContainer_01$create";

        // set view to use for retrieveContent:
        // set headings:
        // set ordering attributes for the corresponding headings:
        // set the instance's attributes:
    } // initClassSpecifics


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * containers content view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setContentButtons ()
    {
        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_SEARCH,
//           ibs.app.Buttons.BTN_HELP,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * object info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can be displayed.
     */
    protected int[] setInfoButtons ()
    {
        // define buttons to be displayed:
        int [] buttons =
        {
            Buttons.BTN_SEARCH,
//           ibs.app.Buttons.BTN_HELP,
        }; // buttons

        // return button array
        return buttons;
    } // showInfoButtons

} // class UserAdminContainer_01

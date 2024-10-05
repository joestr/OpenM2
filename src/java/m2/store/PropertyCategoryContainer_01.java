/*
 * Class: PropertyCategoryContainer_01.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.OID;
import ibs.service.user.User;


/******************************************************************************
 * This class represents one container object of type Store with version 01.
 * <BR/>
 *
 * @version     $Id: PropertyCategoryContainer_01.java,v 1.10 2009/07/25 00:43:15 kreimueller Exp $
 *
 * @author      Bernhard Walter (BW), 981018
 ******************************************************************************
 */
public class PropertyCategoryContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: PropertyCategoryContainer_01.java,v 1.10 2009/07/25 00:43:15 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class
     * PropertyCategoryContainer_01. <BR/>
     */
    public PropertyCategoryContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // PropertyCategoryContainer_01


    /**************************************************************************
     * This constructor creates a new instance of the class Store.
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
    public PropertyCategoryContainer_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // PropertyCategoryContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set type-specific attributes:
        this.setIcon ();
    } // initClassSpecifics


    /**************************************************************************
     * Set the icon of the actual business object. <BR/>
     * If the icon is already set this method leaves it as is.
     * If there is no icon defined yet, the icon name is derived from the name
     * of the type of this object. <BR/>
     */
    protected void setIcon ()
    {
        this.icon = "PropertyCategoryContainer.gif";
    } // setIcon


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in an
     * object info view. <BR/>
     *
     * @return  An array with button ids that can be displayed.
     */
    protected int[] setInfoButtons ()
    {
        // define buttons to be displayed:
        int [] buttons =
        {
            Buttons.BTN_EDIT,
            Buttons.BTN_DELETE,
        }; // buttons

        // return button array
        return buttons;
    } // showInfoButtons


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * container's content view. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setContentButtons ()
    {
        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_NEW,
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_LIST_COPY,
            Buttons.BTN_LIST_CUT,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons

} // class PropertyCategoryContainer_01

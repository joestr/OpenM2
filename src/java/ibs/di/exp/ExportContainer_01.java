/*
 * Class: ExportContainer_01.java
 */

// package:
package ibs.di.exp;

// imports:
//KR TODO: unsauber
import ibs.bo.Buttons;
//KR TODO: unsauber
import ibs.bo.Container;
//KR TODO: unsauber
import ibs.bo.OID;
//KR TODO: unsauber
import ibs.service.user.User;


/******************************************************************************
 * This class represents one object of type ExportContainer with version 01.
 * <BR/>
 *
 * @version     $Id: ExportContainer_01.java,v 1.14 2009/07/24 23:25:18 kreimueller Exp $
 *
 * @author      Bernd Buchegger (BB), 990104
 ******************************************************************************
 */
public class ExportContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ExportContainer_01.java,v 1.14 2009/07/24 23:25:18 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class ExportContainer. <BR/>
     */
    public ExportContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // ExportContainer_01


    /**************************************************************************
     * This constructor creates a new instance of the class ExportContainer. <BR/>
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
    public ExportContainer_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // ExportContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the instance's attributes:
    } // initClassSpecifics


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
        // define buttons to be displayed:
        int[] buttons =
        {
//            Buttons.BTN_NEW,
//            Buttons.BTN_PASTE,
            Buttons.BTN_SEARCH,
            Buttons.BTN_EXPORT,
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_REFERENCE,
            Buttons.BTN_LIST_COPY,
            Buttons.BTN_LIST_CUT,
            Buttons.BTN_DISTRIBUTE,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons sepp


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in an
     * object's info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setInfoButtons ()
    {
        // define buttons to be displayed:
        int [] buttons =
        {
            Buttons.BTN_EDIT,
            Buttons.BTN_DELETE,
            Buttons.BTN_CUT,
            Buttons.BTN_COPY,
            Buttons.BTN_DISTRIBUTE,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons

} // class ExportContainer_01

/*
 * Class: ImportContainer_01.java
 */

// package:
package ibs.di.imp;

// imports:
//KR TODO: unsauber
import ibs.bo.Buttons;
//KR TODO: unsauber
import ibs.bo.Container;
//KR TODO: unsauber
import ibs.bo.OID;
//KR TODO: unsauber
import ibs.service.user.User;
import ibs.tech.html.TableElement;


/******************************************************************************
 * This class represents one object of type ImportContainer with version 01.
 * <BR/>
 *
 * @version     $Id: ImportContainer_01.java,v 1.13 2009/07/25 09:11:21 kreimueller Exp $
 *
 * @author      Bernd Buchegger (BB), 990104
 ******************************************************************************
 */
public class ImportContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ImportContainer_01.java,v 1.13 2009/07/25 09:11:21 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class ImportContainer. <BR/>
     */
    public ImportContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // ImportContainer_01


    /**************************************************************************
     * This constructor creates a new instance of the class ImportContainer. <BR/>
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
    public ImportContainer_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // ImportContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set creatable objects for container ImportContainer_01:
/*
        String[] typeIds =
               {Integer.toString (createTVersionId(ibs.bo.type.TypeConstants.TYPE_XMLViewer_01))};
        String[] typeNames = {  ibs.bo.type.TypeConstants.TN_XMLViewer_01};
        this.typeIds = typeIds;
        this.typeNames = typeNames;
*/
        // set the instance's attributes:
    } // initClassSpecifics


    /**************************************************************************
     * Represent the properties of a ImportContainer_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showProperties (table);
    } // showProperties


    /**************************************************************************
     * Represent the properties of a ImportContainer_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showFormProperties (table);
    } // showFormProperties


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
            Buttons.BTN_IMPORT,
            Buttons.BTN_PASTE,
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_LIST_COPY,
            Buttons.BTN_LIST_CUT,
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

} // class ImportContainer_01

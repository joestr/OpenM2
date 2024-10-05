/*
 * Class: Root_01.java
 */

// package:
package ibs.obj.dom;

// imports:
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.OID;
import ibs.service.user.User;
import ibs.tech.html.TableElement;


/******************************************************************************
 * This class represents one object of type Type with version 01. <BR/>
 *
 * @version     $Id: Root_01.java,v 1.12 2009/07/24 20:57:11 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 980727
 ******************************************************************************
 */
public class Root_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Root_01.java,v 1.12 2009/07/24 20:57:11 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class. <BR/>
     */
    public Root_01 ()
    {
        // call constructor of super class:
        super ();
    } // Root_01


    /**************************************************************************
     * This constructor creates a new instance of the class Root_01. <BR/>
     * The compound object id is used as base for getting the
     * <A HREF="#server">server</A>, <A HREF="#type">type</A>, and
     * <A HREF="#id">id</A> of the business object. These values are stored in
     * the special public attributes of this type. <BR/>
     * The <A HREF="#user">user object</A> is also stored in a specific
     * attribute of this object to make sure that the user's context can be
     * used for getting his/her rights.
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Root_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // Root_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the class-procedureNames
        // set view to use for retrieveContent:
        // set headings:
        // set ordering attributes for the corresponding headings:
        // set the instance's attributes:
        // set the class wich contains the data of the elements to be shown:
    } // initClassSpecifics



    ///////////////////////////////////////////////////////////////////////////
    // database functions
    ///////////////////////////////////////////////////////////////////////////



    ///////////////////////////////////////////////////////////////////////////
    // viewing functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Represent the properties of a Root_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        // display the base object's properties:
        super.showProperties (table);

        // loop through all properties of this object and display them:
/*
        this.showProperty (table, ARG_IDPROPERTY, TOK_IDPROPERTY, DT_INTEGER, idProperty);
*/
    } // showProperties


    /**************************************************************************
     * Represent the properties of a Root_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        // display the base object's properties:
        super.showFormProperties (table);

        // loop through all properties of this object and display them:
/*
        this.showFormProperty (table, ARG_IDPROPERTY, TOK_IDPROPERTY, DT_INTEGER, idProperty);
*/
    } // showFormProperties


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
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons

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
            Buttons.BTN_NEW,
            Buttons.BTN_SEARCH,
            Buttons.BTN_LISTDELETE,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons

} // class Root_01

/*
 * Class: MenuTabContainer_01.java
 */

// package:
package ibs.obj.menu;

// imports:
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.OID;
import ibs.service.user.User;


/******************************************************************************
 * This class represents one object of type MenuTabContainer with version 01.
 * <BR/>
 *
 * @version     1.10.0001
 *
 * @author      Monika Eisenkolb  (ME), 270901
 ******************************************************************************
 */
public class MenuTabContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MenuTabContainer_01.java,v 1.7 2009/07/24 10:19:48 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class MenuTabContainer_01.
     * <BR/>
     */
    public MenuTabContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // MenuTabContainer_01

    /**************************************************************************
     * This constructor creates a new instance of the class MenuTabContainer_01.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public MenuTabContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // MenuTabContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the instance's attributes:.
        this.elementClassName = "ibs.obj.menu.MenuTabContainerElement_01";
        //this.viewContent = "v_MenuTabContainer_01$content";
    } // initClassSpecifics



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
            // Buttons.BTN_PASTE,
            Buttons.BTN_SEARCH,
            Buttons.BTN_LISTDELETE,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons


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
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons

} // MenuTabContainer_01

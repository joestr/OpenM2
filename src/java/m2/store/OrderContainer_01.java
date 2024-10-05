/*
 * Class: OrderContainer_01.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.States;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.util.StringHelpers;


/******************************************************************************
 * This class represents one container object of type Store with version 01.
 * <BR/>
 *
 * @version     $Id: OrderContainer_01.java,v 1.12 2010/04/13 15:55:57 rburgermann Exp $
 *
 * @author      Rupert Thurner (RT), 980528
 ******************************************************************************
 */
public class OrderContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: OrderContainer_01.java,v 1.12 2010/04/13 15:55:57 rburgermann Exp $";

    /**
     * Headings of columns. <BR/>
     * These headings are shown at the top of lists.
     */
    public static final String [] LST_HEADINGS_ORDERCONTAINER =
    {
        BOTokens.ML_NAME,
        BOTokens.ML_STATE,
        BOTokens.ML_CHANGED,
    }; // LST_HEADINGS_ORDERCONTAINER

    /**
     * Name of a container column. <BR/>
     * These attributes are used for ordering the elements.
     */
    public static final String [] LST_ORDERINGS_ORDERCONTAINER =
    {
        "name",
        "state",
        "lastChanged",
    }; // LST_ORDERINGS_ORDERCONTAINER

    /**************************************************************************
     * This constructor creates a new instance of the class OrderContainer_01.
     * <BR/>
     */
    public OrderContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // OrderContainer_01


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
    public OrderContainer_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
        // initialize properties common to all subclasses:
    } // OrderContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the instance's attributes:
        this.elementClassName = "m2.store.OrderContainerElement_01";
    } // initClassSpecifics


    /**************************************************************************
     * Set the headings for this container. This method MUST be overloaded if. <BR/>
     * you have your own subclass of ContainerElement and if you need other headings. <BR/>
     * You have to overload the method setOrderings () as well.
     */
    protected void setHeadingsAndOrderings ()
    {
        // set headings for this container
        this.headings = MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE, 
            OrderContainer_01.LST_HEADINGS_ORDERCONTAINER, env);
        // set orderings for this container
        this.orderings = OrderContainer_01.LST_ORDERINGS_ORDERCONTAINER;

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This metod is used to get all attributes of one element out of the
     * resultset. The attribute names which can be used are the ones which
     * are defined within the resultset of
     * {@link #createQueryRetrieveContentData createQueryRetrieveContentData}.
     * <BR/>
     * <B>Format:</B>. <BR/>
     * for oid properties:
     *      obj.&lt;property> = getQuOidValue (action, "&lt;attribute>"); <BR/>
     * for other properties:
     *      obj.&lt;property> = action.get&lt;type> ("&lt;attribute>"); <BR/>
     * The property <B>oid</B> is already gotten. This must not be done within
     * this method. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     *
     * @param   action  The action for the database connection.
     * @param   obj     Object representing the list element.
     *
     * @throws  DBError
     *          Error when executing database statement.
     */
    protected void getContainerElementData (SQLAction action,
                                            ContainerElement obj)
        throws DBError
    {
        // call method of super class:
        super.getContainerElementData (action, obj);

        // now only for OrderContainer_01 but later for all container types:
        // get processStateId
        int processStateId = States.PST_NONE;
        processStateId = action.getInt ("processState");

        // find processStateId in Array ST_STATEIDS and get the right token out of
        // the ST_STATENAMES Array
        int index = StringHelpers.findString (States.PST_STATEIDS, Integer.toString (processStateId));
        if (index == -1)
        {
            index = StringHelpers.findString (States.PST_STATEIDS, Integer.toString (States.PST_NONE));
        } // if
        obj.processState = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, States.PST_STATENAMES [index], env);
    } // getContainerElementData


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
//            Buttons.BTN_EDIT,
//            Buttons.BTN_DELETE,
//            Buttons.BTN_CUT,
//            Buttons.BTN_COPY,
//            Buttons.BTN_DISTRIBUTE,
//            Buttons.BTN_CLEAN,
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
//            Buttons.BTN_LOGIN
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
//            Buttons.BTN_NEW,
//            Buttons.BTN_PASTE,
//            Buttons.BTN_CLEAN,
            Buttons.BTN_SEARCH,


//            Buttons.BTN_HELP,
//            Buttons.BTN_LOGIN
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons

} // class OrderContainer_01

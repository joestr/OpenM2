/*
 * Class: StateContainer_01.java
 */

// package:
package ibs.bo;

// imports:
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.StateContainerElement_01;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;


/******************************************************************************
 * This class represents one object of type StateContainer with version 01.
 * <BR/>
 *
 * @version     $Id: StateContainer_01.java,v 1.12 2010/04/13 15:55:57 rburgermann Exp $
 *
 * @author      Thomas Joham   (TJ), 010323
 ******************************************************************************
 */
public class StateContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: StateContainer_01.java,v 1.12 2010/04/13 15:55:57 rburgermann Exp $";

    /**************************************************************************
     * This constructor creates a new instance of the class StateContainer_01.
     * <BR/>
     */
    public StateContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // StateContainer_01


    /**************************************************************************
     * This constructor creates a new instance of the class StateContainer_01.
     * <BR/>
     *
     * @param   oid     ???
     * @param   user    ???
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public StateContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // ParticipantContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the instance's attributes:.
        this.elementClassName = "ibs.bo.StateContainerElement_01";
        this.viewContent = "v_StateContainer_01$content";
    } // initClassSpecifics


    /**************************************************************************
     * Create the query to get the container's content out of the database. <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        // the query for the content of the container
        String query =
            "SELECT DISTINCT oid, state, name, typeName, " +
            "        isLink, linkedObjectId, userId, owner, ownerName, " +
            "        ownerOid, ownerFullname, lastChanged, isNew, icon, " +
            "        description, workflowState, workflowOid, stateChangeDate" +
            " FROM " + this.viewContent +
            " WHERE   containerId = " + this.oid.toStringQu ();
        return query;
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     *
     * @param   action      The action for the database connection.
     * @param   commonObj   Object representing the list element.
     *
     * @exception   DBError
     *              Error when executing database statement.
     */
    protected void getContainerElementData (SQLAction action,
                                            ContainerElement commonObj)
        throws DBError
    {
        // convert common element object to actual type:
        StateContainerElement_01 obj = (StateContainerElement_01) commonObj;
        // get common attributes:
        super.getContainerElementData (action, obj);

        // get element type specific attributes:
        obj.workflowState = action.getString ("workflowState");
        obj.workflowOid = action.getString ("workflowOid");
        obj.stateChangeDate = action.getDate ("stateChangeDate");
    } // getContainerElementData


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard
     * container. <BR/>
     */
    protected void setHeadingsAndOrderings ()
    {
        // do not show extended attributes
        if (!this.getUserInfo ().userProfile.showExtendedAttributes)
        {
            // set super attribute
            this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOListConstants.LST_HEADINGS_STATECONTAINER_REDUCED, env);

            // set super attribute
            this.orderings = BOListConstants.LST_ORDERINGS_STATECONTAINER_REDUCED;
        } // if showExtendedAttributes
        else
        // show extended attributes
        {
            // set super attribute
            this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOListConstants.LST_HEADINGS_STATECONTAINER, env);

            // set super attribute
            this.orderings = BOListConstants.LST_ORDERINGS_STATECONTAINER;
        } // else

        // ensure that there is an available ordering token:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings


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
            Buttons.BTN_SEARCH,
        }; // buttons
        // return button array
        return buttons;
    } // setContentButtons

} // class StateContainer_01


/*
 * Class: WorkflowTemplateContainer_01.java
 */

// package:
package ibs.obj.workflow;

// imports:
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.OID;
import ibs.service.user.User;


/******************************************************************************
 * This class represents one object of type WorkflowTemplateContainer with
 * version 01. <BR/>
 *
 * @version     $Id: WorkflowTemplateContainer_01.java,v 1.10 2009/07/24 18:22:08 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public class WorkflowTemplateContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WorkflowTemplateContainer_01.java,v 1.10 2009/07/24 18:22:08 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class
     * WorkflowTemplateContainer. <BR/>
     */
    public WorkflowTemplateContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // WorkflowTemplateContainer_01


    /**************************************************************************
     * This constructor creates a new instance of the class
     * WorkflowTemplateContainer. <BR/>
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
    public WorkflowTemplateContainer_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // WorkflowTemplateContainer_01


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
            Buttons.BTN_NEW,
            Buttons.BTN_PASTE,
            Buttons.BTN_REFERENCE,
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
            Buttons.BTN_NEW,
            Buttons.BTN_EDIT,
/**
 * We do not allow to delete a workflow template container
 * because the workflow templates in the container must be
 * deleted object by object to ensure that templates in use
 * are not deleted.
 */
//            Buttons.BTN_DELETE,
            Buttons.BTN_CUT,
            Buttons.BTN_COPY,
            Buttons.BTN_DISTRIBUTE,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons


    /**************************************************************************
     * Extend the query with constraints specific for delete operations. <BR/>
     * This method just adds some constraints to the already existing query.
     * It must be empty or start with "AND...".
     * This method can be overwritten in subclasses. <BR/>
     *
     * @return  The extension to the query.
     *
     * @see ibs.bo.Container#createQueryRetrieveContentData
     */
    protected StringBuffer extendQueryRetrieveDeleteData ()
    {
        return super.extendQueryRetrieveDeleteData ()
            // get the oid of all referenced document templates
            .append (" AND oid NOT IN (SELECT definitionId FROM v_WorkflowTemplate_01$ref)");
    } // extendQueryRetrieveDeleteData

} // class WorkflowTemplateContainer_01

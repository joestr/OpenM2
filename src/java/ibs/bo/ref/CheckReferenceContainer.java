/*
 * Class: CheckReferenceContainer.java
 */

// package:
package ibs.bo.ref;

// imports:
//KR TODO: unsauber
import ibs.app.AppFunctions;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOMessages;
import ibs.bo.BOTokens;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.States;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.FormElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.Page;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.util.NoAccessException;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This class is responsible for checking of references and handling of them.
 * <BR/>
 *
 * @version     $Id: CheckReferenceContainer.java,v 1.17 2010/04/13 15:55:58 rburgermann Exp $
 *
 * @author      kreimueller, 011218
 ******************************************************************************
 */
public class CheckReferenceContainer extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: CheckReferenceContainer.java,v 1.17 2010/04/13 15:55:58 rburgermann Exp $";

    /**
     * Headings of columns. <BR/>
     * These headings are shown at the top of lists.
     */
    public static final String[] LST_HEADINGS =
    {
        BOTokens.ML_NAME,
        BOTokens.ML_REFCOUNT,
    }; // LST_HEADINGS

    /**
     * Initial step within function sequence. <BR/>
     * This constant is used to identify the current step within a sequence of
     * checkReference dialogs.
     */
    private static final int STEP_INIT = 1;

    /**
     * Finishing step within function sequence. <BR/>
     * This constant is used to identify the current step within a sequence of
     * checkReference dialogs.
     */
    private static final int STEP_FINISH = 2;


    /**
     * The actual step within a sequence of checkReference steps. <BR/>
     * This property is filled at the beginning of each call to represent the
     * current step of the operation. <BR/>
     * Default value: {@link #STEP_INIT STEP_INIT}
     */
    private int p_refStep = CheckReferenceContainer.STEP_INIT;


    /**************************************************************************
     * This constructor creates a new instance of the class
     * CheckReferenceContainer. <BR/>
     */
    public CheckReferenceContainer ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // CheckReferenceContainer


    /**************************************************************************
     * Creates a CheckReferenceContainer object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public CheckReferenceContainer (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // CheckReferenceContainer


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.elementClassName = "ibs.bo.ref.CheckReferenceContainerElement";
        this.ownOrdering = true;
    } // initClassSpecifics


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <CODE>env</CODE> property is used for getting
     * the parameters. This property must be set before calling this method.
     * <BR/>
     */
    public void getParameters ()
    {
        int num = 0;

        // get parameters which are relevant for the super class:
        super.getParameters ();

        // get the step:
        if ((num = this.env.getIntParam (BOArguments.ARG_REFSTEP)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
                                        // step defined?
        {
            this.p_refStep = num;
        } // if step defined
        else                            // no step defined
        {
            // set the standard step:
            this.p_refStep = CheckReferenceContainer.STEP_INIT;
        } // else no step defined
    } // getParameters


    /**************************************************************************
     * Get the Container's content out of the database. <BR/>
     * <BR/>
     * First this method tries to load the objects from the database. During
     * this operation a rights check is done, too. If this is all right the
     * objects are returned otherwise an exception is raised. <BR/>
     *
     * @param   operation         Operation to be performed with the objects.
     * @param   selectedElements  object ids that are marked for paste
     *
     * @exception   NoAccessException
     *              The user does not have access to these objects to perform
     *              the required operation.
     */
    protected void performCheckReferencesData (int operation,
                                               Vector<OID> selectedElements)
        throws NoAccessException
    {
        SQLAction action = null;        //SQLAction for Databaseoperations
        CheckReferenceContainerElement obj = null; // the actual list element
        int rowCount = 0;               // row counter
        int i = 0;                      // index of actual tuple
        StringBuffer queryStr = null;   // the query string

        // initialize the number of found elements:
        this.size = 0;

        // ensure a correct ordering:
        if (!this.orderHow.equalsIgnoreCase (BOConstants.ORDER_DESC))
                                        // not descending?
        {
            this.orderHow = BOConstants.ORDER_ASC; // order ascending
        } // if not descending

        // empty the elements vector:
        this.elements.removeAllElements ();

        // get the query string:
        queryStr = this.createQueryCheckReferenceData (selectedElements);

        // check if the string is null
        if (queryStr == null)
        {
            // no query string constructed: that means abort the db operation
            return;
        } // if (queryStr == null)
// test
//debug ("performRetrieveContentData: " + queryStr);

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);

            // empty resultset?
            if (rowCount == 0)
            {
                return;                 // terminate this method
            } // if
            // error while executing?
            else if (rowCount < 0)
            {
                return;
            } // else if

            // everything ok - go on

            // get tuples out of db
            i = 0;                      // initialize loop counter

            while ((this.maxElements == 0 || i++ < this.maxElements) &&
                                        // maximum number of tuples not reached?
                   !action.getEOF ())   // there are tuples left?
            {
                // create an instance of the element's class:
                obj = new CheckReferenceContainerElement ();

                // add element to list of elements:
                this.elements.addElement (obj);
                // set the headings:
                obj.setWidth (this.headings);
                // get data of container element:
                obj.getData (action, this.sess);

                // new number of elements:
                this.size++;

                // step one tuple ahead for the next loop:
                action.next ();
            } // while

            // check if there are more tuples left
            if (!action.getEOF ())      // too many elements?
            {
                // indicate that maximum number of elements have been exceeded
                this.areMaxElementsExceeded = true;
            } // if too many elements

            // the last tuple has been processed
            // end transaction:
            action.end ();
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally
    } // performCheckReferencesData


    /**************************************************************************
     * Create the query to check if copied/cutted data is still valid.
     * <BR/>
     * This method can be overwritten in subclasses. <BR/>
     *
     * @param   selectedElements    Elements that were previously copied/Cutted.
     *
     * @return  The constructed query.
     */
    protected StringBuffer createQueryCheckReferenceData (Vector<OID> selectedElements)
    {
        StringBuffer query = null;      // the query
        StringBuffer oidList = this.createOidStringQu (selectedElements); // the oids

        // check if the list is not empty:
        if (oidList != null && oidList.length () > 0) // list not empty?
        {
            // select the data of all objects that are in the oidList
            query = new StringBuffer ()
                .append ("SELECT o.oid AS oid, o.name AS name,")
                .append (" o.icon AS icon, o.isLink AS isLink,")
                .append (" o.linkedObjectId AS linkedObjectId,")
                .append (" SUM (r.refCount) AS refCount")
                .append (" FROM  ibs_Object o, ")
                .append ("(")
                    .append (" SELECT refO.oid, refO.posNoPath, 1 AS refCount")
                    .append (" FROM ibs_Object rO, ibs_Reference ref,")
                    .append (" ibs_Object refO")
                    .append (" WHERE rO.oid = ref.referencingOid")
                    .append (" AND ref.referencedOid = refO.oid")
                    .append (" AND rO.state = ").append (States.ST_ACTIVE)
                    .append (" AND refO.state = ").append (States.ST_ACTIVE)
                    .append (" AND ref.kind IN (")
                        .append (BOConstants.REF_FIELDREF).append (",")
                        .append (BOConstants.REF_VALUEDOMAIN).append (",")
                        .append (BOConstants.REF_MULTIPLE).append (")")
                    .append (" UNION ALL")
                    .append (" SELECT oid, posNoPath, 0 AS refCount")
                    .append (" FROM ibs_Object")
                    .append (" WHERE oid IN (").append (oidList).append (")")
                .append (") r ")
                .append (" WHERE o.oid IN (").append (oidList).append (")")
                .append (" AND ").append (SQLHelpers.getQueryConditionAttribute (
                    "r.posNoPath", SQLConstants.MATCH_STARTSWITH, "o.posNoPath", true))
                .append (" GROUP BY o.oid, o.name, o.icon, o.isLink,")
                .append (" o.linkedObjectId")
                .append (" ORDER BY o.name ASC");
        } // if list not empty

        // return the computed query:
        return query;
    } // createQueryCheckReferenceData


    /**************************************************************************
     * Count the references of all objects within this container. <BR/>
     *
     * @return  The number of found references, <CODE>0</CODE> if no references
     *          where found.
     */
    public int countReferences ()
    {
        int refCount = 0;               // the number of found references

        // loop through all elements of the vector:
        for (Iterator<? extends ContainerElement> iterator = this.elements.iterator (); iterator
            .hasNext ();)
        {
            // get the actual element:
            CheckReferenceContainerElement elem =
                (CheckReferenceContainerElement) iterator.next ();

            // add the element's references to the sum:
            refCount += elem.getRefCount ();
        } // for iterator

        // return the computed value:
        return refCount;
    } // countReferences


    /**************************************************************************
     * Check the references for a list of business objects. <BR/>
     * The business objects are defined through their oids. It is checked, if
     * there are any references to one of the objects or any objects which are
     * below these objects (recursive check). <BR/>
     * If there are any references found, an integer != 0 is returned, otherwise
     * the return value is <CODE>0</CODE>. <BR/>
     * If there are any references found this method also generates output to
     * enable the user to select the desired elements for the current operation.
     *
     * @param   oids        A string array containing all oids as strings.
     * @param   operation   Operation for rights check.
     *
     * @return  The number of found references, <CODE>0</CODE> if no references
     *          where found.
     */
    public int checkReferences (Vector<OID> oids, int operation)
    {
        int refCount = 0;               // the number of found references

        // ensure that the parameters where read:
        this.getParameters ();

        // check which operation shall be done:
        // check which operation shall be done:
        switch (this.p_refStep)
        {
            case CheckReferenceContainer.STEP_INIT: // initial step
                // ensure the correct headings and orderings:
                this.setHeadingsAndOrderings ();

                try
                {
                    // perform the check for the references and possibly create the output:
                    this.performCheckReferencesData (operation, oids);

                    // set the number of found references:
                    refCount = this.countReferences ();

                    if (refCount > 0)           // references found?
                    {
                        // show the selection list:
                        this.performShowSelectionContent (1, this.orderBy,
                            this.orderHow, AppFunctions.FCT_LISTDELETE,
                            MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
                                BOMessages.ML_MSG_LISTDELETECONFIRMREF, this.env));
                    } // if references found
                } // try
                catch (NoAccessException e)
                {
                    // nothing to do
                } // catch
                break;

            case CheckReferenceContainer.STEP_FINISH:
            default:
                // nothing to do
        } // switch this.p_refStep

        // return the computed value:
        return refCount;
    } // checkReferences


    /**************************************************************************
     * Creates the header of a form. <BR/>
     * This method just adds a hidden field to the form which shall be used for
     * wizard-like operations.
     *
     * @param   page        Page to add a form.
     * @param   name        Name of the object.
     * @param   navItems    Buttons to be shown in NavBar
     * @param   masterName  Name of the master object.
     * @param   operation   Operation to be performed on sumbit.
     * @param   target      Target of the action to be performed when
     *                      submitting the form.
     * @param   icon        Name of the object icon.
     * @param   containerName Name of the container where the object resides.
     * @param   elements    Number of elements in an container
     *
     * @return  The empty group of I/O elements where the body of the page shall
     *          be inserted. <CODE>null</CODE> if there is no body.
     *
     * @see ibs.IbsObject#createFormFooter(FormElement)
     */
    protected FormElement createFormHeader (Page page, String name,
                                            int[] navItems, String masterName,
                                            String operation, String target,
                                            String icon, String containerName,
                                            int elements)
    {
        FormElement form = null;        // the form

        // create the form by calling corresponding method of the super class:
        form = super.createFormHeader (page, name, navItems, masterName,
                                       operation, target, icon, containerName,
                                       elements);

        // add the hidden field to the form to go to the next step:
        form.addElement (new InputElement (BOArguments.ARG_REFSTEP,
                                           InputElement.INP_HIDDEN,
                                           "" + (this.p_refStep + 1)));

        // return the computed form:
        return form;
    } // createFormHeader


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard container. <BR/>
     */
    protected void setHeadingsAndOrderings ()
    {
        // set super attribute
        this.headings = MultilingualTextProvider.getText (
            BOTokens.TOK_BUNDLE, CheckReferenceContainer.LST_HEADINGS, env); 

        // set super attribute
        this.orderings = new String []
        {
            null,
            null,
        }; // this.orderings

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings

} // class CheckReferenceContainer

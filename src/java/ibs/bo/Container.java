/*
 * Class: Container.java
 */

// package:
package ibs.bo;

// imports:
//KR TODO: unsauber
import ibs.app.AppConstants;
import ibs.app.AppFunctions;
import ibs.app.CssConstants;
import ibs.bo.tab.TabConstants;
import ibs.bo.type.TypeConstants;
import ibs.di.KeyMapper.ExternalKey;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.FormElement;
import ibs.tech.html.FrameElement;
import ibs.tech.html.FrameSetElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.IE302;
import ibs.tech.html.ImageElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.LineElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.tech.http.HttpArguments;
import ibs.tech.sql.DBError;
import ibs.tech.sql.DBParameterException;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilExceptions;
import ibs.util.file.FileHelpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This is a base class for business objects being containers. <BR/>
 *
 * @version     $Id: Container.java,v 1.101 2013/01/18 10:38:17 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 980330
 ******************************************************************************
 */
public class Container extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Container.java,v 1.101 2013/01/18 10:38:17 rburgermann Exp $";


    /**
     * Column by which the content of the container shall be ordered. <BR/>
     */
    public int orderBy = BOListConstants.LST_DEFAULTORDERING;

    /**
     * Kind of ordering for the <A HREF="#orderBy">orderBy</A> column. <BR/>
     * <A HREF="UtilConstants.html#BOConstants.ORDER_ASC">BOConstants.ORDER_ASC</A> for ASCending,
     * <A HREF="UtilConstants.html#BOConstants.ORDER_DESC">BOConstants.ORDER_DESC</A>for
     * DESCending.
     */
    public String orderHow = BOConstants.ORDER_ASC;

    /**
     * Heading texts of this container. <BR/>
     * The value (s) of this property can be overwritten within the
     * constructor of each subtype. <BR/>
     */
    protected String[] headings = BOListConstants.LST_HEADINGS;

    /**
     * Ordering attributes of this container. <BR/>
     * The value (s) of this property can be overwritten within the
     * constructor of each subtype. <BR/>
     */
    protected String[] orderings = BOListConstants.LST_ORDERINGS;

    /**
     * Indicates that container has its own default ordering. <BR/>
     * Suppresses currently ordering set.
     */
    protected boolean ownOrdering = false;

    /**
     * Stored procedure used to retrieve the content of the container. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype. <BR/>
     */
    protected String procRetrieveContent = "p_Container$retrieveContent";

    /**
     * View the content of the selectionlist. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype. <BR/>
     */
    protected String viewSelectionContent = "v_Container$content";

    /**
     * Indicate if maxElements have been exceeded. <BR/>
     */
    protected boolean areMaxElementsExceeded = false;

    /**
     * Called order function in header of displayed container list. <BR/>
     */
    protected int fct = AppFunctions.FCT_LISTPERFORM;

    /**
     * When displaying the content within a frameset. <BR/>
     * Shall the frames be displayed as rows (=> true) or as columns
     * (=> false). <BR/>
     */
    protected boolean asRows = true;

    /**
     * Tells whether the content view of the object shall be shown as frameset.
     * <BR/>
     */
    protected boolean showContentAsFrameset = false;

    /**
     * Tells whether the search list change form view of the object shall be shown as
     * frameset. <BR/>
     */
    protected boolean showListChangeFormAsFrameset = false;

    /**
     * Defines whether the extended attributes shall be displayed. <BR/>
     */
    protected boolean p_showExtendedAttributes = false;

    /**
     * Text for name of object within heading. <BR/>
     * Default: <A HREF="UtilConstants.html#TAG_NAME">UtilConstants.TAG_NAME</A>. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype. <BR/>
     */
    protected String headingName = BOMessages.ML_MSG_OBJHEADER_NAME;

    /**
     * Bundle for message to be displayed if the list is empty. <BR/>
     * This message is allowed to contain
     * Default:
     * {@link ibs.bo.BOMessages#MSG_BUNDLE ibs.bo.BOMessages.MSG_BUNDLE}.
     * <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype. <BR/>
     * 
     * The bundle must correspond to the message. So when overwriting
     * the message in a subtype, also set the bundle if it differs
     * from the default bundle.
     */
    protected String msgBundleContainerEmpty = BOMessages.MSG_BUNDLE;
    
    /**
     * Message to be displayed if the list is empty. <BR/>
     * This message is allowed to contain
     * {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME} which is replaced
     * by the name of this object. <BR/>
     * Default:
     * {@link ibs.bo.BOMessages#MSG_CONTAINEREMPTY ibs.bo.BOMessages.MSG_CONTAINEREMPTY}.
     * <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype. <BR/>
     */
    protected String msgContainerEmpty = BOMessages.ML_MSG_CONTAINEREMPTY;
    /**
     * Message to be displayed if the list has too much elements. <BR/>
     * This message is allowed to contain
     * {@link ibs.util.UtilConstants#TAG_NUMBER UtilConstants.TAG_NUMBER} which is replaced
     * by the actual number of elements within the list. <BR/>
     * Default:
     * {@link ibs.bo.BOMessages#MSG_TOOMUCHELEMENTS ibs.bo.BOMessages.MSG_TOOMUCHELEMENTS}.
     * <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype. <BR/>
     */
    protected String msgTooMuchElements = BOMessages.ML_MSG_TOOMUCHELEMENTS;
    /**
     * Message to be displayed if the list is empty. <BR/>
     * This message is allowed to contain
     * {@link ibs.util.UtilConstants#TAG_NUMBER UtilConstants.TAG_NUMBER} which is replaced
     * by the allowed number of elements within the list. <BR/>
     * Default:
     * {@link ibs.bo.BOMessages#MSG_DISPLAYABLEELEMENTS ibs.bo.BOMessages.MSG_DISPLAYABLEELEMENTS}.
     * <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype. <BR/>
     */
    protected String msgDisplayableElements = BOMessages.ML_MSG_DISPLAYABLEELEMENTS;

    /**
     * The class of one element within the list. <BR/>
     */
    private Class<? extends ContainerElement> elementClass;

    /**
     * Remember if the content of the object is actual. <BR/>
     * The value of this property is set to true if the content of the object
     * was already read from the database.
     */
    private boolean isActualContent = false;

    /**
     * flag to indicate if container is selectioncontainer ( = extends SelectionContainer_01)
     * or other container
     */
    public boolean isSingleSelectionContainer = false;

    /**
     * flag that defines if links need to be retrieved fromn the database
     */
    protected boolean retrieveLinks = true;

    /**
     * Vector that holds objects to be pasted. <BR/>
     * Note that this is used for pasting virtual objects and represents
     * a workaround in order not to touch the existing interfaces. <BR/>
     */
    protected Vector<BusinessObject> p_selectedObjects = null;

    /**************************************************************************
     * This constructor creates a new instance of the class Container.
     * <BR/>
     */
    public Container ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
        this.isContainer = true;
        this.msgDeleteConfirm = ibs.bo.BOMessages.ML_MSG_CONTAINERDELETECONFIRM;
    } // Container


    /**************************************************************************
     * Creates a Container object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     * {@link ibs.bo.BusinessObject#isContainer isContainer} is initialized
     *      to <CODE>true</CODE>. <BR/>
     * {@link ibs.bo.BusinessObject#msgDeleteConfirm msgDeleteConfirm} is
     *      initialized to
     *      {@link ibs.bo.BOMessages#MSG_CONTAINERDELETECONFIRM
     *      BOMessages.CONTAINERDELETECONFIRM}. <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Container (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:
        this.isContainer = true;
        this.msgDeleteConfirm = ibs.bo.BOMessages.ML_MSG_CONTAINERDELETECONFIRM;
    } // Container


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // call corresponding method of super class:
        super.initClassSpecifics ();

        // initialize the instance's private properties:

        // set the instance's public properties:
    } // initClassSpecifics


    ///////////////////////////////////////////////////////////////////////////
    // internal helper functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Checks if the content of the object is actual. <BR/>
     * This method checks if the object's content where already read from the
     * data store. <BR/>
     * As extension there could be a check if the content has changed within
     * the data store since it was read. <I>Not implemented yet!</I>. <BR/>
     *
     * @return  <CODE>true</CODE> if the content is actual,
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean isActualContent ()
    {
        // check if the object's content is actual and return the result:
        return this.isActualContent;
    } // isActualContent


    /**************************************************************************
     * Sets the content of the object actual. <BR/>
     * This method sets the object's content actual. <BR/>
     */
    protected void setActualContent ()
    {
        // set the object's content actual:
/*! this is not runnable to go because the content of an object can be changed
 *! without being recognised within the object. So the value stays at false to
 *! ensure that the content is loaded every time from the data store.
        isActualContent = true;
*/
    } // setActualContent


    /**************************************************************************
     * Sets the content of the object actual. <BR/>
     * This method sets the object's content actual. <BR/>
     *
     * @param   bool    Boolean value on which to set the isActualContent
     *                  condition.
     */
    protected void setActualContent (boolean bool)
    {
        this.isActualContent = bool;
    } // setActualContent


    /**************************************************************************
     * ???
     *
     * @param  oid  oid of the searched ContainerElement
     *
     * @return if a ContainerElement with the spezified oid was found method returns
     *         the found ContainerElement otherwise null.
     */
    public ContainerElement getElement (OID oid)
    {
        ContainerElement elem;
        // loop through all ContainerElements and find the one with the specific oid:
        for (Iterator<ContainerElement> iter = this.elements.iterator (); iter.hasNext ();)
        {
            elem = iter.next ();

            if (elem != null && elem.oid != null && elem.oid.equals (oid))
            {
                return elem;
            } // if
        } // for iter

        return null;
    } // getElement


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <CODE>env</CODE> property is used
     * for getting the parameters. This property must be set before calling
     * this method. <BR/>
     */
    public void getParameters ()
    {
        String str = null;
        int num = 0;

        // get parameters relevant for super class:
        super.getParameters ();

        if (!this.ownOrdering ||
            (this.env.getIntParam (BOArguments.ARG_REORDER) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID))
        {
            // get kind of ordering: ASCending or DESCending
            if ((str = this.env.getStringParam (BOArguments.ARG_ORDERHOW)) != null)
            {
                this.orderHow = str;
            } // if
            else
            {
                this.orderHow = this.getUserInfo ().orderHow; // set actual ordering
            } // else
            // ensure valid ordering:
            if (!this.orderHow.equalsIgnoreCase (BOConstants.ORDER_DESC))
            {
                this.orderHow = BOConstants.ORDER_ASC;
            } // if

            // get order column:
            if ((num = this.env.getIntParam (BOArguments.ARG_ORDERBY)) !=
                IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
            {
                this.orderBy = num;
            } // if
            else
            {
                this.orderBy = this.getUserInfo ().orderBy; // set actual order column
            } // else

            // ensure that there is an available ordering taken:
            // BB TODO: ensuring the ordering at this point leads to confusion
            // because the orderings for the specific container are set to
            // standard ordering at this point.
            // this leads to loss of orderHow and orderBy
            // setting in case the orderBy is greater then the standard ordering
//            ensureAvailableOrdering ();

            // store ordering:
            (this.getUserInfo ()).orderBy = this.orderBy;
            (this.getUserInfo ()).orderHow = this.orderHow;
            // get max elements
            if ((num = this.env.getIntParam (BOArguments.ARG_MAXRESULT)) !=
                IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
            {
                this.maxElements = num;
            } // if
        } // if
    } // getParameters


    /**************************************************************************
     * Get the Container's content out of the database. <BR/>
     * <BR/>
     * First this method tries to load the objects from the database. During
     * this operation a rights check is done, too. If this is all right the
     * objects are returned otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the objects.
     * @param   orderBy     Property, by which the result shall be
     *                      sorted. If this parameter is null the
     *                      default order is by name.
     * @param   orderHow    Kind of ordering: BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                      null => BOConstants.ORDER_ASC
     *
     * @exception   NoAccessException
     *              The user does not have access to these objects to perform
     *              the required operation.
     */
    protected void performRetrieveContentData (int operation, int orderBy,
                                               String orderHow)
        throws NoAccessException
    {
        this.performRetrieveContentData (operation, orderBy, orderHow, null);
    } // performRetrieveContentData


    /**************************************************************************
     * Get the Container's content out of the database. <BR/>
     * <BR/>
     * First this method tries to load the objects from the database. During
     * this operation a rights check is done, too. If this is all right the
     * objects are returned otherwise an exception is raised. <BR/>
     *
     * @param   operation         Operation to be performed with the objects.
     * @param   orderBy           Property, by which the result shall be
     *                            sorted. If this parameter is null the
     *                            default order is by name.
     * @param   orderHow          Kind of ordering: BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                            null => BOConstants.ORDER_ASC
     * @param   selectedElements  object ids that are marked for paste
     *
     * @exception   NoAccessException
     *              The user does not have access to these objects to perform
     *              the required operation.
     */
    protected void performRetrieveContentData (int operation, int orderBy,
                                               String orderHow,
                                               Vector<OID> selectedElements)
        throws NoAccessException
    {
        String orderHowLocal = orderHow; // variable for local assignments
        Class<? extends ContainerElement> elementClass = null;
        SQLAction action = null;        //SQLAction for database operations
        ContainerElement obj;
        int rowCount;                   // row counter
        int i;                          // index of actual tuple
        this.size = 0;
        StringBuffer queryStr = new StringBuffer (); // the complete query
        String queryPart = null;        // the current queryPart
        boolean isPasteMode = selectedElements != null; // are we currently
                                        // pasting something?
        boolean isAddCheckRights = !isPasteMode; // shall a rights check be
                                        // done within the query?

        // ensure a correct ordering:
        if (!orderHowLocal.equalsIgnoreCase (BOConstants.ORDER_DESC)) // not descending?
        {
            orderHowLocal = BOConstants.ORDER_ASC;       // order ascending
        } // if not descending


        // empty the elements vector:
        this.elements.removeAllElements ();

        // if no elements selected prepare a query to get the content data from the data base
        if (!isPasteMode)
        {
            queryPart = this.createQueryRetrieveContentData ();
        } // if
        // if elements are selected prepare a query to check which may be displayed
        else
        {
            queryPart = this.createQueryCopyData (selectedElements);
            isAddCheckRights = false;
        } // else

        // check if the string is null
        if (queryPart == null)
        {
            // no query string constructed: that means abort the db operation
            return;
        } // if (query == null)

        // query part was set
        // add the query part to the query:
        queryStr.append (queryPart);

        // get the elements out of the database:
        // create the SQL String to select all tuples

 /*************
  * HACK!
  * necessary for the retrieve of the deletable objects
  * having regard to the checkOut-flag
  */
        if ((operation & Operations.OP_DELETE) == Operations.OP_DELETE)
        {
            // add the checkOut-flag constraints
            queryStr.append (this.extendQueryConstraintsForCheckOutFlag (isPasteMode));

            // add the delete specific constraints:
            queryStr.append (this.extendQueryRetrieveDeleteData ());
        } // if
/*
 * HACK Ende
 **************/
        // check if the rights shall be proved:
        if (isAddCheckRights)           // add rights check?
        {
            queryStr
                .append (" AND userId = ").append (this.user.id)
                .append (SQLHelpers.getStringCheckRights (operation));
        } // if add rights check
        queryStr
            .append (" ORDER BY ").append (this.orderings[orderBy])
            .append (" ").append (orderHowLocal);

// test
//debug ("performRetrieveContentData: " + queryStr);

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
//debug ("vor container action.execute (queryStr, false);");
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

            // get the class of one element of this container:
            if (isPasteMode)
            {
                // always use the standard container element:
                elementClass = ContainerElement.class;
            } // if
            else
            {
                // use the container specific element class:
                elementClass = this.getElementClass ();
            } // else

            // get tuples out of db
            i = 0;                      // initialize loop counter

            while ((this.maxElements == 0 || i++ < this.maxElements) &&
                                        // maximum number of tuples not reached?
                   !action.getEOF ())   // there are tuples left?
            {
                // create a new object:
                try
                {
                    // create an instance of the element's class:
                    obj = elementClass.newInstance ();

                    // temporary solution for the problem: what happens if
                    // one element occures more then one time in the resultset of
                    // the selectstatement - problem is solved in method
                    // getContainerElementData (see Class ProductGroup_01)

                    // add element to list of elements:
                    this.elements.addElement (obj);
                    // get and set values for element:
                    obj.oid = SQLHelpers.getQuOidValue (action, "oid");
                    // get specific data of container element:
                    if (isPasteMode)
                    {
                        this.getContainerElementCopyData (action, obj);
                    } // if
                    else
                    {
                        this.getContainerElementData (action, obj);
                    } // else
                } // try
                catch (IllegalAccessException e)
                {
                    // should not occur
                    IOHelpers.showMessage (e, this.app, this.sess, this.env,
                        true);
                } // catch
                catch (InstantiationException e)
                {
                    // should not occur
                    IOHelpers.showMessage (e, this.app, this.sess, this.env,
                        true);
                } // catch
                this.size++;
                // step one tuple ahead for the next loop:
                action.next ();
            } // while

            // check if there are more tuples left
            if (!action.getEOF ())
            {
                // indicate that maximum number of elements have been exceeded
                this.areMaxElementsExceeded = true;
            } // if

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
//debug (" --- Container.performRetrieveContentData ENDE --- ");
    } // performRetrieveContentData


    /**************************************************************************
     * Extend the query with constraints regarding the checkout flag. <BR/>
     * This method just adds some constraints to the already existing query.
     * It must be empty or start with "AND...".
     * This method can be overwritten in subclasses. <BR/>
     * 
     * The creation of separate method has become necessary since
     * XMLViewerContainer objects need a special implementation
     * see IBS-279 Error when executing List Delete operation on container.
     *
     * @param isPasteMode Defines if the query is executed within the past mode
     *
     * @return  The extension to the query.
     */
    protected StringBuffer extendQueryConstraintsForCheckOutFlag (boolean isPasteMode)
    {
        StringBuffer queryStr = new StringBuffer ()
            .append (SQLConstants.SQL_AND)
                .append (SQLHelpers.getBitAnd ("flags", "16"))
                .append ("<> 16 ");

        // check if the actual user is the Administrator:
        // (the administrator is allowed to delete objects which are marked
        // as undeletable)
        if (!this.getUser ().username.equalsIgnoreCase (IOConstants.USERNAME_ADMINISTRATOR))
                                    // not Administrator
        {
            // ensure that the object is deletable:
            queryStr
                .append (SQLConstants.SQL_AND)
                    .append (SQLHelpers.getBitAnd (
                        "flags", "" + BOConstants.FLG_NOTDELETABLE))
                    .append ("<> ").append (BOConstants.FLG_NOTDELETABLE)
                    .append (" ");
        } // if not Administrator
        
        return queryStr;
    } // extendQueryCheckCheckOutFlag


/////////////////////////
//
// START HP: Get container content for "Multiple Forward"
//

    /**************************************************************************
     * Create the query to get the container's content for multiple forward.
     * <BR/>
     * The query or view must at least have the attributes userId and rights,
     * and the table 'ibs_Workflow_01 wf' which has to be connected to
     * objects to be forwarded with statement: WHERE oid = wf.objectId.
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * !!!!!            IMPORTANT                             !!!!!
     * !!!! the tablealias for ibs_Workflow_01 has to be 'wf' !!!!!
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveForwardContentData ()
    {
        StringBuffer queryStr = new StringBuffer (); // the query

        // create query to get containers content (without links)
        queryStr
            .append ("SELECT DISTINCT c.oid, c.state, c.name, c.typeCode, c.typeName,")
            .append (" c.isLink, c.linkedObjectId, c.owner, c.ownerName,")
            .append (" c.ownerOid, c.ownerFullname, c.lastChanged, c.isNew,")
            .append (" c.icon, c.description, c.flags, c.processState")
            .append (" FROM ").append (this.viewContent).append (" c,")
            .append (" ibs_Workflow_01 wf ")
            .append (" WHERE c.containerId = ").append (this.oid.toStringQu ())
            .append (" AND c.isLink = 0 ")
            // workflow-constraints:
            // 1. only objects with an active workflow attached are forward-able
            // 2. only the (wf-)current-owner is allowed to forward an object
            .append (" AND c.oid = wf.objectId ");

        // return the computed query string:
        return queryStr.toString ();
    } // createQueryRetrieveForwardContentData


    /**************************************************************************
     * Get the Container's content (only forwardable objects)
     * out of the database. <BR/>
     *
     * @param   operation         Operation to be performed with the objects.
     * @param   orderBy           Property, by which the result shall be
     *                            sorted. If this parameter is null the
     *                            default order is by name.
     * @param   orderHow          Kind of ordering: BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                            null => BOConstants.ORDER_ASC
     * @param   selectedElements  object ids that are marked for paste
     *
     * @exception   NoAccessException
     *              The user does not have access to these objects to perform
     *              the required operation.
     */
    protected void performRetrieveForwardContentData (int operation,
        int orderBy, String orderHow, Vector<OID> selectedElements)
        throws NoAccessException
    {
        String orderHowLocal = orderHow; // variable for local assignments
        Class<? extends ContainerElement> elementClass = null;
        SQLAction action = null;
        ContainerElement obj;
        StringBuffer queryStr = null;   // the query
        int rowCount;
        int i;
        this.size = 0;

        // ensure a correct ordering:
        if (!orderHowLocal.equalsIgnoreCase (BOConstants.ORDER_DESC)) // not descending?
        {
            orderHowLocal = BOConstants.ORDER_ASC;       // order ascending
        } // if not descending

        // empty the elements vector:
        this.elements.removeAllElements ();

        queryStr = new StringBuffer (this.createQueryRetrieveForwardContentData ())
            .append (" AND userId = ").append (this.user.id)
            .append (SQLHelpers.getStringCheckRights (operation))
            .append (" ORDER BY ").append (this.orderings[orderBy])
            .append (" ").append (orderHowLocal);

//debug ("QUERY:" + queryStr);

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

            // get the class of one element of this container:
            elementClass = this.getElementClass ();

            // get tuples out of db
            i = 0;                      // initialize loop counter

            while ((this.maxElements == 0 || i++ < this.maxElements) &&
                                        // maximum number of tuples not reached?
                   !action.getEOF ())   // there are tuples left?
            {
                // create a new object:
                try
                {
                    // create an instance of the element's class:
                    obj = elementClass.newInstance ();

                    // temporary solution for the problem: what happens if
                    // one element occurs more then one time in the result set of
                    // the select statement - problem is solved in method
                    // getContainerElementData (see Class ProductGroup_01)

                    // add element to list of elements:
                    this.elements.addElement (obj);
                    // get and set values for element:
                    obj.oid = SQLHelpers.getQuOidValue (action, "oid");
                    // get specific data of container element:
                    this.getContainerElementData (action, obj);
                } // try
                catch (IllegalAccessException e)
                {
                    // should not occur
                    IOHelpers.showMessage (e, this.app, this.sess, this.env,
                        true);
                } // catch
                catch (InstantiationException e)
                {
                    // should not occur
                    IOHelpers.showMessage (e, this.app, this.sess, this.env,
                        true);
                } // catch

                this.size++;
                // step one tuple ahead for the next loop:
                action.next ();
            } // while

            // check if there are more tuples left
            if (!action.getEOF ())
            {
                // indicate that maximum number of elements have been exceeded
                this.areMaxElementsExceeded = true;
            } // if

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
    } // performRetrieveForwardContentData
//
// END HP: Get container content for "Multiple Forward"
//
/////////////////////////



    /**************************************************************************
     * Get the Content for the Selectionlist out of the DB. <BR/>
     * <B>THE METHOD createQueryRetrieveSelectionData () MUST BE OVERWRITTEN</B>
     * <BR/>
     * First this method tries to load the objects from the database. During
     * this operation a rights check is done, too. If this is all right the
     * objects are returned otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the objects.
     * @param   orderBy     Property, by which the result shall be
     *                      sorted. If this parameter is null the
     *                      default order is by name.
     * @param   orderHow    Kind of ordering: BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                      null => BOConstants.ORDER_ASC
     *
     * @exception   NoAccessException
     *              The user does not have access to these objects to perform
     *              the required operation.
     */
    protected void performRetrieveSelectionData (int operation, int orderBy,
                                                 String orderHow)
        throws NoAccessException
    {
        String orderHowLocal = orderHow; // variable for local assignments
        Class<? extends ContainerElement> elementClass = null;
        SQLAction action = null;        // the action object used to access the
                                        // database
        ContainerElement obj;
        int rowCount;                   // row counter
        int i;                          // index of actual tuple
        this.size = 0;
        StringBuffer queryStr = null;   // the query

        // ensure a correct ordering:
        if (!orderHowLocal.equalsIgnoreCase (BOConstants.ORDER_DESC)) // not descending?
        {
            orderHowLocal = BOConstants.ORDER_ASC;       // order ascending
        } // if not descending



        // empty the elements vector:
        this.elements.removeAllElements ();

        // get the elements out of the database:
        // create the SQL String to select all tuples
        queryStr = new StringBuffer (this.createQueryRetrieveSelectionData ())
            .append (" AND userId = ").append (this.user.id)
            .append (SQLHelpers.getStringCheckRights (operation))
            .append ("ORDER BY ").append (this.orderings[orderBy])
            .append (" ").append (orderHowLocal);


//debug ("performRetrieveSelectionData: " + queryStr);
        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
//debug ("vor container action.execute (queryStr, false);");
            rowCount = action.execute (queryStr, false);

            // empty result set?
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

            // get the class of one element of this container:
            elementClass = this.getElementClass ();

            // get tuples out of db
            i = 0;                      // initialize loop counter

            while ((this.maxElements == 0 || i++ < this.maxElements) &&
                                        // maximum number of tuples not reached?
                   !action.getEOF ())   // there are tuples left?
            {
                // create a new object:
                try
                {
                    // create an instance of the element's class:
                    obj = elementClass.newInstance ();

                // get and set values for element:
                    obj.oid = SQLHelpers.getQuOidValue (action, "oid");
                    // get specific data of container element:
                    this.getContainerElementData (action, obj);
                    // add element to list of elements:
                    this.elements.addElement (obj);
                } // try
                catch (IllegalAccessException e)
                {
                    // should not occur
                    IOHelpers.showMessage (e, this.app, this.sess, this.env,
                        true);
                } // catch
                catch (InstantiationException e)
                {
                    // should not occur
                    IOHelpers.showMessage (e, this.app, this.sess, this.env,
                        true);
                } // catch

                this.size++;

                // step one tuple ahead for the next loop:
                action.next ();
            } // while

            // check if there are more tuples left
            if (!action.getEOF ())
            {
                // indicate that maximum number of elements have been exceeded
                this.areMaxElementsExceeded = true;
            } // if

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
    } // performRetrieveContentData


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     * The query or view must at least have the attributes userId and rights.
     * Queries on these attributes have to be addable to this query. <BR/>
     * The query must have at least one constraint within WHERE to ensure that
     * other constraints can be added with AND. <BR/>
     * <B>Standard format:</B>. <BR/>
     *      "SELECT DISTINCT oid, &lt;other attributes> " +
     *      " FROM " + this.viewContent +
     *      " WHERE containerId = " + oid;. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     * As you can see the query should contain at least the oid.
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
    //use when everybody has flags within his / her views:
        StringBuffer queryStr = null;

        queryStr = new StringBuffer ()
            .append ("SELECT DISTINCT oid, state, name, typeCode, typeName, isLink,")
            .append ("        linkedObjectId, owner, ownerName, ownerOid,")
            .append ("        ownerFullname, lastChanged, isNew, icon,")
            .append ("        description, flags, processState")
            .append (" FROM    ").append (this.viewContent)
            .append (" WHERE   containerId = ").append (this.oid.toStringQu ());

        // some operations must not retrieve links
        // in these cases exclude all links from the query
        if (!this.retrieveLinks)
        {
            queryStr.append (" AND isLink = 0 ");
        } // if

        // return the computed query string:
        return queryStr.toString ();
/*
        return
            "SELECT DISTINCT oid, state, name, typeName, isLink, " +
            "        linkedObjectId, owner, ownerName, ownerOid, " +
            "        ownerFullname, lastChanged, isNew, icon, description " +
            "FROM    " + this.viewContent + " " +
            "WHERE   containerId = " + this.oid;

*/
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Create a string consisting of oids which are contained in a vector. <BR/>
     * This string has the format: <CODE>&lt;oid>, &lt;oid>, ...</CODE> and is
     * intended for use in queries
     * (<CODE>... WHERE ... IN (createOidStringQu (Vector)) ...</CODE>).
     * Each oid is got out of the Vector and converted to a string using the
     * method {@link ibs.bo.OID#toStringQu () toStringQu}.
     *
     * @param   elements    The oids to be concatenated to the string.
     *
     * @return  The constructed string.
     */
    protected final StringBuffer createOidStringQu (Vector<OID> elements)
    {
        StringBuffer oidList = new StringBuffer (); // the string
        String sep = "";                // the separator

        // put all selected elements into a string, where the elements are
        // separated by a comma:
        // loop through all elements of the list:
        for (Enumeration<OID> elems = elements.elements (); elems.hasMoreElements ();)
        {
            // get the actual oid and append it to the string:
            oidList
                .append (sep)
                .append (elems.nextElement ().toStringQu ());
            // set the new separator:
            sep = ", ";
        } // for

        // return the computed string:
        return oidList;
    } // createOidStringQu


    /**************************************************************************
     * Create the query to check if copied/cutted data is still valid.
     * <BR/>
     *
     * @param   selectedElements    Elements that were previously copied/cutted.
     *
     * @return  The constructed query.
     */
    protected final String createQueryCopyData (Vector<OID> selectedElements)
    {
        StringBuffer query = new StringBuffer (); // the query
        StringBuffer oidList = this.createOidStringQu (selectedElements); // the oids

        // select the data of all objects that are in the oidList
/*
        query = "SELECT DISTINCT oid, state, name, typeName, isLink, " +
                "        linkedObjectId, owner, ownerName, ownerOid, " +
                "        ownerFullname, lastChanged, isNew, icon, description, " +
                "        flags, processState " +
*/
/* KR 040611 dropped away because of performance reasons
        query
            .append (" SELECT oid, state, name, typeName, isLink,")
                .append (" linkedObjectId, owner, u.ownerName AS ownerName,")
                .append (" '' AS ownerOid, u.ownerFullName AS ownerFullname,")
                .append (" lastChanged, 0 AS isNew,")
                .append (" icon, description, flags, processState")
            .append (" FROM ibs_Object o,")
                .append (" (SELECT id AS userId, name AS ownerName,")
                .append (" fullName AS ownerFullName")
                .append (" FROM ibs_User) u")
            .append (" WHERE o.owner = u.userId")
                .append (" AND o.state = ").append (States.ST_ACTIVE);
*/
        query
            .append (" SELECT oid, state, name, typeCode, typeName, isLink,")
            .append (" linkedObjectId, owner, lastChanged,")
            .append (" icon, description, flags, processState")
            .append (" FROM ibs_Object")
            .append (" WHERE state = ").append (States.ST_ACTIVE);

        // check if there are elements to search for
        if (oidList.length () == 0)     // no elements to search for
        {
            // query should return NO tuples
            query.append (" AND oid <> oid");
        } // if no elements to search for
        else                            // there are elements to search for
        {
            query.append (" AND oid IN (").append (oidList).append (")");
        } // else there are elements to search for
/*
        query = createQueryRetrieveContentData ();
        // check if there are elements to search for
        if (oidList == "")              // no elements to search for
            // querey should return NO tuples
            query += " AND oid <> oid";
        else                            // there are elements to search for
            query += " AND oid IN (" + oidList + ")";
*/

        // return the computed query:
        return query.toString ();
    } // createQueryCopyData


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
     * The property <B>oid</B> is already gotten. This must not be done within this
     * method. <BR/>
     * This method can be overwritten in subclasses. <BR/>
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
        int flags = 0;                  // the flags which are set

        commonObj.state = action.getInt ("state");
        commonObj.name = action.getString ("name");
        commonObj.typeName = action.getString ("typeName");
        commonObj.isLink = action.getBoolean ("isLink");
        commonObj.linkedObjectId = SQLHelpers.getQuOidValue (action, "linkedObjectId");
        commonObj.owner = new User (action.getInt ("owner"));
        commonObj.owner.username = action.getString ("ownerName");
        commonObj.owner.oid = SQLHelpers.getQuOidValue (action, "ownerOid");
        commonObj.owner.fullname = action.getString ("ownerFullname");
        commonObj.lastChanged = action.getDate ("lastChanged");
        commonObj.isNew = action.getBoolean ("isNew");
        commonObj.icon = action.getString ("icon");
        commonObj.description = action.getString ("description");
        if ((this.sess != null) && (this.sess.activeLayout != null))
        {
            commonObj.layoutpath = this.sess.activeLayout.path;
        } // if
        else
        {
            commonObj.layoutpath = "";
        } // else
        // - HACK HACK -
        // get the typeCode out of the resultSet
        // if not typeCode available a DBParameterException is thrown
        // TODO: This try should not be necessary - find all select statements which not include the typeCode yet!
        try
        {
            commonObj.typeCode = action.getString ("typeCode");
        } // try
        catch (DBParameterException e)
        {
            commonObj.typeCode = null;
        } // catch DBParameterException
        
        // use when everybody has flags within his/her views:
        // this statement help us to avoid mistakes because of different queries
        // in the sub structures:
        try
        {
            flags = action.getInt ("flags");
        } // try
        catch (DBError e)
        {
            flags = 0;
        } // catch
        // obj.flags = action.getInt ("flags");

        commonObj.showInWindow = this.getUserInfo ().userProfile.showFilesInWindows;

        if ((flags & BOConstants.FLG_HYPERMASTER) ==
            BOConstants.FLG_HYPERMASTER)
        {
            commonObj.masterSelect = BOConstants.FLG_HYPERMASTER;
        } // if
        else if ((flags & BOConstants.FLG_FILEMASTER) ==
            BOConstants.FLG_FILEMASTER)
        {
            commonObj.masterSelect = BOConstants.FLG_FILEMASTER;
        } // else if

        if ((flags & BOConstants.FLG_ISWEBLINK) ==
            BOConstants.FLG_ISWEBLINK)
        {
            // if the object is a Hyperlink with internal weblink then
            // the content should not be shown in a window but in the
            // frameset
            commonObj.showInWindow = false;
            commonObj.isWeblink = true;
        } // else
    } // getContainerElementData


    /**************************************************************************
     * Get the element specific data out of the actual tuple of the query
     * result. <BR/>
     * This metod is used to get all attributes of one element of a copy
     * selection out of the resultset. The attribute names which can be used
     * are the ones which are defined within the resultset of
     * {@link #createQueryCopyData (Vector) createQueryCopyData}.
     * <BR/>
     * <B>Format:</B>. <BR/>
     * for oid properties:
     *      obj.&lt;property> = getQuOidValue (action, "&lt;attribute>");. <BR/>
     * for other properties:
     *      obj.&lt;property> = action.get&lt;type> ("&lt;attribute>");. <BR/>
     * The property <B>oid</B> is already gotten. This must not be done within this
     * method. <BR/>
     *
     * @param   action      The action for the database connection.
     * @param   commonObj   Object representing the list element.
     *
     * @exception   DBError
     *              Error when executing database statement.
     *
     * @see ibs.bo.Container#getContainerElementData (SQLAction, ContainerElement)
     */
    protected final void getContainerElementCopyData (SQLAction action,
                                                      ContainerElement commonObj)
        throws DBError
    {
        int flags = 0;                  // the flags which are set

        commonObj.state = action.getInt ("state");
        commonObj.name = action.getString ("name");
        commonObj.typeName = action.getString ("typeName");
        commonObj.isLink = action.getBoolean ("isLink");
        commonObj.linkedObjectId = SQLHelpers.getQuOidValue (action, "linkedObjectId");
        commonObj.owner = new User (action.getInt ("owner"));
/* KR 040611 dropped away because of performance reasons
        commonObj.owner.username = action.getString ("ownerName");
        commonObj.owner.oid = SQLHelpers.getQuOidValue (action, "ownerOid");
        commonObj.owner.fullname = action.getString ("ownerFullname");
        commonObj.isNew = action.getBoolean ("isNew");
*/
        commonObj.lastChanged = action.getDate ("lastChanged");
        commonObj.icon = action.getString ("icon");
        commonObj.description = action.getString ("description");
        if ((this.sess != null) && (this.sess.activeLayout != null))
        {
            commonObj.layoutpath = this.sess.activeLayout.path;
        } // if
        else
        {
            commonObj.layoutpath = "";
        } // else
        // - HACK HACK -
        // get the typeCode out of the resultSet
        // if not typeCode available a DBParameterException is thrown
        // TODO: This try should not be necessary - find all select statements which not include the typeCode yet!
        try
        {
            commonObj.typeCode = action.getString ("typeCode");
        } // try
        catch (DBParameterException e)
        {
            commonObj.typeCode = null;
        } // catch DBParameterException

        // use when everybody has flags within his/her views:
        // this statement help us to avoid mistakes because of different queries
        // in the sub structures:
        try
        {
            flags = action.getInt ("flags");
        } // try
        catch (DBError e)
        {
            flags = 0;
        } // catch
        // obj.flags = action.getInt ("flags");

        commonObj.showInWindow = this.getUserInfo ().userProfile.showFilesInWindows;

        if ((flags & BOConstants.FLG_HYPERMASTER) ==
            BOConstants.FLG_HYPERMASTER)
        {
            commonObj.masterSelect = BOConstants.FLG_HYPERMASTER;
        } // if
        else if ((flags & BOConstants.FLG_FILEMASTER) ==
            BOConstants.FLG_FILEMASTER)
        {
            commonObj.masterSelect = BOConstants.FLG_FILEMASTER;
        } // else if

        if ((flags & BOConstants.FLG_ISWEBLINK) ==
            BOConstants.FLG_ISWEBLINK)
        {
            // if the object is a Hyperlink with internal weblink then
            // the content should not be shown in a window but in the
            // frameset
            commonObj.showInWindow = false;
            commonObj.isWeblink = true;
        } // else
    } // getContainerElementCopyData


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This metod is used to get all attributes of one element out of the
     * resultset. The attribute names which can be used are the ones which
     * are defined within the resultset of
     * <A HREF="#createQueryRetrieveContentData">createQueryRetrieveContentData</A>.
     * <BR/>
     * <B>Format:</B>. <BR/>
     * for oid properties:
     *      obj.&lt;property> = getQuOidValue (action, "&lt;attribute>");. <BR/>
     * for other properties:
     *      obj.&lt;property> = action.get&lt;type> ("&lt;attribute>");. <BR/>
     * The property <B>oid</B> is already gotten. This must not be done within this
     * method. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     *
     * @param   virtualObj  The virtual business object.
     * @param   obj         Object representing the list element.
     */
    protected void getVirtualContainerElementData (BusinessObject virtualObj,
                                                   ContainerElement obj)
    {
        // set the virtual object specific data
        obj.p_isVirtual = true;
        obj.p_virtualId = virtualObj.p_virtualId;
        // set the standard object data
        obj.oid = virtualObj.oid;
        obj.state = virtualObj.state;
        obj.name = virtualObj.name;
        obj.typeName = virtualObj.typeName;
        obj.isLink = virtualObj.isLink;
        obj.linkedObjectId = virtualObj.linkedObjectId;
        obj.owner = virtualObj.owner;
        obj.lastChanged = virtualObj.lastChanged;
        obj.isNew = false;
        obj.icon = virtualObj.icon;
        obj.description = virtualObj.description;
        if ((this.sess != null) && (this.sess.activeLayout != null))
        {
            obj.layoutpath = this.sess.activeLayout.path;
        } // if
        else
        {
            obj.layoutpath = "";
        } // else

        // use when everybody has flags within his/her views:
        // this statement help us to avoid mistakes because of different queries
        // in the sub structures:
        // BB: because the flags are not stored in the businessObject
        // the following code can be deactivated
/*
        flags = 0;
        obj.showInWindow = this.getUserInfo ().userProfile.showFilesInWindows;
        if ((flags & BOConstants.FLG_HYPERMASTER) ==
                BOConstants.FLG_HYPERMASTER)
            obj.masterSelect = BOConstants.FLG_HYPERMASTER;
        else if ((flags & BOConstants.FLG_FILEMASTER) ==
                BOConstants.FLG_FILEMASTER)
            obj.masterSelect = BOConstants.FLG_FILEMASTER;

        if ((flags & BOConstants.FLG_ISWEBLINK) == BOConstants.FLG_ISWEBLINK)
        {
            // if the object is a Hyperlink with internal weblink then
            // the content should not be shown in a window but in the
            // frameset
            obj.showInWindow = false;
            obj.isWeblink = true;
        } // if ((flags & BOConstants.FLG_ISWEBLINK) == BOConstants.FLG_ISWEBLINK)
*/
    } // getVirtualContainerElementData


    /**************************************************************************
     * Get the Container's content out of the database. <BR/>
     * This method checks if the objects were already loaded into memory. In
     * this case it checks if the user's rights are sufficient to perform the
     * requested operation on the objects. If this is all right the objects are
     * returned otherwise an exception is raised. <BR/>
     * If the objects are not already in the memory they must be loaded from the
     * database. In this case there is also a rights check done. If this is all
     * right the objects are returned otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the objects.
     * @param   orderBy     Property, by which the result shall be
     *                      sorted. If this parameter is null the
     *                      default order is by name.
     * @param   orderHow    Kind of ordering: BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                      null => BOConstants.ORDER_ASC
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    public void retrieveContent (int operation,
                                 int orderBy, String orderHow)
        throws NoAccessException
    {
//debug (" --- Container.retrieveContent ANFANG --- ");
        // check if columns shall be reduced
        this.setHeadingsAndOrderings ();

        if (this.isActualContent ())    // content already in memory?
        {
//debug ("isActualContent = true");
            // check user's rights:
            if (!this.user.hasRights (this.oid, operation)) // access not allowed?
            {
                // raise no access exception
                NoAccessException error = new NoAccessException (
                    MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
                        UtilExceptions.ML_E_NOACCESSEXCEPTION, env));
                throw error;
            } // if access not allowed
        } // if content already in memory
        else                            // content not in memory
        {
//IOHelpers.printTrace ("  before performRetrieveContentData");
            // call the object type specific method:
            this.performRetrieveContentData (operation, orderBy, orderHow);
//IOHelpers.printTrace ("  after performRetrieveContentData");
            this.setActualContent ();   // the content within the object is
                                        // actual
//IOHelpers.printTrace ("  after setActualContent");
        } // else content not in memory
//debug (" --- Container.retrieveContent ENDE --- ");
    } // retrieveContent


    /**************************************************************************
     * Show the the Container, i.e. its content. <BR/>
     * This method calls <A HREF="#showContent">showContent</A> to show the
     * content of the container. Thus the properties
     * <A HREF="#orderBy">orderBy</A> and <A HREF="#orderHow">orderHow</A> must
     * be set before the call of this method. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void show (int representationForm)
    {
        // show the content of this container:
        this.showContent (representationForm);
    } // show


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
            Buttons.BTN_STARTWORKFLOW,
            Buttons.BTN_FORWARD,
            Buttons.BTN_FINISHWORKFLOW,
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons


    /**************************************************************************
     * Is the object type allowed in workflows? <BR/>
     * This method shall be overwritten in subclasses.
     *
     * @return  <CODE>true</CODE> if the object type is allowed in workflows,
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean isWfAllowed ()
    {
        // check if this is a standard container:
        // subclasses must implement their own check
        return this.getClass ().getName ().equals ("ibs.bo.Container");
    } // isWfAllowed


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
        // for the data interchange container the buttons
        //      o delete list
        //      o copy list
        //      o cut list
        // should not be displayed
        if (this.oid.tVersionId ==
            this.getTypeCache ().getTVersionId (TypeConstants.TC_IntegratorContainer))
        {
            // define buttons to be displayed:
            int[] buttons =
            {
                Buttons.BTN_NEW,
                Buttons.BTN_PASTE,
                Buttons.BTN_SEARCH,
//                Buttons.BTN_HELP,
//                Buttons.BTN_LISTDELETE,
                Buttons.BTN_REFERENCE,
//                Buttons.BTN_LIST_COPY,
//                Buttons.BTN_LIST_CUT,
                Buttons.BTN_DISTRIBUTE,
            }; // buttons
            // return button array
            return buttons;
        } // if IntegratorContainer

        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_NEW,
            Buttons.BTN_PASTE,
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_REFERENCE,
            Buttons.BTN_LIST_COPY,
            Buttons.BTN_LIST_CUT,
            Buttons.BTN_DISTRIBUTE,
// HP: Multiple Forward
            Buttons.BTN_LISTFORWARD,
        }; // buttons
        // return button array
        return buttons;
    } // setContentButtons


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <CODE>env</CODE> property is used
     * for getting the parameters. This property must be set before calling
     * this method. <BR/>
     *
     * @return  A string array.
     */
    public String[] getSelectedElements ()
    {
        String str = null;
        String[] multiStr = null;
        int num = 0;

        // get parameters relevant for super class:
        super.getParameters ();

        if (!this.ownOrdering ||
            (this.env.getIntParam (BOArguments.ARG_REORDER) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID))
        {
            // get kind of ordering: ASCending or DESCending
            if ((str = this.env.getStringParam (BOArguments.ARG_ORDERHOW)) != null)
            {
                this.orderHow = str;
            } // if
            else
            {
                this.orderHow = this.getUserInfo ().orderHow; // set actual ordering
            } // else
            // ensure valid ordering:
            if (!this.orderHow.equalsIgnoreCase (BOConstants.ORDER_DESC))
            {
                this.orderHow = BOConstants.ORDER_ASC;
            } // if

            // get order column:
            if ((num = this.env.getIntParam (BOArguments.ARG_ORDERBY)) !=
                IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
            {
                this.orderBy = num;
            } // if
            else
            {
                this.orderBy = this.getUserInfo ().orderBy; // set actual order column
            } // else
            if (this.orderBy < 0 || this.orderBy >= this.orderings.length)
                                            // order column out of range?
            {
                this.orderBy = BOListConstants.LST_DEFAULTORDERING; // take default column
                this.orderHow = BOConstants.ORDER_ASC;  // take default kind of ordering
            } // if order column out of range

            // store ordering:
            (this.getUserInfo ()).orderBy = this.orderBy;
            (this.getUserInfo ()).orderHow = this.orderHow;
            // get max elements
            if ((num = this.env.getIntParam (BOArguments.ARG_MAXRESULT)) !=
                IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
            {
                this.maxElements = num;
            } // if
        } // if

        multiStr = this.env.getMultipleParam (BOArguments.ARG_DELLIST);

        this.framesetPossible = true;

        return multiStr;
    } // getSelectedElements


    /**************************************************************************
     * Show the content of the Container, i.e. its elements. <BR/>
     * The objects are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showDeleteForm (int representationForm)
    {
        this.showSelectionForm (representationForm, AppFunctions.FCT_LISTDELETEFORM,
                           Operations.OP_DELETE, AppFunctions.FCT_LISTDELETE,
                           MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SELHEADERDELETE, env));
    } // showDeleteForm


    /**************************************************************************
     * Show the content of the Container, i.e. its elements for delete selection. <BR/>
     * The objects are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   function            function that overwrites the standard list
     *                              show function
     */
    public void showDeleteSelForm (int representationForm, int function)
    {
        this.showSelectionForm (representationForm, function,
                           Operations.OP_DELETE, AppFunctions.FCT_LISTDELETE,
                           MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SELHEADERDELETE, env));
    } // showDeleteSelForm


    /**************************************************************************
     * Show the content of the Container, i.e. its elements for copy selection. <BR/>
     * The objects are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showCopySelForm (int representationForm)
    {
        // no links may be selected for multiple copy ->
        // exclude them from the retrive-query
        this.retrieveLinks = false;

        this.showSelectionForm (representationForm, AppFunctions.FCT_LISTCOPYFORM,
                           Operations.OP_VIEW, AppFunctions.FCT_LISTCOPY,
                           MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SELHEADERCOPY, env));

        this.retrieveLinks = true;
    } // showCopySelForm


    /**************************************************************************
     * Show the content of the Container, i.e. its elements for cut selection. <BR/>
     * The objects are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showCutSelForm (int representationForm)
    {
        // no links may be selected for multiple cut ->
        // exclude them from the retrive-query
        this.retrieveLinks = false;

        this.showSelectionForm (representationForm, AppFunctions.FCT_LISTCUTFORM,
                           Operations.OP_DELELEM, AppFunctions.FCT_LISTCUT,
                           MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SELHEADERCUT, env));

        this.retrieveLinks = true;
    } // showCopySelForm


    /**************************************************************************
     * Show the content of the Container, i.e. its elements for a forward-object
     * selection. <BR/>
     *
     * @param   representationForm  unused parameter - don't mind!
     */
    public void showForwardSelForm (int representationForm)
    {
        // no links may be selected for multiple copy ->
        // exclude them from the retrive-query
        this.retrieveLinks = false;

        this.showSelectionForm (representationForm, AppFunctions.FCT_LISTFORWARDFORM,
                           Operations.OP_VIEW, AppFunctions.FCT_LISTFORWARD,
                           MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUTTONFORWARD, env));

        this.retrieveLinks = true;
    } // showForwardSelForm


    /**************************************************************************
     * Copies all legal elements for this container from selectedElements
     * to selectedElementsClean. <BR/>
     *
     * @param   selectedElements        original list.
     *
     * @return  selectedElementsClean   cleaned list.
     */
    private Vector<OID> removeIllegalElements (Vector<OID> selectedElements)
    {
        Vector<OID> selectedElementsClean = new Vector<OID> (10, 10);
                                        // array of cleaned elementlist
        String typeString;              // the typeString of an Element
        int selectedElemLength;         // number of original elements
        int lengthOfTypeIds = 0;        // number of types allowed
        String[] typeIds = this.getTypeIds (); // ids of all allowed types

        selectedElemLength = selectedElements.size ();
        if (typeIds != null)            // types allowed?
        {
            lengthOfTypeIds = typeIds.length; // get the number of types
        } // if

        // run through all elements and get the typeString of them
        for (int j = 0; j < selectedElemLength; j++)
        {
            // get the typeString of the element
            typeString = Integer
                .toString (selectedElements.elementAt (j).tVersionId);
            for (int i = 0; i < lengthOfTypeIds; i++)
            {
                if (typeIds[i].equalsIgnoreCase (typeString))
                                        // the type was found?
                {
                    // add the element to output list
                    selectedElementsClean.addElement (selectedElements
                        .elementAt (j));
                } // if the type was found
            } // for i
        } // for j

        return selectedElementsClean;
    } // removeIllegalElements


    /**************************************************************************
     * Show the elements that may be pasted into the Container.
     * The objects are gotten from the memory and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   selectedElements    object ids that are marked for paste
     */
    public void showPasteSelForm (int representationForm, Vector<OID> selectedElements)
    {
        this.showSelectionForm (representationForm, AppFunctions.FCT_OBJECTPASTE,
                           Operations.OP_COPY, AppFunctions.FCT_LISTPASTE,
                           MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SELHEADERPASTE, env), selectedElements);
    } // showPasteSelForm


    /**************************************************************************
     * Reduce the list of elements to these elements which are allowed within
     * the actual container object. <BR/>
     * As a side effect this method also reduces the contents of the property
     * {@link #p_selectedObjects p_selectedObjects}.
     *
     * @param   selectedElements    Object ids that shall be checked.
     *
     * @return  A vector containing the reduced elements.
     */
    public Vector<OID> reduceToAllowedElements (Vector<OID> selectedElements)
    {
        // local copy of the selectedElements list. only legal types are left.
        Vector<OID> selectedElementsReduced;

        // call a method which removes all illegal elements
        selectedElementsReduced = this.removeIllegalElements (selectedElements);
        this.p_selectedObjects = this.removeIllegalObjects (this.p_selectedObjects);

        // return the result:
        return selectedElementsReduced;
    } // reduceToAllowedElements


    /**************************************************************************
     * Show the elements that may be added as link to the Container.
     * The objects are gotten from the memory and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   selectedElements    object ids that are marked for paste
     */
    public void showPasteLinkSelForm (int representationForm,
                                      Vector<OID> selectedElements)
    {
        // local copy of the selectedElements list. only legal types are left.
        Vector<OID> selectedElementsClean;

        // remove illegal types:
        // call a method which removes all illegal elements
        selectedElementsClean = this.removeIllegalElements (selectedElements);

        this.showSelectionForm (representationForm,
            AppFunctions.FCT_LISTLINKPASTEFORM, Operations.OP_COPY,
            AppFunctions.FCT_LISTPASTELINK, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SELHEADERPASTELINK, env),
            selectedElementsClean);
    } // showPasteLinkSelForm


    /**************************************************************************
     * Show the elements that may be distributed into the Container.
     * The objects are gotten from the memory and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showDistributeSelForm (int representationForm)
    {
        // no links may be selected for multiple distribute ->
        // exclude them from the retrive-query
        this.retrieveLinks = false;

        this.showSelectionForm (representationForm, AppFunctions.FCT_LISTDISTRIBUTEFORM,
                           Operations.OP_VIEW, AppFunctions.FCT_LISTDISTRIBUTE,
                           MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NOTIFY, env));

        this.retrieveLinks = true;
    } // showDistributeSelForm


    /**************************************************************************
     * Show the elements that may be distributed into the Container.
     * The objects are gotten from the memory and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showExportSelForm (int representationForm)
    {
        this.showSelectionForm (representationForm, AppFunctions.FCT_LISTEXPORTFORM,
                           Operations.OP_VIEW, AppFunctions.FCT_SHOWEXPORTFORM,
                           MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SELHEADEREXPORT, env));
    } // showDistributeSelForm


    /**************************************************************************
     * Show the content of the Container, i.e. its elements. <BR/>
     * The objects are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   function            function that overwrites the standard list
     *                              show function
     * @param   operation           operation to perform during retrieve of data
     * @param   selFunction         function to perform on ok in selection form
     * @param   tokenHeader         token to display in header
     */
    public void showSelectionForm (int representationForm, int function,
                                   int operation, int selFunction, String tokenHeader)
    {
        this.showSelectionForm (representationForm, function, operation, selFunction,
                           tokenHeader, null);
    } // show selectionForm


    /**************************************************************************
     * Show the content of the Container, i.e. its elements. <BR/>
     * The objects are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   function            function that overwrites the standard list
     *                              show function
     * @param   operation           operation to perform during retrieve of data
     * @param   selFunction         function to perform on ok in selection form
     * @param   tokenHeader         token to display in header
     * @param   selectedElements    object ids that are marked for paste
     */
    public void showSelectionForm (int representationForm, int function,
                                   int operation, int selFunction,
                                   String tokenHeader, Vector<OID> selectedElements)
    {
        // set the function for the selection list:
        this.fct = function;

        if (true)                       // container object resists on this
                                        // server?
        {
            try
            {
                // set headings and orderings:
                if (selectedElements == null) // no elements preselected?
                {
                    this.setHeadingsAndOrderings ();
                } // if no elements preselected
                else                    // elements already selected
                {
                    this.setSelectionHeadingsAndOrderings ();
                } // else elements already selected

/* KR 020125: not necessary because already done before
                // try to retrieve the container:
                retrieve (Operations.OP_VIEWELEMS);
*/
//
// HP: Multiple Forward
// The whole multiple-selection concept sucks. It is impossible to build new
// selection-lists with different queries! Review + redesign necessary!
//
                // try to retrieve the content of this container:
                if (function == AppFunctions.FCT_LISTFORWARDFORM)
                {
                    this.performRetrieveForwardContentData (operation, this.orderBy,
                        this.orderHow, selectedElements);
                } // if
                else                    // other selection operation than forward
                {
                    this.retrieveSelectionContentData (operation, this.orderBy,
                        this.orderHow, selectedElements);
                    // add the virtual objects
                    if (this.p_selectedObjects != null)
                    {
                        int vectorSize = this.p_selectedObjects.size ();
                        ContainerElement containerElement = null;
                        BusinessObject virtualObject = null;
                        // loop through the virtual objects vector
                        for (int i = 0; i < vectorSize; i++)
                        {
                            virtualObject = this.p_selectedObjects.elementAt (i);
                            containerElement = new ContainerElement ();
                            // copy the data from the virtual object into the
                            // container element
                            this.getVirtualContainerElementData (virtualObject,
                                    containerElement);
                            this.elements.addElement (containerElement);
                        } // for (int i = 0; i < vectorSize; i++)
                    } // if (this.p_selectedObjects != null)
                } // other selection operation than forward

                // show the container's content:
                // show the content:
                this.performShowSelectionContent (representationForm,
                    this.orderBy, this.orderHow, selFunction, tokenHeader);

                // to show a frameset is now possible again
                this.framesetPossible = true;

            } // try
            catch (NoAccessException e) // no access to objects allowed
            {
                // send message to the user:
                this.showNoAccessMessage (Operations.OP_VIEWELEMS);
            } // catch
/* KR 020125: not necessary because already done before
            catch (AlreadyDeletedException e) // no access to objects allowed
            {
                // send message to the user:
                showAlreadyDeletedMessage ();
            } // catch
*/
        } // if container object resists on this server
        else                            // object resists on another server
        {
          // invoke the object on the other server
        } // else object resists on another server
    } // showSelectionForm


    /**************************************************************************
     * Show all elements wich are filtered in createQueryRetrieveSelectionData. <BR/>
     * The objects are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showSelectionForm (int representationForm)
    {
        if (true)                       // container object resists on this
                                        // server?
        {
            try
            {
                // set headings and orderings:
                this.setHeadingsAndOrderings ();

                // try to retrieve old content
                this.performRetrieveContentData (Operations.OP_VIEW , this.orderBy, this.orderHow);

                // copy oid`s from contentobjects in stringarray
                String[] checkedObjects = new String [this.elements.size ()];
                ContainerElement cE = new ContainerElement ();

                for (int i = 0; i < checkedObjects.length; i++)
                {
                    cE = this.elements.elementAt (i);
                    checkedObjects [i] = new String (cE.oid.toString ());
                } // for i
/* KR 020125: not necessary because already done before
                // try to retrieve the container:
                retrieve (Operations.OP_VIEWELEMS);
*/
                // try to retrieve the content of this container:
                this.performRetrieveSelectionData (Operations.OP_VIEW, this.orderBy, this.orderHow);  // !!!!
                // show the container's content:
                // show the content:
                this.performShowSelectionContent (representationForm,
                    this.orderBy, this.orderHow,
                    AppFunctions.FCT_HANDLESELECTEDELEMENTS, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SELHEADERSELECT, env), checkedObjects);
            } // try
            catch (NoAccessException e) // no access to objects allowed
            {
                // send message to the user:
                this.showNoAccessMessage (Operations.OP_VIEWELEMS);
            } // catch
/* KR 020125: not necessary because already done before
            catch (AlreadyDeletedException e) // no access to objects allowed
            {
                // send message to the user:
                showAlreadyDeletedMessage ();
            } // catch
*/
        } // if container object resists on this server
        else                            // object resists on another server
        {
          // invoke the object on the other server
        } // else object resists on another server
    } // showSelectionForm



    /**************************************************************************
     * Represent list content with its properties to the user within a change
     * form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showListChangeForm (int representationForm)
    {
        // always show as frameset:
        this.showListChangeFormAsFrameset = true;
        this.frm1Size = "*";
        this.frm2Size = "0";
        
        if (this.showListChangeFormAsFrameset && this.framesetPossible)
                                        // show as frameset?
        {
            // create a frameset to show the actual view within:
            if (this.frm1Function != AppFunctions.FCT_NOFUNCTION)
            {
                this.showFrameset (representationForm, this.frm1Function);
            } // if
            else
            {
                this.showFrameset (representationForm, AppFunctions.FCT_LISTCHANGEFORM);
            } // else
        } // if show as frameset
        else                        // don't show as frameset
        {
            try
            {
                // set headings and orderings:
                this.setHeadingsAndOrderings ();
/* KR 020125: not necessary because already done before
                // try to retrieve the container:
                retrieve (Operations.OP_VIEWELEMS);
*/
                // try to retrieve the content of this container:
                this.retrieveContent (Operations.OP_VIEW | Operations.OP_CHANGE, this.orderBy, this.orderHow);
                // show the changeForm
                this.performShowListChangeForm (representationForm);
                // frameset is possible again
                this.framesetPossible = true;
            } // try
            catch (NoAccessException e) // no access to objects allowed
            {
                // send message to the user:
                this.showNoAccessMessage (Operations.OP_VIEWELEMS);
            } // catch
/* KR 020125: not necessary because already done before
            catch (AlreadyDeletedException e) // no access to objects allowed
            {
                // send message to the user:
                showAlreadyDeletedMessage ();
            } // catch
*/
        } // else
    } // showListChangeForm


    /**************************************************************************
     * Represent list content with its properties to the user within a change
     * form. <BR/>
     * <B>THIS METHOD IS A DUMMY WHICH MUST BE OVERWRITTEN IN SUB CLASSES!</B>
     *
     * @param   representationForm  Kind of representation.
     */
    protected void performShowListChangeForm (int representationForm)
    {
/*
         for an example, take a look in the RightsContainer ...
*/
    } // performShowListChangeForm


    /**************************************************************************
     * listChange changes the container contents attributes
     * change. <BR/>
     *
     * @param   representationForm  ???
     */
    public void listChange (int representationForm)
    {
        try
        {
            // change attributes of objects in list
            this.performListChange (Operations.OP_CHANGE);
        } // try
        catch (NoAccessException e) // no access to objects allowed
        {
            // send message to the user:
            this.showNoAccessMessage (Operations.OP_VIEWELEMS);
        } // catch
        this.showContent (representationForm);
    } // listChange


    /**************************************************************************
     * change attributes of objects in the containers content
     * <B>THIS METHOD IS A DUMMY WHICH MUST BE OVERWRITTEN IN SUB CLASSES!</B>
     *
     * @param   operation   The operation code.
     *
     * @throws  NoAccessException
     *          The user does not have the required permissions.
     */
    public void performListChange (int operation)
        throws NoAccessException
    {
        // this method may be overwritten in subclasses
    } // performListChange


    /**************************************************************************
     * Get the Container's content out of the database. <BR/>
     * This method checks if the objects were already loaded into memory. In
     * this case it checks if the user's rights are sufficient to perform the
     * requested operation on the objects. If this is all right the objects are
     * returned otherwise an exception is raised. <BR/>
     * If the objects are not already in the memory they must be loaded from the
     * database. In this case there is also a rights check done. If this is all
     * right the objects are returned otherwise an exception is raised. <BR/>
     *
     * @param   operation         Operation to be performed with the objects.
     * @param   orderBy           Property, by which the result shall be
     *                            sorted. If this parameter is null the
     *                            default order is by name.
     * @param   orderHow          Kind of ordering: BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                            null => BOConstants.ORDER_ASC
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected void retrieveSelectionContentData (int operation,
                                                 int orderBy,
                                                 String orderHow)
        throws NoAccessException
    {
        this.retrieveSelectionContentData (operation, orderBy, orderHow, null);
    } // retrieveSelectionContentData


    /**************************************************************************
     * Get the Container's content out of the database. <BR/>
     * This method checks if the objects were already loaded into memory. In
     * this case it checks if the user's rights are sufficient to perform the
     * requested operation on the objects. If this is all right the objects are
     * returned otherwise an exception is raised. <BR/>
     * If the objects are not already in the memory they must be loaded from the
     * database. In this case there is also a rights check done. If this is all
     * right the objects are returned otherwise an exception is raised. <BR/>
     *
     * @param   operation         Operation to be performed with the objects.
     * @param   orderBy           Property, by which the result shall be
     *                            sorted. If this parameter is null the
     *                            default order is by name.
     * @param   orderHow          Kind of ordering: BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                            null => BOConstants.ORDER_ASC
     * @param   selectedElements  object ids that are marked for paste
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected void retrieveSelectionContentData (int operation, int orderBy,
                                                 String orderHow,
                                                 Vector<OID> selectedElements)
        throws NoAccessException
    {
        if (this.isActualContent ())    // content already in memory?
        {
            // check user's rights:
            if (!this.user.hasRights (this.oid, operation)) // access not allowed?
            {
                // raise no access exception
                NoAccessException error = new NoAccessException (
                    MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
                        UtilExceptions.ML_E_NOACCESSEXCEPTION, env));
                throw error;
            } // if access not allowed
        } // if content already in memory
        else                            // content not in memory
        {
            // call the object type specific method:

            this.performRetrieveContentData (operation, orderBy, orderHow, selectedElements);
            this.setActualContent ();   // the content within the object is
                                        // actual
        } // else content not in memory
    } // retrieveSelectionContentData


    /**************************************************************************
     * Represent the content of the Container, i.e. its elements, to the user.
     * <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   orderBy             Property, by which the result is
     *                              sorted.
     * @param   orderHow            Kind of ordering: BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                              null => BOConstants.ORDER_ASC
     */
    protected void performShowDeleteContent (int representationForm,
                                     int orderBy, String orderHow)
    {
        this.performShowSelectionContent (representationForm,
                                     orderBy, orderHow,
                                     AppFunctions.FCT_LISTDELETE,
                                     MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SELHEADERDELETE, env));
    } // performShowDeleteContent


    /**************************************************************************
     * Represent the content of the Container, i.e. its elements, to the user.
     * <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   orderBy             Property, by which the result is
     *                              sorted.
     * @param   orderHow            Kind of ordering: BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                              null => BOConstants.ORDER_ASC
     * @param   function            The function which is used for list
     *                              operations.
     * @param   selectionlistHeader Header to be shown on the dialog
     */
    protected void performShowSelectionContent (int representationForm,
                                     int orderBy, String orderHow,
                                     int function, String selectionlistHeader)
    {
        this.performShowSelectionContent (representationForm,
                                     orderBy, orderHow,
                                     function, selectionlistHeader, null);
    } // performShowSelectionContent



    /**************************************************************************
     * Represent the content of the Container, i.e. its elements, to the user.
     * <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   orderBy             Property, by which the result is
     *                              sorted.
     * @param   orderHow            Kind of ordering: BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                              null => BOConstants.ORDER_ASC
     * @param   function            The function which is used for list
     *                              operations.
     * @param   selectionlistHeader Header to be shown on the dialog
     * @param   checkedObjects      Oids of objects where the checkbox shall be checked
     */
    protected void performShowSelectionContent (int representationForm,
                                     int orderBy, String orderHow,
                                     int function, String selectionlistHeader,
                                     String[] checkedObjects)
    {
//debug (" performShowSelectionContent ");

        int i;                          // actual index
        int size = this.elements.size (); // number of elements within this
                                        // container

        // start with the container representation: show header

        Page page = new Page ("List", false); // the output page

//debug ("AJ 1");

        // Stylesheetfile wird geladen
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () + "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETLIST].styleSheet;
        page.head.addElement (style);

//debug ("AJ 2");
        // set the icon of this object:
        this.setIcon ();

//debug ("AJ 3");

        GroupElement list = new GroupElement ();

        FormElement form = this.createFormHeader (page, this.name,
            this.getNavItems (), null, selectionlistHeader, null, this.icon,
            this.containerName, size);

        // start with the object representation: show header
        form.addElement (new InputElement (BOArguments.ARG_OID, InputElement.INP_HIDDEN, "" + this.oid));
        form.addElement (new InputElement (BOArguments.ARG_FUNCTION, InputElement.INP_HIDDEN, "" + function));

        TextElement text = new TextElement ("text");

//debug ("AJ 4 size =" + size);

        if (size > 0)               // there are some elements?
        {
            TableElement table;     // table containing the list
            RowElement tr;          // actual table row

            // set number of colums in result list:
            int columns = this.headings.length + 1;

/* ******* */
/* HACK!!! */
/* ******* */
            // set alignments:
            String[] alignments = new String[columns - 1];
            alignments[0] = "" + IOConstants.ALIGN_RIGHT + "\">" +
                "<COL CLASS=\"" + CssConstants.CLASS_NAME + "\"" + " ALIGN=\"" +
                IOConstants.ALIGN_LEFT;
            // set alignments for all columns:
            for (i = 1; i < columns - 1; i++)
            {
                alignments[i] = null;
            } // for i
/* ************ */
/* HACK ENDE!!! */
/* ************ */


//debug ("AJ 5");
            // create table definition for list:
            table = new TableElement (columns);

            table.classId = CssConstants.CLASS_LIST;
            table.frametypes = IOConstants.FRAME_VOID;
            table.width = HtmlConstants.TAV_FULLWIDTH;
            table.alignment = alignments;

//debug ("AJ 6");
            // create header
            tr = this.createHeading (this.headings,
                                this.orderings, orderBy, orderHow);
//debug ("AJ 7");

            // add header to table:
            table.addElement (tr, true);

            ContainerElement element;
//debug ("AJ 8");
            for (i = 0; i < size; i++)
            {
                // get the actual element and represent it to the user:
                element = this.elements.elementAt (i);
                element.showExtendedAttributes = this.p_showExtendedAttributes;

                if (checkedObjects != null)
                {
                    element.checked = StringHelpers.findString (checkedObjects,
                                                     "" + element.oid) != -1;
                } // if

//debug ("AJ 9");
                table.addElement (element
                    .showSelection (BOListConstants.LST_CLASSROWS[i %
                        BOListConstants.LST_CLASSROWS.length], env));
//debug ("AJ 10" + i);
            } // for i

            // finish the container representation: show footer
            list.addElement (table);

        } // if there are some elements
        else                        // there are no elements
        {
            // show the according message to the user:
            TableElement table = new TableElement ();
            table.width = HtmlConstants.TAV_FULLWIDTH;
            RowElement tr = new RowElement (2);
            text = new TextElement (MultilingualTextProvider.getMessage(this.msgBundleContainerEmpty,
                    this.msgContainerEmpty, new String[] {this.name}, env));
            TableDataElement td = new TableDataElement (text);
            td.classId = CssConstants.CLASS_BODY;
            tr.addElement (td);
            table.addElement (tr);

            if (this.getUserInfo ().userProfile.showRef)
            {
                this.showRefs (table, page);
                list.addElement (table);
            } // if
        } // else there are no elements
        // add generated list of selectable items to form
        form.addElement (list);

        if (size > 0)
        {
            // add the mark buttons
            TableElement markTable = new TableElement ();
            markTable.border = 0;
            markTable.width = HtmlConstants.TAV_FULLWIDTH;
            RowElement tr = new RowElement (1);
            GroupElement gel = new GroupElement ();
            LineElement line = new LineElement ();
            line.width = "95%";
            gel.addElement (line);
            gel.addElement (this.createMarkButtons (BOArguments.ARG_DELLIST));
            TableDataElement td = new TableDataElement (gel);
            td.alignment = IOConstants.ALIGN_CENTER;
            td.valign = IOConstants.ALIGN_BOTTOM;
            tr.addElement (td);
            markTable.addElement (tr);
            form.addElement (markTable);
        } // if


        // add form footer to form
        this.createFormFooter (form);

        // create the script to be executed on client:
        if (this.p_isShowCommonScript)
        {
            page.body.addElement (this.getCommonScript (true));
        } // if

//debug ("AJ 11");

        ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);

        // show message if there are too many entries in the list:
        if (this.areMaxElementsExceeded)
        {
            // add alert message to script:
            script.addScript ("alert (\"" +
                MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
                    this.msgTooMuchElements, new String[] {"" + this.maxElements}, env) +
                "\\n\\n" +
                MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
                    this.msgDisplayableElements, new String[] {"" + this.maxElements}, env) +
                "\");\n");

            page.body.addElement (script);
        } // if
//debug ("AJ 12");

        String deactivateTabs = "top.setNoActiveTab ()";
        script.addScript (deactivateTabs);
        page.body.addElement (script);

        // build the page and show it to the user:
        try
        {
            // try to bulid the page
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            // show according message to the user
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // performShowSelectionContent


    /**************************************************************************
     * Ensure that the private property elementClass is set. <BR/>
     * This method tries to set the value of property elementClass to a value
     * according to {@link #elementClassName elementClassName}. <BR/>
     * If this is not possible the class is set to the default value
     * "ibs.bo.ContainerElement".
     *
     * @return  The class object for the element.
     *          <CODE>null</CODE> if there is no valid class.
     */
    @SuppressWarnings ("unchecked") // suppress compiler warning
    protected Class<? extends ContainerElement> getElementClass ()
    {
        // get the class of one element of this container:
        if (this.elementClass == null)  // class not set yet?
        {
            try
            {
                // get the element class:
                this.elementClass = (Class<? extends ContainerElement>) Class
                    .forName (this.elementClassName);
            } // try
            catch (ClassNotFoundException e)
            {
                try
                {
                    // get default element class:
                    this.elementClass = (Class<? extends ContainerElement>) Class
                        .forName ("ibs.bo.ContainerElement");
                } // try
                catch (ClassNotFoundException e1)
                {
                    // should not occur
                    IOHelpers.showMessage (e, this.app, this.sess, this.env,
                        true);
                } // catch
            } // catch
        } // if class not set yet

        // return the class object:
        return this.elementClass;
    } // getElementClass


    /**************************************************************************
     * Get the Container's content out of the database. <BR/>
     * <B>THIS METHOD IS A DUMMY WHICH MUST BE OVERWRITTEN IN SUB CLASSES!</B>
     * <BR/>
     * First this method tries to load the objects from the database. During
     * this operation a rights check is done, too. If this is all right the
     * objects are returned otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the objects.
     * @param   orderBy     Property, by which the result shall be
     *                      sorted. If this parameter is null the
     *                      default order is by name.
     * @param   orderHow    Kind of ordering: BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                      null => BOConstants.ORDER_ASC
     *
     * @exception   NoAccessException
     *              The user does not have access to these objects to perform
     *              the required operation.
     */
    protected void performRetrieveDeleteContentData (int operation,
                                                     int orderBy,
                                                     String orderHow)
        throws NoAccessException
    {
        String orderHowLocal = orderHow; // variable for local assignments
        Class<? extends ContainerElement> elementClass = null;
        SQLAction action = null;        // the action object used to access the
                                        // database
        ContainerElement obj;
        int rowCount;                   // row counter
        int i;                          // index of actual tuple
        this.size = 0;
        StringBuffer queryStr = new StringBuffer (); // the query
        String queryPart = null;        // a part of the query

        // ensure a correct ordering:
        if (!orderHowLocal.equalsIgnoreCase (BOConstants.ORDER_DESC)) // not descending?
        {
            orderHowLocal = BOConstants.ORDER_ASC;       // order ascending
        } // if not descending

        // empty the elements vector:
        this.elements.removeAllElements ();

        // get the query string:
        queryPart = this.createQueryRetrieveContentData ();
        // check if the string is null:
        if (queryPart == null)
        {
            // no query string constructed: that means abort the db operation
            return;
        } // if (query == null)

        // get the elements out of the database:
        // create the SQL String to select all tuples
        queryStr
            .append (queryPart)
            .append (SQLConstants.SQL_AND)
            .append (SQLHelpers.getBitAnd ("flags", "16")).append ("<> 16 ")
            .append (" AND userId = ").append (this.user.id)
            .append (SQLHelpers.getStringCheckRights (operation))
            .append ("ORDER BY ").append (this.orderings[orderBy])
            .append (" ").append (orderHowLocal);


//debug ("performRetrieveDeleteContentData: " + queryStr);
        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();

            rowCount = action.execute (queryStr, false);

            // empty result set?
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

            // get the class of one element of this container:
            elementClass = this.getElementClass ();

            i = 0;                      // initialize loop counter

            while ((this.maxElements == 0 || i++ < this.maxElements) &&
                                        // maximum number of tuples not reached?
                   !action.getEOF ())   // there are tuples left?
            {
                // create a new object:
                try
                {
                    // create an instance of the element's class:
                    obj = elementClass.newInstance ();

                    obj.oid = SQLHelpers.getQuOidValue (action, "oid");
                    // get specific data of container element:
                    this.getContainerElementData (action, obj);
                    // add element to list of elements:
                    this.elements.addElement (obj);
                } // try
                catch (IllegalAccessException e)
                {
                    // should not occur
                    IOHelpers.showMessage (e, this.app, this.sess, this.env,
                        true);
                } // catch
                catch (InstantiationException e)
                {
                    // should not occur
                    IOHelpers.showMessage (e, this.app, this.sess, this.env,
                        true);
                } // catch
                this.size++;

                // step one tuple ahead for the next loop:
                action.next ();
            } // while

            // check if there are more tuples left
            if (!action.getEOF ())
            {
                // indicate that maximum number of elements have been exceeded
                this.areMaxElementsExceeded = true;
            } // if

            // fetch done
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
    } // performRetrieveDeleteContentData



    /**************************************************************************
     * Show the content of the Container, i.e. its elements. <BR/>
     * The objects are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showContent (int representationForm)
    {
        if (true)                       // container object resists on this
                                        // server?
        {
            if (this.showContentAsFrameset && this.framesetPossible)
                                        // show as frameset?
            {
                // create a frameset to show the actual view within:
                if (this.frm1Function != AppFunctions.FCT_NOFUNCTION)
                {
                    this.showFrameset (representationForm, this.frm1Function);
                } // if
                else
                {
                    this.showFrameset (representationForm, AppFunctions.FCT_SHOWOBJECTCONTENT);
                } // else
            } // if show as frameset
            else                        // don't show as frameset
            {
                try
                {
                    // remember actual function:
                    this.fct = AppFunctions.FCT_LISTPERFORM;

                    // set headings and orderings:
                    this.setHeadingsAndOrderings ();

/* KR 020125: not necessary because already done before
                    // try to retrieve the container:
                    retrieve (Operations.OP_VIEWELEMS);
*/
                    if (this.getUserInfo ().userProfile.showRef)
                    {
                        this.performRetrieveRefs (Operations.OP_VIEW);
                    } // if

                    // try to retrieve the content of this container:
                    this.retrieveContent (Operations.OP_VIEW, this.orderBy, this.orderHow);
                    // show the container's content:
                    this.performShowContent (representationForm, this.orderBy, this.orderHow);
                } // try
                catch (NoAccessException e) // no access to objects allowed
                {
                    // send message to the user:
                    this.showNoAccessMessage (Operations.OP_VIEWELEMS);
                } // catch
/* KR 020125: not necessary because already done before
                catch (AlreadyDeletedException e) // no access to objects allowed
                {
                    // send message to the user:
                    showAlreadyDeletedMessage ();
                } // catch
*/
                finally
                {
                    // empty the elements vector, necessesary because of OutOfMemory-
                    // problem if too much ValueDataElements are cached with object
                    // in pool
                    if (this.elements != null)
                    {
                        this.elements.removeAllElements ();
                    } // if
                } // finally
//IOHelpers.printTrace ("after try");

                this.framesetPossible = true; // frameset view is possible again

                if (this.sess.weblink)
                {
                    this.sess.weblink = false; // now weblink is false
                } // if
            } // else don't show as frameset
        } // if container object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server
    } // showContent


    /**************************************************************************
     * Create the heading of a list method for IE4.0. <BR/>
     * The property <A HREF="#orderings">orderings</A> is queried to determine
     * if a column shall be orderable or not. If orderings[i] == null the column
     * i is not orderable.
     *
     * @param   headings    Headings to be shown.
     * @param   orderings   The attribute names for the several orderings.
     * @param   orderBy     Property, by which the result is sorted.
     * @param   orderHow    Kind of ordering: BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                      <CODE>null</CODE> => BOConstants.ORDER_ASC
     *
     * @return  Table row containing the heading of the list.
     */
    protected RowElement createHeading (String[] headings, String[] orderings,
                                        int orderBy, String orderHow)
    {
        return this.createHeading (headings, orderings, orderBy, orderHow, true);
    } // createHeading


    /**************************************************************************
     * Create the heading of a list. <BR/>
     * The property <A HREF="#orderings">orderings</A> is queried to determine
     * if a column shall be orderable or not. If orderings[i] == null the column
     * i is not orderable.
     *
     * @param   headings            Headings to be shown.
     * @param   orderings           The attribute names for the several orderings.
     * @param   orderBy             Property, by which the result is
     *                              sorted.
     * @param   orderHow            Kind of ordering: BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                              <CODE>null</CODE> => BOConstants.ORDER_ASC
     * @param   isOrderingsChangeable Shall the orderings be changeable?
     *
     * @return  Table row containing the heading of the list.
     */
    protected RowElement createHeading (String [] headings,
        String [] orderings, int orderBy, String orderHow,
        boolean isOrderingsChangeable)
    {
        RowElement tr;                  // the heading row
        TableDataElement td;            // a column of the heading
        String kind;                    // kind of ordering
        int i;                          // counter
        TextElement text;               // text of column header
        Element link;                   // link column header
        ImageElement orderingImage;     // image representing the ordering
        GroupElement group;             // group element within table cell
        int columns = headings.length + 1; // number of columns


        // create the new heading row:
        tr = new RowElement (columns);
        tr.classId = CssConstants.CLASS_LISTHEADER;

        td = new TableDataElement (new TextElement (IE302.HCH_NBSP));
        td.classId = CssConstants.CLASS_LISTHEADER;
        tr.addElement (td);

        // define other columns:
        for (i = 0; i < headings.length; i++)
        {
            kind = BOConstants.ORDER_ASC;
            if (orderBy == i && orderHow.equalsIgnoreCase (BOConstants.ORDER_ASC))
            {
                kind = BOConstants.ORDER_DESC;
            } // if
            text = new TextElement (headings[i]);

            if (this.orderings[i] == null || !isOrderingsChangeable)
                                        // don't order by this column?
            {
                link = text;            // just take the text to form the header
            } // if don't order by this column
            else                        // order by this column
            {
                // form the header as link with the text:
                link = new LinkElement (text, this.getBaseUrlGet () +
                    HttpArguments
                        .createArg (BOArguments.ARG_FUNCTION, this.fct) +
                    HttpArguments
                        .createArg (BOArguments.ARG_OID, "" + this.oid) +
                    HttpArguments.createArg (BOArguments.ARG_ORDERBY, i) +
                    HttpArguments.createArg (BOArguments.ARG_ORDERHOW, kind) +
                    HttpArguments.createArg (BOArguments.ARG_REORDER, 1) +
                    HttpArguments.createArg (BOArguments.ARG_ISFRAMESET,
                        this.showContentAsFrameset));
            } // else order by this column

            link.classId = CssConstants.CLASS_LISTHEADER;
            group = new GroupElement ();
            group.addElement (link);

            // check if the actual column is the ordering column and if the
            // ordering through this column is allowed:
            if (orderBy == i && this.orderings[i] != null && isOrderingsChangeable)
                                        // order by this column
            {
                // set ordering image an add it to the actual group element:
                orderingImage = new ImageElement (this.sess.activeLayout.path + BOPathConstants.PATH_GLOBAL + BOConstants.IMG_ORDERASC);
                if (orderHow.equalsIgnoreCase (BOConstants.ORDER_DESC))
                {
                    orderingImage.source = this.sess.activeLayout.path + BOPathConstants.PATH_GLOBAL + BOConstants.IMG_ORDERDESC;
                } // if
                group.addElement (orderingImage);
            } // if order by this column
            else                        // order by other column
            {
                // nothing to do
            } // else order by other column

            td = new TableDataElement (group);
            td.classId = CssConstants.CLASS_LISTHEADER;
            tr.addElement (td);
        } // for i

        return tr;                      // return the computed heading row
    } // createHeading


    /**************************************************************************
     * Creates the description of a list. <BR/>
     *
     * @return  Group element containing the graphical representation of the
     *          description.
     */
    protected GroupElement createDescription ()
    {
        TableElement table = null;      // table containing the description
        RowElement tr;                  // actual table row
        TableDataElement td;            // actual element of a row
        GroupElement group = new GroupElement ();

        if (this.description != null &&
            this.description.length () > 0) // description exists?
        {
            table = new TableElement (1); // create table
            table.width = HtmlConstants.TAV_FULLWIDTH;       // set width of table to full frame size
            table.border = 0;
            table.classId = CssConstants.CLASS_LISTDESCRIPTION;
            tr = new RowElement (1);    // create table row
            tr.classId = CssConstants.CLASS_LISTDESCRIPTION;
            td = new TableDataElement (IOHelpers.getTextField (this.description));
            td.classId = CssConstants.CLASS_LISTDESCRIPTION;

            // add text to table and add table to body of document:
            tr.addElement (td);
            table.addElement (tr);
            group.addElement (table);
        } // if description exists

        return group;                   // return the constructed group element
    } // createDescription


    /**************************************************************************
     * Represent the content of the Container, i.e. its elements, to the user.
     * <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   orderBy             Property, by which the result is
     *                              sorted.
     * @param   orderHow            Kind of ordering: BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                              null => BOConstants.ORDER_ASC
     */
    public void performShowContent (int representationForm,
                                    int orderBy, String orderHow)
    {
        int i;                          // actual index
        int size = this.elements.size (); // number of elements within this
                                        // container
        Page page = new Page ("List", false); // the output page

        // Stylesheetfile wird geladen
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () + "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETLIST].styleSheet;
        page.head.addElement (style);

        // start with the container representation: show header

        // check if columns shall be reduced
        // according to userprofile settings
        this.setHeadingsAndOrderings ();

        // set the icon of this object:
        this.setIcon ();

        GroupElement body;

        if (!this.isSingleSelectionContainer)
        {
            // set the name of the object as multilang name for fallback
            String mlName = this.name;
            // get the external key of this object 
            ExternalKey objExtKey = BOHelpers.getExtKeyByOid (this.oid, env);
            
            // do we have an extKey for this object
            if (objExtKey != null)
            {
                // try to get a translation for the objects name
                mlName = MultilingualTextProvider.getMultilangObjectName (
                    objExtKey.getId (), objExtKey.getDomain (), mlName, env);
            } // if
            
            // Container is no Selectioncontainer
            if (this.isTab ())
            {
                // set the name of the container object as multilang name for fallback
                String containerMlName = this.containerName;
                // get the external key of this container object 
                ExternalKey objContExtKey = BOHelpers.getExtKeyByOid (this.containerId, env);
                
                // do we have an extKey for this container object
                if (objContExtKey != null)
                {
                    // try to get a translation for the container objects name
                    containerMlName = MultilingualTextProvider.getMultilangObjectName (
                        objContExtKey.getId (), objContExtKey.getDomain (), containerMlName, env);
                } // if

                body = this.createHeader (page,
                    MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    this.headingName, new String[] {mlName}, this.env),
                    this.getNavItems (), containerMlName, this.icon, size);
            } // if
            else
            {
                body = this.createHeader (page,
                    MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    this.headingName, new String[] {mlName}, this.env),
                    this.getNavItems (), null, this.icon, size);
            } // else
        } // if !isSingleSelectionContainer
        else
        {

            // Container is Selectioncontainer - set special Header
            body = this.createHeader (page, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SINGLESELHEADER, env), this
                .getNavItems (), null, null, this.icon, null, false, -1);
        } // else isSingleSelectionContainer


        // if the container supports layout generation  with
        // XSL style sheed use this content-output otherwise
        // get the default layout from the TableElement.
        String xslOutput = this.showContentXSL ();
        if (xslOutput != null)
        {
            body.addElement (new TextElement (xslOutput));
        } // if (xslOutput != null)
        else
        {
/* ***************** */
/* EUI HACK (BB) !!! */
/* ***************** */
// we check if an include file exists for this query
// this is done by checking if a file with name "<typename>_content_header.htm"
// and/or name "<typename>_content_footer.htm"
// exists in the include directory
// if yes insert the content of this file as header/footer before/after the table
// with the content
            String htmlString = "";
            String line = null;
            File headerFile = null;
            File footerFile = null;
            String headerFileName = null;
            String footerFileName = null;

            if (this.typeObj != null)
            // check if typeObj = null, because viewtabs do not have an typeObj yet
            // AJ HINT: view tabs should have a typeObj as well
            {
                String includeBasePath = FileHelpers.makeFileNameValid (
                    this.app.p_system.p_m2AbsBasePath +
                    BOPathConstants.PATH_APPINCLUDE);
                headerFileName = includeBasePath + this.typeObj.getCode () +
                    "_content_header.htm";
                footerFileName = includeBasePath + this.typeObj.getCode () +
                    "_content_footer.htm";

                try
                {
                    headerFile = new File (headerFileName);
                    // test if header file exists
                    if (headerFile.exists ())
                    {
                        // read the content of the file and write it into the string
                        BufferedReader headerBufferedReader =
                            new BufferedReader (new FileReader (headerFile));
                        while ((line = headerBufferedReader.readLine ()) != null)
                        {
                            htmlString += line;
                        } // while ((chr = headerFileReader.read ()) != -1)
                        headerBufferedReader.close ();
                        // add the content to the page
                        body.addElement (new TextElement (htmlString));
                    } // if FileHelpers.exists (headerFileName)
                } // try
                catch (IOException e)
                {
                    IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
                } // catch
            } // this.typeObj != null
/* **************** */
/* EUI HACK ENDE!!! */
/* **************** */


            TextElement text = new TextElement ("text");

            // show description of container:
            body.addElement (this.createDescription ());

            if (size > 0)               // there are some elements?
            {
                TableElement table;     // table containing the list
                RowElement tr;          // actual table row

                // set number of colums in result list:
                int columns = this.headings.length + 1;

/* ******* */
/* HACK!!! */
/* ******* */
                // set alignments:
                String[] alignments = new String[columns - 1];
                alignments[0] = "" + IOConstants.ALIGN_RIGHT + "\">" +
                    "<COL CLASS=\"" + CssConstants.CLASS_NAME + "\"" +
                    " ALIGN=\"" + IOConstants.ALIGN_LEFT;
                // set alignments for all columns:
                for (i = 1; i < columns - 1; i++)
                {
                    alignments[i] = null;
                } // for i
/* ************ */
/* HACK ENDE!!! */
/* ************ */

                // create table definition for list:
                table = new TableElement (columns);
                table.classId = CssConstants.CLASS_LIST;
                table.frametypes = IOConstants.FRAME_VOID;
                table.width = HtmlConstants.TAV_FULLWIDTH;
                table.alignment = alignments;

                // define header:
                // no sort-on-attributes will be provided if max number of
                // has been exceeded. reason: only a part of all available
                // element swill be showed in this case.
                if (this.areMaxElementsExceeded)
                {
                    // store orderings in temporary variable:
                    String [] tempOrderings = this.orderings;
                    // create new empty orderings:
                    String [] tempOrderings2 = new String [columns];
                    for (i = 0; i < columns; i++)
                    {
                        tempOrderings2[i] = null;
                    } // for i
                    this.orderings = tempOrderings2;

                    // create header
                    tr = this.createHeading (this.headings, this.orderings, orderBy, orderHow);

                    // restore orderings
                    this.orderings = tempOrderings;

                } // if there are too many elements
                else                    // not too many elements
                {

                    // create header
                    tr = this.createHeading (this.headings, this.orderings, orderBy, orderHow);
                } // else not too many elements

                // add header to table:
                table.addElement (tr, true);
                // loop through all elements of this container and display them.
                ContainerElement element;
                for (i = 0; i < size; i++)
                {
                    // get the actual element and represent it to the user:
                    element = this.elements.elementAt (i);
                    element.showExtendedAttributes = this.getUserInfo ().userProfile.showExtendedAttributes;

                    table.addElement (element.show (BOListConstants.LST_CLASSROWS[i % BOListConstants.LST_CLASSROWS.length], env));

/* **************** */
/* EUI HACK DJ      */
/* **************** */
// this hack is necessary to display more than one row for the TaskResourceContainerElement.
/*
                    if (element.getClass ().getName ().equals ("eui.TaskResourceContainerElement"))
                    {
                        eui.TaskResourceContainerElement element1 = (eui.TaskResourceContainerElement) element;

                        RowElement extraRow1 = element1.show1 (BOListConstants.LST_CLASSROWS[i % BOListConstants.LST_CLASSROWS.length]);
                        if (extraRow1 != null)
                        {
                            table.addElement (extraRow1);
                        } // if

                        RowElement extraRow2 = element1.show2 (BOListConstants.LST_CLASSROWS[i % BOListConstants.LST_CLASSROWS.length]);
                        if (extraRow2 != null)
                        {
                            table.addElement (extraRow2);
                        } // if
                    } // if
*/
/* **************** */
/* EUI HACK ENDE!!! */
/* **************** */
                } // for i

                // finish the container representation: show footer
                body.addElement (table);

                if (this.getUserInfo ().userProfile.showRef)
                {
                    this.showRefs (table, page);
                } // if

            } // if there are some elements
            else                            // there are no elements
            {
                 // show the according message to the user:
                TableElement table = new TableElement ();
                table.width = HtmlConstants.TAV_FULLWIDTH;
                RowElement tr = new RowElement (2);
                text = new TextElement (MultilingualTextProvider.getMessage(this.msgBundleContainerEmpty,
                        this.msgContainerEmpty, new String[] {this.name}, env));
                TableDataElement td = new TableDataElement (text);
                td.classId = CssConstants.CLASS_BODY;
                tr.addElement (td);
                table.addElement (tr);

                if (this.getUserInfo ().userProfile.showRef)
                {
                    this.showRefs (table, page);
                    body.addElement (table);
                } // if
            } // else there are no elements

/* ***************** */
/* EUI HACK (BB) !!! */
/* ***************** */

            if (this.typeObj != null)
            // check if typeObj = null, because view tabs do not have an typeObj yet
            // AJ HINT: view tabs should have a typeObj as well
            {
                // try to add a footer
                try
                {
                    htmlString = "";
                    footerFile = new File (footerFileName);
                    // test if header file exists
                    if (footerFile.exists ())
                    {
                        // read the content of the file and write it into the string
                        BufferedReader footerBufferedReader =
                            new BufferedReader (new FileReader (footerFile));
                        while ((line = footerBufferedReader.readLine ()) != null)
                        {
                            htmlString += line;
                        } // while ((chr = headerFileReader.read ()) != -1)
                        footerBufferedReader.close ();
                        // add the content to the page
                        body.addElement (new TextElement (htmlString));
                    } // if FileHelpers.exists (headerFileName)
                } // try
                catch (IOException e)
                {
                    IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
                } // catch
            } // if typeObj != null
/* **************** */
/* EUI HACK ENDE!!! */
/* **************** */

        } // else no xsl stylesheet exist for content
        if (!this.isSingleSelectionContainer)
        {
            // ensure that the content tab is active:
            if (this.p_tabs != null)        // tabs exist?
            {
                this.p_tabs.setActiveTab (0, TabConstants.TC_CONTENT);
            } // if tabs exist

            // create the script to be executed on client:
            if (this.p_isShowCommonScript)
            {
                page.body.addElement (this.getCommonScript (true));
            } // if

            // show message if there are too many entries in the list:
            if (this.areMaxElementsExceeded)
            {
                ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);

                // add alert message to script:
                script.addScript ("alert (\"" +
                    MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
                        this.msgTooMuchElements, new String[] {"" + this.maxElements}, env) +
                    "\\n\\n" +
                    MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
                        this.msgDisplayableElements, new String[] {"" + this.maxElements}, env) +
                    "\");\n");

                page.body.addElement (script);
            } // if
        } // if !this.isSingleSelectionContainer

        // perform the retrieve/show of specific link fields
        Element specificContent = performShowSpecificContent (representationForm, orderBy, orderHow); 
        // do we have additional specific content to add to the page?
        if (specificContent != null)
        {
            page.body.addElement(specificContent);
        } // if

        // build the page and show it to the user:
        try
        {
            // try to bulid the page
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            // show according message to the user
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // performShowContent


    /**************************************************************************
     * Method to add addtional data after the default data of the page. <BR/>
     * This is a stub method which should be overridden in subclasses. <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   orderBy             Property, by which the result is sorted.
     * @param   orderHow            Kind of ordering:
     *                              BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                              null => BOConstants.ORDER_ASC
     */
    protected Element performShowSpecificContent (int representationForm,
                                                  int orderBy, String orderHow)
    {
        return null;
    } // performShowSpecificContent

    
    /**************************************************************************
     * Generates the HTML code for the content view by using a stylesheet file.
     * Which should be named &lt;typecode>_content.xsl. <BR/>
     *
     * @return      the HTML code or <CODE>null</CODE>
     *              if no stylesheet is defined.
     */
    protected String showContentXSL ()
    {
        return null;
    } // showPropertiesXSL


    /**************************************************************************
     * Show a frameset view of the Container's content. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showContentFrameset (int representationForm)
    {
        if (true)                       // container object resists on this
                                        // server?
        {
            // show the frameset:
            this.performShowContentFrameset (representationForm);
        } // if container object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server
    } // showContentFrameset


    /**************************************************************************
     * Represent the content of the Container to the user within a frameset.
     * <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    protected void performShowContentFrameset (int representationForm)
    {
        // start with the container representation: show frameset
        Page page = new Page (true);    // the output page
        FrameSetElement frameset = new FrameSetElement (this.asRows, 2);

        // define URL for first frame:
        if ((this.frm1Function != AppFunctions.FCT_NOFUNCTION) &&
            (this.frm1Url.equals (AppConstants.FILE_EMPTYPAGE)))
        {
            this.frm1Url = this.getBaseUrlGet () +
                HttpArguments.createArg (BOArguments.ARG_FUNCTION,
                    this.frm1Function) +
                HttpArguments.createArg (BOArguments.ARG_OID, "" + this.oid) +
                HttpArguments.createArg (BOArguments.ARG_SHOWTABBAR,
                    this.displayTabs ? 1 : 0) +
                HttpArguments.createArg (BOArguments.ARG_WEBLINK,
                    this.sess.weblink ? 1 : 0);
        } // if


        // define URL for second frame:
        if ((this.frm2Function != AppFunctions.FCT_NOFUNCTION) &&
            (this.frm2Url.equals (AppConstants.FILE_EMPTYPAGE)))
        {
            this.frm2Url = this.getBaseUrlGet () +
                HttpArguments.createArg (BOArguments.ARG_FUNCTION,
                    this.frm2Function) +
                HttpArguments.createArg (BOArguments.ARG_OID, "" + this.oid) +
                HttpArguments.createArg (BOArguments.ARG_SHOWTABBAR,
                    this.displayTabs ? 1 : 0) +
                HttpArguments.createArg (BOArguments.ARG_WEBLINK,
                    this.sess.weblink ? 1 : 0);
        } // if

        FrameElement frm1Frame =
            new FrameElement (HtmlConstants.FRM_SHEET1, this.frm1Url);
        FrameElement frm2Frame =
            new FrameElement (HtmlConstants.FRM_SHEET2, this.frm2Url);

        // insert frames into frame set:
//        frm1Frame.frameborder = true;
        frm1Frame.resize = true;
//        frm2Frame.frameborder = true;
        frm2Frame.resize = true;

        frameset.addElement (frm1Frame, this.frm1Size);
        frameset.addElement (frm2Frame, this.frm2Size);
        frameset.frameborder = true;
        frameset.frameSpacing = 1;

        page.body.addElement (frameset);

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // performShowContentFrameset


    /**************************************************************************
     * Get the previous element before the actual one. <BR/>
     * This method uses the provided oid to get the previous out of the database.
     *
     * @param   oid     Oid of the actual object.
     *
     * @return  Oid of the previous element within the container.
     *
     * @exception   NoMoreElementsException
     *              The element is the first one within the container.
     */
    public OID prevElement (OID oid) throws NoMoreElementsException
    {
        int i = 0;                      // loop variable
        boolean found = false;          // found requested object?
        ContainerElement actElem;       // actual element within container
        OID newOid = null;              // oid of next object

        if (true)                       // container object resists on this
                                        // server?
        {
            try
            {
                // try to retrieve the content of this container:
                this.retrieveContent (Operations.OP_VIEW,
                    this.getUserInfo ().orderBy, this.getUserInfo ().orderHow);
                // get the oid of the first element before the actual one:
                for (i = 0; !found && i < this.elements.size (); i++)
                {
                    actElem = this.elements.elementAt (i);
                    if (actElem.oid.oid[7] == oid.oid[7])
                    {
                        found = true;   // found the element
                    } // if
                } // for

                if (found)              // element was found?
                {
                    if (i > 1)          // not at the beginning of the container?
                    {
                        // get oid of next element:
                        newOid = this.elements.elementAt (i - 2).oid;
                    } // if not at the beginning of the container
                    else                // element is at the beginning of the container
                    {
                        // return exception:
                        NoMoreElementsException error =
                            new NoMoreElementsException (this.ERR_TYPE);
                        throw error;
                    } // else element is at the beginning of the container
                } // if element was found
                else                    // element was not found
                {
                    // return exception:
                    NoMoreElementsException error =
                        new NoMoreElementsException (this.ERR_TYPE);
                    throw error;
                } // else element was not found
            } // try
            catch (NoAccessException e) // no access to objects allowed
            {
                // send message to the user:
                this.showNoAccessMessage (Operations.OP_VIEW);
            } // catch
        } // if container object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server

        return newOid;                  // return the oid of the next element
    } // prevElement


    /**************************************************************************
     * Get the next element after the actual one. <BR/>
     * This method uses the provided oid to get the next out of the database.
     *
     * @param   oid     Oid of the actual object.
     *
     * @return  Oid of the next element within the container.
     *
     * @exception   NoMoreElementsException
     *              The element is the first one within the container.
     */
    public OID nextElement (OID oid) throws NoMoreElementsException
    {
        int i = 0;                      // loop variable
        boolean found = false;          // found requested object?
        ContainerElement actElem;       // actual element within container
        OID newOid = null;              // oid of next object

        if (true)                       // container object resists on this
                                        // server?
        {
            try
            {
                // try to retrieve the content of this container:
                this.retrieveContent (Operations.OP_VIEW,
                    this.getUserInfo ().orderBy, this.getUserInfo ().orderHow);
                // get the oid of the first element after the actual one:
                for (i = 0; !found && i < this.elements.size (); i++)
                {
                    actElem = this.elements.elementAt (i);
                    if (actElem.oid.oid[7] == oid.oid[7])
                    {
                        found = true;
                    } // if
                } // for

                if (found)              // element was found?
                {
                    if (i < this.elements.size ())
                                        // not at the end of the container?
                    {
                        // get oid of next element:
                        newOid = this.elements.elementAt (i).oid;
                    } // if not at the end of the container
                    else                // element is at the end of the container
                    {
                        // return exception:
                        NoMoreElementsException error =
                            new NoMoreElementsException (this.ERR_TYPE);
                        throw error;
                    } // else element is at the end of the container
                } // if element was found
                else                    // element was not found
                {
                    // return exception:
                    NoMoreElementsException error =
                        new NoMoreElementsException (this.ERR_TYPE);
                    throw error;
                } // else element was not found
            } // try
            catch (NoAccessException e) // no access to objects allowed
            {
                // send message to the user:
                this.showNoAccessMessage (Operations.OP_VIEW);
            } // catch
        } // if container object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server

        return newOid;                  // return the oid of the next element
    } // nextElement


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     * The query or view must at least have the attributes userId and rights.
     * Queries on these attributes have to be addable to this query. <BR/>
     * The query must have at least one constraint within WHERE to ensure that
     * other constraints can be added with AND. <BR/>
     * <B>Standard format:</B>. <BR/>
     *      "SELECT DISTINCT oid, &lt;other attributes> " +
     *      " FROM " + this.viewContent +
     *      " WHERE containerId = " + oid;. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     * As you can see the query should contain at least the oid.
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveSelectionData ()
    {
//debug ("createQueryRetrieveSelectionData");
        StringBuffer queryStr = new StringBuffer (); // the query
    //use when everybody has flags within his / her views:
        queryStr
            .append ("SELECT DISTINCT oid, state, name, typeCode, typeName, isLink,")
            .append ("        linkedObjectId, owner, ownerName, ownerOid,")
            .append ("        ownerFullname, lastChanged, isNew, icon,")
            .append ("        description, flags")
            .append (" FROM    ").append (this.viewSelectionContent)
            .append (" WHERE   oid = oid");
                // no extended filter required, but there must be a WHERE clause

        // return the computed query string:
        return queryStr.toString ();
    } // createQueryRetrieveSelectionData


    /**************************************************************************
     * Extend the query with constraints specific for delete operations. <BR/>
     * This method just adds some constraints to the already existing query.
     * It must be empty or start with "AND...".
     * This method can be overwritten in subclasses. <BR/>
     *
     * @return  The extension to the query.
     *
     * @see #createQueryRetrieveContentData
     */
    protected StringBuffer extendQueryRetrieveDeleteData ()
    {
        return new StringBuffer ();     // normally don't extend the query
    } // extendQueryRetrieveDeleteData


    /**************************************************************************
     * Create/delete Join to selected/deselected Businessobjects
     * ONLY A DUMMY - MUST BE OVERLOADED IN SUBCONTAINER. <BR/>
     */
    public void handleSelectedElements ()
    {
        // this method can be overwritten in subclasses
    } // handleSelectedElements


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard container. <BR/>
     */
    protected void setHeadingsAndOrderings ()
    {
        this.p_showExtendedAttributes =
            this.getUserInfo ().userProfile.showExtendedAttributes;

        if (!this.p_showExtendedAttributes)
            // do not show extended attributes
        {
            this.headings = MultilingualTextProvider.getText (
                BOTokens.TOK_BUNDLE, BOListConstants.LST_HEADINGS_REDUCED, env);
            this.orderings = BOListConstants.LST_ORDERINGS_REDUCED;
        } // if showExtendedAttributes
        else
            // show extended attributes
        {
            this.headings = MultilingualTextProvider.getText (
                BOTokens.TOK_BUNDLE, BOListConstants.LST_HEADINGS, env);
            this.orderings = BOListConstants.LST_ORDERINGS;
        } // else

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings


    /**************************************************************************
     * Set the headings and the orderings for a selection list. <BR/>
     */
    protected final void setSelectionHeadingsAndOrderings ()
    {
        this.p_showExtendedAttributes = false;

        this.headings =  MultilingualTextProvider.getText (
                BOTokens.TOK_BUNDLE, BOListConstants.LST_HEADINGS_REDUCED, env);
        this.orderings = BOListConstants.LST_ORDERINGS_REDUCED;

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setSelectionHeadingsAndOrderings


    /**************************************************************************
     * Ensure that the actual ordering is available. <BR/>
     * If there is an ordering defined which is not available within the
     * <A HREF="#orderings">orderings</A> array the default value is used.
     * If ther is an ordering defined wich null in the orderings array, the
     * first available ordering in the orderings array is used.
     */
    protected void ensureAvailableOrdering ()
    {
        if (this.orderBy < 0 || this.orderBy >= this.orderings.length)
                                        // order column out of range?
        {
            this.orderBy = BOListConstants.LST_DEFAULTORDERING; // take default column
            this.orderHow = BOConstants.ORDER_ASC;  // take default kind of ordering
        } // if order column out of range


        if (this.orderings [this.orderBy] == null)
        {
            this.orderHow = BOConstants.ORDER_ASC;  // take default kind of ordering

            // find first available ordering
            for (this.orderBy = 0;
                 this.orderBy < this.orderings.length && this.orderings [this.orderBy] == null;
                 this.orderBy++)
            {
                // nothing to do
            } // for

            // check again if any ordering has been found
            // if not reset again to default ordering. this indicates
            // that there were NO available orderings at all!!!
            if (this.orderBy >= this.orderings.length)
            {
                this.orderBy = BOListConstants.LST_DEFAULTORDERING;
            } // if
        } // if current order by column in current container is not able no be ordered by
          // like 'image' in productgroup
    } // ensureAvailableOrdering


    /**************************************************************************
     * This function returns the number of elements in this container. <BR/>
     * Its used in the BuliButtonBar function of the BusinessObject and used,BR>
     * for disabling buttons for list operation , if there are no elements in the
     * container.
     *
     * @return  The no of elements in the container if elements is defined otherwise
     *          returns <CODE>0</CODE>.
     */
    protected int getElementSize ()
    {
        if (this.elements != null)
        {
            return this.elements.size ();
        } // if

        return 0;
    } // getElementSize


    /**************************************************************************
     * This method finds an object in the container which can be checked in
     * next (without selecting an object). The object must be checked out first.
     * If more objects in a container are checked out then this method shall
     * implement the strategy. <BR/>
     * Must be overridden.
     *
     * @return  <CODE>null</CODE> is returned.
     *
     * @throws  NoAccessException
     *          An access error occurred.
     */
    protected BusinessObject getObjectForCheckIn () throws NoAccessException
    {
        return null;
    } // getCheckedOutObject


    /**************************************************************************
     * This method finds an object in the container which can be checked out
     * next (without selecting an object). <BR/>
     * If more objects in a container are checked out then this method shall
     * implement the strategy. <BR/>
     * Must be overridden.
     *
     * @return  <CODE>null</CODE> is returned.
     *
     * @throws  NoAccessException
     *          An access error occurred.
     */
    protected BusinessObject getObjectForCheckOut () throws NoAccessException
    {
        return null;
    } // getObjectForCheckOut


    /**************************************************************************
     * This method checks the object of the container in which is retrieved
     * by the method getObjectForCheckIn. <BR/>
     * A popup message is displayed which shows which object was checked in.
     *
     * @return The container element itself is returned.
     *
     * @throws  NoAccessException
     *          An access error occurred.
     *
     * @see #getObjectForCheckIn
     */
    public BusinessObject checkIn () throws NoAccessException
    {
        // find the checked out object and check it in
        BusinessObject b = this.getObjectForCheckIn ();

        // if object not null
        if (b != null)
        {
            b.checkIn ();
        } // if

        return b;
    } // checkIn


    /**************************************************************************
     * Checks in a Businessoject and returns the oid of the object that shall
     * be displayed afterwards. <BR/>
     *
     * @return  The oid of the object that shall be displayed afterwards.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    public OID checkInReturnDisplayableObject () throws NoAccessException
    {
        BusinessObject boToShow = null; // business object to be displayed

        // perform the checkin:
        boToShow = this.checkIn ();

        this.showPopupMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTCHECKEDIN, new String[] {boToShow.name}, env));

        // ensure that the tabs are displayed:
        this.displayTabs = true;

        // return the oid of the object to be displayed:
        return this.oid;
    } // checkIn


    /**************************************************************************
     * This method realizes that the edit form is showed before checking in
     * the actual (master) businessobject within the container.
     *
     * @param function The function which shall be displayed after pressing
     *                 the ok button in the change form.
     *
     * @throws  NoAccessException
     *          An access error occurred.
     */
    public void editBeforeCheckInContainer (int function) throws NoAccessException
    {
        // find the checked out object and check it in
        BusinessObject b = this.getObjectForCheckIn ();
        if (b != null)
        {
            b.editBeforeCheckIn (function);
        } // if
    } // editBeforeCheckInContainer


    /**************************************************************************
     * This method realizes that one object is checked out in the container.
     * The container itself determines the object. <BR/>
     *
     * @return  The business object which was checked out.
     *
     * @throws  NoAccessException
     *          An access error occurred.
     */
    public BusinessObject checkOut () throws NoAccessException
    {
        // find the checked out object and check it in
        BusinessObject b = this.getObjectForCheckOut ();

        // if an object is found for checking out
        if (b != null)
        {
            // check the object out:
            b.checkOut ();
        } // if object to check out was found

        return b;
    } // checkOut


    /**************************************************************************
     * Sets the virtual objects that have been selected for pasting. <BR/>
     *
     * @param   selectedObjects     The vector with the objects to be set.
     */
    public void setPasteObjects (Vector<BusinessObject> selectedObjects)
    {
        this.p_selectedObjects = selectedObjects;
    } // setPasteObjects


    /**************************************************************************
     * Gets the virtual objects that have been selected for pasting. <BR/>
     *
     * @return  The vector with the objects to be pasted.
     */
    public Vector<BusinessObject> getPasteObjects ()
    {
        return this.p_selectedObjects;
    } // getPasteObjects


    /**************************************************************************
     * Copies all legal elements for this container from selectedObjects
     * to selectedObjectsClean. <BR/>
     *
     * @param   selectedObjects        original list.
     *
     * @return  The cleaned list.
     */
    private Vector<BusinessObject> removeIllegalObjects (Vector<BusinessObject> selectedObjects)
    {
        Vector<BusinessObject> selectedObjectsClean = new Vector<BusinessObject> (10, 10);
                                        // array of cleaned element list
        String typeIdStr;               // the typeString of an Element
        int selectedObjectsLength;      // number of original elements
        int lengthOfTypeIds = 0;        // number of types allowed
        String[] typeIds = this.getTypeIds (); // ids of all allowed types
        String typeCode = null;

        selectedObjectsLength = selectedObjects.size ();
        if (typeIds != null)                    // types allowed?
        {
            lengthOfTypeIds = typeIds.length;   // get the number of types
        } // if types allowed

        // run through all elements and get the typeString of them
        for (int j = 0; j < selectedObjectsLength; j++)
        {
            // get the typeCode of the object it will be transformed to
            typeCode = selectedObjects.elementAt (j).p_targetPhysicalTypeCode;
            typeIdStr = "" + this.getTypeCache ().getTVersionId (typeCode);
            for (int i = 0; i < lengthOfTypeIds; i++)
            {
                if (typeIds[i].equalsIgnoreCase (typeIdStr))
                                        // the type was found?
                {
                    // add the element to output list
                    selectedObjectsClean.addElement (selectedObjects
                        .elementAt (j));
                } // if the type was found
            } // for i
        } // for j
        return selectedObjectsClean;
    } // removeIllegalObjects

} // class Container

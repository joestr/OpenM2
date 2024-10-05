/*
 * Class: UserContainer_01.java
 */

// package:
package ibs.obj.user;

// imports:
//KR TODO: unsauber
import ibs.app.CssConstants;
import ibs.bo.BOConstants;
import ibs.bo.BOListConstants;
import ibs.bo.BOMessages;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.BuildException;
import ibs.tech.html.GroupElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;

import java.util.Vector;


/******************************************************************************
 * This class represents one object of type UserContainer with version 01. <BR/>
 *
 * @version     $Id: UserContainer_01.java,v 1.24 2010/04/13 15:55:57 rburgermann Exp $
 *
 * @author      Centner Martin (CM), 980701
 ******************************************************************************
 */
public class UserContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: UserContainer_01.java,v 1.24 2010/04/13 15:55:57 rburgermann Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class. <BR/>
     */
    public UserContainer_01 ()
    {
        // call constructor of super class:
        super ();
    } // UserContainer_01


    /**************************************************************************
     * This constructor creates a new instance of the class UserContainer_01.
     * <BR/>
     * The compound object id is used as base for getting the
     * <A HREF="#server">server</A>, <A HREF="#type">type</A>, and
     * <A HREF="#id">id</A> of the business object. These values are stored in
     * the special public attributes of this type. <BR/>
     * The <A HREF="#user">user object</A> is also stored in a specific
     * attribute of this object to make sure that the user's context can be
     * used for getting his/her rights.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public UserContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // UserContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the class-procedureNames:
        // set view to use for retrieveContent:
        this.viewContent = "v_UserContainer_01$content";

        // set headings:
        // set ordering attributes for the corresponding headings:
        // set the instance's attributes:
        // set the class wich contains the data of the elements to be shown:
        this.elementClassName = "ibs.obj.user.UserContainerElement_01";
    } // initClassSpecifics


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * containers content view. <BR/>
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
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_PASTE,
            Buttons.BTN_SEARCH,
//           Buttons.BTN_HELP,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * object info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can be displayed.
     */
    protected int[] setInfoButtons ()
    {
        // define buttons to be displayed:
        int [] buttons =
        {
            Buttons.BTN_SEARCH,
//           Buttons.BTN_HELP,
        }; // buttons

        // return button array
        return buttons;
    } // showInfoButtons


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
        UserContainerElement_01 actElem;    // actual object
        int i;                              // actual index
        int size = this.elements.size ();   // number of elements within this
                                            // container

        // start with the container representation: show header
        Page page = new Page ("List", false); // the output page

        // Stylesheetfile wird geladen
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETLIST].styleSheet;
        page.head.addElement (style);

        // set the icon of this object:
        this.setIcon ();

        GroupElement body;
        if (this.isTab ())
        {
            body = this.createHeader (page, MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                this.headingName, new String[] {this.name}, this.env), this.getNavItems (),
                this.containerName, this.icon, size);
        } // if
        else
        {
            body = this.createHeader (page, MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                this.headingName, new String[] {this.name}, this.env), this.getNavItems (), null,
                this.icon, size);
        } // else

        TextElement text = new TextElement ("text");

        // show description of container:
        body.addElement (this.createDescription ());

        if (size > 0)   // there are some elements?
        {
            int columns = this.headings.length + 1;
            TableElement table;
            RowElement tr;

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

            // create table definition for list:
            table = new TableElement (columns);
            table.classId = CssConstants.CLASS_LIST;
            table.frametypes = IOConstants.FRAME_VOID;
            table.width = HtmlConstants.TAV_FULLWIDTH;
            table.alignment = alignments;

            // create header
            tr = this.createHeading (this.headings,
                                this.orderings, orderBy, orderHow);

            // add header to table:
            table.addElement (tr, true);

            // loop through all elements of this container and display them:
            for (i = 0; i < size; i++)
            {
                actElem = (UserContainerElement_01) this.elements.elementAt (i);
                actElem.showExtendedAttributes = this.getUserInfo ().userProfile.showExtendedAttributes;
                table.addElement (actElem.show (BOListConstants.LST_CLASSROWS[i % BOListConstants.LST_CLASSROWS.length], env));
            } // for i

            // finish the container representation: show footer
            body.addElement (table);

        } // if there are some elements
        else // there are no elements
        {
            // show the according message to the user:
            text = new TextElement (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_CONTAINEREMPTY, new String[] {this.name}, this.env));
            body.addElement (text);
        } // else there are no elements

        if (this.p_isShowCommonScript)
        {
            // create the script to be executed on client:
            ScriptElement script = this.getCommonScript (true);
            page.body.addElement (script);
        } // if

        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // performShowContent


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     * The query or view must at least have the attributes userId and rights.
     * Queries on these attributes have to be addable to this query. <BR/>
     * The query must have at least one constraint within WHERE to ensure that
     * other constraints can be added with AND. <BR/>
     * <B>Standard format:</B><BR/>
     *      "SELECT DISTINCT oid, &lt;other attributes> " +
     *      " FROM " + this.viewContent +
     *      " WHERE containerId = " + oid;<BR/>
     * This method can be overwritten in subclasses. <BR/>
     * As you can see the query should contain at least the oid.
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        //use when everybody has flags within his / her views:

        return
            "SELECT DISTINCT oid, name, fullname, containerId, typeName, " +
                            "userId, ownerName, lastChanged, state, icon, " +
                            "isLink, linkedObjectId, owner, ownerOid, ownerFullname, isNew, description " +
            " FROM    " + this.viewContent +
            " WHERE 0 = 0 ";
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This metod is used to get all attributes of one element out of the
     * resultset. The attribute names which can be used are the ones which
     * are defined within the resultset of
     * <A HREF="#createQueryRetrieveContentData">createQueryRetrieveContentData</A>.
     * <BR/>
     * <B>Format:</B><BR/>
     * for oid properties:
     *      obj.&lt;property> = getQuOidValue ("&lt;attribute>"); <BR/>
     * for other properties:
     *      obj.&lt;property> = action.get&lt;type> ("&lt;attribute>"); <BR/>
     * The property <B>oid</B> is already gotten. This must not be done within this
     * method. <BR/>
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
        super.getContainerElementData (action, commonObj);
        UserContainerElement_01 obj = (UserContainerElement_01) commonObj;
        obj.fullName = action.getString ("fullname");
    } // getContainerElementData


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard
     * container.
     */
    protected void setHeadingsAndOrderings ()
    {
        if (!this.getUserInfo ().userProfile.showExtendedAttributes)
            // do notshow extended attributes
        {
            // set headings:
            this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOListConstants.LST_HEADINGS_USERCONTAINER_REDUCED, env);

            // set ordering attributes for the corresponding headings:
            this.orderings = BOListConstants.LST_ORDERINGS_USERCONTAINER_REDUCED;
            
        } // if showExtendedAttributes
        else
        {
            // show extende attributes
            // set headings:
            this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOListConstants.LST_HEADINGS_USERCONTAINER, env);

            // set ordering attributes for the corresponding headings:
            this.orderings = BOListConstants.LST_ORDERINGS_USERCONTAINER;
        } // else

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings

///////////////////////////////
//
// HP: Performance-Tuning
//
// modified methods: performRetrieveContentData, performRetrieveDeleteContentData
//
// nearly same methods as in super (Container) - modifications are:
// * had to add variable 'elementClass' (private in super)
// * rights-check if read/viewable
// * rights-check if deleteable
//
//
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
        this.debug (" --- UserContainer_01.performRetrieveContentData ANFANG --- ");
        String orderHowLocal = orderHow; // variable for local assignments

        ///////////////////////////////
        //
        // HP: Performance-Tuning - was declared PRIVATE in super!!!!
        //
        Class<? extends ContainerElement> elementClass = null;
        //
        //
        ///////////////////////////////

        SQLAction action = null;        //SQLAction for Databaseoperations
        ContainerElement obj;
        int rowCount;                   // row counter
        int i;                          // index of actual tuple
        this.size = 0;

        // ensure a correct ordering:
        if (!orderHowLocal.equalsIgnoreCase (BOConstants.ORDER_DESC)) // not descending?
        {
            orderHowLocal = BOConstants.ORDER_ASC;       // order ascending
        } // if


        // empty the elements vector:
        this.elements.removeAllElements ();

        // get the query string
        String query = null;
        // if no elements selected prepare a query to get the content data from the data base
        if (null == selectedElements)
        {
            query = this.createQueryRetrieveContentData ();
        } // if
        // if elements are selected prepare a query to check which may be displayed
        else
        {
            query = this.createQueryCopyData (selectedElements);
        } // else

        // check if the string is null
        if (query == null)
        {
            // no query string constructed: that means abort the db operation
            return;
        } // if (query == null)
        // get the elements out of the database:
        // create the SQL String to select all tuples

        String queryStr = query;
 /*************
  * HACK!
  * necessary for the retrieve of the deletable objects
  * having regard to the checkOut-flag
  */
        if (operation == Operations.OP_DELETE)
        {

///////////////////////////////
//
// HP: Performance-Tuning - removed rights-check
//
//            if (AppConstants.DB_TYPE == AppConstants.DB_ORACLE)
//                queryStr += "    AND B_AND(flags, 16) <> 16 ";
//            else
//                queryStr += "    AND (flags & 16) <> 16 ";
//
//
///////////////////////////////

            // add the delete specific constraints:
            queryStr += " " + this.extendQueryRetrieveDeleteData ();
        } // if
/**************
 * HACK Ende
 *
 */
        queryStr +=
            "    AND userId = " + this.user.id +
///////////////////////////////
//
// HP: Performance-Tuning - removed rights-check
//
//            getStringCheckRights (operation) +
//
///////////////////////////////
            " ORDER BY " + this.orderings[orderBy] + " " + orderHowLocal;

// test
        this.debug ("performRetrieveContentData: " + queryStr);

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            this.debug ("vor container action.execute (queryStr, false);");
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
                    // one element occures more then one time in the resultset of
                    // the selectstatement - problem is solved in method
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
                    // should not occur, display error message:
                    IOHelpers.showMessage ("Could not create container element",
                        e, this.app, this.sess, this.env, true);
                } // catch
                catch (InstantiationException e)
                {
                    // should not occur, display error message:
                    IOHelpers.showMessage ("Could not create container element",
                        e, this.app, this.sess, this.env, true);
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
        this.debug (" --- UserContainer_01.performRetrieveContentData ENDE --- ");
    } // performRetrieveContentData


//////////////////////////////////////////////////////////////////////
//
// HP: Tuning, SECOND METHOD
//
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
        ///////////////////////////////
        //
        // HP: Performance-Tuning - was declared PRIVATE in super!!!!
        //
        Class<? extends ContainerElement> elementClass = null;
        //
        //
        ///////////////////////////////
        String orderHowLocal = orderHow; // variable for local assignments
        SQLAction action = null;        // the action object used to access the
                                        // database
        ContainerElement obj;
        int rowCount;                   // row counter
        int i;                          // index of actual tuple
        this.size = 0;

        // ensure a correct ordering:
        if (!orderHowLocal.equalsIgnoreCase (BOConstants.ORDER_DESC)) // not descending?
        {
            orderHowLocal = BOConstants.ORDER_ASC;       // order ascending
        } // if

        // empty the elements vector:
        this.elements.removeAllElements ();

        // get the query string
        String query = this.createQueryRetrieveContentData ();
        // check if the string is null
        if (query == null)
        {
            // no query string constructed: that means abort the db operation
            return;
        } // if (query == null)
        // get the elements out of the database:
        // create the SQL String to select all tuples

        String queryStr = query;

///////////////////////////////
//
// HP: Performance-Tuning - removed rights-check
//
//        if (AppConstants.DB_TYPE == AppConstants.DB_ORACLE)
//            queryStr += "    AND B_AND(flags, 16) <> 16 ";
//        else
//            queryStr += "    AND (flags & 16) <> 16 ";
//
///////////////////////////////

        queryStr += "    AND userId = " + this.user.id +
///////////////////////////////
//
// HP: Performance-Tuning - removed rights-check
//
//                        getStringCheckRights (operation) +
//
///////////////////////////////
                    "ORDER BY " + this.orderings[orderBy] + " " + orderHowLocal;


        this.debug ("performRetrieveDeleteContentData: " + queryStr);
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
                    // should not occur, display error message:
                    IOHelpers.showMessage ("Could not create container element",
                        e, this.app, this.sess, this.env, true);
                } // catch
                catch (InstantiationException e)
                {
                    // should not occur, display error message:
                    IOHelpers.showMessage ("Could not create container element",
                        e, this.app, this.sess, this.env, true);
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
//
// HP: Tuning Ende
//
///////////////////////////////

} // class UserContainer_01

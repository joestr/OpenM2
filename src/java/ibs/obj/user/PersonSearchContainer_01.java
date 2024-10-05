/*
 * Class: PersonSearchContainer_01.java
 */

// package:
package ibs.obj.user;

// imports:
//KR TODO: unsauber
import ibs.app.CssConstants;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOListConstants;
import ibs.bo.BOMessages;
import ibs.bo.BOTokens;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.SingleSelectionContainer_01;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.user.PersonSearchContainerElement_01;
import ibs.obj.user.UserConstants;
import ibs.obj.user.UserTokens;
import ibs.service.user.User;
import ibs.tech.html.BuildException;
import ibs.tech.html.GroupElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;


/******************************************************************************
 * This class represents one object of type PersonContainer with version 01. <BR/>
 *
 * @version     $Id: PersonSearchContainer_01.java,v 1.22 2013/01/18 10:38:17 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ), 981216
 ******************************************************************************
 */
public class PersonSearchContainer_01 extends SingleSelectionContainer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: PersonSearchContainer_01.java,v 1.22 2013/01/18 10:38:17 rburgermann Exp $";


    /**
     * Show the Link to callingObject, or show all matching Objects to
     * searchparameter. <BR/>
     */
    protected int showLink = 0;

    /**
     * Oid of the calling object where a link to a specific person should be
     * added. <BR/>
     */
    public OID callingOid = null;


    /**************************************************************************
     * This constructor creates a new instance of the class. <BR/>
     */
    public PersonSearchContainer_01 ()
    {
        // call constructor of super class:
        super ();
    } // PersonSearchContainer_01


    /**************************************************************************
     * This constructor creates a new instance of the class document. <BR/>
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
    public PersonSearchContainer_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // PersonSearchContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the view containing the data for the list:
        this.viewContent = "v_PersonSearchCont_01$content";
        // set the class wich contains the data of the elements to be shown:
        this.elementClassName = "ibs.obj.user.PersonSearchContainerElement_01";

        // set majorContainer true:
        this.isMajorContainer = true;

        // indicate that no object of this class is physically on the database:
        this.isPhysical = false;
        this.name = "";
        this.setIcon ();
        this.searchColumnName = "fullname";
    } // initClassSpecifics


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
    public void performShowContent (int representationForm, int orderBy,
                                    String orderHow)
    {
        int i;                          // actual index
        int size = this.elements.size (); // number of elements within this
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

        // insert header
        GroupElement body;

        if (this.showLink == BOConstants.SHOWSEARCHEDOBJECTS)
                                        // Container shows matching objects?
        {
            body = this.createHeader (page, MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                this.headingName, new String[] {MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                    UserTokens.ML_PERSONSEARCHHEADER_SEARCH, env)}, env), this.getNavItems (),
                this.containerName, this.icon, size);
        } // if
        else
        // Container shows object which is
        // linked to calling object
        {
            body = this.createHeader (page, MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                this.headingName, new String[] {MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                    UserTokens.ML_PERSONSEARCHHEADER_SHOWLINK, env)}, env), this.getNavItems (),
                this.containerName, this.icon, size);
        } // else Container shows object which is linked to calling object

        TextElement text = new TextElement ("text");

        // show description of container:
        body.addElement (this.createDescription ());

        if (size > 0)               // there are some elements?
        {
            TableElement table;     // table containing the list
            RowElement tr;          // actual table row
/*
            TableDataElement td;
*/

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

            // create table definition for list:
            table = new TableElement (columns);
            table.classId = CssConstants.CLASS_LIST;
            table.frametypes = IOConstants.FRAME_VOID;
            table.width = HtmlConstants.TAV_FULLWIDTH;
            table.alignment = alignments;

            // define header:
            if (this.maxElements > 0 && this.size > this.maxElements)
                                    // too many elements?
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
                tr = this.createHeading (this.headings,
                                    this.orderings, orderBy, orderHow, false);

                // restore orderings
                this.orderings = tempOrderings;
            } // if there are too many elements
            else                    // not too many elements
            {
                // create header
                tr = this.createHeading (this.headings,
                                    this.orderings, orderBy, orderHow, false);
            } // else not too many elements

            // add header to table:
            table.addElement (tr, true);

            // loop through all elements of this container and display them.
            PersonSearchContainerElement_01 element;
            for (i = 0; i < size; i++)
            {
                // get the actual element:
                element = (PersonSearchContainerElement_01) this.elements
                    .elementAt (i);

                // set the oid of the user where a link to a person should be added
                element.callingOid = this.callingOid;

                // set the flag to indicate if url under name should show the person or
                // create a reference to person
                element.showLink = this.showLink;

                //set fieldname of field to be updated in user_changeform after selection of person
                element.updateFieldName = this.updateFieldName;

                // represent the actual element to the user:
                element.showExtendedAttributes =
                    this.getUserInfo ().userProfile.showExtendedAttributes;

                table.addElement (element
                    .show (BOListConstants.LST_CLASSROWS[i %
                        BOListConstants.LST_CLASSROWS.length], env));

                if (i == 0 && this.showLink == BOConstants.SHOWLINKEDOBJECT)
                                        // Container shows object which is
                                        // linked to calling object
                {
                    ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
                    // Set Value in field linked person in changeform of User_01
                    script.addScript (
                        "top.sheet.sheet1." + HtmlConstants.JREF_SHEETFORM +
                        this.updateFieldName + HtmlConstants.JREF_VALUEASSIGN +
                        "'" + element.fullname + "'");

                    page.body.addElement (script);
                } // if Container shows object which is linked to calling object
            } // for i

            // finish the container representation: show footer
            body.addElement (table);
        } // if there are some elements
        else                            // there are no elements
        {
            // show the according message to the user:
            TableElement table = new TableElement ();
            table.width = "10hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhj0%";
            RowElement tr = new RowElement (2);
            text = new TextElement (MultilingualTextProvider.getMessage(this.msgBundleContainerEmpty,
                    this.msgContainerEmpty, new String[] {this.name}, env));
            TableDataElement td = new TableDataElement (text);
            td.classId = CssConstants.CLASS_BODY;
            tr.addElement (td);
            table.addElement (tr);

            body.addElement (table);

        } // else there are no elements

        // create script
        ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);

        // show message if there are too many entries in the list:
        if (this.maxElements > 0 && this.size > this.maxElements)
                                    // too many elements?
        {
            // add alert message to script:
            script.addScript ("alert (\"" +
                MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
                    this.msgTooMuchElements, new String[] {"" + this.maxElements}, env) +
                "\\n\\n" +
                MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
                    this.msgDisplayableElements, new String[] {"" + this.maxElements}, env) +
                "\");\n");
        } // if

/*
        // add oids to script:
        script.addScript (
//            "top.showListHeading ('" + name + "', " + size + ");\n" +
            "top.containerId = \"" + containerId + "\";\n" +
            "top.majorOid = \"" + oid + "\";\n" +
            "top.oid = \"" + oid + "\";\n" +
//! MS 990107 changed ... /////////////////////////////////////////////////////
            Helpers.replace (AppConstants.CALL_SHOWBUTTONSCONTENT, UtilConstants.TAG_NAME, getButtonBarCode (representationForm, true)));
//! ... MS 990107 changed ... /////////////////////////////////////////////////
*/
/*
            AppConstants.CALL_SHOWBUTTONSCONTENT);
*/
//! ... MS 990107 changed /////////////////////////////////////////////////////

/*
        if (this.isMajorContainer)
            script.addScript (
                "top.isOtherContent=true;\n" +
                "top.otherContentId=\"" + oid + "\"");
        if (this.displayTabs)           // shall the tab bar be displayed?
            script.addScript (
                "top.showTabs (\"" + oid + "\", " + preselectTab + ");");
*/

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
    } // performShowContent


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        if (this.showLink == BOConstants.SHOWLINKEDOBJECT)
                                        // show only linked Person
        {
            String queryString =
                " SELECT DISTINCT c.oid, c.state, c.name, c.typeCode, c.typeName, c.isLink, c.linkedObjectId," +
                " c.owner, c.ownerName, c.ownerOid, c.ownerFullname, c.lastChanged, c.isNew," +
                " c.icon, c.description," +
                " p.fullname AS fullname, p.prefix AS prefix, p.company AS company, p.offemail AS email" +
                " FROM v_Container$content c, mad_Person_01 p" +
                " WHERE p.oid = c.linkedObjectId" +
                " AND c.containerId = " + this.callingOid.toStringQu ();

/*
                "SELECT DISTINCT oid, state, name, typeName, isLink, " +
                "        linkedObjectId, owner, ownerName, ownerOid, " +
                "        ownerFullname, lastChanged, isNew, icon, description, " +
                "        fullname, prefix, company, email " +
                " FROM    " + this.viewContent +
                " WHERE   oid = isLink = 1 AND containerId = " + this.callingOid;     // oid = oid is only used because WHERE must be there
*/
            return queryString;
        } // if

        return super.createQueryRetrieveContentData ();
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Create the query to get the container's (selectionlist) content out of the
     * database. <BR/>
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
    protected String createQueryRetrieveSelectionData ()
    {
    //use when everybody has flags within his / her views:
        return
            "SELECT DISTINCT oid, state, name, typeName, isLink, " +
            "        linkedObjectId, owner, ownerName, ownerOid, " +
            "        ownerFullname, lastChanged, isNew, icon, description, " +
            "        fullname, prefix, company, email " +
            " FROM    " + this.viewContent +
            " WHERE   oid = oid ";      // oid = oid is only used because WHERE must be there
    } // createQueryRetrieveSelectionData


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
        // convert common element object to actual type:
        PersonSearchContainerElement_01 obj = (PersonSearchContainerElement_01) commonObj;
        // get common attributes:

        super.getContainerElementData (action, obj);

        // get element type specific attributes:
        obj.prefix = action.getString ("prefix");
        obj.fullname = action.getString ("fullname");
        obj.firm =  action.getString ("company");
        obj.email = action.getString ("email");

    } // getContainerElementData


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <CODE>ibs.bo.BusinessObject.env</CODE> property is used
     * for getting the parameters. This property must be set before calling
     * this method. <BR/>
     */
    public void getParameters ()
    {
        OID oid = null;
        int num = 0;

        // get other parameters
        super.getParameters ();

        // get oid of calling object
        if ((oid = this.env.getOidParam (BOArguments.ARG_CALLINGOID)) != null)
        {
            this.callingOid = oid;
        } // if
        else
        {
            this.callingOid = null;
        } // else

        // show linked person or matching persons in container
        if ((num = this.env.getIntParam (BOArguments.ARG_SHOWLINK)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.showLink = num;
        } // if
    } // getParameters ()


    /**************************************************************************
     * Try to read the name of the user wich this PersonSearchContainer belongs
     * to, out of the db.
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected void performRetrieveData (int operation) throws NoAccessException
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        String str;
        int rowCount;                   // row counter
        String queryStr =  " SELECT name" +
                           " FROM ibs_Object" +
                           " WHERE oid = " + this.callingOid.toStringQu ();

        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();

            rowCount = action.execute (queryStr, false);

            // empty resultset or error while executing ( rowCount < 0)
            if (rowCount <= 0)
            {
                return;                 // terminate this method
            } // if

            // get tuple out of db
            if (!action.getEOF ())
            {
                // try to read name out of tuple
                if ((str = action.getString ("name")) != null)
                {
                    this.containerName = str;
                } // if
            } // if
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
    } // performRetrieveData


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard
     * container.
     */
    protected void setHeadingsAndOrderings ()
    {
        // check if columns shall be reduced
        if (!this.getUserInfo ().userProfile.showExtendedAttributes)
        {
            // reduce list

            // set headings:
            this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                UserConstants.LST_HEADINGS_PERSONS_REDUCED, env);

            // set ordering attributes for the corresponding headings:
            this.orderings = new String[2];
            this.orderings[0] = UserConstants.ARG_ORDER_PNAME;
            this.orderings[1] = UserConstants.ARG_ORDER_PFIRM;

            // set standard "order by" column
            this.orderBy = 0;  // order by name
        } // if
        else
        {
            // set the headings
            this.headings = MultilingualTextProvider.getText (
                BOTokens.TOK_BUNDLE, UserConstants.LST_HEADINGS_PERSONS, env);

            // set the orderings
            this.orderings = new String [4];
            this.orderings[0] = UserConstants.ARG_ORDER_PPREFIX;
            this.orderings[1] = UserConstants.ARG_ORDER_PNAME;
            this.orderings[2] = UserConstants.ARG_ORDER_PFIRM;
            this.orderings[3] = UserConstants.ARG_ORDER_PEMAIL;

            this.orderBy = 1; // order by name
        } // else

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings

} // class PersonSearchContainer_01

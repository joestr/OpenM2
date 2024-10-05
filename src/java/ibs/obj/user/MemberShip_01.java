/*
 * Class: MemberShip_01.java
 */

// package:
package ibs.obj.user;

// imports:
//KR TODO: unsauber
import ibs.app.CssConstants;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOListConstants;
import ibs.bo.BOMessages;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.bo.type.TypeConstants;
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
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;

import java.util.Vector;


/******************************************************************************
 * This class represents one object of type MemberShip with version 01. <BR/>
 *
 * @version     $Id: MemberShip_01.java,v 1.26 2013/01/16 16:14:10 btatzmann Exp $
 *
 * @author      Keim Christine (CK), 980727
 ******************************************************************************
 */
public class MemberShip_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MemberShip_01.java,v 1.26 2013/01/16 16:14:10 btatzmann Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor creates a new instance of the class. <BR/>
     */
    public MemberShip_01 ()
    {
        // call constructor of super class:
        super ();
    } // MemberShip_01


    /**************************************************************************
     * This constructor creates a new instance of the class MemberShip_01.
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
    public MemberShip_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // MemberShip_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the class-procedureNames:
        // set view to use for retrieveContent:
        this.viewContent = "v_MemberShip_01$content";

        // set headings:
        // set ordering attributes for the corresponding headings:
        // set the instance's attributes:
        // set the class wich contains the data of the elements to be shown:
        this.elementClassName = "ibs.obj.user.MemberShipElement_01";
    } // initClassSpecifics


    ///////////////////////////////////////////////////////////////////////////
    // functions called from application level
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * containers content view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  an array with button ids that can potentially be displayed
     */
    protected int[] setContentButtons ()
    {
        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_ASSIGN,
            Buttons.BTN_REFERENCE,
//            Buttons.BTN_CLEAN,
//            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
//            Buttons.BTN_LOGIN
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons


    ///////////////////////////////////////////////////////////////////////////
    // database functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        StringBuffer queryStr;          // the query string

        queryStr = new StringBuffer ()
            .append (" SELECT DISTINCT oid, state, name, typeName, isLink,")
                .append (" linkedObjectId, owner, ownerName, ownerOid,")
                .append (" ownerFullname, lastChanged, isNew, icon, description")
            .append (" FROM ").append (this.viewContent)
            .append (" WHERE containerId = ").append (this.containerId.toStringQu ())
                .append (" AND origGroupId = id ");

        // return the result:
        return queryStr.toString ();
    } // createQueryRetrieveContentData


    ///////////////////////////////////////////////////////////////////////////
    // viewing functions
    ///////////////////////////////////////////////////////////////////////////

    /***************************************************************************
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
        MemberShipElement_01 actElem;   // actual object
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
                actElem = (MemberShipElement_01) this.elements.elementAt (i);
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
                BOMessages.ML_MSG_CONTAINEREMPTY, new String[] {this.name}, env));
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
            IOHelpers.showMessage (e.getMsg (), this.app, this.sess, this.env);
        } // catch
    } // performShowContent


    /**************************************************************************
     * Create the query to get the data for the selectionlist out of the db.
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
    protected String createQueryRetrieveSelectionData ()
    {
        StringBuffer queryStr;          // the query string

        // use when everybody has flags within his / her views:
        queryStr = new StringBuffer ()
            .append ("SELECT DISTINCT oid, state, name, typeName, isLink,")
                .append (" linkedObjectId, owner, ownerName, ownerOid,")
                .append (" ownerFullname, lastChanged, isNew, icon, description,")
                .append (" flags")
            .append (" FROM ").append (this.viewSelectionContent)
            .append (" WHERE tVersionId = ")
            .append (this.getTypeCache ().getTVersionId (TypeConstants.TC_Group));

        // return the query:
        return queryStr.toString ();
    } // createQueryRetrieveSelectionData


    /**************************************************************************
     * Create/delete Join to selected/deselected Groups. <BR/>
     */
    public void handleSelectedElements ()
    {
        // get oid`s of old joined groups
        try
        {
            this.performRetrieveContentData (Operations.OP_DELETE, this.orderBy,
                this.orderHow);
        } // try
        catch (NoAccessException e)
        {
            return;
        } // catch

        // convert oid`s of old joined groups to strings in stringarray
        String[] oldOidString = new String [this.elements.size ()];
        ContainerElement element;

        for (int i = 0; i < this.elements.size (); i++)
        {
            element = this.elements.elementAt (i);
            oldOidString[i] = element.oid.toString ();
        } // for

        // get oid`s of new joined groups
        String[] newOidString = this.getSelectedElements ();


        // compare old and new joined groups and decide wich join should be inserted
        // and wich join should be deleted
        Vector<String> deleteOids = new Vector<String> ();
        Vector<String> insertOids = new Vector<String> ();

        StringHelpers.compareStringArrays (newOidString, oldOidString,
            deleteOids, insertOids);
//debug (" Helpers: " + Helpers.trace);

        // convert vectors to stringarrays
        OID [] deleteOidArray = new OID [deleteOids.size ()];

// HACK -------------   mgl. lösung .. vergleich über Vectoren mit OID's lösen
//                      statt Helpers.compareStringArrays ..
        OID helpOid = null;

        for (int i = 0; i < deleteOids.size (); i++)
        {
            try
            {
                helpOid = new OID (deleteOids.elementAt (i).toString ());
            } // try
            catch (IncorrectOidException e)
            {
                // should not occur, display error message:
                IOHelpers.showMessage ("Could not create oid from \'" +
                    deleteOids.elementAt (i) + "\'", e, this.app, this.sess,
                    this.env, true);
            } // catch

            deleteOidArray[i] = helpOid;
        } // for
// HACK -------------------

        String [] insertOidStrings = new String [insertOids.size ()];
        for (int i = 0; i < insertOids.size (); i++)
        {
            insertOidStrings[i] = insertOids.elementAt (i).toString ();
        } // for i

        this.deleteRelationshipToGroups (deleteOidArray);
        this.insertRelationshipToGroups (insertOidStrings);
    } // handleSelectedElements


    /**************************************************************************
     * Delete joins to groups in table ibs_GroupUser. <BR/>
     *
     * @param   deleteOids  oids of groups where the join to should be deleted.
     */
    private void deleteRelationshipToGroups (OID [] deleteOids)
    {
        // perform stored procedure in database to store (ins/upd) the object
        // properties- checking of rights also happen in stored procedure

        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                "p_Group_01$delUser",
                StoredProcedureConstants.RETURN_VALUE);
        
        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters:
        // uid
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            (this.user != null) ? this.user.id : 0);

        // operation:
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            Operations.OP_DELELEM);
        // groupOid
        Parameter currOid = BOHelpers.addInParameter(sp, null);
        // userOid
        BOHelpers.addInParameter(sp, this.containerId);
        // roleOid
        BOHelpers.addInParameter(sp, null);

        // insert all joins to the group
        for (int k = 0; k < deleteOids.length; k++)
        {
            currOid.setValue (deleteOids[k].toString ());

            try
            {
                // perform the function call:
                BOHelpers.performCallFunctionData(sp, this.env);
            } // try
            catch (NoAccessException e)
            {
                // should not occur, display error message:
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
        } // for
    } // deleteRelationshipToGroups


    /**************************************************************************
     * Create join to groups in table ibs_GroupUser. <BR/>
     *
     * @param   oidStrings  oids of groups to be joined.
     */
    private void insertRelationshipToGroups (String[] oidStrings)
    {
        // perform stored procedure in database to store (ins/upd) the object
        // properties- checking of rights also happen in stored procedure

        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                "p_Referenz_01$create",
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // uid
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            (this.user != null) ? this.user.id : 0);

        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            Operations.OP_ADDELEM);
        // tVersionId
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            this.getTypeCache ().getTVersionId (TypeConstants.TC_Reference));
        // name
        sp.addInParameter (ParameterConstants.TYPE_STRING,
            this.getTypeCache ().getTypeName (TypeConstants.TC_Reference));
        // containerId_s
        BOHelpers.addInParameter(sp, this.oid);  // User (MembershipContainer)
        // containerKind
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            BOConstants.CONT_PARTOF);
        // isLink
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
            true);
        // linkedObjectId_s
        Parameter currOid = BOHelpers.addInParameter(sp, null);
        // description
        sp.addInParameter (ParameterConstants.TYPE_STRING,
            "");
        // oid_s
        sp.addOutParameter (ParameterConstants.TYPE_STRING);

        // insert all joins to the group
        for (int k = 0; k < oidStrings.length; k++)
        {
            currOid.setValue (oidStrings[k]);  // Group

            try
            {
                // perform the function call:
                BOHelpers.performCallFunctionData(sp, this.env);
            } // try
            catch (NoAccessException e)
            {
                // should not occur, display error message:
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
        } // for
    } // insertRelationshipToGroups


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
                BOListConstants.LST_HEADINGS_MEMBERSHIP_REDUCED, env);

            // set ordering attributes for the corresponding headings:
            this.orderings = BOListConstants.LST_ORDERINGS_MEMBERSHIP_REDUCED;
        } // if
        else
        {
            // set headings:
            this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOListConstants.LST_HEADINGS_MEMBERSHIP, env);

            // set ordering attributes for the corresponding headings:
            this.orderings = BOListConstants.LST_ORDERINGS_MEMBERSHIP;
        } // else

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings

} // class MemberShip_01

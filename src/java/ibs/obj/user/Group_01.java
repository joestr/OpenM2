/*
 * Class: Group_01.java
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
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;

import java.util.Vector;


/******************************************************************************
 * This class represents one object of type Group with version 01. <BR/>
 *
 * @version     $Id: Group_01.java,v 1.34 2013/01/16 16:14:10 btatzmann Exp $
 *
 * @author      Keim Christine (CK), 980729
 ******************************************************************************
 */
public class Group_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Group_01.java,v 1.34 2013/01/16 16:14:10 btatzmann Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class Group_01. <BR/>
     */
    public Group_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // Group_01


    /**************************************************************************
     * This constructor creates a new instance of the class Group_01. <BR/>
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
    public Group_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // Group_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the class-procedureNames:
        this.procCreate =       "p_Group_01$create";
        this.procChange =       "p_Group_01$change";
        this.procRetrieve =     "p_Group_01$retrieve";
        this.procDelete =       "p_Group_01$delete";
        this.procDeleteRec =    "p_Group_01$delete";
        this.procChangeState =  "p_Group_01$changeState";

        this.viewSelectionContent = "v_GroupUser$getAll";

        // set view to use for retrieveContent:
        this.viewContent   = "v_Group_01$content";
        this.elementClassName = "ibs.obj.user.GroupElement_01";

        this.specificChangeParameters = 1;
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
            Buttons.BTN_ASSIGN,
            Buttons.BTN_REFERENCE,
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
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
            Buttons.BTN_EDIT,
            Buttons.BTN_DELETE,
            Buttons.BTN_COPY,
//            Buttons.BTN_DISTRIBUTE,
//            Buttons.BTN_CLEAN,
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
        }; // buttons

        // return button array
        return buttons;
    } // showInfoButtons


    /**************************************************************************
     * Represent the content of the GroupContainer, i.e. its elements, to the
     * user.
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
        GroupElement_01 actElem;       // actual object
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

        if (size > 0)               // there are some elements?
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
            tr = this.createHeading (this.headings, this.orderings, orderBy,
                orderHow);

            // add header to table:
            table.addElement (tr, true);

            // loop through all elements of this container and display them.
            for (i = 0; i < size; i++)
            {
                actElem = (GroupElement_01) this.elements.elementAt (i);
                actElem.showExtendedAttributes =
                    this.getUserInfo ().userProfile.showExtendedAttributes;

                table.addElement (actElem.show (BOListConstants.LST_CLASSROWS[i % BOListConstants.LST_CLASSROWS.length], env));
            } // for i

            // finish the container representation: show footer
            body.addElement (table);

        } // if there are some elements
        else // there are no elements
        {
            // show the according message to the user:
            text = new TextElement (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_CONTAINEREMPTY, env));
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
     * Create the query to get the container's content out of the database.
     * <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        StringBuffer queryStr;          // the query string

        queryStr = new StringBuffer ()
            .append ("SELECT DISTINCT oid, name, fullname, containerId,")
                .append (" typeName, userId, ownerName, lastChanged, state,")
                .append (" icon, isLink, linkedObjectId, owner, ownerOid,")
                .append (" ownerFullname, isNew, description")
            .append (" FROM ").append (this.viewContent).append (" o,")
                .append (" (SELECT oid AS gOid, id AS gId FROM ibs_Group) g")
            .append (" WHERE containerId = ").append (this.oid.toStringQu ())
                .append (" AND origGroupId = gId")
                .append (" AND gOid = ").append (this.oid.toStringQu ());

        return queryStr.toString ();
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
     *      obj.&lt;property> = getQuOidValue (action, "&lt;attribute>"); <BR/>
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

        //convert the common object to the actual type
        GroupElement_01 obj = (GroupElement_01) commonObj;

        // get the element specific attributes
        obj.fullName = action.getString ("fullname");
    } // getContainerElementData


    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performChangeData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the change data stored procedure.
     *
     * @param sp        The stored procedure to add the change parameters to.
     */
    @Override
    protected void setSpecificChangeParameters (StoredProcedure sp)
    {
        // set the specific parameters:
        // active
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            this.state);
    } // setSpecificChangeParameters


    /**************************************************************************
     * Delete the user from the group. <BR/>
     *
     * @param   object  The oid of the user which shall be dropped from the
     *                  group.
     */
    public void deleteLink (OID object)
    {
        if (true)                       // business object resists on this
                                        // server?
        {
            try
            {
/* KR 020125: not necessary because already done before
                // try to retrieve the object:
                retrieve (Operations.OP_READ);
*/
                // show the object's data:
                this.performDeleteLink (Operations.OP_DELETE, object);
            } // try
            catch (NoAccessException e) // no access to objects allowed
            {
                // send message to the user:
                this.showNoAccessMessage (Operations.OP_DELETE);
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
            // invoke the object on the other server:
        } // else object resists on another server
    } // deleteLink


    /**************************************************************************
     * Delete the given object from the group. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     * @param   object      Id of the element to be dropped from the group.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected void performDeleteLink (int operation, OID object)
        throws NoAccessException
    {
        // check if the object is deletable:
/* TODO KR This check may not be performed like this because it works on the
 * actual object instead of the object parameter.
        checkDeletable ();
*/

        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                (object.type == this.getTypeCache ().getTypeId (TypeConstants.TC_User)) ?
                        "p_Group_01$delUser" : "p_Group_01$delGroup",
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // user id
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, (this.user != null) ? this.user.id : 0);
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, operation);
        // groupOid
        BOHelpers.addInParameter(sp, this.oid);
        // objectOid
        BOHelpers.addInParameter(sp, object);
        // roleOid
        sp.addInParameter (ParameterConstants.TYPE_STRING, "0");

        // perform the function call:
        BOHelpers.performCallFunctionData(sp, this.env);
    } // performDeleteLink


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
            .append (" SELECT DISTINCT oid, state, name, typeName, isLink,")
                .append (" linkedObjectId, owner, ownerName, ownerOid,")
                .append (" ownerFullname, lastChanged, isNew, icon, description,")
                .append (" flags, fullname")
            .append (" FROM ").append (this.viewSelectionContent)
            // do not show current group:
            .append (" WHERE  oid <> ").append (this.oid.toStringQu ());

        // return the result:
        return queryStr.toString ();
    } // createQueryRetrieveSelectionData


    /**************************************************************************
     * Create/delete Relationship to selected/deselected Users. <BR/>
     */
    public void handleSelectedElements ()
    {
        // get oids of old joined groups
        try
        {
            this.performRetrieveContentData (Operations.OP_DELETE, this.orderBy, this.orderHow);
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
                IOHelpers.showMessage ("Could not convert oid " + deleteOids.elementAt (i),
                    e, this.app, this.sess, this.env, true);
            } // catch

            deleteOidArray[i] = helpOid;
        } // for

        String [] insertOidStrings = new String [insertOids.size ()];
        for (int i = 0; i < insertOids.size (); i++)
        {
            insertOidStrings[i] = insertOids.elementAt (i).toString ();
        } // if

        this.deleteRelationshipToUsers (deleteOidArray);
        this.insertRelationshipToUsers (insertOidStrings);
    } // handleSelectedElements


    /**************************************************************************
     * Delete joins to users in table ibs_GroupUser. <BR/>
     *
     * @param   deleteOids  oids of groups where the join to should be deleted.
     */
    private void deleteRelationshipToUsers (OID [] deleteOids)
    {
        SQLAction action = null;        // SQLAction for Databaseoperation
        StoredProcedure sp = new StoredProcedure ();

        // set stored procedure return type:
        sp.setReturnType (StoredProcedureConstants.RETURN_VALUE);

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
        BOHelpers.addInParameter(sp, this.oid);
        // userOid
        Parameter currOid = BOHelpers.addInParameter(sp, null);
        // roleOid
        BOHelpers.addInParameter(sp, null);

        // delete all joins to the group
        for (int k = 0; k < deleteOids.length; k++)
        {
            currOid.setValue (deleteOids [k].toString ());
            // set stored procedure name
            if (deleteOids[k].type ==
                this.getTypeCache ().getTypeId (TypeConstants.TC_Group))
            {
                sp.setName ("p_Group_01$delGroup");
            } // if
            else if (deleteOids [k].type ==
                this.getTypeCache ().getTypeId (TypeConstants.TC_User))
            {
                sp.setName ("p_Group_01$delUser");
            } // if

            // perform stored procedure in database to store (ins/upd) the object
            // properties- checking of rights also happen in stored procedure

            IOHelpers.showProcCall (this, sp, this.sess, this.env);
            // execute stored procedure
            try
            {
                // open db connection -  only workaround - db connection must
                // be handled somewhere else
                action = this.getDBConnection ();
                // execute stored procedure - return value
                // gives right-information
                action.execStoredProc (sp);
                // end action
                action.end ();
            } // try
            catch (DBError e)
            {
                // an error occurred - show name and info
                IOHelpers.showMessage (e, this.app, this.sess, this.env);
            } // catch
            finally
            {
                this.releaseDBConnection (action);
            } // finally
        } // for
    } // deleteRelationshipToUsers


    /**************************************************************************
     * Create relationship to users in table ibs_GroupUser. <BR/>
     *
     * @param   oidStrings  oids of groups to be joined.
     */
    private void insertRelationshipToUsers (String[] oidStrings)
    {
        SQLAction action = null;    // SQLAction for Databaseoperation
        StoredProcedure sp = new StoredProcedure ();

        // set stored procedure return type
        sp.setReturnType (StoredProcedureConstants.RETURN_VALUE);
        // set stored procedure name
        sp.setName ("p_Referenz_01$create");

        // perform stored procedure in database to store (ins/upd) the object
        // properties- checking of rights also happen in stored procedure

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters:
        // uid
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            (this.user != null) ? this.user.id : 0);

        // operation:
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            Operations.OP_ADDELEM);
        // tVersionId
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            this.getTypeCache ().getTVersionId (TypeConstants.TC_Reference));
        // name
        sp.addInParameter (ParameterConstants.TYPE_STRING,
            this.getTypeCache ().getTypeName (TypeConstants.TC_Reference));
        // containerId_s
        BOHelpers.addInParameter(sp, this.oid);  // Group
        // containerKind
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            BOConstants.CONT_STANDARD);
        // isLink
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
            true);
        // linkedObjectId_s
        Parameter currOid = BOHelpers.addInParameter(sp, null);

        // description
        sp.addInParameter (ParameterConstants.TYPE_STRING, "");
        // oid_s
        sp.addOutParameter (ParameterConstants.TYPE_STRING);

        // insert all joins to the group
        for (int k = 0; k < oidStrings.length; k++)
        {
            currOid.setValue (oidStrings[k]);

            IOHelpers.showProcCall (this, sp, this.sess, this.env);
            // execute stored procedure
            try
            {
                // open db connection -  only workaround - db connection must
                // be handled somewhere else
                action = this.getDBConnection ();
                // execute stored procedure - return value
                // gives right-information
                action.execStoredProc (sp);
                // end action
                action.end ();
            } // try
            catch (DBError e)
            {
                // an error occurred - show name and info
                IOHelpers.showMessage (e, this.app, this.sess, this.env);
            } // catch
            finally
            {
                this.releaseDBConnection (action);
            } // finally
        } // for k
    } // insertRelationshipToUsers


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
            // reduced list
            // set headings:
            this.headings = MultilingualTextProvider.getText (
                BOTokens.TOK_BUNDLE, BOListConstants.LST_HEADINGS_GROUP_REDUCED, env);
            
            // set ordering attributes for the corresponding headings:
            this.orderings = BOListConstants.LST_ORDERINGS_GROUP_REDUCED;
        } // if
        else
        {
            // set headings:
            this.headings = MultilingualTextProvider.getText (
                BOTokens.TOK_BUNDLE, BOListConstants.LST_HEADINGS_GROUP, env);

            // set ordering attributes for the corresponding headings:
            this.orderings = BOListConstants.LST_ORDERINGS_GROUP;
        } // else

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings

} // class Group_01

/*
 * Class: RightsContainer_01.java
 */

// package:
package ibs.obj.user;

// imports:
//KR TODO: unsauber
import ibs.app.AppFunctions;
import ibs.app.CssConstants;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOListConstants;
import ibs.bo.BOMessages;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.OID;
import ibs.bo.ObjectClassNotFoundException;
import ibs.bo.ObjectInitializeException;
import ibs.bo.ObjectNotAffectedException;
import ibs.bo.Operations;
import ibs.bo.States;
import ibs.bo.type.TypeConstants;
import ibs.bo.type.TypeNotFoundException;
import ibs.di.KeyMapper.ExternalKey;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.ml.MultilangConstants;
import ibs.ml.MultilingualTextInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.BlankElement;
import ibs.tech.html.BuildException;
import ibs.tech.html.DivElement;
import ibs.tech.html.FormElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.ImageElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.NewLineElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.SpanElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.UtilExceptions;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;


/******************************************************************************
 * This class represents one object of type RigthsContainer with version 01. <BR/>
 *
 * @version     $Id: RightsContainer_01.java,v 1.57 2013/01/16 16:14:10 btatzmann Exp $
 *
 * @author      Keim Christine (CK), 980615
 ******************************************************************************
 */
public class RightsContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: RightsContainer_01.java,v 1.57 2013/01/16 16:14:10 btatzmann Exp $";

    /**  
     * Tabcode for the rights TAB
     */
    public static final String TABCODE_RIGHTS = "TA_Rights";
    
    /*
     * attributes for the changelistform
     */
    /**
     *
     */
    StringBuffer usersFilter = new StringBuffer ();
    /**
     *
     */
    StringBuffer groupsFilter = new StringBuffer ();
    /**
     *
     */
    int group = -1;
    /**
     *
     */
    String [] addReceiversArray = null;
    /**
     *
     */
    String [] rmReceiversArray = null;
    /**
     *
     */
    String addReceivers = null;

    /**
     * state of master - object
     */
    BusinessObject master = null;


    /**************************************************************************
     * This constructor creates a new instance of the class RightsContainer_01.
     * <BR/>
     */
    public RightsContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        this.procRetrieve = "p_RightsContainer_01$retrieve";
        this.viewContent = "v_RightsContainer_01$content";
    } // RightsContainer_01


    /**************************************************************************
     * Creates a RightsContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public RightsContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        this.procRetrieve = "p_RightsContainer_01$retrieve";
        this.viewContent = "v_RightsContainer_01$content";
    } // RightsContainer_01


    /**************************************************************************
     * Represent the content of the Container, i.e. its elements, to the user.
     * <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   orderBy             Property, by which the result is
     *                              sorted.
     * @param   orderHow            Kind of ordering: BOConstants.ORDER_ASC or
     *                              BOConstants.ORDER_DESC
     *                              null => BOConstants.ORDER_ASC
     */
    public void performShowContent (int representationForm, int orderBy,
                                    String orderHow)
    {
        RightsContainerElement_01 actElem;       // actual object
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

        // set the name of the object as multilang name for fallback
        String mlName = this.name;
        // try to get a translation for the rights tab with the TAB code
        MultilingualTextInfo mlNameInfo = MultilingualTextProvider.getMultilingualTextInfo (
            MultilangConstants.RESOURCE_BUNDLE_TABS_NAME,
            MultilingualTextProvider.getNameLookupKey (TABCODE_RIGHTS),
            MultilingualTextProvider.getUserLocale (env),
            env);            

        // do we have a translation for the rights tab
        if (mlNameInfo.isFound ())
        {
            mlName = mlNameInfo.getMLValue ();
        } // if

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
        
        if (this.isTab ())
        {
            body = this.createHeader (page, MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                this.headingName, new String[] {mlName}, this.env), this.getNavItems (),
                containerMlName, null, this.icon, containerMlName, false,
                size);
        } // if
        else
        {
            body = this.createHeader (page, MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                this.headingName, new String[] {mlName}, this.env), this.getNavItems (), null, null,
                this.icon, containerMlName, false, size);
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
                // get the actual element and represent it to the user:
                actElem = (RightsContainerElement_01) this.elements.elementAt (i);
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

        Rights_01.showLegend (page,
            this.getUserInfo ().userProfile.showExtendedRights, this.env);

        if (!this.getUserInfo ().userProfile.showExtendedRights)
        {
            // show legend for tokens in rightslist
            String delim = " ... ";
            DivElement div = new DivElement ();
            div.alignment = IOConstants.ALIGN_CENTER;

            TableElement t = new TableElement (2);
            div.addElement (t);
            RowElement r = new RowElement (2);

            // set classIds for table
            t.classId = CssConstants.CLASS_LEGEND;
            String[] classIds = new String[2];
            classIds[0] = CssConstants.CLASS_COLRIGHT;
            classIds[1] = CssConstants.CLASS_COLRIGHTDESCRIPTION;
            t.classIds = classIds;

            // token 'x'
            TableDataElement td = new TableDataElement (new TextElement (
                "'" + MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_FULLRIGHTSHORT, env) + "'" + delim));
            td.classId = CssConstants.CLASS_TDRIGHT;
            r.addElement (td);
            td = new TableDataElement (new TextElement (
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_FULLRIGHTDESCRIPTION, env)));
            r.addElement (td);
            t.addElement (r);
            r = new RowElement (2);
            // token 'p'
            td = new TableDataElement (new TextElement (
                "'" + MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_HALFRIGHTSHORT, env) + "'" + delim));
            td.classId = CssConstants.CLASS_TDRIGHT;
            r.addElement (td);
            td = new TableDataElement (new TextElement (
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_HALFRIGHTDESCRIPTION, env)));
            r.addElement (td);
            t.addElement (r);
            r = new RowElement (2);

            // token ' '
            td = new TableDataElement (new TextElement (
                "'" + UserTokens.TOK_NORIGHTSHORT + "'" + delim));
            td.classId = CssConstants.CLASS_TDRIGHT;
            r.addElement (td);
            td = new TableDataElement (new TextElement (
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_NORIGHTDESCRIPTION, env)));
            r.addElement (td);
            t.addElement (r);

            page.body.addElement (div);
        } // if show rightaliases

        if (this.p_isShowCommonScript)
        {
            // create the script to be executed on client:
            ScriptElement script = this.getCommonScript (true);
            page.body.addElement (script);
        } // if

        NewLineElement nl = new NewLineElement ();
        page.body.addElement (nl);

        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e.getMsg (), this.app, this.sess, this.env);
        } // catch
    } // performShowContent


    /***************************************************************************
     * Get the Container's content out of the database. <BR/>
     * First this method tries to load the objects from the database. During
     * this operation a rights check is done, too. If this is all right the
     * objects are returned otherwise an exception is raised. <BR/>
     *
     * @param   operation        Operation to be performed with the objects.
     * @param   orderBy          Property, by which the result shall be
     *                           sorted. If this parameter is null the
     *                           default order is by name.
     * @param   orderHow         Kind of ordering: BOConstants.ORDER_ASC or
     *                           BOConstants.ORDER_DESC null =>
     *                           BOConstants.ORDER_ASC
     * @param   selectedElements object ids that are marked for paste
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
        this.debug (" ---- RightsContainer_01.performRetrieveContentData ANFANG ----");
        int operationLocal = operation; // variable for local assignments
        String orderHowLocal = orderHow; // variable for local assignments
        SQLAction action = null;        // SQLAction for Databaseoperation
        RightsContainerElement_01 obj;
        StringBuffer queryStr;          // the query string

/* ****************** */
/* momentane Lösung   */
/* ****************** */
        operationLocal = Operations.OP_VIEWRIGHTS;

        // row counter
        int rowCount;
        // ensure a correct ordering:
        if (!orderHowLocal.equalsIgnoreCase (BOConstants.ORDER_DESC))
                                        // not descending?
        {
            orderHowLocal = BOConstants.ORDER_ASC; // order ascending
        } // if
        // empty the elements vector:
        this.elements.removeAllElements ();
        // get the elements out of the database:
        // create the SQL String to select all tuples

        queryStr = new StringBuffer ()
            .append ("SELECT DISTINCT oid, state, pTypeName, rOid, pOid, pName,")
            .append (" rPersonId, rRights")
            .append (" FROM    ").append (this.viewContent)
            .append (" WHERE   rOid = ").append (this.oid.toStringQu ())
            .append (" AND userId = ").append (this.user.id)
            .append (SQLHelpers.getStringCheckRights (operationLocal))
            .append (SQLHelpers.getStringCheckRights (Operations.OP_VIEW, "pRights"))
            .append (" ORDER BY ").append (this.orderings[orderBy])
            .append (" ").append (orderHowLocal);
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
            while (!action.getEOF ())
            {
                // create a new object:
                obj = new RightsContainerElement_01 ();

                // get and set values for element
                obj.oid = SQLHelpers.getQuOidValue (action, "oid");
                obj.state = action.getInt ("state");
                obj.typeName = action.getString ("pTypeName");
                obj.rOid = SQLHelpers.getQuOidValue (action, "rOid");
                obj.rPersonOid = SQLHelpers.getQuOidValue (action, "pOid");
                obj.rPersonName = action.getString ("pName");
                obj.rPersonId = action.getInt ("rPersonId");
                obj.rights = action.getInt ("rRights");
                obj.showExtendedRights =
                    this.getUserInfo ().userProfile.showExtendedRights;
                if ((this.sess != null) && (this.sess.activeLayout != null))
                {
                    obj.layoutpath = this.sess.activeLayout.path;
                } // if
                // add element to list of elements
                this.elements.addElement (obj);

                // step one tuple ahead for the next loop
                action.next ();
            } // while
            // the last tuple has been processed
            // end transaction
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (dbErr, this.app, this.sess, this.env);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround -
            // db connection must be handled somewhere else
            this.releaseDBConnection (action);
        } // finally
    } // performRetrieveContentData


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <CODE>ibs.bo.BusinessObject.env</CODE> property is used
     * for getting the parameters. This property must be set before calling
     * this method. <BR/>
     */
    public void getParameters ()
    {
        this.debug ("---- RightsContainer_01.getParameters ANFANG ----");
        String button;
        String str = null;
        int num = 0;
        RightsContainerElement_01 element = new RightsContainerElement_01 ();
        String [] setRights;
        int index = 0;

        // get parameters relevant for super class:
        super.getParameters ();


        // check if the SET GROUP FILTER button has been pressed
        button = this.env.getStringParam (BOArguments.ARG_SETGROUPSFILTER);
        if (button != null && !button.isEmpty ())
        {
            this.groupsFilter = new StringBuffer ().append (this.env
                .getStringParam (BOArguments.ARG_GROUPSFILTER));
            // a new Filter has to be applied, a connection to the DB is
            // necessary to get the new groups and save them in the Vector in
            // the session
            this.sess.groups = this.createGroupsVector (this.groupsFilter);
        } // if
        else                            // groups filter button not pressed
        {
            // try to get active groups filter:
            if ((str = this.env
                .getStringParam (BOArguments.ARG_ACTIVEGROUPSFILTER)) != null)
                                        // got active groups filter?
            {
                this.groupsFilter = new StringBuffer (str);
            } // if got active groups filter
        } // else groups filter button not pressed

        // check if groups filter is valid:
        if (this.groupsFilter == null)  // filter not valid?
        {
            this.groupsFilter = new StringBuffer ();
        } // if filter not valid

//debug (" set users filter ");
        // check if the SET USERS FILTER button has been pressed
        button = this.env.getStringParam (BOArguments.ARG_SETUSERSFILTER);
        if (button != null && !button.isEmpty ())
        {
            this.usersFilter = new StringBuffer ().append (this.env
                .getStringParam (BOArguments.ARG_USERSFILTER));
            // if a new filter has to be applied for the users, a connection to
            // the DB is necessary to get the new users and save them in the
            // Vector in the session
            // to do this, the active group has to be retrieved first.
            if ((num = this.env.getIntParam (BOArguments.ARG_ACTIVEGROUP)) !=
                IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
            {
                this.group = num;
            } // if
            this.sess.users =
                this.createUsersVector ("" + this.group, this.usersFilter);
        } // if
        else                            // users filter button not pressed
        {
            // try to get active users filter:
            if ((str = this.env
                .getStringParam (BOArguments.ARG_ACTIVEUSERSFILTER)) != null)
                                        // got active groups filter?
            {
                this.usersFilter = new StringBuffer (str);
            } // if got active groups filter
        } // else users filter button not pressed

        // check if users filter is valid:
        if (this.usersFilter == null)   // filter not valid?
        {
            this.usersFilter = new StringBuffer ();
        } // if filter not valid

//debug (" set group ");
        // check if the SET GROUP button has been pressed
        button = this.env.getStringParam (BOArguments.ARG_SETGROUPS);
        this.debug ("button = " + button);
        if (button != null && !button.isEmpty ())
        {
            this.group = this.env.getIntParam (BOArguments.ARG_GROUPS);
            this.debug ("group = " + this.group);
            // if a new group was chosen, then the users have to be retreived
            // again and stored in the session of course
            this.sess.users =
                this.createUsersVector ("" + this.group, this.usersFilter);
        } // if
        else
        {
            if ((num = this.env.getIntParam (BOArguments.ARG_ACTIVEGROUP)) !=
                IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
            {
                this.group = num;
            } // if
        } // else

        // check if the ADD USER button has been pressed
        button = this.env.getStringParam (BOArguments.ARG_ADD);
        if (button != null && !button.isEmpty ())
        {
            this.addReceiversArray =
                this.env.getMultipleParam (BOArguments.ARG_USERS);
        } // if
        else
        {
            this.addReceiversArray = null;
        } // else

//debug (" remove user ");
        // check if the REMOVE USERS button has been pressed
        button = this.env.getStringParam (BOArguments.ARG_REMOVE);
        if (button != null && !button.isEmpty ())
        {
            this.rmReceiversArray =
                this.env.getMultipleParam (BOArguments.ARG_DELLIST);
        } // if
        else
        {
            this.rmReceiversArray = null;
        } // else

        // check if the ADD GROUPS button has been pressed
        button = this.env.getStringParam (UserArguments.ARG_SELGROUPS);
        if (button != null && !button.isEmpty ())
        {
            this.addReceivers = this.env.getStringParam (BOArguments.ARG_GROUPS);
        } // if
        else
        {
            this.addReceivers = null;
        } // else

        // get parameters of different containerelements:
        for (Iterator<RightsContainerElement_01> iter =
                this.sess.rights.values ().iterator (); iter.hasNext ();)
        {
            element = iter.next ();
            // delete rights of element
            element.rights = 0;
            // get checked checkboxes  (rights)
            setRights = this.env.getMultipleParam (BOArguments.ARG_CHANGELIST +
                    element.rPersonId);

            // set rights or not ??
            if (setRights != null)
            {
                if (StringHelpers.findString (setRights,
                    UserArguments.ARG_NOCHANGE) < 0)
                {
                    element.noChangeChecked = false;
                    // set all checked rights
                    for (int i = 0; i < setRights.length; i++)
                    {
                        if (this.getUserInfo ().userProfile.showExtendedRights)
                        {
                            index = StringHelpers.findString (
                            	Operations.OP_SHORTNAMES, setRights[i]);
                            element.rights =
                                element.rights | Operations.OP_IDS[index];
                        } // if
                        else
                        {
                            // check which right should be set
                            index = StringHelpers.findString (
                                UserArguments.ARG_RIGHTALIASES, setRights[i]);
                            element.rights =
                                element.rights |
                                UserConstants.RIGHTALIASES[index];
                        } // else show rightaliases
                    } // for
                } // if change rights
                else   // do not change rights
                {
                    element.noChangeChecked = true;
                    for (StringTokenizer oldRights =
                            new StringTokenizer (element.rightsString, ";");
                        oldRights.hasMoreTokens ();)
                    {
                        // TODO RB: Will this work with MLI support??
                        index = StringHelpers.findString (
                            MultilingualTextProvider.getText (Operations.TOK_BUNDLE, 
                                Operations.OP_SHORTNAMES, env), oldRights.nextToken ());
                        element.rights =
                            element.rights | Operations.OP_IDS[index];
                    } // for
                } // else do not change rights
            } // if setRights != null
        } // for iter
        this.debug ("---- RightsContainer_01.getParameters ENDE ----");
    } // getParameters


    /**************************************************************************
     * Represent list content with its properties, to the user within a change
     * form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showListChangeForm (int representationForm)
    {
        String str;

        if ((str = this.env.getStringParam (BOArguments.ARG_NAME)) != null)
        {
            this.name = str;
        } // if

        String initForm = this.env.getParam (BOArguments.ARG_INITFORM);
        // check if the form has just been initialized
        if (initForm == null || initForm.isEmpty ())
        {
            // clear all used lists
            this.sess.groups = new Vector<String[]> ();
            this.sess.users = new Vector<String[]> ();
            this.sess.receivers = new Vector<String[]> ();
            this.sess.rights = new Hashtable<String, RightsContainerElement_01> ();

            // the groups have to be retreived from the db and
            // stored in the vector in the session
            this.sess.groups = this.createGroupsVector (null);

            // fill rights vector with current Rights on master object
            try
            {
                // set the headings and orderings for the listchangeform
                this.setHeadingsAndOrderings ();
/* KR 020125: not necessary because already done before
                // get object properties from the db
                retrieve (Operations.OP_VIEWELEMS);
*/
                // try to retrieve the content of this container:
                this.performRetrieveContentData (Operations.OP_VIEW |
                    Operations.OP_CHANGE | Operations.OP_DELETE, this.orderBy,
                    this.orderHow);

                // store partial rights if right aliases are used:
                if (!this.getUserInfo ().userProfile.showExtendedRights)
                {
                    // store retrieved rights in rightsString
                    RightsContainerElement_01 element;
                    for (int i = 0; i < this.elements.size (); i++)
                    {
                        element = (RightsContainerElement_01)
                            this.elements.elementAt (i);
                        this.storeRights (element);
                    } // for
                } // if right - aliases
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

            // fill the Hash and fill the vector with the rights
            // which are active now for this object
            for (int i = 0; i < this.elements.size (); i++)
            {
                RightsContainerElement_01 temp =
                    (RightsContainerElement_01) this.elements.elementAt (i);

                String[] t = new String[2];
                t[0] = temp.rPersonName;
                t[1] = "" + temp.rPersonId;
                this.sess.receivers.addElement (t);
                this.debug (" this.sess.rights.put (" + temp.rPersonName + ")");
                this.sess.rights.put ("" + temp.rPersonId, temp);
            } // for
        } //if

        // perform actions on changeform
        this.handleListChangeForm ();
        // show change form
        this.performShowListChangeForm (representationForm);
        this.debug ("---- RightsContainer_01.showListChangeForm ENDE ----");
    } // showListChangeForm


    /**************************************************************************
     * Stores the rights of one rightscontainerelement in a string of
     * shortnames of the rights. <BR/>
     *
     * @param   element rightscontainerelement wich rights should be stored
     */
    protected void storeRights (RightsContainerElement_01 element)
    {
        // check if element has partial rights:
        boolean partialRights = false;
        for (int j = 0; j < UserConstants.RIGHTALIASES.length; j++)
        {
            if ((element.rights & UserConstants.RIGHTALIASES[j]) !=
                UserConstants.RIGHTALIASES[j] &&
                (element.rights & UserConstants.RIGHTALIASES[j]) > 0)
            {
                // one rightaliases is only half set
                partialRights = true;
            } // if
        } // for

        if (partialRights)
        {
            // check checkbox 'noChange'
            element.noChangeChecked = true;
            // store oldRights into String
            for (int j = 0; j < Operations.OP_IDS.length; j++)
            {
                if ((element.rights & Operations.OP_IDS[j]) ==
                    Operations.OP_IDS[j])
                {
                    if (element.rightsString != null)
                    {
                        element.rightsString += ";";
                    } // if
                    else
                    {
                        element.rightsString = new String ("");
                    } // else
                    element.rightsString += MultilingualTextProvider.getText (
                        Operations.TOK_BUNDLE, Operations.OP_SHORTNAMES[j], env);
                } // if
            } // for
        } // if partiellRights
    } // storeRights


    /**************************************************************************
     * Handle the changes on the listchangeform. <BR/>
     */
    protected void handleListChangeForm ()
    {
        int i;
        RightsContainerElement_01 rightPosition;

        // get the active receivers/rights out of the session objects
        Vector<String[]> receivers = this.sess.receivers;
        Hashtable<String, RightsContainerElement_01> rights = this.sess.rights;

        // add rights for user
        if (this.addReceiversArray != null)
        {
            // loop through the array of users to be added
            for (i = 0; i < this.addReceiversArray.length; i++)
            {
                // check if entry is not empty
                if (!this.addReceiversArray[i].isEmpty ())
                {
                    // check if the user does already exist in the vector
                    if (!rights.containsKey (this.addReceiversArray[i]))
                    {
                        // if not, add the user to the recievers vector
                        // retrieve the right for the specific personId
///////////////////////////////////////////////
//
// START HACK: PERFORMANCE (HP)
//
/*
                        rightPosition = retrieveRightPosition (
                            (new Integer (addReceiversArray[i])).intValue ());
*/
                        rightPosition = this.retrieveRightPosition (
                            (new Integer (this.addReceiversArray[i])).intValue ()
                            , "USER");
//
// END HACK: PERFORMANCE (HP)
//
///////////////////////////////////////////////

                        // set layoutpath for viewing object (icons, ...)
                        if ((this.sess != null) &&
                            (this.sess.activeLayout != null))
                        {
                            rightPosition.layoutpath =
                                this.sess.activeLayout.path;
                        } // if

                        rights.put (this.addReceiversArray[i], rightPosition);

                        // must put in vector also!
                        String[] t = new String[2];
                        t[0] = rightPosition.rPersonName;
                        t[1] = "" + rightPosition.rPersonId;
                        receivers.addElement (t);
                    } // if
                } // if
            } // for
            // store the new receivers hashtable in the session object
            this.sess.rights = rights;
            this.sess.receivers = receivers;
        } // if (addReceiversArray != null)

        // add rights for group
        if (this.addReceivers != null && !rights.containsKey (this.addReceivers))
        {
            // retrieve the right for the specific personId
///////////////////////////////////////////////
//
// START HACK: PERFORMANCE (HP)
//
/*
            rightPosition = retrieveRightPosition (
                (new Integer (addReceiversArray[i])).intValue ());
*/
            rightPosition = this.retrieveRightPosition ((new Integer (
                this.addReceivers)).intValue (), "GROUP");
//
// END HACK: PERFORMANCE (HP)
//
///////////////////////////////////////////////

            rights.put (this.addReceivers, rightPosition);
            // must put in vector also!
            String[] t = new String [2];
            t[0] = rightPosition.rPersonName;
            t[1] = "" + rightPosition.rPersonId;
            receivers.addElement (t);
            // store the new receivers hashtable in the session object
            this.sess.rights = rights;
            this.sess.receivers = receivers;
        } // if

        // remove rights
        if (this.rmReceiversArray != null)
        {
            // loop through the array of users to be removed
            for (i = 0; i < this.rmReceiversArray.length; i++)
            {
                if (rights.remove (this.rmReceiversArray[i]) != null)
                {
                    // the object has been actually removed
                    // remove from Vector also!
                    // get the corresponding object
                    for (Iterator<String[]> iter = this.sess.receivers.iterator ();
                         iter.hasNext ();)
                    {
                        String[] t = iter.next ();
                        if (t != null &&
                            t[1].equalsIgnoreCase (this.rmReceiversArray[i]))
                        {
                            // remove the object from the receivers Vector
                            // in the session
                            receivers.removeElement (t);
                            break;
                        } // if
                    } // for iter
                } // if
            } // for
            //store the new rights hashtable in the session object
            this.sess.rights = rights;
            this.sess.receivers = receivers;
        } // if (rmReceiversArray != null)
    } // handleListChangeForm


    /**************************************************************************
     * Represent list content with its properties, to the user within a change
     * form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    protected void performShowListChangeForm (int representationForm)
    {
        ImageElement img;
        LinkElement link;
        String initForm = this.env.getParam (BOArguments.ARG_INITFORM);

        // init variables for page
        Page page = new Page ("NewRights", false);

        // Stylesheetfile wird geladen
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" + this.sess.activeLayout.elems[LayoutConstants.SHEETLIST].styleSheet;
        page.head.addElement (style);
        style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
             "/" + this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);

        // create header;
        FormElement form = this.createFormHeader (page, this.containerName,
            this.getNavItems (), null, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_FCTNEWRIGHTS, env),
            null, "Rights.gif");

        // set form parameters:
        form.addElement (new InputElement (
            BOArguments.ARG_OID, InputElement.INP_HIDDEN, "" + this.oid));

        // check if the form has just been initialized
        if (initForm == null || initForm.isEmpty ())
        {
            form.addElement (new InputElement (UserArguments.ARG_ROID,
                InputElement.INP_HIDDEN, "" + this.containerId));
        } // if
        else
        {
            form.addElement (new InputElement (UserArguments.ARG_ROID,
                InputElement.INP_HIDDEN, this.env
                    .getParam (UserArguments.ARG_ROID)));
        } // else

/* BB 20060718: this is messing up the name of the object
        form.addElement (new InputElement (BOArguments.ARG_NAME,
            InputElement.INP_HIDDEN, this.name) );
*/
        form.addElement (new InputElement (BOArguments.ARG_FUNCTION,
            InputElement.INP_HIDDEN, "" + AppFunctions.FCT_FORWARDTABVIEWEVENT));
        form.addElement (new InputElement (BOArguments.ARG_TABID,
            InputElement.INP_HIDDEN, "" + this.p_tabId));
        form.addElement (new InputElement (BOArguments.ARG_EVENT,
            InputElement.INP_HIDDEN, "" + AppFunctions.FCT_LISTCHANGEFORM));
        form.addElement (new InputElement (BOArguments.ARG_TYPE,
            InputElement.INP_HIDDEN,
            "" + Integer.toString (this.getTypeCache ().getTVersionId (TypeConstants.TC_Rights))));

        // set filter arguments
        form.addElement (new InputElement (BOArguments.ARG_ACTIVEUSERSFILTER,
            InputElement.INP_HIDDEN, this.usersFilter.toString ()));
        form.addElement (new InputElement (BOArguments.ARG_ACTIVEGROUPSFILTER,
            InputElement.INP_HIDDEN, this.groupsFilter.toString ()));
        form.addElement (new InputElement (BOArguments.ARG_ACTIVEGROUP,
            InputElement.INP_HIDDEN, "" + this.group));
        // indicate that form has been initialized
        form.addElement (new InputElement (BOArguments.ARG_INITFORM,
            InputElement.INP_HIDDEN, "1"));

        // construct inner table
        TableElement table =  new TableElement ();
        table.classId = CssConstants.CLASS_INFO;

        table.border = 0;
        table.ruletype = IOConstants.RULE_NONE;
//        table.frametypes = IOConstants.FRAME_BOX;
        table.width = HtmlConstants.TAV_FULLWIDTH;
        table.cellpadding = 5;

        // init variables for table
        RowElement tr;
        TableDataElement td;
        NewLineElement nl = new NewLineElement ();
        InputElement iel;
        GroupElement gel;
        SpanElement span;

        // ---------------- first row -------------------------------
        tr = new RowElement (3);

        tr.classId = CssConstants.CLASS_SELECT;

        // add groups selection box
        gel = new GroupElement ();
        span = new SpanElement ();
        span.classId = CssConstants.CLASS_NAME;
        span.addElement (new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_GROUPS, env)));
        gel.addElement (span);

        // editfield and button 'Filter setzen'
        gel.addElement (nl);
        iel = new InputElement (BOArguments.ARG_GROUPSFILTER,
            InputElement.INP_TEXT, this.groupsFilter.toString ());
        iel.size = 10;
        iel.maxlength = 10;
        gel.addElement (iel);

        iel = new InputElement (BOArguments.ARG_SETGROUPSFILTER,
            InputElement.INP_SUBMIT, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SETFILTER, env));
        gel.addElement (iel);
        gel.addElement (nl);

        // generate groups selection box
        // from the vector groups in the session
        gel.addElement (this.createSelectionBox
            (BOArguments.ARG_GROUPS, this.sess.groups, "" + this.group));
        td = new TableDataElement (gel);
        tr.addElement (td);

        // arrow - button to select groups into user selectionbox
        iel = new InputElement (BOArguments.ARG_SETGROUPS,
            InputElement.INP_HIDDEN, "");
        gel.addElement (iel);
        img = new ImageElement (this.sess.activeLayout.path +
            BOPathConstants.PATH_ARROWICONS + "arrow_right.jpg");
        img.alt = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_SETGROUP, env);

        link = new LinkElement (img, IOConstants.URL_JAVASCRIPT +
            HtmlConstants.JREF_SHEETFORM +
            BOArguments.ARG_SETGROUPS + HtmlConstants.JREF_VALUEASSIGN + "'1';" +
            HtmlConstants.JREF_SHEETFORMSUBMIT);
        td = new TableDataElement (link);
        td.valign = IOConstants.ALIGN_MIDDLE;
        tr.addElement (td);

        // add users and roles selection box
        gel = new GroupElement ();

        span = new SpanElement ();
        span.classId = CssConstants.CLASS_NAME;
        span.addElement (new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_USERS, env)));
        gel.addElement (span);

        // editfield and button 'Filter setzen'
        gel.addElement (nl);
        iel = new InputElement (BOArguments.ARG_USERSFILTER,
            InputElement.INP_TEXT, this.usersFilter.toString ());
        iel.size = 10;
        iel.maxlength = 10;
        gel.addElement (iel);
        iel = new InputElement (BOArguments.ARG_SETUSERSFILTER,
            InputElement.INP_SUBMIT, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SETFILTER, env));
        gel.addElement (iel);
        gel.addElement (nl);

        // generate the users selection box
        gel.addElement (this.createSelectionBox (BOArguments.ARG_USERS,
            this.sess.users, this.sess.receivers));
        td = new TableDataElement (gel);
        tr.addElement (td);
        table.addElement (tr);

        // ------------------  second row  (arrow - buttons)  -----------------
        tr = new RowElement (3);

        // button add group to rights
        iel = new InputElement (UserArguments.ARG_SELGROUPS,
            InputElement.INP_HIDDEN, "");
        gel.addElement (iel);
        img = new ImageElement (this.sess.activeLayout.path +
            BOPathConstants.PATH_ARROWICONS + "arrow_down.jpg");
        img.alt = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_ASSIGNGROUP, env);
        link = new LinkElement (img, IOConstants.URL_JAVASCRIPT +
            HtmlConstants.JREF_SHEETFORM +
            UserArguments.ARG_SELGROUPS + HtmlConstants.JREF_VALUEASSIGN + "'1';" +
            HtmlConstants.JREF_SHEETFORMSUBMIT);
        td = new TableDataElement (link);
        td.alignment = IOConstants.ALIGN_CENTER;
        tr.addElement (td);

        td = new TableDataElement (new BlankElement ());
        tr.addElement (td);

        // button add user to rights
        iel = new InputElement (BOArguments.ARG_ADD,
            InputElement.INP_HIDDEN, "");
        gel.addElement (iel);
        img = new ImageElement (this.sess.activeLayout.path +
            BOPathConstants.PATH_ARROWICONS + "arrow_down.jpg");
        img.alt = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_ASSIGNUSER, env);
        link = new LinkElement (img, IOConstants.URL_JAVASCRIPT +
            HtmlConstants.JREF_SHEETFORM +
            BOArguments.ARG_ADD + HtmlConstants.JREF_VALUEASSIGN + "'1';" +
            HtmlConstants.JREF_SHEETFORMSUBMIT);
        td = new TableDataElement (link);
        td.alignment = IOConstants.ALIGN_CENTER;
        tr.addElement (td);
        table.addElement (tr);

        // ---------------------  third row -------------------
        tr = new RowElement (1);
        // reciever selection box

        // -----------------------------  show rights -------------------------
        this.showRightPositions (representationForm, table);

        // ----------------------------- show remove button -------------------
        // button remove rights
        InputElement iE = new InputElement (BOArguments.ARG_REMOVE,
            InputElement.INP_SUBMIT, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_REMOVERIGHT, env));
        td = new TableDataElement (iE);
        td.alignment = IOConstants.ALIGN_LEFT;
        tr.addElement (td);
        table.addElement (tr);

        form.addElement (table);

        // create footer:
        if (this.master.state == States.ST_CREATED)
        {
            this.createFormFooter (form, "this.form." + BOArguments.ARG_EVENT +
                ".value = '" + AppFunctions.FCT_LISTCHANGE + "';",
                "top.goback (1);");
        } // if master was just created
        else
        {
            this.createFormFooter (form, "this.form." + BOArguments.ARG_EVENT +
                ".value = '" + AppFunctions.FCT_LISTCHANGE + "';");
        } // else master is active

        Rights_01.showLegend (
            page, this.getUserInfo ().userProfile.showExtendedRights, this.env);

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e.getMsg (), this.app, this.sess, this.env);
        } // catch
    } // performShowListChangeForm


    /**************************************************************************
     * Generates a groups Vector with all groups available. <BR/>
     *
     * @param   groupsFilter   filter for the query
     *
     * @return  The groups vector.
     */
    private Vector<String[]> createGroupsVector (StringBuffer groupsFilter)
    {
        StringBuffer queryStr;          // the query string
        StringBuffer conditionStr;      // the condition

        // create the SQL String to select all tuples
        // workaround: there are no right checks done
        queryStr = new StringBuffer ()
            .append ("SELECT DISTINCT u.id AS id, u.name AS name")
            .append (" FROM v_GroupContainer_01$content u")
            .append (" WHERE u.domainId = ").append (this.getUser ().domain)
            .append (SQLHelpers.getStringCheckRights (Operations.OP_VIEW))
            .append (" AND userId  = ").append (this.getUser ().id);

        // check if filter has to be set:
        if (groupsFilter != null && groupsFilter.length () != 0)
        {
            conditionStr = SQLHelpers.getQueryConditionString (
                new StringBuffer ("u.name"), SQLConstants.MATCH_SUBSTRING,
                groupsFilter, true);

            // check if we found a valid condition:
            if (conditionStr != null)
            {
                queryStr.append (" AND ").append (conditionStr);
            } // if
        } // if
        queryStr.append (" ORDER BY name ASC ");
//debug ("createGroupsVector: " + queryStr);

        // execute the query and return the result:
        return this.createVectorFromQuery (queryStr);
    } // createGroupsVector


    /**************************************************************************
     * Generates a Vector with all users belonging to a certain group. <BR/>
     * In case there has no group id be specified all users will be listed
     *
     * @param   groupID     Id of a group (null ... list all users).
     * @param   usersFilter Filterstring for the query.
     *
     * @return  The users vector.
     */
    protected Vector<String[]> createUsersVector (String groupID,
                                        StringBuffer usersFilter)
    {
        StringBuffer queryStr;          // the query string
        StringBuffer conditionStr;      // the condition

        // create queryString
        if (groupID != null && !groupID.isEmpty ()) // group set?
        {
////////////////////////////
//
// HP - Tuned: removed rights-check and avoided v_Group_01$rights-View
//
            queryStr = new StringBuffer ()
                .append ("SELECT DISTINCT u.id AS id, u.name AS name")
                .append (" FROM   ibs_GroupUser gu, ibs_User u, ibs_Object o")
                .append (" WHERE  gu.groupId = ").append (groupID)
                .append ("   AND  gu.userId = u.id")
                .append ("   AND  u.oid = o.oid")
                .append ("   AND  o.state = ").append (States.ST_ACTIVE);
//                "SELECT  DISTINCT u.id, u.name" +
//                " FROM v_Group_01$rights u" +
//                " WHERE u.domainId = " + this.getUser ().domain +
//                getStringCheckRights (Operations.OP_VIEW) +
//                " AND    u.groupId = " + groupID +
//                " AND userId  =" + this.getUser ().id +
//                " AND tVersionId = " + getCache ().getTVersionId (TypeConstants.TC_User);
//
////////////////////////////

            // check if a name filter has been set
            if (usersFilter != null && usersFilter.length () != 0)
            {
                conditionStr = SQLHelpers.getQueryConditionString (
                    new StringBuffer ("u.name"), SQLConstants.MATCH_SUBSTRING,
                    usersFilter, true);

                // check if we found a valid condition:
                if (conditionStr != null)
                {
                    queryStr.append (" AND ").append (conditionStr);
                } // if
            } // if

            queryStr.append (" ORDER BY name ASC ");
//debug ("createUsersVector: " + queryStr);

            // execute the query and return the result:
            return this.createVectorFromQuery (queryStr);
        } // if group set

        return new Vector<String[]> ();
    } // createUsersVector


    /**************************************************************************
     * Generates a Vector with all result tuples from the query. <BR/>
     * The query must at least contain the attributes <CODE>id</CODE> and
     * <CODE>name</CODE>. These are used to create a vector consisting of
     * {name, id} pairs where {name, id} is an array with two Strings.
     *
     * @param   queryStr    Filter for the query.
     *
     * @return  The result vector.
     */
    private Vector<String[]> createVectorFromQuery (StringBuffer queryStr)
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        Vector<String[]> result = new Vector<String[]> ();
        int rowCount;

        // open db connection -  only workaround - db connection must
        // be handled somewhere else
        action = this.getDBConnection ();

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            rowCount = action.execute (queryStr, false);

            // empty resultset?
            if (rowCount == 0)
            {
                return result;
            } // if
            // error while executing?
            else if (rowCount < 0)
            {
                return result;
            } // else if
            // everything ok - go on

            // get tuples out of db:
            while (!action.getEOF ())
            {
                // create entries in list
                String[] t = new String[2];
                t[0] = action.getString ("name");
                t[1] = action.getString ("id");
                // add this element to the result Vector
                result.addElement (t);
                // step one tuple ahead for the next loop
                action.next ();
            } // while

            // the last tuple has been processed
            // finish transaction:
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (dbErr, this.app, this.sess, this.env);
        } // catch
        finally
        {
            // close db connection in every case - only workaround -
            // db connection must be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        // return the final Vector
        return result;
    } // createVectorFromQuery


    /**************************************************************************
     * retrieve one rightPosition to show it on the new-right-form. <BR/>
     *
     * @param   rPersonId   Id of the person/user or the group for whom the
     *                      right counts.
     * @param   rPersonType type of rPerson -> can be USER or GROUP
     *
     * @return  RightsContainerElement_01 object if found
     *          <CODE>null</CODE> if not found.
     */
    private RightsContainerElement_01 retrieveRightPosition (int rPersonId,
                                                             String rPersonType)
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        StringBuffer queryStr;          // the query string

///////////////////////////////////////////////
//
// START HACK: PERFORMANCE (HP)
//  no rights check necessary at this place -> for selection of given
//  rightsposition (rPersonId) user must have had read-rights anyway
//
/*
        String queryStr =
            " SELECT DISTINCT oid, state, name AS pName," +
            " typeName AS pTypeName, icon, 0 AS rRights" +
            " FROM " + rightPositionView +
            " WHERE id = " + rPersonId;
*/
        // check if query shall be build for user or group
        if (rPersonType.equalsIgnoreCase ("USER"))
        {
            queryStr = new StringBuffer ()
                .append (" SELECT DISTINCT o.oid, o.state, o.name AS pName,")
                .append ("   o.typeName AS pTypeName, o.icon, 0 as rRights")
                .append (" FROM ibs_Object o, ibs_user p")
                .append (" WHERE o.oid = p.oid")
                .append (" AND   p.id = ").append (rPersonId);
        } // if
        else if (rPersonType.equalsIgnoreCase ("GROUP"))
        {
            queryStr = new StringBuffer ()
                .append (" SELECT DISTINCT o.oid, o.state, o.name AS pName,")
                .append ("   o.typeName AS pTypeName, o.icon, 0 as rRights")
                .append (" FROM ibs_Object o, ibs_group p")
                .append (" WHERE o.oid = p.oid")
                .append (" AND   p.id = ").append (rPersonId);
        } // else if
        else                            // error
        {
            return null;
        } // else error
//
// END HACK: PERFORMANCE (HP)
//
///////////////////////////////////////////////

//debug ("retrieveRightPosition: " + queryStr);

        RightsContainerElement_01 re = new RightsContainerElement_01 ();

        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();

            int rowCount = action.execute (queryStr, false);

            if (rowCount <= 0)
            {
                return null;
            } // if

            // get tuples out of db
            re.oid = this.oid;
            re.state = action.getInt ("state");
            re.typeName = action.getString ("pTypeName");
            re.rOid = this.oid;
            re.rPersonOid = SQLHelpers.getQuOidValue (action, "oid"); // oid
            re.rPersonName = action.getString ("pName"); // name
            re.rPersonId = rPersonId;
            re.rights = action.getInt ("rRights"); // rights;
            re.icon = action.getString ("icon");
            re.showExtendedRights =
                this.getUserInfo ().userProfile.showExtendedRights;

            // finish transaction:
            action.end ();
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (e, this.app, this.sess, this.env);
        } // catch
        finally
        {
            // close db connection in every case - only workaround -
            // db connection must be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        return re;
    } // retrieveRightPosition


    /**************************************************************************
     * show rights position on new-right form to user. <BR/>
     *
     * @param   representationForm  ???
     * @param   table               ???
     */
    private void showRightPositions (int representationForm, TableElement table)
    {
        RightsContainerElement_01 element;
        TableElement innerTable = new TableElement ();
        innerTable.classId = CssConstants.CLASS_LIST;
        RowElement tr;
        TableDataElement td;
        int i;

        // header
        if (this.getUserInfo ().userProfile.showExtendedRights)
        {
            tr = new RowElement (Operations.OP_SHORTNAMES.length + 2);
            tr.classId = CssConstants.CLASS_LISTHEADER;
            // create header
            td = new TableDataElement (new BlankElement ());
            td.classId = CssConstants.CLASS_LISTHEADER;
            tr.addElement (td);
            td = new TableDataElement (new TextElement (
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_LEGITIMATED, env)));
            td.classId = CssConstants.CLASS_LISTHEADER;
            tr.addElement (td);

            // display header
            for (i = 0; i < Operations.OP_SHORTNAMES.length; i++)
            {
                td = new TableDataElement (new TextElement (
                    MultilingualTextProvider.getText (
                        Operations.TOK_BUNDLE, Operations.OP_SHORTNAMES[i], env)));
                td.classId = CssConstants.CLASS_LISTHEADER;
                tr.addElement (td);
            } // for

            // add header to table:
            innerTable.addElement (tr);
        } // if
        else
        {
            tr =  new RowElement (UserTokens.ML_RIGHTALIASES.length + 2 + 1);
            tr.classId = CssConstants.CLASS_LISTHEADER;
            // create header
            td = new TableDataElement (new BlankElement ());
            td.classId = CssConstants.CLASS_LISTHEADER;
            tr.addElement (td);
            td = new TableDataElement (new TextElement (
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_LEGITIMATED, env)));
            td.classId = CssConstants.CLASS_LISTHEADER;
            tr.addElement (td);

            // display header
            for (i = 0; i < UserTokens.ML_RIGHTALIASES.length; i++)
            {
                td = new TableDataElement (new TextElement (
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_RIGHTALIASES[i], env)));
                td.classId = CssConstants.CLASS_LISTHEADER;
                tr.addElement (td);
            } // for

            td = new TableDataElement (new TextElement (
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_NOCHANGE, env)));
            td.classId = CssConstants.CLASS_LISTHEADER;
            tr.addElement (td);

            // add header to table
            innerTable.addElement (tr);
        } // else

        i = 0;
        //  go trough Hashtable
        for (Iterator<String> iter = this.sess.rights.keySet ().iterator ();
             iter.hasNext ();)
        {
            // create single rows:
            element = this.sess.rights.get (iter.next ());

            // set the layout path of the RightsContainerElement
            if ((this.sess != null) && (this.sess.activeLayout != null))
            {
                element.layoutpath = this.sess.activeLayout.path;
            } // if
            innerTable.addElement (element.showChangeFormSelection
                (BOListConstants.LST_CLASSROWS
                    [i % BOListConstants.LST_CLASSROWS.length]));
            i++;
        } // for iter

        innerTable.width = HtmlConstants.TAV_FULLWIDTH;
        td = new TableDataElement (innerTable);
        td.colspan = 3;
        tr = new RowElement (1);
        tr.addElement (td);
        table.addElement (tr);
    } // showRightPositions


    /**************************************************************************
     * Update the rights recursively. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void setRightsRec (int representationForm)
    {
        try
        {
            // set the rights recursively:
            this.performSetRightsRecData (Operations.OP_SETRIGHTS);

            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_SETRIGHTSREC_OK, this.env),
                this.app, this.sess, this.env);

            // show the object's data:
            this.show (representationForm);
        } // try
        catch (NoAccessException e) // no access to objects allowed
        {
            // send message to the user:
            this.showNoAccessMessage (Operations.OP_READ);
        } // catch
        catch (ObjectNotAffectedException e) // no access to objects allowed
        {
            // send message to the user:
            this.showObjectNotAffectedMessage ();
        } // catch
    } // setRightsRec


    /**************************************************************************
     * Change the rights of the subObjects of this Object. <BR/>
     * During this operation a rights check is done, too.
     * If this is all right the object is stored and this method terminates
     * otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ObjectNotAffectedException
     *              It was not possible to set the rights of all objects.
     */
    protected void performSetRightsRecData (int operation)
        throws NoAccessException, ObjectNotAffectedException
    {
        // define local variables:
        int retVal = UtilConstants.QRY_OK;            // return value of query

        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                "p_Rights$setRightsRecursive",
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // oid
        BOHelpers.addInParameter(sp, this.oid);

        // user id
        if (this.user != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
        } // else

        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, operation);

        // perform the function call:
        retVal = BOHelpers.performCallFunctionData (sp, this.env);

        // check the rights
        if (retVal == UtilConstants.QRY_INSUFFICIENTRIGHTS)
                                        // access not allowed?
        {
            // raise no access exception
            NoAccessException error =
                new NoAccessException (MultilingualTextProvider.getMessage (
                    UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_NOACCESSEXCEPTION, env));
            throw error;
        } // if access not allowed
        else if (retVal == UtilConstants.QRY_NOTALLAFFECTED)
        {
            // raise object(s) not affected exception
            ObjectNotAffectedException error =
                new ObjectNotAffectedException (MultilingualTextProvider.getMessage (
                    UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_OBJECTNOTAFFECTEDEXCEPTION, env));
            throw error;
        } // else if
        else                          // access allowed
        {
            // room for some success statements
        } // else access allowed
    } // performSetRightsRecData


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard
     * container. <BR/>
     */
    protected void setHeadingsAndOrderings ()
    {
        // constant order by attribute (there is only one in this container)
        this.orderBy = 0;

        if (this.getUserInfo ().userProfile.showExtendedRights)
                                        // show extended rights?
        {
            // set headings:
            this.headings = new String[Operations.OP_SHORTNAMES.length + 1];
            this.headings[0] = MultilingualTextProvider.getText (
                BOTokens.TOK_BUNDLE, BOTokens.ML_PGR, env);

            // set ordering attributes for the corresponding headings:
            this.orderings = new String[Operations.OP_SHORTNAMES.length + 1];
            this.orderings[0] = BOConstants.ORD_PGR;

            // set headings and orderings:
            for (int i = 1; i < Operations.OP_SHORTNAMES.length + 1; i++)
            {
                this.headings[i] = MultilingualTextProvider.getText (
                    Operations.TOK_BUNDLE, Operations.OP_SHORTNAMES[i - 1], env);
                this.orderings[i] = null;
            } // for
        } // if show extended rights
        else  // show simple rights
        {
            this.headings = new String []
            {
                MultilingualTextProvider.getText (
                    BOTokens.TOK_BUNDLE, BOTokens.ML_PGR, env),
                MultilingualTextProvider.getText (
                    BOTokens.TOK_BUNDLE, UserTokens.ML_RANREAD, env),
                MultilingualTextProvider.getText (
                    BOTokens.TOK_BUNDLE, UserTokens.ML_RANWRITE, env),
                MultilingualTextProvider.getText (
                    BOTokens.TOK_BUNDLE, UserTokens.ML_RANADMIN, env),
            }; // headings
            this.orderings = new String []
            {
                BOConstants.ORD_PGR,
                null,
                null,
                null,
            }; // orderings
        } // else show simple rights

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings


    /**************************************************************************
     * Represent the content of the Container, i.e. its elements, to the user.
     * <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   orderBy             Property, by which the result is
     *                              sorted.
     * @param   orderHow            Kind of ordering: BOConstants.ORDER_ASC or
     *                              BOConstants.ORDER_DESC
     *                              null => BOConstants.ORDER_ASC
     * @param   function            The function to be performed when clicking
     *                              on "OK".
     * @param   selectionlistHeader Header to be shown on the dialog
     * @param   checkedObjects      Oids of objects where the checkbox shall be
     *                              checked.
     */
    protected void performShowSelectionContent (int representationForm,
                                                int orderBy, String orderHow,
                                                int function,
                                                String selectionlistHeader,
                                                String[] checkedObjects)
    {
        this.debug (" performShowSelectionContent von RightsContainer");
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

        GroupElement list = new GroupElement ();
        FormElement form = this.createFormHeader (page, this.name, this.getNavItems (),
            null, selectionlistHeader, null, this.icon);
        // start with the object representation: show header
        form.addElement (new InputElement (BOArguments.ARG_OID,
            InputElement.INP_HIDDEN, "" + this.oid));
        form.addElement (new InputElement (BOArguments.ARG_ROID,
            InputElement.INP_HIDDEN, "" + this.oid));
        form.addElement (new InputElement (BOArguments.ARG_FUNCTION,
            InputElement.INP_HIDDEN, "" + AppFunctions.FCT_FORWARDTABVIEWEVENT));
        form.addElement (new InputElement (BOArguments.ARG_TABID,
            InputElement.INP_HIDDEN, "" + this.p_tabId));
        form.addElement (new InputElement (BOArguments.ARG_EVENT,
            InputElement.INP_HIDDEN, "" + function));

        TextElement text = new TextElement ("text");

        if (size > 0)                   // there are some elements?
        {
            TableElement table;         // table containing the list
            RowElement tr;              // actual table row

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

            // create header
            tr = this.createHeading (this.headings,
                                this.orderings, orderBy, orderHow);

            // add header to table:
            table.addElement (tr, true);

            RightsContainerElement_01 element;

            for (i = 0; i < size; i++)
            {
                // get the actual element and represent it to the user:
                element = (RightsContainerElement_01) this.elements.elementAt (i);
                element.showExtendedAttributes =
                    this.p_showExtendedAttributes;

                if (checkedObjects != null)
                {
                    element.checked = StringHelpers.findString (
                        checkedObjects, "" + element.oid) != -1;
                } // if

                table.addElement (element.showSelection
                    (BOListConstants.LST_CLASSROWS
                        [i % BOListConstants.LST_CLASSROWS.length], env));
            } // for i

            if (function == AppFunctions.FCT_LISTDELETE)
            {
                tr = new RowElement (Operations.OP_SHORTNAMES.length + 2);
                tr.classId = BOListConstants.LST_CLASSROWS
                    [size % BOListConstants.LST_CLASSROWS.length];

                TableDataElement td;
                InputElement iE = new InputElement (BOArguments.ARG_RECURSIVE,
                    InputElement.INP_CHECKBOX, "" + true);
                iE.checked = false;

                td = new TableDataElement (iE);
                td.width = BOListConstants.LST_NEWCOLWIDTH;
                tr.addElement (td);

                text = new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_DELRECURSIVE, env));
                td = new TableDataElement (text);
                td.colspan = Operations.OP_SHORTNAMES.length + 1;
                td.classId = BOListConstants.LST_CLASSROWS
                    [size % BOListConstants.LST_CLASSROWS.length];
                tr.addElement (td);

                table.addElement (tr);
            } // if

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

            list.addElement (table);
        } // else there are no elements

        // add generated list of selectable items to form
        form.addElement (list);
        // add form footer to form
        this.createFormFooter (form);

        if (this.p_isShowCommonScript)
        {
            // create the script to be executed on client:
            page.body.addElement (this.getCommonScript (true));
        } // if

        // show message if there are too many entries in the list:
        if (this.maxElements > 0 && this.size > this.maxElements)
                                    // too many elements?
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

        // build the page and show it to the user:
        try
        {
            // try to bulid the page
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            // show according message to the user
            IOHelpers.showMessage (e.getMsg (), this.app, this.sess, this.env);
        } // catch
    } // performShowSelectionContent


    /**************************************************************************
     * listChange changes the container contents attributes wich were
     * edited in listchangeform. <BR/>
     *
     * @param   representationForm  ???
     */
    public void listChange (int representationForm)
    {
        this.getParameters ();

        try
        {
            // set headings and orderings:
            this.setHeadingsAndOrderings ();
            // retrieve the old content of this container:
            this.performRetrieveContentData (Operations.OP_CHANGE, this.orderBy,
                this.orderHow);
            // change attributes of objects in list
            this.performListChange (Operations.OP_CHANGE);
        } // try
        catch (NoAccessException e) // no access to objects allowed
        {
            // send message to the user:
            this.showNoAccessMessage (Operations.OP_VIEWELEMS);
        } // catch

        if (this.master.state == States.ST_CREATED ||
            this.master.state == States.ST_UNKNOWN)
        {
            this.master.state = States.ST_ACTIVE;
            this.master.changeState (Operations.OP_CHANGESTATE);

            this.master = BOHelpers.getObject (this.oid, this.app, this.sess,
                this.user, this.env, false, false, true);
            this.master.displayTabs = true;
            this.master.show (0);
        } // if
        else
        {
            this.show (0);
        } // else
    } // listChange


    /**************************************************************************
     * Change the container content attributes which were edited in
     * listchangeform. <BR/>
     *
     * @param   operation   The operation code.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    public void performListChange (int operation)
        throws NoAccessException
    {
        this.debug ("RightsContainer_01.performListChange");
        RightsContainerElement_01 rigElementNew;
        RightsContainerElement_01 rigElementOld;
        Rights_01 right;
        int i = 0;

        // delete deleted rights:
        for (i = 0; i < this.elements.size (); i++)
        {
            rigElementOld =
                (RightsContainerElement_01) this.elements.elementAt (i);
//debug ("element, rPersonId = " + rigElementOld.rPersonId + "  name = " + rigElementOld.rPersonName);

            String searchKey;
            String keyNew = null;
            String keyOld = (new Integer (rigElementOld.rPersonId)).toString ();
            // find key of old right in Hashtable of new rights:
            for (Iterator<String> iter = this.sess.rights.keySet ().iterator ();
                 iter.hasNext ();)
            {
                searchKey = iter.next ();

                if (keyOld.equals (searchKey))
                {
                    // key found ??
                    keyNew = searchKey;
                } // if
            } // for iter

            // check if old right is still in current set rights (key was found)
            if (keyNew != null)
            {
                rigElementNew = this.sess.rights.get (keyNew);
                // check if right was changed
                if (rigElementNew.rights != rigElementOld.rights)
                {   //changeRight
                    right = this.mapRightElementToRight (rigElementNew);
                    try
                    {
                        right.performChange (Operations.OP_CHANGE);
                    } // try
                    catch (NameAlreadyGivenException e)
                    {
                        // that's not possible !!!
                        IOHelpers.showMessage ("RightsContainer_01.performListChange -" +
                                               " NameAlreadyGivenException",
                                               this.app, this.sess, this.env);
                    } // catch
                } // if

                // delete handled right from rights-hashtable
                this.sess.rights.remove (keyNew);
            } // if change right
            else
            {
                right = this.mapRightElementToRight (rigElementOld);
                this.debug ("delete right .." + right.pName);
                right.delete (Operations.OP_DELETE);
            } // else delete right
        } // for

        // create new rights:
        for (Iterator<RightsContainerElement_01> iter =
                this.sess.rights.values ().iterator (); iter.hasNext ();)
        {
            rigElementNew = iter.next ();
            this.debug ("maybe new right .." + rigElementNew.rPersonName + " rights: " + rigElementNew.rights);

            if (rigElementNew.rights != 0)
            {
                right = this.mapRightElementToRight (rigElementNew);
                right.create (Operations.OP_NEW);
            }  // if  rights are set
        } // for iter

        // executed listChange clear the old receivers list
        this.sess.rights = new Hashtable<String, RightsContainerElement_01> ();
    } // performListChange


    /**************************************************************************
     * copy values from RightContainerElement_01 Object to Right_01 Object.
     * <BR/>
     *
     * @param   rigElem ???
     *
     * @return  ???
     */
    public Rights_01 mapRightElementToRight (RightsContainerElement_01 rigElem)
    {
        Rights_01 right = new Rights_01 ();
        right.initObject (rigElem.rPersonOid, this.user, this.env,
            this.sess, this.app);
        right.rPersonId = rigElem.rPersonId;
        right.rights = rigElem.rights;
        right.pName = rigElem.rPersonName;
        right.rOid  = rigElem.rOid;
        right.pOid = rigElem.rPersonOid;

        return right;
    } // mapRightElementToRight


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
            Buttons.BTN_NONE,
            Buttons.BTN_ASSIGNRIGHTS,
            Buttons.BTN_LISTDELETERIGHTS,
        }; // buttons

        // set the button "Inherit Rights" for all types, except
        // the GroupContainer, UserContainer, UserGroupsContainer and
        // UserAdminContainer
        if ((this.oid.type !=
                this.getTypeCache ().getTypeId (TypeConstants.TC_GroupContainer)) &&
            (this.oid.type !=
                this.getTypeCache ().getTypeId (TypeConstants.TC_UserContainer)) &&
            (this.oid.type !=
                this.getTypeCache ().getTypeId (TypeConstants.TC_UserGroupsContainer)) &&
            (this.oid.type !=
                this.getTypeCache ().getTypeId (TypeConstants.TC_UserAdminContainer)))
                                        // BTN_SETRIGHTSREC
        {
            // replace the button BTN_NONE with the BTN_SETRIGHTSREC at position
            // 0; this solution has been chosen, because of the advantage in
            // saving time and memory.
            buttons[0] = Buttons.BTN_SETRIGHTSREC;
        } // if

        // return button array
        return buttons;
    } // setContentButtons


    /**************************************************************************
     * delete rights which were selected in list delete form. <BR/>
     */
    protected void listDelete ()
    {
        // get all oids of selected elements
        String[] oidString = this.getSelectedElements ();
        int num = 0;
        boolean rec = false;

        if (oidString != null)          // at least one object to delete?
        {
            if ((num = this.env.getBoolParam (BOArguments.ARG_RECURSIVE)) >=
                IOConstants.BOOLPARAM_FALSE)
            {
                rec = num == IOConstants.BOOLPARAM_TRUE;
            } // if

            Rights_01 rigObj = new Rights_01 ();

            try
            {
                rigObj = (Rights_01)
                    this.getObjectCache ().fetchNewObject
                        (TypeConstants.TC_Rights, this.user, this.sess, this.env);
            } // try
            catch (TypeNotFoundException e)
            {
                // should not occur, display error message:
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
            catch (ObjectClassNotFoundException e)
            {
                // should not occur, display error message:
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
            catch (ObjectInitializeException e)
            {
                // should not occur, display error message:
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch

            rigObj.rOid = this.env.getOidParam (BOArguments.ARG_ROID);
            for (int i = 0; i < oidString.length; i++)
            {
                if (oidString[i] != null)
                {
                    rigObj.rPersonId = Integer.parseInt (oidString[i]);
                    rigObj.deleteRecursive = rec;
                    rigObj.delete (0);
                } // if
                else
                {
                    // didn't get object
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_OBJECTNOTFOUND, this.env),
                        this.app, this.sess, this.env);
                } // else
            } // for
        } // if at least one object to delete
    } // listDelete


    /**************************************************************************
     * handle events for current tabview. <BR/>
     *
     * @param   evt        event to be handled.
     */
    public void handleEvent (int evt)
    {
        this.debug ("RightsContainer_01.handleEvent (" + evt + ")");
        // example code
        switch (evt)            // perform function
        {
            case AppFunctions.FCT_LISTCHANGEFORM:
                // get userinteraction from listchangeform
                this.getParameters ();  // necessary because of arrow buttons
                this.showListChangeForm (0);
                break;

            case AppFunctions.FCT_LISTCHANGE:
                this.listChange (0);
                break;

            case AppFunctions.FCT_LISTDELETEFORM:
                this.showDeleteForm (0);
                break;

            case AppFunctions.FCT_LISTDELETE:
                this.listDelete ();
                this.show (0);
                break;

            case AppFunctions.FCT_SETRIGHTSREC:
                this.setRightsRec (0);
                break;

            default:
                super.handleEvent (evt);
        } // switch
    } // handleEvent


    /**************************************************************************
     * Set the specific properties for tabview rightscontainer. <BR/>
     *
     * @param   majorObject The major object of this view tab.
     */
    public void setSpecificProperties (BusinessObject majorObject)
    {
        this.debug ("RightsContainer_01.setSpecificProperties (BusinessObject majorObject)");
        super.setSpecificProperties (majorObject);
        this.master = majorObject;
        this.containerName = majorObject.name;
        this.icon = "RightsContainer.gif";
    } // setSpecificProperties

} // class RightsContainer_01

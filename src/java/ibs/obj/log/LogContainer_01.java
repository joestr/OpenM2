/*
 * Class: LogContainer_01.java
 */

// package:
package ibs.obj.log;

// imports:
//KR TODO: unsauber
import ibs.app.AppFunctions;
import ibs.app.CssConstants;
import ibs.app.FilenameElement;
import ibs.app.UserInfo;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOListConstants;
import ibs.bo.BOMessages;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.bo.States;
import ibs.bo.cache.ObjectPool;
import ibs.bo.type.Type;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.log.LogConstants;
import ibs.obj.log.LogContainerElement_01;
import ibs.service.user.User;
import ibs.tech.html.BuildException;
import ibs.tech.html.FormElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.Page;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.DateTimeHelpers;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


/******************************************************************************
 * This class represents one object of type DocumentContainer with version 01.
 * <BR/>
 *
 * @version     $Id: LogContainer_01.java,v 1.32 2012/11/07 10:25:38 rburgermann Exp $
 *
 * @author      Heinz Stampfer (HJ), 980720
 ******************************************************************************
 */
public class LogContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO = "$Id: LogContainer_01.java,v 1.32 2012/11/07 10:25:38 rburgermann Exp $";


    /**
     * This Flag mark if the Container is in Objectuse or in a generam manner.
     * <BR/>
     */
    public boolean inObjectUse = false;

    /**
     * The types of Attachment can be HyperlinkType or Filetype. <BR/>
     */
    private String[] typeLogNames = {
        "alle",
        "erzeugt",
        "bearbeitet",
        "gelöscht",
    }; // typeLogNames

    /**
     * The types of Attachment can be HyperlinkType or Filetype. <BR/>
     */
    private String[] typeLogIds = {
        Short.toString (LogConstants.LOG_ALL),
        Short.toString (LogConstants.LOG_CREATE),
        Short.toString (LogConstants.LOG_CHANGE),
        Short.toString (LogConstants.LOG_DELETE),
    }; // typeLogIds

    /**
     * The types of Attachment can be HyperlinkType or Filetype. <BR/>
     */
    private String procCleanLog = "p_LogContainer_01$clean";


    /**************************************************************************
     * This constructor creates a new instance of the class NewsContainer_01.
     * <BR´/>
     */
    public LogContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // LogContainer_01


    /**************************************************************************
     * Creates a LogContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    @Deprecated
    public LogContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // LogContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    @Override
    public void initClassSpecifics ()
    {
        this.ownOrdering = true;
        this.orderBy = BOListConstants.LST_LOGDEFAULTORDERING;
        this.orderHow = BOConstants.ORDER_DESC;

        this.inObjectUse = false;
        this.elementClassName = "ibs.obj.log.LogContainerElement_01";
    } // initClassSpecifics


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <CODE>env</CODE> property is used
     * for getting the parameters. This property must be set before calling
     * this method. <BR/>
     */
    @Override
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
                // set actual order column:
                this.orderBy = this.getUserInfo ().orderBy;
            } // else
            if (this.orderBy < 0 || this.orderBy >= this.orderings.length)
                                            // order column out of range?
            {
                // take default column:
                this.orderBy = BOListConstants.LST_DEFAULTORDERING;
                // take default kind of ordering:
                this.orderHow = BOConstants.ORDER_ASC;
            } // if order column out of range
            // store ordering:
            UserInfo userInfo = this.getUserInfo ();
            userInfo.orderBy = this.orderBy;
            userInfo.orderHow = this.orderHow;
            // get max elements
            if ((num = this.env.getIntParam (BOArguments.ARG_MAXRESULT)) !=
                IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
            {
                this.maxElements = num;
            } // if
        } // if
    } // getParameters


    /**************************************************************************
     * Show the object, i.e. its properties. <BR/>
     * The properties are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showFilter (int representationForm)
    {
        if (true)                       // business object resists on this
                                        // server?
        {
                // show the object's data:
            this.performShowFilter (representationForm);
        } // if container object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server
    } // showFilter


    /**************************************************************************
     * Represent the properties of a BusinessObject object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFilterProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
//        showProperty (table, BOArguments.ARG_NAME, TOK_NAME, Datatypes.DT_NAME, "FILTER FÜR DIE PROTOKOLLIERUNG");
        this.showFormProperty (table, BOArguments.ARG_STARTSHOWDATE,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_STARTSHOWDATE, env), Datatypes.DT_DATE,
            this.getUserInfo ().startLogDate);
        this.showFormProperty (table, BOArguments.ARG_ENDSHOWDATE,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ENDSHOWDATE, env), Datatypes.DT_DATE,
            this.getUserInfo ().endLogDate);
        this.showFormProperty (table, BOArguments.ARG_KINDOFLOG,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LOGTYPE, env), Datatypes.DT_SELECT, Integer.toString (this
                .getUserInfo ().logTypeEntry), this.typeLogIds,
            this.typeLogNames, this.getUserInfo ().logTypeEntry);

        this.showFormProperty (table, BOArguments.ARG_PARTOF,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LOGPARTOF, env), Datatypes.DT_BOOL,
            "" + this.getUserInfo ().showPartOf);
    } // showFilterProperties



    /***************************************************************************
     * Represent the properties of a LogContainer_01 object to the user. <BR/>
     *
     * @param representationForm The kind of representation.
     */
    public void performShowFilter (int representationForm)
    {
        Page page = new Page (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUSINESSOBJECT, env), false);

        // add the appropriate style sheet
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () + "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);

        FormElement form = this.createFormHeader (page, this.name,
            this.getNavItems (), null, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SETFILTER, env), null, null,
            this.containerName);
        TableElement table = this.createFrame (representationForm, 2);
        table.border = 0;

        // set table styles
        table.classId = CssConstants.CLASS_INFO;
        String[] classIds = new String[2];
        classIds[0] = CssConstants.CLASS_NAME;
        classIds[1] = CssConstants.CLASS_VALUE;
        table.classIds = classIds;

        // clear filenames-vector in session (userinfo)
        // will be filled when ibs.bo.Datatypes.DT_FILE input types are shown
        UserInfo userInfo = this.getUserInfo ();
        userInfo.filenames = new Vector<FilenameElement> ();

        // start with the object representation: show header
        form.addElement (new InputElement (BOArguments.ARG_OID, InputElement.INP_HIDDEN,
                        "" + this.oid));
        form.addElement (new InputElement (BOArguments.ARG_FUNCTION, InputElement.INP_HIDDEN,
                        "" + AppFunctions.FCT_SETFILTER));

        // loop through all properties of this object and display them:
        this.properties = 0;

        this.showFilterProperties (table);

        form.addElement (table);

        this.createFormFooter (form);

        if (this.p_isShowCommonScript)
        {
            // create the script to be executed on client:
            ScriptElement script = this.getCommonScript (true);
            page.body.addElement (script);
        } // if

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // performShowFilter


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in an
     * object's info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    @Override
    protected int[] setInfoButtons ()
    {
        // define buttons to be displayed:
        int [] buttons =
        {
            Buttons.BTN_EDIT,
//            Buttons.BTN_DELETE,
//            Buttons.BTN_CUT,
//            Buttons.BTN_COPY,
//            Buttons.BTN_DISTRIBUTE,
//            Buttons.BTN_CLEAN,
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
        }; // buttons

        // return button array
        return buttons;
    } // showInfoButtons

    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * container's content view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    @Override
    protected int[] setContentButtons ()
    {
        int[] buttons;
        // define buttons to be displayed:
        if (this.isTab ())
        {
            int[] temp =
            {
//                Buttons.BTN_NEW,
//                Buttons.BTN_PASTE,
//                Buttons.BTN_CLEANLOG,
                Buttons.BTN_SEARCH,
//                Buttons.BTN_HELP,
//                Buttons.BTN_LISTDELETE,
//                Buttons.BTN_REFERENCE,
            }; // buttons
            buttons = temp;
        } // if
        else
        {
            int[] temp =
            {
//                Buttons.BTN_NEW,
//                Buttons.BTN_PASTE,
                Buttons.BTN_CLEANLOG,
                Buttons.BTN_SEARCH,
//                Buttons.BTN_HELP,
//                Buttons.BTN_LISTDELETE,
//                Buttons.BTN_REFERENCE,
            };
            buttons = temp;
        } // else
        // return button array
        return buttons;
    } // setContentButtons


    /**************************************************************************
     * Represent the properties of a LogContainer_01 object to the user
     * within a form. <BR/>
     *
     * @param   operation   The necessary permissions.
     *
     * @return  The query result.
     *
     * @exception   NoAccessException
     *              The user has not the necessary permissions for this
     *              operation.
     */
    public int performLogDeleteData (int operation) throws NoAccessException
    {
        // create stored procedure call:
        StoredProcedure sp = new StoredProcedure (this.procCleanLog,
            StoredProcedureConstants.RETURN_VALUE);
        int retVal = UtilConstants.QRY_OK;            // return value of query

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)

        // input parameters:
        // user id
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            (this.user != null) ? this.user.id : 0);
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, operation);

        // perform the function call:
        retVal = BOHelpers.performCallFunctionData (sp, this.env);

        return retVal;                  // return the oid of the new object
    } // performLogDeleteData


    /***************************************************************************
     * Represent the properties of a LogContainer_01 object to the user
     * within a form. <BR/>
     *
     * @param   action  The database connection object.
     *
     * @return  The log message.
     */
    protected String logMessage (int action)
    {
        String logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                BOMessages.ML_LOG_DEFAULTMESSAGE, env);

        switch (action)                 // handle direction of parameter
        {
            case Operations.OP_NEW:   // create a new object
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_NEWMESSAGE, env);
                break;
            case Operations.OP_READ:  // Read the data of an object
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_READMESSAGE, env);
                break;
            case Operations.OP_VIEW:  // View the object within a container
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_VIEWMESSAGE, env);
                break;
            case Operations.OP_EDIT: // Edit an object's properties
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_EDITMESSAGE, env);
                break;
            case Operations.OP_DELETE: // delete an objekt
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_DELETEMESSAGE, env);
                break;
            case Operations.OP_LOGIN: // make a login
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_LOGINMESSAGE, env);
                break;
            case Operations.OP_VIEWRIGHTS: // view the rights
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_VIEWRIGHTSMESSAGE, env);
                break;
            case Operations.OP_EDITRIGHTS: // edit the rights of an Objekt
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_EDITRIGHTSMESSAGE, env);
                break;
                //               case Operations.OP_SETRIGHTS: // set the rights Operations.OP_EDITRIGHTS
                //                  break;
            case Operations.OP_CREATELINK: // create an Link
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_CREATELINKMESSAGE, env);
                break;
            case Operations.OP_DISTRIBUTE: // distribute an object
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_DISTRIBUTEMESSAGE, env);
                break;
            case Operations.OP_ADDELEM : // add an object to an Container
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_ADDELEMMESSAGE, env);
                break;
            case Operations.OP_DELELEM: // Delete an element from a container
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_DELELEMMESSAGE, env);
                break;
            case Operations.OP_VIEWELEMS: // View the elements of a container
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_VIEWELEMSMESSAGE, env);
                break;
            default:                // unknown direction
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_DEFAULTMESSAGE, env);
                break;
        } // switch
        if ((action & Operations.OP_CHANGEPROCSTATE) == Operations.OP_CHANGEPROCSTATE)
        {
            int processState = action - Operations.OP_CHANGEPROCSTATE;
            // state of order
            String stateString;
            int index = StringHelpers.findString (
                States.PST_STATEIDS, Integer.toString (processState));
            if (index != -1)
            {
                stateString = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                        States.PST_STATENAMES [index], env);
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_CHANGEPROCESSSTATE, env) + stateString;
            } // if
        } // if
        // display that a forward was performed and the according workflow state
        if ((action & Operations.OP_FORWARD) == Operations.OP_FORWARD)
        {
            int processState = action - Operations.OP_FORWARD;
            // state of order
            String stateString;
            int index = StringHelpers.findString (
                States.PST_STATEIDS, Integer.toString (processState));
            if (index != -1)
            {
                stateString = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                        States.PST_STATENAMES [index], env);
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_FORWARDMESSAGE, env) + stateString;
            } // if ( index != -1)
        } // if ((action & Operations.OP_FORWARD) == Operations.OP_FORWARD)

        return logMessage;
    } // LogMessage


    /**************************************************************************
     * Get the Container's content out of the database. <BR/>
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
    @Override
    protected void performRetrieveContentData (int operation, int orderBy,
                                               String orderHow)
        throws NoAccessException
    {
        int operationLocal = operation; // variable for local assignments
        String orderHowLocal = orderHow; // variable for local assignments
        SQLAction action = null;        //SQLAction for Databaseoperation
        LogContainerElement_01 obj;
        List<LogContainerElementEntry_01> containerElementEntries =
            new ArrayList<LogContainerElementEntry_01> ();

        Type type = null; // holds the type of the logged object

        int rowCount = 1;
        StringBuffer filterQueryStr = new StringBuffer ();
        StringBuffer filterWhere = new StringBuffer (" ");
        StringBuffer filterOrderStr = new StringBuffer ();
        StringBuffer filterPartOfStr = new StringBuffer ();
        StringBuffer filterAndWhere = new StringBuffer (" ");
        StringBuffer filterKindofStr = new StringBuffer ();
        StringBuffer filterRightsStr = new StringBuffer ();
        StringBuffer filterDomainStr = new StringBuffer ();

// showMessage("Name des O_" + name + "Kind of Container "+ containerKind);
//showMessage("Orderby in performRetrieveContentData-->" + orderBy);

        // ensure a correct ordering:
        if (!orderHowLocal.equalsIgnoreCase (BOConstants.ORDER_DESC)) // not descending?
        {
            orderHowLocal = BOConstants.ORDER_ASC;       // order ascending
        } // if

        this.elements.removeAllElements ();

        // the LogContainer is in ObjectUse
        if (this.isTab ())
        {
           // read out the containerId because you have troubles when you sort
           //  containerId = app.userInfo.containerIdOfLogContainer;
            filterQueryStr
                .append ("  WHERE (( pr.oid = ").append (this.containerId.toStringQu ())
                .append (") OR ( pr.containerId = ").append (this.containerId.toStringQu ())
                .append (")) ");
            filterOrderStr
                .append ("       ORDER BY ").append (this.orderings[orderBy])
                .append (" ").append (orderHowLocal);
            filterRightsStr
                .append (" AND r.roid = pr.oid ");
        } // if
        else                            // use as a Container in the database
        {
            filterQueryStr = new StringBuffer ();
            // check if time frame has been set and activated

            if (this.getUserInfo ().startLogDate != null)
            {
                filterAndWhere
                    .append (" AND ");
                filterQueryStr
                    .append (" WHERE ")
                    .append (SQLHelpers.getDateDiff (new StringBuffer ("actiondate"),
                        SQLHelpers.getDateString (
                            DateTimeHelpers.dateToString (
                                this.getUserInfo ().startLogDate) + " 00:00"),
                        SQLConstants.UNIT_DAY))
                    .append (" <= 0 ");
            } // if
            if (this.getUserInfo ().endLogDate != null)
            {
                filterQueryStr
                    .append (filterAndWhere)
                    .append (SQLHelpers.getDateDiff (new StringBuffer ("actiondate"),
                        SQLHelpers.getDateString (
                            DateTimeHelpers.dateToString (
                                this.getUserInfo ().endLogDate) + " 23:59"),
                        SQLConstants.UNIT_DAY))
                    .append (" >= 0 ");
            } //if

            filterDomainStr
                .append (" AND pr.userId IN (Select id from ibs_user where domainId = ")
                .append (this.user.domain).append (") ");
            // Set the View Rights
            operationLocal = Operations.OP_VIEWPROTOCOL;
            filterRightsStr
                .append (" AND r.roid = pr.oid ")
                .append (" AND r.rPersonId = ").append (this.user.id)
                .append (SQLHelpers.getStringCheckRights (operationLocal));
//  showMessage("perform 2");
/*
Select *  From ibs_protocol_01 pr, ibs_rights r
WHERE (DATEDIFF(day, actionDate,'6.10.1998 00:00') <= 0)
AND (DATEDIFF(day, actionDate, '6.10.1998 23:59') >= 0)
AND userId IN (Select id FROM ibs_User where domainId = 2)
AND ((containerKind = 1) OR (containerKind = 2))
AND action = 8
AND r.roid = pr.oid
-- AND r.rPersonId = 41943043
-- AND (r.rights & 2 ) > 0
ORDER BY objectName ASC
*/

// showMessage(" partof  = " + app.userInfo.showPartOf);

            if (this.getUserInfo ().showPartOf)
            {
                int helpInt = 0;
                helpInt = (this.getUserInfo ().showPartOf ? 1 : 0) + 1;
                filterPartOfStr
                    .append (" AND (pr.containerKind IN (").append (BOConstants.CONT_STANDARD)
                    .append (", ").append (helpInt)
                    .append ("))");
            } // if
            else
            {
                filterPartOfStr
                    .append (" AND pr.containerKind = ").append (BOConstants.CONT_STANDARD).append (" ");
            } // else

            if (this.getUserInfo ().logTypeEntry > 1)
            {
                int helpInt = 0;
                switch (this.getUserInfo ().logTypeEntry)
                {
                    case 2:
                        helpInt = Operations.OP_NEW;
                        break;
                    case 3:
                        helpInt = Operations.OP_CHANGE;
                        break;
                    case 4:
                        helpInt = Operations.OP_DELETE;
                        break;
                    default:
                        helpInt = Operations.OP_READ;
                        break;
                } // switch

                filterKindofStr
                    .append (" AND action = ").append (helpInt);
            } // if
            filterOrderStr
                .append ("  ORDER BY ").append (this.orderings[orderBy])
                .append (" ").append (orderHowLocal);
        } // else

        // get the elements and entries out of the database:
        // create the SQL String to select all tuples
        StringBuffer queryStr = new StringBuffer ()
            .append ("SELECT DISTINCT pr.id, pr.fullName, pr.objectName, pr.action, pr.actionDate, pr.icon as icon, pre.fieldName, pre.oldValue, pre.newValue, o.typeCode ")
            .append ("FROM   ibs_Protocol_01 pr left outer join ibs_object o on pr.oid = o.oid left outer join ibs_ProtocolEntry_01 pre on pre.protocolId = pr.id");

//this.user.domain;

        if (this.isTab ())
        {
            queryStr
                .append (filterWhere).append (filterQueryStr)
                .append (filterOrderStr);
        } // if
        else
        {
            queryStr
                .append (filterQueryStr).append (filterKindofStr)
                .append (filterDomainStr)
                .append (filterPartOfStr).append (filterOrderStr);
        } // else

//debug ("Query: " + queryStr);
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
            } // if

            // holds the last protocolId
            int id = -1;

            // everything ok - go on
//  showMessage("perform 2.1");
            // get tuples out of db
            while (!action.getEOF ())
            {
                // check if the row contains another protocol id
                if (id == -1 || id != action.getInt ("id"))
                {
                    // create a new object:
                    //  obj = new OverlapContainerElement_01 ();
                    obj = new LogContainerElement_01 ();

    //  showMessage("perform 3");
                    obj.id = action.getInt("id");
                    obj.fullName = action.getString ("fullName");
                    obj.objectName = action.getString ("objectName");
                    obj.actionString = this.logMessage (action.getInt ("action"));
                    obj.actionDate = action.getDate ("actionDate");
                    obj.icon = action.getString ("icon");

                    if ((this.sess != null) && (this.sess.activeLayout != null))
                    {
                        obj.layoutpath = this.sess.activeLayout.path;
                    } // if

                    // Retrieve the typecode
                    String typeCode = action.getString ("typeCode");

                    // check if the typecode is set
                    if (typeCode != null)
                    {
                        // retrieve the type for the object's typecode
                        type = ((ObjectPool) env.getApplicationInfo ().cache).
                            getTypeContainer ().findType (typeCode);
                    } // if
                    
                    containerElementEntries =
                        new ArrayList<LogContainerElementEntry_01> ();
                    String fieldName = action.getString ("fieldName");
                    // read string and convert unicode ('$#39') (stored literally in the DB!) back to "'" (DB premature string termination problem)
                    String oldValue = SQLHelpers.dbToAscii (action.getString ("oldValue"));
                    // read string and convert unicode ('$#39') here as well
                    String value = SQLHelpers.dbToAscii (action.getString ("newValue"));

                    // downward compatibility check:
                    // if the fieldName and value are empty do not add the entry to the list
                    if ((fieldName != null && !fieldName.isEmpty ()) ||
                        (value != null && !value.isEmpty ()))
                    {
                        LogContainerElementEntry_01 entry =
                            new LogContainerElementEntry_01 ();
                        entry.setFieldName (type, fieldName, env);
                        entry.setOldValue (oldValue);
                        entry.setValue (value);
                        containerElementEntries.add (entry);
                    } // if

                    obj.entries = containerElementEntries;

                    // add element to list of elements:
                    this.elements.addElement (obj); // add element to list of elements
                } // if
                // still the same protcol id
                else
                {
                    String fieldName = action.getString ("fieldName");
                    
                    // read string and convert unicode ('$#39') (stored literally in the DB!) back to "'" (DB premature string termination problem)
                    String oldValue = SQLHelpers.dbToAscii (action.getString ("oldValue"));
                    // read string and convert unicode ('$#39') here as well
                    String value = SQLHelpers.dbToAscii (action.getString ("newValue"));
                    
                    LogContainerElementEntry_01 entry =
                        new LogContainerElementEntry_01 ();
                    entry.setFieldName (type, fieldName, env);
                    entry.setOldValue (oldValue);
                    entry.setValue (value);
                    containerElementEntries.add (entry);
                } // else

                // remember the old id
                id = action.getInt ("id");

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
            IOHelpers.showMessage (dbErr, this.app, this.sess, this.env, false);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally
    } // performRetrieveContentData


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard
     * container. <BR/>
     */
    @Override
    protected void setHeadingsAndOrderings ()
    {
        // set super attribute
        this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
            BOListConstants.LST_HEADINGS_LOGCONTAINER, env);

        // set super attribute
        this.orderings = BOListConstants.LST_ORDERINGS_LOGCONTAINER;

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings

} // class LogContainer_01

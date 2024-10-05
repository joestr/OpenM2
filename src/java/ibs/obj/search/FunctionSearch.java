/*
 * Class: FunctionSearch.java
 */

// package:
package ibs.obj.search;

// imports:
import ibs.app.AppConstants;
import ibs.app.CssConstants;
import ibs.app.AppFunctions;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOTokens;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.bo.SelectionList;
import ibs.bo.States;
import ibs.bo.type.TypeConstants;
import ibs.io.Environment;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilangConstants;
import ibs.ml.MultilingualTextInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.func.FunctionArguments;
import ibs.obj.func.IbsFunction;
import ibs.obj.query.QueryConstants;
import ibs.obj.query.QueryExecutive_01;
import ibs.obj.search.SearchArguments;
import ibs.obj.search.SearchEvents;
import ibs.obj.search.SearchTokens;
import ibs.service.user.User;
import ibs.tech.html.BlankElement;
import ibs.tech.html.BuildException;
import ibs.tech.html.FormElement;
import ibs.tech.html.FrameElement;
import ibs.tech.html.FrameSetElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.Page;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableElement;
import ibs.tech.http.HttpArguments;
import ibs.tech.sql.DBConnector;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;


/******************************************************************************
 * Interface for all Functions to be performed within the framework. <BR/>
 * This class should ensure, that all objects, data etc. regarding to
 * one function, are encapsulated together in one topclass. <BR/>
 *
 * In first Version, the function extends BusinessObject, to be able to
 * use the cachingalgorithm of Application - should be an abstract class
 * in final implementation.
 *
 * @version     $Id: FunctionSearch.java,v 1.45 2011/12/12 17:23:09 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ), 000918
 ******************************************************************************
 */
public class FunctionSearch extends IbsFunction
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FunctionSearch.java,v 1.45 2011/12/12 17:23:09 rburgermann Exp $";


    /**
     *
     */
    private QueryExecutive_01 queryExec = null;


    /**************************************************************************
     * This constructor creates a new instance of the class IbsFunction. <BR/>
     */
    public FunctionSearch ()
    {
        // nothing to do
    } // FunctionSearch


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set parameters for frameset:
        this.frm1Size = "*";
        this.frm2Size = "0";
    } // initClassSpecifics


    /**************************************************************************
     * Initializes a Function. <BR/>
     *
     * The user object is also stored in a specific
     * property of this object to make sure that the user's context can be used
     * for getting his/her rights. <BR/>
     * {@link #env env} is initialized to the provided object. <BR/>
     * {@link #sess sess} is initialized to the provided object. <BR/>
     * {@link #app app} is initialized to the provided object. <BR/>
     *
     * @param   aId     The id of the function.
     * @param   aUser   Object representing the user.
     * @param   aEnv    The actual call environment.
     * @param   aSess   The actual session info.
     * @param   aApp    The global application info.
     */
    public void initFunction (int aId, User aUser, Environment aEnv,
                            SessionInfo aSess, ApplicationInfo aApp)
    {
        super.initFunction (aId, aUser, aEnv, aSess, aApp);

// HACK AJ BEGIN
        // there has to be an empty oid to mark this function as
        // non-physical object. this is necesseary for some GUI-Methods which are
        // trying to read rights from DB
        this.setOid (OID.getEmptyOid ());
// HACK AJ END

        // check if there was an oid set:
        OID queryExecOid = this.env.getOidParam (BOArguments.ARG_OID);

        // check if the oid is really a query executive:
        if (queryExecOid != null && queryExecOid.tVersionId !=
            this.getTypeCache ().getTVersionId (TypeConstants.TC_QueryExecutive))
                                        // wrong type?
        {
            // oid not needed, drop it:
            queryExecOid = null;
        } // if wrong type

        // check if there was an oid set:
        if (queryExecOid != null)
        {
            // get the object's instance:
            this.queryExec = (QueryExecutive_01) BOHelpers.getObject (
                queryExecOid, this.env, false,
                false, false);
        } // if
        else
        {
            // get a new query executive object:
            this.queryExec = (QueryExecutive_01) BOHelpers.getNewObject (
                TypeConstants.TC_QueryExecutive, this.env, false);
        } // else
    } // initFunction


    /**************************************************************************
     * main method = sequence control of this function. <BR/>
     */
    public void start ()
    {
        this.trace ("FunctionSearch.start ()");
        int event;                      // the event

        // reset query in session:
        this.sess.queryObject = null;

        // get current event:
        event = this.getEvent ();
        this.trace ("FunctionSearch.start event = " + event);

        // check which event was thrown:
        switch (event)
        {
            case SearchEvents.EVT_BUILDCALLINGSEARCHFORM:
                this.buildCallingSearchForm ();
                break;

            case SearchEvents.EVT_CLICKSYSTEMSEARCHBUTTON:
                // show form with selectionbox which contains
                // all queries for advanced search:
                this.showSearchSelectionForm ();
                break;

            case SearchEvents.EVT_CLICKOKSEARCHSELECTIONFORM:
                // show frame for searchform:
                this.showSearchFrame ();
                break;

            case SearchEvents.EVT_SHOWSEARCHFORM:
                // show searchform with searchfields for selected searchquery:
                this.showSearchForm ();
                break;

            case SearchEvents.EVT_CLICKOKSEARCHFORM:
                // show results:
                this.showResultForm ();
                break;

            default:
                // message if the function was called with an wrong event:
                IOHelpers.showMessage ("Event " + event + " is not valid for Function " +
                             this.id, this.app, this.sess, this.env);
                break;
        } // switch
    } // start


    /**************************************************************************
     * Generates the call for the searchFrame also a string which is the "search
     * type selection list" is generated. <BR/>
     *
     * @deprecated has been replaced by buildCallingSearchForm
     */
    public void buildCallingSearchFormOLD ()
    {
        boolean showSelectionList = false; // if true the searchForm should also
                                        // have a selection list for the types
        int num = 0;                    // a numerical parameter value
        int length = 0;                 // number of elements in selection
        // create a new empty page:
        Page page = new Page ("searchForm", false);

        if ((num = this.env.getBoolParam (SearchArguments.ARG_SELECTIONLIST)) >=
            IOConstants.BOOLPARAM_FALSE)
        {
            showSelectionList = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        if (showSelectionList)          // selection list shall be displayed?
        {
            // get a selectionlist, with an empty element at the first position
            // of all reporttemplates in system, where the current user has
            // rights on:
            SelectionList queryObjectListData =
                this.performRetrieveQueryCreatorSelectionList (true);

            if (queryObjectListData.ids != null &&
                queryObjectListData.ids.length > 0)
                                        // are there elements for the selection
                                        // list?
            {
                length = queryObjectListData.ids.length;
                                        // how many elements has the value

                // sorted array (the indexes if values):
                int [] sorts = new int [length];
                // loop variables:
                int i;
                int j;
                int k;

                // order the arrays by values:
                sorts[0] = 0;           // initialize sorted array

                // loop through all elements of the values array:
                for (i = 1; i < length; i++)
                {
                    // loop through all values which are already sorted:
                    for (j = i - 1;
                         j > -1 &&
                        queryObjectListData.values[sorts[j]]
                            .compareTo (queryObjectListData.values[i]) > 0;
                         j--)
                    {
                        /* nothing to do */
                    } // for j
                    for (k = i - 1; k > j; k--)
                    {
                        sorts[k + 1] = sorts[k];
                    } // for k
                    sorts[j + 1] = i;
                } // for i

                ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);

                for (i = 0; i < length; i++)
                {
                    script.addScript ("top.tryCall ('top.search.add (\\'" +
                                      queryObjectListData.values[sorts[i]] + "\\', \\'" +
                                      queryObjectListData.ids[sorts[i]] + "\\');');");
                } // for

                script.addScript ("top.tryCall ('top.search.refresh ()');");
                page.head.addElement (script);
            } // if are there elements for the selection list
        } // if selection list shall be displayed

        page.body.addElement (new BlankElement ());

        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            // should not occur
            // display error message:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // buildCallingSearchForm


    /**************************************************************************
     * Generates the javascript code for the searchtemp frame that populates
     * the content of the search query selection box within the search frame. <BR/>
     *
     * @see #buildCallingSearchFormOld for the old implementation
     */
    public void buildCallingSearchForm ()
    {
        boolean showSelectionList = false; // if true the searchForm should also
                                        // have a selection list for the types
        int num = 0;                    // a numerical parameter value
        // create a new empty page:
        Page page = new Page ("searchForm", false);
        String queryCategory;
        String actQueryCategory = null;
        String queryName;
        String queryDesc;
        String queryOidStr;

        if ((num = this.env.getBoolParam (SearchArguments.ARG_SELECTIONLIST)) >=
            IOConstants.BOOLPARAM_FALSE)
        {
            showSelectionList = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        if (showSelectionList)          // selection list shall be displayed?
        {
            ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
            // get all search queries out of the database sorted by category
            int rowCount;
            SQLAction action = null;        // the action object used to access the

            // construct the query to read the data
            // TODO: note that this query does not take care of domains anymore!
            // like in #performRetrieveQueryCreatorSelectionList
            // the old implementation is using posnopath to resolve the domain
            // which leads so slow performance on large systems
            //
            // also note that ltrim must be used for the category because
            // the base stores " " instead of "" for empty categories.

            StringBuffer queryStr = new StringBuffer ()
                .append (" SELECT oqc.oid, oqc.name, oqc.description, ltrim (qc.category) as category ")
                .append (" FROM v_Container$rights oqc")
                .append (", ibs_QueryCreator_01 qc")
                .append (" WHERE oqc.oid = qc.oid ")
                .append (" AND oqc.state = ").append (States.ST_ACTIVE)
                .append (" AND oqc.userId = ").append (this.user.id)
                .append (SQLHelpers.getStringCheckRights (Operations.OP_VIEW))
                .append (" AND ")
                .append (SQLHelpers.getBitAnd ("qc.queryType",
                        String.valueOf (QueryConstants.QT_SEARCH)))
                        .append ("= 1")
                .append (" ORDER BY ltrim (qc.category), oqc.name");

            try
            {
                action = DBConnector.getDBConnection ();
                rowCount = action.execute (queryStr, false);
                // empty resultset?
                if (rowCount > 0)
                {
                    // try to get the select element in the search frame
                    // in case the search frame does not support the getSelectElem
                    // function
                    script.addScript ("if (top.search.getSelectElem)")
                        .addScript ("{ var selectElem = top.search.getSelectElem (); }")
                        .addScript ("else { var selectElem = top.search.document.getElementById (\"slct\"); }")
                        .addScript ("if (selectElem) {")

                    // add an empty entry:
                        .addScript ("var optionElem = top.search.document.createElement (\"option\");")
                        .addScript ("optionElem.value = \"\";")
                        .addScript ("optionElem.innerHTML = \"\";")
                        .addScript ("selectElem.appendChild (optionElem);");

                    // get the queries
                    while (!action.getEOF ())
                    {
                        // If available get the multilingual name for this query
                        queryName = action.getString ("name");
                        // keep the original name for multilingual access parts
                        String queryOrgName = queryName;
                        String queryNameLookupKey = MultilingualTextProvider
                            .getQueryGenericLookupKey (queryOrgName, queryOrgName, 
                                MultilangConstants.LOOKUP_KEY_POSTFIX_NAME);
                        // Retrieve the ml text with the defined lookup key
                        MultilingualTextInfo mlValueInfoName = MultilingualTextProvider
                            .getMultilingualTextInfo (
                                MultilangConstants.RESOURCE_BUNDLE_QUERIES_NAME,
                                queryNameLookupKey,
                                MultilingualTextProvider.getUserLocale (env), env);
                        if (mlValueInfoName.isFound ())
                        {
                            queryName = mlValueInfoName.getMLValue ();
                        } // if

                        // If available get the multilingual description for this query                        
                        queryDesc = action.getString ("description");
                        String queryDescLookupKey = MultilingualTextProvider
                            .getQueryGenericLookupKey (queryOrgName, queryOrgName, 
                                MultilangConstants.LOOKUP_KEY_POSTFIX_DESCRIPTION);
                        // Retrieve the ml text with the defined lookup key
                        MultilingualTextInfo mlValueInfoDesc = MultilingualTextProvider
                            .getMultilingualTextInfo (
                                MultilangConstants.RESOURCE_BUNDLE_QUERIES_NAME,
                                queryDescLookupKey,
                                MultilingualTextProvider.getUserLocale (env), env);
                        if (mlValueInfoDesc.isFound ())
                        {
                            queryDesc = mlValueInfoDesc.getMLValue ();
                        } // if

                        // read the data form the query
                        // If available get the multilingual category for this query
                        queryCategory = action.getString ("category");
                        String queryCatLookupKey = MultilingualTextProvider
                            .getQueryGenericLookupKey (queryOrgName, queryOrgName, 
                                MultilangConstants.LOOKUP_KEY_POSTFIX_CATEGORY);
                        // Retrieve the ml text with the defined lookup key
                        MultilingualTextInfo mlValueInfoCat = MultilingualTextProvider
                            .getMultilingualTextInfo (
                                MultilangConstants.RESOURCE_BUNDLE_QUERIES_NAME,
                                queryCatLookupKey,
                                MultilingualTextProvider.getUserLocale (env), env);
                        if (mlValueInfoCat.isFound ())
                        {
                            queryCategory = mlValueInfoCat.getMLValue ();
                        } // if
                        
                        queryOidStr = action.getString ("oid");

                        // check if we need to add a optgroup for the category
                        if (queryCategory != null & !queryCategory.isEmpty () &&
                            !queryCategory.equals (actQueryCategory))
                        {
                            // add an <optgroup> for the category
                            script.addScript ("var categoryElem = top.search.document.createElement (\"optgroup\");")
                                .addScript ("categoryElem.label = \"" + queryCategory + "\";")
                                .addScript ("selectElem.appendChild (categoryElem);");
                            // remember the actual queryCategory
                            actQueryCategory = queryCategory;
                        } // if (queryCategory != null &&

                        script.addScript ("var optionElem = top.search.document.createElement (\"option\");")
                            .addScript ("optionElem.value = \""  + queryOidStr + "\";");
                        // check if we can use the description as title
                        if (!queryDesc.trim ().isEmpty ())
                        {
                            // replace crucial characters within the description
                            script.addScript ("optionElem.title = \""  +
                                queryDesc.replace ("\"", "\\\"")
                                         .replace ("\n", "\\n")
                                         .replace ("\r", "\\r") +
                                         "\";");
                        } // if (!queryDesc.trim().equals (""))
                        script.addScript ("optionElem.innerHTML = \""  + queryName + "\";");
                        // empty category?
                        if (queryCategory == null || queryCategory.isEmpty ())
                        {
                            // if no category set add the option to the select
                            script.addScript ("selectElem.appendChild (optionElem);");
                        } // if (queryCategory != null)
                        else // no category set
                        {
                            // if no category set add the option to the optgroup
                            script.addScript ("categoryElem.appendChild (optionElem);");
                        } // else no category set
                        action.next ();
                    } // while (!action.getEOF())
                    script.addScript ("}")
                        .addScript ("else { alert (\"Could not find search frame or search selection box!\"); }");
                } // if (rowCount > 0)
                // end transaction
                action.end ();
            } // try
            catch (DBError e)
            {
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
            finally
            {
                try
                {
                    DBConnector.releaseDBConnection (action);
                } // try
                catch (DBError e)
                {
                    IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
                } // catch
            } // finally

            script.addScript ("top.tryCall ('top.search.refresh ()');");
            page.head.addElement (script);
        } // if selection list shall be displayed

        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            // should not occur
            // display error message:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // buildCallingSearchForm


    /**************************************************************************
     * show the searchform with the selection over all possible queries. <BR/>
     */
    protected void showSearchSelectionForm ()
    {
        this.trace ("FunctionSearch.showSearchSelectionForm");
        OID currentObjectOid = null;    // oid of current object

        currentObjectOid = this.env.getOidParam (BOArguments.ARG_OID);

        // create page:
        Page page = new Page ("Search Form", false);

        // Stylesheetfile is loaded:
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);

        // create Header:
        FormElement form = this.createFormHeader (page, null,
            this.getNavItems (), null, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FUNCTIONSEARCH, env), null,
            "SearchContainer.gif", this.containerName);


        // add hidden elements:
        // oid of queryexecutive:
        form.addElement (new InputElement (BOArguments.ARG_OID,
            InputElement.INP_HIDDEN, "" + ((this.queryExec == null) ?
                                            OID.EMPTYOID :
                                            this.queryExec.oid.toString ())));
        // containerId (for some m2-base reasons):
        form.addElement (new InputElement (BOArguments.ARG_CONTAINERID,
            InputElement.INP_HIDDEN, "" + this.containerId));
        // function id:
        form.addElement (new InputElement (BOArguments.ARG_FUNCTION,
            InputElement.INP_HIDDEN, "" + AppFunctions.FCT_OBJECTSEARCH));
        // event to be thrown on submit:
        form.addElement (new InputElement (FunctionArguments.ARG_EVENT,
            InputElement.INP_HIDDEN, "" +
            SearchEvents.EVT_CLICKOKSEARCHSELECTIONFORM));
        // oid of current object:
        form.addElement (new InputElement (SearchArguments.ARG_CURRENTOBJECTOID,
            InputElement.INP_HIDDEN, "" + ((currentObjectOid == null) ?
                                            OID.EMPTYOID :
                                            currentObjectOid.toString ())));


        // create inner table:
        TableElement table = new TableElement (2);
        table.border = 0;
        table.width = HtmlConstants.TAV_FULLWIDTH;

        table.classId = CssConstants.CLASS_INFO;
        String[] classIds = new String[2];
        classIds[0] = CssConstants.CLASS_NAME;
        classIds[1] = CssConstants.CLASS_VALUE;
        table.classIds = classIds;


        // get selectionlist of all reporttemplates in system, where
        // the current user has rights on:
        SelectionList queryObjectList =
            this.performRetrieveQueryCreatorSelectionList (false);

        // show selectionbox for querytemplate:
        this.showFormProperty (table
                          , SearchArguments.ARG_QUERYCREATOROID
                          , SearchTokens.TOK_QUERYCREATORSELECTION
                          , Datatypes.DT_SELECT
                          , ""
                          , queryObjectList.ids
                          , queryObjectList.values
                          , 1 // index of preselected if preseclected value (queryObjectOid)
                          //is not set
        );

        // deactivate tabs and buttons:
        page.body.addElement (this.getButtonsTabsDeactivationScript ());

        form.addElement (table);

        // create footer - do not show cancelbutton (last parameter = true):
        this.createFormFooter (form, null, null, null, null, false, true);

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage ("FunctionSearch.showSearchSelectionForm",
                                   e.getMsg (), this.app, this.sess, this.env);
        } // catch
    } // showSearchSelectionForm


    /**************************************************************************
     * show search with given query, currentObject and rootObject. <BR/>
     *
     * @param   queryOid    oid of queryObject to be shown in search
     * @param   currentOid  oid of currentObject
     * @param   rootOid     oid of rootObject for searchQuery
     */
    public void showSearch (OID queryOid, OID currentOid, OID rootOid)
    {
        this.trace ("FunctionSearch.showSearch (" + queryOid + ", " +
            currentOid + ", " + rootOid + ")");
        this.showSearchFrame (queryOid, null, currentOid, rootOid);
    } // showSearch


    /**************************************************************************
     * show search with given query, currentObject and rootObject. <BR/>
     *
     * @param   queryName   name of queryObject to be shown in search
     * @param   currentOid  oid of currentObject
     * @param   rootOid     oid of rootObject for searchQuery
     */
    public void showSearch (String queryName, OID currentOid, OID rootOid)
    {
        this.trace ("FunctionSearch.showSearch (" + queryName + ", " +
            currentOid + ", " + rootOid + ")");
        this.showSearchFrame (null, queryName, currentOid, rootOid);
    } // showSearch


    /**************************************************************************
     * show Frame for searchForm. <BR/>
     * gets data for search from environment. <BR/>
     */
    protected void showSearchFrame ()
    {
        this.trace ("FunctionSearch.showSearchFrame");
        OID currentObjectOid = null;
        OID queryCreatorOid = null;
        String queryCreatorName = null;
        OID rootObjectOid = null;

        // get oid of current object:
        currentObjectOid =
            this.env.getOidParam (SearchArguments.ARG_CURRENTOBJECTOID);

        // get oid of used query creator:
        queryCreatorOid =
            this.env.getOidParam (SearchArguments.ARG_QUERYCREATOROID);

        // get name of used query creator:
        queryCreatorName =
            this.env.getStringParam (SearchArguments.ARG_QUERYCREATORNAME);

        // get oid of object which is the search root:
        rootObjectOid =
            this.env.getOidParam (SearchArguments.ARG_ROOTOBJECTOID);

        this.showSearchFrame (queryCreatorOid, queryCreatorName,
            currentObjectOid, rootObjectOid);
    } // showSearchFrame


    /***************************************************************************
     * show Frame for searchForm. <BR/>
     * One of Param queryOid or queryName had to be set. <BR/>
     *
     * @param   queryOid    oid of queryObject to be shown in search
     * @param   queryName   name od queryObject (instead of queryOid)
     * @param   currentOid  oid of currentObject
     * @param   rootOid     oid of rootObject for searchQuery
     */
    protected void showSearchFrame (OID queryOid, String queryName,
                                    OID currentOid, OID rootOid)
    {
        this.trace ("FunctionSearch.showSearchFrame (" + queryOid + ", " +
            queryName + ", " + currentOid + ", " + rootOid + ")");

        // The search has been removed from the history list. The last search
        // result can always be access
        // by the back to search button. The history list is only for objects.
/*
        // If the last object in history is already a search remove the last history entry
        if(this.getUserInfo ().history != null && this.getUserInfo ().history.getName() != null &&
                this.getUserInfo ().history.getName().equals(HistoryInfo.HISTORY_ENTRY_TYPE_QUERY))
        {
            this.getUserInfo ().history.go(-1);
        } // if

        // Add the search to the history
        this.getUserInfo ().history.add
            (this.queryExec.oid, TabConstants.TAB_NONE, HistoryInfo.HISTORY_ENTRY_TYPE_QUERY, null,
                    this.queryExec.typeName);
*/

        // start with the container representation: show frameset
        Page page = new Page (true);    // the output page
        FrameSetElement frameset = new FrameSetElement (this.framesAsRows, 2);

        // build url to show searchproperties in first frame
        String frame1Url = this.getBaseUrlGet () +
                // function for building first frame
                HttpArguments.createArg (BOArguments.ARG_FUNCTION,
                                         AppFunctions.FCT_OBJECTSEARCH) +
                // event to show searchfields
                HttpArguments.createArg (FunctionArguments.ARG_EVENT,
                                         SearchEvents.EVT_SHOWSEARCHFORM) +
                // oid of selected querycreator (type to be searched)
                HttpArguments.createArg (SearchArguments.ARG_QUERYCREATOROID,
                                         queryOid == null ?
                                            OID.EMPTYOID :
                                            queryOid.toString ()) +
                // oid of current object when search was started
                HttpArguments.createArg (SearchArguments.ARG_CURRENTOBJECTOID,
                                         currentOid == null ?
                                            OID.EMPTYOID :
                                            currentOid.toString ()) +
                // oid of object which is the rootobject of search
                HttpArguments.createArg (SearchArguments.ARG_ROOTOBJECTOID,
                                         rootOid == null ?
                                            OID.EMPTYOID :
                                            rootOid.toString ()) +
                // oid of queryexecutive
                HttpArguments.createArg (BOArguments.ARG_OID,
                                         this.queryExec == null ?
                                            OID.EMPTYOID :
                                            this.queryExec.oid.toString ());
        // if parameter queryCreatorName was set instead of queryCreatorOid,
        // add queryCreatorName as Argument in URL
        if (queryName != null)
        {
            frame1Url +=
                HttpArguments.createArg (SearchArguments.ARG_QUERYCREATORNAME,
                                         queryName);
        } // if


        // show emptypage in second frame
        String frame2Url = this.getUserInfo ().homepagePath +
            AppConstants.FILE_EMPTYPAGE;

        // set urls for frames
        FrameElement frm1Frame =
            new FrameElement (HtmlConstants.FRM_SHEET1, frame1Url);
        FrameElement frm2Frame =
            new FrameElement (HtmlConstants.FRM_SHEET2, frame2Url);

        // insert frames into frameset:
//        frm1Frame.frameborder = true;
        frm1Frame.resize = true;
//        frm2Frame.frameborder = true;
        frm2Frame.resize = true;
        frameset.addElement (frm1Frame, this.frm1Size);
        frameset.addElement (frm2Frame, this.frm2Size);
        frameset.frameborder = true;
        frameset.frameSpacing = 1;

        // add frameset to page
        page.body.addElement (frameset);

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage ("FunctionSearch.showSearchFrame",
                                   e.getMsg (), this.app, this.sess, this.env);
        } // catch
    } // showSearchFrame


    /**************************************************************************
     * show the searchform with the selectionfields - use an QueryExecutive.
     * <BR/>
     */
    protected void showSearchForm ()
    {
        this.trace ("FunctionSearch.showSearchForm");
        // try to get current Data for QueryExecutive
        if (!this.setQueryExecutiveData ())
        {
            // if an error occured, some exception is shown
            // -> do not show search form and leave method
            return;
        } // if

        // show the search form within query executive:
        this.queryExec.showSearchForm ();
    } // showSearchForm


    /**************************************************************************
     * perform the search. <BR/>
     */
    protected void showResultForm ()
    {
//this.debug ("AJ FunctionSearch.search");
        // query is currently executed
        // do not overwrite saved search and match type values
        this.queryExec.setIsQueryExecuted (true);
        // try to get current Data for QueryExecutive
        if (!this.setQueryExecutiveData ())
        {
            // if an error occured, some exception is shown
            // -> do not show search form and leave method
            return;
        } // if
        // query was currently executed
        this.queryExec.setIsQueryExecuted (false);
        // ensure that the query executive don't display the search form:
        boolean isShowSearchForm = this.queryExec.isShowSearchForm ();
        this.queryExec.setShowSearchForm (false);
        // show resultset
        this.queryExec.showContent (0);
        // set the showSearchForm to original value:
        this.queryExec.setShowSearchForm (isShowSearchForm);
    } // search


    /**************************************************************************
     * get data for query executive (retrieve queryexecutive ...). <BR/>
     *
     * @return  <CODE>true</CODE> if everything was o.k.,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean setQueryExecutiveData ()
    {
//this.debug ("AJ FunctionSearch.getQueryExecutiveData");
        OID localOid = null;
        String str = null;
        boolean ok = false;

        // get oid of selected querycreator
        // get oid of object which was current object, when search-button was pressed
        if ((localOid = this.env
            .getOidParam (SearchArguments.ARG_CURRENTOBJECTOID)) != null)
        {
            this.queryExec.setCurrentObjectOid (localOid);
        } // if

        // get oid of object where search should be performed  (could be used
        // for javascript interface for buttons in xml-forms or stylesheets)
        if ((localOid = this.env
            .getOidParam (SearchArguments.ARG_ROOTOBJECTOID)) != null)
        {
            this.queryExec.setRootObjectOid (localOid);
        } // if

        // get name of querycreator to be executed
        if ((str = this.env
            .getStringParam (SearchArguments.ARG_QUERYCREATORNAME)) != null)
        {
            this.queryExec.setQueryCreatorName (str);
            // prepare query with querycreatoroid to be executed
            ok = this.queryExec.prepareQueryCreator ();
        } // if
        else if ((localOid = this.env
            .getOidParam (SearchArguments.ARG_QUERYCREATOROID)) != null)
                // get oid of selected querycreator
        {
            this.queryExec.setQueryCreatorOid (localOid);
            // prepare query with querycreatoroid to be executed
            ok = this.queryExec.prepareQueryCreator ();
        } // else if

        // getParameters of searchForm
        this.queryExec.getParameters ();

        // return result of queryobject preparation
        return ok;
    } // getQueryExecutiveData


    /**************************************************************************
     * Get a selectionList with queryCreators which should be
     * selectable in advanced search out of the database. <BR/>
     *
     * @param   isNullable  Is there a placeholder for no object within the
     *                      selection list? <BR/>
     *                      true:   first row in selection list will be
     *                              placeholder for no object. <BR/>
     *                      false:  no placeholder for no object will be
     *                              included, except the selection list would
     *                              otherwise be empty (this behaviour is
     *                              implemented only cause I don't know how
     *                              showFormProperty handles an empty
     *                              selection list).
     *
     * @return  The selection list.
     */
    protected SelectionList performRetrieveQueryCreatorSelectionList (
                                                                      boolean isNullable)
    {
        this.trace ("FunctionSearch.performRetrieveQueryCreatorSelectionList BEGIN");
        StringBuffer queryStr;          // the query string

        // get all QueryCreators of the current domain.
        // create the SQL String to select all tuples:
        queryStr = new StringBuffer ()
            .append ("SELECT distinct")
            .append (" s.oid, s.name")
            .append (" FROM ").append (this.viewSelectionList).append (" s,")
            .append (" ibs_QueryCreator_01 q , ibs_Object o,")
                .append (" (SELECT posNoPath")
                .append (" FROM ibs_Domain_01 d, ibs_Object o")
                .append (" WHERE d.oid = o.oid")
                .append (" AND d.id = ").append (this.user.domain)
                .append (" )p")
            .append (" WHERE s.userId = ").append (this.user.id)
            .append (SQLHelpers.getStringCheckRights (Operations.OP_VIEW))
            .append (" AND s.tVersionId = ")
                .append (this.getTypeCache ().getTVersionId (TypeConstants.TC_QueryCreator))
            .append (" AND s.oid = o.oid ")
// AJ HACK BEGIN
            // get only queryCreators which should be selectable for advanced
            // search:
            .append (" AND ")
            .append (SQLHelpers.getBitAnd ("q.queryType", "1")).append ("= 1")
            .append (" AND ").append (SQLHelpers.getQueryConditionAttribute (
                "o.posNoPath", SQLConstants.MATCH_STARTSWITH, "p.posNoPath", true))
// AJ HACK END
            .append (" AND s.oid = q.oid ")
            .append (" ORDER BY s.name ").append (BOConstants.ORDER_ASC);
//debug ("Query: " + queryStr);

        // returns a selection list which contains all selectable queryCreators
        // for the advanced search from the database:
        return
            this.performRetrieveSelectionListDataQuery (isNullable, queryStr, "");
    } // performRetrieveQueryCreatorSelectionList

} // FunctionSearch

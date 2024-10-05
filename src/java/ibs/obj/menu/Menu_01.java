/*
 * Class: Menu_01.java
 */

// package:
package ibs.obj.menu;

// imports:
import ibs.bo.BOMessages;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.bo.States;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.BuildException;
import ibs.tech.html.MenuElement;
import ibs.tech.html.MenuRootElement;
import ibs.tech.html.Page;
import ibs.tech.html.TextElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.SelectQuery;
import ibs.util.Helpers;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This class represents one object of type Menu with version 01. <BR/>
 *
 * @version     $Id: Menu_01.java,v 1.26 2010/04/28 10:02:56 btatzmann Exp $
 *
 * @author      Keim Christine (CK), 980525
 ******************************************************************************
 */
public class Menu_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Menu_01.java,v 1.26 2010/04/28 10:02:56 btatzmann Exp $";


    /**
     * The data of the menu tab where this menu belongs to. <BR/>
     */
    private MenuData_01 p_menuData = null;

    /**
     * Oid of object which is the search root for incremental search. <BR/>
     * If <CODE>null</CODE> the whole menu tree is searched. <BR/>
     * Default: <CODE>null</CODE>
     */
    private OID p_rootObjOid = null;

    /**
     * Nodes which are currently open within the menu. <BR/>
     */
    private String p_openNodes = null;

    /**
     * @deprecated  Should not be necessary furthermore.
     */
    @Deprecated
    private long p_durationDatabaseQuery = 0;
    /**
     * @deprecated  Should not be necessary furthermore.
     */
    @Deprecated
    private long p_durationStructureCreation1 = 0;
    /**
     * @deprecated  Should not be necessary furthermore.
     */
    @Deprecated
    private long p_durationStructureCreation2 = 0;



    /**************************************************************************
     * Creates a Menu_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     */
    public Menu_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // Menu_01


    /**************************************************************************
     * Creates a Menu_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    @Deprecated
    public Menu_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // Menu_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // nothing to do
    } // initClassSpecifics


    /**************************************************************************
     * Sets the menu data of the menu tab. <BR/>
     * The menu data is stored in the {@link #p_menuData}
     * property of this object. <BR/>
     *
     * @param   menuData    Value to be set.
     */
    public void setMenuData (MenuData_01 menuData)
    {
        // set the new oid:
        this.p_menuData = menuData;
    } // setMenuData


    /**************************************************************************
     * Sets the oid of the search root. <BR/>
     * The compound object id is stored in the
     * {@link #p_rootObjOid p_rootObjOid} property of this object. <BR/>
     *
     * @param   rootObjOid  Value to be set.
     */
    public void setRootObjOid (OID rootObjOid)
    {
        // set the new oid:
        this.p_rootObjOid = rootObjOid;
    } // setRootObjOid


    /**************************************************************************
     * Sets the open nodes within the menu. <BR/>
     * The value is stored in the
     * {@link #p_openNodes p_openNodes} property of this object. <BR/>
     *
     * @param   openNodes   Value to be set.
     */
    public void setOpenNodes (String openNodes)
    {
        // set the new value:
        this.p_openNodes = openNodes;
    } // setOpenNodes


    /**************************************************************************
     * Get the data of the root object out of the database. <BR/>
     *
     * @param   action      Db connection to be used for database operations.
     * @param   rootObjOid  Oid of root object for which to get the data.
     * @param   operation   Operation to be performed with the objects.
     * @param   user        User for whom to get the data.
     *
     * @return  A vector containing 2 elements:
     *          <PRE>
     *          String  rootPosNoPath
     *          Integer rootOLevel
     *          </PRE>
     *          <CODE>null</CODE> if no data for the root object were found.
     *
     * @exception   NoAccessException
     *              The user does not have access to these objects to perform
     *              the required operation.
     */
//    @SuppressWarnings ("unchecked")
    private Vector<Object> performRetrieveRootData (SQLAction action, OID rootObjOid,
                                            int operation, User user)
        throws NoAccessException
    {
        Vector<Object> retVal = null;        // return value
        SelectQuery query;              // the query
        int rowCount = 0;               // row counter

        // get the data out of the database:
        query = new SelectQuery (new StringBuilder ("v.posNoPath, v.oLevel"),
            new StringBuilder ("ibs_Object v, ibs_RightsCum r"),
            new StringBuilder ("v.oid = ").append (rootObjOid.toStringBuilderQu ())
                .append (SQLHelpers.getStringCheckRights (operation, "r.rights"))
                .append (" AND v.rKey = r.rKey")
                .append (" AND r.userId = ").append (user.id), null, null, null);
/*
            .append ("SELECT v.containerId, v.oid, v.name, v.posNoPath,")
                .append (" v.isLink, v.linkedObjectId, v.icon, v.oLevel")
*/

        try
        {
            // execute the query:
            rowCount = action.execute (query);

            // get tuples out of db
            if (rowCount > 0 && !action.getEOF ())
            {
                retVal = new Vector<Object> (2);
                // add values to result of method:
                retVal.add (action.getString ("posNoPath"));
                retVal.add (new Integer (action.getInt ("oLevel")));
            } // if

            // the last tuple has been processed
            // end transaction
            action.end ();
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch

        // return the result:
        return retVal;
    } // performRetrieveRootData


    /**************************************************************************
     * Get the menu's content out of the database. <BR/>
     * First this method tries to load the objects from the database. During
     * this operation a rights check is done, too. If this is all right the
     * objects are returned otherwise an exception is raised. <BR/>
     *
     * @param   tree            Menu tree structure where the objects have to be
     *                          added.
     * @param   action          Db connection for performing database operations.
     * @param   rootObjOid      Oid of root object for which to get the data.
     * @param   rootPosNoPath   posNoPath of root object.
     * @param   rootOLevel      oLevel of root object.
     *
     * @return  Number of elements within the menu.
     *
     * @exception   NoAccessException
     *              The user does not have access to these objects to perform
     *              the required operation.
     */
    private int performRetrieveMenuContentData (MenuRootElement tree,
                                                SQLAction action,
                                                OID rootObjOid,
                                                String rootPosNoPath,
                                                int rootOLevel)
        throws NoAccessException
    {
        StringBuilder queryStr;          // the query string
        StringBuilder queryPath;
        StringBuilder querySelect;       // additional elements for SELECT clause
        int rowCount = 0;               // row counter
        int stepLevels = this.p_menuData.p_levelStep; // levels per step
        int counter = 0;                // number of actual element
        Vector<MenuElement> tracking = new Vector<MenuElement> ();
        int[] positions = new int[50];
        long start = 0;                 // start time of measured duration
        long end = 0;                   // end time of measured duration

        // compute correct step levels:
        if (tree.p_startLevel >= this.p_menuData.p_levelStepMax)
        {
            stepLevels = 0;
        } // if

        // check if we have some open nodes:
        if (this.p_openNodes == null)   // no open nodes?
        {
            // create query for retrieving the data of the menu elements:
            switch (stepLevels)
            {
                case 0:                 // get all levels
                    querySelect = new StringBuilder ()
                        .append (", 0 AS hasSubNodes");
                    queryPath = new StringBuilder ()
                        .append (" WHERE ")
                        .append (SQLHelpers.getQueryConditionString (
                            "v.posNoPath", SQLConstants.MATCH_STARTSWITH,
                            rootPosNoPath, true))
//                    .append (SQLHelpers.getStringCheckRights (Operations.OP_VIEW))
                        .append (" AND v.oid NOT IN (")
                            .append (rootObjOid.toStringBuilderQu ())
                            .append (")")
                        .append (" AND v.oLevel > ").append (rootOLevel);
                    break;

                case 1:
                    querySelect = new StringBuilder ()
                        .append (", o.hasSubNodes");
                    queryPath = new StringBuilder ()
                        .append (", v_Menu_01$menuLevels1Sub o")
                        .append (" WHERE v.oid = o.oid")
                        .append (" AND ").append (rootObjOid.toStringBuilderQu ())
                        .append (" IN (o.oid1)");
//                        .append (" WHERE v.containerId = ").append (this.oid.toStringQu ());
                    break;

                case 2:
                    querySelect = new StringBuilder ()
                        .append (", o.hasSubNodes");
                    queryPath = new StringBuilder ()
                        .append (", v_Menu_01$menuLevels2Sub o")
                        .append (" WHERE v.oid = o.oid")
                        .append (" AND ").append (rootObjOid.toStringBuilderQu ())
                        .append (" IN (o.oid2, o.oid1)");
/*
                    queryPath = new StringBuilder ()
                        .append (", ibs_Object o")
                        .append (" WHERE v.oid = o.oid")
                        .append (" AND (o.containerId = ").append (this.oid.toStringQu ())
                        .append (" OR o.containerOid2 = ").append (this.oid.toStringQu ())
                        .append (")");
*/
                    break;

                case 3:
                    querySelect = new StringBuilder ()
                        .append (", o.hasSubNodes");
                    queryPath = new StringBuilder ()
                        .append (", v_Menu_01$menuLevels3Sub o")
                        .append (" WHERE v.oid = o.oid")
                        .append (" AND ").append (rootObjOid.toStringBuilderQu ())
                        .append (" IN (o.oid3, o.oid2, o.oid1)");
                    break;

                case 4:
                    querySelect = new StringBuilder ()
                        .append (", o.hasSubNodes");
                    queryPath = new StringBuilder ()
                        .append (", v_Menu_01$menuLevels4Sub o")
                        .append (" WHERE v.oid = o.oid")
                        .append (" AND ").append (rootObjOid.toStringBuilderQu ())
                        .append (" IN (o.oid4, o.oid3, o.oid2, o.oid1)");
                    break;

                case 5:
                    querySelect = new StringBuilder ()
                        .append (", o.hasSubNodes");
                    queryPath = new StringBuilder ()
                        .append (", v_Menu_01$menuLevels5Sub o")
                        .append (" WHERE v.oid = o.oid")
                        .append (" AND ").append (rootObjOid.toStringBuilderQu ())
                        .append (" IN (o.oid5, o.oid4, o.oid3, o.oid2, o.oid1)");
                    break;

                default:
                    querySelect = new StringBuilder ()
                        .append (", o.hasSubNodes");
                    queryPath = new StringBuilder ()
                        .append (", v_Menu_01$menuLevelsNSub o")
                        .append (" WHERE v.oid = o.oid")
                        .append (" AND ")
                        .append (SQLHelpers.getQueryConditionString (
                            "o.posNoPathN", SQLConstants.MATCH_EXACT,
                            rootPosNoPath, true))
//                    .append (SQLHelpers.getStringCheckRights (Operations.OP_VIEW))
                        .append (" AND v.oid <> ").append (rootObjOid.toStringBuilderQu ())
                        .append (" AND v.oLevel > ").append (rootOLevel)
                        .append (" AND v.oLevel <= ").append (rootOLevel + stepLevels);
                    break;
            } // switch levelStep
        } // if no open nodes
        else                            // there are some open nodes
        {
            querySelect = new StringBuilder ()
                .append (", o.hasSubNodes");
            queryPath = new StringBuilder ()
                .append (", v_Menu_01$menuLevels1Sub o")
                .append (" WHERE v.oid = o.oid")
                .append (" AND v.containerId IN (")
                    .append (rootObjOid.toStringBuilderQu ());

            if (this.p_openNodes.trim ().length () > 0)
            {
                queryPath
                    .append (",")
                    .append (this.p_openNodes);
            } // if

            queryPath
                .append (")")
                .append (" AND ")
                    .append (SQLHelpers.getQueryConditionString (
                        "v.posNoPath", SQLConstants.MATCH_STARTSWITH,
                        rootPosNoPath, true))
                .append (" AND v.oLevel > ").append (rootOLevel);
        } // else there are some open nodes

        queryStr = new StringBuilder ()
            .append ("SELECT v.containerId, v.oid, v.name, v.rights,")
                .append (" v.isLink, v.linkedObjectId, v.icon, v.oLevel, v.extId, v.extDomain")
                .append (querySelect)
            .append (" FROM v_Menu_01$content v")
            .append (queryPath)
                .append (" AND v.userId = ").append (this.user.id)
            .append (" ORDER BY v.posNoPath ASC");
/* KR old query with container specific ordering:
        queryStr = new StringBuilder ()
            .append ("SELECT v.containerId, v.oid, v.name, v.rights,")
                .append (" v.isLink, v.linkedObjectId, v.icon, v.oLevel")
                .append (querySelect)
            .append (" FROM v_Menu_01$content v, ibs_Object c")
            .append (queryPath)
                .append (" AND v.userId = ").append (this.user.id)
                .append (" AND v.containerId = c.oid")
                .append (" AND c.state = ").append (States.ST_ACTIVE)
            .append (" ORDER BY c.posNoPath ASC, v.name ASC");
*/

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            start = System.currentTimeMillis ();
            rowCount = action.execute (queryStr, false);
            end = System.currentTimeMillis ();
            tree.p_durationDatabaseQuery = end - start;

            start = System.currentTimeMillis ();
            // check for empty resultset:
            if (rowCount > 0)
            {
                tracking.add (tree);
                counter++;

                // get tuples out of db
                while (!action.getEOF ())
                {
                    // check if the user is allowed to view the object:
                    if ((action.getInt ("rights") & Operations.OP_VIEW) ==
                        Operations.OP_VIEW)
                    {
                        StringBuffer oidStr;

                        // get the actual element data:
                        OID oid = SQLHelpers.getQuOidValue (action, "oid");
                        OID containerOid =
                            SQLHelpers.getQuOidValue (action, "containerId");
                        int oLevel = action.getInt ("oLevel");
                        String name = action.getString ("name");
                        boolean isLink = action.getBoolean ("isLink");
                        OID linkedObjectId =
                            SQLHelpers.getQuOidValue (action, "linkedObjectId");
                        String icon = action.getString ("icon");
                        boolean hasSubNodes = action.getBoolean ("hasSubNodes");
                        
                        // get the extKey element data:
                        String extId = action.getString ("extId");
                        String extDomain = action.getString ("extDomain");

                        if (isLink)
                        {
                            oidStr = linkedObjectId.toStringBuffer ();
                        } // if
                        else
                        {
                            oidStr = oid.toStringBuffer ();
                        } // else

                        String escName = StringHelpers.escape (name); // the escaped name

                        // Retrieve the multilang name
                        String mlName = MultilingualTextProvider.
                            getMultilangObjectName (extId, extDomain, escName, this.env);

                        MenuElement m1 = new MenuElement (mlName, oidStr);
                        m1.id = oid.toString ();
                        m1.p_upperId = containerOid.toString ();
                        m1.p_key = oid;
                        m1.name = escName;
                        m1.icon = icon;
                        m1.p_hasSubNodes = hasSubNodes;

                        // tracking of the MenuElements!
                        tracking.add (m1);

                        // add to the correct container element:
                        int j = positions[oLevel - 1]; // element above actual element
                        while ((j >= 0) &&
                               !containerOid.equals (tracking.elementAt (j).p_key))
                        {
                            j--;
                        } // while

                        if (j > -1)
                        {
                            tracking.elementAt (j).addElement (m1);
                            positions[oLevel] = counter;
                        } // if

                        counter++;
                    } // if

                    // step one tuple ahead for the next loop
                    action.next ();
                } // while
            } // if
            end = System.currentTimeMillis ();
            tree.p_durationStructureCreation = end - start;

            // the last tuple has been processed
            // end transaction
            action.end ();
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
        } // catch

        // return the number of elements within the menu:
        return counter;
    } // performRetrieveMenuContentData


    /**************************************************************************
     * Get the Menu_01's content out of the database. <BR/>
     * <BR/>
     * First this method tries to load the objects from the database. During
     * this operation a rights check is done, too. If this is all right the
     * objects are returned otherwise an exception is raised. <BR/>
     *
     * @exception   NoAccessException
     *              The user does not have access to these objects to perform
     *              the required operation.
     */
    public void showMenu ()
        throws NoAccessException
    {
        SQLAction action = null;        // SQLAction for Databaseoperation
        OID menuObjOid = this.p_menuData.oid; // oid of menu bar
        OID rootObjOid = this.p_rootObjOid; // oid of root object
        String rootPosNoPath = null;    // posnopath of first object (root)
        int rootOLevel = 0;             // level of root object
        Vector<?> objData = null;       // vector containing the data of a
                                        // root object

        // check if a root object was set:
        if (rootObjOid == null || rootObjOid.isEmpty () || rootObjOid.isTemp ())
        {
            // search within the whole tree:
            rootObjOid = menuObjOid;
        } // if

//IOHelpers.showMessage (this.p_openNodes, this.app, this.sess, this.env);
        // get the elements out of the database:
//IOHelpers.printTrace ("    before getFirst");
        // open db connection -  only workaround - db connection must
        // be handled somewhere else
        action = this.getDBConnection ();

        // check if we have to get the root data:
        if (!rootObjOid.equals (menuObjOid))
        {
            objData = this.performRetrieveRootData (action, rootObjOid,
                Operations.OP_VIEW, this.user);

            // check if the root object was found:
            if (objData != null)
            {
                rootPosNoPath = (String) objData.get (0);
                rootOLevel = ((Integer) objData.get (1)).intValue ();
            } // if
            else
            {
                this.releaseDBConnection (action);
                return;
            } // else
        } // if
        else
        {
            // use data of menu bar itself:
            rootPosNoPath = this.p_menuData.p_posNoPath;
            rootOLevel = this.p_menuData.p_oLevel;
        } // else

        Page page = new Page (false);   // the page
        TextElement text = null;        // the text
        int counter = 0;                // number of elements within the menu

        // create the menu root:
        MenuRootElement tree = new MenuRootElement ("oid");
        tree.id = this.name;
        tree.name = this.name;
        tree.p_key = rootObjOid;
        tree.p_startLevel = rootOLevel - this.p_menuData.p_oLevel + 1;
        // check if we are having a complete tree or just part of it:
        if (!rootObjOid.equals (menuObjOid))
        {
            tree.p_rootObjId = rootObjOid.toString ();
        } // if
        if (this.p_openNodes == null)
        {
            tree.p_mode = MenuRootElement.MMODE_CREATE;
        } // if
        else
        {
            tree.p_mode = MenuRootElement.MMODE_SYNC;
        } // else

        counter = this.performRetrieveMenuContentData (tree, action, rootObjOid,
            rootPosNoPath, rootOLevel);

        // close db connection in every case -  only workaround - db connection must
        // be handled somewhere else
        this.releaseDBConnection (action);

        if (counter <= 1)               // there are no elements?
        {
            // show the according message to the user:
            text = new TextElement (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_CONTAINEREMPTY, env));
            page.body.addElement (text);
        } // if there are no elements

        tree.maxlevels = tree.getMaxLevel ();
        page.body.addElement (tree);

        try
        {
//IOHelpers.printTrace ("performShowContent before page.build");
            page.build (this.env);
//IOHelpers.printTrace ("performShowContent after page.build");
        } // try

        catch (BuildException e)
        {
            // show the according message to the user:
            IOHelpers.showMessage ("Menu_01.performShowContent", e.getMsg (),
                this.app, this.sess, this.env);
        } // catch
    } // showMenu


    /**************************************************************************
     * Get the Menu_01's content out of the database. <BR/>
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
     *
     * @deprecated  This method should not longer be used. Use
     *              {@link #performRetrieveMenuContentData(MenuRootElement, SQLAction, OID, String, int) }
     *              instead.
     */
    @Deprecated
    protected void performRetrieveContentData (int operation, int orderBy,
                                               String orderHow)
        throws NoAccessException
    {
        SQLAction action = null;        // SQLAction for Databaseoperation
        ContainerElement obj = null;
        StringBuffer queryStrFirst;     // the query string for first menuelement
        StringBuffer queryStrFollowing; // the query string for following menudata
        StringBuffer queryPath;
        String rootPosNoPath = null;    // posnopath of first object (root)
        int rowCount = 0;               // row counter
        int oLevel = 0;                 // level of root object
        int stepLevels = 10;             // levels per step
        OID rootObjOid = this.p_rootObjOid;
        long start = 0;                 // start time of measured duration
        long end = 0;                   // end time of measured duration

        // empty the elements vector:
        this.elements.removeAllElements ();

        // check if a root object was set:
        if (rootObjOid == null || rootObjOid.isEmpty () || rootObjOid.isTemp ())
        {
            // search within the whole tree:
            rootObjOid = this.oid;
        } // if


        // get the elements out of the database:
//IOHelpers.printTrace ("    before getFirst");
        // open db connection -  only workaround - db connection must
        // be handled somewhere else
        action = this.getDBConnection ();

        queryStrFirst = new StringBuffer ()
            .append ("SELECT v.containerId, v.oid, v.name, v.posNoPath,")
                .append (" v.isLink, v.linkedObjectId, v.icon, v.oLevel")
            .append (" FROM ibs_Object v, ibs_RightsCum r ")
            .append (" WHERE v.oid = ").append (rootObjOid.toStringQu ())
                .append (SQLHelpers.getStringCheckRights (Operations.OP_VIEW, "r.rights"))
                .append (" AND v.rKey = r.rKey")
                .append (" AND r.userId = ").append (this.user.id);
//IOHelpers.printTrace ("      query defined");

//debug ("performRetrieveContentData Query: " + queryStrFirst);

        try
        {
            // execute the query:
            rowCount = action.execute (queryStrFirst, false);
//IOHelpers.printTrace ("      query executed");

            // get tuples out of db
            if (rowCount > 0 && !action.getEOF ())
            {
                obj = new ContainerElement (SQLHelpers.getQuOidValue (action, "oid"));
                this.getContainerElementData (action, obj);
                rootPosNoPath = action.getString ("posNoPath");
                oLevel = action.getInt ("oLevel");
                // add element to list of elements
                this.elements.addElement (obj);
            } // if

            // the last tuple has been processed
            // end transaction
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (dbErr, this.app, this.sess, this.env, true);
        } // catch
//IOHelpers.printTrace ("    after getFirst");

        // if first element was not found
        if (this.elements.size () < 1)
        {
            this.releaseDBConnection (action);
            return;
        } // if

//IOHelpers.printTrace ("    before getFollowing");
        switch (stepLevels)
        {
            case 1:
                queryPath = new StringBuffer ()
                    .append (", v_Menu_01$menuLevels1Sub o")
                    .append (" WHERE v.oid = o.oid")
                    .append (" AND ").append (this.oid.toStringBufferQu ())
                    .append (" IN (o.oid1)");
//                    .append (" WHERE v.containerId = ").append (this.oid.toStringQu ());
                break;

            case 2:
                queryPath = new StringBuffer ()
                    .append (", v_Menu_01$menuLevels2Sub o")
                    .append (" WHERE v.oid = o.oid")
                    .append (" AND ").append (this.oid.toStringBufferQu ())
                    .append (" IN (o.oid2, o.oid1)");
/*
                queryPath = new StringBuffer ()
                    .append (", ibs_Object o")
                    .append (" WHERE v.oid = o.oid")
                    .append (" AND (o.containerId = ").append (this.oid.toStringQu ())
                    .append (" OR o.containerOid2 = ").append (this.oid.toStringQu ())
                    .append (")");
*/
                break;

            case 3:
                queryPath = new StringBuffer ()
                    .append (", v_Menu_01$menuLevels3Sub o")
                    .append (" WHERE v.oid = o.oid")
                    .append (" AND ").append (this.oid.toStringBufferQu ())
                    .append (" IN (o.oid3, o.oid2, o.oid1)");
                break;

            case 4:
                queryPath = new StringBuffer ()
                    .append (", v_Menu_01$menuLevels4Sub o")
                    .append (" WHERE v.oid = o.oid")
                    .append (" AND ").append (this.oid.toStringBufferQu ())
                    .append (" IN (o.oid4, o.oid3, o.oid2, o.oid1)");
                break;

            case 5:
                queryPath = new StringBuffer ()
                    .append (", v_Menu_01$menuLevels5Sub o")
                    .append (" WHERE v.oid = o.oid")
                    .append (" AND ").append (this.oid.toStringBufferQu ())
                    .append (" IN (o.oid5, o.oid4, o.oid3, o.oid2, o.oid1)");
                break;

            default:
                queryPath = new StringBuffer ()
                    .append (", v_Menu_01$menuLevelsNSub o")
                    .append (" WHERE v.oid = o.oid")
                    .append (" AND ").append (SQLHelpers.getQueryConditionString (
                        "o.posNoPathN", SQLConstants.MATCH_EXACT, rootPosNoPath, true))
/*
                    .append (" AND ").append (SQLHelpers.getQueryConditionString (
                        "v.posNoPath", SQLConstants.MATCH_STARTSWITH, rootPosNoPath, false))
*/
//                    .append (SQLHelpers.getStringCheckRights (Operations.OP_VIEW))
                    .append (" AND v.oid <> ").append (this.oid.toStringBufferQu ())
                    .append (" AND v.oLevel > ").append (oLevel)
                    .append (" AND v.oLevel <= ").append (oLevel + stepLevels);
                break;
        } // switch levelStep

        queryStrFollowing = new StringBuffer ()
            .append ("SELECT v.containerId, v.oid, v.name, v.rights,")
            .append (" v.isLink, v.linkedObjectId, v.icon, v.oLevel,")
            .append (" o.hasSubNodes")
            .append (" FROM v_Menu_01$content v, ibs_Object c")
            .append (queryPath)
            .append (" AND v.userId = ").append (this.user.id)
            .append (" AND v.containerId = c.oid")
            .append (" AND c.state = ").append (States.ST_ACTIVE)
            .append (" ORDER BY c.posNoPath ASC, v.name ASC");

//debug ("performRetrieveContentData Query: " + queryStrFollowing);
//IOHelpers.printTrace ("      query defined");

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            start = System.currentTimeMillis ();
            rowCount = action.execute (queryStrFollowing, false);
            end = System.currentTimeMillis ();
            this.p_durationDatabaseQuery = end - start;
//IOHelpers.printTrace ("      query executed");

            start = System.currentTimeMillis ();
            // check for empty resultset:
            if (rowCount > 0)
            {
                // get tuples out of db
                while (!action.getEOF ())
                {
                    // check if the user is allowed to view the object:
                    if ((action.getInt ("rights") & Operations.OP_VIEW) ==
                        Operations.OP_VIEW)
                    {
                        obj = new ContainerElement (SQLHelpers.getQuOidValue (action, "oid"));
                        this.getContainerElementData (action, obj);

                        // add element to list of elements
                        this.elements.addElement (obj);
                    } // if

                    // step one tuple ahead for the next loop
                    action.next ();
                } // while
            } // if
            end = System.currentTimeMillis ();
            this.p_durationStructureCreation1 = end - start;

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
//IOHelpers.printTrace ("    after getFollowing");
    } // performRetrieveContentData


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This method is used to get all attributes of one element out of the
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
     *
     * @deprecated  This method should not longer be used. Use
     *              {@link #performRetrieveMenuContentData(MenuRootElement, SQLAction, OID, String, int) }
     *              instead.
     */
    @Deprecated
    protected void getContainerElementData (SQLAction action,
                                            ContainerElement commonObj)
        throws DBError
    {
        // get and set values for element
        commonObj.p_containerOid = SQLHelpers.getQuOidValue (action, "containerId");
        commonObj.p_oLevel = action.getInt ("oLevel");
        commonObj.name = action.getString ("name");
        commonObj.isLink = action.getBoolean ("isLink");
        commonObj.linkedObjectId = SQLHelpers.getQuOidValue (action, "linkedObjectId");
        commonObj.icon = action.getString ("icon");
    } // getContainerElementData



    /**************************************************************************
     * Represent the Menu, i.e. its entries, to the user. <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   orderBy             Property, by which the result is sorted.
     * @param   orderHow            Kind of ordering: BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                              null => BOConstants.ORDER_ASC
     *
     * @deprecated  This method should not longer be used. Use
     *              {@link #showMenu() } instead.
     */
    @Deprecated
    public void performShowContent (int representationForm,
                                    int orderBy, String orderHow)
    {
        Page page = new Page (false);   // the page
        TextElement text = null;        // the text
        long start = 0;                 // start time of measured duration
        long end = 0;                   // end time of measured duration

        start = System.currentTimeMillis ();

        // create the menu root:
        MenuRootElement tree = new MenuRootElement ("oid");
        tree.id = this.name;
        tree.name = this.name;

        // check if there are some elements in the menu:
        if (this.elements.size () > 1) // there are some elements?
        {
            this.trace ("menu size -->" + this.size);
            int counter = 0;
            MenuElement[] tracking = new MenuElement[this.elements.size ()];
            int[] positions = new int[50];

            Iterator<ContainerElement> iter = this.elements.iterator ();
            // the base container is the forst object and shall be ignored:
            ContainerElement e = null;
            if (iter.hasNext ())
            {
                e = iter.next ();
            } // if
            tracking[counter++] = tree;
            while (iter.hasNext ())
            {
                // get the actual element:
                e = iter.next ();

                if (e != null)
                {
                    StringBuffer oidStr;

                    if (e.isLink)
                    {
                        oidStr = e.linkedObjectId.toStringBuffer ();
                    } // if
                    else
                    {
                        oidStr = e.oid.toStringBuffer ();
                    } // else

                    String escName = Helpers.escape (e.name); // the escaped name
                    MenuElement m1 = new MenuElement (escName, oidStr);
                    m1.id = e.oid.toString ();
                    m1.name = escName;
                    m1.icon = e.icon;
                    m1.p_hasSubNodes = true;

                    // tracking of the MenuElements!
                    tracking[counter] = m1;

                    // add to the correct container element:
                    int j = positions[e.p_oLevel - 1]; // element above actual element
                    while ((j >= 0) &&
//                        ((ContainerElement) this.elements.elementAt (j)).p_oLevel == e.p_oLevel - 1 ||
                        !e.p_containerOid
                            .equals (this.elements.elementAt (j).oid))
                    {
                        j--;
                    } // while

                    if (j > -1)
                    {
                        tracking[j].addElement (m1);
                        positions[e.p_oLevel] = counter;
                    } // if

                    counter++;
                } // if
            } // while iter.hasNext ()
//IOHelpers.printTrace ("performShowContent after loop");
        } // if there are some elements
        else                        // there are no elements
        {
            // show the according message to the user:
            text = new TextElement (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_CONTAINEREMPTY, env));
            page.body.addElement (text);
        } // else there are no elements

        tree.maxlevels = tree.getMaxLevel ();
        end = System.currentTimeMillis ();
        this.p_durationStructureCreation2 = end - start;
        tree.p_durationDatabaseQuery = this.p_durationDatabaseQuery;
        tree.p_durationStructureCreation =
            this.p_durationStructureCreation1 + this.p_durationStructureCreation2;
        page.body.addElement (tree);

        try
        {
//IOHelpers.printTrace ("performShowContent before page.build");
            page.build (this.env);
//IOHelpers.printTrace ("performShowContent after page.build");
        } // try

        catch (BuildException e)
        {
            // show the according message to the user:
            IOHelpers.showMessage ("Menu_01.performShowContent", e.getMsg (), this.app, this.sess, this.env);
        } // catch
    } // performShowContent

} // class Menu_01

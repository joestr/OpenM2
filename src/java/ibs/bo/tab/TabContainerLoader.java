/*
 * Class: TabContainerLoader.java
 */
/*
 * Created by IntelliJ IDEA.
 * User: kreimueller
 * Date: Dec 11, 2001
 * Time: 10:28:48 PM
 */

// package:
package ibs.bo.tab;

// imports:
import ibs.io.Environment;
import ibs.ml.MultilangConstants;
import ibs.ml.MultilingualTextInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.service.list.SQLElementContainerLoader;
import ibs.tech.html.BuildException;
import ibs.tech.html.Page;
import ibs.tech.html.ScriptElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;

import java.util.Enumeration;


/******************************************************************************
 * This class is responsible for loading the data for the tab container out
 * of the data store. <BR/>
 * The data is loaded into the tab container which can be retrieved through
 * {@link ibs.bo.cache.ObjectPool#getTypeContainer ()
 * ibs.bo.cache.ObjectPool.getTypeContainer ()}.
 *
 * @version     $Id: TabContainerLoader.java,v 1.13 2010/04/22 11:38:17 btatzmann Exp $
 *
 * @author      kreimueller, 011211
 ******************************************************************************
 */
public class TabContainerLoader
    extends SQLElementContainerLoader<TabContainer, Tab>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TabContainerLoader.java,v 1.13 2010/04/22 11:38:17 btatzmann Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates an TabContainerLoader object. <BR/>
     *
     * @param   container   The container in which to load the information.
     */
    public TabContainerLoader (TabContainer container)
    {
        // call constructor of super class:
        super (container);
    } // TabContainerLoader


    ///////////////////////////////////////////////////////////////////////////
    // methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Create the query to retrieve the data from the database. <BR/>
     *
     * @return  The constructed query.
     */
    protected final String createQuery ()
    {
        StringBuffer queryStr;          // the query string
        StringBuffer fromClause = new StringBuffer ();
        StringBuffer whereClause = new StringBuffer ();

        // create the outer join:
        SQLHelpers.getLeftOuterJoin (
            new StringBuffer ("ibs_ObjectDesc_01"), new StringBuffer ("od"),
                new StringBuffer ("t.multilangKey = od.name"),
                new StringBuffer ("WHERE"), fromClause, whereClause);

        // get all version of all types:
        queryStr = new StringBuffer ()
            .append (" SELECT t.id, t.domainId, t.code, t.kind, t.fct, t.rights,")
            .append (SQLHelpers.getSelectCondition
                ("od." + "objName", SQLConstants.DB_NULL, "t.code", "od.objName"))
            .append (" AS name,")
            .append (" t.class")
            .append (" FROM ibs_Tab t").append (fromClause)
            .append (whereClause);

        // return the computed query string:
        return queryStr.toString ();
    } // createQuery


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This method is used to get all attributes of one element out of the
     * result set. The attribute names which can be used are the ones which
     * are defined within the resultset of {@link #createQuery createQuery}.
     * <BR/>
     * <B>Format:</B>. <BR/>
     * for oid properties:
     *      &lt;variable&gt; = getQuOidValue (action, "&lt;attribute&gt;");. <BR/>
     * for other properties:
     *      &lt;variable&gt; = action.get&lt;type&gt; ("&lt;attribute&gt;");. <BR/>
     *
     * @param   action  The database object used for getting the tuple values.
     *
     * @return  The newly created element filled with the values out of the
     *          actual tuple.
     *
     * @exception   DBError
     *              Error when executing database statement.
     */
    protected final Tab parseElement (SQLAction action)
        throws DBError
    {
        // get the data out of the tuple:
        int id = action.getInt ("id");
        int domainId = action.getInt ("domainId");
        String code = action.getString ("code");
        String name = action.getString ("name");
        int kind = action.getInt ("kind");
        int fct = action.getInt ("fct");
        int rights = action.getInt ("rights");
        String tabClass = action.getString ("class");

        // create a new SystemValue object with the tuple data and return it:
        return new Tab (id, domainId, code, name, kind, fct, rights, tabClass);
    } // parseElement


    /**************************************************************************
     * Represent the content of the Container, i.e. its elements, to the user.
     * <BR/>
     *
     * @param   env     The actual environment object.
     */
    public void show (Environment env)
    {
        Tab elem = null;                // the current element
        Page page = new Page ("List", false); // the output page
        ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
                                                // the JavaScript block to be sent
                                                // to the client

        // define the JavaScript function to add a tab:
        script.addScript (
            "function t (id,n,desc)" +
            "{" +
            "top.scripts.addTab (id,n,desc);" +
            "}");

        // loop through all elements of this container and display them:
        for (Enumeration<Tab> enumElems = this.getElems ().elements ();
            enumElems.hasMoreElements ();)
        {
            // get the tab out of the enumeration:
            elem = enumElems.nextElement ();
            
            // retrieve the multilang tab info:
            String [] mlTabInfo = getMultilangTabInfo (elem, env);
            
            // append the current element to the output:
            script.addScript ("t (" + elem.getIdInt () + ",'" +
                    mlTabInfo [0] + "','" + mlTabInfo [1] + "');");
        } // for

        // tell the client that the tabs are not longer loading:
        script.addScript ("top.scripts.tabsLoaded ();");

        // add the script block to the HTML page:
        page.body.addElement (script);

        // build the page and show it to the user:
        try
        {
            // try to build the page
            page.build (env);
        } // try
        catch (BuildException e)
        {
            // show according message to the user
//            showMessage (e.getMsg ());
        } // catch
    } // show
    
    
    /**************************************************************************
     * Retrieves the multilang info for the given tab object.
     *
     * @param   tab     The tab object.
     * @param   env     The actual environment object.
     * 
     * @return  String[] of size 2 containing the multilang name at index 0 and
     *          the description at index 1 
     */
    private String[] getMultilangTabInfo (Tab tab, Environment env)
    {
        String[] mlTabInfo = new String[] {"", ""};

        // (1) Perform lookup within resource bundle for user's locale:        
        String lookupKey = MultilingualTextProvider.getTabBaseLookupKey (tab);
        
        // retrieve the name with the defined lookup key
        MultilingualTextInfo mlNameInfo = MultilingualTextProvider.getMultilingualTextInfo (
                MultilangConstants.RESOURCE_BUNDLE_TABS_NAME,
                MultilingualTextProvider.getNameLookupKey (lookupKey),
                MultilingualTextProvider.getUserLocale (env),
                env);
        
        // retrieve the description with the defined lookup key        
        MultilingualTextInfo mlDescriptionInfo = MultilingualTextProvider.getMultilingualTextInfo (
                MultilangConstants.RESOURCE_BUNDLE_TABS_NAME,
                MultilingualTextProvider.getDescriptionLookupKey (lookupKey),
                MultilingualTextProvider.getUserLocale (env),
                env);
        
        // check if something has been found
        mlTabInfo [0] = mlNameInfo.isFound () ?
                // and use it
                mlNameInfo.getMLValue () :
                // (2) fallback - use the tab name
                tab.getName ();                

        // check if something has been found
                mlTabInfo [1] = mlDescriptionInfo.isFound () ?
                // and use it
                mlDescriptionInfo.getMLValue () :
                // (2) fallback - return ""
                "";
        
        return mlTabInfo;
    } // getMultilangTabInfo

} // class TabContainerLoader

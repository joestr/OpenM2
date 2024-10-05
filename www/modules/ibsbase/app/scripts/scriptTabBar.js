/******************************************************************************
 * JavaScript-File. <BR>
 *
 * @version     $Id: scriptTabBar.js,v 1.22 2010/04/21 08:54:21 btatzmann Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */

//============= declared classes and functions ================================
// class Tab
// class TabBar

// function createTabBar
// function clearTabBar
// function dropTabBar
// function addTab
// function tabPressed
// function setNoActiveTab
// function setNoActiveTabCleanButtonBar
// function setActTabBar
// function getAvailableTabs
// function tabsLoaded


//============= necessary classes and variables ===============================
// var loaderHandler
//
// class Tuple
// class TupleList
// class LoadableList


//============= declarations ==================================================

/**
 * The current tab bar. <BR>
 */
var actTabBar = null;

/**
 * All available tabs. <BR>
 */
var p_availTabs = new TabBar (0, "name", top.sheettabs, "");


//============= class Tab =====================================================

/**
 * void Tab_show (String|int tabBarId, DocumentObject doc, String|int activeTab)
 * Display a tab.
 * activeTab is the id of the active tab. If the actual tab is the active
 * tab it is displayed in a special way (not clickable).
 */
function Tab_show (tabBarId, doc, activeTab)
{
    if (this.id == activeTab)
    {
        doc.write (this.getActiveCode ());
    } // if
    else
    {
        doc.write (this.getInactiveCode ());
    } // else
} // Tab_show


/**
 * Tab Tab (int id, String name, String description) extends Tuple
 * Create tab.
 */
function Tab (id, name, description)
{
    // call super constructor(s):
    Tuple.apply (this, arguments);

    // set property values:
    this.name = name;                   // name
    this.description = description;     // description of the tab
} // Tab

// create class form constructor:
createClass (Tab, Tuple,
{
    // define properties and assign initial values:
    name: null,                         // name
    fct: 251,                           // function of the tab (FCT_SHOWTAB)
    description: null,                  // description of the button

    // define methods:
    show: Tab_show,                     // display the tab
    getActiveCode: Tab_getActiveCode,   // get HTML code of active tab
    getInactiveCode: Tab_getInactiveCode // get HTML code of inact. tab
}); // createClass


//============= class TabBar ==================================================

/**
 * void TabBar_clear ()
 * Clear a tab bar.
 */
function TabBar_clear ()
{
    var doc = this.frame.document;
    doc.open ();
    doc.clear ();
/*
    doc.write ("<HTML><BODY BGCOLOR=\"FFFFFF\"></BODY></HTML>");
*/
    doc.close ();
} // TabBar_clear


/**
 * void TabBar_setActiveTab (int id[, boolean showButtonsLoading])
 * Set active tab of tab bar.
 */
function TabBar_setActiveTab (id, showButtonsLoading)
{
    // set the active tab:
    this.activeTab = id;

    // display the tab bar:
    this.show (showButtonsLoading);
} // TabBar_setActiveTab


/**
 * void TabBar_setStyleSheet (String styleSheet)
 * Set style sheet.
 */
function TabBar_setStyleSheet (styleSheet)
{
    this.styleSheet = styleSheet;
} // TabBar_setStyleSheet


/**
 * void TabBar_setLoaded ()
 * Tell the tab bar that the available tabs are loaded.
 */
function TabBar_setLoaded ()
{
    // add the several elements to the element list:
    // loop through all elements within element id list and add them
    // in the same order to the element list.
    this.setElements (p_availTabs, this.idList);

    // make this tab bar the actual one and display it:
    this.setActual ();
} // TabBar_setLoaded


/**
 * void TabBar_setActual ()
 * Set this tab bar as actual one.
 */
function TabBar_setActual ()
{
    // make this tab bar the actual one and display it:
    setActTabBar (this);
} // TabBar_setActual


/**
 * void TabBar_getAvailable ()
 * Tell the tab bar to get the available elements.
 */
function TabBar_getAvailable ()
{
    // display the buttons as loading if necessary:
    if (this.showButtonsLoading)
    {
        top.scripts.showButtonsLoading ();
        this.showButtonsLoading = false;
    } // if

    // get all available tabs:
    getAvailableTabs (this);
} // TabBar_getAvailable


/**
 * void TabBar_show ([boolean showButtonsLoading])
 * Display a tab bar.
 * If showButtonsLoading is set to true then the loading state is displayed
 * within the button bar.
 */
function TabBar_show (showButtonsLoading)
{
    if (this.length > 0)                // at least one tab exists?
    {
        if (showButtonsLoading || this.showButtonsLoading)
        {
            top.scripts.showButtonsLoading ();
            this.showButtonsLoading = false;
        } // if

        this.performShow ();
    } // if at least one tab exists
    else                                // no tab exists
    {
        // display the empty tabs page:
        clearTabBar ();
    } // else no tab exists
} // TabBar_show


/**
 * void TabBar_showElements ()
 * Display the elements of the tab bar.
 */
function TabBar_showElements ()
{
    var tab = this.first ();            // the actual tab

    // show the tab just if there is more than one tab OR
    // the showSingleTab variable is set to true for this layout
    if (top.scripts.showSingleTab || this.length > 1)
    {
        // loop through all tabs and display each of them:
        while (tab != null && tab != this.notFound)
        {
            // display the element:
            tab.show (this.id, this.frame.document, this.activeTab);
            // get the next element:
            tab = this.next ();
        } // while
    } // if (showSingleTab || this.length > 1)
} // TabBar_showElements


/**
 * TabBar TabBar (String id, String name, FrameObject frame) extends TupleList
 * Create tab bar.
 */
function TabBar (id, name, frame)
{
    // call super constructor(s):
    TupleList.call (this, id, frame, false);

    // set property values:
    this.name = name;                   // name

    // perform initial statements:
} // TabBar

// create class form constructor:
createClass (TabBar, [TupleList, LoadableList],
{
    // define properties and assign initial values:
    name: null,                         // name
    activeTab: -1,                      // the active tab
    styleSheet: "styleTabBar.css",      // actual style sheet
    showButtonsLoading: false,          // display loading state in button bar?

    // define methods:
    show: TabBar_show,                  // display the tab bar
    performShow: TabBar_performShow,    // display the whole tab bar
    clear: TabBar_clear,                // clear the tab bar
    setActiveTab: TabBar_setActiveTab,  // set active tab
    setLoaded: TabBar_setLoaded,        // tell the tab bar that the
                                        // available tabs are loaded
    setActual: TabBar_setActual,        // set this tab bar as actual one
    getAvailable: TabBar_getAvailable,  // get all available elements
    showElements: TabBar_showElements   // display the elements of the tab bar
}); // createClass


//============= common functions ==============================================

/**
 * void createTabBar (OIDString oid, int activeTab, boolean showButtonsLoading,
 * int tabId1, int tabId2, int tabId3, ...)
 * Create a new tab bar and display it.
 */
function createTabBar (oid, activeTab, showButtonsLoading)
{
    var tabIdList = $A2 (arguments, 3); // list of ids of required tabs
    var tabBar = null;                  // the new tab bar


    // save current tab id in top
    top.tab = activeTab;

    // create the tab bar:
    tabBar = new TabBar (oid, 'name', top.sheettabs);

    // activate the active tab:
    if (activeTab)                      // a tab shall be activated?
    {
        tabBar.activeTab = activeTab;
    } // if a tab shall be activated
    tabBar.showButtonsLoading = showButtonsLoading;

    // add the tabs to the tab bar:
    tabBar.setAndLoadElements (p_availTabs, tabIdList);
} // createTabBar


/**
 * void clearTabBar ()
 * Clear the actual tab bar.
 */
function clearTabBar ()
{
    // ensure that there is no tab bar defined:
    dropTabBar ();

    // clear the tab bar frame:
    callUrl (top.system.layoutDir + 'sheettabsempty.htm', null, null, 'sheettabs');
} // clearTabBar


/**
 * void dropTabBar ()
 * Drop the actual tab bar.
 */
function dropTabBar ()
{
    // ensure that there is no tab bar defined:
    if (actTabBar != null)              // tab bar exists?
    {
        actTabBar = null;
    } // if tab bar exists
} // dropTabBar


/**
 * void addTab (String|int id, String name, String description)
 * Add a tab to the available tabs. <BR>
 */
function addTab (id, name, description)
{
    p_availTabs.add (new Tab (id, name, description));
} // addTab


/**
 * void tabPressed (String|int id)
 * A tab was pressed. <BR>
 */
function tabPressed (id)
{
    var offlineUrl = null;              // url for offline mode

    // handling for offline mode:
    for (var i = 0; i < top.tabIdUrls.length; i++)
    {
        if (top.tabIdUrls[i++] == id)
        {
            offlineUrl = actTabBar.id + "_" + top.tabIdUrls[i];
        } // if
    } // for

	// IBS-90: Check if the object is a search (check method with OID is a HACK!!!).
	// is search
	if(actTabBar.id.indexOf("0x01017F51") != -1)
	{
		// open the query within the search frame
	    callUrl (251, '&oid=' + actTabBar.id + '&tab=' + id + '&tabs=0', offlineUrl, 'searchFrame');
    } // if
    else
    {
		// open the query within the sheet frame
    	callUrl (251, '&oid=' + actTabBar.id + '&tab=' + id + '&tabs=0', offlineUrl, 'sheet');
    } // else
    
    actTabBar.setActiveTab (id);
} // tabPressed


/**
 * void setNoActiveTab ()
 * Ensure that no tab is active.
 */
function setNoActiveTab ()
{
    if (actTabBar != null)
    {
        actTabBar.setActiveTab (-1, true);
    } // if
} // setNoActiveTab


/**
 * void setNoActiveTabCleanButtonBar ()
 * Ensure that no tab is active and clean the button bar.
 */
function setNoActiveTabCleanButtonBar ()
{
    setNoActiveTab ();
    top.cleanButtonBar ();
} // setNoActiveTabCleanButtonBar


/**
 * void setActTabBar (TabBar tabBar)
 * Set a new actual tab bar and display it.
 */
function setActTabBar (tabBar)
{
    // set the new tab bar:
    actTabBar = tabBar;

    // display the actual tab bar:
    actTabBar.show (false, actTabBar.showButtonsLoading);
} // setActTabBar


/**
 * void getAvailableTabs (TabBar tabBar)
 * Get info of all tabs from the server. This method just calls the
 * corresponding url at the server.
 */
function getAvailableTabs (tabBar)
{
    // get all available tabs:
    loaderHandler.load ("availTabs", tabBar, 252, "", "tabs", "sheettabs", "tabs", 5);
} // getAvailableTabs


/**
 * void tabsLoaded ()
 * Tell the client application that all available tabs have been loaded to the
 * client.
 */
function tabsLoaded ()
{
    loaderHandler.finishLoading ("availTabs");
} // tabsLoaded


/**
 * void resetTabs ()
 */
function resetTabs ()
{
    // reset all available tabs:
    p_availTabs.empty ();
} // resetTabs
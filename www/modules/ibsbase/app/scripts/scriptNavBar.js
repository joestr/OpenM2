/******************************************************************************
 * This file contains all JavaScript classes and their methods which are used
 * to create and manage lists. <BR>
 * 
 * @version     $Id: scriptNavBar.js,v 1.9 2010/04/28 10:04:05 btatzmann Exp $
 *
 * @author      Christine Keim (CK)  000214
 ******************************************************************************
 */

//============= declared classes and functions ================================
// class NavBarTab
// class NavBar
// class NavItem

// function showNavItem
// function navItemPressed
// function setNavTab
// function setNavTabNoSheet


//============= necessary classes and variables ===============================
// var loaderHandler
// class Tuple
// class TupleList
// function loadMenuBar ()


//============= declarations ==================================================

// actual navigation bar:
var actNavBar = new NavBar (1, top.navigationtabs, top.navigation);

// a callback which gets executed at the end of NavBar_loadingFinished
// if defined
var navBarCallbackFct = null;

var nav_gotoContainer = 32;
var nav_back = 33;

var p_navItems = new Array ();
var p_navCount = 0;

function n (id,n,i,d,u)
{
    p_navItems[p_navCount++] = new NavItem (id,n,i,d,u);
} // n

n (nav_back, top.multilang.ibs_ibsbase_scripts_ML_backName, "navBack.gif", top.multilang.ibs_ibsbase_scripts_ML_backDesc, "top.goback(1);");
n (nav_gotoContainer, top.multilang.ibs_ibsbase_scripts_ML_gotoContainerName, "navGotoC.gif", top.multilang.ibs_ibsbase_scripts_ML_gotoContainerDesc, "top.content();");


//============= class NavBarTab ===============================================

/**
 * void NavBarTab_show (String|int navBarId, DocumentObject doc)
 * Display a tab of a navigation bar.
 */
function NavBarTab_show (navBarId, doc)
{
    // display the menu bar of the tab (used for prefetching):
    if (this.menuBar != null)       // menu bar already loaded?
    {
        this.menuBar.navBarTab = this;
        this.menuBar.showPage ();   // display the menu bar
    } // if menu bar already loaded

    // when this tab is set to active and shown, the tab is seen as the
    // actual object:
    if (this.isActive)                  // the tab is active?
    {
        doc.write (this.getActiveCode ());
        top.oid = this.tabOid;
        top.majorOid = top.oid;
        top.isPhysical = true;
        top.containerId = 'null';

        // display the menu bar of the tab:
        if (this.menuBar != null)       // menu bar already loaded?
        {
            actMenuBar = this.menuBar;  // set actual menu bar^
            this.menuBar.show ();       // display the menu bar
        } // if menu bar already loaded
        else                            // menu bar not loaded
        {
            // create a new menu bar:
            this.menuBar = new MenuBar (this.id, this.name,
                this.tabOid, this.menuFrame[this.menuFrameName]);

            // ensure that the menu bar is displayed immediately when loaded 
            // from the server:
            this.displayMenuBar = true;
            // trigger the process which is loading the menu bar from the
            // server:
            loadMenuBar (this.menuBar);
        } // else menu bar not loaded

        // display the html file for this menu bar (if there exists one) and
        // clear the button bar and the tab bar frame:
        if (this.htmlfile && this.htmlfile != null && this.htmlfile != "" &&
            this.overwriteSheet)
                                        // html file was given and sheet frame 
                                        // may be overwritten?
        {
            // display the html file:
            callUrl (top.system.includeDir + this.htmlfile, null, null, 'sheet', null, true);
            // clear the tabs and buttons:
            clearTabBar ();
            clearButtonBar ();
        } // if html file was given and sheet frame may be overwritten
        else if (!this.overwriteSheet)  // sheet frame may not be overwritten?
        {
            // overwrite the sheet frame the next time:
            this.overwriteSheet = true;
        } // else if sheet frame may not be overwritten
    } // if the tab is active
    else                                // the tab is inactive
    {
        doc.write (this.getInactiveCode ());
    } // else the tab is inactive
} // NavBarTab_show


/**
 * void NavBarTab_print ()
 * Show definition of tab of navigation bar.
 */
function NavBarTab_print ()
{
    alert (this.getCode ());            // get definition and show it
} // NavBarTab_print


/**
 * void NavBarTab_activate ()
 * Activate the tab.
 */
function NavBarTab_activate ()
{
    this.isActive = true;               // the tab is now active
    this.classId = this.activeClassId;  // set the correct display class

    if (this.menuBar == null)           // menu bar not loaded?
    {
        // create a new menu bar:
        this.menuBar = new MenuBar (this.id, this.name,
            this.tabOid, this.menuFrame[this.menuFrameName]);

        // ensure that the menu bar is not displayed after being loaded 
        // from the server:
        this.displayMenuBar = false;
        // trigger the process which is loading the menu bar from the server:
        loadMenuBar (this.menuBar);
    } // if menu bar not loaded

    // show the correct frame:
    top.scripts.showSingleFrame (this.menuFrame, this.menuFrameName, 0);
} // NavBarTab_activate


/**
 * void NavBarTab_deactivate ()
 * Deactivate the tab.
 */
function NavBarTab_deactivate ()
{
    this.isActive = false;              // the tab is not longer active
    this.classId = this.inactiveClassId; // set the correct display class
} // NavBarTab_deactivate


/**
 * void NavBarTab_setMenuBar (MenuBar menuBar)
 * Set a new menu bar for this tab.
 */
function NavBarTab_setMenuBar (menuBar)
{
    // if there was an old menu bar stored within the tab synchronize the 
    // new menu bar with the old one:
    if (this.menuBar != null)           // menu bar exists?
    {
        this.menuBar.synchronize (menuBar); // perform synchronization
    } // if menu bar exists
    else
    {
        // set the new menu bar:
        this.menuBar = menuBar;
    } // else

    // display the menu bar if necessary:
    if (this.displayMenuBar)            // shall the menu bar immediately be
                                        // displayed?
    {
        actMenuBar = this.menuBar;      // set actual menu bar
        this.menuBar.show ();           // display the menu bar
        this.displayMenuBar = false;    // don't display the menu bar next time
    } // if shall the menu bar immediately be displayed
} // NavBarTab_setMenuBar


/**
 * void NavBarTab_setProperties (int|String id, in tabId, String name, 
 *            int width, int height, 
 *            String activeClassId, String inactiveClassId, String description, 
 *            String htmlfile)
 * Set the values of the properties of the navigation tab.
 * For each property is first checked if the value provided exists (!= null).
 * Otherwise the value stays unchanged.
 */
function NavBarTab_setProperties (id, tabId, name, width, height, 
                    activeClassId, inactiveClassId, description, htmlfile)
{
    // the id of the tab:
    if (tabId != null)
        this.tabId = tabId;
    // the tab's name:
    if (name != null)
        this.name = name;
    // the dimensions of the tab:
    if (width != null && height != null)
    {
        this.width = width;
        this.height = height;
    } // if
    // layout classes:
    if (activeClassId != null && inactiveClassId != null)
    {
        this.activeClassId = activeClassId;
        this.inactiveClassId = inactiveClassId;
        this.classId = inactiveClassId;
    } // if
    // description of the tab:
    if (description != null)
        this.description = description;
    // the assigned html file:
    if (htmlfile != null)
        this.htmlfile = htmlfile;
} // NavBarTab_setProperties


/**
 * NavBarTab NavBarTab (int|String id, FrameObject menuFrame, int tabId,
 *            String name, int width, int height, 
 *            String activeClassId, String inactiveClassId, String description, 
 *            String htmlfile, OID tabOid) extends Tuple
 * Constructor of class NavBarTab.
 */
function NavBarTab (id, menuFrame, tabId, name, width, height,
                    activeClassId, inactiveClassId, description,
                    htmlfile, tabOid)
{
    // call super constructor(s):
    Tuple.call (this, id, name);

    // set property values:
    this.tabId = tabId;                 // the alternative tab id
    this.menuFrame = menuFrame;         // the frame in which to display the
                                        // menu content
    this.name = name;                   // name
    this.width = width;                 // width of tab
    this.height = height;               // height of tab
    this.activeClassId = activeClassId; // the class id if the tab is active
    this.inactiveClassId = inactiveClassId; // the class id if the tab is
                                        // inactive
    this.description = description;     // the description of the tab
    this.htmlfile = htmlfile;           // the htmlfile to be displayed within
                                        // sheet when the tab is activated
    this.tabOid = tabOid;               // oid of object which belongs to the
                                        // tab
    this.classId = inactiveClassId;     // stylesheet class id of tab
    this.menuFrameName = "navigation_" + this.tabId;
                                        // name of frame for menu bar

    // perform initializations:
} // NavBarTab

// create class form constructor:
createClass (NavBarTab, Tuple,
{
    // define properties and assign initial values:
    tabId: 0,                           // the alternative tab id
    menuFrame: null,                    // the frame in which to display the
                                        // menu content
    name: null,                         // name
    width: 0,                           // width of tab
    height: 0,                          // height of tab
    activeClassId: null,                // the class id if the tab is active
    inactiveClassId: null,              // the class id if the tab is
                                        // inactive
    description: null,                  // the description of the tab
    htmlfile: null,                     // the htmlfile to be displayed within
                                        // sheet when the tab is activated
    tabOid: null,                       // oid of object which belongs to the
                                        // tab
    classId: null,                      // stylesheet class id of tab
    isActive: false,                    // tells whether the tab is active
                                        // or not
    menuBar: null,                      // the menu bar belonging to this 
                                        // navigation bar tab
    menuFrameName: null,
                                        // name of frame for menu bar
    displayMenuBar: false,              // shall the menu bar immediately be
                                        // displayed when loaded from server?
    overwriteSheet: true,               // may the sheet frame be overwritten?

    // define methods:
    getActiveCode: NavBarTab_getActiveCode,
                                        // get HTML code of active tab
    getInactiveCode: NavBarTab_getInactiveCode,
                                        // get HTML code of inactive tab
    show: NavBarTab_show,               // display the tab
    activate: NavBarTab_activate,       // activate the tab
    deactivate: NavBarTab_deactivate,   // deactivate the tab
    setMenuBar: NavBarTab_setMenuBar,   // set a new menu bar
    setProperties: NavBarTab_setProperties // set the properties of the tab
}); // createClass


//============= class NavBar ==================================================

/**
 * void NavBar_activateTab (int|String id)
 * Set a specific tab identified by its id as active
 * (and all others as inactive).
 */
function NavBar_activateTab (id)
{
    var tab = this.first ();            // the actual tab

    // check if it is allowed to place a call in the sheet frame:
    if (checkCallAllowed ("sheet"))     // call allowed?
    {
        // deactivate the currently active tab:
        if (this.activeTab != null && this.activeTab != this.notFound)
                                        // active tab set?
        {
            // deactivate the tab:
            this.activeTab.deactivate ();
        } // if active tab set

/*
        // deactivate all tabs:
        // loop through all tabs and deactivate them.
        while (tab != null && tab != this.notFound)
        {
            tab.deactivate ();          // deactivate the current tab
            tab = this.next ();         // go to the next one
        } // while
*/

        // activate the specific tab:
        tab = this.get (id);            // search for the tab
        if (tab != null && tab != this.notFound) // tab found?
        {
/*
            top.scripts.setFrameHeight (this.menuFrame.frames[0], null,
                "*", , false)

            var frameset =
                this.menuFrame.document.getElementById ("menuFrameset");
            var rows = "";
            var sep = "";

            for (var i = 0; i < this.length; i++)
            {
                rows += sep;
                if (this.menuFrame.frames[i] == tab.menubar.frame)
                {
                    rows += "*";
                } // if
                else
                {
                    rows += "0";
                } // else
                sep = ",";
            } // for i

            frameset.rows = rows;
*/

            // activate the tab:
            tab.overwriteSheet = this.overwriteSheet; // set same state for tab
            tab.activate ();            // activate the tab
            this.activeTab = tab;       // remember the active tab
            this.overwriteSheet = true; // next time the sheet can be written
        } // if

        // (re-)display the navigation bar:
        this.show ();
    } // if call allowed
} // NavBar_activateTab


/**
 * NavBarTab NavBar_setTab (int String id, int tabId, String name, 
 *            int width, int height, 
 *            String activeClassId, String inactiveClassId, String description, 
 *            String htmlfile, OID tabOid)
 * Set a tab of this navigation bar.
 * This function first checks if a tab with this id already exists. If this
 * is the case the tab is used. Otherwise a new tab is created using the
 * given data.
 */
function NavBar_setTab (id, tabId, name, width, height, 
                    activeClassId, inactiveClassId, description, htmlfile,
                    tabOid)
{
    var tab = this.get (id);            // try to find the tab

    if (tab == null || tab == this.notFound) // tab not found?
    {
        // create a new tab:
        tab = new NavBarTab (id, this.menuFrame, tabId, name, width, height, 
            activeClassId, inactiveClassId, description, htmlfile, tabOid);
        // add the new tab to this navigation bar:
        this.add (tab);
    } // if tab not found

    // return the actual navigation bar tab:
    return (tab);
} // NavBar_setTab


/**
 * void NavBar_setTabMenuBar (int|String id, MenuBar menuBar)
 * Set the menu bar of a navigation bar tab.
 * If the tab does not exist nothing ist done.
 */
function NavBar_setTabMenuBar (id, menuBar)
{
    var tab = this.get (id);            // try to find the tab

    // tell the loader that the menu bar was loaded:
    loaderHandler.finishLoading ("menuBar_" + id);

    if (tab != null && tab != this.notFound) // tab found?
    {
        // set the new menu bar:
        tab.setMenuBar (menuBar);
    } // if tab found
} // NavBar_setTabMenuBar


/**
 * void NavBar_loadingFinished ()
 * Loading of menu bar was finished.
 */
function NavBar_loadingFinished ()
{
    // check if the frames for the menu bars were alread created:
    if (this.menuFrame.frames.length == 0 && this.length > 0)
                                        // no frames created?
    {
        // create the frames:
        var doc = this.menuFrame.document;
        var url = doc.location.href;
        var framesetRows = "*";

        for (var i = 0; i < this.length; i++)
        {
            framesetRows += ",0";
        } // for i

        doc.open ();
        doc.write (
            '<FRAMESET ID="menuFrameset" ROWS="' + framesetRows + '"' +
            ' FRAMEBORDER="0" BORDER="0" FRAMESPACING="0"' +
            ' BORDERCOLOR="BLACK">' +
            '<FRAME SRC="' + url + '"' +
            ' NAME="' + this.menuFrame.name + '_intro"' +
            ' FRAMEBORDER="0" MARGINWIDTH="0" MARGINHEIGHT="0"' +
            ' SCROLLING="AUTO">');

        for (var i = 0; i < this.length; i++)
        {
            doc.write (
                '<FRAME SRC="empty.htm" NAME="' + this[i].menuFrameName + '"' +
                ' FRAMEBORDER="0" MARGINWIDTH="0" MARGINHEIGHT="0"' +
                ' SCROLLING="AUTO">');
        } // for i

        doc.write (
            '</FRAMESET>');
        doc.close ();

        // initialize menu bars within navigation tabs:
        for (var i = 0; i < this.length; i++)
        {
            // check if the menuBar was already set:
            if (this[i].menuBar == null)
            {
                // create the menu bar and store it within the navigation tab:
                this[i].setMenuBar (new MenuBar (this[i].id, this[i].name,
                    this[i].tabOid, this.menuFrame[this[i].menuFrameName]));
            } // if
        } // for i
    } // if no frames created
	
	// check if a callback function has been set
	if (navBarCallbackFct != null)
	{
		// execute the callback function
		setTimeout (navBarCallbackFct, 0);
		navBarCallbackFct = null;
	} // if
} // NavBar_loadingFinished


/**
 * NavBar NavBar (String id, FrameObject frame, FrameObject menuFrame)
 *      extends TupleList
 * Constructor of class NavBar.
 */
function NavBar (id, frame, menuFrame)
{
    // call super constructor(s):
    TupleList.call (this, id, frame, false);

    // set property values:

    // define properties and assign initial values:
    this.menuFrame = menuFrame;         // frame where the menu content shall
                                        // be displayed
} // NavBar

// create class form constructor:
createClass (NavBar, TupleList,
{
    // define properties and assign initial values:
    menuFrame: null,                    // frame where the menu content shall
                                        // be displayed
    activeTab: null,                    // the currently active tab
    overwriteSheet: true,               // may the sheet frame be overwritten?

    // define methods:
    show: NavBar_show,                  // display the navigation bar
    activateTab: NavBar_activateTab,    // activate a specific tab 
                                        // (and deactivate the others)
    setTab: NavBar_setTab,              // set a tab of this bar
    setTabMenuBar: NavBar_setTabMenuBar, // set menu bar of tab
    loadingFinished: NavBar_loadingFinished // loading of navigation bar
                                        // was finished
}); // createClass


//============= class NavItem =================================================

/**
 * NavItem NavItem (int id, String name, String imageActive, 
 *                  String description, String url) extends Tuple
 * Create a navigation Item.
 */
function NavItem (id, name, imageActive, description, url)
{
    // call super constructor(s):
    Tuple.apply (this, arguments);

    // set property values:
    if (id == nav_back)
    {
        this.constructor.prototype.show = NavItem_showHistory;
                                        // display the history item
    } // if

    // assign initial values:
    this.name = name;                   // name
    this.imageActive = imageActive;     // image shown for active button
    this.description = description;     // description of the button
    this.url = url;                     // url of the button
} // NavItem

// create class form constructor:
createClass (NavItem, Tuple,
{
    // define properties and assign initial values:
    name: null,                         // name
    imageActive: null,                  // image shown for active button
    description: null,                  // description of the button
    url: null,                          // url of the button

    // assign methods:
    show: NavItem_show                  // display the button
}); // createClass


//============= common functions ==============================================

/**
 * void showNavItem (OidString oid, String|int id, DocumentObject doc)
 * Show one single navigation-Item at the actual position within the page.
 * This function is used to display the history and the gotoContainer in the
 * Header.
 */
function showNavItem (oid, id, doc)
{
    var i = 0;
    var count = p_navItems.length;

    // search for the actual navItem within the available navItems:
    for (i = 0; i < count && p_navItems[i].id != id; i++);

    if (i < count)                     // the navItem was found?
    {
        p_navItems[i].show (oid, doc);
    } // if the navItem was found
} // showNavItem


/**
 * void navItemPressed (String|int id, OidString oid)
 * A nav item was pressed. <BR>
 */
function navItemPressed (id, oid)
{
    var i = 0;
    var count = p_navItems.length;

    // search for the actual navItem within the available navItems:
    for (i = 0; i < count && p_navItems[i].id != id; i++);

    if (i < count)                     // the navItem was found?
    {
        // perform the navItem function:
        top.scripts.callOidFunction (oid, p_navItems[i].url);
    } // if the navItem was found
} // navItemPressed


/**
 * void setNavTab (int|String id)
 * Set a new active navigation tab.
 */
function setNavTab (id)
{
	// hide the possibly displayed search form
	hideSearchFrame(false);

    // call the actual navigation bar and activate the tab identified by
    // its id:
    actNavBar.activateTab (id);
} // setNavTab


/**
 * void setNavTabNoSheet (int|String id)
 * Set a new active navigation tab and don't overwrite the actual sheet.
 */
function setNavTabNoSheet (id)
{
    // ensure that the sheet is not overwritten:
    actNavBar.overwriteSheet = false;
    // call common method for setting a new active navigation tab:
    setNavTab (id);
} // setNavTabNoSheet

/**
 * void setHTMLFileAsWelcomePage ()
 * Set module intro page as welcome page
 */
function setHTMLFileAsWelcomePage ()
{
    // check if a tab is selected
    if (actNavBar.activeTab != null)            // online?
    {
        var offlineDir = top.system.getOfflineDir ();
        var url = top.system.includeDir;
        
        // set current welcome page to activeTab intro page
        if (top.system.isOnline)            // online?
        {
            top.urlWelcome   = url + actNavBar.activeTab.htmlfile;
        } // if
        else                                // offline
        {
            top.urlWelcome   = offlineDir + actNavBar.activeTab.htmlfile;
        } // else
    } // if
} // setHTMLFileAsWelcomePage

/**
 * void reloadNavBar ()
 */
function reloadNavBar ()
{
    // reset the nav bar
    actNavBar.empty ();
    
    // and reload it
    top.tempnavigation.location.href = top.urlNomenu;
    
	// disable overwriteSheet to assure that the current content
	// of the sheet frame is not overwritten when the callback function
	// executes 'activateTab'
	actNavBar.overwriteSheet = false;
	
	// to reload the menu bar a callback function is defined,
	// which activates the current tab 
	navBarCallbackFct = "actNavBar.activateTab ('" + actNavBar.activeTab.id + "')";
} // reloadNavBar
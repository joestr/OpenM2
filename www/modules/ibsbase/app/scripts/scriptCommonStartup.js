/******************************************************************************
 * This files contains all common JavaScript code which is executed at top
 * level when the application ist started. <BR>
 *
 * @version     $Id: scriptCommonStartup.js,v 1.47 2010/04/28 10:04:05 btatzmann Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */

//============= declared classes and functions ================================
// function framesLoaded
// function loadMajorScripts
// function loadScript
// function addScript
// function showLoadState
// function setScriptsLoaded
// function tryCall
// function tryShowObject
// function trySetNoActiveTab
// function getSearchString
// function setTitle
// function getFrameName
// function debug
// function iter
// function unload
// function setUrls


//============= necessary classes and variables ===============================


//============= declarations ==================================================

// constants:
var MODE_VIEW = 1;                      // view mode
var MODE_EDIT = 2;                      // edit mode

// variables:
var isOnline = true;
var containerId = '0x0000000000000000';
var majorOid = '0x0000000000000000';
var oid = '0x0000000000000000';
var isPhysical = false;
var curViewDate = '';
var otherContentId = '0x0000000000000000';
var isOtherContent = false;
var dbg = "";                           // debug text

var scriptSum = 100;
var scriptCount = 0;
var scriptsLoaded = false;
var pendingCalls = new Array ();
var actCall = 0;
var startUrl = top.document.referrer;
var simpleSearch = false;
var mode = MODE_VIEW;                   // the actual mode

var multilangTextsLoaded = false;

var v_durations =
{
    nextId: 0,
    elems: new Array (),

    set: function (text, duration)
    {
        var id = this.nextId++;
        var elem =
        {
            id: id,
            text: text,
            duration: duration,
            start: null
        };
        this.elems[id] = elem;
        return id;
    },

    start: function (text)
    {
        var id = this.nextId++;
        var elem =
        {
            id: id,
            text: text,
            duration: 0,
            start: new Date ()
        };
        this.elems[id] = elem;
        return id;
    },

    restart: function (id)
    {
        var elem = this.elems[id];
        elem.start = new Date ();
    },

    end: function (id)
    {
        var elem = this.elems[id];
        var end = new Date ();
        elem.duration = end - elem.start;
    },

    toString: function ()
    {
        var str = "durations:";
        var elem = null;
        var dur = null;
        var text = null;
        var length = 0;
        var c_SPACES = "                    ";
        var c_TABS = "\t\t\t\t\t\t\t\t\t\t";

        // find longest text:
        var maxLength = 0;
        for (var i = 0; i < this.elems.length; i++)
        {
            maxLength = Math.max (maxLength, this.elems[i].text.length);
        } // for i
        length = maxLength / 8 + 1;

        for (var i = 0; i < this.elems.length; i++)
        {
            elem = this.elems[i];
            dur = "" + elem.duration;
            dur = c_SPACES.substring (0, 2 * (6 - dur.length)) + dur;
            text = elem.text + ": ";
            text += c_TABS.substring (0, length - ((text.length - 2) / 8));

            str += "\n" + text + dur + " ms";
        } // for i
        return str;
    }
};

// urls for several purposes:
var urlWelcome   = "";
var urlNomenu    = "";
var urlGroup     = "";
var urlPrivate   = "";
var urlSearch    = "";
var urlSearchAdv = "";
var urlMenu      = "";
var urlMultilangInfo      = "";

// redirected top methods:
var buttonPressed = null;
var callUrl = null;
var cleanButtonBar = null;
var content = null;
var createButtonBar = null;
var createLinkTabReference = null;
var createTabBar = null;
var dropButtonBar = null;
var reloadButtons = null;
var reloadNavBar = null;
var dropTabBar = null;
var resetTabs = null;
var goback = null;
var reloadMultilang = null;
var setMultilangLoaded = null;
var isMultilangLoaded = null;
var load = null;
var loadCont = null;
var loadEvent = null;
var loadEventArg = null;
var loadFile = null;
var loadFileInNewWindow = null;
var loadPreview = null;
var saveFile = null;
var loadLink = null;
var loadForReference = null;
var loadInWindow = null;
var loadMaster = null;
var loadOrderWindow = null;
var loadOrderWindowInList = null;
var loadTabEvent = null;
var loadWeblink = null;
var loadWindowFile = null;
var loadWindowLink = null;
var loadWindowMaster = null;
var navItemPressed = null;
var newAndReference = null;
var offShowObject = null;
var setFieldRef = null;
var setObjectRef = null;
var setNoActiveTab = top.trySetNoActiveTab;
var setNoActiveTabCleanButtonBar = null;
var setSearchTextValues = null;
var showFieldRefQuery = null;
var showHelp = null;
var showMaster = null;
var showNavItem = null;
var showObject = top.tryShowObject;
var showExtObject = null;
var showRightsObject = null;
var showSearch = null;
var showSingleButton = null;
var showState = null;
var showStylesheet = null;
var showWeblink = null;
var showWindowWeblink = null;
var singleButtonPressed = null;
var tabPressed = null;
var toggleNode = null;
var getHtmlPageStart = null;

// redirected top methods of form validation:
var cD = null;
var cDT = null;
var cTx = null;
var cTxI = null;
var fVWiI = null;
var iD = null;
var iDA = null;
var iDB = null;
var iDTA = null;
var iDTB = null;
var iEm = null;
var iI = null;
var iIGE = null;
var iIR = null;
var iLLE = null;
var iM = null;
var iMGE = null;
var iMLE = null;
var iMR = null;
var iNE = null;
var iNu = null;
var iT = null;
var iTA = null;
var iTB = null;
var iTx = null;
var iFile = null;
var iFileExt = null;
var mRef = null;
var iESlct = null;
var iERadio = null;


//============= application startup functions =================================

/**
 * void framesLoaded ()
 * Tell the application that all frames are loaded.
 */
function framesLoaded ()
{
    // load the minor scripts:
    loadMinorScripts ();
} // framesLoaded


/**
 * void loadMajorScripts ()
 * Load the major scripts.
 */
function loadMajorScripts ()
{
    var url = window.location.href;     // the complete url
    var pos = 0;                        // position within string

    window.status = "loading scripts...";

    // try to get the base url out of the window url:
    if ((pos = url.lastIndexOf ('app/')) >= 0)
    {
        url = url.substring (0, pos);
    } // if
    else if ((pos = url.lastIndexOf ('/')) >= 0)
    {
        url = url.substring (0, pos + 1);
    } // else if
    else
    {
        url = "/m2/";
    } // else

    // the scripts directory:
    url += "app/scripts/";

    // load the scripts:
    loadScript (document, url + "scriptBrowser.js");
    loadScript (document, url + "scriptSystem.js");
    loadScript (document, url + "scriptConfig.js");
    loadScript (document, "scriptSpecificStartup.js");
} // loadMajorScripts


function loadMinorScripts ()
{
    // load the scripts:
    self.open (top.system.baseDir + '/scripts/scripts.htm', 'scripts');
} // loadMinorScripts


/**
 * void loadScript (DocumentObject doc, String path)
 * Load a specific javascript file. The file is defined through the path which
 * contains the directories and the file name itself.
 */
function loadScript (doc, path)
{
    doc.write (
        '<SCRIPT LANGUAGE="javascript" TYPE="text/javascript" SRC="' + path +
        '"></SCRIPT>');
    addScript ();
} // loadScript


/**
 * void addScript ()
 * Add another script to the loaded scripts.
 * Display a status text.
 */
function addScript ()
{
    if (++scriptCount >= scriptSum)
    {
        setScriptsLoaded ();
    } // if
    else if (actCall > 0)
    {
        showLoadState ();
    } // if
} // addScript


/**
 * void showLoadState ()
 * Display the current load state.
 */
function showLoadState ()
{
/*
    var doc = top.sheettabs.document;
    var actState = Math.floor (scriptCount / scriptSum * 100);
    doc.open ();
    doc.write ('<HTML><BODY BACKGROUND="Images/global/folderup.jpg" TEXT="WHITE">');
    doc.write ('Loading scripts: ' + actState + '%' + scriptCount + '.' + scriptSum);
    doc.write ('</HTML></BODY>');
    doc.close ();
*/
    if (scriptsLoaded)                  // all scripts loaded?
    {
        top.showState ("all scripts loaded.");
    } // if all scripts loaded
    else                                // scripts still loading
    {
        window.state = "loaded script no. " + scriptCount +
                       "; number of calls: " + actCall;
    } // else scripts still loading
} // showLoadState


/**
 * void setScriptsLoaded ()
 * Tell the application that all scripts have been loaded.
 */
function setScriptsLoaded ()
{
    scriptsLoaded = true;
    var scr = top.scripts;

    // set redirections for top methods:
    buttonPressed = scr.buttonPressed;
    callUrl = scr.callUrl;
    cleanButtonBar = scr.cleanButtonBar;
    content = scr.content;
    createButtonBar = scr.createButtonBar;
    createLinkTabReference = scr.createLinkTabReference;
    createTabBar = scr.createTabBar;
    dropButtonBar = scr.dropButtonBar;
    reloadButtons = scr.reloadButtons;
    reloadNavBar = scr.reloadNavBar;
    dropTabBar = scr.dropTabBar;
    resetTabs = scr.resetTabs;
    goback = scr.goback;
	reloadMultilang = scr.reloadMultilang;
	setMultilangLoaded = scr.setMultilangLoaded;
	isMultilangLoaded = scr.isMultilangLoaded;
    goforward = scr.goforward;
    load = scr.load;
    loadCont = scr.loadCont;
    loadEvent = scr.loadEvent;
    loadEventArg = scr.loadEventArg;
    loadFile = scr.loadFile;
    loadFileInNewWindow = scr.loadFileInNewWindow;
    loadPreview = scr.loadPreview;
    saveFile = scr.saveFile;
    loadLink = scr.loadLink;
    loadForReference = scr.loadForReference;
    loadInWindow = scr.loadInWindow;
    loadMaster = scr.loadMaster;
    loadOrderWindow = scr.loadOrderWindow;
    loadOrderWindowInList = scr.loadOrderWindowInList;
    loadTabEvent = scr.loadTabEvent;
    loadWeblink = scr.loadWeblink;
    loadWindowFile = scr.loadWindowFile;
    loadWindowLink = scr.loadWindowLink;
    loadWindowMaster = scr.loadWindowMaster;
    navItemPressed = scr.navItemPressed;
    newAndReference = scr.newAndReference;
    offShowObject = scr.offShowObject;
    setFieldRef = scr.setFieldRef;
    setObjectRef = scr.setObjectRef;
    setNoActiveTab = scr.setNoActiveTab;
    setNoActiveTabCleanButtonBar = scr.setNoActiveTabCleanButtonBar;
    setSearchTextValues = scr.setSearchTextValues;
    showFieldRefQuery = scr.showFieldRefQuery;
    showHelp = scr.showHelp;
    showMaster = scr.showMaster;
    showNavItem = scr.showNavItem;
    showObject = scr.showObject;
    showExtObject = scr.showExtObject;
    showRightsObject = scr.showRightsObject;
    showSearch = scr.showSearch;
    showSingleButton = scr.showSingleButton;
    showState = scr.showState;
    showStylesheet = scr.showStylesheet;
    showWeblink = scr.showWeblink;
    showWindowWeblink = scr.showWindowWeblink;
    singleButtonPressed = scr.singleButtonPressed;
    toggleNode = scr.toggleNode;
    tabPressed = scr.tabPressed;
    getHtmlPageStart = scr.getHtmlPageStart;

    // set redirections for top methods of form validation:
    cD = scr.cD;
    cDT = scr.cDT;
    cTx = scr.cTx;
    cTxI = scr.cTxI;
    fVWiI = scr.fVWiI;
    iD = scr.iD;
    iDA = scr.iDA;
    iDB = scr.iDB;
    iDTA = scr.iDTA;
    iDTB = scr.iDTB;
    iEm = scr.iEm;
    iI = scr.iI;
    iIGE = scr.iIGE;
    iIR = scr.iIR;
    iLLE = scr.iLLE;
    iM = scr.iM;
    iMGE = scr.iMGE;
    iMLE = scr.iMLE;
    iMR = scr.iMR;
    iNE = scr.iNE;
    iNu = scr.iNu;
    iT = scr.iT;
    iTA = scr.iTA;
    iTB = scr.iTB;
    iTx = scr.iTx;
    iFile = scr.iFile;
    iFileExt = scr.iFileExt;
    mRef = scr.mRef;
    iESlct = scr.iESlct;
    iERadio = scr.iERadio;

/* KR 20060331 Does not work on MSIE 6.x
    // load the scripts out of the scripts frame or object:
    loadScripts (scr);
*/

    if (actCall > 0)                    // at least one call queued?
    {
        // execute the calls in the order they were triggered:
        for (var i = 1; i <= actCall; i++)
        {
            eval (pendingCalls[i]);
        } // for
    } // if at least one call queued
} // setScriptsLoaded


/**
 * void loadScripts (Object obj)
 * Load the scripts out of a specific frame or object into the actual context.
 */
function loadScripts (obj)
{
    var arr = new Array ();
    for (var i in obj)
    {
        arr[arr.length] = i;
    } // for i

    // must be several 100 elements:
    alert (iter (arr, null, "\n"));
} // loadScripts


/**
 * void tryCall (String call)
 * Try to execute a call. If all scripts are loaded the call is executed
 * immediately. Otherwise the call is added to a queue of pending calls which
 * will be executed after all scripts have been loaded.
 */
function tryCall (call)
{
//alert ('trying call #' + call + '#')
    if (scriptsLoaded)                  // the scripts have been loaded?
    {
        eval (call);
    } // if the scripts have been loaded
    else                                // not all scripts loaded
    {
        pendingCalls[++actCall] = call;
        showLoadState ();
    } // else not all scripts loaded
} // tryCall


/**
 * void tryShowObject (OidString oid)
 * Try to execute the showObject method.
 */
function tryShowObject (oid)
{
    tryCall ('top.showObject (\'' + oid + '\')');
} // tryShowObject


/**
 * void trySetNoActiveTab (OidString oid)
 * Try to execute the setNoActiveTab method.
 */
function trySetNoActiveTab ()
{
    tryCall ('top.setNoActiveTab ()');
} // trySetNoActiveTab



//============= common functions ==============================================

/**
 * String getSearchString (DocumentObject doc)
 * Get the search string out of the url (the part after the '?'). This is the
 * original string after the '?', but it is unescaped.
 * If there is no search string in the url the result is "".
 */
function getSearchString (doc)
{
    var searchString = "";              // the resulting string
    var search = "";                    // the unparsed search string
    var actPos = 0;                     // actual position
    var lastPos = 0;                    // last position
    var SPC = '+';                      // escape string for spaces
                                        // (these need special handling)

    // get the original search string:
    if ((search = doc.location.search.substring (1)) != "")
                                        // search string found?
    {
        // loop through the string and replace all escaped spaces with real
        // spaces:
        for (actPos = search.indexOf (SPC); actPos >= 0;)
        {
            searchString += search.substring (lastPos, actPos) + ' ';
            lastPos = actPos + SPC.length;
            actPos = search.indexOf (SPC, lastPos);
        } // for
        // append the rest of the string:
        searchString += search.substring (lastPos);

        // unescape the string:
        searchString = decodeURI (searchString);
    } // if search string found

    // return the computed string:
    return (searchString);
} // getSearchString


/**
 * void setTitle (String title)
 * Set the title of the browser window.
 */
function setTitle (title)
{
    var urlTitle;                       // the title out of the url

    // get the search string out of the url:
    if ((urlTitle = getSearchString (top.document)) != "")
    {
        title = urlTitle;
    } // if

//    doc.write (title.italics ());
    top.document.write ("<TITLE>" + title + "</TITLE>");
} // setTitle


/**
 * String getFrameName (FrameObject frame)
 * Get the name of a specific frame below top.
 * If the frame is the top window the String 'top' is returned, otherwise this
 * function returns 'top.<framename>'.
 */
function getFrameName (frame)
{
    var name = 'top';                   // initialize the frame name
    if (frame != top)                   // the frame is not top?
        name += '.' + frame.name;       // append the frame name to the string

    return (name);                      // return the computed name
} // getFrameName


/*
 * void debug ()
 * A simple debugger.
 */
function debug ()
{
    var code = "";
    code = prompt ("debugging code", code);
    while (code != null && code != "")
    {
        alert (eval (code));
        code = prompt ("debugging code", code);
    } // while
} // debug


/**
 * String iter (Array arr, String propName, String sep)
 * Iterate over the elements of an array and return one property of each
 * element.
 */
function iter (arr, propName, sep)
{
    var result = new Array ();
    var i = 0;
    var name = (propName == null ? "" : "." + propName);
    var length = arr.length;

    for (i = 0; i < length; i++)
    {
        result[result.length] = eval ("arr[i]" + name);
    } // for elem

    if (sep == null)
    {
        sep = ",";
    } // if

    // compute and return the result:
    return length + " elements:\n" + result.join (sep);
} // iter


// tryCall ('alert ("Scripts wurden geladen.")');

var backAllowed = false;

/*
 * void unload ()
 * Unload of application.
 */
function unload ()
{
    if (top.backAllowed)
    {
        top.goback (1);
        top.backAllowed = false;
    } // if
} // unload


/**
 * void setUrls (String startMenu)
 * Set the urls for the frameset.
 * startMenu may hava one of the following values:
 * "none" ...... no menu activated
 * "group" ..... group menu activated
 * "private" ... private menu activated
 */
function setUrls (startMenu)
{
    var offlineDir = top.system.getOfflineDir ();
    var url = top.getBaseUrl () + '&fct=';

    if (top.system.isOnline)            // online?
    {
        urlWelcome   = url + '131';
        urlNomenu    = url + '13&menu=none';
        urlGroup     = url + '13&menu=group';
        urlPrivate   = url + '13&menu=private';
        urlSearch    = url + '85&evt=99&slct=true';
        urlSearchNoSelect = url + '85&evt=99&slct=false';
        urlSearchAdv = top.getBaseUrl ();
        urlMenu      = url + '13&menu=' + startMenu;
        urlMultilangInfo = url + '31';
    } // online
    else                                // offline
    {
        urlWelcome   = offlineDir + 'welcome.html';
        urlNomenu    = offlineDir + 'navigationtabs.html?none';
        urlGroup     = offlineDir + 'navigationtabs.html?group';
        urlPrivate   = offlineDir + 'navigationtabs.html?private';
        urlSearch    = offlineDir + 'search.html';
        urlSearchNoSelect = offlineDir + 'search.html';
        urlSearchAdv = offlineDir + 'advancedsearchframe.html';
        urlMenu      = urlNomenu;
        urlMultilangInfo = offlineDir + 'mlitexts.html';
    } // offline

    if (!startMenu || startMenu == null || startMenu == "" ||
        startMenu == "none")
        urlMenu = urlNomenu;
    else if (startMenu == "group")
        urlMenu = urlGroup;
    else if (startMenu == "private")
        urlMenu = urlPrivate;
} // setUrls


// load the major scripts:
loadMajorScripts ();
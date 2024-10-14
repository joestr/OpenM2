/******************************************************************************
 * This file contains the JavaScript code which is common to all m2 instances
 * (independent of the layout) and is executed within top.scripts. <BR>
 *
 * @version     $Id: scriptCommon.js,v 1.87 2011/08/18 09:50:17 btatzmann Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */

//============= declared classes and functions ================================
// class Status
// class Tracer

// function showState
// function clearState
// function strEquals
// function showHelp
// function getUpperObjectId
// function callUrl
// function checkCallAllowed
// function getFrameObject
// function getFrameObject2
// function checkFrameForm
// function content
// function goback
// function goforward
// function showHistory
// function setMultilangLoaded
// function isMultilangLoaded
// function reloadMultilang
// function showObject
// function showExtObject
// function offShowObject
// function showRightsObject
// function showWindowWeblink
// function showWeblink
// function loadWeblink
// function loadMaster
// function loadWindowMaster
// function showMaster
// function loadPreview
// function loadFile
// function loadLink
// function loadWindowLink
// function loadWindowFile
// function loadInNewWindow
// function loadInWindow
// function loadForReference
// function loadOrderWindow
// function loadOrderWindowInList
// function setValue
// function setSearchTextValues
// function callOidFunction
// function callOidFunction (actOid,
// function putInShoppingCart
// function showQuery
// function showFieldRefQuery
// function setFieldRef
// function toggleStyle
// function showSearch
// function createLinkTabReference
// function newAndReference
// function load
// function load
// function loadTypeObj
// function loadEvent
// function loadEventArg
// function loadTabEvent
// function loadCont
// function computeTopOffset
// function computeLeftOffset
// function computeWidth
// function computeHeight
// function computeFrameWidth
// function computeFrameHeight
// function computeFramePosX
// function computeFramePosY
// function computeYScrollOffset
// function notInOff
// function checkOnline
// function showStylesheet
// function startExport
// function getEscapedObjectRef
// function saveFile
// function setFieldFocus
// function getEvent
// function getFrameset
// function resizeFramesRec
// function resizeFrames
// function showSingleFrame
// function setFrameHeight
// function activateNavigation
// function startTimer
// function execTimer
// function showSearchFrame
// function hideSearchFrame
// function clearFrame
// function displayHistory
// function createHistoryElement
// function actualizeSearchFrameHistory
// function getHistoryFrame
// function toggleHistory
// function showUpHistory
// function hideHistory


//============= necessary classes and variables ===============================
// class System
// class TupleList
// class Tuple

//============= declarations ==================================================

// constants:
var OID_NOOBJECT = "0x0000000000000000";
var STATUS_TIMEOUT = 3000;              // timeout for status bar in milli
                                        // seconds
var OFFLINE_DATA = top.OFFLINE_DATA;    // path to the offline files

// menu kinds:
// these constants are necessary here because they must be loaded BEFORE
// scriptSpecific.js.
var v_menu_mk = 0;
var c_menu_MK_UNDEF = v_menu_mk++;      // no defined menu kind
var c_menu_MK_TREE = v_menu_mk++;       // menu kind tree
var c_menu_MK_TREE_LINED = v_menu_mk++; // menu kind tree with lines
var c_menu_MK_WHEEL = v_menu_mk++;      // menu kind wheel


// variables:
var hasLayers = document.layers;        // does the browser have the ability
                                        // of layers (NS)?
var hasAll = document.all;              // does the browser have the all object
                                        // (MSIE)?
var statusBar = new Status (STATUS_TIMEOUT); // create new object for handling
                                        // status messages
var tab = null;                         // used for storing the actual tab
top.tracer = new Tracer ();             // the tracer
/*
var openWindows = new TupleList ("windows", top, false); // list of open windows
*/
var v_framesets = new Array ();         // actual framesets for animations
var v_menuKind = c_menu_MK_TREE;        // kind of menu bar
//var v_menuKind = c_menu_MK_TREE_LINED;  // kind of menu bar
var v_isFrameAnimationInProgress = false; // currently no frame animation

var v_sheettabsHeight = "30";           // the default height of the frame sheettabs
var v_buttonsHeight = "50";             // the default height of the frame buttons

var _lastHideHistory = new Date ();     // time when the history has been hidden the last time
										// necessary for the toggle handling of the history

var v_refFieldName = null;              // holds the fieldname of the field a reference
                                        // search (fieldref search, objectref search, ...)
                                        // has just been executed for


//============= class Status ==================================================

/**
 * void Status_print (String message)
 * Display a status message.
 */
function Status_print (message)
{
    // ensure that the clear timeout does not affect the current output:
    clearTimeout (this.offTimerId);

    // store the message:
    this.message = message;
    // display the message:
    window.status = this.message;

    // set time out for the message:
    this.offTimerId = setTimeout ("clearState ()", this.timeout);
} // Status_print


/**
 * void Status_clear ()
 * Clear the status output.
 */
function Status_clear ()
{
    // delete the message:
    this.message = "";
    // clear the output:
    window.status = this.message;

    // clear timeout:
    clearTimeout (this.offTimerId);
    this.offTimerId = null;
} // Status_clear


/**
 * Status Status (int timeout)
 * Initialize the status bar of the current output.
 */
function Status (timeout)
{
    // define methods:
    this.print = Status_print;          // display a new status text
    this.clear = Status_clear;          // clear the status output

    // define properties and assign initial values:
    this.offTimerId = null;             // timer used for switching the state
                                        // off
    this.timeout = timeout;             // time to clear the state after
                                        // displaying (in milli seconds)
    this.message = "";                  // the last state message

    // clear the status bar:
    this.clear ();
} // Status


//============= class Tracer ==================================================

/**
 * void Tracer_print ([boolean clear])
 * Display the tracer messages.
 */
function Tracer_print (clear)
{
    var outStr = '';
    var baseTime = this.times[0];
    var diff = 0;
    var diffStr = "";

    // loop through all entries:
    for (var i = 0; i < this.length; i++)
    {
        diff = 10000000 + (this.times[i] - baseTime);
        diffStr = '' + diff;
        diffStr = diffStr.substr (1, 4) + '.' + diffStr.substr (5);

        outStr += diffStr + ': ' + this.messages[i] + '\n';
    } // for i
    // display the message:
    alert (outStr);

    // check if the tracer shall be cleared:
    if (clear)
    {
        // clear the tracer:
        this.clear ();
    } // if
} // Tracer_print


/**
 * void Tracer_clear ()
 * Clear the Tracer output.
 */
function Tracer_clear ()
{
    // delete the messages and times:
    this.messages = new Array ();
    this.times = new Array ();
    this.length = 0;
} // Tracer_clear


/**
 * void Tracer_trace (String message)
 * Trace a new message.
 */
function Tracer_trace (message)
{
    // delete the messages and times:
    this.messages[this.length] = message;
    this.times[this.length++] = new Date ();
} // Tracer_trace


/**
 * Tracer Tracer ()
 * Initialize the tracer.
 */
function Tracer ()
{
    // define methods:
    this.print = Tracer_print;          // display the tracer text
    this.trace = Tracer_trace;          // add a new trace message
    this.clear = Tracer_clear;          // clear the tracer output

    // define properties and assign initial values:
    this.messages = new Array ();       // the messages
    this.times = new Array ();          // the times for the messages
    this.length = 0;                    // number of elements

    // clear the tracer:
    this.clear ();
} // Tracer


//============= class Tuple ===================================================

/**
 * OpenFile OpenFile (String fileName, Window win)
 * Constructur of class OpenFile.
 */
function OpenFile (fileName, win)
{
    // call super constructor(s): (here not necessary)
    Tuple.call (this, fileName, fileName);

    // set property values:
    this.win = win;
} // OpenFile

// create class form constructor:
createClass (OpenFile, Tuple,
{
    // define properties and assign initial values:
    win: null                           // the window
}); // OpenFile


//============= common functions ==============================================

/**
 * true showState (String text)
 * Show state message on status bar.
 */
function showState (text)
{
    statusBar.print (text);
    return true;
} // showState


/**
 * void clearState ()
 * Clear the status bar.
 */
function clearState ()
{
    statusBar.clear ();
} // clearState


function strEquals (str1, str2)
{
    var i = 0;
    if (str1.length != str2.length)
        return (false);
    for (i = 0; i < str1.length && str1[i] == str2[i]; i++);
    if (i < str1.length)
        return (false);
    return (true);
} // strEquals


function showHelp ()
{
    alert (top.multilang.ibs_ibsbase_scripts_MSG_notImplemented);
} // showHelp


function getUpperObjectId ()
{
    if (top.isOtherContent && top.otherContentId != top.containerId &&
        top.otherContentId != top.oid)
        return (top.otherContentId);
    else if (top.majorOid != OID_NOOBJECT)
    {
        var localOid = top.majorOid;
        top.majorOid = OID_NOOBJECT;
        return (localOid);
    } // else if
    else if (top.containerId != OID_NOOBJECT)
    {
        isOtherContent = false;
        return (top.containerId);
    } // else if
    else
    {
        alert (top.multilang.ibs_ibsbase_scripts_MSG_toplevel);
        return (OID_NOOBJECT);
    } // else
} // getUpperObjectId


/**
 * WindowObject callUrl (...)
 * Call an url.
 * Potential kinds of calls:
 * callUrl (int fct, {String params | null}, {String fileName | null},
 *          String target[, String winParams[, boolean noCheckAllowed]])
 * callUrl (String url, null, {String fileName | null},
 *          String target[, String winParams[, boolean noCheckAllowed]])
 * callUrl (null, String file, {String fileName | null},
 *          String target[, String winParams[, boolean noCheckAllowed]])
 *
 * fct ........ The m2 function to be called (integer value).
 * params ..... The parameters for the function ("&oid=...&name=value&...")
 * url ........ The complete url to be called.
 * file ....... The name of the file to be displayed.
 *              (This must be a web path relativ to the layout directory.)
 * fileName ... The name of the file to be displayed in case of offline mode.
 *              (This name must not contain the extension ".html", it is
 *              appended autmatically!)
 * target ..... The name of the target window or frame. If it does not exist
 *              there is a new window opened with this name.
 * winParams .. Parameters for the window (used for function window.open ()).
 * checkAllowed Is it not allowed to check whether the url may be loaded or not?
 *
 * The result is a pointer to the window or frame in which the url was opened.
 * If it was not possible to call the url the result is null.
 */
function callUrl (fct, params, fileName, target, winParams, noCheckAllowed)
{
    var url = null;                     // the url to be called
    var file = params;                  // the name of the file
    var win = null;                     // the opened window
/*
    var tuple = null;                   // the window tuple
*/

    // check if online or offline:
    if (checkOnline (false))            // online?
    {
        if (fct && fct != null)         // function is defined and valid?
        {
            if (isNaN (fct))            // function is complete url?
            {
                // the online url:
                url = fct;
            } // else if function is complete url
            else                        // function is number
            {
                // ensure that the params can be concatenated:
                if (!params || params == null)
                {
                    params = "";        // no params
                } // if

                // the online url:
                url = top.getBaseUrl () + "&fct=" + fct + params;
            } // else function is number
        } // if function is defined and valid
        else                            // no valid function
        {
            // display the file from within the layout directory:
            url = top.system.layoutDir + file;
        } // else no valid function
    } // if online
    else if (fileName && fileName != null && fileName != "") // offline
    {
        // if no fileName is defined then no url shall be called in offline mode
        // the offline url:
        url = top.system.getBaseDir () + OFFLINE_DATA + fileName + '.html';
    } // else offline

    // check if an url shall be called:
    if (url != null)                    // call an url?
    {
        // ensure that the noCheckAllowed value is correctly set:
        if (!noCheckAllowed)
        {
            noCheckAllowed = false;
        } // if

        // check whether it is allowed to place a call in the target frame or
        // window:
        if (noCheckAllowed || checkCallAllowed (target)) // a call is allowed?
        {
            if (target == "top")        // top frame?
            {
                // necessary because top has no name     for the frame!
                // call the url and return the top frame:
                top.location.href = url;
                win = top;
            } // if top frame
            else                        // any other frame or window
            {
                // call the url and return the window object:
                win = self.open (url, target, winParams);
            } // else any other frame or window
/*
            // remember the window:
            tuple = new Tuple (target, win);
            if (!openWindows.replace (tuple))
            {
                openWindows.add (tuple);
            } // if
*/
        } // if a call is allowed
        else                            // no call allowed
        {
            // return the default value:
            win = null;
        } // else no call allowed
    } // if
    else
    {
        // function not supported:
        notInOff ();
        // return error code:
        win = null;
    } // else

    // return the result:
    return (win);
} // callUrl


/**
 * boolean checkCallAllowed (String target)
 * Check if it is allowed to place a call within the target frame.
 * If there is currently a form in one of the editable frames the user is
 * asked if he really wants to perform the operation and loose his data.
 */
function checkCallAllowed (target)
{
return true;

    var retValue = true;                // the result of this function
    var frame = null;                   // the actual frame

    if (typeof (target) == "string")
    {
        // try to get the frame object:
        frame = getFrameObject (target, top);
    } // if
    else
    {
        // use the target as is:
        frame = target;
    } // else

    // check if the frame contains any forms:
    if (frame != null && checkFrameForm (frame)) // the frame contains forms?
    {
        // allow the user to decide whether he wants to loose his data or
        // not:
        retValue = confirm (MSG_formChangeLost);
//        alert (frame.name + ": " + frame.document.forms.length + " forms.\n" + url);
    } // if the frame contains forms

    // return the result:
    return retValue;
} // checkCallAllowed


/**
 * FrameObject getFrameObject (String name[, FrameObject startFrame])
 * Get a frame object by name recursively starting at the actual frame.
 */
function getFrameObject (name, startFrame)
{
    var frame = null;                   // the frame object
    var tuple = null;                   // the window tuple

    if (!startFrame || startFrame == null) // no start frame defined?
    {
        // use the toplevel frame:
        startFrame = top;
    } // if no start frame defined

    if (name == "top")                  // top frame?
    {
        if (startFrame == top)          // start at the top frame?
        {
            // set the handle:
            frame = top;
        } // if start at the top frame
    } // if top frame
    else                                // any frame
    {
        // get the frame object:
        frame = getFrameObject2 (name, startFrame);
//alert (frame + "\n" + name);
    } // else any frame

    if (frame == null && top.system.browser.ns)
    {
        if (name == "sheet1" && top.sheet && top.sheet.sheet1)
        {
            frame = top.sheet.sheet1;
        } // if
        else if (name == "sheet2" && top.sheet && top.sheet.sheet2)
        {
            frame = top.sheet.sheet2;
        } // else if
    } // if

    // return the result:
    return frame;
} // getFrameObject


/**
 * FrameObject getFrameObject (String name, FrameObject startFrame)
 * Get a frame object by name recursively starting at the actual frame.
 */
function getFrameObject2 (name, startFrame)
{
    var frame = null;                   // the frame object
    var frames = null;                  // the sub frames

    if (startFrame.name == name)   // found the frame?
    {
        // store the frame:
        frame = startFrame;
    } // if found the frame
    else                                // frame not found
    {
        // get the frames:
        // get the sub frames of the current frame:
        frames = startFrame.frames;

        if (frames != null)             // frames exist?
        {
            for (var i = 0; frame == null && i < frames.length; i++)
            {
                // call the function recursively:
                frame = getFrameObject2 (name, frames[i]);
            } // for
        } // if frames exist
    } // else frame not found

    // return the result:
    return frame;
} // getFrameObject2


/**
 * boolean checkFrameForm ([FrameObject frame])
 * Check if a frame or any of the below frames contains a form.
 */
function checkFrameForm (frame)
{
    var formFound = false;              // the return value

    // ensure that a correct start frame is set:
    if (!frame || frame == null)        // no start frame defined?
    {
        // use the toplevel frame:
        frame = top;
    } // if no start frame defined

    // check if the frame contains a form:
    if (frame.document.forms.length > 0) // the frame contains a form?
    {
        formFound = true;
    } // if the frame contains a form
/*
    else if (frame == top.sheet)        // the sheet frame contains no form?
    {
        if (frame.frames.length > 0)
        {
//            alert (top.sheet.sheet1);
//            checkFrameForm (top.sheet.sheet2);
        } // if
    } // else if the sheet frame contains no form
*/
    else if (!top.exists (frame.checkSubFrames) || frame.checkSubFrames)
                                        // no form; check sub frames?
    {
        // search in the sub frames:
        for (var i = 0; !formFound && i < frame.frames.length; i++)
        {
            // call the function recursively:
            formFound = checkFrameForm (frame.frames[i]);
        } // for
    } // else if no form; check sub frames

    // return the result:
    return formFound;
} // checkFrameForm


function content ()
{
    if (top.containerId != OID_NOOBJECT)
    {
        callUrl (54, "&oid=" + top.oid + "&cid=" + top.containerId + "&sho=1&tabs=1", top.containerId + "_content", 'sheet');
    } // if
    else
    {
        alert (top.multilang.ibs_ibsbase_scripts_MSG_toplevel);
        return (OID_NOOBJECT);
    } // else
} // content


function goback (howmuch)
{
    callUrl (151, "&bck=-" + howmuch + "&sho=1&tabs=1", null, 'sheet');
} // goback

function goforward (howmuch)
{
    callUrl (151, "&bck=" + howmuch + "&sho=1&tabs=1", null, 'sheet');
} // goforward

function showHistory ()
{
    callUrl (152, "", null, 'sheetnavigation');
} // showHistory

/**
 * void setMultilangLoaded (boolean isLoaded)
 * Sets the flag that the locale specific mliclient.htm file has
 * been already loaded.
 */
function setMultilangLoaded (isLoaded)
{
	// Set multilang texts loaded
	top.multilangTextsLoaded = isLoaded;
} // setMultilangLoaded

/**
 * boolean isMultilangLoaded ()
 * Returns if the multilang client texts are generally available.
 * Means the at least the locale specific mliclient.htm file has
 * been already loaded.
 */
function isMultilangLoaded ()
{
	// Set multilang texts loaded
	return top.multilangTextsLoaded;
} // isMultilangLoaded

/**
 * void reloadMultilang ()
 * Reinitializes the hidden multilang frame. Can be called if the user's
 * locale changes or the preloaded mli text files have been updated.
 */
function reloadMultilang ()
{
	// Set multilang texts loaded to false.
	// Set flag is further on set to true within the mliclient.htm
	// file when loaded.
	top.setMultilangLoaded (false);

	// Resets the multilang frame, which causes a frame reload
	// if the locale has been changed the server redirects to
	// another mli text file
    top.multilang.location.href = top.urlMultilangInfo;
	
	// Reload the button bar
	top.reloadButtons ();
	
    // Reload the tab bar
    top.resetTabs ();
    
    // Reload the nav bar
    top.reloadNavBar ();
} // reloadMultilang

function showObject (oid)
{
    top.setNoActiveTab ();
    
	// IBS-90: Check if the object is a search (check method with OID is a HACK!!!).
	// is search
	if(oid.indexOf("0x01017F51") != -1)
	{
		// show the search frame
		top.scripts.showSearchFrame(false, true, true);
			
		// open the query within the search frame
		callUrl (51, '&oid=' + oid + '&sho=1&tabs=1', oid, 'searchFrame');
	} // if
    else
    {	
		// Clear the sheettabs frame
	    clearFrame(top.sheettabs);

	    // open the object within the sheet frame
    	callUrl (51, '&oid=' + oid + '&sho=1&tabs=1', oid, 'sheet'); 	
	       
    	// Hide the search form and switch to the sheet frame
		hideSearchFrame (false);
    } // else
} // showObject

function showExtObject (oid, extId)
{
    top.setNoActiveTab ();
    callUrl (51, '&oid=' + oid + '&sho=1&tabs=1&extId=' + escape (extId), oid, 'sheet');
} // showExtObject

function offShowObject (oid)
{
    notInOff ();
} // offShowObject


function showRightsObject (oid, rPersonId)
{
    if (checkOnline (false))            // online?
    {
        top.setNoActiveTab ();
        callUrl (153, "&oid=" + oid + "&rpid=" + rPersonId + "&sho=1&tabs=1", null, 'sheet');
    } // if online
} // showObject


function showWindowWeblink (url)
{
    callUrl (url, null, null, "_new");
} // showWindowWeblink


function showWeblink (url)
{
    var target = top.sheet;
    if (showWeblink.arguments[1] != null)
    {
        target = showWeblink.arguments[1];
    } // if
    callUrl (url, null, null, target.name);
} // showWeblink


function loadWeblink (oid, targetFrame)
{
    var target = top.temp;
    var fct = 667;
    if (targetFrame && targetFrame != null)
    {
        target = targetFrame;
    } // if
    callUrl (fct, "&oid=" + oid, null, target.name);
} // loadWeblink


function loadMaster (oid, targetFrame)
{
    var target = top.temp;
    var fct = 109;
    if (targetFrame && targetFrame != null)
    {
        target = targetFrame;
    } // if
    callUrl (fct, "&oid=" + oid, null, target.name);
} // loadMaster


function loadWindowMaster (oid)
{
    var target = top.temp;
    var fct = 109;
    callUrl (fct, "&oid=" + oid, null, "_new", "toolbar=yes,scrollbars=yes,directories=no,menubar=yes,resizable=yes,width=800,height=600,screenX=0,screenY=0");
} // loadWindowMaster


function showMaster (url)
{
    var target = top.sheet;
    var doc = target.document;

    doc.open ();
    doc.write (getHtmlPageStart () +
               '<META HTTP-EQUIV="Pragma" CONTENT="no-cache">' +
               '<FRAMESET ROWS="*,0" BORDER="0" FRAMEBORDER="1" FRAMESPACING="5" onUnload="checkSubFrames = true;">\n' +
               '<FRAME SRC="' + url + '" NAME="master" FRAMEBORDER="1" MARGINWIDTH="0" MARGINHEIGHT="0" SCROLLING="AUTO">\n' +
               '<FRAME SRC="' + top.system.layoutDir + 'empty.htm" NAME="slave" FRAMEBORDER="0" MARGINWIDTH="0" MARGINHEIGHT="0" SCROLLING="NO">\n' +
               '</FRAMESET>\n' +
               '</HTML>');
    doc.close ();

    // ensure that the sub frames are not checked:
    target.checkSubFrames = false;
} // showMaster


function loadPreview (file, target)
{
    var doc = target.document;

    doc.open ();
    doc.write (getHtmlPageStart () +
               '<META HTTP-EQUIV="Pragma" CONTENT="no-cache">' +
               '<META HTTP-EQUIV="Expires" CONTENT="Tue, 01 Jan 1980 1:00:00 GMT">' +
               '<FRAMESET ROWS="*" BORDER="0" FRAMEBORDER="1" FRAMESPACING="5" onUnload="checkSubFrames = true;">\n' +
               '<FRAME SRC="' + top.system.getFileAccessUrl (file) + '" NAME="document" FRAMEBORDER="1" NORESIZE>\n' +
               '</FRAMESET>\n' +
               '</HTML>');
    doc.close ();
    // ensure that the sub frames are not checked:
    target.checkSubFrames = false;
} // loadPreview


function loadFileInWindow (file, win, onload, onunload, encodeUri)
{
    if (!win || win == null)
    {
        win = top;
    } // if
    var doc = win.document;

    doc.open ();
    doc.write (
        getHtmlPageStart () +
        '<META HTTP-EQUIV="Pragma" CONTENT="no-cache">' +
        '<META HTTP-EQUIV="Expires" CONTENT="Tue, 01 Jan 1980 1:00:00 GMT">' +
        '<FRAMESET ROWS="*,0" BORDER="0" FRAMEBORDER="1" FRAMESPACING="5"' +
            (onload != null ? ' onLoad="' + onload + '"' : '') +
            (onunload != null ? ' onUnload="' + onunload + '"' : '') +
        '>\n' +
            '<FRAME SRC="' + top.system.getFileAccessUrl (file, encodeUri) + '"' +
            ' NAME="document" FRAMEBORDER="1" NORESIZE>\n' +
            '<FRAME SRC="' + top.system.layoutDir + 'empty.htm"' +
            ' NAME="slave" FRAMEBORDER="0" MARGINWIDTH="0" MARGINHEIGHT="0"' +
            ' SCROLLING="NO">\n' +
        '</FRAMESET>\n' +
        '</HTML>');
    doc.close ();
} // loadFileInWindow


function loadFile (file, target, encodeUri)
{
    if (!target || target == null)
    {
        target = top.sheet;
    } // if
    top.setNoActiveTab ();

    loadFileInWindow (file, target,
                      "top.cleanButtonBar ()", "checkSubFrames = true;", encodeUri);

    // ensure that the sub frames are not checked:
    target.checkSubFrames = false;
} // loadFile


var v_openFiles = new TupleList ("openFiles");

/**
 * void loadFileInNewWindow (String file)
 * Load a file in a new browser.
 */
function loadFileInNewWindow (file)
{
    var i = 0;
    var elem = v_openFiles.get (file);

    // check if the file is already open in another window:
    if (elem != v_openFiles.notFound)   // file already open?
    {
        // bring the window to front:
        elem.win.focus ();
    } // if file already open
    else                                // file not open
    {
        // open the file in a new window:
        // first open the new window:
        var win = window.open (
            top.system.layoutDir + "empty.htm", "_blank",
            "dependent=no,hotkeys=yes,location=no,menubar=no,status=no," +
            "toolbar=no,resizable=yes,scrollbars=yes");
        // load the file within the window:
        loadFileInWindow (file, win, null, "top.unloadFile (top)");
        // set the title:
        win.document.title = file.substring (file.lastIndexOf ("/") + 1);

        // create unload function:
        win.unloadFile = top.scripts.unloadFile;

        // remember that the file is open:
        v_openFiles.add (new OpenFile (file, win));
    } // else file not open
} // loadFileInNewWindow


function unloadFile (win)
{
    var elem = null;

    // search for the window within all open files:
    for (var iter = v_openFiles.iterator (); iter.hasNext (); )
    {
        // check if we found the window:
        elem = iter.next ();
        if (elem.win == win)
        {
            // delete the object:
            v_openFiles.dropIndexed (elem.index);
            break;
        } // if
    } // for iter
} // unloadFile


function loadLink (link)
{
    var target = top.sheet;
    top.setNoActiveTab ();
    var doc = target.document;

    doc.open ();
    doc.write (getHtmlPageStart () +
               '<FRAMESET ROWS="*,0" BORDER="0" FRAMEBORDER="1" FRAMESPACING="5" onLoad="top.cleanButtonBar ()" onUnload="checkSubFrames = true;">\n' +
               '<FRAME SRC="' + link + '" NAME="document" FRAMEBORDER="1" NORESIZE>\n' +
               '<FRAME SRC="' + top.system.layoutDir + 'empty.htm" NAME="slave" FRAMEBORDER="0" MARGINWIDTH="0" MARGINHEIGHT="0" SCROLLING="NO">\n' +
               '</FRAMESET>\n' +
               '</HTML>');
    doc.close ();

    // ensure that the sub frames are not checked:
    target.checkSubFrames = false;
} // loadFile


function loadWindowLink (file)
{
    var win = callUrl (top.system.layoutDir + "empty.htm", null, "empty", "link", "toolbar=yes,scrollbars=yes,directories=no,menubar=yes,resizable=yes,width=800,height=600,screenX=0,screenY=0");
    var doc = win.document;

    doc.open ();
    doc.write (getHtmlPageStart () +
               '<META HTTP-EQUIV="Pragma" CONTENT="no-cache">' +
               '<FRAMESET ROWS="50,*" BORDER="0" FRAMEBORDER="1" FRAMESPACING="5">\n' +
               '<FRAME SRC="' + top.system.includeDir + "back.htm" + '" NAME="back" FRAMEBORDER="1" SCROLLING=NO NORESIZE>\n' +
               '<FRAME SRC="' + file + '" NAME="document" FRAMEBORDER="0" MARGINWIDTH="0" MARGINHEIGHT="0">\n' +
               '</FRAMESET>\n' +
               '</HTML>');
    doc.close ();
    doc.title = file;
    win.focus ();
} // loadWindowLink


function loadWindowFile (file, title)
{
    var win = callUrl (top.system.layoutDir + "empty.htm", null, "empty", "file", "toolbar=yes,scrollbars=yes,directories=no,menubar=yes,resizable=yes,width=800,height=600,screenX=0,screenY=0");
    var doc = win.document;

    doc.open ();
    doc.write (getHtmlPageStart () +
               '<META HTTP-EQUIV="Pragma" CONTENT="no-cache">' +
               '<FRAMESET ROWS="50,*" BORDER="0" FRAMEBORDER="1" FRAMESPACING="5">\n' +
               '<FRAME SRC="' + top.system.includeDir + "back.htm" + '" NAME="back" FRAMEBORDER="1" SCROLLING=NO NORESIZE>\n' +
               '<FRAME SRC="' + top.system.getFileAccessUrl (file) + '" NAME="document" FRAMEBORDER="1" MARGINWIDTH="0" MARGINHEIGHT="0" NORESIZE>\n' +
               '</FRAMESET>\n' +
               '</HTML>');
    doc.close ();
    if (title != "")
    {
        doc.title = title;
    } // if
    else
    {
        doc.title = file;
    } // else

    win.focus ();
} // loadWindowFile


function loadInNewWindow (fct)
{
    callUrl (fct, "&oid=" + top.oid + "&cid=" + top.containerId, null, "_new",
        "toolbar=no,scrollbars=yes,directories=no,menubar=no,resizable=yes,width=800,height=600,name='" + top.multilang.ibs_ibsbase_scripts_ML_orderWindowName + "'");
} // loadInNewWindow


function loadInWindow (fct)
{
    offlineUrl = null;

    if (fct == 5010)                    // order?
    {
        offlineUrl = top.oid + "printorder";
    } // if order

    callUrl (fct, "&oid=" + top.oid, offlineUrl, "_new",
        "toolbar=no,scrollbars=yes,directories=no,menubar=yes,resizable=yes,name='" + top.multilang.ibs_ibsbase_scripts_ML_orderWindowName + "'");
} // loadInWindow


function loadForReference (fct, referencedOid, targetOid, field)
{
    var target = top.sheet.sheet2;
    if (loadForReference.arguments[4] != null) // explicit target defined?
    {
        target = loadForReference.arguments[4];
    } // if
    else if (!target)                   // target does not exist?
    {
        target = top.sheet;
    } // if
    callUrl (fct, "&oid=" + referencedOid + "&calloid=" + targetOid + "&shwlnk=1&fieldname=" + escape (field), null, target.name);
} // loadForReference


function loadOrderWindow (fct)
{
    callUrl (fct, "&oid=" + top.oid, top.oid + "merken", "OrderWindow",
        "toolbar=no,scrollbars=yes,directories=no,menubar=no,resizable=yes,width=600,height=400,name='" + top.multilang.ibs_ibsbase_scripts_ML_shoppingCartWindowName + "'");
} // loadOrderWindow


function loadOrderWindowInList (fct, oids)
{
    callUrl (fct, "&oid=" + oids, oids + "merken", "OrderWindow",
        "toolbar=no,scrollbars=yes,directories=no,menubar=no,resizable=yes,width=600,height=400,name='" + top.multilang.ibs_ibsbase_scripts_ML_shoppingCartWindowName + "'");
} // loadOrderWindowInList


function setValue (fieldName, value, targetFrame)
{
    var target = top.sheet.sheet1.document.sheetForm;
    var field = null;

    if (targetFrame && targetFrame != null) // explicit target defined?
    {
        target = targetFrame;
    } // if
    else if (!target)                   // target does not exist?
    {
        target = top.sheet.document.sheetForm;
    } // else

    field = target[fieldName];
    field.value = value;

    if (field.fct_onChange != null)
    {
        field.fct_onChange (field);
    } // if
} // setValue


function setSearchTextValues (fieldName, value, oidValue)
{
    var target = top.sheet.sheet1.document.sheetForm;

    top.sheet.document.body.rows = "*,1";

    if (setSearchTextValues.arguments[3] != null) // explicit target defined?
    {
        target = setSearchTextValues.arguments[3];
    } // if
    else if (!target)                   // target does not exist?
    {
        target = top.sheet.document.sheetForm;
    } // else

    setValue (fieldName, value, target);
    setValue (fieldName + "_OID", oidValue, target);
    if (target[fieldName + "_BTC"] != null)
    {
        target[fieldName + "_BTC"].style.visibility = 'hidden';
    } // if

} // setSearchTextValues


function callOidFunction (actOid, fct)
{
    var oldOid = top.oid;
    top.oid = actOid;
    eval (fct);
    top.oid = oldOid;
} // callOidFunction


function putInShoppingCart (oid, count, validation, query)
{
    if (eval (validation))
    {
        callUrl (5101, "&oid=" + oid + "&evt=600" + "&count=" + count + "&query=" + query, null, 'temp');
    }
} // putInShoppingCart


function showQuery (name, rootObjOid)
{
    top.setNoActiveTab ();
    callUrl (51, "&oid=0x01017F5100000000&sho=1&tabs=1&qcrname=" + name + "&curoid=" + top.oid + "&rootobjoid=" + rootObjOid, null, 'sheet');
} // showQuery


function showFieldRefQuery
    (name, rootObjOid, searchField, matchType, searchValue, fieldName)
{
//alert (searchField + ', ' + matchType + ', ' + searchValue);

    if (fieldName != null)
    {
        v_refFieldName = fieldName;
    } // if
    var url = "&evt=103&oid=0x01017F5100000000&sho=1&tabs=1&qucrname=" + name + "&rootobjoid=" + rootObjOid + "&" + searchField + "_M=" + matchType + "&" + searchField + "=" + encodeURI (searchValue);

    // set current object oid:
    if (top.oid != null)
    {
        url += "&curoid=" + top.oid;
    } // if
    callUrl (85, url, null, 'sheet2');
} // showFieldRefQuery

/**
 * void setObjectRef()
 * 
 * Fills the object ref field and closes the search area  of the field ref element.
 * 
 * @param fieldName
 * @param value
 * @param oidValue
 * @return
 */
function setObjectRef (fieldName, value, oidValue)
{
    var fieldNameLocal = fieldName;
    
    // check if the field name has been cached
    if (v_refFieldName != null)
    {
        // use the cached field name
        fieldNameLocal = v_refFieldName;
    } // if

	setSearchTextValues (fieldNameLocal, value, oidValue);

    toggleStyle (top.sheet.sheet1.document,   
    		fieldNameLocal + '_div_search', 'objectRefSearch_open', 'objectRefSearch_closed',
    		fieldNameLocal + '_search_img',
    		top.system.layoutDir + 'images/global/elemOpen.gif',
    		top.system.layoutDir + 'images/global/elemClosed.gif');
} // setObjectRef

/**
 * void setFieldRef()
 * 
 * @param fieldName
 * @param oidValue
 * @param resultValues
 * @param dontToggleSearchArea Defines if the search area of the field ref element should not be toggled
 * @param dontUseCache  Defines not to use the cached fieldname
 * @return
 */
function setFieldRef (fieldName, oidValue, resultValues, dontToggleSearchArea, dontUseCache)
{
    var target = top.sheet.document.sheetForm;
    var i = 0;
    var fieldNameLocal = fieldName;
    var dontUseCacheLocal = (dontUseCache != null && dontUseCache);

    // check if the field name has been cached
    if (!dontUseCacheLocal && v_refFieldName != null)
    {
        // use the cached field name
        fieldNameLocal = v_refFieldName;
    } // if

    top.sheet.document.body.rows = "*,1";

    if (oidValue.substr (0, 2) != "0x")
    {
        oidValue = "0x" + oidValue;
    } // if

    setSearchTextValues (fieldNameLocal, '', oidValue, target);

    for (i = 0; i < resultValues.length; i++)
    {
        setValue (fieldNameLocal + "_" + i, resultValues[i], target);
    } // for i
    
    if(dontToggleSearchArea == null || !dontToggleSearchArea)
    {
	    // close the fieldref search div
	    toggleStyle (top.sheet.sheet1.document,   
	    		fieldNameLocal + '_div_search', 'fieldRefSearch_open', 'fieldRefSearch_closed',
	    		fieldNameLocal + '_search_img',
	    		top.system.layoutDir + 'images/global/elemOpen.gif',
	    		top.system.layoutDir + 'images/global/elemClosed.gif');
    } // if close the search ?
} // setFieldRef

function toggleStyle (doc, elemId, style1, style2, imgId, imgUrl1, imgUrl2)
{
    var elem = doc.getElementById (elemId);
    var imgUrl = imgUrl1;

    // check if we found the element:
    if (elem != null)
    {
        // check for current style and set the other one:
        if (elem.className == style1)
        {
            elem.className = style2;
            imgUrl = imgUrl2;
        } // if
        else
        {
            elem.className = style1;
        } // else

        // also toggle the image if necessary:
        if (imgId != null && imgId != "")
        {
            var imgObj = doc.getElementById (imgId);
            if (imgObj != null)
            {
                imgObj.src = imgUrl;
            } // if
        } // if
    } // if
} // toggleStyle


function showSearch (name, rootObjOid)
{
    top.setNoActiveTab ();
       
    showSearchFrame(false, true, true);
      
    callUrl (85, "&evt=101&sho=1&tabs=1&qucrname=" + name + "&curoid=" + top.oid + "&rootobjoid=" + rootObjOid, null, 'searchFrame');   
} // showQuery


function createLinkTabReference (referencedId, targetMajorId)
{
    callUrl (212, "&sho=1&tabs=1&oid=" + referencedId + "&calloid=" + targetMajorId, null, 'sheet');
} // createLinkTabReference


function newAndReference (containerId, targetMajorId)
{
    callUrl (214, "&sho=1&tabs=1&oid=" + targetMajorId + "&cid=" + containerId, null, 'sheet');
} // newAndReference


function load (fct, targetFrame)
{
    var target = top.sheet;
    var offlineUrl = null;              // url to be called in offline mode
    var name = "";

    if (targetFrame && targetFrame != null)
        target = targetFrame;

    if (fct == 5007)                // order?
    {
        offlineUrl = top.oid + "ordering";
    } // if order

    name = (target == top) ? "top" : target.name;

    callUrl (fct, "&oid=" + top.oid + "&cid=" + top.containerId, offlineUrl, name);
} // load


function loadTypeObj (fct, typeCode, targetFrame)
{
    var target = top.sheet;
    var offlineUrl = null;              // url to be called in offline mode
    var name = "";

    if (targetFrame && targetFrame != null)
        target = targetFrame;

    if (fct == 5007)                // order?
    {
        offlineUrl = typeCode + "ordering";
    } // if order

    name = (target == top) ? "top" : target.name;

    callUrl (fct, "&type=" + typeCode + "&cid=" + top.containerId, offlineUrl, name);
} // loadTypeObj


function loadEvent (fct, evt)
{
    var target = top.sheet;
    var name = (target == top) ? "top" : target.name;

    callUrl (fct, "&evt=" + evt + "&oid=" + top.oid + "&cid=" + top.containerId, null, name);
} // loadEvent


function loadEventArg (fct, evt, arg)
{
    var target = top.sheet;
    var name = (target == top) ? "top" : target.name;

    callUrl (fct, "&evt=" + evt + "&evtarg=" + arg + "&oid=" + top.oid + "&cid=" + top.containerId, null, name);
} // loadEventArg


function loadTabEvent (evt)
{
    var target = top.sheet;
    var name = (target == top) ? "top" : target.name;

    callUrl (255, "&evt=" + evt + "&oid=" + top.oid + "&cid=" + top.containerId + "&tab=" + top.tab, null, name);
} // loadEvent


function loadCont (fct, targetFrame)
{
    var target = top.sheet;

    if (targetFrame && targetFrame != null)
    {
        target = targetFrame;
    } // if

    callUrl (fct, "&cid=" + top.oid, null, target.name);
} // loadCont


/**
 * int computeTopOffset (Object obj)
 * Compute the top offset position of the object within the object's frame.
 * If obj is no valid object the value 0 is returned.
 */
function computeTopOffset (obj)
{
    if (obj == null)                    // no valid object
        return (0);                     // return default value

    if (top.system.browser.ns)          // netscape navigator?
    {
        return (obj.y);
    } // if netscape navigator
    else if (top.system.browser.ie || top.system.browser.firefox ||
             top.system.browser.opera)
    {
        if (obj.offsetParent != null)
            return (obj.offsetTop + computeTopOffset (obj.offsetParent));
        else
            return (obj.offsetTop);
    } // else if
} // computeTopOffset


/**
 * int computeLeftOffset (Object obj)
 * Compute the left offset position of the object within the object's frame.
 * If obj is no valid object the value 0 is returned.
 */
function computeLeftOffset (obj)
{
    if (obj == null)                    // no valid object
        return (0);                     // return default value

    if (top.system.browser.ns)          // netscape navigator?
    {
        return (obj.x);
    } // if netscape navigator
    else if (top.system.browser.ie || top.system.browser.firefox ||
             top.system.browser.opera)
    {
        if (obj.offsetParent != null)
            return (obj.offsetLeft + computeLeftOffset (obj.offsetParent));
        else
            return (obj.offsetLeft);
    } // else if
} // computeLeftOffset


/**
 * int computeWidth (Object obj)
 * Compute the width of the object within the object's frame.
 * If obj is no valid object the value 0 is returned.
 */
function computeWidth (obj)
{
    if (obj == null)                    // no valid object
        return (0);                     // return default value

    if (top.system.browser.ns)          // netscape navigator?
    {
        return (0);
    } // if netscape navigator
    else if (top.system.browser.ie || top.system.browser.firefox ||
             top.system.browser.opera)
    {
        return (obj.offsetWidth);
    } // else if
} // computeWidth


/**
 * int computeHeight (Object obj)
 * Compute the height of the object within the object's frame.
 * If obj is no valid object the value 0 is returned.
 */
function computeHeight (obj)
{
    if (obj == null)                    // no valid object
        return (0);                     // return default value

    if (top.system.browser.ns)          // netscape navigator?
    {
        return (10);
    } // if netscape navigator
    else if (top.system.browser.ie || top.system.browser.firefox ||
             top.system.browser.opera)
    {
        return (obj.offsetHeight);
    } // else if
} // computeHeight


/**
 * int computeFrameWidth (FrameObject frame)
 * Compute the visible width of the frame within the browser.
 * If frame is no valid frame the value 0 is returned.
 */
function computeFrameWidth (frame)
{
    if (frame == null)                  // no valid frame
        return (0);                     // return default value

    if (top.system.browser.ns || top.system.browser.firefox ||
        top.system.browser.opera)
    {
        return (frame.innerWidth);
    } // if
    else if (top.system.browser.ie)
    {
        return (frame.document.body.clientWidth);
    } // else if
} // computeFrameWidth


/**
 * int computeFrameHeight (FrameObject frame)
 * Compute the visible height of the frame within the browser.
 * If frame is no valid frame the value 0 is returned.
 */
function computeFrameHeight (frame)
{
    if (frame == null)                  // no valid frame
        return (0);                     // return default value

    if (top.system.browser.ns || top.system.browser.firefox ||
        top.system.browser.opera)
    {
        return (frame.innerHeight);
    } // if
    else if (top.system.browser.ie)
    {
        return (frame.document.body.clientHeight);
    } // else if
} // computeFrameHeight


/**
 * int computeFramePosX (FrameObject frame)
 * Compute the current horizontal position of the document within the frame.
 * If frame is no valid frame the value 0 is returned.
 */
function computeFramePosX (frame)
{
    if (frame == null)                  // no valid frame
        return (0);                     // return default value

    if (top.system.browser.ns || top.system.browser.firefox ||
        top.system.browser.opera)
    {
        return (frame.pageXOffset);
    } // if
    else if (top.system.browser.ie)     // internet explorer?
    {
        return (frame.document.body.scrollLeft);
    } // else if internet explorer
} // computeFramePosX


/**
 * int computeFramePosY (FrameObject frame)
 * Compute the current vertical position of the document within the frame.
 * If frame is no valid frame the value 0 is returned.
 */
function computeFramePosY (frame)
{
    if (frame == null)                  // no valid frame
        return (0);                     // return default value

    if (top.system.browser.ns || top.system.browser.firefox ||
        top.system.browser.opera)
    {
        return (frame.pageYOffset);
    } // if
    else if (top.system.browser.ie)     // internet explorer?
    {
        return (frame.document.body.scrollTop);
    } // else if internet explorer
} // computeFramePosY

/**
 * int computeYScrollOffset (FrameObject frame)
 * Compute the current horizontal scroll offset within the given frame.
 * If frame is no valid frame the value 0 is returned.
 */
function computeYScrollOffset (frame)
{
    if (frame == null)                  // no valid frame
        return (0);                     // return default value

    // if <ie7
    if (top.system.browser.ie && top.system.browser.version < 7)
    {
        return (0);
    } // if
    else
    {
        return (frame.document.documentElement.scrollTop||frame.document.body.scrollTop);
    } // else
} // computeYScrollOffset

/**
 * void notInOff ()
 * Display a message that something is not available in the offline version.
 */
function notInOff ()
{
    alert (top.multilang.ibs_ibsbase_scripts_MSG_notAvailOffline);
} // notInOff


/**
 * boolean checkOnline (boolean showMessage)
 * Check whether the application is running online.
 * If it is not online and showMessage is true a message is displayed.
 * If the application is online <CODE>true</CODE> is returned,
 * otherwise <CODE>false</CODE>.
 */
function checkOnline (showMessage)
{
    return top.system.checkOnline (showMessage);
} // checkOnline


/**
 * void showStylesheet (DocumentObject doc, String stylesheet)
 * Include a stylesheet within a document.
 * The stylesheet must be in the browser specific stylesheet directory.
 */
function showStylesheet (doc, stylesheet)
{
    if (doc && doc != null &&
        stylesheet && stylesheet != null && stylesheet != "")
                                        // document and stylesheet exist?
    {
        var url = top.system.browserDir + stylesheet; // the stylesheet url

/* BT 20101110:
   IBS-590 Import url within scriptCommon.showStylesheet not properly rewritten when using a portal
        if (top.system.browser.ns)
        {
            doc.write (
                '<LINK REL=StyleSheet TYPE="text/css" HREF="' + url + '">');
        } // if
        else
        {
            doc.write ('<STYLE TYPE="text/css"> \n' +
                       '@import url(' + url + '); \n' +
                       '</STYLE>\n');
        } // else
*/
        
        doc.write (
                '<LINK REL=StyleSheet TYPE="text/css" HREF="' + url + '">');
    } // if document and stylesheet exist
} // showStylesheet

/**
 * void startExport (String oid, String targetDir, String fileName,
 *                   String translator, boolean displayLog, boolean writeLog,
 *                   boolean appendLog, String logFileName, String logFilePath)
 * Export object with given oid.
 * The stylesheet must be in the browser specific stylesheet directory.
 */
function startExport (oid, targetDir, fileName, translator,
    displayLog, writeLog, appendLog, logFileName, logFilePath)
{
    var urlstr;
    urlstr = top.getBaseUrl () +
        "&fct=398" +
        "&exp=" + escape (targetDir) +
        "&exf=" + escape (fileName) +
        "&exsf=true" +
        "&trn=" + translator +
        "&dlf=" + displayLog +
        "&wlf=" + writeLog +
        "&alf=" + appendLog +
        "&lfn=" + escape (logFileName) +
        "&lfp=" + escape (logFilePath) +
        "&oid=" + oid;

    callUrl (urlstr, null, null, 'sheet');
} // startExport


/**
 * String getEscapedObjectRef (String incompleteUrl, String objectRef,
 *                      String objectRef_M, String objectRefName)
 * Get escaped object reference, i.e. build the corresponding url.
 */
function getEscapedObjectRef
    (incompleteUrl, objectRef, objectRef_M, objectRefName)
{
    if (objectRefName != null)
    {
        v_refFieldName = objectRefName;
    } // if

    // replace all occurencies of "&amp;" with "&":
    var completeUrl = incompleteUrl.replace (/\&\a\m\p\;/g, "&");

    // append the other parameters:
    completeUrl += '&' + objectRefName + '=' + encodeURI (objectRef);
    completeUrl += '&' + objectRefName + '_M=' + objectRef_M;

    // return the result:
    return completeUrl;
} // getEscapedObjectRef


/**
 * saveFile (file)
 * Opens a file in save mode.
 */
function saveFile (file)
{
    var target = top.temp;
    var url = top.system.getFileAccessUrl (file) + '&save=true';

//alert (navigator.appName);
//alert (navigator.appVersion);

    if (top.system.browser.ie || top.system.browser.firefox ||
        top.system.browser.opera)
    {
        var doc = target.document;
        doc.open ();
        doc.write (
            getHtmlPageStart () +
            '<HEAD></HEAD><BODY>' +
            '<IFRAME type=\"hidden\" src=\"' + url + '\"' +
            ' style=\"display: none;\" name=\"navframe\"></IFRAME>' +
            '</BODY></HTML>');
        doc.close ();
    } // if
    else
    {
        target.document.location.href = url;
    } // else
} // saveFile


/**
 * void setFieldFocus (FormObject form[, String fieldName])
 * Put the focus on one specific field of a form.
 * If there is no fieldName defined the first visible field of the form gets
 * the focus.
 */
function setFieldFocus (form, fieldName)
{
    var field;                          // the field
    var found = false;                  // was the field found?

    // check the field name:
    if (top.isValidString (fieldName))  // field name was defined?
    {
        // get the field out of the form:
        field = eval ("top." + form.document.parentWindow.name + "." + form.name + "." + fieldName);

        if (top.exists (field))         // the field exists?
        {
            found = true;
        } // if the field exists
    } // if field name was defined

    // check if the field was found:
    if (!found && form != null)         // no field found thus far?
    {
        // get the first field:
        // loop through all fields of the form and find the first non-hidden
        // one:
        for (var i = 0; i < form.length && !found; i++)
        {
            // get the actual field:
            field = form[i];

            // check if the field is shown to the user:
            if (field.type != "hidden")
            {
                found = true;
            } // if
        } // for i
    } // if no field found thus far

    // check if the field was found:
    if (found)                          // found the field?
    {
        // put the focus on the field:
        field.focus ();
    } // if found the field
    else                                // field not found
    {
        // nothing to do
    } // else field not found
} // setFieldFocus


/**
 * Event getEvent (Event ev, Object obj, Event ev2)
 * Get event object. This function is browser specific.
 * The event ev2 can be used if the IE has problems with the global variable
 * "event". In that case the parameter ev2 can be computed with the following
 * code (where "ev" is the first parameter of the event handler):
 *   var ev2 = ev;
 *   // get event:
 *   if (top.system.browser.ie)
 *   {
 *       ev2 = event;
 *   } // if
 *   ev = top.scripts.getEvent (ev, this, ev2);
 *   if (ev == null) return true;
 *
 */
function getEvent (ev, obj, ev2)
{
    var evt = null;

    if (top.system.browser.ie)
    {
        if (event != null)
        {
            evt = event;
        } // if
        else if (obj != null && obj.event != null)
        {
            evt = obj.event;
        } // else if
        else if (obj != null && obj.parentWindow != null &&
                 obj.parentWindow.event != null)
        {
            evt = obj.parentWindow.event;
        } // else if
        else if (ev2 != null)
        {
            evt = ev2;
        } // else if

        if (evt != null)
        {
            evt.eventObj = evt.srcElement;
        } // if
    } // if
    else if (top.system.browser.firefox || top.system.browser.ns ||
             top.system.browser.opera)
    {
        evt = ev;

        evt.eventObj = evt.target;
    } // else if

    return evt;
} // getEvent


/**
 * FramesetObject getFrameset (FrameObject frame)
 * Get the frameset for a specific frame.
 * If the frame is top (i.e. no frame) null is returned.
 */
function getFrameset (frame)
{
    var retVal = null;                  // the found frameset
    var framesetList = null;            // list of framesets
    var frames = null;                  // list of frames

    if (frame != null)
    {
        framesetList = frame.parent.document.getElementsByTagName ("frameset");

        // loop through all framesets and try to find out which is the parent
        // of the actual frame:
        for (var i = 0; retVal == null && i < framesetList.length; i++)
        {
            // get the frames within the frameset:
            frames = framesetList[i].childNodes;
            // loop through the frames and search for the required one:
            for (var j = 0; retVal == null && j < frames.length; j++)
            {
                // check if we found the frame:
                if (frames[j].nodeName.toLowerCase () == "frame" &&
                    (frames[j] == frame || frames[j].name == frame.name))
                {
                    // set the frameset as result value:
                    retVal = framesetList[i];
                } // if
            } // for j
        } // for i
    } // if

    // return the result:
    return retVal;
} // getFrameset


/**
 * void resizeFramesRec (int framesetVariable, String framesetExt, int size,
 *                       int targetSize, String pattern, float percentage)
 * Set the size of a frame within a frameset. This function performs an
 * animation by incrementally changing the size upto (or downto) the targetSize.
 * The frameset must have a valid id set.
 * Because the frameset contains several frames the pattern defines the sizes
 * of all frames, e.g. "*,<size>". <size> is the value which is replaced by
 * the actual size.
 * The frameset has the following syntax: "top.<framesetId>".
 * The framesetExt has the following possible values: "rows" or "cols".
 * The percentage is used for the speed of the animation
 */
function resizeFramesRec (framesetVariable, framesetExt, size, targetSize,
                          pattern, percentage)
{
    // notify that the frame animation is in progress:
    v_isFrameAnimationInProgress = true;

    // compute difference to next size:
    var diff = Math.floor ((targetSize - size) * percentage);

/*
alert (diff + " - " + pattern + " - " + framesetVariable + " - " +
       eval ("v_framesets[framesetVariable]." + framesetExt));
*/
    // set the actual (start) size:
    eval ("v_framesets[framesetVariable]." + framesetExt +
          " = pattern.replace (/<size>/, \"\" + size)");

    if (diff != 0)
    {
        resizeTimer = setTimeout (
            "resizeFramesRec (" + framesetVariable + ", " +
            "\"" + framesetExt + "\", " +
            (size + diff) + ", " + targetSize + ", " +
            "\"" + pattern + "\", " + percentage + ")",
            1);
        v_framesets[framesetVariable].v_resizeTimer = resizeTimer;
    } // if
    else
    {
        // get the frames within the frameset:
        var frames = v_framesets[framesetVariable].childNodes;

        v_framesets[framesetVariable] = null;

        // loop through the frames and search for the required one:
        for (var j = 0; j < frames.length; j++)
        {
            // call callback function after resizing if available in
            // sheet1 or sheet2
            if ( top.sheet &&  eval("top.sheet." + frames[j].name) &&
                 eval("top.sheet." + frames[j].name).evtResizingDone )
            {
                    eval("top.sheet." + frames[j].name).evtResizingDone ();
            } // if

            // reset the global animation progress variable
            v_isFrameAnimationInProgress = false;
        } // for j
    } // else
} // resizeFramesRec


/**
 * void resizeFrames (FrameObject frame, String framesetId, int size,
 *                    int targetSize, String pattern, boolean withAnimation)
 * Set the height of one or more frames within a frameset. This function
 * performs an animation by incrementally changing the size upto or downto the
 * targetSize.
 * The frameset must have a valid id set.
 * Because the frameset contains several frames the pattern defines the sizes
 * of all frames, e.g. "*,<size>". <size> is the value which is replaced by
 * the actual size.
 */
function resizeFrames (frame, framesetId, framesetExt, size, targetSize,
                       pattern, withAnimation)
{
    var percentage = withAnimation ? (top.system.browser.ie9 ? 1/60 : 1/15) : 1;
    var frameset = null;
    var resizeTimer = 0;
    var i = 0;
    var framesetVariable = null;

    // check if the frameset id was defined:
    if (framesetId != null)
    {
        frameset = eval ("top." + framesetId);
    } // if
    else
    {
        // try to find the frameset through the parent document of the frame:
        frameset = getFrameset (frame);
    } // else

    // check if we got a frameset:
    if (frameset != null)
    {
        // create name for frameset variable:
        for (i = 0; i < v_framesets.length && v_framesets[i] != null; i++);

        // set frameset variable:
        v_framesets[i] = frameset;

        // ensure that there is no old animation running:
        if (frameset.v_resizeTimer != null)
        {
            resizeTimer = frameset.v_resizeTimer;

            if (resizeTimer != null)
            {
                clearTimeout (resizeTimer);
            } // if
        } // if

        // call recursive function:
        resizeFramesRec (i, framesetExt, size, targetSize, pattern, percentage);
    } // if
} // resizeFrames


/**
 * void showSingleFrame (WindowObject win, String frameName,
 *                      int sizeOthers)
 * Show only one frame out of a frameset which is in a specific window.
 * The other frames shall be resized to a specific size. If no size is defined
 * they are hidden (size = 0).
 */
function showSingleFrame (win, frameName, sizeOthers)
{
    var frame = eval ("win." + frameName);

    if (isNaN (sizeOthers) || sizeOthers < 0)
    {
        sizeOthers = 0;
    }  // if

    // check if a valid frame is defined:
    if (frame != null)
    {
        // get the frameset for the frame:
        var frameset = getFrameset (frame);
        var framesetExt = "cols";
        var pattern = "";
        var sep = "";
        var targetSize = 0;
        // get the frames within the frameset:
        var frames = frameset.childNodes;

        if (frameset.rows != null && frameset.rows != "")
        {
            framesetExt = "rows";
            // compute actual height:
            targetSize = computeHeight (win.document.body);
        } // if
        else
        {
            framesetExt = "cols";
            // compute actual width:
            targetSize = computeWidth (win.document.body);
        } // else

        // ensure that the frame is always displayed:
        if (targetSize <= 0 && sizeOthers == 0)
        {
            targetSize = 1;
        } // if

        // loop through the frames and search for the required one:
        for (var i = 0; i < frames.length; i++)
        {
            pattern += sep;

            // check if we found the frame:
            if (frames[i].nodeName.toLowerCase () == "frame" &&
                (frames[i] == frame || frames[i].name == frame.name))
            {
                // set the frameset as result value:
                pattern += "<size>";
            } // if
            else
            {
                pattern += "" + sizeOthers;
            } // else

            sep = ",";
        } // for j

//alert (pattern + "\n" + targetSize);
        // call common function:
        resizeFrames (frame, null, framesetExt, targetSize, targetSize, pattern,
                      false);
    } // if
} // showSingleFrame


/**
 * void setFrameHeight (FrameObject frame, String framesetId, int targetHeight,
 *                      String pattern, boolean withAnimation)
 * Set the height of a frame within a frameset. This function performs an
 * animation by incrementally changing the height upto the targetHeight.
 * The frameset must have a valid id set.
 * Because the frameset contains several frames the pattern defines the sizes
 * of all frames, e.g. "*,<size>". <size> is the value which is replaced by
 * the actual size.
 */
function setFrameHeight (frame, framesetId, targetHeight, pattern,
                         withAnimation)
{
    // check if a valid frame is defined:
    if (frame != null)
    {
        // compute actual height:
        var height = computeHeight (frame.document.body);

        // call common function:
        resizeFrames (frame, framesetId, "rows", height, targetHeight, pattern,
                      withAnimation);
    } // if
} // setFrameHeight


/**
 * void activateNavigation ()
 * Activate the navigation frame.
 */
function activateNavigation ()
{
    var frame = top.navigation;
    var height = 0;
    var frameset = getFrameset (frame);
    var frameHeight = 0;
    var targetHeight = 0;
    var countFrames = 0;
    var pattern = "";
    var patternSep = "";

    // check if the values are valid:
    if (height <= 0 && frame != null && frameset != null && frameset.id != null)
    {
        // compute actual height:
        height = computeHeight (frame.document.body);

        // compute the targetHeight:
        targetHeight = computeHeight (frameset);

        // compute the pattern:
        var frameSizes = frameset.rows.split (",");
        var frames = frameset.childNodes;

        for (var i = 0; i < frames.length; i++)
        {
            if (frames[i].nodeName.toLowerCase () == "frame")
            {
                countFrames++;
/*
                // add the frame height to the sum:
                frameHeight = computeHeight (frames[i].document.body);
                targetHeight += frameHeight;
alert (frames[i].name + ": " + frameHeight + " - " + targetHeight);
*/

                // add pattern separator:
                pattern += patternSep;
                patternSep = ",";

                // add the frame to the frame pattern:
                if (frames[i] == frame || frames[i].name == frame.name)
                {
                    pattern += "*";
                } // if
                else
                {
                    pattern += "<size>";
                } // else
            } // if
        } // for i

        // compute actual height for each frame:
        height = Math.ceil (targetHeight / (countFrames - 1));
        // resize the frames:
        resizeFrames (frame, frameset.id, "rows", height, 0, pattern,
                      true);
    } // if
} // activateNavigation


/**
 * Timer startTimer (Object obj, Function fct, int timeout)
 * Start a timer which shall execute a function on an object.
 */
function startTimer (obj, fct, timeout)
{
    var otherArgs = $A2 (arguments, 3);
    var timerIndex = 0;

    // store the object:
    if (top.timers == null)
    {
        top.timers = new Array ();
    } // if

    timerIndex = top.timers.length;
    top.timers[timerIndex] = new Array (obj, fct, otherArgs);

    // start the timer:
    return setTimeout (function () {execTimer (timerIndex)}, timeout);
} // startTimer


/**
 * void execTimer (int index)
 * Execute a specific timer.
 */
function execTimer (index)
{
    var timerData = top.timers[index];
    var obj = timerData[0];
    var fct = timerData[1];
    var args = timerData[2];

    // execute the function on the object:
    fct.apply (obj, args);

    // drop timer data:
    delete top.timers[index];
} // execTimer


/**
 * String getHtmlPageStart ()
 * Create a string which can be used as the start part for an HTML page.
 */
function getHtmlPageStart ()
{
    // HTML 3.1 DTD
/*
    return '<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">';
*/
    // HTML 4.01 - Strict
    return '<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"' +
           ' "http://www.w3.org/TR/html4/strict.dtd">' +
           '<HTML>';

} // getHtmlPageStart

/**
 * void resizeTextArea ()
 * Dynamically resize the heigth of an textarea depending on the content.
 */
function dynResizeTextArea (textAreaElem)
{
    if (textAreaElem)
    {
    	var a = textAreaElem.value.split('\n');
    	var b = 1;

    	for (var x = 0; x < a.length; x++)
        {
    	   if (a[x].length >= textAreaElem.cols)
    	   {
                b+= Math.floor (a[x].length / textAreaElem.cols);
           } // if
    	} // for
    	b += a.length;
    	if (b != textAreaElem.rows)
    	{
            textAreaElem.rows = b;
        } // if
    } // if
} // dynResizeTextArea

/**
 * void resizeTextArea ()
 * Resize an textarea with a given row increment and col increment
 * The minRows and minCols set the minimal borders for the values.
 */
function resizeTextArea (textAreaElem, rowInc, colInc, minRows, minCols)
{
    if (textAreaElem)
    {
        if (textAreaElem.rows + rowInc >= minRows)
        {
            textAreaElem.rows = textAreaElem.rows + rowInc;
        } // if
        if (textAreaElem.cols + colInc >= minCols)
        {
            textAreaElem.cols = textAreaElem.cols + colInc;
        } // if
    } // if
} // resizeTextArea

/**
 * void showSearchFrame ()
 * Shows the search form while hiding the sheet form.
 */
function showSearchFrame(animate, clearOldSheetContent, clearOldSearchContent, clearButtonBar)
{
	// Clear the sheettabs frame
    clearFrame(top.sheettabs);
    
    if(clearButtonBar)
    {
    	clearFrame(top.buttons);
    } // if clearButtonBar
    
    if(clearOldSheetContent)
    {
    	clearFrame(top.sheet);
    } // if clearOldSheetContent
    
    if(clearOldSearchContent)
    {
   	   	// Clear the search frame
	    clearFrame(top.searchFrame);
    } // if clearOldSearchContent

	if(animate==false)
	{
		frameset = getFrameset (top.sheet);
		
		// set the height of the sheet frame to 0
		var pattern = v_showSearchFramePattern.replace (/<size>/, "0");
		frameset.rows = pattern;
	} // if !animate
	else
	{
		setFrameHeight (top.sheet, null, 0, v_showSearchFramePattern, animate);
	} // else animate
} // showSearchFrame

/**
 * void hideSearchFrame ()
 * Hides the search form while showing the sheet form.
 */
function hideSearchFrame(animate)
{
	if(animate==false)
	{
		frameset = getFrameset (top.sheet);

		// set the height of the search frame to 0
		var pattern = v_hideSearchFramePattern.replace (/<size>/, "0");
		frameset.rows = pattern;
	} // if
	else
	{
		setFrameHeight (top.sheet, null, 0, v_hideSearchFramePattern, false);
	} // else
} // hideSearchFrame

/**
 * void clearFrame ()
 * Clears the given frame.
 */
function clearFrame(frame)
{
	var doc = frame.document;
	
    doc.close ();
    doc.open ();
    doc.close ();
} // clearFrame

/**
 * void createHistory ()
 * Renders the necessary elements for displaying the history list.
 * This method is empty and has to be overridden by specific implementations.
 */
function createHistory (list, actIndex)
{
	/*
    if (list !== undefined && list !== null)
    {
        if (actIndex === undefined || actIndex === null)
        {
            actIndex = 0;
        } // if

        var histElem = createHistoryElement ();

		// add the content
        var content = ...;

        histElem.innerHTML = content;
    } // if
    */
} // createHistory

/**
 * element getHistoryElement ()
 * Creates the history element, which can then contain the history list if not already existing.
 * 
 * @return the history element
 */
function getHistoryElement ()
{
    var frame = getHistoryFrame();
    
    //alert(frame.name);
    
    var doc = frame.document;
    
    var ID = "historyElem";
    var elem = doc.getElementById (ID);
    if (elem === undefined || elem === null)
    {  	
    	var elem = doc.createElement ("div");
        elem.id = ID;
        elem.className = "historyClosed";
        
        //var body = doc.getElementsByTagName ("body")[0];
        var body = doc.body;
        
        // IBS-170 Historyfehler bei Ausgabe von Log Messages:
        //body.appendChild (elem);
        // ... is not used since problems occure if the page has two documents like it is the
        // case if a log message is rendered.
        // Instead the following statement is used:
        body.insertBefore(elem, body.lastChild);
        // An alternative would also be to use:
        //body.innerHTML+="<div id='historyElem' class='historyClosed'/>";
        
    	if(frame == top.sheet.sheet2)
    	{    	
    		elem.innerHTML = top.sheet.sheet1.document.getElementById (ID).innerHTML;
    	} // frame == top.sheet.sheet2
    	else if(frame == top.searchFrame.sheet2)
    	{
    		elem.innerHTML = top.searchFrame.sheet1.document.getElementById (ID).innerHTML;    		
    	} // frame == top.searchFrame.sheet2
    } // if
    
    return elem;
} // getHistoryElement

/**
 * void actualizeSearchFrameHistory ()
 * 
 * Actualizes the history within the search frame by replacing it with the one from the sheet frame.
 * This method can be used, when back to search is used and the history list within the cached frame
 * is obsolete.
 */
function actualizeSearchFrameHistory ()
{
    var ID = "historyElem";
	
	var frame = top.sheet;
	
	// check if top.sheet has subframes
	if(top.sheet.sheet1 != null)
	{
    	frame = top.sheet.sheet1;
	} // top.sheet has subframes

	var sheetDiv = frame.document.getElementById (ID);
	
	if(sheetDiv == null)
	{
		return;
	} // sheetDiv == null
		
	var searchFrame = top.searchFrame.sheet1; 	
	if(searchFrame != null)
	{
		var elem = searchFrame.document.getElementById (ID);
		
		if(elem != null)
		{
			elem.innerHTML = sheetDiv.innerHTML;
		} // elem != null
	} // searchFrame != null
	
	searchFrame = top.searchFrame.sheet2; 	
	if(searchFrame != null)
	{
		var elem = searchFrame.document.getElementById (ID);
		
		if(elem != null)
		{
			elem.innerHTML = sheetDiv.innerHTML;
		} // elem != null
	} // searchFrame != null
} // actualizeSearchFrameHistory

/**
 * frame getHistoryFrame()
 * 
 * This method returns the frame, where the history div should be placed in.
 * 
 * This method places the history in general into sheet frame if this on top and into the search frame
 * if that one is on top.
 * 
 * Further more it renders the history div into the sheet1 of sheet and search frame if the search result
 * is currently closed and otherwise inot sheet2. This is handling is desired if the button bar is at the
 * bottom. If the button at the top of the sheet frame within the frameset this method can be overridden
 * within scriptSpecific, like it is done for the standard layout.  
 * 
 * @return
 */
function getHistoryFrame()
{
	// check if the search frame is active
    if(computeFrameHeight(top.sheet) == 0)
    {
    	frameset = getFrameset (top.searchFrame.sheet1);

    	if(frameset == null)
    	{
    		return top.searchFrame;
    	}
    	else
    	{
	    	// HACK: Check if sheet1 or sheet2 is open
    		if(frameset.rows == "*,0" || frameset.rows == "*,1")
	    	{
	    		return top.searchFrame.sheet1;
	    	} // if sheet1 openend
	    	else
	    	{
	    		return top.searchFrame.sheet2;
	    	} // else sheet2 openend
    	}
    } // if search frame is active
    else // sheet frame is active 
    {  	
    	// check if top.sheet has subframes
    	if(top.sheet.sheet1 != null)
    	{
        	frameset = getFrameset (top.sheet.sheet1);

        	// HACK: Check if sheet1 or sheet2 is open
        	if(frameset.rows == "*,0" || frameset.rows == "*,1")
        	{
        		return top.sheet.sheet1;
        	} // if sheet1 opened
        	else
        	{
        		return top.sheet.sheet2;
        	} // else sheet2 opened
    	} // top.sheet has subframes
    	else
    	{
    		return top.sheet;
    	} // no subframes
    } // else frame is active
} // getHistoryFrame

/**
 * boolean toggleHistory ()
 * Toggles the history.
 * 
 * @return if the history is open after the method has been called 
 */
function toggleHistory ()
{
    // allow toggling history only when no animation is in progress
    if(v_isFrameAnimationInProgress)
    {
        return false;
    } // animation in progress
    
    var elem = getHistoryElement ();

    var actTime = new Date();

    // is the history already shown
    if (elem.className == "historyOpen" || (actTime - _lastHideHistory.getTime ()) <= 200)                                                  
    {
        hideHistory ();
        return false;
    } // if
    else
    {   
        showUpHistory ();
        return true;
    } // else
} // toggleHistory

/**
 * void showUpHistory ()
 * Shows the history list.
 */
function showUpHistory ()
{
    // retrieve the history element
    var elem = getHistoryElement ();
    
    // show the history element
    elem.className = "historyOpen";

    // set the horizontal position for the history element
    setHistoryOffset (elem);

    // disable the history on scroll
    if (top.system.browser.ie)
    {
        getHistoryFrame().attachEvent ('onscroll', top.scripts.handleSetHistoryOffset);
    } // if
    else
    {
        getHistoryFrame().addEventListener ('scroll', top.scripts.handleSetHistoryOffset, false);
    } // else
} // showHistory

/**
 * void handleSetHistoryOffset ()
 * Handles the event for setting the history element's offset.
 */
function handleSetHistoryOffset ()
{
    // retrieve the history element
    var elem = getHistoryElement ();

    // set the horizontal position for the history element
    setHistoryOffset (elem);
} // handleSetHistoryOffset

/**
 * void setHistoryOffset (historyElem)
 * Sets the history element's offset.
 *
 * For specific layouts with button bar at the bottom this method has to be overwritten.
 */
function setHistoryOffset (historyElem)
{
    // set the horizontal position for the history element
    var offset = computeYScrollOffset (getHistoryFrame());

    historyElem.style.top = new String (offset + "px");
} // setHistoryOffset

/**
 * void hideHistory ()
 * Hides the history list.
 */
function hideHistory (paramElem)
{
    // remove the event listener for hiding the history
    if (top.system.browser.ie)
    {
        getHistoryFrame().detachEvent ('onscroll', top.scripts.handleSetHistoryOffset);
    } // if
    else
    {
        getHistoryFrame().removeEventListener ('scroll', top.scripts.handleSetHistoryOffset, false);
    } // else

    var elem = paramElem;
    
    if(elem == null || elem.id != "historyElem")
    {
        // retrieve the history element
        elem = getHistoryElement ();
    }
    
    if (elem.className == "historyOpen")
    {
        // hide the history element
        elem.className = "historyClosed";
    
        _lastHideHistory = new Date();
    } // if
} // hideHistory

// inform the top-level scripts that the scripts are loaded:
//top.setScriptsLoaded ();

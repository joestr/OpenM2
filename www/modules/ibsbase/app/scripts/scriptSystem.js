/******************************************************************************
 * This file contains all JavaScript code regarding the system information.
 *
 * @version     2.23.0001, 08.03.2002
 *
 * @author      Klaus Reimüller (KR)  020308
 ******************************************************************************
 */

//============= necessary classes and variables ===============================
// class Browser


//============= declarations ==================================================

// constants:
var FILE_ACCESS = "#CONFVAR.ibsbase.fileServlet#?file="; // url for file access
var PATH_ALL_BROWSERS = "css";

// variables:
var browser = null;                     // the browser signature (ex. "IE40")
var system = null;                      // the actual System object


//============= class System ==================================================

/**
 * void System_setBrowserDir ()
 * Compute and store the browser directory path.
 */
function System_setBrowserDir ()
{
    // set the layout directory for this browser:
//    this.browserDir = this.layoutDir + this.browser.typeVersion + '/';
    this.browserDir = this.layoutDir + PATH_ALL_BROWSERS + '/';

    // this is just here for backward compatibility:
    browser = this.browser.typeVersion;
} // System_setBrowserDir


/**
 * void System_setLayoutDir ()
 * Compute and store the layout directory path.
 */
function System_setLayoutDir ()
{
    this.layoutDir = this.baseDir + "layouts/" + this.layout + "/";
    // the browserDir is recalculated because layoutDir may have been changed:
    this.setBrowserDir ();
} // System_setLayoutDir


/**
 * void System_setBaseDir ([String baseDir])
 * Set the base directory and some derived properties within the System object.
 */
function System_setBaseDir (baseDir)
{
    var url = this.win.location.href;   // the complete url

    // set the base directory:
    if (!baseDir || baseDir == null || baseDir == "") // no base dir defined?
    {
        // baseDir is the whole url until inclusive 'app/':
        baseDir = url.substring (0, url.lastIndexOf ('app/') + 4);
    } // if no base dir defined

    // set the base directory:
    this.baseDir = baseDir;

    // set the application directory:
    this.appDir = this.baseDir.substring (0, this.baseDir.lastIndexOf ('app/') - 1);
    this.appDir = this.appDir.substring (this.appDir.lastIndexOf ('/')) + '/';

    // set the offline directory:
    this.offlineDir = this.baseDir + top.OFFLINE_DATA;

    // the directory where to find all layout specifics:
    this.setLayoutDir ();
    // the directory where to find all include files:
    this.includeDir = this.baseDir + 'include/';
} // System_setBaseDir


/**
 * void System_setFileAccessUrl ()
 * Store the url for the file access. Note that
  the appDir must be calculated first
 */
function System_setFileAccessUrl ()
{
    this.fileAccessUrl = this.appDir + FILE_ACCESS;
} // System_setFileAccessUrl


/**
 * URL System_getBaseUrl ()
 * Get the base url and some derived properties within the System object.
 */
function System_getBaseUrl ()
{
    var value = '';
    var dValue = Math.random () * 100000;
    value += Math.floor (dValue);
    var url = this.appDir + this.appName + '?r=' + value;

//    if (top.sheet && top.sheet.count > 0) // frame sheet exists and has frameset?
    //    url += "&frs=true";
    return (url);
} // System_getBaseUrl


/**
 * void System_setLayout (String layoutName)
 * Set the actual layout and some derived properties.
 */
function System_setLayout (layoutName)
{
    var url = this.win.location.href;
    if (!layoutName || layoutName == null || layoutName == "")
                                        // no layout defined?
    {
        // get the layout out of the url:
        // The layout name is the directory name within the layouts directory.
        if ((pos = url.lastIndexOf ('layouts/')) >= 0)
                                        // layouts directory found?
        {
            // set name and sub path of the layout:
            layoutName = url.substring (pos + 8);

            if ((pos = layoutName.indexOf ("/")) > 0)
            {
                // set the real layout name:
                layoutName = layoutName.substring (0, pos);
            } // if
        } // if layouts directory found
    } // if no layout defined

    this.layout = layoutName;
    // compute the new layout directory:
    this.setLayoutDir ();
} // System_setLayout


/**
 * URL System_getBaseDir ()
 * Get the base directory from System.
 */
function System_getBaseDir ()
{
    return this.baseDir;
} // System_getBaseDir


/**
 * URL System_getOfflineDir ()
 * Get the offline directory from System.
 */
function System_getOfflineDir ()
{
    return this.offlineDir;
} // System_getOfflineDir


/**
 * URL System_getFileAccessUrl (String file, boolean encodeUri)
 * Get the file access url to a specific file defined through its file path 
 * and name. If encodeUri is set to true the filename is encoded as Uri.
 */
function System_getFileAccessUrl (file, encodeUri)
{
    // Depending on encodeUri either encode the file name or only escape it.
    var url = (encodeUri == true) ? encodeURI (file) : escape (file);

    return this.fileAccessUrl + url;
} // System_getFileAccessUrl


/**
 * boolean System_checkOnline (boolean showMessage)
 * Check whether the application is running online.
 * If it is not online and showMessage is true a message is displayed.
 * If the application is online <CODE>true</CODE> is returned,
 * otherwise <CODE>false</CODE>.
 */
function System_checkOnline (showMessage)
{
    // check if we are in the online version:
    if (this.isOnline)                  // currently online?
    {
        return true;                    // return the result
    } // if currently online
    else                                // currently offline
    {
        if (showMessage)                // display message?
        {
            notInOff ();                // show message
        } // if display message

        return false;                   // return the result
    } // else currently offline
} // System_checkOnline


/**
 * System System (String appName, WindowObject win, boolean isOnline)
 * Constructor of System.
 */
function System (appName, win, isOnline)
{
    // set methods:
    this.setBrowserDir = System_setBrowserDir; // set the browser directory
    this.setLayoutDir = System_setLayoutDir; // set the layout directory
    this.setBaseDir = System_setBaseDir; // set the base directory
    this.setLayout = System_setLayout;  // set the layout
    this.setFileAccessUrl = System_setFileAccessUrl; // set the file access url
    this.getBaseDir = System_getBaseDir; // get the base directory
    this.getBaseUrl = System_getBaseUrl; // get the base url
    this.getOfflineDir = System_getOfflineDir; // get the offline directory
    this.getFileAccessUrl = System_getFileAccessUrl; // get the file access url
    this.checkOnline = System_checkOnline; // is the application online?

    // set initial values:
    this.isOnline = isOnline;
    this.win = win;
    this.appDir = "/m2/";
    this.baseDir = this.appDir + "app/";
    this.layout = "unknown";
    this.layoutDir = "";
    this.browserDir = "";
    this.includeDir = "";
    this.fileAccessUrl = "";
    this.browser = new Browser ();
    this.appName = (appName != null ? appName : 'm2ApplicationServlet');
    this.offlineDir = this.baseDir + "offlinedata/";

    // call some initialisations:
    this.setBaseDir ();
    this.setLayout ();
    this.setFileAccessUrl ();
} // System


//============= common functions ==============================================

/**
 * String getBaseUrl ()
 * Get the base url of the system.
 */
function getBaseUrl ()
{
    if (top.system != null)
    {
        return top.system.getBaseUrl ();
    } // if
    else
    {
        return "";
    } // else
} // getBaseUrl


/**
 * void initSystem (String appName, boolean isOnline)
 * Initialize the system object, which is holding all necessary system
 * information.
 */
function initSystem (appName, isOnline)
{
    // create the system object and store it:
    system = new System (appName, top, isOnline);
} // initSystem


/**
 * boolean exists (Object obj)
 * Check if a specific object exists
 */
function exists (obj)
{
    // check if the object exists and return the result:
    if (top.system.browser.ns6 || top.system.browser.ie6)
    {
        return (obj != undefined);
    } // if
    else
    {
        return (("" + obj) != "undefined");
    } // else
} // exists


/**
 * boolean isValidString ([String str])
 * Check if the parameter is a valid string, i.e. it must exist and not be null
 * or empty.
 */
function isValidString (str)
{
    return (top.exists (str) && str != null && str != "");
} // isValidString

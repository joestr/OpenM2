/******************************************************************************
 * Browser functions. <BR>
 *
 * @version     $Id: scriptBrowser.js,v 1.7 2011/07/04 10:24:24 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR)  20010913
 ******************************************************************************
 */

//============= necessary classes and variables ===============================


//============= declarations ==================================================

// constants:
var BT_NS = 'NS';                       // browser type Netscape
var BT_IE = 'IE';                       // browser type Internet Explorer
var BT_OP = 'OP';                       // browser type Opera
var BT_LY = 'LY';                       // browser type Lynx
var BT_FF = 'FF';                       // browser type Firefox
var BT_UNKNOWN = 'UNKNOWN';             // browser type unknown
var BV_10 = '10';                       // browser version 1.x
var BV_20 = '20';                       // browser version 2.x
var BV_30 = '30';                       // browser version 3.x
var BV_40 = '40';                       // browser version 4.x
var BV_50 = '50';                       // browser version 5.x
var BV_60 = '60';                       // browser version 6.x
var BV_70 = '70';                       // browser version 7.x
var BV_80 = '80';                       // browser version 8.x
var BV_90 = '90';                       // browser version 9.x
var BV_UNKNOWN = 'UNKNOWN';             // browser version unknown

// variables:


//============= class Browser =================================================

/*
- browser identification:
userAgent
appName
appVersion
vendor
vendorSub

- Netscape 6.1:
Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:0.9.2) Gecko/20010726 Netscape6/6.1
Netscape
5.0 (Windows; en-US)
Netscape6
6.1

- Netscape 4.77:
Mozilla/4.77 [en] (Windows NT 5.0; U)
Netscape
4.77 [en] (Windows NT 5.0; U)
undefined
undefined

- MSIE 6.0:
Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)
Microsoft Internet Explorer
4.0 (compatible; MSIE 6.0; Windows NT 5.0)
undefined
undefined

- MSIE 7.0 Beta:
Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 6.0)
Microsoft Internet Explorer
4.0 (compatible; MSIE 7.0b; Windows NT 6.0)
undefined
undefined

- MSIE 7.0:
Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)
Microsoft Internet Explorer
4.0 (compatible; MSIE 7.0; Windows NT 6.0)
undefined
undefined

- MSIE 8.0:
Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1)
Microsoft Internet Explorer
4.0 (compatible; MSIE 8.0; Windows NT 5.1)
undefined
undefined

- MSIE 9.0:
Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1)
Microsoft Internet Explorer
5.0 (compatible; MSIE 9.0; Windows NT 6.1)
undefined
undefined

- Opera 5.10:
Opera/5.10 (Windows 2000; U)  [en]
Opera
5.10 (Windows 2000; U)
undefined
undefined

- Opera 5.10 as Mozilla 5.0:
Mozilla/5.0 (Windows 2000; U) Opera 5.10  [en]
Netscape
5.0 (Windows 2000; U)
undefined
undefined

- Opera 5.10 as Mozilla 4.76:
Mozilla/4.76 (Windows 2000; U) Opera 5.10  [en]
Netscape
4.76 (Windows 2000; U)
undefined
undefined

- Opera 5.10 as Mozilla 3.0:
Mozilla/3.0 (Windows 2000; U) Opera 5.10  [en]
Netscape
3.0 (Windows 2000; U)
undefined
undefined

- Opera 5.10 as MSIE 5.0:
Mozilla/4.0 (compatible; MSIE 5.0; Windows 2000) Opera 5.10  [en]
Microsoft Internet Explorer
4.0 (compatible; MSIE 5.0; Windows 2000)
undefined
undefined

- Firefox 1.0
Mozilla/5.0 (Windows; U; Windows NT 5.0; de-DE; rv:1.7.5) Gecko/20041122 Firefox/1.0
Netscape
5.0 (Windows; de-DE)
Firefox
1.0

- Firefox 4.0.1
Mozilla/5.0 (Windows NT 5.0; rv:2.0.1) Gecko/20100101 Firefox/4.0.1
Netscape
5.0 (Windows)
undefined
undefined

- Firefox 5.0
AgentMozilla/5.0 (Windows NT 5.1; rv:5.0) Gecko/20100101 Firefox/5.0
Netscape
5.0 (Windows)
undefined
undefined
*/


/**
 * void Browser_print ()
 * Display the browser's values.
 */
function Browser_print ()
{
    var text = "Browser:\n";

    text += "type = " + this.type + "\n";
    text += "version = " + this.version + "." + this.subVersion + "\n";
//    text += "platform = " + this.platform + "\n";

    alert (text);
} // Browser_print

/**
 * Browser_initialize ()
 * Get the version number of the current browser.
 */
function Browser_initialize ()
{
    /* Activate to log trace the browser info:
    alert ("navigator.userAgent:" + navigator.userAgent + "\n" +
        "navigator.appName:" + navigator.appName + "\n" +
        "navigator.appVersion" + navigator.appVersion + "\n" +
        "navigator.vendor:" + navigator.vendor  + "\n" +
        "navigator.vendorSub:" + navigator.vendorSub
        );
    */

//    var dom = document.getElementById;
//    var appName = navigator.appName;    // the appName string of the browser
    var appVersion = navigator.appVersion; // the appVersion string of the br.
    var userAgent = navigator.userAgent; // the userAgent string of the browser
    var versionStr = appVersion;        // the full version as string
    var nameVersion = "";               // the name and version
    var version = 0;                    // the version number
    var subVersion = 0;                 // the sub vers. no.
    var pos = 0;                        // actual position within string
    var pos2 = 0;                       // actual position within string
    var platform = "";                  // the platform the br. is running on
    var identifiers = new Array ("Mozilla", "Netscape6", "Netscape", "Lynx", "MSIE", "Microsoft Internet Explorer", "Opera", "Firefox");
                                        // known browser identifiers
    var props = new Array ("ns", "ns", "ns", "lynx", "ie", "ie", "opera", "firefox");
                                        // the browser properties according to
                                        // the identifiers
    var parenthesis = "";               // the parenthesis part of userAgent
    var lastPart = "";                  // the last part of the userAgent
    var tokens = null;                  // array of tokens
    var token = "";                     // the actual token

    // get the version and sub version numbers:
    pos = appVersion.indexOf (".");
    if (pos >= 0)
    {
        if (pos > 0)
        {
            version = parseInt (appVersion.substr (0, pos), 10);
        } // if
        else                                // "." at first position
        {
            version = 0;
        } // else "." at first position

        pos2 = appVersion.indexOf (" ", pos + 1);
        if (pos2 > 0)
        {
            subVersion =
                parseInt (appVersion.substr (pos + 1, pos2 - pos - 1), 10);
        } // if
        else
        {
            subVersion = 0;
        } // else
    } // if
    else                                // "." not found?
    {
        version = parseInt (appVersion, 10);
    } // else "." not found


    // extract the several parts of the user agent:
    if ((pos = userAgent.indexOf ("(")) >= 0) // a parenthesis exists?
    {
        // the first part - the name and version of Mozilla:
        nameVersion = trim (userAgent.substring (0, pos));
        // the part in parents which contains several pieces:
        parenthesis = userAgent.substring (pos + 1, userAgent.length);

        if ((pos = parenthesis.indexOf (")")) >= 0) // found right parenthesis?
        {
            // the part after the parenthesis:
            lastPart = parenthesis.substring (pos + 1, parenthesis.length);
            // the parenthesis content:
            parenthesis = parenthesis.substring (0, pos);
        } // if found right parenthesis

        // tokenize the part in the parenthesis:
        tokens = parenthesis.split (";");
        // add the last part:
        tokens[tokens.length] = lastPart;

        // now go through the tokens:
        for (var i = 0; i < tokens.length; i++)
        {
            // get the actual token and drop leading and trailing spaces:
            token = trim (tokens[i]);
            // compatible - might want to reset from Netscape
            if (token == "compatible")
            {
                // One might want to reset nameVersion to a null string
                // here, but instead, we'll assume that if we don't
                // find out otherwise, then it really is Mozilla
                // (or whatever showed up before the parens).
                // browser - try for Opera or IE
            } // if
            else if (token.indexOf ("Netscape") >= 0)
            {
                nameVersion = token;
            } // else if
            else if (token.indexOf ("MSIE") >= 0)
            {
                nameVersion = token;
            } // else if
            else if (token.indexOf ("Opera") >= 0)
            {
                nameVersion = token;
            } // else if
            else if (token.indexOf ("Firefox") >= 0)
            {
                // Within Firefox the name token has a prefix (Gecko/20100101);
                // so remove the prefix.
                nameVersion = token.substring (token.indexOf ("Firefox"),token.length);
            } // else if

            // platform - try for X11, SunOS, Win, Mac, PPC
            else if ((token.indexOf ("X11") >= 0) ||
                     (token.indexOf ("SunOS") >= 0) ||
                     (token.indexOf ("Linux") >= 0))
            {
                platform = "Unix";
            } // else if
            else if (token.indexOf ("Win") >= 0)
            {
                platform = token;
            } // else if
            else if ((token.indexOf ("Mac") >= 0) ||
                     (token.indexOf ("PPC") >= 0))
            {
                platform = token;
            } // else if
        } // for
    } // if a parenthesis exists
    else                                // no parenthesis
    {
        // just the name and version:
        nameVersion = trim (navigator.userAgent);
    } // else no parenthesis

/*
    var msieIndex = nameVersion.indexOf ("MSIE");
    if (msieIndex >= 0)
    {
        nameVersion = nameVersion.substring (msieIndex, nameVersion.length);
    } // if
*/

    // check vendor:
    if (navigator.vendor)
    {
        if (navigator.vendor.indexOf ("Netscape6") == 0)
        {
            // Netscape 6 is Mozilla 5
            // set the version and sub version:
            // (in Netscape 6 navigator has the new properties vendor and
            // vendorSub)
            versionStr = navigator.vendorSub;
            nameVersion = navigator.vendor + "/" + navigator.vendorSub;
                                        // the sub vers. no.
        } // if
        else if (navigator.vendor.indexOf ("Firefox") == 0)
        {
            // Firefox is Mozilla 5
            // set the version and sub version:
            // (in Firefox navigator has the new properties vendor and
            // vendorSub)
            versionStr = navigator.vendorSub;
            nameVersion = navigator.vendor + "/" + navigator.vendorSub;
        } // if
    } // if


    // ensure that no browser is set:
    for (var i = 0; i < props.length; i++)
    {
        eval ("this." + props[i] + " = false"); // set browser to false
    } // for


    // identify the browser type:
    pos = -1;                           // position of found browser
    // loop through all known identifiers and compare them with the actual one:
    for (var i = 0; i < identifiers.length; i++)
    {
        if (pos < 0 &&
            nameVersion.substring (0, identifiers[i].length) == identifiers[i])
                                        // identifier found?
        {
            pos = i;
            eval ("this." + props[i] + " = true"); // set the browser
            // compute the version string:
            versionStr = nameVersion.substring (identifiers[i].length + 1, nameVersion.length);
        } // if identifier found
    } // for i

    // if the version is 3 or less the version is used as identified from
    // navigator.appVersion
    // for all other browser versions the version must be computed from the
    // string which was determined above
    if (version > 3 || this.firefox)    // navigator.appVersion bigger than 3?
    {
        // ensure that there is no leading '/':
        if (versionStr.charAt (0) == '/')
        {
            // drop the first character:
            versionStr = versionStr.substring (1, versionStr.length);
        } // if

        // drop leading and trailing spaces:
        versionStr = trim (versionStr);

        // drop the string which is after the number:
        if ((pos = versionStr.indexOf (" ")) >= 0)
        {
            // use just the part before the space:
            versionStr = versionStr.substring (0, pos);
        } // if

        // get the browser version and sub version:
        // split the version and sub version at the '.':
        if ((pos = versionStr.indexOf (".")) >= 0) // found a '.'?
        {
            version = parseInt (versionStr.substring (0, pos), 10);
            subVersion = parseInt (
                versionStr.substring (pos + 1, versionStr.length), 10);
        } // if found a '.'
        else                                // no sub version
        {
            version = parseInt (versionStr, 10);
            subVersion = 0;
        } // else no sub version
    } // if navigator.appVersion bigger than 3


    // set the properties within the class:
    this.version = version;
    this.subVersion = subVersion;
    this.ie4 = (this.ie && this.version == 4);
    this.ie5 = (this.ie && this.version == 5);
    this.ie6 = (this.ie && this.version == 6);
    this.ie7 = (this.ie && this.version == 7);
    this.ie8 = (this.ie && this.version == 8);
    this.ie9 = (this.ie && this.version == 9);
    this.ns4 = (this.ns && this.version == 4);
    this.ns5 = (this.ns && this.version == 5);
    this.ns6 = (this.ns && this.version == 6);
    this.op4 = (this.opera && this.version == 4);
    this.op5 = (this.opera && this.version == 5);
    this.op6 = (this.opera && this.version == 6);
    this.ff1 = (this.firefox && this.version == 1);
    this.ff4 = (this.firefox && this.version == 4);
    this.ff5 = (this.firefox && this.version == 5);
    this.type = (this.ns ? BT_NS :
                    (this.ie ? BT_IE :
                        (this.opera ? BT_OP :
                            (this.lynx ? BT_LY :
                                (this.firefox ? BT_FF : BT_UNKNOWN)))));
    if (this.ie)
    {
        this.typeVersion = this.type +
            (this.version < 3 ? BV_UNKNOWN :
                (this.version < 4 ? BV_30 :
                    (this.version < 9 ? BV_40 :
                        (this.version < 10 ? BV_50 :BV_UNKNOWN))));
    } // if
    else
    {
        this.typeVersion = this.type +
            ((this.version == 1 && this.firefox) ? BV_10 :
                (this.version < 3 ? BV_UNKNOWN :
                    (this.version < 4 ? BV_30 :
                        (this.version < 5 ? BV_40 :
                            (this.version < 6 ? BV_50 :
                                (this.version < 7 ? BV_60 :
                                    (this.version < 8 ? BV_70 :
                                     BV_UNKNOWN)))))));
    } // else
    this.platform = platform;
} // Browser_initialize

/**
 * Browser Browser ()
 * Create a new Browser object.
 */
function Browser ()
{
    // define methods:
    this.print = Browser_print;         // display the browser's properties
    this.initialize = Browser_initialize; // initilize the browser

    // define properties and assign initial values:
    this.ns = false;                    // type of browser: Netscape
    this.ie = false;                    // type of browser: Internet Explorer
    this.opera = false;                 // type of browser: Opera
    this.lynx = false;                  // type of browser: Lynx
    this.firefox = false;               // type of browser: Firefox
    this.version = 0;                   // browser version
    this.subVersion = 0;                // sub version of browser
    this.isBeta = false;                // is this a beta version?
    this.ie4 = false;                   // internet explorer 4.x
    this.ie5 = false;                   // internet explorer 5.x
    this.ie6 = false;                   // internet explorer 6.x
    this.ie7 = false;                   // internet explorer 7.x
    this.ie8 = false;                   // internet explorer 8.x
    this.ie9 = false;                   // internet explorer 9.x
    this.ns4 = false;                   // netscape 4.x
    this.ns5 = false;                   // netscape 5.x
    this.ns6 = false;                   // netscape 6.x
    this.op4 = false;                   // opera 4.x
    this.op5 = false;                   // opera 5.x
    this.op6 = false;                   // opera 6.x
    this.ff1 = false;                   // Firefox 1.x
    this.ff4 = false;                   // Firefox 4.x
    this.ff5 = false;                   // Firefox 5.x
    this.type = BT_UNKNOWN;             // browser type
    this.typeVersion = BT_UNKNOWN + BV_UNKNOWN; // complete browser
                                        // identification as string
    this.platform = "Unix";             // the platform

    // perform initial statements:
    this.initialize ();                 // set the type, version, and sub vers.

    // ensure constraints:
} // Browser


//============= common functions ==============================================

/**
 * String trim (String str)
 * Utility function to trim spaces from both ends of a string.
 */
function trim (str)
{
    var retVal = "";
    var start = 0;
    var end = str.length;

    while ((start < str.length) && (str.charAt (start) == ' '))
    {
        ++start;
    } // while
    while ((end > 0) && (str.charAt (end - 1) == ' '))
    {
        --end;
    } // while

    retVal = str.substring (start, end);
    return retVal;
} // trim
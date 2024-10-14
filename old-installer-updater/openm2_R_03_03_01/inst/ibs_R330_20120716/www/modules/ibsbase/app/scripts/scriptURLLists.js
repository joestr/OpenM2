/******************************************************************************
 * This file contains all JavaScript classes and their methods which are used
 * to create and manage lists. <BR>
 * 
 * @version     2.23.0001, 07.03.2002
 *
 * @author      Klaus Reimüller (KR)  020307
 ******************************************************************************
 */

//============= declarations ==================================================

var actList = null;                     // the actual (url) list


//============= initializations ===============================================


//============= class URLList =================================================

/**
 * void URLList_add (String url)
 * Store a new URL within the list.
 */
function URLList_add (url)
{
    // store the url using the no. as id:
    this.add (new Tuple ("" + this.length, url));
} // URLList_add


/**
 * void execute (String|int id)
 * Execute the JavaScript statements which are stored with the provided id.
 */
function execute (id)
{
    var tuple = this.get (id);          // get the tuple out of the list

    if (tuple != this.notFound)         // found tuple?
        eval (tuple.value);             // interpret the text as JavaScript
                                        // code and execute it
} // execute


/**
 * void URLList_nextURL (FrameObject frame)
 * Load the frame with that url, which comes within the list after the actual 
 * url of that frame.
 */
function URLList_nextURL (frame)
{
    // get the actual url of the frame:
    var id = getURL (frame, this.fullPath);

    // get the tuple with the new url:
    var tuple = this.find (id);         // search the actual tuple
    if (tuple != this.notFound)         // found tuple?
        tuple = this.next ();           // get the next tuple
    else                                // didn't find tuple
        tuple = this.first ();          // get the first tuple

    loadTupleURL (tuple, frame);        // load the frame with the tuple url
} // URLList_nextURL


/**
 * void URLList_prevURL (FrameObject frame)
 * Load the frame with that url, which comes within the list before the actual 
 * url of that frame.
 */
function URLList_prevURL (frame)
{
    // get the actual url of the frame:
    var id = getURL (frame, this.fullPath);

    // get the tuple with the new url:
    var tuple = this.find (id);         // search the actual tuple
    if (tuple != this.notFound)         // found tuple?
        tuple = this.prev ();           // get the previous tuple
    else                                // didn't find tuple
        tuple = this.last ();           // get the last tuple

    loadTupleURL (tuple, frame);        // load the frame with the tuple url
} // URLList_prevURL


/**
 * URLList URLList (boolean cyclic, boolean fullPath) extends TupleList
 * Constructor of class URLList. (This list may be cyclic list.)
 */
function URLList (cyclic, fullPath)
{
    // inherit TupleList:
    this.parent = TupleList;            // create method for super class
    this.parent ("UL1", window, cyclic); // create TupleList

    // define methods:
    this.add = URLList_add;             // insert new url
    this.nextURL = URLList_nextURL;     // load the next url
    this.prevURL = URLList_prevURL;     // load the previous url

    // define properties and assign initial values:
    this.fullPath = fullPath;           // full path for url

    // ensure constraints:
} // URLList


//============= actual (url) list =============================================

/**
 * void setList (URLList list)
 * Store the provided list actual one.
 */
function setList (list)
{
    actList = list;                     // set list
} // setList


/**
 * void nextURL (FrameObject frame)
 * Call the next url of the actual list within the frame.
 */
function nextURL (frame)
{
    actList.nextURL (frame);            // get and call url
} // nextURL


/**
 * void prevURL (FrameObject frame)
 * Call the previous url of the actual list within the frame.
 */
function prevURL (frame)
{
    actList.prevURL (frame);            // get and call url
} // prevURL


//============= common functions ==============================================

/**
 * URL getURL (FrameObject frame, boolean fullPath)
 * Compute the url of a frame.
 *
 * @input parameters:
 * @param   frame       The frame from which to compute the url.
 * @param   fullPath    Compute absolute (true) or relative path (false)?
 */
function getURL (frame, fullPath)
{
    var url = frame.location.pathname;  // define url
    if (!fullPath)                      // only the file name?
        url = url.substring (url.lastIndexOf("\\") + 1, url.length);
                                        // cut the path in front of the file name
    if (frame.location.search)          // are there some parameters?
        url += frame.location.search;   // add these parameters
/*
    if (frame.location.hash)            // reference within a page?
        url += "#" + frame.location.hash; // add the reference
*/

    return url;                         // return the computed url
} // getURL


/**
 * void loadTupleURL (Tuple tuple, frameObject frame)
 * Load an url, which is the value of a tuple, within a specific frame.
 */
function loadTupleURL (tuple, frame)
{
    var JAVASCRIPT = "javascript:";     // beginning of a JavaScript url

    if (frame)                          // frame exists?
    {
        if (tuple != TUPLE_NOT_FOUND)   // tuple found?
        {
            if (tuple.value.substring (0, JAVASCRIPT.length) == JAVASCRIPT)
                                        // JavaScript call?
            {
                // define some common JavaScript string constants:
                var topLoadFrameParam = "top.loadframe_param(top,";
                var topLoad = "top.load(top,";
                var topHistory = "top.history";
                // define url and initialize it with the text comming after
                // the JAVASCRIPT key word:
                var url = tuple.value.substring (JAVASCRIPT.length);

                if (url.substring (0, topLoad.length) == topLoad ||
                    url.substring (0, topHistory.length) == topHistory ||
                    url.substring (0, topLoadFrameParam.length) == 
                        topLoadFrameParam)
                    // main window?
                    // The main window (top) cannot be overloaded directly.
                    // Because of this we must first go to another frame to
                    // overload the main window from there.
                    top.load (frame, tuple.value);
                                        // The text is interpreted as 
                                        // JavaScript functionality and 
                                        // executed
                else                    // any other frame
                    eval (url);         // The text is interpreted as
                                        // JavaScript functionality and
                                        // executed

            } // if JavaScript call
            else
                top.load (frame, tuple.value); // interprete text as url and
                                        // load it
        } // if tuple found
    } // if frame exists
} // loadTupleURL

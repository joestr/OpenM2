/******************************************************************************
 * This file contains all JavaScript classes and their methods which are used
 * to create and manage loading functionality. <BR>
 * 
 * @version     $Id: scriptLoader.js,v 1.3 2010/04/19 09:50:44 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR) 20060320
 ******************************************************************************
 */

//============= declared classes and functions ================================
// class Loader
// class LoaderHandler

// function startLoaderHandler


//============= necessary classes and variables ===============================
// class TupleList

// messages:


//============= declarations ==================================================

// constants:

// variables:
/**
 * The loader handler. <BR>
 * This handler is used for loading elements of TupleLists.
 */
var loaderHandler = new LoaderHandler ();


//============= initializations ===============================================


//============= class Loader ==================================================

/**
 * void Loader_load (float interval)
 * Perform the loading process for the loader.
 */
function Loader_load (interval)
{
    // set semaphore for loading the elements:
    if (!this.isLoading)                // elements currently not loading?
    {
        this.interval = interval;       // store the interval
        this.isLoading = true;          // now the elements are loading
        this.isFinished = false;        // loading not finished
        this.couldNotLoad = false;      // loading is still possible
        this.increment = this.interval / this.secondsMax / 10;
                                        // set the increment
        this.percentage = 0;            // the actual loading percentage

        // call the corresponding function at the server:
        callUrl (this.fct, this.params, this.fileName, this.frameName);

        // ensure that the loading process is started:
        startLoaderHandler ();
    } // if elements currently not loading
} // Loader_load


/**
 * boolean Loader_checkLoading ()
 * Check if the loader is currently loading.
 */
function Loader_checkLoading ()
{
    var oldPercentage = this.percentage; // percentage before incrementation

    if (this.isLoading)                 // elements currently loading?
    {
        // check if the loading has been finished:
        if (this.percentage <= 99)      // loading not finished?
        {
            // compute the new percentage:
            this.percentage += this.increment;

            // set new increment value:
            if (this.percentage >= 70 && oldPercentage < 70)
            {
                this.increment /= 2;
            } // if
            else if (this.percentage >= 80 && oldPercentage < 80)
            {
                this.increment /= 2;
            } // if
            else if (this.percentage >= 90 && oldPercentage < 90)
            {
                this.increment /= 2;
            } // if
        } // if loading not finished
        else                            // loading not possible
        {
            this.isLoading = false;
            this.couldNotLoad = true;
            this.increment = this.interval / this.secondsMax / 20;
        } // else loading not possible
    } // if elements currently not loading
    else if (this.isFinished || this.couldNotLoad) // loading already finished?
    {
        // increment the counter:
        this.percentage += this.increment;
    } // else if loading already finished

    // return the actual loading state:
    return this.isLoading;
} // Loader_checkLoading


/**
 * void Loader_finishLoading ()
 * The loader finished its loading task.
 */
function Loader_finishLoading ()
{
    // set the corresponding values:
    this.percentage = 100;
    this.increment = this.interval / 50;
    this.isLoading = false;
    this.isFinished = true;

    // inform the list that the loading process has been finished:
    if (this.list != null && this.list.setLoaded) // element list defined?
    {
        // tell the list that the required elements have been loaded:
        this.list.setLoaded ();
    } // if element list defined
} // Loader_finishLoading


/**
 * Loader Loader (String|int id, TupleList list, int fct, String params,
 *                String fileName, String frameName, String outText,
 *                float secondsMax)
 *                extends Tuple
 * Create loader.
 */
function Loader
    (id, list, fct, params, fileName, frameName, outText, secondsMax)
{
    // call super constructor(s):
    CollectionElement.call (this, id);

    // set property values:
    this.list = list;                   // the list for which to load the
                                        // elements
    this.fct = fct;                     // loading function for online mode
    this.params = params;               // additional parameters for the url
    this.fileName = fileName;           // file name for offline mode
    this.frameName = frameName;         // the name of the frame for loading
    this.outText = outText;             // text for the actual element
    this.secondsMax = secondsMax;       // maximum number of seconds
} // Loader

// create class form constructor:
createClass (Loader, CollectionElement,
{
    // define properties and assign initial values:
    list: null,                         // the list for which to load the
                                        // elements
    fct: null,                          // loading function for online mode
    params: null,                       // additional parameters for the url
    fileName: null,                     // file name for offline mode
    frameName: null,                    // the name of the frame for loading
    outText: null,                      // text for the actual element
    secondsMax: 0,                      // maximum number of seconds
    percentage: 0,                      // loading percentage
    interval: 1,                        // the loading interval
    isLoading: false,                   // is the loader currently loading?
    isFinished: false,                  // was the loading process finished?
    couldNotLoad: false,                // loading was not possible?

    // assign methods:
    load: Loader_load,                  // starts the loading process
    checkLoading: Loader_checkLoading,  // check if currently loading
    finishLoading: Loader_finishLoading // loading finished
}); // createClass


//============= class LoaderHandler ===========================================

/**
 * void LoaderHandler_load (String|int id, TupleList list, int fct,
 *                          String params, String fileName, String frameName,
 *                          String outText, float secondsMax)
 * Load a new list.
 */
function LoaderHandler_load
    (id, list, fct, params, fileName, frameName, outText, secondsMax)
{
    var elem = this.notFound;           // the actual element

    // check if there exists already such a loader within the handler:
    if ((elem = this.get (id)) == this.notFound) // loader does not exist?
    {
        // create a new loader:
        elem = new Loader
            (id, list, fct, params, fileName, frameName, outText, secondsMax);
        // add the loader to the handler:
        this.add (elem);

        // start the loading process:
        elem.load (this.interval);
    } // if
    else
    {
        // set the new list
        elem.list = list;
        
        // start the loading process:
        elem.load (this.interval);
    } // else
} // LoaderHandler_load


/**
 * void LoaderHandler_startLoading ()
 * Start the loading process.
 */
function LoaderHandler_startLoading ()
{
    if (this.timer == null)             // no timer set?
    {
        // call the function:
        this.timer = startTimer (this, this.continueLoading, 500);
    } // if no timer set
} // LoaderHandler_startLoading


/**
 * void LoaderHandler_continueLoading ()
 * Continue the loading process.
 */
function LoaderHandler_continueLoading ()
{
    var elem = this.notFound;           // the actual element
    var text = "";                      // the output string

    this.forAll (
        function (elem, loaderHandler)
        {
//showProps (elem);
//showProps (this);
//alert ("elem: " + elem + "\n" + "loaderHandler: " + loaderHandler);
            // check if the current element is loading:
            if (elem.checkLoading ())       // currently loading?
            {
                // concate the current element to the text:
                text += elem.outText + " loading: " +
                        Math.floor (elem.percentage) + "%. ";
            } // if currently loading
            else if (elem.couldNotLoad)     // loading not possible?
            {
                if (elem.percentage < 200)  // waiting for possible loading?
                {
                    // concate the current element to the text:
                    text += elem.outText + " loading: 99%. ";
                } // if waiting for possible loading
                else                        // element shall be dropped?
                {
                    alert ("Could not load " + elem.outText + ".\n" +
                           "Please try again.\n" +
                           "If this does not work contact your administrator.");
                    // drop the element:
                    this.drop (elem.id);
                } // else element shall be dropped
            } // else if loading not possible
            else if (elem.isFinished)       // loading finished?
            {
                text += elem.outText + " loaded. ";

                if (elem.percentage >= 200) // element shall be dropped?
                {
                    // drop the element:
//showProps (loaderHandler);
//loaderHandler.print ();
                    this.drop (elem.id);
                } // if element shall be dropped
            } // else if loading finished
        } // function
    );

    // write the text:
    top.showState (text);

    if (this.length > 0)                // at least one loader exists?
    {
        // call this function another time:
        this.timer = startTimer (this, this.continueLoading, this.interval);
    } // if at least one loader exists
} // LoaderHandler_continueLoading


/**
 * void LoaderHandler_finishLoading (String|int id)
 * Finish the loading for a specific loader.
 */
function LoaderHandler_finishLoading (id)
{
    var elem = this.notFound;           // the actual element

    // search for the loader:
    if ((elem = this.get (id)) != this.notFound) // loader exists?
    {
        // finish the loader:
        elem.finishLoading (this.interval);
    } // if loader exists
} // LoaderHandler_finishLoading


/**
 * LoaderHandler LoaderHandler () extends TupleList
 * Create loader handler.
 * A loader handler is responsible for loading TupleLists.
 */
function LoaderHandler ()
{
    // call super constructor(s):
    TupleList.call (this, "LoaderHandler");

    // set property values:

    // perform initial statements:
} // LoaderHandler

// create class form constructor:
createClass (LoaderHandler, TupleList,
{
    // define properties and assign initial values:
    timer: null,                        // loading timer
    interval: 200,                      // primary time interval in ms

    // assign methods:
    load: LoaderHandler_load,           // start loading for a specific task
    continueLoading: LoaderHandler_continueLoading,
                                        // continue the loading process
    startLoading: LoaderHandler_startLoading, // start the loading process
    finishLoading: LoaderHandler_finishLoading // loading task finished
}); // createClass


//============= common functions ==============================================

/**
 * void startLoaderHandler ()
 * Start the loading process.
 */
function startLoaderHandler ()
{
    loaderHandler.startLoading ();
} // startLoaderHandler

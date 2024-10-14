/******************************************************************************
 * JavaScript-File. <BR>
 * 
 * @version     2.23.0023, 09.04.2002 KR
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */

//============= declared classes and functions ================================
// class Button
// class ButtonBar

// function createButtonBar
// function disableButtons
// function disableButton
// function dropButtonBar
// function clearButtonBar
// function cleanButtonBar
// function addButton
// function buttonPressed
// function singleButtonPressed
// function showSingleButton
// function showButtonsLoading
// function getVarContent
// function setActButtonBar
// function getAvailableButtons
// function buttonsLoaded


//============= necessary classes and variables ===============================
// var loaderHandler
//
// class Tuple
// class TupleList
// class LoadableList


//============= declarations ==================================================
/**
 * The timeout for waiting on multilang texts for beeing loaded.
 */
var BUTTON_BAR_ML_TIMEOUT = 10000;

/**
 * Timeout starting time.
 * Null if no timeout is running.
 */
var timeoutStartTime = null;

/**
 * Holds the history list for beeing able to provide it to createButtonBar
 * method during retryCreateButtonBar ().
 */
var historyListCache = null;

/**
 * The current button bar.
 */
var actButtonBar = null;

/**
 * The oid for which some buttons shall be disabled.
 */
var p_oidForDisabling = null;

/**
 * The buttons which shall be disabled.
 */
var p_buttonsForDisabling = null;

/**
 * The currently disabled buttons.
 */
var p_disabledButtons = null;

/**
 * All available buttons.
 */
var p_availButtons = new ButtonBar (0, "name", top.buttons, "");

//============= class Button ==================================================

/**
 * Button Button (int id, String name, String imageActive, 
 *                String description, String url)
 * Create button.
 */
function Button (id, name, imageActive, description, url)
{
    // call super constructor(s):
    Tuple.apply (this, arguments);

    // set property values:
    this.name = name;                   // name
    this.imageActive = imageActive;     // image shown for active button
    this.description = description;     // description of the button
    this.url = url;                     // url of the button
} // Button

// create class form constructor:
createClass (Button, Tuple,
{
    // define properties and assign initial values:
    name: null,                         // name
    imageActive: null,                  // image shown for active button
    description: null,                  // description of the button
    url: null,                          // url of the button

    // define methods:
    show: Button_show,                  // display the button
    showAsSingle: Button_showAsSingle   // display the button not within a
                                        // button bar, but as single button
}); // createClass


//============= class ButtonBar ===============================================

/**
 * boolean ButtonBar_setElements (ButtonBar availElements, Array idList)
 * Set the elements of the button bar.
 * This method first drops all old elements of the button bar and then sets
 * the elements to those identified by the ids in the idList and available in
 * availElements.
 * The return value is true, if all elements where found and added to the list,
 * false otherwise.
 */
function ButtonBar_setElements (availElements, idList)
{
    var i = 0;                          // loop counter
    var elem = null;                    // the actual element
    var found = 0;                      // number of found elements

    // drop all old elements:
    this.empty ();

    // initialize the available property:
    this.allAvailable = true;
    // set the new id list:
    this.idList = idList;

    // add all elements in the id list to the button bar:
    // the order is defined through the appearance of the buttons in the
    // availElements list
    for (elem = availElements.first (); elem != availElements.notFound;
         elem = availElements.next ())
    {
        // check if the actual element has to be added:
        // loop through all elements to be added and compare the to the actual
        // one
        for (i = 0; i < idList[i]; i++)
        {
            if (elem.id == idList[i])   // element found?
            {
                // add the element to the tuple list:
                this.add (elem);
                found++;
            } // if element found
        } // if for
    } // for

    if (found < idList.length)          // at least one element not found?
    {
        this.allAvailable = false;      // not all elements are available
    } // if at least one element not found

    // return the result:
    return this.allAvailable;
} // ButtonBar_setElements


/**
 * void ButtonBar_setLoaded ()
 * Tell the button bar that the available buttons are loaded.
 */
function ButtonBar_setLoaded ()
{
    // add the several elements to the element list:
    // loop through all elements within element id list and add them
    // in the same order to the element list.
    this.setElements (p_availButtons, this.idList);

    // make this button bar the actual one and display it:
    this.setActual ();
} // ButtonBar_setLoaded


/**
 * void ButtonBar_setActual ()
 * Set this button bar as actual one.
 */
function ButtonBar_setActual ()
{
    // make this button bar the actual one and display it:
    setActButtonBar (this);
} // ButtonBar_setActual


/**
 * void ButtonBar_getAvailable ()
 * Tell the button bar to get the available elements.
 */
function ButtonBar_getAvailable ()
{
    // get all available buttons:
    getAvailableButtons (this);
} // ButtonBar_getAvailable


/**
 * ButtonBar ButtonBar (String id, String name, FrameObject frame)
 * extends TupleList
 * Create button bar.
 */
function ButtonBar (id, name, frame)
{
    lastButtonBar = actButtonBar;       // remember the last button bar
    
    // call super constructor(s):
    TupleList.call (this, id, frame, false);

    // set property values:
    this.name = name;                   // name
} // ButtonBar

// create class form constructor:
createClass (ButtonBar, [TupleList, LoadableList],
{
    // define properties and assign initial values:
    name: null,                         // name
    styleSheet: "styleButtonBar.css",   // actual style sheet

    // define methods:
    show: ButtonBar_show,               // display the button bar
    clear: ButtonBar_clear,             // clear the button bar
    showLoading: ButtonBar_showLoading, // display the loading state
    setElements: ButtonBar_setElements, // set the elements of the list
    setLoaded: ButtonBar_setLoaded,     // tell the button bar that the
                                        // available buttons are loaded
    setActual: ButtonBar_setActual,     // set this button bar as actual one
    getAvailable: ButtonBar_getAvailable // get all available elements
}); // createClass


//============= common functions ==============================================

/**
 * void createButtonBar (OIDString oid, HexString allowedButtons, Object[] historyList, int actHistoryIndex)
 * Create a new button bar and display it if multilang texts are available.
 * If no texts are available it is retried to create the button bar after a defined delay until texts are
 * available or the timeout is reached.
 */
function createButtonBar (oid, allowedButtons, historyList, actHistoryIndex)
{
	// check if multilang client texts file is available otherwise
	// starting to load the buttons does not make sense
	if (top.isMultilangLoaded ())
	{
		// check if the timer has to be deactivated:
		if (timeoutStartTime != null)
		{
			timeoutStartTime = null;
			historyListCache = null;
		} // if
	
		// create the button bar:
		performCreateButtonBar (oid, allowedButtons, historyList, actHistoryIndex);
	} // if
	else
	{	
		// check if max timeout has not been reached yet
		if (timeoutStartTime == null || (new Date () - timeoutStartTime) <= BUTTON_BAR_ML_TIMEOUT)
		{
			if (timeoutStartTime == null) // first retry
			{
				timeoutStartTime = new Date ();
				historyListCache = historyList;
			} // if

			// retry to create the button after 100ms:
			var retryCmd = "retryCreateButtonBar (\"" + oid + "\", \"" + allowedButtons + "\", " + actHistoryIndex + ")";
			setTimeout (retryCmd, 100);
		} // if
		else // timeout reached
		{
			alert ("Could not load buttons due to missing multilang client texts file.\n" +
				   "Please contact your administrator.");
		} // else
	} // else
} // createButtonBar

/**
 * void retryCreateButtonBar (OIDString oid, HexString allowedButtons, int actHistoryIndex)
 * Calls createButtonBar method by using the provided parameters and the cached historyList.
 */
function retryCreateButtonBar (oid, allowedButtons, actHistoryIndex)
{
	// retry to create the button bar:
	createButtonBar (oid, allowedButtons, historyListCache, actHistoryIndex);
} // retryCreateButtonBar

/**
 * void performCreateButtonBar (OIDString oid, HexString allowedButtons, Object[] historyList, int actHistoryIndex)
 * Create a new button bar and display it.
 */
function performCreateButtonBar (oid, allowedButtons, historyList, actHistoryIndex)
{	
	var buttonIdList = new Array ();
	var pos = 0;
	var i = 0;
	var blockSize = 4;
	var blocks = 0;
	var base = 0;
	var isDisabled = false;             // is the actual button disabled?
	var blength = allowedButtons.length;
	var allowed = parseInt ("0x" + allowedButtons.charAt (blength - 1));

	// check if there are any buttons for disabling:
	if (p_buttonsForDisabling != null)
	{
		disableButtons (p_oidForDisabling, p_buttonsForDisabling);
		p_oidForDisabling = null;
		p_buttonsForDisabling = null;
	} // if

	// get all button ids and store them in an array:
	while (blocks < blength)
	{
		base = blocks * blockSize;
		for (i = 0; i < blockSize; i++)
		{
			if ((allowed & 1) == 1)
			{
				// check if the button is not disabled:
				if (p_disabledButtons != null)
				{
					isDisabled = false;

					// search for the button:
					for (var j = 0; j < p_disabledButtons.length; j++)
					{
						// check for id:
						if (p_disabledButtons[j] == base + i)
						{
							isDisabled = true;
						} // if
					} // for i
				} // if

				if (!isDisabled)
				{
					buttonIdList[pos++] = (base + i);
				} // if
			} // if
			allowed >>>= 1;
		} // for
		blocks++;
		allowed = parseInt ("0x" + allowedButtons.charAt (blength - 1 - blocks));
	} // while

	// create the button bar:
	buttonBar = new ButtonBar (oid, "name", top.buttons);

	// add the buttons to the button bar:
	buttonBar.setAndLoadElements (p_availButtons, buttonIdList);

	// reinitialize the disabled buttons:
	p_disabledButtons = null;

	// create the history:
	createHistory (historyList, actHistoryIndex);
} // performCreateButtonBar

/**
 * void disableButtons (OIDString oid, StringArray disabledButtons)
 * Disable a list of buttons for a specific object.
 * This list is only used once.
 */
function disableButtons (oid, disabledButtons)
{
    // check if there are any buttons available:
    if (p_availButtons.length == 0)
    {
        // get available buttons:
        getAvailableButtons (null);
        // remember the buttons for disabling:
        p_oidForDisabling = oid;
        p_buttonsForDisabling = disabledButtons;
    } // if
    else
    {
        // loop through all button names and disable each of them:
        for (var i = 0; i < disabledButtons.length; i++)
        {
            disableButton (oid, disabledButtons[i]);
        } // for i
    } // else
} // disableButtons


/**
 * void disableButton (OIDString oid, int|String idName)
 * Disable a button for a specific object.
 * The button is defined through its id or name.
 */
function disableButton (oid, idName)
{
    var button = null;                  // the actual button object
    var buttonList = null;              // list of buttons

    // try to get the button by id:
    button = p_availButtons.get (idName);

    // check if we found the id:
    if (button != p_availButtons.notFound)
    {
        buttonList = new Array (button);
    } // if
    else
    {
        // try to find the button through its name:
        buttonList = p_availButtons.findAll (idName);
    } // else

    // check if we found the button:
    if (buttonList.length > 0)
    {
        // ensure that the disabled buttons array is initialized:
        if (p_disabledButtons == null)
        {
            p_disabledButtons = new Array ();
        } // if

        // get the button ids and add them to the disabled buttons:
        for (var i = 0; i < buttonList.length; i++)
        {
            p_disabledButtons[p_disabledButtons.length] = buttonList[i].id;
        } // for i
    } // if
} // disableButton

/**
 * void dropButtonBar ()
 * Drop the actual button bar.
 */
function dropButtonBar ()
{
    if (actButtonBar != null)
    {
        // drop all elements of the button bar:
        actButtonBar.empty ();
    } // if
    actButtonBar = null;                // drop the button bar itself
} // dropButtonBar


/**
 * void clearButtonBar ()
 * Clear the actual button bar.
 */
function clearButtonBar ()
{
    // ensure that there is no button bar defined:
    dropButtonBar ();

    // clear the button bar frame:
    callUrl (top.system.layoutDir + 'buttonsempty.htm', null, null, 'buttons');
} // clearButtonBar


/**
 * void cleanButtonBar ()
 * Clean the button bar frame.
 */
function cleanButtonBar ()
{
    if (actButtonBar != null)
        actButtonBar.clear ();
} // cleanButtonBar


/**
 * void addButton (String|int id, String name, String icon, String description,
 *                 String url)
 * Add a button to the available buttons. <BR>
 */
function addButton (id, name, icon, description, url)
{
    p_availButtons.add (new Button (id, name, icon, description, url));
} // addButton


/**
 * void buttonPressed (String|int id)
 * A button was pressed. <BR>
 */
function buttonPressed (id)
{
	// hide the possibly displayed search form
	top.scripts.hideSearchFrame(false);

    // call common function:
    singleButtonPressed (id, actButtonBar.id);
} // buttonPressed


/**
 * void singleButtonPressed (String|int id, OIDString oid)
 * A single button was pressed. <BR>
 */
function singleButtonPressed (id, oid)
{
    var button = p_availButtons.get (id); // the button

    // search for the actual button within the available buttons
    if (button != p_availButtons.notFound) // the button was found?
    {
        // perform the button function:
        top.scripts.callOidFunction (oid, button.url);
    } // if the button was found
} // singleButtonPressed


/**
 * void showSingleButton (id)
 * Show one single button at the actual position within the page.
 * This function is used to display a button within the entry of a discussion.
 */
function showSingleButton (oid, id, doc)
{
    var button = p_availButtons.get (id); // the button

    // search for the actual button within the available buttons:

    if (button != p_availButtons.notFound) // the button was found?
    {
        // display the button:
        button.showAsSingle (oid, doc);
    } // if the button was found
} // showSingleButton


/**
 * void showButtonsLoading ()
 * Display the loading state within the button bar.
 */
function showButtonsLoading ()
{
    if (actButtonBar && actButtonBar != null) // button bar exists?
    {
        // perform the function:
        actButtonBar.showLoading ();
    } // if button bar exists
} // showButtonsLoading


/**
 * String getVarContent (String varName)
 * Returns the content of the decriptions variables.
 * (btnDesc_search, btnDesc_searchContainer, btnDesc_searchAdvanced)
 */
function getVarContent (varName)
{
    try
    {
        var str;
        switch (varName)
        {
            case "s1":
                str = top.multilang.ibs_ibsbase_buttons_btnDesc_search;
            break;
            case "s2":
                str = top.multilang.ibs_ibsbase_buttons_btnDesc_searchContainer;
            break;
            case "s3":
                str = top.multilang.ibs_ibsbase_buttons_btnDesc_searchAdvanced;
            break;
            case "s4":
                str = top.multilang.ibs_ibsbase_buttons_btnDesc_searchBack;
            break;
            default:
                str = eval (varName);
            break;
        } // switch
        return (str);
    } // try
    catch (e)
    {
        alert (e.description);
        return "";
    } // catch
} // getVarContent


/**
 * void setActButtonBar (ButtonBar buttonBar)
 * Set a new actual button bar and display it.
 */
function setActButtonBar (buttonBar)
{
    // set the new button bar:
    actButtonBar = buttonBar;

    // display the actual button bar:
    actButtonBar.show (false);
} // setActButtonBar


/**
 * void getAvailableButtons (ButtonBar buttonBar)
 * Get info of all buttons from the server. This method just calls the
 * corresponding url at the server.
 */
function getAvailableButtons (buttonBar)
{
    // get all available buttons:
    loaderHandler.load ("availButtons", buttonBar, top.system.getBaseDir () + "scripts/buttons.html", "", "buttons", "buttons", "buttons", 5);
} // getAvailableButtons


/**
 * void buttonsLoaded ()
 * Tell the client application that all available buttons have been loaded to
 * the client.
 */
function buttonsLoaded ()
{
    loaderHandler.finishLoading ("availButtons");
} // buttonsLoaded


/**
 * void reloadButtons ()
 */
function reloadButtons ()
{
    // reset all available buttons:
    p_availButtons.empty ();
    
    // reload available buttons:
    //getAvailableButtons (p_availButtons);
} // reloadButtons
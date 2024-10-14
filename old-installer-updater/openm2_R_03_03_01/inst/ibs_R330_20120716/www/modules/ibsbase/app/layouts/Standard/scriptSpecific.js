/******************************************************************************
 * This file contains the scripts with the layout specific code, which is not
 * necessary to run at application startup. <BR>
 *
 * @version     $Id: scriptSpecific.js,v 1.37 2010/04/21 08:54:09 btatzmann Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */

//============= necessary classes and variables ===============================
// class Tuple
// class TupleList


//============= available properties, methods, and functions ==================
/*
available properties:
P01 this.id             the id of the actual object or list
P02 this.value          the object's value
P03 this.index          the index if the object is an element of a list
P04 this.frame          frame within to display an object (HTML)
    this.frame.document document within to display an object (HTML)
P05 this.length         number of elements within list
P06 this.akt            index of actual element within a list
P07 this.notFound       value of element if required element was not found
P08 this.name           the object's name
P09 this.imageActive    image shown for active button
P10 this.description    the object's description
P11 this.url            url of the object
P12 this.styleSheet     actual style sheet
P13 this.fct            function of the tab
P14 this.activeTab      the active tab
P15 this.width          width of tab
P16 this.height         height of tab
P17 this.classId        stylesheet class id of tab
P18 this.isActive       is the element active?

available methods:
M01 this.show (listId, doc[, additionalParam])
                            display the object within the document
M02 this.print ()           print the object or list (javascript alert window)
M04 this.find (value)       search for an element
M05 this.get (id)           get an element identified by its id
M06 this.getIndexed (index) get an element identified by its index
M07 this.first ()           get the first element
M08 this.next ()            get the next element
M09 this.prev ()            get the previous element
M10 this.last ()            get the last element
M11 this.add (object)       add a new element to a list
M12 this.replace (object)   replace element within a list
M13 this.drop (id)          drop element from list
M14 this.dropIndexed (index) drop element from list identified by its index
M15 this.show ()            display the list
M16 this.showElements ()    display the list's elements
M17 this.clear ()           clear the html document of the list
M18 this.showLoading ()     display the loading state within the button bar
M19 this.getActiveCode ()   get HTML code, if object is active
M20 this.getInactiveCode () get HTML code, if object is inactive

useful variables (may be used anywhere):
    top.system              system values
    top.system.layoutDir    directory (url) of the layout
    top.scripts.actMenuBar  the actual menu bar
    btn_reloadName          name of reload button
    btn_reloadDesc          description of reload button
useful functions (may be used anywhere):
    top.showState (stateText)   display message in the browser's state bar
    top.showObject (oidString)  open a specific business object
    top.scripts.callOidFunction (oidString, javascriptFct)
                                call javascript function for a specific object
                                (ex. oidString = "0x01010021000001F3")
                                (ex. javascriptFct = "top.loadCont(61);")
    showPageHeader (doc, stylesheet) show header of html page
    top.tabPressed (id, name, fct) inform the system that a tab was pressed
    top.toggleNode (index)      toggle the node of a menu bar (open/close)
    top.scripts.actMenuBar.scrollIn (index)
                                scroll to an element within the actual menu bar
    top.scripts.reloadMenuBar () reload the actual menu bar
    top.scripts.setNavTab (id)  set the actual navigation tab (group, private, ...)

function                P01 P02 P03 P08 P09 P10 P11 P13 P15 P16 P17 P18
showPageHeader          (no method, just a common function, no own properties)
Tuple                    x   x   x
Button_show              x   x   x   x   x   x   x
Button_showAsSingle      x   x   x   x   x   x   x
Tab_getActiveCode        x   x   x                   x
Tab_getInactiveCode      x   x   x                   x
Node_getCode             x   x   x                                   x
NavBarTab_getActiveCode  x   x   x   x       x           x   x   x
NavBarTab_getInactiveCodex   x   x   x       x           x   x   x
NavItem_show             x   x   x   x   x   x   x
NavItem_showHistory      x   x   x   x   x   x   x

function                P01 P04 P05 P06 P07 P08 P12 P14
TupleList                x   x   x   x   x
ButtonBar_show           x   x   x   x   x   x   x
ButtonBar_clear          x   x   x   x   x   x   x
ButtonBar_showLoading    x   x   x   x   x   x   x
TabBar_performShow       x   x   x   x   x       x   x
MenuBar_show             x   x   x   x   x
NavBar_show              x   x   x   x   x           x

function                M01 M02 M19 M20
showPageHeader          (no method, just a common function, no own methods)
Button_show              x   x
Button_showAsSingle      x   x
Tab_getActiveCode        x   x   x   x
Tab_getInactiveCode      x   x   x   x
NavBarTab_getActiveCode  x   x
NavBarTab_getInactiveCodex   x
Node_getCode             x   x
NavItem_show             x   x
NavItem_showHistory      x   x

function                M02 M04-M17 M18
ButtonBar_show           x     x     x
ButtonBar_clear          x     x     x
ButtonBar_showLoading    x     x     x
TabBar_performShow       x     x  
MenuBar_show             x     x  
NavBar_show              x     x  

*/

//============= declarations ==================================================

/**
 * Define, if the tab bar shall be shown if there is just one tab.
 * If set to true, the tabBar is shown independently of the number of tabs.
 * If set to false, the tabBar is just shown if number of tabs > 1.
 */
var showSingleTab = true;

/**
 * Kind of menu bar:
 * possible values:
 *      c_menu_MK_UNDEF                 // no defined menu kind
 *                                       // uses layout-specific menu bar
 *      c_menu_MK_TREE                  // menu kind tree
 *      c_menu_MK_TREE_LINED            // menu kind tree with lines
 *      c_menu_MK_WHEEL                 // menu kind wheel
 * The variable v_menuKind is already defined and has only be set to a new
 * value.
 */
v_menuKind = c_menu_MK_TREE_LINED;

/**
 * Pattern for the function setFrameHeight when showing the search frame.
 */
v_showSearchFramePattern = "30,30,<size>,*";

/**
 * Pattern for the function setFrameHeight when hiding the search frame.
 */
v_hideSearchFramePattern = "30,30,*,<size>";

/**
 * Relevant button ids
 */
var btn_gotoContainer = 32;
var btn_back = 33;
var btn_goforward = 66;

var historyDontBlur = false;					// used to deactivate the history selection box blur functionality

//============= common representation functions ===============================

/**
 * void showPageHeader (DocumentObject doc[, String stylesheet])
 * Show header of html page. Include necessary style sheet.
 */
function showPageHeader (doc, stylesheet)
{
    if (doc)                            // document exists?
    {
        doc.write (
            '<!DOCTYPE HTML protected "-//W3C//DTD HTML 3.2 Final//EN">' +
            '<HTML><META HTTP-EQUIV="Pragma" CONTENT="no-cache">\n' +
            '<HEAD>');

        // include the stylesheet in the document:
        showStylesheet (doc, stylesheet);

        doc.write ('</HEAD>\n');
    } // if document exists
} // showPageHeader


//============= representation functions for buttons ==========================

/**
 * void Button_show (OidString actOid, DocumentObject doc)
 * Display a button.
 */
function Button_show (actOid, doc)
{
	// Do not render the back and gotoContainer buttons within this layout
	// Perhaps this can also be done by a disable button function. If a better solution
	// can be found. This can be replaced.
	if(this.id == btn_gotoContainer || this.id == btn_back || this.id == btn_goforward)
	{
		return;
	} // if it is a button which is know displayed within the history panel (see displayHistoryPanel()) 
	
    doc.write (
        '<NOBR><BUTTON ONCLICK="disableBtn (this); top.buttonPressed (' + this.id + ');" ' +
        'onMouseOver="return top.showState (\'' + this.description + '\')" CLASS="description">' +
        this.name + '</BUTTON></NOBR>');
} // Button_show


/**
 * void Button_showAsSingle (OidString actOid, DocumentObject doc)
 * Display a button not within a button bar, but as a single button.
 */
function Button_showAsSingle (actOid, doc)
{
    doc.write (
        '&nbsp;&nbsp;&nbsp;<NOBR><A HREF="javascript:top.singleButtonPressed (' + this.id + ', \'' + actOid + '\');" ' +
        'onMouseOver="return top.showState (\'' + this.description + '\')" CLASS="description" TARGET="searchFrame">' +
        this.name + '</A></NOBR>' +
        '&nbsp;');
} // Button_showAsSingle


/**
 * void ButtonBar_show ()
 * Display a button bar.
 */
function ButtonBar_show ()
{
    var doc = this.frame.document;

    doc.open ();
    showPageHeader (doc, 'styleButtonBar.css');
    doc.write (
        '<BODY>' +
        '<SCRIPT LANGUAGE="Javascript">\n' +
        '<!--\n' +
        'var buttonElem;' +
        'var buttonLabel;' +
        'function disableBtn (elem)' +
        '{' +
            'buttonElem = elem;' +
            'buttonLabel = elem.innerHTML;' +
            'elem.innerHTML = "<IMG SRC=\\"' +
                top.system.layoutDir + 'images/buttons/loading.gif\\"' +
                ' ALIGN=\\"ABSMIDDLE\\"/>&nbsp;" + buttonLabel;' +
            'elem.disabled = true;' +
            'var timer = setTimeout ("enableBtn ();", 1000);' +
        '}' +
        'function enableBtn ()' +
        '{' +
            'if (buttonElem != null)' +
            '{' +
                'buttonElem.innerHTML = buttonLabel;' +
                'buttonElem.disabled = false;' +
            '}' +
        '}\n' +
        '//-->\n' +
        '</SCRIPT>\n' +
        '<TABLE BORDER="0" WIDTH="99%"><TR><TD>');
    
    // display the history:
	var goUpButton = this.get (btn_gotoContainer);
	displayHistoryPanel (doc, this.get (btn_back), this.get (btn_goforward), goUpButton,
			// (goUpButton != this.notFound) did not work so (goUpButton.id != "TUPLE_NOT_FOUND") was used
			this.get (btn_back) != this.notFound, this.get (btn_goforward) != this.notFound, goUpButton.id != "TUPLE_NOT_FOUND");

    // display the buttons:
	this.showElements ();

    if (this.length > 0)                // at least one element?
    {
        doc.write (' &nbsp;&nbsp; ');          // finish the button bar
    } // if at least one element

    doc.write ('</TD></TR></TABLE></BODY></HTML>');
    doc.close ();
} // ButtonBar_show


/**
 * void ButtonBar_clear ()
 * Clear a button bar.
 */
function ButtonBar_clear ()
{
    var doc = this.frame.document;

    doc.open ();
    showPageHeader (doc, 'styleButtonBar.css');
    doc.write ('<BODY>&nbsp;</BODY></HTML>');
    doc.close ();
} // ButtonBar_clear


/**
 * void ButtonBar_showLoading ()
 * Show the loading state within the button bar.
 */
function ButtonBar_showLoading ()
{
    var doc = this.frame.document;

    doc.open ();
    showPageHeader (doc, 'styleButtonBar.css');
    doc.write (
        '<BODY>' +
        '<DIV ALIGN=RIGHT>' +
        '<TABLE BORDER="0" CELLPADDING="0" CELLSPACING="0" HEIGHT="100%">' +
        '<TR><TD VALIGN="TOP">' +
        '<IMG SRC="' + top.system.layoutDir + 'images/global/waiting.gif"' +
            ' ALT="loading...">' +
        '</TR></TD></TABLE>' +
        '</DIV>' +
        '</BODY></HTML>');
    doc.close ();
} // ButtonBar_showLoading


//============= representation functions for tabs =============================
/**
 * void Tab_getActiveCode ()
 * Get the HTML code for an active tab.
 * An active tab should be displayed in a non clickable way.
 */
function Tab_getActiveCode ()
{
    return (
        '<TD HEIGHT="25" CLASS="nameFront" TITLE="' + this.description + '">' +
        '<A HREF="JavaScript:top.tabPressed (' + this.id + ');"' +
        ' onMouseOver="return top.showState (\'' + this.name + '\')"' +
        ' CLASS="nameFront">' +
        this.name +
        '</A></TD>');
} // Tab_getActiveCode


/**
 * void Tab_getInactiveCode ()
 * Get the HTML code for an inactive tab.
 * An inactive tab should be displayed in a clickable way.
 */
function Tab_getInactiveCode ()
{
    return (
        '<TD HEIGHT="25" VALIGN="TOP" ALIGN="CENTER" CLASS="nameBack" TITLE="' + this.description + '">' +
        '<A HREF="JavaScript:top.tabPressed (' + this.id + ');"' +
        ' onMouseOver="return top.showState (\'' + this.name + '\')"' +
        ' CLASS="nameBack">' +
        this.name +
        '</A></TD>')
} // Tab_getInactiveCode


/**
 * void TabBar_performShow ()
 * Display a tab bar.
 */

function TabBar_performShow ()
{
    var doc = this.frame.document;

    doc.open ();
    showPageHeader (doc, this.styleSheet);
    doc.write ('<BODY><TABLE><TR>\n');

    // display the tabs:
    this.showElements ();

    doc.write ('</TR></TABLE></BODY></HTML>');
    doc.close ();
} // TabBar_performShow




//============= representation functions for the menu bar =====================

// not necessary because we are using a standard menu bar mechanism.


//============= representation functions for the navigation bar ===============

/**
 * void NavBarTab_getActiveCode ()
 * Get the HTML code for an active navigation bar tab.
 * An active tab should be displayed in a non clickable way.
 */
function NavBarTab_getActiveCode ()
{
    return (
        '<TD VALIGN="TOP" ALIGN="CENTER"' +
//        ' WIDTH="' + this.width + '" HEIGHT="' + this.height + '"' +
        ' onClick="top.scripts.setNavTab (\'' + this.id + '\');"' +
        ' CLASS="' + this.classId + '">' +
//        '<FONT SIZE=-3>&nbsp;<BR/></FONT>' + this.description +
        '<FONT SIZE=-3>&nbsp;</FONT>' +
        '<A HREF="javascript:top.scripts.setNavTab (\'' + this.id + '\');"' +
        ' onMouseOver="return top.showState (\'' + this.name + '\');">' +
        this.description + '</A>' +
        '</TD>');
} // NavBarTab_getActiveCode


/**
 * void NavBarTab_getInactiveCode ()
 * Get the HTML code for an inactive navigation bar tab.
 * An inactive tab should be displayed in a clickable way.
 */
function NavBarTab_getInactiveCode ()
{
    return (
        '<TD VALIGN="TOP" ALIGN="CENTER"' +
//        ' WIDTH="' + this.width + '" HEIGHT="' + this.height + '"' +
        ' onMouseOut="this.className=\'' + this.classId + '\';" ' +
        ' onMouseOver="this.className=\'high\';" ' +
        ' onClick="top.scripts.setNavTab (\'' + this.id + '\');"' +
        ' CLASS="' + this.classId + '">' +
        '<FONT SIZE=-3>&nbsp;</FONT>' +
        '<A HREF="javascript:top.scripts.setNavTab (\'' + this.id + '\');"' +
        ' onMouseOver="return top.showState (\'' + this.name + '\');"' +
        '">' +
        this.description + '</A>' +
        '</TD>');
} // NavBarTab_getInactiveCode


/**
 * void NavBar_show ()
 * Display a navigation bar.
 */
function NavBar_show ()
{
    var doc = this.frame.document;      // the document

    doc.open ();
    showPageHeader (doc, 'styleNavBar.css');
    doc.write (
        '<BODY>' +
        '<TABLE BORDER="0" CELLPADDING="0" CELLSPACING="0" CLASS="none">' +
        '<TR>\n');

    // loop through all tabs:
    this.showElements ();
    doc.writeln ('</TR></TABLE></BODY></HTML>');
    doc.close ();
} // NavBar_show


/**
 * NavItem_show (oidString actOid, DocumentObject doc)
 * Display a single navigation item.
 */
function NavItem_show (actOid, doc)
{
    doc.write (
        '<A HREF="javascript:top.navItemPressed (' + this.id + ', \'' + actOid + '\');" ' +
        'onMouseOver="return top.showState (\'' + this.description + '\')" CLASS="goto">' +
        '<IMG SRC="' + top.system.layoutDir + 'images/header/' + this.imageActive + '"' +
        'ALT="' + this.description + '" BORDER="0"></A>');
} // NavItem_show


/**
 * NavItem_showHistory (oidString actOid, DocumentObject doc)
 * Display a history navigation item.
 */
function NavItem_showHistory (actOid, doc)
{
    doc.write (
        '<A HREF="javascript:top.navItemPressed (' + this.id + ', \'' + actOid + '\');" ' +
        'onMouseOver="return top.showState (\'' + this.description + '\')" CLASS="hist">' +
        '<IMG SRC="' + top.system.layoutDir + 'images/header/' + this.imageActive + '"' +
        'ALT="' + this.description + '" BORDER="0"></A>');
} // NavItem_showHistory


/**
 * void createHistory ()
 * Renders the necessary elements for displaying the history list.
 */
function createHistory (list, actIndex)
{
    if (list !== undefined && list !== null)
    {
        if (actIndex === undefined || actIndex === null)
        {
            actIndex = 0;
        } // if
      
        var histElem = getHistoryElement ();

        // Holds the number of the option corresponding to the actIndex
        var actOption = list.length - actIndex -1;
        
        var content = "<SELECT size=\"" + list.length +
        	"\" id=\"historyElemSelection\" ONBLUR=\"if(!top.scripts.historyDontBlur) {top.scripts.hideHistory (this.parentNode);}\" " +
        	"ONCHANGE=\"top.scripts.hideSearchFrame(false); if(this.value >= 0) {top.goforward(this.value);} " +
        	"else if (this.value < 0) {top.goback(-this.value);} " +
        	// hide the history within this frame
        	"top.scripts.hideHistory(this.parentNode);"+
        	// set to history dont blur to avoid, that the onblur event is called when the frame changes
        	// an the user clicks on another item in the other frame
        	"top.scripts.historyDontBlur = true; \">";
        
        for (var i = list.length - 1; i >= 0; i--)
        {
            var elem = list[i];
            content += "<OPTION TITLE=\"Öffne " + elem.name + " (" + elem.type + ")\" VALUE=\"" + (i - actIndex) + "\"";
            if (i == actIndex)
            {
                content += " class=\"activeIndex\"";
            } // if
            content += ">" + elem.name + " (" + elem.type + ")</OPTION>";
        } // for i
        content += "</SELECT>";

        histElem.innerHTML = content;
    } // if
} // createHistory

/**
 * Renders the history button panel.
 * 
 * @param doc
 * @param backEnabled Defines if the back button should be enabled
 * @param forwardEnabled Defines if the forward button should be enabled
 * @return
 */
function displayHistoryPanel (doc, backButton, forwardButton, goUpButton,
		backEnabled, forwardEnabled, goUpEnabled)
{
	//doc.write ('<FIELDSET>');
	//doc.write ('<LEGEND>History</LEGEND>');
	//doc.write ('<TABLE>');
	
	var disabled = "";
	
	if(!backEnabled)
	{
		disabled = " disabled=\"disabled\"";
	} // if
	
	var description = backButton.description; 
	
	if(description == undefined)
	{
		description = '';
	} // if
	
	doc.write ('<BUTTON' + disabled + ' ONCLICK="var goBack = (top.scripts.computeFrameHeight(top.sheet) != 0); top.scripts.hideSearchFrame(false); if(goBack) {top.goback(1);} else {top.goback(0);}" CLASS="history">');
	doc.write ('<IMG NAME="back" SRC="' + top.system.layoutDir + 'images/header/navBack.gif"');
	doc.write ('ALT="' + description + '" ALIGN="absmiddle">');
	doc.write ('</BUTTON>');

	disabled = "";
	
	if(!forwardEnabled)
	{
		disabled = " disabled=\"disabled\"";
	} // if
	
	var description = forwardButton.description; 
	
	if(description == undefined)
	{
		description = '';
	} // if
	
	doc.write ('<BUTTON' + disabled + ' ONCLICK="top.scripts.hideSearchFrame(false); top.goforward (1);" CLASS="history">');
	doc.write ('<IMG NAME="forward" SRC="' + top.system.layoutDir + 'images/header/navForward.gif"');
	doc.write ('ALT="' + description + '" ALIGN="absmiddle">');
	doc.write ('</BUTTON>');
	
	disabled = "";

	if(!(backEnabled || forwardEnabled))
	{
		disabled = " disabled=\"disabled\"";
	} // if
	
	doc.write ('<BUTTON ' + disabled +
			' id="histButton" ONCLICK="top.scripts.historyDontBlur = false;' +
			// check if the history has not be closed by the onblur event just before
			// in that case it should not be opened again until a time limit of 200ms
			' var shown = top.scripts.toggleHistory ();' +
			' if(shown && top.scripts.getHistoryFrame().document.getElementById (\'historyElemSelection\') != null) {' +
			' top.scripts.getHistoryFrame().document.getElementById (\'historyElemSelection\').focus ();}"' +
			' CLASS="history">');
	doc.write ('<IMG NAME="forward" SRC="' + top.system.layoutDir + 'images/header/navHistory.gif"');
	doc.write ('ALT="Verlauf" ALIGN="absmiddle">');
	doc.write ('</BUTTON>');
	
	disabled = "";

	if(!goUpEnabled)
	{
		disabled = " disabled=\"disabled\"";
	} // if
	
	var description = goUpButton.description; 
	
	if(description == undefined)
	{
		description = '';
	} // if

	doc.write ('&nbsp;<BUTTON ' + disabled +
			' id="histButton" ONCLICK="javascript:top.buttonPressed (' + goUpButton.id + ');" CLASS="goUp">');
	doc.write ('<IMG NAME="forward" SRC="' + top.system.layoutDir + 'images/header/navUp.gif"');
	doc.write ('ALT="' + description + '" ALIGN="absmiddle">');
	doc.write ('</BUTTON>&nbsp;');	
	
	
	//doc.write ('</TR></TABLE>');
	//doc.write ('</FIELDSET>');
} // displayHistoryPanel

/**
 * frame getHistoryFrame()
 * 
 * This method overrides the method within scriptCommon because the button bar is at the top of
 * the frameset within the standard layout.
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
       		return top.sheet.sheet1;
    	} // top.sheet has subframes
    	else
    	{
    		return top.sheet;
    	} // no subframes
    } // else frame is active
} // getHistoryFrame
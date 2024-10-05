/******************************************************************************
 * This file contains all classes, methods, and global variables needed for
 * the menu bar and navigation bar functionality. <BR>
 *
 * @version     $Id: scriptMenuBar.js,v 1.22 2012/07/09 12:11:58 btatzmann Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */


//============= declared classes and functions ================================
// class MenuBarNode
// class MenuBar

// function toggleNode
// function loadMenuBar
// function reloadMenuBar
// function menu_getCallParamsNextIndex
// function menu_addCall
// function menu_startCalls
// function menu_performCalls
// function menu_setSubNodesInvisible
// function menu_setElementVisible
// function menu_setElementInvisible
// function menu_displayNode
// function menu_help


//============= necessary classes and variables ===============================
// class HierarchyNode
// class Hierarchy

// var loaderHandler
// var actNavBar


//============= declarations ==================================================

// constants:
// messages:

// images:
var IMG_OPEN = menu_createMenuImage ("menu_open.gif", "<--", toggleNode);
var IMG_CLOSED =
    menu_createMenuImage ("menu_closed.gif", "-->", toggleNode);
var IMG_BRANCH = menu_createMenuImage ("menu_branch.gif");
var IMG_LOADING = menu_createMenuImage ("menu_loading.gif", "Loading...");
    IMG_LOADING.srcEnd = IMG_LOADING.src;
var IMG_DISABLED = menu_createMenuImage ("menu_disabled.gif");

var ID_TREELOADING_IMAGE = "treeLoadingImg";

// layer styles:
var lay_VISIBILITY_HIDDEN = "none";
var lay_VISIBILITY_VISIBLE = "inline"; // block
// call kinds:
var v_menu_ck = 0;
var c_menu_CK_UNDEF = v_menu_ck++;
var c_menu_CK_CALLFCT = v_menu_ck++;
var c_menu_CK_SHOWNODE = v_menu_ck++;
var c_menu_CK_SETTOGGLEICON = v_menu_ck++;
var c_menu_CK_SETTOGGLEICONS = v_menu_ck++;
var c_menu_CK_SCROLLIN = v_menu_ck++;
var c_menu_CK_SHOWELEMENTS = v_menu_ck++;
var c_menu_CK_CHANGEIMAGE = v_menu_ck++;

// variables:

// actual menu bar:
var actMenuBar = null;
var lastMenuBar = null;
var v_nodeGetCode = null;               // method for getting the node code
var v_nodeGetCodePrefix = null;         // get prefix for the current node
var v_menuBarShow = null;               // method for displaying the menu bar
var v_menuBarGetStyles = null;          // method for getting styles for the
                                        // menu bar
var v_callList = new Array ();
var v_timer = 0;


//============= initializations ===============================================


//============= class MenuBarNode =============================================

/**
 * void MenuBarNode_childInserted (MenuBarNode childNode)
 * Event handler which is called after a new child was inserted.
 */
function MenuBarNode_childInserted (childNode)
{
    // ensure that the new node is displayed if necessary:
    if (this.hasChildNodes && this.divSub != null && (this.isOpen || this.level == 0))
    {
        // display the node (do not scroll into the node):
        childNode.show (null, this.divSub, false, false);
    } // if
} // MenuBarNode_childInserted


/**
 * void MenuBarNode_show (DocumentObject doc, DivElement parentDiv,
 *                        boolean isScrollIn, boolean isFirst,
 *                        boolean isUpdateData)
 * Display a node within a menu row.
 */
function MenuBarNode_show (doc, parentDiv, isScrollIn, isFirst, isUpdateData)
{
    var act = null;                     // the actual node
    var scrollingPossible = false;      // is scrolling possible?

    if (this != null && this.isValid && this.isVisible) // node exists?
    {
        // check if there was a document set:
        if (doc == null && this.frame != null)
        {
            doc = this.frame.document;
        } // if

        if (this.div == null)           // node was not displayed yet?
        {
            var idWholeNode = "div_" + this.id;
            var idNodeData = "own_" + this.id;
            var idName = "name_" + this.id;
            var idNameLink = "namel_" + this.id;
            var idToggleIcon = "toggle_" + this.id;
            var idSubData = "sub_" + this.id;
			var idPrefix = "pref_" + this.id;
            var objectFct = "showObject";
            var myDiv = null;

            // check if we have a document:
            if (doc == null)
            {
                // we cannot work without a document:
                return;
            } // if

            if (!this.isActive)         // not active ?
            {
                objectFct = "offShowObject";
            } // if not active

            // check if there is a DIV element defined within to show the node:
            if (parentDiv == null)
            {
                parentDiv = doc;
            } // if

            // compute the menu row and put it into the document:
            myDiv = doc.createElement ("LI");
            myDiv.setAttribute ("ID", idWholeNode);
            myDiv.setAttribute ("CLASS", "node");
            myDiv.innerHTML =
                this.getCode (idNodeData, idName, idNameLink, idToggleIcon,
                              idSubData, idPrefix);
            // display the node as first or last node within parent:
            menu_displayNode (myDiv, isFirst, parentDiv, this.nextSibling);

            this.div = myDiv;
            // get the node's DIV elements:
//            this.divData = doc.getElementById (idNodeData);
            this.divName = doc.getElementById (idName);
            this.divSub = doc.getElementById (idSubData);
            this.toggleIcon = doc.getElementById (idToggleIcon);
            nameLink = doc.getElementById (idNameLink);
            nameLink.href =
                "javascript:top." + objectFct + "('" + this.id + "')";
            nameLink.title =
            	top.multilang.ibs_ibsbase_scripts_MSG_SHOWMENUOBJECT + this.nodeName;
            nameLink.node = this;

            // ensure that the toggle icon object is present:
            if (!this.toggleIcon || this.toggleIcon == null)
            {
                // create a dummy toggle icon:
                this.toggleIcon = doc.createElement ("IMG");
            } // if

            // initialize the toggle icon:
            this.toggleIcon.node = this;
            this.setToggleIcon ();

            // hide the node if it is not visible:
            if ((!this.isValid || !this.isVisible) && this.div != null)
            {
                menu_setElementInvisible (this.div);
            } // if
            menu_setElementInvisible (this.divSub);

            scrollingPossible = true;
        } // if sub node container does not exist
        else                            // node already displayed
        {
            // ensure that the node data are correctly displayed:
            if (isUpdateData)
            {
                // update name div:
                this.divName.replaceChild(document.createTextNode(this.nodeName), this.divName.firstChild);
                this.setToggleIcon ();
            } // if

            // check if the node has to be moved:
            if (parentDiv != null && this.div.parentNode != parentDiv)
                                    // the node has to be moved?
            {
                // display the node as first or last node within parent:
                menu_displayNode (this.div, isFirst, parentDiv,
                                  this.nextSibling);

				// update the prefixes for the current node, which is necessary if the level has changed:
				this.updatePrefixes (doc);
            } // else if the node has to be moved

            if (this.isValid && this.isVisible) // node is visible?
            {
                if (this.isOpen)        // the node is open?
                {
                    if (this.isChildNodesDisplayed)
//                    if (this.divSub.hasChildNodes ())
                                        // node is open and already filled?
                    {
                        menu_setElementVisible (this.divSub);
                        this.setToggleIcons (IMG_OPEN);
                        scrollingPossible = true;
                    } // if node is open and already filled
                    else                // the node is open but not filled?
                    {
                        menu_addCall (0, c_menu_CK_SETTOGGLEICON,
                            new Array (this, IMG_LOADING));
                        menu_startCalls ();
/*
                        this.toggleIcon.src = IMG_LOADING.src;
                        this.toggleIcon.title = IMG_LOADING.title;
*/
                        menu_addCall (0, c_menu_CK_CALLFCT,
                            new Array (menu_setElementVisible, this.divSub));
                        menu_startCalls ();

                        // also show the nodes below:
                        // check if we already have the data of the child nodes:
                        if (this.firstChild != null) // child node data present?
                        {
                            // loop through all child nodes and show each one:
                            for (var iter = this.iterator (); iter.hasNext ();)
                            {
                                // get the actual node and show it:
                                menu_addCall (0, c_menu_CK_SHOWNODE,
                                    new Array (iter.next (), doc, this.divSub,
                                               false, false, "1"));
//                                iter.next ().show (doc, this.divSub, false);
                            } // for iter

                            menu_addCall (0, c_menu_CK_SCROLLIN,
                                new Array (this.lastChild));

                            menu_addCall (0, c_menu_CK_SCROLLIN,
                                new Array (this));

                            menu_addCall (0, c_menu_CK_SETTOGGLEICON,
                                new Array (this, IMG_OPEN));

                            scrollingPossible = true;
                        } // if child node data present
                        else            // no child node data
                        {
                            // call url for loading the subnodes:
                            this.isScrollIn = isScrollIn;
                            loadMenuBar (actMenuBar, this);
                        } // else no child node data

                        menu_startCalls ();
                        this.isChildNodesDisplayed = true;
                    } // else if the node is open but not filled
                } // if the node is open
                else                    // the node is closed
                {
                    this.setToggleIcon ();
                    menu_setElementInvisible (this.divSub);
                    scrollingPossible = true;
                } // else the node is closed
            } // if node is visible
            else                        // node is not visible
            {
                if (this.div != null)
                {
                    menu_setElementInvisible (this.div);
                } // if
            } // else node is not visible
        } // else node already displayed

        if (scrollingPossible && (isScrollIn || this.isScrollIn))
        {
            this.scrollIn ();
        } // if
    } // if node exists
} // MenuBarNode_show


/**
 * void MenuBarNode_remove (DocumentObject doc, DivElement parentDiv)
 * Remove a node within a menu row.
 */
function MenuBarNode_remove (doc, parentDiv)
{
	if (this.div != null && this.div.parentNode != null)
	{
		this.div.parentNode.removeChild (this.div);
	} // if

	// IBS-793: only hidding removed nodes causes problems on dom tree handling when the node gets assigned to another html element
	// set removed nodes invisible
	//menu_setElementInvisible (this.div);
} // MenuBarNode_remove


/**
 * void MenuBarNode_showData ()
 * Redisplay the data of the node.
 */
/*
function MenuBarNode_showData ()
{
    if (this.divData != null)
    {
        var idWholeNode = "div_" + this.index;
        var idNodeData = "own_" + this.index;
        var idSubData = "sub_" + this.index;

        // check if we have a document:
        if (doc == null)
        {
            // we cannot work without a document:
            return;
        } // if

        // check if there is a DIV element defined within to show the node:
        if (div != null)
        {
            // compute the menu row and print it:
            var myDiv = doc.createElement ("LI");
            myDiv.setAttribute ("ID", idWholeNode);
            myDiv.innerHTML = this.getCode (idNodeData, idSubData);
            div.appendChild (myDiv);
//            div.innerHTML += this.getCode ();
        } // if
        else
        {
            // compute the menu row and print it:
            doc.write (
                '<DIV ID="' + idWholeNode + '">' +
                this.getCode (idNodeData, idSubData) +
                '</DIV>');
        } // else

        // get the node's DIV elements:
        this.div = doc.getElementById (idWholeNode);
        this.divData = doc.getElementById (idNodeData);
        this.divSub = doc.getElementById (idSubData);

        if (this.hasChildNodes)
        {
            this.toggleIcon =
                doc.getElementById ("toggleIcon" + this.index);
        } // if

        if (!this.isVisible && this.div != null)
        {
            menu_setElementInvisible (this.div);
        } // if

        menu_setElementInvisible (this.divSub);
    } // if
} // MenuBarNode_showData
*/


/**
 * void MenuBarNode_print ()
 * Show node definition.
 */
function MenuBarNode_print ()
{
    alert (this.getCode ());            // get definition and show it
} // MenuBarNode_print


/**
 * String MenuBarNode_getCodeTree (String idNodeData, String idName,
 *                                 String idNameLink, String idToggleIcon,
 *                                 String idSubData, String idPrefix)
 * Create HTML code with menu format containing the data for a menu node.
 * Used for displaying a menu tree.
 */
function MenuBarNode_getCodeTree (idNodeData, idName, idNameLink, idToggleIcon,
                                  idSubData, idPrefix)
{
    var row = '';                       // the row itself
    var nameStr = '';                   // the part with the name

    // start with the row:
    row =
        '<SPAN ID="' + idNodeData + '" CLASS="l' + this.level + '"' +
        ' STYLE="white-space: nowrap;">' +
		'<SPAN ID="' + idPrefix + '">' +
        this.getCodePrefix () +
		'</SPAN>';

    // create toggle open/close of actual node:
    row += '<IMG ID="' + idToggleIcon + '" BORDER="0" ALIGN="ABSMIDDLE"/>';
//           ' onMouseOver="return top.showState (\'' + title + '\')">' +

    // create icon and name within link to object:
    nameStr =
        '<A ID="' + idNameLink + '"' +
        ' onMouseOver="return top.showState (top.scripts.escapeString (this.node.nodeName));">' +
        '<IMG SRC="images/objectIcons/' + this.icon + '" BORDER="0" ALIGN="ABSMIDDLE"/>' +
//       '<IMG SRC="images/menu/menu_space.gif" BORDER="0"/>' +
        '&nbsp;' +
        '<SPAN ID="' + idName + '">' + this.nodeName + '</SPAN></A>';

    if (!this.isActive)                 // not active?
    {
        nameStr = '<STRIKE>' + nameStr + '</STRIKE>';
    } // if not active

    // finish the row:
    row += nameStr +
        '</SPAN>' +
        '<UL ID="' + idSubData + '" CLASS="sub">\n' +
        '</UL>\n' +
        '';

    return (row);                       // return the computed row
} // MenuBarNode_getCodeTree


/**
 * String MenuBarNode_getCodePrefixTree ()
 * Create HTML code with the prefix for the node within a menu tree.
 * This is the standard version for trees.
 */
function MenuBarNode_getCodePrefixTree ()
{
    // nothing to do, just return empty string:
    return ("");
} // MenuBarNode_getCodePrefixTree


/**
 * String MenuBarNode_getCodePrefixTreeLined ()
 * Create HTML code with the prefix for the node within a menu tree.
 * This is the version for trees with lines.
 */
function MenuBarNode_getCodePrefixTreeLined ()
{
    var prefix = "";                    // the prefix

    // loop through all upper nodes and check if there shall be a line
    // displayed:
    for (var node = this.parentNode;
         node.parentNode != null;
         node = node.parentNode)
    {
        prefix =
            '<IMG SRC="' + ((node.nextSibling != null) ?
                IMG_DISABLED.src : IMG_DISABLED.srcEnd) + '"' +
            ' BORDER="0" ALIGN="ABSMIDDLE">' + prefix;
    } // for node

    // return the computed prefix:
    return ("&nbsp;" + prefix);
} // MenuBarNode_getCodePrefixTreeLined


/**
 * void MenuBarNode_scrollIn ()
 * Ensure that the node is correctly shown in the window.
 */
function MenuBarNode_scrollIn ()
{
    var divElem = this.div;

    if (divElem != null)                // the element to be displayed exists?
    {
        var frame = this.frame;
        if (top.system.browser.ie)
        {
            frame = divElem.document.parentWindow;
        } // if

        // get frame position values:
        var frameTop = top.scripts.computeFramePosY (frame);
        var frameHeight = top.scripts.computeFrameHeight (frame);
        var frameBottom = frameTop + frameHeight;
        // get element position values:
        var topOffset = top.scripts.computeTopOffset (divElem);
        var height = top.scripts.computeHeight (divElem);
        var bottomOffset = topOffset + height;
        // scrolling values:
        var posY = frameTop;
        var isScrollToTop = true;
//alert (frameTop + "\t" + frameBottom + "\n" + topOffset + "\t" + bottomOffset);

        // check if there has to be scrolled:
        if (topOffset < frameTop || bottomOffset > frameBottom)
                                        // scrolling necessary?
        {
            // compute the position to scroll to:
            if (topOffset < frameTop || height > frameHeight) // scroll up?
            {
                posY = topOffset;
                isScrollToTop = true;
            } // if scroll up
            else                        // scroll down to element end
            {
                posY = topOffset + height - frameHeight;
                isScrollToTop = false;
            } // else scroll down to element end

            // scroll to the computed position:
            if (top.system.browser.ie || top.system.browser.firefox)
            {
                divElem.scrollIntoView (isScrollToTop);
            } // if
            else
            {
                frame.scrollTo (top.scripts.computeFramePosX (frame), posY);
            } // else
        } // if scrolling necessary
    } // if the element to be displayed exists

    // no further scrolling necessary:
    this.isScrollIn = false;
} // MenuBarNode_scrollIn


/**
 * void MenuBarNode_setToggleIcon ([Image img])
 * Set the toggle icon of the node.
 * If defined the img is set for the current node.
 */
function MenuBarNode_setToggleIcon (img)
{
    // check if the icon is set:
    if (this.toggleIcon != null)
    {
        // check if we already have the image:
        if (img == null)
        {
            // get the image:
            img = this.getToggleIcon ();
        } // if

        // check if there are more nodes after this one and set toggleIcon:
        if (this.nextSibling == null)
        {
            this.toggleIcon.src = img.srcEnd;
        } // if
        else
        {
            this.toggleIcon.src = img.src;
        } // else

        // set title for the icon:
        this.toggleIcon.title = img.title;
        // set onclick event:
        this.toggleIcon.onclick = img.onclick;
    } // if
} // MenuBarNode_setToggleIcon


/**
 * Image MenuBarNode_getToggleIcon ([Image img])
 * Get the image for the toggle icon of the node.
 * If defined the img is returned unchanged.
 */
function MenuBarNode_getToggleIcon (img)
{
    // check if an image was defined:
    if (img == null)
    {
        if (this.hasChildNodes)
        {
            // compute the image out of the node's state:
            if (this.isOpen)            // the node is open?
            {
                img = IMG_OPEN;
            } // if the node is open
            else                        // the node is closed
            {
                img = IMG_CLOSED;
            } // else the node is closed
        } // if
        else
        {
            img = IMG_BRANCH;
        } // else
    } // if

    // return the result:
    return (img);
} // MenuBarNode_getToggleIcon


/**
 * void MenuBarNode_setToggleIcons ([Image img])
 * Set the toggle icons of the node and all nodes which are directly below.
 * If defined the img is set for the current node, but never for the sub nodes.
 */
function MenuBarNode_setToggleIcons (img)
{
    // set toggle icon for current node:
    this.setToggleIcon (img);

    // set toggle icon for all child nodes:
    for (var iter = this.iterator (); iter.hasNext ();)
    {
        // set toggle icon to current value:
        iter.next ().setToggleIcon ();
    } // for iter
} // MenuBarNode_setToggleIcons


/**
 * void MenuBarNode_updatePrefixes (DocumentObject doc)
 * 
 * Update the current node's prefix SPAN element and for recursively for all child nodes.
 */
function MenuBarNode_updatePrefixes (doc)
{
    // set toggle icon for current node:
	this.updatePrefix (doc);

    // set toggle icon for all child nodes:
    for (var iter = this.iterator (); iter.hasNext ();)
    {
        // set toggle icon to current value:
        iter.next ().updatePrefixes (doc);
    } // for iter
} // MenuBarNode_updatePrefixes


/**
 * void MenuBarNode_updatePrefix (DocumentObject doc)
 * 
 * Update the current node's prefix SPAN element.
 */
function MenuBarNode_updatePrefix (doc)
{
    // set toggle icon for current node:
	var idPrefix = "pref_" + this.id;
	var prefixRef = doc.getElementById (idPrefix);
	
	if (prefixRef != null)
	{
		prefixRef.innerHTML = this.getCodePrefix ();
	} // if
} // MenuBarNode_updatePrefix


/**
 * String MenuBarNode_toString ()
 * Create String representation.
 */
function MenuBarNode_toString ()
{
    return (this.constructor._inheritance[0].prototype.toString2.call (this) +
            ", isOpen=" + this.isOpen + ", isVisible=" + this.isVisible +
            ", div=" + this.div + ", divSub=" + this.divSub);
} // MenuBarNode_toString


/**
 * MenuBarNode MenuBarNode (String id, String name, String parentId,
 *                          boolean hasChildNodes, int level, String icon,
 *                          boolean isActive)
 *           extends HierarchyNode
 * Constructor of class MenuBarNode.
 */
function MenuBarNode (id, name, parentId, hasChildNodes, level, icon, isActive)
{
//alert ("MenuBarNode:\n" + id + "\n" + name);
    // call super constructor(s):
    HierarchyNode.apply (this, arguments);

    // set property values:
    this.isActive = (isActive == true); // is the node active?
    this.icon = icon;                   // icon of the node
    with (this.constructor)
    {
        prototype.getCode = v_nodeGetCode; // get HTML code for menu output
        prototype.getCodePrefix = v_nodeGetCodePrefix; // get prefix for node output
    } // with
} // MenuBarNode

// create class form constructor:
createClass (MenuBarNode, HierarchyNode,
{
    // define properties and assign initial values:
    isChildNodesDisplayed: false,       // are the child nodes already displayed
    isActive: false,                    // is the node active?
    icon: null,                         // icon of the node
    isOpen: false,                      // is the node open?
    isVisible: true,                    // is the node visible?
    div: null,                          // the DIV element of the node
    divSub: null,                       // the DIV element for the sub nodes
    toggleIcon: null,                   // IMG element for toggle +/-
    frame: null,                        // frame for displaying the node
    isScrollIn: false,                  // scrolling in necessary?

    // define methods:
    childInserted: MenuBarNode_childInserted, // event handler: child inserted
    print: MenuBarNode_print,           // print the node
    getCode: v_nodeGetCode,             // get HTML code for menu output
    getCodePrefix: v_nodeGetCodePrefix, // get prefix for node output
    show: MenuBarNode_show,             // display the node
    remove: MenuBarNode_remove,         // remove the node
    scrollIn: MenuBarNode_scrollIn,     // scroll into the node
    setToggleIcon: MenuBarNode_setToggleIcon, // set the toggle icon
    getToggleIcon: MenuBarNode_getToggleIcon, // get the toggle icon
    setToggleIcons: MenuBarNode_setToggleIcons, // set the toggle icon for
                                        // this node and its child nodes
    updatePrefix: MenuBarNode_updatePrefix, // set the node prefix
	updatePrefixes: MenuBarNode_updatePrefixes, // set the prefix for
                                        // this node and its child nodes
    toString2: MenuBarNode_toString     // create string representation
}); // createClass


//============= class MenuBar =================================================

var v_time1 = 0;
var v_time2 = 0;
var v_time3 = 0;
var v_time4 = 0;
var v_time5 = 0;
var v_start = null; // new Date ();
var v_end = null;


/**
 * MenuBarNode MenuBar_createNode (String id, String name, String parentId,
 *                                 boolean hasChildNodes, int level,
 *                                 String icon, boolean isActive)
 * Create a new node.
 * The node is not inserted into the hierarchy.
 */
function MenuBar_createNode (id, name, parentId, hasChildNodes, level, icon,
                             isActive)
{
    // create the new node:
    var node = new MenuBarNode (id, name, parentId, hasChildNodes, level, icon,
                                isActive, this.rootNode == id);

    // call event handler:
    this.nodeCreated (node);

    // return the result:
    return node;
} // MenuBar_createNode


/**
 * void MenuBar_nodeCreated (HierarchyNode node)
 * Event handler which is called after a new node was created.
 */
function MenuBar_nodeCreated (node)
{
    // set additional values for the node:
    node.frame = this.frame;
} // MenuBar_nodeCreated


/**
 * void MenuBar_nodeInserted (MenBarNode node)
 * Event handler which is called after a node was inserted into the hierarchy.
 */
function MenuBar_nodeInserted (node)
{
    // display the node immediately if it is on first level:
    if (node.level == 1)
    {
        node.show (this.frame.document, this.div);
    } // if
} // MenuBar_nodeInserted


/**
 * void MenuBar_nodeSynchronized (MenuBarNode node)
 * Event handler which is called after a node was synchronized.
 */
function MenuBar_nodeSynchronized (node)
{
	// ensure that an object without child nodes is closed
	if (!node.hasChildNodes)
	{
		node.isOpen = false;
	} // if

    // ensure that the sub nodes of the node are loaded the next time the node
    // is opened:
    if (!node.isOpen)
    {
        node.isChildNodesDisplayed = false;
        if (node.divSub != null)
        {
            menu_removeNodes (node.divSub);
        } // if
    } // if

	// retrieve the parent's div element
	var parentDiv = (node.parentNode != null) ? node.parentNode.divSub : null;

    // ensure that the node is correctly displayed:
    node.show (null, parentDiv, false, false, true);
} // MenuBar_nodeSynchronized


/**
 * void Hierarchy_nodeRemoved (HierarchyNode node)
 * Event handler which is called after a node was removed from the hierarchy.
 */
function MenuBar_nodeRemoved (node)
{
    // remove the node, so that it is not displayed anymore
    node.remove (null, null);
} // MenuBar_nodeRemoved


/**
 * void MenuBar_showPage ()
 * Display page for menu bar.
 */
function MenuBar_showPage ()
{
    var doc = this.frame.document;      // the document
    var id = 'div_' + this.id;
    var topVar = "top.menu_navBarTab_" + this.navBarTab.id;

    // check if there is already a DIV element defined:
    if (this.div == null)
    {
        eval (topVar + " = this.navBarTab");

        doc.open ();
        showPageHeader (doc, 'styleMenuBar.css');
        doc.write (
            '<BASE HREF="' + top.system.layoutDir + '"/>' +
            this.getStyles (id) +
            '<BODY onLoad="' + topVar +
                '.menuBar.pageLoaded (document, \'' + id + '\');"' +
            ' onSelectStart="return false">' +
/*
                                        // does not work for MSIE 4.0!
            '<BODY onLoad="top.scripts.actMenuBar.scrollIn (' + actIndex + ')"'+
            ' onSelectStart="return false">' +
                                        // does not work for MSIE 4.0!
*/
            '<A HREF="javascript:top.scripts.reloadMenuBar (this.document, ' + topVar + ')"' +
            ' onMouseOver="return top.showState (\'' + top.multilang.ibs_ibsbase_buttons_btn_reloadDesc+ '\')">' +
            top.multilang.ibs_ibsbase_buttons_btn_reloadName + '</A>\n' +
            '&nbsp;<IMG ID=\"' + ID_TREELOADING_IMAGE +
            '" SRC="' + IMG_LOADING.src + '" ALIGN="ABSMIDDLE"/>' +
            '<UL ID="' + id + '" CLASS="' + id + '">' +
            '</UL>\n' +
            '<SCRIPT LANGUAGE="JavaScript" TYPE="text/javascript">' +
            '<!--\n' +
            'document.onhelp = top.scripts.menu_help;' +
            '\/\/-->\n' +
            '</SCRIPT>' +
            '');
/*
        // must be here to be compatible with IE 4.0 which does not correctly
        // support the onLoad attribute in the BODY tag:
        if (top.system.browser.ie4)         // IE40
        {
            doc.write (
                '<SCRIPT LANGUAGE="JavaScript">' +
                'top.scripts.actMenuBar.scrollIn (' + actIndex + ');\n' +
                '</SCRIPT>');
        } // if IE40
*/

        doc.write ('</BODY></HTML>');
        doc.close ();
    } // if
} // MenuBar_showPage


/**
 * void MenuBar_pageLoaded (Document doc, String id)
 * Tell the menu bar that the page was loaded.
 */
function MenuBar_pageLoaded (doc, id)
{
    // get the DIV element:
    this.div = doc.getElementById (id);

    // assign the DIV element to the root node:
    this.rootNode.divSub = this.div;

    // display the elements of the menu bar:
    if (!this.rootNode.hasChildNodes)
    {
        // the menu content has to be loaded:
/*
        // ensure that the menu bar is displayed immediately when loaded
        // from the server:
        this.displayMenuBar = true;
*/
        // trigger the process which is loading the menu bar from the
        // server:
//        loadMenuBar (this);
    } // if
    else
    {
        // display the already known elements of the menu bar:
        menu_addCall (5000, c_menu_CK_SHOWELEMENTS, new Array (this));
        menu_startCalls ();
    } // else
} // MenuBar_pageLoaded


/**
 * void MenuBar_showTree (Node rootNode)
 * Display a menu bar as tree or just a subtree.
 */
function MenuBar_showTree (rootNode)
{
    // check if there is already a DIV element defined:
    if (this.div == null)
    {
        // show the page:
        this.showPage ();
    } // if
    else
    {
        // check if the content has already been loaded:
        if (!this.rootNode.hasChildNodes)
        {
            // the menu content has to be loaded:
/*
            // ensure that the menu bar is displayed immediately when loaded
            // from the server:
            this.displayMenuBar = true;
*/
            // trigger the process which is loading the menu bar from the
            // server:
            loadMenuBar (this);
        } // if
        else
        {
            if (rootNode != null)
            {
                rootNode.show (this.frame.document, this.div);
            } // if
            else
            {
                // display the elements of the menu bar:
//                this.showElements ();
            } // else
        } // else
    } // else
} // MenuBar_showTree


/**
 * void MenuBar_getStylesTree (String id)
 * Get cascading style sheet information for tree.
 */
function MenuBar_getStylesTree (id)
{
    return (
        '<STYLE TYPE="text/css">\n' +
            // top level sub menu group:
            'UL.' + id + '\n' +
            '{\n' +
                'margin: 0px;\n' +
                'margin-left: -12px;\n' + // left spacing
                'padding: 0px;\n' +
            '}\n' +
            // other sub menu groups:
            '.' + id + ' UL\n' +
            '{\n' +
                'margin-left: 0px;\n' + // left spacing
                'padding-left: 0px;\n' +
            '}\n' +
            // nodes:
            '.' + id + ' LI\n' +
            '{\n' +
                'list-style-type: none;\n' +
                'margin-left: 16px;\n' + // left spacing
                'padding-left: 0px;\n' +
            '}\n' +
        '</STYLE>\n' +
        ''
        );
} // MenuBar_getStylesTree


/**
 * void MenuBar_getStylesTreeLined (String id)
 * Get cascading style sheet information for lined tree.
 */
function MenuBar_getStylesTreeLined (id)
{
    return (
        '<STYLE TYPE="text/css">\n' +
            // top level sub menu group:
            'UL.' + id + '\n' +
            '{\n' +
                'margin: 0px;\n' +
                'margin-left: 0px;\n' + // left spacing
                'padding: 0px;\n' +
            '}\n' +
            // other sub menu groups:
            '.' + id + ' UL\n' +
            '{\n' +
                'margin-left: 0px;\n' + // left spacing
                'padding-left: 0px;\n' +
            '}\n' +
            // nodes:
            '.' + id + ' LI\n' +
            '{\n' +
                'list-style-type: none;\n' +
                'margin-left: 0px;\n' + // left spacing
                'padding-left: 0px;\n' +
            '}\n' +
        '</STYLE>\n' +
        ''
        );
} // MenuBar_getStylesTreeLined


/**
 * void MenuBar_showElements ()
 * Display the elements of a list of tuples.
 */
function MenuBar_showElements ()
{
    var doc = this.frame.document;
    var div = this.div;

    // loop through all nodes and display each of them:
    for (var iter = this.rootNode.iterator (); iter.hasNext ();)
    {
        // get the actual node and display it:
//        iter.next ().show (this.id, doc, div);
        menu_addCall (10, c_menu_CK_SHOWNODE,
            new Array (iter.next (), doc, div, false, false, "2"));
    } // for iter

    // perform the calls:
    menu_startCalls ();
} // MenuBar_showElements


/**
 * String MenuBar_getLoaderId ()
 * Get the id for the loader.
 * If there is already an id set its value is returned.
 * Otherwise a new loader id is created before.
 */
function MenuBar_getLoaderId ()
{
    // check if there is already a loader id set:
    if (this.loaderId == null)
    {
        // define a new loader id:
        this.loaderId = "menuBar_" + this.id + "_" + (new Date ().getTime ());
    } // if

    // return the actual loader id:
    return this.loaderId;
} // MenuBar_getLoaderId


/**
 * void MenuBar_loadingFinished ()
 * Loading of menu bar was finished.
 */
function MenuBar_loadingFinished ()
{
    // tell the loader that the menu bar was loaded:
    loaderHandler.finishLoading (this.loaderId);
    // reset the loader id:
    this.loaderId = null;
    this.rootNode.setToggleIcons ();

    // check if we are in synchronization mode:
    if (this.isSyncMode)
    {
        // finish the synchronization:
        this.finishSynchronization ();
    } // if

    // hide the loading image
    var imgElem = this.frame.document.getElementById (ID_TREELOADING_IMAGE);
    if (imgElem != null)
        imgElem.style.display = lay_VISIBILITY_HIDDEN;

} // MenuBar_loadingFinished


/**
 * String MenuBar_getOpenNodes ()
 * Get the open nodes within the menu bar.
 */
function MenuBar_getOpenNodes ()
{
    var retValue = "";                  // return value
    var sep = "";                       // separator
    var node = null;                    // the actual node

    // loop through all nodes and get all open ones:
    for (var iter = this.iterator (); iter.hasNext ();)
    {
        // get actual node:
        node = iter.next ();

        // check if the node is open:
        if (node.isOpen)
        {
            retValue += sep + node.id;
            sep = ",";
        } // if
    } // for iter

    // return the result:
    return retValue;
} // MenuBar_getOpenNodes


/**
 * MenuBar MenuBar (String id, String name, String rootId, FrameObject frame)
 *      extends Hierarchy
 * Constructor of class MenuBar.
 */
function MenuBar (id, name, rootId, frame)
{
//alert ("menuBar:\n" + id + "\n" + name + "\n" + rootId + "\n" + frame);
    // call super constructor(s):
    Hierarchy.apply (this, arguments);

    // set kind of menu:
    switch (v_menuKind)
    {
        case c_menu_MK_TREE:
            v_nodeGetCode = MenuBarNode_getCodeTree;
            v_nodeGetCodePrefix = MenuBarNode_getCodePrefixTree;
            v_menuBarShow = MenuBar_showTree;
            v_menuBarGetStyles = MenuBar_getStylesTree;
            break;

        case c_menu_MK_TREE_LINED:
            v_nodeGetCode = MenuBarNode_getCodeTree;
            v_nodeGetCodePrefix = MenuBarNode_getCodePrefixTreeLined;
            v_menuBarShow = MenuBar_showTree;
            v_menuBarGetStyles = MenuBar_getStylesTreeLined;
            break;

        case c_menu_MK_WHEEL:
            v_nodeGetCode = MenuBarNode_getCodeWheel;
            v_nodeGetPrefix = null;
            v_menuBarShow = MenuBar_showWheel;
            v_menuBarGetStyles = null;
            break;

        case c_menu_MK_UNDEF:
        default:
            v_nodeGetCode = MenuBarNode_getCode;
            v_nodeGetPrefix = null;
            v_menuBarShow = MenuBar_show;
            v_menuBarGetStyles = null;
    } // switch v_menuKind
/*
alert (v_menuKind + "\n" + v_nodeGetCode + "\n" +
       v_menuBarShow + "\n" + v_menuBarGetStyles);
*/

    // set property values:
    with (this.constructor)
    {
        prototype.show = v_menuBarShow;
        prototype.getStyles = v_menuBarGetStyles;
    } // with

    // add root node:
    this.rootNode = null;               // drop old root node
    hier_createNodeAtLevel (this, 0, this.rootId, this.name, null, false,
                            0, null, true);
} // MenuBar

// create class form constructor:
createClass (MenuBar, Hierarchy,
{
    // define properties and assign initial values:
    framePosX: 0,                       // x scrolling position within frame
    framePosY: 0,                       // y scrolling position within frame
    div: null,                          // DIV element of the menu bar
    isSyncMode: false,                  // menu bar is in synchronization mode?
    loaderId: null,                     // id used for menu bar loader

    // define methods:
    createNode: MenuBar_createNode,     // create new node
    nodeCreated: MenuBar_nodeCreated,   // event handler: new node created
    nodeInserted: MenuBar_nodeInserted, // event handler: node inserted
    nodeSynchronized: MenuBar_nodeSynchronized,
                                        // event handler: node was synchronized
    nodeRemoved: MenuBar_nodeRemoved,   // event handler: node removed
    showPage: MenuBar_showPage,         // show the page
    pageLoaded: MenuBar_pageLoaded,     // event handler: page loaded
    show: v_menuBarShow,                // display the menu bar
    getStyles: v_menuBarGetStyles,      // get stylesheets
    showElements: MenuBar_showElements, // display the elements of the
                                        // menu bar
    getLoaderId: MenuBar_getLoaderId,   // get id for menu bar loader
    loadingFinished: MenuBar_loadingFinished, // loading of menu bar was
                                        // finished
    getOpenNodes: MenuBar_getOpenNodes  // get the open nodes within the
                                        // menu bar
}); // createClass



//============= common functions ==============================================

/**
 * void toggleNode ()
 * Toggle the node open/close and (re-)display it.
 */
function toggleNode ()
{
    var node = this.node;               // the node

    if (node != null)                   // node exists?
    {
        // toggle its open state:
        node.isOpen = !node.isOpen;
        // redisplay the node:
        node.show (null, null, true);
/*
        if (node.isOpen)
        {
            node.scrollIn ();
        } // if
*/
    } // if node exists
} // toggleNode


/**
 * void loadMenuBar (MenuBar menuBar, Node rootNode, String openNodes)
 * Load a menu bar or part of it from the server.
 */
function loadMenuBar (menuBar, rootNode, openNodes)
{
//alert (menuBar.id + "\n" + rootNode + "\n#" + openNodes + "#");
    var offlineUrl = null;              // url for offline mode
    var openNodesClause = "";           // clause for open nodes
    var id = menuBar.name; //menuBar.id;
    var loaderId = menuBar.getLoaderId ();
    var fct = 11;
    var params = null;

    // set the correct url for the offline mode:
    if (id == "group")
    {
        offlineUrl = "menu_group";
    } // if
    else if (id == "private")
    {
        offlineUrl = "menu_private";
    } // else if
    else if (id == "testtree")
    {
        offlineUrl = "menu_testtree";
    } // else if

    // trigger the process which is loading the menu bar from the server:
    if (id == "testtree1")
    {
        fct = "/facturpmc/app/include/menu_testtree.html";
    } // if
    else if (rootNode != null)          // load only partial tree?
    {
        params = "&menu=" + escape (id) + "&srcid=" + escape (rootNode.id);
    } // else if load only partial tree
    else                                // load complete tree
    {
        params = "&menu=" + escape (id);
    } // else load complete tree

    if (params != null && openNodes != null)
    {
        params += "&opennodes=" + escape (openNodes);
    } // if

    loaderHandler.load (loaderId, null, fct, params,
        offlineUrl, "tempnavigation", "menu bar '" + menuBar.name + "'", 20);
} // loadMenuBar


/**
 * void reloadMenuBar (document doc, [NavBarTab navBarTab])
 * Reload the actual menu bar from the server.
 */
function reloadMenuBar (doc, navBarTab)
{
    // set navBarTab:
    if (navBarTab == null)
    {
        navBarTab = actNavBar.activeTab;
    } // if

    // ensure that the menu bar is displayed immediately when loaded
    // from the server:
    navBarTab.displayMenuBar = true;

    // display the loading image
    var imgElem = doc.getElementById (ID_TREELOADING_IMAGE);
    if (imgElem != null)
        imgElem.style.display = lay_VISIBILITY_VISIBLE;

    // trigger the process which is loading the menu bar from the server:
    loadMenuBar (navBarTab.menuBar, null, navBarTab.menuBar.getOpenNodes ());
} // reloadMenuBar


function menu_getCallParamsNextIndex ()
{
    return v_callList.length;
} // menu_getCallParamsNextIndex


/**
 * Image menu_createImage (URL src, [String title], [onclick])
 * Create a new image object.
 * The srcEnd property is computed as src extended with "_end" before the ".".
 */
function menu_createImage (src, title, onclick)
{
    var img = new Image ();
    img.src = src;

    var lastIndex = src.lastIndexOf (".");
    img.srcEnd = src.substring (0, lastIndex) + "_end" +
                 src.substring (lastIndex);

    if (title && title != null)
    {
        img.title = title;
    } // if

    if (onclick && onclick != null)
    {
        img.onclick = onclick;
    } // if

    // return the image:
    return img;
} // menu_createImage


/**
 * Image menu_createMenuImage (String fileName, [String title], [onclick])
 * Create a new image object within menu directory.
 */
function menu_createMenuImage (fileName, title, onclick)
{
    return menu_createImage
        (top.system.layoutDir + "images/menu/" + fileName, title, onclick);
} // menu_createMenuImage


/**
 * void menu_addCall (int timeout, int callKind, Array array)
 * Add a call to the call list.
 */
function menu_addCall (timeout, callKind, array)
{
    v_callList[v_callList.length] =
        new Array (timeout, callKind, array);
} // menu_addCall


/**
 * void menu_startCalls (int timeout)
 * Start the calls from the call list beginning at the first element.
 * If timeout is defined the timeout of the start element is used.
 */
function menu_startCalls (timeout)
{
    if (v_timer == 0 && v_callList.length > 0)
    {
        if (isNaN (timeout))
        {
            timeout = v_callList[0][0];
            if (isNaN (timeout))
            {
                timeout = 0;
            } // if
        } // if
        v_timer = window.setTimeout ("menu_performCalls ()", timeout);
    } // if
} // menu_startCalls


/**
 * void menu_performCalls ()
 * Perform all calls beginning from the first one.
 */
function menu_performCalls ()
{
    if (v_callList.length > 0)
    {
        var call = v_callList.shift ();
        var callKind = call[1];
        var params = call[2];

        switch (callKind)
        {
            case c_menu_CK_CALLFCT:       // call a function
                params[0] (params[1], params[2], params[3], params[4],
                           params[5], params[6], params[7], params[8]);
                break;

            case c_menu_CK_SHOWNODE:      // show a node
                params[0].show (params[1], params[2], params[3], params[4]);
                break;

            case c_menu_CK_SETTOGGLEICON: // set toggle icon of a node
                params[0].setToggleIcon (params[1]);
                break;

            case c_menu_CK_SETTOGGLEICONS: // set toggle icon of several nodes
                params[0].setToggleIcons (params[1]);
                break;

            case c_menu_CK_SCROLLIN:      // scroll into node
                params[0].scrollIn ();
                break;

            case c_menu_CK_SHOWELEMENTS:  // show all elements of menu bar
                params[0].showElements ();
                break;

            case c_menu_CK_CHANGEIMAGE:   // change data of an image
                params[0].src = params[1];
                params[0].title = params[2];
                break;
        } // switch (callKind)

        // drop timer:
        v_timer = 0;

        // check if there are some other calls in the list:
        if (v_callList.length > 0)
        {
            menu_startCalls ();
        } // if
    } // if
} // menu_performCalls


/**
 * void menu_setSubNodesInvisible (Node rootNode)
 * Set a node an all nodes below to invisible.
 */
function menu_setSubNodesInvisible (rootNode)
{
    // set the root node to invisible:
    rootNode.isVisible = false;

    // loop through all nodes within the root node and set them to
    // invisible:
    for (var iter = rootNode.iterator (); iter.hasNext ();)
    {
        // set actual node invisible:
        menu_setSubNodesInvisible (iter.next ());
    } // for iter
} // menu_setSubNodesInvisible


/**
 * void menu_setElementVisible (Element elem)
 * Set a specific element of the html page to visible.
 */
function menu_setElementVisible (elem)
{
    if (elem != null)
    {
        elem.style.display = lay_VISIBILITY_VISIBLE;
        elem.style.visibility = "visible";
    } // if
} // menu_setElementVisible


/**
 * void menu_setElementInvisible (Element elem)
 * Set a specific element of the html page to invisible.
 */
function menu_setElementInvisible (elem)
{
    if (elem != null)
    {
        elem.style.display = lay_VISIBILITY_HIDDEN;
        elem.style.visibility = "hidden";
    } // if
} // menu_setElementInvisible


/**
 * void menu_displayNode (HTMLNode node, boolean isFirst, HTMLNode parentNode,
 *                        Node nextElem)
 * Display a node.
 */
function menu_displayNode (node, isFirst, parentNode, nextElem)
{
//alert (node + "\n" + isFirst + "\n" + parentNode + "\n" + nextElem);
    if (isFirst)
    {
        parentNode.insertBefore (node, parentNode.firstChild);
    } // if
    else if (nextElem != null &&
             nextElem.div != null &&
             nextElem.div.parentNode == parentNode)
    {
        parentNode.insertBefore (node, nextElem.div);
    } // else if
    else
    {
        parentNode.appendChild (node);
    } // else
} // menu_displayNode


/**
 * void menu_removeNodes (HTMLNode parentNode)
 * Remove all nodes within a specific parent HTML node.
 */
function menu_removeNodes (parentNode)
{
    var childNodes = parentNode.childNodes;
    var length = childNodes.length;

    // loop though all child nodes and remove each of them:
    for (var i = length - 1; i >= 0; i--)
    {
        parentNode.removeChild (childNodes[i]);
    } // for i
} // menu_removeNodes


/**
 * void menu_help ()
 */
function menu_help ()
{
    alert (top.multilang.ibs_ibsbase_scripts_MSG_NOHELP);
    return false;
} // menu_help

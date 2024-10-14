/******************************************************************************
 * JavaScript-File. <BR>
 *
 * @version     2.23.0002, 07.03.2002
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */

//============= necessary classes and variables ===============================
// class Layer


//============= declarations ==================================================


//============= class ContextMenu =============================================

/**
 * ContextMenu ContextMenu (int|String id, String name, Document doc)
 * extends Layer
 * Constructor of class ContextMenu.
 */
function ContextMenu (id, name, doc)
{
    // inherit Layer:
    this.parent = Layer;                // define new method parent
    this.parent (id, name, doc);        // create Layer

    // set methods:

    // set initial values:
} // ContextMenu


//============= common functions ==============================================

function callCreateContextMenu (id, content, frame, withCreate)
{
    var menu = new ContextMenu (id, createLayerName (id), frame.document);
    menu.setDimensions (xBase, yBase, layerWidth, layerHeight);
    menu.content = content;
    if (withCreate)
        menu.create ();
    if (actLayerList.get (id) == actLayerList.notFound)
        actLayerList.add (menu);
    else
        actLayerList.replace (menu);
    return (menu);
} // callCreateContextMenu



//############# Here comes the new code #######################################

function getFrame (obj)
{
    if (hasLayers)
        return top.sheet;
    else if (hasAll)
        return obj.document.parentWindow;
    else
        return top;
} // getFrame


function computeLeftOffset (obj)
{
    if (obj == null)
        return (0);

    if (hasLayers)
    {
        return (obj.x);
    } // if
    else if (hasAll)
    {
        if (obj.offsetParent != null)
            return (obj.offsetLeft + computeLeftOffset (obj.offsetParent));
        else
            return (obj.offsetLeft);
    } // else if
} // computeLeftOffset


function computeTopOffset (obj)
{
    if (obj == null)
        return (0);

    if (hasLayers)
    {
        return (obj.y);
    } // if
    else if (hasAll)
    {
        if (obj.offsetParent != null)
            return (obj.offsetTop + computeTopOffset (obj.offsetParent));
        else
            return (obj.offsetTop);
    } // else if
} // computeTopOffset


function computeWidth (obj)
{
    if (obj == null)
        return (0);

    if (hasLayers)
    {
        return (0);
    } // if
    else if (hasAll)
    {
        return (obj.offsetWidth);
    } // else if
} // computeWidth


function computeHeight (obj)
{
    if (obj == null)
        return (0);

    if (hasLayers)
    {
        return (10);
    } // if
    else if (hasAll)
    {
        return (obj.offsetHeight);
    } // else if
} // computeHeight


function showContextMenu (obj)
{
    var frame = getFrame (obj);
    var menu = frame.contextMenu;
    menu.move (computeLeftOffset (obj) + computeWidth (obj) / 2, computeTopOffset (obj) + computeHeight (obj) / 2);
    menu.handler (true);
} // showContextMenu


function showContextMenuDelayed (obj)
{
    var frame = getFrame (obj);
    var menu = frame.contextMenu;
    menu.move (computeLeftOffset (obj), computeTopOffset (obj) + computeHeight (obj));
    menu.setTimer ('top.' + frame.name + '.contextMenu.handler (true)', waitToShow);
} // showContextMenu


function hideContextMenuDelayed (obj)
{
    var frame = getFrame (obj);
    var menu = frame.contextMenu;
    menu.setTimer ('top.scripts.hideContextMenu (\'top.' + frame.name + '.contextMenu\')', 100);
} // hideContextMenuDelayed


function hideContextMenu (menuName)
{
    var menu = eval (menuName);
    menu.clearTimer ();
    menu.handler (false);
menu.hide ();
} // hideContextMenu

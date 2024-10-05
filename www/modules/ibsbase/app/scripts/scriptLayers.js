/******************************************************************************
 * Handling of layers in Netscape Navigator/Communicator and MS Internet 
 * Explorer. <BR>
 * The code in this script supports NS 4.x and MSIE 4.x
 *
 * @version     2.23.0002, 07.03.2002
 *
 * @author      Klaus Reimüller (KR)  991101
 ******************************************************************************
 */

//============= necessary classes and variables ===============================
// class Tuple
// class TupleList


//============= declarations ==================================================

var LAYER_NOT_FOUND =   null;           // error value if layer was not found

var dhtml = false;                      // is there a dhtml support?
var hasLayers = document.layers;        // does the browser have the ability 
                                        // of layers (NS)?
var hasAll = document.all;              // does the browser have the all object
                                        // (MSIE)?
var show = "";                          // code for showing a text block
var hide = "";                          // code for hiding a text block

// the actual layers:
var actLayerList = new LayerList ();

// timer settings:
var notOnTimeout = 2000;                // time to display layer when mouse was 
                                        // not already on it
var afterOnTimeout = 1000;              // time to display layer after mouse 
                                        // left it
var waitToShow = 1000;                  // time to wait before displaying layer

// positioning variables:
var ie = 0;
var iemac = 0;
var iemach = 0;
var xBase = 0;
var yBase = 0;
var levelXSize = 0;
var levelYSize = 25;
var borderX = 3;
var borderY = 0;
var layerWidth = 130;
var layerHeight = 200;

// layout variables:
var onColor = 'blue';
var offColor = 'black';
var layerBGColor = 'silver';
/*
var layerBGColor = '#FFFFCC';
*/
var imageDir = "homepage/images/";      // directory with the images
//var spacer = "homepage/images/spacer.gif";
var globeSpacer = '';
var globeBegin = '<TABLE WIDTH="100%" CLASS="contextmenu" BORDER="1" CELLSPACING="0" CELLPADDING="0" BACKGROUND="images/special/grey.gif">';
var globeBeginB = '<TR><TD WIDTH="2"></TD>';
var globeBeginC = '' +
                  '<TD WIDTH="125">';
/*
var globeBeginC = '<BR><IMG SRC="homepage/images/spffffcc.gif" HEIGHT="17" WIDTH="2"></TD><TD WIDTH="167"><IMG SRC="homepage/images/popup_top.gif" WIDTH="167" HEIGHT="14" BORDER="0"></TD></TR>' +
                  '<TR><TD WIDTH="167">';
*/
var globeBeginD = '<TABLE WIDTH="100%" BORDER="0" CELLSPACING="0" CELLPADDING="0">';
/*
var globeBeginD = '<TABLE WIDTH="167" BORDER="0" CELLSPACING="0" CELLPADDING="0"><TR><TD WIDTH="20" BGCOLOR="#FFFFCC" ALIGN="CENTER" VALIGN="TOP"><IMG SRC="homepage/images/spacer.gif" HEIGHT="1" WIDTH="20"></TD><TD WIDTH="146" BGCOLOR="#FFFFCC"><IMG SRC="homepage/images/spacer.gif" HEIGHT="1" WIDTH="146"></TD><TD WIDTH="1" BGCOLOR="66CC33">' + spacer1 + '</TD></TR>';
*/
var globeEnd = '</TABLE></TD><TD WIDTH="2"></TD></TR>' + 
               '</TABLE>';
/*
var globeEnd = '</TABLE></TD></TR>' + 
               '<TR><TD WIDTH="167"><IMG SRC="homepage/images/popup_bot.gif" WIDTH="167" HEIGHT="14" BORDER="0"></TD></TR></TABLE>';
*/
var content = "";
var bullCream = '';
/*
var bullCream = '<IMG SRC="homepage/images/bullcream.gif" WIDTH="5" HEIGHT="10">';
*/
var spacer1 = '<IMG SRC="homepage/images/spacer.gif" HEIGHT="1" WIDTH="1">';
var td1Begin = '';
/*
var td1Begin = '<TD WIDTH="20" ALIGN="CENTER" VALIGN="TOP">';
*/
var td1End = '';
/*
var td1End = '</TD>';
*/
var td2Begin = '<TD CLASS="contextitem" ALIGN="left">';
var td2BeginInactive = '<TD CLASS="contextiteminactive" ALIGN="left">';
/*
var td2Begin = '<TD WIDTH="146">';
*/
var td2End = '</TD>';
var td3Begin = '';
/*
var td3Begin = '<TD WIDTH="1">';
*/
var td3End = '';
/*
var td3End = '</TD>';
*/
var td3Spacer = '';
/*
var td3Spacer = spacer1;
*/
var font2Begin = '';
/*
var font2Begin = '<FONT FACE="ARIAL, HELVETICA" CLASS="contextitem" SIZE="2">';
*/
var font2End = '';
/*
var font2End = '</FONT>';
*/


// initialize the variables:
// set the default layer which is always returned in case of an error
// if the required layer was not found:
LAYER_NOT_FOUND = new Layer ("LAYER_NOT_FOUND", top.multilang.ibs_ibsbase_scripts_MSG_LAYER_NOT_FOUND, window.document);

if (hasAll || hasLayers)
    dhtml = 1;

if (hasLayers)
{
    show = "show";
    hide = "hide";
    globeSpacer = '<IMG SRC="homepage/images/spacer.gif" HEIGHT="10" WIDTH="2">';
} // hasLayers
if (hasAll)
{
    show = "visible";
    hide = "hidden";
    globeSpacer = '<IMG SRC="homepage/images/spacer.gif" HEIGHT="8" WIDTH="2">';
} // hasAll

globeSpacer = '';

if (hasAll)
    ie = 1;
if ((hasAll) && (navigator.appVersion.indexOf ("Macintosh") != -1))
    iemac = 7;
if ((hasAll) && (navigator.appVersion.indexOf ("Macintosh") != -1))
    iemach = 2;

xBase = 12 + ie + ie - iemach;
yBase = 213 - ie - iemac;

if (dhtml)
    globeBegin += globeBeginB + globeSpacer + globeBeginC;
globeBegin += globeBeginD;


function TreeNode (id, name, level, onImageFile, offImageFile)
{
    this.id = id;
    this.name = name;
    this.level = level;
    this.hasImage = (onImageFile != null || offImageFile != null)
    this.onImage = new Image ();
    this.offImage = new Image ();
    if (document.images)
    {
        if ((navigator.appName.indexOf("Netscape") != -1) && 
            (navigator.appVersion.indexOf("4.") != -1)) 
            this.setImage = setImage40;
        else 
            this.setImage = setImage30;
    } // if
    if (onImageFile != null)
        this.setImage (this.onImage, onImageFile);
    if (offImageFile != null)
        this.setImage (this.offImage, offImageFile);
    else
        this.offImage = this.onImage;
    if (onImageFile == null)
        this.onImage = this.offImage;
} // TreeNode


// Define SRC attributes for rollover images for Net 4.x
function setImage40 (img, file)
{
    img.src = imageDir + file;
} // setImage40


// Cache rollover images and define their SRC attributes for Net 3.x and IE 4.x
function setImage30 (img, file)
{
    img.onload = count_img;
    img.src = imageDir + file;
} // setImage30


// Count rollover images for IE 4.x and Net 3.x after their OnLoad event is called
// Set the callouts flag to true after all images have been cached
function count_img ()
{
    images_cached++;
    if (images_cached == Total_Images)
    {
        set_callouts ();
    }
}


// Returns a handle to the named layer.
function getLayer (name, doc)
{
    var layer = null;
    if (hasLayers)
        layer = doc.layers[name];
    else if (hasAll)
    {
        if (eval ('doc.all.' + name) != null)
            layer = eval ('doc.all.' + name);
    } // else if
    else
        return (null);

    return (layer);
} // getLayer


function createLayerName (id)
{
    return "l_" + id;
} // createLayerName


function callCreateLayer (id, content, frame, withCreate)
{
    var layer = new Layer (id, createLayerName (id), frame.document);
    layer.setDimensions (xBase, yBase, layerWidth, layerHeight);
    layer.content = content;
    if (withCreate)
        layer.create ();
    if (actLayerList.get (id) == actLayerList.notFound)
        actLayerList.add (layer);
    else
        actLayerList.replace (layer);
    return (layer);
} // callCreateLayer


//============= class Layer ===================================================

/**
 * String Layer_getDefinition () 
 * Get the layer definition as String.
 */
function Layer_getDefinition ()
{
    var definition = null;              // the computed definition

    if (this != null && this.layer != null) // layer is there?
    {
        // create layer definition:
        if (hasLayers)
            definition = this.id + ":\n" + 
                         this.layer.src + "\n" +
                         this.layer.toSource + "\n" +
                         this.layer.toString + "\n";
        if (hasAll)
            definition = this.id + ":\n" + 
                         this.layer.innerHTML;
    } // if layer is there

    return (definition);                // return the definition
} // Layer_getDefinition


function Layer_create ()
{
    var z = actLayerList.length;
    var globeBeginId = globeBegin.replace (/<id>/gi, "" + this.id);

    if (hasLayers)
    {
        this.doc.writeln ('<LAYER NAME="' + this.name + '"' +
                          ' LEFT=' + this.left + ' TOP=' + this.top + 
                          ' WIDTH=' + this.width + ' HEIGHT=' + this.height + 
                          ' VISIBILITY="' + (this.visible ? show : hide) + '"' +
                          ' Z-INDEX=' + z + 
                          ' onMouseOver="top.scripts.mouseOn (\'' + this.id + '\')"' + 
                          ' onMouseOut="top.scripts.mouseOff (\'' + this.id + '\');"' +
                          '>');
this.doc.writeln ('<A HREF="" onMouseOver="alert(\'bin drauf\');">');
        this.doc.writeln (globeBeginId + this.content + globeEnd);
this.doc.writeln ('</A>');
        this.doc.writeln ('</LAYER>');
        this.layer = getLayer (this.name, this.doc);
        this.layerStyle = this.layer;
        this.layerStyle.width = this.width;
        this.layerStyle.height = this.height;
    } // if

    if (hasAll)
    {
//        top += 8;
        this.doc.writeln ('<DIV ID="' + this.name + 
                          '" STYLE="position:relative;' +
                          ' left:' + this.left + 'px; top:' + this.top + 'px;' +
                          ' width:' + this.width + 'px; height:' + this.height + 'px;' + 
                          ' visibility:' + (this.visible ? show : hide) + ';' +
                          ' z-index:' + z + '"' +
                          ' onMouseOver="top.scripts.mouseOn (\'' + this.id + '\')"' + 
                          ' onMouseOut="top.scripts.mouseOff (\'' + this.id + '\');"' +
                          '>');
/*
        this.doc.writeln ('<DIV ID="' + this.name + 
                          '" STYLE="position:absolute; overflow:none;' +
                          ' left:' + this.left + 'px; top:' + this.top + 'px;' +
                          ' width:' + this.width + 'px; height:' + this.height + 'px;' + 
                          ' visibility:' + (this.visible ? show : hide) + ';' +
                          ' z-index:' + z + '"' +
                          ' onMouseOver="top.scripts.mouseOn (\'' + this.id + '\')"' + 
                          ' onMouseOut="top.scripts.mouseOff (\'' + this.id + '\');"' +
                          '>');
*/
        this.doc.writeln (globeBeginId + this.content + globeEnd);
        this.doc.writeln ('</DIV>');
        this.layer = getLayer (this.name, this.doc);
        this.layerStyle = this.layer.style;
        if (this.width < this.layer.offsetWidth)
                                            // layer would be too small?
            this.width = this.layer.offsetWidth; // set correct width
        if (this.height < this.layer.offsetHeight)
                                            // layer would be too less high?
            this.height = this.layer.offsetHeight; // set correct height
    } // if

    this.clip (0, 0, this.width, this.height + borderY);
} // Layer_create


function Layer_hide ()
{
    this.layerStyle.visibility = hide;
    this.visible = false;
} // Layer_hide


function Layer_show ()
{
    this.layerStyle.visibility = show;
    this.visible = true;
} // Layer_show


function Layer_clip (clipLeft, clipTop, clipRight, clipBottom)
{
    if (hasLayers)
    {
        this.layerStyle.clip.left   = clipLeft;
        this.layerStyle.clip.top    = clipTop;
        this.layerStyle.clip.right  = clipRight;
        this.layerStyle.clip.bottom = clipBottom;
    } // if
    else if (hasAll)
    {
        this.layerStyle.clip = 'rect(' + clipTop + ' ' +  clipRight + ' ' + clipBottom + ' ' + clipLeft +')';
    } // else if
} // Layer_clip


function Layer_handler (rollOn)
{
    this.onLayer = rollOn;
    this.clearTimer ();
    if (rollOn)
    {
        imageHandler (this);
        actLayerList.hideParents (this);
        this.show ();
        this.setTimer ('top.scripts.actLayerList.hideAll ()', this.notOnTimeout);
    } // if
    else
        this.setTimer ('top.scripts.actLayerList.hideAll ()', this.afterOnTimeout);
} // Layer_handler


/**
 * void Layer_setDimensions (int left, int top, int width, int height)
 * Set dimensions of layer.
 */
function Layer_setDimensions (left, top, width, height)
{
    // set the properties:
    this.left = left;
    this.top = top;
    this.width = width;
    this.height = height;

    // set the properties of the already drawn layer:
    if (this.layer != null)
    {
        if (hasLayers)
        {
            this.layerStyle.pageX = left;
            this.layerStyle.paxeY = top;
            this.layerStyle.width = width;
            this.layerStyle.height = height;
        } // if
        else if (hasAll)
        {
            this.layerStyle.pixelLeft = left;
            this.layerStyle.pixelTop = top;
            this.layerStyle.pixelWidth = width;
            this.layerStyle.pixelHeight = height;
        } // if
    } // if
} // Layer_setDimensions


/**
 * void Layer_move (int toX, int toY)
 * Move layer to another position.
 */
function Layer_move (toX, toY)
{
    // set the properties:
    this.left = toX;
    this.top = toY;

    // set the properties of the already drawn layer:
    if (this.layer != null)
    {
        if (hasLayers)
        {
            this.layer.moveToAbsolute (toX, toY);
        } // if
        else if (hasAll)
        {
            this.layerStyle.pixelLeft = toX;
            this.layerStyle.pixelTop = toY;
        } // if
    } // if
} // Layer_move


/**
 * void Layer_setTimer (String stmt, int time)
 * Set timer of layer.
 */
function Layer_setTimer (stmt, time)
{
    clearTimeout (this.timerId);
    this.timerId = setTimeout (stmt, time);
} // Layer_setTimer


/**
 * void Layer_clearTimer ()
 * Clear timer of layer.
 */
function Layer_clearTimer ()
{
    clearTimeout (this.timerId);
} // Layer_clearTimer


/**
 * void Layer_rollOn ()
 * Mouse comes on layer.
 */
function Layer_rollOn ()
{
    this.handler (true);
    this.clearTimer ();
} // Layer_rollOn


/**
 * void Layer_rollOff ()
 * Mouse leaves layer.
 */
function Layer_rollOff ()
{
    this.handler (false);
    this.setTimer ('top.scripts.actLayerList.hideAll ()', this.afterOnTimeout);
} // Layer_rollOff


/**
 * void Layer_rollOnItem (HTMLObject obj)
 * Mouse comes on layer item.
 */
function Layer_rollOnItem (obj)
{
    if (!this.onLayer)
        this.rollOn ();
    if (hasAll)
    {
        obj.style.color = this.onColor;
//        obj.style.backgroundcolor = 'darkblue';
    } // if
} // Layer_rollOnItem


/**
 * void Layer_rollOffItem (HTMLObject obj)
 * Mouse leaves layer item.
 */
function Layer_rollOffItem (obj)
{
    if (hasLayers && this.onLayer)
        this.rollOff ();
    if (hasAll)
    {
        obj.style.color = this.offColor;
//        obj.style.backgroundcolor = 'silver';
    } // if
} // Layer_rollOffItem


/**
 * void Layer_addSeparator ()
 * Add a separator item to the layer.
 */
function Layer_addSeparator ()
{
    this.addText ('<HR>');
} // Layer_addSeparator


/**
 * void Layer_addText (String text)
 * Add a text only item to the layer.
 */
function Layer_addText (text)
{
    this.content +=
        '<TR CLASS=contextlineinactive>' + td1Begin + bullCream + td1End + 
        td2BeginInactive + font2Begin + 
        text +
        font2End + td2End + 
        td3Begin + td3Spacer + td3End + '</TR>';;
} // Layer_addText


/**
 * void Layer_addLink (String text, String link)
 * Add a link item to the layer.
 */
function Layer_addLink (text, link)
{
    this.content +=
        '<TR CLASS=contextline>' + td1Begin + bullCream + td1End + 
        td2Begin + font2Begin + 
//        '<A HREF="' + link + '">' + 
        '<A HREF="' + link + '" ' +
        'onMouseOver="top.scripts.mouseOnItem (\'' + this.id + '\', this)" ' +
        'onMouseOut="top.scripts.mouseOffItem (\'' + this.id + '\', this);">' + 
        text +
        '</A>' +
        font2End + td2End + 
        td3Begin + td3Spacer + td3End + '</TR>';;
} // Layer_addLink


/**
 * void Layer_changeContent (String newContent)
 * Change the content of the layer.
 */
function Layer_changeContent (newContent)
{
    var globeBeginId = globeBegin.replace (/<id>/gi, "" + this.id);
    this.content = newContent;
    if (this.layer != null && hasAll)
        this.layer.innerHTML = globeBeginId + this.content + globeEnd;
} // Layer_changeContent


/**
 * Layer Layer (int|String id, String name, Document doc) extends Tuple
 * Constructor of Layer.
 */
function Layer (id, name, doc)
{
    // inherit Tuple:
    this.parent = Tuple;                // define new method parent
    this.parent (id, name);             // create Tuple

    // set methods:
    this.getDefinition = Layer_getDefinition; 
                                        // compute the layer definition
    this.create = Layer_create;         // create the layer within HTML
    this.show = Layer_show;             // display the layer
    this.move = Layer_move;             // move layer to another position
    this.hide = Layer_hide;             // hide the layer
    this.clip = Layer_clip;             // clip the layer
    this.handler = Layer_handler;       // the handler for the layer
    this.setDimensions = Layer_setDimensions; // set dimensions of layer
    this.setTimer = Layer_setTimer;     // set timer of layer
    this.clearTimer = Layer_clearTimer; // clear timer of layer
    this.rollOn = Layer_rollOn;         // mouse comes on layer
    this.rollOff = Layer_rollOff;       // mouse leaves layer
    this.rollOnItem = Layer_rollOnItem; // mouse comes on layer item
    this.rollOffItem = Layer_rollOffItem; // mouse leaves layer item
    this.addText = Layer_addText;       // add a text item to the layer
    this.addLink = Layer_addLink;       // add a link item to the layer
    this.addSeparator = Layer_addSeparator; // add a separator to the layer
    this.changeContent = Layer_changeContent; // change the content of the l.

    // set initial values:
    this.name = name;                   // name of layer
    this.left = 0;                      // left position of layer
    this.top = 0;                       // top position of layer
    this.height = 0;                    // height of layer
    this.width = 0;                     // width of layer
    this.visible = false;               // visibility of layer
    this.doc = doc;                     // the document of the layer
    this.content = "";                  // content of layer
    this.layer = null;                  // the layer object
    this.layerStyle = null;             // the style object of the layer
    this.timerId = null;                // timer of this layer
    this.notOnTimeout = notOnTimeout;   // time to display layer when mouse was 
                                        // not already on it
    this.afterOnTimeout = afterOnTimeout; // time to display layer after mouse 
                                        // left it
    this.waitToShow = waitToShow;       // time to wait before displaying layer
    this.onColor = onColor;             // color when mouse is on item
    this.offColor = offColor;           // color when mouse is not on item
    this.onLayer = false;               // is the mouse on the layer?
} // Layer


//============= class LayerList ===============================================

/**
 * void LayerList_hideParents (Layer layer)
 * Hide the parents of one layer.
 */
function LayerList_hideParents (layer)
{
    exitHandler (layer);

    for (var i = 0; i < this.length; i++)
        if (layer == null || (this[i] != layer && this[i].level >= layer.level))
            this[i].hide ();
} // LayerList_hideParents


/**
 * void LayerList_hideAllLayers ()
 * Hide all currently known layers.
 */
function LayerList_hideAllLayers ()
{
    for (var i = 0; i < this.length; i++)
    {
        this[i].hide ();
    } // for
} // LayerList_hideAllLayers


/**
 * LayerList LayerList () extends TupleList
 * Constructor of LayerList.
 */
function LayerList ()
{
    // inherit TupleList:
    this.parent = TupleList;            // define new method parent
    this.parent ("l1", null, false);    // create TupleList

    // assign methods:
    this.hideParents = LayerList_hideParents; // hide parents of one layer
    this.hideAll = LayerList_hideAllLayers; // hide all layers within the list

    // assign initial values:
    this.notFound = LAYER_NOT_FOUND;    // default layer when layer was not 
                                        // found
} // LayerList


//============= class MenuLayer ===============================================

/**
 * MenuLayer MenuLayer (int|String id, String name, DocumentObject doc)
 *                      extends Layer
 * Constructor of MenuLayer.
 */
function MenuLayer (id, name, doc)
{
    // inherit Layer:
    this.parent = Layer;                // define new method parent
    this.parent (id, name, doc);        // create Layer

    // set methods:

    // set initial values:
    this.level = 0;                     // level of layer
    this.pos = 0;                       // position of layer
} // MenuLayer


//============= common functions ==============================================

function imageHandler (layer)
{
/*
    if (node.hasImage)
        document.images[node.name].src = node.onImage.src;
*/
} // imageHandler


function exitHandler (layer)
{
//createError ();
/*
    for (var i = 1; i <= maxNodes; i++)
        if ((node == null || (node != nodeData[i] && nodeData[i].level >= node.level))
             && nodeData[i].hasImage)
            document.images[nodeData[i].name].src = nodeData[i].offImage.src;
*/
} // exitHandler


function callMenuLayer (id, level, pos, content, frame)
{
    var layer = new MenuLayer (id, createLayerName (id), frame.document);
    layer.setDimensions (xBase + level * levelXSize, yBase + pos * levelYSize,
        layerWidth, layerHeight);
    layer.content = content;
    layer.level = level;
    layer.pos = pos;
    layer.create ();
    if (actLayerList.get (id) == actLayerList.notFound)
        actLayerList.add (layer);
    else
        actLayerList.replace (layer);
    return (layer);
} // callCreateMenuLayer


function mouseOn (id)
{
    var layer = null;                   // the actual layer

    if (dhtml)
    {
        // get the layer and call its handler:
        if ((layer = actLayerList.get (id)) != actLayerList.notFound)
        {
            layer.rollOn ();
        } // if
/*
        else
        {
            alert ("layer " + id + " not found");
            actLayerList.print ();
        } // else
*/
    } // if
} // mouseOn


function mouseOff (id)
{
    var layer = null;                   // the actual layer

    if (dhtml)
    {
        // get the layer and call its handler:
        if ((layer = actLayerList.get (id)) != actLayerList.notFound)
            layer.rollOff ();
    } // if
} // mouseOff


function mouseOnItem (id, obj)
{
    var layer = actLayerList.get (id);
    if (layer != actLayerList.notFound)
    {
        layer.rollOnItem (obj);
        layer.clearTimer ();
    } // if
} // mouseOnItem


function mouseOffItem (id, obj)
{
    var layer = actLayerList.get (id);
    if (layer != actLayerList.notFound)
    {
        layer.rollOffItem (obj);
    } // if
} // mouseOffItem


/*
// LAYERS BUILDING
function textLine (text)
{
    return '<TR CLASS=contextlineinactive>' + td1Begin + bullCream + td1End + 
        td2BeginInactive + font2Begin + 
        text +
        font2End + td2End + 
        td3Begin + td3Spacer + td3End + '</TR>';;
} // textLine


function separatorLine ()
{
    return textLine ('<HR>');
} // separatorLine


function linkLine (id, link, linkText)
{
    return '<TR CLASS=contextline>' + td1Begin + bullCream + td1End + 
        td2Begin + font2Begin + 
//        '<A HREF="' + link + '">' + 
        '<A HREF="' + link + '" onMouseOver="top.scripts.mouseOnItem (\'' + id + '\', this)" onMouseOut="top.scripts.mouseOffItem (\'' + id + '\', this);">' + 
        linkText +
        '</A>' +
        font2End + td2End + 
        td3Begin + td3Spacer + td3End + '</TR>';;
} // linkLine


function linkLine2 (id, link, linkText)
{
    return '<TR CLASS=contextline>' + td1Begin + bullCream + td1End + 
        td2Begin + font2Begin + 
        '<A HREF="' + link + '" onMouseOver="top.scripts.mouseOnItem (\'' + id + '\', this);" onMouseOut="top.scripts.mouseOffItem (\'' + id + '\', this);">' + 
        linkText +
        '</A>' +
        font2End + td2End + 
        td3Begin + td3Spacer + td3End + '</TR>';;
} // linkLine2


// Hide global image declarations from JavaScript browsers that don't support Image Objects
if (document.images)
{
    var devNode = new TreeNode ("0x0101002100000101", "dev", 2, "l_nav_devon.gif", "l_nav_develop.gif");
    var chanNode = new TreeNode ("0x0101002100000102", "chan", 1, "l_nav_chanon.gif", "l_nav_channel.gif");
    var prodNode = new TreeNode ("0x0101002100000103", "prod", 1, "l_nav_prodon.gif", "l_nav_product.gif");
    var comNode = new TreeNode ("0x0101002100000104", "com", 1, null, null);
    var ytkNode = new TreeNode ("0x0101002100000105", "ytk", 1, "l_nav_ytkon.gif", "l_nav_y2k.gif");
} // if

nodeData[1] = devNode;
nodeData[2] = chanNode;
nodeData[3] = prodNode;
nodeData[4] = comNode;
nodeData[5] = ytkNode;

for (var i = 1; i <= maxLayers; i++)
    layernames[i] = createLayerName (nodeData[i]);

// Developer Table Structure:
content = textLine ('For hardware, software and Web developers.');
callCreateLayer (devNode, 5.5, content);


// create Channel Table Structure:
content = textLine ('For resellers, retailers, VARs and Intel&#174; product dealers.');
callCreateLayer (chanNode, 2, content);


// Product Info Table Structure
content = linkLine ('/PentiumIII/index.htm?iid=intelhome+roll_PentiumIII', 'Pentium&#174; III processor') +
    linkLine ('/PentiumIII/Xeon/home.htm?iid=intelhome+roll_P3XP', 'Pentium&#174; III Xeon&#153; processor') +
    linkLine ('/Celeron/index.htm?iid=intelhome+roll_Celeron', 'Intel&#174; Celeron&#153; processor') +
    linkLine ('/network/index.htm?iid=intelhome+roll_network', 'Intel&#174; Networking products') +
    linkLine ('/AnyPoint/home.htm?iid=intelhome+roll_AnyPoint', 'AnyPoint&#153; Home Network') +
    linkLine ('/intel/product/index.htm?iid=intelhome+roll_product', 'Other Intel&#174; products & services') +
callCreateLayer (prodNode, 3, content);


// Company Info Table Structure:
content = linkLine ('/pressroom/index.htm?iid=intelhome+roll_pressroom', 'Press Room') +
    linkLine ('/intel/finance/index.htm?iid=intelhome+roll_IR', 'Investor Relations') +
    linkLine2 ('javascript:showObject (0x01010021000003FF)', 'Employment at tectum') +
    linkLine2 ('javascript:showObject (0x0101002100000400)', 'tectum in Education') +
    linkLine ('/intel/index.htm?iid=intelhome+roll_company', 'More Company Info');
callCreateLayer (comNode, 4, content);


// Y2K Table Structure
content = textLine ('Year 2000 readiness information for home and business.');
callCreateLayer (ytkNode, 5, content);

//jsLoaded = 1;
*/

/******************************************************************************
 * This file contains all classes, methods, and global variables needed for
 * the hierarchy functionality. <BR>
 *
 * @version     $Id: scriptHierarchy.js,v 1.6 2012/06/21 07:11:06 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR) 20060323
 ******************************************************************************
 */


//============= declared classes and functions ================================
// class HierarchyNode
// class Hierarchy
// class HierarchyNodeIterator
// class HierarchyIterator

// function hier_createNodeBelow
// function hier_createNodeAtLevel
// function hier_syncNode


//============= necessary classes and variables ===============================
// class CollectionElement

// var loaderHandler


//============= declarations ==================================================

// messages:

// constants:

// variables:


//============= initializations ===============================================


//============= class HierarchyNode ===========================================

/**
 * void HierarchyNode_appendChild (HierarchyNode node)
 * Add a new node as last sub node.
 */
function HierarchyNode_appendChild (node)
{
    node.parentNode = this;             // this is now the father node
    node.level = this.level + 1;        // set node level
    node.parentId = this.id;            // remember id of parent node

alert ("X");
    if (this.firstChild == null)        // the new node is the only one below?
    {
        node.previousSibling = null;    // no previous sibling
        node.nextSibling = null;        // no next sibling
        this.firstChild = node;         // set as first node
        this.lastChild = node;          // set as last node
        this.hasChildNodes = true;      // this node now has child nodes
    } // if the new node is the only one below
    else
    {
        // add the node after the last one:
        node.previousSibling = this.lastChild; // the last node is the previous
                                        // of the new node
        node.nextSibling = null;        // no next node
        this.lastChild.nextSibling = node; // the new node is the next one of
                                        // the not longer last node
        this.lastChild = node;          // the node is the last one below father
    } // else
alert ("Y");

    // call event handler:
    this.childInserted (node);
alert ("Z");
} // HierarchyNode_appendChild


/**
 * void HierarchyNode_insertChild (HierarchyNode node)
 * Add a new node as sub node.
 * The node is inserted in a sorted manner.
 */
function HierarchyNode_insertChild (node)
{
    var prev = null;                    // previous node

    node.parentNode = this;             // this is now the father node
    node.level = this.level + 1;        // set node level
    node.parentId = this.id;            // remember id of parent node

    if (this.firstChild == null)        // the new node is the only one below?
    {
        node.previousSibling = null;    // no previous sibling
        node.nextSibling = null;        // no next sibling
        this.firstChild = node;         // set as first node
        this.lastChild = node;          // set as last node
        this.hasChildNodes = true;      // this node now has child nodes
    } // if the new node is the only one below
    else
    {
        // search for the correct insertion position (sorting):
        for (prev = this.lastChild; prev != null && node.compareTo (prev) < 0;
             prev = prev.previousSibling)
        {
            // nothing to do
        } // for prev

        // check where to insert:
        if (prev == null)               // insert at first position?
        {
            node.previousSibling = null; // no previous node
            node.nextSibling = this.firstChild; // the first node is the next of 
                                        // the new node
            node.nextSibling.previousSibling = node;
                                        // the new node is the previous
                                        // one of the not longer first node
            this.firstChild = node;     // set as first node
        } // if insert at first position
        else if (prev == this.lastChild) // insert at last position?
        {
            // add the node after the last one:
            node.previousSibling = prev; // the last node is the previous of 
                                        // the new node
            node.nextSibling = null;    // no next node
            prev.nextSibling = node;    // the new node is the next one of the
                                        // not longer last node
            this.lastChild = node;      // the node is the last one below father
        } // else if insert at last position
        else                            // any other position
        {
            node.previousSibling = prev;    // the found node is the previous of 
                                        // the new node
            node.nextSibling = prev.nextSibling; // the node after the found
                                        // node is the next of the new node
            prev.nextSibling.previousSibling = node;
                                        // the new node is the previous one of
                                        // the node after the found node
            prev.nextSibling = node;    // the new node is the next one of the
                                        // found node
        } // else any other position
    } // else

    // call event handler:
    this.childInserted (node);
} // HierarchyNode_insertChild


/**
 * void HierarchyNode_childInserted (HierarchyNode childNode)
 * Event handler which is called after a new child was inserted.
 */
function HierarchyNode_childInserted (childNode)
{
    // nothing to do, can be overwritten
} // HierarchyNode_childInserted


/**
 * HierarchyNode HierarchyNode_removeChild (Node node)
 * Add a new node as sub node.
 */
function HierarchyNode_removeChild (node)
{
    if (node != null && node.parentNode == this)
                                        // node is existing within this node?
    {
        // cut the actual node out of the list on this node's level:
        // set next node of previous node:
        if (node.previousSibling != null)
        {
            node.previousSibling.nextSibling = node.nextSibling;
        } // if
        // set previous node of next node:
        if (node.nextSibling != null)
        {
            node.nextSibling.previousSibling = node.previousSibling;
        } // if

        // ensure that the current node has correct information regarding its
        // first and last node:
        // if this node was the first one set new first node:
        if (this.firstChild == node)
        {
            this.firstChild = node.nextSibling;
        } // if
        // if this node was the last one set new last node:
        if (this.lastChild == node)
        {
            this.lastChild = node.previousSibling;
        } // if
    } // if node is existing within this node

    // call event handler:
    this.childRemoved (node);

    // return the removed node:
    return node;
} // HierarchyNode_removeChild


/**
 * void HierarchyNode_childRemoved (HierarchyNode childNode)
 * Event handler which is called after a child was removed.
 */
function HierarchyNode_childRemoved (childNode)
{
    // nothing to do, can be overwritten
} // HierarchyNode_childRemoved


/**
 * void HierarchyNode_syncValues (HierarchyNode newNode)
 * Snychronize the values for this node with another node which is newer.
 */
function HierarchyNode_syncValues (newNode)
{
    var isChanged = false;

    isChanged = (this.nodeName != newNode.nodeName ||
                 this.hasChildNodes != newNode.hasChildNodes ||
                 this.isActive != newNode.isActive);
//alert ("test1: "+ this.id + "\n" + this.nodeName);

    if (isChanged)
    {
        // object information:
        this.nodeName = newNode.nodeName;
        this.isActive = newNode.isActive;

        // hierarchy information: (has to be set in hierarchy itself)
        this.hasChildNodes = newNode.hasChildNodes;
    } // if
} // HierarchyNode_syncValues


/**
 * void HierarchyNode_compareTo (HierarchyNode otherObj)
 * Compare this HierarchyNode with another one.
 * Return 0 if both are equal,
 * <0 if this HierarchyNode is less than the other one,
 * >0 if the other one is less.
 */
function HierarchyNode_compareTo (otherObj)
{
/* it is alredy known that the other object is not null
    if (otherObj == null)
    {
        return 1;
    } // if
*/
    if (this.key == otherObj.key)
    {
        return 0;
    } // else if
    if (this.key < otherObj.key)
    {
        return -1;
    } // else if
    return 1;
} // HierarchyNode_compareTo


/**
 * String HierarchyNode_toString ()
 * Create String representation.
 */
function HierarchyNode_toString ()
{
    return ("id=" + this.id + ", nodeName=" + this.nodeName +
            ", parentId=" + this.parentId + ", isValid=" + this.isValid +
            ", hasChildNodes=" + this.hasChildNodes + ", level=" + this.level);
} // HierarchyNode_toString


/**
 * HierarchyNode HierarchyNode (String id, String name, String parentId,
 *                              [boolean hasChildNodes])
 *           extends CollectionElement
 * Constructor of class HierarchyNode.
 */
function HierarchyNode (id, name, parentId, hasChildNodes)
{
    // call super constructor(s): (not necessary)
//    CollectionElement.apply (this, arguments);

    // set property values:
    this.id = id;                       // id
    this.nodeName = name;               // name
    this.key = name.toLowerCase ();     // key used for sorting
    this.parentId = parentId;           // id of parent node
    this.hasChildNodes = (hasChildNodes == true);
                                        // does the node have child nodes?
} // HierarchyNode

// create class form constructor:
createClass (HierarchyNode, CollectionElement,
{
    // define properties and assign initial values:
    nodeName: null,                     // name
    level: 0,                           // level of the node
    parentId: 0,                        // id of parent node
    isValid: true,                      // is the node valid?
    hasChildNodes: false,               // does the node have child nodes?
    firstChild: null,                   // first child node
    lastChild: null,                    // last child node
    parentNode: null,                   // upper node where this is a child node
    nextSibling: null,                  // next node on same level
    previousSibling: null,              // previous node on same level
    iteratorProto: HierarchyNodeIterator, // kind of iterator (= prototype)

    // define methods:
    appendChild: HierarchyNode_appendChild, // add a sub node as last child
    insertChild: HierarchyNode_insertChild, // add a child node to this node
    childInserted: HierarchyNode_childInserted, // event handler: child inserted
    removeChild: HierarchyNode_removeChild, // drop a child node from this node
    childRemoved: HierarchyNode_childRemoved, // event handler: child removed
    iterator: Collection.prototype.iterator, // get iterator
    syncValues: HierarchyNode_syncValues,
                                        // synchronize with a newer node version
    compareTo: HierarchyNode_compareTo, // compare the node with another one
    toString2: HierarchyNode_toString   // create string representation
}); // createClass


//============= class HierarchyNodeIterator ===================================

/**
 * Object HierarchyNodeIterator_first ()
 * Get the first element.
 */
function HierarchyNodeIterator_first ()
{
    if (this.p_node.hasChildNodes)      // there is at least one element?
    {
        this.p_actElem = this.p_node.firstChild; // store the actual element
        return this.p_actElem;          // return the actual element
    } // if there is at least one element
    else                                // didn't find element
    {
        return this.notFound;           // return error element
    } // else
} // HierarchyNodeIterator_first


/**
 * Object HierarchyNodeIterator_next ()
 * Get the next element of the actual element.
 */
function HierarchyNodeIterator_next ()
{
    if (this.p_actElem == null)         // currently no element selected?
    {
        return (this.first ());         // return first element
    } // if currently no element selected
    else if (this.p_actElem.nextSibling != null) // element exists?
    {
        this.p_actElem = this.p_actElem.nextSibling; // store the actual element
        return this.p_actElem;          // return the actual element
    } // else if element exists
    else                                // didn't find element
    {
        return this.notFound;           // return error element
    } // else
} // HierarchyNodeIterator_next


/**
 * Object HierarchyNodeIterator_prev ()
 * Get the previous element of the actual element.
 */
function HierarchyNodeIterator_prev ()
{
    if (this.p_actElem == null)         // currently no element selected?
    {
        return (this.last ());          // return last element
    } // if currently no element selected
    else if (this.p_actElem.previousSibling != null) // element exists?
    {
        this.p_actElem = this.p_actElem.previousSibling;
                                        // store the actual element
        return this.p_actElem;          // return the actual element
    } // else if element exists
    else                                // didn't find element
    {
        return this.notFound;           // return error element
    } // else
} // HierarchyNodeIterator_prev


/**
 * Object HierarchyNodeIterator_last ()
 * Get the last element.
 */
function HierarchyNodeIterator_last ()
{
    if (this.p_node.lastChild != null)  // there is at least one element?
    {
        this.p_actElem = this.p_node.lastChild; // store the actual element
        return this.p_actElem;          // return the actual element
    } // if there is at least one element
    else                                // didn't find element
    {
        return this.notFound;           // return error element
    } // else
} // HierarchyNodeIterator_last


/**
 * boolean HierarchyNodeIterator_hasNext ()
 * Check if there is a next element.
 */
function HierarchyNodeIterator_hasNext ()
{
    // compute the result and return it:
    return ((this.p_actElem == null && this.p_node.firstChild != null) ||
            (this.p_actElem != null && this.p_actElem.nextSibling != null));
} // HierarchyNodeIterator_hasNext


/**
 * HierarchyNodeIterator HierarchyNodeIterator (HierarchyNode node,
 *          [HierarchyNode searchStart])
 * Constructor of class HierarchyNodeIterator.
 */
function HierarchyNodeIterator (node, searchStart)
{
    // call super constructor(s):
    Iterator.apply (this, arguments);

    // set property values:
    this.p_node = node;                 // the node

    // ensure constraints:
    if (searchStart && searchStart != null)
    {
        this.p_actElem = searchStart;
    } // if
} // HierarchyNodeIterator

// create class form constructor:
createClass (HierarchyNodeIterator, Iterator,
{
    // define properties and assign initial values:
    p_node: null,                       // the node

    // define methods:
    first: HierarchyNodeIterator_first, // get the first element
    next: HierarchyNodeIterator_next,   // get the next element
    prev: HierarchyNodeIterator_prev,   // get the previous element
    last: HierarchyNodeIterator_last,   // get the last element
    hasNext: HierarchyNodeIterator_hasNext // is there a next element?
}); // createClass


//============= class Hierarchy ===============================================

/**
 * HierarchyNode Hierarchy_createNode (String id, String name)
 * Create a new node.
 * The node is not inserted into the hierarchy.
 */
function Hierarchy_createNode (id, name)
{
    // create the new node:
    var node = new HierarchyNode (id, name, null, null, null, null, null, true);

    // call event handler:
    this.nodeCreated (node);

    // return the result:
    return node;
} // Hierarchy_createNode


/**
 * void Hierarchy_nodeCreated (HierarchyNode node)
 * Event handler which is called after a new node was created.
 */
function Hierarchy_nodeCreated (node)
{
    // nothing to do, can be overwritten
} // Hierarchy_nodeCreated


/**
 * void Hierarchy_setActNode (HierarchyNode node)
 * Set the actual node.
 * There is no check done whether the node exists within the hierarchy.
 */
function Hierarchy_setActNode (node, parentNode)
{
    // remember actual node:
    this.act = node;
} // Hierarchy_setActNode


/**
 * void Hierarchy_insertNodeBelow (HierarchyNode node, HierarchyNode parentNode)
 * Insert a node below a specific parent node.
 */
function Hierarchy_insertNodeBelow (node, parentNode)
{
    // insert the node below the parent node:
    parentNode.insertChild (node);

    // remember actual node:
    this.act = node;

    // call event handler:
    this.nodeInserted (node);
} // Hierarchy_insertNodeBelow


/**
 * void Hierarchy_insertNodeAtLevel (HierarchyNode node, int level)
 * Insert a node at a specific level at the last position.
 * This method can be used for incrementally filling a hierarchy.
 */
function Hierarchy_insertNodeAtLevel (node, level)
{
    var act = this.act;                 // actual node

    // get actual node on higher level (i.e. one less level number):
    if (level > 0)                      // there must be a parent node?
    {
        // get the last node on higher level:
        while (act != null && act.level >= level)
        {
            act = act.parentNode;
        } // while

        // check if the node is the parent:
        if (node.parentId != null && act.id != node.parentId)
        {
/*
            // get the correct parent node:
            act = this.get (node.parentId);

            // check if we got the parent node:
            if (act == null)
            {
                alert (top.multilang.ibs_ibsbase_scripts_MSG_NODENOTINSERTEDATLEVEL + " " + level);
                return;
            } // if
*/
        } // if

        // add the node to the parent node:
        act.insertChild (node);
    } // if there must be a parent node
    else if (level == 0 && this.rootNode == null)
    {
        // store the node as root node:
        this.rootNode = node;
    } // else if
    else
    {
        alert (top.multilang.ibs_ibsbase_scripts_MSG_NODENOTINSERTEDATLEVEL + " " + level);
        return;
    } // else

    // remember actual node:
    this.act = node;

    // call event handler:
    this.nodeInserted (node);
} // Hierarchy_insertNodeAtLevel


/**
 * void Hierarchy_nodeInserted (HierarchyNode node)
 * Event handler which is called after a node was inserted into the hierarchy.
 */
function Hierarchy_nodeInserted (node)
{
    // nothing to do, can be overwritten
} // Hierarchy_nodeInserted


/**
 * HierarchyNode Hierarchy_removeNode (HierarchyNode node)
 * Drop a node from the menu bar.
 */
function Hierarchy_removeNode (node)
{
    var act = null;                     // actual node

    if (node != null)                   // node was defined?
    {
        // remove the node from its parent:
        if (node.parentNode != null)
        {
            node.parentNode.removeChild (node);
        } // if
        else if (node.parentId != null)
        {
            var parentNode = this.get (node.parentId);
            if (parentNode != null)
            {
                parentNode.removeChild (node);
            } // if
        } // else if

/*
        // loop through the nodes which are below the dropped node and drop 
        // them all (recursively):
        for (var iter = node.iterator (); iter.hasNext ();)
        {
            // get the node:
            act = iter.next ();

            // remove actual node:
            node.removeChild (act);
        } // for iter
*/

        // call event handler:
        this.nodeRemoved (node);
    } // if node was defined

    // return the removed node:
    return node;
} // Hierarchy_removeNode


/**
 * HierarchyNode Hierarchy_removeNodeById (String id)
 * Drop a node from the menu bar.
 */
function Hierarchy_removeNodeById (id)
{
    // search for the node and remove it:
    return (this.removeNode (this.get (id)));
} // Hierarchy_removeNodeById


/**
 * void Hierarchy_nodeRemoved (HierarchyNode node)
 * Event handler which is called after a node was removed from the hierarchy.
 */
function Hierarchy_nodeRemoved (node)
{
    // nothing to do, can be overwritten
} // Hierarchy_nodeRemoved


/**
 * HierarchyNode Hierarchy_get (String|int id, [HierarchyNode searchRoot],
 *                              [HierarchyNode searchStart])
 * Get node out of hierarchy using its id.
 * The method returns null if the id was not found.
 */
function Hierarchy_get (id, searchRoot, searchStart)
{
    var node = null;                    // current node

    // loop through all elements of the hierarchy until the id is found or
    // all elements were searched:
    for (var iter = this.iterator (searchRoot, searchStart); iter.hasNext ();)
    {
        // get the node:
        node = iter.next ();

        // check the node's id:
        if (node.id == id)
        {
            // return the node:
            return node;
        } // if
    } // for iter

    // the node was not found, return default value:
    return null;
} // Hierarchy_get


/**
 * HierarchyNode Hierarchy_find (String name, [HierarchyNode searchRoot],
 *                               [HierarchyNode searchStart])
 * Get node out of hierarchy using its name.
 * The method returns null if the name was not found.
 */
function Hierarchy_find (name, searchRoot, searchStart)
{
    var node = null;                    // current node

    // loop through all elements of the hierarchy until the id is found or
    // all elements were searched:
    for (var iter = this.iterator (searchRoot, searchStart); iter.hasNext ();)
    {
        // get the node:
        node = iter.next ();

        // check the node's id:
        if (node.nodeName == name)
        {
            // return the node:
            return node;
        } // if
    } // for iter

    // the node was not found, return default value:
    return null;
} // Hierarchy_find


/**
 * void Hierarchy_startSynchronization (Node rootNode)
 * Synchronize new or changed nodes into this hierarchy, i.e. ensure that 
 * the hierarchy consists of the actual node set.
 * Start synchronization of hierarchy.
 */
function Hierarchy_startSynchronization (rootNode)
{
    var act = null;                     // actual node

    // remember that we are in synchronization mode:
    this.isSyncMode = true;

    // loop through all elements of this hierarchy and set them to invalid:
    for (var iter = this.iterator (rootNode); iter.hasNext ();)
    {
        // get the node and set it to invalid:
        iter.next ().isValid = false;
    } // for iter
} // Hierarchy_startSynchronization


/**
 * void Hierarchy_syncNodes (HierarchyNode node, HierarchyNode newNode)
 * Synchronize nodes within hierarchy.
 */
function Hierarchy_syncNodes (node, newNode)
{
    var parentNode = null;              // the parent node of the node

    // check if the nodes were correctly defined:
    if (node != null && newNode != null) // the nodes are defined?
    {
        // transfer the values from the new node to the old node:
        node.syncValues (newNode);

        // check if the parent node was changed:
        if (node.parentId != newNode.parentId)
        {
            // search for the new parent node:
            if ((parentNode = this.get (newNode.parentId)) != null)
            {
                // drop the node from the old parent node:
                node.parentNode.removeChild (node);
                // add the node to the parent node:
                parentNode.insertChild (node);
            } // if
        } // if
    } // if the nodes are defined
} // Hierarchy_syncNodes


/**
 * void Hierarchy_syncNode (HierarchyNode newNode)
 * Synchronize node within hierarchy.
 * IMPORTANT: The newNode has to be created with the createNode function.
 */
function Hierarchy_syncNode (newNode)
{
    var node = this.get (newNode.id);   // the node which is already in the
                                        // hierarchy
    var parentNode = null;              // the parent node of the node

    // check if the node is already existing:
    if (node != null)                   // node already present in hierarchy?
    {
        // synchronize the nodes:
        this.syncNodes (node, newNode);
        // set the node to valid:
        node.isValid = true;
        // call event handler:
        this.nodeSynchronized (node);
    } // if node already present in hierarchy
    else                                // node not existing in hierarchy
    {
        // set the node:
        node = newNode;
        // ensure that the correct properties are set:
/* already done in createNode
       this.nodeCreated (node);
*/
        // get the parent node:
        if ((parentNode = this.get (node.parentId)) != null)
        {
            // add the node to the parent node:
            parentNode.insertChild (node);
        } // if
    } // else node not existing in hierarchy
} // Hierarchy_syncNode


/**
 * void Hierarchy_nodeSynchronized (HierarchyNode node)
 * Event handler which is called after a node was synchronized.
 */
function Hierarchy_nodeSynchronized (node)
{
    // nothing to do, can be overwritten
} // Hierarchy_nodeSynchronized


/**
 * void Hierarchy_synchronize (Hierarchy newHierarchy)
 * Synchronize this hierarchy with a new one, i.e. ensure that 
 * the nodes are consistent with the nodes in the new hierarchy.
 */
function Hierarchy_synchronize (newHierarchy)
{
    if (newHierarchy == null)           // no new hierarchy?
    {
        return;                         // terminate method
    } // if no new hierarchy

    // loop through all elements of the new hierarchy, check for each
    // element if it is in this hierarchy and synchronize the node data:
    var newIter = newHierarchy.iterator ();
    var iter = this.iterator ();
    var newNode = null;
    var node = null;

    while (newIter.hasNext ())
    {
        // get the nodes:
        newNode = newIter.next ();
        node = iter.next ();

        if (node == null || newNode.id != node.id)
        {
            // try to find the node:
            while (node.id != newNode.id && iter.hasNext)
            {
                node = iter.next ();
            } // while
        } // if

        if (node != null && newNode.id == node.id)
        {
            // synchronize the nodes:
            this.syncNodes (node, newNode);
        } // if
        else
        {
            // synchronize the new node into the hierarchy:
            this.syncNode (newNode);
        } // else
    } // while newIter
} // Hierarchy_synchronize


/**
 * void Hierarchy_finishSynchronization (MenuBar oldMenuBar)
 * Synchronize new or changed nodes into this bar, i.e. ensure that 
 * the menu bar consists of the actual node set.
 * Finish synchronization of menu bar.
 */
function Hierarchy_finishSynchronization (oldMenuBar)
{
    // drop all nodes which are not valid:
    // loop through all elements of this hierarchy and remove the invalid ones:
    for (var iter = this.iterator (); iter.hasNext ();)
    {
        // get the node and check its state:
        if (!(node = iter.next ()).isValid)
        {
            // remove the node:
            this.removeNode (node);
        } // if
    } // for iter

    // we are not longer in synchronization mode:
    this.isSyncMode = false;
//alert ("synchronization finished");
} // Hierarchy_finishSynchronization


/**
 * String Hierarchy_getLoaderId ()
 * Get the id for the loader.
 * If there is already an id set its value is returned.
 * Otherwise a new loader id is created before.
 */
function Hierarchy_getLoaderId ()
{
    // check if there is already a loader id set:
    if (this.loaderId == null)
    {
        // define a new loader id:
        this.loaderId = "hierarchy" + this.id + "_" + (new Date ().getTime ());
    } // if

    // return the actual loader id:
    return this.loaderId;
} // Hierarchy_getLoaderId


/**
 * void Hierarchy_loadingFinished ()
 * Loading of menu bar was finished.
 */
function Hierarchy_loadingFinished ()
{
    // tell the loader that the menu bar was loaded:
    loaderHandler.finishLoading (this.loaderId);
    // reset the loader id:
    this.loaderId = null;
    this[0].setToggleIcons ();

    // check if we are in synchronization mode:
    if (this.isSyncMode)
    {
        // finish the synchronization:
        this.finishSynchronization ();
    } // if
} // Hierarchy_loadingFinished


/**
 * Hierarchy Hierarchy (String id, String name, String rootId,
 *                      FrameObject frame)
 * Constructor of class Hierarchy.
 */
function Hierarchy (id, name, rootId, frame)
{
//alert ("Hierarchy:\n" + id + "\n" + name + "\n" + rootId + "\n" + frame);
    // call super constructor(s):
//    Collection.apply (this, arguments);

    // set property values:
    this.id = id;                       // the id of the list
    this.name = name;                   // name
    this.rootId = rootId;               // id of root object
    this.frame = frame;                 // the frame within to display the
                                        // hierarchy

    // add root node:
//alert ("    hier_createNodeAtLevel (\n" + this + ",\n" + 0 + ",\n" + this.rootId+ ",\n" + this.name);
    hier_createNodeAtLevel (this, 0, this.rootId, this.name);
} // Hierarchy

// create class form constructor:
createClass (Hierarchy, null,
{
    // define properties and assign initial values:
    id: 0,                              // the id of the list
    name: null,                         // name
    rootId: null,                       // id of root object
    frame: null,                        // the frame within to display the
                                        // hierarchy
    isSyncMode: false,                  // menu bar is in synchronization mode?
    loaderId: null,                     // id used for menu bar loader
    rootNode: null,                     // the hierarchy root
    act: null,                          // actual node, used for incremental
                                        // insertion
    iteratorProto: HierarchyIterator,   // kind of iterator (= prototype)

    // define methods:
    createNode: Hierarchy_createNode,   // create new node
    nodeCreated: Hierarchy_nodeCreated, // event handler: new node created
    setActNode: Hierarchy_setActNode,   // set actual node
    insertNodeBelow: Hierarchy_insertNodeBelow,
                                        // insert node below parent
    insertNodeAtLevel: Hierarchy_insertNodeAtLevel,
                                        // insert node at specific level
    nodeInserted: Hierarchy_nodeInserted, // event handler: node inserted
    removeNode: Hierarchy_removeNode,   // remove a node
    removeNodeById: Hierarchy_removeNodeById,
                                        // remove a node defined through id
    nodeRemoved: Hierarchy_nodeRemoved,
                                        // event handler: node removed
    get: Hierarchy_get,                 // get a node through its id
    find: Hierarchy_find,               // get a node through its name
    iterator: Collection.prototype.iterator,
                                        // get iterator
    startSynchronization: Hierarchy_startSynchronization, 
                                        // start synchronization of menu bar
    syncNodes: Hierarchy_syncNodes,     // synchronize nodes
    syncNode: Hierarchy_syncNode,       // synchronize node
    nodeSynchronized: Hierarchy_nodeSynchronized,
                                        // event handler: node was synchronized
    synchronize: Hierarchy_synchronize, // synchronize this menu bar with
                                        // another one
    finishSynchronization: Hierarchy_finishSynchronization, 
                                        // finish synchronization of menu bar
    getLoaderId: Hierarchy_getLoaderId, // get id for menu bar loader
    loadingFinished: Hierarchy_loadingFinished // loading of menu bar was
                                        // finished
}); // createClass


//============= class HierarchyIterator =======================================

/**
 * Object HierarchyIterator_getFirst ()
 * Get the first element without storing it.
 */
function HierarchyIterator_getFirst ()
{
    if (this.p_searchRoot != null)      // search root was defined?
    {
        return this.p_searchRoot;
    } // if search root was defined
    else                                // no search root
    {
        return this.collection.rootNode;
    } // else no search root
} // HierarchyIterator_getFirst


/**
 * Object HierarchyIterator_first ()
 * Get the first element.
 */
function HierarchyIterator_first ()
{
    var node = this.getFirst ();

    if (node != null)                   // node was found?
    {
        this.p_actElem = node;          // store the actual element
        this.p_nextElem = null;         // no next element
        return this.p_actElem;          // return the actual element
    } // if node was found
    else                                // didn't find element
    {
        return this.notFound;           // return error element
    } // else
} // HierarchyIterator_first


/**
 * Object HierarchyIterator_next ()
 * Get the next element of the actual element.
 */
function HierarchyIterator_next ()
{
    if (this.p_actElem == null)         // currently no element selected?
    {
        return (this.first ());         // return first element
    } // if currently no element selected
    else if (this.p_actElem.firstChild != null) // child element exists?
    {
        this.p_actElem = this.p_actElem.firstChild; // store the actual element
        this.p_nextElem = null;         // no next element
        return this.p_actElem;          // return the actual element
    } // else if child element exists
    else if (this.p_actElem.nextSibling != null &&
             this.p_actElem != this.p_searchRoot) // sibling exists?
    {
        this.p_actElem = this.p_actElem.nextSibling; // store the actual element
        this.p_nextElem = null;         // no next element
        return this.p_actElem;          // return the actual element
    } // else if sibling exists
    else if (this.p_actElem != this.p_searchRoot) // search on upper level?
    {
        var elem = null;

        // search for parent node having a sibling:
        for (elem = this.p_actElem.parentNode;
             elem != null && elem.nextSibling == null;
             elem = elem.parentNode);

        // check if we found a parent node with a sibling:
        if (elem != null)
        {
            this.p_actElem = elem.nextSibling; // store the actual element
            this.p_nextElem = null;     // no next element
            return this.p_actElem;      // return the actual element
        } // if
        else                            // didn't find element
        {
            return this.notFound;       // return error element
        } // else
    } // else if search on upper level?
    else                                // didn't find element
    {
        return this.notFound;           // return error element
    } // else
} // HierarchyIterator_next


/**
 * Object HierarchyIterator_prev ()
 * Get the previous element of the actual element.
 */
function HierarchyIterator_prev ()
{
    if (this.p_actElem == null)         // currently no element selected?
    {
        return (this.last ());          // return last element
    } // if currently no element selected
    else if (this.p_actElem != this.p_searchRoot)
                                        // going to side and up possible?
    {
        if (this.p_actElem.previousSibling != null) // sibling exists?
        {
            var elem = null;

            // find last node within this element:
            for (elem = this.p_actElem.previousSibling;
                 elem.lastChild != null;
                 elem = elem.lastChild);

            this.p_actElem = elem;      // store the actual element
            this.p_nextElem = null;     // no next element
            return this.p_actElem;      // return the actual element
        } // if sibling exists
        else if (this.p_actElem.parentNode != null) // parent node exists?
        {
            this.p_actElem = this.p_actElem.firstChild;
                                        // store the actual element
            this.p_nextElem = null;     // no next element
            return this.p_actElem;      // return the actual element
        } // else if parent node  exists
        else                            // didn't find element
        {
            return this.notFound;       // return error element
        } // else
    } // else if going to side and up possible
    else                                // didn't find element
    {
        return this.notFound;           // return error element
    } // else
} // HierarchyIterator_prev


/**
 * Object HierarchyIterator_last ()
 * Get the last element.
 */
function HierarchyIterator_last ()
{
    var elem = this.collection.rootNode;

    if (elem != null)
    {
        // find last node within hierarchy:
        for (; elem.lastChild != null; elem = elem.lastChild);

        this.p_actElem = elem;          // store the actual element
        this.p_nextElem = null;         // no next element
        return this.p_actElem;          // return the actual element
    } // if
    else                                // didn't find element
    {
        return this.notFound;           // return error element
    } // else
} // HierarchyIterator_last


/**
 * Object HierarchyIterator_setActElem (Object actElem)
 * Set a new actual element.
 */
function HierarchyIterator_setActElem (actElem)
{
    // set the actual element:
    this.p_actElem = actElem;
} // HierarchyIterator_setActElem


/**
 * boolean HierarchyIterator_hasNext ()
 * Check if there is a next element.
 */
function HierarchyIterator_hasNext ()
{
    var node = this.p_actElem;          // the actual node

    // compute the result from first node:
    if (node == null)
    {
        return this.getFirst () != null;
    } // if

    // compute the result from child nodes and siblings:
    if (node.firstChild != null || node.nextSibling != null)
    {
        return true;
    } // if

    // check upper hierarchy:
    // search for parent node having a sibling:
    for (node = node.parentNode;
         node != null && node.nextSibling == null;
         node = node.parentNode);

    // check if we found a parent node with a sibling:
    if (node != null)
    {
        this.p_nextElem = node.nextSibling; // store the next element
        return true;                // return the result
    } // if
    else                            // didn't find element
    {
        return false;               // return the result
    } // else
} // HierarchyIterator_hasNext


/**
 * HierarchyIterator HierarchyIterator (Hierarchy hier,
 *      [HierarchyNode searchRoot], [HierarchyNode searchStart])
 * Constructor of class HierarchyIterator.
 */
function HierarchyIterator (hier, searchRoot, searchStart)
{
    // call super constructor(s):
    Iterator.apply (this, arguments);

    // set property values:
    this.p_searchRoot = searchRoot;     // the search root

    // ensure constraints:
    if (searchRoot && searchRoot != null)
    {
        this.p_actElem = searchRoot;
    } // if
    if (searchStart && searchStart != null)
    {
        this.p_actElem = searchStart;
    } // if
} // HierarchyIterator

// create class form constructor:
createClass (HierarchyIterator, Iterator,
{
    // define properties and assign initial values:
    p_searchRoot: null,                 // the search root

    // define methods:
    getFirst: HierarchyIterator_getFirst, // get the first element
    first: HierarchyIterator_first,     // get the first element
    next: HierarchyIterator_next,       // get the next element
    setActElem: HierarchyIterator_setActElem, // set actual element
    prev: HierarchyIterator_prev,       // get the previous element
    last: HierarchyIterator_last,       // get the last element
    hasNext: HierarchyIterator_hasNext  // is there a next element?
}); // createClass


//============= common functions ==============================================

/**
 * HierarchyNode hier_createNodeBelow (Hierarchy hier, HierarchyNode parentNode,
 *                                     String id, String name, ...)
 * Create a new node and insert it below a specific parent node.
 */
function hier_createNodeBelow (hier, parentNode, id, name)
{
    var node = null;                    // the node
    var args = $A2 (arguments, 2);

    // create the new node:
    node = hier.createNode.apply (hier, args);

    // insert the node below the parent node:
    parentNode.insertChild (node);

    // return the node:
    return node;
} // hier_createNodeBelow


/**
 * HierarchyNode hier_createNodeAtLevel (Hierarchy hier, int level,
 *                                       String id, String name, ...)
 * Create a new node and insert it at a specific level at the last position.
 * This method can be used for incrementally filling a hierarchy.
 */
function hier_createNodeAtLevel (hier, level, id, name)
{
    var node = null;                    // the node
    var args = $A2 (arguments, 2);      // drop first two arguments
//alert ("createNodeAtLevel:\n" + level + "\n" + id + "\n" + name);

    // create the new node:
    node = hier.createNode.apply (hier, args);
//    node = hier.createNode (id, name);

    // add node to parent node within hierarchy:
    hier.insertNodeAtLevel (node, level);

    // return the node:
    return node;
} // hier_createNodeAtLevel


/**
 * HierarchyNode hier_syncNode (Hierarchy hier, int level,
 *                              String id, String name, ...)
 * Create a new node and sync it with the corresponding already existing node
 * with the same id.
 * This method can be used for incrementally updating a hierarchy.
 */
function hier_syncNode (hier, level, id, name)
{
    var node = null;                    // the node
    var args = $A2 (arguments, 2);      // drop first two arguments

    // create the new node:
    node = hier.createNode.apply (hier, args);

    // add node to parent node within hierarchy:
    hier.syncNode (node);

    // return the node:
    return node;
} // hier_syncNode

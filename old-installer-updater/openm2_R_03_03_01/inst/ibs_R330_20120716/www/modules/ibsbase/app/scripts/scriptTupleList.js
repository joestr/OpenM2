/******************************************************************************
 * This file contains all JavaScript classes and their methods which are used
 * to create and manage lists. <BR>
 * 
 * @version     $Id: scriptTupleList.js,v 1.3 2010/05/03 16:02:39 rburgermann Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */

//============= declared classes and functions ================================
// class Tuple
// class TupleList
// class TupleListIterator

// function printList


//============= necessary classes and variables ===============================


//============= declarations ==================================================

// constants:
var MAX_LIST_LENGTH =   100000;         // maximum length of list
var TUPLE_NOT_FOUND =   null;           // constant tuple used as default value
var NOOID = "0x0000000000000000";       // OID for no object

// variables:

//============= initializations ===============================================

// set the default tuple which is always returned in case of an error
// if the required tuple was not found:
TUPLE_NOT_FOUND = new Tuple ("TUPLE_NOT_FOUND", top.multilang.ibs_ibsbase_scripts_MSG_TUPLE_NOT_FOUND);


//============= class Tuple ===================================================

/**
 * String Tuple_toString2 ()
 * Returns a string representation of this tuple.  The string
 * representation consists of the id and the value where the id is enclosed
 * in brackets ("()").
 */
function Tuple_toString2 ()
{
    // begin the string:
    var buf = "";

    if (this != null)                   // tuple is there?
    {
        // create tuple definition:
        buf += this.value + " (" + this.id + ")";
    } // if

    // return the result:
    return buf;
} // Tuple_toString2


/**
 * Tuple Tuple (String|int id, String value)
 * Constructur of class Tuple.
 */
function Tuple (id, value)
{
    // call super constructor(s): (here not necessary)
//    CollectionElement.apply (this, arguments);

    // set property values:
    this.id = id;
    this.value = value;
    this.key = value.toLowerCase ();    // key used for sorting
} // Tuple

// create class form constructor:
createClass (Tuple, CollectionElement,
{
    // define properties and assign initial values:
    value: null,                        // the value
    key: null,                          // key used for sorting
    index: 0,                           // index within list

    // define methods:
    toString2: Tuple_toString2,         // get string representation
    getDefinition: Tuple_toString2      // compute the tuple definition
}); // createClass


//============= class TupleList ===============================================

/**
 * boolean TupleList_add (Tuple tuple[, boolean nocheck])
 * Add tuple to list.
 */
function TupleList_add (tuple, nocheck)
{
    var i = 0;                          // counter
    var retVal = false;                 // return value

    // check if there is already a tuple with this id:
    if (nocheck || this.get (tuple.id) == this.notFound) // tuple not found?
    {
        // add tuple to the end of the list:
        if (this.length < MAX_LIST_LENGTH) // list not full?
        {
            // add tuple to the end and increment number of tuples within the 
            // list:
            this.akt = this.length++;   // store index of actual tuple
            this[this.akt] = tuple;     // add tuple to list
            tuple.index = this.akt;     // store index within the tuple
            retVal = true;              // the tuple was inserted correctly
        } // if list not full
    } // if tuple not found

    return retVal;                      // return state value
} // TupleList_add


/**
 * boolean TupleList_replace (Tuple tuple)
 * Replace tuple with new one identified by its id.
 */
function TupleList_replace (tuple)
{
    var i = 0;                          // counter
    var retVal = false;                 // return value

    // try to get the tuple out of the list:
    if (this.get (tuple.id) != this.notFound) // tuple found?
    {
        this[this.akt] = tuple;         // replace tuple with new one
        tuple.index = this.akt;         // store index within tuple
        retVal = true;                  // the tuple was replaced correctly
    } // if tuple found

    return retVal;                      // return state value
} // TupleList_replace


/**
 * boolean TupleList_drop (int|String id)
 * Drop tuple from list identified by its id.
 */
function TupleList_drop (id)
{
    var retVal = false;                 // return value
    var tuple = null;                   // the tuple

    // try to get the tuple out of the list:
    if ((tuple = this.get (id)) != this.notFound) // tuple found?
    {
        if (this.akt == tuple.index)    // trying to drop actual tuple?
            this.akt = 0;               // there is no actual tuple

        // move the other tuples one position further:
        for (var j = tuple.index; j < this.length - 1; j++)
        {
            // move the actual tuple one position further:
            this[j] = this[j + 1];
            this[j].index = j;
        } // for

        // set the new length:
        if (this.length > 0)            // at least one element was in list?
            this.length--;              // there is one element less

        retVal = true;                  // operation done correctly
    } // if tuple found

    return retVal;                      // return state value
} // TupleList_drop


/**
 * boolean TupleList_dropIndexed (int index)
 * Drop tuple from list identified by its index.
 */
function TupleList_dropIndexed (index)
{
    var retVal = false;                 // return value

    if (index >= 0 && index < this.length) // tuple exists?
    {
        if (this.akt == index)          // trying to drop actual tuple?
            this.akt = 0;               // there is no actual tuple

        // move the other tuples one position further:
        for (var j = index; j < this.length - 1; j++)
        {
            // move the actual tuple one position further:
            this[j] = this[j + 1];
            this[j].index = j;
        } // for

        // set the new length:
        if (this.length > 0)            // at least one element was in list?
            this.length--;              // there is one element less

        retVal = true;                  // operation done correctly
    } // tuple exists

    return retVal;                      // return state value
} // TupleList_dropIndexed


/**
 * void TupleList_empty ()
 * Empty the list, i.e. drop all elements.
 */
function TupleList_empty ()
{
    // loop through all elements and clear them:
    for (var i = 0; i < this.length; i++)
    {
        // drop the actual element:
        this[i] = null;
    } // for

    // reinitialize the properties:
    this.akt = 0;                       // there is no actual tuple
    this.length = 0;                    // no element in the list
    this.idList = null;                 // no idList known
    this.allAvailable = false;          // no ids available
} // TupleList_empty


/**
 * Tuple TupleList_get (String|int id, [int startIndex])
 * Get tuple out of list using its id.
 */
function TupleList_get (id, startIndex)
{
    var elem = null;                    // actual element

    // loop through all elements of the list until the id is found or
    // the end of the list is reached:
    for (var iter = this.iterator (startIndex); iter.hasNext ();)
    {
        // get the node:
        elem = iter.next ();

        // check the node's id:
        if (elem.id == id)
        {
            // store the actual tuple:
            this.akt = iter.actIndex;
            // return the node:
            return elem;
        } // if
    } // for iter

    // the node was not found, return default value:
    return this.notFound;               // return error tuple
} // TupleList_get


/**
 * Tuple TupleList_getIndexed (int index)
 * Get tuple out of the list identified by its index.
 */
function TupleList_getIndexed (index)
{
    var tuple = null;                   // the tuple

    // check the index and get the tuple out of the list:
    if (index >= 0 && index < this.length) // index is within allowed range?
    {
        tuple = this[index];            // assign the tuple
    } // if index is within allowed range
    
    if (tuple != null)                  // found tuple?
    {
        this.akt = index;               // store the index of the actual tuple
    } // if found tuple
    else                                // didn't find tuple
    {
        tuple = this.notFound;          // set error tuple
    } // else didn't find tuple

    return tuple;                       // return the tuple
} // TupleList_getIndexed


/**
 * Tuple TupleList_find (String value, [int startIndex])
 * Get tuple out of list identified by its value.
 * This method finds the first tuple with this value.
 */
function TupleList_find (value, startIndex)
{
    var elem = null;                    // actual element

    // loop through all elements of the list until the value is found or
    // the end of the list is reached:
    for (var iter = this.iterator (startIndex); iter.hasNext ();)
    {
        // get the node:
        elem = iter.next ();

        // check the node's id:
        if (elem.value == value)
        {
            // store the actual tuple:
            this.akt = iter.actIndex;
            // return the node:
            return elem;
        } // if
    } // for iter

    // the node was not found, return default value:
    return this.notFound;               // return error tuple
} // TupleList_find


/**
 * TupleArray TupleList_findAll (String value, [int startIndex])
 * Get all tuples out of list identified by the same value.
 * If no tuple was found the array is empty.
 */
function TupleList_findAll (value, startIndex)
{
    var elem = null;                    // actual element
    var retVal = new Array ();          // the result array

    // loop through all elements of the list until
    // the end of the list is reached:
    for (var iter = this.iterator (startIndex); iter.hasNext ();)
    {
        elem = iter.next ();
        if (elem.value == value)
        {
            retVal[retVal.length] = elem;
        } // if
    } // for i

    // return the result:
    return retVal;
} // TupleList_findAll


/**
 * Tuple TupleList_first ()
 * Get the first tuple out of the list.
 * @deprecated: Use TupleListIterator instead.
 */
function TupleList_first ()
{
    if (this.length > 0)                // there is at least one tuple?
    {
        this.akt = 0;                   // store the index of the actual tuple
        return this[this.akt];          // return the actual tuple
    } // if mind. ein Tupel vorhanden
    else                                // didn't find tuple
    {
        return this.notFound;           // return error tuple
    } // else didn't find tuple
} // TupleList_first


/**
 * Tuple TupleList_next ()
 * Get the next tuple of the actual tuple out of the list.
 * @deprecated: Use TupleListIterator instead.
 */
function TupleList_next ()
{
    if ((this.akt + 1) < this.length)   // tuple exists?
    {
        this.akt++;                     // compute and store index of tuple
        return this[this.akt];          // return the tuple
    } // if tuple exists
    else if (this.length > 0 && this.cyclic) 
                                        // cyclic list with at least one tuple?
    {
        this.akt = 0;                   // set first tuple as actual one
        return this[this.akt];          // return the actual tuple
    } // else if cyclic list with at least one tuple
    else                                // didn't find tuple
        return this.notFound;           // return error tuple
} // TupleList_next


/**
 * Tuple TupleList_prev ()
 * Get the previous tuple of the actual tuple out of the list.
 * @deprecated: Use TupleListIterator instead.
 */
function TupleList_prev ()
{
    if (this.akt > 0)                   // tuple exists?
    {
        this.akt--;                     // compute and store index of tuple
        return this[this.akt];          // return the tuple
    } // if tuple exists
    else if (this.length > 0 && this.cyclic) 
                                        // cyclic list with at least one tuple?
    {
        this.akt = this.length - 1;     // set last tuple as actual one
        return this[this.akt];          // return the actual tuple
    } // else if cyclic list with at least one tuple
    else                                // didn't find tuple
        return this.notFound;           // return error tuple
} // TupleList_prev


/**
 * Tuple TupleList_last ()
 * Get the last tuple out of the list.
 * @deprecated: Use TupleListIterator instead.
 */
function TupleList_last ()
{
    if (this.length > 0)                // there is at least one tuple?
    {
        this.akt = this.length - 1;     // set last tuple as actual one
        return this[this.akt];          // return the actual tuple
    } // if there is at least one tuple
    else                                // didn't find tuple
        return this.notFound;           // return error tuple
} // TupleList_last


/**
 * Array TupleList_forAll (FunctionObject fct)
 * Iterate over all tuples and perform a function for each of these.
 * If the function returns a value all values are concatenated to an array.
 */
function TupleList_forAll (fct)
{
    var retVal = new Array ();
    var otherArgs = $A (arguments);
    otherArgs.shift ();

    // loop through all elements and display each of them:
    for (var iter = this.iterator (); iter.hasNext ();)
    {
        // get the next element and perform the function:
        retVal[retVal.length] =
            fct.apply (this, new Array (iter.next ()).concat (otherArgs));
    } // for iter

    // return the result:
    return retVal;
} // TupleList_forAll


/**
 * void TupleList_print ()
 * Print a list of tuples.
 */
function TupleList_print ()
{
    var str = "";                       // output string

    // loop through all elements of the list and append their definitions
    // to the string:
    for (var iter = this.iterator (); iter.hasNext ();)
    {
        str += iter.next ().getDefinition () + "\n";
    } // for iter

    // Print the string and the number of found elements:
    alert (str + this.length + ' ' + top.multilang.ibs_ibsbase_scripts_MSG_ELEMENTS);
} // TupleList_print


/**
 * void TupleList_show ()
 * Display a list of tuples.
 */
function TupleList_show ()
{
    var doc = this.frame.document;      // document where to display the tuple
                                        // list

    // display the list header:
    doc.write ("<HTML><BODY><H1>TupleList" + this.name + ":</H1>");

    // display the list elements:
    this.showElements ();

    // display the list footer:
    doc.write ("</BODY></HTML>");
} // TupleList_show


/**
 * void TupleList_showElements ()
 * Display the elements of a list of tuples.
 */
function TupleList_showElements ()
{
    // loop through all elements and display each of them:
    for (var iter = this.iterator (); iter.hasNext ();)
    {
        // display the element:
        iter.next ().show (this.id, this.frame.document);
    } // for iter
} // TupleList_showElements


/**
 * void TupleList_clear ()
 * Clean the html document of the list.
 */
function TupleList_clear ()
{
    this.frame.document.open ();
    this.frame.document.write ('<HTML><BODY>&nbsp;</BODY></HTML>');
    this.frame.document.close ();
} // TupleList_clear


/**
 * TupleList TupleList (id[, FrameObject frame[, boolean cyclic]])
 * Constructor of class TupleList. (The list may be cyclic.)
 */
function TupleList (id, frame, cyclic)
{
    // call super constructor(s):
    LoadableList.apply (this, arguments);

    // set property values:
    this.id = id;                       // the id of the list
    this.frame = frame;                 // the frame within to display the list

    // ensure constraints:
    this.cyclic = toBoolean (this.cyclic);
} // TupleList

// create class from constructor:
createClass (TupleList, LoadableList,
{
    // define properties and assign initial values:
    id: 0,                              // the id of the list
    frame: null,                        // the frame within to display the list
    length: 0,                          // number of elements within the list
    cyclic: false,                      // is the list cyclic?
    akt: 0,                             // index of actual tuple
    notFound: TUPLE_NOT_FOUND,          // default tuple when tuple was not 
                                        // found
    iteratorProto: TupleListIterator,   // kind of iterator (= prototype)

    // define methods:
    add: TupleList_add,                 // add a new element to list
    replace: TupleList_replace,         // replace tuple within list
    drop: TupleList_drop,               // drop tuple from list
    dropIndexed: TupleList_dropIndexed, // drop tuple from list identified 
                                        // by its index
    empty: TupleList_empty,             // empty the list
    get: TupleList_get,                 // get an element identified by its id
    getIndexed: TupleList_getIndexed,   // get an element identified by its 
                                        // index
    find: TupleList_find,               // search for an element
    findAll: TupleList_findAll,         // search for all element with the same
                                        // value
    iterator: function () {return new this.iteratorProto (this);},
                                        // get iterator
    // first, next, prev, last are deprecated, use iterator () instead
    first: TupleList_first,             // get the first element
    next: TupleList_next,               // get the next element
    prev: TupleList_prev,               // get the previous element
    last: TupleList_last,               // get the last element
    forAll: TupleList_forAll,           // perform function for all elements
    print: TupleList_print,             // print the list
    show: TupleList_show,               // display the list
    showElements: TupleList_showElements, // display the list elements
    clear: TupleList_clear              // clear the html document of the list
}); // createClass


//============= class TupleListIterator =======================================

/**
 * Object TupleListIterator_first ()
 * Get the first element out of the list.
 */
function TupleListIterator_first ()
{
    if (this.collection.length > 0)     // there is at least one element?
    {
        if (this.startIndex > 0)        // start index is defined?
        {
            if (this.startIndex < this.collection.length)
                                        // valid start index?
            {
                this.p_actIndex = this.startIndex; // store the index of the
                                        // actual element
            } // if valid start index
            else                        // didn't find element
            {
                return this.notFound;   // return error element
            } // else
        } // if start index is defined
        else                            // no start index
        {
            this.p_actIndex = 0;        // store the index of the actual element
        } // else no start index
        this.p_actElem = this.collection[this.p_actIndex];
                                        // store the actual element
        return this.p_actElem;          // return the actual element
    } // if there is at least one element
    else                                // didn't find element
    {
        return this.notFound;           // return error element
    } // else
    if (this.p_list.length > 0)         // there is at least one element?
    {
        this.p_actIndex = 0;            // store the index of the actual element
        this.p_actElem = this.p_list[this.p_actIndex];
                                        // store the actual element
        return this.p_actElem;          // return the actual element
    } // if there is at least one element
    else                                // didn't find element
    {
        return this.notFound;           // return error element
    } // else
} // TupleListIterator_first


/**
 * Object TupleListIterator_next ()
 * Get the next element of the actual element out of the list.
 */
function TupleListIterator_next ()
{
    if ((this.p_actIndex + 1) < this.p_list.length) // element exists?
    {
        this.p_actIndex++;              // compute and store index of actual
                                        // element
        this.p_actElem = this.p_list[this.p_actIndex];
                                        // store the actual element
        return this.p_actElem;          // return the actual element
    } // if element exists
    else if (this.p_list.length > 0 && this.p_list.cyclic) 
                                        // cyclic list with at least one elem.?
    {
        // return first element:
        return this.first ();
    } // else if cyclic list with at least one elem.
    else                                // didn't find element
    {
        return this.notFound;           // return error element
    } // else
} // TupleListIterator_next


/**
 * Object TupleListIterator_prev ()
 * Get the previous element of the actual element out of the list.
 */
function TupleListIterator_prev ()
{
    if (this.p_actIndex > 0)            // element exists?
    {
        this.p_actIndex--;              // compute and store index of actual
                                        // element
        this.p_actElem = this.p_list[this.p_actIndex];
                                        // store the actual element
        return this.p_actElem;          // return the actual element
    } // if element exists
    else if (this.p_list.length > 0 && this.p_list.cyclic) 
                                        // cyclic list with at least one elem.?
    {
        // return last element:
        return this.first ();
    } // else if cyclic list with at least one elem.
    else                                // didn't find element
    {
        return this.notFound;           // return error element
    } // else
} // TupleListIterator_prev


/**
 * Object TupleListIterator_last ()
 * Get the last element out of the list.
 */
function TupleListIterator_last ()
{
    if (this.p_list.length > 0)         // there is at least one element?
    {
        this.p_actIndex = this.p_list.length - 1;
                                        // set last element as actual one
        this.p_actElem = this.p_list[this.p_actIndex];
                                        // store the actual element
        return this.p_actElem;          // return the actual element
    } // if there is at least one element
    else                                // didn't find element
    {
        return this.notFound;           // return error element
    } // else
} // TupleListIterator_last


/**
 * boolean TupleListIterator_hasNext ()
 * Check if there is a next element within the list.
 */
function TupleListIterator_hasNext ()
{
    // compute the result and return it:
    return (this.p_list.length > 0 &&
            ((this.p_actIndex + 1) < this.p_list.length ||
             this.p_list.cyclic)
           );
} // TupleListIterator_hasNext


/**
 * Object HierarchyIterator_setActIndex (int index)
 * Set a new actual index.
 */
function HierarchyIterator_setActIndex (index)
{
    // set the actual index:
    this.p_actIndex = index;

    // set the actual element:
    if (this.p_list.length > index)
    {
        this.p_actElem = this.p_list[index];
    } // if
    else
    {
        this.p_actElem = null;
    } // else
} // HierarchyIterator_setActIndex


/**
 * TupleListIterator TupleListIterator (Array|TupleList list, [int startIndex])
 * Constructor of class TupleListIterator.
 */
function TupleListIterator (list, startIndex)
{
    // call super constructor(s):
    ListIterator.apply (this, arguments);

    // set property values:

    // define properties and assign initial values:
    this.p_list = list;                 // the list

    // ensure constraints:
    if (startIndex && startIndex != null &&
        startIndex >= 0 && startIndex < list.length)
    {
        this.p_actIndex = startIndex;
    } // if
} // TupleListIterator

// create class form constructor:
createClass (TupleListIterator, ListIterator,
{
    // define properties and assign initial values:
    p_list: null,                       // the list
    p_actIndex: -1,                     // index of actual element

    // define methods:
    first: TupleListIterator_first,     // get the first element
    next: TupleListIterator_next,       // get the next element
    prev: TupleListIterator_prev,       // get the previous element
    last: TupleListIterator_last,       // get the last element
    hasNext: TupleListIterator_hasNext,  // is there a next element?
    setActIndex: HierarchyIterator_setActIndex // set actual index
}); // createClass


//============= common functions ==============================================

/**
 * void printList (TupleList list)
 * Print a tuple list.
 */
function printList (list)
{
    list.print ();
} // printList

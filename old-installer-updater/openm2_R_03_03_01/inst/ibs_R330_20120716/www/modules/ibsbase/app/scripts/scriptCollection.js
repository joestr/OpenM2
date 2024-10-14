/******************************************************************************
 * This file contains all JavaScript classes and their methods which are used
 * to create and manage collections. <BR>
 * The collection implementation is based upon the java implementation of
 * collections. (J2SDK 5.0)
 * 
 * @version     $Id: scriptCollection.js,v 1.2 2006/04/12 17:10:00 klreimue Exp $
 *
 * @author      Klaus Reimüller (KR) 20060320
 ******************************************************************************
 */

//============= declared classes and functions ================================
// class CollectionElement
// class Collection
// class Iterator


//============= necessary classes and variables ===============================
// class Function
// class Object

// messages:


//============= declarations ==================================================

// constants:

// variables:


//============= initializations ===============================================



//============= class CollectionElement =======================================

/**
 * void CollectionElement_print ()
 * Make an output of the CollectionElement.
 */
function CollectionElement_print ()
{
    alert (this.toString2 ());
} // CollectionElement_print


/**
 * void CollectionElement_show (DocumentObject doc)
 * Display the CollectionElement.
 */
function CollectionElement_show (doc)
{
    // compute the string representation and print it:
    doc.write ("<LI>" + this.toString2 () + "</LI>");
} // CollectionElement_show


/**
 * void CollectionElement_compareTo (CollectionElement otherObj)
 * Compare this CollectionElement with another one.
 * Return 0 if both are equal,
 * <0 if this CollectionElement is less than the other one,
 * >0 if the other one is less.
 */
function CollectionElement_compareTo (otherObj)
{
    if (otherObj == null)
    {
        return 1;
    } // if
    if (this.constructor != otherObj.constructor && 
        !otherObj.instanceOf (CollectionElement))
    {
        return -1;
    } // if
/*
    if (classOf (this) != classOf (otherObj))
    {
        if (classOf (this) < classOf (otherObj))
        {
            return -1;
        } // if
        else
        {
            return 1;
        } // else
    } // else if
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
} // CollectionElement_compareTo


/**
 * String CollectionElement_toString2 ()
 * Returns a string representation of this CollectionElement.
 * The string representation consists only of the id.
 */
function CollectionElement_toString2 ()
{
    // return the string:
    return this.id;
} // CollectionElement_toString2


/**
 * CollectionElement CollectionElement (String|int id)
 * Constructur of class CollectionElement.
 */
function CollectionElement (id)
{
    // set property values:
    this.id = id;                       // id
    this.key = ("" + id).toLowerCase (); // key used for sorting
} // CollectionElement

// create class form constructor:
createClass (CollectionElement, null,
{
    // define properties and assign initial values:
    id: 0,                              // id
    key: null,                          // key used for sorting

    // define methods:
    print: CollectionElement_print,     // output of CollectionElement
    show: CollectionElement_show,       // display the CollectionElement
    compareTo: CollectionElement_compareTo, // compare this
                                        // CollectionElement with another one
    toString2: CollectionElement_toString2 // get string representation
}); // createClass


//============= class Collection ==============================================
/******************************************************************************
 * The root interface/class in the <i>collection hierarchy</i>.  A collection
 * represents a group of objects, known as its <i>elements</i>.  Some
 * collections allow duplicate elements and others do not.  Some are ordered
 * and others unordered.  The JDK does not provide any <i>direct</i>
 * implementations of this interface: it provides implementations of more
 * specific subinterfaces like <tt>Set</tt> and <tt>List</tt>.  This interface
 * is typically used to pass collections around and manipulate them where
 * maximum generality is desired.
 *
 * <p><i>Bags</i> or <i>multisets</i> (unordered collections that may contain
 * duplicate elements) should implement this interface directly.
 *
 * <p>All general-purpose <tt>Collection</tt> implementation classes (which
 * typically implement <tt>Collection</tt> indirectly through one of its
 * subinterfaces) should provide two "standard" constructors: a void (no
 * arguments) constructor, which creates an empty collection, and a
 * constructor with a single argument of type <tt>Collection</tt>, which
 * creates a new collection with the same elements as its argument.  In
 * effect, the latter constructor allows the user to copy any collection,
 * producing an equivalent collection of the desired implementation type.
 * There is no way to enforce this convention (as interfaces cannot contain
 * constructors) but all of the general-purpose <tt>Collection</tt>
 * implementations in the Java platform libraries comply.
 *
 * <p>The "destructive" methods contained in this interface, that is, the
 * methods that modify the collection on which they operate, are specified to
 * throw <tt>UnsupportedOperationException</tt> if this collection does not
 * support the operation.  If this is the case, these methods may, but are not
 * required to, throw an <tt>UnsupportedOperationException</tt> if the
 * invocation would have no effect on the collection.  For example, invoking
 * the {@link #addAll(Collection)} method on an unmodifiable collection may,
 * but is not required to, throw the exception if the collection to be added
 * is empty.
 *
 * <p>Some collection implementations have restrictions on the elements that
 * they may contain.  For example, some implementations prohibit null elements,
 * and some have restrictions on the types of their elements.  Attempting to
 * add an ineligible element throws an unchecked exception, typically
 * <tt>NullPointerException</tt> or <tt>ClassCastException</tt>.  Attempting
 * to query the presence of an ineligible element may throw an exception,
 * or it may simply return false; some implementations will exhibit the former
 * behavior and some will exhibit the latter.  More generally, attempting an
 * operation on an ineligible element whose completion would not result in
 * the insertion of an ineligible element into the collection may throw an
 * exception or it may succeed, at the option of the implementation.
 * Such exceptions are marked as "optional" in the specification for this
 * interface. 
 *
 * <p>This interface is a member of the 
 * <a href="{@docRoot}/../guide/collections/index.html">
 * Java Collections Framework</a>.
 *
 * <p>Many methods in Collections Framework interfaces are defined in
 * terms of the {@link Object#equals(Object) equals} method.  For example,
 * the specification for the {@link #contains(Object) contains(Object o)}
 * method says: "returns <tt>true</tt> if and only if this collection
 * contains at least one element <tt>e</tt> such that
 * <tt>(o==null ? e==null : o.equals(e))</tt>."  This specification should
 * <i>not</i> be construed to imply that invoking <tt>Collection.contains</tt>
 * with a non-null argument <tt>o</tt> will cause <tt>o.equals(e)</tt> to be
 * invoked for any element <tt>e</tt>.  Implementations are free to implement
 * optimizations whereby the <tt>equals</tt> invocation is avoided, for
 * example, by first comparing the hash codes of the two elements.  (The
 * {@link Object#hashCode()} specification guarantees that two objects with
 * unequal hash codes cannot be equal.)  More generally, implementations of
 * the various Collections Framework interfaces are free to take advantage of
 * the specified behavior of underlying {@link Object} methods wherever the
 * implementor deems it appropriate.
 */

// Query Operations

/**
 * boolean Collection_isEmpty ()
 * Returns true if this collection contains no elements.
 */
function Collection_isEmpty ()
{
    return (this.size () == 0);
} // Collection_isEmpty


/**
 * boolean Collection_contains (Object elem)
 * Returns true if this collection contains the specified element.
 */
function Collection_contains (elem)
{
    // search for the element and return the result:
    return this.iterator ().contains (elem);
} // Collection_contains


/**
 * Array Collection_toArray ()
 * Returns an array containing all of the elements in this collection.  If
 * the collection makes any guarantees as to what order its elements are
 * returned by its iterator, this method must return the elements in the
 * same order.  The returned array will be "safe" in that no references to
 * it are maintained by the collection.  (In other words, this method must
 * allocate a new array even if the collection is backed by an Array).
 * The caller is thus free to modify the returned array.
 */
function Collection_toArray ()
{
    // loop through all elements and add them to the array:
    return this.forAll (
        function () {
            return iter.next ();
        } // function
    );
} // Collection_toArray


// Modification Operations

/**
 * boolean Collection_add ([Object elem])
 * Ensures that this collection contains the specified element (optional
 * operation).  Returns true if the collection changed as a
 * result of the call.  (Returns false if this collection does
 * not permit duplicates and already contains the specified element.)
 * Collections that support this operation may place limitations on what
 * elements may be added to the collection.  In particular, some
 * collections will refuse to add null´elements, and others will
 * impose restrictions on the type of elements that may be added.
 * Collection classes should clearly specify in their documentation any
 * restrictions on what elements may be added.
 */
function Collection_add (elem)
{
    throw new UnsupportedOperationException ();
} // Collection_add


/**
 * boolean Collection_remove (Object elem)
 * Removes a single instance of the specified element from this
 * collection, if it is present (optional operation).  More formally,
 * removes an element e such that (elem==null ? e==null :
 * elem.equals(e)), if the collection contains one or more such
 * elements.  Returns true if the collection contained the
 * specified element (or equivalently, if the collection changed as a
 * result of the call).
 */
function Collection_remove (elem)
{
    var iter = this.iterator ();

    // search for the element:
    if (iter.contains (elem))           // element was found?
    {
        // remove it:
        iter.remove ();
        return true;
    } // if element was found

    // the collection was not changed:
    return false;
} // Collection_remove


// Bulk Operations

/**
 * boolean Collection_containsAll (Collection c)
 * Returns true if this collection contains all of the elements in the specified
 * collection.
 */
function Collection_containsAll (c)
{
    var iter = c.iterator ();

    // check for each element of the specified collection if it is within
    // this collection:
    while (iter.hasNext ())
    {
        if (!this.contains (iter.next ()))
        {
            return false;
        } // if
    } // while iter.hasNext

    // found all elements:
    return true;
} // Collection_containsAll


/**
 * boolean Collection_addAll (Collection c)
 * Adds all of the elements in the specified collection to this collection.
 * Returns true if this collection changed as a result of the call.
 */
function Collection_addAll (c)
{
    var modified = false;
    var iter = c.iterator ();

    // for each element of the specified collection add it to this collection:
    while (iter.hasNext ())
    {
        if (this.add (iter.next ()))
        {
            modified = true;
        } // if
    } // while iter.hasNext

    // return the result:
    return modified;
} // Collection_addAll


/**
 * boolean Collection_removeAll (Collection c)
 * Removes from this collection all of its elements that are contained in
 * the specified collection.
 * Returns true if this collection changed as a result of the call.
 */
function Collection_removeAll (c)
{
    var modified = false;
    var iter = this.iterator ();

    // check for each element of the specified collection if it is within
    // this collection and remove it:
    while (iter.hasNext ())
    {
        if (c.contains (iter.next ()))
        {
            iter.remove ();
            modified = true;
        } // if
    } // while iter.hasNext

    // return the result:
    return modified;
} // Collection_removeAll


/**
 * boolean Collection_retainAll (Collection c)
 * Retains only the elements in this collection that are contained in the
 * specified collection.  In other words, removes from this collection all of
 * its elements that are not contained in the specified collection.
 * Returns true if this collection changed as a result of the call.
 */
function Collection_retainAll (c)
{
    var modified = false;
    var iter = this.iterator ();

    // check for each element of the specified collection if it is within
    // this collection and remove all elements which are not there:
    while (iter.hasNext ())
    {
        if (!c.contains (iter.next ()))
        {
            iter.remove ();
            modified = true;
        } // if
    } // while iter.hasNext

    // return the result:
    return modified;
} // Collection_retainAll


/**
 * boolean Collection_retainAllById (Array idList)
 * Retains only the elements in this collection whose ids are in the
 * specified id list.  In other words, removes from this collection all of
 * its elements whose ids are not contained in the specified id list.
 * Returns true if this collection changed as a result of the call.
 */
function Collection_retainAllById (idList)
{
    var modified = false;
    var iter = this.iterator ();
    var found = false;
    var elem = null;

    // add all elements from the id list to the collection:
    // check for each element of the specified collection if it is within
    // this collection and remove all elements which are not there:
    while (iter.hasNext ())
    {
        elem = iter.next ();

        // check if the element's id is part of the id list:
        found = false;
        for (i = 0; i < idList.length; i++)
        {
            if (elem.id = idList[i])
            {
                found = true;
                break;
            } // if
        } // for i

        // check if we found the element:
        if (!found)
        {
            iter.remove ();
            modified = true;
        } // if
    } // while iter.hasNext

    // return the result:
    return modified;
} // Collection_retainAllById


/**
 * boolean Collection_setElements (Collection availElements,
 *                                         Array idList)
 * Set the elements of the Collection.
 * This method first removes all old elements of the collection which are not
 * necessary and then adds the elements which are identified by the ids in the
 * idList and available in availElements.
 * The return value is true, if all elements where found and added to the list,
 * false otherwise.
 */
function Collection_setElements (availElements, idList)
{
    var i = 0;                          // loop counter
    var elem = null;                    // the actual element

    // drop not necessary elements from this collection:
    this.retainAllById (idList);

    // initialize the available property:
    this.allAvailable = true;
    // set the new id list:
    this.idList = idList;

    // add all elements from the id list to this collection:
    for (i = 0; i < idList.length; i++)
    {
        // check if the actual element is available and get it:
        elem = availElements.get (idList[i]);
        if (elem != availElements.notFound) // element found?
        {
            // add the element to the collection:
            this.add (elem);
        } // if element found
        else                            // element not found
        {
            this.allAvailable = false;  // not all elements are available
        } // else element not found
    } // for i

    // return the result:
    return this.allAvailable;
} // Collection_setElements


/**
 * boolean Collection_setAndLoadElements (
 *      Collection availElements, Array idList)
 * Set the elements of the list and load the available elements if necessary.
 */
function Collection_setAndLoadElements (availElements, idList)
{
    if (idList && idList != null && idList.length > 0)
    {
        // set the elements:
        this.setElements (availElements, idList);

        // check if all elements were found:
        if (this.allAvailable)          // all elements are available?
        {
            // make this list the actual one and display it:
            this.setActual ();
        } // if all elements are available
        else                            // not all elements are available
        {
            // get all available elements:
            this.getAvailable ();
        } // else not all elements are available
    } // if

    // return the result:
    return this.allAvailable;
} // Collection_setAndLoadElements


/**
 * void Collection_setLoaded ()
 * Tell the collection that the available elements are loaded.
 */
function Collection_setLoaded ()
{
} // Collection_setLoaded


/**
 * void Collection_clear ()
 * Removes all of the elements from this collection.
 * The collection will be empty after this call returns (unless it throws
 * an exception).
 */
function Collection_clear ()
{
    var iter = this.iterator ();

    // loop through this collection and remove each element:
    while (iter.hasNext ())
    {
        iter.next ();
        iter.remove ();
    } // while iter.hasNext

    this.idList = null;                 // no idList known
    this.allAvailable = false;          // no ids available
} // Collection_clear


//  String conversion

/**
 * String Collection_toString2 ()
 * Returns a string representation of this collection.  The string
 * representation consists of a list of the collection's elements in the
 * order they are returned by its iterator, enclosed in square brackets
 * ("[]").  Adjacent elements are separated by the characters
 * ", " (comma and space).  Elements are converted to strings as
 * by Object.toString2 ().
 */
function Collection_toString2 ()
{
    // begin the string:
    var buf = "[";
    var iter = this.iterator ();
    var hasNext = iter.hasNext ();
    var elem = null;

    // loop through all elements and add each of them:
    while (hasNext)
    {
        elem = iter.next ();
        buf += (elem == this ? "(this Collection)" : elem.toString2 ());
        hasNext = iter.hasNext ();
        if (hasNext)
        {
            buf += ", ";
        } // if
    } // while hasNext

    // finish the string:
    buf += "]";
    // return the result:
    return buf;
} // Collection_toString2


/**
 * void Collection_setActual ()
 * Set this collection as actual one.
 */
function Collection_setActual ()
{
} // Collection_setActual


/**
 * void Collection_getAvailable ()
 * Tell the collection to get the available elements.
 */
function Collection_getAvailable ()
{
} // Collection_getAvailable


/**
 * Object Collection_get (String|int id, ...)
 * Get element out of collection using its id.
 */
function Collection_get (id)
{
    var elem = null;                    // actual element
    var otherArgs = $A2 (arguments, 1); // get the arguments

    // loop through all elements of the list until the id is found or
    // the end of the list is reached:
    for (var iter = this.iterator.apply (this, otherArgs); iter.hasNext ();)
    {
        // get the element:
        elem = iter.next ();

        // check the element's id:
        if (elem.id == id)
        {
            // return the element:
            return elem;
        } // if
    } // for iter

    // the element was not found, return default value:
    return this.notFound;               // return error element
} // Collection_get


/**
 * Object Collection_find (String value)
 * Get element out of collection identified by its value.
 * This method finds the first element with this value.
 */
function Collection_find (value)
{
    var elem = null;                    // actual element
    var otherArgs = $A2 (arguments, 1); // get the arguments

    // loop through all elements of the collection until the value is found or
    // no more elements are available:
    for (var iter = this.iterator.apply (this, otherArgs); iter.hasNext ();)
    {
        // get the element:
        elem = iter.next ();

        // check the element's value:
        if (elem.value == value)
        {
            // return the element:
            return elem;
        } // if
    } // for iter

    // the element was not found, return default value:
    return this.notFound;               // return error element
} // Collection_find


/**
 * ObjectArray Collection_findAll (String value)
 * Get all elements out of the collection identified by the same value.
 * If no element was found the array is empty.
 */
function Collection_findAll (value)
{
    var elem = null;                    // actual element
    var retVal = new Array ();          // the result array
    var otherArgs = $A2 (arguments, 1); // get the arguments

    // loop through all elements of the collection until
    // no more elements are available:
    for (var iter = this.iterator.apply (this, otherArgs); iter.hasNext ();)
    {
        elem = iter.next ();
        if (elem.value == value)
        {
            retVal[retVal.length] = elem;
        } // if
    } // for iter

    // return the result:
    return retVal;
} // Collection_findAll


/**
 * Array Collection_forAll (FunctionObject fct, ArgumentArray iteratorArgs)
 * Iterate over all elements and perform a function for each of them.
 * If the function returns a value all values are concatenated to an array
 * which is returned.
 */
function Collection_forAll (fct, iteratorArgs)
{
    var retVal = new Array ();          // return value
    var otherArgs = $A2 (arguments, 2); // get the arguments

    // loop through all elements and perform the function on each of them:
    for (var iter = this.iterator.apply (this, iteratorArgs); iter.hasNext ();)
    {
        // get the next element and perform the function:
        retVal[retVal.length] =
            fct.apply (this, new Array (iter.next ()).concat (otherArgs));
    } // for iter

    // return the result:
    return retVal;
} // Collection_forAll


/**
 * Collection Collection (String|int id)
 * Constructor of class Collection.
 */
function Collection (id)
{
    // set property values:
    this.id = id;                       // the id of the list

    // ensure constraints:
} // Collection

// create class form constructor:
createClass (Collection, null,
{
    // define properties and assign initial values:
    id: 0,                              // the id of the list
    length: 0,                          // number of elements within the list
    notFound: null,                     // default object when required object
                                        // was not found
    idList: null,                       // the last list of ids (in setElements)
    allAvailable: false,                // all elements of an idList available?
    iteratorProto: null,                // kind of iterator (= prototype)

    // define methods:
    isEmpty: Collection_isEmpty,        // is the collection empty?
    contains: Collection_contains,      // is there a specific element
                                        // contained?
    toArray: Collection_toArray,        // convert the coll. to an array
    add: Collection_add,                // add a new element to collection
    remove: Collection_remove,          // remove element from collection
    containsAll: Collection_containsAll, // check whether the
                                        // collection contains all elements from
                                        // another collection
    addAll: Collection_addAll,          // add all elements from another
                                        // collection
    setElements: Collection_setElements, // set the elements of the collection
    setAndLoadElements: Collection_setAndLoadElements,
                                        // set and load all elements
    setLoaded: Collection_setLoaded,    // tell the collection that the
                                        // available elements are loaded
    removeAll: Collection_removeAll,    // remove all elements being
                                        // in another collection
    retainAll: Collection_retainAll,    // remove all elements being
                                        // not in another collection
    retainAllById: Collection_retainAllById, // retain specific
                                        // elements identified through their ids
    clear: Collection_clear,            // remove all elements
    toString2: Collection_toString2,    // get string representation
    setActual: Collection_setActual,    // set this list as actual one
    getAvailable: Collection_getAvailable, // get all available elements
    empty: Collection_clear,            // remove all elements from the list
    get: Collection_get,                // get an element identified by its id
    find: Collection_find,              // search for an element
    findAll: Collection_findAll,        // search for all element with the same
                                        // value
    iterator: function () {return new this.iteratorProto (this);},
                                        // get iterator
    size: notImplemented ("Collection.size"),
                                        // get size of collection
    forAll: Collection_forAll           // perform function for all elements
}); // createClass


//============= class Iterator ================================================
/******************************************************************************
 * An iterator over a collection.  Iterator takes the place of Enumeration in
 * the Java collections framework.  Iterators differ from enumerations in two
 * ways: <ul>
 *	<li> Iterators allow the caller to remove elements from the
 *	     underlying collection during the iteration with well-defined
 * 	     semantics.
 *	<li> Method names have been improved.
 * </ul><p>
 *
 * This interface is a member of the 
 * <a href="{@docRoot}/../guide/collections/index.html">
 * Java Collections Framework</a>.
 */

/**
 * Object Iterator_getFirst ()
 * Get the first element without storing it.
 */
function Iterator_getFirst ()
{
    if (this.startObj != null)          // start object was defined?
    {
        return this.startObj;
    } // if start object was defined
    else if (this.searchRoot != null)   // search root was defined?
    {
        return this.searchRoot;
    } // else if search root was defined
    else                                // no search root
    {
        return this.collection.firstElem;
    } // else no search root
} // Iterator_getFirst


/**
 * boolean Iterator_hasNext ()
 * Check if there is a next element.
 */
function Iterator_hasNext ()
{
    var node = this.p_actElem;            // the actual node

    if (this.p_nextElem != null)          // next element already fetched?
    {
        return true;                    // there is a next element
    } // if next element already fetched
    // compute the result from first node:
    else if (node == null)
    {
        return this.getFirst () != null;
    } // else if

    // didn't find element:
    return false;                       // return the result
} // Iterator_hasNext


/**
 * Object Iterator_next ()
 * Get the next element of the actual element.
 */
function Iterator_next ()
{
    if (this.p_nextElem != null)          // next element already fetched?
    {
        this.p_actElem = this.p_nextElem;   // store the actual element
        this.p_nextElem = null;           // no next element
        return this.p_actElem;            // return the actual element
    } // if next element already fetched
    else if (this.p_actElem == null)      // currently no element selected?
    {
        var node = this.getFirst ();

        if (node != null)               // node was found?
        {
            this.p_actElem = node;        // store the actual element
            return this.p_actElem;        // return the actual element
        } // if node was found
        else                            // didn't find element
        {
            return this.notFound;       // return error element
        } // else
    } // else if currently no element selected
    else                                // didn't find element
    {
        return this.notFound;           // return error element
    } // else didn't find element
} // Iterator_next


/**
 * Object Iterator_setActElem (Object actElem)
 * Set a new actual element.
 */
function Iterator_setActElem (actElem)
{
    // set the actual element:
    this.p_actElem = actElem;
} // Iterator_setActElem


/**
 * void Iterator_remove ()
 * Remove the last element which we got by calling next or prev.
 */
function Iterator_remove ()
{
    this.collection.remove (this.p_actElem);
} // Iterator_remove


/**
 * boolean Iterator_contains (Object elem)
 * Find the specified element. The element comparison is done through
 * Object.equals.
 * Returns true if the element was found, false otherwise.
 * After termination of the method the iterators stays at the object's position.
 */
function Iterator_contains (elem)
{
    // check if we have to search for a specific element:
    if (elem == null)
    {
        // just check if there is at least one element in the list which is
        // null:
        while (this.hasNext ())
        {
            if (this.next () == null)
            {
                return true;
            } // if
        } // while iter.hasNext
    } // if
    else
    {
        // search for the element:
        while (this.hasNext ())
        {
            if (elem.equals (this.next ()))
            {
                return true;
            } // if
        } // while iter.hasNext
    } // else

    // the element was not found:
    return false;
} // Iterator_contains


/**
 * Iterator Iterator (Collection collection)
 * Constructor of class Iterator.
 */
function Iterator (collection)
{
    // define properties and assign initial values:
    this.collection = collection;       // the collection

    // ensure constraints:
    if (collection != null && collection.notFound != null)
    {
        this.notFound = collection.notFound;
    } // if
} // Iterator

// create class form constructor:
createClass (Iterator, null,
{
    // define properties and assign initial values:
    collection: null,                   // the collection
    p_actElem: null,                    // the actual element
    p_nextElem: null,                   // the next element
    notFound: null,                     // default value when element was not
                                        // found

    // define methods:
    hasNext: notImplemented ("Iterator.hasNext"), // is there a next element?
    next: notImplemented ("Iterator.next"), // get the next element
    remove: notImplemented ("Iterator.remove"), // remove the last element which
                                        // was returned by next or prev
    setActElem: notImplemented ("Iterator.setActElem"), // set actual element
    contains: Iterator_contains         // find a specific element
/*
    getFirst: Iterator_getFirst,        // get the first element
    hasNext: Iterator_hasNext,          // is there a next element?
    next: Iterator_next,                // get the next element
    setActElem: Iterator_setActElem,    // set actual element
    remove: Iterator_remove,            // remove the last element which
                                        // was returned by next or prev
*/
}); // createClass


//============= common functions ==============================================

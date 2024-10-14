/******************************************************************************
 * This file contains all JavaScript classes and their methods which are used
 * to create and manage lists. <BR>
 * 
 * @version     $Id: scriptLists.js,v 1.14 2006/04/12 17:10:00 klreimue Exp $
 *
 * @author      Klaus Reimüller (KR) 20060412
 ******************************************************************************
 */

//============= declared classes and functions ================================
// class List
// class ListIterator
// class SubList
// class LoadableList


//============= necessary classes and variables ===============================


//============= declarations ==================================================
// constants:

// variables:

//============= initializations ===============================================



//============= class List ====================================================
/******************************************************************************
 * An ordered collection (also known as a <i>sequence</i>).  The user of this
 * interface has precise control over where in the list each element is
 * inserted.  The user can access elements by their integer index (position in
 * the list), and search for elements in the list.<p>
 *
 * Unlike sets, lists typically allow duplicate elements.  More formally,
 * lists typically allow pairs of elements <tt>e1</tt> and <tt>e2</tt>
 * such that <tt>e1.equals(e2)</tt>, and they typically allow multiple
 * null elements if they allow null elements at all.  It is not inconceivable
 * that someone might wish to implement a list that prohibits duplicates, by
 * throwing runtime exceptions when the user attempts to insert them, but we
 * expect this usage to be rare.<p>
 *
 * The <tt>List</tt> interface places additional stipulations, beyond those
 * specified in the <tt>Collection</tt> interface, on the contracts of the
 * <tt>iterator</tt>, <tt>add</tt>, <tt>remove</tt>, <tt>equals</tt>, and
 * <tt>hashCode</tt> methods.  Declarations for other inherited methods are
 * also included here for convenience.<p>
 *
 * The <tt>List</tt> interface provides four methods for positional (indexed)
 * access to list elements.  Lists (like Java arrays) are zero based.  Note
 * that these operations may execute in time proportional to the index value
 * for some implementations (the <tt>LinkedList</tt> class, for
 * example). Thus, iterating over the elements in a list is typically
 * preferable to indexing through it if the caller does not know the
 * implementation.<p>
 *
 * The <tt>List</tt> interface provides a special iterator, called a
 * <tt>ListIterator</tt>, that allows element insertion and replacement, and
 * bidirectional access in addition to the normal operations that the
 * <tt>Iterator</tt> interface provides.  A method is provided to obtain a
 * list iterator that starts at a specified position in the list.<p>
 *
 * The <tt>List</tt> interface provides two methods to search for a specified
 * object.  From a performance standpoint, these methods should be used with
 * caution.  In many implementations they will perform costly linear
 * searches.<p>
 *
 * The <tt>List</tt> interface provides two methods to efficiently insert and
 * remove multiple elements at an arbitrary point in the list.<p>
 *
 * Note: While it is permissible for lists to contain themselves as elements,
 * extreme caution is advised: the <tt>equals</tt> and <tt>hashCode</tt>
 * methods are no longer well defined on a such a list.
 *
 * <p>Some list implementations have restrictions on the elements that
 * they may contain.  For example, some implementations prohibit null elements,
 * and some have restrictions on the types of their elements.  Attempting to
 * add an ineligible element throws an unchecked exception, typically
 * <tt>NullPointerException</tt> or <tt>ClassCastException</tt>.  Attempting
 * to query the presence of an ineligible element may throw an exception,
 * or it may simply return false; some implementations will exhibit the former
 * behavior and some will exhibit the latter.  More generally, attempting an
 * operation on an ineligible element whose completion would not result in
 * the insertion of an ineligible element into the list may throw an
 * exception or it may succeed, at the option of the implementation.
 * Such exceptions are marked as "optional" in the specification for this
 * interface. 
 *
 * <p>This interface is a member of the 
 * <a href="{@docRoot}/../guide/collections/index.html">
 * Java Collections Framework</a>.
 */

/**
 * boolean List_add (Object elem, [index], [boolean nocheck])
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
function List_add (elem, index, nocheck)
{
    throw new UnsupportedOperationException ();
} // List_add


/**
 * boolean List_set (int index, Object elem)
 * Replaces the element at the specified position in this list with the
 * specified element.
 */
function List_set (index, elem)
{
    throw new UnsupportedOperationException ();
} // List_set


/**
 * boolean List_removeIndexed (int index)
 * Removes the element at the specified position in this list (optional
 * operation).  Shifts any subsequent elements to the left (subtracts one
 * from their indices).
 * Returns the element that was removed from the list.
 */
function List_removeIndexed (index)
{
    throw new UnsupportedOperationException ();
} // List_removeIndexed


// Search Operations

/**
 * int List_indexOf (Object elem)
 * Returns the index in this list of the first occurence of the specified
 * element, or -1 if the list does not contain this element.  More
 * formally, returns the lowest index i such that (elem==null ?
 * getIndexed (i)==null : elem.equals (getIndexed (i))),
 * or -1 if there is no such index.
 */
function List_indexOf (elem)
{
    var iter = this.iterator ();

    // search for the element:
    if (iter.contains (elem))
    {
        // return the element index:
        return iter.prevIndex;
    } // if

    // the element was not found:
    return -1;
} // List_indexOf


/**
 * int List_lastIndexOf (Object elem)
 * Returns the index in this list of the last occurence of the specified
 * element, or -1 if the list does not contain this element.  More
 * formally, returns the highest index i such that (elem==null ?
 * getIndexed(i)==null : elem.equals(getIndexed(i))),
 * or -1 if there is no such index.
 */
function List_lastIndexOf (elem)
{
    var iter = this.iterator (this.size ());

    // check if we have to find a specific element:
    if (elem == null)
    {
        // get index of last null element:
        while (iter.hasPrev ())
        {
            if (iter.prev () == null)
            {
                return iter.nextIndex;
            } // if
        } // while iter.hasNext
    } // if
    else
    {
        // search for the element and return its index:
        while (iter.hasPrev ())
        {
            if (elem.equals (iter.prev ()))
            {
                return iter.nextIndex;
            } // if
        } // while iter.hasNext
    } // else

    // the element was not found:
    return -1;
} // List_lastIndexOf


// Bulk Operations

/**
 * void List_clear ()
 * Removes all of the elements from this collection.
 * The collection will be empty after this call returns (unless it throws
 * an exception).
 */
function List_clear ()
{
    this.removeRange (0, this.size ());

    this.idList = null;                 // no idList known
    this.allAvailable = false;          // no ids available
} // List_clear


/**
 * boolean List_addAll (Collection c, int index)
 * Adds all of the elements in the specified collection to this collection.
 * Returns true if this collection changed as a result of the call.
 */
function List_addAll (c, index)
{
    var modified = false;
    var iter = c.iterator ();

    // for each element of the specified collection add it to this collection:
    while (iter.hasNext ())
    {
        if (this.add (iter.next (), index++))
        {
            modified = true;
        } // if
    } // while iter.hasNext

    // return the result:
    return modified;
} // List_addAll


// Iterators

/**
 * SubList List_subList (int fromIndex, int toIndex)
 * Returns a view of the portion of this list between fromIndex,
 * inclusive, and toIndex, exclusive.  (If fromIndex and
 * toIndex are equal, the returned list is empty.)  The returned
 * list is backed by this list, so changes in the returned list are
 * reflected in this list, and vice-versa.  The returned list supports all
 * of the optional list operations supported by this list.
 */
function List_subList ()
{
    // create sub list and return it:
    return new SubList (this, fromIndex, toIndex);
} // List_subList


// Comparison and hashing

/**
 * boolean List_equals (Object obj)
 * Compares the specified object with this list for equality.  Returns
 * true if and only if the specified object is also a list, both
 * lists have the same size, and all corresponding pairs of elements in
 * the two lists are equal.  (Two elements e1 and
 * e2 are equal if (e1==null ? e2==null :
 * e1.equals(e2)).)  In other words, two lists are defined to be
 * equal if they contain the same elements in the same order.
 */
function List_equals (obj)
{
    // check for identity:
    if (obj == this)
    {
        return true;
    } // if

    // check classes of list:
    if (!obj.instanceOf (List))
    {
        return false;
    } // if

    // loop through both lists and check each element:
    var elem1 = null;
    var elem2 = null;
    var iter1 = this.iterator ();
    var iter2 = obj.iterator ();
    while (iter1.hasNext () && iter2.hasNext ())
    {
        elem1 = iter1.next ();
        elem2 = iter2.next ();
        if (!(elem1 == null ? elem2 == null : elem1.equals (elem2)))
        {
            return false;
        } // if
    } // while

    // check if there are some elements left:
    return !(iter1.hasNext () || iter2.hasNext ());
} // List_equals


/**
 * int List_hashCode ()
 * Returns the hash code value for this list.
 */
function List_hashCode (obj)
{
    var hashCode = 1;
    var iter = this.iterator ();
    var elem = null;

    // loop through the list and add the hashCode for each element to the
    // result:
    while (iter.hasNext ())
    {
        elem = iter.next ();
        hashCode = 31 * hashCode +
                   ((elem == null || !elem.hashCode) ? 0 : elem.hashCode ());
    } // while iter.hasNext

    // return the result:
    return hashCode;
} // List_hashCode


/**
 * void List_removeRange (int fromIndex, int toIndex)
 * Removes from this list all of the elements whose index is between
 * fromIndex, inclusive, and toIndex, exclusive.
 * Shifts any succeeding elements to the left (reduces their index).  This
 * call shortens the list by (toIndex - fromIndex)
 * elements.  (If toIndex<=fromIndex, this operation has no effect.).
 */
function List_removeRange (fromIndex, toIndex)
{
    var iter = this.iterator (fromIndex);
    var count = toIndex - fromIndex;

    // loop through this collection and remove each element within range:
    for (var i = 0; i < count; i++)
    {
        iter.next ();
        iter.remove ();
    } // for i
} // List_removeRange


/**
 * Object List_getIndexed (int index)
 * Get element out of the list identified by its index.
 */
function List_getIndexed (index)
{
    throw new UnsupportedOperationException ();
} // List_getIndexed


/**
 * List List (id)
 * Constructor of class List.
 */
function List (id)
{
    // call super constructor(s):
    Collection.apply (this, arguments);

    // set property values:

    // ensure constraints:
} // List

// create class form constructor:
createClass (List, Collection,
{
    // define properties and assign initial values:
    iteratorProto: ListIterator,        // the list iterator prototype
    modCount: 0,                        // the number of times this list has
                                        // been structurally modified.
                                        // Structural modifications are those
                                        // that change the size of the
                                        // list, or otherwise perturb it in such
                                        // a fashion that iterations in
                                        // progress may yield incorrect results.

    // define methods:
    add: List_add,                      // add a new element to list
    set: List_set,                      // replace element within list
    removeIndexed: List_removeIndexed,  // remove element from list
                                        // found by its index
    dropIndexed: List_removeIndexed,    // drop element from list
                                        // found by its index
    indexOf: List_indexOf,              // get index of element within list
    lastIndexOf: List_lastIndexOf,      // get last index of element
    clear: List_clear,                  // remove all elements from list
    addAll: List_addAll,                // add all elements from another
                                        // collection to this one
    subList: List_subList,              // get sub list
    equals: List_equals,                // compare two lists
    hashCode: List_hashCode,            // get the hash code
    removeRange: List_removeRange,      // remove range of elements
    getIndexed: List_getIndexed         // get element by its index
}); // createClass


//============= class ListIterator ============================================

/**
 * Object ListIterator_getFirst ()
 * Get the first element without storing it.
 */
/*
function ListIterator_getFirst ()
{
    if (this.collection.length > 0)     // there is at least one element?
    {
        if (this.startIndex > 0)        // start index is defined?
        {
            if (this.startIndex < this.collection.length)
                                        // valid start index?
            {
                this.actIndex = this.startIndex; // store the index of the
                                        // actual element
            } // if valid start index
            else                        // didn't find element
            {
                return this.notFound;   // return error element
            } // else
        } // if start index is defined
        else                            // no start index
        {
            this.actIndex = 0;          // store the index of the actual element
        } // else no start index
        this.p_actElem = this.collection[this.actIndex];
                                        // store the actual element
        return this.p_actElem;            // return the actual element
    } // if there is at least one element
    else                                // didn't find element
    {
        return this.notFound;           // return error element
    } // else
} // ListIterator_getFirst
*/


/**
 * boolean ListIterator_hasNext ()
 * Check if there is a next element.
 */
function ListIterator_hasNext ()
{
    // check if we reached the end of the list:
    return (this.cursor != this.collection.size ());
} // ListIterator_hasNext


/**
 * Object ListIterator_next ()
 * Get the next element of the actual element.
 */
function ListIterator_next ()
{
    this.checkForComodification ();
    try
    {
        var next = this.collection.getIndexed (this.cursor);
        this.lastRet = this.cursor++;
        return next;
    } // try
    catch (IndexOutOfBoundsException)
    {
        this.checkForComodification ();
        throw new NoSuchElementException ();
    } // catch
} // ListIterator_next


/**
 * boolean ListIterator_hasPrev ()
 * Check if there is a previous element.
 */
function ListIterator_hasPrev ()
{
    // check if reached the beginning of the list:
    return (this.cursor != 0);
} // ListIterator_hasPrev


/**
 * Object ListIterator_prev ()
 * Get the previous element of the actual element.
 */
function ListIterator_prev ()
{
    this.checkForComodification ();
    try
    {
        var i = this.cursor - 1;
        var prev = this.collection.getIndexed (i);
        this.lastRet = this.cursor = i;
        return prev;
    } // try
    catch (IndexOutOfBoundsException)
    {
        this.checkForComodification ();
        throw new NoSuchElementException ();
    } // catch
} // ListIterator_prev


/**
 * int ListIterator_nextIndex ()
 * Get index of next element.
 */
function ListIterator_nextIndex ()
{
    return (this.cursor);
} // ListIterator_nextIndex


/**
 * int ListIterator_prevIndex ()
 * Get index of previous element.
 */
function ListIterator_prevIndex ()
{
    return (this.cursor - 1);
} // ListIterator_prevIndex


/**
 * Object ListIterator_setActElem (Object actElem)
 * Set a new actual element.
 */
function ListIterator_setActElem (actElem)
{
    // set the actual element:
    this.p_actElem = actElem;
} // ListIterator_setActElem


/**
 * void ListIterator_remove ()
 * Remove the last element which we got by calling next or prev.
 */
function ListIterator_remove ()
{
    if (this.lastRet == -1)
    {
        throw new IllegalStateException ();
    } // if
    this.checkForComodification ();

    try
    {
        this.collection.remove (lastRet);
        if (this.lastRet < this.cursor)
        {
            this.cursor--;
        } // if
        this.lastRet = -1;
        this.expectedModCount = this.collection.modCount;
    } // try
    catch (IndexOutOfBoundsException)
    {
        throw new ConcurrentModificationException ();
    } // catch
//    this.list.remove (this.p_actElem);
} // ListIterator_remove


/**
 * void ListIterator_add (Object newElem)
 * Add a new element to the collection at the current position.
 */
function ListIterator_add (newElem)
{
    try
    {
        this.collection.add (this.cursor++, newElem);
        this.lastRet = -1;
        this.expectedModCount = this.collection.modCount;
    } // try
    catch (IndexOutOfBoundsException)
    {
        throw new ConcurrentModificationException ();
    } // catch
//    this.list.addAfter (newElem, this.p_actElem);
} // ListIterator_add


/**
 * void ListIterator_set (Object newElem)
 * Replace the current element within the collection.
 */
function ListIterator_set (newElem)
{
    if (this.lastRet == -1)
    {
        throw new IllegalStateException ();
    } // if
    this.checkForComodification ();

    try
    {
        this.collection.set (this.lastRet, newElem);
        this.expectedModCount = this.collection.modCount;
    } // try
    catch (IndexOutOfBoundsException)
    {
        throw new ConcurrentModificationException ();
    } // catch
//    this.list.addAfter (newElem, this.p_actElem);
} // ListIterator_set


/**
 * void checkForComodification ()
 * Check if there occurred a modification at the original list.
 */
function checkForComodification ()
{
    if (this.collection.modCount != this.expectedModCount)
    {
        throw new ConcurrentModificationException ();
    } // if
} // checkForComodification


/**
 * ListIterator ListIterator (List collection, [int startIndex],
 *                            [Object startObj])
 * Constructor of class ListIterator.
 */
function ListIterator (collection, startIndex, startObj)
{
    // call super constructor(s):
    Iterator.apply (this, arguments);

    // set property values:
    this.expectedModCount = this.collection.modCount;
                                        // used for detecting concurrent
                                        // modifications

    // ensure constraints:
    if (startObj && startObj != null)
    {
        // search for the object within the list:
        var index = this.collection.indexOf (startObj);
        if (index >= 0)
        {
            this.cursor = index;
            this.startObj = startObj;
            this.p_nextElem = startObj;
        } // if
    } // if

    if (this.cursor == 0 &&
        startIndex && startIndex != null && startIndex > 0 &&
        startIndex < this.collection.size ())
    {
        this.startIndex = startIndex;
        this.cursor = startIndex;
    } // if
} // ListIterator

// create class form constructor:
createClass (ListIterator, Iterator,
{
    // define properties and assign initial values:
    cursor: 0,                          // index of next element
    lastRet: -1,                        // index of element returned by
    expectedModCount: 0,                // used for detecting concurrent
                                        // modifications
    startIndex: null,                   // index where to start the search
    startObj: null,                     // object where to start the search´

    // define methods:
    hasNext: ListIterator_hasNext,      // is there a next element?
    next: ListIterator_next,            // get the next element
    hasPrev: ListIterator_hasPrev,      // is there a previous element?
    prev: ListIterator_prev,            // get the previous element
    nextIndex: ListIterator_nextIndex,  // get index of next element
    prevIndex: ListIterator_prevIndex,  // get index of previous element
    setActElem: ListIterator_setActElem, // set actual element
    remove: ListIterator_remove,        // remove the last element which was
                                        // returned be next or prev
    add: ListIterator_add,              // insert a new element into the list
    set: ListIterator_set               // replace the last element which was
                                        // returned by next or prev
/*
    getFirst: ListIterator_getFirst,    // get the first element
*/
}); // createClass


//============= class SubList =================================================

/**
 * void SubList_add (Object element, int index)
 */
function SubList_add (element, index)
{
    if (index < 0 || index > this.size)
    {
        throw new IndexOutOfBoundsException ();
    } // if

    this.checkForComodification ();
    this.l.add (element, index + this.offset);
    this.expectedModCount = this.l.modCount;
    this.size++;
    this.modCount++;
} // SubList_add


/**
 * Object SubList_removeIndexed (int index)
 */
function SubList_removeIndexed (index)
{
    this.rangeCheck (index);
    this.checkForComodification ();
    var result = this.l.removeIndexed (index + this.offset);
    this.expectedModCount = this.l.modCount;
    this.size--;
    this.modCount++;
    return result;
} // SubList_removeIndexed


/**
 * SubList_set (int index, Object element)
 */
function SubList_set (index, element)
{
    this.rangeCheck (index);
    this.checkForComodification ();
    return this.l.set (index + this.offset, element);
} // SubList_set


/**
 * SubList_addAll (Collection c, [int index])
 */
function SubList_addAll (c, index)
{
    if (isNaN (index))
    {
        index = this.size ();
    } // if

    var cSize = c.size ();
    if (cSize == 0)
    {
        return false;
    } // if

    this.checkForComodification ();
    this.l.addAll (c, this.offset + index);
    this.expectedModCount = this.l.modCount;
    this.size += cSize;
    this.modCount++;
    return true;
} // SubList_addAll


/**
 * void SubList_removeRange (int fromIndex, int toIndex)
 */
function SubList_removeRange (fromIndex, toIndex)
{
    this.checkForComodification ();
    this.l.removeRange (fromIndex + this.offset, toIndex + this.offset);
    this.expectedModCount = this.l.modCount;
    this.size -= (toIndex - fromIndex);
    this.modCount++;
} // SubList_removeRange


/**
 * Object SubList_getIndexed (int index)
 */
function SubList_getIndexed (index)
{
    this.rangeCheck (index);
    this.checkForComodification ();
    return this.l.getIndexed (index + this.offset);
} // SubList_getIndexed


/**
 * int SubList_size ()
 */
function SubList_size ()
{
    this.checkForComodification ();
    return this.size;
} // SubList_size


/**
 * List SubList_subList (int fromIndex, int toIndex)
 */
function SubList_subList (fromIndex, toIndex)
{
    return new SubList (this, fromIndex, toIndex);
} // SubList_subList


/**
 * void SubList_rangeCheck (int index)
 */
function SubList_rangeCheck (index)
{
    if (index < 0 || index >= this.size)
    {
        throw new IndexOutOfBoundsException("Index: " + index +
                                            ", Size: " + this.size);
    } // if
} // SubList_rangeCheck


/**
 * void SubList_checkForComodification ()
 */
function SubList_checkForComodification ()
{
    if (this.l.modCount != this.expectedModCount)
    {
        throw new ConcurrentModificationException ();
    } // if
} // SubList_checkForComodification


/**
 * SubList SubList (List list, int fromIndex, int toIndex)
 * Constructor of class SubList.
 */
function SubList (id, frame, cyclic)
{
    // check preconditions:
    if (fromIndex < 0)
        throw new IndexOutOfBoundsException ("fromIndex = " + fromIndex);
    if (toIndex > list.size ())
        throw new IndexOutOfBoundsException ("toIndex = " + toIndex);
    if (fromIndex > toIndex)
        throw new IllegalArgumentException ("fromIndex(" + fromIndex +
                                           ") > toIndex(" + toIndex + ")");

    // call super constructor(s):
    List.apply (this, arguments);

    // set property values:
    this.l = list;                      // the basic list
    this.offset = fromIndex;            // offset to basic list
    this.size = toIndex - fromIndex;    // size of sub list
    this.expectedModCount = list.modCount; // used for checking whether the
                                        // basic list has changed
} // SubList

// create class form constructor:
createClass (SubList, List,
{
    // define properties and assign initial values:
    iteratorProto: ListIterator,        // the list iterator prototype
    l: null,                            // the basic list
    offset: 0,                          // offset to basic list

    // define methods:
    add: SubList_add,                   // add a new element to collection
    removeIndexed: SubList_removeIndexed, // remove element from collection
    set: SubList_set,                   // replace element
    addAll: SubList_addAll,             // add all elements from another list
    removeRange: SubList_removeRange,   // remove range of elements
    getIndexed: SubList_getIndexed,     // get an element by its index
    size: SubList_size,                 // get size of list
    subList: SubList_subList,           // get sub list
    rangeCheck: SubList_rangeCheck,     // check index for valid range
    checkForComodification: SubList_checkForComodification
                                        // check if there ocurred a modification
                                        // on the basic list
}); // createClass


//============= class LoadableList ============================================

/**
 * boolean LoadableList_setElements (LoadableList availElements, Array idList)
 * Set the elements of the tuple list.
 * This method first drops all old elements of the tuple list and then sets
 * the elements to those identified by the ids in the idList and available in
 * availElements.
 * The return value is true, if all elements where found and added to the list,
 * false otherwise.
 */
function LoadableList_setElements (availElements, idList)
{
    var i = 0;                          // loop counter
    var elem = null;                    // the actual element

    // drop all old elements:
    this.empty ();

    // initialize the available property:
    this.allAvailable = true;
    // set the new id list:
    this.idList = idList;

    // add all elements in the id list to the tuple list:
    for (i = 0; i < idList.length; i++)
    {
        // check if the actual element is available and get it:
        elem = availElements.get (idList[i]);
        if (elem != availElements.notFound) // element found?
        {
            // add the element to the tuple list:
            this.add (elem);
        } // if element found
        else                            // element not found
        {
            this.allAvailable = false;  // not all elements are available
        } // else element not found
    } // for

    // return the result:
    return this.allAvailable;
} // LoadableList_setElements


/**
 * boolean LoadableList_setAndLoadElements (LoadableList availElements, Array idList)
 * Set the elements of the list and load the available elements if necessary.
 */
function LoadableList_setAndLoadElements (availElements, idList)
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
} // LoadableList_setAndLoadElements


/**
 * void LoadableList_setLoaded ()
 * Tell the tuple list that the available elements are loaded.
 */
function LoadableList_setLoaded ()
{
} // LoadableList_setLoaded


/**
 * void LoadableList_setActual ()
 * Set this tuple list as actual one.
 */
function LoadableList_setActual ()
{
} // LoadableList_setActual


/**
 * void LoadableList_getAvailable ()
 * Tell the tuple list to get the available elements.
 */
function LoadableList_getAvailable ()
{
} // LoadableList_getAvailable


/**
 * LoadableList LoadableList (id)
 * Constructor of class LoadableList.
 */
function LoadableList (id)
{
    // set property values:

    // ensure constraints:
} // LoadableList

// create class form constructor:
createClass (LoadableList, null,
{
    // define properties and assign initial values:
    idList: null,                       // the last list of ids (in setElements)
    allAvailable: false,                // all elements of an idList available?

    // define methods:
    setElements: LoadableList_setElements, // set the elements of the list
    setAndLoadElements: LoadableList_setAndLoadElements,
                                        // set and load all elements
    setLoaded: LoadableList_setLoaded,  // tell the tuple list that the
                                        // available elements are loaded
    setActual: LoadableList_setActual,  // set this list as actual one
    getAvailable: LoadableList_getAvailable // get all available elements
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

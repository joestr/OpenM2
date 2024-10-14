/******************************************************************************
 * This file contains all JavaScript classes and their methods which are used
 * to create and manage sets. <BR>
 * The set implementation is based upon the java implementation of
 * sets. (J2SDK 5.0)
 * 
 * @version     $Id: scriptSet.js,v 1.1 2006/04/11 15:52:08 klreimue Exp $
 *
 * @author      Klaus Reimüller (KR) 20060320
 ******************************************************************************
 */

//============= declared classes and functions ================================
// class Set


//============= necessary classes and variables ===============================
// class Function
// class Object

// messages:


//============= declarations ==================================================

// constants:

// variables:


//============= initializations ===============================================


//============= class Set =====================================================
/******************************************************************************
 * A collection that contains no duplicate elements.  More formally, sets
 * contain no pair of elements <code>e1</code> and <code>e2</code> such that
 * <code>e1.equals(e2)</code>, and at most one null element.  As implied by
 * its name, this interface models the mathematical <i>set</i> abstraction.<p>
 *
 * The <tt>Set</tt> interface places additional stipulations, beyond those
 * inherited from the <tt>Collection</tt> interface, on the contracts of all
 * constructors and on the contracts of the <tt>add</tt>, <tt>equals</tt> and
 * <tt>hashCode</tt> methods.  Declarations for other inherited methods are
 * also included here for convenience.  (The specifications accompanying these
 * declarations have been tailored to the <tt>Set</tt> interface, but they do
 * not contain any additional stipulations.)<p>
 *
 * The additional stipulation on constructors is, not surprisingly,
 * that all constructors must create a set that contains no duplicate elements
 * (as defined above).<p>
 *
 * Note: Great care must be exercised if mutable objects are used as set
 * elements.  The behavior of a set is not specified if the value of an object
 * is changed in a manner that affects equals comparisons while the object is
 * an element in the set.  A special case of this prohibition is that it is
 * not permissible for a set to contain itself as an element.
 *
 * <p>Some set implementations have restrictions on the elements that
 * they may contain.  For example, some implementations prohibit null elements,
 * and some have restrictions on the types of their elements.  Attempting to
 * add an ineligible element throws an unchecked exception, typically
 * <tt>NullPointerException</tt> or <tt>ClassCastException</tt>.  Attempting
 * to query the presence of an ineligible element may throw an exception,
 * or it may simply return false; some implementations will exhibit the former
 * behavior and some will exhibit the latter.  More generally, attempting an
 * operation on an ineligible element whose completion would not result in
 * the insertion of an ineligible element into the set may throw an
 * exception or it may succeed, at the option of the implementation.
 * Such exceptions are marked as "optional" in the specification for this
 * interface. 
 *
 * <p>This interface is a member of the 
 * <a href="{@docRoot}/../guide/collections/index.html">
 * Java Collections Framework</a>.
 */

// Comparison and hashing

/**
 * boolean Set_equals (Set obj)
 * Compares the specified object with this set for equality.  Returns
 * true if the given object is also a set, the two sets have
 * the same size, and every member of the given set is contained in
 * this set.  This ensures that the equals method works
 * properly across different implementations of the Set interface.
 */
function Set_equals (obj)
{
    // check for identity:
    if (obj == this)
    {
        return true;
    } // if

    // check classes of set:
    if (!obj.instanceOf (Set))
    {
        return false;
    } // if

    // check size:
    if (obj.size () != this.size ())
    {
        return false;
    } // if

    // check if all elements of the other collection are contained in this one:
    try
    {
        return this.containsAll (obj);
    } // try
    catch (e)
    {
        return false;
    } // catch
} // Set_equals


/**
 * int Set_hashCode ()
 * Returns the hash code value for this set.  The hash code of a set is
 * defined to be the sum of the hash codes of the elements in the set.
 * This ensures that s1.equals(s2) implies that
 * s1.hashCode()==s2.hashCode() for any two sets s1
 * and s2, as required by the general contract of
 * Object.hashCode.
 */
function Set_hashCode ()
{
    var h = 0;
    var iter = this.iterator ();
    var obj = null;

    // loop through all elements and add the hash code:
    while (iter.hasNext ())
    {
        obj = iter.next ();
        if (obj != null)
        {
            h += obj.hashCode ();
        } // if
    } // while

    // return the result:
    return h;
} // Set_hashCode


// Bulk Operations

/**
 * boolean Set_removeAll (Set c)
 * Removes from this Set all of its elements that are contained in
 * the specified Set.
 * Returns true if this Set changed as a result of the call.
 */
function Set_removeAll (c)
{
    var modified = false;
    var iter = this.iterator ();

    // check for each element of the specified Set if it is within
    // this Set and remove it:
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
} // Set_removeAll


/**
 * Set Set (String|int id)
 * Constructor of class Set.
 */
function Set (id)
{
    // call super constructor(s):
    Collection.apply (this, arguments);

    // set property values:

    // ensure constraints:
} // Set

// create class form constructor:
createClass (Set, Collection,
{
    // define properties and assign initial values:

    // define methods:
    equals: Set_equals,                 // is the Set equal to another one?
    hashCode: Set_hashCode,             // get the hash code of the set
    removeAll: Set_removeAll,           // remove all elements being
                                        // in another Set
    size: notImplemented ("Set.size")   // get size of Set
}); // createClass


//============= common functions ==============================================

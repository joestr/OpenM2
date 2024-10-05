/******************************************************************************
 * This file contains all JavaScript classes and their methods which are used
 * to create and manage maps. <BR>
 * A map is an object that maps keys to values. A map cannot contain duplicate
 * keys; each key can map to at most one value. 
 *
 * The Map provides three collection views, which allow a map's contents to be
 * viewed as a set of keys, collection of values, or set of key-value mappings.
 * The order of a map is defined as the order in which the iterators on the
 * map's collection views return their elements. Some map implementations, like
 * the TreeMap class, make specific guarantees as to their order; others, like
 * the HashMap class, do not. 
 * 
 * @version     $Id: scriptMap.js,v 1.2 2006/04/12 17:10:00 klreimue Exp $
 *
 * @author      Klaus Reimüller (KR) 20060320
 ******************************************************************************
 */

//============= declared classes and functions ================================
// class SimpleEntry
// class Map
// class KeySetIterator
// class ValuesIterator


//============= necessary classes and variables ===============================
// class Function
// class Object

// messages:


//============= declarations ==================================================

// constants:

// variables:


//============= initializations ===============================================


//============= class SimpleEntry =============================================

/**
 * void SimpleEntry_print ()
 * Make an output of the SimpleEntry.
 */
function SimpleEntry_print ()
{
    alert (this.toString2 ());
} // SimpleEntry_print


/**
 * void SimpleEntry_show (DocumentObject doc)
 * Display the SimpleEntry.
 */
function SimpleEntry_show (doc)
{
    // compute the string representation and print it:
    doc.write ("<LI>" + this.toString2 () + "</LI>");
} // SimpleEntry_show


/**
 * void SimpleEntry_compareTo (SimpleEntry otherObj)
 * Compare this SimpleEntry with another one.
 * Return 0 if both are equal,
 * <0 if this SimpleEntry is less than the other one,
 * >0 if the other one is less.
 */
function SimpleEntry_compareTo (otherObj)
{
    if (otherObj == null)
    {
        return 1;
    } // if
    else if (classOf (this) != classOf (otherObj))
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
    else if (this.key.equals (otherObj.key))
    {
        if (this.value < otherObj.value)
        {
            return -1;
        } // if
        else if (this.value > otherObj.value)
        {
            return 1;
        } // if

        return 0;
    } // else if
    else if (this.key < otherObj.key)
    {
        return -1;
    } // else if
    else
    {
        return 1;
    } // else
} // SimpleEntry_compareTo


/**
 * Object SimpleEntry_setValue (Object value)
 * Set a new value for the entry.
 */
function SimpleEntry_setValue (value)
{
    var oldValue = this.value;
    this.value = value;
    return oldValue;
} // SimpleEntry_setValue


/**
 * boolean SimpleEntry_equals (Object entry)
 * Check if the entry is equal to another one.
 */
function SimpleEntry_equals (entry)
{
    // check for identity:
    if (entry == this)
    {
        return true;
    } // if

    if (!entry.instanceOf (SimpleEntry))
    {
        return false;
    } // if

    return (equals (this.key, entry.getKey ()) &&
            equals (this.value, entry.getValue ()));
} // SimpleEntry_equals


/**
 * int SimpleEntry_hashCode ()
 * Compute the hash code of the entry.
 */
function SimpleEntry_hashCode ()
{
    return ((this.key   == null) ? 0 : this.key.hashCode ()) ^
           ((this.value == null) ? 0 : this.value.hashCode ());
} // SimpleEntry_hashCode


/**
 * String SimpleEntry_toString2 ()
 * Returns a string representation of this SimpleEntry.
 * The string representation consists only of the id.
 */
function SimpleEntry_toString2 ()
{
    // return the string:
    return this.key + "=" + this.value;
} // SimpleEntry_toString2


/**
 * SimpleEntry SimpleEntry ([Object key, Object value], [Entry entry])
 * Constructur of class SimpleEntry.
 * Either key or the entry must be != null.
 */
function SimpleEntry (key, value, entry)
{
    // set property values:
    this.key = key;                     // key
    this.value = value;                 // value

    // ensure constraints:
    if (entry && entry != null)
    {
        this.key = entry.getKey ();
        this.value = entry.getValue ();
    } // if
} // SimpleEntry

// create class form constructor:
createClass (SimpleEntry, null,
{
    // define properties and assign initial values:
    key: null,                          // key
    value: null,                        // value

    // define methods:
    print: SimpleEntry_print,           // output of SimpleEntry
    show: SimpleEntry_show,             // display the SimpleEntry
    compareTo: SimpleEntry_compareTo,   // compare this
                                        // SimpleEntry with another one
    getKey: function () {return this.key;}, // get the key
    getValue: function () {return this.value;}, // get the value
    setValue: SimpleEntry_setValue,     // set value of entry
    equals: SimpleEntry_equals,         // is the entry equal to another one?
    hashCode: SimpleEntry_hashCode,     // hash code of the entry
    toString2: SimpleEntry_toString2    // get string representation
}); // createClass


//============= class Map =====================================================
/******************************************************************************
 * An object that maps keys to values.  A map cannot contain duplicate keys;
 * each key can map to at most one value.
 *
 * <p>This interface takes the place of the <tt>Dictionary</tt> class, which
 * was a totally abstract class rather than an interface.
 *
 * <p>The <tt>Map</tt> interface provides three <i>collection views</i>, which
 * allow a map's contents to be viewed as a set of keys, collection of values,
 * or set of key-value mappings.  The <i>order</i> of a map is defined as
 * the order in which the iterators on the map's collection views return their
 * elements.  Some map implementations, like the <tt>TreeMap</tt> class, make
 * specific guarantees as to their order; others, like the <tt>HashMap</tt>
 * class, do not.
 *
 * <p>Note: great care must be exercised if mutable objects are used as map
 * keys.  The behavior of a map is not specified if the value of an object is
 * changed in a manner that affects equals comparisons while the object is a
 * key in the map.  A special case of this prohibition is that it is not
 * permissible for a map to contain itself as a key.  While it is permissible
 * for a map to contain itself as a value, extreme caution is advised: the
 * equals and hashCode methods are no longer well defined on a such a map.
 *
 * <p>All general-purpose map implementation classes should provide two
 * "standard" constructors: a void (no arguments) constructor which creates an
 * empty map, and a constructor with a single argument of type <tt>Map</tt>,
 * which creates a new map with the same key-value mappings as its argument.
 * In effect, the latter constructor allows the user to copy any map,
 * producing an equivalent map of the desired class.  There is no way to
 * enforce this recommendation (as interfaces cannot contain constructors) but
 * all of the general-purpose map implementations in the JDK comply.
 *
 * <p>The "destructive" methods contained in this interface, that is, the
 * methods that modify the map on which they operate, are specified to throw
 * <tt>UnsupportedOperationException</tt> if this map does not support the
 * operation.  If this is the case, these methods may, but are not required
 * to, throw an <tt>UnsupportedOperationException</tt> if the invocation would
 * have no effect on the map.  For example, invoking the {@link #putAll(Map)}
 * method on an unmodifiable map may, but is not required to, throw the
 * exception if the map whose mappings are to be "superimposed" is empty.
 *
 * <p>Some map implementations have restrictions on the keys and values they
 * may contain.  For example, some implementations prohibit null keys and
 * values, and some have restrictions on the types of their keys.  Attempting
 * to insert an ineligible key or value throws an unchecked exception,
 * typically <tt>NullPointerException</tt> or <tt>ClassCastException</tt>.
 * Attempting to query the presence of an ineligible key or value may throw an
 * exception, or it may simply return false; some implementations will exhibit
 * the former behavior and some will exhibit the latter.  More generally,
 * attempting an operation on an ineligible key or value whose completion
 * would not result in the insertion of an ineligible element into the map may
 * throw an exception or it may succeed, at the option of the implementation.
 * Such exceptions are marked as "optional" in the specification for this
 * interface.
 *
 * <p>This interface is a member of the 
 * <a href="{@docRoot}/../guide/collections/index.html">
 * Java Collections Framework</a>.
 */

// Query Operations

/**
 * int Map_size ()
 * Returns the number of key-value mappings in this map.  If the map
 * contains more than Integer.MAX_VALUE elements, returns
 * Integer.MAX_VALUE.
 */
function Map_size ()
{
    return (this.entrySet ().size ());
} // Map_size


/**
 * boolean Map_isEmpty ()
 * Returns true if this map contains no key-value mappings.
 */
function Map_isEmpty ()
{
    return (this.size () == 0);
} // Map_isEmpty


/**
 * Entry Map_getEntry (Object key)
 * Returns the entry associated with the specified key in the
 * map.  Returns null if the map contains no mapping for this key.
 */
function Map_getEntry (key)
{
    // search for the key and return the result:
    return this.entrySet ().iterator ().get (key);
} // Map_getEntry


/**
 * Entry Map_findEntry (Object value)
 * Returns the entry associated with the specified value in the
 * map.  Returns null if the map does not contain the value.
 */
function Map_findEntry (value)
{
    // search for the key and return the result:
    return this.entrySet ().iterator ().find (value);
} // Map_findEntry


/**
 * boolean Map_containsValue (Object value)
 * Returns true if this map maps one or more keys to this value.
 */
function Map_containsValue (value)
{
    // search for the value and return the result:
    return (this.findEntry (value) != null);
} // Map_containsValue


/**
 * boolean Map_containsKey (Object key)
 * Returns true if this map contains a mapping for the specified key.
 */
function Map_containsKey (key)
{
    // search for the key and return the result:
    return (this.getEntry (key) != null);
} // Map_containsKey


/**
 * Object Map_get (Object key)
 * Returns the value to which this map maps the specified key.  Returns
 * null if the map contains no mapping for this key.  A return
 * value of null does not necessarily indicate that the
 * map contains no mapping for the key; it's also possible that the map
 * explicitly maps the key to null.  The containsKey operation
 * may be used to distinguish these two cases.
 */
function Map_get (key)
{
    // search for the key:
    var entry = this.getEntry (key);

    if (entry == null)
    {
        return null;
    } // if
    else
    {
        return entry.value;
    } // else
} // Map_get


// Modification Operations

/**
 * Object Map_put (Object key, Object value)
 * Associates the specified value with the specified key in this map
 * (optional operation).  If the map previously contained a mapping for
 * this key, the old value is replaced.
 * Returns previous value associated with specified key, or null if there was
 * no mapping for key.  (A null return can also indicate that the map
 * previously associated null with the specified key, if the implementation
 * supports null values).
 */
function Map_put (key, value)
{
    throw new UnsupportedOperationException ();
} // Map_put


/**
 * Object Map_remove (Object key)
 * Removes the mapping for this key from this map if present.
 * This implementation iterates over entrySet() searching for an
 * entry with the specified key.  If such an entry is found, its value is
 * obtained with its getValue operation, the entry is removed
 * from the Collection (and the backing map) with the iterator's
 * remove operation, and the saved value is returned.  If the
 * iteration terminates without finding such an entry, null is
 * returned.  Note that this implementation requires linear time in the
 * size of the map; many implementations will override this method.
 */
function Map_remove (key)
{
    // search for the key:
    var iter = this.entrySet ().iterator ();
    var correctEntry = iter.get (key);
    var oldValue = null;

    // check if we found the entry:
    if (correctEntry != null)
    {
        // get old value:
        oldValue = correctEntry.getValue ();
        // remove the entry from the map:
        iter.remove ();
    } // if

    // return the old value:
    return oldValue;
} // Map_remove


// Bulk Operations

/**
 * void Map_putAll (Map t)
 * Copies all of the mappings from the specified map to this map.
 * These mappings will replace any mappings that this map had for any of the
 * keys currently in the specified map.
 */
function Map_putAll (t)
{
    var iter = t.entrySet ().iterator ();
    var elem = null;

    // for each element of the specified map add it to this map:
    while (iter.hasNext ())
    {
        elem = iter.next ();
        this.put (elem.getKey (), elem.getValue ());
    } // while iter.hasNext
} // Map_putAll


/**
 * void Map_clear ()
 * Removes all mappings from this Map.
 */
function Map_clear ()
{
    // clear underlying entry set:
    this.entrySet ().clear ();
} // Map_clear


// Views

/**
 * void Map_keySet ()
 * Returns a Set view of the keys contained in this map.  The Set is
 * backed by the map, so changes to the map are reflected in the Set,
 * and vice-versa.  (If the map is modified while an iteration over
 * the Set is in progress, the results of the iteration are undefined.)
 * The Set supports element removal, which removes the corresponding entry
 * from the map, via the Iterator.remove, Set.remove,  removeAll
 * retainAll, and clear operations.  It does not support the add or
 * addAll operations.
 */
function Map_keySet ()
{
    // check if the key set is already set:
    if (this.p_keySet == null)
    {
        this.p_keySet = new Set (this);
        this.p_keySet.iterator =
            function () {return new KeySetIterator (this);};
        this.p_keySet.size = function () {return this.size ();};
        this.p_keySet.contains = function (k) {return this.containsKey (k);};
    } // if

    // return the result:
    return keySet;
} // Map_keySet


/**
 * Collection Map_values ()
 * Returns a collection view of the values contained in this map.  The
 * collection is backed by the map, so changes to the map are reflected in
 * the collection, and vice-versa.  (If the map is modified while an
 * iteration over the collection is in progress, the results of the
 * iteration are undefined.)  The collection supports element removal,
 * which removes the corresponding entry from the map, via the
 * Iterator.remove, Collection.remove, removeAll, retainAll and clear
 * operations. It does not support the add or addAll operations.
 */
function Map_values ()
{
    // check if the values are already set:
    if (this.p_values == null)
    {
        this.p_values = new Collection ();
        this.p_values.iterator =
            function () {return new ValuesIterator (this);};
        this.p_values.size = function () {return this.size ();};
        this.p_values.contains = function (v) {return this.containsValue (v);};
    } // if

    // return the result:
    return values;
} // Map_values


// Comparison and hashing

/**
 * boolean Map_equals (Map obj)
 * Compares the specified object with this map for equality.  Returns
 * true if the given object is also a map and the two maps
 * represent the same mappings.  More formally, two maps t1 and
 * t2 represent the same mappings if
 * t1.keySet().equals(t2.keySet()) and for every key k
 * in t1.keySet(),  (t1.get(k)==null ? t2.get(k)==null :
 * t1.get(k).equals(t2.get(k))) .  This ensures that the
 * equals method works properly across different implementations
 * of the map interface.
 */
function Map_equals (obj)
{
    // check for identity:
    if (obj == this)
    {
        return true;
    } // if

    // check classes:
    if (!obj.instanceOf (Map))
    {
        return false;
    } // if

    // check size:
    if (obj.size () != this.size ())
    {
        return false;
    } // if

    // check if all elements of the other map are contained in this one:
    try
    {
        var iter = this.entrySet ().iterator ();
        var entry = null;
        var key = null;
        var value = null;

        while (iter.hasNext ())
        {
            entry = iter.next ();
            key = entry.getKey ();
            value = entry.getValue ();

            if (value == null)
            {
                // search for the key:
                if (!((entry = t.getEntry (key)) != null &&
                      entry.getValue () == null))
                {
                    return false;
                } // if
            } // if
            else
            {
                if (!value.equals (t.get (key)))
                {
                    return false;
                } // if
            } // else
        } // while iter.hasNext
    } // try
    catch (e)
    {
        return false;
    } // catch

    // all entries found, return result:
    return true;
} // Map_equals


/**
 * int Map_hashCode ()
 * Returns the hash code value for this map.  The hash code of a map is
 * defined to be the sum of the hash codes of each entry in the map's
 * entrySet() view.  This ensures that t1.equals(t2)
 * implies that t1.hashCode()==t2.hashCode() for any two maps
 * t1 and t2, as required by the general contract of
 * Object.hashCode.
 */
function Map_hashCode ()
{
    var h = 0;
    var iter = this.entrySet ().iterator ();

    // loop through all elements and add the hash code:
    while (iter.hasNext ())
    {
        h += iter.next ().hashCode ();
    } // while

    // return the result:
    return h;
} // Map_hashCode


//  String conversion

/**
 * String Map_toString2 ()
 * Returns a string representation of this map.  The string representation
 * consists of a list of key-value mappings in the order returned by the
 * map's entrySet view's iterator, enclosed in braces
 * ("{}").  Adjacent mappings are separated by the characters
 * ", " (comma and space).  Each key-value mapping is rendered as
 * the key followed by an equals sign ("=") followed by the
 * associated value.  Keys and values are converted to strings as by
 * String.valueOf(Object).
 */
function Map_toString2 ()
{
    // begin the string:
    var buf = "{";
    var iter = this.entrySet ().iterator ();
    var hasNext = iter.hasNext ();
    var entry = null;
    var key = null;
    var value = null;

    // loop through all elements and add each of them:
    while (hasNext)
    {
        entry = iter.next ();
        key = entry.getKey ();
        value = entry.getValue ();

        // append the key and value:
        buf +=
            (key == this ? "(this Map)" : key) +
            "=" +
            (value == this ? "(this Map)" : value);

        hasNext = iter.hasNext ();
        if (hasNext)
        {
            buf += ", ";
        } // if
    } // while hasNext

    // finish the string:
    buf += "}";
    // return the result:
    return buf;
} // Map_toString2


/**
 * Map Map_clone ()
 * Returns a shallow copy of this map instance: the keys and values
 * themselves are not cloned.
 */
function Map_clone ()
{
    // clone the object itself:
    var result = this._clone ();
    // set specific values:
    result.p_keySet = null;
    result.p_values = null;
    // return the result:
    return result;
} // Map_clone


/**
 * Map Map (id)
 * Constructor of class Map.
 */
function Map (id)
{
    // call super constructor(s):
    Collection.apply (this, arguments);

    // set property values:

    // ensure constraints:
} // Map

// create class form constructor:
createClass (Map, Collection,
{
    // define properties and assign initial values:
    p_keySet: null,
    p_values: null,

    // define methods:
    size: Map_size,               // number of key-value mappings
    isEmpty: Map_isEmpty,         // check if the map is empty
    getEntry: Map_getEntry,       // get entry through key
    findEntry: Map_findEntry,     // find entry through value
    containsValue: Map_containsValue, // check for specific value
    containsKey: Map_containsKey, // check for specific key
    get: Map_get,                 // get value through key
    put: Map_put,                 // put an entry into the map
    remove: Map_remove,           // remove mapping from the map
    putAll: Map_putAll,           // import all entries of another map
    clear: Map_clear,             // remove all mappings
    keySet: Map_keySet,           // get a set of all keys
    values: Map_values,           // get all values
    equals: Map_equals,           // is the Map equal to another one?
    hashCode: Map_hashCode,       // get the hash code of the Map
    toString2: Map_toString2,     // get the string representation
    clone: Map_clone,             // clone the map
    entrySet: notImplemented ("Map.entrySet") // get the entry set
}); // createClass


//============= class KeySetIterator ==========================================

/**
 * KeySetIterator KeySetIterator (Map map)
 * Constructor of class KeySetIterator.
 */
function KeySetIterator (map)
{
    // set property values:
    this.map = map;                     // the map
    this.iter = map.entrySet ().iterator ();

    // ensure constraints:
    if (map != null && map.notFound != null)
    {
        this.notFound = map.notFound;
    } // if
} // KeySetIterator

// create class form constructor:
createClass (KeySetIterator, null,
{
    // define properties and assign initial values:
    map: null,                          // the map
    iter: null,                         // iterator within map
    notFound: null,                     // default value when element was not
                                        // found

    // define methods:
    hasNext: function () {return iter.hasNext ();},
                                        // is there a next element?
    next: function () {return iter.next ().getKey ();},
                                        // get the next element
    remove: function () {iter.remove ();} // remove the last element which
                                        // was returned by next
}); // createClass


//============= class ValuesIterator ==========================================

/**
 * ValuesIterator ValuesIterator (Map map)
 * Constructor of class ValuesIterator.
 */
function ValuesIterator (map)
{
    // set property values:
    this.map = map;                     // the map
    this.iter = map.entrySet ().iterator ();

    // ensure constraints:
    if (map != null && map.notFound != null)
    {
        this.notFound = map.notFound;
    } // if
} // ValuesIterator

// create class form constructor:
createClass (ValuesIterator, null,
{
    map: null,                          // the map
    iter: null,                         // iterator within map
    notFound: null,                     // default value when element was not
                                        // found

    // define methods:
    hasNext: function () {return iter.hasNext ();},
                                        // is there a next element?
    next: function () {return iter.next ().getValue ();},
                                        // get the next element
    remove: function () {iter.remove ();} // remove the last element which
                                        // was returned by next
}); // createClass


//============= common functions ==============================================

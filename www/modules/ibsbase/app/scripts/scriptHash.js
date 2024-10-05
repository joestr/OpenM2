/******************************************************************************
 * This file contains all JavaScript classes and their methods which are used
 * to create and manage hash maps and hash sets. <BR>
 * 
 * @version     $Id: scriptHash.js,v 1.1 2006/04/11 15:52:07 klreimue Exp $
 *
 * @author      Klaus Reimüller (KR) 20060320
 ******************************************************************************
 */

//============= declared classes and functions ================================
// class HashMapEntry
// class HashMap
// class HashIterator
// class HashMapValueIterator
// class HashMapKeyIterator
// class HashMapEntryIterator
// class HashMapKeySet
// class HashMapValues
// class HashMapEntrySet
// class HashSet

// function hash_maskNull
// function hash_unmaskNull
// function hash_hash
// function hash_indexFor
// function hash_ensureHashable


//============= necessary classes and variables ===============================
// class Function
// class Object

// messages:


//============= declarations ==================================================

// constants:
var c_hash_DEFAULT_INITIAL_CAPACITY = 16; // The default initial capacity -
                                        // MUST be a power of two.
var c_hash_MAXIMUM_CAPACITY = 1 << 30;  // The maximum capacity, used if a
                                        // higher value is implicitly specified
                                        // by either of the constructors with
                                        // arguments.
                                        // MUST be a power of two <= 1<<30.
var c_hash_DEFAULT_LOAD_FACTOR = 0.75;  // The load factor used when none
                                        // specified in constructor.
var c_hash_NULL_KEY = new Object();     // Value representing null keys inside
                                        // tables.

// variables:


//============= initializations ===============================================


//============= class HashableObject ==========================================

/**
 * int HashableObject_hashCode ()
 * Compute the hash code of the object.
 */
function HashableObject_hashCode ()
{
    var str = this.key != null ? this.key :
              this.id != null ? this.id :
              this.value != null ? this.value :
              "" + this;
    var hash = 0;

    if (str == "")
    {
        // search for first property of the object which is not a function:
        for (prop in this)
        {
            // check if this is not a function:
            var i = ("" + this[prop]).indexOf ("function");
            if (this[prop] != null && (i < 0 || i > 1))
            {
                str = this[prop];
                break;
            } // if
        } // for prop
    } // if

    // compute the hash code:
    for (var i = 0; i < str.length; i++)
    {
        hash += str.charCodeAt (i);
    } // for i

    // return the result:
    return hash;
} // HashableObject_hashCode


/**
 * HashableObject HashableObject ()
 * Constructur of class HashableObject.
 */
function HashableObject ()
{
    // set property values:

    // ensure constraints:
} // HashableObject


// create class form constructor:
createClass (HashableObject, null,
{
    hashCode: HashableObject_hashCode   // hash code of the entry
}); // createClass



/* KR debugging
//alert (new HashableObject ().hashCode);
var s = new HashableObject ();
//alert (s.hashCode);
var obj1 = HashableObject;
var obj2 = String.prototype;
var i = ("" + obj1).indexOf ("function");
if (i == 0 || i == 1)
{
    obj1 = new obj1 ();
} // if
obj1.copyAttributes (obj2);
//String.prototype.hash1 = HashableObject_hashCode;
//String.prototype.hash1 = function () {alert ("test");};
//alert (ff + "\n" + ff.inspect + "\nhashCode = " + ff.hashCode + "\n" + ff.hash1);
var ff = "SS";
//alert (ff.hashCode ());
*/


//============= class HashMapEntry ============================================

/**
 * int HashMapEntry_hashCode ()
 * Compute the hash code of the entry.
 */
function HashMapEntry_hashCode ()
{
    return (this.key == c_hash_NULL_KEY ? 0 : this.key.hashCode ()) ^
           (this.value == null          ? 0 : this.value.hashCode ());
} // HashMapEntry_hashCode


/**
 * HashMapEntry HashMapEntry (int h, Object k, Object v, Entry n)
 * Constructur of class HashMapEntry.
 * Either key or the entry must be != null.
 */
function HashMapEntry (h, k, v, n)
{
    // call super constructor(s):
    SimpleEntry.call (this, k, v, null);
//    HashableObject.apply (this, arguments); // not necessary

    // set property values:
    this.p_hash = h;
    this.p_next = n;

    // ensure constraints:
} // HashMapEntry


// create class form constructor:
createClass (HashMapEntry, [SimpleEntry, HashableObject],
{
    // define properties and assign initial values:
    p_hash: 0,
    p_next: null,

    // define methods:
    getKey: function () {return hash_unmaskNull (key);},
                                        // get the key
    hashCode: HashMapEntry_hashCode,    // hash code of the entry
    recordAccess: function (m) {},      // This method is invoked whenever the
                                        // value in an entry is overwritten by
                                        // an invocation of put(k,v) for a key k
                                        // that's already in the HashMap.
    recordRemoval: function (m) {}      // This method is invoked whenever the
                                        // entry is removed from the table.
}); // createClass


//============= class HashMap =================================================

// internal utilities

/**
 * void HashMap_init ()
 * Initialization hook for subclasses. This method is called
 * in all constructors and pseudo-constructors (clone, readObject)
 * after HashMap has been initialized but before any entries have
 * been inserted.  (In the absence of this method, readObject would
 * require explicit knowledge of subclasses.)
 */
function HashMap_init ()
{
    // nothing to do here
} // HashMap_init


// Query Operations

/**
 * int HashMap_size ()
 * Returns the number of key-value mappings in this map.
 */
function HashMap_size ()
{
    return (this.p_size);
} // HashMap_size


/**
 * boolean HashMap_isEmpty ()
 * Returns true if this map contains no key-value mappings.
 */
function HashMap_isEmpty ()
{
    return (this.p_size == 0);
} // HashMap_isEmpty


/**
 * Entry HashMap_getEntry (Object key)
 * Returns the entry associated with the specified key in the
 * HashMap.  Returns null if the HashMap contains no mapping for this key.
 */
function HashMap_getEntry (key)
{
    var k = hash_maskNull (key);
    var hash = hash_hash (k);
    var i = hash_indexFor (hash, this.table.length);
    var entry = this.table[i];

    // check if the entry is found:
    while (entry != null &&
           !(entry.p_hash == hash && equalsNotNull (k, entry.key)))
    {
        entry = entry.p_next;
    } // while true

    // return the entry:
    return entry;
} // HashMap_getEntry


/**
 * Entry HashMap_findEntry (Object value)
 * Returns the entry associated with the specified value in the
 * map.  Returns null if the map does not contain the value.
 */
function HashMap_findEntry (value)
{
    var tab = this.table;

    if (value == null) 
    {
        // loop through all elements and check if we find the null value:
        for (var i = 0; i < tab.length ; i++)
        {
            for (var e = tab[i]; e != null; e = e.p_next)
            {
                if (e.value == null)
                {
                    return e;
                } // if
            } // for e
        } // for i
    } // if
    else
    {
        // loop through all elements and check if we find the value:
        for (var i = 0; i < tab.length ; i++)
        {
            for (var e = tab[i]; e != null; e = e.p_next)
            {
                if (value.equals (e.value))
                {
                    return e;
                } // if
            } // for e
        } // for i
    } // else

    // value not found:
    return null;
} // HashMap_findEntry


/**
 * boolean HashMap_containsNullValue ()
 * Returns true if this map maps one or more keys to the specified value.
 */
function HashMap_containsNullValue ()
{
    return (this.findEntry (null) != null);
} // HashMap_containsNullValue


// Modification Operations

/**
 * Object HashMap_put (Object key, Object value)
 * Associates the specified value with the specified key in this map.
 * If the map previously contained a mapping for this key, the old
 * value is replaced.
 */
function HashMap_put (key, value)
{
    var k = hash_maskNull (key);
    var hash = hash_hash (k);
    var i = hash_indexFor (hash, this.table.length);

    // loop through all elements and check if one of them has the same key:
    for (var e = this.table[i]; e != null; e = e.next ())
    {
        if (e.p_hash == hash && equalsNotNull (k, e.key))
        {
            var oldValue = e.value;
            e.value = value;
            e.recordAccess (this);
            return oldValue;
        } // if
    } // for e

    // add the entry:
    modCount++;
    this.addEntry (hash, k, value, i);
    return null;
} // HashMap_put


/**
 * void HashMap_putForCreate (Object key, Object value)
 * This method is used instead of put by constructors and
 * pseudoconstructors (clone, readObject).  It does not resize the table,
 * check for comodification, etc.  It calls createEntry rather than
 * addEntry.
 */
function HashMap_putForCreate (key, value)
{
    var k = hash_maskNull (key);
    var hash = hash_hash (k);
    var i = hash_indexFor (hash, this.table.length);

    // Look for preexisting entry for key.  This will never happen for
    // clone or deserialize.  It will only happen for construction if the
    // input Map is a sorted map whose ordering is inconsistent w/ equals.
    // loop through all elements and check if one of them has the same key:
    for (var e = this.table[i]; e != null; e = e.next ())
    {
        if (e.p_hash == hash && equalsNotNull (k, e.key))
        {
            e.value = value;
            return;
        } // if
    } // for e

    // add the entry:
    this.createEntry (hash, k, value, i);
} // HashMap_putForCreate


/**
 * void HashMap_putAllForCreate (Map m)
 * This method is used instead of put by constructors and
 * pseudoconstructors (clone, readObject).  It does not resize the table,
 * check for comodification, etc.  It calls createEntry rather than
 * addEntry.
 */
function HashMap_putAllForCreate (key, value)
{
    var e = null;

    for (var i = m.entrySet ().iterator (); i.hasNext ();)
    {
        e = i.next ();
        this.putForCreate (e.getKey (), e.getValue ());
    } // for i
} // HashMap_putAllForCreate


/**
 * void HashMap_resize (int newCapacity)
 * Rehashes the contents of this map into a new array with a
 * larger capacity.  This method is called automatically when the
 * number of keys in this map reaches its threshold.
 *
 * If current capacity is MAXIMUM_CAPACITY, this method does not
 * resize the map, but sets threshold to Number.MAX_VALUE.
 * This has the effect of preventing future calls.
 */
function HashMap_resize (value)
{
    var oldTable = this.table;
    var oldCapacity = oldTable.length;
    if (oldCapacity == c_hash_MAXIMUM_CAPACITY)
    {
        this.threshold = Number.MAX_VALUE;
        return;
    } // if

    var newTable = new Array (newCapacity);
    this.transfer (newTable);
    this.table = newTable;
    this.threshold = (newCapacity * loadFactor);
} // HashMap_resize


/**
 * void HashMap_transfer (Entry[] newTable)
 * Transfer all entries from current table to newTable.
 */
function HashMap_transfer (newTable)
{
    var src = this.table;
    var newCapacity = newTable.length;
    var e = null;
    var next = null;
    var i = 0;

    for (var j = 0; j < src.length; j++)
    {
        e = src[j];
        if (e != null)
        {
            src[j] = null;
            do
            {
                next = e.p_next;
                i = hash_indexFor (e.p_hash, newCapacity);  
                e.p_next = newTable[i];
                newTable[i] = e;
                e = next;
            } while (e != null);
        } // if
    } // for j
} // HashMap_transfer


// Bulk Operations

/**
 * void HashMap_putAll (Map m)
 * Copies all of the mappings from the specified map to this map
 * These mappings will replace any mappings that
 * this map had for any of the keys currently in the specified map.
 */
function HashMap_putAll (m)
{
    var numKeysToBeAdded = m.size ();
    if (numKeysToBeAdded == 0)
    {
        return;
    } // if

    // Expand the map if the number of mappings to be added
    // is greater than or equal to threshold.  This is conservative; the
    // obvious condition is (m.size() + this.p_size) >= threshold, but this
    // condition could result in a map with twice the appropriate capacity,
    // if the keys to be added overlap with the keys already in this map.
    // By using the conservative calculation, we subject ourself
    // to at most one extra resize.
    if (numKeysToBeAdded > this.threshold)
    {
        var targetCapacity = (numKeysToBeAdded / this.loadFactor + 1);
        if (targetCapacity > c_hash_MAXIMUM_CAPACITY)
        {
            targetCapacity = c_hash_MAXIMUM_CAPACITY;
        } // if
        var newCapacity = this.table.length;
        while (newCapacity < targetCapacity)
        {
            newCapacity <<= 1;
        } // if
        if (newCapacity > this.table.length)
        {
            this.resize (newCapacity);
        } // if
    } // if

    var e = null;
    for (var i = m.entrySet ().iterator (); i.hasNext (); )
    {
        e = i.next ();
        this.put (e.getKey (), e.getValue ());
    } // for i
} // HashMap_putAll


/**
 * Object HashMap_remove (Object key)
 * Removes the mapping for this key from this map if present.
 */
function HashMap_remove (key)
{
    var e = this.removeEntryForKey (key);
    return (e == null ? null : e.value);
} // HashMap_remove


/**
 * Entry HashMap_removeEntryForKey (Object key)
 * Removes and returns the entry associated with the specified key in the
 * HashMap.  Returns null if the HashMap contains no mapping for this key.
 */
function HashMap_removeEntryForKey (key)
{
    var k = hash_maskNull (key);
    var hash = hash_hash (k);
    var i = hash_indexFor (hash, this.table.length);
    var prev = this.table[i];
    var e = prev;
    var next = null;

    while (e != null)
    {
        next = e.p_next;
        if (e.p_hash == hash && equalsNotNull (k, e.key))
        {
            this.modCount++;
            this.p_size--;
            if (prev == e) 
            {
                this.table[i] = next;
            } // if
            else
            {
                prev.p_next = next;
            } // else
            e.recordRemoval (this);
            return e;
        } // if
        prev = e;
        e = next;
    } // while

    return e;
} // HashMap_removeEntryForKey


/**
 * Entry HashMap_removeMapping (SimpleEntry entry)
 * Removes and returns the entry associated with the specified key in the
 * HashMap.  Returns null if the HashMap contains no mapping for this key.
 */
function HashMap_removeMapping (entry)
{
    if (!entry.instanceOf (SimpleEntry))
    {
        return null;
    } // if

    var k = hash_maskNull (entry.getKey ());
    var hash = hash_hash (k);
    var i = hash_indexFor (hash, this.table.length);
    var prev = this.table[i];
    var e = prev;
    var next = null;

    while (e != null)
    {
        next = e.p_next;
        if (e.p_hash == hash && e.equals (entry))
        {
            this.modCount++;
            this.p_size--;
            if (prev == e) 
            {
                this.table[i] = next;
            } // if
            else
            {
                prev.p_next = next;
            } // else
            e.recordRemoval (this);
            return e;
        } // if
        prev = e;
        e = next;
    } // while

    return e;
} // HashMap_removeMapping


/**
 * void HashMap_clear ()
 * Removes all mappings from this map.
 */
function HashMap_clear ()
{
    this.modCount++;
    var tab = this.table;
    for (var i = 0; i < tab.length; i++) 
    {
        delete tab[i];
        tab[i] = null;
    } // for i

    this.p_size = 0;
} // HashMap_clear


/**
 * HashMap HashMap_clone ()
 * Returns a shallow copy of this HashMap instance: the keys and values
 * themselves are not cloned.
 */
function HashMap_clone ()
{
    // clone the object itself:
    var result = this._clone ();
    // set specific values:
    result.table = new Array (table.length);
    result.p_entrySet = null;
    result.modCount = 0;
    result.p_size = 0;
    result.init ();
    result.putAllForCreate (this);
    // return the result:
    return result;
} // HashMap_clone


/**
 * void HashMap_addEntry (int hash, Object key, Object value, int bucketIndex)
 * Add a new entry with the specified key, value and hash code to
 * the specified bucket.  It is the responsibility of this 
 * method to resize the table if appropriate.
 */
function HashMap_addEntry (hash, key, value, bucketIndex)
{
	var e = this.table[bucketIndex];
    this.table[bucketIndex] = new HashMapEntry (hash, key, value, e);

    if (this.p_size++ >= this.threshold)
    {
        this.resize (2 * this.table.length);
    } // if
} // HashMap_addEntry


/**
 * void HashMap_createEntry (int hash, Object key, Object value,int bucketIndex)
 * Like addEntry except that this version is used when creating entries
 * as part of Map construction or "pseudo-construction" (cloning,
 * deserialization).  This version needn't worry about resizing the table.
 */
function HashMap_createEntry (hash, key, value, bucketIndex)
{
    var e = this.table[bucketIndex];
    this.table[bucketIndex] = new HashMapEntry (hash, key, value, e);
    this.p_size++;
} // HashMap_createEntry


// Views

/**
 * void HashMap_keySet ()
 * Returns a set view of the keys contained in this map.  The set is
 * backed by the map, so changes to the map are reflected in the set, and
 * vice-versa.  The set supports element removal, which removes the
 * corresponding mapping from this map, via the Iterator.remove,
 * Set.remove, removeAll, retainAll, and
 * clear operations.  It does not support the add or addAll operations.
 */
function HashMap_keySet ()
{
    var ks = this.p_keySet;
    return (ks != null ? ks :
            (this.p_keySet = new HashMapKeySet (this.id + "_keySet", this)));
} // HashMap_keySet


/**
 * Collection HashMap_values ()
 * Returns a collection view of the values contained in this map.  The
 * collection is backed by the map, so changes to the map are reflected in
 * the collection, and vice-versa.  The collection supports element
 * removal, which removes the corresponding mapping from this map, via the
 * Iterator.remove, Collection.remove, removeAll, retainAll, and clear
 * operations. It does not support the add or addAll operations.
 */
function HashMap_values ()
{
    var vs = this.p_values;
    return (vs != null ? vs :
            (this.p_values = new HashMapValues (this.id + "_values", this)));
} // HashMap_values


/**
 * Collection HashMap_entrySet ()
 * Returns a collection view of the mappings contained in this map.  Each
 * element in the returned collection is a Map.Entry.  The
 * collection is backed by the map, so changes to the map are reflected in
 * the collection, and vice-versa.  The collection supports element
 * removal, which removes the corresponding mapping from the map, via the
 * Iterator.remove, Collection.remove, removeAll, retainAll, and clear
 * operations. It does not support the add or addAll operations.
 */
function HashMap_entrySet ()
{
    var es = this.p_entrySet;
    return (es != null ? es :
            (this.p_entrySet =
                new HashMapEntrySet (this.id + "_entrySet", this)));
} // HashMap_entrySet


/**
 * HashMap HashMap (id, [Map m | int m], [float loadFactor])
 * Constructor of class HashMap.
 * m is either a map from which to get the initial elements or a
 * number defining the initialCapacity of the HashSet.
 */
function HashMap (id, m, loadFactor)
{
    // call super constructor(s):
    Map.apply (this, arguments);

    // set property values:

    // ensure constraints:
    if (!m || m == null)
    {
        this.loadFactor = c_hash_DEFAULT_LOAD_FACTOR;
        this.threshold = c_hash_DEFAULT_INITIAL_CAPACITY *
                         c_hash_DEFAULT_LOAD_FACTOR;
        this.table = new Array (c_hash_DEFAULT_INITIAL_CAPACITY);
        this.init ();
    } // if
    else
    {
        var initialCapacity = m;
        if (isNaN (initialCapacity))
        {
            initialCapacity =
                Math.max ((c.size () / c_hash_DEFAULT_LOAD_FACTOR) + 1,
                          c_hash_DEFAULT_INITIAL_CAPACITY);
            loadFactor = c_hash_DEFAULT_LOAD_FACTOR;
        } // if
        else if (isNaN (loadFactor))
        {
            loadFactor = c_hash_DEFAULT_LOAD_FACTOR;
        } // else

        if (initialCapacity < 0)
        {
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        } // if
        if (initialCapacity > c_hash_MAXIMUM_CAPACITY)
        {
            initialCapacity = c_hash_MAXIMUM_CAPACITY;
        } // if
        if (loadFactor <= 0)
        {
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        } // if

        // find a power of 2 >= initialCapacity:
        var capacity = 1;
        while (capacity < initialCapacity) 
        {
            capacity <<= 1;
        } // while

        this.loadFactor = loadFactor;
        this.threshold = (capacity * loadFactor);
        this.table = new Array (capacity);
        this.init ();

        if (isNaN (m))
        {
            this.putAllForCreate (m);
        } // if
    } // else
} // HashMap

// create class form constructor:
createClass (HashMap, Map,
{
    // define properties and assign initial values:
    table: null,                        // The table, resized as necessary.
                                        // Length MUST Always be a power of two.
    p_size: 0,                          // The number of key-value mappings
                                        // contained in this identity hash map.
    threshold: 0,                       // The next size value at which to
                                        // resize (capacity * load factor).
    loadFactor: 0,                      // The load factor for the hash table.
    modCount: 0,                        // The number of times this HashMap has
                                        // been structurally modified
    p_entrySet: null,                   // entry set

    // define methods:
    init: HashMap_init,                 // initialization
    size: HashMap_size,                 // number of key-value mappings
    isEmpty: HashMap_isEmpty,           // check if the map is empty
    getEntry: HashMap_getEntry,         // get entry through key
    findEntry: HashMap_findEntry,       // find entry through value
    containsNullValue: HashMap_containsNullValue, // check if there is a
                                        // null value within the map
    put: HashMap_put,                   // put an entry into the map
    putForCreate: HashMap_putForCreate, // put an entry into the map
                                        // (for use during creation)
    putAllForCreate: HashMap_putAllForCreate, // put several entries into
                                        // the map (for use during creation)
    resize: HashMap_resize,             // resize the map
    transfer: HashMap_transfer,         // transfer elements into another map
    putAll: HashMap_putAll,             // import all entries of another map
    remove: HashMap_remove,             // remove mapping from the map
    removeEntryForKey: HashMap_removeEntryForKey, // remove mapping from
                                        // the map
    removeMapping: HashMap_removeMapping, // remove entry from the map
    clear: HashMap_clear,               // remove all mappings
    clone: HashMap_clone,               // clone the map
    addEntry: HashMap_addEntry,         // add entry to the map
    createEntry: HashMap_createEntry,   // add entry to the map
                                        // (for use during creation)
    keySet: HashMap_keySet,             // get a set of all keys
    values: HashMap_values,             // get all values
    entrySet: HashMap_entrySet,         // get the entry set
    iterator: notImplemented ("HashMap.iterator"), // get iterator
    newKeyIterator: function () {return new HashMapKeyIterator (this);},
    newValueIterator: function () {return new HashMapValueIterator (this);},
    newEntryIterator: function () {return new HashMapEntryIterator (this);}
}); // createClass


//============= class HashIterator ============================================

/**
 * boolean HashIterator_hasNext ()
 * Check if there is a next element.
 */
function HashIterator_hasNext ()
{
    return (this.p_nextElem != null);
} // HashIterator_hasNext


/**
 * Object HashIterator_nextEntry ()
 * Get the next element out of the iterator.
 */
function HashIterator_nextEntry ()
{
    if (this.collection.modCount != this.expectedModCount)
    {
        throw new ConcurrentModificationException ();
    } // if

    var e = this.p_nextElem;
    if (e == null) 
    {
        throw new NoSuchElementException ();
    } // if
        
    var n = e.p_next;
    var t = this.collection.table;
    var i = this.index;

    while (n == null && i > 0)
    {
        n = t[--i];
    } // while

    this.index = i;
    this.p_nextElem = n;
    return this.current = e;
} // HashIterator_nextEntry


/**
 * void HashIterator_remove ()
 * Remove the last element which we got by calling next or prev.
 */
function HashIterator_remove ()
{
    if (this.current == null)
    {
        throw new IllegalStateException ();
    } // if
    if (this.collection.modCount != this.expectedModCount)
    {
        throw new ConcurrentModificationException ();
    } // if
    var k = this.current.key;
    this.current = null;
    this.collection.removeEntryForKey (k);
    this.expectedModCount = this.collection.modCount;
} // HashIterator_remove


/**
 * HashIterator HashIterator (HashMap map)
 * Constructor of class HashIterator.
 */
function HashIterator (map)
{
    // call super constructor(s):
    Iterator.apply (this, arguments);

    // set property values:
    this.expectedModCount = map.modCount; // For fast-fail 

    // ensure constraints:
    var t = this.collection.table;
    var i = t.length;
    var n = null;
    // advance to first entry:
    if (this.collection.table.p_size != 0)
    {
        while (i > 0 && (n = t[--i]) == null);
    } // if
    this.p_nextElem = n;
    this.index = i;
} // HashIterator

// create class form constructor:
createClass (HashIterator, Iterator,
{
    // define properties and assign initial values:
    expectedModCount: 0,                // For fast-fail 
    index: 0,                           // current slot 
    current: null,                      // current entry

    // define methods:
    hasNext: function () {return (this.p_nextElem != null);},
                                        // is there a next element?
    nextEntry: HashIterator_nextEntry,  // get the next entry
    remove: HashIterator_remove         // remove the last element which
                                        // was returned by nextEntry
}); // createClass


//============= class HashMapValueIterator ====================================

/**
 * HashMapValueIterator HashMapValueIterator (HashMap map)
 * Constructor of class HashMapValueIterator.
 */
function HashMapValueIterator (map)
{
    // call super constructor(s):
    HashIterator.apply (this, arguments);
} // HashMapValueIterator

// create class form constructor:
createClass (HashMapValueIterator, HashIterator,
{
    // define methods:
    next: function () {return (this.nextEntry ().value);}
                                        // get the next element?
}); // createClass


//============= class HashMapKeyIterator ======================================

/**
 * HashMapKeyIterator HashMapKeyIterator (HashMap map)
 * Constructor of class HashMapKeyIterator.
 */
function HashMapKeyIterator (map)
{
    // call super constructor(s):
    HashIterator.apply (this, arguments);
} // HashMapKeyIterator

// create class form constructor:
createClass (HashMapKeyIterator, HashIterator,
{
    // define methods:
    next: function () {return (this.nextEntry ().getKey ());}
                                        // get the next element?
}); // createClass


//============= class HashMapEntryIterator ====================================

/**
 * HashMapEntryIterator HashMapEntryIterator (HashMap map)
 * Constructor of class HashMapEntryIterator.
 */
function HashMapEntryIterator (map)
{
    // call super constructor(s):
    HashIterator.apply (this, arguments);
} // HashMapEntryIterator

// create class form constructor:
createClass (HashMapEntryIterator, HashIterator,
{
    // define methods:
    next: function () {return (this.nextEntry ());}
                                        // get the next element?
}); // createClass


//============= class HashMapKeySet ===========================================

/**
 * HashMapKeySet HashMapKeySet (String|int id, HashMap map)
 * Constructor of class HashMapKeySet.
 */
function HashMapKeySet (id, map)
{
    // call super constructor(s):
    Set.apply (this, arguments);

    // set property values:
    this.map = map;                     // the hash map

    // ensure constraints:
} // HashMapKeySet

// create class form constructor:
createClass (HashMapKeySet, CollectionElement,
{
    // define properties and assign initial values:
    map: null,                          // the hash map

    // define methods:
    iterator: function () {return map.newKeyIterator ();},
                                        // get iterator
    size: function () {return map.size ();}, // get number of entries
    contains: function (o) {return map.containsKey (o);},
                                        // check for key
    remove: function (o) {return map.removeEntryForKey (o);},
                                        // remove entry
    clear: function () {map.clear ();}  // remove all entries
}); // createClass


//============= class HashMapValues ===========================================

/**
 * HashMapValues HashMapValues (String|int id, HashMap map)
 * Constructor of class HashMapValues.
 */
function HashMapValues (id, map)
{
    // call super constructor(s):
    Collection.apply (this, arguments);

    // set property values:
    this.map = map;                     // the hash map

    // ensure constraints:
} // HashMapValues

// create class form constructor:
createClass (HashMapValues, CollectionElement,
{
    // define properties and assign initial values:
    map: null,                          // the hash map

    // define methods:
    iterator: function () {return map.newValueIterator ();},
                                        // get iterator
    size: function () {return map.size ();}, // get number of entries
    contains: function (o) {return map.containsValue (o);},
                                        // check for value
    clear: function () {map.clear ();}  // remove all entries
}); //reateClass


//============= class HashMapEntrySet =========================================

/**
 * boolean HashMapEntrySet_contains (SimpleEntry e)
 * Check if a specific entry is contained in the set.
 */
function HashMapEntrySet_contains (e)
{
    // check class:
    if (!e.instanceOf (SimpleEntry))
    {
        return false;
    } // if

    // search for the key:
    var candidate = this.map.getEntry (e.getKey ());
    return (candidate != null && candidate.equals (e));
} // HashMapEntrySet_contains


/**
 * HashMapEntrySet HashMapEntrySet (String|int id, HashMap map)
 * Constructor of class HashMapEntrySet.
 */
function HashMapEntrySet (id, map)
{
    // call super constructor(s):
    Set.apply (this, arguments);

    // set property values:
    this.map = map;                     // the hash map

    // ensure constraints:
} // HashMapEntrySet

// create class form constructor:
createClass (HashMapEntrySet, CollectionElement,
{
    // define properties and assign initial values:
    map: null,                          // the hash map

    // define methods:
    iterator: function () {return map.newEntryIterator ();},
                                        // get iterator
    size: function () {return map.size ();}, // get number of entries
    contains: HashMapEntrySet_contains, // check for key
    remove: function (o) {return map.removeMapping (o);},
                                        // remove entry
    clear: function () {map.clear ();}  // remove all entries
}); // createClass


//============= class HashSet =================================================

/**
 * HashSet HashSet_clone ()
 * Returns a shallow copy of this HashSet instance: the elements
 * themselves are not cloned.
 */
function HashSet_clone ()
{
    // clone the object itself:
    var newSet = this._clone ();
    // clone the map:
    newSet.map = this.map.clone ();
    // return the result:
    return newSet;
} // HashSet_clone


/**
 * HashSet HashSet (id, [Collection c | int c], [float loadFactor])
 * Constructor of class HashSet.
 * c is either a collection from which to get the initial elements or a
 * number defining the initialCapacity of the HashSet.
 */
function HashSet (id, c, loadFactor)
{
    // call super constructor(s):
    Set.apply (this, arguments);

    // set property values:
    this.PRESENT = new Object ();       // dummy value to associate with an
                                        // Object in the backing Map

    // ensure constraints:
    if (!c || c == null)
    {
        this.map = new HashMap ();
    } // if
    else if (isNaN (c))
    {
        this.map = new HashMap (Math.max ((c.size () / 0.75) + 1, 16));
        this.addAll (c);
    } // else if
    else
    {
        this.map = new HashMap (c, loadFactor);
    } // else
} // HashSet

// create class form constructor:
createClass (HashSet, CollectionElement,
{
    // define properties and assign initial values:
    map: null,                          // the hash map
    PRESENT: new Object (),             // dummy value to associate with an
                                        // Object in the backing Map

    // define methods:
    iterator: function () {return map.keySet ().iterator ();},
                                        // get iterator
    size: function () {return map.size ();}, // get size of HashSet
    isEmpty: function () {return map.isEmpty ();}, // is the HashSet empty?
    contains: function (o) {return map.containsKey (o);},
                                        // is a specific object part of the set?
    add: function (o) {return (map.put (o, PRESENT) == null);},
                                        // add an element to the set
    remove: function (o) {return (map.remove (o) == PRESENT);},
                                        // remove element from this set
    clear: function () {map.clear ();}, // remove all elements from this set
    clone: HashSet_clone                // clone the HashSet
}); // createClass


//============= constraint functions ==========================================


//============= common functions ==============================================

/**
 * Object hash_maskNull (Object key)
 * Returns internal representation for key. Use NULL_KEY if key is null.
 */
function hash_maskNull (key)
{
    return key == null ? c_hash_NULL_KEY : key;
} // hash_maskNull


/**
 * Object hash_unmaskNull (Object key)
 * Returns key represented by specified internal representation.
 */
function hash_unmaskNull (key)
{
    return (key == c_hash_NULL_KEY ? null : key);
} // hash_unmaskNull


/**
 * int hash_hash (Object x)
 * Returns a hash value for the specified object.  In addition to 
 * the object's own hashCode, this method applies a "supplemental
 * hash function," which defends against poor quality hash functions.
 * This is critical because HashMap uses power-of two length 
 * hash tables.
 *
 * The shift distances in this function were chosen as the result
 * of an automated search over the entire four-dimensional search space.
 */
function hash_hash (x)
{
    var h = x.hashCode ();

    h += ~(h << 9);
    h ^=  (h >>> 14);
    h +=  (h << 4);
    h ^=  (h >>> 10);
    return h;
} // hash_hash


/**
 * int hash_indexFor (int h, int length)
 * Returns index for hash code h.
 */
function hash_indexFor (h, length)
{
    return h & (length - 1);
} // hash_indexFor


/**
 * HashableObject hash_ensureHashable (Object obj)
 * Ensure that the object supports hashing.
 */
function hash_ensureHashable (obj)
{
    if (!obj.hashCode || obj.hashCode == null)
    {
        // extend object with hashable functionality:
        HashableObject.apply (obj);
    } // if

    // return the object:
    return obj;
} // hash_ensureHashable

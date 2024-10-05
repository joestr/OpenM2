/*
 * Class: AccessCache.java
 */

// package:
package ibs.bo;

// imports:
import ibs.BaseObject;


/******************************************************************************
 * Caches the accesses of users to one object. <BR/>
 * The intend if this class is to prevent the server from going to the data
 * store for each rights check, but to do this check within this structure.
 *
 * @version     $Id: AccessCache.java,v 1.7 2007/07/10 22:30:12 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 990119
 ******************************************************************************
 */
public class AccessCache extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: AccessCache.java,v 1.7 2007/07/10 22:30:12 kreimueller Exp $";


    /**
     * The maximum number of elements within this cache. <BR/>
     */
    public int maxSize = 0;

    /**
     * Actual number of elements within the pool. <BR/>
     */
    private int actSize = 0;

    /**
     * The first element within the cache, i.e. the element which was most
     * recently used. <BR/>
     * This is the object which shall be the last to be removed from the cache.
     */
    private ListElement firstElem = null;

    /**
     * The last element within the cache, i.e. the element which was least
     * recently used. <BR/>
     * This is the object which shall be the first to be removed from the cache.
     */
    private ListElement lastElem = null;

/*
    /**
     * Time which an element is valid within this cache in milliseconds. <BR/>
     * Default: 1 hour.
     */
/*
    public long timeOut = 3600000;
*/

    /**
     * Number of accesses to the cache. <BR/>
     */
    private int accesses = 0;

    /**
     * Number of successful accesses to the cache. <BR/>
     */
    private int hits = 0;

    /**
     *
     */
    public String trace = "";


    /**************************************************************************
     * Constructor of this class. <BR/>
     *
     * @param   maxSize     The maximum number of elements within this cache.
     */
    public AccessCache (int maxSize)
    {
        this.maxSize = maxSize;
    } // AccessCache


    /**************************************************************************
     * Get one object out of the cache determined through its user and
     * operation. <BR/>
     *
     * @param   userId      Id of user for whom the rights to be gotten.
     * @param   operation   Operation to be performed.
     *
     * @return  Value indicating if the user has the required rights. <BR/>
     *          1 ... The user has the rights.<BR/>
     *          0 ... The user does not have the rights.<BR/>
     *          -1 .. unknown.
     */
    public int get (int userId, int operation)
    {
        int retVal = -1;                // the return value
        ListElement elem = null;        // the actual list element

        this.accesses++;                // one more access

        // search for the entry within the cache:
        for (elem = this.firstElem;
             elem != null && (elem.userId != userId || (elem.operation & operation) != operation);
             elem = elem.next)
        {
            // nothing to do
        } // for elem

        if (elem != null)               // found the required element?
        {
            this.hits++;                // one more hit
/*
            if (new Date ().before (elem.timeOut)) // no time out?
            {
*/
            if (elem.hasRights) // user has the rights?
            {
                retVal = 1;
            } // if user has the rights
            else
            // user does not have the rights
            {
                retVal = 0;
            } // else user does not have the rights
            /*
             * } // if no time out
             */
        } // if found the required element

        return retVal;                  // return the computed value
    } // get


    /**************************************************************************
     * Get one object out of the cache determined through its user and
     * operation. <BR/>
     *
     * @param   userId      Id of user for whom the rights to be gotten.
     * @param   operation   Operation to be performed.
     * @param   hasRights   Does the user have the rights?
     */
    public void put (int userId, int operation, boolean hasRights)
    {
        ListElement elem = null;        // the actual list element

        // search for the entry within the cache:
        for (elem = this.firstElem;
             elem != null && (elem.userId != userId || elem.operation != operation);
             elem = elem.next)
        {
            // nothing to do
        } // for elem

        if (elem != null)               // found the required element?
        {
            // update the information:
            elem.hasRights = hasRights;
/*
            elem.timeOut = new Date (new Date ().getTime () + this.timeOut);
*/
        } // if found the required element
        else                            // required element not found
        {
            elem = new ListElement (userId, operation, hasRights);
/*
            elem = new ListElement (userId, operation, hasRights,
                                    new Date (new Date ().getTime () + this.timeOut));
*/

            if (this.actSize >= this.maxSize) // the cache is full?
            {
                // eliminate the last element from the cache:
                if (this.lastElem != null) // at least one element in cache?
                {
                    this.lastElem = this.lastElem.prev; // new last element
                } // if at least one element in cache
                if (this.lastElem != null) // at least one more elem. in cache?
                {
                    this.lastElem.next = null; // no next element
                } // if at least one more elem. in cache
            } // if the cache is full

            // add element at first position of list:
            elem.next = this.firstElem; // the next element after the actual one
            if (this.firstElem != null) // there was at least one element?
            {
                this.firstElem.prev = elem; // actual element is previous
            } // if
            this.firstElem = elem;      // new first element
        } // else required element not found
    } // put


    /**************************************************************************
     * Get the hit rate. <BR/>
     * The hit rate is the result of dividing the number of hits by the number
     * of accesses.
     *
     * @return  The hit rate.
     */
    public double getHitRate ()
    {
        if (this.accesses > 0)          // at least one access?
        {
            // compute the hit rate and return it:
            return ((double) this.hits) / ((double) this.accesses);
        } // if at least one access

        // no accesses yet
        return 0;                       // return default value
    } // getHitRate



    /**************************************************************************
     * Implements elements of lists containing business objects. <BR/>
     *
     * @version     $Id: AccessCache.java,v 1.7 2007/07/10 22:30:12 kreimueller Exp $
     *
     * @author      Klaus Reimüller (KR), 990115
     **************************************************************************
     */
    private class ListElement
    {
        /**
         * Id of the user.
         */
        int userId;
        /**
         * The operation (s) the user tried to perform.
         */
        int operation;
        /**
         * Did the user have the rights?
         */
        boolean hasRights;
/*
        / **
         * date/time when the data is not longer valid
         * /
        Date timeOut;
*/
        /**
         * The previous element within list.
         */
        ListElement prev = null;
        /**
         * The next element within list.
         */
        ListElement next = null;


        /**********************************************************************
         * Constructor of this class. <BR/>
         *
         * @param   userId      The id of the user who tried to access the object.
         * @param   operation   The operation (s) the user tried to perform.
         * @param   hasRights   Is the user allowed to perform the operation (s)?
         */
        public ListElement (int userId, int operation, boolean hasRights)
/*
         * @param   timeOut     Date/time when the data is not longer valid.
        public ListElement (int userId, int operation, boolean hasRights,
                            Date timeOut)
*/
        {
            this.userId = userId;
            this.operation = operation;
            this.hasRights = hasRights;
/*
            this.timeOut = timeOut;
*/
        } // ListElement

    } // class ListElement

} // class AccessCache

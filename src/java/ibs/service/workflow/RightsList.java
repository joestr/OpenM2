/*
 * Class: RightsList
 */

// package:
package ibs.service.workflow;

// imports:
import ibs.BaseObject;
import ibs.service.workflow.RightsListElement;

import java.util.Hashtable;
import java.util.Iterator;


/******************************************************************************
 * Object holds list of rights and user/groups. The context is always on ONE
 * BusinessObject. Holds all rights entries stored in ibs_RightsKeys. <BR/>
 *
 * @version     $Id: RightsList.java,v 1.9 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 18.10.2000
 ******************************************************************************
 */
public class RightsList extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: RightsList.java,v 1.9 2007/07/24 21:27:33 kreimueller Exp $";


    /**
     * The list of rights entries for given users/groups. The key
     * is of type rPersonId (Integer) and the according value is a
     * RightsListElement. <BR/>
     */
    protected Hashtable<Integer, RightsListElement> list;


    /**************************************************************************
     * Creates a RightsList. <BR/>
     */
    public RightsList ()
    {
        // init class variables
        this.list = new Hashtable<Integer, RightsListElement> ();
    } // RightsList


     /**************************************************************************
     * Gets an entry with given rPersonId. <BR/>
     *
     * @param   rPersonId   id of group or user
     *
     * @return  RightsListElement  entry with key rPersonId exists
     *          <CODE>null</CODE> entry with given rPersonId where not found
     */
    public RightsListElement getEntry (int rPersonId)
    {
        // create Integer object
        Integer rPersonIdObject = new Integer (rPersonId);

        // check if entry with key rPersonId exists
        if (this.list.contains (rPersonIdObject))
        {
            // key exists; return entry
            return this.list.get (rPersonIdObject);
        } // if

        // key does not exist; return null
        return null;
    } // getEntry


     /**************************************************************************
     * Gets rights of given rPersonId. <BR/>
     *
     * @param   rPersonId   id of group or user
     *
     * @return  >=0    rights for given rPersonId
     *          < 0    no rights found
     */
    public int getRights (int rPersonId)
    {
        // create Integer object
        Integer rPersonIdObject = new Integer (rPersonId);

        // check if entry with key rPersonId exists
        if (this.list.containsKey (rPersonIdObject))
        {
            // get rights list element
            RightsListElement elem = this.list.get (rPersonIdObject);

            // check if element is valid
            if (elem != null)
            {
                // return rights value
                return elem.rights;
            } // if

            return -1;
        } // if

        // key does not exist; return null
        return -1;
    } // getRights



    /**************************************************************************
     * Adds an entry with given rPersonId an rights to the list. <BR/>
     *
     * @param   rPersonId   id of group or user
     * @param   rights      rights for rPersonId
     * @param   changed     Was the entry changed?
     *
     * @return  <CODE>false</CODE> if an entry with key rPersonId already exists,
     *          <CODE>true</CODE> if entry could have been added.
     */
    public boolean addEntry (int rPersonId, int rights, boolean changed)
    {
        // create Integer object
        Integer rPersonIdObject = new Integer (rPersonId);

        // check if entry with key rPersonId already exists
        if (this.list.contains (rPersonIdObject))
        {
            // key already exists; exit method
            return false;
        } // if

        // create new entry
        RightsListElement elem = new RightsListElement (rPersonId, rights, changed);

        // add entry to list
        this.list.put (rPersonIdObject, elem);

        // exit
        return true;
    } // addEntry


    /**************************************************************************
     * Changes an entry with given rPersonId in the list. <BR> The
     * changed-flag of the entry will be set to 'changed=true'.
     *
     * @param   rPersonId   id of group or user
     * @param   rights      rights for rPersonId
     *
     * @return  <CODE>true</CODE> if entry changed,
     *          <CODE>false</CODE> if an error occurred.
     */
    public boolean changeEntry (int rPersonId, int rights)
    {
        // create Integer object
        Integer rPersonIdObject = new Integer (rPersonId);

        // check if entry with key rPersonId already exists
        if (this.list.contains (rPersonIdObject))
        {
            // key already exists; remove entry
            this.list.remove (rPersonIdObject);
        } // changeEntry

        // now add new or changed entry
        return this.addEntry (rPersonId, rights, true);
    } // changeEntry


    /**************************************************************************
     * Changes entry of every user/group in list to given rights. <BR/>
     * The changed-flag of every entry will be set to 'changed=true'.
     *
     * @param   rights      rights for all entries
     */
    public void changeAllEntries (int rights)
    {
        // new list:
        Hashtable<Integer, RightsListElement> newList =
            new Hashtable<Integer, RightsListElement> ();
        Integer rPersonIdObject;
        RightsListElement elem = null;

        // iterate through all entries of old list and
        // create entries for new list
        for (Iterator<Integer> iter = this.list.keySet ().iterator (); iter.hasNext ();)
        {
            // get next key:
            rPersonIdObject = iter.next ();

            // create new rights list element object
            // - indicate that entry was changed
            elem = new RightsListElement (rPersonIdObject.intValue (), rights,
                true);

            // put new entry in new list
            newList.put (rPersonIdObject, elem);
        } // for iter

        // set new list
        this.list = newList;
    } // changeAllEntries


    /**************************************************************************
     * Return an iterator over all RightsElements in the list. <BR/>
     *
     * @return  Iterator object of hash table 'list'.
     */
    public Iterator<RightsListElement> iterator ()
    {
        return this.list.values ().iterator ();
    } // iterator


    /**************************************************************************
     * Returns a string representation of this object. <BR/>
     *
     * @return  A string representation of this object.
     */
    public String toString ()
    {
        return this.list.toString ();
    } // toString

} // class RightsList

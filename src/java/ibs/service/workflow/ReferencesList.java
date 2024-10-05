/*
 * Class: ReferencesList
 */

// package:
package ibs.service.workflow;

// imports:
import ibs.BaseObject;
//KR TODO: unsauber
import ibs.bo.OID;
import ibs.service.user.User;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * Object holds information for references which must be created during
 * one step of the workflow-process.<BR> Holds internaly a mapping between
 * two vectors which correlate to each other via position of elements.
 * one list holds user-object representing the user for whom the reference
 * shall be created and the oder list holds the according target-container-oids.
 * the user-objects are distinguished via their id-values which should be unique
 * in m2; that means: 2 different user-objects with the same id are treated as
 * equal in this list.
 *
 * @version     $Id: ReferencesList.java,v 1.7 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 18.10.2000
 ******************************************************************************
 */
public class ReferencesList extends BaseObject implements Cloneable
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ReferencesList.java,v 1.7 2007/07/24 21:27:33 kreimueller Exp $";


    /**
     * holds the information about the user of a reference. <BR/>
     * the user-element at the position i is directly associated with
     * the oid-entry at position i in referencesListOIDs. user-object
     * are distinguished via their id-values which should be unique
     * in m2; that means: 2 different user-objects with the same id
     * are treated as equal in this list.
     */
    protected Vector<User> referencesListUsers;

    /**
     * holds the information about the target-container OID of a reference. <BR/>
     * the oid at the position i is directly associated with
     * the user-entry at position i in referencesListUsers.
     */
    protected Vector<OID> referencesListOIDs;


    /**************************************************************************
     * Creates a ReferencesList. <BR/>
     */
    public ReferencesList ()
    {
        //init class variables
        this.referencesListUsers = new Vector<User> ();
        this.referencesListOIDs = new Vector<OID> ();
    } // RightsList


    /**************************************************************************
     * Gets the targetContainer for the given user. <BR/>
     *
     * @param   user  user-object
     *
     * @return  OID of the target-container
     *          null if no entry with given id found
     */
    public OID getEntry (User user)
    {
        // iterate through keys
        User userInList;
        for (int i = 0; i < this.referencesListUsers.size (); i++)
        {
            // get next entry
            userInList = this.referencesListUsers.elementAt (i);

            // check id
            if (userInList.id == user.id)
            {
                // return value of this entry
                return this.referencesListOIDs.elementAt (i);
            } // if
        } // while

        // not found
        return null;
    } // getEntry


    /**************************************************************************
     * Adds an entry with given user and target-oid to the reference structure.
     * <BR> Duplicate entries will not be added (same user[id], same targetOid).
     *
     * @param   user        user-object
     * @param   targetId    oid of the target-container
     *
     * @return  false       if an entry with key userId already exists
     *          true        entry added
     */
    public boolean addEntry (User user, OID targetId)
    {
        // check if entry with users id and target oid exists
        if (this.containsUserIdTargetOid (user, targetId))
        {
            // key already exists; exit method
            return false;
        } // if

        // add new entry to list
        this.referencesListUsers.addElement (user);
        this.referencesListOIDs.addElement (targetId);

        // exit
        return true;
    } // addEntry


    /**************************************************************************
     * Removes entry with given user and target-oid. <BR/>
     *
     * @param   user        user-object
     * @param   targetId    oid of the target-container
     */
    public void removeEntry (User user, OID targetId)
    {
        // iterate through keys
        User userInList;
        for (int i = 0; i < this.referencesListUsers.size (); i++)
        {
            // get next entry
            userInList = this.referencesListUsers.elementAt (i);

            // check if user with id exists
            if (userInList.id == user.id)
            {
                // user found: now check if targetOid of this
                // entry is equal to given targetOid
                if (this.referencesListOIDs.elementAt (i).equals (targetId))
                {
                    // remove entry
                    this.referencesListUsers.removeElementAt (i);
                    this.referencesListOIDs.removeElementAt (i);
                    // exit
                    return;
                } // if
            } // if
        } // for
    } // removeEntry


    /**************************************************************************
     * Check if references list contains a user-object with id of given user.
     * <BR/>
     *
     * @param   user        user-object
     *
     * @return  false       if an entry with key userId already exists
     *          true        entry added
     */
    public boolean containsUserId (User user)
    {
        // iterate through keys
        User userInList;
        for (int i = 0; i < this.referencesListUsers.size (); i++)
        {
            // get next entry
            userInList = this.referencesListUsers.elementAt (i);

            // check if user with id exists
            if (userInList.id == user.id)
            {
                // user found: now check if targetOid of this
                return true;
            } // if
        } // for

        // not found
        return false;
    } // containsUserId


    /**************************************************************************
     * Check if references list contains an entry with a user-object where
     * id is equal to id of given user and given OID is equal to targetoid
     * of entry.
     * <BR/>
     *
     * @param   user        User object.
     * @param   targetOid   Oid of the target container.
     *
     * @return  <CODE>true</CODE> entry added.
     *          <CODE>false</CODE> if an entry with key userId and
     *          value targetOid already exists.
     */
    public boolean containsUserIdTargetOid (User user, OID targetOid)
    {
        // iterate through keys
        User userInList;
        for (int i = 0; i < this.referencesListUsers.size (); i++)
        {
            // get next entry
            userInList = this.referencesListUsers.elementAt (i);

            // check if user with id exists
            if (userInList.id == user.id)
            {
                // user found: now check if targetOid of this
                // entry is equal to given targetOid
                if (this.referencesListOIDs.elementAt (i).equals (targetOid))
                {
                    // found - exit with success
                    return true;
                } // if
            } // if
        } // for

        // not found
        return false;
    } // containsUserId


    /**************************************************************************
     * Return iterator for keys of references-entry-mappings (user-objects).
     * <BR/>
     *
     * @return  The iterator for the keys.
     */
    public Iterator<User> keyIterator ()
    {
        return this.referencesListUsers.iterator ();
    } // keyIterator


    /**************************************************************************
     * Returns a string representation of this object. <BR/>
     *
     * @return      a string representation of this object
     */
    public String toString ()
    {
        String str = "[";

        // iterate through keys
        User userInList;
        OID oidInList;
        for (int i = 0; i < this.referencesListUsers.size (); i++)
        {
            // get next entry
            userInList = this.referencesListUsers.elementAt (i);
            oidInList = this.referencesListOIDs.elementAt (i);

            // append to string
            str += " user=" + userInList.id + "/" + userInList.username + " " +
                   "targetOid= " + oidInList.toString () + "; ";
        } // for

        // exit
        return str + "]";
    } // toString


    /**************************************************************************
     * Returns a clone (shallow copy) of this object. <BR/>
     *
     * @return      the cloned ReferencesList
     *              null if cloning was not successfull
     */
    @SuppressWarnings ("unchecked") // suppress compiler warning
    public Object clone ()
    {
        try
        {
            ReferencesList rl = (ReferencesList) super.clone ();
            rl.referencesListOIDs = (Vector<OID>) this.referencesListOIDs.clone ();
            rl.referencesListUsers = (Vector<User>) this.referencesListUsers.clone ();
            return rl;
        } // try
        catch (CloneNotSupportedException e)
        {
            return e;
        } // catch
    } // keys

} // class ReferencesList

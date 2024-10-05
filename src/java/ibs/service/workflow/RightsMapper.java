/*
 * Class: RightsMapper
 */

// package:
package ibs.service.workflow;

// imports:
import ibs.BaseObject;

import java.util.Hashtable;


/******************************************************************************
 * Object holds mapping of m2-rights to definable rights-aliases. <BR/>
 * Mapping: logical rights name (string) -> combination of m2 rights (integer).
 * The  key (name) must be unique.
 *
 * @version     $Id: RightsMapper.java,v 1.9 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 18.10.2000
 ******************************************************************************
 */
public class RightsMapper extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: RightsMapper.java,v 1.9 2007/07/24 21:27:33 kreimueller Exp $";


    /**
     * holds the mapping from m2-rights to rights-aliases. <BR/>
     * key: logical name of alias (String)
     * value: m2-rights combination (Integer)
     */
    protected Hashtable<String, Integer> mapping;


    /**************************************************************************
     * Creates a WorkflowRightsMapper. <BR/>
     */
    public RightsMapper ()
    {
        //init class variables
        this.mapping = new Hashtable<String, Integer> ();
    } // RightsList


     /**************************************************************************
     * Gets an rights-entry for given rights-alias. <BR/>
     *
     * @param   aliasName   name of the alias
     *
     * @return  >=0     the rights-value itself
     *          <0      rights for given name not found
     */
    public int getEntry (String aliasName)
    {
        // check if entry with key rPersonId exists
        if (this.mapping.containsKey (aliasName))
        {
            // key exists; return integer rights-entry
            return this.mapping.get (aliasName).intValue ();
        } // if

        // key does not exist; return null
        return -1;
    } // getEntry


    /**************************************************************************
     * Adds an entry with given aliasName and rights to the mapping structure.
     * <BR/>
     *
     * @param   aliasName   name of the rights alias
     * @param   rights      m2 rights for the alias
     *
     * @return  <CODE>false</CODE> if an entry with key aliasName already exists,
     *          <CODE>true</CODE> if entry added.
     */
    public boolean addEntry (String aliasName, int rights)
    {
        // check if entry with key rPersonId already exists
        if (this.mapping.contains (aliasName))
        {
            // key already exists; exit method
            return false;
        } // if

        // create Integer object for rights
        Integer rightsObject = new Integer (rights);

        // add new entry to list
        this.mapping.put (aliasName, rightsObject);

        // exit
        return true;
    } // addEntry


    /**************************************************************************
     * Returns a string representation of this object. <BR/>
     *
     * @return  A string representation of this object.
     */
    public String toString ()
    {
        return this.mapping.toString ();
    } // toString

} // class RightsMapper

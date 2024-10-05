/*
 * Class: RightsListElement
 */

// package:
package ibs.service.workflow;

// imports:
import ibs.BaseObject;


/******************************************************************************
 * Object holds one entry of RightsList. <BR/>
 *
 * @version     $Id: RightsListElement.java,v 1.8 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 18.10.2000
 ******************************************************************************
 */
public class RightsListElement extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: RightsListElement.java,v 1.8 2007/07/24 21:27:33 kreimueller Exp $";


    /**
     * rPersonId of the rights entry (can be group or user). <BR/>
     */
    public int rPersonId = 0;

    /**
     * rights for rPersonId. <BR/>
     */
    public int rights = 0;

    /**
     * was entry changed?. <BR/>
     */
    public boolean changed = false;


    /**************************************************************************
     * Creates an WorkflowRightsList. <BR/>
     */
    public RightsListElement ()
    {
        // nothing to do
    } // RightsListElement


    /**************************************************************************
     * Creates an WorkflowRightsList. <BR/>
     *
     * @param   rPersonId   id of group or user
     * @param   rights      rights for rPersonId
     * @param   changed     change-state of entry
     */
    public RightsListElement (int rPersonId, int rights, boolean changed)
    {
        //init class variables
        this.rPersonId = rPersonId;
        this.rights = rights;
        this.changed = changed;
    } // RightsListElement


    /**************************************************************************
     * Returns a string representation of this object. <BR/>
     *
     * @return  A string representation of this object.
     */
    public String toString ()
    {
        return "rPersonId = " + this.rPersonId + "; " +
               "rights = " + this.rights + "; " +
               "changed = " + this.changed;
    } // toString

} // class RightsListElement

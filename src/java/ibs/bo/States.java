/*
 * Class: States.java
 */

// package:
package ibs.bo;

// imports:
import ibs.bo.BOTokens;


/******************************************************************************
 * Constants for states of business objects. <BR/>
 * This abstract class contains all constants which are necessary to deal with
 * the utilities delivered within this package.
 *
 * @version     $Id: States.java,v 1.9 2010/04/07 13:37:08 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 980429
 ******************************************************************************
 */
public abstract class States extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: States.java,v 1.9 2010/04/07 13:37:08 rburgermann Exp $";


    // states which a business object may have:
    /**
     * The state of the object is not known. <BR/>
     *  Possible reasons may be that the object was not read from the database
     * yet or it was just initialized.
     */
    public static final int ST_UNKNOWN         =    -1;
    /**
     * The object is not existent. <BR/>
     * The object is not defined yet.
     */
    public static final int ST_NONEXISTENT     =    0;
    /**
     * The object was deleted. <BR/>
     */
    public static final int ST_DELETED         =    1;
    /**
     * The object is active. <BR/>
     */
    public static final int ST_ACTIVE          =    2;
    /**
     * The object is inactive, i.e. it was deactivated. <BR/>
     */
    public static final int ST_INACTIVE        =    3;
    /**
     * The object was just created, i.e. there was no other writing operation
     * to the object. <BR/>
     */
    public static final int ST_CREATED         =    4;
    /**
     * The object was finished, so it cannot be changed in the future. <BR/>
     */
    public static final int ST_FINISHED        =    11;
    /**
     * The object is frozen, so it must be unfrozen to be able to change it.
     * <BR/>
     */
    public static final int ST_FROZEN          =    12;
    /**
     * The object is locked. <BR/>
     * This means that a user has locked this object to have exclusive rights on
     * it. This can be done by checking it out. Nobody else has the possibility
     * to change the object until it is unlocked.
     */
    public static final int ST_LOCKED          =    13;
    /**
     * The object is requested. <BR/>
     */
    public static final int ST_REQUESTED       =    14;

//=================   PROZESS - STATES ====================================
// STANDARD STATES
    /**
     * Process state: NONE. <BR/>
     */
    public static final int PST_NONE             = 0;
    /**
     * Process state: Process currently executing. <BR/>
     */
    public static final int PST_EXECUTING        = 5;
    /**
     * Process state: Process not approved. <BR/>
     */
    public static final int PST_NOTAPPROVED      = 10;
    /**
     * Process state: Process approved. <BR/>
     */
    public static final int PST_APPROVED         = 15;
    /**
     * Process state: The data were stored. <BR/>
     */
    public static final int PST_STORED           = 20;
    /**
     * Process state: Process open. <BR/>
     */
    public static final int PST_OPEN             = 25;
    /**
     * Process state: Process rejected. <BR/>
     */
    public static final int PST_REJECTED         = 30;

// ORDER STATES
    /**
     * The order was sent to the supplier. <BR/>
     */
    public static final int PST_ORDERED = 35;
    /**
     * The order receipt was commited by supplier. <BR/>
     */
    public static final int PST_DELIVERED = 40;

    /**
     * The order was discarded from the owner. <BR/>
     */
    public static final int PST_DISCARDED = 45;
    /**
     * The order was discarded from the owner. <BR/>
     */
    public static final int PST_COMPLETED = 50;

// STATE ARRAYS
    /**
     * Ids of project states. <BR/>
     */
    public static final String [] PST_STATEIDS =
    {
        Integer.toString (States.PST_NONE),
        Integer.toString (States.PST_EXECUTING),
        Integer.toString (States.PST_NOTAPPROVED),
        Integer.toString (States.PST_APPROVED),
        Integer.toString (States.PST_STORED),
        Integer.toString (States.PST_OPEN),
        Integer.toString (States.PST_REJECTED),
        Integer.toString (States.PST_ORDERED),
        Integer.toString (States.PST_DELIVERED),
        Integer.toString (States.PST_DISCARDED),
        Integer.toString (States.PST_COMPLETED),
    }; // ST_STATEIDS

    /**
     * Names of project states. <BR/>
     */
    public static final String [] PST_STATENAMES =
    {
        BOTokens.ML_PST_NONE,
        BOTokens.ML_PST_EXECUTING,
        BOTokens.ML_PST_NOTAPPROVED,
        BOTokens.ML_PST_APPROVED,
        BOTokens.ML_PST_STORED,
        BOTokens.ML_PST_OPEN,
        BOTokens.ML_PST_REJECTED,
        BOTokens.ML_PST_ORDERED,
        BOTokens.ML_PST_DELIVERED,
        BOTokens.ML_PST_DISCARDED,
        BOTokens.ML_PST_COMPLETED,
    }; // ST_STATENAMES

} // class States

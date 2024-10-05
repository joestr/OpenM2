/*
 * Class: IbsFunction.java
 */

// package:
package ibs.obj.func;

// imports:
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.obj.func.FunctionArguments;
import ibs.service.user.User;


/******************************************************************************
 * Interface for all Functions to be performed within the framework. <BR/>
 * This class should ensure, that all objects, data etc. regarding to
 * one function, are encapsulated together in one topclass. <BR/>
 *
 * In first Version, the function extends BusinessObject, to be able to
 * use the cachingalgorithm of Application - should be an abstract class
 * in final implementation.
 *
 * @version     $Id: IbsFunction.java,v 1.10 2009/07/24 08:36:58 kreimueller Exp $
 *
 * @author      Andreas Jansa (AJ), 000918
 ******************************************************************************
 */
public abstract class IbsFunction extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IbsFunction.java,v 1.10 2009/07/24 08:36:58 kreimueller Exp $";


    /**
     * id of function. <BR/>
     */
    protected int id = -1;

// will be needed when IbsFunctions not anymore extends IbsObject

    /**
     * Environment for getting input and generating output. <BR/>
     */
//    protected Environment env = null;

    /**
     * Holds the actual session info. <BR/>
     */
//    public Sessioninfo sess;

    /**
     * Holds the actual application info. <BR/>
     */
//    public ApplicationInfo app;

    /**
     * Object representing the actual user. <BR/>
     * This object must contain all information which is necessary for checking
     * the actual user's rights, i.e. user name, groups, etc.
     */
//    public User user = null;


    /**************************************************************************
     * This constructor creates a new instance of the class IbsFunction. <BR/>
     */
    public IbsFunction ()
    {
        // nothing to do
    } // IbsFunction


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // nothing to do
    } // initClassSpecifics


    /**************************************************************************
     * Initializes a Function. <BR/>
     *
     * The user object is also stored in a specific
     * property of this object to make sure that the user's context can be used
     * for getting his/her rights. <BR/>
     * {@link #env env} is initialized to the provided object. <BR/>
     * {@link #sess sess} is initialized to the provided object. <BR/>
     * {@link #app app} is initialized to the provided object. <BR/>
     *
     * @param   aId     The id of the function.
     * @param   aUser   Object representing the user.
     * @param   aEnv    The actual call environment.
     * @param   aSess   The actual session info.
     * @param   aApp    The global application info.
     */
    public void initFunction (int aId, User aUser, Environment aEnv,
                            SessionInfo aSess, ApplicationInfo aApp)
    {
        // set id of function
        this.id = aId;

        // set the instance's public/protected properties:
        this.setOid (OID.EMPTYOID);
        this.setEnv (aEnv);
        this.app = aApp;
        this.sess = aSess;
        this.user = aUser;               // set the user

        // ensure that there is a tracer available:
        this.openTrace ();
    } // initFunction


    /**************************************************************************
     * Get the current thrown event. <BR/>
     *
     * @return  id of last thrown event, or -1 if no current event exist.
     */
    public int getEvent ()
    {
        int eventId;

        // if eventId is set in environment (via URL) return current eventId
        if ((eventId = this.env.getIntParam (FunctionArguments.ARG_EVENT)) !=
                                IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            return eventId;
        } // if

        return -1;
    } // getEvent


    /**************************************************************************
     * mainmethod = sequence control of this function. <BR/>
     * THIS METHOD MUST BE OVERWRITTEN IN SUBCLASSES !!!
     */
    public void start ()
    {
/*
EXAMPLE:
        switch (getEvent)
        {
            case EVT_ID1:
            {
                    statement ...
            } // case
            case EVT_ID2:
            {

            } // case
            default:
            {

            } // default
        } // switch
*/
    } // getEvent

} // class IbsFunction

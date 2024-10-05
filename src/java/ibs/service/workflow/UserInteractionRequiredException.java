/*
 * Class: UserInteractionRequiredException.java
 */


// package:
package ibs.service.workflow;

// imports:
//KR TODO: unsauber
import ibs.bo.BusinessObject;
import ibs.service.workflow.State;
import ibs.util.GeneralException;


/******************************************************************************
 * This class implements the error handler. <BR/>
 * An error mapping file is used to get meaningful error messages according to
 * the rather abstract error codes returned by implementation specific
 * classes.<P>
 * Note that errors could be chained - this is represented by a vector
 * containing all errors that happened on the way to and from the database. <BR/>
 *
 * @version     $Id: UserInteractionRequiredException.java,v 1.6 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR) 980401
 ******************************************************************************
 */
public class UserInteractionRequiredException extends GeneralException
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: UserInteractionRequiredException.java,v 1.6 2007/07/24 21:27:33 kreimueller Exp $";


    /**
     * Serializable version number. <BR/>
     * This value is used by the serialization runtime during deserialization
     * to verify that the sender and receiver of a serialized object have
     * loaded classes for that object that are compatible with respect to
     * serialization. <BR/>
     * If the receiver has loaded a class for the object that has a different
     * serialVersionUID than that of the corresponding sender's class, then
     * deserialization will result in an {@link java.io.InvalidClassException}.
     * <BR/>
     * This field's value has to be changed every time any serialized property
     * definition is changed. Use the tool serialver for that purpose.
     */
    static final long serialVersionUID = 7496458581248284592L;


    /**
     * The object wich has been forwarded in the workflow in which
     * this exception occurred. <BR/>
     */
    public BusinessObject forwardObj = null;

    /**
     * Holds one state of the workflow in which this exception occurred. <BR/>
     */
    public State state = null;


    /**************************************************************************
     * Create a new exception. <BR/>
     *
     * @param   name    The name of the error object representing the class that
     *                  raised an error first.
     */
    public UserInteractionRequiredException (String name)
    {
        super (name);                   // call constructor of upper class
    } // UserInteractionRequiredException


    /**************************************************************************
     * Create a new exception. <BR/>
     *
     * @param   name        The name of the error object representing the class
     *                      that raised an error first.
     * @param   forwardObj  The object to be forwarded.
     * @param   state       The state of the object.
     */
    public UserInteractionRequiredException (String name,
                                             BusinessObject forwardObj,
                                             State state)
    {
        super (name);                   // call constructor of upper class

        // set properties
        this.forwardObj = forwardObj;
        this.state = state;
    } // UserInteractionRequiredException

} // class UserInteractionRequiredException

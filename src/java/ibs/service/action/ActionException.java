/*
 * Class ActionException.java
 */

// package:
package ibs.service.action;

// imports:
import ibs.util.GeneralException;


/******************************************************************************
 * The ActionException is used to report errors in the action package. <BR/>
 *
 * @version     $Id: ActionException.java,v 1.5 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      Michael Steiner
 ******************************************************************************
 */
public class ActionException extends GeneralException
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ActionException.java,v 1.5 2007/07/24 21:27:33 kreimueller Exp $";


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
    static final long serialVersionUID = 1795105216227036439L;


    /**************************************************************************
     * Constructor for the ActionException class.
     *
     * @param   message     A String containing the error message
     */
    public ActionException (String message)
    {
        super (message);
    } // ActionException

} // ActionException

/*
 * Class: DBActionPoolException.java
 */

// package:
package ibs.tech.sql;

// imports:
import ibs.tech.sql.DBError;


/******************************************************************************
 * The DBActionPoolException is thrown if ther is a problem in the
 * DBActionPool. <BR/>
 *
 * @version     $Id: DBActionPoolException.java,v 1.8 2007/07/23 08:17:33 kreimueller Exp $
 *
 * @author      Mark Wassermann (MW)
 ******************************************************************************
 */
public class DBActionPoolException extends DBError
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DBActionPoolException.java,v 1.8 2007/07/23 08:17:33 kreimueller Exp $";


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
    static final long serialVersionUID = -2532645639257988500L;


    /**************************************************************************
     * Constructor of this class. <BR/>
     *
     * @param   name    The name of the error object representing the class that
     *                  raised an error first.
     */
    public DBActionPoolException (String name)
    {
        super (name);
    } // DBActionPoolException


    /**************************************************************************
     * Create a new exception. <BR/>
     *
     * @param runtimeObject    The object of the runtime class where
     *                         the exception occurs.
     * @param chainedException The chained exception if one exists, else null.
     * @param displayChained   The flag if the chained exception shall be displayed
     *                         or not. If t was null then chained is ignored.
     */
    public DBActionPoolException (Object runtimeObject,
                                  Throwable chainedException,
                                  boolean displayChained)
    {
        super (chainedException);
/*
        super (chainedException.getClass () + ": " +
               chainedException.getMessage () + IE302.TAG_NEWLINE + "Stacktrace: " +
               Helpers.getStackTraceFromThrowable (chainedException));
*/
    } // DBActionPoolException

} // DBActionPoolException

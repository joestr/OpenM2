/*
 * Class: ExportException.java
 */

// package:
package ibs.di.exp;

// imports:
import ibs.util.GeneralException;


/******************************************************************************
 * This class.... <BR/>
 *
 * @version     $Id: ExportException.java,v 1.3 2007/07/31 19:13:54 kreimueller Exp $
 *
 * @author      klaus, 17.05.2005
 ******************************************************************************
 */
public class ExportException extends GeneralException
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ExportException.java,v 1.3 2007/07/31 19:13:54 kreimueller Exp $";


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
    static final long serialVersionUID = -2777532073427938917L;


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////


    /**************************************************************************
     * Creates a ExportException object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param name  Name of the exception.
     */
    public ExportException (String name)
    {
        super (name);
    } // ExportException


    /**************************************************************************
     * Creates a ExportException object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param name  Name of the exception.
     * @param cause Cause of the exception (= cascaded exception).
     */
    public ExportException (String name, Throwable cause)
    {
        super (name, cause);
    } // ExportException

} // class ExportException

/*
 * Class FormException.java
 */

// package:
package ibs.di;

// imports:
import ibs.util.GeneralException;


/******************************************************************************
 * The FormException is used to handle errors in form objects. <BR/>
 *
 * @version     $Id: FormException.java,v 1.5 2007/08/10 14:56:37 kreimueller Exp $
 *
 * @author      Michael Steiner
 ******************************************************************************
 */
public class FormException extends GeneralException
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FormException.java,v 1.5 2007/08/10 14:56:37 kreimueller Exp $";


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
    static final long serialVersionUID = 5159817918315954615L;


    /**************************************************************************
     * Constructor for the FormException class.
     *
     * @param   message     A String containing the error message
     */
    public FormException (String message)
    {
        super (message);
    } // FormException

} // FormException

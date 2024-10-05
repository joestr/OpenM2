/*
 * Class: BuildException.java
 */

// package:
package ibs.tech.html;

// imports:


/******************************************************************************
 * This is the BuildException
 *
 * @version     $Id: BuildException.java,v 1.6 2007/07/20 12:59:26 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980318
 ******************************************************************************
 */
public class BuildException extends Throwable
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BuildException.java,v 1.6 2007/07/20 12:59:26 kreimueller Exp $";


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
    static final long serialVersionUID = -6042287899354315409L;


    /**
     * ???
     */
    String error;


    /**************************************************************************
     * This method ... <BR/>
     *
     * @return  ???
     */
    public String getMsg ()
    {
        return this.error;
    } // getMsg


    /**************************************************************************
     * Creates a BuildException object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   exception   ???
     */
    public BuildException (String exception)
    {
        this.error = exception;
    } // BuildException

} // class BuildException

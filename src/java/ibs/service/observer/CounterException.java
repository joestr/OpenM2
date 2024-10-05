/*
 * Created by IntelliJ IDEA.
 * User: hpichler
 * Date: 06.08.2002
 * Time: 11:07:08
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */

// package:
package ibs.service.observer;

// imports:


/******************************************************************************
 * This class.... <BR/>
 *
 * @version     $Id: CounterException.java,v 1.4 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      hpichler, 06.08.2002
 ******************************************************************************
 */
public class CounterException extends Exception
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: CounterException.java,v 1.4 2007/07/24 21:27:33 kreimueller Exp $";


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
    static final long serialVersionUID = -3583541904957623747L;



    /**************************************************************************
     * Creates a CounterException object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     */
    public CounterException ()
    {
        // call common method:
        super ();
    } // CounterException


    /**************************************************************************
     * Creates a CounterException object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   s       The exception message.
     */
    public CounterException (String s)
    {
        super (s);
    } // CounterException

} // CounterException

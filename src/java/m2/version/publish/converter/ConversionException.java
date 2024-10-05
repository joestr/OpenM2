/**
 * Class: ConversionException
 */

// package:
package m2.version.publish.converter;

// imports:
import ibs.io.Environment;
import ibs.ml.MultilingualTextProvider;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;

import m2.version.publish.converter.ConversionMessages;

import java.io.File;


/******************************************************************************
 * The exception which is thrown in case of an error while converting a file. <BR/>
 *
 * @version     $Id: ConversionException.java,v 1.7 2010/04/07 13:37:05 rburgermann Exp $
 *
 * @author      Bernd Martin (BM) Dec 10, 2001
 ******************************************************************************
 */
public class ConversionException extends Exception
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ConversionException.java,v 1.7 2010/04/07 13:37:05 rburgermann Exp $";


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
    static final long serialVersionUID = -6155137891088544368L;



    /**************************************************************************
     * Constructor for the exception. <BR/>
     *
     * @param   file    The file which caused the conversion process to fail.
     * @param   env     The current environment
     */
    public ConversionException (File file, Environment env)
    {
        super (MultilingualTextProvider.getMessage (ConversionMessages.MSG_BUNDLE, 
           ConversionMessages.ML_MSG_CONVERSIONFAILED, 
           new String[] {file.getName ()}, env));
    } // ConversionException


    /**************************************************************************
     * Constructor for the exception. <BR/>
     *
     * @param   msg The detailed error message.
     */
    public ConversionException (String msg)
    {
        super (msg);
    } // ConversionException

} // class ConversionException

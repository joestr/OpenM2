/**
 * Class: ConverterImpl
 */

// package:
package m2.version.publish.converter;

// imports:
import m2.version.publish.converter.ConversionException;
import m2.version.publish.converter.IConverter;

import java.io.File;


/******************************************************************************
 * This class represents a default implementation of the converter. It does
 * not do anything. <BR/>
 *
 * @version     $Id: ConverterImpl.java,v 1.3 2007/07/10 21:01:32 kreimueller Exp $
 *
 * @author      Bernd Martin (BM) Dec 3, 2001
 ******************************************************************************
 */
public class ConverterImpl implements IConverter
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ConverterImpl.java,v 1.3 2007/07/10 21:01:32 kreimueller Exp $";


    /**************************************************************************
     * This method converts a file to another file. <BR/>
     */
    public ConverterImpl ()
    {
        // nothing to do within this common implementation
    } // ConverterImpl


    /***************************************************************************
     * This method converts a file to another file. <BR/>
     *
     * @param   file    The file object which contains the original object.
     *
     * @return  The converted file object. This can be the same as the input
     *          file object or an object representing a newly generated file.
     *          If no conversion takes place then the original object file is
     *          returned.
     *
     * @throws  ConversionException
     *          If an error occurred this exception is thrown.
     */
    public File convertFile (File file) throws ConversionException
    {
        return file;
    } // convertFile

} // class ConverterImpl

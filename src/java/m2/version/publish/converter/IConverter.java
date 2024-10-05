/*
 * Class: IConverter.java
 */

// package:
package m2.version.publish.converter;

// imports:
import m2.version.publish.converter.ConversionException;

import java.io.File;


/*******************************************************************************
 * This represents the interface for the file conversion utility. <BR/>
 *
 * @version     $Id: IConverter.java,v 1.3 2007/07/10 21:01:32 kreimueller Exp $
 *
 * @author      Bernd Martin (BM), 011203
 *******************************************************************************
 */
public interface IConverter
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IConverter.java,v 1.3 2007/07/10 21:01:32 kreimueller Exp $";


    /***************************************************************************
     * The method converts an input file into another format. If an error occurs
     * an exception is thrown. <BR/>
     *
     * @param   file    The input file which must be converted.
     *
     * @return  The converted file object. This can be the same as the input
     *          file object or an object representing a newly generated file.
     *          If no conversion takes place then the original object file is
     *          returned.
     *
     * @throws  ConversionException
     *          If an error occurred this exception is thrown.
     */
    public File convertFile (File file) throws ConversionException;

} // interface IConverter

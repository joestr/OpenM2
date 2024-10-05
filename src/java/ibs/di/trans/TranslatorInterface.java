/*
 * Interface: TranslatorInterface.java
 */

// package:
package ibs.di.trans;

// imports:
import ibs.di.Log_01;
import ibs.di.trans.TranslationFailedException;


/******************************************************************************
 * The TranslatorInterface defines the interface for all kind of Translators.
 * <BR/>
 *
 * @version     $Id: TranslatorInterface.java,v 1.7 2007/07/31 19:13:55 kreimueller Exp $
 *
 * @author      Buchegger Bernd (BB), 991102
 ******************************************************************************
 */
interface TranslatorInterface
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TranslatorInterface.java,v 1.7 2007/07/31 19:13:55 kreimueller Exp $";


    /**************************************************************************
     * The translate method converts a file into a specific format and returns
     * the name of the generated file. <BR/>
     * A translator should never changes the original file. A new file will be
     * generated with the prefix "t_" in the generated filename.
     * E.g. the file "file.xml" will be translated into a file named
     * "t_file.xml" without deleted the original file. The new filename will
     * be returned. <BR/>
     *
     * @param   path        The path to the file to be translated.
     * @param   fileName    The name of the file to be translated.
     * @param   log         The log to write translation messages to.
     *
     * @return  The name of the translated file.
     *
     * @throws  TranslationFailedException
     *          The translation could not be processed.
     */
    abstract String translate (String path, String fileName, Log_01 log)
        throws TranslationFailedException;

} // interface TranslatorInterface

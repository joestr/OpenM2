/*
 * Class: DIErrorHandler.java
 */

// package:
package ibs.di;

// imports:
//KR TODO: unsauber
import ibs.bo.BusinessObject;
import ibs.di.DITokens;
//KR TODO: unsauber
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/******************************************************************************
 * The DIErrorHandler handles errors from the parser. <BR/>
 * The DIErrorHandler implements the sax ErrorHandler class which is valid for
 * both SAX and DOM parsers. <BR/>
 *
 * @version     $Id: DIErrorHandler.java,v 1.6 2010/04/07 13:37:06 rburgermann Exp $
 *
 * @author      Buchegger Bernd (BB), 990108
 ******************************************************************************
 */
public class DIErrorHandler extends BusinessObject implements ErrorHandler
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DIErrorHandler.java,v 1.6 2010/04/07 13:37:06 rburgermann Exp $";


    /**************************************************************************
     * Handles fatal errors from the parser. <BR/>
     *
     * @param   e   The error information encapsulated in a SAX parse exception.
     *
     * @throws  SAXException
     *          Any SAX exception, possibly wrapping another exception.
     */
    public void fatalError (SAXParseException e) throws SAXException
    {
        IOHelpers.showMessage (
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_PARSERFATALERROR, this.env) + ": " + e.toString () +
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_AT_LINE, this.env) + e.getLineNumber () +
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_COLUMN, this.env) + e.getColumnNumber (),
            this.app, this.sess, this.env);
    } // fatalError


    /**************************************************************************
     * Handles errors from the parser. <BR/>
     *
     * @param   e   The error information encapsulated in a SAX parse exception.
     *
     * @throws  SAXException
     *          Any SAX exception, possibly wrapping another exception.
     */
    public void error (SAXParseException e) throws SAXException
    {
        IOHelpers.showMessage (
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_PARSERERROR, this.env) + ": " + e.toString () +
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_AT_LINE, this.env) + e.getLineNumber () +
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_COLUMN, this.env) + e.getColumnNumber (),
            this.app, this.sess, this.env);
    } // error


    /**************************************************************************
     * Handles warnings from the parser. <BR/>
     *
     * @param   e   The warning information encapsulated in a SAX parse exception.
     *
     * @throws  SAXException
     *          Any SAX exception, possibly wrapping another exception.
     */
    public void warning (SAXParseException e) throws SAXException
    {
        IOHelpers.showMessage (
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_PARSERWARNING, this.env) + ": " + e.toString () +
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_AT_LINE, this.env) + e.getLineNumber () +
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_COLUMN, this.env) + e.getColumnNumber (),
            this.app, this.sess, this.env);
    } // warning

} // class DIErrorHandler

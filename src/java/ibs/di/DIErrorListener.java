/*
 * Class: DIErrorListener.java
 */

// package:
package ibs.di;

// imports:
import ibs.ml.MultilingualTextProvider;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXParseException;


/******************************************************************************
 * The DIErrorListener handles errors from the XML parser. <BR/>
 *
 * @version     $Id: DIErrorListener.java,v 1.4 2010/04/07 13:37:06 rburgermann Exp $
 *
 * @author      Buchegger Bernd (BB), 20080911
 ******************************************************************************
 */
public class DIErrorListener  implements ErrorListener
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DIErrorListener.java,v 1.4 2010/04/07 13:37:06 rburgermann Exp $";

    /**
     * The current error message. <BR/>
     */
    private StringBuffer errorMessage = new StringBuffer ();



    /**************************************************************************
     * Handles fatal errors from the parser. <BR/>
     *
     * @param   e   The error information encapsulated in a SAX parse exception.
     *
     * @throws  TransformerException
     *          Exception during transforming.
     */
    public void fatalError (TransformerException e) throws TransformerException
    {
        // TODO RB: Call  
        //          MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
        //              DITokens.ML_PARSERFATALERROR, env)
        //          to get the text in the correct language
        this.addErrorMessage (e, DITokens.ML_PARSERFATALERROR);
        throw e;
    } // fatalError

    /**************************************************************************
     * Handles fatal errors from the parser. <BR/>
     *
     * @param   e   The error information encapsulated in a SAX parse exception.
     *
     * @throws  SAXParseException
     *          Exception during parsing.
     */
    public void fatalError (SAXParseException e) throws SAXParseException
    {
        // TODO RB: Call  
        //          MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
        //              DITokens.ML_PARSERFATALERROR, env)
        //          to get the text in the correct language
        this.addErrorMessage (e, DITokens.ML_PARSERFATALERROR);
        throw e;
    } // fatalError

    /**************************************************************************
     * Handles errors from the parser. <BR/>
     *
     * @param   e   The error information encapsulated in a SAX parse exception.
     *
     * @throws  TransformerException
     *          Exception during transforming.
     */
    public void error (TransformerException e) throws TransformerException
    {
        // TODO RB: Call  
        //          MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
        //              DITokens.ML_PARSERERROR, env)
        //          to get the text in the correct language
        this.addErrorMessage (e, DITokens.ML_PARSERERROR);
        throw e;
    } // error

    /**************************************************************************
     * Handles errors from the parser. <BR/>
     *
     * @param   e   The error information encapsulated in a SAX parse exception.
     *
     * @throws  SAXParseException
     *          Exception during parsing.
     */
    public void error (SAXParseException e) throws SAXParseException
    {
        // TODO RB: Call  
        //          MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
        //              DITokens.ML_PARSERERROR, env)
        //          to get the text in the correct language
        this.addErrorMessage (e, DITokens.ML_PARSERERROR);
        throw e;
    } // error


    /**************************************************************************
     * Handles warnings from the parser. <BR/>
     *
     * @param   e   The warning information encapsulated in a SAX parse exception.
     *
     * @throws  TransformerException
     *          Exception during transforming.
     */
    public void warning (TransformerException e) throws TransformerException
    {
        // TODO RB: Call  
        //          MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
        //              DITokens.ML_PARSERWARNING, env)
        //          to get the text in the correct language
        this.addErrorMessage (e, DITokens.ML_PARSERWARNING);
        throw e;
    } // warning


    /**************************************************************************
     * Handles warnings from the parser. <BR/>
     *
     * @param   e   The warning information encapsulated in a SAX parse exception.
     *
     * @throws  SAXParseException
     *          Exception during parsing.
     */
    public void warning (SAXParseException e) throws SAXParseException
    {
        // TODO RB: Call  
        //          MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
        //              DITokens.ML_PARSERWARNING, env)
        //          to get the text in the correct language
        this.addErrorMessage (e, DITokens.ML_PARSERWARNING);
        throw e;
    } // warning


    /**************************************************************************
     * Add an exception to the the collected error messages. <BR/>
     *
     * @param e            the TransformerException
     * @param msgType    the message type as string
     */
    private void addErrorMessage (TransformerException e, String msgType)
    {
        StringBuffer msg = new StringBuffer ().append (msgType)
            .append (": ")
            .append (e.getMessageAndLocation ());
        // any locator information available?
        if (e.getLocator () != null)
        {
            // TODO RB: Call  
            //          MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
            //              DITokens.ML_AT_LINE, env)
            //          and
            //          MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
            //              DITokens.ML_COLUMN, env)
            //          to get the text in the correct language
            msg.append (DITokens.ML_AT_LINE)
                .append (e.getLocator ().getLineNumber ())
                .append (DITokens.ML_COLUMN)
                .append (e.getLocator ().getColumnNumber ());
        } // if (e.getLocator () != null)

        msg.append ("\n");
        // collect the error messages
        this.errorMessage.append (msg);
    } // addErrorMessage


    /**************************************************************************
     * Add an exception to the the collected error messages. <BR/>
     *
     * @param e            the TransformerException
     * @param msgType    the message type as string
     */
    private void addErrorMessage (SAXParseException e, String msgType)
    {
        // TODO RB: Call  
        //          MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
        //              DITokens.ML_AT_LINE, env)
        //          and
        //          MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
        //              DITokens.ML_COLUMN, env)
        //          to get the text in the correct language
        StringBuffer msg = new StringBuffer ().append (msgType)
            .append (": ")
            .append (e.getMessage ())
            .append (DITokens.ML_AT_LINE)
            .append (e.getLineNumber ())
            .append (DITokens.ML_COLUMN)
            .append (e.getColumnNumber ())
            .append ("\n");
        // collect the error messages
        this.errorMessage.append (msg);
    } // addErrorMessage


    /**************************************************************************
     * Get the collected error messages. <BR/>
     *
     * @return    the collected error messages as string.
     */
    public String getErrorMessage ()
    {
        return this.errorMessage.toString ();
    } // getErrorMessage


} // class DIErrorListener

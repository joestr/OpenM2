/*
 * Class: TextElement.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.Font;
import ibs.tech.html.IE302;


/******************************************************************************
 * This is the abstract TextElement Object, which builds a HTML-String
 * needed for a Text to be displayed from the browser.
 *
 * @version     $Id: TextElement.java,v 1.7 2007/07/20 12:59:27 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class TextElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TextElement.java,v 1.7 2007/07/20 12:59:27 kreimueller Exp $";


    /**
     * Specifies the font to use to write the text.
     * default : none (null)
     */
    public Font font;

    /**
     * Specifies the text to be displayed.
     */
    public String text;


    /**************************************************************************
     * Create a new instance of a TextElement with a given String. <BR/>
     * Sets the font, Class, name, id  to null
     * and instantiates the variable text with the given
     * paramenters.
     *
     * @param pText          ....the String to display
     */
    public TextElement (String pText)
    {
        this.font = null;
        this.text = pText;
    } // TextElement


    /**************************************************************************
     * Create a new instance of a TextElement with a given String and a given font
     * Sets the font, Class, name, id  to null
     * and instantiates the variable text and font with the given
     * paramenters.
     *
     * @param pText           ....the String to display
     * @param pFont           ....the Font of the String
     */
    public TextElement (String pText, Font pFont)
    {
        this.font = pFont;
        this.text = pText;
    } // TextElement


    /**************************************************************************
     * Writes the element on the browser. <BR/>
     *
     * @param   env     OutputStream
     * @param   buf     Buffer in which to write the result.
     *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void build (Environment env, StringBuffer buf) throws BuildException
    {
        if (this.isBrowserSupported (env))
                                        // browser is supported?
        {
            if (this.font == null)
            {
                buf.append (this.text);
            } // if
            else
            {
                this.font.build (env, buf);
                if (this.font.bold)
                {
                    buf.append (IE302.TAG_BOLDBEGIN);
                } // if
                if (this.font.italic)
                {
                    buf.append (IE302.TAG_ITALICBEGIN);
                } // if
                if (this.font.underline)
                {
                    buf.append (IE302.TAG_UNDERBEGIN);
                } // if
                buf.append (this.text);
                if (this.font.underline)
                {
                    buf.append (IE302.TAG_UNDEREND);
                } // if
                if (this.font.italic)
                {
                    buf.append (IE302.TAG_ITALICEND);
                } // if
                if (this.font.bold)
                {
                    buf.append (IE302.TAG_BOLDEND);
                } // if
                buf.append (IE302.TAG_FONTEND);
            } // else
        } // if
    } // build

} // class TextElement

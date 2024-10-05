/*
 * Class: NewLineElement.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.IE302;


/******************************************************************************
 * This is the NewLineElement Object, which makes a new line in the
 * HTML-Page.
 *
 * @version     $Id: NewLineElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980314
 ******************************************************************************
 */
public class NewLineElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: NewLineElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $";


    /**************************************************************************
     * Constructs a NewLineElement. <BR/>
     */
    public NewLineElement ()
    {
        // nothing to do
    } // NewLineElement


    /**************************************************************************
     * Writes the element on the browser. <BR/>
     *
     * @param   env     OutputStream
     * @param   buf     Buffer where to write the output to.
     *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void build (Environment env, StringBuffer buf) throws BuildException
    {
        buf.append (IE302.TAG_NEWLINE + "\n");
    } // build

} // class NewLineElement

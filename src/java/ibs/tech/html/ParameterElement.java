/*
 * Class: ParameterElement.java
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
 * This is the abstract ParameterElement Object, which builds a HTML-String
 * needed for a Param-tag to be add to the page.
 *
 * @version     $Id: ParameterElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class ParameterElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ParameterElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $";


    /**
     * Specifies the value of the parameter
     */
    public String value;


    /**************************************************************************
     * Create a new instance of a ParamElement with a given name and a given
     * value. <BR/>
     *
     * @param   pName   Name of the Parameter
     * @param   pValue  Value of the Parameter
     */
    public ParameterElement (String pName, String pValue)
    {
        this.name = pName;
        this.value = pValue;
        this.id = null;
        this.classId = null;
    } // ParameterElement


    /**************************************************************************
     * Writes the element on the browser. <BR/>
     *
     * @param   env         OutputStream
     * @param   buf     Buffer where to write the output to.
      *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void build (Environment env, StringBuffer buf) throws BuildException
    {
        if (this.isBrowserSupported (env)) // browser is supported?
        {
            buf.append (IE302.TAG_PARAMBEGIN);
            if (this.name != null)
            {
                buf.append (IE302.TA_NAME + this.inBrackets (this.name));
            } // if
            if (this.value != null)
            {
                buf.append (IE302.TA_VALUE + this.inBrackets (this.value));
            } // if
            buf.append (IE302.TO_TAGEND);
        } // if
    } // build

} // class ParameterElement

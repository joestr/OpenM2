/*
 * Class: ScriptElement.java
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
 * This is the ScriptElement Object, which builds a HTML-String
 * needed for a Script.
 *
 * @version     $Id: ScriptElement.java,v 1.10 2009/07/24 08:13:06 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class ScriptElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ScriptElement.java,v 1.10 2009/07/24 08:13:06 kreimueller Exp $";


    /**
     * Scripting language: JavaScript. <BR/>
     */
    public static final String LANG_JAVASCRIPT = "JavaScript";

    /**
     * Scripting type: JavaScript. <BR/>
     */
    public static final String TYPE_JAVASCRIPT = "text/javascript";

    /**
     * Used programming Script;
     */
    public String language;

    /**
     * The type
     */
    public String type = null;

    /**
     * The url where script can be found.
     */
    public String src = null;

    /**
     * The statements
     */
    private StringBuffer p_script;


    /**************************************************************************
     * Creates a new instance of a RowElement with cols Elements in
     *
     * @param   language    Language of the script.
     */
    public ScriptElement (String language)
    {
        this.language = language;

        // set standard type for JavaScript element
        if (language.equalsIgnoreCase (ScriptElement.LANG_JAVASCRIPT))
        {
            this.type = ScriptElement.TYPE_JAVASCRIPT;
        } // if

        this.p_script = new StringBuffer ();
    } // ScriptElement


    /**************************************************************************
     * Creates a new instance of a RowElement with cols Elements in
     *
     * @param   language    Language of the script.
     * @param   type        The script type.
     */
    public ScriptElement (String language, String type)
    {
        this.language = language;
        this.type = type;
        this.p_script = new StringBuffer ();
    } // ScriptElement


    /**************************************************************************
     * Adds a statement
     *
     * @param   pScript     The statement to be added.
     *
     * @return  The ScriptElement itself for direct concatenation.
     */
    public ScriptElement addScript (String pScript)
    {
        this.p_script.append ("\n").append (pScript);
        return this;
    } // addScript


    /**************************************************************************
     * Adds a statement
     *
     * @param   pScript     The statement to be added.
     *
     * @return  The ScriptElement itself for direct concatenation.
     */
    public ScriptElement addScript (StringBuffer pScript)
    {
        this.p_script.append ("\n").append (pScript);
        return this;
    } // addScript


    /**************************************************************************
     * Clears the Script
     */
    public void clear ()
    {
        this.p_script = new StringBuffer ();
    } // clear


    /**************************************************************************
     * Writes the element on the browser. <BR/>
     *
     * @param   env     OutputStream
     * @param   buf     The buffer to write the text into.
     *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void build (Environment env, StringBuffer buf) throws BuildException
    {
        buf.append (IE302.TAG_SCRIPTBEGIN);
        buf.append (IE302.TA_LANGUAGE + this.inBrackets (this.language));
        if (this.src == null)
        {
            buf.append (IE302.TO_TAGEND);
            buf.append (this.p_script);
        } // if
        else
        {
            buf.append (IE302.TA_TYPE + this.inBrackets (this.type));
            buf.append (IE302.TA_SRC + this.inBrackets (this.src));
            buf.append (IE302.TO_TAGEND);
        } // else
        buf.append (IE302.TAG_SCRIPTEND + "\n");
    } // build

} // class ScriptElement

/*
 * Class: LogElement.java
 */

// package:
package ibs.di;

// imports:
import ibs.BaseObject;
//KR TODO: unsauber
import ibs.bo.OID;
import ibs.di.DIConstants;
import ibs.di.DITokens;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.tech.html.GroupElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.TextElement;


/******************************************************************************
 * The LogElement hold the information of an VALUES section from the
 * XML import file. <BR/>
 *
 * @version     $Id: LogElement.java,v 1.13 2013/01/17 15:21:29 btatzmann Exp $
 *
 * @author      Buchegger Bernd (BB), 990107
 ******************************************************************************
 */
public class LogElement extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: LogElement.java,v 1.13 2013/01/17 15:21:29 btatzmann Exp $";


    /**
     *  type attribute. <BR/>
     */
    public int type;

    /**
     *  profile attribute. <BR/>
     */
    public OID oid;

    /**
     *  profile attribute. <BR/>
     */
    public String name;

    /**
     *  profile attribute. <BR/>
     */
    public String text;


    /**************************************************************************
     * Creates an LogElement. <BR/>
     */
    public LogElement ()
    {
        // call constructor of super class ObjectReference:
        this.type = 0;
        this.oid = null;
        this.text = "";
    } // LogElement


    /**************************************************************************
     * Creates an LogElement. <BR/>
     *
     * @param type      type of LogElement
     * @param oid       oid of imported or exported object
     * @param text      text of log entry
     */
    public LogElement (int type, OID oid, String text)
    {
        this.type = type;
        this.oid = oid;
        this.text = text;
    } // LogElement


    /**************************************************************************
     * Prints a LogElement and create a group element. <BR/>
     *
     * @return  The group element with the log element contents.
     */
    public GroupElement print ()
    {
        GroupElement group = new GroupElement ();
        switch (this.type)
        {
            case DIConstants.LOG_ENTRY:
                break;
            case DIConstants.LOG_ERROR:
                // print error token, multilinguality is not required for log files
                group.addElement (new TextElement (DITokens.TOK_ERROR + ":"));
                break;
            case DIConstants.LOG_WARNING:
                // print warning token, multilinguality is not required for log files
                group.addElement (new TextElement (DITokens.TOK_WARNING + ": "));
                break;
            default:
                // nothing to do
        } // switch
        // print the text of the log entry
        if (this.oid != null)
        {
            TextElement text = new TextElement (this.text);
            group.addElement (new LinkElement (text,
                IOHelpers.getShowObjectJavaScriptUrl (this.oid.toString ())));
        } // if (this.oid != null)
        else
        {
            group.addElement (new TextElement (this.text));
        } // else
        return group;
    } // print


    /**************************************************************************
     * Get the string representation of a LogElement for ASCII output. <BR/>
     *
     * @return  The string representation of the log element.
     *
     * @see java.lang.Object#toString()
     */
    public String toString ()
    {
        String str = "";
        switch (this.type)
        {
            case DIConstants.LOG_ERROR:
                // print error token, multilinguality is not required for log files
                str += DITokens.TOK_ERROR + ": ";
                break;
            case DIConstants.LOG_WARNING:
                // print warning token, multilinguality is not required for log files
                str += DITokens.TOK_WARNING + ": ";
                break;
            default:
                str += "";
        } // switch
        str += this.text;
        return str;
    } // toString


    /**************************************************************************
     * Prints a LogElement for HTML output. <BR/>
     *
     * @return  The generated HTML string.
     *
     * @see java.lang.Object#toString()
     */
    public String toHTMLString ()
    {
        StringBuilder str = new StringBuilder ("");
        switch (this.type)
        {
            case DIConstants.LOG_ERROR:
                // print error token, multilinguality is not required for log files
                str.append ("<B>").append (DITokens.TOK_ERROR).append ("</B>: ");
                break;
            case DIConstants.LOG_WARNING:
                // print warning token, multilinguality is not required for log files
                str.append ("<B>").append (DITokens.TOK_WARNING).append ("</B>: ");
                break;
            default:
                str.append ("");
        } // switch
        str.append (this.text);
        return str.toString ();
    } // toHTMLString

} // class LogElement

/*
 * Class: BlankElement.java
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
 * This is the abstract BlankElement Object, which builds a HTML-String
 * needed for Blanks to be displayed from the browser.
 *
 * @version     $Id: BlankElement.java,v 1.9 2013/01/17 15:21:53 btatzmann Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class BlankElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BlankElement.java,v 1.9 2013/01/17 15:21:53 btatzmann Exp $";


    /**
     * Specifies how many blanks you want.
     * default : 1
     */
    public int blanks;


    /**************************************************************************
     * Create a new instance of a BlankElement with a given number of blanks
     * to display. <BR/>
     *
     * @param number          ....how many blanks are to be displayed
     */
    public BlankElement (int number)
    {
        this.blanks = number;
    } // BlankElement


    /**************************************************************************
     * Create a new instance of a BlankElement with 1 blank to display
     */
    public BlankElement ()
    {
        this.blanks = 1;
    } // BlankElement


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
        for (int i = 0; i < this.blanks; i++)
        {
            buf.append (IE302.TO_BLANK);
        } // for
    } // build

} // class BlankElement

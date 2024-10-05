/*
 * Class: AppletElement.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.IE302;
import ibs.tech.html.ParameterElement;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This is the abstract AppletElement Object, which builds a HTML-String
 * needed for a Applet to be loaded from the browser.
 *
 * @version     $Id: AppletElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980404
 ******************************************************************************
 */
public class AppletElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: AppletElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $";


    /**
     * Constant ELEMENTS_INITIAL of the Vector holding the
     * elements.
     * set to 20
     */
    protected static final int ELEMENTS_INITIAL = 20;

    /**
     * Constant ELEMENTS_INCREMENT of the Vector holding the
     * elements.
     * set to 5
     */
    protected static final int ELEMENTS_INCREMENT = 5;

    /**
     * Specifies the source where the applet lies (URL)
     */
    public String source;

    /**
     * Along with <A HREF="#width">width</A> specifies the size which has
     * the applet. <BR/>
     * default : -1 (which means no height is given)
     */
    public int height;

    /**
     * Along with <A HREF="#height">height</A> specifies the size which has
     * the applet. <BR/>
     * default : -1 (which means no wisth is given)
     */
    public int width;

    /**
     * Text to show if applet is not loadable.
     * default : none (null)
     */
    public String alt;

    /**
     * pixelwidth of the border drawn around the image.
     * default : 0
     */
    protected Vector<ParameterElement> parameters;



    /**************************************************************************
     * Create a new instance of a AppletElement with the class of the given url.
     * Sets the default-values for all variables but source.
     * See the variables to know their default-values.
     *
     * @param   pSrc    URL where the Image lies
     */
    public AppletElement (String pSrc)
    {
        this.source = pSrc;
        this.height = -1;
        this.width = -1;
        this.alt = null;
        this.name = null;
        this.id = null;
        this.classId = null;
        this.parameters = null;
    } // AppletElement


    /**************************************************************************
     * Adds a Parameter at the actual position
     *
     * @param   par     ?????
     */
    public void addElement (ParameterElement par)
    {
        if (this.parameters == null)
        {
            this.parameters = new Vector<ParameterElement> (
                AppletElement.ELEMENTS_INITIAL,
                AppletElement.ELEMENTS_INCREMENT);
        } // if
        this.parameters.addElement (par);
    } // addElement


    /**************************************************************************
     * Adds a Parameter at the given position
     *
     * @param   par     ?????
     * @param   order   ?????
     */
    public void addElement (ParameterElement par, int order)
    {
        if (this.parameters == null)
        {
            this.parameters = new Vector<ParameterElement> (
                AppletElement.ELEMENTS_INITIAL,
                AppletElement.ELEMENTS_INCREMENT);
        } // if
        try
        {
            this.parameters.insertElementAt (par, order);
        } // try
        catch (ArrayIndexOutOfBoundsException e)
        {
            this.parameters.setSize (order);
            this.parameters.insertElementAt (par, order);
        } // catch
    } // addElement


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
            buf.append (IE302.TAG_APPLETBEGIN);
            buf.append (IE302.TA_CODEBEGIN + this.inBrackets (this.source));
            if (this.name != null)
            {
                buf.append (IE302.TA_NAME + this.inBrackets (this.name));
            } // if
            if (this.title != null)
            {
                buf.append (IE302.TA_TITLE + this.inBrackets (this.title));
            } // if
            if (this.height != -1)
            {
                buf.append (IE302.TA_HEIGHT + this.inBrackets ("" + this.height));
            } // if
            if (this.width != -1)
            {
                buf.append (IE302.TA_WIDTH + this.inBrackets ("" + this.width));
            } // if
            if (this.alt != null)
            {
                buf.append (IE302.TA_ALT + this.inBrackets (this.alt));
            } // if
            buf.append (IE302.TO_TAGEND + "\n");

            if (this.parameters != null)     // there are some elements in the vector
            {
                for (Iterator<ParameterElement> iter = this.parameters.iterator (); iter.hasNext ();)
                {
                    Element e = iter.next ();

                    if (e != null)
                    {
                        e.build (env, buf);
                    } // if
                } // for iter
            } // if

            buf.append (IE302.TAG_APPLETEND + "\n");
        } // if
    } // build

} // class AppletElement

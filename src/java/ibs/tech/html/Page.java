/*
 * Class: Page.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.tech.html.BodyElement;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.HeadElement;
import ibs.tech.html.IE302;


/******************************************************************************
 * This is the Page Object, which builds a HTML-String
 * needed for a Page to be displayed
 *
 * @version     $Id: Page.java,v 1.12 2009/11/24 12:56:29 btatzmann Exp $
 *
 * @author      Christine Keim (CK), 980318
 ******************************************************************************
 */
public class Page extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Page.java,v 1.12 2009/11/24 12:56:29 btatzmann Exp $";


    /**
     * Head of the page. <BR/>
     */
    public HeadElement head;

    /**
     * Body of the page. <BR/>
     */
    public BodyElement body;

    /**
     * Page header already created? <BR/>
     */
    private boolean isPageHeaderCreated = false;

    /**
     * Is BodyElement in use? <BR/>
     */
    private boolean isPageBodyUsed = false;

    /**************************************************************************
     * Creates a new instance of a PageElement which is a . <BR/>
     * Creates a new BodyElement and a new HeadElement
     *
     * @param frameset  ....is the page a framesetspecification?
     */
    public Page (boolean frameset)
    {
        this.head = new HeadElement ();
        this.body = new BodyElement ();
        this.body.frameset = frameset;
    } // Page


    /**************************************************************************
     * Creates a new instance of a PageElement with the given title. <BR/>
     * Creates a new BodyElement (with the given title)
     * and a new HeadElement.
     *
     * @param title     ....the title of the page
     * @param frameset  ....is the page a framesetspecification?
     */
    public Page (String title, boolean frameset)
    {
        this.head = new HeadElement ();
        this.head.title = title;
        this.body = new BodyElement ();
        this.body.frameset = frameset;
    } // Page


    /**************************************************************************
     * Clears the Content of the page. <BR/>
     * Clears the head and the body of the page.
     */
    public void clear ()
    {
        this.head = new HeadElement ();
        this.body = new BodyElement ();
    } // clear


    /**************************************************************************
     * Writes the element on the browser. <BR/>
     *
     * @param   env         OutputStream
     *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void build (Environment env) throws BuildException
    {
        if (env != null)
        {
            // browser is supported?
            if (this.isBrowserSupported (env))
            {
                // Use body element or generate body tags?
                this.isPageBodyUsed = true;

                // build and write page
                this.buildHeader (env);
                this.buildBody (env);
                this.buildFooter (env);

                this.isPageBodyUsed = false;
            } // if
        } // if (env != null)
    } // build

    /**************************************************************************
     * Writes the header element on the browser. <BR/>
     *
     * @param   env         OutputStream
     *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void buildHeader (Environment env) throws BuildException
    {
        if (this.isBrowserSupported (env))
                                        // browser is supported?
        {
            if (!this.isPageHeaderCreated)
            {
                StringBuffer buf = new StringBuffer ();

                // create page header
                buf.append (IE302.DOCTYPE_HTML41_STRICT_MODE);
                buf.append (IE302.TAG_PAGEBEGIN + "\n");
                buf.append (IE302.META_TAG);

                this.head.build (env, buf);

                // uses Body element?
                if (!this.isPageBodyUsed)
                {
                    buf.append (IE302.TAG_BODYBEGIN);
                    buf.append (IE302.TO_TAGEND);
                } // if

                // write page header
                env.write (buf.toString ());

                // set flag
                this.isPageHeaderCreated = true;
            } // if
        } // if
    } // buildHeader

    /**************************************************************************
     * Writes the body element on the browser. <BR/>
     *
     * @param   env         OutputStream
     *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void buildBody (Environment env) throws BuildException
    {
        if (this.isBrowserSupported (env))
                                        // browser is supported?
        {
            StringBuffer buf = new StringBuffer ();

            // creates body
            this.body.build (env, buf);

            // write page body
            env.write (buf.toString ());
        } // if
    } // buildBody

    /**************************************************************************
     * Writes the footer element on the browser. <BR/>
     *
     * @param   env         OutputStream
     *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void buildFooter (Environment env) throws BuildException
    {
        if (this.isBrowserSupported (env))
        {
            StringBuffer buf = new StringBuffer ();

            // uses Body element?
            if (!this.isPageBodyUsed)
            {
                // close Body tag
                buf.append (IE302.TAG_BODYEND);
            } // if

            // creates footer
            buf.append (IE302.TAG_PAGEEND + "\n");

            // write page footer
            env.write (buf.toString ());

            // set flag
            this.isPageHeaderCreated = false;
        } // if
    } // buildFooter


    /**************************************************************************
     * Get <CODE>isPageHeaderCreated</CODE>. <BR/>
     *
     * @return  <CODE>true</CODE> if the page header was created,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean getPageHeaderCreated ()
    {
        return this.isPageHeaderCreated;
    } // setPageHeaderCreated
} // class Page

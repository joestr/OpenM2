/*
 * Class: ButtonElement.java
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
 * This is the Button, which displays a Button on the Browser. <BR/>
 *
 * @version     $Id: ButtonElement.java,v 1.11 2010/04/07 13:37:16 rburgermann Exp $
 *
 * @author      Christine Keim (CK), 980727
 ******************************************************************************
 */
public class ButtonElement extends Element implements Cloneable
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ButtonElement.java,v 1.11 2010/04/07 13:37:16 rburgermann Exp $";


    /**
     * text of the Button
     */
    public String text;

    /**
     * What ist displayed by mouseover
     */
    public String description;

    /**
     * What is done when clicking on the button
     */
    public String url;

    /**
     * Needed to activate/inactivate Button
     */
    public int buttonId;

    /**
     * Is the Button active? (which means visible)
     */
    public boolean active;

    /**
     * Image to display when active (visible)
     */
    public String activeImage;

    /**
     * Image to display when inactive (shaded/invisible)
     */
    public String inactiveImage;

    /**
     * Holds the rights required rights to display for the Object
     *
     */
    public int objectRights;

    /**
     * Holds the rights required rights to display for the Container
     *
     */
    public int containerRights;


    /**************************************************************************
     * Is id member of the given array?. <BR/>
     *
     * @param   id      The id to be checked.
     * @param   ids     The array in which to search.
     *
     * @return  <CODE>true</CODE> if the id was found,
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean member (int id, int[] ids)
    {
        if (ids == null)
        {
            return false;
        } // if

        for (int i = 0; i < ids.length; i++)
        {
            if (id == ids[i])
            {
                return true;
            } // if
        } // for i

        return false;
    } // member


    /**************************************************************************
     * Is text member of the given array?. <BR/>
     *
     * @param   text    The text to be checked.
     * @param   texts   The array in which to search.
     *
     * @return  <CODE>true</CODE> if the text was found,
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean member (String text, String[] texts)
    {
        if ((texts == null) || (text == null))
        {
            return false;
        } // if

        for (int i = 0; i < texts.length; i++)
        {
            if (text.equalsIgnoreCase (texts[i]))
            {
                return true;
            } // if
        } // for i

        return false;
    } // member


    /**************************************************************************
     * Creates a default Button. <BR/>
     *
     * @param   id          The id of the button.
     * @param   activeImg   The image of the button if it is active.
     */
    public ButtonElement (int id, String activeImg)
    {
        this.buttonId = id;
        this.text = null;
        this.active = true;
        this.activeImage = activeImg;
        this.inactiveImage = null;
        this.objectRights = -1;
        this.containerRights = -1;
    } // ButtonElement


    /**************************************************************************
     * Creates a Button. <BR/>
     *
     * @param   id          The id of the button.
     * @param   name        The name of the button.
     * @param   activeImg   The image of the button if it is active.
     */
    public ButtonElement (int id, String name, String activeImg)
    {
        this.buttonId = id;
        this.text = name;
        this.active = true;
        this.activeImage = activeImg;
        this.inactiveImage = null;
        this.objectRights = -1;
        this.containerRights = -1;
    } // ButtonElement


    /**************************************************************************
     * Creates a Button. <BR/>
     *
     * @param   id          The id of the button.
     * @param   name        The name of the button.
     * @param   activeImg   The image of the button if it is active.
     * @param   inactiveImg Image of the button if the button is inactive.
     * @param   alt         ALT-Text for the button.
     * @param   pUrl        URL to be called when clicking on the button.
     * @param   pObjectRights   Rights which the user needs on the object for
     *                          the button to be displayed.
     * @param   pContainerRights Rights which the user needs on the object's
     *                          container for the button to be displayed.
     * 
     * @deprecated      This method is deprecated because name, activeImg, 
     *                  inactiveImg and alt is not used on the server side 
     *                  anymore, because this parts are done on the client
     */
    @Deprecated
    public ButtonElement (int id, String name, String activeImg,
        String inactiveImg, String alt,
        String pUrl, int pObjectRights, int pContainerRights)
    {
        this.buttonId = id;
        this.text = name;
        this.active = true;
        this.description = alt;
        this.url = pUrl;
        this.inactiveImage = inactiveImg;
        this.activeImage = activeImg;
        this.inactiveImage = null;
        this.objectRights = pObjectRights;
        this.containerRights = pContainerRights;
    } // ButtonElement

    /**************************************************************************
     * Creates a Button. <BR/>
     *
     * @param   id          The id of the button.
     * @param   pUrl        URL to be called when clicking on the button.
     * @param   pObjectRights   Rights which the user needs on the object for
     *                          the button to be displayed.
     * @param   pContainerRights Rights which the user needs on the object's
     *                          container for the button to be displayed.
     */
    public ButtonElement (int id, String pUrl, int pObjectRights, int pContainerRights)
    {
        this.buttonId = id;
        this.text = null;
        this.active = true;
        this.description = null;
        this.url = pUrl;
        this.activeImage = null;
        this.inactiveImage = null;
        this.objectRights = pObjectRights;
        this.containerRights = pContainerRights;
    } // ButtonElement

    /**************************************************************************
     * Change the active status of the button.
     *
     * @param   ids     List of buttons, whose status shall be changed.
     */
    public void changeStatus (int[] ids)
    {
        this.active = this.member (this.buttonId, ids);
    } // changeStatus


    /**************************************************************************
     * Changes the state of this tab to active if the provided text equals to
     * the text of the tab, to inactive otherwise.
     *
     * @param   pText       Text to compare the tab's text to.
     */
    public void changeStatus (String[] pText)
    {
        this.active = this.member (this.text, pText);
    } // changeStatus


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
        if (this.isBrowserSupported (env)) // browser is supported?
        {
            buf.append (" ('");
            buf.append (this.text);
            buf.append (IE302.JS_OPENBETWEEN);
            if (this.active)
            {
                buf.append (this.activeImage);
            } // if
            else
            {
                buf.append (this.inactiveImage);
            } // else
            buf.append (IE302.JS_OPENBETWEEN);
            buf.append (this.description);
            buf.append (IE302.JS_OPENBETWEEN);
            buf.append (this.url);
            buf.append (IE302.JS_OPENEND);
        } // if
    } // build


    /**************************************************************************
     * Creates and returns a copy of this object. <BR/>
     * For any object <tt>x</tt>, the following expressions will be
     * <tt>true</tt>:
     * <blockquote><pre>
     * x.clone () != x
     * x.clone ().getClass () == x.getClass ()
     * x.clone ().equals (x)
     * </pre></blockquote>
     * The object returned by this method is independent of this object (which
     * is being cloned).
     *
     * @return  A clone of this instance. <BR/>
     *
     * @exception   CloneNotSupportedException
     *              if the object's class does not support the
     *              <code>Cloneable</code> interface. Subclasses that override
     *              the <code>clone</code> method can also throw this exception
     *              to indicate that an instance cannot be cloned.
     * @exception   OutOfMemoryError
     *              if there is not enough memory.
     *
     * @see java.lang.Cloneable
     */
    public Object clone ()  throws CloneNotSupportedException, OutOfMemoryError
    {
        ButtonElement obj = null;       // the new object

        // call corresponding method of super class:
        obj = (ButtonElement) super.clone ();

        // set specific properties:
        // because the clone method of {@link java.lang.Object Object}
        // performs a shallow and not a deep copy of all existing properties
        // we have to perform the deep copy here to ensure that there are
        // no side effects.

        // return the new object:
        return obj;
    } // clone

} // class ButtonElement

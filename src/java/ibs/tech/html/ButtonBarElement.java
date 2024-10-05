/*
 * Class: ButtonBarElement.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This is the Button List, which displays Buttons on the Browser. <BR/>
 *
 * @version     $Id: ButtonBarElement.java,v 1.12 2007/07/24 21:27:02 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980727
 ******************************************************************************
 */
public class ButtonBarElement extends Element implements Cloneable
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ButtonBarElement.java,v 1.12 2007/07/24 21:27:02 kreimueller Exp $";


    /**
     * Constant ELEMENTS_INITIAL of the Vector holding the
     * elements.
     * set to 20
     */
    protected static final int ELEMENTS_INITIAL = 5;

    /**
     * Constant ELEMENTS_INCREMENT of the Vector holding the
     * elements.
     * set to 5
     */
    protected static final int ELEMENTS_INCREMENT = 2;

    /**
     * Holds the Buttondata.
     *
     */
    protected Vector<ButtonElement> buttons;

    /**
     * Holds the actual rights on the Object
     *
     */
    public int objectRights;

    /**
     * Holds the actual rights on the Container
     *
     */
    public int containerRights;

    /**
     * Which function is called on each button
     */
    public String function;


    /**************************************************************************
     * Creates a default Reiterlist. <BR/>
     */
    public ButtonBarElement ()
    {
        this.buttons = null;
        this.objectRights = -1;
        this.containerRights = -1;
    } // ButtonBarElement


    /**************************************************************************
     * Clears the element. <BR/>
     * Sets the whole ButtonBar empty.
     */
    public void clear ()
    {
        this.buttons = null;
    } // clear


    /**************************************************************************
     * Adds a Button at a specific position. <BR/>
     * If this is the first button within the button list a new button list is
     * generated. <BR/>
     * Otherwise the button is inserted at the required position and all
     * buttons after that are moved one position backwards.
     *
     * @param   button  The button to be added.
     * @param   order   Position, at which the button shall be added.
     */
    public void addElement (ButtonElement button, int order)
    {

        if (this.buttons == null)
        {
            this.buttons = new Vector<ButtonElement> (
                ButtonBarElement.ELEMENTS_INITIAL,
                ButtonBarElement.ELEMENTS_INCREMENT);
        } // if
        try
        {
            this.buttons.insertElementAt (button, order);
        } // try
        catch (ArrayIndexOutOfBoundsException e)
        {
            this.buttons.setSize (order);
            this.buttons.insertElementAt (button, order);
        } // catch
    } // addElement


    /**************************************************************************
     * Adds a Button. <BR/>
     * If this is the first button within the button list a new button list is
     * generated. <BR/>
     * Otherwise the button is appended to the button list.
     *
     * @param   button  The button to be added.
     */
    public void addElement (ButtonElement button)
    {
        if (this.buttons == null)
        {
            this.buttons = new Vector<ButtonElement> (
                ButtonBarElement.ELEMENTS_INITIAL,
                ButtonBarElement.ELEMENTS_INCREMENT);
        } // if

        this.buttons.addElement (button);
    } // addElement


    /**************************************************************************
     * Sets buttons with id in the array active and all others inactive.
     *
     * @param   ids     List of button ids.
     */
    public void changeStatus (int[] ids)
    {
        if (this.buttons != null)             // there are some elements in the vector?
        {
            for (Iterator<ButtonElement> iter = this.buttons.iterator (); iter.hasNext ();)
            {
                ButtonElement e = iter.next ();

                if (e != null)
                {
                    e.changeStatus (ids);
                } // if
            } // for iter
        } // if there are some elements in the vector
    } // changeStatus


    /**************************************************************************
     * Sets buttons with name in the array active and all others inactive. <BR/>
     * The buttons to be set active are identified by theyr text which must be equal
     * to one member of the text array parameter of this method.
     *
     * @param   pText       Array of Texts to compare the tab's text to.
     */
    public void changeStatus (String[] pText)
    {
        if (this.buttons != null)       // there are some elements in the vector?
        {
            for (Iterator<ButtonElement> iter = this.buttons.iterator (); iter.hasNext ();)
            {
                ButtonElement e = iter.next ();

                if (e != null)
                {
                    e.changeStatus (pText);
                } // if
            } // for iter
        } // if there are some elements in the vector
    } // changeStatus


    /**************************************************************************
     * Gets the ButtonElement with the given id.
     *
     * @param   pId     id of the Button
     *
     * @return  The button.
     *          <CODE>null</CODE> if the button could not be found.
     */
    public ButtonElement getButton (int pId)
    {
        ButtonElement element = null;
        if (this.buttons != null)       // there are some elements in the vector?
        {
            for (Iterator<ButtonElement> iter = this.buttons.iterator (); iter.hasNext ();)
            {
                ButtonElement e = iter.next ();

                if (e != null && e.buttonId == pId)
                {
                    return e;
                } // if
            } // for iter
        } // if there are some elements in the vector
        return element;
    } // getButton


    /**************************************************************************
     * Gets the ButtonElement with the given tab text.
     *
     * @param   pText       Text to compare the tab's text to.
     *
     * @return  The button.
     *          <CODE>null</CODE> if the button could not be found.
     */
    public ButtonElement getButton (String pText)
    {
        if (this.buttons != null)       // there are some elements in the vector?
        {
            for (Iterator<ButtonElement> iter = this.buttons.iterator (); iter.hasNext ();)
            {
                ButtonElement e = iter.next ();

                if (e != null && e.text.equalsIgnoreCase (pText))
                {
                    return e;
                } // if
            } // for iter
        } // if there are some elements in the vector

        return null;
    } // getButton


    /**************************************************************************
     * Get the maximum button id. <BR/>
     * Loop through all buttons and get the highest id.
     *
     * @return  The maximum button id.
     */
    public int getMaxButtonId ()
    {
        int retVal = 0;                 // the return value

        if (this.buttons != null)
        {
            // loop through all buttons and get the highest id:
            for (Iterator<ButtonElement> iter = this.buttons.iterator (); iter.hasNext ();)
            {
                ButtonElement elem = iter.next ();
                if (elem.buttonId > retVal)
                {
                    retVal = elem.buttonId;
                } // if
            } // for iter
        } // if

        // return the result:
        return retVal;
    } // getMaxButtonId


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
            if (this.buttons != null)   // there are some elements in the vector?
            {
                buf.append (IE302.TAG_SCRIPTBEGIN);
                buf.append (IE302.TA_LANGUAGE + this.inBrackets (ScriptElement.LANG_JAVASCRIPT));
                buf.append (IE302.TO_TAGEND);

                for (Iterator<ButtonElement> iter = this.buttons.iterator (); iter.hasNext ();)
                {
                    ButtonElement e = iter.next ();

                    if (e != null)
                    {
                        if (e.active)
                        {
                            if (((this.objectRights & e.objectRights) == e.objectRights) &&
                                ((this.containerRights & e.containerRights) == e.containerRights))
                            {
                                buf.append (this.function);
                                e.build (env, buf);
                                buf.append ("\n");
                            } // if
                        } // if
                    } // if
                } // for iter

                buf.append (IE302.TAG_SCRIPTEND);
            } // if there are some elements in the vector
        } // if browser is supported
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
    @SuppressWarnings ("unchecked") // suppress compiler warning
    public Object clone ()  throws CloneNotSupportedException, OutOfMemoryError
    {
        ButtonBarElement obj = null;    // the new object

        // call corresponding method of super class:
        obj = (ButtonBarElement) super.clone ();

        // set specific properties:
        // because the clone method of {@link java.lang.Object Object}
        // performs a shallow and not a deep copy of all existing properties
        // we have to perform the deep copy here to ensure that there are
        // no side effects.
        obj.buttons = (Vector<ButtonElement>) this.buttons.clone ();
        for (int i = 0; i < obj.buttons.size (); i++)
        {
            obj.buttons.setElementAt ((ButtonElement)
                obj.buttons.elementAt (i).clone (), i);
        } // for i

        // return the new object:
        return obj;
    } // clone

} // class ButtonBarElement

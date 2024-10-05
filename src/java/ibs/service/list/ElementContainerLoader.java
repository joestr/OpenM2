/*
 * Class: ElementContainerLoader.java
 */

// package:
package ibs.service.list;

// imports:
import ibs.BaseObject;
import ibs.io.Environment;
import ibs.obj.ml.Locale_01;
import ibs.util.list.Element;
import ibs.util.list.ElementContainer;
import ibs.util.list.IElementContainer;
import ibs.util.list.ListException;

import java.util.Collection;


/******************************************************************************
 * This class is responsible for loading the data for a specific element
 * container out of the data store. <BR/>
 *
 * @version     $Id: ElementContainerLoader.java,v 1.18 2010/04/09 09:54:12 btatzmann Exp $
 *
 * @author      kreimueller, 011210
 *
 * @param   <EC>    The container for which this container loader is defined.
 *                  Must be a subclass of ElementContainer&lt;E>.
 * @param   <E>     The class for which this container loader is defined.
 *                  Must be a subclass of Element.
 ******************************************************************************
 */
public abstract class ElementContainerLoader<EC extends ElementContainer<E>, E extends Element>
    extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ElementContainerLoader.java,v 1.18 2010/04/09 09:54:12 btatzmann Exp $";


    /**
     * The container which holds the elements which where got out of the data
     * store. <BR/>
     * This property shall be used to store all elements gotten out of the
     * database. It shall be set first when starting the algorithm
     * (constructor).
     */
    private EC p_elems = null;


    /**
     * The class for the element container. <BR/>
     * The class must implement the interface
     * {@link IElementContainer IElementContainer}.
     */
    private Class<? extends EC> p_containerClass;


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates an ElementContainerLoader object. <BR/>
     *
     * @param   container   The ElementContainer in which to load the information.
     */
    public ElementContainerLoader (EC container)
    {
        // call constructor of super class:
        super ();

        // set the properties:
        this.p_elems = container;
        try
        {
            this.initContainerClass ();
        } // try
        catch (ListException e)
        {
            // nothing to do
        } // catch
    } // ElementContainerLoader


    ///////////////////////////////////////////////////////////////////////////
    // methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Initialize the container class. <BR/>
     * If there is already a container set, the class of this container is used
     * as actual class. <BR/>
     * This method can be overwritten in sub classes.
     *
     * @throws  ListException
     *          The class could not be initialized.
     *
     * @see #setContainerClass
     */
    @SuppressWarnings ("unchecked") // suppress compiler warning
    protected final void initContainerClass ()
        throws ListException
    {
        if (this.p_elems != null)
        {
            this.setContainerClass ((Class<? extends EC>) this.p_elems.getClass ());
        } // if
        else
        {
            this.setContainerClass ((Class<? extends EC>) ElementContainer.class);
        } // else
    } // initContainerClass


    /**************************************************************************
     * Set the class of an element container. <BR/>
     * The class must implement the interface
     * {@link IElementContainer IElementContainer}.
     *
     * @param   c       The class object.
     *
     * @throws  ListException
     *          The instance could not be created.
     *          Possible reasons: InstantiationException, IllegalAccessException
     *
     * @see #getContainerInstance
     */
    protected void setContainerClass (Class<? extends EC> c)
        throws ListException
    {
        Class<? extends EC> oldClass =
            this.p_containerClass; // the old class

        // set the new class:
        this.p_containerClass = c;

        try
        {
            // try to create a new instance:
            this.getContainerInstanceInternal ();
        } // try
        catch (ListException e)
        {
            // set the class to the old value:
            this.p_containerClass = oldClass;

            // throw the exception:
            throw e;
        } // catch

    } // setContainerClass


    /**************************************************************************
     * Get the class of an element container. <BR/>
     *
     * @return  The class object.
     */
    public Class<? extends EC> getContainerClass ()
    {
        return this.p_containerClass;
    } // getContainerClass


    /**************************************************************************
     * Get a new instance of an element container. <BR/>
     *
     * @return  The instance.
     */
    public EC getContainerInstance ()
    {
        try
        {
            return this.getContainerInstanceInternal ();
        } // try
        catch (ListException e)
        {
            // normally this should not be possible,
            // because correct instance creation was checked when setting
            // the class
            System.out.println (this.getClass ().getName () +
                ".getContainerInstance: " + e);
            return null;
        } // catch
    } // getContainerInstance


    /**************************************************************************
     * Get a new instance of an element container. <BR/>
     *
     * @return  The instance.
     *
     * @throws  ListException
     *          The instance could not be created.
     *          Possible reasons: InstantiationException, IllegalAccessException
     */
    private EC getContainerInstanceInternal ()
        throws ListException
    {
        String methodName = "GeneralException in Loader: ";

        try
        {
            return this.p_containerClass.newInstance ();
        } // try
        catch (InstantiationException e)
        {
            System.out.println (
                this.getClass ().getName () +
                "getContainerInstance: InstantiationException occurred : " +
                e.toString ());
            throw new ListException (this.getClass ().getName () +
                methodName, e);
        } // catch InstantiationException
        catch (IllegalAccessException e)
        {
            System.out.println (
                this.getClass ().getName () +
                "getContainerInstance: IllegalAccessException occurred : " +
                e.toString ());
            throw new ListException (this.getClass ().getName () +
                methodName, e);
        } // catch IllegalAccessException
    } // getContainerInstanceInternal


    /**************************************************************************
     * Get the ElementContainer. <BR/>
     * This container is used to store all information.
     *
     * @return  The ElementContainer or <CODE>null</CODE> if it is not defined.
     */
    public final EC getElems ()
    {
        // get the value and return it:
        return this.p_elems;
    } // getElems


    /**************************************************************************
     * Get the Container's content out of the data store. <BR/>
     * <BR/>
     *
     * @param   append  Shall the data be appended to the existing data
     *                  (<CODE>true</CODE>) or shall the existing data be
     *                  replaced?
     * @param   checkConstraints    Shall the constraints be checked, too?
     *
     * @throws  ListException
     *          An error occurred in a list operation.
     */
    public final void load (boolean append, boolean checkConstraints)
        throws ListException
    {
        EC newElems;

        // check if the new tuples shall be appended:
        if (!append)                    // the list shall be initialized?
        {
            this.p_elems.clear ();
        } // if the list shall be initialized

        // get the new elements and add them to the existing ones:
        newElems = this.loadElements ();
        if (newElems != this.p_elems)
        {
            this.p_elems.addAll (newElems);
        } // if

        // if necessary check the constraints:
        if (checkConstraints)
        {
            // check the constraints:
            this.p_elems.checkConstraints ();
        } // if

        // perform post processing for the whole container:
        this.postProcess (this.p_elems);
    } // load
    
    
    /***************************************************************************
     * Loads the multilang info for all provided locales and for all
     * elements.<BR/>
     *
     * @param locales   The locales to init the multilang info for
     * @param env       The environment
     */
    public final void loadMultilangInfo (Collection<Locale_01> locales, Environment env)
        throws ListException
    {
        if (this.p_elems == null)
        {
            // fill the object cache with all the known types and their
            // versions with classNames:
            this.load (false, true);
        } // if
        
        // load the multilingual values
        this.performLoadMultilangInfo (this.p_elems, locales, env);
    } // loadMultilangInfo


    /**************************************************************************
     * Load the elements for the container. <BR/>
     * This method must be overwritten in subclasses. <BR/>
     *
     * @return  The elements.
     *          If there are no elements the return value must be an empty
     *          ElementContainer. <CODE>null</CODE> is not allowed.
     *
     * @throws  ListException
     *          An error occurred in a list operation.
     */
    protected abstract EC loadElements ()
        throws ListException;


    /**************************************************************************
     * Perform post processing for the container. <BR/>
     * This method is called after loading the several elements. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     *
     * @param   elems   The container.
     *
     * @throws  ListException
     *          An error occurred in a list operation.
     */
    protected void postProcess (EC elems)
        throws ListException
    {
        // nothing to do
    } // postProcess
    
    
    /***************************************************************************
     * Loads the multilang info for all provided elements and provided
     * locales. <BR/>
     *
     * @param elems     The elements
     * @param locales   The locales to init the multilang info for
     * @param env       The environment
     */
    protected void performLoadMultilangInfo (EC elems,
            Collection<Locale_01> locales, Environment env)
    {
        // nothing to do
    } // performLoadMultilangInfo

} // class ElementContainerLoader

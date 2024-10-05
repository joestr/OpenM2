/*
 * Class: ElementContainer.java
 */
/*
 * Created by IntelliJ IDEA.
 * User: kreimueller
 * Date: Nov 22, 2001
 * Time: 3:23:07 PM
 */

// package:
package ibs.util.list;

// imports:
//KR TODO: unsauber
import ibs.BaseObject;
import ibs.util.list.Element;
import ibs.util.list.ElementId;
import ibs.util.list.IElementId;
import ibs.util.list.ListException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This class contains all data regarding a set of elements. <BR/>
 *
 * @version     $Id: ElementContainer.java,v 1.17 2007/07/24 21:39:58 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 011122
 *
 * @param   <E>     The class for which this container is defined.
 *                  Must be a subclass of Element.
 ******************************************************************************
 */
public class ElementContainer<E extends Element> extends BaseObject
    implements IElementContainer<E>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ElementContainer.java,v 1.17 2007/07/24 21:39:58 kreimueller Exp $";


    /**
     * The elements of the container. <BR/>
     * Each of these elements must implement interface
     * {@link ibs.util.list.IElement IElement} or a sub interface of this one.
     *
     * @see ibs.util.list.IElement
     */
    protected Vector<E> p_elems = null;


    /**
     * The element class of the container. <BR/>
     * The class must implement the interface {@link IElement IElement}.
     */
    private Class<E> p_elementClass = null;

    /**
     * The constructor for an element. <BR/>
     */
    private Constructor<E> p_elementConstr = null;

    /**
     * The parameters for a constructor of an element of the container. <BR/>
     */
    private static final Class<?> [] PARAMETER_TYPES =
    {
        IElementId.class,
        String.class,
    }; // PARAMETER_TYPES

    /**
     * Description used for handling exceptions. <BR/>
     */
    private static final String ERRORDESC = "getElementInstance";



    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates an ElementContainer object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     * The private property p_elems is initialized to a new vector. <BR/>
     *
     * @throws  ListException
     *          An error occurred during initializing the container.
     */
    public ElementContainer ()
        throws ListException
    {
        // call constructor of super class:
        super ();

        // initialize the other instance properties:
        this.p_elems = new Vector<E> (10, 10);

        this.initElementClass ();
    } // ElementContainer



    ///////////////////////////////////////////////////////////////////////////
    // methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Initialize the element class. <BR/>
     * This method can be overwritten in sub classes.
     *
     * @throws  ListException
     *          The class could not be initialized.
     *
     * @see #setElementClass
     */
    @SuppressWarnings ("unchecked") // suppress compiler warning
    protected void initElementClass ()
        throws ListException
    {
        this.setElementClass ((Class<E>) Element.class);
    } // initElementClass


    /**************************************************************************
     * Set the class of an element of the container. <BR/>
     * The class must implement the interface {@link IElement IElement}.
     * It must also implement a constructor <CODE> (int id, String name)</CODE>.
     * <BR/>
     * This method tries to create a new instance of the class. If this is not
     * o.k. a ListException is thrown.
     *
     * @param   cls     The class object.
     *
     * @throws  ListException
     *          The instance could not be created.
     *          Possible reasons: NoSuchMethodException, LinkageError,
     *          InvocationTargetException, InstantiationException,
     *          IllegalAccessException, SecurityException, NullPointerException
     *
     * @see #getElementInstanceInternal
     */
    public void setElementClass (Class<E> cls)
        throws ListException
    {
        // try to create a new instance:
        this.p_elementConstr = this.getConstructor (cls);

        // at this point we know that there was no error
        // (otherwise an exception would have been thrown)

        // set the new class:
        this.p_elementClass = cls;
    } // setElementClass


    /**************************************************************************
     * Get the class of an element of the container. <BR/>
     *
     * @return  The class object.
     */
    public Class<E> getElementClass ()
    {
        return this.p_elementClass;
    } // getElementClass


    /**************************************************************************
     * Check if the class of an element is valid. <BR/>
     * This method proofs if the class exists and has a constructor with the
     * parameters <CODE> (IElementId, String)</CODE>.
     *
     * @param   cls         The class of the element.
     *
     * @return  <CODE>true</CODE> if the class is valid,
     *          <CODE>false</CODE> otherwise.
     *
     * @throws  ListException
     *          The class is not valid.
     *          Possible reasons: NoSuchMethodException, LinkageError,
     *          InvocationTargetException, InstantiationException,
     *          IllegalAccessException, SecurityException, NullPointerException.
     */
    private Constructor<E> getConstructor (Class<E> cls)
        throws ListException
    {
        Constructor<E> constr = null; // the constructor

        try
        {
            // get the constructor:
//System.out.println ("class = " + cls);
            constr = cls.getConstructor (ElementContainer.PARAMETER_TYPES);
//System.out.println ("constr = " + constr);
            // return the result:
            return constr;
        } // try
        catch (NoSuchMethodException e)
        {
            throw this.handleException (e, ElementContainer.ERRORDESC);
        } // catch
        catch (LinkageError e)
        {
            throw this.handleException (e, ElementContainer.ERRORDESC);
        } // catch
        catch (SecurityException e)
        {
            throw this.handleException (e, ElementContainer.ERRORDESC);
        } // catch
        catch (NullPointerException e)
        {
            throw this.handleException (e, ElementContainer.ERRORDESC);
        } // catch
    } // getConstructor


    /**************************************************************************
     * Get a new instance of an element container. <BR/>
     *
     * @param   className   Name of the class for which to get an instance.
     * @param   id          Id of the element.
     * @param   name        The element's name.
     *
     * @return  The instance.
     *
     * @throws  ListException
     *          The instance could not be created.
     *          Possible reasons: NoSuchMethodException, LinkageError,
     *          InvocationTargetException, InstantiationException,
     *          IllegalAccessException, SecurityException, NullPointerException,
     *          ClassNotFoundException.
     */
    public E getElementInstance (String className, int id, String name)
        throws ListException
    {
        try
        {
            // get the class object:
            @SuppressWarnings("unchecked") // suppress compiler warning
            Class<E> cls = (Class<E>) Class.forName (className);
            // get the instance:
            return this.getElementInstanceInternal (this.getConstructor (cls),
                new ElementId (id), name);
        } // try
        catch (ClassNotFoundException e)
        {
            throw this.handleException (e, ElementContainer.ERRORDESC);
        } // catch
    } // getElementInstance


    /**************************************************************************
     * Get a new instance of an element container. <BR/>
     *
     * @param   id          Id of the element.
     * @param   name        The element's name.
     *
     * @return  The instance.
     *
     * @throws  ListException
     *          The instance could not be created.
     *          Possible reasons: An error during calling the constructor.
     */
    public E getElementInstance (int id, String name)
        throws ListException
    {
        // normally an exception should not be possible,
        // because correct instance creation was checked when setting
        // the class
        return this.getElementInstanceInternal (this.p_elementConstr, new ElementId (id), name);
    } // getElementInstance


    /**************************************************************************
     * Get a new instance of an element container. <BR/>
     *
     * @param   id          Id of the element.
     * @param   name        The element's name.
     *
     * @return  The instance.
     *
     * @throws  ListException
     *          The instance could not be created.
     *          Possible reasons: An error during calling the constructor.
     */
    public E getElementInstance (IElementId id, String name)
        throws ListException
    {
        // normally an exception should not be possible,
        // because correct instance creation was checked when setting
        // the class
        return this.getElementInstanceInternal (this.p_elementConstr, id, name);
    } // getElementInstance


    /**************************************************************************
     * Get a new instance of an element. <BR/>
     *
     * @param   constr  The constructor for the element.
     * @param   id      Id object of the element.
     * @param   name    The element's name.
     *
     * @return  The instance.
     *
     * @throws  ListException
     *          The instance could not be created.
     *          Possible reasons: NoSuchMethodException, LinkageError,
     *          InvocationTargetException, InstantiationException,
     *          IllegalAccessException, SecurityException, NullPointerException.
     */
    protected E getElementInstanceInternal (Constructor<E> constr,
                                            IElementId id, String name)
        throws ListException
    {
        try
        {
            // set the arguments:
            Object[] initArgs =
            {
                id,
                name,
            }; // initArgs

            // create the element instance and return it:
            return constr.newInstance (initArgs);
        } // try
        catch (LinkageError e)
        {
            throw this.handleException (e, ElementContainer.ERRORDESC);
        } // catch
        catch (InvocationTargetException e)
        {
            this.handleException (e, ElementContainer.ERRORDESC);
            throw this.handleException (e, ElementContainer.ERRORDESC);
        } // catch
        catch (InstantiationException e)
        {
            throw this.handleException (e, ElementContainer.ERRORDESC);
        } // catch InstantiationException
        catch (IllegalAccessException e)
        {
            throw this.handleException (e, ElementContainer.ERRORDESC);
        } // catch IllegalAccessException
        catch (SecurityException e)
        {
            throw this.handleException (e, ElementContainer.ERRORDESC);
        } // catch
        catch (NullPointerException e)
        {
            throw this.handleException (e, ElementContainer.ERRORDESC);
        } // catch
    } // getElementInstanceInternal


    /**************************************************************************
     * Check if the container is empty. <BR/>
     *
     * @return  <CODE>true</CODE> if the container is empty,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isEmpty ()
    {
        return this.p_elems.isEmpty ();
    } // isEmpty


    /**************************************************************************
     * Add a new element to the container. <BR/>
     * If there is already an element with the same id that element is replaced.
     * If the element id is <CODE>-1</CODE> this check is not done.
     *
     * @param   elem    The element to be added.
     *
     * @return  <CODE>true</CODE> if the element was successfully added,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean add (E elem)
    {
        int pos = 0;                    // the current position of the element

        // check if the element is valid:
        if (elem != null)               // the element is valid?
        {
            // check if the element already exists within the container:
            if (elem.getIdInt () == -1 ||
                (pos = this.p_elems.indexOf (elem)) == -1)
                                            // the element does not exist yet?
            {
                // add the element to the vector:
                this.p_elems.addElement (elem);
            } // if the element does not exist yet
            else                            // the element already exists
            {
                // replace the old version of the element with the new one:
                this.p_elems.setElementAt (elem, pos);
            } // else the element already exists

            return true;
        } // if the element is valid

        // nothing done, return corresponding result:
        return false;
    } // add


    /**************************************************************************
     * Add the elements of another collection to this container. <BR/>
     * If the collection is an instance of IElementContainer the method
     * {@link #addAll (Collection) contains} is called.
     * Otherwise this method returns a ClassCastException.
     *
     * @param   coll    The collection to be added to the container.
     *
     * @return  <CODE>true</CODE> if the container changed as a result of the
     *          call,
     *          <CODE>false</CODE> otherwise.
     *
     * @throws  ClassCastException
     *          If the specified element is not of type IElement.
     */
    public boolean addAll (Collection<? extends E> coll)
        throws ClassCastException
    {
        if (coll instanceof IElementContainer)
        {
            return this.addAll ((IElementContainer<? extends E>) coll);
        } // if

        throw this.getClassCastException ("addAll");
    } // addAll


    /**************************************************************************
     * Add the elements of another container to this container. <BR/>
     *
     * @param   elemContainer   The container which contains the elements to be
     *                          added.
     *
     * @return  <CODE>true</CODE> if all elements were successfully added,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean addAll (IElementContainer<? extends E> elemContainer)
    {
        boolean retVal = false;         // the return value

        // loop through all elements of the container:
        for (Iterator<? extends E> iter = elemContainer.iterator (); iter.hasNext ();)
        {
            // get the actual element and add it to the container:
            this.add (iter.next ());

            retVal = true;
        } // for iter

        // return the result:
        return retVal;
    } // addAll


    /**************************************************************************
     * Move a specific element from this container to another one. <BR/>
     * The element is removed from the current container and added to the other
     * one.
     *
     * @param   elem        The element to be moved.
     * @param   container   The target container for the element.
     *
     * @throws  ListException
     *          An error occurred in a list operation.
     */
    public synchronized void move (E elem, IElementContainer<E> container)
        throws ListException
    {
        this.remove (elem);
        container.add (elem);
    } // move


    /**************************************************************************
     * Remove a specific element defined through its id. <BR/>
     *
     * @param   id      The id of the element to be removed.
     *
     * @return  The removed element or <CODE>null</CODE> if no element was
     *          removed.
     *
     * @throws  ListException
     *          An error occurred in a list operation.
     */
    public E remove (IElementId id)
        throws ListException
    {
        E dummy = null;          // a dummy element
        E elem = null;           // the removed element
        int pos = 0;                    // position of the element

        // create the element:
        dummy = this.getElementInstance (id, null);

        // search for the element:
        if ((pos = this.p_elems.indexOf (dummy)) != -1) // required element found?
        {
            // remove the found element:
/* java 1.3.1
            elem = (E) this.p_elems.remove (pos);
*/
            this.p_elems.removeElementAt (pos);
        } // if required element found

        // return the removed element:
        return elem;
    } // remove


    /**************************************************************************
     * Remove a specific element defined through its name. <BR/>
     *
     * @param   name    The name of the element to be removed.
     *
     * @return  The removed element or <CODE>null</CODE> if no element was
     *          removed.
     */
    public E remove (String name)
    {
        E elem = null;           // the found element
        int pos = 0;                    // position of the element
        boolean found = false;          // was there an element found?

        // loop through the elements and search for the elements with the
        // correct name:
        for (pos = 0; !found && pos < this.p_elems.size (); pos++)
        {
            // get the element out of the vector:
            elem = this.p_elems.elementAt (pos);

            // check if the name is the one we are searching for:
            // compare the name.
            found = elem.getName ().equals (name);
        } // for

        if (found)                      // the element was found?
        {
            // remove the found element:
/* java 1.3.1
            elem = (E) this.p_elems.remove (pos);
*/
            this.p_elems.removeElementAt (--pos);
        } // if the element was found
        else                            // the element was not found
        {
            elem = null;                // no element found
        } // else the element was not found

        // return the removed element:
        return elem;
    } // remove


    /**************************************************************************
     * Remove an element from the container. <BR/>
     *
     * @param   elem    The element to be removed.
     *
     * @return  <CODE>true</CODE> if the container changed as a result of the
     *          call,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean remove (E elem)
    {
        // check if the element exists:
        if (this.p_elems.indexOf (elem) != -1)
                                        // the element exists?
        {
            // remove the element from the vector:
/* java 1.3.1
            this.p_elems.remove (elem);
*/
            this.p_elems.removeElement (elem);
            return true;
        } // if the element exists

        // the element does not exist
        // nothing to do
        return false;
    } // remove


    /**************************************************************************
     * Check if a specific element is contained within the container. <BR/>
     * If the object is an instance of IElement the method
     * {@link #remove (E) contains} is called.
     * Otherwise this method returns a ClassCastException.
     *
     * @param   obj     The element to be searched for.
     *
     * @return  <CODE>true</CODE> if the container changed as a result of the
     *          call,
     *          <CODE>false</CODE> otherwise.
     *
     * @throws  ClassCastException
     *          If the specified element is not of type E.
     */
    public boolean remove (Object obj)
        throws ClassCastException
    {
        // object cannot be converted to E, raise exception:
        throw this.getClassCastException ("remove");
    } // remove


    /**************************************************************************
     * Removes all of this collection's elements that are also contained in the
     * specified collection (optional operation).  After this call returns,
     * this collection will contain no elements in common with the specified
     * collection.
     *
     * @param c collection containing elements to be removed from this collection
     * @return <tt>true</tt> if this collection changed as a result of the
     *         call
     * @throws UnsupportedOperationException if the <tt>removeAll</tt> method
     *         is not supported by this collection
     * @throws ClassCastException if the types of one or more elements
     *         in this collection are incompatible with the specified
     *         collection (optional)
     * @throws NullPointerException if this collection contains one or more
     *         null elements and the specified collection does not support
     *         null elements (optional), or if the specified collection is null
     * @see #remove (Object)
     * @see #contains (Object)
     */
    public boolean removeAll (Collection<?> c)
        throws UnsupportedOperationException, ClassCastException,
        NullPointerException
    {
        return this.p_elems.removeAll (c);
    } // removeAll


    /**************************************************************************
     * Get the number of elements within this container. <BR/>
     *
     * @return  The number of elements within this container.
     */
    public final int size ()
    {
        // return the number of elements within the element vector:
        return this.p_elems.size ();
    } // size


    /**************************************************************************
     * Check if a specific element is contained within the container. <BR/>
     * The comparison is done through the equals method of the object, but
     * the object must also be the same java instance.
     *
     * @param   searchElem  The element to be searched for.
     *
     * @return  <CODE>true</CODE> if the element is contained,
     *          <CODE>false</CODE> otherwise.
     */
    public final boolean contains (E searchElem)
    {
        E elem = null;            // the found element
        int pos = 0;                    // position of the element

        // search for the element:
        if ((pos = this.p_elems.indexOf (searchElem)) != -1)
                                        // required element found?
        {
            // get the element out of the vector:
            elem = this.p_elems.elementAt (pos);
        } // if required element found

        // return if the element is the same:
        return elem == searchElem;
    } // contains


    /**************************************************************************
     * Check if a specific element is contained within the container. <BR/>
     * If the object is an instance of IElement the method
     * {@link #contains (E) contains} is called.
     * Otherwise this method returns a ClassCastException.
     *
     * @param   obj     The element to be searched for.
     *
     * @return  <CODE>true</CODE> if the element is contained,
     *          <CODE>false</CODE> otherwise.
     *
     * @throws  ClassCastException
     *          If the specified element is not of type E.
     */
    public final boolean contains (Object obj)
        throws ClassCastException
    {
        // object cannot be converted to E, raise exception:
        throw this.getClassCastException ("contains");
    } // contains


    /**************************************************************************
     * Check if the equal for a specific element is contained within the
     * container. <BR/>
     * The comparison is done through the equals method of the object.
     *
     * @param   searchElem  The element to be searched for.
     *
     * @return  <CODE>true</CODE> if the element is contained,
     *          <CODE>false</CODE> otherwise.
     */
    public final boolean containsEqual (E searchElem)
    {
        // search for the element and return the result:
        return this.p_elems.indexOf (searchElem) != -1;
    } // containsEqual


    /**************************************************************************
     * Returns <tt>true</tt> if this collection contains all of the elements
     * in the specified collection.
     *
     * @param  c collection to be checked for containment in this collection
     * @return <tt>true</tt> if this collection contains all of the elements
     *         in the specified collection
     * @throws ClassCastException if the types of one or more elements
     *         in the specified collection are incompatible with this
     *         collection (optional)
     * @throws NullPointerException if the specified collection contains one
     *         or more null elements and this collection does not permit null
     *         elements (optional), or if the specified collection is null
     * @see    #contains (Object)
     */
    public boolean containsAll (Collection<?> c)
        throws ClassCastException, NullPointerException
    {
        return this.p_elems.containsAll (c);
    } // containsAll


    /**************************************************************************
     * Get a specific element out of the list. <BR/>
     *
     * @param   id      The (unique) id of the element.
     *
     * @return  The found element or <CODE>null</CODE> if it was not found.
     */
    public final E get (int id)
    {
        // get the element and return it:
        return this.get (new ElementId (id));
    } // get


    /**************************************************************************
     * Get a specific element out of the list. <BR/>
     *
     * @param   id      The (unique) id of the element.
     *
     * @return  The found element or <CODE>null</CODE> if it was not found.
     *
     * @throws  ListException
     *          Error when initializing the element.
     *          Possible causes: invalid id.
     */
    public final E get (String id)
        throws ListException
    {
        // get the element and return it:
        return this.get (new ElementId (id));
    } // get


    /**************************************************************************
     * Get a specific element out of the list. <BR/>
     *
     * @param   id      The (unique) id of the element.
     *
     * @return  The found element or <CODE>null</CODE> if it was not found.
     */
    public final E get (IElementId id)
    {
        E dummy = null;          // a dummy element
        E elem = null;           // the found element
        int pos = 0;                    // position of the element

        try
        {
            dummy = this.getDummy (id);

            if ((pos = this.p_elems.indexOf (dummy)) != -1) // required element found?
            {
                // get the element out of the vector:
                elem = this.p_elems.elementAt (pos);
            } // if required element found
        } // try
        catch (ListException e)
        {
//System.out.println ("error in getDummy: " + e);
            // nothing to do
        } // catch

        // return the computed element:
        return elem;
    } // get


    /**************************************************************************
     * Create a dummy element out of the id. <BR/>
     *
     * @param   id      The id from which to create the dummy object.
     *
     * @return  The dummy object.
     *
     * @throws  ListException
     *          An error occurred in a list operation.
     */
    protected E getDummy (IElementId id)
        throws ListException
    {
        // create the element and return it.
        return this.getElementInstance (id, null);
    } // getDummy


    /**************************************************************************
     * Derive a element from the name. <BR/>
     *
     * @param   name        The (unique) name.
     *
     * @return  The found element or <CODE>null</CODE> if it was not found.
     */
    public E find (String name)
    {
        E elem = null;           // the found element
        int pos = 0;                    // position of the element
        boolean found = false;          // was there an element found?

        // loop through the elements and search for the elements with the
        // correct name:
        for (pos = 0; !found && pos < this.p_elems.size (); pos++)
        {
            // get the element out of the vector:
            elem = this.p_elems.elementAt (pos);

            // check if the name is the one we are searching for:
            // compare the name.
            found = elem.getName ().equals (name);
        } // for

        if (found)                      // the element was found?
        {
            return elem;                // return the element
        } // if the element was found

        // the element was not found
        return null;                // return the error code
    } // find


    /**************************************************************************
     * Sort the elements of the container. <BR/>
     * The sorting order is defined through the behaviour of the container. <BR/>
     * This class implements the standard ordering which is by id ascending.
     *
     * @throws  ListException
     *          There occurred an error during sorting.
     */
    public void sort ()
        throws ListException
    {
        E actElem = null;               // the actual element
        E compElem = null;              // the element to be compared
        int elemCount = 0;              // number of elements to be sorted
        int pos = 0;                    // position of the element

        for (elemCount = this.p_elems.size (); elemCount > 0; elemCount--)
        {
            actElem = this.p_elems.elementAt (0);

            // loop through the elements and sort them:
            for (pos = 1; pos < elemCount; pos++)
            {
                // get the element out of the vector:
                compElem = this.p_elems.elementAt (pos);

                // check if the element is larger than the actual element:
                if (actElem.getIdInt () > compElem.getIdInt ())
                {
                    // exchange the elements:
                    this.p_elems.set (pos - 1, compElem);
                    this.p_elems.set (pos, actElem);
                } // if
                else
                {
                    // make the new element the actual element:
                    actElem = compElem;
                } // else
            } // for pos
        } // for elemCount
    } // sort


    /**************************************************************************
     * Check constraints for the elements within the container. <BR/>
     *
     * @throws  ListException
     *          Error when checking the constraints.
     */
    public void checkConstraints ()
        throws ListException
    {
        // nothing to do
    } // checkConstraints


    /**************************************************************************
     * Ensure that there are no elements within the element container. <BR/>
     */
    public void clear ()
    {
        // clear the vector:
        // (Remark: beginning with JDK 1.2 it is possible to use the method
        // clear () of class Vector)
        this.p_elems.removeAllElements ();
    } // clear


    /**************************************************************************
     * Returns an enumeration of the components of this container. <BR/>
     * The returned {@link java.util.Enumeration Enumeration} object will
     * generate all items in this container. The first item generated is the
     * item at index 0, then the item at index 1, and so on.
     *
     * @return  An enumeration of the components in this container.
     *
     * @see java.util.Vector#elements
     */
    public final Enumeration<E> elements ()
    {
        // get the enumeration of the vector and return it:
        return this.p_elems.elements ();
    } // elements


    /**************************************************************************
     * Returns an enumeration of the components of this container. <BR/>
     * The returned {@link java.util.Enumeration Enumeration} object will
     * generate all items in this container. The first item generated is the
     * item at index 0, then the item at index 1, and so on.
     *
     * @return  An enumeration of the components in this container.
     *
     * @see java.util.Vector#elements
     */
    public final Iterator<E> iterator ()
    {
        // get the enumeration of the vector and return it:
        return this.p_elems.iterator ();
    } // getIterator


    /**************************************************************************
     * Retains only the elements in this collection that are contained in the
     * specified collection (optional operation).  In other words, removes from
     * this collection all of its elements that are not contained in the
     * specified collection.
     *
     * @param c collection containing elements to be retained in this collection
     * @return <tt>true</tt> if this collection changed as a result of the call
     * @throws UnsupportedOperationException if the <tt>retainAll</tt> operation
     *         is not supported by this collection
     * @throws ClassCastException if the types of one or more elements
     *         in this collection are incompatible with the specified
     *         collection (optional)
     * @throws NullPointerException if this collection contains one or more
     *         null elements and the specified collection does not permit null
     *         elements (optional), or if the specified collection is null
     * @see #remove (Object)
     * @see #contains (Object)
     */
    public boolean retainAll (Collection<?> c)
        throws UnsupportedOperationException, ClassCastException,
        NullPointerException
    {
        return this.p_elems.retainAll (c);
    } // retainAll


    /**************************************************************************
     * Returns an array containing all of the elements in this collection.
     * If this collection makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order.
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this collection.  (In other words, this method must
     * allocate a new array even if this collection is backed by an array).
     * The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this collection
     */
    public Object[] toArray ()
    {
        return this.p_elems.toArray ();
    } // toArray


    /**************************************************************************
     * Returns an array containing all of the elements in this collection;
     * the runtime type of the returned array is that of the specified array.
     * If the collection fits in the specified array, it is returned therein.
     * Otherwise, a new array is allocated with the runtime type of the
     * specified array and the size of this collection.
     *
     * <p>If this collection fits in the specified array with room to spare
     * (i.e., the array has more elements than this collection), the element
     * in the array immediately following the end of the collection is set to
     * <tt>null</tt>.  (This is useful in determining the length of this
     * collection <i>only</i> if the caller knows that this collection does
     * not contain any <tt>null</tt> elements.)
     *
     * <p>If this collection makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order.
     *
     * <p>Like the {@link #toArray ()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
     *
     * <p>Suppose <tt>x</tt> is a collection known to contain only strings.
     * The following code can be used to dump the collection into a newly
     * allocated array of <tt>String</tt>:
     *
     * <pre>
     *     String[] y = x.toArray (new String[0]);</pre>
     *
     * Note that <tt>toArray (new Object[0])</tt> is identical in function to
     * <tt>toArray ()</tt>.
     *
     * @param a the array into which the elements of this collection are to be
     *        stored, if it is big enough; otherwise, a new array of the same
     *        runtime type is allocated for this purpose.
     * @param   <T> Type of an array element.
     *
     * @return an array containing all of the elements in this collection
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a super type of the runtime type of every element in
     *         this collection
     * @throws NullPointerException if the specified array is null
     */
    public <T> T[] toArray (T[] a)
        throws ArrayStoreException, NullPointerException
    {
        return this.p_elems.toArray (a);
    } // toArray


    /**************************************************************************
     * Handle a class cast exception. <BR/>
     * This method should be called if the specified element is not of type
     * IElement.
     *
     * @param   methodName  The name of the method where the error occurred.
     *
     * @return  The classCastException.
     */
    private ClassCastException getClassCastException (String methodName)
    {
        return new ClassCastException ("Incompatible class within " +
            this.getClass ().getName () + "." + methodName + ".");
    } // getClassCastException


    /**************************************************************************
     * Handle an exception. <BR/>
     * This methods displays the exception and throws a new ListException.
     *
     * @param   e           The exception.
     * @param   methodName  The name of the method where the error occurred.
     *
     * @return  ListExceptionn which chains the other exception.
     */
    private ListException handleException (Throwable e, String methodName)
    {
        System.out.println (
            this.getClass ().getName () +
            "." + methodName + ": exception occurred: " +
            e.toString ());
//            e.toString () + "\n" + Helpers.getStackTraceFromThrowable (e));
        return new ListException (this.getClass ().getName () + "." +
            methodName + ".", e);
    } // handleException


    /**************************************************************************
     * Returns the string representation of this object. <BR/>
     * The element string representations are concatenated to create a string
     * representation according to "{elem1, elem2, ...}".
     *
     * @return  String represention of the object.
     */
    public String toString ()
    {
        String theString = "{";         // the string
        String sep = "";                // the element separator

        // start the list:
        theString = "elements {";

        // loop through the elements and append their string representations to
        // this object's string:
        // loop through all elements of the list:
        for (Iterator<E> iter = this.p_elems.iterator (); iter.hasNext ();)
        {
            // get the actual element and append it to the string:
            theString += sep + "{" + iter.next () + "}";
            // the new separator for the next element:
            sep = ", ";
        } // for iter

        // finish the list:
        theString += "}";

        // return the computed string:
        return theString;
    } // toString

} // class ElementContainer

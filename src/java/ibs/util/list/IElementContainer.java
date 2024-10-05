/*
 * Class: IElementContainer.java
 */

// package:
package ibs.util.list;

// imports:
import ibs.util.list.IElement;
import ibs.util.list.ListException;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;


/******************************************************************************
 * This class contains all data regarding a set of elements. <BR/>
 *
 * @version     $Id: IElementContainer.java,v 1.7 2007/07/10 09:16:35 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 011122
 *
 * @param   <E> Class for which to create the container.
 *              Must be subclass of IElement.
 ******************************************************************************
 */
public interface IElementContainer<E extends IElement> extends Collection<E>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IElementContainer.java,v 1.7 2007/07/10 09:16:35 kreimueller Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get a new instance of an element container. <BR/>
     *
     * @param   className   The class for the instance.
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
        throws ListException;


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
        throws ListException;


    /**************************************************************************
     * Add a new element to the container. <BR/>
     * If there is already an element with the same id that element is replaced.
     * If the id of the new element is -1 this check is not done.
     *
     * @param   elem    The element to be added.
     *
     * @return  <CODE>true</CODE> if the element was successfully added,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean add (E elem);


    /**************************************************************************
     * Add the elements of another container to this container. <BR/>
     *
     * @param   elemContainer   The container which contains the elements to be
     *                          added.
     *
     * @return  <CODE>true</CODE> if all elements were successfully added,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean addAll (IElementContainer<? extends E> elemContainer);


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
    public void move (E elem, IElementContainer<E> container)
        throws ListException;


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
        throws ListException;


    /**************************************************************************
     * Remove a specific element defined through its name. <BR/>
     *
     * @param   name    The name of the element to be removed.
     *
     * @return  The removed element or <CODE>null</CODE> if no element was
     *          removed.
     */
    public E remove (String name);


    /**************************************************************************
     * Remove an element from the container. <BR/>
     *
     * @param   elem    The element to be removed.
     *
     * @return  <CODE>true</CODE> if the container changed as a result of the
     *          call,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean remove (E elem);


    /**************************************************************************
     * Check if a specific element is contained within the container. <BR/>
     * The comparison is not done through the equals method of the object.
     * Instead the object must be the same java instance.
     *
     * @param   searchElem  The element to be searched for.
     *
     * @return  <CODE>true</CODE> if the element is contained,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean contains (E searchElem);


    /**************************************************************************
     * Get a specific element out of the list. <BR/>
     *
     * @param   id      The (unique) id of the element.
     *
     * @return  The found element or <CODE>null</CODE> if it was not found.
     */
    public E get (int id);


    /**************************************************************************
     * Get a specific element out of the list. <BR/>
     *
     * @param   id      The (unique) id of the element.
     *
     * @return  The found element or <CODE>null</CODE> if it was not found.
     */
    public E get (IElementId id);


    /**************************************************************************
     * Derive a element from the name. <BR/>
     *
     * @param   name        The (unique) name.
     *
     * @return  The found element or <CODE>null</CODE> if it was not found.
     */
    public E find (String name);


    /**************************************************************************
     * Sort the elements of the container. <BR/>
     * The sorting order is defined through the behaviour of the container.
     *
     * @throws  ListException
     *          There occurred an error during sorting.
     */
    public void sort ()
        throws ListException;


    /**************************************************************************
     * Check constraints for the elements within the container. <BR/>
     *
     * @throws  ListException
     *          Error when checking the constraints.
     */
    public void checkConstraints ()
        throws ListException;


    /**************************************************************************
     * Ensure that there are no elements within the element container. <BR/>
     */
    public void clear ();


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
    public Enumeration<E> elements ();


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
    public Iterator<E> iterator ();


    /**************************************************************************
     * Returns the string representation of this object. <BR/>
     * The element string representations are concatenated to create a string
     * representation according to "{elem1, elem2, ...}".
     *
     * @return  String represention of the object.
     */
    public String toString ();

} // interface IElementContainer

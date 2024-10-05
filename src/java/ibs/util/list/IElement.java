/*
 * Class: IElement.java
 */

// package:
package ibs.util.list;

// imports:


/******************************************************************************
 * This interface defines an element of an element list. All classe, whose
 * objects can be elements of such a list, must implement this interface. <BR/>
 * Implementing classes must have a constructor with only one parameter of type
 * <CODE>IElementId</CODE>.
 *
 * @version     $Id: IElement.java,v 1.6 2007/07/24 21:39:58 kreimueller Exp $
 *
 * @author      Klaus, 30.10.2003
 ******************************************************************************
 */
public interface IElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IElement.java,v 1.6 2007/07/24 21:39:58 kreimueller Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get the integer id of this element. <BR/>
     *
     * @return  The id of the element.
     */
    public int getIdInt ();


    /**************************************************************************
     * Get the String id of this element. <BR/>
     *
     * @return  The id of the element.
     */
    public String getIdStr ();


    /**************************************************************************
     * Return the name of the element. <BR/>
     *
     * @return  The name.
     */
    public String getName ();


    /**************************************************************************
     * Check constraints for the element. <BR/>
     *
     * @throws  ListException
     *          Error when checking the constraints.
     */
    public void checkConstraints ()
        throws ListException;


    /**************************************************************************
     * Returns the string representation of this object. <BR/>
     * The id and the name are concatenated to create a string
     * representation according to "id, name".
     *
     * @return  String represention of the object.
     */
    public String toString ();


    /**************************************************************************
     * Compares this object to another object. <BR/>
     * The result is <CODE>true</CODE> if and only if the argument is not
     * <CODE>null</CODE> and is an object of the same class with the same id
     * as this object.
     *
     * @param   anotherObj  The object to compare this element against.
     *
     * @return  <CODE>true</CODE> if the elements are equal,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean equals (Object anotherObj);


    /**************************************************************************
     * Compare this object with another object. <BR/>
     * This method presumes that other checks like != null and the class of the
     * other object are already done. Theses checks are not done within this
     * method. <BR/>
     * This method is specially designed to be overwritten in sub classes. It
     * is called by the {@link #equals equals} method.
     *
     * @param   anotherObj  The object to compare this element against.
     *
     * @return  <CODE>true</CODE> if the elements are equal,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean equals2 (IElement anotherObj);


    /**************************************************************************
     * Returns a hash code value for the object. <BR/>
     *
     * @return  A hash code value for this object.
     */
    public int hashCode ();

} // interface IElement

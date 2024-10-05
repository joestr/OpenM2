/*
 * Class: IElementId.java
 */

// package:
package ibs.util.list;

// imports:


/******************************************************************************
 * This interface defines the id of an element. <BR/>
 * This id can be of type int or String. <BR/>
 * An implementation must provide two constructors:. <BR/>
 * - one with parameter type int. <BR/>
 * - one with parameter type String. <BR/>
 *
 * @version     $Id: IElementId.java,v 1.5 2007/07/31 19:14:01 kreimueller Exp $
 *
 * @author      Klaus, 21.12.2003
 ******************************************************************************
 */
public interface IElementId
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IElementId.java,v 1.5 2007/07/31 19:14:01 kreimueller Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


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
    public boolean equals2 (ElementId anotherObj);


    /**************************************************************************
     * Returns a hash code value for the object. <BR/>
     *
     * @return  A hash code value for this object.
     */
    public int hashCode ();

} // interface IElementId

/*
 * Class: Element.java
 */
/*
 * Created by IntelliJ IDEA.
 * User: kreimueller
 * Date: Nov 22, 2001
 * Time: 3:26:58 PM
 */

// package:
package ibs.util.list;

// imports:
import ibs.BaseObject;
import ibs.util.list.ElementId;
import ibs.util.list.IElementId;


/******************************************************************************
 * This class represents an Element to be used within a container. <BR/>
 *
 * @version     $Id: Element.java,v 1.10 2007/07/24 21:39:58 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 011122
 ******************************************************************************
 */
public class Element extends BaseObject implements IElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Element.java,v 1.10 2007/07/24 21:39:58 kreimueller Exp $";


    /**
     * The unique id of the element. <BR/>
     */
    private IElementId p_id = null;

    /**
     * Name of the element. <BR/>
     */
    private String p_name = null;

    /**
     * The hash code. <BR/>
     */
    private int p_hashCode = Integer.MIN_VALUE;


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates an Element object. <BR/>
     *
     * @param   id          Id of the element.
     * @param   name        The element's name.
     */
    public Element (IElementId id, String name)
    {
        // set the instance's properties:
        this.init (id, name);

        // initialize the other instance properties:
    } // Element


    /**************************************************************************
     * Creates an Element object. <BR/>
     *
     * @param   id          Id of the element.
     * @param   name        The element's name.
     */
    public Element (int id, String name)
    {
        // set the instance's properties:
        this.init (new ElementId (id), name);

        // initialize the other instance properties:
    } // Element


    /**************************************************************************
     * Creates an Element object. <BR/>
     *
     * @param   id          Id of the element.
     * @param   name        The element's name.
     *
     * @throws  ListException
     *          Error when initializing the element.
     *          Possible causes: invalid id.
     */
    public Element (String id, String name)
        throws ListException
    {
        // set the instance's properties:
        this.init (new ElementId (id), name);

        // initialize the other instance properties:
    } // Element



    ///////////////////////////////////////////////////////////////////////////
    // methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Initialize an Element object. <BR/>
     *
     * @param   id          Id of the element.
     * @param   name        The element's name.
     */
    protected void init (IElementId id, String name)
    {
        // set the instance's properties:
        this.p_id = id;
        this.p_name = name;

        // initialize the other instance properties:
    } // init


    /**************************************************************************
     * Get the id of this element. <BR/>
     *
     * @return  The id of the element.
     */
    public final IElementId getId ()
    {
        // get the value and return it:
        return this.p_id;
    } // getId


    /**************************************************************************
     * Get the integer id of this element. <BR/>
     *
     * @return  The id of the element.
     */
    public final int getIdInt ()
    {
        // get the value and return it:
        return this.p_id.getIdInt ();
    } // getIdInt


    /**************************************************************************
     * Get the String id of this element. <BR/>
     *
     * @return  The id of the element.
     *
     * @deprecated  don't use this method.
     */
    public final String getIdStr ()
    {
        // get the value and return it:
        return this.p_id.getIdStr ();
    } // getIdStr


    /**************************************************************************
     * Return the name of the element. <BR/>
     *
     * @return  The name.
     */
    public String getName ()
    {
        // get the name and return it:
        return this.p_name;
    } // getName


    /**************************************************************************
     * Check constraints for the element. <BR/>
     *
     * @throws  ListException
     *          Error when checking the constraints.
     */
    public void checkConstraints ()
        throws ListException
    {
        // nothing to do here
    } // checkConstraints


    /**************************************************************************
     * Returns the string representation of this object. <BR/>
     * The id and the name are concatenated to create a string
     * representation according to "id, name".
     *
     * @return  String represention of the object.
     */
    public String toString ()
    {
        // compute the string and return it:
        return this.p_id.getIdStr () + "," + this.p_name;
    } // toString


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
    public boolean equals (Object anotherObj)
    {
        // check if the object is not null and of correct class:
        if (anotherObj != null &&       // not null?
            this.getClass ().isAssignableFrom (anotherObj.getClass ())) // correct class?
        {
            // check for equality; compare the id:
            return this.equals2 ((IElement) anotherObj);
        } // if not null and correct class

        // null or wrong class
        return false;
    } // equals


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
    public boolean equals2 (IElement anotherObj)
    {
        // check for equality; compare the id:
        return this == anotherObj ||
               this.p_id.equals (((Element) anotherObj).p_id);
    } // equals2


    /**************************************************************************
     * Returns a hash code value for the object. <BR/>
     *
     * @return  A hash code value for this object.
     */
    public int hashCode ()
    {
        // check if a valid hash code was set:
        if (this.p_hashCode == Integer.MIN_VALUE)
        {
            // compute hash code from id:
            this.p_hashCode = this.p_id.hashCode ();
        } // if

        // return the result:
        return this.p_hashCode;
    } // hashCode

} // class Element

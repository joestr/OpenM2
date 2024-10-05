/*
 * Class: ElementId.java
 */

// package:
package ibs.util.list;

// imports:
import ibs.util.list.IElementId;


/******************************************************************************
 * This class implements the id of an Element. <BR/>
 *
 * @version     $Id: ElementId.java,v 1.6 2007/07/24 21:39:58 kreimueller Exp $
 *
 * @author      Klaus, 21.12.2003
 ******************************************************************************
 */
public class ElementId extends Object implements IElementId
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ElementId.java,v 1.6 2007/07/24 21:39:58 kreimueller Exp $";


    /**
     * The id as integer. <BR/>
     */
    private int p_idInt = 0;

    /**
     * The id as String. <BR/>
     */
    private String p_idStr = "";

    /**
     * <CODE>true</CODE> if the id is of type int,
     * <CODE>false</CODE> otherwise. <BR/>
     * Default: <CODE>true</CODE>
     */
    private boolean p_isIdInt = true;

    /**
     * The hash code. <BR/>
     */
    private int p_hashCode = Integer.MIN_VALUE;



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates an ElementId object. <BR/>
     *
     * @param   id          Id of the element.
     */
    public ElementId (int id)
    {
        // set the instance's properties:
        this.init (id);

        // initialize the other instance properties:
    } // ElementId


    /**************************************************************************
     * Creates an ElementId object. <BR/>
     *
     * @param   id          Id of the element.
     *
     * @throws  ListException
     *          There was an error within the id.
     *          Possible causes: id string is null or empty.
     */
    public ElementId (String id)
        throws ListException
    {
        // set the instance's properties:
        this.init (id);

        // initialize the other instance properties:
    } // ElementId


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////


    /**************************************************************************
     * Initialize an Element object. <BR/>
     *
     * @param   id          Id of the element.
     */
    private void init (int id)
    {
        // set the instance's properties:
        this.p_idInt = id;
        this.p_idStr = Integer.toString (id);
        this.p_isIdInt = true;

        // initialize the other instance properties:
    } // init


    /**************************************************************************
     * Initialize an Element object. <BR/>
     *
     * @param   id          Id of the element.
     *
     * @throws  ListException
     *          There was an error within the id.
     *          Possible causes: id string is null or empty.
     */
    private void init (String id)
        throws ListException
    {
        // check constraints:
        if (id == null || id.length () == 0)
        {
            throw new ListException (
                "The id for an element is not allowed to be null or empty.");
        } // if

        // set the instance's properties:
        this.p_idStr = id;
        try
        {
            this.p_idInt = Integer.parseInt (id);
        } // try
        catch (NumberFormatException e)
        {
            this.p_idInt = 0;
        } // catch
        this.p_isIdInt = false;

        // initialize the other instance properties:
    } // init


    /**************************************************************************
     * Get the integer id of this element. <BR/>
     *
     * @return  The id of the element.
     */
    public final int getIdInt ()
    {
        // get the value and return it:
        return this.p_idInt;
    } // getIdInt


    /**************************************************************************
     * Get the String id of this element. <BR/>
     *
     * @return  The id of the element.
     */
    public final String getIdStr ()
    {
        // get the value and return it:
        return this.p_idStr;
    } // getIdStr


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
            return this.equals2 ((ElementId) anotherObj);
        } // if not null and correct class

        // null or wrong class
        return false;
    } // equals


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
            // check if the id is of type integer:
            if (this.p_isIdInt)
            {
                // set hash value equal to id:
                this.p_hashCode = this.p_idInt;
            } // if
            else if (this.p_idStr != null)
            {
                // compute hash value from id string:
                this.p_hashCode = this.p_idStr.hashCode ();
            } // else if
        } // if

        // return the result:
        return this.p_hashCode;
    } // hashCode


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
    public boolean equals2 (ElementId anotherObj)
    {
        // check for equality; compare the id:
        return this == anotherObj ||
                ((this.p_isIdInt && this.p_idInt == anotherObj.p_idInt) ||
                 (!this.p_isIdInt && this.p_idStr.equals (anotherObj.p_idStr)));
    } // equals2


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
        return this.getIdStr ();
    } // toString

} // class ElementId

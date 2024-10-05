/*
 * Class: SystemValue.java
 */
/*
 * Created by IntelliJ IDEA.
 * User: kreimueller
 * Date: Dec 3, 2001
 * Time: 4:44:20 PM
 */

// package:
package ibs.app.system;

// imports:
import ibs.util.list.Element;
import ibs.util.list.IElement;
import ibs.util.list.IElementId;


/******************************************************************************
 * This class contains one system value. <BR/>
 *
 * @version     $Id: SystemValue.java,v 1.7 2007/07/17 12:15:36 kreimueller Exp $
 *
 * @author      kreimueller, 011122
 ******************************************************************************
 */
public class SystemValue extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SystemValue.java,v 1.7 2007/07/17 12:15:36 kreimueller Exp $";


    /**
     * The type of the value. <BR/>
     */
    private String p_type = null;

    /**
     * The value itself. <BR/>
     */
    private String p_value = null;


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a SystemValue object. <BR/>
     *
     * @param   id          Id of the element.
     * @param   name        The element's name.
     */
    public SystemValue (IElementId id, String name)
    {
        // call constructor of super class:
        super (id, name);

        // set the instance's properties:
        this.p_type = null;
        this.p_value = null;
    } // SystemValue


    /**************************************************************************
     * Creates a SystemValue object. <BR/>
     * Calls the constructor of the super class. <BR/>
     * The private property p_type is set to <CODE>null</CODE>. <BR/>
     * The private property p_value is set to <CODE>null</CODE>. <BR/>
     *
     * @param   id          Id of the value.
     */
    public SystemValue (int id)
    {
        // call constructor of super class:
        super (id, null);

        // set the instance's properties:
        this.p_type = null;
        this.p_value = null;
    } // SystemValue


    /**************************************************************************
     * Creates a SystemValue object. <BR/>
     *
     * @param   id          Id of the value.
     * @param   name        The value's name.
     * @param   type        The value's type.
     * @param   value       The value itself.
     */
    public SystemValue (int id, String name, String type, String value)
    {
        // call constructor of super class:
        super (id, name);

        // set the instance's properties:
        this.p_type = type;
        this.p_value = value;
    } // SystemValue



    ///////////////////////////////////////////////////////////////////////////
    // methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Return the value. <BR/>
     *
     * @return  The value.
     */
    public final String getValue ()
    {
        // get the value and return it:
        return this.p_value;
    } // getValue


    /**************************************************************************
     * Returns the string representation of this object. <BR/>
     * The type, name and value are concatenated to create a string
     * representation according to "type name = value".
     *
     * @return  String represention of the object.
     */
    public final String toString ()
    {
        // compute the string and return it:
        return this.p_type + " " + this.getName () + " = " + this.p_value;
    } // toString


    /**************************************************************************
     * Compare this object with another object. <BR/>
     * This method presumes that other checks like != null and the class of the
     * other object are already done. Theses checks are not done within this
     * method. <BR/>
     * The result is <CODE>true</CODE> if and only if the argument
     * has the same id and name as this object.
     *
     * @param   anotherObj  The object to compare this object against.
     *
     * @return  <CODE>true</CODE> if the tabs are equal; false otherwise.
     */
    public final boolean equals2 (IElement anotherObj)
    {
        // check for equality; compare the id:
        return super.equals2 (anotherObj) &&
               this.getName ().equals (((SystemValue) anotherObj).getName ());
    } // equals2

} // class SystemValue

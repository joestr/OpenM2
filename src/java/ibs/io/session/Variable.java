/**
 * Class: Variable.java
 */

// package:
package ibs.io.session;

// imports:
import ibs.BaseObject;

import java.util.Date;


/******************************************************************************
 * This is the Variable Object, which holds information about a Variable
 *
 * @version     $Id: Variable.java,v 1.6 2007/07/24 21:29:09 kreimueller Exp $
 *
 * @author        Christine Keim  (CK)    980304
 ******************************************************************************
 */
public class Variable extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Variable.java,v 1.6 2007/07/24 21:29:09 kreimueller Exp $";


    /**
     * name of the variable. <BR/>
     */
    public String name;
    /**
     * value of the variable. <BR/>
     */
    public String value;
    /**
     * set. Time when the variable was last set. <BR/>
     */
    public Date set;


    /**************************************************************************
     * Create a new instance representing a Value. <BR/>
     * The properties name and value are instantiated with the given parameters.
     * The property <A HREF="#set">set</A> is set to the current date.
     *
     * @param   varName     name of the variable
     * @param   varValue    value of the variable
     */
    public Variable (String varName, String varValue)
    {
        this.name = varName;
        this.value = varValue;
        this.set = new Date ();
    } // Variable


    /**************************************************************************
     * Sets the value of the variable. <BR/>
     * The property <A HREF="#set">set</A> is set to the current date.
     *
     * @param   newValue    Value to be set.
     */
    public void setVariable (String newValue)
    {
        this.value = newValue;
        this.set = new Date ();
    } // setVariable


    /**************************************************************************
     * Deletes the value of the variable. <BR/>
     * The property <A HREF="#set">set</A> is set to the current date and
     * the value is set to null.
     */
    public void delete ()
    {
        this.value = null;
        this.set = new Date ();
    } // delete

} // class Variable

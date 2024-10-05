/*
 * Class: ActionParameter.java
 */

// package:
package ibs.service.action;

// imports:
import ibs.BaseObject;
import ibs.service.action.ActionConstants;


/******************************************************************************
 * The ActionParameter holds information about one parameter for action
 * defined in workflow or form templates. <BR/>
 *
 * @version     $Id: ActionParameter.java,v 1.8 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      Michael Steiner (MS), 6.04.2000
 ******************************************************************************
 */
public class ActionParameter extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ActionParameter.java,v 1.8 2007/07/24 21:27:33 kreimueller Exp $";


    ///////////////////////////////////////////////
    //
    // Information that comes from the XML-definition
    //

    /**
     *  name of the variable. <BR/>
     */
    private String p_name = ActionConstants.UNDEFINED;

    /**
     *  the optional query field name. <BR/>
     */
    private String p_field = null;


    /**************************************************************************
     * Creates an ActionParameter. <BR/>
     *
     * @param   name        name of variable
     * @param   field       query field name
     */
    public ActionParameter (String name, String field)
    {
        this.p_name = name;
        this.p_field = field;
    } // ActionParameter


    /**************************************************************************
     * Returns a copy (clone) of the ActionParameter object.
     *
     * @return  The copy.
     */
    public ActionParameter getCopy ()
    {
        return new ActionParameter (this.p_name, this.p_field);
    } // getCopy


    /**************************************************************************
     * Returns variables name. <BR/>
     *
     * @return  The name of the variable.
     */
    public String getName ()
    {
        return this.p_name;
    } // getName


    /**************************************************************************
     * Returns the optional query field name. <BR/>
     *
     * @return  The field name.
     */
    public String getQueryField ()
    {
        return this.p_field;
    } // getQueryField


    /**************************************************************************
     * Returns a string representation of this object. <BR/>
     *
     * @return  A string representation of this object.
     */
    public String toString ()
    {
        // build string
        return "name = " + this.p_name + "; " +
               "field = " + this.p_field + "; ";
    } // toString

} // class Variable

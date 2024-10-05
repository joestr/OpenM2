/*
 * Class: OutputParamElement.java
 */

// package:
package ibs.di;

// imports:
import ibs.BaseObject;


/******************************************************************************
 * This class holds all information regarding to one input-parameter. <BR/>
 *
 * @version     $Id: OutputParamElement.java,v 1.5 2007/08/10 14:56:37 kreimueller Exp $
 *
 * @author      Daniel Janesch (DJ), 020122
 ******************************************************************************
 */
public class OutputParamElement extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: OutputParamElement.java,v 1.5 2007/08/10 14:56:37 kreimueller Exp $";


    /**
     * Name of parameter. <BR/>
     */
    private String p_name = null;

    /**
     * Value of parameter. <BR/>
     */
    private String p_value = null;


    /**************************************************************************
     * Creates an OutputParamElement. <BR/>
     * The private property p_name is set to <CODE>null</CODE>. <BR/>
     * The private property p_value is set to <CODE>null</CODE>. <BR/>
     */
    public OutputParamElement ()
    {
        this.p_name = null;
        this.p_value = null;
    } // ValueDataElement


    /**************************************************************************
     * Creates an OutputParamElement. <BR/>
     *
     * @param   name    Name of the param.
     * @param   value   Value of the param.
     */
    public OutputParamElement (String name, String value)
    {
        this.p_name = name;
        this.p_value = value;
    } // OutputParamElement


    /***************************************************************************
     * Returns the name of param. <BR/>
     *
     * @return  The name of the param.
     */
    public String getName ()
    {
        return this.p_name;
    } // getName


    /***************************************************************************
     * Returns the value of param. <BR/>
     *
     * @return  The value of the param.
     */
    public String getValue ()
    {
        return this.p_value;
    } // getValue

} // class OutputParamElement

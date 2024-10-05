/*
 * Class: StoredProcedure.java
 */

// package:
package ibs.tech.sql;

// imports:
import ibs.BaseObject;
import ibs.tech.sql.DBParameterException;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.StoredProcedureConstants;

import java.util.Date;
import java.util.Vector;


/******************************************************************************
 * Used to build stored procedures. <BR/>
 * User has to set the (database) name and return type of the stored
 * procedure and must add all necessary parameters (of type Parameter).
 *
 * @version     $Id: StoredProcedure.java,v 1.8 2013/01/17 15:22:13 btatzmann Exp $
 *
 * @author      Mark Wassermann (MW)
 ******************************************************************************
 */
public class StoredProcedure extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: StoredProcedure.java,v 1.8 2013/01/17 15:22:13 btatzmann Exp $";


    /**
     * Name of the stored procedure (in database). <BR/>
     */
    private String              p_name;

    /**
     * Vector of <A HREF="#Parameter">Parameter</A> objects. <BR/>
     */
    private Vector<Parameter>   p_parameters;

    /**
     * Return type of stored procedure. <BR/>
     * Referring to class <A HREF="#ConstantsStoredProcedure">ConstantsStoredProcedure</A>.
     */
    private short               p_returnType;

    /**
     * Return Value of stored procedure. <BR/>
     * Can only be integer: MS-SQL server is not capable of returning
     * other types.
     */
    private int                 p_returnValue;


    /**************************************************************************
     * Simple constructor. <BR/>
     */
    public StoredProcedure ()
    {
        // initialize attributes
        this.p_name = new String ("");
        this.p_parameters = new Vector<Parameter> ();
        this.p_returnType = StoredProcedureConstants.RETURN_UNDEFINED;
        this.p_returnValue = -1;
    } // PreparedStatement


    /**************************************************************************
     * Constructor. <BR/>
     *
     * @param   name    The name to prepare.
     * @param   returnType  The return type of the statement
     */
    public StoredProcedure (String name, short returnType)
    {
        // initialize attributes
        this.p_name = new String (name);
        this.p_parameters = new Vector<Parameter> ();
        this.p_returnType = returnType;
        this.p_returnValue = -1;
    } // PreparedStatement


    /**************************************************************************
     * Set the (database) name of the stored procedure. <BR/>
     * Important: USE THE SAME NAME AS IN THE DB-DEFINITION
     *
     * @param   name    The name of the stored procedure
     */
    public void setName (String name)
    {
        this.p_name = name;
    } // setStatement


    /**************************************************************************
     * Get the name of the stored procedure. <BR/>
     *
     * @return  The name of the stored procedure.
     */
    public String getName ()
    {
        return this.p_name;
    } // setStatement


    /**************************************************************************
     * Set the return type of the stored procedure. <BR/>
     *
     * @param   returnType  The returnType of the stored procedure.
     */
    public void setReturnType (short returnType)
    {
        this.p_returnType = returnType;
    } // setReturnType


    /**************************************************************************
     * Get the return type of the stored procedure. <BR/>
     *
     * @return  The returnType of the stored procedure.
     */
    public short getReturnType ()
    {
        return this.p_returnType;
    } // getReturnType


    /**************************************************************************
     * Set the return value of the stored procedure. <BR/>
     *
     * @param   returnValue The return value of the stored procedure.
     */
    protected void setReturnType (int returnValue)
    {
        this.p_returnValue = returnValue;
    } // setReturnType


    /**************************************************************************
     * Set the return value of the stored procedure. <BR/>
     *
     * @param   returnValue The return value of the stored procedure.
     */
    public void setReturnValue (int returnValue)
    {
        this.p_returnValue = returnValue;
    } // setReturnValue


    /**************************************************************************
     * Get the return value of the stored procedure. <BR/>
     *
     * @return The return value of the stored procedure.
     */
    public int getReturnValue ()
    {
        return this.p_returnValue;
    } // getReturnType


    /**************************************************************************
     * Adds a parameter to the stored procedure. <BR/>
     *
     * @param   parameter  The paremeter to add.
     *
     * @return  The parameter itself.
     */
    public Parameter addParameter (Parameter parameter)
    {
        (this.p_parameters).addElement (parameter);
        return parameter;
    } // addParameter


    /**************************************************************************
     * Gets all parameters of the stored procedure as array. <BR/>
     *
     * @return  The parameter from the specified position.
     */
    public Parameter[] getParameters ()
    {
        return (Parameter[]) this.p_parameters.toArray ();
    } // getParameters


    /**************************************************************************
     * Gets a parameter of stored procedure from the specified
     * position. <BR/>
     *
     * @param   number  The position of the parameter in the prepared Statement
     *
     * @return  The parameter from the specified position.
     */
    public Parameter getParameter (int number)
    {
        return this.p_parameters.elementAt (number);
    } // getParameter


    /**************************************************************************
     * Gets the number of parameters in the stored procedure. <BR/>
     *
     * @return  The number of parameters in the stored procedure.
     */
    public int countParameters ()
    {
        return (this.p_parameters).size ();
    } // addParameter


    /**************************************************************************
     * Set an input parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInParameter (short datatype, boolean value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_IN);
        param.setValue (value);
        return this.addParameter (param);
    } // addInParameter


    /**************************************************************************
     * Set an input parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInParameter (short datatype, byte value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_IN);
        param.setValue (value);
        return this.addParameter (param);
    } // addInParameter


    /**************************************************************************
     * Set an input parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInParameter (short datatype, short value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_IN);
        param.setValue (value);
        return this.addParameter (param);
    } // addInParameter


    /**************************************************************************
     * Set an input parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInParameter (short datatype, int value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_IN);
        param.setValue (value);
        return this.addParameter (param);
    } // addInParameter


    /**************************************************************************
     * Set an input parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInParameter (short datatype, float value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_IN);
        param.setValue (value);
        return this.addParameter (param);
    } // addInParameter


    /**************************************************************************
     * Set an input parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInParameter (short datatype, double value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_IN);
        param.setValue (value);
        return this.addParameter (param);
    } // addInParameter


    /**************************************************************************
     * Set an input parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInParameter (short datatype, Date value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_IN);
        param.setValue (value);
        return this.addParameter (param);
    } // addInParameter


    /**************************************************************************
     * Set an input parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInParameter (short datatype, long value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_IN);
        param.setValue (value);
        return this.addParameter (param);
    } // addInParameter


    /**************************************************************************
     * Set an input parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInParameter (short datatype, String value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_IN);
        param.setValue (value);
        return this.addParameter (param);
    } // addInParameter


    /**************************************************************************
     * Set an input parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInParameter (short datatype, char[] value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_IN);
        param.setValue (value);
        return this.addParameter (param);
    } // addInParameter


    /**************************************************************************
     * Set an input parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInParameter (short datatype, Object value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_IN);
        param.setValue (value);
        return this.addParameter (param);
    } // addInParameter


    /**************************************************************************
     * Set an input parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInParameter (short datatype, byte[] value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_IN);
        param.setValue (value);
        return this.addParameter (param);
    } // addInParameter


    /**************************************************************************
     * Set an input/output parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInOutParameter (short datatype, boolean value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_INOUT);
        param.setValue (value);
        return this.addParameter (param);
    } // addInOutParameter


    /**************************************************************************
     * Set an input/output parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInOutParameter (short datatype, byte value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_INOUT);
        param.setValue (value);
        return this.addParameter (param);
    } // addInOutParameter


    /**************************************************************************
     * Set an input/output parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInOutParameter (short datatype, short value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_INOUT);
        param.setValue (value);
        return this.addParameter (param);
    } // addInOutParameter


    /**************************************************************************
     * Set an input/output parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInOutParameter (short datatype, int value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_INOUT);
        param.setValue (value);
        return this.addParameter (param);
    } // addInOutParameter


    /**************************************************************************
     * Set an input/output parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInOutParameter (short datatype, float value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_INOUT);
        param.setValue (value);
        return this.addParameter (param);
    } // addInOutParameter


    /**************************************************************************
     * Set an input/output parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInOutParameter (short datatype, double value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_INOUT);
        param.setValue (value);
        return this.addParameter (param);
    } // addInOutParameter


    /**************************************************************************
     * Set an input/output parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInOutParameter (short datatype, Date value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_INOUT);
        param.setValue (value);
        return this.addParameter (param);
    } // addInOutParameter


    /**************************************************************************
     * Set an input/output parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInOutParameter (short datatype, long value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_INOUT);
        param.setValue (value);
        return this.addParameter (param);
    } // addInOutParameter


    /**************************************************************************
     * Set an input/output parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInOutParameter (short datatype, String value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_INOUT);
        param.setValue (value);
        return this.addParameter (param);
    } // addInOutParameter


    /**************************************************************************
     * Set an input/output parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInOutParameter (short datatype, char[] value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_INOUT);
        param.setValue (value);
        return this.addParameter (param);
    } // addInOutParameter


    /**************************************************************************
     * Set an input/output parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInOutParameter (short datatype, Object value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_INOUT);
        param.setValue (value);
        return this.addParameter (param);
    } // addInOutParameter


    /**************************************************************************
     * Set an input/output parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     * @param   value       The parameter value.
     *
     * @return  The newly created parameter.
     */
    public Parameter addInOutParameter (short datatype, byte[] value)
    {
        Parameter param = new Parameter ("", datatype, ParameterConstants.DIRECTION_INOUT);
        param.setValue (value);
        return this.addParameter (param);
    } // addInOutParameter


    /**************************************************************************
     * Set an output parameter of a stored procedure. <BR/>
     *
     * @param   datatype    The datatype of the parameter.
     *
     * @return  The newly created parameter.
     */
    public Parameter addOutParameter (short datatype)
    {
        return this.addParameter (
            new Parameter ("", datatype, ParameterConstants.DIRECTION_OUT));
    } // addOutParameter


    /**************************************************************************
     * Check the parameter kinds of the stored procedure. <BR/>
     * This method checks if all parameters are of the desired kinds. These are
     * {@link ParameterConstants#DIRECTION_IN ParameterConstants.DIRECTION_IN}
     * and
     * {@link ParameterConstants#DIRECTION_OUT ParameterConstants.DIRECTION_OUT}.
     *
     * @return  <CODE>true</CODE> if everything was o.k.,
     *          <CODE>false</CODE> otherwise. The value <CODE>false</CODE>
     *          should never be returned because there should be thrown an
     *          exception.
     *
     * @throws  DBParameterException
     *          At least one parameter is of wrong kind.
     */
    public boolean checkParameterKinds () throws DBParameterException
    {
        int count = this.countParameters (); // the number of parameters
        Parameter param;                // the actual parameter

        // loop through all parameters:
        for (int i = 0; i < count; i++)
        {
            // create help parameter
            param = this.getParameter (i);

            // check for invalid direction of parameter:
            if (param.getDirection () != ParameterConstants.DIRECTION_IN &&
                param.getDirection () != ParameterConstants.DIRECTION_OUT)
            {
                throw new DBParameterException (this, null, true);
            } // if
        } // for

        // everything o.k.
        return true;
    } // checkParameterKinds


    /**************************************************************************
     * Builds the ODBC compliant stored procedure call-string. <BR/>
     *
     * @return  The number of parameters in the stored procedure.
     *
     * @throws  DBParameterException
     *          At least one parameter is of wrong kind.
     */
    public String buildOdbcCallString () throws DBParameterException
    {
        // check the parameter kinds:
        this.checkParameterKinds ();

        // create and initialize the return string
        StringBuilder odbcString = new StringBuilder ("{");

        // add return value?
        if (this.p_returnType == StoredProcedureConstants.RETURN_VALUE)
        {
            odbcString.append ("? = ");
        } // if

        // add stored procedure name
        odbcString.append ("call " + this.p_name + " (");

        // add a questionmark for every parameter
        for (int i = 0; i < this.countParameters (); i++)
        {
            // output parameter
            odbcString.append ("?");

            // add comma?
            if (i != (this.countParameters () - 1))
            {
                // yes - not last parameter
                odbcString.append (", ");
            } // if

        } // for

        // finish string
        odbcString.append (")}");

        //done - return string, exit
        return odbcString.toString ();
    } // buildOdbcString


    /**************************************************************************
     * Remove all parameters from stored procedure
     */
    public void removeParameters ()
    {
        this.p_parameters.removeAllElements ();
    } // removeParameter

} // class StoredProcedure

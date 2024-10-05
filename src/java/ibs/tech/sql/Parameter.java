/*
 * Class: Parameter.java
 */

// package:
package ibs.tech.sql;

// imports:
import ibs.BaseObject;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLHelpers;

import java.util.Date;


/******************************************************************************
 * Parameter represents one parameter of a stored procedure and is used in
 * the StoredProcedure class to build a stored procedure call. <BR/>
 * The user has to create parameters for stored procedures.
 *
 * @version     $Id: Parameter.java,v 1.6 2007/07/31 19:13:59 kreimueller Exp $
 *
 * @author      Mark Wassermann (MW)
 ******************************************************************************
 */
public class Parameter extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Parameter.java,v 1.6 2007/07/31 19:13:59 kreimueller Exp $";


    /**
     * Name of the parameter. <BR/>
     */
    private String              p_name;

    /**
     * Data type of the parameter. <BR/>
     * Referring to class <A HREF="#ParameterConstants">ParameterConstants</A>.
     */
    private short               p_dataType;

    /**
     * Direction of the parameter. <BR/>
     * Referring to class <A HREF="#ParameterConstants">ParameterConstants</A>.
     * <BR/>
     */
    private short               p_direction;

    /**
     * Is the parameter NULL. <BR/>
     * Means: no value set or NULL value returned. >BR>
     */
    private boolean             p_isNull;

    // Value of the parameter.
    // Only one is set.
    /**
     * Boolean value of the parameter. <BR/>
     */
    private boolean             p_valueBoolean = true;
    /**
     * Byte value of the parameter. <BR/>
     */
    private byte                p_valueByte = 0;
    /**
     * Short value of the parameter. <BR/>
     */
    private short               p_valueShort = 0;
    /**
     * Integer value of the parameter. <BR/>
     */
    private int                 p_valueInteger = 0;
    /**
     * Float value of the parameter. <BR/>
     */
    private float               p_valueFloat = 0.0f;
    /**
     * Double value of the parameter. <BR/>
     */
    private double              p_valueDouble = 0.0d;
    /**
     * String value of the parameter. <BR/>
     */
    private String              p_valueString = "";
    /**
     * VARCHAR value of the parameter. <BR/>
     */
    private char[]              p_valueVarChar = "".toCharArray ();
    /**
     * Object value of the parameter. <BR/>
     */
    private Object              p_valueObject = null;
    /**
     * Date value of the parameter. <BR/>
     */
    private Date                p_valueDate = null;
    /**
     * Currency value of the parameter. <BR/>
     */
    private long                p_valueCurrency = 0;
    /**
     * VARBYTE value of the parameter. <BR/>
     */
    private byte[]              p_valueVarByte = {0};


    /**************************************************************************
     * Constructor. <BR/>
     */
    public Parameter ()
    {
        // initialize attributes
        this.p_name = new String ("");
        this.p_dataType = ParameterConstants.TYPE_UNDEFINED;
        this.p_direction = ParameterConstants.DIRECTION_UNDEFINED;
        this.p_isNull = true;
    } // Parameter


    /**************************************************************************
     * Constructor. <BR/>
     *
     * @param   name        The name of the parameter
     * @param   dataType     The data type of the parameter
     * @param   direction   The direction (in/out) of the parameter
     *
     */
    public Parameter (String name, short dataType, short direction)
    {
        // initialize attributes
        this.p_name = new String (name);
        this.p_dataType = dataType;
        this.p_direction = direction;
        this.p_isNull = true;
    } // Parameter


    /**************************************************************************
     * Set the name of the parameter. <BR/>
     *
     * @param   name    Name of the parameter
     *
     */
    public void setName (String name)
    {
        this.p_name = name;
    } // setName


    /**************************************************************************
     * Get the name of the parameter. <BR/>
     *
     * @return  The name of the parameter
     */
    public String getName ()
    {
        return this.p_name;
    } // setName


    /**************************************************************************
     * Set the data type of the parameter. <BR/>
     * Datatype can be (defined in ParameterConstants class):
     *
     * @param   dataType    The data type of the parameter.
     *
     * @see     ParameterConstants
     */
    public void setDataType (short dataType)
    {
        this.p_dataType = dataType;
    } // setDataType


    /**************************************************************************
     * Get the data type of the parameter. <BR/>
     * Datatype can be (defined in ParameterConstants class):
     *
     * @return  The data type of the parameter.
     *
     * @see     ParameterConstants
     */
    public short getDataType ()
    {
        return this.p_dataType;
    } // getDataType


    /**************************************************************************
     * Set the direction of the parameter. <BR/>
     * Direction can be (defined in ParameterConstants class):
     *
     * @param   direction   The direction of the parameter.
     *
     * @see     ParameterConstants
     */
    public void setDirection (short direction)
    {
        this.p_direction = direction;
    } // setDirection


    /**************************************************************************
     * Get the direction of the parameter. <BR/>
     * Direction can be (defined in ParameterConstants class):
     *
     * @return  The direction of the parameter.
     *
     * @see     ParameterConstants
     */
    public short getDirection ()
    {
        return this.p_direction;
    } // getDirection


    /**************************************************************************
     * Is the value of the parameter NULL?. <BR/>
     *
     * @return  Is the parameter NULL?
     */
    public boolean isNull ()
    {
        return this.p_isNull;
    } // isNull


    /**************************************************************************
     * Set parameter value to NULL. <BR/>
     */
    public void setNull ()
    {
        this.p_isNull = true;
    } // setNull


    /**************************************************************************
     * Set the value of the parameter: boolean. <BR/>
     *
     * @param value   The boolean value of the parameter.
     */
    public void setValue (boolean value)
    {
        this.p_valueBoolean = value;
        this.p_isNull = false;
    } // setValue


    /**************************************************************************
     * Get the boolean value of the parameter. <BR/>
     *
     * @return  The boolean value of the parameter.
     */
    public boolean getValueBoolean ()
    {
        return this.p_valueBoolean;
    } // getValue


    /**************************************************************************
     * Set the value of the parameter: byte. <BR/>
     *
     * @param   value   The byte value of the parameter.
     */
    public void setValue (byte value)
    {
        this.p_valueByte = value;
        this.p_isNull = false;
    } // setValue


    /**************************************************************************
     * Get the byte value of the parameter. <BR/>
     *
     * @return  The byte value of the parameter.
     */
    public byte getValueByte ()
    {
        return this.p_valueByte;
    } // getValue


    /**************************************************************************
     * Set the value of the parameter: short. <BR/>
     *
     * @param   value   The short value of the parameter.
     */
    public void setValue (short value)
    {
        this.p_valueShort = value;
        this.p_isNull = false;
    } // setValue


    /**************************************************************************
     * Get the short value of the parameter. <BR/>
     *
     * @return  The short value of the parameter.
     */
    public short getValueShort ()
    {
        return this.p_valueShort;
    } // getValue


    /**************************************************************************
     * Set the value of the parameter: integer. <BR/>
     *
     * @param   value   The integer value of the parameter.
     */
    public void setValue (int value)
    {
        this.p_valueInteger = value;
        this.p_isNull = false;
    } // setValue


    /**************************************************************************
     * Get the integer value of the parameter. <BR/>
     *
     * @return  The integer value of the parameter.
     */
    public int getValueInteger ()
    {
        return this.p_valueInteger;
    } // getValue


    /**************************************************************************
     * Set the value of the parameter: float. <BR/>
     *
     * @param   value   The float value of the parameter.
     */
    public void setValue (float value)
    {
        this.p_valueFloat = value;
        this.p_isNull = false;
    } // setValue


    /**************************************************************************
     * Get the float value of the parameter. <BR/>
     *
     * @return  The float value of the parameter.
     */
    public float getValueFloat ()
    {
        return this.p_valueFloat;
    } // getValue


    /**************************************************************************
     * Set the value of the parameter: double. <BR/>
     *
     * @param   value   The double value of the parameter.
     */
    public void setValue (double value)
    {
        this.p_valueDouble = value;
        this.p_isNull = false;
    } // setValue


    /**************************************************************************
     * Get the double value of the parameter. <BR/>
     *
     * @return  The double value of the parameter.
     */
    public double getValueDouble ()
    {
        return this.p_valueDouble;
    } // getValue


    /**************************************************************************
     * Set the value of the parameter: date. <BR/>
     *
     * @param   value   The date value of the parameter.
     */
    public void setValue (Date value)
    {
        this.p_valueDate = value;
        // is value a null object
        if (value != null)
        {
            this.p_isNull = false;
        } // if
    } // setValue


    /**************************************************************************
     * Get the date value of the parameter. <BR/>
     *
     * @return  The date value of the parameter.
     */
    public Date getValueDate ()
    {
        return this.p_valueDate;
    } // getValue


    /**************************************************************************
     * Set the value of the parameter: currency. <BR/>
     *
     * @param   value   The currency value of the parameter.
     */
    public void setValue (long value)
    {
        this.p_valueCurrency = value;
        this.p_isNull = false;
    } // setValue


    /**************************************************************************
     * Get the currency value of the parameter. <BR/>
     *
     * @return  The currency value of the parameter.
     */
    public long getValueCurrency ()
    {
        return this.p_valueCurrency;
    } // getValue


    /**************************************************************************
     * Set the value of the parameter: string. <BR/>
     *
     * @param   value   The string value of the parameter.
     */
    public void setValue (String value)
    {
        this.p_valueString = value;
        // is value a null object
        if (value != null)
        {
            this.p_isNull = false;
        } // if
    } // setValue


    /**************************************************************************
     * Get the string value of the parameter. <BR/>
     *
     * @return  The string value of the parameter.
     */
    public String getValueString ()
    {
        return this.p_valueString;
    } // getValue


    /**************************************************************************
     * Set the value of the parameter: varchar. <BR/>
     *
     * @param   value   The varchar value of the parameter.
     */
    public void setValue (char[] value)
    {
        this.p_valueVarChar = value;
        this.p_isNull = false;
    } // setValue


    /**************************************************************************
     * Get the varchar value of the parameter. <BR/>
     *
     * @return  The varchar value of the parameter.
     */
    public char[] getValueVarChar ()
    {
        return this.p_valueVarChar;
    } // getValue


    /**************************************************************************
     * Set the value of the parameter: object. <BR/>
     *
     * @param   value   The object value of the parameter.
     */
    public void setValue (Object value)
    {
        this.p_valueObject = value;
        // is value a null object
        if (value != null)
        {
            this.p_isNull = false;
        } // if
    } // setValue


    /**************************************************************************
     * Get the object value of the parameter. <BR/>
     *
     * @return  The object value of the parameter.
     */
    public Object getValueObject ()
    {
        return this.p_valueObject;
    } // getValue


    /**************************************************************************
     * Set the value of the parameter: byte[]. <BR/>
     *
     * @param   value   The object value of the parameter.
     */
    public void setValue (byte[] value)
    {
        this.p_valueVarByte = value;
        this.p_isNull = false;
        // convert to compatibles: String
        this.p_valueString = SQLHelpers.byteArrayToString (value);
    } // setValue


    /**************************************************************************
     * Get the byte[] value of the parameter. <BR/>
     *
     * @return  The object value of the parameter.
     */
    public byte[] getValueVarByte ()
    {
        return this.p_valueVarByte;
    } // getValue

} // class Parameter

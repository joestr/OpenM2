/*
 * Class: QueryParameter.java
 */

// package:
package ibs.obj.query;

// imports:
import ibs.BaseObject;
import ibs.obj.query.QueryConstants;


/******************************************************************************
 * This class is used for in parameters for one querycreator. <BR/>
 *
 * @version     $Id: QueryParameter.java,v 1.9 2010/04/15 15:31:13 rburgermann Exp $
 *
 * @author      Andreas Jansa (BW) 040201
 ******************************************************************************
 */
public class QueryParameter extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: QueryParameter.java,v 1.9 2010/04/15 15:31:13 rburgermann Exp $";


    /**
     * Position of input parameter in query creator.
     */
    protected int pos = -1;

    /**
     *  Name of parameter.
     */
    protected String name = null;

    /**
     *  Mulitlang name of parameter.
     */
    protected String mlName = null;

    /**
     *  Multilang description of parameter.
     */
    protected String mlDescription = null;

    /**
     *  Value of parameter.
     */
    protected String value = null;

    /**
     *  Range value of parameter.
     */
    protected String rangeValue = null;

    /**
     *  Type of parameter (all query fieldtypes are possible).
     */
    protected String type = null;

    /**
     *  Matchtype for sqlcondition in query.
     */
    protected String match = QueryConstants.MATCH_NONE;

    /**
     * Indicates if the value was set. <BR/>
     * Default: <CODE>false</CODE>
     */
    protected boolean p_isValueSet = false;


    /**************************************************************************
     * get predefined position of parameter. <BR/>
     *
     * @return  The position.
     */
    public int getPos ()
    {
        return this.pos;
    } // getPos


    /**************************************************************************
     * set position. <BR/>
     *
     * @param   val     String to be set for parameter name.
     */
    public void setPos (int val)
    {
        this.pos = val;
    } // setPos


    /**************************************************************************
     * get name of parameter. <BR/>
     *
     * @return  Name of parameter.
     */
    public String getName ()
    {
        return this.name;
    } // getName


    /**************************************************************************
     * set name. <BR/>
     *
     * @param   val     String to be set for parameter name.
     */
    public void setName (String val)
    {
        this.name = val;
    } // setName

    
    /**************************************************************************
     * get multilang name of parameter. <BR/>
     *
     * @return  multilang name of parameter.
     */
    public String getMlName ()
    {
        return this.mlName;
    } // getMlName


    /**************************************************************************
     * set mulitlang name of parameter. <BR/>
     *
     * @param   val     String to be set for multilang parameter name.
     */
    public void setMlName (String val)
    {
        this.mlName = val;
    } // setMlName


    /**************************************************************************
     * get multilang description of parameter. <BR/>
     *
     * @return  multilang description of parameter.
     */
    public String getMlDescription ()
    {
        return this.mlDescription;
    } // getMlDescription


    /**************************************************************************
     * set mulitlang description of parameter. <BR/>
     *
     * @param   val     String to be set for multilang parameter description.
     */
    public void setMlDescription (String val)
    {
        this.mlDescription = val;
    } // setMlDescription

    
    /**************************************************************************
     * get value of parameter. <BR/>
     *
     * @return  Value of parameter.
     */
    public String getValue ()
    {
        return this.value;
    } // getValue


    /**************************************************************************
     * set value. <BR/>
     *
     * @param   val     String to be set for parameter value.
     */
    public void setValue (String val)
    {
        this.value = val;
        this.p_isValueSet = true;
    } // setValue


    /**************************************************************************
     * get range value of parameter. <BR/>
     *
     * @return  Range value of parameter.
     */
    public String getRangeValue ()
    {
        return this.rangeValue;
    } // getRangeValue


    /**************************************************************************
     * set range value. <BR/>
     *
     * @param   val     String to be set for parameter value.
     */
    public void setRangeValue (String val)
    {
        this.rangeValue = val;
    } // setRangeValue


    /**************************************************************************
     * get type of parameter. <BR/>
     *
     * @return  Type of parameter.
     */
    public String getType ()
    {
        return this.type;
    } // getType


    /**************************************************************************
     * set type. <BR/>
     *
     * @param   val     String to be set for parameter type.
     */
    public void setType (String val)
    {
        this.type = val;
    } // setType


    /**************************************************************************
     * get matchtype of parameter. <BR/>
     *
     * @return  Matchtype of parameter.
     */
    public String getMatchType ()
    {
        return this.match;
    } // getMatchType


    /**************************************************************************
     * set matchtype. <BR/>
     *
     * @param   value   String to be set for parameter matchtype
     *                  -> use {@link ibs.obj.query.QueryConstants
     *                          QueryConstants}.MATCH_* constants.
     */
    public void setMatchType (String value)
    {
        this.match = value;
    } // setMatchType


    /**************************************************************************
     * Set complete range. <BR/>
     * The match type is set to <CODE>null</CODE>.
     *
     * @param   value       Value to be set.
     * @param   rangeValue  Range value to be set.
     */
    public void setRange (String value, String rangeValue)
    {
        this.value = value;
        this.rangeValue = rangeValue;
        this.match = null;
        this.p_isValueSet = true;
    } // setRange

} // class QueryParameter

/*
 * Class: ResultElement.java
 */

// package:
package ibs.obj.query;

// imports:
import ibs.BaseObject;


/******************************************************************************
 * . <BR/>
 *
 * @version     $Id: ResultElement.java,v 1.9 2010/04/15 15:31:13 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ) 010130
 ******************************************************************************
 */
public class ResultElement extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ResultElement.java,v 1.9 2010/04/15 15:31:13 rburgermann Exp $";


    /**
     *  name of mapped attribute in query. <BR/>
     */
    protected String attribute = null;

    /**
     *  name of ResultElement (defined in queryCreator). <BR/>
     */
    protected String name = null;

    /**
     *  resultvalue of ResultElement
     */
    protected String value = null;

    /**
     *  type of ResultElement (defined in queryCreator). <BR/>
     */
    protected String type = null;

    /**
     *  modifier for type of ResultElement (defined in queryCreator). <BR/>
     */
    protected boolean multiple = false;

    /**
     *  multilang name of ResultElement (defined in queryCreator). <BR/>
     */
    protected String mlName = null;

    /**
     *  multilang description of ResultElement (defined in queryCreator). <BR/>
     */
    protected String mlDescription = null;

    /**************************************************************************
     * get name of mapped queryattribute. <BR/>
     *
     * @return  name of mapped queryattribute.
     */
    public String getAttribute ()
    {
        return this.attribute;
    } // getAttribute


    /**************************************************************************
     * set name of queryattribute mapped to this ResultElement. <BR/>
     *
     * @param   val     name of queryattribute mapped to this ResultElement.
     */
    public void setAttribute (String val)
    {
        this.attribute = val;
    } // setAttribute


    /**************************************************************************
     * get name or ResultElement which is defined in QueryCreator. <BR/>
     *
     * @return  name of ResultElement.
     */
    public String getName ()
    {
        return this.name;
    } // getName


    /**************************************************************************
     * set name of ResultElement. <BR/>
     *
     * @param   val     name of ResultElement.
     */
    public void setName (String val)
    {
        this.name = val;
    } // setName


    /**************************************************************************
     * get resultvalue of ResultElement. <BR/>
     *
     * @return  resultvalue of ResultElement.
     */
    public String getValue ()
    {
        return this.value;
    } // getValue


    /**************************************************************************
     * set resultvalue of ResultElement. <BR/>
     *
     * @param           val         resultvalue of ResultElement.
     */
    public void setValue (String val)
    {
        this.value = val;
    } // setValue


    /**************************************************************************
     * get type of ResultElement. <BR/>
     * (possible are all COLUMNTYPE_..s in QueryConstants.
     *
     * @return  type of ResultElement.
     */
    public String getType ()
    {
        return this.type;
    } // getType


    /***************************************************************************
     * set type of ResultElement. <BR/>
     *
     * @param   val     type of ResultElement. (possible are all COLUMNTYPE_..s
     *                  in QueryConstants.
     */
    public void setType (String val)
    {
        this.type = val;
    } // setType

    /**************************************************************************
     * get modifier for type of ResultElement. <BR/>
     * (see COLUMNTYPE_MODIFIER_... in QueryConstants)
     *
     * @return  multiple.
     */
    public boolean getMultiple ()
    {
        return this.multiple;
    } // getMultiple


    /***************************************************************************
     * set if the ResultElement contains multiple values. <BR/>
     *
     * @param   multiple    Defines if ResultElement contains multiple values.
     */
    public void setMultiple (boolean multiple)
    {
        this.multiple = multiple;
    } // setMultiple

    /**************************************************************************
     * get all data within ResultElement in a String. <BR/>
     *
     * @return  String for debugging with all data of this ResultElement.
     */
    public String toString ()
    {
        return "ResultElement [attribute = " + this.attribute + ", name = " +
            this.name + ", type = " + this.type + ", multiple = " + this.multiple + ", value = " +
            this.value + "]";
    } // setType

    
    /**************************************************************************
     * get multilang name of ResultElement which is defined in QueryCreator. <BR/>
     *
     * @return  multilang name of ResultElement.
     */
    public String getMlName ()
    {
        return this.mlName;
    } // getMlName


    /**************************************************************************
     * set multilang name of ResultElement. <BR/>
     *
     * @param   val     multilang name of ResultElement.
     */
    public void setMlName (String val)
    {
        this.mlName = val;
    } // setMlName

    
    /**************************************************************************
     * get multilang description of ResultElement which is defined in 
     * QueryCreator. <BR/>
     *
     * @return  multilang description of ResultElement.
     */
    public String getMlDescription ()
    {
        return this.mlDescription;
    } // getMlDescription


    /**************************************************************************
     * set multilang description of ResultElement. <BR/>
     *
     * @param   val     multilang description of ResultElement.
     */
    public void setMlDescription (String val)
    {
        this.mlDescription = val;
    } // setMlDescription

    
} // class ResultElement

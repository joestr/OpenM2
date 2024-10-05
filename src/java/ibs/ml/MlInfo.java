/*
 * Class: MlInfo
 */

// package:
package ibs.ml;


// imports:


/******************************************************************************
 * This object holds the multilang info for one locale. <BR/>
 *
 * @version     $Id: MlInfo.java,v 1.2 2012/01/10 12:01:31 rburgermann Exp $
 *
 * @author      Bernhard Tatzmann (BT)
 ******************************************************************************
 */
public class MlInfo
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MlInfo.java,v 1.2 2012/01/10 12:01:31 rburgermann Exp $";
    
    /**
     * Contains the name
     */
    private String name;

    /**
     * Contains the description
     */
    private String description;

    /**
     * Contains the unit
     */
    private String unit;

    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////
    
    /**************************************************************************
     * Creates a new MlInfo.
     */
    public MlInfo ()
    {
        setName (null);
        setDescription (null);
        setUnit (null);
    } // MlInfo
    
    
    /**************************************************************************
     * Creates a new MlInfo.
     *
     * @param name             The name
     */
    public MlInfo (String name)
    {
        setName (name);
        setDescription (null);
        setUnit (null);
    } // MlInfo
    
    
    /**************************************************************************
     * Creates a new MlInfo.
     *
     * @param name             The name
     * @param description      The description
     */
    public MlInfo (String name, String description)
    {
        setName (name);
        setDescription (description);
        setUnit (null);
    } // MlInfo

    
    /**************************************************************************
     * Creates a new MlInfo.
     *
     * @param name             The name
     * @param description      The description
     * @param unit             The unit
     */
    public MlInfo (String name, String description, String unit)
    {
        setName (name);
        setDescription (description);
        setUnit (unit);
    } // MlInfo


    ///////////////////////////////////////////////////////////////////////////
    // methods
    ///////////////////////////////////////////////////////////////////////////
    
    
    /**************************************************************************
     * Sets the name.
     *
     * @param name Name to set
     */
    public void setName (String name)
    {
        this.name = name;
    } // setName
    
    
    /**************************************************************************
     * Sets the description.
     *
     * @param description Description to set
     */
    public void setDescription (String description)
    {
        this.description = description;
    } // setDescription


    /**************************************************************************
     * Sets the unit.
     *
     * @param unit Unit to set
     */
    public void setUnit (String unit)
    {
        this.unit = unit;
    } // setUnit

    
    /**************************************************************************
     * Get the name
     *
     * @return    The name
     */
    public String getName ()
    {
        return name;
    } // getName
    
    
    /**************************************************************************
     * Get the description
     *
     * @return    The description
     */
    public String getDescription ()
    {
        return description;
    } // getDescription

    
    /**************************************************************************
     * Get the unit
     *
     * @return    The unit
     */
    public String getUnit ()
    {
        return unit;
    } // getUnit

    
    /***************************************************************************
     * Returns the string representation of this object. <BR/>
     * The name, description and unit are concatenated to create a string
     * representation according to name, description, unit.
     *
     * @return  String represention of the object.
     */
    public final String toString ()
    {
        // compute the string and return it:
        return this.getName () + ", " + this.getDescription () + ", " + this.getUnit();
    } // toString
} // MultilangTextInfo

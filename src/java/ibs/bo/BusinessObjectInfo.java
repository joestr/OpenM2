/*
 * Class: BusinessObjectInfo.java
 */

// package:
package ibs.bo;

//imports:
import ibs.bo.OID;
import ibs.io.Environment;


/******************************************************************************
 * This class represents the common meta information of an object. <BR/>
 * It is intended to be used when the whole object is not necessary or not
 * available, e.g. for holding query results when searching for objects. <BR/>
 * The properties containing the object information are public to make direct
 * possible and fast.
 *
 * @version     $Id: BusinessObjectInfo.java,v 1.3 2010/08/31 12:31:31 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 24.08.2009
 * @since       3.0.0
 ******************************************************************************
 */
public class BusinessObjectInfo
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BusinessObjectInfo.java,v 1.3 2010/08/31 12:31:31 rburgermann Exp $";


    /**
     * Oid of object. <BR/>
     */
    public OID p_oid = null;

    /**
     * State of the object. <BR/>
     */
    public int p_state = States.ST_UNKNOWN;

    /**
     * Name of the object. <BR/>
     */
    public String p_name = null;

    /**
     * Type name of the object. <BR/>
     */
    public String p_typeName = null;

    /**
     * Type description of the object. <BR/>
     */
    public String p_description = null;

    /**
     * The object itself. <BR/>
     */
    private BusinessObject p_obj = null;



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Create instance of class BusinessObjectInfo. <BR/>
     *
     * @param   oid         Oid of the object.
     * @param   state       State of the object.
     * @param   name        Name of the object.
     * @param   typeName    Type name.
     * @param   description Description of the object.
     */
    public BusinessObjectInfo (OID oid, int state, String name, String typeName, String description)
    {
        this.p_oid = oid;
        this.p_state = state;
        this.p_name = name;
        this.p_typeName = typeName;
        this.p_description = description;
    } // BusinessObjectInfo



    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get the oid of the object. <BR/>
     *
     * @return  The oid of the object.
     */
    public OID getOid ()
    {
        // get the property value and return the result:
        return this.p_oid;
    } // getOid


    /**************************************************************************
     * Set the oid of the object. <BR/>
     *
     * @param   oid     The oid to be set.
     */
    public void setOid (OID oid)
    {
        // set the property value:
        this.p_oid = oid;
    } // setOid


    /**************************************************************************
     * Get the state of the object. <BR/>
     *
     * @return  The state of the object.
     */
    public int getState ()
    {
        // get the property value and return the result:
        return this.p_state;
    } // getState


    /**************************************************************************
     * Set the state of the object. <BR/>
     *
     * @param   state   The state to be set.
     */
    public void setState (int state)
    {
        // set the property value:
        this.p_state = state;
    } // setState


    /**************************************************************************
     * Get the name of the object. <BR/>
     *
     * @return  The name of the object.
     */
    public String getName ()
    {
        // get the property value and return the result:
        return this.p_name;
    } // getName


    /**************************************************************************
     * Set the name of the object. <BR/>
     *
     * @param   name    The name to be set.
     */
    public void setName (String name)
    {
        // set the property value:
        this.p_name = name;
    } // setName


    /**************************************************************************
     * Get the type name of the object. <BR/>
     *
     * @return  The type name of the object.
     */
    public String getTypeName ()
    {
        // get the property value and return the result:
        return this.p_typeName;
    } // getTypeName


    /**************************************************************************
     * Set the type name of the object. <BR/>
     *
     * @param   typeName    Type name to be set.
     */
    public void setTypeName (String typeName)
    {
        // set the property value:
        this.p_typeName = typeName;
    } // setTypeName

    
    /**************************************************************************
     * Get the description of the object. <BR/>
     *
     * @return  The description of the object.
     */
    public String getDescription ()
    {
        // get the property value and return the result:
        return this.p_description;
    } // getDescription


    /**************************************************************************
     * Set the description of the object. <BR/>
     *
     * @param   description    Description to be set.
     */
    public void setDescription (String description)
    {
        // set the property value:
        this.p_description = description;
    } // setDescription

    
    /**************************************************************************
     * Get the object itself. <BR/>
     * If the object was not yet instantiated is is retrieved through the
     * cache.
     * In each case the environment is set within the object.
     *
     * @param   env     The current environment.
     *
     * @return  The object.
     *          <CODE>null</CODE> if the object does not exist.
     */
    public BusinessObject getObject (Environment env)
    {
        // check if the object is already known:
        if (this.p_obj == null)
        {
            // get the object out of the cache:
            this.p_obj = BOHelpers.getObject (this.p_oid, env, false, false, false);
        } // if
        else
        {
            // ensure that the object has the correct environment:
            this.p_obj.setEnv (env);
        } // else

        // get the property value and return the result:
        return this.p_obj;
    } // getObject

} // class BusinessObjectInfo

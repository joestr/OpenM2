/*
 * Class: FielRefInfo.java
 */

// package:
package ibs.di;

// imports:
import ibs.BaseObject;


/******************************************************************************
 * This class holds all information regarding to a FIELDREF field within a
 * form template definition or a COLUMN field within a container template
 * definition. <BR/>
 * Note that this is not very proper and should be separated into two
 * different classes.<BR/>
 *
 * @author      Andreas Jansa (AJ), 011206
 ******************************************************************************
 */
public class FieldRefInfo extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FieldRefInfo.java,v 1.6 2008/09/17 16:46:06 kreimueller Exp $";

    /**
     * Fieldtype unkown. <BR/>
     */
    public static final int TYPE_UNKNOWN = -1;

    /**
     * Fieldtype standard. <BR/>
     */
    public static final int TYPE_STANDARD = 0;

    /**
     * Fieldtype system. <BR/>
     * This means that it references a system value.<BR/>
     */
    public static final int TYPE_SYSTEM = 1;

    /**
     * Fieldtype extended. <BR/>
     * This means that the columns references an extension query column
     * in an container.<BR/>
     */
    public static final int TYPE_EXTENDED = 2;

    /**
     * Column type. <BR/>
     */
    private int p_type = FieldRefInfo.TYPE_UNKNOWN;

    /**
     * Name of subtag in FIELDREF VALUE. <BR/>
     */
    private String p_name = null;

    /**
     * Token of subtag in FIELDREF VALUE. <BR/>
     */
    private String p_token = null;


    /**************************************************************************
     * Check if this is a system field. <BR/>
     *
     * @return  <CODE>true</CODE> if the field is a system field,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isSysField ()
    {
        return this.p_type == FieldRefInfo.TYPE_SYSTEM;
    } // isSysField


    /**************************************************************************
     * Check if this is an extended field. <BR/>
     *
     * @return  <CODE>true</CODE> if the field is an extended field,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isExtendedField ()
    {
        return this.p_type == FieldRefInfo.TYPE_EXTENDED;
    } // isExtendedField


    /**************************************************************************
     * Check if this is an unkwown field type. <BR/>
     *
     * @return  <CODE>true</CODE> if the field is unkown
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isUnkown ()
    {
        return this.p_type == FieldRefInfo.TYPE_UNKNOWN;
    } // isUnkown


    /**************************************************************************
     * Get the type of the column. <BR/>
     *
     * @return  The type of the column.
     *          <CODE>null</CODE> if no name was set.
     */
    public int getType ()
    {
        return this.p_type;
    } // getType


    /**************************************************************************
     * Set the type.<BR/>
     *
     * @param type the p_type to set
     */
    public void setType (int type)
    {
        this.p_type = type;
    } // setType


    /**************************************************************************
     * Get the name. <BR/>
     *
     * @return  The name.
     *          <CODE>null</CODE> if no name was set.
     */
    public String getName ()
    {
        return this.p_name;
    } // getName


    /**************************************************************************
     * Set the name.<BR/>
     *
     * @param name the p_name to set
     */
    public void setName (String name)
    {
        this.p_name = name;
    } // setName


    /**************************************************************************
     * Get the token. <BR/>
     *
     * @return  The token.
     *          <CODE>null</CODE> if no token was defined.
     */
    public String getToken ()
    {
        return this.p_token;
    } // getToken


    /**************************************************************************
     * Set the token.<BR/>
     *
     * @param token the p_token to set
     */
    public void setToken (String token)
    {
        this.p_token = token;
    } // setToken


    /**************************************************************************
     * Creates an FieldRefInfo. <BR/>
     */
    public FieldRefInfo ()
    {
        this.p_type = FieldRefInfo.TYPE_UNKNOWN;
        this.p_name = null;
        this.p_token = null;
    } // FieldRefInfo


    /**************************************************************************
     * Creates an FieldRefInfo. <BR/>
     *
     * @param type            type of the column
     * @param name          name of field.
     * @param token         token to be shown to user for this field.
     */
    public FieldRefInfo (int type, String name, String token)
    {
        this.p_type = type;
        this.p_name = name;
        this.p_token = token;
    } // FieldRefInfo

} // class FieldRefInfo

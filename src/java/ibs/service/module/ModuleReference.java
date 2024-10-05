/*
 * Class: ModuleReference.java
 */

// package:
package ibs.service.module;

//imports:
import ibs.service.list.XMLElement;
import ibs.service.module.IModuleReference;
import ibs.tech.xml.XMLHelpers;
import ibs.util.list.ElementId;
import ibs.util.list.IElementId;
import ibs.util.list.ListException;

import java.io.File;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/******************************************************************************
 * Contains the data which are necessary to reference a module. <BR/>
 *
 * @version     $Id: ModuleReference.java,v 1.5 2007/07/31 19:13:58 kreimueller Exp $
 *
 * @author      Klaus, 29.12.2003
 ******************************************************************************
 */
public class ModuleReference extends XMLElement implements IModuleReference
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ModuleReference.java,v 1.5 2007/07/31 19:13:58 kreimueller Exp $";


    /**
     * The version of the module. <BR/>
     */
    private ModuleVersion p_version = null;

    /**
     * Version match. <BR/>
     * <PRE>
     * x ... identical, y ... greater, z ... any value
     * perfect:        x.x.x
     * equivalent:     x.x.x or x.x.y
     * compatible:     x.x.x or x.x.y or x.y.z
     * greaterOrEqual: x.x.x or x.x.y or x.y.z or y.z.z
     * </PRE>
     */
    private short p_matchType = 0;

    /**
     * The object to which this is a reference. <BR/>
     */
    public Module p_referencedElement = null;

    /**
     * Start of exception message. <BR/>
     */
    private static final String MSG_ERROR_START = "Module reference \"";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get the version. <BR/>
     *
     * @return  The version if already set or <CODE>null</CODE> if not set.
     */
    public ModuleVersion getVersion ()
    {
        return this.p_version;
    } // getVersion


    /**************************************************************************
     * Creates a ModuleReference object. <BR/>
     *
     * @param   id          Id of the element.
     * @param   name        The element's name.
     *
     * @throws  ListException
     *          An error occurred during initializing the container.
     */
    public ModuleReference (IElementId id, String name)
        throws ListException
    {
        // call constructor of super class:
        super (id, name);

        // initialize the other instance properties:
    } // ModuleReference


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Check if the module is active. <BR/>
     *
     * @return  <CODE>true</CODE> if the module is active,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isActive ()
    {
        return true;
    } // isActive


    /**************************************************************************
     * Get the element type specific data out of the actual element data. <BR/>
     * This method is used to get all values of one element out of the
     * result set. <BR/>
     * <B>example:</B>. <BR/>
     * <PRE>
     * NamedNodeMap attributes = elemData.getAttributes ();
     * this.p_value = XMLHelpers.getAttributeValue (attributes, "itemName");
     * </PRE>
     *
     * @param   elemData    The data for the element.
     * @param   dataFile    The file which contains the data.
     *
     * @throws  ListException
     *          An error occurred during parsing the element.
     */
    public void setProperties (Node elemData, File dataFile)
        throws ListException
    {
        NamedNodeMap attributes = elemData.getAttributes ();
        String str;

        str = XMLHelpers.getAttributeValue (attributes, "id");
        this.init (new ElementId (str), str);
        try
        {
            this.p_version = new ModuleVersion (XMLHelpers.getAttributeValue (
                attributes, "version"));
        } // try
        catch (ModuleVersionException e)
        {
            throw new ListException (e);
        } // catch

        str = XMLHelpers.getAttributeValue (attributes, "match");
        if (str.equals (ModuleConstants.MATCHSTR_PERFECT))
        {
            this.p_matchType = ModuleConstants.MATCH_PERFECT;
        } // if
        else if (str.equals (ModuleConstants.MATCHSTR_EQUIVALENT))
        {
            this.p_matchType = ModuleConstants.MATCH_EQUIVALENT;
        } // else if
        else if (str.equals (ModuleConstants.MATCHSTR_COMPATIBLE))
        {
            this.p_matchType = ModuleConstants.MATCH_COMPATIBLE;
        } // else if
        else if (str.equals (ModuleConstants.MATCHSTR_GREATEROREQUAL))
        {
            this.p_matchType = ModuleConstants.MATCH_GREATEROREQUAL;
        } // else if
        else                            // invalid match type
        {
            throw new ListException ("Invalid match type: \"" + str + "\"");
        } // else invalid match type
    } // setProperties


    /**************************************************************************
     * Resolve the dependencies between this element and other elements out of
     * the container. <BR/>
     * If there are any dependencies ensure that these dependencies are object
     * references instead of (textual) descriptions.
     *
     * @param   elems       The container with the other elements.
     *
     * @throws  ListException
     *          There occurred an error during the operation.
     *          Possible causes:
     *          <LI>The referenced module was not found.</LI>
     *          <LI>The referenced module is not active.</LI>
     *          <LI>The referenced module has an incompatible version.</LI>
     */
    public void resolveDependencies (ModuleContainer elems)
        throws ListException
    {
        Module actElem = null;          // the actual element

        // search for the actual id within the container:
        if ((actElem = elems.get (this.getId ())) != null)
        {
            // check the version:
            if (actElem.isActive ())
            {
                // check the version:
                if (this.p_version.isMatch (actElem.getVersion (), this.p_matchType))
                {
                    // set the referenced element:
                    this.p_referencedElement = actElem;
                } // if
                else                    // version mismatch
                {
                    throw new ListException (
                        "Version mismatch for module reference \"" + this.toString () + "\".");
                } // else version mismatch
            } // if
            else                        // element not active
            {
                throw new ListException (ModuleReference.MSG_ERROR_START +
                    this.toString () + "\" is not active.");
            } // else element not active
        } // if
        else                        // reference not resolved
        {
            throw new ListException (ModuleReference.MSG_ERROR_START +
                this.toString () + "\" could not be resolved.");
        } // else reference not resolved
    } // resolveDependencies


    /**************************************************************************
     * Check if the actual module depends on another one. <BR/>
     *
     * @param   otherObj    The other object to be checked.
     *
     * @return  <CODE>true</CODE> if the object depends on the other object,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean depends (Module otherObj)
    {
        return this.p_referencedElement.equals (otherObj);
    } // depends


    /**************************************************************************
     * Returns the string representation of the id and the version. <BR/>
     * The id and the version name are concatenated to create a string
     * representation according to <CODE>"id_version"</CODE>. <BR/>
     * e.g.: <CODE>"ibsbase_2.4.1"</CODE>
     *
     * @return  String represention of the object.
     */
    public String getIdVersion ()
    {
        // compute the string and return it:
        return this.getId () + "_" + this.p_version;
    } // getIdVersion


    /**************************************************************************
     * Returns the string representation of this object. <BR/>
     * The id and the name are concatenated to create a string
     * representation according to "id, name".
     *
     * @return  String represention of the object.
     */
    public String toString ()
    {
        // compute the string and return it:
        return this.getIdVersion ();
    } // toString

} // class ModuleReference

/*
 * Class: ConfValue.java
 */

// package:
package ibs.service.module;

// imports:
import ibs.service.list.XMLElement;
import ibs.service.module.ModuleVersion;
import ibs.service.module.ModuleVersionException;
import ibs.tech.xml.XMLHelpers;
import ibs.util.list.ElementId;
import ibs.util.list.IElementId;
import ibs.util.list.ListException;

import java.io.File;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


/******************************************************************************
 * A configuration variable. <BR/>
 *
 * @version     $Id: ConfValue.java,v 1.4 2007/07/31 19:13:58 kreimueller Exp $
 *
 * @author      Klaus, 17.12.2003
 ******************************************************************************
 */
public class ConfValue extends XMLElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ConfValue.java,v 1.4 2007/07/31 19:13:58 kreimueller Exp $";


    /**
     * The module id. <BR/>
     */
    private String p_moduleId = null;

    /**
     * The module version. <BR/>
     */
    public ModuleVersion p_moduleVersion = null;

    /**
     * The actual value of the configuration variable. <BR/>
     */
    public String p_value = null;



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a ConfValue object. <BR/>
     *
     * @param   id          Id of the element.
     * @param   name        The element's name.
     */
    public ConfValue (IElementId id, String name)
    {
        // call constructor of super class:
        super (id, name);

        // initialize the other instance properties:
    } // ConfValue


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

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
        NamedNodeMap moduleAttributes = null; // attributes of module
        Text valueNode = (Text) elemData.getFirstChild ();
        String value = null;
        String name = null;
        String moduleId = null;

        // check if there was a value set:
        if (valueNode != null)
        {
            // use node value:
            value = valueNode.getNodeValue ();
        } // if
        else                            // no value set
        {
            // use the empty string:
            value = "";
        } // else no value set

        moduleAttributes = elemData.getParentNode ().getAttributes ();

        name = XMLHelpers.getAttributeValue (attributes, "name");
        moduleId = XMLHelpers.getAttributeValue (moduleAttributes, "moduleid");

        this.init (new ElementId (moduleId + "." + name), name);
        this.p_moduleId = moduleId;
        try
        {
            this.p_moduleVersion = new ModuleVersion (XMLHelpers
                .getAttributeValue (moduleAttributes, "moduleversion"));
        } // try
        catch (ModuleVersionException e)
        {
            throw new ListException (e);
        } // catch
        this.p_value = value;
// KR TODO set module
//            this.p_module = ???
    } // setProperties


    /**************************************************************************
     * Returns the string representation of the id and the version of the
     * module. <BR/>
     * The id and the version name are concatenated to create a string
     * representation according to <CODE>"id_version"</CODE>. <BR/>
     * e.g.: <CODE>"ibsbase_2.4.1"</CODE>
     *
     * @return  String represention of the object.
     */
    public String getIdVersion ()
    {
        // compute the string and return it:
        return this.p_moduleId + "_" + this.p_moduleVersion;
    } // getIdVersion


    /**************************************************************************
     * Returns the string representation of this object. <BR/>
     * The id and the value are used to create a string representation
     * according to "id, value".
     *
     * @return  String represention of the object.
     */
    public String toString ()
    {
        // compute the string and return it:
        return this.getId ().getIdStr () + "," + this.p_value;
    } // toString

} // class ConfValue

/*
 * Class: XMLHelpers.java
 */

// package:
package ibs.tech.xml;

//imports:
import ibs.io.Environment;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/******************************************************************************
 * This class contains some helper methods for working with xml. <BR/>
 *
 * @version     $Id: XMLHelpers.java,v 1.4 2009/09/09 22:12:53 kreimueller Exp $
 *
 * @author      Klaus, 17.12.2003
 ******************************************************************************
 */
public abstract class XMLHelpers extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XMLHelpers.java,v 1.4 2009/09/09 22:12:53 kreimueller Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get the value of one attribute. <BR/>
     *
     * @param   attributes  The list of attributes.
     * @param   name        The name of the attribute.
     *
     * @return  The value of the attribute.
     *          <CODE>null</CODE> if the attribute does not exist.
     */
    public static String getAttributeValue (NamedNodeMap attributes, String name)
    {
        Node node = attributes.getNamedItem (name); // the node

        // check if the attribute exists:
        if (node != null)               // the attribute exists?
        {
            // get the value and return it:
            return node.getNodeValue ();
        } // if the attribute exists

        // return the default value:
        return null;
    } // getAttributeValue


    /**************************************************************************
     * Get the value of one attribute as integer. <BR/>
     *
     * @param   attributes  The list of attributes.
     * @param   name        The name of the attribute.
     *
     * @return  The integer value of the attribute.
     *          <CODE>0</CODE> if the attribute does not exist;
     *          <CODE>-1</CODE> if it is not a valid integer.
     */
    public static int getAttributeValueInt (NamedNodeMap attributes, String name)
    {
        String value = XMLHelpers.getAttributeValue (attributes, name);
        int retVal = 0;                 // the return value

        // check if the value exists:
        if (value != null)              // value exists?
        {
            try
            {
                // convert the value to integer:
                retVal = Integer.parseInt (value);
            } // try
            catch (NumberFormatException e) // parameter not correctly set?
            {
                retVal = -1;            // set not-exists-or-invalid value
            } // catch
        } // if value exists

        // return the result:
        return retVal;
    } // getAttributeValueInt


    /**************************************************************************
     * Retrieve the node with the given name from the given document.
     *
     * @param   document    The document.
     * @param   nodeName    The name of the node to be retrieved.
     * @param   env         Environment of the application.
     *
     * @return  The found node.
     *          <CODE>null</CODE> if the node was not found.
     */
    public static Node getNodeByName (Document document, String nodeName,
                                      Environment env)
    {
        NodeList nodes = document.getElementsByTagName (nodeName);

        if (nodes.getLength () > 0)     // was the node found ?
        {
            return nodes.item (0);
        } // if was the node found

        // node not found:
        return null;
    } // getNodeByName



    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////


} // class XMLHelpers

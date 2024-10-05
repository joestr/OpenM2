/*
 * Class: XMLElementContainerLoader.java
 */

// package:
package ibs.service.list;

// imports:
import ibs.service.list.FileElementContainerLoader;
import ibs.tech.xml.XMLReader;
import ibs.tech.xml.XMLReaderException;
import ibs.util.list.ElementContainer;
import ibs.util.list.ListException;

import java.io.File;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/******************************************************************************
 * This class is responsible for loading the data for a specific element
 * container out of xml files. <BR/>
 *
 * @version     $Id: XMLElementContainerLoader.java,v 1.7 2007/07/23 12:34:14 kreimueller Exp $
 *
 * @author      Klaus, 15.11.2003
 *
 * @param   <EC>    The container for which this container loader is defined.
 *                  Must be a subclass of ElementContainer&lt;E>.
 * @param   <E>     The class for which this container loader is defined.
 *                  Must be a subclass of Element.
 ******************************************************************************
 */
public class XMLElementContainerLoader<EC extends ElementContainer<E>, E extends XMLElement>
    extends FileElementContainerLoader<EC, E>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XMLElementContainerLoader.java,v 1.7 2007/07/23 12:34:14 kreimueller Exp $";


    /**
     * The name of the tag which contains the relevant data. <BR/>
     * Default: <CODE>"*"</CODE>
     */
    private String p_tagName = "*";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a XMLElementContainerLoader object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   container   The ElementContainer in which to load the information.
     */
    public XMLElementContainerLoader (EC container)
    {
        // call constructor of super class:
        super (container);
    } // XMLElementContainerLoader


    /**************************************************************************
     * Creates a XMLElementContainerLoader object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   container   The ElementContainer in which to load the information.
     * @param   rootDir     The root directory where to start the search.
     */
    public XMLElementContainerLoader (EC container,
                                      String rootDir)
    {
        // call constructor of super class:
        super (container, rootDir);
    } // XMLElementContainerLoader


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Set the name of the tag with the relevant data. <BR/>
     * If the name of the tag is not set the default value is <CODE>"*"</CODE>.
     *
     * @param   tagName The name of the tag.
     */
    public void setTagName (String tagName)
    {
        // set the property:
        this.p_tagName = tagName;
    } // setTagName


    /**************************************************************************
     * Get the name of the tag with the relevant data. <BR/>
     *
     * @return  The name of the tag.
     */
    protected String getTagName ()
    {
        // return the property:
        return this.p_tagName;
    } // getTagName


    /**************************************************************************
     * Load the elements of a file. <BR/>
     *
     * @param   dataFile    The file to be read.
     *
     * @return  The elements.
     *          If there are no elements the return value must be an empty
     *          ElementContainer. <CODE>null</CODE> is not allowed.
     *
     * @throws  ListException
     *          An error occurred in a list operation.
     */
    protected final EC loadFile (File dataFile)
        throws ListException
    {
        EC elems = null; // the element container
        Element rootElem;               // the root element of the document

        try
        {
            // read the file:
            rootElem = new XMLReader (dataFile, true, null).getRootElem ();

            // get the elements:
            elems = this.loadElements (rootElem, dataFile);
        } // try
        catch (XMLReaderException e)
        {
            // an error occurred - show name and info
            String message =
                "LoaderException: Xml exception in " +
                dataFile.getName () + ".\n" +
                "loader: " + this.getClass ().getName () + "\n" +
                "data file: " + dataFile.getPath () + "\n";
            throw new ListException (message, e);
        } // catch

        // return the result:
        return elems;
    } // loadFile


    /**************************************************************************
     * Load the elements of a file. <BR/>
     *
     * @param   rootElem    The root element where to start the search.
     * @param   dataFile    The file which contains the data.
     *
     * @return  The elements.
     *          If there are no elements the return value must be an empty
     *          ElementContainer. <CODE>null</CODE> is not allowed.
     *
     * @throws  ListException
     *          An error occurred in a list operation.
     */
    public EC loadElements (Element rootElem, File dataFile)
        throws ListException
    {
        EC elems = null; // the element container
        NodeList elements;              // the relevant elements
        int elementCount;               // number of elements to handle
        int i;                          // loop counter

        // create the container:
        elems = this.getContainerInstance ();

        // check if the root element is already the tag to be searched for:
        if (rootElem.getTagName ().equals (this.p_tagName))
        {
            // use the root element
            // get specific data of container element:
            elems.add (this.parseElement (rootElem, dataFile));
        } // if
        else
        {
            // get the relevant elements:
            elements = this.getRelevantElements (rootElem);
            elementCount = elements.getLength ();

            // loop through all found elements and add them to the element
            // container:
            for (i = 0; i < elementCount; i++)
            {
                // get specific data of container element:
                elems.add (this.parseElement (elements.item (i), dataFile));
            } // for i
        } // else

        // return the result:
        return elems;
    } // loadElements


    /**************************************************************************
     * Get all relevant elements for the element container. <BR/>
     * <B>examples:</B>
     * <PRE>
     * return (rootElem.getElementsByTagName (this.getTagName ()));
     * </PRE> (default)
     * or
     * <PRE>
     * return (rootElem.getElementsByTagName ("*"));
     * </PRE>
     * or
     * <PRE>
     * return (rootElem.getElementsByTagName ("tagname"));
     * </PRE>
     *
     * @param   rootElem    The root element.
     *
     * @return  A list of all nodes which are relevant.
     */
    protected NodeList getRelevantElements (Element rootElem)
    {
        // get the elements and return them:
        return rootElem.getElementsByTagName (this.p_tagName);
    } // getRelevantElements


    /**************************************************************************
     * Get the element type specific data out of the actual element data. <BR/>
     * This method is used to get all values of one element out of the
     * result set. <BR/>
     *
     * @param   elemData    The data for the element.
     * @param   dataFile    The file which contains the data.
     *
     * @return  The newly created element filled with the values out of the
     *          actual element data.
     *
     * @throws  ListException
     *          An error occurred during parsing the element.
     */
    protected E parseElement (Node elemData, File dataFile)
        throws ListException
    {
        E elem = this.getElems ().getElementInstance (0, null);

        // set the properties of the new element:
        elem.setProperties (elemData, dataFile);

        // return the element:
        return elem;
    } // parseElement

} // class XMLElementContainerLoader

/*
 * Class: ModuleReferenceContainerLoader.java
 */

// package:
package ibs.service.module;

// imports:
import ibs.service.list.XMLElementContainerLoader;
import ibs.service.module.ModuleConstants;
import ibs.util.list.ListException;

import java.io.File;

import org.w3c.dom.Node;


/******************************************************************************
 * This class is responsible for loading all modules. <BR/>
 *
 * @version     $Id: ModuleReferenceContainerLoader.java,v 1.1 2007/07/23 12:34:23 kreimueller Exp $
 *
 * @author      Klaus, 28.12.2003
 ******************************************************************************
 */
public class ModuleReferenceContainerLoader
    extends XMLElementContainerLoader<ModuleReferenceContainer, ModuleReference>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ModuleReferenceContainerLoader.java,v 1.1 2007/07/23 12:34:23 kreimueller Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a ModuleReferenceContainerLoader object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   container   The container in which to load the information.
     * @param   rootDir     The root directory where to start the search.
     */
    public ModuleReferenceContainerLoader (ModuleReferenceContainer container,
                                           String rootDir)
    {
        // call constructor of super class:
        super (container, rootDir);

        // initialize the instance's properties:
        this.setFileNameFilter (ModuleConstants.FILE_MODULE);
        this.setTagName (ModuleConstants.TAG_REQMODULE);
    } // ModuleReferenceContainerLoader


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

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
    protected ModuleReference parseElement (Node elemData, File dataFile)
        throws ListException
    {
        ModuleReference elem = null;

        // set the properties of the new element:
        elem = this.getElems ().getElementInstance (0, null);
        elem.setProperties (elemData, dataFile);

        // return the element:
        return elem;
    } // parseElement

} // class ModuleReferenceContainerLoader

/*
 * Class: ConfValueLoader.java
 */

// package:
package ibs.service.module;

// imports:
import ibs.service.list.XMLElementContainerLoader;


/******************************************************************************
 * This class is responsible for loading configuration values into the
 * configuration variables. <BR/>
 *
 * @version     $Id: ConfValueLoader.java,v 1.3 2007/07/23 12:34:23 kreimueller Exp $
 *
 * @author      Klaus, 30.12.2003
 ******************************************************************************
 */
public class ConfValueLoader
    extends XMLElementContainerLoader<ConfValueContainer, ConfValue>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ConfValueLoader.java,v 1.3 2007/07/23 12:34:23 kreimueller Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a ConfValueLoader object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   container   The container in which to load the information.
     * @param   rootDir     The root directory where to start the search.
     */
    public ConfValueLoader (ConfValueContainer container, String rootDir)
    {
        // call constructor of super class:
        super (container, rootDir);

        // initialize the instance's properties:
        this.setFileNameFilter (ModuleConstants.FILE_CONFVALUES);
        this.setTagName (ModuleConstants.TAG_CONFVALUE);
    } // ConfValueLoader


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////


} // class ConfValueLoader

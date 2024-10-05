/*
 * Class: XMLElementContainerLoader.java
 */

// package:
package ibs.service.list;

// imports:
import ibs.util.file.FileManager;
import ibs.util.file.FileHelpers;
import ibs.util.list.Element;
import ibs.util.list.ElementContainer;
import ibs.util.list.ListException;

import java.io.File;
import java.io.FilenameFilter;


/******************************************************************************
 * This class is responsible for loading the data for a specific element
 * container out of files. <BR/>
 *
 * @version     $Id: FileElementContainerLoader.java,v 1.6 2007/07/23 12:34:14 kreimueller Exp $
 *
 * @author      Klaus, 16.12.2003
 *
 * @param   <EC>    The container for which this container loader is defined.
 *                  Must be a subclass of ElementContainer&lt;E>.
 * @param   <E>     The class for which this container loader is defined.
 *                  Must be a subclass of Element.
 ******************************************************************************
 */
public abstract class FileElementContainerLoader<EC extends ElementContainer<E>, E extends Element>
    extends ElementContainerLoader<EC, E>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FileElementContainerLoader.java,v 1.6 2007/07/23 12:34:14 kreimueller Exp $";


    /**
     * The root directory for starting the search. <BR/>
     */
    private String p_rootDirName = null;

    /**
     * The filter for the directory name. <BR/>
     */
    private FilenameFilter p_dirNameFilter = null;

    /**
     * The filter for the file name. <BR/>
     */
    private FilenameFilter p_fileNameFilter = null;


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a FileElementContainerLoader object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   container   The ElementContainer in which to load the information.
     */
    public FileElementContainerLoader (EC container)
    {
        // call constructor of super class:
        super (container);
    } // FileElementContainerLoader


    /**************************************************************************
     * Creates a FileElementContainerLoader object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   container   The ElementContainer in which to load the information.
     * @param   rootDir     The root directory where to start the search.
     */
    public FileElementContainerLoader (EC container,
                                       String rootDir)
    {
        // call constructor of super class:
        super (container);

        // initialize the instance's properties:
        this.setRootDir (rootDir);
    } // FileElementContainerLoader


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Set the path of the directory where to start the search. <BR/>
     *
     * @param   rootDirName The path and name of the directory.
     */
    public void setRootDir (String rootDirName)
    {
        // set the property value:
        this.p_rootDirName = rootDirName;
    } // setDirName


    /**************************************************************************
     * Set the filter for the data directories. <BR/>
     *
     * @param   filterString    The filter string. <BR/>
     *                          <CODE>"*"</CODE> denotes all directories. <BR/>
     *                          <CODE>"*"</CODE> is the default value.
     */
    public void setFileNameFilter (String filterString)
    {
        // set the property value:
        this.p_fileNameFilter = FileManager.getFilter (filterString);
    } // setFileNameFilter


    /**************************************************************************
     * Set the filter for the data files. <BR/>
     *
     * @param   filterString    The filter string. <BR/>
     *                          <CODE>"*"</CODE> denotes all files. <BR/>
     *                          <CODE>"*"</CODE> is the default value.
     */
    public void setDirNameFilter (String filterString)
    {
        // set the property value:
        this.p_dirNameFilter = FileManager.getFilter (filterString);
    } // setDirNameFilter


    /**************************************************************************
     * Ensure that the filters are set to valid values. <BR/>
     * If a filter is not set it is set to <CODE>"*"</CODE>.
     */
    private void ensureFilters ()
    {
        // dir name filter:
        if (this.p_dirNameFilter == null)
        {
            this.p_dirNameFilter = FileManager.getFilter ("*");
        } // if

        // file name filter:
        if (this.p_fileNameFilter == null)
        {
            this.p_fileNameFilter = FileManager.getFilter ("*");
        } // if
    } // ensureFilters


    /**************************************************************************
     * Load the elements for the container. <BR/>
     * The elements are loaded out of the file which is set through
     * {@link #setFileNameFilter (String) setFileNameFilter}.
     *
     * @return  The elements.
     *          If there are no elements the return value must be an empty
     *          ElementContainer. <CODE>null</CODE> is not allowed.
     *
     * @throws  ListException
     *          An error occurred in a list operation.
     */
    protected EC loadElements ()
        throws ListException
    {
        File dir;

        // check if the file exists:
        if (FileHelpers.exists (this.p_rootDirName))
        {
            // create the file object:
            dir = new File (this.p_rootDirName);

            // get the data file and read it:
            return this.loadDir (dir);
        } // if

        System.out.println ("Data directory not found: " + this.p_rootDirName + ".");

        // create the container:
        return this.getContainerInstance ();
    } // loadElements


    /**************************************************************************
     * Load the contents of a directory. <BR/>
     *
     * @param   dir     The directory to search in.
     *
     * @return  The elements.
     *          If there are no elements the return value must be an empty
     *          ElementContainer. <CODE>null</CODE> is not allowed.
     *
     * @throws  ListException
     *          An error occurred in a list operation.
     */
    protected final EC loadDir (File dir)
        throws ListException
    {
        EC elems = null; // the element container
        File[] searchFiles = null;      // the files to be searched for

        // create the container:
        elems = this.getContainerInstance ();

        // ensure that the filters are set correctly:
        this.ensureFilters ();

        // get the files to be searched for:
        searchFiles = FileHelpers.getFilesArray (dir, this.p_dirNameFilter,
            this.p_fileNameFilter, true);

        // handle all relevant files:
        // loop through all search files; for each file
        // call the corresponding method
        for (int i = 0; i < searchFiles.length; i++)
        {
            // read the file:
            elems.addAll (this.loadFile (searchFiles[i]));
        } // for i

        // return the result:
        return elems;
    } // loadDir


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
    protected abstract EC loadFile (File dataFile)
        throws ListException;

} // class FileElementContainerLoader

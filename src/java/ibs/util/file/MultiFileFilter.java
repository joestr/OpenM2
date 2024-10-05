/*
 * Class: MultiFileFilter
 */

// package:
package ibs.util.file;

// imports:

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This filter is intended to be used if there are several different file
 * names allowed. <BR/>
 *
 * @version     $Id: MultiFileFilter.java,v 1.3 2007/07/10 09:04:13 kreimueller Exp $
 *
 * @author      Klaus, 31.12.2003
 ******************************************************************************
 */
public class MultiFileFilter extends Object implements FilenameFilter
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MultiFileFilter.java,v 1.3 2007/07/10 09:04:13 kreimueller Exp $";


    /**
     * Vector with all filters. <BR/>
     */
    private Vector<FilenameFilter> p_allFilters =
        new Vector<FilenameFilter> (5, 5);

    /**
     * Array with all filters. <BR/>
     */
    private FilenameFilter[] p_filters = null;

    /**
     * Number of filters. <BR/>
     */
    private int p_length = 0;


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a MultiFileFilter object. <BR/>
     */
    protected MultiFileFilter ()
    {
        // set properties:
    } // MultiFileFilter


    /**************************************************************************
     * Creates a MultiFileFilter object. <BR/>
     *
     * @param   allFilters  The filters.
     */
    protected MultiFileFilter (Vector<FilenameFilter> allFilters)
    {
        // set properties:
        this.setFilters (allFilters);
    } // MultiFileFilter


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Sets the filters. <BR/>
     *
     * @param   allFilters  The filters.
     */
    protected void setFilters (Vector<FilenameFilter> allFilters)
    {
        // check if there are any filters to be set:
        if (allFilters != null)
        {
            this.p_allFilters = allFilters;
            this.p_filters = (FilenameFilter[]) allFilters.toArray ();
            this.p_length = allFilters.size ();
        } // if
        else
        {
            // initialize the filters:
            this.p_allFilters = new Vector<FilenameFilter> (5, 5);
            this.createArray ();
        } // else
    } // setFilters


    /**************************************************************************
     * Adds a new filter. <BR/>
     *
     * @param   filter  The filter to be added.
     */
    protected void addFilter (FilenameFilter filter)
    {
        // check if there is a filter to be set:
        if (filter != null)
        {
            // add the new filter:
            this.p_allFilters.add (filter);
            this.createArray ();
        } // if
    } // addFilter


    /**************************************************************************
     * Create the array out of the vector. <BR/>
     */
    private void createArray ()
    {
        FilenameFilter[] filters;
        int i = 0;                      // loop counter

        // create the array:
        filters = new FilenameFilter[this.p_allFilters.size ()];

        for (Iterator<FilenameFilter> iter = this.p_allFilters.iterator (); iter.hasNext ();)
        {
            // add the actual filter:
            filters[i++] = iter.next ();
        } // for iter

        // set the new array:
        this.p_filters = filters;
        this.p_length = filters.length;
    } // createArray


    /**************************************************************************
     * Tests if a specified file should be included in a file list. <BR/>
     *
     * @param   dir     The directory in which the file was found.
     * @param   name    The name of the file.
     *
     * @return  <CODE>true</CODE> if and only if the name should be
     *          included in the file list; <CODE>false</CODE> otherwise.
     */
    public boolean accept (File dir, String name)
    {
        // loop through all filters and check for each one:
        for (int i = 0; i < this.p_length; i++)
        {
            // check the actual filter:
            if (this.p_filters[i].accept (dir, name))
            {
                // return the result:
                return true;
            } // if
        } // for i

        // return the result:
        return false;
    } // accept

} // class MultiFileFilter

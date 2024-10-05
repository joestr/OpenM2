/*
 * Class: FileManager.java
 */

// package:
package ibs.util.file;

// imports:

import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This class is used to manage files. <BR/>
 *
 * @version     $Id: FileManager.java,v 1.6 2007/07/10 09:04:13 kreimueller Exp $
 *
 * @author      Klaus, 30.12.2003
 ******************************************************************************
 */
public abstract class FileManager extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FileManager.java,v 1.6 2007/07/10 09:04:13 kreimueller Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get a file name filter. <BR/>
     * The filter string may contain <CODE>"*"</CODE> as wild card.
     * Currently supported filter types:
     * <LI>exact:     <CODE>"fullfilename"</CODE></LI>
     * <LI>any:       <CODE>"*"</CODE></LI>
     * <LI>ending:    <CODE>"*str"</CODE></LI>
     * <LI>beginning: <CODE>"str*"</CODE></LI>
     * <LI>partof:    <CODE>"*str*"</CODE></LI>
     *
     * @param   filterString    The filter string.
     *
     * @return  The filter.
     */
    public static FilenameFilter getFilter (String filterString)
    {
        // check for type of filter:
        if (filterString == null || filterString.equals ("*"))
                                        // accept all file names?
        {
            // create universal filter:
            return new UniversalFileFilter ();
        } // if accept all file names
        else if (filterString.startsWith ("*")) // beginning is not relevant?
        {
            if (filterString.endsWith ("*")) // ending is not relevant?
            {
                // create part of filter:
                return new FilePartOfFilter (filterString.substring (1, filterString.length () - 1));
            } // if ending is not relevant

            // create file ending filter:
            return new FileEndingFilter (filterString.substring (1));
        } // else if beginning is not relevant
        else if (filterString.endsWith ("*")) // ending is not relevant?
        {
            // create file beginning filter:
            return new FileBeginningFilter (filterString.substring (0, filterString.length () - 1));
        } // else if ending is not relevant
        else                            // standard filter
        {
            // create identity filter:
            return new FileIdentityFilter (filterString);
        } // else standard filter
    } // getFilter


    /**************************************************************************
     * Get a multiple file filter. <BR/>
     * This method creates a MultiFileFilter containing several filters created
     * out of the strings in the vector by calling
     * {@link #getFilter (String) getFilter}.
     *
     * @param   filters The filters to be set.
     *
     * @return  The filter.
     *
     * @see #getFilter (String)
     */
    public static FilenameFilter getFilter (Vector<String> filters)
    {
        MultiFileFilter filter = new MultiFileFilter ();

        // check if there are any filters to be set:
        if (filters != null)
        {
            for (Iterator<String> iter = filters.iterator (); iter.hasNext ();)
            {
                // create and add the new filter:
                filter.addFilter (FileManager.getFilter (iter.next ()));
            } // for iter

            // return the result:
            return filter;
        } // if

        // no filters to be set
        // create a new universal filter:
        return FileManager.getFilter ((String) null);
    } // getFilter


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////


} // class FileManager

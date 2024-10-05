/*
 * Class: SelectionList.java
 */

// package:
package ibs.bo;

// imports:
import ibs.BaseObject;


/******************************************************************************
 * This class is a helper class for getting a selection list out of the database
 * and display it at the browser..
 * <BR/>
 *
 * @version     $Id: SelectionList.java,v 1.9 2010/02/12 08:41:41 btatzmann Exp $
 *
 * @author      Rupert Thurner (RT), 980616
 ******************************************************************************
 */
public class SelectionList extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SelectionList.java,v 1.9 2010/02/12 08:41:41 btatzmann Exp $";


    // special SelectionList attributes
    /**
     * String array of Id's. <BR/>
     */
    public String[] ids = null;
    /**
     * String array of id's values. Usually text strings for helping the user selecting. <BR/>
     */
    public String[] values = null;
    /**
     * String array of grouping id's values. Usually ids for grouping selectionbox entries.
     * Can be used for combined selectionboxes. <BR/>
     */
    public String[] groupingIds = null;

    /**************************************************************************
     * Creates a SelectionList object. <BR/>
     * <BR/>
     */
    public SelectionList ()
    {
        // nothing to do
    } // SelectionList


    /**************************************************************************
     * This constructor creates a new instance of the class SelectionList.
     * <BR/>
     *
     * @param   ids     Array of Strings, containing the ids.
     * @param   values  Array of Strings, containing the selection values.
     */
    public SelectionList (String[] ids, String[] values)
    {
        this.ids = ids;
        this.values = values;
    } // SelectionList

} // class SelectionList

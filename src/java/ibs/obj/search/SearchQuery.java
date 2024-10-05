/*
 * Class: SearchQuery.java
 */

// package:
package ibs.obj.search;

// imports:
import ibs.BaseObject;

import java.util.Vector;


/******************************************************************************
 * This class represents one object of type SearchQuery. <BR/>
 *
 * @version     $Id: SearchQuery.java,v 1.7 2009/07/24 08:26:44 kreimueller Exp $
 *
 * @author      Bernd Buchegger (BB), 980512
 ******************************************************************************
 */
public class SearchQuery extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SearchQuery.java,v 1.7 2009/07/24 08:26:44 kreimueller Exp $";


    /**
     * holds a vector of filters elements for a search query
     */
    public Vector<SearchQueryElement> filters;


    /**************************************************************************
     * This constructor creates a new instance of the class SearchQuery. <BR/>
     */
    public SearchQuery ()
    {
        this.filters = new Vector<SearchQueryElement> ();

        // set the instance's attributes:
    } // SearchQuery

} // class SearchQuery

/*
 * Class: QuerySearchValueMapper.java
 */

// package:
package ibs.obj.query;

// imports:
import ibs.obj.query.QueryAttributeMapper;


/******************************************************************************
 * QuerySearchValueMapper - dataclass for mapping between columnheaders and
 * queryattributes, queryattributetypes, querysearchvalues and matchtypes. <BR/>
 *
 * @version     $Id: QuerySearchValueMapper.java,v 1.7 2010/04/15 15:31:13 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ), 000918
 ******************************************************************************
 */
public class QuerySearchValueMapper extends QueryAttributeMapper
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: QuerySearchValueMapper.java,v 1.7 2010/04/15 15:31:13 rburgermann Exp $";

    /**
     * The value to search for. <BR/>
     */
    public String searchValue = null;

    /**
     * The value to search for in a range matchtype.
     */
    public String searchRangeValue = null;

    /**
     * The match type. <BR/>
     */
    public String matchType = null;


    /**************************************************************************
     * This constructor creates a new instance of the class
     * QuerySearchValueMapper. <BR/>
     */
    public QuerySearchValueMapper ()
    {
        // nothing to do
    } // QuerySearchValueMapper

} // QuerySearchValueMapper

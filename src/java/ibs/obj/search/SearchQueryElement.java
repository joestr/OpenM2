/*
 * Class: SearchQueryElement.java
 */

// package:
package ibs.obj.search;

// imports:
import ibs.BaseObject;


/******************************************************************************
 * This class represents one object of type SearchQueryElement. <BR/>
 *
 * @version     $Id: SearchQueryElement.java,v 1.8 2009/07/24 08:26:44 kreimueller Exp $
 *
 * @author      Bernd Buchegger (BB), 980512
 ******************************************************************************
 */
public class SearchQueryElement extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SearchQueryElement.java,v 1.8 2009/07/24 08:26:44 kreimueller Exp $";


    /**
     * holds the name of the attribute in the database
     */
    public String name;

    /**
     * holds the value of the attribute
     */
    public String value;

    /**
     * holds a range value of the attribute.
     * only active if a range datatype has been selected.
     */
    public String range;

    /**
     * holds the type of the attribute.
     */
    public int type;

    /**
     * name of the table in the database.
     */
    public String table;
    /**
     * type of stringmatch. used for text variables.
     */
    public String matchType;


    /**************************************************************************
     * This constructor creates a new instance of the class QueryQueryElement.
     * <BR/>
     *
     * @param   pName       The name of the element.
     * @param   pValue      Value of the attribute.
     * @param   pType       Type of the attribute.
     * @param   pTable      The table in which to search.
     * @param   pMatchType  Kind of search match.
     */
    public SearchQueryElement (String pName, String pValue, int pType,
                                String pTable, String pMatchType)
    {
        // set the instance's attributes:
        this.name = pName;
        this.value = pValue;
        this.range = "";
        this.type = pType;
        this.table = pTable;
        this.matchType = pMatchType;
    } // SearchQueryElement


    /**************************************************************************
     * This constructor creates a new instance of the class QueryFilterElement:
     * <BR/>
     *
     * @param   pName       The name of the element.
     * @param   pValue      Value of the attribute.
     * @param   pRange      Range value of the attribute.
     * @param   pType       Type of the attribute.
     * @param   pTable      The table in which to search.
     * @param   pMatchType  Kind of search match.
     */
    public SearchQueryElement (String pName, String pValue, String pRange,
                                int pType, String pTable , String pMatchType)
    {
        // set the instance's attributes:
        this.name = pName;
        this.value = pValue;
        this.range = pRange;
        this.type = pType;
        this.table = pTable;
        this.matchType = pMatchType;
    } // SearchQueryElement

} // class SearchQueryElement

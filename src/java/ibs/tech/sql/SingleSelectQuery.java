/*
 * Class: SingleSelectQuery.java
 */

// package:
package ibs.tech.sql;

// imports:


/******************************************************************************
 * This class represents a SELECT query which returns exactly one result. <BR/>
 * Format:
 * <PRE>
 *      SELECT    [DISTINCT] selectClause
 *      FROM      fromClause
 *      [WHERE    whereClause]
 *      [GROUP BY groupByClause]
 *      [HAVING   havingClause]
 *      [ORDER BY orderByClause]
 * </PRE>
 *
 * @version     $Id: SingleSelectQuery.java,v 1.4 2009/09/04 19:48:07 kreimueller Exp $
 *
 * @author      klaus, 25.06.2005
 ******************************************************************************
 */
public class SingleSelectQuery extends SelectQuery
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SingleSelectQuery.java,v 1.4 2009/09/04 19:48:07 kreimueller Exp $";




    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a SingleSelectQuery object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     * Not all query parts have to be initialized. If not available just set the
     * value <CODE>null</CODE>. <BR/>
     * IMPORTANT: The <CODE>SELECT</CODE> and the <CODE>FROM</CODE> part are not
     * optional when executing a query.
     *
     * @param   select  SELECT part of query.
     * @param   from    FROM part of the query.
     * @param   where   WHERE part of the query.
     * @param   groupBy GROUP BY part of the query.
     * @param   having  HAVING part of the query.
     * @param   orderBy ORDER BY part of the query.
     */
    public SingleSelectQuery (StringBuilder select, StringBuilder from,
                              StringBuilder where, StringBuilder groupBy,
                              StringBuilder having, StringBuilder orderBy)
    {
        // call super constructor:
        super (select, from, where, groupBy, having, orderBy);
    } // SingleSelectQuery


    /**************************************************************************
     * Creates a SingleSelectQuery object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     * Not all query parts have to be initialized. If not available just set the
     * value <CODE>null</CODE>. <BR/>
     * IMPORTANT: The <CODE>SELECT</CODE> and the <CODE>FROM</CODE> part are not
     * optional when executing a query.
     *
     * @param   select  SELECT part of query.
     * @param   from    FROM part of the query.
     * @param   where   WHERE part of the query.
     * @param   groupBy GROUP BY part of the query.
     * @param   having  HAVING part of the query.
     * @param   orderBy ORDER BY part of the query.
     *
     * @deprecated  KR 20090904 Use
     *              {@link #SingleSelectQuery(StringBuilder, StringBuilder, StringBuilder, StringBuilder, StringBuilder, StringBuilder)}
     *              instead.
     */
    public SingleSelectQuery (StringBuffer select, StringBuffer from,
                              StringBuffer where, StringBuffer groupBy,
                              StringBuffer having, StringBuffer orderBy)
    {
        // call super constructor:
        super (select, from, where, groupBy, having, orderBy);
    } // SingleSelectQuery


    /**************************************************************************
     * Creates a SingleSelectQuery object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     * Not all query parts have to be initialized. If not available just set the
     * value <CODE>null</CODE>. <BR/>
     * IMPORTANT: The <CODE>SELECT</CODE> and the <CODE>FROM</CODE> part are not
     * optional when executing a query.
     *
     * @param   select  SELECT part of query.
     * @param   from    FROM part of the query.
     * @param   where   WHERE part of the query.
     * @param   groupBy GROUP BY part of the query.
     * @param   having  HAVING part of the query.
     * @param   orderBy ORDER BY part of the query.
     */
    public SingleSelectQuery (String select, String from, String where,
                              String groupBy, String having, String orderBy)
    {
        // call super constructor:
        super (select, from, where, groupBy, having, orderBy);
    } // SingleSelectQuery



    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

} // class SingleSelectQuery

/*
 * Class: SelectQuery.java
 */

// package:
package ibs.tech.sql;

// imports:


/******************************************************************************
 * This class represents a SELECT query. <BR/>
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
 * @version     $Id: SelectQuery.java,v 1.10 2009/09/07 09:45:22 bbuchegger Exp $
 *
 * @author      klaus, 25.06.2005
 ******************************************************************************
 */
public class SelectQuery extends SQLRetrievalStatement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SelectQuery.java,v 1.10 2009/09/07 09:45:22 bbuchegger Exp $";



    /**
     * The SELECT clause of the query. <BR/>
     */
    private StringBuilder p_select = null;

    /**
     * The FROM clause of the query. <BR/>
     */
    private StringBuilder p_from = null;

    /**
     * The WHERE clause of the query. <BR/>
     */
    private StringBuilder p_where = null;

    /**
     * The GROUP BY clause of the query. <BR/>
     */
    private StringBuilder p_groupBy = null;

    /**
     * The HAVING clause of the query. <BR/>
     */
    private StringBuilder p_having = null;

    /**
     * The ORDER BY clause of the query. <BR/>
     */
    private StringBuilder p_orderBy = null;

    /**
     * Use <CODE>DISTINCT</CODE> within SELECT clause. <BR/>
     */
    private boolean p_useDistinct = false;



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a SelectQuery object. <BR/>
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
    public SelectQuery (StringBuilder select, StringBuilder from,
                        StringBuilder where, StringBuilder groupBy,
                        StringBuilder having, StringBuilder orderBy)
    {
        // set the properties:
        this.p_select = select;
        this.p_from = from;
        this.p_where = where;
        this.p_groupBy = groupBy;
        this.p_having = having;
        this.p_orderBy = orderBy;
    } // SelectQuery


    /**************************************************************************
     * Creates a SelectQuery object. <BR/>
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
     *              {@link #SelectQuery(StringBuilder, StringBuilder, StringBuilder, StringBuilder, StringBuilder, StringBuilder)}
     *              instead.
     */
    @Deprecated
    public SelectQuery (StringBuffer select, StringBuffer from,
                        StringBuffer where, StringBuffer groupBy,
                        StringBuffer having, StringBuffer orderBy)
    {
        // set the properties:
        this.p_select = select == null ? null : new StringBuilder (select);
        this.p_from = from == null ? null : new StringBuilder (from);
        this.p_where = where == null ? null : new StringBuilder (where);
        this.p_groupBy = groupBy == null ? null : new StringBuilder (groupBy);
        this.p_having = having == null ? null : new StringBuilder (having);
        this.p_orderBy = orderBy == null ? null : new StringBuilder (orderBy);
    } // SelectQuery


    /**************************************************************************
     * Creates a SelectQuery object. <BR/>
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
    public SelectQuery (String select, String from,
                        String where, String groupBy,
                        String having, String orderBy)
    {
        // set the properties:
        this.p_select = select == null ? null : new StringBuilder (select);
        this.p_from = from == null ? null : new StringBuilder (from);
        this.p_where = where == null ? null : new StringBuilder (where);
        this.p_groupBy = groupBy == null ? null : new StringBuilder (groupBy);
        this.p_having = having == null ? null : new StringBuilder (having);
        this.p_orderBy = orderBy == null ? null : new StringBuilder (orderBy);
    } // SelectQuery



    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This method sets the useDistinct. <BR/>
     *
     * @param useDistinct The useDistinct to set.
     */
    public void setUseDistinct (boolean useDistinct)
    {
        this.p_useDistinct = useDistinct;
    } // setUseDistinct


    /**************************************************************************
     * This method gets the useDistinct. <BR/>
     *
     * @return Returns the useDistinct.
     */
    public boolean isUseDistinct ()
    {
        return this.p_useDistinct;
    } // isUseDistinct


    /**************************************************************************
     * Get the StringBuilder representation of the query. <BR/>
     * Especially this method concatenates all parts of the query. <BR/>
     * It calls {@link #toStringBuilder () toStringBuilder} and performs a check
     * if the query is valid.
     *
     * @return  The generated StringBuilder or
     *          <CODE>""</CODE> if the query is empty or
     *          <CODE>null</CODE> if there occurred an error.
     *
     * @throws  DBQueryException
     *          This exception is thrown if not all necessary parts of the
     *          query are set or there is another problem with the query.
     */
    @Override
    public StringBuilder toValidStringBuilder () throws DBQueryException
    {
        StringBuilder retVal = null;     // the return value

        // get the query:
        retVal = this.toStringBuilder ();

        // check if all necessary query parts have been set:
        if (this.p_select == null || this.p_select.length () == 0 ||
            this.p_from == null || this.p_from.length () == 0)
        {
            // throw corresponding exception:
            throw new DBQueryException (
                "Missing SELECT or FROM part within SELECT query: " + retVal);
        } // if

        // return the result:
        return retVal;
    } // toValidStringBuilder


    /**************************************************************************
     * Get the toStringBuilder representation of the query. <BR/>
     * Especially this method concatenates all parts of the query.
     *
     * @return  The generated toStringBuilder or
     *          <CODE>""</CODE> if the query is empty or
     *          <CODE>null</CODE> if there occurred an error.
     */
    public StringBuilder toStringBuilder ()
    {
        StringBuilder retVal = new StringBuilder (); // the return value

        // construct the query and return it:
        if (this.isUseDistinct ())
        {
            retVal.append (SQLQueryConstants.QL_SELECTDISTINCT);
        } // if
        else
        {
            retVal.append (SQLQueryConstants.QL_SELECT);
        } // else
        retVal
            .append (this.p_select)
            .append (SQLQueryConstants.QL_FROM)
            .append (this.p_from);
        if (this.p_where != null)
        {
            retVal
                .append (SQLQueryConstants.QL_WHERE)
                .append (this.p_where);
        } // if
        if (this.p_groupBy != null)
        {
            retVal
                .append (SQLQueryConstants.QL_GROUPBY)
                .append (this.p_groupBy);
        } // if
        if (this.p_having != null)
        {
            retVal
                .append (SQLQueryConstants.QL_HAVING)
                .append (this.p_having);
        } // if
        if (this.p_orderBy != null)
        {
            retVal
                .append (SQLQueryConstants.QL_ORDERBY)
                .append (this.p_orderBy);
        } // if

        // return the result:
        return retVal;
    } // toStringBuilder


    /**************************************************************************
     * Add something to the query. <BR/>
     *
     * @param   select  Additional SELECT part of query.
     * @param   from    Additional FROM part of the query.
     * @param   where   Additional WHERE part of the query.
     * @param   groupBy Additional GROUP BY part of the query.
     * @param   having  Additional HAVING part of the query.
     * @param   orderBy Additional ORDER BY part of the query.
     *
     * @see #add (StringBuffer, StringBuffer, StringBuffer, StringBuffer, StringBuffer, StringBuffer)
     *
     * @deprecated  KR 20090904 Use
     *              {@link #add(StringBuilder, StringBuilder, StringBuilder, StringBuilder, StringBuilder, StringBuilder)}
     *              instead.
     */
    @Deprecated
    public void add (StringBuffer select, StringBuffer from,
                     StringBuffer where, StringBuffer groupBy,
                     StringBuffer having, StringBuffer orderBy)
    {
        // call common method:
        this.add (
            select == null ? null : new StringBuilder (select),
            from == null ? null : new StringBuilder (from),
            where == null ? null : new StringBuilder (where),
            groupBy == null ? null : new StringBuilder (groupBy),
            having == null ? null : new StringBuilder (having),
            orderBy == null ? null : new StringBuilder (orderBy));
    } // add


    /**************************************************************************
     * Add something to the query. <BR/>
     *
     * @param   select  Additional SELECT part of query.
     * @param   from    Additional FROM part of the query.
     * @param   where   Additional WHERE part of the query.
     * @param   groupBy Additional GROUP BY part of the query.
     * @param   having  Additional HAVING part of the query.
     * @param   orderBy Additional ORDER BY part of the query.
     *
     * @see #add (StringBuffer, StringBuffer, StringBuffer, StringBuffer, StringBuffer, StringBuffer)
     */
    public void add (String select, String from,
                     String where, String groupBy,
                     String having, String orderBy)
    {
        // call common method:
        this.add (
            select == null ? null : new StringBuilder (select.trim ()),
            from == null ? null : new StringBuilder (from.trim ()),
            where == null ? null : new StringBuilder (where.trim ()),
            groupBy == null ? null : new StringBuilder (groupBy.trim ()),
            having == null ? null : new StringBuilder (having.trim ()),
            orderBy == null ? null : new StringBuilder (orderBy.trim ()));
    } // add


    /**************************************************************************
     * Add something to the query. <BR/>
     *
     * @param   select  Additional SELECT part of query.
     *                  If the SELECT part already contains something
     *                  <CODE>","</CODE> is appended before the additional
     *                  SELECT part.
     * @param   from    Additional FROM part of the query.
     *                  If the FROM part already contains something
     *                  <CODE>","</CODE> is appended before the additional
     *                  FROM part.
     * @param   where   Additional WHERE part of the query.
     *                  If the WHERE part already contains something
     *                  <CODE>" "</CODE> is appended before the additional
     *                  WHERE part.
     * @param   groupBy Additional GROUP BY part of the query.
     *                  If the GROUP BY part already contains something
     *                  <CODE>","</CODE> is appended before the additional
     *                  GROUP BY part.
     * @param   having  Additional HAVING part of the query.
     *                  If the HAVING part already contains something
     *                  <CODE>" "</CODE> is appended before the additional
     *                  HAVING part.
     * @param   orderBy Additional ORDER BY part of the query.
     *                  If the ORDER BY part already contains something
     *                  <CODE>","</CODE> is appended before the additional
     *                  ORDER BY part.
     */
    public void add (StringBuilder select, StringBuilder from,
                     StringBuilder where, StringBuilder groupBy,
                     StringBuilder having, StringBuilder orderBy)
    {
        // add the strings:
        if (select != null && select.length () > 0)
        {
            if (this.p_select != null)
            {
                this.p_select
                    .append (SQLQueryConstants.QL_COMMA)
                    .append (select);
            } // if
            else
            {
                this.p_select = select;
            } // else
        } // if
        if (from != null && from.length () > 0)
        {
            if (this.p_from != null)
            {
                this.p_from
                    .append (SQLQueryConstants.QL_COMMA)
                    .append (from);
            } // if
            else
            {
                this.p_from = from;
            } // else
        } // if
        if (where != null && where.length () > 0)
        {
            if (this.p_where != null)
            {
                // check if the where extension has the AND set correctly:
                if (!where.toString ().startsWith (SQLQueryConstants.QL_AND.toString ()))
                {
                    this.p_where.append (SQLQueryConstants.QL_AND);
                } // if
                this.p_where
                    .append (SQLQueryConstants.QL_SPACE)
                    .append (where);
            } // if
            else
            {
                this.p_where = where;
            } // else
        } // if
        if (groupBy != null && groupBy.length () > 0)
        {
            if (this.p_groupBy != null)
            {
                this.p_groupBy
                    .append (SQLQueryConstants.QL_COMMA)
                    .append (groupBy);
            } // if
            else
            {
                this.p_groupBy = groupBy;
            } // else
        } // if
        if (having != null && having.length () > 0)
        {
            if (this.p_having != null)
            {
                this.p_having
                    .append (SQLQueryConstants.QL_SPACE)
                    .append (having);
            } // if
            else
            {
                this.p_having = having;
            } // else
        } // if
        if (orderBy != null && orderBy.length () > 0)
        {
            if (this.p_orderBy != null)
            {
                this.p_orderBy
                    .append (SQLQueryConstants.QL_COMMA)
                    .append (orderBy);
            } // if
            else
            {
                this.p_orderBy = orderBy;
            } // else
        } // if
    } // add


    /**************************************************************************
     * Extend the WHERE clause. <BR/>
     * This method extends the where clause with the new String. If the WHERE
     * clause already has a not empty content the keyword <CODE>" AND "</CODE>
     * (with spaces) is inserted before appending the new String. <BR/>
     * If the String is empty just <CODE>" AND "</CODE> is concatenated if
     * necessary.
     *
     * @param   where   The Stirng to append to the WHERE clause.
     *
     * @deprecated  KR 20090904 Use {@link #extendWhere(StringBuilder)} instead.
     */
    @Deprecated
    public void extendWhere (StringBuffer where)
    {
        // call common method:
        this.extendWhere (new StringBuilder (where));
    } // extendWhere


    /**************************************************************************
     * Extend the WHERE clause. <BR/>
     * This method extends the where clause with the new String. If the WHERE
     * clause already has a not empty content the keyword <CODE>" AND "</CODE>
     * (with spaces) is inserted before appending the new String. <BR/>
     * If the String is empty just <CODE>" AND "</CODE> is concatenated if
     * necessary.
     *
     * @param   where   The Stirng to append to the WHERE clause.
     */
    public void extendWhere (StringBuilder where)
    {
        // call common method:
        this.p_where = this.extendWhere (this.p_where, where);
    } // extendWhere

} // class SelectQuery

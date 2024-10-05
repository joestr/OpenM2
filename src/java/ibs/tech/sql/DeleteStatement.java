/*
 * Class: DeleteStatement.java
 */

// package:
package ibs.tech.sql;

// imports:


/******************************************************************************
 * This class represents a DELETE query. <BR/>
 * Format:
 * <PRE>
 *      DELETE FROM tableName
 *      [WHERE      whereClause]
 * </PRE>
 *
 * @version     $Id: DeleteStatement.java,v 1.5 2009/09/04 19:48:07 kreimueller Exp $
 *
 * @author      klaus, 25.06.2005
 ******************************************************************************
 */
public class DeleteStatement extends SQLManipulationStatement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DeleteStatement.java,v 1.5 2009/09/04 19:48:07 kreimueller Exp $";


    /**
     * The table name of the query. <BR/>
     */
    private StringBuilder p_tableName = null;

    /**
     * The WHERE clause of the query. <BR/>
     */
    private StringBuilder p_where = null;



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a DeleteStatement object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     * Not all query parts have to be initialized. If not available just set the
     * value <CODE>null</CODE>. <BR/>
     * IMPORTANT: The <CODE>SELECT</CODE> and the <CODE>FROM</CODE> part are not
     * optional when executing a query.
     *
     * @param   tableName   Name of table on which to perform the query.
     *                      The table name must not be <CODE>null</CODE>!
     * @param   where       WHERE part of the query.
     */
    public DeleteStatement (StringBuilder tableName, StringBuilder where)
    {
        // set the properties:
        this.p_tableName = tableName;
        this.p_where = where;

        // this is an action query:
        this.p_isAction = true;
    } // DeleteStatement


    /**************************************************************************
     * Creates a DeleteStatement object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     * Not all query parts have to be initialized. If not available just set the
     * value <CODE>null</CODE>. <BR/>
     * IMPORTANT: The <CODE>SELECT</CODE> and the <CODE>FROM</CODE> part are not
     * optional when executing a query.
     *
     * @param   tableName   Name of table on which to perform the query.
     *                      The table name must not be <CODE>null</CODE>!
     * @param   where       WHERE part of the query.
     *
     * @throws  NullPointerException
     *          The table name was <CODE>null</CODE>.
     */
    public DeleteStatement (String tableName, String where)
        throws NullPointerException
    {
        // set the properties:
        this.p_tableName = new StringBuilder (tableName);
        this.p_where = where == null ? null : new StringBuilder (where);

        // this is an action query:
        this.p_isAction = true;
    } // DeleteStatement



    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

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
        if (this.p_tableName == null || this.p_tableName.length () == 0)
        {
            // throw corresponding exception:
            throw new DBQueryException (
                "Missing table name within DELETE query: " + retVal);
        } // if

        // return the result:
        return retVal;
    } // toValidStringBuilder


    /**************************************************************************
     * Get the StringBuilder representation of the query. <BR/>
     * Especially this method concatenates all parts of the query.
     *
     * @return  The generated StringBuilder or
     *          <CODE>""</CODE> if the query is empty or
     *          <CODE>null</CODE> if there occurred an error.
     */
    @Override
    public StringBuilder toStringBuilder ()
    {
        StringBuilder retVal = new StringBuilder (); // the return value

        // construct the query and return it:
        retVal
            .append (SQLQueryConstants.QL_DELETE)
            .append (this.p_tableName);
        if (this.p_where != null)
        {
            retVal
                .append (SQLQueryConstants.QL_WHERE)
                .append (this.p_where);
        } // if

        // return the result:
        return retVal;
    } // toStringBuilder


    /**************************************************************************
     * Add something to the query. <BR/>
     *
     * @param   where   Additional WHERE part of the query.
     *
     * @see #add (StringBuilder)
     */
    public void add (String where)
    {
        // call common method:
        this.add (
            where == null ? null : new StringBuilder (where));
    } // add


    /**************************************************************************
     * Add something to the query. <BR/>
     *
     * @param   where   Additional WHERE part of the query.
     *                  If the WHERE part already contains something
     *                  <CODE>" "</CODE> is appended before the additional
     *                  WHERE part.
     */
    public void add (StringBuilder where)
    {
        // add the strings:
        if (where != null)
        {
            if (this.p_where != null)
            {
                this.p_where
                    .append (SQLQueryConstants.QL_SPACE)
                    .append (where);
            } // if
            else
            {
                this.p_where = where;
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
     */
    public void extendWhere (StringBuilder where)
    {
        // call common method:
        this.p_where = this.extendWhere (this.p_where, where);
    } // extendWhere

} // class DeleteStatement

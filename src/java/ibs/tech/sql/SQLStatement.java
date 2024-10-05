/*
 * Class: SQLStatement.java
 */

// package:
package ibs.tech.sql;

// imports:
import ibs.BaseObject;

import java.sql.PreparedStatement;


/******************************************************************************
 * This class represents a statement of any type. <BR/>
 *
 * @version     $Id: SQLStatement.java,v 1.5 2009/09/04 19:48:07 kreimueller Exp $
 *
 * @author      klaus, 25.06.2005
 ******************************************************************************
 */
public abstract class SQLStatement extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SQLStatement.java,v 1.5 2009/09/04 19:48:07 kreimueller Exp $";


    /**
     * The prepared statement. <BR/>
     */
    private PreparedStatement p_statement = null;

    /**
     * Is this an action query, i.e. does the query possibly perform changes
     * within the database? <BR/>
     */
    protected boolean p_isAction = true;



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a Query object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     */
    public SQLStatement ()
    {
        // call constructor of super class:
        super ();
    } // SQLStatement


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Execute the query. <BR/>
     * Important: If the action is not <CODE>null</CODE> this means that the
     * query returns at least one result. So the query has to be closed
     * explicitely after working with the action ({@link #close (SQLAction)}).
     *
     * @return  The action which was used to execute the query and from which
     *          to get the query result.
     *          <CODE>null</CODE> if the query does not return any result.
     *
     * @throws  DBQueryException
     *          An error occurred during execution of query.
     */
    public abstract SQLAction execute () throws DBQueryException;


    /**************************************************************************
     * Execute the query. <BR/>
     *
     * @param   action  The action used to execute the query.
     *
     * @return  <CODE>0</CODE> if the query did not return any rows.
     *          <CODE>>0</CODE> if the query returned one or more rows.
     *
     * @throws  DBQueryException
     *          An error occurred during execution of query.
     */
    public abstract int execute (SQLAction action) throws DBQueryException;


    /**************************************************************************
     * Close the query. <BR/>
     *
     * @param   action  The action used to execute the query.
     *
     * @throws  DBQueryException
     *          An error occurred during closing of query.
     */
    public void close (SQLAction action) throws DBQueryException
    {
/*
        try
        {
            // the last tuple has been processed
            // end transaction:
            action.end ();
        } // try
        catch (DBError e)
        {
            throw new DBQueryException (
                "Error when closing query in SQLStatement", e);
        } // catch
*/
        try
        {
            // close db connection in every case -  only workaround -
            // db connection must be handled somewhere else
            DBConnector.releaseDBConnection (action);
        } // try
        catch (DBError e)
        {
            throw new DBQueryException (
                "Error when releasing database connection in SQLStatement", e);
        } // catch DBError
    } // close


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
    public abstract StringBuilder toValidStringBuilder () throws DBQueryException;


    /**************************************************************************
     * Get the StringBuilder representation of the query. <BR/>
     * Especially this method concatenates all parts of the query.
     *
     * @return  The generated StringBuilder or
     *          <CODE>""</CODE> if the query is empty or
     *          <CODE>null</CODE> if there occurred an error.
     */
    protected abstract StringBuilder toStringBuilder ();


    /**************************************************************************
     * Get the String representation of the query. <BR/>
     * This method calls {@link #toStringBuilder () toStringBuilder} and converts
     * the result to a String.
     *
     * @return  The generated String or
     *          <CODE>""</CODE> if the query is empty or
     *          <CODE>null</CODE> if there occurred an error.
     */
    public final String toString ()
    {
        // get this string buffer and convert it to string representation:
        return this.toStringBuilder ().toString ();
    } // toString


    /**************************************************************************
     * Get the prepared statement for the query. <BR/>
     *
     * @return  The prepared statement.
     */
    public final PreparedStatement getPreparedStatement ()
    {
        // check if the statement is already stored:
        if (this.p_statement == null)
        {
            this.p_statement =
                SQLHelpers.getPreparedStatement (this.toStringBuilder ());
        } // if

        // return the statement:
        return this.p_statement;
    } // getPreparedStatement


    /**************************************************************************
     * Extend the WHERE clause. <BR/>
     * This method extends the where clause with the new String. If the WHERE
     * clause already has a not empty content the keyword <CODE>" AND "</CODE>
     * (with spaces) is inserted before appending the new String. <BR/>
     * If the String is empty just <CODE>" AND "</CODE> is concatenated if
     * necessary.
     *
     * @param   oldWhere    The original WHERE clause.
     * @param   newWhere    The String to append to the WHERE clause.
     *
     * @return  The actual WHERE clause which is constructed from oldWhere and
     *          newWhere.
     */
    protected StringBuilder extendWhere (StringBuilder oldWhere,
                                         StringBuilder newWhere)
    {
        StringBuilder where = oldWhere; // the actual WHERE clause

        // check if we have to append something:
        // important: an empty String means just to append the "AND" keyword
        if (newWhere != null)
        {
            // check if the WHERE already has a content:
            if (where == null)
            {
                // initialize the WHERE clause and add the new string:
                where = new StringBuilder ().append (newWhere);
            } // if
            else if (where.length () > 0)
            {
                // append the "AND" keyword and the new string to the WHERE
                // clause:
                where.append (SQLQueryConstants.QL_AND).append (newWhere);
            } // else if
        } // if

        // return the result:
        return where;
    } // extendWhere

} // class SQLStatement

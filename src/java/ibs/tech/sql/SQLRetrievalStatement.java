/*
 * Class: SQLRetrievalStatement.java
 */

// package:
package ibs.tech.sql;

// imports:


/******************************************************************************
 * This class represents a statement which is used to retrieve some data. <BR/>
 *
 * @version     $Id: SQLRetrievalStatement.java,v 1.5 2009/09/04 19:48:07 kreimueller Exp $
 *
 * @author      klaus, 25.06.2005
 ******************************************************************************
 */
public abstract class SQLRetrievalStatement extends SQLStatement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SQLRetrievalStatement.java,v 1.5 2009/09/04 19:48:07 kreimueller Exp $";



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
    public SQLRetrievalStatement ()
    {
        // call constructor of super class:
        super ();

        // this is no action query:
        this.p_isAction = false;
    } // SQLRetrievalStatement


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
    @Override
    public SQLAction execute () throws DBQueryException
    {
        SQLAction action = null;        // action object for database access

        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = DBConnector.getDBConnection ();

            // execute the query:
            if (this.execute (action) > 0)
            {
                // there are some results, so return the action:
                return action;
            } // if

            // close the query:
            this.close (action);
        } // try
        catch (DBError e)
        {
            // close the action:
            if (action != null)
            {
                this.close (action);
            } // if

            throw new DBQueryException (
                "Error when opening database connection in SQLRetrievalStatement", e);
        } // catch

        // no action to return:
        return null;
    } // execute


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
    @Override
    public int execute (SQLAction action) throws DBQueryException
    {
        int rowCount = 0;               // row counter
        StringBuilder queryStr;          // the query to be executed

        // get the query:
        queryStr = this.toStringBuilder ();

        try
        {
            // execute the queryString, indicate if we're performing an
            // action query:
            // execute the query and get the number of resulting tuples:
            rowCount = action.execute (queryStr, false);

            // check if there was something returned:
            if (rowCount > 0)           // there were some tuples found?
            {
                return rowCount;
            } // if there were some tuples found

            // the last tuple has been processed
            // end transaction:
            action.end ();
        } // try
        catch (DBError e)
        {
            throw new DBQueryException (
                "Error when executing query in SQLRetrievalStatement", e);
        } // catch

        // return number of rows:
        return rowCount;
    } // execute

} // class SQLRetrievalStatement

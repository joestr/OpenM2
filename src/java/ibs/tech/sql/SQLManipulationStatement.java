/*
 * Class: SQLManipulationStatement.java
 */

// package:
package ibs.tech.sql;

// imports:


/******************************************************************************
 * This class represents a statement which is used to manipulate data. <BR/>
 *
 * @version     $Id: SQLManipulationStatement.java,v 1.4 2009/09/04 19:48:07 kreimueller Exp $
 *
 * @author      klaus, 25.06.2005
 ******************************************************************************
 */
public abstract class SQLManipulationStatement extends SQLStatement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SQLManipulationStatement.java,v 1.4 2009/09/04 19:48:07 kreimueller Exp $";



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
    public SQLManipulationStatement ()
    {
        // call constructor of super class:
        super ();
    } // SQLManipulationStatement


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
     *          For data manipulation statements this is always
     *          <CODE>null</CODE>.
     *
     * @throws  DBQueryException
     *          An error occurred during execution of query.
     */
    @Override
    public SQLAction execute () throws DBQueryException
    {
        SQLAction action = null;        // the action object used to access the
                                        // database

        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = DBConnector.getDBConnection ();

            // execute the query:
            this.execute (action);

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
                "Error when opening database connection in SQLManipulationStatement", e);
        } // catch

        // no action to return:
        return null;
    } // execute


    /**************************************************************************
     * Execute the query. <BR/>
     *
     * @param   action  The action used to execute the query.
     *
     * @return  For data manipulation statement this is always <CODE>0</CODE>.
     *
     * @throws  DBQueryException
     *          An error occurred during execution of query.
     */
    @Override
    public int execute (SQLAction action) throws DBQueryException
    {
        StringBuilder queryStr;         // the query to be executed

        // get the query:
        queryStr = this.toStringBuilder ();

        try
        {
            // execute the queryString, indicate if we're performing an
            // action query:
            // execute the query:
            action.execute (queryStr, true);

            // the last tuple has been processed
            // end transaction:
            action.end ();
        } // try
        catch (DBError e)
        {
            throw new DBQueryException (
                "Error when executing query in SQLManipulationStatement", e);
        } // catch

        // return number of rows:
        return 0;
    } // execute

} // class SQLManipulationStatement

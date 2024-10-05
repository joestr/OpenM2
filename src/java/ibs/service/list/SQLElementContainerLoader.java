/*
 * Class: SQLElementContainerLoader.java
 */

// package:
package ibs.service.list;

// imports:
import ibs.tech.sql.DBConnector;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.util.GeneralException;
import ibs.util.list.Element;
import ibs.util.list.ElementContainer;
import ibs.util.list.ListException;


/******************************************************************************
 * This class is responsible for loading the data for a specific element
 * container out of a sql data store. <BR/>
 *
 * @version     $Id: SQLElementContainerLoader.java,v 1.9 2007/07/31 19:13:58 kreimueller Exp $
 *
 * @author      Klaus, 15.11.2003
 *
 * @param   <EC>    The container for which this container loader is defined.
 *                  Must be a subclass of ElementContainer&lt;E>.
 * @param   <E>     The class for which this container loader is defined.
 *                  Must be a subclass of Element.
 ******************************************************************************
 */
public abstract class SQLElementContainerLoader<EC extends ElementContainer<E>, E extends Element>
    extends ElementContainerLoader<EC, E>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SQLElementContainerLoader.java,v 1.9 2007/07/31 19:13:58 kreimueller Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a SQLElementContainerLoader object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   container   The ElementContainer in which to load the information.
     */
    public SQLElementContainerLoader (EC container)
    {
        // call constructor of super class:
        super (container);
    } // SQLElementContainerLoader


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Load the elements for the container. <BR/>
     * First this method tries to load the objects from the database. During
     * this operation a rights check is done, too. If this is all right the
     * objects are returned otherwise an exception is raised. <BR/>
     *
     * @return  The elements.
     *          If there are no elements the return value must be an empty
     *          ElementContainer. <CODE>null</CODE> is not allowed.
     *
     * @throws  ListException
     *          An error occurred in a list operation.
     */
    protected final EC loadElements ()
        throws ListException
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        int rowCount;                   // row counter
        String queryStr;                // the query to be executed
        EC elems = null;                // the element container
        Throwable lastError = null;     // the last error which occurred
        String msgError = "loader: ";
        String msgDbErrorStart = "DBError in Loader: ";


        // create the SQL String to select all tuples
        queryStr = this.createQuery ();

        // create the container:
//        elems = this.getContainerInstance ();
        elems = this.getElems ();

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = DBConnector.getDBConnection ();
            // execute the query and get the number of resulting tuples:
            rowCount = action.execute (queryStr, false);

            // check if there was something returned:
            if (rowCount > 0)           // there were some tuples found?
            {
                // get the tuples out of the database:
                while (!action.getEOF ()) // there are tuples left?
                {
                    // get specific data of container element:
                    elems.add (this.parseElement (action));

                    // step one tuple ahead for the next loop:
                    action.next ();
                } // while
            } // if there were some tuples found

            // the last tuple has been processed
            // end transaction:
            action.end ();
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            System.out.println (msgError + this.getClass ().getName ());
            System.out.println ("queryStr: " + queryStr);
            System.out.println (msgDbErrorStart + e.getMessage () + e.getError ());
            lastError = e;
        } // catch
        catch (GeneralException e)
        {
            // an error occurred - show name and info
            System.out.println (msgError + this.getClass ().getName ());
            System.out.println ("GeneralException in Loader: " + e.getMessage () + e.getError ());
            lastError = e;
        } // catch
        finally
        {
            try
            {
                // close db connection in every case -  only workaround -
                // db connection must be handled somewhere else
                DBConnector.releaseDBConnection (action);
            } // try
            catch (DBError e)
            {
                // an error occurred - show name and info
                System.out.println (msgDbErrorStart + e.getMessage () + e.getError ());
                lastError = e;
            } // catch DBError
        } // finally

        if (lastError != null)
        {
            throw new ListException ("Exception in loader", lastError);
        } // if

        // return the result:
        return elems;
    } // loadElements


    /**************************************************************************
     * Create the query to retrieve the data from the database. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     *
     * @return  The constructed query.
     */
    protected abstract String createQuery ();


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This method is used to get all attributes of one element out of the
     * result set. The attribute names which can be used are the ones which
     * are defined within the resultset of {@link #createQuery createQuery}.
     * <BR/>
     * <B>Format:</B>. <BR/>
     * for oid properties:
     *      &lt;variable&gt; = getQuOidValue (action, "&lt;attribute&gt;");. <BR/>
     * for other properties:
     *      &lt;variable&gt; = action.get&lt;type&gt; ("&lt;attribute&gt;");. <BR/>
     *
     * @param   action  The database object used for getting the tuple values.
     *
     * @return  The newly created element filled with the values out of the
     *          actual tuple.
     *
     * @exception   ibs.tech.sql.DBError
     *              Error when executing database statement.
     * @exception   ibs.util.GeneralException
     *              Spezific error of the Element class.
     */
    protected abstract E parseElement (SQLAction action)
        throws DBError, GeneralException;

} // class SQLElementContainerLoader

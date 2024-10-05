/*
 * Created by IntelliJ IDEA.
 * User: hpichler
 * Date: 06.08.2002
 * Time: 11:03:00
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */

// package:
package ibs.service.observer;

// imports:
import ibs.BaseObject;
import ibs.service.observer.CounterException;
import ibs.tech.sql.DBConnector;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;


/******************************************************************************
 * Counter implements a persistent (db-stored) counter to create unique ids.
 * <BR> Hint: This class is a reimplementation of ibs.util.Counter. It does not
 * need the m2-context, only the database-connection must be given via
 * the static DBConnector-class.
 *
 * @version     $Id: Counter.java,v 1.5 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      HORST PICHLER, 06.08.2002
 ******************************************************************************
 */
public class Counter extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Counter.java,v 1.5 2007/07/24 21:27:33 kreimueller Exp $";


    /**
     * Prefix of counters tablename.
     */
    private final String TABLE_PREFIX = "cnt_";

    /**
     * Counters name.
     */
    private String p_name = null;
    /**
     * Counters table name.
     */
    private String p_tableName = null;
    /**
     * Counter.
     */
    private int p_cnt = -1;

    /**
     * The hash code. <BR/>
     */
    private int p_hashCode = Integer.MIN_VALUE;

    /**
     * Null value for String. <BR/>
     */
    private static final String VALUE_NULL = "null";



    /**************************************************************************
     * This constructor creates a new instance of the class Counter. <BR/>
     *
     * @param   name    The name of the counter.
     */
    public Counter (String name)
    {
        this.p_name = name;
        if (this.p_name != null)
        {
            this.p_tableName = this.TABLE_PREFIX + this.p_name;
        } // if
    } // Counter


    //
    // getters
    //

    /**************************************************************************
     * This method ... <BR/>
     *
     * @return  The name.
     */
    public String getName ()
    {
        return this.p_name;
    } // getName


    /**************************************************************************
     * This method ... <BR/>
     *
     * @return  The table name.
     */
    public String getTableName ()
    {
        return this.p_tableName;
    } // getTableName


    /**************************************************************************
     * This method ... <BR/>
     *
     * @return  The counter.
     */
    public int getCnt ()
    {
        return this.p_cnt;
    } // getCnt


    /**************************************************************************
     * Increments the counter by 1 and returns it.
     *
     * @return  The next counter value.
     *
     * @throws  CounterException
     *          An error occurred.
     */
    public int getNext () throws CounterException
    {
        this.p_cnt = this.getNext (1);
        return this.p_cnt;
    } // getNext


    /**************************************************************************
     * Increments the counter by given value and returns it.
     *
     * @param   incr    The incrementor for the counter.
     *
     * @return  The next counter value.
     *
     * @throws  CounterException
     *          An error occurred.
     */
    public int getNext (int incr) throws CounterException
    {
        SQLAction action = null;
        String queryUpdate = null;
        String querySelect = null;
        int rowCount = 0;

        // check init
        this.checkInitialization ("getNext");

        // generate query-strings
        queryUpdate = "UPDATE " + this.p_tableName + " SET cnt = cnt + " + incr;
        querySelect = "SELECT cnt FROM " + this.p_tableName;

        // perform query
        try
        {
            // open connection - begin transaction
            action = DBConnector.getDBConnection ();
            action.beginTrans ();

            // execute 1st query
            rowCount = action.execute (queryUpdate, true);
            action.end ();

            // execute 2nd query
            rowCount = action.execute (querySelect, false);
            // get entry
            if (!action.getEOF ())               // at least one entry exists
            {
                // fetch counter
                this.p_cnt = action.getInt ("cnt");
                // unique-constraint: check if more than one row returned
                rowCount = 1;
                action.next ();
                if (!action.getEOF ())
                {
                    rowCount++;
                } // if
                action.end ();
            } // if (!action.getEOF ())
            // otherwise skip - throw exception at end of method

            // commit transaction
            action.commitTrans ();

        } // try
        catch (DBError e)
        {
            try
            {
                action.rollbackTrans ();
                throw new CounterException (
                    "Error while fetching next Counter: " + e.getMessage () +
                        "\nQueries: " + queryUpdate + "; " + querySelect);
            } // try
            catch (DBError err)
            {
                throw new CounterException (
                    "Error during rollback of fetching next Counter: " +
                        e.getMessage () + "\nQueries: " + queryUpdate + "; " +
                        querySelect);
            } // catch
        } // catch DBError
        finally
        {
            // close action and db-connection
            try
            {
                action.end ();
                DBConnector.releaseDBConnection (action);
            } // try
            catch (DBError e)
            {
                throw new CounterException (
                    "Error while checking structure of Counter: " +
                        e.getMessage () + "\nQueries: " + queryUpdate + "; " +
                        querySelect);
            } // catch
        } // finally

        // perform some consistency checks
        String err = "";
        if (rowCount == 0)
        {
            err = " is not initalized.";
        } // if
        else if (rowCount != 1)
        {
            err = " is not unique (more than one entry).";
        } // else if

        if (rowCount != 1)              // wrong number of entries in table?
        {
            throw new CounterException ("Counter " + this.p_name + err +
                " Query: " + querySelect);
        } // if

        return this.p_cnt;
    } // getNext


    /**************************************************************************
     * Increments the counter by 1 and returns a string with
     * given format. <BR/>
     *
     * @param   formatString    The format for the returned String.
     *                          every # is replaced with a number, leading #
     *                          are replaced with 0. escapecharacter for # is \.
     *
     * @return  the currentCount + 1 of the specified counter formated like
     *          the given format in formatString
     *
     * @throws  CounterException
     *          An error occurred.
     */
    public String getNextFormat (String formatString) throws CounterException
    {
        return this.getNextFormat (formatString, 1);
    } // getNextFormat


    /**************************************************************************
     * Increments the counter by given increment and returns a string with
     * given format. <BR/>
     *
     * @param   formatString    The format for the returned String.
     *                          Every # is replaced with a number, leading #
     *                          are replaced with 0. escapecharacter for # is \.
     * @param   incr            The incrementor for the counter.
     *
     * @return  the currentCount + 1 of the specified counter formated like
     *          the given format in formatString
     *
     * @throws  CounterException
     *          An error occurred.
     */
    public String getNextFormat (String formatString, int incr)
        throws CounterException
    {
        this.p_cnt = this.getNext (incr);

        String countStr = Integer.toString (this.p_cnt);
            // contains the currect count as a String
        StringBuffer escapedFormatStr = new StringBuffer ();
            // contains only the escaped chars of formatStr
        StringBuffer unEscapedFormatStr = new StringBuffer ();
            // contains only the unescaped chars of formatStr
        StringBuffer countResultStr = new StringBuffer ();
        StringBuffer resultStr = new StringBuffer ();


        // seperate escaped chars from unescaped chars in formatString
        for (int i = 0; i < formatString.length (); i++)
        {
            if (formatString.charAt (i) == '\\' &&
                i < formatString.length () - 1 &&
                (formatString.charAt (i + 1) == '#' || formatString
                    .charAt (i + 1) == '\\'))
            {
                escapedFormatStr.append (formatString.charAt (++i));
            } // if
            else if (formatString.charAt (i) != '\\')
                // check if there is an unescaped \ in the formatString
            {
                escapedFormatStr.append ('_');
                unEscapedFormatStr.append (formatString.charAt (i));
            } // else

        } // for


        // fill counterStr into unEscapedFormatStr
        int j = countStr.length () - 1;

        for (int i = unEscapedFormatStr.length () - 1; i >= 0; i--)
        {
            // replace the character # with the current count
            if (unEscapedFormatStr.charAt (i) == '#')
            {
                // check if the # should be fullfilled with "0"
                if (j >= 0)
                {
                    countResultStr.insert (0, countStr.charAt (j--));
                } // if
                else
                {
                    countResultStr.insert (0, '0');
                } // else
            } // if
            else
            {
                countResultStr.insert (0, unEscapedFormatStr.charAt (i));
            } // else
        } // for


        // join escaped chars of formatString with the result
        // of fullfilling the unEscapedFormatStr with the current count
        j = 0;
        for (int i = 0; i < escapedFormatStr.length (); i++)
        {
            if (escapedFormatStr.charAt (i) != '_')
            {
                resultStr.append (escapedFormatStr.charAt (i));
            } // if
            else
            {
                resultStr.append (countResultStr.charAt (j++));
            } // else
        } // for i

        return resultStr.toString ();
    } // getNextFormat


    /**************************************************************************
     * Set counter to <CODE>0</CODE>. <BR/>
     *
     * @throws  CounterException
     *          An error occurred.
     */
    public void reset () throws CounterException
    {
        this.p_cnt = 0;
        this.reset (this.p_cnt);
    } // reset


    /**************************************************************************
     * Set specified counter to given integer. <BR/>
     *
     * @param   cnt     The value to which the counter will be set.
     *
     * @throws  CounterException
     *          An error occurred.
     */
    public void reset (int cnt) throws CounterException
    {
        SQLAction action = null;
        String queryUpdate = null;

        // check init
        this.checkInitialization ("reset");

        // reset counter
        this.p_cnt = cnt;

        // generate query-strings
        queryUpdate = "UPDATE " + this.p_tableName + " SET cnt = " + this.p_cnt;

        // perform query
        try
        {
            // open connection - begin transaction
            action = DBConnector.getDBConnection ();

            // execute 1st query
            action.execute (queryUpdate, true);
            action.end ();

        } // try
        catch (DBError e)
        {
            throw new CounterException ("Error while reseting counter to 0: " +
                e.getMessage () + "\nQuery: " + queryUpdate);
        } // catch DBError
        finally
        {
            // close action and db-connection
            try
            {
                action.end ();
                DBConnector.releaseDBConnection (action);
            } // try
            catch (DBError e)
            {
                throw new CounterException (
                    "Error while reseting counter to 0: " + e.getMessage () +
                        "\nQuery: " + queryUpdate);
            } // catch
        } // finally
    } // reset


    /**************************************************************************
     * Creates the datastructure and initializes the counter. <BR/>
     *
     * @param   initValue   initial value for the counter.
     *
     * @throws  CounterException
     *          An error occurred.
     */
    public void createStructure (int initValue) throws CounterException
    {
        SQLAction action = null;
        String ddl = null;
        String query = null;

        // check init
        this.checkInitialization ("createStructure");

        // generate ddl to create table
        ddl = "CREATE TABLE " + this.p_tableName + " (" +
              "cnt INTEGER NOT NULL)";

        // generate INSERT query-string
        query = " INSERT INTO " + this.p_tableName +
                "  (cnt)" +
                " VALUES (" + initValue + ")";

        // perform query
        try
        {
            // open connection - begin transaction
            action = DBConnector.getDBConnection ();
            action.beginTrans ();

            // execute ddls
            action.execute (ddl, true);
            action.end ();

            // execute insert-query
            action.execute (query, true);
            action.end ();

            // commit transaction
            action.commitTrans ();
        } // try
        catch (DBError e)
        {
            // get error message:
            // close action and db-connection
            try
            {
                action.rollbackTrans ();

                String q = ddl + "; " + query;

                throw new CounterException (
                    "Error while creation and initialization of counter: " +
                        e.getMessage () + "\nQueries" + q);
            } // try
            catch (DBError err)
            {
                String q = "";

                throw new CounterException (
                    "Error during rollback from creation and initialization of counter: " +
                        err.getMessage () + "\nQueries" + q);
            } // catch
        } // catch
        finally
        {
            // close action and db-connection
            try
            {
                action.end ();
                DBConnector.releaseDBConnection (action);
            } // try
            catch (DBError e)
            {
                String q = ddl + "; " + query;

                throw new CounterException (
                    "Error while creation and initialization of counter: " +
                        e.getMessage () + "\nQueries" + q);
            } // catch
        } // finally
    } // createStructure

    /**************************************************************************
     * Drops the datastructure. <BR/>
     *
     * @throws  CounterException
     *          An error occurred.
     */
    public void dropStructure () throws CounterException
    {
        SQLAction action = null;
        String ddl = null;

        // check init
        this.checkInitialization ("dropStructure");

        // generate ddl to create table
        ddl = "DROP TABLE " + this.p_tableName;

        // perform query
        try
        {
            // open connection - begin transaction
            action = DBConnector.getDBConnection ();

            // execute ddls
            action.execute (ddl, true);
            action.end ();
        } // try
        catch (DBError e)
        {
            // get error message:
            // close action and db-connection
            throw new CounterException (
                "Error while dropping structure for counter: " +
                    e.getMessage () + "\nQuery: " + ddl);
        } // catch
        finally
        {
            // close action and db-connection
            try
            {
                action.end ();
                DBConnector.releaseDBConnection (action);
            } // try
            catch (DBError e)
            {
                throw new CounterException (
                    "Error while creation and initialization of counter: " +
                        e.getMessage () + "\nQueries: " + ddl);
            } // catch
        } // finally
    } // dropStructure


    /**************************************************************************
     * Checks the datastructures. <BR> Structure of base data will be checked
     * in this method, for additional structure please overwrite the
     * method 'checkAdditionalStructure'.
     *
     * @return  The current value of the counter.
     *
     * @throws  CounterException
     *          An error occurred.
     */
    public int checkStructure () throws CounterException
    {
        SQLAction action = null;
        int rowCount = 0;

        // check init
        this.checkInitialization ("checkStructure");

        // generate SELECT query-string
        String query =
            " SELECT cnt " +
            " FROM " + this.p_tableName;

        // perform query
        try
        {
            // open connection - begin transaction
            action = DBConnector.getDBConnection ();

            // execute query
            rowCount = action.execute (query, false);

            // check validity of entry
            if (!action.getEOF ())               // at least one entry exists
            {
                // fetch counter
                this.p_cnt = action.getInt ("cnt");

                // unique-constraint: check if more than one row returned
                rowCount = 1;
                action.next ();
                if (!action.getEOF ())
                {
                    rowCount++;
                } // if
                action.end ();
            } // if (!action.getEOF ())
            // otherwise skip - throw exception at end of method
        } // try
        catch (DBError e)
        {
            throw new CounterException (
                "Error while checking structure of Counter: " +
                    e.getMessage () + "\nQuery: " + query);
        } // catch
        finally
        {
            // close action and db-connection
            try
            {
                action.end ();
                DBConnector.releaseDBConnection (action);
            } // try
            catch (DBError e)
            {
                throw new CounterException (
                    "Error while checking structure of Counter: " +
                        e.getMessage () + "\nQuery: " + query);
            } // catch
        } // finally

        // perform some consistency checks
        String err = "";
        if (rowCount == 0)
        {
            err = " is not initalized.";
        } // if
        else if (rowCount != 1)
        {
            err = " is not unique (more than one entry).";
        } // else if

        if (rowCount != 1)              // wrong number of entries in table?
        {
            throw new CounterException ("Counter " + this.p_name + err +
                " Query: " + query);
        } // if

        return this.p_cnt;
    } // checkStructure


    /**************************************************************************
     * Checks for given operation if needed objects are initialized . <BR/>
     *
     * @param   op      The operation name.
     *
     * @throws  CounterException
     *          An error occurred.
     */
    private final void checkInitialization (String op)
        throws CounterException
    {
        if (this.p_name == null)
        {
            throw new CounterException ("Error during " + op + " Counter: " +
                " Name not defined.");
        } // if
        if (this.p_tableName == null)
        {
            throw new CounterException ("Error during " + op + " Counter: " +
                " Tablename not defined.");
        } // if
    } // checkInitialization


    /**************************************************************************
     * Returns a string representation of the object. <BR/>
     *
     * @return  a string representation of the object.
     */
    public String toString ()
    {
        return "[p_name=" + this.p_name + "; p_cnt=" + this.p_cnt +
            "; p_tableName=" + this.p_tableName + "]";
    } // toString


    /**************************************************************************
     * Returns true if given object equals this object. <BR/>
     * Tests: p_name, p_tableName, p_cnt.
     *
     * @param   obj     The object to check for equality.
     *
     * @return  <CODE>true</CODE> if the objects are equal,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean equals (Counter obj)
    {
        String name = null;
        String tableName = null;
        String oName = null;
        String oTableName = null;

        // check null value
        if (obj == null)
        {
            return false;
        } // if
        // check class equality
        if (!this.getClass ().getName ().equals (obj.getClass ().getName ()))
        {
            return false;
        } // if

        // set temporary variables - to avoid NullPointerException
        name = this.p_name == null ? Counter.VALUE_NULL : this.p_name;
        tableName = this.p_tableName == null ? Counter.VALUE_NULL : this.p_tableName;
        oName = obj.getName () == null ? Counter.VALUE_NULL : obj.getName ();
        oTableName = obj.getTableName () == null ? Counter.VALUE_NULL : obj.getTableName ();

        // check property-equality and return result:
        return name.equals (oName) && tableName.equals (oTableName) &&
            this.p_cnt == obj.getCnt ();
    } // equals


    /**************************************************************************
     * Returns a hash code value for the object. <BR/>
     *
     * @return  A hash code value for this object.
     */
    public int hashCode ()
    {
        // check if a valid hash code was set:
        if (this.p_hashCode == Integer.MIN_VALUE)
        {
            // concatenate the relevant fields and compute the hash code from
            // this value:
            this.p_hashCode = ("" + this.p_name + "." + this.p_tableName)
                .hashCode ();
        } // if

        // return the result:
        return this.p_hashCode;
    } // hashCode

} // Counter

/*
 * Created by IntelliJ IDEA.
 * User: hpichler
 * Date: 18.08.2002
 * Time: 22:05:05
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */

// package:
package ibs.service.observer;

// imports:
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.service.observer.ObserverContext;
import ibs.service.observer.ObserverException;
import ibs.service.observer.ObserverJob;
import ibs.service.observer.ObserverJobData;
import ibs.tech.sql.DBConnector;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.util.StringHelpers;


/******************************************************************************
 * m2ObserverJobData holds relevant base data for an m2ObserverJob. <BR> This
 * class also provides methods to load/save data and create/delete the needed
 * data-structures.
 *
 * @version     $Id: M2ObserverJobData.java,v 1.2 2007/07/31 19:13:58 kreimueller Exp $
 *
 * @author      HORST PICHLER, 18.08.2002
 ******************************************************************************
 */
public class M2ObserverJobData extends ObserverJobData
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: M2ObserverJobData.java,v 1.2 2007/07/31 19:13:58 kreimueller Exp $";


    /**
     * Oid of 1st referenced m2-object.
     */
    protected OID oid1 = null;

    /**
     * Oid of 2nd referenced m2-object (will be workflow in most cases
     */
    protected OID oid2 = null;

    /**
     * Postfix used for tables and indexes. <BR/>
     */
    private static final String TABLE_POSTFIX = "_m2";



    /**************************************************************************
     * Public constructor. <BR/>
     */
    public M2ObserverJobData ()
    {
        // nothing to do
    } // m2ObserverJobData


    /**************************************************************************
     * Public constructor for a new m2ObserverJobData object.<BR> To use with
     * ObserverLoader.[un]register () or execute ().
     *
     * @param   context     The observer context.
     * @param   className   The name of the class which implements the observer.
     * @param   name        The name of the observer job.
     * @param   oid1        Oid of the first object.
     * @param   oid2        Oid of the second object.
     */
    public M2ObserverJobData (ObserverContext context, String className,
                              String name, OID oid1, OID oid2)
    {
        // call constructor of super class:
        super (context, className, name);

        // set properties:
        this.oid1 = oid1;
        this.oid2 = oid2;
    } // m2ObserverJobData


    /**************************************************************************
     * Protected constructor for a new m2ObserverJobData object. <BR/>
     *
     * @param   context     The observer context.
     */
    protected M2ObserverJobData (ObserverContext context)
    {
        // call constructor of super class:
        super (context);
    } // m2ObserverJobData


    /**************************************************************************
     * Initializes m2ObserverJobObject.
     *
     * @param   context     The observer context.
     * @param   className   The name of the class which implements the observer.
     * @param   name        The name of the observer job.
     * @param   oid1        Oid of the first object.
     * @param   oid2        Oid of the second object.
     */
    public void init (ObserverContext context, String className, String name,
                      OID oid1, OID oid2)
    {
        super.init (context, className, name);
        this.oid1 = oid1;
        this.oid2 = oid2;
    } // init


    //
    // getters & setters
    //

    /**************************************************************************
     * Get the first oid. <BR/>
     *
     * @return  The oid.
     */
    public OID getOid1 ()
    {
        return this.oid1;
    } // getOid1


    /**************************************************************************
     * Get the second oid. <BR/>
     *
     * @return  The oid.
     */
    public OID getOid2 ()
    {
        return this.oid2;
    } // getOid2


    /**************************************************************************
     * Set the first oid. <BR/>
     *
     * @param   oid     The oid to be set.
     */
    protected void setOid1 (OID oid)
    {
        this.oid1 = oid;
    } // setOid1


    /**************************************************************************
     * Set the second oid. <BR/>
     *
     * @param   oid     The oid to be set.
     */
    protected void setOid2 (OID oid)
    {
        this.oid2 = oid;
    } // setOid2


    /**************************************************************************
     * Loads additional data for extended ObserverJobs.
     *
     * @param   action  The database connection object.
     *
     * @throws  ObserverException
     *          An error occurred.
     * @throws  DBError
     *          An error occurred during a database operation.
     */
    protected void loadAdditionalData (SQLAction action)
        throws ObserverException, DBError
    {
        int rowCount = 0;
        String oidStr = null;
        String query =
                " SELECT booid, wfoid FROM " + this.createTableName () +
                " WHERE id = " + this.getId ();

        // execute query
        rowCount = action.execute (query, false);

        // check if job is unique
        if (!action.getEOF ())
        {
            // fetch additional data
            oidStr = action.getString ("booid");

            try
            {
                this.oid1 = new OID (oidStr);
            } // try
            catch (IncorrectOidException e)
            {
                throw new ObserverException ("m2ObserverJobData.loadAdditionalData: booid not valid; " +
                    "value=" + oidStr + "; " + e.toString ());
            } // catch

            oidStr = action.getString ("wfoid");
            try
            {
                this.oid2 = new OID (oidStr);
            } // try
            catch (IncorrectOidException e)
            {
                throw new ObserverException ("m2ObserverJobData.loadAdditionalData: wfoid not valid; " +
                        "value=" + oidStr + "; " + e.toString ());
            } // catch

            // unique-constraint: check if more than one row returned
            rowCount = 1;
            action.next ();
            if (!action.getEOF ())
            {
                rowCount++;
            } // if
            action.end ();
        } // if

        // perform some consistency checks
        String err = "";
        if (rowCount == 0)
        {
            err = " does not exist.";
        } // if
        else if (rowCount != 1)
        {
            err = " is not unique.";
        } // else if

        if (rowCount != 1)
        {
            throw new ObserverException (
                "Error while loading additional m2ObserverJobData: Job with id = " +
                    this.getId () + err +
                    " Query: " + query +
                    "; " + " ObserverJobData=" + this.toString ());
        } // if
    } // loadAdditionalData

    /**************************************************************************
     * Loads additional data for extended ObserverJobs.
     *
     * @param   action  The database connection object.
     *
     * @throws  ObserverException
     *          An error occurred.
     * @throws  DBError
     *          An error occurred during a database operation.
     */
    protected void createAdditionalData (SQLAction action)
        throws ObserverException, DBError
    {
        this.checkOids ("createAdditionalData");

        String query =
            " INSERT INTO " + this.createTableName () +
            " (id, booid, wfoid) VALUES (" +
            this.getId () + ", " +
            this.oid1.toStringQu () + "," +
            this.oid2.toStringQu () + ")";

        action.execute (query, true);
        action.end ();
    } // saveAdditionalData


    /**************************************************************************
     * Updates additional data for extended ObserverJobs in one transaction. <BR/>
     *
     * @param   action  The database connection object.
     *
     * @throws  ObserverException
     *          An error occurred.
     * @throws  DBError
     *          An error occurred during a database operation.
     */
    protected void updateAdditionalData (SQLAction action)
        throws ObserverException, DBError
    {
        this.checkOids ("updateAdditionalData");

        String query =
            " UPDATE " + this.createTableName () + " SET" +
            "  booid = " + this.oid1.toStringQu () +
            ", wfoid = " + this.oid2.toStringQu () +
            " WHERE id = " + this.getId ();

        action.execute (query, true);
        action.end ();
    } // updateAdditionalData


    /**************************************************************************
     * Deletes (physically) additional data for extended ObserverJobs in one
     * transaction. <BR/>
     *
     * @param   action  The database connection object.
     *
     * @throws  ObserverException
     *          An error occurred.
     * @throws  DBError
     *          An error occurred during a database operation.
     */
    protected void deleteAdditionalData (SQLAction action)
        throws ObserverException, DBError
    {
        this.checkOids ("deleteAdditionalData");

        String query =
            " DELETE " + this.createTableName () +
            " WHERE id = " + this.getId ();

        action.execute (query, true);
        action.end ();
    } // deleteAdditionalData


    /**************************************************************************
     * Returns the unique-query. This query must assure that no job returned is
     * unique: results in one row.
     *
     * @return  The constructed query.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected String createUniquenessQuery () throws ObserverException
    {
        return
            " SELECT ext.id " +
            " FROM " + super.p_context.getTableName () + " base, " +
                     this.createTableName () + " ext " +
            " WHERE ext.booid = " + this.oid1.toStringQu () +
            "   AND ext.wfoid = " + this.oid2.toStringQu () +
            "   AND ext.id = base.id " +
            "   AND base.name = '" + this.getName () + "'" +
            "   AND base.className = '" + this.getClassName () + "'";
    } // createUniquityQuery


    /**************************************************************************
     * Tries to determine unique id based on given data: name, booid, wfoid,
     * classname, state=active|created|waiting|error|terminated. Only states
     * with state <> finished will return an id.
     *
     * @return  <CODE>id</CODE> ... unique job-id found. <BR/>
     *          <CODE>0</CODE>  ... no job found. <BR/>
     *          <CODE>-1</CODE> ... multiple jobs found (not-unique)
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected int determineUniqueIdNonFinished () throws ObserverException
    {
        SQLAction action = null;
        int jobId = 0;
        int rowCount = 0;
        String query = this.createUniquenessQuery () +
                " AND (base.state = " + ObserverJob.STATE_ACTIVE +
                " OR  base.state = " + ObserverJob.STATE_CREATED +
                " OR  base.state = " + ObserverJob.STATE_WAITING +
                " OR  base.state = " + ObserverJob.STATE_ERROR +
                " OR  base.state = " + ObserverJob.STATE_TERMINATED +
                ")";

        try
        {
            // open connection - begin transaction
            action = DBConnector.getDBConnection ();

            // execute query
            rowCount = action.execute (query, false);

            // check if job is unique
            if (!action.getEOF ())
            {
                // fetch additional data
                jobId = action.getInt ("id");

                // unique-constraint: check if more than one row returned
                rowCount = 1;
                action.next ();
                if (!action.getEOF ())
                {
                    rowCount++;
                } // if
                action.end ();
            } // if
        } // try
        catch (DBError err)
        {
            throw new ObserverException (
                "Error while determining unique id of m2ObserverJobData: " +
                    err.getMessage () + "; Query: " + query +
                    "; ObserverJobData=" + this.toString ());
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
                throw new ObserverException (
                    "Error while determining unique id of m2ObserverJobData: " +
                        e.getMessage () + "; Query: " + query +
                        "; ObserverJobData=" + this.toString ());
            } // catch
        } // finally

        if (rowCount == 0)
        {
            return 0;
        } // if
        else if (rowCount != 1)
        {
            return -1;
        } // else if
        else
        {
            return jobId;
        } // else
    } // determineUniqueIdNonFinished


    /**************************************************************************
     * Tries to determine unique id based on given data: name, booid, wfoid,
     * classname.
     *
     * @return  <CODE>id</CODE> ... unique job-id found. <BR/>
     *          <CODE>0</CODE>  ... no job found. <BR/>
     *          <CODE>-1</CODE> ... multiple jobs found (not-unique)
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected int determineUniqueId () throws ObserverException
    {
        SQLAction action = null;
        int jobId = 0;
        int rowCount = 0;
        String query = this.createUniquenessQuery ();

        try
        {
            // open connection - begin transaction
            action = DBConnector.getDBConnection ();

            // execute query
            rowCount = action.execute (query, false);

            // check if job is unique
            if (!action.getEOF ())
            {
                // fetch additional data
                jobId = action.getInt ("id");

                // unique-constraint: check if more than one row returned
                rowCount = 1;
                action.next ();
                if (!action.getEOF ())
                {
                    rowCount++;
                } // if
                action.end ();
            } // if
        } // try
        catch (DBError err)
        {
            throw new ObserverException (
                "Error while determining unique id of m2ObserverJobData: " +
                    err.getMessage () + "; Query: " + query +
                    "; ObserverJobData=" + this.toString ());
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
                throw new ObserverException (
                    "Error while determining unique id of m2ObserverJobData: " +
                        e.getMessage () + "; Query: " + query +
                        "; ObserverJobData=" + this.toString ());
            } // catch
        } // finally

        if (rowCount == 0)
        {
            return 0;
        } // if
        else if (rowCount != 1)
        {
            return -1;
        } // else if
        else
        {
            return jobId;
        } // else
    } // determineUniqueIdNonFinished


    /**************************************************************************
     * Create additional structure for extended ObserverJobs.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void createAdditionalStructure () throws ObserverException
    {
        SQLAction action = null;
        String[] ddl =
        {
            null, null, null,
        }; // ddl
        String tableName = this.createTableName ();
        String indexPrefix = this.createIndexPrefix ();

        // check initialization
        this.checkInitialization ("createAdditionalStructure");

        if (SQLConstants.DB_TYPE == SQLConstants.DB_ORACLE)
        {
            // generate ddl to create table
            ddl[0] = "CREATE TABLE " + tableName + " (" +
                  "id INTEGER NOT NULL, " +
                  "booid RAW (8) NOT NULL, " +
                  "wfoid RAW (8) NOT NULL" +
                  ")";

            // generate indexes
            ddl[1] = "CREATE UNIQUE INDEX " + indexPrefix +
                "1 ON " + tableName  + " (id)";
            ddl[2] = "CREATE INDEX " + indexPrefix +
                "2 ON " + tableName  + " (booid, wfoid)";
        } // if oracle
        else if (SQLConstants.DB_TYPE == SQLConstants.DB_MSSQL)
                                        // sql-server?
        {
            // generate ddl to create table
            ddl[0] = "CREATE TABLE " + tableName + " (" +
                  "id INTEGER NOT NULL, " +
                  "booid OBJECTID NOT NULL, " +
                  "wfoid OBJECTID NOT NULL" +
                  ")";

            // generate indexes
            ddl[1] = "CREATE UNIQUE INDEX " + indexPrefix +
                "1 ON " + tableName  + " (id)";
            ddl[2] = "CREATE INDEX " + indexPrefix +
                "2 ON " + tableName  + " (booid, wfoid)";
        } // else if sql-server
        else if (SQLConstants.DB_TYPE == SQLConstants.DB_DB2)
                                        // db2?
        {
            // generate ddl to create table
            ddl[0] = "CREATE TABLE " + tableName + " (" +
                  "id INTEGER NOT NULL, " +
                  "booid CHAR (8) FOR BIT DATA NOT NULL WITH DEFAULT X'0000000000000000', " +
                  "wfoid CHAR (8) FOR BIT DATA NOT NULL WITH DEFAULT X'0000000000000000'" +
                  ")";

            // generate indexes
            ddl[1] = "CREATE UNIQUE INDEX " + indexPrefix +
                "1 ON " + tableName  + " (id)";
            ddl[2] = "CREATE INDEX " + indexPrefix +
                "2 ON " + tableName  + " (booid, wfoid)";
        } // else if db2

        // perform query
        try
        {
            // open connection - begin transaction
            action = DBConnector.getDBConnection ();
            action.beginTrans ();

            // execute all ddls
            for (int i = 0; i < ddl.length; i++)
            {
                // insert jobs data
                action.execute (ddl[i], true);
                action.end ();
            } // for i

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

                throw new ObserverException (
                    "Error while creating structures for m2ObserverJobData: " +
                        e.getMessage () +
                        "; Queries" + StringHelpers.stringArrayToStringSC (ddl) +
                        "; ObserverJobData=" + this.toString ());
            } // try
            catch (DBError err)
            {
                throw new ObserverException (
                    "Error during rollback from creation of structures for m2ObserverJobData: " +
                        err.getMessage () +
                        "; Queries" + StringHelpers.stringArrayToStringSC (ddl) +
                        "; ObserverJobData=" + this.toString ());
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
                throw new ObserverException (
                    "Error while creating structures for m2ObserverJobData: " +
                        e.getMessage () +
                        "; Queries" + StringHelpers.stringArrayToStringSC (ddl) +
                        "; ObserverJobData=" + this.toString ());
            } // catch
        } // finally
    } // createAdditionalStructure


    /**************************************************************************
     * Drop additional structure for extended ObserverJobs.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void dropAdditionalStructure () throws ObserverException
    {
        SQLAction action = null;
        String[] ddl = {null};

        // generate ddl to drop table
        ddl[0] = "DROP TABLE " + this.createTableName ();

        // perform query
        try
        {
            // open connection - begin transaction
            action = DBConnector.getDBConnection ();
            action.beginTrans ();

            // execute all ddls
            for (int i = 0; i < ddl.length; i++)
            {
                // insert jobs data
                action.execute (ddl[i], true);
                action.end ();
            } // for

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

                throw new ObserverException (
                    "Error while dropping structures for m2ObserverJobData: " +
                        e.getMessage () +
                        "; Queries" + StringHelpers.stringArrayToStringSC (ddl) +
                        "; ObserverJobData=" + this.toString ());
            } // try
            catch (DBError err)
            {
                throw new ObserverException (
                    "Error during rollback from dropping structures for m2ObserverJobData: " +
                        err.getMessage () +
                        "; Queries" + StringHelpers.stringArrayToStringSC (ddl) +
                        "; ObserverJobData=" + this.toString ());
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
                throw new ObserverException (
                    "Error while dropping structures for m2ObserverJobData: " +
                        e.getMessage () +
                        "; Queries" + StringHelpers.stringArrayToStringSC (ddl) +
                        "; ObserverJobData=" + this.toString ());
            } // catch
        } // finally
    } // dropAdditionalStructure


    /**************************************************************************
     * Check additional structure for extended ObserverJobs. <BR/>
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void checkAdditionalStructure () throws ObserverException
    {
        SQLAction action = null;
        String query =
                " SELECT booid, wfoid FROM " + this.createTableName () +
                " WHERE id = " + this.getId ();

        // perform query
        try
        {
            // open connection - begin transaction
            action = DBConnector.getDBConnection ();

            // execute query:
            action.execute (query, false);
            action.end ();
        } // try
        catch (DBError e)
        {
            throw new ObserverException (
                "Error while checking structures for ObserverJobData: " +
                    e.getMessage () +
                    "; Queries" + query + "; ObserverJobData=" + this.toString ());
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
                throw new ObserverException (
                    "Error while checking structures for ObserverJobData: " +
                        e.getMessage () +
                        "; Queries" + query + "; ObserverJobData=" + this.toString ());
            } // catch
        } // finally
    } // checkAdditionalStructure

    /**************************************************************************
     * Checks for given operation if needed objects are initialized . <BR/>
     *
     * @param   op      The operation to be checked.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected final void checkOids (String op)
        throws ObserverException
    {
        if (this.oid1 == null)
        {
            throw new ObserverException ("Error during " +
                this.getClass ().getName () + "." + op +
                " (): booid not initialized.");
        } // if
        if (this.oid2 == null)
        {
            throw new ObserverException ("Error during " +
                this.getClass ().getName () + "." + op +
                " (): wfoid not initialized.");
        } // if
    } // checkInitialization


    /**************************************************************************
     * Creates the table name for additional data.
     *
     * @return  The created table name.
     */
    public String createTableName ()
    {
        // get the table name out of the context and return it:
        return this.getContext ().getTableName () +
            M2ObserverJobData.TABLE_POSTFIX;
    } // createTableName


    /**************************************************************************
     * Creates the index-prefix-name additional data.
     *
     * @return  The created index prefix.
     */
    public String createIndexPrefix ()
    {
        return this.getContext ().getIndexPrefix () +
            M2ObserverJobData.TABLE_POSTFIX;
    } // createIndexPrefix


    /**************************************************************************
     * Returns a string representation of the object. <BR/>
     *
     * @return  a string representation of the object.
     */
    public String toString ()
    {
        return "[base=" + super.toString () + "; extended=[booid=" + this.oid1 +
                "; wfoid=" + this.oid2 + "]]";
    } // toString


    /**************************************************************************
     * Check if this job data object is equal to another one. <BR/>
     *
     * @param   obj     The object to compare with.
     *
     * @return  <CODE>true</CODE> if the objects are equal,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean equals (M2ObserverJobData obj)
    {
        return super.equals (obj) && this.oid1.equals (obj.getOid1 ()) &&
            this.oid2.equals (obj.getOid2 ());
    } // equals


    /**************************************************************************
     * Returns a hash code value for the object. <BR/>
     *
     * @return  A hash code value for this object.
     */
    public int hashCode ()
    {
        // the common method of the super class is enough:
        return super.hashCode ();
    } // hashCode

} // M2ObserverJobData

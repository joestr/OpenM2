/*
 * Class: m2ParameterObserverJobData
 */

// package:
package ibs.service.observer;

// imports:
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.tech.sql.DBConnector;
import ibs.tech.sql.DBError;
import ibs.tech.sql.InsertStatement;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.UpdateStatement;
import ibs.util.StringHelpers;


/******************************************************************************
 * m2ParameterObserverJobData holds relevant base data for an
 * m2ParameterObserverJob.
 * <BR> This class also provides methods to load/save data and create/delete
 * the needed data-structures.
 *
 * @version     $Id: M2ParameterObserverJobData.java,v 1.4 2009/12/02 13:40:21 jzlattinger Exp $
 *
 * @author      HORST PICHLER
 ******************************************************************************
 */
public class M2ParameterObserverJobData extends M2ObserverJobData
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: M2ParameterObserverJobData.java,v 1.4 2009/12/02 13:40:21 jzlattinger Exp $";


    /**
     * Parameter.
     */
    protected  String param0 = null;
    /**
     * Parameter.
     */
    protected  String param1 = null;
    /**
     * Parameter.
     */
    protected  String param2 = null;
    /**
     * Parameter.
     */
    protected  String param3 = null;
    /**
     * Parameter.
     */
    protected  String param4 = null;
    /**
     * Parameter.
     */
    protected  String param5 = null;
    /**
     * Parameter.
     */
    protected  String param6 = null;
    /**
     * Parameter.
     */
    protected  String param7 = null;
    /**
     * Parameter.
     */
    protected  String param8 = null;
    /**
     * Parameter.
     */
    protected  String param9 = null;

    /**
     * Post fix for table and index name. <BR/>
     */
    private static final String TABLE_POSTFIX = "_param";


    /**************************************************************************
     * Public constructor. <BR/>
     */
    public M2ParameterObserverJobData ()
    {
        // may be overwritten in sub classes
    } // m2ParameterObserverJobData


    /**************************************************************************
     * Public constructor for a new m2ParameterObserverJobData object. <BR/>
     * To use with ObserverLoader.[un]register () or execute ().
     *
     * @param   context     Context of the observer job.
     * @param   className   Class name.
     * @param   name        Name of the job.
     * @param   paramOid1   Oid parameter 1.
     * @param   paramOid2   Oid parameter 2.
     * @param   param0      Parameter 0.
     * @param   param1      Parameter 1.
     * @param   param2      Parameter 2.
     * @param   param3      Parameter 3.
     * @param   param4      Parameter 4.
     * @param   param5      Parameter 5.
     * @param   param6      Parameter 6.
     * @param   param7      Parameter 7.
     * @param   param8      Parameter 8.
     * @param   param9      Parameter 9.
      */
    public M2ParameterObserverJobData (ObserverContext context,
                                       String className, String name,
                                       OID paramOid1, OID paramOid2,
                                       String param0, String param1,
                                       String param2, String param3,
                                       String param4, String param5,
                                       String param6, String param7,
                                       String param8, String param9)
    {
        // call cosntructor of super class:
        super (context, className, name, paramOid1, paramOid2);

        this.param0 = param0;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.param4 = param4;
        this.param5 = param5;
        this.param6 = param6;
        this.param7 = param7;
        this.param8 = param8;
        this.param9 = param9;
    } // m2ObserverJobData


    /**************************************************************************
     * Protected constructor.
     *
     * @param   context     The observer context.
     */
    protected M2ParameterObserverJobData (ObserverContext context)
    {
        // call cosntructor of super class:
        super (context);
    } // m2ObserverJobData


    /**************************************************************************
     * Initializes m2ObserverJobObject.
     *
     * @param   context     Context of the observer job.
     * @param   className   Class name.
     * @param   name        Name of the job.
     * @param   oid1        Oid parameter 1.
     * @param   oid2        Oid parameter 2.
     * @param   param0      Parameter 0.
     * @param   param1      Parameter 1.
     * @param   param2      Parameter 2.
     * @param   param3      Parameter 3.
     * @param   param4      Parameter 4.
     * @param   param5      Parameter 5.
     * @param   param6      Parameter 6.
     * @param   param7      Parameter 7.
     * @param   param8      Parameter 8.
     * @param   param9      Parameter 9.
     */
    public void init (ObserverContext context, String className, String name,
                      OID oid1, OID oid2, String param0, String param1,
                      String param2, String param3, String param4,
                      String param5, String param6, String param7,
                      String param8, String param9)
    {
        // call initializer from super class:
        super.init (context, className, name, oid1, oid2);

        this.param0 = param0;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.param4 = param4;
        this.param5 = param5;
        this.param6 = param6;
        this.param7 = param7;
        this.param8 = param8;
        this.param9 = param9;
    } // init


    //
    // getters
    //

    /**************************************************************************
     * Get the parameter number 0. <BR/>
     *
     * @return  The parameter.
     */
    protected String getParam0 ()
    {
        return this.param0;
    } // getParam0


    /**************************************************************************
     * Get the parameter number 1. <BR/>
     *
     * @return  The parameter.
     */
    protected String getParam1 ()
    {
        return this.param1;
    } // getParam1


    /**************************************************************************
     * Get the parameter number 2. <BR/>
     *
     * @return  The parameter.
     */
    protected String getParam2 ()
    {
        return this.param2;
    } // getParam2


    /**************************************************************************
     * Get the parameter number 3. <BR/>
     *
     * @return  The parameter.
     */
    protected String getParam3 ()
    {
        return this.param3;
    } // getParam3


    /**************************************************************************
     * Get the parameter number 4. <BR/>
     *
     * @return  The parameter.
     */
    protected String getParam4 ()
    {
        return this.param4;
    } // getParam4


    /**************************************************************************
     * Get the parameter number 5. <BR/>
     *
     * @return  The parameter.
     */
    protected String getParam5 ()
    {
        return this.param5;
    } // getParam5


    /**************************************************************************
     * Get the parameter number 6. <BR/>
     *
     * @return  The parameter.
     */
    protected String getParam6 ()
    {
        return this.param6;
    } // getParam6


    /**************************************************************************
     * Get the parameter number 7. <BR/>
     *
     * @return  The parameter.
     */
    protected String getParam7 ()
    {
        return this.param7;
    } // getParam7


    /**************************************************************************
     * Get the parameter number 8. <BR/>
     *
     * @return  The parameter.
     */
    protected String getParam8 ()
    {
        return this.param8;
    } // getParam8


    /**************************************************************************
     * Get the parameter number 9. <BR/>
     *
     * @return  The parameter.
     */
    protected String getParam9 ()
    {
        return this.param9;
    } // getParam9

    //
    // Setters
    //


    /**************************************************************************
     * Set the parameter number 0. <BR/>
     *
     * @param   param0  The parameter value to be set.
     */
    protected void setParam0 (String param0)
    {
        this.param0 = param0;
    } // setParam0


    /**************************************************************************
     * Set the parameter number 1. <BR/>
     *
     * @param   param1  The parameter value to be set.
     */
    protected void setParam1 (String param1)
    {
        this.param1 = param1;
    } // setParam1


    /**************************************************************************
     * Set the parameter number 2. <BR/>
     *
     * @param   param2  The parameter value to be set.
     */
    protected void setParam2 (String param2)
    {
        this.param2 = param2;
    } // setParam2


    /**************************************************************************
     * Set the parameter number 3. <BR/>
     *
     * @param   param3  The parameter value to be set.
     */
    protected void setParam3 (String param3)
    {
        this.param3 = param3;
    } // setParam3


    /**************************************************************************
     * Set the parameter number 4. <BR/>
     *
     * @param   param4  The parameter value to be set.
     */
    protected void setParam4 (String param4)
    {
        this.param4 = param4;
    } // setParam4


    /**************************************************************************
     * Set the parameter number 5. <BR/>
     *
     * @param   param5  The parameter value to be set.
     */
    protected void setParam5 (String param5)
    {
        this.param5 = param5;
    } // setParam5


    /**************************************************************************
     * Set the parameter number 6. <BR/>
     *
     * @param   param6  The parameter value to be set.
     */
    protected void setParam6 (String param6)
    {
        this.param6 = param6;
    } // setParam6


    /**************************************************************************
     * Set the parameter number 7. <BR/>
     *
     * @param   param7  The parameter value to be set.
     */
    protected void setParam7 (String param7)
    {
        this.param7 = param7;
    } // setParam7


    /**************************************************************************
     * Set the parameter number 8. <BR/>
     *
     * @param   param8  The parameter value to be set.
     */
    protected void setParam8 (String param8)
    {
        this.param8 = param8;
    } // setParam8


    /**************************************************************************
     * Set the parameter number 9. <BR/>
     *
     * @param   param9  The parameter value to be set.
     */
    protected void setParam9 (String param9)
    {
        this.param9 = param9;
    } // setParam9


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
            " WHERE ext.paramOid1 = " + this.oid1.toStringQu () +
            "   AND ext.paramOid2 = " + this.oid2.toStringQu () +
            "   AND ext.id = base.id " +
            "   AND base.name = '" + this.getName () + "'" +
            "   AND base.className = '" + this.getClassName () + "'";
    } // createUniquityQuery


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
                " SELECT paramOid1, paramOid2, param0, param1, param2, param3, " +
                "   param4, param5, param6, param7, param8, param9 " +
                " FROM " + this.createTableName () +
                " WHERE id = " + this.getId ();

        // execute query
        rowCount = action.execute (query, false);

        // check if job is unique
        if (!action.getEOF ())
        {
            // fetch additional data

            // paramOid1
            oidStr = action.getString ("paramOid1");
            if (!action.wasNull ())
            {
                try
                {
                    this.setOid1 (new OID (oidStr));
                } // try
                catch (IncorrectOidException e)
                {
                    throw new ObserverException (
                        "m2ParameterObserverJobData.loadAdditionalData: paramOid1 not valid; " +
                            "value=" + oidStr + "; " + e.toString ());
                } // catch
            } // if
            else
            {
                this.setOid1 (null);
            } // else

            // paramOid2
            oidStr = action.getString ("paramOid2");
            if (!action.wasNull ())
            {
                try
                {
                    this.setOid2 (new OID (oidStr));
                } // try
                catch (IncorrectOidException e)
                {
                    throw new ObserverException ("m2ParameterObserverJobData.loadAdditionalData: paramOid2 not valid; " +
                            "value=" + oidStr + "; " + e.toString ());
                } // catch
            } // if
            else
            {
                this.setOid2 (null);
            } // else

            this.param0 = action.getString ("param0");
            this.param1 = action.getString ("param1");
            this.param2 = action.getString ("param2");
            this.param3 = action.getString ("param3");
            this.param4 = action.getString ("param4");
            this.param5 = action.getString ("param5");
            this.param6 = action.getString ("param6");
            this.param7 = action.getString ("param7");
            this.param8 = action.getString ("param8");
            this.param9 = action.getString ("param9");

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
        // Create the insert statement
        InsertStatement stmt = new InsertStatement (this.createTableName (),
                "id, paramOid1, paramOid2",
                this.getId () + ", " +
                this.getOid1 ().toStringQu () + "," +
                this.getOid2 ().toStringQu ());
        stmt.addUnicodeString ("param0", this.param0);
        stmt.addUnicodeString ("param1", this.param1);
        stmt.addUnicodeString ("param2", this.param2);
        stmt.addUnicodeString ("param3", this.param3);
        stmt.addUnicodeString ("param4", this.param4);
        stmt.addUnicodeString ("param5", this.param5);
        stmt.addUnicodeString ("param6", this.param6);
        stmt.addUnicodeString ("param7", this.param7);
        stmt.addUnicodeString ("param8", this.param8);
        stmt.addUnicodeString ("param9", this.param9);
        
        stmt.execute (action);

        // end transaction
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
        // Create the update statement
        UpdateStatement stmt = new UpdateStatement (this.createTableName (),
                "paramOid1 = " + this.getOid1 ().toStringQu () +
                ", paramOid2 = " + this.getOid2 ().toStringQu (),
                "id = " + this.getId ());
        stmt.addUnicodeStringToSet ("param0", this.param0);
        stmt.addUnicodeStringToSet ("param1", this.param1);
        stmt.addUnicodeStringToSet ("param2", this.param2);
        stmt.addUnicodeStringToSet ("param3", this.param3);
        stmt.addUnicodeStringToSet ("param4", this.param4);
        stmt.addUnicodeStringToSet ("param5", this.param5);
        stmt.addUnicodeStringToSet ("param6", this.param6);
        stmt.addUnicodeStringToSet ("param7", this.param7);
        stmt.addUnicodeStringToSet ("param8", this.param8);
        stmt.addUnicodeStringToSet ("param9", this.param9);
        
        stmt.execute (action);

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
        String query =
            " DELETE " + this.createTableName () +
            " WHERE id = " + this.getId ();

        action.execute (query, true);
        action.end ();
    } // deleteAdditionalData


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
            null, null, null, null,
        };

        String tableName = this.createTableName ();
        String idxPrefix = this.createIndexPrefix ();

        // check initialization
        this.checkInitialization ("createAdditionalStructure");

        if (SQLConstants.DB_TYPE == SQLConstants.DB_ORACLE)
        {
            // generate ddl to create table
            ddl[0] = "CREATE TABLE " + tableName + " (" +
                  "id INTEGER NOT NULL, " +
                  "paramOid1 RAW (8), " +
                  "paramOid2 RAW (8), " +
                  "param0 NVARCHAR2 (1020), " +
                  "param1 NVARCHAR2 (1020), " +
                  "param2 NVARCHAR2 (1020), " +
                  "param3 NVARCHAR2 (1020), " +
                  "param4 NVARCHAR2 (1020), " +
                  "param5 NVARCHAR2 (1020), " +
                  "param6 NVARCHAR2 (1020), " +
                  "param7 NVARCHAR2 (1020), " +
                  "param8 NVARCHAR2 (1020), " +
                  "param9 NVARCHAR2 (1020)" +
                  ")";

            // generate indexes
            ddl[1] = "CREATE UNIQUE INDEX " + idxPrefix +
                "1 ON " + tableName  + " (id)";
            ddl[2] = "CREATE INDEX " + idxPrefix +
                "2 ON " + tableName  + " (paramOid1)";
            ddl[3] = "CREATE INDEX " + idxPrefix +
                "3 ON " + tableName  + " (paramOid2)";
        } // if oracle
        else if (SQLConstants.DB_TYPE == SQLConstants.DB_MSSQL)
                                        // sql-server?
        {
            // generate ddl to create table
            ddl[0] = "CREATE TABLE " + tableName + " (" +
                  "id INTEGER NOT NULL, " +
                  "paramOid1 OBJECTID, " +
                  "paramOid2 OBJECTID, " +
                  "param0 NVARCHAR (255), " +
                  "param1 NVARCHAR (255), " +
                  "param2 NVARCHAR (255), " +
                  "param3 NVARCHAR (255), " +
                  "param4 NVARCHAR (255), " +
                  "param5 NVARCHAR (255), " +
                  "param6 NVARCHAR (255), " +
                  "param7 NVARCHAR (255), " +
                  "param8 NVARCHAR (255), " +
                  "param9 NVARCHAR (255)" +
                  ")";

            // generate indexes:
            ddl[1] = "CREATE UNIQUE INDEX " + idxPrefix +
                "1 ON " + tableName  + " (id)";
            ddl[2] = "CREATE INDEX " + idxPrefix +
                "2 ON " + tableName  + " (paramOid1)";
            ddl[3] = "CREATE INDEX " + idxPrefix +
                "3 ON " + tableName  + " (paramOid2)";
        } // else if sql-server
        else if (SQLConstants.DB_TYPE == SQLConstants.DB_DB2)
                                        // db2?
        {
            // generate ddl to create table
            ddl[0] = "CREATE TABLE " + tableName + " (" +
                  "id INTEGER NOT NULL, " +
                  "paramOid1 CHAR (8) FOR BIT DATA NOT NULL WITH DEFAULT X'0000000000000000', " +
                  "paramOid2 CHAR (8) FOR BIT DATA NOT NULL WITH DEFAULT X'0000000000000000', " +
                  "param0 VARCHAR (1020), " +
                  "param1 VARCHAR (1020), " +
                  "param2 VARCHAR (1020), " +
                  "param3 VARCHAR (1020), " +
                  "param4 VARCHAR (1020), " +
                  "param5 VARCHAR (1020), " +
                  "param6 VARCHAR (1020), " +
                  "param7 VARCHAR (1020), " +
                  "param8 VARCHAR (1020), " +
                  "param9 VARCHAR (1020)" +
                  ")";

            // generate indexes:
            ddl[1] = "CREATE UNIQUE INDEX " + idxPrefix +
                "1 ON " + tableName  + " (id)";
            ddl[2] = "CREATE INDEX " + idxPrefix +
                "2 ON " + tableName  + " (paramOid1)";
            ddl[3] = "CREATE INDEX " + idxPrefix +
                "3 ON " + tableName  + " (paramOid2)";
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
                " SELECT paramOid1, paramOid2, param0, param1, param2, param3, " +
                "   param4, param5, param6, param7, param8, param9 " +
                " FROM " + this.createTableName () +
                " WHERE id = " + this.getId ();

        // perform query
        try
        {
            // open connection - begin transaction
            action = DBConnector.getDBConnection ();

            // execute query
            action.execute (query, false);
            action.end ();
        } // try
        catch (DBError e)
        {
            throw new ObserverException (
                "Error while checking structures for ObserverJobData: " +
                    e.getMessage () + "; Queries" + query +
                    "; ObserverJobData=" + this.toString ());
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
                throw new ObserverException (
                    "Error while checking structures for ObserverJobData: " +
                        e.getMessage () + "; Queries" + query +
                        "; ObserverJobData=" + this.toString ());
            } // catch
        } // finally
    } // checkAdditionalStructure


    /**************************************************************************
     * Creates the table name for additional data.
     *
     * @return  The created table name.
     */
    public String createTableName ()
    {
        return this.getContext ().getTableName () +
            M2ParameterObserverJobData.TABLE_POSTFIX;
    } // createTableName


    /**************************************************************************
     * Creates the index-prefix-name additional data.
     *
     * @return  The created index prefix.
     */
    public String createIndexPrefix ()
    {
        return this.getContext ().getIndexPrefix () +
            M2ParameterObserverJobData.TABLE_POSTFIX;
    } // createIndexPrefix


    /**************************************************************************
     * Returns a string representation of the object. <BR/>
     *
     * @return  a string representation of the object.
     */
    public String toString ()
    {
        return "[base=" + super.toString () +
            "; extended=[paramOid1=" + this.getOid1 () +
            "; paramOid2=" + this.getOid2 () +
            "; param0=" + this.param0 + "; param1=" + this.param1 +
            "; param2=" + this.param2 + "; param3=" + this.param3 +
            "; param4=" + this.param4 + "; param5=" + this.param5 +
            "; param6=" + this.param6 + "; param7=" + this.param7 +
            "; param8=" + this.param8 + "; param9=" + this.param9 + "]]";
    } // toString

} // m2ParameterObserverJobData

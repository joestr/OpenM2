/*
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 31, 2002
 * Time: 10:49:17 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */

// package:
package ibs.service.observer;

// imports:
import ibs.service.observer.Counter;
import ibs.service.observer.CounterException;
import ibs.service.observer.ObserverConstants;
import ibs.service.observer.ObserverContext;
import ibs.service.observer.ObserverException;
import ibs.service.observer.ObserverJob;
import ibs.service.observer.ObserverJobDataComparator;
import ibs.tech.sql.DBConnector;
import ibs.tech.sql.DBError;
import ibs.tech.sql.InsertStatement;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.UpdateStatement;
import ibs.util.DateTimeHelpers;
import ibs.util.StringHelpers;

import java.util.Date;
import java.util.TreeSet;


/******************************************************************************
 * ObserverJobData holds relevant base data for an ObserverJob. <BR> This class
 * also provides methods to load/save data and create/delete the needed
 * data-structures.
 *
 * @version     $Id: ObserverJobData.java,v 1.9 2009/12/02 13:40:21 jzlattinger Exp $
 *
 * @author      HORST PICHLER, 31.07.2002
 ******************************************************************************
 */
public class ObserverJobData
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ObserverJobData.java,v 1.9 2009/12/02 13:40:21 jzlattinger Exp $";


    //
    // Persistent data.
    //
    /**
     * Unique id of observer-job
     */
    private int p_id = ObserverConstants.UNDEFINED_INTEGER;
    /**
     * Unique name of observer-job
     */
    private String p_name = null;
     /**
     * Date and time of jobs creation.
     */
    private Date p_created = null;
     /**
     * Date and time of jobs next execution;
     */
    private Date p_nextExecution = null;
     /**
     * Date and time of jobs last execution;
     */
    private Date p_prevExecution = null;
     /**
     * Number of executions until now.
     */
    private int p_executionCount = ObserverConstants.UNDEFINED_INTEGER;
     /**
     * Jobs current state.
     */
    private int p_state = ObserverJob.STATE_UNDEFINED;
    /**
     * Name of the class where the specific ObserverJob-logic is implemented
     */
    private String p_className = null;
    /**
     * Indicates if job shall be triggered again on next execution.
     */
    private boolean p_retryOnError = true;
    /**
     * Number of retry attempts.
     */
    private int p_retryAttempts = 0;

    /**
     * The hash code. <BR/>
     */
    private int p_hashCode = Integer.MIN_VALUE;

    /**
     * Error message: ObserverJobData = . <BR/>
     */
    private static final String ERRM_OBSJOBDATA = "; ObserverJobData=";

    //
    // Non-persistent data.
    //
    /**
     * The observer context for the job.
     */
    protected ObserverContext p_context = null;

    //
    // constructors
    //

    /**************************************************************************
     * Public constructor. <BR/>
     */
    public ObserverJobData ()
    {
        // nothing to do
    } // ObserverJobData


    /**************************************************************************
     * Public constructor for a new ObserverJobData object.<BR> To use with
     * ObserverLoader.[un]register () or execute ().
     *
     * @param   className   The name of the class.
     * @param   name        The name of the observer job.
     */
    public ObserverJobData (String className, String name)
    {
        this.p_className = className;
        this.p_name = name;
    } // ObserverJobData


    /**************************************************************************
     * Constructor for a new ObserverJobData object.<BR> Use 'create' to
     * store objects data and create unique id.
     *
     * @param   context     The context for the observer.
     * @param   className   The name of the class.
     * @param   name        The name of the observer job.
     */
    protected ObserverJobData (ObserverContext context,
                            String className,
                            String name)
    {
        this.p_context = context;
        this.p_className = className;
        this.p_name = name;
    } // ObserverJobData


    /**************************************************************************
     * Constructor for an empty ObserverJobData object. <BR/>
     * For creation and deletion of datastructures where no data-manipulation
     * is performed.
     *
     * @param   context     The context for the observer.
     */
    protected ObserverJobData (ObserverContext context)
    {
        this.p_context = context;
    } // ObserverJobData


    /**************************************************************************
     * Constructor for an existing ObserverJobData object. <BR/>
     * Only used in ObserverLoader.
     *
     * @param   context         The context for the observer.
     * @param   className       The name of the class.
     * @param   id              Id of the observer job.
     * @param   name            The name of the observer job.
     * @param   created         Date/time when the job was created.
     * @param   nextExecution   Date/time of next execution.
     * @param   prevExecution   Date/time of previous execution.
     * @param   executionCount  Number of executions.
     * @param   state           Current state of the job.
     * @param   retryOnError    Shall there be a retry after an error?
     * @param   retryAttempts   Number of retry attempts.
     */
    protected ObserverJobData (ObserverContext context, String className,
                               int id, String name, Date created,
                               Date nextExecution, Date prevExecution,
                               int executionCount, int state,
                               boolean retryOnError, int retryAttempts)
    {
        this.p_context = context;
        this.p_className = className;
        this.p_id = id;
        this.p_name = name;
        this.p_created = created;
        this.p_nextExecution = nextExecution;
        this.p_prevExecution = prevExecution;
        this.p_executionCount = executionCount;
        this.p_state = state;
        this.p_retryOnError = retryOnError;
        this.p_retryAttempts = retryAttempts;
    } // ObserverJobData


    /**************************************************************************
     * Constructor for an ObserverJobData object. <BR/>
     * For testing purposes only.
     *
     * @param   context         The context for the observer.
     * @param   className       The name of the class.
     * @param   name            The name of the observer job.
     * @param   created         Date/time of job creation.
     * @param   nextExecution   Date/time of next execution.
     * @param   prevExecution   Date/time of previous execution.
     * @param   executionCount  Number of executions.
     * @param   state           Current state of the job.
     * @param   retryOnError    Shall there be a retry after an error?
     * @param   retryAttempts   Number of retry attempts.
     */
    protected ObserverJobData (ObserverContext context, String className,
                               String name, Date created, Date nextExecution,
                               Date prevExecution, int executionCount,
                               int state, boolean retryOnError,
                               int retryAttempts)
    {
        this.p_context = context;
        this.p_className = className;
        this.p_name = name;
        this.p_created = created;
        this.p_nextExecution = nextExecution;
        this.p_prevExecution = prevExecution;
        this.p_executionCount = executionCount;
        this.p_state = state;
        this.p_retryOnError = retryOnError;
        this.p_retryAttempts = retryAttempts;
    } // ObserverJobData


    /**************************************************************************
     * Initializes the object.
     *
     * @param   context     The context for the observer.
     */
    public void init (ObserverContext context)
    {
        this.p_context = context;
    } // init


    /**************************************************************************
     * Initializes the object.
     *
     * @param   context     The context for the observer.
     * @param   className   The name of the class.
     * @param   name        The name of the observer job.
     */
    public void init (ObserverContext context, String className, String name)
    {
        this.p_context = context;
        this.p_className = className;
        this.p_name = name;
    } // init


    ///////////////////////////////////////////////////////////////////////////
    //
    // getters
    //
    //

    /**************************************************************************
     * Get the observer job id. <BR/>
     *
     * @return  The id.
     */
    public final int getId ()
    {
        return this.p_id;
    } // getId


    /**************************************************************************
     * Get the observer job name. <BR/>
     *
     * @return  The name.
     */
    public final String getName ()
    {
        return this.p_name;
    } // getName


    /**************************************************************************
     * Get the date/time when the observer job was created. <BR/>
     *
     * @return  The date when the observer job was created.
     */
    public final Date getCreated ()
    {
        return this.p_created;
    } // getCreated


    /**************************************************************************
     * Get the next execution date and time. <BR/>
     *
     * @return  The next execution date/time.
     */
    public final Date getNextExecution ()
    {
        return this.p_nextExecution;
    } // getNextExecution


    /**************************************************************************
     * Get the last execution date and time. <BR/>
     *
     * @return  The last execution date/time.
     */
    public final Date getPrevExecution ()
    {
        return this.p_prevExecution;
    } // getPrevExecution


    /**************************************************************************
     * Get the number of executions thus far. <BR/>
     *
     * @return  The execution count.
     */
    public final int getExecutionCount ()
    {
        return this.p_executionCount;
    } // getExecutionCount


    /**************************************************************************
     * Get the state of the observer job. <BR/>
     *
     * @return  The state.
     */
    public final int getState ()
    {
        return this.p_state;
    } // getState


    /**************************************************************************
     * Check whether the operation shall be retried if there occurs an error.
     * <BR/>
     *
     * @return  <CODE>true</CODE> if the operation shall be retried,
     *          <CODE>false</CODE> otherwise.
     */
    public final boolean getRetryOnError ()
    {
        return this.p_retryOnError;
    } // getRetryOnError


    /**************************************************************************
     * Get the number of attempts which shall be made for retry. <BR/>
     *
     * @return  The possible number of attempts.
     */
    public final int getRetryAttempts ()
    {
        return this.p_retryAttempts;
    } // getRetryAttempts


    /**************************************************************************
     * Get the name of the implementing class. <BR/>
     *
     * @return  The class name.
     *          <CODE>null</CODE> if no class name was set.
     */
    public final String getClassName ()
    {
        return this.p_className;
    } // getClassName


    /**************************************************************************
     * Get the context of the observer. <BR/>
     *
     * @return  The context.
     */
    public final ObserverContext getContext ()
    {
        return this.p_context;
    } // getContext


    ///////////////////////////////////////////////////////////////////////////
    //
    // setters
    //
    //

/*
must not be used ==> id will be generated
    protected void setId (int id)
    {
        this.p_id = id;
    }
*/

    /**************************************************************************
     * Set the name of the observer job. <BR/>
     *
     * @param   name    The name to be set.
     */
    protected final void setName (String name)
    {
        this.p_name = name;
    } // setName


    /**************************************************************************
     * Set the date and time when the observer job was created. <BR/>
     *
     * @param   created The date/time to be set.
     */
    protected final void setCreated (Date created)
    {
        this.p_created = created;
    } // setCreated


    /**************************************************************************
     * Set the date and time when the observer job shall be executed next. <BR/>
     *
     * @param   nextExecution   Date/time of next execution.
     */
    protected final void setNextExecution (Date nextExecution)
    {
        this.p_nextExecution = nextExecution;
    } // setNextExecution


    /**************************************************************************
     * Set the date and time of the last execution. <BR/>
     *
     * @param   prevExecution   Date/time of last execution.
     */
    protected final void setPrevExecution (Date prevExecution)
    {
        this.p_prevExecution = prevExecution;
    } // setPrevExecution


    /**************************************************************************
     * Set the number of executions. <BR/>
     *
     * @param   executionCount  The current execution counter value.
     */
    protected final void setExecutionCount (int executionCount)
    {
        this.p_executionCount = executionCount;
    } // setExecutionCount


    /**************************************************************************
     * Set the state of the observer job. <BR/>
     *
     * @param   state   The state to be set.
     */
    protected final void setState (int state)
    {
        this.p_state = state;
    } // setState


    /**************************************************************************
     * Set whether the operation shall be retried if there occurs an error.
     * <BR/>
     *
     * @param   retryOnError    <CODE>true</CODE> if the operation shall be
     *                          retried,
     *                          <CODE>false</CODE> otherwise.
     */
    protected final void setRetryOnError (boolean retryOnError)
    {
        this.p_retryOnError = retryOnError;
    } // setRetryOnError


    /**************************************************************************
     * Set the number of retry attempts. <BR/>
     *
     * @param   retryAttempts   The number of attempts to be set.
     */
    protected final void setRetryAttempts (int retryAttempts)
    {
        this.p_retryAttempts = retryAttempts;
    } // setRetryAttempts


    /**************************************************************************
     * Set the name of the class which implements the observer job. <BR/>
     *
     * @param   className   The class name.
     */
    protected final void setClassName (String className)
    {
        this.p_className = className;
    } // setClassName


    /**************************************************************************
     * Set the observer context. <BR/>
     *
     * @param   context The context to be set.
     */
    public void setContext (ObserverContext context)
    {
        this.p_context = context;
    } // setContext


    ///////////////////////////////////////////////////////////////////////////
    //
    // "abstract" methods
    //
    //

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
        throw new ObserverException (
            this.getClass ().getName () + ".createUniquenessQuery not implemented!");
    } // createUniquenessQuery


    /**************************************************************************
     * Tries to determine unique id based on given data: name, booid, wfoid,
     * classname, state=active|created|waiting|error|terminated. Only jobs
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
        throw new ObserverException (
            this.getClass ().getName () + ".determineUniqueIdNonFinished not implemented!");
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
        throw new ObserverException (
            this.getClass ().getName () + ".determineUniqueId not implemented!");
    } // determineUniqueId


    /**************************************************************************
     * Save additional data for extended ObserverJobs in one transaction. <BR/>
     *
     * @param   action  The database connection object.
     *
     * @throws  ObserverException
     *          An error occurred.
     * @throws  DBError
     *          An error occurred in database operation.
     */
    protected void createAdditionalData (SQLAction action)
        throws ObserverException, DBError
    {
        // please overwrite
    } // saveAdditionalData


    /**************************************************************************
     * Loads additional data for extended ObserverJobs.
     *
     * @param   action  The database connection object.
     *
     * @throws  ObserverException
     *          An error occurred.
     * @throws  DBError
     *          An error occurred in database operation.
     */
    protected void loadAdditionalData (SQLAction action)
        throws ObserverException, DBError
    {
        // please overwrite
    } // loadAdditionalData


    /**************************************************************************
     * Updates additional data for extended ObserverJobs in one transaction. <BR/>
     *
     * @param   action  The database connection object.
     *
     * @throws  ObserverException
     *          An error occurred.
     * @throws  DBError
     *          An error occurred in database operation.
     */
    protected void updateAdditionalData (SQLAction action)
        throws ObserverException, DBError
    {
        // please overwrite
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
     *          An error occurred in database operation.
     */
    protected void deleteAdditionalData (SQLAction action)
        throws ObserverException, DBError
    {
        // please overwrite
    } // deleteAdditionalData


    /**************************************************************************
     * Create additional structure for extended ObserverJobs.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void createAdditionalStructure () throws ObserverException
    {
        // please overwrite
    } // createAdditionalStructure


    /**************************************************************************
     * Drops additional structure for extended ObserverJobs.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void dropAdditionalStructure () throws ObserverException
    {
        // please overwrite
    } // dropAdditionalStructure


    /**************************************************************************
     * Check additional structure for extended ObserverJobs. <BR/>
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void checkAdditionalStructure () throws ObserverException
    {
        // please overwrite
    } // checkAdditionalStructure



    ///////////////////////////////////////////////////////////////////////////
    //
    // data manipulation methods
    //
    //

    /**************************************************************************
     * Loads ObserverJobData. <BR/>
     * Base data will be loaded in this method, for additional data please
     * overwrite the method 'loadAdditionalData'.
     *
     * @param   id      The id of the observer job to be loaded.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected final void load (int id) throws ObserverException
    {
        SQLAction action = null;
        int rowCount = 0;

        // check initialization
        this.checkInitialization ("load");
        if (id <= 0)
        {
            throw new ObserverException (
                "Could not load ObserverJobData: No id given." +
                ObserverJobData.ERRM_OBSJOBDATA + this.toString ());
        } // if

        // set id
        this.p_id = id;

        // generate SELECT query-string
        String query = this.createQuery () + " AND (id = " + this.p_id + ")";

        // perform query
        try
        {
            // open connection - begin transaction
            action = DBConnector.getDBConnection ();

            // execute query
            rowCount = action.execute (query, false);

            // check if job is unique
            if (!action.getEOF ())
            {
                // fetch base data of job
                this.p_name = action.getString ("name");
                this.p_created = action.getDate ("created");
                this.p_nextExecution = action.getDate ("nextExecution");
                this.p_prevExecution = action.getDate ("prevExecution");
                this.p_executionCount = action.getInt ("executionCount");
                this.p_state = action.getInt ("state");
                this.p_retryOnError = action.getBoolean ("retryOnError");
                this.p_retryAttempts = action.getInt ("retryAttempts");
                this.p_className = action.getString ("className");

                // unique-constraint: check if more than one row returned
                rowCount = 1;
                action.next ();
                if (!action.getEOF ())
                {
                    rowCount++;
                } // if
                action.end ();

                // handle additional data
                this.loadAdditionalData (action);
            } // if (rowCount == 1)
            // otherwise skip - throw exception at end of method
        } // try
        catch (DBError e)
        {
            throw new ObserverException (
                "Error while loading ObserverJobData: " + e.getMessage () +
                    " Query: " + query + ObserverJobData.ERRM_OBSJOBDATA +
                    this.toString ());
        } // catch
        finally
        {
            // close action and db-connection
            try
            {
                action.end ();
                DBConnector.releaseDBConnection (action);
            } // try            catch (DBError e)
            catch (DBError e)
            {
                throw new ObserverException (
                    "Error while loading ObserverJobData: " + e.getMessage () +
                        " Query: " + query + ObserverJobData.ERRM_OBSJOBDATA +
                        this.toString ());
            } // catch
        } // finally

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
                "Error while loading ObserverJobData: Job with id = " +
                    this.p_id + err + " Query: " + query +
                    ObserverJobData.ERRM_OBSJOBDATA + this.toString ());
        } // if

    } // load

    /**************************************************************************
     * Saves ObserverJobData in one transaction and completes/creates some
     * of the base data. <BR>Please use constructor ObserverJobData (context,
     * className, name) before calling create. The following data will be
     * created: unique id, state=CREATED, created=now, prevExecution=null,
     * nextExecution=this.calculateFirstExecution (), executionCount=0.
     * <BR> To create additional data please overwrite the method
     * 'createAdditionalData'.
     *
     * @return  Unique id which identifies this object.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected final int create () throws ObserverException
    {
        // check initialization
        this.checkInitialization ("create");
        if (this.p_id > 0)
        {
            throw new ObserverException ("Error during creation of ObserverJobData:" +
                " Not allowed to create data where id is already set! Use another constructor." +
                " data=" + this.toString ());
        } // if
        if (this.p_className == null || this.p_className.length () == 0)
        {
            throw new ObserverException ("Error during creation of ObserverJobData:" +
                " Not allowed to create data where classname is not set." +
                " data=" + this.toString ());
        } // if
        if (this.p_name == null || this.p_name.length () == 0)
        {
            throw new ObserverException ("Error during creation of ObserverJobData:" +
                " Not allowed to create data where name is not set." +
                " data=" + this.toString ());
        } // if
        if (this.p_state != ObserverJob.STATE_ACTIVE &&
            this.p_state != ObserverJob.STATE_CREATED &&
            this.p_state != ObserverJob.STATE_ERROR &&
            this.p_state != ObserverJob.STATE_FINISHED &&
            this.p_state != ObserverJob.STATE_PAUSED &&
            this.p_state != ObserverJob.STATE_UNDEFINED &&
            this.p_state != ObserverJob.STATE_WAITING &&
            this.p_state != ObserverJob.STATE_TERMINATED &&
            this.p_state != ObserverJob.STATE_UNREGISTERED)
        {
            throw new ObserverException ("Error during creation of ObserverJobData:" +
                " Unknown state=" + this.p_state +
                " data=" + this.toString ());
        } // if

        SQLAction action = null;

        // create unique id
        try
        {
            Counter cnt = new Counter (this.p_context.getTableName ());
            this.p_id = cnt.getNext ();
        } // try
        catch (CounterException e)
        {
            this.p_id = ObserverConstants.UNDEFINED_INTEGER;
            throw new ObserverException (
                "Error during creation of ObserverJobDataq; Counter Problem: " +
                    e.getMessage ());
        } // catch
      
        // Create the insert statement
        InsertStatement stmt = new InsertStatement (this.p_context.getTableName (),
                "id, created, nextExecution, prevExecution, executionCount, retryOnError, retryAttempts, className",
                this.p_id +
                ","  + ObserverJobData.dateTimeToDBString (this.p_created) +
                ","  + ObserverJobData.dateTimeToDBString (this.p_nextExecution) +
                ","  + ObserverJobData.dateTimeToDBString (this.p_prevExecution) +
                ","  + this.p_executionCount +
                ","  + (this.p_retryOnError ? 1 : 0) +  // convert boolean value
                ","  + this.p_retryAttempts +
                ",'" + this.p_className + "'");
        
        stmt.addUnicodeString ("name", this.p_name);
        stmt.addUnicodeString ("state", Integer.toString (this.p_state));

        // perform query
        try
        {
            // open connection - begin transaction
            action = DBConnector.getDBConnection ();
            action.beginTrans ();

            // insert jobs data
            stmt.execute (action);
            action.end ();

            // handle additional data
            this.createAdditionalData (action);

            // commit transaction
            action.commitTrans ();
        } // try
        catch (DBError e)
        {
            // reset p_id
            this.p_id = ObserverConstants.UNDEFINED_INTEGER;

            // get error message:
            // close action and db-connection
            try
            {
                action.rollbackTrans ();
                throw new ObserverException (
                    "Error while creating ObserverJobData: " + e.getMessage () +
                        " Query: " + stmt + ObserverJobData.ERRM_OBSJOBDATA +
                        this.toString ());
            } // try
            catch (DBError err)
            {
                throw new ObserverException (
                    "Error during rollback from creation of ObserverJobData: " +
                        err.getMessage () + " Query: " + stmt +
                        ObserverJobData.ERRM_OBSJOBDATA + this.toString ());
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
                    "Error while creating ObserverJobData: " + e.getMessage () +
                        " Query: " + stmt + ObserverJobData.ERRM_OBSJOBDATA +
                        this.toString ());
            } // catch
        } // finally

        return this.p_id;

    } // create

    /**************************************************************************
     * Updates ObserverJobData in one transaction. <BR> Base data
     * will be updated in this method, for additional data please overwrite the
     * method 'updateAdditionalData'.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected final void update () throws ObserverException
    {
        // check initialization
        this.checkInitialization ("update");
        if (this.p_id <= 0)
        {
            throw new ObserverException (
                "Could not load ObserverJobData: No id given." +
                ObserverJobData.ERRM_OBSJOBDATA + this.toString ());
        } // if

        SQLAction action = null;
       
        // Create the update statement
        UpdateStatement stmt = new UpdateStatement ( this.p_context.getTableName (),
                " id = "  + this.p_id +
                ", created = " + ObserverJobData.dateTimeToDBString (this.p_created) +
                ", nextExecution = "  + ObserverJobData.dateTimeToDBString (this.p_nextExecution) +
                ", prevExecution = "  + ObserverJobData.dateTimeToDBString (this.p_prevExecution) +
                ", executioncount = "  + this.p_executionCount +
                ", retryOnError = "  + (this.p_retryOnError ? 1 : 0) +  // convert boolean value
                ", retryAttempts = "  + this.p_retryAttempts +
                ", className = '" + this.p_className + "'",
                "id = " + this.p_id);

        stmt.addUnicodeStringToSet ("name", this.p_name);
        stmt.addUnicodeStringToSet ("state", Integer.toString (this.p_state));

        // perform query
        try
        {
            // open connection - begin transaction
            action = DBConnector.getDBConnection ();
            action.beginTrans ();

            // insert jobs data
            stmt.execute (action);
            action.end ();

            // handle additional data
            this.updateAdditionalData (action);

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
                    "Error while updating ObserverJobData: " + e.getMessage () +
                        " Query: " + stmt + ObserverJobData.ERRM_OBSJOBDATA +
                        this.toString ());
            } // try
            catch (DBError err)
            {
                throw new ObserverException (
                    "Error during rollback from updating ObserverJobData: " +
                        err.getMessage () + " Query: " + stmt +
                        ObserverJobData.ERRM_OBSJOBDATA + this.toString ());
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
                    "Error while updating ObserverJobData: " + e.getMessage () +
                        " Query: " + stmt + ObserverJobData.ERRM_OBSJOBDATA +
                        this.toString ());
            } // catch
        } // finally
    } // update


    /**************************************************************************
     * Deletes (physically) ObserverJobData in one transaction. <BR> Base data
     * will be deleted in this method, for additional data please overwrite the
     * method 'deleteAdditionalData'.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected final void delete () throws ObserverException
    {
        // check initialization
        this.checkInitialization ("delete");

        SQLAction action = null;

        // generate UPDATE query-string
        String query =
            " DELETE " + this.p_context.getTableName ();
        // add WHERE-clause
        if (this.p_id > 0)            // is id set?
        {
            query += " WHERE id = " + this.p_id;
        } // if is id set
        else                                    // ... or is neither id nor name set
        {
            throw new ObserverException (
                "Could not delete ObserverJobData: no id or name given." +
                ObserverJobData.ERRM_OBSJOBDATA + this.toString ());
        } // else ... of is neither id nor name set

        // save data
        try
        {
            // open connection - begin transaction
            action = DBConnector.getDBConnection ();
            action.beginTrans ();

            // insert jobs data
            action.execute (query, true);
            action.end ();

            // handle additional data
            this.deleteAdditionalData (action);

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
                    "Error while deleting ObserverJobData: " + e.getMessage () +
                        " Query: " + query + ObserverJobData.ERRM_OBSJOBDATA +
                        this.toString ());
            } // try
            catch (DBError err)
            {
                throw new ObserverException (
                    "Error during rollback from deleting ObserverJobData: " +
                        err.getMessage () + " Query: " + query +
                        ObserverJobData.ERRM_OBSJOBDATA + this.toString ());
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
                    "Error while deleting ObserverJobData: " + e.getMessage () +
                        " Query: " + query + ObserverJobData.ERRM_OBSJOBDATA +
                        this.toString ());
            } // catch
        } // finally
    } // delete

    /**************************************************************************
     * Checks if data for given id exist. Properties of object will not be
     * stored or changed. <BR/>
     *
     * @param   id      The id of the observer job to search for.
     *
     * @return  <CODE>false</CODE> if job does not exist.
     *          <CODE>true</CODE> if job exists.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected final boolean exists (int id) throws ObserverException
    {
        SQLAction action = null;
        int rowCount = 0;

        // check initialization
        this.checkInitialization ("exists");
        if (id <= 0)
        {
            throw new ObserverException (
                "Could not check existence of ObserverJobData: No id given." +
                ObserverJobData.ERRM_OBSJOBDATA + this.toString ());
        } // if

        // generate SELECT query-string
        String query = this.createQuery () +
                " AND (id = " + id + ")";

        // perform query
        try
        {
            // open connection - begin transaction
            action = DBConnector.getDBConnection ();

            // execute query
            rowCount = action.execute (query, false);

            // check if job is exists
            if (!action.getEOF ())
            {
                // unique-constraint: check if more than one row returned
                rowCount = 1;
                action.next ();
                if (!action.getEOF ())
                {
                    rowCount++;
                } // if
                action.end ();
            } // if (rowCount == 1)
            // otherwise skip - throw exception at end of method

        } // try
        catch (DBError e)
        {
            throw new ObserverException (
                "Error while checking existence of ObserverJobData: " +
                    e.getMessage () + " Query: " + query +
                    ObserverJobData.ERRM_OBSJOBDATA + this.toString ());
        } // catch
        finally
        {
            // close action and db-connection
            try
            {
                action.end ();
                DBConnector.releaseDBConnection (action);
            } // try            catch (DBError e)
            catch (DBError e)
            {
                throw new ObserverException (
                    "Error while checking existence of ObserverJobData:" +
                        e.getMessage () + " Query: " + query +
                        ObserverJobData.ERRM_OBSJOBDATA + this.toString ());
            } // catch
        } // finally

        // perform some consistency checks
        if (rowCount == 0)
        {
            return false;
        } // if
        else if (rowCount == 1)
        {
            return true;
        } // else if
        else
        {
            throw new ObserverException (
                "Error while checking existence ObserverJobData: Job with id = " +
                    this.p_id +
                    " is not unique." +
                    " Query: " +
                    query +
                    ObserverJobData.ERRM_OBSJOBDATA + this.toString ());
        } // if
    } // exists

    /**************************************************************************
     * Loads base data of all ObserverJobs with STATE_ACTIVE which exist in
     * given context and returns them as a TreeSet of ObserverJobData-objects.
     *
     * @return  The tree set of ObserverJobData objects.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected final TreeSet<ObserverJobData> loadBaseDataOfActiveJobs ()
        throws ObserverException
    {
        SQLAction action = null;
        int obsId = -1;
        int obsExecutionCount = -1;
        int obsState = ObserverJob.STATE_UNDEFINED;
        int obsRetryAttempts = 0;
        String obsName;
        String obsClassName;
        Date obsNextExecution = null;
        Date obsPrevExecution = null;
        Date obsCreated = null;
        boolean obsRetryOnError = false;
        ObserverJobData ojdata = null;
        TreeSet<ObserverJobData> jobList = new TreeSet<ObserverJobData> (
            new ObserverJobDataComparator ());

        // check initialization
        this.checkInitialization ("loadBaseDataOfActiveJobs");

        // generate SELECT query-string
        String query = this.createQuery () +
                " AND state = " + ObserverJob.STATE_ACTIVE;

        // perform query
        try
        {
            // open connection - begin transaction
            action = DBConnector.getDBConnection ();

            // execute query
            action.execute (query, false);

            // check if job is unique
            while (!action.getEOF ())
            {
                // fetch base data of job
                obsClassName = action.getString ("className");
                obsId = action.getInt ("id");
                obsName = action.getString ("name");
                obsCreated = action.getDate ("created");
                obsNextExecution = action.getDate ("nextExecution");
                obsPrevExecution = action.getDate ("prevExecution");
                obsExecutionCount = action.getInt ("executionCount");
                obsState = action.getInt ("state");
                obsRetryOnError = action.getBoolean ("retryOnError");
                obsRetryAttempts = action.getInt ("retryAttempts");

                // create ObserverJobData object
                ojdata = new ObserverJobData (this.p_context, obsClassName, obsId, obsName, obsCreated,
                        obsNextExecution, obsPrevExecution, obsExecutionCount, obsState, obsRetryOnError,
                        obsRetryAttempts);

                // add job to sortet list
                jobList.add (ojdata);

                // next row
                action.next ();
            } // while

            action.end ();
        } // try
        catch (DBError e)
        {
            throw new ObserverException ("Error while loading ObserverJobs: " +
                e.getMessage () + " Query: " + query);
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
                    "Error while loading ObserverJobs: " + e.getMessage () +
                        " Query: " + query);
            } // catch
        } // finally

        return jobList;
    } // loadBaseDataOfActiveJobs

    /**************************************************************************
     * Loads base data of all ObserverJobs with STATE_CREATED or STATE_WAITING
     * and nextExecution < now, which exist in given context and returns them
     * as a TreeSet of ObserverJobData-objects (which assures that jobs are
     * ordered by their nextExecution-date).
     *
     * @return  The tree set of ObserverJobData objects.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected final TreeSet<ObserverJobData> loadBaseDataOfExecuteableJobs ()
        throws ObserverException
    {
        SQLAction action = null;
        int obsId = -1;
        int obsExecutionCount = -1;
        int obsState = ObserverJob.STATE_UNDEFINED;
        int obsRetryAttempts = 0;
        String obsName;
        String obsClassName;
        Date obsNextExecution = null;
        Date obsPrevExecution = null;
        Date obsCreated = null;
        boolean obsRetryOnError = false;
        ObserverJobData ojdata = null;
        TreeSet<ObserverJobData> jobList =
            new TreeSet<ObserverJobData> (new ObserverJobDataComparator ());

        // check initialization
        this.checkInitialization ("loadBaseDataOfActiveJobs");

        // generate SELECT query-string
        String query = this.createQuery () +
                " AND (state = " + ObserverJob.STATE_CREATED +
                " OR state = " + ObserverJob.STATE_WAITING + ")" +
                " AND nextExecution < " + ObserverJobData.dateTimeToDBString (new Date ());

        // perform query
        try
        {
            // open connection - begin transaction
            action = DBConnector.getDBConnection ();

            // execute query
            action.execute (query, false);

            // check if job is unique
            while (!action.getEOF ())
            {
                // fetch base data of job
                obsClassName = action.getString ("className");
                obsId = action.getInt ("id");
                obsName = action.getString ("name");
                obsCreated = action.getDate ("created");
                obsNextExecution = action.getDate ("nextExecution");
                obsPrevExecution = action.getDate ("prevExecution");
                obsExecutionCount = action.getInt ("executionCount");
                obsState = action.getInt ("state");
                obsRetryOnError = action.getBoolean ("retryOnError");
                obsRetryAttempts = action.getInt ("retryAttempts");

                // create ObserverJobData object
                ojdata = new ObserverJobData (this.p_context, obsClassName, obsId, obsName, obsCreated,
                        obsNextExecution, obsPrevExecution, obsExecutionCount, obsState, obsRetryOnError,
                        obsRetryAttempts);

                // add job to sortet list
                jobList.add (ojdata);

                // next row
                action.next ();
            } // while

            action.end ();
        } // try
        catch (DBError e)
        {
            throw new ObserverException ("Error while loading ObserverJobs: " +
                e.getMessage () + " Query: " + query);
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
                    "Error while loading ObserverJobs: " + e.getMessage () +
                        " Query: " + query);
            } // catch
        } // finally

        return jobList;
    } // loadBaseDataOfExecutableJobs

    /**************************************************************************
     * Loads base data of all ObserverJobs which exist in given context and
     * returns them as a TreeSet of ObserverJobData-objects (which assures
     * that jobs are ordered by their nextExecution-date).
     *
     * @return  The tree set of ObserverJobData objects.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected final TreeSet<ObserverJobData> loadBaseDataOfJobs ()
        throws ObserverException
    {
        SQLAction action = null;
        int obsId = -1;
        int obsExecutionCount = -1;
        int obsState = ObserverJob.STATE_UNDEFINED;
        int obsRetryAttempts = 0;
        String obsName;
        String obsClassName;
        Date obsNextExecution = null;
        Date obsPrevExecution = null;
        Date obsCreated = null;
        boolean obsRetryOnError = false;
        ObserverJobData ojdata = null;
        TreeSet<ObserverJobData> jobList =
            new TreeSet<ObserverJobData> (new ObserverJobDataComparator ());

        // check initialization
        this.checkInitialization ("loadBaseDataOfAllJobs");

        // generate SELECT query-string
        String query = this.createQuery ();

        // perform query
        try
        {
            // open connection - begin transaction
            action = DBConnector.getDBConnection ();

            // execute query
            action.execute (query, false);

            // check if job is unique
            while (!action.getEOF ())
            {
                // fetch base data of job
                obsClassName = action.getString ("className");
                obsId = action.getInt ("id");
                obsName = action.getString ("name");
                obsCreated = action.getDate ("created");
                obsNextExecution = action.getDate ("nextExecution");
                obsPrevExecution = action.getDate ("prevExecution");
                obsExecutionCount = action.getInt ("executionCount");
                obsState = action.getInt ("state");
                obsRetryOnError = action.getBoolean ("retryOnError");
                obsRetryAttempts = action.getInt ("retryAttempts");

                // create ObserverJobData object
                ojdata = new ObserverJobData (this.p_context, obsClassName, obsId, obsName, obsCreated,
                        obsNextExecution, obsPrevExecution, obsExecutionCount, obsState, obsRetryOnError,
                        obsRetryAttempts);

                // add job to sorted list
                jobList.add (ojdata);

                // next row
                action.next ();
            } // while

            action.end ();
        } // try
        catch (DBError e)
        {
            throw new ObserverException ("Error while loading ObserverJobs: " +
                e.getMessage () + " Query: " + query);
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
                    "Error while loading ObserverJobs: " + e.getMessage () +
                        " Query: " + query);
            } // catch
        } // finally

        return jobList;
    } // loadBaseDataOfJobs


    /**************************************************************************
     * Creates query to load base data of ObserverJobs. <BR/>
     * Select-statement MUST hold id, name, created, nextExecution, prevExecution,
     * executionCount, state, className.
     *
     * @return  The constructed query.
     */
    private String createQuery ()
    {
        return
            " SELECT id, name, created, nextExecution, prevExecution," +
            " executionCount, state, retryOnError, retryAttempts, className" +
            " FROM " + this.p_context.getTableName () +
            " WHERE 0 = 0 ";
    } // createQuery


    ///////////////////////////////////////////////////////////////////////////
    //
    // methods for datastructures
    //
    //

    /**************************************************************************
     * Creates the datastructures. <BR> Structures for the base data will be
     * created in this method, for structures of additional data please
     * overwrite the method 'createAdditionalStructure'.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected final void createStructure () throws ObserverException
    {
        // check initialization
        this.checkInitialization ("createStructure");

        SQLAction action = null;
        String[] ddl =
        {
            null, null, null, null,
        };

        // drop counter for ObserverJobDatas
        try
        {
            Counter cnt = new Counter (this.p_context.getTableName ());
            cnt.checkStructure ();
        } // try
        catch (CounterException e)
        {
            // create counter for ObserverJobDatas --> create id
            try
            {
                Counter cnt = new Counter (this.p_context.getTableName ());
                cnt.createStructure (0);
            } // try
            catch (CounterException e1)
            {
                throw new ObserverException (
                    "Error while creating counter structures for ObserverJobData: " +
                        e1.getMessage () +
                        ObserverJobData.ERRM_OBSJOBDATA + this.toString () + "\n");
            } // catch
        } // catch

        if (SQLConstants.DB_TYPE == SQLConstants.DB_ORACLE)
        {
            // generate ddl to create table
            ddl[0] = "CREATE TABLE " + this.p_context.getTableName () + " (" +
                  "id INTEGER NOT NULL, " +
                  "name NVARCHAR2 (252) NOT NULL, " +
                  "created DATE NOT NULL," +
                  "nextExecution DATE, " +
                  "prevExecution DATE, " +
                  "executionCount INTEGER NOT NULL, " +
                  "state NVARCHAR2 (252) NOT NULL, " +
                  "retryOnError NUMBER (1) NOT NULL, " +
                  "retryAttempts INTEGER NOT NULL, " +
                  "className VARCHAR2 (1020) NOT NULL" +
                  ")";

            // generate indexes
            ddl[1] = "CREATE UNIQUE INDEX " + this.p_context.getIndexPrefix () +
                "_1 ON " + this.p_context.getTableName () + " (id)";
            ddl[2] = "CREATE INDEX " + this.p_context.getIndexPrefix () +
                "_2 ON " + this.p_context.getTableName ()  + " (name)";
            ddl[3] = "CREATE INDEX " + this.p_context.getIndexPrefix () +
                "_3 ON " + this.p_context.getTableName ()  + " (state,nextExecution)";

        } // if oracle
        else if (SQLConstants.DB_TYPE == SQLConstants.DB_DB2)
        {
            // generate ddl to create table
            ddl[0] = "CREATE TABLE " + this.p_context.getTableName () + " (" +
                  "id INTEGER NOT NULL, " +
                  "name VARCHAR (252) NOT NULL, " +
                  "created DATETIME NOT NULL," +
                  "nextExecution DATETIME, " +
                  "prevExecution DATETIME, " +
                  "executionCount INTEGER NOT NULL, " +
                  //BT 20091201: Should perhaps be changed to a numeric field
                  // since state is an int within Java
                  "state VARCHAR (252) NOT NULL, " +
                  "retryOnError BOOL NOT NULL, " +
                  "retryAttempts INTEGER NOT NULL, " +
                  "className VARCHAR (1020) NOT NULL" +
                  ")";

            // generate ddl to create indexes
            ddl[1] = "CREATE UNIQUE INDEX " + this.p_context.getIndexPrefix () +
                "1 ON " + this.p_context.getTableName ()  + " (id)";
            ddl[2] = "CREATE INDEX " + this.p_context.getIndexPrefix () +
                "Name ON " + this.p_context.getTableName ()  + " (name)";
            ddl[3] = "CREATE INDEX " + this.p_context.getIndexPrefix () +
                "3 ON " + this.p_context.getTableName ()  + " (state,nextExecution)";
        } // else if (this.isDBMSOracle)
        else // sql-server
        {
            // generate ddl to create table
            ddl[0] = "CREATE TABLE " + this.p_context.getTableName () + " (" +
                  "id INTEGER NOT NULL, " +
                  "name NVARCHAR (63) NOT NULL, " +
                  "created DATETIME NOT NULL," +
                  "nextExecution DATETIME, " +
                  "prevExecution DATETIME, " +
                  "executionCount INTEGER NOT NULL, " +
                  //BT 20091201: Should perhaps be changed to a numeric field
                  // since state is an int within Java
                  "state NVARCHAR (63) NOT NULL, " +
                  "retryOnError BOOL NOT NULL, " +
                  "retryAttempts INTEGER NOT NULL, " +
                  "className VARCHAR (255) NOT NULL" +
                  ")";

            // generate ddl to create indexes
            ddl[1] = "CREATE UNIQUE INDEX " + this.p_context.getIndexPrefix () +
                "1 ON " + this.p_context.getTableName ()  + " (id)";
            ddl[2] = "CREATE INDEX " + this.p_context.getIndexPrefix () +
                "Name ON " + this.p_context.getTableName ()  + " (name)";
            ddl[3] = "CREATE INDEX " + this.p_context.getIndexPrefix () +
                "3 ON " + this.p_context.getTableName ()  + " (state,nextExecution)";
        } // else if (this.isDBMSOracle)

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
                    "Error while creating structures for ObserverJobData: " +
                        e.getMessage () + "; Queries" + StringHelpers.stringArrayToStringSC (ddl) +
                        ObserverJobData.ERRM_OBSJOBDATA + this.toString ());
            } // try
            catch (DBError err)
            {
                throw new ObserverException (
                    "Error during rollback from creation of structures for ObserverJobData: " +
                        err.getMessage () +
                        "; Queries" + StringHelpers.stringArrayToStringSC (ddl) +
                        ObserverJobData.ERRM_OBSJOBDATA + this.toString ());
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
                    "Error while creating structures for ObserverJobData: " +
                        e.getMessage () + "; Queries" + StringHelpers.stringArrayToStringSC (ddl) +
                        ObserverJobData.ERRM_OBSJOBDATA + this.toString ());
            } // catch
        } // finally
    } // createStructure

    /**************************************************************************
     * Drops the datastructures. <BR> Structure of base data will be dropped
     * in this method, for additional structure please overwrite the
     * method 'dropAdditionalStructure'.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected final void dropStructure () throws ObserverException
    {
        // check initialization
        this.checkInitialization ("dropStructure");

        SQLAction action = null;
        String[] ddl = {null};

        // drop counter for ObserverJobDatas
        try
        {
            Counter cnt = new Counter (this.p_context.getTableName ());
            cnt.dropStructure ();
        } // try
        catch (CounterException e)
        {
            throw new ObserverException ("Error while dropping counter structures for ObserverJobData: " +
                    e.getMessage () + ObserverJobData.ERRM_OBSJOBDATA + this.toString ());
        } // catch

        // generate ddl to drop table
        ddl[0] = "DROP TABLE " + this.p_context.getTableName ();

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
                    "Error while dropping structures for ObserverJobData: " +
                        e.getMessage () + "; Queries" + StringHelpers.stringArrayToStringSC (ddl) +
                        ObserverJobData.ERRM_OBSJOBDATA + this.toString ());
            } // try
            catch (DBError err)
            {
                throw new ObserverException (
                    "Error during rollback from dropping structures for ObserverJobData: " +
                        err.getMessage () +
                        "; Queries" + StringHelpers.stringArrayToStringSC (ddl) +
                        ObserverJobData.ERRM_OBSJOBDATA + this.toString ());
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
                    "Error while dropping structures for ObserverJobData: " +
                        e.getMessage () + "; Queries" + StringHelpers.stringArrayToStringSC (ddl) +
                        ObserverJobData.ERRM_OBSJOBDATA + this.toString ());
            } // catch
        } // finally
    } // dropStructure

    /**************************************************************************
     * Checks the datastructures. <BR> Structure of base data will be checked
     * in this method, for additional structure please overwrite the
     * method 'checkAdditionalStructure'.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected final void checkStructure () throws ObserverException
    {
        // check initialization
        this.checkInitialization ("checkStructure");

        SQLAction action = null;

        // drop counter for ObserverJobDatas
        try
        {
            Counter cnt = new Counter (this.p_context.getTableName ());
            cnt.checkStructure ();
        } // try
        catch (CounterException e)
        {
            throw new ObserverException ("Error while checking counter structures for ObserverJobData: " +
                    e.getMessage () + ObserverJobData.ERRM_OBSJOBDATA + this.toString ());
        } // catch

        // generate SELECT query-string
        String query = this.createQuery () + " AND id = 0";

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
                    e.getMessage () + ObserverJobData.ERRM_OBSJOBDATA + this.toString ());
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
                        e.getMessage () + ObserverJobData.ERRM_OBSJOBDATA +
                        this.toString ());
            } // catch
        } // finally
    } // checkStructure


    /**************************************************************************
     * Checks for given operation if needed objects are initialized . <BR/>
     *
     * @param   op      The operation to be checked.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected final void checkInitialization (String op)
        throws ObserverException
    {
        if (this.p_context == null)
        {
            throw new ObserverException ("Error during " + this.getClass ().getName () + "." + op +
                "Context undefined.");
        } // if
    } // checkInitialization


    /**************************************************************************
     * Converts a given Date-object to a string which is understandable by
     * any dbserver given through AppConstancts.DB_TYPE. The returned string
     * can be used in any SQL-Statement.
     *
     * @param   date    Date which will be converted.
     *
     * @return  String, which contains a database-dependent formatted
     *          string-representation of the given date. <BR/>
     *          ORACLE ... ' TO_DATE ([datestring], 'DD.MM.YY HH24:MI:SS') '. <BR/>
     *          MS_SQL ... '[datestring]'. <BR/>
     *          The [datestring] is always in format "dd.MM.yyyy HH:mm:ss". <BR/>
     *          If given date is null the string "'null'" will be returned.
     */
    public static String dateTimeToDBString (Date date)
    {
        if (date == null)
        {
            return "null";
        } // if

        // create conversion format
        String d = DateTimeHelpers.dateTimeToString (date);
        return SQLHelpers.getDateString (d).toString ();
    } // dateTimeToDBString


    /**************************************************************************
     * Returns a string representation of the object. <BR/>
     *
     * @return  a string representation of the object.
     */
    public String toString ()
    {
        return "[p_id=" + this.p_id + "; p_name=" + this.p_name +
            "; p_created=" + this.p_created +
            "; p_nextExecution=" + this.p_nextExecution +
            "; p_prevExecution=" + this.p_prevExecution +
            "; p_executionCount=" + this.p_executionCount +
            "; p_state=" + this.p_state +
            "; p_retryOnError=" + (this.p_retryOnError ? "true" : "false") +
            "; p_retryAttempts=" + this.p_retryAttempts +
            "; p_className=" + this.p_className + "; context=" +
                this.p_context + "]";
    } // toString


    /**************************************************************************
     * Returns <CODE>true</CODE> if given object equals this object. <BR/>
     * Tests: p_className, p_id, p_name, p_created, p_nextExecution,
     *        p_prevExecution, p_executionCount, p_state, p_context. <BR/>
     * Date-types will be compared without milliseconds as supported by
     * {@link DateTimeHelpers#compareDateTimes(Date, Date)}.
     *
     * @param   obj     The object for comparison.
     *
     * @return  <CODE>true</CODE> if the object are equal,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean equals (ObserverJobData obj)
    {
        // check null value:
        if (obj == null)
        {
            return false;
        } // if

        // set temporary variables - to avoid NullPointerException
        String name = this.p_name == null ? "null" : this.p_name;
        String className = this.p_className == null ? "null" : this.p_className;
        String objName = obj.getName () == null ? "null" : obj.getName ();
        String objClassName = obj.getClassName () == null ? "null" : obj.getClassName ();
        boolean bContext = true;

        // test equality of sub objects (including null-pointer tests)
        if (this.p_context == null || obj.getContext () == null)
        {
            bContext = this.p_context == obj.getContext ();
        } // if
        else
        {
            bContext = this.p_context.equals (obj.getContext ());
        } // else

        // check property-equality
        if (className.equals (objClassName) &&
            name.equals (objName) &&
            this.p_executionCount == obj.getExecutionCount () &&
            this.p_state == obj.getState () &&
            this.p_id == obj.getId () &&
            DateTimeHelpers.compareDateTimes (this.p_created, obj.getCreated ()) == 0 &&
            DateTimeHelpers.compareDateTimes (this.p_nextExecution, obj.getNextExecution ()) == 0 &&
            DateTimeHelpers.compareDateTimes (this.p_prevExecution, obj.getPrevExecution ()) == 0 &&
            bContext)
        {
            return true;
        } // if

        return false;
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
            // set hash value equal to id:
            this.p_hashCode = this.p_id;
        } // if

        // return the result:
        return this.p_hashCode;
    } // hashCode

} // class ObserverJobData

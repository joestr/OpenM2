/*
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 31, 2002
 * Time: 12:51:59 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */

// package:
package ibs.service.observer;

// imports:


/******************************************************************************
 * ObserverContext holds data that is created out of one observers context.
 * <BR>. This includes information like tablenames for basedata, directories
 * and filenames for config/log-files, etc.
 *
 * @version     $Id: ObserverContext.java,v 1.4 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      HORST PICHLER, 31.07.2002
 ******************************************************************************
 */
public class ObserverContext
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ObserverContext.java,v 1.4 2007/07/24 21:27:33 kreimueller Exp $";


    /**
     * Prefix of observers tablename.
     */
    private final String TABLE_PREFIX = "obs_";
    /**
     * Prefix of observers indexname.
     */
    private final String INDEX_PREFIX = "idx_";

    /**
     * Unique name of observer.
     */
    private String p_name = null;
    /**
     * path to observers-relevant files.
     */
    private String p_path = null;
    /**
     * path to logfile of observer.
     */
    private String p_logPath = null;
    /**
     * path to logfiles of observerjobs.
     */
    private String p_jobsLogPath = null;
    /**
     * Name of the table where observerjob-data is stored.
     */
    private String p_tableName = null;
    /**
     * Prefix of the indexes for tables.
     */
    private String p_indexPrefix = null;

    /**
     * The hash code. <BR/>
     */
    private int p_hashCode = Integer.MIN_VALUE;

/**
     * Null value for String. <BR/>
     */
    private static final String NULL_VALUE = "null";


    //
    // constructors
    //
    /**************************************************************************
     * Creates a ObserverContext object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   name        ???
     * @param   path        ???
     * @param   logPath     ???
     * @param   jobsLogPath ???
     */
    public ObserverContext (String name,
                            String path,
                            String logPath,
                            String jobsLogPath)
    {
        this.p_name = name;
        this.p_path = path;
        this.p_logPath = logPath;
        this.p_jobsLogPath = jobsLogPath;
        if (name != null)
        {
            this.p_tableName = this.TABLE_PREFIX + name;
            this.p_indexPrefix = this.INDEX_PREFIX + name;
        } // if
    } // ObserverContext


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
     * @return  The path.
     */
    public String getPath ()
    {
        return this.p_path;
    } // getPath


    /**************************************************************************
     * This method ... <BR/>
     *
     * @return  The log path.
     */
    public String getLogPath ()
    {
        return this.p_logPath;
    } // getLogPath


    /**************************************************************************
     * This method ... <BR/>
     *
     * @return  The log path.
     */
    public String getJobsLogPath ()
    {
        return this.p_jobsLogPath;
    } // getJobsLogPath


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
     * @return  The index prefix.
     */
    public String getIndexPrefix ()
    {
        return this.p_indexPrefix;
    } // getIndexPrefix


    /**************************************************************************
     * Returns a string representation of the object. <BR/>
     *
     * @return  a string representation of the object.
     */
    public String toString ()
    {
        return "[p_name=" + this.p_name + "; p_path=" + this.p_path +
            "; p_logPath=" + this.p_logPath +
            "; p_jobsLogPath=" + this.p_jobsLogPath +
            "; p_tableName=" + this.p_tableName +
            "; p_indexPrefix=" + this.p_indexPrefix + "]";
    } // toString


    /**************************************************************************
     * Returns true if given object equals this object. <BR/>
     * Tests: p_name, p_domain, p_domainId, p_path, p_logPath, p_jobsLogPath,
     * p_tableName
     *
     * @param   obj     The observer context.
     *
     * @return  <CODE>true</CODE> if the objects are equal,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean equals (ObserverContext obj)
    {
        String name;
        String path;
        String logPath;
        String jobsLogPath;
        String tableName;
        String indexPrefix;
        String objName;
        String objPath;
        String objLogPath;
        String objJobsLogPath;
        String objTableName;
        String objIndexPrefix;

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

        // set temporary variables - to avoid null-pointer-exception
        name = this.p_name == null ? ObserverContext.NULL_VALUE : this.p_name;
        path = this.p_path == null ? ObserverContext.NULL_VALUE : this.p_path;
        logPath = this.p_logPath == null ? ObserverContext.NULL_VALUE : this.p_logPath;
        jobsLogPath = this.p_jobsLogPath == null ? ObserverContext.NULL_VALUE : this.p_jobsLogPath;
        tableName = this.p_tableName == null ? ObserverContext.NULL_VALUE : this.p_tableName;
        indexPrefix = this.p_indexPrefix == null ? ObserverContext.NULL_VALUE : this.p_indexPrefix;

        objName = obj.getName () == null ? ObserverContext.NULL_VALUE : obj.getName ();
        objPath = obj.getPath () == null ? ObserverContext.NULL_VALUE : obj.getPath ();
        objLogPath = obj.getLogPath () == null ? ObserverContext.NULL_VALUE : obj.getLogPath ();
        objJobsLogPath = obj.getJobsLogPath () == null ? ObserverContext.NULL_VALUE : obj.getJobsLogPath ();
        objTableName = obj.getTableName () == null ? ObserverContext.NULL_VALUE : obj.getTableName ();
        objIndexPrefix = obj.getIndexPrefix () == null ? ObserverContext.NULL_VALUE : obj.getIndexPrefix ();

        // check property-equality
        if (name.equals (objName) && path.equals (objPath) &&
            logPath.equals (objLogPath) && jobsLogPath.equals (objJobsLogPath) &&
            tableName.equals (objTableName) && indexPrefix.equals (objIndexPrefix))
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
            // concatenate the relevant fields and compute the hash code from
            // the resulting value:
            this.p_hashCode = ("" + this.p_name + "." + this.p_path + "." +
                this.p_logPath + "." + this.p_jobsLogPath + "." +
                this.p_tableName + "." + this.p_indexPrefix)
                .hashCode ();
        } // if

        // return the result:
        return this.p_hashCode;
    } // hashCode

} // ObserverContext

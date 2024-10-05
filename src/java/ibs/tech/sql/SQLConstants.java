/*
 * Class: SQLConstants.java
 */

// package:
package ibs.tech.sql;

// imports:


/******************************************************************************
 * This class defines the constants used in the ibs package. <BR/>
 *
 * @version     $Id: SQLConstants.java,v 1.15 2012/10/09 14:54:13 gweiss Exp $
 *
 * @author      Mark Wassermann (MW)
 ******************************************************************************
 */
public class SQLConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SQLConstants.java,v 1.15 2012/10/09 14:54:13 gweiss Exp $";


    /**
     * NULL value of database. <BR/>
     */
    public static final String DB_NULL = "NULL";

    /**
     * Place holder if no statement is to be executed. <BR/>
     */
    public static final String DB_NOSTMT = SQLConstants.DB_NULL;

    /**
     * Constant indicating a MS SQL database. <BR/>
     */
    //public static final int DB_MSSQL = 1;

    /**
     * Constant indicating an ORACLE database. <BR/>
     */
    //public static final int DB_ORACLE = 2;

    // Constants indicating database types. <BR/>
    // - UNDEFINED              0
    // - MS SQL 6.5     sqls65  1
    // - ORACLE         ora805  2
    // - DB2            db2     4

    /**
     * Constant indicating database type UNDEFINED. <BR/>
     */
    public static final String DB_UNDEF_STR   = null;
    /**
     * Constant indicating database type: MS SQL Server 6.5. <BR/>
     */
    public static final String DB_MSSQL_STR   = "sqls65";
    /**
     * Constant indicating database type: ORACLE 8.0. <BR/>
     */
    public static final String DB_ORACLE_STR  = "ora805";
    /**
     * Constant indicating database type: DB2. <BR/>
     */
    public static final String DB_DB2_STR     = "db2";

    /**
     * Constant indicating database type: UNDEFINED. <BR/>
     */
    public static final int DB_UNDEF    = 0;
    /**
     * Constant indicating database type: MS SQL Server 6.5. <BR/>
     */
    public static final int DB_MSSQL    = 1;
    /**
     * Constant indicating database type: ORACLE. <BR/>
     */
    public static final int DB_ORACLE   = 2;
/* KR MS SQL Server 7.0 is not supported.
    public static final int DB_MSSQL70  = 3;
*/
    /**
     * Constant indicating database type: DB2. <BR/>
     */
    public static final int DB_DB2      = 4;

    /**
     * Database directory: UNDEFINED. <BR/>
     */
    public static final String DBDIR_UNDEF   = null;
    /**
     * Database directory: MS SQL Server. <BR/>
     */
    public static final String DBDIR_MSSQL   = "MS-SQL";
    /**
     * Database directory: ORACLE. <BR/>
     */
    public static final String DBDIR_ORACLE  = "ORACLE";
    /**
     * Database directory: DB2. <BR/>
     */
    public static final String DBDIR_DB2     = "DB2";

    /**
     * Class variable to define the type of DB used. Will be set when the
     * configuration files are read.
     */
    public static int DB_TYPE = SQLConstants.DB_UNDEF;


    /**
     * Boolean value for FALSE. <BR/>
     */
    public static final int BOOL_FALSE = 0;

    /**
     * Boolean value for TRUE. <BR/>
     */
    public static final int BOOL_TRUE = 1;

    /**
     * Boolean string for FALSE. <BR/>
     */
    public static final String BOOLSTR_FALSE = "" + false;

    /**
     * Boolean string for TRUE. <BR/>
     */
    public static final String BOOLSTR_TRUE = "" + true;


    /**
     * Constants for undefined timeout values
     */
    public static final int UNDEFINED_QUERYTIMEOUT  = -1;

    /**
     * Constants for undefined timeout values
     */
    public static final int UNDEFINED_LOGINTIMEOUT  = -1;


    /**
     * code for no restrictions. <BR/>
     */
    public static final String MATCH_NONE = "0";

    /**
     * name for substring string match. <BR/>
     */
    public static final String MATCHNAME_SUBSTRING = "SUBSTRING";

    /**
     * code for substring string match. <BR/>
     */
    public static final String MATCH_SUBSTRING = "1";

    /**
     * code for exact string match . <BR/>
     */
    public static final String MATCHNAME_EXACT = "EQUAL";

    /**
     * code for exact string match . <BR/>
     */
    public static final String MATCH_EXACT = "2";

    /**
     * code for soundex match . <BR/>
     */
    public static final String MATCHNAME_SOUNDEX = "SOUNDEX";

    /**
     * code for soundex match . <BR/>
     */
    public static final String MATCH_SOUNDEX = "3";

    /**
     * code for greater number match. <BR/>
     */
    public static final String MATCHNAME_GREATER = "GREATER";

    /**
     * code for greater number match. <BR/>
     */
    public static final String MATCH_GREATER = "4";

    /**
     * code for less number match. <BR/>
     */
    public static final String MATCHNAME_LESS = "LESS";

    /**
     * code for less number match. <BR/>
     */
    public static final String MATCH_LESS = "5";

    /**
     * code for greater-equal number match. <BR/>
     */
    public static final String MATCHNAME_GREATEREQUAL = "GREATEREQUAL";

    /**
     * code for greater-equal number match. <BR/>
     */
    public static final String MATCH_GREATEREQUAL = "6";

    /**
     * code for greater-equal number match. <BR/>
     */
    public static final String MATCHNAME_LESSEQUAL = "LESSEQUAL";

    /**
     * code for less-equal number match. <BR/>
     */
    public static final String MATCH_LESSEQUAL = "7";

    /**
     * Name for starts with string match. <BR/>
     */
    public static final String MATCHNAME_STARTSWITH = "STARTSWITH";

    /**
     * Code for starts with string match. <BR/>
     */
    public static final String MATCH_STARTSWITH = "8";

    /**
     * Name for ends with string match. <BR/>
     */
    public static final String MATCHNAME_ENDSWITH = "ENDSWITH";

    /**
     * Code for ends with string match. <BR/>
     */
    public static final String MATCH_ENDSWITH = "9";

    /**
     * Name for IN match. <BR/>
     */
    public static final String MATCHNAME_IN = "IN";

    /**
     * Code for IN match. <BR/>
     */
    public static final String MATCH_IN = "10";

    /**
     * Unit for date time comparisons and computations based on hour. <BR/>
     */
    public static final String UNIT_HOUR = "hour";

    /**
     * Unit for date time comparisons and computations based on day. <BR/>
     */
    public static final String UNIT_DAY = "day";

    /**
     * Unit for date time comparisons and computations based on month. <BR/>
     */
    public static final String UNIT_MONTH = "month";


    /**
     * Number of retries if there is a deadlock. <BR/>
     */
    public static int RETRIES_ON_DEADLOCK = 20;

    /**
     * Time in milliseconds before retry after a deadlock. <BR/>
     */
    public static int RETRYTIME_ON_DEADLOCK = 1000;

    /**
     * Time in milliseconds before retry after a deadlock. <BR/>
     */
    public static int RETRYTIMEOFFSET_ON_DEADLOCK = 512;

    
    /**
     * Timespan in milliseconds before reconnect on loss of DB connection. <BR/>
     */
    public static int RETRYTIMEOFFSET_ON_DBCONNECTION_LOSS = 5000;

    /**
     * Number of retries to create a new DB connection. <BR/>
     */
    public static int RETRIES_FOR_DBCONNECTION = 10;

    /**
     * Number of losses of DB connection before informing the SysAdmin. <BR/>
     */
    public static int THRESHOLD_OF_CONNECTION_LOSSES = 10;

    /**
     * Timespan in milliseconds within losses of DBConnections are counted. <BR/>
     */
    public static int TIMESPAN_OF_CONNECTION_LOSSES = 1000;


    /**
     * SQL conjunction: AND. <BR/>
     */
    public static final StringBuffer SQL_AND = new StringBuffer (" AND ");

    /**
     * SQL conjunction: OR. <BR/>
     */
    public static final StringBuffer SQL_OR = new StringBuffer (" OR ");


    // SQL type names:
    /**
     * Type names for SQL server. <BR/>
     */
    public static final String [] TYPENAMES_SQLS = // type names - sqls version
    {
        "BOOL",                     // boolean
        "BINARY (1)",               // byte
        "SHORT",                    // short
        "INT",                      // integer
        "REAL",                     // float
        "FLOAT",                    // double
        "DATETIME",                 // date
        "MONEY",                    // currency
        "VARCHAR (255)",            // string
        "VARCHAR (255)",            // varchar
        "?OBJECT?",                 // object
        "VARBINARY (255)",          // varbyte
        "TEXT",                     // text
    }; // TYPENAMES_SQLS

    /**
     * Type names for Oracle. <BR/>
     */
    public static final String [] TYPENAMES_ORACLE = // type names - oracle version
    {
        "NUMBER (1)",               // boolean
        "NUMBER (1)",               // byte
        "SHORT",                    // short
        "INTEGER",                  // integer
        "FLOAT",                    // float
        "DOUBLE",                   // double
        "DATE",                     // date
        "NUMBER (19, 4)",           // currency
        "VARCHAR2 (255)",           // string
        "VARCHAR2 (255)",           // varchar
        "?OBJECT",                  // object
        "RAW (255)",                // varbyte
        "CLOB",                     // text
    }; // TYPENAMES_ORACLE

    /**
     * Type names for DB2. <BR/>
     */
    public static final String [] TYPENAMES_DB2 =  // type names - DB2 version
    {
        "SMALLINT",                 // boolean
        "SMALLINT",                 // byte
        "SMALLINT",                 // short
        "INTEGER",                  // integer
        "REAL",                     // float
        "DOUBLE",                   // double
        "TIMESTAMP",                // date
        "NUMERIC (19, 4)",          // currency
        "VARCHAR (255)",            // string
        "VARCHAR (255)",            // varchar
        "?OBJECT?",                 // object
        "VARCHAR (255) FOR BIT DATA", // varbyte
        "CLOB",                     // text
    }; // TYPENAMES_DB2

    /**
     * MSSQL German date format. <BR/>
     */
    public static final String MSSQL_DATEFORMAT_GERMAN = "104";
} // class SQLConstants
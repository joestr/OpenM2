/*
 * Class: DBMapper.java
 */

// package:
package ibs.di.service;

// imports:
//KR TODO: unsauber
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BusinessObject;
import ibs.bo.BusinessObjectInfo;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.bo.States;
import ibs.bo.type.TypeConstants;
import ibs.di.DIConstants;
import ibs.di.DIHelpers;
import ibs.di.DataElement;
import ibs.di.DocumentTemplate_01;
import ibs.di.Log_01;
import ibs.di.ValueDataElement;
import ibs.di.XMLViewer_01;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.IE302;
import ibs.tech.sql.DBError;
import ibs.tech.sql.InsertStatement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.SelectQuery;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.tech.sql.UpdateStatement;
import ibs.util.AlreadyDeletedException;
import ibs.util.DateTimeHelpers;
import ibs.util.Helpers;
import ibs.util.NoAccessException;
import ibs.util.UtilConstants;
import ibs.util.crypto.EncryptionManager;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;


/******************************************************************************
 * The class DBMapper implements the service to map type specific attributes
 * of xml forms in a table in the database. It provides a interface to
 * create, maintain and delete the mapping table.
 *
 * @version     $Id: DBMapper.java,v 1.73 2012/08/24 10:30:04 rburgermann Exp $
 *
 * @author      Michael Steiner (MS), 001003
 ******************************************************************************
 */
public class DBMapper extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DBMapper.java,v 1.73 2012/08/24 10:30:04 rburgermann Exp $";


    /**************************************************************************
     * This class represents the information of a value which is used as
     * parameter for a stored procedure call within db-mapping.
     *
     * @version     $Id: DBMapper.java,v 1.73 2012/08/24 10:30:04 rburgermann Exp $
     *
     * @author      Klaus Reimüller (KR) 20090828
     **************************************************************************
     */
    private class ValueParam
    {
        /**
         * The value to be db-mapped. <BR/>
         */
        public ValueDataElement p_value = null;

        /**
         * The parameter for the stored procedure call. <BR/>
         */
        public Parameter p_param = null;


        /**********************************************************************
         * Create instance of class ValueParam. <BR/>
         *
         * @param   value   The value to be db-mapped.
         * @param   param   The parameter for the stored procedure call.
         */
        public ValueParam (ValueDataElement value, Parameter param)
        {
            this.p_value = value;
            this.p_param = param;
        } // ValueParam
    } // ValueParam


    // Possible mapping actions.
    /**
     * mapping action: create object(s)
     */
    private static final int MAPPING_ACTION_CREATE   = 1;
    /**
     * mapping action: update object(s)
     */
    private static final int MAPPING_ACTION_UPDATE   = 2;
    /**
     * mapping action: retrieve object(s)
     */
    private static final int MAPPING_ACTION_RETRIEVE = 3;

    /**
     * The prefix for multiselection fields for the VALUETYPETABLE array. <BR/>
     */
    private static final String MULTISELECTION_PREFIX = "MUTLISELECTION_";

    /**
     * Table with all valid value typs in a form. <BR/>
     */
    private static final String[] VALUETYPETABLE =
    {
        DIConstants.VTYPE_CHAR,
        DIConstants.VTYPE_TEXT,
        DIConstants.VTYPE_LONGTEXT,
        DIConstants.VTYPE_HTMLTEXT,
        DIConstants.VTYPE_DATE,
        DIConstants.VTYPE_TIME,
        DIConstants.VTYPE_DATETIME,
        DIConstants.VTYPE_BOOLEAN,
        DIConstants.VTYPE_INT,
        DIConstants.VTYPE_FLOAT,
        DIConstants.VTYPE_DOUBLE,
        DIConstants.VTYPE_NUMBER,
        DIConstants.VTYPE_MONEY,
        DIConstants.VTYPE_FILE,
        DIConstants.VTYPE_URL,
        DIConstants.VTYPE_EMAIL,
        DIConstants.VTYPE_OPTION,
        DIConstants.VTYPE_IMAGE,
/* not mappable
        DIConstants.VTYPE_SEPARATOR,
        DIConstants.VTYPE_REMARK,
        DIConstants.VTYPE_BUTTON,
*/
        DIConstants.VTYPE_OBJECTREF,
        DIConstants.VTYPE_QUERYSELECTIONBOXNUM,
        DIConstants.VTYPE_QUERYSELECTIONBOXINT,
        DIConstants.VTYPE_QUERYSELECTIONBOX,
        DBMapper.MULTISELECTION_PREFIX + DIConstants.VTYPE_QUERYSELECTIONBOX,
        DIConstants.VTYPE_SELECTIONBOXNUM,
        DIConstants.VTYPE_SELECTIONBOXINT,
        DIConstants.VTYPE_SELECTIONBOX,
        DBMapper.MULTISELECTION_PREFIX + DIConstants.VTYPE_SELECTIONBOX,
        DIConstants.VTYPE_FIELDREF,
        DIConstants.VTYPE_VALUEDOMAIN,
        DBMapper.MULTISELECTION_PREFIX + DIConstants.VTYPE_VALUEDOMAIN,
        DIConstants.VTYPE_PASSWORD,
        DIConstants.VTYPE_REMINDER,
    };

    /**
     * Table with the database field types for MS-SQL. <BR/>
     */
    private static final String[] TYPETABLE_SQL =
    {
        "NVARCHAR(255)", // DIConstants.VTYPE_CHAR
        "NVARCHAR(255)", // DIConstants.VTYPE_NTEXT
        "NTEXT",         // DIConstants.VTYPE_LONGNTEXT
        "NTEXT",         // DIConstants.VTYPE_HTMLNTEXT
        "DATETIME",     // DIConstants.VTYPE_DATE
        "DATETIME",     // DIConstants.VTYPE_TIME
        "DATETIME",     // DIConstants.VTYPE_DATETIME
        "BOOL",         // DIConstants.VTYPE_BOOLEAN
        "INTEGER",      // DIConstants.VTYPE_INT
        "FLOAT",        // DIConstants.VTYPE_FLOAT
        "FLOAT",        // DIConstants.VTYPE_DOUBLE
        "FLOAT",        // DIConstants.VTYPE_NUMBER
        "MONEY",        // DIConstants.VTYPE_MONEY
        "FILENAME",     // DIConstants.VTYPE_FILE
        "FILENAME",     // DIConstants.VTYPE_URL
        "EMAIL",        // DIConstants.VTYPE_EMAIL
        "NTEXT",         // DIConstants.VTYPE_OPTION
        "FILENAME",     // DIConstants.VTYPE_IMAGE
/*
        TYPE_NOTMAPPABLE, // DIConstants.VTYPE_SEPARATOR
        TYPE_NOTMAPPABLE, // DIConstants.VTYPE_REMARK
        TYPE_NOTMAPPABLE, // DIConstants.VTYPE_BUTTON
*/
        "NVARCHAR(255)", // DIConstants.VTYPE_OBJECTREF
        "FLOAT",        // DIConstants.VTYPE_QUERYSELECTIONBOXNUM
        "INTEGER",      // DIConstants.VTYPE_QUERYSELECTIONBOXINT
        "NVARCHAR(255)", // DIConstants.VTYPE_QUERYSELECTIONBOX
        "NVARCHAR(4000)", // DIConstants.VTYPE_QUERYSELECTIONBOX and MULTISELCTION=true
        "FLOAT",        // DIConstants.VTYPE_SELECTIONBOXNUM
        "INTEGER",      // DIConstants.VTYPE_SELECTIONBOXINT
        "NVARCHAR(255)", // DIConstants.VTYPE_SELECTIONBOX
        "NVARCHAR(4000)", // DIConstants.VTYPE_SELECTIONBOX and MULTISELCTION=true
        "OBJECTID",     // DIConstants.VTYPE_FIELDREF
        "OBJECTID",     // DIConstants.VTYPE_VALUEDOMAIN
        "NVARCHAR(4000)", // DIConstants.VTYPE_VALUEDOMAIN and MULTISELCTION=true
        "NVARCHAR(255)", // DIConstants.VTYPE_PASSWORD
        "DATETIME",     // DIConstants.VTYPE_REMINDER
    };

    /**
     * Table with the database field types for ORACLE. <BR/>
     * 
     * Note: The length of a NVARCHAR2 is specified in byte. When using UTF-8
     *       a character is encoded as a variable number of bytes with a 
     *       maximum length of 4 bytes. Because of that the length of a String
     *       that should contain not more than 255 characters is 4*255 = 1020.
     */
    private static final String[] TYPETABLE_ORACLE =
    {
        "NVARCHAR2(1020)", // DIConstants.VTYPE_CHAR
        "NVARCHAR2(1020)", // DIConstants.VTYPE_TEXT
        "NCLOB",          // DIConstants.VTYPE_LONGTEXT
        "NCLOB",          // DIConstants.VTYPE_HTMLTEXT
        "DATE",          // DIConstants.VTYPE_DATE
        "DATE",          // DIConstants.VTYPE_TIME
        "DATE",          // DIConstants.VTYPE_DATETIME
        "NUMBER(1)",     // DIConstants.VTYPE_BOOLEAN
        "INTEGER",       // DIConstants.VTYPE_INT
        "FLOAT",         // DIConstants.VTYPE_FLOAT
        "FLOAT",         // DIConstants.VTYPE_DOUBLE
        "NUMBER(19,4)",  // DIConstants.VTYPE_NUMBER
        "NUMBER(19,4)",  // DIConstants.VTYPE_MONEY
        "NVARCHAR2(1020)", // DIConstants.VTYPE_FILE
        "NVARCHAR2(1020)", // DIConstants.VTYPE_URL
        "NVARCHAR2(508)", // DIConstants.VTYPE_EMAIL
        "NCLOB",          // DIConstants.VTYPE_OPTION
        "NVARCHAR2(1020)", // DIConstants.VTYPE_IMAGE
/*
        TYPE_NOTMAPPABLE, // DIConstants.VTYPE_SEPARATOR
        TYPE_NOTMAPPABLE, // DIConstants.VTYPE_REMARK
        TYPE_NOTMAPPABLE, // DIConstants.VTYPE_BUTTON
*/
        "NVARCHAR2(1020)", // DIConstants.VTYPE_OBJECTREF
        "FLOAT",         // DIConstants.VTYPE_QUERYSELECTIONBOXNUM
        "INTEGER",       // DIConstants.VTYPE_QUERYSELECTIONBOXINT
        "NVARCHAR2(1020)", // DIConstants.VTYPE_QUERYSELECTIONBOX
        "NVARCHAR2(8000)", // DIConstants.VTYPE_QUERYSELECTIONBOX and MULTISELCTION=true
        "FLOAT",         // DIConstants.VTYPE_SELECTIONBOXNUM
        "INTEGER",       // DIConstants.VTYPE_SELECTIONBOXINT
        "NVARCHAR2(1020)", // DIConstants.VTYPE_SELECTIONBOX
        "NVARCHAR2(32000)", // DIConstants.VTYPE_SELECTIONBOX and MULTISELCTION=true
        "RAW(8)",        // DIConstants.VTYPE_FIELDREF
        "RAW(8)",        // DIConstants.VTYPE_VALUEDOMAIN
        "NVARCHAR2(32000)", // DIConstants.VTYPE_VALUEDOMAIN and MULTISELCTION=true
        "NVARCHAR2(1020)", // DIConstants.VTYPE_PASSWORD
        "DATE",          // DIConstants.VTYPE_REMINDER
    };

    /**
     * Table with the database field types for DB2. <br/>
     * <br/>
     * <b>Notes</b>: 
     * <ul>
     *   <li>
     *     When using DB2 it is not possible to define just a data-field that
     *     stores unicode characters. Either the whole database is set up to 
     *     use unicode or not.  
     *   </li>
     *   <li> 
     *     The length of a NVARCHAR2 is specified in byte. When using UTF-8
     *     a character is encoded as a variable number of bytes with a 
     *     maximum length of 4 bytes. Because of that the maximum length of a 
     *     String that should contain not more than 255 characters is 
     *     4*255 = 1020.
     *   </li>
     * </ul>
     */
    private static final String[] TYPETABLE_DB2 =
    {
        "VARCHAR(1020)",  // DIConstants.VTYPE_CHAR
        "VARCHAR(1020)",  // DIConstants.VTYPE_TEXT
        "CLOB",          // DIConstants.VTYPE_LONGTEXT
        "CLOB",          // DIConstants.VTYPE_HTMLTEXT
        "TIMESTAMP",     // DIConstants.VTYPE_DATE
        "TIMESTAMP",     // DIConstants.VTYPE_TIME
        "TIMESTAMP",     // DIConstants.VTYPE_DATETIME
        "SMALLINT",      // DIConstants.VTYPE_BOOLEAN
        "INTEGER",       // DIConstants.VTYPE_INT
        "REAL",          // DIConstants.VTYPE_FLOAT
        "DOUBLE",        // DIConstants.VTYPE_DOUBLE
        "DECIMAL(19,4)", // DIConstants.VTYPE_NUMBER
        "DECIMAL(19,4)", // DIConstants.VTYPE_MONEY
        "VARCHAR(1020)",  // DIConstants.VTYPE_FILE
        "VARCHAR(1020)",  // DIConstants.VTYPE_URL
        "VARCHAR(508)",  // DIConstants.VTYPE_EMAIL
        "CLOB",          // DIConstants.VTYPE_OPTION
        "VARCHAR(1020)",  // DIConstants.VTYPE_IMAGE
/*
        TYPE_NOTMAPPABLE, // DIConstants.VTYPE_SEPARATOR
        TYPE_NOTMAPPABLE, // DIConstants.VTYPE_REMARK
        TYPE_NOTMAPPABLE, // DIConstants.VTYPE_BUTTON
*/
        "VARCHAR(1020)",  // DIConstants.VTYPE_OBJECTREF
        "REAL",          // DIConstants.VTYPE_QUERYSELECTIONBOXNUM
        "INTEGER",       // DIConstants.VTYPE_QUERYSELECTIONBOXINT
        "VARCHAR(1020)",  // DIConstants.VTYPE_QUERYSELECTIONBOX
        "VARCHAR(8000)",  // DIConstants.VTYPE_QUERYSELECTIONBOX and MULTISELCTION=true
        "REAL",          // DIConstants.VTYPE_SELECTIONBOXNUM
        "INTEGER",       // DIConstants.VTYPE_SELECTIONBOXINT
        "VARCHAR(1020)",  // DIConstants.VTYPE_SELECTIONBOX
        "VARCHAR(32000)",  // DIConstants.VTYPE_SELECTIONBOX and MULTISELCTION=true
        "VARCHAR(8) FOR BIT DATA", // DIConstants.VTYPE_FIELDREF
        "VARCHAR(8) FOR BIT DATA", // DIConstants.VTYPE_VALUEDOMAIN
        "VARCHAR(32000)", // DIConstants.VTYPE_VALUEDOMAIN and MULTISELCTION=true
        "VARCHAR(1020)",  // DIConstants.VTYPE_PASSWORD
        "TIMESTAMP",     // DIConstants.VTYPE_REMINDER
    };

    /**
     * Table with parameter types for the stored procedures. <BR/>
     */
    private static final short[] PARAMTYPETABLE =
    {
        ParameterConstants.TYPE_STRING,     // DIConstants.VTYPE_CHAR
        ParameterConstants.TYPE_STRING,     // DIConstants.VTYPE_TEXT
        ParameterConstants.TYPE_STRING,     // DIConstants.VTYPE_LONGTEXT
        ParameterConstants.TYPE_STRING,     // DIConstants.VTYPE_HTMLTEXT
        ParameterConstants.TYPE_DATE,       // DIConstants.VTYPE_DATE
        ParameterConstants.TYPE_DATE,       // DIConstants.VTYPE_TIME
        ParameterConstants.TYPE_DATE,       // DIConstants.VTYPE_DATETIME
        ParameterConstants.TYPE_BOOLEAN,    // DIConstants.VTYPE_BOOLEAN
        ParameterConstants.TYPE_INTEGER,    // DIConstants.VTYPE_INT
        ParameterConstants.TYPE_DOUBLE,     // DIConstants.VTYPE_FLOAT
        ParameterConstants.TYPE_DOUBLE,     // DIConstants.VTYPE_DOUBLE
        ParameterConstants.TYPE_DOUBLE,     // DIConstants.VTYPE_NUMBER
        ParameterConstants.TYPE_CURRENCY,   // DIConstants.VTYPE_MONEY
        ParameterConstants.TYPE_STRING,     // DIConstants.VTYPE_FILE
        ParameterConstants.TYPE_STRING,     // DIConstants.VTYPE_URL
        ParameterConstants.TYPE_STRING,     // DIConstants.VTYPE_EMAIL
        ParameterConstants.TYPE_STRING,     // DIConstants.VTYPE_OPTION
        ParameterConstants.TYPE_STRING,     // DIConstants.VTYPE_IMAGE
/*
        PARAM_TYPE_NOTMAPPABLE,             // DIConstants.VTYPE_SEPARATOR
        PARAM_TYPE_NOTMAPPABLE,             // DIConstants.VTYPE_REMARK
        PARAM_TYPE_NOTMAPPABLE,             // DIConstants.VTYPE_BUTTON
*/
        ParameterConstants.TYPE_STRING,     // DIConstants.VTYPE_OBJECTREF
        ParameterConstants.TYPE_DOUBLE,     // DIConstants.VTYPE_QUERYSELECTIONBOXNUM
        ParameterConstants.TYPE_INTEGER,    // DIConstants.VTYPE_QUERYSELECTIONBOXINT
        ParameterConstants.TYPE_STRING,     // DIConstants.VTYPE_QUERYSELECTIONBOX
        ParameterConstants.TYPE_STRING,     // DIConstants.VTYPE_QUERYSELECTIONBOX and MULTISELCTION=true
        ParameterConstants.TYPE_DOUBLE,     // DIConstants.VTYPE_SELECTIONBOXNUM
        ParameterConstants.TYPE_INTEGER,    // DIConstants.VTYPE_SELECTIONBOXINT
        ParameterConstants.TYPE_STRING,     // DIConstants.VTYPE_SELECTIONBOX
        ParameterConstants.TYPE_STRING,     // DIConstants.VTYPE_SELECTIONBOX and MULTISELCTION=true
        ParameterConstants.TYPE_STRING,     // DIConstants.VTYPE_FIELDREF
        ParameterConstants.TYPE_STRING,     // DIConstants.VTYPE_VALUEDOMAIN
        ParameterConstants.TYPE_STRING,     // DIConstants.VTYPE_VALUEDOMAIN and MULTISELCTION=true
        ParameterConstants.TYPE_STRING,     // DIConstants.VTYPE_PASSWORD
        ParameterConstants.TYPE_DATE,       // DIConstants.VTYPE_REMINDER
    };

    /**
     * The prefix for all mapping table names. <BR/>
     */
    public static final String MAPPING_TABLE_PREFIX = "dbm_";

    /**
     * The prefix for temporary mapping table names during type translation. <BR/>
     */
    public static final String TYPE_TRANSLATION_TEMPORARY_MAPPING_TABLE_PREFIX = "tmp_";

    /**
     * The prefix for the recovery copy of the mapping table during type translation. <BR/>
     */
    public static final String TYPE_TRANSLATION_RECOVERY_TABLE_PREFIX = "rec_";

    /**
     * The prefix for all field names. <BR/>
     */
    private static final String MAPPING_FIELD_PREFIX = "m_";

    /**
     * The prefix for all stored procedures. <BR/>
     */
    private static final String MAPPING_PROC_PREFIX = "p_";

    /**
     * The maximum name length of a table in the database. <BR/>
     * We cannot use the maximum characters (30) for the
     * table name because the names for the stored procedures
     * are composed by the table name and the specific procedure
     * name.
     */
    private static final int MAX_TABLENAME_LENGTH = 20;

    /**
     * The maximum name length of a table field in the database. <BR/>
     */
    private static final int MAX_FIELDNAME_LENGTH = 30;

    /**
     * The logging object for status and error reports. <BR/>
     */
    private Log_01 logObject;

    /**
     * Type of database. <BR/>
     * This property contains the type of the currently used database.
     */
    private int p_dbType = SQLConstants.DB_MSSQL;

    /**
     * The type table. <BR/>
     * This value is set base on the {@link #p_dbType} property.
     */
    private String[] p_typeTable = DBMapper.TYPETABLE_SQL;



    /**************************************************************************
     * The constructor for a DBMapper object. <BR/>
     *
     * @param   user    ???
     * @param   env     ???
     * @param   sess    ???
     * @param   app     ???
     */
    public DBMapper (User user, Environment env, SessionInfo sess, ApplicationInfo app)
    {
        // call the constructor of the super class
        super ();

        // initialize the business object:
        this.initObject (OID.getEmptyOid (), user, env, sess, app);

        // create the log object
        this.logObject = new Log_01 ();
        this.logObject.initObject (OID.getEmptyOid (), user, env, sess, app);
        this.logObject.isDisplayLog = false;
        this.logObject.isWriteLog = false;

        // get the DBMS type
        this.setDbType (SQLConstants.DB_TYPE);

        // do some consistency checks
        this.checkAssertion (DBMapper.VALUETYPETABLE.length ==
            DBMapper.TYPETABLE_SQL.length);
        this.checkAssertion (DBMapper.VALUETYPETABLE.length ==
            DBMapper.TYPETABLE_ORACLE.length);
        this.checkAssertion (DBMapper.VALUETYPETABLE.length ==
            DBMapper.PARAMTYPETABLE.length);
    } // DBMapper


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // nothing to do, not even the functionality of the super class
    } // initClassSpecifics


    /**************************************************************************
     * Set the logging object of the DBMapper. <BR/>
     * This object holds all the error and status messages created
     * by the DBMapper. <BR/>
     *
     * @param   log     The logging object to be set.
     */
    public void setLogObject (Log_01 log)
    {
        this.logObject = log;
    } // setLogObject


    /**************************************************************************
     * Returns the logging object of the DBMapper. <BR/>
     * This object holds all the error and status messages created
     * by the DBMapper. <BR/>
     *
     * @return  the log object
     */
    public Log_01 getLogObject ()
    {
        return this.logObject;
    } // getLogObject


    /**************************************************************************
     * Validates the mapping info of the given data element. <BR/>
     * This data element is typicaly dereived from a template xml file.
     *
     * @param dataElement   the data element of the form type.
     *
     * @return  true on success otherwise false.
     */
    public boolean validateMappingInfo (DataElement dataElement)
    {
        return this.validateMappingInfo (dataElement, false);
    } // validateMappingInfo


    /**************************************************************************
     * Validates the mapping info of the given data element. <BR/>
     * This data element is typicaly dereived from a template xml file.
     *
     * @param dataElement   the data element of the form type.
     * @param isMappingToTempTranslTableAllowed
     *                      is the mapping to the temporary type translation
     *                      table performed
     *
     * @return  true on success otherwise false.
     */
    public boolean validateMappingInfo (DataElement dataElement, boolean isMappingToTempTranslTableAllowed)
    {
        String tableName = dataElement.tableName;
        // check the table name
        // the table name must have a the prefix
        if (tableName == null)
        {
            this.error (
                MultilingualTextProvider.getMessage (ServiceMessages.MSG_BUNDLE,
                    ServiceMessages.ML_MSG_INVALID_TABLENAME, env) + "null");
            return false;
        } // if (tableName == null)

        // the table prefix must be either the standard prefix
        if (!tableName.startsWith (DBMapper.MAPPING_TABLE_PREFIX) &&
                // or the temporary prefix if a mapping to the temporary type translation table is performed
                !(isMappingToTempTranslTableAllowed &&
                        this.isTemporaryTable (DBMapper.TYPE_TRANSLATION_TEMPORARY_MAPPING_TABLE_PREFIX)))
        {
            this.error (
                MultilingualTextProvider.getMessage (ServiceMessages.MSG_BUNDLE,
                    ServiceMessages.ML_MSG_INVALID_TABLEPREFIX, env) + tableName);
            return false;
        } // if (! tableName.startsWith (this.MAPPING_TABLE_PREFIX))

        int len = tableName.length ();

        if (len <= DBMapper.MAPPING_TABLE_PREFIX.length () ||
            len > DBMapper.MAX_TABLENAME_LENGTH)
        {
            this.error (
                MultilingualTextProvider.getMessage (ServiceMessages.MSG_BUNDLE,
                    ServiceMessages.ML_MSG_INVALID_TABLENAME, env) +
        		" (max. " + DBMapper.MAX_TABLENAME_LENGTH + ") :" + 
        		tableName + 
        		" = " + len);
            		
            return false;
        } // if invalid length

        if (!this.isValidDatabaseIdentifier (tableName))
        {
            this.error (
                MultilingualTextProvider.getMessage (ServiceMessages.MSG_BUNDLE,
                    ServiceMessages.ML_MSG_INVALID_TABLENAME, env) + tableName);
            return false;
        } // if (!isValidDatabaseIdentifier (tableName))


        // this vector is used to find duplicate field names:
        Vector<String> v = new Vector<String> ();

        if (dataElement.values != null)
        {
            // now check the field names of all mapped fields:
            // loop trough the value elements:
            for (Iterator<ValueDataElement> iter = dataElement.values.iterator (); iter.hasNext ();)
            {
                // get the next value:
                ValueDataElement value = iter.next ();

                // QUERY and SEPARATOR fields do not have a mapping 
                if (value.type.equals (DIConstants.VTYPE_QUERY) ||
                    value.type.equals (DIConstants.VTYPE_SEPARATOR))
                {
                	// delete any dbmapping setting just in case it is set
                	value.mappingField = null;                	
                } // if
                // check if the value is mapped:                
                else if (value.mappingField != null)
                {
                    String field = value.mappingField;

                    // the field name must have a prefix
                    if (!field.startsWith (DBMapper.MAPPING_FIELD_PREFIX))
                    {
                        this.error (
                            MultilingualTextProvider.getMessage (ServiceMessages.MSG_BUNDLE,
                                ServiceMessages.ML_MSG_INVALID_FIELDNAME, env) + field);
                        return false;
                    } // if (! field.startsWith (this.MAPPING_FIELD_PREFIX))

                    // the field name a prefix and at least one character
                    if (field.equals (DBMapper.MAPPING_FIELD_PREFIX))
                    {
                        this.error (
                            MultilingualTextProvider.getMessage (ServiceMessages.MSG_BUNDLE,
                                ServiceMessages.ML_MSG_INVALID_FIELDNAME, env) + field);
                        return false;
                    } // if (field.equals (this.MAPPING_FIELD_PREFIX))

                    // check if the field name is already used:
                    // compare it with all previous field names
                    for (Iterator<String> iter2 = v.iterator (); iter2.hasNext ();)
                    {
                        if (iter2.next ().equalsIgnoreCase (field))
                        {
                            this.error (
                                MultilingualTextProvider.getMessage (ServiceMessages.MSG_BUNDLE,
                                    ServiceMessages.ML_MSG_DUPLICATE_FIELD, env) + field);
                            return false;
                        } // if field name already used
                    } // for iter2

                    if (!this.isValidDatabaseIdentifier (field))
                    {
                        this.error (
                            MultilingualTextProvider.getMessage (ServiceMessages.MSG_BUNDLE,
                                ServiceMessages.ML_MSG_INVALID_FIELDNAME, env) + field);
                        return false;
                    } // if (!isValidDatabaseIdentifier (field))

                    if (field.length () > DBMapper.MAX_FIELDNAME_LENGTH)
                    {
                        this.error (
                            MultilingualTextProvider.getMessage (ServiceMessages.MSG_BUNDLE,
                                ServiceMessages.ML_MSG_INVALID_FIELDNAME, env) + field);
                        return false;
                    } // if (field.length () > this.MAX_FIELDNAME_LENGTH)

                    // determinate the correct parameter type for the value:
                    short paramType = this.getParamType (value);

                    // check if the value type is mappable
                    if (paramType == ParameterConstants.TYPE_UNDEFINED)
                    {
                        this.error (
                                MultilingualTextProvider.getMessage (ServiceMessages.MSG_BUNDLE,
                                        ServiceMessages.MSG_MAPPING_NOTPOSSIBLE, env)
                                + " Field=" + value.field + ", Value=" + value.value);
                        return false;
                    } // if (paramType == ParameterConstants.TYPE_UNDEFINED)

                    v.addElement (field);
                } // if (value.mappingField != null)
                else
                {
                	// TODO: an empty mapping is possible for QUERYSELECTIONBOX
                	// and FIELDREF but note that this is a special case
                	// and must be considered a HACK when used
                	// for downward compatibility we keep this behaviour
                	if (!value.type.equals (DIConstants.VTYPE_QUERYSELECTIONBOX) &&
                		!value.type.equals (DIConstants.VTYPE_QUERYSELECTIONBOXINT) &&
                        !value.type.equals (DIConstants.VTYPE_QUERYSELECTIONBOXNUM) &&
                        !value.type.equals (DIConstants.VTYPE_FIELDREF))
                	{                	
	                    // field has no mapping info:
	                    this.error (MultilingualTextProvider.getMessage (ServiceMessages.MSG_BUNDLE,
                                ServiceMessages.MSG_MAPPING_NOTPOSSIBLE, env) +
                                " Field=" + value.field + ", Value=" + value.value);
	                    return false;
                	} // if (!value.type.equals (DIConstants.VTYPE_QUERYSELECTIONBOX) && ..
                } // else
            } // for iter
        } // if (dataElement.values == null)

        // check if we have at least one mapped field
        if (v.size () < 1)
        {
            this.error (
                MultilingualTextProvider.getMessage (ServiceMessages.MSG_BUNDLE,
                    ServiceMessages.ML_MSG_NO_FIELDS_FOUND, env));
            return false;
        } // if (v.size < 1)

        return true;
    } // validateMappingInfo


    /**************************************************************************
     * Returns the name of stored procedure used to copy mapping attributes. <BR/>
     * The stored procedure is called by the stored procedure p_XMLViewer$BOCopy.
     *
     * @param dataElement   the data element from a template xml file
     *
     * @return  ???
     */
    public StringBuffer getProcCopyName (DataElement dataElement)
    {
        StringBuffer tableName = this.getDatabaseTableName (dataElement);
        if (tableName == null)
        {
            return null;
        } // if
        return this.getCopyProcName (tableName);
    } // getProcCopyName

    /**************************************************************************
     * Creates the mapping table for the given form type. <BR/>
     *
     * @param dataElement   the data element of the form type.
     * @param isCreateTable flag to include creating the table
     *
     * @return  true on success otherwise false.
     */
    public boolean createFormTemplateDBTable (DataElement dataElement,
            boolean isCreateTable)
    {
        return this.createFormTemplateDBTable (dataElement,
                null,
                isCreateTable,
                true);
    } // createFormTemplateDBTable

    /**************************************************************************
     * Creates the mapping table for the given form type. <BR/>
     *
     * @param   dataElement         the data element of the form type.
     * @param   tableName           The table name for the table to create.
     *                              If no table name is provided the name from
     *                              the data element is taken. 
     * @param   isCreateTable       flag to include creating the table
     * @param   isCreateProcedures  Flag indicating if the stored procedures
     *                              should be created.
     *
     * @return  true on success otherwise false.
     */
    public boolean createFormTemplateDBTable (DataElement dataElement,
                                              StringBuffer tableName,
                                              boolean isCreateTable,
                                              boolean isCreateProcedures)
    {
        StringBuffer tableNameLocal = tableName; // variable for local assignments
        // do same consistency checks on the data element:
        if (!this.isValidDataElement (dataElement, false))
        {
            return false;
        } // if

        // validate the mapping info:
        if (!this.validateMappingInfo (dataElement))
        {
            return false;
        } // if

        // check if a table name has been provided
        if (tableNameLocal == null)
        {
            // get the name of the mapping table
            tableNameLocal = this.getDatabaseTableName (dataElement);
        } // if

        if (tableNameLocal == null)
        {
            return false;
        } // if

        // create the DDL for the database table form the data element
        // and the stored procedures to create/update/copy the data:

        StringBuffer ddl = new StringBuffer ();
        StringBuffer idx = new StringBuffer ();
        StringBuffer procInputParams = new StringBuffer ();
        StringBuffer procOutputParams = new StringBuffer ();
        StringBuffer procUpdateArgs = new StringBuffer ();
        StringBuffer procArgs = new StringBuffer ();
        StringBuffer procValues = new StringBuffer ();
        StringBuffer procRetrieveArgs = new StringBuffer ();
        StringBuffer procCopyArgs = new StringBuffer ();
        StringBuffer varPrefix;
        StringBuffer declareString = new StringBuffer (); // for declaring local variables
        // for performing a different stored procedure
        // used for converting an oidstring to an oid
        StringBuffer execString = new StringBuffer ();
        StringBuffer convertOidString = new StringBuffer ();

        //
        StringBuffer execByteToString = new StringBuffer ();
        StringBuffer createProc;        // create procedure
        StringBuffer updateProc;        // update procedure
        StringBuffer retrieveProc;      // retrieve procedure
        StringBuffer copyProc;          // copy procedure
        StringBuffer typeSpecificCreateString = new StringBuffer (); // type specific code necessary within the create procedure
        StringBuffer typeSpecificCopyString = new StringBuffer (); // type specific code necessary within the copy procedure
        StringBuffer procName;          // name of current procedure
        int attributeCnt = 0;
        Enumeration<ValueDataElement> values = dataElement.values.elements ();
        boolean reminderSpecCopyStringInitialized = false; // if reminder specific copy string has already been initialized

        // get the ddl and index definitions for the table:
        this.getTableHeader (tableNameLocal, ddl, idx);

        // define common code:
        switch (this.p_dbType)
        {
            case SQLConstants.DB_ORACLE:
                // define code for standard attributes:
                declareString.append ("DECLARE l_oid RAW (8);");

                procInputParams.append ("ai_oid_s VARCHAR2, ai_typeName VARCHAR2");
                procOutputParams.append ("ai_oid_s VARCHAR2, ao_typeName OUT VARCHAR2");
                procUpdateArgs.append ("typeName = ai_typeName");
                procRetrieveArgs.append ("ao_typeName");
                procArgs.append ("typeName");
                procValues.append ("l_oid, ai_typeName");
                // convert oidstring:
                convertOidString.append (" p_stringToByte (ai_oid_s, l_oid);");

                // set the prefix for all names of parameters and local variables
                // used in the stored procedures.
                varPrefix = new StringBuffer ("");
                break;

            case SQLConstants.DB_MSSQL:
                // define code for standard attributes:
                declareString.append ("DECLARE @l_oid OBJECTID ");

                procInputParams.append ("@ai_oid_s OBJECTIDSTRING, @ai_typeName NAME");
                procOutputParams.append ("@ai_oid_s OBJECTIDSTRING, @ao_typeName NAME OUTPUT");
                procUpdateArgs.append ("typeName = @ai_typeName");
                procRetrieveArgs.append ("@ao_typeName = typeName");
                procArgs.append ("typeName");
                procValues.append ("@l_oid, @ai_typeName");
                // convert oidstring:
                convertOidString.append (" EXEC p_stringToByte @ai_oid_s, @l_oid OUTPUT");

                // set the prefix for all names of parameters and local variables
                // used in the stored procedures.
                varPrefix = new StringBuffer ("@");
                break;

            case SQLConstants.DB_DB2:
                // define code for standard attributes:
                declareString.append ("DECLARE l_oid CHAR (8) FOR BIT DATA;");

                procInputParams.append ("IN ai_oid_s VARCHAR (18), IN ai_typeName VARCHAR (63)");
                procOutputParams.append ("IN ai_oid_s VARCHAR (18), OUT ao_typeName VARCHAR (63)");
                procUpdateArgs.append ("typeName = ai_typeName");
                procRetrieveArgs.append ("ao_typeName");
                procArgs.append ("typeName");
                procValues.append ("l_oid, ai_typeName");
                // convert oidstring:
                convertOidString.append (" CALL p_stringToByte (ai_oid_s, l_oid);");

                // set the prefix for all names of parameters and local variables
                // used in the stored procedures.
                varPrefix = new StringBuffer ("");
                break;

            default:
                IOHelpers.showMessage ("database type " + this.p_dbType + " not supported: ",
                                       this.app, this.sess, this.env);
                varPrefix = new StringBuffer ("");
        } // switch

        boolean firstMultipleSelectionField = true;

        // loop through the value elements:
        while (values.hasMoreElements ())
        {
            // get the next value:
            ValueDataElement value = values.nextElement ();
            // get the database field name for the value:
            String fieldName = value.mappingField;
            String fieldInArg = varPrefix + "ai_" + fieldName;
            String fieldOutArg = varPrefix + "ao_" + fieldName;
            String fieldVar = fieldInArg;

            if (fieldName != null)
            {
                // get the database field type for the value.
                String fieldType = this.getDatabaseFieldType (value);
                if (fieldType == null || fieldType.length () == 0)
                {
                    IOHelpers.showMessage ("invalid field type: '" + fieldType + "'.",
                                           this.app, this.sess, this.env);
                    return false;
                } // invalid field type

        // **********  mark special fieldtypes begin ****************

                // large text fields cannot be handled with a stored procedure
                // this fields are thread separatly.
                boolean isLargeTextField = this.isTextField (fieldType);

                // check if type of current field is stored in db as OID
                boolean isOidField = this.isOidField  (value);

        // **********  mark special fieldtypes end ****************


                // large text attributes are not passed
                // to the create/update/retrieve stored procedures.
                if (!isLargeTextField)
                {
                    procInputParams.append (",\n ");
                    procOutputParams.append (",\n ");
                    procUpdateArgs.append (",\n ");
                    procRetrieveArgs.append (",\n ");
                    procArgs.append (",\n ");
                    procValues.append (",\n ");
                } // if (!largeTextField)

                ddl.append (", ").append (fieldName).append (" ").append (fieldType);
                // large text attributes are only handled
                // in the copy procedure.
                procCopyArgs.append (", ").append (fieldName);

                // special handling of oid fields:
                // create declarestring for local sql-variables for OID-Fields
                if (isOidField)
                {
                    // set postfix for input argument:
                    fieldInArg += "_s";
                    // set name of local variable:
                    fieldVar = varPrefix + "l_" + fieldName;

                    // define variable declarations and type conversions for
                    // oid field:
                    switch (this.p_dbType)
                    {
                        case SQLConstants.DB_ORACLE:
                            declareString
                                .append ("\nDECLARE ").append (fieldVar)
                                .append (" RAW (8);");

                            execString
                                .append (" p_stringToByte (")
                                .append (fieldInArg).append (", ")
                                .append (fieldVar).append (");");

                            execByteToString
                                .append (" p_byteToString (")
                                .append (fieldVar).append (", ")
                                .append (fieldOutArg)
                                .append (");");
                            break;

                        case SQLConstants.DB_MSSQL:
                            declareString
                                .append ("\nDECLARE ").append (fieldVar)
                                .append (" OBJECTID ");

                            execString
                                .append (" EXEC p_stringToByte ")
                                .append (fieldInArg).append (", ")
                                .append (fieldVar).append (" OUTPUT");

                            execByteToString
                                .append (" EXEC p_byteToString ")
                                .append (fieldVar).append (", ")
                                .append (fieldOutArg).append (" OUTPUT ");
                            break;

                        case SQLConstants.DB_DB2:
                            declareString
                                .append ("\nDECLARE ").append (fieldVar)
                                .append (" CHAR (8) FOR BIT DATA;");

                            execString
                                .append (" CALL p_stringToByte (")
                                .append (fieldInArg).append (", ")
                                .append (fieldVar).append (");");

                            execByteToString
                                .append (" CALL p_byteToString (")
                                .append (fieldVar).append (", ")
                                .append (fieldOutArg)
                                .append (");");
                            break;

                        default:
                            IOHelpers.showMessage ("database type " + this.p_dbType + " not supported: ",
                                                   this.app, this.sess, this.env);
                    } // switch
                } // isOidField

                //Sets the BO Constant for the given referenced object field type.
                int referencedObjectFieldBo = 0;

                // check if
                if (DIConstants.VTYPE_FIELDREF.equals (value.type))
                {
                    referencedObjectFieldBo =  BOConstants.REF_FIELDREF;
                } // if
                else if (DIConstants.VTYPE_VALUEDOMAIN.startsWith (value.type))
                {
                    referencedObjectFieldBo =  BOConstants.REF_VALUEDOMAIN;
                } // else
                else if (DIConstants.VTYPE_REMINDER.equals (value.type))
                {
                    // add reminder specific code to type specific create string:
                    if (tableName == null || // is the cases during recreation of procedures after a type translation
                        !this.isTemporaryTable (tableName.toString ())) // is the normal case (no type translation)
                    {                   
                        typeSpecificCreateString.
                            append ("\n INSERT INTO ibs_Reminder (oid, fieldDbName) ").
                            append ("VALUES (@l_oid, ").
                            append(SQLHelpers.getUnicodeString (value.mappingField)).append(")");
                    } // if
                    else // type translation case
                    {
                        typeSpecificCreateString.
                            append ("\n IF NOT EXISTS (SELECT * FROM ibs_Reminder WHERE oid = @l_oid and fieldDbName = ").
                                append(SQLHelpers.getUnicodeString (value.mappingField)).append(")").
                            append ("\n     INSERT INTO ibs_Reminder (oid, fieldDbName) ").
                                append ("VALUES (@l_oid, ").
                                append(SQLHelpers.getUnicodeString (value.mappingField)).append(")");
                    } // else

                    // check if the type specific copy has not been already initialized since there is only one copy statement necessary
                    if (!reminderSpecCopyStringInitialized)
                    {
                        // add reminder specific code to type specific copy string
                        typeSpecificCopyString.
                            append ("\n INSERT INTO ibs_Reminder (oid, fieldDbName, reminderDate, remind1Days, remind1Text, remind1Recip, remind2Days, remind2Text, ").
                            append ("remind2Recip, escalateDays, escalateText, escalateRecip) ").
                            append ("\n SELECT @ai_newOid, fieldDbName, reminderDate, remind1Days, remind1Text, remind1Recip, remind2Days, remind2Text, ").
                            append ("remind2Recip, escalateDays, escalateText, escalateRecip").
                            append ("\n FROM ibs_Reminder").
                            append ("\n WHERE oid = @ai_oid");
                        
                        reminderSpecCopyStringInitialized = true;
                    } // if
                } // else

                boolean multiSelection = value.multiSelection != null &&
                    value.multiSelection.equalsIgnoreCase (DIConstants.ATTRVAL_YES);

                // create entry in ibs_Reference table for
                // referenced object fields to be considered in copy and delete of objects
                if (DIConstants.VTYPE_FIELDREF.equals (value.type) ||
                        (DIConstants.VTYPE_VALUEDOMAIN.equals (value.type) && !multiSelection))
                {
                    switch (this.p_dbType)
                    {
                        case SQLConstants.DB_ORACLE:
                            execString.append (" p_Reference$create (l_oid,")
                                .append ("'").append (value.field).append ("', ")
                                .append (fieldVar).append (", ")
                                .append (referencedObjectFieldBo).append (");");
                            break;

                        case SQLConstants.DB_MSSQL:
                            execString.append (" EXEC p_Reference$create @l_oid, ")
                                .append ("'").append (value.field).append ("', ")
                                .append (fieldVar).append (", ")
                                .append (referencedObjectFieldBo);
                            break;

                        case SQLConstants.DB_DB2:
                            execString.append (" CALL p_Reference$create (l_oid,")
                                .append ("'").append (value.field).append ("', ")
                                .append (fieldVar).append (", ")
                                .append (referencedObjectFieldBo).append (");");
                            break;

                        default:
                            IOHelpers.showMessage ("database type " + this.p_dbType + " not supported: ",
                                                   this.app, this.sess, this.env);
                    } // switch
                } // if

                // create multiple entries in ibs_Reference table for
                // value domain fields to be considered in copy and delete of objects
                if (DIConstants.VTYPE_VALUEDOMAIN.equals (value.type) && multiSelection)
                {
                    switch (this.p_dbType)
                    {
                        case SQLConstants.DB_MSSQL:
                            if (firstMultipleSelectionField)
                            {
                                declareString
                                    .append ("\nDECLARE @l_count INT, ")
                                    .append ("@l_refOid_s OBJECTIDSTRING, ")
                                    .append ("@l_refOid OBJECTID");

                                //set the flag to false to disable further equal declare statements
                                firstMultipleSelectionField = false;
                            } // if

                            execString.
                                append ("\n    EXEC p_Reference$delete @l_oid, '").
                                append (value.field).append ("'").
                                append ("\nSELECT @l_count = (").
                                append (SQLHelpers.getLengthExpression (fieldInArg)).
                                append (" + 1) / 19 - 1");

                            execString.
                                append ("\n\nWHILE (@l_count > -1)").
                                append ("\nBEGIN").
                                append ("\n    SELECT @l_refOid_s = ").
                                    append (SQLHelpers.getSubstringExpression (
                                            fieldInArg, "@l_count * 19 + 1", "18")).
                                append ("\n    EXEC p_stringToByte @l_refOid_s, @l_refOid OUTPUT").
                                append ("\n    EXEC p_Reference$create @l_oid, '").
                                    append (value.field).append ("', @l_refOid, ").
                                    append (BOConstants.REF_MULTIPLE).
                                append ("\n    SELECT @l_count = @l_count - 1").
                                append ("\nEND");
                            break;

                        default:
                            IOHelpers.showMessage ("database type " + this.p_dbType + " not supported: ",
                                                   this.app, this.sess, this.env);
                    } // switch
                } // if

                // large text attributes are not passed
                // to the create/update stored procedures.
                if (!isLargeTextField)
                {
                    switch (this.p_dbType)
                    {
                        case SQLConstants.DB_ORACLE:
                            // eliminate the size argument from type definitions
                            // for arguments:
                            int pos;
                            if ((pos = fieldType.indexOf ("(")) >= 0)
                            {
                                fieldType = fieldType.substring (0, pos);
                            } // if

                            // create the paramater list for stored procedures:
                            procInputParams.append (fieldInArg)
                                .append (isOidField ? " VARCHAR2" : " " + fieldType);
                            procOutputParams.append (fieldOutArg)
                                .append (" OUT ").append (fieldType);
                            procRetrieveArgs.append (fieldOutArg);
                            procUpdateArgs.append (fieldName).append (" = ")
                                .append (fieldVar);
                            procValues.append (fieldVar);
                            procArgs.append (fieldName);
                            break;

                        case SQLConstants.DB_MSSQL:
                            // create the paramater list for stored procedures:
                            procInputParams.append (fieldInArg)
                                .append (isOidField ? " OBJECTIDSTRING" : " " + fieldType);
                            procOutputParams.append (fieldOutArg).append (" ")
                                .append (fieldType).append (" OUTPUT");
                            procRetrieveArgs.append (fieldOutArg).append (" = ")
                                .append (fieldName);
                            procUpdateArgs.append (fieldName).append (" = ")
                                .append (fieldVar);
                            procValues.append (fieldVar);
                            procArgs.append (fieldName);
                            break;

                        case SQLConstants.DB_DB2:
                            IOHelpers.showMessage ("database type " + this.p_dbType + " not supported: ",
                                                   this.app, this.sess, this.env);
                            // create the paramater list for stored procedures:
                            procInputParams.append ("IN ").append (fieldInArg)
                                .append (isOidField ? " VARCHAR (18)" : " " + fieldType);
                            procOutputParams.append ("OUT ").append (fieldOutArg)
                                .append (" ").append (fieldType);
                            procRetrieveArgs.append (fieldOutArg);
                            procUpdateArgs.append (fieldName).append (" = ")
                                .append (fieldVar);
                            procValues.append (fieldVar);
                            procArgs.append (fieldName);
                            break;

                        default:
                            IOHelpers.showMessage ("database type " + this.p_dbType + " not supported: ",
                                                   this.app, this.sess, this.env);
                    } // switch
                } // if (!largeTextField)

                // increment the number of attributes:
                attributeCnt++;
            } // if (fieldName != null)
        } // while (values.hasMoreElements())

        if (attributeCnt == 0)
        {
            this.error (
                MultilingualTextProvider.getMessage (ServiceMessages.MSG_BUNDLE,
                    ServiceMessages.ML_MSG_NO_FIELDS_FOUND, env));
            return false;
        } // if (attributeCnt == 0)

        // initialize the several procedures:
        createProc = new StringBuffer ();
        updateProc = new StringBuffer ();
        retrieveProc = new StringBuffer ();
        copyProc = new StringBuffer ();

        // the DDL is database dependent:
        switch (this.p_dbType)
        {
            case SQLConstants.DB_ORACLE:
                ddl.append (") /*TABLESPACE*/;");

                // construct the stored procedure to insert an object:
                procName = this.getCreateProcName (tableNameLocal);
                createProc
                    .append ("CREATE FUNCTION ").append (procName)
                    .append ("\n (" + procInputParams).append (")")
                    .append ("\n RETURN INTEGER AS ")
                    .append (declareString)
                    .append ("\nBEGIN \n")
                    .append ("\n ").append (convertOidString)
                    .append ("\n ").append (execString)
                    .append ("\n\n INSERT INTO ").append (tableNameLocal)
                    .append ("\n (oid, ").append (procArgs).append (")")
                    .append ("\n VALUES (").append (procValues).append (");")
                    .append ("\n ").append (typeSpecificCreateString).append (";")
                    .append ("\n COMMIT WORK;")
                    .append ("\n RETURN 1;")
                    .append ("\n\nEND ").append (procName).append (";");
//trace (createProc);

                // construct the stored procedure to update an object:
                procName = this.getUpdateProcName (tableNameLocal);
                updateProc
                    .append ("CREATE FUNCTION ").append (procName)
                    .append ("\n (").append (procInputParams).append (")")
                    .append ("\n RETURN INTEGER AS ")
                    .append (declareString).
                    append ("\nBEGIN \n ")
                    .append ("\n ").append (convertOidString)
                    .append ("\n ").append (execString)
                    .append ("\n\n UPDATE ").append (tableNameLocal)
                    .append ("\n SET ").append (procUpdateArgs)
                    .append ("\n WHERE oid = l_oid;")
                    .append ("\n COMMIT WORK;")
                    .append ("\n RETURN 1;")
                    .append ("\n\nEND ").append (procName).append (";");
//trace (updateProc);

                // construct the stored procedure to retrieve an object:
                procName = this.getRetrieveProcName (tableNameLocal);
                retrieveProc
                    .append ("CREATE FUNCTION ").append (procName)
                    .append ("\n (").append (procOutputParams).append (")")
                    .append ("\n RETURN INTEGER AS ")
                    .append (declareString)
                    .append ("\nBEGIN \n")
                    .append ("\n ").append (convertOidString)
                    .append ("\n\n SELECT ").append (procArgs)
                    .append ("\n INTO ").append (procRetrieveArgs)
                    .append ("\n FROM ").append (tableNameLocal)
                    .append ("\n WHERE oid = l_oid;")
/* BB20070903: the conversion is not neccessary
                    .append ("\n ").append (execByteToString)
*/
                    .append ("\n COMMIT WORK;")
                    .append ("\n RETURN 1;")
                    .append ("\n\nEND ").append (procName).append (";");
//trace (retrieveProc);

                // construct the stored procedure to copy an object:
                procName = this.getCopyProcName (tableNameLocal);
                copyProc
                    .append ("CREATE FUNCTION ").append (procName)
                    .append ("\n (ai_oid RAW, ai_newOid RAW)")
                    .append ("\n RETURN INTEGER AS ")
                    .append ("\nBEGIN \n")
                    .append ("\n INSERT INTO ").append (tableNameLocal)
                    .append ("\n (oid, typeName").append (procCopyArgs).append (")")
                    .append ("\n\n SELECT ai_newOid, typeName").append (procCopyArgs)
                    .append ("\n FROM ").append (tableNameLocal)
                    .append ("\n WHERE oid = ai_oid;")
                    .append ("\n ").append (typeSpecificCopyString).append (";")
                    .append ("\n COMMIT WORK;")
                    .append ("\n RETURN 1;")
                    .append ("\n\nEND ").append (procName).append (";");
//trace (copyProc);
                break;

            case SQLConstants.DB_MSSQL:
                ddl.append (")");

                // construct the stored procedure to insert a object
                procName = this.getCreateProcName (tableNameLocal);
                createProc
                    .append ("CREATE PROCEDURE ").append (procName)
                    .append ("\n (").append (procInputParams).append (") AS")
                    .append ("\n ").append (declareString)
                    .append ("\nBEGIN TRANSACTION\n")
                    .append ("\n ").append (convertOidString)
                    .append ("\n ").append (execString)
                    .append ("\n\n INSERT INTO ").append (tableNameLocal)
                    .append ("\n (oid, ").append (procArgs).append (")")
                    .append ("\n VALUES (").append (procValues).append (")")
                    .append ("\n ").append (typeSpecificCreateString)
                    .append ("\n\nCOMMIT TRANSACTION")
                    .append ("\nRETURN 1");
//trace (createProc);

                // construct the stored procedure to update an object:
                procName = this.getUpdateProcName (tableNameLocal);
                updateProc
                    .append ("CREATE PROCEDURE ").append (procName)
                    .append ("\n (").append (procInputParams).append (") AS")
                    .append ("\n ").append (declareString)
                    .append ("\nBEGIN TRANSACTION\n")
                    .append ("\n ").append (convertOidString)
                    .append ("\n ").append (execString)
                    .append ("\n\n UPDATE ").append (tableNameLocal)
                    .append ("\n SET ").append (procUpdateArgs)
                    .append ("\n WHERE oid = @l_oid")
                    .append ("\n\nCOMMIT TRANSACTION")
                    .append ("\nRETURN 1");
//trace (updateProc);

                // construct the stored procedure to retrieve an object:
                procName = this.getRetrieveProcName (tableNameLocal);
                retrieveProc
                    .append ("CREATE PROCEDURE ").append (procName)
                    .append ("\n (").append (procOutputParams).append (") AS")
                    .append ("\n ").append (declareString)
                    .append ("\nBEGIN TRANSACTION\n")
                    .append ("\n ").append (convertOidString)
                    .append ("\n\n SELECT ").append (procRetrieveArgs)
                    .append ("\n FROM ").append (tableNameLocal)
                    .append ("\n WHERE oid = @l_oid ")
/* BB20070903: the conversion is not neccessary
                    .append (execByteToString)
*/
                    .append ("\n\nCOMMIT TRANSACTION")
                    .append ("\nRETURN 1");
//trace (retrieveProc);

                // construct the stored procedure to copy an object:
                procName = this.getCopyProcName (tableNameLocal);
                copyProc
                    .append ("CREATE PROCEDURE ").append (procName)
                    .append ("\n (@ai_oid OBJECTID, @ai_newOid OBJECTID) AS")
                    .append ("\nBEGIN TRANSACTION \n")
                    .append ("\n INSERT INTO ").append (tableNameLocal)
                    .append ("\n (oid, typeName").append (procCopyArgs).append (")")
                    .append ("\n\n SELECT @ai_newOid, typeName").append (procCopyArgs)
                    .append ("\n FROM ").append (tableNameLocal)
                    .append ("\n WHERE oid = @ai_oid")
                    .append ("\n ").append (typeSpecificCopyString)
                    .append ("\n\nCOMMIT TRANSACTION")
                    .append ("\nRETURN 1");
//trace (copyProc);
                break;

            case SQLConstants.DB_DB2:
                ddl.append (");");

                // construct the stored procedure to insert an object:
                procName = this.getCreateProcName (tableNameLocal);
                createProc
                    .append ("CREATE PROCEDURE ").append (procName)
                    .append ("\n (" + procInputParams).append (")")
                    .append ("\n DYNAMIC RESULT SETS 1 LANGUAGE SQL")
                    .append ("\nBEGIN \n")
                    .append ("\n ").append (declareString)
                    .append ("\n ").append (convertOidString)
                    .append ("\n ").append (execString)
                    .append ("\n\n INSERT INTO ").append (tableNameLocal)
                    .append ("\n (oid, ").append (procArgs).append (")")
                    .append ("\n VALUES (").append (procValues).append (");")
                    .append ("\n ").append (typeSpecificCreateString).append (";")
                    .append ("\n COMMIT;")
                    .append ("\n RETURN 1;")
                    .append ("\n\nEND;");
//trace (createProc);

                // construct the stored procedure to update an object:
                procName = this.getUpdateProcName (tableNameLocal);
                updateProc
                    .append ("CREATE PROCEDURE ").append (procName)
                    .append ("\n (").append (procInputParams).append (")")
                    .append ("\n DYNAMIC RESULT SETS 1 LANGUAGE SQL")
                    .append ("\nBEGIN \n")
                    .append ("\n ").append (declareString)
                    .append ("\n ").append (convertOidString)
                    .append ("\n ").append (execString)
                    .append ("\n\n UPDATE ").append (tableNameLocal)
                    .append ("\n SET ").append (procUpdateArgs)
                    .append ("\n WHERE oid = l_oid;")
                    .append ("\n COMMIT;")
                    .append ("\n RETURN 1;")
                    .append ("\n\nEND;");
//trace (updateProc);

                // construct the stored procedure to retrieve an object:
                procName = this.getRetrieveProcName (tableNameLocal);
                retrieveProc
                    .append ("CREATE PROCEDURE ").append (procName)
                    .append ("\n (").append (procOutputParams).append (")")
                    .append ("\n DYNAMIC RESULT SETS 1 LANGUAGE SQL")
                    .append ("\nBEGIN \n")
                    .append ("\n ").append (declareString)
                    .append ("\n ").append (convertOidString)
                    .append ("\n\n SELECT ").append (procArgs)
                    .append ("\n INTO ").append (procRetrieveArgs)
                    .append ("\n FROM ").append (tableNameLocal)
                    .append ("\n WHERE oid = l_oid;")
/* BB20070903: the conversion is not neccessary

                    .append (execByteToString)
*/
                    .append ("\n COMMIT;")
                    .append ("\n RETURN 1;")
                    .append ("\n\nEND;");
//trace (retrieveProc);

                // construct the stored procedure to copy an object:
                procName = this.getCopyProcName (tableNameLocal);
                copyProc
                    .append ("CREATE FUNCTION ").append (procName)
                    .append ("\n (IN ai_oid CHAR (8) FOR BIT DATA, IN ai_newOid CHAR (8) FOR BIT DATA)")
                    .append ("\n DYNAMIC RESULT SETS 1 LANGUAGE SQL ")
                    .append ("\nBEGIN \n")
                    .append ("\n INSERT INTO ").append (tableNameLocal)
                    .append ("\n (oid, typeName").append (procCopyArgs).append (")")
                    .append ("\n\n SELECT ai_newOid, typeName").append (procCopyArgs)
                    .append ("\n FROM ").append (tableNameLocal)
                    .append ("\n WHERE oid = ai_oid;")
                    .append ("\n ").append (typeSpecificCopyString).append (";")
                    .append ("\n COMMIT;")
                    .append ("\n RETURN 1;")
                    .append ("\n\nEND;");
//trace (copyProc);
                break;

            default:
                IOHelpers.showMessage ("database type " + this.p_dbType + " not supported: ",
                                       this.app, this.sess, this.env);
        } // switch


        // execute the sql statements:

        // shall the table be created?
        if (isCreateTable)
        {
            if (!this.executeSQL (ddl))
            {
                this.dbg ("create table failed!");
                return false;
            } // if (! executeSQLStatement (ddl, true))

            // execute the sql statements:
            if (!this.executeSQL (idx))
            {
                this.dbg ("create index failed! : " + idx);
                return false;
            } // if (! executeSQLStatement (ddl, true))
        } // if (isCreateTable)

        // shall the procedures be created?
        if (isCreateProcedures)
        {
            // execute the sql statements:
            if (!this.executeSQL (createProc))
            {
                this.dbg ("create createProc failed!");
                return false;
            } // if (! executeSQLStatement (createProc, true))

            // execute the sql statements:
            if (!this.executeSQL (updateProc))
            {
                this.dbg ("create updateProc failed!");
                return false;
            } // if (! executeSQLStatement (updateProc, true))

            // execute the sql statements:
            if (!this.executeSQL (retrieveProc))
            {
                this.dbg ("create retrieveProc failed!");
                return false;
            } // if (! executeSQL (retrieveProc))

            // execute the sql statements:
            if (!this.executeSQL (copyProc))
            {
                this.dbg ("create copyProc failed!");
                return false;
            } // if (! executeSQL (copyProc))
        } // if isCreateProcedures

        return true;
    } // createFormTemplateDBTable


    /**
     * Returns if the provided table is a temporary table.
     *
     * @param tableName     the table name
     * @return
     */
    private boolean isTemporaryTable (String tableName)
    {
        return tableName.startsWith (DBMapper.TYPE_TRANSLATION_TEMPORARY_MAPPING_TABLE_PREFIX);
    } // isTemporaryTable


    /**************************************************************************
     * Creates the mapping table for the given form type. <BR/>
     * Both ddl and idx must be initialized StringBuffers. The values are
     * appended to these.
     *
     * @param   tableName   The name of the table.
     * @param   ddl         DDL for the table.
     * @param   idx         The indexes.
     */
    private void getTableHeader (StringBuffer tableName, StringBuffer ddl,
                                 StringBuffer idx)
    {
        // the DDL for ORACLE and MS-SQL is different
        switch (this.p_dbType)
        {
            case SQLConstants.DB_ORACLE:
                ddl
                    .append ("CREATE TABLE /*USER*/").append (tableName).append (" (")
                    .append (" oid RAW (8) NOT NULL,")
                    .append (" typeName VARCHAR2 (63) NOT NULL");

                idx
                    .append ("CREATE UNIQUE INDEX i_").append (tableName)
                    .append ("Oid ON ").append (tableName).append ("(oid) /*TABLESPACE*/;");
                break;

            case SQLConstants.DB_MSSQL:
                ddl
                    .append ("CREATE TABLE ").append (tableName).append (" (")
                    .append (" oid OBJECTID NOT NULL,")
                    .append (" typeName NAME NOT NULL");

                idx
                    .append ("CREATE UNIQUE INDEX i_").append (tableName)
                    .append ("Oid ON ").append (tableName).append ("(oid)");
                break;

            case SQLConstants.DB_DB2:
                ddl
                    .append ("CREATE TABLE ").append (tableName).append (" (")
                    .append (" oid CHAR (8) NOT NULL FOR BIT DATA WITH DEFAULT X'00000000',")
                    .append (" typeName VARCHAR (63) NOT NULL WITH DEFAULT ('UNKNOWN')");

                idx
                    .append ("CREATE UNIQUE INDEX i_").append (tableName)
                    .append ("Oid ON ").append (tableName).append ("(oid ASC);");
                break;

            default:
                IOHelpers.showMessage ("database type " + this.p_dbType + " not supported: ",
                                       this.app, this.sess, this.env);
        } // switch
    } // getTableHeader

    /**************************************************************************
     * Deletes the mapping table and the stored procedures
     * for the given form type. <BR/>
     *
     * @param dataElement   the data element of the form type.
     * @param isDeleteTable    delete the table. If <code>false</code> only the
     *                         stored procedures will be deleted
     *
     * @return  true on success otherwise false.
     */
    public boolean deleteFormTemplateDBTable (DataElement dataElement,
            boolean isDeleteTable)
    {
        return this.deleteFormTemplateDBTable (dataElement,
                null,
                isDeleteTable,
                true);
    } // deleteFormTemplateDBTable

    /**************************************************************************
     * Deletes the mapping table and the stored procedures
     * for the given form type. <BR/>
     *
     * @param   dataElement         the data element of the form type.
     * @param   tableName           The table name for the table to create.
     *                              If no table name is provided the name from
     *                              the data element is taken. 
     * @param   isDeleteTable       delete the table. If <code>false</code> only
     *                              the stored procedures will be deleted
     * @param   isDeleteProcedures  Flag indicating if the stored procedures
     *                              should be deleted.
     *
     * @return  true on success otherwise false.
     */
    public boolean deleteFormTemplateDBTable (DataElement dataElement,
                                              StringBuffer tableName,
                                              boolean isDeleteTable,
                                              boolean isDeleteProcedures)
    {
        StringBuffer tableNameLocal = tableName; // variable for local assignments
        StringBuffer queryStr;          // the query string
        boolean result = true;          // the result of this method

        // do same consistency checks on the data element.
        if (!this.isValidDataElement (dataElement, false))
        {
            return false;
        } // if

        // validate the mapping info
        if (!this.validateMappingInfo (dataElement))
        {
            return false;
        } // if

        // check if a table name has been provided
        if (tableNameLocal == null)
        {
            // get the name of the mapping table
            tableNameLocal = this.getDatabaseTableName (dataElement);
        } // if

        if (tableNameLocal == null)
        {
            return false;
        } // if

        // create the SQL statement to delete the table and the stored
        // procedures:
        switch (this.p_dbType)
        {
            case SQLConstants.DB_ORACLE:

                // delete the mapping table?
                if (isDeleteTable)
                {
                    queryStr = new StringBuffer ()
                        .append ("DROP TABLE ").append (tableNameLocal);
                    this.dbg (queryStr);         // log the statement
                    result = result && this.executeSQL (queryStr);
                } // if (isDeleteTable)

                // delete the stored procedures?
                if (isDeleteProcedures)
                {
                    queryStr = new StringBuffer ()
                        .append ("DROP FUNCTION ").append (this.getCreateProcName (tableNameLocal));
                    this.dbg (queryStr);         // log the statement
                    result = result && this.executeSQL (queryStr);

                    queryStr = new StringBuffer ()
                        .append ("DROP FUNCTION ").append (this.getUpdateProcName (tableNameLocal));
                    this.dbg (queryStr);         // log the statement
                    result = result && this.executeSQL (queryStr);

                    queryStr = new StringBuffer ()
                        .append ("DROP FUNCTION ").append (this.getRetrieveProcName (tableNameLocal));
                    this.dbg (queryStr);         // log the statement
                    result = result && this.executeSQL (queryStr);

                    queryStr = new StringBuffer ()
                        .append ("DROP FUNCTION ").append (this.getCopyProcName (tableNameLocal));
                    this.dbg (queryStr);         // log the statement
                    result = result && this.executeSQL (queryStr);
                } // if (isDeleteProcedures)

                break;

            case SQLConstants.DB_MSSQL:

                queryStr = new StringBuffer ();

                // delete the mapping table?
                if (isDeleteTable)
                {
                    queryStr .append ("DROP TABLE ").append (tableNameLocal);
                } // if (isDeleteTable)

                // delete the stored procedures?
                if (isDeleteProcedures)
                {
                    queryStr.append (" DROP PROCEDURE ").append (
                            this.getCreateProcName (tableNameLocal)).append (
                        " DROP PROCEDURE ").append (
                            this.getUpdateProcName (tableNameLocal)).append (
                        " DROP PROCEDURE ").append (
                            this.getRetrieveProcName (tableNameLocal)).append (
                        " DROP PROCEDURE ").append (
                            this.getCopyProcName (tableNameLocal));
                } // if (isDeleteProcedures)

                // log the statement:
                this.dbg (queryStr);
                // execute the sql statement and return the result:
                result = this.executeSQL (queryStr);
                break;

            case SQLConstants.DB_DB2:
                IOHelpers.showMessage ("database type " + this.p_dbType + " not supported: ",
                                       this.app, this.sess, this.env);

                // delete the mapping table?
                if (isDeleteTable)
                {
                    queryStr = new StringBuffer ()
                        .append ("DROP TABLE ").append (tableNameLocal);
                    this.dbg (queryStr);         // log the statement
                    result = result && this.executeSQL (queryStr);
                } // if (isDeleteTable)

                // delete the stored procedures?
                if (isDeleteProcedures)
                {
                    queryStr = new StringBuffer ()
                        .append ("DROP PROCEDURE ").append (this.getCreateProcName (tableNameLocal));
                    this.dbg (queryStr);         // log the statement
                    result = result && this.executeSQL (queryStr);

                    queryStr = new StringBuffer ()
                        .append ("DROP PROCEDURE ").append (this.getUpdateProcName (tableNameLocal));
                    this.dbg (queryStr);         // log the statement
                    result = result && this.executeSQL (queryStr);

                    queryStr = new StringBuffer ()
                        .append ("DROP PROCEDURE ").append (this.getRetrieveProcName (tableNameLocal));
                    this.dbg (queryStr);         // log the statement
                    result = result && this.executeSQL (queryStr);

                    queryStr = new StringBuffer ()
                        .append ("DROP PROCEDURE ").append (this.getCopyProcName (tableNameLocal));
                    this.dbg (queryStr);         // log the statement
                    result = result && this.executeSQL (queryStr);
                } // if (isDeleteProcedures)

                break;

            default:
                IOHelpers.showMessage ("database type " + this.p_dbType + " not supported: ",
                                       this.app, this.sess, this.env);
        } // switch

        return result;
    } // deleteFormTemplateDBTable



    /**************************************************************************
     * Inserts the attributes of all objects of the given form type in the
     * mapping table. <BR/>
     * This works only for Forms (XMLViewer_01 objects)!!
     *
     * @param dataElement       the data element of the form type.
     * @param isMapSystemValues option to activate the mapping of system values
     *
     * @return  true on success otherwise false.
     */
    public boolean createAllDBEntries (DataElement dataElement,
                                       boolean isMapSystemValues)
    {
        boolean isAllRight = true;
        int objCounter = 0;
        int errorCounter = 0;
        Date startDate;
        int tVersionId;
        Vector<BusinessObjectInfo> objects = null;

        // mark the starting date
        startDate = new Date ();

        this.env.write ("<DIV ALIGN=\"LEFT\"><FONT SIZE=\"2\">");
        this.env.write ("Updating database mapping started at " +
            DateTimeHelpers.dateTimeToString (startDate) + " ..." + IE302.TAG_NEWLINE);

        // do same consistency checks on the data element.
        // the data element must have the oid from the template object
        if (!this.isValidDataElement (dataElement, true))
        {
            return false;
        } // if (!isValidDataElement (dataElement, true))

        // get the tversion id for the type
        tVersionId = this.getTypeCache ().getTVersionId (dataElement.p_typeCode);
        // CONSTRAINT: tversionid must have been found
        if (tVersionId == TypeConstants.TYPE_NOTYPE)
        {
            this.env.write ("<B>ERROR</B>: Could not find corresponding tversionid to typecode '" +
                dataElement.p_typeCode +
                "'. DBMapping aborted!" + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);
            this.env.write ("</FONT></DIV>");
            return false;
        } // if (tversionId == TypeConstants.TYPE_NOTYPE)

        // get the Documenttemplate
        // because it will be the same for all objects
        DocumentTemplate_01 template =
            (DocumentTemplate_01) (this.getTypeCache ()
                    .getType (tVersionId)).getTemplate ();
        // CONSTRAINT: template must have been found
        if (template == null)
        {
            this.env.write ("<B>ERROR</B>: Could not find corresponding documenttemplate to tVersionId '" +
                    tVersionId +
                "'. DBMapping aborted!" + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);
            this.env.write ("</FONT></DIV>");
            return false;
        } // if (template == null)

        // find all form objects with the specific template oid
        objects = BOHelpers.findObjects (new StringBuffer ().append (States.ST_ACTIVE),
            tVersionId, this.env);

        // check if there are some objects for which to recreate the db-mapping:
        if (objects != null && objects.size () > 0)
        {
// PROB KR 20090823: Because xmldata files are not longer existing reading of the file does not make sense.
            // create a pseudo xmlviewer object
            // that will be used to read the xmldata files
            XMLViewer_01 viewer = new XMLViewer_01 ();
            viewer.initObject (OID.getEmptyOid (), this.user, this.env, this.sess, this.app);
            viewer.setDocumentTemplate (template);

            for (Iterator<BusinessObjectInfo> iter = objects.iterator ();
                iter.hasNext ();)
            {
                BusinessObjectInfo objInfo = iter.next ();

                // increment object counter:
                objCounter++;
                // show log message:
                this.env.write ("<LI/>[" + objCounter + "] " +
                    objInfo.p_typeName +
                    " '<A HREF=\"" +
                    IOHelpers.getShowObjectJavaScriptUrl (objInfo.p_oid.toString ()) +
                    "\"><CODE>" + objInfo.p_name +
                    "</CODE>' (<CODE>" + objInfo.p_oid.toString () + "</CODE>)</A> ... ");

                try
                {
                    // instantiate the object:
                    // set the oid in the xmlviewer
                    viewer.oid = objInfo.p_oid;
                    // note that during the retrieve
                    // the mapping will be done
                    viewer.retrieve (Operations.OP_NONE);
                    this.env.write (" initialized and mapped");

                    // mapping of system values must be explicitly
                    // activated. In that case the system values
                    // are taken from the data element and
                    // written into the ibs_object table
                    if (isMapSystemValues)
                    {
                        this.env.write (" + system values ... ");
                        // map the system values of the viewer object
                        if (this.mapSystemValues (viewer))
                        {
                            this.env.write ("mapped");
                        } // if (mapSystemValues (viewer))
                        else    // an error occurred
                        {
                            this.env.write ("<B>Mapping-Error</B>: " +
                                this.getLogObject ().toString ());
                            errorCounter++;
                            isAllRight = false;
                        } // else    // an error occurred
                    }  // if (isMapSystemValues)
                } // try
/*
                    catch (NameAlreadyGivenException e)
                    {
                        errorCounter++;
                        // report the error:
                        this.env.write ("<B>ERROR</B>: "  + e.getMessage () + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);
                        isAllRight = false;
                    } // catch
*/
                catch (NoAccessException e)
                {
                    // report the error:
                    this.env.write ("<B>ERROR</B>: "  + e.getMessage () + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);
                    errorCounter++;
                    isAllRight = false;
                } // catch
                catch (AlreadyDeletedException e)
                {
                    // report the error:
                    this.env.write ("<B>ERROR</B>: "  + e.getMessage () + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);
                    errorCounter++;
                    isAllRight = false;
                } // catch
                catch (ObjectNotFoundException e)
                {
                    // report the error:
                    this.env.write ("<B>ERROR</B>: "  + e.getMessage () + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);
                    errorCounter++;
                    isAllRight = false;
                } // catch
            } // for iter
        } // if

        // show some statistics:
        DIHelpers.showElapsedTime (this.env, "Finished updating database mapping",
            startDate, new Date (), objCounter);
        // any errors found?
        if (errorCounter > 0)
        {
            this.env.write (IE302.TAG_NEWLINE + "<B>WARNING</B>: " + errorCounter +
                " error(s) found!");
        } // if (errorCounter > 0)
        else    // else no errors found
        {
            this.env.write (IE302.TAG_NEWLINE + "No errors found.");
        } // else    // else no errors found
        this.env.write ("<P/></FONT></DIV>");
        return isAllRight;
    } // createAllDBEntries


    /**************************************************************************
     * This methods maps the system values from a dataelement into the
     * ibs_obect table by executing an update statement. <BR/>
     *
     * @param viewer the xmlviewer object with the data element to get
     *                  the system values from
     *
     * @return  <CODE>true</CODE> if the update was successful or
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean mapSystemValues (XMLViewer_01 viewer)
    {
        boolean isShowInNews = viewer.dataElement.getShowInNews ();
        Date validUntil = viewer.dataElement.getValidUntil ();
        boolean notChanged = false;

/* the viewer does not have any values when no retrieve has been used!
        // first find out if any valus changed
        boolean notChanged =
            (viewer.name.equals (viewer.dataElement.getName())) &&
            (viewer.description.equals (viewer.dataElement.getDescription ())) &&
            (viewer.showInNews == isShowInNews) &&
            (viewer.validUntil.equals (validUntil));
*/
        if (notChanged)
        {
            // no changes to make
            return true;
        } // if (notChanged)

        // changes found in system values
        StringBuffer updSql = new StringBuffer ()
            .append (" UPDATE ibs_object")
            .append (" SET name = '")
            .append (viewer.dataElement.getName ())
            .append ("', description = '")
            .append (viewer.dataElement.getDescription ())
            .append ("', validUntil = '")
            .append (SQLHelpers.getDateString (validUntil));

        // note that the showInNews is hidden in the flags of the object
        // TODO BB20060330: this implementation is possibly not database
        // independent!

        if (isShowInNews)
        {
            updSql.append ("', flags = (flags | 4))");
        } // if (isShowInNews)
        else // disable showInNews flag
        {
            updSql.append ("', flags = (flags & (0x7FFFFFFF ^ 4))");
        } // else // disable showInNews flag
        updSql.append (" WHERE oid = ")
            .append (viewer.dataElement.oid.toStringQu ());

        // now execute the update statement
        return this.executeSQL (updSql);
    } // mapSystemValues


    /**************************************************************************
     * Inserts the attributes of all objects of the given form type in the
     * mapping table. <BR/>
     * This works only for Forms (XMLViewer_01 objects)!!
     *
     * @param dataElement       the data element of the form type.
     *
     * @return  true on success otherwise false.
     *
     * @deprecated  ???
     * @deprecated  KR 20090715 This method seems to be never used.
     */
    @Deprecated
    public boolean createAllDBEntries (DataElement dataElement)
    {
        boolean isAllRight = true;
        int objCounter = 0;
        int errorCounter = 0;
        Date startDate;

        // mark the starting date
        startDate = new Date ();

        this.env.write ("<DIV ALIGN=\"LEFT\"><FONT SIZE=\"2\">");
        this.env.write ("Updating database mapping started at " +
            DateTimeHelpers.dateTimeToString (startDate) + " ..." + IE302.TAG_NEWLINE);

        // do same consistency checks on the data element.
        // the data element must have the oid from the template object
        if (!this.isValidDataElement (dataElement, true))
        {
            return false;
        } // if (!isValidDataElement (dataElement, true))

        // find all form objects with the specific template oid:
        Vector<BusinessObjectInfo> objects =
            BOHelpers.findObjects (new StringBuffer () .append (States.ST_ACTIVE),
                null, null,
                new StringBuffer ("o.oid IN")
                    .append (" (SELECT oid")
                    .append (" FROM ibs_XMLViewer_01")
                    .append (" WHERE templateOid = ").append (dataElement.oid.toStringQu ()).append (")"),
                this.env);

        // check if there were any objects found:
        if (objects.size () > 0)
        {
            // read the results set and perform an object mapping:
            for (Iterator<BusinessObjectInfo> iter = objects.iterator (); iter.hasNext ();)
            {
                BusinessObjectInfo objInfo = iter.next ();

                objCounter++;
                this.env.write ("<LI/>[" + objCounter + "] " +
                    objInfo.p_typeName +
                    " '<A HREF=\"" +
                    IOHelpers.getShowObjectJavaScriptUrl (objInfo.p_oid.toString ()) +
                    "\"><CODE>" + objInfo.p_name +
                    "</CODE>' (<CODE>" + objInfo.p_oid.toString () + "</CODE>)</A> ... ");

                XMLViewer_01 viewer = (XMLViewer_01) objInfo.getObject (this.env);

                // check if we got the object
                if (viewer != null)
                {
// PROB KR 20090823: Because xmldata files are not longer existing checking and creating of the file does not make sense.
                    this.env.write (" initialized and mapped.");
                } // If got the object
                else    // did not get the object
                {
                    errorCounter++;
                    // report the error:
                    DIHelpers.showError (this.env, "Object not found.");
                    isAllRight = false;
                } // else did not get the object
            } // for iter
        } // if
        // show some statistics
        DIHelpers.showElapsedTime (this.env, "Finished updating database mapping",
            startDate, new Date (), objCounter);
        // any errors found?
        if (errorCounter > 0)
        {
            this.env.write (IE302.TAG_NEWLINE + "<B>WARNING</B>: " + errorCounter +
                " error(s) found!");
        } // if (errorCounter > 0)
        else    // else no errors found
        {
            this.env.write (IE302.TAG_NEWLINE + "No errors found.");
        } // else    // else no errors found
        this.env.write ("<P/></FONT></DIV>");

        return isAllRight;
    } // createAllDBEntries


    /**************************************************************************
     * Deletes all rows in the mapping table of the given form type. <BR/>
     *
     * @param dataElement   the data element of the form type.
     *
     * @return  true on success otherwise false.
     */
    public boolean deleteAllDBEntries (DataElement dataElement)
    {
        String typeCode = null;         // the type code
        StringBuffer queryStr;          // the query string
        StringBuffer tableName;         // the name of the table

        // do same consistency checks on the data element.
        if (!this.isValidDataElement (dataElement, false))
        {
            return false;
        } // if

        // get the table name from the data element type name
        if ((tableName = this.getDatabaseTableName (dataElement)) == null)
        {
            return false;
        } // if

        typeCode = dataElement.p_typeCode;

        queryStr = new StringBuffer (" DELETE ").append (tableName);
//this.dbg (queryStr);                         // log the statement

        // execute the sql statement:
        if (!this.executeSQL (queryStr))     // there occurred an error?
        {
            // return error code:
            return false;
        } // if there occurred an error

        // delete references from objects of the regarding type:
        queryStr = new StringBuffer ()
            .append (" DELETE ibs_Reference")
            .append (" WHERE referencingOid IN")
            .append (" (SELECT o.oid")
            .append (" FROM ibs_Object o, ibs_Type t, ibs_TVersion tv")
            .append (" WHERE t.code = \'").append (typeCode).append ("\'")
            .append (" AND t.id = tv.typeId")
            .append (" AND tv.id = o.tVersionId")
            .append (")");
//this.dbg (queryStr);                 // log the statement

        // execute the sql statement and return the result:
        return this.executeSQL (queryStr);
    } // deleteAllDBEntries


    /**************************************************************************
     * Inserts the attributes of the given form in the mapping table. <BR/>
     *
     * @param   dataElement The data element of the form.
     *
     * @return  <CODE>true</CODE> on success otherwise <CODE>false</CODE>.
     */
    public boolean createDBEntry (DataElement dataElement)
    {
        // do same consistency checks on the data element.
        if (!this.isValidDataElement (dataElement, true))
        {
            return false;
        } // if

        return this.performMappingAction (DBMapper.MAPPING_ACTION_CREATE,
            dataElement);
    } // createDBEntry


    /**************************************************************************
     * Updates the attributes of the given form in the mapping table. <BR/>
     *
     * @param   dataElement The data element of the form.
     *
     * @return  <CODE>true</CODE> on success otherwise <CODE>false</CODE>.
     */
    public boolean updateDBEntry (DataElement dataElement)
    {
        // do same consistency checks on the data element.
        if (!this.isValidDataElement (dataElement, true))
        {
            return false;
        } // if (!this.isValidDataElement (dataElement, true))

        // valid data element
        return this.performMappingAction (
                DBMapper.MAPPING_ACTION_UPDATE, dataElement);
    } // updateDBEntry


    /**************************************************************************
     * Deletes the row of the given form in the mapping table. <BR/>
     * Not yet used!
     *
     * @param tableName     the name of the mapping table.
     * @param dataElement   the data element of the form.
     *
     * @return  true on success otherwise false.
     */
    public boolean deleteDBEntry (String tableName, DataElement dataElement)
    {
        // do same consistency checks on the data element.
        return this.isValidDataElement (dataElement, true);
    } // deleteDBEntry


    /**************************************************************************
     * Retrieve the attributes of the given form in the mapping table. <BR/>
     *
     * @param   dataElement The data element of the form.
     *
     * @return  <CODE>true</CODE> on success otherwise <CODE>false</CODE>.
     */
    public boolean retrieveDBEntry (DataElement dataElement)
    {
        // do same consistency checks on the data element.
        if (!this.isValidDataElement (dataElement, true))
        {
            return false;
        } // if (! this.isValidDataElement (dataElement, true))

        // valid data element
        return this.performMappingAction (
                DBMapper.MAPPING_ACTION_RETRIEVE, dataElement);
    } // retrieveDBEntry


    /**************************************************************************
     * Returns the name of the mapping table for the given form template. <BR/>
     *
     * @param dataElement   the data element from a template xml file
     *
     * @return  ???
     */
    private StringBuffer getDatabaseTableName (DataElement dataElement)
    {
        if (dataElement == null)
        {
            return null;
        } // if

        return new StringBuffer ().append (dataElement.tableName);
    } // getDatabaseTableName


    /**************************************************************************
     * Compute the key of the field. <BR/>
     *
     * @param   value   The value.
     *
     * @return  The computed key.
     */
    private String computeKey (ValueDataElement value)
    {
        boolean multiSelection = value.multiSelection != null &&
            value.multiSelection.equalsIgnoreCase (DIConstants.ATTRVAL_YES);

        // search the value type in the type table
        String key =
            (((value.type.equals (DIConstants.VTYPE_SELECTIONBOX) ||
               value.type.equals (DIConstants.VTYPE_QUERYSELECTIONBOX)) ||
              value.type.equals (DIConstants.VTYPE_VALUEDOMAIN) &&
              multiSelection) ? DBMapper.MULTISELECTION_PREFIX : "") +
            value.type;

        // return the result:
        return key;
    } // computeKey


    /**************************************************************************
     * Returns the database attribute type for the given value. <BR/>
     *
     * @param value         the value
     *
     * @return  ???
     */
    private String getDatabaseFieldType (ValueDataElement value)
    {
        int size = DBMapper.VALUETYPETABLE.length;
        String retVal = null;
        String key = this.computeKey (value);

        for (int i = 0; i < size; i++)
        {
            if (key.equals (DBMapper.VALUETYPETABLE[i]))
            {
                // get the attribute type.
                retVal = this.p_typeTable[i];
            } // type found in the table
        } // for i

        // return the result:
        return retVal;
    } // getDatabaseFieldType


    /**************************************************************************
     * Returns the parameter type for the given value. <BR/>
     *
     * @param value         the value
     *
     * @return  ???
     */
    private short getParamType (ValueDataElement value)
    {
        int size = DBMapper.VALUETYPETABLE.length;
        String key = this.computeKey (value);

        // search the value type in the type table
        for (int i = 0; i < size; i++)
        {
            if (key.equals (DBMapper.VALUETYPETABLE[i]))
            {
                return DBMapper.PARAMTYPETABLE[i];
            } // type found in the table
        } // for i

        // report an error
        return ParameterConstants.TYPE_UNDEFINED;
    } // getParamType


    /**************************************************************************
     * Writes a error message in the log object. <BR/>
     *
     * @param   msg     the error msg
     */
    private void error (String msg)
    {
        this.logObject.add (DIConstants.LOG_ERROR, msg);
        this.dbg (msg);
    } // error


    /**************************************************************************
     * Writes a warning in the log object. <BR/>
     *
     * @param   msg     the warning
     */
/*
    private void warning (String msg)
    {
        this.logObject.add (DIConstants.LOG_WARNING, msg);
        dbg (msg);
    } // error
*/


    /**************************************************************************
     * Writes a message in the log object. <BR/>
     *
     * @param   msg     the message
     */
/*
    private void log (String msg)
    {
        this.logObject.add (DIConstants.LOG_ENTRY, msg);
        dbg (msg);
    } // log
*/


    /**************************************************************************
     * Executes a action query on the database. <BR/>
     *
     * @param   sql     The action query.
     *
     * @return  <CODE>true</CODE>, if the query was performed successfully,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean executeSQL (StringBuffer sql)
    {
        // the action object used to access the database
        SQLAction action = null;
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            action.execute (sql, true);
            // end action:
            action.end ();
        } // try
        catch (DBError e)
        {
            // get error message:
            this.error (e.getMessage ());
            return false;
        } // catch
        finally
        {
            try
            {
                action.end ();
            } // try
            catch (DBError e)
            {
                // TODO: handle the exception
            } // catch

            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally
        return true;
    } // executeSQL


    /**************************************************************************
     * Performs a action on the mapping table for the given data element. <BR/>
     *
     * Possible actions are:
     *  - create:       creates a new row in the mapping table.
     *  - update:       updates a existing row in the mapping table.
     *  - retrieve:     retrieves the attributes of a existing row.
     *
     * @param dbAction      the action to perform
     * @param dataElement   the data element with holds/receive the values
     *
     * @return  ???
     */
    private boolean performMappingAction (int dbAction, DataElement dataElement)
    {
        // the name of the dbmapper table
        StringBuffer tableName = null;
        // the name of the stored procedure to execute
        StringBuffer procName = null;
        // set the direction for the parameters:
        short paramDirection = 0;
        short paramType = 0;
        // holds the protocol id of the newest entry
        String protocolId = null;

        // BB: in case this data element has no dbtable we can not perform
        // a mapping action
        // is that valid? Yes, because there can be formtemplates
        // that do only consist of a QUERY field that does not require a DBFIELD
        // and therefore also do not have DBTABLE
        // This needs to be checked when the formtemplate is created. 
        // TODO: Check if that can lead to inconsistencies
        if (dataElement.tableName == null || dataElement.tableName.length() == 0)
        {
        	return true;
        } // if (dataElement.tableName != null && ("").equals (dataElement.tableName))        
        
        // validate the mapping info:
        if (!this.validateMappingInfo (dataElement, true))
        {
            return false;
        } // if (!this.validateMappingInfo (dataElement))

        // get the name of the mapping table:
        tableName = this.getDatabaseTableName (dataElement);
        // CONTRAINT: The name must not be null
        if (tableName == null)
        {
            return false;
        } // if (tableName == null)

        // initialize the parameter list:
        Vector<DBMapper.ValueParam> params =
            new Vector<DBMapper.ValueParam> (dataElement.values.size ());

        // determine the parameter direction and the stored procedure name
        switch (dbAction)
        {
            case DBMapper.MAPPING_ACTION_CREATE:
                paramDirection = ParameterConstants.DIRECTION_IN;
                // set the name of the stored procedure:
                procName = this.getCreateProcName (tableName);
                break;

            case DBMapper.MAPPING_ACTION_UPDATE:
                paramDirection = ParameterConstants.DIRECTION_IN;
                // set the name of the stored procedure:
                procName = this.getUpdateProcName (tableName);
                break;

            case DBMapper.MAPPING_ACTION_RETRIEVE:
                paramDirection = ParameterConstants.DIRECTION_OUT;
                // set the name of the stored procedure:
                procName = this.getRetrieveProcName (tableName);
                break;

            default:
                // unknown action!!
                this.error ("Unknown mapping action.");
                return false;
        } // switch (dbAction)
        StoredProcedure sp = new StoredProcedure (
            procName.toString (), StoredProcedureConstants.RETURN_VALUE);


        // the first parameter is always the oid and is always an inparameter
        // the oid is passed as string
        BOHelpers.addInParameter (sp, dataElement.oid);
        // set the typeName
        // set the value for the typeName parameters when it is an inparameter
        if (paramDirection == ParameterConstants.DIRECTION_IN)
        {
            sp.addInParameter (
                ParameterConstants.TYPE_STRING, dataElement.p_typeCode);
        } // if
        else
        {
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        } // else

        // for UPDATE actions get the protocol id of the newest entry for this
        // object and the current user:
        if (dbAction == DBMapper.MAPPING_ACTION_UPDATE)
        {
            protocolId = this.getNewestProtocollIdForObject (
                dataElement.oid, this.env.getUserInfo ().getUser ().id);
        } // if
  
        // large text attributes are not handled by the stored procedures.
        // this attributes must be updated separately after the stored
        // procedure call.
        // a vector is used to store the attribute name and value
        // of large text attributes.
        Vector<ValueDataElement> textVector = new Vector<ValueDataElement> ();

        // specific reminder fields are not handled by the change and retrieve stored procedures.
        // those fields must be updated and retrieved separately after the stored procedure call.
        // a vector is used to store the value data elemnents of all reminder fields.
        Vector<ValueDataElement> reminderFieldsVector = new Vector<ValueDataElement> ();

        // get the values from the data element and
        // loop trough the value elements
        for (Iterator<ValueDataElement> iter = dataElement.values.iterator (); iter.hasNext ();)
        {
            // get the next value:
            ValueDataElement value = iter.next ();

            // skip the not mapped fields:
            // INFO KR 20090730: A non-existing mappingField is not possible
            //                   because the mappingField must be set.
            if (value.mappingField != null)
            {
                // check if it is a large text field type because
                // large text fields cannot be handled with a stored procedure
                // this fields are handled separately.
                //
                // Note that these fields are not added as parameter
                // but influence the size of the parameters array!
                if (this.isTextField (value))
                {
                    // store the value into the large text vector.
                    textVector.addElement (value);
                } // if (fieldType.equals ("TEXT") || fieldType.equals ("CLOB"))
                else    // not a TEXT value
                {
                    // all other attributes are passed to the stored procedure

                    // special case: retr stored procedures have
                    // OBJECTID as OUTPUT paramter and not OBJECTIDSTRING
                    // Standard treatment would map the OID fields to
                    // a String parameter that leads to an error
                    if (dbAction == DBMapper.MAPPING_ACTION_RETRIEVE &&
                        this.isOidField (value))
                    {
                        // set the correct parameter type for oid fields
                        paramType = ParameterConstants.TYPE_VARBYTE;
                    } // if (dbAction == DBMapper.MAPPING_ACTION_RETRIEVE ...
                    else // standard treatment
                    {
                        // determinate the correct parameter type for the value
                        paramType = this.getParamType (value);
                        // check if the value type is mappable
                    } // else standard treatment

                    if (paramType == ParameterConstants.TYPE_UNDEFINED)
                    {
                        this.error (MultilingualTextProvider.getMessage (ServiceMessages.MSG_BUNDLE,
                                ServiceMessages.MSG_MAPPING_NOTPOSSIBLE, env) +
                                " Field=" + value.field + ", Value=" + value.value);
                        return false;
                    } // if invalid type
                    
                    // ensure a correct OID for oid fields is set
                    // It happened that an invalid string containing a \n
                    // has been set
                    // in case the oid is not correct it gets deleted
                    if (isOidField (value))
                    {
                    	try
                    	{
                    		value.value = (new OID (value.value)).toString();
                    	} // try
                    	catch (IncorrectOidException e)
                    	{
/*                    		
                            this.error ("performMappingAction: invalid OID setting: " + 
                            		value.value + " in field " + value.field);                            
                            return false;
*/
                    		// delete the value
                    		value.value = "";
                    	} // catch (IncorrectOidException e)
                    } // if (isOidField (value))
                    
                    Parameter param = new Parameter (value.field, paramType,
                        paramDirection);
                    // set the value for IN parameters
                    if (paramDirection == ParameterConstants.DIRECTION_IN)
                    {
                        if (!this.setParamValue (param, dataElement, value))
                        {
                            this.error ("performMappingAction: could not set " +
                                "parameter for value '" + value.field + "'");
                            return false;
                        } // if (!this.setParamValue (param, dataElement, value))
                    } // if (paramDirection == ParameterConstants.DIRECTION_IN)

                    // set the parameter in the array:
                    params.addElement (new ValueParam (value, sp.addParameter (param)));

                    if (value.type.equals (DIConstants.VTYPE_REMINDER))
                    {
                        reminderFieldsVector.add (value);
                    } // if (value.type.equals (DIConstants.VTYPE_REMINDER))
                } // else if (largeTextField)

                // Check if the value has changed and insert a protocol entry if so
                // this is only neccessary for update actions
                if (dbAction == DBMapper.MAPPING_ACTION_UPDATE && value.isChanged ())
                {
                    this.insertProtocolEntry (protocolId, value);
                } // if                        
            } // if (mappingField != null
        } // for iter

        // now execute the stored procedure
        try
        {
            // perform the function call:
            int retVal = BOHelpers.performCallFunctionData (sp, this.env);
            this.dbg ("mapping action " + procName + "(): result " + retVal);

            // procedure successfully completed?
            if (retVal != UtilConstants.QRY_OK)
            {
                this.error ("performMappingAction: stored procedure '" + procName.toString () +
                        "' returned error code: " + retVal);
                return false;
            } // if (retVal != 1)

            // in case of the retrieve mapping action the output parameters
            // must be read into the dataElement
            if (dbAction == DBMapper.MAPPING_ACTION_RETRIEVE)
            {
                // read the fields from the parameters
                if (!this.performRetrieveFields (params))
                {
                    this.error ("performMappingAction: Could not retrieve " +
                        "the fields!");
                    return false;
                } // if (! performRetrieveFields (dataElement, params))
                // now retrieve the large text attributes
                // that must be handled specifically
                if (!this.performRetrieveTextFields (tableName,
                        dataElement.oid, textVector))
                {
                    this.error ("performMappingAction: Could not retrieve " +
                        "large text fields!");
                    return false;
                } // if (! performChangeTextField (tableName.toString (),...

                // now retrieve the specific reminder fields
                // that must be handled specifically
                if (!this.performRetrieveReminderFields (tableName,
                        dataElement.oid, reminderFieldsVector))
                {
                    this.error ("performMappingAction: Could not retrieve " +
                        "specific reminder fields!");
                    return false;
                } // if (! performChangeTextField (tableName.toString (),...
            } // if (dbAction == DBMapper.MAPPING_ACTION_RETRIEVE)
            // in case of the UPDATE or CREATE mapping action the large text
            // fields must be handled
            else if (dbAction == DBMapper.MAPPING_ACTION_UPDATE ||
                     dbAction == DBMapper.MAPPING_ACTION_CREATE)
            {
                // update the large text fields
                // that must be handled specifically
                if (!this.performChangeTextFields (tableName, dataElement.oid,
                        textVector))
                {
                    this.error ("performMappingAction: Could not change " +
                        "large text fields!");
                    return false;
                } // if (! performChangeTextField (tableName.toString (),...

                // update the specific reminder fields
                // that must be handled specifically                
                if (!this.performChangeReminderFields (tableName, dataElement.oid,
                        reminderFieldsVector))
                {
                    this.error ("performMappingAction: Could not change " +
                        "specific reminder fields!");
                    return false;
                } // if (! performChangeTextField (tableName.toString (),...
            } // else if (dbAction == DBMapper.MAPPING_ACTION_UPDATE)

            // return true to indicate that everything went ok
            return true;
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            this.error (procName + "(): " + e.getMessage ());
            this.dbg ("mapping action " + procName + "(): " + e.getMessage ());
            return false;
        } // catch (NoAccessException e)
        catch (Exception e)     // other exceptions
        {
            this.error (procName + "(): " + e.getMessage ());
            this.dbg ("mapping action " + procName + "(): " + e.getMessage ());
            return false;
        } // catch (Exception e)
    } // performMappingAction


    /**************************************************************************
     * Retrieve the fields from the given output parameters. <BR/>
     *
     * @param   params      The parameters to be read.
     *
     * @return  <code>true</code> if everything was successful
     *          or <code>false</code> otherwise.
     */
    private boolean performRetrieveFields (Vector<DBMapper.ValueParam> params)
    {
        // loop through the fields:
        for (Iterator<DBMapper.ValueParam> iter = params.iterator (); iter.hasNext ();)
        {
            DBMapper.ValueParam valueParam = iter.next ();

            // set the value from the parameter
            if (!this.getParamValue (valueParam))
            {
                this.error (
                    "performRetrieveFields: Could not read the parameter for '" +
                    valueParam.p_value.field + "'");
                return false;
            } // if (! getParamValue (param [paramsIndex++], value))
        } // for iter

        // return true to indicate everything was ok
        return true;
    } // performRetrieveFields


    /**************************************************************************
     * Process the large text attributes that must be handled specifically.<BR/>
     *
     * @param dbAction        the mapping action to perform
     * @param textVector    the vector holding the large text fields
     * @param dataElement    the dataElement that holds the data
     * @param tableName     the name of the mapping table
     *
     * @return  <code>true</code> if everything was successfully
     *              or <code>false</code> otherwise
     *
     * @deprecated  ???
     * @deprecated  KR 20090715 This method is never used.
     */
/*
    @Deprecated
    private boolean processTextFields (int dbAction,
                                       Vector<ValueDataElement> textVector,
                                       DataElement dataElement,
                                       String tableName)
    {
        String resultString;

        // any text fields set?
        if (textVector != null)
        {
            for (int i = 0; i < textVector.size (); i++)
            {
                ValueDataElement value = textVector.elementAt (i);

                switch (dbAction)
                {
                    case DBMapper.MAPPING_ACTION_CREATE:
                        // no action needed
                    case DBMapper.MAPPING_ACTION_UPDATE:
                        // store the large text field
                        if (!this.performChangeTextField (tableName,
                                dataElement.oid, value.mappingField, value.value))
                        {
                            return false;
                        } // if (! performChangeTextField (tableName.toString (),...
                        break;
                    case DBMapper.MAPPING_ACTION_RETRIEVE:
                        // get the large text string
                        resultString = this.performRetrieveTextField (tableName,
                                dataElement.oid, value.mappingField);
                        // did we get any string?
                        if (resultString != null)
                        {
                            // set the text value
                            value.value = resultString;
                        } // if (resultString != null)
                        else    // did not get any value
                        {
                            return false;
                        } // else    // did not get any value
                        break;
                    default:
                    // nothing to do
                } // switch (action)
            } // for (int i = 0; i < textVector.size (); i++)
        } // if (textVector != null)
        return true;
    } // processTextFields
*/


    /**************************************************************************
     * Sets the value for a TEXT (CLOB) field in the mapping table. <BR/>
     *
     * @param tableName     the name of the mapping table
     * @param objectOid     the oid of the object
     * @param attribute     the name of the attribute in the table
     * @param value         the value to set
     *
     * @return  <code>true</code> if the value could have been changed
     *          successfully or <code>false</code> otherwise
     *
     * @deprecated  ???
     * @deprecated  KR 20090715 This method is never used.
     */
/*
    @Deprecated
    private boolean performChangeTextField (String tableName, OID objectOid,
                                            String attribute, String value)
    {

        StringBuffer queryStr;          // the query string
        SQLAction action = null;        // the action object used to access the
                                        // database

// TODO BB20070823: obviously this code has never been completed?
//
//        if (this.p_isDBMSOracle)
//        {
/ *
CREATE OR REPLACE FUNCTION p_setAttribute
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_tableName            VARCHAR2,
    ai_attrName             VARCHAR2,
    ai_attrValue            CLOB
    -- output parameters:
)
RETURN INTEGER
* /

/ *

            Parameter[] params = new Parameter[4];
            // 1. parameter: the oid
            params[0] = new Parameter ("oid_s",
                                        ParameterConstants.TYPE_STRING,
                                        ParameterConstants.DIRECTION_IN);
            params[0].setValue (objectOid.toString ());

            // 2. parameter: the table name
            params[1] = new Parameter ("tableName",
                                        ParameterConstants.TYPE_STRING,
                                        ParameterConstants.DIRECTION_IN);
            params[1].setValue (tableName);

            // 3. parameter: the attribute name
            params[2] = new Parameter ("attrName",
                                        ParameterConstants.TYPE_STRING,
                                        ParameterConstants.DIRECTION_IN);
            params[2].setValue (attribute);

            // 4. parameter: the CLOB value
            params[3] = new Parameter ("attrValue",
                                        ParameterConstants.TYPE_TEXT,
                                        ParameterConstants.DIRECTION_IN);
            params[3].setValue (value);

            try
            {
                // perform the function call:
                int retVal = performCallFunctionData ("p_setAttribute", params, 3);
                return (retVal == 1);
            } // try
            catch (NoAccessException e)
            {
                dbg ("performChangeTextField: NoAccessException!");
                return false;
            } // catch
* /
//        } // if (this.p_isDBMSOracle)
//        else
//        {


        // create the SQL String to update 'attribute' in 'tableName' to 'value'
        queryStr = new StringBuffer ()
            .append ("UPDATE ").append (tableName)
            .append (" SET ").append (attribute).append (" = '")
            .append (SQLHelpers.asciiToDb (value)).append ("'")
            .append (" WHERE oid = ").append (objectOid.toStringQu ());

        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            action.execute (queryStr, true);
            // end action:
            action.end ();
            return true;
        } // try
        catch (DBError e)
        {
            // get all errors (can be chained):
            String allErrors = "";
            String h = new String (e.getMessage ());
            h += e.getError ();
            // loop through all errors and concatenate them to a String:
            while (h != null)
            {
                allErrors += h;
                h = e.getError ();
            } // while
            // show the message
            this.error (allErrors);
            return false;
        } // catch
        finally
        {
            try
            {
                action.end ();
            } // try
            catch (DBError e)
            {
                error (e.toString ());
            } // catch
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally
//        } // else if (this.p_isDBMSOracle)
    } // performChangeTextField
*/

    /**************************************************************************
     * Get the value for a TEXT (CLOB) field in the mapping table. <BR/>
     *
     * @param tableName     the name of the mapping table
     * @param objectOid     the oid of the object
     * @param attribute     the name of the attribute in the table
     *
     * @return the value from the text field or <code>null</code> otherweise
     *
     * @deprecated  ???
     * @deprecated  KR 20090715 This method is never used.
     */
/*
    @Deprecated
    private String performRetrieveTextField (String tableName,
                                             OID objectOid,
                                             String attribute)
    {
        String resultString = null;
        StringBuffer queryStr;          // the query string
        int rowCount;
        SQLAction action = null;        // the action object used to access the
                                        // database

        // create the SQL String to update 'attribute' in 'tableName' to 'value'
        queryStr = new StringBuffer ()
            .append ("SELECT ").append (attribute)
            .append (" FROM ").append (tableName)
            .append (" WHERE oid = ").append (objectOid.toStringQu ());

        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, true);

            if (rowCount > 0)
            {
                resultString = action.getString (attribute);
                if (resultString != null && resultString.length () > 0)
                {
                    // convert the value
                    resultString = SQLHelpers.dbToAscii (resultString);
                } // if (resultString != null && resultString.length () > 0)
            } // if (rowCount > 0)
            // end action:
            action.end ();
        } // try
        catch (DBError e)
        {
            // get all errors (can be chained):
            String allErrors = "";
            String h = new String (e.getMessage ());
            h += e.getError ();
            // loop through all errors and concatenate them to a String:
            while (h != null)
            {
                allErrors += h;
                h = e.getError ();
            } // while
            // show the message
            this.error (allErrors);
        } // catch
        finally
        {
            try
            {
                action.end ();
            } // try
            catch (DBError e)
            {
                this.error (e.toString ());
            } // catch
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        // return the result
        return resultString;
    } // performRetrieveTextField
*/


    /**************************************************************************
     * Sets the value for all TEXT or CLOB field in the mapping table. <BR/>
     *
     * @param tableName     the name of the mapping table
     * @param objectOid     the oid of the object
     * @param textValues    a vector containting the large text value data elements
     *
     * @return  <code>true</code> if the value could have been changed
     *             successfully or <code>false</code> otherwise
     */
    private boolean performChangeTextFields (StringBuffer tableName,
                                             OID objectOid,
                                             Vector<ValueDataElement> textValues)
    {
        SQLAction action = null;        // the action object used to access the
                                        // database

        // CONTRAINT: the textValues vector must not be empty
        if (textValues == null || textValues.size () == 0)
        {
            return true;
        } // if (textValues == null || textValues.size () == 0)

        // Create the update statement to update 'attribute' in 'tableName' to 'value'
        UpdateStatement stmt = new UpdateStatement (tableName.toString (),
                null, "oid = " + objectOid.toStringQu ());

        // loop through the text values
        for (int i = 0; i < textValues.size (); i++)
        {
            ValueDataElement value = textValues.elementAt (i);
            
            stmt.addUnicodeTextToSet (value.mappingField, value.value);
        } // for
               
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            stmt.execute (action);
            // end action:
            action.end ();
            // return true to indicate successfull completion
            return true;
        } // try
        catch (DBError e)
        {
            // get all errors (can be chained):
            String allErrors = "";
            String h = new String (e.getMessage ());
            h += e.getError ();
            // loop through all errors and concatenate them to a String:
            while (h != null)
            {
                allErrors += h;
                h = e.getError ();
            } // while
            // show the message
            this.error (allErrors);
            return false;
        } // catch
        finally
        {
            try
            {
                action.end ();
            } // try
            catch (DBError e)
            {
                this.error (e.toString ());
            } // catch

            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally
    } // performChangeTextFields


    /**************************************************************************
     * Sets the values for all provided REMINDER fields. <BR/>
     *
     * @param tableName             the name of the mapping table
     * @param objectOid             the oid of the object
     * @param reminderFieldsVector  a vector containting the reminder value data elements
     *
     * @return  <code>true</code> if the values could have been changed
     *             successfully or <code>false</code> otherwise
     */
    private boolean performChangeReminderFields (StringBuffer tableName,
                                             OID objectOid,
                                             Vector<ValueDataElement> reminderFieldsVector)
    {
        SQLAction action = null;        // the action object used to access the
                                        // database

        // CONTRAINT: the reminder vector must not be empty
        if (reminderFieldsVector == null || reminderFieldsVector.size () == 0)
        {
            return true;
        } // if (textValues == null || textValues.size () == 0)

        try
        {
            // loop through the text values
            for (int i = 0; i < reminderFieldsVector.size (); i++)
            {
                ValueDataElement value = reminderFieldsVector.elementAt (i);

                // open db connection
                action = this.getDBConnection ();
                
                // Create the update statement to update 'attribute' in 'tableName' to 'value'
                UpdateStatement stmt = new UpdateStatement ("ibs_Reminder",
                        null,
                        "oid = " + objectOid.toStringQu () + " AND fieldDbName = " + SQLHelpers.getUnicodeString (value.mappingField));
                
                stmt.extendSet ("reminderDate", (value.value != null && !value.value.trim ().isEmpty ()) ?
                        SQLHelpers.getDateString (value.value).toString () : "null");
                stmt.extendSet ("remind1Days", Integer.toString (value.p_remind1Days));
                stmt.addUnicodeStringToSet ("remind1Text", value.p_remind1Text);
                stmt.addUnicodeStringToSet ("remind1Recip", value.p_remind1Recip);
                stmt.extendSet ("remind2Days", Integer.toString (value.p_remind2Days));
                stmt.addUnicodeStringToSet ("remind2Text", value.p_remind2Text);
                stmt.addUnicodeStringToSet ("remind2Recip", value.p_remind2Recip);
                stmt.extendSet ("escalateDays", Integer.toString (value.p_escalateDays));
                stmt.addUnicodeStringToSet ("escalateText", value.p_escalateText);
                stmt.addUnicodeStringToSet ("escalateRecip", value.p_escalateRecip);

                stmt.execute (action);
                // end action:
                action.end ();

                // close db connection in every case
                this.releaseDBConnection (action);
            } // for

            // return true to indicate successfull completion
            return true;
        } // try
        catch (DBError e)
        {
            // show the message
            this.error (e.getMessage ());

            try
            {
                action.end ();
            } // try
            catch (DBError e1)
            {
                this.error (e1.getMessage ());
            } // catch

            // close db connection in every case
            this.releaseDBConnection (action);
            
            return false;
        } // catch
    } // performChangeReminderFields


    /**************************************************************************
     * Retrieve the value for all TEXT or CLOB field from the mapping table. <BR/>
     *
     * @param tableName     the name of the mapping table
     * @param objectOid     the oid of the object
     * @param textValues    a vector containting the large text value data elements
     *
     * @return  <code>true</code> if the value could have been retrieved
     *             successfully or <code>false</code> otherwise
     */
    private boolean performRetrieveTextFields (StringBuffer tableName,
                                              OID objectOid,
                                              Vector<ValueDataElement> textValues)
    {
        String str = null;
        StringBuffer queryStr;          // the query string
        int rowCount;
        SQLAction action = null;        // the action object used to access the
                                        // database


        // CONTRAINT: the textValues vector must not be empty
        if (textValues == null || textValues.size () == 0)
        {
            return true;
        } // if (textValues == null || textValues.size () == 0)

        // create the SQL String to update 'attribute' in 'tableName' to 'value'
        queryStr = new StringBuffer ("SELECT ");

        // loop through the text values
        for (int i = 0; i < textValues.size (); i++)
        {
            ValueDataElement value = textValues.elementAt (i);
            // add a comma?
            if (i > 0)
            {
                queryStr.append (",");
            } // if (i > 0)
            queryStr.append (value.mappingField);
        } // for (int i = 0; i < textValues.size (); i++)

        queryStr.append (" FROM ").append (tableName)
            .append (" WHERE oid = ").append (objectOid.toStringQu ());


        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);

            if (rowCount != 0)
            {
                // loop through the text values
                for (int i = 0; i < textValues.size (); i++)
                {
                    ValueDataElement value = textValues.elementAt (i);
                    // get the mapped field
                    str = action.getString (value.mappingField);
                    if (str != null && str.length () > 0)
                    {
                        // convert the value
                        str = SQLHelpers.dbToAscii (str);
                    } // if (resultString != null && resultString.length () > 0)
                    value.value = str;
                } // for (int i = 0; i < textValues.size (); i++)
            } // if (rowCount > 0)
            // end action:
            action.end ();
            // return true to indicate successfull completion
            return true;
        } // try
        catch (DBError e)
        {
            // get all errors (can be chained):
            String allErrors = "";
            String h = new String (e.getMessage ());
            h += e.getError ();
            // loop through all errors and concatenate them to a String:
            while (h != null)
            {
                allErrors += h;
                h = e.getError ();
            } // while
            // show the message
            this.error (allErrors);
            return false;
        } // catch
        finally
        {
            try
            {
                action.end ();
            } // try
            catch (DBError e)
            {
                this.error (e.toString ());
            } // catch

            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

    } // performRetrieveTextFields


    /**************************************************************************
     * Retrieves the values for all REMINDER fields from the reminder table. <BR/>
     *
     * @param tableName             the name of the mapping table
     * @param objectOid             the oid of the object
     * @param reminderFieldsVector  a vector containting the reminder value data elements
     *
     * @return  <code>true</code> if the values could have been retrieved
     *             successfully or <code>false</code> otherwise
     */
    private boolean performRetrieveReminderFields (StringBuffer tableName,
                                              OID objectOid,
                                              Vector<ValueDataElement> reminderFieldsVector)
    {
        SelectQuery query;              // the query
        int rowCount;
        SQLAction action = null;        // the action object used to access the
                                        // database

        // map holding all value data elements by db field name
        Map <String, ValueDataElement> fieldNamesMap = new HashMap <String, ValueDataElement> ();

        // CONTRAINT: the textValues vector must not be empty
        if (reminderFieldsVector == null || reminderFieldsVector.size () == 0)
        {
            return true;
        } // if (textValues == null || textValues.size () == 0)

        // create the field name filter for the select query
        StringBuffer fieldNamesFilter = new StringBuffer ();
        
        // loop through the text values
        for (int i = 0; i < reminderFieldsVector.size (); i++)
        {
            ValueDataElement value = reminderFieldsVector.elementAt (i);
            // add a comma?
            if (i > 0)
            {
                fieldNamesFilter.append (",");
            } // if (i > 0)
            fieldNamesFilter.append (SQLHelpers.getUnicodeString (value.mappingField));
            
            fieldNamesMap.put (value.mappingField, value);
        } // for (int i = 0; i < textValues.size (); i++)

        // get the data out of the database:
        query = new SelectQuery (
                // do not retrieve the reminderDate field since this value is read from the type specific table
                new StringBuilder ("fieldDbName, remind1Days, remind1Text, remind1Recip, remind2Days, remind2Text, ").
                    append ("remind2Recip, escalateDays, escalateText, escalateRecip"),
                new StringBuilder ("ibs_Reminder"),
                new StringBuilder ("oid = ").append (objectOid.toStringBuilderQu ()).
                    append (" AND fieldDbName in (").append (fieldNamesFilter).append (")"), null, null, null);

        try
        {
            // open db connection
            action = this.getDBConnection ();
            rowCount = action.execute (query);

            // check if the resultset is not empty:
            if (rowCount > 0)
            {
                // get the data:
                while (!action.getEOF ())
                {
                    // retrieve the field name
                    String fieldDbName = action.getString ("fieldDbName");
                    
                    // retrieve the value data elment belonging to this fieldname
                    ValueDataElement vde = fieldNamesMap.get (fieldDbName);
                    
                    // set the values:
                    vde.p_remind1Days = action.getInt ("remind1Days");
                    vde.p_remind1Text = action.getString ("remind1Text");
                    vde.p_remind1Recip = action.getString ("remind1Recip");
                    vde.p_remind2Days = action.getInt ("remind2Days");
                    vde.p_remind2Text = action.getString ("remind2Text");
                    vde.p_remind2Recip = action.getString ("remind2Recip");
                    vde.p_escalateDays = action.getInt ("escalateDays");
                    vde.p_escalateText = action.getString ("escalateText");
                    vde.p_escalateRecip = action.getString ("escalateRecip");

                    // go to the next result tuple:
                    action.next ();
                } // while (!action.getEOF())
            } // if (rowCount > 0)

            // end action:
            action.end ();

            // return true to indicate successfull completion
            return true;
        } // try
        catch (DBError e)
        {
            // show the message
            this.error (e.getMessage ());
            return false;
        } // catch
        finally
        {
            try
            {
                action.end ();
            } // try
            catch (DBError e)
            {
                this.error (e.getMessage ());
            } // catch

            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally
    } // performRetrieveReminderFields


    /**************************************************************************
     * Sets the value for the given parameter. <BR/>
     *
     * @param param         the parameter for the stored procedure
     * @param dataElement   the data element
     * @param value         the value to be set
     *
     * @return  <code>true</code> if everything went ok or
     *             <code>false</code> otherwise
     */
    private boolean setParamValue (Parameter param, DataElement dataElement,
                                   ValueDataElement value)
    {
        // the assignment of the values seems to be a litle bit complicated.
        // we use the conversion methods from the data element class
        // to get consistent results for the mapping table.
        try
        {
            // check the parameter type
            switch (param.getDataType ())
            {
                case ParameterConstants.TYPE_STRING:
                    String tempStr = dataElement.getImportStringValue (value.field);

                    if (value.type.equals (DIConstants.VTYPE_PASSWORD))
                                        // is the value a password ?
                    {
                        // encrypt the password:
                        tempStr = EncryptionManager.encrypt (tempStr);
                    } // if is the value a password

                    param.setValue (tempStr);
                    break;

                case ParameterConstants.TYPE_INTEGER:
                    param.setValue (dataElement.getImportIntValue (value.field));
                    break;

                case ParameterConstants.TYPE_DOUBLE:
                    double dval = dataElement.getImportDoubleValue (value.field);
                    this.dbg ("setParamValue: DOUBLE " + dval);
                    param.setValue (dval);
                    break;

                case ParameterConstants.TYPE_CURRENCY:
                    String s = dataElement.getImportStringValue (value.field);
                    long lval = Helpers.stringToMoney (s);
                    if (lval == -1)
                    {
                        lval = 0;
                    } // if
                    param.setValue (lval);

                    break;
                case ParameterConstants.TYPE_BOOLEAN:
                    param.setValue (dataElement.getImportBooleanValue (value.field));
                    break;

                case ParameterConstants.TYPE_DATE:
                    if (value.type.equals (DIConstants.VTYPE_DATE) ||
                        value.type.equals (DIConstants.VTYPE_REMINDER))
                    {
                        param.setValue (dataElement.getImportDateValue (value.field));
                    } // if
                    else if (value.type.equals (DIConstants.VTYPE_TIME))
                    {
                        param.setValue (dataElement.getImportTimeValue (value.field));
                    } // else if
                    else if (value.type.equals (DIConstants.VTYPE_DATETIME))
                    {
                        param.setValue (dataElement.getImportDateTimeValue (value.field));
                    } // else if
                    break;

                default:
                    this.error ("setParamValue: Invalid type for parameter " + param.getName ());
                    return false;
            } // switch paramType
        } // try
        catch (NumberFormatException e)
        {
            // invalid numbers are ignored an set to zero.
            this.dbg ("setParamValue: Invalid value for parameter " + param.getName ());

            switch (param.getDataType ())
            {
                case ParameterConstants.TYPE_INTEGER:
                    param.setValue (0);
                    break;

                case ParameterConstants.TYPE_DOUBLE:
                    param.setValue ((float) 0.0);
                    break;

                default: // nothing to do
            } // switch
            return true;
        } // catch NumberFormatException
        catch (Exception e)
        {
            this.error ("setParamValue: Invalid value for parameter " + param.getName ());
            return false;
        } // catch
        return true;
    } // setParamValue


    /***************************************************************************
     * Get the value for the given parameter and set it in the valueDataElement.
     * <BR/>
     *
     * @param   valueParam  The valueDataElement and the parameter.
     *
     * @return  <code>true</code> if everything went ok or <code>false</code>
     *          otherwise.
     */
    private boolean getParamValue (ValueParam valueParam)
    {
        String str = null;
        Date date = null;
        OID paramOid = null;
        ValueDataElement value = valueParam.p_value;
        Parameter param = valueParam.p_param;

        // the assignment of the values seems to be a litle bit complicated.
        // we use the conversion methods from the data element class
        // to get consistent results for the mapping table.
        // check if the parameter is null
        if (param.isNull ())
        {
            // set the value also null
            value.value = null;
            return true;
        } // if (param.isNull ())

        // CONTRAINT: the parameter must be an output parameter
        if (param.getDirection () != ParameterConstants.DIRECTION_OUT)
        {
            this.error ("getParamValue: parameter '" + param.getName () +
                    "' must be an output paramter!");
            return false;
        } // if (param.getDirection () != ParameterConstants.DIRECTION_OUT)

        // check the parameter type
        switch (param.getDataType ())
        {
            case ParameterConstants.TYPE_STRING:
                str = param.getValueString ();
                // is the value a password ?
                if (value.type.equals (DIConstants.VTYPE_PASSWORD))
                {
                    // encrypt the password:
                    value.value = EncryptionManager.decrypt (str);
                } // if is the value a password
                else    // not a password
                {
                    value.value = str;
                } // else not a password

                value.setFileFlag ((value.type.equals (DIConstants.VTYPE_FILE) || 
                		value.type.equals (DIConstants.VTYPE_IMAGE)) && 
                		str != null && str != "");
                break;

            case ParameterConstants.TYPE_INTEGER:
                value.value = Integer.toString (param.getValueInteger ());
                break;

            case ParameterConstants.TYPE_DOUBLE:
                value.value = Double.toString (param.getValueDouble ());
                break;

            case ParameterConstants.TYPE_CURRENCY:
                value.value = Helpers.moneyToString (param.getValueCurrency ());
                break;

            case ParameterConstants.TYPE_BOOLEAN:
                value.value = Boolean.toString (param.getValueBoolean ()).toUpperCase ();
                break;

            case ParameterConstants.TYPE_VARBYTE:
                paramOid = SQLHelpers.getSpOidParam (param);
                if (paramOid != null)
                {
                    value.value = paramOid.toString ();
                } // if (paramOid != null)
/* KR 20090715 "value" is never used afterwards. Thus it
   makes no sense to set it to null
                else    // oid was null
                {
                    value = null;
                } // else oid was null
*/
                break;

            case ParameterConstants.TYPE_DATE:
                date = param.getValueDate ();

                if (value.type.equals (DIConstants.VTYPE_DATE) ||
                        value.type.equals (DIConstants.VTYPE_REMINDER))
                {
                    value.value = DateTimeHelpers.dateToString (date);
                } // if
                else if (value.type.equals (DIConstants.VTYPE_TIME))
                {
                    value.value = DateTimeHelpers.timeToString (date);
                } // else if
                else if (value.type.equals (DIConstants.VTYPE_DATETIME))
                {
                    value.value = DateTimeHelpers.dateTimeToString (date);
                } // else if
                break;

            default:
                this.error ("getParamValue: Invalid type for parameter " + param.getName ());
                return false;
        } // switch paramType

        return true;
    } // getParamValue


    /**************************************************************************
     * Get the name of the stored procedure which is used for creating object
     * data. <BR/>
     *
     * @param   table   The table name, on which to perform the create
     *                  operation.
     *
     * @return  The procedure name.
     */
    private StringBuffer getCreateProcName (StringBuffer table)
    {
        return new StringBuffer (DBMapper.MAPPING_PROC_PREFIX + table + "$create");
    } // getCreateProcName


    /**************************************************************************
     * Get the name of the stored procedure which is used for retrieving data.
     * <BR/>
     *
     * @param   table   The table name, on which to perform the retrieve
     *                  operation.
     *
     * @return  The procedure name.
     */
    private StringBuffer getRetrieveProcName (StringBuffer table)
    {
        return new StringBuffer (DBMapper.MAPPING_PROC_PREFIX + table + "$retrv");
    } // getRetrieveProcName


    /**************************************************************************
     * Get the name of the stored procedure which is used for updating. <BR/>
     *
     * @param   table   The table name, on which to perform the update
     *                  operation.
     *
     * @return  The procedure name.
     */
    private StringBuffer getUpdateProcName (StringBuffer table)
    {
        return new StringBuffer (DBMapper.MAPPING_PROC_PREFIX + table + "$update");
    } // getUpdateProcName


    /**************************************************************************
     * Get the name of the stored procedure which is used for copying. <BR/>
     *
     * @param   table   The table name, on which to perform the copy operation.
     *
     * @return  The procedure name.
     */
    private StringBuffer getCopyProcName (StringBuffer table)
    {
        return new StringBuffer (DBMapper.MAPPING_PROC_PREFIX + table + "$copy");
    } // getCopyProcName


    /**************************************************************************
     * Helper function to check the data element. <BR/>
     *
     * @param   dataElement Data element to be checked.
     * @param   checkOid    Shall the oid be checked, too?
     *
     * @return  <CODE>true</CODE> if the data element is valid,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean isValidDataElement (DataElement dataElement, boolean checkOid)
    {
        if (dataElement == null)
        {
            this.dbg ("DataElement is null!");
            return false;
        } // if
        if (checkOid && dataElement.oid == null)
        {
            this.dbg ("DataElement has no oid!");
            return false;
        } // if
        if (dataElement.p_typeCode == null || dataElement.p_typeCode.length () == 0)
        {
            this.dbg ("DataElement has no type code!");
            return false;
        } // if
        
        if ((dataElement.values == null || dataElement.values.size () == 0) &&
                // BT 20090918 - IBS-268
                // Check if a table name is set. If no table name is set, it is ok that
                // the object has no values.
                (dataElement.tableName != null && dataElement.tableName.length() != 0))
        {
            this.dbg ("DataElement has no values!");
            return false;
        } // if
        return true;
    } // isValidDataEleemnt


    /**************************************************************************
     * Check if a string is a valid database identifier. <BR/>
     *
     * @param   ident   The identifier to be checked.
     *
     * @return  <CODE>true</CODE> if the identifier is valid,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean isValidDatabaseIdentifier (String ident)
    {
        if (ident == null)
        {
            return false;
        } // if
        // check if all characters are valid for a table name in the database
        int len = ident.length ();

        if (len == 0)
        {
            return false;
        } // if

        if (!Character.isJavaIdentifierStart (ident.charAt (0)))
        {
            return false;
        } // if

        for (int i = 0; i < len; i++)
        {
            // all characters in the table name must be valid characters
            // for the database.
            if (!Character.isJavaIdentifierPart (ident.charAt (i)))
            {
                return false;
            } // if
        } // for i
        return true;
    } // isValidDataBaseIdentifer


    /**************************************************************************
     * Is the given field type a TEXT or CLOB field. <BR/>
     * Note that those fields needs special treatment because they cannot
     * be read through stored procedures.<BR/r>
     *
     * @param value        the valueDataElement to check
     *
     * @return  <CODE>true</CODE> if the fieldtype is a TEXT field or
     *          <CODE>false</CODE> otherwise.
     */
    private boolean isTextField (ValueDataElement value)
    {
        // get the database field type for the value.
        String fieldType = this.getDatabaseFieldType (value);
        // check if the field type is valid
        if (fieldType == null || fieldType.length () == 0)
        {
            this.error ("performMappingAction: no fieldtype found for value '" +
                    value.field + "'");
            return false;
        } // invalid field type

        // got the field type
        return this.isTextField (fieldType);
    } // isTextField


    /**************************************************************************
     * Is the given field type a TEXT or CLOB field. <BR/>
     * Note that those fields needs special treatment because they cannot
     * be read through stored procedures.<BR/r>
     *
     * @param   fieldType   The fieldtype to check.
     *
     * @return  <CODE>true</CODE> if the fieldtype is a TEXT field or
     *          <CODE>false</CODE> otherwise.
     */
    private boolean isTextField (String fieldType)
    {
        // check if the field type is valid
        if (fieldType == null || fieldType.length () == 0)
        {
            return false;
        } // invalid field type

        // got the field type
        return fieldType.equals ("NTEXT") || fieldType.equals("NCLOB")|| fieldType.equals ("CLOB");
    } // isTextField


    /**************************************************************************
     * Is the given field type a referenced object field. <BR/>
     *
     * @param value        the valueDataElement to check
     *
     * @return  <CODE>true</CODE> if the fieldtype is a referenced object field or
     *          <CODE>false</CODE> otherwise.
     */
    private boolean isOidField (ValueDataElement value)
    {
        boolean multiSelection = value.multiSelection != null &&
            value.multiSelection.equalsIgnoreCase (DIConstants.ATTRVAL_YES);

        // check if the field type is FIELDREF or VALUEDOMAIN:
        // Exclude multi selection fields since several values are stored
        // within one field when using multiSelection.
        return DIConstants.VTYPE_FIELDREF.equals (value.type) ||
            (DIConstants.VTYPE_VALUEDOMAIN.equals (value.type) &&  !multiSelection);
    } // isOidField


    /**************************************************************************
     * Checks the given assertion. <BR/>
     *
     * @param   assertion   Contains the value <CODE>true</CODE> if the
     *                      assertion was o.k., <CODE>false</CODE> otherwise.
     */
    private void checkAssertion (boolean assertion)
    {
        if (!assertion)
        {
            this.dbg ("!!!! ASSERTION FAILED !!!!");
        } // if
    } // checkAssertion


    /**************************************************************************
     * Debug function. <BR/>
     *
     * @param   msg     The debugging message.
     */
    private void dbg (String msg)
    {
//        debug.DebugClient.debugln (this.getClass ().getName () + ": " + msg);
    } // dbg


    /**************************************************************************
     * Debug function. <BR/>
     *
     * @param   msg     The debugging message.
     */
    private void dbg (StringBuffer msg)
    {
//        debug.DebugClient.debugln (this.getClass ().getName () + ": " + msg);
    } // dbg


    /**************************************************************************
     * Get the current DB type. <BR/>
     *
     * @return  The actual set DB type.
     */
    public int getDbType ()
    {
        // get the property value and return the result:
        return this.p_dbType;
    } // getDbType


    /**************************************************************************
     * Set the DB type. <BR/>
     *
     * @param   dbType  DB type to be set.
     */
    public void setDbType (int dbType)
    {
        // set the property value:
        this.p_dbType = dbType;

        // check the database type and set the database mappings:
        switch (this.p_dbType)
        {
            case SQLConstants.DB_ORACLE:
                this.p_typeTable = DBMapper.TYPETABLE_ORACLE;
                break;

            case SQLConstants.DB_MSSQL:
                this.p_typeTable = DBMapper.TYPETABLE_SQL;
                break;

            case SQLConstants.DB_DB2:
                this.p_typeTable = DBMapper.TYPETABLE_DB2;
                break;

            default:
                IOHelpers.showMessage ("database type " + this.p_dbType + " not supported: ",
                                       this.app, this.sess, this.env);
        } // switch
    } // setDbType

    /**************************************************************************
     * Retrieves the ID of the newest protocol entry for this object. <BR/>
     *
     * @param   oidParam    The oid of the object for which to get the protocol
     *                      entry.
     * @param   userId      Id of the user for whom to get the protocol entry.
     *
     * @return  id of the newest protocol entry for this object
     *          or <CODE>null</CODE> if nothing is found
     */
    private String getNewestProtocollIdForObject (OID oidParam, int userId)
    {
        String retId = null;
//trace (" --- DBMapper.getNewestProtocollEntryForObject ANFANG --- ");
        SQLAction action = null;        // the action object used to access the
                                        // database
        int rowCount = 0;               // row counter

        StringBuffer queryStr = null;         // the query to be performed

        // get the elements out of the database:
        // create the SQL String to select all tuples
        queryStr =
            new StringBuffer ("SELECT MAX(id) AS id")
                .append (" FROM  ibs_protocol_01")
                .append (" WHERE oid = ").append (oidParam.toStringQu ())
                .append ("   AND userId = ").append (userId);

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            // execute the query:
            rowCount = action.execute (queryStr.toString (), false);

            // check if the resultset is empty:
            if (rowCount > 0)      // at least one tuple found?
            {
                //get the oid
                retId = action.getString ("id");

                // end transaction
                action.end ();
            } // if
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (e, this.app, this.sess, this.env);
        } // catch
        finally
        {
            try
            {
                // ensure that the action is not longer used:
                action.end ();
            } // try
            catch (DBError e)
            {
                // nothing to do
            } // catch

            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

//trace (" --- DBMapper.getNewestProtocollEntryForObject ENDE --- ");
        // return the number of found references:
        return retId;
    } // getNewestProtocollIdForObject


    /**************************************************************************
     * Insert a new protocol entry into the database.
     *
     * @param   protocolId  The protocol Id of the corresponding protocol
     *                      element.
     * @param   vde         The value data element which has been changed.
     *
     * @return  The id of the new protocol entry.
     */
    private String insertProtocolEntry (String protocolId, ValueDataElement vde)
    {
//        trace (" --- DBMapper.insertProtocollEntry ANFANG --- ");
        String value = vde.value;
        String oldValue = vde.getOldValue ();
        
        // TODO: BB: Note that this can become a performance killer for large
        // multiselection valuedomains since EVERY seleted value is retrieved
        // as object and concatendated to the string that will be truncated 
        // afterwards to 60 charaters!!!        
        // Since the protocol is not really used it does not make sence to produce
        // such an overhead. Storing the OIDS appears to be sufficent
        // therefore we only resolve the value for non multiselection fields
        /*
        boolean multiSelection = vde.multiSelection != null &&
        	DIConstants.ATTRVAL_YES.equalsIgnoreCase (vde.multiSelection);
         */        	
        // non-multiple VALUEDOMAIN or FIELDREF field?
        if (DIConstants.VTYPE_FIELDREF.equals (vde.type) || 
        	(DIConstants.VTYPE_VALUEDOMAIN.equals (vde.type)))
        {
            // replace the oid string with a string including the 
        	// names of the referenced object
            value = this.getValueStringFromOidString (vde.value);
            // replace the old oid string with a string 
            // including the names of the referenced object
            oldValue = this.getValueStringFromOidString (vde.getOldValue ());
        } // if ((DIConstants.VTYPE_FIELDREF.equals (vde.type) && ...

        // truncate to max 63 characters
        if (value != null && value.length () > 63)
        {
            value = value.substring (0, 60);
            value += "...";
        } // if (value != null && value.length () > 63)
        // truncate to max 63 characters
        if (oldValue != null && oldValue.length () > 63)
        {
            oldValue = oldValue.substring (0, 60);
            oldValue += "...";
        } // if (oldValue != null && oldValue.length () > 63)

        String retId = null;
        SQLAction action = null;        // the action object used to access the
                                        // database
        
        // Create the insert statment
        InsertStatement stmnt = new InsertStatement ("ibs_ProtocolEntry_01", "protocolId", protocolId);
        stmnt.addUnicodeString ("fieldName", vde.field);
        stmnt.addUnicodeString ("oldValue", SQLHelpers.asciiToDb(oldValue));
        stmnt.addUnicodeString ("newValue", SQLHelpers.asciiToDb(value));
        
        // execute the insertStr, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();

            stmnt.execute (action);

            // end transaction
            action.end ();
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (e, this.app, this.sess, this.env);
        } // catch
        finally
        {
            try
            {
                // ensure that the action is not longer used:
                action.end ();
            } // try
            catch (DBError e)
            {
                // nothing to do
            } // catch

            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

//trace (" --- BusinessObject.getNewestProtocollEntryForObject ENDE --- ");
        // return the number of found references:
        return retId;
    } // insertProtocollEntry

    
    /***************************************************************************
     * Replaces the given string including oids with a string including the names of the referenced objects.
     *
     * @param oidString string with oids
     *
     * @return a string including the names of the referenced objects
     */
    private String getValueStringFromOidString (String oidString)
    {
        StringBuffer colValueBuffer = new StringBuffer (oidString.length ());

        // any oidString set?
        // Note that the string could contain whitespace without any data
        if (oidString != null && oidString.trim().length() > 0)
        {
        	String oidFilter = oidString.replace (BOConstants.MULTISELECTION_VALUE_SAPERATOR, ",");
        	// cut leading separator	
        	if (oidFilter.startsWith(","))
        	{
        		oidFilter = oidFilter.substring (1);        		
        	} // if (oidFilter.startsWith(","))
        	if (oidFilter.endsWith(","))
        	{
        		oidFilter = oidFilter.substring (0, oidFilter.length() - 1);
        	} // if (oidFilter.startsWith(","))        	
        	
        	Vector<BusinessObjectInfo> objInfos =
        		BOHelpers.findObjects (null, null, new StringBuilder (oidFilter), null, this.env);
        	// any object found?
        	if (objInfos != null && objInfos.size () > 0)
        	{
        		for (Iterator iterator = objInfos.iterator(); iterator.hasNext();) 
        		{
					BusinessObjectInfo businessObjectInfo = 
						(BusinessObjectInfo) iterator.next();
					// append the name
					colValueBuffer.append (BOConstants.MULTISELECTION_VALUE_SAPERATOR);
					colValueBuffer.append (businessObjectInfo.p_name);
				} // for        			
        	} // if (objInfos != null && objInfos.size () > 0)
        } // if (oidString != null && oidString.length() > 0)                

        return colValueBuffer.toString ();
    } // getValueStringFromOidString

} // class DBMapper

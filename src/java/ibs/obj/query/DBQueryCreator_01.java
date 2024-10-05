/*
 * Class: DBQueryCreator_01.java
 */

// package:
package ibs.obj.query;

// imports:
import ibs.bo.BOHelpers;
import ibs.bo.BOPathConstants;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.bo.ObjectClassNotFoundException;
import ibs.bo.ObjectInitializeException;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.type.TypeNotFoundException;
import ibs.di.DIConstants;
import ibs.di.DataElement;
import ibs.di.XMLViewer_01;
import ibs.service.conf.Configuration;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.DBConf;
import ibs.tech.sql.DBConnector;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.util.file.FileHelpers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;


/******************************************************************************
 * Implements a query creator for a specific database. <BR/>
 *
 * @version     $Id: DBQueryCreator_01.java,v 1.13 2013/01/16 16:14:13 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR)  020502
 ******************************************************************************
 */
public class DBQueryCreator_01 extends QueryCreator_01 implements Cloneable
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DBQueryCreator_01.java,v 1.13 2013/01/16 16:14:13 btatzmann Exp $";


    /**
     * The database connection object for this query creator. <BR/>
     */
    private OID p_connectorOid = null;

    /**
     * The name of the database connection object for this query creator. <BR/>
     */
    private String p_connectorName = null;

    /**
     * The database configuration. <BR/>
     * This configuration is always used to get the database connection when
     * trying to execute a query.
     */
    private DBConf p_dbConf = null;


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    /**************************************************************************
     * This constructor creates a new instance of the class QueryCreator_01.
     * <BR/>
     */
    public DBQueryCreator_01 ()
    {
        // call constructor of super class:
        super ();

        // initialize properties common to all subclasses:
    } // DBQueryCreator_01


    /**************************************************************************
     * Creates a QueryCreator_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public DBQueryCreator_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // DBQueryCreator_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // call corresponding method of super class:
        super.initClassSpecifics ();

        // set names of procedures:
        this.procCreate     = "p_DBQueryCreator_01$create";
        this.procRetrieve   = "p_DBQueryCreator_01$retrieve";
        this.procChange     = "p_DBQueryCreator_01$change";

        // set elementClassName:
//        this.elementClassName = "ibs.bo.ReportObjectElement";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters += 1;
        this.specificChangeParameters += 1;

        // set tablename (for CLOB and TEXT-editing):
        // (use the same table as the super class, so don't set the table name)
//        this.tableName = "ibs_BOQueryCreator_01";
    } // initClassSpecifics



    ///////////////////////////////////////////////////////////////////////////
    // object data methods  - interface to other objects
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Set the oid for the connector. <BR/>
     * To ensure that the current DBQueryCreator has no connnector assigned the
     * value for the connectorOid must be <CODE>null</CODE>. <BR/>
     *
     * @param   connectorOid    The oid of the connector.
     */
    public void setConnectorOid (OID connectorOid)
    {
//System.out.println ("connectorOid = " + connectorOid);
        XMLViewer_01 connector = null;  // the conector object
        String connectorName = this.p_connectorName; // the name of the connector

        if (connectorOid == null)       // no connectorOid defined?
        {
            // initialize the changed values:
            connector = null;
            connectorName = "";
        } // if no connectorOid defined
        else if (!connectorOid.equals (this.p_connectorOid))
                                        // connectorOid has changed?
        {
            // initialize the changed values:
            connector = null;
            connectorName = "" + connectorOid;

            try
            {
                // get the associated connector object:
//System.out.println ("    getting connector...");
                connector = (XMLViewer_01) this.getObjectCache ().fetchObject
                    (connectorOid, this.user, this.sess, this.env, false);
//System.out.println ("    connector: " + connector);
                connectorName = connector.name;
//System.out.println ("    connectorName: " + connectorName);
            } // try
            catch (ObjectNotFoundException e)
            {
                System.out.println (e.toString ());
            } // catch
            catch (TypeNotFoundException e)
            {
                System.out.println (e.toString ());
            } // catch
            catch (ObjectClassNotFoundException e)
            {
                System.out.println (e.toString ());
            } // catch
            catch (ObjectInitializeException e)
            {
                System.out.println (e.toString ());
            } // catch
            catch (Exception e)
            {
//                showMessage ("KR Exception within fetchObject: " + e + "." + IE302.TAG_NEWLINE);
                System.out.println ("Exception for oid " + connectorOid + ": ");
                ByteArrayOutputStream out = new ByteArrayOutputStream ();
                PrintStream stream = new PrintStream (out);
                e.printStackTrace (stream);
                System.out.println (out.toString ());
//                showMessage (out.toString ());
            } // catch
        } // else if connectorOid has changed

        // get the configuration out of the connector:
        if (connector != null)          // connector set?
        {
//System.out.println ("" + this.oid + ": using specific configuration.");
            // set the database configuration:
            this.p_dbConf = new DBConf ();
            this.p_dbConf.setDbLogDir (
                FileHelpers.makeFileNameValid (
                    this.app.p_system.p_m2AbsBaseDir + File.separator +
                    BOPathConstants.PATH_SQLLOGS));
            
            // take over e-mail settings from standard configuration (as they
            // are not read out from configuration file by class DBConfig
            Configuration stdConfig = (Configuration) this.app.configuration;
            
            if (stdConfig != null)
            {
            	this.p_dbConf.setSmtpServer (stdConfig.getSmtpServer ());
            	this.p_dbConf.setMailSystem (stdConfig.getMailSystem ());
            	this.p_dbConf.setMailAdmin (stdConfig.getMailAdmin ());
            }

            // get the configuration properties out of the connector:
            this.p_dbConf.setDBConfiguration (connector.dataElement, false);
        } // if connector set
        else                            // no connector set
        {
//System.out.println ("" + this.oid + ": using default configuration.");
            // use default configuration:
            this.p_dbConf = ((Configuration) this.app.configuration).getDbConf ();
        } // else no connector set

        // set the connector data:
        this.p_connectorOid = connectorOid;
        this.p_connectorName = connectorName;
    } // setConnectorOid


    /**************************************************************************
     * Returns a sql action object associated with a connection. <BR/>
     *
     * @return  The action object associated with the required connection.
     *
     * @throws  DBError
     *          An exception occurred within database statement.
     */
    public SQLAction getQueryDBConnection () throws DBError
    {
        return DBConnector.getDBConnection (this.p_dbConf);
    } // getQueryDBConnection


    /**************************************************************************
     * Releases a sql action object associated with a database connection. <BR/>
     *
     * @param   action      The action object associated with the connection.
     *
     * @throws  DBError
     *          An exception occurred within database statement.
     */
    public void releaseQueryDBConnection (SQLAction action) throws DBError
    {
        // release the action object:
        DBConnector.releaseDBConnection (this.p_dbConf, action);
    } // releaseQueryDBConnection


    ///////////////////////////////////////////////////////////////////////////
    // database methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performChangeData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the change data stored procedure.
     *
     * @param sp        The stored procedure to add the change parameters to.
     */
    @Override
    protected void setSpecificChangeParameters (StoredProcedure sp)
    {
        // call method of super class:
        super.setSpecificChangeParameters (sp);

        // set the specific parameters:
        // connectorOid:
        BOHelpers.addInParameter (sp, this.p_connectorOid);
    } // setSpecificChangeParameters


    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the retrieve data stored procedure.
     *
     * @param sp        The stored procedure the specific retrieve parameters
     *                  should be added to.
     * @param params    Array of parameters the specific retrieve parameters
     *                  have to be added to for beeing able to retrieve the
     *                  results within getSpecificRetrieveParameters.
     * @param lastIndex The index to the last element used in params thus far.
     *
     * @return  The index of the last element used in params.
     */
    @Override
    protected int setSpecificRetrieveParameters (StoredProcedure sp, Parameter[] params,
                                                 int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // call method of super class:
        i = super.setSpecificRetrieveParameters (sp, params, lastIndex);

        // set the specific parameters:
        // connectorOid:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


    /**************************************************************************
     * Get the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * type specific data from the retrieve data stored procedure.
     *
     * @param   params      The array of parameters from the retrieve data
     *                      stored procedure.
     * @param   lastIndex   The index to the last element used in params thus far.
     */
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // call method of super class:
        super.getSpecificRetrieveParameters (params, lastIndex);
        i += 5;

        // get the specific parameters:
        // connectorOid:
        this.setConnectorOid (SQLHelpers.getSpOidParam (params[++i]));
    } // getSpecificRetrieveParameters



    ///////////////////////////////////////////////////////////////////////////
    // GUI methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <CODE>env</CODE> property is used for getting the parameters.
     * This property must be set before calling this method. <BR/>
     */
    public void getParameters ()
    {
        OID oid = null;

        // get parameters of super class:
        super.getParameters ();

        // connectorOid:
        if ((oid = this.env.getOidParam (QueryArguments.ARG_CONNECTOROID)) != null)
        {
            // set the connector oid:
            this.setConnectorOid (oid);
        } // if
    } // getParameters


    /***************************************************************************
     * Represent the properties of a ProductGroup_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showProperties
     * @see ibs.IbsObject#showProperty(TableElement, String, String, int, String, String)
     */
    protected void showProperties (TableElement table)
    {
        // object specific attributes
        // loop through all properties of this object and display them:
        super.showProperties (table);

        // connectorOid:
        this.showProperty (table, QueryArguments.ARG_CONNECTOROID, "Connector",
            Datatypes.DT_LINK, this.p_connectorName, this.p_connectorOid);
    } // showProperties


    /***************************************************************************
     * Represent the properties of a ProductGroup_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showFormProperties
     * @see ibs.IbsObject#showFormProperty(TableElement, String, String, String[], int, int, int, String)
     */
    protected void showFormProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showFormProperties (table);

        // connectorOid:
        // show selectionbox filled with query data
        this.showFormProperty (table, QueryArguments.ARG_CONNECTOROID,
            "Connector", Datatypes.DT_QUERYSELECTIONBOX, "DBConnectors",
            "" + this.p_connectorOid, DIConstants.IDTYPE_OBJECTID, false);
    } // showFormProperties


    // /////////////////////////////////////////////////////////////////////////
    // import / export methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Reads the object data from a DataElement. <BR/>
     *
     * @param   dataElement     The DataElement to read the data from.
     *
     * @see ibs.bo.BusinessObject#readImportData
     */
    public void readImportData (DataElement dataElement)
    {
        // get business object specific values
        super.readImportData (dataElement);

        // get the type specific values
        this.setConnectorOid (this.readImportOid (dataElement, "CONNECTOROID"));
    } // readImportData


    /**************************************************************************
     * Writes the object data to a DataElement. <BR/>
     *
     * @param   dataElement     The dataElement to write the data to.
     *
     * @see ibs.bo.BusinessObject#writeExportData
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        super.writeExportData (dataElement);

        // set the type specific values:
        dataElement.setExportValue ("CONNECTOROID", this.p_connectorOid.toString ());
    } // writeExportData

} // class DBQueryCreator_01

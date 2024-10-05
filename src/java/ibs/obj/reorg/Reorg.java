/*
 * Class: Reorg.java
 */

// package:
package ibs.obj.reorg;

// imports:
import ibs.bo.BOHelpers;
import ibs.bo.BusinessObject;
import ibs.bo.BusinessObjectInfo;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.States;
import ibs.bo.type.TypeConstants;
import ibs.di.DIConstants;
import ibs.di.XMLViewer_01;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.service.user.User;
import ibs.tech.html.IE302;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.util.DateTimeHelpers;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;

import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;


/******************************************************************************
 * This class represents one object of type Reorg. <BR/>
 * The class handles the reorganisation functions. <BR/>
 *
 * @version     $Id: Reorg.java,v 1.17 2012/10/18 11:56:24 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 040709
 ******************************************************************************
 */
public abstract class Reorg extends XMLViewer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Reorg.java,v 1.17 2012/10/18 11:56:24 btatzmann Exp $";

    /**
     * The separator string used for list e.q. typecode. <BR/>
     */
    public static final char LIST_SEPARATOR = ',';

    /**
     * The separator string used for oids lis. <BR/>
     */
    public static final char OIDLIST_SEPARATOR = '\n';

    /**
     * The separator string used for SQL IN (..,..) clause. <BR/>
     */
    public static final String SQL_SEPARATOR = ",";

    /**
     * The asterix always means: get al. <BR/>
     */
    public static final String ALL = "*";

    /**
     * argument: simulation mode activate. <BR/>
     */
    public static final String ARG_ISSIMULATION = "isSim";

    /**
     * argument: reorganisation function to perfor. <BR/>
     */
    public static final String ARG_REORGFCT = "reorgFct";

    /**
     * field name: lo. <BR/>
     */
    public static final String FLD_LOG = "Log";

    /**
     * The log of the last actio. <BR/>
     */
    private StringBuffer p_log = null;

    /**
     * The global objects counter used to determine the actual object
     * processe. <BR/>
     */
    protected int p_objCounter = 0;

    /**
     * A vector to hold the error. <BR/>
     */
    private Vector<String[]> p_errors = null;

    /**
     * A vector to hold the conflict. <BR/>
     */
    private Vector<String[]> p_conflicts = null;

    /**
     * A vector to hold the warning. <BR/>
     */
    private Vector<String[]> p_warnings = null;

    /**
     * The starting date and time of a reorg functio. <BR/>
     */
    protected Date p_startDate = null;


    /**************************************************************************
     * This constructor creates a new instance of the class ProcedureObject. <BR/>
     */
    public Reorg ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init the error statistic
        this.initStatistic ();
    } // Reorg


    /**************************************************************************
     * This constructor calls the corresponding constructor of the super class.
     . <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     */
    public Reorg (OID oid, User user)
    {
        // call constructor of super class:
        super ();

        // initialize properties common to all subclasses:
        this.initObject (oid, user, null, null, null);

        // init the error statistic
        this.initStatistic ();
    } // Reorg


    /***************************************************************************
     * Initialize the error statistic. <BR/>
     */
    public void initStatistic ()
    {
        this.p_objCounter = 0;
        this.p_errors = new Vector<String[]> ();
        this.p_conflicts = new Vector<String[]> ();
        this.p_warnings = new Vector<String[]> ();
    } // initStatistic


    /***************************************************************************
     * Reset the reorg log. <BR/>
     */
    public void resetLog ()
    {
        this.p_log = new StringBuffer ();
    } // resetLog


    /***************************************************************************
     * Get the reorg log. <BR/>
     *
     * @return the log string
     */
    public String getLog ()
    {
        if (this.p_log != null)
        {
            return this.p_log.toString ();
        } // if

        return null;
    } // getLog


    /***************************************************************************
     * Add a log entr. <BR/>
     *
     * @param entry a log entry string buffer
     */
    public void addLog (StringBuffer entry)
    {
        this.addLog (entry.toString ());
    } // addLog


    /***************************************************************************
     * Add a log entr. <BR/>
     *
     * @param entry a log entry string builder
     */
    public void addLog (StringBuilder entry)
    {
        this.addLog (entry.toString ());
    } // addLog


    /***************************************************************************
     * Add a log entr. <BR/>
     *
     * @param entry a log entry string
     */
    public void addLog (String entry)
    {
        if (this.p_log == null)
        {
            this.resetLog ();
        } // if

        // add the log entry to the log
        this.p_log.append (entry);
        // write the log entry to the environment to display it
        this.env.write (entry);
    } // addLog


    /***************************************************************************
     * Add an error message to the log and the error vector. <BR/>
     *
     * @param msg the message to be added
     */
    public void addError (String msg)
    {
        String[] entryData = new String[2];
        String newMsg;
        entryData[0] = "" + this.p_objCounter;
        entryData[1] = msg;
        this.p_errors.add (entryData);

        newMsg = IE302.TAG_NEWLINE + "<FONT COLOR=\"RED\"><B>FEHLER: " + msg +
            "</B></FONT>";

        this.addLog (newMsg);
    } // addError


    /***************************************************************************
     * Add a conflict message to the log and the conflict vector. <BR/>
     *
     * @param msg the message to be added
     */
    public void addConflict (String msg)
    {
        String[] entryData = new String[2];
        String newMsg;
        entryData[0] = "" + this.p_objCounter;
        entryData[1] = msg;
        this.p_conflicts.add (entryData);

        newMsg = IE302.TAG_NEWLINE + "<FONT COLOR=\"BLUE\"><B>KONFLIKT: " + msg +
            "</B></FONT>";

        this.addLog (newMsg);
    } // addConflict


    /***************************************************************************
     * Add a conflict message to the log and the conflict vector. <BR/>
     *
     * @param msg the message to be added
     */
    public void addWarning (String msg)
    {
        String[] entryData = new String[2];
        String newMsg;
        entryData[0] = "" + this.p_objCounter;
        entryData[1] = msg;
        this.p_warnings.add (entryData);

        newMsg = IE302.TAG_NEWLINE + "<FONT COLOR=\"GREEN\"><B>WARNUNG: " + msg +
            "</B></FONT>";

        this.addLog (newMsg);
    } // addConflict


    /***************************************************************************
     * Add a message to the log with a link to the actual object to process and
     * increment the object counte. <BR/>
     *
     * @param oidStr    oid of the object
     * @param objName   name of the object
     * @param typeName  type name of the object (OPTIONAL)
     */
    public void addLogObj (String oidStr, String objName, String typeName)
    {
        this.p_objCounter++;

        StringBuffer msg = new StringBuffer ()
            .append ("<LI ID=\"")
            .append (this.p_objCounter)
            .append ("\"/>[")
            .append (this.p_objCounter)
            .append ("] Bearbeite ");

        // any typename set?
        if (typeName != null)
        {
            msg.append (typeName).append (" ");
        } // if (typeName != null)

        msg.append ("<A HREF=\"")
            .append (this.getShowObjectJavaScriptUrl (oidStr))
            .append ("\">").append (objName).append (" (<CODE>")
            .append (oidStr).append ("</CODE>)</A> ... ");

        this.addLog (msg);
    } // addLogObj

    /***************************************************************************
     * Add a message to the log with links the actual object to be processed
     * and any second objec. <BR/>
     * Increments the object counte. <BR/>
     *
     * @param oidStr    oid of the object
     * @param objName   name of the object
     * @param typeName  type name ov th object (OPTIONAL)
     * @param middleStr any string in the middle to display
     * @param oidStr2   oid of the second object
     * @param objName2  name of the second object
     * @param typeName2 type name of the second object (OPTIONAL)
     */
    public void addLogObj (String oidStr, String objName, String typeName,
       String middleStr,
       String oidStr2, String objName2, String typeName2)
    {
        this.p_objCounter++;

        StringBuffer msg = new StringBuffer ()
            .append ("<LI ID=\"")
            .append (this.p_objCounter)
            .append ("\"/>[")
            .append (this.p_objCounter)
            .append ("] Editing ");

        // any typename set?
        if (typeName != null)
        {
            msg.append (typeName).append (" ");
        } // if (typeName != null)

        msg.append ("<A HREF=\"")
            .append (this.getShowObjectJavaScriptUrl (oidStr))
            .append ("\">")
            .append (objName)
            .append (" (<CODE>").append (oidStr).append ("</CODE>)</A> ")
            .append (middleStr);

        // any typename set?
        if (typeName2 != null)
        {
            msg.append (typeName2).append (" ");
        } // if (typeName2 != null)

        msg.append ("<A HREF=\"")
            .append (this.getShowObjectJavaScriptUrl (oidStr2))
            .append ("\">")
            .append (objName2)
            .append (" (<CODE>").append (oidStr2)
            .append ("</CODE>)</A> ... ");

        this.addLog (msg);
    } // addLogObj


    /***************************************************************************
     * Create the show object java script with the given oid.<BR/>
     *
     * @param   oidStr      the oid string
     *
     * @return the show object java script as string buffer
     */
    public StringBuffer getShowObjectJavaScriptUrl (String oidStr)
    {
        // oidstring given?
        if (oidStr != null && oidStr.length () > 0)
        {
            return new StringBuffer ("javascript:show('").append (oidStr)
                .append ("');");
        } // if (oidStr != null && oidStr.length() >0)

        // no oidstring given
        return new StringBuffer ();
    } // getShowObjectJavaScriptUrl


    /***************************************************************************
     * Add a message to the log with a link to the actual object to process and
     * increment the object counte. <BR/>
     *
     * @param msgStr the message to display
     */
    public void addLogObj (String msgStr)
    {
        this.p_objCounter++;

        StringBuffer msg = new StringBuffer ()
            .append ("<LI ID=\"")
            .append (this.p_objCounter)
            .append ("\"/>[")
            .append (this.p_objCounter)
            .append ("] ")
            .append (msgStr);

        this.addLog (msg);
    } // addLogObj


    /***************************************************************************
     * Returns if the log has been set. <BR/>
     *
     * @return <code>true</code> in case the log has been set
     */
    public boolean existsLog ()
    {
        return this.p_log != null;
    } // existsLog


    /***************************************************************************
     * Change the data of a business object in the database. <BR/>
     . <BR/>
     * This method tries to store the object into the database.
     * During this operation a rights check is done, too.
     * If this is all right the object is stored and this method terminates
     * otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   NameAlreadyGivenException
     *              An object with this name already exists. This exception is
     *              only raised by some specific object types which don't allow
     *              more than one object with the same name.
     */
    protected final void performChangeData (int operation)
        throws NoAccessException, NameAlreadyGivenException
    {

    // simulation mode on?
        boolean isSimulate =
            this.env.getBoolParam (Reorg.ARG_ISSIMULATION) == IOConstants.BOOLPARAM_TRUE;

        // set a javascript variable to indicate that the page is shown after a reorg
        StringBuffer jsCode = new StringBuffer ("<SCRIPT LANGUAGE=\"Javascript\">")
            .append ("var isAfterReorg = true;")
            .append ("</SCRIPT>");
        this.env.write (jsCode.toString ());

        // init the error statistic
        this.initStatistic ();

        // perform the reorganisation:
        this.performReorg (isSimulate);

        // save the log in case it is not null
        if (this.existsLog ())
        {
            // for compatibility reasons check if the log field exists
            if (this.dataElement.exists (Reorg.FLD_LOG))
            {
                // save the log
                this.dataElement.changeValue (Reorg.FLD_LOG, this.getLog ());
            } // if (dataElement.exists(FLD_LOG))
            // reset the log
            this.resetLog ();
        } // if (getLog () != null)

        // set the values in the database:
        super.performChangeData (operation);
    } // performChangeData


    /**************************************************************************
     * Perform reorganisation. <BR/>
     *
     * This method should be overwritten in subclasses. <BR/>
     *
     * @param   isSimulate  Shall the reorganisation be really performed or
     *                      just simulated?
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   NameAlreadyGivenException
     *              An object with this name already exists. This exception is
     *              only raised by some specific object types which don't allow
     *              more than one object with the same name.
     */
    protected abstract void performReorg (boolean isSimulate)
        throws NoAccessException, NameAlreadyGivenException;


    /**************************************************************************
     * Check if an object with a given oid exist. <BR/>
     * Note that the object must be physically deleted from the database
     * because the state will be ignore. <BR/>
     *
     * @param objectOid         oid of the object to check
     *
     * @return true if the object exists or false otherwise
     */
    protected boolean existsObject (OID objectOid)
    {
        int rowCount;
        SQLAction action = null;        // the action object used to access the DB
        boolean exists = false;

        // create the SQL String to check if the object exists:
        StringBuffer queryStr = new StringBuffer ()
            .append ("SELECT oid")
            .append (" FROM ibs_Object")
            .append (" WHERE oid = ").append (objectOid.toStringQu ());

        try
        {
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);
            // set if the object has been found
            exists = rowCount > 0;
            action.end ();
        } // try
        catch (DBError e)
        {
            // set the receiver list to null in order to indicate an error:
            // show error message:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            this.releaseDBConnection (action);
        } // finally

        // return the receivers list
        return exists;
    } // existsObject


    /**************************************************************************
     * Checks is a given typename exist. <BR/>
     *
     * @param typename  the typename to check
     *
     * @return true if the typename exists
     */
    protected boolean typeNameExists (String typename)
    {
        int rowCount;
        SQLAction action = null;        // the action object used to access the DB
        boolean exists = false;

        // create the SQL String to select the project order
        StringBuffer queryStr = new StringBuffer ()
            .append ("SELECT name")
            .append (" FROM ibs_type")
            .append (" WHERE name = '").append (typename).append ("'")
                .append (" AND state = ").append (States.ST_ACTIVE);

        try
        {
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);
            // is the result exactly one row?
            if (rowCount == 1)
            {
                exists = true;
            } // if (rowCount == 1)
            // end transaction
            action.end ();
        } // try
        catch (DBError e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            this.releaseDBConnection (action);
        } // finally
        // return the result
        return exists;
    } // typeNameExists


    /**************************************************************************
     * Create an string  that contains a list of typenames for an
     * ... IN ('&lt;typename1>','&lt;typename2>',...) sql clause.<BR/>
     * The typename will be checked if valid.<BR/>
     *
     * @param   typenames   A comma separated list with typenames.
     *
     * @return  a sql query string containing ('&lt;typename1>','&lt;typename2>',...)
     */
    protected String getTypeNamesClause (String typenames)
    {
        String [] typenamesArray;
        StringBuffer typenamesClause = new StringBuffer ();
        String comma = "";

        // constraint: any typenames must be given
        if (typenames == null || typenames.isEmpty ())
        {
            return null;
        } // if (typenames == null || typenames.length() == 0)

        typenamesArray =
            StringHelpers.stringToStringArray (typenames, Reorg.LIST_SEPARATOR);
        // did we get any typecodes?
        if (typenamesArray != null && typenamesArray.length > 0)
        {
            for (int i = 0; i < typenamesArray.length; i++)
            {
                this.addLog ("<LI>Verifying typename '" + typenamesArray [i] + "' ...");

                // check if the type exists
                if (this.typeNameExists (typenamesArray [i]))
                {
                    this.addLog ("is valid.");
                    typenamesClause.append (comma)
                        .append ("'")
                        .append (typenamesArray [i].trim ())
                        .append ("'");
                    comma = Reorg.SQL_SEPARATOR;
                } // if (this.app.cache.getTVersionId (typenamesArray [i]) != 0)
                else    // type invalid
                {
                    this.addError ("' is an invalid type!");
                } // type invalid
            } // for (int i = 0; i < typenamesArray; i++)

            // any typenames found?
            if (typenamesClause.length () == 0)
            {
                this.addError ("No valid typenames found!");
                return null;
            } // if (typenamesClause.length () == 0)

            // valid typenames found
            return "(" + typenamesClause + ")";
        } // if (typenamesArray != null)

        // no typenames defined
        this.addError ("No typenames defined!");
        return null;
    } // getTypeNamesClause


    /**************************************************************************
     * Create an string  that contains a list of tyecodes for an
     * ... IN (&lt;tVersionId1>,&lt;tVersionId2>,...) sql clause.<BR/>
     * The typecode will be checked if valid.<BR/>
     *
     * @param   typeCodes   A comma separated list with type codes.
     *
     * @return  a sql query string containing (&lt;tVersionId1>,&lt;tVersionId2>,...)
     */
    protected String getTVersionIdsClause (String typeCodes)
    {
        String [] typeCodesArray;
        StringBuffer tVersionIdClause = new StringBuffer ();
        String comma = "";
        int tVersionId;

        // constraint: any typecode must be given
        if (typeCodes == null || typeCodes.isEmpty ())
        {
            return null;
        } // if (typeCodes == null || typeCodes.length() == 0)

        typeCodesArray =
            StringHelpers.stringToStringArray (typeCodes, Reorg.LIST_SEPARATOR);
        // did we get any typenames?
        if (typeCodesArray != null && typeCodesArray.length > 0)
        {
            for (int i = 0; i < typeCodesArray.length; i++)
            {
                this.addLog ("<LI>Verifying typecode '" + typeCodesArray [i] + "' ...");
                tVersionId = this.getTypeCache ().getTVersionId (typeCodesArray [i].trim ());

                if (tVersionId != TypeConstants.TYPE_NOTYPE)
                {
                    this.addLog ("is valid.");
                    // add the tVersionId to the list of tVersionIds
                    tVersionIdClause.append (comma)
                        .append (tVersionId);
                    comma = Reorg.SQL_SEPARATOR;
                } // if (tVersionId != Types.TYPE_NOTYPE)
                else // invalid typecode
                {
                    this.addError ("' is an invalid typecode!");
                } // invalid typecode
            } // for (int i = 0; i < typeCodesArray.length; i++)

            // any typenames found?
            if (tVersionIdClause.length () == 0)
            {
                this.addError ("No valid typecodes found!");
                return null;
            } // if (typenamesClause.length () == 0)

            // valid typecode found
            return "(" + tVersionIdClause + ")";
        } // if (typenamesArray != null)

        // no typecodes defined
        this.addError ("No typecodes defined!");
        return null;
    } // getTVersionIdsClause


    /**************************************************************************
     * Create an string  that contains a list of tyecodes for an
     * ... IN (&lt;oid1>,&lt;oid2>,...) sql clause.<BR/>
     * The oid will be checked if valid.<BR/>
     *
     * @param   objOids A comma separated list with oids.
     *
     * @return  a sql query string containing ('&lt;oid1>','&lt;oid2>',...)
     */
    protected String getOIDsClause (String objOids)
    {
        String [] oidArray;
        StringBuffer oidsClause = new StringBuffer ();
        String comma = "";
        OID objOid;

        // constraint: any oids must be given
        if (objOids == null || objOids.isEmpty ())
        {
            return null;
        } // if (objOids == null || objOids.length() == 0)

        oidArray =
            StringHelpers.stringToStringArray (objOids, Reorg.OIDLIST_SEPARATOR);
        // did we get any oids?
        if (oidArray != null && oidArray.length > 0)
        {
            for (int i = 0; i < oidArray.length; i++)
            {
                this.addLog ("<LI>Verifying OID '" + oidArray [i] + "' ...");
                try
                {
                    objOid = new OID (oidArray [i].trim ());
                    this.addLog ("is valid.");
                        // add the oids
                    oidsClause.append (comma)
                        .append (objOid.toString ());
                    comma = Reorg.SQL_SEPARATOR;
                } // try
                catch (IncorrectOidException e)
                {
                    this.addError ("' is invalid: " + e.toString ());
                } // catch (IncorrectOidException e)
            } // for (int i = 0; i < typeCodesArray.length; i++)

            // any oids found?
            if (oidsClause.length () == 0)
            {
                this.addError ("No valid OIDs found!");
                return null;
            } // if (typenamesClause.length () == 0)

            // valid oids found
            return "(" + oidsClause + ")";
        } // if (typenamesArray != null)

        // no oids defined
        this.addLog ("No OIDs defined!");
        return null;
    } // getTVersionIdsClause


    /**************************************************************************
     * Takes a list of typecodes separated by a comma and creates a string
     * with a list of corresponding tVersionIds separated by a comma. <BR/>
     * This will be used as filter in the quer. <BR/>
     *
     * @param   typecodes  The string that contains the typecodes.
     *
     * @return  The string with the tversionIds or "" in case the
     *          typeFilter parameter was empty.
     */
    protected String getTypeIdsList (String typecodes)
    {
        int tVersionId         = 0;
        String tVersionIdList  = "";
        String delim           = "";

        // check if any value has been defined
        if (typecodes != null && typecodes.trim ().length () != 0)
        {
            StringTokenizer tokenizer;
            // tokenize the showTypes
            // according to the specification for the SELECTION typefilter
            // argument
            // the typenames in the showType are separated by a comma
            tokenizer = new StringTokenizer (typecodes, String.valueOf (Reorg.LIST_SEPARATOR));
            // loop through all the typenames
            while (tokenizer.hasMoreTokens ())
            {
                // get a typename
                String typeCode = tokenizer.nextToken ();
                // This type code can be used to get the
                // respective tVersionId from the object pool.
                // tVersionId will be Types.TYPE_NOTYPE in case
                // the type has not been found.
                tVersionId = this.getTypeCache ().getTVersionId (typeCode);

                if (tVersionId != TypeConstants.TYPE_NOTYPE)
                {
                    // add the tVersionId to the list of tVersionIds we want to
                    // look for
                    tVersionIdList += delim + tVersionId;
                    delim = DIConstants.TYPE_DELIMITER;
                } // if (tVersionId != Types.TYPE_NOTYPE)
            } // while (tokenizer.hasMoreTokens ())
        } // if (typecodes != null && typecodes.trim ().length () != 0)

        return tVersionIdList;
    } // getTypeIdsClause


    /**************************************************************************
     * Try to search an object by a key and with the given search parameters
     * from an OBJECTREF fiel. <BR/>
     *
     * @param   repObj          the object to be repaired
     * @param   searchObjectKey the search for the object key,
     *                          e.g. "o.oid = 0x0101....",
     *                          e.g. "o.name = 'the name'"
     * @param   searchRoot      the root path to search in
     * @param   searchRootIdDomain
     *                          the EXTKEY id domain of the root object search in
     * @param   searchRootId    the EXTKEY id of the root object search in
     * @param   searchRecursive option to activate recursive search
     * @param   typeCodeFilter  list of comma separated typecodes
     *
     * @return  The "oid, name" combination of the object.
     *          <CODE>null</CODE> if there occurred an error or the object
     *          was not found.
     */
    protected String searchObject (BusinessObject repObj,
                                   StringBuffer searchObjectKey,
                                   String searchRoot,
                                   String searchRootIdDomain,
                                   String searchRootId,
                                   boolean searchRecursive,
                                   String typeCodeFilter)
    {
        int rowCount;
        SQLAction action = null;        // the action object used to access the DB
        OID searchRootOid;
        StringBuffer queryStr;
        String result = null;
        OID objOid = null;
        String objName = "";

        // resolve the searchRoot:
        // check if a search root is defined via extKey
        if (searchRootIdDomain != null && !searchRootIdDomain.isEmpty () && searchRootId != null && !searchRootId.isEmpty ())
        {
            searchRootOid = BOHelpers.getOidByExtKey (searchRootIdDomain, searchRootId, this.env);
        } // if
        else
        {
            searchRootOid = BOHelpers.resolveObjectPath (searchRoot,
                    repObj.containerId, this, this.env);
        } // else

        if (searchRootOid == null)
        {
            this.env.write (
                IE302.TAG_NEWLINE + "<B>FEHLER</B>: SEARCHROOT='" + searchRoot +
                "' is invalid!!!"
            );
            return null;
        } // if (searchRootOid == null)

        // create the SQL String:
        queryStr = new StringBuffer ()
            .append (" SELECT o.oid, o.name")
            .append (" FROM ibs_Object o");
        // if there has to be a recursive search (posNoPath)
        // there has to be a join on ibs_object
        if (searchRecursive && (searchRootOid != null))
        {
            queryStr.append (", ibs_Object b");
        } // if ((searchRecursive == true) && (searchRootOid != null))
        queryStr
            .append (" WHERE ").append (searchObjectKey)
            .append (" AND o.state = ").append (States.ST_ACTIVE)
            .append (" AND o.isLink = ").append (SQLConstants.BOOL_FALSE);

        // add the type filter if not empty:
        if (typeCodeFilter != null && typeCodeFilter.length () > 0)
        {
            // showTypes must be transformed into a comma separated
            // list with tVersionIds
            String tVersionIdList = this.getTypeIdsList (typeCodeFilter);
            // if no valid type codes are specified set the list to '0'
            // to avoid a sql syntax error.
            if (tVersionIdList.length () == 0)
            {
                tVersionIdList = "0";
            } // if (tVersionIdList.length () == 0)
            // construct the where clause with the type filter
            queryStr
                .append (" AND o.tVersionId IN (")
                .append (tVersionIdList).append (")");
        } // if
        else                            // emtpy; no objecttype can be searched
        {
            this.env.write (IE302.TAG_NEWLINE + "<B>WARNING</B>: TYPECODEFILTER not set!");
        } // else empty; no object type can be searched

        // search only in the actual container:
        if (!searchRecursive)  // no recursive search?
        {
            queryStr
                .append (" AND o.containerId = ")
                .append (searchRootOid.toStringQu ());
        } // if no recursive search
        else                        // search in underlying folders too
        {
            // search all objects containing the posNoPath of the searchStart object
            queryStr
                .append (" AND b.oid = ")
                .append (searchRootOid.toStringQu ())
                .append (" AND ").append (SQLHelpers.getQueryConditionAttribute (
                    "o.posNoPath", SQLConstants.MATCH_STARTSWITH, "b.posNoPath", false));
        } // else search in underlying folders too

        try
        {
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);
            // is the result exactly one row?
            if (rowCount == 1)
            {
                objName = action.getString ("name");
                objOid =  SQLHelpers.getQuOidValue (action, "oid");
                result = objOid.toString () + "," + objName;
                // check if we have more than one result
                action.next ();
                if (!action.getEOF ())
                {
                    this.env.write (IE302.TAG_NEWLINE + "<B>WARNING</B>: More than one object found!");
                } // if (!action.getEOF ())
            } // if (rowCount == 1)
            // end transaction
            action.end ();
        } // try
        catch (DBError e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            this.releaseDBConnection (action);
        } // finally

        // return the result:
        return result;
    } // searchObject


    /**************************************************************************
     * Try to search an object by name and with the given search parameters
     * from an OBJECTREF fiel. <BR/>
     *
     * @param   repObj          the object to be repaired
     * @param   searchName      the name of the object
     * @param   searchRoot      the root path to search in
     * @param   searchRootIdDomain
     *                          the EXTKEY id domain of the root object search in
     * @param   searchRootId    the EXTKEY id of the root object search in
     * @param   searchRecursive option to activate recursive search
     * @param   typeCodeFilter  list of comma separated typecodes
     *
     * @return  The "oid, name" combination of the object.
     *          <CODE>null</CODE> if there occurred an error or the object
     *          was not found.
     */
    protected String searchObject (BusinessObject repObj,
                                   String searchName,
                                   String searchRoot,
                                   String searchRootIdDomain,
                                   String searchRootId,
                                   boolean searchRecursive,
                                   String typeCodeFilter)
    {
        // call common method and return the result:
        return this.searchObject (repObj,
            new StringBuffer ("o.name = '").append (searchName).append ("' "),
            searchRoot, searchRootIdDomain, searchRootId, searchRecursive, typeCodeFilter);
    } // searchObject


    /**************************************************************************
     * Try to search an object by oid and with the given search parameters
     * from an OBJECTREF fiel. <BR/>
     *
     * @param   repObj          the object to be repaired
     * @param   searchOid       the oid of the object
     * @param   searchRoot      the root path to search in
     * @param   searchRootIdDomain
     *                          the EXTKEY id domain of the root object search in
     * @param   searchRootId    the EXTKEY id of the root object search in
     * @param   searchRecursive option to activate recursive search
     * @param   typeCodeFilter  list of comma separated typecodes
     *
     * @return  The "oid, name" combination of the object.
     *          <CODE>null</CODE> if there occurred an error or the object
     *          was not found.
     */
    protected String searchObject (BusinessObject repObj,
                                   OID searchOid,
                                   String searchRoot,
                                   String searchRootIdDomain,
                                   String searchRootId,
                                   boolean searchRecursive,
                                   String typeCodeFilter)
    {
        // call common method and return the result:
        return this.searchObject (repObj,
            new StringBuffer ("o.oid = ").append (searchOid.toStringQu ()),
            searchRoot, searchRootIdDomain, searchRootId, searchRecursive, typeCodeFilter);
    } // searchObject


    /**************************************************************************
     * Check if an object has any subobjects. <BR/>
     *
     * @param   objOid      The oid of the object to be tested.
     * @param   typeName    Typename of the subobject. Optional.
     *
     * @return  <code>true</code> in case the object has any subobjects
     * or <code>false</code> otherwise
     */
    public boolean hasSubobjects (String objOid, String typeName)
    {
        int rowCount;                   // number of result rows
        SQLAction action = null;        // the action object used to access the DB
        boolean isFound = false;


        // create the SQL String to select the project order
        StringBuffer queryStr = new StringBuffer ()
            .append ("SELECT")
                .append (" o.oid")
            .append (" FROM")
                .append (" ibs_object o")
            .append (" WHERE ")
                .append ("     o.state = ").append (States.ST_ACTIVE)
                .append (" AND o.containerId = ").append (objOid);
        // any typename set?
        if (typeName != null)
        {
            queryStr.append (" AND o.typename = '").append (typeName).append ("'");
        } // if

        try
        {
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);
            // empty resultset?
            isFound = rowCount > 0;
            // end transaction
            action.end ();
        } // try
        catch (DBError e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            this.releaseDBConnection (action);
        } // finally

        // return the result
        return isFound;
    } // hasSubobjects


    /**************************************************************************
     * Retrieve the oids of several objects. <BR/>
     * This method just performs a query which must at least return an
     * attribute named oid of type OID and returns the result list. <BR/>
     *
     * @param   queryStr    The query to be performed.
     *
     * @return  The vector containing the resulting oids.
     *          If there was no tuple found the vector is empty.
     */
    public Vector<OID> retrieveOids (StringBuffer queryStr)
    {
        Vector<OID> oids = new Vector<OID> (10, 10); // the result vector
        int rowCount;                   // number of result rows
        SQLAction action = null;        // the action object used to access the DB

        try
        {
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);

            // empty resultset?
            if (rowCount > 0)
            {
                // get the oids
                while (!action.getEOF ())
                {
                    // get the oid and add it to the result vector:
                    oids.add (SQLHelpers.getQuOidValue (action, "oid"));
                    action.next ();
                } // while (!action.getEOF())
            } // if (rowCount > 0)

            // end transaction
            action.end ();
        } // try
        catch (DBError e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            this.releaseDBConnection (action);
        } // finally

        // return the result vector:
        return oids;
    } // retrieveOids


    /**************************************************************************
     * Print an "elapsed time" output showing a message with the ending date
     * and the calculated time and the average process time per object
     * if greater then . <BR/>
     *
     * @param   env         The current environment.
     * @param   msg         The message to be printed showing the ending date.
     * @param   startDate   The starting date.
     * @param   endDate     The ending date.
     * @param   objCounter  The number of objects processed.
     */
    public void showElapsedTime (Environment env, String msg,
                                 Date startDate, Date endDate, int objCounter)
    {
        Date checkedEndDate = endDate;

        // any endDate set?
        if (checkedEndDate == null)
        {
            // set endDate to NOW
            checkedEndDate = new Date ();
        } // if (endDate == null)

        // should a message be printed with the enddate?
        if (msg != null)
        {
            this.addLog ("<BR/>" + msg + " at " +
                DateTimeHelpers.dateTimeToString (checkedEndDate) + ".");
        } // if (msg != null)
        this.addLog ("<BR/>Overall time: ");
        long milliseconds = checkedEndDate.getTime () - startDate.getTime ();
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds -= minutes * 60;
        this.addLog (minutes + " Minute(s) " + seconds + " second(s).");
        if (objCounter > 0)
        {
            this.addLog ("<BR/>" + objCounter + " Object(s) processed.");
            this.addLog ("<BR/>Average time per object: " +
                (milliseconds / objCounter) + " milliseconds.");
        } // if (objCounter > 0)
        this.addLog (IE302.TAG_NEWLINE);
    } // showElapsedTime


    /**************************************************************************
     * Display the n error statistic
     */
    protected void showErrorStatistics ()
    {
        String [] msgData;

        // set a javascript variable to indicate that the page is shown after a reorg
/*
        StringBuffer jsCode = new StringBuffer ("<SCRIPT LANGUAGE=\"Javascript\">")
            .append (" function go (elemId)")
            .append (" {")
            .append ("   document.getElementById (elemId).scrollIntoView (true); ")
            .append (" }")
            .append ("</SCRIPT>");
//        this.env.write (jsCode.toString ());
*/

        this.addLog ("<BR/><INPUT TYPE=\"HIDDEN\" ID=\"stat\"/>&gt;&gt;&gt; Summary:");

        // any errors found?
        if (this.p_errors != null && this.p_errors.size () > 0)
        {
            this.addLog ("<BR/>" + this.p_errors.size () + " error(s) found:");
            // loop through all date controls:
            for (Iterator<String[]> iter = this.p_errors.iterator (); iter.hasNext ();)
            {
                msgData = iter.next ();
                this.addLog ("<LI/><A HREF=\"javascript:go(" + msgData [0] + ")\">[" +
                    msgData [0] + "] " + msgData [1] + "</A>");
            } // for (Iterator iter = this.p_errors.iterator (); iter.hasNext ())
        } // if (this.p_errors != null && this.p_errors.size() > 0)
        else // no errors found
        {
            this.addLog ("<BR/>No error.");
        } // else no errors found

        // any conflicts found?
        if (this.p_conflicts != null && this.p_conflicts.size () > 0)
        {
            this.addLog ("<BR/>" + this.p_conflicts.size () + " conflict(s) found:");
            // loop through all date controls:
            for (Iterator<String[]> iter = this.p_conflicts.iterator (); iter.hasNext ();)
            {
                msgData = iter.next ();
                this.addLog ("<LI/><A HREF=\"javascript:go(" + msgData [0] + ")\">[" +
                    msgData [0] + "] " + msgData [1] + "</A>");
            } // for (Iterator<String[]> iter = this.p_conflicts.iterator (); iter.hasNext ();)
        } // if (this.p_conflicts != null && this.p_conflicts.size() > 0)
        else // no conflicts found
        {
            this.addLog ("<BR/>No conflicts.");
        } // else no conflicts found

        // any warnings found?
        if (this.p_warnings != null && this.p_warnings.size () > 0)
        {
            this.addLog ("<BR/>" + this.p_warnings.size () + " warning(s) found:");
            // loop through all date controls:
            for (Iterator<String[]> iter = this.p_warnings.iterator (); iter.hasNext ();)
            {
                msgData = iter.next ();
                this.addLog ("<LI/><A HREF=\"javascript:go(" + msgData [0] + ")\">[" +
                    msgData [0] + "] " + msgData [1] + "</A>");
            } // for (Iterator<String[]> iter = this.p_warnings.iterator (); iter.hasNext ();)
        } // if (this.p_warnings != null && this.p_warnings.size() > 0)
        else // no warnings found
        {
            this.addLog ("<BR/>No warnings.");
        } // no warnings found

        this.addLog (IE302.TAG_NEWLINE);
    } // showErrorStatistics


    /**************************************************************************
     * Display the footer of a reorg function.<BR/>
     *
     * @param   isSimulation    Simulation mode on?
     * @param   title           The tile.
     *
     */
    protected void showReorgHeader (boolean isSimulation, String title)
    {
        // set the global start date
        this.p_startDate = new Date ();

        // creates HTML Header for output
        this.createHTMLHeader (this.app, this.sess, this.env);

        this.addLog (IE302.TAG_NEWLINE);
        this.addLog ("<DIV ALIGN=\"LEFT\">");
        this.addLog ("<FONT SIZE=\"2\">");

        this.addLog (IE302.HCH_GT + IE302.HCH_GT + IE302.HCH_GT + IE302.HCH_NBSP);
        if (isSimulation)
        {
            this.addLog ("Simulation of ");
        } // if (isSimulation)
        this.addLog (title + " started at " +
            DateTimeHelpers.dateTimeToString (this.p_startDate));

        this.addLog (IE302.TAG_NEWLINE);
        this.addLog (IE302.TAG_NEWLINE);
    } // showReorgHeader


    /**************************************************************************
     * Display the footer of a reorg function showing the time elapsed
     * and the error statistics. <BR/>
     *
     * @param   isSimulation    Simulation mode on?
     * @param   title           The tile.
     */
    protected void showReorgFooter (boolean isSimulation, String title)
    {
        // display the footer with the objCounter property
        this.showReorgFooter (isSimulation, title, this.p_objCounter);
    } // showReorgFooter


    /**************************************************************************
     * Display the header of a reorg function.<BR/>
     *
     * @param   isSimulation    Simulation mode on?
     * @param   title           The tile.
     * @param   objCounter      The number of objects processed.
     */
    protected void showReorgFooter (boolean isSimulation, String title, int objCounter)
    {
        this.addLog (IE302.TAG_NEWLINE);
        // show the elapsed time
        this.showElapsedTime (this.env,
            isSimulation ? "Simulation " : title  + " finished",
            this.p_startDate, null, objCounter);

        // display error statistics
        this.showErrorStatistics ();

        this.addLog ("</FONT></DIV>");
        this.addLog (IE302.TAG_NEWLINE);

        // creates HTML Footer for output
        this.createHTMLFooter (this.env);
    } // showReorgFooter


    /**************************************************************************
     * Get all objects that are result of the query with the given
     * filter. <BR/>
     *
     * @param   tVersionIdFilter The sql clause with a tversionID filter.
     * @param   oidFilter       The sql clause with a oid filter.
     * @param   otherSQLClause  Another sql clause to append to the query.
     *
     * @return  The data in a Vector containing arrays having . <BR/>
     * arrayindex 0: the oid from the que. <BR/>
     * arrayindex 1: the name from the que. <BR/>
     * arrayindex 2: the typename from the que. <BR/>
     *          Empty if no objects where found. <BR/>
     *          <CODE>null</CODE> if there occurred an error.
     */
    public Vector<BusinessObjectInfo> getObjects (String tVersionIdFilter,
                                        String oidFilter, String otherSQLClause)
    {
        // call common method and return the result:
        return BOHelpers.findObjects (
            new StringBuffer () .append (States.ST_ACTIVE),
            new StringBuffer ().append (tVersionIdFilter), null,
            new StringBuffer ("o.isLink = 0"), this.env);
    } // getObjects


    /**************************************************************************
     * Construct a sql filter string that can be used in a LIKE operation
     * with a given string, a placeholder character and a sql placeholder
     * characte. <BR/>
     *
     * @param   str         the string
     * @param   placeholder the placeholder character to be replaced
     *                      by the sql placeholder character
     * @param   sqlPlaceholder the sql placeholder character
     *
     * @return  The number of errors which occurred during the migration.
     */
    public static StringBuffer getSQLFilter (String str,
         String placeholder,
         String sqlPlaceholder)
    {
        StringBuffer filter = new StringBuffer ();

        if (str != null && !str.isEmpty ())
        {
            filter.append (StringHelpers.replace (str, placeholder,
                sqlPlaceholder));
        } // if (str != null && !str.equals (""))
        else
        // no filter set
        {
            // only append the placeholder
            filter.append (sqlPlaceholder);
        } // else no filter set
        // return the resulting filter
        return filter;
    } // getSQLFilter

} // class Reorg

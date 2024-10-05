/*
 * Class: Catalog_01.java
 */

// package:
package m2.store;

// imports:
import ibs.app.AppConstants;
import ibs.app.AppFunctions;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOMessages;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.Datatypes;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.SelectionList;
import ibs.bo.States;
import ibs.bo.type.TypeConstants;
import ibs.di.DIConstants;
import ibs.di.DIHelpers;
import ibs.di.DITokens;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.GroupElement;
import ibs.tech.html.IE302;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.tech.http.HttpArguments;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.util.FormFieldRestriction;

import java.util.Vector;


/******************************************************************************
 * This class represents one container object of type Store with version 01.
 * <BR/>
 *
 * @version     $Id: Catalog_01.java,v 1.37 2013/01/16 16:14:12 btatzmann Exp $
 *
 * @author      Bernhard Walter (BW), 980926
 ******************************************************************************
 */
public class Catalog_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Catalog_01.java,v 1.37 2013/01/16 16:14:12 btatzmann Exp $";

    /**
     * Headings of columns. <BR/>
     * These headings are shown at the top of lists.
     */
    public static final String [] LST_HEADINGS_CATALOG =
    {
        BOTokens.ML_NAME,
        BOTokens.ML_DESCRIPTION,
        BOTokens.ML_TYPE,
        BOTokens.ML_CHANGED,
    }; // LST_HEADINGS_CATALOG

    /**
     * Reduced headings of columns. <BR/>
     * These headings are shown at the top of lists.
     */
    public static final String [] LST_HEADINGS_CATALOG_REDUCED =
    {
        Catalog_01.LST_HEADINGS_CATALOG[0],
        Catalog_01.LST_HEADINGS_CATALOG[1],
    }; // LST_HEADINGS_CATALOG_REDUCED

    /**
     * Name of a container column. <BR/>
     * These attributes are used for ordering the elements.
     */
    public static final String [] LST_ORDERINGS_CATALOG =
    {
        Catalog_01.FIELD_NAME,
        Catalog_01.FIELD_DESCRIPTION,
        "typeName",
        "lastChanged",
    }; // LST_ORDERINGS_CATALOG

    /**
     * Name of a container column. <BR/>
     * These attributes are used for ordering the elements.
     */
    public static final String [] LST_ORDERINGS_CATALOG_REDUCED =
    {
        Catalog_01.LST_ORDERINGS_CATALOG[0],
        Catalog_01.LST_ORDERINGS_CATALOG[1],
    }; // LST_ORDERINGS_CATALOG_REDUCED

    
    ///////////////////////////////////////////////////////////////////////////
    // properties
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Medium for default distribution. <BR/>
     */
    private static final int MEDIUM_DISTRIBUTE = 1;

    /**
     * Id of firm responsible for the catalog. <BR/>
     */
    private OID companyOid = null;

    /**
     * firm responsible for the catalog. <BR/>
     */
    public String company = "";

   /**
     * Oid of the person responsible for the ordering. <BR/>
     */
    protected OID ordRespOid = null;

    /**
     * Name of the person responsible for the ordering. <BR/>
     */
    private String ordRespName = "";

    /**
     * Oid of the medium the ordering is done through. <BR/>
     */
    protected OID ordRespMedOid = null;

    /**
     * Name of the medium the ordering is send through. <BR/>
     */
/*
    private String ordRespMedName = "";
*/

    /**
     * Oid of the user, group or person responsible for the contens of this
     * catalog. <BR/>
     */
    private OID contResp = null;

    /**
     * Name of the user, group or person responsible for the contens of this
     * catalog. <BR/>
     */
    private String contRespName = "";

    /**
     * Oid of the medium the contens responsible can be reached through. <BR/>
     */
    private OID contRespMed = null;

    /**
     * Name of the medium the contens responsible can be reached through. <BR/>
     */
/*
    private String contRespMedName = "";
*/

    /**
     * Is the catalog locked. <BR/>
     */
    private boolean locked = false;

    /**
     * This string contains a description how the supplier handles the order
     * if not all products in the order are available. <BR/>
     */
    public String deliveryNotPossibleHandling = "";

    /**
     * The string describes the way how the order is shipped. <BR/>
     */
    public String shippmentOrder = "";

    /**
     * flag to use export when creating an order in this catalog. <BR/>
     */
    public boolean isOrderExport = false;

    /**
     * OID of the connector to be used for order export. <BR/>
     */
    public OID connectorOid = null;

    /**
     * name of the connector to be used for order export. <BR/>
     */
    public String connectorName = "";

    /**
     * OID of the translator to be used for order export. <BR/>
     */
    public OID translatorOid = null;

    /**
     * Name of the translator to be used for order export. <BR/>
     */
    public String translatorName = "";

    /**
     * ID of the filter to be used. <BR/>
     */
    public int filterId = 0;

    /**
     * true if orderresponsible should be notify by email
     * when getting one.
     */
    public boolean notifyByEmail = false;

    /**
     *  subject of Email
     */
    public String subject = null;

    /**
     *  content of Email
     */
    public String content = null;

    /**
     * List of all payment types in this catalog
     */
    protected SelectionList p_paymentTypes = null;

    /**
     * Field name: name. <BR/>
     */
    private static final String FIELD_NAME = "name";
    /**
     * Field name: description. <BR/>
     */
    private static final String FIELD_DESCRIPTION = "description";



    /**************************************************************************
     * This constructor creates a new instance of the class Store.
     * <BR/>
     */
    public Catalog_01 ()
    {
        // call constructor of super class BussinesObject:
        super ();
    } // Catalog_01


    /**************************************************************************
     * This constructor creates a new instance of the class Store.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Catalog_01 (OID oid, User user)
    {
        // call constructor of super class BussinesObject:
        super (oid, user);
    } // Catalog_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the instance's attributes:
        this.procCreate =     "p_Catalog_01$create";
        this.procChange =     "p_Catalog_01$change";
        this.procRetrieve =   "p_Catalog_01$retrieve";
        this.procDelete =     "p_Catalog_01$delete";

        // initialize paymenttypes - selectionlist
        this.p_paymentTypes = new SelectionList ();

        this.elementClassName = "m2.store.CatalogElement_01";
        // the change view shall be shown as frameset:
        this.showChangeFormAsFrameset = true;

        // set relevant attributes for second frame (show emptypage first)
        this.frm2Url = AppConstants.FILE_EMPTYPAGE;
        this.frm2Function = AppFunctions.FCT_NOFUNCTION;

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 22;
        this.specificChangeParameters = 15;
    } // initClassSpecifics


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
        // set the specific parameters:
        // companyOid
        BOHelpers.addInParameter (sp, this.companyOid);
        // oid of the order responsible
        BOHelpers.addInParameter (sp, this.ordRespOid);
        // oid of the medium a order is done
        BOHelpers.addInParameter (sp, this.ordRespMedOid);

        // oid of the contens responsible
        BOHelpers.addInParameter (sp, this.contResp);
        // oid of the medium the contens responsible is reached
        BOHelpers.addInParameter (sp, this.contRespMed);
        // locked
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
                        this.locked);
        // deliveryNotPossibleHandling
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.deliveryNotPossibleHandling);
        // shippmentOrder
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.shippmentOrder);
        // isOrderExport
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
                        this.isOrderExport);
        // connectorOid
        BOHelpers.addInParameter (sp, this.connectorOid);
        // translatorOid
        BOHelpers.addInParameter (sp, this.translatorOid);
        // filterId
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                        this.filterId);
// HACK
// ME
        // notifyByEmail
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
                        this.notifyByEmail);
        // subject
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.subject);
        // content
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.content);
// ME
// HACK
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

        // set the specific parameters:
        // companyOid
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // company
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // oid of the order responsible
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // name of the order responsible
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // oid of the medium a order is done
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // name of the medium a order is done
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // oid of the contens responsible
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // name of the contens responsible
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // oid of the medium the contens responsible can be reached
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // name of the medium the coNtens responsible can be reached
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // locked
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // description1
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // description2
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // isOrderExport
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // connectorOid
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // connectorName
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // translatorOid
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // translatorName
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // filterId
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

        // notfyByEmail
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // subject
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // content
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

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
     * @param   lastIndex   The index to the last element used in params thus
     *                      far.
     */
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // get the specific parameters:
        this.companyOid     = SQLHelpers.getSpOidParam (params[++i]);
        this.company        = params[++i].getValueString ();
        this.ordRespOid     = SQLHelpers.getSpOidParam (params[++i]);
        if (this.ordRespOid != null && this.ordRespOid.isEmpty ())
        {
            this.ordRespOid = null;
        } // if
        this.ordRespName    = params[++i].getValueString ();
        this.ordRespMedOid  = SQLHelpers.getSpOidParam (params[++i]);
/*
        this.ordRespMedName = params[++i].getValueString ();
*/
        ++i;                            // currently not used
        this.contResp       = SQLHelpers.getSpOidParam (params[++i]);
        this.contRespName   = params[++i].getValueString ();
        this.contRespMed    = SQLHelpers.getSpOidParam (params[++i]);
/*
        this.contRespMedName = params[++i].getValueString ();
*/
        ++i;                            // currently not used
        this.locked = params[++i].getValueBoolean ();
        this.deliveryNotPossibleHandling = params[++i].getValueString ();
        this.shippmentOrder = params[++i].getValueString ();
        this.isOrderExport  = params[++i].getValueBoolean ();
        this.connectorOid   = SQLHelpers.getSpOidParam (params[++i]);
        this.connectorName  = params[++i].getValueString ();
        this.translatorOid  = SQLHelpers.getSpOidParam (params[++i]);
        this.translatorName = params[++i].getValueString ();
        this.filterId       = params[++i].getValueInteger ();
        this.notifyByEmail  = params[++i].getValueBoolean ();
        this.subject        = params[++i].getValueString ();
        this.content        = params[++i].getValueString ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Set the headings for this container. This method MUST be overloaded if
     * you have your own subclass of ContainerElement and if you need other
     * headings. <BR/>
     * You have to overload the method setOrderings () as well.
     */
    protected void setHeadingsAndOrderings ()
    {
        if (!this.getUserInfo ().userProfile.showExtendedAttributes)
            // show the exetended attributes
        {
            // set headings
            this.headings = MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE, 
                Catalog_01.LST_HEADINGS_CATALOG_REDUCED, env);

            // set orderings
            this.orderings = Catalog_01.LST_ORDERINGS_CATALOG_REDUCED;
        } // if
        else
        {
            // set headings
            this.headings = MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE, 
                Catalog_01.LST_HEADINGS_CATALOG, env);

            // set orderings
            this.orderings = Catalog_01.LST_ORDERINGS_CATALOG;        
        } // else

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings


    ///////////////////////////////////////////////////////////////////////////
    // functions called from application level
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="#env">env</A> property is used for getting the parameters.
     * This property must be set before calling this method. <BR/>
     */
    public void getParameters ()
    {
        String str;
        String[] strArr = null;
        int num = 0;
        OID oid = null;

        // get parameters of super class:
        super.getParameters ();

        // locked
        if ((num = this.env.getBoolParam (StoreArguments.ARG_CAT_LOCKED)) >=
            IOConstants.BOOLPARAM_FALSE)
        {
            this.locked = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        // supplier
        if ((oid = this.env.getOidParam (StoreArguments.ARG_CAT_COMPANY +
                                    BOArguments.ARG_OID_EXTENSION)) != null)
        {
            this.companyOid = oid;
        } // if

        // catalog content responsible
        if ((oid = this.env.getOidParam (StoreArguments.ARG_CAT_CONTRESP +
                                    BOArguments.ARG_OID_EXTENSION)) != null)
        {
            this.contResp = oid;
        } // if

        // order responsible
        if ((oid = this.env.getOidParam (StoreArguments.ARG_CAT_ORDRESP +
                                    BOArguments.ARG_OID_EXTENSION)) != null)
        {
            this.ordRespOid = oid;
        } // if

        // shippment
        if ((str = this.env.getStringParam (StoreArguments.ARG_SHIPPMENT)) != null)
        {
            this.shippmentOrder = str;
        } // if

        // delivery cond
        if ((str = this.env.getStringParam (StoreArguments.ARG_NOTAVAILABLE)) != null)
        {
            this.deliveryNotPossibleHandling = str;
        } // if

        // get isOrderExport flag
        if ((num = this.env.getBoolParam (StoreArguments.ARG_ISORDEREXPORT)) >=
            IOConstants.BOOLPARAM_FALSE)
        {
            this.isOrderExport = num == IOConstants.BOOLPARAM_TRUE;
        } // if ((num = env.getBoolParam (StoreArguments.ARG_ISORDEREXPORT)) >= IOConstants.BOOLPARAM_FALSE)

        // get connector oid
        if ((oid = this.env.getOidParam (StoreArguments.ARG_CONNECTOR)) != null)
        {
            this.connectorOid = oid;
        } // if ((oid = env.getOidParam (StoreArguments.ARG_CONNECTOR)) != null)

        // get translator oid
        if ((oid = this.env.getOidParam (StoreArguments.ARG_TRANSLATOR)) != null)
        {
            this.translatorOid = oid;
        } // if ((oid = env.getOidParam (StoreArguments.ARG_TRANSLATOR)) != null)

        // get filter id
        if ((num = this.env.getIntParam (StoreArguments.ARG_FILTER)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.filterId = num;
        } // if ((num = env.getIntParam (StoreArguments.ARG_FILTER)) != IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)

        // send email notification
        if ((num = this.env.getBoolParam ("notify")) >= IOConstants.BOOLPARAM_FALSE)
        {
            this.notifyByEmail = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        // subject
        if ((str = this.env.getStringParam ("subj")) != null)
        {
            this.subject = str;
        } // if

        // content
        if ((str = this.env.getStringParam ("cont")) != null)
        {
            this.content = str;
        } // if

        // allowed payments
        if ((strArr = this.env
            .getMultipleFormParam (StoreArguments.ARG_ALLOWED_PAYMENTS)) != null)
        {

            this.p_paymentTypes.ids = strArr;
        } // if allowed payments
        else
        {
            // WORKAROUND - check if payment type field was displayed and no
            // payment type was selected
            if ((str = this.env
                .getStringParam (StoreArguments.ARG_ALLOWED_PAYMENTS + "Help")) != null)
            {
                // payment type field was displayed, but no payment type was
                // selected - set payment array to null, then the old
                // payment types will be deleted
                this.p_paymentTypes.ids = null;
                this.p_paymentTypes.values = null;
            } // if WORKAROUND
        } // else payment field returns null
    } // getParameters


    /**************************************************************************
     * Change all type specific data that is not changed by performChangeData.
     * <BR/>
     * This method must be overwritten by all subclasses that have to change
     * type specific data.
     *
     * @param   action  The action object associated with the connection.
     *
     * @throws  DBError
     *          This exception is always thrown, if there happens
     *          an error during accessing data.
     */
    protected void performChangeSpecificData (SQLAction action) throws DBError
    {
        int i;
        StringBuffer queryStr;          // the query string
        String methodName = "Catalog_01.performChangeSpecificData";

        //-----------------------------------------------------------------
        // first delete all entries
        // room for some success statements

        // if the product profile was just created, there are no categories
        // to delete -> skip the delete categories step
        if (this.state != States.ST_CREATED)
        {
            queryStr = new StringBuffer ()
                .append (" DELETE FROM  m2_CatalogPayments")
                .append (" WHERE catalogOid = ").append (this.oid.toStringQu ());

            // execute the queryString, indicate that we're not performing
            // an action query:
            try
            {
                action.execute (queryStr, true);
            } // try
            catch (DBError dbErr)
            {
                this.env.write (queryStr.toString ());
                // an error occurred - show name and info
                IOHelpers.showMessage (methodName,
                    dbErr, this.app, this.sess, this.env, false);
            } // catch
        } // if (this.state != States.ST_CREATED)

        // then insert the new tuples
        // room for some success statements

        if (this.p_paymentTypes != null && this.p_paymentTypes.ids != null &&
            this.p_paymentTypes.ids.length != 0)
        {
            StringBuffer sb = new StringBuffer (1024);
            // add the payment types
            for (i = 0; i < this.p_paymentTypes.ids.length; i++)
            {
                try
                {
                    sb.append (new OID (this.p_paymentTypes.ids[i])
                        .toStringQu ());
                } // try
                catch (IncorrectOidException e)
                {
                    // should not occur
                    throw new DBError (e);
                } // catch

                // add separator:
                if (i != this.p_paymentTypes.ids.length - 1)
                {
                    sb.append (",");
                } // if
            } // for add the payment types

            queryStr = new StringBuffer ()
                .append (" INSERT INTO m2_CatalogPayments (catalogOid, paymentOid)")
                .append (" SELECT ").append (this.oid.toStringQu ()).append (",oid")
                .append (" FROM ibs_Object")
                .append (" WHERE oid IN (").append (sb).append (")");

            // execute the queryString, indicate that we're performing
            // an action query:
            try
            {
                action.execute (queryStr, true);
            } // try
            catch (DBError dbErr)
            {
                this.env.write (queryStr.toString ());
                // an error occurred - show name and info
                IOHelpers.showMessage (methodName, dbErr, this.app, this.sess,
                    this.env, true);
            } // catch
        } // if then insert the new tuples room for some success statements
    } // performChangeSpecificData


    /**************************************************************************
     * Retrieve the type specific data that is not got from the stored
     * procedure. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * type specific data that cannot be got from the retrieve data stored
     * procedure.
     *
     * @param   action  The action object associated with the connection.
     *
     * @throws  DBError
     *          This exception is always thrown, if there happens
     *          an error during accessing data.
     */
    protected void performRetrieveSpecificData (SQLAction action) throws DBError
    {
        Vector<String> ids = new Vector<String> (); // initialize elements vector
        Vector<String> values = new Vector<String> (); // initialize elements vector
        StringBuffer queryStr;          // the query string

        // create the SQL String to select assigned payment types
        queryStr = new StringBuffer ()
            .append ("SELECT cp.paymentOid, o.name")
            .append (" FROM  m2_CatalogPayments cp, ibs_Object o")
            .append (" WHERE o.oid = cp.paymentOid")
                .append (" AND cp.catalogOid = ").append (this.oid.toStringQu ())
                .append (" AND o.state = ").append (States.ST_ACTIVE);

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            int rowCount = action.execute (queryStr, false);

            // no valid tuples
            if (rowCount <= 0)
            {
                return;
            } // if

            // get tuples out of db
            while (!action.getEOF ())
            {
                // get the parameters out of the database into the
                // SelectionList paymentTypes
                ids.addElement (
                    SQLHelpers.getQuOidValue (action, "paymentOid").toString ());

                values.addElement (action.getString (Catalog_01.FIELD_NAME));

                // step one tuple ahead for the next loop
                action.next ();
            } // while get tuples out of db

            // copy values of the vector to an array
            this.p_paymentTypes.ids = new String[ids.size ()];
            this.p_paymentTypes.values = new String[values.size ()];
            ids.copyInto (this.p_paymentTypes.ids);
            values.copyInto (this.p_paymentTypes.values);

            // the last tuple has been processed
            // end transaction
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            this.env.write (queryStr.toString ());
            // an error occurred - show name and info
            IOHelpers.showMessage ("Catalog_01.performRetrieveSpecificData",
                                   dbErr, this.app, this.sess, this.env, true);
        } // catch
    } // performRetrieveSpecificData


    /**************************************************************************
     * Represent the properties of a Catalog_01 object to the user. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showProperty
     */
    protected void showProperties (TableElement table)
    {
//showDebug ("--- START showProperties ---");
        // display the base object's properties:
        super.showProperties (table);

        StringBuffer sb = new StringBuffer (50);
                                        // Buffer for the payment types

        // show Catalog_01 specific attributes
        this.showProperty (table, StoreArguments.ARG_CAT_COMPANY,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_CAT_COMPANY, env),
            Datatypes.DT_TEXT, this.company);
        this.showProperty (table, StoreArguments.ARG_CAT_CONTRESP,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_CAT_CONTRESP, env),
            Datatypes.DT_TEXT, this.contRespName);
        this.showProperty (table, StoreArguments.ARG_CAT_ORDRESP,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_CAT_ORDRESP, env), 
            Datatypes.DT_TEXT, this.ordRespName);
        this.showProperty (table, StoreArguments.ARG_SHIPPMENT,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_SHIPPMENT, env),
            Datatypes.DT_DESCRIPTION, this.shippmentOrder);
        this.showProperty (table, StoreArguments.ARG_NOTAVAILABLE,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_NOTAVAILABLE, env), 
            Datatypes.DT_DESCRIPTION, this.deliveryNotPossibleHandling);

        // payment type
        if (this.p_paymentTypes != null && this.p_paymentTypes.values != null)
        {
            // add the payment types
            for (int i = 0; i < this.p_paymentTypes.values.length; i++)
            {
                sb.append (this.p_paymentTypes.values[i]);
                sb.append (IE302.TAG_NEWLINE);
            } // for add the payment types
        } // if payment type

        this.showProperty (table, StoreArguments.ARG_ALLOWED_PAYMENTS,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_ALLOWED_PAYMENTS, env),
            Datatypes.DT_DESCRIPTION, sb.toString ());

        // display a separator line
        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);

        // isOrderExport
        this.showProperty (table, StoreArguments.ARG_ISORDEREXPORT,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_ISORDEREXPORT, env),
            Datatypes.DT_BOOL, "" + this.isOrderExport);
        // connector

        this.showProperty (table, StoreArguments.ARG_CONNECTOR,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_CONNECTOR, env),
            Datatypes.DT_LINK, this.connectorName, this.connectorOid);
        // translator
        this.showProperty (table, StoreArguments.ARG_TRANSLATOR,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_TRANSLATOR, env),
            Datatypes.DT_LINK, this.translatorName, this.translatorOid);

        // filter
        this.showProperty (table, StoreArguments.ARG_FILTER,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_FILTER, env),
            Datatypes.DT_TEXT,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DIConstants.EXPORTFILTER_NAMES[this.filterId], env));

        // if order responsible should be notified by email
        // notify by email
        this.showProperty (table, "notify", 
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_MAIL_SENDMAIL, env),
            Datatypes.DT_BOOL, "" + this.notifyByEmail);

        if (this.notifyByEmail)
        {
            // subject
            this.showProperty (table, "subj", 
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_MAIL_SUBJECT, env),
                Datatypes.DT_DESCRIPTION, this.subject);

            // content
            this.showProperty (table, "cont", 
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_MAIL_CONTENT, env),
                Datatypes.DT_DESCRIPTION, this.content);
        } // if notifyByEmail
    } // showProperties


    /***************************************************************************
     * Get a selectionList of paymenttypes without checking the rights. <BR/> A
     * selectionList (id,name) will be retrieved for paymenttypes. <BR/>
     *
     * @return The selection list.
     */
    protected SelectionList performRetrievePaymentTypeList ()
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        SelectionList selList = new SelectionList (); // resulting SelectionList
        int rowCount;                   // row counter
        Vector<String> ids = new Vector<String> (); // initialize elements vector
        Vector<String> values = new Vector<String> (); // initialize elements vector
        StringBuffer queryStr;          // the query string

        // get all paymenttypes of current domain out of the database:
        // create the SQL String to select all tuples
        // create querystring get all oids and related domainIds of all
        // querytemplates in db
        queryStr = new StringBuffer ()
            .append (" SELECT o.oid, o.name AS name, dom.id")
            .append (" FROM ibs_Object o,")
            .append ("      ibs_domain_01 dom, ibs_Object odom")
            .append (" WHERE o.tVersionId =")
                .append (this.getTypeCache ().getTVersionId (
                    StoreTypeConstants.TC_PaymentType))
            .append ("   AND o.state = ").append (States.ST_ACTIVE)
            .append ("   AND odom.oid = dom.oid")
            .append ("   AND dom.id = ").append (this.user.domain)
            .append (" AND ").append (SQLHelpers.getQueryConditionAttribute (
                "o.posNoPath", SQLConstants.MATCH_STARTSWITH, "odom.posNoPath",
                false))
            .append (" ORDER BY name ").append (BOConstants.ORDER_ASC);
//debug ("Query: " + queryStr);

        // start index in ids and values - array
        int start = 0;

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);

            // empty resultset?
            if (rowCount == 0)
            {
                selList.ids = new String[0];
                selList.values = new String[0];
            } // if empty resultset?
            // rowcount
            if (rowCount > 0)
            {
                // get tuples out of db
                while (!action.getEOF ())
                {
                    // get and set values for element:
                    ids.addElement (SQLHelpers.getQuOidValue (action, "oid").toString ());
                    values.addElement (action.getString (Catalog_01.FIELD_NAME));

                    // step one tuple ahead for the next loop
                    action.next ();
                } // while get tuples out of db

                // the last tuple has been processed
                // end transaction
                action.end ();

                // set real rowCount of result set
                rowCount = ids.size ();

                // create return array of right size
                selList.ids = new String [ids.size () + start];
                selList.values = new String [ids.size () + start];

                // declare row index for copy into String arrays
                int i = 0;

                // add row
                while (i < rowCount)
                {
                    selList.ids [i + start] = ids.elementAt (i);
                    selList.values [i + start] = values.elementAt (i);
                    i++;
                } // while add row
            } //if rowcount
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage ("Catalog_01.performRetrievePaymentTypeList",
                                   e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround -
            // db connection must be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        return selList;
    } // performRetrievePaymentTypeList


    /**************************************************************************
     * Represent the properties of a Catalog_01 object to the user
     * within a form. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showFormProperty
     */
    protected void showFormProperties (TableElement table)
    {
//showDebug ("--- START showFormProperties ---");
        // loop through all properties of this object and display them:
        super.showFormProperties (table);

        // a list which contents all payment types which are stored on the
        // data base in the table m2_PaymentType_01
        SelectionList allPaymentTypes = this.performRetrievePaymentTypeList ();

        // searchtext-field for company
        this.showFormProperty (table, StoreArguments.ARG_CAT_COMPANY,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_CAT_COMPANY, env), 
            Datatypes.DT_SEARCHTEXTFUNCTION, this.company, "" + this.companyOid, 
            this.getBaseUrl () +
                HttpArguments.createArg (BOArguments.ARG_FUNCTION,
                    AppFunctions.FCT_SHOWOBJECTCONTENT) +
                HttpArguments.createArg (BOArguments.ARG_OID, new OID (this
                    .getTypeCache ().getTVersionId (
                        StoreTypeConstants.TC_SelectCompanyContainer), 0).toString ())
//                + AppArguments.ARG_SEP + AppArguments.ARG_CALLINGOID + AppArguments.ARG_ASSIGN + this.oid
            , HtmlConstants.FRM_SHEET2);

        // searchtext-field for catalog-responsible
        this.showFormProperty (table, StoreArguments.ARG_CAT_CONTRESP,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_CAT_CONTRESP, env), Datatypes.DT_SEARCHTEXTFUNCTION,
            this.contRespName, "" + this.contResp, this.getBaseUrl () +
                HttpArguments.createArg (BOArguments.ARG_FUNCTION,
                    AppFunctions.FCT_SHOWOBJECTCONTENT) +
                HttpArguments.createArg (BOArguments.ARG_OID, new OID (this
                    .getTypeCache ().getTVersionId (
                        StoreTypeConstants.TC_SelectUserContainer), 0).toString ())
//                + AppArguments.ARG_SEP + AppArguments.ARG_CALLINGOID + AppArguments.ARG_ASSIGN + this.oid
            , HtmlConstants.FRM_SHEET2);

        String ordRespStr = "";
        if (this.ordRespOid != null)
        {
            ordRespStr += this.ordRespOid;
        } // if

        // searchtext-field for order-responsible
        this.showFormProperty (table, StoreArguments.ARG_CAT_ORDRESP,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_CAT_ORDRESP, env), Datatypes.DT_SEARCHTEXTFUNCTION,
            this.ordRespName, "" + ordRespStr, this.getBaseUrl () +
                HttpArguments.createArg (BOArguments.ARG_FUNCTION,
                    AppFunctions.FCT_SHOWOBJECTCONTENT) +
                HttpArguments.createArg (BOArguments.ARG_OID, new OID (this
                    .getTypeCache ().getTVersionId (
                        StoreTypeConstants.TC_SelectUserContainer), 0).toString ())
//                + AppArguments.ARG_SEP + AppArguments.ARG_CALLINGOID + AppArguments.ARG_ASSIGN + this.oid
            , HtmlConstants.FRM_SHEET2);

        this.showFormProperty (table, StoreArguments.ARG_CAT_CONTRESPMED,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_CAT_CONTRESPMED, env), Datatypes.DT_HIDDEN,
            Catalog_01.MEDIUM_DISTRIBUTE);
        this.showFormProperty (table, StoreArguments.ARG_CAT_ORDRESPMED,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_CAT_ORDRESPMED, env), Datatypes.DT_HIDDEN,
            Catalog_01.MEDIUM_DISTRIBUTE);

        // restrict: empty entries allowed and not more than 255 characters
        this.formFieldRestriction =
            new FormFieldRestriction (true, BOConstants.MAX_LENGTH_DESCRIPTION, 0);
        this.showFormProperty (table, StoreArguments.ARG_SHIPPMENT,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_SHIPPMENT, env), Datatypes.DT_DESCRIPTION,
            this.shippmentOrder);

        // restrict: empty entries allowed and not more than 255 characters
        this.formFieldRestriction = new FormFieldRestriction (true,
            BOConstants.MAX_LENGTH_DESCRIPTION, 0);
        this.showFormProperty (table, StoreArguments.ARG_NOTAVAILABLE,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_NOTAVAILABLE, env), Datatypes.DT_DESCRIPTION,
            this.deliveryNotPossibleHandling);

        // payments:
        if (this.p_paymentTypes != null) // payment types defined?
        {
            // (index of oid: 5)
            this.showFormProperty (table, StoreArguments.ARG_ALLOWED_PAYMENTS,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_ALLOWED_PAYMENTS, env), Datatypes.DT_MULTISELECT,
                this.p_paymentTypes.ids, allPaymentTypes.ids,
                allPaymentTypes.values, 5);
        } // if payment types defined
        else                            // no payment types defined
        {
            GroupElement gel = new GroupElement ();
            gel.addElement (new TextElement (MultilingualTextProvider
                .getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_NO_ELEMENTS_FOUND, env)));
            this.showFormProperty (table, 
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_ALLOWED_PAYMENTS, env), gel);
        } // else no payment types defined

        // WORKAROUND  - if no payment is selected null is returned for
        // paymentfield this field marks, that paymentfield was displayed.
        this.showFormProperty (table, StoreArguments.ARG_ALLOWED_PAYMENTS +
            "Help", "", Datatypes.DT_HIDDEN, "x");

        // display a separator line
        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);

        // display the isOrderExport checkbox
        this.showFormProperty (table, StoreArguments.ARG_ISORDEREXPORT,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_ISORDEREXPORT, env), Datatypes.DT_BOOL, "" +
                this.isOrderExport);

        GroupElement gel;
        // the connector can be selected from a list
        gel = this.createConnectorSelectionBox (StoreArguments.ARG_CONNECTOR,
                                           "" + this.connectorOid, true, false, true);
        this.showFormProperty (table, 
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_CONNECTOR, env), gel);
        // the translator can be selected from a list
        int[] translatorTypeIds =
        {
            this.getTypeCache ().getTypeId (TypeConstants.TC_Translator),
        };
        // create the selection box
        gel = this.createSelectionBoxFromObjectType (translatorTypeIds,
            StoreArguments.ARG_TRANSLATOR, "" + this.translatorOid, true);
        this.showFormProperty (table, 
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_TRANSLATOR, env), gel);
        // the filter can be selected from a list
        gel = DIHelpers.createExportFilterSelectionBox (StoreArguments.ARG_FILTER, 
            this.filterId, true, env);
        this.showFormProperty (table, 
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_FILTER, env), gel);

        // notify by email
        this.showFormProperty (table, "notify", 
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_MAIL_SENDMAIL, env),
            Datatypes.DT_BOOL, "" + this.notifyByEmail);

        // subject
        this.showFormProperty (table, "subj", 
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_MAIL_SUBJECT, env),
            Datatypes.DT_DESCRIPTION, this.subject);

        // content
        this.showFormProperty (table, "cont", 
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_MAIL_CONTENT, env),
            Datatypes.DT_DESCRIPTION, this.content);
    } // showFormProperties


    /***************************************************************************
     * Sets the buttons that can be displayed when the user is in an object info
     * view. <BR/>
     *
     * @return An array with button ids that can be displayed.
     */
    protected int[] setInfoButtons ()
    {
        // define buttons to be displayed:
        int [] buttons =
        {
            Buttons.BTN_EDIT,
            Buttons.BTN_DELETE,
            Buttons.BTN_CUT,
//            Buttons.BTN_COPY,
//            Buttons.BTN_DISTRIBUTE,
//            Buttons.BTN_CLEAN,
//            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
//            Buttons.BTN_LOGIN
        }; // buttons

        // return button array
        return buttons;
    } // showInfoButtons


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * container's content view. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setContentButtons ()
    {
        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_NEW,
            Buttons.BTN_PASTE,
            Buttons.BTN_REFERENCE,
//            Buttons.BTN_CLEAN,
            Buttons.BTN_SEARCH,
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_LIST_COPY,
            Buttons.BTN_LIST_CUT,
//            Buttons.BTN_HELP,
//            Buttons.BTN_LOGIN
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons


    /**************************************************************************
     * Shows a debug message. <BR/>
     *
     * @param   message Debug message to be shown.
     *
     * @deprecated  This method is not longer necessary. Instead the IDE
     *              debugging mechanism shall be used. All calls to this method
     *              shall be deleted.
     */
    public void showDebug (String message)
    {
        if (false)
        {
            this.env.write ("<DIV ALIGN=\"LEFT\">" + this.getClass ().getName () + ":" +
                      message + "</DIV><P>");
        } // if
        else
        {
            this.debug (message);
        } // else
    } // showStatusBar

} // class Catalog_01

/*
 * Class: MasterDataContainer_01.java
 */

// package:
package m2.mad;

// imports:
import ibs.bo.BOHelpers;
import ibs.bo.BOListConstants;
import ibs.bo.BOTokens;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.SelectElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.NoAccessException;


/******************************************************************************
 * This class represents one object of type MasterDataContainer with
 * version 01. <BR/>
 *
 * @version     $Id: MasterDataContainer_01.java,v 1.24 2013/01/16 16:14:13 btatzmann Exp $
 *
 * @author      Keim Christine (Ck), 980729
 ******************************************************************************
 */
public class MasterDataContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MasterDataContainer_01.java,v 1.24 2013/01/16 16:14:13 btatzmann Exp $";

    /**
     * Reduced headings of columns. <BR/>
     * These headings are shown at the top of lists.
     */
    public static final String [] LST_HEADINGS_MASTERDATACONTAINER_REDUCED =
    {
        BOTokens.ML_NAME,
        MadTokens.ML_COMPOWNER,
        BOTokens.ML_EMAIL,
    }; // LST_HEADINGS_MASTERDATACONTAINER_REDUCED

    /**
     * Name of a container column. <BR/>
     * These attributes are used for ordering the elements.
     */
    public static final String [] LST_ORDERINGS_MASTERDATACONTAINER_REDUCED =
    {
        BOListConstants.LST_ORDERINGS[0],
        MasterDataContainer_01.ATTR_COMPANYOWNER,
        MadConstants.ORD_EMAIL,
    }; // LST_ORDERINGS_MASTERDATACONTAINER_REDUCED

    /**
     * Class name. <BR/>
     */
    private static final String CLASS_NAME = "m2.mad.MasterDataContainerElement_01";

    /**
     * Attribute name: company owner. <BR/>
     */
    private static final String ATTR_COMPANYOWNER = "compowner";


    /**************************************************************************
     * This constructor creates a new instance of the class
     * MasterDataContainer_01. <BR/>
     */
    public MasterDataContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // MasterDataContainer_01


    /**************************************************************************
     * Creates a MasterDataContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public MasterDataContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // MasterDataContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set which types are allowed in the Container

        this.elementClassName = MasterDataContainer_01.CLASS_NAME;
        this.viewContent = "v_MasterDataContainer_01$cont";

        // set majorContainer true
        this.isMajorContainer = true;
    } // initClassSpecifics


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        //use when everybody has flags within his / her views:
        String query = "";

        query =
            "SELECT  DISTINCT oid, state, name, typeName, isLink, linkedObjectId," +
                " owner, ownerName, ownerOid, ownerFullname, lastChanged," +
                " isNew, icon, " +
                MasterDataContainer_01.ATTR_COMPANYOWNER + ", email, description" +
            " FROM " + this.viewContent +
            " WHERE  containerId = " + this.oid.toStringQu () + " ";

        // some operations must not retrieve links
        // in these cases exclude all links from the query
        if (!this.retrieveLinks)
        {
            query += " AND isLink = 0 ";
        } // if

        return query;
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This metod is used to get all attributes of one element out of the
     * resultset. The attribute names which can be used are the ones which
     * are defined within the resultset of
     * <A HREF="#createQueryRetrieveContentData">createQueryRetrieveContentData</A>.
     * <BR/>
     * <B>Format:</B>. <BR/>
     * for oid properties:
     *      obj.&lt;property> = getQuOidValue ("&lt;attribute>"); <BR/>
     * for other properties:
     *      obj.&lt;property> = action.get&lt;type> ("&lt;attribute>"); <BR/>
     * The property <B>oid</B> is already gotten. This must not be done within this
     * method. <BR/>
     *
     * @param   action      ???
     * @param   commonObj   Object representing the list element.
     *
     * @exception   DBError
     *              Error when executing database statement.
     */
    protected void getContainerElementData (SQLAction action, ContainerElement commonObj)
        throws DBError
    {
        super.getContainerElementData (action, commonObj);
        MasterDataContainerElement_01 obj = (MasterDataContainerElement_01) commonObj;
        if (this.elementClassName.equals (MasterDataContainer_01.CLASS_NAME))
        {
            obj.compowner = action.getString (MasterDataContainer_01.ATTR_COMPANYOWNER);
        } // if

        obj.email = action.getString ("email");
    } // getContainerElementData


    /**************************************************************************
     * Show the content of the Container, i.e. its elements. <BR/>
     * The objects are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   function            function that overwrites the standard list
     *                              show function
     */
    public void showDeleteForm (int representationForm, int function)
    {
        // set the function for the delete list
        this.fct = function;

        if (true)                       // container object resists on this
                                        // server?
        {
            try
            {
/* KR 020125: not necessary because already done before
                // try to retrieve the container:
                retrieve (ibs.bo.Operations.OP_VIEWELEMS);
*/
                // try to retrieve the content of this container:
                this.retrieveSelectionContentData (Operations.OP_DELETE,
                                              this.orderBy,
                                              this.orderHow, null);

                // show the content:
                this.performShowDeleteContent (representationForm,
                                          this.orderBy, this.orderHow);
            } // try
            catch (NoAccessException e) // no access to objects allowed
            {
                // send message to the user:
                this.showNoAccessMessage (Operations.OP_VIEWELEMS);
            } // catch
/* KR 020125: not necessary because already done before
            catch (AlreadyDeletedException e) // no access to objects allowed
            {
                // send message to the user:
                showAlreadyDeletedMessage ();
            } // catch
*/
        } // if container object resists on this server
        else                            // object resists on another server
        {
          // invoke the object on the other server
        } // else object resists on another server
    } // showDeleteForm


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard container. <BR/>
     */
    protected void setHeadingsAndOrderings ()
    {
        if (!this.getUserInfo ().userProfile.showExtendedAttributes)
        {
            // reduced list

            // set headings:
            this.headings = MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE, 
                MasterDataContainer_01.LST_HEADINGS_MASTERDATACONTAINER_REDUCED, env);

            // set ordering attributes for the corresponding headings:
            this.orderings = MasterDataContainer_01.LST_ORDERINGS_MASTERDATACONTAINER_REDUCED;

        } // if
        else
        { // extended headingslist
            super.setHeadingsAndOrderings ();

            // extend the headings
            String[] temp = this.headings;
            this.headings = new String[this.headings.length + 2];
            for (int i = 0; i < temp.length; i++)
            {
                this.headings[i] = temp[i];
            } // for i
            this.headings[this.headings.length - 2] = 
                MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                    MadTokens.ML_COMPOWNER, env);
            this.headings[this.headings.length - 1] =  
                MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                    BOTokens.ML_EMAIL, env);

            // extend the orderings
            temp = this.orderings;
            this.orderings = new String[this.orderings.length + 2];
            for (int i = 0; i < temp.length; i++)
            {
                this.orderings[i] = temp[i];
            } // for i
            this.orderings[this.orderings.length - 2] = MasterDataContainer_01.ATTR_COMPANYOWNER;
            this.orderings[this.orderings.length - 1] = MadConstants.ORD_EMAIL;
        } // else

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings


    /**************************************************************************
     * Generates a Selection Box with all groups available. <BR/>
     *
     * @param   companyFilter   filter for the query.
     *
     * @return  ???
     */
    public SelectElement createCompanySelectionBox (String companyFilter)
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        SelectElement sel;
        int rowCount;
        String methodName = "MasterDataContainer_01.createCompanySelectionBox";

        sel = new SelectElement (MadArguments.ARG_COMPANY, false);
        sel.size = 6;

        // open db connection -  only workaround - db connection must
        // be handled somewhere else
        action = this.getDBConnection ();

        // create the SQL String to select all tuples
        // workaround: there are no right checks done
        String queryStr =
            "SELECT DISTINCT mad.oid, mad.name " +
            " FROM v_Container$content mad" +
            " WHERE userId  = " + this.getUser ().id +
            " AND tVersionId = " + this.getTypeCache ().getTVersionId (MadTypeConstants.TC_Company) + " " +
            SQLHelpers.getStringCheckRights (Operations.OP_VIEW);

        if (companyFilter != null && companyFilter.length () > 0)
        {
            queryStr +=
                " AND " +
                SQLHelpers.getQueryConditionString (
                    "mad.name", SQLConstants.MATCH_SUBSTRING, companyFilter, false);
        } //if
        // check if filter has to be set

        queryStr = queryStr + " ORDER BY name ";

//debug (queryStr);
        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            rowCount = action.execute (queryStr, false);

            // empty resultset?
            if (rowCount == 0)
            {
                return sel;
            } // if
            // error while executing?
            else if (rowCount < 0)
            {
                IOHelpers.showMessage (methodName, "no data found", this.app,
                    this.sess, this.env);
            } // else if
            // everything ok - go on

            // get tuples out of db
            while (!action.getEOF ())
            {
                // create entries in list
                String oid = action.getString ("oid");
                sel.addOption (action.getString ("Name"), oid);
                // step one tuple ahead for the next loop
                action.next ();
            } // while

            // the last tuple has been processed
            // end transaction
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (methodName, dbErr, this.app, this.sess,
                this.env, false);
        } // catch
        finally
        {
            // close db connection in every case - only workaround - db
            // connection  must be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        return sel;
    } // createCompanySelectionBox


    /**************************************************************************
     * Gets a MasterDataContainer out of the database (with given name and
     * containerName). <BR/>
     *
     * @param   pContainerName  Name of the Container where the searched one is in
     * @param   pName           Name of the searched Container
     *
     * @return  ???
     */
    public OID getMasterDataContainer (String pContainerName, String pName)
    {
        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                "p_MasterDataContainer_01$gOid",
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // user id
        if (this.user != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
        } // else
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, Operations.OP_READ);
        // containerName
        sp.addInParameter (ParameterConstants.TYPE_STRING, pContainerName);
        // name
        sp.addInParameter (ParameterConstants.TYPE_STRING, pName);

        // output parameters
        // oid
        Parameter outParamOid = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);

        try
        {
            // perform the function call:
            BOHelpers.performCallFunctionData (sp, this.env);
        } // try
        catch (NoAccessException e)
        {
            // should not occur
            // TODO: handle the exception
        } // catch

        // set object properties - get them out of parameters
        this.oid = SQLHelpers.getSpOidParam (outParamOid);
        return this.oid;
    } // getMasterDataContainer

} // class MasterDataContainer_01

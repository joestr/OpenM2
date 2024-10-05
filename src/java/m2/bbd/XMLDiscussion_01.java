/*
 * Class: XMLDiscussion_01.java
 */

// package:
package m2.bbd;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOHelpers;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.di.DataElement;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.GroupElement;
import ibs.tech.html.SelectElement;
import ibs.tech.html.TableElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.NoAccessException;

import java.util.Vector;


/******************************************************************************
 * This class represents one object of type XMLDiscussion_01 with version 01. <BR/>
 *
 * @version     $Id: XMLDiscussion_01.java,v 1.22 2013/01/16 16:14:10 btatzmann Exp $
 *
 * @author      Keim Christine (CK), 000925
 ******************************************************************************
 */
public class XMLDiscussion_01 extends Discussion_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XMLDiscussion_01.java,v 1.22 2013/01/16 16:14:10 btatzmann Exp $";


    /**
     * the referenced XMLDiscussionTemplate. <BR/>
     */
    public OID refOid  = null;

    /**
     * the name of the referenced XMLDiscussionTemplate. <BR/>
     */
    public String refName  = "";

    /**
     * vector of all discussion templates in the system. <BR/>
     */
    public Vector<String[]> templates  = null;

    /**
     * XML tag name for referenced oid.
     */
    private static final String XML_REFOID = "refOid";


    /**************************************************************************
     * This constructor creates a new instance of the class XMLDiscussion_01.
     * <BR/>
     */
    public XMLDiscussion_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // XMLDiscussion_01


    /**************************************************************************
     * Creates a XMLDiscussion_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @see     ibs.bo.BusinessObject
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public XMLDiscussion_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // XMLDiscussion_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        super.initClassSpecifics ();

        // set the class-procedureNames
        this.procCreate   = "p_Discussion_01$create";
        this.procChange   = "p_XMLDiscussion_01$change";
        this.procRetrieve = "p_XMLDiscussion_01$retrieve";
        this.procDelete   = "p_Discussion_01$delete";

        this.specificChangeParameters += 1;
        this.specificRetrieveParameters += 2;

        this.msgBundleContainerEmpty = BbdMessages.MSG_BUNDLE;
        this.msgContainerEmpty = BbdMessages.ML_MSG_DISCUSSIONEMPTY;

        // this class is instanced for contaienrelements which
        // are shown in containercontent
        this.elementClassName = "m2.bbd.DiscussionElement_01";

        // show content as frameset, first frame for themetree, second frame
        // for articles
        this.showContentAsFrameset = true;
        this.viewContent = "v_XMLDiscussion_01$content";
        this.delContent = "v_XMLDiscussion_01$delcontent";
    } // initClassSpecifics


    /**************************************************************************
     * Initializes a XMLDiscussion object. <BR/>
     * The <A HREF="m2.bbd.Discussion_01#discType">discType</A> is set to the
     * name of this object type.
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     * @param   env     The actual environment object.
     * @param   sess    The actual session info object.
     * @param   app     The global application info object.
     *
     * @see     ibs.bo.BusinessObject#initObject
     */
    public void initObject (OID oid, User user, Environment env,
                            SessionInfo sess, ApplicationInfo app)
    {
        // call method of super class:
        super.initObject (oid, user, env, sess, app);

        // set the instance's public/protected properties:
        this.discType = this.typeObj != null ? this.typeObj.getName () : null;
    } // initObject


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="#env">env</A> property is used for getting the parameters.
     * This property must be set before calling this method. <BR/>
     */
    public void getParameters ()
    {
        OID temp = null;

        // call super method
        super.getParameters ();

        // get id of container:
        if ((temp = this.env.getOidParam (BOArguments.ARG_REFOID)) != null)
        {
            this.refOid = temp;
        } // if
    } // getParameters


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
        // call the method to set all Specific Parameters for the superobjects
        super.setSpecificChangeParameters (sp);

        // set the specific parameters:
        // reference object for templates
        // refOid
        BOHelpers.addInParameter (sp, this.refOid);
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
        int i = super.setSpecificRetrieveParameters (sp, params, lastIndex);

        // set the specific parameters:
        // refOid
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // refName
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * containers content view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setContentButtons ()
    {
        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_NEW,
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_SEARCH,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons


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
        int i = lastIndex;

        // momentary hardcoded - bug in the basis
        this.maxLevels = params[++i].getValueInteger ();
        this.defaultView = params[++i].getValueInteger ();

        if (this.defaultView == BbdConstants.VAL_STANDARD)
        {
            // frameset shall not be shown
            this.showContentAsFrameset = false;
        } // if
        else
        {
            // frameset shall be shown
            this.showContentAsFrameset = true;
        } // else

        // get the specific parameters:
        this.refOid = SQLHelpers.getSpOidParam (params[++i]);
        this.refName = params[++i].getValueString ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Represent the properties of a BusinessObject object to the user. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        GroupElement gel;
        SelectElement sel;

        super.showFormProperties (table);
/*
        // property 'name':
        // set form field restriction (will automatically be used in
        // next showFormProperty)
        // restrict: no empty entries allowed
        this.formFieldRestriction =
            new FormFieldRestriction (false);
        // loop through all properties of this object and display them:
        showFormProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env), Datatypes.DT_NAME, name);

        showFormProperty (table, BOArguments.ARG_INNEWS, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env), Datatypes.DT_BOOL,"" + this.showInNews);
        showFormProperty (table, BOArguments.ARG_DESCRIPTION, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CONTENT, env), Datatypes.DT_DESCRIPTION, description);

        // property 'validUntil':
        // set form field restriction (will automatically be used in
        // next showFormProperty)
        // restrict: no empty entries allowed
        // 0 .. default size/length values for datatype will be taken
        // null .. no upper bound
        this.formFieldRestriction =
            new FormFieldRestriction (false);
        showFormProperty (table, BOArguments.ARG_VALIDUNTIL, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_VALIDUNTIL, env), Datatypes.DT_DATE, validUntil);
*/
        // show selection box with pertinent template selected
        // get the Vector with the names and the oids of the DiscussionTemplates in the system
        if (this.templates == null)
        {
            this.templates = this.createTemplatesVector ();
        } // if

        gel = new GroupElement ();
        sel = this.createSelectionBox (BOArguments.ARG_REFOID, this.templates, this.refOid.toString ());
        sel.size = 1;
        sel.multiple = false;

        // show the template
        gel.addElement (sel);
        this.showFormProperty (table,  
            MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                BbdTokens.ML_XMLDISCTEMPLATE, env), gel);
    } // showFormProperties


    /**************************************************************************
     * Represent the properties of a BusinessObject object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        super.showProperties (table);

        // show pertinent template selected
        this.showProperty (table, BbdArguments.ARG_DUMMY,  
            MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                BbdTokens.ML_XMLDISCTEMPLATE, env),
            Datatypes.DT_NAME, this.refName);
        this.showProperty (table, BOArguments.ARG_REFOID,  
            MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                BbdTokens.ML_XMLDISCTEMPLATE, env),
            Datatypes.DT_HIDDEN, this.refOid.toString ());

    } // showProperties


    /**************************************************************************
     * Create a Vector with all discussion templates visible in the system
     *
     * @return  The Vector filled with the name/oid-pair of all discussion
     *          templates.
     */
    protected Vector<String[]> createTemplatesVector ()
    {
        Vector<String[]> temp = new Vector<String[]> ();
        SQLAction action = null;

        // set view to use for the query
        String viewContent = "v_XMLDiscTempContainer_01$cont";
        // type of operation
        int operation = Operations.OP_VIEW;

        // init variables
        int rowCount;

        String queryStr =
            " SELECT DISTINCT oid, name" +
            " FROM " + viewContent +
            " WHERE userId = " + this.user.id +
            SQLHelpers.getStringCheckRights (operation) +
            " ORDER BY name";

        // open db connection
        action = this.getDBConnection ();

        // execute the queryString, indicate that we're not performing an action query:
        try
        {
            rowCount = action.execute (queryStr, false);

            // empty resultSet?
            if (rowCount == 0)
            {
                return temp;
            } // if
            // error while executing
            else if (rowCount < 0)
            {
                return temp;
            } // else

            // get tuples out of db
            while (!action.getEOF ())
            {
                // create entries in list
                String[] t = new String [2];
                t[0] = action.getString ("name");
                t[1] = action.getString ("oid");
                temp.addElement (t);
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
            IOHelpers.showMessage (dbErr, this.app, this.sess, this.env, false);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        return temp;
    } // createTemplatesVector


    /**************************************************************************
     * This method returns a boolean which is false if the object which should
     * be created, has e.g. no template. <BR/>
     * This method overrides the method of BusinessObject. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     *
     * @return  boolean which is set to false if the object which should be
     *          created has no templates on the database.
     *
     * @see ibs.bo.BusinessObject#showChangeForm (int, int)
     */
    protected boolean checkShowChangeFormConstraints ()
    {
        boolean constraintExists = false; // return value of this method

        // checks if there is at least one template to create an XMLDiscussion
        if (this.existsAnyTemplate ())
        {
            constraintExists = true;
        } // if

        return constraintExists;
    } // checkShowChangeFormConstraints



    /**************************************************************************
     * Checks if the XMLDiscussion has an template on the database. <BR/>
     *
     * @return  <CODE>boolean</CODE> which is false if the XMLDiscussion has no
     *          templates.
     */
    protected boolean existsAnyTemplate ()
    {
        boolean templateExists = false; // return value of this method

        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                "p_XMLDiscussion_01$checkTempl",        // procedure to check constraints
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
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, Operations.OP_VIEW);

        // output parameters
        // counter
        Parameter counter = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

        try
        {
            // perform the function call:
            BOHelpers.performCallFunctionData (sp, this.env);
        } // try
        catch (NoAccessException e)
        {
            // do nothing because the procedure is only an SELECT-Statement
        } // catch

        if (counter.getValueInteger () > 0) // are there templates?
        {
            templateExists = true;
        } // if

        return templateExists;
    } // existsAnyTemplate


    /**************************************************************************
     * writes the object data to an dataElement. <BR/>
     *
     * @param dataElement     the dataElement to write the data to
     *
     * @see ibs.bo.BusinessObject#writeExportData
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        super.writeExportData (dataElement);
        // set the type specific values
        dataElement.setExportValue (XMLDiscussion_01.XML_REFOID, this.refOid.toString ());
    } // writeExportData


    /**************************************************************************
     * Reads the object data from an dataElement. <BR/>
     *
     * @param dataElement     the dataElement to read the data from
     *
     * @see ibs.bo.BusinessObject#readImportData
     */
    public void readImportData (DataElement dataElement)
    {
        // get business object specific values
        super.readImportData (dataElement);
        // get the type specific values

        try
        {
            if (dataElement.exists (XMLDiscussion_01.XML_REFOID))
            {
                this.refOid =
                    new OID (dataElement.getImportStringValue (XMLDiscussion_01.XML_REFOID));
            } // if
        } // try
        catch (IncorrectOidException e)
        {
            throw new RuntimeException (e.toString ());
        } // catch
    } // readImportData


    /**************************************************************************
     * Show a frameset view of the object's content. <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   function1           The function for the first frame.
     */
    public void showFrameset (int representationForm, int function1)
    {
        if (true)                       // business object resists on this
                                        // server?
        {
            String contentType = this.env.getServerVariable (IOConstants.SV_CONTENT_TYPE);
            if ((contentType != null) && (contentType.equals (IOConstants.CONT_MULTIPARTFORM)))
            {
                // an upload was done, for that it is necessary to
                // reshow the object content (the frameset)
                // create the output:
                this.processJavaScriptCode (IOHelpers.getShowObjectJavaScript ("" + this.oid));
            } // if
            else
            {
                // show the frameset:
                this.performShowFrameset (representationForm, function1);

                // frameset view is not possible for next call:
                this.framesetPossible = false;
            } // else
        } // if business object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server
    } // showFrameset


    /**************************************************************************
     * Get the ids of all types which are allowed to be contained within the
     * actual object type. <BR/>
     * This method depends on the value of the oid and gets the corresponding
     * object type out of the object pool. Then it gets the types which may be
     * contained within this type out of the type itself.
     *
     * @return  An array with the allowed type ids.
     */
    public String[] getTypeIds ()
    {
        String[] typeIds = null;        // the type ids for the object type

        // check if there is a valid oid:
        if (this.oid != null)           // oid exists?
        {
            XMLDiscussionTemplate_01 discTemp = (XMLDiscussionTemplate_01)
                BOHelpers.getObject (this.refOid, this.app, this.sess,
                                 this.user, this.env, false, false, true);

            // check if we got the object:
            if (discTemp != null)   // got the discussion template?
            {
                OID templateOid = discTemp.level1;
                // retrieve the type of the first level template
                typeIds = new String[1];
                // get the type of the first level template:
                typeIds[0] = "" + templateOid.tVersionId;
            } // if got the discussion template
        } // if oid exists

        return typeIds;                 // return the computed type ids
    } // getTypeIds

} // class XMLDiscussion_01

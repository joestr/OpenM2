/*
 * Class: Document_01.java
 */

// package:
package m2.doc;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.doc.DocConstants;
import ibs.service.user.User;
import ibs.tech.html.SelectElement;
import ibs.tech.html.TableElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.util.Helpers;


/******************************************************************************
 * This class represents one object of type Document with version 01. <BR/>
 *
 * @version     $Id: Document_01.java,v 1.23 2013/01/16 16:14:14 btatzmann Exp $
 *
 * @author      Heinz Josef Stampfer (HJ), 980526
 ******************************************************************************
 */
public class Document_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Document_01.java,v 1.23 2013/01/16 16:14:14 btatzmann Exp $";


    /**
     * Oid of the attachmentlist we have sent. <BR/>
     */
    public OID attachmentContainerId = null;

    /**
     * The OID of the referenced MasterAttachment. <BR/>
     */
    public OID masterId;

    /**
     * The Name of the MasterFile. <BR/>
     */
    public String fileName = "";

    /**
     * A hyperlink is a URL. <BR/>
     */
    public String url = "";

    /**
     * The path of the File Source. <BR/>
     */
    public String path = BOPathConstants.PATH_UPLOAD;

    /**
     *  Used to read out attachmentType in RetrieveContentData. <BR/>
     */
    public int attachmentType = 0;

    /**
     * The filesize of a file in KBytes. <BR/>
     */
    public float filesize = 0;


    /**************************************************************************
     * This constructor creates a new instance of the class Document_01.
     * <BR/>
     */
    public Document_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // Document_01


    /**************************************************************************
     * Creates a Document_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Document_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // Document_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the instance's attributes:

        this.procCreate =     "p_Document_01$create";
        this.procChange =     "p_Document_01$change";
        this.procRetrieve =   "p_Document_01$retrieve";
    //    this.procDelete =     "p_Document_01$delete";

        // set the instance's attributes:

        // set extended search flag
        this.searchExtended = false;

        // set db table name
        this.tableName = "";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 6;
    } // initClassSpecifics


    /**************************************************************************
     * Returns if the Object is a file-type. <BR/>
     * For documents this is <CODE>true</CODE>.
     *
     * @return  <CODE>true</CODE> if the object contains a file field,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean hasFile ()
    {
        return true;
    } // hasFile


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * container's content view. <BR/>
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
//            Buttons.BTN_NEW,
//            Buttons.BTN_PASTE,
//            Buttons.BTN_CLEAN,
            Buttons.BTN_SEARCH,
//          Buttons.BTN_HELP,
            Buttons.BTN_LOGIN,
//          Buttons.BTN_LISTDELETE,
//            Buttons.BTN_REFERENCE,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in an
     * object's info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setInfoButtons ()
    {
        // define buttons to be displayed:
        int [] buttons =
        {
            Buttons.BTN_EDIT,
            Buttons.BTN_DELETE,
            Buttons.BTN_CUT,
            Buttons.BTN_COPY,
            Buttons.BTN_DISTRIBUTE,
            Buttons.BTN_STARTWORKFLOW,
            Buttons.BTN_FORWARD,
            Buttons.BTN_FINISHWORKFLOW,
//            Buttons.BTN_EXPORT,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons


    /**************************************************************************
     * Is the object type allowed in workflows? <BR/>
     * This method shall be overwritten in subclasses.
     *
     * @return  <CODE>true</CODE> if the object type is allowed in workflows,
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean isWfAllowed ()
    {
        return true;
    } // isWfAllowed


    /**************************************************************************
     * Represent the properties of a Document_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showProperties
     * @see ibs.bo.BusinessObject#showProperty
     */
    protected void showProperties (TableElement table)
    {
        // display the base object's properties:
        super.showProperties (table);
        // loop through all properties of this object and display them:
        //    showProperty (table, AppArguments.ARG_FILE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FILE, env), Datatypes.DT_FILE, fileName);
        if (this.attachmentType == DocConstants.ATT_FILE)
        {
            this.showProperty (table, BOArguments.ARG_MASTERDEFINED, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FILE, env), Datatypes.DT_FILE, this.fileName, this.path);
            String str = Helpers.convertFileSize (this.getFilesize (this.masterId), env);
            this.showProperty (table, BOArguments.ARG_FILESIZE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FILESIZE, env), Datatypes.DT_NUMBER, str);
        } // if

        if (this.attachmentType == DocConstants.ATT_HYPERLINK)
        {
            this.showProperty (table, BOArguments.ARG_MASTERDEFINED, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_HYPERLINK, env), Datatypes.DT_URL, this.url);
        } // if
    } // showProperties


    /**************************************************************************
     * Represent the properties of a Document_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showFormProperties
     * @see ibs.bo.BusinessObject#showFormProperty
     */
    protected void showFormProperties (TableElement table)
    {
        // display the base object's properties:
        super.showFormProperties (table);
        if (this.attachmentType == DocConstants.ATT_FILE)
        {
            this.showProperty (table, BOArguments.ARG_MASTERDEFINED, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FILE, env), Datatypes.DT_FILE, this.fileName);
        } // if

        if (this.attachmentType == DocConstants.ATT_HYPERLINK)
        {
            this.showProperty (table, BOArguments.ARG_MASTERDEFINED, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_HYPERLINK, env), Datatypes.DT_URL, this.url);
        } // if
       //showProperty (table, AppArguments.ARG_FILE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FILE, env), Datatypes.DT_FILE, masterName);
       //showProperty (table, AppArguments.ARG_PATH, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_PATH, env), Datatypes.DT_NAME, masterPath);
    } // showFormProperties


    /**************************************************************************
     * Defines the activity entries being displayed in the activities selection
     * box. <BR/>
     * This method must be overwritten in subclasses in order to provide
     * type specific activities lists. <BR/>
     *
     * @param   sel     Selection Box Element.
     * @param   pa      Preselected activity.
     */
    protected void addActivities (SelectElement sel, String pa)
    {
        this.addActivity (sel, pa, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ACT_PLEASE_REVISE, env));
        this.addActivity (sel, pa, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ACT_PLEASE_COMPLEMENT, env));
        this.addActivity (sel, pa, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ACT_PLEASE_PUBLISH, env));
        this.addActivity (sel, pa, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ACT_PLEASE_TAKE_NOTE_OF, env));
        this.addActivity (sel, pa, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ACT_URGENT_REVISION, env));
        this.addActivity (sel, pa, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ACT_PLEASE_EDIT, env));
        this.addActivity (sel, pa, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ACT_PLEASE_CORRECT, env));
        this.addActivity (sel, pa, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ACT_PLEASE_APPROVE, env));
        this.addActivity (sel, pa, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ACT_FOR_PRESENTATION, env));
    } // addActivities


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
        // attachmentContainerId
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // masterId
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // fileName
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // url
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // path
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // attachmentType
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


    /**************************************************************************
     * Get the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * type specific data from the retrieve data stored procedure.
     *
     * @param   params      The array of parameters from the retrieve data stored
     *                      procedure.
     * @param   lastIndex   The index to the last element used in params thus far.
     */
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // get the specific parameters:
        this.attachmentContainerId = SQLHelpers.getSpOidParam (params[++i]);
        this.masterId = SQLHelpers.getSpOidParam (params[++i]);
        this.fileName = params[++i].getValueString ();
        this.url = params[++i].getValueString ();
        this.path = params[++i].getValueString ();
        this.attachmentType = params[++i].getValueInteger ();
    } // getSpecificRetrieveParameters


    //
    // import / export methods
    //
    /**************************************************************************
     * Reads the object data from an dataelement. <BR/>
     *
     * @param dataElement   The dataElement to read the data from.
     *
     * @see ibs.bo.BusinessObject#readImportData
     */
    public void readImportData (ibs.di.DataElement dataElement)
    {
        // get business object specific values:
        super.readImportData (dataElement);
        // get the type specific values:
    } // readImportData


    /**************************************************************************
     * writes the object data to an dataelement. <BR/>
     *
     * @param dataElement   The dataElement to write the data to.
     *
     * @see ibs.bo.BusinessObject#writeExportData
     */
    public void writeExportData (ibs.di.DataElement dataElement)
    {
        // set the business object specific values:
        super.writeExportData (dataElement);
        // set the type specific values:
        dataElement.typename = this.typeObj.getName ();
    } // writeExportData


    /**************************************************************************
     * Get the file size of the attachment with the master id. <BR/>
     *
     * @param master     oid of the master-attachment
     *
     * @return  The size of the attachment file.
     */
    private float getFilesize (OID master)
    {
        SQLAction action = null;        // the action object used to access the
                                        // database

        this.filesize = 0;              // filesize of the attachment with the
                                        // master id
        int rowCount = 0;               // are there some entries in the DB?
        String queryStr = " SELECT filesize" +
                          " FROM ibs_Attachment_01" +
                          " WHERE oid = " + master.toStringQu ();
                                        // query to execute

        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);
            if (rowCount > 0)
            {
                this.filesize = action.getFloat ("filesize");
            } // if
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
        } // catch
        finally
        {
            // close db connection in every case - only workaround -
            // db connection must be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        return this.filesize;
    } // getFilesize

} // class Document_01

/*
 * Class: AttachmentContainer_01.java
 */

// package:
package ibs.obj.doc;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.doc.AttachmentContainerElement_01;
import ibs.obj.doc.DocConstants;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;


/******************************************************************************
 * This class represents one object of type Dokument with version 01. <BR/>
 *
 * @version     $Id: AttachmentContainer_01.java,v 1.22 2013/01/16 16:14:13 btatzmann Exp $
 *
 * @author      Stampfer Heinz Josef (HJ), 980428
 ******************************************************************************
 */
public class AttachmentContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: AttachmentContainer_01.java,v 1.22 2013/01/16 16:14:13 btatzmann Exp $";


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


    /***************************************************************************
     * This constructor creates a new instance of the class
     * AttachmentContainer_01. <BR/>
     */
    public AttachmentContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // AttachmentContainer_01


    /**************************************************************************
     * Creates a AttachmentContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public AttachmentContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // AttachmentContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.procCreate =     "p_AC_01$create";
        this.procChange =     "p_AC_01$change";
        this.procRetrieve =   "p_AC_01$retrieve";
        this.procDelete =     "p_AC_01$delete";

        this.elementClassName = "ibs.obj.doc.AttachmentContainerElement_01";
        this.viewContent = "v_AttachmentCont_01$content";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 5;
    } // initClassSpecifics


    /**************************************************************************
     * Represent the properties of a Dokument_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showProperties (table); // name, type, description
//   showProperty (table, BOArguments.ARG_VALIDUNTIL, TOK_VALIDUNTIL, Datatypes.DT_DATE, Helpers.dateToString (validUntil));
//   showProperty (table, BOArguments.ARG_CREATED, TOK_CREATED, Datatypes.DT_DATE, Helpers.dateToString (creationDate));
//   showProperty (table, BOArguments.ARG_CREATOR, TOK_CREATOR, Datatypes.DT_USER, creator.toString ());
        if (this.attachmentType == DocConstants.ATT_FILE)
        {
            this.showProperty (table, BOArguments.ARG_MASTERDEFINED,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MASTERFILE, env), Datatypes.DT_FILE, this.fileName);
        } // if
        else if (this.attachmentType == DocConstants.ATT_HYPERLINK)
        {
            this.showProperty (table, BOArguments.ARG_MASTERDEFINED,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MASTERFILE, env), Datatypes.DT_URL, this.url);
        } // else if
    } // showProperties


    /**************************************************************************
     * Represent the properties of a Dokument_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showFormProperties (table);

//   showProperty (table, BOArguments.ARG_CREATED, TOK_CREATED, Datatypes.DT_DATE, creationDate);
        this.showProperty (table, BOArguments.ARG_CREATOR, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CREATOR, env),
            Datatypes.DT_USER, this.creator.toString ());
        this.showProperty (table, BOArguments.ARG_MASTERDEFINED,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MASTERDEFINED, env), Datatypes.DT_NAME, this.fileName);

        if (this.attachmentType == DocConstants.ATT_FILE)
        {
            this.showProperty (table, BOArguments.ARG_MASTERDEFINED,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MASTERFILE, env), Datatypes.DT_FILE, this.fileName);
        } // if
        else if (this.attachmentType == DocConstants.ATT_HYPERLINK)
        {
            this.showProperty (table, BOArguments.ARG_MASTERDEFINED,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MASTERFILE, env), Datatypes.DT_URL, this.url);
        } // else if
    } // showFormProperties


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
        int[] buttons =
        {
            Buttons.BTN_EDIT,
            Buttons.BTN_DELETE,
            Buttons.BTN_CUT,
            Buttons.BTN_COPY,
            Buttons.BTN_DISTRIBUTE,
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
//            Buttons.BTN_LOGIN
        }; // buttons

        // return button array
        return buttons;
    } // showInfoButtons


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
        this.debug (" Bin im setContentButtons des AC!");
        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_NEW,
            Buttons.BTN_PASTE,
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
//            Buttons.BTN_LOGIN,
            Buttons.BTN_LISTDELETE,
//            Buttons.BTN_REFERENCE,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons


    /***************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     *
     * @return The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        return new StringBuffer ()
            .append (" SELECT * ")
            .append (" FROM ").append (this.viewContent)
            .append (" WHERE  containerId = ").append (this.oid.toStringQu ())
            .append (" ")
            .toString ();
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This metod is used to get all attributes of one element out of the
     * resultset. The attribute names which can be used are the ones which
     * are defined within the resultset of
     * {@link #createQueryRetrieveContentData} <BR/>
     * <B>Format:</B><BR/>
     * for oid properties:
     * <PRE>
     *      obj.&lt;property> = getQuOidValue (action, "&lt;attribute>");
     * </PRE>
     * for other properties:
     * <PRE>
     *      obj.&lt;property> = action.get&lt;type> ("&lt;attribute>");
     * </PRE>
     * The property <B>oid</B> is already gotten. This must not be done within this
     * method. <BR/>
     *
     * @param   action      The action for the database connection.
     * @param   commonObj   Object representing the list element.
     *
     * @exception   DBError
     *              Error when executing database statement.
     */
    protected void getContainerElementData (SQLAction action,
                                            ContainerElement commonObj)
        throws DBError
    {
        super.getContainerElementData (action, commonObj);

        AttachmentContainerElement_01 obj = (AttachmentContainerElement_01) commonObj;

        obj.sourceName = action.getString ("sourceName");

        obj.path = this.getBase () + action.getString ("path");
        obj.attachmentType = action.getInt ("attachmentType");
        obj.filesize = action.getFloat ("filesize");
        obj.isMaster = action.getInt ("isMaster");

        obj.showInWindow = this.getUserInfo ().userProfile.showFilesInWindows;
    } // getContainerElementData


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
        // oid of the master
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // name of file
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
     * @param params        The array of parameters from the retrieve data stored
     *                   procedure.
     * @param lastIndex    The index to the last element used in params thus far.
     */
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // get the specific parameters:
        this.masterId = SQLHelpers.getSpOidParam (params[++i]);
        this.fileName = params[++i].getValueString ();
        this.url = params[++i].getValueString ();
        this.path = params[++i].getValueString ();
        this.attachmentType = params[++i].getValueInteger ();
    } // getSpecificRetrieveParameters


    /***************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard
     * container. <BR/>
     */
    protected void setHeadingsAndOrderings ()
    {
        if (!this.getUserInfo ().userProfile.showExtendedAttributes)
        // if reduced list
        {
            // set headings:
            this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOListConstants.LST_HEADINGS_ATTACHMENTCONTAINERREDUCED, env);

            // set ordering attributes for the corresponding headings:
            this.orderings = BOListConstants.LST_ORDERINGS_ATTACHMENTCONTAINERREDUCED;
        } // if reduced list
        else
        // show extended attributes
        {
            // set headings:
            this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOListConstants.LST_HEADINGS_ATTACHMENTCONTAINER, env);

            // set ordering attributes for the corresponding headings:
            this.orderings = BOListConstants.LST_ORDERINGS_ATTACHMENTCONTAINER;
        } // else show extended attributes

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings

} // class AttachmentContainer_01

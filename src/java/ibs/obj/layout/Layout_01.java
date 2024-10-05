/*
 * Class: Layout_01.java
 */

// package:
package ibs.obj.layout;

// imports:
//KR TODO: unsauber
import ibs.app.AppConstants;
import ibs.bo.BOArguments;
import ibs.bo.BOHelpers;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.di.DataElement;
import ibs.io.IOConstants;
import ibs.io.LayoutConstants;
import ibs.io.LayoutElement;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.StoredProcedure;


/******************************************************************************
 * This class represents one object of type Dokument with version 01. <BR/>
 *
 * @version     $Id: Layout_01.java,v 1.12 2013/01/16 16:14:15 btatzmann Exp $
 *
 * @author      Keim Christine (CK), 981221
 ******************************************************************************
 */
public class Layout_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Layout_01.java,v 1.12 2013/01/16 16:14:15 btatzmann Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // properties
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Path, where the Layout can be found. <BR/>
     */
    public String path = "";

    /**
     * Name of the file, where the frameset is loaded
     */
    public String frameset = "";

    /**
     * Elements of the Layout. <BR/>
     */
    public LayoutElement[] elems =
        new LayoutElement[LayoutConstants.STYLECOUNT];

    /**
     * Is this the default layout? <BR/>
     * Default: <CODE>false</CODE>
     */
    protected boolean p_isDefault = false;

    /**
     * fieldname: isDefault. <BR/>
     */
    public static final String FIELD_ISDEFAULT = "Standard";



    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor creates a new instance of the class Layout_01. <BR/>
     */
    public Layout_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // Layout_01


    /**************************************************************************
     * Creates a Layout_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    @Deprecated
    public Layout_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // Layout_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    @Override
    public void initClassSpecifics ()
    {
        // set common attributes:
        this.procCreate = "p_Layout_01$create";
        this.procChange = "p_Layout_01$change";
        this.procRetrieve = "p_Layout_01$retrieve";
        this.procDelete = "p_Layout_01$delete";

        // set the instance's attributes:
        // initialize elems
        this.elems = new LayoutElement[LayoutConstants.STYLECOUNT];

        LayoutElement temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_BUTTONS;
        temp.images = BOPathConstants.PATH_IMAGE_BUTTONS;
        temp.javascript = AppConstants.JS_BUTTONS;

        this.elems[LayoutConstants.BUTTONBAR] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_TABS;
        temp.images = BOPathConstants.PATH_IMAGE_TABS;
        temp.javascript = AppConstants.JS_TABS;

        this.elems[LayoutConstants.TABBAR] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_SHEET;
        temp.images = BOPathConstants.PATH_IMAGE_SHEET;
        temp.javascript = AppConstants.JS_SHEET;

        this.elems[LayoutConstants.SHEETINFO] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_LIST;
        temp.images = BOPathConstants.PATH_IMAGE_LIST;
        temp.javascript = AppConstants.JS_LIST;

        this.elems[LayoutConstants.SHEETLIST] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_MENU;
        temp.images = BOPathConstants.PATH_IMAGE_MENU;
        temp.javascript = AppConstants.JS_MENU;

        this.elems[LayoutConstants.MENUBAR] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_NAVTABS;
        temp.images = BOPathConstants.PATH_IMAGE_TABS;
        temp.javascript = AppConstants.JS_NAVTABS;

        this.elems[LayoutConstants.NAVBAR] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_HEADER;
        temp.images = BOPathConstants.PATH_IMAGE_HEADER;
        temp.javascript = AppConstants.JS_HEADER;

        this.elems[LayoutConstants.HEADER] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_SHEETORDER;
        temp.images = BOPathConstants.PATH_IMAGE_SHEETORDER;
        temp.javascript = AppConstants.JS_SHEETORDER;

        this.elems[LayoutConstants.SHEETORDER] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_SHEETCOLLECTION;
        temp.images = BOPathConstants.PATH_IMAGE_SHEETCOLLECTION;
        temp.javascript = AppConstants.JS_SHEETCOLLECTION;

        this.elems[LayoutConstants.SHEETCOLLECTION] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_CALENDAR;
        temp.images = BOPathConstants.PATH_IMAGE_CALENDAR;
        temp.javascript = AppConstants.JS_CALENDAR;

        this.elems[LayoutConstants.CALENDAR] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_MESSAGE;
        temp.images = BOPathConstants.PATH_IMAGE_MESSAGE;
        temp.javascript = AppConstants.JS_MESSAGE;

        this.elems[LayoutConstants.MESSAGE] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_SHEETPRINT;
        temp.images = BOPathConstants.PATH_IMAGE_SHEETORDER;
        temp.javascript = AppConstants.JS_SHEETORDER;

        this.elems[LayoutConstants.PRINTSHEET] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_PRODUCTCATALOG;
        temp.images = BOPathConstants.PATH_IMAGE_PRODUCTCATALOG;
        temp.javascript = AppConstants.JS_PRODUCTCATALOG;

        this.elems[LayoutConstants.PRODUCTCATALOG] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_REFERENCES;
        temp.images = BOPathConstants.PATH_IMAGE_SHEET;
        temp.javascript = AppConstants.JS_SHEET;

        this.elems[LayoutConstants.REFERENCES] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_FOOTER;
        temp.images = BOPathConstants.PATH_IMAGE_SHEET;
        temp.javascript = null;

        this.elems[LayoutConstants.FOOTER] = temp;

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 1;
        this.specificChangeParameters = 1;
    } // initClassSpecifics



    /**************************************************************************
     * This method gets the isDefault value. <BR/>
     *
     * @return Returns the isDefault value.
     */
    public boolean isDefault ()
    {
        //get the property value and return the result:
        return this.p_isDefault;
    } // isDefault


    /**************************************************************************
     * This method sets the isDefault value. <BR/>
     *
     * @param isDefault The isDefault value to set.
     */
    public void setDefault (boolean isDefault)
    {
        //set the property value:
        this.p_isDefault = isDefault;
    } // setDefault


    /**************************************************************************
     * Read form the User the data used in the Object. <BR/>
     */
    @Override
    public void getParameters ()
    {
        int num = 0;
        boolean updateDefaultLayout = false;

        super.getParameters ();

        // isDefault:
        if ((num = this.env.getBoolParam (BOArguments.ARG_ISDEFAULT)) >=
            IOConstants.BOOLPARAM_FALSE)
        {
            updateDefaultLayout = !this.p_isDefault;
            this.p_isDefault = num == IOConstants.BOOLPARAM_TRUE;

            // check if this layout has changed from not default to default:
            updateDefaultLayout = updateDefaultLayout && this.p_isDefault;
            if (updateDefaultLayout)
            {
                // update the default layout entry within the container:
                this.ensureUniqueDefaultLayout ();
            } // if
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
        // add specific parameters:
        // isDefault:
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, this.p_isDefault);
    } //setSpecificChangeParameters


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
        // isDefault:
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_BOOLEAN);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


    /***************************************************************************
     * Get the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * type specific data from the retrieve data stored procedure.
     *
     * @param params        The array of parameters from the retrieve data stored
     *                   procedure.
     * @param lastIndex The index to the last element used in params thus far.
     */
    @Override
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // get the specific parameters:
        this.p_isDefault = params[++i].getValueBoolean ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Reads the object data from an dataElement. <BR/>
     *
     * @param dataElement     the dataElement to read the data from
     *
     * @see ibs.bo.BusinessObject#readImportData
     */
    @Override
    public void readImportData (DataElement dataElement)
    {
        boolean updateDefaultLayout = false;

        // get business object specific values:
        super.readImportData (dataElement);

        // get the type specific values:
        if (dataElement.exists (Layout_01.FIELD_ISDEFAULT))
        {
            updateDefaultLayout = !this.p_isDefault;
            // get the value:
            this.p_isDefault =
                dataElement.getImportBooleanValue (Layout_01.FIELD_ISDEFAULT);

            // check if this layout has changed from not default to default:
            updateDefaultLayout = updateDefaultLayout && this.p_isDefault;
            if (updateDefaultLayout)
            {
                // update the default layout entry within the container:
                this.ensureUniqueDefaultLayout ();
            } // if
        } // if (url == null)
    } // readImportData


    /**************************************************************************
     * Writes the object data to an dataElement. <BR/>
     *
     * @param dataElement     the dataElement to write the data to
     *
     * @see ibs.bo.BusinessObject#writeExportData
     */
    @Override
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values:
        super.writeExportData (dataElement);

        // export the isDefault value:
        dataElement.setExportValue (Layout_01.FIELD_ISDEFAULT, this.p_isDefault);
    } // writeExportData


    ///////////////////////////////////////////////////////////////////////////
    // viewing functions
    ///////////////////////////////////////////////////////////////////////////


    /***************************************************************************
     * Represent the properties of a Attachment_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties should be added.
     *
     * @see ibs.bo.BusinessObject#showProperties
     * @see ibs.IbsObject#showProperty(TableElement, String, String, int, User, java.util.Date)
     */
    @Override
    protected void showProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showProperties (table); // name, type, description

        this.showProperty (table, BOArguments.ARG_ISDEFAULT, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ISDEFAULT, env),
            Datatypes.DT_BOOL, "" + this.p_isDefault);
    } //  showProperties


    /***************************************************************************
     * Represent the properties of a Dokument_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showFormProperties
     * @see ibs.IbsObject#showFormProperty(TableElement, String, String, int, User)
     */
    @Override
    protected void showFormProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showFormProperties (table);

        if (this.p_isDefault)           // you can only change if the object
                                        // is not default
        {
            this.showProperty (table, BOArguments.ARG_ISDEFAULT,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ISDEFAULT, env), Datatypes.DT_BOOL,
                "" + this.p_isDefault);
            this.showFormProperty (table, BOArguments.ARG_ISDEFAULT,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ISDEFAULT, env), Datatypes.DT_HIDDEN,
                "" + this.p_isDefault);
        } // if you can only change if the object is not default
        else                            // change isDefault flag if you want
        {
            this.showFormProperty (table, BOArguments.ARG_ISDEFAULT,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ISDEFAULT, env), Datatypes.DT_BOOL,
                "" + this.p_isDefault);
        } // else change isDefault flag if you want
    } // showFormProperties


    /***************************************************************************
     * Esnure that the container has exactly one default layout. <BR/>
     */
    protected void ensureUniqueDefaultLayout ()
    {
        LayoutContainer_01 container = null;

        // get the container object:
        container = (LayoutContainer_01) BOHelpers.getObject (
            this.containerId, this.env, false, false, false);

        // check if we got the object:
        if (container != null)
        {
            // tell the container to ensure that there is only one default layout:
            container.setDefaultLayout (this);
        } // if
    } // ensureUniqueDefaultLayout

} // class Layout_01

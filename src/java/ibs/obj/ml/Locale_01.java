/*
 * Class: Locale_01.java
 */

// package:
package ibs.obj.ml;

// imports:
import ibs.app.AppConstants;
import ibs.app.AppFunctions;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOMessages;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.BusinessObjectInfo;
import ibs.bo.ContainerElement;
import ibs.bo.Datatypes;
import ibs.bo.ObjectNotAffectedException;
import ibs.bo.Operations;
import ibs.bo.States;
import ibs.di.DataElement;
import ibs.di.KeyMapper;
import ibs.di.KeyMapper.ExternalKey;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.io.LayoutElement;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.user.UserProfile_01;
import ibs.service.user.User;
import ibs.tech.html.BuildException;
import ibs.tech.html.Page;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.TableElement;
import ibs.tech.http.HttpArguments;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.SelectQuery;
import ibs.tech.sql.StoredProcedure;
import ibs.util.DependentObjectExistsException;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * Locale_01 handles the configuration for locales. <BR/>
 *
 * @version     $Id: Locale_01.java,v 1.7 2013/01/16 16:14:14 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann (BT), 20100311
 ******************************************************************************
 */
public class Locale_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Locale_01.java,v 1.7 2013/01/16 16:14:14 btatzmann Exp $";


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
     * The language of the locale <BR/>
     */
    protected String p_language = "";

    /**
     * The country of the locale <BR/>
     */
    protected String p_country = "";
    
    /**
     * Is this the default layout? <BR/>
     * Default: <CODE>false</CODE>
     */
    protected boolean p_isDefault = false;

    /**
     * fieldname: language. <BR/>
     */
    public static final String FIELD_LANGUAGE = "language";

    /**
     * fieldname: country. <BR/>
     */
    public static final String FIELD_COUNTRY = "country";
    
    /**
     * fieldname: isDefault. <BR/>
     */
    public static final String FIELD_ISDEFAULT = "isDefault";

    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor creates a new instance of the class Locale_01. <BR/>
     */
    public Locale_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // Locale_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    @Override
    public void initClassSpecifics ()
    {
        // set common attributes:
        this.procCreate = "p_Locale_01$create";
        this.procChange = "p_Locale_01$change";
        this.procRetrieve = "p_Locale_01$retrieve";
        this.procDelete = "p_Locale_01$delete";

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

        // set number of parameters for procedure call:
        this.specificRetrieveParameters = 3;
        this.specificChangeParameters = 3;
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
        String str = null;
        int num = 0;
//        boolean updateDefaultLayout = false;

        super.getParameters ();

        // get the language:
        if ((str = this.env.getStringParam (LocaleArguments.ARG_LANGUAGE)) != null)
        {
            this.p_language = str;
        } // if

        // get the country:
        if ((str = this.env.getStringParam (LocaleArguments.ARG_COUNTRY)) != null)
        {
            this.p_country = str;
        } // if
        
        // isDefault:
        if ((num = this.env.getBoolParam (BOArguments.ARG_ISDEFAULT)) >=
            IOConstants.BOOLPARAM_FALSE)
        {
//            updateDefaultLayout = !this.p_isDefault;
            this.p_isDefault = num == IOConstants.BOOLPARAM_TRUE;

//            // check if this layout has changed from not default to default:
//            updateDefaultLayout = updateDefaultLayout && this.p_isDefault;
//            if (updateDefaultLayout)
//            {
//                // update the default layout entry within the container:
//                this.ensureUniqueDefaultLayout ();
//            } // if
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
        // language:
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.p_language);
        
        // country:
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.p_country);
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
        // language:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // country:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // isDefault:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);

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
        this.p_language = params[++i].getValueString ();
        this.p_country = params[++i].getValueString ();
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
        // get business object specific values:
        super.readImportData (dataElement);

        // get the type specific values:
        if (dataElement.exists (Locale_01.FIELD_LANGUAGE))
        {
            // get the value:
            this.p_language =
                dataElement.getImportStringValue (Locale_01.FIELD_LANGUAGE);
        } // if
        
        if (dataElement.exists (Locale_01.FIELD_COUNTRY))
        {
            // get the value:
            this.p_country =
                dataElement.getImportStringValue (Locale_01.FIELD_COUNTRY);
        } // if
        
        if (dataElement.exists (Locale_01.FIELD_ISDEFAULT))
        {
            // get the value:
            this.p_isDefault =
                dataElement.getImportBooleanValue (Locale_01.FIELD_ISDEFAULT);
        } // if
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
        dataElement.setExportValue (Locale_01.FIELD_LANGUAGE, this.p_language);
        dataElement.setExportValue (Locale_01.FIELD_COUNTRY, this.p_country);
        dataElement.setExportValue (Locale_01.FIELD_ISDEFAULT, this.p_isDefault);
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
//        super.showProperties (table); // name, type, description

        this.showProperty (table, LocaleArguments.ARG_LANGUAGE,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LOCALE_LANGUAGE, this.env),
            Datatypes.DT_TEXT, "" + this.p_language);
        
        this.showProperty (table, LocaleArguments.ARG_COUNTRY,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LOCALE_COUNTRY, this.env),
                Datatypes.DT_TEXT, "" + this.p_country);
        
        this.showProperty (table, LocaleArguments.ARG_ISDEFAULT,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_IS_DEFAULT, this.env),
                Datatypes.DT_BOOL, "" + this.p_isDefault);
    } //  showProperties


    /***************************************************************************
     * Represent the properties of a Locale_01 object to the user
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
        //super.showFormProperties (table);

        this.showFormProperty (table, LocaleArguments.ARG_LANGUAGE,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LOCALE_LANGUAGE, this.env),
                Datatypes.DT_TEXT,
                "" + this.p_language);
        
        this.showFormProperty (table, LocaleArguments.ARG_COUNTRY,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LOCALE_COUNTRY, this.env),
                Datatypes.DT_TEXT,
                "" + this.p_country);
        
        this.showFormProperty (table, LocaleArguments.ARG_ISDEFAULT,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_IS_DEFAULT, this.env),
                Datatypes.DT_BOOL,
                "" + this.p_isDefault);
    } // showFormProperties


//    /***************************************************************************
//     * Esnure that the container has exactly one default layout. <BR/>
//     */
//    protected void ensureUniqueDefaultLayout ()
//    {
//        LayoutContainer_01 container = null;
//
//        // get the container object:
//        container = (LayoutContainer_01) BOHelpers.getObject (
//            this.containerId, this.env, false, false, false);
//
//        // check if we got the object:
//        if (container != null)
//        {
//            // tell the container to ensure that there is only one default layout:
//            container.setDefaultLayout (this);
//        } // if
//    } // ensureUniqueDefaultLayout

    /**************************************************************************
     * Change the data of a business object in the database. <BR/>
     * <B>THIS METHOD IS A DUMMY WHICH MUST BE OVERWRITTEN IN SUB CLASSES!</B>
     * <BR/>
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
     * @exception   ibs.util.NameAlreadyGivenException
     *              An object with this name already exists. This exception is
     *              only raised by some specific object types which don't allow
     *              more than one object with the same name.
     */
    protected void performChangeData (int operation)
        throws NoAccessException, NameAlreadyGivenException
    {     
        // initialize variables
        boolean created = this.state == States.ST_CREATED;         
        boolean wasDefault = MultilingualTextProvider.getDefaultLocale ().oid.equals (this.oid);
        
        // check if the default locale flag is removed      
        if (wasDefault && !this.isDefault ())
        {
            IOHelpers.showMessage (BOMessages.ML_MSG_DEFAULT_LOCALE_FLAG_CAN_NOT_BE_REMOVED, this.env);
            
            return;
        } // if
        
        // set the name
        this.name = this.p_language.substring (0, 2) + "_" + this.p_country.substring (0, 2);
        
        // instantiate the Keymapper to resolve the external object key:
        KeyMapper keyMapper = new KeyMapper (this.user, this.env, this.sess, this.app);

        // try to resolve the external key:
        KeyMapper.ExternalKey extKey = keyMapper.performResolveMapping (this.oid, false);
       
        // set the external key
        extKey = new ExternalKey (
                this.oid,
                "ibs_instobj", "locale_" + this.name
                );
        
        if (!keyMapper.performCreateMapping (extKey))
        {
            IOHelpers.showMessage(
                    "Error during creating/changing locale. Another locale with the same ext key already exists. Locale is not created/changed.",
                    this.app, this.sess, this.env);
            return;
        } // if
        
        super.performChangeData (operation);

        // check if the current locale is set as default locale
        if (this.isDefault ())
        {
            // set the new default locale
            MultilingualTextProvider.setDefaultLocale (this);
            
            // remove the default locale flag from other locales
            this.setDefaultLocale ();  
            
            // check the default locale was not the default locale before
            if (!created && !wasDefault)
            {
                IOHelpers.showPopupMessage (
                        BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_DEFAULT_LOCALE_CHANGED,
                        null, this.app, this.sess, this.env);
            } // if
        } // if
        
        // reset the locale cache to be reiintialized on next access
        // if new locales have been added
        MultilingualTextProvider.resetLocaleCache (this.env);
        
        // Check if the locales has just been created an show a message
        if (created)
        {
            IOHelpers.showPopupMessage (
                    BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_LOCALE_ADDED,
                    null, this.app, this.sess, this.env);
        } // if
    } // performChangeData
    
    
    /**************************************************************************
     * Delete a business object from the database. <BR/>
     * First this method tries to delete the object from the database.
     * During this operation a rights check is done, too.
     * If this is all right the object is deleted and this method terminates
     * otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ObjectNotAffectedException
     *              The operation could not be performed on all required
     *              objects.
     * @exception   DependentObjectExistsException
     *              The object could not be deleted because there are still
     *              objects which are refer to this object.
     */
    protected void performDeleteData (int operation)
        throws NoAccessException, ObjectNotAffectedException,
               DependentObjectExistsException
    {          
        // check if it is default locale
        if (isDefault ())
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_DEFAULT_LOCALE_CAN_NOT_BE_DELETED, this.env), this.env);
            
            return;
        } // if
        
        // create query for retrieving user profile with references to the locale to delete
        SelectQuery query = new SelectQuery (
                new StringBuilder ("o.oid, o.state, o.name, o.typename"),
                new StringBuilder ("ibs_userprofile p, ibs_object o"),
                null, null, null,
                new StringBuilder ("o.name ASC"));

        // add filters to query:
        query.extendWhere (new StringBuilder (SQLHelpers.createQueryFilter ("o.state", new StringBuilder (new Integer (States.ST_ACTIVE).toString ()))));
        query.extendWhere (new StringBuilder (SQLHelpers.createQueryFilter ("p.oid", new StringBuilder ("o.oid"))));
        query.extendWhere (new StringBuilder (SQLHelpers.createQueryFilter ("p.localeId", new StringBuilder (this.oid.toStringQu ()))));
            
        // retrieve the references
        Vector<BusinessObjectInfo> objects = BOHelpers.findObjects(query, env);
        
        // set the user profile to the default locale
        if (objects != null)
        {
            for (Iterator<BusinessObjectInfo> iter = objects.iterator (); iter.hasNext ();)
            {
                BusinessObjectInfo info = (BusinessObjectInfo) iter.next ();
    
                // retrieve the locale object without rights check
                UserProfile_01 userProfile = (UserProfile_01) BOHelpers.getObject (info.p_oid, env, false, false, true);
                
                try
                {
                    IOHelpers.showMessage ("Changing user profile to default locale: "+ userProfile.oid, this.env);
                    
                    // set the default locale
                    userProfile.localeId = MultilingualTextProvider.getDefaultLocale ().oid;
                    userProfile.localeName = MultilingualTextProvider.getDefaultLocale ().name;
                    userProfile.performChange (Operations.OP_CHANGE);
                } // try
                catch (NameAlreadyGivenException e)
                {
                    IOHelpers.showMessage ("Error during setting user profile to default locale for profile with oid: " + userProfile.oid, env);
                } // catch
            } // for iter
        } // if

        super.performDeleteData(operation);
    } // performDeleteData
    
    
    /**************************************************************************
     * Show a delete confirmation message to the user. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    protected void performShowDeleteConfirmation (int representationForm)
    {       
        // check if the current locale is not the default locale
        // then deletion is allowed
        if (!isDefault ())
        {
            // BEGIN of standard method except call this.checkAdditionalReferences () 
            int countReferences = 0;        // number of references
            Page page = new Page ("DeleteConfirmation", false);
            String url = this.getBaseUrl () +
                HttpArguments.createArg (BOArguments.ARG_FUNCTION, AppFunctions.FCT_OBJECTDELETE) +
                HttpArguments.createArg (BOArguments.ARG_OID, "" + this.oid) +
                HttpArguments.createArg (BOArguments.ARG_CONTAINERID, "" + this.containerId);
            String message = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                this.msgDeleteConfirm, env); // the message for the user

            // check if there are any references to the objects which shall be
            // deleted:
            countReferences = this.checkReferences (true) +
                // check additional references
                this.checkAdditionalReferences ();
            
            // set the correct message:
            if (countReferences > 0)        // at least one reference found?
            {
                message = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                   BOMessages.ML_MSG_LOCALEDELETECONFIRMREF,
                   new String[] {"" + countReferences}, env);
            } // if at least one reference found

            // show script:
            ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
            script.addScript (
                "if (confirm (\"" +
                StringHelpers.replace (message, UtilConstants.TAG_NAME, this.name) +
                "\"))\n" +
                "top.callUrl (\"" + url + "\", null, null, \"" + this.frmSheet + "\");\n\n");
            page.body.addElement (script);

            // build the page and show it to the user:
            try
            {
                page.build (this.env);
            } // try
            catch (BuildException e)
            {
                IOHelpers.showMessage (e.getMsg (), this.app, this.sess, this.env);
            } // catch
            // END of standard method except call this.checkAdditionalReferences ()
        } // if
        else
        {
            Page page = new Page ("DeleteDefaultLocale", false);
    
            // show alert: 'The default locale can not be deleted'
            ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
            
            script.addScript ("alert ('" + MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_DEFAULT_LOCALE_CAN_NOT_BE_DELETED, this.env) + "');");
            page.body.addElement (script);
    
            // build the page and show it to the user:
            try
            {
                page.build (this.env);
            } // try
            catch (BuildException e)
            {
                IOHelpers.showMessage (e.getMsg (), this.app, this.sess, this.env);
            } // catch
        } // else
    } // performShowDeleteConfirmation
    
    
    /**************************************************************************
     * Check if there are any additional references on the actual object and
     * its subsequent objects. <BR/>
     *
     * @return  The number of references or <CODE>0</CODE> if there are no
     *          references found.
     */
    protected int checkAdditionalReferences ()
    {
        int references = 0;
        SQLAction action = null;        // the action object used to access the DB
        
        // create the SQL Query to select the objects:
        SelectQuery query = new SelectQuery (
            new StringBuilder ("o.oid"),
            new StringBuilder ("ibs_userProfile p, ibs_object o"),
            null,
            null, null, null);

        // add filters to query:
        query.extendWhere (new StringBuilder (SQLHelpers.createQueryFilter ("o.state", new StringBuilder (new Integer (States.ST_ACTIVE).toString ()))));
        query.extendWhere (new StringBuilder (SQLHelpers.createQueryFilter ("p.oid", new StringBuilder ("o.oid"))));
        query.extendWhere (new StringBuilder (SQLHelpers.createQueryFilter ("p.localeId", new StringBuilder (this.oid.toStringQu ()))));
        
        try
        {
            action = BOHelpers.getDBConnection (env);
            references = action.execute (query);

            // finish transaction:
            action.end ();
        } // try
        catch (DBError e)
        {
            // show error message:
            IOHelpers.showMessage (e, env, true);
        } // catch
        finally
        {
            // the database connection is not longer needed:
            BOHelpers.releaseDBConnection (action, env);
        } // finally
        
        return references;
    } // checkUserReferences
    

    /***************************************************************************
     * Removes the default locale flag for all other locales. <BR/>´
     * 
     * @throws NameAlreadyGivenException 
     * @throws NoAccessException 
     */
    protected void setDefaultLocale () throws NoAccessException, NameAlreadyGivenException
    {
        // get the locales container object:
        LocaleContainer_01 container = (LocaleContainer_01) BOHelpers.getObject (
            this.containerId, this.env, false, false, false);
        
        try
        {
            // get all the locales:
            container.retrieveContent (Operations.OP_NONE, 0, BOConstants.ORDER_ASC);
        } // try
        catch (NoAccessException e)
        {
            // cannot occur
        } // catch

        // iterate through all locales and set them to non default
        for (Iterator<ContainerElement> iter = container.elements.iterator (); iter.hasNext ();)
        {
            ContainerElement elem = (ContainerElement) iter.next ();

            Locale_01 localeElem = (Locale_01) BOHelpers.getObject (
                    elem.oid, this.env, false, false, false);
            
            // check if the locale is not the acutal and is set as default
            if (!elem.oid.equals (this.oid) && localeElem.isDefault ())
            {
                localeElem.setDefault (false);
                localeElem.performChange (Operations.OP_CHANGE);
            } // if
        } // for iter
    } // setDefaultLocale
    

    /**************************************************************************
     * Returns a <code>Locale</code> object. <BR/>
     *
     * @return Returns the locale.
     */
    public java.util.Locale getLocale ()
    {
        // Create a locale from language and country
        java.util.Locale locale = new java.util.Locale (
                this.p_language, this.p_country);
        
        return locale;
    } // getLocale
    
    
    /**************************************************************************
     * Returns a representation for the locale, which can be used as key
     * within maps holding texts for several locales.
     *
     * @return Returns the locale key.
     */
    public String getLocaleKey ()
    {
        // Create a locale from language and country
        return this.getLocale ().toString ();
    } // getLocaleKey
} // class Locale_01

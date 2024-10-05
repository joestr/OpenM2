/*
 * Class: UserProfile_01.java
 */

// package:
package ibs.obj.user;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOHelpers;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.io.IOConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.layout.LayoutContainerElement_01;
import ibs.obj.ml.Locale_01;
import ibs.service.user.User;
import ibs.tech.html.GroupElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.TableElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.util.DateTimeHelpers;
import ibs.util.FormFieldRestriction;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;

import java.util.Date;


/******************************************************************************
 * This class represents one object of type UserProfile with version 01. <BR/>
 *
 * @version     $Id: UserProfile_01.java,v 1.34 2013/01/16 16:14:10 btatzmann Exp $
 *
 * @author      Bernd Buchegger (BB), 980709
 ******************************************************************************
 */
public class UserProfile_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: UserProfile_01.java,v 1.34 2013/01/16 16:14:10 btatzmann Exp $";


    /**
     * time limit for the newslist. <BR/>
     */
    public int newsTimeLimit;

    /**
     * flag to show only unread messages in newslist. <BR/>
     */
    public boolean newsShowOnlyUnread = false;

    /**
     * flag to use time limit filter in outbox. <BR/>
     */
    public boolean outboxUseTimeLimit = false;

    /**
     * time limit filter for outbox (in days). <BR/>
     */
    public int outboxTimeLimit;

    /**
     * flag to use time frame filter in outbox. <BR/>
     */
    public boolean outboxUseTimeFrame = false;

    /**
     * begin date of time frame filter in outbox. <BR/>
     */
    public Date outboxTimeFrameFrom;

    /**
     * end date of time frame filter in outbox. <BR/>
     */
    public Date outboxTimeFrameTo;

    /**
     * flag to show complete object attributes. <BR/>
     */
    public boolean showExtendedAttributes = false;

    /**
     * flag to show files in a separate window. <BR/>
     */
    public boolean showFilesInWindows = false;

    /**
     * date of last login. <BR/>
     */
    public Date lastLogin;

    /**
     * Oid of the active layout. <BR/>
     */
    public OID layoutId = null;

    /**
     * Oid of the active locale. <BR/>
     */
    public OID localeId = null;
    
    /**
     * Name of the actual layout. <BR/>
     */
    public String layoutName = "";

    /**
     * Name of the actual locale. <BR/>
     */
    public String localeName = "";
    
    /**
     * . <BR/>
     */
    public boolean showRef = !this.showExtendedAttributes;

    /**
     * flag to indicate if the right aliases or all possible rights should be shown
     */
    public boolean showExtendedRights = false;

    /**
     * flag to indicate if a Cookie should be stored when logging in
     */
    public boolean saveProfile = false;

    /**
     * number (1-3) to indicate what notification Kind the user prefers
     */
    public int notificationKind = 0;

    /**
     * flag to indicate if notification should be performed via sms
     */
    public boolean sendSms = false;

    /**
     * flag to indicate if a weblink should be added
     */
    public boolean addWeblink = false;


    /**
     * The locale assigned to the user profile.
     */
    private Locale_01 locale = null;

    /**
     * Defines if the multilang client info should be reloaded
     */
    private boolean reloadMultilangClientInfo = true;

    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor creates a new instance of the class UserProfile_01.
     * <BR/>
     */
    public UserProfile_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // UserProfile_01


    /**************************************************************************
     * This constructor creates a new instance of the class UserProfile_01. <BR/>
     * The compound object id is used as base for getting the
     * <A HREF="#server">server</A>, <A HREF="#type">type</A>, and
     * <A HREF="#id">id</A> of the business object. These values are stored in
     * the special public attributes of this type. <BR/>
     * The <A HREF="#user">user object</A> is also stored in a specific
     * attribute of this object to make sure that the user's context can be
     * used for getting his/her rights.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public UserProfile_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // UserProfile_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set common attributes:
        this.procCreate = "p_UserProfile_01$create";
        this.procChange = "p_UserProfile_01$change";
        this.procRetrieve = "p_UserProfile_01$retrieve";
        this.procDelete = "p_UserProfile_01$delete";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 22;
        this.specificChangeParameters = 17;

        // set the instance's attributes:
    } // initClassSpecifics


    ///////////////////////////////////////////////////////////////////////////
    // functions called from application level
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Read form the User the data used in the Object. <BR/>
     */
    public void getParameters ()
    {
        // declare needed variables
        String[] strArr = null;
        int num = 0;
        String outboxfilter = null;
        Date date;

        // get parameters relevant for super class:
        super.getParameters ();

        // outbox configuration
        if ((outboxfilter = this.env.getParam (BOArguments.ARG_OUTBOXFILTER)) != null)
        {
            // outboxUseTimeLimit
            if (outboxfilter.equals (UserConstants.OUTBOX_TIMELIMIT))
            {
                this.outboxUseTimeLimit = true;
            } // if
            else
            {
                this.outboxUseTimeLimit = false;
            } // else

            // outboxUseTimeFrame
            if (outboxfilter.equals (UserConstants.OUTBOX_TIMEFRAME))
            {
                this.outboxUseTimeFrame = true;
            } // if
            else
            {
                this.outboxUseTimeFrame = false;
            } // else
        } // if

         // set userProfile_01 specific object attributes

        // get newsTimeLimit
        if ((num = this.env.getIntParam (BOArguments.ARG_NEWSTIMELIMIT)) != IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.newsTimeLimit = num;
        } // if
        if (this.newsTimeLimit < 0)
        {
            this.newsTimeLimit = 1;
        } // if
        // newsShowOnlyUnread

        if ((num = this.env.getBoolParam (BOArguments.ARG_NEWSSHOWONLYUNREAD)) >= IOConstants.BOOLPARAM_FALSE)
        {
            this.newsShowOnlyUnread = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        // outboxTimeLimit
        if ((num = this.env.getIntParam (BOArguments.ARG_OUTBOXTIMELIMIT)) != IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.outboxTimeLimit = num;
        } // if
        if (this.outboxTimeLimit < 0)
        {
            this.outboxTimeLimit = 0;
        } // if

        // outboxTimeFrameFrom
        if ((date = this.env.getDateParam (BOArguments.ARG_OUTBOXTIMEFRAME)) != null)
        {
            this.outboxTimeFrameFrom = date;
        } // if
        // outboxTimeFrameTo
        if ((date = this.env.getDateParam (BOArguments.ARG_OUTBOXTIMEFRAME +
            BOArguments.ARG_RANGE_EXTENSION)) != null)
        {
            this.outboxTimeFrameTo = date;
        } // if

        // showExtendedAttributes
        if ((num = this.env
            .getBoolParam (BOArguments.ARG_SHOWEXTENDEDATTRIBUTES)) >= IOConstants.BOOLPARAM_FALSE)
        {
            this.showExtendedAttributes = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        // showFilesInWindows
        if ((num = this.env.getBoolParam (BOArguments.ARG_SHOWFILESINWINDOWS)) >= IOConstants.BOOLPARAM_FALSE)
        {
            this.showFilesInWindows = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        // lastLogin - cannot be edited

        // layoutId
        OID layoutId;
        if ((layoutId = this.env.getOidParam (BOArguments.ARG_LAYOUTID)) != null)
        {
            this.layoutId = layoutId;
        } // if

        // localeId
        OID localeId;
        if ((localeId = this.env.getOidParam (BOArguments.ARG_LOCALEID)) != null)
        {
            this.localeId = localeId;
        } // if
        
        if ((num = this.env.getBoolParam (BOArguments.ARG_USERIGHTALIASES)) >= IOConstants.BOOLPARAM_FALSE)
        {
            this.showExtendedRights = !(num == IOConstants.BOOLPARAM_TRUE);
        } // if

        if ((num = this.env.getBoolParam (BOArguments.ARG_SAVEPROFILE)) >= IOConstants.BOOLPARAM_FALSE)
        {
            this.saveProfile = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        //notificationKind
        if ((strArr = this.env
            .getMultipleFormParam (BOArguments.ARG_NOTIFICATIONKIND)) != null)
        {
            this.notificationKind = 0;

            // create bit pattern for selected notificationKinds
            for (int i = 0; i < strArr.length; i++)
            {
                this.notificationKind +=
                    Integer.valueOf (strArr[i]).intValue ();
            } // for
        } // if

        //additional sms notification ?
        if ((num = this.env.getBoolParam (BOArguments.ARG_SMSACTIV)) >= IOConstants.BOOLPARAM_FALSE)
        {
            this.sendSms = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        //add a weblink ?
        if ((num = this.env.getBoolParam (BOArguments.ARG_ADDWEBLINK)) >= IOConstants.BOOLPARAM_FALSE)
        {
            this.addWeblink = num == IOConstants.BOOLPARAM_TRUE;
        } // if
    } // getParameters


    ///////////////////////////////////////////////////////////////////////////
    // database functions
    ///////////////////////////////////////////////////////////////////////////

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
        // newsTimeLimit
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // newsShowOnlyUnread
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // outboxUseTimeLimit
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // outboxTimeLimit
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // outboxUseTimeFrame
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // outboxTimeFrameFrom
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_DATE);
        // outboxTimeFrameTo
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_DATE);
        // showExtendedAttributes
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // showFilesInWindows
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // lastLogin
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_DATE);
        // m2AbsBasePath
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // homePath
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // layoutId
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // layoutName
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // showRef
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // showExtendedRights
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // saveProfile
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
         // notificationKind
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
         // add SmS Notifikation
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
         // add Weblink
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // localeId
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // localeName
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        
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
    * @param lastIndex    The index to the last element used in params thus far.
    */
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // get type-specific properties:
        this.newsTimeLimit = params[++i].getValueInteger ();
        this.newsShowOnlyUnread = params[++i].getValueBoolean ();
        this.outboxUseTimeLimit = params[++i].getValueBoolean ();
        this.outboxTimeLimit = params[++i].getValueInteger ();
        this.outboxUseTimeFrame = params[++i].getValueBoolean ();
        this.outboxTimeFrameFrom = params[++i].getValueDate ();
        this.outboxTimeFrameTo = params[++i].getValueDate ();
        this.showExtendedAttributes = params[++i].getValueBoolean ();
        this.showFilesInWindows = params[++i].getValueBoolean ();
        this.lastLogin = params[++i].getValueDate ();
        // don't use the abs base path:
        ++i;
        // don't use the www base path:
        ++i;
        this.layoutId = SQLHelpers.getSpOidParam (params[++i]);
        this.layoutName = params[++i].getValueString ();
        this.showRef = params[++i].getValueBoolean ();
        this.showExtendedRights = params[++i].getValueBoolean ();
        this.saveProfile = params[++i].getValueBoolean ();
        this.notificationKind = params[++i].getValueInteger ();
        this.sendSms = params[++i].getValueBoolean ();
        this.addWeblink = params[++i].getValueBoolean ();
        this.localeId = SQLHelpers.getSpOidParam (params[++i]);
        this.localeName = params[++i].getValueString ();
    } // getSpecificRetrieveParameters


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
        // newsTimeLimit
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            this.newsTimeLimit);
        //newsShowOnlyUnread
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
            this.newsShowOnlyUnread);
        // outboxUseTimeLimit
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
            this.outboxUseTimeLimit);
        // outboxTimeLimit
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            this.outboxTimeLimit);
        // outboxUseTimeFrame
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
            this.outboxUseTimeFrame);
        // outboxTimeFrameFrom
        sp.addInParameter (ParameterConstants.TYPE_DATE,
            this.outboxTimeFrameFrom);
        // outboxTimeFrameTo
        sp.addInParameter (ParameterConstants.TYPE_DATE,
            this.outboxTimeFrameTo);
        // showExtendedAttributes
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
            this.showExtendedAttributes);
        // showFilesInWindows
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
            this.showFilesInWindows);
        // lastLogin
        sp.addInParameter (ParameterConstants.TYPE_DATE,
            this.lastLogin);
        // layoutId
        BOHelpers.addInParameter (sp, this.layoutId);
        // showExtendedRights
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
            this.showExtendedRights);
        // saveProfile
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
            this.saveProfile);
         // notificationKind
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            this.notificationKind);
         // sendSms
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
            this.sendSms);
         // addWeblink
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
            this.addWeblink);
        // localeId
        BOHelpers.addInParameter (sp, this.localeId);
    } // setSpecificChangeParameters


    ///////////////////////////////////////////////////////////////////////////
    // viewing functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Represent the properties of a UserProfile_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {       
        String timeFrame = "";

        // display the base object's properties:
//        super.showProperties (table);
        // loop through all properties of this object and display them:

        // newsTimeLimit
        if (this.newsTimeLimit < 0)
        {
            this.newsTimeLimit = 0;
        } // if
        this.showProperty (table, BOArguments.ARG_NEWSTIMELIMIT,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NEWSTIMELIMIT, env), Datatypes.DT_INTEGER,
            this.newsTimeLimit);
        // newsShowOnlyUnread
        this.showProperty (table, BOArguments.ARG_NEWSSHOWONLYUNREAD,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NEWSSHOWONLYUNREAD, env), Datatypes.DT_BOOL,
            "" + this.newsShowOnlyUnread);
        // separator line
//        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);
        // ftpProxy
//        this.showProperty (table, BOArguments.ARG_FTPPROXY, TOK_FTPPROXY,
//            Datatypes.DT_TEXT, this.ftpProxy);
        // ftpProxyBypass
//        this.showProperty (table, BOArguments.ARG_FTPPROXYBYPASS, TOK_FTPPROXYBYPASS,
//            Datatypes.DT_TEXT, this.ftpProxyBypass);
        // separator line
        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);
        // outboxUseTimeLimit
        if (this.outboxTimeLimit < 0)
        {
            this.outboxTimeLimit = 0;
        } // if
        this.showProperty (table, BOArguments.ARG_OUTBOXUSETIMELIMIT,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OUTBOXUSETIMELIMIT, env), Datatypes.DT_BOOL,
            "" + this.outboxUseTimeLimit);
        // outboxTimeLimit
        this.showProperty (table, BOArguments.ARG_OUTBOXTIMELIMIT,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OUTBOXTIMELIMIT, env), Datatypes.DT_INTEGER,
            this.outboxTimeLimit);
        // outboxUseTimeFrame
        this.showProperty (table, BOArguments.ARG_OUTBOXUSETIMEFRAME,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OUTBOXUSETIMEFRAME, env), Datatypes.DT_BOOL,
            "" + this.outboxUseTimeFrame);

        // outboxTimeFrameFrom - outboxTimeFrameTo
        if (this.outboxTimeFrameFrom != null && this.outboxTimeFrameTo != null)
        {
            timeFrame = DateTimeHelpers.dateToString (this.outboxTimeFrameFrom) + " - " +
                DateTimeHelpers.dateToString (this.outboxTimeFrameTo);
        } // if
        else if (this.outboxTimeFrameFrom != null)
        {
            timeFrame = DateTimeHelpers.dateToString (this.outboxTimeFrameFrom) + " - ";
        } // else if
        else if (this.outboxTimeFrameTo != null)
        {
            timeFrame = " - "  + DateTimeHelpers.dateToString (this.outboxTimeFrameTo);
        } // else if
        else
        {
            timeFrame = "";
        } // else
        this.showProperty (table, BOArguments.ARG_OUTBOXTIMEFRAME,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OUTBOXTIMEFRAME, env), Datatypes.DT_TEXT, timeFrame);
        // separator line
        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);
        // showExtendedAttributes
        this.showProperty (table, BOArguments.ARG_SHOWEXTENDEDATTRIBUTES,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SHOWEXTENDEDATTRIBUTES, env), Datatypes.DT_BOOL,
            "" + this.showExtendedAttributes);
        // showFilesInWindows
        this.showProperty (table, BOArguments.ARG_SHOWFILESINWINDOWS,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SHOWFILESINWINDOWS, env), Datatypes.DT_BOOL,
            "" + this.showFilesInWindows);
        // showExtendedRights
        this.showProperty (table, BOArguments.ARG_USERIGHTALIASES,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_USERIGHTALIASES, env), Datatypes.DT_BOOL,
            "" + !this.showExtendedRights);
        // lastLogin
        if (this.lastLogin == null)
        {
            this.showProperty (table, BOArguments.ARG_LASTLOGIN,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LASTLOGIN, env), Datatypes.DT_TEXT, "");
        } // if
        else
        {
            this.showProperty (table, BOArguments.ARG_LASTLOGIN,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LASTLOGIN, env), Datatypes.DT_DATETIME,
                DateTimeHelpers.dateToString (this.lastLogin));
        } // else
        this.showProperty (table, BOArguments.ARG_SAVEPROFILE,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SAVEPROFILE, env), Datatypes.DT_BOOL, "" + this.saveProfile);

        //show the notification-kind
        // String for choosen notifikationKinds
        String notifikationKindString = "";

        // map bit pattern this.notifikationKind to an String with
        // the names of the choosen notifikationkinds
        int j = 1;
        for (int i = 0;
             i < BOTokens.ML_NOTIFIKATION_SELECTION.length;
             i++, j *= 2)
        {
            // check if string for notifikationkind should be added
            // to String for choosen notifikationKinds
            if ((this.notificationKind & j) == j)
            {
                // add ";" as delimiter
                if (!notifikationKindString.isEmpty ())
                {
                    notifikationKindString += ";";
                } // if

                notifikationKindString +=
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NOTIFIKATION_SELECTION[i], env);
            } // if
        } // for

        this.showProperty (table, BOArguments.ARG_NOTIFICATIONKIND,
                      MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NOTIFICATION_KIND, env), Datatypes.DT_INTEGER,
                      notifikationKindString);

        //show if sms should be activated
        this.showProperty (table, BOArguments.ARG_SMSACTIV
                      , MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NOTIFICATION_SMSADDITIVE, env), Datatypes.DT_BOOL,
                      "" + this.sendSms);

       //show if weblink should be attached to email
       /*
       this.showProperty (table, BOArguments.ARG_ADDWEBLINK,
                     MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ADD_HYPERLINK, env), Datatypes.DT_BOOL,
                      "" + this.addWeblink);
       */

        this.showProperty (table, BOArguments.ARG_LAYOUT, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LAYOUT, env), Datatypes.DT_TEXT, this.layoutName);
        this.showProperty (table, BOArguments.ARG_LOCALE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LOCALE, env), Datatypes.DT_TEXT, this.localeName);
        
        // 20100301: Remove the hidden fields because otherwise blank lines are displayed. Do not seem to be needed.
//        this.showProperty (table, BOArguments.ARG_LAYOUTID, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LAYOUT, env), Datatypes.DT_HIDDEN, this.layoutId);
//        this.showProperty (table, BOArguments.ARG_LOCALEID, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LOCALE, env), Datatypes.DT_HIDDEN, this.localeId);

/*
        env.write ("available Layouts -->");
        env.write ("" + this.sess.layouts.elements.size ());
        env.write (IE302.TAG_NEWLINE + "aktives Layout --> ");
        env.write (this.sess.activeLayout.name);
*/
    } // showProperties


    /**************************************************************************
     * Represent the properties of a UserProfile_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        // display the base object's properties:
//        super.showProperties (table);
        this.showFormProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env),
            Datatypes.DT_HIDDEN, this.name);
        this.showFormProperty (table, BOArguments.ARG_DESCRIPTION,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DESCRIPTION, env), Datatypes.DT_HIDDEN, this.description);
        this.showFormProperty (table, BOArguments.ARG_VALIDUNTIL,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_VALIDUNTIL, env), Datatypes.DT_HIDDEN, this.validUntil);
//FF        this.showFormProperty (table, BOArguments.ARG_VALIDUNTIL, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_VALIDUNTIL, env), Datatypes.DT_HIDDEN, this.validUntil);

        // loop through all properties of this object and display them:
        // newTimeLimit must be greater than 1
        this.formFieldRestriction =
            new FormFieldRestriction (false, 0, 0, "0" , null);
        // newsTimeLimit
        if (this.newsTimeLimit < 0)
        {
            this.newsTimeLimit = 0;
        } // if
        this.showFormProperty (table, BOArguments.ARG_NEWSTIMELIMIT,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NEWSTIMELIMIT, env), Datatypes.DT_INTEGER, this.newsTimeLimit);
        // newsShowOnlyUnread
        this.showFormProperty (table, BOArguments.ARG_NEWSSHOWONLYUNREAD,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NEWSSHOWONLYUNREAD, env), Datatypes.DT_BOOL,
            "" + this.newsShowOnlyUnread);
        // separator line
//        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);
        // ftpProxy
//        this.showFormProperty (table, BOArguments.ARG_FTPPROXY, TOK_FTPPROXY,
//            Datatypes.DT_TEXT, this.ftpProxy);
        // ftpProxyBypass
//        this.showFormProperty (table, BOArguments.ARG_FTPPROXYBYPASS, TOK_FTPPROXYBYPASS,
//            Datatypes.DT_TEXT, this.ftpProxyBypass);
        // separator line
        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);


        // outbox filters
        GroupElement gel = new GroupElement ();
        InputElement input;

        // use no outbox filter
        gel = new GroupElement ();
        input = new InputElement (BOArguments.ARG_OUTBOXFILTER,
            InputElement.INP_RADIO, UserConstants.OUTBOX_NOFILTER);
        if (!this.outboxUseTimeLimit && !this.outboxUseTimeFrame)
        {
            input.checked = true;
        } // if

        gel.addElement (input);
        this.showFormProperty (table, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OUTBOXUSENOFILTER, env), gel);
        // outboxUseTimeLimit
        gel = new GroupElement ();
        input = new InputElement (BOArguments.ARG_OUTBOXFILTER,
            InputElement.INP_RADIO, UserConstants.OUTBOX_TIMELIMIT);
        if (this.outboxUseTimeLimit && !this.outboxUseTimeFrame)
        {
            input.checked = true;
        } // if

        gel.addElement (input);
        this.showFormProperty (table, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OUTBOXUSETIMELIMIT, env), gel);
        // outboxTimeLimit
        // outboxTimeLimit must be greater than 1 or emtpy
//        this.formFieldRestriction =
//            new FormFieldRestriction (true, 0, 0, "1" , null);
        if (this.outboxTimeLimit < 0)
        {
            this.outboxTimeLimit = 0;
        } // if
        this.showFormProperty (table, BOArguments.ARG_OUTBOXTIMELIMIT,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OUTBOXTIMELIMIT, env), Datatypes.DT_INTEGER,
            this.outboxTimeLimit);

        // outboxUseTimeFrame
        gel = new GroupElement ();
        input = new InputElement (BOArguments.ARG_OUTBOXFILTER,
            InputElement.INP_RADIO, UserConstants.OUTBOX_TIMEFRAME);
        if (!this.outboxUseTimeLimit && this.outboxUseTimeFrame)
        {
            input.checked = true;
        } // if
        gel.addElement (input);
        this.showFormProperty (table, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OUTBOXUSETIMEFRAME, env), gel);

        this.showFormProperty (table, BOArguments.ARG_OUTBOXTIMEFRAME,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OUTBOXTIMEFRAME, env), Datatypes.DT_DATERANGE,
            this.outboxTimeFrameFrom, this.outboxTimeFrameTo);

        // separator line
        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);
        // showExtendedAttributes
        this.showFormProperty (table, BOArguments.ARG_SHOWEXTENDEDATTRIBUTES,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SHOWEXTENDEDATTRIBUTES, env), Datatypes.DT_BOOL,
            "" + this.showExtendedAttributes);
        // showFilesInWindows
        this.showFormProperty (table, BOArguments.ARG_SHOWFILESINWINDOWS,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SHOWFILESINWINDOWS, env), Datatypes.DT_BOOL,
            "" + this.showFilesInWindows);
        // showExtendedRights
        this.showFormProperty (table, BOArguments.ARG_USERIGHTALIASES,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_USERIGHTALIASES, env), Datatypes.DT_BOOL,
            "" + !this.showExtendedRights);

        // saveProfile
        this.showFormProperty (table, BOArguments.ARG_SAVEPROFILE,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SAVEPROFILE, env), Datatypes.DT_BOOL, "" + this.saveProfile);


        // NotificationKind
        int length = BOTokens.ML_NOTIFIKATION_SELECTION.length;
        String[] notificationIds = new String [length];
        String[] selectedIds = new String [length];
        int j = 0;

        // map bit pattern this.notifikationKind to an String with
        // the names of the choosen notifikationkinds
        int notiId = 1;
        for (int i = 0; i < length; i++, notiId *= 2)
        {
            notificationIds[i] = Integer.toString (notiId);

            // check if current notificationkind is used for user
            if ((this.notificationKind & notiId) == notiId)
            {
                // set id of notification in array for preselected
                // notificationkinds
                selectedIds[j++] = Integer.toString (notiId);
            } // if

        } // for

        // Get the notification texts with the correct language of the current user
        String[] notificationTexts = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NOTIFIKATION_SELECTION, env);
        
        this.showFormProperty (table, BOArguments.ARG_NOTIFICATIONKIND,
                          MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NOTIFICATION_KIND, env),
                          Datatypes.DT_MULTISELECT,
                          selectedIds,
                          notificationIds,
                          notificationTexts, 0);

        //show if sms should be activated
        this.showFormProperty (table, BOArguments.ARG_SMSACTIV,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NOTIFICATION_SMSADDITIVE, env), Datatypes.DT_BOOL,
            "" + this.sendSms);

       // show if weblink should be attached to email
       /*
       this.showFormProperty (table, BOArguments.ARG_ADDWEBLINK,
                     MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ADD_HYPERLINK, env), Datatypes.DT_BOOL,
                      "" + this.addWeblink);
        */
            // show layouts selection box
        int active = 0;
        int layouts = this.sess.layouts.elements.size ();
        if (layouts <= 1)
        {
            this.showProperty (table, BOArguments.ARG_LAYOUT, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LAYOUT, env), Datatypes.DT_TEXT, this.layoutName);
            this.showProperty (table, BOArguments.ARG_LAYOUTID, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LAYOUT, env), Datatypes.DT_HIDDEN, this.layoutId);
        } // (layouts <= 1)
        else // more than one layout
        {
            String[] temp1 = new String[layouts];
            String[] temp2 = new String[layouts];
            for (int i = 0; i < layouts; i++)
            {
                temp1[i] = null;
                temp2[i] = null;
                LayoutContainerElement_01 temp = (LayoutContainerElement_01)
                    this.sess.layouts.elements.elementAt (i);

                if (temp != null)
                {
                    temp1[i] = temp.name;
                    temp2[i] = temp.oid.toString ();
                    if (temp.oid.equals (this.layoutId))
                    {
                        active = i;
                    } // if
                } // (temp != null)
            } // for (int i = 0; i < layouts; i++)
            this.showFormProperty (table, BOArguments.ARG_LAYOUTID,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LAYOUT, env), Datatypes.DT_SELECT, this.layoutId
                    .toString (), temp2, temp1, active);
        } // else more than one layout
        
        // show locales selection box
        int activeLocale = 0;
        int locales = MultilingualTextProvider.getLocales (this.env).size ();
        if (locales <= 1)
        {
            this.showProperty (table, BOArguments.ARG_LOCALE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LOCALE, env), Datatypes.DT_TEXT, this.localeName);
            this.showProperty (table, BOArguments.ARG_LOCALEID, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LOCALE, env), Datatypes.DT_HIDDEN, this.localeId);
        } // if
        else // more than one locale
        {
            String[] temp1 = new String[locales];
            String[] temp2 = new String[locales];
            for (int i = 0; i < locales; i++)
            {
                temp1[i] = null;
                temp2[i] = null;
                Locale_01 temp = MultilingualTextProvider.getLocales (this.env).get (i);

                if (temp != null)
                {
                    temp1[i] = temp.name;
                    temp2[i] = temp.oid.toString ();
                    if (temp.oid.equals (this.localeId))
                    {
                        activeLocale = i;
                    } // if
                } // if
            } // for
            this.showFormProperty (table, BOArguments.ARG_LOCALEID,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LOCALE, env), Datatypes.DT_SELECT, this.localeId
                    .toString (), temp2, temp1, active);
        } // else
    } // showFormProperties


    /**************************************************************************
     * Sets the last login to the actual date and stores the data in the
     * database. <BR/>
     *
     * @exception   NoAccessException
     *              There was no access to change the user data.
     */
    public void updateLastLogin () throws NoAccessException
    {
        this.lastLogin = DateTimeHelpers.getCurAbsDate ();

        try
        {
            // remember the old value of the multilang client info reloading flag
            // and disable reloading multilang info
            boolean reloadMultilangClientInfoBefore = this.reloadMultilangClientInfo;
            this.reloadMultilangClientInfo = false;
            
            this.performChangeData (Operations.OP_CHANGE); // get the data of the workspace

            // reset the reloading flag
            this.reloadMultilangClientInfo = reloadMultilangClientInfoBefore;
        } // try
        catch (NameAlreadyGivenException e)
        {
            // send message to the user:
            this.showNameAlreadyGivenMessage ();
        } // catch
    } // updateLastLogin


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
        int [] buttons;
        int [] temp =
        {
            Buttons.BTN_EDIT,
            Buttons.BTN_CLEAN,
            Buttons.BTN_SEARCH,
            Buttons.BTN_CHANGEPASSWORD,
            Buttons.BTN_LOGIN,
            Buttons.BTN_ACTIVATE,
        }; // buttons
        buttons = temp;

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
        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_CLEAN,
            Buttons.BTN_SEARCH,
            Buttons.BTN_LOGIN,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons
    
    
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
        super.performChangeData (operation);
        
        // reset the locale cache
        this.locale = null;
        
        // check if relaoding of multilang client info is enabled
        if (this.reloadMultilangClientInfo)
        {
            // reload the multilang info on client - locale might have been changed
            MultilingualTextProvider.reloadMultilangClientInfo (this.env);
        } // if
    } // performChangeData
    
    
    /**************************************************************************
     * Provided the configured locale for the user profile.
     *
     * @return  The locale assigned to the user profile.
     */
    public Locale_01 getLocale ()
    {
        // check if the locale is already cached
        if (this.locale == null && this.localeId != null && !this.localeId.isEmpty ())
        {
            // retrieve the locale object for the stored localeId
            this.locale = (Locale_01) BOHelpers.getObject (
                this.localeId, this.env, false, false, false);
        } // if
        
        return this.locale;
    } // getLocale ()

} // class UserProfile_01

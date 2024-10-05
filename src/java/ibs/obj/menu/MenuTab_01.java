/*
 * Class: MenuTab_01.java
 */

// package:
package ibs.obj.menu;

// imports:
//KR TODO: unsauber
import ibs.app.AppConstants;
//KR TODO: unsauber
import ibs.app.AppFunctions;
import ibs.bo.BOArguments;
import ibs.bo.BOHelpers;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.type.TypeConstants;
import ibs.di.DataElement;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.menu.MenuTabArguments;
import ibs.obj.menu.MenuTabTokens;
import ibs.tech.html.TableElement;
import ibs.tech.http.HttpArguments;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.util.file.FileHelpers;


/******************************************************************************
 * This class represents a Menutab object. <BR/>
 *
 * @version     $Id: MenuTab_01.java,v 1.19 2013/01/16 16:14:12 btatzmann Exp $
 *
 * @author Monika Eisenkolb (ME)
 ******************************************************************************
 */
public class MenuTab_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MenuTab_01.java,v 1.19 2013/01/16 16:14:12 btatzmann Exp $";


    /**
     * File type HTML file. <BR/>
     */
    private static final int FT_HTML = 0;

    /**
     * File type front file. <BR/>
     */
    private static final int FT_FRONT = 1;

    /**
     * File type back file. <BR/>
     */
    private static final int FT_BACK = 2;

    /**
     * filename for the front view. <BR/>
     */
    protected String front = "";


    /**
     * filename for the back view. <BR/>
     */
    protected String back = "";


    /**
     * filename for the html page. <BR/>
     */
    protected String fileName = "";


    /**
     * oid of the object that is represented by the menutab. <BR/>
     */
    protected OID objectOid = null;


    /**
     * name of the object that is represented by the menutab. <BR/>
     */
    protected String objectName = "";


    /**
     * position of the menutab. <BR/>
     */
    protected int tabSort = 1;


    /**
     * temp path and filename of the html file. <BR/>
     */
    private String htmlFile = "";


    /**
     * temp path and filename of the front view image. <BR/>
     */
    private String frontViewFile = "";


    /**
     * temp path and filename of the back view image. <BR/>
     */
    private String backViewFile = "";

    /**
     * Number of levels to retrieve from the menu bar within one step. <BR/>
     * Default: <CODE>0</CODE> (means: "get all levels at once")
     */
    private int p_levelStep = 0;

    /**
     * Maximum level upto which to work with levelStep. Starting from that level
     * we get all elements at once. <BR/>
     * Default: <CODE>0</CODE> (means: "get all levels at once")
     */
    private int p_levelStepMax = 0;


    /**************************************************************************
     * Creates a MenuTab_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     */
    public MenuTab_01 ()
    {
        // call constructor of super class:
        super ();
    } // MenuTab_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the class-procedureNames:
        this.procCreate   = "p_MenuTab_01$create";
        this.procChange   = "p_MenuTab_01$change";
        this.procRetrieve = "p_MenuTab_01$retrieve";
        this.procDelete   = "p_MenuTab_01$delete";
        this.procDeleteRec = "p_MenuTab_01$delete";

        //show changeForm as frameset for object selection:
        this.showChangeFormAsFrameset = true;
        this.frm1Size = "*";
        this.frm2Size = "0";

        //set extended search flag:
        this.searchExtended = true;

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 8;
        this.specificChangeParameters = 7;
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
        // assigned objectoid
        BOHelpers.addInParameter(sp, this.objectOid);

        // filename
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                               this.fileName);
        // position
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                        this.tabSort);
        // front view
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.front);
        // back view
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.back);
        // levelStep:
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                        this.p_levelStep);
        // levelStepMax:
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                        this.p_levelStepMax);

        // copy the three files belonging to this menutab from the temp directory
        // to the right application directory
        this.copyFileToAppDir (this.htmlFile, MenuTab_01.FT_HTML);
        this.copyFileToAppDir (this.frontViewFile, MenuTab_01.FT_FRONT);
        this.copyFileToAppDir (this.backViewFile, MenuTab_01.FT_BACK);
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
        // assigned objectoid
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);

        // get the assigned objectname for the link
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        // position
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

        // front view
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        //  back view
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        //  filename
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        // levelStep:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

        // levelStepMax:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


    /**************************************************************************
     * Get the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * type specific data from the retrieve data stored procedure. <BR/>
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

        this.objectOid = SQLHelpers.getSpOidParam (params[++i]);
        this.objectName = params[++i].getValueString ();
        this.tabSort = params[++i].getValueInteger ();
        this.front = params[++i].getValueString ();
        this.back = params[++i].getValueString ();
        this.fileName = params[++i].getValueString ();
        this.p_levelStep = params[++i].getValueInteger ();
        this.p_levelStepMax = params[++i].getValueInteger ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     */
    public void getParameters ()
    {
        OID oid = null;
        String str = null;
        int num = 0;

        // call the super method:
        super.getParameters ();

        // objectOid:
        if ((oid = this.env.getOidParam (MenuTabArguments.ARG_LINKED_TO +
            MenuTabArguments.ARG_OID_EXTENSION)) != null)
        {
            this.objectOid = oid;
        } // if

        // fileName:
        if ((str = this.getFileParamBO (MenuTabArguments.ARG_MENU_FILENAME)) != null)
        {
            this.fileName = str;
            this.setFileFlag(true, true);
        } // if

        // set the htmlFile:
        if ((str = this.env.getFilePath (MenuTabArguments.ARG_MENU_FILENAME)) != null)
        {
            this.htmlFile = str;
            this.setFileFlag(true, true);
        } // if

        // position:
        if ((num = this.env.getIntParam (MenuTabArguments.ARG_MENU_TABSORT)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.tabSort = num;
        } // if

        // front view:
        if ((str = this.getFileParamBO (MenuTabArguments.ARG_MENU_FRONT)) != null)
        {
            this.front = str;
        } // if

        // set the frontViewFile
        if ((str = this.env.getFilePath (MenuTabArguments.ARG_MENU_FRONT)) != null)
        {
            this.frontViewFile = str;
        } // if

        // back view:
        if ((str = this.getFileParamBO (MenuTabArguments.ARG_MENU_BACK)) != null)
        {
            this.back = str;
        } // if

        // set the backViewFile
        if ((str = this.env.getFilePath (MenuTabArguments.ARG_MENU_BACK)) != null)
        {
            this.backViewFile = str;
        } // if

        // levelStep:
        if ((num = this.env.getIntParam (MenuTabArguments.ARG_MENU_LEVELSTEP)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.p_levelStep = num;
        } // if

        // levelStepMax:
        if ((num = this.env.getIntParam (MenuTabArguments.ARG_MENU_LEVELSTEPMAX)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.p_levelStepMax = num;
        } // if
    } // getParameters


    /**************************************************************************
     * Represent the properties of a MenuTab_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showProperties (table);

        // assigned object
        this.showProperty (table, MenuTabArguments.ARG_LINKED_TO,
            MultilingualTextProvider.getText (MenuTabTokens.TOK_BUNDLE,
                MenuTabTokens.ML_ASSIGNED_TO_OBJECT, env),
            Datatypes.DT_LINK, this.objectName, this.objectOid);

        // html file
        this.showProperty (table, MenuTabArguments.ARG_MENU_FILENAME,
            MultilingualTextProvider.getText (MenuTabTokens.TOK_BUNDLE,
                MenuTabTokens.ML_MENU_FILENAME, env),
            Datatypes.DT_TEXT, this.fileName);

        // position
        this.showProperty (table, MenuTabArguments.ARG_MENU_TABSORT,
            MultilingualTextProvider.getText (MenuTabTokens.TOK_BUNDLE,
                MenuTabTokens.ML_MENU_TABSORT, env),
            Datatypes.DT_INTEGER, this.tabSort);

        // front view
        this.showProperty (table, MenuTabArguments.ARG_MENU_FRONT,
            MultilingualTextProvider.getText (MenuTabTokens.TOK_BUNDLE,
                MenuTabTokens.ML_MENU_FRONT, env),
            Datatypes.DT_TEXT, this.front);

        // back view
        this.showProperty (table, MenuTabArguments.ARG_MENU_BACK,
            MultilingualTextProvider.getText (MenuTabTokens.TOK_BUNDLE,
                MenuTabTokens.ML_MENU_BACK, env),
            Datatypes.DT_TEXT, this.back);

        // levelStep:
        this.showProperty (table, MenuTabArguments.ARG_MENU_LEVELSTEP,
            MultilingualTextProvider.getText (MenuTabTokens.TOK_BUNDLE,
                MenuTabTokens.ML_MENU_LEVELSTEP, env),
            Datatypes.DT_INTEGER, this.p_levelStep);
        // provide the user with an explanation:
        this.showProperty (table, BOArguments.ARG_NOARG, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOTokens.ML_EXPLANATION, env),
            Datatypes.DT_HTMLTEXT,
            MultilingualTextProvider.getMessage (MenuMessages.MSG_BUNDLE, 
                MenuMessages.ML_MSG_LEVELSTEP_DESCRIPTION, env));

        // levelStepMax
        this.showProperty (table, MenuTabArguments.ARG_MENU_LEVELSTEPMAX,
            MultilingualTextProvider.getText (MenuTabTokens.TOK_BUNDLE,
                MenuTabTokens.ML_MENU_LEVELSTEPMAX, env),
            Datatypes.DT_INTEGER, this.p_levelStepMax);
        this.showProperty (table, BOArguments.ARG_NOARG, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOTokens.ML_EXPLANATION, env),
            Datatypes.DT_HTMLTEXT,
            MultilingualTextProvider.getMessage (MenuMessages.MSG_BUNDLE, 
                MenuMessages.ML_MSG_LEVELSTEPMAX_DESCRIPTION, env));
    } // showProperties


    /**************************************************************************
     * Represent the properties of a MenuTab_01 object to the user within a form.
     * <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)

    {
        // loop through all properties of this object and display them:
        super.showFormProperties (table);

        // separator
        this.showProperty (table, null, null, ibs.bo.Datatypes.DT_SEPARATOR, (String) null);

        // searchtext - field for assigned object returns the oid
        this.showFormProperty (table, MenuTabArguments.ARG_LINKED_TO, 
            MultilingualTextProvider.getText (MenuTabTokens.TOK_BUNDLE,
                MenuTabTokens.ML_ASSIGNED_TO_OBJECT, env),
            Datatypes.DT_SEARCHTEXTFUNCTION, this.objectName, "" + this.objectOid,
            // construct the url:
            this.getBaseUrl () +
            HttpArguments.createArg (BOArguments.ARG_FUNCTION,
                AppFunctions.FCT_SHOWOBJECTCONTENT) +
            HttpArguments.createArg (BOArguments.ARG_OID,
                // new OID ((getCache ().getTVersionId (TypeConstants.TC_SelectUserContainer)), 0).toString ()) +
                new OID (this.getTypeCache ().getTVersionId (TypeConstants.TC_QuerySelectContainer), 0).toString ()) +
            HttpArguments.createArg (BOArguments.ARG_CALLINGOID, "" + this.oid) +
            HttpArguments.createArg (BOArguments.ARG_SHOWLINK,
                AppConstants.SHOWSEARCHEDOBJECTS), HtmlConstants.FRM_SHEET2);


        // html file
        this.showFormProperty (table, MenuTabArguments.ARG_MENU_FILENAME,
            MultilingualTextProvider.getText (MenuTabTokens.TOK_BUNDLE,
                MenuTabTokens.ML_MENU_FILENAME, env),
            Datatypes.DT_FILE, this.fileName, "" + this.containerId);

        // position
        this.showFormProperty (table, MenuTabArguments.ARG_MENU_TABSORT,
            MultilingualTextProvider.getText (MenuTabTokens.TOK_BUNDLE,
                MenuTabTokens.ML_MENU_TABSORT, env),
            Datatypes.DT_INTEGER, this.tabSort);

        // remark telling some important additional information
        this.showProperty (table, MenuTabArguments.ARG_REMARK, 
            MultilingualTextProvider.getText (MenuTabTokens.TOK_BUNDLE,
                MenuTabTokens.ML_REMARK, env),
            Datatypes.DT_TEXT, 
            MultilingualTextProvider.getText (MenuTabTokens.TOK_BUNDLE,
                MenuTabTokens.ML_ADDITIONALINFO, env));

        // front view
        this.showFormProperty (table, MenuTabArguments.ARG_MENU_FRONT,
            MultilingualTextProvider.getText (MenuTabTokens.TOK_BUNDLE,
                MenuTabTokens.ML_MENU_FRONT, env),
            Datatypes.DT_FILE, this.front, "" + this.oid);

        // back view
        this.showFormProperty (table, MenuTabArguments.ARG_MENU_BACK,
            MultilingualTextProvider.getText (MenuTabTokens.TOK_BUNDLE,
                MenuTabTokens.ML_MENU_BACK, env),
            Datatypes.DT_FILE, this.back, "" + this.oid);

        // levelStep:
        this.showFormProperty (table, MenuTabArguments.ARG_MENU_LEVELSTEP,
            MultilingualTextProvider.getText (MenuTabTokens.TOK_BUNDLE,
                MenuTabTokens.ML_MENU_LEVELSTEP, env),
            Datatypes.DT_INTEGER, this.p_levelStep);
        // provide the user with an explanation:
        this.showProperty (table, BOArguments.ARG_NOARG, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE,
                BOTokens.ML_EXPLANATION, env),
            Datatypes.DT_HTMLTEXT,
            MultilingualTextProvider.getMessage (MenuMessages.MSG_BUNDLE, 
                MenuMessages.ML_MSG_LEVELSTEP_DESCRIPTION, env));

        // levelStepMax
        this.showFormProperty (table, MenuTabArguments.ARG_MENU_LEVELSTEPMAX,
            MultilingualTextProvider.getText (MenuTabTokens.TOK_BUNDLE,
                MenuTabTokens.ML_MENU_LEVELSTEPMAX, env),
            Datatypes.DT_INTEGER, this.p_levelStepMax);
        this.showProperty (table, BOArguments.ARG_NOARG, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOTokens.ML_EXPLANATION, env),
            Datatypes.DT_HTMLTEXT,
            MultilingualTextProvider.getMessage (MenuMessages.MSG_BUNDLE, 
                MenuMessages.ML_MSG_LEVELSTEPMAX_DESCRIPTION, env));
    } // showFormProperties


    /**************************************************************************
     * copies the file to the right application directory. <BR/>
     *
     * @param   fromFile       temp path and filename
     * @param   fileType       0 for html page and 1 for front and 2 for back view image
     *
     * @return   true if copying the file was successful
     */
    protected boolean copyFileToAppDir (String fromFile, int fileType)
    {
        // get the new path:
        String toFile = this.getFilePath (fileType);

        // copy the file:
        if ((fromFile != null) && (toFile != null))
        {
            return FileHelpers.copyFile (fromFile, toFile);
        } // if

        // no success:
        return false;
    } // copyFileToAppDir


    /**************************************************************************
     * Get the path and name of a file. <BR/>
     *
     * @param   fileType       0 for html page and 1 for front and 2 for back view image
     *
     * @return  The path and name for the file.
     *          <CODE>null</CODE> if the fileType is not valid.
     */
    protected String getFilePath (int fileType)
    {
        String filePath = null;

        // get the new path:
        switch (fileType)
        {
            case MenuTab_01.FT_HTML:
                filePath = this.getPath (fileType) + this.fileName;
                break;

            case MenuTab_01.FT_FRONT:
                filePath = this.getPath (fileType) + this.front;
                break;

            case MenuTab_01.FT_BACK:
                filePath = this.getPath (fileType) + this.back;
                break;

            default:
                // nothing to do
        } // switch

        // return the result:
        return filePath;
    } // getFilePath


    /**************************************************************************
     * Get the path of a file. <BR/>
     *
     * @param   fileType       0 for html page and 1 for front and 2 for back view image
     *
     * @return  The path for the file.
     *          <CODE>null</CODE> if the fileType is not valid.
     */
    protected String getPath (int fileType)
    {
        String path = null;

        // get the new path
        switch (fileType)
        {
            case MenuTab_01.FT_HTML:
                path = FileHelpers.makeFileNameValid (
                       this.app.p_system.p_m2AbsBasePath +
                       BOPathConstants.PATH_APPINCLUDE);
                break;

            case MenuTab_01.FT_FRONT:
                path = FileHelpers.makeFileNameValid (
                       this.app.p_system.p_m2AbsBasePath +
                       BOPathConstants.PATH_APPLAYOUTS +
                       this.getUserInfo ().userProfile.layoutName +
                       "\\images\\tabs\\");
                break;

            case MenuTab_01.FT_BACK:
                path = FileHelpers.makeFileNameValid (
                       this.app.p_system.p_m2AbsBasePath +
                       BOPathConstants.PATH_APPLAYOUTS +
                       this.getUserInfo ().userProfile.layoutName +
                       "\\images\\tabs\\");
                break;

            default:
                // nothing to do
        } // switch

        // return the result:
        return path;
    } // getPath


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
            Buttons.BTN_NEW,
            //Buttons.BTN_PASTE,
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_REFERENCE,
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
            //Buttons.BTN_CUT,
            Buttons.BTN_COPY,
            //Buttons.BTN_DISTRIBUTE,
            Buttons.BTN_STARTWORKFLOW,
            Buttons.BTN_FORWARD,
            Buttons.BTN_FINISHWORKFLOW,
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
//            Buttons.BTN_EXPORT,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons


    ////////////////////////////////////////////////////////////////////////////
    // import / export methods
    ////////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Reads the object data from an dataElement. <BR/>
     *
     * @param dataElement     the dataElement to read the data from
     *
     * @see ibs.bo.BusinessObject#readImportData
     */
    public void readImportData (DataElement dataElement)
    {
        String fileNameStr;
//        String filePathStr;
        String pathStr;

        // get business object specific values:
        super.readImportData (dataElement);

        // get the type specific values:
        // assigned object:
        // file to be displayed:
        // position within menu tab list:
        // css style for front view:
        // css style for back view:

        // assigned object:
        if (dataElement.exists ("assignedObjectOid"))
        {
            String oidString =
                dataElement.getImportStringValue ("assignedObjectOid");
            try
            {
                this.objectOid = new OID (oidString);
            } // try
            catch (IncorrectOidException e)
            {
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
        } // if (dataElement.exists ("assignedObjectOid"))
        // the field 'assignedObjectPath' holds the m2 path of the template object.
        else if (dataElement.exists ("assignedObjectPath"))
        {
            // get the path of the object:
            String objectPath =
                dataElement.getImportStringValue ("assignedObjectPath");
            if (objectPath != null)
            {
                // get the OID of the object out of the path:
                boolean[] isContainer = new boolean[1];
                OID oid = BOHelpers.resolveObjectPath (objectPath, isContainer,
                    this, this.app, this.sess, this.env);

                // check if the OID is valid:
                if (oid != null)
                {
                    this.objectOid = oid;
                } // if oid has correct type
                else
                {
                    IOHelpers.showMessage (
                        "MenuTab_01.readImportData: invalid objectOid " + oid,
                        this.app, this.sess, this.env);
                } // else
            } // if valid template path
        } // if (dataElement.exists ("assignedObjectPath"))

        // file to be displayed:
        if (dataElement.exists ("filename"))
        {
            // set the name of the file:
            fileNameStr = FileHelpers.makeFileNameValid (
                dataElement.getImportStringValue ("filename"));
//            filePathStr = FileHelpers.makeFileNameValid (
//                dataElement.sourcePath + File.separator + fileNameStr);
            // set path the file should be copied to
            // BB: is it true that the path will always be
            // constructed with the containerId ???
            pathStr = this.getPath (MenuTab_01.FT_HTML);
            // set the file we want to read
            dataElement.addFile ("fileName", pathStr, fileNameStr,
                FileHelpers.getFileSize (pathStr, fileNameStr));
            this.fileName = fileNameStr;
        } // if filename exists

        // position within menu tab list:
        if (dataElement.exists ("tabSort"))
        {
            this.tabSort =
                dataElement.getImportIntValue ("tabSort");
        } // if (dataElement.exists ("tabSort"))

        // css style for front view:
//      dataElement.setExportFileValue ("frontViewFile", getPath (FT_FRONT), this.front);
        if (dataElement.exists ("frontView"))
        {
            this.front =
                dataElement.getImportStringValue ("frontView");
        } // if (dataElement.exists ("frontView"))

        // css style for back view:
//        dataElement.setExportFileValue ("backViewFile", getPath (FT_BACK), this.back);
        if (dataElement.exists ("backView"))
        {
            this.back =
                dataElement.getImportStringValue ("backView");
        } // if (dataElement.exists ("backView"))
    } // readImportData


    /**************************************************************************
     * Check if the files set in the dataElement could have been
     * created and set the respective object properties. <BR/>
     *
     * @param dataElement     the dataElement to read the data from
     *
     * @see ibs.bo.BusinessObject#readImportFiles
     */
    public void readImportFiles (DataElement dataElement)
    {
        // set the name, path and the size of the file
        this.fileName = dataElement.getFileName ("fileName");
//showDebug ("this.fileName: " + this.fileName);
    } // readImportFiles


    /**************************************************************************
     * Writes the object data to an dataElement. <BR/>
     *
     * @param dataElement     the dataElement to write the data to
     *
     * @see ibs.bo.BusinessObject#writeExportData
     */
    public void writeExportData (DataElement dataElement)
    {
        super.writeExportData (dataElement);

        // set the business object specific values:
        // assigned object:
        dataElement.setExportValue ("assignedObjectOid", this.objectOid.toString ());
        dataElement.setExportValue ("assignedObjectPath", this.getObjectPathString (this.objectOid));
        // file to be displayed:
        dataElement.setExportFileValue ("filename",
            this.getPath (MenuTab_01.FT_HTML), this.fileName);
        // position within menu tab list:
        dataElement.setExportValue ("tabSort", this.tabSort);
        // css style for front view:
        dataElement.setExportValue ("frontView", this.front);
//        dataElement.setExportFileValue ("frontViewFile", getPath (FT_FRONT), this.front);
        // css style for back view:
        dataElement.setExportValue ("backView", this.back);
//        dataElement.setExportFileValue ("backViewFile", getPath (FT_BACK), this.back);
    } // writeExportData

} // class MenuTab_01

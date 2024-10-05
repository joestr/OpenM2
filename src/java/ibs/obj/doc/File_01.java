/*
 * Class: File_01.java
 */

// package:
package ibs.obj.doc;

// imports:
//KR TODO: unsauber
import ibs.app.AppConstants;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.di.DIMessages;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.conf.Configuration;
import ibs.service.user.User;
import ibs.tech.html.HtmlHelpers;
import ibs.tech.html.TableElement;
import ibs.util.FormFieldRestriction;
import ibs.util.Helpers;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.UtilExceptions;
import ibs.util.file.FileHelpers;

import java.io.File;


/******************************************************************************
 * This class represents one object of type Attachment with version 01. <BR/>
 * Attachments represent relationships between objects and files or webpages.
 * An object can have multiple attachments. In case multiple attachments have been
 * assigned one must be set as master which means that these master assignments
 * will be displayed when the content of an objects is viewed by the user.
 * Within a set of attachments there can only be one master attachment. <BR/>
 * This version of attachment does not support compound documents. (files that
 * includes other files like HTML files) <BR/>
 *
 * @version     $Id: File_01.java,v 1.37 2012/07/16 07:43:04 gweiss Exp $
 *
 * @author      Stampfer Heinz Josef (HJ) 981006
 ******************************************************************************
 */
public class File_01 extends Attachment_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: File_01.java,v 1.37 2012/07/16 07:43:04 gweiss Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class File_01.
     * <BR/>
     */
    public File_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // File_01


    /**************************************************************************
     * Creates a File_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    @Deprecated
    public File_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // File_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        super.initClassSpecifics ();    // has same specifics as super class

        this.attachmentType = DocConstants.ATT_FILE;
        this.isMaster = false;
    } // initClassSpecifics


    /**************************************************************************
     * Read form the User the data used in the Object. <BR/>
     */
    public void getParameters ()
    {
        super.getParameters ();

        // BB HINT: we have to check here if a container id has been set
        // it can be null in the case the user created a file object
        // or one of its subtypes and activated the option
        // store and new. we dont have to get the parameters from the
        // environment anyway because the object will be a new one
        if (this.containerId != null)
        {
            // get the temporary file name
            String tempFilePath = this.env.getFilePath (BOArguments.ARG_FILE);

            // if the temporary file is valid it will be moved in the
            // upload directory
            if (tempFilePath != null && this.checkIsValidFile (tempFilePath))
            {
                String str = null;
                // created
                if ((str = this.env.getStringParam (BOArguments.ARG_CREATED)) != null)
                {
                    this.creationDateString = str;
                } // if

                // it's a file
                // getFileParamBO is a method which includes a browser check
    //            if (this.attachmentType == ATT_FILE) this.fileName = str;
                if ((str = this.getFileParamBO (BOArguments.ARG_FILE)) != null)
                {
                    String filePath = "";
                    String tempName = str;
                    if (str.indexOf (this.oid.toString ()) < 0)
                    {
                        this.fileName = this.oid.toString () + str;
                    } // if

                    // path = filename + extension
                    if ((str = this.env.getStringParam (BOArguments.ARG_FILE + AppConstants.DT_FILE_PATH_EXT)) != null)
                    {
                        filePath = str;
                    } // if

                    String physPath =
                        BOHelpers.getFilePath (this.containerId);

                    // if the path is not null (this means we are NOT in
                    // the search dialogue) AND
                    // if the name of the file does not contain the oid
                    // then rename it the way it physically to have the oid in it
                    if ((str != null) &&
                        tempName.indexOf (this.oid.toString ()) < 0)
                    {
                        String actFile = this.env.getFilePath (BOArguments.ARG_FILE);
                        if (FileHelpers.exists (actFile))
                        {
                            // do we have to delete the former file?
                            if (FileHelpers.exists (physPath + this.fileName))
                            {
                                // try to delete it
                                if (FileHelpers.deleteFile (physPath + this.fileName))
                                {
                                    // no perform the renaming
                                    if (!FileHelpers.renameFile (actFile, physPath + this.fileName))
                                    {
                                        IOHelpers.showMessage (MultilingualTextProvider.getMessage (
                                            UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_NOFILEMOVE, env) +
                                            actFile + "-->" + physPath + this.fileName,
                                            this.app, this.sess, this.env);
                                    } // if (!FileHelpers.renameFile (actFile, physPath + this.fileName))
                                } // if
                                else    // could not delete former file
                                {
                                    IOHelpers.showMessage ( 
                                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                            DIMessages.ML_MSG_COULD_NOT_DELETE_FILE,
                                            new String[] {physPath + this.fileName}, env),
                                        this.app, this.sess, this.env);
                                } // else
                            } // if (FileHelpers.exists (physPath + this.fileName))
                            else
                            {
                                if (!FileHelpers.renameFile (actFile, physPath + this.fileName))
                                {
                                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (
                                        UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_NOFILEMOVE, this.env) +
                                        actFile + "-->" + physPath + this.fileName,
                                        this.app, this.sess, this.env);
                                } // if (!FileHelpers.renameFile (actFile, physPath + this.fileName))
                            } // else
                        } // if
                    } // if

                    // get size of the file in Bytes
                    String fn = filePath + this.fileName;
                    File f = new File (fn);
                    if (f.exists ())            // the file exists?
                    {
                        long sizeoffile = f.length ();
                        this.filesize = sizeoffile;

                        // set the flag indicating that there exists a file attached to this object
                        this.setFileFlag(true, true);
                    } // if the file exists
                } // if ((str = getFileParamBO (BOArguments.ARG_FILE)) != null)

                // path = filename + extension
                if ((str = this.env.getStringParam (BOArguments.ARG_FILE + AppConstants.DT_WWW_PATH_EXT)) != null)
                {
                    this.path = str;
                } // if
            } // if (checkIsValidFile (tempFilePath))
            else if (tempFilePath != null)
            {
                // delete the temporary file
                FileHelpers.deleteFile (tempFilePath);
            } // else if (checkIsValidFile (tempFilePath))
        } // if (this.containerId != null)

        this.attachmentType = DocConstants.ATT_FILE;
        this.isMaster = false;
    } // getParameters


    /**************************************************************************
     * Get a parameter which is a file. <BR/>
     */
    protected void getFileParameter ()
    {
        // nothing to do
    } // getFileParameter


    /**************************************************************************
     * Returns if the Object is a file-type. <BR/>
     * For files this is <CODE>true</CODE>.
     *
     * @return  <CODE>true</CODE> if the object contains a file field,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean hasFile ()
    {
        return true;
    } // hasFile


    /**************************************************************************
     * Represent the properties of a File_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties should be added.
     */
    protected void showProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        this.showProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env), Datatypes.DT_NAME, this.name);
        this.showProperty (table, BOArguments.ARG_TYPE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TYPE, env), Datatypes.DT_TYPE, this.getMlTypeName ());
        this.showProperty (table, BOArguments.ARG_INNEWS, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env), Datatypes.DT_BOOL, "" + this.showInNews);
        this.showProperty (table, BOArguments.ARG_DESCRIPTION, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DESCRIPTION, env), Datatypes.DT_DESCRIPTION, this.description);
        this.showProperty (table, BOArguments.ARG_VALIDUNTIL, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_VALIDUNTIL, env), Datatypes.DT_DATE, this.validUntil);

        if (this.checkedOut)
        {
            if (this.checkOutUserName != null &&
                !this.checkOutUserName.isEmpty ())
            {
                this.showProperty (table, BOArguments.ARG_NOARG,
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CHECKEDOUT, env), Datatypes.DT_USERDATE,
                    this.checkOutUser, this.checkOutDate);
            } // if
            else
            {
                this.showProperty (table, BOArguments.ARG_NOARG,
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CHECKEDOUT, env), Datatypes.DT_DATETIME,
                    this.checkOutDate);
            } // else
        } // if
        // dummy

        if (this.getUserInfo ().userProfile.showExtendedAttributes)
        {
            this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);

            this.showProperty (table, BOArguments.ARG_OWNER, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OWNER, env),
                Datatypes.DT_USER, this.owner);
            this.showProperty (table, BOArguments.ARG_CREATED, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CREATED, env),
                Datatypes.DT_USERDATE, this.creator, this.creationDate);
            this.showProperty (table, BOArguments.ARG_CHANGED, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CHANGED, env),
                Datatypes.DT_USERDATE, this.changer, this.lastChanged);
        } // if (sess.userInfo.userProfile.showExtendedAttributes)
        this.showProperty (table, null, null, ibs.bo.Datatypes.DT_SEPARATOR,
            (String) null);

        if (this.checkedOut)
        {
            this.showProperty (table, BOArguments.ARG_FILE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FILE, env),
                          ibs.bo.Datatypes.DT_WEBDAVFOLDER,
                          "?uid=" + this.user.oid.toString () +
                          "&oid=" + this.oid.toString () +
                          "&file=" + HtmlHelpers.encodeRequestParameter (this.fileName),
                          ((Configuration) this.app.configuration).getWebDavURL (this.env));
        } // if
        else
        {
            this.showProperty (table, BOArguments.ARG_FILE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FILE, env),
                          Datatypes.DT_FILE, this.fileName, this.getBase () + this.path);
        } //else
        this.showProperty (table, BOArguments.ARG_FILESIZE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FILESIZE, env),
                      Datatypes.DT_NUMBER, Helpers.convertFileSize (this.filesize, env));
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
        this.formFieldRestriction =
            new FormFieldRestriction (false);
        this.showFormProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env),
            Datatypes.DT_NAME, this.name);
        this.showProperty (table, BOArguments.ARG_TYPE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TYPE, env),
            Datatypes.DT_TYPE, this.getMlTypeName ());
        this.showFormProperty (table, BOArguments.ARG_INNEWS, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env),
            Datatypes.DT_BOOL, "" + this.showInNews);
        this.formFieldRestriction = new FormFieldRestriction (true,
            BOConstants.MAX_LENGTH_DESCRIPTION, 0);
        this.showFormProperty (table, BOArguments.ARG_DESCRIPTION,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DESCRIPTION, env), Datatypes.DT_DESCRIPTION, this.description);
        this.formFieldRestriction = new FormFieldRestriction (false);
        this.showFormProperty (table, BOArguments.ARG_VALIDUNTIL,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_VALIDUNTIL, env), Datatypes.DT_DATE, this.validUntil);

        // incl. pathname = oid of container
        this.showFormProperty (table, BOArguments.ARG_FILE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FILE, env),
            Datatypes.DT_FILE, this.fileName, "" + this.containerId);
    } // showFormProperties


    /**************************************************************************
     * Returns physical path to uploaded file. <BR/>
     *
     * @return  The path.
     */
    public String getPhysicalPath ()
    {
        return BOHelpers.getFilePath (this.containerId);
    } // getPhysicalPath

    //
    // IMPORT / EXPORT METHODS
    //

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
            Buttons.BTN_WEBDAVCHECKOUT,
            Buttons.BTN_WEBDAVCHECKIN,
            Buttons.BTN_EDIT,
            Buttons.BTN_DELETE,
            Buttons.BTN_CUT,
            Buttons.BTN_COPY,

            Buttons.BTN_DISTRIBUTE,

            Buttons.BTN_FINISHWORKFLOW,
            Buttons.BTN_SEARCH,
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


    /***************************************************************************
     * Checks out a Businessoject (WebDAV). <BR/>
     *
     * @return  The BusinessObject which was checked out.
     *
     * @throws  NoAccessException
     *          The user does not have access to this object to perform the
     *          required operation.
     */
    public BusinessObject webdavCheckOut () throws NoAccessException
    {
        String webdavpath = new String (((Configuration) this.app.configuration).getWebDavPath ());
        String sourcepath = new String ();
        String targetpath = new String ();
        String targetpath2 = new String ();

        sourcepath = BOHelpers.getFilePath (this.containerId);
        targetpath = webdavpath + File.separator +
                     this.user.oid.toString () + File.separator;
        targetpath2 = targetpath +
                      this.oid.toString () + File.separator;


        // call the object type specific method:
        this.performCheckOutData (Operations.OP_CHANGE);

        // ensure that both paths exist:
        if (!FileHelpers.exists (targetpath))
        {
            FileHelpers.makeDir (targetpath);
        } // if

        if (!FileHelpers.exists (targetpath2))
        {
            FileHelpers.makeDir (targetpath2);
        } // if

        FileHelpers.copyFile (sourcepath + this.fileName,
                              targetpath2 + this.fileName);

        return this;
    } // webdavCheckOut


    /***************************************************************************
     * Checks out a Businessoject (WebDAV). <BR/>
     *
     * @return The actual business object is returned.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    public BusinessObject webdavCheckIn () throws NoAccessException
    {
        String webdavpath = new String (((Configuration) this.app.configuration).getWebDavPath ());
        String sourcepath = new String ();
        String targetpath = new String ();

        sourcepath = webdavpath + File.separator +
                     this.user.oid.toString () + File.separator +
                     this.oid.toString () + File.separator;
        targetpath = BOHelpers.getFilePath (this.containerId);

        if (this.user.id == this.checkOutUser.id) // user allowed?
        {
            // the user is allowed to check the object in
            // call the object type specific method:
            this.performCheckInData (Operations.OP_CHANGE);

            FileHelpers.copyFile (sourcepath + this.fileName,
                                  targetpath + this.fileName);
            FileHelpers.deleteFile (sourcepath + this.fileName);

            return this;
        } // if user allowed

         // raise no access exception:
        throw new NoAccessException (MultilingualTextProvider.getMessage (
            UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_NOACCESSEXCEPTION, env));
    } // webdavCheckIn

} // class File_01

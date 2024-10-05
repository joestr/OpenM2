/*
 * Class: Attachment_01.java
 */

// package:
package ibs.obj.doc;

// imports:
//KR TODO: unsauber
import ibs.app.AppConstants;
import ibs.app.AppFunctions;
import ibs.bo.BOArguments;
import ibs.bo.BOHelpers;
import ibs.bo.BOMessages;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.di.DIHelpers;
import ibs.di.DataElement;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.conf.ServerRecord;
import ibs.service.user.User;
import ibs.tech.html.BlankElement;
import ibs.tech.html.BuildException;
import ibs.tech.html.Page;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.TableElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.StoredProcedure;
import ibs.util.NoAccessException;
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
 * includes other files like HTML files).
 *
 * @version     $Id: Attachment_01.java,v 1.60 2013/01/16 16:14:13 btatzmann Exp $
 *
 * @author      Stampfer Heinz Josef (HJ), 980415
 ******************************************************************************
 */
public class Attachment_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Attachment_01.java,v 1.60 2013/01/16 16:14:13 btatzmann Exp $";


    /**
     * A hyperlink is a URL. <BR/>
     */
    public String url = "";

    /**
     * The path is important to find the File. <BR/>
     */
    public String path = BOPathConstants.PATH_UPLOAD;

    /**
     * A fileName of the referenced file. <BR/>
     */
    public String fileName = "";

    /**
     * The types of Attachment can be HyperlinkType or Filetype. <BR/>
     */
    private String[] typeNames = {BOTokens.ML_HYPERLINK, BOTokens.ML_FILE};

    /**
     * The types of Attachment can be HyperlinkType or Filetype. <BR/>
     */
    private String[] typeIds =
    {
        Short.toString (DocConstants.ATT_HYPERLINK),
        Short.toString (DocConstants.ATT_FILE),
    };

    /**
     * The filesize of a file in KBytes. <BR/>
     */
    public float filesize = 0;

    /**
     * This is  a String filled with a Date in it. <BR/>
     */
    public String creationDateString = " ";

    /**
     * Is used to convert filesize. <BR/>
     */
    public String validUntilDateString = " ";

    /**
     * An attachment has a reference to a file or to a hyperlink. <BR/>
     */
    public int attachmentType = DocConstants.ATT_HYPERLINK;

     /**
     * An attachment has a reference to a file. One File in a Conatiner is
     * marked as Masterattachment. "true" means MasterFile and "false" means
     * no Masterfile. <BR/>
     */
    protected boolean isMaster = false;

    /**
     * The File linked to an Attachment can be shown in a Frame or in a
     * window of a new Browser instance. <BR/>
     */
    protected boolean showInWindow = true;

    /**
     * The File linked to an Attachment is an internal weblink. <BR/>
     */
    protected boolean isWeblink = false;

    /**
     * fieldname: filename. <BR/>
     */
    public static final String FIELD_FILENAME = "filename";

    /**
     * fieldname: url. <BR/>
     */
    public static final String FIELD_URL = "url";


    /***************************************************************************
     * This constructor creates a new instance of the class Attachment_01. <BR/>
     */
    public Attachment_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
        this.isMaster = false;
        this.searchExtended = true;
    } // Attachment_01


    /**************************************************************************
     * Creates a Attachment_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @see     ibs.bo.BusinessObject
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    @Deprecated
    public Attachment_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:
        this.isMaster = false;
        this.searchExtended = true;
    } // Attachment_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.attachmentType = DocConstants.ATT_HYPERLINK;

        this.procCreate =     "p_Attachment_01$create";
        this.procChange =     "p_Attachment_01$change";
        this.procRetrieve =   "p_Attachment_01$retrieve";
        this.procDelete =     "p_Attachment_01$delete";
        this.procDeleteRec =  "p_Attachment_01$delete";

        // set db table name
        this.tableName = "ibs_Attachment_01";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 7;
        this.specificChangeParameters = 7;
    } // initClassSpecifics


    /**************************************************************************
     * Read form the User the data used in the Object. <BR/>
     */
    public void getParameters ()
    {
        String str = null;
        int num = 0;

        super.getParameters ();

        // created
        if ((str = this.env.getStringParam (BOArguments.ARG_CREATED)) != null)
        {
            this.creationDateString = str;
        } // if

        //AttachmentType
        if ((num = this.env.getIntParam (BOArguments.ARG_SELECT)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.attachmentType = num;
        } // if

        //Masterfile
        if ((num = this.env.getBoolParam (BOArguments.ARG_ISMASTER)) >= IOConstants.BOOLPARAM_FALSE)
        {
            this.isMaster = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        // differ between file and link
        // it's a link
//        if (this.attachmentType == ATT_HYPERLINK)
        if ((str = this.env.getStringParam (BOArguments.ARG_HYPERLINK)) != null)
        {
            this.url = str;
        } // if

        // it's a file
        // getFileParamBO is a method which includes a browser check

//       if (this.attachmentType == ATT_FILE) this.fileName = str;
        // get the file parameter:
        this.getFileParameter ();
    } // getParameters


    /**************************************************************************
     * Returns if the Object is a file-type. <BR/>
     * For attachments this is <CODE>true</CODE>.
     *
     * @return  <CODE>true</CODE> if the object contains a file field,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean hasFile ()
    {
        return true;
    } // hasFile


    /**************************************************************************
     * Get a parameter which is a file. <BR/>
     * Get the File only if this is directly this class -
     * subclasses have to get it for themselves.
     */
    protected void getFileParameter ()
    {
        String str = null;
        String tempFilePath;
        String paramName = BOArguments.ARG_FILE;

        // get the temporary file name:
        tempFilePath = this.env.getFilePath (paramName);

        // if the temporary file is valid it will be moved in the
        // upload directory
        if (this.checkIsValidFile (tempFilePath))
        {
            if ((str = this.getFileParamBO (paramName)) != null)
            {
                String filePath = "";
                this.fileName = str;

                // path = filename + extension
                if ((str = this.env.getStringParam (paramName +
                     AppConstants.DT_FILE_PATH_EXT)) != null)
                {
                    filePath = str;
                } // if

                // get size of the file in Bytes
                String fn = filePath + this.fileName;
                File f = new File (fn);
                if (f.exists ())            // the file exists?
                {
                    long sizeoffile = f.length ();
                    this.filesize = sizeoffile;
                } // if the file exists
                
             // set the flag indicating that there exists a file attached to this object
                this.setFileFlag(true, true);
            } // if

            // path = filename + extension
            if ((str = this.env.getStringParam (paramName +
                 AppConstants.DT_WWW_PATH_EXT)) != null)
            {
                this.path = str;
            } // if

        } // if (checkIsValidFile (tempFilePath))
        else
        {
            // delete the temporary file
            FileHelpers.deleteFile (tempFilePath);
        } // else if (checkIsValidFile (tempFilePath))
    } // getFileParameter


    /**************************************************************************
     * Get the absolute directory path of the attachment. <BR/>
     * This path is computed through the following rule:
     * <CODE>&lt;absBasePath&gt;/&lt;uploadPath&gt;/&lt;containerId&gt;/</CODE>.
     * <BR/>
     * The last separator "/" is the system dependent value as derived through
     * {@link java.io.File#separator File.separator}.
     *
     * @return  The absolute path or
     *          <CODE>null</CODE> if the containerId is not set.
     */
    public String getAbsPath ()
    {
        // has a path been set?
        if (this.path == null || this.path.isEmpty ())
        {
            // containerId set?
            if (this.containerId != null && !this.containerId.isEmpty ())
            {
                // compute the value and return it:
                return BOHelpers.getFilePath (this.containerId);
            } // if containerId set

            // necessary value not set
            // return error value:
            return null;
        } // if (this.path != null && (!this.path.equals ("")))

        // a path has been set
        return BOHelpers.getFilePath (this.getOidFromPath (this.path));
    } // getAbsPath


    /**************************************************************************
     * Get the path of the attachment that is also used to store in
     * the database. <BR/>
     * This path is computed through the following rule:
     * <CODE>&lt;apphome&gt;/&lt;uploadPath&gt;/&lt;containerId&gt;/</CODE>.
     * <BR/>
     *
     * @return  The attachment path or
     *          <CODE>null</CODE> if the containerId is not set.
     */
    public String getPath ()
    {
        // has a path been set?
        if (this.path == null || this.path.isEmpty ())
        {
            // containerId set?
            if (this.containerId != null && !this.containerId.isEmpty ())
            {
                // compute the value and return it:
                return  this.sess.home + BOPathConstants.PATH_UPLOAD_FILES +
                    this.containerId.toString () + "/";
            } // if containerId set

            // necessary value not set
            // return error value:
            return null;
        } // if (this.path != null && (!this.path.equals ("")))

        // a path has been set
        return this.path;
    } // getPath


    /**************************************************************************
     * Get the oid from a path. <BR/>
     * Note that a path has the following format
     * <CODE>/&lt;approot&gt;/&lt;uploadPath&gt;/&lt;oid&gt;/</CODE>.
     *
     * @param path  the path to get the oid from
     *
     * @return  The oid from a path or
     *          <CODE>null</CODE> if the path has not been set
     */
    public String getOidFromPath (String path)
    {
        // did we got a valid path?
        if (path != null && path.length () > 19)
        {
            // cut out the oid
            return path.substring (path.length () - 19, path.length () - 1);
        } // if (path != null && (!path.equals ("")))

        // path not valid
        // return the contianerId as default
        return this.containerId.toString ();
    } // getOidFromPath


    /**************************************************************************
     * Get the absolute file path of the file. <BR/>
     * This path is computed through the following rule:
     * <CODE>&lt;{@link #getAbsPath getAbsPath ()}&gt;/&lt;fileName&gt;</CODE>.
     *
     * @return  The absolute path of the file or
     *          <CODE>null</CODE> if the fileName is not set.
     */
    public String getAbsFilePath ()
    {
        String path = this.getAbsPath (); // the path of the file

        if (path != null &&
            this.fileName != null && !this.fileName.trim ().isEmpty ())
                                        // file name set?
        {
            // compute the value and return it:
            return path + this.fileName;
        } // if file name set

        // necessary value not set
        // return error value:
        return null;
    } // getAbsFilePath


    /***************************************************************************
     * Represent the properties of a Attachment_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties should be added.
     *
     * @see ibs.bo.BusinessObject#showProperties
     * @see ibs.IbsObject#showProperty(TableElement, String, String, int, User, java.util.Date)
     */
    protected void showProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showProperties (table); // name, type, description
        this.showProperty (table, BOArguments.ARG_ISMASTER, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MASTERFILE, env),
            Datatypes.DT_BOOL, "" + this.isMaster);

        if (this.attachmentType == DocConstants.ATT_FILE)
        {
            this.showProperty (table, BOArguments.ARG_FILE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FILE, env),
                          Datatypes.DT_FILE, this.fileName,
                          this.getBase () + this.path);
            String str = null;
            str = ibs.util.Helpers.convertFileSize (this.filesize, env);
            this.showProperty (table, BOArguments.ARG_FILESIZE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FILESIZE, env),
                          Datatypes.DT_NUMBER, str);
        } // if

        // is it a internal weblink?
        boolean isInternalWeblink = this.checkIsInternalWeblink (this.url);

        if (this.attachmentType == DocConstants.ATT_HYPERLINK)
        {
            this.showProperty (table, BOArguments.ARG_FILE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_HYPERLINK, env),
                          Datatypes.DT_URL, this.url, this.isWeblink,
                          isInternalWeblink);
        } // if
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
    protected void showFormProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showFormProperties (table);
        if (this.isMaster)              // you can only change if the object
                                        // is no master
        {
            this.showProperty (table, BOArguments.ARG_ISMASTER,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MASTERFILE, env), Datatypes.DT_BOOL,
                "" + this.isMaster);
            this.showFormProperty (table, BOArguments.ARG_ISMASTER,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MASTERFILE, env), Datatypes.DT_HIDDEN,
                "" + this.isMaster);
        } // if you can only change if the object is no master
        else                            // change master if you want
        {
            this.showFormProperty (table, BOArguments.ARG_ISMASTER,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MASTERFILE, env), Datatypes.DT_BOOL,
                "" + this.isMaster);
        } // else change master if you want
        this.showFormProperty (table, BOArguments.ARG_SELECT,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ATTACHMENTTYPE, env), Datatypes.DT_SELECT,
            Integer.toString (this.attachmentType), this.typeIds, this.typeNames,
            this.attachmentType);

        // incl. pathname = oid of container
        this.showFormProperty (table, BOArguments.ARG_FILE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FILE, env),
            Datatypes.DT_FILE, this.fileName, "" + this.containerId);
        this.showFormProperty (table, BOArguments.ARG_HYPERLINK,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_HYPERLINK, env), Datatypes.DT_URL, this.url);
    } // showFormProperties


    /**************************************************************************
     * Display the master document. <BR/>
     */
    public void performShowMaster ()
    {
        String masterUrl = "";
        String masterFile = "";

        // get data
        try
        {
            // operation to view the attachment is READ
            this.performRetrieveData (Operations.OP_READ);
        } // try
        catch (NoAccessException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        catch (ObjectNotFoundException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch

        // differ between document types
        if (this.attachmentType == DocConstants.ATT_HYPERLINK)
                                        // document is an url?
        {
            if (this.url.indexOf (":") == -1)
            {
                masterUrl = IOConstants.URL_HTTP + this.url;
            } // if
            else
            {
                masterUrl = this.url;
            } // else

            // if isWeblink or showFilesInWindows is true the hyperlink
            // performs in the frameset
            if ((!this.getUserInfo ().userProfile.showFilesInWindows) ||
                 this.isWeblink)
            {
                if (this.sess.weblink)
                {
                    masterUrl = "top.tryCall (\'top.loadLink (\\\'" + masterUrl + "\\\');\');";
                } // if
                else
                {
                    masterUrl = "top.loadLink (\'" + masterUrl + "\');";
                } // else
            } // if
            else
            {
                if (this.sess.weblink)
                {
                    masterUrl = "top.tryCall (\'top.loadWindowLink (\\\'" + masterUrl + "\\\');\');";
                } // if
                else
                {
                    masterUrl = "top.loadWindowLink (\'" + masterUrl + "\');";
                } // else
            } // else
            // masterUrl = this.url;
        } // if document is an url
        else if (this.attachmentType == DocConstants.ATT_FILE)
                                        // document is a file?
        {
            /* this is the old implementation without the FileAccessServlet
                        masterFile = this.path + this.fileName;
            */
            // the FileAccessServlet awaits a parameter of the format
            // <OID>/<filename>
            // note that the path has the format
            // /<appdir>/upload/files/<oid>/
            masterFile = this.path.substring (
                this.path.lastIndexOf ("/", this.path.length () - 2)) +
                this.fileName;

            if (!this.getUserInfo ().userProfile.showFilesInWindows)
            {
                if (this.sess.weblink)
                {
                    // create the java script code by enabling uri encoding
                    masterUrl = "top.tryCall (\'top.loadFile (\\\'" +
                        masterFile + "\\\', null, true);\');";
                } // if
                else
                {
                    // create the java script code by enabling uri encoding
                    masterUrl = "top.loadFile (\'" + masterFile + "\', null, true);";
                } // else
            } // if
            else
            {
                if (this.sess.weblink)
                {
                    // create the java script code by enabling uri encoding
                    masterUrl = "top.tryCall (\'top.loadWindowFile (\\\'" +
                        masterFile + "\\\',\\\'" + this.fileName + "\\\', null, null, null, true);\');";
                } // if
                else
                {
                    // create the java script code by enabling uri encoding
                    masterUrl = "top.loadWindowFile (\'" + masterFile + "\','" +
                        this.fileName + "', null, null, null, true);";
                } // else
            } // else
        } // else if document is a file

        // build page which performs javascript call:
        Page page = new Page (false);

        // IBS-331 Cannot open "Datei" that has been created in "Arbeitskorb"
        // The if condition has been changed from
        // if ((this.fileName.isEmpty ()) || (this.url.isEmpty ()))
        // to the following form.
        if (this.fileName.isEmpty () && this.url.isEmpty ())
                                        // is the filename or the url empty?
        {
            page.body.onLoad = "alert('" + 
                MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                BOMessages.MSG_NOACCESS_NONAME[2], env) + "');";
        } // if is the filename or the url empty
        else                            // neither filename nor url are empty
        {
            page.body.onLoad = masterUrl;
        } // else nether filename nor url are empty

        // add an dummy element because of better performance in IE
        page.body.addElement (new BlankElement ());

        // build page
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            this.env.write (e.getMsg ());
        } // catch
    } // performShowMaster


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
        //add specific parameters
        // isMaster
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, this.isMaster);

        // attachmentType
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.attachmentType);

        // fileName
        sp.addInParameter (ParameterConstants.TYPE_STRING, (this.fileName != null) ? this.fileName : "");

        // path
        sp.addInParameter (ParameterConstants.TYPE_STRING, (this.path != null) ? this.path : BOPathConstants.PATH_UPLOAD);

        //url
        sp.addInParameter (ParameterConstants.TYPE_STRING, (this.url != null) ? this.url : "");

        //filesize
        sp.addInParameter (ParameterConstants.TYPE_FLOAT, (this.attachmentType == DocConstants.ATT_HYPERLINK) ?
            0 : this.filesize);

        // checks if the url is a weblink or not
        // important when the user has set in his user profile, that every
        // files should be shown in an several frames
        this.isWeblink = this.checkIsWeblink (this.url);

        //isWeblink
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, this.isWeblink);
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
        // isMaster
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // attachmentType
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // fileName
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        //path
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        //url
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        //filesize
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_FLOAT);
        // isWeblink
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
     * @param lastIndex    The index to the last element used in params thus far.
     */
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // get the specific parameters:
        this.isMaster = params[++i].getValueBoolean ();
        this.attachmentType = params[++i].getValueInteger ();
        this.fileName = params[++i].getValueString ();
        this.path = params[++i].getValueString ();
        this.url = params[++i].getValueString ();
        this.filesize = params[++i].getValueFloat ();
        this.isWeblink = params[++i].getValueBoolean ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Reads the object data from an dataElement. <BR/>
     *
     * @param dataElement     the dataElement to read the data from
     *
     * @see ibs.bo.BusinessObject#readImportData
     */
    public void readImportData (DataElement dataElement)
    {
        String str;
        String fileNameStr;
        String filePathStr;

        // get business object specific values:
        super.readImportData (dataElement);
        // get the type specific values:
        if (dataElement.exists (Attachment_01.FIELD_FILENAME))
        {
            // set the attachment type to file
            this.attachmentType = DocConstants.ATT_FILE;
            // set the name of the file
            fileNameStr = FileHelpers.makeFileNameValid (
                dataElement.getImportStringValue (Attachment_01.FIELD_FILENAME));
            filePathStr = FileHelpers.makeFileNameValid (
                dataElement.sourcePath + File.separator + fileNameStr);
            // set path the file should be copied to
            if (this.checkIsValidFile (filePathStr))
            {
                // set the file we want to read
                dataElement.addFile (Attachment_01.FIELD_FILENAME,
                    this.getAbsPath (), fileNameStr,
                    FileHelpers.getFileSize (this.getAbsPath (), fileNameStr));
            } // if (checkIsValidFile (filePathStr))
        } // if (url == null)
        else    // no filename found thus create a hyperlink
        {
            str = dataElement.getImportStringValue (Attachment_01.FIELD_URL);
            this.url = str;
            this.attachmentType = DocConstants.ATT_HYPERLINK;
        } // else no filename found thus create a hyperlink
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
        // first check if the attachment requests a file
        if (this.attachmentType == DocConstants.ATT_FILE)
        {
            // do only change the value in case it exists
            if (dataElement.existsFile (Attachment_01.FIELD_FILENAME))
            {
                // set the name, path and the size of the file
                this.fileName = dataElement.getFileName (Attachment_01.FIELD_FILENAME);
                this.filesize = dataElement.getFileSize (Attachment_01.FIELD_FILENAME);
                // set the path only if we got the file
                if (this.filesize != -1)
                {
                    // set the path. This can be neccessary in case
                    // the object is newly imported
                    this.path = this.getPath ();
                } // if (this.filesize != -1)
                else // got no file
                {
                    // TODO BB050808: should be delete the path?
//                    this.path = null;
                } // else got no file
            } // if (dataElement.existsFile (FIELD_FILENAME))
        } // if (this.attachmentType == DocConstants.ATT_FILE)
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
        // set the business object specific values
        super.writeExportData (dataElement);
        // check wheather we have to export a hyperlink or a file
        if (this.attachmentType == DocConstants.ATT_FILE)
        {
            // construct the absolute path to the file to be exported
            String sourcePath =
                BOHelpers.getFilePath (DIHelpers.getOidFromPath (this.path));
            dataElement.setExportFileValue (Attachment_01.FIELD_FILENAME, sourcePath, this.fileName);
        } // if (attachmentType == DocConstants.ATT_FILE)
        else            // type is url
        {
            dataElement.setExportHyperlinkValue (Attachment_01.FIELD_URL, this.url);
        } // else type is url
    } // writeExportData


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
            Buttons.BTN_CHECKOUT,
            Buttons.BTN_CHECKIN,
            Buttons.BTN_EDIT,
            Buttons.BTN_DELETE,
            Buttons.BTN_CUT,
            Buttons.BTN_COPY,

            Buttons.BTN_DISTRIBUTE,
//            Buttons.BTN_STARTWORKFLOW,
//            Buttons.BTN_FORWARD,

            Buttons.BTN_FINISHWORKFLOW,
            Buttons.BTN_SEARCH,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons


    /**************************************************************************
     * Checks if the recieved url is a internal weblink or not. <BR/>
     *
     * @param url       the url to be checked
     *
     * @return  a boolean which is true if the url is a internal weblink
     */
    private boolean checkIsWeblink (String url)
    {
        boolean isUrlWeblink = false;   // is the return value of this method

        if (url != null)
        {
            // shifting all characters in url to lower case:
            url.toLowerCase ();

            // Checks the recieved url and looks if it is an internal weblink
            // or not. When the url is an internal weblink the return value of
            // this method is true. The url is a internal weblink when the
            // following points are fullfilled:
            //      1. The name of the server in the weblink is equal to the
            //         name of the server on which the application is running.
            //      2. The function number is equal to the function numer of the
            //         weblink (AppFunctions.FCT_WEBLINK).
            if (url.indexOf ("fct=" + AppFunctions.FCT_WEBLINK) >= 0)
            {
                isUrlWeblink = true;
            } // if
        } // if

        return isUrlWeblink;            // return value of this method
    } // checkIsWeblink


    /**************************************************************************
     * Checks if the recieved url is a internal weblink or not. <BR/>
     *
     * @param url       the url to be checked
     *
     * @return  a boolean which is true if the url is a internal weblink
     */
    protected boolean checkIsInternalWeblink (String url)
    {
        boolean isInternalWeblink = false;
        if (this.isWeblink)
        {
            isInternalWeblink = true;
            String serverName;

            // checks if the name of the server in the werblink is equal to
            // the name of the server on which the application is running.
            if (!((ServerRecord) this.sess.actServerConfiguration).getSsl ())
            {
                serverName = ((ServerRecord) this.sess.actServerConfiguration)
                    .getApplicationServer ().toLowerCase ();
            } // if
            else
            {
                serverName = ((ServerRecord) this.sess.actServerConfiguration)
                    .getSslServer ().toLowerCase ();
            } // else

            if (url.toLowerCase ().indexOf (serverName) < 0)
            {
                isInternalWeblink = false;
            } // if
        } // if
        return isInternalWeblink;
    } // checkIsInternalWeblink


    /**************************************************************************
     * Checks if the file is valid for upload. <BR/>
     * Overwrite this method to perform a check of the file
     * before it is moved in the upload directory.
     *
     * @param filePath      the file to be checked
     *
     * @return  a boolean which is true if the file is valid for
     *          upload
     */
    protected boolean checkIsValidFile (String filePath)
    {
        return true;
    } // checkIsValidFile


    /**************************************************************************
     * Retrieve the relevant data for this object (url) and then create
     * the right call to display the weblink properly. <BR/>
     */
    public void loadWeblinkUrl ()
    {
/* KR 020125: not necessary because already done before
        try
        {
            // try to retrieve the object:
            retrieve (Operations.OP_READ);
*/

        // build the Page to load the WeblinkUrl:
        this.performLoadWeblinkUrl ();
/* KR 020125: not necessary because already done before
         } // try
         catch (NoAccessException e) // no access to objects allowed
         {
            // send message to the user:
            showNoAccessMessage (Operations.OP_READ);
         } // catch
        catch (AlreadyDeletedException e) // no access to objects allowed
         {
            // send message to the user:
            showAlreadyDeletedMessage ();
         } // catch
*/
    } // loadWeblinkUrl


    /**************************************************************************
     * Build the Java Page with the correct call to show the weblink. <BR/>
     */
    private void performLoadWeblinkUrl ()
    {
        // is the weblink an internal weblink?
        boolean isInternalWeblink = this.checkIsInternalWeblink (this.url);
        boolean loadFrame = false;
        if (this.url.indexOf ("frame=true") > 0)
        {
            loadFrame = true;
        } // if

        Page page = new Page ("", false);

        // create script for calling the right url
        ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
        if (isInternalWeblink && loadFrame)
        {
            script.addScript ("top.showWeblink ('" + this.url + "','" + HtmlConstants.FRM_TOP + "');");
        } // if
        else if (isInternalWeblink)
        {
            script.addScript ("top.showWeblink ('" + this.url + "');");
        } // else if
        else if (loadFrame)
        {
            script.addScript ("top.showWindowWeblink ('" + this.url + "');");
        } // else if

        page.head.addElement (script);

        page.body.addElement (new BlankElement ());
        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // performLoadWeblinkUrl

} // class Attachment_01

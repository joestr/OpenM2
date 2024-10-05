/*
 * FileVersion_01.java
 */

// package:
package m2.version;

// imports:
import ibs.app.AppFunctions;
import ibs.bo.BOHelpers;
import ibs.bo.BOMessages;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.OID;
import ibs.bo.ObjectClassNotFoundException;
import ibs.bo.ObjectInitializeException;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.bo.tab.Tab;
import ibs.bo.tab.TabContainer;
import ibs.bo.type.Type;
import ibs.bo.type.TypeClassNotFoundException;
import ibs.bo.type.TypeNotFoundException;
import ibs.di.DIHelpers;
import ibs.di.ValueDataElement;
import ibs.di.XMLViewer_01;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.webdav.WebdavData;
import ibs.service.user.User;
import ibs.util.NoAccessException;
import ibs.util.UtilConstants;
import ibs.util.file.FileHelpers;

import java.util.Enumeration;

import org.w3c.dom.Document;


/******************************************************************************
 * This class represents one object for a file which can be versioned.
 * The class contains a version number. This number is gathered from
 * the VersionContainer_01 who is responsible for the versioning itself. <BR/>
 *
 * @version     $Id: FileVersion_01.java,v 1.15 2010/04/07 13:37:12 rburgermann Exp $
 *
 * @author      Bernd Martin (BM), 011115
 ******************************************************************************
 */
public class FileVersion_01 extends XMLViewer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FileVersion_01.java,v 1.15 2010/04/07 13:37:12 rburgermann Exp $";


    /**
     * The property contains the name of the form template for the property
     * filename. <BR/>
     */
    protected String ATTR_FILE = "Dateiname";

    /**
     * The class name of the version container tab. <BR/>
     * This name is used to find the tab object witch holds the version objects.
     * @see #getVersionContainerOid ()
     */
    protected String VERSIONTAB_CLASSNAME = "m2.version.VersionContainer_01";

    /**
     * The version container of the file. <BR/>
     */
    private VersionContainer_01 p_versionContainer = null;

    /**
     * The master version of the file. <BR/>
     */
//    private Version_01 p_masterVersion = null;

    /**
     * The name of the file argument for the master version. <BR/>
     */
    private String p_argMasterFile = null;

    /**
     * The type code of the version. <BR/>
     */
    protected String p_versionTypeCode = "m2Version";



    /**************************************************************************
     * This constructor creates a new instance of the class FileVersion_01.
     * <BR/>
     */
    public FileVersion_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // FileVersion_01


    /**************************************************************************
     * This constructor creates a new instance of the class FileVersion_01.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public FileVersion_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // FileVersion_01


    /**************************************************************************
     * This method returns OID of the version container. <BR/>
     *
     * @return  The OID of the version container tab object.
     */
    public OID getVersionContainerOid ()
    {
        OID versionTabOid = null;

        // Search the version container tab object of the current object.
        // The information about the object tabs can be retrieved by calling the
        // method getTabBar () (ATTENTION! do not use the property p_tabs because
        // at this time the values of the property are not uptodate!).
        // The problem is that the type code of the version container is not
        // known because the user can customize the XML template for this tab.
        // The only possibility to locate the version container tab is to
        // look at the java class behind the tab object.
        // Any version container type must use the class 'm2.version.VersionContainer_01'.
        // We assume that the first tab object witch uses this class is the
        // version container tab.

        TabContainer tabs = this.getTabBar ();
        if (tabs != null)
        {
            Enumeration<Tab> elems = tabs.elements ();
            while (elems.hasMoreElements () && versionTabOid == null)
            {
                Tab t = elems.nextElement ();
                // get the tab oid: if the oid is not valid the tab is not
                // a physical tab and can be ignored
                OID tabOid = t.getOid ();
                if (tabOid != null)
                {
                    // get the type of the tab object to determinate the
                    // java class of the tab object.
                    Type tabType =
                        this.getTypeCache ().getType (tabOid.tVersionId);

                    if (tabType != null)
                    {
                        try
                        {
                            Class<?> c = tabType.getTypeClass (env);
                            Class<?> cSuper;
                            String className = c.getName ();
                            while (className != null && !className.equals ("ibs.bo.BusinessObject"))
                            {
                                // if the class is the VersionContainer_01 class
                                // we assume that this tab object is the version tab
                                if (className.equals (this.VERSIONTAB_CLASSNAME))
                                {
                                    versionTabOid = tabOid;
                                    break;
                                } // if the tab has the correct class
                                // get the class object:
                                cSuper = c.getSuperclass ();
                                className = cSuper.getName ();
                                c = cSuper;
                            } // while
                        } // try
                        catch (TypeClassNotFoundException e)
                        {
                            IOHelpers.showMessage (e, this.app, this.sess, this.env);
                        } // catch
                    } // if type valid
                } // if tabOid is valid
            } // enum
        } // if tab info loaded

        // return the oid of the version container tab
        return versionTabOid;
    } // getVersionContainerOid


    /**************************************************************************
     * Returns the representation of the object as a DOM object. <BR/>
     *
     * @param   viewMode    The specific view mode.
     *
     * @return  The DOM tree of the object.
     */
    public Document createDomTree (int viewMode)
    {
//        this.isShowDOMTree = true;
        Document doc = super.createDomTree (viewMode);

/*
        if (doc != null)
        {
            // get the master version:
            Version_01 masterVersion =
                getMasterVersionWithDependentParameters ();

            // ensure that the master version has unique names for tokens
            // and arguments:
            masterVersion.setNumInList (1);
            masterVersion.getParameters ();

            // get the values node out of the documet:
            NodeList valuesNodeList =
                doc.getElementsByTagName (DIConstants.ELEM_VALUES);
            // get the first item out of the node list:
            Node valuesNode = valuesNodeList.item (0);

            if (valuesNodeList.getLength () == 1)
            {
                if (viewMode == XMLViewer_01.VIEWMODE_SHOW)
                {
                    String masterName = "";
                    String masterLink = "";

                    // if a master version exists then set masterName
                    // variable to show it:
                    if (masterVersion != null && !masterVersion.oid.isTemp ())
                    {
                        // set the name of the master
                        masterName = masterVersion.name;
                        // add the link of the master to the file
                        masterLink =
                            masterVersion.oid + "/" + masterVersion.getFilename ();
                    } // if masterVersion not null

                    // append the node for the master to the DOM tree
                    // the master is shown dynamically
                    Element value = doc.createElement (DIConstants.ELEM_VALUE);
                    value.setAttribute
                        (DIConstants.ATTR_FIELD, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MASTERVERSION, env));
                    value.setAttribute
                        (DIConstants.ATTR_TYPE, DIConstants.VTYPE_FILE);
                    value.setAttribute (DIConstants.ATTR_URL, masterLink);
                    // set the input field name (replace all critical characters)
//                    value.setAttribute ("INPUT", createArgument (vie.field));
                    value.appendChild (doc.createTextNode (masterName));

                    valuesNode.appendChild (value);
                } // if show mode

                // get all values out of the master version and add them
                // to the actual values:
                // <NAME>:
                masterVersion.createDomTreeValueNode (doc, valuesNode,
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env), DIConstants.VTYPE_TEXT,
                    masterVersion.name, DIConstants.ATTRVAL_NO, null,
                    BOArguments.ARG_NAME, viewMode);

                // <DESCRIPTION>:
                masterVersion.createDomTreeValueNode (doc, valuesNode,
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DESCRIPTION, env), DIConstants.VTYPE_TEXT,
                    masterVersion.description, DIConstants.ATTRVAL_NO, null,
                    BOArguments.ARG_DESCRIPTION, viewMode);

                // <VALIDUNTIL>:
                String validDate = Helpers
                    .dateToString (masterVersion.validUntil);
                masterVersion.createDomTreeValueNode (doc, valuesNode,
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_VALIDUNTIL, env), DIConstants.VTYPE_DATE, validDate,
                    DIConstants.ATTRVAL_NO, null, BOArguments.ARG_VALIDUNTIL,
                    viewMode);

                // <VALUES>:
                this.p_argMasterFile = masterVersion.getFilenameArg ();

                if (!masterVersion.oid.isTemp ())
                {
                    masterVersion.createDomTreeValues (doc, valuesNode,
                        viewMode);
                } // if
                else
                {
                    // set the fields manually
                    // file name:
                    masterVersion.createDomTreeValueNode (doc, valuesNode,
                        Version_01.ATTR_FILE, DIConstants.VTYPE_FILE, null,
                        DIConstants.ATTRVAL_NO, null, null, viewMode);

                    // file size:
                    masterVersion.createDomTreeValueNode (doc, valuesNode,
                        Version_01.ATTR_SIZE, DIConstants.VTYPE_INT, null,
                        DIConstants.ATTRVAL_NO, null, null, viewMode);

                    // version number:
                    masterVersion.createDomTreeValueNode (doc, valuesNode,
                        Version_01.ATTR_VERSION, DIConstants.VTYPE_INT, null,
                        DIConstants.ATTRVAL_NO, null, null, viewMode);

                    // is this the master:
                    masterVersion.createDomTreeValueNode (doc, valuesNode,
                        Version_01.ATTR_MASTER, DIConstants.VTYPE_BOOLEAN, null,
                        DIConstants.ATTRVAL_NO, null, null, viewMode);
                } // else
            } // if elements in nodelist

            // ensure that the master version is independent:
            masterVersion.dropFromList ();
        } // if document not null
*/

        return doc;
    } // createDomTree


    /***************************************************************************
     * This method returns the master of the actual container which must be a
     * version container. If the version container object is already retrieved
     * it can be passed as parameter, otherwise the container object is
     * retrieved in the method. <BR/>
     * After getting the object the parameters are read.
     *
     * @return  The version object which represents the master. If there exists
     *          no master either a dummy object is created or <CODE>null</CODE>
     *          is returned depending on the value of the argument createDummy.
     */
    protected Version_01 getMasterVersionWithDependentParameters ()
    {
        // get the master version:
        Version_01 masterVersion = this.getMasterVersion (null, true);

        // ensure that the master version has unique names for tokens
        // and arguments:
        masterVersion.setNumInList (1);
        masterVersion.getParameters ();

        // return the result:
        return masterVersion;
    } // getMasterVersionWithDependentParameters


    /***************************************************************************
     * This method returns the master of the actual container which must be a
     * version container. If the version container object is already retrieved
     * it can be passed as parameter, otherwise the container object is
     * retrieved in the method.
     *
     * @param   vCont       The version container object. This parameter is
     *                      optional and can be <code>null</code>.
     * @param   createDummy If there is no master version existing the method
     *                      can create a dummy.
     *
     * @return  The version object which represents the master. If there exists
     *          no master either a dummy object is created or <CODE>null</CODE>
     *          is returned depending on the value of the argument createDummy.
     */
    protected Version_01 getMasterVersion (VersionContainer_01 vCont,
                                           boolean createDummy)
    {
        VersionContainer_01 vContLocal = vCont; // variable for local assignments
        Version_01 masterVersion = null;

        if (vContLocal == null)
        {
            // get the oid of the version container tab
            OID versionContainerOid = this.getVersionContainerOid ();
            // if the tab was found
            if (versionContainerOid != null)
            {
                // get the version container object
                vContLocal = this.getVersionContainer (versionContainerOid);
            } // if container oid found
        } // if version container not given

        if (vContLocal != null)
        {
            masterVersion = vContLocal.getMasterVersion ();
        } // if vCont valid

        // check if there exists a master version:
        if (masterVersion == null && createDummy)
        {
            // create a dummy object on the fly:
            masterVersion = (Version_01)
                BOHelpers.getNewObject (VersionConstants.TC_VERSION, this.env, false);
        } // if

        // return the result:
        return masterVersion;
    } // getMasterVersion


    /**************************************************************************
     * Checks out a Businessoject (freezes it for other users). <BR/>
     *
     * @return  The BusinessObject which was checked out.
     *
     * @throws  NoAccessException
     *          The user does not have access to this object to perform the
     *          required operation.
     */
    public BusinessObject checkOut () throws NoAccessException
    {
        // we redirect the standard checkOut to the WebDav checkout:
        return this.webdavCheckOut ();

/* KR 20500930 It is not necessary to check out the master.
 *            The master cannot be checked out and cannot be edited.
        Version_01 master = null;

        // master found:
        if ((master = getMasterVersion (null, false)) != null)
        {
            master.checkOut ();

            // message must be displayed here because the application does not know
            // about the structure of versioning
            showPopupMessage (
                Helpers.replace (BOMessages.MSG_OBJECTCHECKEDOUT,
                                 UtilConstants.TAG_QUOTEDNAME,
                                 master.name));
        } // if master is available for checkout
 */
    } // checkOut


    /**************************************************************************
     * The method should show the edit form before checking in the actual
     * object. <BR/>
     *
     * @param   function    The function which is called with the OK button.
     *
     * @throws  NoAccessException
     *          The user does not have access to this object to perform the
     *          required operation.
     */
    public void editBeforeCheckIn (int function) throws NoAccessException
    {
        this.checkIn ();
/*
        Version_01 master = null;

        // check if the master is checked out by the current user:
        if ((master = getMasterVersion (null, false)) != null &&
           (this.user.id == master.checkOutUser.id))
        {
            // check if the object was changed:
            if (isObjectChanged (master)) // the object was changed?
            {
                // message must be displayed here because the application does not know
                // about the structure of versioning
                showPopupMessage (
                    Helpers.replace (BOMessages.MSG_OBJECTEDITCHECKIN,
                                     UtilConstants.TAG_QUOTEDNAME,
                                     master.name));

                // show the change form and set the next function FCT_CHECKIN
//                master.editBeforeCheckIn (AppFunctions.FCT_CHECKIN);
            } // if the object was changed
        } // if

        // call common method:
        super.editBeforeCheckIn (function);
*/
    } // editBeforeCheckIn


    /**************************************************************************
     * Checks in a Businessoject (makes it available to other users). <BR/>
     *
     * @return  The actual business object is returned.
     *
     * @throws  NoAccessException
     *          The user does not have access to this object to perform the
     *          required operation.
     */
    public BusinessObject checkIn () throws NoAccessException
    {
        BusinessObject obj = null;

        // check if the actual user has checked out the object and made some
        // changes to the object:
        // if there is no master existent a new one has to be created
        if (this.checkedOut && this.user.id == this.checkOutUser.id &&
            this.isObjectChanged ())
                                        // create new version?
        {
/*
            // message must be displayed here because the application does
            // not know about the structure of versioning:
            showPopupMessage (Helpers.replace (
                BOMessages.MSG_OBJECTEDITCHECKIN,
                UtilConstants.TAG_QUOTEDNAME, this.name));
*/
            // create a new version:
            this.createVersion (this.getVersionContainerOid ());
        } // if create new version
        else                            // no changes
        {
            // call common method:
            obj = super.checkIn ();

            // show the object:
            this.show (UtilConstants.REP_STANDARD);
        } // else no changes

        // return the result:
        return obj;
    } // checkIn


    /**************************************************************************
     * This methods performs a checkIn of the FileVersion_01. <BR/>
     * The method is intended to be called with the version where the copy
     * of this object shall be placed.
     *
     * @param   version The version where to place the copy of this object.
     *
     * @throws  NoAccessException
     *          The user does not have access to this object to perform the
     *          required operation.
     */
    public void checkInFromVersion (Version_01 version) throws NoAccessException
    {
        // check in the file field:
        this.checkInFile (this.ATTR_FILE);

        this.showAllowed = false;

        // now perform the change of the actual object:
        this.change (Operations.OP_CHANGE);

        // create a copy of the actual object which will be the
        // version object of the version:
        this.createVersionObject (version);

        // call common method:
        super.checkIn ();

        // show the object:
        this.show (UtilConstants.REP_STANDARD);
    } // checkInFromVersion


    /**************************************************************************
     * Create the versioned copy of this object. <BR/>
     * TheThis methods performs a checkIn of the FileVersion_01. <BR/>
     * The method is intended to be called with the version where the copy
     * of this object shall be placed.
     *
     * @param   version The version where to place the copy of this object.
     */
    public void createVersionObject (Version_01 version)
    {
        OID copyOid = null;
        FileVersion_01 copyObj = null;

        // check if the version has already a version object:
        if (version.getVersionObject () == null)
                                        // no version object yet?
        {
            try
            {
                // create a copy of the actual object which will be the
                // version object of the version:
                // The copy function  copies only the object itself and not the
                // tree below. We don't even need the tabs.
                // TODO KR: It would be best if the copy was a tab of the version.
                copyOid =
                    this.performCopyData (version.oid, Operations.OP_NONE, false);
                copyObj = (FileVersion_01) BOHelpers.getObject (copyOid, this.env, false, false, false);
                copyObj.setDeletable (false, false);
                copyObj.setChangeable (false, true);
            } // try
            catch (NoAccessException e)
            {
                // should not possible because of OP_NONE
                // display error message:
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
        } // if no version object yet
        else                            // version object already defined
        {
            this
                .showPopupMessage ("Für diese Version wurde schon ein Versionsobjekt erzeugt.");
        } // if version object already defined
    } // createVersionObject


    /**************************************************************************
     * This methods performs a checkIn of a file field. <BR/>
     *
     * @param   fileField       The name of the field with the file data.
     */
    public void checkInFile (String fileField)
    {
        String inputFieldFile;
        String sourcepath;
        String targetpath;
        String webdavFilename;
        ValueDataElement value;
        WebdavData webdavData = null;

        // delete the webdav file and directory without copying the
        // webdav file that has been overwritten by the upload now
        // note that we set the oid of the masterVersion in order to
        // get the correct sourcepath
        webdavData = new WebdavData (this.app, this.oid, this.user);
        // path to webdav root + user's webdav directory + directory of
        // the object
        sourcepath = webdavData.p_webdavObjectDir;
//        targetpath = webdavData.p_filesDir;
        // the target path is determined by the new version
        targetpath = BOHelpers.getFilePath (this.oid);
        // get the actual data element:
        value = this.dataElement.getValueElement (fileField);
        if (value != null)
        {
            // get the webdav filename:
            webdavFilename =
                WebdavData.getWebdavFilename (value.value, this.checkOutDate);

            // we need to check if a new file has been uploaded
            // in that case we ignore any previous file and just take the
            // new file
            // if not we try to copy the wevdav file
            inputFieldFile = DIHelpers.replaceCriticalCharacters (fileField);
            if (this.getFileParamBO (inputFieldFile) == null)
            {
                // copy file back from webdav to files directory:
                FileHelpers.copyFile (sourcepath + webdavFilename,
                    targetpath + value.value);
                // set filename and filesize
                this.setFilename (fileField, value.value);
            } // if ((str = getFileParamBO (inputFieldFile)) == null)

            // delete file in webdav directory:
            FileHelpers.deleteFile (sourcepath + webdavFilename);
            // delete object directory in the user's webdav directory:
            FileHelpers.deleteDir (webdavData.p_webdavObjectDirNoSep);
        } // if
    } // checkInFile


    /**************************************************************************
     * Create a new version object. <BR/>
     *
     * @param   versionContainerOid The oid of the version container.
     *
     * @return  The new version object.
     *          <CODE>null</CODE> if there occurred an error.
     */
    protected Version_01 createVersion (OID versionContainerOid)
    {
        Version_01 version = null;      // the newly created business object
        Version_01 masterVersion = null; // the master version
        OID oid;                        // oid of the new business object
        boolean isShowPossible = true;  // return value of obj.showChangeForm

        // create the version object:
        if ((version = (Version_01) BOHelpers.getNewObject (
            this.p_versionTypeCode, this.env)) != null)
                                        // got object?
        {
            // try to get the master version:
            masterVersion = this.getMasterVersion (null, false);
            if (masterVersion != null)
            {
                // get the name out of the master version:
                version.name = masterVersion.name;
            } // if

            // ensure that the object has a correct name:
            if (version.name == null)   // no name defined?
            {
                version.name = "";      // set default name
            } // if

            // set container id:
            version.containerId = versionContainerOid;
            // set major object:
            version.setMajorObjOid (this.oid);
            // create the new object:
            oid = version.create (UtilConstants.REP_STANDARD);

            if (oid != null)            // object was created, oid returned?
            {
                // ensure that the object is in the cache:
                if (version.isPhysical) // the object is physical?
                {
                    // store the actual object in the session cache:
                    (this.getUserInfo ()).actObject = version;
                } // if the object is physical

                // object successfully created => show change form

                // don't show extended creation menu:
                version.showExtendedCreationMenu = false;
                // show the tabs:
                version.displayTabs = true;

                // ensure that the correct function is called for framesets:
                this.sess.p_actFct = AppFunctions.FCT_OBJECTCHANGEFORM;
                // show the form:
                isShowPossible = version.showChangeForm (
                    UtilConstants.REP_STANDARD, AppFunctions.FCT_CHANGECHECKIN);

                if (!isShowPossible)    // object could not be created?
                {
                    // the object could not be created so we have to show a
                    // message to the user and then display the container
                    // within which the new button was pressed:
                    IOHelpers.showPopupMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_NOTEMPLATEEXISTS, env),
                            null, this.app, this.sess, this.env);
                    this.show (UtilConstants.REP_STANDARD);
                } // if object could not be created
            } // if object was created, oid returned
            else                        // object not created
            {
                // show corresponding message:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_OBJECTNOTCREATED, env),
                        this.app, this.sess, this.env);
            } // else object not created
        } // if got object
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTNOTFOUND, env),
                    this.app, this.sess, this.env);
        } // else

        // return the newly created object:
        return version;
    } // createVersion


    /**************************************************************************
     * Check if the object was changed. <BR/>
     * The method gets the master version and checks all fields against the
     * version. <BR/>
     * If the version object does not have all values of this object it is
     * assumed to be different. The method returns <CODE>true</CODE>.
     *
     * @return  <CODE>true</CODE> if the object was changed,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean isObjectChanged ()
    {
        // call common method:
        return this.isObjectChanged (this.getMasterVersion (null, false));
    } // isObjectChanged


    /**************************************************************************
     * Check if the object was changed. <BR/>
     * The method gets the master version and checks all fields against the
     * version. <BR/>
     * If the version object does not have all values of this object it is
     * assumed to be different. The method returns <CODE>true</CODE>.
     *
     * @param   master  The master against which to check the object.
     *
     * @return  <CODE>true</CODE> if the object was changed,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean isObjectChanged (Version_01 master)
    {
        boolean isChanged = false;
        FileVersion_01 versionObj = null; // the version object

        // check the master version:
        if (master != null)
        {
            // get the version object:
            if ((versionObj = (FileVersion_01) master.getVersionObject ()) != null)
            {
                // check if the object was changed:
                // it was changed if it is newer than the master version and
                // at least one value was changed.
                isChanged = this.lastChanged.after (versionObj.lastChanged) &&
                    !this.dataElement.equals (versionObj.dataElement);
            } // if
            else
            {
                // if there was no old version the object is always changed:
                isChanged = true;
            } // else
/*
            // compare all fields with the fields of the master version:
            for (Iterator iter = this.dataElement.values.iterator ();
                 !isChanged && iter.hasNext ();)
            {
                ValueDataElement value = (ValueDataElement) iter.next ();

                // search for the value within the master:
                ValueDataElement masterValue =
                    master.dataElement.getValueElement (value.field);

                if (masterValue != null) // value exists?
                {
                    if ((masterValue.value == null && value.value != null) ||
                        (masterValue.value != null &&
                         !masterValue.value.equals (value.value)))
                    {
                        isChanged = true;
                    } // if
                } // if value exists
                else                    // value does not exist
                {
                    // the object has another structure, so it is not the same:
                    isChanged = true;
                } // else
            } // for iter
*/
        } // if
        else
        {
            // if there was no old version the object is always changed:
            isChanged = true;
        } // else

        // return the result:
        return isChanged;
    } // isObjectChanged


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
        int[] buttons = super.setInfoButtons ();

        for (int i = 0; i < buttons.length; i++)
        {
            // if button checkin can be available substitute it with
            // editbefore checkin
            if (buttons[i] == Buttons.BTN_CHECKIN)
            {
                buttons[i] = Buttons.BTN_NONE;
            } // if
            else if (buttons[i] == Buttons.BTN_CHECKOUT)
            {
                buttons[i] = Buttons.BTN_NONE;
            } // elseif
            else if (buttons[i] == Buttons.BTN_EDITBEFORECHECKIN)
            {
                buttons[i] = Buttons.BTN_NONE;
            } // elseif
        } // for

/*
        VersionContainer_01 vCont = getVersionContainer (getVersionContainerOid ());
        Version_01 master = getMasterVersion (vCont, false);
        // if the master set already
        if (master != null)
*/
        // define a new button array
        int[] newButtons = new int[buttons.length + 2];

        // copy the button array
        for (int i = 0; i < buttons.length; i++)
        {
            newButtons[i] = buttons[i];
        } // for

        // set the button for check in
        newButtons[buttons.length] = this.getButtonCheckin ();
        // set the button for check out
        newButtons[buttons.length + 1] = this.getButtonCheckout ();
        // copy the newbutton array to the button array
        buttons = newButtons;

        // return an empty array (no button to display)
        return buttons;
    } // setInfoButtons


    /**************************************************************************
     * This method checks if the button checkout must be displayed or not. <BR/>
     * The master must not be null, otherwise a NullpointerException will be
     * thrown.
     *
     * @return  The button CHECKOUT if it should be displayed or NONE if not.
     */
    protected int getButtonCheckout ()
    {
        // display the button only if the master is not checked out:
        if (!this.checkedOut)
        {
            // button shall be displayed
            return Buttons.BTN_CHECKOUT;
        } // if checked out

        return Buttons.BTN_NONE;
    } // getButtonCheckout


    /**************************************************************************
     * This method checks if the button checkin must be displayed or not. <BR/>
     * The button must be displayed when the master was checked out by the same
     * user only. For Versioning the button EDITBEFORECHECKIN must be displayed
     * instead of the checkin. <BR/>
     * The master must not be null, otherwise a NullpointerException will be
     * thrown.
     *
     * @return  The button EDITBEFORECHECKIN if it should be displayed or NONE
     *          if not.
     */
    protected int getButtonCheckin ()
    {
        // check if the object is checked out by the current user:
        if (this.checkedOut && this.checkOutUser.id == this.getUser ().id)
        {
            return Buttons.BTN_EDITBEFORECHECKIN;
        } // if
/*
        if (master != null && master.checkedOut)
        {
            OID checkedOutOid = null;
            int userCheckOutId = -1;

            if (vCont != null)
            {
                Vector checkOutInfo = null;
                try
                {
                    // get the info from the checked out object
                    checkOutInfo = vCont.getCheckedOutInfo ();
                } // try
                catch (NoAccessException e)
                {
                    checkOutInfo = null;
                } // catch

                // if info available
                if (checkOutInfo != null)
                {
                    // get the oid of the checked out object
                    checkedOutOid = (OID) checkOutInfo.elementAt (0);
                    // get the id of the user who did the check out
                    userCheckOutId =
                        ((Integer) checkOutInfo.elementAt (1)).intValue ();
                } // if

                // only the 'checkin' button is allowed
                // but the button realizes the editing first so
                // that a new version is created
                if (this.oid.equals (checkedOutOid) &&
                    this.user.id == userCheckOutId)
                {
                    return Buttons.BTN_EDITBEFORECHECKIN;
                } // if user allowed to see the button
            } // if version container valid
        } // master available and checked out
*/

        return Buttons.BTN_NONE;
    } // getButtonCheckin


    /**************************************************************************
     * The method sets the filename of the actual object. <BR/>
     *
     * @param   filename    The new filename.
     * @param   fileField   The name of the field with the file data.
     */
    public void setFilename (String fileField, String filename)
    {
        // return the boolean value isMaster out of the dataelement
        this.dataElement.changeValue (this.ATTR_FILE, filename);
    } // setFilename


    /**************************************************************************
     * This method sets the filesize in the dataelement from a given filepath.
     * If the file cannot be found -1 is set.
     *
     * @param   file            The absolute path to the file.
     * @param   fileSizeField   The name of the field with the file size data.
     */
/* KR not needed because the file size is saved within the file value itself.
    private void setFilesize (String file, String fileSizeField)
    {
        long fileSize = -1;

        File f = new File (file);
        if (f.exists ())            // the file exists?
        {
            // get the filesize of the file
            fileSize = f.length ();

            // set the file size in the corresponding attribute:
            this.dataElement.changeValue (fileSizeField, "" + fileSize);
        } // if the file exists
    } // setFilesize
*/


    /**************************************************************************
     * This method gets the version container. The
     * operation indicates whatfor the container is needed. <BR/>
     *
     * @return  The VersionContainer_01 object if found or null else.
     *
     * @see #getVersionContainerOid ()
     */
    protected VersionContainer_01 getVersionContainer ()
    {
        // get the stored version container object:
        VersionContainer_01 versCont = this.p_versionContainer;

        if (versCont == null)
        {
            // call common method:
            versCont = this.getVersionContainer (this.getVersionContainerOid ());
        } // if

        // return the result:
        return versCont;
    } // getVersionContainer


    /**************************************************************************
     * This method gets the version container from the container oid. The
     * operation indicates whatfor the container is needed. <BR/>
     *
     * @param   vContOid    The oid of the version container.
     *
     * @return  The VersionContainer_01 object if found or null else.
     */
    protected VersionContainer_01 getVersionContainer (OID vContOid)
    {
        // get the stored version container object:
        VersionContainer_01 versCont = this.p_versionContainer;

        if (versCont == null)
        {
            try
            {
                // get the container from the cache
                versCont = (VersionContainer_01) this.getObjectCache ()
                    .fetchObject (vContOid, this.user, this.sess, this.env,
                        true);
                // store the version container:
                this.p_versionContainer = versCont;
            } // try
            catch (ObjectNotFoundException e)
            {
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_OBJECTNOTFOUND, env),
                    this.app, this.sess, this.env);
            } // catch
            catch (TypeNotFoundException e)
            {
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_TYPENOTFOUND, env),
                    this.app, this.sess, this.env);
            } // catch
            catch (ObjectClassNotFoundException e)
            {
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_CLASSNOTFOUND, env),
                    this.app, this.sess, this.env);
            } // catch
            catch (ObjectInitializeException e)
            {
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_INITIALIZATIONFAILED, env),
                    this.app, this.sess, this.env);
            } // catch
        } // if

        // return the result:
        return versCont;
    } // getVersionContainer


    /**************************************************************************
     * Set the specific properties for tabview version. <BR/>
     *
     * @param   majorObject The major object of this view tab.
     */
    public void setSpecificProperties (BusinessObject majorObject)
    {
        BusinessObject versionObj = null;

        // call common method:
        super.setSpecificProperties (majorObject);

        // display the version object:
        versionObj = ((Version_01) majorObject).getVersionObject ();
        versionObj.show (UtilConstants.REP_STANDARD);
    } // setSpecificProperties


    /**************************************************************************
     * Creates and returns a copy of this object. <BR/>
     * For any object <tt>x</tt>, the following expressions will be
     * <tt>true</tt>:
     * <blockquote><pre>
     * x.clone () != x
     * x.clone ().getClass () == x.getClass ()
     * x.clone ().equals (x)
     * </pre></blockquote>
     * The object returned by this method is independent of this object (which
     * is being cloned).
     *
     * @return  A clone of this instance. <BR/>
     *
     * @exception   OutOfMemoryError
     *              if there is not enough memory.
     *
     * @see java.lang.Cloneable
     */
    public Object clone () throws OutOfMemoryError
    {
        FileVersion_01 clone = null;      // the new object

        try
        {
            // call corresponding method of super class:
            clone = (FileVersion_01) super.clone ();

            // set specific properties:
            // because the clone method of {@link java.lang.Object Object}
            // performs a shallow and not a deep copy of all existing properties
            // we have to perform the deep copy here to ensure that there are
            // no side effects.
            clone.p_argMasterFile = new String (this.p_argMasterFile);
            clone.p_versionTypeCode = new String (this.p_versionTypeCode);
            clone.p_versionContainer = null;
        } // try
        catch (OutOfMemoryError e)
        {
            // should not occur
            IOHelpers.printError ("Error when cloning FileVersion_01", e, true);
        } // catch OutOfMemoryError

        // return the new object:
        return clone;
    } // clone

} // class FileVersion_01

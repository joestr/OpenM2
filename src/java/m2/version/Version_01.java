/*
 * Version_01.java
 */

// package:
package m2.version;

// imports:
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOMessages;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.OID;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.bo.States;
import ibs.bo.tab.Tab;
import ibs.di.DIConstants;
import ibs.di.ValueDataElement;
import ibs.di.XMLViewer_01;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.Counter;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;
import ibs.util.UtilConstants;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/******************************************************************************
 * This class represents one object for a file which can be versioned.
 * The class contains a version number. This number is gathered from
 * the VersionContainer_01 who is responsible for the versioning itself. <BR/>
 *
 * @version     $Id: Version_01.java,v 1.18 2012/04/20 09:48:18 btatzmann Exp $
 *
 * @author      Bernd Martin (BM), 011115
 ******************************************************************************
 */
public class Version_01 extends XMLViewer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Version_01.java,v 1.18 2012/04/20 09:48:18 btatzmann Exp $";


    /**
     * The property contains the token of the from template. <BR/>
     */
    private static final String TYPECODE = "m2Version";

    /**
     * The property contains the name of the form template for the property
     * version. <BR/>
     */
    public static final String ATTR_VERSION  = "Version";

    /**
     * The property contains the name of the form template for the property
     * size. <BR/>
     */
    public static final String ATTR_SIZE     = "Dateigröße";

    /**
     * The property contains the name of the form template for the property
     * filename. <BR/>
     */
    public static final String ATTR_FILE     = "Dateiname";

    /**
     * The property contains the name of the form template for the property
     * master. <BR/>
     */
    public static final String ATTR_MASTER = "Master";

    /**
     * The property contains the name of the database field of the master from
     * the form template. <BR/>
     */
    public static final String DBFIELD_MASTER = "m_isMaster";

    /**
     * The property contains the name of the database field of the version from
     * the form template. <BR/>
     */
    public static final String DBFIELD_VERSION  = "m_Version";


    /**
     * Number in list value. <BR/>
     * This value is used if the object is part of a list.<BR/>
     * Default: <CODE>-1</CODE> (means no list part)
     */
    private int p_numInList = -1;

    /**
     * The object which is stored within this version. <BR/>
     */
    private BusinessObject p_versionObj = null;

    /**
     * The oid of the major object where this version belongs to. <BR/>
     */
    private OID p_majorObjOid = null;


    /**************************************************************************
     * This constructor creates a new instance of the class Version_01. <BR/>
     */
    public Version_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // Version_01


    /**************************************************************************
     * This constructor creates a new instance of the class Version_01. <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    @Deprecated
    public Version_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // Version_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        super.initClassSpecifics ();
        // do not display the extended menu
        // after clicking ok the info view from the new object is displayed
        this.showExtendedCreationMenu = false;
    } // initClassSpecifics


    /**************************************************************************
     * This method gets the majorObjOid. <BR/>
     *
     * @return Returns the majorObjOid.
     */
    public OID getMajorObjOid ()
    {
        // get the property value and return the result:
        return this.p_majorObjOid;
    } // getMajorObjOid


    /**************************************************************************
     * This method sets the majorObjOid. <BR/>
     *
     * @param majorObjOid The majorObjOid to set.
     */
    public void setMajorObjOid (OID majorObjOid)
    {
        // set the property value:
        this.p_majorObjOid = majorObjOid;
    } // setMajorObjOid


    /**************************************************************************
     * Returns the representation of the object as a DOM object. <BR/>
     *
     * @param   viewMode    The specific view mode.
     *
     * @return  The DOM tree of the object.
     */
    public Document createDomTree (int viewMode)
    {
        Document doc = super.createDomTree (viewMode);
        NodeList nodeList = doc.getElementsByTagName (DIConstants.ELEM_VALUE);
        NamedNodeMap map = null;
        Node attrNode = null;

        // edit mode?
        if (doc != null && viewMode == XMLViewer_01.VIEWMODE_EDIT)
        {
            for (int i = 0; i < nodeList.getLength (); i++)
            {
                map = nodeList.item (i).getAttributes ();
                attrNode = map.getNamedItem (DIConstants.ATTR_FIELD);

                // check if attribute is ismaster, size or version
                if (attrNode.getNodeValue ().equalsIgnoreCase (
                    Version_01.ATTR_VERSION) ||
                    attrNode.getNodeValue ().equalsIgnoreCase (
                        Version_01.ATTR_SIZE) ||
                    attrNode.getNodeValue ().equalsIgnoreCase (
                        Version_01.ATTR_MASTER))
                {
                    try
                    {
                        // remove the node
                        nodeList.item (i).getParentNode ().removeChild (nodeList.item (i));
                        i--;
                    } // try
                    catch (DOMException e)
                    {
                        // should not occur, display message:
                        IOHelpers.showMessage (e, this.app, this.sess,
                            this.env, true);
                    } // catch
                } // if
            } // for
        } // if mode is edit
        // elseif view mode is show
        else if (doc != null && viewMode == XMLViewer_01.VIEWMODE_SHOW)
        {
/*            // loop through nodelist
            for (int i = 0; i < nodeList.getLength (); i++)
            {
                map = nodeList.item (i).getAttributes ();
                attrNode = map.getNamedItem (DIConstants.ATTR_FIELD);

                // find the is master attribute
                if (attrNode.getNodeValue ().equalsIgnoreCase (this.ATTR_MASTER))
                {
                    try
                    {
                        // remove the node of is master
                        nodeList.item (i).getParentNode ().removeChild (nodeList.item (i));
                        i--;
                    } // try
                    catch (DOMException e)
                    {
                        // should not occur, display message:
                        IOHelpers.showMessage (e, this.app, this.sess,
                            this.env, true);
                    } // catch
                } // if
            } // for
*/
        } // else if mode is show

        return doc;
    } // createDomTree


    /**************************************************************************
     * This method calls the super method and then tries to get the size of
     * new file. Therefore the file must be found in the file system. <BR/>
     */
    public void getParameters ()
    {
//        String str = null;
        super.getParameters ();

/*
        // get the temporary file name
        String fileName = "";

        // if the temporary file is valid it will be moved in the
        // upload directory
        if (this.dataElement != null)
        {
            String inputFieldFile = DIHelpers.replaceCriticalCharacters (Version_01.ATTR_FILE);
            if ((str = getFileParamBO (inputFieldFile)) != null)
            {
                fileName = str;

                // path = filename + extension
                if ((str = env.getStringParam (inputFieldFile + AppConstants.DT_FILE_PATH_EXT)) != null)
                {
                    // get size of the file in Bytes
                    setFilesize (str + fileName);
                } // if file param found
            } // if
        } // if
*/
    } // getParameters


    /**************************************************************************
     * This method gets the numInList. <BR/>
     *
     * @return Returns the numInList.
     */
    public int getNumInList ()
    {
        // get the property value and return the result:
        return this.p_numInList;
    } // getNumInList


    /**************************************************************************
     * This method sets the numInList. <BR/>
     *
     * @param numInList The numInList to set.
     */
    public void setNumInList (int numInList)
    {
        // set the property value:
        this.p_numInList = numInList;
    } // setNumInList


    /**************************************************************************
     * This method sets the numInList to -1. <BR/>
     */
    public void dropFromList ()
    {
        // set the property value:
        this.p_numInList = -1;
    } // dropFromList


    /**************************************************************************
     * This method takes a field name and possibly adopts it for the actual
     * object. <BR/>
     *
     * @param   fieldName   The name of the field.
     *
     * @return  The possibly adopted field name.
     */
    protected String adoptFieldName (String fieldName)
    {
        String fieldNameLocal = fieldName; // variable for local assignments

        // call common method of super class:
        fieldNameLocal = super.adoptFieldName (fieldNameLocal);

        // if set add the numInList value:
        if (this.p_numInList >= 0)      // numInList value set?
        {
            // add it to the field name:
            fieldNameLocal += "_" + this.p_numInList;
        } // if numInList value set

        // return the field name:
        return fieldNameLocal;
    } // adoptFieldName


    /**************************************************************************
     * This method takes an argument name and possibly adopts it for the actual
     * object. <BR/>
     *
     * @param   argName The name of the argument.
     *
     * @return  The possibly adopted argument name.
     */
    public String adoptArgName (String argName)
    {
        String argNameLocal = argName; // variable for local assignments

        // call common method of super class:
        argNameLocal = super.adoptArgName (argNameLocal);

        // if set add the numInList value:
        if (this.p_numInList >= 0)      // numInList value set?
        {
            // add it to the argument:
            argNameLocal += "_" + this.p_numInList;
        } // if numInList value set

        // return the argument:
        return argNameLocal;
    } // adoptArgName


    /**************************************************************************
     * This method sets the filesize in the dataelement from a given filepath.
     * If the file cannot be found -1 is set.
     *
     * @param   file    The absolute path to the file.
     */
/* KR 20051007 The file is deprecated, so this is not longer necessary
    private void setFilesize (String file)
    {
        long fileSize = -1;

        File f = new File (file);
        if (f.exists ())            // the file exists?
        {
            // get the filesize of the file
            fileSize = f.length ();

            // set the file size in the corresponding attribute:
            this.dataElement.changeValue (Version_01.ATTR_SIZE, "" + fileSize);
        } // if the file exists
    } // setFilesize
*/

    /**************************************************************************
     * Overrides the XMLViewer_01 method. Here the versioning is implemented. <BR/>
     *
     * @param   action  The action to get database access.
     *
     * @throws  DBError
     *          If the connetion to the database cannot be established
     *          the exception is thrown.
     */
    public void performChangeSpecificData (SQLAction action) throws DBError
    {
        if (this.dataElement != null && this.state == States.ST_CREATED)
                                        // dataelement set and state is created?
        {
            // fill in the version number:
            this.dataElement.changeValue (Version_01.ATTR_VERSION, "" +
                this.getNextVersionNumber ());

/* KR 20051007 The file is deprecated, so this is not longer necessary
            // set the file size if applicable:
            ValueDataElement value = this.dataElement.getValueElement (Version_01.ATTR_FILE);
            if (value != null && !value.value.length () == 0)
            {
                this.setFilesize (getPath () + value.value);
            } // if
*/
        } // if dataelement set and state is created

        super.performChangeSpecificData  (action);
    } // performChangeSpecificData


    /**************************************************************************
     * This method realises the versioning. It makes use of the counter class
     * to get the actual version number. <BR/>
     *
     * @return  The next version number.
     */
    public int getNextVersionNumber ()
    {
        Counter c = new Counter ();
        c.initObject (this.oid, this.user, this.env, this.sess, this.app);
        int nextVersion = -1;

        try
        {
            // get the next version number for the container
            nextVersion = c.getNext (this.containerId.toString ());

            // set the version number in the dataelement
            (this.dataElement.getValueElement (Version_01.ATTR_VERSION)).value =
                "" + nextVersion;

            // set the actual object to master:
            (this.dataElement.getValueElement (Version_01.ATTR_MASTER)).value =
                "true";

            // get the actual version container
            // this container is needed to find the old master. the attribute
            // must be set to false
            VersionContainer_01 cont = this.getVersionContainer ();

            if (cont != null)
            {
                // get the version object which was master
                Version_01 v = cont.getMasterVersion (this.getTableName ());

                // if container does not consist any element ignore it
                if (v != null)
                {
                    (v.dataElement.getValueElement (Version_01.ATTR_MASTER))
                        .value = "false";
                    v.performChangeData (Operations.OP_CHANGE);
                } // if master version exists
            } // if container not null
        } // try
        catch (NoAccessException e)
        {
            this.showNoAccessMessage (Operations.OP_CHANGE);
        } // catch
        catch (NameAlreadyGivenException e)
        {
            this.showNameAlreadyGivenMessage ();
        } // catch

        return nextVersion;
    } // getNextVersionNumber


    /**************************************************************************
     * The method retrieves the VersionContainer_01 where the actual object is
     * stored in. <BR/>
     *
     * @return  The version number where the object is stored in or
     *          <CODE>null</CODE> if not found.
     */
    protected VersionContainer_01 getVersionContainer ()
    {
        // get the container object and return the result:
        return (VersionContainer_01) BOHelpers.getObject (
            this.containerId, this.env, false, true, true);
    } // getVersionContainer


    /**************************************************************************
     * Get the version object, i.e. the copy of the major object which was
     * stored under this version. <BR/>
     *
     * @return  The version object or
     *          <CODE>null</CODE> if not found.
     */
    protected BusinessObject getVersionObject ()
    {
        OID versionObjOid = null;
        BusinessObject versionObj = null;

        // check if the version object is already known:
        if (this.p_versionObj != null)
        {
            // use the stored object:
            versionObj = this.p_versionObj;
        } // if
        else
        {
            // create instance of the version object:
            // get the oid of the version object:
            if ((versionObjOid = this.getVersionObjectOid ()) != null)
            {
                // get the version object:
                versionObj = BOHelpers.getObject (
                    versionObjOid, this.env, false, false, false);

                // store the version object:
                this.p_versionObj = versionObj;
            } // if
        } // else

        // return the result:
        return versionObj;
    } // getVersionObject


    /**************************************************************************
     * Get the oid of the version object which is stored within this version.
     * <BR/>
     *
     * @return  The oid of the version object or
     *          <CODE>null</CODE> if not found.
     */
    protected OID getVersionObjectOid ()
    {
        OID versionObjOid = null;
        SQLAction action = this.getDBConnection ();
        StringBuffer query = null;

        // create the query which looks for the version object:
        // the version object is the only object within this version
        // which is no tab
        query = new StringBuffer ()
            .append ("SELECT o.oid")
            .append (" FROM ibs_object o ")
            .append (" WHERE o.containerId = ").append (this.oid.toStringQu ())
                .append (" AND o.state = ").append (States.ST_ACTIVE)
                .append (" AND o.containerKind = ")
                    .append (BOConstants.CONT_STANDARD);

        try
        {
            // get the number of datasets:
            int rowCount = action.execute (query, false);

            // not empty result set ?
            if (rowCount > 0)
            {
                // get tuples out of db
                // add the name and the oid of the document template
                versionObjOid = SQLHelpers.getQuOidValue (action, "oid");
            } // if tuples found

            // end transaction
            action.end ();
        } // try
        catch (DBError error)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_CLASSNOTFOUND, this.env),
                    this.app, this.sess, this.env);
        } // catch
        finally
        {
            // close db connection in every case:
            this.releaseDBConnection (action);
        } // finally

        // return the result:
        return versionObjOid;
    } // getVersionObjectOid


    /**************************************************************************
     * Get the major object, where this version belongs to. <BR/>
     *
     * @return  The major object or
     *          <CODE>null</CODE> if not found.
     */
    protected BusinessObject getMajorObject ()
    {
        OID majorObjOid = null;
        BusinessObject majorObj = null;

        // create instance of the major object:
        // get the oid of the major object:
        if ((majorObjOid = this.getMajorObjectOid ()) != null)
        {
            // get the version object:
            majorObj = BOHelpers.getObject (
                majorObjOid, this.env, false, false, false);
        } // if

        // return the result:
        return majorObj;
    } // getMajorObject


    /**************************************************************************
     * Get the oid of the major object where this version belongs to. <BR/>
     *
     * @return  The oid of the major object or
     *          <CODE>null</CODE> if not found.
     */
    protected OID getMajorObjectOid ()
    {
        OID majorObjOid = null;

        // check if the oid is already known:
        if (this.p_majorObjOid != null)
        {
            // use the stored oid:
            majorObjOid = this.p_majorObjOid;
        } // if
        else
        {
            try
            {
                // get the oid of the major object:
                // the major object is the object which is two levels above the
                // actual object
                if ((majorObjOid = this
                    .performRetrieveUpperData (this.containerId)) != null)
                {
                    // store the major oid:
                    this.p_majorObjOid = majorObjOid;
                } // if
            } // try
            catch (NoAccessException e)
            {
                // should not occur, display corresponding message:
                this.showNoAccessPopupMessage (Operations.OP_READ);
            } // catch
            catch (ObjectNotFoundException e)
            {
                // should not occur, display corresponding message:
                this.showObjectNotFoundMessage ();
            } // catch
        } // else

        // return the result:
        return majorObjOid;
    } // getMajorObjectOid


    /**************************************************************************
     * Gets the tablename out of the actual object from the document template.
     * <BR/>
     *
     * @return  The table name.
     */
    public String getTableName ()
    {
        // get the table name out of the document template:
        return this.getDocumentTemplate ().getMappingTableName ();
    } // getTableName


    /**************************************************************************
     * This method saves the edited data first and checks in object which
     * was checked out before. Therefore it finds the object which was checked
     * out before from container. Furthermore it sets the master property to
     * false. <BR/>
     *
     * @return  The object which was checked in is returned or
     *          <CODE>null</CODE> if the check failed because no object was
     *          found.
     *
     * @throws  NoAccessException
     *          The user does not have access to the object.
     */
    public BusinessObject checkIn () throws NoAccessException
    {
        BusinessObject obj = this;
        FileVersion_01 majorObj = (FileVersion_01) this.getMajorObject ();

        if (this.checkedOut)
        {
            // call common method:
            obj = super.checkIn ();
        } // if

        // check in the major object:
        if (majorObj != null)
        {
            majorObj.checkInFromVersion (this);
        } // if

        // return the result:
        return obj;
    } // checkIn


    /**************************************************************************
     * Checks out a Businessoject (freezes it for other users). <BR/>
     *
     * @return  The BusinessObject which was checked out.
     *
     * @throws  NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    public BusinessObject checkOut () throws NoAccessException
    {
        // we redirect the standard checkOut to the WebDav checkout
        return this.webdavCheckOut ();
    } // checkOut


    /**************************************************************************
     * The method checks if the actual object is the master of the container
     * or not (information out of the dataelement). <BR/>
     *
     * @return  <CODE>true</CODE> if it is the master, else <CODE>false</CODE>.
     */
    public boolean isMasterVersion ()
    {
        // return the boolean value isMaster out of the data element
        ValueDataElement val = this.dataElement.getValueElement (Version_01.ATTR_MASTER);

        if (val != null)
        {
            return new Boolean (val.value).booleanValue ();
        } // if

        return true;
    } // isMasterVersion


    /**************************************************************************
     * The method checks if the actual object has already a versioned copy of
     * the major object where it belongs to. <BR/>
     *
     * @return  <CODE>true</CODE> if it has already a versioned copy,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean hasVersionObject ()
    {
        // try to get the oid of the version object and return if found:
        return this.getVersionObjectOid () != null;
    } // hasVersionObject


    /**************************************************************************
     * The method returns the version of the actual object. <BR/>
     *
     * @return  The version if it is found in the dataelement or
     *          <CODE>-1</CODE> if it is not found.
     */
    public int getVersion ()
    {
        // return the version number out of the data element:
        return this.dataElement.getImportIntValue (Version_01.ATTR_VERSION);
    } // getVersion


    /**************************************************************************
     * The method sets the version of the actual object. <BR/>
     *
     * @param   value   The new version number.
     */
    public void setVersion (int value)
    {
        // set the version number in the data element:
        this.dataElement.changeValue (Version_01.ATTR_VERSION, "" + value);
    } // setVersion


    /**************************************************************************
     * The method sets the filename of the actual object. <BR/>
     *
     * @param   filename    The new filename.
     */
    public void setFilename (String filename)
    {
        // set the file name in the data element:
        this.dataElement.changeValue (Version_01.ATTR_FILE, filename);
    } // setFilename


    /**************************************************************************
     * The method returns the version of the actual object. <BR/>
     *
     * @param   value   The new version number.
     */
    public void setMaster (boolean value)
    {
        // set the boolean value isMaster in the data element:
        this.dataElement.changeValue (Version_01.ATTR_MASTER, "" + value);
    } // setVersion


    /**************************************************************************
     * The method returns the filename of the actual version. <BR/>
     *
     * @return  The filename.
     */
    public String getFilename ()
    {
        // return the file name out of the data element:
        return this.dataElement.getValueElement (Version_01.ATTR_FILE).value;
    } // getFilename


    /**************************************************************************
     * This method retrieves the filesize for the file in question. <BR/>
     *
     * @return  The filesize in bytes if the filesize is stored in the
     *          data element,
     *          <CODE>-1</CODE> if the entry is not found.
     */
    public int getFilesize ()
    {
        // return the file size out of the data element:
        return this.dataElement.getImportIntValue (Version_01.ATTR_SIZE);
    } // getVersion


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in an
     * object's info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     *
     */
    protected int[] setInfoButtons ()
    {
        int[] buttons = new int[2];

        // define the buttons to be displayed:
        if (this.isMasterVersion ())
        {
/* KR 20050930 don't display checkout and checkin buttons within version
 *             and version container
            if (this.checkedOut)
            {
                OID checkedOutOid = null;
                int userCheckOutId = -1;
                VersionContainer_01 vCont = getVersionContainer ();
                Vector checkOutInfo = null;

                try
                {
                    checkOutInfo = vCont.getCheckedOutInfo ();
                } // try
                catch (NoAccessException e)
                {
                    checkOutInfo = null;
                } // catch

                if (checkOutInfo != null)
                {
                    checkedOutOid = (OID) checkOutInfo.elementAt (0);
                    userCheckOutId = ((Integer) checkOutInfo.elementAt (1)).intValue ();
                } // if
                // only the 'checkin' button is allowed
                // but the button realizes the editing first so
                // that a new version is created
                if (this.oid.equals (checkedOutOid) &&
                    this.user.id == userCheckOutId)
                {
                    buttons[0] = Buttons.BTN_EDITBEFORECHECKIN;
                } // if user allowed to see the button
            } // if checked out
            else                        // is checked in
            {
*/
                // only the delete and checkout buttons are allowed
            buttons[0] = Buttons.BTN_DELETE;
/* KR 20050930 don't display checkout and checkin buttons within version
 *             and version container
                buttons[1] = Buttons.BTN_CHECKOUT;
            } // else checked in
*/
        } // if
        // is not master, object can be deleted only.
        else
        {
            buttons[0] = Buttons.BTN_DELETE;
        } // else

        // return an empty array (no button to display)
        return buttons;
    } // setInfoButtons


    /**************************************************************************
     * Delete the object. If it is the master then the method
     * updateMasterVersion from the container is called to set the new master. <BR/>
     *
     * @param   representationForm  Kind of representation.
     *
     * @return  <CODE>true</CODE> if operation performed properly,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean delete (int representationForm)
    {
        boolean retVal = super.delete (representationForm);
        // get the container
        VersionContainer_01 cont = this.getVersionContainer ();

        // update the master in the container
        if (retVal)
        {
            cont.updateMasterVersion (this.getTableName ());
        } // if

        return retVal;
    } // delete


    /**************************************************************************
     * Gets the typecode for this object. <BR/>
     *
     * @return  A string representing the typecode.
     */
    public String getTypecodex ()
    {
        return Version_01.TYPECODE;
    } // getTypecode


    /**************************************************************************
     * Get the argument name for the file name. <BR/>
     *
     * @return  A string representing the argument name.
     */
    public String getFilenameArg ()
    {
        return this.adoptArgName (this.createArgument (Version_01.ATTR_FILE));
    } // getFilenameArg


    /**************************************************************************
     * Set the specific properties for tabview version. <BR/>
     *
     * @param   majorObject The major object of this view tab.
     */
    public void setSpecificProperties (BusinessObject majorObject)
    {
        BusinessObject versionObj = null;

        // get the common tab data:
        Tab tabView = this.getTabCache ().get (this.p_tabId);

        // call common method:
        super.setSpecificProperties (majorObject);

        // get version object and display it as tab:
        versionObj = ((Version_01) majorObject).getVersionObject ();
        versionObj.p_tabs = this.getTabBar ();
        versionObj.p_tabs.setActiveTab (tabView);
        versionObj.setIsTab (true);
        versionObj.displayTabs = true;
        versionObj.show (UtilConstants.REP_STANDARD);
        this.showAllowed = false;
    } // setSpecificProperties


    /**************************************************************************
     * Show the object, i.e. its content. <BR/>
     * This method checks whether the object shall be displayed or not.
     * If not the method does nothing.
     *
     * @param   representationForm  Kind of representation.
     */
    public void show (int representationForm)
    {
        // check whether it is allowed to show the object:
        if (this.showAllowed)
        {
            // call common method:
            super.show (representationForm);
        } // if
        else
        {
            // reset the value:
            this.showAllowed = true;
        } // else
    } // show

} // class Version_01

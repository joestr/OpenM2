/*
 * FilePVersion_01.java
 */

// package:
package m2.version.publish;

// imports:
import ibs.bo.BOMessages;
import ibs.bo.Buttons;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.ObjectClassNotFoundException;
import ibs.bo.ObjectInitializeException;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.bo.States;
import ibs.bo.type.TypeNotFoundException;
import ibs.di.DIConstants;
import ibs.di.XMLViewer_01;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.file.FileHelpers;

import m2.version.FileVersion_01;
import m2.version.publish.FilePublication_01;
import m2.version.publish.PublicationMessages;
import m2.version.publish.PVersion_01;
import m2.version.publish.converter.ConversionException;
import m2.version.publish.converter.IConverter;

import java.io.File;
import java.util.Vector;

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
 * @version     $Id: FilePVersion_01.java,v 1.16 2010/04/07 13:37:12 rburgermann Exp $
 *
 * @author      Bernd Martin (BM), 011115
 ******************************************************************************
 */
public class FilePVersion_01 extends FileVersion_01
{
    /**
     * The attribute for the converter class. <BR/>
     */
    private static final String ATTR_CONVERTER = "Konverter";

    /**
     * The attribute for the info where the object will be published. <BR/>
     */
    private static final String ATTR_PUBLISHIN = "publiziere in";

    /**
     * The attribute for the info where the object will be published. <BR/>
     */
    private static final String ATTR_POBJECT = "PObjekt";

    /**
     * The OID of the object which where the objects should be published. <BR/>
     */
    private OID p_publishToContainerOid = null;

    /**
     * The name of the object which where the objects should be published. <BR/>
     */
    private String p_publishToContainerName = null;

    /**
     * The OID of the object which where the objects should be published. <BR/>
     */
    private OID p_publishedObjectOid = null;

    /**
     * The name of the object which where the objects should be published. <BR/>
     */
    private String p_publishedObjectName = null;


    /**************************************************************************
     * This constructor creates a new instance of the class FileVersion_01. <BR/>
     */
    public FilePVersion_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // Version_01


    /**************************************************************************
     * This constructor creates a new instance of the class FileVersion_01. <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public FilePVersion_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // FileVersion_01


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

        if (doc != null && viewMode == VIEWMODE_EDIT &&
            this.state == States.ST_CREATED)
        {
            for (int i = 0; i < nodeList.getLength (); i++)
            {
                map = nodeList.item (i).getAttributes ();
                attrNode = map.getNamedItem (DIConstants.ATTR_FIELD);

                if (attrNode.getNodeValue ().equalsIgnoreCase (
                    FilePVersion_01.ATTR_CONVERTER) ||
                    attrNode.getNodeValue ().equalsIgnoreCase (
                        FilePVersion_01.ATTR_POBJECT))
                {
                    try
                    {
                        // remove the node
                        nodeList.item (i).getParentNode ().removeChild (nodeList.item (i));
                        i--;
                    } // try
                    catch (DOMException e)
                    {
                        this.trace ("exception occurred: " + e.getMessage ());
                    } // catch
                } // else if converter attribute found
            } // for
        } // if
        else if (doc != null && viewMode == VIEWMODE_EDIT &&
            this.state != States.ST_CREATED)
        {
            for (int i = 0; i < nodeList.getLength (); i++)
            {
                String attrNodeValue = "";
                map = nodeList.item (i).getAttributes ();
                attrNode = map.getNamedItem (DIConstants.ATTR_FIELD);
                attrNodeValue =  attrNode.getNodeValue ();

                // check if attribute is ismaster, size or version
                if (attrNodeValue.equalsIgnoreCase (FilePVersion_01.ATTR_PUBLISHIN))
                {
                    // check if node is set already, if so do not display it
                    // for editing anymore

                    // value publish In was set
                    if (this.getPublishToContainerOid () != null)
                    {
                        try
                        {
                            // remove the node
                            nodeList.item (i).getParentNode ().removeChild (
                                nodeList.item (i));
                            i--;
                        } // try
                        catch (DOMException e)
                        {
                            this.trace ("exception occurred:  " + e.getMessage ());
                        } // catch
                    } // if node is set already
                    else
                    {
                        this.setPublishToContainerNameAndOid (null, null);
                    } // else
                } // if publish in attribute
                else if (attrNode.getNodeValue ().equalsIgnoreCase (
                    FilePVersion_01.ATTR_CONVERTER) ||
                    attrNode.getNodeValue ().equalsIgnoreCase (
                        FilePVersion_01.ATTR_POBJECT))
                {
                    try
                    {
                        // remove the node
                        nodeList.item (i).getParentNode ().removeChild (nodeList.item (i));
                        i--;
                    } // try
                    catch (DOMException e)
                    {
                        this.trace ("exception occurred:   " + e.getMessage ());
                    } // catch
                } // else if converter attribute found
            } // for
        } // if mode is edit
        else if (doc != null && viewMode == XMLViewer_01.VIEWMODE_SHOW)
        {
            for (int i = 0; i < nodeList.getLength (); i++)
            {
                map = nodeList.item (i).getAttributes ();
                attrNode = map.getNamedItem (DIConstants.ATTR_FIELD);

                // check if attribute is ismaster, size or version
                if (attrNode.getNodeValue ().equalsIgnoreCase (FilePVersion_01.ATTR_CONVERTER) ||
                    attrNode.getNodeValue ().equalsIgnoreCase (FilePVersion_01.ATTR_POBJECT))
                {
                    try
                    {
                        // remove the node
                        nodeList.item (i).getParentNode ().removeChild (nodeList.item (i));
                        i--;
                    } // try
                    catch (DOMException e)
                    {
                        this.trace ("trace ex occurred: " + e.getMessage ());
                    } // catch
                } // if
            } // for
        } // if mode is edit

        return doc;
    } // createDomTree


    /**************************************************************************
     * This method sets the value for the oid and name of the container where
     * the published object resides in the local properties but also in the
     * dataelement. <BR/>
     *
     * @param   oid     The oid of the target container.
     * @param   name    The name of the target container.
     */
    private void setPublishToContainerNameAndOid (OID oid, String name)
    {
        if (this.dataElement != null)
        {
            // no oid given
            if (oid == null || !this.isObjectActive (oid))
            {
                // set the value from the datalement to null
                (this.dataElement
                    .getValueElement (FilePVersion_01.ATTR_PUBLISHIN)).value = null;
                // reset the local properties
                this.p_publishToContainerOid  = null;
                this.p_publishToContainerName = null;
            } // if
            else // oid was given
            {
                // set the value from the datalement
                (this.dataElement
                    .getValueElement (FilePVersion_01.ATTR_PUBLISHIN)).value =
                    oid.toString () + "," + name;
                // set the local properties
                this.p_publishToContainerOid  = oid;
                this.p_publishToContainerName = name;
            } // else
        } // if dataelement not null
    } // setPublishToContainerNameAndOid


    /**************************************************************************
     * This method sets the value for the oid and name of the container where
     * the published object resides. <BR/>
     */
    private void setPublishToContainerNameAndOid ()
    {
        String val = null;

        if (this.dataElement != null)
        {
            // get the value from the data element
            val = this.dataElement
                .getValueElement (FilePVersion_01.ATTR_PUBLISHIN).value;
        } // if data element not null

        // if value returned is not null and valid
        if (val != null &&
            val.trim ().length () > 0 &&
            !val.trim ().equals (","))
        {
            // return the value publishedIn value of the data element
            String tmpOidString = val.substring (0, val.indexOf (','));
            try
            {
                OID tmpOid = new OID (tmpOidString);

                if (this.isObjectActive (tmpOid))
                {
                    this.p_publishToContainerOid  = tmpOid;
                    this.p_publishToContainerName = val.substring (val.indexOf (',') + 1);
                } // if oid still valid
                else
                {
                    (this.dataElement
                        .getValueElement (FilePVersion_01.ATTR_PUBLISHIN)).value = null;
                } // else
            } // try
            catch (IncorrectOidException e)
            {
                this.showIncorrectOidMessage (tmpOidString);
            } // catch
        } // if
    } // setPublishToContainerNameAndOid


    /**************************************************************************
     * This method sets the value for the oid and name of the object where
     * which represents the published object. <BR/>
     */
    private void setPublishedFileNameAndOid ()
    {
        String val = null;

        if (this.dataElement != null)
        {
            // get the value from the datalement
            val = this.dataElement.getValueElement (FilePVersion_01.ATTR_POBJECT).value;
        } // if dataelement not null

        // if value returned is notn ull and valid
        if (val != null &&
            val.trim ().length () > 0 &&
            !val.trim ().equals (","))
        {
            String tmpOid = val.substring (0, val.indexOf (','));
            try
            {
                // check if object still exists
                OID tempOid = new OID (tmpOid);

                if (this.isObjectActive (tempOid))
                {
                    // if exits set values
                    this.p_publishedObjectOid  = tempOid;
                    this.p_publishedObjectName = val.substring (val.indexOf (',') + 1);
                } // if object still exists
            } // try
            catch (IncorrectOidException e)
            {
                this.showIncorrectOidMessage (tmpOid);
            } // catch
        } // if
    } // setPublishToContainerNameAndOid


    /**************************************************************************
     * This method retrieves the object name where the published versions
     * should be. <BR/>
     *
     * @return  The name of the container which contains the published objects
     *          or null if not available.
     */
    public String getPublishToContainerName ()
    {
        if (this.p_publishToContainerName == null)
        {
            // set the values
            this.setPublishToContainerNameAndOid ();
        } // if

        // nothing found
        return this.p_publishToContainerName;
    } // getPublishToContainerName


    /**************************************************************************
     * This method retrieves the object oid where the published versions
     * should be. <BR/>
     *
     * @return  The OID of the container which contains the published objects
     *          or <CODE>null</CODE> if not available.
     */
    public OID getPublishToContainerOid ()
    {
        if (this.p_publishToContainerOid == null)
        {
            // set the values
            this.setPublishToContainerNameAndOid ();
        } // if
        // nothing found
        return this.p_publishToContainerOid;
    } // getPublishToContainerOid


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

        PVersion_01 master = (PVersion_01) this.getMasterVersion (null, false);
        String publishIn = this.getPublishToContainerName ();

        // if any component (master or destination container is null)
        // then publishing is not available
        boolean masterInfoNull = (master == null) || (publishIn == null);


        // the destinaton container is an active object
        boolean destinationContainerActive = !masterInfoNull &&
            this.isObjectActive (this.getPublishToContainerOid ());

        // the master version was published already
        boolean masterIsPublished = destinationContainerActive &&
            master.isPublished ();

        // master is published and is active
        boolean masterPublishedAndActive = masterIsPublished && this
            .isObjectActive (master.getPublishedObjectOid ());

        // master is checked out
        boolean masterIsCheckedOut = !masterInfoNull && master.checkedOut;

        // if the master is not published and active in an active container
        if (!masterInfoNull && !masterPublishedAndActive && !masterIsCheckedOut)
        {
            // define a new button array
            int[] newButtons = new int[buttons.length + 1];

            // copy the button array
            for (int i = 0; i < buttons.length; i++)
            {
                newButtons[i] = buttons[i];
            } // for

            // set the button for publish
            newButtons[buttons.length] = Buttons.BTN_PUBLISH;
            buttons = newButtons;
        } // if master set

        // return an empty array (no button to display)
        return buttons;
    } // setInfoButtons

    /**************************************************************************
     * The method returns the oid of the published object or null if not
     * published or not active anymore. <BR/>
     *
     * @return  The oid of the published object or null if not active or
     *          published.
     */
    public OID getPublishedObjectOid ()
    {
        if (this.p_publishedObjectOid == null)
        {
            this.setPublishedFileNameAndOid ();
        } // if not set yet
        return this.p_publishedObjectOid;
    } // getPublishedFile


    /**************************************************************************
     * Sets the published file values in the dataelement and the local
     * properties. <BR/>
     *
     * @param   oid     The oid of the published object.
     * @param   name    The name for the published object.
     */
    private void setPublishedObject (OID oid, String name)
    {
        // set the fieldref in the dataelement
        (this.dataElement.getValueElement (FilePVersion_01.ATTR_POBJECT)).value =
            oid + "," + name;
        // set the attributes of the published file within this object
        this.p_publishedObjectName = name;
        this.p_publishedObjectOid = oid;
    } // setPublishedObject


    /**************************************************************************
     * Gets the tablename out of the actual object from the document template.
     * <BR/>
     *
     * @return  The table name.
     */
    protected String getTableName ()
    {
        return this.getDocumentTemplate ().getMappingTableName ();
    } // getTableName


    /**************************************************************************
     * This method is call from the published object after the import of the
     * transformation process to tell the 'original' object the oid of the
     * associated published object.
     *
     * @param   oid     The oid of the published object.
     */
    public void setPublishedObjectOid (OID oid)
    {
        try
        {
            // set the oid in the value field
            // at this point the object name is ignored and set to an empty string
            this.setPublishedObject (oid, "");
            // save the changes
            this.performChangeData (Operations.OP_CHANGE);
        } // try
        catch (NoAccessException e)
        {
            this.showNoAccessMessage (Operations.OP_CHANGE);
        } // catch
        catch (NameAlreadyGivenException e)
        {
            this.showNameAlreadyGivenMessage ();
        } // catch
    } // setPublishedObjectOid


    /**************************************************************************
     * Sets the published object data into the local properties and the data
     * element store the changed in the database. <BR/>
     */
    private void setPublishedObjectData ()
    {
        try
        {
            // retrieve the object datas again to reflect changes performed
            // by the method setPublishedObjectOid ().
            this.performRetrieveData (Operations.OP_READ);

            // now load the published object
            FilePublication_01 p = this.getFilePublishedObject ();
            // publication object found
            if (p != null)
            {
                this.setPublishedObject (p.oid, p.name);
                // store the changes in the database
                this.performChangeData (Operations.OP_CHANGE);
            } // publication found
        } // try
        catch (NoAccessException e)
        {
            this.showNoAccessMessage (Operations.OP_READ);
        } // catch
        catch (NameAlreadyGivenException e)
        {
            this.showNameAlreadyGivenMessage ();
        } // catch
        catch (ObjectNotFoundException e)
        {
            this.showObjectNotFoundMessage ();
        } // catch
    } // setPublishedObjectData


    /**************************************************************************
     * The method sets the object ref field for the actual object where the
     * published object resides then. <BR/>
     *
     * @return  The name of the published object.
     */
    public String getPublishedObjectName ()
    {
        if (this.p_publishedObjectName == null)
        {
            this.setPublishedFileNameAndOid ();
        } // if not set yet

        return this.p_publishedObjectName;
    } // getPublishedFile


    /**************************************************************************
     * To publish the object. <BR/>
     *
     * @return  <CODE>true</CODE> if the operation was successful,
     *          <CODE>false</CODE> otherwise.
     *
     * @throws  NoAccessException
     *          The user does not have the necessary rights.
     */
    public boolean publish () throws NoAccessException
    {
        OID publishedToContainerOid = this.getPublishToContainerOid ();

        ////////////////////////////////////////////////////////////////////////
        // COPYING MOTHER FILE: PFILEVERISON
        // if container to publish is available and and mother object
        // exists already
        if (publishedToContainerOid == null)
        {
            this.showPopupMessage (StringHelpers.replace (
                PublicationMessages.MSG_PUBLICATIONCONTAINERNOTFOUND,
                UtilConstants.TAG_OID, "" + publishedToContainerOid));
            return false;
        } // if
        else if (!this.isObjectActive (this.getPublishedObjectOid ()))
        {
            File tmpDir = this.getTempDir (this.oid.toString ());
            String transformationFile = this.getTransFilePVersionFile ();

            // create new FilePublication Object
            // make transformation
            String error = this.executeTransformation (publishedToContainerOid,
                                   transformationFile, this, tmpDir);

            // check if errors occurred
            // in the transformation process
            if (error != null)
            {
                IOHelpers.showMessage (
                    StringHelpers.replace (
                        StringHelpers.replace (PublicationMessages.MSG_TRANSFORMATIONERROR,
                                         UtilConstants.TAG_NAME, error),
                        UtilConstants.TAG_NAME2, transformationFile),
                    this.app, this.sess, this.env);
                return false;
            } // if error occurred

            // if the publishing process was successfully the new created
            // publication object calls the method setPublishedObjectOid ()
            // of the original object to set the oid of the new created
            // object. To complete the information about the referring
            // published object call the setPublishedObjectData ()
            // of the orignal object now!!

            // set referring document datas
            this.setPublishedObjectData ();
        } // if destination container set

        ////////////////////////////////////////////////////////////////////////
        // COPYING VERSION FILE
        // get master object
        PVersion_01 master = (PVersion_01) this.getMasterVersion (null, false);

        // make file conversion
        // save old filename for restoring
        String originalFilename = master.getFilename ();

        // convert files
        File convertedFile = this.makeFileConverion (master);

        // if file coud be converted and exists
        if (convertedFile != null && convertedFile.exists ())
        {

            // get the version container oid of the publication object
            OID versionContainerOid = this
                .getVersionContainerOidForPublicationObject (this
                    .getPublishedObjectOid ());

            // versionContainer not found (should not occur)
            if (versionContainerOid == null)
            {
                return false;
            } // if

            String convertedFilename = convertedFile.getName ();

            // get a temporary working directory to prevent overwriting of
            // files with the same name
            File tmpDir = this.getTempDir (versionContainerOid.toString ());

            // if the converted file is the same as the original then the
            // actual file is copied only.
            if (convertedFilename.equals (originalFilename))
            {
                // move the file to the temporary directory for importing from there
                FileHelpers.copyFile (convertedFile.getAbsolutePath (),
                                      tmpDir.getAbsolutePath () + File.separator +
                                      convertedFilename);
            } // if same filename
            else
            {
                // move the file to the temporary directory for importing from there
                FileHelpers.moveFile (convertedFile.getAbsolutePath (),
                                      tmpDir.getAbsolutePath () + File.separator +
                                      convertedFilename);
            } // else
            // master version set new filename
            master.setFilename (convertedFilename);

///////////////////////////////
// BM NOT TO BE DONE NOW BEGIN
// these lines realise the copying of some customized attributes of type file
// this must be done because the import needs those files otherwise they will
// not be published.
/*
            // check if pversion has attributes of type file, then the files
            // must be copied to the temporary directory as well.
            if (master.dataElement != null && master.dataElement.values != null)
            {
                Enumeration values = master.dataElement.values.elements ();
                ValueDataElement vde = null;

                while (values.hasMoreElements ())
                {
                    vde = (ValueDataElement) values.nextElement ();

                    if (vde.type == DIConstants.VTYPE_FILE)
                    {
//debug.DebugClient.debugln ("FileTYPE FOUND: value = " + vde.value);
                        if (vde.value != null)
                        {
                            File f = new File (vde.value);

                            FileHelpers.copyFile (vde.value,
                                tmpDir.getAbsolutePath () + File.separator + f.getName ());
                        } // if value of file set
                    } // if type file found
                } // while still elements available
            } // if dataelements found
*/
// BM NOT TO BE DONE NOW END
///////////////////////////////

            // get the xslt file for the translation
            String transformationFile = this.getTransPVersionFile ();

            // master version transform
            String error = this.executeTransformation (versionContainerOid,
                 transformationFile, master, tmpDir);

            // masterversion set old filename back
            master.setFilename (originalFilename);

            // if no error occurred
            if (error != null)
            {
                IOHelpers.showMessage (
                    StringHelpers.replace (
                        StringHelpers.replace (PublicationMessages.MSG_TRANSFORMATIONERROR,
                                         UtilConstants.TAG_NAME, error),
                        UtilConstants.TAG_NAME2, transformationFile),
                    this.app, this.sess, this.env);
                return false;
            } // if error occurred

            // if the publishing process was successfully the
            // publication object calls the method setPublishedObjectOid ()
            // of the master version object to set the oid of the new created
            // object. To complete the information about the referring
            // publication object call the setPublishedObjectData ()
            // of the master version now!!

            // masterversion set referring document
            master.setPublishedObjectData ();

            return true;
        } // if convertedFilename available

        return false;
    } // publish


    /**************************************************************************
     * Gets a temporary working directory for the transformation process. If
     * the key for the directory does identify an existing directory already
     * numbers are added at the end devided by a '_' character. If the directory
     * does not exist it is created. <BR/>
     *
     * @param   key     An identifier in the temp directory.
     *
     * @return  The file representing the temp directory.
     */
    protected File getTempDir (String key)
    {
        String tmpFilename = this.app.p_system.p_m2AbsBasePath +
                             DIConstants.PATH_TEMPROOT + key;

        File f = new File (tmpFilename);
        int i = 0;

        // loop until finding a directory which does not exist
        while (f.exists () && f.isDirectory ())
        {
            tmpFilename = this.app.p_system.p_m2AbsBasePath +
                             DIConstants.PATH_TEMPROOT + key + "_" + i;
            i++;
            f = new File (tmpFilename);
        } // while

        // create the directory that noone else can use it
        f.mkdir ();

        return f;
    } // getTempDir


    /**************************************************************************
     * Gets the file publication object. <BR/>
     *
     * @return  The published object.
     */
    protected FilePublication_01 getFilePublishedObject ()
    {
        OID pubOid = this.getPublishedObjectOid ();

        if (pubOid != null)
        {
            try
            {
                // return the object from the cache
                return (FilePublication_01) this.getObjectCache ().fetchObject
                    (pubOid, this.user, this.sess, this.env, false);
            } // try
            catch (ObjectNotFoundException e)
            {
                String pubOidString =
                    pubOid != null ? pubOid.toString () : "null";
                IOHelpers.showMessage (
                    StringHelpers.replace (
                        PublicationMessages.MSG_FILEPUBLICATIONOBJECTNOTFOUND,
                        UtilConstants.TAG_OID, pubOidString),
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
        } // if publishedObject available

        return null;
    } // getFilePublicationObject


    /**************************************************************************
     * This method gets the oid of the version tab of the published object.
     * <BR/>
     *
     * @param   objectOid   The oid of the FilePublicaton object where the tab
     *                      must be.
     *
     * @return  The oid of the required object.
     */
    protected OID getVersionContainerOidForPublicationObject (OID objectOid)
    {
        try
        {
            FilePublication_01 pub =
                (FilePublication_01) this.getObjectCache ().fetchObject
                    (objectOid, this.user, this.sess, this.env, false);
            return pub.getVersionContainerOid ();
        } // try
        catch (ObjectNotFoundException e)
        {
            IOHelpers.showMessage (
                "publication container not found!!!",
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

        return null;
    } // getVersionContainerOidForPublicationObject


    /***************************************************************************
     * This method executes the transformation of a given verison object. The
     * necessary xsl transformation file must be given and the oid as the
     * destination as well. <BR/>
     *
     * @param   oid         The destination container oid.
     * @param   xslFilename The xsl transformation file.
     * @param   version     The object on which to perform the transformation.
     * @param   tmpDir      A temporary directory where the execution takes
     *                      place. Make sure that this directory exists!
     *
     * @return  In case of an error the error string from the
     *          performTransformation method of the XMLViewer_01 class is
     *          returned, else <CODE>null</CODE>.
     */
    private String executeTransformation (OID oid, String xslFilename,
                                          XMLViewer_01 version, File tmpDir)
    {
        // add param to the param vector
        // first param OID of the container to publish to
        // second param name of the XSL file to be used
        Vector<String> v = new Vector<String> ();
        String retVal = null;
        v.addElement (oid.toString ());
        v.addElement (xslFilename);
        v = version.performTransformation (v, tmpDir.getAbsolutePath () +
            File.separator);

        // check for errors
        if (v != null &&
            !v.elementAt (0).equals ("0"))
        {
            retVal = v.elementAt (1);
        } // if error occurred

        // delete the temporary directory recursively
        FileHelpers.deleteDirRec (tmpDir.getAbsolutePath ());

        return retVal;
    } // executeTransformation


    /**************************************************************************
     * This method forces to make the fileconversion for the master. It retrieves
     * the converter class and converts the file. <BR/>
     *
     * @param   master  The publishable version object.
     *
     * @return  The converted file.
     */
    private File makeFileConverion (PVersion_01 master)
    {
        String converter = "";

        try
        {
            // get the class name for the converter
            converter = this.getConverterClass ();

            // get the version file name
            String versionFile = master.getFilePath (master.getFilename ());

            // get converter class
            @SuppressWarnings ("unchecked") // suppress compiler warning
            Class<? extends IConverter> converterClass =
                (Class<? extends IConverter>) Class.forName (converter);

            // initialize converter
            IConverter converterImpl = converterClass.newInstance ();

            // make conversion
            File originalFilename = new File (versionFile);
            return converterImpl.convertFile (originalFilename);
        } // try
        catch (ClassNotFoundException e)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_CLASSNOTFOUND, new String[] {converter}, env),
                this.app, this.sess, this.env);
        } // catch
        catch (InstantiationException e)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_INSTANTIATIONFAILED, new String[] {converter}, env),
                this.app, this.sess, this.env);
        } // catch
        catch (IllegalAccessException e)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_INSTANTIATIONFAILED, new String[] {converter}, env),
                this.app, this.sess, this.env);
        } // catch
        catch (ConversionException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch

        return null;
    } // makeFileConversion


    /**************************************************************************
     * Overwritten method. It sets the showAsFormFrameset to false when the
     * container is given already where the published objects should be stored
     * in. <BR/>
     * Then the super method is called.
     *
     * @param   representationForm  The representation form.
     * @param   function            The required function value.
     *                              (necessary for rights check)
     *
     * @return  The operation succeeded successfully.
     */
    public boolean showChangeForm (int representationForm, int function)
    {
        OID publishInOid = this.getPublishToContainerOid ();

        // if a destination container set already
        if (this.isObjectActive (publishInOid))
        {
            this.showChangeFormAsFrameset = false;
        } // if
        else
        {
            this.showChangeFormAsFrameset = true;
        } // else

        // return the super methods return value
        return super.showChangeForm (representationForm, function);
    } // showChangeForm


    /**************************************************************************
     * The method returns the converter class name given in this object. <BR/>
     *
     * @return  The name of the class (fully qualified name) which represents
     *          the implementation of the converter.
     */
    public String getConverterClass ()
    {
        // return the boolean value isMaster out of the dataelement
        if (this.dataElement.getValueElement (FilePVersion_01.ATTR_CONVERTER) == null)
        {
            return null;
        } // if
        return this.dataElement.getValueElement (FilePVersion_01.ATTR_CONVERTER).value;
    } // getPDate


    /**************************************************************************
     * The method checks if the object is active. An active object is an object
     * which state is set to active. <BR/>
     *
     * @param   oid     The oid of the object to check to.
     *
     * @return  <CODE>true</CODE> if the object is active,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean isObjectActive (OID oid)
    {
        // declare a variable for the state of the actual object
        int tmpState = States.ST_UNKNOWN;

        // if oid not null
        if (oid != null)
        {
            SQLAction action = this.getDBConnection ();
            StringBuffer query = new StringBuffer ();

            // create the query. the query should find the checked out object
            // from that container. it can be one at a time only because only the
            // master can be checked out
            query.append ("select state from ibs_object");
            query.append (" where oid = ").append (oid.toString ());

            try
            {
                // get the number of resultsets
                int rowCount = action.execute (query.toString (), false);
                // check if a dataset was found
                if (rowCount > 0)
                {
                    // get tuples out of db
                    // add the name and the oid of the document template
                    tmpState = action.getInt ("state");
                } // if
                action.end ();
            } // try
            catch (DBError error)
            {
                try
                {
                    action.end ();
                } // try
                catch (DBError e)
                {
                    // TODO: handle the exception
                } // catch
            } // catch
        } // if oid set

        return States.ST_ACTIVE == tmpState;
    } // isObjectActive


    /**************************************************************************
     * The method which returns the transformation file name which transforms
     * a PVersion object into a Publication object. <BR/>
     *
     * @return  The xsl filename.
     */
    public String getTransPVersionFile ()
    {
        return "TransPVersionToPublication.xsl";
    } // getTransPVersionFile


    /**************************************************************************
     * The method which returns the transformation file name which transforms
     * a FilePVersion object into a FilePublication object. <BR/>
     *
     * @return  The xsl filename.
     */
    public String getTransFilePVersionFile ()
    {
        return "TransFilePVersionToFilePublication.xsl";
    } //getTransPVersionFile

} // class FilePVersion_01

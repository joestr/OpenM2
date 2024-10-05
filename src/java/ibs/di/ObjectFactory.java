
/*
 * Class: ObjectFactory.java
 */

// package:
package ibs.di;

// imports:
//KR TODO: unsauber
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOMessages;
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.bo.States;
import ibs.bo.cache.ObjectPool;
import ibs.bo.type.Type;
import ibs.bo.type.TypeConstants;
import ibs.bo.type.TypeContainer;
import ibs.di.connect.ConnectionFailedException;
import ibs.di.connect.Connector_01;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;
import ibs.util.UtilConstants;
import ibs.util.file.FileHelpers;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * The ObjectFactory class handles all object operations that are
 * requested by an import. This can be creating, changing and deleting
 * objects. <BR/>
 *
 * @version     $Id: ObjectFactory.java,v 1.90 2011/05/24 14:02:12 rburgermann Exp $
 *
 * @author      Buchegger Bernd (BB), 991008
 ******************************************************************************
 */
public class ObjectFactory extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ObjectFactory.java,v 1.90 2011/05/24 14:02:12 rburgermann Exp $";


    /**
     * Absolute base path of the m2 system. <BR/>
     */
    protected String m2AbsBasePath = "";

    /**
     * The actual connector in use. <BR/>
     * The connector will be set through the importIntegrator and is
     * necessary in order to read files from the connector. <BR/>
     */
    public Connector_01 connector;

    /**
     * A vector that holds all names of the files that could have been
     * successfully imported. <BR/>
     */
    private Vector<String> p_importedFiles = new Vector<String> ();

    /**
     *  The log object that will hold the log entries
     */
    private Log_01 p_log = null;

    /**
     * the keymapper object. <BR/>
     */
    private KeyMapper p_keyMapper = null;

    /**
     * flag to enable objectname and objecttype for keymapping. <BR/>
     */
    private boolean p_isNameTypeMapping = false;


    /**************************************************************************
     * Creates an ObjectFactory Object. <BR/>
     */
    public ObjectFactory ()
    {
        // call constructor of super class:
        super ();
    } // ObjectFactory


    /**************************************************************************
     * Creates an ObjectFactory Object. <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ObjectFactory (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // ObjectFactory


    /**************************************************************************
     * Set the objectname and type mapping option. <BR/>
     *
     * @param   isNameTypeMapping   The setting.
     */
    public void setIsNameTypeMapping (boolean isNameTypeMapping)
    {
        this.p_isNameTypeMapping = isNameTypeMapping;
    } // setIsNameTypeMapping


    /**************************************************************************
     * Set the log value. <BR/>
     *
     * @param   log     The value to be set.
     */
    public void setLog (Log_01 log)
    {
        this.p_log = log;
    } // setLog


    /**************************************************************************
     * Set the connector used to read and write files. <BR/>
     *
     * @param   connector   The connector value.
     */
    public void setConnector (Connector_01 connector)
    {
        // set the connector
        this.connector = connector;
    } // setConnector


    /**************************************************************************
     * Sets the m2AbsbasePath. This is the absolute file path to the m2
     * system directories and is stored the in session. <BR/>
     *
     * @param   m2AbsBasePath   The m2AbsBasePath.
     */
    public void setM2AbsBasePath (String m2AbsBasePath)
    {
        this.m2AbsBasePath = m2AbsBasePath;
    } // setM2AbsBasePath

    
    /**************************************************************************
     * Process an object according their given operation. <BR/>
     * If a not compatible operation is provided for the DataElement a log 
     * entry is written and an according operation is used instead of the 
     * provided one. <BR/>
     * Currently this method supports the operations NEW, CHANGE and DELETE. <BR/>
     * Note that this method calls the following methods: <BR/>
     *  #createObject(DataElement, OID) <BR/>
     *  #changeAndGetObject(DataElement) <BR/>
     *  #deleteObject(DataElement) <BR/>
     *
     * @param   dataElement     The DataElement to extract the data from
     * @param   containerOid    The container OID in which to create the object
     * @param   importOperation The given operation to proceed
     *
     * @return  Object[]        contains three entries with the return values <BR/> 
     *                          (1) boolean  <CODE>true</CODE> when operation 
     *                                       was successful, otherwise 
     *                                       <CODE>false</CODE> <BR/>
     *                          (2) String   The operation which was really
     *                                       processed <BR/>
     *                          (3) BusinessObject
     *                                       contains the current BusinessObject
     *                                       when NEW or CHANGE processed and
     *                                       <CODE>null</CODE> when DELETE 
     *                                       processed <BR/> 
     *
     * @see #createObject(DataElement, OID)
     * @see #changeAndGetObject(DataElement)
     * @see #deleteObject(DataElement)
     */
    public Object[] processObject (DataElement dataElement, OID containerOid,
                                   String importOperation)
    {
        Object[] resultArray = new Object[3];
        OID exists = null;
        boolean successful = false;
            
        if (DIConstants.OPERATION_NEW.equals(importOperation))
        {
            // check if the element already exists
            exists = this.getKeyMapper (dataElement.id, dataElement.idDomain);

            // no object exists yet?
            if (exists == null)
            {
                // set processed operation and create new object
                resultArray[1] = DIConstants.OPERATION_NEW;
                resultArray[2] = this.createObject (dataElement, containerOid);
            } // if
            else // object found
            {
                this.p_log.add (DIConstants.LOG_WARNING,
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE, 
                    DIMessages.ML_MSG_OPERATION_UPDATE_INSTEAD_OF_NEW, env));
                
                // set processed operation and update existing object
                resultArray[1] = DIConstants.OPERATION_CHANGE;
                resultArray[2] = this.changeAndGetObject (dataElement);
            } // else

            // set indicator if operation was successful or not
            successful = (resultArray[2] != null) ? true : false;
        } // if
        else if (DIConstants.OPERATION_CHANGE.equals(importOperation))
        {
            // check if the element already exists
            exists = this.getKeyMapper (dataElement.id, dataElement.idDomain);

            // no object exists yet?
            if (exists == null)
            {
                this.p_log.add (DIConstants.LOG_WARNING,
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE, 
                    DIMessages.ML_MSG_OPERATION_NEW_INSTEAD_OF_UPDATE, env));

                // set processed operation and create new object
                resultArray[1] = DIConstants.OPERATION_NEW;
                resultArray[2]= this.createObject (dataElement, containerOid);
            } // if
            else // object found
            {
                // set processed operation and update existing object
                resultArray[1] = DIConstants.OPERATION_CHANGE;
                resultArray[2] = this.changeAndGetObject (dataElement);
            } // else

            // set indicator if operation was successful or not
            successful = (resultArray[2] != null) ? true : false;
        } // else if
        else if (DIConstants.OPERATION_DELETE.equals (importOperation))
        {
            // set processed operation and try to delete object
            resultArray[1] = DIConstants.OPERATION_DELETE;
            successful = this.deleteObject (dataElement);

            // was delete not successful?
            if (!successful)
            {
                this.p_log.add (DIConstants.LOG_WARNING,
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE, 
                    DIMessages.ML_MSG_OPERATION_DELETE_OBJECT_NOT_FOUND, env));
            } // if
        } // else if
        else
        {
            this.p_log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE, 
                DIMessages.ML_MSG_UNKNOWN_OPERATION, env));
        } // else

        resultArray[0] = successful; 
        return resultArray;
    } // processObject

    
    /**************************************************************************
     * Creates a new object with the data from the DataElement. <BR/>
     * Note that this is a wrapper method for
     * #createObject(DataElement, OID, boolean, boolean). <BR/>
     *
     * @param   dataElement     The DataElement to extract the data from
     * @param   containerOid    The container OID in which to create the object
     * @param   isTabObject     <CODE>true</CODE> if the new object is a tab
     *                          object.
     *
     * @return  The object or
     *          <CODE>null</CODE> in case it could not have been created.
     *
     * @see #createObject(DataElement, OID, boolean, boolean)
     */
    public BusinessObject createObject (DataElement dataElement,
                                        OID containerOid,
                                        boolean isTabObject)
    {
        return this.createObject (dataElement, containerOid, isTabObject, false);
    } // createObject

    
    /**************************************************************************
     * Creates a new object with the data from the DataElement. <BR/>
     * Note that this is a wrapper method for
     * #createObject(DataElement, OID, boolean, boolean). <BR/>
     *
     * @param   dataElement     The DataElement to extract the data from
     * @param   containerOid    The container OID in which to create the object
     *
     * @return  The object or
     *          <CODE>null</CODE> in case it could not have been created.
     *
     * @see #createObject(DataElement, OID, boolean, boolean)
     */
    public BusinessObject createObject (DataElement dataElement,
                                        OID containerOid)
    {
        return this.createObject (dataElement, containerOid, false, false);
    } // createObject


    /**************************************************************************
     * Creates a new object with the data from the DataElement. <BR/>
     * Note that the structure of the new object is read from the its template
     * and only the data read from the dataElement that is available.
     * Thus the dataElement does not nesseccarily have all fields the object
     * supports. <BR/>
     *
     * @param   dataElement     The DataElement to extract the data from
     * @param   containerOid    The container OID in which to create the object
     * @param   isTabObject     <CODE>true</CODE> if the new object is a tab
     *                          object.
     * @param   isValidate      option to activate the structural check.
     *
     * @return  The object or
     *          <CODE>null</CODE> in case it could not have been created.
     */
    public BusinessObject createObject (DataElement dataElement,
                                        OID containerOid,
                                        boolean isTabObject,
                                        boolean isValidate)
    {
        OID tempOid = new OID (0, 0);
        Type tempType = null;

        // first check if the given containerOid is valid
        if (containerOid == null)
        {
            // log that object could not have been created
            this.p_log.add (DIConstants.LOG_ERROR,  
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_INVALID_CONTAINERID, env));
            this.p_log.add (DIConstants.LOG_ERROR, dataElement.typename + " '" +
                    dataElement.name + "' " +  
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULDNOTCREATEOBJECT, env));
            return null;
        } // if (containerOid == null)

        // check if an object with the same external key already exists.
        // if so the object cannot be created
        if (!this.isImportNewAllowed (dataElement.id, dataElement.idDomain, tempOid))
        {
            // check if the object is an user within a group:
            if ((tempType = this.getTypeCache ().getType (tempOid.tVersionId)) != null &&
                tempType.getCode ().equals (TypeConstants.TC_User) &&
                (tempType = this.getTypeCache ().getType (
                    containerOid.tVersionId)) != null &&
                tempType.getCode ().equals (TypeConstants.TC_Group))
            {
                // ensure that there is a reference for the user within the
                // group:
                BusinessObject group =
                    this.getObject (containerOid, this.user, this.sess, this.env);
                group.addReference (tempOid);
            } // if
            // log that the object is ignored
            this.p_log.add (DIConstants.LOG_ENTRY, dataElement.typename + " '" +
                    dataElement.name + "' " +  
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_EXTKEY_CLASH, env));
            return null;
        } // if object already exists

        // find out which object type shall be created
        // get the type code from the dataElement:
        String typeCode = dataElement.p_typeCode;

        // get the type id for the new object:
        Type type = this.getTypeCache ().findType (typeCode);

        // check if the type could have been found
        // if not the object can not be created
        if (type == null)
        {
            // log that there was no valid template or objecttype found
            this.p_log.add (DIConstants.LOG_ERROR,
                dataElement.typename + " '" +  dataElement.name + "' " +
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_TEMPLATE_NOT_FOUND, env));
            // return null to indicate that the object could not have been created
            return null;
        } // if (type = TypeConstants.TYPE_NOTYPE)

        // type found
        OID newOid = null;

        // create the full tVersion id:
        newOid = new OID (type.getTVersionId (), 0);

        // try to create an instance of the object type provided
        BusinessObject newObj = this.getObject (newOid, this.user, this.sess,
            this.env);

        // check if object can be created now:
        if (newObj == null)
        {
            // log that object could not have been created
            this.p_log.add (DIConstants.LOG_ERROR, dataElement.typename + " '" +
                dataElement.name + "' " +  
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULDNOTCREATEOBJECT, env));
            return null;
        } // if (newObj == null)

        // set container id for the new object
        newObj.containerId = containerOid;
        // for tab objects the containerKind must set to
        // BOConstants.CONT_PARTOF!!
        if (isTabObject)
        {
            newObj.setIsTab (true);
        } // if (isTabObject)

        // check if we create an XMLViewer and if yes log that
        // and find the corresponding document template.
        // Forms cannot be imported without a document template.
        if (isValidate && newObj instanceof XMLViewer_01)
        {
            // normaly when a form object is created
            // it is initialized with the default values
            // defined in the form template.
            // for imported objects this should not be done.
            XMLViewer_01 viewer = (XMLViewer_01) newObj;
            viewer.setIsObjectImport ();
            // try to find find the template
            DocumentTemplate_01 docTemplate;
            docTemplate = this.findDocumentTemplate (dataElement.p_typeCode);
            // if no template found the import is not possible.
            if (docTemplate == null)
            {
                this.p_log.add (DIConstants.LOG_ERROR,
                    dataElement.typename + " '" + dataElement.name + "' " +
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_TEMPLATE_NOT_FOUND, env));
                return null;
            } // if (docTemplate != null)
            // now validate the data element with the document template.
            if (!docTemplate.validateImportDataElement (dataElement, false, this.p_log))
            {
                this.p_log.add (DIConstants.LOG_ERROR,
                    dataElement.typename + " '" + dataElement.name + "' " +
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_INVALID_TEMPLATE, env));
                return null;
            } // if (!docTemplate.validateDataElement (dataElement))
        } // if (newObj instanceof ibs.di.XMLViewer_01)

        // now perform the creation of the object in the database:
        newObj = this.performCreateObject (newObj);
        // check if the object was not created successfully
        if (newObj == null)
        {
            // log that object has not been created
            this.p_log.add (DIConstants.LOG_ERROR, dataElement.typename + " '" +
                dataElement.name + "' " +  
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULDNOTCREATEOBJECT, env));
            return null;
        } // if (newObj == null)

        // for form objects the tabs must be initialized.
        // to set the default values and initialize (templateOID) the
        // XMLViewerContainer tab if any defined.
        if (newObj instanceof XMLViewer_01)
        {
            // if the object has tab object that are forms
            // or a form containers the initialisation
            // of this objects must be done now.
            ((XMLViewer_01) newObj).performSetTabDefaults (true);
        } // if (newObj instanceof ibs.di.XMLViewer_01)

        // set the oid of the new object in the import dataElement
        dataElement.oid = newObj.oid;
        // read in the date of the object from the dataElement
        newObj.readImportData (dataElement);

        // create the importFiles set in the dataElement
        // files vector
        this.createImportFiles (dataElement);
        // set the file names in the object. this must be
        // done in order to check if all files requested
        // by the object have successfully been written
        newObj.readImportFiles (dataElement);
        // save the changes to the object:
        if (this.performChangeObject (newObj))
        {
            // create the rights if set in the dataElement
            if (!dataElement.rights.isEmpty ())
            {
                // create the rights
                if (this.createRights (newObj.oid, dataElement))
                {       // rights successfully set
                    this.p_log.add (DIConstants.LOG_ENTRY,  
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_RIGHTSSETSUCCESSFULL, env));
                } // if (createRights (obj.oid, DataElement))
                else    // there as an error when setting the rights
                {
                    this.p_log.add (DIConstants.LOG_WARNING,  
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_RIGHTSSETFAILED, env));
                } // else there as an error when setting the rights
            } // if (!dataElement.rights.isEmpty ())

            // create a key mapping
            // for tab objects the mapping is not performed
            if (!isTabObject)
            {
/*
                    // check if the id shall be extended with the workspace
                    // user:
                    if (dataElement.idAddWspUser)
                    {
                        // get the extension and add it to the id:
                        dataElement.extendId
                            (this.getExtKeyWspUserExtension (containerOid));
                    } // if
*/
                if (!this.createKeyMapper (newObj.oid,
                    dataElement.id, dataElement.idDomain))
                {
                    this.p_log.add (DIConstants.LOG_WARNING, newObj.oid,
                        newObj.typeName + " '" + newObj.name + "' " +
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_EXTKEY_ERROR, env));
                } // if keymapping failed
            } // if is not a tab
            // log that object has been created
            this.p_log.add (DIConstants.LOG_ENTRY, newObj.oid,
                newObj.typeName + " '" + newObj.name + "' " +
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_OBJECTCREATED, env));
            // return the object to created
            return newObj;
        } // if (performChangeObject (newObj))

        // could not change object
        // return null to indicate that object could not have been changed
        return null;
    } // createObject

    
    /**************************************************************************
     * Changes an object via import.<BR> 
     * 
     * Note that this is a wrapper method for
     * #changeObject(DataElement, OID, boolean, boolean). <BR/>
     *
     * @param   dataElement     The DataElement to extract the data from.
     *
     * @return  <CODE>true</CODE> if object could be found and changed and
     *          <CODE>false</CODE> if not.
     *
     * @see #changeObject(DataElement, OID, boolean, boolean)
     */
    public boolean changeObject (DataElement dataElement)
    {
        return changeObject (dataElement, null, false, false);
    } // changeObject
    
    
    /**************************************************************************
     * Changes an object via import.<BR> 
     * 
     * Note that this is a wrapper method for
     * #changeObject(DataElement, OID, boolean, boolean). <BR/>
     *
     * @param   dataElement     The DataElement to extract the data from.
     * @param   containerOid    The container OID in which to create the object.
     * @param   isTabObject     Is the object a tab object?
     *
     * @return  <CODE>true</CODE> if object could be found and changed and
     *          <CODE>false</CODE> if not.
     *
     * @see #changeObject(DataElement, OID, boolean, boolean)
     */
    public boolean changeObject (DataElement dataElement,
                                 OID containerOid,
                                 boolean isTabObject)
    {
        return this.changeObject (dataElement, containerOid, isTabObject, false);
    } // changeObject


    /**************************************************************************
     * Changes an object via import. 
     * 
     * Note that this is a wrapper method for compatibility reasons
     * #changeAndGetObject(DataElement, OID, boolean, boolean). <BR/>
     *
     * @param   dataElement     The DataElement to extract the data from.
     * @param   containerOid    The container OID in which to create the object.
     * @param   isTabObject     Is the object a tab object?
     * @param   isValidate      option to activate the structural check
     *
     * @return  <CODE>true</CODE> if object could be found and changed and
     *          <CODE>false</CODE> if not.
     *
     * @see #changeObject(DataElement, OID, boolean)
     */
    public boolean changeObject (DataElement dataElement,
            OID containerOid,
            boolean isTabObject,
            boolean isValidate)
    {
        return (changeAndGetObject (dataElement, containerOid, 
                                    isTabObject, false) != null);
    } // changeObject        

    
    /**************************************************************************
     * Changes an object via import.<BR> 
     * 
     * Note that this is a wrapper method for
     * #changeAndGetObject(DataElement, OID, boolean, boolean). <BR/>
     *
     * @param   dataElement     The DataElement to extract the data from.
     *
     * @return  the changed object or <code>null</code> otherwise
     *
     * @see #changeObject(DataElement, OID, boolean, boolean)
     */
    public BusinessObject changeAndGetObject (DataElement dataElement)
    {
        return changeAndGetObject (dataElement, null, false, false);
    } // changeAndGetObject
    
    
    /**************************************************************************
     * Changes an object via import. First a key mapping between the external
     * object and its related object in the database has to be resolved. <BR/>
     * In case this mapping succeeds the object will be changed by the data
     * stored in an DataElement. <BR/>
     *
     * @param   dataElement     The DataElement to extract the data from.
     * @param   containerOid    The container OID in which to create the object.
     *                          Note that this parameter is not in use anymore
     *                          because of an bug
     * @param   isTabObject     Is the object a tab object?
     * @param   isValidate      option to activate the structural check
     *
     * @return  the changed object or <code>null</code> otherwise
     *
     * @see #changeObject(DataElement, OID, boolean)
     */
    public BusinessObject changeAndGetObject (DataElement dataElement,
                                              OID containerOid,
                                              boolean isTabObject,
                                              boolean isValidate)
    {
        OID objectOid = null;

        // check if we have to handle a tab object
        if (isTabObject)
        {
            // for tab objects the oid is already defined in the data element
            objectOid = dataElement.oid;
            if (objectOid == null)
            {
                return null;
            } // if (objectOid == null)
        } // if (isTabObject)
        else                            // not a tab object
        {
            // try to resolve a keymapping
            // objectname and type mapping activated?
            if (this.p_isNameTypeMapping)
            {
                objectOid = this.getOid (dataElement.typename, dataElement.name);
            } // if
            else
            {
                // get a key mapping:
                objectOid = this.getKeyMapper (dataElement.id,  dataElement.idDomain);
            } // if (objectOid == null)
            // check if we could resolve an oid:
            if (objectOid == null)  // object not found?
            {
                return null;
            } // if object not found
        } // else not a tab object

        // get the object and change it:
        // KR 20100219: reading of parameters for the object is not necessary
        //              thus the parameter "getParameters" is set to false
        //              Reason: This method may only be called from Import
        //              mechanism within ImportIntegrator. There is no GUI
        //              functionality which calls the method changeAndGetObject.
        BusinessObject obj =
            this.getObject (objectOid, this.user, this.sess, this.env, false);
        // check if we got the object
        if (obj == null)
        {
            return null;
        } // if (obj == null)

        // if the object is a xml viewer object
        // we have to get the document template assigned to this
        // object and perform a check if the data element
        // confirms to this document template.
        if (isValidate && obj instanceof ibs.di.XMLViewer_01)
        {
            // cast to an XVLViewer_01 object
            XMLViewer_01 viewer = (XMLViewer_01) obj;
            // get the document template for the object
            DocumentTemplate_01 docTemplate = viewer.p_templateObj;
            // if no template matches the form the
            // import is not possible.
            if (docTemplate == null)
            {
                this.p_log.add (DIConstants.LOG_ERROR,
                    dataElement.typename + " '" + dataElement.name + "' " +
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_TEMPLATE_NOT_FOUND, env));
                return null;
            } // if (docTemplate == null)

            // now validate the data element with the document template.
            if (!docTemplate.validateImportDataElement (dataElement, true, this.p_log))
            {
                this.p_log.add (DIConstants.LOG_ERROR,
                    dataElement.typename + " '" + dataElement.name + "' " +
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_INVALID_TEMPLATE, env));
                return null;
            } // if (!docTemplate.validateDataElement (dataElement))

        } // if (newObj instanceof ibs.di.XMLViewer_01)

        // set the oid of the object in the import element
        dataElement.oid = obj.oid;
        // read in the date of the object from the DataElement
        obj.readImportData (dataElement);
        // create the importFiles set in the dataElement
        // files vector
        this.createImportFiles (dataElement);
        // set the file names in the object. this must be
        // done in order to check if all files requested
        // by the object have successfully been written
        obj.readImportFiles (dataElement);

        // now save the changes to the object:
        if (this.performChangeObject (obj))
        {
            // check it there are rigths set in the DataElement
            if (!dataElement.rights.isEmpty ())
            {
                if (this.createRights (obj.oid, dataElement))
                {       // rights successfully set
                    this.p_log.add (DIConstants.LOG_ENTRY,  
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_RIGHTSSETSUCCESSFULL, env));
                } // if (createRights (obj.oid, DataElement))
                else    // there as an error when setting the rights
                {
                    this.p_log.add (DIConstants.LOG_WARNING,  
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_RIGHTSSETFAILED, env));
                } // else there as an error when setting the rights
            } // if (DataElement.rights != null)

            // move the object to a new container if necessary:
            if (containerOid != null && !containerOid.equals (obj.containerId))
            {
                // move the object:
                this.moveObject (obj, containerOid);
            } // if (containerOid != null)

            // set the name of the object in the dataelement
            // because some partial imports do not set the name in the dataelement
            // in that case the importintegrator does only show "" as name
            // in its log messages.
            dataElement.name = obj.name;
            // return true to indicate that the change operation was successfull
            return obj;
        } // if (performChangeObject (obj))
        else    // could not change object
        {
            // could not change object
            // return null to indicate that object could not have been changed
            return null;            
        } // could not change object
    } // changeAndGetObject


    /**************************************************************************
     * Deletes an object. First a key mapping between the external
     * object and its related object in the database has to be resolved. <BR/>
     * In case this mapping succeeds the object will be deleted. <BR/>
     *
     * @param   dataElement The DataElement to extract the data from.
     *
     * @return  <CODE>true</CODE> if object could be found and deleted and
     *          <CODE>false</CODE> if not.
     */
    public boolean deleteObject (DataElement dataElement)
    {
        OID objectOid = null;

        // try to resolve a keymapping
        // objectname and type mapping activated?
        if (this.p_isNameTypeMapping)
        {
            objectOid = this.getOid (dataElement.typename, dataElement.name);
        } // if
        else
        {
            objectOid = this.getKeyMapper (dataElement.id,  dataElement.idDomain);
        } // else

        // check if the oid is valid:
        if (objectOid == null)          // oid not valid?
        {
            // return false to indicate that object was not found:
            return false;
        } // if oid not valid

        // get the object and change it:
        BusinessObject obj =
            this.getObject (objectOid, this.user, this.sess, this.env);
        // check if we got an object
        if (obj == null)
        {
            // return false to indicate that we could not get the object
            return false;
        } // if (obj == null)

        // set the parameters of the object
        // BB: i am not sure if this really has to be done but some object
        // set specific values through this method
        obj.getParameters ();
        // now try to retrieve the object in order to do the rights check
        // and delete it afterwards
        try
        {
/* KR 020125: not necessary because already done before
            // get the object data out of the database
            // BB: do we really have to retrieve the object first
            obj.retrieve (Operations.OP_DELETE);
*/
            // set the oid of the object into the import element
            dataElement.oid = obj.oid;
            dataElement.name = obj.name;
            // try to delete the object and return the result
            return this.performDeleteObject (obj);
        } // try
/* KR 020125: not necessary because already done before
        catch (NoAccessException e) // no access to object allowed
        {
            this.log.add (DIConstants.LOG_ERROR, e.getMessage ());
            return false;
        } // catch
        catch (AlreadyDeletedException e)
        {
            this.log.add (DIConstants.LOG_ERROR, e.getMessage ());
            return false;
        } // catch
*/
        catch (Exception e)
        {
            this.p_log.add (DIConstants.LOG_ERROR, e.toString ());
            return false;
        } // catch
    } // deleteObject


    /**************************************************************************
     * Creates a new business object. <BR/>
     *
     * @param obj       The object to be created.
     *
     * @return  The object which was created or <CODE>null</CODE> if this was
     *          not possible.
     */
    public BusinessObject performCreateObject (BusinessObject obj)
    {
        // this is a dummy value because representationForm will not be
        // supported anymore in the future
        int representationForm = 0;
        // ensure that the object has a correct name:
        if (obj.name == null)       // no name defined?
        {
            obj.name = "";          // set default name
        } // if
        // create the new object:
        OID newOid = obj.create (representationForm);

        // check if the object has been created
        if (newOid == null)
        {
            // return null to indicate that object has not been created
            return null;
        } // if (newOid == null)

        // object created
        // show the tabs:
        obj.displayTabs = true;

        // return the object to indicate that object has been created
        return obj;
    } // performCreateObject


    /**************************************************************************
     * Changes a business object. <BR/>
     *
     * @param obj       The object to be created.
     *
     * @return  <CODE>true</CODE> if the object has been changed sucessfully
     */
    public boolean performChangeObject (BusinessObject obj)
    {
        // ensure that the object has a correct name:
        if (obj.name == null)       // no name defined?
        {
            obj.name = "";          // set default name
        } // if
        // change the object:
        try
        {
            // store the object in the database:
            // note that we use the performChange(int) instead of the
            // the performChange() method includes an additional
            // retrieve that is not neccessary when importing objects
            obj.performChange (Operations.OP_CHANGE);
            // HACK: sideeffekt after using performChange (int)
            // the state of the object is changed during the execution
            // of the change stored procedure. due to skipping the retrieve()
            // the state of a newly created object still remains ST_CREATED
            // instead of ST_ACTIVE and that has some sideeffect
            // we therefore set the state hardcoded to ST_ACTIVE because
            // we can assume that the state must be active at that point
            obj.state = States.ST_ACTIVE;
            // return true to indicate that object has been changed
            return true;
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            this.p_log.add (DIConstants.LOG_ERROR, e.getMessage ());
            return false;
        } // catch
        catch (NameAlreadyGivenException e) // name of object already given?
        {
            this.p_log.add (DIConstants.LOG_ERROR, e.getMessage ());
            return false;
        } // catch
        catch (Exception e) // name of object already given?
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            this.p_log.add (DIConstants.LOG_ERROR, e.toString ());
            return false;
        } // catch
    } // performChangeObject


    /**************************************************************************
     * Moves the given object to the given target. <BR/>
     *
     * @param   obj         The object which shall be moved.
     * @param   targetId    Oid of the target container.
     *
     * @return  <CODE>true</CODE>, <CODE>false</CODE>.
     */
    private boolean moveObject (BusinessObject obj, OID targetId)
    {
        // PROBLEM: no success-check possible!

        // update the object structure from the database:
        try
        {
            // try to move within the database:
            // (operation = 0, no rights needed for workflow-move)
            obj.performMoveData (targetId, 0); // not defined
            this.p_log.add (DIConstants.LOG_ENTRY,
                MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTMOVED, new String[] {obj.name}, env));
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            // send message to the user:
            obj.showNoAccessMessage (Operations.OP_MOVE);
            return false;
        } // catch

        // exit
        return true;
    } // moveObject


    /**************************************************************************
     * Delete a business object. <BR/>
     *
     * @param obj       The object to be deleted.
     *
     * @return  <CODE>true</CODE> if the object has been deleted sucessfully
     */
    public boolean performDeleteObject (BusinessObject obj)
    {
        // try to delete the object in the database:
        return obj.delete (Operations.OP_DELETE);
    } // performDeleteObject


    /**************************************************************************
     * Get an object out of the object cache. <BR/>
     *
     * @param   oid     The oid the object to get.
     * @param   user    The user who wants to get the object.
     * @param   sess    The actual session info object.
     * @param   env     The actual environment.
     *
     * @return  The object if it was found,
                <CODE>null</CODE> otherwise.
     */
    public BusinessObject getObject (OID oid, User user, SessionInfo sess,
                                     Environment env)
    {
        // call common function:
        return this.getObject (oid, user, sess, env, true);
    } // getObject


    /**************************************************************************
     * Get an object out of the object cache. <BR/>
     *
     * @param   oid             The oid the object to get.
     * @param   user            The user who wants to get the object.
     * @param   sess            The actual session info object.
     * @param   env             The actual environment.
     * @param   getParameters   Shall the environment parameters be read, too?
     *
     * @return  The object if it was found,
                <CODE>null</CODE> otherwise.
     */
    public BusinessObject getObject (OID oid,
                                     User user,
                                     SessionInfo sess,
                                     Environment env,
                                     boolean getParameters)
    {
        return BOHelpers.getObject (oid, env, false, getParameters);
    } // getObject


    /**************************************************************************
     * Gets the type code for the given typename.
     *
     * @param appInfo       the application info object
     * @param typeName      name of the object type
     *
     * @return              the object type code
     *                      <CODE>""</CODE> if the type was not found
     */
    public static String getTypeCodeFromName (ApplicationInfo appInfo,
                                              String typeName)
    {
        // get the type container from the application cache.
        TypeContainer types = ((ObjectPool) appInfo.cache).getTypeContainer ();

        // search the type name in the type vector
        // and return the type code
        Type type = types.findTypeByName (typeName);
        if (type != null)
        {
            return type.getCode ();
        } // if

        // if the type name was not found in the type container
        // return an empty name:
        return "";
    } // getTypeCodeFromName


    /**************************************************************************
     * Gets the type id of a given typename. If type cannot be found the type
     * of an XMLViewer object will be returned. <BR/>
     *
     * @param typename      name of the object type
     * @param isTabObject   true if the object is a tab object
     *
     * @return the type id of the object
     */
/*
    private int getObjectType (String typename, boolean isTabObject)
    {
//showDebug ("--- START getObjectType" +
           " - typename : " + typename +
           " - isTabObject : " + isTabObject);

        int i = 0;
        int type = 0;

        if (isTabObject)
        {
            for (i = this.importableTabTypes.length - 1;
                 i >= 0 && !typename.equalsIgnoreCase (this.importableTabTypes[i]);
                 i--)
                 showDebug ("Compare "+ this.importableTabTypes[i] + " and " + typename);

            // the type was found?
            if (i >= 0)
            {
                showDebug ("tab typename found!");
                // set the type of the new object
                type = this.importableTabTypeIds[i];
            } // if (i >= 0)
            else       // unknown object type
            {
                showDebug ("tab typename NOT found!");
                // set the xml viewer as object type:
                type = getCache ().getTypeId (TypeConstants.TC_XMLViewer);
            } // else unknown object type
        } // if (isTabObject)
        else        // no tab object
        {
            for (i = this.importableTypes.length - 1;
                 i >= 0 && !typename.equalsIgnoreCase (this.importableTypes[i]);
                 i--);
            if (i >= 0)                     // the type was found?
            {
                // set the type of the new object
                type = this.importableTypeIds[i];
            } // if (i >= 0)
            else                            // unknown object type
            {
                // get the type id from the application cache
                type = getCache ().getTypeId (typename);

                // if the type name (code) is not a registred m2 type
                // set the xml viewer as object type:
                if (type == 0)
                    type = getCache ().getTypeId (TypeConstants.TC_XMLViewer);
            } // else unknown object type
        } // else no tab object
        return type;
    } // getObjectType
*/

    /**************************************************************************
     * Creates a BusinessObject from a type id. <BR/>
     *
     * @param   type    The type from which to create the business object.
     *
     * @return  The new business object instance which was created.
     *          <CODE>null</CODE> if the instance could not be created.
     */
    public BusinessObject getObjectFromType (int type)
    {
        OID newOid;

        // set the oid of the new object:
        newOid = new OID (Type.createTVersionId (type), 0);

        // try to init the object
        BusinessObject obj = this.getObject (newOid, this.user, this.sess, this.env);
        if (obj == null)
        {
            String oidName = String.valueOf (newOid).toString ();
            IOHelpers.showMessage ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTOIDNOTFOUND,
                    new String[] {oidName}, env),
                this.app, this.sess, this.env);
            return null;
        } // if (obj == null)

        return obj;
    } // getObjectFromType


    /**************************************************************************
     * Creates the rights of an object from the declarations in the
     * importdocument. <BR/>
     *
     * @param   oid         the oid of the object to create the rights for
     * @param   dataElement the dataElement that stores the right definitions
     *
     * @return  <CODE>true</CODE> if the rights were created successfully,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean createRights (OID oid, DataElement dataElement)
    {
        boolean allOk = true;
        boolean isDeleteRights = false;
        int counter = 1;

        // check if there are rights to set
        if (dataElement != null)
        {
            // loop trough the rights elements and create the rights
            for (Iterator<RightDataElement> iter = dataElement.rights.iterator (); iter.hasNext ();)
            {
                RightDataElement rie = iter.next ();
                // delete all rights at the first call
                isDeleteRights = counter++ == 1;
                // create the right and set a flag if operation failed
                if (this.performCreateRights (oid, rie.name, rie.isUser (),
                                        rie.getRights (), false, isDeleteRights,
                                        this.getUser ().domain))
                {
                    // add log entry that a rights has been set successfully
                    if (rie.isUser ())
                    {
                        this.p_log.add (DIConstants.LOG_ENTRY, 
                            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                DIMessages.ML_MSG_RIGHTS_SET_FOR_USER, env)
                            + " '" + rie.name + "'.");
                    } // if
                    else
                    {
                        this.p_log.add (DIConstants.LOG_ENTRY, 
                            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                DIMessages.ML_MSG_RIGHTS_SET_FOR_GROUP, env)
                            + " '" + rie.name + "'.");
                    } // else
                } // if ( performCreateRight (oid, rie.name,  rie.isUser (),
                else    // error while setting rights
                {
                    // add log entry that a right could not have been set
                    if (rie.isUser ())
                    {
                        this.p_log.add (DIConstants.LOG_WARNING, 
                            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                DIMessages.ML_MSG_COULD_NOT_SET_RIGHTS_FOR_USER, env)
                            + " '" + rie.name + "'.");
                    } // if
                    else
                    {
                        this.p_log.add (DIConstants.LOG_WARNING, 
                            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                DIMessages.ML_MSG_COULD_NOT_SET_RIGHTS_FOR_GROUP, env)
                            + " '" + rie.name + "'.");
                    } // else
                    allOk = false;
                } // error while setting rights
            } // for iter
        } // if (DataElement.rights != null)
        return allOk;
    } // createRights


    /**************************************************************************
     * Sets rights for a user or a group for a specific object . <BR/>
     * Additionally all rights set for the object can be deleted first.
     * The reference to the user or the group to set the rights for will be
     * done though the name of the user or the group. This expects that there
     * are no ambiguous user or group names in the system. <BR/>
     *
     * @param   oid             oid of the object to set the rights for
     * @param   name            name of the user or the group
     * @param   isUser          flag to set rights for user or otherwise group
     * @param   rights          rights to set
     * @param   isRecursive     Shall the rights be set recursively?
     * @param   isDeleteRights  flag to first delete all rights set for the object
     * @param   domainId        id of domain to look for the user or the group
     *
     * @return  <CODE>true</CODE> if the right could have been set or
                otherwise <CODE>false</CODE>.
     */
    public boolean performCreateRights (OID oid, String name, boolean isUser,
                                        int rights, boolean isRecursive,
                                        boolean isDeleteRights, int domainId)
    {
        int retVal = UtilConstants.QRY_OK;            // return value of query
        StoredProcedure sp = new StoredProcedure (
            "p_Integrator$setImportRights", StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)

        // oid:
        BOHelpers.addInParameter (sp, oid);
        // name:
        sp.addInParameter (ParameterConstants.TYPE_STRING, name);
        // isUser:
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, isUser);
        // rights:
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, rights);
        // isRecursive:
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, isRecursive);
        // deleteRights:
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, isDeleteRights);
        // domainId:
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, domainId);

        try
        {
            // perform the function call:
            retVal = BOHelpers.performCallFunctionData (sp, this.env);
        } // try
        catch (NoAccessException e)
        {
            return false;
        } // catch
        // check if query was successfull
        if (retVal == UtilConstants.QRY_OK)
        {
            return true;
        } // if

        return false;
    } // performCreateRights


    /**************************************************************************
     * Create object references in a set of container objects. <BR/>
     *
     * @param   containerOids       an array of containers OID
     * @param   linkedObjectOid     the oid of the object we want to create
     *                              references to
     * @param   name                the name of the object that will also be the
     *                              name of the reference object
     *
     * @return  <CODE>true</CODE> if the references were created successfully,
     *          <CODE>false</CODE> otherwise.
     *
     * @deprecated  This method is not longer used. (2006-01-20)
     */
    public boolean createReferences (OID[] containerOids, OID linkedObjectOid,
                                     String name)
    {
        int i = 0;
        int k = 0;
        SQLAction action = null;        // the action object used to access the
        boolean allOk = true;
        StoredProcedure sp = new StoredProcedure ();
        Parameter [] params = new Parameter[10]; // contains the parameters

        // set stored procedure return type
        sp.setReturnType (StoredProcedureConstants.RETURN_VALUE);
        // set stored procedure name
        sp.setName ("p_Referenz_01$create");

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        i = -1;                         // initialize parameter number

        // input parameters
        // uid
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_INTEGER);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        if (this.user != null)
        {
            params[i].setValue (this.user.id);
        } // if
        else
        {
            params[i].setValue (0);
        } // else

        // operation
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_INTEGER);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        params[i].setValue (Operations.OP_ADDELEM + 1);  //  ???? +1 ????
        // tVersionId
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_INTEGER);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        params[i].setValue (this.getTypeCache ().getTVersionId (TypeConstants.TC_Reference));
        // name
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_STRING);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        params[i].setValue (name);
        // containerId_s
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_STRING);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        params[i].setValue ("");  //
        // set a link to this parameter in order to use it in the loop
        Parameter containerIdParam = params[i]; // remember the parameter
        // containerKind
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_INTEGER);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        params[i].setValue (BOConstants.CONT_STANDARD);
        // isLink
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_BOOLEAN);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        params[i].setValue (true);
        // linkedObjectId_s
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_STRING);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        params[i].setValue (linkedObjectOid.toString ());  // Group
        // description
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_STRING);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        params[i].setValue ("");
        // oid_s
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_STRING);
        params[i].setDirection (ParameterConstants.DIRECTION_OUT);

        // remove old parameters
        sp.removeParameters ();

        // add parameters to stored procedure
        for (int j = 0; j <= i; j++)
        {
            sp.addParameter (params[j]);
        } // for j

        IOHelpers.showProcCall (this, sp, this.sess, this.env);
        // execute stored procedure
        try
        {
            action = this.getDBConnection ();
            // insert all joins to the group
            for (k = 0; k < containerOids.length; k++)
            {
                // check if there is a container oid
                if (containerOids [k] != null)
                {
                    // set the next group oid
                    containerIdParam.setValue (containerOids[k].toString ());
                    // execute stored procedure - return value
                    // gives right-information
                    action.execStoredProc (sp);
                } // if (containerOids [k] != null)
            } // for (int k=0; k < oidStrings.length; k++)
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            allOk = false;
        } // catch
        finally
        {
            this.releaseDBConnection (action);
        } // finally
        return allOk;
    } // createReferences


    /**************************************************************************
     * Create a reference to an object in a specific container. <BR/>
     *
     * @param   containerOid        the containers OID
     * @param   linkedObjectOid     the oid of the object we want to create
     *                              references to
     * @param   name                the name of the object that will also be the
     *                              name of the reference object
     *
     * @return  <CODE>true</CODE> if the reference was created successfully,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean performCreateReference (OID containerOid,
                                           OID linkedObjectOid, String name)
    {
        int i = 0;
        SQLAction action = null;        // the action object used to access the
        boolean allOk = true;
                                        // database
        int retVal = UtilConstants.QRY_OK;          // return value of query
        StoredProcedure sp = new StoredProcedure ();
        Parameter [] params = new Parameter[10]; // contains the parameters

        // set stored procedure return type
        sp.setReturnType (StoredProcedureConstants.RETURN_VALUE);
        // set stored procedure name
        sp.setName ("p_Referenz_01$create");

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        i = -1;                         // initialize parameter number

        // input parameters
        // uid
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_INTEGER);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        if (this.user != null)
        {
            params[i].setValue (this.user.id);
        } // if
        else
        {
            params[i].setValue (0);
        } // else

        // operation
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_INTEGER);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        params[i].setValue (Operations.OP_ADDELEM + 1);  //  ???? +1 ????
        // tVersionId
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_INTEGER);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        params[i].setValue (this.getTypeCache ().getTVersionId (TypeConstants.TC_Reference));
        // name
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_STRING);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        params[i].setValue (name);
        // containerId_s
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_STRING);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        params[i].setValue (containerOid.toString ());
        // containerKind
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_INTEGER);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        params[i].setValue (BOConstants.CONT_STANDARD);
        // isLink
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_BOOLEAN);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        params[i].setValue (true);
        // linkedObjectId_s
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_STRING);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        params[i].setValue (linkedObjectOid.toString ());  // Group
        // description
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_STRING);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        params[i].setValue ("");
        // oid_s
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_STRING);
        params[i].setDirection (ParameterConstants.DIRECTION_OUT);

        // remove old parameters
        sp.removeParameters ();

        // add parameters to stored procedure
        for (int j = 0; j <= i; j++)
        {
            sp.addParameter (params[j]);
        } // for j

        IOHelpers.showProcCall (this, sp, this.sess, this.env);
        // execute stored procedure
        try
        {
            action = this.getDBConnection ();
            // execute stored procedure - return value
            // gives right-information
            retVal = action.execStoredProc (sp);
            // check if everything was ok
            if (retVal != UtilConstants.QRY_OK)
            {
                // set allOk to false to indicate that an error occurred
                allOk = false;
            } // if (retVal != UtilConstants.QRY_OK)
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            allOk = false;
        } // catch
        finally
        {
            this.releaseDBConnection (action);
        } // finally
        return allOk;
    } // performCreateReferences


    /**************************************************************************
     * Creates a key mapping for an external object and an internal object. <BR/>
     * In case there is no id paramter specified the key mapping will not be
     * created. <BR/>
     *
     * @param   oid         Oid of the object.
     * @param   id          External id.
     * @param   idDomain    Key domain of external id.
     *
     * @return  <CODE>true</CODE> if the key mapper was created successfully,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean createKeyMapper (OID oid, String id, String idDomain)
    {
        // do only create a key mapping if at least the id value has been
        // specified
        if (id != null && id.length () > 0)
        {
            // init a keymapper if necessary:
            this.initKeyMapper ();
            // create the key mapping:
            return this.p_keyMapper
                .performCreateMapping (new KeyMapper.ExternalKey (oid,
                    idDomain, id));
        } // if (id != null && id.length () > 0)
        return true;
    } // createKeyMapper


    /**************************************************************************
     * Resolves a keyMapping between an external object and an internal object.
     * Returns the oid of the internal object if found. <BR/>
     * In case there is no id parameter specified the key mapping will not be
     * resolved. <BR/>
     *
     * @param id        external id
     * @param idDomain  key domain of external id
     *
     * @return if found the oid of the internal object or null otherwise
     */
    public OID getKeyMapper (String id, String idDomain)
    {
        // check first if an id value has been defined
        if (id != null && id.length () > 0)
        {
            // init a keymapper if necessary:
            this.initKeyMapper ();
            // try to resolve the key mapping
            return this.p_keyMapper.performResolveMapping (
                new KeyMapper.ExternalKey (idDomain, id));
        } // if (id != null && id.length () > 0)

        return null;
    } // getKeyMapper


    /**************************************************************************
     * Check if an object with the given ExtKey can be imported as a new Object.
     *
     * @param   id          External id.
     * @param   idDomain    Key domain of external id.
     * @param   tempOid     Container for the oid. This value is changed within
     *                      this method.
     *
     * @return  <CODE>true</CODE> if the import is allowed,
     *          otherwise <CODE>false</CODE>.
     *          The parameter tempOid contains the oid which was found for the
     *          external key (only of the return value is <CODE>false</CODE>).
     */
    protected boolean isImportNewAllowed (String id, String idDomain,
                                          OID tempOid)
    {
        // check first if the EXTKEY is valid
        if (id != null && idDomain != null)
        {
            // init a keymapper if necessary:
            this.initKeyMapper ();
            // try to resolve the key mapping:
            OID objOid = this.p_keyMapper.performResolveMapping (
                new KeyMapper.ExternalKey (idDomain, id));

            if (objOid != null)         // object found?
            {
                // set the oid for the return value.
                tempOid.setOid (objOid.oid);
                return false;
            } // if object found

            return true;
        } // if (id != null && idDomain != null)

        return true;
    } // isImportAllowed


    /**************************************************************************
     * Gets the matching document template for the given type code. <BR/>
     *
     * ATTENTION!! THIS IS REDUNDANT CODE!!
     * You will find the same code in the DocumentTemplate class.
     * The redundancy is needed to avoid cyclic imports.
     *
     * @param   typeCode    the type code of the template
     *
     * @return  the matching document template or <CODE>null</CODE> if
     *          no matching template is found.
     */
    private DocumentTemplate_01 findDocumentTemplate (String typeCode)
    {
        // search for the type in the type cache:
        Type type = this.getTypeCache ().findType (typeCode);

        // check if the type was found:
        if (type != null)
        {
            // return the template of the type:
            return (DocumentTemplate_01) (type.getTemplate ());
        } // if

        // no document template found
        return null;
    } // findDocumentTemplate


    /**************************************************************************
     * Create the import files for an object. <BR/>
     * All files that should be created are stored in the files vector
     * of the data element and will be read via the connector. <BR/>
     *
     * @param   dataElement The DataElement to extract the data from.
     */
    public void createImportFiles (DataElement dataElement)
    {
        FileDataElement fileDataElement;
        String targetFileName;
        String fileName;
        int pos = 0;

        // check if any files have been set
        if (dataElement.files != null)
        {
            // loop through the files set in the DataElement
            for (Iterator<FileDataElement> iter = dataElement.files.iterator ();
                 iter.hasNext ();)
            {
                // get the fileDataElement:
                fileDataElement = iter.next ();

                // drop the directory name from the file name:
                fileName = fileDataElement.fileName;
                if ((pos = fileName.lastIndexOf (File.separator)) > -1)
                {
                    fileName = fileName.substring (pos + 1);
                } // if

                // cut off any oid at the beginning of the filename in order
                // to avoid growing filenames having multiple oids at the beginning
                // note that the oid of the object will always be added to the
                // filename!
                if (fileName.startsWith (UtilConstants.NUM_START_HEX) &&
                    fileName.length () > 18)
                {
                    fileName = dataElement.oid + fileName.substring (18);
                } // if
                else
                {
                    fileName = dataElement.oid + fileName;
                } // else

                // create an unique file name for the target file:
                targetFileName = FileHelpers.getUniqueFileName (
                    fileDataElement.path, fileName);

                // create the directory if neccessary
                if (FileHelpers.makeDir (fileDataElement.path))
                {
                    try
                    {
                        // read the file through the connector
                        fileDataElement.size = this.connector.readFile (
                            fileDataElement.fileName,
                            fileDataElement.path, targetFileName);
                    } // try
                    catch (ConnectionFailedException e)
                    {
                        this.p_log.add (DIConstants.LOG_WARNING, e.getMessage ());
                        // add a log entry
                        this.p_log.add (DIConstants.LOG_WARNING, 
                            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                DIMessages.ML_MSG_COULD_NOT_READ_FILE,
                                new String[] {fileDataElement.fileName}, env));
                    } // catch
                } // if (FileHelpers.makeDir (fileDataElement.path))
                else                    // could not create destination directory
                {
                    // set the filesize to -1 to indicate that file could
                    // not be written
                    fileDataElement.size = -1;
                } // else could not create destination directory
                // check if the file could have been read
                if (fileDataElement.size != -1)
                {
                    // set the filename in the list of files that could have
                    // been read. This can be used later to delete all files
                    // that have been imported
                    this.p_importedFiles.addElement (fileDataElement.fileName);
                    // new update the file name with the
                    // just in case it has been changed
                    // to ensure a unique file name
                    fileDataElement.fileName = targetFileName;
                } // if (fileDataElement.size != -1)
                else     // file could not have been read
                {
                    // reset the fileName
                    fileDataElement.fileName = "";
                } // if (fileDataElement.size != -1)
            } // for iter
        } // if (dataElement.files != null)
    } // createImportFiles


    /**************************************************************************
     * Returns the filenames stored in the importedFiles propery as a
     * string array. <BR/>
     *
     * @return a string array with the filenames or null if the vector is empty
     */
    public String [] getImportedFiles ()
    {
        // are there any files set in the importFiles vector
        if (this.p_importedFiles != null && this.p_importedFiles.size () > 0)
        {
            // create an array with the file names
            String [] fileNames = new String  [this.p_importedFiles.size ()];
            // copy the elements
            for (int i = 0; i < this.p_importedFiles.size ();  i++)
            {
                fileNames [i] = this.p_importedFiles.elementAt (i);
            } // for (int i = 0; i < this.importedFiles.size; i++)
            // return the array
            return fileNames;
        } // if (this.importedFiles != null)

        return null;
    } // getImportedFiles


    /**************************************************************************
     * Initialize the global keymapper object if neccessary. <BR/>
     */
    public void initKeyMapper ()
    {
        if (this.p_keyMapper == null)
        {
            this.p_keyMapper =
                new KeyMapper (this.user, this.env, this.sess, this.app);
        } // if
    } // initKeyMapper


    /***************************************************************************
     * Get the oid of an object with a given typename and a given
     * object name. <BR/>
     * If there are more than one objects which satisfy these criteria the
     * first one is returned.
     *
     * @param   typeName    The typename of the object.
     * @param   objName     The name of the object.
     *
     * @return  The oid of the object in case it could have been found
     *          and if it was unique or <CODE>null</CODE> otherwise.
     */
    public OID getOid (String typeName, String objName)
    {
        OID returnOid = null;
        int rowCount;
        SQLAction action = null;        // the action object used to access the
                                        // database

        // create the SQL String to select the project order
        String queryStr =
            " SELECT oid" +
            " FROM   ibs_object " +
            " WHERE  typeName = '" + typeName + "'" +
            " AND    name = '" + objName + "' " +
            " AND    state = " + States.ST_ACTIVE +
            " AND    isLink = 0 ";

        try
        {
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);
            // is the result exactly one row?
            if (rowCount == 1)
            {
                // get the oid
                returnOid = SQLHelpers.getQuOidValue (action, "oid");
            } // if
            // end transaction
            action.end ();
        } // try
        catch (DBError e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            this.releaseDBConnection (action);
        } // finally

        // return the oid
        return returnOid;
    } // getOid

} // ObjectFactory

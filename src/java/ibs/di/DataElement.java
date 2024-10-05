/*
 * Class: DataElement.java
 */

// package:
package ibs.di;

// imports:
import ibs.BaseObject;
import ibs.bo.OID;
import ibs.bo.cache.ObjectPool;
import ibs.bo.type.Type;
import ibs.bo.type.TypeNotFoundException;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.ml.MlInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.ml.Locale_01;
import ibs.service.action.Actions;
import ibs.util.DateTimeHelpers;
import ibs.util.file.FileHelpers;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.w3c.dom.Node;


/******************************************************************************
 * The DataElement hold the information of an OBJECT section from the
 * XML import file. <BR/>
 *
 * @version     $Id: DataElement.java,v 1.79 2013/01/15 14:48:28 rburgermann Exp $
 *
 * @author      Buchegger Bernd (BB), 990107
 ******************************************************************************
 */
public class DataElement extends BaseObject implements Cloneable
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DataElement.java,v 1.79 2013/01/15 14:48:28 rburgermann Exp $";


    /**
     * flags for checking whether dataelement changed or not
     */
    public boolean isChanged = false;

    /**
     * flags for checking whether dataelement matched or not
     */
    public boolean isMatched = false;

    /**
     *  oid of Object created with this importScripteEement. <BR/>
     */
    public OID oid = null;

    /**
     * Absolute base path of the m2 system. <BR/>
     */
    public String m2AbsBasePath = "";

    /**
     * Source path directory for physical files to be uploaded. <BR/>
     */
    public String sourcePath = "";

    /**
     *  Typename of object to be imported. <BR/>
     */
    public String typename = "";

    /**
     *  Type code of object to be imported. <BR/>
     */
    public String p_typeCode = "";

    /**
     *  Operation for the import of the object. <BR/>
     */
    public String operation = null;

    /**
     * Marks this object as a tab object. <BR/>
     * This information is used during the export process. <BR/>
     */
    public boolean p_isTabObject = false;

    /**
     * Type tab code for tab objects. <BR/>
     * This information is used during the export process. <BR/>
     * Code of Tab. Should be Typecode with Tabidentifier. <BR/>
     */
    public String p_tabCode = "";

    /**
     * Priority of tab. Tab with highest priority is. <BR/>
     * default tab of majorobject. <BR/>
     * Priority of info-tab = 9000. <BR/>
     * Priority of content-tab = 10000. <BR/>
     */
    public int p_priority = 0;

    //-----------------------------------------------------------------
    // The following attributs are only used for document templates!!
    //-----------------------------------------------------------------

    /**
     *  The name of the database table if db-mapped. <BR/>
     */
    public String tableName = "";

    /**
     *  Class name for this object type. <BR/>
     */
    public String p_className = "";

    /**
     *  Icon name for this object type. <BR/>
     */
    public String p_iconName = "";

    /**
     * This attribute holds a list of m2 container type codes. Objects of this types
     * can reside only in this container types. <BR/>
     */
    public String p_mayExistIn = "";

    /**
     *  Type code of the super object. <BR/>
     */
    public String p_superTypeCode = "";

    /**
     *  Should the object type be shown in the menu tree. <BR/>
     *  ATTENTION! This is the default value for document templates!
     */
    public boolean p_isShowInMenu = false;

    /**
     *  Default setting for the 'show in news' flag. <BR/>
     *  ATTENTION! This is the default value for document templates!
     */
    public boolean p_isShowInNews = true;

    /**
     *  Is the object type searchable. <BR/>
     *  ATTENTION! This is the default value for document templates!
     */
    public boolean p_isSearchable = true;

    /**
     *  Is the object type inheritable. <BR/>
     *  ATTENTION! This is the default value for document templates!
     */
    public boolean p_isInheritable = true;


    /**
     * If the template defines a container type this attribute
     * holds a list of type codes. Only objects of this types
     * can reside in this container type. <BR/>
     */
    public String p_mayContain = "";


    /**
     * A container template can define a alternative token for the
     * name column. This can be defined in the
     * &lt;COLUMNS NAMETOKEN="..."> attribute
     */
    public String p_nameToken = null;

    /**
     * A container template can define an extension query that is added
     * to the containers content query.
     * This can be defined in the
     * <pre>
     * <CONTENT>
     *         <EXTENSION QUERYNAME="..."/>
     * </CONTENT>
     * </pre>
     * attribute.
     */
    public String p_extensionQueryName = null;

    /**
     * If the template defines a container type this attribute
     * holds a list of field names used to compose the content
     * view of the container. <BR/>
     */
    public Vector<ReferencedObjectInfo> p_headerFields = new Vector<ReferencedObjectInfo> ();


    /**
     * This vector holds the 'OnInit' actions defined in the template. <BR/>
     */
    public Actions p_initActions = null;


    //
    // values from a CONFIG section:
    //

    /**
     * Comma-separated list of buttons to be displayed in info view. <BR/>
     * <CODE>null</CODE> means that the value was not set. <BR/>
     * Default: <CODE>null</CODE>
     */
    public String p_infoButtonList = null;

    /**
     * Comma-separated list of buttons to be displayed in content view. <BR/>
     * <CODE>null</CODE> means that the value was not set. <BR/>
     * Default: <CODE>null</CODE>
     */
    public String p_contentButtonList = null;

    /**
     * Transformation for the object. <BR/>
     * <CODE>null</CODE> means that the value was not set. <BR/>
     * Default: <CODE>null</CODE>
     */
    public String p_transformation = null;

    /**
     * Rule for creating the value of the object name. <BR/>
     * <CODE>null</CODE> means that the value was not set. <BR/>
     * Default: <CODE>null</CODE>
     */
    public String p_nameTemplate = null;


    //
    // values from a SYSTEM section:
    //

    /**
     * ID of Object to be imported. <BR/>
     */
    public String id = "";

    /**
     * Original id. This value is used, if the id is extended. <BR/>
     */
    private String p_origId = "";

    /**
     * ID-Domain of Object to be imported. <BR/>
     */
    public String idDomain = "";

    /**
     * Shall the workspace user be added to the id?. <BR/>
     * Default: <CODE>false</CODE>
     */
    public boolean idAddWspUser = false;

    /**
     *  Name of Object to be imported. <BR/>
     */
    public String name = "";

    /**
     *  Description of Object to be imported. <BR/>
     */
    public String description = "";

    /**
     *  ValidUntil Date of Object to be imported. <BR/>
     */
    public String validUntil = "";

    /**
     *  container type for this object. <BR/>
     */
    public String containerType = null;

    /**
     *  container name or oid for this object. <BR/>
     */
    public String containerId = null;

    /**
     *  the domain of an external container id. <BR/>
     */
    public String containerIdDomain = null;

    /**
     *  the name of a tab of the container. <BR/>
     */
    public String containerTabName = null;

    /**
     *  'show in news' flag for this object. <BR/>
     */
    public String showInNews = null;

    /**
     * show system section of the object in this manner. <BR/>
     */
    public int systemDisplayMode = DSP_MODE_TOP;

    //
    // values from a VALUES section
    //
    /**
     *  values to be imported. <BR/>
     */
    public Vector<ValueDataElement> values = new Vector<ValueDataElement> ();

    //
    // values from a RIGHTS section
    //
    /**
     *  rights to be imported. <BR/>
     */
    public Vector<RightDataElement> rights = new Vector<RightDataElement> ();

    //
    // values from TAB section
    //

    /**
     * Kind of TAB. Possible Values: VIEW, OBJECT, LINK, FCT. <BR/>
     */
    public String p_tabKind = DIConstants.TABKIND_OBJECT;

    //
    // values from a REFERENCES section
    //
    /**
     *  references to be created for this object. <BR/>
     */
    public Vector<ReferenceDataElement> references = new Vector<ReferenceDataElement> ();

    /**
     *  files used within this object. <BR/>
     */
    public Vector<FileDataElement> files = new Vector<FileDataElement> ();

    /**
     * this containes tab objects
     */
    public DataElementList tabElementList;

    /**
     * this containes underlying objects
     */
    public DataElementList dataElementList;

    /**
     * this containes possible xml attachments as DOM Documents
     */
    public Vector<Node> attachmentList = new Vector<Node> ();

    /**
     *  value for showing system section of the object on top (default). <BR/>
     */
    public static final int DSP_MODE_TOP = 0;

    /**
     * hide system section of the object. <BR/>
     */
    public static final int DSP_MODE_HIDE = 1;

    /**
     * show the system section of the object on bottom. <BR/>
     */
    public static final int DSP_MODE_BOTTOM = 2;

    /**
     * This is a buffer for an dataValueElement. The exists method writes
     * a valueDataElement into this buffer in case it finds the requested field.
     * The getValue method always checks this element first before it scans
     * all dataElement in the values vector. <BR/>
     */
    private ValueDataElement valueDataElementBuffer;

    /**
     * This is a buffer for a fileValueElement. <BR/>
     */
    private FileDataElement fileDataElementBuffer;

    /**
     * Flag set in the method setSystemValues if the given name is not null. <BR/>
     * This information is needed for the UPDATE import process.
     */
    private boolean p_isNameGiven = false;
    /**
     * Flag set in the method setSystemValues if the given description is not null. <BR/>
     * This information is needed for the UPDATE import process.
     */
    private boolean p_isDescriptionGiven = false;
    /**
     * Flag set in the method setSystemValues if the given validUntil is not null. <BR/>
     * This information is needed for the UPDATE import process.
     */
    private boolean p_isValidUntilGiven = false;
    /**
     * Flag set in the method setSystemValues if the given showInNews is not null. <BR/>
     * This information is needed for the UPDATE import process.
     */
    private boolean p_isShowInNewsGiven = false;

    /**
     * The hash code. <BR/>
     */
    private int p_hashCode = Integer.MIN_VALUE;


    /**************************************************************************
     * Creates a DataElement. <BR/>
     */
    public DataElement ()
    {
        // nothing to do
    } // DataElement


    /**************************************************************************
     * Set values of config section. <BR/>
     *
     * @param   infoButtonList  Comma-separated list of buttons to be displayed
     *                          in info view.
     * @param   contentButtonList  Comma-separated list of buttons to be
     *                          displayed in content view.
     * @param   transformation  Transformation for the object.
     * @param   nameTemplate    Rule for creating the value of the object name.
     */
    public void setConfigValues (String infoButtonList,
                                 String contentButtonList,
                                 String transformation,
                                 String nameTemplate)
    {
        // set the values:
        this.p_infoButtonList = infoButtonList;
        this.p_contentButtonList = contentButtonList;
        this.p_transformation = transformation;
        this.p_nameTemplate = nameTemplate;
    } // setConfigValues


    /**************************************************************************
     * Set values of systems section. <BR/>
     *
     * @param id                external id of business object
     * @param idDomain          domain od external id
     * @param idAddWspUser    Shall the workspace user be added to the id?
     * @param name              name of business object
     * @param description       description of business object
     * @param validUntil        validUntil date of business object as string
     * @param containerType     type of the container setting
     * @param containerId       container name or id
     * @param containerIdDomain domain of an external container id
     * @param containerTabName  name of a tab of a container
     * @param showInNews        showInNews flag
     * @param displayMode     displayMode for system values.
     */
    public void setSystemValues (String id,
                                 String idDomain,
                                 boolean idAddWspUser,
                                 String name,
                                 String description,
                                 String validUntil,
                                 String containerType,
                                 String containerId,
                                 String containerIdDomain,
                                 String containerTabName,
                                 String showInNews,
                                 int displayMode)
    {
        // the system attributes name, desc, validUntil and showInNews
        // can be null to indicate that this values are not present
        // in the import document. the properties cannot be set to null because
        // the implementation assumes that this properties have always a value.
        // so we have to store this information in the flags.
        this.p_isNameGiven = name != null;
        this.p_isDescriptionGiven = description != null;
        this.p_isShowInNews = showInNews != null;
        this.p_isValidUntilGiven = validUntil != null;

        // check the null values to avoid null pointer exceptions
        this.name = this.p_isNameGiven ? name : "";
        this.description = this.p_isDescriptionGiven ? description : "";
        this.validUntil = this.p_isValidUntilGiven ? validUntil : "";
        this.showInNews = this.p_isShowInNewsGiven ? showInNews : "";

        this.id = id;
        this.p_origId = id;
        this.idDomain = idDomain;
        this.idAddWspUser = idAddWspUser;
        this.containerType = containerType;
        this.containerId = containerId;
        this.containerIdDomain = containerIdDomain;
        this.containerTabName = containerTabName;
        this.systemDisplayMode = displayMode;
    } // setSystemValues


    /**************************************************************************
     * This method gets the isNameGiven. <BR/>
     *
     * @return  Returns <CODE>true</CODE> if the name property is valid.
     */
    public boolean isNameGiven ()
    {
        //get the property value and return the result:
        return this.p_isNameGiven;
    } // isNameGiven


    /**************************************************************************
     * This method gets the isDescriptionGiven. <BR/>
     *
     * @return  Returns <CODE>true</CODE> if the description property is valid.
     */
    public boolean isDescriptionGiven ()
    {
        //get the property value and return the result:
        return this.p_isDescriptionGiven;
    } // isDescriptionGiven


    /**************************************************************************
     * This method gets the isValidUntilGiven. <BR/>
     *
     * @return  Returns <CODE>true</CODE> if the validUntil property is valid.
     */
    public boolean isValidUntilGiven ()
    {
        //get the property value and return the result:
        return this.p_isValidUntilGiven;
    } // isValidUntilGiven


    /**************************************************************************
     * This method gets the isShowInNewsGiven. <BR/>
     *
     * @return  Returns <CODE>true</CODE> if the shoowInNews property is valid.
     */
    public boolean isShowInNewsGiven ()
    {
        //get the property value and return the result:
        return this.p_isShowInNewsGiven;
    } // isShowInNewsGiven


    /**************************************************************************
     * Extend the id with an extension. <BR/>
     * The extension is added to the original id and stored within the property
     * id. Subsequent calls always start again with the original id.
     *
     * @param   extension   The extension to be added to the id.
     */
    public void extendId (String extension)
    {
        this.id = this.p_origId + extension;
    } // extendId


    /**************************************************************************
     * add value to values vector. <BR/>
     *
     * @param elem ValueDataElement to add to DataElement.
     */
    public void addValue (ValueDataElement elem)
    {
        this.values.addElement (elem);
    } // addValue


    /**************************************************************************
     * Add a value to this data element. <BR/>
     *
     * @param   field           Name of the field.
     * @param   type            Type of field.
     * @param   value           Value of field.
     * @param   mandatory       Mandatory flag.
     * @param   readonly        Readonly flag.
     * @param   info            Info about the field.
     * @param   typeFilter      Type filter for an objectref field.
     * @param   searchRoot      Search root for an objectref field.
     * @param   searchRootIdDomain
     *                          Search root EXTKEY id domain for an objectref field.
     * @param   searchRootId    Search root EXTKEY id for an objectref field.
     * @param   searchRecursive Flag to search recusively in objectref fields.
     * @param   mappingField    Name of the field for the dbmapping.
     * @param   queryName       Name of the query.
     * @param   options         All possible values separated by a comma.
     * @param   valueUnit       Unit of the value.
     * @param   emptyOption     Telling if there is an empty item in the
     *                          selection box.
     * @param   refresh         Telling if the query is executed editing the
     *                          form.
     * @param   valueSubTags    Vector with any subtags for current value.
     * @param   domain          Value of the domain-attribute for the
     *                          fieldTypeExtKeys.
     * @param   size            Size of the value content.
     * @param   reminderParams  Parameters of reminder value.
     *                          Must be of class <CODE>Serializable</CODE>
     *                          because this is a common super class of
     *                          <CODE>String</CODE> and <CODE>Integer</CODE>.
     * @param   context         Defines the list of displayed ValueDomains.
     * @param   viewType        Defines the view type how to display selectionboxes.
     * @param   noColumns       Defines the how many columns should be used to render checklists.
     * @param   multiSelection  Defines the multi selection of a checklist.
     * @param   fileParams      Parameters of file value.
     *                          Must be of class <CODE>Serializable</CODE>
     *                          because this is a common super class of
     *                          <CODE>String</CODE> and <CODE>Integer</CODE>.
     * @param   mlKey           Defines the multilang lookup key for the Value field.
     */
    public void addValue (String field,
                          String type,
                          String value,
                          String mandatory,
                          String readonly,
                          String info,
                          String typeFilter,
                          String searchRoot,
                          String searchRootIdDomain,
                          String searchRootId,
                          String searchRecursive,
                          String mappingField,
                          String queryName,
                          String options,
                          String valueUnit,
                          String emptyOption,
                          String refresh,
                          Vector<?> valueSubTags,
                          String domain,
                          long size,
                          Vector<Serializable> reminderParams,
                          String context,
                          String viewType,
                          String noColumns,
                          String multiSelection,
                          Vector<Serializable> fileParams,
                          String mlKey,
                          String showInLinks)
    {

        // the field name cannot be empty
        if (field == null || field.isEmpty ())
        {
            return;
        } // if

        // the value may not be null
        // the type may not be null
        // create the value data element
        ValueDataElement valueDataElement = new ValueDataElement (field,
            type != null ? type : DIConstants.VTYPE_TEXT,
            value != null ? value : "", mandatory, readonly, info, typeFilter,
                    searchRoot, searchRootIdDomain, searchRootId, searchRecursive, mappingField, queryName,
                    options, valueUnit, emptyOption, refresh, domain, size,
                    valueSubTags, reminderParams, context, viewType, noColumns, multiSelection, fileParams,
                    mlKey, null, showInLinks);
        // add the value data element to the vector
        this.values.addElement (valueDataElement);
    } // addValue


    /**************************************************************************
     * add right to rights Vector. <BR/>
     *
     * @param name      name of user/group
     * @param type      user or group
     * @param profile    right profile
     *
     * @see DataElement#addValue (ValueDataElement)
     */
    public void addRight (String name, String type, String profile)
    {
        RightDataElement right;
        boolean isFound = false;

        // check first if this name and type already exist and add the profile
        // if found. This is used in order to cumulate the right aliases
        for (Iterator<RightDataElement> iter = this.rights.iterator (); iter.hasNext ();)
        {
            right = iter.next ();

            if (right.isEqual (name, type))
            {
                // found: just add the profile to the existing entry:
                right.addProfile (profile);
                // mark that we have found the required element:
                isFound = true;
                break;
            } // if
        } // for iter

        if (!isFound)
        {
            try
            {
                // create a new right import element
                right = new RightDataElement (name, type, profile);
                // and add it to the rights vector
                this.rights.addElement (right);
            } // try
            catch (Exception e)
            {
                // normally (!) there should no exception occur at all
            } // catch
        } // if
    } // addRight

    /**************************************************************************
     * Add reference to references vector. <BR/>
     *
     * @param containerType     type of container setting
     * @param containerId       container name or oid
     * @param containerIdDomain domain of external id
     * @param containerTabName  name of a tab of the container
     */
    public void addReference (String containerType,
                              String containerId,
                              String containerIdDomain,
                              String containerTabName)
    {
        try
        {
            // create a new ReferenceDataElement object
            ReferenceDataElement reference = new ReferenceDataElement (containerType,
                                                                       containerId,
                                                                       containerIdDomain,
                                                                       containerTabName);
            // add the object to the references vector
            this.references.addElement (reference);
        } // try
        catch (Exception e)
        {
            // normally (!) there should no exception occur at all
        } // catch
    } // addReference


    /**************************************************************************
     * Add a fileDataElement to the files Vector that holds the data of a
     * file to be imported or exported. <BR/>
     *
     * @param field         name of field that holds the filename
     * @param path          path of file
     * @param fileName      name of file
     * @param   fileSize    size of file
     *
     * @see FileDataElement
     */
    public void addFile (String field, String path, String fileName, long fileSize)
    {
        // check if a filename value has been set
        if (fileName != null && fileName.trim ().length () > 0)
        {
            this.files.addElement (new FileDataElement (field, path, fileName,
                fileSize));
        } // if
    } // addFile


    /**************************************************************************
     * Changes the value of a specific field. <BR/>
     *
     * @param field     name of field whose value shall be changed
     * @param newValue  the new value to set
     *
     * @return true if value could have been changed or false otherwise
     */
    public boolean changeValue (String field, String newValue)
    {
        ValueDataElement vie = this.getValueElement (field);
        // check if we could find the element
        if (vie != null)
        {
            // set the new value
            vie.value = newValue;
            return true;
        } // if

        return false;
    } //changeValue


    /**************************************************************************
     * Checks if a field exists and stores it in the valueDataElementBuffer
     * in case the field exists. <BR/>
     *
     * @param field     the field to be looked up in the values vector
     *
     * @return true if the value exists or false otherwise
     */
    public boolean exists (String field)
    {
        // look up the field
        ValueDataElement valueDataElement = this.getValueElement (field);
        // check if field has been found
        if (valueDataElement != null)
        {
            this.valueDataElementBuffer = valueDataElement;
            return true;
        } // if (valueDataElement != null)

        return false;
    } // exists


    /**************************************************************************
     * Checks if a file field exists. If yes the element is stored in the
     * fileDataElementBuffer. <BR/>
     *
     * @param field     the field to be looked up in the files vector
     *
     * @return true if the value exists or false otherwise
     */
    public boolean existsFile (String field)
    {
        FileDataElement fileDataElement;
        // lookup the file identified by the name of the field
        fileDataElement = this.getFileElement (field);
        // did we find the file
        if (fileDataElement != null)
        {
/* KR 20090923 this statement is redundant becaus it is already made within
               getFileElement.

            this.fileDataElementBuffer = fileDataElement;
*/
            return true;
        } // if (valueDataElement != null)

        return false;
    } // existsFile


//
// SET METHODS
//

    /***************************************************************************
     * Sets an string value in the VALUES vector. <BR/>
     *
     * @param field fieldname
     * @param value value of the field
     *
     * @see DataElement#addValue (String, String, String, String, String, String,
     *      String, String, String, String, String, String, String, String,
     *      String, Vector, String, long, Vector, String, String, String, Vector,
     *      String)
     */
    public void setExportValue (String field, String value)
    {
        // add the value to the values vector
        this.addValue (field, DIConstants.VTYPE_CHAR, value, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, -1,
            null, null, null, null, null, null, null, null);
    } // setExportValue


    /***************************************************************************
     * Sets an integer value in the VALUES vector. <BR/>
     *
     * @param field fieldname
     * @param value value of the field
     *
     * @see DataElement#addValue (String, String, String, String, String, String,
     *      String, String, String, String, String, String, String, String,
     *      String, Vector, String, long, Vector, String, String, String, Vector,
     *      String)
     */
    public void setExportValue (String field, int value)
    {
        this.addValue (field, DIConstants.VTYPE_INT, Integer.toString (value), null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, -1, null, null, null, null, null, null, null, null);
    } // setExportValue


    /**************************************************************************
     * Sets an datetime value in the VALUES vector. <BR/>
     *
     * @param field     fieldname
     * @param value     value of the field
     *
     * @see DataElement#addValue (String, String, String, String, String, String,
     *      String, String, String, String, String, String, String, String,
     *      String, Vector, String, long, Vector, String, String, String, Vector,
     *      String)
     */
    public void setExportValue (String  field, Date value)
    {
        // add the value to the values vector
        this.addValue (field, DIConstants.VTYPE_DATETIME, DateTimeHelpers.dateTimeToString (value),
            null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, -1, null, null, null, null, null, null,
            null, null);
    } // setExportValue


    /**************************************************************************
     * Sets an datetime value in the VALUES vector. <BR/>
     *
     * @param field     fieldname
     * @param value     value of the field
     *
     * @see DataElement#addValue (String, String, String, String, String, String,
     *      String, String, String, String, String, String, String, String,
     *      String, Vector, String, long, Vector, String, String, String, Vector,
     *      String)
     */
    public void setExportDateValue (String field, Date value)
    {
        // add the value to the values vector
        this.addValue (field, DIConstants.VTYPE_DATE, DateTimeHelpers.dateToString (value),
            null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, -1, null, null, null, null, null, null, null, null);
    } // setExportValue


    /**************************************************************************
     * Sets an time value in the VALUES vector. <BR/>
     *
     * @param field     fieldname
     * @param value     value of the field
     *
     * @see DataElement#addValue (String, String, String, String, String, String,
     *      String, String, String, String, String, String, String, String,
     *      String, Vector, String, long, Vector, String, String, String, Vector,
     *      String)
     */
    public void setExportTimeValue (String field, Date value)
    {
        // add the value to the values vector
        this.addValue (field, DIConstants.VTYPE_TIME, DateTimeHelpers.timeToString (value),
            null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, -1, null, null, null, null, null, null, null, null);
    } // setExportValue


    /**************************************************************************
     * Sets an boolean value in the VALUES vector. <BR/>
     *
     * @param field     fieldname
     * @param value     value of the field
     */
    public void setExportValue (String field, boolean value)
    {
        // add the value to the values vector
        if (value)
        {
            this.addValue (field, DIConstants.VTYPE_BOOLEAN, DIConstants.BOOL_TRUE,
                null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, -1, null, null, null, null, null, null,
                null, null);
        } // if
        else
        {
            this.addValue (field, DIConstants.VTYPE_BOOLEAN, DIConstants.BOOL_FALSE,
                null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, -1, null, null, null, null, null, null,
                null, null);
        } // else
    } // setExportValue


    /**************************************************************************
     * Sets an double value in the VALUES vector. <BR/>
     *
     * @param field     fieldname
     * @param value     value of the field
     */
    public void setExportValue (String  field, double value)
    {
        this.addValue (field, DIConstants.VTYPE_DOUBLE,
            Double.toString (value), null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, -1, null, null, null,
            null, null, null, null, null);
    } //setExportValue


    /**************************************************************************
     * Sets an float value in the VALUES vector. <BR/>
     *
     * @param field     fieldname
     * @param value     value of the field
     */
    public void setExportValue (String  field, float value)
    {
        this.addValue (field, DIConstants.VTYPE_FLOAT, Float.toString (value),
            null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, -1, null, null, null, null, null, null,
            null, null);
    } // setExportValue


    /**************************************************************************
     * Add a value entry and a file entry. This method it meant for any
     * field that use attachments. <BR/>
     *
     * @param field         fieldname
     * @param path          path of the file
     * @param fileName      name of the file
     */
    public void setExportFileValue (String field, String path, String fileName)
    {
        long size = FileHelpers.getFileSize (path, fileName);
        // we need to trim the filename in case it only exists of
        String trimmedFileName = fileName.trim ();
        // check if we need to add the value
        this.addValue (field, DIConstants.VTYPE_FILE, trimmedFileName, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null,
            size, null, null, null, null, null, null, null, null);
        // check if a filename has been set
        if (trimmedFileName != null && !trimmedFileName.isEmpty ())
        {
            // set the file in the list of files we exported
            this.addFile (field, path, trimmedFileName, size);
        } // if (fileName != null && fileName.length () > 0)
    } // setExportFileValue


    /**************************************************************************
     * Sets a physical File for the export. <BR/>
     *
     * @param field         fieldname
     * @param fileName      name of the file
     * @param path          path of the file
     * @param isAdd         switch to add the value if false update the value
     */
    public void setExportFileValue (String field, String fileName,
                                    String path, boolean isAdd)
    {
        long size = FileHelpers.getFileSize (path, fileName);

        // check if we need to add the value
        if (isAdd)
        {
            // add the value to the values vector
            this.addValue (field, DIConstants.VTYPE_FILE, fileName, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, size, null, null, null, null, null, null, null, null);
        } // if (isAdd)
        else // just update the field
        {
            ValueDataElement vie = this.getValueElement (field);
            // check if we got the element
            if (vie != null)
            {
                vie.value = fileName;
                vie.p_size = size;
            } // if
        } // else update the field
        // check if a filename has been set
        if (fileName != null && fileName.trim ().length () > 0)
        {
            // set the file in the list of files we exported
            this.addFile (field, path, fileName, size);
        } // if (fileName != null && fileName.length () > 0)
    } // setExportFileValue


    /**************************************************************************
     * Sets an hyperlink value for the export. <BR/>
     *
     * @param field     fieldname
     * @param value     value of the field
     */
    public void setExportHyperlinkValue (String field, String value)
    {
        // add the value to the values vector
        this.addValue (field, DIConstants.VTYPE_URL, value, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, -1,
            null, null, null, null, null, null, null, null);
    } //setExportHyperlinkValue

    /**************************************************************************
     * Sets an email value for the export. <BR/>
     *
     * @param field     fieldname
     * @param value     value of the field
     */
    public void setExportEmailValue (String field, String value)
    {
        // add the value to the values vector
        this.addValue (field, DIConstants.VTYPE_EMAIL, value, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null,
            -1, null, null, null, null, null, null, null, null);
    } //setExportEmailValue


    /**************************************************************************
     * Sets an EXTKEY for the export. <BR/>
     *
     * @param field     fieldname
     * @param value     value of the EXTKEY id
     * @param domain    value of the EXTKEY domain
     */
    public void setExportExtKeyValue (String field, String value, String domain)
    {
        this.addValue (field, DIConstants.VTYPE_EXTKEY, value, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null,
            domain, -1, null, null, null, null, null, null, null, null);
    } //setExportExtKeyValue

//
// GET METHODS
//


    /**************************************************************************
     * Returns a string value from the import VALUES vector. <BR/>
     *
     * @param field     name of the field to get the value from
     *
     * @return the value as string or "" in case field was null
     *
     * @see DataElement#getImportValue
     */
    public String getImportStringValue (String field)
    {
        String value = this.getImportValue (field);
        if (value != null)
        {
            return value;
        } // if

        return "";
    } // getImportStringValue


    /**************************************************************************
     * Returns a integer value from the import VALUES vector. <BR/>
     *
     * @param field     name of the field to get the value from
     *
     * @return the value as integer or null in case field does not exist
     *
     * @see DataElement#getImportValue
     */
    public int getImportIntValue (String field)
    {
        // get the value of the field
        String value = this.getImportValue (field);
        try
        {
            // create an integer value
            return Integer.parseInt (value);
        } // try
        catch (NumberFormatException e)
        {
            return 0;
        } // catch
    } // getImportIntValue


    /**************************************************************************
     * Returns a double value from the import VALUES vector. <BR/>
     *
     * @param field     name of the field to get the value from
     *
     * @return the value as integer or null in case field does not exist
     *
     * @see DataElement#getImportValue
     */
    public double getImportDoubleValue (String field)
    {
        // get the value of the field
        String value = this.getImportValue (field);
        try
        {
            // replace any "," by a "." because this could lead
            // to a conversion problem
            value = value.replace (',', '.');
            // create and return a double value
            return new Double (value).doubleValue ();
        } // try
        catch (NumberFormatException e)
        {
            return 0;
        } // catch
    } // getImportDoubleValue


    /**************************************************************************
     * Returns a field value from the import VALUES vector. <BR/>
     *
     * @param field     name of the field to get the value from
     *
     * @return the value as double or null in case field does not exist
     *
     * @see DataElement#getImportValue
     */
    public float getImportFloatValue (String field)
    {
        // get the value of the field
        String value = this.getImportValue (field);
        try
        {
            // replace any "," by a "." because this could lead
            // to a conversion problem
            value = value.replace (',', '.');
            // create and return a float value
            return new Float (value).floatValue ();
        } // try
        catch (NumberFormatException e)
        {
            return 0;
        } // catch
    } // getImportFloatValue


    /**************************************************************************
     * Returns a date value from the import VALUES vector. <BR/>
     *
     * @param field     name of the field to get the value from
     *
     * @return the value as date or null in case field does not exist
     *
     * @see DataElement#getImportValue
     */
    public Date getImportDateValue (String field)
    {
        Date date = null;
        // get the value of the field
        String value = this.getImportValue (field);
        // check if a value has been set
        if (value != null && !value.trim ().isEmpty ())
        {
            date = DateTimeHelpers.stringToDate (value);
        } // if

        // return the date as date object
        return date;
    } // getImportDateValue


    /**************************************************************************
     * Returns a datetime value from the import VALUES vector. <BR/>
     *
     * @param field     name of the field to get the value from
     *
     * @return the value as datetime or null in case field does not exist
     *
     * @see DataElement#getImportValue
     */
    public Date getImportDateTimeValue (String field)
    {
        Date date = null;
        // get the value of the field
        String value = this.getImportValue (field);
        // check if a value has been set
        if (value != null && !value.trim ().isEmpty ())
        {
            date = DateTimeHelpers.stringToDateTime (value);
        } // if

        // return the datetime as date object
        return date;
    } // getImportDateValue


    /**************************************************************************
     * Returns a datetime value from the import VALUES vector. <BR/>
     *
     * @param field     name of the field to get the value from
     *
     * @return the value as datetime or null in case field does not exist
     *
     * @see DataElement#getImportValue
     */
    public Date getImportTimeValue (String field)
    {
        Date date = null;
        // try to find the value by its field name
        String value = this.getImportValue (field);
        // check if we got a value
        if (value != null && !value.trim ().isEmpty ())
        {
            date = DateTimeHelpers.stringToTime (value);
        } // if

        // return the time as date object
        return date;
    } // getImportDateValue


    /**************************************************************************
     * Returns a boolean value from the import VALUES vector. <BR/>
     *
     * @param field     name of the field to get the value from
     *
     * @return the value as boolean
     *
     * @see DataElement#getImportValue
     * @see DataElement#resolveBooleanValue
     */
    public boolean getImportBooleanValue (String field)
    {
        // try to get the value identified by its field name
        String value = this.getImportValue (field);
        // get a boolean value and return it
        return DataElement.resolveBooleanValue (value);
    } // getImportBooleanValue


    /**************************************************************************
     * Returns the value of the name property. <BR/>
     *
     * @return the name
     *
     * @see DataElement#name
     */
    public String getName ()
    {
        return this.name;
    } // getName


    /**************************************************************************
     * Returns the value of the description property. <BR/>
     *
     * @return the desciption
     *
     * @see DataElement#description
     */
    public String getDescription ()
    {
        return this.description;
    } // getDescription


    /**************************************************************************
     * Returns the date value of the validUntil property. <BR/>
     *
     * @return the validUntil value as datevalue
     *
     * @see DataElement#validUntil
     */
    public Date getValidUntil ()
    {
        return DateTimeHelpers.stringToDate (this.validUntil);
    } // getValidUntil


    /**************************************************************************
     * Returns the boolean value of the showInNews property. <BR/>
     *
     * @return the showInNews value as boolean value
     *
     * @see DataElement#showInNews
     */
    public boolean getShowInNews ()
    {
        return DataElement.resolveBooleanValue (this.showInNews);
    } // getShowInNews


    /**************************************************************************
     * Returns the system section display mode of the object. <BR/>
     *
     * @return  The system section display mode.
     */
    public int getSystemSectionDisplayMode ()
    {
        return this.systemDisplayMode;
    } // getSystemSectionDisplayMode


    /**************************************************************************
     * Try to find a file entry identified by its field name and return the
     * name of the file. <BR/>
     *
     * @param field         name of field that holds the filename
     *
     * @return the name of the file or “” in case it could not be found
     *
     * @see FileDataElement
     */
    public String getFileName (String field)
    {
        FileDataElement fileDataElement;
        // lookup the file identified by the name of the field
        fileDataElement = this.getFileElement (field);
        // did we find the file
        if (fileDataElement != null)
        {
            return fileDataElement.fileName;
        } // if (fileDataElement != null)
        return "";
    } // getFileName


    /**************************************************************************
     * Try to find a file entry identified by its field name and return the
     * path + name of the file. <BR/>
     *
     * @param   field   name of field that holds the filename
     *
     * @return  the path + name of the file or
     *          <CODE>""</CODE> in case it could not be found
     *
     * @see FileDataElement
     */
    public String getFilePathName (String field)
    {
        FileDataElement fileDataElement;
        // lookup the file identified by the name of the field
        fileDataElement = this.getFileElement (field);
        // did we find the file
        if (fileDataElement != null)
        {
            // compute the full path:
            return FileHelpers.makeFileNameValid (
                    fileDataElement.path + File.separator +
                    fileDataElement.fileName);
        } // if (fileDataElement != null)
        return "";
    } // getFilePathName


    /**************************************************************************
     * Try to find a file entry identified by its field name and return the
     * path of the file. <BR/>
     *
     * @param   field   name of field that holds the filename
     *
     * @return  the path + name of the file or
     *          <CODE>""</CODE> in case it could not be found
     *
     * @see FileDataElement
     */
    public String getFilePath (String field)
    {
        FileDataElement fileDataElement;
        // lookup the file identified by the name of the field
        fileDataElement = this.getFileElement (field);
        // did we find the file
        if (fileDataElement != null)
        {
            // compute the full path:
            return FileHelpers.makeFileNameValid (fileDataElement.path);
        } // if (fileDataElement != null)
        return "";
    } // getFilePath


    /**************************************************************************
     * Try to find a file entry identified by its field name and return the
     * size of the file. <BR/>
     *
     * @param field         name of field that holds the filename
     *
     * @return the size of the file or -1 in case it could not be found
     *
     * @see FileDataElement
     */
    public long getFileSize (String field)
    {
        FileDataElement fileDataElement;
        // lookup the file identified by the name of the field
        fileDataElement = this.getFileElement (field);
        // did we find the file
        if (fileDataElement != null)
        {
            return fileDataElement.size;
        } // if

        return -1;
    } // getFileSize


    /**************************************************************************
     * Copies a file from an importsource to the object directory. <BR/>
     *
     * @param field     name of the field to get the value from
     *
     * @return the filesize or null in case the file does not exist
     */
/*
    public long getImportFile (String fileName)
    {
        // create the target filepath
        String targetPath = this.m2AbsBasePath +
            ibs.app.AppConstants.PATH_UPLOAD_ABS_FILES +
            this.oid.toString () + File.separatorChar;
        return createImportFile (this.sourcePath, fileName, targetPath);
    } // getImportFile
*/

    /**************************************************************************
     * Copies a file from an importsource to the directory specified by the
     * targetPath parameter. <BR/>
     *
     * @param fileName          name of the file to import
     * @param targetPath        path to write the
     *
     * @return the filesize or null in case the file does not exist
     */
/*
    public long getImportFile (String fileName, String targetPath)
    {
        return createImportFile (this.sourcePath, fileName, targetPath);
    } // getImportFile
*/

    /**************************************************************************
     * Returns a value of a specific field from the import VALUES vector. <BR/>
     *
     * @param field     name of the field to get the value from
     *
     * @return the value as string or "" in case field does not exist
     */
    public String getImportValue (String field)
    {
        // try to get the value
        ValueDataElement vie = this.getValueElement (field);
        if (vie != null)
        {
            return vie.value;
        } // if

        return null;
    } // getImportValue


    /**************************************************************************
     * Returns a ValueDataElement from the import VALUES vector.
     * this method looks up the VALUES vector and tries to find the element
     * via the fieldname. <BR/>
     *
     * Note: Has to be declared public because it is used in the
     * ibs.di.XMLViewerContainerElement_01 class
     *
     * @param field     name of the ValueDataElement
     *
     * @return the ValueDataElement if found or null otherwise
     */
    public ValueDataElement getValueElement (String field)
    {
        // first check the buffer
        if (this.valueDataElementBuffer != null &&
            this.valueDataElementBuffer.field.equals (field))
        {
            return this.valueDataElementBuffer;
        } // if (valueDataElementBuffer != null && valueDataElementBuffer.field.equals (field))

        // field not found in buffer
        ValueDataElement vie;
        for (Iterator<ValueDataElement> iter = this.values.iterator (); iter.hasNext ();)
        {
            vie = iter.next ();

            if (vie.field.equals (field))
            {
                // store the value element in the buffer in order to fasten
                // a second access
                this.valueDataElementBuffer = vie;
                // return the value element
                return vie;
            } // if (vie.field.equalsIgnoreCase (field))
        } // for iter

        // not found: return null
        return null;
    } // getValueElement


    /**************************************************************************
     * Returns a FileDataElement identified by a field name. <BR/>
     * This method first checks a buffer that holds that last FileDataElement
     * that has been accessed via this method. In case the buffer does not
     * hold the right element the files vector of the dataElement will be
     * scanned. <BR/>
     *
     * @param field     name of the field that identifies
     *
     * @return the FileDataElement if found or null otherwise
     *
     * @see FileDataElement
     */
    private FileDataElement getFileElement (String field)
    {
        // first check the buffer
        if (this.fileDataElementBuffer != null &&
            this.fileDataElementBuffer.field.equals (field))
        {
            return this.fileDataElementBuffer;
        } // if (this.fileDataElementBuffer != null && …

        // field not found in buffer
        FileDataElement fileDataElement;
        // scan the files vector:
        for (Iterator<FileDataElement> iter = this.files.iterator (); iter.hasNext ();)
        {
            fileDataElement = iter.next ();

            // is it the file element with th4e right fieldname?
            if (fileDataElement.field.equals (field))
            {
                // store the file element we found in the buffer
                this.fileDataElementBuffer = fileDataElement;
                // return the file element
                return fileDataElement;
            } // if (fileDataElement.field.equalsIgnoreCase (field))
        } // for iter

        // return null because the vector has been scanned without success
        return null;
    } // getFileElement


    /**************************************************************************
     * Copies a file from an importsource to the directory specified by the
     * targetPath parameter. <BR/>
     *
     * @param sourcePath        the path the file should be copied from
     * @param fileName          the name of the source file
     * @param targetPath        the path to copy the import file to
     *
     * @return  the filesize or null in case the file does not exist
     */
/*
    public long createImportFile (String sourcePath, String fileName,
                                  String targetPath)
    {
        String sourcePathName;
        String targetFileName;

        // check for file separators
        FileHelpers.addEndingFileSeparator (sourcePath);
        FileHelpers.addEndingFileSeparator (targetPath);

        // construct the full source path
        sourcePathName = sourcePath + fileName;
        // check if source file exists
        if (FileHelpers.exists (sourcePathName))
        {
            // set the target file name
            // the target filename will be composed of "OID + filename"
            targetFileName = this.oid.toString () + fileName;
            // try to create the target directory
            if (FileHelpers.makeDir (targetPath))
            {
                // check if target file already exists
                if (FileHelpers.exists (targetPath + targetFileName))
                {
                    // prepare to overwrite the file
                    // delete the target file first
                    FileHelpers.deleteFile (targetPath + targetFileName);
                } // if (FileHelpers.exists (targetPath + targetFileName))
                // now copy the source file to the target destination
                if (FileHelpers.copyFile (sourcePathName, targetPath + targetFileName))
                {
                    return FileHelpers.getFileSize (targetPath, targetFileName);
                } // if (FileHelpers.copyFile (sourcePathName, targetPath + targetFileName))
                else    // file could not be copied
                {
                    return -1;
                } // else file could not be copied
            } // if (FileHelpers.makeDir (targetPath))
            else
            {
                // target path could not be created
                return -1;
            } // else if (FileHelpers.makeDir (targetPath))
        } // if (FileHelpers.exists (sourcePathName))
        else
        {
            // source file does not exist
            return -1;
        } // else  if (FileHelpers.exists (sourcePathName))
    } // createImportFile
*/

    /**************************************************************************
     * Resolves the boolean value of a string. <BR/>
     * The string must match one of the valid boolean values defined
     * in DIConstants.BOOL_TRUE_VALUES. If a match has been found the
     * method will return true. If the string is null or does not match
     * the method will return false. <BR/>
     *
     * @param value     the value to be resolved as boolean value
     *
     * @return true if the value matches one of the valid true values
     *         or false otherwise
     *
     * @see DIConstants#BOOL_TRUE_VALUES
     */
    public static boolean resolveBooleanValue (String value)
    {
        if (value != null && value.trim ().length () > 0 &&
            DIConstants.BOOL_TRUE_VALUES.indexOf (value.toUpperCase ()) != -1)
        {
            return true;
        } // if (value != null && value.trim ().length () > 0 &&

        // the value did not match one of the valid true values
        return false;
    } // resolveBooleanValue


   /***************************************************************************
    * Takes the Oid String out of a String which looks like: "oid,name". <BR/>
    * For the option "selection" was taken a value which is composed from oid
    * and name. We often only need the oid and so we select the oid from the
    * whole string. The string is separated with a komma. <BR/>
    *
    * @param    composedString  The composed String (like "oid,name").
    *
    * @return   The oid string.
    */
    public String getSelectionOidValue (String composedString)
    {
        String oidString = "";
        OID emptyOid = null;
        OID selObjOid = emptyOid;
        int indexDelim = 0;

        // try to extract the value we want to display
        // note that selection value uses the form
        // (oid), (name)
        if (composedString != null && composedString.length () > 0)
        {
            // get the index of the delimiter
            indexDelim = composedString.indexOf (DIConstants.OPTION_DELIMITER);
            // if a delimiter was found, it must be a valid value
            if (indexDelim > 0)
            {
                // extract the name
/*
                selObjName = composedString.substring (indexDelim + 1);
*/
                // extract the oid
                try
                {
                    selObjOid = new OID (composedString.substring (0, indexDelim));
                    oidString = selObjOid.toString ();
                } // try
                catch (ibs.bo.IncorrectOidException e)
                {
                    // reset the values
/*
                    selObjName = "";
*/
                    selObjOid = emptyOid;
                } // catch
            } // if (indexDelim > 0)
        } // if (vie.value != null && vie.value.length () > 0)
        return oidString;
    } // getSelectionOidValue


    /**************************************************************************
     * Copy the values of another object to this one. <BR/>
     * This method runs through all defined value elements and tries to get the
     * corresponding value for each value element out of the other data element.
     * Then this value is stored in the local value element.
     *
     * @param   obj     The other data element.
     */
    public void copyValues (DataElement obj)
    {
        IOHelpers.printMessage (this.name + ": copying values...");

        // loop through all values and try to copy each of them:
        for (Iterator<ValueDataElement> iter = this.values.iterator (); iter.hasNext ();)
        {
            ValueDataElement valueElem = iter.next ();

            // now search for the value in the other data element and copy the
            // value:
            valueElem.copyValue (obj.getValueElement (valueElem.field));
        } // for iter
    } // copyValues


    /**************************************************************************
     * Clone a String. <BR/>
     * This method is used from the {@link #clone () clone} method.
     *
     * @param   str     The string to be cloned.
     *
     * @return  The cloned string.
     *          <CODE>null</CODE> if the cloned string is <CODE>null</CODE>.
     */
    private String cloneString (String str)
    {
        String cloneStr = null;
        cloneStr = (str != null) ? new String (str) : null;
        return cloneStr;
    } // cloneString


    /**************************************************************************
     * Creates and returns a copy of this object. <BR/>
     *
     * @return     a clone of this instance.
     *
     * @see Object#clone ()
     */
    @SuppressWarnings ("unchecked")
    public Object clone ()
    {
        try
        {
            DataElement clone = (DataElement) super.clone ();

            clone.isChanged = this.isChanged;
            clone.isMatched = this.isMatched;
            if (this.oid != null)
            {
                clone.oid = (OID) (this.oid.clone ());
            } // if
            clone.m2AbsBasePath = this.cloneString (this.m2AbsBasePath);
            clone.sourcePath = this.cloneString (this.sourcePath);
            clone.typename = this.cloneString (this.typename);
            clone.p_typeCode = this.cloneString (this.p_typeCode);
            clone.p_isTabObject = this.p_isTabObject;
            clone.p_tabCode = this.cloneString (this.p_tabCode);
            clone.tableName = this.cloneString (this.tableName);
            clone.p_className = this.cloneString (this.p_className);
            clone.p_iconName = this.cloneString (this.p_iconName);
            clone.p_mayExistIn = this.cloneString (this.p_mayExistIn);
            clone.p_superTypeCode = this.cloneString (this.p_superTypeCode);
            clone.p_isShowInMenu = this.p_isShowInMenu;
            clone.p_isShowInNews = this.p_isShowInNews;
            clone.p_isSearchable = this.p_isSearchable;
            clone.p_isInheritable = this.p_isInheritable;
            clone.p_mayContain = this.cloneString (this.p_mayContain);
            clone.p_headerFields = (Vector<ReferencedObjectInfo>) this.p_headerFields.clone ();
            clone.p_initActions = this.p_initActions;
            clone.id = this.cloneString (this.id);
            clone.p_origId = this.cloneString (this.p_origId);
            clone.containerType = this.cloneString (this.containerType);
            clone.idDomain = this.cloneString (this.idDomain);
            clone.idAddWspUser = this.idAddWspUser;
            clone.name = this.cloneString (this.name);
            clone.description = this.cloneString (this.description);
            clone.validUntil = this.cloneString (this.validUntil);
            clone.containerId = this.cloneString (this.containerId);
            clone.containerIdDomain = this.cloneString (this.containerIdDomain);
            clone.containerTabName = this.cloneString (this.containerTabName);
            clone.showInNews = this.cloneString (this.showInNews);
            clone.rights = (Vector<RightDataElement>) this.rights.clone ();
            clone.files = (Vector<FileDataElement>) this.files.clone ();
            clone.tabElementList = this.tabElementList;
            clone.dataElementList = this.dataElementList;
            clone.attachmentList = (Vector<Node>) this.attachmentList.clone ();
            clone.p_isNameGiven = this.p_isNameGiven;
            clone.p_isDescriptionGiven = this.p_isDescriptionGiven;
            clone.p_isValidUntilGiven = this.p_isValidUntilGiven;
            clone.p_isShowInNewsGiven = this.p_isShowInNewsGiven;
            clone.p_nameToken = this.p_nameToken;            
            // do not clone the buffers in order to avoid wrong pointers:
            clone.valueDataElementBuffer = null;
            clone.fileDataElementBuffer = null;

            // clone values
            clone.values = new Vector<ValueDataElement> ();
            ValueDataElement cloneValue = null;
            for (int i = 0; this.values != null && i < this.values.size (); i++)
            {
                cloneValue = new ValueDataElement (this.values.elementAt (i));
                clone.values.addElement (cloneValue);
            } // for

            return clone;
        } // try
        catch (Exception e)
        {
            return null;
        } // catch
    } // clone


    /**************************************************************************
     * Compares this DataElement to another DataElement. <BR/>
     * The result is <CODE>true</CODE> if and only if the argument is not
     * <CODE>null</CODE> and is a DataElement object that has the same
     * values as this object. This means that both the system values
     * (&lt;SYSTEM> section of dom tree) and the specific values
     * (&lt;VALUES> section of dom tree) are compared.
     *
     * @param   obj     The DataElement object to compare this DataElement
     *                  against.
     *
     * @return  <CODE>true</CODE> if the DataElements are equal;
     *          <CODE>false</CODE> otherwise.
     */
    public boolean equals (Object obj)
    {
        boolean isEqual = true;

        // check for null:
        if (obj == null)
        {
            return false;               // not equal
        } // if

        if (obj instanceof DataElement) // DataElement object?
        {
            DataElement otherObj = (DataElement) obj;

            // check for equality; compare system values:
            isEqual =
/* KR just equal for the same object, not for a copy
                (this.id == null && otherObj.id == null ||
                 this.id != null && this.id.equals (otherObj.id)) &&
                ((this.oid == null && otherObj.oid == null) ||
                 (this.oid != null && this.oid.equals (otherObj.oid))) &&
*/
                ((this.name == null && otherObj.name == null) ||
                 (this.name != null && this.name.equals (otherObj.name))) &&
                ((this.description == null && otherObj.description == null) ||
                 (this.description != null &&
                  this.description.equals (otherObj.description))) &&
                ((this.showInNews == null && otherObj.showInNews == null) ||
                 (this.showInNews != null && this.showInNews.length () == 0) ||
                 (otherObj.showInNews != null && otherObj.showInNews.length () == 0) ||
                 (this.showInNews != null &&
                  this.showInNews.equals (otherObj.showInNews))) &&
                ((this.validUntil == null && otherObj.validUntil == null) ||
                 (this.validUntil != null &&
                  this.validUntil.equals (otherObj.validUntil)));

            // compare specific values:
            if (isEqual)
            {
                if (this.values.size () == otherObj.values.size ())
                                        // same number of values?
                {
                    // compare all fields with the fields of the other data element:
                    for (Iterator<ValueDataElement> iter = this.values.iterator ();
                         isEqual && iter.hasNext ();)
                    {
                        ValueDataElement value = iter.next ();

                        // search for the value within the master:
                        ValueDataElement otherValue =
                            otherObj.getValueElement (value.field);

                        if (otherValue != null) // value exists?
                        {
                            // compare the values of the elements:
                            // for files and images we currently cannot check
                            // if they were changed. So they are always assumed
                            // to be different if the size is different.
                            if ((value.value == null && otherValue.value != null) ||
                                (value.value != null &&
                                 !value.value.equals (otherValue.value)) ||
                                ((value.type.equals (DIConstants.VTYPE_FILE) ||
                                  value.type.equals (DIConstants.VTYPE_IMAGE)) &&
                                  value.p_size != otherValue.p_size))
                            {
                                isEqual = false;
                            } // if
                        } // if value exists
                        else            // value does not exist
                        {
                            // the object has another structure, so it is not
                            // the same:
                            isEqual = false;
                        } // else
                    } // for iter
                } // if same number of values
                else                    // different number of values
                {
                    isEqual = false;
                } // else different number of values
            } // if
        } // if DataElement object
        else                            // no DataElement object
        {
            isEqual = false;
        } // if no DataElement object

        // return the result:
        return isEqual;
    } // equals


    /**************************************************************************
     * Returns a hash code value for the object. <BR/>
     *
     * @return  A hash code value for this object.
     */
    public int hashCode ()
    {
        // check if a valid hash code was set:
        if (this.p_hashCode == Integer.MIN_VALUE)
        {
            // check if the oid is set:
            if (this.oid != null && !this.oid.isEmpty ())
            {
                // compute hash code from the type code:
                this.p_hashCode = this.oid.hashCode ();
            } // if
            // check if the id is set:
            else if (this.id != null && this.id.length () > 0)
            {
                // set hash code from the id:
                this.p_hashCode = this.id.hashCode ();
            } // else if
            // check if the orig id is set:
            else if (this.p_origId != null && this.p_origId.length () > 0)
            {
                // compute hash code from the type code:
                this.p_hashCode = this.p_origId.hashCode ();
            } // else if
            // check if the name is set:
            else if (this.name != null && this.name.length () > 0)
            {
                // compute hash code from the name:
                this.p_hashCode = this.name.hashCode ();
            } // else if
            // check if the type code is set:
            else if (this.p_typeCode != null && this.p_typeCode.length () > 0)
            {
                // compute hash code from the type code:
                this.p_hashCode = this.p_typeCode.hashCode ();
            } // else if
        } // if

        // return the result:
        return this.p_hashCode;
    } // hashCode

    
    /***************************************************************************
     * Remove a value with a given name.
     * 
     * @param fieldName
     *            The fieldname of the value to be removed.
     */
    public void removeValue (String fieldName)
    {
        // check contraints
        if (fieldName != null && values != null && values.size () > 0)
        {
            for (int i = 0; i < values.size (); ++i)
            {
                ValueDataElement value = values.get (i);
                if (fieldName.equals (value.field))
                {
                    values.remove (i);
                    break;
                } // if 
            } // for 
        } // if
    } // removeField

    
    /***************************************************************************
     * Initializes the multilang data element info for all provided locales.
     * <BR/>
     *
     * @param   locales  The locales to init the multilang info for
     * @param   env      The environment
     */
    public void initMultilangInfo (Collection<Locale_01> locales, Environment env)
    {       
        // iterate through all value data elements and intialize the multilang info
        Iterator<ValueDataElement> it = this.values.iterator ();
        while (it.hasNext ())
        {
            ValueDataElement vde = it.next ();
            vde.initMultilangInfo (locales, this.p_typeCode, env);
        } // while        
    } // initMultilangInfo
    
    
    /***************************************************************************
     * Initializes the multilang data element info for references for
     * all provided locales. <BR/>
     *
     * @param   locales  The locales to init the multilang info for
     * @param   env      The environment
     */
    public void initMultilangReferenceInfo (Collection<Locale_01> locales, Environment env)
    {       
        // load the multilang info for reference fields
        this.performLoadMultilangRefFieldInfo (locales, env);
        
        // load the multilang info for containers
        this.performLoadMultilangContainerInfo (locales, env);
    } // initMultilangReferenceInfo
    

    /***************************************************************************
     * Initializes the multilang data element info for reference fields for
     * all provided locales. <BR/>
     *
     * @param   locales  The locales to init the multilang info for
     * @param   env      The environment
     */
    private void performLoadMultilangRefFieldInfo (Collection<Locale_01> locales, Environment env)
    {       
        // iterate through all value data elements and intialize the multilang info
        Iterator<ValueDataElement> it = this.values.iterator ();
        while (it.hasNext ())
        {
            ValueDataElement vde = it.next ();

            // Add multilang info for reference fields
            ValueDataElementTS.addRefFieldMlInfo (this.p_typeCode, vde, locales, env);
        } // while        
    } // performLoadMultilangRefFieldInfo
    
    
    /***************************************************************************
     * Initializes the multilang data element container info for all provided
     * locales. <BR/>
     *
     * @param   locales  The locales to init the multilang info for
     * @param   env      The environment
     */
    private void performLoadMultilangContainerInfo (Collection<Locale_01> locales,
            Environment env)
    {        
        Vector<Type> types = null;
        
        // check if any header fields (is a container)
        if (p_headerFields != null)
        {
            try
            {
                // get the types from the may contain declaration:
                types = ((ObjectPool) env.getApplicationInfo ().cache).
                    getTypeContainer ().findTypes(this.p_mayContain);
            } // try
            catch (TypeNotFoundException e)
            {
                // do nothing
            } // catch
        
            boolean completed = false;
            
            Iterator<ReferencedObjectInfo> it = p_headerFields.iterator ();
            while (it.hasNext ())
            {
                ReferencedObjectInfo oInfo = it.next ();
              
                // reset the mulitlang tokens for the reference object info
                oInfo.resetMultilangTokens ();
                                                
                // (1) Generic lookup  
                String lookupKey = MultilingualTextProvider.
                    getColumnBaseLookupKey (this.p_typeCode, oInfo.getName ());

                completed = oInfo.setMlTokens (lookupKey, locales, env);    
                
                // not completed?
                if (!completed)
                {
                    // (2) Use token
                    if (oInfo.getToken () != null && !oInfo.getToken ().isEmpty ())
                    {
                        oInfo.setMultilangTokenForLocales (new MlInfo (oInfo.getToken ()), locales, env);
                    } // if
                    // (3) Use type info
                    else
                    {
                        // check if it is a system field
                        if (oInfo.getType () != ReferencedObjectInfo.TYPE_SYSTEM)
                        {
                            // loop through all types until something has been found
                            for (int i = 0; types != null && i < types.size (); i++)
                            {
                                // retrieve the first type
                                Type type = types.get (i);
                                
                                // retrieve the multilang field info from the current type
                                Map<String, MlInfo> mlInfos = type.getMultilangFieldInfo (oInfo.getName ());
                                
                                // check if something has been found
                                if (mlInfos != null)
                                {
                                    // link the referenced object info ML names to
                                    // the Value Data Element Names 
                                    oInfo.setMultilangTokens (mlInfos);
                                    
                                    completed = true;
                                    
                                    break;
                                } // if
                            } // for
                        } // if (columnType != FieldRefInfo.TYPE_SYSTEM))
                        
                        // (4) Use name - Can occur if no type is found containing the field
                        if (!completed)
                        {
                            // set the multilang token
                            oInfo.setMultilangTokenForLocales (null, locales, env);
                        } // else
                    } // else
                } // if
            } // while
        } // if   
    } // performLoadMultilangContainerInfo
  
} // class DataElement

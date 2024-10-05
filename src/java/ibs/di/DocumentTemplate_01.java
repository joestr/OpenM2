/*
 * Class: DocumentTemplate_01.java
 */

// package:
package ibs.di;

// imports:
//KR TODO: unsauber
import ibs.app.AppFunctions;
import ibs.app.ApplicationContext;
import ibs.app.CssConstants;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.BusinessObjectInfo;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.ObjectNotAffectedException;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.bo.States;
import ibs.bo.type.ITemplate;
import ibs.bo.type.Type;
import ibs.bo.type.TypeConstants;
import ibs.bo.type.TypeNotFoundException;
import ibs.di.filter.m2XMLFilter;
import ibs.di.service.DBMapper;
import ibs.di.service.ServiceMessages;
import ibs.io.Environment;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.io.servlet.ApplicationInitializationException;
import ibs.io.session.ApplicationInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.doc.DocConstants;
import ibs.obj.doc.File_01;
import ibs.obj.ml.Locale_01;
import ibs.service.user.User;
import ibs.tech.html.BuildException;
import ibs.tech.html.FormElement;
import ibs.tech.html.IE302;
import ibs.tech.html.InputElement;
import ibs.tech.html.Page;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.DBQueryException;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.SelectQuery;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.tech.xml.XMLHelpers;
import ibs.tech.xml.XMLWriter;
import ibs.tech.xml.XMLWriterException;
import ibs.tech.xslt.XSLTTransformationException;
import ibs.tech.xslt.XSLTTransformer;
import ibs.util.AlreadyDeletedException;
import ibs.util.DateTimeHelpers;
import ibs.util.DependentObjectExistsException;
import ibs.util.Helpers;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilExceptions;
import ibs.util.file.FileHelpers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/*******************************************************************************
 * This class represents one object of type DocumentTemplate with version 01. A
 * DocumentTemplate object holds an XML file that serves as a template for new
 * document types. DocumentTemplates act like file objects that store single
 * files. XMLViewer objects read these XML files to build their document
 * structure upon the definiton in the XML file. <BR/>
 * 
 * @version $Id: DocumentTemplate_01.java,v 1.85 2009/09/10 13:49:23 btatzmann
 *          Exp $
 * 
 * @author Bernd Buchegger (BB) 990309
 *         *****************************************************************************
 */
public class DocumentTemplate_01 extends File_01 implements ITemplate
{
	/**
	 * Version info of the actual class. <BR/> This String contains the version
	 * number, date, and author of the last check in to the code versioning
	 * system. This is implemented as CVS tag to ensure that it is automatically
	 * updated by the cvs system.
	 */
	public static final String VERSIONINFO = "$Id: DocumentTemplate_01.java,v 1.97 2013/01/18 10:38:17 rburgermann Exp $";

	/***************************************************************************
	 * This class.... <BR/>
	 * 
	 * @version $Id: DocumentTemplate_01.java,v 1.85 2009/09/10 13:49:23
	 *          btatzmann Exp $
	 * 
	 * @author ???, ??.??.????
	 *         *************************************************************************
	 */
	private class TabTemplate {
		/**
		 * kind of tab
		 */
		public int p_kind = 2; // Object tab
		/**
		 * function associated with the tab
		 */
		public int p_fct = AppFunctions.FCT_SHOWOBJECT;
		/**
		 * tab priority, i.e. sequence in which to display the tabs
		 */
		public int p_priority = 1;
		/**
		 * tVersionId of type which implements the tab
		 */
		public int p_tVersionId = 0;
		/**
		 * tab name
		 */
		public String p_name = "";
		/**
		 * tab description
		 */
		public String p_desc = "";
		/**
		 * unique tab code
		 */
		public String p_tabCode = "";
		/**
		 * class of tab
		 */
		public String p_class = "";

		/***********************************************************************
		 * Create instance of class TabTemplate. <BR/>
		 * 
		 * @param tVersionId
		 *            Version for the tab template.
		 * @param name
		 *            Name of the template.
		 * @param desc
		 *            Description.
		 * @param tabCode
		 *            Tab code. Must be unique.
		 * @param priority
		 *            Priority within tab bar.
		 */
		public TabTemplate(int tVersionId, String name, String desc,
				String tabCode, int priority) {
			this.p_tVersionId = tVersionId;
			this.p_name = name;
			this.p_desc = desc;
			this.p_tabCode = tabCode;
			this.p_priority = priority;
		} // TabTemplate

		/***********************************************************************
		 * Create instance of class TabTemplate. <BR/>
		 * 
		 * @param tVersionId
		 *            Version for the tab template.
		 * @param name
		 *            Name of the template.
		 * @param desc
		 *            Description.
		 * @param tabCode
		 *            Tab code. Must be unique.
		 * @param tabKind
		 *            Kind of tab.
		 * @param fct
		 *            Function to be executed when clicking on tab.
		 * @param tabClass
		 *            Class of tab.
		 * @param priority
		 *            Priority within tab bar.
		 */
		public TabTemplate(int tVersionId, String name, String desc,
				String tabCode, int tabKind, int fct, String tabClass,
				int priority) {
			this.p_tVersionId = tVersionId;
			this.p_name = name;
			this.p_desc = desc;
			this.p_kind = tabKind;
			this.p_fct = fct;
			this.p_tabCode = tabCode;
			this.p_priority = priority;
			this.p_class = tabClass;
		} // TabTemplate
	} // TabTemplate

	/***************************************************************************
	 * This class represents the information of an object which implements the
	 * current template. <BR/>
	 * 
	 * @version $Id: DocumentTemplate_01.java,v 1.85 2009/09/10 13:49:23
	 *          btatzmann Exp $
	 * 
	 * @author Klaus Reimüller (KR) 20090823
	 *         *************************************************************************
	 */
	private class ObjInfo {
		/**
		 * Translation state: before start. <BR/>
		 */
		public static final int ST_INIT = 1;
		/**
		 * Translation state: during test translation. <BR/>
		 */
		public static final int ST_TRANSTEMP = 2;
		/**
		 * Translation state: test translation finished. <BR/>
		 */
		public static final int ST_TRANSTEMP_FINISHED = 3;
		/**
		 * Translation state: during permanent translation. <BR/>
		 */
		public static final int ST_TRANSPERMANENT = 4;
		/**
		 * Translation state: translation successfully finished. <BR/>
		 */
		public static final int ST_FINISHED = 5;
		/**
		 * Translation state: translation finished with errors. <BR/>
		 */
		public static final int ST_FINISHED_ERRORS = 6;
		/**
		 * Translation state: object not in mapping table. <BR/>
		 */
		public static final int ST_NOT_IN_MAPPING_TABLE = 7;

		/**
		 * Name of original data file. <BR/>
		 */
		public static final String FILE_XMLDATAORIG = "xmldata_orig.xml";
		/**
		 * Name of translated data file. <BR/>
		 */
		public static final String FILE_XMLDATATRANS = "xmldata_trans.xml";

		/**
		 * The basic business object info. <BR/>
		 */
		public BusinessObjectInfo p_objInfo = null;

		/**
		 * The file path of the object. <BR/>
		 */
		public String p_path = null;

		/**
		 * The file name of the newly generated data file for the object. <BR/>
		 */
		public String p_transFileName = null;

		/**
		 * The file path of the newly generated data file for the object. <BR/>
		 */
		public String p_transFilePath = null;

		/**
		 * The state of the translation process for this object. <BR/>
		 */
		public int p_translationState = 0;

		/**
		 * The object itself. <BR/>
		 */
		private BusinessObject p_obj = null;

		/***********************************************************************
		 * Create instance of class ObjInfo. <BR/>
		 * 
		 * @param objInfo
		 *            The information about the business object.
		 * @param app
		 *            The global application info.
		 * @param fileExt
		 *            Extension of new temporary file name.
		 */
		public ObjInfo(BusinessObjectInfo objInfo, ApplicationInfo app,
				String fileExt) {
			this.p_objInfo = objInfo;
			// compute and store the path:
			this.p_path = app.p_system.p_m2AbsBasePath
					+ BOPathConstants.PATH_UPLOAD_ABS_FILES_TEMP;
			// generate the filename for the new xmldata file:
			this.p_transFileName = DocumentTemplate_01.extendFilename(
					DocumentTemplate_01.ObjInfo.FILE_XMLDATATRANS, fileExt);
			// set the path and filename to the new document:
			this.p_transFilePath = this.p_path + this.p_transFileName;

			this.p_translationState = DocumentTemplate_01.ObjInfo.ST_INIT;
		} // ObjInfo

		/***********************************************************************
		 * Get the object itself. <BR/> If the object was not yet instantiated
		 * is is retrieved through the cache. In each case the environment is
		 * set within the object.
		 * 
		 * @param env
		 *            The current environment.
		 * 
		 * @return The object. <CODE>null</CODE> if the object does not exist.
		 */
		public BusinessObject getObject(Environment env) {
			// check if the object is already known:
			if (this.p_obj == null) {
				// get the object out of the cache:
				this.p_obj = BOHelpers.getObject(this.p_objInfo.p_oid, env,
						false, false, false);
			} // if
			else {
				// ensure that the object has the correct environment:
				this.p_obj.setEnv(env);
			} // else

			// get the property value and return the result:
			return this.p_obj;
		} // getObject

	} // ObjInfo

	/**
	 * Defines the number of specific parameters needed for change. <BR/>
	 */
	private final int NUMBER_OF_SPECIFIC_CHANGE_PARAMETERS = 19;

	/**
	 * Defines the number of specific parameters needed for retrieve. <BR/>
	 */
	private final int NUMBER_OF_SPECIFIC_RETRIEVE_PARAMETERS = 23;

	/**
	 * Fieldname: translator. <BR/>
	 */
	public final String FIELD_TRANSLATOR = "translator";

	/**
	 * Fieldname: update mapping. <BR/>
	 */
	public final String FIELD_UPDATEMAPPING = "updatemapping";

	/**
	 * Fieldname: reload types. <BR/>
	 */
	public final String FIELD_RELOADTYPES = "reloadtypes";

	/**
	 * Fieldname: reload types. <BR/>
	 */
	public final String FIELD_SHOWDOMTREE = "showdomtree";

	/**
	 * Fieldname: workflow template. <BR/>
	 */
	public final String FIELD_WORKFLOWTEMPLATE = "workflowtemplate";

	/**
	 * Fieldname: force using a type translator. <BR/>
	 */
	public final String FIELD_FORCE_TRANSLATOR = "forcetranslator";

	/**
	 * Fieldname: update system values when updating mapping. <BR/>
	 */
	public final String FIELD_UPDATEMAPPING_SYSTEM = "updatemapping_system";

	/**
	 * The display mode for the system section. <BR/>
	 */
	private int p_systemDisplayMode = 0;

	/**
	 * The type code from the xml template file &gt;OBJECT TYPECODE="Type"&lt;.
	 * <BR/>
	 */
	private String p_objectTypeCode = null;

	/**
	 * The m2 type id. <BR/>
	 */
	private int p_objectTypeId = 0;

	/**
	 * The m2 tVersion id. <BR/>
	 */
	private int p_objectTVersionId = 0;

	/**
	 * The mayExistIn code names for this object type. <BR/>
	 */
	private String p_objectMayExistIn = null;

	/**
	 * True if the template defines a container type. <BR/>
	 */
	private boolean p_objectIsContainerType = false;

	/**
	 * The mayContain code names for this container object type. <BR/>
	 */
	private String p_objectMayContain = null;

	/**
	 * The mayContain templates for this container object type. <BR/>
	 */
	private Vector<DocumentTemplate_01> p_objectMayContainTemplates = null;

	/**
	 * The java class for this object type. <BR/>
	 */
	private String p_objectClassName = null;

	/**
	 * The icon for this object type. <BR/>
	 */
	private String p_objectIconName = null;

	/**
	 * The type name from the xml template file &lt;OBJECT NAME="Name"&gt;.
	 * <BR/>
	 */
	private String p_objectTypeName = null;

	/**
	 * The type code of the super type. <BR/>
	 */
	private String p_objectSuperTypeCode = null;

	/**
	 * True if objects of this type are searchable. <BR/>
	 */
	private boolean p_objectIsSearchable = true;

	/**
	 * True if this type is inheritable. <BR/>
	 */
	private boolean p_objectIsInheritable = true;

	/**
	 * True if objects of this type are shown in the tree menu (Web browser).
	 * <BR/>
	 */
	private boolean p_objectIsShowInMenu = true;

	/**
	 * Default value for the showInNews attribute. <BR/>
	 */
	private boolean p_objectIsShowInNews = true;

	/**
	 * Holds the tab information defined in the template file. <BR/>
	 */
	private Vector<DocumentTemplate_01.TabTemplate> p_objectTabs = new Vector<DocumentTemplate_01.TabTemplate>();

	/**
	 * The optional stored procedure to copy attachments (XMLDATA). <BR/>
	 */
	private String p_attachmentCopy = null;

	/**
	 * The name of the directory where the log-file of translation should be
	 * stored. <BR/>
	 */
	private String p_logDirectory = null;

	/**
	 * The name of the mapping table in the database. <BR/>
	 */
	private String p_mappingTableName = null;

	/**
	 * The name of the mapping table in the database. <BR/>
	 */
	private String p_mappingInfo = null;

	/**
	 * The name of the copy stored procedure for the mapping. <BR/> This is used
	 * by the stored procedure p_XMLViewer_01$BOCopy to copy the attributes in
	 * the mapping table too.
	 */
	private StringBuffer p_mappingProcCopy = null;

	/**
	 * The workflow oid for the document template. <BR/>
	 */
	private OID p_workflowTemplateOid = null;

	/**
	 * The name of the workflow template. <BR/>
	 */
	private String p_workflowTemplateName = null;

	/**
	 * Option to activate the db mapping. <BR/>
	 */
	private boolean p_isUpdateMapping = false;

	/**
	 * Option to reload objecttypes into typecache. <BR/>
	 */
	private boolean p_isReloadTypes = true;

	/**
	 * True if the template file is valid. <BR/>
	 */
	private boolean p_isTemplateFileValid = false;

	/**
	 * The data element which represents the current template file. <BR/> This
	 * variable is set when retrieving the data out of the template file.
	 * 
	 * @see #performGetTemplateDataElement
	 */
	private DataElement p_templateDataElement = null;

	/**
	 * Holds the data element of the old template file. <BR/> In case the user
	 * changes the dbmapping this data element is used to delete the old mapping
	 * table.
	 */
	private DataElement p_oldTemplateDataElement = null;

	/**
	 * Holds the name of the new template xml file. <BR/>
	 */
	private String p_newTemplateFile = null;

	/**
	 * Holds the Environment instance used in the last checkIsValidFile ().
	 * <BR/>
	 */
	private Environment p_lastEnv = null;

	/**
	 * Holds the data element created from the template file. <BR/> This
	 * attribute is set in the validateDataElement() method and should be used
	 * only in this context.
	 */
	private DataElement p_validationDataElement = null;

	/**
	 * Path and name of the xsl-translator. <BR/>
	 */
	private String p_translatorPathName = null;

	/**
	 * Flag to show DOMTree. <BR/>
	 */
	private boolean p_isShowDOMTree = false;

	/**
	 * Flag which indicates whether the object is created through import. <BR/>
	 */
	private boolean p_isImported = false;

	/**
	 * Option to force using a type translator. <BR/>
	 */
	private boolean p_isForceTranslator = false;

    /**
     * Contains possible additional linked fields. <BR/>
     */
    private Vector<ValueDataElement> p_linkFields = null;

	// /////////////////////////////////////////////////////////////////////////
	// constructors
	// /////////////////////////////////////////////////////////////////////////

    /***************************************************************************
	 * This constructor creates a new instance of the class DocumentTemplate_01.
	 * <BR/>
	 */
	public DocumentTemplate_01() {
		// call constructor of super class ObjectReference:
		super();

		// initialize properties common to all subclasses:
	} // DocumentTemplate_01

	/***************************************************************************
	 * Creates a DocumentTemplate_01 object. <BR/> This constructor calls the
	 * corresponding constructor of the super class. <BR/>
	 * 
	 * @param oid
	 *            Value for the compound object id.
	 * @param user
	 *            Object representing the user.
	 * 
	 * @deprecated KR 20090723 This constructor should not be used.
	 */
	@Deprecated
	public DocumentTemplate_01(OID oid, User user) {
		// call constructor of super class:
		super(oid, user);

		// initialize properties common to all subclasses:
	} // DocumentTemplate_01

	// /////////////////////////////////////////////////////////////////////////
	// implementations of ITemplate
	// /////////////////////////////////////////////////////////////////////////

	/***************************************************************************
	 * Check if the template is loadable, i.e. if all resources which are
	 * necessary for loading the template are known and existing. <BR/> These
	 * resources can be other objects, files, ftp servers, connections to other
	 * applications, etc.
	 * 
	 * @return <CODE>true</CODE> if the template is loadable,
	 *         <CODE>false</CODE> otherwise.
	 */
	public boolean isLoadable() {
		// check if the file path is known and return the result:
		return this.getAbsFilePath() != null;
	} // isLoadable

	/***************************************************************************
	 * Check if the template was already loaded. <BR/>
	 * 
	 * @return <CODE>true</CODE> if the template was already loaded,
	 *         <CODE>false</CODE> otherwise.
	 */
	public boolean isLoaded() {
		// check if we can get the template data element and return the result:
		return this.getTemplateDataElement() != null;
	} // getTemplateDataElement

	/***************************************************************************
	 * Get the buttons to be displayed in info view. <BR/>
	 * 
	 * @return The buttons.
	 */
	public String getInfoButtons() {
		// get the info buttons out of the data element and return the result:
		return this.getTemplateDataElement().p_infoButtonList;
	} // getInfoButtons

	/***************************************************************************
	 * Get the buttons to be displayed in content view. <BR/>
	 * 
	 * @return The buttons
	 */
	public String getContentButtons() {
		// get the content buttons out of the data element and return the
		// result:
		return this.getTemplateDataElement().p_contentButtonList;
	} // getContentButtons

	/***************************************************************************
	 * Get the transformation for instances of the template. <BR/>
	 * 
	 * @return The transformation information. <CODE>null</CODE> means that
	 *         the value was not set.
	 */
	public String getTransformation() {
		// get the transformation out of the data element and return the result:
		return this.getTemplateDataElement().p_transformation;
	} // getTransformation

	/***************************************************************************
	 * Get the rule for creating the value of the object name. <BR/>
	 * 
	 * @return The rule. <CODE>null</CODE> means that no rule was defined.
	 */
	public String getNameTemplate() {
		// get the name template out of the data element and return the result:
		return this.getTemplateDataElement().p_nameTemplate;
	} // getNameTemplate

	/***************************************************************************
	 * Get the oid of the template. <BR/>
	 * 
	 * @return The oid. <CODE>null</CODE> if no oid was defined.
	 */
	public OID getOid() {
		return this.oid;
	} // getOid

	// /////////////////////////////////////////////////////////////////////////
	// instance methods
	// /////////////////////////////////////////////////////////////////////////

	/***************************************************************************
	 * Returns the object type code of the template. <BR/>
	 * 
	 * @return The type code.
	 */
	public String getObjectTypeCode() {
		return this.p_objectTypeCode;
	} // getObjectTypeCode

	/***************************************************************************
	 * Returns the type id of the template. <BR/>
	 * 
	 * @return The type id.
	 */
	public int getObjectTypeId() {
		return this.p_objectTypeId;
	} // getObjectTypeId

	/***************************************************************************
	 * Returns the tVersion id of the template. <BR/>
	 * 
	 * @return The tVersionId.
	 */
	public int getObjectTVersionId() {
		return this.p_objectTVersionId;
	} // getObjectTVersionId

	/***************************************************************************
	 * Returns the display mode for the system section. <BR/>
	 * 
	 * @return The mode.
	 */
	public int getSystemDisplayMode() {
		return this.p_systemDisplayMode;
	} // getSystemDisplayMode

	/***************************************************************************
	 * Returns may contain definition. <BR/>
	 * 
	 * @return The may contain definition.
	 */
	public String getMayContain() {
		return this.p_objectMayContain;
	} // getMayContain

	/***************************************************************************
	 * Returns may contain templates. <BR/> These are the templates of those
	 * object types which may exist within the actual object container. If the
	 * templates are not known yet they are searched for and stored into
	 * {@link #p_objectMayContainTemplates}.
	 * 
	 * @return The may contain templates. If for any type no template is defined
	 *         the corresponding position in the vector is <CODE>null</CODE>.
	 * 
	 * @throws TypeNotFoundException
	 *             One of the defined types was not found.
	 */
	public Vector<DocumentTemplate_01> getMayContainTemplates()
			throws TypeNotFoundException {
		// check if the templates are already known:
		if (this.p_objectMayContainTemplates == null) {
			// compute the templates out of the maycontain definition:
			this.p_objectMayContainTemplates = DIHelpers.findTemplates(this
					.getTypeCache(), this.p_objectMayContain);
		} // if

		// return the templates:
		return this.p_objectMayContainTemplates;
	} // getMayContainTemplates

	/***************************************************************************
	 * Returns the oid of the workflow template. <BR/>
	 * 
	 * @return The workflow template oid.
	 */
	public OID getWorkflowTemplateOid() {
		return this.p_workflowTemplateOid;
	} // getWorkflowTemplateOid

	/***************************************************************************
	 * Returns the name of the mapping table used by the template. <BR/>
	 * 
	 * @return The name of the mapping table.
	 */
	public String getMappingTableName() {
		return this.p_mappingTableName;
	} // getMappingTableName

	/***************************************************************************
	 * Returns the mapping information of the template as a hash. <BR/>
	 * 
	 * @return A hashtable with the value/mapping pairs.
	 * @deprecated This method is not needed anymore since the mapping inof is
	 *             available from the document template.
	 */
	@Deprecated
	public Hashtable<String, String> getMappingInfo() {
		if (this.p_mappingInfo == null) {
			return null;
		} // if

		// we use a tokenizer to extract the value names and the table field
		// names
		// from the mapping information string.
		StringTokenizer token = new StringTokenizer(this.p_mappingInfo, "\n");
		Hashtable<String, String> hash = new Hashtable<String, String>();
		// loop through the tokens
		while (token.hasMoreElements()) {
			// get the value field name
			String valueName = token.nextToken();
			// the value name must be followed by the database field name
			if (!token.hasMoreElements()) {
				return null;
			} // if
			// get the database field name
			String dbName = token.nextToken();
			// insert the dbName with the valueName as key
			hash.put(valueName, dbName);
		} // while (token.hasMoreElements())
		return hash;
	} // getMappingInfo

	/***************************************************************************
	 * This method returns the flag if show DOM Tree or not. <BR/>
	 * 
	 * @return <CODE>true</CODE> if the dom tree shall be shown,
	 *         <CODE>false</CODE> otherwise.
	 */
	public boolean getDOMTree() {
		return this.p_isShowDOMTree;
	} // public boolean getDOMTree ()

    /***************************************************************************
     * Returns possible link fields. <BR/> 
     * 
     * @return Returns possible link fields. If nothing is set <CODE>null</CODE>
     *         is returnd. <BR/>
     */
    public Vector<ValueDataElement> getLinkFields ()
    {
        return p_linkFields;
    } // getLinkFields

    /***************************************************************************
     * Check if a least one link field is defined. <BR/>
     * 
     * @return <CODE>true</CODE> if link fields are defined, 
     *         <CODE>false</CODE> otherwise.
     */
    public boolean hasLinkFields() 
    {
        return this.getLinkFields() != null;
    } // hasLinkFields
    
	/***************************************************************************
	 * This method makes the class specific initializations. <BR/>
	 */
	public void initClassSpecifics() {
		super.initClassSpecifics(); // has same specifics as super class

		this.attachmentType = DocConstants.ATT_HYPERLINK;

		this.procCreate = "p_DocumentTemplate_01$create";
		this.procChange = "p_DocumentTemplate_01$change";
		this.procRetrieve = "p_DocumentTemplate_01$retrieve";
		this.procDelete = "p_DocumentTemplate_01$delete";
		this.procDeleteRec = "p_DocumentTemplate_01$delete";

		// set db table name
		this.tableName = "ibs_DocumentTemplate_01";

		// set number of parameters for procedure calls:
		this.specificRetrieveParameters += this.NUMBER_OF_SPECIFIC_RETRIEVE_PARAMETERS;
		this.specificChangeParameters += this.NUMBER_OF_SPECIFIC_CHANGE_PARAMETERS;

		// set as master attachment:
		this.isMaster = true;
		this.p_translatorPathName = null;

		// reset some of the global parameters
		this.p_isTemplateFileValid = false;
		this.p_newTemplateFile = null;
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
		// set specific parameters of the File_01 object
		super.setSpecificChangeParameters(sp);

		// set default values for undefined attributes

		// if no other type name defined set the type code as type name
		if (this.p_objectTypeName == null
				|| this.p_objectTypeName.length() == 0) {
			this.p_objectTypeName = this.p_objectTypeCode;
		} // no class defined

		// if no other class defined set the default class
		if (this.p_objectClassName == null
				|| this.p_objectClassName.length() == 0)
		// no class defined?
		{
			if (this.p_objectIsContainerType) {
				this.p_objectClassName = "ibs.di.XMLContainer_01";
			} // if
			else {
				this.p_objectClassName = "ibs.di.XMLViewer_01";
			} // else
		} // if no class defined

		// if no other icon defined set the default icon
		if (this.p_objectIconName == null
				|| this.p_objectIconName.length() == 0) {
			if (this.p_objectIsContainerType) {
				this.p_objectIconName = "XMLViewerContainer.gif";
			} // if
			else {
				this.p_objectIconName = "XMLViewer.gif";
			} // else
		} // no icon defined

		// add specific parameters
		// objectTypeCode
		sp.addInParameter(ParameterConstants.TYPE_STRING,
				(this.p_objectTypeCode != null) ? this.p_objectTypeCode : "");
		// objectTypeName
		sp.addInParameter(ParameterConstants.TYPE_STRING,
				(this.p_objectTypeName != null) ? this.p_objectTypeName : "");
		// class name
		sp.addInParameter(ParameterConstants.TYPE_STRING,
				(this.p_objectClassName != null) ? this.p_objectClassName : "");
		// icon name
		sp.addInParameter(ParameterConstants.TYPE_STRING,
				(this.p_objectIconName != null) ? this.p_objectIconName : "");
		// mayExistIn info
		sp.addInParameter(ParameterConstants.TYPE_STRING,
				(this.p_objectMayExistIn != null) ? this.p_objectMayExistIn
						: "");
		// isContainerType flag
		sp.addInParameter(ParameterConstants.TYPE_BOOLEAN,
				this.p_objectIsContainerType);
		// mayContain info
		sp.addInParameter(ParameterConstants.TYPE_STRING,
				(this.p_objectMayContain != null) ? this.p_objectMayContain
						: "");
		// super type code
		sp.addInParameter(
						ParameterConstants.TYPE_STRING,
						(this.p_objectSuperTypeCode != null) ? this.p_objectSuperTypeCode
								: "");
		// isSearchable
		sp.addInParameter(ParameterConstants.TYPE_BOOLEAN,
				this.p_objectIsSearchable);
		// isInheritable
		sp.addInParameter(ParameterConstants.TYPE_BOOLEAN,
				this.p_objectIsInheritable);
		// showInMenu
		sp.addInParameter(ParameterConstants.TYPE_BOOLEAN,
				this.p_objectIsShowInMenu);
		// showInNews
		sp.addInParameter(ParameterConstants.TYPE_BOOLEAN,
				this.p_objectIsShowInNews);
		// display mode
		sp.addInParameter(ParameterConstants.TYPE_INTEGER,
				this.p_systemDisplayMode);
		// dbMapped flag: all templates are db-mapped
		sp.addInParameter(ParameterConstants.TYPE_BOOLEAN, true);
		// name of the mapping table
		sp.addInParameter(ParameterConstants.TYPE_STRING,
				(this.p_mappingTableName != null) ? this.p_mappingTableName
						: "");
		// name of the copy procedure for the mapping
		sp.addInParameter(ParameterConstants.TYPE_STRING,
				(this.p_mappingProcCopy != null) ? this.p_mappingProcCopy
						.toString() : "");
		// the workflow oid
		sp.addInParameter(
						ParameterConstants.TYPE_STRING,
						(this.p_workflowTemplateOid != null) ? this.p_workflowTemplateOid
								.toString()
								: "");
		// name of the copy procedure for optional attachments
		sp.addInParameter(ParameterConstants.TYPE_STRING,
				(this.p_attachmentCopy != null) ? this.p_attachmentCopy : "");
		// showDOMTree
		sp.addInParameter(ParameterConstants.TYPE_BOOLEAN,
				this.p_isShowDOMTree);
		// trace ("--- this.p_isShowDOMTree ---" + this.p_isShowDOMTree);
	} // setSpecificChangeParameters


	/***************************************************************************
	 * Retrieves a DocumentTemplate.<BR/> This method had to be overwritten
	 * because there should be no rights check performed when retrieving a
	 * documenttemplate to provide the information of all documenttemplates to
	 * all users (type chache).
	 * 
	 * @param operation
	 *            Operation to be performed with the object.
	 * 
	 * @exception NoAccessException
	 *                The user does not have access to this object to perform
	 *                the required operation.
	 * @exception AlreadyDeletedException
	 *                The object was deleted before the user tried to access it.
	 * @exception ObjectNotFoundException
	 *                The required object was not found during search, i.e.
	 *                there does not exist any object with the required oid.
	 */
	public void retrieve(int operation) throws NoAccessException,
			AlreadyDeletedException, ObjectNotFoundException {
		super.retrieve(0);
	} // retrieve


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
		// initialize the index
		int i = super.setSpecificRetrieveParameters(sp, params, lastIndex);

		// set the specific parameters:
		// objectTypeCode
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
		// objectTypeName
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
		// objectClassName
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
		// objectIconName
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
		// objectMayExistIn
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
		// isContainerType
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_BOOLEAN);
		// objectMayContain
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
		// objectSuperTypeCode
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
		// isSearchable
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_BOOLEAN);
		// isInheritable
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_BOOLEAN);
		// showInMenu
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_BOOLEAN);
		// showInNews
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_BOOLEAN);
		// m2 object id
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_INTEGER);
		// m2 tVersion id
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_INTEGER);
		// systemDisplayMode
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_INTEGER);
		// idDBMapped
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_BOOLEAN);
		// mappingTableName
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
		// mappingProcCopy
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
		// the workflow template oid
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_VARBYTE);
		// the workflow template name
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
		// attachmentCopy
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
		// logDirectory
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
		// showDOMTree
		params[++i] = sp.addOutParameter(ParameterConstants.TYPE_BOOLEAN);
		// trace ("--- this.p_isShowDOMTree ---" + this.p_isShowDOMTree);

		return i; // return the current index
	} // setSpecificRetrieveParameters

	/***************************************************************************
	 * Get the data for the additional (type specific) parameters for
	 * performRetrieveData. <BR/> This method must be overwritten by all
	 * subclasses that have to get type specific data from the retrieve data
	 * stored procedure.
	 * 
	 * @param params
	 *            The array of parameters from the retrieve data stored
	 *            procedure.
	 * @param lastIndex
	 *            The index to the last element used in params thus far.
	 */
	protected void getSpecificRetrieveParameters(Parameter[] params,
			int lastIndex) {
		super.getSpecificRetrieveParameters(params, lastIndex);

		// initialize the index
		int i = lastIndex + this.specificRetrieveParameters
				- this.NUMBER_OF_SPECIFIC_RETRIEVE_PARAMETERS;

		// get the specific parameters:
		this.p_objectTypeCode = params[++i].getValueString();
		this.p_objectTypeName = params[++i].getValueString();
		this.p_objectClassName = params[++i].getValueString();
		this.p_objectIconName = params[++i].getValueString();
		this.p_objectMayExistIn = params[++i].getValueString();
		this.p_objectIsContainerType = params[++i].getValueBoolean();
		this.p_objectMayContain = params[++i].getValueString();
		this.p_objectSuperTypeCode = params[++i].getValueString();
		this.p_objectIsSearchable = params[++i].getValueBoolean();
		this.p_objectIsInheritable = params[++i].getValueBoolean();
		this.p_objectIsShowInMenu = params[++i].getValueBoolean();
		this.p_objectIsShowInNews = params[++i].getValueBoolean();
		this.p_objectTypeId = params[++i].getValueInteger();
		this.p_objectTVersionId = params[++i].getValueInteger();

		this.p_systemDisplayMode = params[++i].getValueInteger();
		// ignore the isDBMapped parameter:
		i++;
		this.p_mappingTableName = params[++i].getValueString();
		this.p_mappingProcCopy = new StringBuffer().append(params[++i]
				.getValueString());
		this.p_workflowTemplateOid = SQLHelpers.getSpOidParam(params[++i]);
		this.p_workflowTemplateName = params[++i].getValueString();
		this.p_attachmentCopy = params[++i].getValueString();
		this.p_logDirectory = params[++i].getValueString();
		this.p_isShowDOMTree = params[++i].getValueBoolean();

		this.trace("--- this.p_isShowDOMTree ---" + this.p_isShowDOMTree);

		/*
		 * showDebug ("p_objectTypeCode: " + this.p_objectTypeCode); showDebug
		 * ("p_objectTypeId: " + this.p_objectTypeId); showDebug
		 * ("p_objectTVersionId: " + this.p_objectTVersionId); showDebug
		 * ("displayMode: " + this.p_systemDisplayMode); showDebug
		 * ("p_isDBMapped: " + this.p_isDBMapped); showDebug ("mappingTable: " +
		 * this.p_mappingTableName); showDebug ("workflowOid: " +
		 * this.p_workflowTemplateOid);
		 */
	} // getSpecificRetrieveParameters

	/***************************************************************************
	 * Change the data of a business object in the database. <BR/> <B>THIS
	 * METHOD IS A DUMMY WHICH MUST BE OVERWRITTEN IN SUB CLASSES!</B> <BR/>
	 * This method tries to store the object into the database. During this
	 * operation a rights check is done, too. If this is all right the object is
	 * stored and this method terminates otherwise an exception is raised. <BR/>
	 * 
	 * @param operation
	 *            Operation to be performed with the object.
	 * 
	 * @exception NoAccessException
	 *                The user does not have access to this object to perform
	 *                the required operation.
	 * @exception NameAlreadyGivenException
	 *                An object with this name already already exists. This
	 *                exception is only raised by some specific object types
	 *                which don't allow more than one object with the same name.
	 */
	protected void performChangeData(int operation) throws NoAccessException,
			NameAlreadyGivenException {
		// we have to store the tab informations first:
		if (this.p_objectTabs.size() > 0) {
			// check if the object is changeable:
			this.checkChangeable();

			int size = this.p_objectTabs.size();
			for (int i = 0; i < size; i++) {
				TabTemplate tab = this.p_objectTabs.elementAt(i);
				// create the stored procedure call:
				StoredProcedure sp = new StoredProcedure(
						"p_TabTemplate_01$addTab",
						StoredProcedureConstants.RETURN_VALUE);

				// parameter definitions:
				// must be in right sequence (like SQL stored procedure def.)
				// input parameters
				// oid
				BOHelpers.addInParameter(sp, this.oid);
				// id
				sp.addInParameter(ParameterConstants.TYPE_INTEGER, i);
				// tabkind
				sp.addInParameter(ParameterConstants.TYPE_INTEGER, tab.p_kind);
				// tVersionId
				sp.addInParameter(ParameterConstants.TYPE_INTEGER,
						tab.p_tVersionId);
				// fct
				sp.addInParameter(ParameterConstants.TYPE_INTEGER, tab.p_fct);
				// priority
				sp.addInParameter(ParameterConstants.TYPE_INTEGER,
						tab.p_priority);
				// name
				sp.addInParameter(ParameterConstants.TYPE_STRING, tab.p_name);
				// description
				sp.addInParameter(ParameterConstants.TYPE_STRING, tab.p_desc);
				// tabcode
				sp
						.addInParameter(ParameterConstants.TYPE_STRING,
								tab.p_tabCode);
				// class
				sp.addInParameter(ParameterConstants.TYPE_STRING, tab.p_class);
				// perform the function call:
				BOHelpers.performCallFunctionData(sp, this.env);
			} // for i
		} // if (this.p_objectTabs.size () > 0)

		super.performChangeData(operation);
	} // performChangeData

	/***************************************************************************
	 * Retrieve the type specific data that is not got from the stored
	 * procedure. <BR/> This method must be overwritten by all subclasses that
	 * have to get type specific data that cannot be got from the retrieve data
	 * stored procedure.
	 * 
	 * @param action
	 *            The action object associated with the connection.
	 * 
	 * @exception DBError
	 *                This exception is always thrown, if there happens an error
	 *                during accessing data.
	 */
	protected void performRetrieveSpecificData(SQLAction action) throws DBError {
		super.performRetrieveSpecificData(action);

		// the mapping info is stored in a large text field (CLOB).
		this.p_mappingInfo = this
				.performRetrieveTextData(action, this.tableName, "mappingInfo",
						"p_DocumentTemplate_01$getMInfo");

		// get the template data:
		this.performGetTemplateDataElement();
	} // performRetrieveSpecificData

	/***************************************************************************
	 * Change all type specific data that is not changed by performChangeData.
	 * <BR/> This method must be overwritten by all subclasses that have to
	 * change type specific data.
	 * 
	 * @param action
	 *            SQL Action for Database
	 * 
	 * @exception DBError
	 *                This exception is always thrown, if there happens an error
	 *                during accessing data.
	 */
	protected void performChangeSpecificData(SQLAction action) throws DBError {
		super.performChangeSpecificData(action);

		this.performChangeTextData(action, this.tableName, "mappingInfo",
				this.p_mappingInfo);
	} // performChangeSpecificData

	/***************************************************************************
	 * Delete a business object from the database. <BR/> First this method tries
	 * to delete the object from the database. During this operation a rights
	 * check is done, too. If this is all right the object is deleted and this
	 * method terminates otherwise an exception is raised. <BR/>
	 * 
	 * @param operation
	 *            Operation to be performed with the object.
	 * 
	 * @exception NoAccessException
	 *                The user does not have access to this object to perform
	 *                the required operation.
	 * @exception ObjectNotAffectedException
	 *                The operation could not be performed on all required
	 *                objects.
	 * @exception DependentObjectExistsException
	 *                There exists an object which depends on the object which
	 *                whould have been deleted.
	 */
	protected void performDeleteData(int operation) throws NoAccessException,
			ObjectNotAffectedException, DependentObjectExistsException {
		// check if the object is deletable:
		this.checkDeletable();

		// when the document template is deleted all objects with this type code
		// are deleted physically in the database. So we have to delete
		// the xml data files of all this form objects too.

		// find all deleted objects which have the tVersionId of the
		// template or which are below such objects:
		SelectQuery query = new SelectQuery(new StringBuilder(
				"o.oid, o.state, o.name, o.typename, o.description"), new StringBuilder(
				"ibs_Object o, ibs_Object c"), new StringBuilder("o.state = ")
				.append(States.ST_DELETED).append(" AND c.tVersionId = ")
				.append(this.p_objectTVersionId).append(" AND").append(
						SQLHelpers.getQueryConditionAttribute("o.posNoPath",
								SQLConstants.MATCH_STARTSWITH, "c.posNoPath",
								false)), null, null, null);

		// get all objects of that type
		Vector<BusinessObjectInfo> deletedObjects = BOHelpers.findObjects(
				query, this.env);

		// delete the template object in the database
		super.performDeleteData(operation);

		// deleting the mapping table:
		DataElement dataElement = this.getTemplateDataElement();
		// instanciate the DBMapper class to activate
		// the db-mapping for this document template.
		DBMapper mapper = new DBMapper(this.user, this.env, this.sess, this.app);
		if (!mapper.deleteFormTemplateDBTable(dataElement, true)) {
			String msg = mapper.getLogObject().toString();
			IOHelpers.showMessage(msg, this.app, this.sess, this.env);
		} // if (!mapper.deleteFormTemplateDBTable (dataElement))

		// template objects are deleted physically!
		// delete the xml template file from the file system.
		// the template file is stored in a directory with the name of the
		// container oid
		// of the template object.
		//
		// Example: /m2/upload/<containerOid>

		// construct the path of the template file:
		String templateFile = BOHelpers.getFilePath(this.containerId)
				+ this.fileName;

		// delete the template file
		FileHelpers.deleteFile(templateFile);

		// now we can delete the files:
		if (deletedObjects != null && deletedObjects.size() > 0) {
			for (Iterator<BusinessObjectInfo> iter = deletedObjects.iterator(); iter
					.hasNext();) {
				BusinessObjectInfo objInfo = iter.next();
				// construct the path and name of the xml file
				String dataDir = BOHelpers.getFilePath(objInfo.p_oid);
				// delete the directory with all contents:
				FileHelpers.deleteDirRec(dataDir);
			} // for iter
		} // if
	} // performDeleteData

	/***************************************************************************
	 * Sets the buttons that can be displayed when the user is in an object's
	 * info view. <BR/> This method can be overwritten in subclasses to redefine
	 * the set of buttons that can be displayed. <BR/>
	 * 
	 * @return An array with button ids that can potentially be displayed.
	 */
	protected int[] setInfoButtons() {
		// if the document template is referenced
		// by other objects the delete button is not shown.
		int rowCount = 0;
		// the action object used to access the database
		SQLAction action = null;
		StringBuffer queryStr; // the query

		try {
			// open db connection - only workaround - db connection must
			// be handled somewhere else
			action = this.getDBConnection();
			// select the object oid in the reference view
			queryStr = new StringBuffer().append(
					"SELECT oid FROM v_DocumentTemplate_01$ref ").append(
					"WHERE oid = ").append(this.oid.toStringQu());

			rowCount = action.execute(queryStr, false);
			// end action:
			action.end();
		} // try
		catch (DBError e) {
			// get all errors (can be chained):
			String allErrors = "";
			String h = new String(e.getMessage());
			h += e.getError();
			// loop through all errors and concatenate them to a String:
			while (h != null) {
				allErrors += h;
				h = e.getError();
			} // while
			// show the message
			IOHelpers.showMessage(allErrors, this.app, this.sess, this.env);
		} // catch
		finally {
			try {
				action.end();
			} // try
			catch (DBError e) {
				// show the error:
				IOHelpers.showMessage("Error when finishing database action.",
						e, this.app, this.sess, this.env, true);
			} // catch
			// close db connection in every case - only workaround - db
			// connection must
			// be handled somewhere else
			this.releaseDBConnection(action);
		} // finally

		// if the document template is referenced
		// by other objects the delete button is not shown.
		if (rowCount > 0) {
			// define buttons to be displayed:
			int[] buttons = {
					Buttons.BTN_EDIT,
					// Buttons.BTN_DELETE,
					Buttons.BTN_CUT,
					// we do not allow to copy a document template
					// because the type name of a template must be unique.
					// Buttons.BTN_COPY,
					Buttons.BTN_DISTRIBUTE, Buttons.BTN_SEARCH,
					Buttons.BTN_GENERATETRANSLATOR, }; // buttons
			// return button array
			return buttons;
		} // if

		// define buttons to be displayed:
		int[] buttons = {
				Buttons.BTN_EDIT,
				Buttons.BTN_DELETE,
				Buttons.BTN_CUT,
				// we do not allow to copy a document template
				// because the type name of a template must be unique.
				// Buttons.BTN_COPY,
				Buttons.BTN_DISTRIBUTE, Buttons.BTN_SEARCH,
				Buttons.BTN_GENERATETRANSLATOR, }; // buttons

		// return button array
		return buttons;
	} // setInfoButtons

	/***************************************************************************
	 * Read form the User the data used in the Object. <BR/>
	 */
	public void getParameters() {
		String str = null; // temporary string for reading
		// the environment
		int num = 0; // temporary number
		String tempFileName = null; // the file name of the translator
		// in the temp directory
		String tmpFilePathName = null; // the path and name of the current
		// translator in the temp directory
		String translatorName = null; // the name of the current translator
		String physTranslatorPath = null;

		// show the property only if the edit is performed (not new):
		if (this.state == States.ST_ACTIVE)
		// is the state of object active ?
		{
			/*
			 * // should a translator be generated for the user or just
			 * temporarily: if ((num = env.getBoolParam
			 * (AppArguments.ARG_DOWNLOADTRANSLATOR)) >=
			 * IOConstants.BOOLPARAM_FALSE) { this.p_downloadTranslator = (num ==
			 * IOConstants.BOOLPARAM_TRUE); } // if
			 */

			// BB HINT: we have to check here if a container id has been set
			// it can be null in the case the user created a file object
			// or one of its subtypes and activated the option
			// store and new. we dont have to get the parameters from the
			// environment anyway because the object will be a new one
			if (this.containerId != null) {
				// it's a file
				// getFileParamBO is a method which includes a browser check
				if ((str = this.getFileParamBO(BOArguments.ARG_TRANSLATOR)) != null) {
					this.p_translatorPathName = null;

					tempFileName = str;
					if (tempFileName.indexOf(this.oid.toString()) < 0) {
						translatorName = this.oid.toString() + tempFileName;
					} // if

					physTranslatorPath = BOHelpers
							.getFilePath(this.containerId);

					// if the path is not null (this means we are NOT in
					// the search dialogue) AND
					// if the name of the file does not contain the oid
					// then rename it the way it physically to have the oid in
					// it
					if ((str != null)
							&& tempFileName.indexOf(this.oid.toString()) < 0) {
						tmpFilePathName = this.env
								.getFilePath(BOArguments.ARG_TRANSLATOR);
						// check if temporary file exists
						if (FileHelpers.exists(tmpFilePathName)) {
							str = physTranslatorPath + translatorName;
							// check if file exists
							if (FileHelpers.exists(str)) {
								FileHelpers.deleteFile(str);
							} // if (FileHelpers.exists (str))
							// rename the file
							if (!FileHelpers.renameFile(tmpFilePathName, str)) {
								IOHelpers.showMessage(MultilingualTextProvider
						            .getMessage (UtilExceptions.EXC_BUNDLE,						                        
										UtilExceptions.ML_E_NOFILEMOVE, env)
									+ tmpFilePathName + "-->"
									+ physTranslatorPath
									+ translatorName, this.app,
									this.sess, this.env);
							} // if (!FileHelpers.renameFile (tmpFilePathName,
								// str))
							this.p_translatorPathName = str;
						} // if (FileHelpers.exists (tmpFilePathName))
					} // if ((str != null) && tempFileName.indexOf
						// (this.oid.toString ()) < 0)
				} // if ((str = getFileParamBO (AppArguments.ARG_FILE)) !=
					// null)
			} // if (this.containerId != null)
		} // if is the state of object active

		// activate the force translation option only in case a translator has
		// been set
		if (this.p_translatorPathName != null) {
			// check if the force translation option has been activated
			if ((num = this.env.getBoolParam(DIArguments.ARG_FORCE_TRANSLATOR)) > IOConstants.BOOLPARAM_NOTEXISTS) {
				this.p_isForceTranslator = num == IOConstants.BOOLPARAM_TRUE;
			} // if
		} // if (this.p_translatorPathName != null)
		else // no translator set
		{
			this.p_isForceTranslator = false;
		} // else no translator set

		// get parameters relevant for super class:
		super.getParameters();

		// check if the update dm mapping option has been activated
		if ((num = this.env.getBoolParam(DIArguments.ARG_UPDATEMAPPING)) > IOConstants.BOOLPARAM_NOTEXISTS) {
			this.p_isUpdateMapping = num == IOConstants.BOOLPARAM_TRUE;
		} // if ((num = this.env.getBoolParam (DIArguments.ARG_UPDATEMAPPING))
			// ...

		/*
		 * KR 20090904 not longer relevant // check if the update dm mapping
		 * option has been activated if ((num = this.env.getBoolParam
		 * (DIArguments.ARG_UPDATEMAPPING_SYSTEM)) >
		 * IOConstants.BOOLPARAM_NOTEXISTS) { this.p_isMapSystemValues = num ==
		 * IOConstants.BOOLPARAM_TRUE; } // if ((num = this.env.getBoolParam
		 * (DIArguments.ARG_UPDATEMAPPING_SYSTEMVALUES)) ...
		 */

		// check if the relad types option has been activated
		if ((num = this.env.getBoolParam(DIArguments.ARG_RELOADTYPES)) > IOConstants.BOOLPARAM_NOTEXISTS) {
			this.p_isReloadTypes = num == IOConstants.BOOLPARAM_TRUE;
		} // if ((num = this.env.getBoolParam (DIArguments.ARG_RELOADTYPES))
			// ...

		// get workflowTemplateOid and workflowTemplateName
		String wfSelection = this.env
				.getStringParam(DIArguments.ARG_WORKFLOWTEMPLATE);
		if (wfSelection != null) {
			try {
				// extract the oid and the name out of the <oid>/<filename> pair
				// we get from the workflowTemplate selection box
				String[] tokens = DIHelpers.getTokens(wfSelection, "/");

				if (tokens.length > 1) {
					this.p_workflowTemplateOid = new OID(tokens[0]);
					this.p_workflowTemplateName = tokens[1];
				} // if (tokens.length > 1)
				else // workflow template selection box is empty
				{
					this.p_workflowTemplateOid = OID.getEmptyOid();
					this.p_workflowTemplateName = "";
				} // workflow template selection box is empty
			} // try
			catch (IncorrectOidException e) {
				this.p_workflowTemplateOid = null;
				this.p_workflowTemplateName = "";
			} // catch
		} // if (wfSelection != NULL)

		if ((num = this.env.getBoolParam(DIArguments.ARG_SHOWDOMTREE)) > IOConstants.BOOLPARAM_NOTEXISTS) {
			this.p_isShowDOMTree = num == IOConstants.BOOLPARAM_TRUE;
		} // if
	} // getParameters

	/***************************************************************************
	 * Represent the properties of a DocumentTemplate_01 object to the user.
	 * <BR/>
	 * 
	 * @param table
	 *            Table where the properties should be added.
	 * 
	 * @see ibs.IbsObject#showProperty(TableElement, String, String, int, User,
	 *      Date)
	 */
	protected void showProperties(TableElement table) {
		super.showProperties(table);

		this.showProperty(table, BOArguments.ARG_TYPECODE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_FORMTYPECODE, env),
            Datatypes.DT_TYPE, this.p_objectTypeCode);

		this.showProperty(table, BOArguments.ARG_TYPE, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_FORMTYPE, env),
            Datatypes.DT_TYPE, this.p_objectTypeName);

		this.showProperty(table, "",
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_DBMAPPING_TABLE, env),
            Datatypes.DT_TEXT, this.p_mappingTableName);

		// workflow template
		this.showProperty(table, DIArguments.ARG_WORKFLOWTEMPLATE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_WORKFLOWTEMPLATE, env),
            Datatypes.DT_TEXT, this.p_workflowTemplateName);

		// show dom tree:
		this.showProperty(table, DIArguments.ARG_SHOWDOMTREE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_SHOWDOMTREE, env),
            Datatypes.DT_BOOL, "" + this.p_isShowDOMTree);

		this.trace(" this.isShowDOMTree" + this.p_isShowDOMTree);
	} // showProperties

	/***************************************************************************
	 * Represent the properties of a DocumentTemplate_01 object to the user
	 * within a form. <BR/>
	 * 
	 * @param table
	 *            Table where the properties shall be added.
	 * 
	 * @see ibs.IbsObject#showFormProperty(TableElement, String, String, int,
	 *      User)
	 */
	protected void showFormProperties(TableElement table) {
		this.trace("---START showFormProperties ---");
		// we do not call the super method because
		// not all fields should be displayed.
		// super.showFormProperties (table);

		// show name, typename, description, validuntil, and the file name
		// the 'show in news' in not shown.
		this.showFormProperty(table, BOArguments.ARG_NAME, 
		    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env),
			Datatypes.DT_NAME, this.name);
		this.showProperty(table, BOArguments.ARG_TYPE, 
		    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TYPE, env),
		    Datatypes.DT_TYPE, this.getMlTypeName ());
		this.showFormProperty(table, BOArguments.ARG_DESCRIPTION,
			MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
			    BOTokens.ML_DESCRIPTION, env),
			Datatypes.DT_DESCRIPTION, this.description);
		this.showFormProperty(table, BOArguments.ARG_VALIDUNTIL,
			MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
			    BOTokens.ML_VALIDUNTIL, env), 
			Datatypes.DT_DATE, this.validUntil);
		this.showFormProperty(table, BOArguments.ARG_FILE, 
		    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FILE, env),
		    Datatypes.DT_FILE, this.fileName, "" + this.containerId);

		// workflow template
		this.showFormProperty(table,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_WORKFLOWTEMPLATE, env),
            this.createWorkflowTemplatesSelectionBox(
				DIArguments.ARG_WORKFLOWTEMPLATE,
				this.p_workflowTemplateName, true));

		this.showFormProperty(table, DIArguments.ARG_SHOWDOMTREE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_SHOWDOMTREE, env),
            Datatypes.DT_BOOL, "" + this.p_isShowDOMTree);

		// show the property only if the edit is performed (not new):
		if (this.state == States.ST_ACTIVE) {
			this.showProperty(table, null, null, Datatypes.DT_SEPARATOR,
					(String) null);

			// show translator name:
			this.showFormProperty(table, BOArguments.ARG_TRANSLATOR,
				MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
				    BOTokens.ML_TRANSLATOR, env),
				Datatypes.DT_FILE, "", "" + this.containerId);

			// option force using type translator:
			this.showFormProperty(table, DIArguments.ARG_FORCE_TRANSLATOR,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_FORCE_TRANSLATION, env), 
                Datatypes.DT_BOOL, "" + false);
		} // if (this.state == States.ST_ACTIVE)

		// for db-mapped document templates show the checkbox for mapping update
		this.showProperty(table, null, null, Datatypes.DT_SEPARATOR,
				(String) null);

		this.showFormProperty(table, DIArguments.ARG_UPDATEMAPPING,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_UPDATEMAPPING, env),
            Datatypes.DT_BOOL, "" + false);

		this.showFormProperty(table, DIArguments.ARG_UPDATEMAPPING_SYSTEM,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_UPDATEMAPPING_SYSTEM, env),
            Datatypes.DT_BOOL, "" + false);

		this.trace("this.isShowDOMTree " + this.p_isShowDOMTree);
	} // showFormProperties

	/***************************************************************************
	 * Show the form for downloading a new translator. <BR/>
	 */
	public void showGenerateTemplateForm() {
		// create page:
		Page page = new Page("GenerateTranslatorForm", false);

		// loading the cascading style sheet:
		StyleSheetElement style = new StyleSheetElement();
		style.importSS = this.sess.activeLayout.path
				+ this.env.getBrowser()
				+ "/"
				+ this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
		page.head.addElement(style);

		// set the icon of this object:
		this.setIcon();

		// create Header
		FormElement form = this.createFormHeader(page, null,
				this.getNavItems(), null, 
				MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
				    BOTokens.ML_GENERATETRANSLATOR, env),
				HtmlConstants.FRM_TEMP, "GenerateTranslator.gif",
				this.containerName);
		/*
		 * FormElement form = createFormHeader (page, null, getNavItems (), null,
		 * MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
		 *     BOTokens.ML_GENERATETRANSLATOR, env), m2Constants.FRM_TEMP,
		 * "GenerateTranslator.gif", this.containerName);
		 */

		// add hidden elements:
		// functionId
		form.addElement(new InputElement(BOArguments.ARG_FUNCTION,
				InputElement.INP_HIDDEN, ""
						+ AppFunctions.FCT_GENERATETRANSLATOR));

		// oid of current object
		form.addElement(new InputElement(BOArguments.ARG_OID,
				InputElement.INP_HIDDEN, "" + this.oid));

		// create inner table
		TableElement table = new TableElement(2);
		table.border = 0;
		table.width = HtmlConstants.TAV_FULLWIDTH;
		table.cellpadding = 5;
		table.cellspacing = 0;

		table.classId = CssConstants.CLASS_INFO;
		String[] classIds = new String[2];
		classIds[0] = CssConstants.CLASS_NAME;
		classIds[1] = CssConstants.CLASS_VALUE;
		table.classIds = classIds;

		// the xml-file for the document-template:
		this.showFormProperty(table, DIArguments.ARG_NEWTEMPLATE,
			MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
			    BOTokens.ML_FILE, env), 
			Datatypes.DT_FILE, this.fileName, "" + this.containerId);

		// option force using type translator:
		this.showFormProperty(table, DIArguments.ARG_FORCE_TRANSLATOR,
		    MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_FORCE_TRANSLATOR, env),
            Datatypes.DT_BOOL, "" + false);

		ScriptElement script = new ScriptElement(ScriptElement.LANG_JAVASCRIPT);
		// deactivate tabs and buttons
		script.addScript(BOConstants.CALL_SHOWTABSBUTTONSEMPTY);

		// add script to page
		page.body.addElement(script);

		form.addElement(table);

		// create footer - do not show cancelbutton (last parameter = true)
		this.createFormFooter(form, null, IOHelpers
				.getShowObjectJavaScript(this.oid.toString()),
				MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
				    BOTokens.ML_BUTTONGENERATETRANSLATOR, env),
				MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
				    DITokens.ML_BUTTON_BACK, env), false, false);
		/*
		 * createFormFooter (form, "top.goback (0);", null,
		 * MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUTTONGENERATETRANSLATOR, env), null, false, false);
		 */

		// build the page and show it to the user:
		try {
			page.build(this.env);
		} // try
		catch (BuildException e) {
			IOHelpers.showMessage(e.getMsg(), this.app, this.sess, this.env);
		} // catch

	} // showGenerateTemplateForm

	//
	// import / export methods
	//
	/***************************************************************************
	 * Reads the object data from an DataElement. <BR/>
	 * 
	 * @param dataElement
	 *            the DataElement to read the data from
	 */
	public void readImportData(DataElement dataElement) {

		this.p_isImported = true;

		// the document template is not exportable!!!
		// get the type specific values:
		if (dataElement.exists(this.FIELD_TRANSLATOR)) {
			String fileNameStr;
			String filePathStr;
			// set the name of the file
			fileNameStr = FileHelpers.makeFileNameValid(dataElement
					.getImportStringValue(this.FIELD_TRANSLATOR));
			filePathStr = FileHelpers.makeFileNameValid(dataElement.sourcePath)
					+ fileNameStr;
			// check if the file really exists
			if (FileHelpers.exists(filePathStr)) {
				// HINT BB050809: just set the translator file from its
				// original location because it will be used before the
				// import files are copied to their new locations
				this.p_translatorPathName = filePathStr;
				// set the file as imported file in order to be able to delete
				// imported files in case the option has been activated
				dataElement.addFile(this.FIELD_TRANSLATOR, this.getAbsPath(),
						fileNameStr, FileHelpers.getFileSize(this.getAbsPath(),
								fileNameStr));
				// in case a translator has been set the forcetranslator option
				// can be set too
				if (dataElement.exists(this.FIELD_FORCE_TRANSLATOR)) {
					this.p_isForceTranslator = dataElement
							.getImportBooleanValue(this.FIELD_FORCE_TRANSLATOR);
				} // if (dataElement.exists (this.FIELD_FORCETRANSLATOR))
			} // if (FileHelpers.exists (filePathStr))
			else // translator file does not exists
			{
				// display an error message
				IOHelpers.showMessage( 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULD_NOT_READ_FILE,
						new String[] {filePathStr}, env),
					this.app, this.sess, this.env);
				// and delete the translator setting
				this.p_translatorPathName = null;
			} // else translator file does not exists
		} // if (dataElement.exists ("translator"))

		// workflow template:
		if (dataElement.exists(this.FIELD_WORKFLOWTEMPLATE)) {
			this.setWorkflowTemplate(dataElement
					.getImportStringValue(this.FIELD_WORKFLOWTEMPLATE));
		} // if

		// show dom tree:
		if (dataElement.exists(this.FIELD_SHOWDOMTREE)) {
			this.p_isShowDOMTree = dataElement
					.getImportBooleanValue(this.FIELD_SHOWDOMTREE);
		} // if

		// update db mapping?
		if (dataElement.exists(this.FIELD_UPDATEMAPPING)) {
			this.p_isUpdateMapping = dataElement
					.getImportBooleanValue(this.FIELD_UPDATEMAPPING);
		} // if

		/*
		 * KR 20090904 not longer relevant // update system values when doing db
		 * mapping? if (dataElement.exists (this.FIELD_UPDATEMAPPING_SYSTEM)) {
		 * this.p_isMapSystemValues = dataElement.getImportBooleanValue
		 * (this.FIELD_UPDATEMAPPING_SYSTEM); } // if
		 */

		// reload the types?
		if (dataElement.exists(this.FIELD_RELOADTYPES)) {
			this.p_isReloadTypes = dataElement
					.getImportBooleanValue(this.FIELD_RELOADTYPES);
		} // if

		// get business object specific values:
		super.readImportData(dataElement);

	} // readImportData

	/***************************************************************************
	 * Check if the files set in the dataElement could have been created and set
	 * the respective object properties. <BR/>
	 * 
	 * @param dataElement
	 *            the dataElement to read the data from
	 * 
	 * @see ibs.bo.BusinessObject#readImportFiles
	 */
	public void readImportFiles(DataElement dataElement) {
		// call super method
		super.readImportFiles(dataElement);

		// do only change the value in case it exists
		if (dataElement.existsFile(this.FIELD_TRANSLATOR)) {
			// set the new file path of the imported translator file
			// note that is is not used anymore but we need to set the new value
			// in order to be able to delete it in the performChange() method
			this.p_translatorPathName = dataElement
					.getFilePathName(this.FIELD_TRANSLATOR);
		} // if (dataElement.existsFile (FIELD_TRANSLATOR))
		else // no translator found
		{
			this.p_translatorPathName = null;
		} // if (dataElement.existsFile (FIELD_TRANSLATOR))
	} // readImportFiles

	/***************************************************************************
	 * Set the workflow template with a given name. <BR/>
	 * 
	 * @param templateName
	 *            the name of the workflow template to set
	 */
	public void setWorkflowTemplate(String templateName) {
		try {
			this.p_workflowTemplateOid = this.getOidFromObjectName(
					templateName, TypeConstants.TC_WorkflowTemplate);
		} // try
		catch (ObjectNotFoundException e) {
			this.p_workflowTemplateOid = null;
			this.p_workflowTemplateName = "";
		} // catch (ObjectNotFoundException e)
	} // setWorkflowTemplate

	/***************************************************************************
	 * Get the oid of an object with a given name and a given typecode. <BR/>
	 * 
	 * @param objName
	 *            the name of the object to be searched for
	 * @param objTypeCode
	 *            the typecode of the object to be searched for
	 * 
	 * @return The oid.
	 * 
	 * @throws ObjectNotFoundException
	 *             The object was not found.
	 */
	public OID getOidFromObjectName(String objName, String objTypeCode)
			throws ObjectNotFoundException {
		SQLAction action = null;
		StringBuffer queryStr = new StringBuffer(); // the query
		OID resultOid = null;

		try {
			// open db connection - only workaround - db connection must
			// be handled somewhere else
			action = this.getDBConnection();

			// select all deleted objects with the tVersionId of the template
			queryStr.append(" SELECT oid ").append(" FROM ibs_object ").append(
					" WHERE tversionid = '").append(
					this.getTypeCache().getTVersionId(objTypeCode)).append(
					"' AND name = '").append(objName).append("' AND state = ")
					.append(States.ST_ACTIVE).append(" AND islink = ").append(
							SQLConstants.BOOL_FALSE);

			int rowCount = action.execute(queryStr, false);
			if (rowCount > 0) {
				resultOid = SQLHelpers.getQuOidValue(action, "oid");
				// TODO: Note that there is no check if the result has unique!
				// the first object found wins!
			} // if (rowCount > 0)
			// end action:
			action.end();
		} // try
		catch (DBError e) {
			throw new ObjectNotFoundException(objName + " (" + objTypeCode
					+ ") ");
		} // catch
		finally {
			// close db connection in every case - only workaround - db
			// connection must
			// be handled somewhere else
			this.releaseDBConnection(action);
		} // finally
		return resultOid;
	} // getOidFromObjectName

	/***************************************************************************
	 * writes the object data to an DataElement. <BR/>
	 * 
	 * @param dataElement
	 *            the DataElement to write the data to
	 */
	public void writeExportData(DataElement dataElement) {
		// set the business object specific values
		super.writeExportData(dataElement);

		// workflow template:
		dataElement.setExportValue(this.FIELD_WORKFLOWTEMPLATE,
				this.p_workflowTemplateName);
		// show dom tree:
		dataElement
				.setExportValue(this.FIELD_SHOWDOMTREE, this.p_isShowDOMTree);
	} // writeExportData

	/***************************************************************************
	 * Change the object, i.e. store its properties within the database. <BR/>
	 * The properties are gotten from the environment. <BR/>
	 * 
	 * @return The function to be performed after calling this method.
	 * 
	 * @exception NoAccessException
	 *                The user does not have access to this object to perform
	 *                the required operation.
	 * @exception NameAlreadyGivenException
	 *                An object with this name already exists. This exception is
	 *                only raised by some specific object types which don't
	 *                allow more than one object with the same name.
	 * @exception AlreadyDeletedException
	 *                The required object was deleted before the user wanted to
	 *                access it.
	 */
	public int performChange() throws NoAccessException,
			NameAlreadyGivenException, AlreadyDeletedException {
		int retVal = BOConstants.BONEWMENU_NOTHING;

		// ok, change the object
		this.performChange(Operations.OP_CHANGE);

		// validate the template file:
		if (!this.p_isTemplateFileValid && this.showAllowed) {
			retVal = BOConstants.BONEWMENU_RESHOW_FORM;
		} // if (!p_isTemplateFileValid ())

		// reset the global parameter
		this.p_isTemplateFileValid = false;
		this.p_newTemplateFile = null;

		// return the result:
		return retVal;
	} // performChange

	/***************************************************************************
	 * Change the data of a business object in the database using a given
	 * operation. <BR/>
	 * 
	 * @param operation
	 *            Operation to be performed with the object.
	 * 
	 * @exception NoAccessException
	 *                The user does not have access to this object to perform
	 *                the required operation.
	 * @exception NameAlreadyGivenException
	 *                An object with this name already exists. This exception is
	 *                only raised by some specific object types which don't
	 *                allow more than one object with the same name.
	 * 
	 * @see #performChange()
	 * @see #performChangeData(int)
	 */
	public void performChange(int operation) throws NoAccessException,
			NameAlreadyGivenException {

		// check if a translator fil has been set
		// if yes delete it because at that point it is not needed anymore
		if (this.p_translatorPathName != null) {
			// delete the translator file
			if (!FileHelpers.deleteFile(this.p_translatorPathName)) {
				// display an error message
				IOHelpers.showMessage( 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULD_NOT_DELETE_FILE,
						new String[] {this.p_translatorPathName}, env),
					this.app, this.sess, this.env);
			} // if (! FileHelpers.deleteFile (this.p_translatorPathName))
			// reset the translator in order to avoid caching side effects
			this.p_translatorPathName = null;
		} // if (this.p_translatorPathName != null)

		// check if we got a new template file
		// if not just do the perform change
		// and ignore the rest
		if (this.p_newTemplateFile == null) {
			super.performChange(operation);
			// and set p_isTemplateFileValid true to indicate
			// that everything is ok because the performChange () method
			// evaluates this flag
			// not needed anymore?
			// this.p_isTemplateFileValid = true;
		} // if (this.p_newTemplateFile == null)
		else // a new template file has been assigned
		{
			// if the template xml file is valid
			if (this.p_isTemplateFileValid) {
				// due to a caching effect the templateDataElement must
				// be deleted in order to force a read in the
				// getTemplateDataElement() method
				this.p_templateDataElement = null;

				// BB TODO: found a problem when importing the documentTemplate
				// because the containerId can be changed via import and this
				// corrupts
				// the path!!!
				// --> changing the containerOid has now been deactivated

				// the dbmapping must be activated now if neccessary
				// note that the activateDBMapping sets the is p_isUpdateMapping
				// property indicating that the dbmapping must be refreshed
				if (!this.activateDBMapping(this.getTemplateDataElement())) {
					// dbmapping could not be activated!!!
					// set the template file invalid to indicate the error
					// TODO BB050808: should we allow the performChange?
					return;
				} // dbmapping activation failed!

				// change the translator settings
				super.performChange(operation);

				// check if the template has been changed and if reloadTypes is
				// enabled. Note that reload types does a complete reload
				// that is not neccessary everytime leading to bad performance
				// on large systems.
				if (this.p_isReloadTypes) {
					Date startDate = new Date();
					this.env.write("<DIV ALIGN=\"LEFT\">");
					this.env.write("<FONT SIZE=\"2\">");
					this.env.write("Reloading types started at "
							+ DateTimeHelpers.dateTimeToString(startDate)
							+ " ...");
					try {
						// Starting with version 2.2 a DocumentTemplate defines
						// a regular m2
						// type.
						// If a DocumentTemplate is changed the type cache
						// must be reloaded to reflect this changes.
						this.app.p_appInitializer
								.reloadTypes(new ApplicationContext(this.app,
										this.sess, this.env), false);
					} // try
					catch (ApplicationInitializationException e) {
						// TODO: handle exception
						IOHelpers.showMessage(
	                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
	                            DIMessages.ML_MSG_NOTEMPLATESAVAILABLE, env), e,
							this.app, this.sess, this.env, true);
					} // catch
					// show the time elapsed for the reloading types
					DIHelpers.showElapsedTime(this.env, "Finished", startDate,
							new Date(), 0);
					this.env.write("</FONT/></DIV>" + IE302.TAG_NEWLINE);
				} // if (this.p_newTemplateFile != null)
			} // if (this.p_isTemplateFileValid)
			else // template file is not valid!
			{
				// abort the method
				return;
			} // else template file is not valid!
		} // else a new template file has been assigned

		// Check if the DB mapping has to be refreshed.
		// Note that the p_isUpdateMapping flag can be set
		// in the activateDBMapping () method indicating that the db structure
		// has been changed or by activating the update db mapping option
		if (this.p_isUpdateMapping) {
			// refresh the dbmapping
			if (!this.refreshDBMapping(this.getTemplateDataElement())) {
				// TODO BB050808: Should we continue or abord now?
				if (!this.p_isImported) {
					this.showPopupMessage( 
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_TEMPLATE_DBMAPPING_IMPOSSIBLE, env));
				} // if
			} // if (refreshDBMapping (getTemplateDataElement ()))
			//
			// reset the update mapping flag
			this.p_isUpdateMapping = false;
		} // if (this.p_isUpdateMapping)

		// set the p_isTemplateFileValid = true because
		// it is checked in the performChange () method in order to
		// show the edit view in case of an error
		this.p_isTemplateFileValid = true;

		/*
		 * HINT BB050809: not in use anymore because of new structure check!!! //
		 * Check if objects have to be updated due to change in field //
		 * settings like for OBJECTREF, FIELDREF or QUERY fields n case // a new
		 * template file has been assigned // In case a translation has already
		 * been done we do not need // to update the objects because this is
		 * done already been done // during the translation. // // This must be
		 * done AFTER the dbmapping refresh in order to ensure // writing the
		 * data into the correct db structures! // // TODO BB050808: this is a
		 * workaround causing bad performance on large // systems. Settings for
		 * OBJECTREF, FIELDREF or QUERY fields // should be read from the
		 * documenttemplate and not from the // xmldate file! if
		 * (this.p_newTemplateFile != null && (! this.p_isTranslated)) { //
		 * check if objects needs to be changed // Note that the
		 * p_newTemplateDataElement property is set within // the
		 * validateTemplateFile method updateObjects
		 * (this.p_oldTemplateDataElement, this.p_newTemplateDataElement); } //
		 * if (this.p_isTemplateFileValid && this.p_newTemplateFile != null)
		 */
	} // performChange

	/***************************************************************************
	 * Checks if the file is valid for upload. <BR/> Overwrite this method to
	 * perform a check of the file before it is moved in the upload directory.
	 * 
	 * @param filePath
	 *            the file to be checked
	 * 
	 * @return a boolean which is true if the file is valid for upload
	 */
	protected boolean checkIsValidFile(String filePath) {

		// first check any file constraints of the super class
		if (!super.checkIsValidFile(filePath)) {
			return false;
		} // if

		// HACK: this method is called twice (it's a bug in the Base).
		// perform the check only the first time.
		if (this.p_lastEnv != this.env) {
			// remember the environment instance
			this.p_lastEnv = this.env;
			// reset the switch
			this.p_isTemplateFileValid = false;
			// store the new template file name
			this.p_newTemplateFile = filePath;
			// save the informations from the current template file.
			// if the template file is changed we need this info
			// to delete the old dbmapping.
			this.p_oldTemplateDataElement = this.getTemplateDataElement();

			// the check is performed twice
			if (filePath == null) {
				this.p_isTemplateFileValid = this.fileName != null;
			} // if (filePath == null)

			if (!this.p_isTemplateFileValid) {
				// validate the temporary file
				// do not activate the dbmapping. this is done later.
				this.p_isTemplateFileValid = this.validateTemplateFile(
						filePath, false, true);
			} // if (!this.p_isTemplateFileValid)
		} // if (this.p_lastEnv != this.env)

		return this.p_isTemplateFileValid;
	} // checkIsValidFile

	/***************************************************************************
	 * Validates a given template file. <BR/>
	 * 
	 * Basic validations:
	 * <UL>
	 * <LI> the xml template file must conform to the m2 import DTD.
	 * <LI> the object type name defined in the template must be unique.
	 * </UL>
	 * 
	 * Additional validations for db-mapped templates:
	 * <UL>
	 * <LI> the name of the mapping table must be unique.
	 * <LI> the DBMapper-Object must by able to handle all the object attributes
	 * defined in the template file.
	 * </UL>
	 * 
	 * @param templateFile
	 *            the path/name of the template xml file
	 * @param activateDBMapping
	 *            should the mapping be activated or only checked if the the
	 *            mapping parameters are valid.
	 * @param changeStructure
	 *            should the structure of the existing forms be changed to the
	 *            new template structure
	 * 
	 * @return <CODE>true</CODE> if the template file is valid otherwise
	 *         <CODE>false</CODE>.
	 */
	private boolean validateTemplateFile(String templateFile,
			boolean activateDBMapping, boolean changeStructure) {
		boolean translatorNeeded = false;
		boolean isOk = true;

		// create a log object used to collect the error messages
		Log_01 log = new Log_01();
		log.initObject(OID.getEmptyOid(), this.user, this.env, this.sess,
				this.app);
		log.isDisplayLog = true;
		log.isWriteLog = false;
		log.isGenerateHtml = true;

		// separate the path and the file name
		int lastSepIndex = templateFile.lastIndexOf(File.separator);
		String tempName = templateFile.substring(lastSepIndex + 1);
		String tempPath = templateFile.substring(0, lastSepIndex);

		this.env.write(IE302.TAG_NEWLINE + "<DIV ALIGN=\"LEFT\">");
		this.env.write("<FONT SIZE=\"2\">");
		this.env.write("Validation of new template file started ... ");

		// CONSTRAINT: file must exist
		if (!FileHelpers.exists(templateFile)) {
			log.add(DIConstants.LOG_ERROR, templateFile + ": " +  
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_TEMPLATE_INVALIDFILE, env));
			this.env.write("</FONT></DIV>");
			return false;
		} // if (! FileHelpers.exists (templateFile))

		// Basic validations:
		// - the xml template file must conform to the m2 import DTD.
		//
		// read in the selected template file in a data element
		DataElement newTemplateDataElement = this.getDataElementFromFile(
				tempPath, tempName);

		// is the data element valid
		if (newTemplateDataElement != null) {
			// check if the data element has a type name
			if (newTemplateDataElement.p_typeCode == null
					|| newTemplateDataElement.p_typeCode.trim().isEmpty()) {
				log.add(DIConstants.LOG_ERROR,
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_TEMPLATE_TYPENAME_INVALID, env));
				isOk = false;
			} // if data element has type name

			// check if the type name is valid.
			// retrieve the object type names of all yet defined document
			// templates and check if the object type name of the new template
			// is unique.
			if (this
					.isTypeCodeAlreadyDefined(newTemplateDataElement.p_typeCode)) {
				log.add(DIConstants.LOG_ERROR,
					newTemplateDataElement.p_typeCode + ": " +
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_TEMPLATE_TYPENAME_INVALID, env));
				isOk = false;
			} // if (isKnownTypeName (dataElement.p_typeCode)

			// check if the structure of the new template is changed
			if (this.p_oldTemplateDataElement != null) {
				// check if the new template has a changed in a way so that a
				// translator is used
				if (!this.validateTemplateDataElements(newTemplateDataElement,
						this.p_oldTemplateDataElement, log)) {
					log.add(DIConstants.LOG_ENTRY,
							"Template structure has been changed. "
									+ "A translator must be used.");
					// translator is needed!
					translatorNeeded = true;
				} // if (! validateTemplateDataElements ( ...
				else // template structure not been changed
				{
					log.add(DIConstants.LOG_ENTRY, "No translator needed.");
				} // else template structure has not been changed in a way
					// that a translator
				// is nedded
			} // if (this.oldTemplateDataElement)

			// CONSTRAINT: all field names of the values must be distinct
			if (!this.checkDistinctValues(newTemplateDataElement, log)) {
				isOk = false;
			} // if (! checkDistinctValues (dataElement))

			// reset the objectTabs
			this.p_objectTabs = new Vector<DocumentTemplate_01.TabTemplate>();
			// check and set the the tabs definitions
			if (!this.checkTabs(newTemplateDataElement, this.p_objectTabs, log)) {
				isOk = false;
			} // if (! checkTabs (dataElement, this.p_objectTabs))

			// check if all type codes in the p_mayExistIn property
			// are valid m2 base types.
			if (!this.checkTypeCodeList(newTemplateDataElement.p_mayExistIn,
					false, newTemplateDataElement.p_typeCode, log)) {
				isOk = false;
			} // if (checkTypeCodeList (dataElement.p_mayExistIn, true,
				// dataElement.p_typeCode))

			// check if all type codes in the p_mayContain property
			// are valid m2 types.
			if (!this.checkTypeCodeList(newTemplateDataElement.p_mayContain,
					true, newTemplateDataElement.p_typeCode, log)) {
				isOk = false;
			} // if (checkTypeCodeList (dataElement.p_mayContain, false,
				// dataElement.p_typeCode))

			// check the super type code
			if (newTemplateDataElement.p_superTypeCode.length() > 0) {
				String typeCode = newTemplateDataElement.p_superTypeCode;
				if (this.getTypeCache().getTVersionId(typeCode) == TypeConstants.TYPE_NOTYPE) {
					log.add(DIConstants.LOG_ERROR,
					    newTemplateDataElement.p_typeCode + ": " +
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_INVALID_SUPER_TYPECODE, env) +
						" (" + typeCode + ")");
					isOk = false;
				} // if (getCache ().getTVersionId (typeCode) ==
					// TypeConstants.TYPE_NOTYPE)
			} // if (dataElement.p_superTypeCode.length () > 0)

			// check the dbmapping if any defined
			// if (!validateDBMapping (dataElement, activateDBMapping))
			// do only check the structure. dbmapping is activated later
			if (!this.validateDBMapping(newTemplateDataElement, log)) {
				isOk = false;
			} // if mapping not valid

			// validation successfull?
			if (isOk) {
				this.env.write("Validation finished." + IE302.TAG_NEWLINE);
				// if the template structure is changed all existing
				// forms must be transformed with the given translator
				// to match the new structure
				// in case the force translator option has been set the
				// translator
				// will be applied even when there is no change in structure
				if (translatorNeeded || this.p_isForceTranslator) {
					if (this.p_isForceTranslator) {
						this.env.write("Forced translation is activated."
								+ IE302.TAG_NEWLINE);
					} // if

					// check if a translator has been set
					if (this.p_translatorPathName == null) {
						log.add(DIConstants.LOG_ERROR, "No translator set!");
						isOk = false;
					} // if (this.p_translatorPathName == null)
					else // translator present
					{
						// should the structure be changed using a translator?
						if (changeStructure) {
							// now start the typetranslation using the
							// translator
							// file and the new template file as input
							isOk = this.startTranslation(
									this.p_translatorPathName, tempPath,
									tempName, newTemplateDataElement);
						} // if (changeStructure)
					} // else translator present
				} // if structure changed
				// check again if everthing was ok
				if (isOk) {
					// set some type properties
					this.setTypeProperties(newTemplateDataElement);
				} // if (isOk)
			} // if (isOk)
			else // validation not successfull
			{
				this.env.write("Finished with errors!");
			} // else validation not successfull
		} // if (dataElement != null)
		else // data element could not have been read
		{
			log.add(DIConstants.LOG_ERROR, templateFile + ": " +
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_TEMPLATE_INVALIDFILE, env));
			isOk = false;
			this.env.write("Validation aborted!");
		} // else data element could not have been read
		// return result of validation
		this.env.write("</FONT></DIV>" + IE302.TAG_NEWLINE);
		return isOk;
	} // validateTemplateFile

	/***************************************************************************
	 * Set type specific properties using the given dataElement. <BR/>
	 * 
	 * @param dataElement
	 *            the dataElement to read the properties from
	 */
	private void setTypeProperties(DataElement dataElement) {
		// get the type code of the template
		this.p_objectTypeCode = dataElement.p_typeCode;
		// get the language specific type name of the template
		this.p_objectTypeName = dataElement.typename;
		// get the class name of the template
		this.p_objectClassName = dataElement.p_className;
		// get the icon name of the template
		this.p_objectIconName = dataElement.p_iconName;
		// get the mayExistIn list
		this.p_objectMayExistIn = dataElement.p_mayExistIn;
		// get the super type code of the template
		this.p_objectSuperTypeCode = dataElement.p_superTypeCode;
		// get the isSearchable flag
		this.p_objectIsSearchable = dataElement.p_isSearchable;
		// get the isInheritable flag
		this.p_objectIsInheritable = dataElement.p_isInheritable;
		// get the showInMenu flag
		this.p_objectIsShowInMenu = dataElement.p_isShowInMenu;
		// get the showInNews flag
		this.p_objectIsShowInNews = dataElement.p_isShowInNews;
		// get the system display mode of the template
		this.p_systemDisplayMode = dataElement.getSystemSectionDisplayMode();
		// attributes for container templates
		// get the mayContain list
		this.p_objectMayContain = dataElement.p_mayContain;
		// if the template defines a mayConatin list the template is a container
		// template.
		this.p_objectIsContainerType = this.p_objectMayContain.length() > 0;
	} // setTypeProperties

	/***************************************************************************
	 * Start the type translation for the given template file and the translator
	 * stored at the given position.
	 * 
	 * @param translatorFilePath
	 *            fill filepath for the translator file
	 * @param templatePath
	 *            the path of the template
	 * @param templateFileName
	 *            the filename of the template
	 * @param newTemplateDataElement
	 *            The data element where to insert the new data.
	 * 
	 * @return <CODE>true</CODE> if translation was successful,
	 *         <CODE>false</CODE> otherwise.
	 */
	private boolean startTranslation(String translatorFilePath,
			String templatePath, String templateFileName,
			DataElement newTemplateDataElement) {
		boolean isTranslated = false;

		// CONTRAINT: a translator must be given
		if (translatorFilePath != null) {
			// lock all objects depending on this document template
			Type tmpType = this.getTypeCache().getType(
					this.getObjectTVersionId());
			tmpType.lock();
			this.env.allowInstantiation(tmpType.getCode());
			// perform the translation process
			isTranslated = this.performTranslate(translatorFilePath,
					templatePath, templateFileName, newTemplateDataElement);
			// unlock all objects depending on this document template
			this.env.allowInstantiation(null);
			tmpType.unlock();
			// return success of translation
			return isTranslated;
		} // if is a translator given

		// no translator set but needed
		if (!this.p_isImported) {
			this.showPopupMessage(this.p_objectTypeCode + ":\\n" +
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_TEMPLATE_STRUCTURE_CHANGED, env));
		} // if (!this.p_isImported)
		return false;
	} // startTranslation

	/***************************************************************************
	 * Validates the template file and sets some properties for creating a
	 * translator. <BR/>
	 * 
	 * Basic validations: <BR/>
	 *  - the xml template file must conform to the m2 import DTD. <BR/> - the
	 * object type name defined in the template must be unique. <BR/>
	 * 
	 * @param templateFile
	 *            the path/name of the template xml file
	 * 
	 * @return <CODE>true</CODE> if the template file is valid otherwise
	 *         <CODE>false</CODE>.
	 * 
	 * @see #validateTemplateFile
	 */
	private boolean validateTemplateFileForTranslator(String templateFile) {
		String tempPath; // the path of the template file
		String tempName; // the name of the template file
		int lastSepIndex = -1; // index of last separator
		DataElement dataElement; // the current data element
		Log_01 log; // the log object

		// constraint: the template file must not be null or empty:
		if (templateFile == null || templateFile.trim().length() == 0) {
			this.showPopupMessage( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_TEMPLATE_INVALIDFILE, env));
			return false;
		} // if (templateFile == null || templateFile.trim().length () == 0)

		// Basic validations:
		// - the xml template file must conform to the m2 import DTD.
		//
		// read in the selected template file in a data element
		// separate the path and the file name
		lastSepIndex = templateFile.lastIndexOf(File.separator);
		tempName = templateFile.substring(lastSepIndex + 1);
		tempPath = templateFile.substring(0, lastSepIndex);

		// read in the selected template file:
		dataElement = this.getDataElementFromFile(tempPath, tempName);

		// check if the data element is valid:
		if (dataElement != null) // the data element is valid?
		{
			// check if the data element has a type name:
			if (dataElement.p_typeCode == null
					|| dataElement.p_typeCode.trim().length() == 0) {
				this.showPopupMessage(StringHelpers.replace(templateFile, "\\",
						"\\\\")
						+ ":\\n" +  
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_TEMPLATE_TYPENAME_INVALID, env));
				return false;
			} // if data element has type name

			// check if the type name is valid.
			// retrieve the object type names of all yet defined document
			// templates and check if the object type name of the new template
			// is unique.
			if (this.isTypeCodeAlreadyDefined(dataElement.p_typeCode)) {
				this.showPopupMessage(dataElement.p_typeCode + ":\\n" +
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_TEMPLATE_HAS_TEMPLATETYPENAME, env));
				return false;
			} // if (isKnownTypeName (dataElement.p_typeCode)

			// create a log object used for the data element validation
			log = new Log_01();
			log.initObject(OID.getEmptyOid(), this.user, this.env, this.sess,
					this.app);
			log.isDisplayLog = false;
			log.isWriteLog = false;

			// check if the structure of the new template is changed
			if (this.p_oldTemplateDataElement != null) {
				// TODO: check if this is ok?
				boolean isSameStructure = this.validateTemplateDataElements(
						dataElement, this.p_oldTemplateDataElement, log);

				// check if the new template has a different structure
				if (this.p_isForceTranslator || (!isSameStructure)) {
					// initiate the download
					this.createAndDownloadTranslator(dataElement);
					return true;
				} // if (!compatible data elements)

				// structure not changed
				// no change in structure
				this.showPopupMessage(dataElement.p_typeCode + ":\\n" +
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_STRUCTURE_NOT_CHANGED, env));
				// return false to prevent overriding the current template
				// settings:
				return false;
			} // if (this.oldTemplateDataElement)

			// check if all field names of the values are distinct:
			if (dataElement.values != null) {
				// this vector holds the field names
				Vector<String> fieldNames = new Vector<String>();

				for (Iterator<ValueDataElement> iter = dataElement.values
						.iterator(); iter.hasNext();) {
					ValueDataElement value = iter.next();
					String fieldName = value.field;

					// check if the value has a valid field name
					if (fieldName == null || fieldName.length() == 0) {
						this.showPopupMessage(dataElement.p_typeCode + ":\\n" +
	                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
	                            DIMessages.ML_MSG_INVALID_FIELD_NAME, env));
						return false;
					} // if (fieldName = null || fieldName.length () == 0)

					for (int i = 0; i < fieldNames.size(); i++) {
						String name = fieldNames.elementAt(i);
						if (name.equals(fieldName)) {
							this.showPopupMessage(dataElement.p_typeCode
									+ ":\\n" +
			                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
			                            DIMessages.ML_MSG_TEMPLATE_DUPLICATE_NAMES, env)
									+ " (" + name + ")");
							return false;
						} // if (name.equals (fieldName))
					} // for i
					fieldNames.addElement(fieldName);
				} // for iter
			} // if (dataElement.dataElementList != null)

			// validate the DB-mapping options of the new template dataElement

			// check if the dataelement defines a db-mapping.
			boolean isMappingDefined = dataElement.tableName != null
					&& dataElement.tableName.length() > 0;

			if (isMappingDefined) {
				// get a DBMapper object to validate
				// the db-mapping for the document template.
				DBMapper mapper = new DBMapper(this.user, this.env, this.sess,
						this.app);
				(mapper.getLogObject()).isGenerateHtml = true;

				// validate the mapping info of the data element
				if (!mapper.validateMappingInfo(dataElement)) {
					String msg = mapper.getLogObject().toString();
					IOHelpers.showMessage(msg, this.app, this.sess, this.env);
					this.showPopupMessage(dataElement.p_typeCode + ":\\n" +
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_TEMPLATE_DBMAPPING_IMPOSSIBLE, env));
					return false;
				} // if (!mapper.validateMappingInfo (dataElement))

				// check if the mapping table name
				// is already used by another document template.
				if (this.isTableNameAlreadyDefined(dataElement.tableName)) {
					this.showPopupMessage(dataElement.p_typeCode + ":\\n" +
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_MAPPINGTABLE_ALREADY_DEFINED, env));
					return false;
				} // if (isTableNameAlreadyDefined (dataElement.tableName))
			} // if mapping is defined

			return true;
		} // if the data element is valid

		this.showPopupMessage(StringHelpers.replace(templateFile, "\\", "\\\\")
				+ ":\\n" +  
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_TEMPLATE_INVALIDFILE, env));

		return false;
	} // validateTemplateFileForTranslator

	/***************************************************************************
	 * Check if all field names of values of a dataElement are distinct. <BR/>
	 * 
	 * @param dataElement
	 *            The dataElement where to check the values.
	 * @param log
	 *            The log to add loggin messages.
	 * 
	 * @return <code>true</code> if values are ok or <code>false</code>
	 *         otherwise.
	 */
	private boolean checkDistinctValues(DataElement dataElement, Log_01 log) {
		boolean isOk = true;

		// check if all field names of the values are distinct
		if (dataElement.values != null) {
			// this vector holds the field names
			Vector<String> fieldNames = new Vector<String>();
			for (Iterator<ValueDataElement> iter = dataElement.values
					.iterator(); iter.hasNext();) {
				ValueDataElement value = iter.next();
				String fieldName = value.field;

				// check if the value has a valid field name
				if (fieldName == null || fieldName.length() == 0) {
					log.add(DIConstants.LOG_ERROR, dataElement.p_typeCode
							+ ": " +  
	                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
	                            DIMessages.ML_MSG_INVALID_FIELD_NAME, env));
					isOk = false;
				} // if (fieldName = null || fieldName.length () == 0)
				// loop through the fields
				for (int i = 0; i < fieldNames.size(); i++) {
					String name = fieldNames.elementAt(i);
					if (name.equals(fieldName)) {
						log.add(DIConstants.LOG_ERROR, dataElement.p_typeCode
								+ ": " +
		                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
		                            DIMessages.ML_MSG_TEMPLATE_DUPLICATE_NAMES, env)
								+ " (" + name + ")");
						isOk = false;
					} // if (name.equals (fieldName))
				} // for (int i=0; i<fieldNames.size (); i++)
				fieldNames.addElement(fieldName);
			} // for iter
		} // if (dataElement.dataElementList != null)
		// return result
		return isOk;
	} // checkDistinctValues

	/***************************************************************************
	 * Check all tab definitions in the given dataelement and set the new. <BR/>
	 * 
	 * @param dataElement
	 *            the dataElement where to check the tab definitions
	 * @param objectTabs
	 *            vector holding the object tabs
	 * @param log
	 *            log to write the messages to
	 * 
	 * @return <code>true</code> if values are ok or <code>false</code>
	 *         otherwise.
	 */
	private boolean checkTabs(DataElement dataElement,
			Vector<DocumentTemplate_01.TabTemplate> objectTabs, Log_01 log) {
		boolean isOk = true;

		// check the tabs
		if (dataElement.tabElementList != null) {
			// go through all tabdefinitions
			for (Iterator<DataElement> iter = dataElement.tabElementList.dataElements
					.iterator(); iter.hasNext();) {
				DataElement tab = iter.next();
				// if tab is objecttab
				if (DIConstants.TABKIND_OBJECT.equals(tab.p_tabKind)) {
					// get the tab type code
					String tabTypeCode = tab.p_typeCode;
					// any tabTypeCode set?
					if (tabTypeCode == null) {
						tabTypeCode = "";
					} // if
					// get the tVersionId of the tab object
					int tabTVersionId = this.getTypeCache().getTVersionId(
							tabTypeCode);
					// report an error if the tab object is not valid
					if (tabTVersionId == 0) {
						log.add(DIConstants.LOG_ERROR, dataElement.p_typeCode
								+ ": " +  
		                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
		                            DIMessages.ML_MSG_INVALID_TABOBJECT, env)
								+ " (" + tabTypeCode + ")");
						isOk = false;
					} // if (tabTVersionId == 0)
					// tab is ok, add the tab information to the tab vector
					if (tabTVersionId != 0) {
						TabTemplate tabDef = new TabTemplate(tabTVersionId,
								tab.name, tab.description, tab.p_tabCode,
								tab.p_priority);
						// add the tabtemplate to the tab vector
						objectTabs.addElement(tabDef);
					} // if (tabTVersionId != 0)
				} // if TABKIND_OBJECT
				else if (DIConstants.TABKIND_VIEW.equals(tab.p_tabKind)) {
					TabTemplate tabDef = new TabTemplate(0, tab.name,
							tab.description, tab.p_tabCode,
							1, // tabkind VIEW
							AppFunctions.FCT_SHOWTABVIEW, tab.p_className,
							tab.p_priority);
					// add the tabtemplate to the tab vector
					objectTabs.addElement(tabDef);
				} // else if TABKIND_VIEW
				else {
					log.add(DIConstants.LOG_ERROR, dataElement.p_typeCode
							+ ":\\n" +  
	                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
	                            DIMessages.ML_MSG_INVALID_TABKIND, env) 
	                        + " (" + tab.p_tabKind + ")");
					isOk = false;
				} // else wron tabkind
			} // for iter
		} // if (dataElement.tabElementList != null)

		// return the result:
		return isOk;
	} // checkTabs

	/***************************************************************************
	 * Converts all templates with the given tVersionId with the given
	 * translator to a new structure. <BR/>
	 * 
	 * The translation is divided into three parts: <BR/>
	 * 
	 * Part 1 - Reading all documents which are using the template. <BR/> Part 2 -
	 * Performing the translation to a temporary file and validate the new
	 * created file with the new template. <BR/> Part 3 - Deleting the old file
	 * and renaming the new file to the old one. <BR/>
	 * 
	 * @param translatorFilePath
	 *            File path to translator file.
	 * @param newTemplatePath
	 *            The path of the new template.
	 * @param newTemplateName
	 *            The name of the new template.
	 * @param newTemplateDataElement
	 *            The data element where to write the the data to.
	 * 
	 * @return <CODE>true</CODE> if the translation has been carried out
	 *         without errors, otherwise <CODE>false</CODE>.
	 */
	private boolean performTranslate(String translatorFilePath,
			String newTemplatePath, String newTemplateName,
			DataElement newTemplateDataElement) {
		FileWriter transLogWriter = null;
		// the writer for the transLog file
		String startTimeStr = null; // the number of millis since 1970... to
		// make the filename of the new document,
		// while processingn all, unique
		Vector<DocumentTemplate_01.ObjInfo> objects = null;
		boolean translationOK = true; // true if the translation was
										// successfull
		Date startDate = new Date();

		// get the number of millisecons since 1970 to have an unique key for
		// the current translation:
		startTimeStr = "_" + this.getCurTimeString();

		// create the log writer and initialize it:
		transLogWriter = this.performStartTranslation(startDate,
				newTemplatePath, newTemplateName);

		// check if everything is o.k.:
		if (transLogWriter != null) {
			// -----------------------------------------------------------------------------
			// -----------------------------------------------------------------------------
			// - Part 1 - Reading all documents which are using the template:
			// -----------------------------------------------------------------------------
			// -----------------------------------------------------------------------------
			objects = this.getTemplateObjects(transLogWriter, startTimeStr);

			// Retrieve the temporary and recovery database table names:
			String tableName = newTemplateDataElement.tableName;

			// Validate the table name:
			if (!tableName.startsWith(DBMapper.MAPPING_TABLE_PREFIX)) {
				IOHelpers.showMessage(
	                MultilingualTextProvider.getMessage (ServiceMessages.MSG_BUNDLE,
	                    ServiceMessages.ML_MSG_INVALID_TABLEPREFIX, env)
						+ tableName, this.env);

				return false;
			} // if

			// Replace the prefix for the temp table
			String tempTableName = tableName.replaceFirst(
					DBMapper.MAPPING_TABLE_PREFIX,
					DBMapper.TYPE_TRANSLATION_TEMPORARY_MAPPING_TABLE_PREFIX);

			// Replace the prefix for the recovery table
			String recoveryTableName = tableName.replaceFirst(
					DBMapper.MAPPING_TABLE_PREFIX,
					DBMapper.TYPE_TRANSLATION_RECOVERY_TABLE_PREFIX);

			// -----------------------------------------------------------------------------
			// -----------------------------------------------------------------------------
			// - Part 2 - Performing the translation to a temporary file and
			// validate the new
			// created file with the new template.
			// -----------------------------------------------------------------------------
			// -----------------------------------------------------------------------------
			translationOK = this.makeTemporaryTranslation(objects,
					transLogWriter, translatorFilePath, newTemplatePath,
					newTemplateName, newTemplateDataElement, tempTableName);

			// -----------------------------------------------------------------------------
			// -----------------------------------------------------------------------------
			// - Part 3 - Deleting the old file and renaming the new file to the
			// old:
			// -----------------------------------------------------------------------------
			// -----------------------------------------------------------------------------
			int warningCounter = -99999999;

			if (translationOK) {
				warningCounter = this.makeTranslationPermanent(objects,
						newTemplateDataElement, tableName, tempTableName,
						recoveryTableName, transLogWriter);

				// decode warningCounter:
				// find out number of warnings and if everything went o.k.
				translationOK = warningCounter >= 0;
				if (warningCounter == -99999999) {
					warningCounter = 0;
				} // if
				warningCounter = Math.abs(warningCounter);
			} // if translationOK

			// finish the translation process:
			translationOK = this.performFinishTranslation(transLogWriter,
					translationOK, warningCounter, objects.size());
		} // if

		return translationOK;
	} // performTranslate

	/***************************************************************************
	 * Start the translation process. <BR/> This method creates a log writer.
	 * Furthermore it writes the contents of the old and the new template into
	 * this log.
	 * 
	 * @param startDate
	 *            Date when the translation process started.
	 * @param newTemplatePath
	 *            The path of the new template.
	 * @param newTemplateName
	 *            The name of the new template.
	 * 
	 * @return The log writer. <CODE>null</CODE> if the log could not be
	 *         created or another error occurred.
	 */
	private FileWriter performStartTranslation(Date startDate,
			String newTemplatePath, String newTemplateName) {
		File transLog = null; // the log file for translation
		FileWriter transLogWriter = null;
		// the writer for the transLog file
		String logFilePathName = null; // path and name of the log file
		String logFilePath = null; // path of the log file
		String logFileName = null; // name of the log file

		// checks if the type dir exists:
		logFilePath = this.app.p_system.p_m2AbsBasePath
				+ BOPathConstants.PATH_ABS_TYPELOGS + this.p_logDirectory
				+ File.separator;

		File tmpDir = new File(logFilePath);
		if (!tmpDir.isDirectory()) {
			FileHelpers.makeDir(logFilePath);
		} // if (!tmpDir.isDirectory ())

		logFileName = "translation" + this.getCurDateTimeString() + ".log";

		// creates the path and the filename of the current log:
		logFilePathName = this.getBase() + this.app.p_system.p_m2WwwBasePath
				+ BOPathConstants.PATH_TYPELOGS + this.p_logDirectory + "/"
				+ logFileName;

		// create the log file:
		transLog = new File(logFilePath + logFileName);

		this.env.write(IE302.TAG_NEWLINE
				+ "<DIV ALIGN=\"LEFT\"><FONT SIZE=\"2\"> ");
		this.env.write( 
            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                DIMessages.ML_MSG_LOGFILE_PATH, env) + IE302.TAG_NEWLINE
            + "<A HREF=\"" + logFilePathName + "\">" + logFilePathName
			+ "</A><P/>\r\n");

		try {
			this.env.write("Started translation process at "
					+ DateTimeHelpers.dateTimeToString(startDate) + " ..."
					+ IE302.TAG_NEWLINE);

			// create a file writer for the log file:
			transLogWriter = new FileWriter(transLog);

			// add the general log informations:
			transLogWriter
					.write("Log file for translation of all documents depending on document template.");
			transLogWriter.write("\r\n\r\nName of template: " + this.name);
			transLogWriter.write("\r\nOID of template: " + this.oid.toString());
			transLogWriter
					.write("\r\n\r\nThe following part contains the old xml-structure: ");

			// write the old xml-structure to the log-file:
			if (!this.logFile(transLogWriter, this.getAbsFilePath(),
					"OLD XML-STRUCTURE")) {
				transLogWriter = null;
			} // if

			// write the new xml-structure to the log-file:
			if (!this.logFile(transLogWriter, newTemplatePath + File.separator
					+ newTemplateName, "NEW XML-STRUCTURE")) {
				transLogWriter = null;
			} // if
		} // try
		catch (IOException e) {
			IOHelpers.showMessage(e.toString(), this.env);
		} // catch

		return transLogWriter;
	} // performStartTranslation

	/***************************************************************************
	 * Write the content of a file to the log. <BR/>
	 * 
	 * @param log
	 *            The log to write the file to.
	 * @param filePath
	 *            The complete path and name of the file.
	 * @param comment
	 *            Comment to be inserted before and after the file.
	 * 
	 * @return <CODE>true</CODE> if everything went o.k., <CODE>false</CODE>
	 *         otherwise
	 */
	private boolean logFile(FileWriter log, String filePath, String comment) {
		BufferedInputStream fis = null; // a buffered input stream for reading
		// the file
		int tmpByte = -1; // the date byte from the xml-structure
		boolean isOk = true; // everything o.k.?

		try {
			// write header to the log:
			log.write("\r\n\r\n**************************** " + comment
					+ " BEGIN ****************************\r\n\r\n");

			// open input stream for the file:
			fis = new BufferedInputStream(new FileInputStream(filePath));

			// read the data of the old document template and add it to the log:
			tmpByte = fis.read();
			while (tmpByte != -1) {
				log.write(tmpByte);
				tmpByte = fis.read();
			} // while

			fis.close();

			// write footer to the log:
			log.write("\r\n\r\n***************************** " + comment
					+ " END *****************************\r\n\r\n");
		} // try
		catch (IOException e) {
			IOHelpers.showMessage(e.toString(), this.env);

			// set the boolean to false:
			isOk = false;
		} // catch

		return isOk;
	} // logFile

	/***************************************************************************
	 * Get the information of all objects which implement the current template.
	 * <BR/>
	 * 
	 * @param transLogWriter
	 *            The writer for the translation log.
	 * @param fileExt
	 *            File extension for temporary translation file.
	 * 
	 * @return The found objects. Empty, if no objects exist. <CODE>null</CODE>
	 *         if there occurred an error.
	 */
	private Vector<DocumentTemplate_01.ObjInfo> getTemplateObjects(
			FileWriter transLogWriter, String fileExt) {
		// the resultset:
		Vector<DocumentTemplate_01.ObjInfo> objects = new Vector<DocumentTemplate_01.ObjInfo>();
		// get all business objects for this type:
		Vector<BusinessObjectInfo> baseObjects = BOHelpers.findObjects(this
				.getObjectTVersionId(), this.env);

		// have any objects been found ?
		if (baseObjects == null) {
			return objects;
		} // if

		// loop through the base objects and create ObjInfo objects out of them:
		for (Iterator<BusinessObjectInfo> iter = baseObjects.iterator(); iter
				.hasNext();) {
			// create the wrapper object:
			ObjInfo obj = new ObjInfo(iter.next(), this.app, fileExt);

			// add the object to the vector:
			objects.add(obj);
		} // for iter

		// return the result:
		return objects;
	} // getTemplateObjects

	/***************************************************************************
	 * Performing the translation to temporary files and validate the newly
	 * created files with the new template. <BR/>
	 * 
	 * @param objects
	 *            The objects to be translated.
	 * @param transLogWriter
	 *            The writer for the translation log.
	 * @param translatorFilePath
	 *            File path to translator file.
	 * @param newTemplatePath
	 *            The path of the new template.
	 * @param newTemplateName
	 *            The name of the new template.
	 * @param newTemplateDataElement
	 *            Data element to write the data to.
	 * @param tempTableName
	 *            Name of the temporary table for translation process.
	 * 
	 * @return <CODE>true</CODE> if the translation has been carried out
	 *         without errors, otherwise <CODE>false</CODE>.
	 */
	private boolean makeTemporaryTranslation(
			Vector<DocumentTemplate_01.ObjInfo> objects,
			FileWriter transLogWriter, String translatorFilePath,
			String newTemplatePath, String newTemplateName,
			DataElement newTemplateDataElement, String tempTableName) {
		boolean translationOK = true; // true if the translation was
										// successfull
		// objects which where already translated:
		Vector<DocumentTemplate_01.ObjInfo> translatedObjects = new Vector<DocumentTemplate_01.ObjInfo>();

		try {
			// write a notation to the log that this is just the test run:
			transLogWriter.write("\r\n\r\n\r\nTest translation started at "
					+ DateTimeHelpers.dateTimeToString(new Date()) + ":\r\n");

			// read in the new document template. We need this to validate the
			// newly translated xml files. This check is performed for each xml
			// file to ensure the correctness of the translator.
			// DataElement newTemplate =
			// this.getDataElementFromFile (newTemplatePath, newTemplateName);

			// create translator:
			XSLTTransformer translator = new XSLTTransformer(translatorFilePath);

			// create the temporary table:
			// TODO: check if no error
			DBMapper mapper = new DBMapper(this.user, this.env, this.sess,
					this.app);
			mapper.createFormTemplateDBTable(newTemplateDataElement,
					new StringBuffer().append(tempTableName), true, true);

			// loop trough the objects:
			for (Iterator<DocumentTemplate_01.ObjInfo> iter = objects
					.iterator(); iter.hasNext();) {
				DocumentTemplate_01.ObjInfo objInfo = iter.next();
				objInfo.p_translationState = DocumentTemplate_01.ObjInfo.ST_TRANSTEMP;

				// writes the oid and the name of document to the log file:
				transLogWriter.write("\r\n" + objInfo.p_objInfo.p_oid + "\t"
						+ objInfo.p_objInfo.p_name + "\t\t");

				// Does the object exist within the mapping table ?
				if (this.isObjectInMappingTable(
						newTemplateDataElement.tableName,
						objInfo.p_objInfo.p_oid)) {
					// get the object:
					XMLViewer_01 obj = (XMLViewer_01) objInfo
							.getObject(this.env);

					/*
					 * BT 20090904 commented out (reason unknown) //
					 * BOHelpers.getObjectDataElement also creates an instance
					 * first XMLViewer_01 obj = new XMLViewer_01 ();
					 * obj.dataElement = BOHelpers.getObjectDataElement
					 * (objInfo.p_objInfo.p_oid, this.env);
					 */

					// get the DOM document:
					Document doc = obj
							.createDomTree(XMLViewer_01.VIEWMODE_TRANSFORM);

					// create temporary document:
					Document newDocument = XMLWriter.createDocument();

					// translate the document in memory:
					translator.translateFile(doc, newDocument);

					// retrieve the OBJECT node from the translated document
					Node objectNode = XMLHelpers.getNodeByName(newDocument,
							DIConstants.ELEM_OBJECT, this.env);

					// create a new data element to map the translated OBJECT
					// node to by using the new template
					DataElement validTranslation = (DataElement) newTemplateDataElement
							.clone();
					validTranslation.oid = obj.getDataElement().oid;

					// map the OBJECT node to the data element:
					validTranslation = m2XMLFilter.parseObject(objectNode,
							validTranslation, false, true, this.env);

					// // trace
					// DOMHandler serializer = new DOMHandler (this.env, null,
					// null);
					// // trace the original DOM tree
					// System.out.println(serializer.serializeDOM
					// (doc).toString());
					// System.out.println (serializer.serializeDOM
					// (newDocument).toString ());

					// initialize a log object for the validation process
					// for now the log is only a dummy and not really used.
					Log_01 log = new Log_01();
					log.initObject(OID.getEmptyOid(), this.user, this.env,
							this.sess, this.app);

					// check if there is a problem with the data element:;
					if (!this.validateDataElements(newTemplateDataElement,
							validTranslation, true, false, false, false, log)) {
						// write a message after the oid-name tuple:
						transLogWriter
								.write("new generated form is not compatible with the template");

						try {
							// close the log file:
							transLogWriter.close();
						} // try
						catch (IOException e1) {
							IOHelpers.showMessage(e1.toString(), this.env);
						} // catch

						IOHelpers
								.showMessage(
										"The new generated form is not compatible with the template!!\r\n"
												+ "Check the translator for further details!",
										this.env);
						objInfo.p_translationState = DocumentTemplate_01.ObjInfo.ST_FINISHED_ERRORS;
						translationOK = false;
						return translationOK;
					} // if is the data element bad

					// write the data element into the database (temp table):
					validTranslation.tableName = tempTableName;
					if (mapper.createDBEntry(validTranslation)) {
						// write a done after the oid-name tupple:
						transLogWriter.write("done");

						// remember the translated object:
						translatedObjects.add(objInfo);
						// write a message for the user for every ten translated
						// documents to the user:
						int numObjects = translatedObjects.size();
						if ((numObjects % 10) == 0) {
							// write a message to the user:
							this.env.write("<LI>objects " + (numObjects - 9)
									+ " - " + numObjects + " processed ..."
									+ IE302.TAG_NEWLINE);
						} // if (((i + 1) % 10) == 0)
						objInfo.p_translationState = DocumentTemplate_01.ObjInfo.ST_TRANSTEMP_FINISHED;
					} // if
					else {
						// write a message after the oid-name tuple:
						transLogWriter
								.write("error when inserting object into datbase");
					} // else
				} // if is already created
				else {
					transLogWriter
							.write("the object does not exist within the objects mapping table!");
					objInfo.p_translationState = DocumentTemplate_01.ObjInfo.ST_NOT_IN_MAPPING_TABLE;
				} // else is not already created
			} // for iter

			transLogWriter.write("\r\n\r\n\r\nFinished test translation at "
					+ DateTimeHelpers.dateTimeToString(new Date()) + ":\r\n");
		} // try
		catch (IOException e) {
			IOHelpers.showMessage(e, this.env, true);

			// something went wrong, delete the temp table with all new
			// documents
			this.undoTranslation(translatedObjects, newTemplateDataElement,
					tempTableName, transLogWriter, e);

			// set the boolean to false:
			translationOK = false;
		} // catch (IOException e)
		catch (XSLTTransformationException e) {
			IOHelpers.showMessage(e, this.env, true);

			// something went wrong, delete the temp table with all new
			// documents
			this.undoTranslation(translatedObjects, newTemplateDataElement,
					tempTableName, transLogWriter, e);

			// set the boolean to false:
			translationOK = false;
		} // catch (XSLTTransformationException e)
		catch (XMLWriterException e) {
			IOHelpers.showMessage(e, this.env, true);

			// something went wrong, delete the temp table with all new
			// documents
			this.undoTranslation(translatedObjects, newTemplateDataElement,
					tempTableName, transLogWriter, e);

			// set the boolean to false:
			translationOK = false;
		} // catch (XMLWriterException e)
		catch (DBQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // catch (DBQueryException e)
		catch (Exception e) {
			IOHelpers.showMessage(e, this.env, true);

			// something went wrong, delete the temp table with all new
			// documents
			this.undoTranslation(translatedObjects, newTemplateDataElement,
					tempTableName, transLogWriter, e);

			// set the boolean to false:
			translationOK = false;
		} // catch (Exception e)

		// exit the method because of a failure at translation:
		return translationOK;
	} // makeTemporaryTranslation

	/***************************************************************************
	 * Checks if the object exists within the given mapping table.
	 * 
	 * @param tableName
	 *            The mapping table name.
	 * @param oid
	 *            The oid of the object
	 * 
	 * @return if the object already exists in the mapping table
	 * @throws DBQueryException
	 *             An error occurred during execution of query.
	 */
	public boolean isObjectInMappingTable(String tableName, OID oid)
			throws DBQueryException {
		// create the SQL Query to select the objects:
		SelectQuery query = new SelectQuery(new StringBuffer("o.oid"),
				new StringBuffer().append(tableName).append(" o"),
				new StringBuffer("o.oid = ").append(oid), null, null, null);

		SQLAction action = query.execute();

		if (action != null) {
			// close the query:
			query.close(action);

			// and return that the object has already been created
			return true;
		} // if

		// the object has not been created
		return false;
	} // isObjectInMappingTable

	/***************************************************************************
	 * Make the translation permanent. <BR/> The translated objects are filled
	 * with the new data structure and the original and the new xmldata files
	 * are deleted.
	 * 
	 * @param objects
	 *            The objects to be translated.
	 * @param newTemplateDataElement
	 *            The new template data element.
	 * @param originalTableName
	 *            The original database table name.
	 * @param tempTableName
	 *            The temporary database table name.
	 * @param recoveryTableName
	 *            The recovery database table name.
	 * @param transLogWriter
	 *            The writer for the translation log.
	 * 
	 * @return The warning counter: 0: no warnings occurred, everything o.k. >
	 *         0: number of warnings. < 0: number of warnings plus the
	 *         translation was not o.k. -99999999: no warnings, but the
	 *         translation was not o.k..
	 */
	private int makeTranslationPermanent(
			Vector<DocumentTemplate_01.ObjInfo> objects,
			DataElement newTemplateDataElement, String originalTableName,
			String tempTableName, String recoveryTableName,
			FileWriter transLogWriter) {
		boolean translationOK = true; // true if the translation was
										// successfull

		// counter for not translated inactive objects
		int warningCounter = 0;

		try {
			transLogWriter.write("\r\n\r\n\r\nTranslation started at "
					+ DateTimeHelpers.dateTimeToString(new Date()) + ":\r\n");

			transLogWriter
					.write("\r\nChecking if all objects have reached the state TRANSTEMP_FINISHED \t\t");

			// check the state for all documents:
			for (Iterator<DocumentTemplate_01.ObjInfo> iter = objects
					.iterator(); iter.hasNext();) {
				DocumentTemplate_01.ObjInfo obj = iter.next();

				// writes the oid and the name of document to the log file:
				transLogWriter.write("\r\n" + obj.p_objInfo.p_oid + "\t"
						+ obj.p_objInfo.p_name + "\t\t");

				// check if the object is not yet finished:
				if (obj.p_translationState != DocumentTemplate_01.ObjInfo.ST_TRANSTEMP_FINISHED) {
					transLogWriter
							.write(" -> Object not within state TRANSTEMP_FINISHED.");

					// check if the problem was that the object is not in the
					// mapping table
					if (obj.p_translationState == DocumentTemplate_01.ObjInfo.ST_NOT_IN_MAPPING_TABLE) {
						transLogWriter
								.write(" -> Object ignored due to missing entry in database mapping table.");

						// write a message to the user:
						this.env
								.write(IE302.TAG_NEWLINE
										+ "<B>WARNUNG</B>: Object "
										+ obj.p_objInfo.p_oid
										+ " '"
										+ obj.p_objInfo.p_name
										+ "' ignored due to missing entry in database mapping table!");

						warningCounter++;
					} // if
					else {
						transLogWriter
								.write(" -> Finish type translation and perform rollback.");

						this.undoTranslation(objects, newTemplateDataElement,
								tempTableName, transLogWriter, null);

						return translationOK ? warningCounter
								: (warningCounter == 0) ? -99999999
										: -warningCounter;
					} // else
				} // else object has reached the state TRANSTEMP_FINISHED
				else {
					transLogWriter.write("ok");
					obj.p_translationState = DocumentTemplate_01.ObjInfo.ST_TRANSPERMANENT;
				} // else
			} // for iter

			// make the translation permant:
			boolean performRecovery = false;
			boolean performNew = false;

			// Initialize the DBMapper
			DBMapper mapper = new DBMapper(this.user, this.env, this.sess,
					this.app);

			// rename the actual table to the recovery table
			if (this.renameTable(originalTableName, recoveryTableName)) {
				performRecovery = true;
				// rename the newly generated temp table to the actual table
				if (this.renameTable(tempTableName, originalTableName)
						&& mapper.deleteFormTemplateDBTable(
								newTemplateDataElement, new StringBuffer()
										.append(tempTableName), false, true)) {
					performNew = true;

					// now delete the recovery table:
					if (mapper.deleteFormTemplateDBTable(
							newTemplateDataElement, new StringBuffer()
									.append(recoveryTableName), true, false)) {
						// write a done after the oid-name tupple:
						transLogWriter.write("done");
					} // if (FileHelpers.deleteFile (recoveryCopy))
					else // recovery file could not be deleted
					{
						// write a warnin to the log file that the recovery
						// file wasn't deleted:
						transLogWriter
								.write("\r\nWARNING: Recovery database table could not be deleted.\r\n");
					} // else // recovery file could not be deleted
				} // if (FileHelpers.renameFile (newFile, oldFile))
			} // if (FileHelpers.renameFile (oldFile, recoveryCopy))

			// check the state
			if (!performRecovery || !performNew)
			// wasn't the replacing successfully ?
			{
				// write a failed after the oid-name tupple:
				transLogWriter
						.write("failed at database table renaming operation\r\n");
				transLogWriter.write("Recovery copy created: "
						+ performRecovery + "\r\n");
				transLogWriter.write("Old table replaced by new: " + performNew
						+ "\r\n");

				// close the log file:
				transLogWriter.close();
				// set the boolean to false:
				translationOK = false;
			} // if wasn't the replacing successfully

			// set the state to FINISHED for all documents:
			for (Iterator<DocumentTemplate_01.ObjInfo> iter = objects
					.iterator(); iter.hasNext();) {
				DocumentTemplate_01.ObjInfo obj = iter.next();
				obj.p_translationState = DocumentTemplate_01.ObjInfo.ST_FINISHED;
			} // for iter

			// set new template data element:
			if (translationOK) {
				this.p_templateDataElement = newTemplateDataElement;
			} // if translationOK
		} // try
		catch (IOException e) {
			IOHelpers.showMessage(e.toString(), this.env);
			// set the boolean to false:
			translationOK = false;
		} // catch

		return translationOK ? warningCounter
				: (warningCounter == 0) ? -99999999 : -warningCounter;
	} // makeTranslationPermanent

	/***************************************************************************
	 * Renames a database table.
	 * 
	 * @param oldTableName
	 *            The old table name.
	 * @param newTableName
	 *            The new table name
	 * 
	 * @return Returns if the renaming was successful.
	 */
	private boolean renameTable(String oldTableName, String newTableName) {
		// Create the stored procedure
		StoredProcedure sp = new StoredProcedure("p_renameTable",
				StoredProcedureConstants.RETURN_VALUE);

		// the first parameter is the context
		sp.addInParameter(ParameterConstants.TYPE_STRING, "Type Translation");

		// the second parameter is the old table name
		sp.addInParameter(ParameterConstants.TYPE_STRING, oldTableName);

		// the third parameter is the new table name
		sp.addInParameter(ParameterConstants.TYPE_STRING, newTableName);

		SQLAction action = null; // the action object used to access the
		// database

		// execute stored procedure
		try {
			// open db connection - only workaround - db connection must
			// be handled somewhere else
			action = BOHelpers.getDBConnection(this.env);
			// execute stored procedure - return value
			// gives right-information
			action.execStoredProc(sp);
			// end action
			action.end();
		} // try
		catch (DBError e) {
			// show the message:
			IOHelpers.showMessage(e, this.env, true);

			return false;
		} // catch
		finally {
			// close db connection in every case - only workaround - db
			// connection must
			// be handled somewhere else
			BOHelpers.releaseDBConnection(action, this.env);
		} // finally

		return true;
	} // renameTable

	/***************************************************************************
	 * Finish the translation process. <BR/>
	 * 
	 * @param transLogWriter
	 *            The writer for the translation log.
	 * @param translationOK
	 *            Was the translation o.k.?
	 * @param warningCount
	 *            Number of warnings during translation process.
	 * @param objectCount
	 *            Number of translated objects.
	 * 
	 * @return <CODE>true</CODE> if everything was o.k., otherwise
	 *         <CODE>false</CODE>. This is a cumulative value with the
	 *         original translationOK value.
	 */
	private boolean performFinishTranslation(FileWriter transLogWriter,
			boolean translationOK, int warningCount, int objectCount) {
		Date startDate = new Date();
		Date endDate;
		long duration = 0;
		long minutes = 0;
		long seconds = 0;

		try {
			endDate = new Date();
			transLogWriter.write("\r\n\r\n\r\nFinished translation at "
					+ DateTimeHelpers.dateTimeToString(endDate) + "\r\n");
			// write translation summary to the log
			duration = (endDate.getTime() - startDate.getTime()) / 1000;
			minutes = duration / 60;
			seconds = duration - minutes * 60;
			transLogWriter.write("Total elapsed time: " + minutes
					+ " minute(s) " + seconds + " second(s)\r\n");
			transLogWriter.write(objectCount + " object(s) processed.\r\n");

			// translation ok?
			if (translationOK) {
				if (warningCount > 0) {
					transLogWriter
							.write("WARNING: "
									+ warningCount
									+ " object(s) have not been translated due to missing entry database mapping table!\r\n");
					transLogWriter
							.write((objectCount - warningCount)
									+ " object(s) have successfully been translated!\r\n");
				} // if warnings
				else {
					transLogWriter
							.write("All "
									+ objectCount
									+ " object(s) have successfully been translated!\r\n");
				} // else
			} // translation ok?
			else {
				DIHelpers.showElapsedTime(this.env,
						"Finished Translation with errors", startDate, endDate,
						0);
			} // else translation is not ok

			// close the log file:
			transLogWriter.close();
		} // try
		catch (IOException e) {
			IOHelpers.showMessage(e.toString(), this.app, this.sess, this.env);
			// set the boolean to false:
			translationOK = false;
			return translationOK;
		} // catch

		this.env.write("</FONT></DIV>");

		return translationOK;
	} // performFinishTranslation

	/***************************************************************************
	 * Drop all documents which where created through the translation process.
	 * <BR/>
	 * 
	 * @param objects
	 *            The objects which have already been translated.
	 * @param newTemplateDataElement
	 *            The data element with the new data structure.
	 * @param tempTableName
	 *            The name of the temporary table in the database.
	 * @param transLogWriter
	 *            The writer for the transLog file.
	 * @param exc
	 *            Exception which is the reason why we are undoing the
	 *            translation.
	 */
	private void undoTranslation(Vector<DocumentTemplate_01.ObjInfo> objects,
			DataElement newTemplateDataElement, String tempTableName,
			FileWriter transLogWriter, Throwable exc) {
		// instantiate the DBMapper
		DBMapper mapper = new DBMapper(this.user, this.env, this.sess, this.app);

		try {
			// delete the temporary table
			if (!mapper.deleteFormTemplateDBTable(newTemplateDataElement,
					new StringBuffer().append(tempTableName), true, true)) {
				// write a warning to the log file that the temporary
				// database table wasn't deleted:
				transLogWriter
						.write("\r\nWARNING: Temporary database table could not be deleted.\r\n");
			} // temporary table could not be deleted

			// write a failed after the last oid-name tupple:
			transLogWriter.write("failed\r\n\r\n");

			if (exc != null) {
				transLogWriter.write("TranslationException:\r\n");
				transLogWriter.write(exc.toString() + "\r\n");
				transLogWriter.write(Helpers.getStackTraceFromThrowable(exc)
						+ "\r\n");
			} // if exc != null

			// write a message to the user:
			this.env.write(objects.size()
					+ " documents translated before failure!"
					+ IE302.TAG_NEWLINE);

			this.env.write( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_ERROR_WHILE_CREATING_TRANSLATOR, env));
		} // try
		catch (IOException e) {
			IOHelpers.showMessage(e.toString(), this.env);
		} // catch

		try {
			// close the log file:
			transLogWriter.close();
		} // try
		catch (IOException e) {
			IOHelpers.showMessage(e.toString(), this.env);
		} // catch
	} // undoTranslation

	/***************************************************************************
	 * Adds to an existing file name an string which is inserted before the last
	 * dot of the file name. <BR/>
	 * 
	 * @param filename
	 *            The filename which should be extended.
	 * @param extensionString
	 *            String which should be inserted in the filename.
	 * 
	 * @return A file name which has the extensionString added.
	 */
	private static String extendFilename(String filename, String extensionString) {
		return filename.substring(0, filename.lastIndexOf("."))
				+ extensionString
				+ filename.substring(filename.lastIndexOf("."));
	} // extendFilename

	/***************************************************************************
	 * Gets the number of milliseconds since January 1, 1970, 00:00:00 GMT as
	 * string representation. <BR/> The string has a fix length of 20 and is
	 * filled with leading zeros.
	 * 
	 * @return The number of milliseconds since January 1, 1970, 00:00:00 GMT
	 *         with leading zeros as string representation (fix length 20).
	 */
	private String getCurTimeString() {
		Date now = new Date();
		long millis = now.getTime();
		String millisStr = Long.toString(millis);
		int millisStrLen = millisStr.length();
		String zeroStr = new String("00000000000000000000");
		int zeroStrLen = zeroStr.length();

		zeroStr += millisStr;

		return zeroStr.substring(zeroStrLen - millisStrLen - 1);
	} // getCurTimeString

	/***************************************************************************
	 * Gets the current date and time string representation. <BR/> The string
	 * has the form: yyyy_mm_dd__hh_mm_ss
	 * 
	 * @return The current date and time with following form:
	 *         yyyy_mm_dd__hh_mm_ss.
	 */
	private String getCurDateTimeString() {
		StringBuffer dateTimeStr = new StringBuffer();
		GregorianCalendar now;
		int tmp;

		// create a GregorianCalendar with default timezone and locale:
		now = new GregorianCalendar();

		dateTimeStr
		// adds the year to the string with leading zero:
				.append(now.get(Calendar.YEAR))
				// adds the month to the string with leading zero:
				.append((tmp = now.get(Calendar.MONTH) + 1) / 10).append(
						tmp % 10)
				// adds the day to the string with leading zero:
				.append((tmp = now.get(Calendar.DAY_OF_MONTH)) / 10).append(
						tmp % 10).append("_")
				// adds the hours to the string with leading zero:
				.append((tmp = now.get(Calendar.HOUR_OF_DAY)) / 10).append(
						tmp % 10)
				// adds the minutes to the string with leading zero:
				.append((tmp = now.get(Calendar.MINUTE)) / 10).append(tmp % 10)
				// adds the seconds to the string with leading zero:
				.append((tmp = now.get(Calendar.SECOND)) / 10).append(tmp % 10);

		return dateTimeStr.toString();
	} // getCurDateTimeString

	/***************************************************************************
	 * Creates an new translator and offers it the user for downloading. <BR/>
	 */
	public void generateAndDownloadTranslator() {
		String filePath = null; // the name of the new template file
		boolean isTemplateFileValid = false; // is the template file valid?
		int num;

		// check if the force translation option has been activated
		if ((num = this.env.getBoolParam(DIArguments.ARG_FORCE_TRANSLATOR)) > IOConstants.BOOLPARAM_NOTEXISTS) {
			this.p_isForceTranslator = num == IOConstants.BOOLPARAM_TRUE;
		} // if ((num = this.env.getBoolParam
			// (DIArguments.ARG_FORCE_TRANSLATOR)) ...

		// get the file name:
		filePath = this.env.getFilePath(DIArguments.ARG_NEWTEMPLATE);
		// check if we got a file:
		// store the new template file name
		this.p_newTemplateFile = filePath;
		// save the informations from the current template file.
		// if the template file is changed we need this info
		// to delete the old dbmapping.
		this.p_oldTemplateDataElement = this.getTemplateDataElement();

		// validate the temporary file skin deep:
		isTemplateFileValid = this.validateTemplateFileForTranslator(filePath);
		// did we generate a valid template file:
		if (!isTemplateFileValid) {
			// show the generate template form again:
			this.showGenerateTemplateForm();
		} // if (!isTemplateFileValid)
	} // generateAndDownloadTranslator

	/***************************************************************************
	 * Creates an new translator and offers it the user for downloading. <BR/>
	 * 
	 * @param newTemplate
	 *            The new data element for generating the translator.
	 */
	public void createAndDownloadTranslator(DataElement newTemplate) {
		// Retrieve the Template Translator Generator from the factory and
		// forward call createAndDownloadTranslator on the returned instance.
		TemplateTranslatorGeneratorFactory
				.getTemplateTranslatorOfType(
						TemplateTranslatorGeneratorFactory.TEMPLATE_TRANSLATOR_TYPE_XSLT)
				.createAndDownloadTranslator(this.p_oldTemplateDataElement,
						newTemplate, this.user, this.env, this.sess, this.app,
						this.oid);
	} // createAndDownloadTranslator

	/***************************************************************************
	 * Returns true if the given type code is a m2 type code or the type code of
	 * an existing document template. <BR/>
	 * 
	 * @param typeCode
	 *            the type code to check
	 * 
	 * @return <CODE>true</CODE> if the type code is already defined,
	 *         <CODE>false</CODE> otherwise.
	 */
	private boolean isTypeCodeAlreadyDefined(String typeCode) {
		// check if the code has been changed:
		if (typeCode != null
				&& !typeCode.equalsIgnoreCase(this.p_objectTypeCode))
		// codes not equal?
		{
			return this.getTypeCache().findType(typeCode) != null
					|| this.getTypeCache().findTypeByName(typeCode) != null;
		} // if codes not equal

		// the typename is not defined:
		return false;
	} // isTypeCodeAlreadyDefined

	/***************************************************************************
	 * Returns true if the given table name is already used by an existing
	 * document template. <BR/>
	 * 
	 * @param tableName
	 *            the name of the mapping table in the database
	 * 
	 * @return true if the table name is already defined otherwise false.
	 */
	private boolean isTableNameAlreadyDefined(String tableName) {
		// check if the table name is already defined by a document template
		// the action object used to access the database
		SQLAction action = null;
		StringBuffer queryStr = null; // the query

		try {
			// open db connection - only workaround - db connection must
			// be handled somewhere else
			action = this.getDBConnection();

			// check if the table name is already defined by a document template
			// ATTENTION!!
			// Select the templates in ALL domains!!
			// Table names must be unique over all domains!
			// ATTENTION!!
			queryStr = new StringBuffer().append("SELECT o.oid, t.tableName")
					.append(" FROM ibs_object o, ibs_DocumentTemplate_01 t")
					.append(" WHERE o.oid = t.oid AND o.state = 2 ").append(
							"     AND UPPER (t.tableName) = '").append(
							tableName.toUpperCase()).append("'");

			// execute the query
			int rowCount = action.execute(queryStr, false);
			if (rowCount > 0) {
				while (!action.getEOF()) {
					// if the result belongs to this document template
					// the type name is not defined by another template.
					try {
						// get the oid to check if the result belongs to this
						// object
						OID resultOid = new OID(action.getString("oid"));
						if (!resultOid.equals(this.oid)) {
							// the table name is already defined
							return true;
						} // if (! resultOid.toString ().equals
							// (this.oid.toString ()))
					} // try
					catch (IncorrectOidException e) {
						// we got a incorrect oid!!
						// database is inconsistent!!
						IOHelpers.showMessage(
								"isTableNameAlreadyDefined: incorrect OID!", e,
								this.app, this.sess, this.env, true);
						return true;
					} // catch
					action.next();
				} // while (!action.getEOF ())
			} // if (rowCount > 0)

			// the typename is not defined
			return false;
		} // try
		catch (DBError e) {
			// get all errors (can be chained):
			String allErrors = "";
			String h = new String(e.getMessage());
			h += e.getError();
			// loop through all errors and concatenate them to a String:
			while (h != null) {
				allErrors += h;
				h = e.getError();
			} // while
			// show the message
			IOHelpers.printMessage(allErrors);
		} // catch
		finally {
			try {
				action.end();
			} // try
			catch (DBError e) {
				// TODO: handle the exception
			} // catch
			// close db connection in every case - only workaround - db
			// connection must
			// be handled somewhere else
			this.releaseDBConnection(action);
		} // finally

		// if somethings goes wrong: the table name is already defined
		return true;
	} // isTableNameAlreadyDefined

	/***************************************************************************
	 * Checks if the dbmapping has to be changed and a ctivates the db-mapping
	 * for the document template by creating it and deletes any previous
	 * dbmapper tables. <BR/>
	 * 
	 * @param dataElement
	 *            the data element of the template file
	 * 
	 * @return <CODE>true</CODE> on success otherwise <CODE>false</CODE>.
	 * 
	 */
	private boolean activateDBMapping(DataElement dataElement) {
		// get a DBMapper object to validate/activate/deactivate
		// the db-mapping for the document template.
		DBMapper mapper = new DBMapper(this.user, this.env, this.sess, this.app);
		(mapper.getLogObject()).isGenerateHtml = true;

		// check if the data element defines a db-mapping.
		if (dataElement.tableName != null && dataElement.tableName.length() > 0) {
			// get the extended mapping info for the new and the old
			// data element.
			// Note that this method is an workaround because the old mapping
			// info
			// did not store the type information which can lead to problems
			// when
			// only the type of a field has been changed and a new db mapping
			// would become neccessary. This case has not correctly been
			// recognized
			// in the old solution. Because the mappingInfo is stored in the
			// database and used to do the dbmapping we can not get rid of the
			// old solution
			String newExtMappingInfo = this.extractExtMappingInfo(dataElement);
			String oldExtMappingInfo = this
					.extractExtMappingInfo(this.p_oldTemplateDataElement);
			// save the old mapping info
			String oldMappingTableName = this.p_mappingTableName;
			StringBuffer oldMappingProcCopy = this.p_mappingProcCopy;
			String oldMappingInfo = this.p_mappingInfo;
			// get the new mapping info from the data element
			if (!this.extractMappingInfo(dataElement, mapper)) {
				// restore the old mapping info
				this.p_mappingTableName = oldMappingTableName;
				this.p_mappingProcCopy = oldMappingProcCopy;
				this.p_mappingInfo = oldMappingInfo;
				// get the log messages from the mapper
				String msg =  
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_TEMPLATE_DBMAPPING_IMPOSSIBLE, env) 
                    + " " + mapper.getLogObject().toString();
				IOHelpers.showMessage(msg, this.app, this.sess, this.env);
				return false;
			} // if (!mapper.validateMappingInfo ())

			// check if the mapping has changed
			// note that we use the extended mapping info to compare
			// if any structual changes have been made
			if (oldMappingTableName == null
					|| !this.p_mappingTableName.equals(oldMappingTableName)
					|| oldMappingProcCopy == null
					|| !this.p_mappingProcCopy.toString().equals(
							oldMappingProcCopy.toString())
					|| oldMappingInfo == null
					|| !newExtMappingInfo.equals(oldExtMappingInfo)) {
				// if the mapping has changed the mapping must be updated
				// in any case.
				this.p_isUpdateMapping = true;
			} // if (isMappingChanged)

			// should the mapping be updated ?
			if (this.p_isUpdateMapping) {
				// check if the database mapping table already existed before
				boolean oldMappingTableExists = this.p_oldTemplateDataElement != null
						&& this.p_oldTemplateDataElement.tableName != null
						&& this.p_oldTemplateDataElement.tableName.length() > 0;

				// the template data element of the old template
				// is used to delete the existing mapping.
				if (oldMappingTableExists) {
					// delete the old mapping table stored procedures in the
					// database but not the table
					if (!mapper.deleteFormTemplateDBTable(
							this.p_oldTemplateDataElement, null, false, true)) {
						String msg =  
	                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
	                            DIMessages.ML_MSG_TEMPLATE_DBMAPPING_IMPOSSIBLE, env)
							+ " " + mapper.getLogObject().toString();
						IOHelpers.showMessage(msg, this.app, this.sess,
								this.env);
					} // if (!mapper.deleteFormTemplateDBTable
						// (this.p_oldTemplateDataElement))
				} // if (this.p_oldTemplateDataElement != null)

				// create the new mapping table in the database
				if (!mapper.createFormTemplateDBTable(dataElement, null,
				// create a new mapping table if none existed before
						!oldMappingTableExists,
						// recreate the stored procedures
						true)) {
					// no db-mapping possible -> this is an error!
					// PROB TODO KR 20098025 raise error
					String msg =  
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_TEMPLATE_DBMAPPING_IMPOSSIBLE, env)
						+ " " + mapper.getLogObject().toString();
					IOHelpers.showMessage(msg, this.app, this.sess, this.env);
					return false;
				} // if (!mapper.createFormTemplateDBTable (dataElement))
				// now the dbmapping is active
			} // if (this.p_isUpdateMapping)
		} // if (isMappingDefined)
		else // no dbmapping activated
		{
			// no db-mapping possible -> this is an error!
			// PROB TODO KR 20098025 raise error
			// has an old dbmapping been set that should be deleted
			if (this.p_oldTemplateDataElement != null
					&& this.p_oldTemplateDataElement.tableName != null
					&& this.p_oldTemplateDataElement.tableName.length() > 0) {
				// delete the old mapping table in the database
				if (!mapper.deleteFormTemplateDBTable(
						this.p_oldTemplateDataElement, true)) {
					String msg =  
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_TEMPLATE_DBMAPPING_IMPOSSIBLE, env)
						+ " " + mapper.getLogObject().toString();
					IOHelpers.showMessage(msg, this.app, this.sess, this.env);
				} // if (!mapper.deleteFormTemplateDBTable
					// (this.p_oldTemplateDataElement))
			} // if (this.p_oldTemplateDataElement != null)
			// now the dbmapping is active
			this.p_mappingTableName = null;
			this.p_mappingProcCopy = null;
			this.p_mappingInfo = null;
		} // else if (isMappingDefined)
		// return true to indicate that everything was ok
		return true;
	} // activateDBMapping

	/***************************************************************************
	 * Validates db-mapping for the given data element. <BR/>
	 * 
	 * @param dataElement
	 *            The data element of the template file.
	 * @param log
	 *            The log to add logging messages.
	 * 
	 * @return <CODE>true</CODE> on success otherwise <CODE>false</CODE>.
	 */
	private boolean validateDBMapping (DataElement dataElement, Log_01 log) {
		boolean isOk = true;

		// check if a new mapping has been set
		if (dataElement.tableName != null && dataElement.tableName.length() > 0) {
			// get a DBMapper object to validate/activate/deactivate
			// the db-mapping for the document template.
			DBMapper mapper = new DBMapper(this.user, this.env, this.sess,
					this.app);
			mapper.setLogObject(log);

			// validate the mapping info of the data element
			if (!mapper.validateMappingInfo(dataElement)) {
				log.add(DIConstants.LOG_ERROR, dataElement.p_typeCode + ": " +
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_TEMPLATE_DBMAPPING_IMPOSSIBLE, env));
				isOk = false;
			} // if (!mapper.validateMappingInfo (dataElement))
			
			// check if the mapping table name
			// is already used by another document template.
			if (this.isTableNameAlreadyDefined(dataElement.tableName)) {
				log.add(DIConstants.LOG_ERROR, dataElement.p_typeCode + ": " +
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_MAPPINGTABLE_ALREADY_DEFINED, env));
				isOk = false;
			} // if (isTableNameAlreadyDefined (dataElement.tableName))
			
			// check for additional warnings
			// these are cases that produce a valid template but the user
			// must shall be aware of these points
			validateForWarnings (dataElement, log);			
						
		} // if (isMappingDefined)
		return isOk;

	} // validateDBMapping

	/***************************************************************************
	 * Check the given template only for warnings. These are cases
	 * that are valid but the user shall be aware of.
	 * 
	 * @param dataElement
	 *            The data element of the template file.
	 * @param log
	 *            The log to add logging messages.
	 */	
	public void validateForWarnings (DataElement dataElement, Log_01 log)
	{
		// loop trough the value elements:
		for (Iterator<ValueDataElement> templateValuesIter = dataElement.values
				.iterator(); templateValuesIter.hasNext();) 
		{
			// check for end of values:
			// more values exist
			// get template value:
			ValueDataElement value = templateValuesIter.next();
			
			// check for dbfield settings
	    	// empty mapping is possible for QUERYSELECTIONBOX
	    	// and FIELDREF but note that this is a special case
	    	// and must be considered a HACK when used
	    	// for downward compatibility we keep this behaviour
			// but show a warning 
	        if (value.mappingField == null &&
	        	(DIConstants.VTYPE_QUERYSELECTIONBOX.equals (value.type) ||
    			DIConstants.VTYPE_QUERYSELECTIONBOXINT.equals (value.type) ||
    			DIConstants.VTYPE_QUERYSELECTIONBOXNUM.equals (value.type) ||
    			DIConstants.VTYPE_FIELDREF.equals (value.type)))
	        {
				log.add (DIConstants.LOG_WARNING, dataElement.p_typeCode + ": " +
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_MISSINGDBMAPPING, env) 
					+ "\"" + value.field + "\"");                	
	    	} // if (value.type.equals (DIConstants.VTYPE_QUERYSELECTIONBOX) ..
		} // for					
	} // validateForWarnings
	
	
	/***************************************************************************
	 * Refresh the dbmapping with the settings from the given dataelement. <BR/>
	 * The old dbmapper table will be completly deleted and the dbmapper table
	 * will be rebuild again using the information from the templates
	 * dataelement. <BR/>
	 * 
	 * @param dataElement
	 *            the data element of the template file
	 * 
	 * @return <CODE>true</CODE> on success otherwise <CODE>false</CODE>.
	 */
	private boolean refreshDBMapping(DataElement dataElement) {
		// get a DBMapper object to update the mapping
		DBMapper mapper = new DBMapper(this.user, this.env, this.sess, this.app);
		(mapper.getLogObject()).isGenerateHtml = true;
		(mapper.getLogObject()).isDisplayLog = true;

		// BT 20090903: Since the data is only stored within the database
		// the tables must not be deleted anymore.
		// Only the stored procedures have to be recreated.
		//
		// Is this necessary at all because stored procedures
		// are already recreated within activateDBMapping?

		// delete the old mapping table in the database
		if (!mapper.deleteFormTemplateDBTable(dataElement, null, false, true)) {
			String msg =  
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_TEMPLATE_DBMAPPING_IMPOSSIBLE, env) 
                + " " + mapper.getLogObject().toString();
			IOHelpers.showMessage(msg, this.app, this.sess, this.env);
			return false;
		} // if (!mapper.deleteFormTemplateDBTable
			// (this.p_oldTemplateDataElement))

		// create the new mapping table in the database
		if (!mapper.createFormTemplateDBTable(dataElement, null, false, true)) {
			String msg =  
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_TEMPLATE_DBMAPPING_IMPOSSIBLE, env) 
                + " " + mapper.getLogObject().toString();
			IOHelpers.showMessage(msg, this.app, this.sess, this.env);
			return false;
		} // if (!mapper.createFormTemplateDBTable (dataElement))

		/*
		 * BT 20090903: Furthermore the creation of all database entries is not
		 * needed anymore. // set the oid of the template in the dataElement //
		 * because this will be checked in mapper.createAllDBEntries () // HINT
		 * 050809: why this is check neccessary? dataElement.oid = this.oid; //
		 * because the table has completely been rebuild // we do not to delete
		 * the entries if (!mapper.createAllDBEntries (dataElement,
		 * this.p_isMapSystemValues)) { String msg =
		 * DIMessages.MSG_TEMPLATE_DBMAPPING_IMPOSSIBLE + " " +
		 * mapper.getLogObject ().toString (); IOHelpers.showMessage (msg,
		 * this.app, this.sess, this.env); return false; } // if
		 * (!mapper.createAllDBEntries (dataElement))
		 */

		// return true to indicate that everthing was successfull
		return true;
	} // refreshDBMapping

	/***************************************************************************
	 * Returns the template file as a data element. <BR/>
	 * 
	 * @return The data element created from the template file or
	 *         <CODE>null</CODE> on error.
	 */
	public DataElement getTemplateDataElement() {
		// check if the template data element is loaded:
		if (this.p_templateDataElement == null) // data element not loaded?
		{
			// get the data element:
			this.performGetTemplateDataElement();
		} // if data element not loaded

		// return the current template data element:
		return this.p_templateDataElement;
	} // getTemplateDataElement

	/***************************************************************************
	 * Gets the template data out of the template file. <BR/> The template is
	 * stored in the property {@link #p_templateDataElement
	 * p_templateDataElement}.
	 */
	private void performGetTemplateDataElement() {
		this.p_templateDataElement = this.getDataElementFromFile(this
				.getAbsPath(), this.fileName);
	} // performGetTemplateDataElement

	/***************************************************************************
	 * Returns the template file as a data element. <BR/>
	 * 
	 * @param absFilePath
	 *            the file path
	 * @param xmlFile
	 *            the file name
	 * 
	 * @return the data element created from the template file or
	 *         <CODE>null</CODE> on error.
	 */
	private DataElement getDataElementFromFile(String absFilePath,
			String xmlFile) {
		// CONSTRAINT: the xml file must not be null
		if (xmlFile == null || xmlFile.length() == 0) {
			return null;
		} // if

		// a xml file has been set
		// read in the selected template file:
		DataElement dataElement = DIHelpers.readTemplateDataFile(absFilePath,
				xmlFile, this.env);

		// if no typename is set set the typecode is typename
		if (dataElement != null) {
			if (dataElement.typename == null
					|| dataElement.typename.length() == 0) {
				dataElement.typename = dataElement.p_typeCode;
			} // if no type na,e set
		} // if (dataElement != null)

		// check for additional linkFields within the dataElement
		if (dataElement != null && dataElement.values != null && !dataElement.values.isEmpty ()) 
		{
		    // iterate over all values and check if 'showInLinks'-field is set
		    Iterator<ValueDataElement> dataElementIter = dataElement.values.iterator ();
		    while(dataElementIter.hasNext ())
		    {
		        ValueDataElement oneDataElement = dataElementIter.next ();
		        // check if the 'showInLinks'-field is set and add it to the link fields
		        if (DIConstants.ATTRVAL_YES.equalsIgnoreCase (oneDataElement.p_showInLinks))
		        {
		            // initialize with an empty vector if p_linkFields is NULL
		            if (p_linkFields == null)
		            {
		                p_linkFields = new Vector<ValueDataElement> ();
		            } // if
		            // add the ValueDataElement to the link fields
		            p_linkFields.add(oneDataElement);
		        } // if
		    } // while
		} // if

		return dataElement;
	} // getDataElementFromFile

	/***************************************************************************
	 * Validates a 'imported' data element with the template file. <BR/> Returns
	 * true if the given data element is conform to the template file.
	 * 
	 * @param dataElement
	 *            the data element to validate
	 * @param isForUpdate
	 *            if this is set to true only the fields in the given
	 *            dataelement must match with the template (partial update)
	 * 
	 * @param log
	 *            the log object for error report
	 * 
	 * @return true if the data element conforms with the templte file otherwise
	 *         false.
	 */
	public boolean validateImportDataElement(DataElement dataElement,
			boolean isForUpdate, Log_01 log) {

		if (this.p_validationDataElement == null) {
			// create a data element from template file
			this.p_validationDataElement = this.getTemplateDataElement();
		} // if (this.p_validationDataElement == null)

		// for an import the tabs not validated.
		return this.validateDataElements(this.p_validationDataElement,
				dataElement, true, false, isForUpdate, false, log);
	} // validateImportDataElement

	/***************************************************************************
	 * Validates two template data element. <BR/> Returns true if the templates
	 * are 'compatible'.
	 * 
	 * @param template1
	 *            the first template data element
	 * @param template2
	 *            the second template data element
	 * @param log
	 *            the log object for error report
	 * 
	 * @return true if the templates are 'compatible' otherwise false.
	 */
	private boolean validateTemplateDataElements(DataElement template1,
			DataElement template2, Log_01 log) {
		// for templates the completed structure and the tabs are validated.
		return this.validateDataElements(template1, template2, false, false,
				false, true, log);
	} // validateTemplateDataElements

	/***************************************************************************
	 * Compares an object dataelement with an template dataelement. <BR/>
	 * Returns true if the object dataelement is 'compatible' with the template.
	 * 
	 * 'compatible' means the two data elements have the same structure. For tab
	 * objects only the type code is checked, the values are ignored.
	 * 
	 * @param template
	 *            the template dataelement
	 * @param data
	 *            the object dataelement to validate
	 * @param isImport
	 *            is the validation for a import data element
	 * @param isTabObject
	 *            the data elements are tab objects
	 * @param isPartial
	 *            the object dataelement must not contain all fields defined in
	 *            the template dataelement. this mode is usefull for the UPDATE
	 *            import
	 * @param isTypeTranslatorCheck
	 *            Indicates if the validation is performed to decide if a type
	 *            translator is needed.
	 * @param log
	 *            the log object for error reports
	 * 
	 * @return true if the data elements are 'compatible' otherwise false.
	 */
	private boolean validateDataElements(DataElement template,
			DataElement data, boolean isImport, boolean isTabObject,
			boolean isPartial, boolean isTypeTranslatorCheck, Log_01 log) {

		boolean isOk = true;

		// the partial validation is only allowed for the UPDATE import process.
		if (!isImport && isPartial) {
			return false;
		} // (!isImport && isPartial)

		// the data elements must not be null
		if (template == null || data == null) {
			log.add(DIConstants.LOG_ERROR, "template or data is null");
			isOk = false;
		} // if parameters invalid

		// the data elements must have a type code
		if (template.p_typeCode == null || data.p_typeCode == null) {
			log.add(DIConstants.LOG_ERROR,
					"template.p_typeCode or data.p_typeCode is null!");
			isOk = false;
		} // if typecodes not valid

		// check the type code
		if (!template.p_typeCode.equalsIgnoreCase(data.p_typeCode)) {
			log.add(DIConstants.LOG_ERROR, "Different type codes: '"
					+ template.p_typeCode + "' <> '" + data.p_typeCode + "'");
			isOk = false;
		} // if typecodes are different

		// for the import the values of tab objects are not validated!
		if (isImport && isTabObject) {
			return isOk;
		} // if

        // check if the old version of the formtemplate has been database mapped
        if (isTypeTranslatorCheck && !isTabObject)
        {
            if (data.tableName == null || data.tableName.isEmpty ())
            {
                log.add (DIConstants.LOG_ENTRY,
                        "Formtemplate has not been database mapped.");
                return isOk;
            } // if
        } // if

		// now validate the values of the data elements
		if (!this.validateValues(data, template, isPartial,
				isTypeTranslatorCheck, log)) {
			isOk = false;
		} // if (validateValues (data, template, log)

		// now check the tabs.
		// for an import data element the tabs are optional
		if (!isImport) {
			// check the tabs
			if (template.tabElementList != null) {
				// check if the dataElement has any tabs
				if (data.tabElementList == null) {
					log.add(DIConstants.LOG_WARNING,
							"Different number of tabs.");
					return false;
				} // if (data.tabElementList == null)

				Iterator<DataElement> tabIter = data.tabElementList.dataElements
						.iterator();

				for (Iterator<DataElement> templateTabIter = template.tabElementList.dataElements
						.iterator(); templateTabIter.hasNext();) {
					if (!tabIter.hasNext()) {
						log.add(DIConstants.LOG_WARNING,
								"Different number of tabs.");
						return false;
					} // if

					DataElement templateTab = templateTabIter.next();
					DataElement tab = tabIter.next();

					// recursive check
					if (!this.validateDataElements(templateTab, tab, isImport,
							true, isPartial, isTypeTranslatorCheck, log)) {
						isOk = false;
					} // if (! validateDataElements (templateTab, tab, ...
				} // for templateTabIter
			} // if (template.tabElementList != null)
			else {
				if (data.tabElementList != null) {
					log.add(DIConstants.LOG_WARNING,
							"Different number of tabs.");
					return false;
				} // if (data.tabElementList != null)
			} // else if (template.tabElementList != null)
		} // if not isImport

		// return the result
		return isOk;
	} // validateDataElements

	/***************************************************************************
	 * Compares an object dataelement with an template dataelement. <BR/>
	 * Returns true if the object dataelement is 'compatible' with the template.
	 * 
	 * 'compatible' means the two data elements have the same structure in their
	 * field definitions. <BR/>
	 * 
	 * @param template
	 *            the template dataelement
	 * @param data
	 *            the object dataelement to validate
	 * @param log
	 *            the log object for error reports
	 * @param isPartial
	 *            Shall the validation be done partially?
	 * @param isTypeTranslatorCheck
	 *            Indicates if the validation is performed to decide if a type
	 *            translator is needed.
	 * 
	 * @return true if the data elements are 'compatible' otherwise false.
	 */
	private boolean validateValues(DataElement data, DataElement template,
			boolean isPartial, boolean isTypeTranslatorCheck, Log_01 log) {
		boolean isOk = true;

		// check the values
		if (template.values != null) {
			if (data.values == null) {
				log.add(DIConstants.LOG_ERROR, "Different number of values.");
				return false;
			} // if (data.values == null)

			// if the isPartial option is set only the value fields defined in
			// the object dataelement are validated.in this case the order of
			// the
			// value fields must not match the order in the template dataelement
			if (isPartial) {
				// validate the entire structure.
				// any value field in the template must appare also in the
				// object dataelement and must have the same type.
				// loop trough the value elements:
				for (Iterator<ValueDataElement> iter = data.values.iterator(); iter
						.hasNext();) {
					// get the value from the object dataelement:
					ValueDataElement value = iter.next();
					// try to find the value in the template
					ValueDataElement templateValue = template
							.getValueElement(value.field);
					if (templateValue == null) {
						log.add(DIConstants.LOG_WARNING, "Value field '"
								+ value.field + "' not found in the template.");
						isOk = false;
					} // if value not found
					// check the field types
					if (!value.type.equals(templateValue.type)) {
						log.add(DIConstants.LOG_WARNING, "Different types: '"
								+ value.field + " (" + value.type + ")' <> '"
								+ templateValue.field + " ("
								+ templateValue.type + ")'");
						isOk = false;
					} // if field type is not the same
					//
					// HINT BB050808: Note that the field settings are ignored
					// when isPartial is activated
					//
				} // for iter
			} // if isPartial
			else {
				// validate all value fields in the template dataelement
				// any value field in the template must appare in the
				// object dataelement at the same position and must have
				// the same type.
				Iterator<ValueDataElement> valuesIter = data.values.iterator();
				// loop trough the value elements:
				for (Iterator<ValueDataElement> templateValuesIter = template.values
						.iterator(); templateValuesIter.hasNext();) {
					// check for end of values:
					if (!valuesIter.hasNext()) {
						log.add(DIConstants.LOG_WARNING,
								"Different number of values.");
						return false;
					} // if (!values.hasMoreElements ())

					// more values exist
					// get template value:
					ValueDataElement templateValue = templateValuesIter.next();
					// get the object value:
					ValueDataElement value = valuesIter.next();

					// check the field names
					if (!templateValue.field.equals(value.field)) {
						log.add(DIConstants.LOG_WARNING, "Different fields: '"
								+ templateValue.field + "' <> '" + value.field
								+ "'");
						isOk = false;
					} // if field names are different

					// check the field types
					if (!templateValue.type.equals(value.type)) {
						log.add(DIConstants.LOG_WARNING, "Different types: '"
								+ templateValue.field + "("
								+ templateValue.type + ")' <> '" + value.field
								+ "(" + value.type + ")'");

						isOk = false;
					} // if field types are different

					// PROB TODO KR 20090823: Because xmldata files are not
					// longer existing the field settings must be derived from
					// the template.
					// now check the field settings
					// in order to force a type translation in case
					// just the settings of a field (like OBJECTREF, QUERY etc)
					// have been changed
					// Note that in future the field settings should be
					// read from the template and not from the xmldata
					// anymore
					if (!templateValue.equals(value)) {
						StringBuilder sb = new StringBuilder(
								"Different field settings: '"
										+ templateValue.field + "' <> '"
										+ value.field + "'");

						// Check if a translator is necessary
						if (!isTypeTranslatorCheck
								|| !(DIHelpers.compareStr(
										templateValue.mappingField,
										value.mappingField) && DIHelpers
										.compareStr(templateValue.mandatory,
												value.mandatory))) {
							sb.append(" => not compatible - Translator needed");
							isOk = false;
						} // if
						else {
							sb.append(" => compatible");
						} // else

						log.add(DIConstants.LOG_WARNING, sb.toString());
					} // if (!templateValue.equals (value))
				} // for templateValuesIter

				if (valuesIter.hasNext()) {
					log.add(DIConstants.LOG_WARNING,
							"Different number of values.");
					return false;
				} // if (values.hasMoreElements ())
			} // else if isPartial
		} // if (template.values != null)
		else // no template values
		{
			if (data.values != null) {
				log.add(DIConstants.LOG_WARNING, "Different number of values.");
				return false;
			} // if (data.values != null)
		} // else no template values
		return isOk;
	} // validateValues

	/***************************************************************************
	 * Extracts the mapping info from the given data element. The mapping info
	 * will include fieldname, type and mappingField separated by carriage
	 * returns. <BR/> Note that this method is an workaround because the old
	 * mapping info did not store the type information which can lead to
	 * problems when only the type of a field has been changed and a new db
	 * mapping would become neccessary. This case has not correctly been
	 * recognized in the old solution. <BR/>
	 * 
	 * @param dataElement
	 *            the data element representing the template file
	 * 
	 * @return the generated mapping info
	 */
	private String extractExtMappingInfo(DataElement dataElement) {
		StringBuffer mappingInfo = new StringBuffer();

		// check if the dataElement exists:
		if (dataElement != null) {
			// loop trough the value elements and store the value field names
			// and table field names in a string:
			for (Iterator<ValueDataElement> iter = dataElement.values
					.iterator(); iter.hasNext();) {
				ValueDataElement value = iter.next();

				// check only the mapped values
				if (value.mappingField != null) {
					// the form field names and table field names are stored
					// sequencially in a string separated be a \n character.
					// just the type of a field has been changed
					mappingInfo.append(value.field).append('\n').append(
							value.type).append('\n').append(value.mappingField)
							.append('\n');
				} // if (value.mappingField != null)
			} // for iter
		} // if

		// this method always returns true because there are no constraints yet
		return mappingInfo.toString();
	} // extractExtMappingInfo

	/***************************************************************************
	 * Extracts the mapping info from the given data element and stores it in
	 * the object propertys {@link #p_mappingTableName p_mappingTableName} and
	 * {@link #p_mappingInfo p_mappingInfo}. <BR/> The data element is the data
	 * element generated from the template xml file. After this the informations
	 * can be stored in the database. Note that the mapping info does not store
	 * the type information which can lead to problems when only the type of a
	 * field has been changed and a new db mapping would become neccessary.
	 * <BR/>
	 * 
	 * @param dataElement
	 *            the data element representing the template file
	 * @param mapper
	 *            the dbmapper object
	 * 
	 * @return <CODE>true</CODE> if everything was o.k., <CODE>false</CODE>
	 *         otherwise.
	 */
	private boolean extractMappingInfo(DataElement dataElement, DBMapper mapper) {
		// get the table name and the name of the copy procedure
		this.p_mappingTableName = dataElement.tableName;
		this.p_mappingProcCopy = mapper.getProcCopyName(dataElement);
		this.p_mappingInfo = "";
		// the form field names and table field names are stored sequencely
		// in a string separated be a \n character.

		// loop trough the value elements and store the value field names
		// and table field names in a string.
		for (Iterator<ValueDataElement> iter = dataElement.values.iterator(); iter
				.hasNext();) {
			// get the next value:
			ValueDataElement value = iter.next();
			// check only the mapped values
			if (value.mappingField != null) {
				this.p_mappingInfo += value.field + '\n' + value.mappingField
						+ '\n';
			} // if (value.mappingField != null)
		} // for iter
		return true;
	} // extractMappingInfo

	/***************************************************************************
	 * Checks if all type codes in the given list are valid m2 type codes. <BR/>
	 * 
	 * @param codeList
	 *            comma separated list of type codes
	 * @param isMayContain
	 *            indicates that the list is the MAYCONTAIN list
	 * @param objTypeCode
	 *            the typecode of the object itself
	 * @param log
	 *            the log object to write the messages to
	 * 
	 * @return <CODE>true</CODE> if the type codes are valid,
	 *         <CODE>false</CODE> otherwise.
	 */
	private boolean checkTypeCodeList(String codeList, boolean isMayContain,
			String objTypeCode, Log_01 log) {
		boolean isOk = true;

		// an empty list is valid in any case:
		if (codeList == null) {
			return true;
		} // if

		try {
			// get the types from the codes:
			Vector<Type> types = this.getTypeCache().findTypes(codeList);

			// loop through the types and check for each of them if they are
			// allowed:
			for (Iterator<Type> iter = types.iterator(); iter.hasNext();) {
				Type type = iter.next();
				String typeCode = type.getCode();

				// check if the typecode is the same as the template
				// because this is a valid code. an object can contain
				// objects of his own type. we need to check this
				// because the type may not exist at this point
				if (typeCode.equals(objTypeCode)) {
					// a form type can appear only in a 'mayContain' list
					// but not in a 'mayExistIn' list
					if (!isMayContain) {
						// invalid type code in MAYCONTAIN attribute
						log.add(DIConstants.LOG_ERROR, objTypeCode + ": " +
	                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
	                            DIMessages.ML_MSG_INVALID_MAYEXISTIN_TYPECODE, env)
							+ " (" + typeCode + ")");
					} // if (!isMayContain)
				} // if (typeCode.equals (objTypeCode))
				else // typecode is not the template typecode
				{
					// get the version id for the type code
					// if the type list is the mayExistIn list
					// check if the type code belongs to m2 basic type and not
					// to a form type
					if (!isMayContain && type.getTemplate() != null) {
						// form types may not be used in the MAYEXISTIN
						// attribute
						log.add(DIConstants.LOG_ERROR, objTypeCode + ": " +
	                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
	                            DIMessages.ML_MSG_INVALID_MAYEXISTIN_TYPECODE, env)
							+ " (" + typeCode + ")");
						isOk = false;
					} // if (!isMayContain)
				} // else typecode is not the template typecode
			} // for iter
		} // try
		catch (TypeNotFoundException e) {
			// the type code is invalid
			if (isMayContain) {
				// invalid type code in MAYCONTAIN attribute
				log.add(DIConstants.LOG_ERROR, objTypeCode + ": " +
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_INVALID_MAYCONTAIN_TYPECODE, env) 
                    + " " + e.getMessage());
			} // if (isMayContain)
			else {
				// invalid type code in MAYCONTAIN attribute
				log.add(DIConstants.LOG_ERROR, objTypeCode + ": " +
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_INVALID_MAYEXISTIN_TYPECODE, env)
                    + " " + e.getMessage());
			} // else
			isOk = false;
		} // catch

		// return the result:
		return isOk;
	} // checkTypeCodeList

	/***************************************************************************
	 * Evaluate the function to be performed. <BR/>
	 * 
	 * @param function
	 *            The function to be performed.
	 * 
	 * @return Function to be performed after this method. <BR/>
	 *         {@link ibs.app.AppFunctions#FCT_NOFUNCTION FCT_NOFUNCTION} if
	 *         there is no function or the function was already performed.
	 */
	public int evalFunction(int function) {
		int resultFunction = AppFunctions.FCT_NOFUNCTION;
		// the resulting function
		switch (function) // perform function
		{
		case AppFunctions.FCT_SHOWGENERATETRANSLATORFORM:
			// get object oid and display the object:
			this.showGenerateTemplateForm();
			break;

		case AppFunctions.FCT_GENERATETRANSLATOR:
			// generate and download the translator:
			this.generateAndDownloadTranslator();
			break;

		default: // unknown function
			resultFunction = super.evalFunction(function);
			// evaluate function in super class
		} // switch function

		// return which function shall be performed after this method:
		return resultFunction;
	} // evalFunction

	/***************************************************************************
	 * Update the field settings of all xmlviewer objects of the given type if
	 * necessary. <BR/> Note that a change can be in objectref settings or in
	 * the mandatory setting. A change of type must be done via structural
	 * update. <BR/>
	 * 
	 * @param oldData
	 *            the data element of the old template
	 * @param newData
	 *            the data element of the new template
	 * 
	 * @deprecated KR 20090823 This method is never used. Last usage was in
	 *             performChangeOLD(int).
	 */
	@Deprecated
	// DEL KR 20090824 can be deleted as agreed with BB.
	public void updateObjects(DataElement oldData, DataElement newData) {
		Vector<ValueDataElement> changeFields = new Vector<ValueDataElement>();
		ValueDataElement changeField;
		ValueDataElement value;
		int objCounter = 0;
		int errorCounter = 0;
		Date startDate;

		// constraint: both data elements must be present
		if (oldData == null || newData == null) {
			return;
		} // if

		// first determine the fields that have an objectref:
		for (Iterator<ValueDataElement> iter = oldData.values.iterator(); iter
				.hasNext();) {
			ValueDataElement oldValue = iter.next();
			ValueDataElement newValue;

			// try to find the old value in the new structure:
			newValue = newData.getValueElement(oldValue.field);
			if (newValue != null) {
				// compare the objectref settings, if not equal add the field:
				if (!newValue.equals(oldValue)) {
					changeFields.addElement(newValue);
				} // if
			} // if
		} // for iter

		// check if any objectrefs were found:
		if (changeFields.size() > 0) {
			// updates necessary
			startDate = new Date();

			this.env.write("<DIV ALIGN=\"LEFT\">");
			this.env.write("<FONT SIZE=\"2\">");
			this.env
					.write("Update objects due to changes in the following fields:"
							+ IE302.TAG_NEWLINE);
			// display the objects that have been changed
			for (int i = 0; i < changeFields.size(); i++) {
				changeField = changeFields.elementAt(i);
				this.env.write("<LI/>" + changeField.field);
			} // for (int i = 0; i < changeFields.size (); i++)
			this.env.write(IE302.TAG_NEWLINE);

			this.env.write("Updating objects started at "
					+ DateTimeHelpers.dateTimeToString(startDate) + " ..."
					+ IE302.TAG_NEWLINE);

			// find the appropriate objects:
			Vector<BusinessObjectInfo> objects = BOHelpers.findObjects(
					newData.p_typeCode, new StringBuffer("o.isLink = ")
							.append(SQLConstants.BOOL_FALSE), this.env);

			// check if there are some objects:
			if (objects.size() > 0) {
				// loop through all objects:
				for (Iterator<BusinessObjectInfo> iter = objects.iterator(); iter
						.hasNext();) {
					BusinessObjectInfo objInfo = iter.next();
					objCounter++;
					this.env.write("<LI/>["
							+ objCounter
							+ "] "
							+ objInfo.p_typeName
							+ " '<A HREF=\""
							+ IOHelpers
									.getShowObjectJavaScriptUrl(objInfo.p_oid
											.toString()) + "\"><CODE>"
							+ objInfo.p_name + "</CODE>' (<CODE>"
							+ objInfo.p_oid.toString() + "</CODE>)</A> ... ");
					try {
						// create the object:
						XMLViewer_01 xmlviewer = (XMLViewer_01) objInfo
								.getObject(this.env);

						// now loop through the fields to get the objectref
						// fields
						for (int i = 0; i < changeFields.size(); i++) {
							changeField = changeFields.elementAt(i);
							// get the value
							value = xmlviewer.dataElement
									.getValueElement(changeField.field);
							if (value != null)
							{
								value.mandatory = changeField.mandatory;
								value.p_readonly = changeField.p_readonly;
								value.p_valueUnit = changeField.p_valueUnit;
								value.p_context = changeField.p_context;
								value.searchRecursive = changeField.searchRecursive;
								value.searchRoot = changeField.searchRoot;
								value.searchRootIdDomain = changeField.searchRootIdDomain;
								value.searchRootId = changeField.searchRootId;
								value.typeFilter = changeField.typeFilter;
								value.queryName = changeField.queryName;
								value.emptyOption = changeField.emptyOption;
								value.refresh = changeField.refresh;
								value.mlKey = changeField.mlKey;
							} // if (value != null)
							else // value not found
							{
								this.env.write("<B>ERROR</B>: field '"
										+ changeField.field + "' not found! ");
								errorCounter++;
							} // else value not found
						} // for (int i = 0; i < objectrefs.size (); i++)

						// change the object
						xmlviewer.performChange(Operations.OP_NONE);
						this.env.write("changed.");
					} // try
					catch (NameAlreadyGivenException e) {
						this.env.write("<B>ERROR</B>: '" + e.toString());
						errorCounter++;
					} // catch (NameAlreadyGivenException e)
					catch (NoAccessException e) {
						this.env.write("<B>ERROR</B>: '" + e.toString());
						errorCounter++;
					} // catch (NoAccessException e)
				} // for iter
			} // if
			else {
				this.env.write("<LI>no objects found!");
			} // else

			// show the time elapsed
			DIHelpers.showElapsedTime(this.env, "Finished updating objects",
					startDate, new Date(), objCounter);
			// any errors found?
			if (errorCounter > 0) {
				this.env.write(IE302.TAG_NEWLINE + "<B>WARNING</B>: "
						+ errorCounter + " error(s) found!");
			} // if
			else {
				this.env.write(IE302.TAG_NEWLINE + "No errors found.");
			} // else
			this.env.write("</FONT></DIV>" + IE302.TAG_NEWLINE);
		} // else updates neccessary
	} // updateObjects


    /***************************************************************************
     * Initializes the multilang template info for all provided locales. <BR/>
     *
     * @param   locales  The locales to init the multilang info for
     * @param   env      The environment
     */
    public void initMultilangInfo (Collection<Locale_01> locales, Environment env)
    {
        this.getTemplateDataElement ().initMultilangInfo (locales, env);
    } // initMultilangInfo
    
    
    /***************************************************************************
     * Initializes the multilang texts for the template's references for all
     * provided locales. <BR/>
     * 
     * This method has to be executed after initMultilangInfo ().
     *
     * @param   locales  The locales to init the multilang info for
     * @param   env      The environment
     */
    public void initMultilangReferenceInfo (Collection<Locale_01> locales, Environment env)
    {
        this.getTemplateDataElement ().initMultilangReferenceInfo (locales, env);
    } // initMultilangReferenceInfo

    
    /***************************************************************************
     * Returns a string with a SQL query to retrieve the data of the link fields. <BR/> 
     * 
     * @return Returns a SQL String to retrieve the data of link fields. <BR/>
     */
    public String getLinkFieldQuery()
    {
        StringBuilder queryStr = new StringBuilder();

        queryStr
            .append (" SELECT 1 AS \"linkType\", ibs_object.oid, ibs_object.state, ibs_object.name, ")
            .append ("        ibs_object.typeCode, ibs_object.typeName, ibs_object.isLink, ibs_object.linkedObjectId, ")
            .append ("        ibs_object.owner, ibs_object.ownerName, ibs_object.ownerOid, ")
            .append ("        ibs_object.ownerFullname, ibs_object.lastChanged, ibs_object.icon, ")
            .append ("        ibs_object.description, ibs_object.flags, ibs_object.processState, ");
        
        Iterator<ValueDataElement> linkFieldIter = this.p_linkFields.iterator ();
        while (linkFieldIter.hasNext ())
        {
            ValueDataElement oneLinkField = linkFieldIter.next ();
            queryStr.append(" typeTable.").append(oneLinkField.mappingField);
            // more fields to add to the statement?
            if (linkFieldIter.hasNext ())
            {
                queryStr.append (", ");    
            } // if
        } // while
        
        queryStr.append (" FROM   v_RefContainer_01$content AS ibs_object, ")
            .append(this.getTemplateDataElement ().tableName)
            .append (" AS typeTable")
            .append (" WHERE  ibs_object.linkedObjectId = typeTable.oid");
        
        return queryStr.toString ();
    } // getLinkFieldQuery

} // class DocumentTemplate_01

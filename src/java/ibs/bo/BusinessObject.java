/*
 * Class: BusinessObject.java
 */

// package:
package ibs.bo;

// imports:
import ibs.IbsObject;
import ibs.app.AppConstants;
import ibs.app.AppFunctions;
import ibs.app.CssConstants;
import ibs.app.FilenameElement;
import ibs.bo.path.ObjectPathHandler;
import ibs.bo.path.ObjectPathNode;
import ibs.bo.tab.Tab;
import ibs.bo.tab.TabConstants;
import ibs.bo.tab.TabContainer;
import ibs.bo.type.ITypeContainer;
import ibs.bo.type.Type;
import ibs.bo.type.TypeConstants;
import ibs.bo.type.TypeContainer;
import ibs.di.DIConstants;
import ibs.di.DataElement;
import ibs.di.RTExceptionInvalidLink;
import ibs.di.ValueDataElement;
import ibs.di.KeyMapper.ExternalKey;
import ibs.di.exp.ExportException;
import ibs.io.Environment;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilangConstants;
import ibs.ml.MultilingualTextInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.menu.MenuData_01;
import ibs.obj.query.QueryExecutive;
import ibs.obj.ref.RefContainerElement_01;
import ibs.obj.workflow.WorkflowTokens;
import ibs.service.conf.Configuration;
import ibs.service.notification.INotificationService;
import ibs.service.notification.NotificationFailedException;
import ibs.service.notification.NotificationServiceFactory;
import ibs.service.notification.NotificationTemplate;
import ibs.service.user.User;
import ibs.service.workflow.WorkflowConstants;
import ibs.service.workflow.WorkflowInstanceInformation;
import ibs.tech.html.BlankElement;
import ibs.tech.html.BuildException;
import ibs.tech.html.ButtonElement;
import ibs.tech.html.DivElement;
import ibs.tech.html.Element;
import ibs.tech.html.FormElement;
import ibs.tech.html.FrameElement;
import ibs.tech.html.FrameSetElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.NewLineElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.SelectElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.tech.http.HttpArguments;
import ibs.tech.sql.DBActionPool;
import ibs.tech.sql.DBConnector;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.tech.sql.UpdateStatement;
import ibs.util.AlreadyDeletedException;
import ibs.util.DateTimeHelpers;
import ibs.util.DependentObjectExistsException;
import ibs.util.FormFieldRestriction;
import ibs.util.Helpers;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.UtilExceptions;
import ibs.util.file.FileHelpers;
import ibs.util.list.ListException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;


/******************************************************************************
 * This is a base class for business objects. <BR/>
 *
 * @version     $Id: BusinessObject.java,v 1.259 2013/01/16 16:14:15 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 980330
 ******************************************************************************
 */
public class BusinessObject extends IbsObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BusinessObject.java,v 1.259 2013/01/16 16:14:15 btatzmann Exp $";


    /**
     * Information about BusinessObjects active workflow-instance. <BR/>
     * (if one exists)
     */
    protected WorkflowInstanceInformation workflowInfo = null;

    /**
     * Server's locale. <BR/>
     */
    protected Locale l = Locale.GERMANY;


    /**
     * Stored procedure to create an object of this type. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procCreate = "p_Object$create";

    /**
     * Stored procedure to change an object of this type. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procChange = "p_Object$change";

    /**
     * Stored procedure to move an object of this type. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procMove = "p_Object$move";

    /**
     * Stored procedure to changeState an object of this type. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procChangeState = "p_Object$changeState";

    /**
     * Stored procedure to changeProcessState an object of this type. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procChangeProcessState = "p_Object$changeProcessState";

    /**
     * Stored procedure to changeOwner of an object of this type. <BR/>
     * Owner of subsequent objects will be changed as well.
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procChangeOwner = "p_Object$changeOwnerRec";

    /**
     * Stored procedure to retrieve an object of this type. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procRetrieve = "p_Object$retrieve";

    /**
     * Stored procedure to check out an object. <BR/>
     * The value of this property should not be overwritten within the
     * constructor of each subtype.
     */
    protected String procCheckOut = "p_Object$checkOut";

    /**
     * Stored procedure to insert an entry into the protocol table. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procInsertProtocol = "p_Object$InsertProtocol";

    /**
     * Stored procedure to retrieve an object of this type. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procCheckIn = "p_Object$checkIn";

    /**
     * Stored procedure to delete an object of this type. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procDelete = "p_Object$delete";

    /**
     * Stored procedure to delete an object of this type. <BR/>
     * This procedure is used to perform a recursive deletion of the object,
     * i.e. a deletion of the object itself and all subsequent objects.
     * This procedure is used if {@link #deleteRecursive deleteRecursive}
     * is set to <CODE>true</CODE>. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procDeleteRec = this.procDelete;

    /**
     * Stored procedure to delete an object of this type. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procUnDelete = "p_Object$undelete";

    /**
     * Stored procedure to delete an object of this type. <BR/>
     * This procedure is used to perform a recursive deletion of the object,
     * i.e. a deletion of the object itself and all subsequent objects.
     * This procedure is used if {@link #deleteRecursive deleteRecursive}
     * is set to <CODE>true</CODE>. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procUnDeleteRec = this.procUnDelete;

    /**
     * Shall the deletion be performed in a recursive manner. <BR/>
     * If this value is set to <CODE>true</CODE> the procedure
     * {@link #procDeleteRec procDeleteRec} is performed, otherwise
     * {@link #procDelete procDelete} will be called. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    public boolean deleteRecursive = true;

    /**
     * Stored procedure to delete all references to this object. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procDeleteAllRefs = "p_Object$deleteAllRefs";


    /**
     * Stored procedure to copy an object and its referenced objects of this
     * type. <BR/>
     * The value of this property can be overwritten within each subtype.
     */
    protected String procCopy = "p_Object$copy";
    
    /**
     * Stored procedure to get the OID of an original object when an object tree is copied. <BR/>
     * The value of this property can be overwritten within each subtype.
     */
    protected String funcRetrOriginalOid = "dbo.f_retrieveOriginalOidFromCopy";

    /**
     * Stored procedure to get the upper object of the actual object. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procGetUpper = "p_Object$getUpperOid";

    /**
     * Get a tab of the actual object. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procGetTab = "p_Object$getTabInfo";

    /**
     * Get a tab of the actual object. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procGetMaster = "p_Object$getMasterOid";

    /**
     * Get a rights of the object and its container. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procGetRightsContainer = "p_Rights$getRightsContainer";

    /**
     * Get a rights of the object. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procGetRights = "p_Rights$getRights";

    /**
     * Check the rights of an user on an object. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procObjRights = "p_Rights$checkObjectRights";

    /**
     * Check the rights of an user on an object. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procCheckRights = "p_Rights$checkRights1";

    /**
     * Check the rights of an user on an object and its sub objects. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procCheckRightsRecursive = "p_Rights$checkUserRightsRec";

    /**
     * View the content of the container. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype. <BR/>
     * <FONT SIZE=+1><B>ATTENTION: THIS PROPERTY IS NECESSARY BECAUSE OF THE CLEAN FUNCTION
     * WHICH SHALL BE MOVED TO A CLEAN CONTAINER!</B></FONT>.
     */
    protected String viewContent = "v_Container$content";

    /**
     * View containing all object data necessary to build a selection list. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String viewSelectionList = "v_Container$rightsSelList";

    /**
     * Confirmation message which is displayed when trying to delete an object
     * of this type. <BR/>
     */
    protected String msgDeleteConfirm = BOMessages.ML_MSG_OBJECTDELETECONFIRM;

    /**
     * Confirmation message which is displayed when trying to delete an object
     * of this type which have some references defined. <BR/>
     */
    protected String msgDeleteConfirmRef = BOMessages.ML_MSG_OBJECTDELETECONFIRMREF;

    /**
     * Shall the buttons be displayed?. <BR/>
     */
    public boolean displayButtons = true;

    /**
     * Don't display the state within the buttons. <BR/>
     */
    public boolean disableButtonsState = false;

    /**
     * Is the object a container for all underlying objects, regardless of
     * their real containers?. <BR/>
     */
    public boolean isMajorContainer = false;

    /**
     * Remember if the object is actual. <BR/>
     * The value of this property is set to true if the object was already read
     * from the database.
     */
    protected boolean isActual = false;

    /**
     * State of the business object. <BR/>
     * The value of this property is one of the ST_* values defined in
     * {@link ibs.util.UtilConstants UtilConstants}.
     */
    public int state = States.ST_UNKNOWN;

    /**
     * The type of the object. <BR/>
     * This value also contains the type version and is equal to oid.tVersionId.
     */
    public int type = 0x00000000;

    /**
     * Name of the type where the object belongs to. <BR/>
     */
    public String typeName = null;

    /**
     * The type object for the object. <BR/>
     */
    public Type typeObj = null;

    /**
     * Is the object a container or not?. <BR/>
     */
    public boolean isContainer = false;

    /**
     * Name of the business object. <BR/>
     */
    public String name = null;

    /**
     * Id of the container where the object belongs to. <BR/>
     */
    public OID containerId = null;

    /**
     * Name of the container where the object belongs to. <BR/>
     */
    public String containerName = null;

    /**
     * Kind of relationship between object and container. <BR/>
     * <UL>
     * <LI>{@link BOConstants#CONT_STANDARD BOConstants.CONT_STANDARD} ...
     *      standard object/container relationship.
     * <LI>{@link BOConstants#CONT_PARTOF BOConstants.CONT_PARTOF} ...
     *      partOf relationship, i.e. the object is a part of the container
     *      and thus existentially dependent of the container.
     * </UL>
     */
    private int containerKind = BOConstants.CONT_STANDARD;

    /**
     * Defines if this object is a link. <BR/>
     * Contains true, if the object is a link, false otherwise.
     */
    public boolean isLink = false;

    /**
     * The oid of the object which this object is a link to. <BR/>
     * This property is null, if {@link #isLink isLink} is false,
     * otherwise it contains the oid of the object where the link shows to.
     */
    public OID linkedObjectId = null;

    /**
     * User who is the owner of this object. <BR/>
     */
    public User owner = null;

    /**
     * Date when the object was created. <BR/>
     */
    public Date creationDate = null;

    /**
     * User who has created the object. <BR/>
     */
    public User creator = null;

    /**
     * Date of last change. <BR/>
     */
    public Date lastChanged = null;

    /**
     * User who has last changed the object. <BR/>
     */
    public User changer = null;

    /**
     * Date until the object is valid. <BR/>
     * After ths time it can be deleted by the administrator.
     */
    public Date validUntil = null;

    /**
     * Description of the object. <BR/>
     */
    public String description = null;

    /**
     * Should the object be displayed in the NewsContainer. <BR/>
     */
    public boolean showInNews = false;

    /**
     * Is the object deletable?. <BR/>
     */
    public boolean p_isDeletable = true;

    /**
     * Is the object changeable?. <BR/>
     */
    public boolean p_isChangeable = true;

    /**
     * Flag to indicate that a file is attached to this object. <BR/>
     * Default: <CODE>false</CODE>
     */
    private boolean p_hasFileFlag = false;

    
    /**
     * Flag to indicate if the common script shall be generated. <BR/>
     * Default: <CODE>true</CODE>
     */
    public boolean p_isShowCommonScript = true;

    /**
     * Is the check out exclusive?. <BR/>
     * If the object supports check out this attribute defines whether other
     * functions (edit, delete, ...) shall be available if the object is not
     * checked out. If the value is <CODE>true</CODE> no other functions are
     * allowed. <BR/>
     * default: <CODE>true</CODE>
     */
    public boolean p_isCheckOutExclusive = true;

    /**
     * Is the Object checked out (frozen to edit except for checkoutUser)?. <BR/>
     */
    public boolean checkedOut = false;

    /**
     * Checkouttype of the object. <BR/>
     */
    public int checkOutType = 0;

    /**
     * Date when the object was checked out. <BR/>
     */
    public Date checkOutDate = null;

    /**
     * The id of the user who checked out the object. <BR/>
     */
    public User checkOutUser = null;

    /**
     * The oid of the user who checked out the object. <BR/>
     * Is this property == null and the object was checked out,
     * then the User has no right to read the checkOutUser!!!
     */
    public OID checkOutUserOid = null;

    /**
     * The name of the user who checked out the object. <BR/>
     * Is this property == null and the object was checked out,
     * then the User has no right to view the checkOutUser!!!
     */
    public String checkOutUserName = null;

    /**
     * true when objecttype has extended search attributes. <BR/>
     */
    public boolean searchExtended = false;

    /**
     * name of the corresponding table in the database. <BR/>
     */
    public String tableName = "";

    /**
     * The last access permissions which where checked for the actual object. <BR/>
     */
    private AccessPermissions p_lastAccessPermissions = new AccessPermissions ();

    /**
     * URL of icon. <BR/>
     */
    public String icon = null;

    /**
     * Current state of any process performed with this object. <BR/>
     */
    public int processState = States.PST_NONE;

    /**
     * State before changing this object. <BR/>
     */
    protected int oldState = this.state;

    /**
     * When displaying the actual view of the object within a frameset:. <BR/>
     * Shall the frames be displayed as rows (=> true) or as columns
     * (=> false). <BR/>
     */
    protected boolean framesAsRows = true;

    /**
     * When displaying the actual view of the object within a frameset:. <BR/>
     * Function to be performed for loading first frame. <BR/>
     * Default: {@link ibs.app.AppFunctions#FCT_NOFUNCTION FCT_NOFUNCTION}.
     */
    protected int frm1Function = AppFunctions.FCT_NOFUNCTION;

    /**
     * When displaying the actual view of the object within a frameset:. <BR/>
     * Function to be performed for loading second frame. <BR/>
     * Default: {@link ibs.app.AppFunctions#FCT_NOFUNCTION FCT_NOFUNCTION}.
     */
    protected int frm2Function = AppFunctions.FCT_NOFUNCTION;

    /**
     * When displaying the actual view of the object within a frameset:. <BR/>
     * Size of first frame. <BR/>
     * Default: "2*".
     */
    protected String frm1Size = "2*";

    /**
     * When displaying the  actual view of the object within a frameset:. <BR/>
     * Size of second frame. <BR/>
     * Default: "*".
     */
    protected String frm2Size = "*";

    /**
     * When displaying the  actual view of the object within a frameset:. <BR/>
     * URL of first frame. <BR/>
     * Default: {@link AppConstants#FILE_EMPTYPAGE FILE_EMPTYPAGE}.
     */
    protected String frm1Url = AppConstants.FILE_EMPTYPAGE;

    /**
     * When displaying the  actual view of the object within a frameset:. <BR/>
     * URL of second frame. <BR/>
     * Default: {@link ibs.app.AppConstants#FILE_EMPTYPAGE
     *          AppConstants.FILE_EMPTYPAGE}.
     */
    protected String frm2Url = AppConstants.FILE_EMPTYPAGE;

    /**
     * Output frame used for object itself. <BR/>
     */
    protected String frmSheet = HtmlConstants.FRM_SHEET;

    /**
     * Tells whether it is allowed to display the object. <BR/>
     * This property is consulted within change.
     */
    public boolean showAllowed = true;

    /**
     * Tells whether it is possible to show the actual view as frameset. <BR/>
     * This property is used to control whether the frameset of an object view
     * or the view itself is displayed. If the value of this property is
     * <CODE>true</CODE> and the view shall be displayed as frameset then the
     * frameset is displayed. After this this property is set to
     * <CODE>false</CODE> to ensure that the next call does not display the
     * frameset but the view itself.
     */
    protected boolean framesetPossible = true;

    /**
     * Tells whether the info view of the object shall be shown as frameset.
     * <BR/>
     */
    protected boolean showInfoAsFrameset = false;

    /**
     * Tells whether the change form view of the object shall be shown as
     * frameset. <BR/>
     */
    protected boolean showChangeFormAsFrameset = false;

    /**
     * Tells whether the new form view of the object shall be shown as frameset.
     * <BR/>
     */
    protected boolean showNewFormAsFrameset = false;

    /**
     * Tells whether the distribute form view of the object shall be shown as
     * frameset. <BR/>
     */
    protected boolean showDistributeFormAsFrameset = false;

    /**
     * Contains the tracing texts. <BR/>
     */
    protected StringBuffer bOtrace = new StringBuffer ();

    /**
     * If true, a short menu is visible, when a new busines object has been
     * created. If False, the "traditional" behavior remains unchanged.
     */
    public boolean showExtendedCreationMenu = true;

    /**
     * Shall the tabs be displayed?. <BR/>
     */
    public boolean displayTabs = false;

    /**
     * The tabs of the current object. <BR/>
     * This value is used for building the tab bar.
     */
    public TabContainer p_tabs = null;

    /**
     * The tabs of the container of the current object. <BR/>
     * This value is used for building the tab bar.
     */
    public TabContainer p_containerTabs = null;

    /**
     * Indicates that Class BusinessObject is used to show view on an other
     * Object BusinessObject. <BR/>
     */
    public boolean p_isTabView = false;

    /**
     * Id of ViewTab if Class BusinessObject is used to show view on an
     * other Object BusinessObject. <BR/>
     */
    public int p_tabId = -1;


    /**
     * Cache containing accesses of users to this object. <BR/>
     * Default: 30 elements.
     */
    private AccessCache accessCache = new AccessCache (30);

    /**
     * Indicates if the BusinessObject corresponds to an object within the
     * data store. Used to determine whether attributes have to be
     * retrieved from the data store or not. <BR/>
     */
    public boolean isPhysical = true;

    /**
     * ???
     */
    protected Vector<Object> refs = null;


    /**
     * Number of type specific parameters for performCreateData. <BR/>
     */
    protected int specificCreateParameters = 0;

    /**
     * Number of type specific parameters for performChangeData. <BR/>
     */
    protected int specificChangeParameters = 0;

    /**
     * Number of type specific parameters for performRetrieveData. <BR/>
     */
    protected int specificRetrieveParameters = 0;

    /**
     * Number of type specific parameters for performDeleteData. <BR/>
     */
    protected int specificDeleteParameters = 3;

    /**
     * The name of the class of one element within the list. <BR/>
     * This name is used to derive elementClass and create instances of it. <BR/>
     * Default: "ibs.bo.ContainerElement".
     */
    protected String elementClassName = "ibs.bo.ContainerElement";

    /**
     * The elements contained in a list. <BR/>
     * This property is used during getting the path information and for
     * getting the elements of a container.
     */
    public Vector<ContainerElement> elements = new Vector<ContainerElement> (10, 10);

    /**
     * Maximum number of elements within a list of elements. <BR/>
     * This property is used during getting the path information and to
     * limit the size of the container's element list. <BR/>
     * If there are more objects within the list than allowed through this
     * property the list is limited to the number of elements allowed through
     * this property and a corresponding message is represented to the user.
     * <BR/>
     * A value of <CODE>0</CODE> means to display all elements within the list.
     * <BR/>
     * Default: <CODE>0</CODE>. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype. <BR/>
     */
    protected int maxElements = 0;

    /**
     * Number of elements within a list of elements. <BR/>
     * This property is used during getting the path information.
     */
    protected int size = 0;

    /**
     * The filename and path of a translator to be used
     * for a type transformation. <BR/>
     */
    public String p_typeTranslator = null;

    /**
     * The target typecode of a virtual object after the transformation. <BR/>
     * Note that this property must be set in virtual objects in order
     * to ensure that the object will be pasted in containers that can contain
     * this objecttype. The objecttype of a virtual changes when converting
     * to a physical objecttype. <BR/>
     */
    public String p_targetPhysicalTypeCode = null;

    /**
     * ID of a virtual object. <BR/>
     * Note that this ID will represent an index in a vector. <BR/>
     */
    public String p_virtualId = null;

    /**
     * object tree node of this businessObject with all parents. <BR/>
     */
    private ObjectPathNode p_objectPath = null;

    /**
     * Shall the name field be editable in new form?. <BR/>
     */
    public boolean p_isEditNameInNewForm = false;

    /**
     * Shall the description field be editable in new form?. <BR/>
     */
    public boolean p_isEditDescInNewForm = false;


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a BusinessObject object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     * After that the method {@link #initClassSpecifics () initClassSpecifics}
     * is called.
     */
    public BusinessObject ()
    {
        // call constructor of super class:
        super ();

        this.initClassSpecifics ();
/*
        // initialize the instance's properties:
        initClassSpecifics ();
        initObject (OID.getEmptyOid (), null, null, null, null);
// catch
*/
    } // BusinessObject


    /**************************************************************************
     * Creates a BusinessObject object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     * After that the method {@link #initClassSpecifics () initClassSpecifics}
     * is called.
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    @Deprecated
    public BusinessObject (OID oid, User user)
    {
        // call constructor of super class:
        super ();

        // initialize the instance's properties:
        this.initClassSpecifics ();
        this.initObject (oid, user, null, null, null);
    } // BusinessObject


    /**************************************************************************
     * Initializes a BusinessObject object. <BR/>
     * The compound object id is stored in the {@link #oid oid} property
     * of this object. <BR/>
     * {@link #tableName tableName} is initialized to
     * <CODE>"ibs_Object"</CODE>. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.tableName = "ibs_Object";
    } // initClassSpecifics


    /**************************************************************************
     * Initializes a BusinessObject object. <BR/>
     * The compound object id is stored in the {@link #oid oid} property
     * of this object. <BR/>
     * The {@link #user user object} is also stored in a specific
     * property of this object to make sure that the user's context can be used
     * for getting his/her rights. <BR/>
     * {@link #env env} is initialized to the provided object. <BR/>
     * {@link #sess sess} is initialized to the provided object. <BR/>
     * {@link #app app} is initialized to the provided object. <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     * @param   env     The actual environment object.
     * @param   sess    The actual session info object.
     * @param   app     The global application info object.
     */
    public void initObject (OID oid, User user, Environment env,
                            SessionInfo sess, ApplicationInfo app)
    {
        // initialize the instance's private properties:

        // set the instance's public/protected properties:
        this.setEnv (env);
        this.setOid (oid);              // set the object id
        this.app = app;
        this.sess = sess;
        this.user = user;               // set the user
        this.type = oid.tVersionId;

        // ensure that there is a tracer available:
        this.openTrace ();
    } // initObject


    /**************************************************************************
     * Initialize the current business object. <BR/>
     *
     * @param   type    The type object for the object's type.
     * @param   oid     The oid of the object.
     * @param   user    User for which the object is to be gotten.
     * @param   env     Environment.
     * @param   sess    Session object.
     * @param   app     The application info object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   AlreadyDeletedException
     *              The object was deleted and cannot be accessed any more.
     * @exception   ObjectNotFoundException
     *              The required object was not found during search, i.e. there
     *              does not exist any object with the required oid.
     */
    public void initialize (Type type, OID oid, User user, Environment env,
                            SessionInfo sess, ApplicationInfo app)
        throws NoAccessException, AlreadyDeletedException,
        ObjectNotFoundException
    {
//trace (this.getClass ().getName () + ".initialize start");
        // init the object's properties:
        this.typeObj = type;
        this.type = type.getTVersionId ();
        this.typeName = type.getName ();
        // it is required that initClassSpecifics is called here
        // calling it in the constructor means that initializations defined
        // in property declarations are done after calling this method.
        this.initClassSpecifics ();
        this.initObject (oid, user, env, sess, app);

        if (this.isPhysical ||
            this.oid.type == this.getTypeCache ().getTypeId (TypeConstants.TC_Workspace))
                                        // object has own id or is workspace?
                                        // (workspace gets its data through the
                                        // user not the oid)
        {
            // get the object's data out of the data store:
            // (must be done before reading parameters because the
            // parameters may overwrite object properties)
            try
            {
                // operation for rights check:
                int operation = Operations.OP_READ;

                // check if there is a user defined:
                if (user == null) // no user defined?
                {
                    // don't make a rights check:
                    operation = Operations.OP_NONE;
                } // if no user defined

                // get the object's data:
                this.retrieve (operation);
            } // try
            catch (NoAccessException e)
            {
                // propagate the exception to the caller:
                throw e;
            } // catch
            catch (AlreadyDeletedException e)
                // no access to objects allowed
            {
/*
                // send message to the user:
                this.showAlreadyDeletedMessage ();
*/

                // propagate the exception to the caller:
                throw e;
            } // catch
//trace ("oid = " + obj.oid);
        } // if object has own id or is workspace
//trace (this.getClass ().getName () + ".initialize end");
    } // initialize


    ///////////////////////////////////////////////////////////////////////////
    // functions called from application level
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Sets the oid of this object. <BR/>
     * The compound object id is stored in the {@link #oid oid} property
     * of this object. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public void setOid (OID oid)
    {
        // set the new oid:
        this.oid = oid;
        this.isPhysical = !this.oid.isTemp ();
    } // setOid


    /**************************************************************************
     * Sets the oid of this object. <BR/>
     * This method uses the String representation of the oid to get an OID
     * object and store this object as oid. If the String does not represent a
     * valid oid the oid is set to the result of
     * {@link ibs.bo.OID#getEmptyOid () OID.getEmptyOid ()}. If the String is
     * <CODE>null</CODE> the oid is set to <CODE>null</CODE>. <BR/>
     * The compound object id is stored in the {@link #oid oid} property
     * of this object. <BR/>
     *
     * @param   oidStr  Value for the compound object id.
     */
    public void setOid (String oidStr)
    {
        OID actOid = null;                 // the oid itself

        // check if the string was set and exists:
        if (oidStr != null)             // string exists?
        {
            try
            {
                // try to create an oid from the string:
                actOid = new OID (oidStr);
            } // try
            catch (IncorrectOidException e)
            {
                // set the standard oid:
                actOid = OID.getEmptyOid ();
            } // catch
        } // if value exists

        // set the new oid:
        this.oid = actOid;
        this.isPhysical = !this.oid.isTemp ();
    } // setOid


    /**************************************************************************
     * Returns if the Object is a file-type. <BR/>
     * Normally the return value is <CODE>false</CODE>.
     *
     * @return  <CODE>true</CODE> if the object contains a file field,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean hasFile ()
    {
        return false;
    } // hasFile


    /**************************************************************************
     * Check if the object is a tab. <BR/>
     *
     * @return  <CODE>true</CODE> if the object is a tab,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isTab ()
    {
        // check if the object is a tab and return the result:
        return this.containerKind == BOConstants.CONT_PARTOF;
    } // isTab

    /**************************************************************************
     * Returns if the object is a query. <BR/>
     *
     * @return  <CODE>true</CODE> if the object is a query,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isQuery ()
    {
        return false;
    } // isQuery


    /**************************************************************************
     * Set if the object is a tab. <BR/>
     *
     * @param   isTab   <CODE>true</CODE> if the object is a tab,
     *                  <CODE>false</CODE> otherwise.
     */
    public void setIsTab (boolean isTab)
    {
        if (isTab)                      // the object is a tab?
        {
            // set the value:
            this.containerKind = BOConstants.CONT_PARTOF;
        } // if the object is a tab
        else                            // the object is no tab
        {
            // set the value:
            this.containerKind = BOConstants.CONT_STANDARD;
        } // else the object is no tab
    } // setIsTab


    /**************************************************************************
     * Show a frameset view of the object's content. <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   function1           The function for the first frame.
     */
    public void showFrameset (int representationForm, int function1)
    {
        if (true)                       // business object resists on this
                                        // server?
        {
            // show the frameset:
            this.performShowFrameset (representationForm, function1);

            // frameset view is not possible for next call:
            this.framesetPossible = false;
        } // if business object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server
    } // showFrameset


    /**************************************************************************
     * Show the object, i.e. its content. <BR/>
     * For the common BusinessObject this method calls
     * {@link #showInfo showInfo}.
     *
     * @param   representationForm  Kind of representation.
     */
    public void show (int representationForm)
    {
        // show the properties of the object:
        this.showInfo (representationForm);
    } // show


    /**************************************************************************
     * Show the object, i.e. its properties. <BR/>
     * The properties are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showInfo (int representationForm)
    {
        if (true)                       // business object resists on this
                                        // server?
        {

            if (this.showInfoAsFrameset && this.framesetPossible)
                                        // show as frameset?
            {
//trace ( "showInfo: frm1Function = " + this.frm1Function + " frm2Function = " + this.frm2Function);
//trace ( "frm2Url = " + this.frm2Url + "\n");
                // create a frameset to show the actual view within:
                this.showFrameset (representationForm, this.sess.p_actFct);
            } // if show as frameset
            else                        // don't show as frameset
            {
                try
                {
/* KR 011205: not necessary because already done before
                    // try to retrieve the object:
                    retrieve (Operations.OP_READ);
*/
                    if (this.getUserInfo ().userProfile.showRef)
                    {
                        this.performRetrieveRefs (Operations.OP_VIEW);
                    } // if

                    // show the object's data:
                    this.performShowInfo (representationForm);
                } // try
                catch (NoAccessException e) // no access to objects allowed
                {
                    // send message to the user:
                    this.showNoAccessMessage (Operations.OP_READ);
                } // catch
/* KR 011205: not necessary because already done before
                catch (AlreadyDeletedException e) // no access to objects allowed
                {
                    // send message to the user:
                    showAlreadyDeletedMessage ();
                } // catch
*/
                this.framesetPossible = true; // frameset view is possible again
            } // else don't show as frameset
        } // if container object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server
    } // showInfo


    /**************************************************************************
     * Show the object, i.e. its properties within a form. <BR/>
     * The properties are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   newFormFct          The function to be performed when submitting
     *                              the form.
     *
     * @return  0 if the form was shown properly. <BR/>
     *          < 0 if there was an error. <BR/>
     *          > 0 id of type if there is just one type possible within the
     *              actual container.
     */
    public int showNewForm (int representationForm, int newFormFct)
    {
        int retVal = 0;                 // return value of method
        String[] typeIds = this.getTypeIds ();

        if (typeIds == null || typeIds.length == 0) // no object type allowed?
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_NOOBJECTTYPEALLOWED, this.env),
                this.app, this.sess, this.env);
            // set error as return value:
            retVal = -1;
        } // if no object type allowed
        else if (typeIds.length == 1)   // exactly one object type allowed?
        {
                        // set type id as return value:
            retVal = Integer.parseInt (typeIds[0]);
        } // if exactly one object type allowed
        else                            // multiple object types allowed
        {
            if (this.showNewFormAsFrameset && this.framesetPossible)
                                        // show as frameset?
            {
                // create a frameset to show the actual view within:
                this.showFrameset (representationForm, this.sess.p_actFct);
            } // if show as frameset
            else                        // don't show as frameset
            {
                // show the form to create a new object:
                this.performShowNewForm (representationForm, newFormFct);

                this.framesetPossible = true; // frameset view is possible again
            } // else don't show as frameset
        } // else multiple object types allowed

        return retVal;                  // return state value
    } // showNewForm


    /**************************************************************************
     * Create the object, i.e. store its properties within the database. <BR/>
     * The properties are gotten from the environment. <BR/>
     *
     * @param   representationForm  Kind of representation.
     *
     * @return  Oid of the newly created object. <BR/>
     *          Null if the object could not be created.
     */
    public OID createActive (int representationForm)
    {
        OID newOid = null;              // oid of newly created object

        // store the object's data within the database:
        try
        {
            // try to store the object to the database:
            newOid = this.performCreateData (Operations.OP_NEW | Operations.OP_ADDELEM);
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            // send message to the user:
            this.showNoAccessMessage (Operations.OP_NEW | Operations.OP_ADDELEM);
            newOid = null;
        } // catch
        catch (NameAlreadyGivenException e) // Name of the user already given
        {
            // send message to the user:
            this.showNameAlreadyGivenMessage ();
            newOid = null;
        } // catch

        // show the object to the user:
//        showChangeForm (representationForm);

        return newOid;                  // return the oid of the newly created
                                        // object
    } // createActive


    /**************************************************************************
     * Create the object, i.e. store its properties within the database. <BR/>
     * The properties are gotten from the environment. <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   op                  operation for rights check
     *
     * @return  Oid of the newly created object. <BR/>
     *          Null if the object could not be created.
     */
    public OID createActive (int representationForm,
                             int op)
    {
        OID newOid = null;              // oid of newly created object

        // store the object's data within the database:
        try
        {
            // try to store the object to the database:
            newOid = this.performCreateData (op);
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            // send message to the user:
            this.showNoAccessMessage (op);
            newOid = null;
        } // catch
        catch (NameAlreadyGivenException e) // Name of the user already given
        {
            // send message to the user:
            this.showNameAlreadyGivenMessage ();
            newOid = null;
        } // catch

        // show the object to the user:
//        showChangeForm (representationForm);

        return newOid;                  // return the oid of the newly created
                                        // object
    } // createActive


    /**************************************************************************
     * Add the reference to another object to this object. <BR/>
     *
     * @param   referencedOid   The oid of the object to create a reference on.
     *
     * @return  Oid of the newly created object. <BR/>
     *          <CODE>null</CODE> if the object could not be created.
     */
    public OID addReference (OID referencedOid)
    {
        OID actOid = null;              // oid of the new business object
        BusinessObject obj = null;      // the object representing the reference

        // check if the right type of Object would be filled in the targetcontainer
        if (this.isAllowedType (referencedOid.tVersionId))
                                        // type is allowed to insert?
        {
            // create the reference object:
            if ((obj = BOHelpers.createReferenceObject (referencedOid,
                this.oid, this.app, this.sess, this.getUser (), this.env)) !=
                null)
                                        // reference object created?
            {
                // set the oid:
                actOid = obj.oid;
            } // if reference object created
            else                        // reference object not created
            {
                // show corresponding message:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTNOTCREATED, this.env),
                    this.app, this.sess, this.env);
            } // else reference object not created
        } // if type is allowed to insert
        else                            // not the correct type to insert
        {
            // show corresponding message:
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTINSERTFAIL, this.env),
                this.app, this.sess, this.env);
        } // else not the correct type to insert

        // return the result:
        return actOid;
    } // addReference


    /**************************************************************************
     * Create the object, i.e. store its properties within the database. <BR/>
     * The properties are gotten from the environment. <BR/>
     *
     * @param   opCreate        Necessary permissions for creating the object.
     * @param   opRead          Necessary permissions for reading the object's
                                data.
     * @param   opChangeState   Necessary permissions for changing the object's
                                state.
     *
     * @return  Oid of the newly created object. <BR/>
     *          <CODE>null</CODE> if the object could not be created.
     */
    public OID create (int opCreate, int opRead, int opChangeState)
    {
        OID newOid = null;              // oid of newly created object

        // store the object's data within the database:
        try
        {
            // try to store the object to the database:
            newOid = this.performCreateData (opCreate);

            // check if we got a valid oid:
            if (newOid != null)
            {
                this.state = States.ST_CREATED;
                this.setOid (newOid);
                this.performChangeState (opChangeState);

/* KR 011217: not necessary if there are no changes done in the stored
              procedure p_Object$create
   BB 050208: TODO bad solution because it produces unnecessary overhead!
*/
                // retrieve the data of this object:
                this.retrieve (opRead);
/* ... KR 011217: not necessary if there are no changes done in the stored
              procedure p_Object$create
*/
            } // if
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            // send message to the user:
            this.showNoAccessMessage (opCreate);
            newOid = null;
        } // catch
        catch (NameAlreadyGivenException e) // Name of the user already given
        {
            // send message to the user:
            this.showNameAlreadyGivenMessage ();
            newOid = null;
        } // catch
        catch (AlreadyDeletedException e) // no access to objects allowed
        {
            // send message to the user:
            this.showAlreadyDeletedMessage ();
            newOid = null;
        } // catch
        catch (ObjectNotFoundException e)
        {
            // send message to the user:
            this.showObjectNotFoundMessage ();
            newOid = null;
        } // catch

        // show the object to the user:
//        showChangeForm (representationForm);

        return newOid;                  // return the oid of the newly created
                                        // object
    } // create


    /**************************************************************************
     * Create the object, i.e. store its properties within the database. <BR/>
     * The properties are gotten from the environment. <BR/>
     *
     * @param   representationForm  Kind of representation.
     *
     * @return  Oid of the newly created object. <BR/>
     *          <CODE>null</CODE> if the object could not be created.
     */
    public OID create (int representationForm)
    {
        // call common method and return the result:
        return this.create (Operations.OP_NEW | Operations.OP_ADDELEM,
                            Operations.OP_READ,
                            Operations.OP_EDIT | Operations.OP_CHANGESTATE);
    } // create


    /**************************************************************************
     * Force the creation of the object. <BR/>
     * This method stores the properties of the object without checking the
     * permissions. <BR/>
     * The properties are gotten from the environment. <BR/>
     *
     * @return  Oid of the newly created object. <BR/>
     *          <CODE>null</CODE> if the object could not be created.
     */
    public OID forceCreate ()
    {
        // call common method and return the result:
        return this.create (Operations.OP_NONE,
                            Operations.OP_NONE,
                            Operations.OP_NONE);
    } // forceCreate


    /**************************************************************************
     * Show the object, i.e. its properties within a form. <BR/>
     * The properties are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     *
     * @return  <CODE>true</CODE> if the change form or its frame set was
     *          displayed, <CODE>false</CODE> otherwise.
     */
    public boolean showChangeForm (int representationForm)
    {
        return this.showChangeForm (representationForm, this.sess.p_changeFormFct);
    } // showChangeForm


    /**************************************************************************
     * Show the object, i.e. its properties within a form. <BR/>
     * The properties are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   changeFormFct       The function to be performed when submitting
     *                              the form.
     *
     * @return  <CODE>true</CODE> if the change form or its frameset was
     *          displayed, <CODE>false</CODE> otherwise.
     */
    public boolean showChangeForm (int representationForm, int changeFormFct)
    {
        boolean performOK = true;       // return value of performShowChangeForm

        if (true)                       // business object resists on this
                                        // server?
        {
            if (this.showChangeFormAsFrameset && this.framesetPossible)
                                        // show as frameset?
            {
                // create a frameset to show the actual view within:
                // BB: I think "changeFormFct" should be used instead
                // this.sess.p_actFct
                this.showFrameset (representationForm, this.sess.p_actFct);
            } // if show as frameset
            else                        // don't show as frameset
            {
                if (this.state == States.ST_CREATED)
                                        // object was just created?
                {
                    // get the permissions of the user on the object:
                    AccessPermissions permissions =
                        this.performGetRightsContainerData (this.oid, this.user);

                    // check if the user has the necessary permissions:
                    if (permissions.checkObjectPermissions (Operations.OP_SETRIGHTS))
                                        // the user has the permissions?
                    {
                        this.canSetRights = true;
                    } // if the user has the permissions
                } // if object was just created

                // show the object's data:
                if ((performOK = this.checkShowChangeFormConstraints ()) == true)
                {
                    this.performShowChangeForm (representationForm, changeFormFct);
                } // if object data to be shown

                this.framesetPossible = true; // frameset view is possible again
            } // else don't show as frameset
        } // if business object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server

        return performOK;
    } // showChangeForm


    /**************************************************************************
     * This method returns a boolean which is false if the object which should
     * be created, has e.g. no template. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     *
     * @return  A boolean which is false if the object has no constraints to
     *          other objects.
     *
     * @see #showChangeForm (int, int)
     */
    protected boolean checkShowChangeFormConstraints ()
    {
        return true;                    // normally the return value is true
    } // checkShowChangeFormConstraints


    /**************************************************************************
     * Change the object, i.e. store its properties within the database. <BR/>
     * The properties are gotten from the environment. <BR/>
     *
     * @param   representationForm  Kind of representation.
     *
     * @return  Function to be performed after the change operation.
     */
    public int change (int representationForm)
    {
        return this.change (representationForm, false);
    } // change


    /**************************************************************************
     * Change the object, i.e. store its properties within the database. <BR/>
     * The properties are gotten from the environment. <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   showChangeForm      Shall the change form be displayed after
     *                              the operation?
     *                              Default: false = showInfo
     *
     * @return  The function to be performed after calling this method.
     */
    public int change (int representationForm, boolean showChangeForm)
    {
        boolean showChangeFormLocal = showChangeForm; // variable for local assignments
        String newObjectAction =
            this.env.getParam (BOConstants.NEW_BUSINESS_OBJECT_MENU);
        int retVal = BOConstants.BONEWMENU_NOTHING;


        // store the object's data within the database:
        try
        {
            // try to store the object to the database:
            retVal = this.performChange ();
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            // send message to the user:
            this.showNoAccessMessage (Operations.OP_CHANGE);
        } // catch
        catch (NameAlreadyGivenException e) // name of object already given?
        {
            // send message to the user:
            this.showNameAlreadyGivenMessage ();
            // display the change form again, but only if the object is allowed
            // to be displayed:
            if (this.showAllowed)
            {
                showChangeFormLocal = true;
            } // if
            newObjectAction = null;
        } // catch
// KR 011206 NOTE: The following exception handling can be deleted if the
// retrieve within performChange is deleted.
        catch (AlreadyDeletedException e) // no access to objects allowed
        {
            // send message to the user:
            this.showAlreadyDeletedMessage ();
        } // catch

        // check if there is already a return value defined:
        if (retVal != BOConstants.BONEWMENU_NOTHING)
        {
            // perform specific actions depending on the return value:
            if (retVal == BOConstants.BONEWMENU_RESHOW_FORM)
            {
                this.showChangeForm (representationForm);
            } // if

            // return the result:
            return retVal;
        } // if

        if (newObjectAction == null)    // standard change form?
        {
            // show the object to the user:
            if (showChangeFormLocal)             // show change form?
            {
                this.showChangeForm (representationForm);
            } // if show change form
            else                            // show info view
            {
                if (this.showAllowed)
                {
                    // set the correct function:
                    this.sess.p_actFct = AppFunctions.FCT_SHOWOBJECTINFO;
                    this.showInfo (representationForm);
                } // if
            } // else show info view
        } // if standard change form
        else if (newObjectAction.equals (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SAVE_AND_NEW, env)))
                                        // save the new object and create
                                        // another one
        {
            return BOConstants.BONEWMENU_NEW_BUSINESS_OBJECT;
        } // else if save the new object and create another one
        else if (newObjectAction.equals (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SAVE_AND_BACK, env)))
                                        // save the new object and go back to
                                        // the clipboard?
        {
            return BOConstants.BONEWMENU_BACK_TO_THE_CLIPBOARD;
        } // else if save the new object and go back to the clipboard
        else if (newObjectAction.equals (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TO_RIGHTS, env)))
        {
            this.restoreState ();
            return BOConstants.BONEWMENU_TO_RIGHTS;
        } // else if
        else                            // save the object and go to it?
        {
            // show the object to the user:
            if (showChangeFormLocal)             // show change form?
            {
                this.showChangeForm (representationForm);
            } // if show change form
            else                            // show default view
            {
                if (this.showAllowed)
                {
                    // tell the caller of this method (Application) to display
                    // the actual object:
                    return BOConstants.BONEWMENU_SHOW_BUSINESS_OBJECT;
/* KR let this handling be done by the caller (Application)
                    show (representationForm);
*/
                } // if
            } // else show default view
        } // else save the object and go to it

        return BOConstants.BONEWMENU_NOTHING;
    } // change


    /**************************************************************************
     * Change the object, i.e. store its properties within the database. <BR/>
     * The properties are gotten from the environment. <BR/>
     *
     * @return  The function to be performed after calling this method.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   NameAlreadyGivenException
     *              An object with this name already exists. This exception is
     *              only raised by some specific object types which don't allow
     *              more than one object with the same name.
     * @exception   AlreadyDeletedException
     *              The required object was deleted before the user wanted to
     *              access it.
     */
    public int performChange ()
        throws NoAccessException, NameAlreadyGivenException,
               AlreadyDeletedException
    {
        // store the object's data within the database:
        if (this.state == States.ST_CREATED || this.state == -1)
                                    // the object was just created?
        {
            // show the tabs of this object after changing it:
            this.displayTabs = true;
            this.disableButtonsState = true;
        } // if the object was just created

        // save the state to allow a later restoring:
        this.saveState ();

        // try to store the object to the database:
//        performChangeData (Operations.OP_CHANGE);
        this.performChange (Operations.OP_CHANGE);

        try
        {
// KR 011206 NOTE: The following method call can be deleted if the data is not
// changed within performChangeData (i.e. within the database procedure).
// BB 050208: TODO: this call produces overhead and become a performance killer
            // ensure that the data are actual:
            this.retrieve (Operations.OP_READ);
        } // try
        catch (ObjectNotFoundException e)
        {
            // this exception cannot occur.
        } // catch

        // return default value:
        return BOConstants.BONEWMENU_NOTHING;
    } // performChange


    /**************************************************************************
     * Change the data of a business object in the database using a given
     * operation. <BR/>
     * Note that this method does not do any retrieve whichs means that
     * any changes to the data of the businessobject that is done during
     * the change stored procedure will not be available. <BR/>
     * Known sideeffect: the state of a newly created object stays ST_CREATED
     * instead of ST_ACTIVE. getTabBar () does not work properly when called
     * after this method. Because of the wrong state getTabBar returns a null
     * value that can lead to a NullPointerException. <BR/>
     * Therefore be careful when using this method!. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   NameAlreadyGivenException
     *              An object with this name already exists. This exception is
     *              only raised by some specific object types which don't allow
     *              more than one object with the same name.
     *
     * @see #performChange ()
     * @see #performChangeData (int)
     */
    public void performChange (int operation)
        throws NoAccessException, NameAlreadyGivenException
    {
        // change the data of the object:
        this.performChangeData (operation);
    } // performChange


    /**************************************************************************
     * Store the object, i.e. create it and store its actual properties within
     * the database. <BR/>
     * The properties are gotten from the environment. <BR/>
     *
     * @param   opCreate        Necessary permissions for creating the object.
     * @param   opChange        Necessary permissions for changing the object's
                                data.
     *
     * @return  Oid of the newly created object. <BR/>
     *          <CODE>null</CODE> if the object could not be created.
     */
    public OID store (int opCreate, int opChange)
    {
        OID newOid = null;              // oid of newly created object
        // store the object's data within the database:
        try
        {
            // try to store the object to the database:
            newOid = this.performCreateData (opCreate);

            // store the object's data within the database:
            try
            {
                // try to store the object to the database:
                this.performChangeData (opChange);
            } // try
            catch (NameAlreadyGivenException e) // name of object already given?
            {
                // send message to the user:
                this.showNameAlreadyGivenMessage ();
            } // catch
            catch (NoAccessException e) // no access to object allowed
            {
                // send message to the user:
                this.showNoAccessMessage (opChange);
            } // catch
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            // send message to the user:
            this.showNoAccessMessage (opCreate);
            newOid = null;
        } // catch
        catch (NameAlreadyGivenException e) // Name of the user already given
        {
            // send message to the user:
            this.showNameAlreadyGivenMessage ();
            newOid = null;
        } // catch

        return newOid;                  // return the oid of the newly created
                                        // object
    } // store


    /**************************************************************************
     * Store the object, i.e. create it and store its actual properties within
     * the database. <BR/>
     * The properties are gotten from the environment. <BR/>
     *
     * @param   representationForm  Kind of representation.
     *
     * @return  Oid of the newly created object. <BR/>
     *          <CODE>null</CODE> if the object could not be created.
     */
    public OID store (int representationForm)
    {
        // call common method and return the result:
        return this.store (Operations.OP_NEW, Operations.OP_CHANGE);
    } // store


    /**************************************************************************
     * Force the storage of the object independent of the permissions. <BR/>
     * This methods creates the object and stores its actual properties within
     * the database. <BR/>
     * The properties are gotten from the environment. <BR/>
     *
     * @return  Oid of the newly created object. <BR/>
     *          <CODE>null</CODE> if the object could not be created.
     */
    public OID forceStore ()
    {
        // call common method and return the result:
        return this.store (Operations.OP_NONE, Operations.OP_NONE);
    } // forceStore


    /**************************************************************************
     * Show confirm message for deleting an object. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showDeleteConfirmation (int representationForm)
    {
/* KR 011205: not necessary because already done before
        // delete the object from the database:
        try
        {
            // try to retrieve the object:
            retrieve (Operations.OP_DELETE);
*/
            // show the confirmation message:
        this.performShowDeleteConfirmation (representationForm);
/* KR 011205: not necessary because already done before
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            // send message to the user:
            showNoAccessMessage (Operations.OP_DELETE);
        } // catch
        catch (AlreadyDeletedException e) // no access to objects allowed
        {
            // send message to the user:
            showAlreadyDeletedMessage ();
        } // catch
*/
        // show the object to the user:
//        show (representationForm);
    } // showDeleteConfirmation


    /**************************************************************************
     * Delete the object, i.e. delete its properties from the database. <BR/>
     *
     * @param   representationForm  Kind of representation.
     *
     * @return  boolean which is false if something went wrong
     */
    public boolean delete (int representationForm)
    {
        boolean allOk = true;           // return value of this method

        // delete the object from the database:
        try
        {
            // try to delete the object from the database:
            this.performDeleteData (Operations.OP_DELETE);
            // to avoid, that a deleted object might be inserted
            this.getUserInfo ().copiedOids.removeElement (this.oid);
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            // send message to the user:
            this.showNoAccessMessage (Operations.OP_DELETE);
            allOk = false;
        } // catch
        catch (ObjectNotAffectedException e)
        {
            // send corresponding message to the user:
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_NOTALLDELETEABLE, this.env),
                this.app, this.sess, this.env);
            allOk = false;
        } // catch
        catch (DependentObjectExistsException e)
        {
            // send corresponding message to the user:
            this.showPopupMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_DEPENDENTOBJECTEXISTS, this.env));
            allOk = false;
        } // catch

        // show the object to the user:
//        show (representationForm);

        return allOk;
    } // delete


    /**************************************************************************
     * Delete the object without permission check. <BR/>
     * This extra method is neccessary because the recursive rights check
     * will fail when deleting with OP_NONE operation. <BR/>
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ObjectNotAffectedException
     *              The operation could not be performed on all required
     *              objects.
     * @exception   DependentObjectExistsException
     *              The object could not be deleted because there are still
     *              objects which are refer to this object.
     */
    protected void performForceDeleteData ()
        throws NoAccessException, ObjectNotAffectedException,
        DependentObjectExistsException
    {
        int retVal = UtilConstants.QRY_OK; // return value of query

        // set the NONE operation in order to avoid the permission check
        int operation = Operations.OP_NONE;

        // check if the object is deletable:
        this.checkDeletable ();

        try
        {
            // check if we have the oid of an existing object or of a virtual
            // object:
            // (a virtual object is an object which has no representation in the
            // object repository and is just created for some specific temporary
            // use)
            // The rights object is an exception to this rule because it can
            // perform the delete operation if it does not exist physically.
            if (!this.oid.isTemp () ||
                this.oid.type == this.getTypeCache ().getTypeId (TypeConstants.TC_Rights))
            // existing object?
            {
                // create the stored procedure call:
                StoredProcedure sp = new StoredProcedure(
                        (this.deleteRecursive) ?
                                this.procDeleteRec :         // call recursive procedure
                                this.procDelete,             // call non-recursive procedure
                        StoredProcedureConstants.RETURN_VALUE);

                // set the parameters for the delete operation:
                this.setSpecificDeleteParameters (sp, operation);

                // perform the function call:
                retVal = BOHelpers.performCallFunctionData(sp, this.env);
            } // if existing object
            else                        // virtual object
            {
                // the operation failed:
                retVal = UtilConstants.QRY_NOTALLAFFECTED;
            } // else virtual object

            if (retVal == UtilConstants.QRY_NOTALLAFFECTED)
            {
                // raise object (s) not affected exception
                ObjectNotAffectedException error = new ObjectNotAffectedException (
                    MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
                        UtilExceptions.ML_E_OBJECTNOTAFFECTEDEXCEPTION, env));
                throw error;
            } // else if QRY_NOTALLAFFECTED
            else if (retVal == UtilConstants.QRY_DEPENDENT_OBJECT_EXISTS)
            {
                // raise dependent object (s) exists exception
                DependentObjectExistsException error = new DependentObjectExistsException (
                    MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
                        UtilExceptions.ML_E_DEPENDENTOBJECTEXISTS, env));
                throw error;
            } // else if QRY_DEPENDENT_OBJECT_EXISTS
            else                        // access allowed
            {
                // room for some success statements
            } // else access allowed
        } // try
        catch (ObjectNotAffectedException e)
        {
            // raise object (s) not affected exception
            ObjectNotAffectedException error = new ObjectNotAffectedException (
                MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
                    UtilExceptions.ML_E_NOTALLDELETEABLE, env));
            error.addError (e.getError ());
            throw error;
        } // catch ObjectNotAffectedException
    } // performForceDeleteData


    /*************************************************************************
     * Undelete the object, i.e. delete its properties from the database. <BR/>
     *
     * @param   representationForm  Kind of representation.
     *
     * @return  boolean which is false if something went wrong
     */
    public boolean undelete (int representationForm)
    {
        boolean allOk = true;           // return value of this method

        // undelete the object from the database:
        try
        {
            // try to delete the object from the database:
            this.performUnDeleteData (Operations.OP_DELETE);
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            // send message to the user:
            this.showNoAccessMessage (Operations.OP_DELETE);
            allOk = false;
        } // catch
        catch (ObjectNotAffectedException e)
        {
            // send corresponding message to the user:
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_NOTALLAFFECTED, this.env),
                this.app, this.sess, this.env);
            allOk = false;
        } // catch
        catch (DependentObjectExistsException e)
        {
            // send corresponding message to the user:
            this.showPopupMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_DEPENDENTOBJECTEXISTS, this.env));
            allOk = false;
        } // catch

        // show the object to the user:
//        show (representationForm);

        return allOk;
    } // undelete


    /**************************************************************************
     * Get the oid of the object which is the virtual container of the actual
     * object. <BR/>
     * For standard object this is the containerId.
     *
     * @return  The oid of the object which is the container of the actual
     *          object.
     *          <CODE>null</CODE> if there is no container found.
     */
    public OID getMajorContainerOid ()
    {
        // just return the actual container oid:
        return this.containerId;
    } // getMajorContainerOid


    /**************************************************************************
     * Get the oid of the object which is in the hierarchy above the actual
     * object out of the database. <BR/>
     *
     * @return  The oid of the object which is above the actual object.
     *          <CODE>null</CODE> if there is no upper object.
     *
     * @exception   NoMoreElementsException
     *              The object is the topmost one.
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ObjectNotFoundException
     *              The object was not found.
     */
    public OID getUpperOid ()
        throws NoAccessException, ObjectNotFoundException, NoMoreElementsException
    {
        OID upperOid = null;            // oid of upper object

        if (true)                       // actual object resists on this
                                        // server?
        {
            // try to retrieve the upper object of this object:
            upperOid = this.performRetrieveUpperData ();
        } // if actual object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server
        return upperOid;                // return the oid of the upper object
    } // getUpperOid


    /**************************************************************************
     * Get the tab object which represents a tab of the actual object
     * determined by the tab's id. <BR/>
     * If the property {@link #p_tabs p_tabs} is <CODE>null</CODE> it is set to
     * the (user-independent) tabs of this object.
     *
     * @param   tabId   The unique id of the tab.
     *
     * @return  A tab object representing the data of the tab.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ObjectNotFoundException
     *              The object was not found.
     */
    public Tab getTabObject (int tabId)
        throws NoAccessException, ObjectNotFoundException
    {
        Tab tab = null;                 // the tab's data

        if (true)                       // actual object resists on this
                                        // server?
        {
            // check if the tabs property is already filled:
            if (this.p_tabs == null)    // the tabs are not known?
            {
                // get the tab data out of the data base:
                this.p_tabs = this.performRetrieveObjectTabData ();
            } // if the tabs are not known

            if (this.p_tabs != null)    // the tabs are now known?
            {
                // get the tab:
                tab = this.p_tabs.get (tabId);
            } // if the tabs are now known
        } // if actual object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server

        // return the tab object:
        return tab;
    } // getTabObject


    /**************************************************************************
     * Check if an object of a specific type can be inserted within this
     * object. <BR/>
     * The parameter should be a valid tVersionId. Currently this method also
     * works if this is just a type id.
     *
     * @param   type    Type of the object which shall be inserted.
     *
     * @return true if the object is allowed to be inserted, false otherwise.
     */
    public boolean isAllowedType (int type)
    {
        ITypeContainer<Type> types;     // all allowed types
        Type typeO = null;              // the found type
        int tVersionId;

        // ensure that there is a valid tVersionId:
        tVersionId = Type.createTVersionId (type);

        // get the types:
        types = this.typeObj.getMayContainTypes ();

        // try to find the type:
        typeO = types.get (tVersionId);

        return typeO != null;           // return if the type is allowed
    } // isAllowedType


    /**************************************************************************
     * Calls the cleaning expired objects methods and displays results
     * depending on selected representation format. <BR/>
     *
     * @param   representationForm  Kind of representation.
     *
     * @see #performClean
     */
    public void clean (int representationForm)
    {
        if (true)                       // business object resists on this
                                        // server?
        {
            // show the Search Form
            this.performClean (representationForm);
        } // if business object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server
    } // clean


    /**************************************************************************
     * Get the oid of the object which represents the tab of the actual object
     * determined by the tab's name out of the database. <BR/>
     * The name of the object and the tab shall be the same.
     *
     * @return  The oid of the object which is a tab of the actual object.
     *          null if there is no corresponding tab object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ObjectNotFoundException
     *              The object was not found.
     */
    public OID getMaster ()
        throws NoAccessException, ObjectNotFoundException
    {
        OID masterOid = null;           // oid of tab object

        if (true)                       // actual object resists on this
                                        // server?
        {
            // try to retrieve the upper object of this object:
            masterOid = this.performRetrieveMasterData ();
        } // if actual object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server

        return masterOid;               // return the oid of the tab object
    } // getMaster


    /**************************************************************************
     * Change the state of a business object in the database. <BR/>
     * <BR/>
     * This method tries to store the object state into the database.
     * During this operation a rights check is done, too.
     *
     * @param   operation   Operation to be performed with the object.
     */
    public void changeState (int operation)
    {
        try
        {
            this.performChangeState (operation);
        } // try
        catch (NoAccessException e)
        {
            this.showNoAccessMessage (operation);
        } // catch
    } // changeState


    /**************************************************************************
     * Change the processState of a business object in the database. <BR/>
     * <BR/>
     * This method tries to store the object state into the database.
     * During this operation a rights check is done, too.
     *
     * @param   operation   Operation to be performed with the object.
     */
    public void changeProcessState (int operation)
    {
        try
        {
            this.performChangeProcessState (operation);
        } // try
        catch (NoAccessException e)
        {
            this.showNoAccessMessage (operation);
        } // catch
    } // changeProcessState


    /**************************************************************************
     * Retrieve the processState of a business object in the database. <BR/>
     * <BR/>
     * This method tries to read the object processState from the database.
     */
    public void retrieveProcessState ()
    {
        try
        {
            this.performRetrieveProcessState ();
        } // try
        catch (NoAccessException e)
        {
            this.showNoAccessMessage (Operations.OP_READ);
        } // catch
    } // retrieveProcessState


    /**************************************************************************
     * Performs copying of an object reference into a container. <BR/>
     * Initially the target object must have been pasted by the user. <BR/>
     *
     * @param   targetId            Target container where the copy of the
     *                              object shall be placed.
     * @param   representationForm  Old parameter, not necessary! Use
     *                              {@link UtilConstants#REP_STANDARD
     *                              UtilConstants.REP_STANDARD}.
     * @param   obj                 The object in which to check if the copy
     *                              is allowed to be inserted.
     *
     * @return  Oid of the copy.
     */
    public OID copy (OID targetId, int representationForm, BusinessObject obj)
    {
        OID newRootOid = null;          // oid of the newly created object

        // duplicate and link the object structure in the database:
        if (obj.isAllowedType (this.type)) // the correct type to insert?
        {
            try
            {
                // try to copy in database:
                newRootOid = this.performCopyData (targetId, Operations.OP_COPY);
            } // try
            catch (NoAccessException e) // no access to object allowed
            {
                // send message to the user:
                this.showNoAccessMessage (Operations.OP_COPY);
                newRootOid = null;
            } // catch
        } // if the correct type to insert
        else                            // type may not be inserted
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTINSERTFAIL, this.env),
                this.app, this.sess, this.env);
        } // else type may not be inserted

        // show the object to the user:
        // show (representationForm);

        // return the ???:
        return newRootOid;
    } // copy


    /**************************************************************************
     * Performs moving of an object from a source into an destination container. <BR/>
     * Initially the target object must have been pasted by the user. <BR/>
     *
     * @param targetId              The Oid of the target BusinessObject.
     * @param representationForm    ??? .
     * @param   targetObj           The target object.
     */
    public void move (OID targetId, int representationForm, BusinessObject targetObj)
    {

        if (targetObj.isAllowedType (this.type)) // the correct type to insert?
        {
            // update the the objectstrukture from the database:
            try
            {
                // try to move within the database:
                this.performMoveData (targetId, Operations.OP_MOVE); // not defined
            } // try
            catch (NoAccessException e) // no access to object allowed
            {
                // send message to the user:
                this.showNoAccessMessage (Operations.OP_MOVE);
            } // catch
        } // if the correct type to insert
        else
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTINSERTFAIL, this.env),
                this.app, this.sess, this.env);
        } // else

        //  show the object to the user:
        //  show (representationForm);
    } // move


   /***************************************************************************
    * Paste a BusinessObject in the new context. You use this method when a cut
    * or a copy was made and the user wants to insert the marked
    * BusinessObject. <BR/>
    */
    public void showPasteForm ()
    {
        int i = 0;                      // loop variable
        Page page = new Page ("Form Clean", false);
        int maxElem = 1;
        RowElement tr;
        TableDataElement td;
        InputElement iel;
        String[] alignments = new String[maxElem];

        // set alignments:
        for (i = 0; i < maxElem; i++)
        {
            alignments[i] = IOConstants.ALIGN_CENTER;
        } // for i

        // create header
        FormElement form = this.createFormHeader (page, this.name, this.getNavItems (),
                                             null, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FUNCTIONPASTE, env),
                                             null, this.containerName);

        // set form parameter
        form.addElement (new InputElement (BOArguments.ARG_OID,
                                           InputElement.INP_HIDDEN,
                                           "" + this.oid));
        form.addElement (new InputElement (BOArguments.ARG_FUNCTION,
                                           InputElement.INP_HIDDEN,
                                           "" + AppFunctions.FCT_OBJECTPASTEPERFORM));

        // inner table
        TableElement table =  new TableElement (4);
        table.border = 0;
        table.ruletype = IOConstants.RULE_GROUPS;
        table.frametypes = IOConstants.FRAME_VOID;
        table.width = "50%";
        table.cellpadding = 1;

        // construct form:
/*
        tr = new RowElement (5);
        td = new TableDataElement (new TextElement ("Eine Verknüpfung oder eine Kopie erstellen ?"));
        tr.addElement (td);

        // create a checkbox
        iel = new InputElement (BOArguments.ARG_PASTEKIND,
                                InputElement.INP_CHECKBOX, Datatypes.BOOL_TRUE);
        iel.checked = true;
        td = new TableDataElement (iel);
        tr.addElement (td);
*/

        tr = new RowElement (5);
        td = new TableDataElement (new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_REFERENCEKIND, env)));
        tr.addElement (td);

        // create a radiobutton
        iel = new InputElement (BOArguments.ARG_PASTEKIND,
                                InputElement.INP_RADIO, Datatypes.BOOL_TRUE);
        iel.checked = true;
        td = new TableDataElement (iel);
        tr.addElement (td);

        table.addElement (tr);
        tr = new RowElement (5);
        td = new TableDataElement (new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_REALCOPYKIND, env)));
        tr.addElement (td);

        // create a checkbox
        iel = new InputElement (BOArguments.ARG_PASTEKIND,
                                InputElement.INP_RADIO, Datatypes.BOOL_FALSE);
     //   iel.checked = false;
        td = new TableDataElement (iel);
        tr.addElement (td);

        table.addElement (tr);
        form.addElement (table);

        // create footer
        this.createFormFooter (form);

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e.getMsg (), this.app, this.sess, this.env);
        } // catch
    } // showPasteForm


    /**************************************************************************
     * Check if the user has the necessary rights to perform the requested
     * operation (s). <BR/>
     *
     * @param   userId      Id of the user who wants to access the object.
     * @param   operation   Operation (s) to be performed with the object.
     *
     * @return  <CODE>true</CODE> if the user has the necessary rights,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean checkRights (int userId, int operation)
    {
        boolean retVal = false;         // the return value
        int cachedRights;

        if ((cachedRights = this.accessCache.get (userId, operation)) != -1)
                                        // already in cache?
        {
/* KR not necessary
            if (userId == 25165828)
                showMessage ("cachedRights: " + cachedRights);
*/
            retVal = cachedRights == 1; // determine if user has the rights
        } // if already in cache
        else                            // rights not cached yet
        {
/* KR not necessary
            if (userId == 25165828)
                showMessage ("cachedRights: " + cachedRights);
*/
            // get the rights from the data store:
            retVal = this.performCheckRights (userId, operation);

            // store the rights within the access cache:
            this.accessCache.put (userId, operation, retVal);
        } // else rights not cached yet

        return retVal;                  // return the computed return value
    } // checkRights


    /**************************************************************************
     * Check if the user has the necessary rights on the specific object to
     * perform the required operation (s) within the data store. <BR/>
     *
     * @param    oid            Oid of the object.
     * @param   userId      Id of user whose rights we want to check.
     * @param   operation   Operation (s) the user wants to perform on the
     *                      object.
     *
     * @return  <CODE>true</CODE> if the user is allowed to perform the
     *          operation (s), <CODE>false</CODE> otherwise.
     */
    public boolean checkObjectRights (OID oid, int userId, int operation)
    {
        boolean hasRights = false;      // does the user have the rights?
                                        // (return value of this method)

        // check if the object is deletable:
/* TODO KR This check may not be performed like this because it works on the
 * actual object instead of the oid and userId parameters.
        checkDeletable ();
*/

        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                this.procObjRights,
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // oid
        BOHelpers.addInParameter (sp, this.oid);
        // containerId
        BOHelpers.addInParameter (sp, this.containerId);
        // user id
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, userId);
        // requiredRights
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, operation);

        // output parameters
        // hasRights
        Parameter hasRightsParam = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

        try
        {
            // perform the function call:
            BOHelpers.performCallFunctionData(sp, this.env);
        } // try
        catch (NoAccessException e)
        {
            // nothing to do
        } // catch

        // return the rights
        hasRights = hasRightsParam.getValueInteger () == operation;

        return hasRights;               // return the computed value
    } // checkObjectRights


    /**************************************************************************
     * Check if the object is deletable. <BR/>
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    public void checkDeletable ()
        throws NoAccessException
    {
        // check if the object is deletable:
        if (!this.isDeletable ())
        {
            // raise no access exception:
            NoAccessException error = new NoAccessException (
                MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
                    UtilExceptions.ML_E_NOACCESSEXCEPTION, env));
            throw error;
        } // if
    } // checkDeletable


    /**************************************************************************
     * Check if the object is deletable. <BR/>
     * This method checks whether the isDeletable flag is set to
     * <CODE>true</CODE> or the actual user is the <CODE>"Administrator"</CODE>.
     *
     * @return  <CODE>true</CODE> if the object is deletable,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isDeletable ()
    {
        // check if the object is deletable and return the result:
        return this.p_isDeletable ||
                this.getUser ().username.equalsIgnoreCase (IOConstants.USERNAME_ADMINISTRATOR);
    } // isDeletable


    /**************************************************************************
     * Check if the object is changeable. <BR/>
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    public void checkChangeable ()
        throws NoAccessException
    {
        // check if the object is changeable:
        if (!this.isChangeable ())
        {
            // raise no access exception:
            NoAccessException error = new NoAccessException (
                MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
                    UtilExceptions.ML_E_NOACCESSEXCEPTION, env));
            throw error;
        } // if
    } // checkChangeable


    /**************************************************************************
     * Check if the object is changeable. <BR/>
     * This method checks whether the isChangeable flag is set to
     * <CODE>true</CODE> or the actual user is the <CODE>"Administrator"</CODE>.
     *
     * @return  <CODE>true</CODE> if the object is changeable,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isChangeable ()
    {
        // check if the object is changeable and return the result:
        return this.p_isChangeable ||
               this.getUser ().username.equalsIgnoreCase (IOConstants.USERNAME_ADMINISTRATOR);
    } // isChangeable

    
    /**************************************************************************
     * Check if the object has attached files. <BR/>
     * This method checks if the hasFile flag is set to <CODE>true</CODE>
     * 
     *  @return <CODE>true</CODE> if the object has attached files,
     *  		<CODE>false</CODE> otherwise.
     */
    public boolean getFileFlag ()
    {
    	return this.p_hasFileFlag;
    } // getFileFlag

    ///////////////////////////////////////////////////////////////////////////
    // internal helper functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Checks if the object is actual. <BR/>
     * This method checks if the object data where already read from the
     * data store. <BR/>
     * As extension there could be a check if the data has changed within the
     * data store since it was read. <I>Not implemented yet!</I>. <BR/>
     *
     * @return  ???
     */
    protected boolean isActual ()
    {
        // check if the object is actual and return the result:
        return this.isActual;
    } // isActual



    /**************************************************************************
     * Sets the object actual. <BR/>
     * This method sets the object actual. <BR/>
     */
    protected void setActual ()
    {
        // set the object actual:
//        this.isActual = true;
    } // setActual


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The {@link #env env} property is used for getting the parameters.
     * That property must be set before calling this method. <BR/>
     */
    public void getParameters ()
    {
        OID actOid = null;
        String str = null;
        int num = 0;
        Date date = null;

        // get id of container:
        if ((actOid = this.env.getOidParam (this
            .adoptArgName (BOArguments.ARG_CONTAINERID))) != null &&
            !this.isTab ())
        {
            this.containerId = actOid;
        } // if

        // state
        if ((num = this.env.getIntParam (this
            .adoptArgName (BOArguments.ARG_STATE))) !=
                IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.state = num;
        } // if

        // name
        if ((str = this.env.getStringParam (this
            .adoptArgName (BOArguments.ARG_NAME))) != null)
        {
            this.name = str;
        } // if

        // owner
        if ((num = this.env.getIntParam (this
            .adoptArgName (BOArguments.ARG_OWNER))) !=
                IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.owner = new User (num);
        } // if

        // type
        if ((num = this.env.getIntParam (this
            .adoptArgName (BOArguments.ARG_TYPE))) !=
                IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.type = num;
        } // if

        // description
        if ((str = this.env.getStringParam (this
            .adoptArgName (BOArguments.ARG_DESCRIPTION))) != null)
        {
            this.description = str;
        } // if

        // showInNews
        if ((num = this.env.getBoolParam (this
            .adoptArgName (BOArguments.ARG_INNEWS))) >=
                IOConstants.BOOLPARAM_FALSE)
        {
            this.showInNews = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        // validUntil
        if ((date = this.env.getDateParam (this
            .adoptArgName (BOArguments.ARG_VALIDUNTIL))) != null)
        {
            this.validUntil = date;
        } // if

        if ((num = this.env.getBoolParam (this
            .adoptArgName (BOArguments.ARG_BUTTONSDISABLESTATE))) >=
                IOConstants.BOOLPARAM_FALSE)
        {
            this.disableButtonsState = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        if ((num = this.env.getBoolParam (this
            .adoptArgName (BOArguments.ARG_ISFRAMESET))) >=
                IOConstants.BOOLPARAM_FALSE)
        {
            this.framesetPossible = !(num == IOConstants.BOOLPARAM_TRUE);
        } // if

        if ((num = this.env.getIntParam (this
            .adoptArgName (BOArguments.ARG_SHOWTABBAR))) !=
                IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.displayTabs = num == 1;
        } // if

        if ((num = this.env.getIntParam (this
            .adoptArgName (BOArguments.ARG_WEBLINK))) !=
                IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.sess.weblink = num == 1;
        } // if

        if ((num = this.env.getBoolParam (this
            .adoptArgName (BOArguments.ARG_RECURSIVE))) >=
                IOConstants.BOOLPARAM_FALSE)
        {
            this.deleteRecursive = num == IOConstants.BOOLPARAM_TRUE;
        } // if
    } // getParameters


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
        // return the field name unchanged:
        return fieldName;
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
        // return the argument name unchanged:
        return argName;
    } // adoptArgName


    /**************************************************************************
     * This method transforms a string into a valid argument that can be used
     * in a form. <BR/>
     *
     * @param   field   The name of a field to be transformed into an argument.
     *
     * @return  A valid argument.
     */
    protected String createArgument (String field)
    {
        // return the field unchanged:
        return field;
    } // ceateArgument


    /***************************************************************************
     * Set the name of the actual business object. <BR/>
     * This method can be overwritten by classes which set specific names.
     */
    protected void setName ()
    {
        // nothing to do
    } // setName


    /**************************************************************************
     * Set the icon of the actual business object. <BR/>
     * If the icon is already set this method leaves it as is.
     * If there is no icon defined yet, the icon name is derived from the name
     * of the type of this object. <BR/>
     */
    protected void setIcon ()
    {
        // check if there is an icon defined:
        if (this.icon == null || this.icon.length () == 0)
                                        // there is no icon defined?
        {
            // get the type object:
            if (this.typeObj != null)   // type was set?
            {
                // get type code and append image extension:
                this.icon = this.typeObj.getIcon (); // get type icon
            } // if type was set
        } // if there is no icon defined
    } // setIcon


    /**************************************************************************
     * Save the actual state for later restoring. <BR/>
     */
    protected void saveState ()
    {
        this.oldState = this.state;
    } // saveState


    /**************************************************************************
     * Restore the state to the value it had before performing the actual
     * operation. <BR/>
     * The actual state value is changed to {@link #oldState oldState}
     * and stored within the data store. <BR/>
     */
    protected void restoreState ()
    {
        try
        {
/* runs only if there was a retrieve after changing the state.
            if (this.oldState != this.state)
                                        // the state was changed?
            {
*/
            if (this.oldState > 0)      // valid state?
            {
                this.state = this.oldState; // set the state to the old value

                // change the state to the old value:
                this.performChangeState (Operations.OP_EDIT | Operations.OP_CHANGESTATE);
            } // if valid state
/*
            } // if the state was changed
*/
        } // try
        catch (NoAccessException e)
        {
            // nothing to do
        } // catch
    } // restoreState


    /**************************************************************************
     * Gets the parameter value of a ibs.bo.Datatypes.DT_FILE type. <BR/>
     * The 'BO' in the methods name only indicates that it is not (like the
     * other getxxxParam methods) a method of the environment class.
     *
     * @param   arg     parameter arguments name
     *
     * @return  The file name or <CODE>null</CODE> if the argument was not found
     *          or another error occurred.
     */
    public String getFileParamBO (String arg)
    {
        // call common method and return result:
        return BOHelpers.getFileParamBO (arg, this.env);
    } // getFileParamBO


    /**************************************************************************
     * Gets the parameter value of a
     * {@link ibs.bo.Datatypes#DT_FILE Datatypes.DT_FILE} type. <BR/>
     * This method calls {@link ibs.bo.BusinessObject#getFileParamBO getFileParamBO}
     * and ensures that the file name starts with a specific oid.
     *
     * @param   argName     Parameter argument's name.
     * @param   oid         The required oid.
     *
     * @return  The file name or <CODE>null</CODE> if the argument was not found
     *          or another error occurred.
     */
    public String getFileParamBOWithOid (String argName, OID oid)
    {
        String fileName = null;         // the name of the actual file
        String filePath = null;         // the path and name of the file
        String origFilePath = null;     // the original path and name

        // try to get the parameter:
        if ((fileName = this.getFileParamBO (argName)) != null) // got the file name?
        {
            // ensure that the oid is contained in the file name:
            if (oid != null && fileName.indexOf (oid.toString ()) < 0)
                                        // no oid in file name?
            {
                // get the original path:
                origFilePath = this.env.getFilePath (argName);
                // prepend the oid to the file and compute the new path:
                fileName = oid.toString () + fileName;
                // get the directory and append the new file name to it:
                File file = new File (origFilePath);
                filePath = file.getParentFile ().getAbsolutePath () +
                    File.separator + fileName;

                // check if the file exists:
                if (FileHelpers.exists (origFilePath)) // file found?
                {
                    // check if there exists an old file with the new file name:
                    if (FileHelpers.exists (filePath)) // file exists?
                    {
                        // delete the old file:
                        FileHelpers.deleteFile (filePath);
                    } // if file exists

                    // rename the original file to the new name:
                    if (!FileHelpers.renameFile (origFilePath, filePath))
                                        // an error occurred?
                    {
                        IOHelpers.showMessage (MultilingualTextProvider
                            .getMessage (UtilExceptions.EXC_BUNDLE,
                                UtilExceptions.ML_E_NOFILEMOVE, env) +
                                origFilePath + "-->" + filePath,
                                               this.env);
                    } // if an error occurred
                } // if file found
            } // if no oid in file name
        } // if got the file name

        // return the computed file name:
        return fileName;
    } // getFileParamBOWithOid


    ///////////////////////////////////////////////////////////////////////////
    // database functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Returns an sql-action object associated with a connection. <BR/>
     *
     * @return  The action object associated with the required connection.
     */
    public SQLAction getDBConnection ()
    {
////////////
// HACK!! MS
        // if no session is available get the connection from
        // the global action pool of the DBConnector class.
        if (this.sess == null)
        {
            try
            {
                return DBConnector.getDBConnection ();
            } // try
            catch (DBError e)
            {
                IOHelpers.printError ("", this, e, true);
                IOHelpers.showMessage (e, this.app, this.sess, this.env);
                return null;
            } // catch
        } // if session not valid
// HACK!! MS
////////////


        if (this.sess.p_connectionPool == null) // no connection pool available?
        {
            try
            {
                Configuration conf = (Configuration) this.app.configuration;
                                        // the configuration object

                // create a new connection pool and store it within the session:
                // version for Release 2.0 Beta or newer:
                this.sess.p_connectionPool = new DBActionPool (conf.getDbConf ());
            } // try
            catch (DBError e)
            {
                IOHelpers.printError ("", this, e, true);
                IOHelpers.showMessage (e, this.app, this.sess, this.env);
            } // catch
        } // if

        // set the tracer holder of the connectionPool (needed for tracing)
        ((DBActionPool) this.sess.p_connectionPool).setTracerHolder (this.sess);

        try
        {
            // get a new action object associated with a connection from the pool
            // and return it:
            return ((DBActionPool) this.sess.p_connectionPool).getAction (this);
        } // try
        catch (DBError e)
        {
            IOHelpers.printError ("getDBConnection", this, e, true);
            IOHelpers.showMessage (e, this.app, this.sess, this.env);
            return null;
        } // catch
    } // getDBConnection


    /**************************************************************************
     * Releases an action-object associated with a database connection. <BR/>
     *
     * @param   action      The action object associated with the connection.
     */
    public void releaseDBConnection (SQLAction action)
    {
//String stacktrace = Helpers.getStackTraceFromThrowable (new Throwable ());
//String line = ExceptionUtils.getLineNumberFromStack (stacktrace);
//String implclass = ExceptionUtils.getImplementationClassNameFromStack (stacktrace);
//String mypackage = ExceptionUtils.getPackageInformationFromStack (stacktrace);
//String method = ExceptionUtils.getMethodNameFromStack (stacktrace);
//System.out.println ("releaseDBConnection called: from " + this.getClass () + "; method: " + method + ":"+line+"; implClass="+implclass);
////////////
// HACK!! MS
        // if no session is available release the connection from
        // the global action pool of the DBConnector class.
        if (this.sess == null || this.sess.p_connectionPool == null)
        {
            if (action != null)
            {
                try
                {
                    // release the action object:
                    DBConnector.releaseDBConnection (action);
                } // try
                catch (DBError e)
                {
                    IOHelpers.printError ("releaseDBConnection", this, e, true);
                } // catch
            } // if
            return;
        } // if session not valid
// HACK!! MS
////////////

        if (action != null)
        {
            try
            {
                // release the action object:
                ((DBActionPool) this.sess.p_connectionPool).releaseAction (this, action);
            } // try
            catch (DBError e)
            {
                // an error occurred - show name and info
                IOHelpers.showMessage (e, this.app, this.sess, this.env);
            } // catch
        } // if
    } // releaseDBConnection


    /**************************************************************************
     * Set the specific properties for a specific tabview. <BR/>
     * THIS METHOD CAN BE OVERWRITEN IN SUBCLASSES !!. <BR/>
     *
     * @param   majorObject The major object of this view tab.
     */
    public void setSpecificProperties (BusinessObject majorObject)
    {
        // get the common tab data:
        Tab tabView = this.getTabCache ().get (this.p_tabId);

        this.name = tabView.getName ();
    } // setSpecificProperties


   /***************************************************************************
    * Set the data for the additional (typespecific) parameters for
    * performCreateData. <BR/>
    * This method must be overwritten by all subclasses that have to pass
    * typespecific data to the create data stored procedure.
    *
     * @param sp        The stored procedure to add the create parameters to.
    */
    protected void setSpecificCreateParameters (StoredProcedure sp)
    {
        return;
    } // setSpecificCreateParameters


    /**************************************************************************
     * Store a new business object in the database. <BR/>
     * <B>THIS METHOD MUST NOT BE OVERWRITTEN IN SUB CLASSES!</B>
     * <BR/>
     * This method tries to store the new object into the database.
     * During this operation a rights check is done, too.
     * If this is all right the object is stored and this method terminates,
     * otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @return  Oid of the newly created object. <BR/>
     *          Null if the object could not be created.
     *
     * @throws  NoAccessException
     *          The user does not have access to this object to perform the
     *          required operation.
     * @throws  NameAlreadyGivenException
     *          An object with this name already exists. This exception is
     *          only raised by some specific object types which don't allow
     *          more than one object with the same name.
     */
    protected OID performCreateData (int operation)
        throws NoAccessException, NameAlreadyGivenException
    {
        OID newOid = null;              // oid of the newly created object

        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                this.procCreate,
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // user id
        if (this.user != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                            this.user.id);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                                 0);
        } // else
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                        operation);
        // type
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                        this.type);
        // name
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.name);
        // containerId
        BOHelpers.addInParameter(sp, this.containerId);
        // containerKind
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                        this.containerKind);
        // isLink
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
                        this.isLink);
        // linkedObjectId
        BOHelpers.addInParameter(sp, this.linkedObjectId);
        // description
        if (this.description != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_STRING,
                            this.description);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_STRING,
                            "");
        } // else

        // set type specific parameters:
        this.setSpecificCreateParameters (sp);

        // output parameters
        // oid
        Parameter oidParam = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        // perform the function call:
        BOHelpers.performCallFunctionData(sp, this.env);

        OID oldOid = this.oid;          // store old oid

        try
        {
            // set the new oid
            this.setOid (new OID (oidParam.getValueString ()));
            newOid = this.oid;
        } // try
        catch (IncorrectOidException e)
        {
            this.setOid (oldOid);       // reset oid
            newOid = null;              // reset new oid
        } // catch

        return newOid;                  // return the oid of the new object
    } // performCreateData


    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performChangeData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the change data stored procedure.
     *
     * @param sp        The stored procedure to add the change parameters to.
     */
    protected void setSpecificChangeParameters (StoredProcedure sp)
    {
        return;               // return the current index
    } // setSpecificChangeParameters


    /**************************************************************************
     * Change all type specific data that is not changed by performChangeData.
     * <BR/>
     * This method must be overwritten by all subclasses that have to change
     * type specific data.
     *
     * @param   action      The action object associated with the connection.
     *
     * @exception    DBError
     *               This exception is always thrown, if there happens
     *               an error during accessing data.
     */
    protected void performChangeSpecificData (SQLAction action) throws DBError
    {
        // this method may be overwritten by sub classes
    } // performChangeSpecificData


    /**************************************************************************
     * Change an attribute of text data. This method is used by
     * performChangeSpecificData. <BR/>
     *
     * @param   action      The action object associated with the connection.
     * @param   table       The table where the attribute is changed.
     * @param   attribute   The attribute to change.
     * @param   value       The attribute's new value.
     *
     * @exception    DBError
     *               This exception is always thrown, if there happens
     *               an error during accessing data.
     */
    protected final void performChangeTextData (SQLAction action, String table,
                                                String attribute, String value)
        throws DBError
    {
        String valueLocal = value;      // variable for local assignments
        // it's not possible to save null - values in clobs
        if (valueLocal == null || valueLocal.length () == 0)
        {
            valueLocal = " ";
        } // if

        // create the statement to update attribute in 'table' to 'value'
        UpdateStatement stmt = new UpdateStatement (table, null,
                "oid = " + this.oid.toStringQu ());
        stmt.addUnicodeTextToSet (attribute, valueLocal);
        
        // execute the stmt
        stmt.execute (action);
    } // performChangeTextData


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
     * @exception   NameAlreadyGivenException
     *              An object with this name already exists. This exception is
     *              only raised by some specific object types which don't allow
     *              more than one object with the same name.
     */
    protected void performChangeData (int operation)
        throws NoAccessException, NameAlreadyGivenException
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        int retVal = UtilConstants.QRY_OK;            // return value of query

/*
Sollte diese Prozedur nicht die id und oid zurueckliefern, falls ein insert
vorgenommen wurde, da diese ja erst von der datenbank erzeugt werden.
*/

        // check if the object is changeable:
        this.checkChangeable ();

        // set the name:
        this.setName ();

        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                this.procChange,
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // oid
        BOHelpers.addInParameter(sp, this.oid);
        // user id
        if (this.user != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
        } // else
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                        operation);
        // name
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.name);
        // validUntil
        sp.addInParameter (ParameterConstants.TYPE_DATE,
                        this.validUntil);
        // description
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.description);
        // showInNews (flags)
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
                        this.showInNews);

        // set all type specific paramaters:
        this.setSpecificChangeParameters (sp);

        // perform the function call:
        retVal = BOHelpers.performCallFunctionData(sp, this.env);

        try
        {
            // open db connection:
            action = this.getDBConnection ();

            // change the common data:
/* KR TODO: 20050228 Performance problem when setting the flags
 *          The value of the isDeletable flag is already set in the method
 *          setDeletable. It seems not to be necessary to set it again.
            performChangeCommonData (action);
*/

            // close db connection
            this.releaseDBConnection (action);

            // open new db connection:
            action = this.getDBConnection ();

            // change all typespecific data
            this.performChangeSpecificData (action);
        } // try
        catch (DBError e)
        {
            // show the message:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            // close db connection in every case
            this.releaseDBConnection (action);
        } // finally

        // check the rights
        if (retVal == UtilConstants.QRY_INSUFFICIENTRIGHTS) // access not allowed?
        {
            // raise no access exception
            NoAccessException error = new NoAccessException (
                MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
                    UtilExceptions.ML_E_NOACCESSEXCEPTION, new String[] {this.name}, env));

            throw error;
        } // if access not allowed
//******************************** HACK BEGIN ********************************
// Copied from User_01 because not only users with the same name are not allowed
// but also groups
        else if (retVal == UtilConstants.QRY_ALREADY_EXISTS_NAME) // access not allowed?
        {
            // raise name already exists exception
            NameAlreadyGivenException error =
                new NameAlreadyGivenException (MultilingualTextProvider
                     .getMessage (UtilExceptions.EXC_BUNDLE,
                          UtilExceptions.ML_E_NAMEALREADYGIVENEXCEPTION,
                          new String[] {this.name}, env));
            throw error;
        } // else if access not allowed
        else                          // access allowed
        {
            // room for some success statements
        } // else access allowed
//********************************* HACK END *********************************

        // room for some success statements
    } // performChangeData


    /**************************************************************************
     * Change the common data of a business object in the database. <BR/>
     */
    private final void performChangeCommonData ()
    {
        SQLAction action = null;        // the action object used to access the
                                        // database

        try
        {
            // open db connection:
            action = this.getDBConnection ();

            // change the common data:
            this.performChangeCommonData (action);
        } // try
        catch (DBError e)
        {
            // show the message:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            // close db connection in every case:
            this.releaseDBConnection (action);
        } // finally
    } // performChangeCommonData


    /**************************************************************************
     * Change the common data of a business object in the database. <BR/>
     * This method can be used if there exists already a db connection.
     *
     * @param   action      The action object associated with the connection.
     *
     * @exception    DBError
     *               This exception is always thrown, if there happens
     *               an error during accessing data.
     */
    private final void performChangeCommonData (SQLAction action)
        throws DBError
    {
        // create the SQL String to update the attributes:
        StringBuffer queryStr = new StringBuffer ("UPDATE ibs_Object");
        StringBuffer flagSetting = null;
        int flagsToSet = 0;
        int flagsToDelete = 0;

        // check if the object is deletable:
        if (this.p_isDeletable)
        {
            flagsToDelete += BOConstants.FLG_NOTDELETABLE;
        } // if
        else
        {
            flagsToSet += BOConstants.FLG_NOTDELETABLE;
        } // if

        // check if the object is changeable:
        if (this.p_isChangeable)
        {
            flagsToDelete += BOConstants.FLG_NOTCHANGEABLE;
        } // if
        else
        {
            flagsToSet += BOConstants.FLG_NOTCHANGEABLE;
        } // if
        
        // check if the object has an attached file:
        if (this.p_hasFileFlag)
        {
        	flagsToSet += BOConstants.FLG_HASFILE;
        } // if
        else
        {
        	flagsToDelete += BOConstants.FLG_HASFILE;
        } // else

        // check if there are some flags to delete::
        if (flagsToDelete != 0)
        {
            // set the flag values to 0:
            // (0x7FFFFFFF = 2147483647)
            flagSetting = SQLHelpers.getBitAnd ("flags",
                         " (2147483647 - " + flagsToDelete + ")");
        } // if
        else
        {
            flagSetting = new StringBuffer ("flags");
        } // else

        // check if there are some flags to set::
        if (flagsToSet != 0)
        {
            // set the flag values to 1:
            flagSetting = SQLHelpers.getBitOr (flagSetting.toString (),
                         "" + flagsToSet);
        } // if

        queryStr
            .append (" SET flags = ").append (flagSetting)
            .append (" WHERE oid = ").append (this.oid.toStringQu ());

        // execute the queryString, indicate that we're performing an
        // action query:
        action.execute (queryStr, true);
    } // performChangeCommonData


    /**************************************************************************
     * Set the deletable flag of the business object. <BR/>
     * Parameters:
     * <LI>param#0: isDeletable ("<CODE>true</CODE>" | "<CODE>false</CODE>")</LI>
     *
     * @param   params  The parameters.
     *
     * @return  The result.
     */
    public Vector<String> setDeletable (Vector<String> params)
    {
        String resultMsg = "";          // result message of method
        String resultCode = "0";        // result code of method
        Vector<String> result = new Vector<String> ();  // the result of the method
        boolean isDeletable = false;    // is the object deletable?
        String value = null;            // parameter value

        if (params.size () == 1)        // correct number of parameters?
        {
            value = params.elementAt (0);

            // check the value:
            if (value.equalsIgnoreCase ("true"))
            {
                isDeletable = true;
            } // if
            else if (!value.equalsIgnoreCase ("false"))
            {
                resultCode = "2";
                resultMsg =
                    "Wrong parameter value: \"" + value + "\"." +
                    " Expected \"true\" or \"false\".";
            } // else if

            if (resultCode.equals ("0")) // no error?
            {
                this.setDeletable (isDeletable, true);
            } // if no error
        } // if correct number of parameters
        else                            // wrong parameter number
        {
            resultCode = "1";
            resultMsg = "Wrong number of parameters.";
        } // else wrong parameter number

        // set the result vector:
        result.addElement (resultCode);
        result.addElement (resultMsg);

        return result;
    } // setDeletable


    /**************************************************************************
     * Set the deletable flag of the business object. <BR/>
     * The method also stores the value in the database if required.
     *
     * @param   isDeletable    <CODE>true</CODE> if te object shall be
     *                          deletable, <CODE>false</CODE> otherwise.
     * @param   isStoreData     Shall the data be stored in the database?
     */
    public void setDeletable (boolean isDeletable, boolean isStoreData)
    {
        this.p_isDeletable = isDeletable;

        if (isStoreData)
        {
            this.performChangeCommonData ();
        } // if
    } // setDeletable


    /**************************************************************************
     * Set the changeable flag of the business object. <BR/>
     * Parameters:
     * <LI>param#0: isDeletable ("<CODE>true</CODE>" | "<CODE>false</CODE>")</LI>
     *
     * @param   params  The parameters.
     *
     * @return  The result.
     */
    public Vector<String> setChangeable (Vector<String> params)
    {
        String resultMsg = "";          // result message of method
        String resultCode = "0";        // result code of method
        Vector<String> result = new Vector<String> ();  // the result of the method
        boolean isChangeable = false;   // is the object changeable?
        String value = null;            // parameter value

        if (params.size () == 1)        // correct number of parameters?
        {
            value = params.elementAt (0);

            // check the value:
            if (value.equalsIgnoreCase ("true"))
            {
                isChangeable = true;
            } // if
            else if (!value.equalsIgnoreCase ("false"))
            {
                resultCode = "2";
                resultMsg =
                    "Wrong parameter value: \"" + value + "\"." +
                    " Expected \"true\" or \"false\".";
            } // else if

            if (resultCode.equals ("0")) // no error?
            {
                this.setChangeable (isChangeable, true);
            } // if no error
        } // if correct number of parameters
        else                            // wrong parameter number
        {
            resultCode = "1";
            resultMsg = "Wrong number of parameters.";
        } // else wrong parameter number

        // set the result vector:
        result.addElement (resultCode);
        result.addElement (resultMsg);

        return result;
    } // setChangeable


    /**************************************************************************
     * Set the changeable flag of the business object. <BR/>
     * The method also stores the value in the database if required.
     *
     * @param   isChangeable    <CODE>true</CODE> if te object shall be
     *                          changeable, <CODE>false</CODE> otherwise.
     * @param   isStoreData     Shall the data be stored in the database?
     */
    public void setChangeable (boolean isChangeable, boolean isStoreData)
    {
        this.p_isChangeable = isChangeable;

        if (isStoreData)
        {
            this.performChangeCommonData ();
        } // if
    } // setChangeable

    /**************************************************************************
     * Set the hasFile flag of the business object. <BR/>
     * The method also stores the value in the database if required.
     *
     * @param   hasFile    		<CODE>true</CODE> if te object has an attached
     * 							file, <CODE>false</CODE> otherwise.
     * @param   isStoreData     Shall the data be stored in the database?
     */
    public void setFileFlag (boolean hasFile, boolean isStoreData)
    {
    	this.p_hasFileFlag = hasFile;
    	
    	if (isStoreData)
        {
            this.performChangeCommonData ();
        } // if
    } // setFileFlag
    
    /**************************************************************************
     * Change the state of a business object in the database. <BR/>
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
     */
    protected void performChangeState (int operation) throws NoAccessException
    {
        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                this.procChangeState,
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // oid
        BOHelpers.addInParameter(sp, this.oid);

        // user id
        if (this.user != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
        } // else
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, operation);
        // state
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.state);

        // perform the function call:
        BOHelpers.performCallFunctionData(sp, this.env);

        // room for some success statements
    } // performChangeState


    /**************************************************************************
     * Change the state of a business object in the database. <BR/>
     * <BR/>
     * This method tries to store the objects processState into the database.
     * During this operation a rights check is done, too.
     * If this is all right the object is stored and this method terminates
     * otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected void performChangeProcessState (int operation) throws NoAccessException
    {
        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                this.procChangeProcessState,
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // oid
        BOHelpers.addInParameter(sp, this.oid);

        // user id
        if (this.user != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
        } // else
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, operation);
        // processState
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.processState);

        // perform the function call:
        BOHelpers.performCallFunctionData(sp, this.env);

        // room for some success statements
    } // performChangeProcessState


    /**************************************************************************
     * Retrieve the processState of a business object in the database. <BR/>
     * <BR/>
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to retrieve
     *              the data
     */
    protected void performRetrieveProcessState () throws NoAccessException
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        int rowCount = 0;

        StringBuffer queryStr = new StringBuffer (" SELECT processState ")
            .append (" FROM ibs_Object")
            .append (" WHERE oid = ").append (this.oid.toStringQu ());

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);


            // empty resultset or error
            if (rowCount <= 0)
            {
                return;                 // terminate this method
            } // if

            // get tupl out of db
            if (!action.getEOF ())
            {
                this.processState = action.getInt ("processState");
            } // if
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally
    } // performRetrieveProcessState


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
    protected int setSpecificRetrieveParameters (StoredProcedure sp, Parameter[] params,
                                                 int lastIndex)
    {
        return lastIndex;               // return the current index
    } // setSpecificRetrieveParameters


    /**************************************************************************
     * Get the data for the additional (typespecific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * typespecific data from the retrieve data stored procedure.
     *
     * @param params    The array of parameters from the retrieve data stored
     *                  procedure.
     * @param lastIndex The index to the last element used in params thus far.
     */
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        // this method may be overwritten in sub classes
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Retrieve the type specific data that is not got from the stored
     * procedure. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * type specific data that cannot be got from the retrieve data stored
     * procedure.
     *
     * @param   action      The action object associated with the connection.
     *
     * @exception    DBError
     *               This exception is always thrown, if there happens
     *               an error during accessing data.
     */
    protected void performRetrieveSpecificData (SQLAction action) throws DBError
    {
        // this method may be overwritten in sub classes
    } // performRetrieveSpecificData


    /**************************************************************************
     * Retrieve an attribute of text data. This method is called from
     * performRetrieveSpecificData. <BR/>
     *
     * @param   action      The action object associated with the connection.
     * @param   table       The table from where the attribute is retrieved.
     * @param   attribute   The attribute to retrieve.
     * @param   extendedProcedure  The name of the extended procedure to call.
     *                      This procedure is called for an ORACLE DB.
     *
     * @return   The value of the text data field.
     *
     * @exception    DBError
     *               This exception is always thrown, if there happens
     *               an error during accessing data.
     */
    protected final String performRetrieveTextData (SQLAction action,
                                                    String table,
                                                    String attribute,
                                                    String extendedProcedure)
        throws DBError
    {
//trace ("KR performRetrieveTextData for " + table + "." + attribute + " begin");

        String value = " ";             // return value of this method
        StoredProcedure sp = new StoredProcedure (); // create procedure

        if (SQLConstants.DB_TYPE == SQLConstants.DB_ORACLE
//            ||
//            AppConstants.DB_TYPE == AppConstants.DB_DB2
        ) // if
                                        // oracle database?
        {
            // parameter definitions:
            // must be in correct sequence (like SQL stored procedure def.)

            // set stored procedure return type
            sp.setReturnType (StoredProcedureConstants.RETURN_VALUE);
            // set stored procedure name
            sp.setName (extendedProcedure);

            // input parameters:
            // oid
            BOHelpers.addInParameter(sp, this.oid);

            // output parameters:
            // content
            Parameter outParamContent = sp.addOutParameter (ParameterConstants.TYPE_TEXT);

//IOHelpers.showProcCall (this, sp, this.sess, this.env);

            // execute stored procedure
            // execute stored procedure - return value
            // gives right-information
            action.execStoredProc (sp);
            // end action
            action.end ();

            // set object properties - get them out of parameters
            value = SQLHelpers.dbToAscii (outParamContent.getValueString ());
        } //  if oracle database
        else                            // non-oracle database
        {
            // create the SQL String to select the content of a entry
            StringBuffer queryStr =
                new StringBuffer ("SELECT ").append (attribute)
                    .append (" FROM ").append (table)
                    .append (" WHERE oid = ").append (this.oid.toStringQu ());

            // execute the queryString, indicate that we're not performing an
            // action query:
            int rowCount = action.execute (queryStr, false);

            // check if the resultset is empty:
            if (rowCount > 0)           // any tuple found?
            {
                // everything ok - go on

                // get and set values for element:
                value = SQLHelpers.dbToAscii (action.getString (attribute));
            } // if any tuple found

            // the last tuple has been processed
            // end transaction:
            action.end ();

        } // else non-oracle database

        return value;                   // return the value
    } // performRetrieveTextData


    /**************************************************************************
     * Get a business object out of the database. <BR/>
     * <B>THIS METHOD IS A DUMMY WHICH MUST BE OVERWRITTEN IN SUB CLASSES!</B>
     * <BR/>
     * First this method tries to load the object from the database. During this
     * operation a rights check is done, too. If this is all right the object is
     * returned otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ObjectNotFoundException
     *              The required object was not found during search, i.e. there
     *              does not exist any object with the required oid.
     */
    protected void performRetrieveData (int operation)
        throws NoAccessException, ObjectNotFoundException
    {
        int retVal = UtilConstants.QRY_OK; // return value of query
        SQLAction action = null;        // the action object used to access the
                                        // database
        int i = 0;                      // number of actual parameter
        Parameter [] params = new Parameter[28 + this.specificRetrieveParameters]; // contains the parameters

        // check if we have the oid of an existing object or of a virtual
        // object:
        // (a virtual object is an object which has no representation in the
        // object repository and is just created for some specific temporary
        // use)
        if (!this.oid.isTemp ())        // existing object?
        {
            // create the stored procedure call:
            StoredProcedure sp = new StoredProcedure(
                    this.procRetrieve,
                    StoredProcedureConstants.RETURN_VALUE);

            // parameter definitions:
            // must be in right sequence (like SQL stored procedure def.)
            i = -1;                         // initialize parameter number

            // input parameters
            // oid
            params[++i] = BOHelpers.addInParameter(sp, this.oid);
            // user id
            if (this.user != null)
            {
                params[++i] = sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
            } // if
            else
            {
                params[++i] = sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
            } // else
            // operation
            params[++i] = sp.addInParameter (ParameterConstants.TYPE_INTEGER, operation);

            // output parameters:
            // state
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
            // tVersionId
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
            // typeName
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
            // name
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
            // containerId
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
            // containerName
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
            // containerKind
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
            // isLink
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
            // linkedObjectId
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
            // owner
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
            // ownerName
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
            // creationDate
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_DATE);
            // creator
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
            // creatorName
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
            // lastChanged
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_DATE);
            // changer
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
            // changerName
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
            // validUntil
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_DATE);
            // description
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
            //showInNews (flag)
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
            // checkedOut (flag)
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
            // checkOutDate
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_DATE);
            // checkOutUser
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
            // checkOutUserOid
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
            // checkOutUserName
            params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

            // set the typespecific retrieve data
            i = this.setSpecificRetrieveParameters (sp, params, i);

//trace ("KR before call function data");
            retVal = BOHelpers.performCallFunctionData (sp, this.env);
//trace ("KR after call function data");

            if (retVal == UtilConstants.QRY_OBJECTNOTFOUND) // object not found?
            {
                // raise no access exception
                ObjectNotFoundException error =
                    new ObjectNotFoundException (MultilingualTextProvider
                        .getMessage (UtilExceptions.EXC_BUNDLE,
                            UtilExceptions.ML_E_OBJECTNOTFOUNDEXCEPTION,
                            new String[] {"" + this.oid}, env));
                throw error;
            } // else if object not found

            // everything o.k., no query exception

            // set object properties - get them out of parameters
            i = 2;
            this.state = params[++i].getValueInteger ();
            this.type = params[++i].getValueInteger ();
            this.typeName = params[++i].getValueString ();
            this.name = params[++i].getValueString ();
            this.containerId = SQLHelpers.getSpOidParam (params[++i]);
            this.containerName = params[++i].getValueString ();
            this.containerKind = params[++i].getValueInteger ();
            this.isLink = params[++i].getValueBoolean ();
            this.linkedObjectId = SQLHelpers.getSpOidParam (params[++i]);
            this.owner = new User (params[++i].getValueInteger ());
            this.owner.fullname = params[++i].getValueString ();
            this.creationDate = params[++i].getValueDate ();
            this.creator = new User (params[++i].getValueInteger ());
            this.creator.fullname = params[++i].getValueString ();
            this.lastChanged = params[++i].getValueDate ();
            this.changer = new User (params[++i].getValueInteger ());
            this.changer.fullname = params[++i].getValueString ();
            this.validUntil = params[++i].getValueDate ();
            this.description = params[++i].getValueString ();
            this.showInNews = params[++i].getValueBoolean ();
            this.checkedOut = params[++i].getValueBoolean ();
            this.checkOutDate = params[++i].getValueDate ();
            this.checkOutUser = new User (params[++i].getValueInteger ());
            this.checkOutUserOid = SQLHelpers.getSpOidParam (params[++i]);
            this.checkOutUserName = params[++i].getValueString ();

            this.checkOutUser.fullname = this.checkOutUserName;


            // retrieve the common data:
            this.performRetrieveCommonData ();
/*
            // retrieve the 'forgotten' icon attribute of the business object.
            performRetrieveIconData ();
*/

            // get the type specific data:
//trace ("KR before getSpecificRetrieveParameters");
            this.getSpecificRetrieveParameters (params, i);
//trace ("KR after getSpecificRetrieveParameters");

            try
            {
                // retrieve the type specific data:

                // open db connection -  only workaround - db connection must
                // be handled somewhere else
                action = this.getDBConnection ();

                // retrieve all type specific data:
//trace ("KR before performRetrieveSpecificData");
                this.performRetrieveSpecificData (action);
//trace ("KR after performRetrieveSpecificData");
            } // try
            catch (DBError e)
            {
                // show the message:
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
            finally
            {
                // close db connection in every case - only workaround -
                // db connection must be handled somewhere else
                this.releaseDBConnection (action);
            } // finally
        } // if existing object
        else                            // virtual object
        {
            // set default values for the common properties to be set:
            this.state = States.ST_ACTIVE;
            this.type = this.oid.tVersionId;
//            this.typeName = "";
//            this.name = "";
//            this.containerId = null;
//            this.containerName = null;
//            this.containerKind = BOConstants.CONT_STANDARD;
//            this.isLink = false;
//            this.linkedObjectId = null;
//            this.owner = 0;
//            this.owner.fullname = "";
//            this.creationDate = new Date ();
//            this.creator = 0;
//            this.creator.fullname = "";
//            this.lastChanged = new Date ();
//            this.changer = 0;
//            this.changer.fullname = "";
//            this.validUntil = new Date ();
//            this.description = "";
//            this.showInNews = false;
            this.checkedOut = false;
            this.checkOutDate = null;
            this.checkOutUser = null;
            this.checkOutUserOid = null;
            this.checkOutUserName = null;

// HACK ...
            // All other (type-specific) properties must be set to default
            // values before this method is called!
            // Otherwise there must be a new method setDefaultPropertyValues.
// ... HACK
        } // else virtual object

//trace ("KR before setIcon");
        this.setIcon ();                // set the icon of this object
    } // performRetrieveData


    /**************************************************************************
     * Get the common data of a business object out of the database. <BR/>
     */
    private final void performRetrieveCommonData ()
    {
        SQLAction action = null;        // the action object for database access
        int flags = 0;                  // the object flags

        try
        {
            // get the object icon out of the database:
            StringBuffer queryStr;      // the query string
            int iconRetVal;             // the result value

            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();

            // select the icon attribute in the ibs_object table for the
            // business object.
            queryStr =
                new StringBuffer ("SELECT flags, icon")
                    .append (" FROM  ibs_Object")
                    .append (" WHERE oid = ").append (this.oid.toStringQu ());

            // perform the query:
            iconRetVal = action.execute (queryStr, false);

            // the result set must have exactly 1 row
            if (iconRetVal == 1)
            {
                // get the icon:
                flags = action.getInt ("flags");
                this.p_isDeletable =
                    (flags & BOConstants.FLG_NOTDELETABLE) == 0;
                this.p_isChangeable =
                    (flags & BOConstants.FLG_NOTCHANGEABLE) == 0;
                this.p_hasFileFlag = 
                	(flags & BOConstants.FLG_HASFILE) == BOConstants.FLG_HASFILE;
                this.icon = action.getString ("icon");
            } // if (iconRetVal == 1)

            // release the resources:
            action.end ();
        } // try
        catch (DBError e)
        {
            // show the message:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            // close db connection in every case - only workaround -
            // db connection must be handled somewhere else
            this.releaseDBConnection (action);
        } // finally
    } // performRetrieveCommonData


    /**************************************************************************
     * Get the icon name of a business object out of the database. <BR/>
     *
     * THIS IS IS A HACK!!
     * This attribute should be retrieved with all the other
     * basic attributes of a business object (see method performRetrieveData).
     * Adding this additional attribute to the stored procedure called in method
     * performRetrieveData makes it necessary to add this attribute to all
     * object specific stored procedures (about 50) too.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
/*
    final private void performRetrieveIconData ()
    {
        SQLAction action = null;        // the action object for database access

        try
        {
            // get the object icon out of the database:
            StringBuffer queryStr;      // the query string
            int iconRetVal;             // the result value

            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = getDBConnection ();

            // select the icon attribute in the ibs_object table for the
            // business object.
            queryStr =
                new StringBuffer ("SELECT icon")
                .append (" FROM  ibs_Object")
                .append (" WHERE oid = ").append (this.oid.toStringQu ());

            // perform the query:
            iconRetVal = action.execute (queryStr, false);

            // the result set must have exactly 1 row
            if (iconRetVal == 1)
            {
                // get the icon:
                this.icon = action.getString ("icon");
            } // if (iconRetVal == 1)

            // release the resources:
            action.end ();
        } // try
        catch (DBError e)
        {
            // show the message:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            // close db connection in every case - only workaround -
            // db connection must be handled somewhere else
            releaseDBConnection (action);
        } // finally
    } // performRetrieveIconData
*/


    /**************************************************************************
     * Get the oid of the object which represents a tab of the actual object
     * determined by the tab's name out of the database. <BR/>
     *
     * @return  The oid of the object which represents the tab.
     *          null if the tab object was not found.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ObjectNotFoundException
     *              The required object was not found during search, i.e. there
     *              does not exist any tab of the actual object with the
     *              required name.
     */
    protected OID performRetrieveMasterData ()
        throws NoAccessException, ObjectNotFoundException
    {
        int retVal = UtilConstants.QRY_OK;            // return value of query
        OID masterOid = null;              // oid of the tab object
        Parameter masterOidParam = null;

        // check if we have the oid of an existing object or of a virtual
        // object:
        // (a virtual object is an object which has no representation in the
        // object repository and is just created for some specific temporary
        // use)
        if (!this.oid.isTemp ())        // existing object?
        {
            // create the stored procedure call:
            StoredProcedure sp = new StoredProcedure(
                    this.procGetMaster,
                    StoredProcedureConstants.RETURN_VALUE);

            // parameter definitions:
            // must be in right sequence (like SQL stored procedure def.)
            // input parameters
            // oid
            BOHelpers.addInParameter(sp, this.oid);
            // user id
            if (this.user != null)
            {
                sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
            } // if
            else
            {
                sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
            } // else

            // output parameters:
            // masterOid
            masterOidParam = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);

            // perform the function call:
            retVal = BOHelpers.performCallFunctionData(sp, this.env);
        } // if existing object
        else                            // virtual object
        {
            // the master object could not be found:
            retVal = UtilConstants.QRY_OBJECTNOTFOUND;
        } // else virtual object

        if (retVal == UtilConstants.QRY_OBJECTNOTFOUND) // object not found?
        {
            // raise no access exception
            ObjectNotFoundException error =
                new ObjectNotFoundException (MultilingualTextProvider
                    .getMessage (UtilExceptions.EXC_BUNDLE,
                        UtilExceptions.ML_E_OBJECTNOTFOUNDEXCEPTION,
                        new String[] {"" + this.oid}, env));
            throw error;
        } // else if object not found
        else if (retVal == UtilConstants.QRY_OK)    // access allowed
        {
            // set object properties - get them out of parameters
            masterOid = SQLHelpers.getSpOidParam (masterOidParam);
        } // else if access allowed

        return masterOid;                  // return the oid of the tab object
    } // performRetrieveMasterData


    /**************************************************************************
     * Get the oid of the object which is in the hierarchy above the actual
     * object out of the database. <BR/>
     *
     * @return  The oid of the object which is above the actual object.
     *          null if there is no upper object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ObjectNotFoundException
     *              The required object was not found during search, i.e. there
     *              does not exist any object which is above the actual one
     *              within the hierarchy.
     */
    protected OID performRetrieveUpperData ()
        throws NoAccessException, ObjectNotFoundException
    {
        // call common function:
        return this.performRetrieveUpperData (this.oid);
    } // performRetrieveUpperData


    /**************************************************************************
     * Get the oid of the object which is in the hierarchy above the actual
     * object out of the database. <BR/>
     *
     * @param   oid     The oid of the object for which to search for the upper
     *                  object.
     *
     * @return  The oid of the object which is above the actual object.
     *          null if there is no upper object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ObjectNotFoundException
     *              The required object was not found during search, i.e. there
     *              does not exist any object which is above the actual one
     *              within the hierarchy.
     */
    protected OID performRetrieveUpperData (OID oid)
        throws NoAccessException, ObjectNotFoundException
    {
        int retVal = UtilConstants.QRY_OK;            // return value of query
        Parameter upperOidOutParam = null;  // out parameter for upper oid
        OID upperOid = null;            // oid of the upper object

        // check if we have the oid of an existing object or of a virtual
        // object:
        // (a virtual object is an object which has no representation in the
        // object repository and is just created for some specific temporary
        // use)
        if (!oid.isTemp ())             // existing object?
        {
            // create the stored procedure call:
            StoredProcedure sp = new StoredProcedure(
                    this.procGetUpper,
                    StoredProcedureConstants.RETURN_VALUE);

            // parameter definitions:
            // must be in right sequence (like SQL stored procedure def.)
            // input parameters
            // oid
            BOHelpers.addInParameter(sp, oid);

            // output parameters:
            // upperOid
            upperOidOutParam = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);

            // perform the function call:
            retVal = BOHelpers.performCallFunctionData(sp, this.env);
        } // if existing object
        else                            // virtual object
        {
            // the master object could not be found:
            retVal = UtilConstants.QRY_OBJECTNOTFOUND;
        } // else virtual object

        if (retVal == UtilConstants.QRY_OBJECTNOTFOUND) // object not found?
        {
            // raise no access exception
            ObjectNotFoundException error =
                new ObjectNotFoundException (MultilingualTextProvider
                    .getMessage (UtilExceptions.EXC_BUNDLE,
                        UtilExceptions.ML_E_OBJECTNOTFOUNDEXCEPTION, env));
            throw error;
        } // else if object not found
        else if (retVal == UtilConstants.QRY_OK) // access allowed
        {
            // set object properties - get them out of parameters
            upperOid = SQLHelpers.getSpOidParam (upperOidOutParam);
        } // else if access allowed

        return upperOid;                // return the oid of the upper object
    } // performRetrieveUpperData


    /**************************************************************************
     * Get the data for all tabs which are available for the actual user at the
     * current object. <BR/>
     *
     * @return  A tab list containing the information for all tabs or
     *          <CODE>null</CODE> if no tab was found.
     *          The list is ordered. This means that the elements are added to
     *          the list in a sequence corresponding to their priority from
     *          highest to lowest.
     */
    protected final TabContainer performRetrieveObjectTabData ()
    {
//trace (" --- BusinessObject.performRetrieveObjectTabData ANFANG --- ");
        SQLAction action = null;        // the action object used to access the
                                        // database
        int rowCount;                   // row counter
        OID oid;                        // the oid of the current tab object
        Tab tab = null;                 // the actual tab
        TabContainer tabList = null;    // list of all tabs
        OID noOid = null;               // oid of no valid object
        String[] disabledTabs = null;   // the tabs which are not allowed to be
                                        // displayed
        int countDisabledTabs = 0;      // number of disabled tabs
        int i = 0;                      // loop counter

        // get the tabs which shall not be displayed for the current object:
        disabledTabs = this.getDisabledTabs ();
        if (disabledTabs != null)
        {
            countDisabledTabs = disabledTabs.length;
        } // if

        // create oid of no valid object:
        noOid = OID.getEmptyOid ();

        // get the elements out of the database:
        // create the SQL String to select all tuples
        StringBuffer queryStr =
            new StringBuffer ("SELECT tabO.oid AS oid,")
                .append (" tabO.linkedObjectId AS linkedObjectId, t.id AS tabId,")
                .append (" t.kind AS kind, t.code AS tabCode, t.class AS tabClass,")
                .append (" t.fct AS tabFct, c.rights AS rights, t.domainId,")
                .append (SQLHelpers.getSelectCondition ("tv.defaultTab", "c.id", "1", "0"))
                .append (" AS isActive, 1 AS countElems")
                .append (" FROM  ibs_Tab t, ibs_ConsistsOf c, ibs_Object o,")
                .append (" (     SELECT  oid, linkedObjectId, containerId,")
                .append ("               consistsOfId, state")
                .append ("       FROM    ibs_Object")
                .append ("       WHERE   containerKind = ").append (BOConstants.CONT_PARTOF)
                .append ("           AND containerID = ").append (this.oid.toStringQu ())
                .append ("  UNION");
        // BB20071126: a sideeffect in SQLSERVER2005 has been found.
        // The special UNION in the QUERY produces OIDs that are cut when
        // the OID ends with "0". I.e. an 0x12345678900 will result in 0x123456789
        // This only happends when an OID column is united with an empty
        // OID string (0x00000000000000).
        // SQLServer2000 does NOT show this behaviour!
        // We must force a conversion to an OID in order to fix the problem.
        queryStr.append ("       SELECT  ").append (noOid.toStringQu (true))
                                           .append (" AS oid,")
                .append ("               ").append (noOid.toStringQu (true))
                                           .append (" AS linkedObjectId,")
                .append ("               ").append (noOid.toStringQu (true))
                                           .append (" AS containerId,")
                .append ("               0 AS consistsOfId,")
                .append (States.ST_ACTIVE).append (" AS state")
                .append (SQLHelpers.getDummyTable ("FROM "))
                .append (") tabO, ibs_TVersion tv")
                .append (" WHERE o.oid = ").append (this.oid.toStringQu ())
                .append (" AND o.tVersionId = c.tVersionId")
                .append (" AND t.id = c.tabId")
                .append (" AND ( (t.kind = ").append (TabConstants.TK_OBJECT)
                .append ("       AND c.id = tabO.consistsOfId")
                .append ("       AND o.oid = tabO.containerId)")
                .append ("     OR")
                .append ("       (t.kind <> ").append (TabConstants.TK_OBJECT)
                .append ("       AND tabO.oid = ").append (noOid.toStringQu ()).append (")")
                .append (")")
                .append (" AND o.tVersionId = tv.id")
                .append (" AND tabO.state = ").append (this.state)
//                .append (SQLHelpers.getStringCheckRights (Operations.OP_VIEW))
                .append (" ORDER BY c.priority ").append (BOConstants.ORDER_DESC).append (",")
                .append (" t.id ").append (BOConstants.ORDER_ASC);

        // start index in ids and values - array
//        int start = 0;

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();

            // perform the query:
            rowCount = action.execute (queryStr, false);

            // check if the resultset is empty:
            if (rowCount > 0)      // at least one tuple found?
            {
                // initialize the tab list:
                tabList = new TabContainer ();

                // get tuples out of db:
                while (!action.getEOF ())
                {
                    // get the tab info out of the query result:
                    int id = action.getInt ("tabId");
                    int kind = action.getInt ("kind");

                    // evaluate the tab kind:
                    switch (kind)
                    {
                        case TabConstants.TK_LINK: // link tab
                            oid = SQLHelpers.getQuOidValue (action, "linkedObjectId");
                            break;
                        case TabConstants.TK_OBJECT: // object tab
                            oid = SQLHelpers.getQuOidValue (action, "oid");
                            break;
                        default:            // any other kind of tab
                            oid = this.oid; // no oid needed
                            break;
                    } // switch kind

                    // get the rights which are necessary for a user to able to
                    // see the tab:
                    int rights = action.getInt ("rights");
                    // get the number of elements within the tab:
                    int countElems = action.getInt ("countElems");

                    // set fct of tab
                    int fct = action.getInt ("tabFct");

                    // set class of tab
                    String className = action.getString ("tabClass");

                    // set code of tab
                    String code = action.getString ("tabCode");

                    int domainId = action.getInt ("domainId");

                    // check if the tab code is part of the disabled tabs list:
                    for (i = 0; i < countDisabledTabs &&
                        !disabledTabs[i].equals (code); i++)
                    {
                        // nothing to do
                    } // for i

                    // check if we found the tab:
                    if (i >= countDisabledTabs) // tab is not disabled?
                    {
                        tab = new Tab (id, domainId, code, kind, fct,
                                       oid, rights, countElems, className);

                        // add the tab to the tab list:
                        tabList.add (tab);

                        // set active tab:
                        if (action.getBoolean ("isActive")) // actual tab is active?
                        {
                            tabList.setActiveTab (tab);
                        } // if actual tab is active
                    } // if tab is not disabled

                    // step one tuple ahead for the next loop:
                    action.next ();
                } // while

                // the last tuple has been processed
                // end transaction:
                action.end ();
            } // if at least one tuple found
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        catch (ListException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            try
            {
                // ensure that the action is not longer used:
                action.end ();
            } // try
            catch (DBError e)
            {
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch

            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

//trace (" --- BusinessObject.performRetrieveObjectTabData ENDE --- ");
        // return the constructed tab list:
        return tabList;
    } // performRetrieveObjectTabData


    /**************************************************************************
     * Get a selectionList out of the database. <BR/>
     * A selectionList (id,name) will be retrieved for a spefic type out of
     * ibs_object. First this method tries to load the object from the database.
     * During this operation a rights check is done, too. <BR/>
     *
     * @param   tVersionId  TypeVersion of objects to be in selection list.
     * @param   isNullable  Is there a placeholder for no object within the
     *                      selection list?. <BR/>
     *                      true:   first row in selection list will be
     *                              placeholder for no object. <BR/>
     *                      false:  no placeholder for no object will be
     *                              included, except the selection list would
     *                              otherwise be empty (this behaviour is
     *                              implemented only cause I don't know how
     *                              showFormProperty handles an empty
     *                              selection list).
     *
     * @return  The resulting selection list.
     */
    protected SelectionList performRetrieveSelectionListData (int tVersionId,
                                                              boolean isNullable)
    {
//trace (" --- BusinessObject.performRetrieveSelectectionListData ANFANG --- ");
        SQLAction action = null;        // the action object used to access the
                                        // database
        // resulting SelectionList
        SelectionList selList = new SelectionList ();
        int rowCount;                   // row counter

        Vector<String> ids = new Vector<String> (); // initialize elements vector
        Vector<String> values = new Vector<String> (); // initialize elements vector

        // get the elements out of the database:
        // create the SQL String to select all tuples
        StringBuffer queryStr =
            new StringBuffer ("SELECT distinct")
                .append (" oid, name")
                .append (" FROM ").append (this.viewSelectionList)
                .append (" WHERE   userId = ").append (this.user.id)
                .append (SQLHelpers.getStringCheckRights (Operations.OP_VIEW))
                .append ("    AND tVersionId=").append (tVersionId)
                .append (" ORDER BY name ").append (BOConstants.ORDER_ASC);

        // start index in ids and values - array
        int start = 0;

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();

            rowCount = action.execute (queryStr, false);

            // empty resultset?
            if (rowCount == 0)
            {
                selList.ids = new String[1];
                selList.values = new String[1];
                selList.ids[0] = "";
                selList.values[0] = "";
            } // if
            if (rowCount > 0)
            {
                // get tuples out of db
                while (!action.getEOF ())
                {
                    // get and set values for element
                    // with ORACLE u get oids in the form of
                    // -> '01017101000002BE' and with MSSQL u get oids in the
                    // form of -> '0x01017101000002BE' so u can't use a
                    // getString - u have to use one form on both systems, and
                    // the oid-transformation is automatically done in class
                    // OID.
                    ids.addElement (SQLHelpers.getQuOidValue (action, "oid").toString ());
                    values.addElement (action.getString ("name"));

                    // step one tuple ahead for the next loop
                    action.next ();

                } // while
                // the last tuple has been processed
                // end transaction
                action.end ();

                // set real rowCount of Resultset
                rowCount = ids.size ();

                if (isNullable)
                {
                    // start at 1 cause 0 will be used for the NOOBJECT
                    start = 1;
                } // if

                // create return array of right size
                selList.ids = new String [ids.size () + start];
                selList.values = new String [ids.size () + start];

                // declare rowcindex for copy into Stringarrays
                int i = 0;

                while (i < rowCount)
                {
                    selList.ids [i + start] = ids.elementAt (i);
                    selList.values [i + start] = values.elementAt (i);
                    i++;
                } // while
            } //if rowcount
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        // set empty value for nullable selection list
        if (isNullable)
        {
            selList.ids[0] = OID.EMPTYOID;
            selList.values[0] = "";
        } //if

//trace (" --- BusinessObject.performRetrieveSelectectionListData ENDE --- ");
        return selList;
    } // performRetrieveSelectionListData


    /**************************************************************************
     * Get a selectionList out of the database. <BR/>
     * A selectionList (id,name) will be retrieved for a spefic type out of
     * ibs_object. First this method tries to load the object from the database.
     * During this operation a rights check is done, too. <BR/>
     *
     * @param   isNullable  Is there a placeholder for no object within the
     *                      selection list?. <BR/>
     *                      true:   first row in selection list will be
     *                              placeholder for no object. <BR/>
     *                      false:  no placeholder for no object will be
     *                              included, except the selection list would
     *                              otherwise be empty (this behaviour is
     *                              implemented only cause I don't know how
     *                              showFormProperty handles an empty
     *                              selection list).
     * @param   queryStr    querystring for selectionlist. SELECT query *MUST*
     *                      select oid and name.
     *
     * @return  The resulting selection list.
     */
    protected SelectionList performRetrieveSelectionListDataQuery (
                                                                   boolean isNullable,
                                                                   String queryStr)
    {
        return this.performRetrieveSelectionListDataQuery (isNullable,
            new StringBuffer ().append (queryStr), "");
    } // performRetrieveSelectionListDataQuery


    /**************************************************************************
     * Get a selectionList out of the database. <BR/>
     * A selectionList (id,name) will be retrieved for a spefic type out of
     * ibs_object. First this method tries to load the object from the database.
     * During this operation a rights check is done, too. <BR/>
     *
     * @param   isNullable  Is there a placeholder for no object within the
     *                      selection list?. <BR/>
     *                      true:   first row in selection list will be
     *                              placeholder for no object. <BR/>
     *                      false:  no placeholder for no object will be
     *                              included, except the selection list would
     *                              otherwise be empty (this behaviour is
     *                              implemented only cause I don't know how
     *                              showFormProperty handles an empty
     *                              selection list).
     * @param   queryStr    querystring for selectionlist. SELECT query *MUST*
     *                      select oid and name.
     * @param   nullString  String to be shown when nullable
     *
     * @return  The resulting selection list.
     */
    protected SelectionList performRetrieveSelectionListDataQuery (
                                                                   boolean isNullable,
                                                                   StringBuffer queryStr,
                                                                   String nullString)
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        SelectionList selList = new SelectionList (); // resulting SelectionList
        int rowCount;                   // row counter
        Vector<String> ids = new Vector<String> (10, 10); // initialize elements vector
        Vector<String> values = new Vector<String> (10, 10); // initialize elements vector
        int start = 0;                  // start index in ids and values array

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();

            rowCount = action.execute (queryStr, false);

            // empty resultset?
            if (rowCount == 0)
            {
                selList.ids = new String[1];
                selList.values = new String[1];
                selList.ids[0] = "";
                selList.values[0] = "";
            } // if
            if (rowCount > 0)
            {
                // get tuples out of db
                while (!action.getEOF ())
                {
                    // get and set values for element
                    ids.addElement (action.getString ("oid"));
                    values.addElement (action.getString ("name"));

                    // step one tuple ahead for the next loop
                    action.next ();

                } // while
                // the last tuple has been processed
                // end transaction
                action.end ();

                // set real rowCount of Resultset
                rowCount = ids.size ();

                if (isNullable)
                {
                    // start at 1 cause 0 will be used for the NOOBJECT
                    start = 1;
                } // if

                // create return array of right size
                selList.ids = new String [ids.size () + start];
                selList.values = new String [ids.size () + start];

                // declare rowcindex for copy into Stringarrays
                int i = 0;

                while (i < rowCount)
                {
                    selList.ids [i + start] = ids.elementAt (i);
                    selList.values [i + start] = values.elementAt (i);
                    i++;
                } // while
            } //if rowcount
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        // set empty value for nullable selection list
        if (isNullable)
        {
            selList.ids[0] = OID.EMPTYOID;
            selList.values[0] = nullString;
        } //if

        return selList;
    } // performRetrieveSelectionListDataQuery


    /**************************************************************************
     * Check if there are any references on the actual object and its
     * subsequent objects. <BR/>
     *
     * @param   recursive   Shall the check be done recursive?
     *                      true:   All objects which are below the actual one
     *                              are checked. <BR/>
     *                      false:  Just the actual object is checked.
     *
     * @return  The number of references or <CODE>0</CODE> if there are no
     *          references found.
     */
    protected final int checkReferences (boolean recursive)
    {
//trace (" --- BusinessObject.checkReferences ANFANG --- ");
        SQLAction action = null;        // the action object used to access the
                                        // database
        int rowCount = 0;               // row counter
        int referenceCount = 0;         // the number of references
        StringBuffer queryStr = null;         // the query to be performed

        // get the elements out of the database:
        // create the SQL String to select all tuples
        queryStr =
            new StringBuffer ("SELECT o.name, ref.referencingOid,")
                .append (" ref.fieldName, ref.referencedOid, ref.kind,")
                .append (" refO.name AS refName")
                .append (" FROM  ibs_Object o, ibs_Reference ref, ibs_Object refO");
        if (recursive)                  // check shall be done recursive?
        {
            queryStr.append (", ibs_Object actO");
        } // if check shall be done recursive
        queryStr
            .append (" WHERE o.oid = ref.referencingOid")
            .append (" AND ref.referencedOid = refO.oid");
        if (recursive)                  // check shall be done recursive?
        {
            queryStr
                .append (" AND actO.oid = ").append (this.oid.toStringQu ())
                .append (" AND ").append (SQLHelpers.getQueryConditionAttribute (
                    "refO.posNoPath", SQLConstants.MATCH_STARTSWITH,
                    "actO.posNoPath", true));
        } // if check shall be done recursive
        else                                    // no recursive check
        {
            queryStr.append (" AND refO.oid = ").append (this.oid.toStringQu ());
        } // else no recursive check
        queryStr
            .append (" AND o.state = ").append (States.ST_ACTIVE)
            .append (" AND refO.state = ").append (States.ST_ACTIVE)
            .append (" AND ref.kind IN (").
                append (BOConstants.REF_FIELDREF).append (",").
                append (BOConstants.REF_VALUEDOMAIN).append (",").
                append (BOConstants.REF_MULTIPLE).append (")")
            .append (" ORDER BY o.name ").append (BOConstants.ORDER_ASC)
            .append (", ref.fieldName ").append (BOConstants.ORDER_ASC);

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            // execute the query:
            rowCount = action.execute (queryStr.toString (), false);

            // check if the resultset is empty:
            if (rowCount > 0)      // at least one tuple found?
            {
                // get tuples out of db:
                while (!action.getEOF ())
                {
                    // increment the number of references:
                    referenceCount++;
                    // step one tuple ahead for the next loop:
                    action.next ();
                } // while
                // the last tuple has been processed
                // end transaction
                action.end ();
            } // if at least one tuple found
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (e, this.app, this.sess, this.env);
        } // catch
        finally
        {
            try
            {
                // ensure that the action is not longer used:
                action.end ();
            } // try
            catch (DBError e)
            {
                // nothing to do
            } // catch

            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

//trace (" --- BusinessObject.checkReferences ENDE --- ");
        // return the number of found references:
        return referenceCount;
    } // checkReferences


    /**************************************************************************
     * Move a business object in the Database. <BR/>
     * First this method tries to move the object from the database.
     * During this operation a rights check is done, too.
     * If this is all right the object is moved and this method terminates
     * otherwise an exception is raised. <BR/>
     *
     * @param   targetId    The oid of the target object where to move the
     *                      actual object to.
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    public void performMoveData (OID targetId, int operation)
        throws NoAccessException
    {
        int retVal = UtilConstants.QRY_OK;            // return value of query

        // check if we have the oid of an existing object or of a virtual
        // object:
        // (a virtual object is an object which has no representation in the
        // object repository and is just created for some specific temporary
        // use)
        if (!this.oid.isTemp () && !targetId.isTemp ()) // existing objects?
        {
            // create the stored procedure call:
            StoredProcedure sp = new StoredProcedure(
                    this.procMove,
                    StoredProcedureConstants.RETURN_VALUE);

            // parameter definitions:
            // must be in right sequence (like SQL stored procedure def.)
            // input parameters
            // oid
            BOHelpers.addInParameter(sp, this.oid);
            // user id
            if (this.user != null)
            {
                sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
            } // if
            else
            {
                sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
            } // else
            // operation
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, operation);
            // targetId
            BOHelpers.addInParameter(sp, targetId);

            // perform the function call:
            BOHelpers.performCallFunctionData(sp, this.env);
        } // if existing object
        else                            // virtual object
        {
            // the operation failed:
            retVal = BOConstants.ORY_CUT_INSERT_FAIL;
        } // else virtual object

        // check the return value:
        if (retVal == BOConstants.ORY_CUT_INSERT_FAIL) // cut_insert_Fail occurred?
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_CUT_INSERT_FAIL, this.env),
                this.app, this.sess, this.env);
        } // if cut_insert_Fail occurred
        else                          // access allowed
        {
        	 // move the HD-Files too
        	this.performMoveHDFiles (this.oid, true, true);
        } // else access allowed
    } // performMoveData


    /**************************************************************************
     * Copy the type specific data that is not copied with the standard
     * mechanism. <BR/>
     * This method must be overwritten by all subclasses that have to copy
     * type specific data that cannot be copied through the standard copy
     * mechanism.
     * The standard mechanism copies all BusinessObject data and the attached
     * files, this means, that the xmldata.xml files are already copied before
     * the method <CODE>performCopySpecificData</CODE> is called.
     *
     * @param   rootOid     ObjectId of the Object which is the copy of the
     *                      actual one.
     * @param   action      The action object associated with the connection.
     *
     * @exception    DBError
     *               This exception is always thrown, if there happens
     *               an error during accessing data.
     */
    protected void performCopySpecificData (OID rootOid, SQLAction action)
        throws DBError
    {
        // this method may be overwritten in sub classes
    } // performCopySpecificData


    /**************************************************************************
     * Copy a business object in the Database. <BR/>
     * During this operation a rights check is done, too.
     * If this is all right the object is copied and this method terminates
     * otherwise an exception is raised. <BR/>
     * By default all objects below are copied, too. So we are making a copy
     * of the complete object tree.
     *
     * @param   targetId    The oid of the target object where to copy the
     *                      actual object to.
     * @param   operation   Operation to be performed with the object.
     *
     * @return  The oid of the new object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected OID performCopyData (OID targetId, int operation)
        throws NoAccessException
    {
        // call common method:
        return this.performCopyData (targetId, operation, true);
    } // performCopyData

    /**************************************************************************
     * Copy a business object in the Database. <BR/>
     * During this operation a rights check is done, too.
     * If this is all right the object is copied and this method terminates
     * otherwise an exception is raised. <BR/>
     * If isRecursive is set to <CODE>true</CODE> the whole tree is copied, i.e.
     * the object itself and all objects below. Otherwise only the object itself
     * is copied (no other objects, no tabs).
     *
     * @param   targetId    The oid of the target object where to copy the
     *                      actual object to.
     * @param   operation   Operation to be performed with the object.
     * @param   isRecursive Shall the copy be done recursively?
     *                      This means shall the objects below also be copied.
     *
     * @return  The oid of the new object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected OID performCopyData (OID targetId, int operation,
                                   boolean isRecursive)
        throws NoAccessException
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        int retVal = UtilConstants.QRY_OK; // return value of query
        OID newRootOid = null;          // oid of the newly created object
        Parameter oidParam = null;      // the oid parameter


        // check if we have the oid of an existing object or of a virtual
        // object:
        // (a virtual object is an object which has no representation in the
        // object repository and is just created for some specific temporary
        // use)
        if (!this.oid.isTemp () && !targetId.isTemp ()) // existing objects?
        {
            // create the stored procedure call:
            StoredProcedure sp = new StoredProcedure(
                    this.procCopy,
                    StoredProcedureConstants.RETURN_VALUE);
            
            // parameter definitions:
            // must be in right sequence (like SQL stored procedure def.)
            // input parameters:
            // oid
            BOHelpers.addInParameter(sp, this.oid);

            // user id
            if (this.user != null)
            {
                sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
            } // if
            else
            {
                sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
            } // else
            // operation
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, operation);
            // targetId
            BOHelpers.addInParameter(sp, targetId);

            // isRecursive
            sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, isRecursive);

            // output parameters:
            // newRootOid
            oidParam = sp.addOutParameter (ParameterConstants.TYPE_STRING);

            // perform the function call:
            retVal = BOHelpers.performCallFunctionData(sp, this.env);
        } // if existing object
        else                            // virtual object
        {
            // the operation failed:
            retVal = BOConstants.ORY_CUT_INSERT_FAIL;
        } // else virtual object

        // check the return value:
        if (retVal == BOConstants.ORY_CUT_INSERT_FAIL) // cut_insert_Fail occurred?
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_CUT_INSERT_FAIL, this.env),
                this.app, this.sess, this.env);
        } // if cut_insert_Fail occurred
        else                          // access allowed
        {
    		try
    		{
	        	// set the new oid
	        	newRootOid = new OID (oidParam.getValueString ());
	
	    		// copy HD files too, if this object or one of its children has some
	        	if (this.hasObjectTreeFiles ())
	        	{
	        		this.performMoveHDFiles (newRootOid, false, isRecursive);
	        	} // if

                try
                {
                    // open db connection -  only workaround - db connection must
                    // be handled somewhere else
                    action = this.getDBConnection ();

                    // perform specific copy operations:
                    this.performCopySpecificData (newRootOid, action);
                } // try
                catch (DBError e)
                {
                    // show the message:
                    IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
                } // catch
                finally
                {
                    // close db connection in every case - only workaround -
                    // db connection must be handled somewhere else
                    this.releaseDBConnection (action);
                } // finally
            } // try
            catch (IncorrectOidException e)
            {
                newRootOid = null;
            } // catch
        } // else access allowed

        return newRootOid;              // return the oid of the new object
    } // performCopyData


    /**************************************************************************
     * Check if this object has child objects with attached files. <BR/>
     * The method searches in the ibs_Object table if one or more of the child
     * object have attached files.
     * 
     * @return	<CODE>true</CODE> if there are child objects with an attached
     * 			file, <CODE>false</CODE> otherwise.
     */
	private boolean hasObjectTreeFiles ()
	{
		SQLAction action = null;
		int rowCount = 0;
		
		StringBuffer query = new StringBuffer ().
			  append ("SELECT 1").
			  append (" FROM ibs_Object co, ibs_object po").
			  append (" WHERE po.oid = ").append (this.oid.toStringQu ()).
			  append (" AND co.posnopath LIKE po.posnopath + '%'").
			  append (" AND (co.flags & ").
			  append(BOConstants.FLG_HASFILE).
			  append(") = ").
			  append(BOConstants.FLG_HASFILE);
		  
		try
		{
			// open db connection:
			action = DBConnector.getDBConnection ();
			
			// perform the query:
			rowCount = action.execute (query, false);
			 
			// end the retrieve action
			action.end ();
		} // try

		catch (DBError e)
		{
			// show the message:
		    IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
		} // catch DBError

		finally
		{
			// release db connection:
			this.releaseDBConnection (action);
		} // finally
		
		// If the query returns a result row, we copy objects with attached files
		if (rowCount > 0)      
		{
			return true;
		} // if there was a tuple found
		else
		{
			return false;
		} // else
	}


    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performDeleteData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the delete data stored procedure.
     *
     * @param params     The stored procedure to add the delete parameters to.
     *
     * @param operation  Operation to be performed with the object.
     */
    protected void setSpecificDeleteParameters (StoredProcedure sp, int operation)
    {
        // set the specific parameters:
        // input parameters:
        // oid
        BOHelpers.addInParameter(sp, this.oid);
        // user id
        if (this.user != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
        } // else
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, operation);
    } // setSpecificDeleteParameters


    /**************************************************************************
     * Delete a business object from the database. <BR/>
     * This method is used as a wrapper to call the
     * performDeleteData method while setting the operation manually. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ObjectNotAffectedException
     *              The operation could not be performed on all required
     *              objects.
     * @exception   DependentObjectExistsException
     *              The object could not be deleted because there are still
     *              objects which are refer to this object.
     */
    public void performDelete (int operation)
        throws NoAccessException, ObjectNotAffectedException,
               DependentObjectExistsException
    {
        // check if the object should be deleted without permission check
        if (operation == Operations.OP_NONE)
        {
            this.performForceDeleteData ();
        } // if (operation != Operations.OP_NONE)
        else    // delete with permission check
        {
            this.performDeleteData (operation);
        } // else delete with permission check
    } // performDelete


    /**************************************************************************
     * Delete a business object from the database. <BR/>
     * First this method tries to delete the object from the database.
     * During this operation a rights check is done, too.
     * If this is all right the object is deleted and this method terminates
     * otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ObjectNotAffectedException
     *              The operation could not be performed on all required
     *              objects.
     * @exception   DependentObjectExistsException
     *              The object could not be deleted because there are still
     *              objects which are refer to this object.
     */
    protected void performDeleteData (int operation)
        throws NoAccessException, ObjectNotAffectedException,
               DependentObjectExistsException
    {
        int retVal = UtilConstants.QRY_OK; // return value of query
        int userId = 0;                 // the id of the user

        // check if the object is deletable:
        this.checkDeletable ();

        // get the user id:
        if (this.user != null)          // user exists?
        {
            // set the user id:
            userId = this.user.id;
        } // if user exists

        try
        {
            // check if the user has the necessary rights to perform the
            // operation:
            // (the corresponding exceptions are raised further)
            this.performCheckRightsRecursive (userId, operation);

            // now we know that the user has the rights.

            // check if we have the oid of an existing object or of a virtual
            // object:
            // (a virtual object is an object which has no representation in the
            // object repository and is just created for some specific temporary
            // use)
            // The rights object is an exception to this rule because it can
            // perform the delete operation if it does not exist physically.
            if (!this.oid.isTemp () ||
                this.oid.type == this.getTypeCache ().getTypeId (TypeConstants.TC_Rights))
            // existing object?
            {
                // create the stored procedure call:
                StoredProcedure sp = new StoredProcedure(
                        (this.deleteRecursive) ?
                                this.procDeleteRec :         // call recursive procedure
                                this.procDelete,             // call non-recursive procedure
                        StoredProcedureConstants.RETURN_VALUE);

                // set the parameters for the delete operation:
                this.setSpecificDeleteParameters (sp, operation);

                // perform the function call:
                retVal = BOHelpers.performCallFunctionData(sp, this.env);
            } // if existing object
            else                        // virtual object
            {
                // the operation failed:
                retVal = UtilConstants.QRY_NOTALLAFFECTED;
            } // else virtual object

            if (retVal == UtilConstants.QRY_NOTALLAFFECTED)
            {
                // raise object (s) not affected exception
                ObjectNotAffectedException error = new ObjectNotAffectedException (
                    MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
                        UtilExceptions.ML_E_OBJECTNOTAFFECTEDEXCEPTION, env));
                throw error;
            } // else if QRY_NOTALLAFFECTED
            else if (retVal == UtilConstants.QRY_DEPENDENT_OBJECT_EXISTS)
            {
                // raise dependent object (s) exists exception
                DependentObjectExistsException error = new DependentObjectExistsException (
                    MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
                        UtilExceptions.ML_E_DEPENDENTOBJECTEXISTS, env));
                throw error;
            } // else if QRY_DEPENDENT_OBJECT_EXISTS
            else                        // access allowed
            {
                // room for some success statements
            } // else access allowed
        } // try
        catch (ObjectNotAffectedException e)
        {
            // raise object (s) not affected exception
            ObjectNotAffectedException error = new ObjectNotAffectedException (
                MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
                    UtilExceptions.ML_E_NOTALLDELETEABLE, env));
            error.addError (e.getError ());
            throw error;
        } // catch ObjectNotAffectedException
    } // performDeleteData


     /*************************************************************************
     * Delete a business object from the database. <BR/>
     * First this method tries to delete the object from the database.
     * During this operation a rights check is done, too.
     * If this is all right the object is deleted and this method terminates
     * otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ObjectNotAffectedException
     *              The operation could not be performed on all required
     *              objects.
     * @exception   DependentObjectExistsException
     *              The object could not be deleted because there are still
     *              objects which are refer to this object.
     */
    protected void performUnDeleteData (int operation)
        throws NoAccessException, ObjectNotAffectedException,
               DependentObjectExistsException
    {
        int retVal = UtilConstants.QRY_OK; // return value of query

        // check if we have the oid of an existing object or of a virtual
        // object:
        // (a virtual object is an object which has no representation in the
        // object repository and is just created for some specific temporary
        // use)
        // The rights object is an exception to this rule because it can
        // perform the delete operation if it does not exist physically.
        if (!this.oid.isTemp () ||
            this.oid.type == this.getTypeCache ().getTypeId (TypeConstants.TC_Rights))
                                        // existing object?
        {
            // create the stored procedure call:
            StoredProcedure sp = new StoredProcedure(
                    (this.deleteRecursive) ?
                            this.procUnDeleteRec :         // call recursive procedure
                            this.procUnDelete,             // call non-recursive procedure
                    StoredProcedureConstants.RETURN_VALUE);

            // set the parameters for the delete operation:
            this.setSpecificDeleteParameters (sp, operation);

            // perform the function call:
            retVal = BOHelpers.performCallFunctionData(sp, this.env);
        } // if existing object
        else                            // virtual object
        {
            // the operation failed:
            retVal = UtilConstants.QRY_NOTALLAFFECTED;
        } // else virtual object

        if (retVal == UtilConstants.QRY_NOTALLAFFECTED)
        {
            // raise object (s) not affected exception
            ObjectNotAffectedException error = new ObjectNotAffectedException (
                MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
                    UtilExceptions.ML_E_OBJECTNOTAFFECTEDEXCEPTION, env));
            throw error;
        } // else if QRY_NOTALLAFFECTED
        else if (retVal == UtilConstants.QRY_DEPENDENT_OBJECT_EXISTS)
        {
            // raise dependent object (s) exists exception
            DependentObjectExistsException error = new DependentObjectExistsException (
                MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
                    UtilExceptions.ML_E_DEPENDENTOBJECTEXISTS, env));
            throw error;
        } // else if QRY_DEPENDENT_OBJECT_EXISTS
        else                          // access allowed
        {
            // room for some success statements
        } // else access allowed
    } // performUnDeleteData


    /**************************************************************************
     * Delete all reference to the business object from the database. <BR/>
     * Attention: no rights check will be done!
     *
     * @param   operation   Operation to be performed with the object.
     */
    public void deleteAllRefs (int operation)
    {
        // check if we have the oid of an existing object or of a virtual
        // object:
        // (a virtual object is an object which has no representation in the
        // object repository and is just created for some specific temporary
        // use)
        if (!this.oid.isTemp ())        // existing object?
        {
            // create the stored procedure call:
            StoredProcedure sp = new StoredProcedure(
                    this.procDeleteAllRefs,
                    StoredProcedureConstants.RETURN_VALUE);
            
            // parameter definitions:
            // must be in right sequence (like SQL stored procedure def.)
            // input parameters
            // oid
            BOHelpers.addInParameter (sp, this.oid);
            // user id
            if (this.user != null)
            {
                sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
            } // if
            else
            {
                sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
            } // else
            // operation
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, operation);

            // no out parameters

            // perform the procedure call:
            try
            {
                BOHelpers.performCallFunctionData(sp, this.env);
            } // try
            catch (NoAccessException e)
            {
                // ignore exception because no user-rights check
                // is done in this function
            } // catch
        } // if existing object
        else                            // virtual object
        {
            // nothing to do; there are no references
        } // else virtual object

        // exit
        return;
    } // performDeleteAllRefs


    /**************************************************************************
     * Get the rights of a business object out of the database. <BR/>
     *
     * @param   oid     oid of object we want to get the rights for
     * @param   user    User whose rights we want to check
     *
     * @return  The access permissions.
     */
    protected AccessPermissions performGetRightsContainerData (OID oid, User user)
    {
        AccessPermissions permissions = null;

        // check if there shall be done any computation:
        // this is only necessary if another object is requested or
        // another user is requesting it.
        // if it is the same object and the same user as in the last
        // query the data is already stored in the properties.
        if (!this.p_lastAccessPermissions.isForObjectUser (oid, user))
                                        // rights are not stored yet?
        {
            // initialize the permissions:
            permissions = new AccessPermissions (oid, user);

            // check if we have the oid of an existing object or of a virtual
            // object:
            // (a virtual object is an object which has no representation in the
            // object repository and is just created for some specific temporary
            // use)
            if (this.isPhysical)        // existing object?
            {
                // create stored procedure call:
                StoredProcedure sp = new StoredProcedure (this.procGetRightsContainer,
                    StoredProcedureConstants.RETURN_NOTHING);

                // parameter definitions:
                // must be in right sequence (like SQL stored procedure def.)
                // input parameters
                // oid
                BOHelpers.addInParameter (sp, oid);
                // user id
                if (user != null)
                {
                    sp.addInParameter (ParameterConstants.TYPE_INTEGER, user.id);
                } // if
                else
                {
                    sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
                } // else

                // output parameters
                // objectRights
                Parameter objectRightsOutParam = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
                // containerRights
                Parameter containerRightsOutParam = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
                // isContainer
                Parameter isContainerOutParam = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);

                // perform the procedure call:
                BOHelpers.performCallProcedureData (sp, this.env);

                // return the rights
                // set permissions within permissions object:
                permissions.setPermissions (
                        objectRightsOutParam.getValueInteger (),
                        containerRightsOutParam.getValueInteger (),
                        isContainerOutParam.getValueBoolean ());
            } // if existing object
            else                        // virtual object
            {
                // explicitly set the rights of the user on the object:
                permissions.setPermissions (Operations.OP_ALL,
                                            Operations.OP_ALL,
                                            false);
            } // else virtual object

            // remember the actual data to prevent that there are more queries
            // made for the same data:
            this.p_lastAccessPermissions = permissions;
        } // if rights are not stored yet
        else // permissions are already stored
        {
            // get the permissions:
            permissions = this.p_lastAccessPermissions;
        } // else permissions are already stored

        // return the resulting access permissions:
        return permissions;
    } // performGetRightsContainerData


    /**************************************************************************
     * Check if the user has the necessary rights on the actual object to
     * perform the required operation (s) within the data store. <BR/>
     *
     * @param   userId      Id of user whose rights we want to check.
     * @param   operation   Operation (s) the user wants to perform on the
     *                      object.
     *
     * @return  <CODE>true</CODE> if the user is allowed to perform the
     *          operation (s), <CODE>false</CODE> otherwise.
     */
    protected boolean performCheckRights (int userId, int operation)
    {
        boolean hasRights = false;      // does the user have the rights?
                                        // (return value of this method)

        // check if we have the oid of an existing object or of a virtual
        // object:
        // (a virtual object is an object which has no representation in the
        // object repository and is just created for some specific temporary
        // use)
        if (!this.oid.isTemp ())        // existing object?
        {
            // create the stored procedure call:
            StoredProcedure sp = new StoredProcedure(
                    this.procCheckRights,
                    StoredProcedureConstants.RETURN_VALUE);

            // parameter definitions:
            // must be in right sequence (like SQL stored procedure def.)
            // input parameters
            // oid
            BOHelpers.addInParameter(sp, this.oid);
            // containerId
            BOHelpers.addInParameter (sp, this.containerId);
            // user id
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, userId);
            // requiredRights
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, operation);

            // output parameters
            // hasRights
            Parameter hasRightsParam = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

            try
            {
                // perform the function call:
                BOHelpers.performCallFunctionData(sp, this.env);
            } // try
            catch (NoAccessException e)
            {
                // nothing to do, exception can be ignored.
            } // catch

            // return the rights
            hasRights = hasRightsParam.getValueInteger () == operation;
        } // if existing object
        else                            // virtual object
        {
            // explicitly set the rights of the user on the object:
            hasRights = true;
        } // else virtual object

        return hasRights;               // return the computed value
    } // performCheckRights


    /**************************************************************************
     * Check if the user has the necessary rights on the actual object and all
     * subsequent objects to perform the required operation (s) within the data
     * store. <BR/>
     *
     * @param   userId      Id of user whose rights we want to check.
     * @param   operation   Operation (s) the user wants to perform on the
     *                      object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object and the sub
     *              objects to perform the required operation.
     * @exception   ObjectNotAffectedException
     *              The user does not have access to at least the object or one
     *              of the sub objects.
     */
    protected void performCheckRightsRecursive (int userId, int operation)
        throws NoAccessException, ObjectNotAffectedException
    {
        int retVal = UtilConstants.QRY_OK; // return value of query

        // check if we have the oid of an existing object or of a virtual
        // object:
        // (a virtual object is an object which has no representation in the
        // object repository and is just created for some specific temporary
        // use)
        if (!this.oid.isTemp ())        // existing object?
        {
            // create the stored procedure call:
            StoredProcedure sp = new StoredProcedure(
                    this.procCheckRightsRecursive,
                    StoredProcedureConstants.RETURN_VALUE);
            
            // parameter definitions:
            // must be in correct sequence (like SQL stored procedure def.)
            // input parameters:
            // oid:
            BOHelpers.addInParameter(sp, this.oid);
            // user id
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, userId);
            // requiredRights
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, operation);

            // perform the function call:
            retVal = BOHelpers.performCallFunctionData(sp, this.env);

            // check the return value:
            if (retVal == UtilConstants.QRY_NOTALLAFFECTED)
            {
                // raise object (s) not affected exception
                ObjectNotAffectedException error = new ObjectNotAffectedException (
                    MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
                        UtilExceptions.ML_E_OBJECTNOTAFFECTEDEXCEPTION, env));
                throw error;
            } // if QRY_NOTALLAFFECTED
        } // if existing object
        else                            // virtual object
        {
            // the user can work on a virtual object like he wants,
            // nothing to do here
        } // else virtual object
    } // performCheckRightsRecursive



    ///////////////////////////////////////////////////////////////////////////
    // database function interfaces
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get a business object out of the database. <BR/>
     * This method checks if the object was already loaded into memory. In this
     * case it checks if the user's rights are sufficient to perform the
     * requested operation on the object. If this is all right the object is
     * returned otherwise an exception is raised. <BR/>
     * If the object is not already in the memory it must be loaded from the
     * database. In this case there is also a rights check done. If this is all
     * right the object is returned otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   AlreadyDeletedException
     *              The required object was deleted before the user wanted to
     *              access it.
     * @exception   ObjectNotFoundException
     *              The required object was not found during search, i.e. there
     *              does not exist any object with the required oid.
     */
    public void retrieve (int operation)
        throws NoAccessException, AlreadyDeletedException,
        ObjectNotFoundException
    {
//trace ("isPhysical: " + isPhysical + ", " + this.oid);
        if (!this.isPhysical)
        {
            // there is no physical object on the database possible from the current class
            return;
        } // if

        if (this.isActual ())           // object already in memory?
        {
            // check user's rights:
            if (!this.user.hasRights (this.oid, operation))
                                        // access not allowed?
            {
                // raise no access exception
                NoAccessException error =
                    new NoAccessException (MultilingualTextProvider.getMessage (
                        UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_NOACCESSEXCEPTION, env));
                throw error;
            } // if access not allowed
        } // if object already in memory
        else                            // object not in memory
        {
            // call the object type specific method:
            this.state = States.ST_UNKNOWN;
            this.performRetrieveData (operation);

            // check if the object is deleted and we are currently not in a
            // type translation process for the object's type:
            // remark: for type translations also deleted objects may be
            // instantiated.
            if (this.state == States.ST_DELETED && !this.env.isInstantiationAllowed (this.typeObj.getCode ()))
            {
//              showMessage ("Objekt konnte nicht gefunden werden!"); // HACK!!!
                throw new AlreadyDeletedException (MultilingualTextProvider
                    .getMessage (UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_OBJECTDELETED, env));

            } // if the actual object is deleted
            this.setActual ();          // the data within the object are
                                        // actual

            // store user-dependent data:
            if (this.user != null) // user set?
            {
                // cache access:
                this.accessCache.put (this.user.id, operation, true);
            } // if user set
        } // else object not in memory
    } // retrieve


    ///////////////////////////////////////////////////////////////////////////
    // viewing functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Represent the actual view of the object to the user within a frameset.
     * <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   function1           Function for first frame.
     */
    protected void performShowFrameset (int representationForm, int function1)
    {
        // start with the container representation: show frameset
        Page page = new Page (true);    // the output page
        FrameSetElement frameset = new FrameSetElement (this.framesAsRows, 2);

        if (function1 != AppFunctions.FCT_NOFUNCTION)
        {
            this.frm1Function = function1;
        } // if

        // define URL for first frame:

        // execute function in first frame
        if ((this.frm1Function != AppFunctions.FCT_NOFUNCTION) &&
            (this.frm1Url.equals (AppConstants.FILE_EMPTYPAGE)))
        {
            this.frm1Url = this.getBaseUrlGet () +
                HttpArguments.createArg (BOArguments.ARG_FUNCTION, this.frm1Function) +
                HttpArguments.createArg (BOArguments.ARG_OID, "" + this.oid) +
                HttpArguments.createArg (BOArguments.ARG_ISFRAMESET, true) +
                HttpArguments.createArg (BOArguments.ARG_SHOWTABBAR, this.displayTabs ? 1 : 0) +
                HttpArguments.createArg (BOArguments.ARG_WEBLINK, this.sess.weblink ? 1 : 0);
        } // if

        // show emptypage in first frame
// !!! AJ..HACK BEGIN
        if ((this.frm1Function == AppFunctions.FCT_NOFUNCTION) &&
            (this.frm1Url.equals (AppConstants.FILE_EMPTYPAGE)))
        {
            this.frm1Url = this.getUserInfo ().homepagePath +
                AppConstants.FILE_EMPTYPAGE;
        } // if

//this would be the good version ->  the problem is, that hompagePath is null on a NT system (inconsistence)
/*
        if ((this.frm1Function == AppFunctions.FCT_NOFUNCTION) &&
           (this.frm1Url.equals (AppConstants.FILE_EMPTYPAGE)))
            frm1Url = BOPathConstants.PATH_HTTPPREFIX + this.getUserInfo ().homepagePath + AppConstants.FILE_EMPTYPAGE;
*/
// !!! AJ...HACK END

        // define URL for second frame:

        // execute function in second frame
        if ((this.frm2Function != AppFunctions.FCT_NOFUNCTION) &&
            (this.frm2Url.equals (AppConstants.FILE_EMPTYPAGE)))
        {
            this.frm2Url = this.getBaseUrlGet () +
                HttpArguments.createArg (BOArguments.ARG_FUNCTION, this.frm2Function) +
                HttpArguments.createArg (BOArguments.ARG_OID, "" + this.oid) +
                HttpArguments.createArg (BOArguments.ARG_SHOWTABBAR, this.displayTabs ? 1 : 0) +
                HttpArguments.createArg (BOArguments.ARG_WEBLINK, this.sess.weblink ? 1 : 0);
        } // if

// !!! AJ..HACK BEGIN
        // show emptypage in second frame
        if ((this.frm2Function == AppFunctions.FCT_NOFUNCTION) &&
            (this.frm2Url.equals (AppConstants.FILE_EMPTYPAGE)))
        {
            this.frm2Url = this.getUserInfo ().homepagePath + AppConstants.FILE_EMPTYPAGE;
        } // if
//this would be the good version ->  the problem is, that hompagePath is null on a NT system (inconsistence)
/*
        // show emptypage in second frame
        if ((this.frm2Function == AppFunctions.FCT_NOFUNCTION) &&
           (this.frm2Url.equals (AppConstants.FILE_EMPTYPAGE)))
            frm2Url = BOPathConstants.PATH_HTTPPREFIX + this.getUserInfo ().homepagePath + AppConstants.FILE_EMPTYPAGE;
*/
// !!! AJ...HACK END


        FrameElement frm1Frame =
            new FrameElement (HtmlConstants.FRM_SHEET1, this.frm1Url);
        FrameElement frm2Frame =
            new FrameElement (HtmlConstants.FRM_SHEET2, this.frm2Url);

        // insert frames into frameset:
//        frm1Frame.frameborder = true;
        frm1Frame.resize = true;
//        frm2Frame.frameborder = true;
        frm2Frame.resize = true;

        frameset.addElement (frm1Frame, this.frm1Size);
        frameset.addElement (frm2Frame, this.frm2Size);
        frameset.frameborder = true;
        frameset.frameSpacing = 1;

        page.body.addElement (frameset);

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e.getMsg (), this.app, this.sess, this.env);
        } // catch
    } // performShowFrameset


    /**************************************************************************
     * Represent the object, i.e. its properties, to the user.
     * <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    private void performShowInfo (int representationForm)
    {
        Page page = new Page (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUSINESSOBJECT, env), false);

        this.insertInfoStyles (page);

        // set the icon of this object:
        this.setIcon ();

        // create Header
        GroupElement body;
        
        // set the name of the object as multilang name for fallback
        String mlName = this.name;
        // get the external key of this object 
        ExternalKey objExtKey = BOHelpers.getExtKeyByOid (this.oid, env);
        
        // do we have an extKey for this object
        if (objExtKey != null)
        {
            // try to get a translation for the objects name
            mlName = MultilingualTextProvider.getMultilangObjectName (
                objExtKey.getId (), objExtKey.getDomain (), mlName, env);
        } // if
        
        if (this.isTab ())              // object is part of upper object?
        {
            // set the name of the container object as multilang name for fallback
            String containerMlName = this.containerName;
            // get the external key of this container object 
            ExternalKey objContExtKey = BOHelpers.getExtKeyByOid (this.containerId, env);
            
            // do we have an extKey for this container object
            if (objContExtKey != null)
            {
                // try to get a translation for the container objects name
                containerMlName = MultilingualTextProvider.getMultilangObjectName (
                    objContExtKey.getId (), objContExtKey.getDomain (), containerMlName, env);
            } // if

            body = this.createHeader (page, mlName, this.getNavItems (),
                containerMlName, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INFO, env),
                this.icon, this.containerName);
        } // if
        else                            // object exists independently
        {
            body = this.createHeader (page, mlName, this.getNavItems (),
                null, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INFO, env),
                this.icon, this.containerName);
        } // else


        // loop through all properties of this object and display them:
        this.properties = 0;

        // if the object supports layout generation  with
        // XSL style sheed use this output otherwise
        // get the default layout ´from the TableElement.
        String xslOutput = this.showPropertiesXSL ();
        if (xslOutput != null)
        {
            body.addElement (new TextElement (xslOutput));
        } // if (xslOutput != null)
        else
        {
            TableElement table = this.createFrame (representationForm);

            table.classId = CssConstants.CLASS_INFO;
            String[] classIds = new String[2];
            classIds[0] = CssConstants.CLASS_NAME;
            classIds[1] = CssConstants.CLASS_VALUE;
            table.classIds = classIds;

            // start with the object representation: show header
            this.showProperties (table);
            body.addElement (table);

            if (this.getUserInfo ().userProfile.showRef)
            {
                this.showRefs (table, page);
            } // if
        } // if (xslOutput != null)


        // create footer
//        outerTable = createFormFooter (outerTable, form);
//        page.body.addElement (outerTable);

        this.showInfoBottom (page);

        // ensure that the info tab is active:
        if (this.p_tabs != null)        // tabs exist?
        {
            this.p_tabs.setActiveTab (0, TabConstants.TC_INFO);
        } // if tabs exist

        if (this.p_isShowCommonScript)
        {
            // create the script to be executed on client:
            ScriptElement script = this.getCommonScript (false);
            page.body.addElement (script);
        } // if

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e.getMsg (), this.app, this.sess, this.env);
        } // catch
    } // performShowInfo


    /**************************************************************************
     * Generates the HTML code for the info view by using a stylesheet file. <BR/>
     *
     * @return  The HTML code or <CODE>null</CODE> if no stylesheet is defined.
     */
    protected String showPropertiesXSL ()
    {
        return null;
    } // showPropertiesXSL


    /**************************************************************************
     * Generates the HTML code for the edit view by using a stylesheet file. <BR/>
     *
     * @return      Vector with the edit dialog info or <CODE>null</CODE>
     *              if no stylesheet is defined.
     *
     *              The returned vector must contain two elements:
     *
     *              1. the HTML code as a String object
     *              2. the multipart flag as a Boolean object
     */
    protected Vector<Object> showFormPropertiesXSL ()
    {
        return null;
    } // showPropertiesXSL


    /**************************************************************************
     * Represent the properties of a BusinessObject object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        this.showProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env),
                      Datatypes.DT_NAME, this.name);
//        showProperty (table, BOArguments.ARG_OID, TOK_OID, Datatypes.DT_TEXT, "" + oid);
        this.showProperty (table, BOArguments.ARG_TYPE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TYPE, env),
                      Datatypes.DT_TYPE, this.getMlTypeName ());
        this.showProperty (table, BOArguments.ARG_INNEWS, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env),
                      Datatypes.DT_BOOL, "" + this.showInNews);
        this.showProperty (table, BOArguments.ARG_DESCRIPTION,
                      MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DESCRIPTION, env), Datatypes.DT_DESCRIPTION,
                      this.description);
        this.showProperty (table, BOArguments.ARG_VALIDUNTIL,
                      MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_VALIDUNTIL, env), Datatypes.DT_DATE,
                      this.validUntil);

        /////////////////////////////////////////
        //
        // WORKFLOW-BLOCK: START
        //
        // retrieve workflow-instance information for this object
        this.getWorkflowInstanceInfo ();

        // check if object has active workflow instance
        if (this.workflowInfo != null)
        {
            // show property to view workflow-instance
            this.showProperty (table,
                          "", //WorkflowArguments.xxx
                          MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                              WorkflowTokens.ML_CURRENT_STATE, env),
                          Datatypes.DT_LINK,
                          this.workflowInfo.currentState,
                          this.workflowInfo.instanceId);
        } // if
        //
        // WORKFLOW-BLOCK: END
        //
        /////////////////////////////////////////

        if (this.checkedOut)
        {
            // the object was checked out
            if (this.checkOutUserName != null &&
                this.checkOutUserName.length () > 0)
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

        if (this.getUserInfo ().userProfile.showExtendedAttributes)
        {
            this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);

            this.showProperty (table, BOArguments.ARG_OWNER, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OWNER, env),
                          Datatypes.DT_USER, this.owner);
            this.showProperty (table, BOArguments.ARG_CREATED, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CREATED, env),
                          Datatypes.DT_USERDATE, this.creator, this.creationDate);
            this.showProperty (table, BOArguments.ARG_CHANGED, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CHANGED, env),
                          Datatypes.DT_USERDATE, this.changer, this.lastChanged);
        } // if (app.userInfo.userProfile.showExtendedAttributes)

        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);
    } // showProperties


    /**************************************************************************
     * Represent something on the bottom of the info - view of an object to
     * the user. <BR/>
     *
     * @param   page    Page where the properties shall be added.
     */
    protected void showInfoBottom (Page page)
    {
        // this method may be overwritten in sub classes
    } // showInfoBottom


    /**************************************************************************
     * Represent the object, i.e. its properties, to the user within a form.
     * <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   newFormFct          The function to be performed when submitting
     *                              the form.
     */
    protected void performShowNewForm (int representationForm, int newFormFct)
    {
        Page page = new Page (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUSINESSOBJECT, env), false);

        // Stylesheetfile wird geladen
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS =
            this.sess.activeLayout.path + this.env.getBrowser () + "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);

        // set the icon of this object:
        this.setIcon ();
        // create Header
        FormElement form =
            this.createFormHeader (page, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUSINESSOBJECT, env),
                              this.getNavItems (),
                              null, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUTTONNEW, env), null,
                              this.icon, this.containerName);
        TableElement table = this.createFrame (representationForm, 2);
        table.border = 0;

        table.classId = CssConstants.CLASS_INFO;
        String[] classIds = new String[2];
        classIds[0] = CssConstants.CLASS_NAME;
        classIds[1] = CssConstants.CLASS_VALUE;
        table.classIds = classIds;

        // start with the object representation: show header
        form.addElement (new InputElement (BOArguments.ARG_CANCELED,
                                           InputElement.INP_HIDDEN, "N"));
        form.addElement (new InputElement (BOArguments.ARG_OID,
                                           InputElement.INP_HIDDEN,
                                           "" + this.oid));
        form.addElement (new InputElement (BOArguments.ARG_CONTAINERID,
                                           InputElement.INP_HIDDEN,
                                           "" + this.containerId));
        form.addElement (new InputElement (BOArguments.ARG_FUNCTION,
                                           InputElement.INP_HIDDEN,
                                           "" + newFormFct));

        // loop through all properties of this object and display them:
        this.properties = 0;
        this.showNewFormProperties (table);

        form.addElement (table);

        // finish the object representation: show footer
        this.createFormFooter (form);

        ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
        script.addScript (
            "top.oid = \"" + this.oid + "\";" +
            "top.majorOid = \"" + this.oid + "\";" +
//            "top.showListHeading ('" + name + "');\n" +
            "top.containerId = \"" + this.containerId + "\";");
        page.body.addElement (script);

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e.getMsg (), this.app, this.sess, this.env);
        } // catch
    } // performShowNewForm


    /**************************************************************************
     * Represent the form for setting the properties to create a BusinessObject
     * object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showNewFormProperties (TableElement table)
    {
        // loop through all properties of this object and display them:

        // Shall Name field be editable?
        if (this.p_isEditNameInNewForm)
        {
            this.showFormProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env),
                              Datatypes.DT_NAME, "");
        } // if (!isReducedNewForm)

        this.showFormProperty (table, BOArguments.ARG_TYPE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TYPE, env),
                          Datatypes.DT_TYPE, "");
        this.formFieldRestriction = new FormFieldRestriction (false);

        // Shall the description field be editable?
        if (this.p_isEditDescInNewForm)
        {
            this.formFieldRestriction =
                new FormFieldRestriction (true, BOConstants.MAX_LENGTH_DESCRIPTION, 0);
            this.showFormProperty (table, BOArguments.ARG_DESCRIPTION,
                              MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DESCRIPTION, env), Datatypes.DT_DESCRIPTION, "");
        } // if (isDescNameInNewForm)
    } // showNewFormProperties


    /**************************************************************************
     * Represent the object, i.e. its properties, to the user within a form.
     * <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   changeFormFct       The function to be performed when submitting
     *                              the form.
     */
    protected void performShowChangeForm (int representationForm,
                                          int changeFormFct)
    {
        // clear filenames-vector in session (userinfo)
        // will be filled when Datatypes.DT_FILE input types are shown
        (this.getUserInfo ()).filenames = new Vector<FilenameElement> ();

        Page page = new Page (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUSINESSOBJECT, env), false);

        this.insertChangeFormStyles (page);

        // set the icon of this object:
        this.setIcon ();

        FormElement form = null;
        if (this.state == States.ST_CREATED) // object was just created?
        {
            // get a multilingual name for the type of the object
            String mlName = this.typeObj.getMlName (MultilingualTextProvider.getUserLocale (env).getLocale ());

            // show corresponding operation name:
            form = this.createFormHeader (page, mlName, this.getNavItems (), null,
                                     MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUTTONNEW, env), null,
                                     this.icon, this.containerName);
        } // if
        else                        // the object was created in past times
        {
            // save the current name of the object
            String mlName = this.name;

            // do we display a tab object here, then get the corresponding translation
            if (this.isTab ())
            {
                // build the lookupKey for getting the translation
                String tabCode = this.performGetTabCodeData (this.oid);
                String lookupKey = MultilangConstants.LOOKUP_KEY_PREFIX_TAB + tabCode;    

                // get a possible translation out of the tabs resource bundle
                MultilingualTextInfo mlNameInfo = MultilingualTextProvider.getMultilingualTextInfo (
                    MultilangConstants.RESOURCE_BUNDLE_TABS_NAME,
                    MultilingualTextProvider.getNameLookupKey (lookupKey),
                    MultilingualTextProvider.getUserLocale (env),
                    env);

                // did we found a translated name for the tab
                if (mlNameInfo.isFound ())
                {
                    mlName = mlNameInfo.getMLValue ();
                } // if
            } // if 
            // is the current name equal to the type name of the object, then try to find a translation for it
            else if (this.name.equals (this.typeName))
            {
                // get a multilingual name for the type of the object
                mlName = this.typeObj.getMlName (MultilingualTextProvider.getUserLocale (env).getLocale ());
            } // else if

            form = this.createFormHeader (page, mlName, this.getNavItems (), null,
                                     MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUTTONEDIT, env), null,
                                     this.icon, this.containerName);
        } // else

        // start with the object representation: show header
        form.addElement (new InputElement (BOArguments.ARG_OID,
                                           InputElement.INP_HIDDEN,
                                           "" + this.oid));
        form.addElement (new InputElement (BOArguments.ARG_FUNCTION,
                                           InputElement.INP_HIDDEN,
                                           "" + changeFormFct));


        // get the xsl output and the multipart form flag.
        // the returned vector must contain 2 objects:
        //      1. a String object containing the xsl output.
        //      2. a Boolean object witch defines the form type:
        //          if the values is true the we have to create a
        //          multipart form otherwise we create a normal form.
        boolean isXslOutputValid = false;
        Vector<Object> xslFormInfo = this.showFormPropertiesXSL ();
        if (xslFormInfo != null && xslFormInfo.size () == 2)
        {
            String xslFormOutput = (String) xslFormInfo.elementAt (0);
            Boolean xslMultipartFlag = (Boolean) xslFormInfo.elementAt (1);

            if (xslFormOutput != null)
            {
                // add the output to the form
                form.addElement (new TextElement (xslFormOutput));
            } // if (xslFormOutput != null)

            // set type of form to multipart
            if (xslMultipartFlag != null && xslMultipartFlag.booleanValue ())
            {
                // in case the form contains a upload input element
                form.enctype = "multipart/form-data";
            } // if (xslMultipart.getBoolean ())

            isXslOutputValid = true;
        } // if (xslFormInfo != null)
        else
        {
            TableElement table = this.createFrame (representationForm, 2);
            table.border = 0;
            table.classId = CssConstants.CLASS_INFO;
            String[] classIds = new String[2];
            classIds[0] = CssConstants.CLASS_NAME;
            classIds[1] = CssConstants.CLASS_VALUE;
            table.classIds = classIds;

            // loop through all properties of this object and display them:
            this.properties = 0;
            this.showFormProperties (table);
            form.addElement (table);
        } // else if (xslForm != null)

        // finish the object representation: show footer
        if (this.state == States.ST_CREATED) // object was just created?
        {
            this.createFormFooter (form, null, null, null, null, this.showExtendedCreationMenu, false);
        } // if
        else
        {
            this.createFormFooter (form); // no? Just edit it, no enhanced menu
        } // else

        this.showChangeFormBottom (page);

        if (this.p_isShowCommonScript)
        {
            // create the script to be executed on client:
            ScriptElement script;
            if (this.state == States.ST_CREATED) // the object was just created?
            {
                script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
                script.addScript (
                    "top.oid = \"" + this.oid + "\";" +
                    "top.majorOid = \"" + this.oid + "\";" +
                    "top.containerId = \"" + this.containerId + "\";" +
                    BOConstants.CALL_SHOWTABSEMPTY +
                    BOConstants.CALL_SHOWBUTTONSEMPTY);
            } // if the object was just created
            else                        // the object was created in past times
            {
                script = this.getCommonScript (false);
            } // else the object was created in past times
            page.body.addElement (script);
        } // if

        // if the form is generated by a stylesheet
        // set a stylesheet specific onSubmit argument.
        if (isXslOutputValid)
        {
            form.onSubmit = "return xslSubmitAllowed ();";
        } // if (isXslOutputValid)

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e.getMsg (), this.app, this.sess, this.env);
        } // catch
    } // performShowChangeForm


    /**************************************************************************
     * Represent the properties of a BusinessObject object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        // property 'name':
        // set form field restriction (will automatically be used in
        // next showFormProperty)
        // restrict: no empty entries allowed
        this.formFieldRestriction =
            new FormFieldRestriction (false);
        // loop through all properties of this object and display them:
        this.showFormProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env),
                          Datatypes.DT_NAME, this.name);

//      showFormProperty (table, BOArguments.ARG_OID, TOK_OID, Datatypes.DT_TEXT, "" + oid);
//        showProperty (table, BOArguments.ARG_TYPE, TOK_TYPE, Datatypes.DT_TYPE, typeName);

        this.showFormProperty (table, BOArguments.ARG_INNEWS,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env), Datatypes.DT_BOOL, "" + this.showInNews);
        // restrict: empty entries allowed
        this.formFieldRestriction =
            new FormFieldRestriction (true, BOConstants.MAX_LENGTH_DESCRIPTION, 0);

        this.showFormProperty (table, BOArguments.ARG_DESCRIPTION,
                          MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DESCRIPTION, env), Datatypes.DT_DESCRIPTION,
                          this.description);

/* BB deactivated: restriction turned out to be not useful
        // create date format for servers locale:
        DateFormat shortdate = DateFormat.getDateInstance (DateFormat.SHORT, l);
        // create date (current time)
        Date curDate = new Date ();
        // create current date string (e.g. '10.8.99')
        String curDateString = shortdate.format (curDate);
        // property 'validUntil':
        // set form field restriction (will automatically be used in
        // next showFormProperty)
        // restrict: no empty entries allowed
        // 0 .. default size/length values for datatype will be taken
        // null .. no upper bound
        this.formFieldRestriction =
            new FormFieldRestriction (false, 0, 0, curDateString, null);
*/
        this.showFormProperty (table, BOArguments.ARG_VALIDUNTIL,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_VALIDUNTIL, env), Datatypes.DT_DATE, this.validUntil);
    } // showFormProperties


    /**************************************************************************
     * Builds the common script which is used in most pages. <BR/>
     * This script must be included into a page like
     * <CODE>page.body.addElement (getCommonScript (...));</CODE>. <BR/>
     * This method uses the properties {@link #oid oid} and
     * {@link #disableButtonsState disableButtonsState} and the method
     * {@link #getButtonBarCall getButtonBarCall}.
     *
     * @param   isContentView   Create button bar for content view
     *                          (<CODE>true</CODE>) or for info view
     *                          (<CODE>false</CODE>).
     *
     * @return  The script containing the tab bar and the button bar.
     */
    protected final ScriptElement getCommonScript (boolean isContentView)
    {
        TabContainer tabBar = null;     // the current tab bar
        OID tabBarOid = this.oid;       // the oid for the tab bar
        TabContainer objTabBar = null;  // tab bar of current object
        ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);

        if (this.sess.wizardRegistration)
        {
            this.displayTabs = false;
        } // if
        else if (this.sess.weblink)
        {
            this.displayTabs = true;
        } // else if
        else                            // obviously we want to display the tabs
        {
            this.displayTabs = true;
        } // else obviously we want to display the tabs

        if (!this.isTab ())             // object is within container?
        {
            // store container:
            script.addScript (
                "top.containerId = \"" + this.containerId + "\";\n");
        } // if
        if (this.isMajorContainer)
        {
            script.addScript (
                "top.isOtherContent=true;\n" +
                "top.otherContentId=\"" + this.oid + "\"");
        } // if

        // show tabs:
        if (this.displayTabs)           // shall the tab bar be displayed?
        {
            // check if the tabs property is already filled:
            if (this.p_tabs == null)    // the tabs are not known?
            {
                // get the tab data out of the data base:
                this.p_tabs = this.performRetrieveObjectTabData ();
            } // if the tabs are not known

            if (this.isTab ())
                                        // object is a tab of another object?
            {
                // set the oid of the object for which the tab bar shall be
                // displayed:
                tabBarOid = this.containerId;

                // set the tab bar to be displayed:
                if (this.p_containerTabs != null) // container tabs known?
                {
                    objTabBar = this.p_containerTabs;
                    objTabBar.setActiveTab (0, this.oid);
                } // if container tabs known
            } // if object is a tab of another object
            else
                                        // object is not a tab
            {
                // set the oid of the object for which the tab bar shall be
                // displayed:
                tabBarOid = this.oid;

                // set the tab bar to be displayed:
                objTabBar = this.p_tabs;
            } // else object is not a tab

            try
            {
                // get the tab bar for the current user:
                tabBar = new TabContainer (tabBarOid, this.user);
                this.setTabs (tabBar, objTabBar);
                tabBar.setShowButtonsLoading (!this.disableButtonsState);

                // build the script call for tab bar:
                if (this.sess.weblink)      // weblink?
                {
                    script.addScript ("top.tryCall (\"" +
                        tabBar.buildJavaScriptCall () + "\");");
                } // if weblink
                else                        // not weblink
                {
                    // build the script call for tab bar:
                    script.addScript (tabBar.buildJavaScriptCall ());
                } // else not weblink
            } // try
            catch (ListException e)
            {
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch

            this.displayTabs = false;   // don't show the tabs again
            this.disableButtonsState = false;
                                        // enable buttons state for next time
        } // if shall the tab bar be displayed

        if (!this.sess.wizardRegistration)
        {
            // show buttons:
            script.addScript (
//                "top.showListHeading ('" + this.name + "');\n" +
//                "top.showListHeading ('" + this.name + "', " + size + ");\n" +
                "top.oid = \"" + this.oid + "\";" +
                "top.containerId = \"" + this.containerId + "\";\n" +
                "top.majorOid = \"" + this.oid + "\";");
            if (this.displayButtons)
            {
                if (this.sess.weblink)
                {
                    script.addScript ("top.tryCall (\"" +
                        this.getButtonBarCall (isContentView) + "\");");
                } // if
                else
                {
                    script.addScript (this.getButtonBarCall (isContentView));
                } // else
            } // if
        } // if

        // let the client know if the object is stored on the database
        // (physical or virtual):
        script.addScript ("top.isPhysical = " + this.isPhysical + ";");

        return script;                  // return the computed script
    } // getCommonScript


    /**************************************************************************
     * Builds a script which deactivates the tabs and buttons. <BR/>
     * This script must be included into a page like
     * <CODE>page.body.addElement (getButtonsTabsDeactivationScript ());</CODE>. <BR/>
     *
     * @return  The script containing the code for emptying the tab bar and
     *          the button bar.
     */
    protected final ScriptElement getButtonsTabsDeactivationScript ()
    {
        ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT); // the script

        // create the script:
        script.addScript (BOConstants.CALL_SHOWTABSEMPTY +
                          BOConstants.CALL_SHOWBUTTONSEMPTY);

        return script;                  // return the computed script
    } // getButtonsTabsDeactivationScript


    /**************************************************************************
     * Represent something after the formfooter (buttons) on the
     * changeform to the user. <BR/>
     *
     * @param   page    Page where the properties shall be added.
     */
    protected void showChangeFormBottom (Page page)
    {
        // this method may be overwritten in sub classes
    } // showChangeFormBottom


    /**************************************************************************
     * Get the user specific tab bar for the BusinessObject. <BR/>
     *
     * @return  The user specific tabs of the actual object.
     */
    public TabContainer getTabBar ()
    {
        TabContainer tabBar = null;     // the tab bar to be filled

        // check if the tabs property is already filled:
        if (this.p_tabs == null)    // the tabs are not known?
        {
            // get the tab data out of the data base:
            this.p_tabs = this.performRetrieveObjectTabData ();
        } // if the tabs are not known

        try
        {
            tabBar = new TabContainer (this.oid, this.user);

            // set the (user specific) tabs within the tab bar:
            this.setTabs (tabBar, this.p_tabs);
        } // try
        catch (ListException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch

        return tabBar;                  // return the completed tab bar
    } // getTabBar


    /**************************************************************************
     * Set all tabs of a BusinessObject which are relevant for the actual
     * user. <BR/>
     *
     * @param   tabBar      Tab bar where the tabs shall be added. The content
     *                      of this parameter is changed.
     * @param   objTabBar   The user-independent tab bar of the object.
     */
    protected final void setTabs (TabContainer tabBar, TabContainer objTabBar)
    {
        // ensure that there are no old tabs within the tab bar:
        tabBar.clear ();

        // check if the object's tab are now known:
        if (objTabBar != null)          // the tabs are known?
        {
            // loop through all tabs and add them to the user specific tab list:
            for (Enumeration<Tab> elems = objTabBar.elements (); elems.hasMoreElements ();)
            {
                // add the current tab to the tab list:
                this.addTab (tabBar, elems.nextElement ());
            } // for

            // check if the active tab of the object tab bar is contained
            // within the user-specific tab bar:
            if (objTabBar.getActiveTab () != null &&
                tabBar.contains (objTabBar.getActiveTab ()))
                                        // active tab is contained?
            {
                // set the active tab for the user-specific tab bar:
                tabBar.setActiveTab (objTabBar.getActiveTab ());
            } // if active tab is contained
        } // if the tabs are known
    } // setTabs


    /**************************************************************************
     * Add one specific tab to the actual tab bar. <BR/>
     * This method checks the rights for the user on the specific tab and other
     * possible constraints and if allowed adds the tab to the tab bar.
     *
     * @param   tabBar      Tab bar where the tab shall be added. <BR/>
     * @param   tab         The tab itself.
     */
    protected final void addTab (TabContainer tabBar, Tab tab)
    {
        OID tabOid = null;              // the oid of the actual tab object
        int actualRights = 0;           // the rights of the user for the tab
        AccessPermissions permissions;  // the actual permissions


        // get the tab oid out of the tab:
        if (tab != null)                // tab was found?
        {
            tabOid = tab.getOid ();     // get the oid
        } // if tab was found
        else                            // the tab was not found
        {
            // the tab has no own oid:
            // (so the oid of the object itself is used; see below)
            tabOid = tabBar.getOid ();
        } // if the tab was not found

        // get actual rights for the actual tab:
        permissions = this.performGetRightsContainerData (tabOid, tabBar.getUser ());

        actualRights = permissions.p_objectRights; // get actual rights

        if ((tabBar.getOid () != null) &&
            (tabBar.getOid ().tVersionId ==
                this.getTypeCache ().getTVersionId (TypeConstants.TC_Rights)))
        {
            actualRights = Operations.OP_READ;
        } // if

        // check the permissions:
        if ((actualRights & tab.getRights ()) == tab.getRights ())
                                        // user has enough rights?
        {
            // for empty tabs insert the tab just if the user has permissions
            // to add a new object:
            if (tab == null || tab.getCountElems () > 0 ||
                (actualRights & Operations.OP_ADDELEM) == Operations.OP_ADDELEM)
            {
                // add the tab to the current user specific tabs:
                tabBar.add (tab);
            } // if
        } // if user has enough rights
    } // addTab


    /**************************************************************************
     * Gets values of the buttons and returns the values as an aray of buttons. <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   isContentView       Display the Info-View buttons or the Content-view buttons
     *
     * @return  An array which includes buttons
     */
    protected int[] buildButtonBar (int representationForm, boolean isContentView)
    {
        int[]           buttons;
        String[] typeIds = this.getTypeIds ();
        AccessPermissions permissions;  // the access permissions
        boolean isDisplayCheckOut = false; // is the checkOut button displayed?

        // get the rights:
        permissions = this.getContainerRights ();

        // set the users rights in the button bar:
        (this.getUserInfo ()).buttonBar.objectRights = permissions.p_objectRights;
        (this.getUserInfo ()).buttonBar.containerRights = permissions.p_containerRights;

        // check if we have to set:
        if (isContentView)
        {
            buttons = this.setContentButtons ();
        } // if
        else
        {
            buttons = this.setInfoButtons ();
        } // else


        boolean enablePaste = false; // show paste buttons?

        // deactivate paste and pasteReference button
        // in case there is no object on the clipboard:
        if (this.getUserInfo ().copiedOids.isEmpty () &&
            this.getUserInfo ().copiedObjects.isEmpty ()) // no object on clipboard?
        {
            enablePaste = false;    // dont't show paste buttons
        } // if no object on clipboard
        else                        // there is an object on the clipboard
        {
            // deactivate paste and pasteReference button
            // in case there the type of the object on the clipboard
            // is not allowed here:
            enablePaste = false;    // initialize: don't show paste buttons

            int length = 0;

            if (typeIds != null)
            {
                length = typeIds.length; // number of allowed types
            } // if

            // BB: this will only check one selected object and not a list
            // of selected objects!!!
            String typeString = Integer.toString (this.getUserInfo ().markedType);
                                    // string code of type of object
                                    // on clipboard
            // run through all allowed types and enable paste buttons
            // if the type is found:
            for (int i = 0; (i < length) && (!enablePaste); i++)
            {
                if (typeIds[i].equalsIgnoreCase (typeString))
                                    // the type is found?
                {
                    enablePaste = true; // enable paste buttons
                } // if the type was found
            } // for (int i = 0; i < length; i++)
            // In a referenceContainer all objects kann be pasted as a link
            if (this.oid.tVersionId ==
                this.getTypeCache ()
                    .getTVersionId (TypeConstants.TC_ReferenceContainer))
                        // is not a ReferenceContainer_01
            {
                enablePaste = true;
            } // if

            // if enablePaste is still false check the copied virtual objects
            if ((!enablePaste) && (!this.getUserInfo ().copiedObjects.isEmpty ()))
            {
                enablePaste = true;

/* BB: deactivated to improve performance
                // BB: we need to loop through all selected virtual
                // object to determine if we can paste one
                int vectorSize = this.getUserInfo ().copiedObjects.size ();
                BusinessObject virtualObj = null;
                for (int j = 0; (j < vectorSize) && (! enablePaste); j++)
                {
                    virtualObj = (BusinessObject)
                            this.getUserInfo ().copiedObjects.elementAt (j);
                    typeString = Integer.toString (virtualObj.type);
                    // run through all allowed types and enable paste buttons
                    // if the type is found:
                    for (int i = 0; (i < length) && (! enablePaste); i++)
                    {
                        if (typeIds[i].equalsIgnoreCase (typeString))
                                            // the type is found?
                        {
                            enablePaste = true; // enable paste buttons
                        } // if the type was found
                    } // for (int i = 0; i < length; i++)
                } // for (int j = 0; (j < vectorSize) && (! enablePaste); j++)
*/
            } // if ((! enablePaste) && (!this.getUserInfo ().copiedObjects.isEmpty ())
        } // else there is an object on the clipboard
        if (!enablePaste)           // paste buttons allowed?
        {
            // look for the paste and pasteReference buttons and disable
            // them:
            for (int i = 0; i < buttons.length; i++)
            {
                // if found deactivate them
                if (buttons[i] == Buttons.BTN_PASTE || buttons[i] == Buttons.BTN_REFERENCE)
                                    // button found?
                {
                    // deactivate button
                    buttons[i] = Buttons.BTN_NONE;
                } // if button found
            } // for i
        } // if paste buttons allowed

        if (!this.getUserInfo ().isCopyLinkAllowed)         // paste buttons allowed?
        {
            // disable 'paste link' button
            for (int i = 0; i < buttons.length; i++)
            {
                // if found deactivate them
                if (buttons[i] == Buttons.BTN_REFERENCE)
                                    // button found?
                {
                    // deactivate button
                    buttons[i] = Buttons.BTN_NONE;
                } // if button found
            } // for i
        } // if paste buttons allowed

        // disable buttons if checkin/checkout
        // search through all buttons
        for (int i = 0; i < buttons.length; i++)
        {
            // if found deactivate them
            // checkin button?
            if (buttons[i] == Buttons.BTN_CHECKIN ||
                buttons[i] == Buttons.BTN_EDITBEFORECHECKIN ||
                buttons[i] == Buttons.BTN_WEBDAVCHECKIN)
            {
                if ((!this.checkedOut) ||
                    (this.checkOutUser.id != this.user.id))
                {
                    // deactivate button if the business
                    // object is a tab or it has not
                    // been checked out
                    // or if the user is not the user
                    // that checked out the object!
                    buttons[i] = Buttons.BTN_NONE;
                } // if
            } // if
            else if (buttons[i] == Buttons.BTN_CHECKOUT ||
                     buttons[i] == Buttons.BTN_WEBDAVCHECKOUT)
            {
                if (this.isTab () || this.checkedOut)
                {
                    // deactivate this button if the
                    // object has been checked out
                    buttons[i] = Buttons.BTN_NONE;
                } // if
                else
                {
                    // remember that the checkOut button is displayed:
                    isDisplayCheckOut = true;
                } // else
            } // else if
        } // for i

        // don't display other buttons which are used to change the object,
        // if the checkOut button is displayed or
        // the object has been checked out and the actual user is not the
        // checkOutUser:
        if ((isDisplayCheckOut && this.p_isCheckOutExclusive) ||
            (this.checkedOut && (this.user.id != this.checkOutUser.id)))
        {
            // loop through all buttons and deactivate not allowed ones:
            for (int i = 0; i < buttons.length; i++)
            {
                if ((buttons[i] == Buttons.BTN_EDIT) ||
                    (buttons[i] == Buttons.BTN_DELETE) ||
                    (buttons[i] == Buttons.BTN_LISTDELETE) ||
                    (buttons[i] == Buttons.BTN_NEW) ||
                    (buttons[i] == Buttons.BTN_ANSWER) ||
                    (buttons[i] == Buttons.BTN_CUT) ||
                    (buttons[i] == Buttons.BTN_LIST_CUT) ||
                    (buttons[i] == Buttons.BTN_PASTE) ||
                    (buttons[i] == Buttons.BTN_REFERENCE))
                {
                    // deactivate this button:
                    buttons[i] = Buttons.BTN_NONE;
                } // else if
            } // for i
        } // if


        /////////////////////////////////////////
        //
        // WORKFLOW-BUTTONS BLOCK
        //
        // show workflow-buttons for object; kind of button depends
        // on workflow-state, current-user and type of object:
        // (a) START-BUTTON    nonactive workflow + specific object-type
        //                     (nonactive = unstarted, aborted or undefined)
        // (b) FORWARD-BUTTON  active workflow + currentOwner
        // (c) FINISH-BUTTON   active workflow + lastStateReached + currentOwner
        //
        // Boolean matrix:
        //                    START    FORWARD   FINISH
        //                    -------------------------
        // wfAllowed          Y        -         -
        // wfActive           N        Y         Y
        // wfLastState        -        N         Y
        // wfCurrentOwner     -        Y         Y
        //
        //                    Y YES    N NO      - does not matter
        //

        // indicates state of objects workflow-instance
        boolean wfAllowed = false;
        boolean wfActive = false;
        boolean wfLastState = false;

        boolean wfCurrentOwner = false;

        // retrieve needed workflow instance information
        this.getWorkflowInstanceInfo ();

        //
        // set state-indicators (for details see above):
        //

        // check if there is any workflow-information at all
        if (this.workflowInfo != null)
        {
            // check if current user ist wf-current-owner
            if (this.user.id == this.workflowInfo.currentOwnerId)
            {
                wfCurrentOwner = true;
            } // if

            // check for active
            if (this.workflowInfo.workflowState.equals (WorkflowConstants.STATE_OPEN_RUNNING))
            {
                // set indicator to active
                wfActive = true;
            } // else if
            //
            // check for active in last state
            else if (this.workflowInfo.workflowState.equals (WorkflowConstants.STATE_OPEN_RUNNING_LASTSTATE))
            {
                // set indicator to active + laststate
                wfActive = true;
                wfLastState = true;
            } // else if
        } // if

        // if workflow is not active:
        // check if this object-type is allowed in workflows
        if (!wfActive)
        {
            wfAllowed = this.isWfAllowed ();
        } // if

        // now iterate through object-buttons and disable
        // workflow-buttons according to states set above
        for (int i = 0; i < buttons.length; i++)
        {
            // disable START-button if and only if:
            //   active workflow OR workflow not allowed:
            if (buttons[i] == Buttons.BTN_STARTWORKFLOW &&
                !(wfAllowed && !wfActive))
            {
                buttons[i] = Buttons.BTN_NONE;
            } // if
            // disable FORWARD-button if and only if:
            //   NOT (wfActive AND wfCurrentOwner AND NOT wfLastState)
            else if (buttons[i] == Buttons.BTN_FORWARD &&
                     !(wfActive && wfCurrentOwner && !wfLastState))
            {
                buttons[i] = Buttons.BTN_NONE;
            } // else if
            // disable FINISH-button if and only if:
            //   NOT (wfActive AND wfCurrentOwner AND wfLastState)
            else if (buttons[i] == Buttons.BTN_FINISHWORKFLOW &&
                     !(wfActive && wfCurrentOwner && wfLastState))
            {
                buttons[i] = Buttons.BTN_NONE;
            } // else if
        } // for

        //
        // WORKFLOW-BUTTONS BLOCK (END)
        //
        /////////////////////////////////////////


        // get the last object in history
        // the new array with the buttons
        int[] bnew = new int[buttons.length + 7];
        // get the actual object in history

        bnew[2] = Buttons.BTN_NONE;
        // if user has the rights to view the container
        // and it is not the upper most container (workspace)
        if ((permissions.checkContainerPermissions (Operations.OP_VIEWELEMS)) &&
            (
              (this.containerId != null &&
               this.containerId.type !=
                    this.getTypeCache ().getTypeId (TypeConstants.TC_Domain) &&
               this.oid.type !=
                    this.getTypeCache ().getTypeId (TypeConstants.TC_Workspace))))
        {
            bnew[2] = Buttons.BTN_GOTOCONTAINER;
        } // if

        if (this.getUserInfo ().history.nextElemExists ())
            // there is a next element?
        {
            bnew[1] = Buttons.BTN_GOFORWARD;
        } // if there is a next element
        else                            // no next element
        {
            bnew[1] = Buttons.BTN_NONE;
        } // else no next element

        if (this.getUserInfo ().history.prevElemExists ())
                                        // there is a previous element?
        {
            bnew[0] = Buttons.BTN_BACK;
        } // if there is a previous element
        else                            // no previous element
        {
            bnew[0] = Buttons.BTN_NONE;
        } // else no previous element


        // copy the buttons array
        int i;
        for (i = 0; i < buttons.length; i++)
        {
            bnew[i + 3] = buttons[i];
        } // for
        i += 3;
        bnew[i++] = Buttons.BTN_SEARCH;

        //
        // adding HELP button
        //
        bnew[i] = Buttons.BTN_HELP;
        buttons = bnew;

/*
String traceStr = "";
for (i = 0; i < buttons.length; i++)
{
    traceStr += Integer.toString (buttons[i]);
    traceStr += ",";
} // for
trace ("- all buttons: (bnew)" + traceStr);
*/

        if (isContentView && (this.getElementSize () == 0))
        {
            for (int z = 0; z < buttons.length; z++)
            {
                if ((buttons[z] == Buttons.BTN_LIST_CUT) ||
                    (buttons[z] == Buttons.BTN_LISTDELETE) ||
                    (buttons[z] == Buttons.BTN_LIST_COPY) ||
                    (buttons[z] == Buttons.BTN_LISTDELETERIGHTS) ||
                    (buttons[z] == Buttons.BTN_DISTRIBUTE))
                {
                    buttons[z] = Buttons.BTN_NONE;
                } // if
            } // for z
        } // if

        return buttons;
    } // buildButtonBar


    /**************************************************************************
     * Is the object type allowed in workflows? <BR/>
     * This method shall be overwritten in subclasses.
     *
     * @return  <CODE>true</CODE> if the object type is allowed in workflows,
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean isWfAllowed ()
    {
        // BusinessObject itself is not allowed in workflows:
        return false;
    } // isWfAllowed


    /**************************************************************************
     * Gets the container rights. <BR/>
     *
     * @return  The access permissions.
     */
    protected AccessPermissions getContainerRights ()
    {
        // get the rights:
        return this.performGetRightsContainerData (this.oid, this.user);
    } // getContainerRights


    /**************************************************************************
     * Builds the call for creating the button bar for the BusinessObject. <BR/>
     * This call must be included into a script like
     * <CODE>script.addScript (getButtonBarCall);</CODE>. <BR/>
     * This method uses the property {@link #oid oid} and the method
     * {@link #getButtonBarCode getButtonBarCode}.
     *
     * @param   isContentView   Create button bar for content view
     *                          (<CODE>true</CODE>) or for info view
     *                          (<CODE>false</CODE>).
     *
     * @return  The call for creating the button bar.
     */
    public final String getButtonBarCall (boolean isContentView)
    {
        if (isContentView)              // content view?
        {
            return
                StringHelpers.replace (
                    StringHelpers.replace (
                        StringHelpers.replace (
                            StringHelpers.replace (BOConstants.CALL_SHOWBUTTONSCONTENT,
                                             UtilConstants.TAG_NAME,
                                             String.valueOf (this.oid)),
                            UtilConstants.TAG_NAME2,
                            this.getButtonBarCode (0, isContentView)),
                        UtilConstants.TAG_NAME3,
                        this.getHistoryListCode ()),
                    UtilConstants.TAG_NAME4,
                    new Integer (this.getUserInfo ().history.getActIndex ()).toString ());
        } // if

        // no content view
        return
            StringHelpers.replace (
                StringHelpers.replace (
                    StringHelpers.replace (
                        StringHelpers.replace (BOConstants.CALL_SHOWBUTTONSINFO,
                                         UtilConstants.TAG_NAME,
                                         String.valueOf (this.oid)),
                        UtilConstants.TAG_NAME2,
                        this.getButtonBarCode (0, isContentView)),
                    UtilConstants.TAG_NAME3,
                    this.getHistoryListCode ()),
                UtilConstants.TAG_NAME4,
                new Integer (this.getUserInfo ().history.getActIndex ()).toString ());
    } // getButtonBarCall


    /**************************************************************************
     * Gets the code for the ButtonBar, which is transmitted to the clients. <BR/>
     * This method converts the byte values of the buttons into a string,
     * which represents the values of the buttons in hex code.
     *
     * @param   representationForm  Kind of representation.
     * @param   isContentView       Display the Info-View buttons or the Content-view buttons
     *
     * @return  A string which contais the hex value of four buttons.
     */
    protected final String getButtonBarCode (int representationForm,
                                             boolean isContentView)
    {
        int[] somebuttons = this.buildButtonBar (representationForm, isContentView);
        // get the length of the button-array
        int maxbuttons = Buttons.BTN_MAX;
        String result = "";
        AccessPermissions permissions = null; // the access permissions;

        // disable all buttons which the user is not allowed to see:
        ButtonElement b = null;

        // get the access permissions:
        permissions = this.getContainerRights ();

        for (int i = 0; i < somebuttons.length; i++)
        {
            b = this.getUserInfo ().buttonBar.getButton (somebuttons[i]);

            if (b == null ||
                !permissions.checkObjectPermissions (b.objectRights) ||
                !permissions.checkContainerPermissions (b.containerRights) ||
                !b.active ||
                (somebuttons[i] == 0))
            {
                somebuttons[i] = Buttons.BTN_NONE;
            } // if

            if (somebuttons[i] == Buttons.BTN_HELP)
            {
                somebuttons[i] = Buttons.BTN_NONE;
            } // if

            // hide the delete, cut, copy and workflow buttons for tab objects
            if (this.isTab ())
            {
                // hide the delete button
                if (somebuttons[i] == Buttons.BTN_DELETE)
                {
                    somebuttons[i] = Buttons.BTN_NONE;
                } // if
                // hide the cut button
                if (somebuttons[i] == Buttons.BTN_CUT)
                {
                    somebuttons[i] = Buttons.BTN_NONE;
                } // if
                // hide the copy button
                if (somebuttons[i] == Buttons.BTN_COPY)
                {
                    somebuttons[i] = Buttons.BTN_NONE;
                } // if
                // hide the start workflow button
                if (somebuttons[i] == Buttons.BTN_STARTWORKFLOW)
                {
                    somebuttons[i] = Buttons.BTN_NONE;
                } // if
                // hide the forward button
                if (somebuttons[i] == Buttons.BTN_FORWARD)
                {
                    somebuttons[i] = Buttons.BTN_NONE;
                } // if
                // hide the finish workflow button
                if (somebuttons[i] == Buttons.BTN_FINISHWORKFLOW)
                {
                    somebuttons[i] = Buttons.BTN_NONE;
                } // if
            } // if (this.containerKind == BOConstants.CONT_PARTOF)

            // hide the delete button:
            if (somebuttons[i] == Buttons.BTN_DELETE &&
                !this.isDeletable ())
            {
                somebuttons[i] = Buttons.BTN_NONE;
            } // if

            // hide the buttons depending on changeable flag:
            if (!this.isChangeable () &&
                (somebuttons[i] == Buttons.BTN_EDIT ||
                 somebuttons[i] == Buttons.BTN_CHECKOUT ||
                 somebuttons[i] == Buttons.BTN_CHECKOUTCONTAINER ||
                 somebuttons[i] == Buttons.BTN_LIST_CUT ||
                 somebuttons[i] == Buttons.BTN_LISTCHANGE ||
                 somebuttons[i] == Buttons.BTN_LISTDELETE ||
                 somebuttons[i] == Buttons.BTN_NEW ||
                 somebuttons[i] == Buttons.BTN_NEWANDREFERENCE ||
                 somebuttons[i] == Buttons.BTN_NEWEXT))
            {
                somebuttons[i] = Buttons.BTN_NONE;
            } // if
        } // for i

        result = Helpers.integerArrayToHex (somebuttons, maxbuttons);

        return result;
    } // getButtonBarCode


    /**************************************************************************
     * Gets the Java Script Code for the list of history items, which is transmitted to the client.
     *
     * @return  A string which contais the hex value of four buttons.
     */
    protected final String getHistoryListCode ()
    {
        StringBuilder builder = new StringBuilder ("[");

        Collection<String> allElemsMapReprColl =
            this.getUserInfo ().history.getAllElemsMapReprColl ();

        Iterator<String> it = allElemsMapReprColl.iterator ();
        while (it.hasNext ())
        {
            builder.append (it.next ()).append (",");
        } // while

        // check if there is at least one entry
        if (allElemsMapReprColl.size () > 0)
        {
            // replace the last ',' by ']'
            builder.replace (builder.length () - 1, builder.length (), "]");
        } // if
        else
        {
            builder.append ("]");
        } // else

        return builder.toString ();
    } // getHistoryListCode


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
        int[] buttons = this.typeObj.getInfoButtons ();

        // check if there are some buttons set:
        if (buttons == null)
        {
            // set common buttons:
            buttons = new int[]
            {
                Buttons.BTN_EDIT,
                Buttons.BTN_DELETE,
                Buttons.BTN_CUT,
                Buttons.BTN_COPY,
                Buttons.BTN_DISTRIBUTE,
                Buttons.BTN_STARTWORKFLOW,
                Buttons.BTN_FORWARD,
                Buttons.BTN_FINISHWORKFLOW,
                Buttons.BTN_SEARCH,
//              Buttons.BTN_HELP,
//              Buttons.BTN_EXPORT,
            }; // buttons
        } // if

        // return button array:
        return buttons;
    } // setInfoButtons


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
        int[] buttons = this.typeObj.getContentButtons ();

        // check if there are some buttons set:
        if (buttons == null)
        {
            // set common buttons:
            buttons = new int[]
            {
                Buttons.BTN_NEW,
                Buttons.BTN_PASTE,
                Buttons.BTN_SEARCH,
//                Buttons.BTN_HELP,
                Buttons.BTN_LISTDELETE,
                Buttons.BTN_REFERENCE,
            }; // buttons
        } // if

        // return button array:
        return buttons;
    } // setContentButtons


    /**************************************************************************
     * Get the tabs which are not displayed for the current object. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * tabs that can be displayed. <BR/>
     *
     * @return  An array with tab codes that are not allowed to be displayed.
     */
    protected String[] getDisabledTabs ()
    {
/*
        // define tabs which shall not be displayed:
        String[] tabs =
        {
            TabConstants.TC_INFO,
            TabConstants.TC_RIGHTS,
            "abcde"
        }; // tabs

        // return tab array:
        return (tabs);
*/
        return null;
    } // getNotAllowedTabs


    /**************************************************************************
     * Show a delete confirmation message to the user. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    protected void performShowDeleteConfirmation (int representationForm)
    {
        int countReferences = 0;        // number of references
        Page page = new Page ("DeleteConfirmation", false);
        String url = this.getBaseUrl () +
            HttpArguments.createArg (BOArguments.ARG_FUNCTION, AppFunctions.FCT_OBJECTDELETE) +
            HttpArguments.createArg (BOArguments.ARG_OID, "" + this.oid) +
            HttpArguments.createArg (BOArguments.ARG_CONTAINERID, "" + this.containerId);
        String message = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
            this.msgDeleteConfirm, env); // the message for the user

        // check if there are any references to the objects which shall be
        // deleted:
        countReferences = this.checkReferences (true);

        // set the correct message:
        if (countReferences > 0)        // at least one reference found?
        {
            message = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                this.msgDeleteConfirmRef,
                new String[] {"" + countReferences}, env);
        } // if at least one reference found

        // show script:
        ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
        script.addScript (
            "if (confirm (\"" +
            message + 
            "\"))\n" +
            "top.callUrl (\"" + url + "\", null, null, \"" + this.frmSheet + "\");\n\n");
        page.body.addElement (script);

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e.getMsg (), this.app, this.sess, this.env);
        } // catch
    } // performShowDeleteConfirmation


    /**************************************************************************
     * Performs multiple-delete of expired objects. <BR/>
     * All objects that have been marked via their appropriate checkboxes
     * in the clean form will be marked deleted.
     *
     * @param   representationForm  Representation form.
     */
    protected void performClean (int representationForm)
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        int rowCount = 0;
        Page page = new Page ("Perform Clean", false);
        GroupElement gel = new GroupElement ();
        NewLineElement nl = new NewLineElement ();

        // open db connection -  only workaround - db connection must
        // be handled somewhere else
        action = this.getDBConnection ();

        // create the SQL String to select all tuples
        // workaround: there are no right checks done

        StringBuffer queryStr =
            new StringBuffer ("SELECT  o.oid, o.name, o.typeName, o.validUntil ")
                .append (" FROM   ibs_Object o ")
                .append (" WHERE (")
                .append (SQLHelpers.getDateDiff (SQLHelpers.getActDateTime (),
                                             new StringBuffer ("validUntil"),
                                             SQLConstants.UNIT_DAY))
                .append (" < 0)")
                .append (" AND    o.state <> ").append (States.ST_DELETED)
                .append (" ORDER BY validUntil DESC ");

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            //execute the query
            rowCount = action.execute (queryStr, false);

            // empty resultset or error?
            if (rowCount <= 0)
            {
                gel.addElement (new TextElement (MultilingualTextProvider
                    .getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_NO_ELEMENTS_FOUND, this.env)));
            } // if
            // everything ok - go on
            else
            {
                // get tuples out of db
                while (!action.getEOF ())
                {
                    // get the oid from the select query
                    String oidStr = action.getString ("oid");
                    // get the OID_<oid> parameter that indicate whether the object
                    // has been marked for deletion
                    try
                    {
                        // try to get the value from the checkbox
                        String param = this.env.getParam (oidStr);

                        if (param != null && param.equalsIgnoreCase ("1"))
                        {
                            try
                            {
                                // create a business object with the id we got
                                BusinessObject bo = new BusinessObject (new OID (oidStr), this.user);
                                //delete the object
                                bo.performDeleteData (Operations.OP_DELETE);
                                gel.addElement (new TextElement ("'" + action.getString ("name") + "' " + MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_HASBEENDELETED, env)));
                                gel.addElement (nl);
                            } // try
                            catch (IncorrectOidException e) // incorrect oid
                            {
                                gel.addElement (new TextElement ("'" + action.getString ("name") + "' " + MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_COULDNOTBEDELETED, env)));
                                gel.addElement (nl);
                            } // catch
                            catch (NoAccessException e) // no access to object allowed
                            {
                                gel.addElement (new TextElement ("'" + action.getString ("name") + "' " + MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_COULDNOTBEDELETED, env)));
                                gel.addElement (nl);
                            } // catch
                            catch (ObjectNotAffectedException e)
                            {
                                // send corresponding message to the user:
                                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                                    BOMessages.ML_MSG_NOTALLAFFECTED, this.env),
                                    this.app, this.sess, this.env);
                            } // catch
                            catch (DependentObjectExistsException e)
                            {
                                // send corresponding message to the user:
                                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                                    BOMessages.ML_MSG_DEPENDENTOBJECTEXISTS, this.env),
                                    this.app, this.sess, this.env);
                            } // catch
                        } // if
                    } //try
                    catch (Exception e)
                    {
                        IOHelpers.printError ("performClean", this, e, true);
                        gel.addElement (new TextElement ("'" + action.getString ("name") + "' " + MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_COULDNOTBEDELETED, env) + e.toString ()));
                        gel.addElement (nl);
                    } // catch

                    // step one tuple ahead for the next loop
                    action.next ();
                } // while
            } //else

            // the last tuple has been processed
            // end transaction
            action.end ();

            // add the outer table to the page
            page.body.addElement (gel);

            // build the page and show it to the user:
            try
            {
                page.build (this.env);
            } // try
            catch (BuildException e)
            {
                IOHelpers.showMessage (e.getMsg (), this.app, this.sess, this.env);
            } // catch

        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (dbErr, this.app, this.sess, this.env);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally
    } // performClean


    /**************************************************************************
     * Generates a Selection Box with all groups available. <BR/>
     *
     * @param   groupsFilter   filter for the query
     * @param   activeGroupOID OID of actually set group to be marked in the selection box
     *
     * @return  ???
     */
    protected SelectElement createGroupsSelectionBox (String groupsFilter,
                                                      String activeGroupOID)
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        // set view to use for the query
        String viewContent = "v_GroupContainer_01$content";
        // type of operation
        int operation = Operations.OP_VIEW;

        SelectElement sel;
        int rowCount;
        String activeGroupString = "";
        activeGroupString = activeGroupOID;

        sel = new SelectElement (BOArguments.ARG_GROUPS, false);
        sel.size = 10;

        // open db connection -  only workaround - db connection must
        // be handled somewhere else
        action = this.getDBConnection ();

        // create the SQL String to select all tuples

        StringBuffer queryStr =
            new StringBuffer ("SELECT DISTINCT v.id AS oid, v.name AS name")
                .append (" FROM  ").append (viewContent).append (" v")
//---------  HACK ----------------------
                .append (" WHERE   v.domainId = ").append (this.getUser ().domain)
                .append (SQLHelpers.getStringCheckRights (operation))
// ------------ HACK -------------------
                .append (" AND   v.userId  =").append (this.getUser ().id);

        if (groupsFilter != null && groupsFilter.length () > 0)
        {
            queryStr
                .append (" AND ").append (SQLHelpers.getQueryConditionString (
                    "v.name", SQLConstants.MATCH_SUBSTRING, groupsFilter, false));
        } // if
        queryStr.append (" ORDER BY name ");

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            rowCount = action.execute (queryStr, false);

            // empty resultset?
            if (rowCount == 0)
            {
                return sel;
            } // if
            // error while executing?
            else if (rowCount < 0)
            {
                // ERROR!
                return sel;
            } // else if
            // everything ok - go on

            // get tuples out of db
            while (!action.getEOF ())
            {
                // create entries in list
                String oid = action.getString ("oid");
                if (oid.equalsIgnoreCase (activeGroupString))
                {
                    sel.addOption (action.getString ("Name"),
                        action.getString ("oid"), true);
                } //if
                else
                {
                    sel.addOption (action.getString ("Name"), action.getString ("oid"));
                } //else
                // step one tuple ahead for the next loop
                action.next ();
            } // while

            // the last tuple has been processed
            // end transaction
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (dbErr, this.app, this.sess, this.env);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        return sel;
    } // createGroupsSelectionBox


    /**************************************************************************
     * Generates a Vector with all users belonging to a certain group. <BR/>
     * In case there has no group id be specified all users will be listed
     *
     * @param   groupID       id (not OID) of a group (null ... list all users)
     * @param   usersFilter   Filterstring for the query
     *
     * @return  The users vector.
     */
    protected Vector<String[]> createUsersVector (String groupID, String usersFilter)
    {
        Vector<String[]> result = new Vector<String[]> ();
        SQLAction action = null;        // the action object used to access the
                                        // database
        String viewContent = "v_UserContainer_01$content";
                                        // set view to use for the query
        int rowCount;
        StringBuffer queryStr;

        // create the SQL String to select all tuples
        // workaround: there are no right checks done
        queryStr =
            new StringBuffer ("SELECT DISTINCT v.oid, u.fullname as name")
                .append (" FROM  ").append (viewContent).append (" v, ibs_user u");

        // check if there is a group filter set:
        if (groupID != null && groupID.length () > 0) // group set?
        {
            queryStr
                .append (", ibs_groupUser gu")
                .append (" WHERE  gu.groupId = ").append (groupID)
                .append (" AND    gu.userId = u.id ");
        } // if group set
        else                            // no group as filter
        {
            queryStr
                .append (" WHERE  u.domainId = ").append (this.getUser ().domain);
        } // else no group as filter

        queryStr
            .append (" AND v.userId = ").append (this.getUser ().id)
            .append (" AND v.oid = u.oid ");

        // check if a user name filter has been set:
        if (usersFilter != null && usersFilter.length () > 0)
        {
            // check if the filter starts and "(" and ends with ")"
            // which indicates a list of values:
            if (usersFilter.charAt(0) == '(' && 
                usersFilter.charAt(usersFilter.length() - 1) == ')')
            {
                queryStr
                    .append (" AND ").append (SQLHelpers.getQueryConditionString (
                        "u.fullname", SQLConstants.MATCH_IN, usersFilter, false));
            } // if
            // check if the filter contains a "," which also indicates a list of
            // values (but missing "(...)"):
            else if (usersFilter.indexOf (",") > -1)
            {
                queryStr
                    .append (" AND ").append (SQLHelpers.getQueryConditionString (
                        "u.fullname", SQLConstants.MATCH_IN, "(" + usersFilter + ")", false));
            } // else if
            else
            {
                queryStr
                    .append (" AND ").append (SQLHelpers.getQueryConditionString (
                        "u.fullname", SQLConstants.MATCH_SUBSTRING, usersFilter, false));
            } // else
        } // if
        queryStr.append (" ORDER BY name ");

        // open db connection -  only workaround - db connection must
        // be handled somewhere else
        action = this.getDBConnection ();

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            rowCount = action.execute (queryStr, false);

            // empty resultset?
            if (rowCount == 0)
            {
                return result;
            } // if
            // error while executing?
            else if (rowCount < 0)
            {
                return result;
            } // else if
            // everything ok - go on

            // get tuples out of db
            while (!action.getEOF ())
            {
                // create entries in list
                String[] t = new String[2];
                t[0] = action.getString ("name");
                t[1] = action.getString ("oid");
                result.addElement (t);
                // step one tuple ahead for the next loop
                action.next ();
            } // while

            // the last tuple has been processed
            // end transaction
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (dbErr, this.app, this.sess, this.env);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        return result;
    } // createUsersVector


    /**************************************************************************
     * Generates a Vector with all users belonging to some groups. <BR/>
     * In case there has no group id be specified all users will be listed.
     *
     * @param   groupIDs    Ids (not OID) of one or several groups
     *                      (null ... list all users)
     * @param   usersFilter User fullnames to be filtered. Only the users,
     *                      which are mentioned within the filter list, are
     *                      listed.
     *                      If the list is <code>null</code> all users are
     *                      returned.
     *
     * @return  The users vector.
     */
    protected Vector<String[]> createUsersVector (String[] groupIDs,
                                                   String[] usersFilter)
    {
        Vector<String[]> result = new Vector<String[]> ();
        SQLAction action = null;        // the action object used to access the
                                        // database
        String viewContent = "v_UserContainer_01$content";
                                        // set view to use for the query
        int rowCount;
        StringBuffer queryStr;

        // create the SQL String to select all tuples
        // workaround: there are no right checks done
        queryStr =
            new StringBuffer ("SELECT DISTINCT v.oid, u.fullname as name")
                .append (" FROM  ").append (viewContent).append (" v, ibs_user u");

        // check if there is a group filter set:
        if (groupIDs != null && groupIDs.length > 0) // group(s) set?
        {
            queryStr
                .append (", ibs_groupUser gu")
                .append (" WHERE ").append (SQLHelpers.getQueryConditionString (
                    "gu.groupId", SQLConstants.MATCH_IN,
                    StringHelpers.stringArrayToString (groupIDs, ","), false))
                .append (" AND    gu.userId = u.id ");
        } // if group set
        else                            // no group as filter
        {
            queryStr
                .append (" WHERE  u.domainId = ").append (this.getUser ().domain);
        } // else no group as filter

        queryStr
            .append (" AND v.userId = ").append (this.getUser ().id)
            .append (" AND v.oid = u.oid ");

        // check if a user name filter has been set:
        if (usersFilter != null && usersFilter.length > 0)
        {
            queryStr
                .append (" AND ").append (SQLHelpers.getQueryConditionString (
                    "u.fullname", SQLConstants.MATCH_IN, 
                    StringHelpers.stringArrayToString (usersFilter, new StringBuffer(","), 
                        new StringBuffer("'")), false));

        } // if
        queryStr.append (" ORDER BY name ");

        // open db connection -  only workaround - db connection must
        // be handled somewhere else
        action = this.getDBConnection ();

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            rowCount = action.execute (queryStr, false);

            // empty resultset?
            if (rowCount == 0)
            {
                return result;
            } // if
            // error while executing?
            else if (rowCount < 0)
            {
                return result;
            } // else if
            // everything ok - go on

            // get tuples out of db
            while (!action.getEOF ())
            {
                // create entries in list
                String[] t = new String[2];
                t[0] = action.getString ("name");
                t[1] = action.getString ("oid");
                result.addElement (t);
                // step one tuple ahead for the next loop
                action.next ();
            } // while

            // the last tuple has been processed
            // end transaction
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (dbErr, this.app, this.sess, this.env);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        return result;
    } // createUsersVector


    /**************************************************************************
     * Generates a Selection Box out of a Vector with String[2] elements. <BR/>
     *
     * @param   arg       name of the Selection Box.
     * @param   elems     Vector with the objects.
     *
     * @return  The selection box for the layout.
     */
    protected SelectElement createSelectionBox (String arg, Vector<String[]> elems)
    {
        return this.createSelectionBox (arg, elems, "");
    } // createSelectionBox


    /**************************************************************************
     * Generates a Selection Box out of a Vector with String[2] elements. <BR/>
     *
     * @param   arg       name of the Selection Box.
     * @param   elems     Vector with the objects.
     * @param   selected  String with the value of the selected element.
     *
     * @return  The selection box for the layout.
     */
    protected SelectElement createSelectionBox (String arg, Vector<String[]> elems, String selected)
    {
        SelectElement sel = new SelectElement (arg, true);
        sel.size = 13;
        String[] elem = null; // a element of the Vector

        // fill the SelectElement with values
        Enumeration<String[]> vectEnum = elems.elements ();

        if (!vectEnum.hasMoreElements ())
        {
            // the Vector does not have any elements
            sel.addOption ("      ", " ");
        } // if
        else
        {
            while (vectEnum.hasMoreElements ())
            {
                elem = vectEnum.nextElement ();
                if (elem != null)
                {
                    // create Option of the SelectElement
                    if (selected.length () > 0 &&
                        elem[1].equalsIgnoreCase (selected))
                    {
                        sel.addOption (elem[0], elem[1], true);
                    } // if
                    else
                    {
                        sel.addOption (elem[0], elem[1]);
                    } // else
                } // if
            } // while
        } // else

        // return the selectionBox
        return sel;
    } // createSelectionBox


    /**************************************************************************
     * Generates a Selection Box out of a Vector with String[2] elements
     * without the elemets which are present in the second Vector given. <BR/>
     *
     * @param   arg     name of the Selection Box.
     * @param   elems   Vector with the objects.
     * @param   rmElems Vector with the objects not to process.
     *
     * @return  The selection box for the layout.
     */
    protected SelectElement createSelectionBox (String arg,
                                                Vector<String[]> elems,
                                                Vector<String[]> rmElems)
    {
        return this.createSelectionBox (arg, elems, rmElems, "");
    } // createSelectionBox


    /**************************************************************************
     * Generates a Selection Box out of a Vector with String[2] elements
     * without the elemets which are present in the second Vector given. <BR/>
     *
     * @param   arg     name of the Selection Box.
     * @param   elems   Vector with the objects.
     * @param   rmElems Vector with the objects not to process.
     * @param   selected String with the value of the selected element.
     *
     * @return  The selection box for the layout.
     */
    protected SelectElement createSelectionBox (String arg,
                                                Vector<String[]> elems,
                                                Vector<String[]> rmElems,
                                                String selected)
    {
        SelectElement sel = new SelectElement (arg,  true);
        sel.size = 13;
        String[] elem = null; // a element of the Vector

        // fill the SelectElement with values
        Enumeration<String[]> vectEnum = elems.elements ();

        if (!vectEnum.hasMoreElements ())
        {
            // the Vector does not have any elements
            sel.addOption ("      ", " ");
        } // if
        else
        {
            while (vectEnum.hasMoreElements ())
            {
                elem = vectEnum.nextElement ();
                if (elem != null)
                {
                    Enumeration<String[]> e2 = rmElems.elements ();
                    boolean found = false;
                    while (e2.hasMoreElements () && !found)
                    {
                        String[] t2 = e2.nextElement ();
                        if (t2 != null)
                        {
                            // is the oid the same?
                            if (t2[1].equalsIgnoreCase (elem[1]))
                            {
                                found = true;
                            } // if
                        } // if
                    } // while
                    // create Option of the SelectElement
                    if (!found)
                    {
                        // create Option of the SelectElement
                        if (selected.length () > 0 &&
                            elem[1].equalsIgnoreCase (selected))
                        {
                            sel.addOption (elem[0], elem[1], true);
                        } // if
                        else
                        {
                            sel.addOption (elem[0], elem[1]);
                        } // else
                    } // if
                } // if
            } // while
        } // else

        // return the selectionBox
        return sel;
    } // createSelectionBox


    /**************************************************************************
     * Generates a Selection Box with a list of users out of a list of
     * user IDs. <BR/>
     * This method is also used to construct the receivers selection box
     * within the object distribute form. <BR/>
     *
     * @param   receivers   Array of user OIDs.
     *
     * @return  The selection box for the layout.
     */
    protected SelectElement createUsersSelectionBox (OID[] receivers)
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        int rowCount;

        SelectElement sel = new SelectElement (BOArguments.ARG_RECEIVERS, true);
        sel.size = 13;

        // construct the filter string for the query
        String receiversFilter = " (";
        String delim = "";

        for (int i = 0; i < receivers.length; i++)
        {
            if (receivers[i] != null)
            {
                receiversFilter += delim + receivers[i].toStringQu ();
                delim = ",";
            } // if
        } // for

        receiversFilter = receiversFilter + ")";

        // check if there are any elements to be retrieved from the DB
        if (!receiversFilter.equalsIgnoreCase (" ()"))
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();

            // create the SQL String to select all tuples
            // workaround: there are no right checks done
            StringBuffer queryStr =
                new StringBuffer ("SELECT DISTINCT u.oid as oid, u.fullname as name")
                    .append (" FROM   ibs_User u ")
                    .append (" WHERE  u.state = ").append (States.ST_ACTIVE)
                    .append (" AND    u.oid IN ").append (receiversFilter)
                    .append (" ORDER BY name ");

            // execute the queryString, indicate that we're not performing an
            // action query:
            try
            {
                rowCount = action.execute (queryStr, false);

                // empty result set?
                if (rowCount == 0)
                {
                    sel.addOption ("           ", "");
                    return sel;
                } //if
                // error while executing?
                else if (rowCount < 0)
                {
                    // ERROR!
                    sel.addOption ("           ", "");
                    return sel;
                } //else if
                // everything ok - go on

                // get tuples out of db
                while (!action.getEOF ())
                {
                    // create entries in list
                    sel.addOption (action.getString ("Name"), action.getString ("oid"));
                    // add the data for this receivers to the Vector holding the
                    // receivers in the session also
                    String[] t = new String[2];
                    t[0] = action.getString ("Name");
                    t[1] = action.getString ("oid");
                    this.sess.receivers.addElement (t);
                    // step one tuple ahead for the next loop
                    action.next ();
                } // while

                // the last tuple has been processed
                // end transaction
                action.end ();
            } // try
            catch (DBError dbErr)
            {
                // an error occurred - show name and info
                IOHelpers.showMessage (dbErr, this.app, this.sess, this.env);
            } // catch
            finally
            {
                // close db connection in every case -  only workaround - db connection must
                // be handled somewhere else
                this.releaseDBConnection (action);
            } // finally
        } // if (! receiversFilter.equalsIgnoreCase (" ()")
        else
        {
            sel.addOption ("           ", "");
        } // else

        return sel;
    } // createUsersSelectionBox


    /**************************************************************************
     * Generates a Activities Box with a list of possible activities depending
     * on the objecttype. <BR/>
     *
     * @param   pa          Preselected activity.
     *
     * @return  The selection box for the layout.
     */
    protected SelectElement createActivitiesSelectionBox (String pa)
    {
        SelectElement sel;

        sel = new SelectElement (BOArguments.ARG_ACTIVITIES, false);
        sel.size = 1;
//        sel.addOption ("          ","");
        // add the activities
        this.addActivities (sel, pa);

        return sel;
    } // createActivitiesSelectionBox


    /**************************************************************************
     * Defines the activity entries being displayed in the activities selection
     * box. <BR/>
     * This method must be overwritten in subclasses in order to provide
     * type specific activities lists. <BR/>
     *
     * @param   sel         Selection box element.
     * @param   pa          Preselected activity.
     */
    protected void addActivities (SelectElement sel, String pa)
    {
        for (int i = 0; i < BOTokens.ML_ACTIVITIES_ARRAY.length; i++)
        {
            this.addActivity (sel, pa, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ACTIVITIES_ARRAY[i], env));
        } // for (int i = 0; i < MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ACTIVITIES, env)[].length; i++)
    } // addActivities


     /*************************************************************************
     * Generates an entry in the activities selection box. <BR/>
     *
     * @param   sel         Selection box element.
     * @param   pa          Selected activity.
     * @param   activity    Activity string to add.
     */
    protected void addActivity (SelectElement sel, String pa, String activity)
    {
        // check if entry has been selected
        if (activity.equals (pa))
        {
            sel.addOption (activity, activity, true);
        } // if
        else
        {
            sel.addOption (activity, activity);
        } // else
    } // addActivity



    ///////////////////////////////////////////////////////////////////////////
    // import / export methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Reads an oid from a DataElement. <BR/>
     *
     * @param   dataElement     The DataElement to read the data from.
     * @param   fieldName       Name of field to be read.
     *
     * @return  The oid or <CODE>null</CODE> if the field was not found.
     *
     * @throws  RuntimeException
     *          There was an error when converting the field value to an oid.
     */
    public OID readImportOid (DataElement dataElement, String fieldName)
        throws RuntimeException
    {
        OID oid = null;                 // the found oid

        try
        {
            // check if the field exists:
            if (dataElement.exists (fieldName)) // field exists?
            {
                oid = new OID (dataElement.getImportStringValue (fieldName));
            } // if field exists
        } // try
        catch (IncorrectOidException e)
        {
            throw new RuntimeException (e.toString ());
        } // catch

        // return the oid:
        return oid;
    } // readImportOid


    /**************************************************************************
     * Reads a key from a DataElement and uses the KeyMapping to convert it
     * into the corresponding oid. <BR/>
     *
     * @param   dataElement     The DataElement to read the data from.
     * @param   fieldName       Name of field to be read.
     *
     * @return  The oid or <CODE>null</CODE> if the field was not found or the
     *          KeyMapper does not contain an oid for this key.
     *
     * @see ibs.di.KeyMapper#performResolveMapping (ibs.di.KeyMapper.ExternalKey)
     */
    public OID readImportOidFromKey (DataElement dataElement, String fieldName)
    {
        OID oid = null;                 // the found oid
        String id = null;               // the key mapping id
        String domain = null;           // the id domain for the key
        ValueDataElement value = null;  // the value element

        // get the key value:
        value = dataElement.getValueElement (fieldName);

        if (value != null)              // found the key?
        {
            // get the id and domain from the reference value (EXTKEX)
            id = value.value;
            domain = value.p_domain;
            
            // retrieve the oid by domain and id
            BOHelpers.getOidByExtKey (domain, id, env);
        } // if found the key

        // check if the oid was imported successfully:
        if (oid == null)                // error when importing key?
        {
            // Sorry! But there is no other possibility to report errors.
            // workaround!!!
            throw new RTExceptionInvalidLink (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, this.env));
        } // if error when importing key

        // return the oid:
        return oid;
    } // readImportOidFromKey


    /**************************************************************************
     * Reads a path from a DataElement and tries to get the oid of the object
     * to which this path belongs. <BR/>
     *
     * @param   dataElement     The DataElement to read the data from.
     * @param   fieldName       Name of field to be read.
     *
     * @return  The oid or <CODE>null</CODE> if the field was not found or no
     *          path was defined.
     *
     * @see #resolveObjectPath (String, boolean[])
     */
    public OID readImportOidFromPath (DataElement dataElement, String fieldName)
    {
        OID oid = null;                 // the found oid
        String path = null;             // the path

        // check if the field exists:
        if (dataElement.exists (fieldName)) // the field exists?
        {
            // get the path of the object:
            path = dataElement.getImportStringValue (fieldName);

            if (path != null)           // got the path?
            {
                // get the OID of the object:
                boolean[] isContainer = new boolean[1];
                oid = BOHelpers.resolveObjectPath (path, isContainer, this, this.env);
            } // if got the path
        } // if the field exists

        // return the oid:
        return oid;
    } // readImportOidFromPath


    /**************************************************************************
     * Reads the data of the object from an import element. <BR/>
     *
     * @param   dataElement   The importElement to read the data from.
     */
    public void readImportData (DataElement dataElement)
    {
        if (dataElement.isNameGiven ())
        {
            this.name = dataElement.name;
        } // if

        if (dataElement.isDescriptionGiven ())
        {
            this.description = dataElement.description;
        } // if

        if (dataElement.isValidUntilGiven () &&
            dataElement.validUntil.length () > 0)
        {
            this.validUntil = DateTimeHelpers.stringToDate (dataElement.validUntil);
        } // if

        if (dataElement.isShowInNewsGiven ())
        {
            this.showInNews = dataElement.getShowInNews ();
        } // if
    } // readImportData


    /**************************************************************************
     * Creates the files from the dataElement. <BR/>
     * This method must be overwritten by all classes that have files
     * attached. <BR/>
     *
     * @param dataElement     the dataElement to read the data from
     */
    public void readImportFiles (DataElement dataElement)
    {
        // this method may be overwritten in sub classes
    } // readImportFiles


    /**************************************************************************
     * Writes the data of the object to an import element. <BR/>
     *
     * @param   dataElement   The dataElement to write the data to.
     */
    public void writeExportData (DataElement dataElement)
    {
        // get the type object from the type cache and set the
        // type code for the export.
        Type typeObj = this.getTypeCache ().getType (this.type);
        dataElement.p_typeCode = typeObj.getCode ();
        dataElement.typename = this.typeName;

        if (this.isTab ())
        {
            // mark the object as a tab
            dataElement.p_isTabObject = true;
            // get the tab code for the tab object
            dataElement.p_tabCode = this.performGetTabCodeData (this.oid);
        } // if (this.containerKind == BOConstants.CONT_PARTOF)

        dataElement.id = this.oid.toString ();
        dataElement.name = this.name;
        dataElement.description = this.description;
        dataElement.validUntil = DateTimeHelpers.dateToString (this.validUntil);
        dataElement.showInNews = "" + this.showInNews;
    } // writeExportData


    /**************************************************************************
     * Finish the export. <BR/>
     * This method is called when the export of the object is finished. <BR/>
     * The method may be overwritte in subclasses.
     *
     * @throws  ExportException
     *          An exception occurred within the method.
     */
    public void finishExport () throws ExportException
    {
        // nothing to do
    } // finishExportData


    ///////////////////////////////////////////////////////////////////////////
    // messages
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Show a message that the user does not have the required access to this
     * object. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     */
    public void showNoAccessMessage (int operation)
    {
        String message = "";            // String containing the message

        if (this.name == null)
        {
            // build the message:
            message = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, BOMessages.MSG_NOACCESS_NONAME
                [ (int) (Math.log (operation) / Math.log (2))], env);
        } // if
        else
        {
            // build the message:
            String [] messageArguments = {this.name};
            message = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, BOMessages.MSG_NOACCESS
                [ (int) (Math.log (operation) / Math.log (2))],
                messageArguments, env);
        } // else

        // show the message to the user:
        IOHelpers.showMessage (message, this.app, this.sess, this.env);
    } // showNoAccessMessage


    /**************************************************************************
     * Show a message that the user wanted to create an object which name is
     * already given (applies to the object user). <BR/>
     */
    public void showAlreadyDeletedMessage ()
    {
        String message = "";            // String containing the message

        // build the message:
        message = MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
            UtilExceptions.ML_E_OBJECTDELETED, env);

        // show the message to the user:
        IOHelpers.showMessage (message, this.app, this.sess, this.env);
    } // showAlreadyDeletedMessage


    /**************************************************************************
     * Show a PopupMessage that the user wanted to create an object whose name
     * is already given (applies to the object user). <BR/>
     */
    public void showNameAlreadyGivenMessage ()
    {
        String message = "";            // String containing the message
        ScriptElement messageScript = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
                                        // Script for message output
        // build the message:
        message = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
            BOMessages.ML_MSG_NAMEALREADYGIVEN, new String[] {this.name}, this.env);
        // show the message as JavaScript to the user:
        messageScript.addScript (
            "alert (\"" + IOHelpers.prepareJavaScriptMessage (message) +
            "\");\n");
        // build the page and show it to the user:
        try
        {
            StringBuffer buf = new StringBuffer ();
            messageScript.build (this.env, buf);
            this.env.write (buf.toString ());
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e.getMsg (), this.app, this.sess, this.env);
        } // catch
    } // showNameAlreadyGivenMessage


    /**************************************************************************
     * Show a message that the function did not affect all possible Objects.
     * <BR/>
     */
    public void showObjectNotAffectedMessage ()
    {
        String message = "";            // String containing the message

        // build the message:
        message = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
            BOMessages.ML_MSG_NOTALLAFFECTED, this.env);

        // show the message to the user:
        IOHelpers.showMessage (message, this.app, this.sess, this.env);
    } // showObjectNotAffectedMessage


    /**************************************************************************
     * Show a message that the was not found. <BR/>
     */
    public void showObjectNotFoundMessage ()
    {
        String message = "";            // String containing the message

        // build the message:
        message = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
            BOMessages.ML_MSG_OBJECTNOTFOUND, this.env);

        // show the message to the user:
        IOHelpers.showMessage (message, this.app, this.sess, this.env);
    } // showObjectNotFoundMessage


    /**************************************************************************
     * Show a popup message that the user does not have the required access to
     * this object. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     */
    public void showNoAccessPopupMessage (int operation)
    {
        String message = "";            // String containing the message

        // build the message:
        String[] messageArguments = {this.name};
        message = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
            BOMessages.MSG_NOACCESS [ (int) (Math.log (operation) / Math.log (2))],
            messageArguments, env);

        // show the message to the user:
        this.showPopupMessage (message);
    } // showNoAccessPopupMessage


    /**************************************************************************
     * Evaluates a XPath expression. <BR/>
     * Gets the value which is referenced by the XPath out of the DOM tree.
     *
     * @param   param       A vector containing the parameters for the
     *                      transformation.
     *                      field 1: XPath expression
     *
     * @return  Result vector with holds the result code (1. element)
     *          and the result message (2. element) as Strings.
     */
    public Vector<String> performXPath (Vector<String> param)
    {
        // the 1. element in the result vector holds the result code.
        // the 2. element in the result vector holds the result message.
        String resultCode = "-1";
        String resultMsg = "ERROR";

        // set the result vector
        Vector<String> result = new Vector<String> ();
        result.addElement (resultCode);
        result.addElement (resultMsg);

        return result;
    } // performXPath


    /**************************************************************************
     * Performs a transformation via xslt. <BR/>
     * The result of the transformation is a new m2 object with is imported
     * in the container specified in the first element of the parameter vector.
     * The stylesheet file for the transformation is obtained by calling the
     * method getTransformationFileName (). Overwrite this method to set the
     * correct stylesheet.
     *
     * @param   param       a vector containing the parameters for the
     *                      transformation.
     *
     * @return      result vector with holds the result code (1. element)
     *              and the result message (2. element) as strings.
     */
    public Vector<String> performTransformation (Vector<String> param)
    {
        // the 1. element in the result vector holds the result code.
        // the 2. element in the result vector holds the result message.
        String resultCode = "-1";
        String resultMsg = "ERROR";

        // set the result vector
        Vector<String> result = new Vector<String> ();
        result.addElement (resultCode);
        result.addElement (resultMsg);

        return result;
    } // performTransformation


    /**************************************************************************
     * Insert style sheet information in a standard change form. <BR/>
     *
     * @param   page    The into which the style sheets shall be inserted.
     */
    protected void insertChangeFormStyles (Page page)
    {
        // Stylesheetfile wird geladen
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);
    } // insertChangeFormStyles

    /**************************************************************************
     * Returns a Element
     *
     * @param   table     Table to add the references to
     * @param   page     Page to add the specific StyleSheet to
     *
     * @return the Element containing the References-Table
     */
    protected Element showRefs (TableElement table, Page page)
    {
        if ((this.refs != null) && (this.refs.size () > 0))
        {
            // added der Sytelsheets fuer die Reference-Table

            // Stylesheetfile wird geladen
            StyleSheetElement style = new StyleSheetElement ();
            style.importSS = this.sess.activeLayout.path +
                this.env.getBrowser () + "/" +
                this.sess.activeLayout.elems[LayoutConstants.REFERENCES].styleSheet;
            page.head.addElement (style);

            RefContainerElement_01 elem;
            DivElement div = new DivElement ();
            div.classId  = CssConstants.CLASS_REFS;
            TableElement refTable = new TableElement (2);
            div.addElement (refTable);
            div.alignment = IOConstants.ALIGN_RIGHT;
            refTable.classId  = CssConstants.CLASS_REFS;
            refTable.cellpadding = 0;
            refTable.cellspacing = 0;
            refTable.width = HtmlConstants.TAV_FULLWIDTH;
            refTable.cellspacing = 0;
            refTable.borderColor = "#ffffff";
            refTable.borderColorLight = "#ffffff";
            refTable.borderColorDark = "#ffffff";
//            refTable.border = 1;
            refTable.frametypes = "void";
            refTable.ruletype = "none";
            RowElement row;
            TableDataElement td;
            TextElement text;

            row = new RowElement (3);
//            row.addElement (new TableDataElement (new BlankElement ()));
            text = new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SEETOO, env));
            td = new TableDataElement (text);
            td.classId = CssConstants.CLASS_LISTHEADER;
            td.colspan = 3;
            td.nowrap = true;
            row.addElement (td);
            refTable.head = row;

            int i = 1;
            Enumeration<Object> vectEnum = this.refs.elements ();
            while (vectEnum.hasMoreElements ())
            {
                i = ++i % 2;
                elem = (RefContainerElement_01) vectEnum.nextElement ();
                if (elem != null)
                {
                    refTable.addElement (elem.show (BOListConstants.LST_CLASSREFROWS[i], env));
                } // if
            } // while

            if (table != null)
            {
                table.cols++;
                td = new TableDataElement (div);
                td.classId = CssConstants.CLASS_REFS;
                int tableSize = table.getRowNumber ();
                if (tableSize < this.refs.size ())
                                        // references are more than the normal rows
                {
                    RowElement dummy = new RowElement (1);
                    TableDataElement dummy2 =
                        new TableDataElement (new BlankElement ());
                    dummy2.rowspan = this.refs.size () - tableSize + 1;
//                    env.write ("rowspan fuer neue Zeile" + dummy2.rowspan);
                    dummy.addElement (dummy2);
                    table.addElement (dummy);
                    td.rowspan = tableSize + dummy2.rowspan;
//                    env.write ("rowSpan references" + td.rowspan);
                } // if references are more than the normal rows
                else
                {
                    td.rowspan = table.getRowNumber ();
                } // else
                table.addToFirstRow (td);
            } // if
            return div;
        } // if
        return null;
    } // showRefs


    /**************************************************************************
     * Fills the Vector containing the references. <BR/>
     *
     * @param   operation    Operation-right necessary to retrieve the information
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected void performRetrieveRefs (int operation)
        throws NoAccessException
    {
        if (this.isTab ())
        {
            return;
        } // if
        SQLAction action = null;        // the action object used to access the
                                        // database
        RefContainerElement_01 obj;
        // row counter
        int rowCount;
        this.size = 0;

        // empty the elements vector:
        this.refs = new Vector<Object> (5);

        // get the elements out of the database:
        // create the SQL String to select all tuples
        StringBuffer queryStr =
            new StringBuffer ("SELECT DISTINCT *")
                .append (" FROM v_Object$refs")
                .append (" WHERE refCoid = ").append (this.oid.toStringQu ())
                .append (" AND tRefVersionId IN ( ")
                .append (this.getTypeCache ().getTVersionId (TypeConstants.TC_AttachmentContainer))
                .append (",")
                .append (this.getTypeCache ().getTVersionId (TypeConstants.TC_ReferenceContainer))
                .append (")")
                .append (" AND userId = ").append (this.user.id)
                .append (SQLHelpers.getStringCheckRights (operation))
                .append (" ORDER BY name");

//trace ("AJ BusinessObject.performRetrieveRefs QUERY: " + queryStr);

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);

            // empty resultset?
            if (rowCount == 0)
            {
                return;                 // terminate this method
            } // if
            // error while executing?
            else if (rowCount < 0)
            {
                return;
            } // else

            // everything ok - go on
            // get tuples out of db
            while (!action.getEOF ())   // there are tuples left?
            {
                // create a new object:
                obj = new RefContainerElement_01 ();
                obj.setApp (this.app);

                // temporary solution for the problem: what happens if
                // one element occures more then one time in the resultset of
                // the selectstatement - problem is solved in method
                // getContainerElementData (see Class ProductGroup_01)

                // add element to list of elements:
                this.refs.addElement (obj);
                // get and set values for element:
                obj.oid = SQLHelpers.getQuOidValue (action, "oid");
                obj.name = action.getString ("name");
                obj.typeName = action.getString ("typeName");
                obj.isLink = action.getBoolean ("isLink");
                obj.linkedObjectId = SQLHelpers.getQuOidValue (action, "linkedObjectId");
                obj.isNew = action.getBoolean ("isNew");
                obj.icon = action.getString ("icon");
                if ((this.sess != null) && (this.sess.activeLayout != null))
                {
                    obj.layoutpath = this.sess.activeLayout.path;
                } // if
//        obj.description = action.getString ("description");

                int flags;
                try
                {
                    flags = action.getInt ("flags");
                } // try
                catch (DBError e)
                {
                    IOHelpers.printError ("performRetrieveRefs", this, e, true);
                    flags = 0;
                } // catch
                // obj.flags = action.getInt ("flags");

                if ((flags & BOConstants.FLG_HYPERMASTER) == BOConstants.FLG_HYPERMASTER)
                {
                    obj.masterSelect = BOConstants.FLG_HYPERMASTER;
                } // if
                else if ((flags & BOConstants.FLG_FILEMASTER) == BOConstants.FLG_FILEMASTER)
                {
                    obj.masterSelect = BOConstants.FLG_FILEMASTER;
                } // else if

                obj.showInWindow = this.getUserInfo ().userProfile.showFilesInWindows;

                if ((flags & BOConstants.FLG_ISWEBLINK) == BOConstants.FLG_ISWEBLINK)
                {
                    // if the object is a hyperlink with internal weblink then
                    // the content should not be shown in a window but in the
                    // frameset
                    obj.showInWindow = false;
                    obj.isWeblink = true;
                } // if

                // step one tuple ahead for the next loop:
                action.next ();
            } // while

            // the last tuple has been processed
            // end transaction:
            action.end ();
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally
    } // performRetrieveRefs


    /**************************************************************************
     * Retrieves the parent tree for a BusinessObject. <BR/>
     *
     * @param   oid     The oid of the required business object.
     *
     * @return  Leaf ObjectPathNode for this business object.
     */
    protected ObjectPathNode getObjectPath (OID oid)
    {
        ObjectPathNode objectPathNode = null;
        ObjectPathHandler treeHandler = new ObjectPathHandler ();

        try
        {
            objectPathNode = treeHandler.retrieveParentTree (oid);
        } // try
        catch (DBError e)
        {
            objectPathNode = null;
        } // catch

        return objectPathNode;
    } // getObjectPath


    /**************************************************************************
     * retrieves the parent tree of one BusinessObject when it is called
     * for the first time.
     *
     * @return  Leaf ObjectPathNode for this business object.
     */
    protected ObjectPathNode getObjectPath ()
    {
        if (this.p_objectPath == null)
        {
            this.p_objectPath = this.getObjectPath (this.oid);
        } // if

        return this.p_objectPath;
    } // getObjectPath


    /**************************************************************************
     * Creates the string representation of the object path of a business
     * object. <BR/>
     *
     * @param   oid     The oid of the required business object.
     *
     * @return  The object path as string.
     *          <CODE>null</CODE> if there could not be an object path found.
     *
     * @see #getObjectPath ()
     */
    protected String getObjectPathString (OID oid)
    {
        StringBuffer path = new StringBuffer (); // the resulting path
        char pathSep = '/';             // the path separator

        // create objectpath from object to menutab:
        ObjectPathNode node = this.getObjectPath (oid);

        if (node != null)
        {
            // create Vector with oids of all menus
            Vector<OID> menuOids = new Vector<OID> ();

            MenuData_01 menDat = null;
            for (int i = 0; i < this.sess.menus.size (); i++)
            {
                menDat = this.sess.menus.elementAt (i);
                menuOids.addElement (menDat.oid);
            } // for

            // while there is still a parent node
            // and last node was not a menutab
            while (node != null)
            {
                path.append (node.getName ());

                // show only tree until any menutab is reached:
                if (menuOids.contains (node.getOid ()))
                {
                    node = null;
                } // if
                else
                {
                    node = node.getParent ();
                } // else

                // add separator if there will be an additional path element
                if (node != null)
                {
                    path.append (pathSep);
                } // if
            } // while
        } // if node != null

        // return the result:
        return path.toString ();
    } // getObjectPathString


    /**************************************************************************
     * Gets buttons to be shown in NavBar. <BR/>
     *
     * @return  int array with the buttons to be shown in the NavBar
     */
    public int[] getNavItems ()
    {
        int[] navb = null;              // the new array with the buttons
        boolean back = false;           // button to be shown
        int count = 0;                  // how many buttons to be shown

        // show only if the weblink is not active
        if (!this.sess.weblink)
        {
            if (this.getUserInfo ().history.prevElemExists ())
                                        // there is a previous element?
            {
                back = true;
                count++;
            } // if there is a previous element

            // setting up values in buttons array:
            if (back)                 // back button?
            {
                // initializing button array
                navb = new int[count];

                navb[0] = Buttons.BTN_BACK;
            } // if back button
            else                        // no back button
            {
                navb = null;
            } // else no back button
        } // if

        return navb;                    // return buttons array to caller
    } // getNavItems


    /**************************************************************************
     * Perform a selection query against the database. <BR/>
     *
     * @param   query   The query to be performed.
     * @param   list    The list to add the data to.
     */
/*
    protected void performSelectionQueryData (String query, DataElementList list)
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        int rowCount;                   // number of result rows
        int i;                          // loop counter

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = getDBConnection ();
            rowCount = action.execute (queryStr, false);

            if (rowCount <= 0)          // empty resultset or error while
                                        // executing?
                return;                 // terminate this method

            // everything ok - go on

            // get tuples out of db
            i = 0;                      // initialize loop counter

            while ((list.maxElements == 0 || i++ < list.maxElements) &&
                                        // maximum number of tuples not reached?
                   !action.getEOF ())   // there are tuples left?
            {
                // add the current data to the list:
                list.addDataElement (action);
                // step one tuple ahead for the next loop:
                action.next ();
            } // while

            // check if there are more tuples left
            if (!action.getEOF ())
                // indicate that maximum number of elements have been exceeded
                list.areMaxElementsExceeded = true;

            // the last tuple has been processed
            // end transaction:
            action.end ();
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            showMessage (e.getMessage () + e.getError ());
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            releaseDBConnection (action);
        } // finally
    } // performSelectionQueryData
*/


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
        if (!this.isPhysical)
        {
            // there is no physical object on the database possible from the current class
            return null;
        } // if

        // call the object type specific method:
        this.performCheckOutData (Operations.OP_CHANGE);
        return this;
    } // checkOut


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
        if (!this.isPhysical)
        {
            // there is no physical object on the database possible from the current class
            return null;
        } // if

        if (this.checkedOut && this.user.id == this.checkOutUser.id)
        {
            // the user is allowed to check the object in
            // call the object type specific method:
            this.performCheckInData (Operations.OP_CHANGE);
            return this;
        } // if user not allowed

         // raise no access exception
        throw new NoAccessException (MultilingualTextProvider
            .getMessage (UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_NOACCESSEXCEPTION, env));
    } // checkIn


    /**************************************************************************
     * Checks in a Businessoject and returns the oid of the object that shall
     * be displayed afterwards. <BR/>
     *
     * @return  The oid of the object that shall be displayed afterwards.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    public OID checkInReturnDisplayableObject () throws NoAccessException
    {
        BusinessObject boToShow = null; // business object to be displayed

        // perform the checkin:
        boToShow = this.checkIn ();

        this.showPopupMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
            BOMessages.ML_MSG_OBJECTCHECKEDIN, new String[] {boToShow.name}, this.env));

        // return the oid of the object to be displayed:
        return boToShow.containerId;
    } // checkIn


    /**************************************************************************
     * Checks out a Businessoject (WebDAV). <BR/>
     *
     * @return The BusinessObject which was checked out.
     *
     * @throws NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    public BusinessObject webdavCheckOut () throws NoAccessException
    {
        if (!this.isPhysical)
        {
            // there is no physical object on the database possible from the current class
            return null;
        } // if

        // call the object type specific method:
        this.performCheckOutData (Operations.OP_CHANGE);
        return this;
    } // webdavCheckOut


    /**************************************************************************
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
        if (!this.isPhysical)
        {
            // there is no physical object on the database possible from the
            // current class
            return null;
        } // if

        if (this.checkedOut && this.user.id == this.checkOutUser.id)
        {
        // the user is allowed to check the object in
            // call the object type specific method:
            this.performCheckInData (Operations.OP_CHANGE);

            return this;
        } // if user not allowed

         // raise no access exception
        throw new NoAccessException (MultilingualTextProvider
            .getMessage (UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_NOACCESSEXCEPTION, env));
    } // webdavCheckIn


    /**************************************************************************
     * The method should show the edit form before checking in the actual object.
     *
     * @param   function    The function which is called with the OK button.
     *
     * @throws  NoAccessException
     *          The user does not have access to this object to perform the
     *          required operation.
     */
    public void editBeforeCheckIn (int function) throws NoAccessException
    {
        // show the change form and set the next function FCT_CHECKIN
        this.showChangeForm (UtilConstants.REP_STANDARD, function);
    } // editBeforeCheckIn


    /**************************************************************************
     * Checks a Businessobject out and freezes edit and delete. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected void performCheckOutData (int operation) throws NoAccessException
    {
        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                this.procCheckOut,
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // oid
        BOHelpers.addInParameter (sp, this.oid);
        // user id
        if (this.user != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
        } // else
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, operation);

        // output parameters:
        // checkedOutDate
        Parameter checkedOutDate = sp.addOutParameter (ParameterConstants.TYPE_DATE);

        // perform the function call:
        BOHelpers.performCallFunctionData (sp, this.env);

        // set object properties - get them out of parameters
        this.checkedOut = true;
        this.checkOutDate = checkedOutDate.getValueDate ();
        this.checkOutUser = this.user;
        this.checkOutUserOid = this.user.oid;
        this.checkOutUserName = this.user.fullname;
    } // performCheckOutData


    /**************************************************************************
     * Checks a Businessobject in and unblocks edit and delete. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected void performCheckInData (int operation) throws NoAccessException
    {
        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                this.procCheckIn,
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // oid
        BOHelpers.addInParameter(sp, this.oid);

        // user id
        if (this.user != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
        } // else
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, operation);

        BOHelpers.performCallFunctionData (sp, this.env);

        // reinitialize object properties:
        this.checkedOut = false;
        this.checkOutDate = null;
        this.checkOutUser = null;
        this.checkOutUserOid = null;
        this.checkOutUserName = null;
    } // performCheckIn


    /**************************************************************************
     * Represent a selection box of workflow templates to the user. <BR/>
     *
     * @param   fieldname      name of the select element. <BR/>
     * @param   defaultName    name of defaultValue. <BR/>
     * @param   addEmptyLine   if a blank line shall be included into the
     *                         selection box. <BR/>
     *
     * @return  ???
     */
    public GroupElement createWorkflowTemplatesSelectionBox (
                                                             String fieldname,
                                                             String defaultName,
                                                             boolean addEmptyLine)
    {
        int rowCount;                   // counter
        GroupElement gel = new GroupElement ();
        SelectElement sel;
        int operation = Operations.OP_VIEW;
        String name;
        boolean isDefaultValue;
        SQLAction action = null;        // the action object used to access the
                                        // database

        // get the elements out of the database:
        // create the SQL String to select all tuples
        StringBuffer queryStr =
            new StringBuffer (" SELECT o.name, o.oid ")
                .append (" FROM v_Container$rights o ")
                .append (" WHERE o.userId = ").append (this.user.id)
                .append (SQLHelpers.getStringCheckRights (operation))
                .append (" AND o.tVersionId = ")
                    .append (this.getTypeCache ().getTVersionId (TypeConstants.TC_WorkflowTemplate));

        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);

            // check for empty resultset?
            if (rowCount <= 0)
            {
                gel =  null;           // set return value to null
            } // if
            else
            {
                // found some rows - create selection-box
                sel = new SelectElement (fieldname, false);
                sel.size = 1;
                // allow to leave workflow empty
                if (addEmptyLine)
                {
                    sel.addOption ("", "/");
                } // if

                // get tuples out of db
                while (!action.getEOF ())
                {
                    // create the option
                    // the value of the option is <oid of tempalte>/<name of template>
                    // this ensures that we get the 2 values we need
                    // the 2 strings must be reconstructed while processing
                    OID quOid = SQLHelpers.getQuOidValue (action, "oid");
                    name = action.getString ("name");
                    // check if oid and name is not null
                    if (quOid != null && name != null && name.length () > 0)
                    {
                        isDefaultValue = false;
                        if (defaultName != null)
                        {
                            if (name.equalsIgnoreCase (defaultName))
                            {
                                isDefaultValue = true;
                            } // if
                        } // if
                        sel.addOption (name, "" + quOid + "/" + name, isDefaultValue);
                    } // if (quOid != null && !name.length () == 0)
                    // step one tuple ahead for the next loop
                    action.next ();
                } // while
                gel.addElement (sel);
                // end transaction
                action.end ();
            } // else - found some rows
        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            gel.addElement (new TextElement (dbErr.getMessage () + dbErr.getError ()));
            return gel;
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        // exit
        return gel;
    } // createWorkflowTemplatesSelectionBox


    /**************************************************************************
     * Get workflow-instance information about the last workflow that
     * was initiated for this object. <BR/>
     *
     * @return  information about active (can only be 1) workflow instance
     *          null no workflow-instance for object found
     */
    public WorkflowInstanceInformation getWorkflowInstanceInfo ()
    {
        // if object is not physical
        if (!this.isPhysical)
        {
            return null;
        } // if

        // check if instance was alread retrieved
        if (this.workflowInfo != null)
        {
            return this.workflowInfo;
        } // if

        int rowCount = 0;
        SQLAction action = null;        // the action object used to access the
                                        // database

        // create the query string to get all the data needed for the
        // the instance-information object
        StringBuffer queryStr =
            new StringBuffer (" SELECT oid, currentState, workflowState, ")
                .append ("        currentOwner, processManager, starter")
                .append (" FROM   ibs_Workflow_01")
                .append (" WHERE  objectId = ").append (this.oid.toStringQu ())
                .append (" ORDER  BY startDate DESC"); // assures newest entry!

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection - only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);

            // empty resultset or error
            if (rowCount <= 0)
            {
                // reset workflow information
                this.workflowInfo = null;

                // exit method
                return this.workflowInfo;
            } // if

            // get tuple out of db
            if (!action.getEOF ())
            {
                // create instance information object
                this.workflowInfo = new WorkflowInstanceInformation ();

                // get data
                this.workflowInfo.instanceId = SQLHelpers.getQuOidValue (action, "oid");
                this.workflowInfo.objectId = this.oid;
                this.workflowInfo.currentState = action.getString ("currentState");
                this.workflowInfo.workflowState = action.getString ("workflowState");
                this.workflowInfo.currentOwnerId = action.getInt ("currentOwner");
                this.workflowInfo.processManagerId = action.getInt ("processManager");
                this.workflowInfo.starterId = action.getInt ("starter");
            } // if
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            this.workflowInfo = null;
        } // catch
        finally
        {
            // close db connection in every case - only workaround - db
            // connection must be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        // exit
        return this.workflowInfo;
    } // getWorkflowInstanceInfo


    /**************************************************************************
     * Change the owner of a business object in the database, including all
     * subsequent objects. <BR/>
     *
     * Attention rights-checking will only be done for the initial object. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     * @param   newOwnerId   Id of the new owner
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    public void performChangeOwnerRec (int operation, int newOwnerId)
        throws NoAccessException
    {
        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                this.procChangeOwner,
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // oid
        BOHelpers.addInParameter(sp, this.oid);
        // user id
        if (this.user != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
        } // else
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, operation);
        // owner
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, newOwnerId);

        // perform the function call:
        BOHelpers.performCallFunctionData(sp, this.env);
    } // performChangeOwnerRec


    /**************************************************************************
     * Insert an entry into the protocol table in the database. <BR/>
     * <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    public void performInsertProtocol (int operation)
        throws NoAccessException
    {
        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                this.procInsertProtocol,
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // oid
        BOHelpers.addInParameter(sp, this.oid);
        // user id
        if (this.user != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
        } // else
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, operation);


        // perform the function call:
        BOHelpers.performCallFunctionData(sp, this.env);
    } // performInsertProtocol


    /**************************************************************************
     * This is dummy function in BusinessObject and is overridden. <BR/>
     *  in Container and checks for the size of elements Vector.
     *
     * @return  Always returns -1 (Its a dummy implementation here)
     */
    protected int getElementSize ()
    {
        return -1;
    } // getElementSize


    /**************************************************************************
     * Moves/Copies the files pertinent to this Object or it's Subobject
     * after a copy/move - Operation. <BR/>
     * <BR/>
     *
     * @param   rootOid         ObjectId of the Object where the hierarchy starts.
     * @param   deleteOldFiles  Shall the original files be deleted?
     * @param   isRecursive     Is the operation done recursively?
     *                          If not we need only to work on the root object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected final void performMoveHDFiles (OID rootOid,
                                             boolean deleteOldFiles,
                                             boolean isRecursive)
        throws NoAccessException
    {
        Vector <String []> fileObjects;
        String [] elem;
        String oldFile = null;
        String oldDirectory = null;
        String newDirectory = null;
        String newFile = null;
        boolean isXMLViewerObject = false;

        fileObjects = this.performGetFileObjectData (rootOid, deleteOldFiles,
            isRecursive);

        if (fileObjects == null)        // nothing to move/copy on HD
        {
            return;
        } // if

        Enumeration<String []> vectEnum = fileObjects.elements ();
        while (vectEnum.hasMoreElements ())
        {
            elem = vectEnum.nextElement ();
            
            // has an element been found and are there files to handle?
            if (elem != null && ((Integer.parseInt(elem [5]) & BOConstants.FLG_HASFILE) == BOConstants.FLG_HASFILE))
            {
            	isXMLViewerObject = elem [2] == null;
            	
        		if (isXMLViewerObject) // it's a XMLViewerObject (no file names stored in ibs_Attachment_01)                  
        		{
            		oldDirectory = this.app.p_system.p_m2AbsBasePath +
	                    BOPathConstants.PATH_UPLOAD_ABS_FILES +
	                    elem [6] + File.separator;
            	} // if (isXMLViewerObject)
        		
                else 				// it's a BusinessObject and the file names are stored in ibs_Attachment_01
                {
	                oldDirectory = this.app.p_system.p_m2AbsBasePath +
	                    BOPathConstants.PATH_UPLOAD_ABS_FILES +
	                    elem [1].substring (elem [1].indexOf (UtilConstants.NUM_START_HEX),
	                        elem [1].length () - 1) + File.separator;
	                oldFile = oldDirectory + elem [2];
                } // else
        		
        		//todo: test ob directory korrekt 
        		newDirectory = this.app.p_system.p_m2AbsBasePath +
		                BOPathConstants.PATH_UPLOAD_ABS_FILES + elem [0] +
		                File.separator;

                // if a copy-operation is performed, and it is not an XMLViewerObject
                // then the filename has to be changed --> the oid of the old object
                // has to be removed from the name and the oid of the copied object has
                // to be added to the resulting substring.
                if (!deleteOldFiles && !isXMLViewerObject && (Integer.parseInt (elem [4]) !=
                    this.getTypeCache ().getTVersionId (TypeConstants.TC_Attachment)))
                {
                    newFile = newDirectory + elem [3] + elem [2].substring (18);
                } // if

                // if a move-operation is performed, then the filename
                // has not to be changed
                // and if the object is an XMLViewerObject then it has not to be
                // changed either
                else
                {
                	newFile = newDirectory + elem [2]; 
                } // else					
                
                if (FileHelpers.makeDir (newDirectory, false))
	                                        // directory could be created?
                {
                    if (deleteOldFiles)
                    {
                    	if (!isXMLViewerObject)
                    	{
                    		FileHelpers.renameFile (oldFile, newFile);
                    	} // if
                    } // if
                    else
                    {
                    	if (isXMLViewerObject)
                        {
                    		FileHelpers.copyDirectory (oldDirectory,
                    			newDirectory);
                        } // if
                        else
                        {
                            FileHelpers.copyFile (oldFile, newFile);
                        } // else
                    } // else
                } // if directory could be created
            } // if (elem != null) && ((Integer.parseInt(elem[5]) & BOConstants.FLG_HASFILE) == BOConstants.FLG_HASFILE))
        } // while

        if (!isXMLViewerObject)
        {
        	// now change the path of the files moved in the DB, too:
        	this.performChangeFilePath (rootOid, deleteOldFiles);
       } // if (!isXMLViewerObject)
    } // performChangeTabsOwner


    /**************************************************************************
     * Gets the necessary data of the files which should be copied
     * after a copy/move - Operation out of the DataBase. <BR/>
     * <BR/>
     *
     * @param   rootOid     ObjectId of the Object where the hierarchy starts.
     * @param   isMove      Shall the object be moved? In thath case we need
     *                      only the data of the object itself. The underlying
     *                      objects are not changed.
     * @param   isRecursive Is the operation done recursively?
     *                      If not we need only the data of the root object.
     *
     * @return  Vector of String[5] which hold the information
     *          regarding which files to copy/move from where to where
     */
    protected final Vector<String[]> performGetFileObjectData (OID rootOid,
                                                     boolean isMove,
                                                     boolean isRecursive)
    {   	
    	SQLAction action = null;        // the action object for database access
        Vector<String[]> result = new Vector<String[]> (5);
        int rowCount;
        String[] obj;

        StringBuffer select =
            new StringBuffer ("SELECT containerId, path, fileName, oid,")
                .append (" tVersionId, flags, CASE ")
                .append (" WHEN (flags & ")
                	.append (BOConstants.FLG_HASFILE).append (" = ").append (BOConstants.FLG_HASFILE)
                	.append (") THEN ")
                	.append (" dbo.f_byteToString (")
                	.append (this.funcRetrOriginalOid)
                	.append (" ( oid, ")
                	.append (this.oid.toStringQu())
                	.append (", ")
                	.append (rootOid.toStringQu())
                	.append ("))")
                .append (" ELSE '' ")
                .append (" END AS oldOid")
                .append (" FROM v_FilesHD$content ")
                .append (" WHERE rootOid = ").append (rootOid.toStringQu ());

        // if the object is moved, then only if the object itself
        // is a file or subtype it is considered ...
        // all others are not moved cause the oid of the rootObject
        // remains the same in a move-Operation!
        if (isMove || !isRecursive)
        {
            select.append (" AND oid = ").append (rootOid.toStringQu ());
        } // if

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            rowCount = action.execute (select, false);

            // empty resultset?
            if (rowCount == 0)
            {
                return null;                 // return no Vector
            } // if
            // error while executing?
            else if (rowCount < 0)
            {
                return null;                 // return no Vector
            } // else if

            // everything ok - go on
            // get tuples out of db
            while (!action.getEOF ())   // there are tuples left?
            {
                // create a new object:
                obj = new String [7];

                // add element to list of elements:
                result.addElement (obj);
                // get and set values for element:
                obj [0] = "" + SQLHelpers.getQuOidValue (action, "containerId");
                obj [1] = action.getString ("path");
                obj [2] = action.getString ("fileName");
                obj [3] = "" + SQLHelpers.getQuOidValue (action, "oid");
                obj [4] = "" + action.getInt ("tVersionId");
                obj [5] = "" + action.getInt("flags");
                obj [6] = action.getString ("oldOid");

                // step one tuple ahead for the next loop:
                action.next ();
            } // while

            // the last tuple has been processed
            // end transaction:
            action.end ();
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        return result;
    } // performGetFileObjectData


    /**************************************************************************
     * Calls a Stored Procedure where the path of the moved files are
     * actualized in the DB
     * <BR/>
     *
     * @param   rootOid          ObjectId of the Object where the hierarchie starts.
     * @param   move             True if the object is moved (then only the File of THIS
     *                           object has to be considered).
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected void performChangeFilePath (OID rootOid, boolean move)
        throws NoAccessException
    {
        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                "p_FilePathData$change",
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // oid
        BOHelpers.addInParameter(sp, rootOid);

        // recursive?
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, !move);

        // perform the function call:
        BOHelpers.performCallFunctionData(sp, this.env);

        // room for some success statements
    } // performChangeFilePath


    /**************************************************************************
     * Creates a selection box with connector objects. <BR/>
     * In case no connector objects could be found or are not accessible by
     * the user a message will be generated. <BR/>
     *
     * @param   fieldname           name of the selectin box
     * @param   activeOid           oid of the object to be marked active
     * @param   addEmptyOption      flag to add a empty line at the beginning
     * @param   isImportConnector   flag that connector must can import
     * @param   isExportConnector   flag that connector must can export
     *
     * @return  The GroupElement that holds the generated selection box.
     */
    protected GroupElement createConnectorSelectionBox (String fieldname,
                                                        String activeOid,
                                                        boolean addEmptyOption,
                                                        boolean isImportConnector,
                                                        boolean isExportConnector)
    {
        int rowCount;                   // row counter
        GroupElement gel = new GroupElement ();
        SelectElement sel;
        int operation = Operations.OP_VIEW;
        String oidStr;
        String name;
        SQLAction action = null;        // the action object used to access the
                                        // database

/*
 * performancetuning
 */
        // get the elements out of the database:
        // create the SQL String to select all tuples
        StringBuffer queryStr =
            new StringBuffer (" SELECT o.name, o.oid")
                .append (" FROM   v_Container$rights o, ibs_Connector_01 c ")
                .append (" WHERE  o.userId = ").append (this.user.id)
                .append (SQLHelpers.getStringCheckRights (operation))
                .append (" AND    o.isLink = 0 ")
                .append (" AND    o.oid = c.oid ");
// BB HINT: DONT WE NEED THE DOMAINID HERE????

        // check if we need to restrict to import enabled connectors
        if (isImportConnector)
        {
            queryStr.append (" AND c.isImportConnector = ").append (UtilConstants.QRY_TRUE);
        } // if
        // check if we need to restrict to export enabled connectors
        if (isExportConnector)
        {
            queryStr.append (" AND c.isExportConnector = ").append (UtilConstants.QRY_TRUE);
        } // if

        try
        {
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);
            // empty resultset?
            if (rowCount == 0)
            {
                gel.addElement (new TextElement (MultilingualTextProvider
                    .getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_NO_ELEMENTS_FOUND, this.env)));
                return gel;             // terminate this method
            } // if
            // error while executing?
            else if (rowCount < 0)
            {
                gel.addElement (new TextElement (MultilingualTextProvider
                    .getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_NO_ELEMENTS_FOUND, this.env)));
                return gel;
            } //else if
            // create the selection box
            sel = new SelectElement (fieldname, false);
            sel.size = 1;
            // check if we need to add a empty option
            if (addEmptyOption)
            {
                sel.addOption ("                    ", OID.EMPTYOID);
            } // if
            // get tuples out of db
            while (!action.getEOF ())
            {
                // get the oid and convert it to a string
                oidStr = SQLHelpers.getQuOidValue (action, "oid").toString ();
                // get the name
                name = action.getString ("name");
                // check if the option needs to be híghlighted
                if (oidStr.equalsIgnoreCase (activeOid))
                {
                    sel.addOption (name, oidStr, true);
                } // if
                else
                {
                    sel.addOption (name, oidStr);
                } // else
                // step one tuple ahead for the next loop
                action.next ();
            } // while
            gel.addElement (sel);
            // end transaction
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            gel.addElement (new TextElement (dbErr.getMessage () + dbErr.getError ()));
            return gel;
        } // catch
        finally
        {
            this.releaseDBConnection (action);
        } // finally
        return gel;
    } // createConnectorSelectionBox


    /**************************************************************************
     * Creates a selection box with all objects of a specified objecttype.
     * In case no objects could be found or are not accessible by the user
     * a message will be generated. <BR/>
     *
     * @param   objectType      an objecttype to use as filter
     * @param   fieldname       name of the selectin box
     * @param   activeOid       oid of the object to be marked active
     * @param   addEmptyOption  flag to add a empty line at the beginning
     *
     * @return  The GroupElement that holds the generated selection box.
     */
    protected GroupElement createSelectionBoxFromObjectType (int objectType,
                                                             String fieldname,
                                                             String activeOid,
                                                             boolean addEmptyOption)
    {
        int [] objectTypes = {objectType};
        return this.createSelectionBoxFromObjectType (objectTypes, fieldname, activeOid, addEmptyOption);
    } // createSelectionBoxFromObjectType


    /**************************************************************************
     * Creates a selection box with all objects of specified objecttypes.
     * In case no objects could be found or are not accessible by the user
     * a message will be generated. <BR/>
     *
     * @param   objectTypes     an array with objecttypes to use as filter
     * @param   fieldname       name of the selectin box
     * @param   activeOid       oid of the object to be marked active
     * @param   addEmptyOption  flag to add a empty line at the beginning
     *
     * @return  The GroupElement that holds the generated selection box.
     */
    protected GroupElement createSelectionBoxFromObjectType (int[] objectTypes,
                                                             String fieldname,
                                                             String activeOid,
                                                             boolean addEmptyOption)
    {
        // row counter
        int rowCount;
        GroupElement gel = new GroupElement ();
        SelectElement sel;
        int operation = Operations.OP_VIEW;
        String oidStr;
        String name;
        StringBuffer typeQueryStr = new StringBuffer (" (");
        String comma = "";
        SQLAction action = null;        // the action object used to access the
                                        // database

        // construct the string for the objectType filter
        for (int i = 0; i < objectTypes.length; i++)
        {
            typeQueryStr.append (comma).append (Type.createTVersionId (objectTypes[i]));
            comma = ",";
        } // for (int i = 0; i < objectTypes.length; i++)
        typeQueryStr.append (")");

/*
 * Performancetuning
 */
        // get the elements out of the database:
        // create the SQL String to select all tuples
        StringBuffer queryStr =
            new StringBuffer (" SELECT o.name, o.oid")
                .append (" FROM   v_Container$rights o ")
                .append (" WHERE  o.userId = ").append (this.user.id)
                .append (SQLHelpers.getStringCheckRights (operation))
                .append (" AND    o.isLink = 0 ")
                .append (" AND    o.tVersionID IN ").append (typeQueryStr);
// BB HINT: DONT WE NEED THE DOMAINID HERE????

        try
        {
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);
            // empty resultset?
            if (rowCount == 0)
            {
                gel.addElement (new TextElement (MultilingualTextProvider
                    .getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_NO_ELEMENTS_FOUND, this.env)));
                return gel;             // terminate this method
            } //if
            // error while executing?
            else if (rowCount < 0)
            {
                gel.addElement (new TextElement (MultilingualTextProvider
                    .getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_NO_ELEMENTS_FOUND, this.env)));
                return gel;
            } //else if
            // create the selection box
            sel = new SelectElement (fieldname, false);
            sel.size = 1;
            // check if we need to add a empty option
            if (addEmptyOption)
            {
                sel.addOption ("                    ", OID.EMPTYOID);
            } // if
            // get tuples out of db
            while (!action.getEOF ())
            {
                // get the oid and convert it to a string
                oidStr = SQLHelpers.getQuOidValue (action, "oid").toString ();
                // get the name
                name = action.getString ("name");
                // check if the option needs to be híghlighted
                if (oidStr.equalsIgnoreCase (activeOid))
                {
                    sel.addOption (name, oidStr, true);
                } // if
                else
                {
                    sel.addOption (name, oidStr);
                } // else
                // step one tuple ahead for the next loop
                action.next ();
            } // while
            gel.addElement (sel);
            // end transaction
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            gel.addElement (new TextElement (dbErr.getMessage () + dbErr.getError ()));
            return gel;
        } // catch
        finally
        {
            this.releaseDBConnection (action);
        } // finally
        return gel;
    } // createSelectionBoxFromObjectType


    /**************************************************************************
     * Returns a Vector with the OIDs of all tab objects of the business object.
     * <BR/>
     *
     * @return  A vector with all tab oids.
     */
    public Vector<OID> performGetTabOids ()
    {
        Vector<OID> tabOids = new Vector<OID> ();
        SQLAction action = null;
        try
        {
            // get a database connection
            action = this.getDBConnection ();
            // get the oids of all tab objects
            StringBuffer queryStr =
                new StringBuffer (" SELECT oid")
                    .append (" FROM ibs_Object")
                    .append (" WHERE containerId = ").append (this.oid.toStringQu ())
                    .append (" AND containerKind = ").append (BOConstants.CONT_PARTOF);

            // execute the query
            int rowCount = action.execute (queryStr, false);
            // not empty result set ?
            if (rowCount > 0)
            {
                // get all found oids
                while (!action.getEOF ())
                {
                    // get the oid of the tab object
                    tabOids.addElement (SQLHelpers.getQuOidValue (action, "oid"));
                    action.next ();
                } // while
            } // if (rowCount > 0)
            // end action (release the resources)
            action.end ();
        } // try
        catch (DBError e) // no access to objects allowed
        {
            // send message to the user:
            IOHelpers.showMessage (e, this.app, this.sess, this.env);
        } // catch
        finally
        {
            this.releaseDBConnection (action);
        } // finally

        // return the vector with all tab oids
        return tabOids;
    } // performGetTabOids


    /**************************************************************************
     * Returns the tab code of the given tab oid. <BR/>
     *
     * @param   tabOid  The oid of the tab object.
     * @return  The tab code of the tab object.
     */
    public String performGetTabCodeData (OID tabOid)
    {
        String tabCode = "";
        try
        {
            // create the stored procedure call:
            StoredProcedure sp = new StoredProcedure(
                    "p_Tab$getCodeFromOid",
                    StoredProcedureConstants.RETURN_VALUE);

            // set parameters:
            sp.addInParameter (ParameterConstants.TYPE_STRING, tabOid.toString ());
            // tab code (output)
            Parameter codeParam = sp.addOutParameter (ParameterConstants.TYPE_STRING);

            // perform the function call:
            int retVal = BOHelpers.performCallFunctionData(sp, this.env);

            if (retVal == UtilConstants.QRY_OBJECTNOTFOUND) // object not found?
            {
                // raise no access exception:
                throw new ObjectNotFoundException (MultilingualTextProvider
                    .getMessage (UtilExceptions.EXC_BUNDLE,
                        UtilExceptions.ML_E_OBJECTNOTFOUNDEXCEPTION, env));
            } // else if object not found
            else if (retVal == UtilConstants.QRY_OK)    // access allowed
            {
                // set object properties - get them out of parameters
                // tabCodes = this.getSpOidParam (params[1]);
                tabCode = codeParam.getValueString ();
            } // else if access allowed
        } // try
        catch (ObjectNotFoundException e) // no access to objects allowed
        {
            // send message to the user:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
        } // catch
        catch (NoAccessException e) // no access to objects allowed
        {
            // send message to the user:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
        } // catch
        return tabCode;
    } // performGetTabCodeData


    /**************************************************************************
     * To publish the object. <BR/>
     *
     * @return  <CODE>true</CODE> if the operation succeeded successfully,
     *          <CODE>false</CODE> otherwise.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    public boolean publish () throws NoAccessException
    {
        // do nothing - must be overwritten
        return false;
    } // publish


    /**************************************************************************
     * Get the ids of all types which are allowed to be contained within the
     * actual object type. <BR/>
     * This method depends on the value of the oid and gets the corresponding
     * object type out of the object pool. Then it gets the types which may be
     * contained within this type out of the type itself.
     *
     * @return  An array with the allowed type ids.
     */
    protected String[] getTypeIds ()
    {
        Type objectType = null;         // the object type
        ITypeContainer<Type> types = null;    // the types
        String[] typeIds = null;        // the type ids for the object type

        // check if there is a valid oid:
        if (this.oid != null)           // oid exists?
        {
            // get the object type:
            objectType = this.typeObj;

            // check if the type was found and if there are any types allowed
            // within it:
            if (objectType != null &&
                (types = objectType.getMayContainTypes ()) != null)
                                        // types allowed?
            {
                // get the type ids:
                typeIds = ((TypeContainer) types).ids;
            } // if types allowed
        } // if oid exists

        return typeIds;                 // return the computed type ids
    } // getTypeIds
    
    
    /**************************************************************************
     * Returns the multilang name of the object's type.
     *
     * @return  The multilang name of the object's type.
     */
    protected final String getMlTypeName ()
    {
        String mlTypeName = null;

        // check if the type can be retrieved
        if (this.getTypeCache () != null && this.getTypeCache ().get (this.type) != null)
        {
            mlTypeName = this.getTypeCache ().get (this.type).
                getMlName (MultilingualTextProvider.getUserLocale (this.env).getLocale ());
        } // if
        else
        {
            // fallback scenario
            mlTypeName = this.typeName;
        } // else
       
        return mlTypeName;
    } // getMlTypeName


    /**************************************************************************
     * Evaluate the function to be performed. <BR/>
     *
     * @param   function    The function to be performed.
     *
     * @return  Function to be performed after this method. <BR/>
     *          {@link ibs.app.AppFunctions#FCT_NOFUNCTION AppFunctions.FCT_NOFUNCTION}
     *          if there is no function or the function was already performed.
     */
    public int evalFunction (int function)
    {
        int resultFunction = AppFunctions.FCT_NOFUNCTION;
                                        // the resulting function
        switch (function)               // perform function
        {
            default:                    // unknown function
                resultFunction = function; // function was not performed
/* code for sub classes:
                resultFunction = super.evalFunction (function);
                                        // evaluate function in super class
*/
        } // switch function

        // return which function shall be performed after this method:
        return resultFunction;
    } // evalFunction


    /**************************************************************************
     * handle events for current tabview. <BR/>
     *
     * @param   evt     Event to be handled.
     */
    public void handleEvent (int evt)
    {
        // example code
        /*
        switch (evt)            // perform function
        {
        case BOEvents.EVT_XXX:
            xxx ();             // xxx
            break;

        case BOEvents.EVT_YYY:
            yyy ();             // yyy
            break;
        default:
            super.handleEvent (evt);
        */

        IOHelpers.showMessage ("Unknown event " + evt +
                               " for class: " + this.getClass ().getName (),
                               this.app, this.sess, this.env);
    } // handleEvent


    /**************************************************************************
     * Function reserved for future use. <BR/>
     */
    public void dummyFunction01 ()
    {
        // this method may be overwritten in sub classes
    } // dummyFunction01


    /**************************************************************************
     * Function reserved for future use. <BR/>
     */
    public void dummyFunction02 ()
    {
        // this method may be overwritten in sub classes
    } // dummyFunction02


    /**************************************************************************
     * Function reserved for future use. <BR/>
     */
    public void dummyFunction03 ()
    {
        // this method may be overwritten in sub classes
    } // dummyFunction03


    /**************************************************************************
     * Function reserved for future use. <BR/>
     */
    public void dummyFunction04 ()
    {
        // this method may be overwritten in sub classes
    } // dummyFunction04


    /**************************************************************************
     * Function reserved for future use. <BR/>
     */
    public void dummyFunction05 ()
    {
        // this method may be overwritten in sub classes
    } // dummyFunction05


    /**************************************************************************
     * Function reserved for future use. <BR/>
     */
    public void dummyFunction06 ()
    {
        // this method may be overwritten in sub classes
    } // dummyFunction06


    /**************************************************************************
     * Function reserved for future use. <BR/>
     */
    public void dummyFunction07 ()
    {
        // this method may be overwritten in sub classes
    } // dummyFunction07


    /**************************************************************************
     * Function reserved for future use. <BR/>
     */
    public void dummyFunction08 ()
    {
        // this method may be overwritten in sub classes
    } // dummyFunction08


    /**************************************************************************
     * Function reserved for future use. <BR/>
     */
    public void dummyFunction09 ()
    {
        // this method may be overwritten in sub classes
    } // dummyFunction09


    /**************************************************************************
     * Function reserved for future use. <BR/>
     */
    public void dummyFunction10 ()
    {
        // this method may be overwritten in sub classes
    } // dummyFunction10


    /**************************************************************************
     * Returns the string to the type translator file. <BR/>
     * In case no explizit type translator file has been a standard filename
     * will be generated using the pattern:
     * &lt;StandardTypeTranslatorPath>/&lt;VirtualType>2&lt;PhysicalType>.xsl
     *
     * @return  The string to the type translator file.
     */
    public String getTypeTranslator ()
    {
        // check if a type translator has been set
        if (this.p_typeTranslator != null && (this.p_typeTranslator.length () > 0))
        {
            return this.p_typeTranslator;
        } // if (this.p_typeTranslator != null && (! this.p_typeTranslator.length () == 0)

        // type translator not set
        return this.app.p_system.p_m2AbsBasePath +
                BOPathConstants.PATH_ABS_TYPETRANSLATORS +
                this.typeObj.getCode () + "2" +
                this.p_targetPhysicalTypeCode + ".xsl";
    } // getTypeTranslator


    /**************************************************************************
     * Shows a debug message. This message can be switched off and on. <BR/>
     *
     * @param message   the message to be displayed
     *
     * @deprecated  This method is not longer necessary. Instead the IDE
     *              debugging mechanism shall be used. All calls to this method
     *              shall be deleted.
     */
    @Deprecated
    protected void showDebug (String message)
    {
        if (false)
        {
            if (this.env != null)
            {
                this.env.write ("<DIV ALIGN=\"LEFT\">" +
                    this.getClass ().getName () + ":" +
                    message + "</DIV><P/>");
            } // if
            else
            {
                System.out.println (this.getClass ().getName () + ":" + message);
            } // else
        } // if
        else
        {
            this.debug (message);
        } // else
    } // showDebug


    /**************************************************************************
     * The chosen query is performed and the chosen column values
     * are returned. <BR/>
     *
     * @param   queryname   name of the query which is filling the selectionbox
     * @param   idtype      databasetype of the ids
     * @param   emptyoption true if there is an empty item
     *
     * @return                  a SelectionList holding the desired
     *                          query data (ids and values)
     *                          or null
     */
    public SelectionList getQueryData (String queryname, String idtype, boolean emptyoption)
    {
        // initialize a SelectionList where ids and values from the query are saved
        SelectionList results = new SelectionList ();

        QueryExecutive qe = new QueryExecutive ();
        // leider noch immer notwendig : (
        qe.initService (this.user, this.env, this.sess, this.app);

        // check if given queryName for query to be executed is not null
        if (queryname != null)
        {
            // set current oid and containerId in order to resolve sysvars
            // referencing object data
            qe.setCurrentObjectOid (this.oid);
            qe.setCurrentContainerId (this.containerId);

            // check if query exists and try to execute it
            if (qe.execute (queryname))
            { // if query with name exists and could be executed
                // get the rowCount of the query
                int rowCount = qe.getRowCount ();
                // check if any entries exist
                if (rowCount <= 0)
                {
                    return null;
                } // if

                // number of the selectionbox entries
                int num = rowCount + (emptyoption ? 1 : 0);

                int i = 0;              // counter
                // initialize the SelectionList
                results = new SelectionList ();
                // initialize the string arrays of the selectionlist
                results.ids = new String[num];
                results.values = new String[num];
                results.groupingIds = new String[num];

                // if an empty item in the selectionbox is required
                if (emptyoption)
                {
                    // add an empty element to the selectionbox
                    if (idtype.equals (DIConstants.IDTYPE_STRING))
                    {
                        results.ids[0] = " ";
                        results.values[0] = " ";
                        results.groupingIds[0] = "";
                    } //if
                    else if (idtype.equals (DIConstants.IDTYPE_NUMBER))
                    {
                        results.ids[0] = "-1";
                        results.values[0] = " ";
                        results.groupingIds[0] = "";
                    } // else if
                    else if (idtype.equals (DIConstants.IDTYPE_OBJECTID))
                    {
                        results.ids[0] = OID.EMPTYOID;
                        results.values[0] = " ";
                        results.groupingIds[0] = "";
                    } // else if
                } // if
                else
                {
                    i = -1;
                } // else

                while (!qe.getEOF ())
                {
                    // add another id to the ids array of the selectionlist
                    results.ids[++i] = qe.getColValue ("id");
                    // add another value to the values array of the selectionlist
                    results.values[i] = qe.getColValue ("value");
                    // add another groupingId to the group ids array of the selectionlist
                    results.groupingIds[i] = qe.getColValue ("groupingId");
                    // get the next row
                    qe.next ();
                } // while (!qe.getEOF ())

            } // if (qe.execute (queryName))

        } // if (queryName != null)
        else                            // if (queryName == null)
        {
            return null;
        } // else

        // return the SelectionList
        return results;
    } // getQueryData

    /**************************************************************************
     * The chosen query is performed and the chosen column values
     * are returned. <BR/>
     *
     * @param   querystr        querystring to get data
     * @param   idtype          databasetype of the ids
     * @param   emptyoption     true if there is an empty item
     *
     * @return                  a SelectionList holding the desired
     *                          query data (ids and values)
     *                          or null
     */
    public SelectionList getQueryData (StringBuffer querystr, String idtype,
                                       boolean emptyoption)
    {
        // create lists for oid and value
        List<OID> oids = new ArrayList<OID> ();

        // initialize a SelectionList where ids and values from the query are saved
        SelectionList results = new SelectionList ();

        // the action object used to access the DB
        SQLAction action = null;

        try
        {
            // result row count
            int rowCount = 0;

            // get current database connection
            action = this.getDBConnection ();

            // get result
            int validResult = action.execute (querystr, false);

            // check if result set is valid
            if (validResult <= 0)
            {
                return null;
            } // if

            // count row records
            while (!action.getEOF ())
            {
                // count
                ++rowCount;
                // next record
                action.next ();
            } // while (!action.getEOF ())

            // number of the selectionbox entries
            int num = rowCount + (emptyoption ? 1 : 0);
            // counter
            int i = 0;

            // value column name
            String idColName = null;

            // initialize the string arrays of the selectionlist
            results.ids = new String[num];
            results.values = new String[num];

            // if an empty item in the selectionbox is required
            if (emptyoption)
            {
                // add an empty element to the selectionbox
                if (idtype.equals (DIConstants.IDTYPE_STRING))
                {
                    results.ids[0] = " ";
                    results.values[0] = " ";
                } //if
                else if (idtype.equals (DIConstants.IDTYPE_NUMBER))
                {
                    results.ids[0] = "-1";
                    results.values[0] = " ";
                } // else if
                else if (idtype.equals (DIConstants.IDTYPE_OBJECTID))
                {
                    results.ids[0] = OID.EMPTYOID;
                    results.values[0] = " ";
                    oids.add (OID.getEmptyOid ());
                } // else if
                // ++counter
                ++i;
            } // if
            else
            {
                i = 0;
            } // else

            // set id column name
            if (idtype.equals (DIConstants.IDTYPE_STRING))
            {
                // id string value
                idColName = "value";
            } //if
            else if (idtype.equals (DIConstants.IDTYPE_NUMBER))
            {
                // id number value
                idColName = "id";
            } // else if
            else if (idtype.equals (DIConstants.IDTYPE_OBJECTID))
            {
                // id oid value
                idColName = "oid";
            } // else if

            // get result, but no check if valid
            validResult = action.execute (querystr, false);

            // check if result set is valid
            if (validResult <= 0)
            {
                return null;
            } // if

            while (!action.getEOF ())
            {
                // check if field type VALUEDOMAINOID
                if (idtype.equals (DIConstants.IDTYPE_OBJECTID))
                {
                    // fill value with OID data
                    oids.add (SQLHelpers.getQuOidValue (action, idColName));
                } // if
                else
                {
                    // add another id value to the id array of the selectionlist
                    results.ids[i] = action.getString (idColName);
                } // else
                // add another value to the value array of the selectionlist
                results.values[i] = action.getString ("value");

                // ++counter
                ++i;

                // get the next row
                action.next ();
            } // while (!action.getEOF ())

            // check if field type VALUEDOMAINOID
            if (idtype.equals (DIConstants.IDTYPE_OBJECTID))
            {
                // reset counter
                i = -1;

                // fill the value attribute:
                for (Iterator<OID> iter = oids.iterator (); iter.hasNext ();)
                {
                    OID oid = iter.next ();
                    results.ids[++i] = oid != null ? oid.toString () : null;
                } // for iter
            } // if
        } // try
        catch (DBError e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            this.releaseDBConnection (action);
        } // finally
        // return the SelectionList
        return results;
    } // getQueryData


    /**************************************************************************
     * Get the standard receivers for distributions of the current object. <BR/>
     *
     * @return  An array containing the oids of the standard receivers.
     *          <CODE>null</CODE> if there are no standard receivers.
     */
    public OID[] getStdNotificationReceivers ()
    {
        // almost all objects don't have standard receivers:
        return null;
    } // getStdNotificationReceivers


    /**************************************************************************
     * Get the notification service which can perform notifications for the
     * actual object. <BR/>
     *
     * @return  The notification service.
     */
    public INotificationService getNotificationService ()
    {
        // retrieve the notification service from the notification service factory:
        return NotificationServiceFactory.getInstance (this.env).getNotificationService ();
    } // getNotificationService


    /**************************************************************************
     * Perform a notification for the actual object. <BR/>
     *
     * @param   notiService     The notification service.
     * @param   template        The notification template.
     * @param   distributedOid  Oid of the distributed object.
     *
     * @throws  NotificationFailedException
     *          An exception occurred during notification.
     *
     * @see ibs.service.notification.INotificationService#performNotification (Vector, OID, NotificationTemplate, boolean)
     */
    public void callNotificationService (INotificationService notiService,
                                         NotificationTemplate template,
        OID distributedOid)
        throws NotificationFailedException
    {
        if (this.getUserInfo ().distributeElements != null)
                                    // there is more than one element to
                                    // distribute?
        {
            // perform the notification:
            notiService.performNotification (this.sess.receivers,
                                        this.getUserInfo ().distributeElements,
                                        template, true);
        } // if there is more than one element to distribute
        else                            // only one element to distribute
        {
            // perform the notification:
            notiService.performNotification (this.sess.receivers,
                distributedOid, template, true);
        } // else only one element to distribute
    } // callNotificationService

} // class BusinessObject

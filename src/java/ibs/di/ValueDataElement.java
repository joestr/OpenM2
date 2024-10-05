/*
 * Class: ValueDataElement.java
 */

// package:
package ibs.di;

// imports:
import ibs.BaseObject;
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MlInfo;
import ibs.ml.MultilangConstants;
import ibs.ml.MultilingualTextInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.ml.Locale_01;
import ibs.service.observer.M2ObserverService;
import ibs.service.observer.M2ReminderObserverJob;
import ibs.service.observer.M2ReminderObserverJobData;
import ibs.service.observer.ObserverConstants;
import ibs.service.observer.ObserverContext;
import ibs.service.observer.ObserverException;
import ibs.service.user.User;
import ibs.util.DateTimeHelpers;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.w3c.dom.Node;


/******************************************************************************
 * The ValueDataElement hold the information of an VALUES section from the
 * XML import file. <BR/>
 * Note that this class has become bloated in the meantime because it supports
 * so many different fieldtypes.<BR/>
 *
 * @version     $Id: ValueDataElement.java,v 1.39 2013/01/15 14:48:28 rburgermann Exp $
 *
 * @author      Buchegger Bernd (BB), 990107
 ******************************************************************************
 */
public class ValueDataElement extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ValueDataElement.java,v 1.39 2013/01/15 14:48:28 rburgermann Exp $";

    /**
     *  fieldname of an VALUE element. <BR/>
     */
    public String field;

    /**
     *  type of an VALUE element. <BR/>
     */
    public String type;

    /**
     *  value of an VALUE element. <BR/>
     */
    public String value;

    /**
     * The size of the element. <BR/>
     * For files this is the file size. <BR/>
     * Default: <CODE>-1</CODE>
     */
    public long p_size = -1;

    /**
     *  is this VALUE element mandatory. <BR/>
     */
    public String mandatory;

    /**
     *  Readonly indicator. <BR/>
     */
    public String p_readonly;

    /**
     *  info about a VALUE element. <BR/>
     */
    public String info;

    /**
     *  context about a VALUE element. <BR/>
     */
    public String p_context;

    /**
     *  which types can be referenced by a VALUE of the OBJECTREF type
     */
    public String typeFilter;

    /**
     *  where starts the search for referenced objects
     */
    public String searchRoot;

    /**
     *  extKey id domain for definition of search root
     */
    public String searchRootIdDomain;

    /**
     *  extKey id for definition of search root
     */
    public String searchRootId;

    /**
     *  should the search be recursive
     */
    public String searchRecursive;

    /**
     *  fieldname of an VALUE element in the mapping table. <BR/>
     */
    public String mappingField;

    /**
     *  query to be performed to get the data for this VALUE element. <BR/>
     */
    public String queryName;

    /**
     *  Attribute of TYPE = "QUERYSELECTIONBOX".<BR/>
     *  a Queryselection query needs values.
     *  If EMPTYOPTION="YES" there will be an empty item in the selectionbox. <BR/>
     */
    public String emptyOption;

     /**
     *  attribute of TYPE = "QUERYSELECTIONBOX" and TYPE = "SELECTIONBOX"
     */
    public String options;

    /**
     * Value attribute in options tag.
     */
    public List<String> values;

    /**
     * Id attribute in options tag.
     */
    public List<String> valueDomainElements;

     /**
     *  attribute of TYPE = "QUERYSELECTIONBOX"
     */
    public String refresh;

    /**
     *  attribute of TYPE = "*SELECTIONBOX"
     */
    public String viewType;

    /**
     *  attribute of TYPE = "*SELECTIONBOX"
     */
    public String noColumns;

    /**
     *  attribute of TYPE = "*SELECTIONBOX"
     */
    public String multiSelection;

    /**
     *  the string to displayed after the value as unit of value. <BR/>
     */
    public String p_valueUnit;

    /**
     *  the domain for EXTKEY values
     */
    public String p_domain;

    /**
     * Type of display. <BR/>
     * Attribute of type REMINDER.
     * Possible values: {@link DIConstants#DISPLAY_POPUP DISPLAY_POPUP},
     * {@link DIConstants#DISPLAY_INLINE DISPLAY_INLINE}.
     */
    public String p_displayType = null;

    /**
     * Days for reminder 1. <BR/>
     * Attribute of type REMINDER.
     */
    public int p_remind1Days = 0;

    /**
     * Text for reminder 1. <BR/>
     * Attribute of type REMINDER.
     */
    public String p_remind1Text = null;

    /**
     * Recipients for reminder 1. <BR/>
     * Attribute of type REMINDER.
     */
    public String p_remind1Recip = null;

    /**
     * Query for getting possible recipients for reminder 1. <BR/>
     * Attribute of type REMINDER.
     */
    public String p_remind1RecipQuery = null;

    /**
     * Days for reminder 2. <BR/>
     * Attribute of type REMINDER.
     */
    public int p_remind2Days = 0;

    /**
     * Text for reminder 2. <BR/>
     * Attribute of type REMINDER.
     */
    public String p_remind2Text = null;

    /**
     * Recipients for reminder 2. <BR/>
     * Attribute of type REMINDER.
     */
    public String p_remind2Recip = null;

    /**
     * Query for getting possible recipients for reminder 2. <BR/>
     * Attribute of type REMINDER.
     */
    public String p_remind2RecipQuery = null;

    /**
     * Days for escalation. <BR/>
     * Attribute of type REMINDER.
     */
    public int p_escalateDays = 0;

    /**
     * Text for escalation. <BR/>
     * Attribute of type REMINDER.
     */
    public String p_escalateText = null;

    /**
     * Recipients for escalation. <BR/>
     * Attribute of type REMINDER.
     */
    public String p_escalateRecip = null;

    /**
     * Query for getting possible recipients for escalation. <BR/>
     * Attribute of type REMINDER.
     */
    public String p_escalateRecipQuery = null;

    /**
     * vector for subtags of tag VALUE. <BR/>
     */
    public Vector<?> p_subTags = null;

    /**
     * An xml node that contains the dom structure with the query
     * result of a query field. <BR/>
     */
    private Node p_queryResultNode = null;

    /**
     * the time stamp for the query result node. <BR/>
     */
    private Date p_queryTimeStamp = null;

    /**
     * The old reminder value. <BR/>
     */
    public ValueDataElement p_oldReminder = null;

    /**
     * The file enconding.
     */
    public String p_encoding = null;

    /**
     * The file filename.
     */
    public String p_filename = null;

    /**
     * The file content type.
     */
    public String p_contentType = null;

    /**
     * The file extension.
     */
    public String p_extension = null;

    /**
     * The hash code. <BR/>
     */
    private int p_hashCode = Integer.MIN_VALUE;

    /**
     * Indicates that this is an extended column value. <BR/>
     * This is used for container queries that have an extended content
     * query. The valueDataElements are used as descriptors for the
     * columns to be displayed and for the query definition.
     * Extended columns must be excluded from the query definition.
     * Thus we have to use this property. <BR/>
     */
    public boolean p_isExtendedColumn = false;

    /**
     * Indicates that this field's value has been changed.
     */
    private boolean p_isChanged = false;

    /**
     * Stores the old value.
     */
    private String p_oldValue = "";

    /**
     * Undefined parameter value. <BR/>
     */
    private static final String PARAM_UNDEFINED = "UNDEFINED";

    /**
     * Error message: error while setting observer job for reminder. <BR/>
     */
    private static final String ERRM_REMINDER_OBSERVERJOB =
        "Error during setting observer job for reminder.";

    /**
     *  The multilang overwrite key for name and description lookup
     *  for a VALUE element. <BR/>
     */
    public String mlKey;
    
    /**
     * Holds the preloaded multilang infos for all locales.
     */
    private Map<String, MlInfo> mlInfos = null;

    /**
     * Should this VALUE element be shown in a link view. <BR/>
     */
    public String p_showInLinks;

    /**
     * Flag to indicate that a file is attached to this object. <BR/>
     * Default: <CODE>false</CODE>
     */
    private boolean p_hasFile = false;
    
    /**************************************************************************
     * Creates an ValueDataElement. <BR/>
     */
    public ValueDataElement ()
    {
        // call constructor of super class ObjectReference:
        this.field = null;
        this.type = null;
        this.value = null;
        this.mandatory = null;
        this.p_readonly = null;
        this.info = null;
        this.typeFilter = null;
        this.searchRoot = null;
        this.searchRootIdDomain = null;
        this.searchRootId = null;
        this.searchRecursive = null;
        this.mappingField = null;
        this.queryName = null;
        this.options = null;
        this.p_valueUnit = null;
        this.emptyOption = null;
        this.refresh = null;
        this.p_context = null;
        this.viewType = null;
        this.multiSelection = null;
        this.noColumns = null;
        this.p_domain = null;
        this.p_size = -1;
        this.p_displayType = null;
        this.p_remind1Days = 0;
        this.p_remind1Text = null;
        this.p_remind1Recip = null;
        this.p_remind1RecipQuery = null;
        this.p_remind2Days = 0;
        this.p_remind2Text = null;
        this.p_remind2Recip = null;
        this.p_remind2RecipQuery = null;
        this.p_escalateDays = 0;
        this.p_escalateText = null;
        this.p_escalateRecip = null;
        this.p_escalateRecipQuery = null;
        this.mlKey = null;
        this.mlInfos = null;
        this.p_showInLinks = null;
    } // ValueDataElement


    /**************************************************************************
     * Creates an ValueDataElement. <BR/>
     *
     * @param   fieldname       fieldname of the VALUE element
     * @param   type            type of the VALUE element
     * @param   value           value of the VALUE element
     * @param   mandatory       is the value a mandatory one?
     * @param   readonly        is the value a readonly one?
     * @param   info            info about this value
     * @param   typeFilter      which types can be refered by this value
     * @param   searchRoot      from where may the refered types come
     * @param   searchRootIdDomain
     *                          search root EXTKEY id domain for an objectref field.
     * @param   searchRootId    search root EXTKEY id for an objectref field.
     * @param   searchRecursive may the search for referred types be recursive?
     * @param   mappingField    The name of the mapping field in the database.
     * @param   queryName       The name of the query.
     * @param   options         all possible values for the queryselectionbox
     * @param   valueUnit       displayed after the value as unit of value
     * @param   emptyOption     yes if there is an empty item in the selectionbox
     * @param   refresh         yes if the query is executed editing the form
     * @param   domain          the domain attribute for EXTKEY values
     * @param   size            Size of the value content.
     * @param   valueSubTags    Vector with any subtags for current value.
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
     * @param   showInLinks     should value be shown within a link view.                          
     */
    public ValueDataElement (String fieldname, String type, String value,
                             String mandatory, String readonly, String info,
                             String typeFilter, String searchRoot,
                             String searchRootIdDomain, String searchRootId,
                             String searchRecursive, String mappingField,
                             String queryName, String options,
                             String valueUnit, String emptyOption,
                             String refresh, String domain, long size,
                             Vector<?> valueSubTags,
                             Vector<Serializable> reminderParams,
                             String context, String viewType, String noColumns,
                             String multiSelection, Vector<Serializable> fileParams,
                             String mlKey,
                             Map <String, MlInfo> mlInfos,
                             String showInLinks)
    {
        this.field = fieldname;
        this.type = type;
        this.value = value;
        this.mandatory = mandatory;
        this.p_readonly = readonly;
        this.info = info;
        this.typeFilter = typeFilter;
        this.searchRoot = searchRoot;
        this.searchRootIdDomain = searchRootIdDomain;
        this.searchRootId = searchRootId;
        this.searchRecursive = searchRecursive;
        this.mappingField = mappingField;
        this.queryName = queryName;
        this.options = options;
        this.p_valueUnit = valueUnit;
        this.emptyOption = emptyOption;
        this.refresh = refresh;
        this.p_domain = domain;
        this.p_size = size;
        this.p_subTags = valueSubTags;
        this.p_context = context;
        this.viewType = viewType;
        this.noColumns = noColumns;
        this.multiSelection = multiSelection;
        this.mlKey = mlKey;
        this.mlInfos = mlInfos;
        this.p_showInLinks = showInLinks;

        if (reminderParams != null)
        {
            this.p_displayType = (String) reminderParams.elementAt (0);
            this.p_remind1Days = ((Integer) reminderParams.elementAt (1)).intValue ();
            this.p_remind1Text = (String) reminderParams.elementAt (2);
            this.p_remind1Recip = (String) reminderParams.elementAt (3);
            this.p_remind1RecipQuery = (String) reminderParams.elementAt (4);
            this.p_remind2Days = ((Integer) reminderParams.elementAt (5)).intValue ();
            this.p_remind2Text = (String) reminderParams.elementAt (6);
            this.p_remind2Recip = (String) reminderParams.elementAt (7);
            this.p_remind2RecipQuery = (String) reminderParams.elementAt (8);
            this.p_escalateDays = ((Integer) reminderParams.elementAt (9)).intValue ();
            this.p_escalateText = (String) reminderParams.elementAt (10);
            this.p_escalateRecip = (String) reminderParams.elementAt (11);
            this.p_escalateRecipQuery = (String) reminderParams.elementAt (12);
        } // if

        if (fileParams != null)
        {
            this.p_encoding = (String) fileParams.elementAt (0);
            this.p_filename = (String) fileParams.elementAt (1);
            this.p_contentType = (String) fileParams.elementAt (2);
            this.p_extension = (String) fileParams.elementAt (3);
        } // if
    } // ValueDataElement


    /**************************************************************************
     * Creates an ValueDataElement. <BR/>
     *
     * @param   fieldname       fieldname of the VALUE element
     * @param   value           value of the VALUE element
     */
    public ValueDataElement (String fieldname, String value)
    {
        this.field = fieldname;
        this.value = value;
    } // ValueDataElement


    /**************************************************************************
     * Creates an ValueDataElement. <BR/>
     *
     * @param elem ValueDataElement to be copied.
     */
    @SuppressWarnings ("unchecked") // suppress compiler warning
    public ValueDataElement (ValueDataElement elem)
    {
        // call constructor of super class ObjectReference:
        this.field = elem.field;
        this.type = elem.type;
        this.value = elem.value;
        this.mandatory = elem.mandatory;
        this.p_readonly = elem.p_readonly;
        this.info = elem.info;
        this.typeFilter = elem.typeFilter;
        this.searchRoot = elem.searchRoot;
        this.searchRootIdDomain = elem.searchRootIdDomain;
        this.searchRootId = elem.searchRootId;
        this.searchRecursive = elem.searchRecursive;
        this.mappingField = elem.mappingField;
        this.queryName = elem.queryName;
        this.options = elem.options;
        this.p_valueUnit = elem.p_valueUnit;
        this.emptyOption = elem.emptyOption;
        this.refresh = elem.refresh;
        this.p_subTags = (elem.p_subTags == null) ?
            null : (Vector<?>) elem.p_subTags.clone ();
        this.p_context = elem.p_context;
        this.viewType = elem.viewType;
        this.noColumns = elem.noColumns;
        this.multiSelection = elem.multiSelection;
        this.p_displayType = elem.p_displayType;
        this.p_remind1Days = elem.p_remind1Days;
        this.p_remind1Text = elem.p_remind1Text;
        this.p_remind1Recip = elem.p_remind1Recip;
        this.p_remind1RecipQuery = elem.p_remind1RecipQuery;
        this.p_remind2Days = elem.p_remind2Days;
        this.p_remind2Text = elem.p_remind2Text;
        this.p_remind2Recip = elem.p_remind2Recip;
        this.p_remind2RecipQuery = elem.p_remind2RecipQuery;
        this.p_escalateDays = elem.p_escalateDays;
        this.p_escalateText = elem.p_escalateText;
        this.p_escalateRecip = elem.p_escalateRecip;
        this.p_escalateRecipQuery = elem.p_escalateRecipQuery;
        this.mlKey = elem.mlKey;
        this.mlInfos = elem.mlInfos;
        this.p_showInLinks = elem.p_showInLinks;
    } // ValueDataElement


    /**************************************************************************
     * Indicates whether some other object is "equal to" this one.
     * <p>
     *
     * @param   obj   the reference object with which to compare.
     *
     * @return  <code>true</code> if this object is the same as the obj
     *          argument; <code>false</code> otherwise.
     *
     * @see     java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals (ValueDataElement obj)
    {
        if (obj == null)
        {
            return false;
        } // if

        // check change of relevant settings:
        return
            DIHelpers.compareStr (this.mandatory, obj.mandatory) &&
            DIHelpers.compareStr (this.p_readonly, obj.p_readonly) &&
            DIHelpers.compareStr (this.p_valueUnit, obj.p_valueUnit) &&
            DIHelpers.compareStr (this.searchRecursive, obj.searchRecursive) &&
            DIHelpers.compareStr (this.searchRoot, obj.searchRoot) &&
            DIHelpers.compareStr (this.searchRootIdDomain, obj.searchRootIdDomain) &&
            DIHelpers.compareStr (this.searchRootId, obj.searchRootId) &&
            DIHelpers.compareStr (this.typeFilter, obj.typeFilter) &&
            DIHelpers.compareStr (this.queryName, obj.queryName) &&
            DIHelpers.compareStr (this.emptyOption, obj.emptyOption) &&
            DIHelpers.compareStr (this.refresh, obj.refresh) &&
            DIHelpers.compareStr (this.p_context, obj.p_context) &&
            DIHelpers.compareStr (this.p_displayType, obj.p_displayType) &&
            DIHelpers.compareStr (this.mlKey, obj.mlKey) &&
            DIHelpers.compareStr (this.p_showInLinks, obj.p_showInLinks) &&
            
            // BT 20090904: Added during Removal of XML Data Files since the
            // database mapping fields have to be equal now.
            DIHelpers.compareStr (this.mappingField, obj.mappingField);
/*
            this.p_remind1Days == obj.p_remind1Days &&
//            DIHelpers.compareStr (this.p_remind1Text, obj.p_remind1Text) &&
            DIHelpers.compareStr (this.p_remind1Recip, obj.p_remind1Recip) &&
//            DIHelpers.compareStr (this.p_remind1RecipQuery, obj.p_remind1RecipQuery) &&
            this.p_remind2Days == obj.p_remind2Days &&
            DIHelpers.compareStr (this.p_remind2Text, obj.p_remind2Text) &&
            DIHelpers.compareStr (this.p_remind2Recip, obj.p_remind2Recip) &&
//            DIHelpers.compareStr (this.p_remind2RecipQuery, obj.p_remind2RecipQuery) &&
            this.p_escalateDays == obj.p_escalateDays &&
            DIHelpers.compareStr (this.p_escalateText, obj.p_escalateText) &&
            DIHelpers.compareStr (this.p_escalateRecip, obj.p_escalateRecip)
//            DIHelpers.compareStr (this.p_escalateRecipQuery, obj.p_escalateRecipQuery)
*/
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
            // concatenate the relevant fields and compute the hash code from
            // the resulting value:
            this.p_hashCode = ("" + this.field + "." + this.mandatory + "." +
                this.p_readonly + "." + this.p_valueUnit + "." +
                this.searchRecursive + "." + this.searchRoot + "." +
                this.searchRootIdDomain + "." + this.searchRootId + "." +
                this.typeFilter + "." + this.queryName + "." +
                this.emptyOption + "." + this.refresh + "." +
                this.p_displayType + "." + this.mlKey + "." + 
                this.p_showInLinks).hashCode ();
        } // if

        // return the result:
        return this.p_hashCode;
    } // hashCode


    /**************************************************************************
     * This method gets the queryResultNode. <BR/>
     * Note that a timestamp check is done. In case the query is already too
     * old invalidate the query result by returning null. <BR/>
     *
     * @return Returns the queryResultNode in case it is not too old
     */
    public Node getQueryResultNode ()
    {
        // check if a query result node has been set
        if (this.p_queryResultNode != null)
        {
            // check the time stamp of the query result
            // calculate the age of the query result in milliseconds
            long age = new Date ().getTime () - this.p_queryTimeStamp.getTime ();
            // check if the queryresult is not over the timeout limit
            // else invalidate the queryResultNode by returning null
            if ((this.refresh == null ||
                 !this.refresh.equals (DIConstants.ATTRVAL_ALWAYS)) &&
                age < DIConstants.QUERYFIELDRESULT_EXPIRES)
            {
                // return the query result node
                return this.p_queryResultNode;
            } // if (age < MAX_QUERYRESULT_AGE)
        } // if (this.p_queryResultNode != null)
        return null;
    } // getQueryResultNode


    /**************************************************************************
     * This method sets the queryResultNode and a timestamp. <BR/>
     *
     * @param queryResultNode The queryResultNode to set.
     */
    public void setQueryResultNode (Node queryResultNode)
    {
        // set the timestamp
        this.p_queryTimeStamp = new Date ();
        // set the property value:
        this.p_queryResultNode = queryResultNode;
    } // setQueryResultNode


    /***************************************************************************
     * Set the reminders for this value. <BR/>
     *
     * @param   objectOid   The oid of the object.
     * @param   objectName  Name of object. Used for reminder subject.
     * @param   app         The Application Info object.
     * @param   sess        The session info object.
     * @param   env         The current environment.
     * @param   user        The user info.
     */
    protected void setReminders (OID objectOid, String objectName,
                                 ApplicationInfo app, SessionInfo sess,
                                 Environment env, User user)
    {
        Date baseDate = DateTimeHelpers.stringToDate (this.value);
        Date oldBaseDate = null;
        int oldRemind1Days;
        int oldRemind2Days;
        int oldEscalateDays;

        // check if we have stored an old reminder value:
        if (this.p_oldReminder != null)
        {
            oldBaseDate = DateTimeHelpers.stringToDate (this.p_oldReminder.value);
        } // if

        // check if we have and old or a new base date
        // if both are null no changes are to be performed
        if (baseDate != null || oldBaseDate != null)
        {
            // store the old values in local variables because
            // the getParameters method will be called within the observer
            // service that is called in the setReminder method and this will
            // overwrite the values!!!
            oldRemind1Days = this.p_oldReminder.p_remind1Days;
            oldRemind2Days = this.p_oldReminder.p_remind2Days;
            oldEscalateDays = this.p_oldReminder.p_escalateDays;

            // set the 1. reminder
            this.setReminder (objectOid, this.field + "." + DIConstants.KEY_REMIND1,
                null,
//                DIConstants.METHOD_REMIND1,
                ValueDataElement.addDateDays (oldBaseDate, -oldRemind1Days),
                ValueDataElement.addDateDays (baseDate, -this.p_remind1Days),
                this.p_remind1Recip,
                StringHelpers.replace (DIConstants.SUBJECT_REMIND1,
                    UtilConstants.TAG_NAME, objectName),
                this.p_remind1Text,
                app, sess, env, user);
            // set the 1. reminder
            this.setReminder (objectOid, this.field + "." + DIConstants.KEY_REMIND2,
                null,
//                DIConstants.METHOD_REMIND2,
                ValueDataElement.addDateDays (oldBaseDate, -oldRemind2Days),
                ValueDataElement.addDateDays (baseDate, -this.p_remind2Days),
                this.p_remind2Recip,
                StringHelpers.replace (DIConstants.SUBJECT_REMIND2,
                    UtilConstants.TAG_NAME, objectName),
                this.p_remind2Text,
                app, sess, env, user);
            // set the escalation
            this.setReminder (objectOid, this.field + "." + DIConstants.KEY_ESCALATE,
                null,
//                DIConstants.METHOD_ESCALATE,
                ValueDataElement.addDateDays (oldBaseDate, oldEscalateDays),
                ValueDataElement.addDateDays (baseDate, this.p_escalateDays),
                this.p_escalateRecip,
                StringHelpers.replace (DIConstants.SUBJECT_ESCALATE,
                    UtilConstants.TAG_NAME, objectName),
                this.p_escalateText,
                app, sess, env, user);
        } // if (limitDate != null)
    } // setReminders


    /***************************************************************************
     * Checks if a reminder ha s to be set and performs the approtiate action.
     * <BR/>
     *
     * @param   objectOid       the oid of the object
     * @param   reminderKey     the name of the remainer key
     * @param   methodName      the name of the method to set in the reminder
     * @param   oldDate         the old date to compare
     * @param   actDate         the new date to compare with.
     * @param   recipients      Recipients of reminder notification.
     * @param   subject         Subject for notification
     * @param   reminderText    Text to be used for notification.
     * @param   app             The Application Info object.
     * @param   sess            The session info object.
     * @param   env             The current environment.
     * @param   user            The user info.
     */
    protected void setReminder (OID objectOid, String reminderKey,
                                String methodName, Date oldDate, Date actDate,
                                String recipients, String subject,
                                String reminderText, ApplicationInfo app,
                                SessionInfo sess, Environment env, User user)
    {
        M2ReminderObserverJobData jData = null;
        ObserverContext context = null;
        String observerName = null;
        int jobId = -1;
        String dateStr = "";

        // init service - get observer context:
        M2ObserverService os = new M2ObserverService (user, env, sess, app);

        try
        {
            context = os
                .getObserverContext (ObserverConstants.STANDARD_OBSERVER);
            observerName = context.getName ();
        } // try
        catch (ObserverException e)
        {
            IOHelpers.showMessage ("Error when getting observer context",
                e, app, sess, env, true);
            return;
        } // catch

        // set data for the reminder-job
        if (actDate != null)
        {
            dateStr = DateTimeHelpers.dateTimeToString (actDate);
        } // if
        else
        {
            dateStr = "";
        } // else

        // set data for the reminder-job
        jData = new M2ReminderObserverJobData (context,
            M2ReminderObserverJob.class.getName (), reminderKey, objectOid, OID
                .getEmptyOid (), dateStr, recipients, subject, reminderText,
            ValueDataElement.PARAM_UNDEFINED, ValueDataElement.PARAM_UNDEFINED,
            methodName, null, null, null);

        //
        // differ between: create/change/delete job
        // compare the dates:
        if (oldDate != null && actDate != null && !oldDate.equals (actDate))
        {
            // change the entry for reminder
            try
            {
                jobId = os.exists (observerName, jData);

                // if job already exists: unregister
                if (jobId > 0)
                {
                    os.unregisterObserverJob (observerName, jobId);
                } // if

                // register new job
                jobId = os.registerObserverJob (context.getName (), jData);
            } // try
            catch (ObserverException e)
            {
                IOHelpers.showMessage (
                    ValueDataElement.ERRM_REMINDER_OBSERVERJOB,
                    e, app, sess, env, true);
                return;
            } // catch
        } // if (oldDate != null && actDate != null)
        else if (oldDate == null && actDate != null)
        {
            // there was no previous date set
            // add the entry for reminder
            try
            {
                jobId = os.exists (observerName, jData);

                // if job already exists: unregister
                if (jobId > 0)
                {
                    os.unregisterObserverJob (observerName, jobId);
                } // if

                // register new job
                jobId = os.registerObserverJob (context.getName (), jData);
            } // try
            catch (ObserverException e)
            {
                IOHelpers.showMessage (
                    ValueDataElement.ERRM_REMINDER_OBSERVERJOB,
                    e, app, sess, env, true);
                return;
            } // catch
        } // else if (oldDate == null && actDate != null)
        else if (oldDate != null && actDate == null)
        {
            // the date has been deleted
            // delete the entry for reminder
            try
            {
                jobId = os.exists (observerName, jData);

                // if job already exists: unregister
                if (jobId > 0)
                {
                    os.unregisterObserverJob (observerName, jobId);
                } // if
            } // try
            catch (ObserverException e)
            {
                IOHelpers.showMessage (
                    "Error during deleting observer job for reminder.",
                    e, app, sess, env, true);
                return;
            } // catch
        } // else if (oldDate != null && actDate == null)
    } // setReminder


    /***************************************************************************
     * Adds an offset to a date. <BR/>
     * The offset must be in days.
     *
     * @param   baseDate    The baseDate to add the offset.
     * @param   offset      The offset to add.
     *
     * @return  The calculated date or
     *          <CODE>null</CODE> if the base date was <CODE>null</CODE> or
     *          the addition could not have been completed.
     */
    public static Date addDateDays (Date baseDate, int offset)
    {
        // check if the date is valid:
        if (baseDate != null)
        {
            // convert the offset in milliseconds and add the result to the date:
            return new Date (baseDate.getTime () + offset * 86400000);
        } // if

        // return error value:
        return null;
    } // addDateDays


    /**************************************************************************
     * Copy the value of a ValueDataElement. <BR/>
     * This method tries to directly get the value out of the other value
     * data element and stores it locally.
     *
     * @param   obj     The other value data element.
     */
    public void copyValue (ValueDataElement obj)
    {
        // check if there is another value data element defined:
        if (obj != null)                // other value data element defined?
        {
            IOHelpers.printMessage ("    " + this.type + " " + this.field +
                ": \'" + this.value + "\' -> \'" + obj.value + "\'");

            // check if the types are equal:
            if (this.type.equals (obj.type))
            {
                // copy the value:
                // ensure that a new instance of a String is created
                this.value = new String (obj.value);
            } // if
        } // if other value data element defined
    } // copyValue


    /**************************************************************************
     * This method should be used to set the value for this <code>ValueDataElement</code>.
     *
     * @param value the values to set
     */
    public void setOldValue (String value)
    {
        boolean isChanged = false;

        // store the old value
        this.setOldValueInternal (this.value);

        // check if the current value is null
        if (this.value == null)
        {
            // value has changed if the new value is not null but the old value was
            isChanged = value != null;
        } // if
        else
        {
            // value has changed if the old and new value differ
            isChanged = !this.value.equals (value);
        } // else

        // set the new value
        this.value = value;

        // set the is changed flag
        this.setIsChanged (isChanged);
    } // setValue


    /**************************************************************************
     * Returns if the object has been changed within the last request.
     *
     * @return  value of the isChanged flag.
     */
    public boolean isChanged ()
    {
        return this.p_isChanged;
    } // isChanged


    /**************************************************************************
     * Sets if the value has been within the last request.
     *
     * @param   changed The new value for the flag.
     */
    private void setIsChanged (boolean changed)
    {
        this.p_isChanged = changed;
    } // setP_isChanged


    /**************************************************************************
     * Returns the value of the element before it has been changed the last time.
     *
     * @return  The old value.
     */
    public String getOldValue ()
    {
        return this.p_oldValue;
    } // getP_oldValue


    /**************************************************************************
     * Method to store the old value of the element before it is changed.
     *
     * @param   value   The value to be set.
     */
    public void setOldValueInternal (String value)
    {
        this.p_oldValue = (value != null) ? value : "";
    } // setP_oldValue
    
    
    /**************************************************************************
     * Sets if the ValueDataElement has an attached file.
     * 
     * @param	hasFile <CODE>true</CODE> if there is a file corresponding to
     * 					the ValueDataElement, <CODE>false</CODE> otherwise
     */
    public void setFileFlag (boolean hasFile)
    {
    	this.p_hasFile = hasFile;
    } // setFileFlag
    
    
    /**************************************************************************
     * Returns if the ValueDataElement has an attached file.
     * 
     * @return	<CODE>true</CODE> if there is a file corresponding to the
     * 			ValueDataElement, <CODE>false</CODE> otherwise
     */
    public boolean getFileFlag ()
    {
    	return this.p_hasFile;
    } // getFileFlag
    
    
    /***************************************************************************
     * Initializes the multilang info for the value data element for all
     * provided locales. <BR/>
     *
     * @param   locales     The locales to init the multilang info for
     * @param   pTypeCode   The typecode
     * @param   env         The environment
     */
    public void initMultilangInfo (Collection<Locale_01> locales, String pTypeCode, Environment env)
    {       
        // Initialize the ml info map
        mlInfos = new HashMap<String, MlInfo> ();

        // Define lookup key:

        // (1) overwrite - check if ml attributes are defined
        String vdeBaseLookupKey = mlKey;
        
        if (vdeBaseLookupKey == null)
        {            
            // (2) generic lookup
            vdeBaseLookupKey = MultilingualTextProvider.getFormtemplateVdeBaseLookupKey (pTypeCode, this.field);
        } // if
        
        // Perform lookup for all locales:
        Iterator<Locale_01> it = locales.iterator ();
        while (it.hasNext ())
        {            
            Locale_01 locale = it.next ();

            // retrieve the name with the defined lookup key
            MultilingualTextInfo mlNameInfo = MultilingualTextProvider.getMultilingualTextInfo (
                MultilangConstants.RESOURCE_BUNDLE_FORMTEMPLATES_NAME,
                MultilingualTextProvider.getNameLookupKey (vdeBaseLookupKey),
                locale,
                env);
            
            // retrieve the description with the defined lookup key        
            MultilingualTextInfo mlDescriptionInfo = MultilingualTextProvider.getMultilingualTextInfo (
                MultilangConstants.RESOURCE_BUNDLE_FORMTEMPLATES_NAME,
                MultilingualTextProvider.getDescriptionLookupKey (vdeBaseLookupKey),
                locale,
                env);

            // retrieve the unit with the defined lookup key        
            MultilingualTextInfo mlUnitInfo = MultilingualTextProvider.getMultilingualTextInfo (
                MultilangConstants.RESOURCE_BUNDLE_FORMTEMPLATES_NAME,
                MultilingualTextProvider.getUnitLookupKey (vdeBaseLookupKey),
                locale,
                env);
            
            // check if something has been found
            String mlName = mlNameInfo.isFound () ?
                    // and use it
                    mlNameInfo.getMLValue () :
                    // (3) fallback - use the name
                    this.field;

            // check if something has been found
            String mlDescription = mlDescriptionInfo.isFound () ?
                    // and use it
                    mlDescriptionInfo.getMLValue () :
                    // (3) fallback - return ""
                    "";

            // check if something has been found
            String mlUnit = mlUnitInfo.isFound () ?
                    // and use it
                    mlUnitInfo.getMLValue () :
                    // (3) fallback - return ""
                    "";

            mlInfos.put (locale.getLocaleKey (), new MlInfo (mlName, mlDescription, mlUnit));
        } // while        
    } // initMultilangInfo


    /***************************************************************************
     * Provides the multilang info for the value data element for the user's
     * locale. <BR/>
     *
     * @param   The environment
     */
    public MlInfo getMlInfo (Environment env)
    {
        return mlInfos.get (MultilingualTextProvider.getUserLocale (env).getLocaleKey ());
    } // getMlInfo
    
    
    /***************************************************************************
     * Provides the multilang infos for all locales for beeing able to link
     * this structure.<BR/>
     *
     * @return multilang names
     */
    public Map<String, MlInfo> getMlInfos ()
    {
        return this.mlInfos;
    } // getMlInfos
} // class ValueDataElement

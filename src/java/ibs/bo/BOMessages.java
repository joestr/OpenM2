/*
 * Class: BOMessages.java
 */

// package:
package ibs.bo;

// imports:

/******************************************************************************
 * Messages for business objects. <BR/>
 * This abstract class contains all messages which are necessary to deal with
 * the classes delivered within this package. <P>
 * The messages can use tags for specific values to be inserted at runtime.
 * This tags can be replaced by the values with the
 * {@link ibs.util.Helpers#replace (String, String, String) Helpers.replace}
 * function.
 *
 * @version     $Id: BOMessages.java,v 1.41 2011/11/21 12:46:17 gweiss Exp $
 *
 * @author      Klaus Reimüller (KR), 980614
 ******************************************************************************
 */
public abstract class BOMessages extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BOMessages.java,v 1.41 2011/11/21 12:46:17 gweiss Exp $";

    /**
     * Name of bundle where the messages are included. <BR/>
     */
    public static String MSG_BUNDLE = "ibs_ibsbase_messages";

    /**
     * Error within oid. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_OID UtilConstants.TAG_OID}
     * is used to represent the invalid oid.
     */
    public static String ML_MSG_INCORRECTOID = "ML_MSG_INCORRECTOID";

    /**
     * If the size of the file is stored with the value 0.0 in the database
     * unknown is display. <BR/>
     */
    public static String ML_MSG_UNKNOWN = "ML_MSG_UNKNOWN";

    /**
     * There is no oid of an object. <BR/>
     */
    public static String ML_MSG_NOOID = "ML_MSG_NOOID";

    /**
     * The required function is not yet implemented. <BR/>
     */
    public static String ML_MSG_NOTIMPLEMENTED = "ML_MSG_NOTIMPLEMENTED";

    /**
     * The required object was not found. <BR/>
     */
    public static String ML_MSG_OBJECTNOTFOUND = "ML_MSG_OBJECTNOTFOUND";
    
    /**
     * The required oid was not found. <BR/>
     * The tag <A HREF="UtilConstants.html#UtilConstants.TAG_NAME">UtilConstants.TAG_NAME</A>
     * is used to represent the name of the object.
     */
    public static String ML_MSG_OBJECTOIDNOTFOUND = "ML_MSG_OBJECTOIDNOTFOUND";

    /**
     * The required object type was not found or there is no class name stored
     * with the type. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NUMBER UtilConstants.TAG_NUMBER}
     * is used to represent the id of the object type.
     */
    public static String ML_MSG_TYPENOTFOUND = "ML_MSG_TYPENOTFOUND";

    /**
     * The Java class for the required object type was not found. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME}
     * is used to represent the fully qualified name of the java class.
     */
    public static String ML_MSG_CLASSNOTFOUND = "ML_MSG_CLASSNOTFOUND";

    /**
     * Could not create instance of class because there is no permission to the
     * class or it is not possible to create an instance from the class
     * (because it is abstract or an array, etc.) or any other reason. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME}
     * is used to represent the fully qualified name of the java class.
     */
    public static String ML_MSG_INSTANTIATIONFAILED = "ML_MSG_INSTANTIATIONFAILED";

    /**
     * The Java class or the initializer for the class is not accessible. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME}
     * is used to represent the fully qualified name of the java class.
     */
    public static String ML_MSG_CLASSORINITNOTACCESSIBLE = "ML_MSG_CLASSORINITNOTACCESSIBLE";

    /**
     * Could not create instance of class because there is no permission to the
     * class or it is not possible to create an instance from the class
     * (because it is abstract or an array, etc.) or any other reason. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME}
     * is used to represent the fully qualified name of the java class.
     */
    public static String ML_MSG_INITIALIZATIONFAILED = "ML_MSG_INITIALIZATIONFAILED";

    /**
     * The user has unread entries in his inbox. <BR/>
     */
    public static String ML_MSG_UNREADMESSAGES = "ML_MSG_UNREADMESSAGES";

    /**
     * The required object was not found. <BR/>
     */
    public static String ML_MSG_NOBUTTONSFOUND = "ML_MSG_NOBUTTONSFOUND";

    /**
     * Show the actual object. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME}
     * is used to represent the name of the object.
     */
    public static String ML_MSG_SHOWOBJECT = "ML_MSG_SHOWOBJECT";

    // container content:
    /**
     * Heading of a container view. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME}
     * is used to represent the name of the container.
     */
    public static String ML_MSG_CONTAINERCONTENT = "ML_MSG_CONTAINERCONTENT";

    /**
     * The container is empty, i.e. it contains no elements. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME}
     * is used to represent the name of the container.
     */
    public static String ML_MSG_CONTAINEREMPTY = "ML_MSG_CONTAINEREMPTY";

    /**
     * The container contains more elements as can be displayed. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NUMBER UtilConstants.TAG_NUMBER}
     * is used to represent the actual number of elements.
     */
    public static String ML_MSG_TOOMUCHELEMENTS = "ML_MSG_TOOMUCHELEMENTS";

    /**
     * Display the number of elements which are allowed to be displayed. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NUMBER UtilConstants.TAG_NUMBER}
     * is used to represent the displayed number of elements.
     */
    public static String ML_MSG_DISPLAYABLEELEMENTS = "ML_MSG_DISPLAYABLEELEMENTS";

    /**
     * Ask user to rearrange sort attributes. <BR/>
     */
    public static String ML_MSG_TOOMUCHELEMENTSSEARCH = "ML_MSG_TOOMUCHELEMENTSSEARCH";

   /**
     * There is no previous element within the container. <BR/>
     */
    public static String ML_MSG_NOPREVELEMENT = "ML_MSG_NOPREVELEMENT";

    /**
     * There is no next element within the container. <BR/>
     */
    public static String ML_MSG_NONEXTELEMENT = "ML_MSG_NONEXTELEMENT";

    // number of elements message:
    /**
     * Number of elements within a container: just one element. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NUMBER UtilConstants.TAG_NUMBER}
     * is used to represent the name of the object.
     */
    public static String ML_MSG_ELEMENT = "ML_MSG_ELEMENT";

    /**
     * Number of elements within a container: 0 or > 1 elements. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NUMBER UtilConstants.TAG_NUMBER}
     * is used to represent the name of the object.
     */
    public static String ML_MSG_ELEMENTS = "ML_MSG_ELEMENTS";

    /**
     * Value shown for cut function pressed. <BR/>
     */
    public static String ML_MSG_OBJECTCUT = "ML_MSG_OBJECTCUT";

    /**
     * Value shown for multiple cut function pressed. <BR/>
     */
    public static String ML_MSG_OBJECTSCUT = "ML_MSG_OBJECTSCUT";

    /**
     * Value shown for cut function pressed. <BR/>
     */
    public static String ML_MSG_OBJECTCOPYFAIL = "ML_MSG_OBJECTCOPYFAIL";

    /**
     * Value shown for copy function pressed. <BR/>
     */
    public static String ML_MSG_OBJECTCOPY = "ML_MSG_OBJECTCOPY";

    /**
     * Value shown for multiple copy function pressed. <BR/>
     */
    public static String ML_MSG_OBJECTSCOPY = "ML_MSG_OBJECTSCOPY";

    /**
     * Value shown when you paste a BO. <BR/>
     */
    public static String ML_MSG_OBJECTPASTE = "ML_MSG_OBJECTPASTE";

    /**
     * Value shown when you paste a BO. <BR/>
     */
    public static String ML_MSG_OBJECTPASTED = "ML_MSG_OBJECTPASTED";

    /**
     * Value shown when you paste a BO. <BR/>
     */
    public static String ML_MSG_OBJECTINSERTFAIL = "ML_MSG_OBJECTINSERTFAIL";

    /**
     * Value shown when you paste a BO. <BR/>
     */
    public static String ML_MSG_NOTCHANGEABLE = "ML_MSG_NOTCHANGEABLE";

    /**
     * The object could not be created. <BR/>
     */
    public static String ML_MSG_OBJECTNOTCREATED = "ML_MSG_OBJECTNOTCREATED";

    /**
     * Confirmation message for deleting an object. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME}
     * is used to represent the name of the object.
     */
    public static String ML_MSG_OBJECTDELETECONFIRM = "ML_MSG_OBJECTDELETECONFIRM";
   
    /**
     * Confirmation message for deleting an object having some references. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME TAG_NAME}
     * is used to represent the name of the object.
     * The tag {@link ibs.util.UtilConstants#TAG_NUMBER TAG_NUMBER}
     * is used to represent the number of references to the object.
     */
    public static String ML_MSG_OBJECTDELETECONFIRMREF = "ML_MSG_OBJECTDELETECONFIRMREF";

    /**
     * Confirmation message for deleting an object having some references. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME TAG_NAME}
     * is used to represent the name of the object.
     * The tag {@link ibs.util.UtilConstants#TAG_NUMBER TAG_NUMBER}
     * is used to represent the number of references to the object.
     */
    public static String ML_MSG_LOCALEDELETECONFIRMREF = "ML_MSG_LOCALEDELETECONFIRMREF";
    
    /**
     * Confirmation message for deleting object within a list having some
     * references. <BR/>
     */
    public static String ML_MSG_LISTDELETECONFIRMREF = "ML_MSG_LISTDELETECONFIRMREF";

    /**
     * Confirmation message for deleting a container object. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME}
     * is used to represent the name of the object.
     */
    public static String ML_MSG_CONTAINERDELETECONFIRM = "ML_MSG_CONTAINERDELETECONFIRM";

    /**
     * The object was deleted. <BR/>
     */
    public static String ML_MSG_OBJECTDELETED = "ML_MSG_OBJECTDELETED";

    /**
     * The object was moved. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME TAG_NAME}
     * is used to represent the name of the object.
     */
    public static String ML_MSG_OBJECTMOVED = "ML_MSG_OBJECTMOVED";

    /**
     * The object was not marked. <BR/>
     */
    public static String ML_MSG_NOOBJECTMARKED = "ML_MSG_NOOBJECTMARKED";

    /**
     * The object could not be deleted. <BR/>
     */
    public static String ML_MSG_OBJECTNOTDELETED = "ML_MSG_OBJECTNOTDELETED";

    /**
     * The object was checked in. <BR/>
     */
    public static String ML_MSG_OBJECTCHECKEDIN = "ML_MSG_OBJECTCHECKEDIN";

    /**
     * The object was checked in. <BR/>
     */
    public static String ML_MSG_OBJECTEDITCHECKIN = "ML_MSG_OBJECTEDITCHECKIN";

    /**
     * The object was checked out. <BR/>
     */
    public static String ML_MSG_OBJECTCHECKEDOUT = "ML_MSG_OBJECTCHECKEDOUT";

    /**
     * Header of object representation. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME}
     * is used to represent the name of the object.
     */
    public static String ML_MSG_OBJHEADER_NAME = "ML_MSG_OBJHEADER_NAME";

    /**
     * Header of object representation including the actual master name. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME}
     * is used to represent the name of the object.
     * The tag {@link ibs.util.UtilConstants#TAG_NAME2 UtilConstants.TAG_NAME2}
     * is used to represent the name of the master object.
     */
    public static String ML_MSG_OBJHEADER_NAMEMASTER = "ML_MSG_OBJHEADER_NAMEMASTER";

    /**
     * Header of object representation including the actual operation. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME}
     * is used to represent the name of the object.
     * The tag {@link ibs.util.UtilConstants#TAG_OPERATION UtilConstants.TAG_OPERATION}
     * is used to represent the operation to be performed on the object.
     */
    public static String ML_MSG_OBJHEADER_NAMEOPERATION = "ML_MSG_OBJHEADER_NAMEOPERATION";

    /**
     * Header of object representation including the master name and the
     * actual operation. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME}
     * is used to represent the name of the object.
     * The tag {@link ibs.util.UtilConstants#TAG_NAME2 UtilConstants.TAG_NAME2}
     * is used to represent the name of the master object.
     * The tag {@link ibs.util.UtilConstants#TAG_OPERATION UtilConstants.TAG_OPERATION}
     * is used to represent the operation to be performed on the object.
     */
    public static String ML_MSG_OBJHEADER_NAMEMASTEROPERATION = "ML_MSG_OBJHEADER_NAMEMASTEROPERATION";

    /**
     * Header of object representation. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME}
     * is used to represent the name of the object.
     * The tag {@link ibs.util.UtilConstants#TAG_NAME3 UtilConstants.TAG_NAME2}
     * is used to represent the name of the container object.
     */
    public static String ML_MSG_OBJHEADER_NAMECONTAINER = "ML_MSG_OBJHEADER_NAMECONTAINER";

    /**
     * Header of object representation including the actual master name. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME}
     * is used to represent the name of the object.
     * The tag {@link ibs.util.UtilConstants#TAG_NAME2 UtilConstants.TAG_NAME2}
     * is used to represent the name of the master object.
     * The tag {@link ibs.util.UtilConstants#TAG_NAME3 UtilConstants.TAG_NAME2}
     * is used to represent the name of the container object.
     */
    public static String ML_MSG_OBJHEADER_NAMEMASTERCONTAINER = "ML_MSG_OBJHEADER_NAMEMASTERCONTAINER";

    /**
     * Header of object representation including the master name and the
     * actual operation. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME}
     * is used to represent the name of the object.
     * The tag {@link ibs.util.UtilConstants#TAG_NAME3 UtilConstants.TAG_NAME2}
     * is used to represent the name of the container object.
     * The tag {@link ibs.util.UtilConstants#TAG_OPERATION UtilConstants.TAG_OPERATION}
     * is used to represent the operation to be performed on the object.
     */
    public static String ML_MSG_OBJHEADER_NAMECONTAINEROPERATION = "ML_MSG_OBJHEADER_NAMECONTAINEROPERATION";

    /**
     * Header of object representation including the master name and the
     * actual operation. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME}
     * is used to represent the name of the object.
     * The tag {@link ibs.util.UtilConstants#TAG_NAME2 UtilConstants.TAG_NAME2}
     * is used to represent the name of the master object.
     * The tag {@link ibs.util.UtilConstants#TAG_NAME3 UtilConstants.TAG_NAME2}
     * is used to represent the name of the container object.
     * The tag {@link ibs.util.UtilConstants#TAG_OPERATION UtilConstants.TAG_OPERATION}
     * is used to represent the operation to be performed on the object.
     */
    public static String ML_MSG_OBJHEADER_NAMEMASTERCONTAINEROPERATION = "ML_MSG_OBJHEADER_NAMEMASTERCONTAINEROPERATION";

    /**
     * Header of object representation including just the operation. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_OPERATION UtilConstants.TAG_OPERATION}
     * is used to represent the operation to be performed on the object.
     */
    public static String ML_MSG_OBJHEADER_OPERATION = "ML_MSG_OBJHEADER_OPERATION";

    /**
     * Message to be printed if there is no object type available to create
     * a new object. <BR/>
     */
    public static String ML_MSG_NOOBJECTTYPEALLOWED = "ML_MSG_NOOBJECTTYPEALLOWED";

    /**
     * Value shown in case no elements have been found <BR/>
     */
    public static String ML_MSG_NO_ELEMENTS_FOUND = "ML_MSG_NO_ELEMENTS_FOUND";

    /**
     * Messaage for insertfail <BR/>
     */
    public static String ML_MSG_CUT_INSERT_FAIL = "ML_MSG_CUT_INSERT_FAIL";

    /**
     * The default logbookmessage <BR/>
     */
    public static String ML_LOG_DEFAULTMESSAGE = "ML_LOG_DEFAULTMESSAGE";

    /**
     * The default logbookmessage <BR/>
     */
    public static String ML_LOG_NEWMESSAGE = "ML_LOG_NEWMESSAGE";

    /**
     * The default logbookmessage <BR/>
     */
    public static String ML_LOG_READMESSAGE = "ML_LOG_READMESSAGE";

    /**
     * The default logbookmessage <BR/>
     */
    public static String ML_LOG_VIEWMESSAGE = "ML_LOG_VIEWMESSAGE";

    /**
     * The default logbookmessage <BR/>
     */
    public static String ML_LOG_EDITMESSAGE = "ML_LOG_EDITMESSAGE";

    /**
     * The default logbookmessage <BR/>
     */
    public static String ML_LOG_DELETEMESSAGE = "ML_LOG_DELETEMESSAGE";

    /**
     * The default logbookmessage <BR/>
     */
    public static String ML_LOG_LOGINMESSAGE = "ML_LOG_LOGINMESSAGE";

    /**
     * The default logbookmessage <BR/>
     */
    public static String ML_LOG_VIEWRIGHTSMESSAGE = "ML_LOG_VIEWRIGHTSMESSAGE";

    /**
     * The default logbookmessage <BR/>
     */
    public static String ML_LOG_EDITRIGHTSMESSAGE = "ML_LOG_EDITRIGHTSMESSAGE";

    /**
     * The default logbookmessage <BR/>
     */
    public static String ML_LOG_CREATELINKMESSAG = "ML_LOG_CREATELINKMESSAG";

    /**
     * The default logbookmessage <BR/>
     */
    public static String ML_LOG_DISTRIBUTEMESSAGE = "ML_LOG_DISTRIBUTEMESSAGE";

    /**
     * The forward logbookmessage <BR/>
     */
    public static String ML_LOG_FORWARDMESSAGE = "ML_LOG_FORWARDMESSAGE";

    /**
     * The default logbookmessage <BR/>
     */
    public static String ML_LOG_ADDELEMMESSAGE = "ML_LOG_ADDELEMMESSAGE";

    /**
     * The default logbookmessage <BR/>
     */
    public static String ML_LOG_DELELEMMESSAGE = "ML_LOG_DELELEMMESSAGE";

    /**
     * The default logbookmessage <BR/>
     */
    public static String ML_LOG_VIEWELEMSMESSAGE = "ML_LOG_VIEWELEMSMESSAGE";

    /**
     * The default logbookmessage <BR/>
     */
    public static String ML_LOG_CREATELINKMESSAGE = "ML_LOG_CREATELINKMESSAGE";

    /**
     * The logbookmessage for changing the processState <BR/>
     */
    public static String ML_LOG_CHANGEPROCESSSTATE = "ML_LOG_CHANGEPROCESSSTATE";

    /**
     * CutMessage <BR/>
     */
    public static String ML_MSG_ONEOBJECTISTNOTHERE = "ML_MSG_ONEOBJECTISTNOTHERE";

    /**
     * Not all Objects were affected by the current operation. <BR/>
     */
    public static String ML_MSG_NOTALLAFFECTED = "ML_MSG_NOTALLAFFECTED";

    /**
     * Message to be displayed if an object should be deleted but there are
     * still objects which depend on it.<BR/>
     */
    public static String ML_MSG_DEPENDENTOBJECTEXISTS = "ML_MSG_DEPENDENTOBJECTEXISTS";

    /**
     * Message to be displayed if an XMLDiscussion should be applyed but there
     * is no template.<BR/>
     */
    public static String ML_MSG_NOTEMPLATEEXISTS = "ML_MSG_NOTEMPLATEEXISTS";

    /**
     * token for "object sucessfully distributed" message<BR/>
     */
    public static String ML_MSG_OBJECTDISTRIBUTED = "ML_MSG_OBJECTDISTRIBUTED";

    /**
     * token for "distribution sucessfully performed" message<BR/>
     */
    public static String ML_MSG_OBJECTSDISTRIBUTED = "ML_MSG_OBJECTSDISTRIBUTED";

    /**
     * message that there were no objects selected for notification <BR/>
     */
    public static String ML_MSG_NOOBJECTSSELECTED = "ML_MSG_NOOBJECTSSELECTED";

    /**
     * Message to be displayd when no search filters specifies. <BR/>
     */
    public static String ML_MSG_NOSEARCHFILTERS = "ML_MSG_NOSEARCHFILTERS";

    // Messages to be represented to the user:
    /**
     * Prefix for NoAccess messages. <BR/>
     */
    public static String ML_MSG_NOACCESS_PREFIX = "ML_MSG_NOACCESS_PREFIX";

    /**
     * Message to be shown if user want`s to put a product in the shoppingcart but ther are no
     * existing prices for this product. <BR/>
     */
    public static String ML_MSG_NOPRICES = "ML_MSG_NOPRICES";

    /**
     * Message to be shown if user want`s to put a product in the shoppingcart but ther are no
     * existing sortments for this product. <BR/>
     */
    public static String ML_MSG_NOASSORTMENTS = "ML_MSG_NOASSORTMENTS";

    /**
     * Message for: what is the home page path of a domain. <BR/>
     */
    public static String ML_MSG_DOMAINHOMEPAGEPATH_DESCRIPTION = "ML_MSG_DOMAINHOMEPAGEPATH_DESCRIPTION";

    /**
     * Message for: what is the secure-mode for a domain and when
     * is it usable. <BR/>
     */
    public static String ML_MSG_DOMAINSSLREQUIRED_DESCRIPTION = "ML_MSG_DOMAINSSLREQUIRED_DESCRIPTION";

    /**
     * Message for: what happens if the user tries to change the scheme of a
     * domain. <BR/>
     */
    public static String ML_MSG_DOMAINSCHEMECHANGE_DESCRIPTION = "ML_MSG_DOMAINSCHEMECHANGE_DESCRIPTION";


    /**
     * Message to be displayed when the name of an object is already given (applies to user). <BR/>
     */
    public static String ML_MSG_NAMEALREADYGIVEN = "ML_MSG_NAMEALREADYGIVEN";

    /**
     * Message to be displayed when the rights where set recursively. <BR/>
     */
    public static String ML_MSG_SETRIGHTSREC_OK = "ML_MSG_SETRIGHTSREC_OK";

    /**
     * Message enter number greater or equal 0. <BR/>
     */
    public static String ML_MSG_ENTER_NUMBER_GE_0 = "ML_MSG_ENTER_NUMBER_GE_0";

    /**
     * Message no constraint question. <BR/>
     */
    public static String ML_MSG_NO_CONSTRAINT_QUESTION = "ML_MSG_NO_CONSTRAINT_QUESTION";

    /**
     * Message possible long evaluation. <BR/>
     */
    public static String ML_MSG_POSSIBLE_LONG_EVALUATION = "ML_MSG_POSSIBLE_LONG_EVALUATION";

    /**
     * Message for datatype bounds alerts
     */
    public static String ML_MSG_FORMVALIDATIONRANGE_NOTIMPL = "ML_MSG_FORMVALIDATIONRANGE_NOTIMPL";

    /**
     * Message for deletion of default locale
     */
    public static String ML_MSG_DEFAULT_LOCALE_CAN_NOT_BE_DELETED = "ML_MSG_DEFAULT_LOCALE_CAN_NOT_BE_DELETED";
    
    /**
     * if the container path is invalid.<BR/>
     */
    public static String ML_MSG_INVALIDPATH = "ML_MSG_INVALIDPATH";

    /**
     * Message if not all objects can be deleted. <BR/>
     */
    public static String ML_MSG_NOTALLDELETEABLE = "ML_MSG_NOTALLDELETEABLE";

    /**
     * The container is empty, i.e. it contains no elements. <BR/>
     * The tag {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME}
     * is used to represent the name of the container.
     */
    public static String ML_MSG_CREATE_OBJECT_IN_ALTERNATIVE_PATH = "ML_MSG_CREATE_OBJECT_IN_ALTERNATIVE_PATH";
    
    /**
     * Message for removal of default locale flag
     */
    public static String ML_MSG_DEFAULT_LOCALE_FLAG_CAN_NOT_BE_REMOVED = "ML_MSG_DEFAULT_LOCALE_FLAG_CAN_NOT_BE_REMOVED";

    /**
     * Message for change of default locale flag
     */
    public static String ML_MSG_DEFAULT_LOCALE_CHANGED = "ML_MSG_DEFAULT_LOCALE_CHANGED";

    /**
     * Message for adding a new locale
     */
    public static String ML_MSG_LOCALE_ADDED = "ML_MSG_LOCALE_ADDED";

    /**
     * Message for no layout defined for user
     */
    public static String MSG_NO_LAYOUT_DEF_FOR_USER = "MSG_NO_LAYOUT_DEF_FOR_USER";

    /**
     * Message for no layout found for user
     */
    public static String MSG_NO_LAYOUT_FOUND_FOR_USER = "MSG_NO_LAYOUT_FOUND_FOR_USER";

    /**
     * Message for login info page
     */
    public static String MSG_LOGIN_INFO = "MSG_LOGIN_INFO";
    
    /**
     * Message for please contact info
     */
    public static String MSG_PLEASE_CONTACT_YOUR = "MSG_PLEASE_CONTACT_YOUR";
    
    
    
    /**
     * No access message without name for right no. 0. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME0 = "ML_MSG_NOACCESS_NONAME0";
    /**
     * No access message without name for right no. 1. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME1 = "ML_MSG_NOACCESS_NONAME1";
    /**
     * No access message without name for right no. 2. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME2 = "ML_MSG_NOACCESS_NONAME2";
    /**
     * No access message without name for right no. 3. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME3 = "ML_MSG_NOACCESS_NONAME3";
    /**
     * No access message without name for right no. 4. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME4 = "ML_MSG_NOACCESS_NONAME4";
    /**
     * No access message without name for right no. 5. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME5 = "ML_MSG_NOACCESS_NONAME5";
    /**
     * No access message without name for right no. 6. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME6 = "ML_MSG_NOACCESS_NONAME6";
    /**
     * No access message without name for right no. 7. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME7 = "ML_MSG_NOACCESS_NONAME7";
    /**
     * No access message without name for right no. 8. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME8 = "ML_MSG_NOACCESS_NONAME8";
    /**
     * No access message without name for right no. 9. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME9 = "ML_MSG_NOACCESS_NONAME9";
    /**
     * No access message without name for right no. 10. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME10 = "ML_MSG_NOACCESS_NONAME10";
    /**
     * No access message without name for right no. 11. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME11 = "ML_MSG_NOACCESS_NONAME11";
    /**
     * No access message without name for right no. 12. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME12 = "ML_MSG_NOACCESS_NONAME12";
    /**
     * No access message without name for right no. 13. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME13 = "ML_MSG_NOACCESS_NONAME13";
    /**
     * No access message without name for right no. 14. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME14 = "ML_MSG_NOACCESS_NONAME14";
    /**
     * No access message without name for right no. 15. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME15 = "ML_MSG_NOACCESS_NONAME15";
    /**
     * No access message without name for right no. 16. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME16 = "ML_MSG_NOACCESS_NONAME16";
    /**
     * No access message without name for right no. 17. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME17 = "ML_MSG_NOACCESS_NONAME17";
    /**
     * No access message without name for right no. 18. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME18 = "ML_MSG_NOACCESS_NONAME18";
    /**
     * No access message without name for right no. 19. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME19 = "ML_MSG_NOACCESS_NONAME19";
    /**
     * No access message without name for right no. 20. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME20 = "ML_MSG_NOACCESS_NONAME20";
    /**
     * No access message without name for right no. 21. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME21 = "ML_MSG_NOACCESS_NONAME21";
    /**
     * No access message without name for right no. 22. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME22 = "ML_MSG_NOACCESS_NONAME22";
    /**
     * No access message without name for right no. 23. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME23 = "ML_MSG_NOACCESS_NONAME23";
    /**
     * No access message without name for right no. 24. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME24 = "ML_MSG_NOACCESS_NONAME24";
    /**
     * No access message without name for right no. 25. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME25 = "ML_MSG_NOACCESS_NONAME25";
    /**
     * No access message without name for right no. 26. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME26 = "ML_MSG_NOACCESS_NONAME26";
    /**
     * No access message without name for right no. 27. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME27 = "ML_MSG_NOACCESS_NONAME27";
    /**
     * No access message without name for right no. 28. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME28 = "ML_MSG_NOACCESS_NONAME28";
    /**
     * No access message without name for right no. 29. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME29 = "ML_MSG_NOACCESS_NONAME29";
    /**
     * No access message without name for right no. 30. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME30 = "ML_MSG_NOACCESS_NONAME30";
    /**
     * No access message without name for right no. 31. <BR/>
     */
    public static String ML_MSG_NOACCESS_NONAME31 = "ML_MSG_NOACCESS_NONAME31";

    
    /**
     * Messages to be shown to the user, when there is no access allowed to
     * an object by one of the operations. <BR/>
     * The Tag {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME}
     * is used to represent the name of the object.
     * This Tag can be replaced by the name of the object with the
     * {@link ibs.util.Helpers#replace (java.lang.String, java.lang.String, java.lang.String)
     * Helpers.replace} function.
     */
    public static String [] MSG_NOACCESS_NONAME =
    {
        BOMessages.ML_MSG_NOACCESS_NONAME0,
        BOMessages.ML_MSG_NOACCESS_NONAME1,
        BOMessages.ML_MSG_NOACCESS_NONAME2,
        BOMessages.ML_MSG_NOACCESS_NONAME3,
        BOMessages.ML_MSG_NOACCESS_NONAME4,
        BOMessages.ML_MSG_NOACCESS_NONAME5,
        BOMessages.ML_MSG_NOACCESS_NONAME6,
        BOMessages.ML_MSG_NOACCESS_NONAME7,

        BOMessages.ML_MSG_NOACCESS_NONAME8,
        BOMessages.ML_MSG_NOACCESS_NONAME9,
        BOMessages.ML_MSG_NOACCESS_NONAME10,
        BOMessages.ML_MSG_NOACCESS_NONAME11,
        BOMessages.ML_MSG_NOACCESS_NONAME12,
        BOMessages.ML_MSG_NOACCESS_NONAME13,
        BOMessages.ML_MSG_NOACCESS_NONAME14,
        BOMessages.ML_MSG_NOACCESS_NONAME15,

        BOMessages.ML_MSG_NOACCESS_NONAME16,
        BOMessages.ML_MSG_NOACCESS_NONAME17,
        BOMessages.ML_MSG_NOACCESS_NONAME18,
        BOMessages.ML_MSG_NOACCESS_NONAME19,
        BOMessages.ML_MSG_NOACCESS_NONAME20,
        BOMessages.ML_MSG_NOACCESS_NONAME21,
        BOMessages.ML_MSG_NOACCESS_NONAME22,
        BOMessages.ML_MSG_NOACCESS_NONAME23,

        BOMessages.ML_MSG_NOACCESS_NONAME24,
        BOMessages.ML_MSG_NOACCESS_NONAME25,
        BOMessages.ML_MSG_NOACCESS_NONAME26,
        BOMessages.ML_MSG_NOACCESS_NONAME27,
        BOMessages.ML_MSG_NOACCESS_NONAME28,
        BOMessages.ML_MSG_NOACCESS_NONAME29,
        BOMessages.ML_MSG_NOACCESS_NONAME30,
        BOMessages.ML_MSG_NOACCESS_NONAME31,
    }; // MSG_NOACCESS_NONAME

    
    
    /**
     * No access message with name for right no. 0. <BR/>
     */
    public static String ML_MSG_NOACCESS0 = "ML_MSG_NOACCESS0";
    /**
     * No access message with name for right no. 1. <BR/>
     */
    public static String ML_MSG_NOACCESS1 = "ML_MSG_NOACCESS1";
    /**
     * No access message with name for right no. 2. <BR/>
     */
    public static String ML_MSG_NOACCESS2 = "ML_MSG_NOACCESS2";
    /**
     * No access message with name for right no. 3. <BR/>
     */
    public static String ML_MSG_NOACCESS3 = "ML_MSG_NOACCESS3";
    /**
     * No access message with name for right no. 4. <BR/>
     */
    public static String ML_MSG_NOACCESS4 = "ML_MSG_NOACCESS4";
    /**
     * No access message with name for right no. 5. <BR/>
     */
    public static String ML_MSG_NOACCESS5 = "ML_MSG_NOACCESS5";
    /**
     * No access message with name for right no. 6. <BR/>
     */
    public static String ML_MSG_NOACCESS6 = "ML_MSG_NOACCESS6";
    /**
     * No access message with name for right no. 7. <BR/>
     */
    public static String ML_MSG_NOACCESS7 = "ML_MSG_NOACCESS7";
    /**
     * No access message with name for right no. 8. <BR/>
     */
    public static String ML_MSG_NOACCESS8 = "ML_MSG_NOACCESS8";
    /**
     * No access message with name for right no. 9. <BR/>
     */
    public static String ML_MSG_NOACCESS9 = "ML_MSG_NOACCESS9";
    /**
     * No access message with name for right no. 10. <BR/>
     */
    public static String ML_MSG_NOACCESS10 = "ML_MSG_NOACCESS10";
    /**
     * No access message with name for right no. 11. <BR/>
     */
    public static String ML_MSG_NOACCESS11 = "ML_MSG_NOACCESS11";
    /**
     * No access message with name for right no. 12. <BR/>
     */
    public static String ML_MSG_NOACCESS12 = "ML_MSG_NOACCESS12";
    /**
     * No access message with name for right no. 13. <BR/>
     */
    public static String ML_MSG_NOACCESS13 = "ML_MSG_NOACCESS13";
    /**
     * No access message with name for right no. 14. <BR/>
     */
    public static String ML_MSG_NOACCESS14 = "ML_MSG_NOACCESS14";
    /**
     * No access message with name for right no. 15. <BR/>
     */
    public static String ML_MSG_NOACCESS15 = "ML_MSG_NOACCESS15";
    /**
     * No access message with name for right no. 16. <BR/>
     */
    public static String ML_MSG_NOACCESS16 = "ML_MSG_NOACCESS16";
    /**
     * No access message with name for right no. 17. <BR/>
     */
    public static String ML_MSG_NOACCESS17 = "ML_MSG_NOACCESS17";
    /**
     * No access message with name for right no. 18. <BR/>
     */
    public static String ML_MSG_NOACCESS18 = "ML_MSG_NOACCESS18";
    /**
     * No access message with name for right no. 19. <BR/>
     */
    public static String ML_MSG_NOACCESS19 = "ML_MSG_NOACCESS19";
    /**
     * No access message with name for right no. 20. <BR/>
     */
    public static String ML_MSG_NOACCESS20 = "ML_MSG_NOACCESS20";
    /**
     * No access message with name for right no. 21. <BR/>
     */
    public static String ML_MSG_NOACCESS21 = "ML_MSG_NOACCESS21";
    /**
     * No access message with name for right no. 22. <BR/>
     */
    public static String ML_MSG_NOACCESS22 = "ML_MSG_NOACCESS22";
    /**
     * No access message with name for right no. 23. <BR/>
     */
    public static String ML_MSG_NOACCESS23 = "ML_MSG_NOACCESS23";
    /**
     * No access message with name for right no. 24. <BR/>
     */
    public static String ML_MSG_NOACCESS24 = "ML_MSG_NOACCESS24";
    /**
     * No access message with name for right no. 25. <BR/>
     */
    public static String ML_MSG_NOACCESS25 = "ML_MSG_NOACCESS25";
    /**
     * No access message with name for right no. 26. <BR/>
     */
    public static String ML_MSG_NOACCESS26 = "ML_MSG_NOACCESS26";
    /**
     * No access message with name for right no. 27. <BR/>
     */
    public static String ML_MSG_NOACCESS27 = "ML_MSG_NOACCESS27";
    /**
     * No access message with name for right no. 28. <BR/>
     */
    public static String ML_MSG_NOACCESS28 = "ML_MSG_NOACCESS28";
    /**
     * No access message with name for right no. 29. <BR/>
     */
    public static String ML_MSG_NOACCESS29 = "ML_MSG_NOACCESS29";
    /**
     * No access message with name for right no. 30. <BR/>
     */
    public static String ML_MSG_NOACCESS30 = "ML_MSG_NOACCESS30";
    /**
     * No access message with name for right no. 31. <BR/>
     */
    public static String ML_MSG_NOACCESS31 = "ML_MSG_NOACCESS31";
    
    
    /**
     * Messages to be shown to the user, when there is no access allowed to
     * an object by one of the operations. <BR/>
     * The Tag {@link ibs.util.UtilConstants#TAG_NAME UtilConstants.TAG_NAME}
     * is used to represent the name of the object.
     * This Tag can be replaced by the name of the object with the
     * {@link ibs.util.Helpers#replace (java.lang.String, java.lang.String, java.lang.String)
     * Helpers.replace} function.
     */
    public static String [] MSG_NOACCESS =
    {
        BOMessages.ML_MSG_NOACCESS0,
        BOMessages.ML_MSG_NOACCESS1,
        BOMessages.ML_MSG_NOACCESS2,
        BOMessages.ML_MSG_NOACCESS3,
        BOMessages.ML_MSG_NOACCESS4,
        BOMessages.ML_MSG_NOACCESS5,
        BOMessages.ML_MSG_NOACCESS6,
        BOMessages.ML_MSG_NOACCESS7,

        BOMessages.ML_MSG_NOACCESS8,
        BOMessages.ML_MSG_NOACCESS9,
        BOMessages.ML_MSG_NOACCESS10,
        BOMessages.ML_MSG_NOACCESS11,
        BOMessages.ML_MSG_NOACCESS12,
        BOMessages.ML_MSG_NOACCESS13,
        BOMessages.ML_MSG_NOACCESS14,
        BOMessages.ML_MSG_NOACCESS15,

        BOMessages.ML_MSG_NOACCESS16,
        BOMessages.ML_MSG_NOACCESS17,
        BOMessages.ML_MSG_NOACCESS18,
        BOMessages.ML_MSG_NOACCESS19,
        BOMessages.ML_MSG_NOACCESS20,
        BOMessages.ML_MSG_NOACCESS21,
        BOMessages.ML_MSG_NOACCESS22,
        BOMessages.ML_MSG_NOACCESS23,

        BOMessages.ML_MSG_NOACCESS24,
        BOMessages.ML_MSG_NOACCESS25,
        BOMessages.ML_MSG_NOACCESS26,
        BOMessages.ML_MSG_NOACCESS27,
        BOMessages.ML_MSG_NOACCESS28,
        BOMessages.ML_MSG_NOACCESS29,
        BOMessages.ML_MSG_NOACCESS30,
        BOMessages.ML_MSG_NOACCESS31,

    }; // MSG_NOACCESS
    
    
    
//    /***********************************************************************
//     * Set the dependent properties. <BR/>
//     * properties from this and other files (initialized Arrays)
//     * that are build from the Attributes of this File.
//     */
    
//    commented out by gw
//    reason: empty function
//    public static void setDependentProperties ()
//    {
        // TODO RB: Remove this part after all parts are migrated to MLI usage
/*
        BOMessages.MSG_NOACCESS_NONAME[0] = BOMessages.MSG_NOACCESS_NONAME0;
        BOMessages.MSG_NOACCESS_NONAME[1] = BOMessages.MSG_NOACCESS_NONAME1;
        BOMessages.MSG_NOACCESS_NONAME[2] = BOMessages.MSG_NOACCESS_NONAME2;
        BOMessages.MSG_NOACCESS_NONAME[3] = BOMessages.MSG_NOACCESS_NONAME3;
        BOMessages.MSG_NOACCESS_NONAME[4] = BOMessages.MSG_NOACCESS_NONAME4;
        BOMessages.MSG_NOACCESS_NONAME[5] = BOMessages.MSG_NOACCESS_NONAME5;
        BOMessages.MSG_NOACCESS_NONAME[6] = BOMessages.MSG_NOACCESS_NONAME6;
        BOMessages.MSG_NOACCESS_NONAME[7] = BOMessages.MSG_NOACCESS_NONAME7;
        BOMessages.MSG_NOACCESS_NONAME[8] = BOMessages.MSG_NOACCESS_NONAME8;
        BOMessages.MSG_NOACCESS_NONAME[9] = BOMessages.MSG_NOACCESS_NONAME9;
        BOMessages.MSG_NOACCESS_NONAME[10] = BOMessages.MSG_NOACCESS_NONAME10;
        BOMessages.MSG_NOACCESS_NONAME[11] = BOMessages.MSG_NOACCESS_NONAME11;
        BOMessages.MSG_NOACCESS_NONAME[12] = BOMessages.MSG_NOACCESS_NONAME12;
        BOMessages.MSG_NOACCESS_NONAME[13] = BOMessages.MSG_NOACCESS_NONAME13;
        BOMessages.MSG_NOACCESS_NONAME[14] = BOMessages.MSG_NOACCESS_NONAME14;
        BOMessages.MSG_NOACCESS_NONAME[15] = BOMessages.MSG_NOACCESS_NONAME15;
        BOMessages.MSG_NOACCESS_NONAME[16] = BOMessages.MSG_NOACCESS_NONAME16;
        BOMessages.MSG_NOACCESS_NONAME[17] = BOMessages.MSG_NOACCESS_NONAME17;
        BOMessages.MSG_NOACCESS_NONAME[18] = BOMessages.MSG_NOACCESS_NONAME18;
        BOMessages.MSG_NOACCESS_NONAME[19] = BOMessages.MSG_NOACCESS_NONAME19;
        BOMessages.MSG_NOACCESS_NONAME[20] = BOMessages.MSG_NOACCESS_NONAME20;
        BOMessages.MSG_NOACCESS_NONAME[21] = BOMessages.MSG_NOACCESS_NONAME21;
        BOMessages.MSG_NOACCESS_NONAME[22] = BOMessages.MSG_NOACCESS_NONAME22;
        BOMessages.MSG_NOACCESS_NONAME[23] = BOMessages.MSG_NOACCESS_NONAME23;
        BOMessages.MSG_NOACCESS_NONAME[24] = BOMessages.MSG_NOACCESS_NONAME24;
        BOMessages.MSG_NOACCESS_NONAME[25] = BOMessages.MSG_NOACCESS_NONAME25;
        BOMessages.MSG_NOACCESS_NONAME[26] = BOMessages.MSG_NOACCESS_NONAME26;
        BOMessages.MSG_NOACCESS_NONAME[27] = BOMessages.MSG_NOACCESS_NONAME27;
        BOMessages.MSG_NOACCESS_NONAME[28] = BOMessages.MSG_NOACCESS_NONAME28;
        BOMessages.MSG_NOACCESS_NONAME[29] = BOMessages.MSG_NOACCESS_NONAME29;
        BOMessages.MSG_NOACCESS_NONAME[30] = BOMessages.MSG_NOACCESS_NONAME30;
        BOMessages.MSG_NOACCESS_NONAME[31] = BOMessages.MSG_NOACCESS_NONAME31;

        BOMessages.MSG_NOACCESS[0] = BOMessages.MSG_NOACCESS0;
        BOMessages.MSG_NOACCESS[1] = BOMessages.MSG_NOACCESS1;
        BOMessages.MSG_NOACCESS[2] = BOMessages.MSG_NOACCESS2;
        BOMessages.MSG_NOACCESS[3] = BOMessages.MSG_NOACCESS3;
        BOMessages.MSG_NOACCESS[4] = BOMessages.MSG_NOACCESS4;
        BOMessages.MSG_NOACCESS[5] = BOMessages.MSG_NOACCESS5;
        BOMessages.MSG_NOACCESS[6] = BOMessages.MSG_NOACCESS6;
        BOMessages.MSG_NOACCESS[7] = BOMessages.MSG_NOACCESS7;
        BOMessages.MSG_NOACCESS[8] = BOMessages.MSG_NOACCESS8;
        BOMessages.MSG_NOACCESS[9] = BOMessages.MSG_NOACCESS9;
        BOMessages.MSG_NOACCESS[10] = BOMessages.MSG_NOACCESS10;
        BOMessages.MSG_NOACCESS[11] = BOMessages.MSG_NOACCESS11;
        BOMessages.MSG_NOACCESS[12] = BOMessages.MSG_NOACCESS12;
        BOMessages.MSG_NOACCESS[13] = BOMessages.MSG_NOACCESS13;
        BOMessages.MSG_NOACCESS[14] = BOMessages.MSG_NOACCESS14;
        BOMessages.MSG_NOACCESS[15] = BOMessages.MSG_NOACCESS15;
        BOMessages.MSG_NOACCESS[16] = BOMessages.MSG_NOACCESS16;
        BOMessages.MSG_NOACCESS[17] = BOMessages.MSG_NOACCESS17;
        BOMessages.MSG_NOACCESS[18] = BOMessages.MSG_NOACCESS18;
        BOMessages.MSG_NOACCESS[19] = BOMessages.MSG_NOACCESS19;
        BOMessages.MSG_NOACCESS[20] = BOMessages.MSG_NOACCESS20;
        BOMessages.MSG_NOACCESS[21] = BOMessages.MSG_NOACCESS21;
        BOMessages.MSG_NOACCESS[22] = BOMessages.MSG_NOACCESS22;
        BOMessages.MSG_NOACCESS[23] = BOMessages.MSG_NOACCESS23;
        BOMessages.MSG_NOACCESS[24] = BOMessages.MSG_NOACCESS24;
        BOMessages.MSG_NOACCESS[25] = BOMessages.MSG_NOACCESS25;
        BOMessages.MSG_NOACCESS[26] = BOMessages.MSG_NOACCESS26;
        BOMessages.MSG_NOACCESS[27] = BOMessages.MSG_NOACCESS27;
        BOMessages.MSG_NOACCESS[28] = BOMessages.MSG_NOACCESS28;
        BOMessages.MSG_NOACCESS[29] = BOMessages.MSG_NOACCESS29;
        BOMessages.MSG_NOACCESS[30] = BOMessages.MSG_NOACCESS30;
        BOMessages.MSG_NOACCESS[31] = BOMessages.MSG_NOACCESS31;
*/
//    } // setDependentProperties

} // class BOMessages

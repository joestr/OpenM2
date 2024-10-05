/*
 * Class: BOListConstants.java
 */

// package:
package ibs.bo;

// imports:
//KR TODO: cyclic dependency
import ibs.bo.BOTokens;


/******************************************************************************
 * Constants regarding list of business objects. <BR/>
 *
 * @version     $Id: BOListConstants.java,v 1.10 2010/04/13 15:55:57 rburgermann Exp $
 *
 * @author      Klaus, 15.10.2003
 ******************************************************************************
 */
public abstract class BOListConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BOListConstants.java,v 1.10 2010/04/13 15:55:57 rburgermann Exp $";


    // values for lists
    /**
     * Headings of columns. <BR/>
     * These headings are shown at the top of lists.
     */
    public static final String[] LST_HEADINGS =
    {
        BOTokens.ML_NAME,
        BOTokens.ML_TYPE,
        BOTokens.ML_OWNER,
        BOTokens.ML_CHANGED,
    }; // LST_HEADINGS

    /**
     * Reduced Headings of standard list columns. <BR/>
     */
    public static final String[] LST_HEADINGS_REDUCED =
    {
        BOListConstants.LST_HEADINGS[0],
    }; // LST_HEADINGS_REDUCED


    // attributes of elements within a container view used for ordering:
    /**
     * Name of a container column. <BR/>
     * These attributes of the table ibs_Object are used for ordering the elements
     * of a container.
     */
    public static final String[] LST_ORDERINGS =
    {
        "name",
        "typeName",
        "owner",
        "lastChanged",
    }; // LST_ORDERINGS

    /**
     * Name of a container column. <BR/>
     * These attributes of the table ibs_Object are used for ordering the elements
     * of a container in reduced list
     */
    public static final String[] LST_ORDERINGS_REDUCED =
    {
        BOListConstants.LST_ORDERINGS[0],
    }; // LST_ORDERINGS_REDUCED

    /**
     * Default column number for ordering. <BR/>
     */
    public static final int LST_DEFAULTORDERING = 0;

    /**
     * width for the new column. <BR/>
     */
    public static final String LST_NEWCOLWIDTH = "13";

    /**
     *
     */
    public static final String[] LST_HEADINGS_SENTOBJECTCONTAINER =
    {
        BOTokens.ML_SUBJECT,
        BOTokens.ML_ACTIVITIES,
        BOTokens.ML_SENTDATE,
    }; // LST_HEADINGS_SENTOBJECTCONTAINER

    /**
     *
     */
    public static final String[] LST_ORDERINGS_SENTOBJECTCONTAINER =
    {
        "name",
        "activities",
        "creationDate",
    }; // LST_ORDERINGS_SENTOBJECTCONTAINER

    /**
     * Default column number for ordering for sentObjectContainer. <BR/>
     */
    public static final int LST_SENTOBJECTDEFAULTORDERING = 2;

    // start init headings and orderings:
    /**
     *
     */
    public static final String[] LST_HEADINGS_ATTACHMENTCONTAINER =
    {
        BOTokens.ML_NAME,
        BOTokens.ML_ATTACHMENTTYPE,
        BOTokens.ML_SOURCENAME,
        BOTokens.ML_FILESIZE,
        BOTokens.ML_CREATIONDATE,
    }; // LST_HEADINGS_ATTACHMENTCONTAINER

    // start init headings and orderings:
    /**
     *
     */
    public static final String[] LST_HEADINGS_ATTACHMENTCONTAINERREDUCED =
    {
        BOTokens.ML_NAME,
        BOTokens.ML_SOURCENAME,
        BOTokens.ML_FILESIZE,
    }; // LST_HEADINGS_ATTACHMENTCONTAINER

    /**
     *
     */
    public static final String[] LST_ORDERINGS_ATTACHMENTCONTAINER =
    {
        "name",
        "attachmentType",
        "sourceName",
        "filesize",
        "creationDate",
    }; // LST_ORDERINGS_ATTACHMENTCONTAINER

    /**
     *
     */
    public static final String[] LST_ORDERINGS_ATTACHMENTCONTAINERREDUCED =
    {
        "name",
        "sourceName",
        "filesize",
    }; // LST_ORDERINGS_ATTACHMENTCONTAINER

    /**
     *
     */
    public static final String[] LST_HEADINGS_NEWSCONTAINERREDUCED =
    {
        BOListConstants.LST_HEADINGS[0],
        BOListConstants.LST_HEADINGS[3],
    }; // LST_ORDERINGS_REDUCED

    /**
     *
     */
    public static final String[] LST_ORDERINGS_NEWSCONTAINERREDUCED =
    {
        BOListConstants.LST_ORDERINGS[0],
        BOListConstants.LST_ORDERINGS[3],
    }; // LST_ORDERINGS_NEWSCONTAINERREDUCED

    /**
     *
     */
    public static final String[] LST_HEADINGS_HELPCONTAINER =
    {
        BOTokens.ML_NAME,
        BOTokens.ML_GOAL,
        BOTokens.ML_TYPE,
        BOTokens.ML_OWNER,
        BOTokens.ML_CHANGED,
    }; // LST_ORDERINGS_REDUCED

    /**
     *
     */
    public static final String[] LST_ORDERINGS_HELPCONTAINER =
    {
        "name",
        null,
        "typeName",
        "owner",
        "lastChanged",
    }; // LST_ORDERINGS_HELPCONTAINER

    /**
     *
     */
    public static final String[] LST_HEADINGS_HELPCONTAINERREDUCED =
    {
        BOListConstants.LST_HEADINGS_HELPCONTAINER[0],
        BOListConstants.LST_HEADINGS_HELPCONTAINER[1],
    }; // LST_HEADINGS_HELPCONTAINERREDUCED

    /**
     *
     */
    public static final String[] LST_ORDERINGS_HELPCONTAINERREDUCED =
    {
        BOListConstants.LST_ORDERINGS_HELPCONTAINER[0],
        BOListConstants.LST_ORDERINGS_HELPCONTAINER[1],
    }; // LST_ORDERINGS_HELPCONTAINERREDUCED

    /**
     *
     */
    public static final String[] LST_HEADINGS_INBOX =
    {
        BOTokens.ML_SUBJECT,
        BOTokens.ML_DISTRIBUTENAME,
        BOTokens.ML_ACTIVITIES,
        BOTokens.ML_RECEIVED,
        BOTokens.ML_SENDER,
    }; // LST_HEADINGS_INBOX

    /**
     *
     */
    public static final String[] LST_ORDERINGS_INBOX =
    {
        "name",
        "distributedName",
        "activities",
        "creationDate",
        "sender",
    }; // LST_ORDERINGS_INBOX

    /**
     * Default column number for ordering for inbox. <BR/>
     */
    public static final int LST_INBOXDEFAULTORDERING = 3;

    // start init headings and orderings:
    /**
     *
     */
    public static final String[] LST_HEADINGS_LOGCONTAINER =
    {
        BOTokens.ML_NAMEOFOBJECT,
        BOTokens.ML_AKTEURNAME,
        BOTokens.ML_ACTION,
        BOTokens.ML_ACTIONDATE,
    }; // LST_HEADINGS_ATTACHMENTCONTAINER

    /**
     *
     */
    public static final String[] LST_ORDERINGS_LOGCONTAINER =
    {
        "objectName",
        "fullName",
        "action",
        "actionDate",
    }; // LST_ORDERINGS_ATTACHMENTCONTAINER

    /**
    *
    */
    public static final String[] LST_HEADINGS_LOGCONTAINER_ENTRY =
    {
        BOTokens.ML_FIELDNAME,
        BOTokens.ML_OLD_VALUE,
        BOTokens.ML_VALUE,
    }; // LST_HEADINGS_LOGCONTAINER_ENTRY

    /**
     * Default column number for ordering for logContainer. <BR/>
     */
    public static final int LST_LOGDEFAULTORDERING = 3;


    /**
     *
     */
    public static final String[] LST_HEADINGS_RECIPIENT =
    {
        BOTokens.ML_RECIPIENTENNAME,
        BOTokens.ML_OBJECTNAME,
        BOTokens.ML_SENTDATE,
        BOTokens.ML_READDATE,
    }; // LST_HEADINGS_RECIPIENT

    /**
     *
     */
    public static final String[] LST_ORDERINGS_RECIPIENT =
    {
        "recipientName",
        "distributeName",
        "creationDate",
        "readDate",
    }; // LST_ORDERINGS_RECIPIENT

    /**
     *
     */
    public static final String[] LST_HEADINGS_LOCALECONTAINER =
    {
        BOTokens.ML_NAME,
        BOTokens.ML_LOCALE_LANGUAGE,
        BOTokens.ML_LOCALE_COUNTRY,
        BOTokens.ML_IS_DEFAULT
    }; // LST_HEADINGS_LOCALECONTAINER

    /**
     *
     */
    public static final String[] LST_HEADINGS_LOCALECONTAINERREDUCED =
    {
        BOTokens.ML_NAME,
        BOTokens.ML_LOCALE_LANGUAGE,
        BOTokens.ML_LOCALE_COUNTRY,
        BOTokens.ML_IS_DEFAULT
    }; // LST_HEADINGS_LOCALECONTAINERREDUCED

    /**
     *
     */
    public static final String[] LST_ORDERINGS_LOCALECONTAINER =
    {
        "name",
        "language",
        "country",
        "isDefault"
    }; // LST_ORDERINGS_LOCALECONTAINER

    /**
     *
     */
    public static final String[] LST_ORDERINGS_LOCALECONTAINERREDUCED =
    {
        "name",
        "language",
        "country",
        "isDefault"
    }; // LST_ORDERINGS_LOCALECONTAINER

   
    /**
     * 
     */
    public static final String[] LST_HEADINGS_STATECONTAINER =
    {
        BOTokens.ML_NAME,
        BOTokens.ML_WORKFLOWSTATE,
        BOTokens.ML_STATECHANGEDATE,
        BOTokens.ML_TYPE,
        BOTokens.ML_OWNER,
        BOTokens.ML_CHANGED,
    }; // LST_HEADINGS_STATECONTAINER
    
    /**
     * 
     */
    public static final String[] LST_HEADINGS_STATECONTAINER_REDUCED =
    {
        BOListConstants.LST_HEADINGS_STATECONTAINER[0],
        BOListConstants.LST_HEADINGS_STATECONTAINER[1],
        BOListConstants.LST_HEADINGS_STATECONTAINER[2],
    }; // LST_HEADINGS_STATECONTAINER_REDUCED
    
    /**
     * 
     */
    public static final String[] LST_ORDERINGS_STATECONTAINER =
    {
        "name",
        "workflowState",
        "stateChangeDate",
        "typeName",
        "owner",
        "lastChanged",
    }; // LST_ORDERINGS_STATECONTAINER
    
    /**
     * 
     */
    public static final String[] LST_ORDERINGS_STATECONTAINER_REDUCED =
    {
        BOListConstants.LST_ORDERINGS_STATECONTAINER[0],
        BOListConstants.LST_ORDERINGS_STATECONTAINER[1],
        BOListConstants.LST_ORDERINGS_STATECONTAINER[2],
    }; // LST_ORDERINGS_STATECONTAINER_REDUCED
   
    /**
     *
     */
    public static final String[] LST_HEADINGS_QUERYSELECTCONTAINER =
    {
        BOTokens.ML_NAME,
        BOTokens.ML_TYPENAME,
        BOTokens.ML_DESCRIPTION,
    }; // LST_HEADINGS_QUERYSELECTCONTAINER

    /**
     *
     */
    public static final String[] LST_ORDERINGS_QUERYSELECTCONTAINER =
    {
        "name",
        "typename",
        "description",
    }; // LST_ORDERINGS_QUERYSELECTCONTAINER

    /**
     * 
     */
    public static final String[] LST_HEADINGS_GROUP =
    {
        BOTokens.ML_NAME,
        BOTokens.ML_TYPENAME,
        BOTokens.ML_FULLNAME,
        BOTokens.ML_OWNER,
        BOTokens.ML_CHANGED,
    }; // LST_HEADINGS_GROUP
   
    /**
     * 
     */
    public static final String[] LST_HEADINGS_GROUP_REDUCED =
    {
        BOListConstants.LST_HEADINGS_GROUP[0],
        BOListConstants.LST_HEADINGS_GROUP[1],
    }; // LST_HEADINGS_GROUP_REDUCED
    
    /**
     * 
     */
    public static final String[] LST_ORDERINGS_GROUP =
    {
        BOListConstants.LST_ORDERINGS[0],
        BOListConstants.LST_ORDERINGS[1],
        BOConstants.ORD_FULLNAME,
        BOConstants.ORD_OWNERNAME,
        BOListConstants.LST_ORDERINGS[3],
    }; // LST_ORDERINGS_GROUP
    
    /**
     * 
     */
    public static final String[] LST_ORDERINGS_GROUP_REDUCED =
    {
        BOListConstants.LST_ORDERINGS[0],
        BOConstants.ORD_FULLNAME,
    }; // LST_ORDERINGS_GROUP_REDUCED

    /**
     * 
     */
    public static final String[] LST_HEADINGS_GROUPCONTAINER =
    {
        BOTokens.ML_NAME,
        BOTokens.ML_OWNER,
        BOTokens.ML_LASTCHANGED,
    }; // LST_HEADINGS_GROUPCONTAINER
   
    /**
     * 
     */
    public static final String[] LST_HEADINGS_GROUPCONTAINER_REDUCED =
    {
        BOListConstants.LST_HEADINGS_GROUPCONTAINER[0],
    }; // LST_HEADINGS_GROUPCONTAINER_REDUCED
    
    /**
     * 
     */
    public static final String[] LST_ORDERINGS_GROUPCONTAINER =
    {
        BOListConstants.LST_ORDERINGS[0],
        "owner",
        "lastChanged",
    }; // LST_ORDERINGS_GROUPCONTAINER
    
    /**
     * 
     */
    public static final String[] LST_ORDERINGS_GROUPCONTAINER_REDUCED =
    {
        BOListConstants.LST_ORDERINGS[0],
    }; // LST_ORDERINGS_GROUPCONTAINER_REDUCED

    /**
     * 
     */
    public static final String[] LST_HEADINGS_MEMBERSHIP =
    {
        BOTokens.ML_NAME,
        BOTokens.ML_OWNER,
        BOTokens.ML_CHANGED,
    }; // LST_HEADINGS_MEMBERSHIP
   
    /**
     * 
     */
    public static final String[] LST_HEADINGS_MEMBERSHIP_REDUCED =
    {
        BOListConstants.LST_HEADINGS_GROUPCONTAINER[0],
    }; // LST_HEADINGS_MEMBERSHIP_REDUCED
    
    /**
     * 
     */
    public static final String[] LST_ORDERINGS_MEMBERSHIP =
    {
        BOListConstants.LST_ORDERINGS[0],
        BOConstants.ORD_OWNERNAME,
        BOListConstants.LST_ORDERINGS[3],
    }; // LST_ORDERINGS_MEMBERSHIP
    
    /**
     * 
     */
    public static final String[] LST_ORDERINGS_MEMBERSHIP_REDUCED =
    {
        BOListConstants.LST_ORDERINGS[0],
    }; // LST_ORDERINGS_MEMBERSHIP_REDUCED

    /**
     * 
     */
    public static final String[] LST_HEADINGS_USERCONTAINER =
    {
        BOTokens.ML_NAME,
        BOTokens.ML_FULLNAME,
        BOTokens.ML_OWNER,
        BOTokens.ML_CHANGED,
    }; // LST_HEADINGS_USERCONTAINER
   
    /**
     * 
     */
    public static final String[] LST_HEADINGS_USERCONTAINER_REDUCED =
    {
        BOListConstants.LST_HEADINGS_USERCONTAINER[0],
        BOListConstants.LST_HEADINGS_USERCONTAINER[1],        
    }; // LST_HEADINGS_USERCONTAINER_REDUCED
    
    /**
     * 
     */
    public static final String[] LST_ORDERINGS_USERCONTAINER =
    {
        BOListConstants.LST_ORDERINGS[0],
        BOConstants.ORD_FULLNAME,
        BOConstants.ORD_OWNERNAME,
        BOListConstants.LST_ORDERINGS[3],    
    }; // LST_ORDERINGS_USERCONTAINER
    
    /**
     * 
     */
    public static final String[] LST_ORDERINGS_USERCONTAINER_REDUCED =
    {
        BOListConstants.LST_ORDERINGS[0],
        BOConstants.ORD_FULLNAME,
    }; // LST_ORDERINGS_USERCONTAINER_REDUCED

    
    /**
     * The names of the class in css for the rows. <BR/>
     */
    public static final String[] LST_CLASSROWS = {"listRow1", "listRow2"};

    /**
     * The names of the class in css for the rows. <BR/>
     */
    public static final String[] LST_CLASSSUBROWS = {"listSubRow1", "listSubRow2"};

    /**
     * The names of the class in css for the rows. <BR/>
     */
    public static final String[] LST_CLASSINFOROWS = {"infoRow1", "infoRow2"};

    /**
     * The names of the class in css for the rows of the reference table. <BR/>
     */
    public static final String[] LST_CLASSREFROWS = {"refRow1", "refRow2"};

} // class BOListConstants

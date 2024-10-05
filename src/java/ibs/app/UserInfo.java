/*
 * Class: UserInfo.java
 */

// package:
package ibs.app;

// imports:
import ibs.BaseObject;
import ibs.app.AppConstants;
import ibs.app.AppFunctions;
import ibs.app.HistoryInfo;
import ibs.bo.BOConstants;
import ibs.bo.BOListConstants;
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.bo.cache.CacheException;
import ibs.bo.cache.ObjectPool;
import ibs.io.servlet.SessionInitializationException;
import ibs.io.session.IUserInfo;
import ibs.obj.search.SearchQuery;
import ibs.obj.user.UserProfile_01;
import ibs.obj.wsp.Workspace_01;
import ibs.service.user.ExtendedUserData;
import ibs.service.user.User;
import ibs.tech.html.ButtonBarElement;
import ibs.tech.sql.SQLAction;
import ibs.util.trace.Tracer;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;


/******************************************************************************
 * Application object which is created with each call of a page. <BR/>
 * An object of this class represents the interface between the network and the
 * business logic itself. <BR/>
 * It gets arguments from the user, controls the program flow, and sends data
 * back to the user and his browser. <BR/>
 * There has to be generated an extension class of this class to realize the
 * functions which are specific to the required application.
 *
 * @version     $Id: UserInfo.java,v 1.21 2009/07/23 14:03:28 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 980507
 ******************************************************************************
 */
public class UserInfo extends BaseObject implements IUserInfo
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: UserInfo.java,v 1.21 2009/07/23 14:03:28 kreimueller Exp $";


    /**
     * Contains the ordering column. <BR/>
     */
    public int orderBy = BOListConstants.LST_DEFAULTORDERING;

    /**
     * Kind of ordering. <BR/>
     */
    public String orderHow = BOConstants.ORDER_ASC;

    /**
     * Path where to upload files. <BR/>
     */
//    public String uploadPath =ibs.app.AppConstants.PATH_UPLOAD;

    /**
     * Absolute path where to upload files - needed to create directories. <BR/>
     */
//    public String uploadPathAbs = "";// =ibs.app.AppConstants.PATH_UPLOAD_ABS;

    /**
     * Filename elements of upload mechanism. <BR/>
     * Holds names, paths
     */
    public Vector<FilenameElement> filenames;

    /**
     * OIDs of BusinessObjects for multiple operations. <BR/>
     */
    public Vector<OID> copiedOids = new Vector<OID> (10, 10);

    /**
     * BusinessObjects for multiple operations. <BR/>
     * Used for pasting virtual objects.<BR/>
     */
    public Hashtable<String, BusinessObject> copiedObjects =
        new Hashtable<String, BusinessObject> ();

    /**
     * OIDs of BusinessObjects for multiple distribute. <BR/>
     */
    public OID[] distributeElements;

    /**
     * OIDs of container where distribution is performed. <BR/>
     */
    public OID distributeContainerId;

    /**
    * Function which is to call if ok is pressed in distribution form. <BR/>
    */
    public int okDistributeFunction = AppFunctions.FCT_NOTIFICATION;

    /**
     * The the is createLink allowed for the BusinessObject. <BR/>
     */
    public boolean isCopyLinkAllowed = false;

    /**
     * The Type of the Operation. It defines if it is a Copy-Paste or Cut-Copy
     * operation. <BR/>
     */
    public short copiedType;

    /**
     * The Type of the targetContainer. We need it to controll
     * the insert in ReferenceConatiner . <BR/>
     */
    public int targetType;

    /**
     * The oid of the target container. <BR/>
     */
    public OID targetOid;
    /**
     * The oid of the target container. <BR/>
     */
    public OID containerIdOfLogContainer;


    /**
     * Holds a vector of filters for a search query
     */
    public SearchQuery searchQuery = new SearchQuery ();


    /**
     * Holds the user data relevant to the application. <BR/>
     */
    private User p_user;

    /**
     * Holds the extended user data relevant to the application. <BR/>
     */
    private ExtendedUserData p_extendedUserData;

    /**
     * Contains the last object the user worked with. <BR/>
     */
    public BusinessObject actObject;

    /**
     * Contains the last object the user worked with. <BR/>
     */
    public BusinessObject actRightsObject;
    /**
     * Contains the last several objects the user worked with. <BR/>
     */
    public ObjectPool cache;

    /**
     * The workspace of the user. <BR/>
     */
    public Workspace_01 workspace;

    /**
     * The profile of the user. <BR/>
     */
    public UserProfile_01 userProfile;

    /**
     * The buttonbar of the user. <BR/>
     */
    public ButtonBarElement buttonBar;

    /**
     * Filename elements of upload mechanism. <BR/>
     */
    public boolean isObjectMarked = false;

    /**
     * Filename elements of upload mechanism. <BR/>
     */
    public boolean displayDeleteForm = false;

    /**
     * The history of object the user has visited. <BR/>
     */
    public HistoryInfo history;


    /**
     * ProductOid of product touched at last, for saving in ehoppingCart. <BR/>
     */
    public OID productOid = null;

                        // PROTOKOLLIERUNG
    /**
     * This Flag mark if the Container is in Objectuse or in a general manner <BR/>
     */
    public boolean showPartOf = false;
    /**
     * The FilterstartLogDate<BR/>
     */
    public  Date startLogDate = new Date ();
    /**
     * The FilterendLogDate <BR/>
     */
    public  Date endLogDate = new Date ();
    /**
     * The LogTypeEntry is change, delete.. <BR/>
     */
    public  int logTypeEntry = 4;
    /**
     * Type of the marked (copied, cut) object.. <BR/>
     */
    public  int markedType = 0;

    /**
     * The id of the last domain where the user tried to log in. <BR/>
     */
    public int loginLastDomain = 0;
    /**
     * The name with which the user tried to log in the last time. <BR/>
     */
    public String loginLastUsername = null;
       /**
     * The stored db connection. <BR/>
     */
    public SQLAction action = null;
    /**
     * The step for the creation of a product. <BR/>
     */
    private Tracer p_trace = null;

    /**
     * The homepagePath of the Application. <BR/>
     */
    public String homepagePath = null;

    /**
     * If the current object's content is displayed. <BR/>
     */
    public boolean inContentView = false;


    /**************************************************************************
     * Creates an UserInfo object. <BR/>
     *
     * @throws  SessionInitializationException
     *          An exception occurred during initializing the user info.
     */
    public UserInfo ()
        throws SessionInitializationException
    {
        // call constructor of super class:

        // initialize the instance's private properties:

        // initialize the instance's public/protected properties:
        this.filenames = new Vector<FilenameElement> ();
        this.history = new HistoryInfo (null);

        try
        {
            // create a cache for the specified number of objects:
            this.cache = new ObjectPool (AppConstants.CACHE_SIZE, null);
        } // try
        catch (CacheException e)
        {
            throw new SessionInitializationException (e);
        } // catch
    } // UserInfo


    /**************************************************************************
     * Set the tracer for the user. <BR/>
     *
     * @param   tracer  The tracer.
     */
    public void setTracer (Tracer tracer)
    {
        // set the tracer:
        this.p_trace = tracer;
    } // setTracer


    /**************************************************************************
     * Get the actual tracer. <BR/>
     *
     * @return  The actual tracer or <CODE>null</CODE> if there is none.
     */
    public Tracer getTracer ()
    {
        // get the tracer property and return it:
        return this.p_trace;
    } // getTracer


    /**************************************************************************
     * Set the actual user. <BR/>
     *
     * @param   user    The user object.
     */
    public void setUser (User user)
    {
        this.p_user = user;
    } // setUser


    /**************************************************************************
     * Get the actual user. <BR/>
     *
     * @return  The user object.
     */
    public User getUser ()
    {
        return this.p_user;
    } // getUser


    /**************************************************************************
     * Set the extended user data for the actual user. <BR/>
     *
     * @param   extendedUserData    The extended user data object.
     */
    public void setExtendedUserData (ExtendedUserData extendedUserData)
    {
        this.p_extendedUserData = extendedUserData;
    } // setExtendedUserData


    /**************************************************************************
     * Get the extended user data for the actual user. <BR/>
     *
     * @return  The extended user data object.
     */
    public ExtendedUserData getExtendedUserData ()
    {
        return this.p_extendedUserData;
    } // getExtendedUserData

} // class UserInfo

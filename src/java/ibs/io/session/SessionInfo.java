/*
 * Class: SessionInfo.java
 */

// package:
package ibs.io.session;

// imports:
//TODO: unsauber
import ibs.app.AppFunctions;
//TODO: unsauber
import ibs.obj.layout.LayoutContainerElement_01;
//TODO: unsauber
import ibs.obj.layout.LayoutContainer_01;
import ibs.obj.menu.MenuData_01;
import ibs.obj.user.RightsContainerElement_01;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.servlet.SessionInitializationException;
import ibs.io.session.IUserInfo;
import ibs.util.trace.Tracer;
import ibs.util.trace.TracerHolder;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Vector;


/******************************************************************************
 * This is the Applicationinfo Object, which holds all
 * application-relevant information, especially Transactions and Values.
 *
 * @version     $Id: SessionInfo.java,v 1.33 2010/07/08 13:38:26 btatzmann Exp $
 *
 * @author      Christine Keim (CK), 980309
 ******************************************************************************
 */
public class SessionInfo extends Object implements TracerHolder
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SessionInfo.java,v 1.33 2010/07/08 13:38:26 btatzmann Exp $";


    /**
     *  A new session. <BR/>
     */
    public boolean isNew = true;

    /**
     * The pool which manages the connections to the repository database. <BR/>
     * Original type: {@link ibs.tech.sql.DBActionPool DBActionPool}
     */
    public Object p_connectionPool = null;

    /**
     * which menu is active?. <BR/>
     */
    public String p_actMenu;

    /**
     * Vector for all tabs on the left side of the screen, on which the
     * current user has rights
     */
    public Vector<MenuData_01> menus;

    /**
     * Discussion type 2
     * Original type: <CODE>m2.news.DiscElement</CODE>
     */
    public Object discussion2;

    /**
     * oid of last Object modified. <BR/>
     * Original type: {@link ibs.bo.OID OID}
     */
    public Object oidLast;

    /**
     * Caching the groupSelectionBox for the distribute-function. <BR/>
     * Original type: {@link ibs.tech.html.SelectElement SelectElement}
     */
    public Object groupBox;

    /**
     * Caching the groups for the groupSelectionBox for the rights assign-function. <BR/>
     */
    public Vector<String[]> groups;

    /**
     * Caching the users for the userSelectionBox for the distribute-function
     */
    public Vector<String[]> users;

    /**
     * Caching the receivers for the receiversSelectionBox
     * for the distribute-function
     */
    public Vector<String[]> receivers;

    /**
     * rights Hashtable for the rights assign-function
     */
    public Hashtable<String, RightsContainerElement_01> rights =
        new Hashtable<String, RightsContainerElement_01> ();

    /**
     * Property containing information regarding the actual user. <BR/>
     */
    public IUserInfo userInfo = null;

    /**
     * home. The webpath of the application without the servername. <BR/>
     */
    public String home = null;

    /**
     * Property containing information regarding the available Layouts. <BR/>
     */
    public LayoutContainer_01 layouts = null;

    /**
     * Property containing information regarding the actual layout. <BR/>
     */
    public LayoutContainerElement_01 activeLayout = null;

    /**
     * start registration wizard
     */
    public boolean wizardRegistration = false;

    /**
     * Indicates if the InfoView should be shown after creation of
     * Object.
     */
    public boolean p_showObjAfterNew = true;

    /**
     * The oid of the object where FCT newAndReference was started. <BR/>
     * Original type: {@link ibs.bo.OID OID}
     */
    public Object p_sourceObjectOid = null;

    /**
     * The query for search to be shown on Button BTN_NEWANDREFERENCE. <BR/>
     */
    public String newQuery = null;

    /**
     * The query for search to be shown on Button BTN_SEARCHANDREFERENCE. <BR/>
     */
    public String searchQuery = null;

    /**
     * The actual function. <BR/>
     * The function which is actually performed.
     * Default value:
     * <CODE>{@link ibs.app.AppFunctions#FCT_NOFUNCTION FCT_NOFUNCTION}</CODE>
     */
    public int p_actFct = AppFunctions.FCT_NOFUNCTION;

    /**
     * The function which shall be performed after commiting the changeform. <BR/>
     * Default value: <CODE>
     * {@link ibs.app.AppFunctions#FCT_OBJECTCHANGE FCT_OBJECTCHANGE}</CODE>
     */
    public int p_changeFormFct = AppFunctions.FCT_OBJECTCHANGE;

    /**
     * The function which shall be performed after the new wizard. <BR/>
     * Default value:
     * <CODE>{@link ibs.app.AppFunctions#FCT_NOFUNCTION FCT_NOFUNCTION}</CODE>
     */
    public int p_afterNewFct = AppFunctions.FCT_NOFUNCTION;

    /**
     * Weblink active? <BR/>
     */
    public boolean weblink = false;

    /**
     * Infos necessary for the weblink: Path of object to be shown. <BR/>
     */
    public  String p_opath = null;

    /**
     * Infos necessary for the weblink: oid of the object to be shown. <BR/>
     * Original type: {@link ibs.bo.OID OID}
     */
    public Object p_weblinkOid = null;

    /**
     * Loading of the frame necessary for weblink? <BR/>
     */
    public boolean p_loadFrame = false;

    /**
     * The server configuration for the actual session. <BR/>
     * Original type: {@link ibs.service.conf.ServerRecord ServerRecord}
     */
    public Object actServerConfiguration = null;

    /**
     * The querycreator of last enhanced search. <BR/>
     * Original type: {@link ibs.obj.query.QueryCreator_01 QueryCreator_01}
     */
    public Object queryObject = null;

    /**
     * Contains all necessary data to perfom a simple search. <BR/>
     * Original type: {@link ibs.obj.search.SimpleSearchData SimpleSearchData}
     */
    public Object simpleSearchData = null;

    /**
     * Was the user logged in correctly?
     */
    public boolean loggedIn = false;

    /**
     * hashtable for temporary variables; <BR/>
     * @deprecated  This property is never used (KR 20070709).
     */
//    public Hashtable hashTable = new Hashtable ();

    /**
     * Is NTLM authentication activated?
     */
    public boolean isNtlmActivated = false;

    /**
     * The name of an object from where a specific function has been called. <BR/>
     */
    public String p_callerName = null;

    /**
     * The oid of an object from where a specific function has been called. <BR/>
     */
    public Object p_callerOid = null;

    /**
     * The container oid of an object from where a specific function has
     * been called. <BR/>
     */
    public Object p_callerContainerId = null;

    /**
     * the function that should be exectuted after the change funcion. <BR/>
     */
    public int p_afterChangeFct = AppFunctions.FCT_NOFUNCTION;

    /**
     * the oid of the object which has been created within the objectNewExtended method. <BR/>
     */
    public Object p_modeObjectNewExtendedOID = null;
    
    /**
     * holds the info if a subsequent object new extended call is performed. <BR/>
     */
    public int p_modeSubsequentObjectNewExtended = IOConstants.BOOLPARAM_NOTEXISTS;
    
    /**
     * typecode info for object new extended calls. <BR/>
     */
    public String p_objectNewExtendedTypecode = null;

    /**
     * path info for object new extended calls. <BR/>
     */
    public String p_objectNewExtendedPath = null;

    /**
     * alt path info for object new extended calls. <BR/>
     */
    public String p_objectNewExtendedPathAlt = null;

    /**
     * the oid of the object for which the after change funcion has been set. <BR/>
     */
    public Object p_afterChangeFctOID = null;

    /**
     * If the actual domain requires SSL. <BR/>
     */
    public boolean sslRequiredDomain = true;

    /**
     * If the actual user requires SSL. <BR/>
     */
    public boolean sslRequiredUser = false;

    /**
     * Application info object. <BR/>
     */
    private ApplicationInfo p_appInfo = null;


    /**************************************************************************
     * Create a new instance representing the Information about the Application.
     * <BR/>
     * {@link #menus menus} is set to <CODE>null</CODE>.
     * {@link #oidLast oidLast} is set to <CODE>null</CODE>.
     * {@link #discussion2 discussion2} is set to <CODE>null</CODE>.
     * {@link #p_actMenu p_actMenu} is set to <CODE>null</CODE>.
     * {@link #layouts layouts} is set to <CODE>null</CODE>.
     * {@link #activeLayout activeLayout} is set to <CODE>null</CODE>.
     * {@link #p_actFct p_actFct} is set to
     *      {@link ibs.app.AppFunctions#FCT_NOFUNCTION AppFunctions.FCT_NOFUNCTION}.
     * {@link #actServerConfiguration actServerConfiguration} is initialized to
     * a new ServerRecord object.
     */
    public SessionInfo ()
    {
        this.menus = null;
        this.oidLast = null;
        this.discussion2 = null;
        this.p_actMenu = null;
        this.layouts = null;
        this.activeLayout = null;
        this.p_actFct = AppFunctions.FCT_NOFUNCTION;

        // specialized objects like simpleSearchData, etc. have to be
        // initialized outside of this class to avoid dependencies
    } // SessionInfo


    /**************************************************************************
     * Copy the values of a SessionInfo-object given as a parameter. <BR/>
     *
     * @param   s2      The SessionInfo object to copy.
     */
    public void copy (SessionInfo s2)
    {
        // gets all of the declared fields in this class
        Field[] fArr = this.getClass ().getDeclaredFields ();

        try
        {
            // loop through all fields of this class
            for (int i = 0; i < fArr.length; i++)
            {
                // copy the value of the actual attribut from s2
                // to the same attribute of the actual object
                fArr[i].set (this, fArr[i].get (s2));
            } // for
        } // try
        catch (Exception e)
        {
            // should not occur, display error message:
            IOHelpers.printError ("SessionInfo.copy",  this, e, true);
        } // catch
    } // copy


    /**************************************************************************
     * Set a new tracer. <BR/>
     *
     * @return  The tracer.
     */
    public Tracer getTracer ()
    {
        // check if the user info object exists:
        if (this.userInfo != null)      // user info exists?
        {
            // get the tracer from the user info object and return it:
            return this.userInfo.getTracer ();
        } // if user info exists

        // return default value:
        return null;
    } // getTracer


    /**************************************************************************
     * Get the name for the tracer. <BR/>
     *
     * @return  The tracer name.
     */
    public String getTracerName ()
    {
        // check if the user object exists:
        if (this.userInfo != null &&
            this.userInfo.getUser () != null) // user info exists?
        {
            // get the tracer from the user info object and return its name:
            return this.userInfo.getUser ().username;
        } // if user info exists

        // return default value:
        return null;
    } // getTracerName


    /**************************************************************************
     * Set a new tracer. <BR/>
     *
     * @param   tracer  The tracer to be set.
     */
    public void setTracer (Tracer tracer)
    {
        // check if the user info object exists:
        if (this.userInfo != null)      // user info exists?
        {
            // set the tracer within the user info object:
            this.userInfo.setTracer (tracer);
        } // if user info exists
    } // setTracer


    /**************************************************************************
     * Set the application info object. <BR/>
     *
     * @param   appInfo The application info object.
     */
    public void setApplicationInfo (ApplicationInfo appInfo)
    {
        this.p_appInfo = appInfo;
    } // setApplicationInfo


    /**************************************************************************
     * Get the application info object. <BR/>
     *
     * @return  The application info object, <CODE>null</CODE> if not object defined.
     */
    public ApplicationInfo getApplicationInfo ()
    {
        return this.p_appInfo;
    } // getApplicationInfo


    /**************************************************************************
     * Checks if the session is consistent. <BR/>
     * Especially it is checked if the current application info object is the
     * same as that one which is stored within the session. If not that means
     * that the session belongs to another application context. <BR/>
     * If the stored application info object is null it is set to the current
     * one.
     *
     * @param   appInfo The application info object.
     *
     * @throws  SessionInitializationException
     *          The session has the wrong application info object.
     */
    public void setApplicationContext (ApplicationInfo appInfo)
        throws SessionInitializationException
    {
        // check if the application info object is already set:
        if (this.p_appInfo != null)
        {
            // check if the correct application info is set:
            if (this.p_appInfo != appInfo)
            {
                throw new SessionInitializationException ("");
            } // if
        } // if
        else
        {
            // no application info set
            // set the current application info:
            this.setApplicationInfo (appInfo);
        } // else
    } // checkConsistency

} // class SessionInfo

/*
 * Class: BOHelpers.java
 */

// package:
package ibs.bo;

//imports:
import ibs.IbsGlobals;
import ibs.app.UserInfo;
import ibs.bo.cache.ObjectPool;
import ibs.bo.type.Type;
import ibs.bo.type.TypeConstants;
import ibs.bo.type.TypeContainer;
import ibs.bo.type.TypeNotFoundException;
import ibs.di.DataElement;
import ibs.di.KeyMapper;
import ibs.di.KeyMapper.ExternalKey;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.io.UploadException;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.menu.MenuData_01;
import ibs.service.action.ActionException;
import ibs.service.action.Variables;
import ibs.service.conf.Configuration;
import ibs.service.user.User;
import ibs.tech.sql.DBActionPool;
import ibs.tech.sql.DBConnector;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.SelectQuery;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.GeneralException;
import ibs.util.NoAccessException;
import ibs.util.UtilConstants;
import ibs.util.UtilExceptions;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;


/******************************************************************************
 * This class implements some useful methods for business objects. <BR/>
 *
 * @version     $Id: BOHelpers.java,v 1.32 2011/12/19 16:43:40 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 040424
 ******************************************************************************
 */
public abstract class BOHelpers extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BOHelpers.java,v 1.32 2011/12/19 16:43:40 rburgermann Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Returns an sql-action object associated with a connection. <BR/>
     *
     * @param   env     The actual environment.
     *
     * @return  The action object associated with the required connection.
     */
    public static SQLAction getDBConnection (Environment env)
    {
        // get the session info:
        SessionInfo sess = null;
        try
        {
            sess = env.getSessionInfo ();
        } // try
        catch (NullPointerException e)
        {
            // nothing to do
        } // catch e

        // call common method:
        return getDBConnection (env, sess, null);
    } // getDBConnection


    /**************************************************************************
     * Returns an sql-action object associated with a connection. <BR/>
     *
     * @param   env     The actual environment.
     * @param   sess    The actuel user session.
     * @param   caller  The caller object of this method.
     *
     * @return  The action object associated with the required connection.
     */
    public static SQLAction getDBConnection (Environment env,
                                             SessionInfo sess,
                                             Object caller)
    {
// //////////
// HACK!! MS
        // if no session is available get the connection from
        // the global action pool of the DBConnector class.
        if (sess == null)
        {
            return getGlobalDBConnection (env);
        } // if session not valid
// HACK!! MS
////////////

        return getLocalDBConnection (env, sess, caller);
    } // getDBConnection


    /**************************************************************************
     * Returns an sql-action object associated with a connection. <BR/>
     *
     * @param   env     The actual environment.
     *
     * @return  The action object associated with the required connection.
     */
    private static SQLAction getGlobalDBConnection (Environment env)
    {
// //////////
// HACK!! MS
        try
        {
            return DBConnector.getDBConnection ();
        } // try
        catch (DBError e)
        {
            IOHelpers.printError ("", e, true);
            IOHelpers.showMessage (e, env);
            return null;
        } // catch
// HACK!! MS
////////////
    } // getGlobalDBConnection


    /**************************************************************************
     * Returns an sql-action object associated with a connection. <BR/>
     *
     * @param   env     The actual environment.
     * @param   sess    The actuel user session.
     * @param   caller  The caller object of this method.
     *
     * @return  The action object associated with the required connection.
     */
    private static SQLAction getLocalDBConnection (Environment env,
                                                  SessionInfo sess,
                                                  Object caller)
    {
        if (sess.p_connectionPool == null) // no connection pool available?
        {
            try
            {
                // get the configuration object:
                Configuration conf = (Configuration)
                    env.getApplicationInfo ().configuration;

                // create a new connection pool and store it within the session:
                // version for Release 2.0 Beta or newer:
                sess.p_connectionPool = new DBActionPool (conf.getDbConf ());
            } // try
            catch (DBError e)
            {
                IOHelpers.printError ("", e, true);
                IOHelpers.showMessage (e, env);
            } // catch
        } // if

        // set the tracer holder of the connectionPool (needed for tracing)
        ((DBActionPool) sess.p_connectionPool).setTracerHolder (sess);

        try
        {
            // get a new action object associated with a connection from the pool
            // and return it:
            return ((DBActionPool) sess.p_connectionPool).getAction (caller);
        } // try
        catch (DBError e)
        {
            IOHelpers.printError ("getDBConnection", e, true);
            IOHelpers.showMessage (e, env);
            return null;
        } // catch
    } // getLocalDBConnection


    /**************************************************************************
     * Releases an action-object associated with a database connection. <BR/>
     *
     * @param   action  The action object associated with the connection.
     * @param   env     The actual environment.
     */
    public static void releaseDBConnection (SQLAction action,
                                            Environment env)
    {
        // get the session info:
        SessionInfo sess = null;
        try
        {
            sess = env.getSessionInfo ();
        } // try
        catch (NullPointerException e)
        {
            // nothing to do
        } // catch e

        // call common method:
        releaseDBConnection (action, env, sess);
    } // releaseDBConnection


    /**************************************************************************
     * Releases an action-object associated with a database connection. <BR/>
     *
     * @param   action  The action object associated with the connection.
     * @param   env     The actual environment.
     * @param   sess    The actuel user session.
     */
    public static void releaseDBConnection (SQLAction action,
                                            Environment env,
                                            SessionInfo sess)
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
        if (sess == null || sess.p_connectionPool == null)
        {
            releaseGlobalDBConnection (action, env);
            return;
        } // if session not valid
// HACK!! MS
////////////

        releaseLocalDBConnection (action, env, sess);
    } // releaseDBConnection


    /**************************************************************************
     * Releases an action-object associated with a database connection. <BR/>
     *
     * @param   action  The action object associated with the connection.
     * @param   env     The actual environment.
     */
    private static void releaseGlobalDBConnection (SQLAction action,
                                                  Environment env)
    {
////////////
// HACK!! MS
        if (action != null)
        {
            try
            {
                // release the action object:
                DBConnector.releaseDBConnection (action);
            } // try
            catch (DBError e)
            {
                IOHelpers.printError ("releaseDBConnection", e, true);
            } // catch
        } // if
// HACK!! MS
////////////
    } // releaseGlobalDBConnection


    /**************************************************************************
     * Releases an action-object associated with a database connection. <BR/>
     *
     * @param   action  The action object associated with the connection.
     * @param   env     The actual environment.
     * @param   sess    The actuel user session.
     */
    private static void releaseLocalDBConnection (SQLAction action,
                                                 Environment env,
                                                 SessionInfo sess)
    {
        if (action != null)
        {
            try
            {
                // release the action object:
                ((DBActionPool) sess.p_connectionPool).releaseAction (null, action);
            } // try
            catch (DBError e)
            {
                // an error occurred - show name and info
                IOHelpers.showMessage (e, env);
            } // catch
        } // if
    } // releaseLocalDBConnection


    /**************************************************************************
     * Get the type cache. <BR/>
     *
     * @return  The cache object.
     */
    public static TypeContainer getTypeCache ()
    {
        return ((ObjectPool) IbsGlobals.p_app.cache).getTypeContainer ();
    } // getTypeCache


    /**************************************************************************
     * Get an object out of the object cache. <BR/>
     * NOTE: This method catches all relevant exceptions and makes the
     * corresponding output.
     *
     * @param   oid             The oid the object to get.
     * @param   app             The global application info.
     * @param   sess            The actual session info object.
     * @param   user            The user who wants to get the object.
     * @param   env             The actual environment.
     * @param   addToHistory    Shall the object be added to the history?
     * @param   getParameters   Shall the environment parameters be read, too?
     * @param   isCheckRights   Shall the rights be checked?
     *
     * @return  The object if it was found,
     *          <CODE>null</CODE> otherwise.
     *
     * @deprecated  KR 20090824 Replaced by
     *              {@link #getObject(OID, Environment, boolean, boolean, boolean)}.
     */
    @Deprecated
    public static BusinessObject getObject (OID oid,
                                            ApplicationInfo app,
                                            SessionInfo sess,
                                            User user,
                                            Environment env,
                                            boolean addToHistory,
                                            boolean getParameters,
                                            boolean isCheckRights)
    {
        // call common method:
        return BOHelpers.getObject (oid, env, addToHistory, getParameters, isCheckRights);
    } // getObject


    /**************************************************************************
     * Get an object out of the object cache. <BR/>
     * When calling this method the parameters are read, too.
     *
     * @param   oid     The oid the object to get.
     * @param   env     The actual environment.
     * @param   addToHistory    Shall the object be added to the history?
     *
     * @return  The object if it was found,
     *          <CODE>null</CODE> otherwise.
     */
    public static BusinessObject getObject (OID oid,
                                            Environment env,
                                            boolean addToHistory)
    {
        // call common method:
        return BOHelpers.getObject (oid, env, addToHistory, true);
    } // getObject


    /**************************************************************************
     * Get an object out of the object cache. <BR/>
     * NOTE: This method catches all relevant exceptions and makes the
     * corresponding output.
     *
     * @param   oid             The oid the object to get.
     * @param   app             The global application info.
     * @param   sess            The actual session info object.
     * @param   user            The user who wants to get the object.
     * @param   env             The actual environment.
     * @param   addToHistory    Shall the object be added to the history?
     * @param   getParameters   Shall the environment parameters be read, too?
     *
     * @return  The object if it was found,
     *          <CODE>null</CODE> otherwise.
     *
     * @deprecated  KR 20090904 Use
     *              {@link #getObject(OID, Environment, boolean, boolean)}
     *              instead.
     */
    @Deprecated
    public static BusinessObject getObject (OID oid,
                                            ApplicationInfo app,
                                            SessionInfo sess,
                                            User user,
                                            Environment env,
                                            boolean addToHistory,
                                            boolean getParameters)
    {
        // call common method and return the result:
        return BOHelpers.getObject (oid, env, addToHistory, getParameters);
    } // getObject


    /**************************************************************************
     * Get an object out of the object cache. <BR/>
     * NOTE: This method catches all relevant exceptions and makes the
     * corresponding output.
     *
     * @param   oid             The oid the object to get.
     * @param   env             The actual environment.
     * @param   addToHistory    Shall the object be added to the history?
     * @param   getParameters   Shall the environment parameters be read, too?
     *
     * @return  The object if it was found,
     *          <CODE>null</CODE> otherwise.
     */
    public static BusinessObject getObject (OID oid,
                                            Environment env,
                                            boolean addToHistory,
                                            boolean getParameters)
    {
        // call common method:
        return BOHelpers.getObject (oid, env, addToHistory,
            getParameters, true);
    } // getObject


    /**************************************************************************
     * Get an object out of the object cache. <BR/>
     * NOTE: This method catches all relevant exceptions and makes the
     * corresponding output.
     *
     * @param   oid             The oid the object to get.
     * @param   env             The actual environment.
     * @param   addToHistory    Shall the object be added to the history?
     * @param   getParameters   Shall the environment parameters be read, too?
     * @param   isCheckRights   Shall the rights be checked?
     *
     * @return  The object if it was found,
                <CODE>null</CODE> otherwise.
     */
    public static BusinessObject getObject (OID oid,
                                            Environment env,
                                            boolean addToHistory,
                                            boolean getParameters,
                                            boolean isCheckRights)
    {
//trace ("--- START getObject" +
//           " - oid: " + oid +
//           " - user: " + user.actUsername);
        BusinessObject obj = null;      // the business object

        // check if the oid is existing and has a type defined:
        // we cannot create a new object instance if there is no type defined
        if (oid != null && oid.tVersionId != 0) // valid oid with existing type?
        {
            try
            {
// trace ("KR searching for object " + oid + "...");
// showMessage ("KR searching for object " + oid + "...");
//trace ("KR before fetch: " + oid + ", " + user + ", " + sess + ", " + env + ".");
                if (isCheckRights)
                {
                    obj = ((ObjectPool) env.getApplicationInfo ().cache)
                        .fetchObject (oid, env.getUserInfo ().getUser (),
                            env.getSessionInfo (), env, getParameters);
                } // if
                else                    // no rights check
                {
                    obj = ((ObjectPool) env.getApplicationInfo ().cache)
                        .fetchObject (oid, null,
                            env.getSessionInfo (), env, getParameters);
                    obj.user = env.getUserInfo ().getUser ();
                } // else no rights check
// trace ("KR object found: " + obj.name);
// showMessage ("KR object found: " + obj.name);

                if (addToHistory)
                {
                    // set the object's data in the history:
                    ((UserInfo) env.getUserInfo ()).history.setObjectData (
                        obj.oid,
                        obj.name,
                        obj.icon);
                } // if
            } // try
            catch (ObjectNotFoundException e)
            {
// trace ("KR Object not found: " + obj + ".");
// showMessage ("Object not found: " + obj + ".");
                // check if a real object should be found or a temporary object:
                if (!oid.isTemp ())     // real object?
                {
                    // show corresponding error message:
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (
                            BOMessages.MSG_BUNDLE, BOMessages.ML_MSG_OBJECTNOTFOUND, env), env);
                } // if real object
                else                    // temporary object?
                {
                    // no problem - work as if the object where found:
// trace ("KR no problem");
// showMessage ("KR no problem");
                } // else temporary object

                return null;
            } // catch
            catch (TypeNotFoundException e)
            {
//showDebug ("Object Type not found for oid " + oid + ".");
//trace ("KR Object Type not found for oid " + oid + ".");
//showMessage ("KR Object Type not found for oid " + oid + ".");
                // show corresponding error message:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (
                        BOMessages.MSG_BUNDLE, BOMessages.ML_MSG_TYPENOTFOUND,
                        new String[] {"" + oid.tVersionId}, env), 
                    e,env, true);
                return null;
            } // catch
            catch (ObjectClassNotFoundException e)
            {
//showDebug ("Object class not found for oid " + oid + ".");
//trace ("KR Object class not found for oid " + oid + ".");
//showMessage ("KR Object class not found for oid " + oid + ".");
                // show corresponding error message:
                // show corresponding error message:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (
                        BOMessages.MSG_BUNDLE, BOMessages.ML_MSG_CLASSNOTFOUND,
                        new String[] {BOHelpers.getTypeCache ()
                            .getType (oid.tVersionId).getCode ()}, env),
                    e, env, true);
                return null;
            } // catch
            catch (ObjectInitializeException e)
            {
//showDebug ("object could not be initialized for oid " + oid + ".");
//trace ("KR Object could not be initialized for oid " + oid + ".");
                // show corresponding error message:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (
                        BOMessages.MSG_BUNDLE, BOMessages.ML_MSG_INITIALIZATIONFAILED,
                        new String[] {BOHelpers.getTypeCache ()
                            .getType (oid.tVersionId).getCode ()}, env),
                    e, env, true);
                return null;
            } // catch
            catch (Throwable e)
            {
//showDebug ("Exception within fetchObject: " + e);
                IOHelpers.showMessage (e, env, true);
                return null;
            } // catch
        } // if valid oid
        else                            // no valid oid
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (
                    BOMessages.MSG_BUNDLE, BOMessages.ML_MSG_NOOID, env), env);
        } // else no valid oid

        return obj;                     // return the created object
    } // getObject


    /**************************************************************************
     * Get a new BusinessObject of a certain object type. <BR/>
     *
     * @param   type    Type of the object to instantiate.
     * @param   env     The actual environment.
     *
     * @return  The new BusinessObject instance or
     *          <CODE>null</CODE> if something went wrong.
     */
    public static BusinessObject getNewObject (int type,
                                               Environment env)
    {
        String typeCode = BOHelpers.getTypeCache ().getType (
            Type.createTVersionId (type)).getCode ();

        // create new BusinessObject from the type and return it:
        return BOHelpers.getNewObject (typeCode, env);
    } // getNewObject


    /**************************************************************************
     * Get a new BusinessObject of a certain object type. <BR/>
     * The object type is specified through the unique tpye code.
     *
     * @param   typeCode        The unique type code.
     * @param   app             The global application info.
     * @param   sess            The actual session info object.
     * @param   user            The user who wants to get the object.
     * @param   env             The actual environment.
     *
     * @return  The new BusinessObject instance or
     *          <CODE>null</CODE> if something went wrong.
     *
     * @deprecated  KR 20090904 Use
     *              {@link #getNewObject(String, Environment)} instead.
     */
    @Deprecated
    public static BusinessObject getNewObject (String typeCode,
                                               ApplicationInfo app,
                                               SessionInfo sess,
                                               User user,
                                               Environment env)
    {
        // call common method and return the result:
        return BOHelpers.getNewObject (typeCode, env);
    } // getNewObject


    /**************************************************************************
     * Get a new BusinessObject of a certain object type. <BR/>
     * The object type is specified through the unique tpye code.
     *
     * @param   typeCode    The unique type code.
     * @param   env         The actual environment.
     *
     * @return  The new BusinessObject instance or
     *          <CODE>null</CODE> if something went wrong.
     */
    public static BusinessObject getNewObject (String typeCode,
                                               Environment env)
    {
        // call common method:
        return BOHelpers.getNewObject (typeCode, env, true);
    } // getNewObject


    /**************************************************************************
     * Get a new BusinessObject of a certain object type. <BR/>
     * The object type is specified through the unique tpye code.
     *
     * @param   typeCode        The unique type code.
     * @param   env             The actual environment.
     * @param   getParameters   Shall the
     *                          {@link ibs.bo.BusinessObject#getParameters
     *                          getParameters} method be called for the object
     *                          after its instantiation?
     *
     * @return  The new BusinessObject instance or
     *          <CODE>null</CODE> if something went wrong.
     */
    public static BusinessObject getNewObject (String typeCode,
                                               Environment env,
                                               boolean getParameters)
    {
        BusinessObject obj = null;      // the business object

        if (typeCode != null)           // valid type code?
        {
            try
            {
//trace ("KR before fetchNew: " + typeCode + ", " + user () + ", " + sess + ", " + env + ".");
                obj = ((ObjectPool) IbsGlobals.p_app.cache).fetchNewObject (
                    typeCode, env.getUserInfo ().getUser (),
                    env.getSessionInfo (), env, getParameters);
//trace ("KR after fetchNew.");
                // set the object's data in the history:
                ((UserInfo) env.getUserInfo ()).history.setObjectData (obj.oid,
                    obj.name, obj.icon);
            } // try
            catch (TypeNotFoundException e)
            {
//trace ("KR Object Type not found for typeCode " + typeCode + ".");
                // show corresponding error message:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (
                        BOMessages.MSG_BUNDLE, BOMessages.ML_MSG_TYPENOTFOUND, env), env);
            } // catch
            catch (ObjectClassNotFoundException e)
            {
//trace ("KR Object class not found for typeCode " + typeCode + ".");
                // show corresponding error message:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (
                        BOMessages.MSG_BUNDLE, BOMessages.ML_MSG_CLASSNOTFOUND, env), env);
            } // catch
            catch (ObjectInitializeException e)
            {
//trace ("KR Object could not be initialized for typeCode " + typeCode + ".");
                // show corresponding error message:
                IOHelpers.showMessage (e, env, false);
            } // catch
            catch (Exception e)
            {
                IOHelpers.showMessage ("Exception within BOHelpers.getNewObject",
                                       e, env, true);
            } // catch
//trace ("KR hitRate: " + getCache ().getHitRate ());
        } // if valid type
        else                            // no valid type
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (
                    BOMessages.MSG_BUNDLE, BOMessages.ML_MSG_NOOID, env), env);
        } // else no valid oid

        return obj;                     // return the found object
    } // getNewObject


    /**************************************************************************
     * Add the reference to another object to this object. <BR/>
     *
     * @param   referencedOid   The oid of the object to create a reference on.
     * @param   containerOid    The oid of the container in which to create
     *                          the object.
     * @param   app             The global application info.
     * @param   sess            The actual session info object.
     * @param   user            The user who wants to get the object.
     * @param   env             The actual environment.
     *
     * @return  The newly created object. <BR/>
     *          <CODE>null</CODE> if the object could not be created.
     */
    public static BusinessObject createReferenceObject (OID referencedOid,
                                                        OID containerOid,
                                                        ApplicationInfo app,
                                                        SessionInfo sess,
                                                        User user,
                                                        Environment env)
    {
        BusinessObject obj = null;      // the reference object

        // check if the referenced object exists:
        if (((ObjectPool) app.cache).exists (referencedOid, user, env, sess))
                                        // referenced object exists?
        {
            if ((obj = BOHelpers.getNewObject (TypeConstants.TC_Reference, env)) != null)
                                        // object ready?
            {
                // set containerId as the oid of the actual object:
                obj.containerId = containerOid;
                obj.isLink = true;
                obj.linkedObjectId = referencedOid;
                // set default name:
                obj.name = ((ObjectPool) app.cache).getTypeContainer ()
                    .getTypeName (TypeConstants.TC_Reference);
                // create the new object:
                obj.createActive (UtilConstants.REP_STANDARD);
            } // if object ready
            else                        // object not created
            {
                // show corresponding message:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (
                        BOMessages.MSG_BUNDLE, BOMessages.ML_MSG_OBJECTNOTCREATED, env),
                        app, sess, env);
            } // else object not created
        } // if referenced object exists
        else                            // no referenced business object available
        {
            // show corresponding message:
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (
                    BOMessages.MSG_BUNDLE, BOMessages.ML_MSG_OBJECTNOTCREATED, env),
                    app, sess, env);
        } // else no referenced business object available

        // return the result:
        return obj;
    } // createReferenceObject


    /**************************************************************************
     * Returns the object oids from a given path. <BR/>
     * The path is allowed to contain place holders so that several objects
     * can be found through the path. <BR/>
     * E.g. {@link BOTokens#TOK_PRIV_SECTION BOTokens#TOK_PRIV_SECTION}/
     * {@link BOConstants#OBJECTPATH_ALLUSERS BOConstants.OBJECTPATH_ALLUSERS}
     * means to use the path to all users' workspaces.
     *
     * @param   contPath    A string containing the path.
     * @param   obj         The object for which to resolve the path.
     * @param   app         The global application info.
     * @param   sess        The actual session info object.
     * @param   env         The actual environment.
     *
     * @return  The oids of the objects.
     *
     * @deprecated  KR 20090827 Use
     *              {@link #resolveMultipleObjectPath(String, BusinessObject, Environment)}
     *              instead.
     */
    @Deprecated
    public static final Vector<OID> resolveMultipleObjectPath (
                                                               String contPath,
                                                               BusinessObject obj,
                                                               ApplicationInfo app,
                                                               SessionInfo sess,
                                                               Environment env)
    {
        Vector<Boolean> isContainer = new Vector<Boolean> ();

        return BOHelpers.resolveMultipleObjectPath (contPath, isContainer, obj,
            env);
    } // resolveMultipleObjectPath


    /**************************************************************************
     * Returns the object oids from a given path. <BR/>
     * The path is allowed to contain place holders so that several objects
     * can be found through the path. <BR/>
     * E.g. {@link BOTokens#TOK_PRIV_SECTION BOTokens#TOK_PRIV_SECTION}/
     * {@link BOConstants#OBJECTPATH_ALLUSERS BOConstants.OBJECTPATH_ALLUSERS}
     * means to use the path to all users' workspaces.
     *
     * @param   contPath    A string containing the path.
     * @param   obj         The object for which to resolve the path.
     * @param   env         The actual environment.
     *
     * @return  The oids of the objects.
     */
    public static final Vector<OID> resolveMultipleObjectPath (
                                                               String contPath,
                                                               BusinessObject obj,
                                                               Environment env)
    {
        Vector<Boolean> isContainer = new Vector<Boolean> ();

        return BOHelpers.resolveMultipleObjectPath (contPath, isContainer, obj,
            env);
    } // resolveMultipleObjectPath


    /**************************************************************************
     * Returns the object oids from a given path. <BR/>
     * The path is allowed to contain place holders so that several objects
     * can be found through the path. <BR/>
     * E.g. {@link BOTokens#TOK_PRIV_SECTION BOTokens#TOK_PRIV_SECTION}/
     * {@link BOConstants#OBJECTPATH_ALLUSERS BOConstants.OBJECTPATH_ALLUSERS}
     * means to use the path to all users' workspaces. <BR/>
     * Attention: The path separator is the
     * {@link BOConstants#OBJECTPATH_SEPARATOR BOConstants.OBJECTPATH_SEPARATOR}.
     * A path that references a object in the private area can contain
     * the joker character {@link BOConstants#OBJECTPATH_USERJOKER
     * BOConstants.OBJECTPATH_USERJOKER} instead of the user name.
     * This joker is automaticaly replaced with the name of the current user.
     *
     * @param   contPath        A string containing the absolute path.
     * @param   isContainer     A vector containing a boolean value for each
     *                          oid in the result vecotr. This value is set to
     *                          <CODE>true</CODE> if the corresponding object
     *                          is a container.
     * @param   obj             The object for which to resolve the path.
     * @param   env             The actual environment.
     *
     * @return  The oids of the objects. Empty if no objects were found.
     */
    public static final Vector<OID> resolveMultipleObjectPath (String contPath,
                                                               Vector<Boolean> isContainer,
                                                               BusinessObject obj,
                                                               Environment env)
    {
        String contPathLocal = contPath; // variable for local assignments
        String menuTabName = null;  // contains the name of the object to seek for
        OID objectOid = null;       // oid of actual object
        Vector<OID> objectOids = new Vector<OID> (); // the result vector
        StringTokenizer pathTokenizer;  // tokenizer used to parse the path
        boolean[] isCont = {false}; // is the actual object a container?
        String userName = null;     // name of actual user
        Vector<OID> workspaceOids = null; // oids off all user workspaces
        int elementsToSkip = 0;     // number of elements to skip for tokenizer
        User user = env.getUserInfo ().getUser (); // the user object

        // replace any system variables
        contPathLocal = BOHelpers.replaceSysVar (obj, contPathLocal);

        // check if a container path has been specified
        if (contPathLocal == null || contPathLocal.length () == 0)
        {
            return null;
        } // if

        // remove starting and ending path separators
        contPathLocal = BOHelpers.trimPath (contPathLocal);
        // create a string tokenizer with path
        pathTokenizer = new StringTokenizer (contPathLocal, BOConstants.OBJECTPATH_SEPARATOR);

        // get the first token:
        if (pathTokenizer.hasMoreElements ())
        {
            // get the container name of the path
            menuTabName = pathTokenizer.nextToken ();
            elementsToSkip++;

            // there are 3 possible ways of setting the parameters
            // of the stored procedure:
            // 1. a container in the private area of a user
            // 2. a "normal" container. get the root of the path
            // 3. after executing part 1 or 2, always execute this one
            if (menuTabName.equalsIgnoreCase (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_PRIV_SECTION, env)))
                                // object is in private area?
            {
                // as a user must be specified for a particular private area
                // the second separator must be found (PRIVAT/USERNAME/...)
                userName = pathTokenizer.nextToken ();
                elementsToSkip++;

                // resolve the user joker character
                if (userName.equals (BOConstants.OBJECTPATH_USERJOKER))
                {
                    userName = user.username;
                } // if the user name is the joker character
                else if (userName.equals (BOConstants.OBJECTPATH_ALLUSERS))
                {
                    // get all user names:
                    workspaceOids = BOHelpers.performGetAllWorkspaces (
                        user.domain, env);
                } // if the user name is the joker character

                // check if we have multiple user names:
                if (workspaceOids == null) // just one workspace?
                {
                    // resolve path retrieves the object id:
                    objectOid = BOHelpers.resolveObjectPathData (obj,
                        menuTabName, userName, user.domain, OID.EMPTYOID,
                        isCont, env);

                    // handle the results of the procedure call:
                    if (objectOid != null)  // all OK
                    {
                        // resolve the rest of the object path:
                        objectOid = BOHelpers.resolveObjectPath (
                            pathTokenizer, objectOid, isCont, obj, env);
                        // remember if the object is a container:
                        isContainer.add (new Boolean (isCont[0]));
                    } // if all OK
                    else            // nothing returned
                    {
                        return objectOids;
                    } // else nothing returned
                } // if just one workspace
                else                    // multiple workspaces
                {
                    boolean isFirstWsp = true;

                    // loop through all workspaces and get the object data
                    // for each of them:
                    for (Iterator<OID> iter = workspaceOids.iterator ();
                         iter.hasNext ();)
                    {
                        objectOid = iter.next ();

                        // work on the workspae:
                        if (objectOid != null)  // all OK
                        {
                            // check if this is the first workspace:
                            // the first workspace can use the already
                            // existing tokenizer
                            if (!isFirstWsp)
                            {
                                // recreate the tokenizer:
                                pathTokenizer = new StringTokenizer (contPathLocal,
                                    BOConstants.OBJECTPATH_SEPARATOR);

                                // skip the first elements:
                                for (int i = elementsToSkip;
                                     i > 0 && pathTokenizer.hasMoreElements ();
                                     i--)
                                {
                                    pathTokenizer.nextToken ();
                                } // for i
                            } // if

                            // compute the resulting object and add it to
                            // the result vector:
                            objectOids.add (BOHelpers.resolveObjectPath (
                                pathTokenizer, objectOid, isCont, obj, env));
                            // remember if the object is a container:
                            isContainer.add (new Boolean (isCont[0]));
                            // the next one will not be the first user:
                            isFirstWsp = false;
                        } // if
                    } // for iter
                } // else multiple workspaces
            } // if
            else                // container not in private area but
                                // still getting the root
            {
                // when called first time it must be a menu tab:
                objectOid = BOHelpers.getMenuTabOid (
                    env.getSessionInfo (), menuTabName);

                // handle the results of the procedure call:
                if (objectOid != null)  // all OK
                {
                    // resolve the rest of the object path:
                    objectOid = BOHelpers.resolveObjectPath (pathTokenizer,
                        objectOid, isCont, obj, env);
                    // remember if the object is a container:
                    isContainer.add (new Boolean (isCont[0]));
                } // if all OK
                else                // nothing returned
                {
                    return objectOids;
                } // else nothing returned
            } // else container not in private area but still getting...
        } // if

        // return the oid
        return objectOids;
    } // resolveMultipleObjectPath


    /**************************************************************************
     * Returns the container OID from a given path. <BR/>
     *
     * @param   contPath    A string containing the path.
     * @param   obj         The object for which to resolve the path.
     * @param   env         The actual environment.
     *
     * @return  The oid of the container to import to.
     */
    public static final OID resolveObjectPath (String contPath,
                                               BusinessObject obj,
                                               Environment env)
    {
        boolean[] isContainer = {false};

        return BOHelpers.resolveObjectPath (contPath, isContainer, obj, env);
    } // resolveObjectPath


    /**************************************************************************
     * Returns the container OID from a given path. <BR/>
     *
     * @param   contPath    A string containing the path.
     * @param   obj         The object for which to resolve the path.
     * @param   app         The global application info.
     * @param   sess        The actual session info object.
     * @param   env         The actual environment.
     *
     * @return  The oid of the container to import to.
     *
     * @deprecated  KR 20090827 Use
     *              {@link #resolveObjectPath(String, BusinessObject, Environment)}
     *              instead.
     */
    @Deprecated
    public static final OID resolveObjectPath (String contPath,
                                               BusinessObject obj,
                                               ApplicationInfo app,
                                               SessionInfo sess, Environment env)
    {
        boolean[] isContainer = {false};

        return BOHelpers.resolveObjectPath (contPath, isContainer, obj, app,
            sess, env);
    } // resolveObjectPath



    /**************************************************************************
     * Returns the container OID from a given path and OID. <BR/>
     *
     * @param   contPath    A string containing the path.
     * @param   actualContainerId   Object containg the OID.
     * @param   obj         The object for which to resolve the path.
     * @param   app         The global application info.
     * @param   sess        The actual session info object.
     * @param   env         The actual environment.
     *
     * @return  the oid of the container to import to
     *
     * @deprecated  KR 20090827 Use
     *              {@link #resolveObjectPath(String, OID, BusinessObject, Environment)}
     *              instead.
     */
    @Deprecated
    public static final OID resolveObjectPath (String contPath,
                                               OID actualContainerId,
                                               BusinessObject obj,
                                               ApplicationInfo app,
                                               SessionInfo sess, Environment env)
    {
        // call common method and return the result:
        return BOHelpers.resolveObjectPath (
            contPath, actualContainerId, obj, env);
    } // resolveObjectPath


    /**************************************************************************
     * Returns the container OID from a given path and OID. <BR/>
     *
     * @param   contPath    A string containing the path.
     * @param   actualContainerId   Object containg the OID.
     * @param   obj         The object for which to resolve the path.
     * @param   env         The actual environment.
     *
     * @return  the oid of the container to import to
     */
    public static final OID resolveObjectPath (String contPath,
                                               OID actualContainerId,
                                               BusinessObject obj,
                                               Environment env)
    {
        String contPathLocal = contPath; // variable for local assignments
        SQLAction action = null;        // the action object used to access the
                                        // database
        String containerName = "";  // contains the name of the container to seek for
        OID containerOid = actualContainerId;
        //int sepIndex;                 // index of the separator in contPath
        boolean hasroot = true;         // flag for marking the occurence of the root string "./"
        boolean hasback = true;         // flag for marking the occurence of the back string "../"
        StringTokenizer pathToken;      // tokenizer used to parse the path
        boolean[] isContainer = {false};

        // container id null call the function
        if (actualContainerId == null)
        {
            return BOHelpers.resolveObjectPath (contPathLocal, obj, env);
        } // if

        // check if a container path has been specified
        if (contPathLocal == null || contPathLocal.length () == 0)
        {
            return null;
        } // if
        // resolve the path
        try
        {
            // remove starting and ending path separators
            contPathLocal = BOHelpers.trimPath (contPathLocal);
            // now check if the path is a relative path
            if (!(contPathLocal.startsWith (BOConstants.PATH_BACK) ||
                  contPathLocal.startsWith (BOConstants.PATH_ROOT)))
            {
                // resolve a non relative path
                return BOHelpers.resolveObjectPath (contPathLocal, obj, env);
            } // else if (! (contPath.startsWith (BOConstants.PATH_BACK) ||

            // this is a relative path
            // replace any system variables
            // note that the resolveObjectPath (String contPath)
            // is also calling the replaceSysVar method
            contPathLocal = BOHelpers.replaceSysVar (obj, contPathLocal);

            // create a string tokenizer with path
            pathToken = new StringTokenizer (contPathLocal, BOConstants.PATH_SEPARATOR);
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = BOHelpers.getDBConnection (env);
            // loop through all parts of the path
            do
            {
                // get the container name which should be searched
                containerName = pathToken.nextToken ();
                // check if container name equals string ../
                if (containerName.equals (BOConstants.PATH_BACK))
                {
                    // check for validity of ../ in path
                    if (hasback)
                    {
                        containerOid = obj.performRetrieveUpperData (containerOid);
                        hasroot = false;
                    } // if (hasback)
                    // throw exception if path is wrong
                    else
                    {
                        throw new GeneralException (MultilingualTextProvider.getMessage (
                                BOMessages.MSG_BUNDLE, BOMessages.ML_MSG_INVALIDPATH, env));
                    } // else
                } // if (containerName.equals (BOConstants.PATH_BACK))
                // check if container name equals string ./
                else if (containerName.equals (BOConstants.PATH_ROOT))
                {
                    // check for validity of ./ in path
                    if (hasroot)
                    {
                        containerOid = actualContainerId;
                        hasroot = false;
                    } // if (hasroot)
                    // throw exception if path is wrong
                    else
                    {
                        throw new GeneralException (MultilingualTextProvider.getMessage (
                                BOMessages.MSG_BUNDLE, BOMessages.ML_MSG_INVALIDPATH, env));
                    } // else
                } // else check if container name equals string ./
                else  // get the container id if path specifies a container name
                {
                    // resolvePath retrieves the container id
                    containerOid = BOHelpers.resolveObjectPathData (obj,
                        containerName, "", -1, containerOid.toString (),
                        isContainer, env);
                    hasroot = false;    // flag marking no more ./ are allowed
                                        // in the path
                    hasback = false;    // flag marking no more ../ are allowed in the path
                } // else get the container id if path specifies a container name
                // in case the container oid is null the path could not be resolved
                if (containerOid == null)
                {
                    return null;
                } // if
            } while (pathToken.hasMoreElements ()); // do loop through all parts of the path
        } // try
        catch (GeneralException e)
        {
            IOHelpers.printError ("resolveObjectPath", obj, e, true);
//          showMessage (BOMessages.MSG_INVALIDPATH);
            containerOid = null;
        } //catch
        catch (Exception e)
        {
            IOHelpers.printError ("resolveObjectPath", obj, e, true);
//            showMessage (BOMessages.MSG_INVALIDPATH);
            containerOid = null;
        } //catch
        finally
        {
            // close db connection in every case -  only workaround -
            // db connection must be handled somewhere else:
            BOHelpers.releaseDBConnection (action, env);
        } // finally
        // return the oid
        return containerOid;
    } // resolveObjectPath


    /**************************************************************************
     * Returns the object OID from a given object path. <BR/>
     * Attention: The path separator is the BOConstants.OBJECTPATH_SEPARATOR.
     * A path that reference a object in the private area can contain
     * the joker character BOConstants.OBJECTPATH_USERJOKER instead of the user name.
     * This joker is automaticaly replaced with the name of the current user.
     *
     * @param   contPath        A string containing the absolute path.
     * @param   isContainer     Set to <CODE>true</CODE> if the object is a
     *                          container.
     * @param   obj             The object for which to resolve the path.
     * @param   app             The global application info.
     * @param   sess            The actual session info object.
     * @param   env             The actual environment.
     *
     * @return  The oid of the object.
     *
     * @deprecated  KR 20090827 Use
     *              {@link #resolveObjectPath(String, boolean[], BusinessObject, Environment)}
     *              instead.
     */
    @Deprecated
    public static final OID resolveObjectPath (String contPath,
                                               boolean[] isContainer,
                                               BusinessObject obj,
                                               ApplicationInfo app,
                                               SessionInfo sess, Environment env)
    {
        // call common method and return the result:
        return BOHelpers.resolveObjectPath (contPath, isContainer, obj, env);
    } // resolveObjectPath


    /**************************************************************************
     * Returns the object OID from a given object path. <BR/>
     * Attention: The path separator is the BOConstants.OBJECTPATH_SEPARATOR.
     * A path that reference a object in the private area can contain
     * the joker character BOConstants.OBJECTPATH_USERJOKER instead of the user name.
     * This joker is automaticaly replaced with the name of the current user.
     *
     * @param   contPath        A string containing the absolute path.
     * @param   isContainer     Set to <CODE>true</CODE> if the object is a
     *                          container.
     * @param   obj             The object for which to resolve the path.
     * @param   env             The actual environment.
     *
     * @return  The oid of the object.
     */
    public static final OID resolveObjectPath (String contPath,
                                               boolean[] isContainer,
                                               BusinessObject obj,
                                               Environment env)
    {
        String contPathLocal = contPath; // variable for local assignments
        String menuTabName = null;  // contains the name of the object to seek for
        OID objectOid = null;
        StringTokenizer pathTokenizer; // tokenizer used to parse the path
        User user = env.getUserInfo ().getUser (); // the user object

        isContainer[0] = false;

        if (obj != null)
        {
            // replace any system variables
            contPathLocal = BOHelpers.replaceSysVar (obj, contPathLocal);
        } // if

        // check if a container path has been specified
        if (contPathLocal == null || contPathLocal.length () == 0)
        {
            return null;
        } // if

        // remove starting and ending path separators
        contPathLocal = BOHelpers.trimPath (contPathLocal);
        // create a string tokenizer with path
        pathTokenizer = new StringTokenizer (contPathLocal, BOConstants.OBJECTPATH_SEPARATOR);

        // get the first token:
        if (pathTokenizer.hasMoreElements ())
        {
            // get the container name of the path
            menuTabName = pathTokenizer.nextToken ();

            // there are 3 possible ways of setting the parameters
            // of the stored procedure:
            // 1. a container in the privat area of a user
            // 2. a "normal" container. get the root of the path
            // 3. after executing part 1 or 2, always execute this one
            if (menuTabName.equalsIgnoreCase (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_PRIV_SECTION, env)))
                                    // object is in private area
            {
                // as a user must be specified for a particular privat area
                // the second separator must be found (PRIVAT/USERNAME/...)
                String userName = pathTokenizer.nextToken ();
                // resolve the user joker character
                if (userName.equals (BOConstants.OBJECTPATH_USERJOKER))
                {
                    userName = user.username;
                } // if the user name is the joker character

                // resolvepath retrieves the object id:
                objectOid = BOHelpers.resolveObjectPathData (obj, menuTabName,
                    userName, user.domain, OID.EMPTYOID, isContainer, env);
            } // if
            else    // container not in privat area but getting the root
            {
                // when called first time it must be a menu tab:
                objectOid = BOHelpers.getMenuTabOid (
                    env.getSessionInfo (), menuTabName);
            } // else

            // handle the results of the procedure call:
            if (objectOid != null)  // all OK
            {
                // resolve the rest of the object path:
                objectOid = BOHelpers.resolveObjectPath (pathTokenizer,
                    objectOid, isContainer, obj, env);
            } // if all OK
            else        // nothing returned
            {
                return objectOid;
            } // else nothing returned
        } // if

        // return the oid:
        return objectOid;
    } // resolveObjectPath


    /**************************************************************************
     * Returns the object OID from a given object path. <BR/>
     * Attention: The path separator is the BOConstants.OBJECTPATH_SEPARATOR.
     * A path that reference a object in the private area can contain
     * the joker character BOConstants.OBJECTPATH_USERJOKER instead of the user name.
     * This joker is automaticaly replaced with the name of the current user.
     *
     * @param   pathTokenizer   Tokenizer which was used for parsing the path.
     *                          The first value can already have been read.
     * @param   containerOid    Oid of container (e.g. menu tab).
     *                          If <CODE>null</CODE> the first element will
     *                          be a menu tab (except PRIVAT!).
     * @param   isContainer     Set to <CODE>true</CODE> if the object is a
     *                          container.
     * @param   obj             The object for which to resolve the path.
     * @param   env             The actual environment.
     *
     * @return  The oid of the object.
     */
    public static final OID resolveObjectPath (StringTokenizer pathTokenizer,
                                               OID containerOid,
                                               boolean[] isContainer,
                                               BusinessObject obj,
                                               Environment env)
    {
        String objectName = "";     // contains the name of the object to seek for
        OID objectOid = containerOid; // oid of current object

        // initialize the isContainer flag:
        isContainer[0] = false;

        // resolve the path:
        try
        {
            // loop through all parts of the path:
            while (pathTokenizer.hasMoreElements ())
            {
                // get the container name of the path:
                objectName = pathTokenizer.nextToken ();

                if (objectOid == null)  // getting the root
                {
                    // when called first time it must be a menu tab:
                    objectOid = BOHelpers.getMenuTabOid (env.getSessionInfo (), objectName);
                } // if getting the root
                else                    // any object below the root
                {
                    // resolveObjectPath retrieves the objectOid
                    objectOid = BOHelpers.resolveObjectPathData (obj, objectName, "", -1,
                        objectOid.toString (), isContainer, env);
                } // else any object below the root

                // in case the object oid is null the path could not be
                // resolved:
                if (objectOid == null) // nothing returned
                {
                    return null;
                } // else nothing returned
            } // while
        } // try
        catch (Exception e)
        {
            IOHelpers.printError ("resolveObjectPath", obj, e, true);
            // the catch block is only used for the finally clause
            objectOid = null;
        } // catch

        // return the oid:
        return objectOid;
    } // resolveObjectPath


    /**************************************************************************
     * Gets an OID out of the DB of a object. <BR/>
     * The method resolves group and private path definitions. If setting
     * <CODE>domainId</CODE> to -1
     *
     * @param   obj             The object which tries to get the database connection.
     * @param   objectName      The name of the object to be found.
     * @param   userName        The name of the user's private area.
     *                          If domainId not set to <CODE>-1</CODE> a private
     *                          path will be resolved.
     * @param   domainId        The id of the domain usually set to
     *                          <CODE>-1</CODE>.
     *                          If not <CODE>-1</CODE> it searches for the group
     *                          container in a domain.
     * @param   actualContainer A string representing the oid of the last
     *                          container found.
     * @param   isContainer     <CODE>true</CODE> if the just found object is a
     *                          container otherwise <CODE>false</CODE>.
     * @param   env             The actual environment.
     *
     * @return  The return value of the stored procedure.
     *
     * @deprecated  KR 20090828 Use
     *              {@link #resolveObjectPathData(String, String, int, String, boolean[], Environment)}
     *              instead.
     */
    @Deprecated
    protected static final OID resolveObjectPathData (Object obj,
                                                      String objectName,
                                                      String userName,
                                                      int domainId,
                                                      String actualContainer,
                                                      boolean[] isContainer,
                                                      Environment env)
    {
        // call common method and return the result:
        return BOHelpers.resolveObjectPathData (
            objectName, userName, domainId, actualContainer, isContainer, env);
    } // resolveObjectPathData


    /**************************************************************************
     * Gets an OID out of the DB of a object. <BR/>
     * The method resolves group and private path definitions. If setting
     * <CODE>domainId</CODE> to -1
     *
     * @param   objectName      The name of the object to be found.
     * @param   userName        The name of the user's private area.
     *                          If domainId not set to <CODE>-1</CODE> a private
     *                          path will be resolved.
     * @param   domainId        The id of the domain usually set to
     *                          <CODE>-1</CODE>.
     *                          If not <CODE>-1</CODE> it searches for the group
     *                          container in a domain.
     * @param   actualContainer A string representing the oid of the last
     *                          container found.
     * @param   isContainer     <CODE>true</CODE> if the just found object is a
     *                          container otherwise <CODE>false</CODE>.
     * @param   env             The actual environment.
     *
     * @return  The return value of the stored procedure.
     */
    protected static final OID resolveObjectPathData (String objectName,
                                                      String userName,
                                                      int domainId,
                                                      String actualContainer,
                                                      boolean[] isContainer,
                                                      Environment env)
    {
        int retVal = 0;
        StoredProcedure sp = new StoredProcedure ("p_Object$resolveObjectPath",
            StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // objectName
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        objectName);
        // userName
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        userName);
        // domainId
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                        domainId);
        // actualContainer
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        actualContainer);

        // output parameters:
        // result containerOid
        Parameter containerOidParam =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);

        // result isContainer
        Parameter isContainerParam =
            sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

        // perform the function call:
        try
        {
            retVal = BOHelpers.performCallFunctionData (sp, env);
        } // try
        catch (NoAccessException e)
        {
            // this exception cannot be thrown by the called procedure
        } // catch

        // if the procedure call went ok get the oid
        if (retVal == UtilConstants.QRY_OK)
        {
            // get the oid of the object
            OID objectOid = SQLHelpers.getSpOidParam (containerOidParam);
            int contRes = isContainerParam.getValueInteger ();
            isContainer[0] = contRes == 1;

            return objectOid;
        } // if (retVal == UtilConstants.QRY_OK)

        // query was NOT o.k.
        // check for an error and give the user a message
        if (retVal == UtilConstants.QRY_OBJECTNOTFOUND) // no object found
        {
            // display the error message and quit the method
            return null;
        } // if (retVal == UtilConstants.QRY_OBJECTNOTFOUND)
        else if (retVal == UtilConstants.QRY_TOOMANYROWS) // more than 1 object found
        {
            // display the error message and quit the method
            return null;
        } // if (retVal == UtilConstants.QRY_OBJECTNOTFOUND)
        return null;                    // any other error occurred - NO MESSAGE
    } // resolveObjectPathData


    /**************************************************************************
     * Removes beginning and ending path separators. <BR/>
     *
     * @param path      a string containing the path
     *
     * @return  the path without beginning or ending path separators.
     */
    protected static final String trimPath (String path)
    {
        String pathLocal = path;        // variable for local assignments

        // delete the forward or backward slashes at the begging or end of the path
        if ((pathLocal.startsWith (BOConstants.PATH_FORWARDSEPARATOR)) ||
            (pathLocal.startsWith (BOConstants.PATH_BACKWARDSEPARATOR)))
        {
            pathLocal = pathLocal.substring (1);
        } // if ((contPath.startsWith (BOConstants.PATH_FORWARDSEPARATOR)) ||
        if (pathLocal.endsWith (BOConstants.PATH_FORWARDSEPARATOR))
        {
            pathLocal = pathLocal.substring (0, pathLocal.lastIndexOf (BOConstants.PATH_FORWARDSEPARATOR));
        } // if ((contPath.endsWith (BOConstants.PATH_FORWARDSEPARATOR)))
        else        // check for "\" ad beginning or end of path
        {
            if (pathLocal.endsWith (BOConstants.PATH_BACKWARDSEPARATOR))
            {
                pathLocal = pathLocal.substring (0, pathLocal.lastIndexOf (BOConstants.PATH_BACKWARDSEPARATOR));
            } // if
        } // check for "\" ad beginning or end of path
        return pathLocal;
    } // trimPath


    /**************************************************************************
     * Get the oids of the workspaces of all active users out of the database.
     * <BR/>
     *
     * @param   domainId    The id of the domain.
     *                      If <CODE>-1</CODE> get all users out of the system.
     * @param   obj         The object which tries to get the database connection.
     * @param   app         The global application info.
     * @param   sess        The actual session info object.
     * @param   env         The actual environment.
     *
     * @return  A vector containing all found workspace oids. Empty if no
     *          workspaces where found.
     *
     * @deprecated  KR 20090828 Use
     *              {@link #performGetAllWorkspaces(int, Environment)} instead.
     */
    @Deprecated
    protected static Vector<OID> performGetAllWorkspaces (int domainId,
                                                          Object obj,
                                                          ApplicationInfo app,
                                                          SessionInfo sess,
                                                          Environment env)
    {
        // call common method and return result:
        return BOHelpers.performGetAllWorkspaces (domainId, env);
    } // performGetAllWorkspaces


    /**************************************************************************
     * Get the oids of the workspaces of all active users out of the database.
     * <BR/>
     *
     * @param   domainId    The id of the domain.
     *                      If <CODE>-1</CODE> get all users out of the system.
     * @param   env         The actual environment.
     *
     * @return  A vector containing all found workspace oids. Empty if no
     *          workspaces where found.
     */
    protected static Vector<OID> performGetAllWorkspaces (int domainId,
                                                          Environment env)
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        int rowCount = 0;
        Vector<OID> workspaceOids = new Vector<OID> (); // the result vector

        StringBuffer queryStr = new StringBuffer ()
            .append ("SELECT wsp.workspace")
            .append (" FROM ibs_User u, ibs_Workspace wsp")
            .append (" WHERE u.state = ").append (States.ST_ACTIVE)
            .append (" AND u.id = wsp.userId");

        // check if the domain shall be taken into account, too:
        if (domainId > -1)
        {
            queryStr.append (" AND u.domainId = ").append (domainId);
        } // if

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = BOHelpers.getDBConnection (env);

            // perform the query:
            rowCount = action.execute (queryStr, false);

            if (rowCount > 0)      // at least one tuple found?
            {
                // get tuples out of db:
                while (!action.getEOF ())
                {
                    // get tuple out of db and add it to the result vector:
                    workspaceOids.add (SQLHelpers.getQuOidValue (action,
                        "workspace"));
                    // step one tuple ahead for the next loop:
                    action.next ();
                } // while
            } // if
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info:
            IOHelpers.showMessage (e, env, true);
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
                IOHelpers.showMessage (e, env, true);
            } // catch

            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            BOHelpers.releaseDBConnection (action, env);
        } // finally

        // return the result:
        return workspaceOids;
    } // performGetAllWorkspaces


    /**************************************************************************
     * Returns the oid of a menu tab. <BR/>
     *
     * @param   sess        The actual session info object.
     * @param   menuTabName The name of the menu tab to look for.
     *
     * @return  The oid of the MenuTab.
     */
    public static OID getMenuTabOid (SessionInfo sess, String menuTabName)
    {
        MenuData_01 menDat = null;

        if (menuTabName == null || menuTabName.length () == 0)
        {
            return null;
        } // if

        // loop trough the menu tabs:
        for (int i = 0; i < sess.menus.size (); i++)
        {
            menDat = sess.menus.elementAt (i);
            if (menDat.name.equals (menuTabName))
            {
                return menDat.oid;
            } // if
        } // for

        return null;
    } // getMenuTabOid


    /**************************************************************************
     * Replaces any occurrences of a a system variable <CODE>#SYSVAR.????#</CODE>
     * in a string by its corresponding value. <BR/>
     *
     * @param   obj The object for which to replace the sysvar.
     * @param   str The string to be changed
     *
     * @return  the input string where all sysvars are replaced by their
     *          corresponding values
     */
    public static final String replaceSysVar (BusinessObject obj, String str)
    {
        String strLocal = str;          // variable for local assignments
        // replace system variables in string with values:
        Variables vars = new Variables ();

        try
        {
            vars.addSysVars (obj);
            strLocal = vars.replaceWithValue (strLocal, obj.getEnv ());
        } // try
        catch (ActionException e)
        {
            IOHelpers.showMessage (e, obj.app, obj.sess, obj.getEnv ());
        } // catch

        // return the string:
        return strLocal;
    } // replaceSysVar


    /**************************************************************************
     * Set an OID input parameter of a stored procedure. <BR/>
     * This method is just a shortcut for handling OID input parameters.
     *
     * @param   sp      The stored procedure object.
     * @param   value   The parameter value, i.e. an OID.
     *
     * @return  The newly created parameter.
     */
    public static Parameter addInParameter (StoredProcedure sp, OID value)
    {
        // check if the oid is not null and set the parameter value
        if (value != null)              // the oid is not null?
        {
            // convert the oid to a string representation and set the parameter:
            return sp.addInParameter (ParameterConstants.TYPE_STRING,
                            value.toString ());
        } // if

        // no oid defined
        // use the default oid:
        return sp.addInParameter (ParameterConstants.TYPE_STRING,
                        OID.EMPTYOID);
    } // addInParameter


    /**************************************************************************
     * Execute a stored procedure as function, i.e. a stored procedure that
     * returns a value. <BR/>
     *
     * @param   sp          The stored procedure to be executed.
     *                      Important: it must have a valid name and return type!
     * @param   env         The actual environment.
     *
     * @return  The return value of the stored procedure.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    public static int performCallFunctionData (StoredProcedure sp,
                                               Environment env)
        throws NoAccessException
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        int retVal = UtilConstants.QRY_NOT_OK; // return value of procedure

//IOHelpers.showProcCall (this, sp, this.sess, this.env);
        // execute stored procedure
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = BOHelpers.getDBConnection (env);
//trace ("KR got the action.");
            // execute stored procedure - return value
            // gives right-information
            retVal = action.execStoredProc (sp);

//trace ("KR after execution.");
            // end action
            action.end ();
//trace ("KR after action.end.");
        } // try
        catch (DBError e)
        {
            // get all errors (can be chained)
            // show the message:
            IOHelpers.showMessage (e, env, true);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
//trace ("KR before releaseDBConnection...");
            BOHelpers.releaseDBConnection (action, env);
//trace ("KR after releaseDBConnection.");
        } // finally

        // check the rights
        if (retVal == UtilConstants.QRY_INSUFFICIENTRIGHTS) // access not allowed?
        {
            // raise no access exception
            NoAccessException error = new NoAccessException (
                MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
                    UtilExceptions.ML_E_NOACCESSEXCEPTION, env));
            throw error;
        } // if access not allowed

        return retVal;                  // return the computed return value
    } // performCallFunctionData


    /**************************************************************************
     * Execute a stored procedure as procedure, i.e. a stored procedure that
     * does not return a value. <BR/>
     *
     * @param   sp          The stored procedure to be executed.
     *                      Important: it must have a valid name and return type!
     * @param   env         The actual environment.
     */
    public static void performCallProcedureData (StoredProcedure sp,
                                                 Environment env)
    {
        SQLAction action = null;        // the action object used to access the
                                        // database

//IOHelpers.showProcCall (this, sp, this.sess, this.env);
        // execute stored procedure
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = BOHelpers.getDBConnection (env);
            // execute stored procedure - return value
            // gives right-information
            action.execStoredProc (sp);
            // end action
            action.end ();
        } // try
        catch (DBError e)
        {
            // show the message:
            IOHelpers.showMessage (e, env, true);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            BOHelpers.releaseDBConnection (action, env);
        } // finally
    } // performCallProcedureData


    /**************************************************************************
     * Gets the parameter value of a ibs.bo.Datatypes.DT_FILE type. <BR/>
     * The 'BO' in the methods name only indicates that it is not (like the
     * other getxxxParam methods) a method of the environment class.
     *
     * @param   arg     Parameter arguments name.
     * @param   env     The current environment to get user's input and write
     *                  output to.
     *
     * @return  The file name or <CODE>null</CODE> if the argument was not found
     *          or another error occurred.
     */
    public static final String getFileParamBO (String arg, Environment env)
    {
        String param = null;            // return value

        try
        {
            // second parameter is null, cause the
            // targetDir should be gotten from the
            // parameters in the Request
            param = env.getFileParam (arg, null);
        } // try
        catch (UploadException e)
        {
            // show the ExceptionMessage
            IOHelpers.showMessage (e, env);
            param = null;
        } // catch

        if (param != null && param.length () == 0) // parameter not set?
        {
            param = null;               // parameter does not exist
        } // if

        // return value
        return param;
    } // getFileParamBO


    /**************************************************************************
     * Constructs the path for data files. <BR/>
     *
     * @param   app     The global application info object.
     * @param   oid     The oid of the actual object.
     *
     * @return  The file path for a data file.
     *
     * @deprecated  KR 20090828 Use {@link #getFilePath(OID)} instead.
     */
    @Deprecated
    public static final String getFilePath (ApplicationInfo app, OID oid)
    {
        // create the filepath and return it:
        return app.p_system.p_m2AbsBasePath +
            BOPathConstants.PATH_UPLOAD_ABS_FILES + oid + File.separatorChar;
    } // getFilePath


    /**************************************************************************
     * Constructs the path for data files. <BR/>
     *
     * @param   oid     The oid of the actual object.
     *
     * @return  The file path for a data file.
     */
    public static final String getFilePath (OID oid)
    {
        // create the filepath and return it:
        return IbsGlobals.p_app.p_system.p_m2AbsBasePath +
            BOPathConstants.PATH_UPLOAD_ABS_FILES + oid + File.separatorChar;
    } // getFilePath


    /**************************************************************************
     * Constructs the path for data files. <BR/>
     *
     * @param   oidStr  The oid of the actual object.
     *
     * @return  The file path for a data file.
     */
    public static final String getFilePath (String oidStr)
    {
        // create the filepath and return it:
        return IbsGlobals.p_app.p_system.p_m2AbsBasePath +
            BOPathConstants.PATH_UPLOAD_ABS_FILES + oidStr + File.separatorChar;
    } // getFilePath


    /**************************************************************************
     * Constructs the path for image files. <BR/>
     *
     * @param   oid     The oid of the actual object.
     *
     * @return  The file path for image files.
     */
    public static final String getImagePath (OID oid)
    {
        // use standard file path:
        return BOHelpers.getFilePath (oid);
    } // getImagePath

    
    /**************************************************************************
     * Convert a list of oid strings to a list of oids. <BR/>
     * 
     * @param oidStrList    The list of oid strings.
     * 
     * @return The resulting list of oids.
     */
    public static final List<OID> convertStringListToOidList (
        List<String> oidStrList)
    {
        List<OID> oidList = new ArrayList<OID> ();

        // loop through all elements of the list and convert each one to an OID:
        for (Iterator<String> iter = oidStrList.iterator (); iter.hasNext ();)
        {
            try
            {
                oidList.add (new OID (iter.next ()));
            } // try
            catch (IncorrectOidException e)
            {
                // this case should never occur.
                // write an error message to the console output:
                IOHelpers.printError ("convertStringToOidList", e, true);
            } // catch IncorrectOidException
        } // for iter

        // return the result:
        return oidList;
    } // convertStringListToOidList


    /**************************************************************************
     * Reorder a list of objects according to a list of oids. <BR/>
     * 
     * @param objList       List of objects.
     * @param orderingList  List of oids in the correct order.
     * 
     * @return The resulting list of objects.
     */
    public static final Vector<BusinessObjectInfo> reorderObjects (
        Vector<BusinessObjectInfo> objList, List<OID> orderingList)
    {
        Vector<BusinessObjectInfo> result = new Vector<BusinessObjectInfo> ();
        BusinessObjectInfo[] objArr = new BusinessObjectInfo[orderingList.size ()];

        // sort the objects according to the intended order into an array:
        for (Iterator<BusinessObjectInfo> iter = objList.iterator (); iter.hasNext ();)
        {
            // search for the element within the object list:
            BusinessObjectInfo objInfo = iter.next ();
            objArr[orderingList.indexOf (objInfo.p_oid)] = objInfo;
        } // for iter

        // put the array elements into a list:
        for (int i = 0; i < objArr.length; i++)
        {
            result.add (objArr[i]);
        } // for i

        // return the result:
        return result;
    } // reorderObjects


    /**************************************************************************
     * Find all business objects for a list of given oid strings. <BR/>
     * The function finds all objects which are active. The result is sorted by
     * the name of the objects.
     * 
     * @param   oidStrList      A list of oid strings for which the objects should be
     *                          found.
     * @param   env             The actual environment.
     * @param   keepOrdering    Shall the ordering be kept?
     *
     * @return  The found objects. <BR/>
     *          The result is empty if no objects were found. <BR/>
     *          <CODE>null</CODE> if there occurred an error.
     */
    public static final Vector<BusinessObjectInfo> findObjects (
        List<String> oidStrList, Environment env, boolean keepOrdering)
    {
        // convert the oid strings to oids:
        List<OID> oidList = convertStringListToOidList (oidStrList);

        // call common method and return the result:
        return findObjects (oidList, keepOrdering, env);
    } // findObjects


    /**************************************************************************
     * Find all business objects for a list of given oid strings. <BR/>
     * The function finds all objects which are active. The result is sorted by
     * the name of the objects.
     * 
     * @param   oidList         A list of oids for which the objects should be
     *                          found.
     * @param   keepOrdering    Shall the ordering be kept?
     * @param   env             The actual environment.
     *
     * @return  The found objects. <BR/>
     *          The result is empty if no objects were found. <BR/>
     *          <CODE>null</CODE> if there occurred an error.
     */
    public static final Vector<BusinessObjectInfo> findObjects (
        List<OID> oidList, boolean keepOrdering, Environment env)
    {
        StringBuilder oidFilter = new StringBuilder ();
        Vector<BusinessObjectInfo> result = null;

        // create database specific selection code for oids:
        oidFilter = SQLHelpers.oidListToQueryString (oidList);

        // call common method and return the result:
        result = BOHelpers.findObjects (null, null, oidFilter, null, env);

        // check if the ordering of the oids shall be kept:
        if (keepOrdering)
        {
            // reorder the result list according the original oid list:
            result = reorderObjects (result, oidList);
        } // if

        return result;
    } // findObjects

    
    /**************************************************************************
     * Find all business objects for a specific type. <BR/>
     * The function finds all objects which are active.
     * The result is sorted by the name of the objects.
     *
     * @param   type            The type for which to find the objects.
     * @param   env             The actual environment.
     *
     * @return  The found objects. <BR/>
     *          The result is empty if no objects were found. <BR/>
     *          <CODE>null</CODE> if there occurred an error.
     */
    public static final Vector<BusinessObjectInfo> findObjects (
                                                                Type type,
                                                                Environment env)
    {
        // call common method and return the result:
        return BOHelpers.findObjects (type, (StringBuilder) null, env);
    } // findObjects


    /**************************************************************************
     * Find all business objects for a specific type. <BR/>
     * The function finds all objects which are active.
     * The result is sorted by the name of the objects.
     *
     * @param   type            The type for which to find the objects.
     * @param   otherSQLClause  Another sql clause to append to the query.
     * @param   env             The actual environment.
     *
     * @return  The found objects. <BR/>
     *          The result is empty if no objects were found. <BR/>
     *          <CODE>null</CODE> if there occurred an error.
     */
    public static final Vector<BusinessObjectInfo> findObjects (
                                                                Type type,
                                                                StringBuffer otherSQLClause,
                                                                Environment env)
    {
        // call common method and return the result:
        return BOHelpers.findObjects (
            new StringBuffer ().append (States.ST_ACTIVE),
            new StringBuffer ().append (type.getTVersionId ()), null, otherSQLClause, env);
    } // findObjects


    /**************************************************************************
     * Find all business objects for a specific type. <BR/>
     * The function finds all objects which are active.
     * The result is sorted by the name of the objects.
     *
     * @param   type            The type for which to find the objects.
     * @param   otherSQLClause  Another sql clause to append to the query.
     * @param   env             The actual environment.
     *
     * @return  The found objects. <BR/>
     *          The result is empty if no objects were found. <BR/>
     *          <CODE>null</CODE> if there occurred an error.
     */
    public static final Vector<BusinessObjectInfo> findObjects (
                                                                Type type,
                                                                StringBuilder otherSQLClause,
                                                                Environment env)
    {
        // call common method and return the result:
        return BOHelpers.findObjects (
            new StringBuilder ().append (States.ST_ACTIVE),
            new StringBuilder ().append (type.getTVersionId ()), null, otherSQLClause, env);
    } // findObjects


    /**************************************************************************
     * Find all business objects for a specific type. <BR/>
     * The function finds all objects which are active.
     * The result is sorted by the name of the objects.
     *
     * @param   tVersionId      The tVersionId of the type for which to find
     *                          the objects.
     * @param   env             The actual environment.
     *
     * @return  The found objects. <BR/>
     *          The result is empty if no objects were found. <BR/>
     *          <CODE>null</CODE> if there occurred an error.
     */
    public static final Vector<BusinessObjectInfo> findObjects (
                                                                int tVersionId,
                                                                Environment env)
    {
        // call common method and return the result:
        return BOHelpers.findObjects (
            null, new StringBuffer ().append (tVersionId), null, null, env);
    } // findObjects


    /**************************************************************************
     * Find all business objects for a specific type. <BR/>
     * The function finds all objects which are active.
     * The result is sorted by the name of the objects.
     * All filters can be either a single value or a comma-separated list
     * surrounded by "(...)" for the IN clause. If the list is not surrounded
     * by brackets they are automatically added.
     *
     * @param   stateFilter     Filter for object state.
     * @param   tVersionId      The tVersionId of the type for which to find
     *                          the objects.
     * @param   env             The actual environment.
     *
     * @return  The found objects. <BR/>
     *          The result is empty if no objects were found. <BR/>
     *          <CODE>null</CODE> if there occurred an error.
     */
    public static final Vector<BusinessObjectInfo> findObjects (
                                                                StringBuffer stateFilter,
                                                                int tVersionId,
                                                                Environment env)
    {
        // call common method and return the result:
        return BOHelpers.findObjects (
            stateFilter, new StringBuffer ().append (tVersionId), null, null, env);
    } // findObjects


    /**************************************************************************
     * Find all business objects for a specific type. <BR/>
     * The function finds all objects which are active.
     * The result is sorted by the name of the objects.
     * All filters can be either a single value or a comma-separated list
     * surrounded by "(...)" for the IN clause. If the list is not surrounded
     * by brackets they are automatically added.
     *
     * @param   stateFilter     Filter for object state.
     * @param   tVersionId      The tVersionId of the type for which to find
     *                          the objects.
     * @param   env             The actual environment.
     *
     * @return  The found objects. <BR/>
     *          The result is empty if no objects were found. <BR/>
     *          <CODE>null</CODE> if there occurred an error.
     */
    public static final Vector<BusinessObjectInfo> findObjects (
                                                                StringBuilder stateFilter,
                                                                int tVersionId,
                                                                Environment env)
    {
        // call common method and return the result:
        return BOHelpers.findObjects (
            stateFilter, new StringBuilder ().append (tVersionId), null, null, env);
    } // findObjects


    /**************************************************************************
     * Find all business objects for a specific type code. <BR/>
     * The function finds all objects which are active.
     * The result is sorted by the name of the objects.
     *
     * @param   typeCode        The type for which to find the objects.
     * @param   otherSQLClause  Another sql clause to append to the query.
     * @param   env             The actual environment.
     *
     * @return  The found objects. <BR/>
     *          The result is empty if no objects were found. <BR/>
     *          <CODE>null</CODE> if there occurred an error.
     */
    public static final Vector<BusinessObjectInfo> findObjects (
                                                                String typeCode,
                                                                StringBuffer otherSQLClause,
                                                                Environment env)
    {
        // call common method and return the result:
        return BOHelpers.findObjects (
            new StringBuffer ().append (States.ST_ACTIVE),
            new StringBuffer ().append (BOHelpers.getTypeCache ()
                .getTVersionId (typeCode)),
            null, otherSQLClause, env);
    } // findObjects


    /**************************************************************************
     * Find all business objects for a specific type code. <BR/>
     * The function finds all objects which are active.
     * The result is sorted by the name of the objects.
     *
     * @param   typeCode        The type for which to find the objects.
     * @param   otherSQLClause  Another sql clause to append to the query.
     * @param   env             The actual environment.
     *
     * @return  The found objects. <BR/>
     *          The result is empty if no objects were found. <BR/>
     *          <CODE>null</CODE> if there occurred an error.
     */
    public static final Vector<BusinessObjectInfo> findObjects (
                                                                String typeCode,
                                                                StringBuilder otherSQLClause,
                                                                Environment env)
    {
        // call common method and return the result:
        return BOHelpers.findObjects (
            new StringBuilder ().append (States.ST_ACTIVE),
            new StringBuilder ().append (BOHelpers.getTypeCache ()
                .getTVersionId (typeCode)),
            null, otherSQLClause, env);
    } // findObjects


    /**************************************************************************
     * Find all business objects that are result of the query with the given
     * filter. <BR/>
     * The result is sorted by the name of the objects.
     * All filters can be either a single value or a comma-separated list
     * surrounded by "(...)" for the IN clause. If the list is not surrounded
     * by brackets they are automatically added.
     *
     * @param   stateFilter     Filter for object state.
     * @param   tVersionIdFilter tversionID filter.
     * @param   oidFilter       Oid filter.
     * @param   otherSQLClause  Another sql clause to append to the query.
     * @param   env             The actual environment.
     *
     * @return  The found objects. <BR/>
     *          The result is empty if no objects were found. <BR/>
     *          <CODE>null</CODE> if there occurred an error.
     */
    public static final Vector<BusinessObjectInfo> findObjects (
                                                                StringBuffer stateFilter,
                                                                StringBuffer tVersionIdFilter,
                                                                StringBuffer oidFilter,
                                                                StringBuffer otherSQLClause,
                                                                Environment env)
    {
        // call common method and return the result:
        return BOHelpers.findObjects (
            stateFilter != null ? new StringBuilder (stateFilter) : null,
            tVersionIdFilter != null ? new StringBuilder (tVersionIdFilter) : null,
            oidFilter != null ? new StringBuilder (oidFilter) : null,
            otherSQLClause != null ? new StringBuilder (otherSQLClause) : null,
            env);
    } // findObjects


    /**************************************************************************
     * Create a filter condition for a query. <BR/>
     * The filter can be either a single value or a comma-separated list
     * surrounded by "(...)" for the IN clause. If the list is not surrounded
     * by brackets they are automatically added. <BR/>
     * The filter may also contain "NULL" or "IS NULL" or "IS NOT NULL" to
     * indicate a check for NULL.
     *
     * @param   attrName    The attribute to be filtered.
     *                      May be something like "o.name", "table.oid", etc.
     * @param   filter      The filter string.
     *
     * @return  The filter condition. <BR/>
     *          <CODE>null</CODE> if the attribute name or the filter where
     *          <CODE>null</CODE>.
	 *
     * @deprecated  BB 20090923 Use
     *              {@link ibs.tech.sql.SQLHelpers.createQueryHelpers}
     *              instead.
     */
    private static final StringBuilder createQueryFilter (String attrName,
                                                         StringBuilder filter)
    {
    	// BB20091001: note that a filter can contain whitespace without any data
    	// this will lead to a SQL exception.
    	// Such a contraint must be checked in the appropriate business logic!
        if (attrName != null && filter != null)
        {
            // check if the filter contains the value "NULL":
            if (filter.equals ("NULL") || filter.equals ("IS NULL"))
            {
                return new StringBuilder (attrName).append (" IS NULL");
            } // if
            if (filter.equals ("IS NOT NULL"))
            {
                return new StringBuilder (attrName).append (" IS NOT NULL");
            } // if
            // check if the filter starts and "(" and ends with ")"
            // which indicates a list of values:
            else if (filter.charAt(0) == '(' && 
            		 filter.charAt(filter.length() - 1) == ')')
            {
                return new StringBuilder (attrName)
                    .append (" IN ").append (filter);
            } // if
            // check if the filter contains a "," which also indicates a list of
            // values (but missing "(...)"):
            // BB 20091001: be carefill with value that contain , in their data!
            else if (filter.indexOf (",") > -1)
            {
                return new StringBuilder (attrName)
                    .append (" IN (").append (filter).append (")");
            } // else if
            else
            {
                return new StringBuilder (attrName).append (" = ").append (filter);
            } // else
        } // if

        // no valid filter defined
        return null;
    } // createQueryFilter


    /**************************************************************************
     * Find all business objects that are result of the query with the given
     * filter. <BR/>
     * The result is sorted by the name of the objects.
     * All filters can be either a single value or a comma-separated list
     * surrounded by "(...)" for the IN clause. If the list is not surrounded
     * by brackets they are automatically added.
     *
     * @param   stateFilter     Filter for object state.
     * @param   tVersionIdFilter tversionID filter.
     * @param   oidFilter       Oid filter.
     * @param   otherSQLClause  Another sql clause to append to the query.
     * @param   env             The actual environment.
     *
     * @return  The found objects. <BR/>
     *          The result is empty if no objects were found. <BR/>
     *          <CODE>null</CODE> if there occurred an error.
     */
    public static final Vector<BusinessObjectInfo> findObjects (
                                                                StringBuilder stateFilter,
                                                                StringBuilder tVersionIdFilter,
                                                                StringBuilder oidFilter,
                                                                StringBuilder otherSQLClause,
                                                                Environment env)
    {
        // create the SQL Query to select the objects:
        SelectQuery query = new SelectQuery (
            new StringBuilder ("o.oid, o.state, o.name, o.typename, o.description"),
            new StringBuilder ("ibs_Object o"),
            null, null, null,
            new StringBuilder ("o.name ASC"));

        // add filters to query:
        query.extendWhere (BOHelpers.createQueryFilter ("o.state", stateFilter));
        query.extendWhere (BOHelpers.createQueryFilter ("o.tVersionId", tVersionIdFilter));
        query.extendWhere (BOHelpers.createQueryFilter ("o.oid", oidFilter));

        if (otherSQLClause != null)
        {
            query.extendWhere (new StringBuilder (otherSQLClause));
        } // if (otherSQLClause != null)

        // get the objects from the query
        return BOHelpers.findObjects (query, env);
    } // findObjects


    /**************************************************************************
     * Find all business objects that are result of the query. <BR/>
     * The query must return the fields "oid", "name", "state", and "typename".
     *
     * @param   query           The query to be performed to read the data.
     * @param   env             The actual environment.
     *
     * @return  The found objects. <BR/>
     *          The result is empty if no objects were found. <BR/>
     *          <CODE>null</CODE> if there occurred an error.
     */
    public static final Vector<BusinessObjectInfo> findObjects (
                                                                SelectQuery query,
                                                                Environment env)
    {
        int rowCount;                   // number of result rows
        SQLAction action = null;        // the action object used to access the DB
        Vector<BusinessObjectInfo> objects = null;

        // contraint: the query must be defined:
        if (query == null)
        {
            return null;
        } // if (queryStr == null)

        try
        {
            action = BOHelpers.getDBConnection (env);
            rowCount = action.execute (query);

            // check if the resultset is not empty:
            if (rowCount > 0)
            {
                // create the objects vector:
                objects = new Vector<BusinessObjectInfo> (rowCount);

                // get the data:
                while (!action.getEOF ())
                {
                    // the current object info:
                    BusinessObjectInfo objInfo = new BusinessObjectInfo (
                        SQLHelpers.getQuOidValue (action, "oid"),
                        action.getInt ("state"),
                        action.getString ("name"),
                        action.getString ("typename"),
                        action.getString ("description"));
                    // add it to the result vector:
                    objects.addElement (objInfo);
                    // go to the next result tuple:
                    action.next ();
                } // while (!action.getEOF())
            } // if (rowCount > 0)
            else
            {
                // create an empty objects vector when result is empty:
                objects = new Vector<BusinessObjectInfo> (0);
            } // else

            // finish transaction:
            action.end ();
        } // try
        catch (DBError e)
        {
            // show error message:
            IOHelpers.showMessage (e, env, true);
        } // catch
        finally
        {
            // the database connection is not longer needed:
            BOHelpers.releaseDBConnection (action, env);
        } // finally

        // return the result:
        return objects;
    } // findObjects


    /**************************************************************************
     * Get the data element for an object. <BR/>
     *
     * @param   oid     The oid of the object for which to get the data element.
     * @param   env     The current environment.
     *
     * @return  The data element.
     *          <CODE>null</CODE> if no data element exists.
     */
    public static DataElement getObjectDataElement (OID oid, Environment env)
    {
        // the resulting data element:
        DataElement dataElement = null;
        // get the business object out of the cache:
        BusinessObject obj = BOHelpers.getObject (oid, env, false);

        // check if an object was found:
        if (obj != null)
        {
            // create a dataElement to hold the data:
            dataElement = new DataElement ();
            // write the data of the object into the data element:
            obj.writeExportData (dataElement);
        } // if

        // return the result:
        return dataElement;
    } // getObjectDataElement
    
    
    /**************************************************************************
     * Get the oid of the object defined by the given ext key. <BR/>
     *
     * @param   extKeyDomain    The domain name of the ext. key
     * @param   extKeyId        The id of the ext. key
     * @param   env             The current environment.
     *
     * @return  The OID.
     *          <CODE>null</CODE> if no object has been found.
     */
    public static OID getOidByExtKey (String extKeyDomain, String extKeyId, Environment env)
    {
        // instantiate the Keymapper to resolve the external object key:
        KeyMapper keyMapper =  new KeyMapper (
                env.getUserInfo ().getUser (), env,
                env.getSessionInfo (), env.getApplicationInfo());
        
        // try to resolve the external key:
        OID oid = keyMapper.performResolveMapping (new KeyMapper.ExternalKey (extKeyDomain, extKeyId));
        
        return oid;
    } // getOidByExtKey

    
    /**************************************************************************
     * Get the ext key of the object defined by the given oid. <BR/>
     *
     * @param   oidObj  OID             The oid of the object
     * @param   env     Environment     The current environment.
     *
     * @return  The ExternalKey.
     *          <CODE>null</CODE> if no object has been found.
     */
    public static ExternalKey getExtKeyByOid (OID oidObj, Environment env)
    {
        // instantiate the Keymapper to resolve the external object key:
        KeyMapper keyMapper =  new KeyMapper (
                env.getUserInfo ().getUser (), env,
                env.getSessionInfo (), env.getApplicationInfo());
        
        // try to resolve the external key:
        ExternalKey extKey = keyMapper.performResolveMapping (oidObj, false);
        
        return extKey;
    } // getExtKeyByOid


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////


} // class BOHelpers

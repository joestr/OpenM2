/*
 * Class: FunctionValues.java
 */

// package:
package ibs.app.func;

// imports:
import ibs.BaseObject;
import ibs.app.AppFunctions;
import ibs.app.UserInfo;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOMessages;
import ibs.bo.BOPathConstants;
import ibs.bo.BusinessObject;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.ObjectClassNotFoundException;
import ibs.bo.ObjectInitializeException;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.cache.ObjectPool;
import ibs.bo.tab.TabContainer;
import ibs.bo.type.Type;
import ibs.bo.type.TypeConstants;
import ibs.bo.type.TypeContainer;
import ibs.bo.type.TypeNotFoundException;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.Ssl;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.wsp.Workspace_01;
import ibs.service.conf.IConfiguration;
import ibs.service.conf.ServerRecord;
import ibs.service.user.User;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;


/******************************************************************************
 * This class holds and handles common values for all function handlers. <BR/>
 *
 * @version     $Id: FunctionValues.java,v 1.25 2010/04/07 13:37:17 rburgermann Exp $
 *
 * @author      Klaus, 15.11.2003
 ******************************************************************************
 */
public class FunctionValues extends BaseObject implements Cloneable
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FunctionValues.java,v 1.25 2010/04/07 13:37:17 rburgermann Exp $";


    /**
     * Holds the actual application info. <BR/>
     * This application info object is used to store the global (= user
     * independent) data within the application.
     */
    public ApplicationInfo p_app = null;

    /**
     * Holds the actual session info. <BR/>
     */
    public SessionInfo p_sess = null;

    /**
     * The environment which holds the values. <BR/>
     */
    public Environment p_env = null;

    /**
     * The function to be executed. <BR/>
     */
    public int p_function = AppFunctions.FCT_NOFUNCTION;

    /**
     * The oid of the actual object. <BR/>
     */
    public OID p_oid = OID.getEmptyOid ();

    /**
     * The actual object, derived through the oid. <BR/>
     */
    private BusinessObject p_obj = null;

    /**
     * The oid of the actual container. <BR/>
     */
    public OID p_containerOid = OID.getEmptyOid ();

    /**
     * The actual representation form. <BR/>
     */
    public int p_representationForm = UtilConstants.REP_STANDARD;


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a FunctionValues object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   app     The global application info object.
     * @param   sess    The actual session info object.
     * @param   env     The environment which holds the values.
     */
    public FunctionValues (ApplicationInfo app, SessionInfo sess, Environment env)
    {
        // call cosntructor of super class:
        super ();

        // set properties:
        this.p_app = app;
        this.p_sess = sess;
        this.p_env = env;

        // call initializer:
        this.initialize (env);
    } // FunctionValues


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get the values out of the environment. <BR/>
     *
     * @param   env     The environment which holds the values.
     */
    private void initialize (Environment env)
    {
        int num = 0;                    // the current number

        // get the actual function out of the arguments for this call:
        this.p_function = env.getIntParam (BOArguments.ARG_FUNCTION);
//trace ("KR function = " + this.p_function);
        if (this.p_function == IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
                                        // no function?
        {
            // no valid function => set default value:
            this.p_function = AppFunctions.FCT_LAYOUTPERFORM;
        } // if no function

        // get the oid:
        this.setOid (env.getOidParam (BOArguments.ARG_OID));

        // get the container oid:
        this.p_containerOid = env.getOidParam (BOArguments.ARG_CONTAINERID);

        // try to get oid of object or of container through path:
        if (this.p_oid == null || this.p_containerOid == null)
                                        // oid not set?
        {
            // get the path from the url:
            String opath = env.getStringParam (BOArguments.ARG_OPATH);
            String objectName = env.getStringParam (BOArguments.ARG_OBJECT);

            // check if path and objectname given:
            if (this.p_containerOid == null && opath != null)
            {
                // resolve the container oid:
                this.p_containerOid = this.resolveObjectPath (env, opath);
            } // if

            if (this.p_oid == null && opath != null && objectName != null)
            {
                // resolve the oid:
                this.resolveObjectPath (env,
                    opath + BOConstants.PATH_FORWARDSEPARATOR + objectName);
            } // if
        } // if oid not set

        // get the actual representation form out of the arguments for this
        // call:
        if ((num = this.p_env.getIntParam (BOArguments.ARG_REPRESENTATION)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.p_representationForm = num;
        } // if
    } // initialize


    /**************************************************************************
     * Get the homepagepath out of the environment and the actual situation.
     * <BR/>
     * If SSL is required and available then a secure-mode URL is retourned,
     * otherwise a non-secure-mode URL. <BR/>
     *
     * @return  The actual homepagePath depending on the actual mode.
     */
    public String getHomePagePath ()
    {
        String dummy = "";              // a dummy string

        // check if SSL is required and available:
        boolean sslRequired =
            Ssl.isSslRequired (this.p_sess) &&
            ((ServerRecord) this.p_sess.actServerConfiguration).getSsl ();

        if (sslRequired)                // SSL is necessary?
        {
            dummy = Ssl.getSecureUrl (
                this.p_env.getServerVariable (IOConstants.SV_URL),
                this.p_sess);
        } // if SSL is necessary
        else                            // SSL is not necessary
        {
            dummy = Ssl.getNonSecureUrl (
                this.p_env.getServerVariable (IOConstants.SV_URL),
                this.p_sess);
        } // else SSL is not necessary

        if (dummy.indexOf (BOPathConstants.PATH_APP) < 0)
                                        // the app directory could not be found
        {
            return null;
        } // if the app directory could not be found

        // app was found
        return dummy.substring (0, dummy.indexOf (BOPathConstants.PATH_APP) +
            BOPathConstants.PATH_APP.length ());
    } // getHomePagePath


    /*************************************************************************
     * sets the homepagepath in the current userInfo. <BR/>
     */
    public void setHomepagePath ()
    {
        String homepagePath;            // the homepage path value

        if (this.getUserInfo ().homepagePath == null)
                                        // no homepagpath set yet?
        {
            // gets the homepagePath-value of the parameter ARG_PATH
            homepagePath = this.p_env.getStringParam (BOArguments.ARG_PATH);

            if (homepagePath == null)   // no parameter was set
            {
                // get the homepage path which is already stored:
                homepagePath = this.getHomePagePath ();
            } // if no parameter was set

            (this.getUserInfo ()).homepagePath = homepagePath;
        } // if no homepagpath set yet
    } // setHomepagePath


    /**************************************************************************
     * Creates and returns a copy of this object. <BR/>
     * For any object <tt>x</tt>, the following expressions will be
     * <tt>true</tt>:
     * <blockquote><pre>
     * x.clone () != x
     * x.clone ().getClass () == x.getClass ()
     * x.clone ().equals (x)
     * </pre></blockquote>
     * The object returned by this method is independent of this object (which
     * is being cloned).
     *
     * @return  A clone of this instance. <BR/>
     *
     * @exception   CloneNotSupportedException
     *              if the object's class does not support the
     *              <code>Cloneable</code> interface. Subclasses that override
     *              the <code>clone</code> method can also throw this exception
     *              to indicate that an instance cannot be cloned.
     * @exception   OutOfMemoryError
     *              if there is not enough memory.
     *
     * @see java.lang.Cloneable
     */
    public Object clone () throws CloneNotSupportedException, OutOfMemoryError
    {
        FunctionValues obj = null;      // the new object

        try
        {
            // call corresponding method of super class:
            obj = (FunctionValues) super.clone ();

            // set specific properties:
            // because the clone method of {@link java.lang.Object Object}
            // performs a shallow and not a deep copy of all existing properties
            // we have to perform the deep copy here to ensure that there are
            // no side effects.
            obj.p_oid = (OID) ((obj.p_oid != null) ? obj.p_oid.clone () : null);
            obj.p_containerOid = (OID)
                ((obj.p_containerOid != null) ? obj.p_containerOid.clone () : null);
        } // try
        catch (CloneNotSupportedException e)
        {
            throw e;
        } // catch CloneNotSupportedException
        catch (OutOfMemoryError e)
        {
            throw e;
        } // catch OutOfMemoryError

        // return the new object:
        return obj;
    } // clone


    /**************************************************************************
     * Get the object cache. <BR/>
     *
     * @return  The cache object.
     */
    public ObjectPool getObjectCache ()
    {
        return (ObjectPool) this.p_app.cache;
    } // getObjectCache


    /**************************************************************************
     * Get the type cache. <BR/>
     *
     * @return  The cache object.
     */
    public TypeContainer getTypeCache ()
    {
        return ((ObjectPool) this.p_app.cache).getTypeContainer ();
    } // getTypeCache


    /**************************************************************************
     * Get the tab cache. <BR/>
     *
     * @return  The cache object.
     */
    public TabContainer getTabCache ()
    {
        return ((ObjectPool) this.p_app.cache).getTabContainer ();
    } // getTabCache


    /**************************************************************************
     * Get the function handler cache. <BR/>
     *
     * @return  The cache object.
     */
    public FunctionHandlerContainer getFunctionCache ()
    {
        return ((ObjectPool) this.p_app.cache).getFunctionHandlerContainer ();
    } // getFunctionCache


    /**************************************************************************
     * Get the tab cache. <BR/>
     *
     * @return  The cache object.
     */
    public IConfiguration getConfiguration ()
    {
        return (IConfiguration) this.p_app.configuration;
    } // getConfiguration


    /**************************************************************************
     * Creates a java object representing the actual business object. <BR/>
     * When calling this method the parameters are read, too.
     *
     * @return  The generated object. <BR/>
     *          <CODE>null</CODE> if the object could not be generated.
     */
    public BusinessObject getObject ()
    {
        // check if there is already an object stored:
        if (this.p_obj == null)         // no object stored?
        {
            // call common method for getting the object:
            this.p_obj = this.getObject (this.p_oid, true);
        } // if no object stored

        // return the object:
        return this.p_obj;
    } // getObject


    /**************************************************************************
     * Creates a java object representing the actual business object. <BR/>
     * When calling this method the parameters are read, too.
     * If the parameter <CODE>getObjectForType</CODE> is set to
     * <CODE>true</CODE> the method tries to get the object through the oid in
     * the url. If this is not possible it gets a dummy object through the
     * type code in the url.
     *
     * @param   getObjectForType    Get the object for the type?
     *                              If set to <CODE>false</CODE> this methods
     *                              works exactly as
     *                              {@link #getObject () getObject}
     *
     * @return  The generated object. <BR/>
     *          <CODE>null</CODE> if the object could not be generated.
     */
    public BusinessObject getObject (boolean getObjectForType)
    {
        int type = TypeConstants.TYPE_NOTYPE; // type of object
        String typeCode = null;         // the type code

        // try to get the object with the standard mechanism:
        if (this.p_oid != null)         // oid set?
        {
            this.getObject ();
        } // if oid set

        // check if the object was found and if we shall get the object for
        // the type:
        if (this.p_obj == null && getObjectForType)
        {
            // try to get a dummy object through the object type:
            // get type out of arguments:
            typeCode = this.p_env.getStringParam (BOArguments.ARG_TYPE);
            if (typeCode != null && typeCode.length () > 0)
                                            // parameter exists?
            {
                try
                {
                    // try to convert to integer:
                    type = Integer.parseInt (typeCode);
                } // try
                catch (NumberFormatException e) // no integer?
                {
                    // use the type code to find the type info:
                    type = this.getTypeCache ().getTypeId (typeCode);
                } // catch
                if (type != TypeConstants.TYPE_NOTYPE)
                                        // the type code exists?
                {
                    this.p_obj = this.getNewObject (type);
                } // if the type code exists
            } // if
        } // if

        // return the object:
        return this.p_obj;
    } // getObject


    /**************************************************************************
     * Creates a java object representing the actual business object. <BR/>
     * This method takes the string representation of the oid to get the
     * business object.
     *
     * @param   oidStr  String representation of object id of the required
     *                  business object.
     *
     * @return  The generated object. <BR/>
     *          <CODE>null</CODE> if the object could not be generated.
     */
    public BusinessObject getObject (String oidStr)
    {
        try
        {
            // create the oid and get the corresponding object:
            return this.getObject (new OID (oidStr));
        } // try
        catch (IncorrectOidException e)
        {
            // no valid oid => show corresponding message:
            this.showIncorrectOidMessage (oidStr);
        } // catch
        catch (Exception e)
        {
            // any exception => show a message:
            IOHelpers.showMessage (e, this.p_app, this.p_sess, this.p_env, true);
        } // catch
        return null;                    // return default value
    } // getObject


    /**************************************************************************
     * Creates a java object representing the actual business object. <BR/>
     * When calling this method the parameters are read, too.
     *
     * @param   oid     Object id of the required business object.
     *
     * @return  The generated object. <BR/>
     *          <CODE>null</CODE> if the object could not be generated.
     */
    public BusinessObject getObject (OID oid)
    {
        // call common method:
        return this.getObject (oid, true);
    } // getObject


    /**************************************************************************
     * Creates a java object representing the actual business object. <BR/>
     *
     * @param   oid     Object id of the required business object.
     * @param   getParameters   Shall the parameters be read, too?
     *
     * @return  The generated object. <BR/>
     *          <CODE>null</CODE> if the object could not be generated.
     */
    public BusinessObject getObject (OID oid, boolean getParameters)
    {
        BusinessObject obj = null;      // the business object
        boolean isActual = false;       // load actual object?

        if (oid != null)                // valid oid?
        {
            isActual = oid.equals (this.p_oid);

            // check if the object is already loaded:
            if (isActual && this.p_obj != null)
            {
                // return the object:
                return this.p_obj;
            } // if

            try
            {
/*
long start = System.nanoTime ();
*/
//trace ("KR searching for object " + oid + "...");
//showMessage ("KR searching for object " + oid + "...");
//trace ("KR before fetch: " + oid + ", " + this.getUser () + ", " + this.p_sess + ", " + this.p_env + ".");
                obj = this.getObjectCache ().fetchObject
                    (oid, this.getUser (), this.p_sess, this.p_env, getParameters);
//trace ("KR after fetch.");
//trace ("KR object found: " + obj.name);
//showMessage ("KR object found: " + obj.name);
/*
long end = System.nanoTime ();
IOHelpers.printMessage ("Getting object1 " + oid + ": " + String.format ("%1$10d", new Object[] {Long.valueOf (end - start)}) + " ns.");
*/
/*
BusinessObject3Cache cache = this.p_app.cache3;
try
{
    cache.p_types = new TypeContainer3 (((ObjectPool) this.p_app.cache).getTypeContainer ());
    BusinessObject3 obj3 = null;
    start = System.nanoTime ();
    obj3 = (BusinessObject3) cache.getElem (oid.id, oid);
    end = System.nanoTime ();
    IOHelpers.printMessage ("Getting object3 " + oid + ": " + String.format ("%1$10d", new Object[] {Long.valueOf (end - start)}) + " ns.");
} // try
catch (ListException e)
{
    IOHelpers.showMessage (e, this.p_app, this.p_sess, this.p_env, true);
} // catch
*/
                // set the object's data in the history:
                this.getUserInfo ().history.setObjectData
                    (obj.oid, obj.name, obj.icon);

                // check if the object is for the actual oid:
                if (isActual)
                {
                    // store the object:
                    this.p_obj = obj;
                } // if

            } // try
            catch (ObjectNotFoundException e)
            {
//trace ("KR Object not found: " + obj + ".");
//showMessage ("Object not found: " + obj + ".");
                // check if a real object should be found or a temporary object:
                if (!oid.isTemp ())     // real object?
                {
                    // show corresponding error message:
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_OBJECTNOTFOUND, this.p_env),
                            this.p_app, this.p_sess, this.p_env);
                } // if real object
                else                    // temporary object?
                {
                    // no problem - work as if the object where found:
//trace ("KR no problem");
//showMessage ("KR no problem");
                } // else temporary object
            } // catch
            catch (TypeNotFoundException e)
            {
//trace ("KR Object Type not found for oid " + oid + ".");
//showMessage ("KR Object Type not found for oid " + oid + ".");
                // show corresponding error message:
                IOHelpers.showMessage (e, this.p_app, this.p_sess, this.p_env, false);
            } // catch
            catch (ObjectClassNotFoundException e)
            {
//trace ("KR Object class not found for oid " + oid + ".");
//showMessage ("KR Object class not found for oid " + oid + ".");
                // show corresponding error message:
                IOHelpers.showMessage (e, this.p_app, this.p_sess, this.p_env, false);
            } // catch
            catch (ObjectInitializeException e)
            {
//trace ("KR Object could not be initialized for oid " + oid + ".");
                // show corresponding error message:
                IOHelpers.showMessage (e, this.p_app, this.p_sess, this.p_env, false);
            } // catch
            catch (Exception e)
            {
                IOHelpers.showMessage ("KR Exception within fetchObject", e,
                                       this.p_app, this.p_sess, this.p_env, true);
            } // catch
//trace ("KR hitRate: " + this.getObjectCache ().getHitRate ());
        } // if valid oid
        else                            // no valid oid
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_NOOID, this.p_env),
                    this.p_app, this.p_sess, this.p_env);
        } // else no valid oid

        return obj;                     // return the created object
    } // getObject


    /**************************************************************************
     * Get a new BusinessObject of a certain object type. <BR/>
     *
     * @param   type    Type of the object to instantiate.
     *
     * @return  The new BusinessObject instance or
     *          <CODE>null</CODE> if something went wrong.
     */
    public BusinessObject getNewObject (int type)
    {
        int typeLocal = Type.createTVersionId (type);
        // create new BusinessObject from the type:
        BusinessObject obj = this.getObject (new OID (this.getTypeCache ().
            getType (typeLocal).getTVersionId (), 0));

        return obj;                     // return the found object
    } // getNewObject


    /**************************************************************************
     * Get a new BusinessObject of a certain object type. <BR/>
     * The object type is specified through the unique tpye code.
     *
     * @param   typeCode    The unique type code.
     *
     * @return  The new BusinessObject instance or
     *          <CODE>null</CODE> if something went wrong.
     */
    public BusinessObject getNewObject (String typeCode)
    {
        BusinessObject obj = null;      // the business object

        if (typeCode != null)           // valid type code?
        {
            try
            {
//trace ("KR before fetchNew: " + typeCode + ", " + this.getUser () + ", " + this.p_sess + ", " + this.p_env + ".");
                obj = this.getObjectCache ().fetchNewObject (typeCode, this.getUser (), this.p_sess, this.p_env);
//trace ("KR after fetchNew.");
                // set the object's data in the history:
                this.getUserInfo ().history.setObjectData
                    (obj.oid, obj.name, obj.icon);
            } // try
            catch (TypeNotFoundException e)
            {
//trace ("KR Object Type not found for typeCode " + typeCode + ".");
                // show corresponding error message:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_TYPENOTFOUND, this.p_env),
                        this.p_app, this.p_sess, this.p_env);
            } // catch
            catch (ObjectClassNotFoundException e)
            {
//trace ("KR Object class not found for typeCode " + typeCode + ".");
                // show corresponding error message:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_CLASSNOTFOUND, this.p_env),
                        this.p_app, this.p_sess, this.p_env);
            } // catch
            catch (ObjectInitializeException e)
            {
//trace ("KR Object could not be initialized for typeCode " + typeCode + ".");
                // show corresponding error message:
                IOHelpers.showMessage (e, this.p_app, this.p_sess, this.p_env, false);
            } // catch
            catch (Exception e)
            {
                IOHelpers.showMessage ("KR Exception within fetchNewObject", e, this.p_app, this.p_sess, this.p_env, true);
            } // catch
//trace ("KR hitRate: " + getCache ().getHitRate ());
        } // if valid type
        else                            // no valid type
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_NOOID, this.p_env),
                    this.p_app, this.p_sess, this.p_env);
        } // else no valid oid

        return obj;                     // return the found object
    } // getNewObject


    /**************************************************************************
     * Get the actual user info. <BR/>
     *
     * @return  The user info object.
     */
    public UserInfo getUserInfo ()
    {
        return (UserInfo) this.p_sess.userInfo;
    } // getUserInfo


    /**************************************************************************
     * Set the actual user. <BR/>
     *
     * @param   user    The user object to be set.
     */
    public void setUser (User user)
    {
        this.getUserInfo ().setUser (user);
    } // setUser


    /**************************************************************************
     * Get the actual user. <BR/>
     *
     * @return  The user object.
     */
    public User getUser ()
    {
        return this.getUserInfo ().getUser ();
    } // getUser


    /**************************************************************************
     * Set a new oid. <BR/>
     *
     * @param   oid     The oid to be set.
     */
    public void setOid (OID oid)
    {
        // check if there is a new oid to be set:
        if (this.p_oid == null || !this.p_oid.equals (oid))
        {
            // set the oid and dependent properties:
            this.p_oid = oid;
            this.p_obj = null;
        } // if
    } // setOid


    /**************************************************************************
     * Return true is we have a user set that is successfully logged in. <BR/>
     *
     * @return  true if we have a successfully logged in user already set. <BR/>
     */
    public boolean isUserLoggedIn ()
    {
        // we assume that having a user with an oid set indicated that
        // there is a user successfully logged in. Note that checking
        // via getUser () != null is not sufficient because
        // there is always a user object set!!!
        return this.getUser () != null && this.getUser ().oid != null;
    } // isUserLoggedIn


    /**************************************************************************
     * Resolve an object path, i.e. get the oid defined through the path of
     * an object. <BR/>
     *
     * @param   env     The environment which holds the values.
     * @param   path    The path to be resolved.
     *
     * @return  The oid of the found object or
     *          <CODE>null</CODE> if there was no object found.
     */
    private OID resolveObjectPath (Environment env, String path)
    {
        // check if there is a path to be resolved:
        if (path != null)               // there is a path?
        {
            Workspace_01 wsp = null;

            if (this.getUserInfo () != null &&
                (wsp = this.getUserInfo ().workspace) != null)
                                        // user workspace exists?
            {
                // resolveObjectPathData is a method of the BusinessObject
                // so getting a BusinessObject to do the operation is
                // necessary - since the user has to be logged in now
                // the workspace (which is a BusinessObject) should be
                // available in the session, for that this BO is used
                wsp.setEnv (env);  // zur Sicherheit!
                return BOHelpers.resolveObjectPath (path, wsp, env);
            } // if user workspace exists
        } // if there is a path

        // return dummy result:
        return null;
    } // resolveObjectPath


    /**************************************************************************
     * Show a message that the requested oid is invalid. <BR/>
     *
     * @param   oidStr  String representation of incorrect oid to be shown
     *                  within message. <BR/>
     *                  <CODE>null</CODE>: don't show oid.
     */
    public void showIncorrectOidMessage (String oidStr)
    {
        // show the message to the user:
        if (oidStr == null)             // no oid for message?
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_INCORRECTOID, this.p_env),
                    this.p_app, this.p_sess, this.p_env);
        } // if no oid for message
        else                            // oid shall be shown in message
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_INCORRECTOID, new String[] {oidStr}, this.p_env),
                this.p_app, this.p_sess, this.p_env);
        } // else oid shall be shown in message
    } // showIncorrectOidMessage

} // class FunctionValues

/*
 * Class: ObjectPool.java
 */

// package:
package ibs.bo.cache;

// imports:
import ibs.BaseObject;
//KR TODO: unsauber
import ibs.app.UserInfo;
//KR TODO: unsauber
import ibs.app.func.FunctionHandlerContainer;
import ibs.bo.BOMessages;
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.bo.ObjectClassNotFoundException;
import ibs.bo.ObjectInitializeException;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.cache.CacheException;
import ibs.bo.cache.CacheFullException;
import ibs.bo.tab.TabContainer;
import ibs.bo.type.Type;
import ibs.bo.type.TypeClassNotFoundException;
import ibs.bo.type.TypeConstants;
import ibs.bo.type.TypeContainer;
import ibs.bo.type.TypeNotFoundException;
import ibs.io.Environment;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.service.module.ModuleContainer;
import ibs.service.user.User;
import ibs.util.AlreadyDeletedException;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.list.ListException;

import java.util.Hashtable;
import java.util.Vector;


/******************************************************************************
 * Supports lookup of business objects in a pool of business objects. <BR/>
 *
 * @version     $Id: ObjectPool.java,v 1.30 2010/04/07 13:37:16 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 981016
 ******************************************************************************
 */
public class ObjectPool extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
            "$Id: ObjectPool.java,v 1.30 2010/04/07 13:37:16 rburgermann Exp $";

    /**
     * A vector containing all objects within the pool. <BR/>
     * The class of each element is
     * {@link ibs.bo.BusinessObject BusinessObject}. <BR/>
     * The elements within this vector are sorted by the date of insertion, so
     * that the first element is the oldest one and the last element the
     * newest. <BR/>
     */
    private Vector<BusinessObject> p_objects = null;

    /**
     * The maximum number of elements within this pool. <BR/>
     */
    private int p_maxSize = 0;

    /**
     * The actual position within the pool. <BR/>
     * This position is used with the methods {@link #first first} and
     * {link #next next}. <BR/>
     */
    private int p_actPos = 0;

    /**
     * Holds the actual application info. <BR/>
     */
    private ApplicationInfo p_app = null;

    /**
     * All the types which are possible within the application. <BR/>
     */
    private TypeContainer p_types = null;

    /**
     * Actual number of elements within the pool. <BR/>
     */
    private int p_actSize = 0;

    /**
     * The first element within the pool, i.e. the element which was most
     * recently used. <BR/>
     * This is the object which shall be the last to be removed from the pool.
     */
    private ListElement p_firstElem = null;

    /**
     * The last element within the pool, i.e. the element which was least
     * recently used. <BR/>
     * This is the object which shall be the first to be removed from the pool.
     */
    private ListElement p_lastElem = null;

    /**
     * The load factor which is used to determine the time when to allocate
     * new memory for enlarging the cache.
     */
    private final float CACHELOADFACTOR = (float) 0.9;

    /**
     * The hash table containing the objects. <BR/>
     */
    private Hashtable<OID, ListElement> p_cache = null;

    /**
     * Number of accesses to the cache. <BR/>
     */
    private int p_accesses = 0;

    /**
     * Number of successful accesses to the cache. <BR/>
     */
    private int p_hits = 0;

    /**
     * Stores all tabs which are available throughout the system. <BR/>
     */
    private TabContainer p_tabs = null;

    /**
     * Stores all registered modules. <BR/>
     */
    private ModuleContainer p_modules = null;

    /**
     * Stores all registered, but inactive modules. <BR/>
     */
    private ModuleContainer p_inactiveModules = null;

    /**
     * Stores all function handlers which are available throughout the system.
     * <BR/>
     */
    private FunctionHandlerContainer p_functionHandlers = null;



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Constructor of this class. <BR/>
     *
     * @param   maxSize     The maximum number of elements within this pool.
     * @param   app         The application info object used during
     *                      initialization of business objects.
     *
     * @throws  CacheException
     *          An error occurred during initializing the ObjectPool.
     */
    public ObjectPool (int maxSize, ApplicationInfo app)
        throws CacheException
    {
        this.p_maxSize = maxSize;
        // initialize the vectors:
        this.p_objects = new Vector<BusinessObject> (this.p_maxSize / 5, this.p_maxSize / 10);
        // create the cache:
        this.p_cache = new Hashtable<OID, ListElement> (this.p_maxSize / 5, this.CACHELOADFACTOR);

        // set application info:
        this.p_app = app;

        // initialize the lists:
        try
        {
            this.p_types = new TypeContainer ();
            this.p_tabs = new TabContainer ();
            this.p_modules = new ModuleContainer ();
            this.p_inactiveModules = new ModuleContainer ();
            this.p_functionHandlers = new FunctionHandlerContainer ();
        } // try
        catch (ListException e)
        {
            throw new CacheException (e);
        } // catch

        // ensure that there is a tracer available within the object pool:
        this.openTrace ();
    } // ObjectPool



    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get one new object out of the pool determined through its type. <BR/>
     * A new object is a object having no own id.
     *
     * @param   typeCode The code of the type.
     * @param   user    User for which the object is to be gotten.
     * @param   sess    Session object.
     * @param   env     Environment.
     *
     * @return  The object corresponding to the type code or <CODE>null</CODE>
     *          if not found.
     *
     * @exception   TypeNotFoundException
     *              The type for the object is currently not available.
     * @exception   ObjectClassNotFoundException
     *              The type for the object was found but the corresponding
     *              Java class not.
     * @exception   ObjectInitializeException
     *              Exception during initializing the object.
     *
     */
    public BusinessObject fetchNewObject (String typeCode, User user,
                                          SessionInfo sess, Environment env)
        throws TypeNotFoundException, ObjectClassNotFoundException,
        ObjectInitializeException
    {
        return this.fetchNewObject (typeCode, user, sess, env, true);
    } // fetchNewObject


    /**************************************************************************
     * Get one new object out of the pool determined through its type. <BR/>
     * A new object is a object having no own id.
     *
     * @param   typeCode The code of the type.
     * @param   user    User for which the object is to be gotten.
     * @param   sess    Session object.
     * @param   env     Environment.
     * @param   getParameters   Shall the
     *                  {@link ibs.bo.BusinessObject#getParameters getParameters}
     *                  method be called for the object after its instantiation?
     *
     * @return  The object corresponding to the type code or <CODE>null</CODE>
     *          if not found.
     *
     * @exception   TypeNotFoundException
     *              The type for the object is currently not available.
     * @exception   ObjectClassNotFoundException
     *              The type for the object was found but the corresponding
     *              Java class not.
     * @exception   ObjectInitializeException
     *              Exception during initializing the object.
     *
     */
    public BusinessObject fetchNewObject (String typeCode, User user,
                                          SessionInfo sess, Environment env,
                                          boolean getParameters)
        throws TypeNotFoundException, ObjectClassNotFoundException,
        ObjectInitializeException
    {
        int tVersionId = TypeConstants.TYPE_NOTYPE;
                                        // the version id of the object type
        BusinessObject obj = null;      // the object

        // get the type out of the pool:
        tVersionId = ((ObjectPool) this.p_app.cache).getTypeContainer ().getTVersionId (typeCode);

        // check if the type was found:
        if (tVersionId != TypeConstants.TYPE_NOTYPE) // the type was found?
        {
            try
            {
                // create the oid and get the corresponding object:
                return this.fetchObject (new OID (tVersionId, 0), user, sess, env,
                                     getParameters);
            } // try
            catch (ObjectNotFoundException e)
            {
                // everything o.k., because the object is really not existing
                // => nothing to do
            } // catch
        } // if
        else                            // type not found
        {
            // raise corresponding exception:
            throw new TypeNotFoundException (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_TYPENOTFOUND, new String[] {"code = \'" + typeCode + "\'"}, env));
        } // else type not found

        return obj;                     // return the found object
    } // fetchNewObject


    /**************************************************************************
     * Get one object out of the pool determined through its oid. <BR/>
     *
     * @param   oid     The oid of the object.
     * @param   user    User for which the object is to be gotten.
     * @param   sess    Session object.
     * @param   env     Environment.
     * @param   getParameters   Shall the
     *                  {@link ibs.bo.BusinessObject#getParameters getParameters}
     *                  method be called for the object after its instantiation?
     *
     * @return  The object corresponding to the oid or <CODE>null</CODE>
     *          if not found.
     *
     * @exception   ObjectNotFoundException
     *              The object could neither be found within the pool nor in
     *              the data store.
     * @exception   TypeNotFoundException
     *              The type for the object is currently not available.
     * @exception   ObjectClassNotFoundException
     *              The type for the object was found but the corresponding
     *              Java class not.
     * @exception   ObjectInitializeException
     *              Exception during initializing the object.
     */
    public BusinessObject fetchObject (OID oid, User user, SessionInfo sess,
                                       Environment env, boolean getParameters)
        throws ObjectNotFoundException, TypeNotFoundException,
        ObjectClassNotFoundException, ObjectInitializeException
    {
//openTrace ();
//trace (" before trying to fetch " + oid + "... ");
        BusinessObject obj = null;      // the object

/* BB TODO: for unknown reasons the object cache has been deactivated!
 *          as a side effect a tab object will never been put in the
 *          actual object cache too!
if (oid != null && oid.id != 0) // object has own id?
{
obj = fetch (oid);          // get object out of pool
this.p_accesses++;            // one more access
} // if object has own id
*/


        if (obj == null)                // object not in pool?
        {
//trace (" object not in pool.");
            // get object from data store:

            obj = this.retrieveObject (oid, user, sess, env, getParameters);

//trace (" retrieved Object: " + obj);
            if (obj != null)            // object was found within data store?
            {
//trace (" found Object, oid = " + obj.oid + " ");
                try
                {
                    if (!oid.isTemp ()) // object has own id?
                    {
                        this.add (obj);      // add object to pool
                    } // if object has own id
                } // try
                catch (CacheFullException e)
                {
                    // TODO: handle the exception
                } // catch
                catch (NullPointerException e)
                {
                    // TODO: handle the exception
                } // catch

            } // if object was found within data store
            else                        // object was not found within data store
            {
                throw new ObjectNotFoundException (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_OBJECTNOTFOUND, env));
            } // else object was not found within data store
        } // if object not in pool
        else                            // object already in pool
        {
//trace (" object already in pool.");
            this.p_hits++;                // one more hit

            // set user specific temporary properties:
            obj.user = user;
            obj.sess = sess;
            obj.setEnv (env);       // set environment
        } // else object already in pool
//trace ("fetchObject end.");

        return obj;                     // return the computed object
    } // fetchObject


    /**************************************************************************
     * Create a new instance for a business object. <BR/>
     *
     * @param   oid     The oid of the object.
     * @param   user    User for which the object is to be gotten.
     * @param   sess    Session object.
     * @param   env     Environment.
     *
     * @return  The object corresponding to the oid or <CODE>null</CODE>
     *          if not found.
     *
     * @exception   TypeNotFoundException
     *              The type for the object is currently not available.
     * @exception   ObjectClassNotFoundException
     *              The type for the object was found but the corresponding
     *              Java class not.
     * @exception   ObjectInitializeException
     *              Exception during initializing the object.
     * @exception   ObjectNotFoundException
     *              The required object was not found during search, i.e. there
     *              does not exist any object with the required oid.
     */
    public BusinessObject retrieveObject (OID oid, User user, SessionInfo sess,
                                          Environment env)
        throws TypeNotFoundException, ObjectClassNotFoundException,
        ObjectInitializeException, ObjectNotFoundException
    {
        // call the main method and ensure that getParameters is called for
        // the object:
        return this.retrieveObject (oid, user, sess, env, true);
    } // retrieveObject


    /**************************************************************************
     * Create a new instance for a business object. <BR/>
     *
     * @param   oid     The oid of the object.
     * @param   user    User for which the object is to be gotten.
     * @param   sess    Session object.
     * @param   env     Environment.
     * @param   getParameters   Shall the
     *                  {@link ibs.bo.BusinessObject#getParameters getParameters}
     *                  method be called for the object after its instantiation?
     *
     * @return  The object corresponding to the oid or <CODE>null</CODE>
     *          if not found.
     *
     * @exception   TypeNotFoundException
     *              The type for the object is currently not available.
     * @exception   ObjectClassNotFoundException
     *              The type for the object was found but the corresponding
     *              Java class not.
     * @exception   ObjectInitializeException
     *              Exception during initializing the object.
     * @exception   ObjectNotFoundException
     *              The required object was not found during search, i.e. there
     *              does not exist any object with the required oid.
     */
    public BusinessObject retrieveObject (OID oid, User user, SessionInfo sess,
                                          Environment env, boolean getParameters)
        throws TypeNotFoundException, ObjectClassNotFoundException,
        ObjectInitializeException, ObjectNotFoundException
    {
        //trace ("retrieveObject start");
        BusinessObject obj = null;      // the business object
//        Type type = null;               // the type object
//        String className = null;        // the class name
        UserInfo userInfo = null;


        if (oid != null)                // valid oid?
        {
//trace ("getting object for oid = " + oid);
            // check if session exists:
            if (sess != null && sess.userInfo != null) // session exists?
            {
                userInfo = (UserInfo) sess.userInfo;

                // check if the object is the cached user profile:
                if (userInfo.userProfile != null &&
                    oid.equals (userInfo.userProfile.oid) &&
                    userInfo.userProfile.user.id == user.id)
                                            // user profile found?
                {
                    // take the userProfile object already
                    // stored in the session info:
                    obj = userInfo.userProfile;
                    obj.setEnv (env);       // set environment
                } // if user profile found
                // check if the object is already in the cache:
                else if (userInfo.actObject != null &&
                    oid.equals (userInfo.actObject.oid))
                                            // object was stored?
                {
                    // take the object already
                    // stored in the session info:
                    obj = userInfo.actObject;
                    obj.setEnv (env);       // set environment
                } // if object was stored
            } // if session exists

            // check if the object was already set:
            if (obj == null)            // object not set?
            {
                // retrieve the new business object:
                obj = this.createInstance (oid, user, env, sess);
            } // if object not set

            if (getParameters)          // get the parameters for the object?
            {
                obj.getParameters ();   // get object specific parameters
            } // if get the parameters for the object

            if (!obj.oid.isTemp () && sess != null)
                                        // the object is physical?
            {
// !!! the following statement is just for backwards compatibility:
                userInfo.actObject = obj; // store the actual object
            } // if the object is physical
        } // else if object not loaded, valid oid
        else                            // no valid oid
        {
//            showMessage (BOMessages.MSG_NOOID);
        } // else no valid oid

//trace ("retrieveObject end");
        return obj;                     // return the created object
    } // retrieveObject


    /**************************************************************************
     * Create a new instance for a business object. <BR/>
     *
     * @param   oid     The oid of the object.
     * @param   user    User for which the object is to be gotten.
     * @param   env     Environment.
     * @param   sess    Session object.
     *
     * @return  The object corresponding to the oid or <CODE>null</CODE>
     *          if not found.
     *
     * @exception   TypeNotFoundException
     *              The type for the object is currently not available.
     * @exception   ObjectClassNotFoundException
     *              The type for the object was found but the corresponding
     *              Java class not.
     * @exception   ObjectInitializeException
     *              Exception during initializing the object.
     * @exception   ObjectNotFoundException
     *              The required object was not found during search, i.e. there
     *              does not exist any object with the required oid.
     */
    private BusinessObject createInstance (OID oid, User user, Environment env,
                                           SessionInfo sess)
        throws TypeNotFoundException, ObjectClassNotFoundException,
        ObjectInitializeException, ObjectNotFoundException
    {
//trace ("createInstance start");
        BusinessObject obj = null;      // the business object
        Type type = null;               // the type object
        String className = null;        // the class name


        if (oid != null)                // valid oid?
        {
//trace ("getting object for oid = " + oid);
            // create business object:
            try
            {
//trace ("searching for class " + oid.type);
                // get the type and the class name:
                type = this.getTypeContainer ().getType (oid.tVersionId);
//trace ("type: " + type);

                // check if the type was found and is not locked:
                if (type != null &&
                    (!type.isLocked () || env.isInstantiationAllowed (type.getCode ())))
                                        // the type was found?
                {
                    // get the class object:
                    Class<? extends BusinessObject> c = type.getTypeClass (env);
//trace ("class: " + c);
                    // create the object instance:
                    obj = c.newInstance ();
//trace ("got the object: " + obj);
                } // if the type was found
                else                    // type not found
                {
                    // raise corresponding exception:
                    throw new TypeNotFoundException (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_TYPENOTFOUND, new String[] {"" + oid.tVersionId}, env));
                } // else type not found
            } // try
            catch (TypeClassNotFoundException e)
            {
                // any linkage error.
//trace ("class not found.");
                throw new ObjectClassNotFoundException (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_CLASSNOTFOUND, new String[] {className}, env), e);
            } // catch
            catch (LinkageError e)
            {
                // any linkage error.
//trace ("LinkageError.");
                throw new ObjectClassNotFoundException (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_CLASSNOTFOUND, new String[] {className}, env), e);
            } // catch
            catch (InstantiationException e)
            {
                // instantiation failed.
                // (if the class represents an abstract class, an interface,
                // an array class, a primitive type, or void; or if the
                // instantiation fails for some other reason)
//trace ("InstantiationException: " + e);
                throw new ObjectInitializeException (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_INSTANTIATIONFAILED, new String[] {className}, env), e);
            } // catch
            catch (SecurityException e)
            {
                // there is no permission to create a new instance.
//trace ("IllegalAccessException: " + e);
                throw new ObjectInitializeException (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_INSTANTIATIONFAILED, new String[] {className}, env), e);
            } // catch
            catch (IllegalAccessException e)
            {
                // class or initializer no accessible.
                // trace ("IllegalAccessException: " + e);
                throw new ObjectInitializeException (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_CLASSORINITNOTACCESSIBLE, new String[] {className}, env), e);
            } // catch
            catch (NullPointerException e)
            {
                // any null pointer exception.
                this.trace ("NullPointerException: " + e);
            } // catch
        } // if valid oid
        else // no oid
        {
            throw new ObjectNotFoundException ("No oid in createInstance.");
        } // else no oid

        try
        {
            // initialize the object:
            obj.initialize (type, oid, user, env, sess, this.p_app);
        } // try
        catch (NoAccessException e)
        {
            throw new ObjectInitializeException (e.getMessage (), e);
        } // catch NoAccessException
        catch (AlreadyDeletedException e)
        {
            throw new ObjectInitializeException (e.getMessage (), e);
        } // catch AlreadyDeletedException
        catch (ObjectNotFoundException e)
        {
            throw e;
        } // catch ObjectNotFoundException

//trace ("createInstance end");
        return obj;                     // return the created object
    } // createInstance


    /**************************************************************************
     * Check if an object with a specific oid exists. <BR/>
     *
     * @param   oid     The oid of the object to be checked.
     * @param   user    User for which the object is to be gotten.
     * @param   env     Environment.
     * @param   sess    Session object.
     *
     * @return  <CODE>true</CODE> if the object exists,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean exists (OID oid, User user, Environment env, SessionInfo sess)
    {
        try
        {
            // try to get the object instance and return if it was found:
            return (this.fetchObject (oid, user, sess, env, false)) != null;
        } // try
        catch (ObjectNotFoundException e)
        {
            return false;
        } // catch
        catch (TypeNotFoundException e)
        {
            return false;
        } // catch
        catch (ObjectClassNotFoundException e)
        {
            return false;
        } // catch
        catch (ObjectInitializeException e)
        {
            return false;
        } // catch
    } // exists


    /**************************************************************************
     * Put one object to the pool. <BR/>
     *
     * @param   obj     The object to be put to the pool.
     *
     * @exception   CacheFullException
     *              There is no more space within the cache.
     */
    private void add (BusinessObject obj) throws CacheFullException
    {
        if (this.p_cache.get (obj.oid) == null) // object not in cache yet?
        {
//p_env.write (" object not in cache yet: " + obj.oid);
            if (this.p_actSize == 0)  // the pool ist empty?
            {
                // initialize the list with the new element:
                this.p_firstElem = this.p_lastElem = new ListElement (obj);
            } // if the pool is empty
            else                        // the pool contains at least one elem.
            {
                ListElement elem;       // the actual list element

                if (this.p_actSize >= this.p_maxSize) // the pool is full?
                {
                    // remove least recently used object which is not locked
                    // from list and cache:
                    // search for the least recently not locked element:
                    elem = this.p_lastElem; // get the last element
                    while (elem != null && elem.isLocked)
                    {
                        elem = elem.prev;
                    } // while
                    if (elem == null)   // there is no unlocked element?
                    {
                        throw new CacheFullException ("");
                    } // if there is no unlocked element

                    // there was an unlocked element found:
                    if (elem == this.p_lastElem) // it is the last element?
                    {
                        this.p_lastElem = elem.prev; // set new last element
                    } // if it is the last element
                    else if (elem.next != null) // there is a next element?
                    {
                        elem.next.prev = elem.prev; // set its previous element
                    } // else if there is a next element
                    elem.prev.next = elem.next; // no next element
                    this.p_cache.remove (elem.key); // remove it from cache

                    // set the new content of the element:
                    elem.reInit (obj);

                    // set actual number of elements within the pool:
                    this.p_actSize--;     // one element less within pool
                } // if the pool is full
                else                    // there is enough place in the pool
                {
                    // create new list element:
                    elem = new ListElement (obj);
                } // else there is enough place in the pool

                // set element data for first element:
                elem.next = this.p_firstElem;
                elem.prev = null;
                if (this.p_firstElem != null && this.p_firstElem != elem)
                                        // there was a first element?
                {
                    this.p_firstElem.prev = elem; // the previous is the actual one
                } // if there was a first element
                this.p_firstElem = elem;  // the element is the new first one
            } // else the pool contains at least one elem.

            // put the most recently used object in the cache:
            this.p_cache.put (obj.oid, this.p_firstElem);
            this.p_actSize++;             // one element more within pool
        } // if object not in cache yet
        else                            // object already in cache
        {
//p_env.write (" object already in cache: " + obj.oid);
            // add shoudln't be called when the object is already in the cache.
            // Since that has happened, do a fetch so that the object becomes
            // the most recently used:
            this.fetch (obj.oid);
        } // else object already in cache
    } // add


    /**************************************************************************
     * Get one object out of the pool determined through its oid. <BR/>
     *
     * @param   oid     The oid of the object.
     *
     * @return  The object corresponding to the oid or <CODE>null</CODE>
     *          if not found.
     */
    private BusinessObject fetch (OID oid)
    {
        ListElement elem = this.p_cache.get (oid);
                                        // get element from cache

        if (elem != null)               // found element?
        {
            if (this.p_firstElem != elem) // element is not the first one?
            {
                // get element out of the list:
                if (elem.prev != null)  // there exists a previous element?
                {
                    elem.prev.next = elem.next; // set next element from
                                        // actual element
                } // if there exists a previous element
                if (elem.next != null)  // there exists a next element?
                {
                    elem.next.prev = elem.prev; // set previous element form
                                        // actual element
                } // if there exists a next element

                // set element as first one:
                elem.prev = null;       // first element has no previous elem.
                elem.next = this.p_firstElem;  // old first element is next one
                if (this.p_firstElem != null && this.p_firstElem != elem)
                                        // there was a first element?
                {
                    this.p_firstElem.prev = elem; // the previous is the actual one
                } // if there was a first element
                this.p_firstElem = elem;  // the element is the new first one
            } // if element is not the first one

            return elem.obj;            // return the computed object
        } // if found element

        return null;                    // return default value
    } // fetch


    /**************************************************************************
     * Get the hit rate. <BR/>
     * The hit rate is the result of dividing the number of hits by the number
     * of accesses.
     *
     * @return  The hit rate.
     */
    public double getHitRate ()
    {
        if (this.p_accesses > 0)          // at least one access?
        {
            // compute the hit rate and return it:
            return ((double) this.p_hits) / ((double) this.p_accesses);
        } // if at least one access

        // no accesses yet
        return 0;                       // return default value
    } // getHitRate


    /**************************************************************************
     * Get the number of elements within the pool. <BR/>
     *
     * @return  The number of elements within the pool.
     */
    public int size ()
    {
        return this.p_objects.size ();
    } // size


    /**************************************************************************
     * Check an element out. <BR/>
     *
     * @param   oid     The oid of the element to be checked out.
     */
    public void checkOut (OID oid)
    {
        // TODO: currently not implemented
    } // checkOut


//## single user cache ... ####################################################
    /**************************************************************************
     * Get one object out of the pool determined through its oid. <BR/>
     *
     * @param   oid     The oid of the object.
     *
     * @return  The object corresponding to the oid or <CODE>null</CODE>
     *          if not found.
     */
    public BusinessObject get (OID oid)
    {
        int pos;                        // the position of the searched object
        // search for the object:
        pos = 0;
        while (pos < this.p_objects.size () &&
               !oid.equals (this.p_objects.elementAt (pos).oid))
        {
            pos++;
        } // while pos

        if (pos < this.p_objects.size ()) // the object was found?
        {
            BusinessObject obj;

            // set the object as newest one:
            obj = this.p_objects.elementAt (pos);
                                        // store element
            this.p_objects.removeElementAt (pos); // remove it
            this.p_objects.addElement (obj); // add it at last position

            return obj;
        } // if the object was found

        // object not found?
        return null;                    // return corresponding value
    } // get


    /**************************************************************************
     * Put one object to the pool. <BR/>
     *
     * @param   value   The object to be put to the pool.
     */
    public void put (BusinessObject value)
    {
        int pos = 0;                    // the position of the object within
                                        // the pool

        // Search for an object with the same oid within the pool:
        if ((pos = this.getPos (value.oid)) >= 0) // there is alreday an object with
                                        // this oid within the pool
        {
            // replace the old object with the new one:
            this.p_objects.setElementAt (value, pos);
        } // if there is alreday an object with this oid within the pool
        else                            // the object is new to the pool
        {
            while (this.p_objects.size () >= this.p_maxSize)
                                        // the vector is full?
            {
                // remove the oldest element:
                this.removeOldest ();
            } // while the vector is full

            // add the new object to the pool:
            this.p_objects.addElement (value);
        } // else the object is new to the pool
    } // put


    /**************************************************************************
     * Get the position of one element within the pool. <BR/>
     *
     * @param   oid     The oid of the object.
     *
     * @return  The position of the object corresponding to the oid or
     *          <CODE>-1</CODE> if not found.
     */
    public int getPos (OID oid)
    {
        int pos;                        // the position of the searched object
        // search for the object:
        pos = 0;
        while (pos < this.p_objects.size () &&
               !oid.equals (this.p_objects.elementAt (pos).oid))
        {
            pos++;
        } // while pos

        if (pos < this.p_objects.size ())
        {
            return pos;
        } // if

        // object not found?
        return -1;                      // return corresponding value
    } // getPos


    /**************************************************************************
     * Set the object as the newest one. <BR/>
     * If the object is not found the pool stays unchanged. <BR/>
     *
     * @param   obj     The object to be the newest one.
     */
    public void setNewest (BusinessObject obj)
    {
        int pos = 0;                    // the position of the object within
                                        // the pool

        // Search for an object with the same oid within the pool:
        if ((pos = this.getPos (obj.oid)) >= 0) // there is alreday an object with
                                        // this oid within the pool
        {
            this.p_objects.removeElementAt (pos); // remove the object
            this.p_objects.addElement (obj); // add the new object at last position
        } // if there is alreday an object with this oid within the pool
    } // setNewest



    /**************************************************************************
     * Removes the object from the pool. <BR/>
     *
     * @param   oid     The oid of the object to be removed.
     *
     * @return  The value to which the key had been mapped in this pool,
     *          or <CODE>null</CODE> if the oid did not have a mapping.
     */
    public BusinessObject remove (OID oid)
    {
        BusinessObject obj = this.get (oid); // store the object
        int pos = this.getPos (oid);         // get the position

        if (pos >= 0)                   // the object was found?
        {
            obj = this.p_objects.elementAt (pos);
                                        // store the object
            this.p_objects.removeElementAt (pos); // remove the object
        } // if the object was found

        return obj;                     // return the stored object
    } // remove

    /**************************************************************************
     * Removes all objects from the pool. <BR/>
     */
    public void removeAll ()
    {
        if (this.p_objects != null)
        {
            this.p_objects.removeAllElements (); // remove the object
        } // if
    } // remove

    /**************************************************************************
     * Removes the oldest object from the pool. <BR/>
     */
    private void removeOldest ()
    {
        if (this.p_objects.size () >= 1) // there are elements within the pool?
        {
            // remove the element from the pool:
            this.p_objects.removeElementAt (0);
        } // if there are elements within the pool
    } // removeOldest


    /**************************************************************************
     * Get the first object out of the pool. <BR/>
     * This is the oldest object.
     *
     * @return  The first object within the pool.
     */
    public BusinessObject first ()
    {
        if (this.p_objects.size () >= 1)  // there are elements within the pool?
        {
            // get the position of the first element:
            this.p_actPos = this.p_objects.size () - 1; // set actual position
            // get the element from the pool:
            return this.p_objects.elementAt (this.p_actPos);
        } // if there are elements within the pool

        // object not found?
        return null;                    // return corresponding value
    } // first


    /**************************************************************************
     * Get the next object out of the pool. <BR/>
     * This is the object next created after the last one got with this method
     * or with {@link #first first}. <BR/>
     *
     * @return  The next object within the pool.
     */
    public BusinessObject next ()
    {
        if (this.p_actPos > 0 && this.p_objects.size () > (this.p_actPos + 1))
                                        // there are more elements
                                        // within the pool?
        {
            this.p_actPos--;              // set actual position
            // get the element from the pool:
            return this.p_objects.elementAt (this.p_actPos);
        } // if there are more elements within the pool

        // object not found?
        return null;                    // return corresponding value
    } // next


    /**************************************************************************
     * Get the last object out of the pool. <BR/>
     * This is the newest object.
     *
     * @return  The last object within the pool.
     */
    public BusinessObject last ()
    {
        if (this.p_objects.size () >= 1)  // there are elements within the pool?
        {
            this.p_actPos = 0;            // set actual position
            // get the element from the pool:
            return this.p_objects.elementAt (this.p_actPos);
        } // if there are elements within the pool

        // object not found?
        return null;                    // return corresponding value
    } // last


    /**********************************************************************
     * Returns the TypeContainer object with all valid types. <BR/>
     *
     * @return      The TypeContainer object of the ObjectPool.
     */
    public TypeContainer getTypeContainer ()
    {
        return ((ObjectPool) this.p_app.cache).p_types;
    } // getTypeContainer


    /**********************************************************************
     * Returns the TabContainer object with all valid tabs. <BR/>
     *
     * @return      The TabContainer object of the ObjectPool.
     */
    public TabContainer getTabContainer ()
    {
        return ((ObjectPool) this.p_app.cache).p_tabs;
    } // getTypeContainer


    /**********************************************************************
     * Returns the FunctionHandlerContainer object with all valid function
     * handlers. <BR/>
     *
     * @return      The FunctionHandlerContainer object of the ObjectPool.
     */
    public FunctionHandlerContainer getFunctionHandlerContainer ()
    {
        return ((ObjectPool) this.p_app.cache).p_functionHandlers;
    } // getFunctionHandlerContainer


    /**********************************************************************
     * Returns the ModuleContainer object with all valid modules. <BR/>
     *
     * @return      The ModuleContainer object of the ObjectPool.
     */
    public ModuleContainer getModuleContainer ()
    {
        return ((ObjectPool) this.p_app.cache).p_modules;
    } // getModuleContainer


    /**********************************************************************
     * Returns the ModuleContainer object with all inactive modules. <BR/>
     *
     * @return      The inactive ModuleContainer object of the ObjectPool.
     */
    public ModuleContainer getInactiveModuleContainer ()
    {
        return ((ObjectPool) this.p_app.cache).p_inactiveModules;
    } // getInactiveModuleContainer


//## ... single user cache ####################################################



    /**************************************************************************
     * Implements elements of lists containing business objects. <BR/>
     *
     * @version     $Id: ObjectPool.java,v 1.30 2010/04/07 13:37:16 rburgermann Exp $
     *
     * @author      Klaus Reimüller (KR), 990115
     **************************************************************************
     */
    private class ListElement
    {
        /**
         * The content of the element.
         */
        BusinessObject obj = null;
        /**
         * The key of the element.
         */
        Object key = null;
        /**
         * Is the object locked?
         */
        boolean isLocked = false;
        /**
         * User who locked the object
         */
        int lockingUser = -1;
        /**
         * The previous element within list.
         */
        ListElement prev = null;
        /**
         * The next element within list.
         */
        ListElement next = null;


        /**********************************************************************
         * Constructor of this class. <BR/>
         *
         * @param   obj     The object to be stored as content of this element.
         */
        protected ListElement (BusinessObject obj)
        {
            this.obj = obj;             // set the object
            this.key = obj.oid;
        } // ListElement


        /**********************************************************************
         * Re-initialize the ListElement with a new object. <BR/>
         *
         * @param   obj     The object to be stored as content of this element.
         */
        protected void reInit (BusinessObject obj)
        {
            // set the element's properties:
            this.obj = obj;             // set the object
            this.key = obj.oid;
            this.isLocked = false;
            this.lockingUser = -1;
            this.prev = null;
            this.next = null;
        } // reInit


        /**********************************************************************
         * Lock the object. <BR/>
         * This method checks if the object is already locked. If not it is locked
         * by this user. After this the method returns if the user has locked the
         * object. The return value is not only true if the object was locked now,
         * but also if the object was already locked by this user.
         *
         * @param   userId      Id of the user who wants to lock the object.
         *
         * @return  <CODE>true</CODE> if the object is locked by the user,
         *          <CODE>false</CODE> otherwise.
         */
        protected boolean lock (int userId)
        {
            if (!this.isLocked)         // object not locked?
            {
                this.isLocked = true;   // lock the object
                this.lockingUser = userId; // set user who locked the object
            } // if object not locked

            // return if the user has locked the object:
            return this.isLocked && this.lockingUser == userId;
        } // lock


        /**********************************************************************
         * Lock the object. <BR/>
         * This method checks if the object is already locked. If not it is locked
         * by this user. After this the method returns if the user has locked the
         * object. The return value is not only true if the object was locked now,
         * but also if the object was already locked by this user.
         *
         * @return  <CODE>true</CODE> if the object is locked by the user,
         *          <CODE>false</CODE> otherwise.
         */
        protected boolean unlock ()
        {
            if (this.isLocked)          // object is locked?
            {
                this.isLocked = false;  // unlock the object
                this.lockingUser = -1;  // no user locked the object
            } // if object is locked

            // return if the object is unlocked:
            return !this.isLocked;
        } // unlock
    } // class ListElement

} // class ObjectPool

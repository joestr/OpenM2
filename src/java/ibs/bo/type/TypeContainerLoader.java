/*
 * Class: TypeContainerLoader.java
 */
/*
 * Created by IntelliJ IDEA.
 * User: kreimueller
 * Date: Dec 11, 2001
 * Time: 3:55:04 PM
 */

// package:
package ibs.bo.type;

// imports:
import ibs.bo.OID;
import ibs.bo.ObjectClassNotFoundException;
import ibs.bo.ObjectInitializeException;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.cache.ObjectPool;
import ibs.io.Environment;
import ibs.io.servlet.IApplicationContext;
import ibs.obj.ml.Locale_01;
import ibs.service.list.SQLElementContainerLoader;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.util.list.ListException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Enumeration;


/******************************************************************************
 * This class is responsible for loading the data for the type container out
 * of the data store. <BR/>
 * The data is loaded into the type container which can be retrieved through
 * {@link ibs.bo.cache.ObjectPool#getTypeContainer ()
 * ibs.bo.cache.ObjectPool.getTypeContainer ()}.
 *
 * @version     $Id: TypeContainerLoader.java,v 1.27 2010/04/29 15:26:33 btatzmann Exp $
 *
 * @author      kreimueller, 011211
 ******************************************************************************
 */
public class TypeContainerLoader
    extends SQLElementContainerLoader<TypeContainer, Type>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TypeContainerLoader.java,v 1.27 2010/04/29 15:26:33 btatzmann Exp $";


    /**
     * The current type to be considered for loading. <BR/>
     * If this property is set to a value != <CODE>0</CODE> there are just
     * the data loaded which belong to this type including its dependencies.
     * Otherwise the data of all types are loaded.
     */
    private int p_tVersionId = 0;


    /**
     * The object cache which contains the type templates. <BR/>
     */
    private ObjectPool p_objectCache = null;

    /**
     * The application context. <BR/>
     */
    protected IApplicationContext p_context = null;


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a TypeContainerLoader object. <BR/>
     *
     * @param   container   The container in which to load the information.
     * @param   objectCache The cache which contains the business objects with
     *                      the type templates.
     * @param   context     The application context.
     */
    public TypeContainerLoader (TypeContainer container, ObjectPool objectCache, IApplicationContext context)
    {
        // call constructor of super class:
        super (container);

        // set properties:
        this.p_objectCache = objectCache;
        this.p_context = context;
    } // TypeContainerLoader


    ///////////////////////////////////////////////////////////////////////////
    // methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Create the query to retrieve the data from the database. <BR/>
     *
     * @return  The constructed query.
     */
    protected final String createQuery ()
    {
        StringBuffer query = new StringBuffer (); // the query to be executed

        // get all versions of all types:
        query.append (" SELECT typeId, typeName, typeCode, id, className, oid")
            .append (", superTVersionId, icon")
            .append (" FROM v_TVersion$content");

        if (this.p_tVersionId != 0) // restrict the query just to one type?
        {
            query.append (" WHERE id = ").append (this.p_tVersionId);
        } // if restrict the query just to one type

        // first the base types and then the xml-types have to be registered
        if (SQLConstants.DB_TYPE == SQLConstants.DB_MSSQL)
        {
            // in mssql the oid of the base types is null:
            query.append (" ORDER BY oid ASC");
        } // if
        else if (SQLConstants.DB_TYPE == SQLConstants.DB_ORACLE ||
                 SQLConstants.DB_TYPE == SQLConstants.DB_DB2)
        {
            // in oracle and db2 the oid of the base types is emptystring:
            query.append (" ORDER BY oid DESC");
        } // else if

        // return the computed query:
        return query.toString ();
    } // createQuery


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This method is used to get all attributes of one element out of the
     * result set. The attribute names which can be used are the ones which
     * are defined within the resultset of {@link #createQuery createQuery}.
     * <BR/>
     * <B>Format:</B>. <BR/>
     * for oid properties:
     *      &lt;variable&gt; = getQuOidValue (action, "&lt;attribute&gt;");. <BR/>
     * for other properties:
     *      &lt;variable&gt; = action.get&lt;type&gt; ("&lt;attribute&gt;");. <BR/>
     *
     * @param   action  The database object used for getting the tuple values.
     *
     * @return  The newly created element filled with the values out of the
     *          actual tuple.
     *
     * @throws  ibs.tech.sql.DBError
     *          Error when executing database statement.
     * @throws  TypeClassNotFoundException
     *          The class was not found or could not be instantiated.
     */
    protected final Type parseElement (SQLAction action)
        throws DBError, TypeClassNotFoundException
    {
        // add a new type version in the application cache:
        String typeName = action.getString ("typeName");
        String typeCode = action.getString ("typeCode");
        int id = action.getInt ("id");
        String className = action.getString ("className");
        OID typeOid = SQLHelpers.getQuOidValue (action, "oid");
        int superTVersionId = action.getInt ("superTVersionId");
        Type type;                      // the type object
        String icon = action.getString ("icon");

        try
        {
            // create the type object:
            type = new Type (id, typeName, className);
        } // try
        catch (TypeClassNotFoundException e)
        {
            // throw the exception to the caller:
            throw e;
        } // catch

        // set the other type properties:
        type.setCode (typeCode);
        type.setSuperTVersionId (superTVersionId);
        try
        {
            type.setSubTypes (new TypeContainer ());
        } // try
        catch (ListException e)
        {
            // nothing to do
        } // catch

        // check if a valid icon is set:
        if (icon == null || icon.length () == 0) // icon not valid?
        {
            // define standard icon:
            icon = typeCode + ".gif";
        } // if icon not valid
        type.setIcon (icon);

        // check if the type is represented as an object:
        if (typeOid != null)        // the type is represented as an object?
        {
            try
            {
                // get the document template business object belonging to the
                // type:
                type.setTemplate ((ITemplate) this.p_objectCache.fetchObject (
                    typeOid, this.p_context.getUser (),
                    this.p_context.getSess (), this.p_context.getEnv (), false));

                // check if document template is set
                if (type.getTemplate () != null)
                {
                    // get template:
                    ITemplate template = type.getTemplate ();

                    // check if we got a template and no template file is set:
                    if (template == null ||
                        (!template.isLoaded () && !template.isLoadable ()))
                    {
                        // log error
                        System.out.println ("Error: Template for type '" +
                            typeCode + "' cannot be loaded because some " +
                            "necessary resources are not available.");
                    } // if
                } // if
            } // try
            catch (ObjectNotFoundException e)
            {
                System.out.println ("Type object not found for type " +
                                    typeCode + ": " + e.toString ());
//trace ("KR Object not found: " + obj + ".");
//showMessage ("Object not found: " + obj + ".");
                // show corresponding error message:
//                showMessage (BOMessages.MSG_OBJECTNOTFOUND);
            } // catch
            catch (TypeNotFoundException e)
            {
                System.out.println ("Type DocumentTemplate not found when loading type " +
                                    typeCode + ": " + e.toString ());
//trace ("KR Object Type not found for oid " + oid + ".");
//showMessage ("KR Object Type not found for oid " + oid + ".");
                // show corresponding error message:
//                showMessage (e.getMessage ());
            } // catch
            catch (ObjectClassNotFoundException e)
            {
                System.out.println ("Object class not found for type " +
                                    typeCode + ": " + e.toString ());
//trace ("KR Object class not found for type with oid " + typeOid + ".");
//showMessage ("KR Object class not found for oid " + oid + ".");
                // show corresponding error message:
//                showMessage (e.getMessage ());
            } // catch
            catch (ObjectInitializeException e)
            {
                System.out.println ("Object initialize exception for type " +
                                    typeCode + ": " + e.toString ());
//trace ("KR Object could not be initialized for oid " + typeOid + ".");
                // show corresponding error message:
//                showMessage (e.getMessage ());
            } // catch
            catch (Exception e)
            {
//                showMessage ("KR Exception within fetchObject: " + e + "." + IE302.TAG_NEWLINE);
                System.out.println ("Exception for type " + typeCode + ": ");
                ByteArrayOutputStream out = new ByteArrayOutputStream ();
                PrintStream stream = new PrintStream (out);
                e.printStackTrace (stream);
                System.out.println (out.toString ());
//                showMessage (out.toString ());
            } // catch
        } // if the type is represented as an object

/*
openTrace ();
trace ("added type: " + typeCode + " (" + className + ") => " +
   getCache ().typesSize () + " types registered.");
*/

        // return the type:
        return type;
    } // parseElement


    /**************************************************************************
     * Perform post processing for the container. <BR/>
     * This method is called after loading the several elements. <BR/>
     * All types are linked together through their type/subtype relations.
     *
     * @param   elems   The container.
     */
    protected void postProcess (TypeContainer elems)
    {
        Type superType = null;          // the actual super type

        // loop through all elements of the container:
        for (Enumeration<Type> types = elems.elements (); types.hasMoreElements ();)
        {
            // get the actual element:
            Type type = types.nextElement ();

            if ((superType = elems.get (type.getSuperTVersionId ())) != null)
                // found super type?
            {
                // add the actual type as sub type to the super type:
                superType.addSubType (type);
            } // if found super type
        } // for
    } // postProcess


    /***************************************************************************
     * Loads the multilang type info for all provided type container
     * elements and provided locales. <BR/>
     *
     * @param   elems    The type container elements
     * @param   locales  The locales to init the multilang info for
     * @param   env      The environment
     */
    protected void performLoadMultilangInfo (TypeContainer elems,
            Collection<Locale_01> locales, Environment env)
    {
        // loop through all elements of the container:
        for (Enumeration<Type> types = elems.elements (); types.hasMoreElements ();)
        {
            // get the actual element:
            Type type = types.nextElement ();
            
            // load the multilang info:
            type.loadMultilangInfo (locales, env);
        } // for

        // loop through all elements of the container again for setting
        // multilang info for references like reference fields, containers, ...:
        for (Enumeration<Type> types = elems.elements (); types.hasMoreElements ();)
        {
            // get the actual element:
            Type type = types.nextElement ();

            // load the multilang container info:
            type.loadMultilangReferenceInfo (elems, locales, env);
        } // for
    } // performLoadMultilangInfo

} // class TypeContainerLoader

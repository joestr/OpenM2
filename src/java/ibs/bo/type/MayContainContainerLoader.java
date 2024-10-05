/*
 * Class: MayContainContainerLoader.java
 */
/*
 * Created by IntelliJ IDEA.
 * User: kreimueller
 * Date: Dec 11, 2001
 * Time: 3:55:14 PM
 */

// package:
package ibs.bo.type;

// imports:
import ibs.service.list.SQLElementContainerLoader;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.util.list.ListException;

import java.util.Enumeration;
import java.util.Vector;


/******************************************************************************
 * This class is responsible for loading the data for all may contain containers
 * out of the data store. <BR/>
 * The data is loaded into the types within the type container which can be
 * retrieved through {@link ibs.bo.cache.ObjectPool#getTypeContainer ()
 * ibs.bo.cache.ObjectPool.getTypeContainer ()}.
 *
 * @version     $Id: MayContainContainerLoader.java,v 1.11 2007/07/31 19:13:52 kreimueller Exp $
 *
 * @author      kreimueller, 011122
 ******************************************************************************
 */
public class MayContainContainerLoader
    extends SQLElementContainerLoader<TypeContainer, Type>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MayContainContainerLoader.java,v 1.11 2007/07/31 19:13:52 kreimueller Exp $";


    /**************************************************************************
     * This inner class holds the mayContain info for one type. <BR/>
     *
     * @version     $Id: MayContainContainerLoader.java,v 1.11 2007/07/31 19:13:52 kreimueller Exp $
     **************************************************************************
     */
    class _Entry
    {
        /**
         * tVersionId of major type.
         */
        int p_majorTVersionId;
        /**
         * Class name of major type.
         */
        String p_majorClassName;
        /**
         * tVersionId of minor type.
         */
        int p_minorTVersionId;
        /**
         * Name of minor type.
         */
        String p_minorName;
        /**
         * The object type.
         */
        Type p_type;


        /**********************************************************************
         * Returns the string representation of this object. <BR/>
         * The type ids and type names are concatenated to create a string
         * representation according to
         * "majorTVersionId (majorClassName) -> minorTVersionId (minorName)".
         *
         * @return  String represention of the object.
         */
        public String toString ()
        {
            String theString = "";          // the string

            // compute the string:
            theString += "" +
                this.p_majorTVersionId + " (" + this.p_majorClassName + ") -> " +
                this.p_minorTVersionId + " (" + this.p_minorName + ")";
            // return the computed string:
            return theString;
        } // toString
    } // class _Entry


    /**
     * Vector used for storing type entries temporarily. <BR/>
     * Each of these elements must be of class
     * {@link ibs.bo.MayContainContainerLoader#_Entry
     * MayContainContainerLoader._Entry}.
     */
    private Vector<MayContainContainerLoader._Entry> p_mayContainElems = null;

    /**
     * The current type to be considered for loading. <BR/>
     * If this property is set to a value != <CODE>0</CODE> there are just
     * the data loaded which belong to this type including its dependencies.
     * Otherwise the data of all types are loaded.
     */
    private int p_tVersionId = 0;


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a MayContainContainerLoader object. <BR/>
     * Calls the constructor of the super class. <P>
     * The private property p_mayContainElems is initialized to a new
     * Vector. <BR/>
     *
     * @param   container   The container in which to load the information.
     */
    public MayContainContainerLoader (TypeContainer container)
    {
        // call constructor of super class:
        super (container);

        // initialize other properties:
        this.p_mayContainElems = new Vector<MayContainContainerLoader._Entry> ();
    } // MayContainContainerLoader


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

        // get all may contain relationships between types:
        query.append ("SELECT  majorClassName, majorTVersionId,")
            .append (" minorTVersionId, minorName")
            .append (" FROM v_MayContain$content");

        if (this.p_tVersionId != 0) // restrict the query just to one type?
        {
            query.append (" WHERE majorTVersionId = ").append (this.p_tVersionId)
                .append (" OR minorTVersionId = ").append (this.p_tVersionId);
        } // if restrict the query just to one type

        query.append (" ORDER BY majorTVersionId, minorName, minorTVersionId");

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
     * @throws  DBError
     *          Error when executing database statement.
     * @throws  TypeClassNotFoundException
     *          The class was not found or could not be instantiated.
     */
    protected final Type parseElement (SQLAction action)
        throws DBError, TypeClassNotFoundException
    {
        _Entry entry;                   // entry for actual type

        entry = new _Entry ();      // create the new entry

        // get different attribute values from the SQLAction object:
        entry.p_majorClassName = action.getString ("majorClassName");
        entry.p_majorTVersionId = action.getInt ("majorTVersionId");
        entry.p_minorTVersionId = action.getInt ("minorTVersionId");
        entry.p_minorName = action.getString ("minorName");

        try
        {
            // create a new type object:
            entry.p_type = new Type (entry.p_minorTVersionId,
                entry.p_minorName, entry.p_majorClassName);
            entry.p_type.setSubTypes (new TypeContainer ());

            // add the temporary entry to the elements vector:
            this.p_mayContainElems.addElement (entry);
        } // try
        catch (ListException e)
        {
            // nothing to do
        } // catch
        catch (TypeClassNotFoundException e)
        {
            // throw the exception to the caller:
            throw e;
        } // catch

        // return default value:
        return null;
    } // parseElement


    /**************************************************************************
     * Perform post processing for the container. <BR/>
     * This method is called after loading the several elements. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     *
     * @param   elems   The container.
     */
    protected void postProcess (TypeContainer elems)
    {
        // initialize the type arrays:
        this.initializeTypeArrays (elems);
    } // postProcess


    /***************************************************************************
     * This function sets the typeIds and typeNames for each of the
     * BusinessObject classes that may contain different other business
     * objects. <BR/>
     *
     * @param   allTypes    The type container.
     */
    private final void initializeTypeArrays (TypeContainer allTypes)
    {
        _Entry entry = null;            // the actual type entry
        TypeContainer types = null;     // the data of all types one business
                                        // object type may contain
        Type objectType = null;         // the actual object type
        int tVersionId = 0;             // the actual type version id

        try
        {
            // read the entries stored in the elements Vector:
            for (Enumeration<MayContainContainerLoader._Entry> elems = this.p_mayContainElems.elements (); elems.hasMoreElements ();)
            {
                // get the current entry:
                entry = elems.nextElement ();

                // check if the tVersionId is the same as for the last entry:
                if (tVersionId == entry.p_majorTVersionId) // same tVersionId?
                {
                    // add the type to the actual types pool:
                    types.add (entry.p_type);
                } // if same tVersionId
                else                    // other tVersionId
                {
                    if (types != null)  // there was another types pool before?
                    {
                        // create the arrays:
                        types.createArrays ();

                        // get the type object out of the type container:
                        objectType = allTypes.get (tVersionId);
                        // check if the type was found:
                        if (objectType != null) // the type was found?
                        {
                            // set the may contain entries for the object type:
                            objectType.setMayContainTypes (types);
                        } // if the type was found
                        else
                        {
                            // nothing to do
                        } // else
                    } // if there was another pool value before

                    // get the type version id of the current entry:
                    tVersionId = entry.p_majorTVersionId;

                    // initialize the type vector for this tVersionId:
                    types = new TypeContainer ();

                    // add the type to the types pool:
                    types.add (entry.p_type);
                } // else other tVersionId
            } // for
        } // try
        catch (ListException e)
        {
            // nothing to do
        } // catch
    } // initializeTypeArrays

} // class MayContainContainerLoader

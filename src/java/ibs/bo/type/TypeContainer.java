/*
 * Class: TypeContainer.java
 */

// package:
package ibs.bo.type;

// imports:
import ibs.bo.BOMessages;
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.ml.Locale_01;
import ibs.util.list.ElementContainer;
import ibs.util.list.ListException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;


/******************************************************************************
 * This class contains all data regarding a type within another type. <BR/>
 * If the types have to be accessed through an ids and a names array this may
 * be done after calling the computaion method
 * {@link #createArrays createArrays}.
 *
 * @version     $Id: TypeContainer.java,v 1.18 2010/04/22 11:38:02 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 001017
 ******************************************************************************
 */
public class TypeContainer extends ElementContainer<Type>
    implements ITypeContainer<Type>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TypeContainer.java,v 1.18 2010/04/22 11:38:02 btatzmann Exp $";


    /**
     * The elements of the container. <BR/>
     * Each of these elements must be of class Type.
     *
     * @see ibs.bo.Type
     */
//    private Vector p_elems = null;

    /**
     * The ids of the types as String representation. <BR/>
     * This array must be explicitly created.
     *
     * @see #createArrays
     */
    public String[] ids = null;

    /**
     * The names of the types. <BR/>
     * This array must be explicitly created.
     *
     * @see #createArrays
     */
    public String[] names = null;

    /**
     * The code names of the types as String representation. <BR/>
     * This array must be explicitly created.
     *
     * @see #createArrays
     */
    public String[] codes = null;

    /**
     * The ml names of the types. <BR/>
     * This array must be explicitly created.
     *
     * @see #createArrays
     */
    public Map<String, String[]> mlNames = null;

    /**
     * The ml descriptions of the types. <BR/>
     * This array must be explicitly created.
     *
     * @see #createArrays
     */
    public Map<String, String[]> mlDescriptions = null;
    
    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a TypeContainer object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     * {@link #ids ids} is initialized to null. <BR/>
     * {@link #names names} is initialized to null. <BR/>
     * {@link #codes codes} is initialized to null. <BR/>
     *
     * @throws  ListException
     *          An error occurred during initializing the container.
     */
    public TypeContainer ()
        throws ListException
    {
        // call constructor of super class:
        super ();

        // initialize the instance's properties:
        this.ids = null;
        this.names = null;
        this.codes = null;
    } // TypeContainer


    ///////////////////////////////////////////////////////////////////////////
    // other methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Initialize the element class. <BR/>
     * This method shall be overwritten in sub classes.
     *
     * @throws  ListException
     *          The class could not be initialized.
     *
     * @see ibs.util.list.ElementContainer#setElementClass (Class)
     */
    protected void initElementClass ()
        throws ListException
    {
        this.setElementClass (Type.class);
    } // initElementClass


    /**************************************************************************
     * Register a new type to the pool. <BR/>
     *
     * @param   idVersion   Combinated value of id and version like it is often
     *                      used. (idVersion = id + version)
     * @param   name        The type's name.
     * @param   code        The type's code name31.10.2000 15:21:22 .
     * @param   className   The name of the java class representing an object
     *                      of this type.
     *
     * @return  The {@link ibs.bo.type.Type} object which was stored within the
     *          type container.
     */
/* KR not used
    public Type addType (int idVersion, String name, String code,
        String className)
    {
//trace ("type for adding: " + idVersion + ":" + name + ":" + className);
        // create a new type instance and add it to the list:
        Type type = new Type (idVersion, name, className);
        type.setCode (code);

        // add the type to the container:
        this.add (type);

        // return the new type object:
        return type;
    } // addType
*/


    /**************************************************************************
     * Create the arrays out of the actual data. <BR/>
     * The {@link #ids ids} and the {@link #names names} arrays are
     * newly generated through this operation. <BR/>
     */
    public final void createArrays ()
    {
        Type type = null;               // the actual type
        int length = this.size ();      // the number of elements
        int i = 0;                      // actual index

        // create the new arrays:
        this.ids = new String[length];
        this.names = new String[length];
        this.codes = new String[length];

        // loop through all elements and fill the array with the data:
        // loop through all elements of the list:
        for (Iterator<Type> iter = this.iterator (); iter.hasNext (); i++)
        {
            // get the actual element:
            type = iter.next ();

            // set the array data:
            this.ids[i] = type.getTVersionIdStr ();
            this.names[i] = type.getName ();
            this.codes[i] = type.getCode ();
        } // for iter
    } // createArrays
    
    
    /**************************************************************************
     * Create the multilang arrays out of the actual data. <BR/>
     * The {@link #mlNames mlNames} and the
     * {@link #mlDescriptions mlDescriptions} arrays are newly generated
     * through this operation. <BR/>
     * 
     * @param   allTypes    The type container.
     */
    public final void createMultilangArrays (TypeContainer allTypes)
    {
        Type type = null;               // the actual type
        int length = this.size ();      // the number of elements
        int i = 0;                      // actual index

        // retrieve the locales
        List<Locale_01> locales = MultilingualTextProvider.getAllLocales (
                // No environment available at this position.
                // But the locales must be already initialized at this point.
                null);
        
        // initialize the arrays for all locales:
        this.mlNames = new HashMap<String, String[]> (locales.size ());
        this.mlDescriptions = new HashMap<String, String[]> (locales.size ());
        
        Iterator<Locale_01> it = locales.iterator ();
        while (it.hasNext ())
        {
            Locale_01 localeI = it.next ();

            this.mlNames.put (localeI.getLocaleKey (), new String[length]);
            this.mlDescriptions.put (localeI.getLocaleKey (), new String[length]);
        } // while
        
        // loop through all elements and fill the array with the data:
        // loop through all elements of the list:
        for (Iterator<Type> iter = this.iterator (); iter.hasNext (); i++)
        {
            // get the actual element:
            type = iter.next ();
            Type fullType = allTypes.get (type.getTVersionId ());
            
            // set the multilang names and descriptions for all locales
            it = locales.iterator ();
            while (it.hasNext ())
            {
                Locale_01 localeI = it.next ();
                
                this.mlNames.get (localeI.getLocaleKey ()) [i] =
                    fullType.getMlName (localeI.getLocale ());

                this.mlDescriptions.get (localeI.getLocaleKey ()) [i] =
                    fullType.getMlDescription (localeI.getLocale ());
            } // while
        } // for iter
    } // createMultilangArrays
    
    
    /**************************************************************************
     * Get the multilang type names for the current user. <BR/>
     *
     * @return  env     The environment.
     */
    public final String[] getMlNames (Environment env)
    {
        // get the value and return it:
        return this.mlNames.get (MultilingualTextProvider.getUserLocale (env).getLocaleKey ());
    } // getMlNames


    /**************************************************************************
     * Get a type. <BR/>
     *
     * @param   idVersion   Combinated value of id and version like it is often
     *                      used. (idVersion = id + version)
     *
     * @return  The type object.
     */
    public Type getType (int idVersion)
    {
        // get the type and return the result:
        return this.get (idVersion);
    } // getType


    /**************************************************************************
     * Derive a type from the type code. <BR/>
     *
     * @param   code    The (unique) type code.
     *
     * @return  The found type or <CODE>null</CODE> if it was not found.
     */
    public Type findType (String code)
    {
        // get the type and return the result:
        return this.find (code);
    } // findType


    /**************************************************************************
     * Derive a type from the type name. <BR/>
     * If there is more than one type with that name the first type with the
     * name is returned.
     *
     * @param   name    The name of the type.
     *
     * @return  The found type or <CODE>null</CODE> if it was not found.
     */
    public Type findTypeByName (String name)
    {
        Type type = null;               // the found type
        boolean found = false;          // was there a type found?

        // check if a valid name was set:
        if (name != null && !name.isEmpty ())
        {
            // loop through the types and search for the type with the correct name:
            // loop through all elements of the list:
            for (Iterator<Type> iter = this.iterator (); iter.hasNext ();)
            {
                // get the element out of the list:
                type = iter.next ();

                // check if the code is the one we are searching for:
                if (name.equals (type.getName ()))
                {
                    found = true;
                    break;
                } // if
            } // for iter

            if (found)                  // the type was found?
            {
                return type;            // return the type
            } // if the type was found
        } // if

        // the type was not found:
        return null;                    // return the error code
    } // findTypeByName


    /**************************************************************************
     * Find the types for a list of object type codes. <BR/>
     *
     * @param   typeCodeList    A comma-separated list of type codes for which
     *                          the types shall be found.
     *
     * @return  The resulting types.
     *          If no types were found the vector is empty.
     *
     * @throws  TypeNotFoundException
     *          At least of the defined types was not found. The list of not
     *          found types is included within the error message.
     */
    public Vector<Type> findTypes (String typeCodeList)
        throws TypeNotFoundException
    {
        // tokenize the type code list:
        String[] typeCodes = typeCodeList.split (" ?, ?");
        int numTypeCodes = typeCodes.length; // number of types
        Vector<Type> types = null;

        // check if there exists at least one type:
        if (typeCodes != null && numTypeCodes > 0)
        {
            Type type = null;           // the current type
            StringBuilder notFoundTypes = new StringBuilder (); // list of not-found types
            types = new Vector<Type> (numTypeCodes);

            // loop through the type codes and get the type for each of them:
            for (int i = 0; i < numTypeCodes; i++)
            {
                // get the type:
                type = this.findType (typeCodes[i]);

                // check if the type was found:
                if (type != null)
                {
                    // get the document template for the type and add it to the
                    // resulting vector:
                    types.add (type);
                } // if
                else
                {
                    // add the type code to the list of not found types:
                    if (notFoundTypes.length () > 0)
                    {
                        notFoundTypes.append (", ");
                    } // if
                    notFoundTypes.append (typeCodes[i]);
                } // else
            } // for i

            // check if there are any not-found types:
            if (notFoundTypes.length () > 0)
            {
                // TODO RB: Call  
                //          MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
                //              BOMessages.ML_MSG_TYPENOTFOUND, new String[] {notFoundTypes.toString ()}, env)
                //          to get the text in the correct language
                throw new TypeNotFoundException (BOMessages.ML_MSG_TYPENOTFOUND);
            } // if
        } // if
        else
        {
            // create empty result vector:
            types = new Vector<Type> ();
        } // else

        // return the result:
        return types;
    } // findTypes


    /**************************************************************************
     * Find the templates for a list of object type codes. <BR/>
     *
     * @param   typeCodeList    A comma-separated list of type codes for which
     *                          the templates shall be found.
     *
     * @return  The resulting templates.
     *          If no templates were found the vector is empty.
     *          If for any type no template is defined the corresponding
     *          position in the vector is <CODE>null</CODE>.
     *
     * @throws  TypeNotFoundException
     *          One of the defined types was not found.
     */
    public Vector<ITemplate> findTemplates (String typeCodeList)
        throws TypeNotFoundException
    {
        // find the types:
        Vector<Type> types = this.findTypes (typeCodeList);
        Vector<ITemplate> templates = null;

        // check if the list exists:
        if (types != null)
        {
            templates = new Vector<ITemplate> (types.size ());

            for (Iterator<Type> iter = types.iterator (); iter.hasNext ();)
            {
                Type type = iter.next ();

                // get the document template for the type and add it to the
                // resulting vector:
                templates.add (type.getTemplate ());
            } // for iter
        } // if

        // return the result:
        return templates;
    } // findTemplates


    /**************************************************************************
     * Derive a type id from the type code. <BR/>
     *
     * @param   code    The (unique) type code.
     *
     * @return  The type version id.
     */
    public int getTypeId (String code)
    {
        // get the type id and return the result:
        return this.getTVersionIdLocalized (code);
    } // getTypeId


    /**************************************************************************
     * Derive a type from the type code. <BR/>
     *
     * @param   code    The (unique) type code.
     *
     * @return  The found type or <CODE>null</CODE> if it was not found.
     */
    public final Type find (String code)
    {
        Type type = null;               // the found type
        boolean found = false;          // was there a type found?

        // check if a valid code was set:
        if (code != null && !code.isEmpty ())
        {
            // loop through the types and search for the type with the correct code:
            // loop through all elements of the list:
            for (Iterator<Type> iter = this.iterator (); iter.hasNext ();)
            {
                // get the element out of the list:
                type = iter.next ();

                // check if the code is the one we are searching for:
                if (code.equals (type.getCode ()))
                {
                    found = true;
                    break;
                } // if
            } // for iter

            if (found)                  // the type was found?
            {
                return type;            // return the type
            } // if the type was found
        } // if

        // the type was not found
        return null;                    // return the error code
    } // find


    /**************************************************************************
     * Derive a localized type version id from the type code. <BR/>
     *
     * @param   code    The (unique) type code.
     *
     * @return  The type id or
     *          <CODE>{@link ibs.bo.type.TypeConstants#TYPE_NOTYPE TYPE_NOTYPE}</CODE>
     *          if no type was found.
     */
    public final int getTVersionIdLocalized (String code)
    {
        Type type = null;               // the found type

        // search for the type:
        type = this.find (code);

        if (type != null)               // the type was found?
        {
            return type.getTVersionIdLocalized (); // return the id of the type
        } // if the type was found

        // the type was not found
        return TypeConstants.TYPE_NOTYPE; // return error code
    } // getTVersionIdLocalized


    /**************************************************************************
     * Derive a type version id from the type code. <BR/>
     *
     * @param   code    The (unique) type code.
     *
     * @return  The type version id or
     *          <CODE>{@link ibs.bo.type.TypeConstants#TYPE_NOTYPE TYPE_NOTYPE}</CODE>
     *          if no type was found.
     */
    public final int getTVersionId (String code)
    {
        Type type = null;               // the found type

        // search for the type:
        type = this.find (code);

        if (type != null)               // the type was found?
        {
            return type.getTVersionId (); // return the type version id
        } // if the type was found

        // the type was not found
        return TypeConstants.TYPE_NOTYPE; // return error code
    } // getTVersionId


    /**************************************************************************
     * Derive a type name from the type code. <BR/>
     *
     * @param   code    The (unique) type code.
     *
     * @return  The type name or <CODE>null</CODE> if no type was found.
     */
    public final String getTypeName (String code)
    {
        Type type = null;               // the found type

        // search for the type:
        type = this.find (code);

        if (type != null)               // the type was found?
        {
            return type.getName ();     // return the type name
        } // if the type was found

        // the type was not found
        return null;                    // return error code
    } // getTypeName


    /**************************************************************************
     * Get the template defined through its oid. <BR/>
     *
     * @param   templateOid The oid of the template.
     *
     * @return  The template or <CODE>null</CODE> if the template was not found.
     */
    public ITemplate getTemplate (OID templateOid)
    {
        Type type = null;               // the actual type
        ITemplate templ = null;         // the found template
        boolean found = false;          // was there a type found?

        // loop through the types and search for the type with the correct
        // template oid:
        for (Iterator<Type> iter = this.iterator (); iter.hasNext ();)
        {
            // get the element out of the list:
            type = iter.next ();
            templ = type.getTemplate ();

            // check if the code is the one we are searching for:
            if (templ != null && templ.getOid ().equals (templateOid))
            {
                found = true;
                break;
            } // if
        } // for iter

        if (found)                      // the template was found?
        {
            return templ;               // return the template
        } // if the template was found

        // the template was not found
        return null;                    // return the error code
    } // getTemplate


    /**************************************************************************
     * Returns the string representation of this object. <BR/>
     * The type ids and type names are concatenated to create a string
     * representation according to "{ids, names}".
     *
     * @return  String represention of the object.
     */
    public final String toString ()
    {
        String theString = "{";         // the string
        String sep = "";                // the element separator
        final String outputSep = ", ";  // output separator

//theString = "" + super.toString () + ": " + theString;
        // check if the arrays exist:
        if (this.codes != null && this.ids != null)
                                        // arrays exist?
        {
            // loop through all elements:
            for (int i = 0; i < this.codes.length; i++)
            {
                theString +=
                    sep + "{" + this.ids[i] + outputSep + this.codes[i] + "}";
                sep = outputSep;
            } // for i
        } // if arrays exist

        // finish the list:
        theString += "}";

        // return the computed string:
        return theString;
    } // toString

} // class TypeContainer

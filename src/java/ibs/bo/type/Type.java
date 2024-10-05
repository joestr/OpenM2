/*
 * Class: Type.java
 */

// package:
package ibs.bo.type;

// imports:
import ibs.bo.BOMessages;
import ibs.bo.BusinessObject;
import ibs.di.DocumentTemplate_01;
import ibs.di.ValueDataElement;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.ml.MlInfo;
import ibs.ml.MultilangConstants;
import ibs.ml.MultilingualTextInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.ml.Locale_01;
import ibs.service.conf.ConfHelpers;
import ibs.util.list.Element;
import ibs.util.list.IElementId;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/******************************************************************************
 * This class represents the Type of a business object. <BR/>
 *
 * @version     $Id: Type.java,v 1.22 2010/04/29 15:26:33 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 990115
 ******************************************************************************
 */
public class Type extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Type.java,v 1.22 2010/04/29 15:26:33 btatzmann Exp $";


    /**
     * Base id of the type. <BR/>
     * This id does not include the version number!
     * So the lowest 8 bits must be set to 0.
     */
/*
    private int p_baseId = 0;
*/

    /**
     * Version of the type. <BR/>
     * This value only uses the least 8 bits.
     */
/*
    private int p_version = 0;
*/

    /**
     * tVersionId of the type as string representation. <BR/>
     * This id has the following format: <CODE>0xddddiiiv</CODE>.
     * <LI><CODE>dddd ...</CODE> for digits as prefix (normally 0101). <BR/>
     * <LI><CODE>iii ....</CODE> the id of the type. <BR/>
     * <LI><CODE>v ......</CODE> the version of the type. <BR/>
     */
    private String p_tVersionIdStr = null;

    /**
     * Code name of the type. <BR/>
     */
    private String p_code = null;

    /**
     * Name of the class which corresponds to the type. <BR/>
     * This class name must be a fully qualified class name (incl. packages) to
     * ensure that a new instance of the class can be created by using this
     * value as class name. <BR/>
     * ex.: <CODE>"ibs.bo.type.Type"</CODE>. <BR/>
     */
    private String p_className = null;

    /**
     * The class object which implements type. <BR/>
     * This object is created by getting the class through
     * {@link #p_className p_className}.
     */
    private Class<? extends BusinessObject> p_class = null;

    /**
     * Name of the icon to be used for objects of this type. <BR/>
     */
    private String p_icon = null;

    /**
     * The types which an instance of this type may contain. <BR/>
     */
    private ITypeContainer<Type> p_mayContainTypes = null;

    /**
     * The document template of the object type. <BR/>
     * This property contains the business object which represents the type
     * information.
     */
    private ITemplate p_template = null;

    /**
     * The locking state of the type. <BR/>
     * If the type is locked it is not possible to access object of this type.
     */
    private boolean p_isLocked = false;

    /**
     * The super type of the actual type. <BR/>
     * This is the object representing the super type.
     */
    private int p_superTVersionId = TypeConstants.TYPE_NOTYPE;

    /**
     * The types which are direct sub types of this type. <BR/>
     * This container only contains direct sub types, i.e. types which are
     * sub types of sub types can only be derived through going into these sub
     * types and getting the sub types of these types.
     */
    private ITypeContainer<Type> p_subTypes = null;

    /**
     * The buttons to be displayed in info view. <BR/>
     */
    private int[] p_infoButtons = null;

    /**
     * The buttons to be displayed in content view. <BR/>
     */
    private int[] p_contentButtons = null;

    /**
     * The transformation for the object type. <BR/>
     */
    private String p_transformation = null;

    /**
     * The tokenized rule for creating an object's name. <BR/>
     */
    private String[] p_nameTemplateTokens = null;
    
    /**
     * Holds the preloaded multilang names for all locales.
     */
    private Map<String, String> mlNames = null;

    /**
     * Holds the preloaded multilang descriptions for all locales.
     */
    private Map<String, String> mlDescriptions = null;


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a Type object. <BR/>
     *
     * @param   id          Id of the element.
     * @param   name        The element's name.
     */
    public Type (IElementId id, String name)
    {
        // call constructor of super class:
        super (id, name);

        // set the instance's properties:
    } // Type


    /**************************************************************************
     * Creates a Type object. <BR/>
     * Calls the constructor of the super class. <P>
     * The private property p_tVersionIdStr is computed from the idVersion. <BR/>
     *
     * @param   idVersion   Combinated value of base id and version like it is
     *                      often used. (idVersion = baseId + version)
     */
    protected Type (int idVersion)
    {
        // call constructor of super class:
        super (idVersion, null);

        // set the object's properties:
/*
        this.p_baseId = idVersion & 0xFFF0;
        this.p_version = idVersion & 0x000F;
*/
        this.p_tVersionIdStr = Integer.toString (this.getTVersionId ());
    } // Type


    /**************************************************************************
     * Creates a Type object. <BR/>
     * Calls the constructor of the super class. <P>
     * The private property p_tVersionIdStr is computed from the idVersion. <BR/>
     * A class object is created out of the className.
     *
     * @param   idVersion   Combinated value of base id and version like it is
     *                      often used. (idVersion = baseId + version)
     * @param   name        The type's name.
     * @param   className   The name of the java class representing an object
     *                      of this type.
     *
     * @throws  TypeClassNotFoundException
     *          The class was not found or could not be instantiated.
     */
    public Type (int idVersion, String name, String className)
        throws TypeClassNotFoundException
    {
        // call constructor of super class:
        super (idVersion, name);

        // set the object's properties:
/*
        this.p_baseId = idVersion & 0xFFF0;
        this.p_version = idVersion & 0x000F;
*/
        this.p_tVersionIdStr = Integer.toString (this.getTVersionId ());
        try
        {
            this.setClassName (className);
        } // try
        catch (TypeClassNotFoundException e)
        {
            IOHelpers.printWarning (
                "Error when loading type class: ", this, e, false);
        } // catch
    } // Type



    ///////////////////////////////////////////////////////////////////////////
    // other methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Set the name of the java class which must be instantiated to get an
     * object of this type. <BR/>
     *
     * @param   className   The class name.
     *
     * @throws  TypeClassNotFoundException
     *          The class was not found or could not be instantiated.
     *
     * @see java.lang.Class
     */
    private void setClassName (String className)
        throws TypeClassNotFoundException
    {
        try
        {
            // get the class object:
            @SuppressWarnings ("unchecked") // suppress compiler warning
            Class<? extends BusinessObject> cls =
                (Class<? extends BusinessObject>) Class.forName (className);

            // at this point we know that the class exists
            // store the class and the class name:
            this.p_class = cls;
            this.p_className = className;
        } // try
        catch (ExceptionInInitializerError e)
        {
            // error in a static initializer.
            // TODO RB: Call  
            //          MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
            //              BOMessages.ML_MSG_CLASSNOTFOUND, new String[] {notFoundTypes.toString ()}, env)
            //          to get the text in the correct language
            throw new TypeClassNotFoundException (BOMessages.ML_MSG_CLASSNOTFOUND, e);
        } // catch
        catch (LinkageError e)
        {
            // any linkage error.
            // TODO RB: Call  
            //          MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
            //              BOMessages.ML_MSG_CLASSNOTFOUND, new String[] {notFoundTypes.toString ()}, env)
            //          to get the text in the correct language
            throw new TypeClassNotFoundException (BOMessages.ML_MSG_CLASSNOTFOUND, e);
        } // catch
        catch (ClassNotFoundException e)
        {
            // class not found.
            // TODO RB: Call  
            //          MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
            //              BOMessages.ML_MSG_TYPENOTFOUND, new String[] {notFoundTypes.toString ()}, env)
            //          to get the text in the correct language
            throw new TypeClassNotFoundException (BOMessages.ML_MSG_CLASSNOTFOUND, e);
        } // catch
    } // setClassName


    /**************************************************************************
     * Get the java class which must be instantiated to get an object of this
     * type. <BR/>
     * This class can be used to create an instance of the required java class.
     *
     * @param   env     The current environment
     *
     * @return  The java class.
     *
     * @see java.lang.Class
     *
     * @throws  TypeClassNotFoundException
     *          The class was not found.
     */
    public final Class<? extends BusinessObject> getTypeClass (Environment env)
        throws TypeClassNotFoundException
    {
        if (this.p_class != null)       // class set?
        {
            return this.p_class;        // return the class
        } // if class set

        // no class set
        throw new TypeClassNotFoundException (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_CLASSNOTFOUND, new String[] {this.p_className}, env));
    } // getTypeClass


    /**************************************************************************
     * Returns a hash code value for the object. This method is supported for
     * the benefit of hashtables such as those provided by java.util.Hashtable.
     * The general contract of hashCode is:. <BR/>
     * Whenever it is invoked on the same object more than once during an
     * execution of a Java application, the hashCode method must consistently
     * return the same integer. This integer need not remain consistent from
     * one execution of an application to another execution of the same
     * application. <BR/>
     * If two objects are equal according to the equals method, then calling
     * the hashCode method on each of the two objects must produce the same
     * integer result.
     *
     * @return  A hash code value for this object.
     *
     * @see java.util.Hashtable
     */
    public final int hashCode ()
    {
        return this.getIdInt ();
    } // hashCode


    /**************************************************************************
     * Get the tVersionId of the type. <BR/>
     * The tVersionId is created out of type and version id. <BR/>
     *
     * @return  The computed tVersionId.
     */
    public final int getTVersionId ()
    {
        // get the type and version info and return it:
        return Type.createTVersionId (this.getIdInt ());
    } // getTVersionId


    /**************************************************************************
     * Get the tVersionId of the type in a localized form. <BR/>
     * The tVersionId is created out of type and version id. The localized form
     * of the tVersionId does not contain any information about server or
     * domain. This means it is the same as returned by
     * {@link #getTVersionId getTVersionId} but the upper two bytes set
     * to <CODE>0</CODE>.
     *
     * @return  The computed tVersionId.
     */
    public final int getTVersionIdLocalized ()
    {
        int tVersionId = this.getIdInt (); // the type and version info

        // ensure the upper two bytes are 0:
        tVersionId &= 0xFFFF;

        if ((tVersionId & 0xF) == 0)    // no version number?
        {
            tVersionId |= 0x1;          // add default version number
        } // if

        return tVersionId;              // return the computed tVersionId
    } // getTVersionIdLocalized


    /**************************************************************************
     * Get the string representation of the tVersionId of this type. <BR/>
     *
     * @return  The tVersionIdStr of the type.
     */
    public final String getTVersionIdStr ()
    {
        // get the value and return it:
        return this.p_tVersionIdStr;
    } // getTVersionIdStr


    /**************************************************************************
     * Get the code of this type. <BR/>
     *
     * @return  The code of the type.
     */
    public final String getCode ()
    {
        // get the value and return it:
        return this.p_code;
    } // getCode


    /**************************************************************************
     * Set the code of this type. <BR/>
     *
     * @param   code    The code to be set.
     */
    public final void setCode (String code)
    {
        // set the new value:
        this.p_code = code;
    } // setCode


    /**************************************************************************
     * Get the tVersionId of the super type. <BR/>
     *
     * @return  The tVersionId of the super type.
     */
    public int getSuperTVersionId ()
    {
        // get the value and return it:
        return this.p_superTVersionId;
    } // getSuperTVersionId


    /**************************************************************************
     * Set the tVersionId of the super type. <BR/>
     *
     * @param   superTVersionId The tVersionId of the super type.
     */
    public void setSuperTVersionId (int superTVersionId)
    {
        // set the new value:
        this.p_superTVersionId = superTVersionId;
    } // setSuperTVersionId


    /**************************************************************************
     * Get the types which are inherited from this type. <BR/>
     *
     * @return  The types which are inherited from this type or
     *          <CODE>null</CODE> if there are no such types defined.
     */
    public final ITypeContainer<Type> getSubTypes ()
    {
        // get the value and return it:
        return this.p_subTypes;
    } // getSubTypes


    /**************************************************************************
     * Set the types which are inherited from this type. <BR/>
     *
     * @param   subTypes    The types to be set.
     */
    public final void setSubTypes (ITypeContainer<Type> subTypes)
    {
        // set the types:
        this.p_subTypes = subTypes;
    } // setSubTypes


    /**************************************************************************
     * Add a type as sub type of this type. <BR/>
     * <B>Important: The sub types have to be initialized first with
     * {@link #setSubTypes (ITypeContainer) setSubTypes}!</B>. <BR/>
     * Otherwise a {@link NullPointerException NullPointerException} is thrown.
     *
     * @param   subType     The type to be added.
     */
    public final void addSubType (Type subType)
    {
        // check if the type is valid:
        if (subType != null) // the sub type is valid?
        {
            // add the type:
            this.p_subTypes.add (subType);
        } // if the sub type is valid
    } // addSubType


    /**************************************************************************
     * Get all types which inherit this type or any sub type. <BR/>
     * This method gets the types which are directly and indirectly inherited
     * from this type.
     *
     * @param   subTypeContainer    The container for the sub types.
     *
     * @return  The types which are directly or indirectly inherited from this
     *          type or <CODE>null</CODE> if there are no such types defined.
     */
    public final ITypeContainer<Type> getAllSubTypes (
                                                      ITypeContainer<Type> subTypeContainer)
    {
        if (subTypeContainer != null)
        {
            // add the sub types to the container
//            subTypeContainer.addAll (this.p_subTypes);

            // loop through all sub types:
            for (Enumeration<Type> subTypes = this.p_subTypes.elements ();
                 subTypes.hasMoreElements ();)
            {
                // get the actual element:
                Type type = subTypes.nextElement ();

                // add the sub type to the container:
                subTypeContainer.add (type);
                // add the sub types of the type to the container:
                subTypeContainer.addAll (type.getAllSubTypes (subTypeContainer));
            } // for
        } // if

        // return the result:
        return subTypeContainer;
    } // getAllSubTypes


    /**************************************************************************
     * Get the types which this type may contain. <BR/>
     *
     * @return  The types which this type may contain or <CODE>null</CODE> if
     *          there are no types defined.
     */
    public final ITypeContainer<Type> getMayContainTypes ()
    {
        // get the value and return it:
        return this.p_mayContainTypes;
    } // getMayContainTypes


    /**************************************************************************
     * Set the types which this type may contain. <BR/>
     *
     * @param   mayContainTypes The types to be set.
     */
    public final void setMayContainTypes (ITypeContainer<Type> mayContainTypes)
    {
        // set the types:
        this.p_mayContainTypes = mayContainTypes;
    } // setMayContainTypes


    /**************************************************************************
     * Get the template for this type. <BR/>
     * The template is a business object which contains all information
     * regarding the type in a system-independent format (XML). <BR/>
     * If there is no template set the value <CODE>null</CODE> is returned.
     *
     * @return  The template of the type or <CODE>null</CODE> if there is no
     *          template defined.
     */
    public final ITemplate getTemplate ()
    {
        // get the value and return it:
        return this.p_template;
    } // getTemplate


    /**************************************************************************
     * Set the template for this type. <BR/>
     * The template is a business object which contains all information
     * regarding the type in a system-independent format (XML).
     *
     * @param   template    The template which shall be set.
     */
    public final void setTemplate (ITemplate template)
    {
        // set the template:
        this.p_template = template;
    } // setTemplate

    
    /**************************************************************************
     * Get the multilang name for this type for the given locale. <BR/>
     *
     * @return  The locale for which the multilang name should be returned.
     */
    public final String getMlName (Locale locale)
    {
        // get the value and return it:
        return this.mlNames.get (locale.toString ());
    } // getMlName


    /**************************************************************************
     * Set the multialng name for this type for the given locale. <BR/>
     *
     * @param   locale    The locale for the mlName.
     * @param   mlName    The multilang name for the locale.
     */
    public final void setMlName (Locale locale, String mlName)
    {
        this.mlNames.put (locale.toString (), mlName);
    } // setMlName
    
    
    /**************************************************************************
     * Get the multilang description for this type for the given locale. <BR/>
     *
     * @return  The locale for which the multilang description should be returned.
     */
    public final String getMlDescription (Locale locale)
    {
        // get the value and return it:
        return this.mlDescriptions.get (locale.toString ());
    } // getMlDescription


    /**************************************************************************
     * Set the multialng description for this type for the given locale. <BR/>
     *
     * @param   locale    The locale for the mlDescription.
     * @param   mlDescription    The multilang description for the locale.
     */
    public final void setMlDescription (Locale locale, String mlDescription)
    {
        this.mlDescriptions.put (locale.toString (), mlDescription);
    } // setMlDescription
    

    /**************************************************************************
     * Check if the type is currently locked. <BR/>
     *
     * @return  <CODE>true</CODE> if the type is locked, <CODE>false</CODE>
     *          otherwise.
     */
    public final boolean isLocked ()
    {
        // get the value and return it:
        return this.p_isLocked;
    } // isLocked


    /**************************************************************************
     * Lock the type. <BR/>
     * After that it is not possible to access objects of this type until the
     * type is unlocked. If the type was already locked nothing is done.
     *
     * @see #unlock ()
     */
    public final void lock ()
    {
        // check if the type is already locked:
        if (!this.p_isLocked) // type is not locked?
        {
            // lock the type:
            this.p_isLocked = true;
        } // if type is not locked
    } // lock


    /**************************************************************************
     * Unlock the type. <BR/>
     * After that it is possible to access objects of this type. If the type was
     * already unlocked nothing is done.
     *
     * @see #lock ()
     */
    public final void unlock ()
    {
        // check if the type is already unlocked:
        if (this.p_isLocked) // type is locked?
        {
            // unlock the type:
            this.p_isLocked = false;
        } // if type is locked
    } // unlock


    /**************************************************************************
     * Get the icon of this type. <BR/>
     *
     * @return  The icon of the type.
     */
    public final String getIcon ()
    {
        // get the value and return it:
        return this.p_icon;
    } // getIcon


    /**************************************************************************
     * Set the icon of this type. <BR/>
     *
     * @param   icon    The icon to be set.
     */
    public final void setIcon (String icon)
    {
        // set the new value:
        this.p_icon = icon;
    } // setIcon


    /***************************************************************************
     * Returns the string representation of this object. <BR/>
     * The type ids and type names are concatenated to create a string
     * representation according to "name (tVersionIdStr): className".
     *
     * @return  String represention of the object.
     */
    public final String toString ()
    {
        // compute the string and return it:
        return this.getName () + " (" + this.p_tVersionIdStr + "): " +
                this.p_className;
    } // toString


    /**************************************************************************
     * Create a tVersionId out of a typeId. <BR/>
     *
     * @param   typeId  Id of type being the base for the desired tVersionId.
     *
     * @return  The computed tVersionId.
     */
    public static final int createTVersionId (int typeId)
    {
        int tVersionId = typeId;        // get the type as initializator for
                                        // the tVersionId

        if (tVersionId < 0x00010000)    // no server and domain?
        {
            tVersionId += 0x01010000;   // add default server and domain
        } // if

        if ((tVersionId & 0xF) == 0)    // no version number?
        {
            tVersionId |= 0x1;          // add default version number
        } // if

        return tVersionId;              // return the computed tVersionId
    } // createTVersionId


    /**************************************************************************
     * Get the buttons that can be displayed when the user is in an
     * object's info view. <BR/>
     *
     * @return  An array with button ids that can be displayed.
     *          <CODE>null</CODE> if no buttons were defined.
     */
    public final int[] getInfoButtons ()
    {
        // check if the buttons were already set:
        if (this.p_infoButtons == null && this.p_template != null)
        {
            String buttonList = this.p_template.getInfoButtons ();
            this.p_infoButtons = ConfHelpers.parseButtonList (buttonList);
        } // if

        // return the button list:
        return this.p_infoButtons;
    } // getInfoButtons


    /**************************************************************************
     * Get the buttons that can be displayed when the user is in an
     * object's content view. <BR/>
     *
     * @return  An array with button ids that can be displayed.
     *          <CODE>null</CODE> if no buttons were defined.
     */
    public final int[] getContentButtons ()
    {
        // check if the buttons were already set:
        if (this.p_contentButtons == null)
        {
            String buttonList = this.p_template.getContentButtons ();
            this.p_contentButtons = ConfHelpers.parseButtonList (buttonList);
        } // if

        // return the button list:
        return this.p_contentButtons;
    } // getContentButtons


    /**************************************************************************
     * Get the transformation defined for the object type. <BR/>
     *
     * @return  The transformation.
     *          <CODE>null</CODE> if no transformation was defined.
     */
    public final String getTransformation ()
    {
        // check if the transformation was already set:
        if (this.p_transformation == null)
        {
            this.p_transformation = this.p_template.getTransformation ();
        } // if

        // return the transformation:
        return this.p_transformation;
    } // getTransformation


    /**************************************************************************
     * Get the tokenized form of the template for creating the name value. <BR/>
     *
     * @return  An array with tokens for the name value. The tokens have the
     *          form <CODE>{literal,fieldname,literal,...}</CODE>. <BR/>
     *          <CODE>literal</CODE> is any String literal. <BR/>
     *          <CODE>fieldname</CODE> is the name of any field whose value
     *          shall be put into the name. <BR/>
     *          <CODE>null</CODE> if no rule was defined.
     */
    public final String[] getNameTemplateTokens ()
    {
        // check if the name value tokens were already set:
        if (this.p_nameTemplateTokens == null)
        {
            String nameValue = this.p_template.getNameTemplate ();
            this.p_nameTemplateTokens = ConfHelpers.parseConfValue (nameValue);
        } // if

        // return the button list:
        return this.p_nameTemplateTokens;
    } // getNameValueTokens
    
    
    /***************************************************************************
     * Loads the multilang type info for all provided locales. <BR/>
     *
     * @param   locales  The locales to init the multilang info for
     * @param   env      The environment
     */
    public final void loadMultilangInfo (Collection<Locale_01> locales, Environment env)
    {
        // initialize the multilang info for the type:
        this.setMultilangTypeInfo (env);
        
        // initialize the multilang info for the type's template:
        if (this.getTemplate () != null)
        {
            this.getTemplate ().initMultilangInfo (locales, env);
        } // if
    } // loadMultilangInfo
    
    
    /***************************************************************************
     * Retrieves the multilang info for the type name and description
     * for all provided locales and sets the info. <BR/>
     *
     * @param   env      The environment
     */
    private final void setMultilangTypeInfo (Environment env)
    {
        List<Locale_01> locales = MultilingualTextProvider.getAllLocales (env);
        
        this.mlNames = new HashMap<String, String> (locales.size ());
        this.mlDescriptions = new HashMap<String, String> (locales.size ());

        // (1) Perform lookup within resource bundle for user's locale:        
        String typeBaseLookupKey = MultilingualTextProvider.getTypeBaseLookupKey (this);

        Iterator<Locale_01> it = locales.iterator ();
        while (it.hasNext ())
        {
            Locale_01 localeI = it.next ();
        
            // retrieve the name with the defined lookup key
            MultilingualTextInfo mlNameInfo = MultilingualTextProvider.getMultilingualTextInfo (
                    MultilangConstants.RESOURCE_BUNDLE_TYPES_NAME,
                    MultilingualTextProvider.getNameLookupKey (typeBaseLookupKey),
                    localeI,
                    env);
            
            // retrieve the description with the defined lookup key        
            MultilingualTextInfo mlDescriptionInfo = MultilingualTextProvider.getMultilingualTextInfo (
                    MultilangConstants.RESOURCE_BUNDLE_TYPES_NAME,
                    MultilingualTextProvider.getDescriptionLookupKey (typeBaseLookupKey),
                    localeI,
                    env);
            
            // check if something has been found
            String name = mlNameInfo.isFound () ?
                    // and use it
                    mlNameInfo.getMLValue () :
                    // (2) fallback - use the tab name
                    this.getName ();                
    
            // check if something has been found
            String description = mlDescriptionInfo.isFound () ?
                    // and use it
                    mlDescriptionInfo.getMLValue () :
                    // (2) fallback - return ""
                    "";
                    
            this.setMlName (localeI.getLocale (), name);
            this.setMlDescription (localeI.getLocale (), description);
        } // while
    } // setMultilangTypeInfo
    
    
    /***************************************************************************
     * Loads the multilang type reference info for all provided locales. <BR/>
     * This method has to be executed after initMultilangInfo () since
     * several texts are retrieved based on texts set within
     * initMultilangInfo ().
     *
     * @param   allTypes    The type container.
     * @param   locales     The locales to init the multilang info for
     * @param   env         The environment
     */
    public final void loadMultilangReferenceInfo (TypeContainer allTypes,
            Collection<Locale_01> locales, Environment env)
    {
        // Retrieve the may contain types type container for this type 
        TypeContainer types = (TypeContainer) this.getMayContainTypes ();
        // Initialize the multilang arrays
        if (types != null)
        {
            types.createMultilangArrays (allTypes);
        } // if
        
        // initialize multilang reference info for the template:
        if (this.getTemplate () != null)
        {
            this.getTemplate ().initMultilangReferenceInfo (locales, env);
        } // if
    } // loadMultilangReferenceInfo

    /***************************************************************************
     * Retrieve the multilang info for the type's field with the given name.
     *
     * @param   fieldName   The field to retrieve the multilang info for
     * 
     * @return  the multilang field info
     */
    public Map<String, MlInfo> getMultilangFieldInfo (String fieldName)
    {
        Map<String, MlInfo> retValue = null; 
            
        // check if it has a document template
        if (this.getTemplate () instanceof DocumentTemplate_01)
        {
            DocumentTemplate_01 docTempl = (DocumentTemplate_01) this.getTemplate ();
            
            // retrieve a value data element with a name like the current column name
            ValueDataElement vde = docTempl.getTemplateDataElement ().getValueElement (fieldName);
            
            if (vde != null) // found ?
            {
                retValue = vde.getMlInfos ();
            } // if
        } // if
        
        return retValue;
    } // getMultilangFieldInfo

} // class Type

/*
 * Class: FielRefInfo.java
 */

// package:
package ibs.di;

// imports:
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import ibs.BaseObject;
import ibs.io.Environment;
import ibs.ml.MlInfo;
import ibs.ml.MultilangConstants;
import ibs.ml.MultilingualTextInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.ml.Locale_01;


/******************************************************************************
 * This class holds all information regarding to a FIELDREF,VALUEDOMAIN,...
 * field within a form template definition or a COLUMN field within a container
 * template definition. <BR/>
 * Note that this is not very proper and should be separated into two
 * different classes.<BR/>
 *
 * @author      Andreas Jansa (AJ), 011206
 *              renamed by Bernhard Tatzmann (BT), Christa Tran (CT), 111207
 ******************************************************************************
 */
public class ReferencedObjectInfo extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ReferencedObjectInfo.java,v 1.5 2010/04/29 15:26:33 btatzmann Exp $";

    /**
     * Fieldtype unkown. <BR/>
     */
    public static final int TYPE_UNKNOWN = -1;

    /**
     * Fieldtype standard. <BR/>
     */
    public static final int TYPE_STANDARD = 0;

    /**
     * Fieldtype system. <BR/>
     * This means that it references a system value.<BR/>
     */
    public static final int TYPE_SYSTEM = 1;

    /**
     * Fieldtype extended. <BR/>
     * This means that the columns references an extension query column
     * in an container.<BR/>
     */
    public static final int TYPE_EXTENDED = 2;

    /**
     * Column type. <BR/>
     */
    private int p_type = ReferencedObjectInfo.TYPE_UNKNOWN;

    /**
     * Name of subtag in referenced object VALUE. <BR/>
     */
    private String p_name = null;

    /**
     * Token of subtag in referenced object VALUE. <BR/>
     */
    private String p_token = null;

    /**
     * Multilang info of subtag in referenced object VALUE. <BR/>
     */
    private Map<String, MlInfo> p_multiLangTokens = null;

    /**************************************************************************
     * Check if this is a system field. <BR/>
     *
     * @return  <CODE>true</CODE> if the field is a system field,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isSysField ()
    {
        return this.p_type == ReferencedObjectInfo.TYPE_SYSTEM;
    } // isSysField


    /**************************************************************************
     * Check if this is an extended field. <BR/>
     *
     * @return  <CODE>true</CODE> if the field is an extended field,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isExtendedField ()
    {
        return this.p_type == ReferencedObjectInfo.TYPE_EXTENDED;
    } // isExtendedField


    /**************************************************************************
     * Check if this is an unkwown field type. <BR/>
     *
     * @return  <CODE>true</CODE> if the field is unkown
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isUnkown ()
    {
        return this.p_type == ReferencedObjectInfo.TYPE_UNKNOWN;
    } // isUnkown


    /**************************************************************************
     * Get the type of the column. <BR/>
     *
     * @return  The type of the column.
     *          <CODE>null</CODE> if no name was set.
     */
    public int getType ()
    {
        return this.p_type;
    } // getType


    /**************************************************************************
     * Set the type.<BR/>
     *
     * @param type the p_type to set
     */
    public void setType (int type)
    {
        this.p_type = type;
    } // setType


    /**************************************************************************
     * Get the name. <BR/>
     *
     * @return  The name.
     *          <CODE>null</CODE> if no name was set.
     */
    public String getName ()
    {
        return this.p_name;
    } // getName


    /**************************************************************************
     * Set the name.<BR/>
     *
     * @param name the p_name to set
     */
    public void setName (String name)
    {
        this.p_name = name;
    } // setName


    /**************************************************************************
     * Get the token. <BR/>
     *
     * @return  The token.
     *          <CODE>null</CODE> if no token was defined.
     */
    public String getToken ()
    {
       return this.p_token;
    } // getToken
    
    
    /**************************************************************************
     * Get the multilang token. <BR/>
     *
     * @return  The multilang info.
     */
    public MlInfo getMultilangToken (Environment env)
    {
        // retrieve the key from the user's locale
        String key = MultilingualTextProvider.getUserLocale (env).getLocaleKey ();
        
        if (this.p_multiLangTokens.containsKey (key))
        {
            // retrieve the mulitlang token from the map
            return this.p_multiLangTokens.get (key);
        } // if
        else
        {
            // fallback handling - retrieve the default token for the current
            // user's locale
            return getDefaultToken (MultilingualTextProvider.getUserLocale (env), env);
        } // else
    } // getMultilangToken

    
    /**************************************************************************
     * Add a multilang token for a given locale. <BR/>
     *
     * @param locale    The locale to add the token for
     * @param mlInfo    The mlInfo to add
     *
     */
    public void addMultilangToken (Locale locale, MlInfo mlInfo)
    {
        if (this.p_multiLangTokens == null)
        {
            this.p_multiLangTokens = new HashMap<String, MlInfo> ();
        } // if
        
        this.p_multiLangTokens.put (locale.toString (), mlInfo);
    } // addMultilangToken
    
    
    /**************************************************************************
     * Resets the multilang tokens. <BR/>
     *
     */
    public void resetMultilangTokens ()
    {
        this.p_multiLangTokens = new HashMap<String, MlInfo> ();
    } // resetMultilangTokens


    /**************************************************************************
     * Set the token.<BR/>
     *
     * @param token the p_token to set
     */
    public void setToken (String token)
    {
        this.p_token = token;
    } // setToken


    /**************************************************************************
     * Creates an ReferencedObjectInfo. <BR/>
    /**************************************************************************
     * Creates an ReferencedObjectInfo. <BR/>
     */
    public ReferencedObjectInfo ()
    {
        this.p_type = ReferencedObjectInfo.TYPE_UNKNOWN;
        this.p_name = null;
        this.p_token = null;
    } // ReferencedObjectInfo


    /**************************************************************************
     * Creates an ReferencedObjectInfo. <BR/>
     *
     * @param type          type of the column
     * @param name          name of field.
     * @param token         token to be shown to user for this field.
     */
    public ReferencedObjectInfo (int type, String name, String token)
    {
        this.p_type = type;
        this.p_name = name;
        this.p_token = token;
    } // ReferencedObjectInfo


    /**************************************************************************
     * Returns the default token.<BR/>
     * 
     * In case of system fields the sys field token is retrieved and returned.
     * Otherwise the name is returned as default token.
     *
     * @param   locale      the locale to retrieve the token for
     * @param   env         the environment
     *
     * @return  The default token.
     */
    public MlInfo getDefaultToken (Locale_01 locale, Environment env)
    {
        MlInfo retValue = new MlInfo ();
        
        // the SYS references get their name from the
        // BOTokens
        if (getType () == ReferencedObjectInfo.TYPE_SYSTEM)
        {
            retValue.setName (DIHelpers.getSysFieldToken (getName (), locale, env));
        } // if (columnType == FieldRefInfo.TYPE_SYSTEM)
        else    // not a system type
        {
            // for non syscolumns set the token = name
            retValue.setName (getName ());
        } // else not a system type
        
        return retValue;
    } // getDefaultToken


    /**************************************************************************
     * Set the provided multilang token for all locales.<BR/>
     *
     * @param mlInfo    The multilang info to set for all locales. If null is
     *                  provided, the default token is set.
     * @param locales   The locales to set the token for.
     * @param end       The environment.
     */
    public void setMultilangTokenForLocales (MlInfo mlInfo, Collection<Locale_01> locales, Environment env)
    {
        Iterator<Locale_01> localeIt = locales.iterator ();
        while (localeIt.hasNext ())
        { 
            Locale_01 localeI = localeIt.next ();
            
            // set the multilang token
            this.addMultilangToken (localeI.getLocale (),
                    mlInfo != null ? mlInfo : this.getDefaultToken (localeI, env));
        } // while
    } // setMultilangTokenForLocales
    
    
    /**************************************************************************
     * Set the provided tokens map as ML token map.<BR/>
     *
     * @param mlTokens   the multilang tokens to set
     */
    public void setMultilangTokens (Map<String, MlInfo> mlTokens)
    {
        this.p_multiLangTokens = mlTokens;
    } // setMultilangTokens
    
    
    /***************************************************************************
     * Performes a ML text lookup (name, description) by using the provided
     * base lookup key for all given locales and adds the result as ML info
     * to the current object. <BR/>
     * 
     * If ml tokens are not found for all locales the ML text info within the
     * ref object info object is reset.
     *
     * @param   baseLookupKey
     *                   The base lookup key for the ML text retrieval, which
     *                   is extended for name and description lookup.
     * @param   locales  The locales to set the multilang info for.
     * @param   env      The environment.
     * 
     * @return returns if ml tokens have been set for all locales
     */
    public boolean setMlTokens (String baseLookupKey, Collection<Locale_01> locales, Environment env)
    {       
        // Perform lookup for all locales:
        Iterator<Locale_01> localeIt = locales.iterator ();
        while (localeIt.hasNext ())
        { 
            Locale_01 localeI = localeIt.next ();
            
            MlInfo mlToken = new MlInfo ();
                           
            // retrieve the name with the defined lookup key
            MultilingualTextInfo mlNameInfo = MultilingualTextProvider.getMultilingualTextInfo (
                    MultilangConstants.RESOURCE_BUNDLE_FORMTEMPLATES_NAME,
                    MultilingualTextProvider.getNameLookupKey (baseLookupKey),
                    localeI,
                    env);
            
            // retrieve the name with the defined lookup key
            MultilingualTextInfo mlDescInfo = MultilingualTextProvider.getMultilingualTextInfo (
                    MultilangConstants.RESOURCE_BUNDLE_FORMTEMPLATES_NAME,
                    MultilingualTextProvider.getDescriptionLookupKey (baseLookupKey),
                    localeI,
                    env);
            
            // check if something has been found
            if (mlDescInfo.isFound ())
            {
                mlToken.setDescription (mlDescInfo.getMLValue ());
            } // if
            
            // check if something has been found
            if (mlNameInfo.isFound ())
            {
                mlToken.setName (mlNameInfo.getMLValue ());
                
                // set the multilang token
                this.addMultilangToken (localeI.getLocale (), mlToken);
            } // if
            else
            {
                // reset the mulitlang tokens if there is not an entry for every language
                // can occur if there is no entry for the default language for example 
                this.resetMultilangTokens ();
                
                return false;
            } // else
        } // while
        
        return true;
    } // setMlTokens

} // class ReferencedObjectInfo

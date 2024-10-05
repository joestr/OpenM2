/*
 * Class: MultilingualTextProvider
 */

// package:
package ibs.ml;

// imports:
import ibs.app.AppMessages;
import ibs.app.UserInfo;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOPathConstants;
import ibs.bo.BusinessObjectInfo;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.bo.tab.Tab;
import ibs.bo.type.Type;
import ibs.di.DIConstants;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.obj.ml.LocaleContainer_01;
import ibs.obj.ml.Locale_01;
import ibs.tech.html.BuildException;
import ibs.tech.html.ScriptElement;
import ibs.util.NoAccessException;
import ibs.util.file.FileHelpers;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;


/******************************************************************************
 * This provides support for handling multilingual texts. <BR/>
 *
 * @version     $Id: MultilingualTextProvider.java,v 1.26 2012/01/10 12:01:31 rburgermann Exp $
 *
 * @author      Bernhard Tatzmann (BT)
 ******************************************************************************
 */
public class MultilingualTextProvider
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MultilingualTextProvider.java,v 1.26 2012/01/10 12:01:31 rburgermann Exp $";
    
    /**
     * The locale's container EXT domain.
     */
    public static final String LOCALES_CONTAINER_EXT_DOMAIN = "ibs_instobj";

    /**
     * The locale's container EXT ID.
     */
    public static final String LOCALES_CONTAINER_EXT_ID = "locales";

    /**
     * Prefix for text displayed if the retrieved text or message is not found.
     */
    public static final String TEXT_NOT_FOUND_PREFIX = "NOT FOUND (";

    /**
     * Post fix for text displayed if the retrieved text or message is not found.
     */
    public static final String TEXT_NOT_FOUND_POSTFIX = ")";
    
    /**
     * Caches the resource bundle class loader
     */
    private static ClassLoader rbClassLoader;

    /**
     * Caches the configured locales.
     */
    private static List<Locale_01> locales;
    
    /**
     * Caches the default locale.
     */
    private static Locale_01 defaultLocale;
    
    /**
     * ThreadLocal variable for providing the user's locale to text retrieval methods
     * without environment.
     */
    private static ThreadLocal<Locale_01> userLocale = new ThreadLocal<Locale_01>();

    /**
     * Caches the customer name
     */
    private static String customerName = null;
    
    /**
     * The character encoding of texts returned by the Java resource bundle API. <BR/>
     */
    public static final String CHARACTER_ENCODING_RB_API = "ISO-8859-1";
    
    ///////////////////////////////////////////////////////////////////////////
    // methods
    ///////////////////////////////////////////////////////////////////////////
    
    /**************************************************************************
     * Initializes the MultilingualTextProvider.
     * 
     * Can be called as soon as types are available.
     *
     * @param   env     The environment.
     *
     * @throws MultilingualTextProviderRuntimeException
     *          thrown if an unexpected error occurs
     */
    public static void init (Environment env)
    {    
        if (env != null &&
                env.getApplicationInfo () != null &&
                env.getApplicationInfo ().p_system != null &&
                env.getApplicationInfo ().p_system.p_customerName != null)
        {
            MultilingualTextProvider.customerName = env.getApplicationInfo ().p_system.p_customerName;
        } // if
        else
        {
            String msg = "Customer name not found during initializing MultilingualTextProvider";
            
            // can only occur if no default locale
            IOHelpers.showMessage (msg, false, env);
            
            throw new MultilingualTextProviderRuntimeException (msg);
        } // else
        
        try
        {
            MultilingualTextProvider.setDefaultLocale (env);
        }
        catch (Exception e)
        {
            // can only occur if no default locale
            String msg = "Exception during setting default locale within MultilingualTextProvider.init";
            
            IOHelpers.showMessage (msg, e.getLocalizedMessage (), env);
            
            throw new MultilingualTextProviderRuntimeException (msg);
        } // catch
        
        // Initialize the resource bundle class loader
        MultilingualTextProvider.setRbClassLoader (env);
    } // init
    
    /**************************************************************************
     * Returns the cummulated keys for the given bundle and all provided
     * locales.
     *
     * @param   bundle  The resource bundle.
     * @param   locales The locales to cummulate the keys for.
     * @param   env     The environment.
     *
     * @return  The locales.
     * @throws MultilingualTextProviderException 
     */
    public static Collection<String> getKeysForBundle (String bundle,
            Collection <Locale_01> locales, Environment env) throws MultilingualTextProviderException
    {
        Set<String> keys = new HashSet <String> ();
        
        // set the default locale
        Locale.setDefault (MultilingualTextProvider.getDefaultLocale ().getLocale ());
        
        Iterator<Locale_01> it = locales.iterator ();
        while (it.hasNext ())
        {
            ResourceBundle resourceBundle = ResourceBundle.getBundle (bundle , 
              it.next ().getLocale (), MultilingualTextProvider.getRbClassLoader ());
        
            // add all keys to the return collection
            keys.addAll (Collections.list (resourceBundle.getKeys ()));
        } // while
        
        return keys;
    } // getKeysForBundle

    
    /**************************************************************************
     * Returns the text for the given key within the given bundle for the
     * user's locale. If no text for the provided locale is found a
     * defaulting mechanism is used which iterates through all candidate
     * bundles and also checks the bundle for the default language.
     *
     * @param   bundle  The resource bundle.
     * @param   key     The resource bundle key to retrieve the text for.
     * @param   env     The environment. If a user locale can be found
     *                  within the provided environment this locale is
     *                  used otherwise the default locale is used for
     *                  text retrieval.
     *
     * @return  The text.
     */
    public static String getText (String bundle, String key, Environment env)
    {       
        try
        {
            String retValue = MultilingualTextProvider.getTextForLocale (
                    bundle, key, getUserLocale (env), env);
            
            if (retValue != null)
            {
                return retValue;
            } // if
            else
            {
                return getTextNotFoundValue (bundle, key);
            } // else
        } // try
        catch (MultilingualTextProviderRuntimeException e)
        {
            if (env != null)
            {
                IOHelpers.showMessage (e, env);
            } // if
            else
            {
                IOHelpers.printError ("Error during MultilingualTextProvider.getText",
                        e, true);
            } // else
            
            // return text not found
            return getTextNotFoundValue (bundle, key);
        } // catch
    } // getText

    
    /**************************************************************************
     * Returns an array of texts for the given keys within the given bundle for 
     * the user's locale. If no text for a key for the provided locale is 
     * found a defaulting mechanism is used which iterates through all 
     * candidate bundles and also checks the bundle for the default language.
     *
     * @param   bundle  The resource bundle.
     * @param   keys    An array with resource bundle keys to retrieve the 
     *                  texts for.
     * @param   env     The environment. If a user locale can be found
     *                  within the provided environment this locale is
     *                  used otherwise the default locale is used for
     *                  text retrieval.
     *
     * @return  An array with the texts.
     */
    public static String[] getText (String bundle, String[] keys, Environment env)
    {       
        if (keys != null && keys.length != 0)
        {
            // new string array for texts
            String[] texts = new String[keys.length];

            // iterate over all keys and get the corresponding text
            for (int kC = 0; kC < keys.length; kC++)
            {
                texts[kC] = getText(bundle, keys[kC], env);
            } // for
            // return the texts
            return texts;
        } // if
        else
        {
            // no keys provided; return an empty array
            return new String[0];
        } // else
    } // getText

    
    /**************************************************************************
     * Returns the text for the given key within the given bundle.
     *
     * This method is intended for being used within XSLT. 
     * 
     * The user's locale is retrieved via getUserLocaleFromThread ().
     * Therefore has to be called before this method is called on a position,
     * where the env is available.
     * 
     * 
     * If no text for the provided locale is found a
     * defaulting mechanism is used which iterates through all candidate
     * bundles and also checks the bundle for the default language.
     *
     * @param   bundle  The resource bundle.
     * @param   key     The resource bundle key to retrieve the text for.
     *
     * @return  The text.
     */
    public String getXsltText (String bundle, String key)
    {           
        try
        {
            String retValue = MultilingualTextProvider.getTextForLocale (bundle, key,
                    MultilingualTextProvider.getUserLocaleFromThread (),
                    null);
            
            if (retValue != null)
            {
                return retValue;
            } // if
            else
            {
                return getTextNotFoundValue (bundle, key);
            } // else
        } // try
        catch (MultilingualTextProviderRuntimeException e)
        {           
            // return text not found
            return getTextNotFoundValue (bundle, key);
        } // catch
    } // getXsltText
    
    
    /**************************************************************************
     * Returns the message for the given key within the given bundle.
     *
     * This method is intended for being used within XSLT. 
     * 
     * The user's locale is retrieved via getUserLocaleFromThread ().
     * Therefore has to be called before this method is called on a position,
     * where the env is available.
     * 
     * If no message for the provided locale is found a
     * defaulting mechanism is used which iterates through all candidate
     * bundles and also checks the bundle for the default language.
     *
     * @param   bundle  The resource bundle.
     * @param   key     The resource bundle key to retrieve the message for.
     * @param   messageArguments
     *                      The message arguments.
     *                      
     * @return  The message.
     */
    public String getXsltMessage (String bundle, String key, Object[] messageArguments)
    {           
        try
        {
            String retValue = MultilingualTextProvider.getMessageForLocale (bundle, key,
                    messageArguments,
                    MultilingualTextProvider.getUserLocaleFromThread (),
                    null);
            
            if (retValue != null)
            {
                return retValue;
            } // if
            else
            {
                return getTextNotFoundValue (bundle, key);
            } // else
        } // try
        catch (MultilingualTextProviderRuntimeException e)
        {           
            // return text not found
            return getTextNotFoundValue (bundle, key);
        } // catch
    } // getXsltMessage
    
    
    /**************************************************************************
     * Returns the message for the given key within the given bundle.
     *
     * This method is intended for being used within XSLT if no message
     * arguments are needed. 
     * 
     * The user's locale is retrieved via getUserLocaleFromThread ().
     * Therefore has to be called before this method is called on a position,
     * where the env is available.
     * 
     * If no message for the provided locale is found a
     * defaulting mechanism is used which iterates through all candidate
     * bundles and also checks the bundle for the default language.
     *
     * @param   bundle  The resource bundle.
     * @param   key     The resource bundle key to retrieve the message for.
     *                      
     * @return  The message.
     */
    public String getXsltMessage (String bundle, String key)
    {           
        try
        {
            String retValue = MultilingualTextProvider.getMessageForLocale (bundle, key,
                    new Object[0],
                    MultilingualTextProvider.getUserLocaleFromThread (),
                    null);
            
            if (retValue != null)
            {
                return retValue;
            } // if
            else
            {
                return getTextNotFoundValue (bundle, key);
            } // else
        } // try
        catch (MultilingualTextProviderRuntimeException e)
        {           
            // return text not found
            return getTextNotFoundValue (bundle, key);
        } // catch
    } // getXsltMessage
    
    
    /**************************************************************************
     * Sets the user locale to the thread.
     * 
     * This method can be used provide the locale within one thread to another
     * method without environment (e.g. text retrieval from XSLT). 
     *
     * @param   env     The environment.
     */
    public static void setUserLocaleToThread (Environment env)
    {   
        MultilingualTextProvider.userLocale.set (
                MultilingualTextProvider.getUserLocale (env));
    } // setUserLocaleToThread

    
    /**************************************************************************
     * Returns the user locale from the thread. Use setUserLocaleToThread
     * to set the locale.
     *
     * @return  The user locale
     */
    public static Locale_01 getUserLocaleFromThread ()
    {   
        return MultilingualTextProvider.userLocale.get ();
    } // getUserLocaleFromThread

    
    /**************************************************************************
     * Returns the text for the given key within the given bundle for the
     * provided locale. If no text for the provided locale is found a
     * defaulting mechanism is used which iterates through all candidate
     * bundles and also checks the bundle for the default language.
     *
     * @param   bundle  The resource bundle.
     * @param   key     The resource bundle key to retrieve the text for.
     * @param   locale  The locale to retrieve the text for.
     * @param   env     The environment. Only needed for showing messages.
     *                  If no env is provided error messages are logged via
     *                  print methods.
     *
     * @return  The text if found. Otherwise null is returned.
     * @throws  MultilingualTextProviderRuntimeException
     *          thrown if an unexpected technical error occurs
     */
    public static String getTextForLocale (String bundle, String key, Locale_01 locale, Environment env)
    {    
        // Holds the multilang return text
        String retValue = null;
        
        // Holds the retrieved resource bundle
        ResourceBundle resourceBundle = null;
        
        // Retrieve the lookup locale
        Locale lookupLocale = getLookupLocale (locale);
        
        try
        {           
            // Retrieve the resource bundle
            resourceBundle = ResourceBundle.getBundle(
                bundle,
                lookupLocale,
                MultilingualTextProvider.getRbClassLoader ());
        } // try
        catch (MissingResourceException e)
        {
            StringBuilder sb = new StringBuilder ().
                append ("No resource bundle found! Resource bundle: ").
                append (bundle).
//                append (", Key: ").
//                append (key).
                append (", Locale: ").
                append (MultilingualTextProvider.getDefaultLocale ().getLocale ()).
                append (". Please contact your administrator.");
            
            // can only occur if no default bundle exists
            if (env != null)
            {
                IOHelpers.showMessage (sb.toString (), false, env);
            } // if
            else
            {
                IOHelpers.printError (sb.toString (), e, false);
            } // else
            
            return null;
        } // catch
        
        // if we have come so far a resource bundle has been found
        // check if the key is within the found bundle
        if (key != null && resourceBundle != null && resourceBundle.containsKey (key))
        {
            // the key exists within the bundle
            retValue = resourceBundle.getString (key);
        } // if
        else if (key == null)
        {
            String pos = "MultilingualTextProvider.getTextForLocale ()";
            String msg = "No key defined.";
            
            // can only occur if no default bundle exists
            if (env != null)
            {
                IOHelpers.showMessage (pos + " " + msg, false, env);
            } // if
            else
            {
                IOHelpers.printMessage (pos, msg, AppMessages.MST_ERROR);
            } // else
            
            return null;
        }
        else
        {
            // The key was not found within the bundle!
            // This means that a specific resource bundle has been found
            // but the key was not found there.
            // The candidate bundle concept (de_AT, de, en_US, en, _) is only applied by the Java API
            // until a resource bundle is found.
            // If one for the desired language (e.g. de_AT) is found the chain is only (de_AT, de, _).
            // The default bundle is not considered anymore.
            // So we have to check also the default resource bundle if the found resource bundle
            // is not already the default resource bundle.
            if (!resourceBundle.getLocale ().equals (Locale.getDefault ()))
            {
                return MultilingualTextProvider.getTextForLocale (bundle, key,
                        MultilingualTextProvider.getDefaultLocale (), env);
            } // if
            else
            {
                // return text not found
                return null;
            } // else
        } // else
        
        return MultilingualTextProvider.getEncodedString (retValue);
    } // getTextForLocale
    
    
    /**************************************************************************
     * Encodes the text retrieved from the resource bundle properly.
     *
     * @param   text  The text to encode.
     *
     * @return  The encoded text
     */
    private static String getEncodedString (String text)
    {
        if (text == null)
        {
            return null;
        } // if
        
        try
        {
            // convert the text from the encoding returned from the resource bundle Java API
            // to the application encoding
            text = new String(
                    text.getBytes(MultilingualTextProvider.CHARACTER_ENCODING_RB_API),
                    DIConstants.CHARACTER_ENCODING);
        } // try
        catch (UnsupportedEncodingException e)
        {
            // should never occur. if occurs return the unencoded value
        } // catch
        
        return text;
    } // getEncodedString
    
    
    /**************************************************************************
     * Returns a <code>MultilingualTextInfo</code> object for the given key
     * within the given bundle for the provided locale.
     *
     * @param   bundle  The resource bundle.
     * @param   key     The resource bundle key to retrieve the text for.
     * @param   locale  The locale to retrieve the text for.
     * @param   env     The environment.
     *
     * @return  The <code>MultilingualTextInfo</code> object
     */
    public static MultilingualTextInfo getMultilingualTextInfo (
            String bundle, String key, Locale_01 locale,
            Environment env)
    {          
        String mlValue = MultilingualTextProvider.getTextForLocale (bundle, key, locale, env);
        
        MultilingualTextInfo mlTextInfo = new MultilingualTextInfo ();
        mlTextInfo.setState (mlValue != null ? MultilingualTextInfo.STATE_FOUND : MultilingualTextInfo.STATE_NOT_FOUND);
        mlTextInfo.setMLValue (mlValue != null ? mlValue : getTextNotFoundValue (bundle, key));
        mlTextInfo.setBundle (bundle);
        mlTextInfo.setLocale (locale);
        mlTextInfo.setMLKey (key);
        
        return mlTextInfo;
    } // getMultilingualTextInfo

    
    /**************************************************************************
     * Returns the lookup local created from the provided
     * <code>Locale_01<code> object and the environment.
     * @param env 
     *
     * @param   locale    The Locale_01 object.
     * @param   env       The environment.
     *
     * @return  The ibs.util.Locale object.
     */
    private static Locale getLookupLocale (Locale_01 locale)
    {        
        Locale lookupLocale = null;
        
        if (MultilingualTextProvider.customerName != null)
        {
            lookupLocale = new Locale (
                // set the provided language
                locale.getLocale ().getLanguage (),
                // set the provided country
                locale.getLocale ().getCountry (),
                // set the customer name as variant
                MultilingualTextProvider.customerName);
        } // if
        else
        {
            lookupLocale = new Locale (
                    // set the provided language
                    locale.getLocale ().getLanguage (),
                    // set the provided country
                    locale.getLocale ().getCountry ());
        } // else
        
        return lookupLocale;
    } // getLookupLocale
    
    
    /**************************************************************************
     * Returns the text not found return value.
     *
     * @param   bundle The message bundle.
     * @param   key    The text or message key.
     *
     * @return  The text not found value.
     */
    private static String getTextNotFoundValue (String bundle, String key)
    {        
        StringBuffer buf = new StringBuffer (TEXT_NOT_FOUND_PREFIX).
            append (bundle).
            append ("/").
            append (key).
            append (TEXT_NOT_FOUND_POSTFIX);
        
        return buf.toString ();
    } // getTextNotFoundValue
    
    
    /**************************************************************************
     * Returns the message for the given key within the given bundle for the
     * user's locale. If no message for the provided locale is found a
     * defaulting mechanism is used which iterates through all candidate
     * bundles and also checks the bundle for the default language.
     *
     * @param   bundle      The resource bundle.
     * @param   key         The resource bundle key to retrieve the message for.
     * @param   env         The environment.
     *
     * @return  The message.
     */
    public static String getMessage (String bundle, String key, Environment env)
    {     
        // Call common getMessage method with an emtpy object array 
        return MultilingualTextProvider.getMessage (
                bundle, key, new Object[0], env);
    } // getMessage

    
    /**************************************************************************
     * Returns the message for the given key within the given bundle for the
     * user's locale. If no message for the provided locale is found a
     * defaulting mechanism is used which iterates through all candidate
     * bundles and also checks the bundle for the default language.
     *
     * @param   bundle      The resource bundle.
     * @param   key         The resource bundle key to retrieve the message for.
     * @param   messageArguments
     *                      The message arguments.
     * @param   env         The environment. If a user locale can be found
     *                      within the provided environment this locale is
     *                      used otherwise the default locale is used for
     *                      text retrieval.
     *
     * @return  The message.
     */
    public static String getMessage (String bundle, String key,
            Object[] messageArguments, Environment env)
    {     
        try
        {
            String retValue = MultilingualTextProvider.getMessageForLocale (
                    bundle, key, messageArguments, getUserLocale (env), env);
            
            if (retValue != null)
            {
                return retValue;
            } // if
            else
            {
                return getTextNotFoundValue (bundle, key);
            } // else
        } // try
        catch (MultilingualTextProviderRuntimeException e)
        {
            if (env != null)
            {
                IOHelpers.showMessage (e, env);
            } // if
            else
            {
                IOHelpers.printError ("Error during MultilingualTextProvider.getMessage",
                        e, true);
            } // else
            
            // return text not found
            return getTextNotFoundValue (bundle, key);
        } // catch
    } // getMessage
    
    
    /**************************************************************************
     * Returns the message for the given key within the given bundle for the
     * provided locale. If no message for the provided locale is found a
     * defaulting mechanism is used which iterates through all candidate
     * bundles and also checks the bundle for the default language.
     *
     * @param   bundle  The resource bundle.
     * @param   key     The resource bundle key to retrieve the message for.
     * @param   messageArguments
     *                      The message arguments.
     * @param   locale  The locale to retrieve the text for.
     * @param   env     The environment.
     *
     * @return  The message if found. Otherwise null is returned. 
     * @throws  MultilingualTextProviderRuntimeException
     *          thrown if an unexpected technical error occurs
     */
    public static String getMessageForLocale (String bundle, String key,
            Object[] messageArguments, Locale_01 locale, Environment env)
    {    
        // Holds the multilang return text
        String retValue = null;
        
        // Holds the retrieved resource bundle
        ResourceBundle resourceBundle = null;
        
        // Retrieve the lookup locale
        Locale lookupLocale = MultilingualTextProvider.getLookupLocale (locale);
        
        try
        {
            // Retrieve the resource bundle
            resourceBundle = ResourceBundle.getBundle(bundle, lookupLocale,
                  MultilingualTextProvider.getRbClassLoader ());
        } // try
        catch (MissingResourceException e)
        {
            // can only occur if no default bundle exists
            StringBuilder sb = new StringBuilder ().
                append ("No fallback bundle defined for: ").
                append (bundle).
                append (" and Locale: ").
                append (MultilingualTextProvider.getDefaultLocale ().getLocale ()).
                append (". Please contact your administrator.");
        
            // can only occur if no default bundle exists
            if (env != null)
            {
                IOHelpers.showMessage (sb.toString (), false, env);
            } // if
            else
            {
                IOHelpers.printError (sb.toString (), e, false);
            } // else
                       
            return null;
        } // catch
        
        MessageFormat formatter = new MessageFormat("");
        
        // check if the key is within the user's locale bundle
        if (key != null && resourceBundle != null && resourceBundle.containsKey (key))
        {
            // retrieve the message from the bundle
            String message = MultilingualTextProvider.getEncodedString (resourceBundle.getString (key));

            // set the message arguments
            formatter.setLocale(locale.getLocale ());
            formatter.applyPattern(message);
            retValue = formatter.format(messageArguments);
        } // if
        else if (key == null)
        {
            String pos = "MultilingualTextProvider.getMessageForLocale ()";
            String msg = "No key defined.";
            
            // can only occur if no default bundle exists
            if (env != null)
            {
                IOHelpers.showMessage (pos + " " + msg, false, env);
            } // if
            else
            {
                IOHelpers.printMessage (pos, msg, AppMessages.MST_ERROR);
            } // else
            
            return null;
        }
        else 
        {
            // The key was not found within the bundle!
            // This means that a specific resource bundle has been found
            // but the key was not found there.
            // The candidate bundle concept (de_AT, de, en_US, en, _) is only applied by the Java API
            // until a resource bundle is found.
            // If one for the desired language (e.g. de_AT) is found the chain is only (de_AT, de, _).
            // The default bundle is not considered anymore.
            // So we have to check also the default resource bundle if the found resource bundle
            // is not already the default resource bundle.
            if (!resourceBundle.getLocale ().equals (Locale.getDefault ()))
            {
                return MultilingualTextProvider.getMessageForLocale (bundle, key, messageArguments,
                        MultilingualTextProvider.getDefaultLocale (), env);
            } // if
            else
            {
                // return text not found
                return null;
            } // else
        } // else
        
        return retValue;
    } // getMessageForLocale
    
    
    /**************************************************************************
     * Returns a <code>MultilingualTextInfo</code> object containing the
     * message for the given key within the given bundle for the provided
     * locale and the provided message arguments.
     *
     * @param   bundle  The resource bundle.
     * @param   key     The resource bundle key to retrieve the message for.
     * @param   messageArguments
     *                      The message arguments.
     * @param   locale  The locale to retrieve the text for.
     * @param   env     The environment.
     *
     * @return  The <code>MultilingualTextInfo</code> object
     * @throws MultilingualTextProviderException 
     */
    public static MultilingualTextInfo getMultilingualMessageInfo (String bundle, String key,
            Object[] messageArguments, Locale_01 locale, Environment env) throws MultilingualTextProviderException
    {           
        String mlValue = MultilingualTextProvider.
            getMessageForLocale (bundle, key, messageArguments, locale, env);
        
        MultilingualTextInfo mlTextInfo = new MultilingualTextInfo ();
        mlTextInfo.setState (mlValue != null ? MultilingualTextInfo.STATE_FOUND : MultilingualTextInfo.STATE_NOT_FOUND);
        mlTextInfo.setMLValue (mlValue != null ? mlValue : getTextNotFoundValue (bundle, key));
        mlTextInfo.setBundle (bundle);
        mlTextInfo.setLocale (locale);
        mlTextInfo.setMLKey (key);
        
        return mlTextInfo;
    } // getMultilingualTextInfo

    
    /**************************************************************************
     * Returns the class loader for resource bundles retrieval.
     *
     * @return  The class loader.
     * @throws MultilingualTextProviderRuntimeException
     *          thrown if class loader has not been initialized 
     */
    private static ClassLoader getRbClassLoader ()
    {     
        if (MultilingualTextProvider.rbClassLoader == null)
        {
            throw new MultilingualTextProviderRuntimeException ("The class loader has not been intialized!" +
                    "MultilingualTextProvider.setRbClassLoader has to be executed first!");
        } // if
        
        return MultilingualTextProvider.rbClassLoader;
    } // getRbClassLoader
    
    
    /**************************************************************************
     * Initializes the class loader for resource bundle retrieval.
     *
     * @param   env     The environment.
     *
     * @return  The text.
     * @throws MultilingualTextProviderRuntimeException
     *          thrown if an error during class loader creation occurs 
     */
    private static void setRbClassLoader (Environment env)
    {     
        String rbPath = BOPathConstants.PROTOCOL_FILE + env.getApplicationInfo ().p_system.p_m2AbsBasePath + BOPathConstants.PATH_RESOURCE_BUNDLES;
       
        try
        {
            MultilingualTextProvider.rbClassLoader = new URLClassLoader(new URL[]{new URL(rbPath)});
        } // try
        catch (MalformedURLException e)
        {
            throw new MultilingualTextProviderRuntimeException ("A MalformedURLException occured during " +
                    "creation of class loader for resource bundle retrieval from path: " + rbPath);
        } // catch
    } // getRbClassLoader
    
    
    /**************************************************************************
     * Returns all configured locales without any rights check.
     *
     * @param   env    The environment.
     *
     * @return  The locales.
     */
    public static List<Locale_01> getAllLocales (Environment env)
    {        
        if (locales == null)
        {
            // Find object without rights check (necessary for preloading mechanism
            Vector<BusinessObjectInfo> objects = BOHelpers.findObjects(
                    "Locale",
                    new StringBuffer("o.containerId = ")
                            .append(BOHelpers.getOidByExtKey (
                                    LOCALES_CONTAINER_EXT_DOMAIN, LOCALES_CONTAINER_EXT_ID, env)),
                    env);

            locales = new ArrayList<Locale_01> ();
            
            // iterate through all locales and add them to the list
            for (Iterator<BusinessObjectInfo> iter = objects.iterator (); iter.hasNext ();)
            {
                BusinessObjectInfo info = (BusinessObjectInfo) iter.next ();

                // retrieve the locale object without rights check
                Locale_01 locale = (Locale_01) info.getObject (env);
                
                // check if locale is null - should never happen
                if (locale == null)
                {
                    // throw ml runtime exception
                    throw new MultilingualTextProviderRuntimeException (
                            "Error during retrieval of locale with oid: " + info.p_oid);
                } // if
                
                locales.add (locale);
            } // for iter
        } // if
        
        return locales;
    } // getLocales    

    
    /**************************************************************************
     * Returns the configured locales the logged on user has access rights for.
     *
     * @param   env    The environment.
     *
     * @return  The locales.
     */
    public static List<Locale_01> getLocales (Environment env)
    {        
        List<Locale_01> locales = new ArrayList<Locale_01> ();
        
        OID localesContainerOid = BOHelpers.getOidByExtKey (
                LOCALES_CONTAINER_EXT_DOMAIN, LOCALES_CONTAINER_EXT_ID, env);
        
        // get the locales container object:
        LocaleContainer_01 container = (LocaleContainer_01) BOHelpers.getObject (
                localesContainerOid, env, false, false, false);
        
        try
        {
            // get all the locales:
            container.retrieveContent (Operations.OP_NONE, 0, BOConstants.ORDER_ASC);
        } // try
        catch (NoAccessException e)
        {
            // cannot occur
        } // catch
        
        // iterate through all locales and add them to the list
        for (Iterator<ContainerElement> iter = container.elements.iterator (); iter.hasNext ();)
        {
            ContainerElement elem = (ContainerElement) iter.next ();

            Locale_01 localeElem = (Locale_01) BOHelpers.getObject (
                    elem.oid, env, false, false, false);
            
            locales.add (localeElem);
        } // for iter
        
        return locales;
    } // getLocales
    
    
    /**************************************************************************
     * Resets the locale cache.
     *
     * @param   env    The environment.
     */
    public static void resetLocaleCache (Environment env)
    {       
        locales = null;
    } // resetLocaleCache


    /**************************************************************************
     * Sets the default locale by iterating through all locales and checking
     * the isDefault flag.
     *
     * @param   env    The environment.
     */
    public static Locale_01 setDefaultLocale (Environment env)
    {      
        List<Locale_01> locales = MultilingualTextProvider.getAllLocales (env);

        // iterate through all locales and add them to the list
        for (Iterator<Locale_01> iter = locales.iterator (); iter.hasNext ();)
        {
            Locale_01 locale = (Locale_01) iter.next ();
            
            if (locale.isDefault ())
            {
                defaultLocale = locale;
                break;
            } // if
        } // for iter
        
        // check if no default locale is defined - should never occur
        if (defaultLocale == null)
        {
            // throw ml runtime exception
            throw new MultilingualTextProviderRuntimeException (
                    "No default locale set");
        } // if
        
        // Set the default locale within the Java API
        Locale.setDefault (defaultLocale.getLocale ());
        
        return defaultLocale;
    } // setDefaultLocale
    
    
    /**************************************************************************
     * Sets the given locale as default locale.
     *
     * @param   locale    Locale to set as default locale.
     */
    public static void setDefaultLocale (Locale_01 locale)
    {      
        MultilingualTextProvider.defaultLocale = locale;
        
        // Set the default locale within the Java API
        Locale.setDefault (MultilingualTextProvider.defaultLocale.getLocale ());
    } // setDefaultLocale
    
    
    /**************************************************************************
     * Returns the default locale.
     *
     * @return  The default locale.
     */
    public static Locale_01 getDefaultLocale ()
    {             
        // check if no default locale is defined - should never occur
        if (defaultLocale == null)
        {
            // throw ml runtime exception
            throw new MultilingualTextProviderRuntimeException ("The default bundle has not been intialized!" +
                "MultilingualTextProvider.setDefaultLocale has to be executed first!");
        } // if
        
        return defaultLocale;
    } // getDefaultLocale
    
    
    /**************************************************************************
     * Returns the locale for the currently logged on
     * user.
     *
     * @param   env     The environment. If no environment is provided the
     *                  default locale is returned.
     *
     * @return  The locale.
     */
    public static Locale_01 getUserLocale (Environment env)
    {
        Locale_01 retLocale = null;
        
        if (env != null && env.getUserInfo () != null && ((UserInfo) env.getUserInfo ()).userProfile != null &&
                ((UserInfo) env.getUserInfo ()).userProfile.getLocale () != null)
        {
            // Retrieve the user's locale
            retLocale = ((UserInfo) env.getUserInfo ()).userProfile.getLocale ();
        } // if
        else
        {
            retLocale = getDefaultLocale ();
        } // else
        
        return retLocale;
    } // getUserLocale
    
    
    /**************************************************************************
     * Resets the multilingual text cache.
     *
     * @param   env    The environment.
     * 
     * @throws MultilingualTextProviderRuntimeException
     *          thrown if an unexpected technical error occurs
     */
    public static void resetMultilingualTextCache (Environment env)
    {       
        ResourceBundle.clearCache (MultilingualTextProvider.getRbClassLoader ());
    } // resetMultilingualTextCache
    
    
    /**************************************************************************
     * Returns the resource bundle module separator.
     * 
     * @param   file    The source file of the source resource bundle. 
     */
    public static String getResourceBundleModuleHeader (File file, boolean addSeparator)
    {       
        StringBuilder sb = new StringBuilder();
        
        if (addSeparator)
        {
            sb.append (FileHelpers.getLineSeparator ());
            sb.append (FileHelpers.getLineSeparator ());
        } // if
        
        sb.append ("#########################################################").append (FileHelpers.getLineSeparator ());
        sb.append ("# Sourcefile: ").append (file.getName ()).append (FileHelpers.getLineSeparator ());
        sb.append ("#########################################################").append (FileHelpers.getLineSeparator ());

        return sb.toString ();
    } // getResourceBundleModuleSeparator
    
   
    /**************************************************************************
     * Returns base lookup key for a column name.
     * 
     * @param   typeCode    The form template's typecode. 
     * @param   columnName  The column name.
     */
    public static String getColumnBaseLookupKey (String typeCode, String columnName)
    {       
        String columnNameLocal = columnName.replace (" ", "_");
        
        StringBuilder sb = new StringBuilder().
            append (typeCode).
            append (MultilangConstants.LOOKUP_KEY_SEPARATOR).
            append (MultilangConstants.LOOKUP_KEY_PREFIX_COLUMN).
            append (columnNameLocal);

        return sb.toString ();
    } // getColumnBaseLookupKey
    
    
    /**************************************************************************
     * Returns the client multilang info filename for the provided user's
     * locale.
     * 
     * @param   locale     The locale to provide the filename for.
     */
    public static String getClientMultilangInfoFilename (Locale locale)
    {       
        StringBuilder sb = new StringBuilder ().
            append (MultilangConstants.CLIENT_ML_INFO_FILE_PREFIX).
            append (locale.toString ()).
            append (MultilangConstants.CLIENT_ML_INFO_FILE_POSTFIX);
        
        return sb.toString ();
    } // getClientMultilangInfoFilenameForUser
    
    
    /**************************************************************************
     * Returns the resource bundle name for the objects resource bundle
     * for the provided locale.
     * 
     * @param   locale     The locale to provide the resource bundle name for.
     */
    public static String getResourceBundleName (String bundle, Locale locale, boolean isTemporary)
    {       
        StringBuilder sb = new StringBuilder ().
            append (bundle);
        
        if (isTemporary)
        {
            sb.append (MultilangConstants.RESOURCE_BUNDLE_TEMP_POSTFIX);
        } // if
        
        sb.append (locale.toString ()).
           append (MultilangConstants.FILEEXT_RESOURCE_BUNDLE);
        
        return sb.toString ();
    } // getObjectsResourceBundleName

    
    /**************************************************************************
     * Reloades the multilang info (texts, messages, ...) for client.
     * 
     * @param   env     The environment holding the user's locale.
     */
    public static void reloadMultilangClientInfo (Environment env)
    {
        try
        {
            ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
            script.addScript ("top.reloadMultilang ();");
    
            StringBuffer buf = new StringBuffer ();
            script.build (env, buf);
            env.write (buf.toString ());
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e, env);
        } // catch
    } // reloadMultilangClientInfo
    
    
    /**************************************************************************
     * Returns lookup key for a query field with query name, prefix, key
     * 
     * @param   queryName   The form template's typecode. 
     * @param   mlKey       The value data elements field name.
     * @param   kindOfField Defines the postfix for the lookupKey
     */
    public static String getQueryGenericLookupKey (String queryName, 
        String mlKey, String kindOfField)
    {       
        String preparedQueryName = queryName.replace (" ", "_");
        String preparedMlKey = mlKey.replace (" ", "_");
        
        StringBuilder sb = new StringBuilder()
            .append (preparedQueryName)
            .append (MultilangConstants.LOOKUP_KEY_SEPARATOR)
            .append (MultilangConstants.LOOKUP_KEY_PREFIX_QUERY)
            .append (preparedMlKey)
            .append (MultilangConstants.LOOKUP_KEY_SEPARATOR)
            .append (kindOfField);

        return sb.toString ();
    } // getQueryGenericLookupKey
    
    
    /**************************************************************************
     * Returns lookup key for a query field with query name, prefix, key
     * 
     * @param   queryName   The form template's typecode. 
     * @param   mlKey       The value data elements field name.
     * @param   kindOfField Defines the postfix for the lookupKey
     */
    public static String getObjectLookupKey (String extId, String extIdDomain)
    {       
        StringBuilder sb = new StringBuilder()
            .append (extIdDomain)
            .append (MultilangConstants.LOOKUP_KEY_SEPARATOR)
            .append (extId);

        return sb.toString ();
    } // getQueryGenericLookupKey
    
    
    /**************************************************************************
     * Returns lookup key for formtemplate value data elements
     * (without name, description, ... postfix).
     * 
     * @param   typeCode    The form template's typecode. 
     * @param   fieldName   The value data elements field name.
     */
    public static String getFormtemplateVdeBaseLookupKey (String typeCode, String fieldName)
    {       
        String fieldNameLocal = fieldName.replace (" ", "_");
        
        StringBuilder sb = new StringBuilder().
            append (typeCode).
            append (MultilangConstants.LOOKUP_KEY_SEPARATOR).
            append (MultilangConstants.LOOKUP_KEY_PREFIX_VDE).
            append (fieldNameLocal);

        return sb.toString ();
    } // getFormtemplateVdeBaseLookupKey
    
    
    /**************************************************************************
     * Returns lookup key for tabs
     * (without name, description, ... postfix).
     * 
     * @param   tab    The tab. 
     */
    public static String getTabBaseLookupKey (Tab tab)
    {              
        StringBuilder sb = new StringBuilder().
            append (MultilangConstants.LOOKUP_KEY_PREFIX_TAB).    
            append (tab.getCode ());

        return sb.toString ();
    } // getTabBaseLookupKey
    
    
    /**************************************************************************
     * Returns lookup key for types
     * (without name, description, ... postfix).
     * 
     * @param   type    The type. 
     */
    public static String getTypeBaseLookupKey (Type type)
    {              
        StringBuilder sb = new StringBuilder().
            append (type.getCode ());

        return sb.toString ();
    } // getTypeBaseLookupKey
    
    
    /**************************************************************************
     * Returns the lookup key for the name based on the given base lookup key.
     * 
     * @param   baseKey    The base lookup key returned from
     *                     getTypeBaseLookupKey, ... methods. 
     */
    public static String getNameLookupKey (String baseKey)
    {       
        StringBuilder sb = new StringBuilder().
            append (baseKey).
            append (MultilangConstants.LOOKUP_KEY_SEPARATOR).
            append (MultilangConstants.LOOKUP_KEY_POSTFIX_NAME);

        return sb.toString ();
    } // getNameLookupKey
    
    
    /**************************************************************************
     * Returns the lookup key for the description based on the given base
     * lookup key.
     * 
     * @param   baseKey    The base lookup key returned from
     *                     getTypeBaseLookupKey, ... methods. 
     */
    public static String getDescriptionLookupKey (String baseKey)
    {       
        StringBuilder sb = new StringBuilder().
            append (baseKey).
            append (MultilangConstants.LOOKUP_KEY_SEPARATOR).
            append (MultilangConstants.LOOKUP_KEY_POSTFIX_DESCRIPTION);

        return sb.toString ();
    } // getDescriptionLookupKey
    

    /**************************************************************************
     * Returns the lookup key for the unit based on the given base lookup key.
     * 
     * @param   baseKey    The base lookup key returned from
     *                     getTypeBaseLookupKey, ... methods. 
     */
    public static String getUnitLookupKey (String baseKey)
    {       
        StringBuilder sb = new StringBuilder().
            append (baseKey).
            append (MultilangConstants.LOOKUP_KEY_SEPARATOR).
            append (MultilangConstants.LOOKUP_KEY_POSTFIX_UNIT);

        return sb.toString ();
    } // getUnitLookupKey

    
    /**************************************************************************
     * Returns the lookup key for a referenced field based on the given base
     * lookup key.
     * 
     * @param   baseKey     The base lookup key returned from
     *                      getFormtemplateVdeBaseLookupKey, ... methods. 
     * @param   reftypPrefix
     *                      The prefix for the ref typ.
     * @param   isSysField  Determines if it is a SYS field.
     * @param   refField    The name of the referenced field.
     */
    public static String getRefFieldLookupKey (String baseKey, String reftypPrefix,
            boolean isSysField, String refField)
    {       
        String refFieldLocal = refField.replace (" ", "_");
        
        StringBuilder sb = new StringBuilder().
            append (baseKey).
            append (MultilangConstants.LOOKUP_KEY_SEPARATOR).
            append (reftypPrefix);
        
        if (isSysField)
        {
            sb.append (MultilangConstants.LOOKUP_KEY_SYSFIELD_PREFIX);
        } // if
        
        sb.append (refFieldLocal);

        return sb.toString ();
    } // getRefFieldLookupKey
    
    
    /**************************************************************************
     * Retrieve the multilang text for the given mathcKey from a resource
     * bundle with pattern keys.
     * 
     * The method iterates through all entries of the given bundle and
     * checks if a pattern fits the matchKey.
     * 
     * The pattern is evaluated by using
     * <code>java.uti.regex.Pattern.matches(keyI, matchKey)</code>
     * so the pattern has to be defined as regular expression.
     *
     * Example bundle entry: ibs_instobj.wsp_inbox_.*=Eingangskorb
     * 
     * This method does not perform a fallback to a default bundle if no
     * pattern is found. So the bundle has to be preloaded by using the
     * <code>ibs.ml.export.ResourceBundlePreloader</code>.
     *
     * @param   patternBundle   The resource bundle which contains the patterns.
     * @param   locale          The locale to retrieve the bundle for.
     * @param   matchKey        The key to compare with the patterns within the
     *                          resource bundle.
     * @param   env             The environment. Only needed for showing messages.
     *                          If no env is provided error messages are logged via
     *                          print methods.
     *
     * @return  The multilingual text info.
     */
    public static MultilingualTextInfo getTextByPattern (String patternBundle, Locale_01 locale,
            String matchKey, Environment env)
    {    
        // Holds the multilang return text
        MultilingualTextInfo mlTextInfo = new MultilingualTextInfo ();
        mlTextInfo.setBundle (patternBundle);
        mlTextInfo.setLocale (locale);
        mlTextInfo.setMLKey (matchKey);
        
        // Holds the retrieved resource bundle
        ResourceBundle resourceBundle = null;
        
        // Retrieve the lookup locale
        Locale lookupLocale = getLookupLocale (locale);
        
        try
        {           
            // Retrieve the resource bundle
            resourceBundle = ResourceBundle.getBundle(
                patternBundle, 
                lookupLocale,
                MultilingualTextProvider.getRbClassLoader ());
        } // try
        catch (MissingResourceException e)
        {
            StringBuilder sb = new StringBuilder ().
                append ("No resource bundle found! Pattern Resource bundle: ").
                append (patternBundle).
//                append (", Key: ").
//                append (key).
                append (", Locale: ").
                append (MultilingualTextProvider.getDefaultLocale ().getLocale ()).
                append (". Please contact your administrator.");
            
            // can only occur if no default bundle exists
            if (env != null)
            {
                IOHelpers.showMessage (sb.toString (), false, env);
            } // if
            else
            {
                IOHelpers.printError (sb.toString (), e, false);
            } // else
            
            return null;
        } // catch
        
        // Iterate through all resource bundle keys (=patterns)
        Enumeration<String> keys = resourceBundle.getKeys ();
        while (keys.hasMoreElements ())
        {
            String keyI = keys.nextElement ();
            
            // check if the given key matches the actual pattern 
            if (Pattern.matches(keyI, matchKey))
            {
                // set the value for the current resource bundle entry
                mlTextInfo.setMLValue (MultilingualTextProvider.getEncodedString (resourceBundle.getString (keyI)));
                
                // set the state to found
                mlTextInfo.setState (MultilingualTextInfo.STATE_FOUND);
                               
                return mlTextInfo;
            } // if
        } // while
        
        // set the state to not found
        mlTextInfo.setState (MultilingualTextInfo.STATE_NOT_FOUND);
        
        return mlTextInfo;
    } // getTextByPattern
    
    
    /**************************************************************************
     * Retrieve the multilang text for a object by using the given
     * extId and extIdDomain.
     * 
     * This method uses MultilingualTextProvider.getTextByPattern () for
     * text retrieval.
     *
     * @param   extId           Ext id of the object.
     * @param   extIdDomain     Ext domain of the object.
     * @param   fallbackName    The fallback name.
     * @param   env             The environment.
     * 
     * @return  the multilang name
     */
    public static String getMultilangObjectName (String extId, String extIdDomain,
            String fallbackName, Environment env)
    {
        // Check if the ext key is properly defined
        if (extId == null || extId.isEmpty () || extIdDomain == null || extIdDomain.isEmpty ())
        {
            return fallbackName;
        } // if
        
        MultilingualTextInfo mlNameInfo =
            MultilingualTextProvider.getTextByPattern (
                    MultilangConstants.RESOURCE_BUNDLE_OBJECTS_NAME,
                    MultilingualTextProvider.getUserLocale (env),
                    MultilingualTextProvider.getObjectLookupKey (extId, extIdDomain),
                    env);
        
        // check if something has been found
        String mlName = mlNameInfo.isFound () ?
                // and use it
                mlNameInfo.getMLValue () :
                // fallback
                fallbackName;

        return mlName;
    } // getMultilangObjectName
    
} // class MultilingualTextProvider
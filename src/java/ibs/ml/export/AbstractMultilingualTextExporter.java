/*
 * Class: MultilingualTextExporter
 */

// package:
package ibs.ml.export;

// imports:
import ibs.io.Environment;
import ibs.ml.MultilingualTextInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.ml.MultilingualTextProviderException;
import ibs.obj.ml.Locale_01;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/******************************************************************************
 * This class provides basic functionality for a resource bundle exporter. <BR/>
 * 
 * @version $Id: MultilingualTextExporter.java,v 1.3 2010/03/23 15:56:37
 *          btatzmann Exp $
 * 
 * @author Roland Burgermann (RB)
 ****************************************************************************** 
 */
public abstract class AbstractMultilingualTextExporter
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag to
     * ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO = "$Id: AbstractMultilingualTextExporter.java,v 1.2 2010/04/28 10:02:56 btatzmann Exp $";

    /**************************************************************************
     * Perform the export of the texts. <BR/>
     * This class have to overridden in sub-class
     * 
     * @param   bundle      Name of the bundle for which the texts should be
     *                      exported.
     * @param   preProcess  Defines if preprocessing should be performed.   
     * @param   env         The environment.
     * 
     * @return Returns a collection with information about not found texts
     * 
     * @throws MultilingualTextProviderException
     * @throws MultilingualTextExporterException 
     */
    public Collection<String> performExportOfTexts (String bundle, boolean preProcess, Environment env) 
            throws MultilingualTextProviderException, MultilingualTextExporterException
    {
        // Collection for warnings which occurred during proccessing 
        Collection<String> warnings = new ArrayList<String> ();
        
        try
        {           
            // Get all multilingual texts out of the resource bundles
            Map<Locale, Collection<MultilingualTextInfo>> texts = getTextsFromBundle (bundle, warnings, env);
    
            if (preProcess)
            {
                // Perform pre processing
                preProcess (bundle, env);
            } // if
            
            // Export the multilingual texts to the databasetable
            exportTexts (bundle, texts, env);

            // Perform post processing
            postProcess (bundle, env);
        } // try
        catch (Exception e)
        {
            throw new MultilingualTextExporterException (e);
        } // catch
        
        return warnings;
    } // performExportOfTexts

    
    /**************************************************************************
     * Get all texts for all locales from a specified bundle.
     * 
     * @param bundle
     *            Name of the bundle from which the texts should be loaded.
     * @param warnings
     *            Contains a list of keys which where not found in a
     *            resourcebundle
     * @param env
     *            The environment.
     * 
     * @throws MultilingualTextProviderException
     */
    public Map<Locale, Collection<MultilingualTextInfo>> getTextsFromBundle(String bundle,
            Collection<String> warnings, Environment env)
            throws MultilingualTextProviderException
    {
        Collection<MultilingualTextInfo> localeTexts = new ArrayList<MultilingualTextInfo>();
        Map<Locale, Collection<MultilingualTextInfo>> texts =
            new HashMap<Locale, Collection<MultilingualTextInfo>> ();

        // Get defined locales from the system
        List<Locale_01> locales = MultilingualTextProvider.getAllLocales(env);

        // Get all keys from all locales for a specified bundle
        Collection<String> allKeys = MultilingualTextProvider.getKeysForBundle(
                bundle, locales, env);

        // Get texts for each defined locale
        for (Iterator<Locale_01> localeIterator = locales.iterator(); localeIterator
                .hasNext();)
        {
            Locale_01 localeI = localeIterator.next();
            
            localeTexts = getTextsFromBundle(bundle, warnings, localeI, allKeys, env);
            
            texts.put (localeI.getLocale (), localeTexts);
        } // for

        // return list with texts
        return texts;
    } // getTextsFromBundle


    /**************************************************************************
     * Get all texts for one specified locale from a specified resource bundle.
     * 
     * @param   bundle      Name of the bundle from which the texts should be loaded.
     * @param   warnings    Contains a list of keys which where not found in the specified
     *                      resource bundle
     * @param   locale      Locale for which the texts should be load
     * @param   allKeys     Collection which contains all keys from all locales for the
     *                      specified resource bundle
     * @param   env         The environment.
     * 
     * @throws MultilingualTextProviderException
     */
    public Collection<MultilingualTextInfo> getTextsFromBundle(String bundle,
            Collection<String> warnings, Locale_01 locale,
            Collection<String> allKeys, Environment env)
            throws MultilingualTextProviderException
    {
        Collection<MultilingualTextInfo> texts = new ArrayList<MultilingualTextInfo>();

        if (allKeys != null)
        {
            // Iterator over all keys and get the texts for the specified locale
            for (Iterator<String> keyIterator = allKeys.iterator(); keyIterator
                    .hasNext();)
            {
                String key = keyIterator.next();

                // Get the text from the specified resource bundle, key and
                // locale
                MultilingualTextInfo textInfo = MultilingualTextProvider
                        .getMultilingualTextInfo(bundle, key, locale, env);

                // Check if the text where not found in the resource bundle
                if (MultilingualTextInfo.STATE_NOT_FOUND == textInfo.getState())
                {
                    // When text not found add this entry to the warnings list
                    warnings.add(bundle + " - " + textInfo.getMLKey ()
                            + " - " + locale.getLocale().toString());
                } // if

                // Add the MultilingualText to the text list
                texts.add(textInfo);
            } // for
        } // if

        return texts;
    } // getTextsFromBundle

    
    /**************************************************************************
     * Perform necessary pre processing steps like deleting old files
     * or initializing tasks.
     * 
     * @param   bundle  Name of the bundle for which the texts should be
     *                  exported.
     * @param   env     The environment 
     * 
     * @throws MultilingualTextExporterException 
     */
    protected abstract void preProcess (String bundle, Environment env)
        throws MultilingualTextExporterException;
    

    /**************************************************************************
     * Perform necessary post processing steps like deleting temp files.
     * 
     * @param   bundle      Name of the bundle for which the texts should be
     *                      exported.
     * @param   env         The environment 
     * 
     * @throws MultilingualTextExporterException 
     */
    protected abstract void postProcess (String bundle, Environment env)
        throws MultilingualTextExporterException;
    
    
    /**************************************************************************
     * Export the multilang texts to the destination.
     * 
     * @param   bundle  Name of the bundle for which the texts should be
     *                  exported.
     * @param   texts   Map of all texts which should be exported.
     * @param   env     The environment
     *
     * @throws  MultilingualTextExporterException 
     */
    protected abstract void exportTexts (String bundle, Map<Locale, Collection<MultilingualTextInfo>> texts,
            Environment env)
        throws MultilingualTextExporterException;
} // class MultilingualTextExporter
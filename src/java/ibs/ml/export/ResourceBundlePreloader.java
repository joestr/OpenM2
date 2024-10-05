/*
 * Class: ResourceBundlePreloader
 */

// package:
package ibs.ml.export;

// imports:
import ibs.bo.BOPathConstants;
import ibs.di.DIConstants;
import ibs.io.Environment;
import ibs.ml.MultilangConstants;
import ibs.ml.MultilingualTextInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.ml.Locale_01;
import ibs.util.file.FileHelpers;
import ibs.util.file.FileManager;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;


/******************************************************************************
 * This class reads the entries of a resource bundles merged from all modules
 * and creates a new bundle for all available locales by respecting the
 * fallback mechanism to the default locale.<BR/>
 * 
 * This is necessary if the fallback mechanism does not want to be used during
 * runtime or it is not possible to use it.
 *
 * @version     $Id: ResourceBundlePreloader.java,v 1.2 2010/05/18 12:58:23 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann (BT)
 ******************************************************************************
 */
public class ResourceBundlePreloader extends AbstractMultilingualTextExporter
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ResourceBundlePreloader.java,v 1.2 2010/05/18 12:58:23 btatzmann Exp $";

    
    /**************************************************************************
     * Constructor which initialize the ResourceBundlePreloader.
     *
     */
    public ResourceBundlePreloader() 
    {
        super();
    }


    /**************************************************************************
     * Prepare a new resource bundle for every locale.
     *
     * @param   bundle      Name of the bundle for which the texts should be
     *                      exported.
     * @param   env         The environment 
     * 
     * @throws MultilingualTextExporterException 
     */
    protected void preProcess (String bundle, Environment env) throws MultilingualTextExporterException
    {                       
        // Iterate through all locales ...
        Iterator<Locale_01> localeIt = MultilingualTextProvider.getAllLocales (env).iterator ();
        
        while (localeIt.hasNext ())
        {
            Locale localeI = localeIt.next ().getLocale ();
            
            String destFilepath = getTargetDir (env) + MultilingualTextProvider.getResourceBundleName (bundle, localeI, true);
            
            // ... and make a copy of the template file for the locale
            boolean copy = FileHelpers.copyFile (
                    // source: resource bundle template file
                    getTargetDir (env) + MultilangConstants.RESOURCE_BUNDLE_TEMPLATE,
                    // destination: locale specific resource bundle
                    destFilepath);
            
            // check if file could be copied
            if (!copy)
            {
                throw new MultilingualTextExporterException ("Error during execution of ResourceBundlePreloader.preProcess:" +
                        " Resource bundle template file can not be copied. Check if the resource bundle template file exists. Destination file: " +
                        destFilepath);
            } // if
        } // while
    } // preProcess
    

    /**************************************************************************
     * Export the multilang texts to the resource bundles for all locales.
     * 
     * @param   bundle  Name of the bundle for which the texts should be
     *                  exported.
     * @param   texts   Map of all texts which should be exported.
     * @param   env     The environment
     *
     * @throws  MultilingualTextExporterException 
     */
    protected void exportTexts (String bundle, Map<Locale, Collection<MultilingualTextInfo>> texts, Environment env) throws MultilingualTextExporterException
    {
        try
        {
            // Iterate through all locales
            Iterator<Locale> localeIt = texts.keySet ().iterator ();
            
            while (localeIt.hasNext ())
            {
                Locale locale = localeIt.next ();
                               
                // Iterate through all texts
                Iterator<MultilingualTextInfo> textIterator = texts.get (locale).iterator ();
                
                StringBuilder textDeclarations = new StringBuilder ();
                
                // Add all texts as VALUES to the statement for export
                while (textIterator.hasNext ())
                {
                    MultilingualTextInfo text = textIterator.next();
                    
                    // Create the resource bundle entry for the current text                   
                    textDeclarations.
                        append (text.getMLKey ()).append ("=").
                        append (text.getMLValue ()).
                        append (FileHelpers.getLineSeparator ());
                } // while
                
                FileHelpers.sendToFile (textDeclarations.toString ().getBytes (DIConstants.CHARACTER_ENCODING),
                        // destination: locale specific resource bundle
                        getTargetDir (env) + MultilingualTextProvider.getResourceBundleName (bundle, locale, true),
                              true);
            } // while
        } // try
        catch (UnsupportedEncodingException e)
        {
            throw new MultilingualTextExporterException (e);
        } // catch
    } // exportTexts
    
    
    /**************************************************************************
     * Remove existing texts in the database.
     *
     * @param   bundle      Name of the bundle for which the texts should be
     *                      exported.
     * @param   env         The environment 
     * 
     * @throws MultilingualTextExporterException 
     */
    @Override
    protected void postProcess (String bundle, Environment env) throws MultilingualTextExporterException
    {        
        // Delete the original bundles
        FileHelpers.deleteFiles (new File (env.getApplicationInfo ().p_system.p_m2AbsBasePath + BOPathConstants.PATH_RESOURCE_BUNDLES),
                FileManager.getFilter ("*"),
                FileManager.getFilter ("*" + bundle + "*"),
                FileManager.getFilter ("*" + bundle + MultilangConstants.RESOURCE_BUNDLE_TEMP_POSTFIX + "*"),
                true);
        
        // Rename all temporary bundles
        FileHelpers.renameFiles (
                new File (env.getApplicationInfo ().p_system.p_m2AbsBasePath + BOPathConstants.PATH_RESOURCE_BUNDLES),
                FileManager.getFilter ("*"),
                FileManager.getFilter ("*" + bundle + "*"),
                bundle + MultilangConstants.RESOURCE_BUNDLE_TEMP_POSTFIX,
                bundle + "_",
                true);
    } // preProcess
    
    
    /***************************************************************************
     * Returns the target directory for the resource bundles.
     *
     * @param   env      The environment
     * @return  the filepath
     */
    private String getTargetDir (Environment env)
    {
        return env.getApplicationInfo ().p_system.p_m2AbsBasePath + BOPathConstants.PATH_RESOURCE_BUNDLES;
    } // getTargetDir
} // class ResourceBundlePreloader
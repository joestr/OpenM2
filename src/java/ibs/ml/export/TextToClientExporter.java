/*
 * Class: TextToClientExporter
 */

// package:
package ibs.ml.export;

// imports:
import ibs.app.AppConstants;
import ibs.bo.BOPathConstants;
import ibs.di.DIConstants;
import ibs.io.Environment;
import ibs.ml.MultilangConstants;
import ibs.ml.MultilingualTextInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.ml.Locale_01;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.file.FileHelpers;
import ibs.util.file.FileManager;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;


/******************************************************************************
 * TODO: This class provides the export of Multilingual Texts to ... <BR/>
 *
 * @version     $Id: TextToClientExporter.java,v 1.4 2010/05/18 12:57:51 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann (BT)
 ******************************************************************************
 */
public class TextToClientExporter extends AbstractMultilingualTextExporter
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TextToClientExporter.java,v 1.4 2010/05/18 12:57:51 btatzmann Exp $";

    
    /**************************************************************************
     * Constructor which initialize the TextToClientExporter.
     *
     */
    public TextToClientExporter () 
    {
        super();
    }

    
    /**************************************************************************
     * Remove existing files and new prepare files for all locales.
     *
     * @param   bundle  Name of the bundle for which the texts should be
     *                  exported.
     * @param   env     The environment 
     * 
     * @throws MultilingualTextExporterException 
     */
    protected void preProcess (String bundle, Environment env) throws MultilingualTextExporterException
    {        
        // Remove all old files from the folder
        FileHelpers.deleteFiles (
                new File (env.getApplicationInfo ().p_system.p_m2AbsBasePath + BOPathConstants.PATH_ABS_APP + BOPathConstants.PATH_MLI_CLIENT_TEXTS),
                FileManager.getFilter ("*"),
                FileManager.getFilter ("*"), true);
        
        // Create the target folder if not already existing
        FileHelpers.createDirectory (getTargetDir (env));
        
        // Iterate through all locales ...
        Iterator<Locale_01> localeIt = MultilingualTextProvider.getAllLocales (env).iterator ();
        
        while (localeIt.hasNext ())
        {
            Locale localeI = localeIt.next ().getLocale ();

            String ssiFile = env.getApplicationInfo ().p_system.p_m2AbsBasePath + BOPathConstants.PATH_ABS_APP +
                BOPathConstants.PATH_INCLUDE + AppConstants.SSI_MLICLIENTTEXTS;
            
            // check if the ssi file exists
            if (!FileHelpers.exists (ssiFile))
            {
                throw new MultilingualTextExporterException ("MLI client text SSI file does not exist: " +
                        ssiFile);
            } // if
            
            // ... and make a copy of the ssi file for the locale
            FileHelpers.copyFile (
                // source: ssi file
                ssiFile,
                // destination: locale specific mli client texts file 
                getTargetDir (env) +
                    MultilingualTextProvider.getClientMultilangInfoFilename (
                        localeI));
        } // while
    } // preProcess

    
    /***************************************************************************
     * Returns the target directory for mli client texts.
     *
     * @param   env      The environment
     * @return  the filepath
     */
    private String getTargetDir (Environment env)
    {
        return env.getApplicationInfo ().p_system.p_m2AbsBasePath + BOPathConstants.PATH_ABS_APP +
            BOPathConstants.PATH_MLI_CLIENT_TEXTS;
    } // getTargetDir


    /**************************************************************************
     * Export the multilang texts to the locale dependent client
     * mli text files.
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
                
                // get the Server side include file:
                String fileContent = FileHelpers.getContent (
                        getTargetDir (env) +
                        MultilingualTextProvider.getClientMultilangInfoFilename (
                                locale), DIConstants.CHARACTER_ENCODING); 
                
                // Iterate through all texts
                Iterator<MultilingualTextInfo> textIterator = texts.get (locale).iterator ();
                
                StringBuilder variableDeclarations = new StringBuilder ();
                
                // Create the header for the current bundle
                variableDeclarations.append ("/*Resource Bundle: ").
                    append (bundle).append ("*/").
                    append (FileHelpers.getLineSeparator ());
                
                // Add all texts as VALUES to the statement for export
                while (textIterator.hasNext ())
                {
                    MultilingualTextInfo text = textIterator.next();
                    
                    // Escape double quotes
                    String escapedText = StringHelpers.replace (text.getMLValue (),"\"", "\\\"");
                    //escapedText = StringHelpers.replace (text.getMLValue (),"\\", "\\\\");
                    
                    // Create the variable definition for the current text
                    variableDeclarations.append ("var ").
                        append (bundle).append (MultilangConstants.CLIENT_KEY_SEPARATOR).
                        append (text.getMLKey ()).append (" = ").
                        append ("\"").
                        append (escapedText).append ("\";").
                        append (FileHelpers.getLineSeparator ());
                } // while
                
                // Create a marker for the next resource bundle
                variableDeclarations.append (FileHelpers.getLineSeparator ()).
                    append ("/*").append (UtilConstants.TAG_MLITEXTS).append ("*/");
                
                fileContent = StringHelpers.replace (fileContent, "/*" + UtilConstants.TAG_MLITEXTS + "*/",
                        variableDeclarations.toString ());
                
                FileHelpers.sendToFile (fileContent.getBytes (DIConstants.CHARACTER_ENCODING),
                        // destination: locale specific mli client texts file 
                        env.getApplicationInfo ().p_system.p_m2AbsBasePath + BOPathConstants.PATH_ABS_APP +
                        BOPathConstants.PATH_MLI_CLIENT_TEXTS + File.separator + 
                              MultilingualTextProvider.getClientMultilangInfoFilename (
                                      locale),
                              false);
            } // while
        } // try
        catch (UnsupportedEncodingException e)
        {
            throw new MultilingualTextExporterException (e);
        } // catch
    } // exportTexts
    
    
    /**************************************************************************
     * Perform necessary post processing steps like deleting temp files.
     * 
     * @param   bundle      Name of the bundle for which the texts should be
     *                      exported.
     * @param   env         The environment 
     * 
     * @throws MultilingualTextExporterException 
     */
    @Override
    protected void postProcess (String bundle, Environment env)
            throws MultilingualTextExporterException
    {
        // nothing to do
    } // postProcess
} // class TextToClientExporter
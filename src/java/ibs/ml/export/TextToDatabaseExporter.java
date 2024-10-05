/*
 * Class: TextToDatabaseExporter
 */

// package:
package ibs.ml.export;

// imports:
import ibs.io.Environment;
import ibs.ml.MultilingualTextInfo;
import ibs.tech.sql.DBQueryException;
import ibs.tech.sql.DeleteStatement;
import ibs.tech.sql.InsertStatement;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;


/******************************************************************************
 * This class provides the export of Multilingual Texts to the database. <BR/>
 *
 * @version     $Id: TextToDatabaseExporter.java,v 1.4 2010/04/28 10:02:56 btatzmann Exp $
 *
 * @author      Roland Burgermann (RB)
 ******************************************************************************
 */
public class TextToDatabaseExporter extends AbstractMultilingualTextExporter
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TextToDatabaseExporter.java,v 1.4 2010/04/28 10:02:56 btatzmann Exp $";
    
    // The name of the table where the texts should be stored
    public static final String TABLENAME = "ibs_Multilang";
    
    // The columns of the multilang table
    public static final String TABLECOLS = "locale, bundle, mlKey, mlValue";
    
    // How many values should be inserted into the database within one insert statement 
    public static final int MAXIMUM_VALUES = 100;

    
    /**************************************************************************
     * Constructor which initialize the TextToDatabaseExporter.
     *
     */
    public TextToDatabaseExporter() 
    {
        super();
    }

    
    /**************************************************************************
     * Remove existing texts in the database.
     *
     * @param   bundle  Name of the bundle for which the texts should be
     *                  exported.
     * @param   env     The environment 
     * 
     * @throws MultilingualTextExporterException 
     */
    @Override
    protected void preProcess (String bundle, Environment env)
            throws MultilingualTextExporterException
    {
        // Create the delete statement for the specified databasetable
        DeleteStatement stmt = new DeleteStatement (TextToDatabaseExporter.TABLENAME, null);
        
        // Execute the statement to remove the multilingual texts from 
        // the specified databasetable
        try
        {
            stmt.execute();
        } // try
        catch (DBQueryException e)
        {
            throw new MultilingualTextExporterException (e);
        } // catch

    } // preProcess

    
    /**************************************************************************
     * Export texts with a select statement into the specified databasetable.
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
        // Iterate through all locales
        Iterator<Locale> localeIt = texts.keySet ().iterator ();
        
        while (localeIt.hasNext ())
        {
            Locale locale = localeIt.next ();
            
            // Iterate through all texts
            Iterator<MultilingualTextInfo> textIterator = texts.get (locale).iterator ();
            
            // Add all texts as VALUES to the statement for export
            while (textIterator.hasNext ())
            {
                MultilingualTextInfo text = textIterator.next();
    
                // Create insert statement for the multilingual texts 
                InsertStatement stmt = new InsertStatement (TextToDatabaseExporter.TABLENAME,
                        TextToDatabaseExporter.TABLECOLS, (String)null);
                
                // Add the values to the statement
                stmt.add (null, text.getLocale ().oid.toString ());
                stmt.add (null, "'" + text.getBundle () + "'");
                stmt.add (null, "'" + text.getMLKey () + "'");
                stmt.add (null, "'" + text.getMLValue () + "'");            
                
                // Execute the statement to insert the multilingual texts to the specified database table
                try
                {
                    stmt.execute();
                } // try
                catch (DBQueryException e)
                {
                    throw new MultilingualTextExporterException (e);
                } // catch
            } // while
        } // while
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
} // class TextToDatabaseExporter
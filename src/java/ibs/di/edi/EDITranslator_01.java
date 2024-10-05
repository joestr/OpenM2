/*
 * Class: EDITranslator_01.java
 */
/*
 * Created by IntelliJ IDEA.
 * User: kreimueller
 * Date: Feb 25, 2002
 * Time: 2:12:55 PM
 */

// package:
package ibs.di.edi;

// imports:
//TODO: unsauber
import ibs.bo.Datatypes;
//TODO: unsauber
import ibs.bo.OID;
import ibs.di.DIConstants;
import ibs.di.DataElement;
import ibs.di.Log_01;
import ibs.di.edi.EDIArguments;
import ibs.di.edi.EDITokens;
import ibs.di.trans.Translator_01;
//TODO: unsauber
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
//TODO: unsauber
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.StoredProcedure;
import ibs.util.file.FileHelpers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import de.mendelson.eagle.EagleException;
import de.mendelson.eagle.converter.edixml.EDIXMLConverter;
import de.mendelson.eagle.converter.edixml.XMLWriter;


/******************************************************************************
 * This class handles transformations from and to EDI. <BR/>
 * The method {@link #getSource(String, String, Log_01)} translates the edi
 * input to a xml file.
 *
 * @version     $Id: EDITranslator_01.java,v 1.12 2013/01/16 16:14:14 btatzmann Exp $
 *
 * @author      kreimueller, 011122
 ******************************************************************************
 */
public class EDITranslator_01 extends Translator_01
//    implements TranslatorInterface
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: EDITranslator_01.java,v 1.12 2013/01/16 16:14:14 btatzmann Exp $";


    /**
     * The file containing the filter for the edi transformation. <BR/>
     * This filter is applied to the original file before the translation
     * process starts.
     */
    private String p_filterFile = null;

    /**
     * The xslt file containing the format of the edi data. <BR/>
     * This file is used by the parser to translate the edi data to xml.
     */
    private String p_formatFile = null;

    /**
     * Field name: filter file. <BR/>
     */
    private static final String FIELD_FILTERFILE = "filterFile";
    /**
     * Field name: format file. <BR/>
     */
    private static final String FIELD_FORMATFILE = "formatFile";

    /**************************************************************************
     * Creates an EDITranslator_01 Object. <BR/>
     */
    public EDITranslator_01 ()
    {
        // call constructor of super class:
        super ();
    } // EDITranslator_01


    /**************************************************************************
     * Creates an EDITranslator_01 Object. <BR/>
     *
     * @param   oid     oid of the object.
     * @param   user    user that created the object.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public EDITranslator_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // EDITranslator_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set class specifics:
        super.initClassSpecifics ();

        // set specific procedures:
        this.procCreate =     "p_EDITranslator_01$create";
        this.procChange =     "p_EDITranslator_01$change";
        this.procRetrieve =   "p_EDITranslator_01$retrieve";
//        this.procDelete =     "p_Attachment_01$delete";
//        this.procDeleteRec =  "p_Attachment_01$delete";

        // set db table name:
        this.tableName = "ibs_EDITranslator_01";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters += 2;
        this.specificChangeParameters += 2;

        // initialize specific properties:
//        this.p_filterFile = "C:\\Daten\\eagle\\nolinebreaks.filter";
//        this.p_formatFile = "C:\\Daten\\eagle\\edifact.93.a.orders_kikalein.xml";
    } // initClassSpecifics


    /**************************************************************************
     * Read the data used in the Object. <BR/>
     */
    public void getParameters ()
    {
        String str = null;              // a string parameter's value

        // call corresponding method of super class:
        super.getParameters ();

        // filterFile:
        if ((str = this.getFileParamBOWithOid (EDIArguments.ARG_FILTERFILE,
            this.oid)) != null)
        {
            this.p_filterFile = str;
        } // if

        // formatFile:
        if ((str = this.getFileParamBOWithOid (EDIArguments.ARG_FORMATFILE,
            this.oid)) != null)
        {
            this.p_formatFile = str;
        } // if
    } // getParameters


    /**************************************************************************
     * Compute the full path and name of the xml source file. <BR/>
     *
     * @param   path        The path of the xml source file.
     * @param   fileName    The name of the xml source file.
     *
     * @return  The full path with file name.
     */
    public String computeXmlSourceFileName (String path, String fileName)
    {
        // compute the new file name and return it:
        return FileHelpers.getUniqueFileName (path, fileName + ".xml");
    } // computeXmlSourceFileName


    /**************************************************************************
     * Translate the original file into the xml structure which shall be
     * translated into the new xml structure. <BR/>
     *
     * @param   sourceFile  The path and name of the source file.
     * @param   xmlFile     The path and file of the destination file.
     * @param   log         The log to write translation messages to.
     *
     * @return  The intermediate xml document or <CODE>null</CODE> if it could
     *          not be created.
     */
    protected Source getSource (String sourceFile, String xmlFile, Log_01 log)
    {
        // parameters for the converter:
        String ediData = sourceFile;    // edi data file
        String outFile = xmlFile;       // output file
        String formatDescription = this.getAbsPath () + this.p_formatFile;
                                        // format description of edi data
        String xmlEncoding = "UTF-8";   // encoding to use to write the outgoing
                                        // XML file
        boolean debugToFile = false;    // debugging to file enabled?
        String debugFile = "./debug.txt"; // debugging file
        String filterName = this.getAbsPath () + this.p_filterFile;
                                        // name of the incoming filter to use
        Properties filter = null;       // the filter
        StringBuffer outText = null;    // output string
        // data streams for reading and writing files:
        PrintStream output = System.out; // output printstream for messages
        XMLWriter xmlWriter = null;     // xml output file writer
        FileInputStream fIStream = null; // edi input data stream
        FileReader formatFileReader = null; // format file reader
        EDIXMLConverter converter = null; // the converter
        long startTime;                 // the process start time
        Source retVal = null;           // the return value

        // log the transformation start:
        log.add (DIConstants.LOG_ENTRY, "Starting EDI to XML conversion.");
        startTime = System.currentTimeMillis ();

        // check if all necessary data exist:
        if (ediData == null || outFile == null || formatDescription == null)
        {
            log.add (DIConstants.LOG_ENTRY, "Missing mandatory parameter!");
        } // if

        // read the filter data:
        filter = this.readFilterData (filterName, log);

        // print used files:
        log.add (DIConstants.LOG_ENTRY, "Edi data: " + ediData);
        log.add (DIConstants.LOG_ENTRY, "Xml file: " + outFile);
        log.add (DIConstants.LOG_ENTRY, "Format file: " + formatDescription);
        log.add (DIConstants.LOG_ENTRY, "Filter file: " + filterName);
        if (debugToFile)
        {
            log.add (DIConstants.LOG_ENTRY,
                     "Debug info will be written to " + debugFile);
        } // if

        try
        {
            if (debugToFile)
            {
                output = new PrintStream (new FileOutputStream (debugFile));
            } // if

            // create the converter instance:
            converter = new EDIXMLConverter ();
            xmlWriter = new XMLWriter (new FileWriter (outFile));
            fIStream = new FileInputStream (ediData);
            formatFileReader = new FileReader (formatDescription);

            // perform the conversion:
            converter.start (output, fIStream, xmlWriter, formatFileReader,
                filter, xmlEncoding, debugToFile);
        } // try
        catch (EagleException e)
        {
            log.add (DIConstants.LOG_ENTRY,
                     "Error parsing EDI data: " + e.getMessage ());
        } // catch
        catch (Throwable e)
        {
            outText = new StringBuffer ("Conversion Exception: ");

            if (e.getMessage () != null)
            {
                outText.append (e.getMessage ());
            } // if
            outText.append (e.getClass ().getName ());
            log.add (DIConstants.LOG_ENTRY, outText.toString ());
        } // catch
        finally
        {
            // close the data streams:
            if (output != null)         // debug output stream set?
            {
                output.close ();
            } // if debug output stream set

            if (xmlWriter != null)  // xmlWriter set?
            {
                try
                {
                    xmlWriter.close ();
                } // try
                catch (IOException e)
                {
                    log.add (DIConstants.LOG_ENTRY,
                             "Error when closing XML Writer: " + e);
                } // catch
            } // if xmlWriter set

            if (fIStream != null)   // fIStream set?
            {
                try
                {
                    fIStream.close ();
                } // try
                catch (IOException e)
                {
                    log.add (DIConstants.LOG_ENTRY,
                             "Error when closing fIStream: " + e);
                } // catch
            } // if fIStream set

            if (formatFileReader != null) // formatFileReader set?
            {
                try
                {
                    formatFileReader.close ();
                } // try
                catch (IOException e)
                {
                    log.add (DIConstants.LOG_ENTRY,
                             "Error when closing formatFileReader: " + e);
                } // catch
            } // if formatFileReader set
        } // finally

        // log the conversion completion:
        log.add (DIConstants.LOG_ENTRY,
                 "EDI to XML conversion finished, running time: " +
                 (System.currentTimeMillis () - startTime) + " ms.");

        try
        {
            // return the computed file:
            retVal = new StreamSource (new FileInputStream (xmlFile));
        } // try
        catch (FileNotFoundException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch

        // return the result:
        return retVal;
    } // getSource


    /**************************************************************************
     * Read the data of the filter. <BR/>
     *
     * @param   filterName  Name of the incoming filter to use.
     * @param   log         The log to write translation messages to.
     *
     * @return  The filter input stream.
     */
    protected Properties readFilterData (String filterName, Log_01 log)
    {
        Properties filter = null;       // the filter
        FileInputStream filterIn = null; // filter input stream

        // read the filter data:
        if (filterName != null)
        {
            try
            {
                filterIn = new FileInputStream (filterName);
                filter = new Properties ();
                filter.load (new FileInputStream (filterName));
            } // try
            catch (Exception e)
            {
                log.add (DIConstants.LOG_ENTRY,
                         "Unable to load filter \'" + filterName + "\': " + e);
            } // catch
            finally
            {
                try
                {
                    if (filterIn != null)
                    {
                        // close the stream:
                        filterIn.close ();
                    } // if
                } // try
                catch (IOException e)
                {
                    log.add (DIConstants.LOG_ENTRY,
                        "Unable to close filter input stream \'" + filterName +
                        "\': " + e);
                } // catch
            } // finally
        } // if

        // return the result:
        return filter;
    } // readFilterData


    /**************************************************************************
     * Represent the properties of an object to the user. <BR/>
     *
     * @param   table       Table where the properties should be added.
     */
    protected void showProperties (TableElement table)
    {
        // display common properties:
        super.showProperties (table);

        // display the specific properties:
        this.showProperty (table, EDIArguments.ARG_FILTERFILE, 
            MultilingualTextProvider.getText (EDITokens.TOK_BUNDLE, 
                EDITokens.ML_FILTERFILE, env),
            Datatypes.DT_FILE, this.p_filterFile, this.path);
        this.showProperty (table, EDIArguments.ARG_FORMATFILE,  
            MultilingualTextProvider.getText (EDITokens.TOK_BUNDLE, 
                EDITokens.ML_FORMATFILE, env),
            Datatypes.DT_FILE, this.p_formatFile, this.path);
    } //  showProperties


    /**************************************************************************
     * Represent the properties of an object to the user within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        // display common properties:
        super.showFormProperties (table);

        // display the specific properties:
        this.showFormProperty (table, EDIArguments.ARG_FILTERFILE,  
            MultilingualTextProvider.getText (EDITokens.TOK_BUNDLE, 
                EDITokens.ML_FILTERFILE, env),
            Datatypes.DT_FILE, this.p_filterFile, "" + this.containerId);
        this.showFormProperty (table, EDIArguments.ARG_FORMATFILE,  
            MultilingualTextProvider.getText (EDITokens.TOK_BUNDLE, 
                EDITokens.ML_FORMATFILE, env),
            Datatypes.DT_FILE, this.p_formatFile, "" + this.containerId);
    } // showFormProperties


    /**************************************************************************
     * Displays the settings of the translator. <BR/>
     *
     * @param   table       Table where the settings shall be added.
     */
    public void showSettings (TableElement table)
    {
        // display common settings:
        super.showSettings (table);

        // display the specific settings:
        this.showProperty (table, EDIArguments.ARG_FILTERFILE,  
            MultilingualTextProvider.getText (EDITokens.TOK_BUNDLE, 
                EDITokens.ML_FILTERFILE, env),
            Datatypes.DT_FILE, this.p_filterFile, this.path);
        this.showProperty (table, EDIArguments.ARG_FORMATFILE,  
            MultilingualTextProvider.getText (EDITokens.TOK_BUNDLE, 
                EDITokens.ML_FORMATFILE, env),
            Datatypes.DT_FILE, this.p_formatFile, this.path);
    } // showSettings


    /**************************************************************************
     * Add the settings of the translator to a log. <BR/>
     *
     * @param   log     The log to add the settings to.
     */
    public void addSettingsToLog (Log_01 log)
    {
        // add common settings:
        super.addSettingsToLog (log);

        // add translator specific settings
        log.add (DIConstants.LOG_ENTRY,  
            MultilingualTextProvider.getText (EDITokens.TOK_BUNDLE, 
                EDITokens.ML_FILTERFILE, env) + ": " +
            this.p_filterFile, false);
        log.add (DIConstants.LOG_ENTRY,  
            MultilingualTextProvider.getText (EDITokens.TOK_BUNDLE, 
                EDITokens.ML_FORMATFILE, env) + ": " +
            this.p_formatFile, false);
    } // addSettingsToLog


    /**************************************************************************
     * Read the object data from a DataElement. <BR/>
     *
     * @param   dataElement The dataElement to read the data from.
     *
     * @see ibs.bo.BusinessObject#readImportData
     */
    public void readImportData (DataElement dataElement)
    {
        // get common values:
        super.readImportData (dataElement);

        // get the type specific values:
        if (dataElement.exists (EDITranslator_01.FIELD_FILTERFILE))
        {
            this.p_filterFile = dataElement
                .getImportStringValue (EDITranslator_01.FIELD_FILTERFILE);
        } // if
        if (dataElement.exists (EDITranslator_01.FIELD_FORMATFILE))
        {
            this.p_formatFile = dataElement
                .getImportStringValue (EDITranslator_01.FIELD_FORMATFILE);
        } // if
    } // readImportData


    /**************************************************************************
     * Write the object data into a DataElement. <BR/>
     *
     * @param   dataElement The dataElement to write the data to.
     *
     * @see ibs.bo.BusinessObject#writeExportData
     */
    public void writeExportData (DataElement dataElement)
    {
        // set common values:
        super.writeExportData (dataElement);

        // set the connector specific values:
        dataElement.setExportValue (EDITranslator_01.FIELD_FILTERFILE,
            this.p_filterFile);
        dataElement.setExportValue (EDITranslator_01.FIELD_FORMATFILE,
            this.p_formatFile);
    } // writeExportData


    ///////////////////////////////////////////////////////////////////////////
    // database functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performChangeData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the change data stored procedure.
     *
     * @param sp        The stored procedure to add the change parameters to.
     */
    @Override
    protected void setSpecificChangeParameters (StoredProcedure sp)
    {
        // initialize params index
        super.setSpecificChangeParameters (sp);

        // set the specific parameters:
        // filterFile:
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.p_filterFile);
        // formatFile:
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.p_formatFile);
    } // setSpecificChangeParameters


    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the retrieve data stored procedure.
     *
     * @param sp        The stored procedure the specific retrieve parameters
     *                  should be added to.
     * @param params    Array of parameters the specific retrieve parameters
     *                  have to be added to for beeing able to retrieve the
     *                  results within getSpecificRetrieveParameters.
     * @param lastIndex The index to the last element used in params thus far.
     *
     * @return  The index of the last element used in params.
     */
    @Override
    protected int setSpecificRetrieveParameters (StoredProcedure sp, Parameter[] params,
                                                 int lastIndex)
    {
        // initialize params index
        int i = super.setSpecificRetrieveParameters (sp, params, lastIndex);

        // set the specific parameters:
        // filterFile:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // formatFile:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


    /**************************************************************************
     * Get the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * type specific data from the retrieve data stored procedure.
     *
     * @param   params      The array of parameters from the retrieve data stored
     *                      procedure.
     * @param   lastIndex   The index to the last element used in params thus far.
     */
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        // read the parameter in the super class which
        // will be attachment_01
        super.getSpecificRetrieveParameters (params, lastIndex);

        // initialize params index:
        // Attachment_01 reads 7 parameters in its getSpecificRetrieveParameters
        // method, Translator_01 reads 1 parameter, therefore we have to add 8
        // to the params index
        int i = lastIndex + 8;

        // get the specific parameters:
        this.p_filterFile = params[++i].getValueString ();
        this.p_formatFile = params[++i].getValueString ();
    } // getSpecificRetrieveParameters

} // class EDITranslator_01

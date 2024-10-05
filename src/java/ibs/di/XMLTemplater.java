/*
 * Class: XMLTemplater.java
 */

/**
 * Created by IntelliJ IDEA.
 * User: mwassermann
 * Date: Dec 11, 2002
 * Time: 11:30:14 AM
 * To change this template use Options | File Templates.
 */

// package:
package ibs.di;

// imports:
import ibs.service.action.ActionConstants;
import ibs.util.DateTimeHelpers;
import ibs.util.StringHelpers;
import ibs.util.file.FileHelpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;


/******************************************************************************
 * This class.... <BR/>
 *
 * @version     $Id: XMLTemplater.java,v 1.9 2011/11/08 15:54:37 btatzmann Exp $
 *
 * @author      mwassermann, 11.12.2002
 ******************************************************************************
 */
public class XMLTemplater extends Thread
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XMLTemplater.java,v 1.9 2011/11/08 15:54:37 btatzmann Exp $";


    /**
     * working directory. <BR/>
     */
    public File p_workDir = null;

    /**
     * template name. <BR/>
     */
    public File p_template = null;

    /**
     * template string. <BR/>
     */
    public String p_templateStr = null;

    /**
     * deplay till next activation. <BR/>
     */
    public int p_delay = 60000;

    /**
     * XML file extension. <BR/>
     */
    private static String EXTENSION_XML = "xml";

    /**
     * template file extension. <BR/>
     */
    private static String EXTENSION_TEMPLATE = "templ";

    /**
     * debugging option. <BR/>
     */
    private static int p_debug = 0;


    /**************************************************************************
     * The main method to start the xml templater. <BR/>
     *
     * @param argv  the command line arguments array
     */
    public static void main (String[] argv)
    {
        String workDirPath = null;
        File workDir;
        String templatePath = null;
        File template;
        BufferedReader reader = null;
        String line;
        int i;
        InputStreamReader inputReader;
        XMLTemplater templater = new XMLTemplater ();


        System.out.println ();
        System.out.println (">>> Starting XML Templater ...");
        System.out.println ();

        if (XMLTemplater.p_debug > 9)
        {
            System.out.println ("argv.length: " + argv.length);
        } // if

        if (argv.length > 0)
        {
            for (int j = 0; j < argv.length; j++)
            {
                try
                {
                    if (argv [j].equalsIgnoreCase ("-WORKDIR"))
                    {
                        workDirPath = argv[++j];
                    } // if (argv [j].equalsIgnoreCase ("WORKDIR"))
                    else if (argv [j].equalsIgnoreCase ("-TEMPLATE"))
                    {
                        templatePath = argv[++j];
                    } // else if (argv [j].equalsIgnoreCase ("TEMPLATE"))
                    else if (argv [j].equalsIgnoreCase ("-DELAY"))
                    {
                        templater.p_delay = Integer.parseInt (argv[++j]) * 1000;
                    } // else if (argv [j].equalsIgnoreCase ("DELAY"))
                    else    // command line unknown
                    {
                        System.out.println ("ERROR: command line parameter unknown: " + argv [j]);
                        XMLTemplater.printUsage ();
                        System.exit (-1);
                    } // else command line unknown
                } // try
                catch (ArrayIndexOutOfBoundsException e)
                {
                    System.out.println ("ERROR: invalid command line parameter!");
                    XMLTemplater.printUsage ();
                    System.exit (-1);
                } // catch (ArrayIndexOutOfBoundsException e)
            } // for (int j=0; j < argv.length; j++

            // check constraints


            // set the working directory
            if (workDirPath == null)
            {
                System.out.println ("ERROR: WORKDIR not set!");
                XMLTemplater.printUsage ();
                System.exit (-1);
            } // if (workDirPath != null)

            // set the working directory
            if (templatePath == null)
            {
                System.out.println ("ERROR: TEMPLATE not set!");
                XMLTemplater.printUsage ();
                System.exit (-1);
            } // if (workDirPath != null)

            workDir = new File (workDirPath);
            // check the working directory
            if (!(workDir.exists () && workDir.isDirectory ()))
            {
                System.out.println ("ERROR: Invalid working directory: " + workDir);
                XMLTemplater.printUsage ();
                System.exit (-1);
            } // if (new File (p_workdir).isDirectory())
            else
            {
                templater.p_workDir = workDir;
            } // else

            // check if the template filename contains a path
            if (templatePath.indexOf (File.separator) == -1)
            {
                templatePath = workDir.getAbsolutePath () +
                    File.separator + templatePath;
            } // if
            // set the template file
            template = new File (templatePath);
            // check the template
            if (!(template.exists () && template.isFile ()))
            {
                System.out.println ("ERROR: Invalid template file: " + template);
                XMLTemplater.printUsage ();
                System.exit (-1);
            } // if (new File (p_workdir).isDirectory())
            else
            {
                templater.p_template = template;
            } // else

            // print settings
            templater.printSettings ();

            try
            {
                // read the template file
                System.out.println (">>> Reading template file:");
                templater.p_templateStr = "";
                reader = new BufferedReader (new InputStreamReader (
                        new FileInputStream (templater.p_template), DIConstants.CHARACTER_ENCODING));
                while ((line = reader.readLine ()) != null)
                {
                    if (XMLTemplater.p_debug > 9)
                    {
                        System.out.println (line);
                    } // if
                    templater.p_templateStr += line + "\n";
                } // while ((line = reader.readLine ()) != null)

                Thread templaterThread = new Thread (templater, "Templater");
                // do we need to start the agent as a deamon thread???
                templaterThread.setDaemon (false);
                // now start the thread
                templaterThread.start ();

                XMLTemplater.printMenu ();

                inputReader =  new InputStreamReader (System.in);
                while ((i = (char) inputReader.read ()) != 'x' && i != 'X')
                {
                    // check whether to print the agent setting
                    if (i == 's' || i == 'S')
                    {
                        templater.printSettings ();
                        XMLTemplater.printMenu ();
                    } // if (i == 's' || i == 'S')
                    // check whether to print the agent setting
                    else if (i == 'h' || i == 'H' || i == '?')
                    {
                        XMLTemplater.printUsage ();
                        XMLTemplater.printMenu ();
                    } // if (i == 'h' || i == 'H')
                    else if (i == 't' || i == 'T')
                    {
                        templater.printTemplate ();
                        XMLTemplater.printMenu ();
                    } // if (i == 't' || i == 'T')
                    // just for case a constant waiting for input consumes to much cpu time
                    Thread.sleep (1000);
                } // while ((i = (char) inputReader.read ()) != 'x' && i != 'X')
                System.out.println ();
                System.out.println (">>> Templater finished.");
                System.exit (0);
            } // try
            catch (IOException e)
            {
                System.out.println ("\r\nERROR: " + e.toString ());
                System.exit (-1);
            } // catch (IOException e)
            catch (InterruptedException e)
            {
                System.out.println ("\r\n" + e.toString ());
                System.exit (-1);
            } // catch (InterruptedException e)
        } // if (argv.length > 1)
        else    // invalid number of arguments given
        {
            // print usage
            System.out.println ("ERROR: Invalid number or arguments!");
            XMLTemplater.printUsage ();
            System.exit (-1);
        } // else invalid number of arguments given
    } // main


    /**************************************************************************
     * Implements the run method used for threads. <BR/>
     */
    public void run ()
    {
        // endless loop for agent
        while (!this.isInterrupted ())
        {
            try
            {
                System.out.println ("[" + DateTimeHelpers.dateTimeToString (new Date ()) +
                    "] Checking for files ...");
                // check for files
                this.createImportFiles ();
                // go to sleep
                System.out.println ("Sleeping for " +  (this.p_delay / 1000) + " seconds ...");
                Thread.sleep (this.p_delay);
            } // try
            catch (InterruptedException e)
            {
                // exit execution
                System.exit (-1);
            } // catch
        } // while (! isInterrupted ())
    } // run


    /**************************************************************************
     * Display the settings. <BR/>
     */
    private void printSettings ()
    {
        System.out.println ();
        System.out.println ("Settings:");
        System.out.println ("> templatefile: " + this.p_template.getAbsolutePath ());
/*
        System.out.println ("> placeholder : " + this.p_placeholder);
*/
        System.out.println ("> workdir     : " + this.p_workDir.getAbsolutePath ());
        System.out.println ("> delay       : " + (this.p_delay / 1000) + " seconds");
        System.out.println ();
    } // printSettings


    /**************************************************************************
     * Display the usage. <BR/>
     */
    private static void printUsage ()
    {
        System.out.println ("");
        System.out.println ("Usage:");
        System.out.println ("java " + XMLTemplater.class.getName () +
            " -WORKDIR <working directory>" +
            " -TEMPLATE <template file>" +
            " [-DELAY <delay in seconds. Default=60>]");
        System.out.println ("");
        System.out.println ("Use the following placeholders in the template file:");
        System.out.println (" #FILE#: replaced by filename including extension ");
        System.out.println (" #FILENAME#: replaced by filename without extension");
        System.out.println (" #FILEEXTENSION#: replaced by file extension");
        System.out.println (" #PATH#: replaced by path");
        System.out.println (" #FILEPATH#: replaced by path and file");
        System.out.println (" #SYSVAR.DATE#: replaced by actual date ");
        System.out.println (" #SYSVAR.TIME#: replaced by actual time");
        System.out.println (" #SYSVAR.DATETIME#: replaced by actual date and time ");
        System.out.println ("");
    } // printUsage


    /**************************************************************************
     * Display the template. <BR/>
     */
    private void printTemplate ()
    {
        System.out.println ("");
        System.out.println ("Template file:");
        System.out.println (this.p_templateStr);
        System.out.println ("");
    } // printTemplate


    /**************************************************************************
     * Prints the menu keys to control the agent. <BR/>
     */
    public static void printMenu ()
    {
        System.out.println ("");
        System.out.println ("Menu:");
        System.out.println ("'s' + <enter> ... print settings.");
        System.out.println ("'t' + <enter> ... print template.");
        System.out.println ("'h' + <enter> ... print usage.");
        System.out.println ("'x' + <enter> ... exit.");
        System.out.println ("");
    } // printMenu


    /**************************************************************************
     * Create the import files. <BR/>
     */
    private void createImportFiles ()
    {
        String importFileName;
        String [] files;
        File file;

        // get the files from the directory
        files = FileHelpers.getFilesArray (this.p_workDir.getAbsolutePath ());
        // any files found
        if (files != null && files.length > 0)
        {
            for (int i = 0; i < files.length; i++)
            {
                if (XMLTemplater.p_debug > 3)
                {
                    System.out.println ("files [i]: " + files [i]);
                } // if

                // check if this is a file not a directory
                file = new File (this.p_workDir.getAbsolutePath () +
                    File.separator + files [i]);
                if (file.isFile ())
                {
                    String fileExtension = files [i].substring (files [i].lastIndexOf (".") + 1);
                    // exclude xml and template files
                    if (!fileExtension.equals (XMLTemplater.EXTENSION_XML) &&
                        !fileExtension.equals (XMLTemplater.EXTENSION_TEMPLATE))
                    {
                        // set the import file name
                        importFileName = this.p_workDir.getAbsolutePath () +
                            files [i].substring (0, files [i].lastIndexOf (".") + 1) + XMLTemplater.EXTENSION_XML;
                        // check if file file exists
                        if (!FileHelpers.exists (importFileName))
                        {
//                            System.out.println ("creating XML File for " + files [i] + " ...");
                            this.createImportFile (files [i]);
                        } // if (!FileHelpers.exists (importFileName))
                    } // if (!files[i].substring(files[i].lastIndexOf(".") + 1)...
                    else    // file excluded
                    {
                        if (XMLTemplater.p_debug > 3)
                        {
                            System.out.println ("file ignored: " + files [i]);
                        } // if
                    } // else file excluded
                } // if (file.isFile ())
                else    // directory found
                {
                    if (XMLTemplater.p_debug > 3)
                    {
                        System.out.println ("directory found: " + files [i]);
                    } // if
                } // else directory found
            } // for (int i = 0; i < files.length; i++)
        } // if (files != null && files.length > 0)
        else
        {
            System.out.println ("no files found.");
        } // else
    } // createImportFiles


    /**************************************************************************
     * Generate the import file from the template. <BR/>
     *
     * @param   filename    The name of the file to process.
     */
    private void createImportFile (String filename)
    {
        if (XMLTemplater.p_debug > 0)
        {
            System.out.println ("> createImportFile (" + filename + ")");
        } // if

        File outputFile;
        OutputStreamWriter outputFileWriter;
        String outputFileStr;
        String outputFileName = this.p_workDir.getAbsolutePath () +
            File.separator + filename.substring (0, filename.lastIndexOf (".")) +
            "." + XMLTemplater.EXTENSION_XML;

        if (XMLTemplater.p_debug > 9)
        {
            System.out.println ("outputFileName: " + outputFileName);
        } // if

        try
        {
            // set the output file:
            outputFile = new File (outputFileName);

            // check if outputFileName already exists:
            if (outputFile.exists () && outputFile.isFile ())
            {
                if (XMLTemplater.p_debug > 1)
                {
                    System.out.println ("output file already exists: " +
                        outputFileName);
                } // if

                return;
            } // if

            // replace common:
            File inputFile = new File (filename);
            String fileName = XMLTemplater.escapeXMLCharacters (inputFile.getName ());
            int pos = fileName.lastIndexOf (".");
            String extension = "";
            String fileNameNoExt = fileName;
            String filePath = XMLTemplater.escapeXMLCharacters (inputFile.getAbsolutePath ());
            String path = filePath.substring (0, filePath.lastIndexOf (File.separator));

            if (pos > 0)
            {
                extension = fileName.substring (pos + 1);
                fileNameNoExt = fileName.substring (0, pos);
            } // if

            // insert the filename
            outputFileStr =
                StringHelpers.replace (this.p_templateStr, "#FILE#", fileName);
            // insert the filename without the extension
            outputFileStr =
                StringHelpers.replace (outputFileStr, "#FILENAME#", fileNameNoExt);
            // insert the extension
            outputFileStr =
                StringHelpers.replace (outputFileStr, "#FILEEXTENSION#", extension);
            // insert the path
            outputFileStr =
                StringHelpers.replace (outputFileStr, "#PATH#", path);
            // insert the path and filename
            outputFileStr =
                StringHelpers.replace (outputFileStr, "#FILEPATH#", filePath);

            // first check if the file contains any system variables
            if (outputFileStr.indexOf (ActionConstants.SYSVAR_PREFIX) != -1)
            {
                Date now = new Date ();

                // replace system variables DATETIME
                outputFileStr = StringHelpers.replace (outputFileStr,
                    ActionConstants.SYSVAR_PREFIX +
                    ActionConstants.SYSVAR_DATE +
                    ActionConstants.SYSVAR_TIME +
                    ActionConstants.VARIABLE_POSTFIX,
                    DateTimeHelpers.dateTimeToString (now));

                // replace system variables DATE
                outputFileStr = StringHelpers.replace (outputFileStr,
                    ActionConstants.SYSVAR_PREFIX +
                    ActionConstants.SYSVAR_DATE +
                    ActionConstants.VARIABLE_POSTFIX,
                    DateTimeHelpers.dateToString (now));

                // replace system variables TIME
                outputFileStr = StringHelpers.replace (outputFileStr,
                    ActionConstants.SYSVAR_PREFIX +
                    ActionConstants.SYSVAR_TIME +
                    ActionConstants.VARIABLE_POSTFIX,
                    DateTimeHelpers.timeToString (now));
            } // if (outputFileStr.indexOf (ActionConstants.SYSVAR_PREFIX) != -1)

            if (XMLTemplater.p_debug > 9)
            {
                System.out.println ("outputFileStr : " + outputFileStr);
            } // if

            // write the output file:
            outputFileWriter = new OutputStreamWriter(new FileOutputStream (outputFile), DIConstants.CHARACTER_ENCODING);

            outputFileWriter.write (outputFileStr, 0, outputFileStr.length ());
            outputFileWriter.close ();

            System.out.println (
                "import file generated: " + outputFile.getAbsolutePath ());
        } // try
        catch (IOException e)
        {
            System.out.println ("ERROR: " + e.toString ());
            System.exit (-1);
        } // catch
    } // createImportFile


    /**************************************************************************
     * Replace critical XML characters by their XML escape sequence. <BR/>
     *
     * @param   inputStr    The string to replace.
     *
     * @return  The string with escape sequences.
     */
    private static String escapeXMLCharacters (String inputStr)
    {
        String outputStr = inputStr;

        if (XMLTemplater.p_debug > 0)
        {
            System.out.println ("> private String escapeXMLCharacters (" + inputStr + ")");
        } // if

        outputStr = StringHelpers.replace (outputStr, "&", "&amp;");
        outputStr = StringHelpers.replace (outputStr, "<", "&lt;");
        outputStr = StringHelpers.replace (outputStr, "<", "&gt;");
        outputStr = StringHelpers.replace (outputStr, "\"", "&quot;");
        outputStr = StringHelpers.replace (outputStr, "'", "&apos;");

        if (XMLTemplater.p_debug > 0)
        {
            System.out.println ("< private String escapeXMLCharacters (" + outputStr + ")");
        } // if

        return outputStr;
    } // escapeXMLCharacters

} // XMLTemplater

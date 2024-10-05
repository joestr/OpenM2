/*
 * Class: m2XDiff.java
 */

// package:
package ibs.di.diff;

// imports:
import ibs.BaseObject;
import ibs.di.CommandLineException;
import ibs.di.DIConstants;
import ibs.di.DIMessages;
import ibs.di.DataElement;
import ibs.di.DataElementList;
import ibs.di.ReferenceDataElement;
import ibs.di.ReferencedObjectInfo;
import ibs.di.RightDataElement;
import ibs.di.ValueDataElement;
import ibs.io.IOHelpers;
import ibs.tech.xml.DOMHandler;
import ibs.tech.xml.XMLReader;
import ibs.tech.xml.XMLReaderException;
import ibs.tech.xml.XMLWriter;
import ibs.tech.xml.XMLWriterException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Vector;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


/******************************************************************************
 * This class is a standalone application which can also be called from
 * other m2 classes.This class takes two import xml files as input,compares
 * these two xml files and generates new import xml file.
 *
 * @version     $Id: m2XDiff.java,v 1.40 2013/01/15 14:48:29 rburgermann Exp $
 *
 * @author      CHINNI RANJITH KUMAR
 ******************************************************************************
 */
public class m2XDiff extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: m2XDiff.java,v 1.40 2013/01/15 14:48:29 rburgermann Exp $";


    /**
     * Holds the old file name. <BR/>
     */
    private String oldFile = null;

    /**
     * Holds the new file name. <BR/>
     */
    private String newFile = null;

    /**
     * Holds the resultant file name. <BR/>
     */
    private String outFile = "";

    /**
     * Holds the file name that contains deleted objects. <BR/>
     */
    private String delFile = "";

    /**
     * Holds a document instance used to create elements. <BR/>
     */
    private Document doc;

    /**
     * Holds the resultant file Document. <BR/>
     */
    private Document resultDoc = null;


    /**
     * Holds the deleted objects file Document. <BR/>
     */
    private Document deletedDoc = null;

    /**
     * Holds old import file DataElementList. <BR/>
     */
    private DataElementList[] oldFileDataElementList;

    /**
     * Holds new import file DataElementList. <BR/>
     */
    private DataElementList[] newFileDataElementList;

    /**
     * Holds resultant import file DataElementList. <BR/>
     */
    private DataElementList[] resultFileDataElementList;

    /**
     * Holds deleted objects from old file. <BR/>
     */
    private DataElementList[] delFileDataElementList;

    /**
     * Holds old import file DataElementList length. <BR/>
     */
    private int oldFileElementsLength;

    /**
     * flag for comparing rights. <BR/>
     */
    private boolean isCompareRights = false;

    /**
     * flag for compare grouped objects. <BR/>
     */
    private boolean isCompareGrouped = false;

    /**
     * flag for compare references. <BR/>
     */
    private boolean isCompareReferences = false;
    /**
     * flag for creating file with deleted objects. <BR/>
     */
    private boolean isDeleted = false;

    /**
     * flag for creating result file with an unique name. <BR/>
     */
    private boolean isUnique = false;

    /**
     * flag checks for commandline call
     */
    private boolean isCommandLine = false;

    /**
     * flag to enable debugging
     */
    private boolean isDebug = false;


    /**************************************************************************
     * Constructor for the m2XDiff class.
     */
    public m2XDiff ()
    {
        super ();
    } // m2XDiff


    /**************************************************************************
     * The main method will be executed when called from the operating
     * system. It reads the command line options and creates
     * an m2XDiff instance. <BR/>
     * Then it calls the function checkArguments to check for the
     * validity of command line arguments. <BR/>
     * After that it calls diff function to find the difference between
     * the two xml files and creates. <BR/>
     * a resultant xml file.
     *
     * @param   args    The string array with the command line arguments.
     */
    public static void main (String[] args)
    {
        long startTime = 0;
        long endTime = 0;
        String resultFileName = "";
        String resultPath = "";
        File resultFile;
        long resultObjects;
        String deletedFileName = "";
        String deletedPath = "";
        File deletedFile;
        long deletedObjects;

        // mark the start time
        startTime = System.currentTimeMillis ();

        try
        {
            // create an instance of the m2XDiff
            m2XDiff m2xdiff = new m2XDiff ();
            // set the flag to commandline call
            m2xdiff.isCommandLine = true;

            // check for the validity of the coomand line arguments
            m2xdiff.checkArguments (args);
            // create a file object of the result file
            resultFile = new File (m2xdiff.outFile);
            // get the path of the result file from file object
            resultPath = resultFile.getParent ();
            resultPath = m2XDiff.addEndingFileSeparator (resultPath);
            // get the name of the result file from File object
            resultFileName = resultFile.getName ();
            // check if unique option is enabled
            if (m2xdiff.isUnique)
            {
                // get a unique file name for result file
                resultFileName = m2xdiff.getUniqueFileName (resultPath, resultFileName);
            } // if (m2xdiff.isUnique)
            m2xdiff.outFile = resultPath + resultFileName;
            // check if deleted option is enabled
            if (m2xdiff.isDeleted)
            {
                // create a File object of deleted objects file
                deletedFile = new File (m2xdiff.delFile);
                // get the path
                deletedPath = deletedFile.getParent ();
                deletedPath = m2XDiff.addEndingFileSeparator (deletedPath);
                // get the file name
                deletedFileName = deletedFile.getName ();
                // get a unique name for the file holding deleted objects
                deletedFileName = m2xdiff.getUniqueFileName (deletedPath, deletedFileName);
                m2xdiff.delFile = deletedPath + deletedFileName;
            } // if (m2xdiff.isDeleted)

            // call the diff function for finding the difference between two import files
            m2xdiff.diff (m2xdiff.oldFile, m2xdiff.newFile, m2xdiff.outFile);

            // check if the result file DOM root is null
            if (m2xdiff.resultDoc == null)
            {
                // The two files have no difference as the doc root of the DOM tree for result file is null
                System.out.println ("> The inputfiles do not have any differences! No result file generated.");
            } // if (m2xdiff.doc == null)
            else    // generate result file
            {
                System.out.println (">>> Generate result file: " + resultPath + resultFileName);
                // serialize the DOM tree
                m2xdiff.writeDocument (m2xdiff.resultDoc, resultPath, resultFileName);
                // get the number of objects found in result file
                resultObjects = m2xdiff.countObjects (m2xdiff.resultFileDataElementList);
                // see if we have any objects in the resultant file and print the count
                System.out.println ("> " + resultObjects + " objects written into result file.");
            } // generate result file

            // check if deleted option is enabled
            if (m2xdiff.isDeleted)
            {
                // check if we have any deleted objects in the import file
                if (m2xdiff.deletedDoc == null)
                {
                    System.out.println ("> No deleted objects found! No file has been created.");
                } // if (m2xdiff.delDoc == null)
                else    // deleted objects found
                {
                    System.out.println (">>> Generate file containing deleted objects: " + deletedPath + deletedFileName);
                    // call the this function to serialize the DOM tree
                    m2xdiff.writeDocument (m2xdiff.deletedDoc, deletedPath, deletedFileName);
                    // count the number of objects
                    deletedObjects = m2xdiff.countObjects (m2xdiff.delFileDataElementList);
                       // print the number of objects being deleted in case there are any
                    System.out.println ("> " + deletedObjects + " objects written into file.");
                } // if (m2xdiff.delDoc != null)
            } // if (m2xdiff.isDeleted)

            // print the process time of the comparison
            endTime = System.currentTimeMillis ();
            m2XDiff.printProcessTime (startTime, endTime);
            // show the end message
            System.out.println (">>> m2XDiff finished successfully.\r\n");
        } // try
        // Any command line arguments anamoly is handled here
        catch (CommandLineException e)
        {
            // show usage of the command line arguments
            m2XDiff.showUsage (e.getMessage ());
        } // catch
        // parser errors will be handled here
        catch (XMLReaderException e)
        {
            System.out.println ("\r\n\r\n>>> PARSER ERROR: " + e.toString ());
        } // catch
        // runtime exception like OutOfMemory errors will be handled here
        catch (Exception e)
        {
            System.out.println ("\r\n\r\n>>> ABORT: " + e.toString ());
        } // catch
    } // main


    /**************************************************************************
     * This method checks for the validity of command line arguments.
     *
     * @param   args []      the string array with the command line arguments
     *
     * @exception  CommandLineException
     *        This exception will be thrown for wrong command line parameters
     */
    public void checkArguments (String [] args)
        throws CommandLineException
    {
        // see if we have commandline arguments
        if (args.length > 0)
        {
            // loop through the arguments
            for (int i = 0; i < args.length; i++)
            {
                if (args [i].equals (m2XDiffConstants.ARG_HELP))
                {
                    throw new CommandLineException (m2XDiffConstants.ARG_HELP);
                } // if (args [i].equals (m2XDiffConstants.ARG_HELP))
                else if (args [i].equals (m2XDiffConstants.ARG_OLD))
                {
                    // check anymore arguments exists
                    if (i + 1 < args.length)
                    {
                        this.oldFile = args [++i].trim ();
                        // check for the validity of the file name
                        File oldFile = new File (this.oldFile);
                        this.checkFileName (oldFile.getName ());
                        continue;
                    } // if (i+1 <= args.length)

                    throw new CommandLineException (m2XDiffMessages.MISSING_OLD);
                } // if (args [i].equals (m2XDiffConstants.ARG_OLD))
                else if (args [i].equals (m2XDiffConstants.ARG_NEW))
                {
                    // check anymore arguments exists
                    if (i + 1 < args.length)
                    {
                        this.newFile = args [++i].trim ();
                        // check for the validity of the file name
                        File newFile = new File (this.newFile);
                        this.checkFileName (newFile.getName ());
                        continue;
                    } // if (i+1 <= args.length)

                    throw new CommandLineException (m2XDiffMessages.MISSING_NEW);
                } // if (args [i].equals (m2XDiffConstants.ARG_NEW))
                else if (args [i].equals (m2XDiffConstants.ARG_OUT))
                {
                    // check anymore arguments exists
                    if (i + 1 < args.length)
                    {
                        this.outFile = args [++i].trim ();
                        // check for the validity of the file name
                        File outFile = new File (this.outFile);
                        this.checkFileName (outFile.getName ());
                        continue;
                    } // if (i+1 < args.length

                    throw new CommandLineException (m2XDiffMessages.MISSING_OUT);
                } // if (args [i].equals (m2XDiffConstants.ARG_OUT))
                else if (args [i].equals (m2XDiffConstants.ARG_GROUPED))
                {
                    // enable the grouped option
                    this.isCompareGrouped  = true;
                    continue;
                } // if (args [i].equals (m2XDiffConstants.ARG_GROUPED))
                else if (args [i].equals (m2XDiffConstants.ARG_RIGHTS))
                {
                    // enable the rights option
                    this.isCompareRights   = true;
                    continue;
                } // if (args [i].equals (m2XDiffConstants.ARG_RIGHTS))
                else if (args [i].equals (m2XDiffConstants.ARG_REFERENCES))
                {
                    // enable the references option
                    this.isCompareReferences  = true;
                    continue;
                } // if (args [i].equals (m2XDiffConstants.ARG_DELETED))
                else if (args [i].equals (m2XDiffConstants.ARG_DELETED))
                {
                    this.isDeleted = true;
                    continue;
                } // if (args [i].equals (m2XDiffConstants.ARG_DELETED))
                else if (args [i].equals (m2XDiffConstants.ARG_UNIQUE))
                {
                    this.isUnique = true;
                    continue;
                } // if (args [i].equals (m2XDiffConstants.ARG_UNIQUE))
                else if (args [i].equals (m2XDiffConstants.ARG_DEBUG))
                {
                    this.isDebug = true;
                    continue;
                } // if (args [i].equals (m2XDiffConstants.ARG_DEBUG))
                else  // if wrong argument found
                {
                    throw new CommandLineException (m2XDiffMessages.WRONG_PARAMS);
                } // else
            } // for (int i = 0; i < args.length; i++)
        } // if (args.length > 0)
        else    // arguments doesn't exists
        {
            throw new CommandLineException (m2XDiffMessages.NO_PARAMS);
        } // else
        // check if we have minimum required inputs
        this.setDefaults ();
    } // checkArguments


    /**************************************************************************
     * Function provides a default name for the resultant file if it doesn't
     * exists in the command line. <BR/>
     */
    private void setDefaults ()
    {
        // check if we got the resultant file name
        if (this.outFile.length () == 0)
        {
            // extract the new file name
            this.outFile = this.newFile.substring (0, this.newFile.lastIndexOf (m2XDiffConstants.EXTENSION_XML));
            // attach the _new.xml to the new file name
            this.outFile += m2XDiffConstants.EXTENSION_NEW;
        } // if (this.outFile.length () == 0)
        // check if the deleted ooption is enabled
        if (this.isDeleted)
        {
            // extract the new file name
            this.delFile = this.newFile.substring (0, this.newFile.lastIndexOf (m2XDiffConstants.EXTENSION_XML));
            // attach the _del.xml to the new file name
            this.delFile += m2XDiffConstants.EXTENSION_DEL;
        } // if (this.isDeleted)
    } // setDefaults


    /**************************************************************************
     * Function checks for the validity of a filename. <BR/>
     * A file must have the .xml extension. <BR/>
     *
     * @param fileName  String containing the name of the file entered
     *                  in the command line
     *
     * @exception  CommandLineException
     *                This exception is thrown for wrong entering wrong file name
     */
    private void checkFileName (String fileName)
        throws CommandLineException
    {
        // check if the file name we got has .xml extension
        if (fileName.lastIndexOf (m2XDiffConstants.EXTENSION_XML) == -1)
        {
            throw new CommandLineException (m2XDiffMessages.MISSING_EXTENSION);
        } // if (file.lastIndexOf (m2XDiffConstants.EXTENSION_XML) == -1)
    } // checkFileName


    /**************************************************************************
     * Checks if a file already exists and adds a number to the beginning of
     * the file until there can be found no file anymore with this filename. <BR/>
     *
     * @param   path        the file path
     * @param   fileName    the name of the file to test
     *
     * @return a string with the new unique fileName
     */
    public String getUniqueFileName (String path, String fileName)
    {
        String pathLocal = path;        // variable for local assignments
        String newFileName = fileName;
        int counter = 1;

        // check if the path is null
        if (pathLocal == null)
        {
            pathLocal = "";
        } // if (path.length () > 0)

        // try to find an unique filename
        while (new File (pathLocal + newFileName).exists ())
        {
            newFileName = counter++ + "_" + fileName;
        } // while (File.exists (destinationPath + newFileName))
        return newFileName;
    } // getUniqueFileName


    /**************************************************************************
     * Function which shows the usage of command line arguments.
     *
     * @param   errorMessage   String containing the error message
     */
    private static void showUsage (String errorMessage)
    {
        System.out.println ();
        if (!errorMessage.equals (m2XDiffConstants.ARG_HELP))
        {
            System.out.println ("\r\n>>> ABORT: " + errorMessage);
        } // if ( !errorMessage.equals (m2XDiffConstants.ARG_HELP))
        System.out.println ("\r\n");
        System.out
            .println ("Usage: java ibs.di.m2XDiff -old <oldfile> -new <newfile>" +
                "  [-out <resultfile>] [-deleted] [-grouped] [-unique]" +
                "  [-rights] [-references]   [-?]");
        System.out.println ("-old <oldfile> .... name of the old importfile");
        System.out.println ("-new <newfile> .... name of the new importfile");
        System.out.println ("-out <resultfile> . name of the resultfile. Default: <new file>_new.xml");
        System.out.println ("-deleted .... generate file with deleted objects named \"<new file>_del.xml\"");
        System.out.println ("-grouped .... handle grouped objects as single object");
        System.out.println ("-unique ..... ensure unique name for resultfiles (avoids overwriting)");
        System.out.println ("-rights ..... include comparing rights definitions");
        System.out.println ("-references . include comparing references definition");
        System.out.println ("-? .......... display this help");
        System.exit (1);
    } // showUsage


    /**************************************************************************
     * Function which prints the settings. <BR/>
     */
    private void printSettings ()
    {
        System.out.println ();
        System.out.println (">>> m2XDiff started with the following settings:");
        System.out.println ("> old input file       : " + this.oldFile);
        System.out.println ("> new input file       : " + this.newFile);
        System.out.println ("> resulting file       : " + this.outFile);
        // delete file setting
        if (this.isDeleted)
        {
            System.out.println ("> deleted objects file : " + this.delFile);
        } // if
        // delete option setting
        if (this.isDeleted)
        {
            System.out.println ("> deleted option enabled");
        } // if
        else
        {
            System.out.println ("> deleted option disabled");
        } // else
        // grouped option setting
        if (this.isCompareGrouped)
        {
            System.out.println ("> grouped option enabled");
        } // if
        else
        {
            System.out.println ("> grouped option disabled");
        } // else
        // unique option setting
        if (this.isUnique)
        {
            System.out.println ("> unique option enabled");
        } // if
        else
        {
            System.out.println ("> unique option disabled");
        } // else
        // compare rights option setting
        if (this.isCompareRights)
        {
            System.out.println ("> rights option enabled");
        } // if
        else
        {
            System.out.println ("> rights option disabled");
        } // else
        // compare reference option setting
        if (this.isCompareReferences)
        {
            System.out.println ("> references option enabled");
        } // if
        else
        {
            System.out.println ("> references option disabled");
        } // else
    } // printSettings


    /**************************************************************************
     * Displays a progress bar. <BR/>
     *
     * @param  num     integer  holding the progress percentage
     */
    public void printProgressBar (int num)
    {
        int i = num / 2;

        String percentage = "" + num + "%  ";
        // print the percentage of process over
        for (int m = 1; m < 10 - percentage.length (); m++)
        {
            System.out.print (" ");
        } // for m
        System.out.print (percentage);
        System.out.print ("[");
        // print a '=' char for every % finished
        for (int k = 1; k <= i; k++)
        {
            System.out.print ("=");
        } // for k
        // fill up the rest with "." char
        for (int j = i; j < 50; j++)
        {
            System.out.print (".");
        } // for j
        System.out.print ("]");
        // brings the cursor to start of the line
        for (int p = 1; p <= 100; p++)
        {
            System.out.print ("\b");
        } // for p
    } // printProgressBar


    /**************************************************************************
     * Displays the time the process took to finish. <BR/>
     *
     * @param startTime     the time the process started in milliseconds
     * @param endTime       the time the process finished in milliseconds
     */
    public static void printProcessTime (long startTime, long endTime)
    {

        long minutes = 0;
        long seconds = 0;

        seconds = (int) (endTime - startTime) / 1000;
        // check i fprocess took longer then a minute
        if (seconds < 60)
        {
            System.out.println ("> Total processing time: " + seconds * 60 + " second(s).");
        } // if (temp < 1)
        else        // process took longer then a minute
        {
            minutes = (int) seconds / 60;
            seconds = seconds - (minutes * 60);
            System.out.print ("> Total processing time: " + minutes + " minute(s) ");
            if (seconds > 0)
            {
                System.out.println (seconds + " second(s).");
            } // if
            else
            {
                System.out.print ("\r\n");
            } // else
        } // else process took longer then a minute
    } // printProcessTime


    /**************************************************************************
     * This function parses the files and creates the DOM tree and stores
     * the values in DataElementList then it compares the two lists and makes
     * a new xml file. <BR/>
     *
     * @param   oldFile      String containing the old import file name
     * @param   newFile      String containing the new import file name
     * @param   resultFile   String containing the result import file name
     *
     * @throws  CommandLineException
     *          This exception will be thrown for passing wrong file names as parameters
     * @throws  XMLReaderException
     *          In case a XML parser occurred.
     */
    public void diff (String oldFile, String newFile, String resultFile)
        throws CommandLineException, XMLReaderException
    {
        try
        {
            // check if we have name of the importFile
            if (oldFile == null || newFile == null || resultFile == null ||
                oldFile.length () == 0 || newFile.length () == 0 ||
                resultFile.length () == 0)
            {
                throw new CommandLineException (m2XDiffMessages.WRONG_FILE_NAMES);
            } // if (oldFile == null || newFile == null || resultFile == null || ...

            // file names ok
            // check if it is a call from main function
            if (this.isCommandLine)
            {
                // print the settings
                this.printSettings ();
                System.out.println (">>> Start reading input files...");
            } // if (this.isCommandLine)

            // parse the old file
            this.oldFileDataElementList = this.parseFile (oldFile);
            this.oldFileElementsLength = this.oldFileDataElementList.length;
            // check if it is a call from main function
            if (this.isCommandLine)
            {
                // print how many objects have been found in the old file
                long oldObjects = 0;
                oldObjects = this.countObjects (this.oldFileDataElementList);
                System.out.println ("> " + oldObjects + " objects found in old file.");
            } // if (this.isCommandLine)

            // parse the new file
            this.newFileDataElementList = this.parseFile (newFile);

            // check if it is a call from main function
            if (this.isCommandLine)
            {
                // print how many objects have been found in the new file
                long newObjects = 0;
                newObjects = this.countObjects (this.newFileDataElementList);
                System.out.println ("> " + newObjects + " objects found in new file.");

                // print the object count in old and new file
                System.out.println ("> ... finished reading input files.");
                System.out.println (">>> Start comparison process...");
            } // if (this.isCommandLine)
            // check if the grouped objects option is disabled or enabled
            if (!this.isCompareGrouped)
            {
                this.compare ();
            } // if
            else
            {
                this.compareGrouped ();
            } // else

            // check if it is a call from main function
            if (this.isCommandLine)
            {
                System.out.println ("\r\n> ... comparison finished.");
                System.out.println (">>> Create resulting XML documents ...");
            } // if (this.isCommandLine)
            this.resultDoc = this.createDocument (this.resultFileDataElementList);
            // check if the a document should be created for deleted objects
            if (this.isDeleted)
            {
                this.deletedDoc = this.createDocument (this.delFileDataElementList);
            } // if (this.isDeleted)
            // check if it is a call from main function
            if (this.isCommandLine)
            {
                System.out.println ("> ... finished.");
            } // if
        } // try
        catch (XMLReaderException e)
        {
            throw e;
        } // try
    } // diff


    /**************************************************************************
     * Function counts number of objects in a DataElementList array
     *
     * @param dataElementList    DataElementList array containing the objects
     *
     * @return  Total number of objects.
     */
    public long countObjects (DataElementList [] dataElementList)
    {
        long count = 0;
        if (dataElementList != null)
        {
            for (int i = 0; i < dataElementList.length; i++)
            {
                if (dataElementList[i] != null)
                {
                    count += dataElementList[i].dataElements.size ();
                } // if
            } // for (int i = 0; i < dataElementList.length; i++)
        } // if (dataElementList != null)
        return count;
    } // countObjects


    /**************************************************************************
     * Function for serializing the DOM tree.
     *
     * @param   doc         The document containing the dom tree to write.
     * @param   path        String containing the path
     * @param   fileName    String containing the file name
     *
     * @throws  Exception
     *          This exception is thrown if any IOException or serilization
     *          exception occurs.
     */
    public void writeDocument (Document doc, String path, String fileName)
        throws Exception
    {
        File file;

        try
        {
            // create the file for the output:
            file = new File (path + fileName);
            // open file output stream:
            FileOutputStream fileOutputStream = new FileOutputStream (file);
            // serialize the dom tree:
            DOMHandler.serializeDOM (doc, fileOutputStream, true, null);
            // close the stream:
            fileOutputStream.close ();
        } // try
        catch (Exception exception)
        {
            throw exception;
        } // catch
    } // writeDocument


    /**************************************************************************
     * Checks if a path end with a valid file separator and add one in case it
     * is missing. This method does nothing in case the path already ends
     * with the appropriate file separator. <BR/>
     *
     * @param   path    the path to test
     *
     * @return a path with an valid file separator at the end
     */
    public static String addEndingFileSeparator (String path)
    {
        if (path == null)
        {
            return "";
        } // if

        String pathLocal = path;        // variable for local assignments

        // path not null
        if (!pathLocal.endsWith (File.separator))
        {
            pathLocal += File.separator;
        } // if

        return pathLocal;
    } // addEndingFileSeparator


    /**************************************************************************
     * Creates the DOM tree out of an dataElementList array. <BR/>
     *
     * @param dataElementList   an array with DataElementLists
     *
     * @return  The resulting xml document.
     */
    public Document createDocument (DataElementList [] dataElementList)
    {
        boolean isFoundChanged = false;
        Document doc = null;
        Element root;

        // check if we got a valid array
        if (dataElementList != null)
        {
            // check if we have any changed objects
            for (int i = 0; i < dataElementList.length; i++)
            {
                if (dataElementList [i] != null)
                {
                    isFoundChanged = true;
                    break;
                } // if (this.resultFileDataElementList [i] != null)
            } // for (int i = 0; i < this.dataElementList.length; i++)
            if (isFoundChanged)
            {
                try
                {
                    // create the doc root for the DOM tree:
                    doc = XMLWriter.createDocument ();
                } // try
                catch (XMLWriterException e)
                {
                    System.out.println (e.toString ());
                    return doc;
                } // catch

                // <IMPORT>
                root = doc.createElement (DIConstants.ELEM_IMPORT);
                // add the <IMPORT VERSION="1.0"> attribute
                root.setAttribute (DIConstants.ATTR_VERSION, DIConstants.DOCUMENT_VERSION);
                // add the root element to the xml document
                doc.appendChild (root);
                // set the global doc that is used in the methods to create
                // element nodes. this is not very proper!
                this.doc = doc;
                // loop through the DataElementList
                for (int i = 0; i < dataElementList.length; i++)
                {
                    // check if DataElementList is null
                    if (dataElementList [i] != null)
                    {
                        // add a <OBJECTS> section
                        root.appendChild (this.addObjects (dataElementList [i]));
                    } // if (this.resultFileDataElementList [i] != null)
                } // for (int i = 0; i < this.resultFileDataElementList.length; i++)
            } // if (isFoundChanged)
        } // if (dataElementList != null)
        else    // dataElementList is null
        {
            this.showDebug ("dataElementList is null");
        } // else
        // return the resulting document
        return doc;
    } // createDocument


    /**************************************************************************
     * Function creates the OBJECTS tag part of the resultant DOM tree.
     *
     * @param   dataElementList   DataElementList containing the OBJECTS tag details
     *
     * @return  The OBJECTS node within the dom tree.
     */
    private Element addObjects (DataElementList dataElementList)
    {
        // <OBJECTS>
        Element objectsNode = this.doc.createElement ("OBJECTS");
        Element object;
        // get the vector of DataElements out of DataElementList
        Vector<DataElement> dataElementVector = dataElementList.dataElements;
        // loop through the vector
        for (int i = 0; i < dataElementVector.size (); i++)
        {
            DataElement dataElement = dataElementVector.elementAt (i);
            if (dataElement != null)
            {
                // <OBJECT>
                object = this.addObject (dataElement);
                // add the <OBJECT> tag to <OBJECTS>
                objectsNode.appendChild (object);
            } // if (dataElement != null)
        } // for (int i = 0; i < dataElementVector.size (); i++)
        return objectsNode;
    } // addObjects


    /**************************************************************************
     * Function creates the OBJECT tag part of the resultant DOM tree.
     *
     * @param   dataElement   DataElement containing the OBJECT tag details
     *
     * @return  The OBJECT node within the dom tree.
     */
    private Element addObject (DataElement dataElement)
    {
        // <OBJECT>
        Element objectNode = this.doc.createElement (DIConstants.ELEM_OBJECT);

        String typeCode = dataElement.p_typeCode;
        // set the type code attribute
        if (typeCode != null && !typeCode.isEmpty ())
        {
            objectNode.setAttribute (DIConstants.ATTR_TYPECODE, typeCode);
        } // if (typeCode != null && typeCode.length () > 0)
        else
        {
            // set the type name attribute:
            if (dataElement.typename != null)
            {
                objectNode.setAttribute (DIConstants.ATTR_TYPE, dataElement.typename);
            } // if
        } // else if (typeCode != null && typeCode.length () > 0)

        // <SYSTEM>
        Element system = this.addSystem (dataElement);
        if (system != null)
        {
            objectNode.appendChild (system);
        } // if
        // <VALUES>
        Element values = this.addValues (dataElement);
        if (values != null)
        {
            objectNode.appendChild (values);
        } // if
        // <RIGHTS>
        Element rights = this.addRights (dataElement);
        if (rights != null)
        {
            objectNode.appendChild (rights);
        } // if
        // <TABS>
        Element tabs = this.addTabs (dataElement);
        if (tabs != null)
        {
            objectNode.appendChild (tabs);
        } // if
        // <REFERENCES>
        Element references = this.addReferences (dataElement);
        if (references != null)
        {
            objectNode.appendChild (references);
        } // if

        return objectNode;
    } // addObject


    /**************************************************************************
     * Function creates the TABS tag part of the resultant DOM tree.
     *
     * @param   dataElement   DataElement containing the TABS tag details
     *
     * @return  The TABS node within the dom tree.
     */
    private Element addTabs (DataElement dataElement)
    {
        Element tabsNode = null;
        // get the tabListElement out of DataElementList
        DataElementList dataElementList = dataElement.tabElementList;
        Vector<DataElement> dataElementVector = null;
        if (dataElementList == null)
        {
            return tabsNode;
        } // if

        // get the DataElement vector out of DataElementList
        dataElementVector = dataElementList.dataElements;
        // check if we have any DataElements
        if (dataElementVector.size () > 0)
        {
            // <TABS>
            tabsNode = this.doc.createElement ("TABS");
            // loop through the DataElement vector
            for (int i = 0; i < dataElementVector.size (); i++)
            {
                DataElement element = dataElementVector.elementAt (i);
                // <TABOBJECT>
                Element tabObjectNode = this.doc.createElement ("TABOBJECT");
                // add the <TABOBJECT> to <TABS>
                tabsNode.appendChild (tabObjectNode);
                String type = element.typename;
                if (type != null)
                {
                    tabObjectNode.setAttribute ("TYPE", type);
                } // if
                // <SYSTEM>
                Element systemNode = this.addSystem (element);
                if (systemNode != null)
                {
                    tabObjectNode.appendChild (systemNode);
                } // if
                // <VALUES>
                Element valuesNode = this.addValues (element);
                if (valuesNode != null)
                {
                    tabObjectNode.appendChild (valuesNode);
                } // if
                // <RIGHTS>
                Element rightsNode = this.addRights (element);
                if (rightsNode != null)
                {
                    tabObjectNode.appendChild (rightsNode);
                } // if
                // <CONTENT>
                //Element contentNode = addContent (element);
                //if (contentNode != null)
                    //tabObjectNode.appendChild (contentNode);
            } // for (int i = 0; i < dataElementVector.size (); i++)
        } // if (dataElementVector.size () > 0)
        return  tabsNode;
    } // addTabs


    /**************************************************************************
     * Function creates the REFERENCES tag part of the resultant DOM tree.
     *
     * @param   dataElement   DataElement containing the REFERENCES tag details
     *
     * @return  The REFERENCES node within the dom tree.
     */
    private Element addReferences (DataElement dataElement)
    {
        Element referencesNode = null;
        // get the vector out of DataElement
        Vector<ReferenceDataElement> references = dataElement.references;
        // check if we have any elements
        if (references.size () > 0)
        {
            // <REFERENCES>
            referencesNode = this.doc.createElement ("REFERENCES");
            // loop through the vector
            for (int i = 0; i < references.size (); i++)
            {
                // get the ReferenceDataElement out of the vector
                ReferenceDataElement ref = references.elementAt (i);
                // <CONTAINER>
                Element container = this.doc.createElement ("CONTAINER");
                container.setAttribute ("TYPE", ref.containerType);
                // check if the <TABNAME> attribute has to be set
                if (ref.containerTabName != null && ref.containerTabName.length () > 0)
                {
                    container.setAttribute ("TABNAME", ref.containerTabName);
                } // if (ref.containerTabName != null && ref.containerTabName.length () > 0)
                // check if the container type is EXTKEY
                // in that case the structure looks like
                // <CONTAINER TYPE="EXTKEY">
                //    <ID DOMAIN=""></ID>
                // </CONTAINER>
                if (ref.containerType.equals (DIConstants.CONTAINER_EXTKEY))
                {
                    Element id = this.doc.createElement ("ID");
                    id.setAttribute ("DOMAIN", ref.containerIdDomain);
                    id.appendChild (this.doc.createTextNode (ref.containerId));
                } // if (ref.containerType.equals (DIConstants.CONTAINER_EXTKEY))
                else
                {
                    container.appendChild (this.doc.createTextNode (ref.containerId));
                } // else
                // add the container to the reference node
                referencesNode.appendChild (container);
            } // for (int i = 0; i < references.size (); i++)
        } // if (references.size () > 0)
        return referencesNode;
    } // addReferences


    /**************************************************************************
     * Function creates the SYSTEM tag part of the resultant DOM tree.
     *
     * @param   dataElement   DataElement containing the SYSTEM tag details
     *
     * @return  The SYSTEM node within the dom tree.
     */
    private Element addSystem (DataElement dataElement)
    {
        // <SYSTEM>
        Element systemNode = this.doc.createElement (DIConstants.ELEM_SYSTEM);
        // <ID>
        Element idNode = this.doc.createElement (DIConstants.ELEM_ID);
        // add the <ID> node to <SYSTEM>
        systemNode.appendChild (idNode);
        String idDomain = dataElement.idDomain;
        idNode.setAttribute (DIConstants.ATTR_DOMAIN, idDomain);
        String id = dataElement.id;
        idNode.appendChild (this.doc.createTextNode (id));
        // <NAME>
        Element nameNode = this.doc.createElement (DIConstants.ELEM_NAME);
        // add the <NAME> node to <SYSTEM>
        systemNode.appendChild (nameNode);
        String name = dataElement.name;
        nameNode.appendChild (this.doc.createTextNode (name));
        // <DESCRIPTION>
        Element descriptionNode = this.doc.createElement (DIConstants.ELEM_DESCRIPTION);
        // add the <DESCRIPTION> node to <SYSTEM>
        systemNode.appendChild (descriptionNode);
        String description = dataElement.description;
        descriptionNode.appendChild (this.doc.createTextNode (description));
        // <VALIDUNTIL>
        Element validUntilNode = this.doc.createElement (DIConstants.ELEM_VALIDUNTIL);
        // add the <VALIDUNTIL> node to <SYSTEM>
        systemNode.appendChild (validUntilNode);
        String validUntil = dataElement.validUntil;
        validUntilNode.appendChild (this.doc.createTextNode (validUntil));
        // <SHOWINNEWS>
        if (dataElement.showInNews != null && dataElement.showInNews.length () > 0)
        {
            // create the <SHOWINNEWS> node
            Element showInNewsNode = this.doc.createElement (DIConstants.ELEM_SHOWINNEWS);
            // add the <SHOWINNEWS> to <SYSTEM>
            systemNode.appendChild (showInNewsNode);
            showInNewsNode.appendChild (this.doc.createTextNode (dataElement.showInNews));
        } // if (showInNews != null)
        // <CONTAINER>
        if (dataElement.containerId != null && dataElement.containerId.length () > 0)
        {
            // create the <CONTAINER> node
            Element containerNode = this.doc.createElement (DIConstants.ELEM_CONTAINER);
            // add the <CONTAINER> to <SYSTEM>
            systemNode.appendChild (containerNode);
            containerNode.setAttribute ("TYPE", dataElement.containerType);
            // check if the <TABNAME> attribute has to be set
            if (dataElement.containerTabName != null && dataElement.containerTabName.length () > 0)
            {
                containerNode.setAttribute ("TABNAME", dataElement.containerTabName);
            } // if (ref.containerTabName != null && ref.containerTabName.length () > 0)
            // check if the container type is EXTKEY
            // in that case the structure looks like
            // <CONTAINER TYPE="EXTKEY">
            //    <ID DOMAIN=""></ID>
            // </CONTAINER>
            if (dataElement.containerType.equals (DIConstants.CONTAINER_EXTKEY))
            {
                Element containerIdNode = this.doc.createElement ("ID");
                containerIdNode.setAttribute ("DOMAIN", dataElement.containerIdDomain);
                containerIdNode.appendChild (this.doc.createTextNode (dataElement.containerId));
                containerNode.appendChild (containerIdNode);
            } // if (ref.containerType.equals (DIConstants.CONTAINER_EXTKEY))
            else
            {
                containerNode.appendChild (this.doc.createTextNode (dataElement.containerId));
            } // else
        } // if (container != null)
        return systemNode;
    } // addSystem


    /**************************************************************************
     * Function creates the VALUES tag part of the resultant DOM tree.
     *
     * @param   dataElement   DataElement containing the VALUES tag details
     *
     * @return  The VALUES node within the dom tree.
     */
    private Element addValues (DataElement dataElement)
    {
        Element valuesNode = null;
        // get the vector out of DataELement
        Vector<ValueDataElement> values = dataElement.values;

        if (values != null)
        {
            // <VALUES>
            valuesNode = this.doc.createElement ("VALUES");
            // now loop through the vector
            for (int i = 0; i < values.size (); i++)
            {
                // get the ValueDataElement out of the vector
                ValueDataElement valueDataElement = values.elementAt (i);
                String valueName = valueDataElement.field;
                String valueType = valueDataElement.type;
                String valueValue = valueDataElement.value;
                String valueMandatory = valueDataElement.mandatory;
                String valueReadonly = valueDataElement.p_readonly;
                String valueInfo = valueDataElement.info;
                String valueTypeFilter = valueDataElement.typeFilter;
                String valueSearchRoot = valueDataElement.searchRoot;
                String valueSearchRootIdDomain = valueDataElement.searchRootIdDomain;
                String valueSearchRootId = valueDataElement.searchRootId;
                String valueSearchRecursive = valueDataElement.searchRecursive;
                String valueContext = valueDataElement.p_context;
                String valueViewType = valueDataElement.viewType;
                String valueNoColumns = valueDataElement.noColumns;
                String valueMultiSelection = valueDataElement.multiSelection;
                String valueMlKey = valueDataElement.mlKey;
                String valueShowInLinks = valueDataElement.p_showInLinks;

                // <VALUE>
                Element valueNode = this.doc.createElement (DIConstants.ELEM_VALUE);
                if (valueName != null)
                {
                    valueNode.setAttribute (DIConstants.ATTR_FIELD, valueName);
                } // if
                if (valueType != null)
                {
                    valueNode.setAttribute (DIConstants.ATTR_TYPE, valueType);
                } // if
                if (valueSearchRoot != null)
                {
                    valueNode.setAttribute (DIConstants.ATTR_SEARCHROOT, valueSearchRoot);
                } // if
                if (valueSearchRootIdDomain != null)
                {
                    valueNode.setAttribute (DIConstants.ATTR_SEARCHROOTIDDOMAIN, valueSearchRootIdDomain);
                } // if
                if (valueSearchRootId != null)
                {
                    valueNode.setAttribute (DIConstants.ATTR_SEARCHROOTID, valueSearchRootId);
                } // if
                if (valueSearchRecursive != null)
                {
                    valueNode.setAttribute (DIConstants.ATTR_SEARCHRECURSIVE, valueSearchRecursive);
                } // if
                if (valueTypeFilter != null)
                {
                    valueNode.setAttribute (DIConstants.ATTR_TYPECODEFILTER, valueTypeFilter);
                } // if
                if (valueMandatory != null)
                {
                    valueNode.setAttribute (DIConstants.ATTR_MANDATORY, valueMandatory);
                } // if
                if (valueReadonly != null)
                {
                    valueNode.setAttribute (DIConstants.ATTR_READONLY, valueReadonly);
                } // if
                if (valueInfo != null)
                {
                    valueNode.setAttribute (DIConstants.ATTR_INFO, valueInfo);
                } // if
                if (valueValue != null)
                {
                    valueNode.appendChild (this.doc.createTextNode (valueValue));
                } // if
                if (valueContext != null)
                {
                    valueNode.setAttribute (DIConstants.ATTR_CONTEXT, valueContext);
                } // if
                if (valueViewType != null)
                {
                    valueNode.setAttribute (DIConstants.ATTR_VIEWTYPE, valueViewType);
                } // if
                if (valueNoColumns != null)
                {
                    valueNode.setAttribute (DIConstants.ATTR_NO_COLUMNS, valueNoColumns);
                } // if
                if (valueMultiSelection != null)
                {
                    valueNode.setAttribute (DIConstants.ATTR_MULTISELECTION, valueMultiSelection);
                } // if
                if (valueMlKey != null)
                {
                    valueNode.setAttribute (DIConstants.ATTR_MLKEY, valueMlKey);
                } // if
                if (valueShowInLinks != null)
                {
                    valueNode.setAttribute (DIConstants.ATTR_SHOWINLINKS, valueShowInLinks);
                } // if

                // add attributes for reminder:
                this.addAttributeValue (valueNode, DIConstants.ATTR_DISPLAY,
                    valueDataElement.p_displayType);
                this.addAttributeValue (valueNode, DIConstants.ATTR_REMIND1DAYS,
                    valueDataElement.p_remind1Days);
                this.addAttributeValue (valueNode, DIConstants.ATTR_REMIND1TEXT,
                    valueDataElement.p_remind1Text);
                this.addAttributeValue (valueNode, DIConstants.ATTR_REMIND1RECIP,
                    valueDataElement.p_remind1Recip);
                this.addAttributeValue (valueNode,
                    DIConstants.ATTR_REMIND1RECIPQUERY,
                    valueDataElement.p_remind1RecipQuery);
                this.addAttributeValue (valueNode, DIConstants.ATTR_REMIND2DAYS,
                    valueDataElement.p_remind2Days);
                this.addAttributeValue (valueNode, DIConstants.ATTR_REMIND2TEXT,
                    valueDataElement.p_remind2Text);
                this.addAttributeValue (valueNode, DIConstants.ATTR_REMIND2RECIP,
                    valueDataElement.p_remind2Recip);
                this.addAttributeValue (valueNode,
                    DIConstants.ATTR_REMIND2RECIPQUERY,
                    valueDataElement.p_remind2RecipQuery);
                this.addAttributeValue (valueNode, DIConstants.ATTR_ESCALATEDAYS,
                    valueDataElement.p_escalateDays);
                this.addAttributeValue (valueNode, DIConstants.ATTR_ESCALATETEXT,
                    valueDataElement.p_escalateText);
                this.addAttributeValue (valueNode, DIConstants.ATTR_ESCALATERECIP,
                    valueDataElement.p_escalateRecip);
                this.addAttributeValue (valueNode,
                    DIConstants.ATTR_ESCALATERECIPQUERY,
                    valueDataElement.p_escalateRecipQuery);

                // add the <VALUE> to <VALUES>
                valuesNode.appendChild (valueNode);
            } // for (int i = 0; i < values.size (); i++)
        } // if (values != null)
        return valuesNode;
    } // addValues


    /**************************************************************************
     * Add the value of an attribute to a value. <BR/>
     * If the value is <CODE>null</CODE> it is not set.
     *
     * @param   valueNode   Value node.
     * @param   attrName    Name of the attribute.
     * @param   value       Value for the attribute.
     */
    protected void addAttributeValue (Element valueNode,
                                      String attrName, String value)
    {
        if (value != null)
        {
            valueNode.setAttribute (attrName, value);
        } // if
    } // addAttributeValue


    /**************************************************************************
     * Add the value of an attribute to a value. <BR/>
     * If the value is <CODE>null</CODE> it is not set.
     *
     * @param   valueNode   Value node.
     * @param   attrName    Name of the attribute.
     * @param   value       Value for the attribute.
     */
    protected void addAttributeValue (Element valueNode,
                                      String attrName, int value)
    {
        valueNode.setAttribute (attrName, Integer.toString (value));
    } // addAttributeValue


    /**************************************************************************
     * Function creates the RIGHTS tag part of the resultant DOM tree.
     *
     * @param   dataElement   DataElement containing the RIGHTS tag details
     *
     * @return  The RIGHTS node within the dom tree.
     */
    private Element addRights (DataElement dataElement)
    {
        Element rightsNode = null;
        // get the vector out of the DataElement
        Vector<RightDataElement> right = dataElement.rights;
        // see if we have any elements
        if (right.size () > 0)
        {
            // <RIGHTS>
            rightsNode =  this.doc.createElement ("RIGHTS");
            // now loop through the vector
            for (int i = 0; i < right.size (); i++)
            {
                // get a RightDataElement out of the vector
                RightDataElement rightElement = right.elementAt (i);
                // get the profiles vector out of RightDataElement
                Vector<String> profiles = rightElement.profiles;
                String name = rightElement.name;
                String type = rightElement.type;
                // loop through the profiles vector
                for (int j = 0; j < profiles.size (); j++)
                {
                    String profileString = profiles.elementAt (j);
                    // <RIGHT>
                    Element rightNode = this.doc.createElement ("RIGHT");
                    rightNode.setAttribute ("NAME", name);
                    rightNode.setAttribute ("TYPE", type);
                    rightNode.setAttribute ("PROFILE", profileString);
                    // add <RIGHT> to <RIGHTS>
                    rightsNode.appendChild (rightNode);
                } // for (int j = 0; j < profiles.size (); j++)
            } // for (int i = 0; i < right.size (); i++)
        } // if (right.size () > 0)
        return rightsNode;
    } // addRights


    /**************************************************************************
     * Function compares the two DOM trees of old and new file
     * with grouped option disabled and stores the changed and new values
     * in the resultant DataElementList. <BR/>
     */
    private void compare ()
    {
        Vector<DataElement> oldDataElementVector;
        Vector<DataElement> newDataElementVector;
        int maximum = 0;
        int progress = 0;
        int oldProgress = -1;

        // get the max number of OBJECTS present in both lists
        maximum = (this.newFileDataElementList.length >= this.oldFileDataElementList.length) ?
            this.newFileDataElementList.length :
            this.oldFileDataElementList.length;
        // create instance for deleted objects
        this.delFileDataElementList = new DataElementList [this.oldFileElementsLength];
        // set all the DataElementList to null
        for (int i = 0; i < this.delFileDataElementList.length; i++)
        {
            this.delFileDataElementList [i] = null;
        } // for i
        this.resultFileDataElementList = new DataElementList [maximum];
        // set all the DataElementList to null
        for (int i = 0; i < this.resultFileDataElementList.length; i++)
        {
            this.resultFileDataElementList [i] = null;
        } // for i

        // see if it is from the command line
        if (this.isCommandLine)
        {
            System.out.println ("> check for modified objects ...");
        } // if
        // This loop starts the comparision of each DataElement in old
        // DataElementList against the other DataElement in new DataELementList
        // It first compares the ID and IDDOMAIN of both DataElements
        // if they are matched it proceeds further for checking the similarity of
        // other values it has got.
        // loop through the oldDataElementList
        for (int i = 0; i < this.oldFileDataElementList.length; i++)
        {
            // get the DataElement Vector out of the DataElementList
            oldDataElementVector = this.oldFileDataElementList [i].dataElements;
            // loop through the newDataElementList
            for (int j = 0; j < this.newFileDataElementList.length; j++)
            {
                // get the DataElement Vector out of the DataElementList
                newDataElementVector = this.newFileDataElementList [j].dataElements;
                // loop through the old DataElement vector
                for (int k = 0; k < oldDataElementVector.size (); k++)
                {
                    // get a DataELement out of the vector
                    DataElement oldDataElement = oldDataElementVector.elementAt (k);
                    // loop through the new DataElement vector
                    for (int l = 0; l < newDataElementVector.size (); l++)
                    {
                        // get a DataELement out of the vector
                        DataElement newDataElement = newDataElementVector.elementAt (l);
                        // check whether it has already matched
                        if (!newDataElement.isMatched && !oldDataElement.isMatched)
                        {
                            // compare the ID's of both DataElements
                            if (this.checkId (oldDataElement, newDataElement))
                            {
                                // if ID's match then set the isMatched to true
                                // this flag is used to skip this DataElement again when it comes for
                                // comparision as it is already compared.This makes the loop to be
                                // run less number of times.
                                newDataElement.isMatched = true;
                                oldDataElement.isMatched = true;
                                // compare the SYSTEM values
                                if (this.checkSystemSection (oldDataElement,
                                    newDataElement))
                                {
                                    // compare the VALUES section
                                    if (this.checkValuesSection (
                                        oldDataElement, newDataElement))
                                    {
                                        // check if RIGHTS option enabled
                                        if (this.isCompareRights)
                                        {
                                            // compare the RIGHTS
                                            if (!this.checkRightsSection (oldDataElement, newDataElement))
                                                // if RIGHTS doesn't match
                                            {
                                                // see if instance is already created
                                                if (this.resultFileDataElementList [j] == null)
                                                {
                                                    // set the flag to true which means we have created an instance for this.resultFileDataElementList [j]
                                                    //newBoolean [j] = true;
                                                    // set the flag to changed
                                                    newDataElement.isChanged = true;
                                                    // create an instance of DataElementList
                                                    this.resultFileDataElementList [j] = new DataElementList ();
                                                    // add the changed DataElement to the vector
                                                    this.resultFileDataElementList [j].addElement (newDataElement);
                                                    break; // break out of the loop
                                                } // if (this.resultFileDataElementList [j] == null)

                                                // instance is already created
                                                // add the changed DataElement to the vector
                                                this.resultFileDataElementList [j].addElement (newDataElement);
                                                break;
                                            } // if (this.isCompareRights)
                                        } // if (this.isCompareRights)
                                        // check if references option is enabled
                                        if (this.isCompareReferences)
                                        {
                                            // compare the REFERENCES section
                                            if (!this.checkReferencesSection (
                                                oldDataElement, newDataElement))
                                            // REFERENCES doesn't match
                                            {
                                                if (this.resultFileDataElementList [j] == null)
                                                {
                                                    //newBoolean [j] = true;
                                                    newDataElement.isChanged = true;
                                                    this.resultFileDataElementList [j] = new DataElementList ();
                                                    this.resultFileDataElementList [j].addElement (newDataElement);
                                                    break;
                                                } // if (this.resultFileDataElementList [j] == null)

                                                // already elements in the result
                                                this.resultFileDataElementList [j].addElement (newDataElement);
                                                break;
                                            } // else REFERENCES doesn't match
                                        } // if (this.checkReferences)
                                        break;
                                    } // if (checkValuesSection (oldDataElement,newDataElement))

                                    // VALUES section doesn't match
                                    if (this.resultFileDataElementList [j] == null)
                                    {
                                        //newBoolean [j] = true;
                                        newDataElement.isChanged = true;
                                        this.resultFileDataElementList [j] = new DataElementList ();
                                        this.resultFileDataElementList [j].addElement (newDataElement);
                                        break;
                                    } // if (this.resultFileDataElementList [j] == null)

                                    //  already elements in the result
                                    this.resultFileDataElementList [j].addElement (newDataElement);
                                    break;
                                } // if (checkSystemSection (oldDataElement,newDataElement))

                                // SYSTEM section doesn't match
                                if (this.resultFileDataElementList [j] == null)
                                {
                                    //newBoolean [j] = true;
                                    newDataElement.isChanged = true;
                                    this.resultFileDataElementList [j] = new DataElementList ();
                                    this.resultFileDataElementList [j].addElement (newDataElement);
                                    break;
                                } // if (this.resultFileDataElementList [j] == null)

                                // already elements in the result
                                this.resultFileDataElementList [j].addElement (newDataElement);
                                break;
                            } // if (checkId (oldDataElement,newDataElement))
                        } // if (!newDataElement.isMatched)
                    } // for parse through the newDataElement Vcetor
                } // for parse through the oldDataElement Vector
            } // for parse through the newDataElementList

            // see if deleted option is enabled
            if (this.isDeleted)
            {
                // call this function to store the deleted objects
                this.checkForDeleted (this.oldFileDataElementList [i], i);
            } // if (this.isDeleted)
            // see if it is from the command line
            if (this.isCommandLine)
            {
                // print the status bar
                progress = ((i + 1) * 100) / this.oldFileDataElementList.length;
                // check if status changed
                if (progress != oldProgress)
                {
                    this.printProgressBar (progress);
                    oldProgress = progress;
                } // if (status != oldStatus)
            } // if (this.isCommandLine)
            // we can assume that this entry is not used anymore
            // we delete it by setting it to null
            this.oldFileDataElementList [i] = null;
        } // for parse through the oldDataElementList
        // delete the whole oldFileDataElementList because it is not needed anymore
        this.oldFileDataElementList = null;

        // see if it is from the command line
        if (this.isCommandLine)
        {
            // initialize progress
            progress = 0;
            oldProgress = -1;
            System.out.println ("\r\n> check for new objects ...");
        } // // see if it is from the command line
        // check for any new OBJECT that are present in new file but not in old file
        for (int i = 0; i < this.newFileDataElementList.length; i++)
        {
            Vector<DataElement> newFileDataElementVector =
                this.newFileDataElementList[i].dataElements;
            // loop through the vector
            for (int j = 0; j < newFileDataElementVector.size (); j++)
            {
                // get a DataElement out of vector
                DataElement newDataElement = newFileDataElementVector.elementAt (j);
                // check if the DataElement already matched
                if (!newDataElement.isMatched)
                {
                    // check if instance is created
                    if (this.resultFileDataElementList [i] == null)
                    {
                        // create the instance
                        this.resultFileDataElementList [i] = new DataElementList ();
                        // add the DataElement which is only present in new xml file
                        this.resultFileDataElementList [i].addElement (newDataElement);
                    } // if (this.resultFileDataElementList [i] == null)
                    else  // if instance is already created
                    {
                        this.resultFileDataElementList [i].dataElements.addElement (newDataElement);
                    } // else
                } // if (!newDataElement.isMatched)
            } // for (int j = 0; j < newFileDataElementVector.size (); j++)
            // see if it is from the command line
            if (this.isCommandLine)
            {
                // print the status bar
                progress = ((i + 1) * 100) / this.newFileDataElementList.length;
                // check if status changed
                if (progress != oldProgress)
                {
                    this.printProgressBar (progress);
                    oldProgress = progress;
                } // if (status != oldStatus)
            } // if (this.isCommandLine)
            // we can assume that this entry is not used anymore
            // we delete it by setting it to null
            this.newFileDataElementList [i] = null;
        } // for (int i = 0; i < this.newFileDataElementList.length; i++)
        // set the newDataElementList to null because we do not need it anymore
        this.newFileDataElementList = null;
    } // compare


    /**************************************************************************
     * Function compares the two DOM trees of old and new file
     * with grouped option enabled and stores the changed and new values in
     * the resultant DataElementList. <BR/>
     */
    private void compareGrouped ()
    {
        Vector<DataElement> oldDataElementVector;
        Vector<DataElement> newDataElementVector;
        int maximum = 0;
        int progress = 0;
        int oldProgress = -1;

        maximum = (this.newFileDataElementList.length >=
            this.oldFileDataElementList.length) ?
                this.newFileDataElementList.length :
                    this.oldFileDataElementList.length;
        // create instance of DataElementList
        this.delFileDataElementList = new DataElementList [this.oldFileElementsLength];
        // set all DataElementList to null
        for (int i = 0; i < this.delFileDataElementList.length; i++)
        {
            this.delFileDataElementList [i] = null;
        } // for i

        this.resultFileDataElementList = new DataElementList [maximum];
        for (int i = 0; i < this.resultFileDataElementList.length; i++)
        {
            this.resultFileDataElementList [i] = null;
        } // for i

        // see if it is from the command line
        if (this.isCommandLine)
        {
            System.out.println ("> check for modified objects ...");
        } // if
        // this loop starts comparision of DataElements and even if a single
        // DataElement changes in a DataElementList all are considered
        // as changed and it adds all the DataElements to the resultant list
        for (int i = 0; i < this.oldFileDataElementList.length; i++)
        {
            oldDataElementVector = this.oldFileDataElementList [i].dataElements;
        newLabel:
            for (int j = 0; j < this.newFileDataElementList.length; j++)
            {
                if (this.newFileDataElementList [j] == null)
                {
                    continue;
                } // if
                newDataElementVector = this.newFileDataElementList [j].dataElements;
            oldLabel:
                for (int k = 0; k < oldDataElementVector.size (); k++)
                {
                    DataElement oldDataElement = oldDataElementVector.elementAt (k);
                    for (int l = 0; l < newDataElementVector.size (); l++)
                    {
                        DataElement newDataElement = newDataElementVector.elementAt (l);
                        // see if it is already matched
                        if (!newDataElement.isMatched && !oldDataElement.isMatched)
                        {
                            // see if it is changed
                            if (!newDataElement.isChanged && !oldDataElement.isChanged)
                            {
                                if (this.checkId (oldDataElement, newDataElement))
                                {
                                    if (this.checkGruppe (newDataElement))
                                    {
                                        if (!this.checkForMatch (
                                            oldDataElementVector,
                                            newDataElementVector))
                                        {
                                            continue;
                                        } // if
                                    } // if
                                    // check the size of the DataElement Vector in both
                                    // old and new DataElementList
                                    // if they doesn't match which means it is a change for the grouped case
                                    if (!this.checkForNew (i, j))
                                    {
                                        this.setFlags (i, j);
                                        this.resultFileDataElementList [j] = this.newFileDataElementList [j];
                                        this.oldFileDataElementList [i] = null;
                                        break newLabel;
                                    } // if
                                    newDataElement.isMatched = true;
                                    oldDataElement.isMatched = true;
                                    if (this.checkSystemSection (
                                        oldDataElement, newDataElement))
                                    {
                                        if (this.checkValuesSection (
                                            oldDataElement, newDataElement))
                                        {
                                            if (this.isCompareRights)
                                            {
                                                if (!this.checkRightsSection (
                                                    oldDataElement,
                                                    newDataElement))
                                                {
                                                    // set the isMatched and isChanged to true for all the DataElements
                                                    // for this DataElementList
                                                    this.setFlags (i, j);
                                                    // put the changed one in resultlist
                                                    this.resultFileDataElementList [j] = this.newFileDataElementList [j];
                                                    this.oldFileDataElementList [i] = null;
                                                    // break out of the newlabel and start comparision with next DataElementlist of old file
                                                    break newLabel;
                                                } // if
                                            } // if (checkValuesSection (oldDataElement,newDataElement))
                                            if (this.isCompareReferences)
                                            {
                                                if (!this
                                                    .checkReferencesSection (
                                                        oldDataElement,
                                                        newDataElement))
                                                {
                                                    this.setFlags (i, j);
                                                    this.resultFileDataElementList [j] = this.newFileDataElementList [j];
                                                    this.oldFileDataElementList [i] = null;
                                                    break newLabel;
                                                } // else
                                            } // if (this.isCompareReferences)
                                            break;
                                        } // if (checkValuesSection (oldDataElement,newDataElement))

                                        this.setFlags (i, j);
                                        this.resultFileDataElementList [j] = this.newFileDataElementList [j];
                                        this.oldFileDataElementList [i] = null;
                                        break newLabel;
                                    } // if (checkSystemSection (oldDataElement,newDataElement))

                                    this.setFlags (i, j);
                                    this.resultFileDataElementList [j] = this.newFileDataElementList [j];
                                    this.oldFileDataElementList [i] = null;
                                    break newLabel;
                                } // if (checkId (oldDataElement,newDataElement))

                                continue;
                            } // if (!newDataElement.isChanged)

                            // if it is changed then break out of the oldLabel
                            // and start comparision with next DataElement vector in newDataElementList
                            // as the present one is already matched and changed also
                            break oldLabel;
                        } // if (!newDataElement.isMatched)
                    } // for parse through the newDataElement Vector
                } // for parse through the oldDataElement vector
            } // for parse through the newDataElementList
            // should it be marked deleted?
            if (this.isDeleted)
            {
                this.checkForDeleted (this.oldFileDataElementList [i], i);
            } // if
            // see if it is from command line
            if (this.isCommandLine)
            {
                // print the status bar
                progress = ((i + 1) * 100) / this.oldFileDataElementList.length;
                // check if status changed
                if (progress != oldProgress)
                {
                    this.printProgressBar (progress);
                    oldProgress = progress;
                } // if (status != oldStatus)
            } // if (this.isCommandLine)
            // delete the entry because we do not need it anymore
            this.oldFileDataElementList [i] = null;
        } // for travese through the oldDataElementList
        // set the oldDataElementList to null because we do not need it anymore
        this.oldFileDataElementList = null;

        // initialize progress
        progress = 0;
        oldProgress = -1;
        // see if it is from the command line
        if (this.isCommandLine)
        {
            System.out.println ("\r\n> check for new objects ...");
        } // if
        // check for any new OBJECT that are present in new file but not in old file
        // loop which checks for any new DataElements present in the new file
        for (int i = 0; i < this.newFileDataElementList.length; i++)
        {
            // get the data elements
            Vector<DataElement> newFileDataElementVector =
                this.newFileDataElementList[i].dataElements;
            // loop through the data elements
            for (int j = 0; j < newFileDataElementVector.size (); j++)
            {
                DataElement newDataElement = newFileDataElementVector.elementAt (j);
                if (!newDataElement.isMatched)
                {
                    if (this.resultFileDataElementList [i] == null)
                    {
                        this.resultFileDataElementList [i] = new DataElementList ();
                        this.resultFileDataElementList [i] = this.newFileDataElementList [i];
                        break;
                    } // if (this.resultFileDataElementList [i] == null)

                    this.resultFileDataElementList[i] =
                        this.newFileDataElementList[i];
                } // if (!newDataElement.isMatched)
            } // for (int j = 0; j < newFileDataElementVector.size (); j++)
            // see if it is from the command line
            if (this.isCommandLine)
            {
                // print the status bar
                progress = ((i + 1) * 100) / this.newFileDataElementList.length;
                // check if status changed
                if (progress != oldProgress)
                {
                    this.printProgressBar (progress);
                    oldProgress = progress;
                } // if (status != oldStatus)
            } // if (this.isCommandLine)
            // delete the entry because we do not need it anymore
            this.newFileDataElementList [i] = null;
        } // for (int i = 0; i < this.newFileDataElementList.length; i++)
        // delete the newFileDataElementList because it is not needed anymore
        this.newFileDataElementList = null;
    } // comparFilesGroup


    /**************************************************************************
     * Function checks for any deleted objects and stores them in dataelementlist
     *
     * @param dataElementList a DataElementList object containing the objects
     * @param index   An integer containing the index of the oldDataElementList
     */
    private void checkForDeleted (DataElementList dataElementList, int index)
    {
        // check if it is null
        if (dataElementList == null)
        {
            return;
        } // if
        // get the DataElement vector out of the DataElementList
        Vector<DataElement> dataElementVector = dataElementList.dataElements;
        // loop through the vector
        for (int i = 0; i < dataElementVector.size (); i++)
        {
            //get a DataElement out of the vector
            DataElement dataElement = dataElementVector.elementAt (i);
            // see if it is null
            if (dataElement == null)
            {
                continue;
            } // if
            // check if it is already matched
            if (!dataElement.isMatched)
            {
                // store the DataElement
                if (this.delFileDataElementList [index] == null)
                {
                    this.delFileDataElementList [index] = new DataElementList ();
                    this.delFileDataElementList [index].addElement (dataElement);
                } // if (this.delFileDataElementList [index] == null)
                else
                {
                    this.delFileDataElementList [index].addElement (dataElement);
                } // else
            } // if (!dataElement.isMatched)
        } // for (int i = 0; i < dataElementVector.size (); i++)
    } // private void checkForDeleted (DataElementList dataElementList,int index)


    /**************************************************************************
     * Function checks for the type of the DataElement
     *
     * @param newElement a DataElement object containing one object details
     *
     * @return  ???
     */
    private boolean checkGruppe (DataElement newElement)
    {
        // check if the type equals m2XDiffConstants.ENGLISH_TYPE or m2XDiffConstants.GERMAN_TYPE
        if (newElement.typename.equals (m2XDiffConstants.ENGLISH_TYPE) ||
            newElement.typename.equals (m2XDiffConstants.GERMAN_TYPE))
        {
            return true;
        } // if

        return false;
    } // private boolean checkGruppe (DataElement newElement)


    /**************************************************************************
     * Function checks for matched objects of a DataElement vector. <BR/>
     *
     * @param   oldDataElementVector    First vector of data elements.
     * @param   newDataElementVector    second vector of data elements.
     *
     * @return  <CODE>true</CODE> if there was match found,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean checkForMatch (Vector<DataElement> oldDataElementVector,
                                   Vector<DataElement> newDataElementVector)
    {
        // loop through the DataElement vector
        for (int i = 0; i < oldDataElementVector.size (); i++)
        {
            // get a DataElement out of vector
            DataElement oldDataElement = oldDataElementVector.elementAt (i);
            // check if it's type is Gruppe or Group
            if (oldDataElement.typename.equals (m2XDiffConstants.GERMAN_TYPE) ||
                oldDataElement.typename.equals (m2XDiffConstants.ENGLISH_TYPE))
            {
                continue;
            } // if
            // loop through the new DataElements
            for (int j = 0; j < newDataElementVector.size (); j++)
            {
                // get a DataElement out of the vector
                DataElement newDataElement = newDataElementVector.elementAt (j);
                // check if it's type is Gruppe or Group
                if ((newDataElement.typename
                    .equals (m2XDiffConstants.GERMAN_TYPE)) ||
                    (newDataElement.typename
                        .equals (m2XDiffConstants.ENGLISH_TYPE)))
                {
                    continue;
                } // if
                // compare the id of two DataElements.
                if (this.checkId (oldDataElement, newDataElement))
                {
                    return true;
                } // if
            } // for j
        } // for i
        return false;
    } // private boolean checkForMatch (Vector newDataElementVector,int index)


    /**************************************************************************
     * Function compares the number of OBJECTS inside old and new DataElementList.
     *
     * @param oldIndex   An integer containing the index of the oldDataElementList
     * @param newIndex   An integer containing the index of the newDataElementList
     *
     * @return  <CODE>true</CODE> if both vectors have the same size,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean checkForNew (int oldIndex, int newIndex)
    {
        Vector<DataElement> oldVector = this.oldFileDataElementList [oldIndex].dataElements;
        Vector<DataElement> newVector = this.newFileDataElementList [newIndex].dataElements;
        return oldVector.size () == newVector.size () ? true : false;
    } // checkForNew


    /**************************************************************************
     * Function sets flags to true.
     *
     * @param oldIndex   An integer holding the index
     * @param newIndex   An integer holding the index
     */
    private void setFlags (int oldIndex, int newIndex)
    {
        // get the DataElement vector out of DataElementList
        Vector<DataElement> oldDataElements =
            this.oldFileDataElementList[oldIndex].dataElements;
        // get the DataElement vector out of DataElementList
        Vector<DataElement> newDataElements =
            this.newFileDataElementList[newIndex].dataElements;
        // loop through the vector and set isMatched and isChanged to true
        for (int i = 0; i < oldDataElements.size (); i++)
        {
            DataElement dataElement = oldDataElements.elementAt (i);
            dataElement.isMatched = true;
            dataElement.isChanged = true;
        } // for (int i = 0; i < dataElements.size (); i++)
        for (int i = 0; i < newDataElements.size (); i++)
        {
            DataElement dataElement = newDataElements.elementAt (i);
            dataElement.isMatched = true;
            dataElement.isChanged = true;
        } // for (int i = 0; i < dataElements.size (); i++)
    } // setFlags


    /**************************************************************************
     * Function compares the System section of the two xml files.
     *
     * @param oldElement  DataElement containing the old xml file details
     * @param newElement  DataElement containing the new xml file details
     *
     * @return  <CODE>true</CODE> if the values of the system section are equal,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean checkSystemSection (DataElement oldElement, DataElement newElement)
    {
        // compare the name
        if (!oldElement.name.equals (newElement.name))
        {
            return false;
        } // if
        // compare the description
        if (!oldElement.description.equals (newElement.description))
        {
            return false;
        } // if
        // compare the validuntil
        if (!oldElement.validUntil.equals (newElement.validUntil))
        {
            return false;
        } // if
        // compare the container type
        if (!oldElement.containerType.equals (newElement.containerType))
        {
            return false;
        } // if
        // compare the container id
        if (!oldElement.containerId.equals (newElement.containerId))
        {
            return false;
        } // if
        // compare the container id domain
        if (!oldElement.containerIdDomain.equals (newElement.containerIdDomain))
        {
            return false;
        } // if
        // compare the container tab name
        if (!oldElement.containerTabName.equals (newElement.containerTabName))
        {
            return false;
        } // if
        // all checks successfull
        return true;
    } // checkSystemSection


    /**************************************************************************
     * Function compares the References section of the two xml files.
     *
     * @param oldElement  DataElement containing the old xml file details
     * @param newElement  DataElement containing the new xml file details
     *
     * @return  <CODE>true</CODE> if the values of the references section are
     *          equal, <CODE>false</CODE> otherwise.
     */
    private boolean checkReferencesSection (DataElement oldElement,
                                            DataElement newElement)
    {
        // get the ReferenceDataElement vector out of DataElement
        Vector<ReferenceDataElement> oldVector = oldElement.references;
        Vector<ReferenceDataElement> newVector = newElement.references;
        // check if both have same number of values
        if (oldVector.size () != newVector.size ())
        {
            return false;
        } // if

        // loop through the vector
        for (int i = 0; i < oldVector.size (); i++)
        {
            // get ReferenceDataElement out of the vector
            ReferenceDataElement oldReference = oldVector.elementAt (i);
            ReferenceDataElement newReference = newVector.elementAt (i);
            // compare the REFERENCES section values
            if (this.compareReferences (oldReference, newReference))
            {
                continue;
            } // if

            return false;
        } // for i

        return true;
    } // checkReferencesSection


    /**************************************************************************
     * Function compares the Reference section of the two xml files.
     *
     * @param oldReference  ReferenceDataElement containing the old xml file Reference details
     * @param newReference  ReferenceDataElement containing the new xml file Reference details
     *
     * @return true/false  boolean result of the comparision
     */
    private boolean compareReferences (ReferenceDataElement oldReference,
                                       ReferenceDataElement newReference)
    {
        // compare the type
        if (!oldReference.containerType.equals (newReference.containerType))
        {
            return false;
        } // if
        // compare id
        if (!oldReference.containerId.equals (newReference.containerId))
        {
            return false;
        } // if
        // compare domain id
        if (!oldReference.containerIdDomain
            .equals (newReference.containerIdDomain))
        {
            return false;
        } // if
        // compare tab name
        if (!oldReference.containerTabName
            .equals (newReference.containerTabName))
        {
            return false;
        } // if
        // return truie because all checks have been successfull
        return true;
    } // compareReferences


    /**************************************************************************
     * Function compares the Rights section of the two xml files.
     *
     * @param oldElement  DataElement containing the old xml file details
     * @param newElement  DataElement containing the new xml file details
     *
     * @return  <CODE>true</CODE> if the contents of the RIGHTS section are
     *          equal, <CODE>false</CODE> otherwise.
     */
    private boolean checkRightsSection (DataElement oldElement,
                                        DataElement newElement)
    {
        // get RightDataElement vector out of the DataElement
        Vector<RightDataElement> oldRights = oldElement.rights;
        Vector<RightDataElement> newRights = newElement.rights;
        // check if both have same number of RIGHTS
        if (oldRights.size () == newRights.size ())
        {
            // loop through the vector
            for (int i = 0; i < oldRights.size (); i++)
            {
                // get the RightDataElement out of the vector
                RightDataElement oldRightsElement = oldRights.elementAt (i);
                RightDataElement newRightsElement = newRights.elementAt (i);
                // compare the RIGHT section
                if (this.compareRights (oldRightsElement, newRightsElement))
                {
                    continue;
                } // if

                return false;
            } // for (int i = 0; i < oldRights.size (); i++)
        } // if (oldRights.size () == newRights.size ())
        else
        {
            return false;
        } // else
        return true;
    } // checkRightsSection


    /**************************************************************************
     * Function compares the Right section of the two xml files.
     *
     * @param oldRights  RightDataElement containing the old xml file Right details
     * @param newRights  RightDataElement containing the new xml file Right details
     *
     * @return true/false  boolean result of the comparision
     */
    private boolean compareRights (RightDataElement oldRights,
                                   RightDataElement newRights)
    {
        String oldName = oldRights.name;
        String newName = newRights.name;
        String oldType = oldRights.type;
        String newType = newRights.type;

        // get the profiles vector out of RightDataElement
        Vector<String> oldProfiles = oldRights.profiles;
        Vector<String> newProfiles = newRights.profiles;
        // check if both have same number of profiles
        if (oldProfiles.size () != newProfiles.size ())
        {
            return false;
        } // if
        if (!oldName.equals (newName))
        {
            return false;
        } // if
        if (!oldType.equals (newType))
        {
            return false;
        } // if
        // loop throgh the profile vector
        for (int i = 0; i < oldProfiles.size (); i++)
        {
            // get a profile name out of the vector
            String oldString = oldProfiles.elementAt (i);
            String newString = newProfiles.elementAt (i);
            // compare the profile names
            if (oldString.equals (newString))
            {
                continue;
            } // if

            return false;
        } // for (int i = 0; i < oldProfiles.size (); i++)
        return true;
    } // compareRights


    /**************************************************************************
     * Function compares the Values section of the two xml files.
     *
     * @param oldElement  DataElement containing the old xml file  details
     * @param newElement  DataElement containing the new xml file  details
     *
     * @return true/false  boolean result of the comparision
     */
    private boolean checkValuesSection (DataElement oldElement,
                                        DataElement newElement)
    {
        // get the ValueDataElement vector out of the DataElement
        Vector<ValueDataElement> oldvalues = oldElement.values;
        Vector<ValueDataElement> newvalues = newElement.values;
        // check if both have same number of VALUES
        if (oldvalues.size () == newvalues.size ())
        {
            // loop through the vector
            for (int i = 0; i < oldvalues.size (); i++)
            {
                // get the ValueDataElement out of the  vector
                ValueDataElement oldValueDataElement = oldvalues.elementAt (i);
                ValueDataElement newValueDataElement = newvalues.elementAt (i);
                // compare VALUE section
                if (!this.compareValues (oldValueDataElement, newValueDataElement))
                {
                    return false;
                } // if
            } // for (int i = 0; i < oldvalues.size (); i++)
        } // if (oldvalues.size () == newvalues.size ())
        else
        {
            return false;
        } // else
        return true;
    } // checkValuesSection


    /**************************************************************************
     * Function compares the Value section of the two xml files.
     *
     * @param oldValueDataElement  ValueDataElement containing the old xml file Value details
     * @param newValueDataElement  ValueDataElement containing the new xml file Value details
     *
     * @return true/false  boolean result of the comparision
     */
    private boolean compareValues (ValueDataElement oldValueDataElement,
                                   ValueDataElement newValueDataElement)
    {
        // compare the field name
        if (!oldValueDataElement.field.equals (newValueDataElement.field))
        {
            return false;
        } // if
        // compare the type
        if (!oldValueDataElement.type.equals (newValueDataElement.type))
        {
            return false;
        } // if
        // compare the value
        if (!oldValueDataElement.value.equals (newValueDataElement.value))
        {
            return false;
        } // if
        // compare the info
        if (!oldValueDataElement.info.equals (newValueDataElement.info))
        {
            return false;
        } // if
        // compare the mapping field
        if (!oldValueDataElement.mappingField
            .equals (newValueDataElement.mappingField))
        {
            return false;
        } // if
        // compare the options
        if (!oldValueDataElement.options.equals (newValueDataElement.options))
        {
            return false;
        } // if

        // compare all other attributes and return the result:
        return oldValueDataElement.equals (newValueDataElement);
    } // compareValues


    /**************************************************************************
     * Function compares the Id section of the two xml files.
     *
     * @param oldElement  DataElement containing the old xml file details
     * @param newElement  DataElement containing the new xml file details
     *
     * @return true/false  boolean result of the comparision
     */
    private boolean checkId (DataElement oldElement, DataElement newElement)
    {
        // compare ID
        if (!oldElement.idDomain.equals (newElement.idDomain))
        {
            return false;
        } // if
        // compare id domain
        if (!oldElement.id.equals (newElement.id))
        {
            return false;
        } // if
        // all checks successfull
        return true;
    } // checkId


    /**************************************************************************
     * Function parses the xml files and creates DOM tree and stores the details
     * in DataElementList. <BR/>
     *
     * @param   fileName    String containing the old file name
     *
     * @return  The resulting DOM tree.
     *
     * @throws  CommandLineException
     *          This exception is thrown if the import xml file structure is wrong
     * @throws  XMLReaderException
     *          In case a XML parser occurred.
     */
    private DataElementList [] parseFile (String fileName)
        throws CommandLineException, XMLReaderException
    {
        NodeList objectsNodeList;
        Document doc;
        int dataElementListLength;
        int progress = 0;
        int oldProgress = -1;
        DataElementList [] dataElementList;

        try
        {
            // create a DOM tree from a xml file
            doc = this.getDocument (fileName);

            // check if we could read the file
            if (doc != null)
            {
                // get the documentRoot
                Element root = doc.getDocumentElement ();
                // check if we have document root
                if (root == null)
                {
                    throw new CommandLineException (m2XDiffMessages.MISSING_DOCROOT);
                } // if (oldroot == null || newroot == null)

                // check if have IMPORT tag in the DOM tree
                if (!this.checkRoot (root))
                {
                    throw new CommandLineException (m2XDiffMessages.MISSING_IMPORT);
                } // if
                // check if we have child nodes for the DOM tree
                if (!root.hasChildNodes ())
                {
                    throw new CommandLineException (m2XDiffMessages.MISSING_CHILDS);
                } // if

                // check if it is a call from main function
                if (this.isCommandLine)
                {
                    System.out.println ("> reading file: " + fileName);
                } // if

                // get the child nodes with node name OBJECTS
                // get all OBJECT nodes:
                objectsNodeList = root.getElementsByTagName (DIConstants.ELEM_OBJECTS);

                // check if we have any nodes
                // BB: is this really wrong? A complete empty file could
                // also be a valid file!
                if (objectsNodeList == null)
                {
                    throw new CommandLineException (m2XDiffMessages.MISSING_OBJECTS);
                } // if

                // set length of array
                dataElementListLength = objectsNodeList.getLength ();

                // create the dataElementList array
                dataElementList = new DataElementList [dataElementListLength];

                // parse through the DOM tree and store the values in DataElementlist
                for (int i = 0; i < dataElementListLength; i++)
                {
                    // read the objects thorugh the filter
                    dataElementList [i] = this.parseObjects (objectsNodeList.item (i));
                    // check if it is a call from main function
                    if (this.isCommandLine)
                    {
                        // print a progress bar
                        progress = ((i + 1) * 100) / dataElementListLength;
                        if (progress != oldProgress)
                        {
                            this.printProgressBar (progress);
                            oldProgress = progress;
                        } // if (progress > oldprogress)
                    } // if (this.isCommandLine)
                } // for (int i = 0; i < oldFileDataElementList.length; i++)

                // remove the doc because it is not needed anymore
                // set the dom tree to null because they are not used anymore
                doc.removeChild (doc.getDocumentElement ());
                doc = null;

                // check if it is a call from main function
                if (this.isCommandLine)
                {
                    System.out.println ();
                } // if
                // return the dataElementList array
                return dataElementList;
            } // if (importOldDoc != null && importNewDoc != null)

            throw new CommandLineException (m2XDiffMessages.WRONG_FILE_NAMES);
        } // try
        catch (XMLReaderException e)
        {
            throw e;
        } // catch
    } // parseFile


    /**************************************************************************
     * Function checks for the existence of IMPORT tag in the xml file
     *
     * @param root  Element an element pointing to IMPORT tag
     *
     * @return true/false  boolean value resultant of checking
     */
    private boolean checkRoot (Element root)
    {
        String nodename = root.getNodeName ();

        // test if this is the IMPORT element:
        if (nodename != null && !nodename.equals (DIConstants.ELEM_IMPORT))
        {
            return false;
        } // if

        return true;
    } // checkRoot


    /**************************************************************************
     * Function creates the DOM tree for the xml file
     *
     * @param fileName  String containing the xml file name
     *
     * @return doc/null Document the doc root of the DOM tree
     *
     * @throws  XMLReaderException
     *          In case a XML parser occurred.
     */
    private Document getDocument (String fileName)
        throws XMLReaderException
    {
        File file = null;
        Document doc;

        // check if filename has been specified
        if (fileName != null && fileName.length () > 0)
        {
            file = new File (fileName);
            // see if the file exists
            if (file.isFile ())
            {
                try
                {
                    // read the document:
                    // do not validate the document. This speeds up the parsing
                    doc = new XMLReader (fileName, false, null).getDocument ();
                    return doc;
                } // try
                catch (XMLReaderException e)
                {
                    throw e;
                } // catch
            } // if (file.isFile ())

            // it's not a file
            // TODO RB: Call  
            //          MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
            //              DIMessages.ML_MSG_FILEISDIRECTORY, env)
            //          to get the text in the correct language
            this.showDebug (DIMessages.ML_MSG_FILEISDIRECTORY); 
            return null;
        } // if (fileName != null && fileName.length () > 0)

        // no file name specified
        // TODO RB: Call  
        //          MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
        //              DIMessages.ML_MSG_NOFILE, env)
        //          to get the text in the correct language
        this.showDebug (DIMessages.ML_MSG_NOFILE);
        return null;
    } // getDocument


    /**************************************************************************
     * parses an objects section and create the objects store them. <BR/>
     * in DataElementList
     *
     * @param root      root node of the subtree to be parsed
     *
     * @return  A DataElementList containing data extracted from import xml file
     */
    private DataElementList parseObjects (Node root)
    {
        Node node;
        NodeList nodelist;
        int size;
        DataElement dataElement = null;
        DataElementList dataElementList = null;

        if (root == null)
        {
            return null;
        } // if

        // root is not null
        // test if this really an OBJECTS element
        if ((!root.getNodeName ().equals (DIConstants.ELEM_OBJECTS)) &&
            (!root.getNodeName ().equals (DIConstants.ELEM_TABS)))
        {
            return null;
        } // if

        // we found an OBJECTS section
        // parse through the OBJECT sections
        nodelist = root.getChildNodes ();
        size = nodelist.getLength ();
        // check if there are no objects
        if (size > 0)
        {
            // initialize an import element list
            dataElementList = new DataElementList ();
            // now go through all the objects
            for (int i = 0; i < size; i++)
            {
                //get a node from the nodelist
                node = nodelist.item (i);
                // check if node is an element node
                if (node.getNodeType () == Node.ELEMENT_NODE)
                {
                    // parse OBJECTS section and create an import element
                    dataElement = this.parseObject (node);
                    // check if we got an DataElement
                    if (dataElement != null)
                    {
                        // store the DataElement in a collection
                        // and process it later together with the importscript
                        dataElementList.addElement (dataElement);
                    } // if (DataElement != null)
                } // if (node.getNodeType () == Node.ELEMENT_NODE)
            } // for (int i = 0; i < size; i++)
            return dataElementList;
        } // if (size > 0)

        // no objects found
        return null;
    } // parseObjects


    /**************************************************************************
     * parses an objects section and store them in DataElement.
     *
     * @param root root node of the tree to be parsed
     *
     * @return  A DataElement containing values of extracted from import file
     */
    private DataElement parseObject (Node root)
    {
        NamedNodeMap attributes;
        Node node = null;
        NodeList nodelist = null;
        int size = 0;
        // system variables
        String nodename;

        // import element that holds the data
        DataElement dataElement = new DataElement ();
        // check if root is not null
        if (root == null)
        {
            return null;
        } // if (root == null)

        // test if this is an OBJECT or an TABOBJECT element so that we have
        // a correct node
        if ((!root.getNodeName ().equals (DIConstants.ELEM_OBJECT)) &&
            (!root.getNodeName ().equals (DIConstants.ELEM_TABOBJECT)))
        {
            return null;
        } // if ((!root.getNodeName ().equals (DIConstants.ELEM_OBJECT)) && ...

        // correct tag
        // get the attributes of the OBJECT/TABOBJECT tag
        attributes = root.getAttributes ();

        // get the TYPECODE attribute:
        node = attributes.getNamedItem (DIConstants.ATTR_TYPECODE);
        if (node != null)
        {
            // set the type code in the DataElement:
            dataElement.p_typeCode = node.getNodeValue ();
        } // if (node != null)

        // get the TYPE attribute
        node = attributes.getNamedItem (DIConstants.ATTR_TYPE);
        if (node != null)
        {
            // set the typename in the DataElement
            dataElement.typename = node.getNodeValue ();
        } // if (node != null)

        // check if there are more nodes
        if (root.hasChildNodes ())
        {
            // get the child nodes
            nodelist = root.getChildNodes ();
            size = nodelist.getLength ();
            for (int i = 0; i < size; i++)
            {
                //parse nodes
                node = nodelist.item (i);
                if (node.getNodeType () == Node.ELEMENT_NODE)
                {
                    nodename = node.getNodeName ();
                    // system section
                    if (nodename.equals (DIConstants.ELEM_SYSTEM))
                    {
                        // parse the system element section
                        this.parseSystemSection (dataElement, node);
                    } // if (nodename.equals (DIConstants.ELEM_SYSTEM))
                    // values section
                    else if (nodename.equals (DIConstants.ELEM_VALUES))
                    {
                        // parse the system element section
                        this.parseValuesSection (dataElement, node);
                    } // else if (nodename.equals (DIConstants.ELEM_VALUES))
                    else if (nodename.equals (DIConstants.ELEM_RIGHTS))
                    {
                        // should the rights be included in the comparison
                        if (this.isCompareRights)
                        {
                            // parse the system element section
                            this.parseRightsSection (dataElement, node);
                        } // if (this.isCompareRights)
                    } // else if (nodename.equals (DIConstants.ELEM_RIGHTS))
                    else if (nodename.equals (DIConstants.ELEM_REFERENCES))
                    {
                        // should the rights be included in the comparison
                        if (this.isCompareReferences)
                        {
                            // parse the system element section
                            this.parseReferencesSection (dataElement, node);
                        } // if (this.isCompareReferences)
                    } // else if (nodename.equals (DIConstants.ELEM_REFERENCES))
                    else if (nodename.equals (DIConstants.ELEM_OBJECTS))
                    {
                        dataElement.dataElementList = this.parseObjects (node);
                    } // else if (nodename.equals (DIConstants.ELEM_OBJECTS))
                    else if (nodename.equals (DIConstants.ELEM_TABS))
                    {
                        dataElement.tabElementList = this.parseObjects (node);
                    } // else if (nodename.equals (DIConstants.ELEM_OBJECTS))

                    // node not valid therefore ignore it
                } // if (node.getNodeType == Node.ElementNode)
            } // for (int i = 0; i < size; i++)
        } // if (root.hasChildNodes ())
        // now remove the child node because it is not needed anymore
        // this has to be done in order to avoid memory leaks
        try
        {
            for (int j = size - 1; j >= 0; j--)
            {
                node = nodelist.item (j);
                node = root.removeChild (node);
                node = null;
            } // for (int j = 0; j < size; j++)
        } // try
        catch (Exception e)
        {
            // ignore any error
            System.out.println ("Exception: " + e.toString ());
        } // catch

        return dataElement;
    } // parseObject


    /**************************************************************************
     * parses the SYSTEM section of the xml document and
     * stores value entries found in the DataElement. <BR/>
     *
     * @param dataElement DataElement object holding the details of SYSTEM section
     * @param root root node of the tree to be parsed
     */
    private void parseSystemSection (DataElement dataElement, Node root)
    {
        NodeList systemNodeList;
        int systemSize;
        Node systemNode;
        Node attributeNode;
        String importId                 = "";
        String importIdDomain           = "";
        boolean importIdAddWspUser      = false;
        String importName               = "";
        String importDescription        = "";
        String importValidUntil         = "";
        String importContainerType      = "";
        String importContainerId        = "";
        String importContainerIdDomain  = "";
        String importContainerTabName   = "";
        String importShowInNews         = "";

        NamedNodeMap attributes;
        Text text;

        systemNodeList = root.getChildNodes ();
        systemSize = systemNodeList.getLength ();
        // loop through the elements
        for (int is = 0; is < systemSize; is++)
        {
            systemNode = systemNodeList.item (is);
            // check if this is an element node
            if (systemNode.getNodeType () == Node.ELEMENT_NODE)
            {
                // check the kind of element we found
                if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_ID))
                {
                    // get the id domain attribute
                    attributes = systemNode.getAttributes ();
                    attributeNode = attributes.getNamedItem (DIConstants.ATTR_DOMAIN);
                    // check if DOMAIN attribute has been set
                    if (attributeNode != null)
                    {
                        importIdDomain = attributeNode.getNodeValue ().trim ();
                    } // if

                    attributeNode = attributes.getNamedItem (DIConstants.ATTR_IDADDWSPUSER);
                    // check if ADDWSPUSER attribute has been set
                    if (attributeNode != null)
                    {
                        String value =
                            attributeNode.getNodeValue ().trim ().toLowerCase ();
                        importIdAddWspUser =
                            value.equals ("true") || value.equals ("y");
                    } // if

                    // get the text value
                    text = (Text) systemNode.getFirstChild ();
                    if (text != null)
                    {
                        importId = text.getNodeValue ().trim ();
                    } // if
                } // if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_ID))
                else if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_NAME))
                {
                    text = (Text) systemNode.getFirstChild ();
                    if (text != null)
                    {
                        importName = text.getNodeValue ().trim ();
                    } // if
                } // else if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_NAME))
                else if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_DESCRIPTION))
                {
                    text = (Text) systemNode.getFirstChild ();
                    if (text != null)
                    {
                        importDescription = text.getNodeValue ().trim ();
                    } // if
                } // else if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_DESCRIPTION))
                else if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_VALIDUNTIL))
                {
                    text = (Text) systemNode.getFirstChild ();
                    if (text != null)
                    {
                        importValidUntil = text.getNodeValue ().trim ();
                    } // if
                } // else if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_VALIDUNTIL))
                else if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_CONTAINER))
                {
                    // parse the container node and set the values
                    this.parseContainerNode (systemNode, dataElement);
                    // because we set the values again below we must store the
                    // container settings. This is a workaround in order
                    // to be able to use the
                    importContainerType = dataElement.containerType;
                    importContainerId = dataElement.containerId;
                    importContainerIdDomain = dataElement.containerIdDomain;
                    importContainerTabName = dataElement.containerTabName;
                } // else if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_CONTAINER))
                else if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_SHOWINNEWS))
                {
                    text = (Text) systemNode.getFirstChild ();
                    if (text != null)
                    {
                        importShowInNews = text.getNodeValue ().trim ();
                    } // if
                } // else if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_SHOWINNEWS))
            } // if (node.getNodeTyoe == Node.ELEMENT_TYPE)
        } // for (int is = 0; is < systemSize; is++)

        // store the systems values in the dataElement
        dataElement.setSystemValues (importId, importIdDomain,
                                     importIdAddWspUser, importName,
                                     importDescription, importValidUntil,
                                     importContainerType, importContainerId,
                                     importContainerIdDomain,
                                     importContainerTabName,
                                     importShowInNews, 0);
    } // parseSystemSection


    /**************************************************************************
     * Parses the container tag in an system section of an  document and
     * sets its values in an dataElement instance. <BR/>
     *
     * @param containerNode         root node of the subtree to parse
     * @param dataElement           the dataElement instance to store
     *                              the values in
     */
    private void parseContainerNode (Node containerNode,
                                     DataElement dataElement)
    {
        NamedNodeMap attributes;
        String containerType        = "";
        String containerId          = "";
        String containerIdDomain    = "";
        String containerTabName     = "";
        Text text;
        NodeList nodelist;
        Node node;
        Node attributeNode;
        int size;

        // read the type attribute
        attributes = containerNode.getAttributes ();
        // check if we got any attributes
        if (attributes != null)
        {
            // container tab name
            attributeNode = attributes.getNamedItem (DIConstants.ATTR_TABNAME);
            if (attributeNode != null)
            {
                containerTabName = attributeNode.getNodeValue ();
            } // if
            // container type
            attributeNode = attributes.getNamedItem (DIConstants.ATTR_TYPE);
            if (attributeNode != null)
            {
                containerType = attributeNode.getNodeValue ();
            } // if
            // check if the containerType is a external key.
            // in that case we have to read a more complex structure
            // in the form:
            // <CONTAINER TYPE="EXTKEY">
            //    <ID DOMAIN="app">123</ID>
            // </CONTAINER>
            if (containerType.equalsIgnoreCase (DIConstants.CONTAINER_EXTKEY))
            {
                // check if there are sub nodes
                if (containerNode.hasChildNodes ())
                {
                    // parse through the OBJECT sections
                    nodelist = containerNode.getChildNodes ();
                    // get amount of objects
                    size = nodelist.getLength ();
                    // find the <OBJECTS> element
                    for (int i = 0; i < size; i++)
                    {
                        // get the node
                        node = nodelist.item (i);
                        // check if it is an ID node
                        if (node.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_ID))
                        {
                            // get the id domain attribute
                            attributes = node.getAttributes ();
                            attributeNode = attributes.getNamedItem (DIConstants.ATTR_DOMAIN);
                            // check if DOMAIN attribute has been set
                            if (attributeNode != null)
                            {
                                containerIdDomain = attributeNode.getNodeValue ();
                            } // if
                            // get the text value
                            text = (Text) node.getFirstChild ();
                            if (text != null)
                            {
                                containerId = text.getNodeValue ();
                            } // if
                        } // if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_ID))
                    } // for (int i = 0; i < size; i++)
                } // if (containerNode.hasChildNodes ())
            } // if (containerType.equalsIgnoreCase (DIConstants.CONTAINER_EXTKEY))
            else    // container is not an external key
            {
                // read the value for custom operation
                text = (Text) containerNode.getFirstChild ();
                // text node found?
                if (text != null)
                {
                    containerId = text.getNodeValue ();
                } // if
            } // else container is not an external key
            // set the values in the importScriptElement
            dataElement.containerType = containerType;
            dataElement.containerId = containerId;
            dataElement.containerIdDomain = containerIdDomain;
            dataElement.containerTabName = containerTabName;
        } // if (attributes != null)
    } // parseContainerNode


    /**************************************************************************
     * parses the VALUES section of the import xml document and
     * stores value entries found in the DataElement. <BR/>
     *
     * @param dataElement  DataElement that holds the values info
     * @param root  root node of the tree to be parsed
     */
    private void parseValuesSection (DataElement dataElement, Node root)
    {
        NodeList valuesNodeList;
        int valuesSize;
        Node valueNode;
        NamedNodeMap valuesAttributes;
        String valueName;
        String valueType;
        String valueValue;
        String valueMandatory;
        String valueReadonly;
        String valueInfo;
        String valueTypeFilter;
        String valueSearchRoot;
        String valueSearchRootIdDomain;
        String valueSearchRootId;
        String valueSearchRecursive;
        String valueQueryName;
        String valueMappingField;
        String valueOptions;
        String valueUnit;
        String valueEmptyOption;
        String valueRefresh;
        String valueDomain;
        String valueContext;
        String valueViewType;
        String valueNoColumns;
        String valueMultiSelection;
        long valueSize;
        Text text;
        Vector<?> valueSubTags;
        Vector<Serializable> reminderParams;
        String valueMlKey;
        String valueShowInLinks;

        valuesNodeList = root.getChildNodes ();
        valuesSize = valuesNodeList.getLength ();
        // now go through all the values
        for (int iv = 0; iv < valuesSize; iv++)
        {
            // initialize the values
            valueName               = "";
            valueType               = "";
            valueValue              = "";
            valueMandatory          = "";
            valueReadonly           = "";
            valueInfo               = "";
            valueTypeFilter         = "";
            valueSearchRoot         = "";
            valueSearchRootIdDomain = "";
            valueSearchRootId       = "";
            valueSearchRecursive    = "";
            valueQueryName          = "";
            valueMappingField       = "";
            valueOptions            = "";
            valueUnit               = "";
            valueEmptyOption        = "";
            valueRefresh            = "";
            valueDomain             = "";
            valueSize               = -1;
            valueSubTags            = null;
            reminderParams          = null;
            valueContext            = "";
            valueViewType           = null;
            valueNoColumns          = null;
            valueMultiSelection     = null;
            valueMlKey              = null;
            valueShowInLinks        = "";

            // get value node from values nodelist
            valueNode = valuesNodeList.item (iv);
            // check if this is an element node
            if (valueNode.getNodeType () == Node.ELEMENT_NODE)
            {
                // get the values from an VALUES element
                // get field name
                valuesAttributes = valueNode.getAttributes ();
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_FIELD) != null)
                {
                    valueName = valuesAttributes.getNamedItem (DIConstants.ATTR_FIELD).getNodeValue ().trim ();
                } // if
                // get ml key
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_MLKEY) != null)
                {
                    valueMlKey = valuesAttributes.getNamedItem (DIConstants.ATTR_MLKEY).getNodeValue ().trim ();
                } // if

                // get the mappingfield attribute
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_DBFIELD) != null)
                {
                    valueMappingField = valuesAttributes.getNamedItem (DIConstants.ATTR_DBFIELD).getNodeValue ().trim ();
                } // if
                // get field type
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_TYPE) != null)
                {
                    valueType = valuesAttributes.getNamedItem (DIConstants.ATTR_TYPE).getNodeValue ().trim ();
                } // if
                // get mandatory flag
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_MANDATORY) != null)
                {
                    valueMandatory = valuesAttributes.getNamedItem (DIConstants.ATTR_MANDATORY).getNodeValue ().trim ();
                } // if
                // get readonly flag
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_READONLY) != null)
                {
                    valueReadonly = valuesAttributes.getNamedItem (DIConstants.ATTR_READONLY).getNodeValue ().trim ();
                } // if
                // get field info
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_INFO) != null)
                {
                    valueInfo = valuesAttributes.getNamedItem (DIConstants.ATTR_INFO).getNodeValue ().trim ();
                } // if
                // get the type name filter for objectref field
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_TYPEFILTER) != null)
                {
                    valueTypeFilter = valuesAttributes.getNamedItem (DIConstants.ATTR_TYPEFILTER).getNodeValue ().trim ();
                } // if
                // get the type code filter for OBJECTREF field
                // this is a new attribute and overwrites the old TYPEFILTER attribute
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_TYPECODEFILTER) != null)
                {
                    valueTypeFilter = valuesAttributes.getNamedItem (DIConstants.ATTR_TYPECODEFILTER).getNodeValue ().trim ();
                } // if
                // get the searchroot for OBJECTREF field
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_SEARCHROOT) != null)
                {
                    valueSearchRoot = valuesAttributes.getNamedItem (DIConstants.ATTR_SEARCHROOT).getNodeValue ().trim ();
                } // if
                // get the searchroot id domain for OBJECTREF field
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_SEARCHROOTIDDOMAIN) != null)
                {
                    valueSearchRootIdDomain = valuesAttributes.getNamedItem (DIConstants.ATTR_SEARCHROOTIDDOMAIN).getNodeValue ().trim ();
                } // if
                // get the searchroot id for OBJECTREF field
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_SEARCHROOTID) != null)
                {
                    valueSearchRootId = valuesAttributes.getNamedItem (DIConstants.ATTR_SEARCHROOTID).getNodeValue ().trim ();
                } // if
                // get the searchrecursive flag for OBJECTREF field
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_SEARCHRECURSIVE) != null)
                {
                    valueSearchRecursive = valuesAttributes.getNamedItem (DIConstants.ATTR_SEARCHRECURSIVE).getNodeValue ().trim ();
                } // if
                // get the searchrecursive flag for OBJECTREF field
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_QUERYNAME) != null)
                {
                    valueQueryName = valuesAttributes.getNamedItem
                    (DIConstants.ATTR_QUERYNAME).getNodeValue ().trim ();
                } // if
                // get isemptyOption allowed in QUERYSELECTIONBOX
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_EMPTYOPTION) != null)
                {
                    valueEmptyOption = valuesAttributes.getNamedItem
                    (DIConstants.ATTR_EMPTYOPTION).getNodeValue ().trim ();
                } // if
                // get the refresh attribute for the QUERYSELECTIONBOX
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_REFRESH) != null)
                {
                    valueRefresh = valuesAttributes.getNamedItem
                    (DIConstants.ATTR_REFRESH).getNodeValue ().trim ();
                } // if
                // get the options for the QUERYSELECTIONBOX or the SELECTIONBOX
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_OPTIONS) != null)
                {
                    valueOptions = valuesAttributes.getNamedItem
                    (DIConstants.ATTR_OPTIONS).getNodeValue ().trim ();
                } // if
                // get the options for the DOMAIN field type
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_DOMAIN) != null)
                {
                    valueDomain = valuesAttributes.getNamedItem
                    (DIConstants.ATTR_DOMAIN).getNodeValue ().trim ();
                } // if
                // get the options for the SIZE field type
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_SIZE) != null)
                {
                    valueSize = Long.parseLong (valuesAttributes.getNamedItem
                        (DIConstants.ATTR_SIZE).getNodeValue ().trim ());
                } // if
                // if type is FIELDREF or VALUEDOMAIN
                if (DIConstants.VTYPE_FIELDREF.equals (valueType) || DIConstants.VTYPE_VALUEDOMAIN.startsWith (valueType))
                {
                    valueSubTags = this.parseSubRefSection (valueNode, valueType);
                } // if
                // get the view type attribute for the SELECTIONBOX and QUERYSELECTIONBOX
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_VIEWTYPE) != null)
                {
                    valueViewType = valuesAttributes.getNamedItem
                    (DIConstants.ATTR_VIEWTYPE).getNodeValue ().trim ();
                } // if
                // get the no columns attribute for the SELECTIONBOX and QUERYSELECTIONBOX
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_NO_COLUMNS) != null)
                {
                    valueNoColumns = valuesAttributes.getNamedItem
                    (DIConstants.ATTR_NO_COLUMNS).getNodeValue ().trim ();
                } // if
                // get the multi selection attribute for the selectionbox,queryselectionbox and value domain
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_MULTISELECTION) != null)
                {
                    valueMultiSelection = valuesAttributes.getNamedItem
                    (DIConstants.ATTR_MULTISELECTION).getNodeValue ().trim ();
                } // if
                // get show in links flag
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_SHOWINLINKS) != null)
                {
                    valueShowInLinks = valuesAttributes.getNamedItem (
                        DIConstants.ATTR_SHOWINLINKS).getNodeValue ().trim ();
                } // if
                // if type is REMINDER
                if (DIConstants.VTYPE_REMINDER.equals (valueType))
                {
                    reminderParams = this.parseReminderAttributes (valuesAttributes);
                } // else if
                // get the attribute context for VALUEDOMAIN
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_CONTEXT) != null)
                {
                    valueContext = valuesAttributes.getNamedItem
                    (DIConstants.ATTR_CONTEXT).getNodeValue ().trim ();
                } // if
                // get field value
                text = (Text) valueNode.getFirstChild ();
                if (text != null)
                {
                    valueValue = text.getNodeValue ().trim ();
                } // if

                // if no value type is defined we assume it as TEXT
                if (valueType == null)
                {
                    valueType = DIConstants.VTYPE_TEXT;
                } // if
                // store the value in the dataElement
                dataElement.addValue (valueName, valueType, valueValue,
                        valueMandatory, valueReadonly, valueInfo, valueTypeFilter,
                        valueSearchRoot, valueSearchRootIdDomain, valueSearchRootId, valueSearchRecursive, valueMappingField,
                        valueQueryName, valueOptions, valueUnit, valueEmptyOption,
                        valueRefresh, valueSubTags, valueDomain, valueSize,
                        reminderParams, valueContext, valueViewType, valueNoColumns,
                        valueMultiSelection, null, valueMlKey, valueShowInLinks);
            } // if (valueNode.getNodeType () == Node.ELEMENT_NODE)
        } // for (int iv = 0; iv < valuesSize; iv++)
    } // parseValuesSection


    /**************************************************************************
     * Parse Section for ref types with sub tags (e.g. "FIELDREF", "VALUEDOMAIN", ...) for import. <BR/>
     *
     * @param   valueNode   From ref type value.
     * @param   contextType Type of context for parsing.
     *
     * @return  A list of all sub tags.
     */
    protected Vector<ReferencedObjectInfo> parseSubRefSection (
                                                               Node valueNode,
                                                               String contextType)
    {
        Vector<ReferencedObjectInfo> valueSubTags = new Vector<ReferencedObjectInfo> ();

        NodeList valueNodeList = null;
        Node valueSubNode = null;
        NamedNodeMap valuesSubNodeAttributes = null;

        Node fieldsNode = null;
        NodeList fieldNodeList = null;

        String fieldName = null;
        String fieldToken = null;

        // find FIELDS TAG
        valueNodeList = valueNode.getChildNodes ();
        int valueSize = valueNodeList.getLength ();

        for (int i = 0; i < valueSize; i++)
        {
            fieldsNode = valueNodeList.item (i);

            if (fieldsNode.getNodeType () == Node.ELEMENT_NODE)
            {
                fieldNodeList = fieldsNode.getChildNodes ();
            } // if
        } // for i

        // check if Tag FIELDS was found
        if (fieldNodeList == null)
        {
            this.showDebug ("m2XMLFilter.parseSubRefSection" +
                " Error: Tag FIELDS is missing in " + contextType + "-Value.");
            return null;
        } // else

        int size = (fieldNodeList != null) ? fieldNodeList.getLength () : 0;

        // now go through all the values
        for (int i = 0; i < size; i++)
        {
            valueSubNode = fieldNodeList.item (i);
            // check if this is an element node
            if (valueSubNode.getNodeType () == Node.ELEMENT_NODE)
            {

                // check if node = SYSFIELD or FIELD
                if (!valueSubNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_SYSFIELD) &&
                    !valueSubNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_FIELD))
                {
                    this.showDebug ("m2XMLFilter.parseSubRefSection" +
                                 " Error: Wrong Tagname in FIELDS section " +
                                 "of " + contextType + "-Value: [" +
                                 valueSubNode.getNodeName () + "]");
                } // if other Tag than SYSFIELD or FIELD is used

                valuesSubNodeAttributes =
                    valueSubNode.getAttributes ();

                // fieldName
                if (valuesSubNodeAttributes
                    .getNamedItem (DIConstants.ATTR_NAME) != null)
                {
                    fieldName = valuesSubNodeAttributes.getNamedItem (
                        DIConstants.ATTR_NAME).getNodeValue ();
                } // if

                // fieldToken
                if (valuesSubNodeAttributes
                    .getNamedItem (DIConstants.ATTR_TOKEN) != null)
                {
                    fieldToken = valuesSubNodeAttributes.getNamedItem (
                        DIConstants.ATTR_TOKEN).getNodeValue ();
                } // if

                // isSysField
                if (valueSubNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_SYSFIELD))
                {
                    valueSubTags.addElement (
                        new ReferencedObjectInfo (
                            ReferencedObjectInfo.TYPE_SYSTEM, fieldName,
                            fieldToken));
                } // if (valueSubNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_SYSFIELD)
                else    // not a sysfield
                {
                    valueSubTags.addElement (
                        new ReferencedObjectInfo (
                            ReferencedObjectInfo.TYPE_STANDARD,
                            fieldName, fieldToken));
                } // not a sysfield
            } // if (valueSubNode.getNodeType () == Node.ELEMENT_NODE)
        } // for
        return valueSubTags;
    } // parseSubRefSection


    /***************************************************************************
     * Parse Section for VALUE FIELD="REMINDER" for import. <BR/> Reminder
     * parameters:
     *
     * <PRE>
     * 0 ... display type for reminder
     * 1 ... days interval for reminder 1
     * 2 ... notification text for reminder 1
     * 3 ... recipient(s) for reminder 1
     * 4 ... possible recipients for reminder 1
     * 5 ... days interval for reminder 2
     * 6 ... notification text for reminder 2
     * 7 ... recipient(s) for reminder 2
     * 8 ... possible recipients for reminder 2
     * 9 ... days interval for escalation
     * 10 ... notification text for escalation
     * 11 ... recipient(s) for escalation
     * 12 ... possible recipients for escalation
     * </PRE>
     *
     * @param valuesAttributes Attributes of the REMINDER value.
     *
     * @return The found reminder parameters, <CODE>null</CODE> if there
     *         occurred an error.
     */
    protected Vector<Serializable> parseReminderAttributes (NamedNodeMap valuesAttributes)
    {
        Vector<Serializable> reminderParams = new Vector<Serializable> ();

        // get value for display type:
        this.addAttributeValue (reminderParams, valuesAttributes,
            DIConstants.ATTR_DISPLAY);

        // get values for reminder 1:
        this.addAttributeValueInt (reminderParams, valuesAttributes,
            DIConstants.ATTR_REMIND1DAYS);
        this.addAttributeValue (reminderParams, valuesAttributes,
            DIConstants.ATTR_REMIND1TEXT);
        this.addAttributeValue (reminderParams, valuesAttributes,
            DIConstants.ATTR_REMIND1RECIP);
        this.addAttributeValue (reminderParams, valuesAttributes,
            DIConstants.ATTR_REMIND1RECIPQUERY);

        // get values for reminder 2:
        this.addAttributeValueInt (reminderParams, valuesAttributes,
            DIConstants.ATTR_REMIND2DAYS);
        this.addAttributeValue (reminderParams, valuesAttributes,
            DIConstants.ATTR_REMIND2TEXT);
        this.addAttributeValue (reminderParams, valuesAttributes,
            DIConstants.ATTR_REMIND2RECIP);
        this.addAttributeValue (reminderParams, valuesAttributes,
            DIConstants.ATTR_REMIND2RECIPQUERY);

        // get values for escalation:
        this.addAttributeValueInt (reminderParams, valuesAttributes,
            DIConstants.ATTR_ESCALATEDAYS);
        this.addAttributeValue (reminderParams, valuesAttributes,
            DIConstants.ATTR_ESCALATETEXT);
        this.addAttributeValue (reminderParams, valuesAttributes,
            DIConstants.ATTR_ESCALATERECIP);
        this.addAttributeValue (reminderParams, valuesAttributes,
            DIConstants.ATTR_ESCALATERECIPQUERY);

        // return the computed vector:
        return reminderParams;
    } // parseReminderAttributes


    /**************************************************************************
     * Add the value of an attribute to a vector. <BR/>
     * If the value was not found <CODE>null</CODE> is added to the vector.
     *
     * @param   values      Vector to which the value shall be added.
     * @param   attributes  Attributes where to search.
     * @param   attrName    Name of the attribute.
     */
    protected void addAttributeValue (Vector<Serializable> values, NamedNodeMap attributes,
                                      String attrName)
    {
        // get value of the field unit
        if (attributes.getNamedItem (attrName) != null)
        {
            // add the value to the vector:
            values.add (attributes.getNamedItem (attrName).getNodeValue ());
        } // if
        else
        {
            // add null value:
            values.add (null);
        } // else
    } // addAttributeValue


    /**************************************************************************
     * Add the value of an attribute to a vector. <BR/>
     * The value will be converted to Integer. <BR/>
     * If the value was not found <CODE>null</CODE> is added to the vector.
     *
     * @param   values      Vector to which the value shall be added.
     * @param   attributes  Attributes where to search.
     * @param   attrName    Name of the attribute.
     */
    protected void addAttributeValueInt (Vector<Serializable> values, NamedNodeMap attributes,
                                         String attrName)
    {
        // get value of the field unit
        if (attributes.getNamedItem (attrName) != null)
        {
            try
            {
                // add the value to the vector:
                values.add (Integer.valueOf (attributes.getNamedItem (attrName)
                    .getNodeValue (), 10));
            } // try
            catch (NumberFormatException e)
            {
                // should not occur, display error message:
                IOHelpers.printError (
                    "Error during getting attribute value for \"" +
                    attrName + "\"",
                    this, e, true);
            } // catch
            catch (DOMException e)
            {
                IOHelpers.printError (
                    "Error during adding attribute value for \"" +
                    attrName + "\"",
                    this, e, true);
            } // catch
        } // if
        else
        {
            // add null value:
            values.add (null);
        } // else
    } // addAttributeValue


    /**************************************************************************
     * parses the RIGHTS section of the import xml document and
     * stores right entries found in the DataElement. <BR/>
     *
     * @param dataElement  DataElement that holds the rights info
     * @param root    root node of the tree to be parsed
     */
    private void parseRightsSection (DataElement dataElement, Node root)
    {
        NodeList rightsNodeList;
        int rightsSize;
        Node rightNode;
        NamedNodeMap rightsAttributes;
        String rightName = "";
        String rightType = "";
        String rightProfile = "";
        Node nameNode;
        Node typeNode;
        Node profileNode;

        rightsNodeList = root.getChildNodes ();
        rightsSize = rightsNodeList.getLength ();
        // now go through all the values
        for (int i = 0; i < rightsSize; i++)
        {
            // reset the values
            rightName       = "";
            rightType       = "";
            rightProfile    = "";
            // get value node from values nodelist
            rightNode = rightsNodeList.item (i);
            // check if this is an element node
            if (rightNode.getNodeType () == Node.ELEMENT_NODE)
            {
                // get the values from an VALUES element
                // get attributes
                rightsAttributes = rightNode.getAttributes ();
                // get name attribute
                nameNode = rightsAttributes.getNamedItem (DIConstants.ATTR_NAME);
                if (nameNode != null)
                {
                    rightName = nameNode.getNodeValue ().trim ();
                } // if
                else
                {
                    rightName = "";
                } // else
                // get type attribute
                typeNode = rightsAttributes.getNamedItem (DIConstants.ATTR_TYPE);
                if (typeNode != null)
                {
                    rightType = typeNode.getNodeValue ().trim ();
                } // if
                else
                {
                    rightType = "";
                } // else
                // get profile or alias attribuite
                // both are valid at the moment but alias should be the one used in the future
                profileNode = rightsAttributes.getNamedItem (DIConstants.ATTR_PROFILE);
                if (profileNode != null)
                {
                    rightProfile = profileNode.getNodeValue ().trim ();
                } // if (aliasNode != null)
                else    // no profile attribute available try the alias attribute
                {
                    // try to get the alias attribute
                    profileNode = rightsAttributes.getNamedItem (DIConstants.ATTR_ALIAS);
                    if (profileNode != null)
                    {
                        rightProfile = profileNode.getNodeValue ().trim ();
                    } // if
                    else
                    {
                        rightProfile = "";
                    } // else
                } // else no profile attribute available try the alias attribute
                // store the value but only if there are all values available
                if (rightName.length () > 0 || rightType.length () == 0 ||
                    rightProfile.length () == 0)
                {
                    dataElement.addRight (rightName, rightType, rightProfile);
                } // if
            } // if (rightNode.getNodeType () == Node.ELEMENT_NODE)

        } // for (int i = 0; i < rightsSize; i++)
    } // parseRightsSection


    /**************************************************************************
     * parses the REFERENCES section of the import xml document and
     * stores reference entries found in the DataElement. <BR/>
     *
     * @param dataElement  DataElement that holds the rights info
     * @param root   root node of the tree to be parsed
     */
    private void parseReferencesSection (DataElement dataElement, Node root)
    {
        NodeList refNodeList;
        int refSize;
        int size;
        Node refNode;
        NamedNodeMap refAttributes;
        NamedNodeMap attributes;
        String containerType        = "";
        String containerId          = "";
        String containerIdDomain    = "";
        String containerTabName     = "";
        Text text;
        Node node;
        NodeList nodelist;
        Node attributeNode;

        refNodeList = root.getChildNodes ();
        refSize = refNodeList.getLength ();
        // now go through all the values
        for (int i = 0; i < refSize; i++)
        {
            // reset the values
            containerType       = "";
            containerId         = "";
            containerIdDomain   = "";
            // get value node from values nodelist
            refNode = refNodeList.item (i);
            // check if this is an element node
            if (refNode.getNodeType () == Node.ELEMENT_NODE)
            {
                // get attributes
                refAttributes = refNode.getAttributes ();
                // get container tab name
                attributeNode = refAttributes.getNamedItem (DIConstants.ATTR_TABNAME);
                if (attributeNode != null)
                {
                    containerTabName = attributeNode.getNodeValue ();
                } // if
                // get container type attribute
                attributeNode = refAttributes.getNamedItem (DIConstants.ATTR_TYPE);
                if (attributeNode != null)
                {
                    containerType = attributeNode.getNodeValue ();
                } // if
                // check if the containerType is a external key.
                // in that case we have to read a more complex structure
                // in the form:
                // <CONTAINER TYPE="EXTKEY">
                //    <ID DOMAIN="app">123</ID>
                // </CONTAINER>
                if (containerType.equalsIgnoreCase (DIConstants.CONTAINER_EXTKEY))
                {
                    // check if there are sub nodes
                    if (refNode.hasChildNodes ())
                    {
                        // parse through the OBJECT sections
                        nodelist = refNode.getChildNodes ();
                        // get amount of objects
                        size = nodelist.getLength ();
                        // find the <OBJECTS> element
                        for (int ii = 0; ii < size; ii++)
                        {
                            // get the node
                            node = nodelist.item (ii);
                            // check if it is an ID node
                            if (node.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_ID))
                            {
                                // get the id domain attribute
                                attributes = node.getAttributes ();
                                attributeNode = attributes.getNamedItem (DIConstants.ATTR_DOMAIN);
                                // check if DOMAIN attribute has been set
                                if (attributeNode != null)
                                {
                                    containerIdDomain = attributeNode.getNodeValue ();
                                } // if
                                // get the text value
                                text = (Text) node.getFirstChild ();
                                if (text != null)
                                {
                                    containerId = text.getNodeValue ();
                                } // if
                            } // if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_ID))
                        } // for (int ii = 0; ii < size; ii++)
                    } // if (containerNode.hasChildNodes ())
                } // if (containerType.equalsIgnoreCase (DIConstants.CONTAINER_EXTKEY))
                else    // container is not an external key
                {
                    // read the value for custom operation
                    text = (Text) refNode.getFirstChild ();
                    // text node found?
                    if (text != null)
                    {
                        containerId = text.getNodeValue ();
                    } // if
                } // else container is not an external key
                // store the value but only if there are all values available
                if (containerType.length () > 0)
                {
                    dataElement.addReference (containerType, containerId,
                                              containerIdDomain, containerTabName);
                } // if
            } // if (refNode.getNodeType () == Node.ELEMENT_NODE)
        } // for (int i = 0; i < referencesSize; i++)
    } // parseReferencesSection


    /**************************************************************************
     * Shows a debug message. <BR/>
     *
     * @param   message Debug message to be shown.
     */
    public void showDebug (String message)
    {
        if (this.isDebug)
        {
            System.out.println ("DEBUG: " + message);
        } // if
    } // showDebug

} // m2XDiff

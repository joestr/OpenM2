/*
 * Class: Log_01.java
 */

// package:
package ibs.di.imp;

// imports:
import java.util.Date;


/******************************************************************************
 * The ImportThread object implements an import agent that runs as a thread. <BR/>
 * The ImportThread checks regularily an import source (can be an server
 * directory or an url) for a specific importFile or looks for any xml file
 * to import from.
 *
 * @version     $Id: ImportThread.java,v 1.7 2007/07/31 19:13:55 kreimueller Exp $
 *
 * @author      Buchegger Bernd (BB), 990128
 ******************************************************************************
 */
public class ImportThread extends Object implements Runnable
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ImportThread.java,v 1.7 2007/07/31 19:13:55 kreimueller Exp $";


    /**
     *  name of the import file. <BR/>
     */
    public String importFileName = "";

    /**
     * flag if a specific import file name shall be used or if any
     * xml file will be imported. <BR/>
     */
    public boolean usesFileName = true;

    /**
     *  path of server directory to import from. <BR/>
     */
    public String importSourcePath = "";

    /**
     *  URL to import from. <BR/>
     */
    public String importSourceURL;

    /**
     *  flag if server directory path shall be used or the URL. <BR/>
     */
    public boolean useImportSourcePath;

    /**
     * full file name and path to the import script if available. <BR/>
     */
    public String importScriptFilePath;

    /**
     * frequency in minutes to check the import source. <BR/>
     */
    public String checkFrequency;

    /**
     * specific time when to check the import source. <BR/>
     */
    public String checkAtTime;

    /**
     * array of days when to check the import source. this works togehter with the
     * checkAtTime property. <BR/>
     */
    public String[] checkAtDays;

    /**
     * start date from which to check the import source. <BR/>
     */
    public Date checkFromDate;

    /**
     * end date to which to check the import source. <BR/>
     */
    public Date checkUntilDate;


    /**************************************************************************
     * Creates an ImportThread object. <BR/>
     */
    public ImportThread ()
    {
        // call constructor of super class ObjectReference:

    } // ImportThread


    /**************************************************************************
     * When an object implementing interface Runnable is used to create a
     * thread, starting the thread causes the object's run method to be called
     * in that separately executing thread.
     */
    public void run ()
    {
/*
        // determine wheather to listen to a server directory path or to an url
        if (this.useImportSourcePath)
        {
        } // if (useImportSourcePath)
        else
        {
        } // else
*/
        // check whether to check frequently or to check at specific days

        // if check at specific days check if it is the right day
    } // run

} // ImportThread

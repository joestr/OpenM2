/*
 * Class: AgentErrorFile.java
 */

// package:
package ibs.di.agent;

// imports:


/******************************************************************************
 * The AgentErrorFile class hold the data of an file that could not be
 * processed by the agent and the number of retries. <BR/>
 *
 * @version     $Id: AgentErrorFile.java,v 1.5 2007/07/31 19:13:53 kreimueller Exp $
 *
 * @author      Buchegger Bernd (BB), 20010731
 ******************************************************************************
 */
public class AgentErrorFile extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: AgentErrorFile.java,v 1.5 2007/07/31 19:13:53 kreimueller Exp $";


    /**
     *  name of file that caused an error. <BR/>
     */
    public String fileName = "";

    /**
     *  Number of retries. <BR/>
     */
    public int retries = 1;


    /**************************************************************************
     * Creates an AgentErrorFile instance. <BR/>
     */
    public AgentErrorFile ()
    {
        // nothing to do
    } // AgentErrorFile


    /**************************************************************************
     * Creates an AgentErrorFile instance. <BR/>
     *
     * @param fileName      name of file
     */
    public AgentErrorFile (String fileName)
    {
        // call constructor of super class ObjectReference:
        this.fileName = fileName;
        this.retries = 1;
    } // AgentErrorFile


    /**************************************************************************
     * Increases the number of retries. <BR/>
     */
    public void increaseRetries ()
    {
        // call constructor of super class ObjectReference:
        this.retries += 1;
    } // increaseRetries

} // class AgentErrorFile

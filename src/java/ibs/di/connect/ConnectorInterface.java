/*
 * Class: ConnectorInterface.java
 */

// package:
package ibs.di.connect;

// imports:
import ibs.di.connect.ConnectionFailedException;


/******************************************************************************
 * The ConnectorInterface class represents the encapsulation of import and export
 * streams. The interfaces privide methods to access data streams or write
 * to data streams in order to get or receive data from various data sources
 * or deliver data to export destinations. Each implementing subclass must
 * implement the methods to write to and to read from data streams.<BR/>
 *
 * @version     $Id: ConnectorInterface.java,v 1.8 2007/07/27 12:01:54 kreimueller Exp $
 *
 * @author      Buchegger Bernd (BB), 991102
 ******************************************************************************
 */
interface ConnectorInterface
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ConnectorInterface.java,v 1.8 2007/07/27 12:01:54 kreimueller Exp $";


    /**************************************************************************
     * Initializes the connector.<BR/>
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established
     */
    abstract void initConnector ()
        throws ConnectionFailedException;


    /**************************************************************************
     * Closes the connector.<BR/>
     */
    abstract void close ();


    /**************************************************************************
     * The dir method reads from the import source and returns all importable
     * objects found in a array of strings.
     *
     * @return  An array of strings containing the importable objects found or
     *          <CODE>null</CODE> otherwise
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established
     */
    abstract String[] dir () throws ConnectionFailedException;


    /**************************************************************************
     * Writes an export file to the export destination.<BR/>
     *
     * @param   fileName    the name of the source file.
     *
     * @exception   ConnectionFailedException
     *              The connection could not be established
     */
    abstract void write (String fileName) throws ConnectionFailedException;


    /**************************************************************************
     * Reads an import file from the import source.<BR/>
     *
     * @param   fileName    the name of the file to read
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established
     */
    abstract void read (String fileName) throws ConnectionFailedException;


    /**************************************************************************
     * Read a file from the connector and copy it to the destination path. <BR/>
     * This is meant for attachment like files that can have a different
     * handling as importfiles depending on the connector used.<BR/>
     *
     * @param fileName              name of the file to read
     * @param destinationPath       the path to write the file to
     * @param destinationFileName   name of the copied file.
     *                              If empty fileName will be used.
     *
     * @return the size of the file in case it could have been read successfully or
     *         -1 if an error occurred or the file has not been found
     *
     * @exception   ConnectionFailedException
     *              could not access the file
     */
    abstract long readFile (String fileName,
                            String destinationPath,
                            String destinationFileName)
        throws ConnectionFailedException;


    /**************************************************************************
     * Write an file to a connector. <BR/>
     * This is meant for attachment like files. The method has to ensure a
     * unique file name. It returns the file name used to write the file and
     * null in case the file could not have been written. <BR/>
     *
     * @param sourcePath        path to read the file from
     * @param fileName          name of the file to read
     *
     * @return the name of the file written or null in case it could not have
     *         been written
     *
     * @exception   ConnectionFailedException
     *              could not access the file
     */
    abstract String writeFile (String sourcePath, String fileName)
        throws ConnectionFailedException;


    /**************************************************************************
     * Delete a file from its original location via the connector.<BR/>
     * This will be used in case the "delete file after import" option
     * has been set within an import and is meant for all sorts of
     * attachment like files.
     * This can be used for the importfile itself and for attachment like
     * files.<BR/>
     *
     * @param fileName            name of the file to delete
     *
     * @return true if the file could be deleted or false otherwiese
     *
     * @exception   ConnectionFailedException
     *              could not access the file
     */
    public boolean deleteFile (String fileName)
        throws ConnectionFailedException;

} // ConnectorInterface

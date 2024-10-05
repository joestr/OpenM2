/*
 * Class: FileDataElement.java
 */

// package:
package ibs.di;

// imports:
import ibs.BaseObject;


/******************************************************************************
 * The FileDataElement holds the information of an file to be imported or
 * exported via the data interchange services. <BR/>
 *
 * @version     $Id: FileDataElement.java,v 1.6 2007/08/10 14:56:37 kreimueller Exp $
 *
 * @author      Buchegger Bernd (BB), 20010322
 ******************************************************************************
 */
public class FileDataElement extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FileDataElement.java,v 1.6 2007/08/10 14:56:37 kreimueller Exp $";


    /**
     * Name of field that holds the filename. <BR/>
     * Needed because filename can be changed due to ambiguities
     */
    public String field = null;

    /**
     *  path of file. <BR/>
     */
    public String path = null;

    /**
     *  name of file to be exported. <BR/>
     */
    public String fileName = null;

    /**
     *  size of file. <BR/>
     */
    public long size = -1;


    /**************************************************************************
     * Creates an FileDataElement. <BR/>
     */
    public FileDataElement ()
    {
        // nothing to do
    } // FileDataElement


    /**************************************************************************
     * Creates an FileDataElement. <BR/>
     *
     * @param field         name of field that holds the filename
     * @param path          path of file
     * @param fileName      name of file
     */
    public FileDataElement (String field, String path, String fileName)
    {
        // call constructor of super class ObjectReference:
        this.field = field;
        this.path = path;
        this.fileName = fileName;
        this.size = -1;
    } // FileDataElement


    /**************************************************************************
     * Creates an FileDataElement. <BR/>
     *
     * @param field         name of field that holds the filename
     * @param path          path of file
     * @param fileName      name of file
     * @param size          size of file
     */
    public FileDataElement (String field, String path, String fileName, long size)
    {
        // call constructor of super class ObjectReference:
        this.field = field;
        this.path = path;
        this.fileName = fileName;
        this.size = size;
    } // FileDataElement

} // class FileDataElement

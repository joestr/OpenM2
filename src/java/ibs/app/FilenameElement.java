/*
 * Class: FilenameElement.java
 */

// package:
package ibs.app;

// imports:
import ibs.BaseObject;


/******************************************************************************
 * This class holds one element for a form field of an upload specific type
 * (e.g. DT_FILE, DT_IMAGE; DT_PICTURE, DT_THUMBNAIL). <BR/> In detail it
 * holds the filname itself, the name of the field, the upload path
 * and the absolute (server-side) upload path
 *
 * @version     $Id: FilenameElement.java,v 1.5 2007/07/05 16:02:51 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 980729
 ******************************************************************************
 */
public class FilenameElement extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FilenameElement.java,v 1.5 2007/07/05 16:02:51 kreimueller Exp $";


    /**
     * Holds the filename itself
     */
    public String filename;

    /**
     * Holds the fieldname (field which holds filename)
     */
    public String filenameField;

    /**
     * Was the filename changed (OK-button in UPLOAD pressed?)
     */
    public boolean changed;

    /**
     * Holds the upload path (web)
     */
    public String uploadPath;

    /**
     * Holds the absolute upload path
     */
    public String uploadPathAbs;


    /**************************************************************************
     * Creates a FilenameElement object. <BR/>
     */
    public FilenameElement ()
    {
        // init attributes
        this.filename = "";
        this.filenameField = "";
        this.changed = false;
        this.uploadPath = "";
        this.uploadPathAbs = "";
    } // FilenameElement

} // class TerminplanContainerElement_01

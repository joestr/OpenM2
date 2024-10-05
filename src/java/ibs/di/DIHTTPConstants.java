/*
 * Class: DIHTTPConstants.java
 */

// package:
package ibs.di;

// imports:
import ibs.BaseObject;
import ibs.util.UtilConstants;


/******************************************************************************
 * Constants for ibs.di.HTTPScriptConnector objects. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the objects delivered within this package.
 *
 * @version     $Id: DIHTTPConstants.java,v 1.5 2007/08/10 14:56:37 kreimueller Exp $
 *
 * @author      Daniel Janesch (DJ), 020312
 ******************************************************************************
 */
public abstract class DIHTTPConstants extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DIHTTPConstants.java,v 1.5 2007/08/10 14:56:37 kreimueller Exp $";


    /**
     * the HTTP protocoll identifier in an url string. <BR/>
     */
    public static final String HTTP_PROTOCOL = "HTTP";

    // Mimetypes:
    /**
     * Mimetype: form data. <BR/>
     */
    public static final String MIMETYPE_FORMDATA   = "multipart/form-data";
    /**
     * Mimetype: plain text. <BR/>
     */
    public static final String MIMETYPE_TEXTPLAIN  = "text/plain";
    /**
     * Mimetype: xml data. <BR/>
     */
    public static final String MIMETYPE_TEXTXML    = "text/xml";

    /**
     * The content type of the following data. <BR/>
     */
    public static final String CONTENT_TYPE        = "Content-type";

    /**
     * The content length of the following data. <BR/>
     */
    public static final String CONTENT_LENGTH      = "Content-length";

    /**
     * This is the separator for the content type definition. <BR/>
     * Because if the content type is "mulitpart/form-data", a boundary has to
     * be defined additionally to the content type. <BR/>
     */
    public static final String CONTENT_TYPEDEL     = "; ";
    /**
     * This string is the boundary deffenition fot the content. <BR/>
     * This boundary sepparates all parameter sections.
     */
    public static final String CONTENT_BOUNDARY       =
        "boundary=" + UtilConstants.TAG_NAME + "";

    /**
     * The line brak for a multipart content. <BR/>
     */
    public static final String MULTIPART_LINEBREAK = "\r\n";
    /**
     * The separator for a name-value or name-filename-file section. <BR/>
     */
    public static final String MULTIPART_PARAMDEL  = "--";
    /**
     * The sign for the beginning of a new boundary
     * (name-value or name-filename-file). <BR/>
     */
    public static final String MULTIPART_BOUNDARY  =
        "---------------------------7d2a1293f0314";
    /**
     * This string is allways used to define the name of the parameter in the
     * "multipart/form-data" content. <BR/>
     * <CODE><B>&lt;NAME&gt;</B></CODE> should be replaced by the real
     * parameter name.
     */
    public static final String MULTIPART_NAME      =
        "content-disposition: form-data; name=\"" + UtilConstants.TAG_NAME + "\"";
    /**
     * This is the seperator for the content dispositon form-datas. <BR/>
     * Because if the value of the current parameter is a file, this separator
     * has to be added between the name and the filename. <BR/>
     */
    public static final String MULTIPART_DISPDEL   = "; ";
    /**
     * This string is allways used to define the filename of a fileparameter.
     * <BR/>
     */
    public static final String MULTIPART_FILENAME  =
        "filename=\"" + UtilConstants.TAG_NAME + "\"";

} // class DIHTTPConstants

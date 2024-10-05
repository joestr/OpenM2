/*
 * Class: Mimetype.java
 */

// package:
package ibs.tech.http;

// imports:
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;


/******************************************************************************
 * Representation of a Mimetype. <BR/>
 *
 * @version     $Id: Mimetype.java,v 1.4 2007/07/23 08:17:33 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 20060205
 ******************************************************************************
 */
public class Mimetype extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Mimetype.java,v 1.4 2007/07/23 08:17:33 kreimueller Exp $";


    /**
     * The mimetype text. <BR/>
     */
    public String p_mimetype = null;

    /**
     * Extension which is assigned to the mimetype. <BR/>
     * This value is only set if there is just one extension. If there are more
     * than one extensions this value is <CODE>null</CODE>.
     */
    public String p_extension = null;

    /**
     * Extensions which are assigned to the mimetype. <BR/>
     */
    public Vector<String> p_extensions = new Vector<String> ();

    /**
     * Description of the mimetype. <BR/>
     */
    public String p_description = null;



    /**************************************************************************
     * Create a new Mimetype instance. <BR/>
     *
     * @param   mimetype    The mimetype.
     * @param   extensions  Comma-separated list of extensions which are
     *                      assigned to the mimetype.
     * @param   description Description for the mimetype.
     */
    protected Mimetype (String mimetype, String extensions, String description)
    {
        // set the properties:
        this.p_mimetype = mimetype;
        this.p_description = description;

        // check if the extensions list contains a comma:
        if (extensions.indexOf (",") >= 0)
        {
            // create tokenizer to loop through the extensions:
            StringTokenizer tokenizer =
                new StringTokenizer (extensions, ", ", false);

            // loop through the extensions:
            while (tokenizer.hasMoreTokens ())
            {
                // get the next extension and add it to the list:
                this.p_extensions.add (tokenizer.nextToken ());
            } // while
        } // if
        else
        {
            // just one extension, add it to the list:
            this.p_extension = extensions;
            this.p_extensions.add (extensions);
        } // else
    } // Mimetype


    /**************************************************************************
     * Create a new Mimetype instance. <BR/>
     *
     * @param   mimetype    The mimetype.
     * @param   extensions  All extension which are assigned to the mimetype.
     * @param   description Description for the mimetype.
     */
    protected Mimetype (String mimetype, Vector<String> extensions, String description)
    {
        // set the properties:
        this.p_mimetype = mimetype;
        this.p_extensions.addAll (extensions);
        this.p_description = description;
    } // Mimetype


    /**************************************************************************
     * Get the mimetype for a specific extension. <BR/>
     * This method checks whether the extension is defined for the current mime
     * type. If this is <CODE>true</CODE> it returns the mime type.
     *
     * @param   extension   The extension to search for.
     *
     * @return  The mime type,
     *          <CODE>null</CODE> if the extension could not be found for this
     *          mime type.
     */
    protected String getMimetype (String extension)
    {
        // check if the search value is valid:
        if (extension != null && extension.length () > 0)
        {
            // search for the extension:
            if (this.p_extension != null)
            {
                // check if the extension is the one we search for:
                if (this.p_extension.equals (extension))
                {
                    // return the mime type:
                    return this.p_mimetype;
                } // if
            } // if
            else
            {
                // loop through all extensions and compare each of them with the
                // one we search for:
                for (Iterator<String> iter = this.p_extensions.iterator ();
                     iter.hasNext ();)
                {
                    // get the current extension:
                    String ext = iter.next ();
                    // compare it with the searched extension:
                    if (extension.equals (ext))
                    {
                        // return the mime type:
                        return this.p_mimetype;
                    } // if
                } // for iter
            } // else
        } // if

        // extension not found, return error value:
        return null;
    } // getMimetype

} // class Mimetype

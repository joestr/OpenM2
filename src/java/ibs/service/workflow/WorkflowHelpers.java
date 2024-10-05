/*
 * Class: WorkflowHelpers.java
 */

// package:
package ibs.service.workflow;

// imports:
import ibs.BaseObject;
import ibs.util.StringHelpers;

import java.io.File;


/******************************************************************************
 * The Helpers classes include various methods that are used by various other
 * workflow classes.
 *
 * @version     $Id: WorkflowHelpers.java,v 1.12 2007/07/31 19:13:59 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 18.10.2000
 ******************************************************************************
 */
public class WorkflowHelpers extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WorkflowHelpers.java,v 1.12 2007/07/31 19:13:59 kreimueller Exp $";


    /**************************************************************************
     * Builds a correct absolute path out of given base path and web path. <BR/>
     *
     * This is a little bit difficult to explain, so here is an example:
     *
     * - absSystemPath = "c:\\InetPub\\wwwroot\\m2\\"
     *      e.g. this is the m2AbsBasePath
     *
     * - relativeWebPath = "/m2/upload/files/.../xxx.xml"
     *      e.g. this is the upload path of an m2 file
     *
     * --> result should be "c:\\InetPub\\wwwroot\\m2\\upload\\...\\xxx.xml"
     *
     * Following steps are performed:
     * - cut first path-information of web path (/m2/)
     * - replace characters in web path (/ -> \\)
     * - concatenate pats
     *
     * @param   absoluteSystemPath  Absolute system path.
     * @param   relativeWebPath     Relative web path (part of an URL).
     *
     * @return  correct concatenated system path
     */
    public static String buildCorrectAbsSystemPath (String absoluteSystemPath,
                                                    String relativeWebPath)
    {
        // init variable
        String path = new String (relativeWebPath);

        // 1. step: cut first '/'
        path = path.substring (path.indexOf ("/") + 1);
        // 2. step: cut everything to next '/'
        path = path.substring (path.indexOf ("/") + 1);
        // 3. step: replace every "/" for
        // --> MS: "\\"
        // --> UNIX : "/"
        path = StringHelpers.replace (path, "/", File.separator);

        // set concatenated path
        return absoluteSystemPath + path;
    } // buildCorrectAbsSystemPath


    /**************************************************************************
     * Checks if given string1 contains given string2, case will be ignored. <BR/>
     *
     * @param   string1   a string
     * @param   string2   the other string
     *
     * @return  <CODE>true</CODE> if string1 contains string1 (ignore case)
     *          <CODE>false</CODE> otherwise.
     */
    public static boolean containsIgnoreCase (String string1, String string2)
    {
        // check if parameters set
        if (string1 == null || string2 == null)
        {
            return false;
        } // if

        // create upper case versions of both strings
        String s1 = string1.toUpperCase ();
        String s2 = string2.toUpperCase ();

        // check if s1 in s2; indexOf returns -1 if not substring
        return s1.indexOf (s2) != -1;
    } // containsIgnoreCase

} // class WorkflowHelpers

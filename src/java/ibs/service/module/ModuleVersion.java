/*
 * Class: ModuleVersion.java
 */

// package:
package ibs.service.module;

// imports:
import ibs.service.module.ModuleConstants;
import ibs.service.module.ModuleVersionException;

import java.util.StringTokenizer;


/******************************************************************************
 * The version of a module. <BR/>
 *
 * @version     $Id: ModuleVersion.java,v 1.4 2007/07/23 12:34:23 kreimueller Exp $
 *
 * @author      Klaus, 29.12.2003
 ******************************************************************************
 */
public class ModuleVersion extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ModuleVersion.java,v 1.4 2007/07/23 12:34:23 kreimueller Exp $";


    /**
     * The version number as String. <BR/>
     */
    public String p_versionStr = null;

    /**
     * The version number. <BR/>
     */
    public short p_version = 0;

    /**
     * The release number. <BR/>
     */
    public short p_release = 0;

    /**
     * The sub release number. <BR/>
     */
    public short p_subRelease = 0;


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a ModuleVersion object. <BR/>
     *
     * @param   versionStr  The version as string representation.
     *                      This value is parsed into its component.
     *
     * @throws  ModuleVersionException
     *          An exception occurred during parsing the version.
     */
    public ModuleVersion (String versionStr)
        throws ModuleVersionException
    {
        // initialize the other instance properties:
        this.init (versionStr);
    } // ModuleVersion


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Initialize the ModuleVersion. <BR/>
     *
     * @param   versionStr  The version as string representation.
     *                      This value is parsed into its component.
     *
     * @throws  ModuleVersionException
     *          An exception occurred during parsing the version.
     */
    public void init (String versionStr)
        throws ModuleVersionException
    {
        StringTokenizer tokenizer = null; // the tokenizer for the string

        if (versionStr != null && versionStr.length () > 0)
        {
            tokenizer = new StringTokenizer (versionStr, ".");

            try
            {
                // get the version number:
                if (tokenizer.hasMoreElements ())
                {
                    this.p_version = Short.parseShort (tokenizer.nextToken ());

                    // get the release number:
                    if (tokenizer.hasMoreElements ())
                    {
                        this.p_release =
                            Short.parseShort (tokenizer.nextToken ());

                        // get the sub release number:
                        if (tokenizer.hasMoreElements ())
                        {
                            this.p_subRelease =
                                Short.parseShort (tokenizer.nextToken ());

                            // check for correct syntax:
                            if (tokenizer.hasMoreElements ())
                            {
                                throw this.createException (versionStr, null);
                            } // else
                        } // if
                    } // if
                } // if
                else
                {
                    throw this.createException (versionStr, null);
                } // else
            } // try
            catch (NumberFormatException e)
            {
                throw this.createException (versionStr, e);
            } // catch
        } // if
        else
        {
            throw new ModuleVersionException (
                "The version string is empty or null");
        } // else
    } // init


    /**************************************************************************
     * Check if the actual version is matched by another version. <BR/>
     * The other version must be greater or equal than the actual version
     * according to the match type.
     *
     * @param   otherVersion    The other version to be checked.
     * @param   matchType       The match type.
     *                          This must be one of
     *                          {@link ModuleConstants#MATCH_PERFECT MATCH_PERFECT}
     *                          {@link ModuleConstants#MATCH_EQUIVALENT MATCH_EQUIVALENT}
     *                          {@link ModuleConstants#MATCH_COMPATIBLE MATCH_COMPATIBLE}
     *                          {@link ModuleConstants#MATCH_GREATEROREQUAL MATCH_GREATEROREQUAL}
     *
     * @return  The exception.
     */
    public boolean isMatch (ModuleVersion otherVersion, short matchType)
    {
        // check if the other version is a valid version object:
        if (otherVersion == null)
        {
            return false;
        } // if

        // check the match type:
        switch (matchType)
        {
            case ModuleConstants.MATCH_PERFECT:
                return this.p_version == otherVersion.p_version &&
                        this.p_release == otherVersion.p_release &&
                        this.p_subRelease == otherVersion.p_subRelease;

            case ModuleConstants.MATCH_EQUIVALENT:
                return this.p_version == otherVersion.p_version &&
                        this.p_release == otherVersion.p_release &&
                        this.p_subRelease <= otherVersion.p_subRelease;

            case ModuleConstants.MATCH_COMPATIBLE:
                return this.p_version == otherVersion.p_version &&
                        ((this.p_release == otherVersion.p_release &&
                          this.p_subRelease <= otherVersion.p_subRelease) ||
                          this.p_release < otherVersion.p_release);

            case ModuleConstants.MATCH_GREATEROREQUAL:
                return (this.p_version == otherVersion.p_version &&
                         ((this.p_release == otherVersion.p_release &&
                           this.p_subRelease <= otherVersion.p_subRelease) ||
                           this.p_release < otherVersion.p_release)) ||
                        this.p_version < otherVersion.p_version;

            default:
                return false;
        } // switch matchType
    } // isMatch


    /**************************************************************************
     * Create an exception. <BR/>
     *
     * @param   versionStr  The version string which contained the error.
     * @param   cause       The cause for this exception (if this was another
     *                      exception).
     *
     * @return  The exception.
     */
    public ModuleVersionException createException (String versionStr,
                                                   Throwable cause)
    {
        return new ModuleVersionException ("Wrong format for version: \"" +
            versionStr + "\"." +
            " Correct format: <version>[.<release>[.<subrelease>]]", cause);
    } // createException


    /**************************************************************************
     * Returns the string representation of this object. <BR/>
     * The id and the name are concatenated to create a string
     * representation according to "id, name".
     *
     * @return  String represention of the object.
     */
    public String toString ()
    {
        // compute the string and return it:
        return this.p_version + "." + this.p_release + "." + this.p_subRelease;
    } // toString

} // class ModuleVersion

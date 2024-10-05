/*
 * Interface: HttpLocalizedStrings.java
 */

// package:
package ibs.tech.http;

// imports:


/******************************************************************************
 * Localizable error message strings. <BR/>
 *
 * @version     $Id: HttpLocalizedStrings.java,v 1.5 2007/07/23 08:17:33 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 981004
 ******************************************************************************
 */
public interface HttpLocalizedStrings
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: HttpLocalizedStrings.java,v 1.5 2007/07/23 08:17:33 kreimueller Exp $";


    /**
     * Exception message thrown when getting or putting a value was tried with
     * a non-string key. <BR/>
     */
    public static final String ASP_E_NON_STRING_DICT_KEY =
        new String ("AspComponent: Get/Put attempted with a non-String key.");

    /**
     * Exception message thrown when tried to remove an asp dictionary. <BR/>
     */
    public static final String ASP_E_DICT_REMOVE =
        new String ("AspComponent: Remove attempted on an ASP dictionary.");

    /**
     * Exception message thrown when retreiving of MTx object context failed.
     * <BR/>
     */
    public static final String ASP_E_FAIL_GET_CONTEXT =
        new String ("AspComponent: Retreiving MTx object context failed.");

    /**
     * Exception message thrown when failed to get an asp intrinsic object.
     * <BR/>
     */
    public static final String ASP_E_FAIL_GET_INTRINSIC =
        new String ("AspComponent: Failure in getting ASP intrinsic object.");

    /**
     * Exception message thrown when an internal error occurred. <BR/>
     */
    public static final String ASP_E_INTERNAL_FAILURE =
        new String ("AspComponent: Internal error detected.");

    /**
     * Exception message thrown when tried to write a value to a read-only
     * asp dictionary. <BR/>
     */
    public static final String ASP_E_READ_ONLY_DICT =
        new String ("AspComponent: Put attempted on read-only ASP dictionary.");

    /**
     * Exception message thrown when tried to reset an unmarked input stream.
     * <BR/>
     */
    public static final String ASP_E_UNMARKED_STREAM =
        new String ("AspComponent: Reset attempted on unmarked InputStream.");

    /**
     * Exception message thrown when tried to write to a cookie dictionary.
     * <BR/>
     */
    public static final String ASP_E_COOKIE_PUT =
        new String ("AspComponent: Put attempted on Cookie dictionary.");

} // interface HttpLocalizedStrings

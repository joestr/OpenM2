/*
 * Class: EDITokens.java
 */
/*
 * Created by IntelliJ IDEA.
 * User: kreimueller
 * Date: Feb 27, 2002
 * Time: 5:23:29 PM
 */

// package:
package ibs.di.edi;

//imports:
import ibs.bo.BOTokens;

/******************************************************************************
 * Tokens for ibs.di.edi. <BR/>
 *
 * @version     $Id: EDITokens.java,v 1.5 2010/04/07 13:37:15 rburgermann Exp $
 *
 * @author      kreimueller, 020227
 ******************************************************************************
 */
public abstract class EDITokens extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: EDITokens.java,v 1.5 2010/04/07 13:37:15 rburgermann Exp $";

    /**
     * Name of bundle where the tokens included. <BR/>
     */
    public static String TOK_BUNDLE = BOTokens.TOK_BUNDLE;

    /**
     * Token for the filter file. <BR/>
     * The filter file contains filter directives which are executed before the
     * edi to xml conversion starts.
     */
    public static String ML_FILTERFILE = "ML_FILTERFILE";

    /**
     * Token for the format file. <BR/>
     * The format file contains the format description for the edi data format..
     */
    public static String ML_FORMATFILE = "ML_FORMATFILE";

} // class EDITokens

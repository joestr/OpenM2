/*
 * Class: EDIArguments.java
 */
/*
 * Created by IntelliJ IDEA.
 * User: kreimueller
 * Date: Feb 27, 2002
 * Time: 5:07:43 PM
 */

// package:
package ibs.di.edi;

// imports:


/******************************************************************************
 * Arguments for ibs.di.edi. <BR/>
 *
 * @version     $Id: EDIArguments.java,v 1.4 2007/07/31 19:13:54 kreimueller Exp $
 *
 * @author      kreimueller, 020227
 ******************************************************************************
 */
public abstract class EDIArguments
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: EDIArguments.java,v 1.4 2007/07/31 19:13:54 kreimueller Exp $";


    /**
     * Argument for the filter file. <BR/>
     * The filter file contains filter directives which are executed before the
     * edi to xml conversion starts.
     */
    public static final String ARG_FILTERFILE = "edifilter";

    /**
     * Argument for the format file. <BR/>
     * The format file contains the format description for the edi data format..
     */
    public static final String ARG_FORMATFILE = "ediformat";

} // class EDIArguments

/*
 * Class: Buttons.java
 */

// package:
package ibs.service.conf;

// imports:


/******************************************************************************
 * Constants used for configuration purposes. <BR/>
 *
 * @version     $Id: ConfConstants.java,v 1.2 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 20060622
 ******************************************************************************
 */
public abstract class ConfConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ConfConstants.java,v 1.2 2007/07/24 21:27:33 kreimueller Exp $";

    /**
     * Extension of configuration file. <BR/>
     */
    public static final String CONFIGFILE_EXTENSION = ".cfg";

    /**
     * Start of comment line in configuration file. <BR/>
     */
    public static final String CONFIGFILE_COMMENT = "#";

    /**
     * Separator between values in configuration value list. <BR/>
     */
    public static final String CONF_VALUESEP = ",";

    /**
     * Delimiter of variables within configuration value. <BR/>
     * This delimiter has to be put in front and afterwards the variable name.
     */
    public static final String CONF_VARIABLEDELIM = "#";

} // class ConfConstants

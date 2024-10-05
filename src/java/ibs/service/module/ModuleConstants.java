/*
 * Class: ModuleConstants.java
 */

// package:
package ibs.service.module;

// imports:
import java.io.File;


/******************************************************************************
 * This class contains constants used for modules. <BR/>
 *
 * @version     $Id: ModuleConstants.java,v 1.10 2010/03/16 13:32:00 btatzmann Exp $
 *
 * @author      Klaus, 28.12.2003
 ******************************************************************************
 */
public abstract class ModuleConstants
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ModuleConstants.java,v 1.10 2010/03/16 13:32:00 btatzmann Exp $";


    /**
     * Name of module configuration directory. <BR/>
     */
    public static final String DIR_MODULES = "modules" + File.separator;

    /**
     * Name of app directory. <BR/>
     */
    public static final String DIR_APP = "app" + File.separator;

    /**
     * Name of directory with common images. <BR/>
     */
    public static final String DIR_IMAGES =
        ModuleConstants.DIR_APP + "images" + File.separator;

    /**
     * Name of directory with include files. <BR/>
     */
    public static final String DIR_INCLUDE =
        ModuleConstants.DIR_APP + "include" + File.separator;

    /**
     * Name of directory with layout files. <BR/>
     */
    public static final String DIR_LAYOUTS =
        ModuleConstants.DIR_APP + "layouts" + File.separator;

    /**
     * Name of directory with form configuration files. <BR/>
     */
    public static final String DIR_FORMCFG =
        ModuleConstants.DIR_APP + "formcfg" + File.separator;

    /**
     * Name of directory with reporting files. <BR/>
     */
    public static final String DIR_REPORTS =
        ModuleConstants.DIR_APP + "reports" + File.separator;

    /**
     * Name of directory with javascript files. <BR/>
     */
    public static final String DIR_SCRIPTFILES =
        ModuleConstants.DIR_APP + "scripts" + File.separator;

    /**
     * Name of xslt stylesheet directory. <BR/>
     */
    public static final String DIR_XSLTSTYLESHEETS =
        ModuleConstants.DIR_APP + "stylesheets" + File.separator;

    /**
     * Name of translators directory. <BR/>
     */
    public static final String DIR_TRANSLATORS =
        ModuleConstants.DIR_APP + "trans" + File.separator;
    
    /**
     * Name of resource bundle directory. <BR/>
     */
    public static final String DIR_RESOURCE_BUNDLES =
        ModuleConstants.DIR_APP + "lang" + File.separator + "rb" + File.separator;

    /**
     * Directory for installation files. <BR/>
     */
    public static final String DIR_INSTALL = "install" + File.separator;
    /**
     * Directory for xml installation files. <BR/>
     */
    public static final String DIR_INSTALLXML =
        ModuleConstants.DIR_INSTALL + "xml" + File.separator;
    /**
     * Directory for sql installation files. <BR/>
     */
    public static final String DIR_INSTALLSQL =
        ModuleConstants.DIR_INSTALL + "sql" + File.separator;
    /**
     * Directory for language-dependent installation files. <BR/>
     */
    public static final String DIR_INSTALLLANG = "lang" + File.separator;

    /**
     * Name of directory with library files. <BR/>
     */
    public static final String DIR_LIB = "lib" + File.separator;


    /**
     * Name of module configuration file. <BR/>
     */
    public static final String FILE_MODULE = "module.xml";

    /**
     * Name of configuration file with configuration variables. <BR/>
     */
    public static final String FILE_CONFVARS = "confvars.xml";

    /**
     * Name of configuration file with configuration variable values. <BR/>
     */
    public static final String FILE_CONFVALUES = "*.xml";


    // match types for module references:
    /**
     * Perfect match. <BR/>
     * x ... identical, y ... greater, z ... any value
     * perfect:        x.x.x
     */
    public static final String MATCHSTR_PERFECT = "perfect";

    /**
     * Equivalent match. <BR/>
     * x ... identical, y ... greater, z ... any value
     * equivalent:     x.x.x or x.x.y
     */
    public static final String MATCHSTR_EQUIVALENT = "equivalent";

    /**
     * Compatible match. <BR/>
     * x ... identical, y ... greater, z ... any value
     * compatible:     x.x.x or x.x.y or x.y.z
     */
    public static final String MATCHSTR_COMPATIBLE = "compatible";

    /**
     * Greater or equal match. <BR/>
     * x ... identical, y ... greater, z ... any value
     * greaterOrEqual: x.x.x or x.x.y or x.y.z or y.z.z
     */
    public static final String MATCHSTR_GREATEROREQUAL = "greaterOrEqual";

    /**
     * Perfect match. <BR/>
     * x ... identical, y ... greater, z ... any value
     * perfect:        x.x.x
     */
    public static final short MATCH_PERFECT = 1;

    /**
     * Equivalent match. <BR/>
     * x ... identical, y ... greater, z ... any value
     * equivalent:     x.x.x or x.x.y
     */
    public static final short MATCH_EQUIVALENT = 2;

    /**
     * Compatible match. <BR/>
     * x ... identical, y ... greater, z ... any value
     * compatible:     x.x.x or x.x.y or x.y.z
     */
    public static final short MATCH_COMPATIBLE = 3;

    /**
     * Greater or equal match. <BR/>
     * x ... identical, y ... greater, z ... any value
     * greaterOrEqual: x.x.x or x.x.y or x.y.z or y.z.z
     */
    public static final short MATCH_GREATEROREQUAL = 4;


    /**
     * Tag name for module. <BR/>
     */
    public static final String TAG_MODULE = "module";

    /**
     * Tag name for required module. <BR/>
     */
    public static final String TAG_REQMODULE = "reqmodule";

    /**
     * Tag name for function handler definition. <BR/>
     */
    public static final String TAG_FUNCHANDLER = "functionhandler";

    /**
     * Tag name for configuration variable definition. <BR/>
     */
    public static final String TAG_CONFVARDEF = "confvardef";

    /**
     * Tag name for configuration. <BR/>
     */
    public static final String TAG_CONFIGURATION = "configuration";

    /**
     * Tag name for configuration variable value. <BR/>
     */
    public static final String TAG_CONFVALUE = "confvalue";


    /**
     * Begin and end of a configuration variable reference. <BR/>
     */
    public static final char CONFVAR_SEPARATOR = '#';

    /**
     * Prefix for a configuration variable reference (after the separator). <BR/>
     */
    public static final String CONFVAR_PREFIX = "CONFVAR.";

} // class ModuleConstants

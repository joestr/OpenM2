/*
 * Class: MultilangConstants.java
 */

// package:
package ibs.ml;

// imports:


/******************************************************************************
 * This class contains constants used for multilang. <BR/>
 *
 * @version     $Id: MultilangConstants.java,v 1.13 2012/01/10 12:01:31 rburgermann Exp $
 *
 * @author      Bernhard Tatzmann
 ******************************************************************************
 */
public abstract class MultilangConstants
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MultilangConstants.java,v 1.13 2012/01/10 12:01:31 rburgermann Exp $";
   
    /**
     * Name of resource bundle with multilang values for form templates without extension. <BR/>
     */
    public static final String RESOURCE_BUNDLE_FORMTEMPLATES_NAME = "formtemplates";

    /**
     * Name of resource bundle with multilang values for tabs without extension. <BR/>
     */
    public static final String RESOURCE_BUNDLE_TABS_NAME = "tabs";
    
    /**
     * Name of resource bundle with multilang values for types without extension. <BR/>
     */
    public static final String RESOURCE_BUNDLE_TYPES_NAME = "types";

    /**
     * Name of resource bundle with multilang values for objects without extension. <BR/>
     */
    public static final String RESOURCE_BUNDLE_OBJECTS_NAME = "objects";

    /**
     * Postfix for temporary resource bundles. <BR/>
     */
    public static final String RESOURCE_BUNDLE_TEMP_POSTFIX = "temp_";
    
    /**
     * Name of template resource bundle. <BR/>
     */
    public static final String RESOURCE_BUNDLE_TEMPLATE = "resource_bundle_template.properties";
    
    /**
     * Name prefix for client side multilang info files. <BR/>
     */
    public static final String CLIENT_ML_INFO_FILE_PREFIX = "mlitexts_";

    /**
     * Name postfix for client side multilang info files. <BR/>
     */
    public static final String CLIENT_ML_INFO_FILE_POSTFIX = ".html";

    /**
     * Name of resource bundle with multilang values for queries without extension. <BR/>
     */
    public static final String RESOURCE_BUNDLE_QUERIES_NAME = "queries";

    /**
     * Name of resource bundle with multilang values for reports without extension. <BR/>
     */
    public static final String RESOURCE_BUNDLE_REPORTS_NAME = "reports";

    /**
     * File extension: resource bundle. <BR/>
     */
    public static final String FILEEXT_RESOURCE_BUNDLE = ".properties";
    
    /**
     * File pattern: form templates resource bundle. <BR/>
     */
    public static final String FILEPATTERN_FORMTEMPLATE_RESOURCE_BUNDLE =
        "*" + RESOURCE_BUNDLE_FORMTEMPLATES_NAME + "*" + FILEEXT_RESOURCE_BUNDLE;
    
    /**
     * Name of form templates resource bundle. <BR/>
     */
    public static final String RESOURCE_BUNDLE_FORMTEMPLATES = RESOURCE_BUNDLE_FORMTEMPLATES_NAME +
        FILEEXT_RESOURCE_BUNDLE;

    /**
     * Name of queries resource bundle. <BR/>
     */
    public static final String RESOURCE_BUNDLE_QUERIES = RESOURCE_BUNDLE_QUERIES_NAME +
        FILEEXT_RESOURCE_BUNDLE;
    
    /**
     * Value data element lookup key separator. <BR/>
     */
    public static final String LOOKUP_KEY_SEPARATOR = ".";
    
    /**
     * Client ml key (=java script constant) separator. <BR/>
     */
    public static final String CLIENT_KEY_SEPARATOR = "_";
    
    /**
     * Lookup key prefix for value data elements. <BR/>
     */
    public static final String LOOKUP_KEY_PREFIX_VDE = "VF_";

    /**
     * Lookup key prefix for tabs. <BR/>
     */
    public static final String LOOKUP_KEY_PREFIX_TAB = "TA_";
    
    /**
     * Lookup key prefix for columns. <BR/>
     */
    public static final String LOOKUP_KEY_PREFIX_COLUMN = "CO_";

    /**
     * Lookup key prefix for query fields. <BR/>
     */
    public static final String LOOKUP_KEY_PREFIX_QUERY = "QY_";
    
    /**
     * Lookup key SYS field prefix. <BR/>
     */
    public static final String LOOKUP_KEY_SYSFIELD_PREFIX = "SYS_";
    
    /**
     * Lookup key Fieldref prefix. <BR/>
     */
    public static final String LOOKUP_KEY_FIELDREF_PREFIX = "FR_";

    /**
     * Lookup key Valuedomain prefix. <BR/>
     */
    public static final String LOOKUP_KEY_VALUEDOMAIN_PREFIX = "VD_";
    
    /**
     * Lookup key postfix for name. <BR/>
     */
    public static final String LOOKUP_KEY_POSTFIX_NAME = "NAME";

    /**
     * Lookup key postfix for description. <BR/>
     */
    public static final String LOOKUP_KEY_POSTFIX_DESCRIPTION = "DESCRIPTION";

    /**
     * Lookup key postfix for unit. <BR/>
     */
    public static final String LOOKUP_KEY_POSTFIX_UNIT = "UNIT";

    /**
     * Lookup key postfix for category. <BR/>
     */
    public static final String LOOKUP_KEY_POSTFIX_CATEGORY = "CATEGORY";

    /**
     * Defines all resource bundles to preload. <BR/>
     * All resource bundles where a fallback to another resource bundle is not
     * possible during runtime have to be preloaded and added here.
     */
    public static final String[] PRELOAD_RESOURCE_BUNDLES = new String[] {
        MultilangConstants.RESOURCE_BUNDLE_OBJECTS_NAME
            };
    
    /**
     * Resource bundles needed on the client. <BR/>
     */
    public static final String[] CLIENT_RESOURCE_BUNDLES = new String[] {
        "ibs_ibsbase_formvalidation",
        "ibs_ibsbase_buttons",
        "ibs_ibsbase_scripts"
            };
    
} // class MultilangConstants
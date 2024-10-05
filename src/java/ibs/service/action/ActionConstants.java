/*
 * Class: ActionConstants.java
 */

// package:
package ibs.service.action;

// imports:


/******************************************************************************
 * Constants for ibs.util.action objects. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the objects delivered within this package.
 *
 * @version     $Id: ActionConstants.java,v 1.11 2008/10/01 14:51:46 btatzmann Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public abstract class ActionConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ActionConstants.java,v 1.11 2008/10/01 14:51:46 btatzmann Exp $";


    /**
     * common text for undefined entries
     */
    public static final String UNDEFINED = "UNDEFINED";


    // Text for action-type-entries:
    /**
     * Action type entry: XPATH. <BR/>
     */
    public static final String ACTIONTYPE_XPATH        = "XPATH";
    /**
     * Action type entry: QUERY. <BR/>
     */
    public static final String ACTIONTYPE_QUERY        = "QUERY";
    /**
     * Action type entry: INTERNALCALL. <BR/>
     */
    public static final String ACTIONTYPE_INTERNALCALL = "INTERNALCALL";
    /**
     * Action type entry: EXTERNALCALL. <BR/>
     */
    public static final String ACTIONTYPE_EXTERNALCALL = "EXTERNALCALL";
    /**
     * Action type entry: EXPORT. <BR/>
     */
    public static final String ACTIONTYPE_EXPORT       = "EXPORT";


    // Action variable types:
    /**
     * Action variable type: TEXT. <BR/>
     */
    public static final String VARIABLETYPE_TEXT       = "TEXT";
    /**
     * Action variable type: NUMBER. <BR/>
     */
    public static final String VARIABLETYPE_NUMBER     = "NUMBER";

    // Action variable types-length:
    /**
     * Length of action variable for type TEXT. <BR/>
     */
    public static final String VARIABLETYPELENGTH_TEXT     = "255";
    /**
     * Length of action variable for type NUMBER. <BR/>
     */
    public static final String VARIABLETYPELENGTH_NUMBER   = "16";

    /**
     * Prefix for system variables
     */
    public static final String SYSVAR_PREFIX    = "#SYSVAR.";

    /**
     * Prefix for runtime variables
     */
    public static final String VARIABLE_PREFIX  = "#VARIABLE.";

    /**
     * Postfix for system/runtime variables
     */
    public static final String VARIABLE_POSTFIX = "#";

    // System variables:
    /**
     * System variable: USERFULLNAME. <BR/>
     */
    public static final String SYSVAR_USERFULLNAME = "USERFULLNAME";
    /**
     * System variable: USERNAME. <BR/>
     */
    public static final String SYSVAR_USERNAME     = "USERNAME";
    /**
     * System variable: USERID. <BR/>
     */
    public static final String SYSVAR_USERID       = "USERID";
    /**
     * System variable: USEROID. <BR/>
     */
    public static final String SYSVAR_USEROID      = "USEROID";
    /**
     * System variable: EXTENDEDUSERDATAOID. <BR/>
     */
    public static final String SYSVAR_EXTENDEDUSERDATAOID      = "EXTENDEDUSERDATAOID";
    /**
     * System variable: DATE. <BR/>
     */
    public static final String SYSVAR_DATE         = "DATE";
    /**
     * System variable: TIME. <BR/>
     */
    public static final String SYSVAR_TIME         = "TIME";
    /**
     * System variable: OID. <BR/>
     */
    public static final String SYSVAR_OID          = "OID";
    /**
     * System variable: CONTAINERID. <BR/>
     */
    public static final String SYSVAR_CONTAINERID  = "CONTAINERID";
    /**
     * System variable: CONTAINEROID2. <BR/>
     */
    public static final String SYSVAR_CONTAINEROID2 = "CONTAINEROID2";
    /**
     * System variable: TYPENAME. <BR/>
     */
    public static final String SYSVAR_TYPENAME     = "TYPENAME";
    /**
     * System variable: TYPECODE. <BR/>
     */
    public static final String SYSVAR_TYPECODE     = "TYPECODE";
    /**
     * System variable: QUERY. <BR/>
     */
    public static final String SYSVAR_QUERYPROPERTY = "QUERY_";

    // Error variables:
    /**
     * Error variable for error code. <BR/>
     */
    public static final String VARIABLE_ERRORCODE      = "ERRORCODE";
    /**
     * Error variable for error message. <BR/>
     */
    public static final String VARIABLE_ERRORMESSAGE   = "ERRORMESSAGE";


    //
    // TAG: <VARIABLES ... >
    //
    /**
     * name of variables-element. <BR/>
     */
    public static final String ELEM_VARIABLES = "VARIABLES";
    /**
     * name of variable-element. <BR/>
     */
    public static final String ELEM_VARIABLE = "VARIABLE";
    /**
     * name of variable-name element. <BR/>
     */
    public static final String ELEM_VARIABLENAME = "NAME";
    /**
     * name of variable-type element. <BR/>
     */
    public static final String ELEM_VARIABLETYPE = "TYPE";
    /**
     * name of variable-length element. <BR/>
     */
    public static final String ELEM_VARIABLELENGTH = "LENGTH";
    /**
     * name of variable-description element. <BR/>
     */
    public static final String ELEM_VARIABLEDESCRIPTION = "DESCRIPTION";
    /**
     * name of variable-description element. <BR/>
     */
    public static final String ELEM_VARIABLEVALUE = "VALUE";

    //
    // TAG: <ACTION ... >
    //
    /**
     * name of action element. <BR/>
     */
    public static final String ELEM_ACTION = "ACTION";
    /**
     * name of state's type attribute. <BR/>
     */
    public static final String ATTR_ACTIONTYPE = ActionConstants.ELEM_VARIABLETYPE;
    /**
     * name of state's name attribute for parameters. <BR/>
     */
    public static final String ATTR_FIELDNAME = ActionConstants.ELEM_VARIABLENAME;
    /**
     * name of call element. <BR/>
     */
    public static final String ELEM_CALL = "CALL";

    /**
     * name of inparams element. <BR/>
     */
    public static final String ELEM_INPARAMS = "INPARAMS";
    /**
     * name of inparams element. <BR/>
     */
    public static final String ELEM_OUTPARAMS = "OUTPARAMS";
    /**
     * name of parameter element. <BR/>
     */
    public static final String ELEM_PARAMETER = "PARAMETER";

} // class ActionConstants

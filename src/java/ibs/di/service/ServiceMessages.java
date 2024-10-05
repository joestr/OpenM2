/*
 * Class: DIMessages.java
 */

// package:
package ibs.di.service;

import ibs.bo.BOMessages;

// imports:


/******************************************************************************
 * Messages for ibs.di.services business objects. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the business objects delivered within this package.
 *
 * @version     $Id: ServiceMessages.java,v 1.8 2010/04/07 13:37:10 rburgermann Exp $
 *
 * @author      Michael Steiner (MS)
 ******************************************************************************
 */
public abstract class ServiceMessages extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ServiceMessages.java,v 1.8 2010/04/07 13:37:10 rburgermann Exp $";

    /**
     * Name of bundle where the messages are included. <BR/>
     */
    public static String MSG_BUNDLE = BOMessages.MSG_BUNDLE;

    /**
     * Message when the name of the mapping table in not valid. <BR/>
     */
    public static String ML_MSG_INVALID_TABLENAME = "ML_MSG_INVALID_TABLENAME";
    /**
     * Message when the name of the mapping table in not valid. <BR/>
     */
    public static String ML_MSG_INVALID_TABLEPREFIX = "ML_MSG_INVALID_TABLEPREFIX";
    /**
     * Message when the name of the mapping field in not valid. <BR/>
     */
    public static String ML_MSG_INVALID_FIELDNAME = "ML_MSG_INVALID_FIELDNAME";
    /**
     * Message when the name of the mapping field in not valid. <BR/>
     */
    public static String ML_MSG_INVALID_FIELDPREFIX = "ML_MSG_INVALID_FIELDPREFIX";
    /**
     * Message when a value in a data element is not mappable. <BR/>
     */
    public static String MSG_MAPPING_NOTPOSSIBLE = "MSG_MAPPING_NOTPOSSIBLE";
    /**
     * Message when the no values found to map. <BR/>
     */
    public static String ML_MSG_NO_FIELDS_FOUND = "ML_MSG_NO_FIELDS_FOUND";
    /**
     * Message when the no values found to map. <BR/>
     */
    public static String ML_MSG_DUPLICATE_FIELD = "ML_MSG_DUPLICATE_FIELD";
    /**
     * Message when the mapping for a object is done. <BR/>
     */
    public static String ML_MSG_OBJECTMAPPING_DONE = "ML_MSG_OBJECTMAPPING_DONE";
    /**
     * Message when a error occurs during the mapping of an object. <BR/>
     */
    public static String ML_MSG_OBJECTMAPPING_ERROR = "ML_MSG_OBJECTMAPPING_ERROR";

    /**
     * Message when a error occurs during the transformation of an object. <BR/>
     */
    public static String ML_E_TRANSFORMATION_FAILED = "ML_E_TRANSFORMATION_FAILED";

    /**
     * Message when a error occurs when creating the xml structure for an
     * object transformation. <BR/>
     */
    public static String ML_MSG_COULD_NOT_CREATE_XML_STRUCTURE = "ML_MSG_COULD_NOT_CREATE_XML_STRUCTURE";

    /**
     * Message when a error occurs when creating the new object in the object
     * transformation. <BR/>
     */
    public static String ML_MSG_COULD_NOT_CREATE_OBJECT = "ML_MSG_COULD_NOT_CREATE_OBJECT";

} // ServiceMessages

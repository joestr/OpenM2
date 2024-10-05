/*
 * Class: ActionMessages.java
 */

// package:
package ibs.service.action;

import ibs.bo.BOMessages;

// imports:


/******************************************************************************
 * Messages for ibs.util.action objects. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the objects delivered within this package.
 *
 * @version     $Id: ActionMessages.java,v 1.9 2010/04/07 13:37:12 rburgermann Exp $
 *
 * @author      Michael Steiner (MS)
 ******************************************************************************
 */
public abstract class ActionMessages extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ActionMessages.java,v 1.9 2010/04/07 13:37:12 rburgermann Exp $";

    /**
     * Name of bundle where the messages are included. <BR/>
     */
    public static String MSG_BUNDLE = BOMessages.MSG_BUNDLE;

    /**
     * Message when the ACTIONS element contains a invalid node. <BR/>
     */
    public static String ML_MSG_INVALID_NODE = "ML_MSG_INVALID_NODE";

    /**
     * Message when the ACTIONS element contains more than one VARIABLES tags. <BR/>
     */
    public static String ML_MSG_DUPLICATE_VARIABLES = "ML_MSG_DUPLICATE_VARIABLES";

    /**
     * Message when a undefined variables is referenced. <BR/>
     */
    public static String ML_MSG_UNDEFINED_VARIABLE = "ML_MSG_UNDEFINED_VARIABLE";

    /**
     * Message when a variables is defined twice. <BR/>
     */
    public static String ML_MSG_VARIABLE_ALREADY_DEFINED = "ML_MSG_VARIABLE_ALREADY_DEFINED";

    /**
     * Message when a incorrect variable reference is found. <BR/>
     */
    public static String ML_MSG_INCORRECT_VARIABLE = "ML_MSG_INCORRECT_VARIABLE";

} // class ActionMessages

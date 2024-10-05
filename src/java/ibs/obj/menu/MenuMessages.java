/*
 * Class: MenuMessages.java
 */

// package:
package ibs.obj.menu;

import ibs.bo.BOMessages;

// imports:


/******************************************************************************
 * Messages for menu handling. <BR/>
 *
 * @version     $$
 *
 * @author      Klaus Reimüller (KR), 20060330
 ******************************************************************************
 */
public abstract class MenuMessages extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MenuMessages.java,v 1.3 2010/04/07 13:37:12 rburgermann Exp $";

    /**
     * Name of bundle where the messages are included. <BR/>
     */
    public static String MSG_BUNDLE = BOMessages.MSG_BUNDLE;

    /**
     * Message for: Description of levelStep. <BR/>
     */
    public static String ML_MSG_LEVELSTEP_DESCRIPTION = "ML_MSG_LEVELSTEP_DESCRIPTION";

    /**
     * Message for: Description of levelStepMax. <BR/>
     */
    public static String ML_MSG_LEVELSTEPMAX_DESCRIPTION = "ML_MSG_LEVELSTEPMAX_DESCRIPTION";

} // class MenuMessages

/*
 * Class: WorkflowArguments.java
 */

// package:
package ibs.service.workflow;

// imports:


/******************************************************************************
 * Arguments for ibs.workflow. <BR/>
 *
 * @version     $Id: WorkflowArguments.java,v 1.7 2007/07/31 19:13:59 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 1.1.2000
 ******************************************************************************
 */
public abstract class WorkflowArguments extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WorkflowArguments.java,v 1.7 2007/07/31 19:13:59 kreimueller Exp $";


    /**
     * Argument which holds selectable users for ADHOC dialog. <BR/>
     */
    public static final String ARG_PREDEFUSERS = "pdu";

    /**
     * Argument which holds selectable groups for ADHOC dialog. <BR/>
     */
    public static final String ARG_PREDEFGROUPS = "pdg";

    /**
     * Argument that indicates if multiple receivers are allowed in ADHOC dialog. <BR/>
     */
    public static final String ARG_ALLOWMULTIPLE = "alm";

    /**
     * Argument which holds name of next state of ALTERNATIVE selection dialog. <BR/>
     */
    public static final String ARG_ALTERNATIVESTATE = "ast";

} // WorkflowArguments

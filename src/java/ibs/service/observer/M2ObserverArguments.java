/*
 * Created by IntelliJ IDEA.
 * User: hpichler
 * Date: 19.08.2002
 * Time: 18:22:11
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */

// package:
package ibs.service.observer;

// imports:


/******************************************************************************
 * Arguments for m2.observer. <BR/>
 * This abstract class contains all arguments which are necessary to deal with
 * the classes delivered within this package. <P>
 *
 * @version     $Id: M2ObserverArguments.java,v 1.2 2007/07/31 19:13:58 kreimueller Exp $
 *
 * @author      hpichler, 19.08.2002
 ******************************************************************************
 */
public abstract class M2ObserverArguments extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: M2ObserverArguments.java,v 1.2 2007/07/31 19:13:58 kreimueller Exp $";


    /**
     * Argument which holds the name of the observer which executes the job. <BR/>
     */
    public static final String ARG_OBS = "obs";
    /**
     * Argument which holds the id of the observerjob which shall be executed. <BR/>
     */
    public static final String ARG_JOBID = "jid";
    /**
     * Argument which holds the class of a job. <BR/>
     */
    public static final String ARG_JOBCLASS = "jclass";

} // M2ObserverArguments

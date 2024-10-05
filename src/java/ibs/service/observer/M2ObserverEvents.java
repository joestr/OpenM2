/*
 * Created by IntelliJ IDEA.
 * User: hpichler
 * Date: 19.08.2002
 * Time: 18:32:14
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */

// package:
package ibs.service.observer;

// imports:


/******************************************************************************
 * Events for m2.observer events, that are called via an m2-URL. These events
 * can come from the observer-gui-component or from an observer-thread. <BR/>
 *
 * @version     $Id: M2ObserverEvents.java,v 1.2 2007/07/31 19:13:58 kreimueller Exp $
 *
 * @author      hpichler, 19.08.2002
 ******************************************************************************
 */
public class M2ObserverEvents
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: M2ObserverEvents.java,v 1.2 2007/07/31 19:13:58 kreimueller Exp $";


    /**
     * Execute an observer job. Requested from an observer-thread. <BR/>
     */
    public static final int EVT_EXECOBSERVERJOB = 1;

    /**
     * Register or change an observer job. Requested from GUI. <BR/>
     */
    public static final int EVT_GUIREGISTERPARAMETERJOB = 10;

    /**
     * Unregister an observer job. Requested from GUI. <BR/>
     */
    public static final int EVT_GUIUNREGISTERPARAMETERJOB = 11;

    /**
     * Show the change/register form for a parameter job. Requested from GUI. <BR/>
     */
    public static final int EVT_GUISHOWPARAMETERJOBFORM = 12;

    /**
     * Show the change/register form for a parameter job. Requested from GUI. <BR/>
     */
    public static final int EVT_GUISHOWPARAMETERJOB = 13;

} // M2ObserverEvents

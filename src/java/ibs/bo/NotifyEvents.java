/*
 * Class: NotifyEvents.java
 */

// package:
package ibs.bo;

// imports:


/******************************************************************************
 * Events which can be thrown in NotifyFunction. <BR/>
 * This abstract class contains all events which are necessary to deal with
 * the classes delivered within the notify interface. <P>
 *
 * @version     $Id: NotifyEvents.java,v 1.6 2007/07/10 22:30:12 kreimueller Exp $
 *
 * @author      Monika Eisenkolb (ME), 001219
 ******************************************************************************
 */
public abstract class NotifyEvents extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: NotifyEvents.java,v 1.6 2007/07/10 22:30:12 kreimueller Exp $";


    /**
     * system button for notify functionality was clicked
     */
    public static final int EVT_SHOWFORM         = 81;

    /**
     * ok button of notify selection form was clicked. <BR/>
     */
    public static final int EVT_DONOTIFICATION      = 82;

} // NotifyEvents

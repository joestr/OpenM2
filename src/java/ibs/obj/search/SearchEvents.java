/*
 * Class: SearchEvents.java
 */

// package:
package ibs.obj.search;

// imports:


/******************************************************************************
 * Events which can be thrown in Searchfunktion. <BR/>
 * This abstract class contains all Events which are necessary to deal with
 * the classes delivered within the searchinterface. <P>
 *
 * @version     $Id: SearchEvents.java,v 1.6 2009/07/24 08:26:44 kreimueller Exp $
 *
 * @author      Andreas Jansa (AJ), 001017
 ******************************************************************************
 */
public abstract class SearchEvents extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SearchEvents.java,v 1.6 2009/07/24 08:26:44 kreimueller Exp $";


    /**
     * first call from framset.html which creates the searchForm.
     * This searchForm contains the input field for simple search, the selection
     * list for advanced search and the buttons. <BR/>
     */
    public static final int EVT_BUILDCALLINGSEARCHFORM          = 99;


    /**
     * systembutton for searchfunctionality was clicked. <BR/>
     */
    public static final int EVT_CLICKSYSTEMSEARCHBUTTON         = 100;

    /**
     * ok button of searchselectionfrom was clicked. <BR/>
     */
    public static final int EVT_CLICKOKSEARCHSELECTIONFORM      = 101;

    /**
     * event to show the form with the specific searchfields for
     * current searchtype. <BR/>
     */
    public static final int EVT_SHOWSEARCHFORM                  = 102;


    /**
     * ok button of search from was clicked. <BR/>
     */
    public static final int EVT_CLICKOKSEARCHFORM               = 103;

} // SearchEvents

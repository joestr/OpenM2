/*
 * Class: Calendar.java
 */

// package:
package ibs.tech.html;

// imports:
import ibs.tech.html.Element;

import java.util.Date;


/******************************************************************************
 * This is the Element Calendar, which represents displays a Calendar
 *
 * @version     $Id: Calendar.java,v 1.7 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980315
 ******************************************************************************
 */
public abstract class Calendar extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Calendar.java,v 1.7 2007/07/23 08:17:32 kreimueller Exp $";


    /**
     * Along with <A HREF="#year">year</A>, specifies the eyact month
     * to be displayed.
     */
    public String month;

    /**
     * Along with <A HREF="#month">month</A>, specifies the eyact month
     * to be displayed.
     */
    public String year;

    /**
     * Specifies textcolor;
     */
    public String textcolor;

    /**
     * Specifies backgroundcolor;
     */
    public String bgcolor;

    /**
     * Specifies color to use on free days;
     */
    public String freecolor;


    /**************************************************************************
     * Create a new instance of a calendar, which shows the infos of a given month
     * in a given year. <BR/>
     *
     * @param   year    The year of the calendar to be displayed.
     * @param   month   The month to be displayed.
     */
    public Calendar (int year, int month)
    {
        // nothing to do
    } // Calendar


    /**************************************************************************
     * Creates a default calendar with actuals month initialized. <BR/>
     */
    public Calendar ()
    {
        // nothing to do
    } // Calendar


    /**************************************************************************
     * Adds a date to a given day.
     *
     * @param date     Specifies the exact day
     * @param what     Specifies what is to do then.
     */
    public abstract void addDate (Date date, Element what);


    /**************************************************************************
     * Clears the element. <BR/>
     * Sets the calendar to the actual month.
     *
     * @return  ???
     */
    public abstract String clear ();


    /**************************************************************************
     * Builds the element. <BR/>
     * Returns the String that is needed to make the Calendar visible on the Browser
     *
     * @param   browser     Browser for which to build the output.
     *
     * @return  The builded string.
     *
     * @deprecated  This method shall never be used. Use
     *              {@link ibs.tech.html.Element#build (Environment, StringBuffer)}
     *              instead
     */
    public abstract String build (String browser);

} // class Calendar

/*
 * Class: Terminplan_01.java
 */

// package:
package m2.diary;

// imports:
import ibs.app.CssConstants;
import ibs.bo.BOArguments;
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.bo.tab.Tab;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.ml.Locale_01;
import ibs.service.user.User;
import ibs.tech.html.BuildException;
import ibs.tech.html.FormElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.IE302;
import ibs.tech.html.ImageElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.NewLineElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.SelectElement;
import ibs.tech.html.SpanElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.tech.http.HttpArguments;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.util.DateTimeHelpers;
import ibs.util.NoAccessException;
import ibs.util.UtilConstants;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Vector;


/******************************************************************************
 * This class represents one object of type TerminPlan with version 01. <BR/>
 *
 * @version     $Id: Terminplan_01.java,v 1.38 2012/03/02 11:01:55 rburgermann Exp $
 *
 * @author      Horst Pichler   (HP), 980428
 ******************************************************************************
 */
public class Terminplan_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Terminplan_01.java,v 1.38 2012/03/02 11:01:55 rburgermann Exp $";


    /**
     *  create date formatter - full
     */
    private DateFormat p_dateFormatFull =
        DateFormat.getDateInstance (DateFormat.FULL, this.l);

    /**
     * create date formatter - default
     */
    private DateFormat p_dateFormatDefault =
        DateFormat.getDateInstance (DateFormat.DEFAULT, this.l);

    /**
     * current view date - needed for the current day or month
     * in day or month view
     */
    private Date p_curViewDate = new Date ();

    /**
     * last view was content. <BR/>
     */
    protected static final int V_CONTENT = 0;

    /**
     * last view was month. <BR/>
     */
    protected static final int V_MONTH = 1;

    /**
     * last view was day. <BR/>
     */
    protected static final int V_DAY = 2;

    /**
     * The last view on the object. <BR/>
     */
    public int lastView = Terminplan_01.V_MONTH;

    /**
     * Separator in header. <BR/>
     */
    private static final String HEADERSEP = " - ";

    /**
     * Rule type: ALL. <BR/>
     */
    private static final String RULETYPE_ALL = "ALL";
    /**
     * Rule type: NONE. <BR/>
     */
    private static final String RULETYPE_NONE = "NONE";

    /**
     * Frame type: VOID. <BR/>
     */
    private static final String FRAMETYPE_VOID = "VOID";
    /**
     * Frame type: BOX. <BR/>
     */
    private static final String FRAMETYPE_BOX = "BOX";


    /**************************************************************************
     * This constructor creates a new instance of the class Terminplan_01.
     * <BR/>
     */
    public Terminplan_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
        Locale_01 curLoc = MultilingualTextProvider.getUserLocale(env);
        if (curLoc != null)
        {
            p_dateFormatFull = DateFormat.getDateInstance (DateFormat.FULL, curLoc.getLocale ());
            p_dateFormatDefault = DateFormat.getDateInstance (DateFormat.DEFAULT, curLoc.getLocale ());
        } // if
        // init specifics of actual class:
    } // Terminplan_01


    /**************************************************************************
     * Creates a Terminplan_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Terminplan_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:
        Locale_01 curLoc = MultilingualTextProvider.getUserLocale(env);
        if (curLoc != null)
        {
            p_dateFormatFull = DateFormat.getDateInstance (DateFormat.FULL, curLoc.getLocale ());
            p_dateFormatDefault = DateFormat.getDateInstance (DateFormat.DEFAULT, curLoc.getLocale ());
        } // if

        // init specifics of actual class:
    } // Terminplan_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // define used views:
        this.viewContent = "v_Terminplan_01$content";

        // class name of row element:
        this.elementClassName = "m2.diary.TerminplanElement_01";
    } // initClassSpecifics


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     * The query or view must at least have the attributes uid and rights.
     * Queries on these attributes have to be addable to this query. <BR/>
     * The query must have at least one constraint within WHERE to ensure that
     * other constraints can be added with AND. <BR/>
     * <B>Standard format:</B>. <BR/>
     * <PRE>
     *      "SELECT DISTINCT oid, &lt;other attributes&gt; " +
     *      " FROM " + this.viewContent +
     *      " WHERE containerId = " + this.oid;. <BR/>
     * </PRE>
     * This method can be overwritten in subclasses. <BR/>
     * As you can see the query should contain at least the oid.
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        return new StringBuffer ()
            .append (" SELECT oid, state, name, typeName, isLink,")
            .append ("    linkedObjectId, userId, owner, ownerName, ownerOid,")
            .append ("    ownerFullname, lastChanged, isNew, icon,")
            .append ("    description, startDate, endDate, place")
            .append (" FROM  ").append (this.viewContent)
            .append (" WHERE containerId = ").append (this.oid.toStringQu ())
            .toString ();
    } // createQueryRetrieveContentData


/**********
 * KR 040612 dropped away because of performance reasons.
 *           This functionality is now fully implemented in ibs.bo.Container.
 * ...
    / **************************************************************************
     * Create the query to check if copied/cutted data is still valid.
     * <BR/>
     * This method can be overwritten in subclasses. <BR/>
     *
     * @param   selectedElements    Elements that were previously copied/cutted.
     *
     * @return  The constructed query.
     * /
    protected String createQueryCopyData (Vector selectedElements)
    {
        StringBuffer oidList = new StringBuffer ();
        StringBuffer sep = new StringBuffer (); // the separator
        StringBuffer standardSep = new StringBuffer (", ");

        // put all selected elements into a string, where the elements are
        // separated by a comma
        // loop through all elements of the list:
        for (Enumeration selElemEnum = selectedElements.elements (); selElemEnum.hasMoreElements ();)
        {
            // separate from previous elements:
            oidList.append (sep);

            // get the actual element:
            OID oid = (OID) selElemEnum.nextElement ();
            oidList.append (oid.toStringQu ());

            // set the separator to the next element:
            sep = standardSep;
        } // for

        // select the data of all objects that are in the oidList:
        return new StringBuffer ()
            .append (" SELECT oid, state, name, typeName, isLink,")
            .append ("    linkedObjectId, userId, owner, ownerName, ownerOid,")
            .append ("    ownerFullname, lastChanged, isNew, icon,")
            .append ("    description, startDate, endDate, place")
            .append (" FROM  ").append (this.viewContent)
            .append (" WHERE  oid IN (").append (oidList).append (")")
            .toString ();
    } // createQueryCopyData
 * ...
 * KR 040612
 **********/


    /**************************************************************************
     * Create the query to get all data for the day view for given intervall.
     * <BR/>
     *
     * @param   dayBegin     datetime as string (start of intervall). <BR/>
     * @param   dayEnd       datetime as string (end of intervall). <BR/>
     *
     * @return  The constructed query.
     */
    protected StringBuffer createQueryDayView (StringBuffer dayBegin,
                                             StringBuffer dayEnd)
    {
        StringBuffer queryStr = new StringBuffer ();
                                        // the query string
        StringBuffer sqlBegin = SQLHelpers.getDateString (dayBegin);
        StringBuffer sqlEnd = SQLHelpers.getDateString (dayEnd);

        // terms resultset must be orderd by term begin-time PLUS
        // the length (begin-end) of the terms

        // build query: get terms of given diary out of the database:
        // startdates and/or enddates of a term, which belongs into the
        // day view of the given day, will be bend (to first or
        // last minute of day) if they are before/after day
        // - necessary for viewing termsa

        queryStr = new StringBuffer ()
            .append (" SELECT oid, name, place, startDate, endDate")
/*
            // get startdate: if startdate is before day to view set it to 00:00
.append (" DECODE (SIGN (startDate - " + sqlBegin + "), -1, " + sqlBegin + ", startDate) AS startDate,")
        " CASE WHEN startDate < '" + dayBegin + "' THEN '" + dayBegin + "' ELSE startDate END AS startDate," +
            // get enddate: if endate is after day to view set it to 23:59
            .append (SQLHelpers.getSelectCondition ("endDate - " + sqlEnd, "0", sqlEnd.toString (), "endDate"))
            .append (" AS endDate,")
.append (" DECODE (SIGN (endDate - " + sqlEnd + "), 1, " + sqlEnd + ", endDate) AS endDate,")
        " CASE WHEN endDate > '" + dayEnd + "' THEN '" + dayEnd + "' ELSE endDate END AS endDate, " +
            // get difference of startdate and enddate in hours
            .append (SQLHelpers.getDateDiff (new StringBuffer ("endDate"),
                                             new StringBuffer ("startDate"),
                                             SQLConstants.UNIT_HOUR))
            .append (" AS difference")
*/
            .append (" FROM ").append (this.viewContent)
            // get only terms which graze given day
            .append (" WHERE endDate > ").append (sqlBegin)
                .append (" AND startDate < ").append (sqlEnd)
/* KR better algorithm found (see above)
            .append (" WHERE   (startDate ")
            .append (           SQLHelpers.getBetween (sqlBegin, sqlEnd))
            .append (       " OR endDate ")
            .append (           SQLHelpers.getBetween (sqlBegin, sqlEnd))
            .append (       " OR")
            .append (           " (startDate < ").append (sqlBegin)
            .append (           " AND endDate > ").append (sqlEnd).append ("))")
*/
                // get only terms of given diary (term-container)
                .append (" AND containerId = ").append (this.oid.toStringQu ())
                // check if user has (at least) right to view terms
                .append (" AND userId = ").append (this.user.id)
                .append (SQLHelpers.getStringCheckRights (Operations.OP_VIEW))
            // order is important - viewing of terms according to this order
            .append (" ORDER BY startDate ASC");

        // return the result:
        return queryStr;
    } // createQueryDayView


    /**************************************************************************
     * Create the query to get all data for the month view for given intervall.
     * <BR/>
     *
     * @param   monthBegin     datetime as string (start of intervall). <BR/>
     * @param   monthEnd       datetime as string (end of intervall). <BR/>
     *
     * @return  The constructed query.
     */
    protected StringBuffer createQueryMonthView (StringBuffer monthBegin,
                                               StringBuffer monthEnd)
    {
        StringBuffer queryStr = new StringBuffer ();
                                        // the query string
        StringBuffer sqlBegin = SQLHelpers.getDateString (monthBegin);
        StringBuffer sqlEnd = SQLHelpers.getDateString (monthEnd);

        // terms resultset must be orderd by term begin-time PLUS
    // the length (begin-end) of the terms

        // build query: get terms of given diary out of the database:
        // startdates and/or enddates of a term, which belongs into the
        // month view of the given month, will be bend (to first or
        // last of month) if they are before/after month
        // - necessary for viewing terms

        queryStr = new StringBuffer ()
            .append (" SELECT oid, name, place, startDate, endDate")
/*
            // get startdate: if startdate is before month to view set it to first of month
            "        DECODE (SIGN (startDate - " + oraBegin + "), -1, " + oraBegin + ", startDate) AS startDate," +
            "        CASE WHEN startDate < '" + monthBegin + "' THEN '" + monthBegin + "' ELSE startDate END AS startDate," +
            // get enddate: if enddate is after month to view set it to last of month
            "        DECODE (SIGN (endDate - " + oraEnd + "), 1, " + oraEnd + ", endDate) AS endDate" +
            "        CASE WHEN endDate > '" + monthEnd + "' THEN '" + monthEnd + "' ELSE endDate END AS endDate " +
*/
            .append (" FROM ").append (this.viewContent)
            // get only terms which graze given month:
            .append (" WHERE endDate > ").append (sqlBegin)
                .append (" AND startDate < ").append (sqlEnd)
/* KR better algorithm found (see above)
            " WHERE   (startDate BETWEEN " + oraBegin + " AND " + oraEnd +
            "         OR " +
            "          endDate BETWEEN " + oraBegin + " AND " + oraEnd +
            "         OR (startDate < " + oraBegin + " AND endDate > " + oraEnd + ")) " +
            " WHERE   (startDate BETWEEN '" + monthBegin + "' AND '" + monthEnd + "' " +
            "        OR " +
            "         endDate BETWEEN '" + monthBegin + "' AND '" + monthEnd + "' " +
            "        OR (startDate < '" + monthBegin + "' AND endDate > '" + monthEnd + "')) " +
*/
                // get only terms of given diary (term-container)
                .append (" AND containerId = ").append (this.oid.toStringQu ())
                // check if user has (at least) right to view terms
                .append (" AND userId = ").append (this.user.id)
                .append (SQLHelpers.getStringCheckRights (Operations.OP_VIEW))
            // order is important - viewing of terms according to this order
            .append (" ORDER BY startDate ASC");

        return queryStr;
    } // createQueryMonthView


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This metod is used to get all attributes of one element out of the
     * resultset. The attribute names which can be used are the ones which
     * are defined within the resultset of
     * <A HREF="#createQueryRetrieveContentData">createQueryRetrieveContentData</A>.
     * <BR/>
     * <B>Format:</B>. <BR/>
     * for oid properties:
     *      obj.&lt;property> = getQuOidValue (action, "&lt;attribute>");. <BR/>
     * for other properties:
     *      obj.&lt;property> = action.get&lt;type> ("&lt;attribute>");. <BR/>
     * The property <B>oid</B> is already gotten. This must not be done within this
     * method. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     *
     * @param   action      The action object used to work on the database.
     * @param   commonObj   Object of common representing the list element.
     *
     * @exception   DBError
     *              Error when executing database statement.
     */
    protected void getContainerElementData (SQLAction action, ContainerElement commonObj)
        throws DBError
    {
        // convert common element object to actual type:
        TerminplanElement_01 obj = (TerminplanElement_01) commonObj;
        // get common attributes:
        super.getContainerElementData (action, obj);

        // get element type specific attributes:
        obj.startDate = action.getDate ("startDate");
        obj.endDate = action.getDate ("endDate");
        obj.place = action.getString ("place");
    } // getContainerElementData


    /**************************************************************************
     * Performs the day view of all terms of this object. <BR/>
     */
    public void dayView ()
    {
        Vector<TermElement> terms;      // the terms

        // remember this view:
        this.lastView = Terminplan_01.V_DAY;

        // get the data:
        terms = this.performGetDayViewData (this.p_curViewDate);

        // display the data:
        this.performDayView (this.p_curViewDate, terms);
    } // dayView


    /**************************************************************************
     * Get all data which is necessary for a day view. <BR/>
     *
     * @param   day     The day for which to create a day view.
     *
     * @return  A vector of the terms which are on the requested day.
     *          <CODE>null</CODE> if the user has no access to the object.
     */
    protected Vector<TermElement> performGetDayViewData (Date day)
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        StringBuffer dayBegin;          // begin date of day
        StringBuffer dayEnd;            // end date of day
        Date dayBeginDate;              // begin date of day
        Date dayEndDate;                // end date of day
//        String dayOfWeek;               // day within week
//        int weekOfYear;                 // calender week
        GregorianCalendar cal;          // the calendar for the actual day
        int rowCount;                   // row counter
        // vector with the different terms:
        Vector<TermElement> terms = new Vector<TermElement> ();
        TermElement t;                  // the current term
        StringBuffer queryStr;          // the query string

        // populate properties:
        try
        {
            this.performRetrieveData (Operations.OP_VIEW);
        } // try
        catch (NoAccessException e)
        {
            this.showNoAccessMessage (Operations.OP_VIEW);
            return null;
        } // catch
        catch (ObjectNotFoundException e)
        {
            this.showObjectNotFoundMessage ();
            return null;
        } // catch

        // create a GregorianCalendar with default timezone and locale:
        cal = new GregorianCalendar ();

        // calendar fills missing data
        // set view date in calendar
        cal.setTime (day);

        // get string for weekday:
//        dayOfWeek = DiaryTokens.DV_WEEKDAYS[cal.get (Calendar.DAY_OF_WEEK)];

        // get calender week:
//        weekOfYear = cal.get (Calendar.WEEK_OF_YEAR);

        // create date strings for db search; search from first hour of
        // given day to last hour of given day

        cal.set (Calendar.HOUR_OF_DAY, 0);
        cal.set (Calendar.MINUTE, 0);
        cal.set (Calendar.SECOND, 0);
        cal.set (Calendar.MILLISECOND, 0);
        dayBegin = SQLHelpers.dateStringB (cal);
        dayBeginDate = cal.getTime ();

        cal.set (Calendar.HOUR_OF_DAY, 23);
        cal.set (Calendar.MINUTE, 59);
        cal.set (Calendar.SECOND, 59);
        cal.set (Calendar.MILLISECOND, 999);
        dayEnd = SQLHelpers.dateStringB (cal);
        dayEndDate = cal.getTime ();

        // create query to get terms for given time interval
        queryStr = this.createQueryDayView (dayBegin, dayEnd);
//debug (queryStr);

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection - only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();

            rowCount = action.execute (queryStr, false);

            // error while executing?
            if (rowCount >= 0)          // no error?
            {
                // get tuples out of db:
                while (!action.getEOF ())
                {
                    // create new vector element:
                    t = new TermElement ();

                    // fill values:
                    t.oid = SQLHelpers.getQuOidValue (action, "oid");
                    t.name = action.getString ("name");
                    t.place = action.getString ("place");

                    t.startDate = action.getDate ("startDate");
                    if (t.startDate.before (dayBeginDate))
                    {
                        t.startDate = dayBeginDate;
                    } // if

                    t.endDate = action.getDate ("endDate");
                    if (t.endDate.after (dayEndDate))
                    {
                        t.endDate = dayEndDate;
                    } // if

                    // add term to vector:
                    terms.addElement (t);

                    // get next term:
                    action.next ();
                } // while
            } // if no error

            // the last tuple has been processed
            // end transaction
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (dbErr, this.app, this.sess, this.env, false);
        } // catch
        finally
        {
            // close db connection in every case - only workaround - db
            // connection must be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        // return the computed terms:
        return terms;
    } // performGetDayViewData


    /**************************************************************************
     * Performs the day view of all terms of this object. <BR/>
     *
     * @param   day     The day for which to create a day view.
     * @param   terms   The terms for this day.
     */
    protected void performDayView (Date day, Vector<TermElement> terms)
    {
        GregorianCalendar cal = new GregorianCalendar ();
        GregorianCalendar calStart = new GregorianCalendar ();
        GregorianCalendar calEnd = new GregorianCalendar ();
        int startHour;                  // start hour of actual term
        // Enumeration object for terms:
        Enumeration<TermElement> termsE = terms.elements ();
        TermElement t;
        String dayString = this.p_dateFormatFull.format (day);
                                        // string that represents the current day
        NewLineElement newLine = new NewLineElement (); // new-line element
        TextElement text = new TextElement (""); // common text element
        GroupElement termGroup = new GroupElement ();
                                        // group to view term in inner table
        Page page;                      // the output page
        StyleSheetElement style;        // stylesheet for the page
        GroupElement header;            // the page header
        RowElement trOuter;
        TableElement tableOuter;        // table
        RowElement trHeader;
        TableElement tableHeader;       // table
        RowElement trNavigation;
        TableElement tableNavigation;   // table
        TableElement tableButton;       // table
        int maxColumns = 5;             // maximum number of columns
        int maxRows = DiaryConstants.DV_TIME_VIEW_END -
            DiaryConstants.DV_TIME_VIEW_START + 3;
                                        // number of viewed hour-rows - is
                                        // calculated from constants which
                                        // define the viewable area -
                                        // + 2 means the time before and the
                                        // time after the viewable area
                                        // (2 additional rows)
        RowElement tr;                  // actual table row
        TableDataElement td;            // actual table cell
        TableElement table;             // the day view table
        ImageElement leftArrow;         // left image
        LinkElement linkLeftArrow;      // link behind left image
        ImageElement rightArrow;        // right image
        LinkElement linkRightArrow;     // link behind right image
        int actHour;                    // the actual hour
        int morningRowNum = 0;          // number of morning row
        int eveningRowNum = maxRows - 1; // number of evening row

        // initialize the calendar:
        cal.setTime (day);

        // create new page -> false: no frameset
        page = new Page ( 
            MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                DiaryTokens.ML_TERM_DIARY, env) +  
            MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                DiaryTokens.ML_VIEW_LIST, env), false);
        style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" +
            this.sess.activeLayout.elems[LayoutConstants.CALENDAR].styleSheet;
        page.head.addElement (style);

        // add header to page:
        header = this.createHeader (page,
            this.name + Terminplan_01.HEADERSEP +  
            MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                DiaryTokens.ML_VIEW_DAY, env),
            this.getNavItems (), null, null, this.icon, this.containerName);
        page.body.addElement (header);

        // get first term out of enumeration:
        if (termsE.hasMoreElements ())
        {
            t = termsE.nextElement ();
        } // if
        else
        {
            t = null;
        } // else

/*      Did not work correctly
        will be replaced in  future version

        String weekString = DiaryTokens.DV_CALENDARWEEK_SHORT +
            weekOfYear + IE302.HCH_NBSP;
*/

        // set timezone of date format - this is necessary due
        // to a bug in some JDK versions
        this.p_dateFormatDefault.setTimeZone (TimeZone.getDefault ());
        this.p_dateFormatFull.setTimeZone (TimeZone.getDefault ());

        // build tables -> day view is a table in a table
        // outer table: is the frame table, holds header table and inner table
        // header table: holds currently viewed date and navigation
        // inner table: holds all terms

        // create tables
        //
        // 1. outer table
        //
        //    holds viewed date, day, calendar week & inner table
        tableOuter = new TableElement (1); // table: 1 column
        tableOuter.classId = DiaryConstants.CLASS_DAY;
        tableOuter.ruletype = Terminplan_01.RULETYPE_ALL;
        tableOuter.frametypes = Terminplan_01.FRAMETYPE_BOX;
        tableOuter.width = HtmlConstants.TAV_FULLWIDTH;
        tableOuter.border = 1;

        // 2. header table - formats displayed text in header row of
        //    outer table
        tableHeader = new TableElement (3); // table: 3 columns
        tableHeader.classId = DiaryConstants.CLASS_CALHEADER;
        tableHeader.width = HtmlConstants.TAV_FULLWIDTH;
        tableHeader.border = 0;

        // 2.1 navigation table - displays date and arrows for navigation in
        // header table
        tableNavigation = new TableElement (3); // table: 3 columns
        tableNavigation.border = 0;
        tableNavigation.width = HtmlConstants.TAV_FULLWIDTH;

        // 2.2 button table - displays three buttons to navigate without tabs
        // header table
        tableButton = new TableElement (3);   // table: 3 columns
        tableButton.border = 0;
        tableButton.width = HtmlConstants.TAV_FULLWIDTH;

        // 3. inner table - holds all terms
        // check if more terms in one row
        if (terms.size () > maxColumns)
        {
            // set maximum number of columns:
            maxColumns = terms.size ();
        } // if

        td = new TableDataElement (text);
        // create table (day view) and set its properties:
        table = new TableElement (maxColumns);

        // fill navigation table
        // create row with 3 columns:
        trNavigation = new RowElement (3);
        // first col - arrow to the left
        leftArrow = new ImageElement (BOPathConstants.PATH_GLOBAL + "zurueck.gif");
        leftArrow.alt =  
            MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                DiaryTokens.ML_PREVIOUS_DAY, env);
        linkLeftArrow =
            new LinkElement (leftArrow,
                IOConstants.URL_JAVASCRIPT + "self.open ('" + this.getBaseUrlGet () +
                    HttpArguments.createArg (BOArguments.ARG_FUNCTION, "" + DiaryFunctions.FCT_TERM_DAY_PREV) +
                    HttpArguments.createArg (BOArguments.ARG_OID, "" + this.oid) +
                    HttpArguments.createArg (DiaryArguments.ARG_TERM_CUR_VIEW_DATE,
                        "" + (cal.getTime ()).getTime ()) + "', '" +
                    HtmlConstants.FRM_SHEET + "')");

        TableDataElement lfArrowTd = new TableDataElement (linkLeftArrow);
        lfArrowTd.width = "1%";
        trNavigation.addElement (lfArrowTd);
        // second col - current date
        text = new TextElement (dayString);
        trNavigation.addElement (new TableDataElement (text));
        // first col - arrow to the left
        rightArrow = new ImageElement (BOPathConstants.PATH_GLOBAL + "vor.gif");
        rightArrow.alt =  
            MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                DiaryTokens.ML_NEXT_DAY, env);
        linkRightArrow =
            new LinkElement (rightArrow,
                IOConstants.URL_JAVASCRIPT + "self.open ('" + this.getBaseUrlGet () +
                    HttpArguments.createArg (BOArguments.ARG_FUNCTION, "" + DiaryFunctions.FCT_TERM_DAY_NEXT) +
                    HttpArguments.createArg (BOArguments.ARG_OID, "" + this.oid) +
                    HttpArguments.createArg (DiaryArguments.ARG_TERM_CUR_VIEW_DATE,
                        "" + (cal.getTime ()).getTime ()) + "', '" +
                    HtmlConstants.FRM_SHEET + "')");
        TableDataElement rgArrowTd = new TableDataElement (linkRightArrow);
        rgArrowTd.width = "1%";
        trNavigation.addElement (rgArrowTd);
        // add to table
        tableNavigation.addElement (trNavigation);


        // fill header table
        // create row with 3 columns:
        trHeader = new RowElement (4);
        // first col - navigation table
        TableDataElement navTd = new TableDataElement (tableNavigation);
        navTd.width = "33%";
        trHeader.addElement (navTd);
        // set second column - blank
        text = new TextElement (IE302.HCH_NBSP);
        TableDataElement spaceTd = new TableDataElement (text);
        spaceTd.width = "1%";
        trHeader.addElement (spaceTd);

        // third col - goto date form
        FormElement form = new FormElement (
            this.getBaseUrlPost () +
                HttpArguments.createArg (BOArguments.ARG_FUNCTION, "" +
                    DiaryFunctions.FCT_TERM_DAY_GOTO) +
                HttpArguments.createArg (BOArguments.ARG_OID, this.oid
                    .toString ()), UtilConstants.HTTP_POST,
            HtmlConstants.FRM_SHEET);
        form.name = "form1";
        form.classId = "navButtons";
        // create submit button
        InputElement submit =
            new InputElement ("Submitbutton", InputElement.INP_SUBMIT, 
                MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                    DiaryTokens.ML_BUTTON_GOTO, env));
        // create input field for year
        InputElement inputDate =
            new InputElement (DiaryArguments.ARG_TERM_DATE, InputElement.INP_TEXT, "" + this.p_dateFormatDefault.format (cal.getTime ()));
        inputDate.onBlur = "top.iD (document.forms[0]." + DiaryArguments.ARG_TERM_DATE + ", false)";
        inputDate.size = 10;
        // add created form elements
        form.addElement (inputDate);
        form.addElement (submit);
        TableDataElement formTd = new TableDataElement (form);
        formTd.width = "33%";
        // construct table elements
        trHeader.addElement (formTd);

        // third col - button table
        trHeader.addElement (renderAdditionalNavigationButtons ());
        
        // add row to header table
        tableHeader.addElement (trHeader);

        // fill outer table
        // create first row of outer table - with 1 column
        trOuter = new RowElement (1);
        // add header table to first row of outer table
        trOuter.addElement (new TableDataElement (tableHeader));
        tableOuter.addElement (trOuter);
        trOuter = new RowElement (1);

        // fill inner table
//        table.border = 1;
        table.border = 0;
        table.ruletype = Terminplan_01.RULETYPE_NONE;
        table.frametypes = Terminplan_01.FRAMETYPE_VOID;
        table.width = HtmlConstants.TAV_FULLWIDTH;
        table.classId = DiaryConstants.CLASS_CALBODY;

        String[] classIds = new String[maxColumns];
        classIds[0] = DiaryConstants.CLASS_COLHOUR;
        for (int i = 1; i < maxColumns; i++)
        {
            classIds[i] = DiaryConstants.CLASS_COLTERMIN;
        } // for
        table.classIds = classIds;

        // create inner table grid - fill it with values:
        // outer loop, rows
        for (int row = 0; row < maxRows; row++)
        {
            // set the actual hour:
            actHour = row + DiaryConstants.DV_TIME_VIEW_START - 1;

            // create row element
            tr = new RowElement (1);

            // first column of row - set hour text:

            // check row number:
            if (row == morningRowNum)
            {
                // first row - set specific hour text
                text = new TextElement ( 
                    MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                        DiaryTokens.ML_DV_TIME_MORNING, env));
            } // if
            else if (row == eveningRowNum)
            {
                // last row - set specific hour text
                text = new TextElement ( 
                    MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                        DiaryTokens.ML_DV_TIME_EVENING, env));
            } // else if
            else
            {
                // row between first and last row - set hour text
                text = new TextElement (
                    MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                        DiaryTokens.DV_TIME_HOURS[row + DiaryConstants.DV_TIME_VIEW_START - 1], env));
            } // else

            // create table data element with specific text:
            td = new TableDataElement (text);
            td.width = "5%";

            // add table data element to row
            tr.addElement (td);

            // inner loop - columns
            for (int col = 1; col < maxColumns; col++)
            {
                // are there any terms to display?
                if (t != null)
                {
                    // set times in calendars
                    calStart.setTime (t.startDate);
                    calEnd.setTime (t.endDate);
                    startHour = calStart.get (Calendar.HOUR_OF_DAY);

                    // differ between 3 viewing blocks:
                    // morning block: e.g. the whole morning 0am to 8am
                    //                show in one row only
                    // day block: e.g. 8am to 6pm
                    //                show a row for each hour
                    // evening block: e.g. the whole evening 6pm to 12pm
                    //                show in one row only
                    boolean allowed =
                        // the morning block:
                        ((row == morningRowNum) &&
                        // check if term is in the morning block:
                        (startHour < DiaryConstants.DV_TIME_VIEW_START)) ||

                        // the day block:
                        ((row > morningRowNum) && (row < eveningRowNum) &&
                        // check if current table cell is hour for
                        // term to display
                        (startHour == actHour)) ||

                        // the evening block:
                        ((row == eveningRowNum) &&
                        // check if term is in the evening block
                        (startHour > DiaryConstants.DV_TIME_VIEW_END));

                    if (allowed)
                    {
                        termGroup = this.getDayViewTerm (t);

                        // create new table data element
                        td = new TableDataElement (termGroup);
                        td.classId = DiaryConstants.CLASS_TERMIN;
                        td.rowspan = this.calcRowSpan (t);

                        // add table data element to row:
                        tr.addElement (td);

                        // goto next term in vector:
                        if (termsE.hasMoreElements ())
                        {
                            t = termsE.nextElement ();
                        } // if
                        else
                        {
                            t = null;
                        } // else

                        // initialize for next loop:
                        allowed = false;
                    } // if
                } // if
            } // for

            // add row to table
            table.addElement (tr);
        } // for

        table.setColsRight ();

        // add inner table to outer table
        trOuter.addElement (new TableDataElement (table));
        tableOuter.addElement (trOuter);

        // add outer table to page
        page.body.addElement (tableOuter);
        page.body.addElement (newLine);

        if (this.p_isShowCommonScript)
        {
            if (this.p_tabs != null)        // tabs exist?
            {
                Tab monthTab = this.p_tabs.find ("Day");
                this.p_tabs.setActiveTab (monthTab);
            } // if
            else
            {
                // get the tab data out of the data base:
                this.p_tabs = this.performRetrieveObjectTabData ();

                if (this.p_tabs != null)        // tabs now exists?
                {
                    Tab monthTab = this.p_tabs.find ("Day");
                    this.p_tabs.setActiveTab (monthTab);
                } // if
            } // else

            // create the script to be executed on client:
            ScriptElement script = this.getCommonScript (true);

            // add to page:
            page.body.addElement (script);
        } // if

        // try to build constructed page
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            // TODO: handle the exception
        } // catch
    } // performDayView


    /**************************************************************************
     * Get the day view for one term. <BR/>
     *
     * @param   term    The term for which to create the day view.
     *
     * @return  The layout element containing the day view of the term.
     */
    protected GroupElement getDayViewTerm (TermElement term)
    {
        String url = new String ("");   // URL string
        NewLineElement newLine = new NewLineElement (); // new-line element
        GroupElement termGroup = new GroupElement ();
                                        // group to view term in inner table
        // text elements to view term in group:
        TextElement termTime = new TextElement ("");
        TextElement termName = new TextElement ("");
        TextElement termPlace = new TextElement ("");
        TextElement termPart = new TextElement ("");
        LinkElement linkName;           // link elements for terms
        LinkElement linkPart;           // link elements for terms
        SpanElement span;               // a span element
        GroupElement linkGroup;         // group with link for term


        // create new GroupElement:
        termGroup = new GroupElement ();

        // create text elements:
        termTime =
            new TextElement (DateTimeHelpers.timeToString (term.startDate) +
                Terminplan_01.HEADERSEP + DateTimeHelpers.timeToString (term.endDate));
        termName = new TextElement (term.name);

        linkGroup = new GroupElement ();
        if (term.oid != null)
        {
            // build url:
            url = IOHelpers.getShowObjectJavaScriptUrl (term.oid.toString ());
            linkName = new LinkElement (termName, url);
            linkGroup.addElement (linkName);
        } // if
        else
        {
            linkGroup.addElement (termName);
        } // else

        termPlace = new TextElement (term.place);


        span = new SpanElement ();
        span.addElement (termTime);
        span.classId = DiaryConstants.CLASS_TERMTIME;
        termGroup.addElement (span);

        termGroup.addElement (newLine);

        span = new SpanElement ();
        span.addElement (linkGroup);
        span.classId = DiaryConstants.CLASS_TERMNAME;
        termGroup.addElement (span);

        termGroup.addElement (newLine);

        span = new SpanElement ();
        span.addElement (termPlace);
        span.classId = DiaryConstants.CLASS_TERMPLACE;
        termGroup.addElement (span);

        // add participants to term group?
        if (term.participants)
        {
            termPart = new TextElement (term.numParticipants +
                MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                    DiaryTokens.ML_TERM_PARTICIPANTS, env));
            linkPart = new LinkElement (termPart, "xxx");

            termGroup.addElement (newLine);

            span = new SpanElement ();
            span.addElement (linkPart);
            span.classId = DiaryConstants.CLASS_TERMPART;
            termGroup.addElement (span);
        } // if

        // return the new layout element:
        return termGroup;
    } // getDayViewTerm


    /**************************************************************************
     * Performs the day view of all terms of this object. <BR/>
     */
    public void monthView ()
    {
        Vector<TermElement>[] terms;    // the terms

        // remember this view:
        this.lastView = Terminplan_01.V_MONTH;

        // get the data:
        terms = this.performGetMonthViewData (this.p_curViewDate);

        // display the data:
        this.performMonthView (this.p_curViewDate, terms);
    } // dayView

    
    /**************************************************************************
     * Create an initialized vector for the terms of one month. <BR/>
     *
     * @return  An array containing an initialized vector for each day of a
     *          month. (element indexes 0..31)
     */
    @SuppressWarnings ("unchecked") // suppress compiler warning
    private Vector<TermElement>[] createMonthTermsVector ()
    {
        // create vector - holds all terms of the act. month - one
        // vector for each day.
        // a term which has a time span of more than one day
        // is held (in month-view) on every day of its time span
        // remark: must be implemented with two variables because
        // Vector<TermElement>[] cannot be initialized in java.
        Vector[] monthTerms =
        {
            new Vector<TermElement> (), new Vector<TermElement> (),
            new Vector<TermElement> (), new Vector<TermElement> (),
            new Vector<TermElement> (), new Vector<TermElement> (),
            new Vector<TermElement> (), new Vector<TermElement> (),
            new Vector<TermElement> (), new Vector<TermElement> (),
            new Vector<TermElement> (), new Vector<TermElement> (),
            new Vector<TermElement> (), new Vector<TermElement> (),
            new Vector<TermElement> (), new Vector<TermElement> (),
            new Vector<TermElement> (), new Vector<TermElement> (),
            new Vector<TermElement> (), new Vector<TermElement> (),
            new Vector<TermElement> (), new Vector<TermElement> (),
            new Vector<TermElement> (), new Vector<TermElement> (),
            new Vector<TermElement> (), new Vector<TermElement> (),
            new Vector<TermElement> (), new Vector<TermElement> (),
            new Vector<TermElement> (), new Vector<TermElement> (),
            new Vector<TermElement> (), new Vector<TermElement> (),
        }; // monthTerms

        // return the computed terms:
        return monthTerms;
    } // createMonthTermsVector


    /**************************************************************************
     * Get all data which is necessary for a month view. <BR/>
     *
     * @param   month   The month for which to create a month view.
     *
     * @return  A vector of the terms which are in the requested month.
     *          <CODE>null</CODE> if the user has no access to the object.
     */
    protected Vector<TermElement>[] performGetMonthViewData (Date month)
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        StringBuffer monthBegin;        // begin date of month
        StringBuffer monthEnd;          // end date of month
        Date monthBeginDate;            // begin date of month
        Date monthEndDate;              // end date of month
        GregorianCalendar cal;          // the calendar for the actual day
        int rowCount;                   // row counter
        TermElement t;                  // the current term
        GregorianCalendar calStart = new GregorianCalendar ();
        GregorianCalendar calEnd = new GregorianCalendar ();
        StringBuffer queryStr;          // the query string
        int startDay;                   // day of month for start of term
        int dateDiff;                   // difference fields for terms
        Vector<TermElement>[] monthTerms = this.createMonthTermsVector ();

        // populate properties:
        try
        {
            this.performRetrieveData (Operations.OP_VIEW);
        } // try
        catch (NoAccessException e)
        {
            this.showNoAccessMessage (Operations.OP_VIEW);
            return null;
        } // catch
        catch (ObjectNotFoundException e)
        {
            this.showObjectNotFoundMessage ();
            return null;
        } // catch

        // create a GregorianCalendar with default timezone and locale:
        cal = new GregorianCalendar ();

        // calendar fills missing data
        // set view date in calendar
        cal.setTime (month);

        // create date strings for db search; search from first day of
        // given month to last day of given month

        cal.set (Calendar.DAY_OF_MONTH,
                 cal.getActualMinimum (Calendar.DAY_OF_MONTH));
        cal.set (Calendar.HOUR_OF_DAY, 0);
        cal.set (Calendar.MINUTE, 0);
        cal.set (Calendar.SECOND, 0);
        cal.set (Calendar.MILLISECOND, 0);
        monthBegin = SQLHelpers.dateStringB (cal);
        monthBeginDate = cal.getTime ();

        cal.set (Calendar.DAY_OF_MONTH,
                 cal.getActualMaximum (Calendar.DAY_OF_MONTH));
        cal.set (Calendar.HOUR_OF_DAY, 23);
        cal.set (Calendar.MINUTE, 59);
        cal.set (Calendar.SECOND, 59);
        cal.set (Calendar.MILLISECOND, 999);
        monthEnd = SQLHelpers.dateStringB (cal);
        monthEndDate = cal.getTime ();

        // create query to get terms for given time interval
        queryStr = this.createQueryMonthView (monthBegin, monthEnd);
//debug (queryStr);

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection - only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();

            rowCount = action.execute (queryStr, false);
// showMessage (rowCount+"");

            // error while executing?
            if (rowCount >= 0)          // no error?
            {
                // get tuples out of db:
                while (!action.getEOF ())
                {
                    // create new vector element:
                    t = new TermElement ();

                    // fill values:
                    t.oid = SQLHelpers.getQuOidValue (action, "oid");
                    t.name = action.getString ("name");
                    t.place = action.getString ("place");

                    t.startDate = action.getDate ("startDate");
                    if (t.startDate.before (monthBeginDate))
                    {
                        t.startDate = monthBeginDate;
                    } // if

                    t.endDate = action.getDate ("endDate");
                    if (t.endDate.after (monthEndDate))
                    {
                        t.endDate = monthEndDate;
                    } // if

                    // get number of days of term - day difference
                    calStart.setTime (t.startDate);
                    calEnd.setTime (t.endDate);
                    dateDiff =
                        calEnd.get (Calendar.DAY_OF_YEAR) -
                        calStart.get (Calendar.DAY_OF_YEAR) + 1;
                    t.numDays = dateDiff;
                    // action.getInt ("diff") + 1;

                    // set values
                    startDay = calStart.get (Calendar.DAY_OF_MONTH);

                    // set term for each day of time span in
                    // terms vector array
                    for (int i = 0; i < dateDiff; i++)
                    {
                        // add term for every day of time span to vector
                        monthTerms[startDay + i].addElement (t);
                    } // for

                    // get next term:
                    action.next ();
                } // while
            } // if no error

            // the last tuple has been processed
            // end transaction
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (dbErr, this.app, this.sess, this.env, false);
        } // catch
        finally
        {
            // close db connection in every case - only workaround - db
            // connection must be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        // return the computed terms:
        return monthTerms;
    } // performGetMonthViewData


    /**************************************************************************
     * Performs the month view of all terms of this object. <BR/>
     *
     * @param   month   The month for which to create a month view.
     * @param   terms   The terms within this month.
     */
    protected void performMonthView (Date month, Vector<TermElement>[] terms)
    {
        GregorianCalendar cal = new GregorianCalendar ();
                                        // GregorianCalendar with default
                                        // timezone and locale
        int lastOfMonth;                // last of month
        int halfOfMonth;                // half of month
        int yearNum = 2000;             // current year
        int monthNum = 1;               // current month
        String currentMonth;            // month as string
        String currentMonthYear;        // month and year as concatenated string
        NewLineElement newLine = new NewLineElement (); // new-line element
        TextElement text = new TextElement (""); // common text element
        Page page;                      // the output page
        StyleSheetElement style;        // stylesheet for the page
        GroupElement header;            // the page header
        RowElement trOuter;
        TableElement tableOuter;        // table
        RowElement trHeader;
        TableElement tableHeader;       // table
        RowElement trNavigation;
        TableElement tableNavigation;   // table
        TableElement tableButton;       // table
        TableElement table;             // the month view table
        RowElement tr;                  // actual table row
        TableDataElement td;            // actual table cell
        ImageElement leftArrow;         // left image
        LinkElement linkLeftArrow;      // link behind left image
        ImageElement rightArrow;        // right image
        LinkElement linkRightArrow;     // link behind right image
        TableElement tableLeft;         // inner left table
        TableElement tableRight;        // inner right table
        final String tdWidth = "50%";


        // calendar fills missing data
        // set view date in calendar:
        cal.setTime (month);

        // get last of month:
        lastOfMonth = cal.getActualMaximum (Calendar.DAY_OF_MONTH);
        // balancing: calculate half of month:
        halfOfMonth = this.getHalfOfMonth (terms, lastOfMonth);
//env.write (IE302.TAG_NEWLINE + "***nachher " + loops + "" + IE302.TAG_NEWLINE + "half: " + halfOfMonth +
//    IE302.TAG_NEWLINE + "left: " + numRowsLeft + "" + IE302.TAG_NEWLINE + "right: " + numRowsRight +
//    IE302.TAG_NEWLINE + "diffrows: " + diffRows + "" + IE302.TAG_NEWLINE + "lastDiffRows: " + lastDiffRows +
//    IE302.TAG_NEWLINE + "lastRowSwitched: " + lastRowSwitched +
//    IE302.TAG_NEWLINE + "lastBeforeLastRowSwitched: " + lastBeforeLastRowSwitched + IE302.TAG_NEWLINE);

        // get current year and month:
        yearNum = cal.get (Calendar.YEAR);
        monthNum = cal.get (Calendar.MONTH);

        currentMonth = MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
            DiaryTokens.MONTHS[monthNum], env);
        currentMonthYear = currentMonth + " " + yearNum;

        // build month view

        // create new page -> false: no frameset
        page = new Page (
            MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                DiaryTokens.ML_TERM_DIARY, env) + 
            MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                DiaryTokens.ML_VIEW_LIST, env), false);

        // Stylesheetfile wird geladen
        style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" +
            this.sess.activeLayout.elems[LayoutConstants.CALENDAR].styleSheet;
        page.head.addElement (style);

        // add header to page

        header = this.createHeader (page, this.name + Terminplan_01.HEADERSEP +
            MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                DiaryTokens.ML_VIEW_MONTH, env), 
            this.getNavItems (), null, null, this.icon, this.containerName);

        page.body.addElement (header);

        // build tables -> day view is a table in a table
        // outer table: is the frame table, holds header table and inner table
        // header table: holds currently viewed date and navigation
        // inner table: holds all terms in two tables (left table and right table)
        // left table: holds one half of the monthly days & terms
        // right table: holds the other half of the monthly days & terms

        // create tables
        //
        // 1. outer table
        //
        //    holds viewed date, day, calender week & inner table
        tableOuter = new TableElement (1);   // table with 1 column
        tableOuter.border = 1;
        tableOuter.classId = DiaryConstants.CLASS_MONTH;
        tableOuter.ruletype = Terminplan_01.RULETYPE_ALL;
        tableOuter.frametypes = Terminplan_01.FRAMETYPE_BOX;
        tableOuter.width = HtmlConstants.TAV_FULLWIDTH;

        // 2. header table - formats displayed text in header row of
        //    outer table
        tableHeader = new TableElement (4);   // table with 3 columns
        tableHeader.border = 0;
//        tableHeader.border = 1;
        tableHeader.ruletype = Terminplan_01.RULETYPE_NONE;
        tableHeader.frametypes = Terminplan_01.FRAMETYPE_VOID;
        tableHeader.width = HtmlConstants.TAV_FULLWIDTH;
        tableHeader.classId = DiaryConstants.CLASS_CALHEADER;

        // 2.1 navigation table - displays date and arrows for navigation in
        // header table
        tableNavigation = new TableElement (3);   // table: 3 columns
        tableNavigation.border = 0;
        tableNavigation.ruletype = Terminplan_01.RULETYPE_NONE;
        tableNavigation.frametypes = Terminplan_01.FRAMETYPE_VOID;
        tableNavigation.width = HtmlConstants.TAV_FULLWIDTH;

        // 2.2 button table - displays three buttons to navigate without tabs
        // header table
        tableButton = new TableElement (3);   // table: 3 columns
        tableButton.border = 0;
        tableButton.ruletype = Terminplan_01.RULETYPE_NONE;
        tableButton.frametypes = Terminplan_01.FRAMETYPE_VOID;
        tableButton.width = HtmlConstants.TAV_FULLWIDTH;

        // 3. inner table - holds all terms
        table = new TableElement (2);
        td = new TableDataElement (text);

        table.border = 0;
        table.ruletype = Terminplan_01.RULETYPE_NONE;
        table.frametypes = Terminplan_01.FRAMETYPE_VOID;
        table.width = HtmlConstants.TAV_FULLWIDTH;
        table.classId = DiaryConstants.CLASS_CALBODY;

        // fill navigation table
        // create row with 3 columns:
        trNavigation = new RowElement (3);
        // first col - arrow to the left
        leftArrow = new ImageElement (BOPathConstants.PATH_GLOBAL + "zurueck.gif");
        leftArrow.alt = "voriges Monat";
        linkLeftArrow =
            new LinkElement (
                leftArrow,
                IOConstants.URL_JAVASCRIPT + "self.open ('" +
                this.getBaseUrlGet () +
                HttpArguments.createArg (BOArguments.ARG_FUNCTION, "" + DiaryFunctions.FCT_TERM_MON_PREV) +
                HttpArguments.createArg (BOArguments.ARG_OID, this.oid.toString ()) +
                HttpArguments.createArg (DiaryArguments.ARG_TERM_CUR_VIEW_DATE,
                                "" + (cal.getTime ()).getTime () + "', '" +
                                HtmlConstants.FRM_SHEET + "')"));

        TableDataElement lfArrowTd = new TableDataElement (linkLeftArrow);
        lfArrowTd.width = "1%";
        trNavigation.addElement (lfArrowTd);
        // second col - current date
        text = new TextElement (currentMonthYear);

        trNavigation.addElement (new TableDataElement (text));
        // first col - arrow to the left
        rightArrow = new ImageElement (BOPathConstants.PATH_GLOBAL + "vor.gif");
        rightArrow.alt = "nächstes Monat";
        linkRightArrow =
            new LinkElement (
                rightArrow,
                    IOConstants.URL_JAVASCRIPT + "self.open ('" +
                    this.getBaseUrlGet () +
                    HttpArguments.createArg (BOArguments.ARG_FUNCTION, "" + DiaryFunctions.FCT_TERM_MON_NEXT) +
                    HttpArguments.createArg (BOArguments.ARG_OID, this.oid.toString ()) +
                    HttpArguments.createArg (DiaryArguments.ARG_TERM_CUR_VIEW_DATE,
                                            "" + (cal.getTime ()).getTime () + "', '" +
                                            HtmlConstants.FRM_SHEET + "')"));

        TableDataElement rgArrowTd = new TableDataElement (linkRightArrow);
        rgArrowTd.width = "1%";
        trNavigation.addElement (rgArrowTd);
        // add to table
        tableNavigation.addElement (trNavigation);

        // fill header table
        // create row with 4 columns
        trHeader = new  RowElement (4);
        // first col - navigation table
        TableDataElement navTd = new TableDataElement (tableNavigation);
        navTd.width = "33%";
        trHeader.addElement (navTd);
        // set second column - blank
        text = new TextElement (IE302.HCH_NBSP);
        TableDataElement spaceTd = new TableDataElement (text);
        spaceTd.width = "1%";
        trHeader.addElement (spaceTd);
        // set third column - go to month form
        // create form: and input fields


        // third col - goto date form
        FormElement form = new FormElement (
            this.getBaseUrlPost () +
                HttpArguments.createArg (BOArguments.ARG_FUNCTION, "" +
                    DiaryFunctions.FCT_TERM_MON_GOTO) +
                HttpArguments.createArg (BOArguments.ARG_OID, this.oid
                    .toString ()), UtilConstants.HTTP_POST,
            HtmlConstants.FRM_SHEET);
        form.name = "form1";
        form.classId = "navButtons";
        // create submit button
        InputElement submit =
            new InputElement ("Submitbutton", InputElement.INP_SUBMIT, 
                MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                    DiaryTokens.ML_BUTTON_GOTO, env));

        // create selection box (month: jan-dec)
        SelectElement selectMonth = new SelectElement (DiaryArguments.ARG_TERM_MONTH, false);
        selectMonth.size = 1;
        for (int i = 0; i < DiaryTokens.MONTHS.length; i++)
                                        // set all month in sel. box
        {
            // add option fields - check for selected/not selected
            // show value as selected or unselected depending on current month
            selectMonth.addOption (
                MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                    DiaryTokens.MONTHS[i], env), "" + i, i == monthNum);
        } // for
        // create input field for year
        InputElement inputYear =
            new InputElement (DiaryArguments.ARG_TERM_YEAR, InputElement.INP_TEXT, "" + yearNum);
        inputYear.onBlur = "top.iIR (document.forms[0]." + DiaryArguments.ARG_TERM_YEAR +
                        ", '0', '9999', false)";
        inputYear.size = 4;
        // add created form elements:
        form.addElement (selectMonth);
        form.addElement (inputYear);
        form.addElement (submit);
        TableDataElement formTd = new TableDataElement (form);
        formTd.width = "33%";
        // construct table elements:
        trHeader.addElement (formTd);

        // third col - button table
        trHeader.addElement (renderAdditionalNavigationButtons ());

        // add row to header table:
        tableHeader.addElement (trHeader);

        // add header table to outer table
        // create first row of outer table - with 1 column
        trOuter = new RowElement (1);
        // add header table to first row of outer table
        trOuter.addElement (new TableDataElement (tableHeader));
        tableOuter.addElement (trOuter);

        // create table row element (for terms)
        tr = new RowElement (2);

        // create inner left table grid - fill it with values:
        // this is the left hand side of the term table view
        tableLeft = this.getMonthViewInterval (cal, terms, 1, halfOfMonth);

        // create inner right table grid - fill it with values:
        // this is the right hand side of the term table view
        tableRight = this.getMonthViewInterval (cal, terms, halfOfMonth + 1, lastOfMonth);

        // add inner left/right tables to inner table
        // ... add both in one row (2 columns)
        tr = new RowElement (2);
        td = new TableDataElement (tableLeft);
        td.width = tdWidth;
        tr.addElement (td);
        td = new TableDataElement (tableRight);
        td.width = tdWidth;
        tr.addElement (td);
        table.addElement (tr);

        // add inner table to outer table
        trOuter = new RowElement (1);
        trOuter.addElement (new TableDataElement (table));
        tableOuter.addElement (trOuter);

        // add outer table to page
        page.body.addElement (tableOuter);
        page.body.addElement (newLine);

        if (this.p_isShowCommonScript)
        {
            if (this.p_tabs != null)        // tabs exist?
            {
                Tab monthTab = this.p_tabs.find ("Month");
                this.p_tabs.setActiveTab (monthTab);
            } // if
            else
            {
                // get the tab data out of the data base:
                this.p_tabs = this.performRetrieveObjectTabData ();

                if (this.p_tabs != null)        // tabs now exists?
                {
                    Tab monthTab = this.p_tabs.find ("Month");
                    this.p_tabs.setActiveTab (monthTab);
                } // if
            } // else
            
            // create the script to be executed on client:
            ScriptElement script = this.getCommonScript (true);

            // add to page:
            page.body.addElement (script);
        } // if

        // try to build constructed page
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            // TODO: handle the exception
        } // catch
    } // performMonthView


    /**************************************************************************
     * Compute the half of the month. <BR/>
     * This computations is intended to be used as part of layouting.
     * It comoutes the best balancing between the left and the right side of a
     * month view.
     *
     * @param   terms       The terms for the whole month.
     * @param   lastOfMonth The last day of the month.
     *
     * @return  The number of the day which is the last in the first half
     *          of the month.
     */
    protected int getHalfOfMonth (Vector<TermElement>[] terms, int lastOfMonth)
    {
        int halfOfMonth = 0;            // half of month
        int numOfTerms = 0;             // number of terms per day
        int numRowsLeft = 0;            // number of lines on the left side
        int numRowsRight = 0;           // number of lines on the right side
        int leftRow = 0;                // left row for balancing
        int rightRow = 0;               // right row for balancing


        // balancing:
        // loop from 0 up for left side and from lastOfMonth down for right
        // side through all days of the month:
        for (leftRow = 1, rightRow = lastOfMonth; leftRow <= rightRow;)
        {
            // check if left side needs more rows:
            if (numRowsLeft <= numRowsRight) // not enough rows on left side?
            {
                // check if there are some terms for this day:
                if ((numOfTerms = terms[leftRow++].size ()) == 0)
                {
                    // just one row for an empty day:
                    numOfTerms = 1;
                } // if
                // add the number of terms:
                numRowsLeft += numOfTerms;
            } // if not enough rows on left side
            else                        // right side needs more rows
            {
                // check if there are some terms for this day:
                if ((numOfTerms = terms[rightRow--].size ()) == 0)
                {
                    // just one row for an empty day:
                    numOfTerms = 1;
                } // if
                // add the number of terms:
                numRowsRight += numOfTerms;
            } // else right side needs more rows
        } // for

        // remember the last row on the left side:
        halfOfMonth = leftRow - 1;

        // check if the left side has at least as much rows as the right side:
        if (numRowsLeft < numRowsRight) // too much rows on rights side?
        {
            // put the last computed rows from right to left:
            numRowsRight -= numOfTerms;
            numRowsLeft += numOfTerms;
            halfOfMonth++;
        } // if too much rows on rights side
//env.write (IE302.TAG_NEWLINE + "***nachher " + loops + IE302.TAG_NEWLINE + "half: " + halfOfMonth +
//    IE302.TAG_NEWLINE + "left: " + numRowsLeft + IE302.TAG_NEWLINE + "right: " + numRowsRight +
//    IE302.TAG_NEWLINE + "diffrows: " + diffRows + IE302.TAG_NEWLINE + "lastDiffRows: " + lastDiffRows +
//    IE302.TAG_NEWLINE + "lastRowSwitched: " + lastRowSwitched +
//    IE302.TAG_NEWLINE + "lastBeforeLastRowSwitched: " + lastBeforeLastRowSwitched + IE302.TAG_NEWLINE);

        // return the result:
        return halfOfMonth;
    } // getHalfOfMonth


    /**************************************************************************
     * Creates some of the days of a month as table for using in a month view.
     * <BR/>
     *
     * @param   cal         A calendar representing the actual month.
     * @param   terms       The terms of the month.
     * @param   firstDay    The number of the first generated day.
     * @param   lastDay     The number of the last generated day.
     *
     * @return  The table containing the requested days.
     */
    protected TableElement getMonthViewInterval (GregorianCalendar cal,
                                                 Vector<TermElement>[] terms,
                                                 int firstDay, int lastDay)
    {
        GregorianCalendar tempCal;      // temporary calendar
        int firstWeekDay;               // first weekday of year/month
        int weekDayCounter = 0;         // define weekday counter - needed to
                                        // display current weekday (mon, tue,
                                        // ...) for each day of month in table
                                        // view
        int termCounter = 0;            // term counter - if more than 1 term
                                        // is displayed on 1 day
        TermElement t;                  // current term element
        Enumeration<TermElement> termsE; // Enumeration object for terms
        int counter = 0;                // day counter
        NewLineElement newLine = new NewLineElement (); // new-line-element (<BR>)
        TextElement text = new TextElement (""); // text element
        GroupElement termGroup;         // the term layout container
        // inner table:
        TableElement table = new TableElement (2);   // table with 2 columns
        RowElement tableRow;
        TableDataElement tableCell = new TableDataElement (text);
        String[] classIds =
        {
            DiaryConstants.CLASS_COLDAY,
            DiaryConstants.CLASS_COLTERMIN,
        }; // classIds


        // create temporary calendar:
        tempCal = (GregorianCalendar) cal.clone ();
        // set first of month:
        tempCal.set (Calendar.DAY_OF_MONTH, firstDay);

        // get first weekday of year/month:
        firstWeekDay = tempCal.get (Calendar.DAY_OF_WEEK);

        // initialize the table data:
        table.border = 0;
        table.classIds = classIds;
        table.ruletype = Terminplan_01.RULETYPE_ALL;
        table.frametypes = Terminplan_01.FRAMETYPE_VOID;
        table.width = HtmlConstants.TAV_FULLWIDTH;

        // initialize weekday counter:
        weekDayCounter = firstWeekDay;

        // create inner table grid - fill it with values:
        // outer loop, rows --> one for each day in month
        // this is the one side of the term table view
        for (int row = firstDay; row <= lastDay;
             row++, weekDayCounter++, counter++)
        {
            // check if week day counter exceeds its limit:
            if (weekDayCounter == 8)
            {
                weekDayCounter = 1;     // yes - reset
            } // if

            // create row element for actual day:
            tableRow = new RowElement (2);

            // display name of weekday:
            text = new TextElement (row + "," + IE302.HCH_NBSP +
                MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                    DiaryTokens.DV_WEEKDAYS_SHORT[weekDayCounter], env));
            tableCell = new TableDataElement (text);
            tableCell.classId = DiaryConstants.CLASS_COLDAY;

            // create first column (day)
            // set display class according to day/holiday
            if (weekDayCounter == 7 || weekDayCounter == 1)
                                        // Saturday or Sunday?
            {
                tableCell.classId += " " + DiaryConstants.CLASS_HOLIDAY;
            } // if
            tableCell.width = "10%";

            // add table data element to row:
            tableRow.addElement (tableCell);

            // create second column (term)
            // create blank text element:
            text = new TextElement (IE302.HCH_NBSP);
            // create table data element with specific text:
            tableCell = new TableDataElement (text);
            tableCell.classId = DiaryConstants.CLASS_COLTERMIN;

            // reset term counter:
            termCounter = 0;

            // create new GroupElement
            termGroup = new GroupElement ();

            // create enumeration for act. day vector (of terms) and
            // loop through all terms of act. day:
            for (termsE = terms[row].elements (); termsE.hasMoreElements ();)
            {
                // get next term element
                t = termsE.nextElement ();

                // increase termCounter and get the month view of the term:
                termGroup.addElement (this.getMonthViewTerm (t, ++termCounter));

                // check if tehre is another term for this day:
                if (termCounter < terms[row].size ())
                {
                    // add a new line after the current term:
                    termGroup.addElement (newLine);
                } // if
            } // for

            // create table data element with specific text:
            tableCell = new TableDataElement (termGroup);
            if (termGroup.group == null)
            {
                tableCell = new TableDataElement (new TextElement (IE302.HCH_NBSP));
            } // if
            tableCell.width = "90%";

            tableCell.classId = BOListConstants.LST_CLASSROWS[counter % BOListConstants.LST_CLASSROWS.length];

            // add table data element to row
            tableRow.addElement (tableCell);

            // add row to table:
            table.addElement (tableRow);
        } // for

        // return the constructed table:
        return table;
    } // getMonthViewInterval


    /**************************************************************************
     * Get the month view for one term. <BR/>
     *
     * @param   term    The term for which to create the month view.
     * @param   termNum The number of the term within the list.
     *
     * @return  The layout element containing the month view of the term.
     */
    protected GroupElement getMonthViewTerm (TermElement term, int termNum)
    {
        GroupElement termGroup = new GroupElement ();
                                        // group to view term in inner table
        String url = new String ("");   // URL string
        // text elements to view term in group:
        TextElement termTime = new TextElement ("");
        TextElement termName = new TextElement ("");
        TextElement termPlace = new TextElement ("");
        LinkElement linkName;           // link elements for terms
        SpanElement span;               // a span element
        GroupElement linkGroup;         // group with link for term
        int currentColorIndex;          // the colour for the current term
        // classes for browser for the different terms on one day
        String[] termClasses = new String[]
        {
            DiaryConstants.CLASS_TERMNAME1,
            DiaryConstants.CLASS_TERMNAME2,
            DiaryConstants.CLASS_TERMNAME3,
            DiaryConstants.CLASS_TERMNAME4,
        }; // termClasses


        // create new GroupElement:
        termGroup = new GroupElement ();

        // create text elements:
        termTime =
            new TextElement (IE302.HCH_NBSP + DateTimeHelpers.timeToString (term.startDate) +
                Terminplan_01.HEADERSEP + DateTimeHelpers.timeToString (term.endDate) + IE302.HCH_NBSP);

        // Change font (fontcolor) between following terms if there are more then one term on one day
        currentColorIndex = (termNum - 1) % termClasses.length;
        termName = new TextElement (term.name);

//debug (" currentColor: " + fontsNameColors[currentColorIndex].color + " termName: " + t.name);

        linkGroup = new GroupElement ();
        if (term.oid != null)
        {
            // build url:
            url = IOHelpers.getShowObjectJavaScriptUrl (term.oid.toString ());
            linkName = new LinkElement (termName, url);
            linkGroup.addElement (linkName);
        } // if
        else
        {
            linkGroup.addElement (termName);
        } // else

        termPlace = new TextElement (" " + term.place);

        // build group element:
        // add new line element if more than 1 term
        // display link to term
        span = new SpanElement ();
        span.addElement (linkGroup);
        span.classId = termClasses [currentColorIndex];
        termGroup.addElement (span);

        // display time of term:
        // check if current TermElement is Part of a long Term
        // (= Term over more then one day)
        if (term.numDays <= 1)     // current term is finished on one day?
        {
            span = new SpanElement ();
            span.addElement (termTime);
            span.classId = DiaryConstants.CLASS_TERMTIME;
            termGroup.addElement (span);
        } // if current term is finished on one day

        // display place:
        span = new SpanElement ();
        span.addElement (termPlace);
        span.classId = DiaryConstants.CLASS_TERMPLACE;
        termGroup.addElement (span);

        // return the new layout element:
        return termGroup;
    } // getMonthViewTerm


    /**************************************************************************
     * Adds one day to currently viewed day and shows day view. <BR/>
     *
     * @param   dateString  The current day.
     */
    public void viewNextDay (String dateString)
    {
        // create a GregorianCalendar with default timezone and locale
        GregorianCalendar cal = new GregorianCalendar ();

        try
        {
            // set current view date - convert from string: date in millis
            this.p_curViewDate = new Date (Long.parseLong (dateString));
        } // try
        catch (NumberFormatException e)
        {
            // new Date
            this.p_curViewDate = new Date ();
        } // catch NumberFormatException

        // calendar fills missing data
        // set view date in calendar
        cal.setTime (this.p_curViewDate);
        // now add one day
        cal.add (Calendar.DATE, 1);

        // now get date back out of calendar - set in view date
        this.p_curViewDate = cal.getTime ();

        // call view day method
        this.dayView ();
    } // viewNextDay


    /**************************************************************************
     * Subtracts one day of currently viewed day and shows day view. <BR/>
     *
     * @param   dateString  The current day.
     */
    public void viewPrevDay (String dateString)
    {
        // create a GregorianCalendar with default timezone and locale

        GregorianCalendar cal = new GregorianCalendar ();

        try
        {
            // set current view date - convert from string: date in millis
            this.p_curViewDate = new Date (Long.parseLong (dateString));
        } // try
        catch (NumberFormatException e)
        {
            // new Date
            this.p_curViewDate = new Date ();
        } // catch NumberFormatException

        // calendar fills missing data
        // set view date in calendar
        cal.setTime (this.p_curViewDate);
        // now add one day
        cal.add (Calendar.DATE, -1);

        // now get date back out of calendar - set in view date
        this.p_curViewDate = cal.getTime ();

        // call view day method
        this.dayView ();
    } // viewPrevDay



    /**************************************************************************
     * Adds one month to currently viewed month and shows month view. <BR/>
     *
     * @param   dateString  The current month.
     */
    public void viewNextMonth (String dateString)
    {
        // create a GregorianCalendar with default timezone and locale
        GregorianCalendar cal = new GregorianCalendar ();

        try
        {
            // set current view date - convert from string: date in millis
            this.p_curViewDate = new Date (Long.parseLong (dateString));
        } // try
        catch (NumberFormatException e)
        {
            // new Date
            this.p_curViewDate = new Date ();
        } // catch NumberFormatException

        // calendar fills missing data
        // set view date in calendar
        cal.setTime (this.p_curViewDate);

        // now add one day
        cal.add (Calendar.MONTH, 1);
//        cal.set (cal.MONTH, cal.get (cal.MONTH) + 1);

        // now get date back out of calendar - set in view date
        this.p_curViewDate = cal.getTime ();

        // call view month method
        this.monthView ();
    } // viewNextMonth


    /**************************************************************************
     * Subtracts one month of currently viewed month and shows month view. <BR/>
     *
     * @param   dateString  The current month.
     */
    public void viewPrevMonth (String dateString)
    {
        // create a GregorianCalendar with default timezone and locale
        GregorianCalendar cal = new GregorianCalendar ();

        try
        {
            // set current view date - convert from string: date in millis
            this.p_curViewDate = new Date (Long.parseLong (dateString));
        } // try
        catch (NumberFormatException e)
        {
            // new Date
            this.p_curViewDate = new Date ();
        } // catch NumberFormatException

        // calendar fills missing data
        // set view date in calendar
        cal.setTime (this.p_curViewDate);

        // subtract one month
        cal.add (Calendar.MONTH, -1);

        // now get date back out of calendar - set in view date
        this.p_curViewDate = cal.getTime ();

        // call view day method
        this.monthView ();
    } // viewPrevDay


    /**************************************************************************
     * Jumps to month view of given month/year. <BR/>
     *
     * @param   month   The selected month.
     * @param   year    The selected year.
     */
    public void gotoMonth (String month, String year)
    {
        // create a GregorianCalendar with default timezone and locale
        GregorianCalendar cal = new GregorianCalendar ();

        // create given date
        cal.set (new Integer (year).intValue (),
            new Integer (month).intValue (), 15);

        // now get date back out of calendar - set in view date
        this.p_curViewDate = cal.getTime ();

        // call view month method
        this.monthView ();
    } // gotoMonth


    /**************************************************************************
     * Jumps to day view of given date. <BR/>
     *
     * @param   dateString  The selected day.
     */
    public void gotoDay (String dateString)
    {
        // set timezone of date format - this is necessary due
        // to a bug in some JDK versions
        this.p_dateFormatDefault.setTimeZone (TimeZone.getDefault ());

        // get date out of string - set to current viewing date
        try
        {
            this.p_curViewDate = this.p_dateFormatDefault.parse (dateString);
        } // try
        catch (ParseException e)
        {
            this.p_curViewDate = new Date ();
        } // catch

        // call view day method
        this.dayView ();
    } // gotoDay


    /**************************************************************************
     * Calculates row span (in a table) of 1 specific term for day view. <BR/>
     * Row span is not always = hour difference, morning/evening is only
     * 1 row!.
     *
     * @param   t       Given term.
     *
     * @return  Calculated row span.
     */
    private int calcRowSpan (TermElement t)
    {
        int rowSpan = 1;
        GregorianCalendar calStart = new GregorianCalendar ();
        GregorianCalendar calEnd = new GregorianCalendar ();
        int startHour;                  // beginning hour of term
        int endHour;                    // ending hour of term
        int morningOffset = 0;          // offset for morning
        int eveningOffset = 0;          // offset for evening

        // set times
        calStart.setTime (t.startDate);
        calEnd.setTime (t.endDate);

        startHour = calStart.get (Calendar.HOUR_OF_DAY);
        endHour = calEnd.get (Calendar.HOUR_OF_DAY);
        if (calEnd.get (Calendar.MINUTE) > 0)
        {
            endHour++;
        } // if

        // start and end are in the morning - term is viewed in 1 row
        if (endHour < DiaryConstants.DV_TIME_VIEW_START)
        {
            return 1;
        } // if
        // start and end are in the evening - term is viewed in 1 row
        if (startHour > DiaryConstants.DV_TIME_VIEW_END)
        {
            return 1;
        } // if

        if (startHour < DiaryConstants.DV_TIME_VIEW_START)
        {
            startHour = DiaryConstants.DV_TIME_VIEW_START;
            morningOffset = 1;
        } // if

        if (endHour > DiaryConstants.DV_TIME_VIEW_END)
        {
            endHour = DiaryConstants.DV_TIME_VIEW_END;
            eveningOffset = 1;
        } // if

        // calculate difference:
        rowSpan = endHour - startHour + morningOffset + eveningOffset;

        // now we have the theoretical rowspan
        // - but what about evening and morning rows?

        // return calculated row span:
        return rowSpan;
    } // calcRowSpan


    /**************************************************************************
     * Show the the Container, i.e. its content. <BR/>
     * This method calls <A HREF="#showContent">showContent</A> to show the
     * content of the container. Thus the properties
     * <A HREF="#orderBy">orderBy</A> and <A HREF="#orderHow">orderHow</A> must
     * be set before the call of this method. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void show (int representationForm)
    {
        // show the content of this container:
        if (this.lastView == Terminplan_01.V_DAY)
        {
            this.dayView ();
        } // if
        else if (this.lastView == Terminplan_01.V_MONTH)
        {
            this.monthView ();
        } // else if
        else if (this.lastView == Terminplan_01.V_CONTENT)
        {
            this.showContent (representationForm);
        } // else if
        else
        {
            this.monthView ();
        } // else
    } // show


    /**************************************************************************
     * Show the content of the Container, i.e. its elements. <BR/>
     * The objects are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showContent (int representationForm)
    {
        this.lastView = Terminplan_01.V_CONTENT;

        super.showContent (representationForm);
    } // showContent


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * container's content view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setContentButtons ()
    {
        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_NEW,
            Buttons.BTN_PASTE,
            Buttons.BTN_REFERENCE,
            Buttons.BTN_SEARCH,
            Buttons.BTN_DISTRIBUTE,
//          Buttons.BTN_HELP,
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_LIST_COPY,
            Buttons.BTN_LIST_CUT,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons


    /**************************************************************************
     * Is the object type allowed in workflows? <BR/>
     * This method shall be overwritten in subclasses.
     *
     * @return  <CODE>true</CODE> if the object type is allowed in workflows,
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean isWfAllowed ()
    {
        return true;
    } // isWfAllowed


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard container. <BR/>
     */
    protected void setHeadingsAndOrderings ()
    {
        // set super attribute
        this.headings = MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE, 
            DiaryConstants.LST_HEADINGS_TERMINPLAN, env);
        // set super attribute
        this.orderings = DiaryConstants.LST_ORDERINGS_TERMINPLAN;
        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings

    
    /**************************************************************************
     * Creates a special list descripition with the additional buttons for
     * the navigation when no tabs are available to navigate. <BR/>
     *
     * @return  Group element containing the graphical representation of the
     *          description.
     */
    protected GroupElement createDescription ()
    {
        TableElement table = null;      // table containing the description
        RowElement tr;                  // actual table row
        TableDataElement td;            // actual element of a row
        GroupElement group = new GroupElement ();

        table = new TableElement (2); // create table
        table.width = HtmlConstants.TAV_FULLWIDTH;       // set width of table to full frame size
        table.border = 0;
        table.classId = CssConstants.CLASS_LISTDESCRIPTION;
        tr = new RowElement (1);    // create table row
        tr.classId = "listdescriptionWithNavButtons";

        // description exists?
        if (this.description != null &&
            this.description.length () > 0)
        {
            td = new TableDataElement (IOHelpers.getTextField (this.description));
        } // if
        else 
        {
            td = new TableDataElement ();
        } // else
        
        td.classId = CssConstants.CLASS_LISTDESCRIPTION;
        td.width = "67%";
        
        // add text to table and add table to body of document:
        tr.addElement (td);
        // add additional navigation buttons when no tabs are available
        tr.addElement (renderAdditionalNavigationButtons());        
        table.addElement (tr);
        group.addElement (table);

        return group;                   // return the constructed group element
    } // createDescription

    
    /**************************************************************************
     * Render additional buttons for the views when no tabs are available. <BR/>
     * 
     * @return  TableDataElement    table data element with the buttons
     */
    protected TableDataElement renderAdditionalNavigationButtons()
    {
        RowElement trButton;
        TableElement tableButton;       // table

        // 2.2 button table - displays three buttons to navigate without tabs
        // header table
        tableButton = new TableElement (3);   // table: 3 columns
        tableButton.border = 0;
        tableButton.ruletype = "NONE";
        tableButton.frametypes = "VOID";
        tableButton.width = HtmlConstants.TAV_FULLWIDTH;

        // add an additional button for the content view
        FormElement formContent = new FormElement (
            this.getBaseUrlPost () +
                HttpArguments.createArg (BOArguments.ARG_FUNCTION, "42") +
                HttpArguments.createArg (BOArguments.ARG_OID, this.oid
                    .toString ()), UtilConstants.HTTP_POST,
            HtmlConstants.FRM_SHEET);
        formContent.name = "form1";

        // create submit button
        InputElement submitContentView =
            new InputElement ("contentView", InputElement.INP_SUBMIT, 
                MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                    DiaryTokens.ML_BUTTON_CONTENT, env));
        submitContentView.src = this.sess.activeLayout.path + BOPathConstants.PATH_OBJECTICONS + "Terminplan.gif";
        submitContentView.title = MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                                      DiaryTokens.ML_VIEW_LIST, env);
        // add created form elements
        formContent.addElement (submitContentView);
        formContent.classId = "navButtons";

        TableDataElement contentTd = new TableDataElement (formContent);
        contentTd.width = "1%";
        // add content element to row
        trButton = new RowElement (3);
        trButton.addElement (contentTd);

        // add an additional button for the day view
        FormElement formDay = new FormElement (
            this.getBaseUrlPost () +
                HttpArguments.createArg (BOArguments.ARG_FUNCTION, "" +
                    DiaryFunctions.FCT_TERM_DAY_VIEW) +
                HttpArguments.createArg (BOArguments.ARG_OID, this.oid
                    .toString ()), UtilConstants.HTTP_POST,
            HtmlConstants.FRM_SHEET);
        formDay.name = "form2";
        formDay.classId = "navButtons";
        
        // create submit button
        InputElement submitDayView =
            new InputElement ("dayView", InputElement.INP_SUBMIT, 
                MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                    DiaryTokens.ML_BUTTON_DAY, env));
        submitDayView.src = this.sess.activeLayout.path + BOPathConstants.PATH_OBJECTICONS + "Termin.gif";
        submitDayView.title = MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                                  DiaryTokens.ML_VIEW_DAY, env);
        // add created form elements
        formDay.addElement (submitDayView);
        TableDataElement dayTd = new TableDataElement (formDay);
        dayTd.width = "1%";
        // add day element to row
        trButton.addElement (dayTd);        

        // add an additional button for the month view
        FormElement formMonth = new FormElement (
            this.getBaseUrlPost () +
                HttpArguments.createArg (BOArguments.ARG_FUNCTION, "" +
                    DiaryFunctions.FCT_TERM_MON_VIEW) +
                HttpArguments.createArg (BOArguments.ARG_OID, this.oid
                    .toString ()), UtilConstants.HTTP_POST,
            HtmlConstants.FRM_SHEET);
        formMonth.name = "form3";
        formMonth.classId = "navButtons";
        
        // create submit button
        InputElement submitMonthView =
            new InputElement ("monthView", InputElement.INP_SUBMIT, 
                MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                    DiaryTokens.ML_BUTTON_MONTH, env));
        submitMonthView.src = this.sess.activeLayout.path + BOPathConstants.PATH_OBJECTICONS + "Terminplan.gif";
        submitMonthView.title = MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                                    DiaryTokens.ML_VIEW_MONTH, env);
        // add created form elements
        formMonth.addElement (submitMonthView);
        TableDataElement monthTd = new TableDataElement (formMonth);
        monthTd.width = "1%";
        // add month element to row
        trButton.addElement (monthTd);
        // add to table
        tableButton.addElement (trButton);
        tableButton.width = "10%";
        TableDataElement buttonTd = new TableDataElement (tableButton);
        buttonTd.width = "33%";
        buttonTd.alignment = "center";

        return buttonTd;
    } // renderAdditionalNavigationButtons
    
} // class Terminplan_01

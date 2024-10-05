/*
 * Class: DiaryConstants.java
 */

// package:
package m2.diary;

// imports:
import ibs.bo.BOTokens;
import ibs.ml.MultilingualTextProvider;

import m2.diary.DiaryTokens;


/******************************************************************************
 * Constants for the diary component. <BR/>
 * This abstract class contains all constants which are necessary to deal with
 * the classes delivered within this package.<P>
 *
 * @version     $Id: DiaryConstants.java,v 1.8 2010/04/07 13:37:12 rburgermann Exp $
 *
 * @author      Horst Pichler (HP), 982904
 ******************************************************************************
 */
public abstract class DiaryConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DiaryConstants.java,v 1.8 2010/04/07 13:37:12 rburgermann Exp $";


    /**
     * viewing area of time plan (day view = DV): starting point. <BR/>
     */
    public static final int DV_TIME_VIEW_START = 8;
    /**
     * viewing area of time plan (day view = DV): end point. <BR/>
     */
    public static final int DV_TIME_VIEW_END = 18;

    /**
     * Day view table color: border. <BR/>
     */
    public static final String DV_COLOR_BORDER = new String ("silver");
    /**
     * Day view table color: background. <BR/>
     */
    public static final String DV_COLOR_TABLE_BG = new String ("white");
    /**
     * Day view table color: term. <BR/>
     */
    public static final String DV_COLOR_TERM = new String ("khaki");


    /**
     * Month view table color: border. <BR/>
     */
    public static final String MV_COLOR_BORDER = DiaryConstants.DV_COLOR_BORDER;
    /**
     * Month view table color: background. <BR/>
     */
    public static final String MV_COLOR_TABLE_BG =
        DiaryConstants.DV_COLOR_TABLE_BG;
    /**
     * Month view table color: row1 (odd). <BR/>
     */
    public static final String MV_COLOR_ROW1 = DiaryConstants.MV_COLOR_TABLE_BG;
    /**
     * Month view table color: row2 (even). <BR/>
     */
    public static final String MV_COLOR_ROW2 = new String ("beige");

    /**
     * File to be displayed for navigation in month view. <BR/>
     */
    public static final String FILE_SHEETNAVIGATION_MONTHVIEW =
        new String ("sheetnavigation_month.htm");
    /**
     * File to be displayed for navigation in day view. <BR/>
     */
    public static final String FILE_SHEETNAVIGATION_DAYVIEW =
        new String ("sheetnavigation_day.htm");

    /**
     * Frame which contains overlapping list. <BR/>
     */
    public static final String FRM_TERM_OVERLAP_LIST = new String ("overlaplist");
    /**
     * Frame to be displayed with form if an overlapping occurs. <BR/>
     */
    public static final String FRM_TERM_OVERLAP_FORM = new String ("overlapform");

    /**
     * Frame which contains the list of participants. <BR/>
     */
    public static final String FRM_TERM_PARTICIPANTS_LIST = new String ("participantslist");
    /**
     * Frame which contains the form to add new participants. <BR/>
     */
    public static final String FRM_TERM_PARTICIPANTS_FORM = new String ("participantsform");

    /**
     * List column: name. <BR/>
     */
    private static final String COL_NAME = "name";
    /**
     * List column: start date. <BR/>
     */
    private static final String COL_STARTDATE = "startDate";
    /**
     * List column: end date. <BR/>
     */
    private static final String COL_ENDDATE = "endDate";
    /**
     * List column: location. <BR/>
     */
    private static final String COL_LOCATION = "place";
    /**
     * List column: owner. <BR/>
     */
    private static final String COL_OWNER = "owner";

    /**
     * Headings for overlapping list. <BR/>
     */
    public static final String[] LST_HEADINGS_OVERLAP =
    {
        BOTokens.ML_NAME,
        DiaryTokens.ML_TERM_START_DATE,
        DiaryTokens.ML_TERM_END_DATE,
        DiaryTokens.ML_TERM_PLACE,
    }; // LST_HEADINGS_OVERLAP

    /**
     * Orderings for overlapping list. <BR/>
     */
    public static final String[] LST_ORDERINGS_OVERLAP =
    {
        DiaryConstants.COL_NAME,
        DiaryConstants.COL_STARTDATE,
        DiaryConstants.COL_ENDDATE,
        DiaryConstants.COL_LOCATION,
    }; // LST_HEADINGS_OVERLAP

    /**
     * Headings for particpants list. <BR/>
     */
    public static final String[] LST_HEADINGS_PARTICIPANTS =
    {
        BOTokens.ML_NAME,
        DiaryTokens.ML_TERM_ANNOUNCED,
        DiaryTokens.ML_TERM_ANN_NAME,
    }; // LST_HEADINGS_PARTICIPANTS

    /**
     * Orderings for participants list. <BR/>
     */
    public static final String[] LST_ORDERINGS_PARTICIPANTS =
    {
        DiaryConstants.COL_NAME,
        "announcementdate",
        "announcerName",
    }; // AppConstants.LST_ORDERINGS_PARTICIPANTS

    /**
     * Headings for diary (list view). <BR/>
     */
    public static final String[] LST_HEADINGS_TERMINPLAN =
    {
        BOTokens.ML_NAME,
        DiaryTokens.ML_TERM_START_DATE,
        DiaryTokens.ML_TERM_END_DATE,
        DiaryTokens.ML_TERM_PLACE,
        BOTokens.ML_OWNER,
    }; // LST_HEADINGS_OVERLAP

    /**
     * Orderings for diary (list view). <BR/>
     */
    public static final String[] LST_ORDERINGS_TERMINPLAN =
    {
        DiaryConstants.COL_NAME,
        DiaryConstants.COL_STARTDATE,
        DiaryConstants.COL_ENDDATE,
        DiaryConstants.COL_LOCATION,
        DiaryConstants.COL_OWNER,
    }; // LST_HEADINGS_OVERLAP

    /**
     * . <BR/>
     */
    public static final String CLASS_MONTH = "month";

    /**
     * . <BR/>
     */
    public static final String CLASS_CALBODY = "calbody";
    /**
     * . <BR/>
     */
    public static final String CLASS_COLHOUR = "hours";

    /**
     * . <BR/>
     */
    public static final String CLASS_COLTERMIN = "date";

    /**
     * . <BR/>
     */
    public static final String CLASS_COLDAY = "days";

    /**
     * . <BR/>
     */
    public static final String CLASS_TERMTIME = "time";

    /**
     * . <BR/>
     */
    public static final String CLASS_TERMPLACE = DiaryConstants.COL_LOCATION;

    /**
     * . <BR/>
     */
    public static final String CLASS_TERMPART = "participants";

    /**
     * . <BR/>
     */
    public static final String CLASS_TERMNAME = DiaryConstants.COL_NAME;



    // The different classes for the terms on one day.
    /**
     * Class 1 for the terms on one day. <BR/>
     */
    public static final String CLASS_TERMNAME1 = "termname1";
    /**
     * Class 2 for the terms on one day. <BR/>
     */
    public static final String CLASS_TERMNAME2 = "termname2";
    /**
     * Class 3 for the terms on one day. <BR/>
     */
    public static final String CLASS_TERMNAME3 = "termname3";
    /**
     * Class 4 for the terms on one day. <BR/>
     */
    public static final String CLASS_TERMNAME4 = "termname4";

    /**
     * . <BR/>
     */
    public static final String CLASS_TERMIN = "term";
    /**
     * . <BR/>
     */
    public static final String CLASS_DAY = "day";
    /**
     * . <BR/>
     */
    public static final String CLASS_CALHEADER = "calheader";

    /**
     * . <BR/>
     */
    public static final String CLASS_HOLIDAY = "holiday";

} // class DiaryConstants

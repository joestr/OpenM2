/*
 * Class: DiaryTokens.java
 */

// package:
package m2.diary;

// imports:


/******************************************************************************
 * Tokens for m2.diary business objects. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the business objects delivered within this package.
 *
 * @version     $Id: DiaryTokens.java,v 1.10 2012/03/02 11:01:55 rburgermann Exp $
 *
 * @author      Horst Pichler (HP), 980707
 ******************************************************************************
 */
public abstract class DiaryTokens extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DiaryTokens.java,v 1.10 2012/03/02 11:01:55 rburgermann Exp $";


    /**
     * Name of bundle where the tokens included. <BR/>
     */
    public static String TOK_BUNDLE = "m2_m2diary_tokens";

    /**
     * Token: start date of term in URL. <BR/>
     */
    public static String ML_TERM_START_DATE   = "ML_TERM_START_DATE";

    /**
     * Token: end date of term in URL. <BR/>
     */
    public static String ML_TERM_END_DATE     = "ML_TERM_END_DATE";

    /**
     * Token: place of term in URL. <BR/>
     */
    public static String ML_TERM_PLACE   = "ML_TERM_PLACE";

    /**
     * Token: participants of term in URL. <BR/>
     */
    public static String ML_TERM_PARTICIPANTS = "ML_TERM_PARTICIPANTS";

    /**
     * Token: show-participants of term in URL. <BR/>
     */
    public static String ML_TERM_SHOW_PARTICIPANTS = "ML_TERM_SHOW_PARTICIPANTS";

    /**
     * Token: max. number of participants of term in URL. <BR/>
     */
    public static String ML_TERM_MAX_PARTICIPANTS = "ML_TERM_MAX_PARTICIPANTS";

    /**
     * Token: cur. number of participants of term in URL. <BR/>
     */
    public static String ML_TERM_CUR_PARTICIPANTS = "ML_TERM_CUR_PARTICIPANTS";

    /**
     * Token: Anmeldedatum. <BR/>
     */
    public static String ML_TERM_ANNOUNCED = "ML_TERM_ANNOUNCED";

    /**
     * Token: beginn date. <BR/>
     */
    public static String ML_TERM_START_DATERANGE = "ML_TERM_START_DATERANGE";

    /**
     * Token: end date. <BR/>
     */
    public static String ML_TERM_END_DATERANGE = "ML_TERM_END_DATERANGE";

    /**
     * Token: deadline of announcements. <BR/>
     */
    public static String ML_TERM_DEADLINE = "ML_TERM_DEADLINE";

    /**
     * Token: announcers name. <BR/>
     */
    public static String ML_TERM_ANN_NAME = "ML_TERM_ANN_NAME";
    /**
     * Token: overlaps. <BR/>
     */
    public static String ML_OVERLAPS = "ML_OVERLAPS";

    /**
     * Token: attachment. <BR/>
     */
    public static String ML_TERM_ATTACHMENT = "ML_TERM_ATTACHMENT";

    /**
     * Token: monthly overview. <BR/>
     */
    public static String ML_TERM_MONTHLYOVERVIEW = "ML_TERM_MONTHLYOVERVIEW";

    /**
     * Token: daily overview. <BR/>
     */
    public static String ML_TERM_DAILYOVERVIEW = "ML_TERM_DAILYOVERVIEW";

    /**
     * diary name. <BR/>
     */
    public static String ML_TERM_DIARY = "ML_TERM_DIARY";

    /**
     * start date. <BR/>
     */
    public static String ML_TERM_START = "ML_TERM_START";
    /**
     * end date. <BR/>
     */
    public static String ML_TERM_END = "ML_TERM_END";

    /**
     * Are there participants allowed? <BR/>
     */
    public static String ML_TERM_PARTICIPANT = "ML_TERM_PARTICIPANT";

    /**
     * Are there attachments allowed? <BR/>
     */
    public static String ML_TERM_ATTACHMENTS = "ML_TERM_ATTACHMENTS";

    // tabs
    // day view

    // views
    /**
     * day view. <BR/>
     */
    public static String ML_VIEW_DAY = "ML_VIEW_DAY";
    /**
     * week view. <BR/>
     */
    public static String ML_VIEW_WEEK = "ML_VIEW_WEEK";
    /**
     * month view. <BR/>
     */
    public static String ML_VIEW_MONTH = "ML_VIEW_MONTH";
    /**
     * quarter of year view. <BR/>
     */
    public static String ML_VIEW_QUARTAL = "ML_VIEW_QUARTAL";
    /**
     * list view. <BR/>
     */
    public static String ML_VIEW_LIST = "ML_VIEW_LIST";

    /**
     * button goto. <BR/>
     */
    public static String ML_BUTTON_GOTO = "ML_BUTTON_GOTO";


    ///////////////////////////////////////////////////////////////////////////
    //
    // constants for day view
    //

    /**
     * calendar week. <BR/>
     */
    public static String ML_DV_CALENDARWEEK = "ML_DV_CALENDARWEEK";
    /**
     * calendar week short. <BR/>
     */
    public static String ML_DV_CALENDARWEEK_SHORT = "ML_DV_CALENDARWEEK_SHORT";

    /**
     * text for timespan before viewing area (morning). <BR/>
     */
    public static String ML_DV_TIME_MORNING = "ML_DV_TIME_MORNING";

    /**
     * Daily hours: 00. <BR/>
     */
    public static String ML_DV_TIME_HOUR00 = "ML_DV_TIME_HOUR00";
    /**
     * Daily hours: 01. <BR/>
     */
    public static String ML_DV_TIME_HOUR01 = "ML_DV_TIME_HOUR01";
    /**
     * Daily hours: 02. <BR/>
     */
    public static String ML_DV_TIME_HOUR02 = "ML_DV_TIME_HOUR02";
    /**
     * Daily hours: 03. <BR/>
     */
    public static String ML_DV_TIME_HOUR03 = "ML_DV_TIME_HOUR03";
    /**
     * Daily hours: 04. <BR/>
     */
    public static String ML_DV_TIME_HOUR04 = "ML_DV_TIME_HOUR04";
    /**
     * Daily hours: 05. <BR/>
     */
    public static String ML_DV_TIME_HOUR05 = "ML_DV_TIME_HOUR05";
    /**
     * Daily hours: 06. <BR/>
     */
    public static String ML_DV_TIME_HOUR06 = "ML_DV_TIME_HOUR06";
    /**
     * Daily hours: 07. <BR/>
     */
    public static String ML_DV_TIME_HOUR07 = "ML_DV_TIME_HOUR07";
    /**
     * Daily hours: 08. <BR/>
     */
    public static String ML_DV_TIME_HOUR08 = "ML_DV_TIME_HOUR08";
    /**
     * Daily hours: 09. <BR/>
     */
    public static String ML_DV_TIME_HOUR09 = "ML_DV_TIME_HOUR09";
    /**
     * Daily hours: 10. <BR/>
     */
    public static String ML_DV_TIME_HOUR10 = "ML_DV_TIME_HOUR10";
    /**
     * Daily hours: 11. <BR/>
     */
    public static String ML_DV_TIME_HOUR11 = "ML_DV_TIME_HOUR11";
    /**
     * Daily hours: 12. <BR/>
     */
    public static String ML_DV_TIME_HOUR12 = "ML_DV_TIME_HOUR12";
    /**
     * Daily hours: 13. <BR/>
     */
    public static String ML_DV_TIME_HOUR13 = "ML_DV_TIME_HOUR13";
    /**
     * Daily hours: 14. <BR/>
     */
    public static String ML_DV_TIME_HOUR14 = "ML_DV_TIME_HOUR14";
    /**
     * Daily hours: 15. <BR/>
     */
    public static String ML_DV_TIME_HOUR15 = "ML_DV_TIME_HOUR15";
    /**
     * Daily hours: 16. <BR/>
     */
    public static String ML_DV_TIME_HOUR16 = "ML_DV_TIME_HOUR16";
    /**
     * Daily hours: 17. <BR/>
     */
    public static String ML_DV_TIME_HOUR17 = "ML_DV_TIME_HOUR17";
    /**
     * Daily hours: 18. <BR/>
     */
    public static String ML_DV_TIME_HOUR18 = "ML_DV_TIME_HOUR18";
    /**
     * Daily hours: 19. <BR/>
     */
    public static String ML_DV_TIME_HOUR19 = "ML_DV_TIME_HOUR19";
    /**
     * Daily hours: 20. <BR/>
     */
    public static String ML_DV_TIME_HOUR20 = "ML_DV_TIME_HOUR20";
    /**
     * Daily hours: 21. <BR/>
     */
    public static String ML_DV_TIME_HOUR21 = "ML_DV_TIME_HOUR21";
    /**
     * Daily hours: 22. <BR/>
     */
    public static String ML_DV_TIME_HOUR22 = "ML_DV_TIME_HOUR22";
    /**
     * Daily hours: 23. <BR/>
     */
    public static String ML_DV_TIME_HOUR23 = "ML_DV_TIME_HOUR23";
    /**
     * Daily hours: 24. <BR/>
     */
    public static String ML_DV_TIME_HOUR24 = "ML_DV_TIME_HOUR24";

    /**
     * List of all time hours. <BR/>
     */
    public static String[] DV_TIME_HOURS =
    {
        DiaryTokens.ML_DV_TIME_HOUR00,
        DiaryTokens.ML_DV_TIME_HOUR01,
        DiaryTokens.ML_DV_TIME_HOUR02,
        DiaryTokens.ML_DV_TIME_HOUR03,
        DiaryTokens.ML_DV_TIME_HOUR04,
        DiaryTokens.ML_DV_TIME_HOUR05,
        DiaryTokens.ML_DV_TIME_HOUR06,
        DiaryTokens.ML_DV_TIME_HOUR07,
        DiaryTokens.ML_DV_TIME_HOUR08,
        DiaryTokens.ML_DV_TIME_HOUR09,
        DiaryTokens.ML_DV_TIME_HOUR10,
        DiaryTokens.ML_DV_TIME_HOUR11,
        DiaryTokens.ML_DV_TIME_HOUR12,
        DiaryTokens.ML_DV_TIME_HOUR13,
        DiaryTokens.ML_DV_TIME_HOUR14,
        DiaryTokens.ML_DV_TIME_HOUR15,
        DiaryTokens.ML_DV_TIME_HOUR16,
        DiaryTokens.ML_DV_TIME_HOUR17,
        DiaryTokens.ML_DV_TIME_HOUR18,
        DiaryTokens.ML_DV_TIME_HOUR19,
        DiaryTokens.ML_DV_TIME_HOUR20,
        DiaryTokens.ML_DV_TIME_HOUR21,
        DiaryTokens.ML_DV_TIME_HOUR22,
        DiaryTokens.ML_DV_TIME_HOUR23,
        DiaryTokens.ML_DV_TIME_HOUR24,
    }; // DV_TIME_HOURS


    /**
     * text for timespan after viewing area (evening). <BR/>
     */
    public static String ML_DV_TIME_EVENING = "ML_DV_TIME_EVENING";

    // weekdays - starts from 1 = Sunday
    /**
     * Weekday: none. <BR/>
     */
    public static String ML_DV_WEEKDAY_NONE = "ML_DV_WEEKDAY_NONE";
    /**
     * Weekday: Sunday. <BR/>
     */
    public static String ML_DV_WEEKDAY_SUN = "ML_DV_WEEKDAY_SUN";
    /**
     * Weekday: Monday. <BR/>
     */
    public static String ML_DV_WEEKDAY_MON = "ML_DV_WEEKDAY_MON";
    /**
     * Weekday: Tuesday. <BR/>
     */
    public static String ML_DV_WEEKDAY_TUE = "ML_DV_WEEKDAY_TUE";
    /**
     * Weekday: Wednesday. <BR/>
     */
    public static String ML_DV_WEEKDAY_WEN = "ML_DV_WEEKDAY_WEN";
    /**
     * Weekday: Thursday. <BR/>
     */
    public static String ML_DV_WEEKDAY_THU = "ML_DV_WEEKDAY_THU";
    /**
     * Weekday: Friday. <BR/>
     */
    public static String ML_DV_WEEKDAY_FRI = "ML_DV_WEEKDAY_FRI";
    /**
     * Weekday: Saturday. <BR/>
     */
    public static String ML_DV_WEEKDAY_SAT = "ML_DV_WEEKDAY_SAT";

    /**
     * List of all weekdays. <BR>/>
     * <LI>0 ... None.</LI>
     * <LI>1 ... Sunday.</LI>
     * <LI>2 ... Monday.</LI>
     * <LI>...</LI>
     */
    public static String[] DV_WEEKDAYS =
    {
        DiaryTokens.ML_DV_WEEKDAY_NONE,
        DiaryTokens.ML_DV_WEEKDAY_SUN,
        DiaryTokens.ML_DV_WEEKDAY_MON,
        DiaryTokens.ML_DV_WEEKDAY_TUE,
        DiaryTokens.ML_DV_WEEKDAY_WEN,
        DiaryTokens.ML_DV_WEEKDAY_THU,
        DiaryTokens.ML_DV_WEEKDAY_FRI,
        DiaryTokens.ML_DV_WEEKDAY_SAT,
    }; // DV_WEEKDAYS


    // weekdays short - starts from 1 = Sunday
    /**
     * Weekday short name: None. <BR/>
     */
    public static String ML_DV_WEEKDAY_SHORT_NONE = "ML_DV_WEEKDAY_SHORT_NONE";
    /**
     * Weekday short name: Sunday. <BR/>
     */
    public static String ML_DV_WEEKDAY_SHORT_SUN = "ML_DV_WEEKDAY_SHORT_SUN";
    /**
     * Weekday short name: Monday. <BR/>
     */
    public static String ML_DV_WEEKDAY_SHORT_MON = "ML_DV_WEEKDAY_SHORT_MON";
    /**
     * Weekday short name: Tuesday. <BR/>
     */
    public static String ML_DV_WEEKDAY_SHORT_TUE = "ML_DV_WEEKDAY_SHORT_TUE";
    /**
     * Weekday short name: Wednesday. <BR/>
     */
    public static String ML_DV_WEEKDAY_SHORT_WEN = "ML_DV_WEEKDAY_SHORT_WEN";
    /**
     * Weekday short name: Thursday. <BR/>
     */
    public static String ML_DV_WEEKDAY_SHORT_THU = "ML_DV_WEEKDAY_SHORT_THU";
    /**
     * Weekday short name: Friday. <BR/>
     */
    public static String ML_DV_WEEKDAY_SHORT_FRI = "ML_DV_WEEKDAY_SHORT_FRI";
    /**
     * Weekday short name: Saturday. <BR/>
     */
    public static String ML_DV_WEEKDAY_SHORT_SAT = "ML_DV_WEEKDAY_SHORT_SAT";

    /**
     * List of all short week day names. <BR/>
     * <LI>0 ... None.</LI>
     * <LI>1 ... Sunday.</LI>
     * <LI>2 ... Monday.</LI>
     * <LI>...</LI>
     */
    public static String[] DV_WEEKDAYS_SHORT =
    {
        DiaryTokens.ML_DV_WEEKDAY_SHORT_NONE,
        DiaryTokens.ML_DV_WEEKDAY_SHORT_SUN,
        DiaryTokens.ML_DV_WEEKDAY_SHORT_MON,
        DiaryTokens.ML_DV_WEEKDAY_SHORT_TUE,
        DiaryTokens.ML_DV_WEEKDAY_SHORT_WEN,
        DiaryTokens.ML_DV_WEEKDAY_SHORT_THU,
        DiaryTokens.ML_DV_WEEKDAY_SHORT_FRI,
        DiaryTokens.ML_DV_WEEKDAY_SHORT_SAT,
    }; // DV_WEEKDAYS_SHORT


    ///////////////////////////////////////////////////////////////////////////
    //
    // constants for month view
    //
    // starts from 0 = January
    /**
     * Month name: January. <BR/>
     */
    public static String ML_MONTH_JAN = "ML_MONTH_JAN";
    /**
     * Month name: February. <BR/>
     */
    public static String ML_MONTH_FEB = "ML_MONTH_FEB";
    /**
     * Month name: March. <BR/>
     */
    public static String ML_MONTH_MAR = "ML_MONTH_MAR";
    /**
     * Month name: April. <BR/>
     */
    public static String ML_MONTH_APR = "ML_MONTH_APR";
    /**
     * Month name: May. <BR/>
     */
    public static String ML_MONTH_MAY = "ML_MONTH_MAY";
    /**
     * Month name: June. <BR/>
     */
    public static String ML_MONTH_JUN = "ML_MONTH_JUN";
    /**
     * Month name: July. <BR/>
     */
    public static String ML_MONTH_JUL = "ML_MONTH_JUL";
    /**
     * Month name: August. <BR/>
     */
    public static String ML_MONTH_AUG = "ML_MONTH_AUG";
    /**
     * Month name: September. <BR/>
     */
    public static String ML_MONTH_SEP = "ML_MONTH_SEP";
    /**
     * Month name: October. <BR/>
     */
    public static String ML_MONTH_OCT = "ML_MONTH_OCT";
    /**
     * Month name: November. <BR/>
     */
    public static String ML_MONTH_NOV = "ML_MONTH_NOV";
    /**
     * Month name: December. <BR/>
     */
    public static String ML_MONTH_DEC = "ML_MONTH_DEC";

    /**
     * List of all month names. <BR/>
     * <LI>0 ... January</LI>
     * <LI>1 ... February</LI>
     * <LI>...</LI>
     */
    public static String[] MONTHS =
    {
        DiaryTokens.ML_MONTH_JAN,
        DiaryTokens.ML_MONTH_FEB,
        DiaryTokens.ML_MONTH_MAR,
        DiaryTokens.ML_MONTH_APR,
        DiaryTokens.ML_MONTH_MAY,
        DiaryTokens.ML_MONTH_JUN,
        DiaryTokens.ML_MONTH_JUL,
        DiaryTokens.ML_MONTH_AUG,
        DiaryTokens.ML_MONTH_SEP,
        DiaryTokens.ML_MONTH_OCT,
        DiaryTokens.ML_MONTH_NOV,
        DiaryTokens.ML_MONTH_DEC,
    }; // MONTHS

    /**
     * text for not limited. <BR/>
     */
    public static String ML_NOLIMIT = "ML_NOLIMIT";

    /**
     * text for previous day. <BR/>
     */
    public static String ML_PREVIOUS_DAY = "ML_PREVIOUS_DAY";

    /**
     * text for next day. <BR/>
     */
    public static String ML_NEXT_DAY = "ML_NEXT_DAY";

    /**
     * text for button content. <BR/>
     */
    public static String ML_BUTTON_CONTENT = "ML_BUTTON_CONTENT";

    /**
     * text for button day. <BR/>
     */
    public static String ML_BUTTON_DAY = "ML_BUTTON_DAY";

    /**
     * text for button month. <BR/>
     */
    public static String ML_BUTTON_MONTH = "ML_BUTTON_MONTH";

    /**************************************************************************
     * Set properties which depend on other ones. <BR/>
     */
    public static void setDependentProperties ()
    {
        // TODO RB: Remove this part after all parts are migrated to MLI usage
/*
        DiaryTokens.DV_TIME_HOURS[0] = DiaryTokens.DV_TIME_HOUR00;
        DiaryTokens.DV_TIME_HOURS[1] = DiaryTokens.DV_TIME_HOUR01;
        DiaryTokens.DV_TIME_HOURS[2] = DiaryTokens.DV_TIME_HOUR02;
        DiaryTokens.DV_TIME_HOURS[3] = DiaryTokens.DV_TIME_HOUR03;
        DiaryTokens.DV_TIME_HOURS[4] = DiaryTokens.DV_TIME_HOUR04;
        DiaryTokens.DV_TIME_HOURS[5] = DiaryTokens.DV_TIME_HOUR05;
        DiaryTokens.DV_TIME_HOURS[6] = DiaryTokens.DV_TIME_HOUR06;
        DiaryTokens.DV_TIME_HOURS[7] = DiaryTokens.DV_TIME_HOUR07;
        DiaryTokens.DV_TIME_HOURS[8] = DiaryTokens.DV_TIME_HOUR08;
        DiaryTokens.DV_TIME_HOURS[9] = DiaryTokens.DV_TIME_HOUR09;
        DiaryTokens.DV_TIME_HOURS[10] = DiaryTokens.DV_TIME_HOUR10;
        DiaryTokens.DV_TIME_HOURS[11] = DiaryTokens.DV_TIME_HOUR11;
        DiaryTokens.DV_TIME_HOURS[12] = DiaryTokens.DV_TIME_HOUR12;
        DiaryTokens.DV_TIME_HOURS[13] = DiaryTokens.DV_TIME_HOUR13;
        DiaryTokens.DV_TIME_HOURS[14] = DiaryTokens.DV_TIME_HOUR14;
        DiaryTokens.DV_TIME_HOURS[15] = DiaryTokens.DV_TIME_HOUR15;
        DiaryTokens.DV_TIME_HOURS[16] = DiaryTokens.DV_TIME_HOUR16;
        DiaryTokens.DV_TIME_HOURS[17] = DiaryTokens.DV_TIME_HOUR17;
        DiaryTokens.DV_TIME_HOURS[18] = DiaryTokens.DV_TIME_HOUR18;
        DiaryTokens.DV_TIME_HOURS[19] = DiaryTokens.DV_TIME_HOUR19;
        DiaryTokens.DV_TIME_HOURS[20] = DiaryTokens.DV_TIME_HOUR20;
        DiaryTokens.DV_TIME_HOURS[21] = DiaryTokens.DV_TIME_HOUR21;
        DiaryTokens.DV_TIME_HOURS[22] = DiaryTokens.DV_TIME_HOUR22;
        DiaryTokens.DV_TIME_HOURS[23] = DiaryTokens.DV_TIME_HOUR23;
        DiaryTokens.DV_TIME_HOURS[24] = DiaryTokens.DV_TIME_HOUR24;

        DiaryTokens.DV_WEEKDAYS[0] = DiaryTokens.DV_WEEKDAY_NONE;
        DiaryTokens.DV_WEEKDAYS[1] = DiaryTokens.DV_WEEKDAY_SUN;
        DiaryTokens.DV_WEEKDAYS[2] = DiaryTokens.DV_WEEKDAY_MON;
        DiaryTokens.DV_WEEKDAYS[3] = DiaryTokens.DV_WEEKDAY_TUE;
        DiaryTokens.DV_WEEKDAYS[4] = DiaryTokens.DV_WEEKDAY_WEN;
        DiaryTokens.DV_WEEKDAYS[5] = DiaryTokens.DV_WEEKDAY_THU;
        DiaryTokens.DV_WEEKDAYS[6] = DiaryTokens.DV_WEEKDAY_FRI;
        DiaryTokens.DV_WEEKDAYS[7] = DiaryTokens.DV_WEEKDAY_SAT;

        DiaryTokens.DV_WEEKDAYS_SHORT[0] = DiaryTokens.DV_WEEKDAY_SHORT_NONE;
        DiaryTokens.DV_WEEKDAYS_SHORT[1] = DiaryTokens.DV_WEEKDAY_SHORT_SUN;
        DiaryTokens.DV_WEEKDAYS_SHORT[2] = DiaryTokens.DV_WEEKDAY_SHORT_MON;
        DiaryTokens.DV_WEEKDAYS_SHORT[3] = DiaryTokens.DV_WEEKDAY_SHORT_TUE;
        DiaryTokens.DV_WEEKDAYS_SHORT[4] = DiaryTokens.DV_WEEKDAY_SHORT_WEN;
        DiaryTokens.DV_WEEKDAYS_SHORT[5] = DiaryTokens.DV_WEEKDAY_SHORT_THU;
        DiaryTokens.DV_WEEKDAYS_SHORT[6] = DiaryTokens.DV_WEEKDAY_SHORT_FRI;
        DiaryTokens.DV_WEEKDAYS_SHORT[7] = DiaryTokens.DV_WEEKDAY_SHORT_SAT;

        DiaryTokens.MONTHS[0] = DiaryTokens.MONTH_JAN;
        DiaryTokens.MONTHS[1] = DiaryTokens.MONTH_FEB;
        DiaryTokens.MONTHS[2] = DiaryTokens.MONTH_MAR;
        DiaryTokens.MONTHS[3] = DiaryTokens.MONTH_APR;
        DiaryTokens.MONTHS[4] = DiaryTokens.MONTH_MAY;
        DiaryTokens.MONTHS[5] = DiaryTokens.MONTH_JUN;
        DiaryTokens.MONTHS[6] = DiaryTokens.MONTH_JUL;
        DiaryTokens.MONTHS[7] = DiaryTokens.MONTH_AUG;
        DiaryTokens.MONTHS[8] = DiaryTokens.MONTH_SEP;
        DiaryTokens.MONTHS[9] = DiaryTokens.MONTH_OCT;
        DiaryTokens.MONTHS[10] = DiaryTokens.MONTH_NOV;
        DiaryTokens.MONTHS[11] = DiaryTokens.MONTH_DEC;

        DiaryConstants.LST_HEADINGS_OVERLAP[0] = BOTokens.ML_NAME;
        DiaryConstants.LST_HEADINGS_OVERLAP[1] = DiaryTokens.ML_TERM_START_DATE;
        DiaryConstants.LST_HEADINGS_OVERLAP[2] = DiaryTokens.ML_TERM_END_DATE;
        DiaryConstants.LST_HEADINGS_OVERLAP[3] = DiaryTokens.ML_TERM_PLACE;

        DiaryConstants.LST_HEADINGS_PARTICIPANTS[0] = BOTokens.ML_NAME;
        DiaryConstants.LST_HEADINGS_PARTICIPANTS[1] = DiaryTokens.ML_TERM_ANNOUNCED;
        DiaryConstants.LST_HEADINGS_PARTICIPANTS[2] = DiaryTokens.ML_TERM_ANN_NAME;

        DiaryConstants.LST_HEADINGS_TERMINPLAN[0] = BOTokens.ML_NAME;
        DiaryConstants.LST_HEADINGS_TERMINPLAN[1] = DiaryTokens.ML_TERM_START_DATE;
        DiaryConstants.LST_HEADINGS_TERMINPLAN[2] = DiaryTokens.ML_TERM_END_DATE;
        DiaryConstants.LST_HEADINGS_TERMINPLAN[3] = DiaryTokens.ML_TERM_PLACE;
        DiaryConstants.LST_HEADINGS_TERMINPLAN[4] = BOTokens.ML_OWNER;
*/
    } // setDependentProperties

} // class DiaryTokens

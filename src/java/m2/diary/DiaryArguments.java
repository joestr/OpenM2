/*
 * Class: DiaryArguments.java
 */

// package:
package m2.diary;

// imports:


/******************************************************************************
 * Arguments for m2.diary. <BR/>
 * This abstract class contains all arguments which are necessary to deal with
 * the classes delivered within this package. <P>
 *
 * @version     $Id: DiaryArguments.java,v 1.4 2007/07/31 19:14:01 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 980707
 ******************************************************************************
 */
public abstract class DiaryArguments extends Object
{
    /**
     * Argument: current date of term in URL. <BR/>
     */
    public static final String ARG_TERM_CUR_VIEW_DATE   = "tcurd";

    /**
     * Argument: start date of term in URL. <BR/>
     */
    public static final String ARG_TERM_START_DATE   = "tstad";

    /**
     * Argument: end date of term in URL. <BR/>
     */
    public static final String ARG_TERM_END_DATE     = "tendd";

    /**
     * Argument: place of term in URL. <BR/>
     */
    public static final String ARG_TERM_PLACE   = "tpla";

    /**
     * Argument: participants container id of term in URL. <BR/>
     */
    public static final String ARG_TERM_PART_CONT   = "tpac";

    /**
     * Argument: day. <BR/>
     */
    public static final String ARG_TERM_DATE = "tdat";

    /**
     * Argument: day. <BR/>
     */
    public static final String ARG_TERM_DAY = "tday";

    /**
     * Argument: month. <BR/>
     */
    public static final String ARG_TERM_MONTH = "tmon";

    /**
     * Argument: year. <BR/>
     */
    public static final String ARG_TERM_YEAR = "tyea";

    /**
     * Argument: overlap of term in URL. <BR/>
     */
    public static final String ARG_TERM_OVERLAP = "tovl";

    /**
     * Argument: participants of term in URL. <BR/>
     */
    public static final String ARG_TERM_PARTICIPANTS = "tpar";

    /**
     * Argument: show-participants of term in URL. <BR/>
     */
    public static final String ARG_TERM_SHOW_PARTICIPANTS = "tpas";

    /**
     * Argument: max. number of participants of term in URL. <BR/>
     */
    public static final String ARG_TERM_MAX_PARTICIPANTS = "tpam";

    /**
     * Argument: cur. number of participants of term in URL. <BR/>
     */
    public static final String ARG_TERM_CUR_PARTICIPANTS = "tpacr";

    /**
     * Argument: deadline of announcements. <BR/>
     */
    public static final String ARG_TERM_DEADLINE = "tddl";

    /**
     * Argument: Announcer id. <BR/>
     */
    public static final String ARG_TERM_ANN_ID = "tani";

    /**
     * Argument: Announcer name. <BR/>
     */
    public static final String ARG_TERM_ANN_NAME = "tann";

    /**
     * Argument: attachments of term in URL. <BR/>
     */
    public static final String ARG_TERM_ATTACHMENTS = "tata";

} // class DiaryArguments

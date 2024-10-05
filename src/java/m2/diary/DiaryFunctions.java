/*
 * Class: DiaryFunctions.java
 */

// package:
package m2.diary;

// imports:


/******************************************************************************
 * Functions for m2. <BR/>
 * This abstract class contains all Functions which are necessary to deal with
 * the classes delivered within this package.<P>
 * Contains application defined object types and derived types with IDs from
 * 0x0101 to 0xFFFF. <BR/>
 *
 * The functions classes for the different components have to be at the
 * following numbers: <BR/>
 * <UL>
 * <LI>documents:           2001 .. 2999    FCT_D...
 * <LI>diary:               3001 .. 3999    FCT_T...
 * <LI>master data:         4001 .. 4999    FCT_S...
 * <LI>catalog of products: 5001 .. 5999    FCT_W...
 * <LI>discussions:         6001 .. 6999    FCT_N...
 * <LI>phone book:          7001 .. 7999    FCT_PB...
 * </UL>
 *
 * @version     $Id: DiaryFunctions.java,v 1.11 2007/07/31 19:14:01 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 98049
 ******************************************************************************
 */
public abstract class DiaryFunctions extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DiaryFunctions.java,v 1.11 2007/07/31 19:14:01 kreimueller Exp $";


    /**
     * Show day view in container object Terminplan. <BR/>
     */
    public static final int FCT_TERM_DAY_VIEW  =  3001;

    /**
     * Show month view in container object Terminplan. <BR/>
     */
    public static final int FCT_TERM_MON_VIEW  =  3002;

    /**
     * Show month view in container object Terminplan. <BR/>
     */
    public static final int FCT_TERM_MON_NEXT  =  3003;

    /**
     * Show month view in container object Terminplan. <BR/>
     */
    public static final int FCT_TERM_MON_PREV  =  3004;

    /**
     * Show month view in container object Terminplan. <BR/>
     */
    public static final int FCT_TERM_DAY_NEXT  =  3005;

    /**
     * Show month view in container object Terminplan. <BR/>
     */
    public static final int FCT_TERM_DAY_PREV  =  3006;

    /**
     * Go to special (given) month - "gehe zu" function in month view. <BR/>
     */
    public static final int FCT_TERM_MON_GOTO  =  3007;

    /**
     * Go to special (given) month - "gehe zu" function in month view. <BR/>
     */
    public static final int FCT_TERM_DAY_GOTO  =  3008;

    /**
     * Go to special (given) month - "gehe zu" function in month view. <BR/>
     */
    public static final int FCT_TERM_OVERLAP_FRAME_LIST  =  3009;

    /**
     * Go to special (given) month - "gehe zu" function in month view. <BR/>
     */
    public static final int FCT_TERM_OVERLAP_FRAME_FORM  =  3010;

    /**
     * Show list of participants for term. <BR/>
     */
    public static final int FCT_TERM_SHOW_PARTICIPANTS  =  3011;

    /**
     * Show list of participants for term. <BR/>
     */
    public static final int FCT_TERM_ADD_PARTICIPANT  =  3012;

    /**
     * Show list of participants for term. <BR/>
     */
    public static final int FCT_TERM_REM_PARTICIPANT  =  3013;

/*
    **
     * Go to special (given) month - "gehe zu" function in month view. <BR/>
     *
    public static final int FCT_TERM_PARTICIPANTS_FRAME_LIST  =  3014;

    **
     * Go to special (given) month - "gehe zu" function in month view. <BR/>
     *
    public static final int FCT_TERM_PARTICIPANTS_FRAME_FORM  =  3015;
*/

    /**
     * Add non-user participant. <BR/>
     */
    public static final int FCT_TERM_ADD_OTHER =  3014;

    /**
     * List-delete of participants. <BR/>
     */
    public static final int FCT_TERM_REM_PARTICIPANTS_LIST  =  3015;


    /**
     * Show list of participants for term. <BR/>
     */
    public static final int FCT_TERM_SENTOBJECTCONTAINER_FRAME_LIST = 3021;

} // class DiaryFunctions

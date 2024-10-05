/*
 * Class: DiaryFunctionHandler.java
 */

// package:
package m2.diary;

// imports:
import ibs.app.AppFunctions;
import ibs.app.func.FunctionValues;
import ibs.app.func.GeneralFunctionHandler;
import ibs.bo.Buttons;
import ibs.bo.OID;
import ibs.io.IOHelpers;
import ibs.tech.html.ButtonBarElement;
import ibs.util.GeneralException;
import ibs.util.list.IElementId;

import m2.diary.DiaryArguments;
import m2.diary.DiaryTokens;
import m2.diary.OverlapContainer_01;
import m2.diary.ParticipantContainer_01;
import m2.diary.Termin_01;
import m2.diary.Terminplan_01;


/******************************************************************************
 * Application object which is created with each call of a page. <BR/>
 * An object of this class represents the interface between the network and the
 * business logic itself. <BR/>
 * It gets arguments from the user, controls the program flow, and sends data
 * back to the user and his browser. <BR/>
 * There has to be generated an extension class of this class to realize the
 * functions which are specific to the required application.
 *
 * @version     $Id: DiaryFunctionHandler.java,v 1.64 2007/07/24 21:24:36 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 980402
 ******************************************************************************
 */
public class DiaryFunctionHandler extends GeneralFunctionHandler
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DiaryFunctionHandler.java,v 1.64 2007/07/24 21:24:36 kreimueller Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a DiaryFunctionHandler object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   id      Id of the element.
     * @param   name    The element's name.
     */
    public DiaryFunctionHandler (IElementId id, String name)
    {
        // call constructor of super class:
        super (id, name);

        // initialize the other instance properties:
    } // DiaryFunctionHandler


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Evaluate the function to be performed. <BR/>
     *
     * @param   function    The function to be performed.
     * @param   values      The values for the function handler.
     *
     * @return  Function to be performed. <BR/>
     *          <CODE>AppFunctions.FCT_NOFUNCTION</CODE> if there is no function
     *          or the function was already performed.
     */
    public int evalFunction (int function, FunctionValues values)
    {
        Terminplan_01 term;
        Termin_01 termin;
        ParticipantContainer_01 pc;
        OverlapContainer_01 oc;
        OID oid = values.p_oid;         // the actual oid

        try
        {
            switch (function)       // perform function
            {
                case DiaryFunctions.FCT_TERM_MON_VIEW: // show month view of Terminplan
                    // get object oid and show view:
                    term = (Terminplan_01) values.getObject ();
                    // show object in month view
                    if (term != null) // got object?
                    {
/* KR method not longer valid
                            values.p_sess.userInfo.history.add (oid, m2Tabs.TABN_VIEW_MONTH, DiaryTokens.TOK_TERM_MONTHLYOVERVIEW);
*/
                        term.monthView ();
                    } // if got object
                    break;

                case DiaryFunctions.FCT_TERM_DAY_VIEW: // show day view of Terminplan
                    // get object oid and show view:
                    term = (Terminplan_01) values.getObject ();
                    // show object in day view
                    if (term != null) // got object?
                    {
/* KR method not longer valid
                            values.p_sess.userInfo.history.add (oid, m2Tabs.TABN_VIEW_DAY, DiaryTokens.TOK_TERM_DAILYOVERVIEW);
*/
                        term.dayView ();
                    } // if got object
                    break;

                case DiaryFunctions.FCT_TERM_DAY_NEXT: // show next day in dayview
                    // get object oid and show view:
                    term = (Terminplan_01) values.getObject ();
                    // show object in day view
                    if (term != null) // got object?
                    {
                        term.viewNextDay (values.p_env.getParam
                            (DiaryArguments.ARG_TERM_CUR_VIEW_DATE));
                    } // if got object
                    break;

                case DiaryFunctions.FCT_TERM_DAY_PREV: // show previous day in day view
                    // get object oid and show view:
                    term = (Terminplan_01) values.getObject ();
                    // show object in day view
                    if (term != null) // got object?
                    {
                        term.viewPrevDay (values.p_env.getParam
                            (DiaryArguments.ARG_TERM_CUR_VIEW_DATE));
                    } // if got object
                    break;

                case DiaryFunctions.FCT_TERM_MON_NEXT: // show next month in month view
                    // get object oid and show view:
                    term = (Terminplan_01) values.getObject ();
                    // show object in month view
                    if (term != null) // got object?
                    {
                        term.viewNextMonth (values.p_env.getParam
                            (DiaryArguments.ARG_TERM_CUR_VIEW_DATE));
                    } // if got object
                    break;

                case DiaryFunctions.FCT_TERM_MON_PREV: // show prev month in month view
                    // get object oid and show view:
                    term = (Terminplan_01) values.getObject ();
                    // show object in month view
                    if (term != null) // got object?
                    {
                        term.viewPrevMonth (values.p_env.getParam
                            (DiaryArguments.ARG_TERM_CUR_VIEW_DATE));
                    } // if got object
                    break;

                case DiaryFunctions.FCT_TERM_MON_GOTO: // show prev month in month view
                    // get object oid and show view:
                    term = (Terminplan_01) values.getObject ();
                    // show object in day view
                    if (term != null) // got object?
                    {
                        term
                            .gotoMonth (
                                "" +
                                    values.p_env
                                        .getIntParam (DiaryArguments.ARG_TERM_MONTH),
                                "" +
                                    values.p_env
                                        .getIntParam (DiaryArguments.ARG_TERM_YEAR));
                    } // if got object
                    break;

                case DiaryFunctions.FCT_TERM_DAY_GOTO: // show prev month in month view
                    // get object oid and show view:
                    term = (Terminplan_01) values.getObject ();
                    // show object in day view
                    if (term != null) // got object?
                    {
                        term.gotoDay (values.p_env.getParam
                            (DiaryArguments.ARG_TERM_DATE));
                    } // if got object
                    break;

                // show list of overlapping terms
                case DiaryFunctions.FCT_TERM_OVERLAP_FRAME_LIST:
                    // get container object -> current user & current term
                    oc = (OverlapContainer_01)
                        values.getNewObject (DiaryTypeConstants.TC_OverlapContainer);
                    // set term OID - current term used to get overlapping
                    // terms
                    if (oc != null) // got object?
                    {
                        oc.containerId = values.p_containerOid;
                        oc.setOverlapTerm (oid);
                        // show list
                        oc.showContent (values.p_representationForm);
                    } // if
                    break;

                // show "bearbeiten" form again for already entered
                // overlapping term
                case DiaryFunctions.FCT_TERM_OVERLAP_FRAME_FORM:
                    // get object
                    termin = (Termin_01) values.getObject ();
                    // show form
                    if (termin != null) // got object?
                    {
/* KR not longer necessary
                            // remove last object (termin itself) in history
                            // to avoid problems on 'ABBRECHEN'
                            values.p_sess.userInfo.history.prev ();
//debug ("removing history entry");
//debug (values.p_sess.userInfo.history.toString ());
*/
                        termin.overlapShowForm ();
                    } // if
                    break;

                case DiaryFunctions.FCT_TERM_SHOW_PARTICIPANTS:
                    pc = (ParticipantContainer_01) values.getObject ();
                    // show list & form
                    if (pc != null) // got object?
                    {
//                            pc.showFrameSet ();
                        pc.show (values.p_representationForm);
                    } // if got object
                    break;

                case DiaryFunctions.FCT_TERM_ADD_PARTICIPANT:
                    pc = (ParticipantContainer_01) values.getObject ();

                    // show list:
                    if (pc != null) // got object?
                    {
                        pc.addParticipant ();
                    } // if got object
                    break;

                case DiaryFunctions.FCT_TERM_REM_PARTICIPANT:
                    pc = (ParticipantContainer_01) values.getObject ();
                    // show list
                    if (pc != null) // got object?
                    {
                        pc.removeParticipant ();
                    } // if got object
                    break;

                default:            // unknown function
                    // return the function to be performed:
                    return function;
            } // switch function
        } // try
        catch (GeneralException e)
        {
            IOHelpers.showMessage (e.toString (),
                values.p_app, values.p_sess, values.p_env);
        } // catch
        catch (Exception e)
        {
            IOHelpers.showMessage (e.toString (),
                values.p_app, values.p_sess, values.p_env);
        } // catch

        // return that no further function shall be performed:
        return AppFunctions.FCT_NOFUNCTION;
    } // evalFunction


    /**************************************************************************
     * Sets the dependencies of the files filled. <BR/>
     */
    public void setDependentProperties ()
    {
        super.setDependentProperties ();

        DiaryTokens.setDependentProperties ();
    } // setDependentProperties


    /**************************************************************************
     * Sets all possible buttons and constructs a buttonBar element. <BR/>
     *
     * @param   buttonBar   a ButtonBarElement Object where the buttons shall
     *                      be added to.
     */
    public void setButtons (ButtonBarElement buttonBar)
    {
        // call corresponding method of super class:
        super.setButtons (buttonBar);

        // announce:
        this.setButton (buttonBar, Buttons.BTN_ANNOUNCE, IOHelpers
            .getLoadJavaScript (DiaryFunctions.FCT_TERM_ADD_PARTICIPANT), 0, 0);

        // unannounce:
        this.setButton (buttonBar, Buttons.BTN_UNANNOUNCE, IOHelpers
            .getLoadJavaScript (DiaryFunctions.FCT_TERM_REM_PARTICIPANT), 0, 0);

        // announce another one:
        this.setButton (buttonBar, Buttons.BTN_ANNOUNCE_OTHER, IOHelpers
            .getLoadContJavaScript (AppFunctions.FCT_OBJECTNEWFORM), 0, 0);

        // unannounce in list:
        this.setButton (buttonBar, Buttons.BTN_UNANNOUNCE_LIST, IOHelpers
            .getLoadJavaScript (DiaryFunctions.FCT_TERM_REM_PARTICIPANTS_LIST),
            0, 0);
    } // setButtons

} // class DiaryFunctionHandler

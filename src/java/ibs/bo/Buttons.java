/*
 * Class: Buttons.java
 */

// package:
package ibs.bo;

// imports:
//KR TODO: cyclic dependency
import ibs.bo.BOTokens;


/******************************************************************************
 * Buttons for ibs applications. <BR/>
 * This abstract class contains all constants which are necessary to deal with
 * the buttons delivered within this package. <P>
 *
 * @version     $Id: Buttons.java,v 1.24 2010/04/07 13:37:08 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 980518
 ******************************************************************************
 */
public abstract class Buttons extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Buttons.java,v 1.24 2010/04/07 13:37:08 rburgermann Exp $";

    /**
     * No button. <BR/>
     */
    public static final int BTN_NONE = -1;

    /**
     * Maximum button number within the {@link #BTN_NAMES BTN_NAMES} array. <BR/>
     */
    public static int BTN_MAX = 0;

    // specific buttons:
    /**
     * Button used for editing an object. <BR/>
     */
    public static final int BTN_EDIT = 1;
    /**
     * Integer version of BTN_EDIT. <BR/>
     */
    public static final Integer BTN_EDITINT = new Integer (Buttons.BTN_EDIT);

    /**
     * Button used for deleting an object. <BR/>
     */
    public static final int BTN_DELETE = 2;
    /**
     * Integer version of BTN_DELETE. <BR/>
     */
    public static final Integer BTN_DELETEINT = new Integer (Buttons.BTN_DELETE);

    /**
     * Button used for cutting an object. <BR/>
     */
    public static final int BTN_CUT = 3;
    /**
     * Integer version of BTN_CUT. <BR/>
     */
    public static final Integer BTN_CUTINT = new Integer (Buttons.BTN_CUT);

    /**
     * Button used for copying an object. <BR/>
     */
    public static final int BTN_COPY = 4;
    /**
     * Integer version of BTN_COPY. <BR/>
     */
    public static final Integer BTN_COPYINT = new Integer (Buttons.BTN_COPY);

    /**
     * Button used for pasting an object. <BR/>
     */
    public static final int BTN_PASTE = 5;
    /**
     * Integer version of BTN_PASTE. <BR/>
     */
    public static final Integer BTN_PASTEINT = new Integer (Buttons.BTN_PASTE);

    /**
     * Button used for distributing an object. <BR/>
     */
    public static final int BTN_DISTRIBUTE = 6;
    /**
     * Integer version of BTN_DISTRIBUTE. <BR/>
     */
    public static final Integer BTN_DISTRIBUTEINT =
        new Integer (Buttons.BTN_DISTRIBUTE);

    /**
     * Button used for cleaning an object. <BR/>
     */
    public static final int BTN_CLEAN = 7;
    /**
     * Integer version of BTN_CLEAN. <BR/>
     */
    public static final Integer BTN_CLEANINT = new Integer (Buttons.BTN_CLEAN);

    /**
     * Button used for assigning an object. <BR/>
     */
    public static final int BTN_ASSIGN = 8;
    /**
     * Integer version of BTN_ASSIGN. <BR/>
     */
    public static final Integer BTN_ASSIGNINT = new Integer (Buttons.BTN_ASSIGN);

    /**
     * Button used for going to an object. <BR/>
     */
    public static final int BTN_GOTO = 9;
    /**
     * Integer version of BTN_GOTO. <BR/>
     */
    public static final Integer BTN_GOTOINT = new Integer (Buttons.BTN_GOTO);

    /**
     * Button used for searching for an object. <BR/>
     */
    public static final int BTN_SEARCH = 10;
    /**
     * Integer version of BTN_SEARCH. <BR/>
     */
    public static final Integer BTN_SEARCHINT = new Integer (Buttons.BTN_SEARCH);

    /**
     * Button used for showing help. <BR/>
     */
    public static final int BTN_HELP = 11;
    /**
     * Integer version of BTN_HELP. <BR/>
     */
    public static final Integer BTN_HELPINT = new Integer (Buttons.BTN_HELP);

    /**
     * Button used for adding an answer to an object. <BR/>
     */
    public static final int BTN_ANSWER = 12;
    /**
     * Integer version of BTN_ANSWER. <BR/>
     */
    public static final Integer BTN_ANSWERINT = new Integer (Buttons.BTN_ANSWER);

    /**
     * Button for submitting a form. <BR/>
     */
    public static final int BTN_SUBMIT = 13;
    /**
     * Integer version of BTN_SUBMIT. <BR/>
     */
    public static final Integer BTN_SUBMITINT = new Integer (Buttons.BTN_SUBMIT);

    /**
     * Button for cancelling a form. <BR/>
     */
    public static final int BTN_CANCEL = 14;
    /**
     * Integer version of BTN_CANCEL. <BR/>
     */
    public static final Integer BTN_CANCELINT = new Integer (Buttons.BTN_CANCEL);

    /**
     * Button for logging in. <BR/>
     */
    public static final int BTN_LOGIN = 15;
    /**
     * Integer version of BTN_LOGIN. <BR/>
     */
    public static final Integer BTN_LOGININT = new Integer (Buttons.BTN_LOGIN);

    /**
     * Button for ordering = adding a product to the shopping cart. <BR/>
     */
    public static final int BTN_SHOPPINGCART = 16;
    /**
     * Integer version of BTN_SHOPPINGCART. <BR/>
     */
    public static final Integer BTN_SHOPPINGCARTINT =
        new Integer (Buttons.BTN_SHOPPINGCART);

    /**
     * Button to display the container content as selectionlist in order to
     * delete objects. <BR/>
     */
    public static final int BTN_LISTDELETE = 17;
    /**
     * Integer version of BTN_LISTDELETE. <BR/>
     */
    public static final Integer BTN_LISTDELETEINT =
        new Integer (Buttons.BTN_LISTDELETE);

    /**
     * Button for make a reference of a marked object. <BR/>
     */
    public static final int BTN_REFERENCE = 18;
    /**
     * Integer version of BTN_REFERENCE. <BR/>
     */
    public static final Integer BTN_REFERENCEINT = new Integer (Buttons.BTN_REFERENCE);

    /**
     * Button to announce user to a term. <BR/>
     */
    public static final int BTN_ANNOUNCE = 19;
    /**
     * Integer version of BTN_ANNOUNCE. <BR/>
     */
    public static final Integer BTN_ANNOUNCEINT = new Integer (Buttons.BTN_ANNOUNCE);

    /**
     * Button to "un" - announce user to a term. <BR/>
     */
    public static final int BTN_UNANNOUNCE = 20;
    /**
     * Integer version of BTN_UNANNOUNCE. <BR/>
     */
    public static final Integer BTN_UNANNOUNCEINT =
        new Integer (Buttons.BTN_UNANNOUNCE);

    /**
     * Button to change the user's password. <BR/>
     */
    public static final int BTN_CHANGEPASSWORD = 21;
    /**
     * Integer version of BTN_CHANGEPASSWORD. <BR/>
     */
    public static final Integer BTN_CHANGEPASSWORDINT =
        new Integer (Buttons.BTN_CHANGEPASSWORD);

    /**
     * Button to change the user's password. <BR/>
     */
    public static final int BTN_DELETEENTRIES = 22;
    /**
     * Integer version of BTN_DELETEENTRIES. <BR/>
     */
    public static final Integer BTN_DELETEENTRIESINT =
        new Integer (Buttons.BTN_DELETEENTRIES);

    /**
     * Button for ordering = adding a product to the shopping cart. <BR/>
     */
    public static final int BTN_ORDER = 23;
    /**
     * Integer version of BTN_ORDER. <BR/>
     */
    public static final Integer BTN_ORDERINT = new Integer (Buttons.BTN_ORDER);

    /**
     * Button used for editing a rightsObject. <BR/>
     */
    public static final int BTN_RIGHTSEDIT = 24;
    /**
     * Integer version of BTN_RIGHTSEDIT. <BR/>
     */
    public static final Integer BTN_RIGHTSEDITINT =
        new Integer (Buttons.BTN_RIGHTSEDIT);

    /**
     * Button used for deleting a rightsObject. <BR/>
     */
    public static final int BTN_RIGHTSDELETE = 25;
    /**
     * Integer version of BTN_RIGHTSDELETE. <BR/>
     */
    public static final Integer BTN_RIGHTSDELETEINT =
        new Integer (Buttons.BTN_RIGHTSDELETE);

    /**
     * Button used for setting rights to all subObjects. <BR/>
     */
    public static final int BTN_SETRIGHTSREC = 26;
    /**
     * Integer version of BTN_SETRIGHTSREC. <BR/>
     */
    public static final Integer BTN_SETRIGHTSRECINT =
        new Integer (Buttons.BTN_SETRIGHTSREC);

    /**
     * Button used for setting rights to all subObjects. <BR/>
     */
    public static final int BTN_CLEANLOG = 27;
    /**
     * Integer version of BTN_CLEANLOG. <BR/>
     */
    public static final Integer BTN_CLEANLOGINT = new Integer (Buttons.BTN_CLEANLOG);

    /**
     * Button for ordering = adding a product to the shopping cart. <BR/>
     */
    public static final int BTN_SENDORDER = 28;
    /**
     * Integer version of BTN_SENDORDER. <BR/>
     */
    public static final Integer BTN_SENDORDERINT = new Integer (Buttons.BTN_SENDORDER);

    /**
     * Button to announce person (no system user) to a term. <BR/>
     */
    public static final int BTN_ANNOUNCE_OTHER = 29;
    /**
     * Integer version of BTN_ANNOUNCE_OTHER. <BR/>
     */
    public static final Integer BTN_ANNOUNCE_OTHERINT =
        new Integer (Buttons.BTN_ANNOUNCE_OTHER);

    /**
     * Button to show announcements delete list. <BR/>
     */
    public static final int BTN_UNANNOUNCE_LIST = 30;
    /**
     * Integer version of BTN_UNANNOUNCE_LIST. <BR/>
     */
    public static final Integer BTN_UNANNOUNCE_LISTINT =
        new Integer (Buttons.BTN_UNANNOUNCE_LIST);

    /**
     * Button to show a print window. <BR/>
     */
    public static final int BTN_PRINT = 31;
    /**
     * Integer version of BTN_PRINT. <BR/>
     */
    public static final Integer BTN_PRINTINT = new Integer (Buttons.BTN_PRINT);

    /**
     * Button to show a print window. <BR/>
     */
    public static final int BTN_GOTOCONTAINER = 32;
    /**
     * Integer version of BTN_GOTOCONTAINER. <BR/>
     */
    public static final Integer BTN_GOTOCONTAINERINT =
        new Integer (Buttons.BTN_GOTOCONTAINER);

    /**
     * Button to go back. <BR/>
     */
    public static final int BTN_BACK = 33;

    /**
     * Button to go forward. <BR/>
     */
    public static final int BTN_GOFORWARD = 66;

    /**
     * Integer version of BTN_BACK. <BR/>
     */
    public static final Integer BTN_BACKINT = new Integer (Buttons.BTN_BACK);

    /**
     * Button to add new Rights. <BR/>
     */
    public static final int BTN_RIGHTSNEW = 34;
    /**
     * Integer version of BTN_RIGHTSNEW. <BR/>
     */
    public static final Integer BTN_RIGHTSNEWINT = new Integer (Buttons.BTN_RIGHTSNEW);

    /**
     * Button used for setting rights to all subObjects. <BR/>
     */
    public static final int BTN_LISTDELETERIGHTS = 35;
    /**
     * Integer version of BTN_LISTDELETERIGHTS. <BR/>
     */
    public static final Integer BTN_LISTDELETERIGHTSINT =
        new Integer (Buttons.BTN_LISTDELETERIGHTS);

    /**
     * Button used for import. <BR/>
     */
    public static final int BTN_IMPORT = 36;
    /**
     * Integer version of BTN_IMPORT. <BR/>
     */
    public static final Integer BTN_IMPORTINT = new Integer (Buttons.BTN_IMPORT);

    /**
     * Button used to activate the settings, for example the Layout for the
     * user profile. <BR/>
     */
    public static final int BTN_ACTIVATE = 37;
    /**
     * Integer version of BTN_ACTIVATE. <BR/>
     */
    public static final Integer BTN_ACTIVATEINT = new Integer (Buttons.BTN_ACTIVATE);

    /**
     * Button used for adding a topic. <BR/>
     */
    public static final int BTN_TOPICNEW = 38;
    /**
     * Integer version of BTN_TOPICNEW. <BR/>
     */
    public static final Integer BTN_TOPICNEWINT = new Integer (Buttons.BTN_TOPICNEW);

    /**
     * Button used for export. <BR/>
     */
    public static final int BTN_EXPORT = 39;
    /**
     * Integer version of BTN_EXPORT. <BR/>
     */
    public static final Integer BTN_EXPORTINT = new Integer (Buttons.BTN_EXPORT);

    /**
     * Button used for change the orderstate. <BR/>
     */
    public static final int BTN_CHANGEORDERSTATE = 40;
    /**
     * Integer version of BTN_CHANGEORDERSTATE. <BR/>
     */
    public static final Integer BTN_CHANGEORDERSTATEINT =
        new Integer (Buttons.BTN_CHANGEORDERSTATE);

    /**
     * Button used for adding an object. <BR/>
     */
    public static final int BTN_NEW = 41;
    /**
     * Integer version of BTN_NEW. <BR/>
     */
    public static final Integer BTN_NEWINT = new Integer (Buttons.BTN_NEW);

    /**
     * Button to display the container content as changeform. <BR/>
     */
    public static final int BTN_LISTCHANGE = 42;
    /**
     * Integer version of BTN_LISTCHANGE. <BR/>
     */
    public static final Integer BTN_LISTCHANGEINT =
        new Integer (Buttons.BTN_LISTCHANGE);

    /**
     * Button to display the container content as changeform for the
     * RightsContainer. <BR/>
     */
    public static final int BTN_ASSIGNRIGHTS = 43;
    /**
     * Integer version of BTN_ASSIGNRIGHTS. <BR/>
     */
    public static final Integer BTN_ASSIGNRIGHTSINT =
        new Integer (Buttons.BTN_ASSIGNRIGHTS);

    /**
    * Button to display the container content as selectionlist in order to
    * copy objects. <BR/>
    */
    public static final int BTN_LIST_COPY = 44;
    /**
     * Integer version of BTN_LIST_COPY. <BR/>
     */
    public static final Integer BTN_LIST_COPYINT = new Integer (Buttons.BTN_LIST_COPY);

    /**
    * Button to display the container content as selectionlist in order to
    * copy objects. <BR/>
    */
    public static final int BTN_LIST_CUT = 45;
    /**
     * Integer version of BTN_LIST_CUT. <BR/>
     */
    public static final Integer BTN_LIST_CUTINT = new Integer (Buttons.BTN_LIST_CUT);

    /**
     * Button to check out an object. <BR/>
     */
    public static final int BTN_CHECKOUT = 46;
    /**
     * Integer version of BTN_CHECKOUT. <BR/>
     */
    public static final Integer BTN_CHECKOUTINT = new Integer (Buttons.BTN_CHECKOUT);

    /**
     * Button to check in an object. <BR/>
     */
    public static final int BTN_CHECKIN = 47;
    /**
     * Integer version of BTN_CHECKIN. <BR/>
     */
    public static final Integer BTN_CHECKININT = new Integer (Buttons.BTN_CHECKIN);

    /**
    * Button to forward objects. <BR/>
    */
    public static final int BTN_FORWARD = 48;
    /**
     * Integer version of BTN_FORWARD. <BR/>
     */
    public static final Integer BTN_FORWARDINT = new Integer (Buttons.BTN_FORWARD);

    /**
    * Button to start workflow. <BR/>
    */
    public static final int BTN_STARTWORKFLOW = 49;
    /**
     * Integer version of BTN_STARTWORKFLOW. <BR/>
     */
    public static final Integer BTN_STARTWORKFLOWINT =
        new Integer (Buttons.BTN_STARTWORKFLOW);

    /**
    * Button to finish workflow. <BR/>
    */
    public static final int BTN_FINISHWORKFLOW = 50;
    /**
     * Integer version of BTN_FINISHWORKFLOW. <BR/>
     */
    public static final Integer BTN_FINISHWORKFLOWINT =
        new Integer (Buttons.BTN_FINISHWORKFLOW);

    /**
    * Button to abort workflow. <BR/>
    */
    public static final int BTN_ABORTWORKFLOW = 51;
    /**
     * Integer version of BTN_ABORTWORKFLOW. <BR/>
     */
    public static final Integer BTN_ABORTWORKFLOWINT =
        new Integer (Buttons.BTN_ABORTWORKFLOW);

    /**
    * Button used for undeleting an object. <BR/>
    */
    public static final int BTN_UNDELETE = 52;
    /**
     * Integer version of BTN_UNDELETE. <BR/>
     */
    public static final Integer BTN_UNDELETEINT = new Integer (Buttons.BTN_UNDELETE);

    /**
     * Button to display the container content as selectionlist to forward objects. <BR/>
    */
    public static final int BTN_LISTFORWARD = 53;
    /**
     * Integer version of BTN_LISTFORWARD. <BR/>
     */
    public static final Integer BTN_LISTFORWARDINT =
        new Integer (Buttons.BTN_LISTFORWARD);

    /**
     * Button to display the generate translator form. <BR/>
     */
    public static final int BTN_GENERATETRANSLATOR = 54;
    /**
     * Integer version of BTN_GENERATETRANSLATOR. <BR/>
     */
    public static final Integer BTN_GENERATETRANSLATORINT =
        new Integer (Buttons.BTN_GENERATETRANSLATOR);

    /**
     * Button to display the checkin function in the object view. It
     * is used for editing first the object and then checking in. <BR/>
     */
    public static final int BTN_EDITBEFORECHECKIN = 55;
    /**
     * Integer version of BTN_EDITBEFORECHECKIN. <BR/>
     */
    public static final Integer BTN_EDITBEFORECHECKININT =
        new Integer (Buttons.BTN_EDITBEFORECHECKIN);

    /**
     * Button to display the checkin function in the container. <BR/>
     */
    public static final int BTN_CHECKINCONTAINER = 56;
    /**
     * Integer version of BTN_CHECKINCONTAINER. <BR/>
     */
    public static final Integer BTN_CHECKINCONTAINERINT =
        new Integer (Buttons.BTN_CHECKINCONTAINER);

    /**
     * Button to display the checkout function in the container. <BR/>
     */
    public static final int BTN_CHECKOUTCONTAINER = 57;
    /**
     * Integer version of BTN_CHECKOUTCONTAINER. <BR/>
     */
    public static final Integer BTN_CHECKOUTCONTAINERINT =
        new Integer (Buttons.BTN_CHECKOUTCONTAINER);

    /**
     * Button to display the checkout function in the container. <BR/>
     */
    public static final int BTN_EDITBEFORECHECKINCONTAINER = 58;
    /**
     * Integer version of BTN_EDITBEFORECHECKINCONTAINER. <BR/>
     */
    public static final Integer BTN_EDITBEFORECHECKINCONTAINERINT =
        new Integer (Buttons.BTN_EDITBEFORECHECKINCONTAINER);

    /**
     * Button used to call query for search and create link. <BR/>
     */
    public static final int BTN_SEARCHANDREFERENCE = 59;
    /**
     * Integer version of BTN_SEARCHANDREFERENCE. <BR/>
     */
    public static final Integer BTN_SEARCHANDREFERENCEINT =
        new Integer (Buttons.BTN_SEARCHANDREFERENCE);

    /**
     * Button used for call query for new and create link. <BR/>
     */
    public static final int BTN_NEWANDREFERENCE = 60;
    /**
     * Integer version of BTN_NEWANDREFERENCE. <BR/>
     */
    public static final Integer BTN_NEWANDREFERENCEINT =
        new Integer (Buttons.BTN_NEWANDREFERENCE);

    /**
     * Button to display the publish function for versions. <BR/>
     */
    public static final int BTN_PUBLISH = 61;
    /**
     * Integer version of BTN_PUBLISH. <BR/>
     */
    public static final Integer BTN_PUBLISHINT = new Integer (Buttons.BTN_PUBLISH);

    /**
     * Button to go to container of ext object. <BR/>
     */
    public static final int BTN_GOTOCONTAINEREXT = 62;
    /**
     * Integer version of BTN_GOTOCONTAINEREXT. <BR/>
     */
    public static final Integer BTN_GOTOCONTAINEREXTINT =
        new Integer (Buttons.BTN_GOTOCONTAINEREXT);

    /**
     * Button to create new external object. <BR/>
     */
    public static final int BTN_NEWEXT = 63;
    /**
     * Integer version of BTN_NEWEXT. <BR/>
     */
    public static final Integer BTN_NEWEXTINT = new Integer (Buttons.BTN_NEWEXT);

    /**
     * Button to check out a webdav object. <BR/>
     */
    public static final int BTN_WEBDAVCHECKOUT = 64;
    /**
     * Integer version of BTN_WEBDAVCHECKOUT. <BR/>
     */
    public static final Integer BTN_WEBDAVCHECKOUTINT =
        new Integer (Buttons.BTN_WEBDAVCHECKOUT);

    /**
     * Button to check in a webdav object. <BR/>
     */
    public static final int BTN_WEBDAVCHECKIN = 65;
    /**
     * Integer version of BTN_WEBDAVCHECKIN. <BR/>
     */
    public static final Integer BTN_WEBDAVCHECKININT =
        new Integer (Buttons.BTN_WEBDAVCHECKIN);



    // names of the different buttons:
    /**
     * Names of the different buttons. <BR/>
     * To be written below the buttons when represented to the user.
     */
    public static String [] BTN_NAMES =
    {
        "",
        BOTokens.ML_BUTTONEDIT,
        BOTokens.ML_BUTTONDELETE,
        BOTokens.ML_BUTTONCUT,
        BOTokens.ML_BUTTONCOPY,
        BOTokens.ML_BUTTONPASTE,
        BOTokens.ML_BUTTONDISTRIBUTE,
        BOTokens.ML_BUTTONCLEAN,
        BOTokens.ML_BUTTONASSIGN,
        BOTokens.ML_BUTTONGOTO,
        BOTokens.ML_BUTTONSEARCH,
        BOTokens.ML_BUTTONHELP,
        BOTokens.ML_BUTTONANSWER,
        BOTokens.ML_BUTTONOK,
        BOTokens.ML_BUTTONCANCEL,
        BOTokens.ML_BUTTONLOGIN,
        BOTokens.ML_BUTTONSHOPPINGCART,
        BOTokens.ML_BUTTONLISTDELETE,
        BOTokens.ML_BUTTONREFERENCE,
        BOTokens.ML_BUTTONANNOUNCE,
        BOTokens.ML_BUTTONUNANNOUNCE,
        BOTokens.ML_BUTTONCHANGEPASSWORD,
        BOTokens.ML_BUTTONDELETEENTRIES,
        BOTokens.ML_BUTTONORDER,
        BOTokens.ML_BUTTONRIGHTSEDIT,
        BOTokens.ML_BUTTONRIGHTSDELETE,
        BOTokens.ML_BUTTONSETRIGHTSREC,
        BOTokens.ML_BUTTONCLEANLOG,
        BOTokens.ML_BUTTONSENDORDER,
        BOTokens.ML_BUTTONANNOUNCE_OTHER,
        BOTokens.ML_BUTTONUNANNOUNCE_LIST,
        BOTokens.ML_BUTTONPRINT,
        BOTokens.ML_BUTTONGOTOCONTAINER,
        BOTokens.ML_BUTTONBACK,
        BOTokens.ML_BUTTONRIGHTSNEW,
        BOTokens.ML_BUTTONLISTDELETERIGHTS,
        BOTokens.ML_BUTTONIMPORT,
        BOTokens.ML_BUTTONACTIVATE,
        BOTokens.ML_BUTTONTOPICNEW,
        BOTokens.ML_BUTTONEXPORT,
        BOTokens.ML_BUTTONCHANGEORDERSTATE,
        BOTokens.ML_BUTTONNEW,
        BOTokens.ML_BUTTONLISTCHANGE,          // multiple operation
        BOTokens.ML_BUTTONASSIGNRIGHTS,
        BOTokens.ML_BUTTONLIST_COPY,           // multiple operation
        BOTokens.ML_BUTTONLIST_CUT,            // multiple operation
        BOTokens.ML_BUTTONCHECKOUT,
        BOTokens.ML_BUTTONCHECKIN,
        BOTokens.ML_BUTTONFORWARD,
        BOTokens.ML_BUTTONSTARTWORKFLOW,
        BOTokens.ML_BUTTONSFINISHWORKFLOW,
        BOTokens.ML_BUTTONSABORTWORKFLOW,
        BOTokens.ML_BUTTONSUNDELETE,
        BOTokens.ML_BUTTONFORWARD,             // multiple operation
        BOTokens.ML_BUTTONGENERATETRANSLATOR,
        BOTokens.ML_BUTTONEDITBEFORECHECKIN,
        BOTokens.ML_BUTTONCHECKINCONTAINER,
        BOTokens.ML_BUTTONCHECKOUTCONTAINER,
        BOTokens.ML_BUTTONEDITBEFORECHECKINCONTAINER,
        BOTokens.ML_BUTTONSEARCHANDREFERENCE,
        BOTokens.ML_BUTTONNEWANDREFERENCE,
        BOTokens.ML_BUTTONPUBLISH,
        BOTokens.ML_BUTTONGOTOCONTAINEREXT,
        BOTokens.ML_BUTTONNEWEXT,
        BOTokens.ML_BUTTONWEBDAVCHECKOUT,
        BOTokens.ML_BUTTONWEBDAVCHECKIN,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
    }; // BTN_NAMES


    // images used to represent the buttons:
    /**
     * Images of buttons used when button active. <BR/>
     */
    public static final String[] BTN_IMAGESACTIVE =
    {
        "",
        "buttonEdit.gif",
        "buttonDelete.gif",
        "buttonCut.gif",
        "buttonCopy.gif",
        "buttonPaste.gif",
        "buttonDistribute.gif",
        "buttonClean.gif",
        "buttonAssign.gif",
        "buttonGoto.gif",
        "buttonSearch.gif",
        "buttonHelp.gif",
        "buttonAnswer.gif",
        "buttonSubmit.gif",
        "buttonCancel.gif",
        "buttonLogin.gif",
        "buttonShoppingCart.gif",
        "buttonDelete.gif",
        "buttonReference.gif",
        "buttonRegister.gif",
        "buttonUnregister.gif",
        "buttonLogin.gif",
        "buttonDelete.gif",
        "buttonShoppingCart.gif",       // order button
        "buttonEdit.gif",
        "buttonDelete.gif",
        "buttonSetRights.gif",
        "buttonCleanLog.gif",
        "buttonSendOrder.gif",
        "buttonRegister.gif",
        "buttonUnregister.gif",
        "buttonPrint.gif",
        "buttonGotoC.gif",
        "buttonBack.gif",
        "buttonNew.gif",
        "buttonDelete.gif",
        "buttonImport.gif",
        "buttonActivate.gif",     // activate button
        "buttonNew.gif",
        "buttonExport.gif",
        "changeState.gif",
        "buttonNew.gif",
        "buttonEdit.gif",
        "buttonAssignRights.gif",
        "buttonCopy.gif",           // multiple operations
        "buttonCut.gif",            // multiple operations
        "buttonCheckOut.gif",
        "buttonCheckIn.gif",
        "buttonForward.gif",
        "buttonStartWorkflow.gif",
        "buttonFinishWorkflow.gif",
        "buttonAbortWorkflow.gif",
        "buttonUnDelete.gif",
        "buttonForward.gif",        // multiple operations
        "buttonGenerateTranslator.gif",
        "buttonEditBeforeCheckIn.gif",
        "buttonCheckInContainer.gif",
        "buttonCheckOutContainer.gif",
        "buttonEditBeforeCheckInContainer.gif",
        "buttonSearchRef.gif",
        "buttonNewRef.gif",
        "buttonPublish.gif",
        "buttonGotoContainer.gif",
        "buttonNew.gif",
        "buttonCheckOut.gif",
        "buttonCheckIn.gif",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
    }; // BTN_IMAGESACTIVE

    /**
     * Images of buttons used when button inactive. <BR/>
     */
    public static final String[] BTN_IMAGESINACTIVE =
    {
        "",
        "buttonEditInactive.jpg",
        "buttonDeleteInactive.jpg",
        "buttonCutInactive.jpg",
        "buttonCopyInactive.jpg",
        "buttonPasteInactive.jpg",
        "buttonDistributeInactive.jpg",
        "buttonCleanInactive.jpg",
        "buttonAssignInactive.jpg",
        "buttonGotoInactive.jpg",
        "buttonSearchInactive.jpg",
        "buttonHelpInactive.jpg",
        "buttonAnswerInactive.jpg",
        "buttonSubmitInactive.jpg",
        "buttonCancelInactive.jpg",
        "buttonLoginInactive.jpg",
        "buttonShoppingCartInactive.jpg",
        "buttonDeleteInactive.jpg",
        "buttonReferenceInactive.jpg",
        "buttonRegisterInactive.jpg",
        "buttonUnregisterInactive.jpg",
        "buttonLoginInactive.jpg",
        "buttonDeleteInactive.jpg",
        "buttonShoppingCartInactive.jpg",       // order button
        "buttonEditInactive.jpg",
        "buttonDeleteInactive.jpg",
        "buttonSetRightsInactive.jpg",
        "buttonCleanLogInactive.jpg",
        "buttonSendOrderInactive.jpg",
        "buttonRegisterInactive.jpg",
        "buttonUnregisterInactive.jpg",
        "buttonPrintInactive.jpg",
        "buttonGotoCInactive.jpg",
        "buttonBackInactive.jpg",
        "buttonNewInactive.jpg",
        "buttonDeleteInactive.jpg",
        "buttonImportInactive.jpg",
        "buttonActivateInactive.gif",
        "buttonNewInactive.jpg",
        "buttonExportInactive.jpg",
        "changeStateInactive.jpg",
        "buttonNewInactive.jpg",
        "buttonEditInactive.jpg",
        "buttonAssignRightsInactive.jpg",
        "buttonCopyInactive.jpg",           // multiple operations
        "buttonCutInactive.jpg",            // multiple operations
        "buttonCheckOutInactive.jpg",
        "buttonCheckInInactive.jpg",
        "buttonForwardInactive.jpg",
        "buttonStartWorkflowInactive.jpg",
        "buttonFinishWorkflowInactive.jpg",
        "buttonAbortWorkflowInactive.jpg",
        "buttonUnDeletelnActive.gif",
        "buttonForwardInactive.jpg",        // multiple operation
        "buttonGenerateTranslatorInactive.jpg",
        "buttonEditBeforeCheckIn.jpg",
        "buttonCheckInContainer.jpg",
        "buttonCheckOutContainer.jpg",
        "buttonEditBeforeCheckInContainer.jpg",
        "buttonSearchRef.gif",
        "buttonNewRef.gif",
        "buttonPublish.jpg",
        "buttonGotoContainer.gif",
        "buttonNew.gif",
        "buttonCheckOutInactive.jpg",
        "buttonCheckInInactive.jpg",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
    }; // BTN_IMAGESACTIVE

    // names of the different buttons:
    /**
     * Description of the different buttons. <BR/>
     */
    public static final String [] BTN_DESCRIPTION =
    {
        "",
        "Objekt bearbeiten",
        "Objekt löschen",
        "Objekt ausschneiden",
        "Objekt kopieren",
        "Objekt einfügen",
        "Objekt verteilen",
        "Abgelaufene Objekte entfernen",
        "Zuordnen",
        "Zu Objekt verzweigen",
        "Suchen im System",
        "Hilfe",
        "Auf Beitrag antworten",
        "OK",
        "Abbrechen",
        "Neu anmelden",
        "Bestellung vormerken",
        "In der Liste löschen",
        "Als Referenz einfügen",
        "Benutzer anmelden",
        "Benutzer abmelden",
        "Ändern des Benutzerkennwortes",
        "Ausgewählte Einträge löschen",
        "Bestellen",
        "Recht bearbeiten",
        "Recht löschen",
        "Rechte allen SubObjekten vererben",
        "Alle Protokolleinträge löschen",
        "Bestellung verschicken",
        "Andere Personen anmelden",
        "Mehrere Personen abmelden",
        "Aktuelles Objekt zum Drucken in neuem Fenster öffnen",
        "Zur Ablage",
        "Zurück",
        "Neue Rechte anlegen",
        "In der Liste löschen",
        "Import",
        "Übernehmen",
        "Neues Thema anlegen",
        "Export",
        "Status ändern",
        "Neues Objekt anlegen",
        "Liste bearbeiten",
        "Rechte zuordnen",
        "Liste kopieren",
        "Liste ausschneiden",
        "Objekt sperren",
        "Objekt entsperren",
        "Objekt weiterleiten",
        "Workflow starten",
        "Workflow abschließen",
        "Workflow abbrechen",
        "Wiederherstellen",
        "Weiterleiten",
        "Translator generieren",
        "Objekt entsperren",
        "Objekt entsperren",
        "Objekt sperren",
        "Objekt entsperren",
        "Suchen-Verlinken",
        "Neu-Verlinken",
        "Objekt publizieren",
        "Zur Ablage",
        "Neu",
        "Objekt sperren",
        "Objekt entsperren",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
    }; // BTN_DESCRIPTION

} // class Buttons

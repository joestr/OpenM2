/*
 * Class: AppFunctions.java
 */

// package:

package ibs.app;

// imports:

/******************************************************************************
 * This is the functions class, which maps functions to numbers. <BR/>
 *
 * @version $Id: AppFunctions.java,v 1.47 2010/04/14 12:53:28 btatzmann Exp $
 *
 * @author Christine Keim (CK), 980318
 ******************************************************************************
 */
public class AppFunctions extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag to
     * ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: AppFunctions.java,v 1.47 2010/04/14 12:53:28 btatzmann Exp $";

    /**
     * No valid function. <BR/>
     */
    public static final int FCT_NOFUNCTION = -1;

    // general functions: 01 .. 99
    /**
     * Show complete layout. <BR/>
     */
    public static final int FCT_LAYOUTPERFORM = 1;

    /**
     * Re-read the configuration file. <BR/>
     */
    public static final int FCT_READCONFIGFILE = 2;

    /**
     * Show login dialog. <BR/>
     */
    public static final int FCT_LOGINFORM = 4;

    /**
     * Perform login. <BR/>
     */
    public static final int FCT_LOGIN = 5;

    /**
     * Perform logout. <BR/>
     */
    public static final int FCT_LOGOUT = 6;

    /**
     * Show change password dialog. <BR/>
     */
    public static final int FCT_CHANGEPASSWORDFORM = 7;

    /**
     * Perform change password. <BR/>
     */
    public static final int FCT_CHANGEPASSWORD = 8;

    /**
     * Show menu. <BR/>
     */
    public static final int FCT_MENUPERFORM = 11;

    /**
     * Change the state of a menu line. <BR/>
     */
    public static final int FCT_MENUCHANGESTATE = 12;

    /**
     * Show tab navigation bar. <BR/>
     */
    public static final int FCT_TABNAVPERFORM = 13;

    /**
     * Change the state of a tab navigation bar. <BR/>
     */
    public static final int FCT_TABNAVCHANGESTATE = 14;

    /**
     * Show list of elements. <BR/>
     */
    public static final int FCT_LISTPERFORM = 21;
    
    /**
     * Retrieve multilang client info (text, messages, buttons). <BR/>
     */
    public static final int FCT_RETRIEVEMLCLIENTINFO = 31;

    /**
     * Show content of business object. <BR/>
     */
    public static final int FCT_SHOWOBJECTCONTENT = 41;

    /**
     * Show content of business object as frameset. <BR/>
     */
    public static final int FCT_SHOWOBJECTCONTENTFRAMESET = 42;

    /**
     * Show content of the LogContainer. <BR/>
     */
    public static final int FCT_SETFILTER = 45;

    /**
     * Show a business object. <BR/>
     */
    public static final int FCT_SHOWOBJECT = 51;

    /**
     * Show previous business object in container. <BR/>
     */
    public static final int FCT_SHOWPREVOBJECT = 52;

    /**
     * Show next business object within container. <BR/>
     */
    public static final int FCT_SHOWNEXTOBJECT = 53;

    /**
     * Show upper business object in hierachy. <BR/>
     */
    public static final int FCT_SHOWUPPEROBJECT = 54;

    /**
     * Show an object being derived from a tab. <BR/>
     */
    public static final int FCT_SHOWTABOBJECT = 55;

    /**
     * Show the info of a business object. <BR/>
     */
    public static final int FCT_SHOWOBJECTINFO = 56;

    /**
     * Show the info of the previous business object in container. <BR/>
     */
    public static final int FCT_SHOWPREVOBJECTINFO = 57;

    /**
     * Show the info of the next business object within container. <BR/>
     */
    public static final int FCT_SHOWNEXTOBJECTINFO = 58;

    /**
     * Show an object that is linked to an object being derived from a tab. <BR/>
     */
    public static final int FCT_SHOWTABLINK = 59;

    /**
     * Show form for creating a new object. <BR/>
     */
    public static final int FCT_OBJECTNEWFORM = 61;

    /**
     * Create a new object. <BR/>
     */
    public static final int FCT_OBJECTNEW = 62;

    /**
     * Create a new object with extended options. <BR/>
     */
    public static final int FCT_OBJECTNEW_EXTENDED = 65;

    /**
     * Show form for changing an existing object. <BR/>
     */
    public static final int FCT_OBJECTCHANGEFORM = 71;

    /**
     * Change an existing object. <BR/>
     */
    public static final int FCT_OBJECTCHANGE = 72;

    /**
     * Change an existing object. <BR/>
     */
    public static final int FCT_OBJECTCHANGE_CHANGEFORM = 73;

    /**
     * Show form and do notification for an existing object. <BR/>
     */
    public static final int FCT_NOTIFICATION = 81;

    /**
     * Show form cleaning expired objects. <BR/>
     */
    public static final int FCT_OBJECTCLEANFORM = 83;

    /**
     * clean expired objects. <BR/>
     */
    public static final int FCT_OBJECTCLEAN = 84;

    /**
     * shows the search result list. <BR/>
     */
    public static final int FCT_OBJECTSEARCH = 85;

    /**
     * clean the LogBook. <BR/>
     */
    public static final int FCT_CLEANLOG = 87;

    /**
     * ???
     */
    public static final int FCT_DISPLAYEMPTYPAGE = 90;

    /**
     * Delete an existing object. <BR/>
     */
    public static final int FCT_OBJECTDELETE = 91;

    /**
     * Show confirm message for deleting an existing object. <BR/>
     */
    public static final int FCT_OBJECTDELETECONFIRM = 92;

    /**
     * Show the filter of the protocolContainer. <BR/>
     */
    public static final int FCT_FILTER = 93;

    /**
     * Show undelelte-form. <BR/>
     */
    public static final int FCT_SHOWUNDELETEFORM = 94;

    /**
     * LIST undelelte-form. <BR/>
     */
    public static final int FCT_LISTUNDELETE = 95;

    /**
     * Show tab navigation bar for an object. <BR/>
     */
    public static final int FCT_OBJECTSHOWTABS = 101;

    /**
     * Change the state of a tab navigation bar for object. <BR/>
     */
    public static final int FCT_OBJECTTABCHANGESTATE = 102;

    /**
     * Show tab navigation bar for the upper object. <BR/>
     */
    public static final int FCT_OBJECTSHOWUPPERTABS = 104;

    /**
     * Show the interface to delete in a list. <BR/>
     */
    public static final int FCT_LISTDELETEFORM = 107;

    /**
     * Deletes marked objects in the listdeleteForm. <BR/>
     */
    public static final int FCT_LISTDELETE = 108;

    /**
     * Show the masterattachment of the attachmentcontainer. <BR/>
     */
    public static final int FCT_SHOWMASTER = 109;

    /**
     * stored oid and CT_CUT. <BR/>
     */
    public static final int FCT_OBJECTCUT = 111;

    /**
     * stored oid and CT_COPY. <BR/>
     */
    public static final int FCT_OBJECTCOPY = 114;

    /**
     * Paste a BusinessObject as link or dublicate it. <BR/>
     */
    public static final int FCT_OBJECTPASTEPERFORM = 115;

    /**
     * signals that the user want to paste and start userdialog. <BR/>
     */
    public static final int FCT_OBJECTPASTE = 117;

    /**
     * signals that the user want to paste and start userdialog. <BR/>
     */
    public static final int FCT_REFERENCE = 118;

    /**
     * Show frame set for uploading file. <BR/>
     */
    public static final int FCT_OBJECTUPLOADFORM = 121;

    /**
     * Upload file. <BR/>
     */
    public static final int FCT_OBJECTUPLOAD = 122;

    /**
     * Show lower frame of uploading frame set. <BR/>
     */
    public static final int FCT_OBJECTUPLOADFORM_DOWN = 124;

    /**
     * Test the upload functionality. <BR/>
     */
    public static final int FCT_OBJECTUPLOAD_TEST = 129;

    /**
     * show the welcome message. <BR/>
     */
    public static final int FCT_SHOWWELCOME = 131;

    /**
     * Go back to the last object. <BR/>
     */
    public static final int FCT_GOBACK = 151;

    /**
     * Show a business object. <BR/>
     */
    public static final int FCT_SHOWRIGHTOBJECT = 153;

    /**
     * show form for changing list contents. <BR/>
     */
    public static final int FCT_LISTCHANGEFORM = 154;

    /**
     * change list contents. <BR/>
     */
    public static final int FCT_LISTCHANGE = 155;

    /**
     * Show form for copying a list of objects. <BR/>
     */
    public static final int FCT_LISTCOPYFORM = 161;

    /**
     * Copy a list of objects. <BR/>
     */
    public static final int FCT_LISTCOPY = 162;

    /**
     * Show form for cutting a list of objects. <BR/>
     */
    public static final int FCT_LISTCUTFORM = 163;

    /**
     * Cut a list of objects. <BR/>
     */
    public static final int FCT_LISTCUT = 164;

    /**
     * Show form for pasting a list of objects. <BR/>
     */
    public static final int FCT_LISTPASTEFORM = 165;

    /**
     * Paste a list of objects. <BR/>
     */
    public static final int FCT_LISTPASTE = 166;

    /**
     * Show form for pasting a list of objects links. <BR/>
     */
    public static final int FCT_LISTLINKPASTEFORM = 167;

    /**
     * Paste a list of object links. <BR/>
     */
    public static final int FCT_LISTPASTELINK = 168;

    /**
     * Show form for distributing a list of objects. <BR/>
     */
    public static final int FCT_LISTDISTRIBUTEFORM = 179;

    /**
     * Distribute a list of objects. <BR/>
     */
    public static final int FCT_LISTDISTRIBUTE = 180;

    /**
     * Perform the distribution of a list of objects. <BR/>
     */
    public static final int FCT_PERFORMLISTDISTRIBUTE = 181;

    ////////////
    //
    // WORKFLOW
    //
    /**
     * Common workflow function. <BR/>
     * Events will be handled in workflow-function-handler
     */
    public static final int FCT_WORKFLOW = 185;

    /**
     * Show the interface to forward from a list. <BR/>
     */
    public static final int FCT_LISTFORWARDFORM = 186;

    /**
     * Multiple forward out of container. <BR/>
     */
    public static final int FCT_LISTFORWARD = 187;

    //
    //
    /////////////

    ////////////
    //
    // OBSERVER
    //
    /**
     * Common observer function requested by an observer-thread. <BR/>
     * Events will be handled in observer-function-handler.
     */
    public static final int FCT_OBSERVER = 190;

    /**
     * Observer function requested by gui-operation. <BR/>
     * Events will be handled in observer-function-handler.
     */
    public static final int FCT_OBSERVERGUI = 191;

    //
    //
    /////////////

    // References: 200 .. 219
    /**
     * create reference between two object. <BR/>
     */
    public static final int FCT_REFERENCENEW = 201;

    /**
     * create reference between two object. <BR/>
     */
    public static final int FCT_REFERENCEDELETE = 202;

    /**
     * create reference from one object to an other. <BR/>
     */
    public static final int FCT_CREATEREFERENCE = 203;

    /**
     * FCT to show search for function search and reference. <BR/>
     */
    public static final int FCT_SHOWSEARCHANDREFERENCE = 211;

    /**
     * FCT to create Reference in link tab of given object. <BR/>
     */
    public static final int FCT_CREATELINKTABREFERENCE = 212;

    /**
     * FCT to show search for function new and reference. <BR/>
     */
    public static final int FCT_SHOWNEWANDREFERENCE = 213;

    /**
     * FCT to show new object form and call FCT_NEWANDREFERNCE after commit.
     * <BR/>
     */
    public static final int FCT_NEWANDREFERENCEFORM = 214;

    /**
     * FCT to create new Object and Reference in link tab of given object. <BR/>
     */
    public static final int FCT_NEWANDREFERENCE = 215;

    // Attachments: 220 .. 239

    /**
     * show form to create a new attachment. <BR/>
     */
    public static final int FCT_ATTACHMENTNEWFORM = 221;

    /**
     * create attachment. <BR/>
     */
    public static final int FCT_ATTACHMENTNEW = 222;

    /**
     * show form to change an attachment. <BR/>
     */
    public static final int FCT_ATTACHMENTCHANGEFORM = 223;

    /**
     * change an attachment. <BR/>
     */
    public static final int FCT_ATTACHMENTCHANGE = 224;

    /**
     * delete an attachment. <BR/>
     */
    public static final int FCT_ATTACHMENTDELETE = 225;

    // common tabs: 251 .. 269
    /**
     * Show a tab of an object. <BR/>
     */
    public static final int FCT_SHOWTAB = 251;

    /**
     * Send all tab info to the client. <BR/>
     */
    public static final int FCT_GETAVAILABLETABS = 252;

    /**
     * Show a view tab of an object. <BR/>
     */
    public static final int FCT_SHOWTABVIEW = 253;

    /**
     * FCT to forward given event to class of current viewtab. <BR/>
     */
    public static final int FCT_FORWARDTABVIEWEVENT = 255;

    // specific tabs: 271 .. 299
    /**
     * Show the private tab of the user object. <BR/>
     */
    public static final int FCT_USERPRIVATETAB = 271;

    /**
     * Sets the rights for the subObject . <BR/>
     */
    public static final int FCT_SETRIGHTSREC = 301;

    /**
     * Perform the simple search. <BR/>
     */
    public static final int FCT_SIMPLESEARCH = 333;

    /**
     * perform a login and an import. used by importagents. <BR/>
     */
    public static final int FCT_AGENTLOGINIMPORT = 390;

    /**
     * perform a login and resolve external ids. Used by exportagents. <BR/>
     */
    public static final int FCT_AGENTLOGINRESTOREEXTERNALIDS = 391;

    /**
     * perform a login and an export. Used by exportagents. <BR/>
     */
    public static final int FCT_AGENTLOGINEXPORT = 395;

    /**
     * Export single object via javasscript 'startExport (...)'. <BR/>
     */
    public static final int FCT_OBJECTEXPORT = 398;

    /**
     * show the import form. <BR/>
     */
    public static final int FCT_SHOWOBJECTIMPORTFORM = 401;

    /**
     * perform an import. <BR/>
     */
    public static final int FCT_OBJECTIMPORT = 402;

    /**
     * show a multiple upload from. <BR/>
     */
    public static final int FCT_SHOWMULTIPLEUPLOADFORM = 403;

    /**
     * show form for exporting a list of objetcs. <BR/>
     */
    public static final int FCT_LISTEXPORTFORM = 405;

    /**
     * show the export form. <BR/>
     */
    public static final int FCT_SHOWEXPORTFORM = 406;

    /**
     * export a list of objects. <BR/>
     */
    public static final int FCT_LISTEXPORT = 407;

    /**
     * ???. <BR/>
     */
    public static final int FCT_SHOWOBJECTINFOFRAMESET = 411;

    /**
     * show selectionlist to select objects for one container. <BR/>
     */
    public static final int FCT_SHOWSELECTFORM = 422;

    /**
     * insert/delete selected/deselected elemets in/from container. <BR/>
     */
    public static final int FCT_HANDLESELECTEDELEMENTS = 423;

    /**
     * activates settings. <BR/>
     */
    public static final int FCT_ACTIVATE = 431;

    /**
     * Checks out an object (blocks editing functions for the other users). <BR/>
     */
    public static final int FCT_CHECKOUT = 441;

    /**
     * Checks an object in (unblocks editing functions for the other users).
     * <BR/>
     */
    public static final int FCT_CHECKIN = 442;

    /**
     * This function is needed for editing first an object (and checking it in
     * later). <BR/>
     */
    public static final int FCT_EDITBEFORECHECKIN = 443;

    /**
     * This checks the checked out file back in the container. <BR/>
     */
    public static final int FCT_CHECKINCONTAINER = 444;

    /**
     * Check out of a file within a container. <BR/>
     */
    public static final int FCT_CHECKOUTCONTAINER = 445;

    /**
     * This checks the checked out file back in the container. <BR/>
     */
    public static final int FCT_EDITBEFORECHECKINCONTAINER = 446;

    /**
     * Checks out an object for WebDAV access (blocks editing functions for the
     * other users). <BR/>
     */
    public static final int FCT_WEBDAVCHECKOUT = 447;

    /**
     * Checks an object in, from WebDAV (unblocks editing functions for the
     * other users). <BR/>
     */
    public static final int FCT_WEBDAVCHECKIN = 448;

    /**
     * First change an object and then check it in (unblock editing functions
     * for the other users). <BR/>
     */
    public static final int FCT_CHANGECHECKIN = 449;

    /**
     * First change an object and then check it in within the container. <BR/>
     */
    public static final int FCT_CHANGECHECKINCONTAINER = 450;

    /**
     * Show a report. <BR/>
     */
    public static final int FCT_GENERATEREPORT = 471;

    /**
     * Download functionality. <BR/>
     */
    public static final int FCT_DOWNLOAD = 461;

    /**
     * Shows the newsContainer without tabs and button!. <BR/>
     */
    public static final int FCT_SHOWNEWSWOTABSBUTTONS = 501;

    /**
     * Shows the frameset for the welcome page (redirect)!. <BR/>
     */
    public static final int FCT_SHOWWELCOMEFRAMESET = 511;

    // functions concerning weblinks:
    /**
     * If the domain doesn't require a secure connection, the session must be
     * changed from secure to insecure. This is done with a redirect and an own
     * function. <BR/>
     */
    public static final int FCT_CHANGETOINSECURESESSION = 665;

    /**
     * Weblink - Function. <BR/>
     */
    public static final int FCT_WEBLINK = 666;

    /**
     * loadWeblink - Function (fuer m2-Objekt in Listenansicht). <BR/>
     */
    public static final int FCT_LOADWEBLINKURL = 667;

    // functions concerning translator creating for converting form-templates:
    /**
     * Show the form for creating a new translator. <BR/>
     */
    public static final int FCT_SHOWGENERATETRANSLATORFORM = 776;

    /**
     * Perform the generating of an new translator for forms. <BR/>
     */
    public static final int FCT_GENERATETRANSLATOR = 777;

    // functions 801 ... 810 used for installer
    /**
     * Install a component. <BR/>
     */
    public static final int FCT_INSTALL = 801;

    /**
     * Forward event to current BusinessObject. <BR/>
     */
    public static final int FCT_FORWARDEVENT = 900;

    // dummy functions:
    /**
     * Dummy function for future use. <BR/>
     */
    public static final int FCT_DUMMYFUNCTION01 = 901;

    /**
     * Dummy function for future use. <BR/>
     */
    public static final int FCT_DUMMYFUNCTION02 = 902;

    /**
     * Dummy function for future use. <BR/>
     */
    public static final int FCT_DUMMYFUNCTION03 = 903;

    /**
     * Dummy function for future use. <BR/>
     */
    public static final int FCT_DUMMYFUNCTION04 = 904;

    /**
     * Dummy function for future use. <BR/>
     */
    public static final int FCT_DUMMYFUNCTION05 = 905;

    /**
     * Dummy function for future use. <BR/>
     */
    public static final int FCT_DUMMYFUNCTION06 = 906;

    /**
     * Dummy function for future use. <BR/>
     */
    public static final int FCT_DUMMYFUNCTION07 = 907;

    /**
     * Dummy function for future use. <BR/>
     */
    public static final int FCT_DUMMYFUNCTION08 = 908;

    /**
     * Dummy function for future use. <BR/>
     */
    public static final int FCT_DUMMYFUNCTION09 = 909;

    /**
     * Dummy function for future use. <BR/>
     */
    public static final int FCT_DUMMYFUNCTION10 = 910;

    /**
     * Create a VCard. <BR/>
     */
    public static final int FCT_GENERATEVCARD = 980;

    /**
     * Create a VCard. <BR/>
     */
    public static final int FCT_GENERATEVCALENDAR = 990;


} // class AppFunctions

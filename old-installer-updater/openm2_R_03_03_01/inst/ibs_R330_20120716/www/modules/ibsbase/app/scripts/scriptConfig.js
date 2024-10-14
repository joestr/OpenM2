/******************************************************************************
 * This file contains the configuration parameters for application startup.
 *
 * @version     2.23.0001, 08.03.2002
 *
 * @author      Klaus Reimüller (KR)  020308
 ******************************************************************************
 */


//============= configuration values for the offline mode =====================
// These values are only used if the application is started in offline mode.
// They must be defined before the initialization of the system.

/**
 * Directory for the offline files (relative to /ibsbase/app/).
 */
var OFFLINE_DATA = "offlinedata/";


/**
 * This array contains an url for each tab id which is supported in the offline
 * mode. If a tab shall not be supported in offline mode it can be dropped out
 * of this list.
 * If a tab is selected in the offline mode the corresponding file is found by
 * concatenating the url to the oid.
 * <fileName> ::= <oidString>_<tabIdUrl>.html
 * e.g.: "0x01010021000001EF_info.html"
 */
var tabIdUrls = new Array
(
    1, "content",
    2, "content",
    5, "info",
    33, "rights",
    34, "log",
    4, "day",
    3, "month",
    22, "prices"
) // tabIdUrls


//============= standard configuration ========================================

/**
 * Define the major system entry point.
 * initSystem (String appName, boolean isOnline)
 * appName      The application which shall be called, e.g.
 *              "ApplicationServlet".
 * isOnline     true if the system is running in online mode, otherwise false.
 */
//initSystem (null, true);
initSystem ("#CONFVAR.ibsbase.appServlet#", true);

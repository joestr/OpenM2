/*
 * Class: WebdavData.java
 */

// package:
package ibs.obj.webdav;

//imports:
import ibs.bo.BOHelpers;
import ibs.bo.OID;
import ibs.io.session.ApplicationInfo;
import ibs.service.conf.Configuration;
import ibs.service.user.User;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/******************************************************************************
 * This class contains the data needed for WebDav. <BR/>
 *
 * @version     $Id: WebdavData.java,v 1.7 2009/08/28 15:02:01 kreimueller Exp $
 *
 * @author      Klaus, 18.11.2003
 ******************************************************************************
 */
public class WebdavData
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WebdavData.java,v 1.7 2009/08/28 15:02:01 kreimueller Exp $";


    /**
     * The path to the upload/files directory. <BR/>
     */
    public String p_filesDir = null;

    /**
     * Path to webdav root + user's webdav directory. <BR/>
     */
    public String p_webdavUserDir = null;

    /**
     * path to webdav root + user's webdav directory + directory of the
     * object. <BR/>
     */
    public String p_webdavObjectDir = null;

    /**
     * path to webdav root + user's webdav directory + directory of the
     * object without file separator at the end. <BR/>
     */
    public String p_webdavObjectDirNoSep = null;


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////

    /***************************************************************************
     * Generates an webdav filename. This is a filename with the oid prefix
     * stripped and the checkout key added instead.<BR/>
     *
     * @param   filename    The original filename.
     * @param   date        The date/time used for creating the key.
     *
     * @return  The generated webdav filename.
     */
    public static String getWebdavFilename (String filename, Date date)
    {
        String webdavFilename = filename;

        // BB: stripping off the OID leads to problems because the full filename
        // will still be used when custructing the wedav url!!!
/*
        // check if the oid is included in the filename
        // and strip of leading oid is applicable:
        if (filename.length () > 18 &&
            filename.startsWith (UtilConstants.NUM_START_HEX))
                                        // oid included?
        {
            webdavFilename = filename.substring (18);
        } // if oid included
*/
        // add the checkout date as unique prefix
        webdavFilename = WebdavData.getDateTimeKey (date) + "_" + webdavFilename;
        // return the result
        return webdavFilename;
    } // getWebdavFilename


    /***************************************************************************
     * Generates a DateTimeKey that has the format "YYYYMMDDHHMMsss"
     * using the checked out date of the business object.<BR/>
     *
     * @param   date    The date/time used for creating the key.
     *
     * @return  The key or
     *          <CODE>""</CODE> in case no date has been set.
     */
    public static String getDateTimeKey (Date date)
    {
        // has a checked out date been set?
        if (date != null)
        {
            GregorianCalendar calendar = new GregorianCalendar ();
            calendar.setTime (date);

            return "" +
                calendar.get (Calendar.YEAR) +
                (calendar.get (Calendar.MONTH) + 1) +
                calendar.get (Calendar.DAY_OF_MONTH) +
                calendar.get (Calendar.HOUR_OF_DAY) +
                calendar.get (Calendar.MINUTE) +
                calendar.get (Calendar.SECOND) +
                calendar.get (Calendar.MILLISECOND);
        } // if (this.checkOutDate != null)

        // no checkedout date set
        return "";
    } // getDateTimeKey


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a WebdavData object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   appInfo The global application info object.
     * @param   oid     The oid of the current object.
     * @param   user    The actual user.
     */
    public WebdavData (ApplicationInfo appInfo, OID oid, User user)
    {
        // call constructor of super class:
        super ();

        // initialize the object:
        this.init (appInfo, oid, user);
    } // WebdavData


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Initialize the webdav data object. <BR/>
     *
     * @param   appInfo The global application info object.
     * @param   oid     The oid of the current object.
     * @param   user    The actual user.
     */
    private void init (ApplicationInfo appInfo, OID oid, User user)
    {
        String webdavpath = new String (((Configuration) appInfo.configuration).getWebDavPath ());

        // path to m2/upload/files:
        this.p_filesDir = BOHelpers.getFilePath (oid);
        // path to webdav root + user's webdav directory:
        this.p_webdavUserDir = webdavpath + File.separator +
                               user.oid.toString () + File.separator;
        // path to webdav root + user's webdav directory + directory of the
        // object without separator at the end:
        this.p_webdavObjectDirNoSep = this.p_webdavUserDir +
                                      oid.toString ();
        // path to webdav root + user's webdav directory + directory of the
        // object:
        this.p_webdavObjectDir = this.p_webdavObjectDirNoSep +
                                 File.separator;
    } // init

} // class WebdavData

/**
 * Class: AppInfoSingleton.java
 */

// package:
package ibs.app;

// imports:
import ibs.BaseObject;
import ibs.io.session.ApplicationInfo;


/******************************************************************************
 * This is the AppInfoSingleton. <BR/>
 *
 * @version     $Id: AppInfoSingleton.java,v 1.8 2007/07/20 12:41:51 kreimueller Exp $
 *
 * @author      Daniel Janesch (DJ), 03.06.2002
 ******************************************************************************
 */
public final class AppInfoSingleton extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: AppInfoSingleton.java,v 1.8 2007/07/20 12:41:51 kreimueller Exp $";


    /**
     * Single instance of the AppInfoSingleton. <BR/>
     */
    private static AppInfoSingleton instance;

    /**
     * The application info from where the connection data is collected. <BR/>
     */
    private static ApplicationInfo appInfo;


    /**************************************************************************
     * This constructor creates a new instance of the class AppInfoSingleton. <BR/>
     */
    private AppInfoSingleton ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // AppInfoSingleton


    /**************************************************************************
     * Returns the single instance of the singleton
     *
     * @return  The singleton.
     */
    public static AppInfoSingleton getInstance ()
    {
        if (AppInfoSingleton.instance == null)
        {
            AppInfoSingleton.instance = new AppInfoSingleton ();
        } // if
        return AppInfoSingleton.instance;
    } // getInstance


    /**************************************************************************
     * Sets the application info for the singleton
     *
     * @param   app     The global application info.
     */
    public void setAppInfo (ApplicationInfo app)
    {
        AppInfoSingleton.appInfo = app;
    } // setAppInfo


    /**************************************************************************
     * Gets the application info from the singleton
     *
     * @return  app     The global application info.
     */
    public ApplicationInfo getAppInfo ()
    {
        return AppInfoSingleton.appInfo;
    } // getAppInfo


    /***************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // can be overwritten in sub classes
    } // initClassSpecifics

} // class AppInfoSingleton

/*
 * Class ApplicationServlet
 */

// package:
package ibs.app;

// imports:
import ibs.bo.cache.CacheException;
import ibs.bo.cache.ObjectPool;
import ibs.extdata.APIConnectionPool;
import ibs.io.IOHelpers;
import ibs.io.servlet.ApplicationInitializationException;
import ibs.io.servlet.BaseServlet;
import ibs.io.servlet.SessionInitializationException;
import ibs.io.session.ServletSessionInfo;
import ibs.io.session.SessionInfo;
import ibs.obj.query.QueryPool;
import ibs.obj.search.SimpleSearchData;
import ibs.service.conf.ConfigurationConstants;
import ibs.service.conf.ServerRecord;


/******************************************************************************
 * This is the ApplicationServlet. <BR/>
 *
 * @version     $Id: ApplicationServlet.java,v 1.28 2007/07/20 12:41:51 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 990303
 ******************************************************************************
 */
public class ApplicationServlet extends BaseServlet
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ApplicationServlet.java,v 1.28 2007/07/20 12:41:51 kreimueller Exp $";


    /**
     * Serializable version number. <BR/>
     * This value is used by the serialization runtime during deserialization
     * to verify that the sender and receiver of a serialized object have
     * loaded classes for that object that are compatible with respect to
     * serialization. <BR/>
     * If the receiver has loaded a class for the object that has a different
     * serialVersionUID than that of the corresponding sender's class, then
     * deserialization will result in an {@link java.io.InvalidClassException}.
     * <BR/>
     * This field's value has to be changed every time any serialized property
     * definition is changed. Use the tool serialver for that purpose.
     */
    static final long serialVersionUID = 4199690684802472726L;



    /**************************************************************************
     * This method initializes the global application info. <BR/>
     *
     * @throws  ApplicationInitializationException
     *          An exception occurred during initialization.
     */
    public void initApplicationInfo () throws ApplicationInitializationException
    {
        // call super initializer:
        super.initApplicationInfo ();

        try
        {
            // initialize the application info:
            // create a cache for the specified number of objects:
            this.p_app.cache = new ObjectPool (AppConstants.CACHE_SIZE, this.p_app);
            // create a pool for all systemqueries
            // (filled in Application.onAppStart ())
            this.p_app.queryPool = new QueryPool ();
            // create a pool for all apiconnections
            // (filled in Application.onAppStart ())
            this.p_app.apiConPool = new APIConnectionPool ();
        } // try
        catch (CacheException e)
        {
            IOHelpers.printError ("Error when initializing the servlet: ",
                this, e, true);
            throw new ApplicationInitializationException (e);
        } // catch
    } // initApplicationInfo


    /**************************************************************************
     * Get the name of the application class to be called for performing the
     * required operation. <BR/>
     * The class name must include the package information in the form
     * <CODE>pkg1.pkg2.className</CODE>.
     *
     * @return  The class name for the application object.
     */
    public String getAppClassName ()
    {
        // return the class name:
        return "ibs.app.Application";
    } // getAppClassName


    /**************************************************************************
     * This method creates and initializes a new session info object. <BR/>
     * This object is intended to hold the data which is valid within one user
     * session.
     *
     * @return  The created session info object.
     *
     * @throws  SessionInitializationException
     *          There occurred an error during initializating the session.
     */
    protected SessionInfo createSessionInfo ()
        throws SessionInitializationException
    {
        SessionInfo session = null;     // the actual session to be used

        // get and set a new session object:
        session = new ServletSessionInfo ();
        session.simpleSearchData = new SimpleSearchData ();
        session.actServerConfiguration =
            new ServerRecord ("",
                              "",
                              Integer.toString (ConfigurationConstants.APPLICATIONSERVERPORT_DEFAULT),
                              Integer.toString (ConfigurationConstants.SSLSERVERPORT_DEFAULT),
                                      new String ("false"));
        session.userInfo = new UserInfo ();

        // return the result:
        return session;
    } // createSessionInfo


    /**************************************************************************
     * Returns information about the servlet, such as author, version, and
     * copyright.
     *
     * @return String information about this servlet.
     */
    public String getServletInfo ()
    {
        return "The Application Servlet";
    } // getServletInfo

} // class ApplicationServlet

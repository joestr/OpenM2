/*
 * Class: IApplicationInitializer.java
 */

// package:
package ibs.io.session;

// imports:
import ibs.io.servlet.ApplicationInitializationException;
import ibs.io.servlet.IApplicationContext;


/******************************************************************************
 * This class.... <BR/>
 *
 * @version     $Id: IApplicationInitializer.java,v 1.6 2010/05/11 11:16:06 btatzmann Exp $
 *
 * @author      Klaus, 22.12.2003
 ******************************************************************************
 */
public interface IApplicationInitializer
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IApplicationInitializer.java,v 1.6 2010/05/11 11:16:06 btatzmann Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Method which is called once for the Application. <BR/>
     * It fills the language specific files and makes other initializations
     * to be done at application startup.
     *
     * @param   context     The application context.
     *
     * @throws  ApplicationInitializationException
     *          An exception occurred during the application initialization.
     */
    public void initApplication (IApplicationContext context)
        throws ApplicationInitializationException;


    /**************************************************************************
     * Reload all types. <BR/>
     * This method should be called when there are some types changed during
     * runtime.
     *
     * @param   context             The application context.
     * @param   onlyMultilangInfo   Indicates if only the multilang info should
     *                              be reloaded.
     *
     * @throws  ApplicationInitializationException
     *          An error occurred during getting the type info.
     */
    public void reloadTypes (IApplicationContext context, boolean onlyMultilangInfo)
        throws ApplicationInitializationException;
    
    
    /**************************************************************************
     * Reload all queries. <BR/>
     * This method should be called when there are some queries or query
     * texts changed during runtime.
     *
     * @param   context             The application context.
     * @param   onlyMultilangInfo   Indicates if only the multilang info should
     *                              be reloaded.
     *
     * @throws  ApplicationInitializationException
     *          An error occurred during getting the type info.
     */
    public void reloadQueries (IApplicationContext context, boolean onlyMultilangInfo)
        throws ApplicationInitializationException;
    

    /**************************************************************************
     * Reloads the preloaded MLI texts for the client and other preloaded
     * resource bundles. <BR/>
     * 
     * For reloading MLI texts for types reloadTypes (appCtx, true) has to
     * be called. 
     * 
     * This method should be called when there are some mli text changes
     * during runtime.
     *
     * @param   context             The application context.
     *
     * @throws  ApplicationInitializationException
     *          An error occurred during reloading the mli client texts.
     */
    public void reloadPreloadedMliTexts (IApplicationContext context)
        throws ApplicationInitializationException;
} // interface IApplicationInitializer

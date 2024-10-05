/**
 * Class: NotificationServiceFactory.java
 */

// package:
package ibs.service.notification;

import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.service.conf.IConfiguration;


// imports:

/******************************************************************************
 * Factory for INotificationService implementations. <BR/>
 *
 * @version     $Id: NotificationServiceFactory.java,v 1.2 2010/11/12 10:19:36 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann (BT) 20101110
 ******************************************************************************
 */
public final class NotificationServiceFactory
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: NotificationServiceFactory.java,v 1.2 2010/11/12 10:19:36 btatzmann Exp $";

    /**
     * Singleton instance of NotificationServiceFactory
     */
    private static NotificationServiceFactory notificationServiceFactory = null;
    
    
    /**
     * Holds the ibsbase confvar key for the notfication service classname.
     */
    private static final String CONFVAR_KEY_NOTIFICATION_SERVICE_CLASSNAME = "ibsbase.notificationServiceClassname";
    

    /**
     * Holds the class for the standard notification service.
     */
    private static final Class STANDARD_NOTIFICATION_SERVICE_CLASS = NotificationService.class;
  
    
    /**
     * Holds the default service class to be used 
     */
    private static Class<INotificationService> defaultServiceClass = STANDARD_NOTIFICATION_SERVICE_CLASS;

    
    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    /**************************************************************************
     * This constructor is just to ensure that there is no default constructor
     * generated during compilation. <BR/>
     */
    private NotificationServiceFactory ()
    {
        // nothing to do
    } // NotificationServiceFactory
    
    
    /**
     * This constructor initializes the notification service factory.
     * 
     * It computes the default service class to be used during process,
     * when no special service class is provided.
     *
     * @param env   the environment
     */
    private NotificationServiceFactory (Environment env)
    {
        INotificationService notificationServiceTestInstance = null;
        
        // retrieve the default notification service implementation from ibsbase.xml
        String serviceClassNameLocal = ((IConfiguration) env.getApplicationInfo ().configuration)
            .getConfVars ().getValue (CONFVAR_KEY_NOTIFICATION_SERVICE_CLASSNAME);
        
        // check if something has been configured
        if (serviceClassNameLocal != null && serviceClassNameLocal.trim ().length () > 0)
        {
            // check if the configured class name is valid
            Class<INotificationService> serviceClassLocal = getServiceClassForName (serviceClassNameLocal);
            
            if (serviceClassLocal != null)
            {
                // check if the configured class can be instantiated
                notificationServiceTestInstance = getServiceInstance (serviceClassLocal);
                
                if (notificationServiceTestInstance != null)
                {
                    // if successful set the service class as default class
                    defaultServiceClass = serviceClassLocal;
                } // if
            } // if
        } // if
    } // NotificationServiceFactory
    
    
    /**
     * Returns the singletone service factory instance.
     *
     * @param env
     * @return
     */
    public static NotificationServiceFactory getInstance (Environment env)
    {
        if (notificationServiceFactory == null)
        {
            notificationServiceFactory = new NotificationServiceFactory (env);
        } // if
        
        return notificationServiceFactory;
    } // getInstance
    
    
    /**************************************************************************
     * Returns the standard INoticationService implementation. <BR/>
     *
     * @return  The standard notification service.
     */
    public INotificationService getNotificationService ()
    {
        return this.getNotificationService (null);
    } // getNotificationService


    /**************************************************************************
     * Returns a INoticationService instance for the given class. <BR/>
     *
     * @param serviceClass    The desired INoticationService implementation
     *
     * @return  The notification service.
     */
    public INotificationService getNotificationService (Class serviceClass)
    {    
        // check if a notification service class is set
        if (serviceClass != null)
        {
            // check if the custom class can be instantiated
            INotificationService customNotificationService = getServiceInstance (serviceClass);
            
            if (customNotificationService != null)
            {
                // return the custom class instance
                return customNotificationService;
            } // if
        } // if

        // Default handling: return a default notification service instance
        return getServiceInstance (defaultServiceClass);
    } // getNotificationService
    
    
    /**
     * Return an INotificationService instance for the given service class name.
     *
     * @param serviceClassName
     * @return
     */
    private static INotificationService getServiceInstance (Class serviceClass)
    {
        INotificationService notificationService = null;
        
        try
        {           
            notificationService = (INotificationService) serviceClass.newInstance ();
        } // try
        catch (InstantiationException e)
        {
            IOHelpers.printError (
                    "Error during instantiating INotificationService instance for serviceClassName="
                        + serviceClass.getName (), e, true);
        } // catch
        catch (IllegalAccessException e)
        {
            IOHelpers.printError (
                    "Error during instantiating INotificationService instance for serviceClassName="
                        + serviceClass.getName (), e, true);
        } // catch
        
        return notificationService;
    } // getServiceInstance
    
    
    /**
     * Return an INotificationService instance for the given service class name.
     *
     * @param serviceClassName
     * @return
     */
    private static Class<INotificationService> getServiceClassForName (String serviceClassName)
    {
        Class<INotificationService> serviceClass = null;
        
        try
        {           
            serviceClass = (Class<INotificationService>) Class.forName (serviceClassName);
        } // try
        catch (ClassNotFoundException e)
        {
            IOHelpers.printError (
                    "Error during instantiating INotificationService instance for serviceClassName="
                        + serviceClassName, e, true);
        } // catch
        
        return serviceClass;
    } // getServiceClassForName
} // NotificationServiceFactory

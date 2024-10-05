/*
 * Created by IntelliJ IDEA.
 * User: hpichler
 * Date: 19.08.2002
 * Time: 09:36:21
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */

// package:
package ibs.service.observer;

// imports:
import ibs.service.observer.ObserverConfiguration;
import ibs.service.observer.ObserverException;

import org.w3c.dom.Node;


/******************************************************************************
 * Holds m2ObserverConfiguration-data and implements methods to load these from
 * an xml file.
 *
 * @version     $Id: M2ObserverConfiguration.java,v 1.1 2007/07/24 21:28:12 kreimueller Exp $
 *
 * @author      HORST PICHLER, 19.08.2002
 ******************************************************************************
 */
public class M2ObserverConfiguration extends ObserverConfiguration
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: M2ObserverConfiguration.java,v 1.1 2007/07/24 21:28:12 kreimueller Exp $";


    /**
     * The name of the M2CONNECTION tag in the configuration. <BR/>
     */
    protected static final String TAG_M2CONNECTION = "M2CONNECTION";
    /**
     * The name of the  TYPE tag in the configuration. <BR/>
     */
    protected static final String TAG_M2CONNECTION_TYPE = "M2TYPE";
    /**
     * Value SERVLET for TYPE:
     */
    protected static final String TYPE_SERVLET = "SERVLET";
    /**
     * Value ASP for TYPE:
     */
    protected static final String TYPE_ASP = "ASP";
    /**
     * The name of the SERVER tag in the configuration. <BR/>
     */
    protected static final String TAG_M2CONNECTION_SERVER = "M2SERVER";
    /**
     * The default-value of the SERVER tag in the configuration:
     */
    protected static final String DEFAULT_SERVER = "localhost";
    /**
     * The name of the APPPATH tag in the configuration. <BR/>
     */
    protected static final String TAG_M2CONNECTION_APPPATH = "M2APPPATH";
    /**
     * The default-value of the APPPATH tag in the configuration:
     */
    protected static final String DEFAULT_APPPATH = "/m2/app";
    /**
     * The name of the DOMAIN tag in the configuration. <BR/>
     */
    protected static final String TAG_M2CONNECTION_DOMAIN = "M2DOMAIN";
    /**
     * The default-value of the DOMAIN tag in the configuration:
     */
    protected static final int DEFAULT_DOMAIN = 1;
    /**
     * The name of the USERNAME tag in the configuration. <BR/>
     */
    protected static final String TAG_M2CONNECTION_USERNAME = "M2USERNAME";
    /**
     * The name of the PASSWORD tag in the configuration. <BR/>
     */
    protected static final String TAG_M2CONNECTION_PASSWORD = "M2PASSWORD";
    /**
     * The name of the PASSWORD tag in the configuration. <BR/>
     */
    protected static final String TAG_M2CONNECTION_TIMEOUT = "M2TIMEOUT";
    /**
     * The default-value of the REFRESH tag in the configuration:
     * 30000 ms = 30 seconds.
     */
    protected static final int DEFAULT_TIMEOUT = 300000;

    /**
     * type: ASP|SERVLET
     */
    private String p_m2ConnectionType = M2ObserverConfiguration.TYPE_SERVLET;
    /**
     * server: name of m2- (web)server
     */
    private String p_m2ConnectionServer = M2ObserverConfiguration.DEFAULT_SERVER;
    /**
     * apppath: url-subpath
     */
    private String p_m2ConnectionApppath = M2ObserverConfiguration.DEFAULT_APPPATH;
    /**
     * domain: id of the m2-domain
     */
    private int p_m2ConnectionDomain = M2ObserverConfiguration.DEFAULT_DOMAIN;
    /**
     * username: of the m2-user
     */
    private String p_m2ConnectionUsername = null;
    /**
     * password: of the m2-user
     */
    private String p_m2ConnectionPassword = null;
    /**
     * timeout: of m2connection
     */
    private int p_m2ConnectionTimeout = M2ObserverConfiguration.DEFAULT_TIMEOUT;


    //
    // constructors
    //

    /**************************************************************************
     * Constructor for an ObserverConfiguration object. <BR/>
     */
    public M2ObserverConfiguration ()
    {
        // nothing to do
    } // m2ObserverConfiguration


    //
    // getters
    //

    /**************************************************************************
     * Get the connection type. <BR/>
     *
     * @return  The connection type.
     */
    public String getM2ConnectionType ()
    {
        return this.p_m2ConnectionType;
    } // getM2ConnectionType


    /**************************************************************************
     * Get the connection server. <BR/>
     *
     * @return  The name of the connection server.
     */
    public String getM2ConnectionServer ()
    {
        return this.p_m2ConnectionServer;
    } // getM2ConnectionServer


    /**************************************************************************
     * Get the path of the conection application. <BR/>
     *
     * @return  The path.
     */
    public String getM2ConnectionApppath ()
    {
        return this.p_m2ConnectionApppath;
    } // getM2ConnectionApppath


    /**************************************************************************
     * Get the domain of the connection. <BR/>
     *
     * @return  The domain id.
     */
    public int getM2ConnectionDomain ()
    {
        return this.p_m2ConnectionDomain;
    } // getM2ConnectionDomain


    /**************************************************************************
     * Get the user name for the connection. <BR/>
     *
     * @return  The user name.
     */
    public String getM2ConnectionUsername ()
    {
        return this.p_m2ConnectionUsername;
    } // getM2ConnectionUsername


    /**************************************************************************
     * Get the password for the connection. <BR/>
     *
     * @return  The password.
     */
    public String getM2ConnectionPassword ()
    {
        return this.p_m2ConnectionPassword;
    } // getM2ConnectionPassword


    /**************************************************************************
     * Get the connection timeout. <BR/>
     *
     * @return  The timeout.
     */
    public int getM2ConnectionTimeout ()
    {
        return this.p_m2ConnectionTimeout;
    } // getM2ConnectionTimeout


    /**************************************************************************
     * Extract data from given OBSERVER-element and set it in this object. <BR/>
     *
     * @param   node    The node with the configuration data.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void setConfigurationData (Node node) throws ObserverException
    {
        Node child = null;

        super.setConfigurationData (node);

        // <m2CONFIGURATION>
        child = ObserverConfiguration.getChildNodeByName (node,
            M2ObserverConfiguration.TAG_M2CONNECTION, true);
        if (child != null)
        {
            this.setm2ConnectionData (child);
        } // if

    } // setConfigurationData


    /**************************************************************************
     * Extract data from given m2C-element and set it in this object. <BR/>
     *
     * @param   node    The node with the connection data.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void setm2ConnectionData (Node node) throws ObserverException
    {
        Node child = null;
        String v = null;

        // check if given node is BASE-element
        if (!M2ObserverConfiguration.TAG_M2CONNECTION.equals (node.getNodeName ()))
        {
            throw new ObserverException ("Error while setting configuration: " +
                    " Expected OBSERVER:M2CONNECTION-element, but got " + node.getNodeName () + ".");
        } // if

        //
        // extract information from childnodes
        //
        // <TYPE>
        child = ObserverConfiguration.getChildNodeByName (node,
            M2ObserverConfiguration.TAG_M2CONNECTION_TYPE, false);
        v = ObserverConfiguration.getNodeText (child, "OBSERVER:M2CONNECTION:TYPE", false);
        if (v == null)
        {
            this.p_m2ConnectionType = M2ObserverConfiguration.TYPE_SERVLET;
        } // if
        else
        {
            this.p_m2ConnectionType = v;
        } // else
        // <SERVER>
        child = ObserverConfiguration.getChildNodeByName (node,
            M2ObserverConfiguration.TAG_M2CONNECTION_SERVER, false);
        v = ObserverConfiguration.getNodeText (child, "OBSERVER:M2CONNECTION:SERVER", false);
        if (v == null)
        {
            this.p_m2ConnectionServer = M2ObserverConfiguration.DEFAULT_SERVER;
        } // if
        else
        {
            this.p_m2ConnectionServer = v;
        } // else
        // <APPPATH>
        child = ObserverConfiguration.getChildNodeByName (node,
            M2ObserverConfiguration.TAG_M2CONNECTION_APPPATH, false);
        v = ObserverConfiguration.getNodeText (child,
            "OBSERVER:M2CONNECTION:APPPATH", false);
        if (v == null)
        {
            this.p_m2ConnectionApppath = M2ObserverConfiguration.DEFAULT_APPPATH;
        } // if
        else
        {
            this.p_m2ConnectionApppath = v;
        } // else
        // <DOMAIN>
        child = ObserverConfiguration.getChildNodeByName (node,
            M2ObserverConfiguration.TAG_M2CONNECTION_DOMAIN, false);
        v = ObserverConfiguration.getNodeText (child,
            "OBSERVER:M2CONNECTION:DOMAIN", false);
        if (v == null)
        {
            this.p_m2ConnectionDomain = M2ObserverConfiguration.DEFAULT_DOMAIN;
        } // if
        else
        {
            this.p_m2ConnectionDomain = this.stringToInt (v);
        } // else
        // <USERNAME>
        child = ObserverConfiguration.getChildNodeByName (node,
            M2ObserverConfiguration.TAG_M2CONNECTION_USERNAME, true);
        v = ObserverConfiguration.getNodeText (child,
            "OBSERVER:M2CONNECTION:USERNAME", true);
        this.p_m2ConnectionUsername = v;
        // <PASSWORD>
        child = ObserverConfiguration.getChildNodeByName (node,
            M2ObserverConfiguration.TAG_M2CONNECTION_PASSWORD, true);
        v = ObserverConfiguration.getNodeText (child,
            "OBSERVER:M2CONNECTION:PASSWORD", true);
        this.p_m2ConnectionPassword = v;
        // <TIMEOUT>
        child = ObserverConfiguration.getChildNodeByName (node,
            M2ObserverConfiguration.TAG_M2CONNECTION_TIMEOUT, false);
        v = ObserverConfiguration.getNodeText (child,
            "OBSERVER:M2CONNECTION:TIMEOUT", false);
        if (v == null)
        {
            this.p_m2ConnectionTimeout = M2ObserverConfiguration.DEFAULT_TIMEOUT;
        } // if
        else
        {
            this.p_m2ConnectionTimeout = this.stringToInt (v);
        } // else
    } // setm2ConnectionData


    /**************************************************************************
     * Returns a string representation of the object. <BR/>
     *
     * @return  a string representation of the object.
     */
    public String toString ()
    {
        return "[basedata=" + super.toString () + "; m2connection=[server=" + this.p_m2ConnectionServer +
                "; apppath=" + this.p_m2ConnectionApppath + "; type=" + this.p_m2ConnectionType +
                "; domain=" + this.p_m2ConnectionDomain + "; user=" + this.p_m2ConnectionUsername +
                "; password=" + this.p_m2ConnectionPassword + "; timeout=" + this.p_m2ConnectionTimeout + "]]";
    } // toString

} // M2ObserverConfiguration

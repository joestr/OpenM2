/**
 * Class: TracerConf
 */

// package:
package ibs.service.conf;

// imports:
import ibs.service.conf.AConfigurationContainer;
import ibs.service.conf.ConfigurationConstants;
import ibs.util.trace.TracerConstants;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Vector;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/******************************************************************************
 * .
 *
 * @version     $Id: TracerConf.java,v 1.5 2007/07/31 19:13:58 kreimueller Exp ${cursor}
 *
 * @author      Bernd Martin (BM) Oct 22, 2001
 ******************************************************************************
 */
public  class TracerConf extends AConfigurationContainer
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TracerConf.java,v 1.5 2007/07/31 19:13:58 kreimueller Exp $";


    // tracer:
    /**
     * Is the trace enabled? <BR/>
     * Default: <CODE>false</CODE>
     */
    protected boolean active = false;

    /**
     * The directory where the tracing files shall be written. <BR/>
     * Default: <CODE>"c:\Inetpub\wwwroot\m2\debug\"</CODE>
     */
    protected String path = TracerConstants.TRACERPATH_DEFAULT;

    /**
     * The port of the trace server. <BR/>
     * Default: <CODE>-1</CODE>
     */
    protected int serverPort = -1;

    /**
     * The name of the trace server. <BR/>
     */
    protected String serverName = TracerConstants.TRACESERVER_DEFAULT;

    /**
     * This password is used to authenticate the tracer clients. <BR/>
     * It is read from the configuration file ibssystem.cfg. <BR/>
     * Default:
     *      <CODE>ibs.util.trace.TracerConstants.TRACERPASSWORD_DEFAULT</CODE>
     */
    protected byte[] password = TracerConstants.TRACERPASSWORD_DEFAULT;

    /**
     * Maximum number of messages to be sent to the clients by the trace server.
     * <BR/>
     * If the current number of messages exceeds this value the other messages
     * are discarded and a message is send to the client. <BR/>
     * Default: <CODE>100</CODE>
     */
    protected int maxMessages = 100;


    /***************************************************************************
     * Only the first node will be set for the tracer settings.
     * The other nodes are ignored. <BR/>
     *
     * @param dbs The nodelist which contains the tracer settings. Only the
     *            first node will be taken.
     */
    protected void setTracerConfiguration (NodeList dbs)
    {
        Node db = dbs.item (0);
        NamedNodeMap attributes = db.getAttributes ();
        String nodeValue = null;

        // set trace server name class for database
        if (attributes.getNamedItem (ConfigurationConstants.TOK_ACTIVE) != null)
        {
            nodeValue = attributes.getNamedItem (
                ConfigurationConstants.TOK_ACTIVE).getNodeValue ();
            if (nodeValue != null)
            {
                this.active = Boolean.valueOf (nodeValue).booleanValue ();
            } // if active value set
        } // if tracer active flag set

        // set trace server name class for database
        if (attributes.getNamedItem (ConfigurationConstants.TOK_NAME) != null)
        {
            this.serverName = attributes.getNamedItem (
                ConfigurationConstants.TOK_NAME).getNodeValue ();
        } // if server name set
        else
        {
            this.serverName = TracerConstants.TRACESERVER_DEFAULT;
        } // else server name not set

        // set trace server port for database
        if (attributes.getNamedItem (ConfigurationConstants.TOK_PORT) != null)
        {
            nodeValue = attributes.getNamedItem (ConfigurationConstants.TOK_PORT).getNodeValue ();
            if (nodeValue != null && nodeValue.length () > 0)
            {
                try
                {
                    this.serverPort = Integer.parseInt (nodeValue);
                } // try
                catch (NumberFormatException e)
                {
                    this.serverPort = TracerConstants.TRACESERVERPORT_DEFAULT;
                } // catch
            } // if port given
        } // if server port set
        else
        {
            this.serverPort = TracerConstants.TRACESERVERPORT_DEFAULT;
        } // else server port not set

        // set trace server path for database
        if (attributes.getNamedItem (ConfigurationConstants.TOK_PATH) != null)
        {
            nodeValue = attributes.getNamedItem (ConfigurationConstants.TOK_PATH).getNodeValue ();
            if (nodeValue != null && nodeValue.length () > 0)
            {
                this.path = nodeValue;
            } // if port given
        } // if server path set
        else
        {
            this.path = TracerConstants.TRACERPATH_DEFAULT;
        } // else server path not set

        // set trace server password for database
        if (attributes.getNamedItem (ConfigurationConstants.TOK_PASSWORD) != null)
        {
            nodeValue = attributes.getNamedItem (ConfigurationConstants.TOK_PASSWORD).getNodeValue ();
            if (nodeValue != null)
            {
                this.password = nodeValue.getBytes ();
            } // if port given
        } // if server password set
        else
        {
            this.password = TracerConstants.TRACERPASSWORD_DEFAULT;
        } // else server password not set
    } // setTracerConfiguration


    /**************************************************************************
     * Get the value of a specific field. <BR/>
     *
     * @param   f       The field.
     *
     * @return  The value of the field as object.
     *
     * @throws  IllegalAccessException
     *          There is no access to the field.
     */
    protected Object getFieldValue (Field f) throws IllegalAccessException
    {
        return f.get (this);
    } // getFieldValue


    /**************************************************************************
     * Get all fields which are declared within this container. <BR/>
     *
     * @return  The fields.
     */
    protected Field[] getDeclaredFields ()
    {
        Field[] fields = this.getClass ().getDeclaredFields ();
        Vector<Field> v = new Vector<Field> ();

        for (int i = 0; i < Array.getLength (fields); i++)
        {
            if (!fields[i].getName ().equalsIgnoreCase (ConfigurationConstants.TOK_PASSWORD))
            {
                v.add (fields[i]);
            } // if field to display
        } // for

        Field[] ret = new Field [v.size ()];

        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = v.elementAt (i);
        } // for

        return ret;
    } // getDeclaredFields


    /**************************************************************************
     * Return the string representation of the Configuration. <BR/>
     * This method just concatenates the most important configuration properties
     * and creates a String out of them.
     *
     * @return  String representation of Configuration.
     */
    public String toString ()
    {
        String s = super.toString ();
        return "\nTracer:\n" + s;
    } // toString

} // TracerConf

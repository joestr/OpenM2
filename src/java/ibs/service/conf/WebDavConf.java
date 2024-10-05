/**
 * Class: WebDavConf
 */

// package:
package ibs.service.conf;

// imports:
import ibs.service.conf.AConfigurationContainer;
import ibs.service.conf.ConfigurationConstants;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Vector;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/******************************************************************************
 * WebDav Configuration
 *
 * @version     $Id: WebDavConf.java,v 1.6 2009/07/24 21:21:56 kreimueller Exp ${cursor}
 *
 * @author      Mark Wassermann (MW)
 ******************************************************************************
 */
public  class WebDavConf extends AConfigurationContainer
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WebDavConf.java,v 1.6 2009/07/24 21:21:56 kreimueller Exp $";


    /**
     * URL for WebDav requests. <BR/>
     */
    protected String webdavurl = "";

    /**
     * Path of WebDav enabled os folder. <BR/>
     */
    protected String webdavpath = "";


    /**************************************************************************
     * Only the first node will be set for the WebDav settings.
     * The other nodes are ignored. <BR/>
     *
     * @param dbs The nodelist which contains the tracer settings. Only the
     *            first node will be taken.
     */
    protected void setWebDavConfiguration (NodeList dbs)
    {
        Node db = dbs.item (0);
        NamedNodeMap attributes = db.getAttributes ();

        // set the webdav url
        if (attributes.getNamedItem (ConfigurationConstants.TOK_WEBDAVURL) != null)
        {
            this.webdavurl = attributes.getNamedItem (
                ConfigurationConstants.TOK_WEBDAVURL).getNodeValue ();

            // IBS-127 webdavurl is no a relative path
            // remove leading path separators
            // Better code (has to be tested):
            // this.webdavurl = this.webdavurl.replaceFirst ("\/+", "");
            while (this.webdavurl.indexOf ("/") == 0)
            {
                this.webdavurl = this.webdavurl.substring (1, this.webdavurl.length ());
            } // while
        } // if
        else
        {
            this.webdavurl = "";
        } // else

        // set webdav path
        if (attributes.getNamedItem (ConfigurationConstants.TOK_WEBDAVPATH) != null)
        {
            this.webdavpath = attributes.getNamedItem (
                ConfigurationConstants.TOK_WEBDAVPATH).getNodeValue ();
        } // if
        else
        {
            this.webdavpath = "";
        } // else
    } // setWebDavConfiguration


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
        return "\nWebDav:\n" + s;
    } // toString

} // WebDavConf

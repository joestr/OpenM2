/*
 * Class: FunctionHandlerContainerLoader.java
 */

// package:
package ibs.app.func;

// imports:
import ibs.service.list.XMLElementContainerLoader;
import ibs.service.module.ModuleConstants;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.xml.XMLHelpers;
import ibs.util.list.IElement;
import ibs.util.list.ListException;

import java.io.File;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/******************************************************************************
 * This class is responsible for loading the data for the function handler
 * container out of the data store. <BR/>
 * The data is loaded into the function handler container which can be
 * retrieved through {@link ibs.bo.cache.ObjectPool#getFunctionHandlerContainer ()
 * ibs.bo.cache.ObjectPool.getFunctionHandlerContainer ()}.
 *
 * @version     $Id: FunctionHandlerContainerLoader.java,v 1.13 2007/07/31 19:13:52 kreimueller Exp $
 *
 * @author      Klaus, 15.11.2003
 ******************************************************************************
 */
public class FunctionHandlerContainerLoader
    extends XMLElementContainerLoader<FunctionHandlerContainer, GeneralFunctionHandler>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FunctionHandlerContainerLoader.java,v 1.13 2007/07/31 19:13:52 kreimueller Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a FunctionHandlerContainerLoader object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   container   The container in which to load the information.
     * @param   rootDir     The root directory where to start the search.
     */
    public FunctionHandlerContainerLoader (FunctionHandlerContainer container,
                                           String rootDir)
    {
        // call constructor of super class:
        super (container, rootDir);

        // initialize the instance's properties:
        this.setFileNameFilter (ModuleConstants.FILE_MODULE);
        this.setTagName (ModuleConstants.TAG_FUNCHANDLER);
    } // FunctionHandlerContainerLoader



    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get the element type specific data out of the actual element data. <BR/>
     * This method is used to get all values of one element out of the
     * result set. <BR/>
     *
     * @param   elemData    The data for the element.
     * @param   dataFile    The file which contains the data.
     *
     * @return  The newly created element filled with the values out of the
     *          actual element data.
     *
     * @throws  ListException
     *          An error occurred during parsing the element.
     */
    protected GeneralFunctionHandler parseElement (Node elemData, File dataFile)
        throws ListException
    {
        GeneralFunctionHandler elem = null;
        NamedNodeMap attributes = elemData.getAttributes ();

        // set the properties of the new element:
        elem = this.getElems ().getElementInstance
            (XMLHelpers.getAttributeValue (attributes, "classname"), 0, null);
        elem.setProperties (elemData, dataFile);

        // return the element:
        return elem;
    } // parseElement


    /**************************************************************************
     * Create the query to retrieve the data from the database. <BR/>
     *
     * @return  The constructed query.
     */
    protected final String createQuery ()
    {
        StringBuffer query = new StringBuffer (); // the query to be executed

        // get all versions of all types:
        query.append (" SELECT id, name, className, minFunc, maxFunc, replace")
            .append (" FROM ibs_FunctionHandler")
            .append (" ORDER BY className ASC");

        // return the computed query:
        return query.toString ();
    } // createQuery


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This method is used to get all attributes of one element out of the
     * result set. The attribute names which can be used are the ones which
     * are defined within the resultset of {@link #createQuery createQuery}.
     * <BR/>
     * <B>Format:</B>. <BR/>
     * for oid properties:
     *      &lt;variable&gt; = getQuOidValue (action, "&lt;attribute&gt;");. <BR/>
     * for other properties:
     *      &lt;variable&gt; = action.get&lt;type&gt; ("&lt;attribute&gt;");. <BR/>
     *
     * @param   action  The database object used for getting the tuple values.
     *
     * @return  The newly created element filled with the values out of the
     *          actual tuple.
     *
     * @exception   ibs.tech.sql.DBError
     *              Error when executing database statement.
     */
    protected final IElement parseElement (SQLAction action)
        throws DBError
    {
        // create the function handler instance and return the result:
        return null;
/*
        return ((IElement) createInstance (
            action.getString ("id"),
            action.getString ("name"),
            action.getString ("className"),
            action.getInt ("minFunc"),
            action.getInt ("maxFunc"),
            action.getString ("replace")
        ));
*/
    } // parseElement

} // class FunctionHandlerContainerLoader

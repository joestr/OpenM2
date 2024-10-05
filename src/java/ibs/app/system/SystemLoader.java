/*
 * Class: SystemLoader.java
 */
/*
 * Created by IntelliJ IDEA.
 * User: klaus
 * Date: Dec 10, 2001
 * Time: 10:48:55 PM
 */

// package:
package ibs.app.system;

// imports:
import ibs.service.list.SQLElementContainerLoader;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;


/******************************************************************************
 * This class is responsible for loading the data for the system container out
 * of the data store. <BR/>
 * The data is loaded into the property
 * {@link ibs.io.session.ApplicationInfo#p_system ApplicationInfo.p_system}.
 *
 * @version     $Id: SystemLoader.java,v 1.11 2007/07/31 19:13:52 kreimueller Exp $
 *
 * @author      kreimueller, 011210
 ******************************************************************************
 */
public class SystemLoader extends SQLElementContainerLoader<System, SystemValue>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SystemLoader.java,v 1.11 2007/07/31 19:13:52 kreimueller Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates an SystemLoader object. <BR/>
     * Calls the constructor of the super class.
     *
     * @param   container   The container in which to load the information.
     */
    public SystemLoader (System container)
    {
        // call constructor of super class:
        super (container);
    } // SystemLoader


    ///////////////////////////////////////////////////////////////////////////
    // methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Create the query to retrieve the data from the database. <BR/>
     *
     * @return  The constructed query.
     */
    protected final String createQuery ()
    {
        // get all entries of the table:
        return " SELECT id, name, type, value" +
               " FROM ibs_System";
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
    protected final SystemValue parseElement (SQLAction action)
        throws DBError
    {
        // get the data out of the tuple:
        int id = action.getInt ("id");
        String name = action.getString ("name");
        String type = action.getString ("type");
        String value = action.getString ("value");

        // create a new SystemValue object with the tuple data and return it:
        return new SystemValue (id, name, type, value);
    } // parseElement

} // class SystemLoader

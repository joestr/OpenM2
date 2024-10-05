/*
 * Class: ObjectPathHandler.java
 */

// package:
package ibs.bo.path;

// import:
import ibs.BaseObject;
import ibs.bo.OID;
import ibs.tech.sql.DBError;


/******************************************************************************
 * This class is an interface to handle data of the business object tree. <BR/>
 *
 * @version     $Id: ObjectPathHandler.java,v 1.9 2007/07/27 12:01:42 kreimueller Exp $
 *
 * @author      Andreas Jansa (AJ) 020117
 ******************************************************************************
 */
public class ObjectPathHandler extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ObjectPathHandler.java,v 1.9 2007/07/27 12:01:42 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class ObjectPathHandler.
     * <BR/>
     */
    public ObjectPathHandler ()
    {
        // nothing to do
    } // ObjectTreeHandler


    /**************************************************************************
     * Retrieves the parent tree of one BusinessObject. <BR/>
     *
     * @param   oid     Oid of object to build the parent tree.
     *
     * @return  The leaf node or <CODE>null</CODE> if no node was found.
     *
     * @throws  DBError
     *          An exception occurred during database access.
     */
    public ObjectPathNode retrieveParentTree (OID oid) throws DBError
    {
        ObjectPathNode parent = null;
        ObjectPathNode node = null;
        ObjectPathNode leaf = null;     // the leaf node

        // check if the node exists:
        if (oid != null && !oid.isEmpty ()) // node exists?
        {
            // create the leaf object and get its data:
            leaf = new ObjectPathNode (oid);
            leaf = leaf.performRetrieveNode ();

            if (leaf != null)           // the data were found?
            {
                leaf.setNodeType (ObjectPathNode.TYPE_LEAF);
                node = leaf;

                // get parent tree
                while ((parent = node.performRetrieveParent ()) != null)
                {
                    node = parent;
                    node.setNodeType (ObjectPathNode.TYPE_NODE);
                } // while

                if (node != leaf)       // at least two nodes found?
                {
                    // set the last node as root
                    node.setNodeType (ObjectPathNode.TYPE_ROOT);
                } // if at least two nodes found
            } // if the data were found
        } // if node exists

        // return the computed node and its parents:
        return leaf;
    } // retrieveParentTree

} // class ObjectPathHandler
